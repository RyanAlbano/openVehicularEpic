package ve;

import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import ve.environment.E;
import ve.trackElements.TE;
import ve.utilities.U;
import ve.vehicles.Vehicle;

public enum Camera {//<-Don't extend Core!
 ;

 static final PerspectiveCamera camera = new PerspectiveCamera(true);
 static final Rotate rotateXY = new Rotate();
 public static double X, Y, Z;
 static double XY;
 public static double XZ, YZ;
 public static double sinXZ;
 public static double cosXZ;
 public static double sinYZ;
 public static double cosYZ;
 static final double defaultZoom = 75;
 public static double zoom = defaultZoom;
 static double zoomChange = 1;
 static final boolean[] toUserPerspective = new boolean[2];
 static final boolean[] restoreZoom = new boolean[2];
 static final boolean[] lookForward = new boolean[2];
 static double aroundVehicleXZ;
 private static double aroundMapXZ = U.randomPlusMinus(180.);
 static long lookAround;
 static boolean flowFlip;
 static boolean lastViewNear;
 public static View view = View.docked;
 static View lastView = View.docked;
 static View lastViewWithLookAround = View.docked;
 static long mapSelectRandomRotationDirection;
 private static final double minimumNearClip = 2;
 static final double normalNearClip = 4;
 static final double maximumFarClip = Double.MAX_VALUE * .125;

 public enum View {docked, near, distant, driver, flow, watchMove, watch}

 static void boot() {
  camera.getTransforms().add(rotateXY);
  camera.setNearClip(normalNearClip);
  camera.setFarClip(maximumFarClip);
  camera.setTranslateX(0);
  camera.setTranslateY(0);
  camera.setTranslateZ(0);
 }

