package ve.ui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ve.effects.Explosion;
import ve.environment.*;
import ve.trackElements.Bonus;
import ve.trackElements.TE;
import ve.trackElements.trackParts.TrackPart;
import ve.utilities.*;
import ve.utilities.Camera;
import ve.vehicles.*;
import ve.vehicles.specials.Port;
import ve.vehicles.specials.Shot;
import ve.vehicles.specials.Special;

import java.io.*;
import java.util.*;

import static ve.instances.I.vehicles;

public class UI/*UserInterface*/ extends Application {

 public static Scene scene;
 public static SubScene scene3D;
 public static final Group group = new Group();
 private static Canvas canvas;
 public static GraphicsContext graphicsContext;
 public static final int maxPlayers = (int) Math.round(Math.max(Network.maxPlayers, Runtime.getRuntime().maxMemory() * .00000001125));
 public static int vehiclePerspective;
 public static int userPlayerIndex;
 public static int vehiclesInMatch = 1;
 public static int bonusHolder = -1;
 public static int map;
 public static boolean yinYang;
 public static double tick;
 public static double timerBase20;
 public static double width, height;
 public static double errorTimer;
 public static Color userRandomRGB = U.getColor(U.random(), U.random(), U.random());
 public static double gameFPS = Double.POSITIVE_INFINITY;
 private static long lastTime;
 public static long userFPS = Long.MAX_VALUE;
 private static String initialization = "Loading V.E.";
 public static String vehicleMaker = "";
 private static String error = "";
 public static final String[] playerNames = new String[maxPlayers];
 public static List<String> vehicleModels;
 public static final List<String> maps = new ArrayList<>(Arrays.asList(SL.basic, "lapsGlory", SL.checkpoint, "gunpowder", "underOver", SL.antigravity, "versus1", "versus2", "versus3", "trackless", "desert", "3DRace", "trip", "raceNowhere", "moonlight", "bottleneck", "railing", "twisted", "deathPit", "falls", "pyramid", "combustion", "darkDivide", "arctic", "scenicRoute", "winterMode", "mountainHop", "damage", "crystalCavern", "southPole", "aerialControl", "matrix", "mist", "vansLand", "dustDevil", "forest", "columns", "zipCross", "highlands", "coldFury", SL.tornado, "volcanic", SL.tsunami, SL.boulder, "sands", SL.meteor, "speedway", "endurance", "tunnel", "circle", "circleXL", "circles", "everything", "linear", "maze", "xy", "stairwell", "immense", "showdown", "ocean", "lastStand", "parkingLot", "city", "machine", "military", "underwater", "hell", "moon", "mars", "sun", "space1", "space2", "space3", "summit", "portal", "blackHole", "doomsday", "+UserMap & TUTORIAL+"));
 public static Status status = UI.Status.mainMenu;
 private static Status lastStatus;

 public enum Status {
  play, replay, paused, optionsMatch, optionsMenu, mainMenu, credits,
  vehicleSelect, vehicleViewer,
  mapJump, mapLoadPass0, mapLoadPass1, mapLoadPass2, mapLoadPass3, mapLoadPass4, mapError, mapView, mapViewer,
  howToPlay, loadLAN
 }

 public enum Units {VEs, metric, US}

 public static long selected;
 public static double selectionWait;
 public static double selectionTimer;
 public static long page;
 public static final double selectionHeight = .03;
 public static final double clickRangeY = selectionHeight * .5;
 private static final double baseClickOffset = -.025;
 public static final double textOffset = .01;
 public static double movementSpeedMultiple = 1;
 public static final String Yes = "Yes";
 public static final String No = "No";
 public static final String ON = "ON";
 public static final String OFF = "OFF";
 public static final String HIDE = "HIDE";
 public static final String _LAST = "<-LAST";
 public static final String NEXT_ = "NEXT->";
 public static final String CONTINUE = "CONTINUE";
 public static final String RETURN = "RETURN";
 public static final String BACK_TO_MAIN_MENU = "BACK TO MAIN MENU";
 static final String HOW_TO_PLAY = "HOW TO PLAY";
 public static final String Made_by_ = "Made by ";
 public static final String GREEN_TEAM = "GREEN TEAM";
 public static final String RED_TEAM = "RED TEAM";
 public static final String/*..*/Please_Wait_For_ = "..Please Wait for ";
 public static final String notifyUserOfArrowKeyNavigation = "You can also use the Arrow Keys and Enter to navigate.";
 public static final String At_File_/*:*/ = "At File: ", At_Line_/*:*/ = "At Line: ";

 public static boolean selectionReady() {
  return selectionTimer > selectionWait;
 }

 public enum colorOpacity {
  ;
  public static final double minimal = .5;
  public static final double maximal = .75;
 }

 public static String getUnitDistanceName() {
  return Options.units == Units.VEs ? Units.VEs.name() : Options.units == Units.metric ? "Meters" : "Feet";
 }

 public static String getUnitSpeedName() {
  return Options.units == Units.VEs ? Units.VEs.name() : Options.units == Units.metric ? "Kph" : "Mph";
 }

 public static double getUnitSpeed(double in) {
  return in * (Options.units == Units.VEs ? 1 : Options.units == Units.metric ? .5364466667 : 1 / 3.);
 }

 public static double getUnitDistance(double in) {
  return in * (Options.units == Units.VEs ? 1 : Options.units == Units.metric ? .0175 : .0574147);
 }

