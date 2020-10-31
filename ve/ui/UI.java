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
import kuusisto.tinysound.TinySound;
import ve.environment.E;
import ve.environment.Pool;
import ve.environment.Sun;
import ve.instances.I;
import ve.trackElements.Bonus;
import ve.trackElements.TE;
import ve.trackElements.trackParts.RepairPoint;
import ve.ui.options.GraphicsOptions;
import ve.ui.options.Options;
import ve.ui.options.SoundOptions;
import ve.ui.options.Units;
import ve.utilities.Camera;
import ve.utilities.*;
import ve.utilities.sound.FireAndForget;
import ve.utilities.sound.Sounds;
import ve.vehicles.Physics;
import ve.vehicles.explosions.MaxNukeBlast;
import ve.vehicles.specials.Special;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class UI/*UserInterface*/ extends Application {

 public static Scene scene;
 public static SubScene scene3D;
 public static final Group group = new Group();
 private static Canvas canvas;
 public static GraphicsContext GC;
 public static double width, height;
 public static double errorTimer;
 public static double gameFPS = Double.POSITIVE_INFINITY;
 public static long userFPS = U.refreshRate;
 private static String initialization = "Loading V.E.";
 private static String error = "";
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
 private static double selectionWait;
 private static double selectionTimer;
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
  canvas = new Canvas(width, height);
  E.canvas = new Canvas(width, height);
  GC = canvas.getGraphicsContext2D();
  E.GC = E.canvas.getGraphicsContext2D();
  scene = new Scene(new StackPane(scene3D, E.canvas, canvas), width, height, false, SceneAntialiasing.DISABLED);
  primaryStage.setScene(scene);
  primaryStage.show();//<-Don't call before this level!
  Nodes.reset();
  Nodes.addPointLight(Sun.light);
  new AnimationTimer() {//<-Don't make static class
   public void handle(long now) {
    try {
     int n;
     GC.clearRect(0, 0, width, height);
     E.GC.clearRect(0, 0, width, height);
     if (U.FPS < 30) {
      E.renderLevel = 10000;
     } else if (U.goodFPS(false)) {
      E.renderLevel += 500 * U.tick;
     }
     E.renderLevel = Math.max(10000, E.renderLevel);
     Camera.FOV = Math.min(Camera.FOV * Camera.adjustFOV, 170);
     if (Camera.restoreZoom[0] && Camera.restoreZoom[1]) {
      Camera.FOV = Camera.defaultFOV;
     }
     Camera.PC.setFieldOfView(Camera.FOV);
     if (I.userPlayerIndex < I.vehicles.size() && I.vehicles.get(I.userPlayerIndex) != null) {
      I.vehicles.get(I.userPlayerIndex).lightBrightness = U.clamp(I.vehicles.get(I.userPlayerIndex).lightBrightness + Match.vehicleLightBrightnessChange);
     }
     E.lightsAdded = 0;//<-Must come before any lights get added, obviously
     if (Mouse.click) {
      Mouse.mouse = Keys.left = Keys.right = Keys.enter = false;
     }
     boolean gamePlay = status == UI.Status.play || status == UI.Status.replay,//<-All 'gamePlay' calls in the entire project are determined by this!
     renderALL = E.renderType == E.RenderType.ALL;
     if (Mouse.mouse && (!gamePlay || !Match.started)) {
      if (Mouse.X < .375) {
       Keys.left = true;
      } else if (Mouse.X > .625) {
       Keys.right = true;
      } else {
       Keys.enter = Mouse.click = true;
      }
      Mouse.click = status != UI.Status.vehicleSelect && status != UI.Status.mapJump && !status.name().contains("options") || Mouse.click;
     }
     selectionTimer += U.tick;
     if (width != primaryStage.getWidth() || height != primaryStage.getHeight()) {
      width = primaryStage.getWidth();
      height = primaryStage.getHeight();
      scene3D.setWidth(width);
      scene3D.setHeight(height);
      canvas.setWidth(width);
      canvas.setHeight(height);
      E.canvas.setWidth(width);
      E.canvas.setHeight(height);
     }
     if (gamePlay || status == UI.Status.paused || status == UI.Status.optionsMatch) {
      for (var vehicle : I.vehicles) {//*These are SPLIT so that energy towers can empower specials before the affected vehicles fire, and to make shots render correctly
       for (var special : vehicle.specials) {
        if (special.type == Special.Type.energy) {
         special.EB.run(gamePlay);//*
        }
       }
      }
      //Energization before runMiscellaneous() is called
      for (var vehicle : I.vehicles) {
       vehicle.runMiscellaneous(gamePlay);
      }
      if (Match.started) {
       if (gamePlay && Match.cursorDriving) {
        Mouse.steerX = 100 * (.5 - Mouse.X);
        Mouse.steerY = 100 * (Mouse.Y - .5);
        if (I.vehicles.get(I.userPlayerIndex).P.mode != Physics.Mode.fly && !I.vehicles.get(I.userPlayerIndex).isFixed()) {
         if (Mouse.Y < .5) {
          Keys.down = false;
          Keys.up = true;
         } else if (Mouse.Y > .75) {
          Keys.up = false;
          Keys.down = true;
         } else {
          Keys.up = Keys.down = false;
         }
        }
        Keys.space = Mouse.mouse;
       }
       if (Network.mode != Network.Mode.OFF) {
        Network.matchDataOut();
       }
       if (gamePlay) {
        for (var vehicle : I.vehicles) {
         vehicle.getPlayerInput();
         vehicle.P.run();
        }
        for (n = I.vehiclesInMatch; --n >= 0; ) {
         Recorder.vehicles.get(n).recordVehicle(I.vehicles.get(n));
        }
       }
       for (var vehicle : I.vehicles) {
        for (var explosion : vehicle.explosions) {
         explosion.run(gamePlay);
        }
        double
        sinXZ = U.sin(vehicle.XZ), cosXZ = U.cos(vehicle.XZ),
        sinYZ = U.sin(vehicle.YZ), cosYZ = U.cos(vehicle.YZ),
        sinXY = U.sin(vehicle.XY), cosXY = U.cos(vehicle.XY);
        for (var special : vehicle.specials) {
         special.run(gamePlay, sinXZ, cosXZ, sinYZ, cosYZ, sinXY, cosXY);//*
        }
       }
       if (gamePlay) {
        for (var vehicle : I.vehicles) {
         vehicle.P.runCollisions();
        }
        for (var vehicle : I.vehicles) {
         if (vehicle.destroyed && vehicle.P.vehicleHit > -1) {
          if (vehicle.index != I.userPlayerIndex) {
           vehicle.AI.target = U.random(I.vehiclesInMatch);//<-Needed!
          }
          vehicle.P.vehicleHit = -1;
         }
         vehicle.energyMultiple = 1;//<-Reset vehicle energy levels for next frame
        }
        if (status == UI.Status.play) {
         Recorder.recordGeneral();
         if (Network.mode == Network.Mode.OFF) {
          for (var vehicle : I.vehicles) {
           vehicle.AI.run();
          }
         }
        }
       }
       Recorder.updateFrame();
      } else {
       for (var vehicle : I.vehicles) {
        vehicle.setTurretY();
       }
       Network.preMatchCommunication(gamePlay);
       Match.cursorDriving = false;
       if (Network.waiting) {
        U.font(.02);
        U.fillRGB(U.yinYang ? 0 : 1);
        if (I.vehiclesInMatch < 3) {
         U.text("..Waiting on " + playerNames[Network.mode == Network.Mode.HOST ? 1 : 0] + "..", .5, .25);
        } else {
         U.text("..Waiting for all Players to Start..", .5, .25);
        }
        long whoIsReady = 0;
        for (n = I.vehiclesInMatch; --n >= 0; ) {
         whoIsReady += Network.ready[n] ? 1 : 0;
        }
        if (whoIsReady >= I.vehiclesInMatch) {
         if (Network.mode == Network.Mode.HOST) {
          for (n = I.vehiclesInMatch; --n > 0; ) {
           Network.gamePlay(n);
          }
         } else {
          Network.gamePlay(0);
         }
         Match.started = true;
         Network.waiting = false;
        }
       } else if (Keys.space) {
        sound.play(1, 0);
        Camera.view = Camera.lastView;
        if (Network.mode == Network.Mode.OFF) {
         Match.started = true;
        } else {
         Network.ready[I.userPlayerIndex] = Network.waiting = true;
         if (Network.mode == Network.Mode.HOST) {
          for (var PW : Network.out) {
           PW.println(D.Ready + "0");
           PW.println(D.Ready + "0");
          }
         } else {
          Network.out.get(0).println(D.Ready);
          Network.out.get(0).println(D.Ready);
         }
        }
        Keys.space = false;
       }
       if (Keys.escape) {
        escapeToLast(true);
       }
      }
      Recorder.playBack();
      //RENDERING begins here
      Camera.run(I.vehicles.get(I.vehiclePerspective), gamePlay);
      MaxNukeBlast.runLighting();//<-Just after camera is placed, but before any other environmental/vehicular lights get added
      E.run(gamePlay);
      getClosests();
      for (var vehicle : I.vehicles) {
       for (var special : vehicle.specials) {
        for (var shot : special.shots) {
         shot.runRender();
        }
        for (var port : special.ports) {
         if (port.spit != null) {
          port.spit.runRender();
         }
         if (port.smokes != null) {
          for (var smoke : port.smokes) {
           smoke.runRender();
          }
         }
        }
        if (special.EB != null) {
         special.EB.renderMesh();
        }
       }
       for (var part : vehicle.parts) {
        Nodes.removePointLight(part.pointLight);
       }
      }
      if (Maps.defaultVehicleLightBrightness > 0) {
       for (var vehicle : I.vehicles) {
        Nodes.removePointLight(vehicle.burnLight);
       }
      }
      if (I.vehiclesInMatch < 2) {
       I.vehicles.get(I.vehiclePerspective).runRender(gamePlay);
      } else {
       int closest = I.vehiclePerspective;//Not replacing with I.closest here--it gets too complicated!
       double compareDistance = Double.POSITIVE_INFINITY;
       for (var vehicle : I.vehicles) {
        if (vehicle.index != I.vehiclePerspective && U.distance(I.vehicles.get(I.vehiclePerspective), vehicle) < compareDistance) {
         closest = vehicle.index;
         compareDistance = U.distance(I.vehicles.get(I.vehiclePerspective), vehicle);
        }
       }
       if (I.vehicles.get(I.vehiclePerspective).lightBrightness >= I.vehicles.get(closest).lightBrightness) {
        I.vehicles.get(I.vehiclePerspective).runRender(gamePlay);
        I.vehicles.get(closest).runRender(gamePlay);
       } else {
        I.vehicles.get(closest).runRender(gamePlay);
        I.vehicles.get(I.vehiclePerspective).runRender(gamePlay);
       }
       for (var vehicle : I.vehicles) {
        if (vehicle.index != I.vehiclePerspective && vehicle.index != closest) {
         vehicle.runRender(gamePlay);
        }
       }
      }
      for (var trackPart : TE.trackParts) {
       trackPart.runGraphics(renderALL);
      }
      for (var repairPoint : RepairPoint.instances) {
       repairPoint.run();
      }
      for (var mound : TE.mounds) {
       mound.runGraphics();
      }
      Bonus.run();
      Match.run(gamePlay);
      if (Camera.toUserPerspective[0] && Camera.toUserPerspective[1]) {
       I.vehiclePerspective = I.userPlayerIndex;
      }
      gameFPS = Double.POSITIVE_INFINITY;
      E.renderType = E.RenderType.standard;
     } else {
      Pool.runVision();//<-Not called in-match HERE because it would draw over screenFlash
     }
     if (status == UI.Status.paused) {
      runPaused();
     } else if (status == UI.Status.optionsMatch || status == UI.Status.optionsMenu) {
      Options.run();
     } else if (status == UI.Status.optionsGraphics) {
      GraphicsOptions.run();
     } else if (status == UI.Status.optionsSound) {
      SoundOptions.run();
     } else if (status == UI.Status.vehicleViewer) {
      Viewer.Vehicle.run(gamePlay);
     } else if (status == UI.Status.mapViewer) {
      Viewer.runMapViewer(gamePlay);
     } else if (status == UI.Status.credits) {
      Credits.run();
     } else if (status == UI.Status.mainMenu) {
      runMainMenu();
     } else if (status == UI.Status.howToPlay) {
      HowToPlay.run();
     } else if (status == UI.Status.vehicleSelect) {
      VS.run(gamePlay);
     } else if (status == UI.Status.loadLAN) {
      runLANMenu();
     } else if (status == UI.Status.mapError) {
      Maps.runErrored();
     } else if (status == UI.Status.mapJump) {
      Maps.runQuickSelect(gamePlay);
     } else if (status == UI.Status.mapView) {
      Maps.runView(gamePlay);
     } else if (status == UI.Status.mapLoadPass0 || status == UI.Status.mapLoadPass1 || status == UI.Status.mapLoadPass2 || status == UI.Status.mapLoadPass3 || status == UI.Status.mapLoadPass4) {
      Maps.load();
      Keys.falsify();
     }
     U.yinYang = !U.yinYang;
     U.timerBase20 = (U.timerBase20 += U.tick) > 20 ? 0 : U.timerBase20;
     selectionTimer = (selectionTimer > selectionWait ? 0 : selectionTimer) + 5 * U.tick;
     if (Keys.left || Keys.right || Keys.up || Keys.down || Keys.space || Keys.enter) {
      if (selectionWait == -1) {
       selectionWait = 30;
       selectionTimer = 0;
      }
      if (selectionWait > 0) {
       selectionWait -= U.tick;
      }
     } else {
      selectionWait = -1;
      selectionTimer = 0;
     }
     double targetFPS = Math.min(gameFPS, userFPS), dividedFPS = 1000 / targetFPS,
     difference = System.currentTimeMillis() - U.FPSTime;
     if (difference < dividedFPS) {
      U.zZz(dividedFPS - difference);
     }
     U.setFPS();
     if (Options.showAppInfo) {
      U.fillRGB(0, 0, 0, UI.colorOpacity.minimal);
      U.fillRectangle(.25, .9625, .15, .05);
      U.fillRectangle(.75, .9625, .15, .05);
      U.fillRGB(1);
      U.font(.015);
      U.text("Nodes: " + (group.getChildren().size() + E.lights.getChildren().size()), .25, .965);
      U.font(.02);
      U.text(Math.round(U.averageFPS) + " FPS", .75, .965);
     }
     long time = System.nanoTime();
     U.tick = Math.min((time - U.lastTime) * .00000002, 1);//<-todo--start migrating to .00000001 standard? (will be long and brutal)
     U.tickSeconds = U.tick * .05;//<-Incrementing/decrementing some value by this value per frame should change its value by '1.0' over the course of 1 second
     U.lastTime = time;
    } catch (Exception E) {//<-It's for the entire loop--a general exception is probably most surefire
     try (PrintWriter PW = new PrintWriter(new File("V.E. EXCEPTION"), U.standardChars)) {
      E.printStackTrace(PW);
     } catch (IOException ignored) {
     }
     E.printStackTrace();
     handleException();
    }
   }
  }.start();
  boot(primaryStage);
 }

 private static void handleException() {
  error = "An Exception Occurred!" + U.lineSeparator + "A File with the exception has been saved to the game folder";
  status = UI.Status.mainMenu;
  Tournament.stage = selected = 0;
  Nodes.reset();
  Sounds.reset();
  Keys.falsify();
  for (int n = VS.chosen.length; --n >= 0; ) {//<-Prevents recursive shutout from Vehicle Select if a bugged vehicle is causing such
   VS.chosen[n] = U.random(I.vehicleModels.size());
  }
  if (Network.mode == Network.Mode.HOST) {
   for (var PW : Network.out) {
    PW.println(D.CANCEL);
    PW.println(D.CANCEL);
   }
  } else if (Network.mode == Network.Mode.JOIN) {
   Network.out.get(0).println(D.CANCEL);
   Network.out.get(0).println(D.CANCEL);
  }
 }

 private static void getClosests() {
  int[] hold = new int[I.vehiclesInMatch];
  for (int n = I.vehiclesInMatch; --n >= 0; ) {
   for (int n1 = n; --n1 >= 0; ) {
    hold[U.distance(I.vehicles.get(n)) > U.distance(I.vehicles.get(n1)) ? n : n1]++;
   }
   I.closest[hold[n]] = n;
  }
 }

 private static void boot(Stage stage) {
  Thread secondaryLoad = new Thread(() -> {
   try {
    int n;
    scene3D.setFill(U.getColor(0));
    initialization = "Loading GameSettings";
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
      if (s.startsWith("SoftwareBased(")) {
       if (s.contains("yes")) {
        Sounds.softwareBased = true;
       } else if (s.contains("no")) {
        Sounds.softwareBased = false;
       }
      }
      Sounds.useEcho = s.startsWith("UseEcho(yes") || Sounds.useEcho;
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
    Images.red = Images.load("red");
    Images.green = Images.load("green");
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
     for (var PW : Network.out) {
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
   for (var PW : Network.out) {
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

 private static void runMainMenu() {
  boolean loaded = initialization.isEmpty();
  scene.setCursor(loaded ? Cursor.CROSSHAIR : Cursor.WAIT);
  if (loaded) {
   I.userPlayerIndex = 0;
   if (Tournament.finished) {
    Tournament.stage = 0;
   }
   Tournament.reset(Math.min(Tournament.stage, 1));
   Network.waiting = Viewer.inUse = false;
   Network.mode = Network.Mode.OFF;
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

 private static void runPaused() {
  U.fillRGB(0, 0, 0, colorOpacity.minimal);
  U.fillRectangle(.5, .5, .375, .375);
  boolean rematch = Network.mode == Network.Mode.OFF && (Tournament.finished || (Tournament.stage < 1 && Match.timeLeft <= 0));
  if (selectionReady()) {
   long wrap = rematch ? 5 : 4;
   if (Keys.up) {
    selected = --selected < 0 ? wrap : selected;
    sound.play(0, 0);
   }
   if (Keys.down) {
    selected = ++selected > wrap ? 0 : selected;
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
   rematch && Math.abs(.575 - baseClickOffset - Mouse.Y) < clickRangeY ? 5 :
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
   } else if (selected > 3) {
    ending = true;
   }
   sound.play(1, 0);
   Keys.enter = Keys.space = false;
  }
  if (Keys.escape) {
   ending = true;
   Keys.escape = rematch = false;
  }
  if (ending) {
   scene.setCursor(Cursor.WAIT);
   if (rematch && selected == 5) {
    status = UI.Status.mapJump;
    Tournament.reset(Tournament.stage > 0 ? 1 : 0);
   } else if (Network.mode == Network.Mode.OFF) {
    if (Tournament.stage > 0 && !Tournament.finished && Match.timeLeft <= 0/*<-Needed!*/) {
     status = UI.Status.mapJump;
     if (!Match.tied()) {
      Tournament.stage++;
     }
    } else {
     status = UI.Status.mainMenu;
     Tournament.reset(0);
    }
    Camera.lastView = Camera.view;
    selected = 0;
    Sounds.reset();
    sound.play(1, 0);
   } else {
    int n;
    if (Network.mode == Network.Mode.HOST) {
     for (var PW : Network.out) {
      PW.println(D.END);
      PW.println(D.END);
     }
    }
    if (Network.hostLeftMatch || Network.mode == Network.Mode.HOST) {
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
      for (var in : Network.in) {
       in.close();
      }
      for (var PW : Network.out) {
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
  if (selected == 5 && rematch) {
   U.fillRectangle(.5, .575, .2, selectionHeight);
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
  U.text(
  Tournament.stage > 0 ? (Match.timeLeft > 0 ? "CANCEL TOURNAMENT" : Tournament.finished ? BACK_TO_MAIN_MENU : "NEXT ROUND") :
  Network.mode == Network.Mode.JOIN && !Network.hostLeftMatch ? "Please Wait for Host to exit Match first" :
  "END MATCH", .55 + textOffset);
  if (rematch) {
   U.text("REMATCH!", .575 + textOffset);
  }
 }

 private static void runLANMenu() {
  U.fillRGB(0, 0, 0, colorOpacity.maximal);
  U.fillRectangle(.5, .5, 1, 1);
  int n;
  if (page < 1) {
   Network.mode = Network.Mode.OFF;
   I.vehiclesInMatch = (int) U.clamp(2, I.vehiclesInMatch, Network.maxPlayers);
   try {
    for (var in : Network.in) {
     in.close();
    }
    Network.in.clear();
    for (var PW : Network.out) {
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
