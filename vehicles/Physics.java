package ve.vehicles;

import javafx.scene.paint.Color;
import ve.Camera;
import ve.Network;
import ve.Sound;
import ve.VE;
import ve.effects.Explosion;
import ve.environment.*;
import ve.trackElements.TE;
import ve.trackElements.trackParts.TrackPart;
import ve.trackElements.trackParts.TrackPlane;
import ve.utilities.SL;
import ve.utilities.U;
import ve.vehicles.specials.Shot;
import ve.vehicles.specials.Special;

public class Physics {
 private final Vehicle V;
 public double speed, speedX, speedY, speedZ,
 speedXZ, speedYZ, stallSpeed;
 public double netSpeed;
 double minimumFlightSpeedWithoutStall;
 private double driftXZ;
 public double cameraXZ;
 private double airSpinXZ;
 final double[] wheelSpin = new double[2];
 private double hitOtherX, hitOtherZ;
 double destructTimer;
 double massiveHitTimer;
 public double localGround;
 public boolean flipped;
 public double stuntTimer;
 public double stuntXY, stuntYZ, stuntXZ;
 double stuntSpeedYZ, stuntSpeedXY, stuntSpeedXZ;
 public double flipTimer, stuntReward, stuntLandWaitTime = 8;
 private boolean onAntiGravity;
 public boolean inTornado;
 boolean onVolcano;
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
 double explosionDiameter, explosionDamage, explosionPush;
 static final double fromAngleToVelocityConstant = .2;
 static final double valueAdjustSmoothing = 1;
 static final double highGrip = 100;
 public long polarity;
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
  if (V.type == Vehicle.Type.aircraft) {
   stuntLandWaitTime = 1;
   minimumFlightSpeedWithoutStall = V.floats ? 0 : E.gravity * (V.engine == Vehicle.Engine.smallprop ? .25 : .5) * 100;
  }
 }

 private boolean angledSurface() {
  return V.wheels.get(0).angledSurface || V.wheels.get(1).angledSurface || V.wheels.get(2).angledSurface || V.wheels.get(3).angledSurface;
 }

 boolean againstWall() {
  return V.wheels.get(0).againstWall || V.wheels.get(1).againstWall || V.wheels.get(2).againstWall || V.wheels.get(3).againstWall;
 }

 private double getNetSpeed() {
  return U.netValue(speedX, speedY, speedZ);
 }

 private void crash(double power, boolean vehicleCollide) {
  power = Math.abs(power);
  double fragilityBased = power * V.fragility;
  if (fragilityBased > (vehicleCollide ? 0 : 50)) {
   V.addDamage(fragilityBased * 4 * VE.tick);
   for (VehiclePart part : V.parts) {
    part.deform();
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
  boolean replay = VE.status == VE.Status.replay, greenTeam = V.index < VE.vehiclesInMatch >> 1;
  if (!V.phantomEngaged) {
   if (!V.destroyed && !V.reviveImmortality) {
    for (Vehicle otherV : VE.vehicles) {
     if (!U.sameTeam(V, otherV) && !otherV.destroyed && !otherV.reviveImmortality && !otherV.phantomEngaged) {
      double collideAt = V.collisionRadius + otherV.collisionRadius,//<-This is absolute--there's no reason for it to be any other value. Change the default value if needed
      behindX = V.X - (speedX * VE.tick),
      behindY = V.Y - (speedY * VE.tick),
      behindZ = V.Z - (speedZ * VE.tick),
      averageX = (V.X + behindX) * .5,
      averageY = (V.Y + behindY) * .5,
      averageZ = (V.Z + behindZ) * .5;
      if (
      ((U.distance(averageY, otherV.Y, averageZ, otherV.Z) < collideAt || U.distance(behindY, otherV.Y, behindZ, otherV.Z) < collideAt) && ((V.X > otherV.X && behindX < otherV.X) || (V.X < otherV.X && behindX > otherV.X))) ||//<-inBoundsX
      ((U.distance(averageX, otherV.X, averageY, otherV.Y) < collideAt || U.distance(behindX, otherV.X, behindY, otherV.Y) < collideAt) && ((V.Z > otherV.Z && behindZ < otherV.Z) || (V.Z < otherV.Z && behindZ > otherV.Z))) ||//<-inBoundsZ
      ((U.distance(averageX, otherV.X, averageZ, otherV.Z) < collideAt || U.distance(behindX, otherV.X, behindZ, otherV.Z) < collideAt) && ((V.Y > otherV.Y && behindY < otherV.Y) || (V.Y < otherV.Y && behindY > otherV.Y))) ||//<-inBoundsY
      U.distance(V, otherV) < collideAt) {
       if (V.getsLifted > 0 && V.Y < otherV.Y) {
        speedY -= E.gravity * 1.5 * VE.tick;
       }
       double yourDamage = Math.abs(netSpeed - otherV.P.netSpeed) * otherV.damageDealt * otherV.energyMultiple;//<-Damage now RECEIVING from other vehicles--not vice versa
       //Don't multiply 'yourDamage' by a constant at initialization or it'll skew scores!
       if (V.isIntegral()) {
        VE.Match.scoreDamage[greenTeam ? 1 : 0] += replay ? 0 : yourDamage;
       }
       hitCheck(otherV);
       double theirPushX = 0, theirPushZ = 0, yourPushX = 0, yourPushZ = 0;
       if (otherV.isIntegral() && V.getsPushed >= otherV.getsPushed) {
        theirPushX = V.getsPushed * (otherV.P.speedX - speedX);
        theirPushZ = V.getsPushed * (otherV.P.speedZ - speedZ);
       }
       if (V.isIntegral() && V.getsPushed <= otherV.getsPushed) {
        yourPushX = V.pushesOthers * (speedX - otherV.P.speedX);
        yourPushZ = V.pushesOthers * (speedZ - otherV.P.speedZ);
       }
       if (V.getsPushed >= otherV.getsPushed) {
        if (
        (V.X > otherV.X && speedX < otherV.P.speedX) ||
        (V.X < otherV.X && speedX > otherV.P.speedX)) {
         hitOtherX = otherV.P.speedX;
        }
        if (
        (V.Z > otherV.Z && speedZ < otherV.P.speedZ) ||
        (V.Z < otherV.Z && speedZ > otherV.P.speedZ)) {
         hitOtherZ = otherV.P.speedZ;
        }
       }
       if (otherV.getsLifted > 0 && otherV.Y != V.Y) {
        otherV.P.speedY -= V.liftsOthers * (otherV.Y < V.Y ? 1 : -1) * Math.abs(U.netValue(speedX, speedZ) - U.netValue(otherV.P.speedX, otherV.P.speedZ));
       }
       if (V.getsLifted > 0 && V.Y != otherV.Y) {
        speedY -= V.getsLifted * (V.Y < otherV.Y ? 1 : -1) * Math.abs(U.netValue(speedX, speedZ) - U.netValue(otherV.P.speedX, otherV.P.speedZ));
       }
       if (speedY < -RichHit.minimumSpeed) {
        V.VA.land();
       }//^Lifting before pushing is probably better
       otherV.P.speedX += yourPushX;
       otherV.P.speedZ += yourPushZ;
       speedX -= theirPushX;
       speedZ -= theirPushZ;
       crash(yourDamage * 2, true);
       if (V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name())) {
        V.setDamage(V.damageCeiling());
        otherV.setDamage(otherV.damageCeiling());
        VE.Match.scoreDamage[greenTeam ? 0 : 1] += replay ? 0 : otherV.durability;
        V.setCameraShake(Camera.shakePresets.normalNuclear);
       }
       if (V.dealsMassiveDamage() && (massiveHitTimer <= 0 || otherV.isIntegral())) {
        otherV.setDamage(otherV.damageCeiling());
        VE.Match.scoreDamage[greenTeam ? 0 : 1] += replay ? 0 : otherV.durability;
        V.VA.massiveHit.play(Double.NaN, V.VA.distanceVehicleToCamera);
        massiveHitTimer = U.random(5.);
        for (VehiclePart part : otherV.parts) {
         part.deform();
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
    for (Vehicle vehicle : VE.vehicles) {
     if (!U.sameTeam(V, vehicle) && (!vehicle.destroyed || wrathEngaged) && !vehicle.reviveImmortality && !vehicle.phantomEngaged) {
      double diameter = special.type == Special.Type.mine ? vehicle.P.netSpeed : special.diameter;
      for (Shot shot : special.shots) {
       if (shot.stage > 0 && shot.hit < 1 && (shot.doneDamaging == null || !shot.doneDamaging[vehicle.index]) && (special.type != Special.Type.missile || vehicle.isIntegral()) && !(special.type == Special.Type.mine && (U.distance(shot, vehicle) > 2000 || !vehicle.isIntegral()))) {
        double collideAt = special.diameter + vehicle.collisionRadius,
        shotAverageX = (shot.X + shot.behindX) * .5,
        shotAverageY = (shot.Y + shot.behindY) * .5,
        shotAverageZ = (shot.Z + shot.behindZ) * .5;
        if (
        ((U.distance(shotAverageY, vehicle.Y, shotAverageZ, vehicle.Z) < collideAt || U.distance(shot.behindY, vehicle.Y, shot.behindZ, vehicle.Z) < collideAt) && ((shot.X > vehicle.X && shot.behindX < vehicle.X) || (shot.X < vehicle.X && shot.behindX > vehicle.X))) ||//<-inBoundsX
        ((U.distance(shotAverageX, vehicle.X, shotAverageY, vehicle.Y) < collideAt || U.distance(shot.behindX, vehicle.X, shot.behindY, vehicle.Y) < collideAt) && ((shot.Z > vehicle.Z && shot.behindZ < vehicle.Z) || (shot.Z < vehicle.Z && shot.behindZ > vehicle.Z))) ||//<-inBoundsZ
        ((U.distance(shotAverageX, vehicle.X, shotAverageZ, vehicle.Z) < collideAt || U.distance(shot.behindX, vehicle.X, shot.behindZ, vehicle.Z) < collideAt) && ((shot.Y > vehicle.Y && shot.behindY < vehicle.Y) || (shot.Y < vehicle.Y && shot.behindY > vehicle.Y))) ||//<-inBoundsY
        U.distance(shot, vehicle) < diameter + vehicle.collisionRadius) {
         hitCheck(vehicle);
         double shotDamage = special.damageDealt;
         if (special.type == Special.Type.raygun || special.type == Special.Type.flamethrower || special.type == Special.Type.thewrath || special.type.name().contains(Special.Type.blaster.name())) {
          shotDamage /= special.type == Special.Type.flamethrower ? Math.max(1, shot.stage) : 1;
          shotDamage *= VE.tick;
         } else if (special.type != Special.Type.forcefield) {
          shot.hit = 1;
         }
         vehicle.addDamage(shotDamage);
         if (vehicle.isIntegral() && !replay) {
          VE.Match.scoreDamage[greenTeam ? 0 : 1] += shotDamage;
          if (vehicle.index != VE.userPlayerIndex && U.distance(vehicle, V) < U.distance(vehicle, VE.vehicles.get(vehicle.AI.target))) {
           vehicle.AI.target = V.index;
          }
         }
         if (special.pushPower > 0) {
          if (vehicle.getsPushed >= 0) {
           vehicle.P.speedX += U.randomPlusMinus(special.pushPower);
           vehicle.P.speedZ += U.randomPlusMinus(special.pushPower);
          }
          if (vehicle.getsLifted >= 0 && (special.type == Special.Type.forcefield || special.type == Special.Type.missile || special.type == Special.Type.mine || U.contains(special.type.name(), Special.Type.shell.name()))) {
           vehicle.P.speedY += U.randomPlusMinus(special.pushPower);
          }
         }
         for (VehiclePart part : vehicle.parts) {
          part.deform();
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
      if (V.isFixed() && VE.bonusHolder < 0 && V.isIntegral()) {
       double bonusX = TE.Bonus.X, bonusY = TE.Bonus.Y, bonusZ = TE.Bonus.Z, radius = special.diameter + TE.Bonus.big.getRadius();
       for (Shot shot : special.shots) {
        if (shot.stage > 0) {
         double shotAverageX = (shot.X + shot.behindX) * .5, shotAverageY = (shot.Y + shot.behindY) * .5, shotAverageZ = (shot.Z + shot.behindZ) * .5;
         if (
         ((U.distance(shotAverageY, bonusY, shotAverageZ, bonusZ) < radius || U.distance(shot.behindY, bonusY, shot.behindZ, bonusZ) < radius) && ((shot.X > bonusX && shot.behindX < bonusX) || (shot.X < bonusX && shot.behindX > bonusX))) ||//<-inBoundsX
         ((U.distance(shotAverageX, bonusX, shotAverageY, bonusY) < radius || U.distance(shot.behindX, bonusX, shot.behindY, bonusY) < radius) && ((shot.Z > bonusZ && shot.behindZ < bonusZ) || (shot.Z < bonusZ && shot.behindZ > bonusZ))) ||//<-inBoundsZ
         ((U.distance(shotAverageX, bonusX, shotAverageZ, bonusZ) < radius || U.distance(shot.behindX, bonusX, shot.behindZ, bonusZ) < radius) && ((shot.Y > bonusY && shot.behindY < bonusY) || (shot.Y < bonusY && shot.behindY > bonusY))) ||//<-inBoundsY
         U.distance(shot.X, bonusX, shot.Y, bonusY, shot.Z, bonusZ) < radius) {
          TE.Bonus.setHolder(V);
         }
        }
       }
      }
      if (special.homing) {
       for (Shot shot : special.shots) {
        if (shot.stage > 0) {
         int shotTarget = VE.userPlayerIndex;
         double compareDistance = Double.POSITIVE_INFINITY;
         for (Vehicle otherVehicle : VE.vehicles) {
          if (!U.sameTeam(V, otherVehicle) && !otherVehicle.destroyed && U.distance(shot, otherVehicle) < compareDistance) {
           shotTarget = otherVehicle.index;
           compareDistance = U.distance(shot, otherVehicle);
          }
         }
         shot.homeXZ = (VE.vehicles.get(shotTarget).X < shot.X ? 90 : VE.vehicles.get(shotTarget).X > shot.X ? -90 : 0) + U.arcTan((VE.vehicles.get(shotTarget).Z - shot.Z) / (VE.vehicles.get(shotTarget).X - shot.X));
         while (Math.abs(shot.XZ - shot.homeXZ) > 180) {
          shot.homeXZ += shot.homeXZ < shot.XZ ? 360 : -360;
         }
         shot.XZ -= shot.XZ > shot.homeXZ ? 10 * VE.tick : 0;
         shot.XZ += shot.XZ < shot.homeXZ ? 10 * VE.tick : 0;
         double distance = U.netValue(VE.vehicles.get(shotTarget).Z - shot.Z, VE.vehicles.get(shotTarget).X - shot.X);
         shot.homeYZ = VE.vehicles.get(shotTarget).Y < shot.Y ? -(-90 - U.arcTan(distance / (VE.vehicles.get(shotTarget).Y - shot.Y))) : VE.vehicles.get(shotTarget).Y > shot.Y ? -(90 - U.arcTan(distance / (VE.vehicles.get(shotTarget).Y - shot.Y))) : shot.homeYZ;
         shot.YZ -= shot.homeYZ < shot.YZ ? 10 * VE.tick : 0;
         shot.YZ += shot.homeYZ > shot.YZ || shot.Y > -special.diameter * .5 - (VE.vehicles.get(shotTarget).isFixed() ? VE.vehicles.get(shotTarget).turretBaseY : VE.vehicles.get(shotTarget).clearanceY) ? 10 * VE.tick : 0;
        }
       }
      }
      if (wrathEngaged && (U.distance(V, vehicle) < V.absoluteRadius + netSpeed || wrathStuck[vehicle.index])) {
       if (vehicle.getsPushed >= 0) {
        vehicle.X = V.X;
        vehicle.Y = V.Y;
        vehicle.Z = V.Z;
        vehicle.P.speedX = speedX;
        vehicle.P.speedY = speedY;
        vehicle.P.speedZ = speedZ;
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
    boolean nuclear = V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name());
    explosionDiameter = nuclear ? U.random(20000.) : explosionDiameter;
    explosionDamage = nuclear ? 2500 + U.random(5000.) : explosionDamage;
    for (Vehicle vehicle : VE.vehicles) {
     if (!U.sameTeam(V, vehicle) && !vehicle.destroyed && !vehicle.reviveImmortality && !vehicle.phantomEngaged) {
      for (Explosion explosion : V.explosions) {
       if (explosion.stage > 0 && U.distance(explosion, vehicle) < vehicle.collisionRadius + explosionDiameter) {
        if (!explosion.doneDamaging[vehicle.index]) {
         hitCheck(vehicle);
         vehicle.addDamage(explosionDamage);
         if (!nuclear) {
          if (vehicle.isIntegral() && !replay) {
           VE.Match.scoreDamage[greenTeam ? 0 : 1] += explosionDamage;
          }
          explosion.doneDamaging[vehicle.index] = true;
         }
        }
        if (vehicle.getsPushed >= 0) {
         vehicle.P.speedX += U.randomPlusMinus(explosionPush);
         vehicle.P.speedZ += U.randomPlusMinus(explosionPush);
        }
        if (vehicle.getsLifted >= 0) {
         vehicle.P.speedY += U.randomPlusMinus(explosionPush);
        }
        for (VehiclePart part : vehicle.parts) {
         part.deform();
         part.throwChip(300);
        }
        if (nuclear) {
         V.VA.crashDestroy.play(Double.NaN, V.VA.distanceVehicleToCamera);
        }
       }
      }
     }
    }
   }
   if (!V.destroyed) {
    if (V.isFixed()) {
     for (Vehicle vehicle : VE.vehicles) {
      if (!U.sameTeam(V, vehicle) && !vehicle.destroyed && !vehicle.reviveImmortality && U.distance(V.X, vehicle.X, V.Y + (V.turretBaseY * .5), vehicle.Y, V.Z, vehicle.Z) < V.collisionRadius * .5 + vehicle.collisionRadius && !vehicle.phantomEngaged) {
       hitCheck(vehicle);
       if (vehicle.fragility > 0) {
        vehicle.addDamage(V.structureBaseDamageDealt * vehicle.fragility);
        VE.Match.scoreDamage[greenTeam ? 0 : 1] += replay ? 0 : V.structureBaseDamageDealt * vehicle.fragility;
       }
       vehicle.P.speedX += U.randomPlusMinus(500.);
       vehicle.P.speedZ += U.randomPlusMinus(500.);
       vehicle.P.speedY += U.randomPlusMinus(200.);
       for (VehiclePart part : vehicle.parts) {
        if (vehicle.fragility > 0) {
         part.deform();
        }
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
       for (Vehicle vehicle : VE.vehicles) {
        if ((disintegrate ? !U.sameTeam(V, vehicle) : !U.sameVehicle(V, vehicle) && U.sameTeam(V, vehicle)) &&
        !vehicle.destroyed && !vehicle.reviveImmortality && !vehicle.phantomEngaged) {
         if (
         ((vehicle.Y <= V.Y && U.sin(V.YZ) >= 0) || (vehicle.Y >= V.Y && U.sin(V.YZ) <= 0) || Math.abs(vehicle.Y - V.Y) < vehicle.collisionRadius) &&//<-inY
         ((vehicle.X <= V.X && U.sin(V.XZ) >= 0) || (vehicle.X >= V.X && U.sin(V.XZ) <= 0)) &&//<-inX
         ((vehicle.Z <= V.Z && U.cos(V.XZ) <= 0) || (vehicle.Z >= V.Z && U.cos(V.XZ) >= 0)))/*<-inZ*/ {
          if (disintegrate) {
           for (VehiclePart part : vehicle.parts) {
            part.deform();
           }
           hitCheck(vehicle);
           VE.Match.scoreDamage[greenTeam ? 0 : 1] += replay ? 0 : 10 * VE.tick;
          }
          if (disintegrate || vehicle.isIntegral()) {//<-Reintegration was sometimes making vehicles non-destroyable
           vehicle.addDamage((disintegrate ? 10 : -10) * V.energyMultiple * VE.tick);
          }
         }
        }
       }
      }
     }
     if (E.Storm.Lightning.exists && E.Storm.Lightning.strikeStage < 1) {
      double distance = U.distance(V.X, E.Storm.Lightning.X, V.Z, E.Storm.Lightning.Z);
      if (V.Y >= E.Storm.stormCloudY && distance < V.collisionRadius * 6) {
       V.addDamage(V.durability * .5 + (distance < V.collisionRadius * 2 ? V.durability : 0));
       for (VehiclePart part : V.parts) {
        part.deform();
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
       V.addDamage(10 * VE.tick);
       if (distance * 2 < V.collisionRadius + fire.absoluteRadius) {
        V.addDamage(10 * VE.tick);
       }
       for (VehiclePart part : V.parts) {
        part.deform();
       }
      }
     }
     for (Boulder.Instance boulder : Boulder.instances) {
      if (U.distanceXZ(V, boulder) < V.collisionRadius + boulder.S.getRadius() && V.Y > boulder.Y - V.collisionRadius - boulder.S.getRadius()) {//Will call incorrectly if a vehicle is underground and it rolls overhead
       V.setDamage(V.damageCeiling());
       for (VehiclePart part : V.parts) {
        part.deform();
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
    String s = Network.mode == Network.Mode.OFF ? V.name : VE.playerNames[V.index];
    V.death = Vehicle.Death.diedAlone;
    for (Vehicle vehicle : VE.vehicles) {
     if (!U.sameTeam(V, vehicle) && vehicleHit == vehicle.index && vehicle.P.vehicleHit == V.index) {
      V.death = Vehicle.Death.killedByAnother;
      VE.DestructionLog.update();
      String s1 = Network.mode == Network.Mode.OFF ? vehicle.name : VE.playerNames[vehicle.index];
      VE.DestructionLog.names[4][0] = s1;
      VE.DestructionLog.names[4][1] = s;
      VE.DestructionLog.nameColors[4][0] = vehicle.index < VE.vehiclesInMatch >> 1 ? Color.color(0, 1, 0) : Color.color(1, 0, 0);
      VE.DestructionLog.nameColors[4][1] = greenTeam ? Color.color(0, 1, 0) : Color.color(1, 0, 0);
     }
    }
    if (V.death == Vehicle.Death.diedAlone) {
     VE.DestructionLog.update();
     VE.DestructionLog.names[4][0] = s;
     VE.DestructionLog.names[4][1] = "";
     VE.DestructionLog.nameColors[4][0] = greenTeam ? Color.color(0, 1, 0) : Color.color(1, 0, 0);
    }
   }
  }
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
  atPoolXZ = E.Pool.exists && U.distance(V.X, E.Pool.X, V.Z, E.Pool.Z) < E.Pool.C[0].getRadius();
  runVehiclesAircraft();
  runTurretsInfrastructure();
  if (V.explosionsWhenDestroyed > 0 && !V.isIntegral() && !V.destroyed) {
   for (n = (int) V.explosionsWhenDestroyed; --n >= 0; ) {
    V.explosions.get(V.currentExplosion).deploy(U.randomPlusMinus(V.absoluteRadius), U.randomPlusMinus(V.absoluteRadius), U.randomPlusMinus(V.absoluteRadius), V);
    V.currentExplosion = ++V.currentExplosion >= E.explosionQuantity ? 0 : V.currentExplosion;
   }
   V.setCameraShake(Camera.shakePresets.vehicleExplode);
   V.VA.explode.play(Double.NaN, V.VA.distanceVehicleToCamera);
   //VA.crashDestroy.play(Double.NaN, VA.distanceVehicleToCamera);<-For turrets...maybe?
  }
  V.X = U.clamp(E.mapBounds.left, V.X, E.mapBounds.right);
  V.Z = U.clamp(E.mapBounds.backward, V.Z, E.mapBounds.forward);
  V.Y = U.clamp(E.mapBounds.Y, V.Y, -E.mapBounds.Y);
 }

 private void runVehiclesAircraft() {
  if (!V.isFixed()) {
   boolean replay = VE.status == VE.Status.replay;
   netSpeed = getNetSpeed();
   polarity = Math.abs(V.YZ) > 90 ? -1 : 1;
   flipped = (Math.abs(V.XY) > 90 && Math.abs(V.YZ) <= 90) || (Math.abs(V.YZ) > 90 && Math.abs(V.XY) <= 90);
   double clearance = flipped && !V.landStuntsBothSides ? -V.absoluteRadius * .075 : V.clearanceY;
   inPool = atPoolXZ && V.Y + clearance > 0;
   boolean onBouncy = U.contains(terrainProperties, SL.Thicks.bounce, " maxbounce ");
   if (mode.name().startsWith(SL.drive)) {
    stuntSpeedYZ = stuntSpeedXY = stuntSpeedXZ = 0;
   }
   if (!V.destroyed) {
    runAirEngage();
    if (mode == Mode.stunt) {
     runAerialControl();
    } else {
     runDrivePower();
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
    if (!flipped) {
     airSpinXZ = fromAngleToVelocityConstant * speedXZ * turnSpeed;
     V.XZ += speedXZ * turnSpeed * fromAngleToVelocityConstant * VE.tick;
    }
   } else if (mode != Mode.fly) {//airSpinXZ gets zeroed in runAerialControl() if aerial control is enhanced
    V.XZ += airSpinXZ * VE.tick;
    stuntXZ += airSpinXZ * VE.tick;
   }
   runFlight(maxTurn, turnSpeed);
   if (!V.phantomEngaged && !inTornado && !V.floats) {
    speedY += E.gravity * (V.amphibious && inPool && V.Y > 0 ? -1 : 1) * VE.tick;
   }//There IS better ground traction if the gravity is applied before setting wheel XYZ
   runSetWheelXYZ(clearance);
   if (mode.name().startsWith(SL.drive) || V.phantomEngaged) {
    runDriveVelocity();
    runSkidsAndDust();
    runBouncyTerrain(onBouncy);
    mode = Mode.neutral;
   }
   driftXZ = V.XZ;//<-Best spot for assigning this
   runSpeedBoost();
   boolean crashLand = (Math.abs(V.YZ) > 30 || Math.abs(V.XY) > 30) && !(Math.abs(V.YZ) > 150 && Math.abs(V.XY) > 150);
   double gravityCompensation = E.gravity * 2 * VE.tick;
   for (Wheel wheel : V.wheels) {//<-Best place for this?
    wheel.againstWall = wheel.angledSurface = false;
   }
   if (!V.phantomEngaged) {
    double bounceBackForce = flipped ? 1 : Math.abs(U.sin(V.XY)) + Math.abs(U.sin(V.YZ)),
    flatPlaneBounce = Math.min(Math.abs(U.sin(V.XY)) + Math.abs(U.sin(V.YZ)), 1);
    runGroundConnect(crashLand, bounceBackForce, flatPlaneBounce);
    runPoolInteract();//<-AFTER ground connect for correct mode!
    if (Volcano.exists) {
     double volcanoDistance = U.distance(V.X, Volcano.X, V.Z, Volcano.Z);
     onVolcano = volcanoDistance < Volcano.radiusBottom && volcanoDistance > Volcano.radiusTop && V.Y > -Volcano.radiusBottom + volcanoDistance;
    } else {
     onVolcano = false;
    }
    terrainProperties = E.Terrain.vehicleDefaultTerrain;
    setTerrainFromTrackPlanes(clearance, gravityCompensation);//<-terrainProperties set here
    runTrackPlaneInteraction(crashLand, bounceBackForce, flatPlaneBounce, gravityCompensation);
    if (onVolcano) {
     mode = Mode.driveSolid;
    }
    //runCollisionSpin();//<-Replace with a new spin system?
   }
   runAngleSet();
   Tornado.vehicleInteract(V);
   runHitOther();
   if (mode == Mode.driveSolid && !onBouncy) {
    V.Y = (V.wheels.get(0).Y + V.wheels.get(1).Y + V.wheels.get(2).Y + V.wheels.get(3).Y) * .25 - (clearance * U.cos(V.YZ) * U.cos(V.XY));//U.cos's correct upside-down landings
   } else {
    V.Y += speedY * V.energyMultiple * VE.tick;
   }
   V.X += speedX * V.energyMultiple * VE.tick;
   V.Z += speedZ * V.energyMultiple * VE.tick;
   localGround = E.Ground.level + (atPoolXZ ? E.Pool.depth : 0);
   V.runMoundInteract(clearance, gravityCompensation);//<-localVehicleGround is set here
   if (onVolcano) {
    double baseAngle = flipped ? 225 : 45, vehicleVolcanoXZ = V.XZ, VolcanoPlaneY = Math.max(Volcano.radiusTop * .5, (Volcano.radiusBottom * .5) - ((Volcano.radiusBottom * .5) * (Math.abs(V.Y) / Volcano.height)));
    vehicleVolcanoXZ += V.Z < Volcano.Z && Math.abs(V.X - Volcano.X) < VolcanoPlaneY ? 180 : V.X >= Volcano.X + VolcanoPlaneY ? 90 : V.X <= Volcano.X - VolcanoPlaneY ? -90 : 0;
    V.XY += (baseAngle * U.sin(vehicleVolcanoXZ) - V.XY) * valueAdjustSmoothing * VE.tick;
    V.YZ += (-baseAngle * U.cos(vehicleVolcanoXZ) - V.YZ) * valueAdjustSmoothing * VE.tick;
    V.Y = Math.min(V.Y, -Volcano.radiusBottom + U.distance(V.X, Volcano.X, V.Z, Volcano.Z));
    speedY = Math.min(speedY, 0);
   }
   runLandInvert();
   runSolidDriveEffects();
   if (V.engine == Vehicle.Engine.hotrod && !V.destroyed && U.random() < .5) {
    V.XY += U.random() < .5 ? 1 : -1;
   }
   TE.runVehicleInteraction(V, replay);
   V.runStuntScoring(replay);
   runSpeedConstraints();
   E.mapBounds.slowVehicle(V);
   if ((mode == Mode.stunt || mode == Mode.fly) && V.destroyed) {
    mode = Mode.neutral;
   }
   while (Math.abs(V.XZ - cameraXZ) > 180) {
    cameraXZ += cameraXZ < V.XZ ? 360 : -360;
   }
   cameraXZ += (V.XZ - cameraXZ) * .3 * StrictMath.pow(VE.tick, .8);
  }
 }

 private void runTurretsInfrastructure() {
  if (V.isFixed()) {
   inPool = atPoolXZ && V.Y > 0;
   polarity = 1;
   double randomTurnKick = U.random(V.randomTurnKick);
   if (V.steerByMouse && V.turnRate >= Double.POSITIVE_INFINITY) {
    speedXZ = U.clamp(-V.maxTurn - randomTurnKick, VE.Mouse.steerX, V.maxTurn + randomTurnKick);
   } else {
    if ((V.turnR && !V.turnL) || (V.steerByMouse && speedXZ > VE.Mouse.steerX)) {
     speedXZ -= (speedXZ > 0 ? 2 : 1) * V.turnRate * VE.tick;
     speedXZ = Math.max(speedXZ, -V.maxTurn);
    }
    if ((V.turnL && !V.turnR) || (V.steerByMouse && speedXZ < VE.Mouse.steerX)) {
     speedXZ += (speedXZ < 0 ? 2 : 1) * V.turnRate * VE.tick;
     speedXZ = Math.min(speedXZ, V.maxTurn);
    }
    if (speedXZ != 0 && !V.turnL && !V.turnR && !V.steerByMouse) {
     if (Math.abs(speedXZ) < V.turnRate * 2 * VE.tick) {
      speedXZ = 0;
     } else {
      speedXZ += (speedXZ < 0 ? 1 : speedXZ > 0 ? -1 : 0) * V.turnRate * 2 * VE.tick;
     }
    }
   }
   if (V.drive || (V.steerByMouse && speedYZ > VE.Mouse.steerY)) {
    speedYZ -= (speedYZ > 0 ? 2 : 1) * V.turnRate * VE.tick;
    speedYZ = Math.max(speedYZ, -V.maxTurn);
   }
   if (V.reverse || (V.steerByMouse && speedYZ < VE.Mouse.steerY)) {
    speedYZ += (speedYZ < 0 ? 2 : 1) * V.turnRate * VE.tick;
    speedYZ = Math.min(speedYZ, V.maxTurn);
   }
   if (speedYZ != 0 && !V.drive && !V.reverse && !V.steerByMouse) {
    if (Math.abs(speedYZ) < V.turnRate * 2 * VE.tick) {
     speedYZ = 0;
    } else {
     speedYZ += (speedYZ < 0 ? 1 : speedYZ > 0 ? -1 : 0) * V.turnRate * 2 * VE.tick;
    }
   }
   double sharpShoot = fromAngleToVelocityConstant * (V.handbrake ? .1 : 1);
   V.XZ += speedXZ * sharpShoot * VE.tick;
   V.YZ += speedYZ * sharpShoot * VE.tick;
   for (Wheel wheel : V.wheels) {
    wheel.X = V.X;
    wheel.Y = V.Y + (V.turretBaseY * .5);
    wheel.Z = V.Z;
    wheel.againstWall = false;
   }
   speedX = speedZ = speedY = 0;
   //speedY += V.isIntegral() ? -speedY : E.gravity * VE.tick;//todo
   localGround = V.Y + V.turretBaseY;//<-Confirmed correct
   V.YZ = U.clamp(-90, V.YZ, 90);
   V.XY = speed = 0;
   flipped = false;
  }
 }

 private void runDrivePower() {
  boolean aircraft = V.type == Vehicle.Type.aircraft, flying = mode == Mode.fly;
  boolean driveGet = !flying && aircraft ? V.drive || V.drive2 : flying ? V.drive2 : V.drive,
  reverseGet = !flying && aircraft ? V.reverse || V.reverse2 : flying ? V.reverse2 : V.reverse;
  if (reverseGet) {
   speed -= speed > 0 && V.engine != Vehicle.Engine.hotrod ? V.brake * .5 * VE.tick : speed > -V.topSpeeds[0] ? V.accelerationStages[0] * VE.tick : 0;
  }
  if (driveGet) {
   if (speed < 0 && V.engine != Vehicle.Engine.hotrod) {
    speed += V.brake * VE.tick;
   } else {
    int u = 0;
    for (int n = 2; --n >= 0; ) {
     u += speed >= V.topSpeeds[n] ? 1 : 0;
    }
    speed += u < 2 ? V.accelerationStages[u] * VE.tick : 0;
   }
  }
  if (!flying && V.handbrake && speed != 0) {
   if (speed < V.brake * VE.tick && speed > -V.brake * VE.tick) {
    speed = 0;
   } else {
    speed += (speed < 0 ? 1 : speed > 0 ? -1 : 0) * V.brake * VE.tick;
   }
  }
 }

 private void runSteering(double turnAmount) {
  if (V.steerByMouse && V.turnRate >= Double.POSITIVE_INFINITY) {
   speedXZ = U.clamp(-turnAmount, VE.Mouse.steerX, turnAmount);
  } else {
   if ((V.turnR && !V.turnL) || (V.steerByMouse && speedXZ > VE.Mouse.steerX)) {
    speedXZ -= (speedXZ > 0 ? 2 : 1) * V.turnRate * VE.tick;
    speedXZ = Math.max(speedXZ, -turnAmount);
   }
   if ((V.turnL && !V.turnR) || (V.steerByMouse && speedXZ < VE.Mouse.steerX)) {
    speedXZ += (speedXZ < 0 ? 2 : 1) * V.turnRate * VE.tick;
    speedXZ = Math.min(speedXZ, turnAmount);
   }
   if (speedXZ != 0 && !V.turnL && !V.turnR && !V.steerByMouse) {
    if (Math.abs(speedXZ) < V.turnRate * 2 * VE.tick) {
     speedXZ = 0;
    } else {
     speedXZ += (speedXZ < 0 ? V.turnRate : speedXZ > 0 ? -V.turnRate : 0) * 2 * VE.tick;
    }
   }
  }
  if (mode == Mode.fly) {
   if (V.drive || (V.steerByMouse && speedYZ > VE.Mouse.steerY)) {
    speedYZ -= (speedYZ > 0 ? 2 : 1) * V.turnRate * VE.tick;
    speedYZ = Math.max(speedYZ, -V.maxTurn);
   }
   if (V.reverse || (V.steerByMouse && speedYZ < VE.Mouse.steerY)) {
    speedYZ += (speedYZ < 0 ? 2 : 1) * V.turnRate * VE.tick;
    speedYZ = Math.min(speedYZ, V.maxTurn);
   }
  }
  if (speedYZ != 0 && (mode != Mode.fly || (!V.drive && !V.reverse && !V.steerByMouse))) {
   if (Math.abs(speedYZ) < V.turnRate * 2 * VE.tick) {
    speedYZ = 0;
   } else {
    speedYZ += (speedYZ < 0 ? V.turnRate : speedYZ > 0 ? -V.turnRate : 0) * 2 * VE.tick;
   }
  }
 }

 private void runSolidDriveEffects() {
  boolean shockAbsorb = !Double.isNaN(V.shockAbsorb);
  if (shockAbsorb) {
   for (Wheel wheel : V.wheels) {
    wheel.vibrateY -= wheel.vibrateY * valueAdjustSmoothing * VE.tick;
   }
  }
  if (V.isIntegral() && mode == Mode.driveSolid && V.bounce > 0) {
   if (V.type == Vehicle.Type.vehicle && !flipped) {
    double lean = speed * V.clearanceY * V.bounce * speedXZ * .0000133 * (speed < 0 ? -1 : 1) * (Math.abs(V.XY) > 10 ? .5 : 1);
    V.XY += lean;
    if (shockAbsorb) {
     lean *= .25;
     for (Wheel wheel : V.wheels) {
      wheel.vibrateY -= wheel.pointX * U.sin(lean) * V.shockAbsorb;
     }
    }
   }
   boolean rockTerrain = terrainProperties.contains(SL.Thicks.rock);
   if (rockTerrain || terrainProperties.contains(SL.Thicks.ground)) {
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
     double vibrate = terrainProperties.contains(SL.Thicks.rock) ? .0003 : .00015;
     V.YZ += U.clamp(-180 - U.random(180.), U.randomPlusMinus(vibrate) * netSpeed * V.clearanceY * V.bounce, 180 + U.random(180.));
     V.XY += U.clamp(-180 - U.random(180.), U.randomPlusMinus(vibrate) * netSpeed * V.clearanceY * V.bounce, 180 + U.random(180.));
    }
   }
  }
 }

 private void runGroundConnect(boolean crashLand, double bounceBackForce, double flatPlaneBounce) {
  double hitsGroundY = -5;
  if (onAntiGravity) {
   hitsGroundY = Double.POSITIVE_INFINITY;//<-Should eliminate '-5' traction-lock that would prevent vehicles from rising
   onAntiGravity = false;
  }
  boolean connected = false;
  for (Wheel wheel : V.wheels) {
   if (wheel.Y > hitsGroundY + localGround) {
    mode = Mode.driveSolid;
    connected = true;
    wheel.Y = localGround;
    wheel.XY -= wheel.XY * valueAdjustSmoothing * VE.tick;
    wheel.YZ -= wheel.YZ * valueAdjustSmoothing * VE.tick;
    if (flipped && terrainProperties.contains(SL.Thicks.hard)) {
     wheel.sparks(true);
    }
   }
  }
  if (connected) {
   if (crashLand) {
    crash(speedY * bounceBackForce, false);
   }
   V.terrainRGB = E.Ground.RGB;
   if (speedY > RichHit.minimumSpeed) {
    V.VA.land();
    for (long n = RichHit.dustQuantity; --n >= 0; ) {
     V.deployDust(true);
    }
   }
   if (speedY > 0) {
    speedY *= V.destroyed ? 0 : -V.bounce * flatPlaneBounce;
   }
   if (V.spinner != null && !((Math.abs(V.YZ) < 10 && Math.abs(V.XY) < 10) || (Math.abs(V.YZ) > 170 && Math.abs(V.XY) > 170))) {
    V.spinner.hit(null);
   }
  }
 }

 private void runWheelSpin() {
  double wheelSpun = U.clamp(-44 / VE.tick, 267 * Math.sqrt(Math.abs(StrictMath.pow(speed, 2) * 1.333)) / V.absoluteRadius, 44 / VE.tick),
  amount = speed < 0 ? -1 : 1;
  if (Math.abs(amount * wheelSpun * VE.tick) > 25) {
   double randomAngle = U.randomPlusMinus(360.);
   wheelSpin[0] = randomAngle;
   wheelSpin[1] = randomAngle;
  } else {
   wheelSpin[0] += amount * wheelSpun * VE.tick;
   wheelSpin[1] += amount * wheelSpun * VE.tick;
   if (V.steerInPlace) {
    double steerSpin = 667 * speedXZ / V.absoluteRadius;//FIX
    wheelSpin[0] += amount * steerSpin * VE.tick;
    wheelSpin[1] -= amount * steerSpin * VE.tick;
   }
   wheelSpin[0] = Math.abs(wheelSpin[0]) > 360 ? 0 : wheelSpin[0];
   wheelSpin[1] = Math.abs(wheelSpin[1]) > 360 ? 0 : wheelSpin[1];
  }
 }

 private void runDriveVelocity() {
  double drift = V.handbrake && !flipped && !U.equals(VE.Map.name, SL.MN.theMaze, SL.MN.XYLand) ? V.grip * .25 * Math.abs(driftXZ - V.XZ) : 0,
  setGrip = V.grip - drift;
  setGrip *= (terrainProperties.contains(SL.Thicks.ice) ? .075 : terrainProperties.contains(SL.Thicks.ground) ? .75 : 1) * (flipped ? .2 : 1);
  setGrip = Math.max(setGrip * VE.tick, 0);
  double cosYZ = U.cos(V.YZ),
  velocityX = -(speed * U.sin(V.XZ) * cosYZ),
  velocityZ = speed * U.cos(V.XZ) * cosYZ,
  velocityY = -(speed * U.sin(V.YZ));
  if (flipped) {
   speedX -= speedX > setGrip ? setGrip : Math.max(speedX, -setGrip);
   speedZ -= speedZ > setGrip ? setGrip : Math.max(speedZ, -setGrip);
  } else {
   if (Math.abs(speedX - velocityX) > setGrip) {
    speedX += setGrip * Double.compare(velocityX, speedX);
   } else {
    speedX = velocityX;
   }
   if (Math.abs(speedZ - velocityZ) > setGrip) {
    speedZ += setGrip * Double.compare(velocityZ, speedZ);
   } else {
    speedZ = velocityZ;
   }
   if (mode == Mode.driveSolid || V.phantomEngaged) {
    if (Math.abs(speedY - velocityY) > setGrip) {
     speedY += setGrip * Double.compare(velocityY, speedY);
    } else {
     speedY = velocityY;
    }
   }
  }
 }

 private void runSkidsAndDust() {
  if (mode == Mode.driveSolid) {
   int n;
   boolean markedSnow = false;
   if (terrainProperties.contains(SL.Thicks.snow)) {
    for (Wheel wheel : V.wheels) {
     wheel.skidmark(true);
    }
    markedSnow = true;
   }
   boolean kineticFriction = Math.abs(Math.abs(speed) - netSpeed) > 15,
   driveEngine = !U.containsEnum(V.engine, Vehicle.Engine.prop, Vehicle.Engine.jet, Vehicle.Engine.rocket);
   if (((driveEngine && kineticFriction) || StrictMath.pow(speedXZ, 2) > 300000 / netSpeed) && (kineticFriction || Math.abs(speed) > V.topSpeeds[1] * .9)) {
    if (terrainProperties.contains(SL.Thicks.hard) && V.contact == Contact.metal) {
     for (Wheel wheel : V.wheels) {
      wheel.sparks(true);
     }
    }
    if (V.contact == Contact.rubber || !terrainProperties.contains(SL.Thicks.hard)) {
     for (n = 4; --n >= 0; ) {
      V.deployDust(false);
     }
    }
    if (!terrainProperties.contains(SL.Thicks.ice)) {
     if (!markedSnow) {
      for (Wheel wheel : V.wheels) {
       wheel.skidmark(false);
      }
     }
     V.VA.skid();
    }
   } else if (terrainProperties.contains(SL.Thicks.snow)) {
    for (n = 4; --n >= 0; ) {
     if (U.random() < .4) {
      V.deployDust(false);
     }
    }
   } else if (terrainProperties.contains(SL.Thicks.ground)) {
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
   airSpinXZ = 0;//TEST
  }
  if (V.drive) {
   stuntSpeedYZ -= V.airAcceleration < Double.POSITIVE_INFINITY ? V.airAcceleration * VE.tick : 0;
   stuntSpeedYZ = stuntSpeedYZ < -V.airTopSpeed || V.airAcceleration == Double.POSITIVE_INFINITY ? -V.airTopSpeed : stuntSpeedYZ;
  }
  if (V.reverse) {
   stuntSpeedYZ += V.airAcceleration < Double.POSITIVE_INFINITY ? V.airAcceleration * VE.tick : 0;
   stuntSpeedYZ = stuntSpeedYZ > V.airTopSpeed || V.airAcceleration == Double.POSITIVE_INFINITY ? V.airTopSpeed : stuntSpeedYZ;
  }
  if (!V.drive && !V.reverse) {
   if (V.airAcceleration < Double.POSITIVE_INFINITY) {
    stuntSpeedYZ += (stuntSpeedYZ < 0 ? 1 : stuntSpeedYZ > 0 ? -1 : 0) * V.airAcceleration * VE.tick;
   }
   stuntSpeedYZ = Math.abs(stuntSpeedYZ) < V.airAcceleration || V.airAcceleration == Double.POSITIVE_INFINITY ? 0 : stuntSpeedYZ;
  }
  if (!inWall && stuntSpeedYZ < 0) {
   double amount = Math.abs(V.XY) > 90 ? -1 : 1;
   V.X += amount * -V.airPush * U.sin(V.XZ) * -stuntSpeedYZ * VE.tick;
   V.Z += amount * V.airPush * U.cos(V.XZ) * -stuntSpeedYZ * VE.tick;
  }
  V.Y -= stuntSpeedYZ > 0 ? V.airPush * stuntSpeedYZ * VE.tick : 0;
  if ((V.turnL && !V.turnR) || (V.steerByMouse && (V.handbrake ? stuntSpeedXZ : stuntSpeedXY) * -40 < VE.Mouse.steerX)) {
   if (V.handbrake) {
    stuntSpeedXZ -= V.airAcceleration < Double.POSITIVE_INFINITY ? V.airAcceleration * VE.tick : 0;
    stuntSpeedXZ = stuntSpeedXZ < -V.airTopSpeed || V.airAcceleration == Double.POSITIVE_INFINITY ? -V.airTopSpeed : stuntSpeedXZ;
   } else {
    stuntSpeedXY -= V.airAcceleration < Double.POSITIVE_INFINITY ? V.airAcceleration * VE.tick : 0;
    stuntSpeedXY = stuntSpeedXY < -V.airTopSpeed || V.airAcceleration == Double.POSITIVE_INFINITY ? -V.airTopSpeed : stuntSpeedXY;
   }
  }
  if ((V.turnR && !V.turnL) || (V.steerByMouse && (V.handbrake ? stuntSpeedXZ : stuntSpeedXY) * -40 > VE.Mouse.steerX)) {
   if (V.handbrake) {
    stuntSpeedXZ += V.airAcceleration < Double.POSITIVE_INFINITY ? V.airAcceleration * VE.tick : 0;
    stuntSpeedXZ = stuntSpeedXZ > V.airTopSpeed || V.airAcceleration == Double.POSITIVE_INFINITY ? V.airTopSpeed : stuntSpeedXZ;
   } else {
    stuntSpeedXY += V.airAcceleration < Double.POSITIVE_INFINITY ? V.airAcceleration * VE.tick : 0;
    stuntSpeedXY = stuntSpeedXY > V.airTopSpeed || V.airAcceleration == Double.POSITIVE_INFINITY ? V.airTopSpeed : stuntSpeedXY;
   }
  }
  if ((!V.turnL && !V.turnR && !V.steerByMouse) || !V.handbrake) {
   if (V.airAcceleration < Double.POSITIVE_INFINITY) {
    stuntSpeedXZ += (stuntSpeedXZ < 0 ? 1 : stuntSpeedXZ > 0 ? -1 : 0) * V.airAcceleration * VE.tick;
   }
   stuntSpeedXZ = Math.abs(stuntSpeedXZ) < V.airAcceleration || V.airAcceleration == Double.POSITIVE_INFINITY ? 0 : stuntSpeedXZ;
  }
  if ((!V.turnL && !V.turnR && !V.steerByMouse) || V.handbrake) {
   if (V.airAcceleration < Double.POSITIVE_INFINITY) {
    stuntSpeedXY += (stuntSpeedXY < 0 ? 1 : stuntSpeedXY > 0 ? -1 : 0) * V.airAcceleration * VE.tick;
   }
   stuntSpeedXY = Math.abs(stuntSpeedXY) < V.airAcceleration || V.airAcceleration == Double.POSITIVE_INFINITY ? 0 : stuntSpeedXY;
  }
  V.YZ += 20 * stuntSpeedYZ * U.cos(V.XY) * VE.tick;
  V.XZ -= 20 * polarity * stuntSpeedYZ * U.sin(V.XY) * VE.tick;
  V.XZ -= stuntSpeedXZ * 20 * polarity * VE.tick;
  V.XY += 20 * stuntSpeedXY * VE.tick;
  if (!inWall) {
   V.X += V.airPush * U.cos(V.XZ) * polarity * stuntSpeedXY * VE.tick;
   V.Z += V.airPush * U.sin(V.XZ) * polarity * stuntSpeedXY * VE.tick;
  }
 }

 private void runFlight(double turnAmount, double turnSpeed) {
  if (mode == Mode.fly) {
   if (V.handbrake) {
    V.XZ += speedXZ * turnSpeed * fromAngleToVelocityConstant * polarity * VE.tick;
    if (Math.abs(V.XY) < turnAmount * fromAngleToVelocityConstant * VE.tick) {
     V.XY = 0;
    } else {
     V.XY += (V.XY < 0 ? 1 : V.XY > 0 ? -1 : 0) * turnAmount * fromAngleToVelocityConstant * VE.tick;
    }
   } else {
    V.XY -= speedXZ * .27 * VE.tick;
    stuntXY -= speedXZ * .27 * VE.tick;
   }
   V.YZ += speedYZ * .135 * U.cos(V.XY) * VE.tick;
   stuntYZ -= speedYZ * .135 * U.cos(V.XY) * VE.tick;
   if (!V.handbrake && V.engine != Vehicle.Engine.powerjet) {
    V.XZ -= speedYZ * .135 * U.sin(V.XY) * polarity * VE.tick;
    stuntXZ -= speedYZ * .135 * U.sin(V.XY) * polarity * VE.tick;
   }
   double amount = (speed < 0 ? -5 : 5) * U.sin(V.XY) * polarity * VE.tick;
   V.XZ -= amount;
   stuntXZ -= amount;
   speedX = -speed * U.sin(V.XZ) * U.cos(V.YZ);
   speedZ = speed * U.cos(V.XZ) * U.cos(V.YZ);
   speedY = -speed * U.sin(V.YZ) + stallSpeed;
   if (E.gravity == 0 || onAntiGravity || V.floats) {
    stallSpeed = 0;
   } else {
    stallSpeed += E.gravity * VE.tick;
    if (Math.abs(speed) > 0 && stallSpeed > 0) {
     stallSpeed -= Math.abs(speed) * VE.tick * (V.engine == Vehicle.Engine.smallprop ? .04 : .02);
    }
    stallSpeed *= inPool ? Math.min(.95 * VE.tick, 1) : 1;
   }
  } else {
   stallSpeed = speedY;
  }
 }

 private void runSpeedBoost() {
  if (V.boost && V.speedBoost > 0 && !V.destroyed) {
   if (Math.abs(speedX) < V.topSpeeds[2]) {
    speedX -= V.speedBoost * U.sin(V.XZ) * polarity * VE.tick;
   } else {
    speedX *= .999;
   }
   if (Math.abs(speedZ) < V.topSpeeds[2]) {
    speedZ += V.speedBoost * U.cos(V.XZ) * polarity * VE.tick;
   } else {
    speedZ *= .999;
   }
   if (Math.abs(speedY) < V.topSpeeds[2] || (E.gravity > 0 && speedY > 0)) {
    speedY -= V.speedBoost * U.sin(V.YZ) * VE.tick;
   } else {
    speedY *= .999;
   }
   if (V.grip <= highGrip) {
    speed += V.speedBoost * VE.tick;
   }
  }
 }

 private void runPoolInteract() {
  if (inPool) {
   if (netSpeed > 0) {
    for (int n = 3; --n >= 0; ) {
     for (Wheel wheel : V.wheels) {
      V.splashes.get(V.currentSplash).deploy(wheel, V.absoluteRadius * .0125 + U.random(V.absoluteRadius * .0125),
      speedX + U.randomPlusMinus(Math.max(speed, netSpeed)),
      speedY + U.randomPlusMinus(Math.max(speed, netSpeed)),
      speedZ + U.randomPlusMinus(Math.max(speed, netSpeed)));
      V.currentSplash = ++V.currentSplash >= E.splashQuantity ? 0 : V.currentSplash;
     }
    }
   }
   V.VA.splashing = netSpeed;
   if (!V.reviveImmortality) {
    if (E.Pool.type == E.Pool.Type.lava) {
     V.addDamage(30 * VE.tick);
     for (VehiclePart part : V.parts) {
      part.deform();
     }
    } else {
     V.addDamage(E.Pool.type == E.Pool.Type.acid ? .0025 * V.durability * VE.tick : 0);
    }
   }
   speedX -= speedX * .01 * VE.tick;
   speedY -= speedY * .1 * VE.tick;
   speedZ -= speedZ * .01 * VE.tick;
   if (V.amphibious) {
    mode = Mode.drivePool;
   }
  }
 }

 private void runTrackPlaneInteraction(boolean crashLand, double bounceBackForce, double flatPlaneBounce, double gravityCompensation) {
  double sinXZ = U.sin(V.XZ), cosXZ = U.cos(V.XZ), cosYZ = U.cos(V.YZ), cosXY = U.cos(V.XY),
  wallPlaneBounce = Math.min(Math.abs(cosXY) + Math.abs(cosYZ), 1),//<-Not the best, but still probably better than a flat value
  crashPower = 0;
  boolean spinnerHit = inWall = false;
  for (TrackPart trackPart : TE.trackParts) {
   if (!trackPart.trackPlanes.isEmpty() && U.distance(V.X, trackPart.X, V.Z, trackPart.Z) < trackPart.renderRadius + V.renderRadius) {
    for (TrackPlane trackPlane : trackPart.trackPlanes) {
     double trackX = trackPlane.X + trackPart.X, trackY = trackPlane.Y + trackPart.Y, trackZ = trackPlane.Z + trackPart.Z,
     velocityXZ = Math.abs(sinXZ),
     radiusX = trackPlane.radiusX + (trackPlane.addSpeed && velocityXZ > U.sin45 ? netSpeed * VE.tick : 0),
     radiusY = trackPlane.radiusY + (trackPlane.addSpeed ? netSpeed * VE.tick : 0),
     radiusZ = trackPlane.radiusZ + (trackPlane.addSpeed && velocityXZ < U.sin45 ? netSpeed * VE.tick : 0);
     boolean isTree = trackPlane.type.contains(SL.Thicks.tree), gate = trackPlane.type.contains(SL.gate),
     isWall = trackPlane.wall != TrackPlane.Wall.none;
     String trackProperties = "";
     if (!isTree && !gate && (isWall || (Math.abs(V.X - trackX) < radiusX && Math.abs(V.Z - trackZ) < radiusZ && trackY + (radiusY * .5) >= V.Y))) {
      trackProperties = trackPlane.type + (U.contains(trackPlane.type, SL.Thicks.paved, SL.Thicks.rock, SL.Thicks.grid, SL.Thicks.antigravity, SL.Thicks.metal, SL.Thicks.brightmetal) ? SL.Thicks.hard : SL.Thicks.ground);
     }
     //NON-WHEEL BASED
     if (Math.abs(V.Y - (trackY + gravityCompensation)) <= trackPlane.radiusY) {
      if (Math.abs(V.X - trackX) <= radiusX && Math.abs(V.Z - trackZ) <= radiusZ) {
       if (gate) {
        runSpeedGate(trackPlane);
       } else if (trackProperties.contains(SL.Thicks.antigravity)) {
        speedY -= E.gravity * 2 * VE.tick;
        onAntiGravity = true;
       }
      }
     }
     //WHEEL BASED
     boolean
     criterion = V.Y >= trackY || speedY >= 0 || Math.abs(speed) < E.gravity * 4 * VE.tick,
     angleAdjustForYZ = criterion || Math.abs((cosXZ > 0 ? -V.YZ : V.YZ) - trackPlane.YZ) < 30,
     angleAdjustForXY = criterion || Math.abs((sinXZ < 0 ? -V.YZ : V.YZ) - trackPlane.XY) < 30;
     for (Wheel wheel : V.wheels) {
      boolean inX = Math.abs(wheel.X - trackX) <= radiusX, inZ = Math.abs(wheel.Z - trackZ) <= radiusZ;
      if (Math.abs(wheel.Y - (trackY + gravityCompensation)) <= trackPlane.radiusY) {
       if (inX && inZ) {
        if (isTree) {
         speedX -= U.random() * speedX * VE.tick;
         speedY -= U.random() * speedY * VE.tick;
         speedZ -= U.random() * speedZ * VE.tick;
         wheel.againstWall = true;
        } else if (!isWall) {
         if (trackPlane.YZ == 0 && trackPlane.XY == 0 && wheel.Y > trackY - 5) {//'- 5' is for better traction control--not to be used for stationary objects. Do not transfer '-5' to any assignments
          mode = Mode.driveSolid;
          wheel.Y = Math.min(localGround, trackY);
          if (flipped && trackProperties.contains(SL.Thicks.hard)) {
           wheel.sparks(true);
          }
          if (crashLand) {
           crashPower = Math.max(crashPower, Math.abs(speedY * bounceBackForce));
          }
          if (speedY > RichHit.minimumSpeed) {
           for (long n = RichHit.dustQuantity; --n >= 0; ) {
            V.deployDust(true);
           }
           V.VA.land();
          }
          if (speedY > 0) {
           speedY *= V.destroyed ? 0 : -V.bounce * flatPlaneBounce;
          }
          wheel.XY -= wheel.XY * valueAdjustSmoothing * VE.tick;
          wheel.YZ -= wheel.YZ * valueAdjustSmoothing * VE.tick;
          wheel.minimumY = trackY;
         } else if (trackPlane.YZ != 0) {
          double setY = trackY + (wheel.Z - trackZ) * (trackPlane.radiusY / trackPlane.radiusZ) * (trackPlane.YZ > 0 ? 1 : trackPlane.YZ < 0 ? -1 : 0);
          if (wheel.Y >= setY - 100) {
           wheel.angledSurface = true;
           mode = Mode.driveSolid;
           if (!trackProperties.contains(SL.Thicks.hard)) {
            V.deployDust(false);
           } else if (flipped) {
            wheel.sparks(true);
           }
           if (angleAdjustForYZ) {
            wheel.YZ += (-trackPlane.YZ * cosXZ - wheel.YZ) * valueAdjustSmoothing * VE.tick;
            wheel.Y = setY;
           }
           wheel.XY += (trackPlane.YZ * sinXZ - wheel.XY) * valueAdjustSmoothing * VE.tick;
          }
         } else if (trackPlane.XY != 0) {
          double setY = trackY + (wheel.X - trackX) * (trackPlane.radiusY / trackPlane.radiusX) * (trackPlane.XY > 0 ? 1 : trackPlane.XY < 0 ? -1 : 0);
          if (wheel.Y >= setY - 100) {
           wheel.angledSurface = true;
           mode = Mode.driveSolid;
           if (!trackProperties.contains(SL.Thicks.hard)) {
            V.deployDust(false);
           } else if (flipped) {
            wheel.sparks(true);
           }
           if (angleAdjustForXY) {
            wheel.YZ += (trackPlane.XY * sinXZ - wheel.YZ) * valueAdjustSmoothing * VE.tick;
            wheel.Y = setY;
           }
           wheel.XY += (trackPlane.XY * cosXZ - wheel.XY) * valueAdjustSmoothing * VE.tick;
          }
         }
        }
       }
       if (isWall) {
        double vehicleRadius = V.collisionRadius * .5, contactX = trackPlane.radiusX + vehicleRadius, contactZ = trackPlane.radiusZ + vehicleRadius;
        if (inX && Math.abs(wheel.Z - trackZ) <= contactZ) {
         if (
         (trackPlane.wall == TrackPlane.Wall.front && wheel.Z < trackZ + contactZ && speedZ < 0) ||
         (trackPlane.wall == TrackPlane.Wall.back && wheel.Z > trackZ - contactZ && speedZ > 0)) {
          if (trackProperties.contains(SL.Thicks.hard)) {
           wheel.sparks(false);
          }
          crashPower = Math.max(crashPower, Math.abs(speedZ * trackPlane.damage));
          speedZ *= -1 * V.bounce * wallPlaneBounce;
          spinnerHit = wheel.againstWall = true;
         }
         inWall = true;
        }
        if (inZ && Math.abs(wheel.X - trackX) <= contactX) {
         if (
         (trackPlane.wall == TrackPlane.Wall.right && wheel.X < trackX + contactX && speedX < 0) ||
         (trackPlane.wall == TrackPlane.Wall.left && wheel.X > trackX - contactX && speedX > 0)) {
          if (trackProperties.contains(SL.Thicks.hard)) {
           wheel.sparks(false);
          }
          crashPower = Math.max(crashPower, Math.abs(speedX * trackPlane.damage));
          speedX *= -1 * V.bounce * wallPlaneBounce;
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
 }

 private void runSpeedGate(TrackPlane trackPlane) {
  if (trackPlane.type.contains(" slowgate ")) {
   if (Math.abs(trackPlane.YZ) == 90) {
    if (Math.abs(speedZ) > V.topSpeeds[0]) {
     speedZ *= .333;
     V.VA.gate.play(1, V.VA.distanceVehicleToCamera);
    }
   } else if (Math.abs(trackPlane.XY) == 90) {
    if (Math.abs(speedX) > V.topSpeeds[0]) {
     speedX *= .333;
     V.VA.gate.play(1, V.VA.distanceVehicleToCamera);
    }
   }
  } else {
   speedZ *= Math.abs(trackPlane.YZ) == 90 ? 3 : 1;
   speedX *= Math.abs(trackPlane.XY) == 90 ? 3 : 1;
   speed *= (speed > 0 && speed < V.topSpeeds[1]) || (speed < 0 && speed > -V.topSpeeds[0]) ? 1.25 : 1;
   if (speedX != 0 || speedZ != 0) {
    V.VA.gate.play(0, V.VA.distanceVehicleToCamera);
   }
  }
 }

 private void setTerrainFromTrackPlanes(double clearance, double gravityCompensation) {
  for (TrackPart trackPart : TE.trackParts) {
   if (!trackPart.trackPlanes.isEmpty() && U.distance(V.X, trackPart.X, V.Z, trackPart.Z) < trackPart.renderRadius + V.renderRadius) {
    for (TrackPlane trackPlane : trackPart.trackPlanes) {
     if (trackPlane.wall == TrackPlane.Wall.none && !trackPlane.type.contains(SL.Thicks.tree) && !trackPlane.type.contains(SL.gate)) {
      double trackX = trackPlane.X + trackPart.X, trackY = trackPlane.Y + trackPart.Y, trackZ = trackPlane.Z + trackPart.Z;
      String addHard = U.contains(trackPlane.type, SL.Thicks.paved, SL.Thicks.rock, SL.Thicks.grid, SL.Thicks.antigravity, SL.Thicks.metal, SL.Thicks.brightmetal) ? SL.Thicks.hard : SL.Thicks.ground;
      if (Math.abs(V.X - trackX) <= trackPlane.radiusX && Math.abs(V.Z - trackZ) <= trackPlane.radiusZ &&
      Math.abs(V.Y + clearance - (trackY + gravityCompensation)) <= trackPlane.radiusY) {
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
 }

 private void runBouncyTerrain(boolean onBouncy) {
  if (onBouncy) {
   speedY -= U.random(terrainProperties.contains(SL.Thicks.bounce) ? .3 : .6) * Math.abs(speed) * V.bounce;
   if (speedY < -50 && V.bounce > .9) {
    V.VA.land.playIfNotPlaying(Double.NaN, V.VA.distanceVehicleToCamera);
   }
  }
 }

 private void runSpeedConstraints() {
  if (mode.name().startsWith(SL.drive) || V.phantomEngaged) {
   if (V.grip <= highGrip && !flipped && Math.abs(speed) > netSpeed && Math.abs(Math.abs(speed) - netSpeed) > Math.abs(speed) * .5 && !terrainProperties.contains(SL.Thicks.ice)) {
    speed *= .5;
   }
   if (V.turnDrag && (V.turnL || V.turnR)) {
    speed -= speed * .01 * VE.tick;
   }
   if (V.destroyed) {
    speed *= .9;
    if (Math.abs(speed) < 2 * VE.tick) {
     speed = 0;
    } else {
     speed += (speed < 0 ? 2 : speed > 0 ? -2 : 0) * VE.tick;
    }
   }
  }
  speed = U.clamp(-V.topSpeeds[2], speed, V.topSpeeds[2]);
  if (againstWall() && V.grip > highGrip) {
   speed *= .95;
  }
  if (V.drag != 0) {
   if (Math.abs(speed) < V.drag * VE.tick) {
    speed = 0;
   } else if ((mode != Mode.fly && !V.drive && !V.reverse) || mode == Mode.stunt || Math.abs(speed) > V.topSpeeds[1]) {
    speed -= (speed > 0 ? 1 : -1) * V.drag * VE.tick;
   }
  }
  if (Math.abs(speed) > V.topSpeeds[1]) {
   speed -= speed * Math.abs(speed) * .0000005 * VE.tick;
  }
 }

 private void runAngleSet() {
  if (mode.name().startsWith(SL.drive)) {
   if (angledSurface()) {
    V.XY = (Math.abs(V.XY) > 90 ? 180 : 0) + (V.wheels.get(0).XY + V.wheels.get(1).XY + V.wheels.get(2).XY + V.wheels.get(3).XY) * .25;
    V.YZ = (Math.abs(V.YZ) > 90 ? 180 : 0) + (V.wheels.get(0).YZ + V.wheels.get(1).YZ + V.wheels.get(2).YZ + V.wheels.get(3).YZ) * .25;
   } else if (!onVolcano) {//<-May need stabilization for drivePOOL!
    if (Math.abs(V.YZ) <= 90) {
     V.YZ -= V.YZ > 0 ? VE.tick : 0;
     V.YZ += V.YZ < 0 ? VE.tick : 0;
    } else {
     V.YZ += (V.YZ > 90 && V.YZ < 180 - VE.tick ? 1 : V.YZ < -90 && V.YZ > -180 + VE.tick ? -1 : 0) * VE.tick;
    }
    if (Math.abs(V.XY) <= 90) {
     V.XY -= V.XY > 0 ? VE.tick : 0;
     V.XY += V.XY < 0 ? VE.tick : 0;
    }
    if (V.sidewaysLandingAngle == 0) {
     V.XY += (V.XY > 90 && V.XY < 180 - VE.tick ? 1 : V.XY < -90 && V.XY > -180 + VE.tick ? -1 : 0) * VE.tick;
    } else {
     V.XY += U.random(3.) * VE.tick *
     ((V.XY > 90 && V.XY < V.sidewaysLandingAngle) || (V.XY < -V.sidewaysLandingAngle && V.XY > -(V.sidewaysLandingAngle + 30)) ? 1 :
     (V.XY < -90 && V.XY > -V.sidewaysLandingAngle) || (V.XY > V.sidewaysLandingAngle && V.XY < V.sidewaysLandingAngle + 30) ? -1 : 0);
     if (Math.abs(V.XY) >= V.sidewaysLandingAngle + 30) {
      V.XY -= V.XY > -180 + VE.tick ? VE.tick : 0;
      V.XY += V.XY < 180 - VE.tick ? VE.tick : 0;
     }
    }
    double centerOut = mode == Mode.drivePool ? .9375 : .25;
    if (Math.abs(V.XY) <= 90) {
     V.XY *= centerOut;
    }
    if (Math.abs(V.YZ) <= 90) {
     V.YZ *= centerOut;
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
   if (flipped && V.landStuntsBothSides) {
    V.XZ += Math.abs(V.YZ) > 90 && Math.abs(V.XY) < 90 ? 180 : 0;
    V.XY = V.YZ = 0;
   }
  }
 }

 private void runHitOther() {
  if (!Double.isNaN(hitOtherX)) {
   speedX = hitOtherX;
   hitOtherX = Double.NaN;
  }
  if (!Double.isNaN(hitOtherZ)) {
   speedZ = hitOtherZ;
   hitOtherZ = Double.NaN;
  }
 }

 private void runSetWheelXYZ(double clearance) {
  double primeX = speedX * VE.tick, primeY = speedY * VE.tick, primeZ = speedZ * VE.tick;
  for (Wheel wheel : V.wheels) {
   double[] wheelX = {wheel.pointX}, wheelY = {clearance}, wheelZ = {wheel.pointZ};
   if (V.XY != 0) {
    U.rotate(wheelX, wheelY, V.XY);
   }
   if (V.YZ != 0) {
    U.rotate(wheelY, wheelZ, V.YZ);
   }
   U.rotate(wheelX, wheelZ, V.XZ);
   wheel.X = wheelX[0] + V.X + primeX;//<-Must be primed with existing speeds or will be delayed from Core positions!
   wheel.Y = wheelY[0] + V.Y + primeY;
   wheel.Z = wheelZ[0] + V.Z + primeZ;
  }
 }
}
