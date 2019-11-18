package ve;

import ve.trackElements.TE;

import java.util.ArrayList;
import java.util.List;

enum Recorder {
 ;
 static final int totalFrames = 500;
 static int recordFrame;
 static int gameFrame;
 static long recordingsCount, recorded;
 private static final double[] bonusX, bonusY, bonusZ;
 private static final int[] bonusHolder;
 static final List<Vehicle> vehicles = new ArrayList<>();

 static {
  bonusHolder = new int[totalFrames];
  bonusX = new double[totalFrames];
  bonusY = new double[totalFrames];
  bonusZ = new double[totalFrames];
  for (int n = VE.maxPlayers; --n >= 0; ) {
   vehicles.add(new Vehicle());
  }
 }

 enum Tsunami {
  ;
  static final double[] tsunamiX = new double[totalFrames], tsunamiZ = new double[totalFrames];
  static final ve.environment.Tsunami.Direction[] direction = new ve.environment.Tsunami.Direction[totalFrames];
 }

 static void updateFrame() {
  if (VE.status == VE.Status.play) {
   recorded = Math.min(recorded + 1, totalFrames);
   gameFrame = ++gameFrame >= totalFrames ? 0 : gameFrame;
  }
 }

 static void recordGeneral() {
  bonusHolder[gameFrame] = VE.bonusHolder;
  bonusX[gameFrame] = TE.Bonus.X;
  bonusY[gameFrame] = TE.Bonus.Y;
  bonusZ[gameFrame] = TE.Bonus.Z;
  if (!ve.environment.Tsunami.parts.isEmpty()) {
   Tsunami.tsunamiX[gameFrame] = ve.environment.Tsunami.X;
   Tsunami.tsunamiZ[gameFrame] = ve.environment.Tsunami.Z;
   Tsunami.direction[gameFrame] = ve.environment.Tsunami.direction;
  }
 }

 static void playBack() {
  if (VE.status == VE.Status.replay) {
   if (++recordingsCount >= recorded || VE.Keys.Enter || VE.Keys.Space || VE.Keys.Escape) {
    VE.status = VE.Status.paused;
    recordFrame = gameFrame - 1;
    while (recordFrame < 0) recordFrame += totalFrames;
    if (VE.Keys.Enter || VE.Keys.Space || VE.Keys.Escape) {
     VE.Sounds.UI.play(1, 0);
    }
    VE.Keys.Up = VE.Keys.Down = VE.Keys.Enter = VE.Keys.Space = VE.Keys.Escape = false;
   }
   VE.bonusHolder = bonusHolder[recordFrame];
   TE.Bonus.X = bonusX[recordFrame];
   TE.Bonus.Y = bonusY[recordFrame];
   TE.Bonus.Z = bonusZ[recordFrame];
   ve.environment.Tsunami.X = Tsunami.tsunamiX[recordFrame];
   ve.environment.Tsunami.Z = Tsunami.tsunamiZ[recordFrame];
   ve.environment.Tsunami.direction = Tsunami.direction[recordFrame];
   for (int n = VE.vehiclesInMatch; --n >= 0; ) {
    vehicles.get(n).playBack();
   }
   recordFrame = ++recordFrame >= totalFrames ? 0 : recordFrame;
  }
 }

 static class Vehicle {
  private final double[] X = new double[totalFrames], Y = new double[totalFrames], Z = new double[totalFrames],
  XZ = new double[totalFrames], XY = new double[totalFrames], YZ = new double[totalFrames],
  vehicleTurretXZ = new double[totalFrames], vehicleTurretYZ = new double[totalFrames], speed = new double[totalFrames];
  private final double[][] speedX = new double[4][totalFrames], speedY = new double[4][totalFrames], speedZ = new double[4][totalFrames];
  private final double[] speedXZ = new double[totalFrames], speedYZ = new double[totalFrames];
  private final double[] damage = new double[totalFrames];
  private final boolean[] drive = new boolean[totalFrames], reverse = new boolean[totalFrames];
  private final boolean[] special0 = new boolean[totalFrames], special1 = new boolean[totalFrames], special2 = new boolean[totalFrames];//<-Must add more if more specials are added in the future
  private final boolean[] boost = new boolean[totalFrames];
  private final double[] explodeStage = new double[totalFrames];
  private boolean subtractExplodeStage;//<-Had to be added to ensure correct operation
  private final double[] spinnerSpeed = new double[totalFrames];
  private final boolean[] wrathEngaged = new boolean[totalFrames];
  private final ve.vehicles.Physics.Mode[] mode = new ve.vehicles.Physics.Mode[totalFrames];

