/*
THE VEHICULAR EPIC

HEAD DEVELOPER: Ryan Albano
PAST ASSISTANT DEVS: Vitor Macedo, Dany Fernández Diaz

NAMING SYSTEM (variable names are usually typed as follows):

wordWord

For example, 'current checkpoint' would be typed as 'currentCheckpoint'.

Successive capital letters are usually abbreviations, such as 'PhongMaterial PM' or 'FileInputStream FIS';

A note on Enums:
Enums are used throughout this project. The IDE may determine some enum fields as unused. Be sure to NOT remove any of them!
(They become utilized when the game loads vehicles.)
 */
package ve;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import ve.converter.MainFrame;
import ve.effects.Explosion;
import ve.environment.*;
import ve.trackElements.Checkpoint;
import ve.trackElements.Point;
import ve.trackElements.TE;
import ve.trackElements.trackParts.TrackPart;
import ve.utilities.U;
import ve.vehicles.*;

import java.io.*;
import java.util.*;

public class VE extends Application {

 private Scene scene;
 public static SubScene scene3D;
 public static final Group group = new Group();
 private static Canvas canvas;
 public static GraphicsContext graphicsContext;
 private static final Sphere viewerCollisionBounds = new Sphere();
 static int vehiclePick;
 static int trackPoint;
 public static int bonusHolder = -1;
 static final int maxPlayers = (int) Math.round(Math.max(Network.maxPlayers, Runtime.getRuntime().maxMemory() * .00000001125));
 public static int vehiclePerspective;
 public static int vehiclesInMatch = 1;
 static final int[] vehicleNumber = new int[maxPlayers];
 public static int map;
 public static int userPlayer;
 public static boolean keyUp, keyDown, keyLeft, keyRight, keySpace;
 public static boolean keyW, keyS, keyA, keyD;
 public static final boolean[] keySpecial = new boolean[2];
 public static boolean keyBoost, keyPassBonus;
 static boolean keyEnter, keyEscape;
 private static boolean mouse, mouseClick;
 private static boolean usingKeys;
 public static boolean muteSound;
 public static boolean cursorDriving;
 private static boolean sameVehicles, showVehicle;
 public static boolean matchStarted;
 public static boolean globalFlick;
 private static boolean showInfo;
 public static boolean headsUpDisplay = true;
 public static boolean normalMapping;
 static boolean cameraShake;
 static boolean degradedSoundEffects;
 public static boolean messageWait;
 private static boolean creditsDirection;
 public static boolean randomStartAngle, guardCheckpoint;
 public static boolean lapCheckpoint;
 private static boolean tournamentOver;
 private static boolean inViewer;
 private static boolean vehicleViewer3DLighting, showCollisionBounds, showWireframe;
 private static double mouseX, mouseY;
 private static double selectionWait, selectionTimer;
 public static double speedLimitAI;
 private static double matchTime;
 private static double stuntTimer;
 public static double printTimer;
 private static double creditsQuantity;
 public static double tick;
 static double timer;
 public static double width, height;
 public static double defaultVehicleLightBrightness;
 private static double vehicleLightBrightnessChange;
 public static double mouseSteerX, mouseSteerY;
 static double trackTimer;
 private static double viewerY, viewerZ, viewerXZ, viewerYZ, viewerHeight, viewerDepth;
 private double UIMovementSpeedMultiple = 1;
 public static double units = 1;
 public static double musicGain = -6.020599913;
 static double errorTimer;
 public static final double[] userRandomRGB = {U.random(), U.random(), U.random()};
 static double gameFPS = Double.POSITIVE_INFINITY;
 private static long tournament;
 private static final long[] tournamentWins = new long[2];
 static long section;
 private static long selected;
 private static final double selectionHeight = .03, clickRangeY = selectionHeight * .5, baseClickOffset = -.025, textOffset = .01;
 public static long matchLength;
 public static long driverSeat;
 public static long currentCheckpoint;
 public static final long[] scoreCheckpoint = new long[2];
 public static final long[] scoreLap = new long[2];
 public static final long[] scoreStunt = new long[2];
 public static final double[] scoreDamage = new double[2];
 private static final long[] scoreKill = new long[2];
 private static final double[] finalScore = new double[2];
 private static long lastTime;
 private static long userFPS = Long.MAX_VALUE;
 public static String initialization = "Loading V.E.";
 public static String print = "";
 private static String stuntPrint = "";

 enum opacityUI {
  ;
  private static final double minimal = .5, maximal = .75;
 }

 private static boolean destructionLog;
 public static final String[][] destructionNames = new String[5][2];
 public static final Color[][] destructionNameColors = new Color[5][2];
 public static String vehicleMaker = "", mapName = "";
 private static final String[] unitName = {"VEs", "VEs"};
 private static String error = "";
 public static final String[] playerNames = new String[maxPlayers];
 private Player mediaPlayer;
 private String songName = "";
 public static final Map<String, Image> images = new HashMap<>();
 public static final List<Vehicle> vehicles = new ArrayList<>(maxPlayers);
 public static List<String> vehicleModels;
 static Sound UI;
 public static Sound checkpoint;
 private static Sound stunt;
 public static Sound bonus;
 private static Sound finish;

 public enum MapModels {
  road, roadshort, roadturn, roadbendL, roadbendR, roadend, roadincline, offroad, offroadshort, offroadturn, offroadbump, offroadrocky, offroadend, offroadincline, mixroad,
  checkpoint, fixpoint,
  ramp, rampcurved, ramptrapezoid, ramptriangle, rampwall, quarterpipe, pyramid, plateau,
  offramp, offplateau, mound, pavedmound,//<-'mound' is needed!
  floor, offfloor, wall, offwall, cube, offcube, spike, spikes, block, blocktower, border, beam, grid, tunnel, roadlift, speedgate, slowgate, antigravity,
  tree0, tree1, tree2, treepalm, cactus0, cactus1, cactus2, rainbow, crescent
 }

 public static final List<String> maps = new ArrayList<>(Arrays.asList("basic", "lapsGlory", "checkpoint", "gunpowder", "underOver", "antigravity", "versus1", "versus2", "versus3", "trackless", "desert", "3DRace", "trip", "raceNowhere", "moonlight", "bottleneck", "railing", "twisted", "deathPit", "falls", "pyramid", "fusion", "darkDivide", "arctic", "scenicRoute", "winterMode", "mountainHop", "damage", "cavern", "southPole", "aerialControl", "matrix", "mist", "vansLand", "dustDevil", "forest", "zipCross", "highlands", "coldFury", "tornado", "volcanic", "tsunami", "boulder", "sands", "meteor", "speedway", "endurance", "tunnel", "circle", "circleXL", "circles", "everything", "linear", "maze", "xy", "stairwell", "immense", "showdown", "ocean", "lastStand", "parkingLot", "city", "machine", "military", "underwater", "hell", "moon", "mars", "sun", "space1", "space2", "space3", "summit", "portal", "blackHole", "doomsday", "+UserMap & TUTORIAL+"));
 public static Status status = Status.mainMenu;
 private static Status lastStatus;

 public enum Status {
  play, replay, paused, optionsMatch, optionsMenu, mainMenu, credits,
  vehicleSelect, vehicleViewer,
  mapJump, mapLoadPass0, mapLoadPass1, mapLoadPass2, mapLoadPass3, mapLoadPass4, mapError, mapView, mapViewer,
  howToPlay, loadLAN
 }

 static void run(String[] s) {
  launch(s);
 }

