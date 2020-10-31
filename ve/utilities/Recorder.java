package ve.utilities;

import ve.environment.Tornado;
import ve.environment.Tsunami;
import ve.environment.Volcano;
import ve.environment.storm.Lightning;
import ve.instances.I;
import ve.trackElements.Bonus;
import ve.ui.Keys;
import ve.ui.UI;
import ve.vehicles.Physics;
import ve.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.List;

public enum Recorder {
 ;
 public static final int totalFrames = U.refreshRate * 10;//<-When the user FPS matches the refresh rate, game replays should be ~10 seconds long
 public static int recordFrame;
 public static int gameFrame;
 public static long recordingsCount;
 public static long recorded;
 private static final double[] bonusX, bonusY, bonusZ;
 private static final int[] bonusHolder;
 public static final List<recordedVehicle> vehicles = new ArrayList<>();

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
   vehicles.add(new recordedVehicle());
  }
 }

 enum recordedTornado {
  ;
  static final double[] X = recDouble(), Z = recDouble();
 }

 enum recordedTsunami {
  ;
  static final double[] X = recDouble(), Z = recDouble();
  static final Tsunami.Direction[] direction = new Tsunami.Direction[totalFrames];
 }

 enum recordedLightning {
  ;
  static final double[] X = recDouble(), Z = recDouble();
  static final double[] strikeStage = recDouble();
 }

 enum recordedVolcano {
  ;
  static double[] eruptionStage = recDouble();
 }

 public static void updateFrame() {
  if (UI.status == UI.Status.play) {
   recorded = Math.min(recorded + 1, totalFrames);
   gameFrame = ++gameFrame >= totalFrames ? 0 : gameFrame;
  }
 }

 public static void recordGeneral() {
  bonusHolder[gameFrame] = Bonus.holder;
  bonusX[gameFrame] = Bonus.C.X;
  bonusY[gameFrame] = Bonus.C.Y;
  bonusZ[gameFrame] = Bonus.C.Z;
  if (Tornado.exists()) {
   recordedTornado.X[gameFrame] = Tornado.parts.get(0).X;
   recordedTornado.Z[gameFrame] = Tornado.parts.get(0).Z;
  }
  if (Tsunami.exists) {
   recordedTsunami.X[gameFrame] = Tsunami.X;
   recordedTsunami.Z[gameFrame] = Tsunami.Z;
   recordedTsunami.direction[gameFrame] = Tsunami.direction;
  }
  if (Lightning.exists) {
   recordedLightning.X[gameFrame] = Lightning.X;
   recordedLightning.Z[gameFrame] = Lightning.Z;
   recordedLightning.strikeStage[gameFrame] = Lightning.strikeStage;
  }
  if (Volcano.isActive()) {
   recordedVolcano.eruptionStage[gameFrame] = Volcano.eruptionStage;
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
   Bonus.C.X = bonusX[recordFrame];
   Bonus.C.Y = bonusY[recordFrame];
   Bonus.C.Z = bonusZ[recordFrame];
   if (Tornado.exists()) {
    Tornado.parts.get(0).X = recordedTornado.X[recordFrame];
    Tornado.parts.get(0).Z = recordedTornado.Z[recordFrame];
   }
   if (Tsunami.exists) {
    Tsunami.X = recordedTsunami.X[recordFrame];
    Tsunami.Z = recordedTsunami.Z[recordFrame];
    Tsunami.direction = recordedTsunami.direction[recordFrame];
   }
   if (Lightning.exists) {
    Lightning.X = recordedLightning.X[recordFrame];
    Lightning.Z = recordedLightning.Z[recordFrame];
    Lightning.strikeStage = recordedLightning.strikeStage[recordFrame];
   }
   if (Volcano.isActive()) {
    Volcano.eruptionStage = recordedVolcano.eruptionStage[recordFrame];
   }
   for (int n = I.vehiclesInMatch; --n >= 0; ) {
    vehicles.get(n).playBack();
   }
   recordFrame = ++recordFrame >= totalFrames ? 0 : recordFrame;
  }
 }

 public static class recordedVehicle {
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
  private final Physics.Mode[] mode = new Physics.Mode[totalFrames];

  public void recordVehicle(Vehicle vehicle) {
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
   Vehicle vehicle = I.vehicles.get(vehicles.indexOf(this));
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
