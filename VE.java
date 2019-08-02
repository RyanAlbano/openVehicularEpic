/*
THE VEHICULAR EPIC

HEAD DEVELOPER: Ryan Albano
ASSISTANT DEVS: Vitor Macedo, Dany Fernández Diaz

NAMING SYSTEM (variable names are usually typed as follows):

wordWord

For example, 'current checkpoint' would be typed as 'currentCheckpoint'.

Successive capital letters are usually abbreviations, such as 'PhongMaterial PM' or 'FileInputStream FIS';
 */
package ve;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;

import javafx.animation.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import javafx.stage.*;
import ve.environment.*;
import ve.effects.*;
import ve.trackparts.TrackPart;
import ve.utilities.U;
import ve.vehicles.*;
import ve.converter.*;

public class VE extends Application {

 private Scene scene;
 private SubScene scene3D, arrowScene;
 public static final Group group = new Group();
 private static final Group group2 = new Group();
 private final PerspectiveCamera camera = new PerspectiveCamera(true);
 private static Canvas canvas;
 public static GraphicsContext graphicsContext;
 private final Rotate cameraRotateXY = new Rotate();
 public static final Sphere bonusBig = new Sphere(500);
 private static final Sphere collisionBoundSphere = new Sphere();
 public static final PhongMaterial phantomPM = new PhongMaterial();
 private static MeshView arrow = new MeshView();
 private static int vehiclePick;
 private static int trackPoint;
 public static int bonusHolder = -1;
 private static int bonusHolderLAN = -1;
 private static final int maxPlayersLAN = 10;
 public static final int maxPlayers = (int) Math.round(Math.max(maxPlayersLAN, Runtime.getRuntime().maxMemory() * .00000001125));
 public static int vehiclePerspective;
 public static int vehiclesInMatch = 1;
 private static final int[] vehicleNumber = new int[maxPlayers];
 private static int arrowTarget;
 public static int map;
 private static int portLAN = 6666;
 public static int userPlayer;
 public static boolean keyUp;
 public static boolean keyDown;
 public static boolean keyL;
 public static boolean keyR;
 public static boolean keySpace;
 public static final boolean[] keySpecial = new boolean[2];
 public static boolean keyBoost;
 public static boolean keyEnter;
 public static boolean keyEscape;
 private static final boolean[] toUserPerspective = new boolean[2];
 private static final boolean[] restoreZoom = new boolean[2];
 private static final boolean[] lookForward = new boolean[2];
 private static boolean mouse;
 private static boolean mouseClick;
 private static boolean usingKeys;
 public static boolean muteSound;
 public static boolean cursorDriving;
 private static boolean sameVehicles;
 private static boolean showVehicle;
 public static boolean matchStarted;
 public static boolean globalFlick;
 private static boolean showFPS;
 public static boolean headsUpDisplay = true;
 public static boolean normalMapping;
 public static boolean degradedSoundEffects;
 public static boolean messageWait;
 private static boolean creditsDirection;
 public static boolean randomPark;
 public static boolean guardCheckpoint;
 public static boolean fixRingsExist;
 private static boolean cameraFlowFlip;
 private static boolean lastCameraViewNear;
 public static boolean lapCheckpoint;
 private static boolean tournamentOver;
 private static boolean inViewer;
 private static boolean vehicleViewer3DLighting;
 private static boolean showCollisionBounds;
 private static boolean waitingLAN;
 private static boolean hostLeftMatch;
 private static boolean runLANLoadThread;
 private static boolean[] readyLAN = new boolean[maxPlayersLAN];
 private static final boolean[] runLANGameThread = new boolean[maxPlayersLAN];
 private static double mouseX;
 private static double mouseY;
 private static double selectionWait;
 private static double selectionTimer;
 public static double bonusX;
 public static double bonusY;
 public static double bonusZ;
 public static double limitL;
 public static double limitR;
 public static double limitFront;
 public static double limitBack;
 public static double limitY;
 public static double speedLimitAI;
 private static double stuntTimer;
 private static double matchTime;
 public static double printTimer;
 private static double creditsQuantity;
 public static double tick;
 private static double timer;
 public static double width, height;
 public static double defaultVehicleLightBrightness;
 private static double vehicleLightBrightnessChange;
 public static final double pavedRGB = .55;
 public static double cameraX;
 public static double cameraY;
 public static double cameraZ;
 public static double cameraXZ;
 public static double cameraYZ;
 private static double cameraXY;
 public static double viewableMapDistance;
 private static final double[] aroundXZ = new double[2];
 public static double mouseSteerX;
 public static double mouseSteerY;
 private static double trackTimer;
 private static double viewerY;
 private static double viewerZ;
 private static double viewerXZ;
 private static double viewerYZ;
 private static double viewerHeight;
 private static double viewerDepth;
 private static double units = 1;
 private static final double defaultZoom = 75;
 public static double zoom = defaultZoom;
 private static double zoomChange = 1;
 public static double renderLevel;
 private static double musicVolume = .5;
 private static double errorTimer;
 public static final double[] userRandomRGB = {U.random(), U.random(), U.random()};
 private static double gameFPS = Double.POSITIVE_INFINITY;
 private static long tournament;
 private static final long[] tournamentWins = new long[2];
 private static long lookAround;
 private static long section, selected;
 private final double selectionHeight = .03;
 private final double clickRangeY = selectionHeight * .5;
 private final double clickOffset = -.025;
 private final double textOffset = .01;
 public static long lightsAdded;
 private static long arrowType;
 private static long lastArrowType;
 public static long matchLength;
 public static long driverSeat;
 public static long currentCheckpoint;
 private static long mapSelectX;
 private static long mapSelectY;
 private static long mapSelectZ;
 private static long mapSelectRandomRotationDirection;
 public static final int dustQuantity = 96, shotQuantity = 96, explosionQuantity = 12, jetQuantity = 20;
 private static long musicTrackNumber = -1;
 public static final long[] scoreCheckpoint = new long[2];
 public static final long[] scoreLap = new long[2];
 public static final long[] scoreStunt = new long[2];
 public static final double[] scoreDamage = new double[2];
 private static final long[] scoreKill = new long[2];
 private static final long[] finalScore = new long[2];
 private static long lastTime;
 private static long userFPS = Long.MAX_VALUE;
 private static String initialization = "Loading V.E.";
 public static String cameraView = "";
 private static String lastCameraView = "";
 private static String lastCameraViewWithLookAround = "";
 public static String print = "";
 private static String stuntPrint = "";
 private boolean destructionLog;
 public static final String[][] destructionNames = new String[5][2];
 public static final Color[][] destructionNameColors = new Color[5][2];
 public static String vehicleMaker = "";
 public static String mapName = "";
 public static String poolType = "";
 public static String terrain = "";
 private static final String[] unitSign = {"VEs", "VEs"};
 private final DecimalFormat DF = new DecimalFormat("0.#E0");
 private static String error = "";
 private static String joinError = "";
 private static String targetHost = "";
 private static String userName = "";
 private static final String[] vehicleDataLAN = {"", "", "", "", "", "", "", "", "", ""};
 private static final String[] lastVehicleDataLAN = {"", "", "", "", "", "", "", "", "", ""};
 public static final String[] playerNames = new String[maxPlayers];
 public static final Map<String, Sound> sounds = new HashMap<>();
 private MediaPlayer mediaPlayer;
 public static final Map<String, Image> images = new HashMap<>();
 private ServerSocket serverSocket;
 private Socket clientSocket;
 private final List<PrintWriter> outLAN = new ArrayList<>();
 public static final List<BufferedReader> inLAN = new ArrayList<>();
 private final Thread[] gameMatchLAN = new Thread[maxPlayersLAN];
 public static final List<Vehicle> vehicles = new ArrayList<>(maxPlayers);
 public static final List<TrackPart> trackParts = new ArrayList<>();
 private static List<String> vehicleModels;
 private static final List<String> mapModels = Arrays.asList("", "road", "roadshort", "roadturn", "roadbendL", "roadbendR", "roadend", "roadincline", "offroad", "offroadshort", "offroadturn", "offroadbump", "offroadrocky", "offroadend", "offroadincline", "mixroad",
 "checkpoint", "fixring",
 "ramp", "rampcurved", "ramptrapezoid", "ramptriangle", "rampwall", "quarterpipe", "pyramid", "plateau",
 "offplateau", "mound",//<-'mound' is needed!
 "floor", "offfloor", "wall", "cube", "offcube", "spike", "spikes", "block", "blocktower", "border", "beam", "grid", "tunnel", "roadlift", "speedgate", "slowgate", "antigravity",
 "tree0", "tree1", "tree2", "treepalm", "cactus0", "cactus1", "cactus2", "rainbow", "crescent");
 public static final List<String> maps = new ArrayList<>(Arrays.asList("basic", "lapsglory", "checkpoint", "gunpowder", "underover", "antigravity", "versus1", "versus2", "versus3", "trackless", "desert", "3drace", "trip", "racenowhere", "moonlight", "bottleneck", "railing", "twisted", "pit", "falls", "pyramid", "fusion", "darkdivide", "arctic", "scenic", "wintermode", "mountainhop", "damage", "cavern", "southpole", "aerialcontrol", "matrix", "mist", "vansland", "dustdevil", "forest", "zipcross", "tornado", "volcanic", "tsunami", "boulder", "sands", "meteor", "speedway", "endurance", "tunnel", "circle", "circleXL", "circles", "everything", "linear", "maze", "xy", "stairwell", "immense", "showdown", "ocean", "laststand", "parkinglot", "city", "machine", "underwater", "hell", "moon", "mars", "sun", "space1", "space2", "summit", "portal", "blackhole", "doomsday", "+UserMap & TUTORIAL+"));
 public static event event;
 private static event lastEvent;
 public static LAN modeLAN;

 public enum event {
  play, replay, paused, optionsMatch, optionsMenu, mainMenu, credits,
  vehicleSelect, vehicleViewer,
  mapJump, mapLoadPass0, mapLoadPass1, mapLoadPass2, mapLoadPass3, mapLoadPass4, mapError, mapView, mapViewer,
  howToPlay, loadLAN
 }

 public enum mode {
  drive, neutral, stunt, fly, drivePool
 }

 public enum type {vehicle, aircraft, turret}

 public enum LAN {
  OFF, HOST, JOIN
 }

 public enum AIBehavior {adapt, race, fight}

 public static class Point {

  public double X, Y, Z;
  public String type = "";
 }

 public static final List<Point> points = new ArrayList<>();

 public static class Checkpoint {

  public double X, Y, Z;
  public String type = "";
  public int location;
 }

 public static final List<Checkpoint> checkpoints = new ArrayList<>();

 static class BonusBall extends Sphere {

  double X, Y, Z, speedX, speedY, speedZ;
 }

 private static final List<BonusBall> bonusBalls = new ArrayList<>();

 void run(String[] s) {
  launch(s);
 }

