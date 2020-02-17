package ve.vehicles;

import ve.utilities.D;
import ve.utilities.U;
import ve.utilities.sound.Controlled;

public class VehicleTurret {
 private final Vehicle V;
 boolean turnL, turnR;
 public final double pivotZ, pivotY;
 private final double[] verticalRanges = new double[2];
 public double XZ, YZ;
 private double speedXZ, speedYZ;
 public final boolean hasAutoAim, driverInside;

 VehicleTurret(Vehicle vehicle, String s) {
  V = vehicle;
  verticalRanges[0] = U.getValue(s, 0);
  verticalRanges[1] = U.getValue(s, 1);
  pivotZ = U.getValue(s, 2) * V.modelSize * V.modelScale[2];
  pivotY = U.getValue(s, 3) * V.modelSize * V.modelScale[1];
  double pitchRatio;
  try {
   pitchRatio = U.getValue(s, 4);
  } catch (Exception E) {
   pitchRatio = 1;
  }
  long audioChoice = 0;
  try {
   audioChoice = Math.round(U.getValue(s, 5));
  } catch (RuntimeException ignored) {
  }
  if (V.VA.turret == null && V.realVehicle) {
   V.VA.turret = new Controlled();
   V.VA.turret.addClip(D.turret + (audioChoice > 0 ? audioChoice : ""), pitchRatio);
  }
  hasAutoAim = s.contains(D.autoAim);
  driverInside = s.contains("driverViewInside");
 }

 void runSteering(double turnAmount) {
  boolean hear = false;
  if (turnR && !turnL) {
   speedXZ -= (speedXZ > 0 ? 2 : 1) * V.turnRate * U.tick;
   speedXZ = Math.max(speedXZ, -turnAmount);
   hear = true;
  }
  if (turnL && !turnR) {
   speedXZ += (speedXZ < 0 ? 2 : 1) * V.turnRate * U.tick;
   speedXZ = Math.min(speedXZ, turnAmount);
   hear = true;
  }
  if (speedXZ != 0 && !turnL && !turnR) {
   if (Math.abs(speedXZ) < V.turnRate * 2 * U.tick) {
    speedXZ = 0;
   } else {
    speedXZ += (speedXZ < 0 ? V.turnRate : speedXZ > 0 ? -V.turnRate : 0) * 2 * U.tick;
   }
  }
  if (V.drive2) {
   speedYZ -= (speedYZ > 0 ? 2 : 1) * V.turnRate * U.tick;
   speedYZ = Math.max(speedYZ, -V.maxTurn);
   hear = true;
  }
  if (V.reverse2) {
   speedYZ += (speedYZ < 0 ? 2 : 1) * V.turnRate * U.tick;
   speedYZ = Math.min(speedYZ, V.maxTurn);
   hear = true;
  }
  if (speedYZ != 0 && !V.drive2 && !V.reverse2) {
   if (Math.abs(speedYZ) < V.turnRate * 2 * U.tick) {
    speedYZ = 0;
   } else {
    speedYZ += (speedYZ < 0 ? V.turnRate : speedYZ > 0 ? -V.turnRate : 0) * 2 * U.tick;
   }
  }
  XZ += speedXZ * Physics.angleToSteerVelocity * U.tick;
  while (XZ < -180) XZ += 360;
  while (XZ > 180) XZ -= 360;
  YZ = U.clamp(verticalRanges[0], YZ + speedYZ * Physics.angleToSteerVelocity * U.tick, verticalRanges[1]);
  if (!V.destroyed && hear) {
   V.VA.turret.loop(V.VA.distanceVehicleToCamera);
  } else {
   V.VA.turret.stop();
  }
 }
}