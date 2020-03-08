package ve.vehicles;

import ve.environment.Pool;
import ve.environment.Tsunami;
import ve.instances.I;
import ve.ui.Match;
import ve.ui.Mouse;
import ve.utilities.*;
import ve.utilities.sound.FireAndForget;
import ve.utilities.sound.Controlled;
import ve.utilities.sound.Sound;
import ve.utilities.sound.Sounds;
import ve.vehicles.specials.Special;

import java.io.File;
import java.util.Objects;

public class VehicleAudio {
 VehicleAudio(Vehicle vehicle) {
  V = vehicle;
 }

 private final Vehicle V;
 int engineClipQuantity;
 public double distanceVehicleToCamera;
 double splashing = Double.NaN;
 private boolean skidding;
 boolean scraping;
 private long engineIndex, exhaustIndex;
 private double landTimer, chuffTimer, forceTimer;
 double crashTimer;
 private boolean engineDiscord;
 double engineTuneRatio = 2, enginePitchBase = 1;
 EngineTuning engineTuning = EngineTuning.equalTemperament;

 enum EngineTuning {equalTemperament, harmonicSeries}

 //*Keep here so we can make sure everything's being closed at a glance
 Controlled burn;
 Sound land;//<-Must be a general Sound class
 FireAndForget repair;
 private Controlled grind;
 private Controlled boost;
 Sound gate;
 Controlled turret;
 private Controlled splash;
 private Controlled splashOverSurface;
 public Controlled tsunamiSplash;
 public FireAndForget mineExplosion;
 private Controlled engine;
 private Controlled turbineThrust;
 FireAndForget death, deathExplode;
 private Controlled fly;
 public FireAndForget crashSoft;
 public FireAndForget crashHard;
 public FireAndForget crashDestroy;
 private Controlled skidHard;
 private Controlled skidOff;
 private FireAndForget exhaust;
 private FireAndForget force;
 private Controlled scrape;
 private FireAndForget chuff;
 public FireAndForget hitShot;
 public FireAndForget hitRicochet;
 public FireAndForget hitExplosive;
 private FireAndForget trainNoise;
 private Controlled trainDrive;//Separate entities for train engine is better
 public Controlled spinner;
 public FireAndForget massiveHit;
 private Controlled backUp;
 FireAndForget nuke, nukeMax;//<-Keeping these split, because it's easier

 //Keep order identical between declarations and 'close()'!
 public void close() {//*
  for (Special special : V.specials) {
   if (special.sound != null) {
    special.sound.close();
   }
  }
  if (burn != null) burn.close();
  if (land != null) land.close();
  if (repair != null) repair.close();
  if (grind != null) grind.close();
  if (boost != null) boost.close();
  if (gate != null) gate.close();
  if (turret != null) turret.close();
  if (splash != null) splash.close();
  if (splashOverSurface != null) splashOverSurface.close();
  if (tsunamiSplash != null) tsunamiSplash.close();
  if (mineExplosion != null) mineExplosion.close();
  if (engine != null) engine.close();
  if (turbineThrust != null) turbineThrust.close();
  if (death != null) death.close();
  if (deathExplode != null) deathExplode.close();
  if (fly != null) fly.close();
  if (crashSoft != null) crashSoft.close();
  if (crashHard != null) crashHard.close();
  if (crashDestroy != null) crashDestroy.close();
  if (skidHard != null) skidHard.close();
  if (skidOff != null) skidOff.close();
  if (exhaust != null) exhaust.close();
  if (force != null) force.close();
  if (scrape != null) scrape.close();
  if (chuff != null) chuff.close();
  if (hitShot != null) hitShot.close();
  if (hitRicochet != null) hitRicochet.close();
  if (hitExplosive != null) hitExplosive.close();
  if (trainNoise != null) trainNoise.close();
  if (trainDrive != null) trainDrive.close();
  if (spinner != null) spinner.close();
  if (massiveHit != null) massiveHit.close();
  if (backUp != null) backUp.close();
  if (nuke != null) nuke.close();
  if (nukeMax != null) nukeMax.close();
  if (V.MNB != null && V.MNB.travel != null) {
   V.MNB.travel.close();
  }
 }

