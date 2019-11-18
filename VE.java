/*
THE VEHICULAR EPIC

HEAD DEVELOPER: Ryan Albano
PAST ASSISTANT DEVS: Vitor Macedo, Dany Fernández Diaz

NAMING SYSTEMS (variable names are usually typed as follows):

wordWord

For example, 'current checkpoint' would be typed as 'currentCheckpoint'.

Successive capital letters are usually abbreviations, such as 'PhongMaterial PM' or 'FileInputStream FIS';

Methods beginning with 'run' are usually called recursively per frame, i.e. 'runGraphics'.

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
import javafx.scene.shape.Sphere;
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
import ve.utilities.SL;
import ve.utilities.U;
import ve.vehicles.*;
import ve.vehicles.specials.Port;
import ve.vehicles.specials.Shot;
import ve.vehicles.specials.Special;

import java.io.*;
import java.util.*;

public class VE extends Application {

 private static Scene scene;
 public static SubScene scene3D;
 public static final Group group = new Group();
 private static Canvas canvas;
 public static GraphicsContext graphicsContext;
 static final int maxPlayers = (int) Math.round(Math.max(Network.maxPlayers, Runtime.getRuntime().maxMemory() * .00000001125));
 public static int vehiclePerspective;
 public static int userPlayerIndex;
 public static int vehiclesInMatch = 1;
 static final int[] vehicleNumber = new int[maxPlayers];
 public static int bonusHolder = -1;
 public static int map;
 public static boolean yinYang;
 public static double tick;
 static double timerBase20;
 public static double width, height;
 static double errorTimer;
 public static Color userRandomRGB = U.getColor(U.random(), U.random(), U.random());
 static double gameFPS = Double.POSITIVE_INFINITY;
 private static long lastTime;
 private static long userFPS = Long.MAX_VALUE;
 private static String initialization = "Loading V.E.";
 public static String vehicleMaker = "";
 private static final String[] unitName = {"VEs", "VEs"};
 private static String error = "";
 public static final String[] playerNames = new String[maxPlayers];
 public static final java.util.Map<String, Image> images = new HashMap<>();
 public static final List<Vehicle> vehicles = new ArrayList<>(maxPlayers);
 public static List<String> vehicleModels;
 public static final List<String> maps = new ArrayList<>(Arrays.asList("basic", "lapsGlory", "checkpoint", "gunpowder", "underOver", "antigravity", "versus1", "versus2", "versus3", "trackless", "desert", "3DRace", "trip", "raceNowhere", "moonlight", "bottleneck", "railing", "twisted", "deathPit", "falls", "pyramid", "fusion", "darkDivide", "arctic", "scenicRoute", "winterMode", "mountainHop", "damage", "cavern", "southPole", "aerialControl", "matrix", "mist", "vansLand", "dustDevil", "forest", "columns", "zipCross", "highlands", "coldFury", "tornado", "volcanic", "tsunami", "boulder", "sands", "meteor", "speedway", "endurance", "tunnel", "circle", "circleXL", "circles", "everything", "linear", "maze", "xy", "stairwell", "immense", "showdown", "ocean", "lastStand", "parkingLot", "city", "machine", "military", "underwater", "hell", "moon", "mars", "sun", "space1", "space2", "space3", "summit", "portal", "blackHole", "doomsday", "+UserMap & TUTORIAL+"));
 public static Status status = VE.Status.mainMenu;
 private static Status lastStatus;

 public enum Status {
  play, replay, paused, optionsMatch, optionsMenu, mainMenu, credits,
  vehicleSelect, vehicleViewer,
  mapJump, mapLoadPass0, mapLoadPass1, mapLoadPass2, mapLoadPass3, mapLoadPass4, mapError, mapView, mapViewer,
  howToPlay, loadLAN
 }

 enum UI {//UserInterface
  ;
  private static long selected;
  private static double selectionWait, selectionTimer;
  static long page;
  private static final double selectionHeight = .03, clickRangeY = selectionHeight * .5, baseClickOffset = -.025, textOffset = .01;
  private static double movementSpeedMultiple = 1;

  static boolean selectionReady() {
   return selectionTimer > selectionWait;
  }

  private enum drawOpacity {
   ;
   private static final double minimal = .5, maximal = .75;
  }
 }

 static int getVehicleIndex(String s) {
  String s1, s3 = "";
  int n;
  for (n = 0; n < vehicleModels.size(); n++) {
   File F = new File(U.modelFolder + File.separator + vehicleModels.get(n));
   if (!F.exists()) {
    F = new File(U.modelFolder + File.separator + U.userSubmittedFolder + File.separator + vehicleModels.get(n));
   }
   if (!F.exists()) {
    F = new File(U.modelFolder + File.separator + "basic");
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
  File F = new File(U.modelFolder + File.separator + vehicleModels.get(in));
  if (!F.exists()) {
   F = new File(U.modelFolder + File.separator + U.userSubmittedFolder + File.separator + vehicleModels.get(in));
  }
  if (!F.exists()) {
   F = new File(U.modelFolder + File.separator + "basic");
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

 public enum Keys {
  ;
  public static boolean Up, Down, Left, Right, Space, W, S, A, D;
  public static final boolean[] Special = new boolean[2];
  public static boolean keyBoost, PassBonus;
  static boolean Enter, Escape;
  private static boolean inUse;

  static {
   scene.setOnKeyPressed((KeyEvent keyEvent) -> {
    KeyCode KC = keyEvent.getCode();
    if (KC == KeyCode.UP || KC == KeyCode.LEFT || KC == KeyCode.RIGHT || KC == KeyCode.DOWN || KC == KeyCode.SPACE) {
     if (Match.cursorDriving) {
      Up = Down = Left = Right = Space = Match.cursorDriving = false;
     }
     Up = KC == KeyCode.UP || Up;
     Down = KC == KeyCode.DOWN || Down;
     Left = KC == KeyCode.LEFT || Left;
     Right = KC == KeyCode.RIGHT || Right;
     Space = KC == KeyCode.SPACE || Space;
    } else if (KC == KeyCode.ENTER) {
     Enter = true;
    } else if (KC == KeyCode.ESCAPE) {
     Escape = true;
    } else if (KC == KeyCode.Z) {
     Camera.lookAround = 1;
     Camera.lookForward[1] = true;
     Camera.view = Camera.view == Camera.View.flow && !Match.started ? Camera.lastViewWithLookAround : Camera.view;
    } else if (KC == KeyCode.X) {
     Camera.lookAround = -1;
     Camera.lookForward[0] = true;
     Camera.view = Camera.view == Camera.View.flow && !Match.started ? Camera.lastViewWithLookAround : Camera.view;
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
     Special[0] = true;
    } else if (KC == KeyCode.F) {
     Special[1] = true;
    } else if (KC == KeyCode.B) {
     keyBoost = true;
    } else if (KC == KeyCode.W) {
     W = true;
    } else if (KC == KeyCode.S) {
     S = true;
    } else if (KC == KeyCode.A) {
     A = true;
     VS.allSame = !VS.allSame;
    } else if (KC == KeyCode.D) {
     D = true;
    } else if (KC == KeyCode.C) {
     TE.Arrow.status =
     TE.Arrow.status == TE.Arrow.Status.racetrack ? TE.Arrow.Status.vehicles :
     TE.Arrow.status == TE.Arrow.Status.vehicles ? TE.Arrow.Status.locked :
     TE.Arrow.Status.racetrack;
    } else if (KC == KeyCode.E) {
     vehiclePerspective = --vehiclePerspective < 0 ? vehiclesInMatch - 1 : vehiclePerspective;
     Camera.toUserPerspective[1] = true;
    } else if (KC == KeyCode.R) {
     vehiclePerspective = ++vehiclePerspective >= vehiclesInMatch ? 0 : vehiclePerspective;
     Camera.toUserPerspective[0] = true;
    } else if (KC == KeyCode.H) {
     Options.headsUpDisplay = !Options.headsUpDisplay;
    } else if (KC == KeyCode.L) {
     DestructionLog.inUse = !DestructionLog.inUse;
    } else if (KC == KeyCode.P) {
     PassBonus = true;
    } else if (KC == KeyCode.SHIFT) {
     Camera.zoomChange = .98;
     Camera.restoreZoom[1] = true;
    } else if (KC == KeyCode.CONTROL) {
     Camera.zoomChange = 1.02;
     Camera.restoreZoom[0] = true;
    } else if (KC == KeyCode.M) {
     Match.muteSound = !Match.muteSound;
    } else if (KC == KeyCode.COMMA) {
     Music.gain = Math.max(Music.gain * 2 - 1, -100);
    } else if (KC == KeyCode.PERIOD) {
     Music.gain = Math.min(Music.gain * .5 + 1, 0);
    } else if (KC == KeyCode.T || KC == KeyCode.G || KC == KeyCode.U || KC == KeyCode.J) {
     Viewer.height = KC == KeyCode.J ? 10 : KC == KeyCode.U ? -10 : Viewer.height;
     Viewer.depth = KC == KeyCode.T ? 10 : KC == KeyCode.G ? -10 : Viewer.depth;
     UI.movementSpeedMultiple = Math.max(10, UI.movementSpeedMultiple * 1.05);
    } else if (KC == KeyCode.EQUALS) {
     Match.vehicleLightBrightnessChange = .01;
    } else if (KC == KeyCode.MINUS) {
     Match.vehicleLightBrightnessChange = -.01;
    } else if (KC == KeyCode.I) {
     Options.showInfo = !Options.showInfo;
    }
   });
   scene.setOnKeyReleased((KeyEvent keyEvent) -> {
    KeyCode KC = keyEvent.getCode();
    Up = KC != KeyCode.UP && Up;
    Down = KC != KeyCode.DOWN && Down;
    Left = KC != KeyCode.LEFT && Left;
    Right = KC != KeyCode.RIGHT && Right;
    Space = KC != KeyCode.SPACE && Space;
    Enter = KC != KeyCode.ENTER && Enter;
    Escape = KC != KeyCode.ESCAPE && Escape;
    W = KC != KeyCode.W && W;
    S = KC != KeyCode.S && S;
    A = KC != KeyCode.A && A;
    D = KC != KeyCode.D && D;
    if (KC == KeyCode.Z || KC == KeyCode.X) {
     Camera.lookAround = 0;
     Camera.lookForward[0] = Camera.lookForward[1] = false;
    } else if (KC == KeyCode.V) {
     Special[0] = false;
    } else if (KC == KeyCode.F) {
     Special[1] = false;
    } else if (KC == KeyCode.B) {
     keyBoost = false;
    } else if (KC == KeyCode.P) {
     PassBonus = false;
    } else if (KC == KeyCode.E || KC == KeyCode.R) {
     Camera.toUserPerspective[0] = Camera.toUserPerspective[1] = false;
    } else if (KC == KeyCode.SHIFT || KC == KeyCode.CONTROL) {
     Camera.zoomChange = 1;
     Camera.restoreZoom[0] = Camera.restoreZoom[1] = false;
    } else if (KC == KeyCode.T || KC == KeyCode.G || KC == KeyCode.U || KC == KeyCode.J) {
     if (KC == KeyCode.U || KC == KeyCode.J) {
      Viewer.height = 0;
     } else {
      Viewer.depth = 0;
     }
     UI.movementSpeedMultiple = 0;
    } else if (KC == KeyCode.EQUALS || KC == KeyCode.MINUS) {
     Match.vehicleLightBrightnessChange = 0;
    }
   });
  }

  private static void falsify() {
   Up = Down = Left = Right = Space =
   W = S = A = D =
   Enter = Special[0] = Special[1] = keyBoost = Escape = false;
  }
 }

 public enum Mouse {
  ;
  private static boolean mouse, click;//What's 'mouse' for?
  private static double X, Y;
  public static double steerX, steerY;

  static {
   scene.setOnMouseMoved((MouseEvent mouseEvent) -> {
    Keys.inUse = false;
    X = mouseEvent.getX() / width;
    Y = mouseEvent.getY() / height;
    Match.cursorDriving = Match.started || Match.cursorDriving;
   });
   scene.setOnMousePressed((MouseEvent mouseEvent) -> {
    X = mouseEvent.getX() / width;
    Y = mouseEvent.getY() / height;
    mouse = !click || mouse;
   });
   scene.setOnMouseReleased((MouseEvent mouseEvent) -> {
    X = mouseEvent.getX() / width;
    Y = mouseEvent.getY() / height;
    click = mouse = false;
    Keys.falsify();
   });
  }
 }

 public enum Map {
  ;
  public static String name = "";
  public static boolean randomVehicleStartAngle, guardCheckpointAI;
  public static double speedLimitAI;
  public static double defaultVehicleLightBrightness;

  private static void load() {
   TE.instanceSize = 1;
   TE.instanceScale = new double[]{1, 1, 1};
   TE.randomX = TE.randomY = TE.randomZ = 0;
   if (status == VE.Status.mapLoadPass0) {
    scene.setCursor(Cursor.WAIT);
   } else if (status == VE.Status.mapLoadPass1) {
    resetGraphics();
    TE.Arrow.addToScene();
    vehicles.clear();
    E.reset();
    TE.reset();
    Camera.camera.setFarClip(Camera.clipRange.maximumFar);
    scene3D.setFill(Color.color(0, 0, 0));
    defaultVehicleLightBrightness = 0;
    randomVehicleStartAngle = guardCheckpointAI = false;
    speedLimitAI = Double.POSITIVE_INFINITY;
   }
   int n;
   String s = "";
   try (BufferedReader BR = new BufferedReader(new InputStreamReader(Objects.requireNonNull(U.getMapFile(map)), U.standardChars))) {
    for (; (s = BR.readLine()) != null; ) {
     s = s.trim();
     if (status == VE.Status.mapLoadPass2) {
      name = s.startsWith("name") ? U.getString(s, 0) : name;
      if (s.startsWith("ambientLight(")) {
       U.Nodes.Light.setRGB(E.ambientLight, U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
      }
      Star.load(s);
      E.loadSky(s);
      E.Ground.load(s);
      E.Terrain.load(s);
      if (s.startsWith("viewDistance(")) {
       E.viewableMapDistance = U.getValue(s, 0);
       Camera.camera.setFarClip(U.clamp(Camera.clipRange.normalNear + 1, U.getValue(s, 0), Camera.clipRange.maximumFar));
      } else if (s.startsWith("soundTravel(")) {
       E.soundMultiple = U.getValue(s, 0);
      }
      E.gravity = s.startsWith("gravity(") ? U.getValue(s, 0) : E.gravity;
      E.Sun.load(s);
      defaultVehicleLightBrightness = s.startsWith("defaultBrightness(") ? U.getValue(s, 0) : defaultVehicleLightBrightness;
      randomVehicleStartAngle = s.startsWith("randomStartAngle") || randomVehicleStartAngle;
      E.mapBounds.left = s.startsWith("xLimitLeft(") ? U.getValue(s, 0) : E.mapBounds.left;
      E.mapBounds.right = s.startsWith("xLimitRight(") ? U.getValue(s, 0) : E.mapBounds.right;
      E.mapBounds.forward = s.startsWith("zLimitFront(") ? U.getValue(s, 0) : E.mapBounds.forward;
      E.mapBounds.backward = s.startsWith("zLimitBack(") ? U.getValue(s, 0) : E.mapBounds.backward;
      E.mapBounds.Y = s.startsWith("yLimit(") ? U.getValue(s, 0) : E.mapBounds.Y;
      E.mapBounds.slowVehicles = s.startsWith("slowVehiclesWhenAtLimit") || E.mapBounds.slowVehicles;
      speedLimitAI = s.startsWith("speedLimit(") ? U.getValue(s, 0) : speedLimitAI;
      E.Ground.level = s.startsWith("noGround") ? Double.POSITIVE_INFINITY : E.Ground.level;
      guardCheckpointAI = s.startsWith("guardCheckpoint") || guardCheckpointAI;
      if (s.startsWith("snow(")) {
       for (n = 0; n < U.getValue(s, 0); n++) {
        Snowball.instances.add(new Snowball.Instance());
       }
      } else if (s.startsWith("wind(")) {
       E.Wind.maxPotency = U.getValue(s, 0);
       E.Wind.speedX = U.randomPlusMinus(E.Wind.maxPotency);
       E.Wind.speedZ = U.randomPlusMinus(E.Wind.maxPotency);
      } else if (s.startsWith("windstorm")) {
       E.Wind.stormExists = true;
       E.Wind.storm = new Sound("storm" + U.getString(s, 0));
      }
      Cloud.load(s);
      E.Storm.load(s);
      Tornado.load(s);
      E.loadMountains(s);
      E.Pool.load(s);
      if (s.startsWith("trees(")) {
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
      } else if (s.startsWith("mounds(")) {
       for (n = 0; n < U.getValue(s, 0); n++) {
        TE.trackParts.add(new TrackPart(U.randomPlusMinus(800000), U.randomPlusMinus(800000), 0, 100 + U.random(400.), U.random(100.), U.random(200.), true, false, false));
       }
      }
      Fire.load(s);
      Boulder.load(s);
      Tsunami.load(s);
      Volcano.load(s);
      Meteor.load(s);
      if (s.startsWith("music(")) {
       Music.load(U.getString(s, 0));
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
     if (status == VE.Status.mapLoadPass3) {
      TE.randomX = s.startsWith("randomX(") ? U.getValue(s, 0) : TE.randomX;
      TE.randomY = s.startsWith("randomY(") ? U.getValue(s, 0) : TE.randomY;
      TE.randomZ = s.startsWith("randomZ(") ? U.getValue(s, 0) : TE.randomZ;
      if (U.startsWith(s, "(", "strip(", "curve(")) {
       int trackNumber = TE.getTrackPartIndex(U.getString(s, 0));//<-Returns '-1' on exception
       if (trackNumber < 0 && !U.getString(s, 0).isEmpty()) {
        System.out.println("Map Part List Exception (" + name + ")");
        System.out.println("At line: " + s);
       }
       long[] random = {Math.round(U.randomPlusMinus(TE.randomX)), Math.round(U.randomPlusMinus(TE.randomY)), Math.round(U.randomPlusMinus(TE.randomZ))};
       if (trackNumber == TE.getTrackPartIndex(TE.Models.checkpoint.name())) {
        long cornerDisplace = name.equals("Death Pit") ? 12000 : name.equals("Arctic Slip") ? 4500 : 0;
        random[0] += U.random() < .5 ? cornerDisplace : -cornerDisplace;
        random[2] += U.random() < .5 ? cornerDisplace : -cornerDisplace;
       }
       if (U.equals(name, "Columns Condemn")) {
        random[0] *= 5000;
        random[2] *= 5000;
        while (random[0] > 47500) random[0] -= 25000;
        while (random[0] < -47500) random[0] += 25000;
        while (random[2] > 47500) random[2] -= 25000;
        while (random[2] < -47500) random[2] += 25000;
       } else if (U.equals(name, "the Linear Accelerator")) {
        random[0] *= 1000;
        random[2] *= 1000;
       } else if (name.equals("Phantom Cavern")) {
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
       if (s.startsWith("strip(")) {
        double partAngle = Double.NaN;
        try {
         partAngle = U.getValue(s, 7);
        } catch (RuntimeException ignored) {
        }
        double stripEnd = U.getValue(s, 4),
        advanceDistance = U.getValue(s, 5),
        advanceAngle = U.getValue(s, 6);
        for (double iteration = 0; iteration < stripEnd; iteration++) {
         TE.addTrackPart(s, trackNumber,
         summedPositionX + (advanceDistance * iteration * U.sin(advanceAngle)),
         summedPositionY,
         summedPositionZ + (advanceDistance * iteration * U.cos(advanceAngle)),
         Double.isNaN(partAngle) ? -advanceAngle : partAngle);
        }
       } else if (s.startsWith("curve(")) {
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
      } else if (s.startsWith("vehicleModel")) {
       TE.trackParts.add(new TrackPart(getVehicleIndex(U.getString(s, 0)), U.getValue(s, 1), U.getValue(s, 3), U.getValue(s, 2), U.getValue(s, 4), true));
      }
     }
    }
   } catch (Exception E) {//<-Don't further specify
    status = VE.Status.mapError;
    System.out.println("Map Error (" + name + ")");
    System.out.println("At line: " + s);
    E.printStackTrace();
   }
   if (status == VE.Status.mapLoadPass3) {
    if (name.equals(SL.MN.ghostCity)) {
     for (n = 5; n < 365; n += 10) {
      TE.instanceSize = 2500 + U.random(2500.);
      TE.instanceScale[0] = 1 + U.random(2.);
      TE.instanceScale[1] = 1 + U.random(2.);
      TE.instanceScale[2] = 1 + U.random(2.);
      double calculatedX = 112500 * -StrictMath.sin(Math.toRadians(n)),
      calculatedZ = 112500 * StrictMath.cos(Math.toRadians(n));
      TE.trackParts.add(new TrackPart(TE.getTrackPartIndex(TE.Models.cube.name()), calculatedX, 0, calculatedZ, n - 90, TE.instanceSize, TE.instanceScale));
     }
    } else if (name.equals("World's Biggest Parking Lot")) {
     for (n = 0; n < vehicleModels.size(); n++) {
      double xRandom = U.randomPlusMinus(30000.), zRandom = U.randomPlusMinus(30000.), randomXZ = U.randomPlusMinus(180.);
      userRandomRGB = U.getColor(U.random(), U.random(), U.random());
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
    if (E.Pool.type == E.Pool.Type.lava) {
     for (Tsunami.Part tsunamiPart : Tsunami.parts) {//Setting the illumination here in case the lava pool gets called AFTER tsunami definition
      U.Phong.setSelfIllumination((PhongMaterial) tsunamiPart.C.getMaterial(), E.lavaSelfIllumination[0], E.lavaSelfIllumination[1], E.lavaSelfIllumination[2]);
     }
    }
    TE.Bonus.X = TE.Bonus.Y = TE.Bonus.Z = 0;
    U.Nodes.add(TE.Bonus.big);
    for (TE.Bonus.Ball bonusBall : TE.Bonus.balls) {
     U.Nodes.add(bonusBall);
    }
   } else if (status == VE.Status.mapLoadPass4) {
    if (!Viewer.inUse) {
     for (n = vehiclesInMatch; --n >= 0; ) {
      vehicles.add(null);
     }
     vehicles.set(userPlayerIndex, new Vehicle(vehicleNumber[userPlayerIndex], userPlayerIndex, true));
     for (n = vehiclesInMatch; --n >= 0; ) {
      if (n != userPlayerIndex) {//<-User player set first for Linux sound optimization
       vehicles.set(n, new Vehicle(vehicleNumber[n], n, true));
      }
     }
     for (Vehicle vehicle : vehicles) {
      vehicle.addTransparents();
     }
    }
    reset();
   }
   String loadText = status == VE.Status.mapLoadPass0 ? "Removing Previous Content" : status == VE.Status.mapLoadPass1 ? "Loading Properties & Scenery" : status == VE.Status.mapLoadPass2 ? "Adding Track Parts" : "Adding " + vehiclesInMatch + " Vehicle(s)";
   U.fillRGB(0, 0, 0);
   U.fillRectangle(.5, .5, 1, 1);
   U.font(.025);
   U.fillRGB(1, 1, 1);
   U.text(Tournament.stage > 0 ? "Round " + Tournament.stage + (Tournament.stage > 5 ? "--Overtime!" : "") : "", .425);
   U.text(name, .475);
   U.text(".." + loadText + "..", .525);
   if (status != VE.Status.mapError) {
    status =
    status == VE.Status.mapLoadPass0 ? VE.Status.mapLoadPass1 :
    status == VE.Status.mapLoadPass1 ? VE.Status.mapLoadPass2 :
    status == VE.Status.mapLoadPass2 ? VE.Status.mapLoadPass3 :
    status == VE.Status.mapLoadPass3 ? VE.Status.mapLoadPass4 :
    (Viewer.inUse ? VE.Status.mapViewer : Network.mode == Network.Mode.JOIN ? VE.Status.play : VE.Status.mapView);
   }
   E.renderType = E.RenderType.ALL;
  }

  static int getMapName(String s) {
   int n;
   String s1;
   for (n = 0; n < maps.size(); n++) {
    try (BufferedReader BR = new BufferedReader(new InputStreamReader(Objects.requireNonNull(U.getMapFile(n)), U.standardChars))) {
     for (String s2; (s2 = BR.readLine()) != null; ) {
      s1 = s2.trim();
      name = s1.startsWith("name") ? U.getString(s1, 0) : name;
     }
    } catch (IOException e) {
     status = VE.Status.mapError;
     e.printStackTrace();
    }
    if (s.equals(name)) {
     break;
    }
   }
   return n;
  }

  private static void runQuickSelect(boolean gamePlay) {
   if (Network.mode == Network.Mode.JOIN) {
    U.font(.03);
    U.text("..Please Wait for " + playerNames[0] + " to Select Map..", .5, .5);
   } else {
    String mapMaker;
    name = mapMaker = "";
    map = Tournament.stage > 0 ? U.random(maps.size()) : map;
    String s;
    try (BufferedReader BR = new BufferedReader(new InputStreamReader(Objects.requireNonNull(U.getMapFile(map)), U.standardChars))) {
     for (String s1; (s1 = BR.readLine()) != null; ) {
      s = s1.trim();
      name = s.startsWith("name") ? U.getString(s, 0) : name;
      mapMaker = s.startsWith("maker") ? U.getString(s, 0) : mapMaker;
     }
    } catch (IOException e) {
     status = VE.Status.mapError;
     e.printStackTrace();
    }
    if (Tournament.stage > 0) {
     status = VE.Status.mapLoadPass0;
    } else {
     U.fillRGB(0, 0, 0, UI.drawOpacity.maximal);
     U.fillRectangle(.5, .5, 1, 1);
     U.fillRGB(1, 1, 1);
     U.font(.05);
     U.text(name, .5);
     U.font(.03);
     U.text(Viewer.inUse ? "SELECT MAP TO EDIT:" : "SELECT MAP:", .25);
     U.text("<-LAST", .125, .75);
     U.text("NEXT->", .875, .75);
     U.text("CONTINUE", .875);
     U.font(.02);
     U.text("Made by " + mapMaker, .6);
     U.font(.01);
     U.text("You can also use the Arrow Keys and Enter to navigate.", .95);
     if (UI.selectionTimer > UI.selectionWait) {
      if (Keys.Right) {
       map = ++map >= maps.size() ? 0 : map;
       Sounds.UI.play(0, 0);
      }
      if (Keys.Left) {
       map = --map < 0 ? maps.size() - 1 : map;
       Sounds.UI.play(0, 0);
      }
      if (Keys.Enter || Keys.Space) {
       status = VE.Status.mapLoadPass0;
       Sounds.UI.play(1, 0);
      }
     }
     gameFPS = U.refreshRate * .5;
    }
   }
   Network.preMatchCommunication(gamePlay);
   if (Keys.Escape) {
    escapeToLast(true);
   }
  }

  private static void runView(boolean gamePlay) {
   Camera.runAroundTrack();
   U.rotate(Camera.camera, Camera.YZ, -Camera.XZ);
   E.run(gamePlay);
   for (Vehicle vehicle : vehicles) {
    vehicle.runGraphics(gamePlay);
   }
   boolean renderALL = E.renderType == E.RenderType.ALL;
   for (TrackPart trackPart : TE.trackParts) {
    trackPart.runGraphics(renderALL);
   }
   scene.setCursor(Cursor.CROSSHAIR);
   U.font(.015);
   U.fillRGB(E.Ground.RGB.invert());
   U.text("<-LAST", .2, .75);
   U.text("NEXT->", .8, .75);
   U.text("CONTINUE", .75);
   U.font(.01);
   U.text("You can also use the Arrow Keys and Enter to navigate.", .95);
   U.fillRGB(E.skyRGB.invert());
   U.font(.02);
   U.text("| " + name + " |", .15);
   Network.preMatchCommunication(gamePlay);
   if (Keys.Space || Keys.Enter || Tournament.stage > 0) {
    status = VE.Status.play;
    if (Tournament.stage < 1) {
     Sounds.UI.play(1, 0);
    }
    Camera.view = Camera.View.flow;
    Keys.Space = Keys.Enter = false;
   } else if (Keys.Right || Keys.Left) {
    if (Keys.Left) {
     map = --map < 0 ? maps.size() - 1 : map;
    }
    if (Keys.Right) {
     map = ++map >= maps.size() ? 0 : map;
    }
    status = VE.Status.mapJump;
    Sounds.UI.play(0, 0);
    for (Vehicle vehicle : vehicles) {
     vehicle.closeSounds();
    }
   }
   if (Keys.Escape) {
    escapeToLast(true);
   }
   TE.Bonus.run();
   gameFPS = Double.POSITIVE_INFINITY;
   E.renderType = E.RenderType.standard;
  }

  private static void runErrred() {
   scene.setCursor(Cursor.CROSSHAIR);
   U.fillRGB(0, 0, 0, UI.drawOpacity.maximal);
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
   if (Keys.Space || Keys.Enter) {
    status = VE.Status.mapLoadPass0;
    Keys.Space = Keys.Enter = false;
    Sounds.UI.play(1, 0);
   }
   if (Keys.Right) {
    map = ++map >= maps.size() ? 0 : map;
    status = VE.Status.mapJump;
    Keys.Right = false;
    Sounds.UI.play(0, 0);
   }
   if (Keys.Left) {
    map = --map < 0 ? maps.size() - 1 : map;
    status = VE.Status.mapJump;
    Keys.Left = false;
    Sounds.UI.play(0, 0);
   }
   gameFPS = U.refreshRate * .25;
   if (Keys.Escape) {
    escapeToLast(true);
   }
  }
 }

 private enum Tournament {
  ;
  private static long stage;
  private static final long[] wins = new long[2];
  private static boolean finished;
 }

 public enum Sounds {
  ;
  static Sound UI;
  public static Sound checkpoint;
  private static Sound stunt, finish;

  private static void clear() {//Not all sounds close
   for (Vehicle vehicle : vehicles) {
    vehicle.closeSounds();
   }
   E.Storm.rain.stop();
   Tornado.sound.stop();
   Tsunami.sound.stop();
   for (Fire.Instance fire : Fire.instances) {
    fire.closeSound();
   }
   for (Boulder.Instance boulder : Boulder.instances) {
    boulder.sound.close();
   }
   for (Meteor.Instance meteor : Meteor.instances) {
    meteor.sound.close();
   }
   if (E.Storm.thunder != null) E.Storm.thunder.close();
   if (E.Wind.storm != null) E.Wind.storm.close();
  }
 }

 public enum Music {
  ;
  public static double gain = -6.020599913;
  private static Player jLayer;
  private static String name = "";

  private static void load(String s) {
   if (!name.equals(s)) {
    name = s != null ? s : name;
    Thread thread = new Thread(() -> {
     if (jLayer != null) {
      jLayer.close();
     }
     try {
      jLayer = new Player(new FileInputStream(new File("music" + File.separator + name + ".mp3")));
     } catch (Exception E) {//<-do NOT change
      System.out.println("Problem loading Music: " + E);
     }
     if (jLayer != null) {
      try {
       jLayer.play();
      } catch (JavaLayerException ignored) {
      }
     }
    });
    thread.setDaemon(true);
    thread.start();
   }
  }
 }

 static void run(String[] s) {//<-Will NOT work if moved to Launcher
  launch(s);
 }

 private static void loadVE(Stage stage) {
  Thread loadVE = new Thread(() -> {
   try {
    int n;
    scene3D.setFill(Color.color(0, 0, 0));
    TE.Arrow.scene.setFill(Color.color(0, 0, 0, 0));
    initialization = "Loading Images";
    U.Images.load(images, "RA");
    U.Images.load(images, SL.Images.white);
    U.Images.load(images, SL.Images.fireLight, 3);
    U.Images.load(images, "blueJet", 3);
    U.Images.load(images, SL.Instance.blink, 3);
    initialization = "Loading Textures";
    U.Images.load(images, "water");
    U.Images.load(images, "rock");
    U.Images.load(images, "metal");
    U.Images.load(images, "brightmetal");
    U.Images.load(images, "grid");
    U.Images.load(images, SL.Images.paved);
    U.Images.load(images, "wood");
    U.Images.load(images, "foliage");
    U.Images.load(images, "cactus");
    U.Images.load(images, "grass");
    U.Images.load(images, "sand");
    U.Images.load(images, "ground1");
    U.Images.load(images, "ground2");
    initialization = "Loading Normal Maps";
    U.Images.load(images, "rockN");
    U.Images.load(images, "metalN");
    U.Images.load(images, "brightmetalN");
    U.Images.load(images, "pavedN");
    U.Images.load(images, "woodN");
    U.Images.load(images, "foliageN");
    U.Images.load(images, "cactusN");
    U.Images.load(images, "grassN");
    U.Images.load(images, "sandN");
    U.Images.load(images, "ground1N");
    U.Images.load(images, "ground2N");
    initialization = "Loading Settings";
    String s;
    try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream(SL.gameSettings), U.standardChars))) {
     for (String s1; (s1 = BR.readLine()) != null; ) {
      s = s1.trim();
      if (s.startsWith("Units(metric")) {
       Options.units = .5364466667;
       unitName[0] = "Kph";
       unitName[1] = "Meters";
      } else if (s.startsWith("Units(U.S.")) {
       Options.units = 1 / 3.;
       unitName[0] = "Mph";
       unitName[1] = "Feet";
      }
      Options.normalMapping = s.startsWith("NormalMapping(yes") || Options.normalMapping;
      Camera.shake = s.startsWith("CameraShake(yes") || Camera.shake;
      try {
       userFPS = s.startsWith("fpsLimit(") ? Math.round(U.getValue(s, 0)) : userFPS;
      } catch (RuntimeException ignored) {
      }
      Options.degradedSoundEffects = s.startsWith("DegradedSoundEffects(yes") || Options.degradedSoundEffects;
      Options.matchLength = s.startsWith("MatchLength(") ? Math.round(U.getValue(s, 0)) : Options.matchLength;
      Options.driverSeat = s.startsWith("DriverSeat(left") ? -1 : s.startsWith("DriverSeat(right") ? 1 : Options.driverSeat;
      vehiclesInMatch = s.startsWith("#ofPlayers(") ? Math.max(1, Math.min((int) Math.round(U.getValue(s, 0)), maxPlayers)) : vehiclesInMatch;
      Options.headsUpDisplay = s.startsWith("HUD(on") || Options.headsUpDisplay;
      Options.showInfo = s.startsWith("ShowInfo(yes") || Options.showInfo;
      VS.showModel = s.startsWith("ShowVehiclesInVehicleSelect(yes") || VS.showModel;
      Network.userName = s.startsWith(SL.Network.findUserName) ? U.getString(s, 0) : Network.userName;
      Network.targetHost = s.startsWith(SL.Network.findTargetHost) ? U.getString(s, 0) : Network.targetHost;
      Network.port = s.startsWith("Port(") ? (int) Math.round(U.getValue(s, 0)) : Network.port;
      if (s.startsWith(SL.findGameVehicles)) {
       vehicleModels = new ArrayList<>(Arrays.asList(s.substring(SL.findGameVehicles.length(), s.length() - 1).split(",")));
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
    Sounds.checkpoint = new Sound("checkpoint");
    Sounds.stunt = new Sound("stunt");
    TE.Bonus.sound = new Sound("bonus");
    E.Storm.rain = new Sound("rain");
    Tornado.sound = new Sound("tornado");
    Tsunami.sound = new Sound("tsunami");
    Volcano.sound = new Sound("volcano");
    Sounds.UI = new Sound("UI", 2);
    Sounds.finish = new Sound("finish", 2);
    stage.setOnCloseRequest((WindowEvent WE) -> {
     for (PrintWriter PW : Network.out) {
      PW.println("END");
      PW.println("END");
      PW.println(SL.Network.cancel);
      PW.println(SL.Network.cancel);
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
  primaryStage.setTitle("The Vehicular Epic");
  try {
   primaryStage.getIcons().add(new Image(new FileInputStream(U.imageFolder + File.separator + "icon.png")));
  } catch (FileNotFoundException ignored) {
  }
  System.setProperty("sun.java2d.opengl", "true");//<-Is this even necessary?
  double windowSize = 1;
  boolean antiAliasing = false;
  String s;
  try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream(SL.gameSettings), U.standardChars))) {
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
  U.Nodes.Light.add(E.Sun.light);
  new innerAnimationTimer(primaryStage).start();
  loadVE(primaryStage);
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
    UI.selectionTimer += tick;
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
     for (Vehicle vehicle : vehicles) {
      vehicle.miscellaneous(gamePlay);
     }
     //*These are SPLIT so that energy towers can empower specials before the affected vehicles fire, and to make shots render correctly
     for (Vehicle vehicle : vehicles) {
      for (Special special : vehicle.specials) {
       if (special.type == Special.Type.energy) {
        special.run(gamePlay);//*
       }
      }
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
       double color = yinYang ? 0 : 1;
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
          PW.println("Ready0");
          PW.println("Ready0");
         }
        } else {
         Network.out.get(0).println(SL.Network.ready);
         Network.out.get(0).println(SL.Network.ready);
        }
       }
       Keys.Space = false;
      }
      if (!Network.waiting) {
       U.font(.02);
       double color = yinYang ? 0 : 1;
       U.fillRGB(color, color, color);
       if (vehicles.get(vehiclePerspective).isFixed() && (vehiclesInMatch < 2 || vehiclePerspective < vehiclesInMatch >> 1)) {
        U.text("Use Arrow Keys to place your turret(s)/infrastructure, then", .2);
        if (Keys.Up || Keys.Down || Keys.Left || Keys.Right) {
         UI.movementSpeedMultiple = Math.max(10, UI.movementSpeedMultiple * 1.05);
         vehicles.get(vehiclePerspective).Z += Keys.Up ? UI.movementSpeedMultiple * tick : 0;
         vehicles.get(vehiclePerspective).Z -= Keys.Down ? UI.movementSpeedMultiple * tick : 0;
         vehicles.get(vehiclePerspective).X -= Keys.Left ? UI.movementSpeedMultiple * tick : 0;
         vehicles.get(vehiclePerspective).X += Keys.Right ? UI.movementSpeedMultiple * tick : 0;
        } else {
         UI.movementSpeedMultiple = 0;
        }
       }
       U.text("Press SPACE to Begin" + (Tournament.stage > 0 ? " Round " + Tournament.stage : ""), .25);
      }
      if (Keys.Escape) {
       escapeToLast(true);
      }
     }
     Recorder.playBack();
     //rendering begins here
     Camera.run(vehicles.get(vehiclePerspective), gamePlay);
     U.rotate(Camera.camera, Camera.YZ, -Camera.XZ);
     Camera.rotateXY.setAngle(-Camera.XY);
     E.run(gamePlay);
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
      trackPart.runGraphics(renderALL);
     }
     TE.Bonus.run();
     Match.runUI(gamePlay);
     vehiclePerspective = Camera.toUserPerspective[0] && Camera.toUserPerspective[1] ? userPlayerIndex : vehiclePerspective;
     gameFPS = Double.POSITIVE_INFINITY;
     E.renderType = E.RenderType.standard;
    }
    E.Pool.runVision();
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
     Map.runErrred();
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
    UI.selectionTimer = (UI.selectionTimer > UI.selectionWait ? 0 : UI.selectionTimer) + 5 * tick;
    if (Keys.Left || Keys.Right || Keys.Up || Keys.Down || Keys.Space || Keys.Enter) {
     if (UI.selectionWait == -1) {
      UI.selectionWait = 30;
      UI.selectionTimer = 0;
     }
     UI.selectionWait -= UI.selectionWait > 0 ? tick : 0;
    } else {
     UI.selectionWait = -1;
     UI.selectionTimer = 0;
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
     U.fillRGB(0, 0, 0, UI.drawOpacity.minimal);
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
  status = VE.Status.mainMenu;
  Tournament.stage = UI.selected = 0;
  resetGraphics();
  Sounds.clear();
  Keys.falsify();
  if (Network.mode == Network.Mode.HOST) {
   for (PrintWriter PW : Network.out) {
    PW.println(SL.Network.cancel);
    PW.println(SL.Network.cancel);
   }
  } else if (Network.mode == Network.Mode.JOIN) {
   Network.out.get(0).println(SL.Network.cancel);
   Network.out.get(0).println(SL.Network.cancel);
  }
 }

 enum VS {//VehicleSelect
  ;
  static int index;
  private static boolean allSame, showModel;

  private static void run(boolean gamePlay) {
   int n;
   U.font(.03);
   if (Network.waiting) {
    U.fillRGB(1);
    if (vehiclesInMatch < 3) {
     U.text("..Please Wait for " + playerNames[Network.mode == Network.Mode.HOST ? 1 : 0] + " to Select Vehicle..", .5, .5);
    } else {
     U.text("..Please Wait for all other players to Select their Vehicle..", .5, .5);
    }
    if (Network.mode == Network.Mode.HOST) {
     if (timerBase20 <= 0) {
      for (PrintWriter PW : Network.out) {
       PW.println("Vehicle0(" + vehicles.get(0).name);
      }
     }
     for (n = vehiclesInMatch; --n > 0; ) {
      String s = Network.readIn(n - 1);
      if (s.startsWith(SL.Network.cancel)) {
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
     if (timerBase20 <= 0) {
      Network.out.get(0).println("Vehicle(" + vehicles.get(0).name);
     }
     String s = Network.readIn(0);
     if (s.startsWith(SL.Network.cancel)) {
      escapeToLast(false);
     } else {
      for (n = vehiclesInMatch; --n >= 0; ) {
       if (n != userPlayerIndex) {
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
     status = VE.Status.mapJump;
     Network.waiting = false;
    }
   } else {
    if (UI.page == 0) {
     resetGraphics();
     vehicles.clear();
     scene3D.setFill(Color.color(0, 0, 0));
     Camera.X = Camera.Z = Camera.YZ = Camera.XY = 0;
     Camera.Y = -250;
     Camera.XZ = 180;
     Camera.rotateXY.setAngle(0);
     Camera.setAngleTable();
     U.setTranslate(E.Ground.C, 0, 0, 0);
     U.Phong.setDiffuseRGB((PhongMaterial) E.Ground.C.getMaterial(), .1);
     for (Raindrop.Instance raindrop : Raindrop.instances) {
      U.Nodes.add(raindrop.C);
     }
     for (Snowball.Instance snowball : Snowball.instances) {
      U.Nodes.add(snowball.round, snowball.lowResolution);
     }
     addVehicleModel(vehicleNumber[index], showModel);
     allSame = false;
     UI.page = 1;
    }
    allSame = index <= 0 && Network.mode == Network.Mode.OFF && allSame;
    Vehicle V = vehicles.get(0);
    U.fillRGB(1, 1, 1);
    U.text("SELECT " + (Viewer.inUse ? "VEHICLE TO EDIT" : index > 0 ? "PLAYER #" + index : "VEHICLE"), .075);
    V.inDriverView = false;
    V.runGraphics(gamePlay);
    V.Z = -1000;
    V.XZ += (.5 - Mouse.X) * 20 * tick;
    if (V.spinner != null) {
     V.spinner.XZ = -V.XZ * 2;
    }
    if (V.isFixed()) {
     V.Y = -V.turretBaseY;
     V.YZ -= (.5 - Mouse.Y) * 20 * tick;
     V.YZ = U.clamp(-90, V.YZ, 90);
    } else {
     V.Y = -V.clearanceY;
    }
    U.font(.0125);
    if (vehiclesInMatch > 2) {
     if (index < vehiclesInMatch >> 1) {
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
    if (showModel) {
     U.text("Meshes: " + V.parts.size(), .8);
     U.text("Vertices: " + V.vertexQuantity, .825);
    }
    U.text("Vehicles [" + (showModel ? "SHOW (can be slow--not recommended)" : "HIDE") + "]", .875);
    U.text("CONTINUE" + (allSame ? " (with all players as " + V.name + ")" : ""), .9);
    boolean singleSelection = !Viewer.inUse && (vehiclesInMatch < 2 || Network.mode != Network.Mode.OFF);
    if (singleSelection) {
     UI.selected = Math.min(1, UI.selected);
    } else {
     U.text(Viewer.inUse ? "START .OBJ-to-V.E. CONVERTER" : "SELECT NEXT VEHICLE", .925);
    }
    if (yinYang) {
     U.strokeRGB(1, 1, 1);
     U.drawRectangle(.5, UI.selected == 0 ? .875 : UI.selected == 1 ? .9 : .925, width, UI.selectionHeight);
    }
    U.fillRGB(1, 1, 1);
    if (UI.selected == 1 && !singleSelection && !Viewer.inUse) {
     U.text(allSame ? "" : "(Remaining players are picked randomly)", .95);
    }
    if (showModel) {
     U.font(.02);
     U.text(V.name, .15);
    } else {
     U.font(.03);
     U.text(V.name, .5);
    }
    U.font(.015);
    U.text("Made by " + vehicleMaker, .2);
    double lineLL = .1125, lineLR = .125, lineRL = 1 - lineLR, lineRR = 1 - lineLL,
    Y0 = .725, Y1 = .75, Y2 = .775, Y3 = .8, Y4 = .825, Y5 = .85;
    U.font(.00875);
    U.textR("Type: ", lineLL, Y0);
    String type =
    V.type == Vehicle.Type.aircraft ? "Aircraft (Flying)" :
    V.type == Vehicle.Type.turret ? "Turret (Fixed)" :
    V.type == Vehicle.Type.supportInfrastructure ? "Support Infrastructure (Fixed)"
    : "Vehicle (Grounded)";
    U.textL(type, lineLR, Y0);
    U.textR("Top Speed:", lineLL, Y1);
    U.textL(
    V.isFixed() ? "N/A" :
    V.topSpeeds[1] >= Long.MAX_VALUE ? "None" :
    V.speedBoost > 0 && V.topSpeeds[2] >= Long.MAX_VALUE ? "None (Speed Boost)" :
    V.speedBoost > 0 ? Math.round(V.topSpeeds[2] * Options.units) + " " + unitName[0] + " (Speed Boost)" :
    Math.round(V.topSpeeds[1] * Options.units) + " " + unitName[0], lineLR, Y1);
    U.textR("Acceleration Phases:", lineLL, Y2);
    U.textL(V.isFixed() ? "N/A" : "+" + V.accelerationStages[0] + ",  +" + V.accelerationStages[1], lineLR, Y2);
    U.textR("Handling Response:", lineLL, Y3);
    U.textL(V.turnRate == Double.POSITIVE_INFINITY ? "Instant" : String.valueOf(V.turnRate), lineLR, Y3);
    U.textR("Stunt Response:", lineLL, Y4);
    U.textL(V.type == Vehicle.Type.vehicle ? String.valueOf(V.airAcceleration == Double.POSITIVE_INFINITY ? "Instant" : (float) V.airAcceleration) : "N/A", lineLR, Y4);
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
    String damageDealt =
    (V.type != Vehicle.Type.aircraft && V.damageDealt[U.random(4)] >= 100) || V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name()) ? "Instant-Kill" :
    hasForceField || V.spinner != null ? "'Inconsistent'" :
    String.valueOf((float) ((V.damageDealt[0] + V.damageDealt[1] + V.damageDealt[2] + V.damageDealt[3]) * .25));
    U.textL(damageDealt, lineRR, Y0);
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
    if (UI.selectionTimer > UI.selectionWait) {
     if (Keys.Up || Keys.Down) {
      if (Keys.Down) {
       UI.selected = ++UI.selected > (singleSelection ? 1 : 2) ? 0 : UI.selected;
      } else {
       UI.selected = --UI.selected < 0 ? (singleSelection ? 1 : 2) : UI.selected;
      }
      Sounds.UI.play(0, 0);
      Keys.inUse = true;
     }
     if (Keys.Right) {
      removeVehicleModel();
      vehicleNumber[index] = ++vehicleNumber[index] >= vehicleModels.size() ? 0 : vehicleNumber[index];
      if (index == userPlayerIndex) {
       userRandomRGB = U.getColor(U.random(), U.random(), U.random());
      }
      addVehicleModel(vehicleNumber[index], showModel);
      Sounds.UI.play(0, 0);
     }
     if (Keys.Left) {
      removeVehicleModel();
      vehicleNumber[index] = --vehicleNumber[index] < 0 ? vehicleModels.size() - 1 : vehicleNumber[index];
      if (index == userPlayerIndex) {
       userRandomRGB = U.getColor(U.random(), U.random(), U.random());
      }
      addVehicleModel(vehicleNumber[index], showModel);
      Sounds.UI.play(0, 0);
     }
     if (Keys.Space || Keys.Enter) {
      if (Viewer.inUse && UI.selected == 2) {
       new MainFrame().setVisible(true);
      } else {
       removeVehicleModel();
       if (UI.selected < 1) {
        showModel = !showModel;
        addVehicleModel(vehicleNumber[index], showModel);
       } else {
        if (Network.mode == Network.Mode.OFF) {
         index++;
         if (index < vehiclesInMatch) {
          addVehicleModel(vehicleNumber[index], showModel);
         }
        }
        if (index > (vehiclesInMatch * (Tournament.stage > 0 ? .5 : 1)) - 1 || UI.selected == 1) {
         if (Viewer.inUse) {
          status = VE.Status.vehicleViewer;
          UI.page = 0;
         } else if (Network.mode != Network.Mode.OFF) {
          Network.ready[userPlayerIndex] = Network.waiting = true;
         } else {
          status = VE.Status.mapJump;
          if (allSame) {
           for (n = vehicleNumber.length; --n > 0; ) {
            vehicleNumber[n] = vehicleNumber[0];
           }
          } else {
           for (n = index; n < vehiclesInMatch; n++) {
            vehicleNumber[n] = U.random(vehicleModels.size());
           }
          }
         }
        }
       }
      }
      Sounds.UI.play(1, 0);
      Keys.Space = Keys.Enter = false;
     }
    }
   }
   if (Keys.Escape) {
    escapeToLast(true);
   }
   if (!Keys.inUse) {
    UI.selected =
    Math.abs(.85 - Mouse.Y) < UI.clickRangeY ? 0 :
    Math.abs(.875 - Mouse.Y) < UI.clickRangeY ? 1 :
    Math.abs(.9 - Mouse.Y) < UI.clickRangeY ? 2 :
    UI.selected;
   }
   U.rotate(Camera.camera, Camera.YZ, -Camera.XZ);
   Raindrop.run();
   Snowball.run();
   gameFPS = Double.POSITIVE_INFINITY;
  }
 }

 private static void addVehicleModel(int v, boolean show) {
  vehicles.clear();
  vehicles.add(new Vehicle(v, 0, false, show));
  vehicles.get(0).lightBrightness = Map.defaultVehicleLightBrightness;
  for (VehiclePart part : vehicles.get(0).parts) {
   U.Nodes.add(part.MV);
   part.MV.setVisible(true);
   part.setDrawMode(Viewer.Vehicle.showWireframe);
  }
 }

 private static void removeVehicleModel() {
  if (!vehicles.isEmpty() && vehicles.get(0) != null) {
   for (VehiclePart part : vehicles.get(0).parts) {
    U.Nodes.remove(part.MV);
    U.Nodes.Light.remove(part.pointLight);
   }
  }
 }

 enum Viewer {
  ;
  private static boolean inUse;
  private static double Y, Z, XZ, YZ, height, depth;

  enum Vehicle {
   ;
   private static boolean lighting3D, showCollisionBounds, showWireframe;
   private static final Sphere collisionBounds = new Sphere();

   static {
    PhongMaterial boundSpherePM = new PhongMaterial();
    U.setMaterialSecurely(collisionBounds, boundSpherePM);
    U.Phong.setDiffuseRGB(boundSpherePM, 1, 1, 1, .5);
    U.Phong.setSpecularRGB(boundSpherePM, 0);
   }

   private static void run(boolean gamePlay) {
    U.font(.03);
    U.fillRGB(1, 1, 1);
    U.text("Vehicle Viewer", .075);
    boolean loadModel = false;
    if (UI.page < 1) {
     resetGraphics();
     U.Nodes.remove(E.Sun.S, E.Ground.C);
     U.Nodes.Light.remove(E.Sun.light);
     scene3D.setFill(Color.color(0, 0, 0));
     U.Nodes.Light.setRGB(E.Sun.light, 1, 1, 1);
     Camera.X = Camera.Y = Camera.Z = Camera.XZ = Camera.YZ = Camera.XY = Y = YZ = 0;
     E.viewableMapDistance = Double.POSITIVE_INFINITY;
     U.rotate(Camera.camera, Camera.YZ, -Camera.XZ);
     Camera.rotateXY.setAngle(0);
     Camera.setAngleTable();
     Z = 1000;
     XZ = 180;
     showCollisionBounds = false;//<-Covers vehicle otherwise
     loadModel = true;
     U.Nodes.Light.setRGB(E.ambientLight, 1, 1, 1);
     U.setTranslate(E.Sun.light, 0, -Long.MAX_VALUE, 0);
     if (lighting3D) {
      U.Nodes.Light.setRGB(E.ambientLight, .5, .5, .5);
      U.Nodes.Light.add(E.Sun.light);
     }
     UI.page = 1;
    }
    XZ += Keys.Left ? 5 : 0;
    XZ -= Keys.Right ? 5 : 0;
    YZ -= Keys.Up ? 5 : 0;
    YZ += Keys.Down ? 5 : 0;
    Y += height * tick;
    Z += depth * tick;
    if (vehicles.get(0) != null) {
     vehicles.get(0).Y = Y;
     vehicles.get(0).Z = Z;
     vehicles.get(0).XZ = XZ;
     vehicles.get(0).YZ = YZ;
     vehicles.get(0).thrusting = (timerBase20 <= 0) != vehicles.get(0).thrusting;
     vehicles.get(0).runGraphics(gamePlay);
     U.font(.02);
     U.text(vehicles.get(0).name, .1125);
     U.font(.0125);
     U.fillRGB(1, 1, 1);
     U.text("Meshes: " + vehicles.get(0).parts.size(), .25, .8);
     U.text("Vertices: " + vehicles.get(0).vertexQuantity, .75, .8);
     if (showCollisionBounds) {
      U.setTranslate(collisionBounds, vehicles.get(0));
      U.Nodes.add(collisionBounds);
     } else {
      U.Nodes.remove(collisionBounds);
     }
    }
    U.fillRGB(1, 1, 1);
    U.text("Move Vehicle with the T, G, U, and J Keys. Rotate with the Arrow Keys", .95 + UI.textOffset);
    if (yinYang) {
     U.strokeRGB(1, 1, 1);
     U.drawRectangle(.5, UI.selected == 0 ? .825 : UI.selected == 1 ? .85 : UI.selected == 2 ? .875 : UI.selected == 3 ? .9 : .925, width, UI.selectionHeight);
    }
    U.text("RE-LOAD VEHICLE FILE", .825 + UI.textOffset);
    U.text("3D Lighting [" + (lighting3D ? "ON" : "OFF") + "]", .85 + UI.textOffset);
    U.text("Draw Mode [" + (showWireframe ? "LINE" : "FILL") + "]", .875 + UI.textOffset);
    U.text("Collision Bounds [" + (showCollisionBounds ? "SHOW" : "HIDE") + "]", .9 + UI.textOffset);
    U.text("BACK TO MAIN MENU", .925 + UI.textOffset);
    if (UI.selectionTimer > UI.selectionWait) {
     if (Keys.Up) {
      UI.selected = --UI.selected < 0 ? 4 : UI.selected;
      Keys.inUse = true;
      Sounds.UI.play(0, 0);
     }
     if (Keys.Down) {
      UI.selected = ++UI.selected > 4 ? 0 : UI.selected;
      Keys.inUse = true;
      Sounds.UI.play(0, 0);
     }
    }
    if (Keys.Space || Keys.Enter) {
     if (UI.selected == 0) {
      loadModel = true;
     } else if (UI.selected == 1) {
      lighting3D = !lighting3D;
      if (lighting3D) {
       U.Nodes.Light.setRGB(E.ambientLight, .5, .5, .5);
       U.Nodes.Light.add(E.Sun.light);
      } else {
       U.Nodes.Light.setRGB(E.ambientLight, 1, 1, 1);
       U.Nodes.Light.remove(E.Sun.light);
      }
     } else if (UI.selected == 2) {
      showWireframe = !showWireframe;
      for (VehiclePart part : vehicles.get(0).parts) {
       part.setDrawMode(showWireframe);
      }
     } else if (UI.selected == 3) {
      showCollisionBounds = !showCollisionBounds;
      collisionBounds.setRadius(vehicles.get(0).collisionRadius());
     } else {
      status = VE.Status.mainMenu;
      removeVehicleModel();
     }
     Sounds.UI.play(1, 0);
     Keys.Space = Keys.Enter = false;
    }
    if (Keys.Escape) {
     status = VE.Status.mainMenu;
     removeVehicleModel();
     Sounds.UI.play(1, 0);
     Keys.Escape = false;
    }
    if (loadModel) {
     removeVehicleModel();
     userRandomRGB = U.getColor(U.random(), U.random(), U.random());
     addVehicleModel(vehicleNumber[0], true);
    }
    if (!Keys.inUse) {
     UI.selected =
     Math.abs(.8 - Mouse.Y) < UI.clickRangeY ? 0 :
     Math.abs(.825 - Mouse.Y) < UI.clickRangeY ? 1 :
     Math.abs(.85 - Mouse.Y) < UI.clickRangeY ? 2 :
     Math.abs(.875 - Mouse.Y) < UI.clickRangeY ? 3 :
     Math.abs(.9 - Mouse.Y) < UI.clickRangeY ? 4 :
     UI.selected;
    }
    gameFPS = Double.POSITIVE_INFINITY;
    E.renderType = E.RenderType.fullDistance;
   }
  }

  private static void runMapViewer(boolean gamePlay) {
   U.font(.03);
   U.fillRGB(1, 1, 1);
   U.text("Map Viewer", .075);
   if (UI.page < 1) {
    Camera.X = Camera.Z = Camera.XZ = Camera.YZ = Camera.XY = 0;
    Y = -5000;
    UI.page = 1;
   }
   Camera.XZ -= Keys.Left ? 5 : 0;
   Camera.XZ += Keys.Right ? 5 : 0;
   Camera.YZ += Keys.Up ? 5 : 0;
   Camera.YZ -= Keys.Down ? 5 : 0;
   Y += height * UI.movementSpeedMultiple * tick;
   Camera.Y = Y;
   Camera.Z += depth * U.cos(Camera.XZ) * UI.movementSpeedMultiple * tick;
   Camera.X += depth * U.sin(Camera.XZ) * UI.movementSpeedMultiple * tick;
   U.rotate(Camera.camera, Camera.YZ, -Camera.XZ);
   Camera.rotateXY.setAngle(-Camera.XY);
   Camera.setAngleTable();
   if (!E.lights.getChildren().contains(E.Sun.light)) {
    U.Nodes.Light.add(E.mapViewerLight);
    U.setTranslate(E.mapViewerLight, Camera.X, Camera.Y, Camera.Z);
   }
   E.run(gamePlay);
   boolean renderALL = E.renderType == E.RenderType.ALL;
   for (TrackPart trackPart : TE.trackParts) {
    trackPart.runGraphics(renderALL);
   }
   U.fillRGB(0, 0, 0, UI.drawOpacity.minimal);
   U.fillRectangle(.5, .9, 1, .2);
   U.font(.015);
   if (yinYang) {
    U.strokeRGB(1, 1, 1);
    U.drawRectangle(.5, UI.selected == 0 ? .85 : .875, width, UI.selectionHeight);
   }
   U.fillRGB(1);
   U.text("RE-LOAD MAP FILE", .85 + UI.textOffset);
   U.text("BACK TO MAIN MENU", .875 + UI.textOffset);
   U.text("Move Camera with the T, G, U, and J Keys. Rotate with the Arrow Keys", .9 + UI.textOffset);
   if (UI.selectionTimer > UI.selectionWait && (Keys.Up || Keys.Down)) {
    UI.selected = UI.selected < 1 ? 1 : 0;
    Keys.inUse = true;
    Sounds.UI.play(0, 0);
   }
   if (Keys.Space || Keys.Enter) {
    status = UI.selected == 0 ? VE.Status.mapLoadPass0 : VE.Status.mainMenu;
    Sounds.UI.play(1, 0);
    Keys.Space = Keys.Enter = false;
    Sounds.clear();
   }
   if (Keys.Escape) {
    status = VE.Status.mainMenu;
    Sounds.UI.play(1, 0);
    Keys.Escape = false;
    Sounds.clear();
   }
   TE.Bonus.run();
   if (!Keys.inUse) {
    UI.selected = Math.abs(.825 - Mouse.Y) < UI.clickRangeY ? 0 : Math.abs(.85 - Mouse.Y) < UI.clickRangeY ? 1 : UI.selected;
   }
   gameFPS = Double.POSITIVE_INFINITY;
   E.renderType = E.RenderType.fullDistance;
  }
 }

 static void escapeToLast(boolean wasUser) {
  if (Network.mode == Network.Mode.OFF) {
   status = VE.Status.mainMenu;
  } else {
   for (PrintWriter PW : Network.out) {
    PW.println(SL.Network.cancel);
    PW.println(SL.Network.cancel);
   }
   status = VE.Status.loadLAN;
  }
  Network.mode = Network.Mode.OFF;
  UI.page = Tournament.stage = 0;
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
   if (UI.selectionTimer > UI.selectionWait) {
    if (Keys.Up) {
     UI.selected = --UI.selected < 0 ? 6 : UI.selected;
     Keys.inUse = true;
     Sounds.UI.play(0, 0);
    }
    if (Keys.Down) {
     UI.selected = ++UI.selected > 6 ? 0 : UI.selected;
     Keys.inUse = true;
     Sounds.UI.play(0, 0);
    }
   }
   double C = UI.selected == 0 && yinYang ? 1 : 0;
   U.fillRGB(C, C, C);
   U.fillRectangle(.5, .6, .2, UI.selectionHeight);
   U.fillRGB(UI.selected == 1 && yinYang ? 1 : 0);
   U.fillRectangle(.5, .65, .2, UI.selectionHeight);
   U.fillRGB(0, UI.selected == 2 && yinYang ? 1 : 0, 0);
   U.fillRectangle(.5, .7, .2, UI.selectionHeight);
   U.fillRGB(UI.selected == 3 && yinYang ? 1 : 0, 0, 0);
   U.fillRectangle(.5, .75, .2, UI.selectionHeight);
   U.fillRGB(0, 0, UI.selected == 4 && yinYang ? 1 : 0);
   U.fillRectangle(.5, .8, .2, UI.selectionHeight);
   U.fillRGB(UI.selected == 5 && yinYang ? .5 : 0);
   U.fillRectangle(.5, .85, .2, UI.selectionHeight);
   U.fillRGB(UI.selected == 6 && yinYang ? .5 : 0);
   U.fillRectangle(.5, .9, .2, UI.selectionHeight);
   if (Keys.Enter || Keys.Space) {
    if (UI.selected == 0) {
     status = VE.Status.vehicleSelect;
     UI.selected = VS.index = 0;
    } else if (UI.selected == 1) {
     status = VE.Status.loadLAN;
     resetGraphics();
     UI.selected = 0;
    } else if (UI.selected == 2) {
     lastStatus = VE.Status.mainMenu;
     status = VE.Status.howToPlay;
    } else if (UI.selected == 3) {
     status = VE.Status.credits;
    } else if (UI.selected == 4) {
     status = VE.Status.optionsMenu;
     UI.selected = 0;
    } else if (UI.selected == 5) {
     status = VE.Status.vehicleSelect;
     vehiclesInMatch = 1;
     VS.index = 0;
     Viewer.inUse = true;
    } else if (UI.selected == 6) {
     status = VE.Status.mapJump;
     Viewer.inUse = true;
    }
    Sounds.UI.play(1, 0);
    UI.page = 0;
    Keys.Enter = Keys.Space = false;
    error = "";
   }
  }
  if (Keys.Escape) {
   System.exit(0);
  }
  U.font(.075);
  U.fillRGB(.5, .5, .5);
  U.text(SL.theVehicularEpic, .498, .173);
  U.text(SL.theVehicularEpic, .502, .173);
  U.text(SL.theVehicularEpic, .498, .177);
  U.text(SL.theVehicularEpic, .502, .177);
  U.fillRGB(1);
  U.text(SL.theVehicularEpic, .499, .174);
  U.text(SL.theVehicularEpic, .501, .174);
  U.text(SL.theVehicularEpic, .499, .176);
  U.text(SL.theVehicularEpic, .501, .176);
  U.fillRGB(.75, .75, .75);
  U.text(SL.theVehicularEpic, .175);
  U.font(.015);
  U.fillRGB(1);
  if (loaded) {
   U.text("NEW GAME", .6 + UI.textOffset);
   U.text("MULTIPLAYER GAME", .65 + UI.textOffset);
   U.text("HOW TO PLAY", .7 + UI.textOffset);
   U.text("CREDITS", .75 + UI.textOffset);
   U.text(SL.options, .8 + UI.textOffset);
   U.text("VEHICLE VIEWER", .85 + UI.textOffset);
   U.text("MAP VIEWER", .9 + UI.textOffset);
   if (!error.isEmpty()) {
    U.fillRGB(yinYang ? 1 : 0, 0, 0);
    U.text(error, .3);
   }
  } else {
   U.font(.025);
   U.text(yinYang ? ".. " + initialization + "   " : "   " + initialization + " ..", .5);
  }
  if (!Keys.inUse) {
   UI.selected =
   Math.abs(.6 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 0 :
   Math.abs(.65 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 1 :
   Math.abs(.7 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 2 :
   Math.abs(.75 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 3 :
   Math.abs(.8 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 4 :
   Math.abs(.85 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 5 :
   Math.abs(.9 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 6 :
   UI.selected;
  }
  gameFPS = U.refreshRate * .5;
 }

 private static void runPaused() {
  boolean ending = false;
  if (UI.selectionTimer > UI.selectionWait) {
   if (Keys.Up) {
    UI.selected = --UI.selected < 0 ? 4 : UI.selected;
    Keys.inUse = true;
    Sounds.UI.play(0, 0);
   }
   if (Keys.Down) {
    UI.selected = ++UI.selected > 4 ? 0 : UI.selected;
    Keys.inUse = true;
    Sounds.UI.play(0, 0);
   }
  }
  if (Keys.Enter || Keys.Space) {
   if (UI.selected == 0) {
    status = VE.Status.play;
   } else if (UI.selected == 1) {
    status = VE.Status.replay;
    Recorder.recordFrame = Recorder.gameFrame - (int) Recorder.recorded;
    while (Recorder.recordFrame < 0) Recorder.recordFrame += Recorder.totalFrames;
    Recorder.recordingsCount = 0;
   } else if (UI.selected == 2) {
    status = VE.Status.optionsMatch;
   } else if (UI.selected == 3) {
    lastStatus = VE.Status.paused;
    status = VE.Status.howToPlay;
   } else if (UI.selected == 4) {
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
     status = VE.Status.mapJump;
     Tournament.stage++;
    } else {
     status = VE.Status.mainMenu;
     Tournament.stage = 0;
    }
    Camera.lastView = Camera.view;
    UI.selected = 0;
    Sounds.clear();
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
     status = VE.Status.mainMenu;
     Camera.lastView = Camera.view;
     UI.selected = 0;
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
  if (UI.selected == 0) {
   U.fillRectangle(.5, .45, .2, UI.selectionHeight);
  } else if (UI.selected == 1) {
   U.fillRectangle(.5, .475, .2, UI.selectionHeight);
  } else if (UI.selected == 2) {
   U.fillRectangle(.5, .5, .2, UI.selectionHeight);
  } else if (UI.selected == 3) {
   U.fillRectangle(.5, .525, .2, UI.selectionHeight);
  } else if (UI.selected == 4) {
   U.fillRectangle(.5, .55, .2, UI.selectionHeight);
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
  U.text(SL.options, .5 + extraY);
  U.text("HOW TO PLAY", .525 + extraY);
  U.text(Tournament.stage > 0 ? (Match.timeLeft > 0 ? "CANCEL TOURNAMENT" : Tournament.finished ? "BACK TO MAIN MENU" : "NEXT ROUND") : Network.mode == Network.Mode.JOIN && !Network.hostLeftMatch ? "Please Wait for Host to exit Match first" : "END MATCH", .55 + extraY);
  if (!Keys.inUse) {
   UI.selected =
   Math.abs(.45 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 0 :
   Math.abs(.475 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 1 :
   Math.abs(.5 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 2 :
   Math.abs(.525 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 3 :
   Math.abs(.55 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 4 :
   UI.selected;
  }
 }

 public enum Match {
  ;
  public static boolean muteSound;
  public static boolean cursorDriving;
  public static boolean started;
  public static boolean messageWait;
  private static double timeLeft;
  private static double stuntTimer;
  public static double printTimer;
  public static String print = "";
  private static String stuntPrint = "";
  private static double vehicleLightBrightnessChange;
  public static final long[] scoreCheckpoint = new long[2];
  public static final long[] scoreLap = new long[2];
  public static final long[] scoreStunt = new long[2];
  public static final double[] scoreDamage = new double[2];
  private static final long[] scoreKill = new long[2];
  private static final double[] finalScore = new double[2];

  private static void runUI(boolean gamePlay) {
   timeLeft -= timeLeft > 0 && status == VE.Status.play && started ? tick : 0;
   Tournament.finished = Tournament.stage > 0 && ((Tournament.stage > 4 && Math.abs(Tournament.wins[0] - Tournament.wins[1]) > 0) || (Tournament.stage > 2 && Math.abs(Tournament.wins[0] - Tournament.wins[1]) > 1));
   if (started && (Keys.Enter || Keys.Escape) && gamePlay) {
    Keys.Up = Keys.Down = Keys.Enter = Keys.Escape = false;
    UI.selected = 0;
    Sounds.UI.play(1, 0);
    status = VE.Status.paused;
   }
   TE.Arrow.MV.setVisible(Options.headsUpDisplay);
   Vehicle V = vehicles.get(vehiclePerspective);
   if (Options.headsUpDisplay) {
    if (timeLeft <= 0) {
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
       String announce = Tournament.stage > 0 && !Tournament.finished ? "ROUND " + Tournament.stage + " OVER" : (finalScore[0] > finalScore[1] ? "GREEN" : "RED") + " TEAM WINS" + (Tournament.finished ? " THE TOURNAMENT!" : "!");
       U.font(.025);
       U.fillRGB(0, 0, 0);
       U.text(announce, titleHeight - .001);
       U.text(announce, titleHeight + .001);
       U.fillRGB(1, 1, 1);
       U.text(announce, titleHeight);
      }
      U.fillRGB(E.Ground.RGB.invert());
      U.font(.0175);
      U.fillRGB(0, 0, 0);
      if (yinYang) {
       U.fillRGB(.5, .5, .5);
      }
      U.text((Tournament.stage < 1 || Tournament.finished ? "FINAL " : "") + "SCORES:", .225);
      if (yinYang) {
       U.fillRGB(0, 1, 0);
      }
      U.text(vehiclesInMatch > 2 ? "GREEN TEAM" : playerNames[0], .3, .225);
      U.text(String.valueOf(Tournament.stage > 0 ? Long.valueOf(Tournament.wins[0]) : formatFinal[0]), .3, .25);
      if (yinYang) {
       U.fillRGB(1, 0, 0);
      }
      U.text(vehiclesInMatch > 2 ? "RED TEAM" : playerNames[1], .7, .225);
      U.text(String.valueOf(Tournament.stage > 0 ? Long.valueOf(Tournament.wins[1]) : formatFinal[1]), .7, .25);
     } else {
      U.font(.025);
      U.fillRGB(0, 0, 0);
      U.text("TIME'S UP!", titleHeight - .001);
      U.text("TIME'S UP!", titleHeight + .001);
      U.fillRGB(1, 1, 1);
      U.text("TIME'S UP!", titleHeight);
      U.fillRGB(E.Ground.RGB.invert());
      U.font(.0175);
      double color = yinYang ? .5 : 0;
      U.fillRGB(color);
      U.text("FINAL SCORE: " + finalScore[1], .225);
     }
    }
    if (V.destroyed) {
     if (V.explosionType != Vehicle.ExplosionType.maxnuclear) {
      U.font(.02);
      if (yinYang) {
       U.fillRGB(1, 1, 1);
       U.text(".. REVIVING.. ", .275);
      } else {
       U.fillRGB(0, 0, 0);
       U.text(" ..REVIVING ..", .275);
      }
     }
    }
    U.font(.01);
    TE.Arrow.run();
    //U.textR(String.valueOf(V.AI.behavior.name()), .9, .5);//U.textR(String.valueOf(V.drive2), .9, .525);//U.textR(String.valueOf(V.reverse), .9, .55);//U.textR(String.valueOf(V.reverse2), .9, .575);
    U.fillRGB(0, 0, 0, UI.drawOpacity.minimal);
    U.fillRectangle(.025, .8, .05, .425);
    U.fillRectangle(.975, .8, .05, .425);
    DestructionLog.run();
    runHUDBlocks(V);
    if (Network.mode == Network.Mode.JOIN && Network.hostLeftMatch) {
     U.font(.02);
     double color = yinYang ? 1 : .5;
     U.fillRGB(color, color, color);
     U.text("The Host has left match--hit Enter to start another match", .9);
    } else if (V.P.mode == Physics.Mode.fly && E.gravity != 0 && U.sin(V.YZ) > 0 && V.P.netSpeedY + V.P.stallSpeed > 0) {
     U.fillRGB(E.skyRGB.invert());
     U.text("STALL", .95);
    }
    if (printTimer > 0) {
     if (timeLeft > 0) {
      U.font(.01);
      double color = yinYang ? 0 : 1;
      U.fillRGB(color, color, color);
      U.text(print, .125);
     }
     printTimer -= gamePlay ? tick : 0;
    } else {
     messageWait = false;
    }
    U.font(.0125);
    if (!DestructionLog.inUse) {
     if (V.P.flipped && V.P.flipTimer > 0) {
      if (V.P.mode.name().startsWith(Physics.Mode.drive.name())) {
       double color = yinYang ? 0 : 1;
       U.fillRGB(color, color, color);
       U.text("Bad Landing", .075);
      }
     } else if (stuntTimer > 0) {
      U.fillRGB(0, yinYang ? 1 : 0, 0);
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
   if (Options.headsUpDisplay) {
    U.font(.00875);
    U.fillRGB(0, 0, 0, UI.drawOpacity.minimal);
    U.fillRectangle(.9375, .26, .125, .2);
    if (vehiclesInMatch > 1) {
     U.fillRectangle(.0625, .26, .125, .2);
     //GREEN
     U.fillRGB(0, 1, 0);
     U.textL(vehiclesInMatch > 2 ? "GREEN TEAM" : playerNames[0], .0125, .175);
     if (bonusHolder > -1 && bonusHolder < vehiclesInMatch >> 1) {
      U.textL("(Player " + bonusHolder + ") BONUS", .0125, .325);
     }
     U.textL(U.DF.format(score[0]) + " :Current Score", .0125, .35);
     if (!TE.checkpoints.isEmpty()) {
      U.fillRGB(0, 1, 0, yinYang || scoreCheckpoint[0] >= scoreCheckpoint[1] ? 1 : .5);
      U.textL(scoreCheckpoint[0] + " :Checkpoints", .0125, .2);
      U.fillRGB(0, 1, 0, yinYang || scoreLap[0] >= scoreLap[1] ? 1 : .5);
      U.textL(scoreLap[0] + " :Laps", .0125, .225);
     }
     U.fillRGB(0, 1, 0, yinYang || scoreStunt0 >= scoreStunt1 ? 1 : .5);
     U.textL(scoreStunt0 + " :Stunts", .0125, .25);
     U.fillRGB(0, 1, 0, yinYang || scoreDamage0 >= scoreDamage1 ? 1 : .5);
     U.textL(U.DF.format(scoreDamage0) + " :Damage Dealt", .0125, .275);
     U.fillRGB(0, 1, 0, yinYang || scoreKill[0] >= scoreKill[1] ? 1 : .5);
     U.textL(scoreKill[0] + " :Kills", .0125, .3);
     //RED
     U.fillRGB(1, 0, 0);
     U.textR(vehiclesInMatch > 2 ? "RED TEAM" : playerNames[1], .9875, .175);
     if (bonusHolder >= vehiclesInMatch >> 1) {
      U.textR("BONUS (Player " + bonusHolder + ")", .9875, .325);
     }
     U.textR("Current Score: " + U.DF.format(score[1]), .9875, .35);
     if (!TE.checkpoints.isEmpty()) {
      U.fillRGB(1, 0, 0, yinYang || scoreCheckpoint[1] >= scoreCheckpoint[0] ? 1 : .5);
      U.textR("Checkpoints: " + scoreCheckpoint[1], .9875, .2);
      U.fillRGB(1, 0, 0, yinYang || scoreLap[1] >= scoreLap[0] ? 1 : .5);
      U.textR("Laps: " + scoreLap[1], .9875, .225);
     }
     U.fillRGB(1, 0, 0, yinYang || scoreStunt[1] >= scoreStunt[0] ? 1 : .5);
     U.textR("Stunts: " + scoreStunt1, .9875, .25);
     U.fillRGB(1, 0, 0, yinYang || scoreDamage1 >= scoreDamage0 ? 1 : .5);
     U.textR("Damage Dealt: " + U.DF.format(scoreDamage1), .9875, .275);
     U.fillRGB(1, 0, 0, yinYang || scoreKill[1] >= scoreKill[0] ? 1 : .5);
     U.textR("Kills: " + scoreKill[1], .9875, .3);
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
     if (bonusHolder >= vehiclesInMatch >> 1) {
      U.textR("BONUS (Player " + bonusHolder + ")", .9875, .325);
     }
     U.textR("Current Score: " + U.DF.format(score[1]), .9875, .35);
    }
   }
   if (timeLeft < 0) {
    finalScore[0] = score[0];
    finalScore[1] = score[1];
    String[] formatFinal = {U.DF.format(finalScore[0]), U.DF.format(finalScore[1])};
    boolean matchTie = formatFinal[0].equals(formatFinal[1]);
    if (vehiclesInMatch > 1 && Options.headsUpDisplay) {
     if (matchTie) {
      Sounds.finish.play(0, 0);
      Sounds.finish.play(1, 0);
     } else {
      Sounds.finish.play((score[0] > score[1] && vehiclePerspective < vehiclesInMatch >> 1) || (score[1] > score[0] && vehiclePerspective >= vehiclesInMatch >> 1) ? 0 : 1, 0);
     }
    }
    if (!matchTie) {
     Tournament.wins[score[0] > score[1] ? 0 : score[1] > score[0] ? 1 : -1]++;
    }
    timeLeft = 0;
   }
  }

  private static void runHUDBlocks(Vehicle V) {
   //LEFT HUD BLOCK
   U.font(.0125);
   if (!V.isFixed()) {
    U.fillRGB(.75, .75, .75);
    U.fillRectangle(.025, .7, .01, Math.min(.2, .2 * (Math.abs(V.P.speed) / V.topSpeeds[1])));
    U.fillRGB(1, 1, 1);
    U.fillRectangle(.025, .6, .02, .001);
    U.fillRectangle(.025, .8, .02, .001);
    U.text(Math.abs(V.P.speed) >= 10000 ? U.DF.format(V.P.speed) : String.valueOf(Math.round(V.P.speed * Options.units)), .025, .7);
    U.text(unitName[0], .025, .825);
   }
   U.fillRGB(1, 1, 1);
   U.font(.01);
   double converted = Options.units == .5364466667 ? .0175 : Options.units == 1 / 3. ? .0574147 : 1;
   U.font(.0075);
   U.text("(" + unitName[1] + ")", .025, .875);
   U.textL("X: " + U.DF.format(V.X * converted), .00625, .9);
   U.textL("Y: " + U.DF.format(V.Y * converted), .00625, .925);
   U.textL("Z: " + U.DF.format(V.Z * converted), .00625, .95);
   U.font(.015);
   //RIGHT HUD BLOCK
   double damage = V.getDamage(true);
   U.fillRGB(1, 1 - damage, 0);
   U.fillRectangle(.975, .7, .01, Math.min(.2, .2 * damage));
   U.fillRGB(1, 1, 1);
   U.fillRectangle(.975, .6, .02, .001);
   U.fillRectangle(.975, .8, .02, .001);
   U.text(Math.round(100 * damage) + "%", .975, .7);
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
    U.text(vehiclePerspective + (vehiclePerspective == userPlayerIndex ? " (You)" : ""), .975, .89);
   }
   U.fillRGB(1, 1, 1);
   U.font(.01);
   U.text("Time", .975, .925);
   U.text("Left:", .975, .94);
   U.font(.015);
   U.text(String.valueOf(Math.round(timeLeft)), .975, .965);
  }

  public static void processStunt(Vehicle V) {
   if (V.index == vehiclePerspective && V.P.stuntTimer > V.P.stuntLandWaitTime && V.P.stuntReward > 0) {
    String stuntSpins = "", stuntRolls = "", stuntFlips = "";
    long computeStuntYZ = 0, computeStuntXY = 0, computeStuntXZ = 0;
    while (computeStuntYZ < Math.abs(V.P.stuntYZ) - 45) computeStuntYZ += 360;
    stuntFlips = computeStuntYZ > 0 ? (V.flipCheck[0] && V.flipCheck[1] ? SL.biDirectional : "") + computeStuntYZ + "-Flip" :
    V.flipCheck[0] || V.flipCheck[1] ? "Half-Flip" : stuntFlips;
    while (computeStuntXY < Math.abs(V.P.stuntXY) - 45) computeStuntXY += 360;
    stuntRolls = computeStuntXY > 0 ? (V.rollCheck[0] && V.rollCheck[1] ? SL.biDirectional : "") + computeStuntXY + "-Roll" :
    V.rollCheck[0] || V.rollCheck[1] ? (stuntFlips.isEmpty() ? "Half-Roll" : "Flipside") : stuntRolls;
    while (computeStuntXZ < Math.abs(V.P.stuntXZ) - 45) computeStuntXZ += 180;
    stuntSpins = computeStuntXZ > 0 ? (V.spinCheck[0] && V.spinCheck[1] ? SL.biDirectional : "") + computeStuntXZ + "-Spin" :
    V.spinCheck[0] || V.spinCheck[1] ? "Half-Spin" : stuntSpins;
    stuntTimer = (stuntFlips.isEmpty() ? 0 : 25) + (stuntRolls.isEmpty() ? 0 : 25) + (stuntSpins.isEmpty() ? 0 : 25) + (V.offTheEdge ? 25 : 0);
    if (status == VE.Status.play || status == VE.Status.replay) {
     if (Options.headsUpDisplay && !DestructionLog.inUse) {
      Sounds.stunt.play(0);
     }
     String by1 = !stuntFlips.isEmpty() && !stuntRolls.isEmpty() ? " by " : "",
     by2 = !stuntSpins.isEmpty() && (!stuntFlips.isEmpty() || !stuntRolls.isEmpty()) ? " by " : "";
     stuntPrint = "Landed " + (V.offTheEdge ? "an off-the-edge " : "a ") + stuntFlips + by1 + stuntRolls + by2 + stuntSpins + "!";
    }
    V.P.stuntReward = 0;
   }
  }
 }

 public enum DestructionLog {
  ;
  private static boolean inUse;
  public static final String[][] names = new String[5][2];
  public static final Color[][] nameColors = new Color[5][2];

  private static void run() {
   if (inUse) {
    U.font(.00875);
    double x1 = .4725, x2 = .5275, y1 = .0375, y2 = .05, y3 = .0625, y4 = .075, y5 = .0875;
    U.fillRectangle(.5, .05, .4, .08);
    U.fillRGB(1, 1, 1);
    U.text(SL.destroyed, y1);
    U.text(SL.destroyed, y2);
    U.text(SL.destroyed, y3);
    U.text(SL.destroyed, y4);
    U.text(SL.destroyed, y5);
    //LEFT
    U.fillRGB(nameColors[0][0]);
    U.textR(names[0][0], x1, y1);
    U.fillRGB(nameColors[1][0]);
    U.textR(names[1][0], x1, y2);
    U.fillRGB(nameColors[2][0]);
    U.textR(names[2][0], x1, y3);
    U.fillRGB(nameColors[3][0]);
    U.textR(names[3][0], x1, y4);
    U.fillRGB(nameColors[4][0]);
    U.textR(names[4][0], x1, y5);
    //RIGHT
    U.fillRGB(nameColors[0][1]);
    U.textL(names[0][1], x2, y1);
    U.fillRGB(nameColors[1][1]);
    U.textL(names[1][1], x2, y2);
    U.fillRGB(nameColors[2][1]);
    U.textL(names[2][1], x2, y3);
    U.fillRGB(nameColors[3][1]);
    U.textL(names[3][1], x2, y4);
    U.fillRGB(nameColors[4][1]);
    U.textL(names[4][1], x2, y5);
   }
  }

  public static void update() {
   for (int n = 1; n < 5; n++) {
    names[n - 1][0] = names[n][0];
    names[n - 1][1] = names[n][1];
    nameColors[n - 1][0] = nameColors[n][0];
    nameColors[n - 1][1] = nameColors[n][1];
   }
  }
 }

 private static void runLANMenu() {
  U.fillRGB(0, 0, 0, UI.drawOpacity.maximal);
  U.fillRectangle(.5, .5, 1, 1);
  int n;
  if (UI.page < 1) {
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
   UI.page = 1;
  } else {
   scene.setCursor(Cursor.CROSSHAIR);
   if (UI.selectionTimer > UI.selectionWait) {
    if (Keys.Up) {
     UI.selected = --UI.selected < 0 ? 1 : UI.selected;
     Keys.inUse = true;
     Sounds.UI.play(0, 0);
    }
    if (Keys.Down) {
     UI.selected = ++UI.selected > 1 ? 0 : UI.selected;
     Keys.inUse = true;
     Sounds.UI.play(0, 0);
    }
   }
   if (yinYang && Network.mode == Network.Mode.OFF) {
    if (UI.selected == 0) {
     U.fillRGB(0, 0, 1);
    } else {
     U.fillRGB(0, 1, 0);
    }
    U.fillRectangle(.5, UI.selected == 1 ? .5 : .45, .25, UI.selectionHeight);
    String s;
    try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream(SL.gameSettings), U.standardChars))) {
     for (String s1; (s1 = BR.readLine()) != null; ) {
      s = s1.trim();
      Network.userName = s.startsWith(SL.Network.findUserName) ? U.getString(s, 0) : Network.userName;
      Network.targetHost = s.startsWith(SL.Network.findTargetHost) ? U.getString(s, 0) : Network.targetHost;
      Network.port = s.startsWith("Port(") ? (int) Math.round(U.getValue(s, 0)) : Network.port;
     }
    } catch (IOException e) {
     System.out.println("Problem updating Online settings: " + e);
    }
   }
   if (Network.mode == Network.Mode.HOST && !Network.out.isEmpty()) {
    for (n = 0; n < Network.out.size(); n++) {
     String s = Network.readIn(n);
     if (s.startsWith(SL.Network.cancel)) {
      escapeToLast(false);
     }
    }
   }
   if ((Keys.Enter || Keys.Space) && !Network.waiting) {
    if (UI.selected == 0) {
     Network.mode = Network.Mode.HOST;
     Network.loadGameThread();
     Network.waiting = true;
    } else if (UI.selected == 1) {
     Network.mode = Network.Mode.JOIN;
     Network.loadGameThread();
     Network.waiting = true;
    }
    Sounds.UI.play(1, 0);
    Keys.Enter = Keys.Space = false;
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
   UI.selected =
   Math.abs(.45 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 0 :
   Math.abs(.5 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 1 :
   UI.selected;
  }
 }

 private static void runHowToPlay() {
  UI.page = Math.max(UI.page, 1);
  U.fillRGB(0, 0, 0, UI.drawOpacity.maximal);
  U.fillRectangle(.5, .5, 1, 1);
  U.fillRGB(1, 1, 1);
  U.font(.03);
  U.text("<-LAST", .1, .75);
  U.text("NEXT->", .9, .75);
  U.text("RETURN", .5, .95);
  if (UI.page == 1) {
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
  } else if (UI.page == 2) {
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
  } else if (UI.page == 3) {
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
   U.fillRGB(1, 1, 1);
   U.text("Some vehicles have Guided weaponry.", .625);
   U.text("When fired, these weapons will intercept the nearest opponent automatically.", .65);
  } else if (UI.page == 4) {
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
  if (UI.selectionTimer > UI.selectionWait) {
   if (Keys.Right) {
    if (++UI.page > 4) {
     UI.page = 0;
     status = lastStatus;
    }
    Sounds.UI.play(0, 0);
   }
   if (Keys.Left) {
    if (--UI.page < 1) {
     UI.page = 0;
     status = lastStatus;
    }
    Sounds.UI.play(0, 0);
   }
   if (Keys.Enter) {
    UI.page = 0;
    status = lastStatus;
    Sounds.UI.play(1, 0);
    Keys.Enter = false;
   }
  }
  if (Keys.Escape) {
   UI.page = 0;
   status = lastStatus;
   Sounds.UI.play(1, 0);
   Keys.Escape = false;
  }
  gameFPS = U.refreshRate * .25;
 }

 public enum Options {
  ;
  public static long driverSeat, matchLength;
  public static double units = 1;
  public static boolean normalMapping, headsUpDisplay = true;
  static boolean degradedSoundEffects;
  private static boolean showInfo;

  private static void run() {
   boolean fromMenu = status == VE.Status.optionsMenu;
   if (fromMenu) {
    U.fillRGB(0, 0, 0);
    U.fillRectangle(.5, .5, 1, 1);
   }
   U.fillRGB(1, 1, 1);
   U.font(.05);
   U.text(SL.options, .15);
   U.font(.015);
   U.text("RETURN", .875 + UI.textOffset);
   U.fillRGB(1, 1, 1);
   U.text("DriverSeat [" + (driverSeat > 0 ? "RIGHT->" : driverSeat < 0 ? "<-LEFT" : "CENTER") + "]", .3 + UI.textOffset);
   U.text("Units [" + (units == .5364466667 ? "METRIC" : units == 1 / 3. ? "U.S." : "VEs") + "]", .35 + UI.textOffset);
   U.text("Limit FPS to [" + (userFPS > U.refreshRate ? "JavaFX Default" : Long.valueOf(userFPS)) + "]", .4 + UI.textOffset);
   U.text("Camera-Shake Effects [" + (Camera.shake ? "ON" : "OFF") + "]", .45 + UI.textOffset);
   if (fromMenu) {
    U.text("Normal-Mapping [" + (normalMapping ? "ON" : "OFF") + "]", .5 + UI.textOffset);
    U.text("Match Length [" + matchLength + "]", .55 + UI.textOffset);
    U.text("Game Mode [" + (Tournament.stage > 0 ? "TOURNAMENT" : "NORMAL") + "]", .6 + UI.textOffset);
    U.text("# of Players [" + vehiclesInMatch + "]", .65 + UI.textOffset);
   }
   if (UI.selectionReady()) {
    if (Keys.Up) {
     if (--UI.selected < 0) {
      UI.selected = fromMenu ? 8 : 4;
     }
     Keys.inUse = true;
     Sounds.UI.play(0, 0);
    }
    if (Keys.Down) {
     UI.selected = ++UI.selected > 8 || (!fromMenu && UI.selected > 4) ? 0 : UI.selected;
     Keys.inUse = true;
     Sounds.UI.play(0, 0);
    }
   }
   double color = yinYang ? 1 : 0;
   U.strokeRGB(color, color, color);
   U.drawRectangle(.5, UI.selected == 0 ? .875 : .25 + (.05 * UI.selected), width, UI.selectionHeight);
   U.fillRGB(1, 1, 1);
   boolean isAdjustFunction = false;
   if (UI.selected == 1) {
    isAdjustFunction = true;
    U.text("Driver view location (for applicable vehicles)", .75);
    if (UI.selectionReady()) {
     if (Keys.Left && driverSeat > -1) {
      driverSeat--;
      Sounds.UI.play(0, 0);
     }
     if (Keys.Right && driverSeat < 1) {
      driverSeat++;
      Sounds.UI.play(0, 0);
     }
    }
   } else if (UI.selected == 2) {
    U.text("Switch between Metric, U.S., or the game's raw units (VEs)", .75);
    if ((Keys.Enter || Keys.Space) && UI.selectionReady()) {
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
   } else if (UI.selected == 3) {
    isAdjustFunction = true;
    if (UI.selectionReady()) {
     if (Keys.Left && userFPS > 1) {
      userFPS = userFPS > U.refreshRate ? U.refreshRate - 1 : --userFPS;
      Sounds.UI.play(0, 0);
     }
     if (Keys.Right && userFPS < Long.MAX_VALUE) {
      userFPS = ++userFPS >= U.refreshRate ? Long.MAX_VALUE : userFPS;
      Sounds.UI.play(0, 0);
     }
    }
    U.text("Lower the FPS ceiling if your PC can't process V.E. well (i.e. overheating). Leave maxed otherwise.", .75);
   } else if (UI.selected == 4) {
    if ((Keys.Enter || Keys.Space) && UI.selectionReady()) {
     Camera.shake = !Camera.shake;
    }
    U.text("Shakes camera when vehicles explode, etc.", .75);
   } else if (UI.selected == 5) {
    if (fromMenu) {
     U.text("Use normal-mapping on textured surfaces", .75);
     if ((Keys.Enter || Keys.Space) && UI.selectionReady()) {
      normalMapping = !normalMapping;
     }
    } else {
     UI.selected++;
    }
   } else if (UI.selected == 6) {
    if (fromMenu) {
     isAdjustFunction = true;
     if (UI.selectionReady()) {
      if (Keys.Left && matchLength > 0) {
       matchLength = Math.max(0, matchLength - 10);
       Sounds.UI.play(0, 0);
      }
      if (Keys.Right) {
       matchLength += 10;
       Sounds.UI.play(0, 0);
      }
     }
     U.text("Set how long the match lasts", .75);
    } else {
     UI.selected++;
    }
   } else if (UI.selected == 7) {
    if (fromMenu) {
     U.text("See the Documentation for more info on Game Modes", .75);
     if ((Keys.Enter || Keys.Space) && UI.selectionReady()) {
      Tournament.stage = Tournament.stage > 0 ? 0 : 1;
     }
    } else {
     UI.selected++;
    }
   } else if (UI.selected == 8) {
    if (fromMenu) {
     isAdjustFunction = true;
     if (UI.selectionReady()) {
      int playerFloor = Tournament.stage > 0 ? 2 : 1;
      if (Keys.Left) {
       vehiclesInMatch = --vehiclesInMatch < playerFloor ? maxPlayers : vehiclesInMatch;
       Sounds.UI.play(0, 0);
      }
      if (Keys.Right) {
       vehiclesInMatch = ++vehiclesInMatch > maxPlayers ? playerFloor : vehiclesInMatch;
       Sounds.UI.play(0, 0);
      }
     }
     U.text("More players may slow down performance", .825);
    } else {
     UI.selected++;
    }
   }
   U.fillRGB(1, 1, 1);
   U.text(UI.selected > 0 ? isAdjustFunction ? "Use Left and Right arrow keys to Adjust" : "Click or hit Enter/Space to Change" : "", .8);
   if ((Keys.Enter || Keys.Space) && UI.selectionReady()) {
    status = UI.selected == 0 ? fromMenu ? VE.Status.mainMenu : VE.Status.paused : status;
    Sounds.UI.play(1, 0);
    Keys.Enter = Keys.Space = false;
   }
   if (Keys.Escape) {
    status = fromMenu ? VE.Status.mainMenu : VE.Status.paused;
    Sounds.UI.play(1, 0);
    Keys.Escape = false;
   }
   vehiclesInMatch = Tournament.stage > 0 ? Math.max(2, vehiclesInMatch) : vehiclesInMatch;
   if (!Keys.inUse) {
    double clickOffset = .025;
    UI.selected =
    Math.abs(.825 + clickOffset - Mouse.Y) < UI.clickRangeY ? 0 :
    Math.abs(.25 + clickOffset - Mouse.Y) < UI.clickRangeY ? 1 :
    Math.abs(.3 + clickOffset - Mouse.Y) < UI.clickRangeY ? 2 :
    Math.abs(.35 + clickOffset - Mouse.Y) < UI.clickRangeY ? 3 :
    Math.abs(.4 + clickOffset - Mouse.Y) < UI.clickRangeY ? 4 :
    UI.selected;
    if (status == VE.Status.optionsMenu) {
     UI.selected =
     Math.abs(.45 + clickOffset - Mouse.Y) < UI.clickRangeY ? 5 :
     Math.abs(.5 + clickOffset - Mouse.Y) < UI.clickRangeY ? 6 :
     Math.abs(.55 + clickOffset - Mouse.Y) < UI.clickRangeY ? 7 :
     Math.abs(.6 + clickOffset - Mouse.Y) < UI.clickRangeY ? 8 :
     UI.selected;
    }
   }
   gameFPS = Double.POSITIVE_INFINITY;
  }
 }

 enum Credits {
  ;
  private static boolean direction;
  private static double quantity;

  private static void run() {
   if (UI.page < 1) {
    Sounds.finish.play(U.random() < .5 ? 0 : 1, 0);
    UI.page = 1;
   }
   U.fillRGB(0, 0, 0);
   U.fillRectangle(.5, .5, 1, 1);
   Image RA = U.Images.get("RA");
   graphicsContext.drawImage(RA, width * .2 - (RA.getWidth() * .5), height * .5 - (RA.getHeight() * .5));
   graphicsContext.drawImage(RA, width * .8 - (RA.getWidth() * .5), height * .5 - (RA.getHeight() * .5));
   if (UI.page == 1) {
    quantity = Math.round(quantity);
    quantity += direction ? -1 : 1;
    direction = !(quantity < 2) && (quantity > 13 || direction);
    U.font(.075);
    U.fillRGB(.5, .5, .5);
    if (quantity == 1) {
     U.fillRGB(1, 1, 1);
    }
    U.text(SL.theVehicularEpic, .15);
    U.font(.015);
    U.fillRGB(.5, .5, .5);
    if (quantity == 2) {
     U.fillRGB(1, 1, 1);
    }
    U.text("an open-source project maintained by", .2);
    U.fillRGB(.5, .5, .5);
    if (quantity == 3) {
     U.fillRGB(1, 1, 1);
    }
    U.text("Ryan Albano", .25);
    U.fillRGB(.5, .5, .5);
    if (quantity == 4) {
     U.fillRGB(1, 1, 1);
    }
    U.text("Other Credits:", .35);
    U.fillRGB(.5, .5, .5);
    if (quantity == 5) {
     U.fillRGB(1, 1, 1);
    }
    U.text("Vitor Macedo (VitorMac) and Dany Fernández Diaz--for programming assistance", .4);
    U.fillRGB(.5, .5, .5);
    if (quantity == 6) {
     U.fillRGB(1, 1, 1);
    }
    U.text("Max Place--for composing some map soundtracks", .45);
    U.fillRGB(.5, .5, .5);
    if (quantity == 7) {
     U.fillRGB(1, 1, 1);
    }
    U.text("Rory McHenry--for teaching IDE/Java basics", .5);
    U.fillRGB(.5, .5, .5);
    if (quantity == 8) {
     U.fillRGB(1, 1, 1);
    }
    U.text("Omar Waly--his Java work (Need for Madness and Radical Aces) have served as a design 'template' for V.E.", .55);
    U.fillRGB(.5, .5, .5);
    if (quantity == 9) {
     U.fillRGB(1, 1, 1);
    }
    U.text("The OpenJavaFX team/community--for their hard work making V.E.'s graphics engine possible", .6);
    U.fillRGB(.5, .5, .5);
    if (quantity == 10) {
     U.fillRGB(1, 1, 1);
    }
    U.text("The FXyz library--for additional shape/geometry support", .65);
    U.fillRGB(.5, .5, .5);
    if (quantity == 11) {
     U.fillRGB(1, 1, 1);
    }
    U.text("JavaZoom--for JLayer (a Java .mp3 player)", .7);
    U.fillRGB(.5, .5, .5);
    if (quantity == 12) {
     U.fillRGB(1, 1, 1);
    }
    U.text("Everyone who suggested or submitted content!", .75);
    U.fillRGB(.5, .5, .5);
    if (quantity == 13) {
     U.fillRGB(1, 1, 1);
    }
    U.font(.03);
    U.text("And thank YOU for playing", .85);
    U.fillRGB(.5, .5, .5);
    if (quantity == 14) {
     U.fillRGB(1, 1, 1);
    }
    U.text("and supporting independent gaming!", .9);
   } else if (UI.page == 2) {
    quantity *= direction ? .99 : 1.01;
    if (quantity < 2) {
     direction = false;
     quantity = 2;
    } else if (quantity > 2000) {
     direction = true;
    }
    double[] clusterX = new double[(int) quantity],
    clusterY = new double[(int) quantity];
    for (int n = (int) quantity; --n >= 0; ) {
     clusterX[n] = (width * .5) + StrictMath.pow(U.random(90000000000.), .25) - StrictMath.pow(U.random(90000000000.), .25);
     clusterY[n] = (height * .5) + StrictMath.pow(U.random(60000000000.), .25) - StrictMath.pow(U.random(60000000000.), .25);
    }
    U.fillRGB(1, 1, 1);
    graphicsContext.fillPolygon(clusterX, clusterY, (int) quantity);
    U.font(.05);
    U.fillRGB(0, 0, 0);
    U.text("VEHICULAR", .45);
    U.text("EPIC", .55);
   }
   if (UI.selectionTimer > UI.selectionWait) {
    if (Keys.Left) {
     if (--UI.page < 1) {
      UI.page = 0;
      status = VE.Status.mainMenu;
     }
     Sounds.UI.play(0, 0);
    }
    if (Keys.Right) {
     if (++UI.page > 2) {
      UI.page = 0;
      status = VE.Status.mainMenu;
     }
     Sounds.UI.play(0, 0);
    }
    if (Keys.Enter || Keys.Space) {
     UI.page = 0;
     status = VE.Status.mainMenu;
     Keys.Enter = Keys.Space = false;
     Sounds.UI.play(1, 0);
    }
   }
   if (Keys.Escape) {
    UI.page = 0;
    status = VE.Status.mainMenu;
    Keys.Escape = false;
    Sounds.UI.play(1, 0);
   }
   U.fillRGB(1, 1, 1);
   U.font(.03);
   U.text("<-LAST", .1, .75);
   U.text("NEXT->", .9, .75);
   gameFPS = Double.POSITIVE_INFINITY;
  }
 }

 private static void reset() {
  Match.started = Camera.flowFlip = false;
  vehiclePerspective = userPlayerIndex;
  Match.timeLeft = Options.matchLength;
  TE.Arrow.target = Math.min(vehiclesInMatch - 1, TE.Arrow.target);
  Match.scoreCheckpoint[0] = Match.scoreCheckpoint[1] = Match.scoreLap[0] = Match.scoreLap[1] = Match.scoreKill[0] = Match.scoreKill[1] = 1;
  Match.scoreDamage[0] = Match.scoreDamage[1] = Camera.aroundVehicleXZ = Match.printTimer = Camera.lookAround = Match.scoreStunt[0] = Match.scoreStunt[1] = 0;
  TE.Bonus.big.setVisible(true);
  for (TE.Bonus.Ball bonusBall : TE.Bonus.balls) {
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

 private static void resetGraphics() {
  boolean addSunlightBack = E.lights.getChildren().contains(E.Sun.light),//<-Check LIGHT group, not main group!
  addSunBack = group.getChildren().contains(E.Sun.S),
  addGroundBack = group.getChildren().contains(E.Ground.C);
  group.getChildren().clear();
  E.lights.getChildren().clear();
  U.Nodes.add(E.ambientLight, addSunBack ? E.Sun.S : null, addGroundBack ? E.Ground.C : null);
  U.Nodes.Light.add(addSunlightBack ? E.Sun.light : null);
  U.Nodes.add(E.lights);
  TE.Arrow.group.getChildren().clear();
 }

 public static void denyExpensiveInGameCall() {
  if (status == Status.play || status == Status.replay) {
   throw new IllegalStateException("For performance reasons--this call is not allowed during gameplay.");
  }
 }
}
