package ve.environment;

import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import ve.effects.Echo;
import ve.environment.storm.Storm;
import ve.instances.Core;
import ve.instances.I;
import ve.trackElements.TE;
import ve.trackElements.trackParts.TrackPlane;
import ve.ui.Maps;
import ve.ui.Match;
import ve.ui.UI;
import ve.utilities.*;
import ve.vehicles.explosions.MaxNukeBlast;

import java.util.Random;

public enum E {//<-Static game-environment content
 ;
 public static Canvas canvas;
 public static GraphicsContext GC;
 public static final Group lights = new Group();
 public static long lightsAdded;
 public static final AmbientLight ambientLight = new AmbientLight();
 public static final PointLight mapViewerLight = new PointLight();
 public static final PhongMaterial phantomPM = new PhongMaterial();
 public static double renderLevel;
 public static RenderType renderType = RenderType.standard;
 public static double viewableMapDistance = Double.POSITIVE_INFINITY;
 public static double gravity;
 private static double centerShiftOffAt;
 public static Color skyRGB = U.getColor(1);//<-Keep bright for first vehicle select
 public static final Color lavaSelfIllumination = Color.color(1, .5, 0);//<-Not explicitly a Pool property, so leave here
 public static double soundMultiple;//<-This system is technically wrong--should be based on gainHalf and not simply * or / 2, etc.

 static {
  Nodes.setLightRGB(ambientLight, .5, .5, .5);
  Phong.setDiffuseRGB(phantomPM, 1, 1, 1, .1);
  Phong.setSpecularRGB(phantomPM, 0);
  Nodes.setLightRGB(mapViewerLight, 1, 1, 1);
 }

 public enum RenderType {standard, fullDistance, ALL}//<-'ALL' is only used when loading a map, so that all meshes are force-rendered at least once. This prevents later frame spikes due to unloaded meshes being shown for the first time in their life-cycle.

 public enum Specular {
  ;

  public enum Colors {
   ;
   public static final double standard = .5, shiny = 1;
  }

  public enum Powers {
   ;
   public static final double standard = 10;
   public static final double dull = 4;
   public static final double shiny = 100;
  }
 }

