package ve.trackElements;

import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import ve.environment.*;
import ve.instances.I;
import ve.trackElements.trackParts.TrackPart;
import ve.ui.*;
import ve.utilities.*;
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
 public static long currentCheckpoint;
 public static boolean lapCheckpoint;
 private static boolean repairPointsExist;
 public static final List<TrackPart> trackParts = new ArrayList<>();
 public static final Collection<FrustumMound> mounds = new ArrayList<>();
 public static final List<Point> points = new ArrayList<>();
 public static final List<Checkpoint> checkpoints = new ArrayList<>();
 public static final Bonus bonus = new Bonus();

 public enum MS {//MapSelect
  ;
  public static int point;
  public static long X, Y, Z;
  public static double timer;
 }

 public enum Models {
  road, roadshort, roadturn, roadbendL, roadbendR, roadend, roadincline, offroad, offroadshort, offroadturn, offroadbump, offroadrocky, offroadend, offroadincline, mixroad,
  checkpoint, repair,
  ramp, rampcurved, ramptrapezoid, ramptriangle, rampwall, quarterpipe, pyramid, plateau,
  offramp, offplateau/*todo-Remove 'offPlateau' and switch to mounds?*/, mound, pavedmound,//<-'mound' is needed!
  floor, offfloor, wall, offwall, cube, offcube, spike, spikes, block, blocktower, border, beam, grid, tunnel, lift, speedgate, slowgate, antigravity,
  tree0, tree1, tree2, treepalm, cactus0, cactus1, cactus2, rainbow, crescent//<-Rainbow, crescent, etc. are not really 'track elements' but good enough
 }

 public enum Paved {
  ;
  public static final double globalShade = .55;
  public static final PhongMaterial universal = new PhongMaterial();
  public static final Image[] lowResolution = new Image[2];

  static {
   universal.setDiffuseMap(Images.get(SL.paved));
   universal.setSpecularMap(Images.get(SL.paved));
   universal.setBumpMap(Images.getNormalMap(SL.paved));
   Phong.setDiffuseRGB(universal, globalShade);
   Phong.setSpecularRGB(universal, E.Specular.Colors.standard);
   universal.setSpecularPower(E.Specular.Powers.standard);
   lowResolution[0] = Images.getLowResolution(Images.get(SL.paved));
   lowResolution[1] = Images.getLowResolution(Images.getNormalMap(SL.paved));
  }
 }

 public static void addTrackPart(String s, int listNumber, double summedPositionX, double summedPositionY, double summedPositionZ, double optionalRotation) {
  double[] X = {summedPositionX};
  double[] Y = {summedPositionY};
  double[] Z = {summedPositionZ};
  double rotation;
  try {
   rotation = Double.isNaN(optionalRotation) ? U.getValue(s, 4) : optionalRotation;
  } catch (RuntimeException ignored) {
   rotation = 0;
  }
  instanceScale[1] = Maps.name.equals(SL.Maps.ghostCity) && listNumber == getTrackPartIndex(Models.cube.name()) && instanceSize == 10000 ? 1 + U.random(3.) : instanceScale[1];
  if (Maps.name.equals("Meteor Fields") && listNumber == getTrackPartIndex(Models.ramp.name())) {
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
  if ((listNumber == getTrackPartIndex(Models.checkpoint.name()) || listNumber == getTrackPartIndex(Models.repair.name()))) {
   if (Maps.name.equals("Pyramid Paradise")) {
    forceTrackPartOutsideExistingParts(s, X, Y, Z, Models.pyramid.name());
   } else if (U.equals(Maps.name, "the Forest", "Volatile Sands")) {
    forceTrackPartOutsideExistingParts(s, X, Y, Z, (String[]) null);
   } else if (Maps.name.equals("Military Base")) {
    forceTrackPartOutsideExistingParts(s, X, Y, Z, Models.cube.name(), Models.ramptriangle.name());
   }
  }
  if (listNumber == getTrackPartIndex(Models.checkpoint.name())) {
   if (Maps.name.equals(SL.Maps.highlands)) {
    if (U.random() < .5) {
     if (U.random() < .5) {
      X[0] += U.random() < .5 ? 100000 : -100000;
      Y[0] -= 25000;
     } else {
      Z[0] += U.random() < .5 ? 100000 : -100000;
      Y[0] -= 25000;
     }
    }
   } else if (Maps.name.equals(SL.Maps.ghostCity)) {
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
     for (TrackPart trackpart : trackParts) {
      inside = (trackpart.modelNumber == getTrackPartIndex(Models.pyramid.name()) || trackpart.modelNumber == getTrackPartIndex(Models.cube.name()) || trackpart.modelNumber == getTrackPartIndex(Models.ramptriangle.name())) &&
      Math.abs(X[0] - trackpart.X) <= trackpart.renderRadius && Math.abs(Z[0] - trackpart.Z) <= trackpart.renderRadius || inside;
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
    false, U.getString(s, 0).contains(SL.paved), true));
   } catch (RuntimeException E) {
    mounds.add(new FrustumMound(X[0], Z[0], Y[0],
    U.getValue(s, 4) * U.random(instanceSize), U.getValue(s, 4) * U.random(instanceSize), U.getValue(s, 4) * U.random(instanceSize),
    false, U.getString(s, 0).contains(SL.paved), true));
   }
  } else {
   trackParts.add(new TrackPart(listNumber, X[0], Y[0], Z[0], rotation, instanceSize, instanceScale));
  }
  if (listNumber == getTrackPartIndex(Models.rainbow.name())) {
   trackParts.get(trackParts.size() - 1).rainbow = true;
  } else if (listNumber == getTrackPartIndex(Models.crescent.name())) {
   Nodes.remove(Sun.S);
  } else if (listNumber == getTrackPartIndex(Models.checkpoint.name())) {
   points.add(new Point());
   points.get(points.size() - 1).X = X[0];
   points.get(points.size() - 1).Y = Y[0];
   points.get(points.size() - 1).Z = Z[0];
   points.get(points.size() - 1).type = Point.Type.checkpoint;
   checkpoints.add(new Checkpoint());
   checkpoints.get(checkpoints.size() - 1).X = X[0];
   checkpoints.get(checkpoints.size() - 1).Y = Y[0];
   checkpoints.get(checkpoints.size() - 1).Z = Z[0];
   checkpoints.get(checkpoints.size() - 1).type = isSidewaysXZ(rotation) ? Checkpoint.Type.passX : Checkpoint.Type.passZ;
   checkpoints.get(checkpoints.size() - 1).location = points.size() - 1;
   trackParts.get(trackParts.size() - 1).checkpointNumber = checkpoints.size() - 1;
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
  repairPointsExist = listNumber == getTrackPartIndex(Models.repair.name()) || repairPointsExist;
 }

 private static void forceTrackPartOutsideExistingParts(CharSequence source, double[] X, double[] Y, double[] Z, String... targets) {
  boolean inside = true;
  double tolerance = 1.05;//<-Not a sufficient distance away otherwise
  while (inside) {
   inside = false;
   for (FrustumMound mound : mounds) {
    inside = !mound.wraps && U.distance(X[0], mound.X, Z[0], mound.Z) <= mound.mound.getMajorRadius() * tolerance || inside;
   }
   if (targets != null) {
    for (TrackPart trackPart : trackParts) {
     inside = U.contains(trackPart.modelName, targets) &&
     Math.abs(Y[0] - trackPart.Y) <= trackPart.boundsY * tolerance &&
     Math.abs(X[0] - trackPart.X) <= trackPart.boundsX * tolerance &&
     Math.abs(Z[0] - trackPart.Z) <= trackPart.boundsZ * tolerance ||
     inside;
    }
   }
   if (inside) {
    X[0] = U.getValue(source, 1) + U.randomPlusMinus(randomX);
    Z[0] = U.getValue(source, 2) + U.randomPlusMinus(randomZ);
    Y[0] = U.getValue(source, 3) + U.randomPlusMinus(randomY);
   }
  }
 }

 public static int getTrackPartIndex(String s) {
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
  V.XZ = !V.isFixed() && Maps.randomVehicleStartAngle ? U.randomPlusMinus(180.) : 0;//<-Fixed units ALWAYS face forward for less confusing placement
  if (Maps.name.equals("Vicious Versus V3") && I.vehiclesInMatch > 1) {
   boolean green = V.index < I.vehiclesInMatch >> 1;
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
  } else if (Maps.name.equals(SL.Maps.testOfDamage)) {
   if (!V.dealsMassiveDamage() && !V.isFixed()) {
    V.X = U.random(MapBounds.right);
    V.Z = U.random(MapBounds.backward);
   }
  } else if (Maps.name.equals(SL.Maps.vehicularFalls)) {
   V.Y -= 100000;
   if (!V.isFixed()) {
    V.Z = U.random(-10000.) + U.random(30000.);
    V.X = 0;
   }
  } else if (Maps.name.equals(SL.Maps.highlands)) {
   V.X = U.randomPlusMinus(100000);
   V.Z = U.randomPlusMinus(100000);
   V.Y = -175000;
  } else if (Maps.name.equals(SL.Maps.circleRaceXL)) {
   V.Z += 320000;
  } else if (Maps.name.equals(SL.Maps.XYLand)) {
   V.X = V.isFixed() ? V.X : U.random(23000.) - U.random(25000.);
  } else if (Maps.name.equals(SL.Maps.matrix2x3)) {
   if (!V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name()) && !V.isFixed()) {
    V.X = U.randomPlusMinus(14000.);
    V.Z = -U.random(31000.);
   }
  } else if (Maps.name.equals("Cold Fury")) {
   V.Y = -4000;
  } else if (Maps.name.equals(SL.Maps.tunnelOfDoom)) {
   if (!V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name()) && !V.isFixed()) {
    V.X = U.randomPlusMinus(700.);
    V.Z = U.random(6000.) - U.random(10000.);
   }
  } else if (Maps.name.equals(SL.Maps.everybodyEverything)) {
   V.X = U.random() < .5 ? -2000 : 2000;
   V.Z = U.randomPlusMinus(20000.);
  } else if (Maps.name.equals(SL.Maps.theMaze)) {
   if (!V.isFixed()) {
    V.X = V.Z = 0;
   }
  } else if (Maps.name.equals(SL.Maps.volcanicProphecy)) {
   V.X *= 2;
   V.Z *= 2;
  } else if (Maps.name.equals(SL.Maps.speedway2000000)) {
   boolean random = U.random() < .5;
   V.XZ = random ? 180 : 0;
   V.Z += random ? 1000000 : -1000000;
  } else if (Maps.name.equals(SL.Maps.ghostCity)) {
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
  } else if (Maps.name.equals(SL.Maps.summitOfEpic)) {
   V.X = !V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name()) && !V.isFixed() ? 0 : V.X;
   boolean random = U.random() < .5;
   V.XZ = random ? 180 : 0;
   V.Z = random ? 1050000 : -1050000;
   V.Z += U.randomPlusMinus(25000.);
  } else if (Maps.name.equals("Parallel Universe Portal")) {
   V.Z = 0;
  }
  if (E.gravity == 0) {
   if (Maps.name.equals("Outer Space V1")) {
    V.X = U.randomPlusMinus(500.);
    V.Z = U.random(2000.) - U.random(4000.);
    if (V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name())) {
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
  V.Y -= V.isFixed() ? 0 : V.clearanceY;
 }

 public static void runVehicleInteraction(Vehicle V, boolean replay) {
  if (!points.isEmpty()) {
   Point P = points.get(V.point);
   if (P.type == Point.Type.mustPassAbsolute && V.P.mode != Physics.Mode.fly) {
    V.point += U.distance(V, P) < 500 ? 1 : 0;
   } else if (P.type != Point.Type.checkpoint &&
   (U.distanceXZ(V, P) < 500 || (V.AI.skipStunts && P.type != Point.Type.mustPassIfClosest) || (!checkpoints.isEmpty() && !Maps.name.equals(SL.Maps.devilsStairwell) && U.distance(V, checkpoints.get(V.checkpointsPassed)) <= U.distance(P, checkpoints.get(V.checkpointsPassed))))) {
    V.point++;
   }
  }
  if (!checkpoints.isEmpty() && !V.phantomEngaged) {
   double checkSize = Maps.name.equals(SL.Maps.circleRaceXL) ? V.P.speed : 0;
   Checkpoint C = checkpoints.get(V.checkpointsPassed);
   if ((C.type == Checkpoint.Type.passZ || C.type == Checkpoint.Type.passAny) &&
   Math.abs(V.Z - C.Z) < (60 + checkSize) + Math.abs(V.speedZ) * U.tick && Math.abs(V.X - C.X) < 700 && Math.abs((V.Y - C.Y) + 350) < 450) {
    V.checkpointsPassed++;
    V.point++;
    if (V.index == I.vehiclePerspective) {
     if (!Match.messageWait) {
      Match.print = SL.Checkpoint;
      Match.printTimer = 10;
     }
     if (Options.headsUpDisplay) {
      Sounds.checkpoint.play(0);
     }
    }
    Match.scoreCheckpoint[V.index < I.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : 1;
    if (V.checkpointsPassed >= checkpoints.size()) {
     Match.scoreLap[V.index < I.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : 1;
     V.checkpointsPassed = V.point = 0;
    }
   }
   if ((C.type == Checkpoint.Type.passX || C.type == Checkpoint.Type.passAny) &&
   Math.abs(V.X - C.X) < (60 + checkSize) + Math.abs(V.speedX) * U.tick && Math.abs(V.Z - C.Z) < 700 && Math.abs((V.Y - C.Y) + 350) < 450) {
    V.checkpointsPassed++;
    V.point++;
    if (V.index == I.vehiclePerspective) {
     if (!Match.messageWait) {
      Match.print = SL.Checkpoint;
      Match.printTimer = 10;
     }
     if (Options.headsUpDisplay) {
      Sounds.checkpoint.play(0);
     }
    }
    Match.scoreCheckpoint[V.index < I.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : 1;
    if (V.checkpointsPassed >= checkpoints.size()) {
     Match.scoreLap[V.index < I.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : 1;
     V.checkpointsPassed = V.point = 0;
    }
   }
   V.point = V.checkpointsPassed > 0 ? (int) U.clamp(checkpoints.get(V.checkpointsPassed - 1).location + 1, V.point, checkpoints.get(V.checkpointsPassed).location) : V.point;
   if (V.index == I.vehiclePerspective) {
    currentCheckpoint = V.checkpointsPassed;
    lapCheckpoint = V.checkpointsPassed >= checkpoints.size() - 1;
   }
  }
  V.point = V.point >= points.size() || V.point < 0 ? 0 : V.point;
 }

 public static void runVehicleRepairPointInteraction(Vehicle V, boolean gamePlay) {
  if (repairPointsExist && V.repairSpheres.get(U.random(V.repairSpheres.size())).stage <= 0 && !V.phantomEngaged) {
   for (TrackPart part : trackParts) {
    if (part.isRepairPoint) {
     boolean sideways = isSidewaysXZ(part.XZ);
     if (U.distance(sideways ? V.Z : V.X, sideways ? part.Z : part.X, V.Y, part.Y) <= 500 && Math.abs(sideways ? V.X - part.X : V.Z - part.Z) <= 200 + Math.abs(sideways ? V.speedX : V.speedZ) * U.tick) {
      V.repair(gamePlay);
     }
    }
   }
  }
 }

 public static boolean isSidewaysXZ(double angleXZ) {
  return Math.abs(U.cos(angleXZ)) < U.sin45;
 }

 public static void reset() {
  trackParts.clear();
  mounds.clear();
  points.clear();
  checkpoints.clear();
  repairPointsExist = false;
 }
}
