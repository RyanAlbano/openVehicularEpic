package ve.trackElements;

import javafx.scene.paint.PhongMaterial;
import ve.environment.*;
import ve.instances.Core;
import ve.instances.I;
import ve.trackElements.trackParts.RepairPoint;
import ve.trackElements.trackParts.TrackPart;
import ve.ui.Maps;
import ve.ui.options.Options;
import ve.utilities.D;
import ve.utilities.Images;
import ve.utilities.Phong;
import ve.utilities.U;
import ve.utilities.sound.Sounds;
import ve.vehicles.Physics;
import ve.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public enum TE {//TrackElements
 ;
 public static double instanceSize = 1;
 public static double[] instanceScale = {1, 1, 1};
 public static double randomX, randomY, randomZ;
 public static final long wrapDistance = 40000;
 private static long currentWaypoint;//<-Just keep for now
 public static boolean lapWaypoint;
 public static final List<TrackPart> trackParts = new ArrayList<>();
 public static final Collection<FrustumMound> mounds = new ArrayList<>();
 public static final List<Point> points = new ArrayList<>();
 public static final List<Waypoint> waypoints = new ArrayList<>();

 public enum MS {//MapSelect
  ;
  public static int point;
  public static long X, Y, Z;
  public static double timer;
 }

 public enum Models {
  road, roadshort, roadturn, roadbendL, roadbendR, roadend, roadincline, offroad, offroadshort, offroadturn, offroadbump, offroadrocky, offroadend, offroadincline, mixroad,
  waypoint, repair,
  ramp, rampcurved, ramptrapezoid, ramptriangle, rampwall, quarterpipe, pyramid, plateau,
  offramp, offplateau/*<-todo-Remove 'offPlateau' and switch to mounds?*/, mound, pavedmound,//<-'(paved)mound' is needed!
  floor, offfloor, wall, offwall, cube, offcube, spike, spikes, block, blocktower, border, beam, grid, tunnel, lift, speedgate, slowgate, antigravity,
  tree0, tree1, tree2, treepalm, cactus0, cactus1, cactus2, rainbow, crescent//<-Rainbow, crescent, etc. are not really 'track elements' but good enough
 }

 public enum Paved {
  ;
  public static final double globalShade = .55;
  public static final PhongMaterial universal = new PhongMaterial();

  static {
   Phong.setDiffuseRGB(universal, globalShade);
   Phong.setSpecularRGB(universal, E.Specular.Colors.standard);
   universal.setSpecularPower(E.Specular.Powers.standard);
  }

  public static void setTexture() {
   universal.setDiffuseMap(Images.get(D.paved));
   universal.setSpecularMap(Images.get(D.paved));
   universal.setBumpMap(Images.getNormalMap(D.paved));
  }
 }

 public static void addTrackPart(String s, TE.Models model, double summedPositionX, double summedPositionY, double summedPositionZ, double optionalRotation) {
  double[] X = {summedPositionX};
  double[] Y = {summedPositionY};
  double[] Z = {summedPositionZ};
  double rotation;
  try {
   rotation = Double.isNaN(optionalRotation) ? U.getValue(s, 4) : optionalRotation;
  } catch (RuntimeException ignored) {
   rotation = 0;
  }
  if (Maps.name.equals(D.Maps.ghostCity) && model == Models.cube && instanceSize == 10000) {
   instanceScale[1] = 1 + U.random(3.);
  }
  if (Maps.name.equals("Meteor Fields") && model == Models.ramp) {
   if (rotation == 0) {
    X[0] = U.randomPlusMinus(2500.);
    Z[0] = 5000 + U.random(20000.);
    Z[0] *= U.random() < .5 ? -1 : 1;
   } else {
    Z[0] = U.randomPlusMinus(2500.);
    X[0] = 5000 + U.random(20000.);
    X[0] *= U.random() < .5 ? -1 : 1;
   }
   rotation += U.random() < .5 ? 180 : 0;
  }
  if (model == Models.waypoint || model == Models.repair) {
   forceTrackPartOutsideExistingParts(s, X, Y, Z, (String[]) null);//<-Always call, since in this case it's only used to set the mound sit
   if (Maps.name.equals("Pyramid Paradise")) {
    forceTrackPartOutsideExistingParts(s, X, Y, Z, Models.pyramid.name());
   } else if (Maps.name.equals("Military Base")) {
    forceTrackPartOutsideExistingParts(s, X, Y, Z, Models.cube.name(), Models.ramptriangle.name());
   }
  }
  if (model == Models.waypoint) {
   if (Maps.name.equals(D.Maps.highlands)) {
    if (U.random() < .5) {
     (U.random() < .5 ? X : Z)[0] += U.random() < .5 ? 100000 : -100000;
     Y[0] -= 25000;
    }
   } else if (Maps.name.equals(D.Maps.ghostCity)) {
    if (U.random() < .5) {
     rotation = 0;
     Z[0] = U.randomPlusMinus(150000.);
     long randomPosition = U.random(4);
     if (randomPosition == 0) {
      X[0] = -150000;
     } else if (randomPosition == 1 || randomPosition == 2) {
      X[0] = randomPosition == 1 ? -2000 : 2000;
      while (Math.abs(Z[0]) < 110000) {
       Z[0] = U.randomPlusMinus(150000.);
      }
     } else {
      X[0] = randomPosition == 3 ? 150000 : X[0];
     }
    } else {
     rotation = 90;
     X[0] = U.randomPlusMinus(150000.);
     long randomPosition = U.random(3);
     if (randomPosition == 0) {
      Z[0] = -150000;
     } else if (randomPosition == 1) {
      Z[0] = 0;
      while (Math.abs(X[0]) < 110000) {
       X[0] = U.randomPlusMinus(150000.);
      }
     }
     Z[0] = randomPosition == 2 ? 150000 : Z[0];
    }
   } else if (Maps.name.equals("the Machine is Out of Control")) {
    boolean inside = true;
    while (inside) {
     inside = Math.abs(X[0]) < 30000 && Math.abs(Z[0]) < 30000;
     for (var trackPart : trackParts) {
      if (U.equals(trackPart.modelName, Models.pyramid.name(), Models.cube.name(), Models.ramptriangle.name()) &&
      Math.abs(X[0] - trackPart.X) <= trackPart.renderRadius && Math.abs(Z[0] - trackPart.Z) <= trackPart.renderRadius) {
       inside = true;
       break;
      }
     }
     if (inside) {
      X[0] = U.getValue(s, 1) + U.randomPlusMinus(randomX);
      Z[0] = U.getValue(s, 2) + U.randomPlusMinus(randomZ);
      Y[0] = U.getValue(s, 3) + U.randomPlusMinus(randomY);
     }
    }
   } else if (Maps.name.equals("DoomsDay")) {
    while (U.distance(X[0], 0, Z[0], 0) <= Volcano.radiusBottom) {
     X[0] = U.getValue(s, 1) + U.randomPlusMinus(randomX);
     Z[0] = U.getValue(s, 2) + U.randomPlusMinus(randomZ);
    }
   }
  }
  if (U.equals(U.getString(s, 0), Models.mound.name(), Models.pavedmound.name())) {
   try {
    mounds.add(new FrustumMound(X[0], Z[0], Y[0],
    U.getValue(s, 4) * instanceSize, U.getValue(s, 5) * instanceSize, U.getValue(s, 6) * instanceSize,
    false, U.getString(s, 0).contains(D.paved), true));
   } catch (RuntimeException E) {
    mounds.add(new FrustumMound(X[0], Z[0], Y[0],
    U.getValue(s, 4) * U.random(instanceSize), U.getValue(s, 4) * U.random(instanceSize), U.getValue(s, 4) * U.random(instanceSize),
    false, U.getString(s, 0).contains(D.paved), true));
   }
  } else if (model == Models.repair) {
   try {
    RepairPoint.instances.add(new RepairPoint.Instance(X[0], Y[0], Z[0], U.getValue(s, 4)));
   } catch (Exception E) {
    RepairPoint.instances.add(new RepairPoint.Instance(X[0], Y[0], Z[0], 500));
   }
  } else if (model != null) {
   trackParts.add(new TrackPart(model.name(), X[0], Y[0], Z[0], rotation, instanceSize, instanceScale));
  }
  if (model == Models.rainbow) {
   trackParts.get(trackParts.size() - 1).rainbow = true;
  } else if (model == Models.crescent) {
   Sun.enforceCrescent();
  } else if (model == Models.waypoint) {
   points.add(new Point());
   points.get(points.size() - 1).X = X[0];
   points.get(points.size() - 1).Y = Y[0];
   points.get(points.size() - 1).Z = Z[0];
   points.get(points.size() - 1).type = Point.Type.waypoint;
   waypoints.add(new Waypoint());
   waypoints.get(waypoints.size() - 1).X = X[0];
   waypoints.get(waypoints.size() - 1).Y = Y[0];
   waypoints.get(waypoints.size() - 1).Z = Z[0];
   waypoints.get(waypoints.size() - 1).type = isSidewaysXZ(rotation) ? Waypoint.Type.passX : Waypoint.Type.passZ;
   waypoints.get(waypoints.size() - 1).location = points.size() - 1;
   trackParts.get(trackParts.size() - 1).waypointNumber = waypoints.size() - 1;
  } else if (s.contains(").")) {
   points.add(new Point());
   points.get(points.size() - 1).X = X[0];
   points.get(points.size() - 1).Y = Y[0];
   points.get(points.size() - 1).Z = Z[0];
   if (s.contains(")...")) {
    points.get(points.size() - 1).type = Point.Type.mustPassAbsolute;
   } else if (s.contains(")..")) {
    points.get(points.size() - 1).type = Point.Type.mustPassIfClosest;
   }
  }
 }

 /**
  * Forces the current map-loading X, Y, and Z values to move outside of any existing map parts that are defined in the String... parameter.
  * In the case of mounds, the Y-value is simply set to sit to the mounds' upper terrain.
  */
 private static void forceTrackPartOutsideExistingParts(CharSequence source, double[] X, double[] Y, double[] Z, String... targets) {
  Core set = new Core(X[0], Y[0], Z[0]);
  E.setMoundSit(set, false);
  X[0] = set.X;
  Y[0] = set.Y;
  Z[0] = set.Z;
  double tolerance = 1.05;//<-Not a sufficient distance away otherwise
  boolean inside = true;
  while (inside) {
   inside = false;
   if (targets != null) {
    for (var trackPart : trackParts) {
     if (U.contains(trackPart.modelName, targets) &&
     Math.abs(Y[0] - trackPart.Y) <= trackPart.boundsY * tolerance &&
     Math.abs(X[0] - trackPart.X) <= trackPart.boundsX * tolerance &&
     Math.abs(Z[0] - trackPart.Z) <= trackPart.boundsZ * tolerance) {
      inside = true;
      break;
     }
    }
   }
   if (inside) {
    X[0] = U.getValue(source, 1) + U.randomPlusMinus(randomX);
    Z[0] = U.getValue(source, 2) + U.randomPlusMinus(randomZ);
    Y[0] = U.getValue(source, 3) + U.randomPlusMinus(randomY);
   }
  }
 }

 public static int getTrackPartIndex(String s) {//<-Could probably be chucked
  try {
   return Models.valueOf(s).ordinal();
  } catch (IllegalArgumentException E) {
   return -1;
  }
 }

 public static String getTrackPartName(int in) {
  return Models.values()[in].name();
 }

 public static void setVehicleMatchStartPlacement(Vehicle V) {
  V.Y = V.XY = V.YZ = 0;
  V.X = U.random(Math.min(50000., MapBounds.right)) + U.random(Math.max(-50000., MapBounds.left));
  V.Z = U.random(Math.min(50000., MapBounds.forward)) + U.random(Math.max(-50000., MapBounds.backward));
  V.XZ = !V.isFixed()/*<-Fixed units face forward for less confusing placement*/ && Maps.randomVehicleStartAngle ? U.randomPlusMinus(180.) : 0;
  boolean regularPlacement = !V.isFixed() && !V.isNuclear();
  if (Maps.name.equals("Vicious Versus V3") && I.vehiclesInMatch > 1) {
   boolean green = V.index < I.halfThePlayers();
   if (green) {
    V.Z = -10000;
    V.XZ = 0;
   } else {
    V.Z = 10000;
    V.XZ = 180;
   }
   if (I.vehiclesInMatch < 3) {
    V.X = 0;
   } else {
    V.X = (V.index - (green ? 0 : (I.vehiclesInMatch * .5))) * 2000;
    V.X -= 2000 * (I.vehiclesInMatch * .5) * .5 - 1000;
   }
  } else if (Maps.name.equals("Moonlight")) {
   if (!V.dealsMassiveDamage() && !V.isFixed()) {
    V.X *= V.X < 0 ? -1 : 1;
    V.Z *= V.Z < 0 ? -1 : 1;
   }
  } else if (Maps.name.equals(D.Maps.testOfDamage)) {
   if (!V.dealsMassiveDamage() && !V.isFixed()) {
    V.X = U.random(MapBounds.right);
    V.Z = U.random(MapBounds.backward);
   }
  } else if (Maps.name.equals(D.Maps.vehicularFalls)) {
   V.Y -= 100000;
   if (!V.isFixed()) {
    V.Z = U.random(-10000.) + U.random(30000.);
    V.X = 0;
   }
  } else if (Maps.name.equals(D.Maps.highlands)) {
   V.X = U.randomPlusMinus(100000);
   V.Z = U.randomPlusMinus(100000);
  } else if (Maps.name.equals("Vehicular Colosseum")) {
   V.X = U.randomPlusMinus(30000);
   V.Z = U.randomPlusMinus(30000);
  } else if (Maps.name.equals(D.Maps.circleRaceXL)) {
   V.Z += 320000;
  } else if (Maps.name.equals(D.Maps.XYLand)) {
   V.X = V.isFixed() ? V.X : U.random(23000.) - U.random(25000.);
  } else if (Maps.name.equals(D.Maps.matrix2x3)) {
   if (regularPlacement) {
    V.X = U.randomPlusMinus(14000.);
    V.Z = -U.random(31000.);
   }
  } else if (Maps.name.equals("Cold Fury")) {
   V.Y = -4000;
  } else if (Maps.name.equals("the Test of Submersion")) {
   if (regularPlacement) {
    V.X = 0;
    V.Z = U.randomPlusMinus(20000.);
   }
  } else if (Maps.name.equals(D.Maps.tunnelOfDoom)) {
   if (regularPlacement) {
    V.X = 0;//<-Zero is best--train was 'beached' against walls at match start
    V.Z = U.random(6000.) - U.random(10000.);
   }
  } else if (Maps.name.equals(D.Maps.methodMadness)) {
   V.X = U.random() < .5 ? -2000 : 2000;
   V.Z = U.randomPlusMinus(20000.);
  } else if (Maps.name.equals(D.Maps.theMaze)) {
   if (!V.isFixed()) {
    V.X = V.Z = 0;
   }
  } else if (Maps.name.equals(D.Maps.volcanicProphecy)) {
   V.X *= 2;
   V.Z *= 2;
  } else if (Maps.name.equals(D.Maps.speedway2000000)) {
   boolean random = U.random() < .5;
   V.XZ = random ? 180 : 0;
   V.Z += random ? 1000000 : -1000000;
  } else if (Maps.name.equals(D.Maps.ghostCity)) {
   V.X *= 4;
   V.Z *= 4;
   if (!V.isFixed()) {
    V.Y = -1000;
    V.X = U.random() < .5 ? 2000 : -2000;
   }
  } else if (Maps.name.equals("Open Ocean")) {
   V.X *= 4;
   V.Z *= 4;
   if (!V.isFixed()) {
    V.X = U.random() < .5 ? 2000 : -2000;
   }
  } else if (Maps.name.equals(D.Maps.summitOfEpic)) {
   V.X = regularPlacement ? 0 : V.X;
   boolean random = U.random() < .5;
   V.XZ = random ? 180 : 0;
   V.Z = random ? 1050000 : -1050000;
   V.Z += U.randomPlusMinus(25000.);
  } else if (Maps.name.equals("Parallel Universe Portal")) {
   V.Z = 0;
  }
  if (E.gravity == 0) {
   if (Maps.name.equals(D.Maps.outerSpace1)) {
    V.X = U.randomPlusMinus(500.);
    V.Z = U.random(2000.) - U.random(4000.);
    if (V.isNuclear()) {
     V.X = 0;
     V.Z = 100500;
    }
    if (V.isFixed()) {
     V.X = U.randomPlusMinus(50000.);
     V.Z = U.randomPlusMinus(50000.);
     V.Y = U.randomPlusMinus(50000.);
    }
   } else {
    V.Y = U.randomPlusMinus(50000.);
   }
   if (Maps.name.equals("Outer Space V3")) {
    V.Y = 0;
    double[] setX = {0}, setZ = {50000};
    U.rotate(setX, setZ, U.random(360.));
    V.X = setX[0];
    V.Z = setZ[0];
   }
  }
  if (Maps.name.equals("Black Hole")) {
   V.X = V.Y = V.Z = 0;
  }
  E.setMoundSit(V, true);
  V.Y -= V.isFixed() ? 0 : V.clearanceY;
 }

 public static void runVehicleInteraction(Vehicle V, boolean replay) {
  if (!points.isEmpty()) {
   Point P = points.get(V.point);
   if (P.type == Point.Type.mustPassAbsolute && V.P.mode != Physics.Mode.fly) {
    V.point += U.distance(V, P) < 500 ? 1 : 0;
   } else if (P.type != Point.Type.waypoint &&
   (U.distanceXZ(V, P) < 500 || (V.AI.skipStunts && P.type != Point.Type.mustPassIfClosest) || (!waypoints.isEmpty() && !Maps.name.equals(D.Maps.devilsStairwell) && U.distance(V, waypoints.get(V.waypointsPassed)) <= U.distance(P, waypoints.get(V.waypointsPassed))))) {
    V.point++;
   }
  }
  if (!waypoints.isEmpty() && !V.phantomEngaged) {
   double checkSize = Maps.name.equals(D.Maps.circleRaceXL) ? V.P.speed : 0;
   Waypoint WP = waypoints.get(V.waypointsPassed);
   if ((WP.type == Waypoint.Type.passZ || WP.type == Waypoint.Type.passAny) &&
   Math.abs(V.Z - WP.Z) < (60 + checkSize) + Math.abs(V.speedZ) * U.tick && Math.abs(V.X - WP.X) < 700 && Math.abs((V.Y - WP.Y) + 350) < 450) {
    V.waypointsPassed++;
    V.point++;
    if (V.index == I.vehiclePerspective) {
     //Some visual indicator here as well, maybe
     if (Options.headsUpDisplay) {
      Sounds.waypoint.play(0);
     }
    }
    V.scoreWaypoint += replay ? 0 : 1;
    if (V.waypointsPassed >= waypoints.size()) {
     V.scoreLap += replay ? 0 : 1;
     V.waypointsPassed = V.point = 0;
    }
   }
   if ((WP.type == Waypoint.Type.passX || WP.type == Waypoint.Type.passAny) &&
   Math.abs(V.X - WP.X) < (60 + checkSize) + Math.abs(V.speedX) * U.tick && Math.abs(V.Z - WP.Z) < 700 && Math.abs((V.Y - WP.Y) + 350) < 450) {
    V.waypointsPassed++;
    V.point++;
    if (V.index == I.vehiclePerspective) {
     //Some visual indicator here as well, maybe
     if (Options.headsUpDisplay) {
      Sounds.waypoint.play(0);
     }
    }
    V.scoreWaypoint += replay ? 0 : 1;
    if (V.waypointsPassed >= waypoints.size()) {
     V.scoreLap += replay ? 0 : 1;
     V.waypointsPassed = V.point = 0;
    }
   }
   V.point = V.waypointsPassed > 0 ? (int) U.clamp(waypoints.get(V.waypointsPassed - 1).location + 1, V.point, waypoints.get(V.waypointsPassed).location) : V.point;
   if (V.index == I.vehiclePerspective) {
    currentWaypoint = V.waypointsPassed;
    lapWaypoint = V.waypointsPassed >= waypoints.size() - 1;
   }
  }
  V.point = V.point >= points.size() || V.point < 0 ? 0 : V.point;
 }

 public static void runVehicleRepairPointInteraction(Vehicle V, boolean gamePlay) {
  if (V.repairSpheres.get(U.random(V.repairSpheres.size())).stage <= 0 && !V.phantomEngaged) {
   for (var part : RepairPoint.instances) {
    if (Physics.advancedCollisionCheck(V, part, part.absoluteRadius)) {
     V.repair(gamePlay);
    }
   }
  }
 }

 private static boolean isSidewaysXZ(double angleXZ) {
  return Math.abs(U.cos(angleXZ)) < U.sin45;
 }

 public static void reset() {
  trackParts.clear();
  mounds.clear();
  points.clear();
  waypoints.clear();
  RepairPoint.instances.clear();
  Bonus.reset();
 }
}
