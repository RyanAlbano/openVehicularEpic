package ve.ui;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ve.environment.*;
import ve.instances.I;
import ve.trackElements.Arrow;
import ve.trackElements.Bonus;
import ve.utilities.*;
import ve.utilities.Camera;
import ve.vehicles.*;

import java.io.*;
import java.util.*;

public class UI/*UserInterface*/ extends Application {

 public static Scene scene;
 public static SubScene scene3D;
 public static final Group group = new Group();
 static Canvas canvas;
 public static GraphicsContext GC;
 public static double width, height;
 public static double errorTimer;
 public static double gameFPS = Double.POSITIVE_INFINITY;
 public static long userFPS = Long.MAX_VALUE;
 private static String initialization = "Loading V.E.";
 static String error = "";
 public static final String[] playerNames = new String[I.maxPlayers];
 public static Status status = Status.mainMenu;
 private static Status lastStatus;
 public static Sound sound;

 public enum Status {
  play, replay, paused, optionsMatch, optionsMenu, mainMenu, credits,
  vehicleSelect, vehicleViewer,
  mapJump, mapLoadPass0, mapLoadPass1, mapLoadPass2, mapLoadPass3, mapLoadPass4, mapError, mapView, mapViewer,
  howToPlay, loadLAN
 }

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

 public static void run(String[] s) {//<-Will NOT work if moved to Launcher!
  launch(s);
 }

