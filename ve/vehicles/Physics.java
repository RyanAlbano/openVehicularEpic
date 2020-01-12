package ve.vehicles;

import javafx.scene.paint.Color;
import ve.environment.*;
import ve.instances.Core;
import ve.instances.CoreAdvanced;
import ve.instances.I;
import ve.trackElements.Bonus;
import ve.trackElements.TE;
import ve.trackElements.trackParts.TrackPart;
import ve.trackElements.trackParts.TrackPlane;
import ve.ui.*;
import ve.utilities.*;
import ve.vehicles.explosions.Explosion;
import ve.vehicles.specials.Shot;
import ve.vehicles.specials.Special;

public class Physics {
 private final Vehicle V;
 public double speed, speedXZ, speedYZ, stallSpeed;//<-'speedX/Y/Z' is not defined here anymore since it already exists in Vehicle
 public double netSpeed;
 final double minimumFlightSpeedWithoutStall;
 private double driftXZ;
 public double cameraXZ;
 private double airSpinXZ;
 final double[] wheelSpin = new double[2];
 private double hitOtherX, hitOtherZ;
 double destructTimer;
 double massiveHitTimer;
 public double localGround, clearance;
 public double stuntTimer;
 public double stuntXY, stuntYZ, stuntXZ;
 double stuntSpeedYZ, stuntSpeedXY, stuntSpeedXZ;
 public double flipTimer, stuntReward;
 private boolean onAntiGravity;
 public boolean inTornado;
 boolean onFlatTrackPlane;
 boolean onMoundSlope;
 boolean atOrAboveAndWithinMoundTopRadius;
 public boolean onVolcano;
 boolean atPoolXZ, inPool;
 private boolean inWall;
 double wheelGapFrontToBack, wheelGapLeftToRight;
 public boolean wrathEngaged;
 public boolean[] wrathStuck;
 boolean inWrath;
 public long vehicleHit = -1;
 public double explodeStage;
 public boolean subtractExplodeStage;
 public String terrainProperties = "";
 public double explosionDiameter;
 public double explosionDamage;
 public double explosionPush;
 public long polarity;
 static final double angleToSteerVelocity = .2, airAngleToSteerVelocity = angleToSteerVelocity * 1.25;//<-.25
 public static final double valueAdjustSmoothing = 1;//<-Not currently utilized
 public Mode mode = Mode.driveSolid;

 public enum Mode {driveSolid, drivePool, neutral, stunt, fly}

 enum Contact {none, rubber, metal}

 enum Landing {tires, touch, crash}

 private enum RichHit {
  ;
  private static final double minimumSpeed = 150;
  static final long dustQuantity = 8;
 }

 Physics(Vehicle vehicle) {
  V = vehicle;
  minimumFlightSpeedWithoutStall = V.floats ? 0 : E.gravity * (V.engine == Vehicle.Engine.smallprop ? .25 : .5) * 100;
 }

 void resetLocalGround() {
  localGround = Ground.level + (atPoolXZ ? Pool.depth : 0);
 }

 private boolean angledSurface() {
  return V.wheels.get(0).angledSurface || V.wheels.get(1).angledSurface || V.wheels.get(2).angledSurface || V.wheels.get(3).angledSurface;
 }

 boolean againstWall() {
  return !V.isFixed() && (V.wheels.get(0).againstWall || V.wheels.get(1).againstWall || V.wheels.get(2).againstWall || V.wheels.get(3).againstWall);
 }

 public boolean flipped() {
  double absXY = Math.abs(V.XY), absYZ = Math.abs(V.YZ);
  return (absXY > 90 && absYZ <= 90) || (absYZ > 90 && absXY <= 90);
 }

 double getNetSpeed() {
  return U.netValue(V.speedX, V.speedY, V.speedZ);
 }

 private void crash(double power, boolean vehicleCollide) {
  power = Math.abs(power);
  double fragilityBased = power * V.fragility;
  if (fragilityBased > (vehicleCollide ? 0 : 50)) {
   V.addDamage(fragilityBased * 3 * U.tick);
   V.deformParts();
   for (VehiclePart part : V.parts) {
    part.throwChip(U.randomPlusMinus(power));
   }
   if (V.isIntegral() && V.VA.crashTimer <= 0) {
    (power > RichHit.minimumSpeed ? V.VA.crashHard : V.VA.crashSoft).play(Double.NaN, V.VA.distanceVehicleToCamera);
    V.VA.crashTimer = 2;
   }
  }
  if (V.bounce > .9 && V.isIntegral() && power > RichHit.minimumSpeed) {
   V.VA.land.play(Double.NaN, V.VA.distanceVehicleToCamera);
  }
 }

