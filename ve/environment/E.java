package ve.environment;

import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import ve.instances.Core;
import ve.instances.I;
import ve.trackElements.TE;
import ve.trackElements.trackParts.TrackPart;
import ve.trackElements.trackParts.TrackPlane;
import ve.ui.Map;
import ve.ui.Match;
import ve.ui.UI;
import ve.utilities.*;
import ve.vehicles.Vehicle;

import java.util.List;
import java.util.Random;

public enum E {//<-Static content for V.E.'s Environment
 ;

 public static Canvas canvas;
 public static GraphicsContext graphicsContext;
 public static final Group lights = new Group();
 public static long lightsAdded;
 public static final AmbientLight ambientLight = new AmbientLight();
 public static final PointLight mapViewerLight = new PointLight();
 public static final PhongMaterial phantomPM = new PhongMaterial(), repairSpherePM = new PhongMaterial();
 public static double renderLevel;
 public static RenderType renderType = RenderType.standard;
 public static double viewableMapDistance = Double.POSITIVE_INFINITY;
 public static double gravity;
 public static double centerShiftOffAt;
 public static Color skyRGB = U.getColor(1);//<-Keep bright for first vehicle select
 public static final double[] lavaSelfIllumination = {1, .5, 0};//<-Not explicitly a Pool property, so leave here
 public static double soundMultiple;
 public static final float[] textureCoordinateBase0 = {0, 1, 1, 1, 1, 0, 0, 0}, textureCoordinateBase1 = {0, 0, 1, 0, 1, 1, 0, 1};
 public static final Pool pool = new Pool();

 static {
  U.Nodes.Light.setRGB(ambientLight, .5, .5, .5);
  U.Phong.setDiffuseRGB(phantomPM, 1, 1, 1, .1);
  U.Phong.setSpecularRGB(phantomPM, 0);
  U.Phong.setDiffuseRGB(repairSpherePM, 1, 1, 1, .25);
  U.Nodes.Light.setRGB(mapViewerLight, 1, 1, 1);
 }

 public enum RenderType {standard, fullDistance, ALL}

 public enum Specular {
  ;

  public enum Colors {
   ;
   public static final double standard = .5, shiny = 1;
  }

  public enum Powers {
   ;
   public static final double standard = 10;
   static final double dull = 4;
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
   U.Nodes.Light.setRGB(Sun.light, r, g, b);
  }
 }

 public static void loadFrustumMountains(String s) {
  if (s.startsWith("mountains(")) {
   double size = U.getValue(s, 0), spread = U.getValue(s, 1);
   Random random = new Random(Math.round(U.getValue(s, 2)));//<-Will SecureRandom change established mountain positions on maps?
   for (int n = 20; --n >= 0; ) {
    double[] rotatedX = {spread + spread * random.nextDouble()}, rotatedZ = {spread + spread * random.nextDouble()};
    U.rotate(rotatedX, rotatedZ, random.nextDouble() * 360);
    TE.mounds.add(new FrustumMound(rotatedX[0], rotatedZ[0], 0, size + random.nextDouble() * size, random.nextDouble() * size, random.nextDouble() * size, false, false, true));
   }
  }
 }