 public static void loadSky(String s) {
  if (s.startsWith("sky(")) {
   skyRGB = U.getColor(U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
   double r = skyRGB.getRed(), g = skyRGB.getGreen(), b = skyRGB.getBlue();
   if (r > 0 || g > 0 || b > 0) {
    while (r < 1 && g < 1 && b < 1) {
     r *= 1.001;
     g *= 1.001;
     b *= 1.001;
    }
   }
   Nodes.setLightRGB(Sun.light, r, g, b);
  }
 }

 public static void loadFrustumMountains(String s) {
  if (s.startsWith("mountains(")) {
   double size = U.getValue(s, 0), spread = U.getValue(s, 1);
   long key;
   try {
    key = Math.round(U.getValue(s, 2));
   } catch (RuntimeException E) {
    key = U.random(Long.MAX_VALUE);
   }
   Random random = new Random(key);//<-Will SecureRandom change established mountain positions on maps?
   for (int n = 20; --n >= 0; ) {
    double[] rotatedX = {spread + spread * random.nextDouble()}, rotatedZ = {spread + spread * random.nextDouble()};
    U.rotate(rotatedX, rotatedZ, random.nextDouble() * 360);
    TE.mounds.add(new FrustumMound(rotatedX[0], rotatedZ[0], 0, size + random.nextDouble() * size, random.nextDouble() * size, random.nextDouble() * size, false, false, true));
   }
  }
 }

 public static void run(boolean gamePlay) {
  boolean mapViewer = UI.status == UI.Status.mapViewer, updateIfMatchBegan = mapViewer || (gamePlay && Match.started);
  double sunlightAngle = Sun.C.X != 0 || Sun.C.Z != 0 ? (((Sun.C.X / (Sun.C.Y * 50)) * Camera.sinXZ) + ((Sun.C.Z / (Sun.C.Y * 50)) * Camera.cosXZ)) * Camera.cosYZ : 0;//<-Camera needs the angleTable set for these sin's to be correct!
  if (Maps.name.equals(D.Maps.theSun)) {
   Sun.RGBVariance *= U.random() < .5 ? 81 / 80. : 80 / 81.;
   Sun.RGBVariance = U.clamp(.2, Sun.RGBVariance, 1);
   UI.scene3D.setFill(Color.color(Sun.RGBVariance, Sun.RGBVariance * .5, 0));
  } else {
   UI.scene3D.setFill(Color.color(U.clamp(skyRGB.getRed() - sunlightAngle), U.clamp(skyRGB.getGreen() - sunlightAngle), U.clamp(skyRGB.getBlue() - sunlightAngle)));
  }
  if (lights.getChildren().contains(Sun.light)) {
   U.setTranslate(Sun.light, Sun.lightX, Sun.lightY, Sun.lightZ);
  }
  if (Sun.type == Sun.Type.sun) {
   if (U.render(Sun.C, -Sun.S.getRadius(), false, false)) {
    U.setTranslate(Sun.S, Sun.C);
    Sun.S.setVisible(true);
   } else {
    Sun.S.setVisible(false);
   }
  }
  if (Ground.exists && Ground.level <= 0) {
   double groundY = Pool.exists && U.distanceXZ(Pool.core) < Pool.surface.getRadius() && Camera.C.Y > 0 ? Pool.depth : Math.max(0, -Camera.C.Y * .01);
   while (Math.abs(Ground.X - Camera.C.X) > 100000) Ground.X += Ground.X > Camera.C.X ? -200000 : 200000;
   while (Math.abs(Ground.Z - Camera.C.Z) > 100000) Ground.Z += Ground.Z > Camera.C.Z ? -200000 : 200000;
   if (Camera.C.Y < groundY) {
    U.setTranslate(Ground.C, Ground.X, groundY, Ground.Z);
    Ground.C.setVisible(true);
   } else {
    Ground.C.setVisible(false);
   }
  } else {
   Ground.C.setVisible(false);
  }
  Texture.adapt();
  Wind.runSetPower();
  Fog.run();
  Atmosphere.run();
  Star.run();
  Cloud.run();
  GroundPlate.run();
  Crystal.run();
  Storm.run(gamePlay || mapViewer);
  if (Pool.exists) {
   if (Camera.C.Y < 0) {
    U.setTranslate(Pool.surface, Pool.core.X, 0, Pool.core.Z);
    Pool.surface.setVisible(true);
    Pool.basin.setVisible(false);
   } else {
    U.setTranslate(Pool.basin, Pool.core.X, Pool.depth * .5, Pool.core.Z);
    Pool.basin.setVisible(true);
    Pool.surface.setVisible(false);
   }
   if (Pool.type == Pool.Type.lava) {
    Phong.setDiffuseRGB(Pool.PM, 1, .25 + U.random(.5), 0);
   }
  }
  Tornado.run(gamePlay || mapViewer);
  Snow.run();
  Tsunami.run(gamePlay || mapViewer, updateIfMatchBegan);
  Fire.run(gamePlay || mapViewer);
  Boulder.run(updateIfMatchBegan);
  Volcano.run(updateIfMatchBegan);
  Meteor.run(gamePlay || mapViewer);
  //*Draw order is windstorm, poolVision, screenFlashes
  Wind.runStorm(gamePlay || mapViewer);//*
  Pool.runVision();//*
  for (var vehicle : I.vehicles) {//*
   if (vehicle.screenFlash > 0) {
    U.fillRGB(GC, 1, 1, 1, vehicle.screenFlash);
    U.fillRectangle(GC, .5, .5, 1, 1);
   }
  }
 }

 public static void setTerrainSit(Core core, boolean vehicle) {
  core.Y = U.distanceXZ(core, Pool.core) < Pool.surface.getRadius() ? Pool.depth : 0;
  if (Volcano.exists) {
   double volcanoDistance = U.distanceXZ(core, Volcano.C);
   core.Y = volcanoDistance < Volcano.radiusBottom && volcanoDistance > Volcano.radiusTop && core.Y > -Volcano.radiusBottom + volcanoDistance ? Math.min(core.Y, -Volcano.radiusBottom + volcanoDistance) : core.Y;
  }
  for (var trackPart : TE.trackParts) {
   if ((!trackPart.wraps || vehicle) && !trackPart.trackPlanes.isEmpty() && U.distanceXZ(core, trackPart) < trackPart.renderRadius + core.absoluteRadius) {//<-Not sure how much this section helps optimize in actuality
    for (var trackPlane : trackPart.trackPlanes) {
     if (!trackPlane.type.contains(D.gate)) {
      double trackX = trackPlane.X + trackPart.X, trackZ = trackPlane.Z + trackPart.Z;
      if (Math.abs(core.X - trackX) <= trackPlane.radiusX && Math.abs(core.Z - trackZ) <= trackPlane.radiusZ) {
       double trackY = trackPlane.Y + trackPart.Y;
       if (trackPlane.type.contains(D.thick(D.tree))) {
        core.Y = trackY - trackPlane.radiusY;
       } else if (trackPlane.wall == TrackPlane.Wall.none) {
        if (trackPlane.YZ == 0 && trackPlane.XY == 0) {
         core.Y = Math.min(core.Y, trackY);
        } else {
         if (trackPlane.YZ != 0) {
          core.Y = Math.min(trackY + (core.Z - trackZ) * (trackPlane.radiusY / trackPlane.radiusZ) * (trackPlane.YZ > 0 ? 1 : trackPlane.YZ < 0 ? -1 : 0), core.Y);
         } else if (trackPlane.XY != 0) {
          core.Y = Math.min(trackY + (core.X - trackX) * (trackPlane.radiusY / trackPlane.radiusX) * (trackPlane.XY > 0 ? 1 : trackPlane.XY < 0 ? -1 : 0), core.Y);
         }
        }
       }
      }
     }
    }
   }
  }
  setMoundSit(core, vehicle);
 }

 public static void setMoundSit(Core core, boolean vehicle) {
  for (var FM : TE.mounds) {
   if (!FM.wraps || vehicle) {
    double distance = U.distanceXZ(core, FM), radiusBottom = FM.majorRadius;
    if (distance < radiusBottom) {
     double radiusTop = FM.minorRadius, moundHeight = FM.height;
     if (distance < radiusTop) {
      if (core.Y <= FM.Y) {//<-Prevents lifting objects with a Y below mound's Y
       core.Y = Math.min(core.Y, FM.Y - moundHeight);
      }
     } else if (Math.abs(core.Y - (FM.Y - (moundHeight * .5))) <= moundHeight * .5) {
      double slope = moundHeight / Math.abs(radiusBottom - radiusTop);
      core.Y = Math.min(core.Y, FM.Y - (radiusBottom - distance) * slope);
     }
    }
   }
  }
 }

 public static void wrap(Core C) {
  boolean setSit = false;
  if (Math.abs(C.X) < centerShiftOffAt) {
   C.X += centerShiftOffAt * (U.random() < .5 ? 1 : -1);
   setSit = true;
  }
  if (Math.abs(C.X - Camera.C.X) > TE.wrapDistance) {
   while (C.X > Camera.C.X + TE.wrapDistance) C.X -= TE.wrapDistance << 1;
   while (C.X < Camera.C.X - TE.wrapDistance) C.X += TE.wrapDistance << 1;
   setSit = true;
  }
  if (Math.abs(C.Z - Camera.C.Z) > TE.wrapDistance) {
   while (C.Z > Camera.C.Z + TE.wrapDistance) C.Z -= TE.wrapDistance << 1;
   while (C.Z < Camera.C.Z - TE.wrapDistance) C.Z += TE.wrapDistance << 1;
   setSit = true;
  }
  if (setSit) {
   setTerrainSit(C, false);
  }
 }

 public static void reset() {
  Sun.reset();
  Ground.reset();
  GroundPlate.instances.clear();
  Cloud.instances.clear();
  Star.instances.clear();
  Crystal.instances.clear();
  Snow.instances.clear();
  Fire.instances.clear();
  Boulder.instances.clear();
  Volcano.reset();
  Meteor.instances.clear();
  skyRGB = U.getColor(0);
  Wind.reset();
  Pool.reset();
  Storm.reset();
  Atmosphere.reset();
  Fog.reset();
  Tornado.reset();
  Tsunami.reset();
  MapBounds.reset();
  viewableMapDistance = Double.POSITIVE_INFINITY;
  gravity = 7;
  soundMultiple = 1;
  Echo.presence = 0;
  MaxNukeBlast.whoIsLit = -1;
  Terrain.reset();
  Nodes.setLightRGB(ambientLight, 0);
  centerShiftOffAt =
  Maps.name.equals(D.Maps.speedway2000000) ? 2000 :
  Maps.name.equals(D.Maps.volcanicProphecy) ? 1000 :
  Double.NEGATIVE_INFINITY;
 }
}