 private void loadVE(Stage stage) {
  Thread loadVE = new Thread(() -> {
   try {
    int n;
    scene3D.setFill(Color.color(0, 0, 0));
    arrowScene.setFill(Color.color(0, 0, 0, 0));
    initialization = "Setting Key/Mouse Input";
    setUpMouseKeys();
    initialization = "Setting Recorder";
    Recorder.boot();
    initialization = "Loading Images";
    U.loadImage(images, "RA");
    U.loadImage(images, "white");
    U.loadImage(images, "firelight", 3);
    U.loadImage(images, "blueJet", 3);
    U.loadImage(images, "blink", 3);
    initialization = "Loading Textures";
    U.loadImage(images, "water");
    U.loadImage(images, "rock");
    U.loadImage(images, "metal");
    U.loadImage(images, "brightmetal");
    U.loadImage(images, "grid");
    U.loadImage(images, "paved");
    U.loadImage(images, "wood");
    U.loadImage(images, "foliage");
    U.loadImage(images, "cactus");
    U.loadImage(images, "grass");
    U.loadImage(images, "sand");
    U.loadImage(images, "ground1");
    U.loadImage(images, "ground2");
    initialization = "Loading Normal Maps";
    U.loadImage(images, "rockN");
    U.loadImage(images, "metalN");
    U.loadImage(images, "brightmetalN");
    U.loadImage(images, "pavedN");
    U.loadImage(images, "woodN");
    U.loadImage(images, "foliageN");
    U.loadImage(images, "cactusN");
    U.loadImage(images, "grassN");
    U.loadImage(images, "sandN");
    U.loadImage(images, "ground1N");
    U.loadImage(images, "ground2N");
    initialization = "Loading Settings";
    String s;
    try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream("GameSettings")))) {
     for (String s1; (s1 = BR.readLine()) != null; ) {
      s = "" + s1.trim();
      if (s.startsWith("Units(metric")) {
       units = .5364466667;
       unitSign[0] = "Kph";
       unitSign[1] = "Meters";
      } else if (s.startsWith("Units(U.S.")) {
       units = 1 / 3.;
       unitSign[0] = "Mph";
       unitSign[1] = "Feet";
      }
      normalMapping = s.startsWith("NormalMapping(yes") || normalMapping;
      try {
       userFPS = s.startsWith("fpsLimit(") ? Math.round(U.getValue(s, 0)) : userFPS;
      } catch (Exception ignored) {
      }
      degradedSoundEffects = s.startsWith("degradedSoundEffects(yes") || degradedSoundEffects;
      matchLength = s.startsWith("MatchLength(") ? Math.round(U.getValue(s, 0)) : matchLength;
      driverSeat = s.startsWith("DriverSeat(left") ? -1 : s.startsWith("DriverSeat(right") ? 1 : driverSeat;
      vehiclesInMatch = s.startsWith("#ofPlayers(") ? Math.max(1, Math.min((int) Math.round(U.getValue(s, 0)), maxPlayers)) : vehiclesInMatch;
      headsUpDisplay = s.startsWith("HUD(on") || headsUpDisplay;
      showFPS = s.startsWith("ShowFPS(yes") || showFPS;
      showVehicle = s.startsWith("ShowVehiclesInVehicleSelect(yes") || showVehicle;
      userName = s.startsWith("UserName(") ? U.getString(s, 0) : userName;
      targetHost = s.startsWith("TargetHost(") ? U.getString(s, 0) : targetHost;
      portLAN = s.startsWith("Port(") ? (int) Math.round(U.getValue(s, 0)) : portLAN;
      if (s.startsWith("GameVehicles(")) {
       vehicleModels = new ArrayList<>(Arrays.asList(s.substring("GameVehicles(".length(), s.length() - 1).split(",")));
      } else if (s.startsWith("UserSubmittedVehicles(")) {
       String[] models = s.split("[(,)]");
       for (n = 1; n < models.length; n++) {
        vehicleModels.add(models[n]);
       }
      } else if (s.startsWith("UserSubmittedMaps(")) {
       String[] mapList = s.split("[(,)]");
       for (n = 1; n < mapList.length; n++) {
        maps.add(mapList[n]);
       }
      }
     }
    } catch (IOException e) {
     System.out.println("Error Loading GameSettings: " + e);
    }
    initialization = "Loading Sounds";
    U.loadSound(sounds, "checkpoint");
    U.loadSound(sounds, "stunt");
    U.loadSound(sounds, "bonus");
    U.loadSound(sounds, "rain");
    U.loadSound(sounds, "tornado");
    U.loadSound(sounds, "tsunami");
    U.loadSound(sounds, "volcano");
    U.loadSound(sounds, "sandstorm");
    U.loadSound(sounds, "UI", 2);
    U.loadSound(sounds, "finish", 2);
    initialization = "Preparing Ground";
    E.ground.setRadius(10000000);
    E.ground.setHeight(0);
    E.ground.setMaterial(new PhongMaterial());
    U.setSpecularRGB((PhongMaterial) E.ground.getMaterial(), .5, .5, .5);
    ((PhongMaterial) E.ground.getMaterial()).setSpecularPower(E.groundSpecularPower);
    initialization = "Preparing Lighting";
    U.setLightRGB(E.ambientLight, .5, .5, .5);
    U.setLightRGB(E.sunlight, 1, 1, 1);
    E.sunlight.setTranslateX(0);
    E.sunlight.setTranslateZ(0);
    E.sunlight.setTranslateY(-Long.MAX_VALUE);
    initialization = "Preparing Camera";
    camera.getTransforms().add(cameraRotateXY);
    camera.setFarClip(Double.MAX_VALUE * .125);
    camera.setTranslateX(0);
    camera.setTranslateY(0);
    camera.setTranslateZ(0);
    PerspectiveCamera camera2 = new PerspectiveCamera(true);
    camera2.setFieldOfView(75);
    camera2.setTranslateX(0);
    camera2.setTranslateY(0);
    camera2.setTranslateZ(0);
    initialization = "Preparing 3D Scene";
    scene3D.setCamera(camera);
    arrowScene.setCamera(camera2);
    initialization = "Creating Guidance Arrow";
    TriangleMesh TM = new TriangleMesh();
    TM.getTexCoords().setAll(0, 0);
    float arrowWidth = .25f;
    TM.getPoints().setAll(
    0, 0, arrowWidth * 3,
    0, -arrowWidth, -arrowWidth * 3,
    -arrowWidth, 0, -arrowWidth * 3,
    arrowWidth, 0, -arrowWidth * 3,
    0, arrowWidth, -arrowWidth * 3);
    TM.getFaces().setAll(
    0, 0, 2, 0, 1, 0,
    0, 0, 1, 0, 3, 0,
    0, 0, 3, 0, 4, 0,
    0, 0, 4, 0, 2, 0,
    4, 0, 1, 0, 2, 0,
    4, 0, 3, 0, 1, 0);
    arrow = new MeshView(TM);
    PhongMaterial PM = new PhongMaterial();
    PM.setSpecularColor(Color.color(1, 1, 1));
    arrow.setMaterial(PM);
    arrow.setTranslateX(0);
    arrow.setTranslateY(-5);
    arrow.setTranslateZ(10);
    initialization = "Creating Storm Cloud";
    E.stormCloud.setScaleY(.1);
    E.stormCloud.setCullFace(CullFace.NONE);
    initialization = "Creating Lightning";
    TriangleMesh lightningTM = new TriangleMesh();
    lightningTM.getTexCoords().setAll(0, 0);
    lightningTM.getFaces().setAll(
    0, 0, 1, 0, 2, 0,
    1, 0, 2, 0, 3, 0,
    2, 0, 3, 0, 4, 0,
    3, 0, 4, 0, 5, 0);
    E.lightningMesh.setMesh(lightningTM);
    E.lightningMesh.setCullFace(CullFace.NONE);
    PhongMaterial lightningPM = new PhongMaterial();
    lightningPM.setSelfIlluminationMap(U.getImage("white"));
    E.lightningMesh.setMaterial(lightningPM);
    for (n = 2; --n >= 0; ) {
     E.lightningLight[n] = new PointLight();
     U.setLightRGB(E.lightningLight[n], 1, 1, 1);
    }
    initialization = "Creating Liquid Pool";
    E.pool[0] = new Cylinder();
    E.pool[1] = new Cylinder();
    E.pool[0].setHeight(0);
    E.pool[0].setMaterial(E.poolPM);
    E.pool[1].setMaterial(E.poolPM);
    E.pool[1].setCullFace(CullFace.FRONT);
    initialization = "Creating Bonus";
    bonusBig.setMaterial(new PhongMaterial());
    for (n = 0; n < 64; n++) {
     bonusBalls.add(new BonusBall());
     bonusBalls.get(n).setMaterial(new PhongMaterial());
    }
    initialization = "Creating Volcano";
    TM = new TriangleMesh();
    TM.getPoints().setAll(0, 0, (float) E.volcanoBottomRadius,
    9203.3534163473084891409812187737f, 0, 52194.810909647027146437380303245f,
    18127.06759626044285133727957816f, 0, 49803.708901653144354867791698211f,
    26500, 0, 45899.346400575248278477328049906f,
    34067.743313386584295100100725085f, 0, 40600.355485305835865726810479437f,
    40600.355485305835865726810479437f, 0, 34067.743313386584295100100725085f,
    45899.346400575248278477328049906f, 0, 26500,
    49803.708901653144354867791698211f, 0, 18127.06759626044285133727957816f,
    52194.810909647027146437380303245f, 0, 9203.3534163473084891409812187737f,
    (float) E.volcanoBottomRadius, 0, 0,
    52194.810909647027146437380303245f, 0, -9203.3534163473084891409812187737f,
    49803.708901653144354867791698211f, 0, -18127.06759626044285133727957816f,
    45899.346400575248278477328049906f, 0, -26500,
    40600.355485305835865726810479437f, 0, -34067.743313386584295100100725085f,
    34067.743313386584295100100725085f, 0, -40600.355485305835865726810479437f,
    26500, 0, -45899.346400575248278477328049906f,
    18127.06759626044285133727957816f, 0, -49803.708901653144354867791698211f,
    9203.3534163473084891409812187737f, 0, -52194.810909647027146437380303245f,
    0, 0, -(float) E.volcanoBottomRadius,
    -9203.3534163473084891409812187737f, 0, -52194.810909647027146437380303245f,
    -18127.06759626044285133727957816f, 0, -49803.708901653144354867791698211f,
    -26500, 0, -45899.346400575248278477328049906f,
    -34067.743313386584295100100725085f, 0, -40600.355485305835865726810479437f,
    -40600.355485305835865726810479437f, 0, -34067.743313386584295100100725085f,
    -45899.346400575248278477328049906f, 0, -26500,
    -49803.708901653144354867791698211f, 0, -18127.06759626044285133727957816f,
    -52194.810909647027146437380303245f, 0, -9203.3534163473084891409812187737f,
    -(float) E.volcanoBottomRadius, 0, 0,
    -52194.810909647027146437380303245f, 0, 9203.3534163473084891409812187737f,
    -49803.708901653144354867791698211f, 0, 18127.06759626044285133727957816f,
    -45899.346400575248278477328049906f, 0, 26500,
    -40600.355485305835865726810479437f, 0, 34067.743313386584295100100725085f,
    -34067.743313386584295100100725085f, 0, 40600.355485305835865726810479437f,
    -26500, 0, 45899.346400575248278477328049906f,
    -18127.06759626044285133727957816f, 0, 49803.708901653144354867791698211f,
    -9203.3534163473084891409812187737f, 0, 52194.810909647027146437380303245f,
    0, -(float) E.volcanoHeight, (float) E.volcanoTopRadius,
    520.94453300079104655514988030794f, -(float) E.volcanoHeight, 2954.4232590366241781002290737686f,
    1026.0604299770061991322988440468f, -(float) E.volcanoHeight, 2819.0778623577251521623278319742f,
    1500, -(float) E.volcanoHeight, 2598.0762113533159402911695122588f,
    1928.3628290596179789679302297218f, -(float) E.volcanoHeight, 2298.1333293569341056071779516663f,
    2298.1333293569341056071779516663f, -(float) E.volcanoHeight, 1928.3628290596179789679302297218f,
    2598.0762113533159402911695122588f, -(float) E.volcanoHeight, 1500,
    2819.0778623577251521623278319742f, -(float) E.volcanoHeight, 1026.0604299770061991322988440468f,
    2954.4232590366241781002290737686f, -(float) E.volcanoHeight, 520.94453300079104655514988030794f,
    (float) E.volcanoTopRadius, -(float) E.volcanoHeight, 0,
    2954.4232590366241781002290737686f, -(float) E.volcanoHeight, -520.94453300079104655514988030794f,
    2819.0778623577251521623278319742f, -(float) E.volcanoHeight, -1026.0604299770061991322988440468f,
    2598.0762113533159402911695122588f, -(float) E.volcanoHeight, -1500,
    2298.1333293569341056071779516663f, -(float) E.volcanoHeight, -1928.3628290596179789679302297218f,
    1928.3628290596179789679302297218f, -(float) E.volcanoHeight, -2298.1333293569341056071779516663f,
    1500, -(float) E.volcanoHeight, -2598.0762113533159402911695122588f,
    1026.0604299770061991322988440468f, -(float) E.volcanoHeight, -2819.0778623577251521623278319742f,
    520.94453300079104655514988030794f, -(float) E.volcanoHeight, -2954.4232590366241781002290737686f,
    0, -(float) E.volcanoHeight, -(float) E.volcanoTopRadius,
    -520.94453300079104655514988030794f, -(float) E.volcanoHeight, -2954.4232590366241781002290737686f,
    -1026.0604299770061991322988440468f, -(float) E.volcanoHeight, -2819.0778623577251521623278319742f,
    -1500, -(float) E.volcanoHeight, -2598.0762113533159402911695122588f,
    -1928.3628290596179789679302297218f, -(float) E.volcanoHeight, -2298.1333293569341056071779516663f,
    -2298.1333293569341056071779516663f, -(float) E.volcanoHeight, -1928.3628290596179789679302297218f,
    -2598.0762113533159402911695122588f, -(float) E.volcanoHeight, -1500,
    -2819.0778623577251521623278319742f, -(float) E.volcanoHeight, -1026.0604299770061991322988440468f,
    -2954.4232590366241781002290737686f, -(float) E.volcanoHeight, -520.94453300079104655514988030794f,
    -(float) E.volcanoTopRadius, -(float) E.volcanoHeight, 0,
    -2954.4232590366241781002290737686f, -(float) E.volcanoHeight, 520.94453300079104655514988030794f,
    -2819.0778623577251521623278319742f, -(float) E.volcanoHeight, 1026.0604299770061991322988440468f,
    -2598.0762113533159402911695122588f, -(float) E.volcanoHeight, 1500,
    -2298.1333293569341056071779516663f, -(float) E.volcanoHeight, 1928.3628290596179789679302297218f,
    -1928.3628290596179789679302297218f, -(float) E.volcanoHeight, 2298.1333293569341056071779516663f,
    -1500, -(float) E.volcanoHeight, 2598.0762113533159402911695122588f,
    -1026.0604299770061991322988440468f, -(float) E.volcanoHeight, 2819.0778623577251521623278319742f,
    -520.94453300079104655514988030794f, -(float) E.volcanoHeight, 2954.4232590366241781002290737686f);
    if (U.random() < .5) {
     TM.getTexCoords().setAll(0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0);
    } else {
     TM.getTexCoords().setAll(0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1);
    }
    TM.getFaces().addAll(
    0, 0, 1, 1, 37, 37, 1, 1, 2, 2, 38, 38, 2, 2, 3, 3, 39, 39, 3, 3, 4, 4, 40, 40, 4, 4, 5, 5, 41, 41, 5, 5, 6, 6, 42, 42, 6, 6, 7, 7, 43, 43, 7, 7, 8, 8, 44, 44, 8, 8, 9, 9, 45, 45, 9, 9, 10, 10, 46, 46, 10, 10, 11, 11, 47, 47, 11, 11, 12, 12, 48, 48, 12, 12, 13, 13, 49, 49, 13, 13, 14, 14, 50, 50, 14, 14, 15, 15, 51, 51, 15, 15, 16, 16, 52, 52, 16, 16, 17, 17, 53, 53, 17, 17, 18, 18, 54, 54, 18, 18, 19, 19, 55, 55, 19, 19, 20, 20, 56, 56, 20, 20, 21, 21, 57, 57, 21, 21, 22, 22, 58, 58, 22, 22, 23, 23, 59, 59, 23, 23, 24, 24, 60, 60, 24, 24, 25, 25, 61, 61, 25, 25, 26, 26, 62, 62, 26, 26, 27, 27, 63, 63, 27, 27, 28, 28, 64, 64, 28, 28, 29, 29, 65, 65, 29, 29, 30, 30, 66, 66, 30, 30, 31, 31, 67, 67, 31, 31, 32, 32, 68, 68, 32, 32, 33, 33, 69, 69, 33, 33, 34, 34, 70, 70, 34, 34, 35, 35, 71, 71, 35, 35, 0, 0, 36, 36,
    0, 0, 36, 36, 37, 37, 1, 1, 37, 37, 38, 38, 2, 2, 38, 38, 39, 39, 3, 3, 39, 39, 40, 40, 4, 4, 40, 40, 41, 41, 5, 5, 41, 41, 42, 42, 6, 6, 42, 42, 43, 43, 7, 7, 43, 43, 44, 44, 8, 8, 44, 44, 45, 45, 9, 9, 45, 45, 46, 46, 10, 10, 46, 46, 47, 47, 11, 11, 47, 47, 48, 48, 12, 12, 48, 48, 49, 49, 13, 13, 49, 49, 50, 50, 14, 14, 50, 50, 51, 51, 15, 15, 51, 51, 52, 52, 16, 16, 52, 52, 53, 53, 17, 17, 53, 53, 54, 54, 18, 18, 54, 54, 55, 55, 19, 19, 55, 55, 56, 56, 20, 20, 56, 56, 57, 57, 21, 21, 57, 57, 58, 58, 22, 22, 58, 58, 59, 59, 23, 23, 59, 59, 60, 60, 24, 24, 60, 60, 61, 61, 25, 25, 61, 61, 62, 62, 26, 26, 62, 62, 63, 63, 27, 27, 63, 63, 64, 64, 28, 28, 64, 64, 65, 65, 29, 29, 65, 65, 66, 66, 30, 30, 66, 66, 67, 67, 31, 31, 67, 67, 68, 68, 32, 32, 68, 68, 69, 69, 33, 33, 69, 69, 70, 70, 34, 34, 70, 70, 71, 71, 35, 35, 71, 71, 36, 36);
    E.volcanoMesh = new MeshView(TM);
    E.volcanoMesh.setCullFace(CullFace.NONE);
    initialization = "Loading the rest";
    aroundXZ[0] = U.randomPlusMinus(180.);
    U.setDiffuseRGB(phantomPM, 1, 1, 1, .1);
    U.setSpecularRGB(phantomPM, 0, 0, 0);
    E.cloudPM.setSpecularPower(4);
    E.starPM.setSelfIlluminationMap(U.getImage("white"));
    PhongMaterial boundSpherePM = new PhongMaterial();
    collisionBoundSphere.setMaterial(boundSpherePM);
    U.setDiffuseRGB(boundSpherePM, 1, 1, 1, .5);
    U.setSpecularRGB(boundSpherePM, 0, 0, 0);
    U.setLightRGB(E.mapViewerLight, 1, 1, 1);
    stage.setOnCloseRequest((WindowEvent WE) -> {
     for (PrintWriter PW : outLAN) {
      PW.println("END");
      PW.println("END");
      PW.println("CANCEL");
      PW.println("CANCEL");
     }
    });
    initialization = "";
    U.soundPlay(sounds, "stunt", 0);
   } catch (Exception E) {
    System.out.println("Exception in secondary load thread:" + E);
   }
  });
  loadVE.setDaemon(true);
  loadVE.start();
 }

 public void start(Stage stage) {
  Thread.currentThread().setPriority(10);
  stage.setTitle("The Vehicular Epic");
  try {
   stage.getIcons().add(new Image(new FileInputStream("images" + File.separator + "icon.png")));
  } catch (FileNotFoundException ignored) {
  }
  System.setProperty("sun.java2d.opengl", "true");
  double windowSize = 1;
  boolean antiAliasing = false;
  String s;
  try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream("GameSettings")))) {
   for (String s1; (s1 = BR.readLine()) != null; ) {
    s = "" + s1.trim();
    antiAliasing = s.startsWith("AntiAliasing(yes") || antiAliasing;
    windowSize = s.startsWith("WindowSize(") ? U.getValue(s, 0) : windowSize;
   }
  } catch (IOException e) {
   System.out.println("Error Loading Settings: " + e);
  }
  stage.setWidth(U.dimension.getWidth() * windowSize);
  stage.setHeight(U.dimension.getHeight() * windowSize);
  width = stage.getWidth();
  height = stage.getHeight();
  scene3D = new SubScene(group, width, height, true, antiAliasing ? SceneAntialiasing.BALANCED : SceneAntialiasing.DISABLED);
  arrowScene = new SubScene(group2, width, height, false, antiAliasing ? SceneAntialiasing.BALANCED : SceneAntialiasing.DISABLED);
  canvas = new Canvas(width, height);
  graphicsContext = canvas.getGraphicsContext2D();
  scene = new Scene(new StackPane(scene3D, arrowScene, canvas), width, height, false, SceneAntialiasing.DISABLED);
  stage.setScene(scene);
  U.add(E.ambientLight, E.sunlight);
  event = event.mainMenu;
  modeLAN = LAN.OFF;
  new AnimationTimer() {
   public void handle(long L) {
    try {
     int n;
     graphicsContext.clearRect(0, 0, width, height);
     renderLevel = U.clamp(10000, renderLevel * (U.FPS < 30 ? .75 : 1.05), 40000);
     renderLevel = U.averageFPS >= 60 ? Double.POSITIVE_INFINITY : renderLevel;
     zoom = Math.min(zoom * zoomChange, 170);
     zoom = restoreZoom[0] && restoreZoom[1] ? defaultZoom : zoom;
     camera.setFieldOfView(zoom);
     if (userPlayer < vehicles.size() && vehicles.get(userPlayer) != null) {
      vehicles.get(userPlayer).lightBrightness = U.clamp(vehicles.get(userPlayer).lightBrightness + vehicleLightBrightnessChange);
     }
     lightsAdded = 0;
     if (mouseClick) {
      mouse = keyL = keyR = keyEnter = false;
     }
     boolean gamePlay = event == event.play || event == event.replay;
     if (mouse && (!gamePlay || !matchStarted)) {
      if (mouseX < .375) {
       keyL = true;
      } else if (mouseX > .625) {
       keyR = true;
      } else {
       keyEnter = mouseClick = true;
      }
      mouseClick = event != event.vehicleSelect && event != event.mapJump && !event.name().contains("options") || mouseClick;
     }
     selectionTimer += tick;
     if (width != stage.getWidth() || height != stage.getHeight()) {
      width = stage.getWidth();
      height = stage.getHeight();
      scene3D.setWidth(width);
      scene3D.setHeight(height);
      arrowScene.setWidth(width);
      arrowScene.setHeight(height);
      canvas.setWidth(width);
      canvas.setHeight(height);
     }
     if (vehiclePerspective < vehicles.size() && vehicles.get(vehiclePerspective) != null && vehicles.get(vehiclePerspective).inPool && cameraY > 0) {
      if (poolType.equals("lava")) {
       U.fillRGB(1, .5 + U.random(.25), 0, .75);
      } else if (poolType.equals("acid")) {
       U.fillRGB(.25, .5, .25, .5);
      } else {
       U.fillRGB(0, 0, defaultVehicleLightBrightness > 0 ? 0 : .5, .5);
      }
      U.fillRectangle(.5, .5, 1, 1);
     }
     if (gamePlay || event == event.paused || event == event.optionsMatch) {
      for (Vehicle vehicle : vehicles) {
       vehicle.miscellaneous();
      }
      if (matchStarted) {
       if (gamePlay) {
        if (cursorDriving) {
         mouseSteerX = 100 * (.5 - mouseX);
         mouseSteerY = 100 * (mouseY - .5);
         if (vehicles.get(userPlayer).mode != mode.fly && vehicles.get(userPlayer).vehicleType != type.turret) {
          if (mouseY < .5) {
           keyDown = false;
           keyUp = true;
          } else if (mouseY > .75) {
           keyUp = false;
           keyDown = true;
          } else {
           keyUp = keyDown = false;
          }
         }
         keySpace = mouse;
        }
       }
       if (modeLAN != LAN.OFF) {
        matchDataOutLAN();
       }
       if (gamePlay) {
        for (Vehicle vehicle : vehicles) {
         if (vehicle.vehicleType == type.turret) {
          vehicle.physicsTurret();
         } else {
          vehicle.physicsVehicle();
         }
         Recorder.record(vehicle);
        }
        for (Vehicle vehicle : vehicles) {
         vehicle.manageCollisions();
        }
        for (Vehicle vehicle : vehicles) {
         vehicle.damage = Math.min(vehicle.damage, vehicle.durability * 1.004);
         if (vehicle.destroyed && vehicle.vehicleHit > -1) {
          scoreKill[vehicle.index < vehiclesInMatch >> 1 ? 1 : 0] += event == event.replay ? 0 : 1;
          vehicle.AI.target = U.random(vehiclesInMatch);
          vehicle.vehicleHit = -1;
         }
        }
        if (event == event.play) {
         Recorder.recordBonusHolder();
         if (modeLAN == LAN.OFF) {
          for (Vehicle vehicle : vehicles) {
           if (vehicle.index > 0)
            vehicle.AI.run();
          }
         }
        }
       }
       Recorder.updateFrame();
      } else {
       for (Vehicle vehicle : vehicles) {
        vehicle.setTurretY();
       }
       preMatchCommunicationLAN();
       cursorDriving = false;
       if (waitingLAN) {
        U.font(.02);
        double color = globalFlick ? 0 : 1;
        U.fillRGB(color, color, color);
        if (vehiclesInMatch < 3) {
         U.text("..Waiting on " + playerNames[modeLAN == LAN.HOST ? 1 : 0] + "..", .5, .25);
        } else {
         U.text("..Waiting for all Players to Start..", .5, .25);
        }
        long whoIsReady = 0;
        for (n = vehiclesInMatch; --n >= 0; ) {
         whoIsReady += readyLAN[n] ? 1 : 0;
        }
        if (whoIsReady >= vehiclesInMatch) {
         if (modeLAN == LAN.HOST) {
          for (n = vehiclesInMatch; --n > 0; ) {
           gamePlayLAN(n);
          }
         } else {
          gamePlayLAN(0);
         }
         matchStarted = true;
         waitingLAN = false;
        }
       } else if (keySpace) {
        U.soundPlay(sounds, "UI1", 0);
        cameraView = lastCameraView;
        if (modeLAN != LAN.OFF) {
         readyLAN[userPlayer] = waitingLAN = true;
         if (modeLAN == LAN.HOST) {
          for (PrintWriter PW : outLAN) {
           PW.println("Ready0");
           PW.println("Ready0");
          }
         } else {
          outLAN.get(0).println("Ready");
          outLAN.get(0).println("Ready");
         }
        } else {
         matchStarted = true;
        }
        for (Vehicle vehicle : vehicles) {
         vehicle.turretDefaultY = vehicle.Y + vehicle.turretBaseY;
        }
        keySpace = false;
       }
       if (!waitingLAN) {
        U.font(.02);
        double color = globalFlick ? 0 : 1;
        U.fillRGB(color, color, color);
        if (vehicles.get(vehiclePerspective).vehicleType == type.turret && (vehiclesInMatch < 2 || vehiclePerspective < vehiclesInMatch >> 1)) {
         U.text("Use Arrow Keys to place your turret(s), then", .2);
         vehicles.get(vehiclePerspective).Z += keyUp ? 200 * tick : 0;
         vehicles.get(vehiclePerspective).Z -= keyDown ? 200 * tick : 0;
         vehicles.get(vehiclePerspective).X -= keyL ? 200 * tick : 0;
         vehicles.get(vehiclePerspective).X += keyR ? 200 * tick : 0;
        }
        U.text("Press SPACE to Begin" + (tournament > 0 ? " Round " + tournament : ""), .25);
       }
       if (keyEscape) {
        escapeToLast(true);
       }
      }
      Recorder.playBack();
      aroundXZ[1] = lookForward[0] && lookForward[1] ? 0 : aroundXZ[1];
      manageCamera(vehicles.get(vehiclePerspective));
      if (Math.abs(aroundXZ[1]) > 180) {
       aroundXZ[1] += aroundXZ[1] < -180 ? 360 : -360;
      }
      U.rotate(camera, cameraYZ, -cameraXZ);
      cameraRotateXY.setAngle(-cameraXY);
      manageEnvironment();
      for (Vehicle vehicle : vehicles) {
       for (VehiclePiece piece : vehicle.pieces) {
        U.remove(piece.pointLight);
       }
      }//Potential MAJOR sources of lag
      if (VE.defaultVehicleLightBrightness > 0) {
       for (Vehicle vehicle : vehicles) {
        U.remove(vehicle.burnLight);
       }
      }
      if (vehiclesInMatch < 2) {
       vehicles.get(vehiclePerspective).processGraphics(gamePlay);
      } else {
       int closest = vehiclePerspective;
       double compareDistance = Double.POSITIVE_INFINITY;
       for (Vehicle vehicle : vehicles) {
        if (vehicle.index != vehiclePerspective && U.distance(vehicles.get(vehiclePerspective).X, vehicle.X, vehicles.get(vehiclePerspective).Z, vehicle.Z, vehicles.get(vehiclePerspective).Y, vehicle.Y) < compareDistance) {
         closest = vehicle.index;
         compareDistance = U.distance(vehicles.get(vehiclePerspective).X, vehicle.X, vehicles.get(vehiclePerspective).Z, vehicle.Z, vehicles.get(vehiclePerspective).Y, vehicle.Y);
        }
       }
       if (vehicles.get(vehiclePerspective).lightBrightness >= vehicles.get(closest).lightBrightness) {
        vehicles.get(vehiclePerspective).processGraphics(gamePlay);
        vehicles.get(closest).processGraphics(gamePlay);
       } else {
        vehicles.get(closest).processGraphics(gamePlay);
        vehicles.get(vehiclePerspective).processGraphics(gamePlay);
       }
       for (Vehicle vehicle : vehicles) {
        if (vehicle.index != vehiclePerspective && vehicle.index != closest) {
         vehicle.processGraphics(gamePlay);
        }
       }
      }
      for (TrackPart trackPart : trackParts) {
       trackPart.processGraphics();
      }
      manageBonus();
      if (matchStarted) {
       for (Vehicle vehicle : vehicles) {
        for (Explosion explosion : vehicle.explosions) {
         explosion.run(gamePlay);
        }
        for (Special special : vehicle.specials) {
         if (!U.startsWith(special.type, "particledisintegrator", "phantom", "teleport")) {
          vehicle.manageSpecial(special, gamePlay);
          vehicle.spit(special, gamePlay);
         }
        }
        if (vehicle.screenFlash > 0) {
         U.fillRGB(1, 1, 1, vehicle.screenFlash);
         U.fillRectangle(.5, .5, 1, 1);
        }
       }
      }
      manageMatch();
      vehiclePerspective = toUserPerspective[0] && toUserPerspective[1] ? userPlayer : vehiclePerspective;
      gameFPS = Double.POSITIVE_INFINITY;
     }
     if (event == event.paused) {
      paused();
     } else if (event == event.optionsMatch || event == event.optionsMenu) {
      options();
      gameFPS = Double.POSITIVE_INFINITY;
     } else if (event == event.vehicleViewer) {
      vehicleViewer(gamePlay);
      gameFPS = Double.POSITIVE_INFINITY;
     } else if (event == event.mapViewer) {
      mapViewer();
      gameFPS = Double.POSITIVE_INFINITY;
     } else if (event == event.credits) {
      credits();
      gameFPS = Double.POSITIVE_INFINITY;
     } else if (event == event.mainMenu) {
      mainMenu();
     } else if (event == event.howToPlay) {
      howToPlay();
     } else if (event == event.vehicleSelect) {
      U.rotate(camera, cameraYZ, -cameraXZ);
      gameFPS = Double.POSITIVE_INFINITY;
      vehicleSelect(gamePlay);
      for (Raindrop raindrop : E.raindrops) {
       raindrop.run();
      }
      for (Snowball snowball : E.snowballs) {
       snowball.run();
      }
     } else if (event == event.loadLAN) {
      setUpLANGame();
     } else if (event == event.mapError) {
      mapError();
     } else if (event == event.mapJump) {
      mapJump();
     } else if (event == event.mapView) {
      U.rotate(camera, cameraYZ, -cameraXZ);
      manageEnvironment();
      cameraAroundTrack();
      for (Vehicle vehicle : vehicles) {
       vehicle.processGraphics(gamePlay);
      }
      for (TrackPart trackPart : trackParts) {
       trackPart.processGraphics();
      }
      mapView();
      manageBonus();
      gameFPS = Double.POSITIVE_INFINITY;
     } else if (event == event.mapLoadPass0 || event == event.mapLoadPass1 || event == event.mapLoadPass2 || event == event.mapLoadPass3 || event == event.mapLoadPass4) {
      mapLoad();
      falsify();
     }
     globalFlick = !globalFlick;
     timer = (timer += tick) > 20 ? 0 : timer;
     if (event != event.vehicleSelect) {
      for (Vehicle vehicle : vehicles) {
       if (vehicle != null && !vehicle.destroyed) {
        vehicle.flicker = !vehicle.flicker;
       }
      }
     }
     selectionTimer = (selectionTimer > selectionWait ? 0 : selectionTimer) + 5 * tick;
     if (keyL || keyR || keyUp || keyDown || keySpace || keyEnter) {
      if (selectionWait == -1) {
       selectionWait = 30;
       selectionTimer = 0;
      }
      selectionWait -= selectionWait > 0 ? tick : 0;
     } else {
      selectionWait = -1;
      selectionTimer = 0;
     }
     double targetFPS = Math.min(gameFPS, userFPS), dividedFPS = 1000 / targetFPS;
     if (targetFPS < U.refreshRate) {
      double difference = System.currentTimeMillis() - U.FPSTime;
      if (difference < dividedFPS) {
       U.zZz(dividedFPS - difference);
      }
     }
     U.getFPS();
     if (showFPS) {
      U.font(.02);
      U.fillRGB(1, 1, 1);
      U.text(Math.round(U.averageFPS) + "", .975, .935);
      U.text("FPS", .975, .965);
     }
     long time = System.nanoTime();
     tick = Math.min((time - lastTime + 500000) * .00000002, 1);
     lastTime = time;
    } catch (Exception E) {
     try (PrintWriter PW = new PrintWriter(new File("V.E. EXCEPTION"))) {
      E.printStackTrace(PW);
     } catch (IOException ignored) {
     }
     E.printStackTrace();
     handleException();
    }
   }
  }.start();
  loadVE(stage);
  stage.show();
 }

 private void handleException() {
  error = "An Exception Occurred!\nA File with the exception has been saved to the game folder";
  event = event.mainMenu;
  tournament = selected = 0;
  clearGraphics();
  clearSounds();
  falsify();
  if (modeLAN == LAN.HOST) {
   for (PrintWriter PW : outLAN) {
    PW.println("CANCEL");
    PW.println("CANCEL");
   }
  } else if (modeLAN == LAN.JOIN) {
   outLAN.get(0).println("CANCEL");
   outLAN.get(0).println("CANCEL");
  }
 }

 private void mapLoad() {
  double instanceSize = 1;
  double[] instanceScale = {1, 1, 1};
  double randomX = 0, randomY = 0, randomZ = 0;
  if (event == event.mapLoadPass0) {
   scene.setCursor(Cursor.WAIT);
  } else if (event == event.mapLoadPass1) {
   clearGraphics();
   U.remove(E.sunlight, E.sun, E.ground);
   addArrow();
   vehicles.clear();
   trackParts.clear();
   E.groundPlates.clear();
   E.clouds.clear();
   E.mountains.clear();
   E.stars.clear();
   E.raindrops.clear();
   E.snowballs.clear();
   E.tornadoParts.clear();
   E.tsunamiParts.clear();
   E.fires.clear();
   E.boulders.clear();
   E.volcanoRocks.clear();
   E.meteors.clear();
   points.clear();
   checkpoints.clear();
   terrain = " ground ";
   E.skyInverse = Color.color(1, 1, 1);
   E.groundInverse = Color.color(1, 1, 1);
   camera.setFarClip(Double.MAX_VALUE * .125);
   E.sunX = E.sunY = E.sunZ
   = viewableMapDistance = E.wind = defaultVehicleLightBrightness
   = E.skyRGB[0] = E.skyRGB[1] = E.skyRGB[2]
   = E.groundRGB[0] = E.groundRGB[1] = E.groundRGB[2] = E.terrainRGB[0] = E.terrainRGB[1] = E.terrainRGB[2] = E.groundLevel
   = E.poolDepth = 0;
   E.sandstormExists = randomPark = guardCheckpoint = fixRingsExist = E.poolExists = E.tornadoMovesFixpoints = false;
   E.gravity = 7;
   U.setLightRGB(E.sunlight, 1, 1, 1);
   U.setLightRGB(E.ambientLight, 0, 0, 0);
   scene3D.setFill(Color.color(0, 0, 0));
   U.setDiffuseRGB((PhongMaterial) E.ground.getMaterial(), 0, 0, 0);
   ((PhongMaterial) E.ground.getMaterial()).setSpecularMap(null);
   E.lightningExists = E.volcanoExists = false;
   limitL = limitBack = limitY = Double.NEGATIVE_INFINITY;
   limitR = limitFront = Double.POSITIVE_INFINITY;
   speedLimitAI = Long.MAX_VALUE;
   poolType = "";
  }
  int n;
  String s = "";
  try (BufferedReader BR = new BufferedReader(new InputStreamReader(U.getMapFile(map)))) {
   for (String s1; (s1 = BR.readLine()) != null; ) {
    s = "" + s1.trim();
    if (event == event.mapLoadPass2) {
     mapName = s.startsWith("name") ? U.getString(s, 0) : mapName;
     if (s.startsWith("ambientLight")) {
      U.setLightRGB(E.ambientLight, U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
     } else if (s.startsWith("sky")) {
      E.skyRGB[0] = U.getValue(s, 0);
      E.skyRGB[1] = U.getValue(s, 1);
      E.skyRGB[2] = U.getValue(s, 2);
      E.skyInverse = Color.color(U.clamp(-E.skyRGB[0] + 1), U.clamp(-E.skyRGB[1] + 1), U.clamp(-E.skyRGB[2] + 1));
      double r = E.skyRGB[0], g = E.skyRGB[1], b = E.skyRGB[2];
      if (r > 0 || g > 0 || b > 0) {
       while (r < 1 && g < 1 && b < 1) {
        r *= 1.001;
        g *= 1.001;
        b *= 1.001;
       }
      }
      U.setLightRGB(E.sunlight, r, g, b);
     } else if (s.startsWith("ground")) {
      E.groundRGB[0] = U.getValue(s, 0);
      E.groundRGB[1] = U.getValue(s, 1);
      E.groundRGB[2] = U.getValue(s, 2);
      U.setDiffuseRGB((PhongMaterial) E.ground.getMaterial(), E.groundRGB[0], E.groundRGB[1], E.groundRGB[2]);
      E.terrainRGB[0] = U.getValue(s, 0);
      E.terrainRGB[1] = U.getValue(s, 1);
      E.terrainRGB[2] = U.getValue(s, 2);
      if (!mapName.equals("Phantom Cavern")) {
       U.add(E.ground);
      }
      E.groundInverse = Color.color(U.clamp(-E.groundRGB[0] + 1), U.clamp(-E.groundRGB[1] + 1), U.clamp(-E.groundRGB[2] + 1));
     } else if (s.startsWith("terrain")) {
      terrain = " " + U.getString(s, 0) + " ";
      ((PhongMaterial) E.ground.getMaterial()).setSpecularMap(U.getImage(terrain.trim()));
      if (!U.getString(s, 0).isEmpty() && (E.terrainRGB[0] > 0 || E.terrainRGB[1] > 0 || E.terrainRGB[2] > 0)) {
       for (n = terrain.contains(" rock ") ? Integer.MAX_VALUE : 4000; --n >= 0; ) {
        if (E.terrainRGB[0] < 1 && E.terrainRGB[1] < 1 && E.terrainRGB[2] < 1) {
         E.terrainRGB[0] *= 1.0001;
         E.terrainRGB[1] *= 1.0001;
         E.terrainRGB[2] *= 1.0001;
        } else {
         break;
        }
       }
      }
      setGroundPlates(terrain.trim());
     } else if (s.startsWith("view") && U.getValue(s, 0) > 0) {
      viewableMapDistance = U.getValue(s, 0);
      camera.setFarClip(Math.min(U.getValue(s, 0), Double.MAX_VALUE * .125));
     }
     E.gravity = s.startsWith("gravity") ? U.getValue(s, 0) : E.gravity;
     if (s.startsWith("pointLight")) {
      E.sunlightX = U.getValue(s, 0);
      E.sunlightY = U.getValue(s, 2);
      E.sunlightZ = U.getValue(s, 1);
      U.add(E.sunlight);
      E.sunX = U.getValue(s, 0) * 2;
      E.sunY = U.getValue(s, 2) * 2;
      E.sunZ = U.getValue(s, 1) * 2;
      PhongMaterial PM = new PhongMaterial();
      U.setDiffuseRGB(PM, 1, 1, 1);
      U.setSpecularRGB(PM, 1, 1, 1);
      PM.setSpecularPower(0);
      U.getRGB.setFill(Color.color(U.clamp(E.skyRGB[0]), U.clamp(E.skyRGB[1]), U.clamp(E.skyRGB[2])));
      U.getRGB.fillRect(0, 0, 1, 1);
      PM.setSelfIlluminationMap(U.getRGBCanvas.snapshot(null, null));
      E.sun.setMaterial(PM);
      U.add(E.sun);
     }
     defaultVehicleLightBrightness = s.startsWith("defaultBrightness") ? U.getValue(s, 0) : defaultVehicleLightBrightness;
     randomPark = s.startsWith("randomPark") || randomPark;
     limitL = s.startsWith("xLimitLeft") ? U.getValue(s, 0) : limitL;
     limitR = s.startsWith("xLimitRight") ? U.getValue(s, 0) : limitR;
     limitFront = s.startsWith("zLimitFront") ? U.getValue(s, 0) : limitFront;
     limitBack = s.startsWith("zLimitBack") ? U.getValue(s, 0) : limitBack;
     limitY = s.startsWith("yLimit") ? U.getValue(s, 0) : limitY;
     speedLimitAI = s.startsWith("speedLimit") ? U.getValue(s, 0) : speedLimitAI;
     E.groundLevel = s.startsWith("noGround") ? Double.POSITIVE_INFINITY : E.groundLevel;
     guardCheckpoint = s.startsWith("guardCheckpoint") || guardCheckpoint;
     E.wind = s.startsWith("wind") ? U.getValue(s, 0) : E.wind;
     if (s.startsWith("snow")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       E.snowballs.add(new Snowball());
      }
     } else if (s.startsWith("sandstorm")) {
      E.sandstormExists = true;
      E.windX = E.wind * U.randomPlusMinus(20.);
      E.windZ = E.wind * U.randomPlusMinus(20.);
     } else if (s.startsWith("clouds")) {
      E.cloudProperties[0] = U.getValue(s, 0);
      E.cloudProperties[1] = U.getValue(s, 1);
      E.cloudProperties[2] = U.getValue(s, 2);
      E.cloudProperties[3] = U.getValue(s, 3);
      setClouds();
     } else if (s.startsWith("storm")) {
      PhongMaterial PM = new PhongMaterial();
      U.setDiffuseRGB(PM, U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
      E.stormCloud.setMaterial(PM);
      E.stormCloudY = U.getValue(s, 3);
      U.add(E.stormCloud);
      if (s.contains("rain")) {
       for (n = 0; n < 1000; n++) {
        E.raindrops.add(new Raindrop());
       }
      }
      if (s.contains("lightning")) {
       E.lightningExists = true;
       U.loadSound(sounds, "thunder", 1 / 0.);
      }
     } else if (s.startsWith("tornado")) {
      double size = 1;
      for (n = 0; n < 40; n++) {
       E.tornadoParts.add(new TornadoPart(U.getValue(s, 0) * size, U.getValue(s, 0) * size));
       E.tornadoParts.get(n).setMaterial(new PhongMaterial());
       size *= U.getValue(s, 2);
       E.tornadoParts.get(n).Y = U.getValue(s, 1) * n / 40.;
       U.add(E.tornadoParts.get(n));
       E.tornadoParts.get(n).groundDust = new Cylinder(
       (n + 1) * U.getValue(s, 0) * .01,
       (n + 1) * U.getValue(s, 0) * .01);
       U.add(E.tornadoParts.get(n).groundDust);
      }
      E.tornadoMaxTravelDistance = U.getValue(s, 3);
      E.tornadoMovesFixpoints = s.contains("moveFixpoints");
     } else if (s.startsWith("mountains")) {
      setMountains(U.getValue(s, 0), Math.round(U.getValue(s, 1)));
     } else if (s.startsWith("pool")) {
      E.poolX = U.getValue(s, 0);
      E.poolZ = U.getValue(s, 1);
      E.pool[0].setRadius(U.getValue(s, 2));
      E.pool[1].setRadius(U.getValue(s, 2));
      E.poolDepth = U.getValue(s, 3);
      E.pool[1].setHeight(E.poolDepth);
      U.add(E.pool[0], E.pool[1]);
      double R = 0, G = .25, B = .75;
      E.poolPM.setSelfIlluminationMap(null);
      if (s.contains("lava")) {
       poolType = "lava";
       U.getRGB.setFill(Color.color(1, .5, 0));
       U.getRGB.fillRect(0, 0, 1, 1);
       E.poolPM.setSelfIlluminationMap(U.getRGBCanvas.snapshot(null, null));
      } else if (s.contains("acid")) {
       poolType = "acid";
       R = B = .25;
       G = 1;
      }
      E.poolPM.setDiffuseMap(U.getImage("water"));
      U.setDiffuseRGB(E.poolPM, R, G, B);
      U.setSpecularRGB(E.poolPM, 1, 1, 1);
      E.poolExists = true;
     } else if (s.startsWith("stars")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       E.stars.add(new Star());
      }
     } else if (s.startsWith("trees")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       trackParts.add(U.random() < .5 ? new TrackPart(getTrackPart("tree0"), mapModels, U.randomPlusMinus(800000.), 0, U.randomPlusMinus(800000.), 0, false, 1 + Math.sqrt(U.random(16.)), instanceScale) : new TrackPart(getTrackPart(U.random() < .5 ? "tree1" : "tree2"), mapModels, U.randomPlusMinus(800000.), 0, U.randomPlusMinus(800000.), 0, false));
      }
     } else if (s.startsWith("palmTrees")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       trackParts.add(new TrackPart(getTrackPart("treepalm"), mapModels, U.randomPlusMinus(800000.), 0, U.randomPlusMinus(800000.), 0, false, 1 + U.random(.6), instanceScale));
      }
     } else if (s.startsWith("cacti")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       trackParts.add(new TrackPart(getTrackPart("cactus" + U.random(3)), mapModels, U.randomPlusMinus(800000.), 0, U.randomPlusMinus(800000.), 0, false, .5 + U.random(.5), instanceScale));
      }
     } else if (s.startsWith("rocks")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       trackParts.add(new TrackPart(U.randomPlusMinus(800000.), U.randomPlusMinus(800000.), 0, 100 + U.random(400.), U.random(100.), U.random(200.), true));
      }
     } else if (s.startsWith("fire")) {
      E.fires.add(new Fire(s));
     } else if (s.startsWith("boulders")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       E.boulders.add(new Boulder(U.getValue(s, 1), 5, U.getValue(s, 2)));
       U.loadSound(sounds, "boulder" + E.boulders.indexOf(E.boulders.get(n)), "boulder" + U.random(2));//<-using lists--NO double
      }
      E.boulderMaxTravelDistance = U.getValue(s, 3);
     } else if (s.startsWith("tsunami")) {
      for (n = 0; n < 200; n++) {
       E.tsunamiParts.add(new TsunamiPart(U.getValue(s, 0), U.getValue(s, 0)));
      }
      E.tsunamiDirection = U.random(4);
      E.tsunamiSpeed = Math.round(U.getValue(s, 1));
      for (TsunamiPart tsunamiPart : E.tsunamiParts) {
       tsunamiPart.Y = -tsunamiPart.getRadius() * .5;
       setTsunami();
       U.add(tsunamiPart);
      }
     } else if (s.startsWith("volcano")) {
      PhongMaterial volcanoPM = new PhongMaterial();
      U.setDiffuseRGB(volcanoPM, E.groundRGB[0], E.groundRGB[1], E.groundRGB[2]);
      E.volcanoMesh.setMaterial(volcanoPM);
      E.volcanoX = U.getValue(s, 0);
      E.volcanoZ = U.getValue(s, 1);
      U.add(E.volcanoMesh);
      E.volcanoExists = true;
      if (s.contains("active")) {
       boolean isLava = true;
       for (n = 0; n < 200; n++) {
        E.volcanoRocks.add(new VolcanoRock(1000 + (n * 10), 4 + U.random(2)));
        PhongMaterial PM = new PhongMaterial();
        if (isLava) {
         E.volcanoRocks.get(n).isLava = true;
         U.setDiffuseRGB(PM, 0, 0, 0);
         U.setSpecularRGB(PM, 0, 0, 0);
        } else {
         PM.setDiffuseMap(U.getImage("rock"));
         PM.setSpecularMap(U.getImage("rock"));
         PM.setBumpMap(U.getImageNormal("rock"));
        }
        E.volcanoRocks.get(n).setMaterial(PM);
        isLava = !isLava;
        U.add(E.volcanoRocks.get(n));
       }
      }
     } else if (s.startsWith("meteors")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       E.meteors.add(new Meteor(U.getValue(s, 1)));
      }
      for (Meteor meteor : E.meteors) {
       U.loadSound(sounds, "meteor" + E.meteors.indexOf(meteor), "meteor" + U.random(2));//<-using lists--NO double
      }
      E.meteorSpeed = U.getValue(s, 2);
     }
    }
    instanceSize = s.startsWith("size(") ? U.getValue(s, 0) : instanceSize;
    if (s.startsWith("scale(")) {
     try {
      instanceScale[0] = U.getValue(s, 0);
      instanceScale[1] = U.getValue(s, 1);
      instanceScale[2] = U.getValue(s, 2);
     } catch (Exception e) {
      instanceScale[0] = instanceScale[1] = instanceScale[2] = U.getValue(s, 0);
     }
    }
    if (event == event.mapLoadPass3) {
     randomX = s.startsWith("randomX") ? U.getValue(s, 0) : randomX;
     randomY = s.startsWith("randomY") ? U.getValue(s, 0) : randomY;
     randomZ = s.startsWith("randomZ") ? U.getValue(s, 0) : randomZ;
     if (s.startsWith("(") || s.startsWith("curve(")) {
      int trackNumber = getTrackPart(U.getString(s, 0));
      if (trackNumber < 0) {
       trackNumber = 0;
       System.out.println("Map Part List Exception (" + mapName + ")");
       System.out.println("At line: " + s);
      }
      long[] random = {Math.round(U.randomPlusMinus(randomX)), Math.round(U.randomPlusMinus(randomY)), Math.round(U.randomPlusMinus(randomZ))};
      if (mapName.equals("Arctic Slip") && trackNumber == getTrackPart("checkpoint")) {
       random[0] = U.random() < .5 ? 4500 : -4500;
       random[2] = U.random() < .5 ? 4500 : -4500;
      } else if (mapName.equals("the Linear Accelerator")) {
       random[0] *= 1000;
       random[2] *= 1000;
      } else if (mapName.equals("Phantom Cavern")) {
       random[0] *= Math.abs(random[0]) > 40000 ? .5 : 1;
       random[2] *= Math.abs(random[2]) > 40000 ? .5 : 1;
       if (random[1] > 0) {
        random[1] *= -1;
        random[1] *= .5;
       }
       random[1] = Math.min(random[1] + 1000, 0);
      }
      double summedPositionX = U.getValue(s, 1) + random[0], summedPositionZ = U.getValue(s, 2) + random[2], summedPositionY = U.getValue(s, 3) + random[1];
      if (s.startsWith("curve(")) {
       double angle = 90;
       try {
        angle += U.getValue(s, 8);
       } catch (Exception ignored) {
       }
       double iterationRate = U.getValue(s, 6);
       for (double iteration = U.getValue(s, 4); ; iteration += iteration < U.getValue(s, 5) ? iterationRate : -iterationRate) {
        trackParts.add(new TrackPart(trackNumber, mapModels, summedPositionX + U.getValue(s, 7) * U.sin(iteration), summedPositionY, summedPositionZ + U.getValue(s, 7) * U.cos(iteration), -iteration + angle, false, instanceSize, instanceScale));
        if (Math.abs(iteration - U.getValue(s, 5)) < iterationRate) {
         break;
        }
       }
      } else {
       double rotation = U.getValue(s, 4);
       instanceScale[1] = mapName.equals("Ghost City") && trackNumber == getTrackPart("cube") && instanceSize == 10000 ? 1 + U.random(3.) : instanceScale[1];
       if (mapName.equals("Meteor Fields") && trackNumber == getTrackPart("ramp")) {
        if (rotation == 0) {
         summedPositionX = U.randomPlusMinus(2500.);
         summedPositionZ = 5000 + U.random(20000.);
         summedPositionZ *= U.random() < .5 ? -1 : 1;
         rotation += U.random() < .5 ? 180 : 0;
        } else {
         summedPositionZ = U.randomPlusMinus(2500.);
         summedPositionX = 5000 + U.random(20000.);
         summedPositionX *= U.random() < .5 ? -1 : 1;
         rotation += U.random() < .5 ? 180 : 0;
        }
       }
       if ((trackNumber == getTrackPart("checkpoint") || trackNumber == getTrackPart("fixring"))) {
        if (mapName.equals("Pyramid Paradise")) {
         boolean inside = true;
         while (inside) {
          inside = false;
          for (TrackPart trackPart : trackParts) {
           inside = trackPart.modelNumber == getTrackPart("pyramid") && Math.abs(summedPositionX - trackPart.X) <= trackPart.renderRadius && Math.abs(summedPositionZ - trackPart.Z) <= trackPart.renderRadius || inside;
          }
          if (inside) {
           summedPositionX = U.getValue(s, 1) + U.randomPlusMinus(randomX);
           summedPositionZ = U.getValue(s, 2) + U.randomPlusMinus(randomZ);
           summedPositionY = U.getValue(s, 3) + U.randomPlusMinus(randomY);
          }
         }
        } else if (U.listEquals(mapName, "the Forest", "Volatile Sands")) {
         boolean inside = true;
         while (inside) {
          inside = false;
          for (TrackPart trackPart : trackParts) {
           inside = !trackPart.scenery && trackPart.mound != null && U.distance(summedPositionX, trackPart.X, summedPositionZ, trackPart.Z) <= trackPart.mound.getMajorRadius() || inside;
          }
          if (inside) {
           summedPositionX = U.getValue(s, 1) + U.randomPlusMinus(randomX);
           summedPositionZ = U.getValue(s, 2) + U.randomPlusMinus(randomZ);
           summedPositionY = U.getValue(s, 3) + U.randomPlusMinus(randomY);
          }
         }
        }
       }
       if (trackNumber == getTrackPart("checkpoint")) {
        if (mapName.equals("Ghost City")) {
         if (U.random() < .5) {
          rotation = 0;
          summedPositionZ = U.randomPlusMinus(150000.);
          long randomPosition = U.random(4);
          if (randomPosition == 0) {
           summedPositionX = -150000;
          } else if (randomPosition == 1 || randomPosition == 2) {
           summedPositionX = randomPosition == 1 ? -2000 : 2000;
           while (Math.abs(summedPositionZ) < 110000) {
            summedPositionZ = U.randomPlusMinus(150000.);
           }
          } else {
           summedPositionX = randomPosition == 3 ? 150000 : summedPositionX;
          }
         } else {
          rotation = 90;
          summedPositionX = U.randomPlusMinus(150000.);
          long randomPosition = U.random(3);
          if (randomPosition == 0) {
           summedPositionZ = -150000;
          } else if (randomPosition == 1) {
           summedPositionZ = 0;
           while (Math.abs(summedPositionX) < 110000) {
            summedPositionX = U.randomPlusMinus(150000.);
           }
          }
          summedPositionZ = randomPosition == 2 ? 150000 : summedPositionZ;
         }
        } else if (mapName.equals("the Machine is Out of Control")) {
         boolean inside = true;
         while (inside) {
          inside = Math.abs(summedPositionX) < 30000 && Math.abs(summedPositionZ) < 30000;
          for (TrackPart trackpart : trackParts) {
           inside = (trackpart.modelNumber == getTrackPart("pyramid") || trackpart.modelNumber == getTrackPart("cube") || trackpart.modelNumber == getTrackPart("ramptriangle")) && Math.abs(summedPositionX - trackpart.X) <= trackpart.renderRadius && Math.abs(summedPositionZ - trackpart.Z) <= trackpart.renderRadius || inside;
          }
          if (inside) {
           summedPositionX = U.getValue(s, 1) + U.randomPlusMinus(randomX);
           summedPositionZ = U.getValue(s, 2) + U.randomPlusMinus(randomZ);
           summedPositionY = U.getValue(s, 3) + U.randomPlusMinus(randomY);
          }
         }
        } else if (mapName.equals("DoomsDay")) {
         while (U.distance(summedPositionX, 0, summedPositionZ, 0) <= 53000) {
          summedPositionX = U.getValue(s, 1) + U.randomPlusMinus(randomX);
          summedPositionZ = U.getValue(s, 2) + U.randomPlusMinus(randomZ);
          summedPositionY = U.getValue(s, 3) + U.randomPlusMinus(randomY);
         }
        }
       }
       if (U.getString(s, 0).equals("mound")) {
        try {
         trackParts.add(new TrackPart(summedPositionX, summedPositionZ, summedPositionY,
         U.getValue(s, 4) * instanceSize, U.getValue(s, 5) * instanceSize, U.getValue(s, 6) * instanceSize, false));
        } catch (Exception E) {
         trackParts.add(new TrackPart(summedPositionX, summedPositionZ, summedPositionY,
         U.getValue(s, 4) * instanceSize * U.random(), U.getValue(s, 4) * instanceSize * U.random(), U.getValue(s, 4) * instanceSize * U.random(), false));
        }
       } else {
        trackParts.add(new TrackPart(trackNumber, mapModels, summedPositionX, summedPositionY, summedPositionZ, rotation, false, instanceSize, instanceScale));
       }
       if (trackNumber == getTrackPart("rainbow")) {
        trackParts.get(trackParts.size() - 1).modelProperties += " rainbow ";
       } else if (trackNumber == getTrackPart("crescent")) {
        U.remove(E.sun);
       } else if (trackNumber == getTrackPart("checkpoint")) {
        points.add(new Point());
        points.get(points.size() - 1).X = summedPositionX;
        points.get(points.size() - 1).Z = summedPositionZ;
        points.get(points.size() - 1).Y = summedPositionY;
        points.get(points.size() - 1).type = Math.abs(rotation) <= 45 ? "cpZ" : "cpX";
        trackParts.get(trackParts.size() - 1).checkpointPerpendicularToX = Math.abs(rotation) > 45;
        checkpoints.add(new Checkpoint());
        checkpoints.get(checkpoints.size() - 1).X = summedPositionX;
        checkpoints.get(checkpoints.size() - 1).Z = summedPositionZ;
        checkpoints.get(checkpoints.size() - 1).Y = summedPositionY;
        checkpoints.get(checkpoints.size() - 1).type = Math.abs(rotation) <= 45 ? "cpZ" : "cpX";
        checkpoints.get(checkpoints.size() - 1).location = points.size() - 1;
        trackParts.get(trackParts.size() - 1).checkpointNumber = checkpoints.size() - 1;
       } else if (s.contains(").")) {
        points.add(new Point());
        points.get(points.size() - 1).X = summedPositionX;
        points.get(points.size() - 1).Z = summedPositionZ;
        points.get(points.size() - 1).Y = summedPositionY;
        if (s.contains(")...")) {
         points.get(points.size() - 1).type = "...";
        } else if (s.contains(")..")) {
         points.get(points.size() - 1).type = "..";
        }
       }
       fixRingsExist = trackNumber == getTrackPart("fixring") || fixRingsExist;
      }
     }
    }
   }
  } catch (Exception e) {//<-Don't further specify
   event = event.mapError;
   System.out.println("Map Error (" + mapName + ")");
   System.out.println("" + e);
   System.out.println("At line: " + s);
   e.printStackTrace();
  }
  if (event == event.mapLoadPass3) {
   if (mapName.equals("Circle Race XL")) {
    for (n = 0; n < 360; n++) {
     double calculatedX = 320000 * -Math.sin(Math.toRadians(n)),
     calculatedZ = 320000 * Math.cos(Math.toRadians(n));
     trackParts.add(new TrackPart(getTrackPart("checkpoint"), mapModels, calculatedX, 0, calculatedZ, n - 90, false));
     points.add(new Point());
     points.get(points.size() - 1).X = calculatedX;
     points.get(points.size() - 1).Z = calculatedZ;
     points.get(points.size() - 1).type = "cp";
     checkpoints.add(new Checkpoint());
     checkpoints.get(checkpoints.size() - 1).X = calculatedX;
     checkpoints.get(checkpoints.size() - 1).Z = calculatedZ;
     checkpoints.get(checkpoints.size() - 1).type = "cp";
     checkpoints.get(checkpoints.size() - 1).location = points.size() - 1;
     trackParts.get(trackParts.size() - 1).checkpointNumber = checkpoints.size() - 1;
    }
   } else if (mapName.equals("Ghost City")) {
    instanceSize = 1;
    instanceScale[0] = 1;
    instanceScale[1] = 1;
    instanceScale[2] = 2;
    for (n = 0; n < 360; n++) {
     double calculatedX = 120000 * -Math.sin(Math.toRadians(n)),
     calculatedZ = 120000 * Math.cos(Math.toRadians(n));
     trackParts.add(new TrackPart(getTrackPart("roadshort"), mapModels, calculatedX, 0, calculatedZ, n - 90, false, instanceSize, instanceScale));
    }
    for (n = 0; n < 360; n++) {
     double calculatedX = 122000 * -Math.sin(Math.toRadians(n)),
     calculatedZ = 122000 * Math.cos(Math.toRadians(n));
     trackParts.add(new TrackPart(getTrackPart("roadshort"), mapModels, calculatedX, 0, calculatedZ, n - 90, false, instanceSize, instanceScale));
    }
    for (n = 5; n < 365; n += 10) {
     instanceSize = 2500 + U.random(2500.);
     instanceScale[0] = 1 + U.random(2.);
     instanceScale[1] = 1 + U.random(2.);
     instanceScale[2] = 1 + U.random(2.);
     double calculatedX = 112500 * -Math.sin(Math.toRadians(n)),
     calculatedZ = 112500 * Math.cos(Math.toRadians(n));
     trackParts.add(new TrackPart(getTrackPart("cube"), mapModels, calculatedX, 0, calculatedZ, n - 90, false, instanceSize, instanceScale));
    }
   } else if (mapName.equals("World's Biggest Parking Lot")) {
    for (n = 0; n < vehicleModels.size(); n++) {
     double xRandom = U.randomPlusMinus(30000.), zRandom = U.randomPlusMinus(30000.), randomXZ = U.randomPlusMinus(180.);
     userRandomRGB[0] = U.random();
     userRandomRGB[1] = U.random();
     userRandomRGB[2] = U.random();
     /*{//<-This block's used to get the V.E. logo image on the World's Biggest Parking Lot
      double vehicleCircle = 360 * n / (double) vehicleModels.size();
      xRandom = 4000 * U.sin(vehicleCircle);
      zRandom = 4000 * U.cos(vehicleCircle);
      randomXZ = -vehicleCircle + 180;
     }*/
     trackParts.add(new TrackPart(n, vehicleModels, xRandom, 0, zRandom, randomXZ, true));
     trackParts.get(trackParts.size() - 1).Y = -trackParts.get(trackParts.size() - 1).clearanceY - trackParts.get(trackParts.size() - 1).turretBaseY;
     points.add(new Point());
     points.get(points.size() - 1).X = xRandom;
     points.get(points.size() - 1).Z = zRandom;
     points.get(points.size() - 1).type = "cp";
     checkpoints.add(new Checkpoint());
     checkpoints.get(checkpoints.size() - 1).X = xRandom;
     checkpoints.get(checkpoints.size() - 1).Z = zRandom;
     checkpoints.get(checkpoints.size() - 1).type = "cp";
     checkpoints.get(checkpoints.size() - 1).location = points.size() - 1;
     trackParts.get(trackParts.size() - 1).checkpointNumber = checkpoints.size() - 1;
    }
   }
   bonusX = bonusY = bonusZ = 0;
   U.add(bonusBig);
   for (BonusBall bonusBall : bonusBalls) {
    U.add(bonusBall);
   }
   for (Fire fire : E.fires) {
    for (Flame flame : fire.flames) {
     double fireSize = fire.size * .5;
     TriangleMesh TM = new TriangleMesh();
     TM.getPoints().setAll((float) U.randomPlusMinus(fireSize), (float) U.randomPlusMinus(fireSize), (float) U.randomPlusMinus(fireSize),
     (float) U.randomPlusMinus(fireSize), (float) U.randomPlusMinus(fireSize), (float) U.randomPlusMinus(fireSize),
     (float) U.randomPlusMinus(fireSize), (float) U.randomPlusMinus(fireSize), (float) U.randomPlusMinus(fireSize));
     TM.getTexCoords().setAll(0, 0);
     TM.getFaces().setAll(0, 0, 1, 0, 2, 0);
     flame.setMesh(TM);
     flame.X = fire.X;
     flame.Y = Double.POSITIVE_INFINITY;
     flame.Z = fire.Z;
     flame.setCullFace(CullFace.NONE);
     PhongMaterial PM = new PhongMaterial();
     U.setDiffuseRGB(PM, 0, 0, 0);
     U.setSpecularRGB(PM, 0, 0, 0);
     flame.setMaterial(PM);
     U.add(flame);
    }
    TriangleMesh TM = new TriangleMesh();
    double fireSize = fire.size;
    TM.getPoints().setAll((float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize));
    TM.getTexCoords().setAll(0, 0);
    TM.getFaces().setAll(0, 0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8, 0, 9, 0, 10, 0, 11, 0, 12, 0, 13, 0, 14, 0, 15, 0, 16, 0, 17, 0, 18, 0, 19, 0, 20, 0, 21, 0, 22, 0, 23, 0);
    fire.setMesh(TM);
    PhongMaterial PM = new PhongMaterial();
    U.setDiffuseRGB(PM, 0, 0, 0);
    U.setSpecularRGB(PM, 0, 0, 0);
    fire.setMaterial(PM);
    U.add(fire);
    if (fire.hasSound) {
     U.loadSound(sounds, "fire" + E.fires.indexOf(fire), "fire");
    }
   }
  } else if (event == event.mapLoadPass4) {
   if (!inViewer) {
    for (n = vehiclesInMatch; --n >= 0; ) {
     vehicles.add(null);
    }
    vehicles.set(userPlayer, new Vehicle(vehicleNumber[userPlayer], vehicleModels, userPlayer, true));
    for (n = vehiclesInMatch; --n >= 0; ) {
     if (n != userPlayer) {
      vehicles.set(n, new Vehicle(vehicleNumber[n], vehicleModels, n, true));
     }
    }
   }
   reset();
   loadSoundtrack();
   readyLAN = new boolean[maxPlayersLAN];
   scene.setCursor(Cursor.CROSSHAIR);
  }
  String loadText = event == event.mapLoadPass0 ? "Removing Previous Content" : event == event.mapLoadPass1 ? "Loading Properties & Scenery" : event == event.mapLoadPass2 ? "Adding Track Pieces" : "Adding " + vehiclesInMatch + " Vehicle(s)";
  U.fillRGB(0, 0, 0);
  U.fillRectangle(.5, .5, 1, 1);
  U.font(.025);
  U.fillRGB(1, 1, 1);
  U.text(tournament > 0 ? "Round " + tournament + (tournament > 5 ? "--Overtime!" : "") : "", .425);
  U.text(mapName, .475);
  U.text(".." + loadText + "..", .525);
  if (event != event.mapError) {
   event = event == event.mapLoadPass0 ? event.mapLoadPass1 : event == event.mapLoadPass1 ? event.mapLoadPass2 : event == event.mapLoadPass2 ? event.mapLoadPass3 : event == event.mapLoadPass3 ? event.mapLoadPass4 : (inViewer ? event.mapViewer : modeLAN == LAN.JOIN ? event.play : event.mapView);
  }
 }

 private void loadSoundtrack() {
  if (musicTrackNumber != map) {
   Thread thread = new Thread(() -> {
    if (mediaPlayer != null) {
     mediaPlayer.stop();
    }
    try {
     Media media;
     try {
      try {
       media = new Media(new File("music" + File.separator + maps.get(map) + ".mp3").toURI().toURL().toString());
      } catch (MalformedURLException E) {
       media = new Media(new File("music" + File.separator + maps.get(map) + ".wav").toURI().toURL().toString());
      }
     } catch (Exception E) {//<-do NOT change
      try {
       media = new Media(new File("music" + File.separator + "User-Submitted" + File.separator + maps.get(map) + ".mp3").toURI().toURL().toString());
      } catch (MalformedURLException e) {
       media = new Media(new File("music" + File.separator + "User-Submitted" + File.separator + maps.get(map) + ".wav").toURI().toURL().toString());
      }
     }
     mediaPlayer = new MediaPlayer(media);
    } catch (Exception E) {//<-do NOT change
     System.out.println("Problem loading Music: " + E);
    }
    if (mediaPlayer != null) {
     mediaPlayer.play();
     mediaPlayer.setVolume(musicVolume);
     mediaPlayer.setCycleCount(-1);
    }
    musicTrackNumber = map;
   });
   thread.setDaemon(true);
   thread.start();
  }
 }

 private void addArrow() {
  if (!group2.getChildren().contains(arrow)) {
   group2.getChildren().add(arrow);
  }
  arrow.setVisible(false);
  PointLight backPL = new PointLight();
  backPL.setTranslateX(0);
  backPL.setTranslateY(arrow.getTranslateY());
  backPL.setTranslateZ(-Long.MAX_VALUE);
  backPL.setColor(Color.color(1, 1, 1));
  PointLight frontPL = new PointLight();
  frontPL.setTranslateX(0);
  frontPL.setTranslateY(arrow.getTranslateY());
  frontPL.setTranslateZ(Long.MAX_VALUE);
  frontPL.setColor(Color.color(1, 1, 1));
  group2.getChildren().addAll(new AmbientLight(Color.color(.5, .5, .5)), backPL, frontPL);
 }

 private int getTrackPart(String s) {
  int trackPart;
  for (trackPart = mapModels.size(); --trackPart >= 0; ) {
   if (s.equals(mapModels.get(trackPart))) {
    break;
   }
  }
  return trackPart;
 }

 private int getMapName(String s) {
  int n;
  String s1;
  for (n = 0; n < maps.size(); n++) {
   try (BufferedReader BR = new BufferedReader(new InputStreamReader(U.getMapFile(n)))) {
    for (String s2; (s2 = BR.readLine()) != null; ) {
     s1 = "" + s2.trim();
     mapName = s1.startsWith("name") ? U.getString(s1, 0) : mapName;
    }
   } catch (Exception e) {
    event = event.mapError;
    e.printStackTrace();
   }
   if (s.equals(mapName)) {
    break;
   }
  }
  return n;
 }

 private int getVehicleName(String s) {
  String s1, s3 = "";
  int n;
  for (n = 0; n < vehicleModels.size(); n++) {
   FileInputStream FIS = null;
   try {
    try {
     try {
      FIS = new FileInputStream("models" + File.separator + vehicleModels.get(n));
     } catch (FileNotFoundException e) {
      FIS = new FileInputStream("models" + File.separator + "User-Submitted" + File.separator + vehicleModels.get(n));
     }
    } catch (FileNotFoundException e) {
     FIS = new FileInputStream("models" + File.separator + "basic");
    }
   } catch (FileNotFoundException ignored) {
   }
   try (BufferedReader BR = new BufferedReader(new InputStreamReader(Objects.requireNonNull(FIS)))) {
    for (String s2; (s2 = BR.readLine()) != null; ) {
     s1 = "" + s2.trim();
     s3 = s1.startsWith("name") ? U.getString(s1, 0) : s3;
    }
   } catch (Exception e) {
    e.printStackTrace();
   }
   if (s.equals(s3)) {
    break;
   }
  }
  return n;
 }

 private void setGroundPlates(String terrain) {
  if (!terrain.contains(" snow ")) {
   for (int n = 0; n < 419; n++) {//<-Check object quantity in a profile
    E.groundPlates.add(new GroundPlate(mapName.equals("Epic Trip") ? 1500 : 1732.0508075688772935274463415059, 0));
   }
   double baseX = -30000, baseZ = -30000;
   boolean shift = false;
   for (GroundPlate groundPlate : E.groundPlates) {
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
    PM.setSpecularPower(E.groundSpecularPower);
    PM.setDiffuseMap(U.getImage(terrain));
    PM.setSpecularMap(U.getImage(terrain));
    PM.setBumpMap(U.getImageNormal(terrain));
    groundPlate.setRotationAxis(Rotate.Y_AXIS);
    groundPlate.setRotate(-30 + (60 * U.random(6)));//<-Hex-rotation basis can't be random double!
    U.add(groundPlate);
    double varyRGB = 1 + U.randomPlusMinus(.05);
    U.setDiffuseRGB(PM, E.terrainRGB[0] * varyRGB, E.terrainRGB[1] * varyRGB, E.terrainRGB[2] * varyRGB);
    U.setSpecularRGB(PM, E.terrainRGB[0] * varyRGB, E.terrainRGB[1] * varyRGB, E.terrainRGB[2] * varyRGB);
    groundPlate.clampXZ();
   }
   for (int n = 0; n < E.groundPlates.size(); n++) {
    E.groundPlates.get(n).checkDuplicate();
   }
  }
 }

 private void setClouds() {
  E.cloudWrapDistance = mapName.equals("Ethereal Mist") ? 100000 : U.listEquals(mapName, "the Test of Endurance", "an Immense Relevance", "SUMMIT of EPIC") ? 10000000 : 1000000;
  U.setDiffuseRGB(E.cloudPM, E.cloudProperties[0], E.cloudProperties[1], E.cloudProperties[2]);
  for (int n = 0; n < U.random(120); n++) {
   E.clouds.add(new Cloud());
  }
 }

 private void setMountains(double spread, long randomGenerate) {
  PhongMaterial mountainPM = new PhongMaterial();
  U.setDiffuseRGB(mountainPM, E.terrainRGB[0], E.terrainRGB[1], E.terrainRGB[2]);
  U.setSpecularRGB(mountainPM, 0, 0, 0);
  mountainPM.setDiffuseMap(U.getImage(terrain.trim()));
  mountainPM.setSpecularMap(U.getImage(terrain.trim()));
  mountainPM.setBumpMap(U.getImageNormal(terrain.trim()));
  for (int n = 0; n < 20; n++) {
   E.mountains.add(new Mountain());
  }
  Random random = mapName.equals("Phantom Cavern") ? new Random(Long.MAX_VALUE) : new Random(randomGenerate);
  for (Mountain mountain : E.mountains) {
   random.nextDouble();//<-Legacy throwback for older maps. Don't remove!
   long size = 50000;
   TriangleMesh TM = new TriangleMesh();
   TM.getTexCoords().setAll(0, 1, 2, 0, 1, 2, 0, 2, 1, 0, 2, 1);
   TM.getPoints().setAll(
   0, -size * random.nextFloat(), 0,
   0, 0, -size * random.nextFloat(),
   -size * random.nextFloat(), 0, 0,
   size * random.nextFloat(), 0, 0,
   0, 0, size * random.nextFloat());
   TM.getFaces().setAll(
   0, 4, 2, 2, 1, 3,
   0, 4, 1, 3, 3, 1,
   0, 4, 3, 1, 4, 0,
   0, 4, 4, 0, 2, 2,
   4, 0, 1, 3, 2, 2,
   4, 0, 3, 1, 1, 3);
   mountain.setMesh(TM);
   mountain.setMaterial(mountainPM);
   double[] rotatedX = {spread + spread * random.nextDouble()}, rotatedZ = {spread + spread * random.nextDouble()};
   U.rotate(rotatedX, rotatedZ, random.nextDouble() * 360);
   mountain.X = rotatedX[0];
   mountain.Z = rotatedZ[0];
   U.add(mountain);
  }
 }

 private void setTsunami() {
  E.tsunamiDirection = U.random(4);
  if (E.tsunamiDirection < 2) {
   E.tsunamiX = -200000;
   E.tsunamiSpeedX = 0;
   if (E.tsunamiDirection == 0) {
    E.tsunamiZ = -200000;
    E.tsunamiSpeedZ = E.tsunamiSpeed;
   } else if (E.tsunamiDirection == 1) {
    E.tsunamiZ = 200000;
    E.tsunamiSpeedZ = -E.tsunamiSpeed;
   }
  } else {
   E.tsunamiZ = -200000;
   E.tsunamiSpeedZ = 0;
   if (E.tsunamiDirection == 2) {
    E.tsunamiX = 200000;
    E.tsunamiSpeedX = -E.tsunamiSpeed;
   } else if (E.tsunamiDirection == 3) {
    E.tsunamiX = -200000;
    E.tsunamiSpeedX = E.tsunamiSpeed;
   }
  }
  for (int n = E.tsunamiParts.size(); --n >= 0; ) {
   if (E.tsunamiDirection < 2) {
    E.tsunamiParts.get(n).X = n * 2000 - 200000;
    E.tsunamiParts.get(n).Z = E.tsunamiDirection == 0 ? -200000 : E.tsunamiDirection == 1 ? 200000 : 0;
   } else {
    E.tsunamiParts.get(n).Z = n * 2000 - 200000;
    E.tsunamiParts.get(n).X = E.tsunamiDirection == 2 ? 200000 : E.tsunamiDirection == 3 ? -200000 : 0;
   }
  }
 }

 private void manageEnvironment() {
  List<Node> theChildren = group.getChildren();
  boolean gamePlay = event == event.play || event == event.replay,
  mapViewer = event == event.mapViewer, update = mapViewer || (gamePlay && matchStarted);
  E.sunlightAngle = E.sunX != 0 || E.sunZ != 0 ? (((E.sunX / (E.sunY * 50)) * U.sin(cameraXZ)) + ((E.sunZ / (E.sunY * 50)) * U.cos(cameraXZ))) * U.cos(cameraYZ) : 0;
  if (mapName.equals("the Sun")) {
   E.sunRGBVariance *= U.random() < .5 ? 81 / 80. : 80 / 81.;
   E.sunRGBVariance = U.clamp(.2, E.sunRGBVariance, 1);
   scene3D.setFill(Color.color(E.sunRGBVariance, E.sunRGBVariance * .5, 0));
  } else {
   scene3D.setFill(Color.color(U.clamp(E.skyRGB[0] - E.sunlightAngle), U.clamp(E.skyRGB[1] - E.sunlightAngle), U.clamp(E.skyRGB[2] - E.sunlightAngle)));
  }
  if (theChildren.contains(E.sunlight)) {
   U.setTranslate(E.sunlight, E.sunlightX, E.sunlightY, E.sunlightZ);
   if (theChildren.contains(E.sun)) {
    U.render(E.sun, E.sunX, E.sunY, E.sunZ, -E.sun.getRadius());
   }
  }
  if (theChildren.contains(E.ground) && E.groundLevel <= 0 && !mapName.equals("Phantom Cavern")) {
   E.groundY = vehiclePerspective < vehicles.size() && vehicles.get(vehiclePerspective).inPool && cameraY > 0 ? E.poolDepth : Math.max(0, -cameraY * .01);
   while (Math.abs(E.groundX - cameraX) > 100000) {
    E.groundX += E.groundX > cameraX ? -200000 : 200000;
   }
   while (Math.abs(E.groundZ - cameraZ) > 100000) {
    E.groundZ += E.groundZ > cameraZ ? -200000 : 200000;
   }
   if (cameraY < E.groundY) {
    U.setTranslate(E.ground, E.groundX, E.groundY, E.groundZ);
    E.ground.setVisible(true);
   } else {
    E.ground.setVisible(false);
   }
  }
  for (Star star : E.stars) {
   star.run();
  }
  for (Cloud cloud : E.clouds) {
   cloud.run();
  }
  for (Mountain mountain : E.mountains) {
   mountain.run();
  }
  if (E.groundPlates.size() > 0) {
   double radius = E.pool[0].getRadius() - E.groundPlates.get(0).getRadius();
   for (GroundPlate groundPlate : E.groundPlates) {
    groundPlate.run(radius);
   }
  }
  if (theChildren.contains(E.stormCloud)) {
   E.stormCloud.setTranslateY(E.stormCloudY - cameraY);
   if (E.raindrops.size() > 0) {
    for (Raindrop raindrop : E.raindrops) {
     raindrop.run();
    }
    if (!muteSound && (gamePlay || mapViewer)) {
     U.soundLoop(sounds, "rain", Math.sqrt(U.distance(0, 0, cameraY, 0, 0, 0)) * .08);
    } else {
     U.soundStop(sounds, "rain");
    }
   }
   if (E.lightningExists) {
    U.remove(E.lightningLight[0]);
    if (E.lightningStrikeStage < 8) {
     double randomX = U.randomPlusMinus(3000.), randomZ = U.randomPlusMinus(3000.);
     ((TriangleMesh) E.lightningMesh.getMesh()).getPoints().setAll((float) U.randomPlusMinus(1000.), (float) E.stormCloudY, (float) U.randomPlusMinus(1000.),
     (float) U.randomPlusMinus(1000.), (float) E.stormCloudY, (float) U.randomPlusMinus(1000.),
     (float) (U.randomPlusMinus(1000.) + randomX), (float) (E.stormCloudY * .5), (float) (U.randomPlusMinus(1000.) + randomZ),
     (float) (U.randomPlusMinus(1000.) + randomX), (float) (E.stormCloudY * .5), (float) (U.randomPlusMinus(1000.) + randomZ),
     (float) U.randomPlusMinus(1000.), 0, (float) U.randomPlusMinus(1000.),
     (float) U.randomPlusMinus(1000.), 0, (float) U.randomPlusMinus(1000.));
     U.setTranslate(E.lightningMesh, E.lightningX, 0, E.lightningZ);
     if (E.lightningStrikeStage < 4) {
      U.setTranslate(E.lightningLight[0], E.lightningX, 0, E.lightningZ);
      U.add(E.lightningLight[0]);
      if (E.lightningStrikeStage < 1) {
       U.add(E.lightningMesh);
       if (gamePlay || mapViewer) {
        E.randomThunderSound = U.randomize(E.randomThunderSound, 11);
        U.soundPlay(sounds, "thunder" + E.randomThunderSound, Math.sqrt(U.distance(cameraX, E.lightningX, cameraY, 0, cameraZ, E.lightningZ)) * .04);
       }
      }
     }
     U.setTranslate(E.lightningLight[1], E.lightningX, 0, E.lightningZ);
     U.add(E.lightningLight[1]);
     U.fillRGB(1, 1, 1, U.random(.5));
     U.fillRectangle(.5, .5, 1, 1);
    } else {
     U.remove(E.lightningMesh, E.lightningLight[1]);
    }
    if (++E.lightningStrikeStage > U.random(13000.)) {
     E.lightningX = cameraX + U.randomPlusMinus(200000.);
     E.lightningZ = cameraZ + U.randomPlusMinus(200000.);
     E.lightningStrikeStage = 0;
    }
   }
  }
  if (E.poolExists) {
   if (cameraY < 0) {
    U.setTranslate(E.pool[0], E.poolX, 0, E.poolZ);
    E.pool[0].setVisible(true);
    E.pool[1].setVisible(false);
   } else {
    U.setTranslate(E.pool[1], E.poolX, E.poolDepth * .5, E.poolZ);
    E.pool[1].setVisible(true);
    E.pool[0].setVisible(false);
   }
  }
  int n;
  if (E.tornadoParts.size() > 0) {
   if (E.wind > 0 && (gamePlay || mapViewer)) {
    E.tornadoParts.get(0).X += E.windX * tick;
    E.tornadoParts.get(0).Z += E.windZ * tick;
   }
   if (U.distance(0, E.tornadoParts.get(0).X, 0, E.tornadoParts.get(0).Z) > E.tornadoMaxTravelDistance) {
    E.tornadoParts.get(0).X *= .999;
    E.tornadoParts.get(0).Z *= .999;
    E.windX *= -1;
    E.windZ *= -1;
   }
   for (n = 1; n < E.tornadoParts.size(); n++) {
    E.tornadoParts.get(n).X = (E.tornadoParts.get(n - 1).X + E.tornadoParts.get(n).X) * .5;
    E.tornadoParts.get(n).Z = (E.tornadoParts.get(n - 1).Z + E.tornadoParts.get(n).Z) * .5;
   }
   for (TornadoPart tornadoPart : E.tornadoParts) {
    tornadoPart.run();
   }
   if (!muteSound && (gamePlay || mapViewer)) {
    U.soundLoop(sounds, "tornado", Math.sqrt(U.distance(cameraX, E.tornadoParts.get(0).X, cameraY, 0, cameraZ, E.tornadoParts.get(0).Z)) * .08);
   } else {
    U.soundStop(sounds, "tornado");
   }
  }
  if (E.wind > 0) {
   E.windX += (E.wind * U.random(tick)) - (E.wind * U.random(tick));
   E.windZ += (E.wind * U.random(tick)) - (E.wind * U.random(tick));
   E.windX -= E.windX * .0004 * tick;
   E.windZ -= E.windZ * .0004 * tick;
   E.windX = U.clamp(-E.wind * 20, E.windX, E.wind * 20);
   E.windZ = U.clamp(-E.wind * 20, E.windZ, E.wind * 20);
  }
  for (Snowball snowball : E.snowballs) {
   snowball.run();
  }
  if (poolType.equals("lava")) {
   U.setDiffuseRGB(E.poolPM, 1, .25 + U.random(.5), 0);
  }
  if (E.tsunamiParts.size() > 0) {
   if (update && ((E.tsunamiDirection < 2 && Math.abs(E.tsunamiZ) > 200000) || (E.tsunamiDirection > 1 && Math.abs(E.tsunamiX) > 200000))) {
    setTsunami();
   }
   if (update) {
    E.tsunamiX += E.tsunamiSpeedX * tick;
    E.tsunamiZ += E.tsunamiSpeedZ * tick;
   }
   for (TsunamiPart tsunamiPart : E.tsunamiParts) {
    tsunamiPart.run(update);
   }
   if (!muteSound && (gamePlay || mapViewer)) {
    double soundDistance = Double.POSITIVE_INFINITY;
    for (TsunamiPart tsunamiPart : E.tsunamiParts) {
     soundDistance = Math.min(soundDistance, U.distance(cameraX, tsunamiPart.X, cameraY, tsunamiPart.Y, cameraZ, tsunamiPart.Z));
    }
    U.soundLoop(sounds, "tsunami", Math.sqrt(soundDistance) * .04);
   } else {
    U.soundStop(sounds, "tsunami");
   }
  }
  for (Fire fire : E.fires) {
   fire.run(gamePlay || mapViewer);
  }
  for (Boulder boulder : E.boulders) {
   boulder.run(update);
  }
  if (E.volcanoExists) {
   U.setTranslate(E.volcanoMesh, E.volcanoX, 0, E.volcanoZ);
   if (E.volcanoRocks.size() > 0) {
    if (E.volcanoEruptionStage > 0) {
     long rocksLanded = 0;
     for (VolcanoRock volcanoRock : E.volcanoRocks) {
      if (volcanoRock.groundHit) {
       volcanoRock.Y = -U.random(46000.);
       rocksLanded++;
      } else {
       if (update) {
        volcanoRock.X += volcanoRock.speedX;
        volcanoRock.Y += volcanoRock.speedY;
        volcanoRock.Z += volcanoRock.speedZ;
        volcanoRock.speedY += E.gravity * tick;
       }
       if (volcanoRock.Y > volcanoRock.getRadius()) {
        volcanoRock.groundHit = true;
        volcanoRock.X = E.volcanoX;
        volcanoRock.Z = E.volcanoZ;
       }
      }
     }
     E.volcanoEruptionStage = rocksLanded >= E.volcanoRocks.size() ? 0 : E.volcanoEruptionStage + tick;
    } else if (update) {
     for (VolcanoRock volcanoRock : E.volcanoRocks) {
      volcanoRock.deploy();
     }
     E.volcanoEruptionStage = 1;
     U.soundPlay(sounds, "volcano", Math.sqrt(U.distance(cameraX, E.volcanoX, cameraY, -50000, cameraZ, E.volcanoZ)) * .02);
    }
    for (VolcanoRock volcanoRock : E.volcanoRocks) {
     volcanoRock.run();
    }
   }
  }
  if (E.sandstormExists) {
   double stormPower = Math.sqrt(Math.pow(E.windX, 2) * Math.pow(E.windZ, 2));//<-Multiplied--not added!
   U.fillRGB(E.groundRGB[0], E.groundRGB[1], E.groundRGB[2], .05);
   double dustWidth = width * .25, dustHeight = height * .25;
   for (n = (int) (stormPower * .025); --n >= 0; ) {
    graphicsContext.fillOval(-dustWidth + U.random(width + dustWidth), -dustHeight + U.random(height + dustHeight), dustWidth, dustHeight);
   }
   if (!muteSound && (gamePlay || mapViewer)) {
    U.soundLoop(sounds, "sandstorm", 40 - (Math.min(40, 3 * Math.pow(stormPower, .25))));
   } else {
    U.soundStop(sounds, "sandstorm");
   }
  }
  for (Meteor meteor : E.meteors) {
   meteor.run(gamePlay || mapViewer);
  }
 }

 private void vehicleSelect(boolean gamePlay) {
  int n;
  if (waitingLAN) {
   U.font(.03);
   U.fillRGB(1, 1, 1);
   if (vehiclesInMatch < 3) {
    U.text("..Please Wait for " + playerNames[modeLAN == LAN.HOST ? 1 : 0] + " to Select Vehicle..", .5, .5);
   } else {
    U.text("..Please Wait for all other players to Select their Vehicle..", .5, .5);
   }
   if (modeLAN == LAN.HOST) {
    if (timer <= 0) {
     for (PrintWriter PW : outLAN) {
      PW.println("Vehicle0(" + vehicles.get(0).vehicleName);
     }
    }
    for (n = vehiclesInMatch; --n > 0; ) {
     String s = U.readInLAN(n - 1);
     if (s.startsWith("CANCEL")) {
      escapeToLast(false);
     } else if (s.startsWith("Vehicle(")) {
      vehicleNumber[n] = getVehicleName(U.getString(s, 0));
      if (vehiclesInMatch > 2) {
       for (PrintWriter out : outLAN) {
        out.println("Vehicle" + n + "(" + U.getString(s, 0));
       }
      }
      readyLAN[n] = true;
      System.out.println(playerNames[n] + " has selected Vehicle");
     }
    }
   } else {
    if (timer <= 0) {
     outLAN.get(0).println("Vehicle(" + vehicles.get(0).vehicleName);
    }
    String s = U.readInLAN(0);
    if (s.startsWith("CANCEL")) {
     escapeToLast(false);
    } else {
     for (n = vehiclesInMatch; --n >= 0; ) {
      if (n != userPlayer) {
       if (s.startsWith("Vehicle" + n + "(")) {
        vehicleNumber[n] = getVehicleName(U.getString(s, 0));
        readyLAN[n] = true;
       }
      }
     }
    }
   }
   long whoIsReady = 0;
   for (n = vehiclesInMatch; --n >= 0; ) {
    whoIsReady = readyLAN[n] ? ++whoIsReady : whoIsReady;
   }
   if (whoIsReady >= vehiclesInMatch) {
    event = event.mapJump;
    waitingLAN = false;
   }
  } else {
   U.font(.03);
   if (section == 0) {
    clearGraphics();
    vehicles.clear();
    scene3D.setFill(Color.color(0, 0, 0));
    cameraX = cameraZ = cameraYZ = cameraXY = 0;
    cameraY = -250;
    cameraXZ = 180;
    cameraRotateXY.setAngle(0);
    U.setTranslate(E.ground, 0, 0, 0);
    U.setDiffuseRGB((PhongMaterial) E.ground.getMaterial(), .1, .1, .1);
    for (Raindrop raindrop : E.raindrops) {
     U.add(raindrop);
    }
    for (Snowball snowball : E.snowballs) {
     U.add(snowball.S);
    }
    addVehicleModel(vehicleNumber[vehiclePick], showVehicle);
    sameVehicles = false;
    section = 1;
   }
   sameVehicles = vehiclePick <= 0 && modeLAN == LAN.OFF && sameVehicles;
   Vehicle V = vehicles.get(0);
   U.fillRGB(1, 1, 1);
   U.text("SELECT " + (inViewer ? "VEHICLE TO EDIT" : vehiclePick > 0 ? "PLAYER #" + vehiclePick : "VEHICLE"), .075);
   V.inDriverView = false;
   V.processGraphics(gamePlay);
   V.Z = -1000;
   V.XZ += (.5 - mouseX) * 20 * tick;
   if (V.spinnerSpeed == V.spinnerSpeed) {
    V.spinnerXZ = -V.XZ;
   }
   if (V.vehicleType == type.turret) {
    V.Y = -V.turretBaseY;
    V.YZ -= (.5 - mouseY) * 20 * tick;
    V.YZ = U.clamp(-90, V.YZ, 90);
   } else {
    V.Y = -V.clearanceY;
   }
   U.font(.0125);
   if (vehiclesInMatch > 2) {
    if (vehiclePick < vehiclesInMatch >> 1) {
     U.fillRGB(0, 1, 0);
     U.text(modeLAN == LAN.OFF ? "(GREEN TEAM)" : "You're on the GREEN TEAM", .1);
    } else {
     U.fillRGB(1, 0, 0);
     U.text(modeLAN == LAN.OFF ? "(RED TEAM)" : "You're on the RED TEAM", .1);
    }
   }
   U.fillRGB(1, 1, 1);
   U.font(.02);
   //U.textR("" + V., .9, .6);
   U.text("<-LAST", .125, .5);
   U.text("NEXT->", .875, .5);
   U.font(.01);
   if (showVehicle) {
    U.text("Meshes: " + V.pieces.size(), .8);
    U.text("Vertices: " + V.vertexQuantity, .825);
   }
   U.text("Vehicles [" + (showVehicle ? "SHOW (can be slow--not recommended)" : "HIDE") + "]", .875);
   U.text("CONTINUE" + (sameVehicles ? " (with all players as " + V.vehicleName + ")" : ""), .9);
   boolean singleSelection = !inViewer && (vehiclesInMatch < 2 || modeLAN != LAN.OFF);
   if (singleSelection) {
    selected = Math.min(1, selected);
   } else {
    U.text(inViewer ? "START .OBJ-to-V.E. CONVERTER" : "SELECT NEXT VEHICLE", .925);
   }
   if (globalFlick) {
    U.strokeRGB(1, 1, 1);
    U.drawRectangle(.5, selected == 0 ? .875 : selected == 1 ? .9 : .925, width, selectionHeight);
   }
   U.fillRGB(1, 1, 1);
   if (selected == 1 && !singleSelection && !inViewer) {
    U.text(sameVehicles ? "" : "(Remaining players are picked randomly)", .95);
   }
   if (showVehicle) {
    U.font(.02);
    U.text(V.vehicleName, .15);
   } else {
    U.font(.03);
    U.text(V.vehicleName, .5);
   }
   U.font(.015);
   U.text("Made by " + vehicleMaker, .2);
   double lineLL = .1125, lineLR = .125, lineRL = 1 - lineLR, lineRR = 1 - lineLL,
   Y0 = .725, Y1 = .75, Y2 = .775, Y3 = .8, Y4 = .825, Y5 = .85;
   U.font(.00875);
   U.textR("Type: ", lineLL, Y0);
   U.textL(V.vehicleType == type.aircraft ? "Aircraft (Flying)" : V.vehicleType == type.turret ? "Turret (Fixed)" : "Vehicle (Grounded)", lineLR, Y0);
   U.textR("Top Speed:", lineLL, Y1);
   U.textL(V.vehicleType == type.turret ? "N/A" : V.topSpeeds[1] >= Long.MAX_VALUE ? "None" : V.speedBoost > 0 && V.topSpeeds[2] >= Long.MAX_VALUE ? "None (Speed Boost)" : V.speedBoost > 0 ? Math.round(V.topSpeeds[2] * units) + " " + unitSign[0] + " (Speed Boost)" : Math.round(V.topSpeeds[1] * units) + " " + unitSign[0], lineLR, Y1);
   U.textR("Acceleration Phases:", lineLL, Y2);
   U.textL(V.vehicleType == type.turret ? "N/A" : "+" + V.accelerationStages[0] + ",  +" + V.accelerationStages[1], lineLR, Y2);
   U.textR("Handling Response:", lineLL, Y3);
   U.textL(V.turnRate == Double.POSITIVE_INFINITY ? "Instant" : "" + V.turnRate, lineLR, Y3);
   U.textR("Stunt Response:", lineLL, Y4);
   U.textL(V.vehicleType == type.vehicle ? "" + (V.airAcceleration == Double.POSITIVE_INFINITY ? "Instant" : (float) V.airAcceleration) : "N/A", lineLR, Y4);
   U.textR("Special(s):", lineLL, Y5);
   U.textL(V.specials.size() > 0 ? V.specials.get(0).type + (V.specials.size() > 1 ? ", " + V.specials.get(1).type : "") : "None", lineLR, Y5);
   U.textR("Collision Damage Rating:", lineRL, Y0);
   U.textL((V.vehicleType != type.aircraft && V.damageDealt[U.random(4)] >= 100) || V.explosionType.contains("nuclear") ? "Instant-Kill" : (V.specials.size() > 0 && V.specials.get(0).type.startsWith("forcefield")) || (V.specials.size() > 1 && V.specials.get(1).type.startsWith("forcefield")) ? "'Inconsistent'" : "" + (float) ((V.damageDealt[0] + V.damageDealt[1] + V.damageDealt[2] + V.damageDealt[3]) * .25), lineRR, Y0);
   U.textR("Fragility:", lineRL, Y1);
   U.textL("" + V.fragility, lineRR, Y1);
   U.textR("Self-Repair:", lineRL, Y2);
   U.textL("" + V.selfRepair, lineRR, Y2);
   U.textR("Total Durability:", lineRL, Y3);
   U.textL("" + V.durability, lineRR, Y3);
   U.textR("Speed Boost:", lineRL, Y4);
   U.textL(V.speedBoost > 0 ? "Yes" : "No", lineRR, Y4);
   U.textR("Amphibious:", lineRL, Y5);
   U.textL(V.amphibious ? "Yes" : "No", lineRR, Y5);
   U.font(.01);
   if (selectionTimer > selectionWait) {
    if (keyUp || keyDown) {
     if (keyDown) {
      selected = ++selected > (singleSelection ? 1 : 2) ? 0 : selected;
     } else {
      selected = --selected < 0 ? (singleSelection ? 1 : 2) : selected;
     }
     U.soundPlay(sounds, "UI0", 0);
     usingKeys = true;
    }
    if (keyR) {
     removeVehicleModel();
     vehicleNumber[vehiclePick] = ++vehicleNumber[vehiclePick] >= vehicleModels.size() ? 0 : vehicleNumber[vehiclePick];
     if (vehiclePick == userPlayer) {
      userRandomRGB[0] = U.random();
      userRandomRGB[1] = U.random();
      userRandomRGB[2] = U.random();
     }
     addVehicleModel(vehicleNumber[vehiclePick], showVehicle);
     U.soundPlay(sounds, "UI0", 0);
    }
    if (keyL) {
     removeVehicleModel();
     vehicleNumber[vehiclePick] = --vehicleNumber[vehiclePick] < 0 ? vehicleModels.size() - 1 : vehicleNumber[vehiclePick];
     if (vehiclePick == userPlayer) {
      userRandomRGB[0] = U.random();
      userRandomRGB[1] = U.random();
      userRandomRGB[2] = U.random();
     }
     addVehicleModel(vehicleNumber[vehiclePick], showVehicle);
     U.soundPlay(sounds, "UI0", 0);
    }
    if (keySpace || keyEnter) {
     if (inViewer && selected == 2) {
      new MainFrame().setVisible(true);
     } else {
      removeVehicleModel();
      if (selected < 1) {
       showVehicle = !showVehicle;
       addVehicleModel(vehicleNumber[vehiclePick], showVehicle);
      } else {
       if (modeLAN == LAN.OFF) {
        vehiclePick++;
        if (vehiclePick < vehiclesInMatch) {
         addVehicleModel(vehicleNumber[vehiclePick], showVehicle);
        }
       }
       if (vehiclePick > (vehiclesInMatch * (tournament > 0 ? .5 : 1)) - 1 || selected == 1) {
        if (inViewer) {
         event = event.vehicleViewer;
         section = 0;
        } else if (modeLAN != LAN.OFF) {
         readyLAN[userPlayer] = waitingLAN = true;
        } else {
         event = event.mapJump;
         if (sameVehicles) {
          for (n = vehicleNumber.length; --n > 0; ) {
           vehicleNumber[n] = vehicleNumber[0];
          }
         } else {
          for (n = vehiclePick; n < vehiclesInMatch; n++) {
           vehicleNumber[n] = U.random(vehicleModels.size());
          }
         }
        }
       }
      }
     }
     U.soundPlay(sounds, "UI1", 0);
     keySpace = keyEnter = false;
    }
   }
  }
  if (keyEscape) {
   escapeToLast(true);
  }
  if (!usingKeys) {
   selected = Math.abs(.85 - mouseY) < clickRangeY ? 0 : Math.abs(.875 - mouseY) < clickRangeY ? 1 : Math.abs(.9 - mouseY) < clickRangeY ? 2 : selected;
  }
 }

 private void preMatchCommunicationLAN() {
  boolean gamePlay = event == event.play || event == event.replay;
  if (modeLAN != LAN.OFF) {
   int n;
   if (modeLAN == LAN.HOST) {
    if (timer <= 0) {
     for (PrintWriter PW : outLAN) {
      PW.println("Vehicle0(" + vehicles.get(0).vehicleName);
     }
     if (gamePlay) {
      for (PrintWriter PW : outLAN) {
       PW.println("Map(" + mapName);
      }
      if (waitingLAN) {
       for (PrintWriter PW : outLAN) {
        PW.println("Ready0");
       }
      }
     }
    }
    for (n = vehiclesInMatch; --n > 0; ) {
     String s = U.readInLAN(n - 1);
     if (s.startsWith("CANCEL")) {
      escapeToLast(false);
     } else if (s.startsWith("Vehicle(")) {
      vehicleNumber[n] = getVehicleName(U.getString(s, 0));
      if (vehiclesInMatch > 2) {
       for (PrintWriter out : outLAN) {
        out.println("Vehicle" + n + "(" + U.getString(s, 0));
       }
      }
     } else if (gamePlay && s.startsWith("Ready")) {
      readyLAN[n] = true;
      if (vehiclesInMatch > 2) {
       for (PrintWriter out : outLAN) {
        out.println("Ready" + n);
       }
      }
     }
    }
   } else {
    if (timer <= 0) {
     outLAN.get(0).println("Vehicle(" + vehicles.get(0).vehicleName);
     if (gamePlay && waitingLAN) {
      outLAN.get(0).println("Ready");
     }
    }
    String s = U.readInLAN(0);
    if (s.startsWith("CANCEL")) {
     escapeToLast(false);
    } else if (event == event.mapJump && s.startsWith("Map(")) {
     map = getMapName(U.getString(s, 0));
     event = event.mapLoadPass0;
    } else if (gamePlay) {
     for (n = vehiclesInMatch; --n >= 0; ) {
      readyLAN[n] = s.startsWith("Ready" + n) || readyLAN[n];
     }
    }
   }
  }
 }

 private void addVehicleModel(int v, boolean show) {
  vehicles.clear();
  vehicles.add(new Vehicle(v, vehicleModels, 0, false, show));
  vehicles.get(0).lightBrightness = defaultVehicleLightBrightness;
  for (VehiclePiece piece : vehicles.get(0).pieces) {
   U.add(piece.MV);
   piece.MV.setVisible(true);
  }
 }

 private void removeVehicleModel() {
  if (vehicles.size() > 0 && vehicles.get(0) != null) {
   for (VehiclePiece piece : vehicles.get(0).pieces) {
    U.remove(piece.MV);
    //piece.MV.setVisible(false);<-Check
    U.remove(piece.pointLight);
   }
  }
 }

 private void vehicleViewer(boolean gamePlay) {
  U.font(.03);
  U.fillRGB(1, 1, 1);
  U.text("Vehicle Viewer", .075);
  boolean loadModel = false;
  if (section < 1) {
   clearGraphics();
   U.remove(E.sunlight, E.sun, E.ground);
   scene3D.setFill(Color.color(0, 0, 0));
   U.setLightRGB(E.sunlight, 1, 1, 1);
   viewableMapDistance = cameraX = cameraY = cameraZ = cameraXZ = cameraYZ = cameraXY = viewerY = viewerYZ = 0;
   U.rotate(camera, cameraYZ, -cameraXZ);
   cameraRotateXY.setAngle(0);
   viewerZ = 1000;
   viewerXZ = 180;
   showCollisionBounds = false;//<-Covers vehicle otherwise
   loadModel = true;
   U.setLightRGB(E.ambientLight, 1, 1, 1);
   E.sunlight.setTranslateX(0);
   E.sunlight.setTranslateZ(0);
   E.sunlight.setTranslateY(-Long.MAX_VALUE);
   if (vehicleViewer3DLighting) {
    U.setLightRGB(E.ambientLight, .5, .5, .5);
    U.add(E.sunlight);
   }
   section = 1;
  }
  viewerXZ += keyL ? 5 : 0;
  viewerXZ -= keyR ? 5 : 0;
  viewerYZ -= keyUp ? 5 : 0;
  viewerYZ += keyDown ? 5 : 0;
  viewerY += viewerHeight * tick;
  viewerZ += viewerDepth * tick;
  if (vehicles.get(0) != null) {
   vehicles.get(0).Y = viewerY;
   vehicles.get(0).Z = viewerZ;
   vehicles.get(0).XZ = viewerXZ;
   vehicles.get(0).YZ = viewerYZ;
   vehicles.get(0).thrusting = (timer <= 0) != vehicles.get(0).thrusting;
   vehicles.get(0).processGraphics(gamePlay);
   U.font(.02);
   U.text(vehicles.get(0).vehicleName, .1125);
   U.font(.0125);
   U.fillRGB(1, 1, 1);
   U.text("Meshes: " + vehicles.get(0).pieces.size(), .25, .8);
   U.text("Vertices: " + vehicles.get(0).vertexQuantity, .75, .8);
   if (showCollisionBounds) {
    U.setTranslate(collisionBoundSphere, vehicles.get(0).X, vehicles.get(0).Y, vehicles.get(0).Z);
    U.add(collisionBoundSphere);
   } else {
    U.remove(collisionBoundSphere);
   }
  }
  U.fillRGB(1, 1, 1);
  U.text("Move Vehicle with the T, G, U, and J Keys. Rotate with the Arrow Keys", .95);
  if (globalFlick) {
   U.strokeRGB(1, 1, 1);
   U.drawRectangle(.5, selected == 0 ? .825 : selected == 1 ? .85 : selected == 2 ? .875 : .9, width, selectionHeight);
  }
  U.text("RE-LOAD VEHICLE FILE", .825);
  U.text("3D Lighting [" + (vehicleViewer3DLighting ? "ON" : "OFF") + "]", .85);
  U.text("Collision Bounds [" + (showCollisionBounds ? "SHOW" : "HIDE") + "]", .875);
  U.text("BACK TO MAIN MENU", .9);
  if (selectionTimer > selectionWait) {
   if (keyUp) {
    selected = --selected < 0 ? 3 : selected;
    usingKeys = true;
    U.soundPlay(sounds, "UI0", 0);
   }
   if (keyDown) {
    selected = ++selected > 3 ? 0 : selected;
    usingKeys = true;
    U.soundPlay(sounds, "UI0", 0);
   }
  }
  if (keySpace || keyEnter) {
   if (selected == 0) {
    loadModel = true;
   } else if (selected == 1) {
    vehicleViewer3DLighting = !vehicleViewer3DLighting;
    if (vehicleViewer3DLighting) {
     U.setLightRGB(E.ambientLight, .5, .5, .5);
     U.add(E.sunlight);
    } else {
     U.setLightRGB(E.ambientLight, 1, 1, 1);
     U.remove(E.sunlight);
    }
   } else if (selected == 2) {
    showCollisionBounds = !showCollisionBounds;
    collisionBoundSphere.setRadius(vehicles.get(0).collisionRadius);
   } else {
    event = event.mainMenu;
    removeVehicleModel();
   }
   U.soundPlay(sounds, "UI1", 0);
   keySpace = keyEnter = false;
  }
  if (keyEscape) {
   event = event.mainMenu;
   removeVehicleModel();
   U.soundPlay(sounds, "UI1", 0);
   keyEscape = false;
  }
  if (loadModel) {
   removeVehicleModel();
   userRandomRGB[0] = U.random();
   userRandomRGB[1] = U.random();
   userRandomRGB[2] = U.random();
   addVehicleModel(vehicleNumber[0], true);
  }
  if (!usingKeys) {
   selected = Math.abs(.8 - mouseY) < clickRangeY ? 0 : Math.abs(.825 - mouseY) < clickRangeY ? 1 : Math.abs(.85 - mouseY) < clickRangeY ? 2 : Math.abs(.875 - mouseY) < clickRangeY ? 3 : selected;
  }
 }

 private void mapViewer() {
  U.font(.03);
  U.fillRGB(1, 1, 1);
  U.text("Map Viewer", .075);
  if (section < 1) {
   cameraX = cameraZ = cameraXZ = cameraYZ = cameraXY = 0;
   viewerY = -5000;
   section = 1;
  }
  cameraXZ -= keyL ? 5 : 0;
  cameraXZ += keyR ? 5 : 0;
  cameraYZ += keyUp ? 5 : 0;
  cameraYZ -= keyDown ? 5 : 0;
  viewerY += viewerHeight * tick;
  cameraY = viewerY;
  cameraZ += viewerDepth * U.cos(cameraXZ) * tick;
  cameraX += viewerDepth * U.sin(cameraXZ) * tick;
  U.rotate(camera, cameraYZ, -cameraXZ);
  cameraRotateXY.setAngle(-cameraXY);
  if (!group.getChildren().contains(E.sunlight)) {
   U.add(E.mapViewerLight);
   U.setTranslate(E.mapViewerLight, cameraX, cameraY, cameraZ);
  }
  manageEnvironment();
  for (TrackPart trackPart : trackParts) {
   trackPart.processGraphics();
  }
  U.fillRGB(0, 0, 0, .5);
  U.fillRectangle(.5, .9, 1, .2);
  U.font(.015);
  if (globalFlick) {
   U.strokeRGB(1, 1, 1);
   U.drawRectangle(.5, selected == 0 ? .85 : .875, width, selectionHeight);
  }
  U.fillRGB(1, 1, 1);
  U.text("Move Camera with the T, G, U, and J Keys. Rotate with the Arrow Keys", .95);
  U.text("RE-LOAD MAP FILE", .85 + textOffset);
  U.text("BACK TO MAIN MENU", .875 + textOffset);
  if (selectionTimer > selectionWait && (keyUp || keyDown)) {
   selected = selected < 1 ? 1 : 0;
   usingKeys = true;
   U.soundPlay(sounds, "UI0", 0);
  }
  if (keySpace || keyEnter) {
   event = selected == 0 ? event.mapLoadPass0 : event.mainMenu;
   U.soundPlay(sounds, "UI1", 0);
   keySpace = keyEnter = false;
   clearSounds();
  }
  if (keyEscape) {
   event = event.mainMenu;
   U.soundPlay(sounds, "UI1", 0);
   keyEscape = false;
   clearSounds();
  }
  manageBonus();
  if (!usingKeys) {
   selected = Math.abs(.825 - mouseY) < clickRangeY ? 0 : Math.abs(.85 - mouseY) < clickRangeY ? 1 : selected;
  }
 }

 private void mapError() {
  scene.setCursor(Cursor.CROSSHAIR);
  U.fillRGB(0, 0, 0, .75);
  U.fillRectangle(.5, .5, 1, 1);
  U.font(.03);
  U.fillRGB(1, 0, 0);
  U.text("Error Loading This Map", .475);
  U.fillRGB(1, 1, 1);
  U.text("<-LAST", .125, .75);
  U.text("NEXT->", .875, .75);
  U.text("CONTINUE", .875);
  U.text("Hit Continue or Enter to try again", .525);
  U.font(.01);
  U.text("You can also use the Arrow Keys and Enter to navigate.", .95);
  if (keySpace || keyEnter) {
   event = event.mapLoadPass0;
   keySpace = keyEnter = false;
   U.soundPlay(sounds, "UI1", 0);
  }
  if (keyR) {
   map = ++map >= maps.size() ? 0 : map;
   event = event.mapJump;
   keyR = false;
   U.soundPlay(sounds, "UI0", 0);
  }
  if (keyL) {
   map = --map < 0 ? maps.size() - 1 : map;
   event = event.mapJump;
   keyL = false;
   U.soundPlay(sounds, "UI0", 0);
  }
  gameFPS = U.refreshRate * .25;
  if (keyEscape) {
   escapeToLast(true);
  }
 }

 private void mapView() {
  scene.setCursor(Cursor.CROSSHAIR);
  U.font(.015);
  U.fillRGB(E.groundInverse);
  U.text("<-LAST", .2, .75);
  U.text("NEXT->", .8, .75);
  U.text("CONTINUE", .75);
  U.font(.01);
  U.text("You can also use the Arrow Keys and Enter to navigate.", .95);
  U.fillRGB(E.skyInverse);
  U.font(.02);
  U.text("| " + mapName + " |", .15);
  preMatchCommunicationLAN();
  if (keySpace || keyEnter || tournament > 0) {
   event = event.play;
   if (tournament < 1) {
    U.soundPlay(sounds, "UI1", 0);
   }
   cameraView = "flow";
   keySpace = keyEnter = false;
  } else if (keyR || keyL) {
   if (keyL) {
    map = --map < 0 ? maps.size() - 1 : map;
   }
   if (keyR) {
    map = ++map >= maps.size() ? 0 : map;
   }
   event = event.mapJump;
   U.soundPlay(sounds, "UI0", 0);
   for (Vehicle vehicle : vehicles) {
    vehicle.closeSounds();
   }
  }
  if (keyEscape) {
   escapeToLast(true);
  }
 }

 private void escapeToLast(boolean wasUser) {
  if (modeLAN != LAN.OFF) {
   for (PrintWriter PW : outLAN) {
    PW.println("CANCEL");
    PW.println("CANCEL");
   }
   event = event.loadLAN;
  } else {
   event = event.mainMenu;
  }
  modeLAN = LAN.OFF;
  section = tournament = 0;
  if (wasUser) {
   U.soundPlay(sounds, "UI1", 0);
  }
  keyEscape = runLANLoadThread = false;
  for (Vehicle vehicle : vehicles) {
   vehicle.closeSounds();
  }
 }

 private void mapJump() {
  if (modeLAN == LAN.JOIN) {
   U.font(.03);
   U.text("..Please Wait for " + playerNames[0] + " to Select Map..", .5, .5);
  } else {
   String mapMaker;
   mapName = mapMaker = "";
   map = tournament > 0 ? U.random(maps.size()) : map;
   String s;
   try (BufferedReader BR = new BufferedReader(new InputStreamReader(U.getMapFile(map)))) {
    for (String s1; (s1 = BR.readLine()) != null; ) {
     s = "" + s1.trim();
     mapName = s.startsWith("name") ? U.getString(s, 0) : mapName;
     mapMaker = s.startsWith("maker") ? U.getString(s, 0) : mapMaker;
    }
   } catch (Exception e) {
    event = event.mapError;
    e.printStackTrace();
   }
   if (tournament > 0) {
    event = event.mapLoadPass0;
   } else {
    U.fillRGB(0, 0, 0, .75);
    U.fillRectangle(.5, .5, 1, 1);
    U.fillRGB(1, 1, 1);
    U.font(.05);
    U.text("" + mapName, .5);
    U.font(.03);
    U.text(inViewer ? "SELECT MAP TO EDIT:" : "SELECT MAP:", .25);
    U.text("<-LAST", .125, .75);
    U.text("NEXT->", .875, .75);
    U.text("CONTINUE", .875);
    U.font(.02);
    U.text("Made by " + mapMaker, .6);
    U.font(.01);
    U.text("You can also use the Arrow Keys and Enter to navigate.", .95);
    if (selectionTimer > selectionWait) {
     if (keyR) {
      map = ++map >= maps.size() ? 0 : map;
      U.soundPlay(sounds, "UI0", 0);
     }
     if (keyL) {
      map = --map < 0 ? maps.size() - 1 : map;
      U.soundPlay(sounds, "UI0", 0);
     }
     if (keyEnter || keySpace) {
      event = event.mapLoadPass0;
      U.soundPlay(sounds, "UI1", 0);
     }
    }
    gameFPS = U.refreshRate * .5;
   }
  }
  preMatchCommunicationLAN();
  if (keyEscape) {
   escapeToLast(true);
  }
 }

 private void howToPlay() {
  section = Math.max(section, 1);
  U.fillRGB(0, 0, 0, .75);
  U.fillRectangle(.5, .5, 1, 1);
  U.fillRGB(1, 1, 1);
  U.font(.03);
  U.text("<-LAST", .1, .75);
  U.text("NEXT->", .9, .75);
  U.text("RETURN", .5, .95);
  if (section == 1) {
   U.font(.03);
   U.text("Vehicle Controls", .2);
   U.font(.02);
   U.text("Welcome to the Vehicular Epic--the EPIC vehicle emulator!", .1);
   double keySizeX = .04, keySizeY = .06;
   U.fillRectangle(.75, .375, keySizeX, keySizeY);
   U.fillRectangle(.75, .45, keySizeX, keySizeY);
   U.fillRectangle(.7, .45, keySizeX, keySizeY);
   U.fillRectangle(.8, .45, keySizeX, keySizeY);
   U.fillRectangle(.25, .45, keySizeX * 8, keySizeY);
   U.fillRectangle(.175, .3, keySizeX, keySizeY);
   U.fillRectangle(.2, .375, keySizeX, keySizeY);
   U.fillRectangle(.25, .375, keySizeX, keySizeY);
   U.fillRectangle(.5, .3, keySizeX, keySizeY);
   U.fillRectangle(.55, .3, keySizeX, keySizeY);
   U.fillRGB(0, 0, 0);
   U.text("UP", .75, .375);
   U.font(.0125);
   U.text("DOWN", .75, .45);
   U.text("LEFT", .7, .45);
   U.text("RIGHT", .8, .45);
   U.text("SPACE", .25, .45);
   U.text("F", .175, .3);
   U.text("V", .2, .375);
   U.text("B", .25, .375);
   U.text("-", .5, .3);
   U.text("+", .55, .3);
   U.fillRGB(1, 1, 1);
   U.text("While driving on the GROUND, Spacebar is the Handbrake", .5);
   U.text("While AIRBORNE, Spacebar enables aerial control of the vehicle, which can include Stunts and/or Flying", .525);
   U.text("(If you're on the ground and using a flying vehicle, press Spacebar + Down Arrow to Take-off at any time)", .55);
   U.text("When flying, hold Spacebar to yaw-steer instead of steer by banking", .575);
   U.text("For TURRET vehicles, Spacebar enables finer Precision for Aiming", .6);
   U.text("B = Boost Speed/Change Aerial Velocity (if available)", .625);
   U.text("V and/or F = Use weapon(s)/specials if your vehicle has them", .65);
   U.text("+ and - = Adjust Vehicle Light Brightness", .675);
   U.fillRGB(.5, 1, .5);
   U.text("----------Cursor Controls----------", .75);
   U.fillRGB(1, 1, 1);
   U.text("Raise the cursor to go forward, lower it to Reverse", .775);
   U.text("Move the Cursor Left and Right to Turn", .8);
   U.text("Click to engage Handbrake/perform Stunts", .825);
  } else if (section == 2) {
   U.font(.03);
   U.text("Game Objectives", .125);
   U.font(.0125);
   U.text("The primary objective in this game is to Maximize your (team's) score.", .2);
   U.text("There are several ways to do this:", .225);
   U.text("Checkpoints--how many checkpoints you/your team passed through", .275);
   U.text("Laps--completing more laps than the opposition can be the key to winning", .3);
   U.text("Stunts--landing stunts can be supported by most vehicles (except turrets)", .325);
   U.text("Damage Dealt--How much damage is dealt to the opposition", .35);
   U.text("Kills--How many opposing vehicles were destroyed by you/your team", .375);
   U.text("And last but not least--the Bonus", .4);
   U.fillRGB(U.random(), U.random(), U.random());
   graphicsContext.fillOval((width * .5) - 50, (height * .5) - 50, 100, 100);
   U.fillRGB(1, 1, 1);
   U.text("Grab the bonus by driving into it--turrets can also get the bonus by shooting it.", .6);
   U.text("Being in possession of the Bonus when time's up will DOUBLE you/your team's score!", .625);
   U.text("All these factors get multiplied together. When time's up, the player/team with the higher score wins!", .65);
   U.text("(Some values are handled in scientific notation for brevity)", .675);
   U.text("The user is always on the Green team--except in Multiplayer Games.", .7);
  } else if (section == 3) {
   U.font(.0125);
   U.text("Other Important Information", .125);
   U.text("Based on the given circumstances, pick the best strategy (race, fight, etc.)", .175);
   U.text("Press 'A' to toggle the guidance arrow between pointing to the Vehicles or Racetrack", .2);
   U.text("Your vehicle will revive shortly after being destroyed.", .25);
   U.text("However, you can Fix it before then by passing through an Electrified Ring/Diamond", .275);
   U.text("(Fixing the vehicle is not possible on all maps).", .3);
   U.text("It's important to note that vehicles on the same team don't 'interact',", .35);
   U.text("so there's no need to worry about crashing into your own team members, friendly fire, etc.", .375);
   U.fillRGB(1, 1, .5);
   U.text("Not all maps have checkpoints and a designated route.", .45);
   U.text("You'll need to score points using methods besides checkpoints and laps", .475);
   U.text("such as good stunts, fighting opponents, or keeping the bonus.", .5);
   U.fillRGB(0, 1, 1);
   U.text("Some Maps have special environments or may be less straightforward.", .55);
   U.text("There may be an extra learning curve to such maps.", .575);
   U.fillRGB(1, 1, 1);
   U.text("Some vehicles have Guided weaponry.", .625);
   U.text("When fired, these weapons will intercept the nearest opponent automatically.", .65);
  } else if (section == 4) {
   U.font(.0125);
   U.text("Other Key Controls:", .15);
   U.text("Digits 1-7 = Camera Views", .25);
   U.text("Z or X = To look around/behind you while driving (for Views 1-4)", .275);
   U.fillRGB(.5, 1, .5);
   U.text("(Press Z and X simultaneously to look forward again)", .3);
   U.fillRGB(1, 1, 1);
   U.text("Enter or Escape = Pause/exit out of Match", .325);
   U.text("M = Mute Sound", .35);
   U.text("< and > = Music Volume", .375);
   U.text("Control and Shift = Adjust Zoom", .4);
   U.fillRGB(.5, 1, .5);
   U.text("(Press Control and Shift simultaneously to restore Zoom)", .425);
   U.fillRGB(1, 1, 1);
   U.text("S or D = Change Player Perspective (See what the other players are doing)", .45);
   U.fillRGB(.5, 1, .5);
   U.text("(Press S and D simultaneously to view yourself again)", .475);
   U.fillRGB(1, 1, 1);
   U.text("H = Heads-up Display ON/OFF", .5);
   U.text("L = Destruction Log ON/OFF", .525);
   U.text("R = Show/Hide Frames-Per-Second", .55);
   U.text("There are many other aspects not covered here in these instructions,", .7);
   U.text("but you will learn with experience.", .725);
   U.text("GOOD LUCK", .75);
  }
  if (selectionTimer > selectionWait) {
   if (keyR) {
    if (++section > 4) {
     section = 0;
     event = lastEvent;
    }
    U.soundPlay(sounds, "UI0", 0);
   }
   if (keyL) {
    if (--section < 1) {
     section = 0;
     event = lastEvent;
    }
    U.soundPlay(sounds, "UI0", 0);
   }
   if (keyEnter) {
    section = 0;
    event = lastEvent;
    U.soundPlay(sounds, "UI1", 0);
    keyEnter = false;
   }
  }
  if (keyEscape) {
   section = 0;
   event = lastEvent;
   U.soundPlay(sounds, "UI1", 0);
   keyEscape = false;
  }
  gameFPS = U.refreshRate * .25;
 }

 private void credits() {
  if (section < 1) {
   U.soundPlay(sounds, "finish" + (U.random() < .5 ? "0" : "1"), 0);
   section = 1;
  }
  U.fillRGB(0, 0, 0);
  U.fillRectangle(.5, .5, 1, 1);
  Image RA = U.getImage("RA");
  graphicsContext.drawImage(RA, width * .2 - (RA.getWidth() * .5), height * .5 - (RA.getHeight() * .5));
  graphicsContext.drawImage(RA, width * .8 - (RA.getWidth() * .5), height * .5 - (RA.getHeight() * .5));
  if (section == 1) {
   creditsQuantity = Math.round(creditsQuantity);
   creditsQuantity += creditsDirection ? -1 : 1;
   creditsDirection = !(creditsQuantity < 2) && (creditsQuantity > 12 || creditsDirection);
   U.font(.075);
   U.fillRGB(.5, .5, .5);
   if (creditsQuantity == 1) {
    U.fillRGB(1, 1, 1);
   }
   U.text("THE VEHICULAR EPIC", .15);
   U.font(.015);
   U.fillRGB(.5, .5, .5);
   if (creditsQuantity == 2) {
    U.fillRGB(1, 1, 1);
   }
   U.text("an open-source project maintained by", .2);
   U.fillRGB(.5, .5, .5);
   if (creditsQuantity == 3) {
    U.fillRGB(1, 1, 1);
   }
   U.text("Ryan Albano", .25);
   U.fillRGB(.5, .5, .5);
   if (creditsQuantity == 4) {
    U.fillRGB(1, 1, 1);
   }
   U.text("Other Credits:", .35);
   U.fillRGB(.5, .5, .5);
   if (creditsQuantity == 5) {
    U.fillRGB(1, 1, 1);
   }
   U.text("Vitor Macedo (VitorMac) and Dany Fernández Diaz--for programming assistance", .4);
   U.fillRGB(.5, .5, .5);
   if (creditsQuantity == 6) {
    U.fillRGB(1, 1, 1);
   }
   U.text("Max Place--for composing some map soundtracks", .45);
   U.fillRGB(.5, .5, .5);
   if (creditsQuantity == 7) {
    U.fillRGB(1, 1, 1);
   }
   U.text("VitorMac (again!)--for making the .OBJ-to-V.E. Converter possible", .5);
   U.fillRGB(.5, .5, .5);
   if (creditsQuantity == 8) {
    U.fillRGB(1, 1, 1);
   }
   U.text("Rory McHenry--for teaching IDE/Java basics", .55);
   U.fillRGB(.5, .5, .5);
   if (creditsQuantity == 9) {
    U.fillRGB(1, 1, 1);
   }
   U.text("The OpenJavaFX team/community--for their hard work making V.E.'s graphics engine possible", .6);
   U.fillRGB(.5, .5, .5);
   if (creditsQuantity == 10) {
    U.fillRGB(1, 1, 1);
   }
   U.text("The FXyz library--for additional shape/geometry support", .65);
   U.fillRGB(.5, .5, .5);
   if (creditsQuantity == 11) {
    U.fillRGB(1, 1, 1);
   }
   U.text("Everyone who suggested or submitted content!", .7);
   U.font(.03);
   U.fillRGB(.5, .5, .5);
   if (creditsQuantity == 12) {
    U.fillRGB(1, 1, 1);
   }
   U.text("And thank YOU for playing", .85);
   U.fillRGB(.5, .5, .5);
   if (creditsQuantity == 13) {
    U.fillRGB(1, 1, 1);
   }
   U.text("and supporting independent gaming!", .9);
  } else if (section == 2) {
   creditsQuantity *= creditsDirection ? .99 : 1.01;
   if (creditsQuantity < 2) {
    creditsDirection = false;
    creditsQuantity = 2;
   } else if (creditsQuantity > 2000) {
    creditsDirection = true;
   }
   double[] clusterX = new double[(int) creditsQuantity],
   clusterY = new double[(int) creditsQuantity];
   for (int n = (int) creditsQuantity; --n >= 0; ) {
    clusterX[n] = (width * .5) + Math.pow(U.random(90000000000.), .25) - Math.pow(U.random(90000000000.), .25);
    clusterY[n] = (height * .5) + Math.pow(U.random(60000000000.), .25) - Math.pow(U.random(60000000000.), .25);
   }
   U.fillRGB(1, 1, 1);
   graphicsContext.fillPolygon(clusterX, clusterY, (int) creditsQuantity);
   U.font(.05);
   U.fillRGB(0, 0, 0);
   U.text("VEHICULAR", .45);
   U.text("EPIC", .55);
  }
  if (selectionTimer > selectionWait) {
   if (keyL) {
    if (--section < 1) {
     section = 0;
     event = event.mainMenu;
    }
    U.soundPlay(sounds, "UI0", 0);
   }
   if (keyR) {
    if (++section > 2) {
     section = 0;
     event = event.mainMenu;
    }
    U.soundPlay(sounds, "UI0", 0);
   }
   if (keyEnter || keySpace) {
    section = 0;
    event = event.mainMenu;
    keyEnter = keySpace = false;
    U.soundPlay(sounds, "UI1", 0);
   }
  }
  if (keyEscape) {
   section = 0;
   event = event.mainMenu;
   keyEscape = false;
   U.soundPlay(sounds, "UI1", 0);
  }
  U.fillRGB(1, 1, 1);
  U.font(.03);
  U.text("<-LAST", .1, .75);
  U.text("NEXT->", .9, .75);
 }

 private void paused() {
  boolean ending = false;
  if (selectionTimer > selectionWait) {
   if (keyUp) {
    selected = --selected < 0 ? 4 : selected;
    usingKeys = true;
    U.soundPlay(sounds, "UI0", 0);
   }
   if (keyDown) {
    selected = ++selected > 4 ? 0 : selected;
    usingKeys = true;
    U.soundPlay(sounds, "UI0", 0);
   }
  }
  if (keyEnter || keySpace) {
   if (selected == 0) {
    event = event.play;
   } else if (selected == 1) {
    event = event.replay;
    for (Recorder.recordFrame = Recorder.gameFrame - (int) Recorder.recorded; Recorder.recordFrame < 0; Recorder.recordFrame += Recorder.totalFrames)
     ;
    Recorder.recordingsCount = 0;
   } else if (selected == 2) {
    event = event.optionsMatch;
   } else if (selected == 3) {
    lastEvent = event.paused;
    event = event.howToPlay;
   } else if (selected == 4) {
    ending = true;
   }
   U.soundPlay(sounds, "UI1", 0);
   keyEnter = keySpace = false;
  }
  if (keyEscape) {
   ending = true;
   U.soundPlay(sounds, "UI1", 0);
   keyEscape = false;
  }
  if (ending) {
   if (modeLAN == LAN.OFF) {
    scene.setCursor(Cursor.WAIT);
    if (tournament > 0 && matchTime <= 0 && !tournamentOver) {
     event = event.mapJump;
     tournament++;
    } else {
     event = event.mainMenu;
     tournament = 0;
    }
    lastCameraView = cameraView;
    selected = 0;
    clearSounds();
   } else {
    int n;
    if (modeLAN == LAN.HOST) {
     for (PrintWriter PW : outLAN) {
      PW.println("END");
      PW.println("END");
     }
    }
    if (hostLeftMatch || modeLAN == LAN.HOST) {
     scene.setCursor(Cursor.WAIT);
     for (n = maxPlayersLAN; --n >= 0; ) {
      runLANGameThread[n] = false;
     }
     event = event.mainMenu;
     lastCameraView = cameraView;
     selected = 0;
     clearSounds();
     try {
      if (serverSocket != null) {
       serverSocket.close();
      }
      if (clientSocket != null) {
       clientSocket.close();
      }
      for (BufferedReader in : inLAN) {
       in.close();
      }
      for (PrintWriter PW : outLAN) {
       PW.close();
      }
     } catch (IOException e) {
      e.printStackTrace();
     }
    }
   }
  }
  double c = globalFlick ? .5 : 0;
  U.fillRGB(c, c, c);
  if (selected == 0) {
   U.fillRectangle(.5, .45, .2, selectionHeight);
  } else if (selected == 1) {
   U.fillRectangle(.5, .475, .2, selectionHeight);
  } else if (selected == 2) {
   U.fillRectangle(.5, .5, .2, selectionHeight);
  } else if (selected == 3) {
   U.fillRectangle(.5, .525, .2, selectionHeight);
  } else if (selected == 4) {
   U.fillRectangle(.5, .55, .2, selectionHeight);
  }
  U.font(.03);
  U.fillRGB(0, 0, 0);
  U.text("MATCH PAUSED", .374);
  U.fillRGB(1, 1, 1);
  U.text("MATCH PAUSED", .375);
  U.font(.015);
  double extraY = .01;
  U.text("RESUME", .45 + extraY);
  U.text("REPLAY", .475 + extraY);
  U.text("OPTIONS", .5 + extraY);
  U.text("HOW TO PLAY", .525 + extraY);
  U.text(tournament > 0 ? (matchTime > 0 ? "CANCEL TOURNAMENT" : tournamentOver ? "BACK TO MAIN MENU" : "NEXT ROUND") : modeLAN == LAN.JOIN && !hostLeftMatch ? "Please Wait for Host to exit Match first" : "END MATCH", .55 + extraY);
  if (!usingKeys) {
   selected =
   Math.abs(.45 + clickOffset - mouseY) < clickRangeY ? 0 :
   Math.abs(.475 + clickOffset - mouseY) < clickRangeY ? 1 :
   Math.abs(.5 + clickOffset - mouseY) < clickRangeY ? 2 :
   Math.abs(.525 + clickOffset - mouseY) < clickRangeY ? 3 :
   Math.abs(.55 + clickOffset - mouseY) < clickRangeY ? 4 : selected;
  }
 }

 private void mainMenu() {
  boolean loaded = initialization.isEmpty();
  scene.setCursor(!loaded ? Cursor.WAIT : Cursor.CROSSHAIR);
  if (loaded) {
   tournamentWins[0] = tournamentWins[1] = userPlayer = 0;
   modeLAN = LAN.OFF;
   tournament = tournamentOver ? 0 : tournament;
   tournamentOver = waitingLAN = inViewer = false;
   arrow.setVisible(false);
   if (selectionTimer > selectionWait) {
    if (keyUp) {
     selected = --selected < 0 ? 6 : selected;
     usingKeys = true;
     U.soundPlay(sounds, "UI0", 0);
    }
    if (keyDown) {
     selected = ++selected > 6 ? 0 : selected;
     usingKeys = true;
     U.soundPlay(sounds, "UI0", 0);
    }
   }
   double C = selected == 0 && globalFlick ? 1 : 0;
   U.fillRGB(C, C, C);
   U.fillRectangle(.5, .6, .2, selectionHeight);
   C = selected == 1 && globalFlick ? 1 : 0;
   U.fillRGB(C, C, C);
   U.fillRectangle(.5, .65, .2, selectionHeight);
   U.fillRGB(0, selected == 2 && globalFlick ? 1 : 0, 0);
   U.fillRectangle(.5, .7, .2, selectionHeight);
   U.fillRGB(selected == 3 && globalFlick ? 1 : 0, 0, 0);
   U.fillRectangle(.5, .75, .2, selectionHeight);
   U.fillRGB(0, 0, selected == 4 && globalFlick ? 1 : 0);
   U.fillRectangle(.5, .8, .2, selectionHeight);
   C = selected == 5 && globalFlick ? .5 : 0;
   U.fillRGB(C, C, C);
   U.fillRectangle(.5, .85, .2, selectionHeight);
   C = selected == 6 && globalFlick ? .5 : 0;
   U.fillRGB(C, C, C);
   U.fillRectangle(.5, .9, .2, selectionHeight);
   if (keyEnter || keySpace) {
    if (selected == 0) {
     event = event.vehicleSelect;
     selected = vehiclePick = 0;
    } else if (selected == 1) {
     event = event.loadLAN;
     clearGraphics();
     selected = 0;
    } else if (selected == 2) {
     lastEvent = event.mainMenu;
     event = event.howToPlay;
    } else if (selected == 3) {
     event = event.credits;
    } else if (selected == 4) {
     event = event.optionsMenu;
     selected = 0;
    } else if (selected == 5) {
     event = event.vehicleSelect;
     vehiclesInMatch = 1;
     vehiclePick = 0;
     inViewer = true;
    } else if (selected == 6) {
     event = event.mapJump;
     inViewer = true;
    }
    U.soundPlay(sounds, "UI1", 0);
    section = 0;
    keyEnter = keySpace = false;
    error = "";
   }
  }
  if (keyEscape) {
   System.exit(0);
  }
  U.font(.075);
  U.fillRGB(.5, .5, .5);
  U.text("THE VEHICULAR EPIC", .498, .173);
  U.text("THE VEHICULAR EPIC", .502, .173);
  U.text("THE VEHICULAR EPIC", .498, .177);
  U.text("THE VEHICULAR EPIC", .502, .177);
  U.fillRGB(1, 1, 1);
  U.text("THE VEHICULAR EPIC", .499, .174);
  U.text("THE VEHICULAR EPIC", .501, .174);
  U.text("THE VEHICULAR EPIC", .499, .176);
  U.text("THE VEHICULAR EPIC", .501, .176);
  U.fillRGB(.75, .75, .75);
  U.text("THE VEHICULAR EPIC", .175);
  U.font(.015);
  U.fillRGB(1, 1, 1);
  if (loaded) {
   U.text("NEW GAME", .6 + textOffset);
   U.text("MULTIPLAYER GAME", .65 + textOffset);
   U.text("HOW TO PLAY", .7 + textOffset);
   U.text("CREDITS", .75 + textOffset);
   U.text("OPTIONS", .8 + textOffset);
   U.text("VEHICLE VIEWER", .85 + textOffset);
   U.text("MAP VIEWER", .9 + textOffset);
   if (!error.isEmpty()) {
    U.fillRGB(globalFlick ? 1 : 0, 0, 0);
    U.text(error, .3);
   }
  } else {
   U.font(.025);
   U.text(globalFlick ? ".. " + initialization + "   " : "   " + initialization + " ..", .5);
  }
  if (!usingKeys) {
   selected =
   Math.abs(.6 + clickOffset - mouseY) < clickRangeY ? 0 :
   Math.abs(.65 + clickOffset - mouseY) < clickRangeY ? 1 :
   Math.abs(.7 + clickOffset - mouseY) < clickRangeY ? 2 :
   Math.abs(.75 + clickOffset - mouseY) < clickRangeY ? 3 :
   Math.abs(.8 + clickOffset - mouseY) < clickRangeY ? 4 :
   Math.abs(.85 + clickOffset - mouseY) < clickRangeY ? 5 :
   Math.abs(.9 + clickOffset - mouseY) < clickRangeY ? 6 : selected;
  }
  gameFPS = U.refreshRate * .5;
 }

 private void setUpLANGame() {
  U.fillRGB(0, 0, 0, .75);
  U.fillRectangle(.5, .5, 1, 1);
  int n;
  if (section < 1) {
   modeLAN = LAN.OFF;
   vehiclesInMatch = Math.max(2, Math.min(vehiclesInMatch, maxPlayersLAN));
   try {
    for (BufferedReader in : inLAN) {
     in.close();
    }
    inLAN.clear();
    for (PrintWriter PW : outLAN) {
     PW.close();
    }
    outLAN.clear();
    if (serverSocket != null) {
     serverSocket.close();
    }
    if (clientSocket != null) {
     clientSocket.close();
    }
   } catch (IOException e) {
    e.printStackTrace();
   }
   for (n = maxPlayersLAN; --n >= 0; ) {
    playerNames[n] = vehicleDataLAN[n] = lastVehicleDataLAN[n] = "";
   }
   hostLeftMatch = waitingLAN = runLANLoadThread = false;
   readyLAN = new boolean[maxPlayersLAN];
   section = 1;
  } else {
   scene.setCursor(Cursor.CROSSHAIR);
   if (selectionTimer > selectionWait) {
    if (keyUp) {
     selected = --selected < 0 ? 1 : selected;
     usingKeys = true;
     U.soundPlay(sounds, "UI0", 0);
    }
    if (keyDown) {
     selected = ++selected > 1 ? 0 : selected;
     usingKeys = true;
     U.soundPlay(sounds, "UI0", 0);
    }
   }
   if (globalFlick && modeLAN == LAN.OFF) {
    if (selected == 0) {
     U.fillRGB(0, 0, 1);
    } else {
     U.fillRGB(0, 1, 0);
    }
    U.fillRectangle(.5, selected == 1 ? .5 : .45, .25, selectionHeight);
    String s;
    try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream("GameSettings")))) {
     for (String s1; (s1 = BR.readLine()) != null; ) {
      s = "" + s1.trim();
      userName = s.startsWith("UserName(") ? U.getString(s, 0) : userName;
      targetHost = s.startsWith("TargetHost(") ? U.getString(s, 0) : targetHost;
      portLAN = s.startsWith("Port(") ? (int) Math.round(U.getValue(s, 0)) : portLAN;
     }
    } catch (IOException e) {
     System.out.println("Problem updating Online settings: " + e);
    }
   }
   if (modeLAN == LAN.HOST && outLAN.size() > 0) {
    for (n = 0; n < outLAN.size(); n++) {
     String s = U.readInLAN(n);
     if (s.startsWith("CANCEL")) {
      escapeToLast(false);
     }
    }
   }
   if ((keyEnter || keySpace) && !waitingLAN) {
    if (selected == 0) {
     modeLAN = LAN.HOST;
     setUpLANGameThread();
     waitingLAN = true;
    } else if (selected == 1) {
     modeLAN = LAN.JOIN;
     setUpLANGameThread();
     waitingLAN = true;
    }
    U.soundPlay(sounds, "UI1", 0);
    keyEnter = keySpace = false;
   }
   U.font(.075);
   U.fillRGB(1, 1, 1);
   U.text(vehiclesInMatch + "-PLAYER GAME", .175);
   U.font(.01);
   U.fillRGB(1, 1, 1);
   if (modeLAN != LAN.OFF) {
    if (globalFlick) {
     StringBuilder s = new StringBuilder("Players in: " + userName);
     for (n = 0; n < vehiclesInMatch; n++) {
      s.append(n != userPlayer ? ", " + playerNames[n] : "");
     }
     U.text(s.toString(), .45);
    }
    U.text("(Hit ESCAPE to Cancel)", .5);
   } else {
    U.text("HOST GAME", .45);
    U.text("JOIN GAME", .5);
   }
   if (errorTimer <= 0) {
    joinError = "";
   } else {
    errorTimer -= tick;
   }
   U.text("Your UserName: " + userName, .7);
   U.text("Your Target Host is: " + targetHost, .725);
   U.text("Your Port #: " + portLAN, .75);
   U.text("For more information about this game mode, please read the Game Documentation", .85);
   if (globalFlick) {
    U.font(.02);
    U.text("" + joinError, .625);
   }
   if (keyEscape) {
    escapeToLast(true);
   }
   gameFPS = U.refreshRate;
  }
  if (!usingKeys) {
   selected = Math.abs(.45 + clickOffset - mouseY) < clickRangeY ? 0 : Math.abs(.5 + clickOffset - mouseY) < clickRangeY ? 1 : selected;
  }
 }

 private void setUpLANGameThread() {
  Thread gameSetupLAN = new Thread(() -> {
   int n;
   runLANLoadThread = true;
   if (modeLAN == LAN.HOST) {
    userPlayer = 0;
    playerNames[0] = userName;
    try {
     serverSocket = new ServerSocket(portLAN);
     while (runLANLoadThread) {
      if (outLAN.size() + 1 < vehiclesInMatch && !serverSocket.isClosed()) {
       Socket S = serverSocket.accept();
       outLAN.add(new PrintWriter(S.getOutputStream(), true));
       try {
        inLAN.add(new BufferedReader(new InputStreamReader(S.getInputStream())));
       } catch (IOException e) {
        e.printStackTrace();
       }
      }
      while (true) {
       String s = U.readInLAN(outLAN.size() - 1);
       if (s.startsWith("CANCEL")) {
        escapeToLast(false);
       } else if (s.startsWith("Name(")) {
        playerNames[outLAN.size()] = U.getString(s, 0);
        for (PrintWriter PW : outLAN) {
         for (int n1 = outLAN.size() + 1; --n1 > 0; ) {
          PW.println("Name" + n1 + "(" + playerNames[n1]);
         }
        }
        outLAN.get(outLAN.size() - 1).println("#Vehicles(" + vehiclesInMatch);
        outLAN.get(outLAN.size() - 1).println("Name0(" + userName);
        outLAN.get(outLAN.size() - 1).println("Join#(" + outLAN.size());
        outLAN.get(outLAN.size() - 1).println("MatchLength(" + matchLength);
       } else if (s.startsWith("joinerReady")) {
        System.out.println(playerNames[outLAN.size()] + " Joined Successfully");
        break;
       }
      }
      if (outLAN.size() + 1 >= vehiclesInMatch) {
       for (PrintWriter PW : outLAN) {
        PW.println("HostReady");
       }
       event = event.vehicleSelect;
       section = vehiclePick = 0;
       waitingLAN = runLANLoadThread = false;
      }
      gameFPS = U.refreshRate;
     }
    } catch (IOException e) {
     e.printStackTrace();
    }
   } else {
    try {
     clientSocket = new Socket(targetHost, portLAN);
     outLAN.add(new PrintWriter(clientSocket.getOutputStream(), true));
     inLAN.add(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));
     outLAN.get(0).println("Name(" + userName);
     while (runLANLoadThread) {
      String s = U.readInLAN(0);
      if (s.startsWith("CANCEL")) {
       escapeToLast(false);
       break;
      }
      vehiclesInMatch = s.startsWith("#Vehicles(") ? (int) Math.round(U.getValue(s, 0)) : vehiclesInMatch;
      if (s.startsWith("Join#(")) {
       userPlayer = (int) Math.round(U.getValue(s, 0));
       playerNames[userPlayer] = userName;
      } else if (s.startsWith("Name")) {
       for (n = maxPlayersLAN; --n >= 0; ) {
        playerNames[n] = s.startsWith("Name" + n) ? U.getString(s, 0) : playerNames[n];
       }
      } else if (s.startsWith("MatchLength(")) {
       matchLength = Math.round(U.getValue(s, 0));
       outLAN.get(0).println("joinerReady");
      } else if (s.startsWith("HostReady")) {
       event = event.vehicleSelect;
       vehiclePick = userPlayer;
       section = 0;
       waitingLAN = runLANLoadThread = false;
      }
      gameFPS = U.refreshRate;
     }
    } catch (IOException E) {
     E.printStackTrace();
     event = event.loadLAN;
     joinError = "Could not connect to Host";
     section = 0;
     errorTimer = 50;
    }
   }
  });
  gameSetupLAN.setPriority(9);
  gameSetupLAN.setDaemon(true);
  gameSetupLAN.start();
 }

 private void options() {
  boolean fromMenu = event == event.optionsMenu;
  if (fromMenu) {
   U.fillRGB(0, 0, 0);
   U.fillRectangle(.5, .5, 1, 1);
  }
  U.fillRGB(1, 1, 1);
  U.font(.05);
  U.text("OPTIONS", .15);
  U.font(.015);
  U.text("RETURN", .875 + textOffset);
  U.fillRGB(1, 1, 1);
  U.text("DriverSeat [" + (driverSeat > 0 ? "RIGHT->" : driverSeat < 0 ? "<-LEFT" : "CENTER") + "]", .3 + textOffset);
  U.text("Units [" + (units == .5364466667 ? "METRIC" : units == 1 / 3. ? "U.S." : "VEs") + "]", .35 + textOffset);
  U.text("Limit FPS to [" + (userFPS > U.refreshRate ? "JavaFX Default" : userFPS) + "]", .4 + textOffset);
  if (fromMenu) {
   U.text("Normal-Mapping [" + (normalMapping ? "ON" : "OFF") + "]", .45 + textOffset);
   U.text("Match Length [" + matchLength + "]", .5 + textOffset);
   U.text("Game Mode [" + (tournament > 0 ? "TOURNAMENT" : "NORMAL") + "]", .55 + textOffset);
   U.text("# of Players [" + vehiclesInMatch + "]", .6 + textOffset);
  }
  if (selectionTimer > selectionWait) {
   if (keyUp) {
    if (--selected < 0) {
     selected = fromMenu ? 7 : 3;
    }
    usingKeys = true;
    U.soundPlay(sounds, "UI0", 0);
   }
   if (keyDown) {
    selected = ++selected > 7 || (!fromMenu && selected > 3) ? 0 : selected;
    usingKeys = true;
    U.soundPlay(sounds, "UI0", 0);
   }
  }
  double color = globalFlick ? 1 : 0;
  U.strokeRGB(color, color, color);
  U.drawRectangle(.5, selected == 0 ? .875 : .25 + (.05 * selected), width, selectionHeight);
  U.fillRGB(1, 1, 1);
  boolean isAdjustFunction = false;
  if (selected == 1) {
   isAdjustFunction = true;
   U.text("Driver view location (for applicable vehicles)", .75);
   if (selectionTimer > selectionWait) {
    if (keyL && driverSeat > -1) {
     driverSeat--;
     U.soundPlay(sounds, "UI0", 0);
    }
    if (keyR && driverSeat < 1) {
     driverSeat++;
     U.soundPlay(sounds, "UI0", 0);
    }
   }
  } else if (selected == 2) {
   U.text("Switch between Metric, U.S., or the game's raw units (VEs)", .75);
   if ((keyEnter || keySpace) && selectionTimer > selectionWait) {
    if (units == 1) {
     units = .5364466667;
     unitSign[0] = "Kph";
     unitSign[1] = "Meters";
    } else if (units == .5364466667) {
     units = 1 / 3.;
     unitSign[0] = "Mph";
     unitSign[1] = "Feet";
    } else if (units == 1 / 3.) {
     units = 1;
     unitSign[0] = unitSign[1] = "VEs";
    }
   }
  } else if (selected == 3) {
   isAdjustFunction = true;
   if (selectionTimer > selectionWait) {
    if (keyL && userFPS > 1) {
     userFPS = userFPS > U.refreshRate ? U.refreshRate - 1 : --userFPS;
     U.soundPlay(sounds, "UI0", 0);
    }
    if (keyR && userFPS < Long.MAX_VALUE) {
     userFPS = ++userFPS >= U.refreshRate ? Long.MAX_VALUE : userFPS;
     U.soundPlay(sounds, "UI0", 0);
    }
   }
   U.text("Lower the FPS ceiling if your PC can't process V.E. well (i.e. overheating). Leave maxed otherwise.", .75);
  } else if (selected == 4) {
   if (fromMenu) {
    U.text("Use normal-mapping on textured surfaces", .75);
    if ((keyEnter || keySpace) && selectionTimer > selectionWait) {
     normalMapping = !normalMapping;
    }
   } else {
    selected++;
   }
  } else if (selected == 5) {
   if (fromMenu) {
    isAdjustFunction = true;
    if (selectionTimer > selectionWait) {
     if (keyL && matchLength > 0) {
      matchLength = Math.max(0, matchLength - 10);
      U.soundPlay(sounds, "UI0", 0);
     }
     if (keyR) {
      matchLength += 10;
      U.soundPlay(sounds, "UI0", 0);
     }
    }
    U.text("Set how long the match lasts", .75);
   } else {
    selected++;
   }
  } else if (selected == 6) {
   if (fromMenu) {
    U.text("See the Documentation for more info on Game Modes", .75);
    if ((keyEnter || keySpace) && selectionTimer > selectionWait) {
     tournament = tournament > 0 ? 0 : 1;
    }
   } else {
    selected++;
   }
  } else if (selected == 7) {
   if (fromMenu) {
    isAdjustFunction = true;
    if (selectionTimer > selectionWait) {
     int playerFloor = tournament > 0 ? 2 : 1;
     if (keyL) {
      vehiclesInMatch = --vehiclesInMatch < playerFloor ? maxPlayers : vehiclesInMatch;
      U.soundPlay(sounds, "UI0", 0);
     }
     if (keyR) {
      vehiclesInMatch = ++vehiclesInMatch > maxPlayers ? playerFloor : vehiclesInMatch;
      U.soundPlay(sounds, "UI0", 0);
     }
    }
    U.text("More players may slow down performance", .825);
   } else {
    selected++;
   }
  }
  U.fillRGB(1, 1, 1);
  U.text(selected > 0 ? isAdjustFunction ? "Use Left and Right arrow keys to Adjust" : "Click or hit Enter/Space to Change" : "", .8);
  if ((keyEnter || keySpace) && selectionTimer > selectionWait) {
   event = selected == 0 ? !fromMenu ? event.paused : event.mainMenu : event;
   U.soundPlay(sounds, "UI1", 0);
   keyEnter = keySpace = false;
  }
  if (keyEscape) {
   event = fromMenu ? event.mainMenu : event.paused;
   U.soundPlay(sounds, "UI1", 0);
   keyEscape = false;
  }
  vehiclesInMatch = tournament > 0 ? Math.max(2, vehiclesInMatch) : vehiclesInMatch;
  if (!usingKeys) {
   double clickOffset = .025;
   selected = Math.abs(.825 + clickOffset - mouseY) < clickRangeY ? 0 : Math.abs(.25 + clickOffset - mouseY) < clickRangeY ? 1 : Math.abs(.3 + clickOffset - mouseY) < clickRangeY ? 2 : Math.abs(.35 + clickOffset - mouseY) < clickRangeY ? 3 : selected;
   if (event == event.optionsMenu) {
    selected = Math.abs(.4 + clickOffset - mouseY) < clickRangeY ? 4 : Math.abs(.45 + clickOffset - mouseY) < clickRangeY ? 5 : Math.abs(.5 + clickOffset - mouseY) < clickRangeY ? 6 : Math.abs(.55 + clickOffset - mouseY) < clickRangeY ? 7 : selected;
   }
  }
 }

 private void manageBonus() {
  if (bonusHolder < 0) {
   if (U.getDepth(bonusX, bonusY, bonusZ) > -bonusBig.getRadius()) {
    U.setTranslate(bonusBig, bonusX, bonusY, bonusZ);
    U.setDiffuseRGB((PhongMaterial) bonusBig.getMaterial(), U.random(), U.random(), U.random());
    bonusBig.setVisible(true);
   } else {
    bonusBig.setVisible(false);
   }
   for (BonusBall bonusBall : bonusBalls) {
    bonusBall.setVisible(false);
   }
  } else {
   bonusBig.setVisible(false);
   bonusX = vehicles.get(bonusHolder).X;
   bonusY = vehicles.get(bonusHolder).Y;
   bonusZ = vehicles.get(bonusHolder).Z;
   for (BonusBall bonusBall : bonusBalls) {
    bonusBall.speedY += U.randomPlusMinus(6.);
    bonusBall.speedX += U.randomPlusMinus(6.);
    bonusBall.speedZ += U.randomPlusMinus(6.);
    bonusBall.speedX *= .99;
    bonusBall.speedY *= .99;
    bonusBall.speedZ *= .99;
    bonusBall.X += bonusBall.speedX * tick;
    if (Math.abs(bonusBall.X) > vehicles.get(bonusHolder).collisionRadius * 2) {
     bonusBall.speedX *= -1;
     bonusBall.X *= .999;
    }
    bonusBall.Y += bonusBall.speedY * tick;
    if (Math.abs(bonusBall.Y) > vehicles.get(bonusHolder).collisionRadius * 2) {
     bonusBall.speedY *= -1;
     bonusBall.Y *= .999;
    }
    bonusBall.Z += bonusBall.speedZ * tick;
    if (Math.abs(bonusBall.Z) > vehicles.get(bonusHolder).collisionRadius * 2) {
     bonusBall.speedZ *= -1;
     bonusBall.Z *= .999;
    }
    if (U.getDepth(vehicles.get(bonusHolder).X + bonusBall.X, vehicles.get(bonusHolder).Y + bonusBall.Y, vehicles.get(bonusHolder).Z + bonusBall.Z) > 0) {
     U.setTranslate(bonusBall, vehicles.get(bonusHolder).X + bonusBall.X, vehicles.get(bonusHolder).Y + bonusBall.Y, vehicles.get(bonusHolder).Z + bonusBall.Z);
     bonusBall.setVisible(true);
     U.setDiffuseRGB((PhongMaterial) bonusBall.getMaterial(), U.random(), U.random(), U.random());
    } else {
     bonusBall.setVisible(false);
    }
   }
  }
  if (matchStarted) {
   if (modeLAN == LAN.OFF) {
    for (Vehicle vehicle : vehicles) {
     if (bonusHolder < 0 && vehicle.damage <= vehicle.durability && !vehicle.phantomEngaged && U.distance(vehicle.X, bonusX, vehicle.Y, bonusY, vehicle.Z, bonusZ) < vehicle.collisionRadius + bonusBig.getRadius()) {
      setBonusHolder(vehicle);
     }
    }
    bonusHolder = bonusHolder > -1 && vehicles.get(bonusHolder).damage > vehicles.get(bonusHolder).durability ? -1 : bonusHolder;
   } else {
    if (bonusHolderLAN < 0 && vehicles.get(userPlayer).damage <= vehicles.get(userPlayer).durability && !vehicles.get(userPlayer).phantomEngaged && U.distance(vehicles.get(userPlayer).X, bonusX, vehicles.get(userPlayer).Y, bonusY, vehicles.get(userPlayer).Z, bonusZ) < vehicles.get(userPlayer).collisionRadius + bonusBig.getRadius()) {
     bonusHolderLAN = userPlayer;
     if (modeLAN == LAN.HOST) {
      for (PrintWriter PW : outLAN) {
       PW.println("BONUS0");
      }
     } else {
      outLAN.get(0).println("BONUS");
     }
    }
    int setHolder = bonusHolderLAN < 0 ? bonusHolderLAN : bonusHolder;
    if (setHolder > -1 && vehicles.get(setHolder).damage > vehicles.get(setHolder).durability) {
     bonusHolderLAN = bonusHolder = -1;
     if (modeLAN == LAN.HOST) {
      for (PrintWriter PW : outLAN) {
       PW.println("BonusOpen");
      }
     } else {
      outLAN.get(0).println("BonusOpen");
     }
    }
    if (bonusHolder != bonusHolderLAN) {
     bonusHolder = bonusHolderLAN;
     if (bonusHolder > -1) {
      setBonusHolder(vehicles.get(bonusHolder));
     }
    }
   }
  }
 }

 public static void setBonusHolder(Vehicle vehicle) {
  bonusHolder = vehicle.index;
  for (BonusBall bonusBall : bonusBalls) {
   bonusBall.setRadius(vehicles.get(bonusHolder).absoluteRadius * .02);
   bonusBall.X = bonusBall.Y = bonusBall.Z = bonusBall.speedX = bonusBall.speedY = bonusBall.speedZ = 0;
  }
  if (headsUpDisplay) {
   U.soundPlayIfNotPlaying(sounds, "bonus", 0);
  }
 }

 public static void updateDestructionNames() {
  for (int n = 1; n < 5; n++) {
   destructionNames[n - 1][0] = destructionNames[n][0];
   destructionNames[n - 1][1] = destructionNames[n][1];
   destructionNameColors[n - 1][0] = destructionNameColors[n][0];
   destructionNameColors[n - 1][1] = destructionNameColors[n][1];
  }
 }

 private void manageMatch() {
  boolean gamePlay = event == event.play || event == event.replay;
  matchTime -= matchTime > 0 && event == event.play && matchStarted ? tick : 0;
  tournamentOver = tournament > 0 && ((tournament > 4 && Math.abs(tournamentWins[0] - tournamentWins[1]) > 0) || (tournament > 2 && Math.abs(tournamentWins[0] - tournamentWins[1]) > 1));
  if (matchStarted && (keyEnter || keyEscape) && gamePlay) {
   keyUp = keyDown = keyEnter = keyEscape = false;
   selected = 0;
   U.soundPlay(sounds, "UI1", 0);
   event = event.paused;
  }
  arrow.setVisible(headsUpDisplay);
  Vehicle V = vehicles.get(vehiclePerspective);
  if (headsUpDisplay) {
   if (matchTime <= 0) {
    double titleHeight = .12625;
    if (vehiclesInMatch > 1) {
     String[] formatFinal = {DF.format(finalScore[0]), DF.format(finalScore[1])};
     if (formatFinal[0].equals(formatFinal[1])) {
      U.font(.025);
      U.fillRGB(0, 0, 0);
      U.text("IT'S A TIE!", titleHeight - .001);
      U.text("IT'S A TIE!", titleHeight + .001);
      U.fillRGB(1, 1, 1);
      U.text("IT'S A TIE!", titleHeight);
     } else {
      String announce = tournament > 0 && !tournamentOver ? "ROUND " + tournament + " OVER" : (finalScore[0] > finalScore[1] ? "GREEN" : "RED") + " TEAM WINS" + (tournamentOver ? " THE TOURNAMENT!" : "!");
      U.font(.025);
      U.fillRGB(0, 0, 0);
      U.text(announce, titleHeight - .001);
      U.text(announce, titleHeight + .001);
      U.fillRGB(1, 1, 1);
      U.text(announce, titleHeight);
     }
     U.fillRGB(E.groundInverse);
     U.font(.0175);
     U.fillRGB(0, 0, 0);
     if (globalFlick) {
      U.fillRGB(.5, .5, .5);
     }
     U.text((tournament < 1 || tournamentOver ? "FINAL " : "") + "SCORES:", .225);
     if (globalFlick) {
      U.fillRGB(0, 1, 0);
     }
     U.text(vehiclesInMatch > 2 ? "GREEN TEAM" : playerNames[0], .3, .225);
     U.text("" + (tournament > 0 ? tournamentWins[0] : formatFinal[0]), .3, .25);
     if (globalFlick) {
      U.fillRGB(1, 0, 0);
     }
     U.text(vehiclesInMatch > 2 ? "RED TEAM" : playerNames[1], .7, .225);
     U.text("" + (tournament > 0 ? tournamentWins[1] : formatFinal[1]), .7, .25);
    } else {
     U.font(.025);
     U.fillRGB(0, 0, 0);
     U.text("TIME'S UP!", titleHeight - .001);
     U.text("TIME'S UP!", titleHeight + .001);
     U.fillRGB(1, 1, 1);
     U.text("TIME'S UP!", titleHeight);
     U.fillRGB(E.groundInverse);
     U.font(.0175);
     double color = globalFlick ? .5 : 0;
     U.fillRGB(color, color, color);
     U.text("FINAL SCORE: " + finalScore[1], .225);
    }
   }
   if (V.destroyed) {
    if (!V.explosionType.contains("max")) {
     U.font(.02);
     if (globalFlick) {
      U.fillRGB(1, 1, 1);
      U.text(".. REVIVING.. ", .25);
     } else {
      U.fillRGB(0, 0, 0);
      U.text(" ..REVIVING ..", .25);
     }
    }
   }
   U.font(.01);
   if (lastArrowType != arrowType) {
    print = arrowType > 1 ? "Arrow now Locked on " + playerNames[arrowTarget] : "Arrow now pointing at " + (arrowType > 0 ? "Vehicles" : "Map");
    messageWait = false;
    printTimer = 50;
    lastArrowType = arrowType;
   }
   manageArrow();
   U.font(.00875);
   U.fillRGB(E.skyInverse);
   U.textL("Remaining Time: " + Math.round(matchTime), .0125, .05);
   //try {
   //U.textR("" + Math.round(FPS), .9, .5);
   //} catch (Exception e) {
   // }
   U.fillRGB(0, 0, 0, .5);
   U.fillRectangle(.025, .8, .05, .425);
   U.fillRectangle(.975, .8, .05, .425);
   if (destructionLog) {
    double x1 = .4725, x2 = .5275, y1 = .0375, y2 = .05, y3 = .0625, y4 = .075, y5 = .0875;
    U.fillRectangle(.5, .05, .4, .08);
    U.fillRGB(1, 1, 1);
    U.text("destroyed", y1);
    U.text("destroyed", y2);
    U.text("destroyed", y3);
    U.text("destroyed", y4);
    U.text("destroyed", y5);
    //LEFT
    U.fillRGB(destructionNameColors[0][0]);
    U.textR(destructionNames[0][0], x1, y1);
    U.fillRGB(destructionNameColors[1][0]);
    U.textR(destructionNames[1][0], x1, y2);
    U.fillRGB(destructionNameColors[2][0]);
    U.textR(destructionNames[2][0], x1, y3);
    U.fillRGB(destructionNameColors[3][0]);
    U.textR(destructionNames[3][0], x1, y4);
    U.fillRGB(destructionNameColors[4][0]);
    U.textR(destructionNames[4][0], x1, y5);
    //RIGHT
    U.fillRGB(destructionNameColors[0][1]);
    U.textL(destructionNames[0][1], x2, y1);
    U.fillRGB(destructionNameColors[1][1]);
    U.textL(destructionNames[1][1], x2, y2);
    U.fillRGB(destructionNameColors[2][1]);
    U.textL(destructionNames[2][1], x2, y3);
    U.fillRGB(destructionNameColors[3][1]);
    U.textL(destructionNames[3][1], x2, y4);
    U.fillRGB(destructionNameColors[4][1]);
    U.textL(destructionNames[4][1], x2, y5);
   }
   //LEFT HUD BLOCK
   U.font(.0125);
   if (V.vehicleType != type.turret) {
    U.fillRGB(.75, .75, .75);
    U.fillRectangle(.025, .7, .01, Math.min(.2, .2 * (Math.abs(V.speed) / V.topSpeeds[1])));
    U.fillRGB(1, 1, 1);
    U.fillRectangle(.025, .6, .02, .001);
    U.fillRectangle(.025, .8, .02, .001);
    long speed = Math.round(V.speed * units);
    U.text(Math.abs(speed) > 9999 ? DF.format(speed) : speed + "", .025, .7);
    U.text(unitSign[0], .025, .825);
   }
   U.fillRGB(1, 1, 1);
   U.font(.01);
   double converted = units == .5364466667 ? .0175 : units == 1 / 3. ? .0574147 : 1;
   U.font(.0075);
   U.text("(" + unitSign[1] + ")", .025, .875);
   U.textL("X: " + DF.format(V.X * converted), .00625, .9);
   U.textL("Y: " + DF.format(V.Y * converted), .00625, .925);
   U.textL("Z: " + DF.format(V.Z * converted), .00625, .95);
   U.font(.015);
   //RIGHT HUD BLOCK
   double damage = V.damage / V.durability;
   U.fillRGB(1, 1 - damage, 0);
   U.fillRectangle(.975, .7, .01, Math.min(.2, .2 * damage));
   U.fillRGB(1, 1, 1);
   U.fillRectangle(.975, .6, .02, .001);
   U.fillRectangle(.975, .8, .02, .001);
   U.text(Math.round(100 * V.damage / V.durability) + "%", .975, .7);
   U.font(.0075);
   U.text("DAMAGE", .975, .825);
   if (vehiclesInMatch > 1) {
    U.text("Vehicle #", .975, .875);
    if (vehiclesInMatch > 2) {
     if (vehiclePerspective < vehiclesInMatch >> 1) {
      U.fillRGB(0, 1, 0);
     } else {
      U.fillRGB(1, 0, 0);
     }
    }
    U.font(.01);
    U.text(vehiclePerspective + (vehiclePerspective == userPlayer ? " (You)" : ""), .975, .9);
   }
   if (modeLAN == LAN.JOIN && hostLeftMatch) {
    U.font(.02);
    double color = globalFlick ? 1 : .5;
    U.fillRGB(color, color, color);
    U.text("The Host has left match--hit Enter to start another match", .9);
   } else if (V.mode == mode.fly && E.gravity != 0 && U.sin(V.YZ) > 0 && V.netSpeedY + V.stallSpeed > 0) {
    U.fillRGB(E.skyInverse);
    U.text("STALL", .95);
   }
   if (printTimer > 0) {
    if (matchTime > 0) {
     U.font(.01);
     double color = globalFlick ? 0 : 1;
     U.fillRGB(color, color, color);
     U.text(print, .125);
    }
    printTimer -= gamePlay ? tick : 0;
   } else {
    messageWait = false;
   }
   U.font(.0125);
   if (V.flipped && V.flipTimer > 0) {
    if (V.mode.name().startsWith("drive")) {
     if (!destructionLog) {
      double color = globalFlick ? 0 : 1;
      U.fillRGB(color, color, color);
      U.text("Bad Landing", .075);
     }
    }
   } else if (stuntTimer > 0) {
    if (!destructionLog) {
     U.fillRGB(0, globalFlick ? 1 : 0, 0);
     U.text(stuntPrint, .075);
    }
    stuntTimer -= gamePlay ? tick : 0;
   }
  }
  long waitTime = V.vehicleType == type.aircraft ? 1 : 8;
  if (V.stuntTimer > waitTime && V.stuntReward > 0 && !V.flipped && !V.stuntEnd) {
   String stuntSpins, stuntRolls;
   String stuntFlips = stuntRolls = stuntSpins = stuntPrint = "";
   long computeStuntYZ = 0, computeStuntXY = 0, computeStuntXZ = 0;
   for (; computeStuntYZ < Math.abs(V.stuntYZ) - 45; computeStuntYZ += 360) ;
   stuntFlips = computeStuntYZ > 0 ? (V.flipCheck[0] && V.flipCheck[1] ? "BiDirectional " : "") + computeStuntYZ + "-Flip" : V.flipCheck[0] || V.flipCheck[1] ? "Half-flip" : stuntFlips;
   for (; computeStuntXY < Math.abs(V.stuntXY) - 45; computeStuntXY += 360) ;
   stuntRolls = computeStuntXY > 0 ? (V.rollCheck[0] && V.rollCheck[1] ? "BiDirectional " : "") + computeStuntXY + "-Roll" : V.rollCheck[0] || V.rollCheck[1] ? (stuntFlips.isEmpty() ? "Half-Roll" : "Flipside") : stuntRolls;
   for (; computeStuntXZ < Math.abs(V.stuntXZ) - 45; computeStuntXZ += 180) ;
   stuntSpins = computeStuntXZ > 0 ? computeStuntXZ + "-Spin" : stuntSpins;
   stuntTimer += !stuntFlips.isEmpty() ? 25 : 0;
   stuntTimer += !stuntRolls.isEmpty() ? 25 : 0;
   stuntTimer += !stuntSpins.isEmpty() ? 25 : 0;
   stuntTimer = Math.min(stuntTimer, 90);
   if (gamePlay) {
    if (headsUpDisplay && !destructionLog) {
     U.soundPlay(sounds, "stunt", 0);
    }
    String by1 = !stuntFlips.isEmpty() && !stuntRolls.isEmpty() ? " by " : "", by2 = !stuntSpins.isEmpty() && (!stuntFlips.isEmpty() || !stuntRolls.isEmpty()) ? " by " : "";
    stuntPrint = "Landed " + (V.offTheEdge ? "an off-the-edge " : "a ") + stuntFlips + by1 + stuntRolls + by2 + stuntSpins + "!";
   }
   V.stuntReward = 0;
  }
  long scoreStunt0 = 1 + Math.round(scoreStunt[0] * .0005);
  long scoreStunt1 = 1 + Math.round(scoreStunt[1] * .0005);
  long scoreDamage0 = 1 + Math.round(scoreDamage[0] * .000125);
  long scoreDamage1 = 1 + Math.round(scoreDamage[1] * .000125);
  long[] score = {scoreCheckpoint[0] * scoreLap[0] * scoreStunt0 * scoreDamage0 * scoreKill[0], scoreCheckpoint[1] * scoreLap[1] * scoreStunt1 * scoreDamage1 * scoreKill[1]};
  if (bonusHolder > -1) {
   score[bonusHolder < vehiclesInMatch >> 1 ? 0 : 1] *= 2;
  }
  if (headsUpDisplay) {
   U.font(.00875);
   U.fillRGB(0, 0, 0, .5);
   U.fillRectangle(.9375, .26, .125, .2);
   if (vehiclesInMatch > 1) {
    U.fillRectangle(.0625, .26, .125, .2);
    //GREEN
    U.fillRGB(0, 1, 0);
    U.textL("" + (vehiclesInMatch > 2 ? "GREEN TEAM" : playerNames[0]), .0125, .175);
    if (checkpoints.size() > 0) {
     if (globalFlick || scoreCheckpoint[0] >= scoreCheckpoint[1]) {
      U.textL(scoreCheckpoint[0] + " :Checkpoints", .0125, .2);
     }
     if (globalFlick || scoreLap[0] >= scoreLap[1]) {
      U.textL(scoreLap[0] + " :Laps", .0125, .225);
     }
    }
    if (globalFlick || scoreStunt0 >= scoreStunt1) {
     U.textL(scoreStunt0 + " :Stunts", .0125, .25);
    }
    if (globalFlick || scoreDamage0 >= scoreDamage1) {
     U.textL(scoreDamage0 + " :Damage Dealt", .0125, .275);
    }
    if (globalFlick || scoreKill[0] >= scoreKill[1]) {
     U.textL(scoreKill[0] + " :Kills", .0125, .3);
    }
    if (bonusHolder > -1 && bonusHolder < vehiclesInMatch >> 1) {
     U.textL("(Player " + bonusHolder + ") BONUS", .0125, .325);
    }
    U.textL(DF.format(score[0]) + " :Current Score", .0125, .35);
    //RED
    U.fillRGB(1, 0, 0);
    U.textR("" + (vehiclesInMatch > 2 ? "RED TEAM" : playerNames[1]), .9875, .175);
    if (checkpoints.size() > 0) {
     if (globalFlick || scoreCheckpoint[1] >= scoreCheckpoint[0]) {
      U.textR("Checkpoints: " + scoreCheckpoint[1], .9875, .2);
     }
     if (globalFlick || scoreLap[1] >= scoreLap[0]) {
      U.textR("Laps: " + scoreLap[1], .9875, .225);
     }
    }
    if (globalFlick || scoreStunt[1] >= scoreStunt[0]) {
     U.textR("Stunts: " + scoreStunt1, .9875, .25);
    }
    if (globalFlick || scoreDamage1 >= scoreDamage0) {
     U.textR("Damage Dealt: " + scoreDamage1, .9875, .275);
    }
    if (globalFlick || scoreKill[1] >= scoreKill[0]) {
     U.textR("Kills: " + scoreKill[1], .9875, .3);
    }
    if (bonusHolder >= vehiclesInMatch >> 1) {
     U.textR("BONUS (Player " + bonusHolder + ")", .9875, .325);
    }
    U.textR("Current Score: " + DF.format(score[1]), .9875, .35);
   } else {
    U.fillRGB(1, 1, 1);
    U.textR("YOU", .9875, .175);
    if (checkpoints.size() > 0) {
     U.textR("Checkpoints: " + scoreCheckpoint[1], .9875, .2);
     U.textR("Laps: " + scoreLap[1], .9875, .225);
    }
    U.textR("Stunts: " + scoreStunt1, .9875, .25);
    U.textR("Damage Dealt: " + scoreDamage1, .9875, .275);
    U.textR("Kills: " + scoreKill[1], .9875, .3);
    if (bonusHolder >= vehiclesInMatch >> 1) {
     U.textR("BONUS (Player " + bonusHolder + ")", .9875, .325);
    }
    U.textR("Current Score: " + DF.format(score[1]), .9875, .35);
   }
  }
  if (matchTime < 0) {
   finalScore[0] = score[0];
   finalScore[1] = score[1];
   if (vehiclesInMatch > 1 && headsUpDisplay) {
    if (score[0] == score[1]) {
     U.soundPlay(sounds, "finish0", 0);
     U.soundPlay(sounds, "finish1", 0);
    } else {
     U.soundPlay(sounds, "finish" + ((score[0] > score[1] && vehiclePerspective < vehiclesInMatch >> 1) || (score[1] > score[0] && vehiclePerspective >= vehiclesInMatch >> 1) ? 0 : 1), 0);
    }
   }
   if (score[0] > score[1]) {
    tournamentWins[0]++;
   } else if (score[1] > score[0]) {
    tournamentWins[1]++;
   }
   matchTime = 0;
  }
 }

 private void manageArrow() {
  Vehicle V = vehicles.get(vehiclePerspective);
  double d, dY, targetX = V.X, targetY = V.Y, targetZ = V.Z;
  if (arrowType < 1) {
   boolean hasSize = points.size() > 0;
   double nX = hasSize ? points.get(V.point).X : 0, nY = hasSize ? points.get(V.point).Y : 0, nZ = hasSize ? points.get(V.point).Z : 0;
   d = (nX - V.X >= 0 ? 270 : 90) + U.arcTan((nZ - V.Z) / (nX - V.X));
   dY = (nY - V.Y >= 0 ? 270 : 90) + U.arcTan(U.distance(nX, V.X, nZ, V.Z) / (nY - V.Y));
   if (hasSize) {
    targetX = points.get(V.point).X;
    targetY = points.get(V.point).Y;
    targetZ = points.get(V.point).Z;
   }
  } else {
   targetX = vehicles.get(arrowTarget).X;
   targetY = vehicles.get(arrowTarget).Y;
   targetZ = vehicles.get(arrowTarget).Z;
   if (arrowType < 2) {
    double compareDistance = Double.POSITIVE_INFINITY;
    for (Vehicle vehicle : vehicles) {
     if (vehicle.index != vehiclePerspective && vehicle.destructionType < 1 && U.distance(V.X, vehicle.X, V.Z, vehicle.Z, V.Y, vehicle.Y) < compareDistance) {
      arrowTarget = vehicle.index;
      compareDistance = U.distance(V.X, vehicle.X, V.Z, vehicle.Z, V.Y, vehicle.Y);
     }
    }
   }
   arrowTarget = vehiclesInMatch < 2 ? 0 : arrowTarget;
   double nameHeight = .15, B = vehicles.get(arrowTarget).damage / vehicles.get(arrowTarget).durability;
   U.fillRGB(1, 1 - B, 0);
   U.fillRectangle(.5, nameHeight, B * .1, .005);
   if (arrowType > 1) {
    double C = globalFlick ? 1 : 0;
    U.strokeRGB(C, C, C);
    graphicsContext.strokeLine((width * .5) - 50, height * nameHeight, (width * .5) + 50, height * nameHeight);
   }
   d = (vehicles.get(arrowTarget).X - V.X >= 0 ? 270 : 90) + U.arcTan((vehicles.get(arrowTarget).Z - V.Z) / (vehicles.get(arrowTarget).X - V.X));
   dY = (vehicles.get(arrowTarget).Y - V.Y >= 0 ? 270 : 90) + U.arcTan(U.distance(vehicles.get(arrowTarget).X, V.X, vehicles.get(arrowTarget).Z, V.Z) / (vehicles.get(arrowTarget).Y - V.Y));
   U.fillRGB(E.skyInverse);
   U.text("[ " + playerNames[arrowTarget] + " ]", nameHeight);
  }
  double convertedUnits = units == .5364466667 ? .0175 : units == 1 / 3. ? .0574147 : 1, color = globalFlick ? 1 : 0;
  U.fillRGB(color, color, color);
  U.text("(" + Math.round(U.distance(V.X, targetX, V.Y, targetY, V.Z, targetZ) * convertedUnits) + ")", .175);
  for (d += cameraXZ; d < -180; d += 360) ;
  for (; d > 180; d -= 360) ;
  if (arrowType > 0 && (vehiclesInMatch < 2 || arrowTarget == vehiclePerspective)) {
   d = dY = 0;
  }
  U.rotate(arrow, -dY, d);
  if (arrowType < 1 || vehiclesInMatch < 3) {
   U.setDiffuseRGB((PhongMaterial) arrow.getMaterial(), E.skyInverse);
  } else {
   long[] RG = {0, 0};
   if (globalFlick) {
    RG[arrowTarget < vehiclesInMatch >> 1 ? 1 : 0] = 1;
   }
   U.setDiffuseRGB((PhongMaterial) arrow.getMaterial(), RG[0], RG[1], 0);
  }
 }

 private void manageCamera(Vehicle vehicle) {
  camera.setNearClip(cameraView.contains("driver") ? 2 : 4);
  double cameraVehicleXZ, cameraVehicleY;
  if (U.listEquals(cameraView, "flow", "distant")) {
   cameraVehicleXZ = lastCameraViewNear ? vehicle.absoluteRadius * .45 : 800;
   cameraVehicleY = lastCameraViewNear ? vehicle.height * .9 : 250;
  } else {
   boolean mainView = cameraView.isEmpty();
   cameraVehicleXZ = mainView ? 800 : vehicle.absoluteRadius * .45;
   cameraVehicleY = mainView ? 250 : vehicle.height * .9;
  }
  if (cameraView.contains("driver")) {
   double xy1 = vehicle.XY, yz1 = vehicle.YZ, xz1 = vehicle.XZ;
   aroundXZ[1] = 0;
   if (lookAround != 0) {
    aroundXZ[1] = 180;
    xz1 += 180;
    xy1 *= -1;
    yz1 *= -1;
   }
   double driverViewZ = vehicle.driverViewZ, driverViewX = vehicle.driverViewX * driverSeat;
   if (lookAround != 0) {
    driverViewZ *= -1;
    driverViewX *= -1;
   }
   double dY = (-driverViewZ * U.sin(yz1)) + (vehicle.driverViewY * U.cos(yz1) * U.cos(xy1)), e = (driverViewZ * U.cos(yz1)) + (vehicle.driverViewY * U.sin(yz1) * U.cos(xy1));
   double dZ = U.cos(xz1) * e + (-vehicle.driverViewY * U.sin(xy1) * U.sin(xz1)), dX = U.sin(xz1) * -e + (-vehicle.driverViewY * U.sin(xy1) * U.cos(xz1));
   if (driverViewX != 0) {
    double[] rotateX = {driverViewX}, rotateY = {0}, rotateZ = {0};
    U.rotate(rotateX, rotateY, xy1);
    U.rotate(rotateY, rotateZ, yz1);
    U.rotate(rotateX, rotateZ, xz1);
    dX += rotateX[0];
    dY += rotateY[0];
    dZ += rotateZ[0];
   }
   cameraYZ = -yz1;
   cameraXZ = -xz1;
   cameraXY = -xy1;
   cameraX += vehicle.X + dX - cameraX;
   cameraY += vehicle.Y + dY - cameraY;
   cameraZ += vehicle.Z + dZ - cameraZ;
  } else if (cameraView.equals("flow")) {
   cameraXY = 0;
   boolean gamePlay = event == event.play || event == event.replay;
   double moveRate = .125 * tick, xd = -cameraVehicleY - (lastCameraViewNear ? 0 : vehicle.extraViewHeight) - (gamePlay ? Math.abs(vehicle.speed) : 0);
   cameraFlowFlip = vehicle.speed != 0 ? vehicle.speed < 0 : cameraFlowFlip;
   if (cameraFlowFlip) {
    while (Math.abs(-vehicle.XZ + 180 - cameraXZ) > 180) {
     cameraXZ += cameraXZ < -vehicle.XZ + 180 ? 360 : -360;
    }
    cameraXZ += (180 + -vehicle.XZ - cameraXZ) * moveRate;
    while (Math.abs(vehicle.YZ - cameraYZ) > 180) {
     cameraYZ += cameraYZ < vehicle.YZ ? 360 : -360;
    }
    cameraYZ += (vehicle.YZ - cameraYZ) * moveRate;
    double e = xd * U.sin(vehicle.YZ) - (-cameraVehicleXZ * U.cos(vehicle.YZ));
    cameraX += (vehicle.X + (-e * U.sin(vehicle.XZ)) - cameraX) * moveRate;
    cameraY += (vehicle.Y + ((xd * U.cos(vehicle.YZ)) + (-cameraVehicleXZ * U.sin(vehicle.YZ))) - cameraY) * moveRate;
    cameraZ += (vehicle.Z + (e * U.cos(vehicle.XZ)) - cameraZ) * moveRate;
   } else {
    while (Math.abs(-vehicle.XZ - cameraXZ) > 180) {
     cameraXZ += cameraXZ < -vehicle.XZ ? 360 : -360;
    }
    cameraXZ += (-vehicle.XZ - cameraXZ) * moveRate;
    while (Math.abs(-vehicle.YZ - cameraYZ) > 180) {
     cameraYZ += cameraYZ < -vehicle.YZ ? 360 : -360;
    }
    cameraYZ += (-vehicle.YZ - cameraYZ) * moveRate;
    double e = xd * U.sin(vehicle.YZ) - (cameraVehicleXZ * U.cos(vehicle.YZ));
    cameraX += (vehicle.X + (-e * U.sin(vehicle.XZ)) - cameraX) * moveRate;
    cameraY += (vehicle.Y + ((xd * U.cos(vehicle.YZ)) + (cameraVehicleXZ * U.sin(vehicle.YZ))) - cameraY) * moveRate;
    cameraZ += (vehicle.Z + (e * U.cos(vehicle.XZ)) - cameraZ) * moveRate;
   }
  } else if (cameraView.contains("watch")) {
   cameraXY = 0;
   if (cameraView.equals("watch&Move")) {
    while (Math.abs(vehicle.X - cameraX) > 10000) {
     cameraX += vehicle.X > cameraX ? 20000 : -20000;
    }
    while (Math.abs(vehicle.Z - cameraZ) > 10000) {
     cameraZ += vehicle.Z > cameraZ ? 20000 : -20000;
    }
    while (Math.abs(vehicle.Y - cameraY) > 10000) {
     cameraY += vehicle.Y > cameraY ? 20000 : -20000;
    }
    cameraY = Math.min(cameraY, U.distance(cameraX, E.poolX, cameraZ, E.poolZ) < E.pool[0].getRadius() ? E.groundLevel - vehicle.collisionRadius + E.poolDepth : E.groundLevel - vehicle.collisionRadius);
   }
   double vehicleCameraDistanceX = vehicle.X - cameraX, vehicleCameraDistanceZ = vehicle.Z - cameraZ, vehicleCameraDistanceY = vehicle.Y - cameraY;
   cameraXZ = -((90 + (vehicleCameraDistanceX >= 0 ? 180 : 0)) + U.arcTan(vehicleCameraDistanceZ / vehicleCameraDistanceX));
   cameraYZ = (90 * (vehicleCameraDistanceY > 0 ? 1 : -1)) - U.arcTan(U.netValue(vehicleCameraDistanceZ, vehicleCameraDistanceX) / vehicleCameraDistanceY);
  } else {
   long viewMultiply = cameraView.equals("distant") ? 10 : 1;
   cameraXY = 0;
   if (vehicle.vehicleType == type.vehicle) {
    cameraYZ = 0;
    if (Math.abs(lookAround) > 0 && !(lookForward[0] && lookForward[1])) {
     aroundXZ[1] += lookAround > 0 ? 10 : -10;
    }
    double sourceCameraXZ = vehicle.cameraXZ + aroundXZ[1];
    cameraXZ = -sourceCameraXZ;
    cameraX = vehicle.X + ((-(vehicle.Z - (cameraVehicleXZ * viewMultiply) - vehicle.Z)) * U.sin(sourceCameraXZ));
    cameraZ = vehicle.Z + ((vehicle.Z - (cameraVehicleXZ * viewMultiply) - vehicle.Z) * U.cos(sourceCameraXZ));
    cameraY = vehicle.Y - ((cameraVehicleY + (cameraView.isEmpty() ? vehicle.extraViewHeight : 0)) * viewMultiply);
   } else {
    double YZ1 = vehicle.YZ, XZ1 = vehicle.XZ, xd = -cameraVehicleY * viewMultiply;
    boolean lookBck = lookAround != 0;
    long lookDirection = lookBck ? -1 : 1;
    aroundXZ[1] = lookBck ? 180 : 0;
    if (Math.abs(vehicle.YZ) > 90) {
     xd *= -1;
     XZ1 -= 180;
     YZ1 *= -1;
     YZ1 += vehicle.YZ > 90 ? 180 : -180;
    }
    cameraYZ = -YZ1 * lookDirection;
    cameraXZ = -XZ1 + aroundXZ[1];
    double e = cameraVehicleXZ * viewMultiply * lookDirection * U.cos(vehicle.YZ);
    cameraX = vehicle.X + (-(xd * U.sin(vehicle.YZ) - e) * U.sin(vehicle.XZ));
    cameraY = vehicle.Y + ((xd * U.cos(vehicle.YZ)) + (cameraVehicleXZ * viewMultiply * lookDirection * U.sin(vehicle.YZ)));
    cameraZ = vehicle.Z + ((xd * U.sin(vehicle.YZ) - e) * U.cos(vehicle.XZ));
   }
  }
 }

 private void cameraAroundTrack() {
  cameraYZ = 10;
  cameraY = mapSelectY - 5000;
  cameraXY = 0;
  cameraX = mapSelectX - (17000 * U.sin(aroundXZ[0]));
  cameraZ = mapSelectZ - (17000 * U.cos(aroundXZ[0]));
  mapSelectRandomRotationDirection *= U.random() > .999 ? -1 : 1;
  for (aroundXZ[0] += mapSelectRandomRotationDirection * tick; aroundXZ[0] > 180; aroundXZ[0] -= 360) ;
  for (; aroundXZ[0] < -180; aroundXZ[0] += 360) ;
  if ((trackTimer += tick) > 6) {
   trackPoint = ++trackPoint >= points.size() ? 0 : trackPoint;
   trackTimer = 0;
  }
  if (points.size() > 0) {
   mapSelectX -= (mapSelectX - points.get(trackPoint).X) * .1 * tick;
   mapSelectY -= (mapSelectY - points.get(trackPoint).Y) * .1 * tick;
   mapSelectZ -= (mapSelectZ - points.get(trackPoint).Z) * .1 * tick;
   mapSelectY = Math.min(mapSelectY, 0);
  } else {
   mapSelectX = mapSelectZ = mapSelectY = 0;
  }
  cameraXZ = aroundXZ[0];
 }

 private void reset() {
  matchStarted = cameraFlowFlip = false;
  vehiclePerspective = userPlayer;
  matchTime = matchLength;
  arrowTarget = Math.min(vehiclesInMatch - 1, arrowTarget);
  scoreCheckpoint[0] = scoreCheckpoint[1] = scoreLap[0] = scoreLap[1] = scoreKill[0] = scoreKill[1] = 1;
  scoreDamage[0] = scoreDamage[1] = aroundXZ[1] = printTimer = lookAround = scoreStunt[0] = scoreStunt[1] = 0;
  bonusBig.setVisible(true);
  for (BonusBall bonusBall : bonusBalls) {
   bonusBall.setVisible(false);
  }
  bonusHolder = bonusHolderLAN = -1;
  stuntTimer = trackTimer = Recorder.recorded = trackPoint = 0;
  int n;
  for (n = destructionNames.length; --n >= 0; ) {
   destructionNames[n][0] = "";
   destructionNames[n][1] = "";
   destructionNameColors[n][0] = new Color(0, 0, 0, 1);
   destructionNameColors[n][1] = new Color(0, 0, 0, 1);
  }
  destructionLog = vehiclesInMatch > 1;
  if (!inViewer && modeLAN == LAN.OFF) {
   for (n = vehiclesInMatch; --n >= 0; ) {
    playerNames[n] = vehicles.get(n).vehicleName;
   }
  }
  mapSelectRandomRotationDirection = U.random() < .5 ? 1 : -1;
 }

 private static void falsify() {
  keyUp = keyDown = keyL = keyR = keySpace = keyEnter = keySpecial[0] = keySpecial[1] = keyBoost = keyEscape = false;
 }

 private void setUpMouseKeys() {
  scene.setOnMouseMoved((MouseEvent mouseEvent) -> {
   usingKeys = false;
   mouseX = mouseEvent.getX() / width;
   mouseY = mouseEvent.getY() / height;
   cursorDriving = matchStarted || cursorDriving;
  });
  scene.setOnMousePressed((MouseEvent mouseEvent) -> {
   mouseX = mouseEvent.getX() / width;
   mouseY = mouseEvent.getY() / height;
   mouse = !mouseClick || mouse;
  });
  scene.setOnMouseReleased((MouseEvent mouseEvent) -> {
   mouseX = mouseEvent.getX() / width;
   mouseY = mouseEvent.getY() / height;
   mouseClick = mouse = false;
   falsify();
  });
  scene.setOnKeyPressed((KeyEvent keyEvent) -> {
   switch (keyEvent.getCode()) {
    case UP:
     keyUp = true;
     if (cursorDriving) {
      keyDown = keyL = keyR = keySpace = cursorDriving = false;
     }
     break;
    case DOWN:
     keyDown = true;
     if (cursorDriving) {
      keyUp = keyL = keyR = keySpace = cursorDriving = false;
     }
     break;
    case LEFT:
     keyL = true;
     if (cursorDriving) {
      keyUp = keyDown = keySpace = cursorDriving = false;
     }
     break;
    case RIGHT:
     keyR = true;
     if (cursorDriving) {
      keyUp = keyDown = keySpace = cursorDriving = false;
     }
     break;
    case SPACE:
     keySpace = true;
     if (cursorDriving) {
      keyUp = keyDown = keyL = keyR = cursorDriving = false;
     }
     break;
    case ENTER:
     keyEnter = true;
     break;
    case ESCAPE:
     keyEscape = true;
     break;
    case Z:
     lookAround = 1;
     lookForward[1] = true;
     cameraView = cameraView.equals("flow") && !matchStarted ? lastCameraViewWithLookAround : cameraView;
     break;
    case X:
     lookAround = -1;
     lookForward[0] = true;
     cameraView = cameraView.equals("flow") && !matchStarted ? lastCameraViewWithLookAround : cameraView;
     break;
    case DIGIT1:
     cameraView = "";
     lastCameraView = cameraView;
     lastCameraViewNear = false;
     lastCameraViewWithLookAround = cameraView;
     break;
    case DIGIT2:
     cameraView = "near";
     lastCameraView = cameraView;
     lastCameraViewNear = true;
     lastCameraViewWithLookAround = cameraView;
     break;
    case DIGIT3:
     cameraView = "driver";
     lastCameraView = cameraView;
     break;
    case DIGIT4:
     cameraView = "distant";
     lastCameraView = cameraView;
     break;
    case DIGIT5:
     cameraView = "flow";
     lastCameraView = cameraView;
     break;
    case DIGIT6:
     cameraView = "watch&Move";
     lastCameraView = cameraView;
     break;
    case DIGIT7:
    case DIGIT8:
    case DIGIT9:
    case DIGIT0:
     cameraView = "watch";
     lastCameraView = cameraView;
     break;
    case V:
     keySpecial[0] = true;
     break;
    case F:
     keySpecial[1] = true;
     break;
    case B:
     keyBoost = true;
     break;
    case A:
     arrowType = ++arrowType > 2 ? 0 : arrowType;
     sameVehicles = !sameVehicles;
     break;
    case S:
     vehiclePerspective = --vehiclePerspective < 0 ? vehiclesInMatch - 1 : vehiclePerspective;
     toUserPerspective[1] = true;
     break;
    case D:
     vehiclePerspective = ++vehiclePerspective >= vehiclesInMatch ? 0 : vehiclePerspective;
     toUserPerspective[0] = true;
     break;
    case H:
     headsUpDisplay = !headsUpDisplay;
     break;
    case L:
     destructionLog = !destructionLog;
     break;
    case SHIFT:
     zoomChange = .98;
     restoreZoom[1] = true;
     break;
    case CONTROL:
     zoomChange = 1.02;
     restoreZoom[0] = true;
     break;
    case M:
     muteSound = !muteSound;
     break;
    case COMMA:
     musicVolume = Math.max(musicVolume - .125, 0);
     if (mediaPlayer != null) {
      mediaPlayer.setVolume(musicVolume);
     }
     break;
    case PERIOD:
     musicVolume = Math.min(musicVolume + .125, 1);
     if (mediaPlayer != null) {
      mediaPlayer.setVolume(musicVolume);
     }
     break;
    case J:
     viewerHeight = event == event.vehicleViewer ? 10 : 100;
     break;
    case U:
     viewerHeight = event == event.vehicleViewer ? -10 : -100;
     break;
    case T:
     viewerDepth = event == event.vehicleViewer ? 10 : 200;
     break;
    case G:
     viewerDepth = event == event.vehicleViewer ? -10 : -200;
     break;
    case EQUALS:
     vehicleLightBrightnessChange = .01;
     break;
    case MINUS:
     vehicleLightBrightnessChange = -.01;
     break;
    case R:
     showFPS = !showFPS;
     break;
   }
  });
  scene.setOnKeyReleased((KeyEvent keyEvent) -> {
   switch (keyEvent.getCode()) {
    case UP:
     keyUp = false;
     break;
    case DOWN:
     keyDown = false;
     break;
    case LEFT:
     keyL = false;
     break;
    case RIGHT:
     keyR = false;
     break;
    case SPACE:
     keySpace = false;
     break;
    case ENTER:
     keyEnter = false;
     break;
    case ESCAPE:
     keyEscape = false;
     break;
    case Z:
    case X:
     lookAround = 0;
     lookForward[0] = lookForward[1] = false;
     break;
    case V:
     keySpecial[0] = false;
     break;
    case F:
     keySpecial[1] = false;
     break;
    case B:
     keyBoost = false;
     break;
    case S:
    case D:
     toUserPerspective[0] = toUserPerspective[1] = false;
     break;
    case SHIFT:
    case CONTROL:
     zoomChange = 1;
     restoreZoom[0] = restoreZoom[1] = false;
     break;
    case U:
    case J:
     viewerHeight = 0;
     break;
    case T:
    case G:
     viewerDepth = 0;
     break;
    case EQUALS:
    case MINUS:
     vehicleLightBrightnessChange = 0;
     break;
   }
  });
 }

 private void matchDataOutLAN() {
  vehicles.get(userPlayer).drive = keyUp;
  vehicles.get(userPlayer).reverse = keyDown;
  vehicles.get(userPlayer).turnL = keyL;
  vehicles.get(userPlayer).turnR = keyR;
  vehicles.get(userPlayer).handbrake = keySpace;
  vehicles.get(userPlayer).useSpecial[0] = keySpecial[0];
  vehicles.get(userPlayer).useSpecial[1] = keySpecial[1];
  vehicles.get(userPlayer).boost = keyBoost;
  String s;
  if (event == event.play) {
   s = "(" + vehicles.get(userPlayer).X + "," + vehicles.get(userPlayer).Y + "," + vehicles.get(userPlayer).Z + "," + vehicles.get(userPlayer).XZ + "," + vehicles.get(userPlayer).YZ + "," + vehicles.get(userPlayer).XY + "," + vehicles.get(userPlayer).speed + "," + vehicles.get(userPlayer).damage + "," + vehicles.get(userPlayer).checkpointsPassed + "," + vehicles.get(userPlayer).lightBrightness + ")";
   s += vehicles.get(userPlayer).drive ? " ^ " : "";
   s += vehicles.get(userPlayer).reverse ? " v " : "";
   s += vehicles.get(userPlayer).turnL ? " < " : "";
   s += vehicles.get(userPlayer).turnR ? " > " : "";
   s += vehicles.get(userPlayer).handbrake ? " _ " : "";
   s += vehicles.get(userPlayer).useSpecial[0] ? " 0 " : "";
   s += vehicles.get(userPlayer).useSpecial[1] ? " 1 " : "";
   s += vehicles.get(userPlayer).boost ? " b " : "";
   if (modeLAN == LAN.HOST) {
    for (PrintWriter out : outLAN) {
     out.println("0" + s);
    }
   } else {
    outLAN.get(0).println(s);
   }
  }
 }

 private void gamePlayLAN(final int n) {
  gameMatchLAN[n] = new Thread(() -> {
   String s;
   runLANGameThread[n] = true;
   while (runLANGameThread[n]) {
    if (modeLAN == LAN.HOST) {
     s = U.readInLAN(n - 1);
     if (s.startsWith("BonusOpen")) {
      bonusHolderLAN = -1;
      if (vehiclesInMatch > 2) {
       for (int n1 = vehiclesInMatch; --n1 > 0; ) {
        if (n1 != n) {
         outLAN.get(n1 - 1).println("BonusOpen");
        }
       }
      }
     } else if (s.startsWith("BONUS")) {
      bonusHolderLAN = n;
      if (vehiclesInMatch > 2) {
       for (int n1 = vehiclesInMatch; --n1 > 0; ) {
        if (n1 != n) {
         outLAN.get(n1 - 1).println("BONUS" + n);
        }
       }
      }
     } else if (s.startsWith("(")) {
      vehicleDataLAN[n] = s;
     }
     if (vehiclesInMatch > 2 && !lastVehicleDataLAN[n].equals(vehicleDataLAN[n])) {
      for (int n1 = vehiclesInMatch; --n1 > 0; ) {
       if (n1 != n) {
        outLAN.get(n1 - 1).println(n + vehicleDataLAN[n]);
       }
      }
      lastVehicleDataLAN[n] = vehicleDataLAN[n];
     }
     if (vehicleDataLAN[n].startsWith("(")) {
      vehicles.get(n).X = U.getValue(vehicleDataLAN[n], 0);
      vehicles.get(n).Y = U.getValue(vehicleDataLAN[n], 1);
      vehicles.get(n).Z = U.getValue(vehicleDataLAN[n], 2);
      vehicles.get(n).XZ = U.getValue(vehicleDataLAN[n], 3);
      vehicles.get(n).YZ = U.getValue(vehicleDataLAN[n], 4);
      vehicles.get(n).XY = U.getValue(vehicleDataLAN[n], 5);
      vehicles.get(n).speed = U.getValue(vehicleDataLAN[n], 6);
      vehicles.get(n).damage = U.getValue(vehicleDataLAN[n], 7);
      vehicles.get(n).checkpointsPassed = (int) Math.round(U.getValue(vehicleDataLAN[n], 8));
      vehicles.get(n).lightBrightness = U.getValue(vehicleDataLAN[n], 9);
      vehicles.get(n).drive = vehicleDataLAN[n].contains(" ^ ");
      vehicles.get(n).reverse = vehicleDataLAN[n].contains(" v ");
      vehicles.get(n).turnL = vehicleDataLAN[n].contains(" < ");
      vehicles.get(n).turnR = vehicleDataLAN[n].contains(" > ");
      vehicles.get(n).handbrake = vehicleDataLAN[n].contains(" _ ");
      vehicles.get(n).useSpecial[0] = vehicleDataLAN[n].contains(" 0 ");
      vehicles.get(n).useSpecial[1] = vehicleDataLAN[n].contains(" 1 ");
      vehicles.get(n).boost = vehicleDataLAN[n].contains(" b ");
     }
    } else {
     s = U.readInLAN(0);
     hostLeftMatch = s.startsWith("END") || hostLeftMatch;
     for (Vehicle vehicle : vehicles) {
      if (vehicle.index != userPlayer) {
       bonusHolderLAN = s.startsWith("BonusOpen") ? -1 : s.startsWith("BONUS" + vehicle.index) ? vehicle.index : bonusHolderLAN;
       if (s.startsWith(vehicle.index + "(")) {
        vehicleDataLAN[vehicle.index] = s;
        vehicle.X = U.getValue(vehicleDataLAN[vehicle.index], 0);
        vehicle.Y = U.getValue(vehicleDataLAN[vehicle.index], 1);
        vehicle.Z = U.getValue(vehicleDataLAN[vehicle.index], 2);
        vehicle.XZ = U.getValue(vehicleDataLAN[vehicle.index], 3);
        vehicle.YZ = U.getValue(vehicleDataLAN[vehicle.index], 4);
        vehicle.XY = U.getValue(vehicleDataLAN[vehicle.index], 5);
        vehicle.speed = U.getValue(vehicleDataLAN[vehicle.index], 6);
        vehicle.damage = U.getValue(vehicleDataLAN[vehicle.index], 7);
        vehicle.checkpointsPassed = (int) Math.round(U.getValue(vehicleDataLAN[vehicle.index], 8));
        vehicle.lightBrightness = U.getValue(vehicleDataLAN[vehicle.index], 9);
        vehicle.drive = vehicleDataLAN[vehicle.index].contains(" ^ ");
        vehicle.reverse = vehicleDataLAN[vehicle.index].contains(" v ");
        vehicle.turnL = vehicleDataLAN[vehicle.index].contains(" < ");
        vehicle.turnR = vehicleDataLAN[vehicle.index].contains(" > ");
        vehicle.handbrake = vehicleDataLAN[vehicle.index].contains(" _ ");
        vehicle.useSpecial[0] = vehicleDataLAN[vehicle.index].contains(" 0 ");
        vehicle.useSpecial[1] = vehicleDataLAN[vehicle.index].contains(" 1 ");
        vehicle.boost = vehicleDataLAN[vehicle.index].contains(" b ");
       }
      }
     }
    }
    try {
     Thread.sleep(1);
    } catch (InterruptedException ignored) {
    }
   }
  });
  gameMatchLAN[n].setPriority(9);
  gameMatchLAN[n].setDaemon(true);
  gameMatchLAN[n].start();
 }

 private void clearGraphics() {
  boolean addSunlightBack = group.getChildren().contains(E.sunlight),
  addSunBack = group.getChildren().contains(E.sun),
  addGroundBack = group.getChildren().contains(E.ground);
  group.getChildren().clear();
  U.add(E.ambientLight, addSunlightBack ? E.sunlight : null, addSunBack ? E.sun : null, addGroundBack ? E.ground : null);
  group2.getChildren().clear();
 }

 private void clearSounds() {//Not all sounds close
  for (Vehicle vehicle : vehicles) {
   vehicle.closeSounds();
  }
  U.soundStop(sounds, "rain");
  U.soundStop(sounds, "tornado");
  U.soundStop(sounds, "tsunami");
  U.soundStop(sounds, "sandstorm");
  for (Fire fire : E.fires) {
   U.soundClose(sounds, "fire" + E.fires.indexOf(fire));
  }
  for (Boulder boulder : E.boulders) {
   U.soundClose(sounds, "boulder" + E.boulders.indexOf(boulder));
  }
  for (Meteor meteor : E.meteors) {
   U.soundClose(sounds, "meteor" + E.meteors.indexOf(meteor));
  }
  U.soundClose(VE.sounds, "thunder", 11);
 }
}