 public static void run(boolean gamePlay) {
  List<Node> theChildren = UI.group.getChildren();
  boolean mapViewer = UI.status == UI.Status.mapViewer, updateIfMatchBegan = mapViewer || (gamePlay && Match.started);
  double sunlightAngle = Sun.X != 0 || Sun.Z != 0 ? (((Sun.X / (Sun.Y * 50)) * U.sin(Camera.XZ)) + ((Sun.Z / (Sun.Y * 50)) * U.cos(Camera.XZ))) * U.cos(Camera.YZ) : 0;
  if (Map.name.equals(SL.Maps.theSun)) {
   Sun.RGBVariance *= U.random() < .5 ? 81 / 80. : 80 / 81.;
   Sun.RGBVariance = U.clamp(.2, Sun.RGBVariance, 1);
   UI.scene3D.setFill(Color.color(Sun.RGBVariance, Sun.RGBVariance * .5, 0));
  } else {
   UI.scene3D.setFill(Color.color(U.clamp(skyRGB.getRed() - sunlightAngle), U.clamp(skyRGB.getGreen() - sunlightAngle), U.clamp(skyRGB.getBlue() - sunlightAngle)));
  }
  if (lights.getChildren().contains(Sun.light)) {
   U.setTranslate(Sun.light, Sun.lightX, Sun.lightY, Sun.lightZ);
   if (theChildren.contains(Sun.S)) {
    if (U.render(Sun.X, Sun.Y, Sun.Z, -Sun.S.getRadius())) {
     U.setTranslate(Sun.S, Sun.X, Sun.Y, Sun.Z);
     Sun.S.setVisible(true);
    } else {
     Sun.S.setVisible(false);
    }
   }
  }
  if (theChildren.contains(Ground.C) && Ground.level <= 0) {
   double groundY = Pool.exists && U.distanceXZ(pool) < Pool.C[0].getRadius() && Camera.Y > 0 ? Pool.depth : Math.max(0, -Camera.Y * .01);
   while (Math.abs(Ground.X - Camera.X) > 100000) Ground.X += Ground.X > Camera.X ? -200000 : 200000;
   while (Math.abs(Ground.Z - Camera.Z) > 100000) Ground.Z += Ground.Z > Camera.Z ? -200000 : 200000;
   if (Camera.Y < groundY) {
    U.setTranslate(Ground.C, Ground.X, groundY, Ground.Z);
    Ground.C.setVisible(true);
   } else {
    Ground.C.setVisible(false);
   }
  } else {
   Ground.C.setVisible(false);
  }
  if (U.averageFPS < 30) {//<-Don't use direct FPS!
   Terrain.universal.setDiffuseMap(Terrain.lowResolution[0]);
   Terrain.universal.setSpecularMap(Terrain.lowResolution[0]);
   Terrain.universal.setBumpMap(Terrain.lowResolution[1]);
   TE.Paved.universal.setDiffuseMap(TE.Paved.lowResolution[0]);
   TE.Paved.universal.setSpecularMap(TE.Paved.lowResolution[0]);
   TE.Paved.universal.setBumpMap(TE.Paved.lowResolution[1]);
  } else if (U.maxedFPS(true)) {//Don't create any 'new' images while setting the universals--or RAM will be killed!
   if (!Terrain.terrain.equals(SL.Thick(SL.ground))) {//<-'ground' string will crash if checked in getter, thus skipped
    Terrain.universal.setDiffuseMap(Images.get(Terrain.terrain.trim()));
    Terrain.universal.setSpecularMap(Images.get(Terrain.terrain.trim()));
    Terrain.universal.setBumpMap(Images.getNormalMap(Terrain.terrain.trim()));
   }
   TE.Paved.universal.setDiffuseMap(Images.get(SL.paved));
   TE.Paved.universal.setSpecularMap(Images.get(SL.paved));
   TE.Paved.universal.setBumpMap(Images.getNormalMap(SL.paved));
  }
  Wind.runPowerSet();
  Fog.run();
  Star.run();
  Cloud.run();
  GroundPlate.run();
  Crystal.run();
  Storm.run(theChildren, gamePlay || mapViewer);
  if (Pool.exists) {
   if (Camera.Y < 0) {
    U.setTranslate(Pool.C[0], pool.X, 0, pool.Z);
    Pool.C[0].setVisible(true);
    Pool.C[1].setVisible(false);
   } else {
    U.setTranslate(Pool.C[1], pool.X, Pool.depth * .5, pool.Z);
    Pool.C[1].setVisible(true);
    Pool.C[0].setVisible(false);
   }
   if (Pool.type == Pool.Type.lava) {
    U.Phong.setDiffuseRGB(Pool.PM, 1, .25 + U.random(.5), 0);
   }
  }
  int n;
  Tornado.run(gamePlay || mapViewer);
  Snowball.run();
  Tsunami.run(gamePlay || mapViewer, updateIfMatchBegan);
  Fire.run(gamePlay || mapViewer);
  Boulder.run(updateIfMatchBegan);
  Volcano.run(updateIfMatchBegan);
  Meteor.run(gamePlay || mapViewer);
  Wind.runStorm(gamePlay || mapViewer);
  pool.runVision();
  //Draw order is windstorm, poolVision, screenFlashes
  for (Vehicle vehicle : I.vehicles) {
   if (vehicle.screenFlash > 0) {
    U.fillRGB(graphicsContext, 1, 1, 1, vehicle.screenFlash);
    U.fillRectangle(graphicsContext, .5, .5, 1, 1);
   }
  }
 }

 public static void setTerrainSit(Core C, boolean vehicle) {
  C.Y = U.distanceXZ(C, pool) < Pool.C[0].getRadius() ? Pool.depth : 0;
  if (Volcano.exists) {
   double volcanoDistance = U.distance(C.X, Volcano.X, C.Z, Volcano.Z);
   C.Y = volcanoDistance < Volcano.radiusBottom && volcanoDistance > Volcano.radiusTop && C.Y > -Volcano.radiusBottom + volcanoDistance ? Math.min(C.Y, -Volcano.radiusBottom + volcanoDistance) : C.Y;
  }
  for (TrackPart trackPart : TE.trackParts) {
   if (!trackPart.wraps || vehicle) {
    for (TrackPlane trackPlane : trackPart.trackPlanes) {
     if (!trackPlane.type.contains(SL.gate)) {
      double trackX = trackPlane.X + trackPart.X, trackZ = trackPlane.Z + trackPart.Z;
      if (Math.abs(C.X - trackX) <= trackPlane.radiusX && Math.abs(C.Z - trackZ) <= trackPlane.radiusZ) {
       double trackY = trackPlane.Y + trackPart.Y;
       if (trackPlane.type.contains(SL.Thick(SL.tree))) {
        C.Y = trackY - trackPlane.radiusY;
       } else if (trackPlane.wall == TrackPlane.Wall.none) {
        if (trackPlane.YZ == 0 && trackPlane.XY == 0) {
         C.Y = Math.min(C.Y, trackY);
        } else {
         if (trackPlane.YZ != 0) {
          C.Y = Math.min(trackY + (C.Z - trackZ) * (trackPlane.radiusY / trackPlane.radiusZ) * (trackPlane.YZ > 0 ? 1 : trackPlane.YZ < 0 ? -1 : 0), C.Y);
         } else if (trackPlane.XY != 0) {
          C.Y = Math.min(trackY + (C.X - trackX) * (trackPlane.radiusY / trackPlane.radiusX) * (trackPlane.XY > 0 ? 1 : trackPlane.XY < 0 ? -1 : 0), C.Y);
         }
        }
       }
      }
     }
    }
   }
  }
  for (FrustumMound mound : TE.mounds) {
   setMoundSit(C, mound, vehicle);
  }
 }

