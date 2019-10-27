package ve;

import ve.trackElements.TE;
import ve.vehicles.Vehicle;

enum Recorder {
 ;

 static final int totalFrames = 500;
 static int recordFrame;
 static int gameFrame;
 private static int[] recordBonusHolder;
 static long recordingsCount, recorded;
 private static double[][] X, Y, Z, XZ, XY, YZ, vehicleTurretXZ, vehicleTurretYZ, speed;
 private static double[][][] speedX, speedY, speedZ;
 private static double[][] speedXZ, speedYZ;
 private static double[][] damage;
 private static double[] bonusX, bonusY, bonusZ;
 private static boolean[][] drive;
 private static boolean[][] reverse;
 private static boolean[][] special0;
 private static boolean[][] special1;
 private static boolean[][] boost;
 private static double[][] spinnerSpeed;
 private static Vehicle.Mode[][] mode;

 static void boot() {
  X = new double[VE.maxPlayers][totalFrames];
  Y = new double[VE.maxPlayers][totalFrames];
  Z = new double[VE.maxPlayers][totalFrames];
  XZ = new double[VE.maxPlayers][totalFrames];
  XY = new double[VE.maxPlayers][totalFrames];
  YZ = new double[VE.maxPlayers][totalFrames];
  vehicleTurretXZ = new double[VE.maxPlayers][totalFrames];
  vehicleTurretYZ = new double[VE.maxPlayers][totalFrames];
  speedXZ = new double[VE.maxPlayers][totalFrames];
  speedYZ = new double[VE.maxPlayers][totalFrames];
  speedX = new double[VE.maxPlayers][4][totalFrames];
  speedY = new double[VE.maxPlayers][4][totalFrames];
  speedZ = new double[VE.maxPlayers][4][totalFrames];
  speed = new double[VE.maxPlayers][totalFrames];
  damage = new double[VE.maxPlayers][totalFrames];
  mode = new Vehicle.Mode[VE.maxPlayers][totalFrames];
  drive = new boolean[VE.maxPlayers][totalFrames];
  reverse = new boolean[VE.maxPlayers][totalFrames];
  special0 = new boolean[VE.maxPlayers][totalFrames];
  special1 = new boolean[VE.maxPlayers][totalFrames];
  boost = new boolean[VE.maxPlayers][totalFrames];
  spinnerSpeed = new double[VE.maxPlayers][totalFrames];
  recordBonusHolder = new int[totalFrames];
  bonusX = new double[totalFrames];
  bonusY = new double[totalFrames];
  bonusZ = new double[totalFrames];
 }

 static void updateFrame() {
  if (VE.status == VE.Status.play) {
   recorded = Math.min(recorded + 1, totalFrames);
   gameFrame = ++gameFrame >= totalFrames ? 0 : gameFrame;
  }
 }

 static void record(Vehicle vehicle) {
  if (VE.status == VE.Status.play) {
   int index = vehicle.index;
   X[index][gameFrame] = vehicle.X;
   Y[index][gameFrame] = vehicle.Y;
   Z[index][gameFrame] = vehicle.Z;
   XZ[index][gameFrame] = vehicle.XZ;
   YZ[index][gameFrame] = vehicle.YZ;
   XY[index][gameFrame] = vehicle.XY;
   if (vehicle.hasTurret) {
    vehicleTurretXZ[index][gameFrame] = vehicle.vehicleTurretXZ;
    vehicleTurretYZ[index][gameFrame] = vehicle.vehicleTurretYZ;
   }
   speedXZ[index][gameFrame] = vehicle.speedXZ;
   speedYZ[index][gameFrame] = vehicle.speedYZ;
   for (int n1 = 4; --n1 >= 0; ) {
    speedX[index][n1][gameFrame] = vehicle.wheels.get(n1).speedX;
    speedY[index][n1][gameFrame] = vehicle.wheels.get(n1).speedY;
    speedZ[index][n1][gameFrame] = vehicle.wheels.get(n1).speedZ;
   }
   speed[index][gameFrame] = vehicle.speed;
   damage[index][gameFrame] = vehicle.damage;
   mode[index][gameFrame] = vehicle.mode;
   drive[index][gameFrame] = vehicle.drive;
   reverse[index][gameFrame] = vehicle.reverse;
   long specialsQuantity = vehicle.specials.size();
   if (specialsQuantity > 0) {
    special0[index][gameFrame] = vehicle.specials.get(0).fire;
   }
   if (specialsQuantity > 1) {
    special1[index][gameFrame] = vehicle.specials.get(1).fire;
   }
   boost[index][gameFrame] = vehicle.boost;
   spinnerSpeed[index][gameFrame] = vehicle.spinnerSpeed;
  }
 }

 static void recordBonusHolder() {
  recordBonusHolder[gameFrame] = VE.bonusHolder;
  bonusX[gameFrame] = TE.bonusX;
  bonusY[gameFrame] = TE.bonusY;
  bonusZ[gameFrame] = TE.bonusZ;
 }

 static void playBack() {
  if (VE.status == VE.Status.replay) {
   if (++recordingsCount >= recorded || VE.keyEnter || VE.keySpace || VE.keyEscape) {
    VE.status = VE.Status.paused;
    recordFrame = gameFrame - 1;
    while (recordFrame < 0) recordFrame += totalFrames;
    if (VE.keyEnter || VE.keySpace || VE.keyEscape) {
     VE.UI.play(1, 0);
    }
    VE.keyUp = VE.keyDown = VE.keyEnter = VE.keySpace = VE.keyEscape = false;
   }
   VE.bonusHolder = recordBonusHolder[recordFrame];
   TE.bonusX = bonusX[recordFrame];
   TE.bonusY = bonusY[recordFrame];
   TE.bonusZ = bonusZ[recordFrame];
   for (Vehicle vehicle : VE.vehicles) {
    int index = vehicle.index;
    vehicle.X = X[index][recordFrame];
    vehicle.Y = Y[index][recordFrame];
    vehicle.Z = Z[index][recordFrame];
    vehicle.XZ = XZ[index][recordFrame];
    vehicle.YZ = YZ[index][recordFrame];
    vehicle.XY = XY[index][recordFrame];
    if (vehicle.hasTurret) {
     vehicle.vehicleTurretXZ = vehicleTurretXZ[index][recordFrame];
     vehicle.vehicleTurretYZ = vehicleTurretYZ[index][recordFrame];
    }
    vehicle.speedXZ = speedXZ[index][recordFrame];
    vehicle.speedYZ = speedYZ[index][recordFrame];
    for (int n1 = 4; --n1 >= 0; ) {
     vehicle.wheels.get(n1).speedX = speedX[index][n1][recordFrame];
     vehicle.wheels.get(n1).speedY = speedY[index][n1][recordFrame];
     vehicle.wheels.get(n1).speedZ = speedZ[index][n1][recordFrame];
    }
    vehicle.speed = speed[index][recordFrame];
    vehicle.damage = damage[index][recordFrame];
    vehicle.mode = mode[index][recordFrame];
    vehicle.drive = drive[index][recordFrame];
    vehicle.reverse = reverse[index][recordFrame];
    long specialsQuantity = vehicle.specials.size();
    if (specialsQuantity > 0) {
     vehicle.specials.get(0).fire = special0[index][recordFrame];
    }
    if (specialsQuantity > 1) {
     vehicle.specials.get(1).fire = special1[index][recordFrame];
    }
    vehicle.boost = boost[index][recordFrame];
    vehicle.spinnerSpeed = spinnerSpeed[index][recordFrame];
   }
   recordFrame = ++recordFrame >= totalFrames ? 0 : recordFrame;
  }
 }
}