 public void start(Stage primaryStage) {
  Thread.currentThread().setPriority(10);
  primaryStage.setTitle("openVehicularEpic");
  try {
   primaryStage.getIcons().add(new Image(new FileInputStream(U.imageFolder + File.separator + "icon" + U.imageExtension)));
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
  Arrow.scene = new SubScene(Arrow.group, width, height, false, antiAliasing ? SceneAntialiasing.BALANCED : SceneAntialiasing.DISABLED);
  canvas = new Canvas(width, height);
  E.canvas = new Canvas(width, height);
  GC = canvas.getGraphicsContext2D();
  E.GC = E.canvas.getGraphicsContext2D();
  scene = new Scene(new StackPane(scene3D, E.canvas, Arrow.scene, canvas), width, height, false, SceneAntialiasing.DISABLED);
  primaryStage.setScene(scene);
  primaryStage.show();//<-Don't call before this level!
  Nodes.reset();
  Nodes.addPointLight(Sun.light);
  new GameLoop(primaryStage).start();
  boot(primaryStage);
 }

 private static void boot(Stage stage) {
  Thread loadVE = new Thread(() -> {
   try {
    int n;
    scene3D.setFill(U.getColor(0));
    Arrow.scene.setFill(Color.color(0, 0, 0, 0));
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
       Units.units = Units.Unit.metric;
      } else if (s.startsWith("Units(U.S.")) {
       Units.units = Units.Unit.US;
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
      I.vehiclesInMatch = s.startsWith("#ofPlayers(") ? Math.max(1, Math.min((int) Math.round(U.getValue(s, 0)), I.maxPlayers)) : I.vehiclesInMatch;
      Options.headsUpDisplay = s.startsWith("HUD(on") || Options.headsUpDisplay;
      Options.showAppInfo = s.startsWith("ShowInfo(yes") || Options.showAppInfo;
      VS.showModel = s.startsWith("ShowVehiclesInVehicleSelect(yes") || VS.showModel;
      Network.userName = s.startsWith(SL.UserName + "(") ? U.getString(s, 0) : Network.userName;
      Network.targetHost = s.startsWith(SL.TargetHost + "(") ? U.getString(s, 0) : Network.targetHost;
      Network.port = s.startsWith(SL.Port + "(") ? (int) Math.round(U.getValue(s, 0)) : Network.port;
      if (s.startsWith(SL.GameVehicles + "(")) {
       I.vehicleModels = new ArrayList<>(Arrays.asList(s.substring((SL.GameVehicles + "(").length(), s.length() - 1).split(",")));
      } else if (s.startsWith("UserSubmittedVehicles(")) {
       String[] models = U.regex.split(s);
       for (n = 1; n < models.length; n++) {
        I.vehicleModels.add(models[n]);
       }
      } else if (s.startsWith("UserSubmittedMaps(")) {
       String[] mapList = U.regex.split(s);
       for (n = 1; n < mapList.length; n++) {
        Maps.maps.add(mapList[n]);
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
    sound = new Sound("UI", 2);
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
   } catch (Exception E) {
    System.out.println("Exception in secondary load thread:" + E);
   }
  });
  loadVE.setDaemon(true);
  loadVE.start();
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
   sound.play(1, 0);
  }
  Keys.Escape = Network.runLoadThread = false;
  for (Vehicle vehicle : I.vehicles) {
   vehicle.closeSounds();
  }
 }

 static void runMainMenu() {
  boolean loaded = initialization.isEmpty();
  scene.setCursor(loaded ? Cursor.CROSSHAIR : Cursor.WAIT);
  if (loaded) {
   Tournament.wins[0] = Tournament.wins[1] = I.userPlayerIndex = 0;
   Network.mode = Network.Mode.OFF;
   Tournament.stage = Tournament.finished ? 0 : Tournament.stage;
   Tournament.finished = Network.waiting = Viewer.inUse = false;
   Arrow.MV.setVisible(false);
   if (selectionReady()) {
    if (Keys.Up) {
     selected = --selected < 0 ? 6 : selected;
     Keys.inUse = true;
     sound.play(0, 0);
    }
    if (Keys.Down) {
     selected = ++selected > 6 ? 0 : selected;
     Keys.inUse = true;
     sound.play(0, 0);
    }
   }
   double C = selected == 0 && U.yinYang ? 1 : 0;
   U.fillRGB(C, C, C);
   U.fillRectangle(.5, .6, .2, selectionHeight);
   U.fillRGB(selected == 1 && U.yinYang ? 1 : 0);
   U.fillRectangle(.5, .65, .2, selectionHeight);
   U.fillRGB(0, selected == 2 && U.yinYang ? 1 : 0, 0);
   U.fillRectangle(.5, .7, .2, selectionHeight);
   U.fillRGB(selected == 3 && U.yinYang ? 1 : 0, 0, 0);
   U.fillRectangle(.5, .75, .2, selectionHeight);
   U.fillRGB(0, 0, selected == 4 && U.yinYang ? 1 : 0);
   U.fillRectangle(.5, .8, .2, selectionHeight);
   U.fillRGB(selected == 5 && U.yinYang ? .5 : 0);
   U.fillRectangle(.5, .85, .2, selectionHeight);
   U.fillRGB(selected == 6 && U.yinYang ? .5 : 0);
   U.fillRectangle(.5, .9, .2, selectionHeight);
   if (Keys.Enter || Keys.Space) {
    Keys.inUse = true;
    if (selected == 0) {
     status = UI.Status.vehicleSelect;
     selected = VS.index = 0;
    } else if (selected == 1) {
     status = UI.Status.loadLAN;
     Nodes.reset();
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
     I.vehiclesInMatch = 1;
     VS.index = 0;
     Viewer.inUse = true;
     selected = 1;
    } else if (selected == 6) {
     status = UI.Status.mapJump;
     Viewer.inUse = true;
    }
    sound.play(1, 0);
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
    U.fillRGB(U.yinYang ? 1 : 0, 0, 0);
    U.text(error, .3);
   }
  } else {
   U.font(.025);
   U.text(U.yinYang ? ".. " + initialization + "   " : "   " + initialization + " ..", .5);
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

 static void runPaused() {
  if (selectionReady()) {
   if (Keys.Up) {
    selected = --selected < 0 ? 4 : selected;
    Keys.inUse = true;
    sound.play(0, 0);
   }
   if (Keys.Down) {
    selected = ++selected > 4 ? 0 : selected;
    Keys.inUse = true;
    sound.play(0, 0);
   }
  }
  boolean ending = false;
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
   sound.play(1, 0);
   Keys.Enter = Keys.Space = false;
  }
  if (Keys.Escape) {
   ending = true;
   sound.play(1, 0);
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
  U.fillRGB(U.yinYang ? .5 : 0);
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

 static void runLANMenu() {
  U.fillRGB(0, 0, 0, colorOpacity.maximal);
  U.fillRectangle(.5, .5, 1, 1);
  int n;
  if (page < 1) {
   Network.mode = Network.Mode.OFF;
   I.vehiclesInMatch = (int) U.clamp(2, I.vehiclesInMatch, Network.maxPlayers);
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
     sound.play(0, 0);
    }
    if (Keys.Down) {
     selected = ++selected > 1 ? 0 : selected;
     Keys.inUse = true;
     sound.play(0, 0);
    }
   }
   if (U.yinYang && Network.mode == Network.Mode.OFF) {
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
    sound.play(1, 0);
    Keys.Enter = Keys.Space = false;
   }
   U.font(.075);
   U.fillRGB(1);
   U.text(I.vehiclesInMatch + "-PLAYER GAME", .175);
   U.font(.01);
   U.fillRGB(1);
   if (Network.mode == Network.Mode.OFF) {
    U.text("HOST GAME", .45);
    U.text("JOIN GAME", .5);
   } else {
    if (U.yinYang) {
     StringBuilder s = new StringBuilder("Players in: " + Network.userName);
     for (n = 0; n < I.vehiclesInMatch; n++) {
      s.append(n == I.userPlayerIndex ? "" : ", " + playerNames[n]);
     }
     U.text(s.toString(), .45);
    }
    U.text("(Hit ESCAPE to Cancel)", .5);
   }
   if (errorTimer <= 0) {
    Network.joinError = "";
   } else {
    errorTimer -= U.tick;
   }
   U.text("Your UserName: " + Network.userName, .7);
   U.text("Your Target Host is: " + Network.targetHost, .725);
   U.text("Your Port #: " + Network.port, .75);
   U.text("For more information about this game mode, please read the Game Documentation", .85);
   if (U.yinYang) {
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

 static void runHowToPlay() {
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
   U.fillRectangle(.125, .225, keySizeX, keySizeY);//Q
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
   U.text("Q", .125, .225);
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
   U.text("V and/or F = Use weapons/specials if your vehicle has them", .65);
   U.text("For TANKS, control the turret with the W/A/S/D keys", .675);
   U.text("(It is recommended to fire the tank cannon by pressing A and D simultaneously)", .7);
   U.text("+ and - = Adjust Vehicle Light Brightness", .725);
   U.text("Q = Amphibious mode ON/OFF (for amphibious-capable vehicles)", .75);
   U.text("P = Pass bonus to a teammate (if crossing paths)", .775);
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
   GC.fillOval((width * .5) - 50, (height * .5) - 50, 100, 100);
   U.fillRGB(1);
   U.text("Grab the bonus by driving into it--turrets can also get the bonus by shooting it.", .6);
   U.text("Being in possession of the Bonus when time's up will DOUBLE your (team's) score!", .625);
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
   U.text("Control and Shift = Adjust Field-Of-View", .375);
   U.fillRGB(.5, 1, .5);
   U.text("(Press Control and Shift simultaneously to restore F.O.V.)", .4);
   U.fillRGB(1);
   U.text("< or > = Change Player Perspective (and set turrets/infrastructure before starting a match)", .425);
   U.fillRGB(.5, 1, .5);
   U.text("(Press < and > simultaneously to view yourself again)", .45);
   U.fillRGB(1);
   U.text("H = Heads-up Display ON/OFF", .475);
   U.text("L = Destruction Log ON/OFF", .5);
   U.text("I = Show/Hide Application Info", .525);
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
    sound.play(0, 0);
   }
   if (Keys.Left) {
    if (--page < 1) {
     page = 0;
     status = lastStatus;
    }
    sound.play(0, 0);
   }
   if (Keys.Enter) {
    page = 0;
    status = lastStatus;
    sound.play(1, 0);
    Keys.Enter = false;
   }
  }
  if (Keys.Escape) {
   page = 0;
   status = lastStatus;
   sound.play(1, 0);
   Keys.Escape = false;
  }
  gameFPS = U.refreshRate * .25;
 }

 public static void crashOnExpensiveInGameCall() {
  if (status == Status.play || status == Status.replay) {
   throw new IllegalStateException("For performance reasons--this call is not allowed during gameplay.");
  }
 }
}
