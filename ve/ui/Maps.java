package ve.ui;

import javafx.scene.Cursor;
import javafx.scene.paint.PhongMaterial;
import ve.effects.Echo;
import ve.environment.Atmosphere;
import ve.environment.Boulder;
import ve.environment.Cloud;
import ve.environment.Crystal;
import ve.environment.E;
import ve.environment.Fire;
import ve.environment.Fog;
import ve.environment.FrustumMound;
import ve.environment.Ground;
import ve.environment.MapBounds;
import ve.environment.Meteor;
import ve.environment.Pool;
import ve.environment.Snow;
import ve.environment.Star;
import ve.environment.Sun;
import ve.environment.Terrain;
import ve.environment.Tornado;
import ve.environment.Tsunami;
import ve.environment.Volcano;
import ve.environment.Wind;
import ve.environment.storm.Storm;
import ve.instances.I;
import ve.trackElements.Bonus;
import ve.trackElements.Point;
import ve.trackElements.TE;
import ve.trackElements.Waypoint;
import ve.trackElements.trackParts.RepairPoint;
import ve.trackElements.trackParts.TrackPart;
import ve.utilities.Camera;
import ve.utilities.D;
import ve.utilities.Network;
import ve.utilities.Nodes;
import ve.utilities.Phong;
import ve.utilities.U;
import ve.utilities.sound.Controlled;
import ve.utilities.sound.Sounds;
import ve.vehicles.AI;
import ve.vehicles.Vehicle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum Maps {
 ;
 public static int map;
 public static final List<String> maps = new ArrayList<>(Arrays.asList(D.basic, "lapsGlory", "derby", "gunpowder", "underOver", D.antigravity, "versus1", "versus2", "versus3", "trackless", "desert", "3DRace", "trip", "raceNowhere", "moonlight", "bottleneck", "railing", "twisted", "deathPit", "falls", "pyramid", "combustion", "darkDivide", "arctic", "scenicRoute", "winterMode", "mountainHop", "damage", "crystalCavern", "southPole", "aerialControl", "matrix", "mist", "vansLand", "dustDevil", "forest", "columns", "zipCross", "highlands", "coldFury", D.tornado, "volcanic", D.tsunami, D.boulder, "sands", D.meteor, "speedway", "submersion", "endurance", "tunnel", "circle", "circleXL", "circles", "methodMadness", "linear", "maze", "xy", "stairwell", "immense", "showdown", "ocean", "lastStand", "parkingLot", "city", "machine", "military", "underwater", "hell", "moon", "mars", "sun", "space1", "space2", "space3", "summit", "portal", "blackHole", "doomsday", "+UserMap & TUTORIAL+"));
 public static String name = "";
 public static boolean randomVehicleStartAngle, guardWaypointAI;
 public static double defaultVehicleLightBrightness;
 private static final String folder = "maps";

 public static void load() {
  TE.instanceSize = 1;
  TE.instanceScale = new double[]{1, 1, 1};
  TE.randomX = TE.randomY = TE.randomZ = 0;
  if (UI.status == UI.Status.mapLoadPass0) {
   UI.scene.setCursor(Cursor.WAIT);
  } else if (UI.status == UI.Status.mapLoadPass1) {
   Nodes.reset();
   I.vehicles.clear();
   E.reset();
   TE.reset();
   I.resetWhoIsIn();
   Camera.PC.setFarClip(Camera.clipRange.maximumFar);
   UI.scene3D.setFill(U.getColor(0));
   defaultVehicleLightBrightness = 0;
   randomVehicleStartAngle = guardWaypointAI = false;
   AI.speedLimit = Double.POSITIVE_INFINITY;
  }
  int n;
  String s = "";
  try (BufferedReader BR = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getMapFile(map)), U.standardChars))) {
   while ((s = BR.readLine()) != null) {
    s = s.trim();
    if (UI.status == UI.Status.mapLoadPass2) {
     name = s.startsWith(D.name) ? U.getString(s, 0) : name;
     if (s.startsWith("ambientLight(")) {
      Nodes.setLightRGB(E.ambientLight, U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
     }
     Star.load(s);
     E.loadSky(s);
     Fog.load(s);
     Atmosphere.load(s);
     Ground.load(s);
     Terrain.load(s);
     if (s.startsWith("viewDistance(")) {
      E.viewableMapDistance = U.getValue(s, 0);
      Camera.PC.setFarClip(U.clamp(Camera.clipRange.normalNear + 1, U.getValue(s, 0), Camera.clipRange.maximumFar));
     } else if (s.startsWith("soundTravel(")) {
      E.soundMultiple = U.getValue(s, 0);
     } else if (s.startsWith("echo(")) {
      Echo.presence = Math.round(U.getValue(s, 0));
      Sounds.echoVolume = Sounds.decibelToLinear(-Echo.presence * .05);
     }
     E.gravity = s.startsWith("gravity(") ? U.getValue(s, 0) : E.gravity;
     Sun.load(s);
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
     guardWaypointAI = s.startsWith("guardWaypoint") || guardWaypointAI;
     if (s.startsWith("snow(")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       Snow.instances.add(new Snow.Ball());
      }
     } else if (s.startsWith("wind(")) {
      Wind.maxPotency = U.getValue(s, 0);
      Wind.speedX = U.randomPlusMinus(Wind.maxPotency);
      Wind.speedZ = U.randomPlusMinus(Wind.maxPotency);
     } else if (s.startsWith("windstorm")) {
      Wind.stormExists = true;
      Wind.storm = new Controlled("storm" + U.getString(s, 0));
     }
     Cloud.load(s);
     Storm.load(s);
     Tornado.load(s);
     E.loadFrustumMountains(s);
     Pool.load(s);
     if (s.startsWith("trees(")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       TE.trackParts.add(
       U.random() < .5 ?
       new TrackPart(TE.Models.tree0.name(), U.randomPlusMinus(TE.wrapDistance), 0, U.randomPlusMinus(TE.wrapDistance), 0, 1 + Math.sqrt(U.random(16.)), TE.instanceScale) :
       new TrackPart(U.random() < .5 ? TE.Models.tree1.name() : TE.Models.tree2.name(), U.randomPlusMinus(TE.wrapDistance), 0, U.randomPlusMinus(TE.wrapDistance), 0));
      }
     } else if (s.startsWith("palmTrees(")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       TE.trackParts.add(new TrackPart(TE.Models.treepalm.name(), U.randomPlusMinus(TE.wrapDistance), 0, U.randomPlusMinus(TE.wrapDistance), 0, 1 + U.random(.6), TE.instanceScale));
      }
     } else if (s.startsWith("cacti(")) {
      for (n = 0; n < U.getValue(s, 0); n++) {
       TE.trackParts.add(new TrackPart(TE.Models.valueOf(D.cactus + U.random(3)).name(), U.randomPlusMinus(TE.wrapDistance), 0, U.randomPlusMinus(TE.wrapDistance), 0, .5 + U.random(.5), TE.instanceScale));
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
     if (s.startsWith("bonus(")) {
      Bonus.startY = U.getValue(s, 0);
      try {
       Bonus.startX = U.getValue(s, 1);
       Bonus.startZ = U.getValue(s, 2);
      } catch (RuntimeException ignored) {
      }
     }
    }
    TE.instanceSize = s.startsWith(D.size + "(") ? U.getValue(s, 0) : TE.instanceSize;
    if (s.startsWith(D.scale + "(")) {
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
     if (U.startsWith(s, "(", D.strip + "(", D.curve + "(")) {
      TE.Models model = null;
      try {
       model = TE.Models.valueOf(U.getString(s, 0));
      } catch (Exception E) {//<-Don't bother
       if (!U.getString(s, 0).isEmpty()) {
        System.out.println("Map Part List Exception (" + name + ")");
        System.out.println(UI.At_Line_ + s);
        throw E;//<-Throw it again if not empty and name of part does not exist in V.E.
       }
      }
      long[] random = {Math.round(U.randomPlusMinus(TE.randomX)), Math.round(U.randomPlusMinus(TE.randomY)), Math.round(U.randomPlusMinus(TE.randomZ))};
      if (model == TE.Models.waypoint) {
       long cornerDisplace = name.equals("Death Pit") ? 12000 : name.equals("Arctic Slip") ? 4500 : 0;
       random[0] += U.random() < .5 ? cornerDisplace : -cornerDisplace;
       random[2] += U.random() < .5 ? cornerDisplace : -cornerDisplace;
      }
      if (U.equals(name, "Columns Condemn")) {
       random[0] *= 5000;
       random[2] *= 5000;
      } else if (U.equals(name, "the Linear Accelerator")) {
       random[0] *= 1000;
       random[2] *= 1000;
      } else if (name.equals(D.Maps.crystalCavern)) {
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
      if (s.startsWith(D.strip + "(")) {
       double partAngle = Double.NaN;
       try {
        partAngle = U.getValue(s, 7);
       } catch (RuntimeException ignored) {
       }
       double stripEnd = U.getValue(s, 4),
       advanceDistance = U.getValue(s, 5),
       advanceAngle = U.getValue(s, 6);
       for (double iteration = 0; iteration < stripEnd; iteration++) {
        TE.addTrackPart(s, model,
        summedPositionX + (advanceDistance * iteration * U.sin(advanceAngle)),
        summedPositionY,
        summedPositionZ + (advanceDistance * iteration * U.cos(advanceAngle)),
        Double.isNaN(partAngle) ? -advanceAngle : partAngle);
       }
      } else if (s.startsWith(D.curve + "(")) {
       double angle = 90;
       try {
        angle += U.getValue(s, 9);
       } catch (RuntimeException ignored) {
       }
       double curveStart = U.getValue(s, 4), curveEnd = U.getValue(s, 5),
       iterationRate = U.getValue(s, 6), curveRadius = U.getValue(s, 7),
       curveHeight = U.getValue(s, 8) / Math.abs(curveStart - curveEnd);
       for (double iteration = curveStart; ; iteration += iteration < curveEnd ? iterationRate : -iterationRate) {
        TE.addTrackPart(s, model,
        summedPositionX + curveRadius * U.sin(iteration),
        summedPositionY + curveHeight * Math.abs(iteration - curveStart),
        summedPositionZ + curveRadius * U.cos(iteration),
        -iteration + angle);
        if (Math.abs(iteration - curveEnd) < iterationRate) {
         break;
        }
       }
      } else {
       TE.addTrackPart(s, model, summedPositionX, summedPositionY, summedPositionZ, Double.NaN);
      }
     } else if (s.startsWith("vehicleModel")) {
      TE.trackParts.add(new TrackPart(I.vehicleModels.get(I.getVehicleIndex(U.getString(s, 0))), U.getValue(s, 1), U.getValue(s, 3), U.getValue(s, 2), U.getValue(s, 4), true));
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
   if (name.equals(D.Maps.ghostCity)) {
    for (n = 5; n < 365; n += 10) {
     TE.instanceSize = 2500 + U.random(2500.);
     TE.instanceScale[0] = 1 + U.random(2.);
     TE.instanceScale[1] = 1 + U.random(2.);
     TE.instanceScale[2] = 1 + U.random(2.);
     double calculatedX = 112500 * -U.sin(n),
     calculatedZ = 112500 * U.cos(n);
     TE.trackParts.add(new TrackPart(TE.Models.cube.name(), calculatedX, 0, calculatedZ, n - 90, TE.instanceSize, TE.instanceScale));
    }
   } else if (name.equals("World's Biggest Parking Lot")) {
    for (n = 0; n < I.vehicleModels.size(); n++) {
     double xRandom = U.randomPlusMinus(30000.), zRandom = U.randomPlusMinus(30000.), randomXZ = U.randomPlusMinus(180.);
     I.userRandomRGB = U.getColor(U.random(), U.random(), U.random());
     /*{//<-This block's used to get the V.E. logo image on the World's Biggest Parking Lot
      double vehicleCircle = 360 * n / (double) I.vehicleModels.size();
      xRandom = 4000 * U.sin(vehicleCircle);
      zRandom = 4000 * U.cos(vehicleCircle);
      randomXZ = -vehicleCircle + 180;
     }*/
     TE.trackParts.add(new TrackPart(I.vehicleModels.get(n), xRandom, 0, zRandom, randomXZ, true));
     TE.points.add(new Point());
     TE.points.get(TE.points.size() - 1).X = xRandom;
     TE.points.get(TE.points.size() - 1).Z = zRandom;
     TE.points.get(TE.points.size() - 1).type = Point.Type.waypoint;
     TE.waypoints.add(new Waypoint());
     TE.waypoints.get(TE.waypoints.size() - 1).X = xRandom;
     TE.waypoints.get(TE.waypoints.size() - 1).Z = zRandom;
     TE.waypoints.get(TE.waypoints.size() - 1).type = Waypoint.Type.passAny;
     TE.waypoints.get(TE.waypoints.size() - 1).location = TE.points.size() - 1;
     TE.trackParts.get(TE.trackParts.size() - 1).waypointNumber = TE.waypoints.size() - 1;
    }
   }
   if (Pool.type == Pool.Type.lava) {
    for (var part : Tsunami.parts) {//Setting the illumination here in case the lava pool gets called AFTER tsunami definition
     ((PhongMaterial) part.C.getMaterial()).setSelfIlluminationMap(Phong.getSelfIllumination(E.lavaSelfIllumination));
    }
   }
   Bonus.load();
  } else if (UI.status == UI.Status.mapLoadPass4) {
   for (var TP : TE.trackParts) {
    TP.setInitialSit();
   }
   RepairPoint.notifyDuplicates();
   for (var FM : TE.mounds) {
    FM.setInitialSit();
   }
   if (!Viewer.inUse) {
    Sounds.loadSoftwareBasedGlobals();
    for (n = I.vehiclesInMatch; --n >= 0; ) {
     I.vehicles.add(null);
    }
    I.vehicles.set(I.userPlayerIndex, new Vehicle(VS.chosen[I.userPlayerIndex], I.userPlayerIndex, true));//<-User player set first to ensure their vehicle sound's heard (for Linux)
    for (n = I.vehiclesInMatch; --n >= 0; ) {//<-Reverse loop ideal, as it's more important the user hears red-team vehicles than green
     if (n != I.userPlayerIndex) {
      I.vehicles.set(n, new Vehicle(VS.chosen[n], n, true));
     }
    }
    Sounds.removeExtraneousGlobals();
   }
   addTransparentNodes();
   Match.reset();
  }
  U.fillRGB(0, 0, 0, UI.colorOpacity.maximal);
  U.fillRectangle(.5, .5, 1, 1);
  U.font(.025);
  U.fillRGB(1);
  U.text(Tournament.stage > 0 ? "Round " + Tournament.stage + (Tournament.stage > 5 ? "--Overtime!" : "") : "", .425);
  U.text(name, .475);
  String loadText =
  UI.status == UI.Status.mapLoadPass0 ? "Removing Previous Content" :
  UI.status == UI.Status.mapLoadPass1 ? "Loading Properties & Scenery" :
  UI.status == UI.Status.mapLoadPass2 ? "Adding Track Parts" :
  "Adding " + I.vehiclesInMatch + " Vehicle(s)";//<-Vehicle names can't be displayed here because this is at least one frame ahead of vehicles actually getting loaded
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

 private static void addTransparentNodes() {
  for (var instance : Boulder.instances) {
   instance.addTransparentNodes();
  }
  for (var repairPoint : RepairPoint.instances) {
   Nodes.add(repairPoint.pulse);
  }
  for (var vehicle : I.vehicles) {
   vehicle.addTransparentNodes();
  }
  for (int n = Fog.spheres.size(); --n >= 0; ) {//<-Adding spheres in forward order doesn't layer fog correctly
   Nodes.add(Fog.spheres.get(n));
  }
  for (int n = Atmosphere.rings.size(); --n >= 0; ) {//<-Adding in forward order doesn't layer correctly
   Nodes.add(Atmosphere.rings.get(n));
  }
 }

 public static int getName(String s) {
  int n;
  String s1;
  for (n = 0; n < maps.size(); n++) {
   try (BufferedReader BR = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getMapFile(n)), U.standardChars))) {
    for (String s2; (s2 = BR.readLine()) != null; ) {
     s1 = s2.trim();
     name = s1.startsWith(D.name) ? U.getString(s1, 0) : name;
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

 private static FileInputStream getMapFile(int n) {
  File F = new File(folder + File.separator + maps.get(n));
  if (!F.exists()) {
   F = new File(folder + File.separator + U.userSubmittedFolder + File.separator + maps.get(n));
  }
  if (!F.exists()) {
   map = 0;
   F = new File(folder + File.separator + maps.get(0));
  }
  try {
   return new FileInputStream(F);
  } catch (FileNotFoundException E) {
   return null;
  }
 }

 public static void runQuickSelect(boolean gamePlay) {
  UI.scene.setCursor(Cursor.CROSSHAIR);
  if (Network.mode == Network.Mode.JOIN) {
   U.font(.03);
   U.text(UI.Please_Wait_For_ + UI.playerNames[0] + " to Select Map..", .5, .5);
  } else {
   String mapMaker;
   name = mapMaker = "";
   if (Tournament.stage > 0) {
    map = U.random(maps.size());
   }
   String s;
   try (BufferedReader BR = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getMapFile(map)), U.standardChars))) {
    for (String s1; (s1 = BR.readLine()) != null; ) {
     s = s1.trim();
     name = s.startsWith(D.name) ? U.getString(s, 0) : name;
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
  UI.scene.setCursor(Cursor.CROSSHAIR);
  Camera.runAroundTrack();
  U.rotate(Camera.PC, Camera.YZ, -Camera.XZ);
  E.run(gamePlay);
  for (var vehicle : I.vehicles) {
   vehicle.runRender(gamePlay);
  }
  boolean renderALL = E.renderType == E.RenderType.ALL;
  for (var trackPart : TE.trackParts) {
   trackPart.runGraphics(renderALL);
  }
  for (var repairPoint : RepairPoint.instances) {
   repairPoint.run();
  }
  for (var mound : TE.mounds) {
   mound.runGraphics();
  }
  U.font(.015);
  U.fillRGB(Ground.RGB.invert());
  U.text(UI._LAST, .2, .75);
  U.text(UI.NEXT_, .8, .75);
  U.font(.02);
  U.text("GO", .75);
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
   Sounds.reset();
  }
  if (Keys.escape) {
   UI.escapeToLast(true);
  }
  Bonus.run();
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