 void setDistance() {
  distanceVehicleToCamera =
  V.index == I.vehiclePerspective && Camera.view == Camera.View.driver ? 0 :
  Math.sqrt(U.distance(V)) * Sounds.standardGain(1);
 }

 void skid() {
  if (V.isIntegral() && !V.P.flipped() && V.contact == Physics.Contact.rubber) {
   if (V.P.terrainProperties.contains(D.thick(D.hard))) {
    if (!skidHard.running()) {
     skidHard.resume(Double.NaN, distanceVehicleToCamera);
    }
   } else {
    if (!skidOff.running()) {
     skidOff.resume(Double.NaN, distanceVehicleToCamera);
    }
   }
   skidding = true;
  }
 }

 void land() {
  if (V.isIntegral() && landTimer <= 0 && !V.isFixed()) {
   if (V.landType == Physics.Landing.crash) {
    crashHard.play(Double.NaN, distanceVehicleToCamera);
   } else if ((Math.abs(V.YZ) < 30 && Math.abs(V.XY) < 30) || (Math.abs(V.YZ) > 150 && Math.abs(V.XY) > 150)) {
    land.play(Double.NaN, distanceVehicleToCamera);
   } else {
    crashSoft.play(Double.NaN, distanceVehicleToCamera);
   }
   landTimer = 5;
   if (V.bounce > .9) {
    land.play(Double.NaN, V.VA.distanceVehicleToCamera);
   }
  }
 }

