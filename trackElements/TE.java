package ve.trackElements;

import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import ve.Camera;
import ve.Network;
import ve.VE;
import ve.environment.E;
import ve.trackElements.trackParts.TrackPart;
import ve.utilities.U;
import ve.vehicles.Vehicle;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public enum TE {//<-TrackElements main Class
 ;
 public static final Sphere bonusBig = new Sphere(500);
 public static double bonusX, bonusY, bonusZ;
 public static int arrowTarget;
 public static MeshView arrow = new MeshView();
 public static final Group arrowGroup = new Group();
 public static Arrow arrowStatus = Arrow.racetrack;
 private static Arrow lastArrowStatus = Arrow.racetrack;
 public static SubScene arrowScene;

 public enum Arrow {racetrack, vehicles, locked}

 public static double instanceSize = 1;
 public static double[] instanceScale = {1, 1, 1};
 public static double randomX, randomY, randomZ;
 public static boolean fixPointsExist;
 public static long mapSelectX, mapSelectY, mapSelectZ;
 public static final List<TrackPart> trackParts = new ArrayList<>();
 public static final List<Point> points = new ArrayList<>();
 public static final List<Checkpoint> checkpoints = new ArrayList<>();

 public static class BonusBall extends Sphere {//<-Not worth extending Core here

  double X, Y, Z, speedX, speedY, speedZ;
 }

 public static final List<BonusBall> bonusBalls = new ArrayList<>();

 private static void runBonusBalls() {
  for (BonusBall bonusBall : bonusBalls) {
   bonusBall.speedY += U.randomPlusMinus(6.);
   bonusBall.speedX += U.randomPlusMinus(6.);
   bonusBall.speedZ += U.randomPlusMinus(6.);
   bonusBall.speedX *= .99;
   bonusBall.speedY *= .99;
   bonusBall.speedZ *= .99;
   bonusBall.X += bonusBall.speedX * VE.tick;
   if (Math.abs(bonusBall.X) > VE.vehicles.get(VE.bonusHolder).collisionRadius * 2) {
    bonusBall.speedX *= -1;
    bonusBall.X *= .999;
   }
   bonusBall.Y += bonusBall.speedY * VE.tick;
   if (Math.abs(bonusBall.Y) > VE.vehicles.get(VE.bonusHolder).collisionRadius * 2) {
    bonusBall.speedY *= -1;
    bonusBall.Y *= .999;
   }
   bonusBall.Z += bonusBall.speedZ * VE.tick;
   if (Math.abs(bonusBall.Z) > VE.vehicles.get(VE.bonusHolder).collisionRadius * 2) {
    bonusBall.speedZ *= -1;
    bonusBall.Z *= .999;
   }
   if (U.getDepth(VE.vehicles.get(VE.bonusHolder).X + bonusBall.X, VE.vehicles.get(VE.bonusHolder).Y + bonusBall.Y, VE.vehicles.get(VE.bonusHolder).Z + bonusBall.Z) > 0) {
    U.setTranslate(bonusBall, VE.vehicles.get(VE.bonusHolder).X + bonusBall.X, VE.vehicles.get(VE.bonusHolder).Y + bonusBall.Y, VE.vehicles.get(VE.bonusHolder).Z + bonusBall.Z);
    bonusBall.setVisible(true);
    U.setDiffuseRGB((PhongMaterial) bonusBall.getMaterial(), U.random(), U.random(), U.random());
   } else {
    bonusBall.setVisible(false);
   }
  }
 }

 public static void runBonus() {
  if (VE.bonusHolder < 0) {
   if (U.getDepth(bonusX, bonusY, bonusZ) > -bonusBig.getRadius()) {
    U.setTranslate(bonusBig, bonusX, bonusY, bonusZ);
    U.setDiffuseRGB((PhongMaterial) bonusBig.getMaterial(), U.random(), U.random(), U.random());
    bonusBig.setVisible(true);
   } else {
    bonusBig.setVisible(false);
   }
   for (TE.BonusBall bonusBall : bonusBalls) {
    bonusBall.setVisible(false);
   }
  } else {
   bonusBig.setVisible(false);
   bonusX = VE.vehicles.get(VE.bonusHolder).X;
   bonusY = VE.vehicles.get(VE.bonusHolder).Y;
   bonusZ = VE.vehicles.get(VE.bonusHolder).Z;
   runBonusBalls();
  }
  if (VE.matchStarted) {
   if (Network.mode == Network.Mode.OFF) {
    for (Vehicle vehicle : VE.vehicles) {
     if (VE.bonusHolder < 0 && vehicle.damage <= vehicle.durability && !vehicle.phantomEngaged && U.distance(vehicle.X, bonusX, vehicle.Y, bonusY, vehicle.Z, bonusZ) < vehicle.collisionRadius + bonusBig.getRadius()) {
      setBonusHolder(vehicle);
     }
    }
    VE.bonusHolder = VE.bonusHolder > -1 && VE.vehicles.get(VE.bonusHolder).damage > VE.vehicles.get(VE.bonusHolder).durability ? -1 : VE.bonusHolder;
   } else {
    if (Network.bonusHolder < 0 && VE.vehicles.get(VE.userPlayer).damage <= VE.vehicles.get(VE.userPlayer).durability && !VE.vehicles.get(VE.userPlayer).phantomEngaged && U.distance(VE.vehicles.get(VE.userPlayer).X, bonusX, VE.vehicles.get(VE.userPlayer).Y, bonusY, VE.vehicles.get(VE.userPlayer).Z, bonusZ) < VE.vehicles.get(VE.userPlayer).collisionRadius + bonusBig.getRadius()) {
     Network.bonusHolder = VE.userPlayer;
     if (Network.mode == Network.Mode.HOST) {
      for (PrintWriter PW : Network.out) {
       PW.println("BONUS0");
      }
     } else {
      Network.out.get(0).println("BONUS");
     }
    }
    int setHolder = Network.bonusHolder < 0 ? Network.bonusHolder : VE.bonusHolder;
    if (setHolder > -1 && VE.vehicles.get(setHolder).damage > VE.vehicles.get(setHolder).durability) {
     Network.bonusHolder = VE.bonusHolder = -1;
     if (Network.mode == Network.Mode.HOST) {
      for (PrintWriter PW : Network.out) {
       PW.println("BonusOpen");
      }
     } else {
      Network.out.get(0).println("BonusOpen");
     }
    }
    if (VE.bonusHolder != Network.bonusHolder) {
     VE.bonusHolder = Network.bonusHolder;
     if (VE.bonusHolder > -1) {
      setBonusHolder(VE.vehicles.get(VE.bonusHolder));
     }
    }
   }
  }
 }

 public static void setBonusHolder(Vehicle vehicle) {
  VE.bonusHolder = vehicle.index;
  for (TE.BonusBall bonusBall : bonusBalls) {
   bonusBall.setRadius(VE.vehicles.get(VE.bonusHolder).absoluteRadius * .02);
   bonusBall.X = bonusBall.Y = bonusBall.Z = bonusBall.speedX = bonusBall.speedY = bonusBall.speedZ = 0;
  }
  if (VE.headsUpDisplay) {
   VE.bonus.playIfNotPlaying(0);
  }
 }

 public static void addArrow() {
  if (!arrowGroup.getChildren().contains(arrow)) {
   arrowGroup.getChildren().add(arrow);
  }
  arrow.setVisible(false);
  PointLight backPL = new PointLight();
  backPL.setTranslateX(0);
  backPL.setTranslateY(arrow.getTranslateY());
  backPL.setTranslateZ(-Long.MAX_VALUE);
  backPL.setColor(Color.color(1, 1, 1));
  PointLight frontPL = new PointLight();
  frontPL.setTranslateX(0);
  frontPL.setTranslateY(arrow.getTranslateY());
  frontPL.setTranslateZ(Long.MAX_VALUE);
  frontPL.setColor(Color.color(1, 1, 1));
  arrowGroup.getChildren().addAll(new AmbientLight(Color.color(.5, .5, .5)), backPL, frontPL);
 }

 public static void runArrow() {
  if (lastArrowStatus != arrowStatus) {
   VE.print = arrowStatus == TE.Arrow.locked ? "Arrow now Locked on " + VE.playerNames[arrowTarget] : "Arrow now pointing at " + (arrowStatus == TE.Arrow.vehicles ? "Vehicles" : "Map");
   VE.messageWait = false;
   VE.printTimer = 50;
   lastArrowStatus = arrowStatus;
  }
  Vehicle V = VE.vehicles.get(VE.vehiclePerspective);
  double d, dY, targetX = V.X, targetY = V.Y, targetZ = V.Z;
  if (arrowStatus == TE.Arrow.racetrack) {
   boolean hasSize = !points.isEmpty();
   double nX = hasSize ? points.get(V.point).X : 0, nY = hasSize ? points.get(V.point).Y : 0, nZ = hasSize ? points.get(V.point).Z : 0;
   d = (nX - V.X >= 0 ? 270 : 90) + U.arcTan((nZ - V.Z) / (nX - V.X));
   dY = (nY - V.Y >= 0 ? 270 : 90) + U.arcTan(U.distance(nX, V.X, nZ, V.Z) / (nY - V.Y));
   if (hasSize) {
    targetX = points.get(V.point).X;
    targetY = points.get(V.point).Y;
    targetZ = points.get(V.point).Z;
   }
  } else {
   targetX = VE.vehicles.get(arrowTarget).X;
   targetY = VE.vehicles.get(arrowTarget).Y;
   targetZ = VE.vehicles.get(arrowTarget).Z;
   if (arrowStatus != TE.Arrow.locked) {
    double compareDistance = Double.POSITIVE_INFINITY;
    for (Vehicle vehicle : VE.vehicles) {
     if (vehicle.index != VE.vehiclePerspective && vehicle.destructionType < 1 && U.distance(V, vehicle) < compareDistance) {
      arrowTarget = vehicle.index;
      compareDistance = U.distance(V, vehicle);
     }
    }
    VE.vehicles.get(VE.userPlayer).AI.target = VE.vehiclePerspective == VE.userPlayer ? arrowTarget : VE.vehicles.get(VE.userPlayer).AI.target;
   }
   arrowTarget = VE.vehiclesInMatch < 2 ? 0 : arrowTarget;
   Vehicle targetVehicle = VE.vehicles.get(arrowTarget);
   double nameHeight = .15, B = targetVehicle.damage / targetVehicle.durability;
   U.fillRGB(1, 1 - B, 0);
   U.fillRectangle(.5, nameHeight, B * .1, .005);
   if (arrowStatus == TE.Arrow.locked) {
    double C = VE.globalFlick ? 1 : 0;
    U.strokeRGB(C, C, C);
    VE.graphicsContext.strokeLine((VE.width * .5) - 50, VE.height * nameHeight, (VE.width * .5) + 50, VE.height * nameHeight);
   }
   d = (targetVehicle.X - V.X >= 0 ? 270 : 90) + U.arcTan((targetVehicle.Z - V.Z) / (targetVehicle.X - V.X));
   dY = (targetVehicle.Y - V.Y >= 0 ? 270 : 90) + U.arcTan(U.distance(targetVehicle.X, V.X, targetVehicle.Z, V.Z) / (targetVehicle.Y - V.Y));
   U.fillRGB(E.skyInverse);
   U.text("[ " + VE.playerNames[arrowTarget] + " ]", nameHeight);
  }
  double convertedUnits = VE.units == .5364466667 ? .0175 : VE.units == 1 / 3. ? .0574147 : 1, color = VE.globalFlick ? 1 : 0;
  U.fillRGB(color, color, color);
  U.text("(" + Math.round(U.distance(V.X, targetX, V.Y, targetY, V.Z, targetZ) * convertedUnits) + ")", .175);
  d += Camera.XZ;
  while (d < -180) d += 360;
  while (d > 180) d -= 360;
  if (arrowStatus != TE.Arrow.racetrack && (VE.vehiclesInMatch < 2 || arrowTarget == VE.vehiclePerspective)) {
   d = dY = 0;
  }
  U.rotate(arrow, -dY, d);
  if (arrowStatus == TE.Arrow.racetrack || VE.vehiclesInMatch < 3) {
   U.setDiffuseRGB((PhongMaterial) arrow.getMaterial(), E.skyInverse);
  } else {
   long[] RG = {0, 0};
   if (VE.globalFlick) {
    RG[arrowTarget < VE.vehiclesInMatch >> 1 ? 1 : 0] = 1;
   }
   U.setDiffuseRGB((PhongMaterial) arrow.getMaterial(), RG[0], RG[1], 0);
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
  instanceScale[1] = VE.mapName.equals("Ghost City") && listNumber == getTrackPartIndex(VE.MapModels.cube.name()) && instanceSize == 10000 ? 1 + U.random(3.) : instanceScale[1];
  if (VE.mapName.equals("Meteor Fields") && listNumber == getTrackPartIndex(VE.MapModels.ramp.name())) {
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
  if ((listNumber == getTrackPartIndex(VE.MapModels.checkpoint.name()) || listNumber == getTrackPartIndex(VE.MapModels.fixpoint.name()))) {
   if (VE.mapName.equals("Pyramid Paradise")) {
    forceTrackPartOutsideExistingParts(s, X, Y, Z, VE.MapModels.pyramid.name());
   } else if (U.listEquals(VE.mapName, "the Forest", "Volatile Sands")) {
    forceTrackPartOutsideExistingParts(s, X, Y, Z, (String[]) null);
   } else if (VE.mapName.equals("Military Base")) {
    forceTrackPartOutsideExistingParts(s, X, Y, Z, VE.MapModels.cube.name(), VE.MapModels.ramptriangle.name());
   }
  }
  if (listNumber == getTrackPartIndex(VE.MapModels.checkpoint.name())) {
   if (VE.mapName.equals("Highlands")) {
    if (U.random() < .5) {
     if (U.random() < .5) {
      X[0] += U.random() < .5 ? 100000 : -100000;
      Y[0] -= 25000;
     } else {
      Z[0] += U.random() < .5 ? 100000 : -100000;
      Y[0] -= 25000;
     }
    }
   } else if (VE.mapName.equals("Ghost City")) {
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
   } else if (VE.mapName.equals("the Machine is Out of Control")) {
    boolean inside = true;
    while (inside) {
     inside = Math.abs(X[0]) < 30000 && Math.abs(Z[0]) < 30000;
     for (TrackPart trackpart : trackParts) {
      inside = (trackpart.modelNumber == getTrackPartIndex(VE.MapModels.pyramid.name()) || trackpart.modelNumber == getTrackPartIndex(VE.MapModels.cube.name()) || trackpart.modelNumber == getTrackPartIndex(VE.MapModels.ramptriangle.name())) &&
      Math.abs(X[0] - trackpart.X) <= trackpart.renderRadius && Math.abs(Z[0] - trackpart.Z) <= trackpart.renderRadius || inside;
     }
     if (inside) {
      X[0] = U.getValue(s, 1) + U.randomPlusMinus(randomX);
      Z[0] = U.getValue(s, 2) + U.randomPlusMinus(randomZ);
      Y[0] = U.getValue(s, 3) + U.randomPlusMinus(randomY);
     }
    }
   } else if (VE.mapName.equals("DoomsDay")) {
    while (U.distance(X[0], 0, Z[0], 0) <= E.volcanoBottomRadius) {
     X[0] = U.getValue(s, 1) + U.randomPlusMinus(randomX);
     Z[0] = U.getValue(s, 2) + U.randomPlusMinus(randomZ);
     Y[0] = U.getValue(s, 3) + U.randomPlusMinus(randomY);
    }
   }
  }
  if (U.listEquals(U.getString(s, 0), VE.MapModels.mound.name(), VE.MapModels.pavedmound.name())) {
   try {
    trackParts.add(new TrackPart(X[0], Z[0], Y[0],
    U.getValue(s, 4) * instanceSize, U.getValue(s, 5) * instanceSize, U.getValue(s, 6) * instanceSize,
    false, U.getString(s, 0).contains("paved"), true));
   } catch (RuntimeException E) {
    trackParts.add(new TrackPart(X[0], Z[0], Y[0],
    U.getValue(s, 4) * U.random(instanceSize), U.getValue(s, 4) * U.random(instanceSize), U.getValue(s, 4) * U.random(instanceSize),
    false, U.getString(s, 0).contains("paved"), true));
   }
  } else {
   trackParts.add(new TrackPart(listNumber, X[0], Y[0], Z[0], rotation, instanceSize, instanceScale));
  }
  if (listNumber == getTrackPartIndex(VE.MapModels.rainbow.name())) {
   trackParts.get(trackParts.size() - 1).modelProperties += " rainbow ";
  } else if (listNumber == getTrackPartIndex(VE.MapModels.crescent.name())) {
   U.remove(E.sun);
  } else if (listNumber == getTrackPartIndex(VE.MapModels.checkpoint.name())) {
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
  fixPointsExist = listNumber == getTrackPartIndex(VE.MapModels.fixpoint.name()) || fixPointsExist;
 }

 private static void forceTrackPartOutsideExistingParts(CharSequence source, double[] X, double[] Y, double[] Z, String... targets) {
  boolean inside = true;
  double tolerance = 1.05;//<-Not a sufficient distance away otherwise
  while (inside) {
   inside = false;
   for (TrackPart trackPart : trackParts) {
    inside = targets == null ? !trackPart.wraps && trackPart.mound != null && U.distance(X[0], trackPart.X, Z[0], trackPart.Z) <= trackPart.mound.getMajorRadius() * tolerance || inside : U.contains(trackPart.modelName, targets) &&
    Math.abs(Y[0] - trackPart.Y) <= trackPart.boundsY * 1.05 &&
    Math.abs(X[0] - trackPart.X) <= trackPart.boundsX * 1.05 &&
    Math.abs(Z[0] - trackPart.Z) <= trackPart.boundsZ * 1.05 ||//<-Aerial map parts check not foolproof
    inside;
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
   return VE.MapModels.valueOf(s).ordinal();
  } catch (IllegalArgumentException E) {
   return -1;
  }
 }

 public static String getTrackPartName(int in) {
  return VE.MapModels.values()[in].name();
 }

 public static boolean isSidewaysXZ(double angleXZ) {
  return Math.abs(U.cos(angleXZ)) < U.sin45;
 }
}
