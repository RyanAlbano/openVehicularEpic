package ve.utilities;

import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import ve.environment.Ground;
import ve.environment.Pool;
import ve.environment.Volcano;
import ve.instances.Core;
import ve.instances.I;
import ve.trackElements.TE;
import ve.ui.Match;
import ve.ui.UI;
import ve.ui.options.Options;
import ve.vehicles.Vehicle;

public enum Camera {//*No need to extend core--a Core for calling location is defined below
 ;
 public static final Core C = new Core();//*
 public static final PerspectiveCamera PC = new PerspectiveCamera(true);
 public static final Rotate rotateXY = new Rotate();
 public static double XY, XZ, YZ;
 public static double sinXZ, cosXZ, sinYZ, cosYZ;
 public static final double defaultFOV = 75;
 public static double FOV = defaultFOV;
 public static double adjustFOV = 1;
 public static boolean shake;
 public static final boolean[] toUserPerspective = new boolean[2];
 public static final boolean[] restoreZoom = new boolean[2];
 public static final boolean[] lookForward = new boolean[2];
 public static double aroundVehicleXZ;
 private static double aroundMapXZ = U.randomPlusMinus(180.);
 public static long lookAround;
 public static boolean flowFlip;
 public static boolean lastViewNear;
 public static View view = View.docked;
 public static View lastView = View.docked;
 public static View lastViewWithLookAround = View.docked;
 public static long mapSelectRandomRotationDirection;

 public enum clipRange {
  ;
  private static final double minimumNear = 2;
  public static final double normalNear = 4;
  public static final double maximumFar = Double.MAX_VALUE * .125;
 }

 public enum shakeIntensity {//<-Only useful at vehicle's positions--not for explosive shots, mines etc.
  ;
  public static final long vehicleDeath = 20, vehicleExplode = 30,
  massiveHit = 30, maxSpinnerHit = 30,
  normalNuclear = 50, maxNuclear = 100, volcano = 100;
 }

 public enum View {docked, near, driver, distant, flow, watchMove, watch}

 static {
  PC.getTransforms().add(rotateXY);
  PC.setNearClip(clipRange.normalNear);
  PC.setFarClip(clipRange.maximumFar);
  PC.setTranslateX(0);
  PC.setTranslateY(0);
  PC.setTranslateZ(0);
  UI.scene3D.setCamera(PC);
 }

