package ve.utilities;

import ve.instances.I;
import ve.trackElements.Bonus;
import ve.trackElements.TE;
import ve.ui.Keys;
import ve.ui.UI;

import java.util.ArrayList;
import java.util.List;

public enum Recorder {
 ;
 public static final int totalFrames = 500;
 public static int recordFrame;
 public static int gameFrame;
 public static long recordingsCount;
 public static long recorded;
 private static final double[] bonusX, bonusY, bonusZ;
 private static final int[] bonusHolder;
 public static final List<Vehicle> vehicles = new ArrayList<>();

 private static boolean[] recBoolean() {
  return new boolean[totalFrames];
 }

 private static long[] recLong() {
  return new long[totalFrames];
 }

 private static double[] recDouble() {
  return new double[totalFrames];
 }

 static {
  bonusHolder = new int[totalFrames];
  bonusX = recDouble();
  bonusY = recDouble();
  bonusZ = recDouble();
  for (int n = I.maxPlayers; --n >= 0; ) {
   vehicles.add(new Vehicle());
  }
 }

 enum Tsunami {
  ;
  static final double[] tsunamiX = recDouble(), tsunamiZ = recDouble();
  static final ve.environment.Tsunami.Direction[] direction = new ve.environment.Tsunami.Direction[totalFrames];
 }

 enum Lightning {
  ;
  static final double[] X = recDouble(), Z = recDouble();
  static final long[] strikeStage = recLong();
 }

 public static void updateFrame() {
  if (UI.status == UI.Status.play) {
   recorded = Math.min(recorded + 1, totalFrames);
   gameFrame = ++gameFrame >= totalFrames ? 0 : gameFrame;
  }
 }

 public static void recordGeneral() {
  bonusHolder[gameFrame] = Bonus.holder;
  bonusX[gameFrame] = TE.bonus.X;
  bonusY[gameFrame] = TE.bonus.Y;
  bonusZ[gameFrame] = TE.bonus.Z;
  if (ve.environment.Tsunami.exists) {
   Tsunami.tsunamiX[gameFrame] = ve.environment.Tsunami.X;
   Tsunami.tsunamiZ[gameFrame] = ve.environment.Tsunami.Z;
   Tsunami.direction[gameFrame] = ve.environment.Tsunami.direction;
  }
  if (ve.environment.storm.Lightning.exists) {
   Lightning.X[gameFrame] = ve.environment.storm.Lightning.X;
   Lightning.Z[gameFrame] = ve.environment.storm.Lightning.Z;
   Lightning.strikeStage[gameFrame] = ve.environment.storm.Lightning.strikeStage;
  }
 }

 public static void playBack() {
  if (UI.status == UI.Status.replay) {
   if (++recordingsCount >= recorded || Keys.enter || Keys.space || Keys.escape) {
    UI.status = UI.Status.paused;
    recordFrame = gameFrame - 1;
    while (recordFrame < 0) recordFrame += totalFrames;
    if (Keys.enter || Keys.space || Keys.escape) {
     UI.sound.play(1, 0);
    }
    Keys.up = Keys.down = Keys.enter = Keys.space = Keys.escape = false;
   }
   Bonus.holder = bonusHolder[recordFrame];
   TE.bonus.X = bonusX[recordFrame];
   TE.bonus.Y = bonusY[recordFrame];
   TE.bonus.Z = bonusZ[recordFrame];
   if (ve.environment.Tsunami.exists) {
    ve.environment.Tsunami.X = Tsunami.tsunamiX[recordFrame];
    ve.environment.Tsunami.Z = Tsunami.tsunamiZ[recordFrame];
    ve.environment.Tsunami.direction = Tsunami.direction[recordFrame];
   }
   if (ve.environment.storm.Lightning.exists) {
    ve.environment.storm.Lightning.X = Lightning.X[recordFrame];
    ve.environment.storm.Lightning.Z = Lightning.Z[recordFrame];
    ve.environment.storm.Lightning.strikeStage = Lightning.strikeStage[recordFrame];
   }
   for (int n = I.vehiclesInMatch; --n >= 0; ) {
    vehicles.get(n).playBack();
   }
   recordFrame = ++recordFrame >= totalFrames ? 0 : recordFrame;
  }
 }

 public static class Vehicle {
  private final double[] X = recDouble(), Y = recDouble(), Z = recDouble(),
  XZ = recDouble(), XY = recDouble(), YZ = recDouble(),
  vehicleTurretXZ = recDouble(), vehicleTurretYZ = recDouble(), speed = recDouble();
  private final double[] speedX = recDouble(), speedY = recDouble(), speedZ = recDouble();
  private final double[] speedXZ = recDouble(), speedYZ = recDouble();
  private final double[] damage = recDouble();
  private final boolean[] drive = recBoolean(), reverse = recBoolean();
  private final boolean[] special0 = recBoolean(), special1 = recBoolean(), special2 = recBoolean();//<-Must add more if more specials are added in the future
  private final boolean[] boost = recBoolean();
  private final double[] explodeStage = recDouble();
  private boolean subtractExplodeStage;//<-Had to be added to ensure correct operation--at least a whole array wasn't required!
  private final double[] spinnerSpeed = recDouble();
  private final boolean[] wrathEngaged = recBoolean();
  private final ve.vehicles.Physics.Mode[] mode = new ve.vehicles.Physics.Mode[totalFrames];

  public void recordVehicle(ve.vehicles.Vehicle vehicle) {
   if (UI.status == UI.Status.play) {
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
    speedX[gameFrame] = vehicle.speedX;
    speedY[gameFrame] = vehicle.speedY;
    speedZ[gameFrame] = vehicle.speedZ;
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
   ve.vehicles.Vehicle vehicle = I.vehicles.get(vehicles.indexOf(this));
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
   vehicle.speedX = speedX[recordFrame];
   vehicle.speedY = speedY[recordFrame];
   vehicle.speedZ = speedZ[recordFrame];
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
