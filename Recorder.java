package ve;

import ve.VE.*;
import ve.utilities.U;
import ve.vehicles.Vehicle;

class Recorder {

 static final int totalFrames = 500;
 static int recordFrame;
 static int gameFrame;
 private static int[] recordBonusHolder;
 static long recordingsCount, recorded;
 private static double[][] X;
 private static double[][] Y;
 private static double[][] Z;
 private static double[][] XZ;
 private static double[][] XY;
 private static double[][] YZ;
 private static double[][] speed;
 private static double[][][] speedX;
 private static double[][][] speedY;
 private static double[][][] speedZ;
 private static double[][] speedXZ;
 private static double[][] speedYZ;
 private static double[][] damage;
 private static double[] bonusX;
 private static double[] bonusY;
 private static double[] bonusZ;
 private static boolean[][] drive;
 private static boolean[][] reverse;
 private static boolean[][] special0;
 private static boolean[][] special1;
 private static boolean[][] boost;
 private static double[][] spinnerSpeed;
 private static mode[][] mode;

 static void boot() {
  X = new double[VE.maxPlayers][totalFrames];
  Y = new double[VE.maxPlayers][totalFrames];
  Z = new double[VE.maxPlayers][totalFrames];
  XZ = new double[VE.maxPlayers][totalFrames];
  XY = new double[VE.maxPlayers][totalFrames];
  YZ = new double[VE.maxPlayers][totalFrames];
  speedXZ = new double[VE.maxPlayers][totalFrames];
  speedYZ = new double[VE.maxPlayers][totalFrames];
  speedX = new double[VE.maxPlayers][4][totalFrames];
  speedY = new double[VE.maxPlayers][4][totalFrames];
  speedZ = new double[VE.maxPlayers][4][totalFrames];
  speed = new double[VE.maxPlayers][totalFrames];
  damage = new double[VE.maxPlayers][totalFrames];
  mode = new mode[VE.maxPlayers][totalFrames];
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
  /*for (n = VE.maxPlayers; --n >= 0;) {
   for (int n1 = Recorder.totalFrames; --n1 >= 0;) {
    mode[n][n1] = "";
   }
  }*/
 }

 static void updateFrame() {
  if (VE.event == event.play) {
   recorded = Math.min(recorded + 1, totalFrames);
   gameFrame = ++gameFrame >= totalFrames ? 0 : gameFrame;
  }
 }

 static void record(Vehicle vehicle) {
  if (VE.event == event.play) {
   int index = vehicle.index;
   X[index][gameFrame] = vehicle.X;
   Y[index][gameFrame] = vehicle.Y;
   Z[index][gameFrame] = vehicle.Z;
   XZ[index][gameFrame] = vehicle.XZ;
   YZ[index][gameFrame] = vehicle.YZ;
   XY[index][gameFrame] = vehicle.XY;
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
   special0[index][gameFrame] = vehicle.useSpecial[0];
   special1[index][gameFrame] = vehicle.useSpecial[1];
   boost[index][gameFrame] = vehicle.boost;
   spinnerSpeed[index][gameFrame] = vehicle.spinnerSpeed;
  }
 }

 static void recordBonusHolder() {
  recordBonusHolder[gameFrame] = VE.bonusHolder;
  bonusX[gameFrame] = VE.bonusX;
  bonusY[gameFrame] = VE.bonusY;
  bonusZ[gameFrame] = VE.bonusZ;
 }

 static void playBack() {
  if (VE.event == event.replay) {
   if (++recordingsCount >= recorded || VE.keyEnter || VE.keySpace || VE.keyEscape) {
    VE.event = event.paused;
    for (recordFrame = gameFrame - 1; recordFrame < 0; recordFrame += totalFrames) ;
    if (VE.keyEnter || VE.keySpace || VE.keyEscape) {
     U.soundPlay(VE.sounds, "UI1", 0);
    }
    VE.keyUp = VE.keyDown = VE.keyEnter = VE.keySpace = VE.keyEscape = false;
   }
   VE.bonusHolder = recordBonusHolder[recordFrame];
   VE.bonusX = bonusX[recordFrame];
   VE.bonusY = bonusY[recordFrame];
   VE.bonusZ = bonusZ[recordFrame];
   for (Vehicle vehicle : VE.vehicles) {
    int index = vehicle.index;
    vehicle.X = X[index][recordFrame];
    vehicle.Y = Y[index][recordFrame];
    vehicle.Z = Z[index][recordFrame];
    vehicle.XZ = XZ[index][recordFrame];
    vehicle.YZ = YZ[index][recordFrame];
    vehicle.XY = XY[index][recordFrame];
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
    vehicle.useSpecial[0] = special0[index][recordFrame];
    vehicle.useSpecial[1] = special1[index][recordFrame];
    vehicle.boost = boost[index][recordFrame];
    vehicle.spinnerSpeed = spinnerSpeed[index][recordFrame];
   }
   recordFrame = ++recordFrame >= totalFrames ? 0 : recordFrame;
  }
 }
}