 static void run(Vehicle vehicle) {
  aroundVehicleXZ = lookForward[0] && lookForward[1] ? 0 : aroundVehicleXZ;
  camera.setNearClip(view == View.driver ? minimumNearClip : normalNearClip);
  double cameraVehicleXZ, cameraVehicleY;
  if (view == View.flow || view == View.distant) {
   cameraVehicleXZ = lastViewNear ? vehicle.absoluteRadius * .45 : 800;
   cameraVehicleY = lastViewNear ? vehicle.height * .9 : 250;
  } else {
   boolean mainView = view == View.docked;
   cameraVehicleXZ = mainView ? 800 : vehicle.absoluteRadius * .45;
   cameraVehicleY = mainView ? 250 : vehicle.height * .9;
  }
  if (view == View.driver) {
   boolean syncToTurret = vehicle.hasTurret && vehicle.driverInVehicleTurret;
   double xy1 = vehicle.XY, yz1 = vehicle.YZ - (syncToTurret ? vehicle.vehicleTurretYZ : 0), xz1 = vehicle.XZ + (syncToTurret ? vehicle.vehicleTurretXZ : 0);
   aroundVehicleXZ = 0;
   if (lookAround != 0) {
    aroundVehicleXZ = 180;
    xz1 += 180;
    xy1 *= -1;
    yz1 *= -1;
   }
   double[] driverViewY = {vehicle.driverViewY}, driverViewZ = {vehicle.driverViewZ}, driverViewX = {vehicle.driverViewX * VE.driverSeat};
   if (syncToTurret && vehicle.vehicleTurretPivotZ != 0) {
    U.rotateWithPivot(driverViewZ, driverViewY, -vehicle.vehicleTurretPivotZ * .5, 0, -vehicle.vehicleTurretYZ);
    U.rotateWithPivot(driverViewX, driverViewZ, 0, vehicle.vehicleTurretPivotZ, vehicle.vehicleTurretXZ);
   }
   U.rotate(driverViewX, driverViewY, vehicle.XY);
   U.rotate(driverViewY, driverViewZ, vehicle.YZ);
   U.rotate(driverViewX, driverViewZ, vehicle.XZ);
   YZ = -yz1;
   XZ = -xz1;
   XY = -xy1;
   X = vehicle.X + driverViewX[0];
   Y = vehicle.Y + driverViewY[0];
   Z = vehicle.Z + driverViewZ[0];
  } else if (view == View.flow) {
   XY = 0;
   boolean gamePlay = VE.status == VE.Status.play || VE.status == VE.Status.replay;
   double moveRate = .125 * VE.tick, xd = -cameraVehicleY - (lastViewNear ? 0 : vehicle.extraViewHeight) - (gamePlay ? Math.abs(vehicle.speed) : 0);
   flowFlip = vehicle.speed != 0 ? vehicle.speed < 0 : flowFlip;
   if (flowFlip) {
    while (Math.abs(-vehicle.XZ + 180 - XZ) > 180) {
     XZ += XZ < -vehicle.XZ + 180 ? 360 : -360;
    }
    XZ += (180 - vehicle.XZ - XZ) * moveRate;
    while (Math.abs(vehicle.YZ - YZ) > 180) {
     YZ += YZ < vehicle.YZ ? 360 : -360;
    }
    YZ += (vehicle.YZ - YZ) * moveRate;
    cameraVehicleXZ *= -1;
   } else {
    while (Math.abs(-vehicle.XZ - XZ) > 180) {
     XZ += XZ < -vehicle.XZ ? 360 : -360;
    }
    XZ += (-vehicle.XZ - XZ) * moveRate;
    while (Math.abs(-vehicle.YZ - YZ) > 180) {
     YZ += YZ < -vehicle.YZ ? 360 : -360;
    }
    YZ += (-vehicle.YZ - YZ) * moveRate;
   }
   double e = xd * U.sin(vehicle.YZ) - (cameraVehicleXZ * U.cos(vehicle.YZ));
   X += (vehicle.X + (-e * U.sin(vehicle.XZ)) - X) * moveRate;
   Y += (vehicle.Y + ((xd * U.cos(vehicle.YZ)) + (cameraVehicleXZ * U.sin(vehicle.YZ))) - Y) * moveRate;
   Z += (vehicle.Z + (e * U.cos(vehicle.XZ)) - Z) * moveRate;
  } else if (view.name().contains(View.watch.name())) {
   XY = 0;
   if (view == View.watchMove) {
    while (Math.abs(vehicle.X - X) > 10000) X += vehicle.X > X ? 20000 : -20000;
    while (Math.abs(vehicle.Z - Z) > 10000) Z += vehicle.Z > Z ? 20000 : -20000;
    while (Math.abs(vehicle.Y - Y) > 10000) Y += vehicle.Y > Y ? 20000 : -20000;
    Y = Math.min(Y, E.groundLevel - vehicle.collisionRadius + (E.poolExists && U.distance(X, E.poolX, Z, E.poolZ) < E.pool[0].getRadius() ? E.poolDepth : 0));
   }
   double vehicleCameraDistanceX = vehicle.X - X, vehicleCameraDistanceZ = vehicle.Z - Z, vehicleCameraDistanceY = vehicle.Y - Y;
   XZ = -((90 + (vehicleCameraDistanceX >= 0 ? 180 : 0)) + U.arcTan(vehicleCameraDistanceZ / vehicleCameraDistanceX));
   YZ = (90 * (vehicleCameraDistanceY > 0 ? 1 : -1)) - U.arcTan(U.netValue(vehicleCameraDistanceZ, vehicleCameraDistanceX) / vehicleCameraDistanceY);
  } else {
   long viewMultiply = view == View.distant ? 10 : 1;
   XY = 0;
   if (vehicle.vehicleType == Vehicle.Type.vehicle) {
    YZ = 0;
    boolean syncToTurret = vehicle.hasTurret && !vehicle.turretAutoAim;
    if (syncToTurret) {
     aroundVehicleXZ = 0;
    } else if (Math.abs(lookAround) > 0 && !(lookForward[0] && lookForward[1])) {
     aroundVehicleXZ += lookAround > 0 ? 10 : -10;
    }
    double sourceCameraXZ = vehicle.cameraXZ + aroundVehicleXZ + (syncToTurret ? vehicle.vehicleTurretXZ : 0);
    XZ = -sourceCameraXZ;
    double[] setX = {syncToTurret ? vehicle.vehicleTurretPivotZ * U.sin(vehicle.vehicleTurretXZ) : 0},
    setZ = {syncToTurret ? (vehicle.vehicleTurretPivotZ * .5 * U.sin(Math.abs(vehicle.vehicleTurretXZ)) - vehicle.vehicleTurretPivotZ * 2 * U.sin(Math.abs(vehicle.vehicleTurretXZ * .5))) : 0};
    setZ[0] -= cameraVehicleXZ * viewMultiply;
    U.rotate(setX, setZ, sourceCameraXZ);
    X = setX[0] + vehicle.X;
    Z = setZ[0] + vehicle.Z;
    Y = vehicle.Y - ((cameraVehicleY + (view == View.docked ? vehicle.extraViewHeight : 0)) * viewMultiply);
   } else {
    double YZ1 = vehicle.YZ, XZ1 = vehicle.XZ, xd = -cameraVehicleY * viewMultiply;
    boolean lookBack = lookAround != 0;
    long lookDirection = lookBack ? -1 : 1;
    aroundVehicleXZ = lookBack ? 180 : 0;
    if (Math.abs(vehicle.YZ) > 90) {
     xd *= -1;
     XZ1 -= 180;
     YZ1 *= -1;
     YZ1 += vehicle.YZ > 90 ? 180 : -180;
    }
    YZ = -YZ1 * lookDirection;
    XZ = -XZ1 + aroundVehicleXZ;
    double e = cameraVehicleXZ * viewMultiply * lookDirection * U.cos(vehicle.YZ);
    X = vehicle.X + (-(xd * U.sin(vehicle.YZ) - e) * U.sin(vehicle.XZ));
    Y = vehicle.Y + ((xd * U.cos(vehicle.YZ)) + (cameraVehicleXZ * viewMultiply * lookDirection * U.sin(vehicle.YZ)));
    Z = vehicle.Z + ((xd * U.sin(vehicle.YZ) - e) * U.cos(vehicle.XZ));
   }
  }
  if (Math.abs(aroundVehicleXZ) > 180) {
   aroundVehicleXZ += aroundVehicleXZ < -180 ? 360 : -360;
  }
  setAngleTable();
 }