 /**
  * For Linux systems, this loading is designed to skip certain audio of lower priority.
  * Though the Sound classes get declared as needed, they may not actually load the audio data within. This allows the program to run normally without throwing any nullPointer exceptions.
  */
 void load() {
  if (!V.isFixed()) {
   engine = new Controlled();
   if (engineClipQuantity < 1) {
    File[] engines = new File(Sounds.folder).listFiles((D, name) -> name.startsWith(V.engine.name() + "-") && name.endsWith(Sounds.extension));
    engineClipQuantity = Objects.requireNonNull(engines).length;
    //Saved in case it's needed again->!U.onLinux || n < 1 || n == engineClipQuantity - 1/*<-Will only load the idle and max rev sounds if on Linux*/
    for (long n = 0; n < engineClipQuantity; n++) {
     engine.addClip(V.engine.name() + "-" + n, enginePitchBase);
    }
   } else {
    for (int n = 0; n < engineClipQuantity; n++) {
     engine.addClip(V.engine.name(), enginePitchBase);
     if (engineTuning == EngineTuning.harmonicSeries) {
      enginePitchBase += engineTuneRatio;
     } else {
      enginePitchBase *= StrictMath.pow(engineTuneRatio, 1 / (double) (engineClipQuantity - 1));
     }
    }
   }
   if (V.contact == Physics.Contact.rubber) {
    skidHard = new Controlled(D.skidHard, Double.POSITIVE_INFINITY);
    skidOff = new Controlled(D.skidOff, Double.POSITIVE_INFINITY);
   }
   scrape = new Controlled(D.scrape, Double.POSITIVE_INFINITY);
   force = Sounds.softwareBased ? Sounds.force : new FireAndForget(D.force, Double.POSITIVE_INFINITY);
  }
  death = Sounds.softwareBased ? Sounds.death : new FireAndForget(D.death);
  if (V.explosionsWhenDestroyed > 0 && !V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name())) {
   deathExplode = Sounds.softwareBased ? Sounds.deathExplode : new FireAndForget(D.deathExplode, Double.POSITIVE_INFINITY);
  }
  crashHard = Sounds.softwareBased ? Sounds.crashHard : new FireAndForget(D.crashHard, Double.POSITIVE_INFINITY);
  crashDestroy = Sounds.softwareBased ? Sounds.crashDestroy : new FireAndForget(D.crashDestroy, Double.POSITIVE_INFINITY);
  crashSoft = Sounds.softwareBased ? Sounds.crashSoft : new FireAndForget(D.crashSoft, Double.POSITIVE_INFINITY);
  if (V.type == Vehicle.Type.aircraft && !V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name()) && !V.floats) {
   fly = new Controlled("fly", Double.POSITIVE_INFINITY);
  }
  if (V.speedBoost > 0 && V.engine != Vehicle.Engine.turbine) {
   boost = new Controlled("boost");
  }
  if (!V.isFixed() && V.landType != Physics.Landing.crash) {
   land = new Sound(V.landType.name(), Double.POSITIVE_INFINITY);
  }
  if (V.engine.name().contains("truck") || V.engine == Vehicle.Engine.tank || V.engine == Vehicle.Engine.massive) {
   grind = new Controlled("grind");
  }
  repair = Sounds.softwareBased ? Sounds.repair : new FireAndForget(D.repair);
  if (V.explosionType != Vehicle.ExplosionType.maxnuclear) {
   burn = new Controlled("burn");
  }
  if (!Double.isNaN(V.exhausting)) {
   exhaust = Sounds.softwareBased ? Sounds.exhaust : new FireAndForget(D.exhaust, Double.POSITIVE_INFINITY);
  }
  if (Tsunami.exists) {
   tsunamiSplash = new Controlled(D.tsunamiSplash);
  }
  if (!V.isFixed()) {
   if (Pool.exists) {
    splash = new Controlled("splash");
    splashOverSurface = new Controlled("splashOver");
    splashing = 0;
   }
   gate = new Controlled("gateSpeed");
   gate.addClip("gateSlow");
  }
  boolean loadHitExplosive = false, loadMineExplosion = false;
  for (Special special : V.specials) {
   loadHitExplosive = special.type.name().contains(Special.Type.shell.name()) || special.type == Special.Type.missile || special.type == Special.Type.bomb || special.type == Special.Type.mine || V.explosionType.name().startsWith(Vehicle.ExplosionType.nuclear.name()) || loadHitExplosive;
   loadMineExplosion = special.type == Special.Type.mine || loadMineExplosion;
  }
  if (loadHitExplosive) {
   hitExplosive = Sounds.softwareBased ? Sounds.hitExplosive : new FireAndForget(D.hitExplosive, Double.POSITIVE_INFINITY);
  }
  if (loadMineExplosion) {
   mineExplosion = Sounds.softwareBased ? Sounds.mineExplosion : new FireAndForget(D.mineExplode);
  }
  boolean hasSpinner = V.spinner != null;
  if (hasSpinner) {
   spinner = new Controlled();
   int spinnerClips = 8, n;
   double equalTemperament = 1, multiple = StrictMath.pow(2, 1 / 3.);
   for (n = spinnerClips - 1; --n >= 0; ) {
    equalTemperament /= multiple;
   }
   for (n = spinnerClips; --n >= 0; ) {
    spinner.addClip(D.spinner, equalTemperament);
    equalTemperament *= multiple;
   }
  }
  if (hasSpinner || V.dealsMassiveDamage()) {
   massiveHit = Sounds.softwareBased ? Sounds.massiveHit : new FireAndForget(D.massiveHit, Double.POSITIVE_INFINITY);
  }
  if (V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name())) {
   if (V.explosionType == Vehicle.ExplosionType.maxnuclear) {
    nukeMax = Sounds.softwareBased ? Sounds.nukeMax : new FireAndForget(D.nukeMax);
    V.MNB.travel = new Controlled("nukeMaxTravel");
   } else {
    nuke = Sounds.softwareBased ? Sounds.nuke : new FireAndForget(D.nuke, Double.POSITIVE_INFINITY);
   }
  }
  if (V.engine == Vehicle.Engine.authentictruck) {
   chuff = new FireAndForget(D.chuff, 5);
   backUp = new Controlled("backUp");
  } else if (V.engine == Vehicle.Engine.train) {
   chuff = new FireAndForget(D.chuff, 4);
   trainNoise = Sounds.softwareBased ? Sounds.trainNoise : new FireAndForget(D.train, Double.POSITIVE_INFINITY);
   trainDrive = new Controlled("trainDrive", 2);
  } else if (V.engine == Vehicle.Engine.turbine) {
   turbineThrust = new Controlled("turbineThrust");
  }
 }

 void run(boolean gamePlay) {
  if (V.engine == Vehicle.Engine.train && gamePlay && Match.started) {
   if (Math.abs(V.P.speed) * U.tick > U.random(5000.)) {
    trainNoise.play(Double.NaN, distanceVehicleToCamera);
   }
   if (U.startsWith(V.P.mode.name(), D.drive, Physics.Mode.neutral.name()) && !V.destroyed && (V.drive || V.reverse)) {
    if (Math.abs(V.P.speed) > V.topSpeeds[1] * .75 && !trainDrive.running(0)) {
     trainDrive.playIfNotPlaying(1, distanceVehicleToCamera);
    } else if (!trainDrive.running(1)) {
     trainDrive.playIfNotPlaying(0, distanceVehicleToCamera);
    }
   }
  }
  if (backUp != null) {
   if (gamePlay && V.P.speed < 0 && !V.destroyed) {
    backUp.loop(distanceVehicleToCamera);
   } else {
    backUp.stop();
   }
  }
  engineDiscord = false;
  if (V.isIntegral() && gamePlay) {
   if (chuff != null && chuffTimer <= 0 && Math.abs(V.P.speed) <= V.accelerationStages[0] * U.tick && (V.drive || V.reverse)) {
    chuff.play(Double.NaN, distanceVehicleToCamera);
    chuff.play(Double.NaN, distanceVehicleToCamera);
    chuffTimer = 22;
   }
   if (fly != null && V.P.mode == Physics.Mode.fly && (V.drive || V.reverse || V.turnL || V.turnR || (V.steerByMouse && (Math.abs(Mouse.steerX) > U.random(2000.) || Math.abs(Mouse.steerY) > U.random(2000.))))) {
    fly.playIfNotPlaying(U.random(fly.clipHolders.size()), distanceVehicleToCamera);
   }
   if (!V.isFixed()) {
    int n = 0;
    if (V.P.mode != Physics.Mode.stunt) {
     n = V.type == Vehicle.Type.vehicle && V.steerInPlace && (V.turnL || V.turnR) ? 1 : n;
     boolean aircraft = V.type == Vehicle.Type.aircraft, flying = V.P.mode == Physics.Mode.fly,
     driveGet = !flying && aircraft ? V.drive || V.drive2 : flying ? V.drive2 : V.drive,
     reverseGet = !flying && aircraft ? V.reverse || V.reverse2 : flying ? V.reverse2 : V.reverse;
     if (driveGet || reverseGet || V.P.mode == Physics.Mode.fly) {
      n =
      driveGet && !(V.P.flipped() && V.P.mode.name().startsWith(D.drive)) && V.P.mode != Physics.Mode.fly &&
      U.containsEnum(V.engine, Vehicle.Engine.prop, Vehicle.Engine.jet, Vehicle.Engine.turbine, Vehicle.Engine.rocket) ?
      engineClipQuantity - 1 :
      Math.max((int) (engineClipQuantity * (Math.abs(V.P.speed) / V.topSpeeds[1])), V.floats ? 0 : 1);
      engineDiscord = grind != null && V.P.mode == Physics.Mode.driveSolid && V.P.againstWall();//<-Ignoring drivePool, since engine probably wouldn't 'grind' in it
     }
    }
    n = V.engine == Vehicle.Engine.turbine ? Math.max((int) (engineClipQuantity * (Math.abs(V.P.netSpeed) / V.topSpeeds[1])), n) : n;
    enginePowerSwitch(n);
    engineIndex = Math.min(n, engineClipQuantity - 1);
   }
  } else if (!V.isFixed()) {
   enginePowerSwitch(-1);
   engineIndex = -1;
  }
  if (grind != null) {
   if (gamePlay && !V.destroyed && V.P.mode != Physics.Mode.stunt && ((V.drive && V.P.speed < 0) || (V.reverse && V.P.speed > 0))) {
    grind.loop(distanceVehicleToCamera);
   } else {
    grind.stop();
   }
  }
  if (V.speedBoost > 0) {
   if (V.boost && gamePlay && !V.destroyed) {
    boost.loop(distanceVehicleToCamera);
   } else {
    boost.stop();
   }
  }
  if (turret != null && V.isFixed()) {//<-Checking null is not sufficient because vehicles with turrets borrow the turret field
   if (gamePlay && !V.destroyed && (V.turnL || V.turnR || V.drive || V.reverse || V.steerByMouse)) {
    turret.loop(distanceVehicleToCamera);
   } else {
    turret.stop();
   }
  }
  if (force != null && gamePlay) {
   forceTimer += U.random(V.P.netSpeed) * U.tick;
   int speedCheck = V.P.netSpeed > 1000 ? 5 : V.P.netSpeed > 500 ? 4 : V.P.netSpeed > 250 ? 3 : 2;
   if (V.steerInPlace && V.P.mode == Physics.Mode.driveSolid && (V.turnL || V.turnR) && !V.P.flipped() && speedCheck < 3) {
    force.play(U.random(2), distanceVehicleToCamera);
    forceTimer = 0;
   }
   if (forceTimer > 800) {
    force.play(U.random(speedCheck), distanceVehicleToCamera);
    forceTimer = 0;
   }
  }
  chuffTimer -= chuffTimer > 0 ? U.tick : 0;
  crashTimer -= crashTimer > 0 ? U.tick : 0;
  landTimer -= landTimer > 0 ? U.tick : 0;
  if (!Double.isNaN(splashing)) {
   if (splashing > 150 && Camera.C.Y <= 0) {
    splashOverSurface.loop(distanceVehicleToCamera);
    splash.stop();
   } else if (splashing > 0) {
    splash.loop(distanceVehicleToCamera);
    splashOverSurface.stop();
   } else {
    splash.stop();
    splashOverSurface.stop();
   }
   splashing = 0;
  }
  if (V.contact == Physics.Contact.rubber) {
   if (!skidding) {
    skidHard.stop();
    skidOff.stop();
   } else if (!V.P.terrainProperties.contains(D.thick(D.hard))) {
    skidHard.stop();
   } else {
    skidOff.stop();
   }
   skidding = false;
  }
  if (scrape != null) {
   if (scraping && V.P.netSpeed > 100) {
    int n1;
    for (n1 = scrape.clipHolders.size(); --n1 >= 0; ) {
     if (scrape.running(n1)) {
      n1 = -2;
      break;
     }
    }
    if (n1 > -2) {
     scrape.resume(Double.NaN, distanceVehicleToCamera);
    }
   } else {
    scrape.stop();
   }
   scraping = false;
  }
  if (!Double.isNaN(V.exhausting)) {
   V.exhausting -= V.exhausting > 0 ? U.tick : 0;
   if (exhaustIndex != engineIndex) {
    if (U.random() < 1 / (double) engineClipQuantity) {
     V.exhausting = 5;
     exhaust.play(Double.NaN, distanceVehicleToCamera);
    }
    exhaustIndex = engineIndex;
   }
  }
  if (Match.muteSound || !V.destroyed || !gamePlay) {
   if (V.explosionType == Vehicle.ExplosionType.maxnuclear) {
    V.MNB.travel.stop();
   } else {
    burn.stop();//<-No burn sound on max nukes
   }
  }
  if (Match.muteSound || !V.isIntegral() || !gamePlay) {
   for (Special special : V.specials) {
    if (special.type == Special.Type.phantom) {
     special.sound.stop();
    }
   }
   if (boost != null) {
    boost.stop();
   }
   if (grind != null) {
    grind.stop();
   }
   if (trainDrive != null) {
    trainDrive.stop();
   }
   if (turret != null) {
    turret.stop();
   }
   if (!V.isFixed()) {
    engine.stop();
   }
   if (turbineThrust != null) {
    turbineThrust.stop();
   }
  }
 }

 private void enginePowerSwitch(int n) {
  n = Math.min(n, engineClipQuantity - 1);
  for (int n1 = engineClipQuantity; --n1 >= 0; ) {
   if (n1 != n) {
    engine.stop(n1);
   }
  }
  if (n > -1) {
   engine.loop(n, distanceVehicleToCamera);
   if (engineDiscord) {
    engine.randomizeFramePosition(n);
   }
   if (V.engine == Vehicle.Engine.turbine) {
    double thrustGain = Math.max(0, StrictMath.pow(1 / (Math.abs(V.P.netSpeed) / V.topSpeeds[1]), 4));
    turbineThrust.loop(distanceVehicleToCamera + (n >= engineClipQuantity - 1 ? 0 : thrustGain));
   }
  }
  if (V.engine == Vehicle.Engine.turbine && n < 1) {//<-Statement split so that echo stops looping when at lowest speed
   turbineThrust.stop();
  }
 }
}
