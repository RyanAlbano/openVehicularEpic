package ve.ui;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import kuusisto.tinysound.TinySound;
import ve.environment.*;
import ve.instances.I;
import ve.trackElements.Arrow;
import ve.trackElements.TE;
import ve.ui.options.Options;
import ve.ui.options.Units;
import ve.utilities.*;
import ve.utilities.sound.FireAndForget;
import ve.utilities.sound.Sounds;

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
 public static long userFPS = U.refreshRate;
 private static String initialization = "Loading V.E.";
 static String error = "";
 public static final String[] playerNames = new String[I.maxPlayers];
 public static Status status = Status.mainMenu;
 static Status lastStatus;

 public enum Status {
  play, replay, paused, mainMenu, credits,
  vehicleSelect, vehicleViewer,
  optionsMatch, optionsMenu, optionsGraphics, optionsSound,
  mapJump, mapLoadPass0, mapLoadPass1, mapLoadPass2, mapLoadPass3, mapLoadPass4, mapError, mapView, mapViewer,
  howToPlay, loadLAN
 }

 public static long selected;
 static double selectionWait;
 static double selectionTimer;
 public static long page;
 public static final double selectionHeight = .03;
 public static final double clickRangeY = selectionHeight * .5;
 public static final double baseClickOffset = .025;
 public static final double textOffset = .01;
 static double movementSpeedMultiple = 1;
 static final String Yes = "Yes";
 static final String No = "No";
 public static final String ON = "ON";
 public static final String OFF = "OFF";
 static final String HIDE = "HIDE";
 static final String _LAST = "<-LAST";
 static final String NEXT_ = "NEXT->";
 static final String CONTINUE = "CONTINUE";
 public static final String RETURN = "RETURN";
 static final String BACK_TO_MAIN_MENU = "BACK TO MAIN MENU";
 private static final String HOW_TO_PLAY = "HOW TO PLAY";
 static final String Made_by_ = "Made by ";
 static final String GREEN_TEAM = "GREEN TEAM";
 static final String RED_TEAM = "RED TEAM";
 static final String/*..*/Please_Wait_For_ = "..Please Wait for ";
 static final String notifyUserOfArrowKeyNavigation = "You can also use the Arrow Keys and Enter to navigate.";
 public static final String At_File_/*:*/ = "At File: ", At_Line_/*:*/ = "At Line: ";
 private static final String loadingTheRest = "Loading the rest";
 public static FireAndForget sound;

 public static boolean selectionReady() {
  return selectionTimer > selectionWait;
 }

 public enum colorOpacity {
  ;
  public static final double minimal = .5;
  public static final double maximal = .75;
 }

 public static void run(String[] s) {
  launch(s);
 }

 public void start(Stage primaryStage) {
  Thread.currentThread().setPriority(10);
  primaryStage.setTitle("openVehicularEpic");
  try {
   primaryStage.getIcons().add(new Image(new FileInputStream(Images.folder + File.separator + "icon" + Images.extension)));
  } catch (FileNotFoundException ignored) {
  }
  //System.out.println(Arrays.toString(AudioSystem.getMixerInfo()));
  double windowSize = 1;
  boolean antiAliasing = false;
  String s;
  try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream(D.GameSettings), U.standardChars))) {
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
  Thread secondaryLoad = new Thread(() -> {
   try {
    int n;
    scene3D.setFill(U.getColor(0));
    Arrow.scene.setFill(Color.color(0, 0, 0, 0));
    initialization = "Loading Settings";
    String s;
    try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream(D.GameSettings), U.standardChars))) {
     for (String s1; (s1 = BR.readLine()) != null; ) {
      s = s1.trim();
      Units.units = s.startsWith("Units(") ? Units.Unit.valueOf(U.getString(s, 0)) : Units.units;
      if (s.startsWith("Units(U.S.")) {
       Units.units = Units.Unit.US;
      }
      Camera.shake = s.startsWith("CameraShake(yes") || Camera.shake;
      try {
       userFPS = s.startsWith("fpsLimit(") ? Math.round(U.getValue(s, 0)) : userFPS;
      } catch (RuntimeException ignored) {
      }
      Options.matchLength = s.startsWith(D.MatchLength + "(") ? Math.round(U.getValue(s, 0)) : Options.matchLength;
      Options.driverSeat = s.startsWith("DriverSeat(left") ? -1 : s.startsWith("DriverSeat(right") ? 1 : Options.driverSeat;
      I.vehiclesInMatch = s.startsWith("#ofPlayers(") ? Math.max(1, Math.min((int) Math.round(U.getValue(s, 0)), I.maxPlayers)) : I.vehiclesInMatch;
      Options.headsUpDisplay = s.startsWith("HUD(on") || Options.headsUpDisplay;
      Options.showAppInfo = s.startsWith("ShowAppInfo(yes") || Options.showAppInfo;
      VS.showModel = s.startsWith("ShowVehiclesInVehicleSelect(yes") || VS.showModel;
      //IMAGES
      Texture.normalMapping = s.startsWith("NormalMapping(yes") || Texture.normalMapping;
      Texture.type = s.startsWith("TextureResolution(") ? Texture.Resolution.valueOf(U.getString(s, 0)) : Texture.type;
      Texture.userMaxResolution = s.startsWith("TextureResolutionLimit(") ? Math.round(U.getValue(s, 0)) : Texture.userMaxResolution;
      //SOUND
      Sounds.softwareBased = s.startsWith("SoftwareBased(yes") || Sounds.softwareBased;
      Sounds.channels = s.startsWith("Channels(") ? (int) Math.round(U.getValue(s, 0)) : Sounds.channels;
      Sounds.bitDepth = s.startsWith("BitDepth(") ? (int) Math.round(U.getValue(s, 0)) : Sounds.bitDepth;
      Sounds.sampleRate = s.startsWith("SampleRate(") ? U.getValue(s, 0) : Sounds.sampleRate;
      Sounds.bufferSize = s.startsWith("BufferSize(") ? U.getValue(s, 0) : Sounds.bufferSize;
      //NETWORK
      Network.userName = s.startsWith(D.UserName + "(") ? U.getString(s, 0) : Network.userName;
      Network.targetHost = s.startsWith(D.TargetHost + "(") ? U.getString(s, 0) : Network.targetHost;
      Network.port = s.startsWith(D.Port + "(") ? (int) Math.round(U.getValue(s, 0)) : Network.port;
      //
      if (s.startsWith(D.GameVehicles + "(")) {
       I.vehicleModels = new ArrayList<>(Arrays.asList(s.substring((D.GameVehicles + "(").length(), s.length() - 1).split(",")));
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
    if (Sounds.softwareBased) {
     TinySound.init();//<-Must load AFTER audio settings are gotten! Loaded here so that stunt boot sound can be fully heard?
    }
    initialization = "Loading Images";
    Images.RA = Images.load(D.RA);
    Images.white = Images.load(D.white);
    Images.fireLight = Images.load(D.firelight, Double.POSITIVE_INFINITY);
    Images.blueJet = Images.load(D.blueJet, Double.POSITIVE_INFINITY);
    Images.blink = Images.load(D.blink, Double.POSITIVE_INFINITY);
    Images.amphibious = Images.load("amphibious");
    initialization = "Loading Textures";
    Images.water = new Texture(D.water);
    Images.rock = new Texture(D.rock);
    Images.metal = new Texture(D.metal);
    Images.brightmetal = new Texture(D.brightmetal);
    Images.grid = new Texture(D.grid);
    Images.paved = new Texture(D.paved);
    Images.wood = new Texture(D.wood);
    Images.foliage = new Texture(D.foliage);
    Images.cactus = new Texture(D.cactus);
    Images.grass = new Texture(D.grass);
    Images.sand = new Texture(D.sand);
    Images.ground1 = new Texture(D.ground + 1);
    Images.ground2 = new Texture(D.ground + 2);
    initialization = "Loading Normal Maps";
    Images.rockN = new Texture("rockN");
    Images.metalN = new Texture("metalN");
    Images.brightmetalN = new Texture("brightmetalN");
    Images.pavedN = new Texture("pavedN");
    Images.woodN = new Texture("woodN");
    Images.foliageN = new Texture("foliageN");
    Images.cactusN = new Texture("cactusN");
    Images.grassN = new Texture("grassN");
    Images.sandN = new Texture("sandN");
    Images.ground1N = new Texture("ground1N");
    Images.ground2N = new Texture("ground2N");
    TE.Paved.setTexture();
    initialization = loadingTheRest;
    stage.setOnCloseRequest((WindowEvent WE) -> {
     for (PrintWriter PW : Network.out) {
      PW.println(D.END);
      PW.println(D.END);
      PW.println(D.CANCEL);
      PW.println(D.CANCEL);
     }
     TinySound.shutdown();//<-IMPORTANT
    });
   } catch (Exception E) {//<-Good enough
    System.out.println("Exception in secondary load thread: " + E);
    E.printStackTrace();//<-Not on the main thread, so better print
   }
  });
  secondaryLoad.setDaemon(true);
  secondaryLoad.start();
 }

 public static void escapeToLast(boolean wasUser) {
  if (Network.mode == Network.Mode.OFF) {
   status = UI.Status.mainMenu;
  } else {
   for (PrintWriter PW : Network.out) {
    PW.println(D.CANCEL);
    PW.println(D.CANCEL);
   }
   status = UI.Status.loadLAN;
  }
  Network.mode = Network.Mode.OFF;
  page = Tournament.stage = 0;
  Keys.escape = Network.runLoadThread = false;
  Sounds.reset();
  if (wasUser) {
   sound.play(1, 0);//<-Must play sound AFTER resetting!
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
    if (Keys.up) {
     selected = --selected < 0 ? 6 : selected;
     sound.play(0, 0);
    }
    if (Keys.down) {
     selected = ++selected > 6 ? 0 : selected;
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
   if (!Keys.inUse) {
    selected =
    Math.abs(.6 - baseClickOffset - Mouse.Y) < clickRangeY ? 0 :
    Math.abs(.65 - baseClickOffset - Mouse.Y) < clickRangeY ? 1 :
    Math.abs(.7 - baseClickOffset - Mouse.Y) < clickRangeY ? 2 :
    Math.abs(.75 - baseClickOffset - Mouse.Y) < clickRangeY ? 3 :
    Math.abs(.8 - baseClickOffset - Mouse.Y) < clickRangeY ? 4 :
    Math.abs(.85 - baseClickOffset - Mouse.Y) < clickRangeY ? 5 :
    Math.abs(.9 - baseClickOffset - Mouse.Y) < clickRangeY ? 6 :
    selected;
   }
   if (Keys.enter || Keys.space) {
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
    Keys.enter = Keys.space = false;
    error = "";
   }
  }
  if (Keys.escape) {
   System.exit(0);
  }
  U.font(.075);
  U.fillRGB(.5);
  U.text(D.OPEN_VEHICULAR_EPIC, .498, .173);
  U.text(D.OPEN_VEHICULAR_EPIC, .502, .173);
  U.text(D.OPEN_VEHICULAR_EPIC, .498, .177);
  U.text(D.OPEN_VEHICULAR_EPIC, .502, .177);
  U.fillRGB(1);
  U.text(D.OPEN_VEHICULAR_EPIC, .499, .174);
  U.text(D.OPEN_VEHICULAR_EPIC, .501, .174);
  U.text(D.OPEN_VEHICULAR_EPIC, .499, .176);
  U.text(D.OPEN_VEHICULAR_EPIC, .501, .176);
  U.fillRGB(.75, .75, .75);
  U.text(D.OPEN_VEHICULAR_EPIC, .175);
  U.font(.015);
  U.fillRGB(1);
  if (loaded) {
   U.text("NEW MATCH", .6 + textOffset);
   U.text("MULTIPLAYER MATCH", .65 + textOffset);
   U.text(HOW_TO_PLAY, .7 + textOffset);
   U.text("CREDITS", .75 + textOffset);
   U.text(D.OPTIONS, .8 + textOffset);
   U.text("VEHICLE VIEWER", .85 + textOffset);
   U.text("MAP VIEWER", .9 + textOffset);
   if (!error.isEmpty()) {
    U.fillRGB(U.yinYang ? 1 : 0, 0, 0);
    U.text(error, .3);
   }
  } else {
   U.font(.025);
   U.text(U.yinYang ? ".. " + initialization + "   " : "   " + initialization + " ..", .5);
   if (initialization.equals(loadingTheRest)) {//<-All the stuff that couldn't be loaded on the secondary-load thread. Better late than never!
    Images.getLowResolutionTextures();
    Sounds.reset();
    Sounds.stunt.play(0);
    initialization = "";//<-Game booting is now finished
   }
  }
  gameFPS = U.refreshRate * .5;
 }

 static void runPaused() {
  if (selectionReady()) {
   if (Keys.up) {
    selected = --selected < 0 ? 4 : selected;
    sound.play(0, 0);
   }
   if (Keys.down) {
    selected = ++selected > 4 ? 0 : selected;
    sound.play(0, 0);
   }
  }
  boolean ending = false;
  if (!Keys.inUse) {
   selected =
   Math.abs(.45 - baseClickOffset - Mouse.Y) < clickRangeY ? 0 :
   Math.abs(.475 - baseClickOffset - Mouse.Y) < clickRangeY ? 1 :
   Math.abs(.5 - baseClickOffset - Mouse.Y) < clickRangeY ? 2 :
   Math.abs(.525 - baseClickOffset - Mouse.Y) < clickRangeY ? 3 :
   Math.abs(.55 - baseClickOffset - Mouse.Y) < clickRangeY ? 4 :
   selected;
  }
  if (Keys.enter || Keys.space) {
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
   Keys.enter = Keys.space = false;
  }
  if (Keys.escape) {
   ending = true;
   Keys.escape = false;
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
    Sounds.reset();
    sound.play(1, 0);
   } else {
    int n;
    if (Network.mode == Network.Mode.HOST) {
     for (PrintWriter PW : Network.out) {
      PW.println(D.END);
      PW.println(D.END);
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
     Sounds.reset();
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
  U.text("RESUME", .45 + textOffset);
  U.text("REPLAY", .475 + textOffset);
  U.text(D.OPTIONS, .5 + textOffset);
  U.text(HOW_TO_PLAY, .525 + textOffset);
  U.text(Tournament.stage > 0 ? (Match.timeLeft > 0 ? "CANCEL TOURNAMENT" : Tournament.finished ? BACK_TO_MAIN_MENU : "NEXT ROUND") :
  Network.mode == Network.Mode.JOIN && !Network.hostLeftMatch ? "Please Wait for Host to exit Match first" :
  "END MATCH", .55 + textOffset);
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
    if (Keys.up) {
     selected = --selected < 0 ? 1 : selected;
     sound.play(0, 0);
    }
    if (Keys.down) {
     selected = ++selected > 1 ? 0 : selected;
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
    try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream(D.GameSettings), U.standardChars))) {
     for (String s1; (s1 = BR.readLine()) != null; ) {
      s = s1.trim();
      Network.userName = s.startsWith(D.UserName + "(") ? U.getString(s, 0) : Network.userName;
      Network.targetHost = s.startsWith(D.TargetHost + "(") ? U.getString(s, 0) : Network.targetHost;
      Network.port = s.startsWith(D.Port + "(") ? (int) Math.round(U.getValue(s, 0)) : Network.port;
     }
    } catch (IOException e) {
     System.out.println("Problem updating Online settings: " + e);
    }
   }
   if (Network.mode == Network.Mode.HOST && !Network.out.isEmpty()) {
    for (n = 0; n < Network.out.size(); n++) {
     String s = Network.readIn(n);
     if (s.startsWith(D.CANCEL)) {
      escapeToLast(false);
     }
    }
   }
   if (!Keys.inUse) {
    selected =
    Math.abs(.45 - baseClickOffset - Mouse.Y) < clickRangeY ? 0 :
    Math.abs(.5 - baseClickOffset - Mouse.Y) < clickRangeY ? 1 :
    selected;
   }
   if ((Keys.enter || Keys.space) && !Network.waiting) {
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
    Keys.enter = Keys.space = false;
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
   if (Keys.escape) {
    escapeToLast(true);
   }
   gameFPS = U.refreshRate;
  }
 }

 static void drawBonus(double X, double Y, double size) {
  Color last = (Color) GC.getFill();
  U.fillRGB(U.random(), U.random(), U.random());
  double half = size * .5;
  GC.fillOval((width * X) - half, (height * Y) - half, size, size);
  U.fillRGB(last);
 }

 public static void denyExpensiveInGameCall() {
  if (status == Status.play || status == Status.replay) {
   crashGame("For performance reasons--this call is not allowed during gameplay.");
  }
 }

 /**
  * Doesn't actually crash (permanently freeze) the application; reports an action in the code deemed illegal and should return to the main menu.
  */
 public static void crashGame(String message) {
  IllegalStateException ISE = new IllegalStateException(message);
  ISE.printStackTrace();//<-Called here in case the app refuses to return to menu and handle the exception--hopefully the stack will still show up in an IDE
  throw ISE;
 }
}