 private static void setMoundSit(Core I, FrustumMound FM, boolean vehicle) {
  if (!FM.wraps || vehicle) {
   double distance = U.distance(I.X, FM.X, I.Z, FM.Z),
   radiusTop = FM.mound.getMinorRadius(), moundHeight = FM.mound.getHeight();
   if (distance < radiusTop) {
    I.Y = Math.min(I.Y, FM.Y - moundHeight);
   } else {
    double radiusBottom = FM.mound.getMajorRadius();
    if (distance < radiusBottom && Math.abs(I.Y - (FM.Y - (moundHeight * .5))) <= moundHeight * .5) {
     double slope = moundHeight / Math.abs(radiusBottom - radiusTop);
     I.Y = Math.min(I.Y, FM.Y - (radiusBottom - distance) * slope);
    }
   }
  }
 }

 public static void wrap(Core C) {
  boolean setSlope = false;
  if (Math.abs(C.X) < E.centerShiftOffAt) {
   C.X += U.random() < .5 ? E.centerShiftOffAt : -E.centerShiftOffAt;
   setSlope = true;
  }
  if (Math.abs(C.X - Camera.X) > TE.wrapDistance) {
   while (C.X > Camera.X + TE.wrapDistance) C.X -= TE.wrapDistance << 1;
   while (C.X < Camera.X - TE.wrapDistance) C.X += TE.wrapDistance << 1;
   setSlope = true;
  }
  if (Math.abs(C.Z - Camera.Z) > TE.wrapDistance) {
   while (C.Z > Camera.Z + TE.wrapDistance) C.Z -= TE.wrapDistance << 1;
   while (C.Z < Camera.Z - TE.wrapDistance) C.Z += TE.wrapDistance << 1;
   setSlope = true;
  }
  if (setSlope) {
   E.setTerrainSit(C, false);
  }
 }

 public static void reset() {
  U.Nodes.remove(Sun.S, Ground.C);
  U.Nodes.Light.remove(Sun.light);
  Fog.spheres.clear();
  GroundPlate.instances.clear();
  Cloud.instances.clear();
  Star.instances.clear();
  Crystal.instances.clear();
  Rain.raindrops.clear();
  Storm.Lightning.groundBursts.clear();
  Snowball.instances.clear();
  Tornado.parts.clear();
  Tsunami.parts.clear();
  Fire.instances.clear();
  Boulder.instances.clear();
  Volcano.rocks.clear();
  Meteor.instances.clear();
  Terrain.terrain = SL.Thick(SL.ground);
  skyRGB = Ground.RGB = U.getColor(0);
  Sun.X = Sun.Y = Sun.Z
  = Wind.maxPotency = Wind.speedX = Wind.speedZ
  = Ground.level = Pool.depth = Volcano.cameraShake = 0;
  Terrain.RGB = U.getColor(0);
  Fog.exists = Storm.Lightning.exists = Volcano.exists = Tsunami.exists = Wind.stormExists = MapBounds.slowVehicles = Pool.exists = Tornado.movesRepairPoints = false;
  MapBounds.left = MapBounds.backward = MapBounds.Y = Double.NEGATIVE_INFINITY;
  MapBounds.right = MapBounds.forward = viewableMapDistance = Double.POSITIVE_INFINITY;
  gravity = 7;
  soundMultiple = 1;
  Terrain.reset();
  Pool.type = Pool.Type.water;
  U.Nodes.Light.setRGB(Sun.light, 1, 1, 1);
  U.Nodes.Light.setRGB(ambientLight, 0, 0, 0);
  U.Phong.setDiffuseRGB((PhongMaterial) Ground.C.getMaterial(), 0);
  ((PhongMaterial) Ground.C.getMaterial()).setSpecularMap(null);
  centerShiftOffAt = Map.name.equals(SL.Maps.speedway2000000) ? 2000 : Map.name.equals(SL.Maps.volcanicProphecy) ? 1000 : Double.NEGATIVE_INFINITY;
 }
}
