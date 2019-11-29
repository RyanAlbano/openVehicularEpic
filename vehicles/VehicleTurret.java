package ve.vehicles;

import ve.Sound;
import ve.VE;
import ve.utilities.SL;
import ve.utilities.U;

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
  if (V.VA.turret == null) {
   V.VA.turret = new Sound();
   V.VA.turret.addClip(SL.turret, pitchRatio);
  }
  hasAutoAim = s.contains(SL.autoAim);
  driverInside = s.contains("driverViewInside");
 }

 void runSteering(double turnAmount) {
  boolean hear = false;
  if (turnR && !turnL) {
   speedXZ -= (speedXZ > 0 ? 2 : 1) * V.turnRate * VE.tick;
   speedXZ = Math.max(speedXZ, -turnAmount);
   hear = true;
  }
  if (turnL && !turnR) {
   speedXZ += (speedXZ < 0 ? 2 : 1) * V.turnRate * VE.tick;
   speedXZ = Math.min(speedXZ, turnAmount);
   hear = true;
  }
  if (speedXZ != 0 && !turnL && !turnR) {
   if (Math.abs(speedXZ) < V.turnRate * 2 * VE.tick) {
    speedXZ = 0;
   } else {
    speedXZ += (speedXZ < 0 ? V.turnRate : speedXZ > 0 ? -V.turnRate : 0) * 2 * VE.tick;
   }
  }
  if (V.drive2) {
   speedYZ -= (speedYZ > 0 ? 2 : 1) * V.turnRate * VE.tick;
   speedYZ = Math.max(speedYZ, -V.maxTurn);
   hear = true;
  }
  if (V.reverse2) {
   speedYZ += (speedYZ < 0 ? 2 : 1) * V.turnRate * VE.tick;
   speedYZ = Math.min(speedYZ, V.maxTurn);
   hear = true;
  }
  if (speedYZ != 0 && !V.drive2 && !V.reverse2) {
   if (Math.abs(speedYZ) < V.turnRate * 2 * VE.tick) {
    speedYZ = 0;
   } else {
    speedYZ += (speedYZ < 0 ? V.turnRate : speedYZ > 0 ? -V.turnRate : 0) * 2 * VE.tick;
   }
  }
  XZ += speedXZ * Physics.fromAngleToVelocityConstant * VE.tick;
  while (XZ < -180) XZ += 360;
  while (XZ > 180) XZ -= 360;
  YZ = U.clamp(verticalRanges[0], YZ + speedYZ * Physics.fromAngleToVelocityConstant * VE.tick, verticalRanges[1]);
  if (!V.destroyed && hear) {
   V.VA.turret.loop(V.VA.distanceVehicleToCamera);
  } else {
   V.VA.turret.stop();
  }
 }
}