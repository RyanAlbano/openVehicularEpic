package ve.vehicles;

import ve.Camera;
import ve.Sound;
import ve.VE;
import ve.environment.E;
import ve.environment.Tsunami;
import ve.utilities.U;
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
 private double landTimer;
 private double chuffTimer;
 private double forceTimer;
 double crashTimer;
 double engineTuneRatio = 2, enginePitchBase = 1;
 EngineTuning engineTuning = EngineTuning.equalTemperament;

 enum EngineTuning {equalTemperament, harmonicSeries}

 //*Keep here so we can make sure everything's being closed at a glance
 Sound repair;
 Sound land;
 private Sound boost;
 Sound gate;
 Sound turret;
 Sound burn;
 Sound grind;
 Sound airEngage;
 private Sound splash;
 private Sound splashOverSurface;
 public Sound tsunamiSplash;
 Sound mineExplosion;
 public Sound crashSoft;
 public Sound crashHard;
 public Sound crashDestroy;
 public Sound hitShot;
 public Sound hitRicochet;
 public Sound hitExplosive;
 Sound explode;
 private Sound force;
 private Sound scrape;
 private Sound skidHard;
 private Sound skidOff;
 private Sound exhaust;
 private Sound fly;
 private Sound train;
 public Sound massiveHit;
 public Sound spinner;
 Sound nuke;
 private Sound chuff;
 private Sound backUp;
 private Sound engine;
 private Sound turbineThrust;//*

 public void close() {//*
  for (Special special : V.specials) {
   if (special.sound != null) {
    special.sound.close();
   }
  }
  if (airEngage != null) airEngage.close();
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
  if (explode != null) explode.close();
  if (fly != null) fly.close();
  if (crashSoft != null) crashSoft.close();
  if (crashHard != null) crashHard.close();
  if (crashDestroy != null) crashDestroy.close();
  if (exhaust != null) exhaust.close();
  if (force != null) force.close();
  if (scrape != null) scrape.close();
  if (chuff != null) chuff.close();
  if (hitShot != null) hitShot.close();
  if (hitRicochet != null) hitRicochet.close();
  if (hitExplosive != null) hitExplosive.close();
  if (skidHard != null) skidHard.close();
  if (skidOff != null) skidOff.close();
  if (train != null) train.close();
  if (spinner != null) spinner.close();
  if (massiveHit != null) massiveHit.close();
  if (backUp != null) backUp.close();
  if (nuke != null) nuke.close();
 }

 void skid() {
  if (V.isIntegral() && !V.P.flipped && V.contact == Physics.Contact.rubber) {
   if (V.P.terrainProperties.contains(" hard ")) {
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
  }
 }

 void load() {//Sounds are loaded by order of importance, as not all of them may load on Linux systems. It's not as elegant but should be done.
  if (!V.isFixed()) {
   if (engineClipQuantity < 1) {
    File[] engines = new File(U.soundFolder).listFiles((D, name) -> name.startsWith(V.engine.name() + "-") && name.endsWith(U.soundExtension));
    engineClipQuantity = Objects.requireNonNull(engines).length;
    engine = new Sound(V.engine.name() + "-", engineClipQuantity, enginePitchBase);
   } else {
    engine = new Sound();
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
    skidHard = new Sound("skidHard", Double.POSITIVE_INFINITY);
    skidOff = new Sound("skidOff", Double.POSITIVE_INFINITY);
   }
   scrape = new Sound("scrape", Double.POSITIVE_INFINITY);
   force = new Sound("force", 5);
  }
  boolean towerOnly = false;
  for (Special special : V.specials) {
   if (special.type == Special.Type.energy) {//<-Is crude, but works for now
    towerOnly = true;
    break;
   }
  }
  if (!towerOnly && (V.isFixed() || V.VT != null)) {
   turret = new Sound("turret");
  }
  explode = new Sound("explode", V.explosionsWhenDestroyed > 0 && !V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name()) ? 2 : 1);
  crashHard = new Sound("crashHard", Double.POSITIVE_INFINITY);
  crashDestroy = new Sound("crashDestroy", Double.POSITIVE_INFINITY);
  crashSoft = new Sound("crashSoft", Double.POSITIVE_INFINITY);
  if (V.type == Vehicle.Type.aircraft && !V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name()) && !V.floats) {
   fly = new Sound("fly", Double.POSITIVE_INFINITY);
  }
  boost = V.speedBoost > 0 && V.engine != Vehicle.Engine.turbine ? new Sound("boost") : boost;
  land = !V.isFixed() && V.landType != Physics.Landing.crash ? new Sound(V.landType.name(), Double.POSITIVE_INFINITY) : land;
  grind = V.engine.name().contains("truck") || V.engine == Vehicle.Engine.tank || V.engine == Vehicle.Engine.massive ? new Sound("grind") : grind;
  repair = new Sound("repair");
  burn = new Sound("burn");
  exhaust = Double.isNaN(V.exhausting) ? exhaust : new Sound("exhaust", Double.POSITIVE_INFINITY);
  tsunamiSplash = Tsunami.parts.isEmpty() ? tsunamiSplash : new Sound("tsunamiSplash");
  if (!V.isFixed()) {
   if (E.Pool.exists) {
    splash = new Sound("splash");
    splashOverSurface = new Sound("splashOver");
    splashing = 0;
   }
   gate = new Sound("gateSpeed");
   gate.addClip("gateSlow", 1);
  }
  boolean loadHitExplosive = false, loadMineExplosion = false;
  for (Special special : V.specials) {
   loadHitExplosive = special.type.name().contains(Special.Type.shell.name()) || special.type == Special.Type.missile || special.type == Special.Type.bomb || special.type == Special.Type.mine || V.explosionType.name().startsWith(Vehicle.ExplosionType.nuclear.name()) || loadHitExplosive;
   loadMineExplosion = special.type == Special.Type.mine || loadMineExplosion;
  }
  hitExplosive = loadHitExplosive ? new Sound("hitExplosive", Double.POSITIVE_INFINITY) : hitExplosive;
  mineExplosion = loadMineExplosion ? new Sound("mineExplode") : mineExplosion;
  boolean hasSpinner = V.spinner != null;
  if (hasSpinner) {
   spinner = new Sound();
   int spinnerClips = 8, n;
   double equalTemperament = 1, multiple = StrictMath.pow(2, 1 / 3.);
   for (n = spinnerClips - 1; --n >= 0; ) {
    equalTemperament /= multiple;
   }
   for (n = spinnerClips; --n >= 0; ) {
    spinner.addClip("spinner", equalTemperament);
    equalTemperament *= multiple;
   }
  }
  if (V.type != Vehicle.Type.aircraft && (hasSpinner || (V.damageDealt[0] >= 100 || V.damageDealt[1] >= 100 || V.damageDealt[2] >= 100 || V.damageDealt[3] >= 100))) {
   massiveHit = new Sound("massiveHit", Double.POSITIVE_INFINITY);
  }
  if (V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name())) {
   nuke = new Sound("nuke" + (V.explosionType == Vehicle.ExplosionType.maxnuclear ? "Max" : ""), 2);
  }
  if (V.engine == Vehicle.Engine.authentictruck) {
   chuff = new Sound("chuff", 5);
   backUp = new Sound("backUp");
  } else if (V.engine == Vehicle.Engine.train) {
   chuff = new Sound("chuff", 4);
   train = new Sound("train", 11);
  } else if (V.engine == Vehicle.Engine.turbine) {
   turbineThrust = new Sound("turbineThrust");
  }
  airEngage = V.isFixed() ? airEngage : new Sound("aA");
 }

 void run(boolean gamePlay) {
  if (V.engine == Vehicle.Engine.train && gamePlay && VE.Match.started) {
   if (Math.abs(V.P.speed) * VE.tick > U.random(5000.)) {
    train.playIfNotPlaying(U.random(9), distanceVehicleToCamera);
   }
   if (U.startsWith(V.P.mode.name(), Physics.Mode.drive.name(), Physics.Mode.neutral.name()) && !V.destroyed && (V.drive || V.reverse)) {
    if (Math.abs(V.P.speed) > V.topSpeeds[1] * .75 && !train.running(9)) {
     train.playIfNotPlaying(10, distanceVehicleToCamera);
    } else if (!train.running(10)) {
     train.playIfNotPlaying(9, distanceVehicleToCamera);
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
  if (V.isIntegral() && gamePlay) {
   if (chuff != null && chuffTimer <= 0 && Math.abs(V.P.speed) < 1 && (V.drive || V.reverse)) {
    chuff.play(Double.NaN, distanceVehicleToCamera);
    chuff.play(Double.NaN, distanceVehicleToCamera);
    chuffTimer = 22;
   }
   if (fly != null && V.P.mode == Physics.Mode.fly && (V.drive || V.reverse || V.turnL || V.turnR || (V.steerByMouse && (Math.abs(VE.Mouse.steerX) > U.random(2000.) || Math.abs(VE.Mouse.steerY) > U.random(2000.))))) {
    fly.playIfNotPlaying(U.random(fly.clips.size()), distanceVehicleToCamera);
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
      driveGet && !(V.P.flipped && V.P.mode.name().startsWith(Physics.Mode.drive.name())) &&
      U.contains(V.engine.name(), "prop", Vehicle.Engine.jet.name(), Vehicle.Engine.turbine.name(), Vehicle.Engine.rocket.name()) && V.P.mode != Physics.Mode.fly ?
      engineClipQuantity - 1 :
      Math.max((int) (engineClipQuantity * (Math.abs(V.P.speed) / V.topSpeeds[1])), V.floats ? 0 : 1);
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
  if (turret != null) {
   if (gamePlay && !V.destroyed && (V.turnL || V.turnR || V.drive || V.reverse || V.steerByMouse)) {
    turret.loop(distanceVehicleToCamera);
   } else {
    turret.stop();
   }
  }
  if (force != null && gamePlay) {
   forceTimer += U.random(V.P.netSpeed) * VE.tick;
   int speedCheck = V.P.netSpeed > 1000 ? 5 : V.P.netSpeed > 500 ? 4 : V.P.netSpeed > 250 ? 3 : 2;
   if (V.steerInPlace && V.P.mode == Physics.Mode.drive && (V.turnL || V.turnR) && !V.P.flipped && speedCheck < 3) {
    force.play(U.random(2), distanceVehicleToCamera);
    forceTimer = 0;
   }
   if (forceTimer > 800) {
    force.play(U.random(speedCheck), distanceVehicleToCamera);
    forceTimer = 0;
   }
  }
  chuffTimer -= chuffTimer > 0 ? VE.tick : 0;
  crashTimer -= crashTimer > 0 ? VE.tick : 0;
  landTimer -= landTimer > 0 ? VE.tick : 0;
  if (!Double.isNaN(splashing)) {
   if (splashing > 150 && Camera.Y <= 0) {
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
   } else if (!V.P.terrainProperties.contains(" hard ")) {
    skidHard.stop();
   } else {
    skidOff.stop();
   }
   skidding = false;
  }
  if (scrape != null) {
   if (scraping && V.P.netSpeed > 100) {
    int n1;
    for (n1 = scrape.clips.size(); --n1 >= 0; ) {
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
   V.exhausting -= V.exhausting > 0 ? VE.tick : 0;
   if (exhaustIndex != engineIndex) {
    if (U.random() < 1 / (double) engineClipQuantity) {
     V.exhausting = 5;
     exhaust.play(Double.NaN, distanceVehicleToCamera);
    }
    exhaustIndex = engineIndex;
   }
  }
  if (VE.Match.muteSound || !V.destroyed || !gamePlay) {
   burn.stop();
   if (V.explosionType == Vehicle.ExplosionType.maxnuclear) {//<-DO check this, as stopping may cut out blast sound on Tactical Nuke
    nuke.stop(1);
   }
  }
  if (VE.Match.muteSound || !V.isIntegral() || !gamePlay) {
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
   if (train != null) {
    train.stop(9);
    train.stop(10);
   }
   if (turret != null) {
    turret.stop();
   }
   if (!V.isFixed()) {
    engine.stop();
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
   if (V.P.wheelDiscord) {
    engine.randomizeFramePosition(n);
   }
   if (V.engine == Vehicle.Engine.turbine) {
    double thrustGain = Math.max(0, StrictMath.pow(1 / (Math.abs(V.P.netSpeed) / V.topSpeeds[1]), 4));
    turbineThrust.loop(distanceVehicleToCamera + (n >= engineClipQuantity - 1 ? 0 : thrustGain));
   }
  } else if (V.engine == Vehicle.Engine.turbine) {
   turbineThrust.stop();
  }
 }

 public static void runEnergyBolt(Special special, boolean gamePlay) {
  if (!gamePlay || special.V.destroyed) {
   special.sound.stop();
  } else {
   special.sound.loop(Math.min(special.V.VA.distanceVehicleToCamera, VE.vehicles.get(special.EB.target).VA.distanceVehicleToCamera));
  }
 }
}