 static void runAroundTrack() {
  YZ = 10;
  Y = VE.mapSelectY - 5000;
  XY = 0;
  X = VE.mapSelectX - (17000 * U.sin(aroundMapXZ));
  Z = VE.mapSelectZ - (17000 * U.cos(aroundMapXZ));
  mapSelectRandomRotationDirection *= U.random() > .999 ? -1 : 1;
  aroundMapXZ += mapSelectRandomRotationDirection * VE.tick;
  while (aroundMapXZ > 180) aroundMapXZ -= 360;
  while (aroundMapXZ < -180) aroundMapXZ += 360;
  if ((VE.trackTimer += VE.tick) > 6) {
   VE.trackPoint = ++VE.trackPoint >= TE.points.size() ? 0 : VE.trackPoint;
   VE.trackTimer = 0;
  }
  if (TE.points.isEmpty()) {
   VE.mapSelectX = VE.mapSelectZ = VE.mapSelectY = 0;
  } else {
   VE.mapSelectX -= (VE.mapSelectX - TE.points.get(VE.trackPoint).X) * .1 * VE.tick;
   VE.mapSelectY -= (VE.mapSelectY - TE.points.get(VE.trackPoint).Y) * .1 * VE.tick;
   VE.mapSelectZ -= (VE.mapSelectZ - TE.points.get(VE.trackPoint).Z) * .1 * VE.tick;
   VE.mapSelectY = Math.min(VE.mapSelectY, 0);
  }
  XZ = aroundMapXZ;
  setAngleTable();
 }

 static void setAngleTable() {
  sinXZ = U.sin(XZ);
  cosXZ = U.cos(XZ);
  sinYZ = U.sin(YZ);
  cosYZ = U.cos(YZ);
 }
}