  void recordVehicle(ve.vehicles.Vehicle vehicle) {
   if (VE.status == VE.Status.play) {
    X[gameFrame] = vehicle.X;
    Y[gameFrame] = vehicle.Y;
    Z[gameFrame] = vehicle.Z;
    XZ[gameFrame] = vehicle.XZ;
    YZ[gameFrame] = vehicle.YZ;
    XY[gameFrame] = vehicle.XY;
    if (vehicle.VT != null) {
     vehicleTurretXZ[gameFrame] = vehicle.VT.XZ;
     vehicleTurretYZ[gameFrame] = vehicle.VT.YZ;
    }
    speedXZ[gameFrame] = vehicle.P.speedXZ;
    speedYZ[gameFrame] = vehicle.P.speedYZ;
    for (int n = 4; --n >= 0; ) {
     speedX[n][gameFrame] = vehicle.wheels.get(n).speedX;
     speedY[n][gameFrame] = vehicle.wheels.get(n).speedY;
     speedZ[n][gameFrame] = vehicle.wheels.get(n).speedZ;
    }
    speed[gameFrame] = vehicle.P.speed;
    damage[gameFrame] = vehicle.getDamage(false);
    mode[gameFrame] = vehicle.P.mode;
    drive[gameFrame] = vehicle.drive;
    reverse[gameFrame] = vehicle.reverse;
    long specialsQuantity = vehicle.specials.size();
    if (specialsQuantity > 0) {
     special0[gameFrame] = vehicle.specials.get(0).fire;
    }
    if (specialsQuantity > 1) {
     special1[gameFrame] = vehicle.specials.get(1).fire;
    }
    if (specialsQuantity > 2) {
     special2[gameFrame] = vehicle.specials.get(2).fire;
    }
    boost[gameFrame] = vehicle.boost;
    explodeStage[gameFrame] = vehicle.P.explodeStage;
    subtractExplodeStage = vehicle.P.subtractExplodeStage;
    if (vehicle.spinner != null) {
     spinnerSpeed[gameFrame] = vehicle.spinner.speed;
    }
    wrathEngaged[gameFrame] = vehicle.P.wrathEngaged;
   }
  }

  void playBack() {
   ve.vehicles.Vehicle vehicle = VE.vehicles.get(vehicles.indexOf(this));
   vehicle.X = X[recordFrame];
   vehicle.Y = Y[recordFrame];
   vehicle.Z = Z[recordFrame];
   vehicle.XZ = XZ[recordFrame];
   vehicle.YZ = YZ[recordFrame];
   vehicle.XY = XY[recordFrame];
   if (vehicle.VT != null) {
    vehicle.VT.XZ = vehicleTurretXZ[recordFrame];
    vehicle.VT.YZ = vehicleTurretYZ[recordFrame];
   }
   vehicle.P.speedXZ = speedXZ[recordFrame];
   vehicle.P.speedYZ = speedYZ[recordFrame];
   for (int n1 = 4; --n1 >= 0; ) {
    vehicle.wheels.get(n1).speedX = speedX[n1][recordFrame];
    vehicle.wheels.get(n1).speedY = speedY[n1][recordFrame];
    vehicle.wheels.get(n1).speedZ = speedZ[n1][recordFrame];
   }
   vehicle.P.speed = speed[recordFrame];
   vehicle.setDamage(damage[recordFrame]);
   vehicle.P.mode = mode[recordFrame];
   vehicle.drive = drive[recordFrame];
   vehicle.reverse = reverse[recordFrame];
   long specialsQuantity = vehicle.specials.size();
   if (specialsQuantity > 0) {
    vehicle.specials.get(0).fire = special0[recordFrame];
   }
   if (specialsQuantity > 1) {
    vehicle.specials.get(1).fire = special1[recordFrame];
   }
   if (specialsQuantity > 2) {
    vehicle.specials.get(2).fire = special2[recordFrame];
   }
   vehicle.boost = boost[recordFrame];
   vehicle.P.explodeStage = explodeStage[recordFrame];
   vehicle.P.subtractExplodeStage = subtractExplodeStage;
   if (vehicle.spinner != null) {
    vehicle.spinner.speed = spinnerSpeed[recordFrame];
   }
   vehicle.P.wrathEngaged = wrathEngaged[recordFrame];
  }
 }
}
