package ve.vehicles;

import ve.Sound;
import ve.environment.E;
import ve.utilities.U;

import java.io.File;
import java.util.Objects;

class VehicleAudio {

 private final Vehicle V;
 double vehicleToCameraSoundDistance;
 int randomCrashSound;
 private int randomSkidSound;
 int randomScrapeSound;
 int randomExhaustSound;
 double landTimer;
 Sound fix, land, boost, gate,
 turret, burn, grind, airEngage, splash, splashOverSurface, tsunamiSplash, mineExplosion,
 crashSoft, crashHard, crashDestroy,
 hitShot, hitRicochet, hitExplosive, explode, force, scrape, skid, exhaust, fly, train, massiveHit, spinner, nuke, chuff, backUp,
 engine, turbineThrust;

 VehicleAudio(Vehicle vehicle) {
  V = vehicle;
 }

 public void close() {//<-Keep void up here so we can make sure everything's being closed at a glance
  for (Special special : V.specials) {
   if (special.sound != null) {
    special.sound.close();
   }
  }
  if (airEngage != null) airEngage.close();
  if (burn != null) burn.close();
  if (land != null) land.close();
  if (fix != null) fix.close();
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
  if (skid != null) skid.close();
  if (train != null) train.close();
  if (spinner != null) spinner.close();
  if (massiveHit != null) massiveHit.close();
  if (backUp != null) backUp.close();
  if (nuke != null) nuke.close();
 }

 void skid() {
  if (V.contact != Vehicle.Contact.metal) {
   int n;
   if (V.terrainProperties.contains(" hard ")) {
    for (n = 5; --n >= 0; ) {
     if (skid.running(n)) {
      n = -2;
      break;
     }
    }
    if (n > -2) {
     randomSkidSound = U.randomize(randomSkidSound, 5);
     skid.resume(randomSkidSound, vehicleToCameraSoundDistance);
    }
   } else {
    for (n = 10; --n >= 5; ) {
     if (skid.running(n)) {
      n = -2;
      break;
     }
    }
    if (n > -2) {
     randomSkidSound = U.randomize(randomSkidSound, 5);
     skid.resume(randomSkidSound + 5, vehicleToCameraSoundDistance);
    }
   }
   V.skidding = true;
  }
 }

 void land() {
  if (V.damage <= V.durability && landTimer <= 0 && V.vehicleType != Vehicle.Type.turret) {
   if (V.landType == Vehicle.Landing.crash) {
    crashHard.play(U.random(crashHard.clips.size()), vehicleToCameraSoundDistance);
   } else if ((Math.abs(V.YZ) < 30 && Math.abs(V.XY) < 30) || (Math.abs(V.YZ) > 150 && Math.abs(V.XY) > 150)) {
    land.play(U.random(land.clips.size()), vehicleToCameraSoundDistance);
   } else {
    crashSoft.play(U.random(crashSoft.clips.size()), vehicleToCameraSoundDistance);
   }
   landTimer = 5;
  }
 }

 void load(Vehicle V) {//Sounds are loaded by order of importance, as not all of them may load on Linux systems. It's not as elegant but should be done.
  boolean isTurret = V.vehicleType == Vehicle.Type.turret;
  if (!isTurret) {
   if (V.engineClipQuantity < 1) {
    File[] engines = new File(U.soundFolder).listFiles((D, name) -> name.startsWith(V.engine.name() + "-") && name.endsWith(U.soundExtension));
    V.engineClipQuantity = Objects.requireNonNull(engines).length;
    engine = new Sound(V.engine.name() + "-", V.engineClipQuantity, V.enginePitchBase);
   } else {
    engine = new Sound();
    for (int n = 0; n < V.engineClipQuantity; n++) {
     engine.addClip(V.engine.name(), V.enginePitchBase);
     if (V.engineTuning == Vehicle.EngineTuning.harmonicSeries) {
      V.enginePitchBase += V.engineTuneRatio;
     } else {
      V.enginePitchBase *= StrictMath.pow(V.engineTuneRatio, 1 / (double) (V.engineClipQuantity - 1));
     }
    }
   }
   if (V.contact == Vehicle.Contact.rubber) {
    skid = new Sound("skid", 10);
   }
   scrape = new Sound("scrape", Double.POSITIVE_INFINITY);
   force = new Sound("force", 5);
  }
  if (isTurret || V.hasTurret) {
   turret = new Sound("turret");
  }
  explode = new Sound("explode", V.explosionsWhenDestroyed > 0 && !V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name()) ? 2 : 1);
  crashHard = new Sound("crashHard", Double.POSITIVE_INFINITY);
  crashDestroy = new Sound("crashDestroy", Double.POSITIVE_INFINITY);
  crashSoft = new Sound("crashSoft", Double.POSITIVE_INFINITY);
  if (V.vehicleType == Vehicle.Type.aircraft && !V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name()) && !V.floats) {
   fly = new Sound("fly", Double.POSITIVE_INFINITY);
  }
  boost = V.speedBoost > 0 && V.engine != Vehicle.Engine.turbine ? new Sound("boost") : null;
  land = !isTurret && V.landType != Vehicle.Landing.crash ? new Sound(V.landType.name(), Double.POSITIVE_INFINITY) : null;
  grind = V.engine.name().contains("truck") || V.engine == Vehicle.Engine.tank || V.engine == Vehicle.Engine.massive ? new Sound("grind") : null;
  fix = new Sound("fix");
  burn = new Sound("burn");
  exhaust = Double.isNaN(V.exhaust) ? null : new Sound("exhaust", Double.POSITIVE_INFINITY);
  tsunamiSplash = E.tsunamiParts.isEmpty() ? null : new Sound("tsunamiSplash");
  if (!isTurret) {
   if (E.poolExists) {
    splash = new Sound("splash");
    splashOverSurface = new Sound("splashOver");
    V.splashing = 0;
   }
   gate = new Sound("gateSpeed");
   gate.addClip("gateSlow", 1);
  }
  boolean loadHitExplosive = false, loadMineExplosion = false;
  for (Special special : V.specials) {
   loadHitExplosive = special.type.name().contains(Special.Type.shell.name()) || special.type == Special.Type.missile || special.type == Special.Type.bomb || special.type == Special.Type.mine || V.explosionType.name().startsWith(Vehicle.ExplosionType.nuclear.name()) || loadHitExplosive;
   loadMineExplosion = special.type == Special.Type.mine || loadMineExplosion;
  }
  hitExplosive = loadHitExplosive ? new Sound("hitExplosive", Double.POSITIVE_INFINITY) : null;
  mineExplosion = loadMineExplosion ? new Sound("mineExplode") : null;
  boolean hasSpinner = !Double.isNaN(V.spinnerSpeed);
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
  if (V.vehicleType != Vehicle.Type.aircraft && (hasSpinner || (V.damageDealt[0] >= 100 || V.damageDealt[1] >= 100 || V.damageDealt[2] >= 100 || V.damageDealt[3] >= 100))) {
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
  airEngage = isTurret ? null : new Sound("aA");
 }

 void stop(Vehicle vehicle) {
  for (Special special : vehicle.specials) {
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
  if (vehicle.vehicleType != Vehicle.Type.turret) {
   engine.stop();
  }
 }
}