 public static int getVehicleIndex(String s) {
  String s1, s3 = "";
  int n;
  for (n = 0; n < vehicleModels.size(); n++) {
   File F = new File(U.modelFolder + File.separator + SL.vehicles + File.separator + vehicleModels.get(n));
   if (!F.exists()) {
    F = new File(U.modelFolder + File.separator + SL.vehicles + File.separator + U.userSubmittedFolder + File.separator + vehicleModels.get(n));
   }
   if (!F.exists()) {
    F = new File(U.modelFolder + File.separator + SL.vehicles + File.separator + SL.basic);
   }
   try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream(F), U.standardChars))) {
    for (String s2; (s2 = BR.readLine()) != null; ) {
     s1 = s2.trim();
     if (s1.startsWith(SL.name)) {
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
  File F = new File(U.modelFolder + File.separator + vehicleModels.get(in));
  if (!F.exists()) {
   F = new File(U.modelFolder + File.separator + U.userSubmittedFolder + File.separator + vehicleModels.get(in));
  }
  if (!F.exists()) {
   F = new File(U.modelFolder + File.separator + SL.basic);
  }
  try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream(F), U.standardChars))) {
   for (String s2; (s2 = BR.readLine()) != null; ) {
    s = s2.trim();
    if (s.startsWith(SL.name)) {
     s3 = U.getString(s, 0);
     break;
    }
   }
  } catch (IOException e) {
   e.printStackTrace();
  }
  return s3;
 }

 public static void run(String[] s) {//<-Will NOT work if moved to Launcher!
  launch(s);
 }

 private static void boot(Stage stage) {
  Thread loadVE = new Thread(() -> {
   try {
    int n;
    scene3D.setFill(U.getColor(0));
    TE.Arrow.scene.setFill(Color.color(0, 0, 0, 0));
    initialization = "Loading Images";
    Images.RA = Images.load(SL.RA);
    Images.white = Images.load(SL.white);
    Images.fireLight = Images.load(SL.firelight, Double.POSITIVE_INFINITY);
    Images.blueJet = Images.load(SL.blueJet, Double.POSITIVE_INFINITY);
    Images.blink = Images.load(SL.blink, Double.POSITIVE_INFINITY);
    Images.amphibious = Images.load("amphibious");
    initialization = "Loading Textures";
    Images.water = Images.load(SL.water);
    Images.rock = Images.load(SL.rock);
    Images.metal = Images.load(SL.metal);
    Images.brightmetal = Images.load(SL.brightmetal);
    Images.grid = Images.load(SL.grid);
    Images.paved = Images.load(SL.paved);
    Images.wood = Images.load(SL.wood);
    Images.foliage = Images.load(SL.foliage);
    Images.cactus = Images.load(SL.cactus);
    Images.grass = Images.load(SL.grass);
    Images.sand = Images.load(SL.sand);
    Images.ground1 = Images.load(SL.ground + 1);
    Images.ground2 = Images.load(SL.ground + 2);
    initialization = "Loading Normal Maps";
    Images.rockN = Images.load("rockN");
    Images.metalN = Images.load("metalN");
    Images.brightmetalN = Images.load("brightmetalN");
    Images.pavedN = Images.load("pavedN");
    Images.woodN = Images.load("woodN");
    Images.foliageN = Images.load("foliageN");
    Images.cactusN = Images.load("cactusN");
    Images.grassN = Images.load("grassN");
    Images.sandN = Images.load("sandN");
    Images.ground1N = Images.load("ground1N");
    Images.ground2N = Images.load("ground2N");
    initialization = "Loading Settings";
    String s;
    try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream(SL.GameSettings), U.standardChars))) {
     for (String s1; (s1 = BR.readLine()) != null; ) {
      s = s1.trim();
      if (s.startsWith("Units(metric")) {
       Options.units = Units.metric;
      } else if (s.startsWith("Units(U.S.")) {
       Options.units = Units.US;
      }
      Options.normalMapping = s.startsWith("NormalMapping(yes") || Options.normalMapping;
      Camera.shake = s.startsWith("CameraShake(yes") || Camera.shake;
      try {
       userFPS = s.startsWith("fpsLimit(") ? Math.round(U.getValue(s, 0)) : userFPS;
      } catch (RuntimeException ignored) {
      }
      Options.degradedSoundEffects = s.startsWith("DegradedSoundEffects(yes") || Options.degradedSoundEffects;
      Options.matchLength = s.startsWith(SL.MatchLength + "(") ? Math.round(U.getValue(s, 0)) : Options.matchLength;
      Options.driverSeat = s.startsWith("DriverSeat(left") ? -1 : s.startsWith("DriverSeat(right") ? 1 : Options.driverSeat;
      vehiclesInMatch = s.startsWith("#ofPlayers(") ? Math.max(1, Math.min((int) Math.round(U.getValue(s, 0)), maxPlayers)) : vehiclesInMatch;
      Options.headsUpDisplay = s.startsWith("HUD(on") || Options.headsUpDisplay;
      Options.showInfo = s.startsWith("ShowInfo(yes") || Options.showInfo;
      VS.showModel = s.startsWith("ShowVehiclesInVehicleSelect(yes") || VS.showModel;
      Network.userName = s.startsWith(SL.UserName + "(") ? U.getString(s, 0) : Network.userName;
      Network.targetHost = s.startsWith(SL.TargetHost + "(") ? U.getString(s, 0) : Network.targetHost;
      Network.port = s.startsWith(SL.Port + "(") ? (int) Math.round(U.getValue(s, 0)) : Network.port;
      if (s.startsWith(SL.GameVehicles + "(")) {
       vehicleModels = new ArrayList<>(Arrays.asList(s.substring((SL.GameVehicles + "(").length(), s.length() - 1).split(",")));
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
    Sounds.checkpoint = new Sound(SL.checkpoint);
    Sounds.stunt = new Sound("stunt");
    Bonus.sound = new Sound("bonus");
    Rain.sound = new Sound(SL.rain);
    Tornado.sound = new Sound(SL.tornado);
    Tsunami.sound = new Sound(SL.tsunami);
    Volcano.sound = new Sound("volcano");
    Sounds.UI = new Sound("UI", 2);
    Sounds.finish = new Sound("finish", 2);
    stage.setOnCloseRequest((WindowEvent WE) -> {
     for (PrintWriter PW : Network.out) {
      PW.println(SL.END);
      PW.println(SL.END);
      PW.println(SL.CANCEL);
      PW.println(SL.CANCEL);
     }
    });
    initialization = "";
    Sounds.stunt.play(0);
   } catch (Exception E) {//<-Can we further specify without problems?
    System.out.println("Exception in secondary load thread:" + E);
   }
  });
  loadVE.setDaemon(true);
  loadVE.start();
 }

 public void start(Stage primaryStage) {
  Thread.currentThread().setPriority(10);
  primaryStage.setTitle("openVehicularEpic");
  try {
   primaryStage.getIcons().add(new Image(new FileInputStream(U.imageFolder + File.separator + "icon.png")));
  } catch (FileNotFoundException ignored) {
  }
  System.setProperty("sun.java2d.opengl", "true");//<-Is this even necessary?
  double windowSize = 1;
  boolean antiAliasing = false;
  String s;
  try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream(SL.GameSettings), U.standardChars))) {
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
  TE.Arrow.scene = new SubScene(TE.Arrow.group, width, height, false, antiAliasing ? SceneAntialiasing.BALANCED : SceneAntialiasing.DISABLED);
  canvas = new Canvas(width, height);
  E.canvas = new Canvas(width, height);
  graphicsContext = canvas.getGraphicsContext2D();
  E.graphicsContext = E.canvas.getGraphicsContext2D();
  scene = new Scene(new StackPane(scene3D, E.canvas, TE.Arrow.scene, canvas), width, height, false, SceneAntialiasing.DISABLED);
  primaryStage.setScene(scene);
  resetGraphics();
  U.Nodes.Light.add(Sun.light);
  new innerAnimationTimer(primaryStage).start();
  boot(primaryStage);
  primaryStage.show();
 }

 private static class innerAnimationTimer extends AnimationTimer {
  private final Stage primaryStage;

  innerAnimationTimer(Stage primaryStage) {
   this.primaryStage = primaryStage;
  }

  public void handle(long now) {
   try {
    int n;
    graphicsContext.clearRect(0, 0, width, height);
    E.graphicsContext.clearRect(0, 0, width, height);
    E.renderLevel = U.clamp(10000, E.renderLevel * (U.FPS < 30 ? .75 : 1.05), 40000);
    E.renderLevel = U.maxedFPS(true) ? Double.POSITIVE_INFINITY : E.renderLevel;
    Camera.zoom = Math.min(Camera.zoom * Camera.zoomChange, 170);
    Camera.zoom = Camera.restoreZoom[0] && Camera.restoreZoom[1] ? Camera.defaultZoom : Camera.zoom;
    Camera.camera.setFieldOfView(Camera.zoom);
    if (userPlayerIndex < vehicles.size() && vehicles.get(userPlayerIndex) != null) {
     vehicles.get(userPlayerIndex).lightBrightness = U.clamp(vehicles.get(userPlayerIndex).lightBrightness + Match.vehicleLightBrightnessChange);
    }
    E.lightsAdded = 0;
    if (Music.jLayer != null) {
     Music.jLayer.setGain(Music.gain);
     if (Music.jLayer.complete) {
      Music.load(null);
     }
    }
    if (Mouse.click) {
     Mouse.mouse = Keys.Left = Keys.Right = Keys.Enter = false;
    }
    boolean gamePlay = status == Status.play || status == Status.replay,//<-All 'gamePlay' calls in the entire project are determined by this!
    renderALL = E.renderType == E.RenderType.ALL;
    if (Mouse.mouse && (!gamePlay || !Match.started)) {
     if (Mouse.X < .375) {
      Keys.Left = true;
     } else if (Mouse.X > .625) {
      Keys.Right = true;
     } else {
      Keys.Enter = Mouse.click = true;
     }
     Mouse.click = status != Status.vehicleSelect && status != Status.mapJump && !status.name().contains("options") || Mouse.click;
    }
    selectionTimer += tick;
    if (width != primaryStage.getWidth() || height != primaryStage.getHeight()) {
     width = primaryStage.getWidth();
     height = primaryStage.getHeight();
     scene3D.setWidth(width);
     scene3D.setHeight(height);
     TE.Arrow.scene.setWidth(width);
     TE.Arrow.scene.setHeight(height);
     canvas.setWidth(width);
     canvas.setHeight(height);
     E.canvas.setWidth(width);
     E.canvas.setHeight(height);
    }
    if (gamePlay || status == Status.paused || status == Status.optionsMatch) {
     for (Vehicle vehicle : vehicles) {//*These are SPLIT so that energy towers can empower specials before the affected vehicles fire, and to make shots render correctly
      for (Special special : vehicle.specials) {
       if (special.type == Special.Type.energy) {
        special.run(gamePlay);//*
       }
      }
     }
     for (Vehicle vehicle : vehicles) {
      vehicle.runMiscellaneous(gamePlay);//Energization before miscellaneous is called
     }
     if (Match.started) {
      if (gamePlay && Match.cursorDriving) {
       Mouse.steerX = 100 * (.5 - Mouse.X);
       Mouse.steerY = 100 * (Mouse.Y - .5);
       if (vehicles.get(userPlayerIndex).P.mode != Physics.Mode.fly && !vehicles.get(userPlayerIndex).isFixed()) {
        if (Mouse.Y < .5) {
         Keys.Down = false;
         Keys.Up = true;
        } else if (Mouse.Y > .75) {
         Keys.Up = false;
         Keys.Down = true;
        } else {
         Keys.Up = Keys.Down = false;
        }
       }
       Keys.Space = Mouse.mouse;
      }
      if (Network.mode != Network.Mode.OFF) {
       Network.matchDataOut();
      }
      if (gamePlay) {
       for (Vehicle vehicle : vehicles) {
        vehicle.getPlayerInput();
        vehicle.P.run();
       }
       for (n = vehiclesInMatch; --n >= 0; ) {
        Recorder.vehicles.get(n).recordVehicle(vehicles.get(n));
       }
      }
      for (Vehicle vehicle : vehicles) {
       for (Explosion explosion : vehicle.explosions) {
        explosion.run(gamePlay);
       }
       for (Special special : vehicle.specials) {
        if (special.type != Special.Type.energy) {
         special.run(gamePlay);//*
        }
       }
      }
      if (gamePlay) {
       for (Vehicle vehicle : vehicles) {
        vehicle.P.runCollisions();
       }
       for (Vehicle vehicle : vehicles) {
        if (vehicle.destroyed && vehicle.P.vehicleHit > -1) {
         Match.scoreKill[vehicle.index < vehiclesInMatch >> 1 ? 1 : 0] += status == Status.replay ? 0 : 1;
         if (vehicle.index != userPlayerIndex) {
          vehicle.AI.target = U.random(vehiclesInMatch);//<-Needed!
         }
         vehicle.P.vehicleHit = -1;
        }
        vehicle.energyMultiple = 1;//<-Reset vehicle energy levels for next frame
       }
       if (status == Status.play) {
        Recorder.recordGeneral();
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
      Network.preMatchCommunication(gamePlay);
      Match.cursorDriving = false;
      if (Network.waiting) {
       U.font(.02);
       U.fillRGB(yinYang ? 0 : 1);
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
        Match.started = true;
        Network.waiting = false;
       }
      } else if (Keys.Space) {
       Sounds.UI.play(1, 0);
       Camera.view = Camera.lastView;
       if (Network.mode == Network.Mode.OFF) {
        Match.started = true;
       } else {
        Network.ready[userPlayerIndex] = Network.waiting = true;
        if (Network.mode == Network.Mode.HOST) {
         for (PrintWriter PW : Network.out) {
          PW.println(SL.Ready + "0");
          PW.println(SL.Ready + "0");
         }
        } else {
         Network.out.get(0).println(SL.Ready);
         Network.out.get(0).println(SL.Ready);
        }
       }
       Keys.Space = false;
      }
      if (!Network.waiting) {
       U.font(.02);
       U.fillRGB(yinYang ? 0 : 1);
       if (vehicles.get(vehiclePerspective).isFixed() && (vehiclesInMatch < 2 || vehiclePerspective < vehiclesInMatch >> 1)) {
        U.text("Use Arrow Keys and E and R to place your infrastructure, then", .2);
        if (Keys.Up || Keys.Down || Keys.Left || Keys.Right) {
         movementSpeedMultiple = Math.max(10, movementSpeedMultiple * 1.05);
         vehicles.get(vehiclePerspective).Z += Keys.Up ? movementSpeedMultiple * tick : 0;
         vehicles.get(vehiclePerspective).Z -= Keys.Down ? movementSpeedMultiple * tick : 0;
         vehicles.get(vehiclePerspective).X -= Keys.Left ? movementSpeedMultiple * tick : 0;
         vehicles.get(vehiclePerspective).X += Keys.Right ? movementSpeedMultiple * tick : 0;
        } else {
         movementSpeedMultiple = 0;
        }
       }
       U.text("Press SPACE to Begin" + (Tournament.stage > 0 ? " Round " + Tournament.stage : ""), .25);
      }
      if (Keys.Escape) {
       escapeToLast(true);
      }
     }
     Recorder.playBack();
     //RENDERING begins here
     Camera.run(vehicles.get(vehiclePerspective), gamePlay);
     U.rotate(Camera.camera, Camera.YZ, -Camera.XZ);
     Camera.rotateXY.setAngle(-Camera.XY);
     E.run(gamePlay);
     for (Vehicle vehicle : vehicles) {
      for (Special special : vehicle.specials) {
       for (Shot shot : special.shots) {
        shot.runRender();
       }
       for (Port port : special.ports) {
        if (port.spit != null) {
         port.spit.render();
        }
       }
       if (special.EB != null) {
        special.EB.renderMesh();
       }
      }
      for (VehiclePart part : vehicle.parts) {
       U.Nodes.Light.remove(part.pointLight);
      }
     }
     if (Map.defaultVehicleLightBrightness > 0) {
      for (Vehicle vehicle : vehicles) {
       U.Nodes.Light.remove(vehicle.burnLight);
      }
     }
     if (vehiclesInMatch < 2) {
      vehicles.get(vehiclePerspective).runRender(gamePlay);
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
       vehicles.get(vehiclePerspective).runRender(gamePlay);
       vehicles.get(closest).runRender(gamePlay);
      } else {
       vehicles.get(closest).runRender(gamePlay);
       vehicles.get(vehiclePerspective).runRender(gamePlay);
      }
      for (Vehicle vehicle : vehicles) {
       if (vehicle.index != vehiclePerspective && vehicle.index != closest) {
        vehicle.runRender(gamePlay);
       }
      }
     }
     for (TrackPart trackPart : TE.trackParts) {
      trackPart.runGraphics(renderALL);
     }
     for (FrustumMound mound : TE.mounds) {
      mound.runGraphics();
     }
     TE.bonus.run();
     Match.runUI(gamePlay);
     vehiclePerspective = Camera.toUserPerspective[0] && Camera.toUserPerspective[1] ? userPlayerIndex : vehiclePerspective;
     gameFPS = Double.POSITIVE_INFINITY;
     E.renderType = E.RenderType.standard;
    } else {
     E.pool.runVision();//<-Not called in-match HERE because it would draw over screenFlash
    }
    if (status == Status.paused) {
     runPaused();
    } else if (status == Status.optionsMatch || status == Status.optionsMenu) {
     Options.run();
    } else if (status == Status.vehicleViewer) {
     Viewer.Vehicle.run(gamePlay);
    } else if (status == Status.mapViewer) {
     Viewer.runMapViewer(gamePlay);
    } else if (status == Status.credits) {
     Credits.run();
    } else if (status == Status.mainMenu) {
     runMainMenu();
    } else if (status == Status.howToPlay) {
     runHowToPlay();
    } else if (status == Status.vehicleSelect) {
     VS.run(gamePlay);
    } else if (status == Status.loadLAN) {
     runLANMenu();
    } else if (status == Status.mapError) {
     Map.runErrored();
    } else if (status == Status.mapJump) {
     Map.runQuickSelect(gamePlay);
    } else if (status == Status.mapView) {
     Map.runView(gamePlay);
    } else if (status == Status.mapLoadPass0 || status == Status.mapLoadPass1 || status == Status.mapLoadPass2 || status == Status.mapLoadPass3 || status == Status.mapLoadPass4) {
     Map.load();
     Keys.falsify();
    }
    yinYang = !yinYang;
    timerBase20 = (timerBase20 += tick) > 20 ? 0 : timerBase20;
    if (status != Status.vehicleSelect) {
     for (Vehicle vehicle : vehicles) {
      if (vehicle != null && !vehicle.destroyed) {
       vehicle.flicker = !vehicle.flicker;
      }
     }
    }
    selectionTimer = (selectionTimer > selectionWait ? 0 : selectionTimer) + 5 * tick;
    if (Keys.Left || Keys.Right || Keys.Up || Keys.Down || Keys.Space || Keys.Enter) {
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
    U.setFPS();
    if (Options.showInfo) {
     U.fillRGB(0, 0, 0, colorOpacity.minimal);
     U.fillRectangle(.25, .9625, .15, .05);
     U.fillRectangle(.75, .9625, .15, .05);
     U.fillRGB(1);
     U.font(.015);
     U.text("Nodes: " + (group.getChildren().size() + TE.Arrow.group.getChildren().size() + E.lights.getChildren().size()), .25, .965);
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
 }

 private static void handleException() {
  error = "An Exception Occurred!" + U.lineSeparator + "A File with the exception has been saved to the game folder";
  status = UI.Status.mainMenu;
  Tournament.stage = selected = 0;
  resetGraphics();
  Sounds.clear();
  Keys.falsify();
  for (int n = VS.chosen.length; --n >= 0; ) {//<-Prevents recursive shutout from Vehicle Select if a bugged vehicle is causing such
   VS.chosen[n] = U.random(vehicleModels.size());
  }
  if (Network.mode == Network.Mode.HOST) {
   for (PrintWriter PW : Network.out) {
    PW.println(SL.CANCEL);
    PW.println(SL.CANCEL);
   }
  } else if (Network.mode == Network.Mode.JOIN) {
   Network.out.get(0).println(SL.CANCEL);
   Network.out.get(0).println(SL.CANCEL);
  }
 }

 public static void escapeToLast(boolean wasUser) {
  if (Network.mode == Network.Mode.OFF) {
   status = UI.Status.mainMenu;
  } else {
   for (PrintWriter PW : Network.out) {
    PW.println(SL.CANCEL);
    PW.println(SL.CANCEL);
   }
   status = UI.Status.loadLAN;
  }
  Network.mode = Network.Mode.OFF;
  page = Tournament.stage = 0;
  if (wasUser) {
   Sounds.UI.play(1, 0);
  }
  Keys.Escape = Network.runLoadThread = false;
  for (Vehicle vehicle : vehicles) {
   vehicle.closeSounds();
  }
 }

 private static void runMainMenu() {
  boolean loaded = initialization.isEmpty();
  scene.setCursor(loaded ? Cursor.CROSSHAIR : Cursor.WAIT);
  if (loaded) {
   Tournament.wins[0] = Tournament.wins[1] = userPlayerIndex = 0;
   Network.mode = Network.Mode.OFF;
   Tournament.stage = Tournament.finished ? 0 : Tournament.stage;
   Tournament.finished = Network.waiting = Viewer.inUse = false;
   TE.Arrow.MV.setVisible(false);
   if (selectionReady()) {
    if (Keys.Up) {
     selected = --selected < 0 ? 6 : selected;
     Keys.inUse = true;
     Sounds.UI.play(0, 0);
    }
    if (Keys.Down) {
     selected = ++selected > 6 ? 0 : selected;
     Keys.inUse = true;
     Sounds.UI.play(0, 0);
    }
   }
   double C = selected == 0 && yinYang ? 1 : 0;
   U.fillRGB(C, C, C);
   U.fillRectangle(.5, .6, .2, selectionHeight);
   U.fillRGB(selected == 1 && yinYang ? 1 : 0);
   U.fillRectangle(.5, .65, .2, selectionHeight);
   U.fillRGB(0, selected == 2 && yinYang ? 1 : 0, 0);
   U.fillRectangle(.5, .7, .2, selectionHeight);
   U.fillRGB(selected == 3 && yinYang ? 1 : 0, 0, 0);
   U.fillRectangle(.5, .75, .2, selectionHeight);
   U.fillRGB(0, 0, selected == 4 && yinYang ? 1 : 0);
   U.fillRectangle(.5, .8, .2, selectionHeight);
   U.fillRGB(selected == 5 && yinYang ? .5 : 0);
   U.fillRectangle(.5, .85, .2, selectionHeight);
   U.fillRGB(selected == 6 && yinYang ? .5 : 0);
   U.fillRectangle(.5, .9, .2, selectionHeight);
   if (Keys.Enter || Keys.Space) {
    Keys.inUse = true;
    if (selected == 0) {
     status = UI.Status.vehicleSelect;
     selected = VS.index = 0;
    } else if (selected == 1) {
     status = UI.Status.loadLAN;
     resetGraphics();
     selected = 0;
    } else if (selected == 2) {
     lastStatus = UI.Status.mainMenu;
     status = UI.Status.howToPlay;
    } else if (selected == 3) {
     status = UI.Status.credits;
    } else if (selected == 4) {
     status = UI.Status.optionsMenu;
     selected = 0;
    } else if (selected == 5) {
     status = UI.Status.vehicleSelect;
     vehiclesInMatch = 1;
     VS.index = 0;
     Viewer.inUse = true;
     selected = 1;
    } else if (selected == 6) {
     status = UI.Status.mapJump;
     Viewer.inUse = true;
    }
    Sounds.UI.play(1, 0);
    page = 0;
    Keys.Enter = Keys.Space = false;
    error = "";
   }
  }
  if (Keys.Escape) {
   System.exit(0);
  }
  U.font(.075);
  U.fillRGB(.5);
  U.text(SL.OPEN_VEHICULAR_EPIC, .498, .173);
  U.text(SL.OPEN_VEHICULAR_EPIC, .502, .173);
  U.text(SL.OPEN_VEHICULAR_EPIC, .498, .177);
  U.text(SL.OPEN_VEHICULAR_EPIC, .502, .177);
  U.fillRGB(1);
  U.text(SL.OPEN_VEHICULAR_EPIC, .499, .174);
  U.text(SL.OPEN_VEHICULAR_EPIC, .501, .174);
  U.text(SL.OPEN_VEHICULAR_EPIC, .499, .176);
  U.text(SL.OPEN_VEHICULAR_EPIC, .501, .176);
  U.fillRGB(.75, .75, .75);
  U.text(SL.OPEN_VEHICULAR_EPIC, .175);
  U.font(.015);
  U.fillRGB(1);
  if (loaded) {
   U.text("NEW GAME", .6 + textOffset);
   U.text("MULTIPLAYER GAME", .65 + textOffset);
   U.text(HOW_TO_PLAY, .7 + textOffset);
   U.text("CREDITS", .75 + textOffset);
   U.text(SL.OPTIONS, .8 + textOffset);
   U.text("VEHICLE VIEWER", .85 + textOffset);
   U.text("MAP VIEWER", .9 + textOffset);
   if (!error.isEmpty()) {
    U.fillRGB(yinYang ? 1 : 0, 0, 0);
    U.text(error, .3);
   }
  } else {
   U.font(.025);
   U.text(yinYang ? ".. " + initialization + "   " : "   " + initialization + " ..", .5);
  }
  if (!Keys.inUse) {
   selected =
   Math.abs(.6 + baseClickOffset - Mouse.Y) < clickRangeY ? 0 :
   Math.abs(.65 + baseClickOffset - Mouse.Y) < clickRangeY ? 1 :
   Math.abs(.7 + baseClickOffset - Mouse.Y) < clickRangeY ? 2 :
   Math.abs(.75 + baseClickOffset - Mouse.Y) < clickRangeY ? 3 :
   Math.abs(.8 + baseClickOffset - Mouse.Y) < clickRangeY ? 4 :
   Math.abs(.85 + baseClickOffset - Mouse.Y) < clickRangeY ? 5 :
   Math.abs(.9 + baseClickOffset - Mouse.Y) < clickRangeY ? 6 :
   selected;
  }
  gameFPS = U.refreshRate * .5;
 }

 private static void runPaused() {
  boolean ending = false;
  if (selectionReady()) {
   if (Keys.Up) {
    selected = --selected < 0 ? 4 : selected;
    Keys.inUse = true;
    Sounds.UI.play(0, 0);
   }
   if (Keys.Down) {
    selected = ++selected > 4 ? 0 : selected;
    Keys.inUse = true;
    Sounds.UI.play(0, 0);
   }
  }
  if (Keys.Enter || Keys.Space) {
   if (selected == 0) {
    status = UI.Status.play;
   } else if (selected == 1) {
    status = UI.Status.replay;
    Recorder.recordFrame = Recorder.gameFrame - (int) Recorder.recorded;
    while (Recorder.recordFrame < 0) Recorder.recordFrame += Recorder.totalFrames;
    Recorder.recordingsCount = 0;
   } else if (selected == 2) {
    status = UI.Status.optionsMatch;
   } else if (selected == 3) {
    lastStatus = UI.Status.paused;
    status = UI.Status.howToPlay;
   } else if (selected == 4) {
    ending = true;
   }
   Sounds.UI.play(1, 0);
   Keys.Enter = Keys.Space = false;
  }
  if (Keys.Escape) {
   ending = true;
   Sounds.UI.play(1, 0);
   Keys.Escape = false;
  }
  if (ending) {
   if (Network.mode == Network.Mode.OFF) {
    scene.setCursor(Cursor.WAIT);
    if (Tournament.stage > 0 && Match.timeLeft <= 0 && !Tournament.finished) {
     status = UI.Status.mapJump;
     Tournament.stage++;
    } else {
     status = UI.Status.mainMenu;
     Tournament.stage = 0;
    }
    Camera.lastView = Camera.view;
    selected = 0;
    Sounds.clear();
   } else {
    int n;
    if (Network.mode == Network.Mode.HOST) {
     for (PrintWriter PW : Network.out) {
      PW.println(SL.END);
      PW.println(SL.END);
     }
    }
    if (Network.hostLeftMatch || Network.mode == Network.Mode.HOST) {
     scene.setCursor(Cursor.WAIT);
     for (n = Network.maxPlayers; --n >= 0; ) {
      Network.runGameThread[n] = false;
     }
     status = UI.Status.mainMenu;
     Camera.lastView = Camera.view;
     selected = 0;
     Sounds.clear();
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
  U.fillRGB(yinYang ? .5 : 0);
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
  String title = "MATCH PAUSED";
  U.fillRGB(0);
  U.text(title, .374);
  U.fillRGB(1);
  U.text(title, .375);
  U.font(.015);
  double extraY = .01;
  U.text("RESUME", .45 + extraY);
  U.text("REPLAY", .475 + extraY);
  U.text(SL.OPTIONS, .5 + extraY);
  U.text(HOW_TO_PLAY, .525 + extraY);
  U.text(Tournament.stage > 0 ? (Match.timeLeft > 0 ? "CANCEL TOURNAMENT" : Tournament.finished ? BACK_TO_MAIN_MENU : "NEXT ROUND") : Network.mode == Network.Mode.JOIN && !Network.hostLeftMatch ? "Please Wait for Host to exit Match first" : "END MATCH", .55 + extraY);
  if (!Keys.inUse) {
   selected =
   Math.abs(.45 + baseClickOffset - Mouse.Y) < clickRangeY ? 0 :
   Math.abs(.475 + baseClickOffset - Mouse.Y) < clickRangeY ? 1 :
   Math.abs(.5 + baseClickOffset - Mouse.Y) < clickRangeY ? 2 :
   Math.abs(.525 + baseClickOffset - Mouse.Y) < clickRangeY ? 3 :
   Math.abs(.55 + baseClickOffset - Mouse.Y) < clickRangeY ? 4 :
   selected;
  }
 }

 private static void runLANMenu() {
  U.fillRGB(0, 0, 0, colorOpacity.maximal);
  U.fillRectangle(.5, .5, 1, 1);
  int n;
  if (page < 1) {
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
   page = 1;
  } else {
   scene.setCursor(Cursor.CROSSHAIR);
   if (selectionReady()) {
    if (Keys.Up) {
     selected = --selected < 0 ? 1 : selected;
     Keys.inUse = true;
     Sounds.UI.play(0, 0);
    }
    if (Keys.Down) {
     selected = ++selected > 1 ? 0 : selected;
     Keys.inUse = true;
     Sounds.UI.play(0, 0);
    }
   }
   if (yinYang && Network.mode == Network.Mode.OFF) {
    if (selected == 0) {
     U.fillRGB(0, 0, 1);
    } else {
     U.fillRGB(0, 1, 0);
    }
    U.fillRectangle(.5, selected == 1 ? .5 : .45, .25, selectionHeight);
    String s;
    try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream(SL.GameSettings), U.standardChars))) {
     for (String s1; (s1 = BR.readLine()) != null; ) {
      s = s1.trim();
      Network.userName = s.startsWith(SL.UserName + "(") ? U.getString(s, 0) : Network.userName;
      Network.targetHost = s.startsWith(SL.TargetHost + "(") ? U.getString(s, 0) : Network.targetHost;
      Network.port = s.startsWith(SL.Port + "(") ? (int) Math.round(U.getValue(s, 0)) : Network.port;
     }
    } catch (IOException e) {
     System.out.println("Problem updating Online settings: " + e);
    }
   }
   if (Network.mode == Network.Mode.HOST && !Network.out.isEmpty()) {
    for (n = 0; n < Network.out.size(); n++) {
     String s = Network.readIn(n);
     if (s.startsWith(SL.CANCEL)) {
      escapeToLast(false);
     }
    }
   }
   if ((Keys.Enter || Keys.Space) && !Network.waiting) {
    if (selected == 0) {
     Network.mode = Network.Mode.HOST;
     Network.loadGameThread();
     Network.waiting = true;
    } else if (selected == 1) {
     Network.mode = Network.Mode.JOIN;
     Network.loadGameThread();
     Network.waiting = true;
    }
    Sounds.UI.play(1, 0);
    Keys.Enter = Keys.Space = false;
   }
   U.font(.075);
   U.fillRGB(1);
   U.text(vehiclesInMatch + "-PLAYER GAME", .175);
   U.font(.01);
   U.fillRGB(1);
   if (Network.mode == Network.Mode.OFF) {
    U.text("HOST GAME", .45);
    U.text("JOIN GAME", .5);
   } else {
    if (yinYang) {
     StringBuilder s = new StringBuilder("Players in: " + Network.userName);
     for (n = 0; n < vehiclesInMatch; n++) {
      s.append(n == userPlayerIndex ? "" : ", " + playerNames[n]);
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
   if (yinYang) {
    U.font(.02);
    U.text(Network.joinError, .625);
   }
   if (Keys.Escape) {
    escapeToLast(true);
   }
   gameFPS = U.refreshRate;
  }
  if (!Keys.inUse) {
   selected =
   Math.abs(.45 + baseClickOffset - Mouse.Y) < clickRangeY ? 0 :
   Math.abs(.5 + baseClickOffset - Mouse.Y) < clickRangeY ? 1 :
   selected;
  }
 }

 private static void runHowToPlay() {
  page = Math.max(page, 1);
  U.fillRGB(0, 0, 0, colorOpacity.maximal);
  U.fillRectangle(.5, .5, 1, 1);
  U.fillRGB(1);
  U.font(.03);
  U.text(_LAST, .1, .75);
  U.text(NEXT_, .9, .75);
  U.text(RETURN, .5, .95);
  if (page == 1) {
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
   U.fillRGB(0);
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
   U.fillRGB(1);
   U.text("While Driving on the GROUND, Spacebar is the Handbrake", .5);
   U.text("For standard CARS and TRUCKS, press Spacebar to perform Stunts for points", .525);
   U.text("When on the ground and using a flying vehicle, press W + Down Arrow to Take-off at any time", .55);
   U.text("While FLYING, hold Spacebar to yaw-steer instead of steer by banking, and use W and S to control throttle", .575);
   U.text("For FIXED TURRETS, Spacebar enables finer Precision while Aiming", .6);
   U.text("B = Boost Speed/Change Aerial Velocity (if available)", .625);
   U.text("V and/or F = Use weapon(s)/specials if your vehicle has them", .65);
   U.text("For TANKS, control the turret with the W/A/S/D keys", .675);
   U.text("(It is recommended to fire the tank cannon by pressing A and D simultaneously)", .7);
   U.text("+ and - = Adjust Vehicle Light Brightness", .725);
   U.text("P = Pass bonus to a teammate (if crossing paths)", .75);
   U.fillRGB(.5, 1, .5);
   U.text("----------Cursor Controls----------", .8);
   U.fillRGB(1);
   U.text("Raise the cursor to go forward, lower it to Reverse", .825);
   U.text("Move the Cursor Left and Right to Turn", .85);
   U.text("Click to engage Handbrake/perform Stunts", .875);
  } else if (page == 2) {
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
   U.fillRGB(1);
   U.text("Grab the bonus by driving into it--turrets can also get the bonus by shooting it.", .6);
   U.text("Being in possession of the Bonus when time's up will DOUBLE you/your team's score!", .625);
   U.text("All these factors get multiplied together. When time's up, the player/team with the higher score wins!", .65);
   U.text("(Some values are handled in scientific notation for brevity)", .675);
   U.text("The user is always on the Green team--except in Multiplayer Games.", .7);
  } else if (page == 3) {
   U.font(.0125);
   U.text("Other Important Information", .125);
   U.text("Based on the given circumstances, pick the best strategy (race, fight, etc.)", .175);
   U.text("Press 'C' to toggle the guidance arrow between pointing to the Vehicles or Racetrack", .2);
   U.text("Your vehicle will revive shortly after being destroyed.", .25);
   U.text("However, you can Repair it before then by passing through an Electrified Ring/Diamond", .275);
   U.text("(Repairing the vehicle is not possible on all maps).", .3);
   U.text("It's important to note that vehicles on the same team don't 'interact',", .35);
   U.text("so there's no need to worry about crashing into your own team members, friendly fire, etc.", .375);
   U.fillRGB(1, 1, .5);
   U.text("Not all maps have checkpoints and a designated route.", .45);
   U.text("You'll need to score points using methods besides checkpoints and laps", .475);
   U.text("such as good stunts, fighting opponents, or keeping the bonus.", .5);
   U.fillRGB(0, 1, 1);
   U.text("Some Maps have special environments or may be less straightforward.", .55);
   U.text("There may be an extra learning curve to such maps.", .575);
   U.fillRGB(1);
   U.text("Some vehicles have Guided weaponry.", .625);
   U.text("When fired, these weapons will intercept the nearest opponent automatically.", .65);
  } else if (page == 4) {
   U.font(.0125);
   U.text("Other Key Controls:", .15);
   U.text("Digits 1-7 = Camera Views", .25);
   U.text("Z or X = To look around/behind you while driving (for Views 1-4)", .275);
   U.fillRGB(.5, 1, .5);
   U.text("(Press Z and X simultaneously to look forward again)", .3);
   U.fillRGB(1);
   U.text("Enter or Escape = Pause/exit out of Match", .325);
   U.text("M = Mute Sound", .35);
   U.text("< and > = Music Volume", .375);
   U.text("Control and Shift = Adjust Zoom", .4);
   U.fillRGB(.5, 1, .5);
   U.text("(Press Control and Shift simultaneously to restore Zoom)", .425);
   U.fillRGB(1);
   U.text("E or R = Change Player Perspective (and set turrets/infrastructure before starting a match)", .45);
   U.fillRGB(.5, 1, .5);
   U.text("(Press E and R simultaneously to view yourself again)", .475);
   U.fillRGB(1);
   U.text("H = Heads-up Display ON/OFF", .5);
   U.text("L = Destruction Log ON/OFF", .525);
   U.text("I = Show/Hide Application Info", .55);
   U.text("There are many other aspects not covered here in these instructions,", .7);
   U.text("but you will learn with experience.", .725);
   U.text("GOOD LUCK", .75);
  }
  if (selectionReady()) {
   if (Keys.Right) {
    if (++page > 4) {
     page = 0;
     status = lastStatus;
    }
    Sounds.UI.play(0, 0);
   }
   if (Keys.Left) {
    if (--page < 1) {
     page = 0;
     status = lastStatus;
    }
    Sounds.UI.play(0, 0);
   }
   if (Keys.Enter) {
    page = 0;
    status = lastStatus;
    Sounds.UI.play(1, 0);
    Keys.Enter = false;
   }
  }
  if (Keys.Escape) {
   page = 0;
   status = lastStatus;
   Sounds.UI.play(1, 0);
   Keys.Escape = false;
  }
  gameFPS = U.refreshRate * .25;
 }

 public static void reset() {
  Match.started = Camera.flowFlip = false;
  vehiclePerspective = userPlayerIndex;
  Match.timeLeft = Options.matchLength;
  TE.Arrow.target = Math.min(vehiclesInMatch - 1, TE.Arrow.target);
  Match.scoreCheckpoint[0] = Match.scoreCheckpoint[1] = Match.scoreLap[0] = Match.scoreLap[1] = Match.scoreKill[0] = Match.scoreKill[1] = 1;
  Match.scoreDamage[0] = Match.scoreDamage[1] = Camera.aroundVehicleXZ = Match.printTimer = Camera.lookAround = Match.scoreStunt[0] = Match.scoreStunt[1] = 0;
  Bonus.big.setVisible(true);
  for (Bonus.Ball bonusBall : Bonus.balls) {
   bonusBall.setVisible(false);
  }
  bonusHolder = Network.bonusHolder = -1;
  Match.stuntTimer = TE.MS.timer = Recorder.recorded = TE.MS.point = 0;
  int n;
  for (n = DestructionLog.names.length; --n >= 0; ) {
   DestructionLog.names[n][0] = "";
   DestructionLog.names[n][1] = "";
   DestructionLog.nameColors[n][0] = new Color(0, 0, 0, 1);
   DestructionLog.nameColors[n][1] = new Color(0, 0, 0, 1);
  }
  DestructionLog.inUse = vehiclesInMatch > 1;
  if (!Viewer.inUse && Network.mode == Network.Mode.OFF) {
   for (n = vehiclesInMatch; --n >= 0; ) {
    playerNames[n] = vehicles.get(n).name;
   }
  }
  Camera.mapSelectRandomRotationDirection = U.random() < .5 ? 1 : -1;
  E.renderLevel = Double.POSITIVE_INFINITY;//<-Render everything once first to prevent frame spikes at match start
  Network.ready = new boolean[Network.maxPlayers];
  scene.setCursor(Cursor.CROSSHAIR);
 }

 public static void resetGraphics() {
  boolean addSunlightBack = E.lights.getChildren().contains(Sun.light),//<-Check LIGHT group, not main group!
  addSunBack = group.getChildren().contains(Sun.S),
  addGroundBack = group.getChildren().contains(Ground.C);
  group.getChildren().clear();
  E.lights.getChildren().clear();
  U.Nodes.add(E.ambientLight, addSunBack ? Sun.S : null, addGroundBack ? Ground.C : null);
  U.Nodes.Light.add(addSunlightBack ? Sun.light : null);
  U.Nodes.add(E.lights);
  TE.Arrow.group.getChildren().clear();
 }

 public static void denyExpensiveInGameCall() {
  if (status == Status.play || status == Status.replay) {
   throw new IllegalStateException("For performance reasons--this call is not allowed during gameplay.");
  }
 }
}