 public static void run(Vehicle playerV, boolean gamePlay) {
  if (lookForward[0] && lookForward[1]) {
   aroundVehicleXZ = 0;
  }
  PC.setNearClip(view == View.driver ? clipRange.minimumNear : clipRange.normalNear);
  double cameraVehicleXZ, cameraVehicleY;
  if (view == View.flow || view == View.distant) {
   cameraVehicleXZ = lastViewNear ? playerV.absoluteRadius * .45 : 800;
   cameraVehicleY = lastViewNear ? playerV.height * .9 : 250;
  } else {
   boolean mainView = view == View.docked;
   cameraVehicleXZ = mainView ? 800 : playerV.absoluteRadius * .45;
   cameraVehicleY = mainView ? 250 : playerV.height * .9;
  }
  if (view == View.driver) {
   boolean syncToTurret = playerV.VT != null && playerV.VT.driverInside;
   double xy1 = playerV.XY, yz1 = playerV.YZ - (syncToTurret ? playerV.VT.YZ : 0), xz1 = playerV.XZ + (syncToTurret ? playerV.VT.XZ : 0);
   aroundVehicleXZ = 0;
   if (lookAround != 0) {
    aroundVehicleXZ = 180;
    xz1 += 180;
    xy1 *= -1;
    yz1 *= -1;
   }
   double[] driverViewY = {playerV.driverViewY}, driverViewZ = {playerV.driverViewZ}, driverViewX = {playerV.driverViewX * Options.driverSeat};
   if (syncToTurret && playerV.VT.pivotZ != 0) {
    U.rotateWithPivot(driverViewZ, driverViewY, -playerV.VT.pivotZ * .5, 0, -playerV.VT.YZ);
    U.rotateWithPivot(driverViewX, driverViewZ, 0, playerV.VT.pivotZ, playerV.VT.XZ);
   }
   U.rotate(driverViewX, driverViewY, playerV.XY);
   U.rotate(driverViewY, driverViewZ, playerV.YZ);
   U.rotate(driverViewX, driverViewZ, playerV.XZ);
   YZ = -yz1;
   XZ = -xz1;
   XY = -xy1;
   C.X = playerV.X + driverViewX[0];
   C.Y = playerV.Y + driverViewY[0];
   C.Z = playerV.Z + driverViewZ[0];
  } else if (view == View.flow) {
   XY = 0;
   double moveRate = .125 * U.tick,
   xd = -cameraVehicleY - (lastViewNear ? 0 : playerV.extraViewHeight) - (gamePlay ? Math.abs(playerV.P.speed) : 0);
   flowFlip = playerV.P.speed != 0 ? playerV.P.speed < 0 : flowFlip;
   if (flowFlip) {
    while (Math.abs(-playerV.XZ + 180 - XZ) > 180) {
     XZ += XZ < -playerV.XZ + 180 ? 360 : -360;
    }
    XZ += (180 - playerV.XZ - XZ) * moveRate;
    while (Math.abs(playerV.YZ - YZ) > 180) {
     YZ += YZ < playerV.YZ ? 360 : -360;
    }
    YZ += (playerV.YZ - YZ) * moveRate;
    cameraVehicleXZ *= -1;
   } else {
    while (Math.abs(-playerV.XZ - XZ) > 180) {
     XZ += XZ < -playerV.XZ ? 360 : -360;
    }
    XZ += (-playerV.XZ - XZ) * moveRate;
    while (Math.abs(-playerV.YZ - YZ) > 180) {
     YZ += YZ < -playerV.YZ ? 360 : -360;
    }
    YZ += (-playerV.YZ - YZ) * moveRate;
   }
   double e = xd * U.sin(playerV.YZ) - (cameraVehicleXZ * U.cos(playerV.YZ));
   C.X += (playerV.X + (-e * U.sin(playerV.XZ)) - C.X) * moveRate;
   C.Y += (playerV.Y + ((xd * U.cos(playerV.YZ)) + (cameraVehicleXZ * U.sin(playerV.YZ))) - C.Y) * moveRate;
   C.Z += (playerV.Z + (e * U.cos(playerV.XZ)) - C.Z) * moveRate;
  } else if (view.name().contains(View.watch.name())) {
   XY = 0;
   if (view == View.watchMove) {
    while (Math.abs(playerV.X - C.X) > 10000) C.X += playerV.X > C.X ? 20000 : -20000;
    while (Math.abs(playerV.Z - C.Z) > 10000) C.Z += playerV.Z > C.Z ? 20000 : -20000;
    while (Math.abs(playerV.Y - C.Y) > 10000) C.Y += playerV.Y > C.Y ? 20000 : -20000;
    C.Y = Math.min(C.Y, Ground.level - playerV.collisionRadius + (Pool.exists && U.distanceXZ(Pool.core) < Pool.surface.getRadius() ? Pool.depth : 0));
   }
   double vehicleCameraDistanceX = playerV.X - C.X, vehicleCameraDistanceZ = playerV.Z - C.Z, vehicleCameraDistanceY = playerV.Y - C.Y;
   XZ = -((90 + (vehicleCameraDistanceX >= 0 ? 180 : 0)) + U.arcTan(vehicleCameraDistanceZ / vehicleCameraDistanceX));
   YZ = (90 * (vehicleCameraDistanceY > 0 ? 1 : -1)) - U.arcTan(U.netValue(vehicleCameraDistanceZ, vehicleCameraDistanceX) / vehicleCameraDistanceY);
  } else {
   long viewMultiply = view == View.distant ? 10 : 1;
   XY = 0;
   double extraViewHeight = view == View.docked ? playerV.extraViewHeight : 0;
   if (playerV.type == Vehicle.Type.vehicle) {
    YZ = 0;
    boolean syncToTurret = playerV.VT != null && !playerV.VT.hasAutoAim;
    if (syncToTurret) {
     aroundVehicleXZ = 0;
    } else {
     if (Math.abs(lookAround) > 0 && !(lookForward[0] && lookForward[1])) {
      aroundVehicleXZ += 30 * U.tick * (lookAround > 0 ? 1 : -1);
     }
     if (lookAround == 0 && 180 - Math.abs(aroundVehicleXZ) < 10) {//<-Snaps the camera correctly to view vehicle directly from the front
      aroundVehicleXZ = 180;
     }
    }
    double sourceCameraXZ = playerV.P.cameraXZ + aroundVehicleXZ + (syncToTurret ? playerV.VT.XZ : 0);
    XZ = -sourceCameraXZ;
    double[] setX = {syncToTurret ? playerV.VT.pivotZ * U.sin(playerV.VT.XZ) : 0},
    setZ = {syncToTurret ? (playerV.VT.pivotZ * .5 * U.sin(Math.abs(playerV.VT.XZ)) - playerV.VT.pivotZ * 2 * U.sin(Math.abs(playerV.VT.XZ * .5))) : 0};
    setZ[0] -= cameraVehicleXZ * viewMultiply;
    U.rotate(setX, setZ, sourceCameraXZ);
    C.X = setX[0] + playerV.X;
    C.Z = setZ[0] + playerV.Z;
    C.Y = playerV.Y - ((cameraVehicleY + extraViewHeight) * viewMultiply);
   } else {
    double YZ1 = playerV.YZ, XZ1 = playerV.XZ, xd = -cameraVehicleY * viewMultiply;
    boolean lookBack = lookAround != 0;
    long lookDirection = lookBack ? -1 : 1;
    aroundVehicleXZ = lookBack ? 180 : 0;
    if (Math.abs(playerV.YZ) > 90) {
     xd *= -1;
     XZ1 -= 180;
     YZ1 *= -1;
     YZ1 += playerV.YZ > 90 ? 180 : -180;
    }
    YZ = -YZ1 * lookDirection;
    XZ = -XZ1 + aroundVehicleXZ;
    double e = cameraVehicleXZ * viewMultiply * lookDirection * U.cos(playerV.YZ);
    C.X = playerV.X + (-(xd * U.sin(playerV.YZ) - e) * U.sin(playerV.XZ));
    C.Y = playerV.Y - extraViewHeight + ((xd * U.cos(playerV.YZ)) + (cameraVehicleXZ * viewMultiply * lookDirection * U.sin(playerV.YZ)));
    C.Z = playerV.Z + ((xd * U.sin(playerV.YZ) - e) * U.cos(playerV.XZ));
   }
  }
  if (Math.abs(aroundVehicleXZ) > 180) {
   aroundVehicleXZ += aroundVehicleXZ < -180 ? 360 : -360;
  }
  runShake();
  setAngleTable();
  U.rotate(PC, YZ, -XZ);//<-Not trying optimized rotate because passed XZ is NEGATIVE
  rotateXY.setAngle(-XY);
 }

