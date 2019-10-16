package ve.environment;

import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import ve.Camera;
import ve.Instance;
import ve.Sound;
import ve.VE;
import ve.trackElements.trackParts.TrackPart;
import ve.trackElements.trackParts.TrackPlane;
import ve.utilities.U;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public enum E {//<-Static content for the game-environment stored here
 ;

 public static Canvas canvas;
 public static GraphicsContext graphicsContext;
 public static final Group lights = new Group();
 public static long lightsAdded;
 public static final AmbientLight ambientLight = new AmbientLight();
 public static final PointLight sunlight = new PointLight();
 private static final PointLight[] lightningLight = new PointLight[2];
 public static final PointLight mapViewerLight = new PointLight();
 public static final Sphere sun = new Sphere(200000000);
 private static final Sphere stormCloud = new Sphere(200000);
 static final PhongMaterial cloudPM = new PhongMaterial();
 static final PhongMaterial starPM = new PhongMaterial();
 private static final PhongMaterial poolPM = new PhongMaterial();
 public static final PhongMaterial phantomPM = new PhongMaterial();
 private static final MeshView lightningMesh = new MeshView();
 public static final List<GroundPlate> groundPlates = new ArrayList<>();
 public static final List<TornadoPart> tornadoParts = new ArrayList<>();
 public static final List<TsunamiPart> tsunamiParts = new ArrayList<>();
 public static final Collection<Fire> fires = new ArrayList<>();
 public static final List<Boulder> boulders = new ArrayList<>();
 public static final List<VolcanoRock> volcanoRocks = new ArrayList<>();
 public static final Collection<Meteor> meteors = new ArrayList<>();
 public static final Collection<Cloud> clouds = new ArrayList<>();
 public static final Collection<Star> stars = new ArrayList<>();
 public static final Collection<Raindrop> raindrops = new ArrayList<>();
 public static final Collection<Snowball> snowballs = new ArrayList<>();
 public static final int splashQuantity = 384;
 private static MeshView volcanoMesh;
 public static boolean poolExists, lightningExists, volcanoExists, windStormExists, tornadoMovesFixpoints;
 public static double renderLevel;
 public static boolean renderAll;
 public static double viewableMapDistance = Double.POSITIVE_INFINITY;
 public static double gravity;
 public static double groundLevel;
 private static double groundX, groundZ;
 public static double limitL, limitR, limitFront, limitBack, limitY;
 public static boolean slowVehiclesWhenAtLimit;
 public static double wind, windX, windZ;
 private static double tornadoMaxTravelDistance;
 static double boulderMaxTravelDistance;
 public static double poolX, poolZ;
 public static double poolDepth;
 public static double sunX, sunY, sunZ;
 private static double sunlightX, sunlightY, sunlightZ;
 private static double sunRGBVariance = .5;
 static double cloudWrapDistance;
 static final double rainWrapDistance = 2000;
 public static double stormCloudY;
 public static double lightningX, lightningZ;
 static final double snowWrapDistance = 2000;
 private static double tsunamiX, tsunamiZ;
 public static double tsunamiSpeedX, tsunamiSpeedZ;
 private static double tsunamiWholeSize;
 public static final double volcanoBottomRadius = 53000, volcanoTopRadius = 3000, volcanoHeight = 50000;
 public static double volcanoX, volcanoZ;
 static double volcanoEruptionStage;
 public static double meteorSpeed;
 public static final double pavedRGB = .55;
 public static final double[] skyRGB = {1, 1, 1};
 public static final double[] groundRGB = new double[3];
 public static final double[] terrainRGB = new double[3];
 static double cloudHeight;
 public static final Cylinder ground = new Cylinder();
 public static final Cylinder[] pool = new Cylinder[2];
 public static final double[] lavaSelfIllumination = {1, .5, 0};
 public static long lightningStrikeStage;
 public static long tsunamiSpeed;
 private static int randomThunderSound;
 private static TsunamiDirection tsunamiDirection;
 public static Color skyInverse = Color.color(0, 0, 0), groundInverse = Color.color(1, 1, 1);
 public static double soundMultiple;
 public static String terrain = "";
 public static final int dustQuantity = 96, shotQuantity = 96, explosionQuantity = 12, thrustTrailQuantity = 48, smokeQuantity = 50;
 public static Pool poolType;
 public static Sound rain, thunder, windstorm, tornado, tsunami, volcano;

 public enum SpecularPowers {
  ;
  public static final double standard = 10, dull = 4, shiny = 100;
 }

 public enum Pool {water, lava, acid}

 public enum TsunamiDirection {
  forward, backward, right, left;

  static TsunamiDirection random() {
   return TsunamiDirection.values()[U.random(TsunamiDirection.values().length)];
  }
 }

 public static void boot() {
  VE.initialization = "Preparing Star Data";
  starPM.setSelfIlluminationMap(U.getImage("white"));
  VE.initialization = "Preparing Cloud Data";
  cloudPM.setSpecularPower(SpecularPowers.dull);
  VE.initialization = "Preparing Ground";
  ground.setRadius(10000000);
  ground.setHeight(0);
  ground.setMaterial(new PhongMaterial());
  U.setSpecularRGB((PhongMaterial) ground.getMaterial(), .5, .5, .5);
  ((PhongMaterial) ground.getMaterial()).setSpecularPower(SpecularPowers.dull);
  VE.initialization = "Preparing Lighting";
  U.setLightRGB(ambientLight, .5, .5, .5);
  U.setLightRGB(sunlight, 1, 1, 1);
  sunlight.setTranslateX(0);
  sunlight.setTranslateZ(0);
  sunlight.setTranslateY(-Long.MAX_VALUE);
  VE.initialization = "Creating Storm Cloud";
  stormCloud.setScaleY(.1);
  stormCloud.setCullFace(CullFace.NONE);
  VE.initialization = "Creating Lightning";
  TriangleMesh lightningTM = new TriangleMesh();
  lightningTM.getTexCoords().setAll(0, 0);
  lightningTM.getFaces().setAll(
  0, 0, 1, 0, 2, 0,
  1, 0, 2, 0, 3, 0,
  2, 0, 3, 0, 4, 0,
  3, 0, 4, 0, 5, 0);
  lightningMesh.setMesh(lightningTM);
  lightningMesh.setCullFace(CullFace.NONE);
  PhongMaterial lightningPM = new PhongMaterial();
  lightningPM.setSelfIlluminationMap(U.getImage("white"));
  lightningMesh.setMaterial(lightningPM);
  for (int n = 2; --n >= 0; ) {
   lightningLight[n] = new PointLight();
   U.setLightRGB(lightningLight[n], 1, 1, 1);
  }
  VE.initialization = "Creating Liquid Pool";
  pool[0] = new Cylinder();
  pool[1] = new Cylinder();
  pool[0].setHeight(0);
  pool[0].setMaterial(poolPM);
  pool[1].setMaterial(poolPM);
  pool[1].setCullFace(CullFace.FRONT);
  VE.initialization = "Creating Volcano";
  TriangleMesh TM = new TriangleMesh();
  TM.getPoints().setAll(0, 0, (float) volcanoBottomRadius,
  9203.3534163473084891409812187737f, 0, 52194.810909647027146437380303245f,
  18127.06759626044285133727957816f, 0, 49803.708901653144354867791698211f,
  26500, 0, 45899.346400575248278477328049906f,
  34067.743313386584295100100725085f, 0, 40600.355485305835865726810479437f,
  40600.355485305835865726810479437f, 0, 34067.743313386584295100100725085f,
  45899.346400575248278477328049906f, 0, 26500,
  49803.708901653144354867791698211f, 0, 18127.06759626044285133727957816f,
  52194.810909647027146437380303245f, 0, 9203.3534163473084891409812187737f,
  (float) volcanoBottomRadius, 0, 0,
  52194.810909647027146437380303245f, 0, -9203.3534163473084891409812187737f,
  49803.708901653144354867791698211f, 0, -18127.06759626044285133727957816f,
  45899.346400575248278477328049906f, 0, -26500,
  40600.355485305835865726810479437f, 0, -34067.743313386584295100100725085f,
  34067.743313386584295100100725085f, 0, -40600.355485305835865726810479437f,
  26500, 0, -45899.346400575248278477328049906f,
  18127.06759626044285133727957816f, 0, -49803.708901653144354867791698211f,
  9203.3534163473084891409812187737f, 0, -52194.810909647027146437380303245f,
  0, 0, -(float) volcanoBottomRadius,
  -9203.3534163473084891409812187737f, 0, -52194.810909647027146437380303245f,
  -18127.06759626044285133727957816f, 0, -49803.708901653144354867791698211f,
  -26500, 0, -45899.346400575248278477328049906f,
  -34067.743313386584295100100725085f, 0, -40600.355485305835865726810479437f,
  -40600.355485305835865726810479437f, 0, -34067.743313386584295100100725085f,
  -45899.346400575248278477328049906f, 0, -26500,
  -49803.708901653144354867791698211f, 0, -18127.06759626044285133727957816f,
  -52194.810909647027146437380303245f, 0, -9203.3534163473084891409812187737f,
  -(float) volcanoBottomRadius, 0, 0,
  -52194.810909647027146437380303245f, 0, 9203.3534163473084891409812187737f,
  -49803.708901653144354867791698211f, 0, 18127.06759626044285133727957816f,
  -45899.346400575248278477328049906f, 0, 26500,
  -40600.355485305835865726810479437f, 0, 34067.743313386584295100100725085f,
  -34067.743313386584295100100725085f, 0, 40600.355485305835865726810479437f,
  -26500, 0, 45899.346400575248278477328049906f,
  -18127.06759626044285133727957816f, 0, 49803.708901653144354867791698211f,
  -9203.3534163473084891409812187737f, 0, 52194.810909647027146437380303245f,
  0, -(float) volcanoHeight, (float) volcanoTopRadius,
  520.94453300079104655514988030794f, -(float) volcanoHeight, 2954.4232590366241781002290737686f,
  1026.0604299770061991322988440468f, -(float) volcanoHeight, 2819.0778623577251521623278319742f,
  1500, -(float) volcanoHeight, 2598.0762113533159402911695122588f,
  1928.3628290596179789679302297218f, -(float) volcanoHeight, 2298.1333293569341056071779516663f,
  2298.1333293569341056071779516663f, -(float) volcanoHeight, 1928.3628290596179789679302297218f,
  2598.0762113533159402911695122588f, -(float) volcanoHeight, 1500,
  2819.0778623577251521623278319742f, -(float) volcanoHeight, 1026.0604299770061991322988440468f,
  2954.4232590366241781002290737686f, -(float) volcanoHeight, 520.94453300079104655514988030794f,
  (float) volcanoTopRadius, -(float) volcanoHeight, 0,
  2954.4232590366241781002290737686f, -(float) volcanoHeight, -520.94453300079104655514988030794f,
  2819.0778623577251521623278319742f, -(float) volcanoHeight, -1026.0604299770061991322988440468f,
  2598.0762113533159402911695122588f, -(float) volcanoHeight, -1500,
  2298.1333293569341056071779516663f, -(float) volcanoHeight, -1928.3628290596179789679302297218f,
  1928.3628290596179789679302297218f, -(float) volcanoHeight, -2298.1333293569341056071779516663f,
  1500, -(float) volcanoHeight, -2598.0762113533159402911695122588f,
  1026.0604299770061991322988440468f, -(float) volcanoHeight, -2819.0778623577251521623278319742f,
  520.94453300079104655514988030794f, -(float) volcanoHeight, -2954.4232590366241781002290737686f,
  0, -(float) volcanoHeight, -(float) volcanoTopRadius,
  -520.94453300079104655514988030794f, -(float) volcanoHeight, -2954.4232590366241781002290737686f,
  -1026.0604299770061991322988440468f, -(float) volcanoHeight, -2819.0778623577251521623278319742f,
  -1500, -(float) volcanoHeight, -2598.0762113533159402911695122588f,
  -1928.3628290596179789679302297218f, -(float) volcanoHeight, -2298.1333293569341056071779516663f,
  -2298.1333293569341056071779516663f, -(float) volcanoHeight, -1928.3628290596179789679302297218f,
  -2598.0762113533159402911695122588f, -(float) volcanoHeight, -1500,
  -2819.0778623577251521623278319742f, -(float) volcanoHeight, -1026.0604299770061991322988440468f,
  -2954.4232590366241781002290737686f, -(float) volcanoHeight, -520.94453300079104655514988030794f,
  -(float) volcanoTopRadius, -(float) volcanoHeight, 0,
  -2954.4232590366241781002290737686f, -(float) volcanoHeight, 520.94453300079104655514988030794f,
  -2819.0778623577251521623278319742f, -(float) volcanoHeight, 1026.0604299770061991322988440468f,
  -2598.0762113533159402911695122588f, -(float) volcanoHeight, 1500,
  -2298.1333293569341056071779516663f, -(float) volcanoHeight, 1928.3628290596179789679302297218f,
  -1928.3628290596179789679302297218f, -(float) volcanoHeight, 2298.1333293569341056071779516663f,
  -1500, -(float) volcanoHeight, 2598.0762113533159402911695122588f,
  -1026.0604299770061991322988440468f, -(float) volcanoHeight, 2819.0778623577251521623278319742f,
  -520.94453300079104655514988030794f, -(float) volcanoHeight, 2954.4232590366241781002290737686f);
  if (U.random() < .5) {
   TM.getTexCoords().setAll(0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0);
  } else {
   TM.getTexCoords().setAll(0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1);
  }
  TM.getFaces().addAll(
  0, 0, 1, 1, 37, 37, 1, 1, 2, 2, 38, 38, 2, 2, 3, 3, 39, 39, 3, 3, 4, 4, 40, 40, 4, 4, 5, 5, 41, 41, 5, 5, 6, 6, 42, 42, 6, 6, 7, 7, 43, 43, 7, 7, 8, 8, 44, 44, 8, 8, 9, 9, 45, 45, 9, 9, 10, 10, 46, 46, 10, 10, 11, 11, 47, 47, 11, 11, 12, 12, 48, 48, 12, 12, 13, 13, 49, 49, 13, 13, 14, 14, 50, 50, 14, 14, 15, 15, 51, 51, 15, 15, 16, 16, 52, 52, 16, 16, 17, 17, 53, 53, 17, 17, 18, 18, 54, 54, 18, 18, 19, 19, 55, 55, 19, 19, 20, 20, 56, 56, 20, 20, 21, 21, 57, 57, 21, 21, 22, 22, 58, 58, 22, 22, 23, 23, 59, 59, 23, 23, 24, 24, 60, 60, 24, 24, 25, 25, 61, 61, 25, 25, 26, 26, 62, 62, 26, 26, 27, 27, 63, 63, 27, 27, 28, 28, 64, 64, 28, 28, 29, 29, 65, 65, 29, 29, 30, 30, 66, 66, 30, 30, 31, 31, 67, 67, 31, 31, 32, 32, 68, 68, 32, 32, 33, 33, 69, 69, 33, 33, 34, 34, 70, 70, 34, 34, 35, 35, 71, 71, 35, 35, 0, 0, 36, 36,
  0, 0, 36, 36, 37, 37, 1, 1, 37, 37, 38, 38, 2, 2, 38, 38, 39, 39, 3, 3, 39, 39, 40, 40, 4, 4, 40, 40, 41, 41, 5, 5, 41, 41, 42, 42, 6, 6, 42, 42, 43, 43, 7, 7, 43, 43, 44, 44, 8, 8, 44, 44, 45, 45, 9, 9, 45, 45, 46, 46, 10, 10, 46, 46, 47, 47, 11, 11, 47, 47, 48, 48, 12, 12, 48, 48, 49, 49, 13, 13, 49, 49, 50, 50, 14, 14, 50, 50, 51, 51, 15, 15, 51, 51, 52, 52, 16, 16, 52, 52, 53, 53, 17, 17, 53, 53, 54, 54, 18, 18, 54, 54, 55, 55, 19, 19, 55, 55, 56, 56, 20, 20, 56, 56, 57, 57, 21, 21, 57, 57, 58, 58, 22, 22, 58, 58, 59, 59, 23, 23, 59, 59, 60, 60, 24, 24, 60, 60, 61, 61, 25, 25, 61, 61, 62, 62, 26, 26, 62, 62, 63, 63, 27, 27, 63, 63, 64, 64, 28, 28, 64, 64, 65, 65, 29, 29, 65, 65, 66, 66, 30, 30, 66, 66, 67, 67, 31, 31, 67, 67, 68, 68, 32, 32, 68, 68, 69, 69, 33, 33, 69, 69, 70, 70, 34, 34, 70, 70, 71, 71, 35, 35, 71, 71, 36, 36);
  volcanoMesh = new MeshView(TM);
  volcanoMesh.setCullFace(CullFace.NONE);
 }

 public static void loadSky(String s) {
  if (s.startsWith("sky(")) {
   skyRGB[0] = U.getValue(s, 0);
   skyRGB[1] = U.getValue(s, 1);
   skyRGB[2] = U.getValue(s, 2);
   skyInverse = Color.color(U.clamp(-skyRGB[0] + 1), U.clamp(-skyRGB[1] + 1), U.clamp(-skyRGB[2] + 1));
   double r = skyRGB[0], g = skyRGB[1], b = skyRGB[2];
   if (r > 0 || g > 0 || b > 0) {
    while (r < 1 && g < 1 && b < 1) {
     r *= 1.001;
     g *= 1.001;
     b *= 1.001;
    }
   }
   U.setLightRGB(sunlight, r, g, b);
  }
 }

 public static void loadGround(String s) {
  if (s.startsWith("ground(")) {
   groundRGB[0] = U.getValue(s, 0);
   groundRGB[1] = U.getValue(s, 1);
   groundRGB[2] = U.getValue(s, 2);
   U.setDiffuseRGB((PhongMaterial) ground.getMaterial(), groundRGB[0], groundRGB[1], groundRGB[2]);
   terrainRGB[0] = U.getValue(s, 0);
   terrainRGB[1] = U.getValue(s, 1);
   terrainRGB[2] = U.getValue(s, 2);
   if (!VE.mapName.equals("Phantom Cavern")) {
    U.add(ground);
   }
   groundInverse = Color.color(U.clamp(-groundRGB[0] + 1), U.clamp(-groundRGB[1] + 1), U.clamp(-groundRGB[2] + 1));
  }
 }

 public static void loadTerrain(String s) {
  if (s.startsWith("terrain(")) {
   terrain = " " + U.getString(s, 0) + " ";
   ((PhongMaterial) ground.getMaterial()).setSpecularMap(U.getImage(terrain.trim()));
   if (!U.getString(s, 0).isEmpty() && (terrainRGB[0] > 0 || terrainRGB[1] > 0 || terrainRGB[2] > 0)) {
    for (int n = terrain.contains(" rock ") ? Integer.MAX_VALUE : 4000; --n >= 0; ) {
     if (terrainRGB[0] < 1 && terrainRGB[1] < 1 && terrainRGB[2] < 1) {
      terrainRGB[0] *= 1.0001;
      terrainRGB[1] *= 1.0001;
      terrainRGB[2] *= 1.0001;
     } else {
      break;
     }
    }
   }
   loadGroundPlates(terrain.trim());
  }
 }

 public static void loadSun(String s) {
  if (s.startsWith("sun(")) {
   sunlightX = U.getValue(s, 0);
   sunlightY = U.getValue(s, 2);
   sunlightZ = U.getValue(s, 1);
   U.addLight(sunlight);
   sunX = U.getValue(s, 0) * 2;
   sunY = U.getValue(s, 2) * 2;
   sunZ = U.getValue(s, 1) * 2;
   PhongMaterial PM = new PhongMaterial();
   U.setDiffuseRGB(PM, 1, 1, 1);
   U.setSpecularRGB(PM, 1, 1, 1);
   PM.setSpecularPower(0);
   U.setSelfIllumination(PM, skyRGB[0], skyRGB[1], skyRGB[2]);
   sun.setMaterial(PM);
   U.add(sun);
  }
 }

 private static void loadGroundPlates(String terrain) {
  if (groundLevel <= 0 && !terrain.contains(" snow ")) {
   for (int n = 0; n < 419; n++) {
    groundPlates.add(new GroundPlate(VE.mapName.equals("Epic Trip") ? 1500 : 1732.0508075688772935274463415059));
   }
   double baseX = -30000, baseZ = -30000;
   boolean shift = false;
   for (GroundPlate groundPlate : groundPlates) {
    PhongMaterial PM = new PhongMaterial();
    groundPlate.setMaterial(PM);
    groundPlate.X = baseX;
    groundPlate.Z = baseZ;
    baseZ += 3000;
    if (baseZ > 30000) {
     baseZ = -30000;
     shift = !shift;
     baseZ -= shift ? 1500 : 0;
     baseX += 2598.0762113533159402911695122588;
    }
    PM.setSpecularPower(SpecularPowers.dull);
    PM.setDiffuseMap(U.getImage(terrain));
    PM.setSpecularMap(U.getImage(terrain));
    PM.setBumpMap(U.getImageNormal(terrain));
    groundPlate.setRotationAxis(Rotate.Y_AXIS);
    groundPlate.setRotate(-30 + (60 * U.random(6)));//<-Hex-rotation basis can't be random double!
    U.add(groundPlate);
    double varyRGB = 1 + U.randomPlusMinus(.05);
    U.setDiffuseRGB(PM, terrainRGB[0] * varyRGB, terrainRGB[1] * varyRGB, terrainRGB[2] * varyRGB);
    U.setSpecularRGB(PM, terrainRGB[0] * varyRGB, terrainRGB[1] * varyRGB, terrainRGB[2] * varyRGB);
    groundPlate.clampXZ();
   }
   for (int n = 0; n < groundPlates.size(); n++) {//<-Do NOT use forEach--ConcurrentModificationException will occur!
    groundPlates.get(n).checkDuplicate();
   }
  }
 }

 public static void loadClouds(String s) {
  if (s.startsWith("clouds(")) {
   cloudHeight = U.getValue(s, 3);
   cloudWrapDistance = VE.mapName.equals("Ethereal Mist") ? 100000 : U.listEquals(VE.mapName, "the Test of Endurance", "an Immense Relevance", "SUMMIT of EPIC") ? 10000000 : 1000000;
   U.setDiffuseRGB(cloudPM, U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
   for (int n = 0; n < U.random(120); n++) {
    clouds.add(new Cloud());
   }
  }
 }

 public static void loadMountains(String s) {
  if (s.startsWith("mountains(")) {
   double size = U.getValue(s, 0), spread = U.getValue(s, 1);
   Random random = new Random(Math.round(U.getValue(s, 2)));//<-Will SecureRandom change established mountain positions on maps?
   for (int n = 20; --n >= 0; ) {
    double[] rotatedX = {spread + spread * random.nextDouble()}, rotatedZ = {spread + spread * random.nextDouble()};
    U.rotate(rotatedX, rotatedZ, random.nextDouble() * 360);
    VE.trackParts.add(new TrackPart(rotatedX[0], rotatedZ[0], 0, size + random.nextDouble() * size, random.nextDouble() * size, random.nextDouble() * size, false, false));
   }
  }
 }

 public static void loadPool(String s) {
  if (s.startsWith("pool(")) {
   poolX = U.getValue(s, 0);
   poolZ = U.getValue(s, 1);
   pool[0].setRadius(U.getValue(s, 2));
   pool[1].setRadius(U.getValue(s, 2));
   poolDepth = U.getValue(s, 3);
   pool[1].setHeight(poolDepth);
   U.add(pool[0], pool[1]);
   double R = 0, G = .25, B = .75;
   poolPM.setSelfIlluminationMap(null);
   if (s.contains("lava")) {
    poolType = Pool.lava;
    U.setSelfIllumination(poolPM, lavaSelfIllumination[0], lavaSelfIllumination[1], lavaSelfIllumination[2]);
   } else if (s.contains("acid")) {
    poolType = Pool.acid;
    R = B = .25;
    G = 1;
   }
   poolPM.setDiffuseMap(U.getImage("water"));
   U.setDiffuseRGB(poolPM, R, G, B);
   U.setSpecularRGB(poolPM, 1, 1, 1);
   poolExists = true;
  }
 }

 public static void loadStorm(String s) {
  if (s.startsWith("storm(")) {
   PhongMaterial PM = new PhongMaterial();
   U.setDiffuseRGB(PM, U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
   stormCloud.setMaterial(PM);
   stormCloudY = U.getValue(s, 3);
   U.add(stormCloud);
   if (s.contains("rain")) {
    for (int n = 0; n < 1000; n++) {
     raindrops.add(new Raindrop());
    }
   }
   if (s.contains("lightning")) {
    lightningExists = true;
    runLightning();//<-Must run first or mesh wil not load
    U.add(lightningMesh);
    thunder = new Sound("thunder", Double.POSITIVE_INFINITY);
   }
  }
 }

 public static void loadTornado(String s) {
  if (s.startsWith("tornado(")) {
   double size = 1;
   for (int n = 0; n < 40; n++) {
    tornadoParts.add(new TornadoPart(U.getValue(s, 0) * size, U.getValue(s, 0) * size));
    tornadoParts.get(n).C.setMaterial(new PhongMaterial());
    size *= U.getValue(s, 2);
    tornadoParts.get(n).Y = U.getValue(s, 1) * n / 40.;
    U.add(tornadoParts.get(n).C);
    tornadoParts.get(n).groundDust = new Cylinder(
    (n + 1) * U.getValue(s, 0) * .01,
    (n + 1) * U.getValue(s, 0) * .01);
    U.add(tornadoParts.get(n).groundDust);
   }
   tornadoMaxTravelDistance = U.getValue(s, 3);
   tornadoMovesFixpoints = s.contains("moveFixpoints");
  }
 }

 public static void loadTsunami(String s) {
  if (s.startsWith("tsunami(")) {
   for (int n = 0; n < 200; n++) {
    tsunamiParts.add(new TsunamiPart(U.getValue(s, 0)));
   }
   tsunamiSpeed = Math.round(U.getValue(s, 1));
   try {
    tsunamiWholeSize = Math.abs(U.getValue(s, 2));
   } catch (RuntimeException e) {
    tsunamiWholeSize = 200000;
   }
   for (TsunamiPart tsunamiPart : tsunamiParts) {
    tsunamiPart.Y = -tsunamiPart.C.getRadius() * .5;
    U.add(tsunamiPart.C);
   }
   wrapTsunami(VE.mapName.equals("Death Pit") ? .33 : 1);
  }
 }

 public static void loadBoulders(String s) {
  if (s.startsWith("boulders(")) {
   for (int n = 0; n < U.getValue(s, 0); n++) {
    boulders.add(new Boulder(U.getValue(s, 1), U.getValue(s, 2)));
   }
   boulderMaxTravelDistance = U.getValue(s, 3);
  }
 }

 public static void loadFire(String s) {
  if (s.startsWith("fire(")) {
   fires.add(new Fire(s));
  }
 }

 public static void loadVolcano(String s) {
  if (s.startsWith("volcano(")) {
   PhongMaterial volcanoPM = new PhongMaterial();
   U.setDiffuseRGB(volcanoPM, groundRGB[0], groundRGB[1], groundRGB[2]);
   volcanoMesh.setMaterial(volcanoPM);
   volcanoX = U.getValue(s, 0);
   volcanoZ = U.getValue(s, 1);
   U.add(volcanoMesh);
   volcanoExists = true;
   if (s.contains("active")) {
    boolean isLava = true;
    for (int n = 0; n < 200; n++) {
     volcanoRocks.add(new VolcanoRock(1000 + (n * 10), 4 + U.random(2)));
     PhongMaterial PM = new PhongMaterial();
     if (isLava) {
      volcanoRocks.get(n).isLava = true;
      U.setDiffuseRGB(PM, 0, 0, 0);
      U.setSpecularRGB(PM, 0, 0, 0);
     } else {
      PM.setDiffuseMap(U.getImage("rock"));
      PM.setSpecularMap(U.getImage("rock"));
      PM.setBumpMap(U.getImageNormal("rock"));
     }
     volcanoRocks.get(n).S.setMaterial(PM);
     isLava = !isLava;
     U.add(volcanoRocks.get(n).S);
    }
   }
  }
 }

 public static void loadMeteors(String s) {
  if (s.startsWith("meteors(")) {
   for (int n = 0; n < U.getValue(s, 0); n++) {
    meteors.add(new Meteor(U.getValue(s, 1)));
   }
   meteorSpeed = U.getValue(s, 2);
  }
 }

 public static void run() {
  List<Node> theChildren = VE.group.getChildren();
  boolean gamePlay = VE.status == VE.Status.play || VE.status == VE.Status.replay,
  mapViewer = VE.status == VE.Status.mapViewer, update = mapViewer || (gamePlay && VE.matchStarted);
  double sunlightAngle = sunX != 0 || sunZ != 0 ? (((sunX / (sunY * 50)) * U.sin(Camera.XZ)) + ((sunZ / (sunY * 50)) * U.cos(Camera.XZ))) * U.cos(Camera.YZ) : 0;
  if (VE.mapName.equals("the Sun")) {
   sunRGBVariance *= U.random() < .5 ? 81 / 80. : 80 / 81.;
   sunRGBVariance = U.clamp(.2, sunRGBVariance, 1);
   VE.scene3D.setFill(Color.color(sunRGBVariance, sunRGBVariance * .5, 0));
  } else {
   VE.scene3D.setFill(Color.color(U.clamp(skyRGB[0] - sunlightAngle), U.clamp(skyRGB[1] - sunlightAngle), U.clamp(skyRGB[2] - sunlightAngle)));
  }
  if (lights.getChildren().contains(sunlight)) {
   U.setTranslate(sunlight, sunlightX, sunlightY, sunlightZ);
   if (theChildren.contains(sun)) {
    if (U.render(sunX, sunY, sunZ, -sun.getRadius())) {
     U.setTranslate(sun, sunX, sunY, sunZ);
     sun.setVisible(true);
    } else {
     sun.setVisible(false);
    }
   }
  }
  if (theChildren.contains(ground) && groundLevel <= 0) {
   double groundY = VE.vehiclePerspective < VE.vehicles.size() && VE.vehicles.get(VE.vehiclePerspective).inPool && Camera.Y > 0 ? poolDepth : Math.max(0, -Camera.Y * .01);
   while (Math.abs(groundX - Camera.X) > 100000) groundX += groundX > Camera.X ? -200000 : 200000;
   while (Math.abs(groundZ - Camera.Z) > 100000) groundZ += groundZ > Camera.Z ? -200000 : 200000;
   if (Camera.Y < groundY) {
    U.setTranslate(ground, groundX, groundY, groundZ);
    ground.setVisible(true);
   } else {
    ground.setVisible(false);
   }
  } else {
   ground.setVisible(false);
  }
  for (Star star : stars) {
   star.run();
  }
  for (Cloud cloud : clouds) {
   cloud.run();
  }
  if (!groundPlates.isEmpty()) {
   double radius = pool[0].getRadius() - groundPlates.get(0).getRadius();
   for (GroundPlate groundPlate : groundPlates) {
    groundPlate.run(radius);
   }
  }
  if (theChildren.contains(stormCloud)) {
   stormCloud.setTranslateY(stormCloudY - Camera.Y);
   if (!raindrops.isEmpty()) {
    for (Raindrop raindrop : raindrops) {
     raindrop.run();
    }
    if (!VE.muteSound && (gamePlay || mapViewer)) {
     rain.loop(Math.sqrt(U.distance(0, 0, Camera.Y, 0, 0, 0)) * .08);
    } else {
     rain.stop();
    }
   }
   if (lightningExists) {
    U.removeLight(lightningLight[0]);
    if (lightningStrikeStage < 8) {
     runLightning();
     U.setTranslate(lightningMesh, lightningX, 0, lightningZ);
     if (lightningStrikeStage < 4) {
      U.setTranslate(lightningLight[0], lightningX, 0, lightningZ);
      U.addLight(lightningLight[0]);
      if (lightningStrikeStage < 1) {
       lightningMesh.setVisible(true);
       if (gamePlay || mapViewer) {
        randomThunderSound = U.randomize(randomThunderSound, thunder.clips.size());
        thunder.play(randomThunderSound, Math.sqrt(U.distance(Camera.X, lightningX, Camera.Y, 0, Camera.Z, lightningZ)) * .04);
       }
      }
     }
     U.setTranslate(lightningLight[1], lightningX, 0, lightningZ);
     U.addLight(lightningLight[1]);
     U.fillRGB(graphicsContext, 1, 1, 1, U.random(.5));
     U.fillRectangle(graphicsContext, .5, .5, 1, 1);
    } else {
     lightningMesh.setVisible(false);
     U.removeLight(lightningLight[1]);
    }
    if (++lightningStrikeStage > U.random(13000.)) {
     lightningX = Camera.X + U.randomPlusMinus(200000.);
     lightningZ = Camera.Z + U.randomPlusMinus(200000.);
     lightningStrikeStage = 0;
    }
   }
  }
  if (poolExists) {
   if (Camera.Y < 0) {
    U.setTranslate(pool[0], poolX, 0, poolZ);
    pool[0].setVisible(true);
    pool[1].setVisible(false);
   } else {
    U.setTranslate(pool[1], poolX, poolDepth * .5, poolZ);
    pool[1].setVisible(true);
    pool[0].setVisible(false);
   }
  }
  int n;
  if (!tornadoParts.isEmpty()) {
   if (wind > 0 && (gamePlay || mapViewer)) {
    tornadoParts.get(0).X += windX * VE.tick;
    tornadoParts.get(0).Z += windZ * VE.tick;
   }
   if (U.distance(0, tornadoParts.get(0).X, 0, tornadoParts.get(0).Z) > tornadoMaxTravelDistance) {
    tornadoParts.get(0).X *= .999;
    tornadoParts.get(0).Z *= .999;
    windX *= -1;
    windZ *= -1;
   }
   for (n = 1; n < tornadoParts.size(); n++) {
    tornadoParts.get(n).X = (tornadoParts.get(n - 1).X + tornadoParts.get(n).X) * .5;
    tornadoParts.get(n).Z = (tornadoParts.get(n - 1).Z + tornadoParts.get(n).Z) * .5;
   }
   for (TornadoPart tornadoPart : tornadoParts) {
    tornadoPart.run();
   }
   if (!VE.muteSound && (gamePlay || mapViewer)) {
    tornado.loop(Math.sqrt(U.distance(Camera.X, tornadoParts.get(0).X, Camera.Y, 0, Camera.Z, tornadoParts.get(0).Z)) * .08);
   } else {
    tornado.stop();
   }
  }
  if (wind > 0) {
   windX += (wind * U.random(VE.tick)) - (wind * U.random(VE.tick));
   windZ += (wind * U.random(VE.tick)) - (wind * U.random(VE.tick));
   windX -= windX * .0004 * VE.tick;
   windZ -= windZ * .0004 * VE.tick;
   windX = U.clamp(-wind * 20, windX, wind * 20);
   windZ = U.clamp(-wind * 20, windZ, wind * 20);
  }
  for (Snowball snowball : snowballs) {
   snowball.run();
  }
  if (poolType == Pool.lava) {
   U.setDiffuseRGB(poolPM, 1, .25 + U.random(.5), 0);
  }
  if (!tsunamiParts.isEmpty()) {
   if (update && Math.abs(tsunamiDirection == TsunamiDirection.left || tsunamiDirection == TsunamiDirection.right ? tsunamiX : tsunamiZ) > tsunamiWholeSize) {
    wrapTsunami(1);
   }
   if (update) {
    tsunamiX += tsunamiSpeedX * VE.tick;
    tsunamiZ += tsunamiSpeedZ * VE.tick;
   }
   for (TsunamiPart tsunamiPart : tsunamiParts) {
    tsunamiPart.run(update);
   }
   if (!VE.muteSound && (gamePlay || mapViewer)) {
    double soundDistance = Double.POSITIVE_INFINITY;
    for (TsunamiPart tsunamiPart : tsunamiParts) {
     soundDistance = Math.min(soundDistance, U.distance(tsunamiPart));
    }
    tsunami.loop(Math.sqrt(soundDistance) * .04);
   } else {
    tsunami.stop();
   }
  }
  for (Fire fire : fires) {
   fire.run(gamePlay || mapViewer);
  }
  for (Boulder boulder : boulders) {
   boulder.run(update);
  }
  if (volcanoExists) {
   U.setTranslate(volcanoMesh, volcanoX, 0, volcanoZ);
   if (!volcanoRocks.isEmpty()) {
    if (volcanoEruptionStage > 0) {
     long rocksLanded = 0;
     for (VolcanoRock volcanoRock : volcanoRocks) {
      if (volcanoRock.groundHit) {
       volcanoRock.Y = -U.random(46000.);
       rocksLanded++;
      } else {
       if (update) {
        volcanoRock.X += volcanoRock.speedX;
        volcanoRock.Y += volcanoRock.speedY;
        volcanoRock.Z += volcanoRock.speedZ;
        volcanoRock.speedY += gravity * VE.tick;
       }
       if (volcanoRock.Y > volcanoRock.S.getRadius()) {
        volcanoRock.groundHit = true;
        volcanoRock.X = volcanoX;
        volcanoRock.Z = volcanoZ;
       }
      }
     }
     volcanoEruptionStage = rocksLanded >= volcanoRocks.size() ? 0 : volcanoEruptionStage + VE.tick;
    } else if (update) {
     for (VolcanoRock volcanoRock : volcanoRocks) {
      volcanoRock.deploy();
     }
     volcanoEruptionStage = 1;
     volcano.play(Math.sqrt(U.distance(Camera.X, volcanoX, Camera.Y, -50000, Camera.Z, volcanoZ)) * .02);
    }
    for (VolcanoRock volcanoRock : volcanoRocks) {
     volcanoRock.run();
    }
   }
  }
  //runPoolVision();
  if (windStormExists) {
   double stormPower = Math.sqrt(StrictMath.pow(windX, 2) * StrictMath.pow(windZ, 2));//<-Multiplied--not added!
   U.fillRGB(graphicsContext, groundRGB[0], groundRGB[1], groundRGB[2], .05);
   double dustWidth = VE.width * .25, dustHeight = VE.height * .25;
   for (n = (int) (stormPower * .025); --n >= 0; ) {
    graphicsContext.fillOval(-dustWidth + U.random(VE.width + dustWidth), -dustHeight + U.random(VE.height + dustHeight), dustWidth, dustHeight);
   }
   if (!VE.muteSound && (gamePlay || mapViewer)) {
    windstorm.loop(40 - (Math.min(40, 3 * StrictMath.pow(stormPower, .25))));
   } else {
    windstorm.stop();
   }
  }
  for (Meteor meteor : meteors) {
   meteor.run(gamePlay || mapViewer);
  }
 }

 private static void runLightning() {
  double randomX = U.randomPlusMinus(3000.), randomZ = U.randomPlusMinus(3000.);
  ((TriangleMesh) lightningMesh.getMesh()).getPoints().setAll((float) U.randomPlusMinus(1000.), (float) stormCloudY, (float) U.randomPlusMinus(1000.),
  (float) U.randomPlusMinus(1000.), (float) stormCloudY, (float) U.randomPlusMinus(1000.),
  (float) (U.randomPlusMinus(1000.) + randomX), (float) (stormCloudY * .5), (float) (U.randomPlusMinus(1000.) + randomZ),
  (float) (U.randomPlusMinus(1000.) + randomX), (float) (stormCloudY * .5), (float) (U.randomPlusMinus(1000.) + randomZ),
  (float) U.randomPlusMinus(1000.), 0, (float) U.randomPlusMinus(1000.),
  (float) U.randomPlusMinus(1000.), 0, (float) U.randomPlusMinus(1000.));
 }

 private static void wrapTsunami(double distanceOutMultiple) {
  tsunamiDirection = TsunamiDirection.random();
  if (tsunamiDirection == TsunamiDirection.forward || tsunamiDirection == TsunamiDirection.backward) {
   tsunamiX = -tsunamiWholeSize * distanceOutMultiple;
   tsunamiSpeedX = 0;
   if (tsunamiDirection == TsunamiDirection.forward) {
    tsunamiZ = -tsunamiWholeSize * distanceOutMultiple;
    tsunamiSpeedZ = tsunamiSpeed;
   } else {
    tsunamiZ = tsunamiWholeSize * distanceOutMultiple;
    tsunamiSpeedZ = -tsunamiSpeed;
   }
  } else {
   tsunamiZ = -tsunamiWholeSize * distanceOutMultiple;
   tsunamiSpeedZ = 0;
   if (tsunamiDirection == TsunamiDirection.left) {
    tsunamiX = tsunamiWholeSize * distanceOutMultiple;
    tsunamiSpeedX = -tsunamiSpeed;
   } else if (tsunamiDirection == TsunamiDirection.right) {
    tsunamiX = -tsunamiWholeSize * distanceOutMultiple;
    tsunamiSpeedX = tsunamiSpeed;
   }
  }
  double tsunamiPartShift = tsunamiWholeSize * .01;
  if (tsunamiDirection == TsunamiDirection.forward || tsunamiDirection == TsunamiDirection.backward) {
   for (int n = tsunamiParts.size(); --n >= 0; ) {
    tsunamiParts.get(n).X = n * tsunamiPartShift - tsunamiWholeSize;
    tsunamiParts.get(n).Z = tsunamiWholeSize * distanceOutMultiple * (tsunamiDirection == TsunamiDirection.forward ? -1 : 1);
   }
  } else {
   for (int n = tsunamiParts.size(); --n >= 0; ) {
    tsunamiParts.get(n).Z = n * tsunamiPartShift - tsunamiWholeSize;
    tsunamiParts.get(n).X = tsunamiWholeSize * distanceOutMultiple * (tsunamiDirection == TsunamiDirection.left ? 1 : -1);
   }
  }
 }

 public static void setTerrainSit(Instance I, boolean vehicle) {
  I.Y = U.distance(I.X, poolX, I.Z, poolZ) < pool[0].getRadius() ? poolDepth : 0;
  if (volcanoExists) {
   double volcanoDistance = U.distance(I.X, volcanoX, I.Z, volcanoZ);
   I.Y = volcanoDistance < volcanoBottomRadius && volcanoDistance > volcanoTopRadius && I.Y > -volcanoBottomRadius + volcanoDistance ? Math.min(I.Y, -volcanoBottomRadius + volcanoDistance) : I.Y;
  }
  for (TrackPart trackPart : VE.trackParts) {
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

 private static void setMoundSit(Instance I, TrackPart TP, boolean vehicle) {
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

 public static void runPoolVision() {
  if (poolExists && Camera.Y > 0 && Camera.Y <= poolDepth && U.distance(Camera.X, poolX, Camera.Z, poolZ) < pool[0].getRadius()) {//<-Should be in E.run, but works more consistently here
   if (poolType == E.Pool.lava) {
    U.fillRGB(graphicsContext, 1, .5 + U.random(.25), 0, .75);
   } else if (poolType == E.Pool.acid) {
    U.fillRGB(graphicsContext, .25, .5, .25, .5);
   } else {
    U.fillRGB(graphicsContext, 0, 0, VE.defaultVehicleLightBrightness > 0 ? 0 : .5, .5);
   }
   U.fillRectangle(graphicsContext, .5, .5, 1, 1);
  }
 }
}