 private void loadVE(Stage stage) {
  Thread loadVE = new Thread(() -> {
   try {
    int n;
    scene3D.setFill(Color.color(0, 0, 0));
    TE.arrowScene.setFill(Color.color(0, 0, 0, 0));
    initialization = "Setting Key/Mouse Input";
    loadKeysMouse();
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
    try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream("GameSettings"), U.standardChars))) {
     for (String s1; (s1 = BR.readLine()) != null; ) {
      s = s1.trim();
      if (s.startsWith("Units(metric")) {
       units = .5364466667;
       unitName[0] = "Kph";
       unitName[1] = "Meters";
      } else if (s.startsWith("Units(U.S.")) {
       units = 1 / 3.;
       unitName[0] = "Mph";
       unitName[1] = "Feet";
      }
      normalMapping = s.startsWith("NormalMapping(yes") || normalMapping;
      cameraShake = s.startsWith("CameraShake(yes") || cameraShake;
      try {
       userFPS = s.startsWith("fpsLimit(") ? Math.round(U.getValue(s, 0)) : userFPS;
      } catch (RuntimeException ignored) {
      }
      degradedSoundEffects = s.startsWith("DegradedSoundEffects(yes") || degradedSoundEffects;
      matchLength = s.startsWith("MatchLength(") ? Math.round(U.getValue(s, 0)) : matchLength;
      driverSeat = s.startsWith("DriverSeat(left") ? -1 : s.startsWith("DriverSeat(right") ? 1 : driverSeat;
      vehiclesInMatch = s.startsWith("#ofPlayers(") ? Math.max(1, Math.min((int) Math.round(U.getValue(s, 0)), maxPlayers)) : vehiclesInMatch;
      headsUpDisplay = s.startsWith("HUD(on") || headsUpDisplay;
      showInfo = s.startsWith("ShowInfo(yes") || showInfo;
      showVehicle = s.startsWith("ShowVehiclesInVehicleSelect(yes") || showVehicle;
      Network.userName = s.startsWith("UserName(") ? U.getString(s, 0) : Network.userName;
      Network.targetHost = s.startsWith("TargetHost(") ? U.getString(s, 0) : Network.targetHost;
      Network.port = s.startsWith("Port(") ? (int) Math.round(U.getValue(s, 0)) : Network.port;
      if (s.startsWith("GameVehicles(")) {
       vehicleModels = new ArrayList<>(Arrays.asList(s.substring("GameVehicles(".length(), s.length() - 1).split(",")));
      } else if (s.startsWith("UserSubmittedVehicles(")) {
       String[] models = U.regex.split(s);
       for (n = 1; n < models.length; n++) {
        vehicleModels.add(models[n]);
       }
      } else if (s.startsWith("UserSubmittedMaps(")) {
       String[] mapList = U.regex.split(s);
       for (n = 1; n < mapList.length; n++) {
        maps.add(mapList[n]);
       }
      }
     }
    } catch (FileNotFoundException E) {
     System.out.println("Error Loading GameSettings: " + E);
    }
    initialization = "Loading Sounds";
    checkpoint = new Sound("checkpoint");
    stunt = new Sound("stunt");
    bonus = new Sound("bonus");
    E.rain = new Sound("rain");
    E.tornado = new Sound("tornado");
    E.tsunami = new Sound("tsunami");
    E.volcano = new Sound("volcano");
    UI = new Sound("UI", 2);
    finish = new Sound("finish", 2);
    initialization = "Preparing Camera";
    Camera.boot();
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
    TE.arrow = new MeshView(TM);
    PhongMaterial PM = new PhongMaterial();
    PM.setSpecularColor(Color.color(1, 1, 1));
    TE.arrow.setMaterial(PM);
    TE.arrow.setTranslateX(0);
    TE.arrow.setTranslateY(-5);
    TE.arrow.setTranslateZ(10);
    initialization = "Creating Bonus";
    TE.bonusBig.setMaterial(new PhongMaterial());
    for (n = 0; n < 64; n++) {
     TE.bonusBalls.add(new TE.BonusBall());
     TE.bonusBalls.get(n).setMaterial(new PhongMaterial());
    }
    E.boot();
    initialization = "Loading the rest";
    U.setDiffuseRGB(E.phantomPM, 1, 1, 1, .1);
    U.setSpecularRGB(E.phantomPM, 0, 0, 0);
    PhongMaterial boundSpherePM = new PhongMaterial();
    viewerCollisionBounds.setMaterial(boundSpherePM);
    U.setDiffuseRGB(boundSpherePM, 1, 1, 1, .5);
    U.setSpecularRGB(boundSpherePM, 0, 0, 0);
    U.setLightRGB(E.mapViewerLight, 1, 1, 1);
    stage.setOnCloseRequest((WindowEvent WE) -> {
     for (PrintWriter PW : Network.out) {
      PW.println("END");
      PW.println("END");
      PW.println("CANCEL");
      PW.println("CANCEL");
     }
    });
    initialization = "";
    stunt.play(0);
   } catch (Exception E) {//<-Can we further specify without problems?
    System.out.println("Exception in secondary load thread:" + E);
   }
  });
  loadVE.setDaemon(true);
  loadVE.start();
 }

 public void start(Stage primaryStage) {
  Thread.currentThread().setPriority(10);
  primaryStage.setTitle("The Vehicular Epic");
  try {
   primaryStage.getIcons().add(new Image(new FileInputStream("images" + File.separator + "icon.png")));
  } catch (FileNotFoundException ignored) {
  }
  System.setProperty("sun.java2d.opengl", "true");//<-Is this even necessary?
  double windowSize = 1;
  boolean antiAliasing = false;
  String s;
  try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream("GameSettings"), U.standardChars))) {
   for (String s1; (s1 = BR.readLine()) != null; ) {
    s = s1.trim();
    antiAliasing = s.startsWith("AntiAliasing(yes") || antiAliasing;
    windowSize = s.startsWith("WindowSize(") ? U.getValue(s, 0) : windowSize;
   }
  } catch (IOException E) {
   System.out.println("Error Loading Settings: " + E);
  }
  primaryStage.setWidth(U.dimension.getWidth() * windowSize);
  primaryStage.setHeight(U.dimension.getHeight() * windowSize);
  width = primaryStage.getWidth();
  height = primaryStage.getHeight();
  scene3D = new SubScene(group, width, height, true, antiAliasing ? SceneAntialiasing.BALANCED : SceneAntialiasing.DISABLED);
  TE.arrowScene = new SubScene(TE.arrowGroup, width, height, false, antiAliasing ? SceneAntialiasing.BALANCED : SceneAntialiasing.DISABLED);
  canvas = new Canvas(width, height);
  E.canvas = new Canvas(width, height);
  graphicsContext = canvas.getGraphicsContext2D();
  E.graphicsContext = E.canvas.getGraphicsContext2D();
  scene = new Scene(new StackPane(scene3D, E.canvas, TE.arrowScene, canvas), width, height, false, SceneAntialiasing.DISABLED);
  primaryStage.setScene(scene);
  resetGraphics();
  U.addLight(E.sunlight);
  new AnimationTimer() {
   public void handle(long now) {
    try {
     int n;
     graphicsContext.clearRect(0, 0, width, height);
     E.graphicsContext.clearRect(0, 0, width, height);
     E.renderLevel = U.clamp(10000, E.renderLevel * (U.FPS < 30 ? .75 : 1.05), 40000);
     E.renderLevel = U.averageFPS >= 60 ? Double.POSITIVE_INFINITY : E.renderLevel;
     Camera.zoom = Math.min(Camera.zoom * Camera.zoomChange, 170);
     Camera.zoom = Camera.restoreZoom[0] && Camera.restoreZoom[1] ? Camera.defaultZoom : Camera.zoom;
     Camera.camera.setFieldOfView(Camera.zoom);
     if (userPlayer < vehicles.size() && vehicles.get(userPlayer) != null) {
      vehicles.get(userPlayer).lightBrightness = U.clamp(vehicles.get(userPlayer).lightBrightness + vehicleLightBrightnessChange);
     }
     E.lightsAdded = 0;
     if (mediaPlayer != null) {
      mediaPlayer.setGain(musicGain);
      if (mediaPlayer.complete) {
       loadSoundtrack(null);
      }
     }
     if (mouseClick) {
      mouse = keyLeft = keyRight = keyEnter = false;
     }
     boolean gamePlay = status == Status.play || status == Status.replay;
     if (mouse && (!gamePlay || !matchStarted)) {
      if (mouseX < .375) {
       keyLeft = true;
      } else if (mouseX > .625) {
       keyRight = true;
      } else {
       keyEnter = mouseClick = true;
      }
      mouseClick = status != Status.vehicleSelect && status != Status.mapJump && !status.name().contains("options") || mouseClick;
     }
     selectionTimer += tick;
     if (width != primaryStage.getWidth() || height != primaryStage.getHeight()) {
      width = primaryStage.getWidth();
      height = primaryStage.getHeight();
      scene3D.setWidth(width);
      scene3D.setHeight(height);
      TE.arrowScene.setWidth(width);
      TE.arrowScene.setHeight(height);
      canvas.setWidth(width);
      canvas.setHeight(height);
      E.canvas.setWidth(width);
      E.canvas.setHeight(height);
     }
     if (gamePlay || status == Status.paused || status == Status.optionsMatch) {
      for (Vehicle vehicle : vehicles) {
       vehicle.miscellaneous();
      }
      if (matchStarted) {
       if (gamePlay && cursorDriving) {
        mouseSteerX = 100 * (.5 - mouseX);
        mouseSteerY = 100 * (mouseY - .5);
        if (vehicles.get(userPlayer).mode != Vehicle.Mode.fly && vehicles.get(userPlayer).vehicleType != Vehicle.Type.turret) {
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
       if (Network.mode != Network.Mode.OFF) {
        Network.matchDataOut();
       }
       if (gamePlay) {
        for (Vehicle vehicle : vehicles) {
         vehicle.checkPlayer();
         if (vehicle.vehicleType == Vehicle.Type.turret) {
          vehicle.physicsTurret();
         } else {
          vehicle.physicsVehicle();
         }
         Recorder.record(vehicle);
        }
       }
       for (Vehicle vehicle : vehicles) {
        for (Explosion explosion : vehicle.explosions) {
         explosion.run(gamePlay);
        }
        for (Special special : vehicle.specials) {
         special.run(vehicle, gamePlay);
        }
       }
       if (gamePlay) {
        for (Vehicle vehicle : vehicles) {
         vehicle.runCollisions();
        }
        for (Vehicle vehicle : vehicles) {
         vehicle.damage = Math.min(vehicle.damage, vehicle.durability * 1.004);//<-Don't move into vehicle collision void
         if (vehicle.destroyed && vehicle.vehicleHit > -1) {
          scoreKill[vehicle.index < vehiclesInMatch >> 1 ? 1 : 0] += status == Status.replay ? 0 : 1;
          vehicle.AI.target = U.random(vehiclesInMatch);//<-Needed!
          vehicle.vehicleHit = -1;
         }
        }
        if (status == Status.play) {
         Recorder.recordBonusHolder();
         if (Network.mode == Network.Mode.OFF) {
          for (Vehicle vehicle : vehicles) {
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
       Network.preMatchCommunication();
       cursorDriving = false;
       if (Network.waiting) {
        U.font(.02);
        double color = globalFlick ? 0 : 1;
        U.fillRGB(color, color, color);
        if (vehiclesInMatch < 3) {
         U.text("..Waiting on " + playerNames[Network.mode == Network.Mode.HOST ? 1 : 0] + "..", .5, .25);
        } else {
         U.text("..Waiting for all Players to Start..", .5, .25);
        }
        long whoIsReady = 0;
        for (n = vehiclesInMatch; --n >= 0; ) {
         whoIsReady += Network.ready[n] ? 1 : 0;
        }
        if (whoIsReady >= vehiclesInMatch) {
         if (Network.mode == Network.Mode.HOST) {
          for (n = vehiclesInMatch; --n > 0; ) {
           Network.gamePlay(n);
          }
         } else {
          Network.gamePlay(0);
         }
         matchStarted = true;
         Network.waiting = false;
        }
       } else if (keySpace) {
        UI.play(1, 0);
        Camera.view = Camera.lastView;
        if (Network.mode == Network.Mode.OFF) {
         matchStarted = true;
        } else {
         Network.ready[userPlayer] = Network.waiting = true;
         if (Network.mode == Network.Mode.HOST) {
          for (PrintWriter PW : Network.out) {
           PW.println("Ready0");
           PW.println("Ready0");
          }
         } else {
          Network.out.get(0).println("Ready");
          Network.out.get(0).println("Ready");
         }
        }
        keySpace = false;
       }
       if (!Network.waiting) {
        U.font(.02);
        double color = globalFlick ? 0 : 1;
        U.fillRGB(color, color, color);
        if (vehicles.get(vehiclePerspective).vehicleType == Vehicle.Type.turret && (vehiclesInMatch < 2 || vehiclePerspective < vehiclesInMatch >> 1)) {
         U.text("Use Arrow Keys to place your turret(s), then", .2);
         if (keyUp || keyDown || keyLeft || keyRight) {
          UIMovementSpeedMultiple = Math.max(10, UIMovementSpeedMultiple * 1.05);
          vehicles.get(vehiclePerspective).Z += keyUp ? UIMovementSpeedMultiple * tick : 0;
          vehicles.get(vehiclePerspective).Z -= keyDown ? UIMovementSpeedMultiple * tick : 0;
          vehicles.get(vehiclePerspective).X -= keyLeft ? UIMovementSpeedMultiple * tick : 0;
          vehicles.get(vehiclePerspective).X += keyRight ? UIMovementSpeedMultiple * tick : 0;
         } else {
          UIMovementSpeedMultiple = 0;
         }
        }
        U.text("Press SPACE to Begin" + (tournament > 0 ? " Round " + tournament : ""), .25);
       }
       if (keyEscape) {
        escapeToLast(true);
       }
      }
      Recorder.playBack();
      //rendering begins here
      Camera.run(vehicles.get(vehiclePerspective));
      U.rotate(Camera.camera, Camera.YZ, -Camera.XZ);
      Camera.rotateXY.setAngle(-Camera.XY);
      E.run();
      for (Vehicle vehicle : vehicles) {
       for (Special special : vehicle.specials) {
        for (Shot shot : special.shots) {
         shot.render(special);
        }
        for (Port port : special.ports) {
         if (port.spit != null) {
          port.spit.render();
         }
        }
       }
       for (VehiclePart part : vehicle.parts) {
        U.removeLight(part.pointLight);
       }
      }
      if (defaultVehicleLightBrightness > 0) {
       for (Vehicle vehicle : vehicles) {
        U.removeLight(vehicle.burnLight);
       }
      }
      if (vehiclesInMatch < 2) {
       vehicles.get(vehiclePerspective).runGraphics(gamePlay);
      } else {
       int closest = vehiclePerspective;
       double compareDistance = Double.POSITIVE_INFINITY;
       for (Vehicle vehicle : vehicles) {
        if (vehicle.index != vehiclePerspective && U.distance(vehicles.get(vehiclePerspective), vehicle) < compareDistance) {
         closest = vehicle.index;
         compareDistance = U.distance(vehicles.get(vehiclePerspective), vehicle);
        }
       }
       if (vehicles.get(vehiclePerspective).lightBrightness >= vehicles.get(closest).lightBrightness) {
        vehicles.get(vehiclePerspective).runGraphics(gamePlay);
        vehicles.get(closest).runGraphics(gamePlay);
       } else {
        vehicles.get(closest).runGraphics(gamePlay);
        vehicles.get(vehiclePerspective).runGraphics(gamePlay);
       }
       for (Vehicle vehicle : vehicles) {
        if (vehicle.index != vehiclePerspective && vehicle.index != closest) {
         vehicle.runGraphics(gamePlay);
        }
       }
      }
      for (TrackPart trackPart : TE.trackParts) {
       trackPart.runGraphics();
      }
      TE.runBonus();
      runMatchUI(gamePlay);
      vehiclePerspective = Camera.toUserPerspective[0] && Camera.toUserPerspective[1] ? userPlayer : vehiclePerspective;
      gameFPS = Double.POSITIVE_INFINITY;
      E.renderType = E.RenderType.standard;
     }
     E.runPoolVision();
     if (status == Status.paused) {
      runPaused();
     } else if (status == Status.optionsMatch || status == Status.optionsMenu) {
      runOptions();
     } else if (status == Status.vehicleViewer) {
      runVehicleViewer(gamePlay);
     } else if (status == Status.mapViewer) {
      runMapViewer();
     } else if (status == Status.credits) {
      runCredits();
     } else if (status == Status.mainMenu) {
      runMainMenu();
     } else if (status == Status.howToPlay) {
      runHowToPlay();
     } else if (status == Status.vehicleSelect) {
      runVehicleSelect(gamePlay);
     } else if (status == Status.loadLAN) {
      runLANMenu();
     } else if (status == Status.mapError) {
      runMapError();
     } else if (status == Status.mapJump) {
      runMapJump();
     } else if (status == Status.mapView) {
      runMapView(gamePlay);
     } else if (status == Status.mapLoadPass0 || status == Status.mapLoadPass1 || status == Status.mapLoadPass2 || status == Status.mapLoadPass3 || status == Status.mapLoadPass4) {
      mapLoad();
      falsify();
     }
     globalFlick = !globalFlick;
     timer = (timer += tick) > 20 ? 0 : timer;
     if (status != Status.vehicleSelect) {
      for (Vehicle vehicle : vehicles) {
       if (vehicle != null && !vehicle.destroyed) {
        vehicle.flicker = !vehicle.flicker;
       }
      }
     }
     selectionTimer = (selectionTimer > selectionWait ? 0 : selectionTimer) + 5 * tick;
     if (keyLeft || keyRight || keyUp || keyDown || keySpace || keyEnter) {
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
     if (showInfo) {
      U.fillRGB(0, 0, 0, opacityUI.minimal);
      U.fillRectangle(.25, .9625, .15, .05);
      U.fillRectangle(.75, .9625, .15, .05);
      U.fillRGB(1, 1, 1);
      U.font(.015);
      U.text("Objects: " + (group.getChildren().size() + TE.arrowGroup.getChildren().size() + E.lights.getChildren().size()), .25, .965);
      U.font(.02);
      U.text(Math.round(U.averageFPS) + " FPS", .75, .965);
     }
     long time = System.nanoTime();
     tick = Math.min((time - lastTime + 500000) * .00000002, 1);
     lastTime = time;
    } catch (Exception E) {//<-It's for the entire game loop--a general exception is probably most surefire
     try (PrintWriter PW = new PrintWriter(new File("V.E. EXCEPTION"), U.standardChars)) {
      E.printStackTrace(PW);
     } catch (IOException ignored) {
     }
     E.printStackTrace();
     handleException();
    }
   }
  }.start();
  loadVE(primaryStage);
  primaryStage.show();
 }

 private static void handleException() {
  error = "An Exception Occurred!" + U.lineSeparator + "A File with the exception has been saved to the game folder";
  status = Status.mainMenu;
  tournament = selected = 0;
  resetGraphics();
  clearSounds();
  falsify();
  if (Network.mode == Network.Mode.HOST) {
   for (PrintWriter PW : Network.out) {
    PW.println("CANCEL");
    PW.println("CANCEL");
   }
  } else if (Network.mode == Network.Mode.JOIN) {
   Network.out.get(0).println("CANCEL");
   Network.out.get(0).println("CANCEL");
  }
 }

 private void mapLoad() {
  TE.instanceSize = 1;
  TE.instanceScale = new double[]{1, 1, 1};
  TE.randomX = TE.randomY = TE.randomZ = 0;
  if (status == Status.mapLoadPass0) {
   scene.setCursor(Cursor.WAIT);
  } else if (status == Status.mapLoadPass1) {
   resetGraphics();
   U.remove(E.sun, E.ground);
   U.removeLight(E.sunlight);
   TE.addArrow();
   vehicles.clear();
   TE.trackParts.clear();
   E.groundPlates.clear();
   E.clouds.clear();
   E.stars.clear();
   E.raindrops.clear();
   E.snowballs.clear();
   E.tornadoParts.clear();
   E.tsunamiParts.clear();
   E.fires.clear();
   E.boulders.clear();
   E.volcanoRocks.clear();
   E.meteors.clear();
   TE.points.clear();
   TE.checkpoints.clear();
   E.terrain = " ground ";
   E.skyInverse = Color.color(1, 1, 1);
   E.groundInverse = Color.color(1, 1, 1);
   Camera.camera.setFarClip(Camera.clipRange.maximumFar);
   E.sunX = E.sunY = E.sunZ
   = E.wind = defaultVehicleLightBrightness
   = E.skyRGB[0] = E.skyRGB[1] = E.skyRGB[2]
   = E.groundRGB[0] = E.groundRGB[1] = E.groundRGB[2] = E.terrainRGB[0] = E.terrainRGB[1] = E.terrainRGB[2] = E.groundLevel
   = E.poolDepth = 0;
   E.windStormExists = randomStartAngle = E.slowVehiclesWhenAtLimit = guardCheckpoint = TE.fixPointsExist = E.poolExists = E.tornadoMovesFixpoints = false;
   E.gravity = 7;
   E.soundMultiple = 1;
   U.setLightRGB(E.sunlight, 1, 1, 1);
   U.setLightRGB(E.ambientLight, 0, 0, 0);
   scene3D.setFill(Color.color(0, 0, 0));
   U.setDiffuseRGB((PhongMaterial) E.ground.getMaterial(), 0, 0, 0);
   ((PhongMaterial) E.ground.getMaterial()).setSpecularMap(null);
   E.lightningExists = E.volcanoExists = false;
   E.mapBounds.left = E.mapBounds.backward = E.mapBounds.Y = Double.NEGATIVE_INFINITY;
   E.mapBounds.right = E.mapBounds.forward = E.viewableMapDistance = Double.POSITIVE_INFINITY;
   speedLimitAI = Double.POSITIVE_INFINITY;
   E.poolType = E.Pool.water;
  }
  int n;
  String s = "";
  try (BufferedReader BR = new BufferedReader(new InputStreamReader(Objects.requireNonNull(U.getMapFile(map)), U.standardChars))) {
   for (; (s = BR.readLine()) != null; ) {
    s = s.trim();
    if (status == Status.mapLoadPass2) {
     mapName = s.startsWith("name") ? U.getString(s, 0) : mapName;
     if (s.startsWith("ambientLight(")) {
      U.setLightRGB(E.ambientLight, U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
     }
     E.loadSky(s);
     E.loadGround(s);
     E.loadTerrain(s);
     if (s.startsWith("viewDistance(")) {
      E.viewableMapDistance = U.getValue(s, 0);
      Camera.camera.setFarClip(U.clamp(Camera.clipRange.normalNear + 1, U.getValue(s, 0), Camera.clipRange.maximumFar));
     } else if (s.startsWith("soundTravel(")) {
      E.soundMultiple = U.getValue(s, 0);
     }
     E.gravity = s.startsWith("gravity(") ? U.getValue(s, 0) : E.gravity;
     E.loadSun(s);
     defaultVehicleLightBrightness = s.startsWith("defaultBrightness(") ? U.getValue(s, 0) : defaultVehicleLightBrightness;
     randomStartAngle = s.startsWith("randomStartAngle") || randomStartAngle;
     E.mapBounds.left = s.startsWith("xLimitLeft(") ? U.getValue(s, 0) : E.mapBounds.left;
     E.mapBounds.right = s.startsWith("xLimitRight(") ? U.getValue(s, 0) : E.mapBounds.right;
     E.mapBounds.forward = s.startsWith("zLimitFront(") ? U.getValue(s, 0) : E.mapBounds.forward;
     E.mapBounds.backward = s.startsWith("zLimitBack(") ? U.getValue(s, 0) : E.mapBounds.backward;
     E.mapBounds.Y = s.startsWith("yLimit(") ? U.getValue(s, 0) : E.mapBounds.Y;
     E.slowVehiclesWhenAtLimit = s.startsWith("slowVehiclesWhenAtLimit") || E.slowVehiclesWhenAtLimit;
     speedLimitAI = s.startsWith("speedLimit(") ? U.getValue(s, 0) : speedLimitAI;
     E.groundLevel = s.startsWith("noGround") ? Double.POSITIVE_INFINITY : E.groundLevel;
     guardCheckpoint = s.startsWith("guardCheckpoint") || guardCheckpoint;
     E.wind = s.startsWith("wind(") ? U.getValue(s, 0) : E.wind;
     if (s.startsWith("snow(")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       E.snowballs.add(new Snowball());
      }
     } else if (s.startsWith("windstorm")) {
      E.windStormExists = true;
      E.windX = U.randomPlusMinus(E.wind);
      E.windZ = U.randomPlusMinus(E.wind);
      E.windstorm = new Sound("storm" + U.getString(s, 0));
     }
     E.loadClouds(s);
     E.loadStorm(s);
     E.loadTornado(s);
     E.loadMountains(s);
     E.loadPool(s);
     if (s.startsWith("stars(")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       E.stars.add(new Star());
      }
     } else if (s.startsWith("trees(")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       TE.trackParts.add(
       U.random() < .5 ?
       new TrackPart(TE.getTrackPartIndex("tree0"), U.randomPlusMinus(800000), 0, U.randomPlusMinus(800000), 0, 1 + Math.sqrt(U.random(16.)), TE.instanceScale)
       :
       new TrackPart(TE.getTrackPartIndex(U.random() < .5 ? "tree1" : "tree2"), U.randomPlusMinus(800000), 0, U.randomPlusMinus(800000), 0));
      }
     } else if (s.startsWith("palmTrees(")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       TE.trackParts.add(new TrackPart(TE.getTrackPartIndex("treepalm"), U.randomPlusMinus(800000), 0, U.randomPlusMinus(800000), 0, 1 + U.random(.6), TE.instanceScale));
      }
     } else if (s.startsWith("cacti(")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       TE.trackParts.add(new TrackPart(TE.getTrackPartIndex("cactus" + U.random(3)), U.randomPlusMinus(800000), 0, U.randomPlusMinus(800000), 0, .5 + U.random(.5), TE.instanceScale));
      }
     } else if (s.startsWith("rocks(")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       TE.trackParts.add(new TrackPart(U.randomPlusMinus(800000), U.randomPlusMinus(800000), 0, 100 + U.random(400.), U.random(100.), U.random(200.), true, false, false));
      }
     }
     E.loadFire(s);
     E.loadBoulders(s);
     E.loadTsunami(s);
     E.loadVolcano(s);
     E.loadMeteors(s);
     if (s.startsWith("music(")) {
      loadSoundtrack(U.getString(s, 0));
     }
    }
    TE.instanceSize = s.startsWith("size(") ? U.getValue(s, 0) : TE.instanceSize;
    if (s.startsWith("scale(")) {
     try {
      TE.instanceScale[0] = U.getValue(s, 0);
      TE.instanceScale[1] = U.getValue(s, 1);
      TE.instanceScale[2] = U.getValue(s, 2);
     } catch (RuntimeException e) {
      TE.instanceScale[0] = TE.instanceScale[1] = TE.instanceScale[2] = U.getValue(s, 0);
     }
    }
    if (status == Status.mapLoadPass3) {
     TE.randomX = s.startsWith("randomX(") ? U.getValue(s, 0) : TE.randomX;
     TE.randomY = s.startsWith("randomY(") ? U.getValue(s, 0) : TE.randomY;
     TE.randomZ = s.startsWith("randomZ(") ? U.getValue(s, 0) : TE.randomZ;
     if (U.startsWith(s, "(", "curve(")) {
      int trackNumber = TE.getTrackPartIndex(U.getString(s, 0));//<-Returns '-1' on exception
      if (trackNumber < 0 && !U.getString(s, 0).isEmpty()) {
       System.out.println("Map Part List Exception (" + mapName + ")");
       System.out.println("At line: " + s);
      }
      long[] random = {Math.round(U.randomPlusMinus(TE.randomX)), Math.round(U.randomPlusMinus(TE.randomY)), Math.round(U.randomPlusMinus(TE.randomZ))};
      if (trackNumber == TE.getTrackPartIndex(MapModels.checkpoint.name())) {
       long cornerDisplace = mapName.equals("Death Pit") ? 12000 : mapName.equals("Arctic Slip") ? 4500 : 0;
       random[0] += U.random() < .5 ? cornerDisplace : -cornerDisplace;
       random[2] += U.random() < .5 ? cornerDisplace : -cornerDisplace;
      }
      if (mapName.equals("the Linear Accelerator")) {
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
      double
      summedPositionX = U.getValue(s, 1) + random[0],
      summedPositionZ = U.getValue(s, 2) + random[2],
      summedPositionY = U.getValue(s, 3) + random[1];
      if (s.startsWith("curve(")) {
       double angle = 90;
       try {
        angle += U.getValue(s, 9);
       } catch (RuntimeException ignored) {
       }
       double curveStart = U.getValue(s, 4), curveEnd = U.getValue(s, 5),
       iterationRate = U.getValue(s, 6), curveRadius = U.getValue(s, 7),
       curveHeight = U.getValue(s, 8) / Math.abs(curveStart - curveEnd);
       for (double iteration = curveStart; ; iteration += iteration < curveEnd ? iterationRate : -iterationRate) {
        TE.addTrackPart(s, trackNumber,
        summedPositionX + curveRadius * U.sin(iteration),
        summedPositionY + curveHeight * Math.abs(iteration - curveStart),
        summedPositionZ + curveRadius * U.cos(iteration),
        -iteration + angle);
        if (Math.abs(iteration - curveEnd) < iterationRate) {
         break;
        }
       }
      } else {
       TE.addTrackPart(s, trackNumber, summedPositionX, summedPositionY, summedPositionZ, Double.NaN);
      }
     }
    }
   }
  } catch (Exception E) {//<-Don't further specify
   status = Status.mapError;
   System.out.println("Map Error (" + mapName + ")");
   System.out.println(E);
   System.out.println("At line: " + s);
   E.printStackTrace();
  }
  if (status == Status.mapLoadPass3) {
   if (mapName.equals("Ghost City")) {
    for (n = 5; n < 365; n += 10) {
     TE.instanceSize = 2500 + U.random(2500.);
     TE.instanceScale[0] = 1 + U.random(2.);
     TE.instanceScale[1] = 1 + U.random(2.);
     TE.instanceScale[2] = 1 + U.random(2.);
     double calculatedX = 112500 * -StrictMath.sin(Math.toRadians(n)),
     calculatedZ = 112500 * StrictMath.cos(Math.toRadians(n));
     TE.trackParts.add(new TrackPart(TE.getTrackPartIndex(MapModels.cube.name()), calculatedX, 0, calculatedZ, n - 90, TE.instanceSize, TE.instanceScale));
    }
   } else if (mapName.equals("Military Base")) {//Put this in 'military'--not the source!
    TE.trackParts.add(new TrackPart(getVehicleIndex("EPIC TANK"), -48000, 0, 3000, -90, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("EPIC TANK"), -48000, 0, -3000, -90, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("EPIC TANK"), -48000, 0, 4000, -90, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("EPIC TANK"), -48000, 0, -4000, -90, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("EPIC TANK"), -72000, 0, 3000, 90, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("EPIC TANK"), -72000, 0, -3000, 90, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("EPIC TANK"), -72000, 0, 4000, 90, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("EPIC TANK"), -72000, 0, -4000, 90, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Desert Humvee"), -44000, 0, 20000, -90, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Desert Humvee"), -44000, 0, -20000, -90, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Desert Humvee"), -44000, 0, 25000, -90, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Desert Humvee"), -44000, 0, -25000, -90, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Desert Humvee"), -44000, 0, 30000, -90, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Desert Humvee"), -44000, 0, -30000, -90, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Desert Humvee"), -44000, 0, 35000, -90, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Desert Humvee"), -44000, 0, -35000, -90, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Turbo Prop"), -55000, 0, 5000, 180, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Turbo Prop"), -55000, 0, -5000, 0, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Stealth Fighter"), -57500, 0, 5000, 180, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Stealth Fighter"), -57500, 0, -5000, 0, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("F-22 Raptor"), -60000, 0, 5000, 180, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("F-22 Raptor"), -60000, 0, -5000, 0, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Heavy Meddle"), -62500, 0, 5000, 180, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Heavy Meddle"), -62500, 0, -5000, 0, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("MiG 31"), -65000, 0, 5000, 180, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("MiG 31"), -65000, 0, -5000, 0, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Desert Humvee"), 29000, 0, 1000, 90, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Desert Humvee"), 29000, 0, -1000, 90, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Desert Humvee"), -101000, 0, 1000, 90, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Desert Humvee"), -101000, 0, -1000, 90, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Gun Turret"), -45150, -500, 36100, -45, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Gun Turret"), -45150, -500, -36100, -135, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Gun Turret"), -21000, 0, 2000, 0, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Gun Turret"), -21000, 0, -2000, 180, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Rail Gun"), 30000, -250, 30000, 45, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Rail Gun"), 30000, -250, -30000, 135, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Rail Gun"), 90000, -250, 30000, -45, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Rail Gun"), 90000, -250, -30000, -135, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Missile Turret"), 21000, 0, 39000, 45, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Missile Turret"), 21000, 0, -39000, 135, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Missile Turret"), -21000, 0, 39000, -45, true));
    TE.trackParts.add(new TrackPart(getVehicleIndex("Missile Turret"), -21000, 0, -39000, -135, true));
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
     TE.trackParts.add(new TrackPart(n, xRandom, 0, zRandom, randomXZ, true));
     TE.points.add(new Point());
     TE.points.get(TE.points.size() - 1).X = xRandom;
     TE.points.get(TE.points.size() - 1).Z = zRandom;
     TE.points.get(TE.points.size() - 1).type = Point.Type.checkpoint;
     TE.checkpoints.add(new Checkpoint());
     TE.checkpoints.get(TE.checkpoints.size() - 1).X = xRandom;
     TE.checkpoints.get(TE.checkpoints.size() - 1).Z = zRandom;
     TE.checkpoints.get(TE.checkpoints.size() - 1).type = Checkpoint.Type.passAny;
     TE.checkpoints.get(TE.checkpoints.size() - 1).location = TE.points.size() - 1;
     TE.trackParts.get(TE.trackParts.size() - 1).checkpointNumber = TE.checkpoints.size() - 1;
    }
   }
   if (E.poolType == E.Pool.lava) {
    for (TsunamiPart tsunamiPart : E.tsunamiParts) {//Setting the illumination here in case the lava pool gets called AFTER tsunami definition
     U.setSelfIllumination((PhongMaterial) tsunamiPart.C.getMaterial(), E.lavaSelfIllumination[0], E.lavaSelfIllumination[1], E.lavaSelfIllumination[2]);
    }
   }
   TE.bonusX = TE.bonusY = TE.bonusZ = 0;
   U.add(TE.bonusBig);
   for (TE.BonusBall bonusBall : TE.bonusBalls) {
    U.add(bonusBall);
   }
  } else if (status == Status.mapLoadPass4) {
   if (!inViewer) {
    for (n = vehiclesInMatch; --n >= 0; ) {
     vehicles.add(null);
    }
    vehicles.set(userPlayer, new Vehicle(vehicleNumber[userPlayer], userPlayer, true));
    for (n = vehiclesInMatch; --n >= 0; ) {
     if (n != userPlayer) {
      vehicles.set(n, new Vehicle(vehicleNumber[n], n, true));
     }
    }
   }
   reset();
  }
  String loadText = status == Status.mapLoadPass0 ? "Removing Previous Content" : status == Status.mapLoadPass1 ? "Loading Properties & Scenery" : status == Status.mapLoadPass2 ? "Adding Track Parts" : "Adding " + vehiclesInMatch + " Vehicle(s)";
  U.fillRGB(0, 0, 0);
  U.fillRectangle(.5, .5, 1, 1);
  U.font(.025);
  U.fillRGB(1, 1, 1);
  U.text(tournament > 0 ? "Round " + tournament + (tournament > 5 ? "--Overtime!" : "") : "", .425);
  U.text(mapName, .475);
  U.text(".." + loadText + "..", .525);
  if (status != Status.mapError) {
   status = status == Status.mapLoadPass0 ? Status.mapLoadPass1 : status == Status.mapLoadPass1 ? Status.mapLoadPass2 : status == Status.mapLoadPass2 ? Status.mapLoadPass3 : status == Status.mapLoadPass3 ? Status.mapLoadPass4 : (inViewer ? Status.mapViewer : Network.mode == Network.Mode.JOIN ? Status.play : Status.mapView);
  }
  E.renderType = E.RenderType.fullDistanceALL;
 }

 private void loadSoundtrack(String s) {
  if (!songName.equals(s)) {
   songName = s != null ? s : songName;
   Thread thread = new Thread(() -> {
    if (mediaPlayer != null) {
     mediaPlayer.close();
    }
    try {
     mediaPlayer = new Player(new FileInputStream(new File("music" + File.separator + songName + ".mp3")));
    } catch (Exception E) {//<-do NOT change
     System.out.println("Problem loading Music: " + E);
    }
    if (mediaPlayer != null) {
     try {
      mediaPlayer.play();
     } catch (JavaLayerException ignored) {
     }
    }
   });
   thread.setDaemon(true);
   thread.start();
  }
 }

 static int getMapName(String s) {
  int n;
  String s1;
  for (n = 0; n < maps.size(); n++) {
   try (BufferedReader BR = new BufferedReader(new InputStreamReader(Objects.requireNonNull(U.getMapFile(n)), U.standardChars))) {
    for (String s2; (s2 = BR.readLine()) != null; ) {
     s1 = s2.trim();
     mapName = s1.startsWith("name") ? U.getString(s1, 0) : mapName;
    }
   } catch (IOException e) {
    status = Status.mapError;
    e.printStackTrace();
   }
   if (s.equals(mapName)) {
    break;
   }
  }
  return n;
 }

 static int getVehicleIndex(String s) {
  String s1, s3 = "";
  int n;
  for (n = 0; n < vehicleModels.size(); n++) {
   File F = new File("models" + File.separator + vehicleModels.get(n));
   if (!F.exists()) {
    F = new File("models" + File.separator + "User-Submitted" + File.separator + vehicleModels.get(n));
   }
   if (!F.exists()) {
    F = new File("models" + File.separator + "basic");
   }
   try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream(F), U.standardChars))) {
    for (String s2; (s2 = BR.readLine()) != null; ) {
     s1 = s2.trim();
     if (s1.startsWith("name")) {
      s3 = U.getString(s1, 0);
      break;
     }
    }
   } catch (IOException e) {//<-Don't bother
    e.printStackTrace();
   }
   if (s.equals(s3)) {
    break;
   }
  }
  return n;
 }

 public static String getVehicleName(int in) {//<-Keep method in case we need it later
  String s, s3 = "";
  File F = new File("models" + File.separator + vehicleModels.get(in));
  if (!F.exists()) {
   F = new File("models" + File.separator + "User-Submitted" + File.separator + vehicleModels.get(in));
  }
  if (!F.exists()) {
   F = new File("models" + File.separator + "basic");
  }
  try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream(F), U.standardChars))) {
   for (String s2; (s2 = BR.readLine()) != null; ) {
    s = s2.trim();
    if (s.startsWith("name")) {
     s3 = U.getString(s, 0);
     break;
    }
   }
  } catch (IOException e) {
   e.printStackTrace();
  }
  return s3;
 }

 private static void runVehicleSelect(boolean gamePlay) {
  int n;
  U.font(.03);
  if (Network.waiting) {
   U.fillRGB(1, 1, 1);
   if (vehiclesInMatch < 3) {
    U.text("..Please Wait for " + playerNames[Network.mode == Network.Mode.HOST ? 1 : 0] + " to Select Vehicle..", .5, .5);
   } else {
    U.text("..Please Wait for all other players to Select their Vehicle..", .5, .5);
   }
   if (Network.mode == Network.Mode.HOST) {
    if (timer <= 0) {
     for (PrintWriter PW : Network.out) {
      PW.println("Vehicle0(" + vehicles.get(0).vehicleName);
     }
    }
    for (n = vehiclesInMatch; --n > 0; ) {
     String s = Network.readIn(n - 1);
     if (s.startsWith("CANCEL")) {
      escapeToLast(false);
     } else if (s.startsWith("Vehicle(")) {
      vehicleNumber[n] = getVehicleIndex(U.getString(s, 0));
      if (vehiclesInMatch > 2) {
       for (PrintWriter out : Network.out) {
        out.println("Vehicle" + n + "(" + U.getString(s, 0));
       }
      }
      Network.ready[n] = true;
      System.out.println(playerNames[n] + " has selected Vehicle");
     }
    }
   } else {
    if (timer <= 0) {
     Network.out.get(0).println("Vehicle(" + vehicles.get(0).vehicleName);
    }
    String s = Network.readIn(0);
    if (s.startsWith("CANCEL")) {
     escapeToLast(false);
    } else {
     for (n = vehiclesInMatch; --n >= 0; ) {
      if (n != userPlayer) {
       if (s.startsWith("Vehicle" + n + "(")) {
        vehicleNumber[n] = getVehicleIndex(U.getString(s, 0));
        Network.ready[n] = true;
       }
      }
     }
    }
   }
   long whoIsReady = 0;
   for (n = vehiclesInMatch; --n >= 0; ) {
    whoIsReady = Network.ready[n] ? ++whoIsReady : whoIsReady;
   }
   if (whoIsReady >= vehiclesInMatch) {
    status = Status.mapJump;
    Network.waiting = false;
   }
  } else {
   if (section == 0) {
    resetGraphics();
    vehicles.clear();
    scene3D.setFill(Color.color(0, 0, 0));
    Camera.X = Camera.Z = Camera.YZ = Camera.XY = 0;
    Camera.Y = -250;
    Camera.XZ = 180;
    Camera.rotateXY.setAngle(0);
    Camera.setAngleTable();
    U.setTranslate(E.ground, 0, 0, 0);
    U.setDiffuseRGB((PhongMaterial) E.ground.getMaterial(), .1, .1, .1);
    for (Raindrop raindrop : E.raindrops) {
     U.add(raindrop.C);
    }
    for (Snowball snowball : E.snowballs) {
     U.add(snowball.S);
    }
    addVehicleModel(vehicleNumber[vehiclePick], showVehicle);
    sameVehicles = false;
    section = 1;
   }
   sameVehicles = vehiclePick <= 0 && Network.mode == Network.Mode.OFF && sameVehicles;
   Vehicle V = vehicles.get(0);
   U.fillRGB(1, 1, 1);
   U.text("SELECT " + (inViewer ? "VEHICLE TO EDIT" : vehiclePick > 0 ? "PLAYER #" + vehiclePick : "VEHICLE"), .075);
   V.inDriverView = false;
   V.runGraphics(gamePlay);
   V.Z = -1000;
   V.XZ += (.5 - mouseX) * 20 * tick;
   if (!Double.isNaN(V.spinnerSpeed)) {
    V.spinnerXZ = -V.XZ * 2;
   }
   if (V.vehicleType == Vehicle.Type.turret) {
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
     U.text(Network.mode == Network.Mode.OFF ? "(GREEN TEAM)" : "You're on the GREEN TEAM", .1);
    } else {
     U.fillRGB(1, 0, 0);
     U.text(Network.mode == Network.Mode.OFF ? "(RED TEAM)" : "You're on the RED TEAM", .1);
    }
   }
   U.fillRGB(1, 1, 1);
   U.font(.02);
   //U.textR("" + V., .9, .6);
   U.text("<-LAST", .125, .5);
   U.text("NEXT->", .875, .5);
   U.font(.01);
   if (showVehicle) {
    U.text("Meshes: " + V.parts.size(), .8);
    U.text("Vertices: " + V.vertexQuantity, .825);
   }
   U.text("Vehicles [" + (showVehicle ? "SHOW (can be slow--not recommended)" : "HIDE") + "]", .875);
   U.text("CONTINUE" + (sameVehicles ? " (with all players as " + V.vehicleName + ")" : ""), .9);
   boolean singleSelection = !inViewer && (vehiclesInMatch < 2 || Network.mode != Network.Mode.OFF);
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
   U.textL(V.vehicleType == Vehicle.Type.aircraft ? "Aircraft (Flying)" : V.vehicleType == Vehicle.Type.turret ? "Turret (Fixed)" : "Vehicle (Grounded)", lineLR, Y0);
   U.textR("Top Speed:", lineLL, Y1);
   U.textL(V.vehicleType == Vehicle.Type.turret ? "N/A" : V.topSpeeds[1] >= Long.MAX_VALUE ? "None" : V.speedBoost > 0 && V.topSpeeds[2] >= Long.MAX_VALUE ? "None (Speed Boost)" : V.speedBoost > 0 ? Math.round(V.topSpeeds[2] * units) + " " + unitName[0] + " (Speed Boost)" : Math.round(V.topSpeeds[1] * units) + " " + unitName[0], lineLR, Y1);
   U.textR("Acceleration Phases:", lineLL, Y2);
   U.textL(V.vehicleType == Vehicle.Type.turret ? "N/A" : "+" + V.accelerationStages[0] + ",  +" + V.accelerationStages[1], lineLR, Y2);
   U.textR("Handling Response:", lineLL, Y3);
   U.textL(V.turnRate == Double.POSITIVE_INFINITY ? "Instant" : String.valueOf(V.turnRate), lineLR, Y3);
   U.textR("Stunt Response:", lineLL, Y4);
   U.textL(V.vehicleType == Vehicle.Type.vehicle ? String.valueOf(V.airAcceleration == Double.POSITIVE_INFINITY ? "Instant" : (float) V.airAcceleration) : "N/A", lineLR, Y4);
   U.textR("Special(s):", lineLL, Y5);
   StringBuilder specials = new StringBuilder();
   boolean hasForceField = false;
   if (V.specials.isEmpty()) {
    specials.append("None");
   } else {
    for (Special special : V.specials) {
     specials.append(special.type.name()).append(", ");
     hasForceField = special.type == Special.Type.forcefield || hasForceField;
    }
   }
   U.textL(String.valueOf(specials), lineLR, Y5);
   U.textR("Collision Damage Rating:", lineRL, Y0);
   U.textL((V.vehicleType != Vehicle.Type.aircraft && V.damageDealt[U.random(4)] >= 100) || V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name()) ? "Instant-Kill" :
   hasForceField || !Double.isNaN(V.spinnerSpeed) ? "'Inconsistent'" :
   String.valueOf((float) ((V.damageDealt[0] + V.damageDealt[1] + V.damageDealt[2] + V.damageDealt[3]) * .25)), lineRR, Y0);
   U.textR("Fragility:", lineRL, Y1);
   U.textL(String.valueOf(V.fragility), lineRR, Y1);
   U.textR("Self-Repair:", lineRL, Y2);
   U.textL(String.valueOf(V.selfRepair), lineRR, Y2);
   U.textR("Total Durability:", lineRL, Y3);
   U.textL(String.valueOf(V.durability), lineRR, Y3);
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
     UI.play(0, 0);
     usingKeys = true;
    }
    if (keyRight) {
     removeVehicleModel();
     vehicleNumber[vehiclePick] = ++vehicleNumber[vehiclePick] >= vehicleModels.size() ? 0 : vehicleNumber[vehiclePick];
     if (vehiclePick == userPlayer) {
      userRandomRGB[0] = U.random();
      userRandomRGB[1] = U.random();
      userRandomRGB[2] = U.random();
     }
     addVehicleModel(vehicleNumber[vehiclePick], showVehicle);
     UI.play(0, 0);
    }
    if (keyLeft) {
     removeVehicleModel();
     vehicleNumber[vehiclePick] = --vehicleNumber[vehiclePick] < 0 ? vehicleModels.size() - 1 : vehicleNumber[vehiclePick];
     if (vehiclePick == userPlayer) {
      userRandomRGB[0] = U.random();
      userRandomRGB[1] = U.random();
      userRandomRGB[2] = U.random();
     }
     addVehicleModel(vehicleNumber[vehiclePick], showVehicle);
     UI.play(0, 0);
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
       if (Network.mode == Network.Mode.OFF) {
        vehiclePick++;
        if (vehiclePick < vehiclesInMatch) {
         addVehicleModel(vehicleNumber[vehiclePick], showVehicle);
        }
       }
       if (vehiclePick > (vehiclesInMatch * (tournament > 0 ? .5 : 1)) - 1 || selected == 1) {
        if (inViewer) {
         status = Status.vehicleViewer;
         section = 0;
        } else if (Network.mode != Network.Mode.OFF) {
         Network.ready[userPlayer] = Network.waiting = true;
        } else {
         status = Status.mapJump;
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
     UI.play(1, 0);
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
  U.rotate(Camera.camera, Camera.YZ, -Camera.XZ);
  for (Raindrop raindrop : E.raindrops) {
   raindrop.run();
  }
  for (Snowball snowball : E.snowballs) {
   snowball.run();
  }
  gameFPS = Double.POSITIVE_INFINITY;
 }

 private static void addVehicleModel(int v, boolean show) {
  vehicles.clear();
  vehicles.add(new Vehicle(v, 0, false, show));
  vehicles.get(0).lightBrightness = defaultVehicleLightBrightness;
  for (VehiclePart part : vehicles.get(0).parts) {
   U.add(part.MV);
   part.MV.setVisible(true);
   part.setDrawMode(showWireframe);
  }
 }

 private static void removeVehicleModel() {
  if (!vehicles.isEmpty() && vehicles.get(0) != null) {
   for (VehiclePart part : vehicles.get(0).parts) {
    U.remove(part.MV);
    U.removeLight(part.pointLight);
   }
  }
 }

 private static void runVehicleViewer(boolean gamePlay) {
  U.font(.03);
  U.fillRGB(1, 1, 1);
  U.text("Vehicle Viewer", .075);
  boolean loadModel = false;
  if (section < 1) {
   resetGraphics();
   U.remove(E.sun, E.ground);
   U.removeLight(E.sunlight);
   scene3D.setFill(Color.color(0, 0, 0));
   U.setLightRGB(E.sunlight, 1, 1, 1);
   Camera.X = Camera.Y = Camera.Z = Camera.XZ = Camera.YZ = Camera.XY = viewerY = viewerYZ = 0;
   E.viewableMapDistance = Double.POSITIVE_INFINITY;
   U.rotate(Camera.camera, Camera.YZ, -Camera.XZ);
   Camera.rotateXY.setAngle(0);
   Camera.setAngleTable();
   viewerZ = 1000;
   viewerXZ = 180;
   showCollisionBounds = false;//<-Covers vehicle otherwise
   loadModel = true;
   U.setLightRGB(E.ambientLight, 1, 1, 1);
   U.setTranslate(E.sunlight, 0, -Long.MAX_VALUE, 0);
   if (vehicleViewer3DLighting) {
    U.setLightRGB(E.ambientLight, .5, .5, .5);
    U.addLight(E.sunlight);
   }
   section = 1;
  }
  viewerXZ += keyLeft ? 5 : 0;
  viewerXZ -= keyRight ? 5 : 0;
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
   vehicles.get(0).runGraphics(gamePlay);
   U.font(.02);
   U.text(vehicles.get(0).vehicleName, .1125);
   U.font(.0125);
   U.fillRGB(1, 1, 1);
   U.text("Meshes: " + vehicles.get(0).parts.size(), .25, .8);
   U.text("Vertices: " + vehicles.get(0).vertexQuantity, .75, .8);
   if (showCollisionBounds) {
    U.setTranslate(viewerCollisionBounds, vehicles.get(0));
    U.add(viewerCollisionBounds);
   } else {
    U.remove(viewerCollisionBounds);
   }
  }
  U.fillRGB(1, 1, 1);
  U.text("Move Vehicle with the T, G, U, and J Keys. Rotate with the Arrow Keys", .95 + textOffset);
  if (globalFlick) {
   U.strokeRGB(1, 1, 1);
   U.drawRectangle(.5, selected == 0 ? .825 : selected == 1 ? .85 : selected == 2 ? .875 : selected == 3 ? .9 : .925, width, selectionHeight);
  }
  U.text("RE-LOAD VEHICLE FILE", .825 + textOffset);
  U.text("3D Lighting [" + (vehicleViewer3DLighting ? "ON" : "OFF") + "]", .85 + textOffset);
  U.text("Draw Mode [" + (showWireframe ? "LINE" : "FILL") + "]", .875 + textOffset);
  U.text("Collision Bounds [" + (showCollisionBounds ? "SHOW" : "HIDE") + "]", .9 + textOffset);
  U.text("BACK TO MAIN MENU", .925 + textOffset);
  if (selectionTimer > selectionWait) {
   if (keyUp) {
    selected = --selected < 0 ? 4 : selected;
    usingKeys = true;
    UI.play(0, 0);
   }
   if (keyDown) {
    selected = ++selected > 4 ? 0 : selected;
    usingKeys = true;
    UI.play(0, 0);
   }
  }
  if (keySpace || keyEnter) {
   if (selected == 0) {
    loadModel = true;
   } else if (selected == 1) {
    vehicleViewer3DLighting = !vehicleViewer3DLighting;
    if (vehicleViewer3DLighting) {
     U.setLightRGB(E.ambientLight, .5, .5, .5);
     U.addLight(E.sunlight);
    } else {
     U.setLightRGB(E.ambientLight, 1, 1, 1);
     U.removeLight(E.sunlight);
    }
   } else if (selected == 2) {
    showWireframe = !showWireframe;
    for (VehiclePart part : vehicles.get(0).parts) {
     part.setDrawMode(showWireframe);
    }
   } else if (selected == 3) {
    showCollisionBounds = !showCollisionBounds;
    viewerCollisionBounds.setRadius(vehicles.get(0).collisionRadius);
   } else {
    status = Status.mainMenu;
    removeVehicleModel();
   }
   UI.play(1, 0);
   keySpace = keyEnter = false;
  }
  if (keyEscape) {
   status = Status.mainMenu;
   removeVehicleModel();
   UI.play(1, 0);
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
   selected = Math.abs(.8 - mouseY) < clickRangeY ? 0 : Math.abs(.825 - mouseY) < clickRangeY ? 1 : Math.abs(.85 - mouseY) < clickRangeY ? 2 : Math.abs(.875 - mouseY) < clickRangeY ? 3 : Math.abs(.9 - mouseY) < clickRangeY ? 4 : selected;
  }
  gameFPS = Double.POSITIVE_INFINITY;
  E.renderType = E.RenderType.fullDistance;
 }

 private void runMapViewer() {
  U.font(.03);
  U.fillRGB(1, 1, 1);
  U.text("Map Viewer", .075);
  if (section < 1) {
   Camera.X = Camera.Z = Camera.XZ = Camera.YZ = Camera.XY = 0;
   viewerY = -5000;
   section = 1;
  }
  Camera.XZ -= keyLeft ? 5 : 0;
  Camera.XZ += keyRight ? 5 : 0;
  Camera.YZ += keyUp ? 5 : 0;
  Camera.YZ -= keyDown ? 5 : 0;
  viewerY += viewerHeight * UIMovementSpeedMultiple * tick;
  Camera.Y = viewerY;
  Camera.Z += viewerDepth * U.cos(Camera.XZ) * UIMovementSpeedMultiple * tick;
  Camera.X += viewerDepth * U.sin(Camera.XZ) * UIMovementSpeedMultiple * tick;
  U.rotate(Camera.camera, Camera.YZ, -Camera.XZ);
  Camera.rotateXY.setAngle(-Camera.XY);
  Camera.setAngleTable();
  if (!E.lights.getChildren().contains(E.sunlight)) {
   U.addLight(E.mapViewerLight);
   U.setTranslate(E.mapViewerLight, Camera.X, Camera.Y, Camera.Z);
  }
  E.run();
  for (TrackPart trackPart : TE.trackParts) {
   trackPart.runGraphics();
  }
  U.fillRGB(0, 0, 0, opacityUI.minimal);
  U.fillRectangle(.5, .9, 1, .2);
  U.font(.015);
  if (globalFlick) {
   U.strokeRGB(1, 1, 1);
   U.drawRectangle(.5, selected == 0 ? .85 : .875, width, selectionHeight);
  }
  U.fillRGB(1, 1, 1);
  U.text("RE-LOAD MAP FILE", .85 + textOffset);
  U.text("BACK TO MAIN MENU", .875 + textOffset);
  U.text("Move Camera with the T, G, U, and J Keys. Rotate with the Arrow Keys", .9 + textOffset);
  if (selectionTimer > selectionWait && (keyUp || keyDown)) {
   selected = selected < 1 ? 1 : 0;
   usingKeys = true;
   UI.play(0, 0);
  }
  if (keySpace || keyEnter) {
   status = selected == 0 ? Status.mapLoadPass0 : Status.mainMenu;
   UI.play(1, 0);
   keySpace = keyEnter = false;
   clearSounds();
  }
  if (keyEscape) {
   status = Status.mainMenu;
   UI.play(1, 0);
   keyEscape = false;
   clearSounds();
  }
  TE.runBonus();
  if (!usingKeys) {
   selected = Math.abs(.825 - mouseY) < clickRangeY ? 0 : Math.abs(.85 - mouseY) < clickRangeY ? 1 : selected;
  }
  gameFPS = Double.POSITIVE_INFINITY;
  E.renderType = E.RenderType.fullDistance;
 }

 private void runMapError() {
  scene.setCursor(Cursor.CROSSHAIR);
  U.fillRGB(0, 0, 0, opacityUI.maximal);
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
   status = Status.mapLoadPass0;
   keySpace = keyEnter = false;
   UI.play(1, 0);
  }
  if (keyRight) {
   map = ++map >= maps.size() ? 0 : map;
   status = Status.mapJump;
   keyRight = false;
   UI.play(0, 0);
  }
  if (keyLeft) {
   map = --map < 0 ? maps.size() - 1 : map;
   status = Status.mapJump;
   keyLeft = false;
   UI.play(0, 0);
  }
  gameFPS = U.refreshRate * .25;
  if (keyEscape) {
   escapeToLast(true);
  }
 }

 private void runMapView(boolean gamePlay) {
  Camera.runAroundTrack();
  U.rotate(Camera.camera, Camera.YZ, -Camera.XZ);
  E.run();
  for (Vehicle vehicle : vehicles) {
   vehicle.runGraphics(gamePlay);
  }
  for (TrackPart trackPart : TE.trackParts) {
   trackPart.runGraphics();
  }
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
  Network.preMatchCommunication();
  if (keySpace || keyEnter || tournament > 0) {
   status = Status.play;
   if (tournament < 1) {
    UI.play(1, 0);
   }
   Camera.view = Camera.View.flow;
   keySpace = keyEnter = false;
  } else if (keyRight || keyLeft) {
   if (keyLeft) {
    map = --map < 0 ? maps.size() - 1 : map;
   }
   if (keyRight) {
    map = ++map >= maps.size() ? 0 : map;
   }
   status = Status.mapJump;
   UI.play(0, 0);
   for (Vehicle vehicle : vehicles) {
    vehicle.closeSounds();
   }
  }
  if (keyEscape) {
   escapeToLast(true);
  }
  TE.runBonus();
  gameFPS = Double.POSITIVE_INFINITY;
  E.renderType = E.RenderType.standard;
 }

 static void escapeToLast(boolean wasUser) {
  if (Network.mode == Network.Mode.OFF) {
   status = Status.mainMenu;
  } else {
   for (PrintWriter PW : Network.out) {
    PW.println("CANCEL");
    PW.println("CANCEL");
   }
   status = Status.loadLAN;
  }
  Network.mode = Network.Mode.OFF;
  section = tournament = 0;
  if (wasUser) {
   UI.play(1, 0);
  }
  keyEscape = Network.runLoadThread = false;
  for (Vehicle vehicle : vehicles) {
   vehicle.closeSounds();
  }
 }

 private static void runMapJump() {
  if (Network.mode == Network.Mode.JOIN) {
   U.font(.03);
   U.text("..Please Wait for " + playerNames[0] + " to Select Map..", .5, .5);
  } else {
   String mapMaker;
   mapName = mapMaker = "";
   map = tournament > 0 ? U.random(maps.size()) : map;
   String s;
   try (BufferedReader BR = new BufferedReader(new InputStreamReader(Objects.requireNonNull(U.getMapFile(map)), U.standardChars))) {
    for (String s1; (s1 = BR.readLine()) != null; ) {
     s = s1.trim();
     mapName = s.startsWith("name") ? U.getString(s, 0) : mapName;
     mapMaker = s.startsWith("maker") ? U.getString(s, 0) : mapMaker;
    }
   } catch (IOException e) {
    status = Status.mapError;
    e.printStackTrace();
   }
   if (tournament > 0) {
    status = Status.mapLoadPass0;
   } else {
    U.fillRGB(0, 0, 0, opacityUI.maximal);
    U.fillRectangle(.5, .5, 1, 1);
    U.fillRGB(1, 1, 1);
    U.font(.05);
    U.text(mapName, .5);
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
     if (keyRight) {
      map = ++map >= maps.size() ? 0 : map;
      UI.play(0, 0);
     }
     if (keyLeft) {
      map = --map < 0 ? maps.size() - 1 : map;
      UI.play(0, 0);
     }
     if (keyEnter || keySpace) {
      status = Status.mapLoadPass0;
      UI.play(1, 0);
     }
    }
    gameFPS = U.refreshRate * .5;
   }
  }
  Network.preMatchCommunication();
  if (keyEscape) {
   escapeToLast(true);
  }
 }

 private static void runHowToPlay() {
  section = Math.max(section, 1);
  U.fillRGB(0, 0, 0, opacityUI.maximal);
  U.fillRectangle(.5, .5, 1, 1);
  U.fillRGB(1, 1, 1);
  U.font(.03);
  U.text("<-LAST", .1, .75);
  U.text("NEXT->", .9, .75);
  U.text("RETURN", .5, .95);
  if (section == 1) {
   U.font(.03);
   U.text("All Vehicle Controls", .15);
   U.font(.02);
   U.text("Welcome to the Vehicular Epic--the EPIC vehicle emulator!", .075);
   double keySizeX = .04, keySizeY = .06;
   U.fillRectangle(.85, .375, keySizeX, keySizeY);//UP
   U.fillRectangle(.85, .45, keySizeX, keySizeY);//DOWN
   U.fillRectangle(.8, .45, keySizeX, keySizeY);//LEFT
   U.fillRectangle(.9, .45, keySizeX, keySizeY);//RIGHT
   U.fillRectangle(.35, .45, keySizeX * 8, keySizeY);//SPACE
   U.fillRectangle(.175, .225, keySizeX, keySizeY);//W
   U.fillRectangle(.125, .3, keySizeX, keySizeY);//A
   U.fillRectangle(.175, .3, keySizeX, keySizeY);//S
   U.fillRectangle(.225, .3, keySizeX, keySizeY);//D
   U.fillRectangle(.275, .3, keySizeX, keySizeY);//F
   U.fillRectangle(.3, .375, keySizeX, keySizeY);//V
   U.fillRectangle(.35, .375, keySizeX, keySizeY);//B
   U.fillRectangle(.575, .3, keySizeX, keySizeY);//P
   U.fillRectangle(.6, .225, keySizeX, keySizeY);//MINUS
   U.fillRectangle(.65, .225, keySizeX, keySizeY);//PLUS
   U.fillRGB(0, 0, 0);
   U.text("UP", .85, .375);
   U.font(.0125);
   U.text("DOWN", .85, .45);
   U.text("LEFT", .8, .45);
   U.text("RIGHT", .9, .45);
   U.text("SPACE", .35, .45);
   U.text("W", .175, .225);
   U.text("A", .125, .3);
   U.text("S", .175, .3);
   U.text("D", .225, .3);
   U.text("F", .275, .3);
   U.text("V", .3, .375);
   U.text("B", .35, .375);
   U.text("P", .575, .3);
   U.text("-", .6, .225);
   U.text("+", .65, .225);
   U.fillRGB(1, 1, 1);
   U.text("While Driving on the GROUND, Spacebar is the Handbrake", .5);
   U.text("For standard CARS and TRUCKS, press Spacebar to perform Stunts for points", .525);
   U.text("When on the ground and using a flying vehicle, press W + Down Arrow to Take-off at any time", .55);
   U.text("While FLYING, hold Spacebar to yaw-steer instead of steer by banking, and use W and S to control throttle", .575);
   U.text("For GROUNDED TURRETS, Spacebar enables finer Precision while Aiming", .6);
   U.text("B = Boost Speed/Change Aerial Velocity (if available)", .625);
   U.text("V and/or F = Use weapon(s)/specials if your vehicle has them", .65);
   U.text("For TANKS, control the turret with the W/A/S/D keys", .675);
   U.text("(You can also fire the tank cannon by pressing A and D simultaneously)", .7);
   U.text("+ and - = Adjust Vehicle Light Brightness", .725);
   U.text("P = Pass bonus to a teammate (if crossing paths)", .75);
   U.fillRGB(.5, 1, .5);
   U.text("----------Cursor Controls----------", .8);
   U.fillRGB(1, 1, 1);
   U.text("Raise the cursor to go forward, lower it to Reverse", .825);
   U.text("Move the Cursor Left and Right to Turn", .85);
   U.text("Click to engage Handbrake/perform Stunts", .875);
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
   U.text("Press 'C' to toggle the guidance arrow between pointing to the Vehicles or Racetrack", .2);
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
   U.text("E or R = Change Player Perspective (See what the other players are doing)", .45);
   U.fillRGB(.5, 1, .5);
   U.text("(Press E and R simultaneously to view yourself again)", .475);
   U.fillRGB(1, 1, 1);
   U.text("H = Heads-up Display ON/OFF", .5);
   U.text("L = Destruction Log ON/OFF", .525);
   U.text("I = Show/Hide Application Info", .55);
   U.text("There are many other aspects not covered here in these instructions,", .7);
   U.text("but you will learn with experience.", .725);
   U.text("GOOD LUCK", .75);
  }
  if (selectionTimer > selectionWait) {
   if (keyRight) {
    if (++section > 4) {
     section = 0;
     status = lastStatus;
    }
    UI.play(0, 0);
   }
   if (keyLeft) {
    if (--section < 1) {
     section = 0;
     status = lastStatus;
    }
    UI.play(0, 0);
   }
   if (keyEnter) {
    section = 0;
    status = lastStatus;
    UI.play(1, 0);
    keyEnter = false;
   }
  }
  if (keyEscape) {
   section = 0;
   status = lastStatus;
   UI.play(1, 0);
   keyEscape = false;
  }
  gameFPS = U.refreshRate * .25;
 }

 private static void runCredits() {
  if (section < 1) {
   finish.play(U.random() < .5 ? 0 : 1, 0);
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
   creditsDirection = !(creditsQuantity < 2) && (creditsQuantity > 13 || creditsDirection);
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
   U.text("Rory McHenry--for teaching IDE/Java basics", .5);
   U.fillRGB(.5, .5, .5);
   if (creditsQuantity == 8) {
    U.fillRGB(1, 1, 1);
   }
   U.text("Omar Waly--his Java work (Need for Madness and Radical Aces) have served as a design 'template' for V.E.", .55);
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
   U.text("JavaZoom--for JLayer (a Java .mp3 player)", .7);
   U.fillRGB(.5, .5, .5);
   if (creditsQuantity == 12) {
    U.fillRGB(1, 1, 1);
   }
   U.text("Everyone who suggested or submitted content!", .75);
   U.fillRGB(.5, .5, .5);
   if (creditsQuantity == 13) {
    U.fillRGB(1, 1, 1);
   }
   U.font(.03);
   U.text("And thank YOU for playing", .85);
   U.fillRGB(.5, .5, .5);
   if (creditsQuantity == 14) {
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
    clusterX[n] = (width * .5) + StrictMath.pow(U.random(90000000000.), .25) - StrictMath.pow(U.random(90000000000.), .25);
    clusterY[n] = (height * .5) + StrictMath.pow(U.random(60000000000.), .25) - StrictMath.pow(U.random(60000000000.), .25);
   }
   U.fillRGB(1, 1, 1);
   graphicsContext.fillPolygon(clusterX, clusterY, (int) creditsQuantity);
   U.font(.05);
   U.fillRGB(0, 0, 0);
   U.text("VEHICULAR", .45);
   U.text("EPIC", .55);
  }
  if (selectionTimer > selectionWait) {
   if (keyLeft) {
    if (--section < 1) {
     section = 0;
     status = Status.mainMenu;
    }
    UI.play(0, 0);
   }
   if (keyRight) {
    if (++section > 2) {
     section = 0;
     status = Status.mainMenu;
    }
    UI.play(0, 0);
   }
   if (keyEnter || keySpace) {
    section = 0;
    status = Status.mainMenu;
    keyEnter = keySpace = false;
    UI.play(1, 0);
   }
  }
  if (keyEscape) {
   section = 0;
   status = Status.mainMenu;
   keyEscape = false;
   UI.play(1, 0);
  }
  U.fillRGB(1, 1, 1);
  U.font(.03);
  U.text("<-LAST", .1, .75);
  U.text("NEXT->", .9, .75);
  gameFPS = Double.POSITIVE_INFINITY;
 }

 private void runPaused() {
  boolean ending = false;
  if (selectionTimer > selectionWait) {
   if (keyUp) {
    selected = --selected < 0 ? 4 : selected;
    usingKeys = true;
    UI.play(0, 0);
   }
   if (keyDown) {
    selected = ++selected > 4 ? 0 : selected;
    usingKeys = true;
    UI.play(0, 0);
   }
  }
  if (keyEnter || keySpace) {
   if (selected == 0) {
    status = Status.play;
   } else if (selected == 1) {
    status = Status.replay;
    Recorder.recordFrame = Recorder.gameFrame - (int) Recorder.recorded;
    while (Recorder.recordFrame < 0) Recorder.recordFrame += Recorder.totalFrames;
    Recorder.recordingsCount = 0;
   } else if (selected == 2) {
    status = Status.optionsMatch;
   } else if (selected == 3) {
    lastStatus = Status.paused;
    status = Status.howToPlay;
   } else if (selected == 4) {
    ending = true;
   }
   UI.play(1, 0);
   keyEnter = keySpace = false;
  }
  if (keyEscape) {
   ending = true;
   UI.play(1, 0);
   keyEscape = false;
  }
  if (ending) {
   if (Network.mode == Network.Mode.OFF) {
    scene.setCursor(Cursor.WAIT);
    if (tournament > 0 && matchTime <= 0 && !tournamentOver) {
     status = Status.mapJump;
     tournament++;
    } else {
     status = Status.mainMenu;
     tournament = 0;
    }
    Camera.lastView = Camera.view;
    selected = 0;
    clearSounds();
   } else {
    int n;
    if (Network.mode == Network.Mode.HOST) {
     for (PrintWriter PW : Network.out) {
      PW.println("END");
      PW.println("END");
     }
    }
    if (Network.hostLeftMatch || Network.mode == Network.Mode.HOST) {
     scene.setCursor(Cursor.WAIT);
     for (n = Network.maxPlayers; --n >= 0; ) {
      Network.runGameThread[n] = false;
     }
     status = Status.mainMenu;
     Camera.lastView = Camera.view;
     selected = 0;
     clearSounds();
     try {
      if (Network.server != null) {
       Network.server.close();
      }
      if (Network.client != null) {
       Network.client.close();
      }
      for (BufferedReader in : Network.in) {
       in.close();
      }
      for (PrintWriter PW : Network.out) {
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
  U.text(tournament > 0 ? (matchTime > 0 ? "CANCEL TOURNAMENT" : tournamentOver ? "BACK TO MAIN MENU" : "NEXT ROUND") : Network.mode == Network.Mode.JOIN && !Network.hostLeftMatch ? "Please Wait for Host to exit Match first" : "END MATCH", .55 + extraY);
  if (!usingKeys) {
   selected =
   Math.abs(.45 + baseClickOffset - mouseY) < clickRangeY ? 0 :
   Math.abs(.475 + baseClickOffset - mouseY) < clickRangeY ? 1 :
   Math.abs(.5 + baseClickOffset - mouseY) < clickRangeY ? 2 :
   Math.abs(.525 + baseClickOffset - mouseY) < clickRangeY ? 3 :
   Math.abs(.55 + baseClickOffset - mouseY) < clickRangeY ? 4 : selected;
  }
 }

 private void runMainMenu() {
  boolean loaded = initialization.isEmpty();
  scene.setCursor(loaded ? Cursor.CROSSHAIR : Cursor.WAIT);
  if (loaded) {
   tournamentWins[0] = tournamentWins[1] = userPlayer = 0;
   Network.mode = Network.Mode.OFF;
   tournament = tournamentOver ? 0 : tournament;
   tournamentOver = Network.waiting = inViewer = false;
   TE.arrow.setVisible(false);
   if (selectionTimer > selectionWait) {
    if (keyUp) {
     selected = --selected < 0 ? 6 : selected;
     usingKeys = true;
     UI.play(0, 0);
    }
    if (keyDown) {
     selected = ++selected > 6 ? 0 : selected;
     usingKeys = true;
     UI.play(0, 0);
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
     status = Status.vehicleSelect;
     selected = vehiclePick = 0;
    } else if (selected == 1) {
     status = Status.loadLAN;
     resetGraphics();
     selected = 0;
    } else if (selected == 2) {
     lastStatus = Status.mainMenu;
     status = Status.howToPlay;
    } else if (selected == 3) {
     status = Status.credits;
    } else if (selected == 4) {
     status = Status.optionsMenu;
     selected = 0;
    } else if (selected == 5) {
     status = Status.vehicleSelect;
     vehiclesInMatch = 1;
     vehiclePick = 0;
     inViewer = true;
    } else if (selected == 6) {
     status = Status.mapJump;
     inViewer = true;
    }
    UI.play(1, 0);
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
   Math.abs(.6 + baseClickOffset - mouseY) < clickRangeY ? 0 :
   Math.abs(.65 + baseClickOffset - mouseY) < clickRangeY ? 1 :
   Math.abs(.7 + baseClickOffset - mouseY) < clickRangeY ? 2 :
   Math.abs(.75 + baseClickOffset - mouseY) < clickRangeY ? 3 :
   Math.abs(.8 + baseClickOffset - mouseY) < clickRangeY ? 4 :
   Math.abs(.85 + baseClickOffset - mouseY) < clickRangeY ? 5 :
   Math.abs(.9 + baseClickOffset - mouseY) < clickRangeY ? 6 : selected;
  }
  gameFPS = U.refreshRate * .5;
 }

 private void runLANMenu() {
  U.fillRGB(0, 0, 0, opacityUI.maximal);
  U.fillRectangle(.5, .5, 1, 1);
  int n;
  if (section < 1) {
   Network.mode = Network.Mode.OFF;
   vehiclesInMatch = (int) U.clamp(2, vehiclesInMatch, Network.maxPlayers);
   try {
    for (BufferedReader in : Network.in) {
     in.close();
    }
    Network.in.clear();
    for (PrintWriter PW : Network.out) {
     PW.close();
    }
    Network.out.clear();
    if (Network.server != null) {
     Network.server.close();
    }
    if (Network.client != null) {
     Network.client.close();
    }
   } catch (IOException e) {
    e.printStackTrace();
   }
   for (n = Network.maxPlayers; --n >= 0; ) {
    playerNames[n] = Network.vehicleData[n] = Network.lastVehicleData[n] = "";
   }
   Network.hostLeftMatch = Network.waiting = Network.runLoadThread = false;
   Network.ready = new boolean[Network.maxPlayers];
   section = 1;
  } else {
   scene.setCursor(Cursor.CROSSHAIR);
   if (selectionTimer > selectionWait) {
    if (keyUp) {
     selected = --selected < 0 ? 1 : selected;
     usingKeys = true;
     UI.play(0, 0);
    }
    if (keyDown) {
     selected = ++selected > 1 ? 0 : selected;
     usingKeys = true;
     UI.play(0, 0);
    }
   }
   if (globalFlick && Network.mode == Network.Mode.OFF) {
    if (selected == 0) {
     U.fillRGB(0, 0, 1);
    } else {
     U.fillRGB(0, 1, 0);
    }
    U.fillRectangle(.5, selected == 1 ? .5 : .45, .25, selectionHeight);
    String s;
    try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream("GameSettings"), U.standardChars))) {
     for (String s1; (s1 = BR.readLine()) != null; ) {
      s = s1.trim();
      Network.userName = s.startsWith("UserName(") ? U.getString(s, 0) : Network.userName;
      Network.targetHost = s.startsWith("TargetHost(") ? U.getString(s, 0) : Network.targetHost;
      Network.port = s.startsWith("Port(") ? (int) Math.round(U.getValue(s, 0)) : Network.port;
     }
    } catch (IOException e) {
     System.out.println("Problem updating Online settings: " + e);
    }
   }
   if (Network.mode == Network.Mode.HOST && !Network.out.isEmpty()) {
    for (n = 0; n < Network.out.size(); n++) {
     String s = Network.readIn(n);
     if (s.startsWith("CANCEL")) {
      escapeToLast(false);
     }
    }
   }
   if ((keyEnter || keySpace) && !Network.waiting) {
    if (selected == 0) {
     Network.mode = Network.Mode.HOST;
     Network.loadGameThread();
     Network.waiting = true;
    } else if (selected == 1) {
     Network.mode = Network.Mode.JOIN;
     Network.loadGameThread();
     Network.waiting = true;
    }
    UI.play(1, 0);
    keyEnter = keySpace = false;
   }
   U.font(.075);
   U.fillRGB(1, 1, 1);
   U.text(vehiclesInMatch + "-PLAYER GAME", .175);
   U.font(.01);
   U.fillRGB(1, 1, 1);
   if (Network.mode == Network.Mode.OFF) {
    U.text("HOST GAME", .45);
    U.text("JOIN GAME", .5);
   } else {
    if (globalFlick) {
     StringBuilder s = new StringBuilder("Players in: " + Network.userName);
     for (n = 0; n < vehiclesInMatch; n++) {
      s.append(n == userPlayer ? "" : ", " + playerNames[n]);
     }
     U.text(s.toString(), .45);
    }
    U.text("(Hit ESCAPE to Cancel)", .5);
   }
   if (errorTimer <= 0) {
    Network.joinError = "";
   } else {
    errorTimer -= tick;
   }
   U.text("Your UserName: " + Network.userName, .7);
   U.text("Your Target Host is: " + Network.targetHost, .725);
   U.text("Your Port #: " + Network.port, .75);
   U.text("For more information about this game mode, please read the Game Documentation", .85);
   if (globalFlick) {
    U.font(.02);
    U.text(Network.joinError, .625);
   }
   if (keyEscape) {
    escapeToLast(true);
   }
   gameFPS = U.refreshRate;
  }
  if (!usingKeys) {
   selected = Math.abs(.45 + baseClickOffset - mouseY) < clickRangeY ? 0 : Math.abs(.5 + baseClickOffset - mouseY) < clickRangeY ? 1 : selected;
  }
 }

 private static void runOptions() {
  boolean fromMenu = status == Status.optionsMenu;
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
  U.text("Limit FPS to [" + (userFPS > U.refreshRate ? "JavaFX Default" : Long.valueOf(userFPS)) + "]", .4 + textOffset);
  U.text("Camera-Shake Effects [" + (cameraShake ? "ON" : "OFF") + "]", .45 + textOffset);
  if (fromMenu) {
   U.text("Normal-Mapping [" + (normalMapping ? "ON" : "OFF") + "]", .5 + textOffset);
   U.text("Match Length [" + matchLength + "]", .55 + textOffset);
   U.text("Game Mode [" + (tournament > 0 ? "TOURNAMENT" : "NORMAL") + "]", .6 + textOffset);
   U.text("# of Players [" + vehiclesInMatch + "]", .65 + textOffset);
  }
  if (selectionTimer > selectionWait) {
   if (keyUp) {
    if (--selected < 0) {
     selected = fromMenu ? 8 : 4;
    }
    usingKeys = true;
    UI.play(0, 0);
   }
   if (keyDown) {
    selected = ++selected > 8 || (!fromMenu && selected > 4) ? 0 : selected;
    usingKeys = true;
    UI.play(0, 0);
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
    if (keyLeft && driverSeat > -1) {
     driverSeat--;
     UI.play(0, 0);
    }
    if (keyRight && driverSeat < 1) {
     driverSeat++;
     UI.play(0, 0);
    }
   }
  } else if (selected == 2) {
   U.text("Switch between Metric, U.S., or the game's raw units (VEs)", .75);
   if ((keyEnter || keySpace) && selectionTimer > selectionWait) {
    if (units == 1) {
     units = .5364466667;
     unitName[0] = "Kph";
     unitName[1] = "Meters";
    } else if (units == .5364466667) {
     units = 1 / 3.;
     unitName[0] = "Mph";
     unitName[1] = "Feet";
    } else if (units == 1 / 3.) {
     units = 1;
     unitName[0] = unitName[1] = "VEs";
    }
   }
  } else if (selected == 3) {
   isAdjustFunction = true;
   if (selectionTimer > selectionWait) {
    if (keyLeft && userFPS > 1) {
     userFPS = userFPS > U.refreshRate ? U.refreshRate - 1 : --userFPS;
     UI.play(0, 0);
    }
    if (keyRight && userFPS < Long.MAX_VALUE) {
     userFPS = ++userFPS >= U.refreshRate ? Long.MAX_VALUE : userFPS;
     UI.play(0, 0);
    }
   }
   U.text("Lower the FPS ceiling if your PC can't process V.E. well (i.e. overheating). Leave maxed otherwise.", .75);
  } else if (selected == 4) {
   if ((keyEnter || keySpace) && selectionTimer > selectionWait) {
    cameraShake = !cameraShake;
   }
   U.text("Shakes camera when vehicles explode (experimental feature)", .75);
  } else if (selected == 5) {
   if (fromMenu) {
    U.text("Use normal-mapping on textured surfaces", .75);
    if ((keyEnter || keySpace) && selectionTimer > selectionWait) {
     normalMapping = !normalMapping;
    }
   } else {
    selected++;
   }
  } else if (selected == 6) {
   if (fromMenu) {
    isAdjustFunction = true;
    if (selectionTimer > selectionWait) {
     if (keyLeft && matchLength > 0) {
      matchLength = Math.max(0, matchLength - 10);
      UI.play(0, 0);
     }
     if (keyRight) {
      matchLength += 10;
      UI.play(0, 0);
     }
    }
    U.text("Set how long the match lasts", .75);
   } else {
    selected++;
   }
  } else if (selected == 7) {
   if (fromMenu) {
    U.text("See the Documentation for more info on Game Modes", .75);
    if ((keyEnter || keySpace) && selectionTimer > selectionWait) {
     tournament = tournament > 0 ? 0 : 1;
    }
   } else {
    selected++;
   }
  } else if (selected == 8) {
   if (fromMenu) {
    isAdjustFunction = true;
    if (selectionTimer > selectionWait) {
     int playerFloor = tournament > 0 ? 2 : 1;
     if (keyLeft) {
      vehiclesInMatch = --vehiclesInMatch < playerFloor ? maxPlayers : vehiclesInMatch;
      UI.play(0, 0);
     }
     if (keyRight) {
      vehiclesInMatch = ++vehiclesInMatch > maxPlayers ? playerFloor : vehiclesInMatch;
      UI.play(0, 0);
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
   status = selected == 0 ? fromMenu ? Status.mainMenu : Status.paused : status;
   UI.play(1, 0);
   keyEnter = keySpace = false;
  }
  if (keyEscape) {
   status = fromMenu ? Status.mainMenu : Status.paused;
   UI.play(1, 0);
   keyEscape = false;
  }
  vehiclesInMatch = tournament > 0 ? Math.max(2, vehiclesInMatch) : vehiclesInMatch;
  if (!usingKeys) {
   double clickOffset = .025;
   selected = Math.abs(.825 + clickOffset - mouseY) < clickRangeY ? 0 : Math.abs(.25 + clickOffset - mouseY) < clickRangeY ? 1 : Math.abs(.3 + clickOffset - mouseY) < clickRangeY ? 2 : Math.abs(.35 + clickOffset - mouseY) < clickRangeY ? 3 : Math.abs(.4 + clickOffset - mouseY) < clickRangeY ? 4 : selected;
   if (status == Status.optionsMenu) {
    selected = Math.abs(.45 + clickOffset - mouseY) < clickRangeY ? 5 : Math.abs(.5 + clickOffset - mouseY) < clickRangeY ? 6 : Math.abs(.55 + clickOffset - mouseY) < clickRangeY ? 7 : Math.abs(.6 + clickOffset - mouseY) < clickRangeY ? 8 : selected;
   }
  }
  gameFPS = Double.POSITIVE_INFINITY;
 }

 public static void updateDestructionNames() {
  for (int n = 1; n < 5; n++) {
   destructionNames[n - 1][0] = destructionNames[n][0];
   destructionNames[n - 1][1] = destructionNames[n][1];
   destructionNameColors[n - 1][0] = destructionNameColors[n][0];
   destructionNameColors[n - 1][1] = destructionNameColors[n][1];
  }
 }

 private static void runMatchUI(boolean gamePlay) {
  matchTime -= matchTime > 0 && status == Status.play && matchStarted ? tick : 0;
  tournamentOver = tournament > 0 && ((tournament > 4 && Math.abs(tournamentWins[0] - tournamentWins[1]) > 0) || (tournament > 2 && Math.abs(tournamentWins[0] - tournamentWins[1]) > 1));
  if (matchStarted && (keyEnter || keyEscape) && gamePlay) {
   keyUp = keyDown = keyEnter = keyEscape = false;
   selected = 0;
   UI.play(1, 0);
   status = Status.paused;
  }
  TE.arrow.setVisible(headsUpDisplay);
  Vehicle V = vehicles.get(vehiclePerspective);
  if (headsUpDisplay) {
   if (matchTime <= 0) {
    double titleHeight = .12625;
    if (vehiclesInMatch > 1) {
     String[] formatFinal = {U.DF.format(finalScore[0]), U.DF.format(finalScore[1])};
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
     U.text(String.valueOf(tournament > 0 ? Long.valueOf(tournamentWins[0]) : formatFinal[0]), .3, .25);
     if (globalFlick) {
      U.fillRGB(1, 0, 0);
     }
     U.text(vehiclesInMatch > 2 ? "RED TEAM" : playerNames[1], .7, .225);
     U.text(String.valueOf(tournament > 0 ? Long.valueOf(tournamentWins[1]) : formatFinal[1]), .7, .25);
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
    if (V.explosionType != Vehicle.ExplosionType.maxnuclear) {
     U.font(.02);
     if (globalFlick) {
      U.fillRGB(1, 1, 1);
      U.text(".. REVIVING.. ", .275);
     } else {
      U.fillRGB(0, 0, 0);
      U.text(" ..REVIVING ..", .275);
     }
    }
   }
   U.font(.01);
   TE.runArrow();
   //U.textR(String.valueOf(V.drive), .9, .5);
   //U.textR(String.valueOf(V.drive2), .9, .525);
   //U.textR(String.valueOf(V.reverse), .9, .55);
   //U.textR(String.valueOf(V.reverse2), .9, .575);
   U.fillRGB(0, 0, 0, opacityUI.minimal);
   U.fillRectangle(.025, .8, .05, .425);
   U.fillRectangle(.975, .8, .05, .425);
   runDestructionLog();
   //LEFT HUD BLOCK
   U.font(.0125);
   if (V.vehicleType != Vehicle.Type.turret) {
    U.fillRGB(.75, .75, .75);
    U.fillRectangle(.025, .7, .01, Math.min(.2, .2 * (Math.abs(V.speed) / V.topSpeeds[1])));
    U.fillRGB(1, 1, 1);
    U.fillRectangle(.025, .6, .02, .001);
    U.fillRectangle(.025, .8, .02, .001);
    U.text(Math.abs(V.speed) >= 10000 ? U.DF.format(V.speed) : String.valueOf(Math.round(V.speed * units)), .025, .7);
    U.text(unitName[0], .025, .825);
   }
   U.fillRGB(1, 1, 1);
   U.font(.01);
   double converted = units == .5364466667 ? .0175 : units == 1 / 3. ? .0574147 : 1;
   U.font(.0075);
   U.text("(" + unitName[1] + ")", .025, .875);
   U.textL("X: " + U.DF.format(V.X * converted), .00625, .9);
   U.textL("Y: " + U.DF.format(V.Y * converted), .00625, .925);
   U.textL("Z: " + U.DF.format(V.Z * converted), .00625, .95);
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
    U.text("Vehicle #", .975, .865);
    if (vehiclesInMatch > 2) {
     if (vehiclePerspective < vehiclesInMatch >> 1) {
      U.fillRGB(0, 1, 0);
     } else {
      U.fillRGB(1, 0, 0);
     }
    }
    U.font(.01);
    U.text(vehiclePerspective + (vehiclePerspective == userPlayer ? " (You)" : ""), .975, .89);
   }
   U.fillRGB(1, 1, 1);
   U.font(.01);
   U.text("Time", .975, .925);
   U.text("Left:", .975, .94);
   U.font(.015);
   U.text(String.valueOf(Math.round(matchTime)), .975, .965);
   //
   if (Network.mode == Network.Mode.JOIN && Network.hostLeftMatch) {
    U.font(.02);
    double color = globalFlick ? 1 : .5;
    U.fillRGB(color, color, color);
    U.text("The Host has left match--hit Enter to start another match", .9);
   } else if (V.mode == Vehicle.Mode.fly && E.gravity != 0 && U.sin(V.YZ) > 0 && V.netSpeedY + V.stallSpeed > 0) {
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
   if (!destructionLog) {
    if (V.flipped && V.flipTimer > 0) {
     if (V.mode.name().startsWith(Vehicle.Mode.drive.name())) {
      double color = globalFlick ? 0 : 1;
      U.fillRGB(color, color, color);
      U.text("Bad Landing", .075);
     }
    } else if (stuntTimer > 0) {
     U.fillRGB(0, globalFlick ? 1 : 0, 0);
     U.text(stuntPrint, .075);
    }
   }
   stuntTimer -= gamePlay ? tick : 0;
  }
  long scoreStunt0 = 1 + Math.round(scoreStunt[0] * .0005),
  scoreStunt1 = 1 + Math.round(scoreStunt[1] * .0005);
  double scoreDamage0 = 1 + scoreDamage[0] * .000125,
  scoreDamage1 = 1 + scoreDamage[1] * .000125;
  double[] score = {
  scoreCheckpoint[0] * scoreLap[0] * scoreStunt0 * scoreDamage0 * scoreKill[0],
  scoreCheckpoint[1] * scoreLap[1] * scoreStunt1 * scoreDamage1 * scoreKill[1]};
  if (bonusHolder > -1) {
   score[bonusHolder < vehiclesInMatch >> 1 ? 0 : 1] *= 2;
  }
  if (headsUpDisplay) {
   U.font(.00875);
   U.fillRGB(0, 0, 0, opacityUI.minimal);
   U.fillRectangle(.9375, .26, .125, .2);
   if (vehiclesInMatch > 1) {
    U.fillRectangle(.0625, .26, .125, .2);
    //GREEN
    U.fillRGB(0, 1, 0);
    U.textL(vehiclesInMatch > 2 ? "GREEN TEAM" : playerNames[0], .0125, .175);
    if (!TE.checkpoints.isEmpty()) {
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
     U.textL(U.DF.format(scoreDamage0) + " :Damage Dealt", .0125, .275);
    }
    if (globalFlick || scoreKill[0] >= scoreKill[1]) {
     U.textL(scoreKill[0] + " :Kills", .0125, .3);
    }
    if (bonusHolder > -1 && bonusHolder < vehiclesInMatch >> 1) {
     U.textL("(Player " + bonusHolder + ") BONUS", .0125, .325);
    }
    U.textL(U.DF.format(score[0]) + " :Current Score", .0125, .35);
    //RED
    U.fillRGB(1, 0, 0);
    U.textR(vehiclesInMatch > 2 ? "RED TEAM" : playerNames[1], .9875, .175);
    if (!TE.checkpoints.isEmpty()) {
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
     U.textR("Damage Dealt: " + U.DF.format(scoreDamage1), .9875, .275);
    }
    if (globalFlick || scoreKill[1] >= scoreKill[0]) {
     U.textR("Kills: " + scoreKill[1], .9875, .3);
    }
   } else {
    U.fillRGB(1, 1, 1);
    U.textR("YOU", .9875, .175);
    if (!TE.checkpoints.isEmpty()) {
     U.textR("Checkpoints: " + scoreCheckpoint[1], .9875, .2);
     U.textR("Laps: " + scoreLap[1], .9875, .225);
    }
    U.textR("Stunts: " + scoreStunt1, .9875, .25);
    U.textR("Damage Dealt: " + scoreDamage1, .9875, .275);
    U.textR("Kills: " + scoreKill[1], .9875, .3);
   }
   if (bonusHolder >= vehiclesInMatch >> 1) {
    U.textR("BONUS (Player " + bonusHolder + ")", .9875, .325);
   }
   U.textR("Current Score: " + U.DF.format(score[1]), .9875, .35);
  }
  if (matchTime < 0) {
   finalScore[0] = score[0];
   finalScore[1] = score[1];
   String[] formatFinal = {U.DF.format(finalScore[0]), U.DF.format(finalScore[1])};
   boolean matchTie = formatFinal[0].equals(formatFinal[1]);
   if (vehiclesInMatch > 1 && headsUpDisplay) {
    if (matchTie) {
     finish.play(0, 0);
     finish.play(1, 0);
    } else {
     finish.play((score[0] > score[1] && vehiclePerspective < vehiclesInMatch >> 1) || (score[1] > score[0] && vehiclePerspective >= vehiclesInMatch >> 1) ? 0 : 1, 0);
    }
   }
   if (!matchTie) {
    if (score[0] > score[1]) {
     tournamentWins[0]++;
    } else if (score[1] > score[0]) {
     tournamentWins[1]++;
    }
   }
   matchTime = 0;
  }
 }

 public static void processStuntForUI(Vehicle V) {
  if (V.index == vehiclePerspective && V.stuntTimer > V.stuntLandWaitTime && V.stuntReward > 0) {
   String stuntSpins = "", stuntRolls = "", stuntFlips = "";
   long computeStuntYZ = 0, computeStuntXY = 0, computeStuntXZ = 0;
   while (computeStuntYZ < Math.abs(V.stuntYZ) - 45) computeStuntYZ += 360;
   stuntFlips = computeStuntYZ > 0 ? (V.flipCheck[0] && V.flipCheck[1] ? "BiDirectional " : "") + computeStuntYZ + "-Flip" :
   V.flipCheck[0] || V.flipCheck[1] ? "Half-Flip" : stuntFlips;
   while (computeStuntXY < Math.abs(V.stuntXY) - 45) computeStuntXY += 360;
   stuntRolls = computeStuntXY > 0 ? (V.rollCheck[0] && V.rollCheck[1] ? "BiDirectional " : "") + computeStuntXY + "-Roll" :
   V.rollCheck[0] || V.rollCheck[1] ? (stuntFlips.isEmpty() ? "Half-Roll" : "Flipside") : stuntRolls;
   while (computeStuntXZ < Math.abs(V.stuntXZ) - 45) computeStuntXZ += 180;
   stuntSpins = computeStuntXZ > 0 ? (V.spinCheck[0] && V.spinCheck[1] ? "BiDirectional " : "") + computeStuntXZ + "-Spin" :
   V.spinCheck[0] || V.spinCheck[1] ? "Half-Spin" : stuntSpins;
   stuntTimer = (stuntFlips.isEmpty() ? 0 : 25) + (stuntRolls.isEmpty() ? 0 : 25) + (stuntSpins.isEmpty() ? 0 : 25) + (V.offTheEdge ? 25 : 0);
   if (status == Status.play || status == Status.replay) {
    if (headsUpDisplay && !destructionLog) {
     stunt.play(0);
    }
    String by1 = !stuntFlips.isEmpty() && !stuntRolls.isEmpty() ? " by " : "",
    by2 = !stuntSpins.isEmpty() && (!stuntFlips.isEmpty() || !stuntRolls.isEmpty()) ? " by " : "";
    stuntPrint = "Landed " + (V.offTheEdge ? "an off-the-edge " : "a ") + stuntFlips + by1 + stuntRolls + by2 + stuntSpins + "!";
   }
   V.stuntReward = 0;
  }
 }

 private static void runDestructionLog() {
  if (destructionLog) {
   U.font(.00875);
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
 }

 private void reset() {
  matchStarted = Camera.flowFlip = false;
  vehiclePerspective = userPlayer;
  matchTime = matchLength;
  TE.arrowTarget = Math.min(vehiclesInMatch - 1, TE.arrowTarget);
  scoreCheckpoint[0] = scoreCheckpoint[1] = scoreLap[0] = scoreLap[1] = scoreKill[0] = scoreKill[1] = 1;
  scoreDamage[0] = scoreDamage[1] = Camera.aroundVehicleXZ = printTimer = Camera.lookAround = scoreStunt[0] = scoreStunt[1] = 0;
  TE.bonusBig.setVisible(true);
  for (TE.BonusBall bonusBall : TE.bonusBalls) {
   bonusBall.setVisible(false);
  }
  bonusHolder = Network.bonusHolder = -1;
  stuntTimer = trackTimer = Recorder.recorded = trackPoint = 0;
  int n;
  for (n = destructionNames.length; --n >= 0; ) {
   destructionNames[n][0] = "";
   destructionNames[n][1] = "";
   destructionNameColors[n][0] = new Color(0, 0, 0, 1);
   destructionNameColors[n][1] = new Color(0, 0, 0, 1);
  }
  destructionLog = vehiclesInMatch > 1;
  if (!inViewer && Network.mode == Network.Mode.OFF) {
   for (n = vehiclesInMatch; --n >= 0; ) {
    playerNames[n] = vehicles.get(n).vehicleName;
   }
  }
  Camera.mapSelectRandomRotationDirection = U.random() < .5 ? 1 : -1;
  E.renderLevel = Double.POSITIVE_INFINITY;//<-Render everything once first to prevent frame spikes at match start
  Network.ready = new boolean[Network.maxPlayers];
  scene.setCursor(Cursor.CROSSHAIR);
 }

 private static void falsify() {
  keyUp = keyDown = keyLeft = keyRight = keySpace =
  keyW = keyS = keyA = keyD =
  keyEnter = keySpecial[0] = keySpecial[1] = keyBoost = keyEscape = false;
 }

 private void loadKeysMouse() {
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
   KeyCode KC = keyEvent.getCode();
   if (KC == KeyCode.UP || KC == KeyCode.LEFT || KC == KeyCode.RIGHT || KC == KeyCode.DOWN || KC == KeyCode.SPACE) {
    if (cursorDriving) {
     keyUp = keyDown = keyLeft = keyRight = keySpace = cursorDriving = false;
    }
    keyUp = KC == KeyCode.UP || keyUp;
    keyDown = KC == KeyCode.DOWN || keyDown;
    keyLeft = KC == KeyCode.LEFT || keyLeft;
    keyRight = KC == KeyCode.RIGHT || keyRight;
    keySpace = KC == KeyCode.SPACE || keySpace;
   } else if (KC == KeyCode.ENTER) {
    keyEnter = true;
   } else if (KC == KeyCode.ESCAPE) {
    keyEscape = true;
   } else if (KC == KeyCode.Z) {
    Camera.lookAround = 1;
    Camera.lookForward[1] = true;
    Camera.view = Camera.view == Camera.View.flow && !matchStarted ? Camera.lastViewWithLookAround : Camera.view;
   } else if (KC == KeyCode.X) {
    Camera.lookAround = -1;
    Camera.lookForward[0] = true;
    Camera.view = Camera.view == Camera.View.flow && !matchStarted ? Camera.lastViewWithLookAround : Camera.view;
   } else if (KC == KeyCode.DIGIT1) {
    Camera.view = Camera.View.docked;
    Camera.lastView = Camera.view;
    Camera.lastViewNear = false;
    Camera.lastViewWithLookAround = Camera.view;
   } else if (KC == KeyCode.DIGIT2) {
    Camera.view = Camera.View.near;
    Camera.lastView = Camera.view;
    Camera.lastViewNear = true;
    Camera.lastViewWithLookAround = Camera.view;
   } else if (KC == KeyCode.DIGIT3) {
    Camera.view = Camera.View.driver;
    Camera.lastView = Camera.view;
   } else if (KC == KeyCode.DIGIT4) {
    Camera.view = Camera.View.distant;
    Camera.lastView = Camera.view;
   } else if (KC == KeyCode.DIGIT5) {
    Camera.view = Camera.View.flow;
    Camera.lastView = Camera.view;
   } else if (KC == KeyCode.DIGIT6) {
    Camera.view = Camera.View.watchMove;
    Camera.lastView = Camera.view;
   } else if (KC == KeyCode.DIGIT7 || KC == KeyCode.DIGIT8 || KC == KeyCode.DIGIT9 || KC == KeyCode.DIGIT0) {
    Camera.view = Camera.View.watch;
    Camera.lastView = Camera.view;
   } else if (KC == KeyCode.V) {
    keySpecial[0] = true;
   } else if (KC == KeyCode.F) {
    keySpecial[1] = true;
   } else if (KC == KeyCode.B) {
    keyBoost = true;
   } else if (KC == KeyCode.W) {
    keyW = true;
   } else if (KC == KeyCode.S) {
    keyS = true;
   } else if (KC == KeyCode.A) {
    keyA = true;
    sameVehicles = !sameVehicles;
   } else if (KC == KeyCode.D) {
    keyD = true;
   } else if (KC == KeyCode.C) {
    TE.arrowStatus = TE.arrowStatus == TE.Arrow.racetrack ? TE.Arrow.vehicles : TE.arrowStatus == TE.Arrow.vehicles ? TE.Arrow.locked : TE.Arrow.racetrack;
   } else if (KC == KeyCode.E) {
    vehiclePerspective = --vehiclePerspective < 0 ? vehiclesInMatch - 1 : vehiclePerspective;
    Camera.toUserPerspective[1] = true;
   } else if (KC == KeyCode.R) {
    vehiclePerspective = ++vehiclePerspective >= vehiclesInMatch ? 0 : vehiclePerspective;
    Camera.toUserPerspective[0] = true;
   } else if (KC == KeyCode.H) {
    headsUpDisplay = !headsUpDisplay;
   } else if (KC == KeyCode.L) {
    destructionLog = !destructionLog;
   } else if (KC == KeyCode.P) {
    keyPassBonus = true;
   } else if (KC == KeyCode.SHIFT) {
    Camera.zoomChange = .98;
    Camera.restoreZoom[1] = true;
   } else if (KC == KeyCode.CONTROL) {
    Camera.zoomChange = 1.02;
    Camera.restoreZoom[0] = true;
   } else if (KC == KeyCode.M) {
    muteSound = !muteSound;
   } else if (KC == KeyCode.COMMA) {
    musicGain = Math.max(musicGain * 2 - 1, -100);
   } else if (KC == KeyCode.PERIOD) {
    musicGain = Math.min(musicGain * .5 + 1, 0);
   } else if (KC == KeyCode.T || KC == KeyCode.G || KC == KeyCode.U || KC == KeyCode.J) {
    viewerHeight = KC == KeyCode.J ? 10 : KC == KeyCode.U ? -10 : viewerHeight;
    viewerDepth = KC == KeyCode.T ? 10 : KC == KeyCode.G ? -10 : viewerDepth;
    UIMovementSpeedMultiple = Math.max(10, UIMovementSpeedMultiple * 1.05);
   } else if (KC == KeyCode.EQUALS) {
    vehicleLightBrightnessChange = .01;
   } else if (KC == KeyCode.MINUS) {
    vehicleLightBrightnessChange = -.01;
   } else if (KC == KeyCode.I) {
    showInfo = !showInfo;
   }
  });
  scene.setOnKeyReleased((KeyEvent keyEvent) -> {
   KeyCode KC = keyEvent.getCode();
   keyUp = KC != KeyCode.UP && keyUp;
   keyDown = KC != KeyCode.DOWN && keyDown;
   keyLeft = KC != KeyCode.LEFT && keyLeft;
   keyRight = KC != KeyCode.RIGHT && keyRight;
   keySpace = KC != KeyCode.SPACE && keySpace;
   keyEnter = KC != KeyCode.ENTER && keyEnter;
   keyEscape = KC != KeyCode.ESCAPE && keyEscape;
   keyW = KC != KeyCode.W && keyW;
   keyS = KC != KeyCode.S && keyS;
   keyA = KC != KeyCode.A && keyA;
   keyD = KC != KeyCode.D && keyD;
   if (KC == KeyCode.Z || KC == KeyCode.X) {
    Camera.lookAround = 0;
    Camera.lookForward[0] = Camera.lookForward[1] = false;
   } else if (KC == KeyCode.V) {
    keySpecial[0] = false;
   } else if (KC == KeyCode.F) {
    keySpecial[1] = false;
   } else if (KC == KeyCode.B) {
    keyBoost = false;
   } else if (KC == KeyCode.P) {
    keyPassBonus = false;
   } else if (KC == KeyCode.E || KC == KeyCode.R) {
    Camera.toUserPerspective[0] = Camera.toUserPerspective[1] = false;
   } else if (KC == KeyCode.SHIFT || KC == KeyCode.CONTROL) {
    Camera.zoomChange = 1;
    Camera.restoreZoom[0] = Camera.restoreZoom[1] = false;
   } else if (KC == KeyCode.T || KC == KeyCode.G || KC == KeyCode.U || KC == KeyCode.J) {
    if (KC == KeyCode.U || KC == KeyCode.J) {
     viewerHeight = 0;
    } else {
     viewerDepth = 0;
    }
    UIMovementSpeedMultiple = 0;
   } else if (KC == KeyCode.EQUALS || KC == KeyCode.MINUS) {
    vehicleLightBrightnessChange = 0;
   }
  });
 }

 private static void resetGraphics() {
  boolean addSunlightBack = E.lights.getChildren().contains(E.sunlight),//<-Check LIGHT group, not main group!
  addSunBack = group.getChildren().contains(E.sun),
  addGroundBack = group.getChildren().contains(E.ground);
  group.getChildren().clear();
  E.lights.getChildren().clear();
  U.add(E.ambientLight, addSunBack ? E.sun : null, addGroundBack ? E.ground : null);
  U.addLight(addSunlightBack ? E.sunlight : null);
  U.add(E.lights);
  TE.arrowGroup.getChildren().clear();
 }

 private static void clearSounds() {//Not all sounds close
  for (Vehicle vehicle : vehicles) {
   vehicle.closeSounds();
  }
  E.rain.stop();
  E.tornado.stop();
  E.tsunami.stop();
  for (Fire fire : E.fires) {
   fire.closeSound();
  }
  for (Boulder boulder : E.boulders) {
   boulder.sound.close();
  }
  for (Meteor meteor : E.meteors) {
   meteor.sound.close();
  }
  if (E.thunder != null) E.thunder.close();
  if (E.windstorm != null) E.windstorm.close();
 }
}