 public static void disablePreMatchFlow() {
  if (view == Camera.View.flow && !Match.started) {
   view = lastViewWithLookAround;
  }
 }

 public static void runAroundTrack() {
  YZ = 10;
  C.Y = TE.MS.Y - 5000;
  XY = 0;
  C.X = TE.MS.X - (17000 * U.sin(aroundMapXZ));
  C.Z = TE.MS.Z - (17000 * U.cos(aroundMapXZ));
  mapSelectRandomRotationDirection *= U.random() > .999 ? -1 : 1;
  aroundMapXZ += mapSelectRandomRotationDirection * U.tick;
  while (aroundMapXZ > 180) aroundMapXZ -= 360;
  while (aroundMapXZ < -180) aroundMapXZ += 360;
  if ((TE.MS.timer += U.tick) > 6) {
   TE.MS.point = ++TE.MS.point >= TE.points.size() ? 0 : TE.MS.point;
   TE.MS.timer = 0;
  }
  if (TE.points.isEmpty()) {
   TE.MS.X = TE.MS.Z = TE.MS.Y = 0;
  } else {
   TE.MS.X -= (TE.MS.X - TE.points.get(TE.MS.point).X) * .1 * U.tick;
   TE.MS.Y -= (TE.MS.Y - TE.points.get(TE.MS.point).Y) * .1 * U.tick;
   TE.MS.Z -= (TE.MS.Z - TE.points.get(TE.MS.point).Z) * .1 * U.tick;
   TE.MS.Y = Math.min(TE.MS.Y, 0);
  }
  XZ = aroundMapXZ;
  setAngleTable();
 }

 private static void runShake() {
  if (shake) {
   double shakeXZ = 0, shakeYZ = 0;
   for (var vehicle : I.vehicles) {
    if (vehicle.cameraShake > 0) {
     double distance = Math.max(1, U.distance(vehicle) * .1);
     shakeXZ += U.randomPlusMinus(vehicle.cameraShake * vehicle.cameraShake) / distance;
     shakeYZ += U.randomPlusMinus(vehicle.cameraShake * vehicle.cameraShake) / distance;
    }
   }
   if (!Volcano.rocks.isEmpty() && Volcano.cameraShake > 0) {
    double distance = Math.max(1, U.distance(C.X, Volcano.C.X, C.Y, -Volcano.height, C.Z, Volcano.C.Z) * .1);
    shakeXZ += U.randomPlusMinus(Volcano.cameraShake * Volcano.cameraShake) / distance;
    shakeYZ += U.randomPlusMinus(Volcano.cameraShake * Volcano.cameraShake) / distance;
   }
   XZ += U.clamp(-45, shakeXZ, 45);
   YZ += U.clamp(-45, shakeYZ, 45);
  }
 }

 public static void setAngleTable() {
  sinXZ = U.sin(XZ);
  cosXZ = U.cos(XZ);
  sinYZ = U.sin(YZ);
  cosYZ = U.cos(YZ);
 }
}
