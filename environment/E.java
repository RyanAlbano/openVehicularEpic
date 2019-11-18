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
 public static final PhongMaterial phantomPM = new PhongMaterial();
 public static final PhongMaterial repairSpherePM = new PhongMaterial();
 public static double renderLevel;
 public static RenderType renderType = RenderType.standard;
 public static double viewableMapDistance = Double.POSITIVE_INFINITY;
 public static double gravity;
 public static Color skyRGB = U.getColor(1);//<-Keep bright for first vehicle select
 public static final double[] lavaSelfIllumination = {1, .5, 0};//<-Not explicitly a Pool property, so leave here
 public static double soundMultiple;
 public static final float[] textureCoordinateBase0 = {0, 1, 1, 1, 1, 0, 0, 0}, textureCoordinateBase1 = {0, 0, 1, 0, 1, 1, 0, 1};
 public static final int dustQuantity = 96, shotQuantity = 96, explosionQuantity = 12, thrustTrailQuantity = 48, smokeQuantity = 50, splashQuantity = 384;//<-Move these to respective classes?

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
    if (!VE.Map.name.equals("Phantom Cavern")) {
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
    vehicleDefaultTerrain = terrain + (U.contains(terrain, " paved ", " rock ", " grid ", " metal ", " brightmetal") ? " hard " : " ground ");
    ((PhongMaterial) Ground.C.getMaterial()).setSpecularMap(U.Images.get(terrain.trim()));
    if (!U.getString(s, 0).isEmpty() && (RGB.getRed() > 0 || RGB.getGreen() > 0 || RGB.getBlue() > 0)) {
     for (long n = terrain.contains(" rock ") ? Long.MAX_VALUE : 4000; --n >= 0; ) {
      if (RGB.getRed() < 1 && RGB.getGreen() < 1 && RGB.getBlue() < 1) {
       RGB = U.getColor(RGB.getRed() * 1.0001, RGB.getGreen() * 1.0001, RGB.getBlue() * 1.0001);
      } else {
       break;
      }
     }
    }
    lowResolution[0] = U.Images.getLowResolution(U.Images.get(terrain.trim()));
    lowResolution[1] = U.Images.getLowResolution(U.Images.getNormalMap(terrain.trim()));
    universal.setSpecularPower(/*sloppy but works->*/vehicleDefaultTerrain.contains(" hard ") ? Specular.Powers.standard : Specular.Powers.dull);
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

 public enum mapBounds {
  ;
  public static double left, right, forward, backward, Y;
  public static boolean slowVehicles;
 }

 public enum Pool {
  ;
  public static boolean exists;
  private static final PhongMaterial PM = new PhongMaterial();
  public static double X, Z, depth;
  public static final Cylinder[] C = new Cylinder[2];
  public static Type type;

  static {
   C[0] = new Cylinder();
   C[1] = new Cylinder();
   C[0].setHeight(0);
   U.setMaterialSecurely(C[0], PM);
   U.setMaterialSecurely(C[1], PM);
   C[1].setCullFace(CullFace.FRONT);
  }

  public enum Type {water, lava, acid}

  public static void load(String s) {
   if (s.startsWith("pool(")) {
    X = U.getValue(s, 0);
    Z = U.getValue(s, 1);
    C[0].setRadius(U.getValue(s, 2));
    C[1].setRadius(U.getValue(s, 2));
    depth = U.getValue(s, 3);
    C[1].setHeight(depth);
    U.Nodes.add(C[0], C[1]);
    double R = 0, G = .25, B = .75;
    PM.setSelfIlluminationMap(null);
    if (s.contains("lava")) {
     type = Pool.Type.lava;
     U.Phong.setSelfIllumination(PM, lavaSelfIllumination[0], lavaSelfIllumination[1], lavaSelfIllumination[2]);
    } else if (s.contains("acid")) {
     type = Pool.Type.acid;
     R = B = .25;
     G = 1;
    }
    PM.setDiffuseMap(U.Images.get("water"));
    U.Phong.setDiffuseRGB(PM, R, G, B);
    U.Phong.setSpecularRGB(PM, Specular.Colors.shiny);
    exists = true;
   }
  }

  public static void runVision() {
   if (exists && Camera.Y > 0 && Camera.Y <= depth && U.distance(Camera.X, X, Camera.Z, Z) < C[0].getRadius()) {
    if (type == Pool.Type.lava) {
     U.fillRGB(graphicsContext, 1, .5 + U.random(.25), 0, .75);
    } else if (type == Pool.Type.acid) {
     U.fillRGB(graphicsContext, .25, .5, .25, .5);
    } else {
     U.fillRGB(graphicsContext, 0, 0, VE.Map.defaultVehicleLightBrightness > 0 ? 0 : .5, .5);
    }
    U.fillRectangle(graphicsContext, .5, .5, 1, 1);
   }
  }
 }

 public enum Storm {
  ;
  private static final Sphere stormCloud = new Sphere(200000);
  public static double stormCloudY;
  public static Sound rain, thunder;

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
    Raindrop.load(s);
    if (s.contains("lightning")) {
     Storm.Lightning.exists = true;
     Storm.Lightning.runLightning();//<-Must run first or mesh wil not load
     U.Nodes.add(Storm.Lightning.MV);
     for (int n = 75; --n >= 0; ) {
      Storm.Lightning.groundBursts.add(new GroundBurst());
     }
     thunder = new Sound("thunder", Double.POSITIVE_INFINITY);
    }
   }
  }

  static void run(Collection<Node> theChildren, boolean update) {
   if (theChildren.contains(stormCloud)) {
    stormCloud.setTranslateY(stormCloudY - Camera.Y);
    if (!Raindrop.instances.isEmpty()) {
     Raindrop.run();
     if (!VE.Match.muteSound && update) {
      rain.loop(Math.sqrt(U.distance(0, 0, Camera.Y, 0, 0, 0)) * .08);
     } else {
      rain.stop();
     }
    }
    if (Storm.Lightning.exists) {
     U.Nodes.Light.remove(Storm.Lightning.light[0]);
     if (Storm.Lightning.strikeStage < 8) {
      Storm.Lightning.runLightning();
      U.setTranslate(Storm.Lightning.MV, Storm.Lightning.X, 0, Storm.Lightning.Z);
      if (Storm.Lightning.strikeStage < 4) {
       U.setTranslate(Storm.Lightning.light[0], Storm.Lightning.X, 0, Storm.Lightning.Z);
       U.Nodes.Light.add(Storm.Lightning.light[0]);
       if (Storm.Lightning.strikeStage < 1) {
        Storm.Lightning.MV.setVisible(true);
        for (int n = 25; --n >= 0; ) {
         Storm.Lightning.groundBursts.get(Storm.Lightning.currentBurst).deploy(Storm.Lightning.X, Storm.Lightning.Z);
         Storm.Lightning.currentBurst = ++Storm.Lightning.currentBurst >= Storm.Lightning.groundBursts.size() ? 0 : Storm.Lightning.currentBurst;
        }
        if (update) {
         thunder.play(Double.NaN, Math.sqrt(U.distance(Camera.X, Storm.Lightning.X, Camera.Y, 0, Camera.Z, Storm.Lightning.Z)) * .04);
        }
       }
      }
      U.setTranslate(Storm.Lightning.light[1], Storm.Lightning.X, 0, Storm.Lightning.Z);
      U.Nodes.Light.add(Storm.Lightning.light[1]);
      U.fillRGB(graphicsContext, 1, 1, 1, U.random(.5));
      U.fillRectangle(graphicsContext, .5, .5, 1, 1);
     } else {
      Storm.Lightning.MV.setVisible(false);
      U.Nodes.Light.remove(Storm.Lightning.light[1]);
     }
     if (++Storm.Lightning.strikeStage > U.random(13000.)) {//<-Progress strike stage using tick?
      Storm.Lightning.X = Camera.X + U.randomPlusMinus(200000.);
      Storm.Lightning.Z = Camera.Z + U.randomPlusMinus(200000.);
      Storm.Lightning.strikeStage = 0;
     }
     for (GroundBurst burst : Storm.Lightning.groundBursts) {
      burst.run();
     }
    }
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
    lightningPM.setSelfIlluminationMap(U.Images.get(SL.Images.white));
    U.setMaterialSecurely(MV, lightningPM);
    for (int n = light.length; --n >= 0; ) {
     light[n] = new PointLight();
     U.Nodes.Light.setRGB(light[n], 1, 1, 1);
    }
   }

   private static void runLightning() {
    double randomX = U.randomPlusMinus(3000.), randomZ = U.randomPlusMinus(3000.);
    ((TriangleMesh) MV.getMesh()).getPoints().setAll((float) U.randomPlusMinus(1000.), (float) stormCloudY, (float) U.randomPlusMinus(1000.),
    (float) U.randomPlusMinus(1000.), (float) stormCloudY, (float) U.randomPlusMinus(1000.),
    (float) (U.randomPlusMinus(1000.) + randomX), (float) (stormCloudY * .5), (float) (U.randomPlusMinus(1000.) + randomZ),
    (float) (U.randomPlusMinus(1000.) + randomX), (float) (stormCloudY * .5), (float) (U.randomPlusMinus(1000.) + randomZ),
    (float) U.randomPlusMinus(1000.), 0, (float) U.randomPlusMinus(1000.),
    (float) U.randomPlusMinus(1000.), 0, (float) U.randomPlusMinus(1000.));
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
   //skyInverse = Color.color(U.clamp(-skyRGB[0] + 1), U.clamp(-skyRGB[1] + 1), U.clamp(-skyRGB[2] + 1));
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

 public static void loadMountains(String s) {
  if (s.startsWith("mountains(")) {
   double size = U.getValue(s, 0), spread = U.getValue(s, 1);
   Random random = new Random(Math.round(U.getValue(s, 2)));//<-Will SecureRandom change established mountain positions on maps?
   for (int n = 20; --n >= 0; ) {
    double[] rotatedX = {spread + spread * random.nextDouble()}, rotatedZ = {spread + spread * random.nextDouble()};
    U.rotate(rotatedX, rotatedZ, random.nextDouble() * 360);
    TE.trackParts.add(new TrackPart(rotatedX[0], rotatedZ[0], 0, size + random.nextDouble() * size, random.nextDouble() * size, random.nextDouble() * size, false, false, true));
   }
  }
 }

 public static void run(boolean gamePlay) {
  List<Node> theChildren = VE.group.getChildren();
  boolean mapViewer = VE.status == VE.Status.mapViewer, updateIfMatchBegan = mapViewer || (gamePlay && VE.Match.started);
  double sunlightAngle = Sun.X != 0 || Sun.Z != 0 ? (((Sun.X / (Sun.Y * 50)) * U.sin(Camera.XZ)) + ((Sun.Z / (Sun.Y * 50)) * U.cos(Camera.XZ))) * U.cos(Camera.YZ) : 0;
  if (VE.Map.name.equals("the Sun")) {
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
   double groundY = VE.vehiclePerspective < VE.vehicles.size() && VE.vehicles.get(VE.vehiclePerspective).P.inPool && Camera.Y > 0 ? Pool.depth : Math.max(0, -Camera.Y * .01);
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
   TE.Paved.universal.setDiffuseMap(U.Images.get(SL.Images.paved));
   TE.Paved.universal.setSpecularMap(U.Images.get(SL.Images.paved));
   TE.Paved.universal.setBumpMap(U.Images.getNormalMap(SL.Images.paved));
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
    U.setTranslate(Pool.C[0], Pool.X, 0, Pool.Z);
    Pool.C[0].setVisible(true);
    Pool.C[1].setVisible(false);
   } else {
    U.setTranslate(Pool.C[1], Pool.X, Pool.depth * .5, Pool.Z);
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
  for (Vehicle vehicle : VE.vehicles) {//Screen-flashes right after drawing windstorm is best
   if (vehicle.screenFlash > 0) {
    U.fillRGB(graphicsContext, 1, 1, 1, vehicle.screenFlash);
    U.fillRectangle(graphicsContext, .5, .5, 1, 1);
   }
  }
 }

 public static void setTerrainSit(ve.Instance I, boolean vehicle) {
  I.Y = U.distance(I.X, Pool.X, I.Z, Pool.Z) < Pool.C[0].getRadius() ? Pool.depth : 0;
  if (Volcano.exists) {
   double volcanoDistance = U.distance(I.X, Volcano.X, I.Z, Volcano.Z);
   I.Y = volcanoDistance < Volcano.radiusBottom && volcanoDistance > Volcano.radiusTop && I.Y > -Volcano.radiusBottom + volcanoDistance ? Math.min(I.Y, -Volcano.radiusBottom + volcanoDistance) : I.Y;
  }
  for (TrackPart trackPart : TE.trackParts) {
   if (trackPart.mound != null) {
    setMoundSit(I, trackPart, vehicle);
   } else if ((!trackPart.wraps || vehicle) && !trackPart.trackPlanes.isEmpty()) {
    double distance = U.distance(I.X, trackPart.X, I.Z, trackPart.Z);
    if (distance < trackPart.renderRadius + I.renderRadius) {
     for (TrackPlane trackPlane : trackPart.trackPlanes) {
      double trackX = trackPlane.X + trackPart.X, trackZ = trackPlane.Z + trackPart.Z;
      if (Math.abs(I.X - trackX) <= trackPlane.radiusX && Math.abs(I.Z - trackZ) <= trackPlane.radiusZ) {
       double trackY = trackPlane.Y + trackPart.Y;
       if (trackPlane.type.contains(" tree ")) {
        I.Y = trackY - trackPlane.radiusY;
       } else if (trackPlane.wall == TrackPlane.Wall.none) {
        if (trackPlane.YZ == 0 && trackPlane.XY == 0) {
         I.Y = Math.min(I.Y, trackY);
        } else {
         if (trackPlane.YZ != 0) {
          I.Y = Math.min(trackY + (I.Z - trackZ) * (trackPlane.radiusY / trackPlane.radiusZ) * (trackPlane.YZ > 0 ? 1 : trackPlane.YZ < 0 ? -1 : 0), I.Y);
         } else if (trackPlane.XY != 0) {
          I.Y = Math.min(trackY + (I.X - trackX) * (trackPlane.radiusY / trackPlane.radiusX) * (trackPlane.XY > 0 ? 1 : trackPlane.XY < 0 ? -1 : 0), I.Y);
         }
        }
       }
      }
     }
    }
   }
  }
 }

 private static void setMoundSit(ve.Instance I, TrackPart TP, boolean vehicle) {
  if (!TP.wraps || vehicle) {
   double distance = U.distance(I.X, TP.X, I.Z, TP.Z),
   radiusTop = TP.mound.getMinorRadius(), moundHeight = TP.mound.getHeight();
   if (distance < radiusTop) {
    I.Y = Math.min(I.Y, TP.Y - moundHeight);
   } else {
    double radiusBottom = TP.mound.getMajorRadius();
    if (distance < radiusBottom && Math.abs(I.Y - (TP.Y - (moundHeight * .5))) <= moundHeight * .5) {
     double slope = moundHeight / Math.abs(radiusBottom - radiusTop);
     I.Y = Math.min(I.Y, TP.Y - (radiusBottom - distance) * slope);
    }
   }
  }
 }

 public static void reset() {
  U.Nodes.remove(E.Sun.S, E.Ground.C);
  U.Nodes.Light.remove(E.Sun.light);
  GroundPlate.instances.clear();
  Cloud.instances.clear();
  Star.instances.clear();
  Raindrop.instances.clear();
  E.Storm.Lightning.groundBursts.clear();
  Snowball.instances.clear();
  Tornado.parts.clear();
  Tsunami.parts.clear();
  Fire.instances.clear();
  Boulder.instances.clear();
  Volcano.rocks.clear();
  Meteor.instances.clear();
  Terrain.terrain = " ground ";
  skyRGB = Ground.RGB = U.getColor(0);
  E.Sun.X = E.Sun.Y = E.Sun.Z
  = E.Wind.maxPotency = E.Wind.speedX = E.Wind.speedZ
  = E.Ground.level = E.Pool.depth = 0;
  Terrain.RGB = U.getColor(0);
  E.Storm.Lightning.exists = Volcano.exists = E.Wind.stormExists = E.mapBounds.slowVehicles = E.Pool.exists = Tornado.movesRepairPoints = false;
  E.mapBounds.left = E.mapBounds.backward = E.mapBounds.Y = Double.NEGATIVE_INFINITY;
  E.mapBounds.right = E.mapBounds.forward = viewableMapDistance = Double.POSITIVE_INFINITY;
  gravity = 7;
  soundMultiple = 1;
  Terrain.reset();
  E.Pool.type = E.Pool.Type.water;
  U.Nodes.Light.setRGB(E.Sun.light, 1, 1, 1);
  U.Nodes.Light.setRGB(ambientLight, 0, 0, 0);
  U.Phong.setDiffuseRGB((PhongMaterial) E.Ground.C.getMaterial(), 0);
  ((PhongMaterial) E.Ground.C.getMaterial()).setSpecularMap(null);
 }
}
