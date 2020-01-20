package ve.ui;

import javafx.scene.Cursor;
import javafx.scene.paint.PhongMaterial;
import ve.environment.*;
import ve.environment.storm.Storm;
import ve.instances.I;
import ve.trackElements.*;
import ve.trackElements.trackParts.TrackPart;
import ve.utilities.*;
import ve.vehicles.AI;
import ve.vehicles.Vehicle;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum Maps {
 ;
 public static int map;
 public static final List<String> maps = new ArrayList<>(Arrays.asList(SL.basic, "lapsGlory", SL.checkpoint, "gunpowder", "underOver", SL.antigravity, "versus1", "versus2", "versus3", "trackless", "desert", "3DRace", "trip", "raceNowhere", "moonlight", "bottleneck", "railing", "twisted", "deathPit", "falls", "pyramid", "combustion", "darkDivide", "arctic", "scenicRoute", "winterMode", "mountainHop", "damage", "crystalCavern", "southPole", "aerialControl", "matrix", "mist", "vansLand", "dustDevil", "forest", "columns", "zipCross", "highlands", "coldFury", SL.tornado, "volcanic", SL.tsunami, SL.boulder, "sands", SL.meteor, "speedway", "endurance", "tunnel", "circle", "circleXL", "circles", "everything", "linear", "maze", "xy", "stairwell", "immense", "showdown", "ocean", "lastStand", "parkingLot", "city", "machine", "military", "underwater", "hell", "moon", "mars", "sun", "space1", "space2", "space3", "summit", "portal", "blackHole", "doomsday", "+UserMap & TUTORIAL+"));
 public static String name = "";
 public static boolean randomVehicleStartAngle, guardCheckpointAI;
 public static double defaultVehicleLightBrightness;

 public static void load() {
  TE.instanceSize = 1;
  TE.instanceScale = new double[]{1, 1, 1};
  TE.randomX = TE.randomY = TE.randomZ = 0;
  if (UI.status == UI.Status.mapLoadPass0) {
   UI.scene.setCursor(Cursor.WAIT);
  } else if (UI.status == UI.Status.mapLoadPass1) {
   Nodes.reset();
   Arrow.addToScene();
   I.vehicles.clear();
   E.reset();
   TE.reset();
   Camera.camera.setFarClip(Camera.clipRange.maximumFar);
   UI.scene3D.setFill(U.getColor(0));
   defaultVehicleLightBrightness = 0;
   randomVehicleStartAngle = guardCheckpointAI = false;
   AI.speedLimit = Double.POSITIVE_INFINITY;
  }
  int n;
  String s = "";
  try (BufferedReader BR = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getMapFile(map)), U.standardChars))) {
   for (; (s = BR.readLine()) != null; ) {
    s = s.trim();
    if (UI.status == UI.Status.mapLoadPass2) {
     name = s.startsWith(SL.name) ? U.getString(s, 0) : name;
     if (s.startsWith("ambientLight(")) {
      Nodes.setRGB(E.ambientLight, U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
     }
     Star.load(s);
     E.loadSky(s);
     Fog.load(s);
     Ground.load(s);
     Terrain.load(s);
     if (s.startsWith("viewDistance(")) {
      E.viewableMapDistance = U.getValue(s, 0);
      Camera.camera.setFarClip(U.clamp(Camera.clipRange.normalNear + 1, U.getValue(s, 0), Camera.clipRange.maximumFar));
     } else if (s.startsWith("soundTravel(")) {
      E.soundMultiple = U.getValue(s, 0);
     }
     E.gravity = s.startsWith("gravity(") ? U.getValue(s, 0) : E.gravity;
     Sun.sun.load(s);
     defaultVehicleLightBrightness = s.startsWith("defaultBrightness(") ? U.getValue(s, 0) : defaultVehicleLightBrightness;
     randomVehicleStartAngle = s.startsWith("randomStartAngle") || randomVehicleStartAngle;
     MapBounds.left = s.startsWith("xLimitLeft(") ? U.getValue(s, 0) : MapBounds.left;
     MapBounds.right = s.startsWith("xLimitRight(") ? U.getValue(s, 0) : MapBounds.right;
     MapBounds.forward = s.startsWith("zLimitFront(") ? U.getValue(s, 0) : MapBounds.forward;
     MapBounds.backward = s.startsWith("zLimitBack(") ? U.getValue(s, 0) : MapBounds.backward;
     MapBounds.Y = s.startsWith("yLimit(") ? U.getValue(s, 0) : MapBounds.Y;
     MapBounds.slowVehicles = s.startsWith("slowVehiclesWhenAtLimit") || MapBounds.slowVehicles;
     AI.speedLimit = s.startsWith("speedLimit(") ? U.getValue(s, 0) : AI.speedLimit;
     Ground.level = s.startsWith("noGround") ? Double.POSITIVE_INFINITY : Ground.level;
     guardCheckpointAI = s.startsWith("guardCheckpoint") || guardCheckpointAI;
     if (s.startsWith("snow(")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       Snowball.instances.add(new Snowball.Instance());
      }
     } else if (s.startsWith("wind(")) {
      Wind.maxPotency = U.getValue(s, 0);
      Wind.speedX = U.randomPlusMinus(Wind.maxPotency);
      Wind.speedZ = U.randomPlusMinus(Wind.maxPotency);
     } else if (s.startsWith("windstorm")) {
      Wind.stormExists = true;
      Wind.storm = new Sound("storm" + U.getString(s, 0));
     }
     Cloud.load(s);
     Storm.load(s);
     Tornado.load(s);
     E.loadFrustumMountains(s);
     Pool.pool.load(s);
     if (s.startsWith("trees(")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       TE.trackParts.add(
       U.random() < .5 ?
       new TrackPart(TE.getTrackPartIndex("tree0"), U.randomPlusMinus(TE.wrapDistance), 0, U.randomPlusMinus(TE.wrapDistance), 0, 1 + Math.sqrt(U.random(16.)), TE.instanceScale)
       :
       new TrackPart(TE.getTrackPartIndex(U.random() < .5 ? "tree1" : "tree2"), U.randomPlusMinus(TE.wrapDistance), 0, U.randomPlusMinus(TE.wrapDistance), 0));
      }
     } else if (s.startsWith("palmTrees(")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       TE.trackParts.add(new TrackPart(TE.getTrackPartIndex("treepalm"), U.randomPlusMinus(TE.wrapDistance), 0, U.randomPlusMinus(TE.wrapDistance), 0, 1 + U.random(.6), TE.instanceScale));
      }
     } else if (s.startsWith("cacti(")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       TE.trackParts.add(new TrackPart(TE.getTrackPartIndex(SL.cactus + U.random(3)), U.randomPlusMinus(TE.wrapDistance), 0, U.randomPlusMinus(TE.wrapDistance), 0, .5 + U.random(.5), TE.instanceScale));
      }
     } else if (s.startsWith("mounds(")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       TE.mounds.add(new FrustumMound(U.randomPlusMinus(TE.wrapDistance), U.randomPlusMinus(TE.wrapDistance), 0, 100 + U.random(400.), U.random(100.), U.random(200.), true, false, false));
      }
     }
     Fire.load(s);
     Boulder.load(s);
     Tsunami.load(s);
     Volcano.load(s);
     Meteor.load(s);
    }
    TE.instanceSize = s.startsWith(SL.size + "(") ? U.getValue(s, 0) : TE.instanceSize;
    if (s.startsWith(SL.scale + "(")) {
     try {
      TE.instanceScale[0] = U.getValue(s, 0);
      TE.instanceScale[1] = U.getValue(s, 1);
      TE.instanceScale[2] = U.getValue(s, 2);
     } catch (RuntimeException e) {
      TE.instanceScale[0] = TE.instanceScale[1] = TE.instanceScale[2] = U.getValue(s, 0);
     }
    }
    if (UI.status == UI.Status.mapLoadPass3) {
     TE.randomX = s.startsWith("randomX(") ? U.getValue(s, 0) : TE.randomX;
     TE.randomY = s.startsWith("randomY(") ? U.getValue(s, 0) : TE.randomY;
     TE.randomZ = s.startsWith("randomZ(") ? U.getValue(s, 0) : TE.randomZ;
     Crystal.load(s);//<-Here so it's affected by randomXYZ
     if (U.startsWith(s, "(", SL.strip + "(", SL.curve + "(")) {
      int trackNumber = TE.getTrackPartIndex(U.getString(s, 0));//<-Returns '-1' on exception
      if (trackNumber < 0 && !U.getString(s, 0).isEmpty()) {
       System.out.println("Map Part List Exception (" + name + ")");
       System.out.println(UI.At_Line_ + s);
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
      } else if (name.equals(SL.Maps.crystalCavern)) {
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
      if (s.startsWith(SL.strip + "(")) {
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
      } else if (s.startsWith(SL.curve + "(")) {
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
      TE.trackParts.add(new TrackPart(I.getVehicleIndex(U.getString(s, 0)), U.getValue(s, 1), U.getValue(s, 3), U.getValue(s, 2), U.getValue(s, 4), true));
     }
    }
   }
  } catch (Exception E) {//<-Don't further specify
   UI.status = UI.Status.mapError;
   System.out.println("Map Error (" + name + ")");
   System.out.println(UI.At_Line_ + s);
   E.printStackTrace();
  }
  if (UI.status == UI.Status.mapLoadPass3) {
   if (name.equals(SL.Maps.ghostCity)) {
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
    for (n = 0; n < I.vehicleModels.size(); n++) {
     double xRandom = U.randomPlusMinus(30000.), zRandom = U.randomPlusMinus(30000.), randomXZ = U.randomPlusMinus(180.);
     I.userRandomRGB = U.getColor(U.random(), U.random(), U.random());
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
   if (Pool.type == Pool.Type.lava) {
    for (Tsunami.Part tsunamiPart : Tsunami.parts) {//Setting the illumination here in case the lava pool gets called AFTER tsunami definition
     ((PhongMaterial) tsunamiPart.C.getMaterial()).setSelfIlluminationMap(Phong.getSelfIllumination(E.lavaSelfIllumination[0], E.lavaSelfIllumination[1], E.lavaSelfIllumination[2]));
    }
   }
   TE.bonus.X = TE.bonus.Y = TE.bonus.Z = 0;
   Nodes.add(Bonus.big);
   for (Bonus.Ball bonusBall : Bonus.balls) {
    Nodes.add(bonusBall.S);
   }
  } else if (UI.status == UI.Status.mapLoadPass4) {
   if (!Viewer.inUse) {
    for (n = I.vehiclesInMatch; --n >= 0; ) {
     I.vehicles.add(null);
    }
    I.vehicles.set(I.userPlayerIndex, new Vehicle(VS.chosen[I.userPlayerIndex], I.userPlayerIndex, true));
    for (n = I.vehiclesInMatch; --n >= 0; ) {
     if (n != I.userPlayerIndex) {//<-User player set first for Linux sound optimization
      I.vehicles.set(n, new Vehicle(VS.chosen[n], n, true));
     }
    }
   }
   addTransparentNodes();
   Match.reset();
  }
  String loadText =
  UI.status == UI.Status.mapLoadPass0 ? "Removing Previous Content" :
  UI.status == UI.Status.mapLoadPass1 ? "Loading Properties & Scenery" :
  UI.status == UI.Status.mapLoadPass2 ? "Adding Track Parts" :
  "Adding " + I.vehiclesInMatch + " Vehicle(s)";
  U.fillRGB(0, 0, 0, UI.colorOpacity.maximal);
  U.fillRectangle(.5, .5, 1, 1);
  U.font(.025);
  U.fillRGB(1);
  U.text(Tournament.stage > 0 ? "Round " + Tournament.stage + (Tournament.stage > 5 ? "--Overtime!" : "") : "", .425);
  U.text(name, .475);
  U.text(".." + loadText + "..", .525);
  if (UI.status != UI.Status.mapError) {
   UI.status =
   UI.status == UI.Status.mapLoadPass0 ? UI.Status.mapLoadPass1 :
   UI.status == UI.Status.mapLoadPass1 ? UI.Status.mapLoadPass2 :
   UI.status == UI.Status.mapLoadPass2 ? UI.Status.mapLoadPass3 :
   UI.status == UI.Status.mapLoadPass3 ? UI.Status.mapLoadPass4 :
   (Viewer.inUse ? UI.Status.mapViewer : Network.mode == Network.Mode.JOIN ? UI.Status.play : UI.Status.mapView);
  }
  E.renderType = E.RenderType.ALL;
 }

 static void addTransparentNodes() {
  for (Boulder.Instance instance : Boulder.instances) {
   instance.addTransparentNodes();
  }
  for (Vehicle vehicle : I.vehicles) {
   vehicle.addTransparentNodes();
  }
  for (int n = Fog.spheres.size(); --n >= 0; ) {//<-Adding spheres in forward order doesn't layer fog correctly
   Nodes.add(Fog.spheres.get(n));
  }
 }

 public static int getName(String s) {
  int n;
  String s1;
  for (n = 0; n < maps.size(); n++) {
   try (BufferedReader BR = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getMapFile(n)), U.standardChars))) {
    for (String s2; (s2 = BR.readLine()) != null; ) {
     s1 = s2.trim();
     name = s1.startsWith(SL.name) ? U.getString(s1, 0) : name;
    }
   } catch (IOException e) {
    UI.status = UI.Status.mapError;
    e.printStackTrace();
   }
   if (s.equals(name)) {
    break;
   }
  }
  return n;
 }

 public static FileInputStream getMapFile(int n) {
  File F = new File(U.mapFolder + File.separator + maps.get(n));
  if (!F.exists()) {
   F = new File(U.mapFolder + File.separator + U.userSubmittedFolder + File.separator + maps.get(n));
  }
  if (!F.exists()) {
   map = 0;
   F = new File(U.mapFolder + File.separator + maps.get(0));
  }
  try {
   return new FileInputStream(F);
  } catch (FileNotFoundException E) {
   return null;
  }
 }

 public static void runQuickSelect(boolean gamePlay) {
  if (Network.mode == Network.Mode.JOIN) {
   U.font(.03);
   U.text(UI.Please_Wait_For_ + UI.playerNames[0] + " to Select Map..", .5, .5);
  } else {
   String mapMaker;
   name = mapMaker = "";
   map = Tournament.stage > 0 ? U.random(maps.size()) : map;
   String s;
   try (BufferedReader BR = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getMapFile(map)), U.standardChars))) {
    for (String s1; (s1 = BR.readLine()) != null; ) {
     s = s1.trim();
     name = s.startsWith(SL.name) ? U.getString(s, 0) : name;
     mapMaker = s.startsWith("maker") ? U.getString(s, 0) : mapMaker;
    }
   } catch (IOException e) {
    UI.status = UI.Status.mapError;
    e.printStackTrace();
   }
   if (Tournament.stage > 0) {
    UI.status = UI.Status.mapLoadPass0;
   } else {
    U.fillRGB(0, 0, 0, UI.colorOpacity.maximal);
    U.fillRectangle(.5, .5, 1, 1);
    U.fillRGB(1);
    U.font(.05);
    U.text(name, .5);
    U.font(.03);
    U.text("SELECT MAP" + (Viewer.inUse ? " TO VIEW/EDIT:" : ":"), .25);
    U.text(UI._LAST, .125, .75);
    U.text(UI.NEXT_, .875, .75);
    U.text(UI.CONTINUE, .875);
    U.font(.02);
    U.text(UI.Made_by_ + mapMaker, .6);
    U.font(.01);
    U.text(UI.notifyUserOfArrowKeyNavigation, .95);
    if (UI.selectionReady()) {
     if (Keys.right) {
      map = ++map >= maps.size() ? 0 : map;
      UI.sound.play(0, 0);
     }
     if (Keys.left) {
      map = --map < 0 ? maps.size() - 1 : map;
      UI.sound.play(0, 0);
     }
     if (Keys.enter || Keys.space) {
      UI.status = UI.Status.mapLoadPass0;
      UI.sound.play(1, 0);
     }
    }
    UI.gameFPS = U.refreshRate * .5;
   }
  }
  Network.preMatchCommunication(gamePlay);
  if (Keys.escape) {
   UI.escapeToLast(true);
  }
 }

 public static void runView(boolean gamePlay) {
  Camera.runAroundTrack();
  U.rotate(Camera.camera, Camera.YZ, -Camera.XZ);
  E.run(gamePlay);
  for (Vehicle vehicle : I.vehicles) {
   vehicle.runRender(gamePlay);
  }
  boolean renderALL = E.renderType == E.RenderType.ALL;
  for (TrackPart trackPart : TE.trackParts) {
   trackPart.runGraphics(renderALL);
  }
  for (FrustumMound mound : TE.mounds) {
   mound.runGraphics();
  }
  UI.scene.setCursor(Cursor.CROSSHAIR);
  U.font(.015);
  U.fillRGB(Ground.RGB.invert());
  U.text(UI._LAST, .2, .75);
  U.text(UI.NEXT_, .8, .75);
  U.text(UI.CONTINUE, .75);
  U.font(.01);
  U.text(UI.notifyUserOfArrowKeyNavigation, .95);
  U.fillRGB(E.skyRGB.invert());
  U.font(.02);
  U.text("| " + name + " |", .15);
  Network.preMatchCommunication(gamePlay);
  if (Keys.space || Keys.enter || Tournament.stage > 0) {
   UI.status = UI.Status.play;
   if (Tournament.stage < 1) {
    UI.sound.play(1, 0);
   }
   Camera.view = Camera.View.flow;
   Keys.space = Keys.enter = false;
  } else if (Keys.right || Keys.left) {
   if (Keys.left) {
    map = --map < 0 ? maps.size() - 1 : map;
   }
   if (Keys.right) {
    map = ++map >= maps.size() ? 0 : map;
   }
   UI.status = UI.Status.mapJump;
   UI.sound.play(0, 0);
   for (Vehicle vehicle : I.vehicles) {
    vehicle.closeSounds();
   }
  }
  if (Keys.escape) {
   UI.escapeToLast(true);
  }
  TE.bonus.run();
  UI.gameFPS = Double.POSITIVE_INFINITY;
  E.renderType = E.RenderType.standard;
 }

 public static void runErrored() {
  UI.scene.setCursor(Cursor.CROSSHAIR);
  U.fillRGB(0, 0, 0, UI.colorOpacity.maximal);
  U.fillRectangle(.5, .5, 1, 1);
  U.font(.03);
  U.fillRGB(1, 0, 0);
  U.text("Error Loading This Map", .475);
  U.fillRGB(1);
  U.text(UI._LAST, .125, .75);
  U.text(UI.NEXT_, .875, .75);
  U.text(UI.CONTINUE, .875);
  U.text("Hit Continue or Enter to try again", .525);
  U.font(.01);
  U.text(UI.notifyUserOfArrowKeyNavigation, .95);
  if (Keys.space || Keys.enter) {
   UI.status = UI.Status.mapLoadPass0;
   Keys.space = Keys.enter = false;
   UI.sound.play(1, 0);
  }
  if (Keys.right) {
   map = ++map >= maps.size() ? 0 : map;
   UI.status = UI.Status.mapJump;
   Keys.right = false;
   UI.sound.play(0, 0);
  }
  if (Keys.left) {
   map = --map < 0 ? maps.size() - 1 : map;
   UI.status = UI.Status.mapJump;
   Keys.left = false;
   UI.sound.play(0, 0);
  }
  UI.gameFPS = U.refreshRate * .25;
  if (Keys.escape) {
   UI.escapeToLast(true);
  }
 }
}