 public void runCollisions() {
  boolean replay = UI.status == UI.Status.replay, greenTeam = V.index < I.vehiclesInMatch >> 1;
  if (!V.phantomEngaged) {
   if (!V.destroyed && !V.reviveImmortality) {
    for (Vehicle otherV : I.vehicles) {
     if (!U.sameTeam(V, otherV) && !otherV.destroyed && !otherV.reviveImmortality && !otherV.phantomEngaged) {
      if (advancedCollisionCheck(V, otherV, V.collisionRadius + otherV.collisionRadius)) {//<-There's no reason for 'collideAt' to be any other value. Change the default value if needed
       if (V.getsLifted > 0 && V.Y < otherV.Y) {
        V.speedY -= E.gravity * 1.5 * U.tick;
       }
       double yourDamage = Math.abs(netSpeed - otherV.P.netSpeed) * otherV.damageDealt * otherV.energyMultiple;//<-Damage now RECEIVING from other vehicles--not vice versa
       //Don't multiply 'yourDamage' by a constant at initialization or it'll skew scores!
       if (V.isIntegral()) {
        Match.scoreDamage[greenTeam ? 1 : 0] += replay ? 0 : yourDamage;
       }
       hitCheck(otherV);
       double theirPushX = 0, theirPushZ = 0, yourPushX = 0, yourPushZ = 0;
       if (otherV.isIntegral() && V.getsPushed >= otherV.getsPushed) {
        theirPushX = V.getsPushed * (otherV.speedX - V.speedX);
        theirPushZ = V.getsPushed * (otherV.speedZ - V.speedZ);
       }
       if (V.isIntegral() && V.getsPushed <= otherV.getsPushed) {
        yourPushX = V.pushesOthers * (V.speedX - otherV.speedX);
        yourPushZ = V.pushesOthers * (V.speedZ - otherV.speedZ);
       }
       if (V.getsPushed >= otherV.getsPushed) {
        if (
        (V.X > otherV.X && V.speedX < otherV.speedX) ||
        (V.X < otherV.X && V.speedX > otherV.speedX)) {
         hitOtherX = otherV.speedX;
        }
        if (
        (V.Z > otherV.Z && V.speedZ < otherV.speedZ) ||
        (V.Z < otherV.Z && V.speedZ > otherV.speedZ)) {
         hitOtherZ = otherV.speedZ;
        }
       }
       if (V.Y != otherV.Y) {
        double lift = Math.abs(U.netValue(V.speedX, V.speedZ) - U.netValue(otherV.speedX, otherV.speedZ));
        if (otherV.getsLifted > 0) {
         otherV.speedY -= V.liftsOthers * (otherV.Y < V.Y ? 1 : -1) * lift;
        }
        if (V.getsLifted > 0) {
         V.speedY -= V.getsLifted * (V.Y < otherV.Y ? 1 : -1) * lift;
        }
       }//^Lifting before pushing is probably SAFER if X and Z speeds go wild
       otherV.speedX += yourPushX;
       otherV.speedZ += yourPushZ;
       V.speedX -= theirPushX;
       V.speedZ -= theirPushZ;
       crash(yourDamage, true);
       if (V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name())) {
        V.setDamage(V.damageCeiling());
        otherV.setDamage(otherV.damageCeiling());
        Match.scoreDamage[greenTeam ? 0 : 1] += replay ? 0 : otherV.durability;
        V.setCameraShake(Camera.shakePresets.normalNuclear);
       }
       if (V.dealsMassiveDamage() && (massiveHitTimer <= 0 || otherV.isIntegral())) {
        otherV.setDamage(otherV.damageCeiling());
        Match.scoreDamage[greenTeam ? 0 : 1] += replay ? 0 : otherV.durability;
        V.VA.massiveHit.play(Double.NaN, V.VA.distanceVehicleToCamera);
        massiveHitTimer = U.random(5.);
        otherV.deformParts();
        for (VehiclePart part : otherV.parts) {
         part.throwChip(netSpeed - otherV.P.getNetSpeed());//<-Get it again because netSpeed field won't be updated with velocities from a KILL-O-MATIC slam
        }
        V.setCameraShake(Camera.shakePresets.massiveHit);
       }
      }
      if (V.spinner != null && U.distance(V, otherV) < V.renderRadius + otherV.collisionRadius) {//<-'renderRadius' ideal if spinner has largest diameter of the vehicle
       V.spinner.hit(otherV);
      }
     }
    }
   }
   for (Special special : V.specials) {
    for (Vehicle vehicle : I.vehicles) {
     if (!U.sameTeam(V, vehicle) && (!vehicle.destroyed || wrathEngaged) && !vehicle.reviveImmortality && !vehicle.phantomEngaged) {
      double diameter = special.type == Special.Type.mine ? vehicle.P.netSpeed : special.diameter;
      for (Shot shot : special.shots) {
       if (shot.stage > 0 && shot.hit < 1 && (shot.doneDamaging == null || !shot.doneDamaging[vehicle.index]) && (special.type != Special.Type.missile || vehicle.isIntegral()) && !(special.type == Special.Type.mine && (U.distance(shot, vehicle) > 2000 || !vehicle.isIntegral()))) {
        if (shot.advancedCollisionCheck(vehicle, diameter + vehicle.collisionRadius)) {
         hitCheck(vehicle);
         double shotDamage = special.damageDealt;
         if (special.type == Special.Type.raygun || special.type == Special.Type.flamethrower || special.type == Special.Type.thewrath || special.type.name().contains(Special.Type.blaster.name())) {
          shotDamage /= special.type == Special.Type.flamethrower ? Math.max(1, shot.stage) : 1;
          shotDamage *= U.tick;
         } else if (special.type != Special.Type.forcefield) {
          shot.hit = 1;
         }
         vehicle.addDamage(shotDamage);
         if (vehicle.isIntegral() && !replay) {
          Match.scoreDamage[greenTeam ? 0 : 1] += shotDamage;
          if (vehicle.index != I.userPlayerIndex && U.distance(vehicle, V) < U.distance(vehicle, I.vehicles.get(vehicle.AI.target))) {
           vehicle.AI.target = V.index;
          }
         }
         if (special.pushPower > 0) {
          if (vehicle.getsPushed >= 0) {
           vehicle.speedX += U.randomPlusMinus(special.pushPower);
           vehicle.speedZ += U.randomPlusMinus(special.pushPower);
          }
          if (vehicle.getsLifted >= 0 && (special.type == Special.Type.forcefield || special.type == Special.Type.missile || special.type == Special.Type.mine || U.contains(special.type.name(), Special.Type.shell.name()))) {
           vehicle.speedY += U.randomPlusMinus(special.pushPower);
          }
         }
         vehicle.deformParts();
         for (VehiclePart part : vehicle.parts) {
          part.throwChip(U.randomPlusMinus(shot.speed));
         }
         double shotToCameraSoundDistance = Math.sqrt(U.distance(shot)) * Sound.standardDistance(1);
         if (special.useSmallHits) {
          V.VA.hitShot.play(Double.NaN, shotToCameraSoundDistance);
         }
         if (special.type == Special.Type.heavymachinegun || special.type == Special.Type.blaster) {
          V.VA.hitShot.play(U.random(7), shotToCameraSoundDistance);
         } else if (special.type == Special.Type.heavyblaster || special.type == Special.Type.thewrath) {//<-These specials don't load hitExplosive audio, so don't call!
          V.VA.crashDestroy.play(Double.NaN, shotToCameraSoundDistance);
         } else if (special.type.name().contains(Special.Type.shell.name()) || special.type == Special.Type.missile || special.type == Special.Type.bomb) {
          V.VA.crashDestroy.play(Double.NaN, shotToCameraSoundDistance);
          V.VA.hitExplosive.play(Double.NaN, shotToCameraSoundDistance);
         } else if (special.type == Special.Type.railgun) {
          for (int n = 4; --n >= 0; ) {
           V.VA.crashHard.play(Double.NaN, shotToCameraSoundDistance);
          }
         } else if (special.type == Special.Type.forcefield) {
          V.VA.crashHard.play(Double.NaN, V.VA.distanceVehicleToCamera);
          V.VA.crashHard.play(Double.NaN, V.VA.distanceVehicleToCamera);
          V.VA.crashHard.play(Double.NaN, V.VA.distanceVehicleToCamera);
         } else if (special.type == Special.Type.mine) {
          V.VA.mineExplosion.play(shotToCameraSoundDistance);
         }
         if (U.random() < .25 && special.ricochets) {
          V.VA.hitRicochet.play(Double.NaN, shotToCameraSoundDistance);
         }
         if (shot.doneDamaging != null) {
          shot.doneDamaging[vehicle.index] = true;
         }
        }
       }
      }
      if (V.isFixed() && Bonus.holder < 0 && V.isIntegral()) {
       double collideAt = special.diameter + Bonus.big.getRadius();
       for (Shot shot : special.shots) {
        if (shot.stage > 0 && shot.advancedCollisionCheck(TE.bonus, collideAt)) {
         Bonus.setHolder(V);
        }
       }
      }
      if (wrathEngaged && (U.distance(V, vehicle) < V.absoluteRadius + netSpeed || wrathStuck[vehicle.index])) {
       if (vehicle.getsPushed >= 0) {
        vehicle.X = V.X;
        vehicle.Y = V.Y;
        vehicle.Z = V.Z;
        vehicle.speedX = V.speedX;
        vehicle.speedY = V.speedY;
        vehicle.speedZ = V.speedZ;
        for (VehiclePart part : vehicle.parts) {
         part.X = V.X + U.randomPlusMinus(V.absoluteRadius);
         part.Y = V.Y + U.randomPlusMinus(V.absoluteRadius);
         part.Z = V.Z + U.randomPlusMinus(V.absoluteRadius);
        }
        vehicle.P.inWrath = wrathStuck[vehicle.index] = true;
       }
       vehicle.setDamage(vehicle.damageCeiling());
      }
     }
    }
   }
   if (V.explosionType != Vehicle.ExplosionType.none) {
    if (V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name())) {
     explosionDiameter = U.random(20000.);
     explosionDamage = 2500 + U.random(5000.);
    }
    for (Vehicle vehicle : I.vehicles) {
     if (!U.sameTeam(V, vehicle) && !vehicle.destroyed && !vehicle.reviveImmortality && !vehicle.phantomEngaged) {
      for (Explosion explosion : V.explosions) {
       explosion.vehicleInteract(vehicle, replay, greenTeam);
      }
     }
    }
   }
   if (!V.destroyed) {
    if (V.isFixed()) {
     for (Vehicle vehicle : I.vehicles) {
      if (!U.sameTeam(V, vehicle) && !vehicle.destroyed && !vehicle.reviveImmortality && U.distance(V.X, vehicle.X, V.Y + (V.turretBaseY * .5), vehicle.Y, V.Z, vehicle.Z) < V.collisionRadius * .5 + vehicle.collisionRadius && !vehicle.phantomEngaged) {
       hitCheck(vehicle);
       if (vehicle.fragility > 0) {
        vehicle.addDamage(V.structureBaseDamageDealt * vehicle.fragility);
        Match.scoreDamage[greenTeam ? 0 : 1] += replay ? 0 : V.structureBaseDamageDealt * vehicle.fragility;
       }
       vehicle.speedX += U.randomPlusMinus(500.);
       vehicle.speedZ += U.randomPlusMinus(500.);
       vehicle.speedY += U.randomPlusMinus(200.);
       if (vehicle.fragility > 0) {
        vehicle.deformParts();
       }
       for (VehiclePart part : vehicle.parts) {
        part.throwChip(U.randomPlusMinus(vehicle.P.netSpeed));
       }
       V.VA.crashHard.play(U.random(4), V.VA.distanceVehicleToCamera);
       V.VA.crashHard.play(U.random(4), V.VA.distanceVehicleToCamera);
       V.VA.crashHard.play(U.random(4), V.VA.distanceVehicleToCamera);
      }
     }
    }
    if (!V.reviveImmortality) {
     for (Special special : V.specials) {
      if (special.type.name().contains(SL.particle) && special.fire) {
       boolean disintegrate = special.type == Special.Type.particledisintegrator;
       for (Vehicle vehicle : I.vehicles) {
        if ((disintegrate ? !U.sameTeam(V, vehicle) : !U.sameVehicle(V, vehicle) && U.sameTeam(V, vehicle)) &&
        !vehicle.destroyed && !vehicle.reviveImmortality && !vehicle.phantomEngaged) {
         if (
         ((vehicle.Y <= V.Y && U.sin(V.YZ) >= 0) || (vehicle.Y >= V.Y && U.sin(V.YZ) <= 0) || Math.abs(vehicle.Y - V.Y) < vehicle.collisionRadius) &&//<-inY
         ((vehicle.X <= V.X && U.sin(V.XZ) >= 0) || (vehicle.X >= V.X && U.sin(V.XZ) <= 0)) &&//<-inX
         ((vehicle.Z <= V.Z && U.cos(V.XZ) <= 0) || (vehicle.Z >= V.Z && U.cos(V.XZ) >= 0)))/*<-inZ*/ {
          double amount = 10 * V.energyMultiple * U.tick;
          if (disintegrate) {
           vehicle.deformParts();
           hitCheck(vehicle);
           Match.scoreDamage[greenTeam ? 0 : 1] += replay ? 0 : amount;
          }
          if (disintegrate || vehicle.isIntegral()) {//<-Reintegration was sometimes making vehicles non-destroyable
           vehicle.addDamage(amount * (disintegrate ? 1 : -1.5));//<-1.5 for reintegration to make a more worthwhile team member
          }
         }
        }
       }
      }
     }
     if (Storm.Lightning.exists && Storm.Lightning.strikeStage < 1) {
      double distance = U.distance(V.X, Storm.Lightning.X, V.Z, Storm.Lightning.Z);
      if (V.Y >= Storm.stormCloudY && distance < V.collisionRadius * 6) {
       V.addDamage(V.durability * .5 + (distance < V.collisionRadius * 2 ? V.durability : 0));
       V.deformParts();
       for (VehiclePart part : V.parts) {
        part.throwChip(U.randomPlusMinus(500.));
       }
       V.VA.crashHard.play(Double.NaN, V.VA.distanceVehicleToCamera);
       V.VA.crashHard.play(Double.NaN, V.VA.distanceVehicleToCamera);
       V.VA.crashHard.play(Double.NaN, V.VA.distanceVehicleToCamera);
      }
     }
     for (Fire.Instance fire : Fire.instances) {
      double distance = U.distance(V, fire);
      if (distance < V.collisionRadius + fire.absoluteRadius) {
       V.addDamage(10 * U.tick);
       if (distance * 2 < V.collisionRadius + fire.absoluteRadius) {
        V.addDamage(10 * U.tick);
       }
       V.deformParts();
      }
     }
     for (Boulder.Instance boulder : Boulder.instances) {
      if (U.distanceXZ(V, boulder) < V.collisionRadius + boulder.S.getRadius() && V.Y > boulder.Y - V.collisionRadius - boulder.S.getRadius()) {//<-Will call incorrectly in the unlikely event a vehicle is underground and the boulder rolls directly overhead
       V.setDamage(V.damageCeiling());
       V.deformParts();
       for (VehiclePart part : V.parts) {
        part.throwChip(U.randomPlusMinus(boulder.speed));
       }
       V.VA.crashDestroy.play(Double.NaN, V.VA.distanceVehicleToCamera);
      }
     }
     Volcano.rockVehicleInteract(V);
     Meteor.vehicleInteract(V);
    }
   }
  }
  if (V.isIntegral()) {
   V.death = Vehicle.Death.none;
  } else {
   if (V.MNB != null) {
    V.MNB.runHitOthers(greenTeam);
   }
   if (V.death == Vehicle.Death.none) {
    String s = Network.mode == Network.Mode.OFF ? V.name : UI.playerNames[V.index];
    V.death = Vehicle.Death.diedAlone;
    for (Vehicle vehicle : I.vehicles) {
     if (!U.sameTeam(V, vehicle) && vehicleHit == vehicle.index && vehicle.P.vehicleHit == V.index) {
      V.death = Vehicle.Death.killedByAnother;
      DestructionLog.update();
      String s1 = Network.mode == Network.Mode.OFF ? vehicle.name : UI.playerNames[vehicle.index];
      DestructionLog.names[4][0] = s1;
      DestructionLog.names[4][1] = s;
      DestructionLog.nameColors[4][0] = vehicle.index < I.vehiclesInMatch >> 1 ? Color.color(0, 1, 0) : Color.color(1, 0, 0);
      DestructionLog.nameColors[4][1] = greenTeam ? Color.color(0, 1, 0) : Color.color(1, 0, 0);
     }
    }
    if (V.death == Vehicle.Death.diedAlone) {
     DestructionLog.update();
     DestructionLog.names[4][0] = s;
     DestructionLog.names[4][1] = "";
     DestructionLog.nameColors[4][0] = greenTeam ? Color.color(0, 1, 0) : Color.color(1, 0, 0);
    }
   }
  }
 }

 static boolean advancedCollisionCheck(CoreAdvanced moving, Core stationary, double collideAt) {//<-Stationary core may not always be stationary in actuality, but good enough
  if (U.distance(moving, stationary) < collideAt) {
   return true;
  }
  double
  behindX = moving.X - (moving.speedX * U.tick),
  behindY = moving.Y - (moving.speedY * U.tick),
  behindZ = moving.Z - (moving.speedZ * U.tick),
  averageX = (moving.X + behindX) * .5,
  averageY = (moving.Y + behindY) * .5,
  averageZ = (moving.Z + behindZ) * .5;
  return
  ((U.distance(averageY, stationary.Y, averageZ, stationary.Z) < collideAt || U.distance(behindY, stationary.Y, behindZ, stationary.Z) < collideAt) && ((moving.X > stationary.X && behindX < stationary.X) || (moving.X < stationary.X && behindX > stationary.X))) ||//<-inBoundsX
  ((U.distance(averageX, stationary.X, averageY, stationary.Y) < collideAt || U.distance(behindX, stationary.X, behindY, stationary.Y) < collideAt) && ((moving.Z > stationary.Z && behindZ < stationary.Z) || (moving.Z < stationary.Z && behindZ > stationary.Z))) ||//<-inBoundsZ
  ((U.distance(averageX, stationary.X, averageZ, stationary.Z) < collideAt || U.distance(behindX, stationary.X, behindZ, stationary.Z) < collideAt) && ((moving.Y > stationary.Y && behindY < stationary.Y) || (moving.Y < stationary.Y && behindY > stationary.Y)));  //<-inBoundsY
 }

 public void hitCheck(Vehicle vehicle) {
  vehicleHit = V.isIntegral() ? vehicle.index : vehicleHit;
  vehicle.P.vehicleHit = vehicle.isIntegral() ? V.index : vehicle.P.vehicleHit;
 }

 public void run() {
  int n;
  if (V.isIntegral()) {
   vehicleHit = -1;
  }
  atPoolXZ = Pool.exists && U.distanceXZ(V, E.pool) < Pool.C[0].getRadius();
  runVehiclesAircraft();
  runTurretsInfrastructure();
  if (V.explosionsWhenDestroyed > 0 && !V.isIntegral() && !V.destroyed) {
   for (n = (int) V.explosionsWhenDestroyed; --n >= 0; ) {
    V.explosions.get(V.currentExplosion).deploy(U.randomPlusMinus(V.absoluteRadius), U.randomPlusMinus(V.absoluteRadius), U.randomPlusMinus(V.absoluteRadius), V);
    V.currentExplosion = ++V.currentExplosion >= Explosion.defaultQuantity ? 0 : V.currentExplosion;
   }
   V.setCameraShake(Camera.shakePresets.vehicleExplode);
   if (V.isFixed()) {//<-For Missile Turret, basically
    V.VA.crashDestroy.play(Double.NaN, V.VA.distanceVehicleToCamera);
   }
  }
  V.X = U.clamp(MapBounds.left, V.X, MapBounds.right);
  V.Z = U.clamp(MapBounds.backward, V.Z, MapBounds.forward);
  V.Y = U.clamp(MapBounds.Y, V.Y, -MapBounds.Y);
 }

 private void runVehiclesAircraft() {
  if (!V.isFixed()) {
   boolean replay = UI.status == UI.Status.replay;
   netSpeed = getNetSpeed();
   polarity = Math.abs(V.YZ) > 90 ? -1 : 1;
   clearance = flipped() && !V.landStuntsBothSides ? -V.absoluteRadius * .075 : V.clearanceY;
   inPool = atPoolXZ && V.Y + clearance > 0;
   boolean onBouncy = U.contains(terrainProperties, SL.thick(SL.bounce), " maxbounce ");
   if (mode.name().startsWith(SL.drive)) {
    stuntSpeedYZ = stuntSpeedXY = stuntSpeedXZ = 0;
   }
   if (!V.destroyed) {
    runAirEngage();
    if (mode == Mode.stunt) {
     runAerialControl();
    } else {
     runDriveAndBrake();
    }
   }
   runWheelSpin();
   double maxTurn = V.maxTurn + U.random(V.randomTurnKick);
   if (!replay) {
    runSteering(maxTurn);
    if (V.VT != null) {
     V.VT.runSteering(maxTurn);
    }
   }
   double turnSpeed = V.steerInPlace ? 1 : Math.min(1, Math.max(netSpeed, Math.abs(speed)) * .025);
   if (speed < 0) {
    turnSpeed *= -1;
   }
   if (mode.name().startsWith(SL.drive) || V.phantomEngaged) {
    if (!flipped()) {
     airSpinXZ = speedXZ * turnSpeed * angleToSteerVelocity * (V.handbrake ? 1 : .5);//<-Reduction needed for non-handbrake air jumps, or airborne vehicles like Over=drivn will become difficult to control!
     V.XZ += speedXZ * turnSpeed * angleToSteerVelocity * U.tick;
    }
   } else if (mode != Mode.fly) {//airSpinXZ gets zeroed in runAerialControl() if aerial control is enhanced
    V.XZ += airSpinXZ * U.tick;
    stuntXZ += airSpinXZ * U.tick;
   }
   runFlight(maxTurn, turnSpeed);
   if (!V.phantomEngaged && !inTornado && !V.floats) {
    V.speedY += E.gravity * (V.amphibious == Vehicle.Amphibious.ON && inPool && V.Y > 0 ? -1 : 1) * U.tick;
   }//There IS better ground traction if the gravity is applied before setting wheel XYZ
   runSetWheelXYZ();
   runSetCorePosition(onBouncy);
   //*Must come AFTER position-setting or wall hits are ineffective!
   runDriveVelocity();//*
   runSpeedBoost();//*
   if (mode.name().startsWith(SL.drive) || V.phantomEngaged) {
    runBouncyTerrain(onBouncy);
    mode = Mode.neutral;
   }
   driftXZ = V.XZ;//<-Best spot for assigning this
   boolean crashLand = (Math.abs(V.YZ) > 30 || Math.abs(V.XY) > 30) && !(Math.abs(V.YZ) > 150 && Math.abs(V.XY) > 150);
   double gravityCompensation = E.gravity * 2 * U.tick;
   for (Wheel wheel : V.wheels) {//<-Best place for this?
    wheel.againstWall = wheel.angledSurface = false;
    wheel.minimumSkidmarkY = localGround;
   }
   if (!V.phantomEngaged) {
    double bounceBackForce = flipped() ? 1 : Math.abs(U.sin(V.XY)) + Math.abs(U.sin(V.YZ)),
    flatPlaneBounce = Math.min(Math.abs(U.sin(V.XY)) + Math.abs(U.sin(V.YZ)), 1);
    terrainProperties = Terrain.vehicleDefaultTerrain;
    Volcano.runVehicleInteract(V);//<-Better first if trackPlanes are on volcano <-terrainProperties set here
    runSetTerrainFromTrackPlanes(gravityCompensation);//<-terrainProperties is set here
    resetLocalGround();
    runTrackPlaneInteraction(crashLand, bounceBackForce, flatPlaneBounce, gravityCompensation);//<-localGround is set here
    //*Not executed if flatTrackPlane gotten in runTrackPlaneInteraction()
    runGroundConnect(crashLand, bounceBackForce, flatPlaneBounce);//*
    runPoolInteract();//<-AFTER groundConnect for correct mode!*
    runMoundInteract(gravityCompensation);//<-localGround is set here
    runSkidsAndDust();//<-Must come AFTER 'mode' is handled by all other voids!
   }
   runAngleSet();
   Tornado.vehicleInteract(V);
   runHitOther();
   runLandInvert();
   runDriveEffects();
   TE.runVehicleInteraction(V, replay);
   V.runStuntScoring(replay);
   runSpeedConstraints();
   MapBounds.slowVehicle(V);
   if ((mode == Mode.stunt || mode == Mode.fly) && V.destroyed) {
    mode = Mode.neutral;
   }
   while (Math.abs(V.XZ - cameraXZ) > 180) {
    cameraXZ += cameraXZ < V.XZ ? 360 : -360;
   }
   cameraXZ += (V.XZ - cameraXZ) * .3 * StrictMath.pow(U.tick, .8);
  }
 }

 private void runTurretsInfrastructure() {
  if (V.isFixed()) {
   inPool = atPoolXZ && V.Y > 0;
   polarity = 1;
   double randomTurnKick = U.random(V.randomTurnKick);
   if (V.steerByMouse && V.turnRate >= Double.POSITIVE_INFINITY) {
    speedXZ = U.clamp(-V.maxTurn - randomTurnKick, Mouse.steerX, V.maxTurn + randomTurnKick);
   } else {
    if ((V.turnR && !V.turnL) || (V.steerByMouse && speedXZ > Mouse.steerX)) {
     speedXZ -= (speedXZ > 0 ? 2 : 1) * V.turnRate * U.tick;
     speedXZ = Math.max(speedXZ, -V.maxTurn);
    }
    if ((V.turnL && !V.turnR) || (V.steerByMouse && speedXZ < Mouse.steerX)) {
     speedXZ += (speedXZ < 0 ? 2 : 1) * V.turnRate * U.tick;
     speedXZ = Math.min(speedXZ, V.maxTurn);
    }
    if (speedXZ != 0 && !V.turnL && !V.turnR && !V.steerByMouse) {
     if (Math.abs(speedXZ) < V.turnRate * 2 * U.tick) {
      speedXZ = 0;
     } else {
      speedXZ += (speedXZ < 0 ? 1 : speedXZ > 0 ? -1 : 0) * V.turnRate * 2 * U.tick;
     }
    }
   }
   if (V.drive || (V.steerByMouse && speedYZ > Mouse.steerY)) {
    speedYZ -= (speedYZ > 0 ? 2 : 1) * V.turnRate * U.tick;
    speedYZ = Math.max(speedYZ, -V.maxTurn);
   }
   if (V.reverse || (V.steerByMouse && speedYZ < Mouse.steerY)) {
    speedYZ += (speedYZ < 0 ? 2 : 1) * V.turnRate * U.tick;
    speedYZ = Math.min(speedYZ, V.maxTurn);
   }
   if (speedYZ != 0 && !V.drive && !V.reverse && !V.steerByMouse) {
    if (Math.abs(speedYZ) < V.turnRate * 2 * U.tick) {
     speedYZ = 0;
    } else {
     speedYZ += (speedYZ < 0 ? 1 : speedYZ > 0 ? -1 : 0) * V.turnRate * 2 * U.tick;
    }
   }
   double sharpShoot = angleToSteerVelocity * (V.handbrake ? .1 : 1);
   V.XZ += speedXZ * sharpShoot * U.tick;
   V.YZ += speedYZ * sharpShoot * U.tick;
   V.speedX = V.speedZ = V.speedY = 0;
   localGround = V.Y + V.turretBaseY;//<-Confirmed correct
   V.YZ = U.clamp(-90, V.YZ, 90);
   V.XY = speed = 0;
  }
 }

 private void runDriveAndBrake() {
  boolean aircraft = V.type == Vehicle.Type.aircraft, flying = mode == Mode.fly,
  driveGet = !flying && aircraft ? V.drive || V.drive2 : flying ? V.drive2 : V.drive,
  reverseGet = !flying && aircraft ? V.reverse || V.reverse2 : flying ? V.reverse2 : V.reverse;
  if (reverseGet) {
   speed -= speed > 0 && V.engine != Vehicle.Engine.hotrod ? V.brake * .5 * U.tick : speed > -V.topSpeeds[0] ? V.accelerationStages[0] * U.tick : 0;
  }
  if (driveGet) {
   if (speed < 0 && V.engine != Vehicle.Engine.hotrod) {
    speed += V.brake * U.tick;
   } else {
    int u = 0;
    for (int n = 2; --n >= 0; ) {
     u += speed >= V.topSpeeds[n] ? 1 : 0;
    }
    speed += u < 2 ? V.accelerationStages[u] * U.tick : 0;
   }
  }
  if (!flying && V.handbrake && speed != 0) {
   if (speed < V.brake * U.tick && speed > -V.brake * U.tick) {
    speed = 0;
   } else {
    speed += (speed < 0 ? 1 : speed > 0 ? -1 : 0) * V.brake * U.tick;
   }
  }
 }

 private void runSteering(double turnAmount) {
  if (V.steerByMouse && V.turnRate >= Double.POSITIVE_INFINITY) {
   speedXZ = U.clamp(-turnAmount, Mouse.steerX, turnAmount);
  } else {
   if ((V.turnR && !V.turnL) || (V.steerByMouse && speedXZ > Mouse.steerX)) {
    speedXZ -= (speedXZ > 0 ? 2 : 1) * V.turnRate * U.tick;
    speedXZ = Math.max(speedXZ, -turnAmount);
   }
   if ((V.turnL && !V.turnR) || (V.steerByMouse && speedXZ < Mouse.steerX)) {
    speedXZ += (speedXZ < 0 ? 2 : 1) * V.turnRate * U.tick;
    speedXZ = Math.min(speedXZ, turnAmount);
   }
   if (speedXZ != 0 && !V.turnL && !V.turnR && !V.steerByMouse) {
    if (Math.abs(speedXZ) < V.turnRate * 2 * U.tick) {
     speedXZ = 0;
    } else {
     speedXZ += (speedXZ < 0 ? V.turnRate : speedXZ > 0 ? -V.turnRate : 0) * 2 * U.tick;
    }
   }
  }
  if (mode == Mode.fly) {
   if (V.drive || (V.steerByMouse && speedYZ > Mouse.steerY)) {
    speedYZ -= (speedYZ > 0 ? 2 : 1) * V.turnRate * U.tick;
    speedYZ = Math.max(speedYZ, -V.maxTurn);
   }
   if (V.reverse || (V.steerByMouse && speedYZ < Mouse.steerY)) {
    speedYZ += (speedYZ < 0 ? 2 : 1) * V.turnRate * U.tick;
    speedYZ = Math.min(speedYZ, V.maxTurn);
   }
  }
  if (speedYZ != 0 && (mode != Mode.fly || (!V.drive && !V.reverse && !V.steerByMouse))) {
   if (Math.abs(speedYZ) < V.turnRate * 2 * U.tick) {
    speedYZ = 0;
   } else {
    speedYZ += (speedYZ < 0 ? V.turnRate : speedYZ > 0 ? -V.turnRate : 0) * 2 * U.tick;
   }
  }
 }

 private void runDriveEffects() {
  boolean shockAbsorb = !Double.isNaN(V.shockAbsorb);
  if (shockAbsorb) {
   for (Wheel wheel : V.wheels) {
    wheel.vibrateY -= wheel.vibrateY * valueAdjustSmoothing * U.tick;
   }
  }
  if (V.isIntegral() && mode == Mode.driveSolid && V.bounce > 0) {
   if (V.type == Vehicle.Type.vehicle && !flipped()) {
    double lean = speed * V.clearanceY * V.bounce * speedXZ * .0000133 * (speed < 0 ? -1 : 1) * (Math.abs(V.XY) > 10 ? .5 : 1);
    V.XY += lean;
    if (shockAbsorb) {
     lean *= .25;
     for (Wheel wheel : V.wheels) {
      wheel.vibrateY -= wheel.pointX * U.sin(lean) * V.shockAbsorb;
     }
    }
   }
   boolean rockTerrain = terrainProperties.contains(SL.thick(SL.rock));
   if (rockTerrain || terrainProperties.contains(SL.thick(SL.ground))) {
    if (shockAbsorb) {
     double vibrate = rockTerrain ? 50 : 25;
     double[] storeVibrate = new double[4];
     for (int n = 4; --n >= 0; ) {
      storeVibrate[n] = U.randomPlusMinus(Math.min(vibrate * netSpeed * V.bounce * .002, vibrate));
      V.wheels.get(n).vibrateY += storeVibrate[n] * V.shockAbsorb;
     }
     if (V.shockAbsorb < 1) {
      double setXY = 0, setYZ = 0;
      setXY -= storeVibrate[0];
      setXY += storeVibrate[1];
      setXY -= storeVibrate[2];
      setXY += storeVibrate[3];
      setXY /= wheelGapLeftToRight;
      setXY -= setXY * V.shockAbsorb;
      setYZ -= storeVibrate[0];
      setYZ -= storeVibrate[1];
      setYZ += storeVibrate[2];
      setYZ += storeVibrate[3];
      setYZ /= wheelGapFrontToBack;
      setYZ -= setYZ * V.shockAbsorb;
      V.YZ -= setYZ * V.clearanceY * .5;
      V.XY -= setXY * V.clearanceY * .5;
     }
    } else {
     double vibrate = terrainProperties.contains(SL.thick(SL.rock)) ? .0003 : .00015;
     V.YZ += U.clamp(-180 - U.random(180.), U.randomPlusMinus(vibrate) * netSpeed * V.clearanceY * V.bounce, 180 + U.random(180.));
     V.XY += U.clamp(-180 - U.random(180.), U.randomPlusMinus(vibrate) * netSpeed * V.clearanceY * V.bounce, 180 + U.random(180.));
    }
   }
  }
  if (V.engine == Vehicle.Engine.hotrod && !V.destroyed && U.random() < .5) {
   V.XY += U.random() < .5 ? 1 : -1;
  }
 }

 private void runGroundConnect(boolean crashLand, double bounceBackForce, double flatPlaneBounce) {
  if (!onFlatTrackPlane && !onMoundSlope) {
   boolean connected = false,
   significantDown = V.speedY > -E.gravity * U.tick;//<-Should prevent vehicles from 'hugging' mounds instead of taking off fully
   boolean flipped = flipped();
   for (Wheel wheel : V.wheels) {
    if (wheel.beneathLocalGround) {
     connected = true;
     if (significantDown) {
      wheel.XY -= wheel.XY * valueAdjustSmoothing * U.tick;
      wheel.YZ -= wheel.YZ * valueAdjustSmoothing * U.tick;
     }
     if (flipped && terrainProperties.contains(SL.thick(SL.hard))) {
      wheel.sparks(true);
     }
    }
   }
   if (connected) {
    mode = Mode.driveSolid;
    V.terrainRGB = Ground.RGB;
    if (V.speedY > RichHit.minimumSpeed) {
     V.VA.land();
     for (long n = RichHit.dustQuantity; --n >= 0; ) {
      V.deployDust(true);
     }
    }
    if (V.speedY > 0) {//<-Take no action if vehicle is moving upwards
     if (crashLand) {
      crash(V.speedY * bounceBackForce, false);
     }
     V.speedY *= V.destroyed ? 0 : -V.bounce * flatPlaneBounce;
    }
    if (V.spinner != null && !((Math.abs(V.YZ) < 10 && Math.abs(V.XY) < 10) || (Math.abs(V.YZ) > 170 && Math.abs(V.XY) > 170))) {
     V.spinner.hit(null);
    }
   }
   onAntiGravity = false;
  }
 }

 double getHitsLocalGroundY() {
  return onAntiGravity ? Double.POSITIVE_INFINITY : -5 + localGround;//<-Should eliminate '-5' traction-lock that would prevent vehicles from rising
 }

 private void runWheelSpin() {
  double wheelSpun = U.clamp(-44 / U.tick, 267 * Math.sqrt(Math.abs(StrictMath.pow(speed, 2) * 1.333)) / V.absoluteRadius, 44 / U.tick),
  amount = speed < 0 ? -1 : 1;
  if (Math.abs(amount * wheelSpun * U.tick) > 25) {
   double randomAngle = U.randomPlusMinus(360.);
   wheelSpin[0] = randomAngle;
   wheelSpin[1] = randomAngle;
  } else {
   wheelSpin[0] += amount * wheelSpun * U.tick;
   wheelSpin[1] += amount * wheelSpun * U.tick;
   if (V.steerInPlace) {
    double steerSpin = 667 * speedXZ / V.absoluteRadius;
    wheelSpin[0] += amount * steerSpin * U.tick;
    wheelSpin[1] -= amount * steerSpin * U.tick;
   }
   wheelSpin[0] = Math.abs(wheelSpin[0]) > 360 ? 0 : wheelSpin[0];
   wheelSpin[1] = Math.abs(wheelSpin[1]) > 360 ? 0 : wheelSpin[1];
  }
 }

 private void runDriveVelocity() {
  if (mode.name().startsWith(SL.drive) || V.phantomEngaged) {
   boolean flipped = flipped();
   double drift = V.handbrake && !flipped && !U.equals(Maps.name, SL.Maps.theMaze, SL.Maps.XYLand) ? V.grip * .25 * Math.abs(driftXZ - V.XZ) : 0,
   setGrip = V.grip - drift;
   setGrip *= (terrainProperties.contains(SL.thick(SL.ice)) ? .075 : terrainProperties.contains(SL.thick(SL.ground)) ? .75 : 1) * (flipped ? .2 : 1);
   setGrip = Math.max(setGrip * V.energyMultiple * U.tick, 0);
   double cosYZ = U.cos(V.YZ);
   if (flipped) {
    V.speedX -= V.speedX > setGrip ? setGrip : Math.max(V.speedX, -setGrip);
    V.speedZ -= V.speedZ > setGrip ? setGrip : Math.max(V.speedZ, -setGrip);
   } else {
    double
    velocityX = speed * V.energyMultiple * U.sin(-V.XZ) * cosYZ,
    velocityZ = speed * V.energyMultiple * U.cos(V.XZ) * cosYZ,
    velocityY = speed * V.energyMultiple * U.sin(-V.YZ);
    if (Math.abs(V.speedX - velocityX) > setGrip) {
     V.speedX += setGrip * Double.compare(velocityX, V.speedX);
    } else {
     V.speedX = velocityX;
    }
    if (Math.abs(V.speedZ - velocityZ) > setGrip) {
     V.speedZ += setGrip * Double.compare(velocityZ, V.speedZ);
    } else {
     V.speedZ = velocityZ;
    }
    if (mode == Mode.driveSolid || V.phantomEngaged) {
     if (Math.abs(V.speedY - velocityY) > setGrip && !onMoundSlope && !angledSurface()) {//fixme--angledSurface check can cause hugging on some standard ramps
      V.speedY += setGrip * Double.compare(velocityY, V.speedY);
     } else {
      V.speedY = velocityY;
     }
    }
   }
  }
 }

 private void runSkidsAndDust() {//todo<-Split skidmarks/dust into separate voids eventually?
  if (mode == Mode.driveSolid) {
   int n;
   boolean markedSnow = false;
   if (terrainProperties.contains(SL.thick(SL.snow))) {
    for (Wheel wheel : V.wheels) {
     wheel.skidmark(true);
    }
    markedSnow = true;
   }
   boolean kineticFriction = Math.abs(Math.abs(speed) - netSpeed) > 15,
   driveEngine = !U.containsEnum(V.engine, Vehicle.Engine.prop, Vehicle.Engine.jet, Vehicle.Engine.rocket);
   if (((driveEngine && kineticFriction) || StrictMath.pow(speedXZ, 2) > 300000 / netSpeed) && (kineticFriction || Math.abs(speed) > V.topSpeeds[1] * .9)) {
    if (terrainProperties.contains(SL.thick(SL.hard)) && V.contact == Contact.metal) {
     for (Wheel wheel : V.wheels) {
      wheel.sparks(true);
     }
    }
    if (V.contact == Contact.rubber || !terrainProperties.contains(SL.thick(SL.hard))) {
     for (n = 4; --n >= 0; ) {
      V.deployDust(false);
     }
    }
    if (!terrainProperties.contains(SL.thick(SL.ice))) {
     if (!markedSnow && V.contact == Contact.rubber) {//<-Contact check is so that skidmarks don't misfire due to being preloaded on snow-terrain maps
      for (Wheel wheel : V.wheels) {
       wheel.skidmark(false);
      }
     }
     V.VA.skid();
    }
   } else if (terrainProperties.contains(SL.thick(SL.snow))) {
    for (n = 4; --n >= 0; ) {
     if (U.random() < .4) {
      V.deployDust(false);
     }
    }
   } else if (terrainProperties.contains(SL.thick(SL.ground))) {
    for (n = 4; --n >= 0; ) {
     if (U.random() < .2) {
      V.deployDust(false);
     }
    }
   }
  }
 }

 private void runAirEngage() {
  if (V.type == Vehicle.Type.vehicle && V.handbrake && mode == Mode.neutral) {
   mode = Mode.stunt;
  }
  if (V.type == Vehicle.Type.aircraft && V.drive2) {
   boolean engageFly = mode == Mode.neutral;
   if (mode.name().startsWith(SL.drive) && V.reverse) {
    V.Y -= 10;
    engageFly = true;
   }
   if (engageFly) {
    mode = Mode.fly;
   }
  }
 }

 private void runAerialControl() {
  if (V.aerialControlEnhanced) {
   airSpinXZ = 0;
  }
  if (V.drive) {
   stuntSpeedYZ -= V.airAcceleration < Double.POSITIVE_INFINITY ? V.airAcceleration * U.tick : 0;
   stuntSpeedYZ = stuntSpeedYZ < -V.airTopSpeed || V.airAcceleration == Double.POSITIVE_INFINITY ? -V.airTopSpeed : stuntSpeedYZ;
  }
  if (V.reverse) {
   stuntSpeedYZ += V.airAcceleration < Double.POSITIVE_INFINITY ? V.airAcceleration * U.tick : 0;
   stuntSpeedYZ = stuntSpeedYZ > V.airTopSpeed || V.airAcceleration == Double.POSITIVE_INFINITY ? V.airTopSpeed : stuntSpeedYZ;
  }
  if (!V.drive && !V.reverse) {
   if (V.airAcceleration < Double.POSITIVE_INFINITY) {
    stuntSpeedYZ += (stuntSpeedYZ < 0 ? 1 : stuntSpeedYZ > 0 ? -1 : 0) * V.airAcceleration * U.tick;
   }
   stuntSpeedYZ = Math.abs(stuntSpeedYZ) < V.airAcceleration || V.airAcceleration == Double.POSITIVE_INFINITY ? 0 : stuntSpeedYZ;
  }
  if (!inWall && stuntSpeedYZ < 0) {
   double amount = Math.abs(V.XY) > 90 ? -1 : 1;
   V.X += amount * -V.airPush * U.sin(V.XZ) * -stuntSpeedYZ * U.tick;
   V.Z += amount * V.airPush * U.cos(V.XZ) * -stuntSpeedYZ * U.tick;
  }
  if (stuntSpeedYZ > 0) {
   V.Y -= V.airPush * stuntSpeedYZ * U.tick;
  }
  boolean steerByMouse = V.steerByMouse && (V.handbrake ? stuntSpeedXZ : stuntSpeedXY) * -40 < Mouse.steerX;
  if ((V.turnL && !V.turnR) || steerByMouse) {
   if (V.handbrake) {
    stuntSpeedXZ -= V.airAcceleration < Double.POSITIVE_INFINITY ? V.airAcceleration * U.tick : 0;
    stuntSpeedXZ = stuntSpeedXZ < -V.airTopSpeed || V.airAcceleration == Double.POSITIVE_INFINITY ? -V.airTopSpeed : stuntSpeedXZ;
   } else {
    stuntSpeedXY -= V.airAcceleration < Double.POSITIVE_INFINITY ? V.airAcceleration * U.tick : 0;
    stuntSpeedXY = stuntSpeedXY < -V.airTopSpeed || V.airAcceleration == Double.POSITIVE_INFINITY ? -V.airTopSpeed : stuntSpeedXY;
   }
  }
  if ((V.turnR && !V.turnL) || steerByMouse) {
   if (V.handbrake) {
    if (V.airAcceleration < Double.POSITIVE_INFINITY) {
     stuntSpeedXZ += V.airAcceleration * U.tick;
    }
    if (stuntSpeedXZ > V.airTopSpeed || V.airAcceleration == Double.POSITIVE_INFINITY) {
     stuntSpeedXZ = V.airTopSpeed;
    }
   } else {
    if (V.airAcceleration < Double.POSITIVE_INFINITY) {
     stuntSpeedXY += V.airAcceleration * U.tick;
    }
    if (stuntSpeedXY > V.airTopSpeed || V.airAcceleration == Double.POSITIVE_INFINITY) {
     stuntSpeedXY = V.airTopSpeed;
    }
   }
  }
  if ((!V.turnL && !V.turnR && !V.steerByMouse) || !V.handbrake) {
   if (V.airAcceleration < Double.POSITIVE_INFINITY) {
    stuntSpeedXZ += (stuntSpeedXZ < 0 ? 1 : stuntSpeedXZ > 0 ? -1 : 0) * V.airAcceleration * U.tick;
   }
   if (Math.abs(stuntSpeedXZ) < V.airAcceleration || V.airAcceleration == Double.POSITIVE_INFINITY) {
    stuntSpeedXZ = 0;
   }
  }
  if ((!V.turnL && !V.turnR && !V.steerByMouse) || V.handbrake) {
   if (V.airAcceleration < Double.POSITIVE_INFINITY) {
    stuntSpeedXY += (stuntSpeedXY < 0 ? 1 : stuntSpeedXY > 0 ? -1 : 0) * V.airAcceleration * U.tick;
   }
   if (Math.abs(stuntSpeedXY) < V.airAcceleration || V.airAcceleration == Double.POSITIVE_INFINITY) {
    stuntSpeedXY = 0;
   }
  }
  V.YZ += 20 * stuntSpeedYZ * U.cos(V.XY) * U.tick;
  V.XZ -= 20 * polarity * stuntSpeedYZ * U.sin(V.XY) * U.tick;
  V.XZ -= stuntSpeedXZ * 20 * polarity * U.tick;
  V.XY += 20 * stuntSpeedXY * U.tick;
  if (!inWall) {
   V.X += V.airPush * U.cos(V.XZ) * polarity * stuntSpeedXY * U.tick;
   V.Z += V.airPush * U.sin(V.XZ) * polarity * stuntSpeedXY * U.tick;
  }
 }

 private void runFlight(double turnAmount, double turnSpeed) {
  if (mode == Mode.fly) {
   if (V.handbrake) {
    V.XZ += speedXZ * turnSpeed * angleToSteerVelocity * polarity * U.tick;
    if (Math.abs(V.XY) < turnAmount * angleToSteerVelocity * U.tick) {
     V.XY = 0;
    } else {
     V.XY += (V.XY < 0 ? 1 : V.XY > 0 ? -1 : 0) * turnAmount * angleToSteerVelocity * U.tick;
    }
   } else {
    double amountXY = speedXZ * airAngleToSteerVelocity * U.tick;
    V.XY -= amountXY;
    stuntXY -= amountXY;
   }
   //DONE ADJUSTING V.XY
   double sinXY = U.sin(V.XY),
   halfAngleToSteer = airAngleToSteerVelocity * .5,
   amountYZ = speedYZ * halfAngleToSteer * U.cos(V.XY) * U.tick;
   V.YZ += amountYZ;
   stuntYZ -= amountYZ;
   //DONE ADJUSTING V.YZ
   double cosYZ = U.cos(V.YZ);
   if (!V.handbrake && V.engine != Vehicle.Engine.powerjet) {
    double amountXZ = speedYZ * halfAngleToSteer * sinXY * polarity * U.tick;
    V.XZ -= amountXZ;
    stuntXZ -= amountXZ;
   }
   double amountXZ = (speed < 0 ? -5 : 5) * sinXY * polarity * U.tick;
   V.XZ -= amountXZ;
   stuntXZ -= amountXZ;
   V.speedX = speed * V.energyMultiple * U.sin(-V.XZ) * cosYZ;
   V.speedZ = speed * V.energyMultiple * U.cos(V.XZ) * cosYZ;
   V.speedY = speed * V.energyMultiple * U.sin(-V.YZ) + stallSpeed;
   if (E.gravity == 0 || onAntiGravity || V.floats || V.energyMultiple > 1) {
    stallSpeed = 0;
   } else {
    stallSpeed += E.gravity * U.tick;
    if (Math.abs(speed) > 0 && stallSpeed > 0) {
     stallSpeed -= Math.abs(speed) * U.tick * (V.engine == Vehicle.Engine.smallprop ? .04 : .02);
    }
    stallSpeed *= inPool ? Math.min(.95 * U.tick, 1) : 1;
   }
  } else {
   stallSpeed = V.speedY;
  }
 }

 private void runSpeedBoost() {
  if (V.boost && V.speedBoost > 0 && !V.destroyed) {
   if (Math.abs(V.speedX) < V.topSpeeds[2] || directionAgainstSpeedX()) {
    V.speedX -= V.speedBoost * U.sin(V.XZ) * polarity * U.tick;
   }
   if (Math.abs(V.speedZ) < V.topSpeeds[2] || directionAgainstSpeedZ()) {
    V.speedZ += V.speedBoost * U.cos(V.XZ) * polarity * U.tick;
   }
   if (Math.abs(V.speedY) < V.topSpeeds[2] || directionAgainstSpeedY()) {
    V.speedY -= V.speedBoost * U.sin(V.YZ) * U.tick;
   }
   if (!V.highGrip()) {
    speed += V.speedBoost * U.tick;
   }
  }
 }

 //*Only used to monitor speedBoosts so far--the algorithm is inexact
 public boolean directionAgainstSpeedX() {//*
  return polarity < 0 ?
  (V.speedX > 0 && V.XZ < 0) || (V.speedX < 0 && V.XZ > 0) :
  (V.speedX > 0 && V.XZ > 0) || (V.speedX < 0 && V.XZ < 0);
 }

 public boolean directionAgainstSpeedZ() {//*
  double cosXZ = U.cos(V.XZ);
  return polarity < 0 ?
  (V.speedZ > 0 && cosXZ > 0) || (V.speedZ < 0 && cosXZ < 0) :
  (V.speedZ > 0 && cosXZ < 0) || (V.speedZ < 0 && cosXZ > 0);
 }

 public boolean directionAgainstSpeedY() {//*
  return (V.speedY > 0 && V.YZ > 0) || (V.speedY < 0 && V.YZ < 0);
 }

 private void runPoolInteract() {
  if (inPool && !onFlatTrackPlane) {//<-onFlatTrackPlane check is risky, but should work for now
   if (netSpeed > 0) {
    for (int n = 3; --n >= 0; ) {
     for (Wheel wheel : V.wheels) {
      V.splashes.get(V.currentSplash).deploy(wheel, V.absoluteRadius * .0125 + U.random(V.absoluteRadius * .0125),
      V.speedX + U.randomPlusMinus(Math.max(speed, netSpeed)),
      V.speedY + U.randomPlusMinus(Math.max(speed, netSpeed)),
      V.speedZ + U.randomPlusMinus(Math.max(speed, netSpeed)));
      V.currentSplash = ++V.currentSplash >= Splash.defaultQuantity ? 0 : V.currentSplash;
     }
    }
   }
   V.VA.splashing = netSpeed;
   if (!V.reviveImmortality) {
    if (Pool.type == Pool.Type.lava) {
     V.addDamage(30 * U.tick);
     V.deformParts();
    } else {
     V.addDamage(Pool.type == Pool.Type.acid ? .0025 * V.durability * U.tick : 0);
    }
   }
   V.speedX -= V.speedX * .01 * U.tick;
   V.speedY -= V.speedY * .1 * U.tick;
   V.speedZ -= V.speedZ * .01 * U.tick;
   if (V.amphibious == Vehicle.Amphibious.ON) {
    mode = Mode.drivePool;
   }
  }
 }

 private void runTrackPlaneInteraction(boolean crashLand, double bounceBackForce, double flatPlaneBounce, double gravityCompensation) {
  onFlatTrackPlane = false;
  double sinXZ = U.sin(V.XZ), cosXZ = U.cos(V.XZ), cosYZ = U.cos(V.YZ), cosXY = U.cos(V.XY),
  wallPlaneBounce = Math.min(Math.abs(cosXY) + Math.abs(cosYZ), 1),//<-Not the best, but still probably better than a flat value
  crashPower = 0;
  boolean spinnerHit = inWall = false, flipped = flipped();
  for (TrackPart trackPart : TE.trackParts) {
   for (TrackPlane trackPlane : trackPart.trackPlanes) {
    double trackX = trackPlane.X + trackPart.X, trackY = trackPlane.Y + trackPart.Y, trackZ = trackPlane.Z + trackPart.Z,
    velocityXZ = Math.abs(sinXZ),
    radiusX = trackPlane.radiusX + (trackPlane.addSpeed && velocityXZ > U.sin45 ? netSpeed * U.tick : 0),
    radiusY = trackPlane.radiusY + (trackPlane.addSpeed ? netSpeed * U.tick : 0),
    radiusZ = trackPlane.radiusZ + (trackPlane.addSpeed && velocityXZ < U.sin45 ? netSpeed * U.tick : 0);
    boolean
    gate = trackPlane.type.contains(SL.gate),
    antiGravity = trackPlane.type.contains(SL.antigravity),
    inX = Math.abs(V.X - trackX) <= radiusX,
    inZ = Math.abs(V.Z - trackZ) <= radiusZ;
    //NON-WHEEL BASED
    if (inX && inZ && Math.abs(V.Y - (trackY + gravityCompensation)) <= trackPlane.radiusY) {
     if (gate) {
      runSpeedGate(trackPlane);
     } else if (antiGravity) {
      V.speedY -= E.gravity * 2 * U.tick;
      onAntiGravity = true;
     }
    }
    if (!gate && !antiGravity) {
     boolean
     tree = trackPlane.type.contains(SL.thick(SL.tree)),
     isWall = trackPlane.wall != TrackPlane.Wall.none,
     hard = false;
     if (!tree && (isWall || (inX && inZ && trackY + radiusY * .5 >= V.Y))) {
      hard = U.contains(trackPlane.type, SL.thick(SL.paved), SL.thick(SL.rock), SL.thick(SL.grid), SL.thick(SL.antigravity), SL.thick(SL.metal), SL.thick(SL.brightmetal));
      if (!isWall) {//Borderline ridiculous code, but it's the only thing seemingly working
       terrainProperties = trackPlane.type + SL.thick(hard ? SL.hard : SL.ground);
      }
     }
     boolean criterion = V.Y >= trackY || V.speedY >= 0 || Math.abs(speed) < E.gravity * 4 * U.tick,
     angleAdjustForYZ = criterion || Math.abs((cosXZ > 0 ? -V.YZ : V.YZ) - trackPlane.YZ) < 30,
     angleAdjustForXY = criterion || Math.abs((sinXZ < 0 ? -V.YZ : V.YZ) - trackPlane.XY) < 30;
     //WHEEL BASED
     for (Wheel wheel : V.wheels) {
      if (Math.abs(wheel.Y - (trackY + gravityCompensation)) <= trackPlane.radiusY) {
       boolean wheelInX = Math.abs(wheel.X - trackX) <= radiusX, wheelInZ = Math.abs(wheel.Z - trackZ) <= radiusZ;
       if (wheelInX && wheelInZ) {
        if (tree) {
         V.speedX -= U.random() * V.speedX * U.tick;
         V.speedY -= U.random() * V.speedY * U.tick;
         V.speedZ -= U.random() * V.speedZ * U.tick;
         wheel.againstWall = true;
        } else if (!isWall) {
         if (trackPlane.YZ == 0 && trackPlane.XY == 0 && wheel.Y > trackY - 5) {//'- 5' is for better traction control--not to be used for map parts, etc.. Do not transfer '-5' to any assignments
          mode = Mode.driveSolid;
          localGround = Math.min(localGround, trackY);
          if (flipped && hard) {
           wheel.sparks(true);
          }
          if (crashLand) {
           crashPower = Math.max(crashPower, Math.abs(V.speedY * bounceBackForce));
          }
          if (V.speedY > RichHit.minimumSpeed) {
           for (long n = RichHit.dustQuantity; --n >= 0; ) {
            V.deployDust(true);
           }
           V.VA.land();
          }
          if (V.speedY > 0) {
           V.speedY *= V.destroyed ? 0 : -V.bounce * flatPlaneBounce;
          }
          wheel.XY -= wheel.XY * valueAdjustSmoothing * U.tick;
          wheel.YZ -= wheel.YZ * valueAdjustSmoothing * U.tick;
          wheel.minimumSkidmarkY = trackY;
          onFlatTrackPlane = true;
         } else if (trackPlane.YZ != 0) {
          double setY = trackY + (wheel.Z - trackZ) * (trackPlane.radiusY / trackPlane.radiusZ) * (trackPlane.YZ > 0 ? 1 : trackPlane.YZ < 0 ? -1 : 0);
          if (wheel.Y >= setY - Math.max(wheelGapFrontToBack, wheelGapLeftToRight)) {
           wheel.angledSurface = true;
           mode = Mode.driveSolid;
           if (!hard) {
            V.deployDust(false);
           } else if (flipped) {
            wheel.sparks(true);
           }
           if (angleAdjustForYZ) {
            wheel.YZ += (-trackPlane.YZ * cosXZ - wheel.YZ) * valueAdjustSmoothing * U.tick;
           }
           wheel.Y = setY;//<-Outside of angle-adjust block, otherwise vehicles could sink under surfaces
           wheel.XY += (trackPlane.YZ * sinXZ - wheel.XY) * valueAdjustSmoothing * U.tick;
          }
         } else if (trackPlane.XY != 0) {
          double setY = trackY + (wheel.X - trackX) * (trackPlane.radiusY / trackPlane.radiusX) * (trackPlane.XY > 0 ? 1 : trackPlane.XY < 0 ? -1 : 0);
          if (wheel.Y >= setY - Math.max(wheelGapFrontToBack, wheelGapLeftToRight)) {
           wheel.angledSurface = true;
           mode = Mode.driveSolid;
           if (!hard) {
            V.deployDust(false);
           } else if (flipped) {
            wheel.sparks(true);
           }
           if (angleAdjustForXY) {
            wheel.YZ += (trackPlane.XY * sinXZ - wheel.YZ) * valueAdjustSmoothing * U.tick;
           }
           wheel.Y = setY;//<-Outside of angle-adjust block, otherwise vehicles could sink under surfaces
           wheel.XY += (trackPlane.XY * cosXZ - wheel.XY) * valueAdjustSmoothing * U.tick;
          }
         }
        }
       }
       if (isWall) {
        double vehicleRadius = V.collisionRadius * .5, contactX = trackPlane.radiusX + vehicleRadius, contactZ = trackPlane.radiusZ + vehicleRadius;
        if (wheelInX && Math.abs(wheel.Z - trackZ) <= contactZ) {
         if (
         (trackPlane.wall == TrackPlane.Wall.front && wheel.Z < trackZ + contactZ && V.speedZ < 0) ||
         (trackPlane.wall == TrackPlane.Wall.back && wheel.Z > trackZ - contactZ && V.speedZ > 0)) {
          if (hard) {
           wheel.sparks(false);
          }
          crashPower = Math.max(crashPower, Math.abs(V.speedZ * trackPlane.damage));
          V.speedZ *= -1 * V.bounce * wallPlaneBounce;
          spinnerHit = wheel.againstWall = true;
         }
         inWall = true;
        }
        if (wheelInZ && Math.abs(wheel.X - trackX) <= contactX) {
         if (
         (trackPlane.wall == TrackPlane.Wall.right && wheel.X < trackX + contactX && V.speedX < 0) ||
         (trackPlane.wall == TrackPlane.Wall.left && wheel.X > trackX - contactX && V.speedX > 0)) {
          if (hard) {
           wheel.sparks(false);
          }
          crashPower = Math.max(crashPower, Math.abs(V.speedX * trackPlane.damage));
          V.speedX *= -1 * V.bounce * wallPlaneBounce;
          spinnerHit = wheel.againstWall = true;
         }
         inWall = true;
        }
       }
      }
     }
    }
   }
  }
  if (crashPower > 0) {//<-Math.abs not needed as value should always be positive
   crash(crashPower, false);
  }
  if (V.spinner != null && spinnerHit) {
   V.spinner.hit(null);
  }
  if (angledSurface()) {
   V.Y = Math.min(V.Y, wheelBasedY());
  }
 }

 private void runSpeedGate(TrackPlane trackPlane) {
  if (trackPlane.type.contains(" slowgate ")) {
   if (Math.abs(trackPlane.YZ) == 90) {
    if (Math.abs(V.speedZ) > V.topSpeeds[0]) {
     V.speedZ *= .333;
     speed *= .333;
     V.VA.gate.playIfNotPlaying(1, V.VA.distanceVehicleToCamera);
    }
   } else if (Math.abs(trackPlane.XY) == 90) {
    if (Math.abs(V.speedX) > V.topSpeeds[0]) {
     V.speedX *= .333;
     speed *= .333;
     V.VA.gate.playIfNotPlaying(1, V.VA.distanceVehicleToCamera);
    }
   }
  } else {
   V.speedZ *= Math.abs(trackPlane.YZ) == 90 ? 3 : 1;
   V.speedX *= Math.abs(trackPlane.XY) == 90 ? 3 : 1;
   if (V.highGrip()) {
    speed *= (speed > 0 && speed < V.topSpeeds[1]) || (speed < 0 && speed > -V.topSpeeds[0]) ? 3 : 1;
   }
   if (V.speedX != 0 || V.speedZ != 0) {
    V.VA.gate.play(0, V.VA.distanceVehicleToCamera);
   }
  }
 }

 private void runSetTerrainFromTrackPlanes(double gravityCompensation) {
  for (TrackPart trackPart : TE.trackParts) {
   for (TrackPlane trackPlane : trackPart.trackPlanes) {
    if (trackPlane.wall == TrackPlane.Wall.none && !trackPlane.type.contains(SL.thick(SL.tree)) && !trackPlane.type.contains(SL.gate)) {
     double trackX = trackPlane.X + trackPart.X, trackY = trackPlane.Y + trackPart.Y, trackZ = trackPlane.Z + trackPart.Z;
     if (Math.abs(V.X - trackX) <= trackPlane.radiusX && Math.abs(V.Z - trackZ) <= trackPlane.radiusZ &&
     Math.abs(V.Y + clearance - (trackY + gravityCompensation)) <= trackPlane.radiusY) {
      String addHard = U.contains(trackPlane.type, SL.thick(SL.paved), SL.thick(SL.rock), SL.thick(SL.grid), SL.thick(SL.antigravity), SL.thick(SL.metal), SL.thick(SL.brightmetal)) ? SL.thick(SL.hard) : SL.thick(SL.ground);
      if (trackPlane.YZ == 0 && trackPlane.XY == 0) {
       terrainProperties = trackPlane.type + addHard;
       V.terrainRGB = trackPlane.RGB;
      } else if (trackPlane.YZ != 0) {
       double setY = trackY + (V.Z - trackZ) * (trackPlane.radiusY / trackPlane.radiusZ) * (trackPlane.YZ > 0 ? 1 : trackPlane.YZ < 0 ? -1 : 0);
       if (V.Y >= setY - 100) {
        terrainProperties = trackPlane.type + addHard;
        V.terrainRGB = trackPlane.RGB;
       }
      } else if (trackPlane.XY != 0) {
       double setY = trackY + (V.X - trackX) * (trackPlane.radiusY / trackPlane.radiusX) * (trackPlane.XY > 0 ? 1 : trackPlane.XY < 0 ? -1 : 0);
       if (V.Y >= setY - 100) {
        terrainProperties = trackPlane.type + addHard;
        V.terrainRGB = trackPlane.RGB;
       }
      }
     }
    }
   }
  }
 }

 void runMoundInteract(double gravityCompensation) {
  onMoundSlope = atOrAboveAndWithinMoundTopRadius = false;
  if (!V.phantomEngaged) {
   boolean flipped = flipped();
   for (FrustumMound FM : TE.mounds) {
    if (!V.bumpIgnore || !FM.wraps) {
     double distanceXZ = U.distanceXZ(V, FM),
     radiusTop = FM.mound.getMinorRadius(), moundHeight = FM.mound.getHeight();
     if (distanceXZ < radiusTop) {
      if (V.Y - clearance <= FM.Y + gravityCompensation) {//<-Don't remove or vehicles will be lifted to airborne mounds!
       localGround = Math.min(localGround, FM.Y - moundHeight);
       atOrAboveAndWithinMoundTopRadius = true;
      }
     } else {
      double radiusBottom = FM.mound.getMajorRadius();
      if (distanceXZ < radiusBottom && Math.abs(V.Y + clearance - ((FM.Y - (moundHeight * .5)) + gravityCompensation)) <= moundHeight * .5) {
       double slope = moundHeight / Math.abs(radiusBottom - radiusTop),
       finalHeight = FM.Y - (radiusBottom - distanceXZ) * slope - clearance;
       if (V.Y >= finalHeight) {
        double baseAngle = U.arcTan(slope) + (flipped ? 180 : 0),
        vehicleMoundXZ = V.XZ, moundPlaneY = Math.max(radiusTop * .5, (radiusBottom * .5) - ((radiusBottom * .5) * (Math.abs(V.Y) / moundHeight)));
        vehicleMoundXZ +=
        V.Z < FM.Z && Math.abs(V.X - FM.X) < moundPlaneY ? 180 :
        V.X >= FM.X + moundPlaneY ? 90 :
        V.X <= FM.X - moundPlaneY ? -90 :
        0;
        if (flipped) {
         V.XY = baseAngle * U.sin(vehicleMoundXZ);
         V.YZ = -baseAngle * U.cos(vehicleMoundXZ);
        } else {//Splitting this helps keep flipped vehicle landings more accurate, for some reason
         V.XY += (baseAngle * U.sin(vehicleMoundXZ) - V.XY) * valueAdjustSmoothing * U.tick;
         V.YZ += (-baseAngle * U.cos(vehicleMoundXZ) - V.YZ) * valueAdjustSmoothing * U.tick;
        }
        V.Y = finalHeight;
        mode = Physics.Mode.driveSolid;
        terrainProperties = Terrain.vehicleDefaultTerrain;
        V.terrainRGB = Ground.RGB;
        onMoundSlope = true;
       }
      }
     }
    }
   }
  }
 }

 private void runBouncyTerrain(boolean onBouncy) {
  if (onBouncy) {
   V.speedY -= U.random(terrainProperties.contains(SL.thick(SL.bounce)) ? .3 : .6) * Math.abs(speed) * V.bounce;
   if (V.speedY < -50 && V.bounce > .9) {
    V.VA.land.playIfNotPlaying(Double.NaN, V.VA.distanceVehicleToCamera);
   }
  }
 }

 private void runSpeedConstraints() {
  if (mode.name().startsWith(SL.drive) || V.phantomEngaged) {
   if (!V.highGrip() && !flipped() && Math.abs(speed) > netSpeed && Math.abs(Math.abs(speed) - netSpeed) > Math.abs(speed) * .5 && !terrainProperties.contains(SL.thick(SL.ice))) {
    speed *= .5;
   }
   if (V.turnDrag && (V.turnL || V.turnR)) {
    speed -= speed * .01 * U.tick;
   }
   if (V.destroyed) {
    speed *= .9;
    if (Math.abs(speed) < 2 * U.tick) {
     speed = 0;
    } else {
     speed += (speed < 0 ? 2 : speed > 0 ? -2 : 0) * U.tick;
    }
   }
  }
  speed = U.clamp(-V.topSpeeds[2], speed, V.topSpeeds[2]);
  if (againstWall() && V.highGrip()) {
   speed *= .95;
  }
  if (V.drag != 0) {
   if (Math.abs(speed) < V.drag * U.tick) {
    speed = 0;
   } else if ((mode != Mode.fly && !V.drive && !V.reverse) || mode == Mode.stunt || Math.abs(speed) > V.topSpeeds[1]) {
    speed -= (speed > 0 ? 1 : -1) * V.drag * U.tick;
   }
  }
  if (Math.abs(speed) > V.topSpeeds[1]) {
   speed -= speed * Math.abs(speed) * .0000005 * U.tick;
  }
 }

 boolean moundHugPrevent() {
  return (onMoundSlope || atOrAboveAndWithinMoundTopRadius) && V.speedY < -E.gravity * 4 * U.tick && !groundPlunge();
 }

 boolean groundPlunge() {//<-Only tested in moundHugPrevent()--not certified for general use
  return speed * U.sin(V.YZ) < 0;
 }

 private void runAngleSet() {
  //*This looks redundant and as if it could be written more succinctly, but only this way seems to work correctly--do NOT change!
  if (mode.name().startsWith(SL.drive) && !onMoundSlope) {
   if (angledSurface()) {
    V.XY = (Math.abs(V.XY) > 90 ? 180 : 0) + (V.wheels.get(0).XY + V.wheels.get(1).XY + V.wheels.get(2).XY + V.wheels.get(3).XY) * .25;
    V.YZ = (Math.abs(V.YZ) > 90 ? 180 : 0) + (V.wheels.get(0).YZ + V.wheels.get(1).YZ + V.wheels.get(2).YZ + V.wheels.get(3).YZ) * .25;
   } else if (!onVolcano) {
    if (Math.abs(V.XY) <= 90) {
     V.XY -= V.XY > 0 ? U.tick : 0;//*
     V.XY += V.XY < 0 ? U.tick : 0;//*
    }
    if (V.sidewaysLandingAngle == 0) {
     V.XY += (V.XY > 90 && V.XY < 180 - U.tick ? 1 : V.XY < -90 && V.XY > -180 + U.tick ? -1 : 0) * U.tick;
    } else {
     V.XY += U.random(3.) * U.tick *
     ((V.XY > 90 && V.XY < V.sidewaysLandingAngle) || (V.XY < -V.sidewaysLandingAngle && V.XY > -(V.sidewaysLandingAngle + 30)) ? 1 :
     (V.XY < -90 && V.XY > -V.sidewaysLandingAngle) || (V.XY > V.sidewaysLandingAngle && V.XY < V.sidewaysLandingAngle + 30) ? -1 : 0);
     if (Math.abs(V.XY) >= V.sidewaysLandingAngle + 30) {
      V.XY -= V.XY > -180 + U.tick ? U.tick : 0;//*
      V.XY += V.XY < 180 - U.tick ? U.tick : 0;//*
     }
    }
    double centerOut = mode == Mode.drivePool ? .9375 : .25;
    if (Math.abs(V.XY) <= 90) {
     V.XY *= centerOut;
    }
    if (!moundHugPrevent()) {
     if (Math.abs(V.YZ) <= 90) {
      V.YZ -= V.YZ > 0 ? U.tick : 0;//*
      V.YZ += V.YZ < 0 ? U.tick : 0;//*
      V.YZ *= centerOut;
     } else {
      V.YZ += (V.YZ > 90 && V.YZ < 180 - U.tick ? 1 : V.YZ < -90 && V.YZ > -180 + U.tick ? -1 : 0) * U.tick;
     }
    }
   }
  }
 }

 private void runLandInvert() {
  if (mode.name().startsWith(SL.drive)) {
   if (Math.abs(V.YZ) > 90 && Math.abs(V.XY) > 90) {
    long randomFlip = U.random() < .5 ? 180 : -180;
    V.XY += randomFlip;
    V.YZ += randomFlip;
    V.XZ += randomFlip;
   }
   if (flipped() && V.landStuntsBothSides) {
    V.XZ += Math.abs(V.YZ) > 90 && Math.abs(V.XY) < 90 ? 180 : 0;
    V.XY = V.YZ = 0;
   }
  }
 }

 private void runHitOther() {
  if (!Double.isNaN(hitOtherX)) {
   V.speedX = hitOtherX;
   hitOtherX = Double.NaN;
  }
  if (!Double.isNaN(hitOtherZ)) {
   V.speedZ = hitOtherZ;
   hitOtherZ = Double.NaN;
  }
 }

 double rotatedClearance() {
  return clearance * U.cos(V.YZ) * U.cos(V.XY);//<-U.cos's correct upside-down landings
 }

 double wheelBasedY() {
  return (V.wheels.get(0).Y + V.wheels.get(1).Y + V.wheels.get(2).Y + V.wheels.get(3).Y) * .25 - rotatedClearance();
 }

 void runSetCorePosition(boolean onBouncy) {
  if (!onMoundSlope && !onVolcano) {//<-V.Y handled in respective areas
   if (mode == Mode.driveSolid && !onBouncy) {
    V.Y = wheelBasedY();
   } else {
    V.Y += V.speedY * U.tick;
   }
  }
  V.X += V.speedX * U.tick;
  V.Z += V.speedZ * U.tick;
  if (!V.phantomEngaged) {
   V.Y = Math.min(V.Y, localGround - rotatedClearance());//<-Prevents vehicles from going underground
  }
 }

 private void runSetWheelXYZ() {
  double primeX = V.speedX * U.tick, primeY = V.speedY * U.tick, primeZ = V.speedZ * U.tick;
  for (Wheel wheel : V.wheels) {
   wheel.beneathLocalGround = false;
   double[] wheelX = {wheel.pointX}, wheelY = {clearance}, wheelZ = {wheel.pointZ};
   if (V.XY != 0) {
    U.rotate(wheelX, wheelY, V.XY);
   }
   if (V.YZ != 0) {
    U.rotate(wheelY, wheelZ, V.YZ);
   }
   U.rotate(wheelX, wheelZ, V.XZ);
   //*Must be primed with existing speeds or will be delayed from Core positions!
   wheel.X = wheelX[0] + V.X + primeX;//*
   wheel.Y = wheelY[0] + V.Y + primeY;//*
   wheel.Z = wheelZ[0] + V.Z + primeZ;//*
   if (wheel.Y > getHitsLocalGroundY()) {
    wheel.Y = localGround;
    wheel.beneathLocalGround = true;
   }
  }
 }
}
