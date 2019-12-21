package ve.environment;

import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import ve.Camera;
import ve.Core;
import ve.Sound;
import ve.VE;
import ve.effects.GroundBurst;
import ve.trackElements.TE;
import ve.trackElements.trackParts.TrackPart;
import ve.trackElements.trackParts.TrackPlane;
import ve.utilities.SL;
import ve.utilities.U;
import ve.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.Collection;
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

 public enum Ground {
  ;
  private static double X, Z;
  public static double level;
  public static Color RGB = U.getColor(0);
  public static final Cylinder C = new Cylinder();

  static {
   C.setRadius(10000000);
   C.setHeight(0);
   U.setMaterialSecurely(C, new PhongMaterial());
   U.Phong.setSpecularRGB((PhongMaterial) C.getMaterial(), Specular.Colors.standard);
   ((PhongMaterial) C.getMaterial()).setSpecularPower(Specular.Powers.dull);
  }

  public static void load(String s) {
   if (s.startsWith("ground(")) {
    RGB = U.getColor(U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
    U.Phong.setDiffuseRGB((PhongMaterial) C.getMaterial(), RGB);
    Terrain.RGB = U.getColor(U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
    if (!VE.Map.name.equals(SL.Maps.phantomCavern)) {
     U.Nodes.add(C);
    }
   }
  }
 }

 public enum Terrain {
  ;
  public static String terrain = "", vehicleDefaultTerrain = "";
  public static final PhongMaterial universal = new PhongMaterial();
  static final Image[] lowResolution = new Image[2];
  public static Color RGB = U.getColor(0);

  public static void load(String s) {
   if (s.startsWith("terrain(")) {
    terrain = " " + U.getString(s, 0) + " ";
    vehicleDefaultTerrain = terrain + (U.contains(terrain, SL.Thick(SL.paved), SL.Thick(SL.rock), SL.Thick(SL.grid), SL.Thick(SL.metal), SL.Thick(SL.brightmetal)) ? SL.Thick(SL.hard) : SL.Thick(SL.ground));
    ((PhongMaterial) Ground.C.getMaterial()).setSpecularMap(U.Images.get(terrain.trim()));
    if (!U.getString(s, 0).isEmpty() && (RGB.getRed() > 0 || RGB.getGreen() > 0 || RGB.getBlue() > 0)) {
     for (long n = terrain.contains(SL.Thick(SL.rock)) ? Long.MAX_VALUE : 4000; --n >= 0; ) {
      if (RGB.getRed() < 1 && RGB.getGreen() < 1 && RGB.getBlue() < 1) {
       RGB = U.getColor(RGB.getRed() * 1.0001, RGB.getGreen() * 1.0001, RGB.getBlue() * 1.0001);
      } else {
       break;
      }
     }
    }
    lowResolution[0] = U.Images.getLowResolution(U.Images.get(terrain.trim()));
    lowResolution[1] = U.Images.getLowResolution(U.Images.getNormalMap(terrain.trim()));
    universal.setSpecularPower(/*sloppy but works->*/vehicleDefaultTerrain.contains(SL.Thick(SL.hard)) ? Specular.Powers.standard : Specular.Powers.dull);
    universal.setDiffuseMap(U.Images.get(terrain.trim()));
    universal.setSpecularMap(U.Images.get(terrain.trim()));
    universal.setBumpMap(U.Images.getNormalMap(terrain.trim()));
    U.Phong.setDiffuseRGB(universal, RGB);
    U.Phong.setSpecularRGB(universal, RGB);//<-'RGB' is tradition, but is 'Specular.Colors.standard' a better choice?
    GroundPlate.load(terrain.trim());
   }
  }

  static void reset() {
   universal.setDiffuseMap(null);
   universal.setSpecularMap(null);
   universal.setBumpMap(null);
   U.Phong.setDiffuseRGB(universal, 0);
   U.Phong.setSpecularRGB(universal, 0);
  }
 }

 public enum Sun {
  ;
  static double X, Y, Z;
  public static final Sphere S = new Sphere(200000000);
  public static final PointLight light = new PointLight();
  private static double lightX, lightY, lightZ;
  private static double RGBVariance = .5;

  static {
   U.Nodes.Light.setRGB(light, 1, 1, 1);
   light.setTranslateX(0);
   light.setTranslateZ(0);
   light.setTranslateY(-Long.MAX_VALUE);
  }

  public static void load(String s) {
   if (s.startsWith("sun(")) {
    lightX = U.getValue(s, 0);
    lightY = U.getValue(s, 2);
    lightZ = U.getValue(s, 1);
    U.Nodes.Light.add(light);
    X = U.getValue(s, 0) * 2;
    Y = U.getValue(s, 2) * 2;
    Z = U.getValue(s, 1) * 2;
    PhongMaterial PM = new PhongMaterial();
    U.Phong.setDiffuseRGB(PM, 1);
    U.Phong.setSpecularRGB(PM, Specular.Colors.shiny);
    PM.setSpecularPower(0);
    U.Phong.setSelfIllumination(PM, skyRGB.getRed(), skyRGB.getGreen(), skyRGB.getBlue());
    U.setMaterialSecurely(S, PM);
    U.Nodes.add(S);
   }
  }
 }

 public enum Fog {
  ;
  public static final List<Sphere> spheres = new ArrayList<>();

  public static void load(String s) {
   if (s.startsWith("fog(") && spheres.isEmpty()) {
    long quantity = Math.round(U.getValue(s, 0));
    double radius = (viewableMapDistance / quantity) * .5;
    for (int n = 0; n < quantity; n++) {
     spheres.add(new Sphere(radius));
     radius += viewableMapDistance / quantity;
    }
    PhongMaterial PM = new PhongMaterial();
    U.Phong.setDiffuseRGB(PM, skyRGB, U.getValue(s, 1));
    U.Phong.setSpecularRGB(PM, 0);
    PM.setSpecularPower(Double.POSITIVE_INFINITY);
    for (Sphere fog : spheres) {
     fog.setMaterial(PM);
     fog.setCullFace(CullFace.FRONT);
    }
   }
  }
 }

 public enum mapBounds {
  ;
  public static double left, right, forward, backward, Y;
  public static boolean slowVehicles;

  public static void slowVehicle(Vehicle V) {
   if (slowVehicles) {
    if (V.Z > forward || V.Z < backward || V.X > right || V.X < left) {
     V.P.speedX *= .5;
     V.P.speedZ *= .5;
     V.P.speed *= .95;
    }
    if (Math.abs(V.Y) > Math.abs(Y)) {
     V.P.speedY *= .5;
     V.P.speed *= .95;
    }
   }
  }
 }

 public enum Storm {
  ;
  private static final Sphere stormCloud = new Sphere(200000);
  public static double stormCloudY;
  public static Sound thunder;

  static {
   stormCloud.setScaleY(.1);
   stormCloud.setCullFace(CullFace.NONE);
  }

  public static void load(String s) {
   if (s.startsWith("storm(")) {
    PhongMaterial PM = new PhongMaterial();
    U.Phong.setDiffuseRGB(PM, U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
    U.setMaterialSecurely(stormCloud, PM);
    stormCloudY = U.getValue(s, 3);
    U.Nodes.add(stormCloud);
    Rain.load(s);
    if (s.contains("lightning")) {
     Lightning.exists = true;
     Lightning.runMesh();//<-Must run first or mesh wil not load
     U.Nodes.add(Lightning.MV);
     for (int n = 75; --n >= 0; ) {
      Lightning.groundBursts.add(new GroundBurst());
     }
     thunder = new Sound("thunder", Double.POSITIVE_INFINITY);
    }
   }
  }

  static void run(Collection<Node> theChildren, boolean update) {
   if (theChildren.contains(stormCloud)) {
    stormCloud.setTranslateY(stormCloudY - Camera.Y);
    Rain.run(update);
    Lightning.run(update);
   }
  }

  public enum Lightning {
   ;
   public static boolean exists;
   public static double X, Z;
   public static long strikeStage;
   private static final MeshView MV = new MeshView();
   private static final PointLight[] light = new PointLight[2];
   static final List<GroundBurst> groundBursts = new ArrayList<>();
   static int currentBurst;

   static {
    TriangleMesh lightningTM = new TriangleMesh();
    lightningTM.getTexCoords().setAll(0, 0);
    lightningTM.getFaces().setAll(
    0, 0, 1, 0, 2, 0,
    1, 0, 2, 0, 3, 0,
    2, 0, 3, 0, 4, 0,
    3, 0, 4, 0, 5, 0);
    MV.setMesh(lightningTM);
    MV.setCullFace(CullFace.NONE);
    PhongMaterial lightningPM = new PhongMaterial();
    lightningPM.setSelfIlluminationMap(U.Images.get(SL.white));
    U.setMaterialSecurely(MV, lightningPM);
    for (int n = light.length; --n >= 0; ) {
     light[n] = new PointLight();
     U.Nodes.Light.setRGB(light[n], 1, 1, 1);
    }
   }

   private static void runMesh() {
    double randomX = U.randomPlusMinus(3000.), randomZ = U.randomPlusMinus(3000.);
    ((TriangleMesh) MV.getMesh()).getPoints().setAll(
    (float) U.randomPlusMinus(1000.), (float) stormCloudY, (float) U.randomPlusMinus(1000.),
    (float) U.randomPlusMinus(1000.), (float) stormCloudY, (float) U.randomPlusMinus(1000.),
    (float) (U.randomPlusMinus(1000.) + randomX), (float) (stormCloudY * .5), (float) (U.randomPlusMinus(1000.) + randomZ),
    (float) (U.randomPlusMinus(1000.) + randomX), (float) (stormCloudY * .5), (float) (U.randomPlusMinus(1000.) + randomZ),
    (float) U.randomPlusMinus(1000.), 0, (float) U.randomPlusMinus(1000.),
    (float) U.randomPlusMinus(1000.), 0, (float) U.randomPlusMinus(1000.));
   }

   static void run(boolean update) {
    if (exists) {
     U.Nodes.Light.remove(light[0]);
     if (strikeStage < 8) {
      runMesh();
      U.setTranslate(MV, X, 0, Z);
      if (strikeStage < 4) {
       U.setTranslate(light[0], X, 0, Z);
       U.Nodes.Light.add(light[0]);
       if (strikeStage < 1) {//<-May call more than once if strikeStage progresses using 'tick'!
        MV.setVisible(true);
        for (int n = 25; --n >= 0; ) {
         groundBursts.get(currentBurst).deploy(X, Z);
         currentBurst = ++currentBurst >= groundBursts.size() ? 0 : currentBurst;
        }
        if (update) {
         thunder.play(Double.NaN, Math.sqrt(U.distance(Camera.X, X, Camera.Y, 0, Camera.Z, Z)) * Sound.standardDistance(.5));
        }
       }
      }
      U.setTranslate(light[1], X, 0, Z);
      U.Nodes.Light.add(light[1]);
      U.fillRGB(graphicsContext, 1, 1, 1, U.random(.5));
      U.fillRectangle(graphicsContext, .5, .5, 1, 1);
     } else {
      MV.setVisible(false);
      U.Nodes.Light.remove(light[1]);
     }
     if (VE.status != VE.Status.replay && ++strikeStage > U.random(13000.)) {//<-Progress strike stage using tick?
      X = Camera.X + U.randomPlusMinus(200000.);
      Z = Camera.Z + U.randomPlusMinus(200000.);
      strikeStage = 0;
     }
     for (GroundBurst burst : groundBursts) {
      burst.run();
     }
    }
   }
  }
 }

 public enum Wind {
  ;
  public static double maxPotency, speedX, speedZ;
  public static boolean stormExists;
  public static Sound storm;
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
  List<Node> theChildren = VE.group.getChildren();
  boolean mapViewer = VE.status == VE.Status.mapViewer, updateIfMatchBegan = mapViewer || (gamePlay && VE.Match.started);
  double sunlightAngle = Sun.X != 0 || Sun.Z != 0 ? (((Sun.X / (Sun.Y * 50)) * U.sin(Camera.XZ)) + ((Sun.Z / (Sun.Y * 50)) * U.cos(Camera.XZ))) * U.cos(Camera.YZ) : 0;
  if (VE.Map.name.equals(SL.Maps.theSun)) {
   Sun.RGBVariance *= U.random() < .5 ? 81 / 80. : 80 / 81.;
   Sun.RGBVariance = U.clamp(.2, Sun.RGBVariance, 1);
   VE.scene3D.setFill(Color.color(Sun.RGBVariance, Sun.RGBVariance * .5, 0));
  } else {
   VE.scene3D.setFill(Color.color(U.clamp(skyRGB.getRed() - sunlightAngle), U.clamp(skyRGB.getGreen() - sunlightAngle), U.clamp(skyRGB.getBlue() - sunlightAngle)));
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
   Terrain.universal.setDiffuseMap(U.Images.get(Terrain.terrain.trim()));
   Terrain.universal.setSpecularMap(U.Images.get(Terrain.terrain.trim()));
   Terrain.universal.setBumpMap(U.Images.getNormalMap(Terrain.terrain.trim()));
   TE.Paved.universal.setDiffuseMap(U.Images.get(SL.paved));
   TE.Paved.universal.setSpecularMap(U.Images.get(SL.paved));
   TE.Paved.universal.setBumpMap(U.Images.getNormalMap(SL.paved));
  }
  Star.run();
  Cloud.run();
  GroundPlate.run();
  if (Wind.maxPotency > 0) {
   Wind.speedX += U.randomPlusMinus(Wind.maxPotency * .1 * VE.tick);
   Wind.speedZ += U.randomPlusMinus(Wind.maxPotency * .1 * VE.tick);
   Wind.speedX -= Wind.speedX * .0004 * VE.tick;
   Wind.speedZ -= Wind.speedZ * .0004 * VE.tick;
   Wind.speedX = U.clamp(-Wind.maxPotency, Wind.speedX, Wind.maxPotency);
   Wind.speedZ = U.clamp(-Wind.maxPotency, Wind.speedZ, Wind.maxPotency);
  }
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
  if (Wind.stormExists) {
   if (U.FPS < 15) {
    Wind.speedX *= .875;
    Wind.speedZ *= .875;
   }
   double stormPower = Math.sqrt(StrictMath.pow(Wind.speedX, 2) * StrictMath.pow(Wind.speedZ, 2));//<-Multiplied--not added!
   U.fillRGB(graphicsContext, Ground.RGB.getRed(), Ground.RGB.getGreen(), Ground.RGB.getBlue(), U.minimumAccurateLayeredOpacity);
   double dustWidth = VE.width * .25, dustHeight = VE.height * .25;
   for (n = (int) (stormPower * .025); --n >= 0; ) {
    graphicsContext.fillOval(-dustWidth + U.random(VE.width + dustWidth), -dustHeight + U.random(VE.height + dustHeight), dustWidth, dustHeight);
   }
   if (!VE.Match.muteSound && (gamePlay || mapViewer)) {
    Wind.storm.loop(40 - (Math.min(40, 3 * StrictMath.pow(stormPower, .25))));
   } else {
    Wind.storm.stop();
   }
  }
  pool.runVision();
  //Draw order is windstorm, poolVision, screenFlashes
  for (Vehicle vehicle : VE.vehicles) {
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

 public static void reset() {
  U.Nodes.remove(E.Sun.S, E.Ground.C);
  U.Nodes.Light.remove(E.Sun.light);
  Fog.spheres.clear();
  GroundPlate.instances.clear();
  Cloud.instances.clear();
  Star.instances.clear();
  Rain.raindrops.clear();
  E.Storm.Lightning.groundBursts.clear();
  Snowball.instances.clear();
  Tornado.parts.clear();
  Tsunami.parts.clear();
  Fire.instances.clear();
  Boulder.instances.clear();
  Volcano.rocks.clear();
  Meteor.instances.clear();
  Terrain.terrain = SL.Thick(SL.ground);
  skyRGB = Ground.RGB = U.getColor(0);
  E.Sun.X = E.Sun.Y = E.Sun.Z
  = E.Wind.maxPotency = E.Wind.speedX = E.Wind.speedZ
  = E.Ground.level = Pool.depth = 0;
  Terrain.RGB = U.getColor(0);
  E.Storm.Lightning.exists = Volcano.exists = Tsunami.exists = E.Wind.stormExists = E.mapBounds.slowVehicles = Pool.exists = Tornado.movesRepairPoints = false;
  E.mapBounds.left = E.mapBounds.backward = E.mapBounds.Y = Double.NEGATIVE_INFINITY;
  E.mapBounds.right = E.mapBounds.forward = viewableMapDistance = Double.POSITIVE_INFINITY;
  gravity = 7;
  soundMultiple = 1;
  Terrain.reset();
  Pool.type = Pool.Type.water;
  U.Nodes.Light.setRGB(E.Sun.light, 1, 1, 1);
  U.Nodes.Light.setRGB(ambientLight, 0, 0, 0);
  U.Phong.setDiffuseRGB((PhongMaterial) E.Ground.C.getMaterial(), 0);
  ((PhongMaterial) E.Ground.C.getMaterial()).setSpecularMap(null);
  centerShiftOffAt = VE.Map.name.equals(SL.Maps.speedway2000000) ? 2000 : VE.Map.name.equals(SL.Maps.volcanicProphecy) ? 1000 : Double.NEGATIVE_INFINITY;
 }
}
