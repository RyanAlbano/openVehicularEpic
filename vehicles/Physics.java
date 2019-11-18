package ve.vehicles;

import javafx.scene.paint.Color;
import ve.Camera;
import ve.Network;
import ve.VE;
import ve.effects.Explosion;
import ve.environment.*;
import ve.trackElements.TE;
import ve.trackElements.trackParts.TrackPart;
import ve.trackElements.trackParts.TrackPlane;
import ve.utilities.U;
import ve.vehicles.specials.Shot;
import ve.vehicles.specials.Special;

public class Physics {
 private final Vehicle V;
 public double speed, speedXZ, speedYZ, stallSpeed;
 double netSpeed;
 public double netSpeedX, netSpeedY, netSpeedZ;
 double minimumFlightSpeedWithoutStall;
 private double lastXZ;
 public double cameraXZ;
 private double airSpinXZ;
 final double[] wheelSpin = new double[2];
 double destructTimer;
 double massiveHitTimer;
 double localVehicleGround;
 public boolean flipped;
 public double stuntTimer;
 public double stuntXY, stuntYZ, stuntXZ;
 double stuntSpeedYZ, stuntSpeedXY, stuntSpeedXZ;
 public double flipTimer, stuntReward, stuntLandWaitTime = 8;
 private boolean onAntiGravity;
 public boolean inTornado;
 boolean onVolcano;
 boolean atPoolXZ;
 public boolean inPool;
 boolean wheelDiscord;
 double wheelGapFrontToBack;
 double wheelGapLeftToRight;
 public boolean wrathEngaged;
 public boolean[] wrathStuck;
 boolean inWrath;
 private long spinMultiplyPositive = 1, spinMultiplyNegative = 1;
 public long vehicleHit = -1;
 public double explodeStage;
 public boolean subtractExplodeStage;
 public String terrainProperties = "";
 double explosionDiameter, explosionDamage, explosionPush;
 static final double fromAngleToVelocityConstant = .2;
 public long polarity;
 public Mode mode = Mode.drive;

 public enum Mode {drive, neutral, stunt, fly, drivePool}

 enum Contact {none, rubber, metal}

 enum Landing {tires, touch, crash}

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

 private void crash(double power) {
  power = Math.abs(power * V.fragility);
  if (power > 10) {
   V.addDamage(Math.abs(power * 2 * VE.tick));
   for (VehiclePart part : V.parts) {
    part.deform();
    part.throwChip(power);
   }
   if (V.isIntegral() && V.VA.crashTimer <= 0) {
    (power > 30 ? V.VA.crashHard : V.VA.crashSoft).play(Double.NaN, V.VA.distanceVehicleToCamera);
    if (V.bounce > .9) {
     V.VA.land.play(Double.NaN, V.VA.distanceVehicleToCamera);
    }
    V.VA.crashTimer = 2;
   }
  }
 }

 public void runCollisions() {
  boolean replay = VE.status == VE.Status.replay, greenTeam = V.index < VE.vehiclesInMatch >> 1;
  if (!V.phantomEngaged) {
   double netDamage;
   if (!V.destroyed && !V.reviveImmortality) {
    for (Vehicle otherV : VE.vehicles) {
     if (!U.sameTeam(V, otherV) && !otherV.destroyed && !otherV.reviveImmortality && !otherV.phantomEngaged) {
      boolean aHit = false;
      for (int n = 4; --n >= 0; ) {
       Wheel W = V.wheels.get(n), otherW = otherV.wheels.get(n);
       double collideAt = (V.collisionRadius() + otherV.collisionRadius()) * .5,
       behindX = W.X - (netSpeedX * VE.tick),
       behindY = W.Y - (netSpeedY * VE.tick),
       behindZ = W.Z - (netSpeedZ * VE.tick),
       averageX = behindX * .5,
       averageY = behindY * .5,
       averageZ = behindZ * .5;
       if (
       ((U.distance(averageY, otherW.Y, averageZ, otherW.Z) < collideAt || U.distance(behindY, otherW.Y, behindZ, otherW.Z) < collideAt) && ((W.X > otherW.X && behindX < otherW.X) || (W.X < otherW.X && behindX > otherW.X))) ||//<-inBoundsX
       ((U.distance(averageX, otherW.X, averageY, otherW.Y) < collideAt || U.distance(behindX, otherW.X, behindY, otherW.Y) < collideAt) && ((W.Z > otherW.Z && behindZ < otherW.Z) || (W.Z < otherW.Z && behindZ > otherW.Z))) ||//<-inBoundsZ
       ((U.distance(averageX, otherW.X, averageZ, otherW.Z) < collideAt || U.distance(behindX, otherW.X, behindZ, otherW.Z) < collideAt) && ((W.Y > otherW.Y && behindY < otherW.Y) || (W.Y < otherW.Y && behindY > otherW.Y))) ||//<-inBoundsY
       U.distance(W, otherW) < collideAt) {
        W.speedY -= V.getsLifted > 0 && V.Y < otherV.Y ? E.gravity * 1.5 * VE.tick : 0;
        netDamage = Math.abs(netSpeed - otherV.P.netSpeed) * otherV.damageDealt[n] * otherV.energyMultiple * .3;//<-Damage now RECEIVING from other vehicles--not vice versa
        if (V.isIntegral()) {
         VE.Match.scoreDamage[greenTeam ? 1 : 0] += replay ? 0 : netDamage;//<-Team assignment swapped because damage is RECEIVING
        }
        hitCheck(otherV);
        double theirPushX = 0, theirPushZ = 0, yourPushX = 0, yourPushZ = 0;
        if (otherV.isIntegral() && V.getsPushed >= otherV.getsPushed) {
         theirPushX = Math.max(0, V.getsPushed) * (otherW.speedX - W.speedX) * .25;
         theirPushZ = Math.max(0, V.getsPushed) * (otherW.speedZ - W.speedZ) * .25;
        }
        if (V.isIntegral() && V.getsPushed <= otherV.getsPushed) {
         yourPushX = Math.max(0, V.pushesOthers) * (W.speedX - otherW.speedX) * .25;
         yourPushZ = Math.max(0, V.pushesOthers) * (W.speedZ - otherW.speedZ) * .25;
        }
        if (V.getsPushed >= otherV.getsPushed) {
         if (
         ((V.X > otherV.X || W.X > otherW.X) && W.speedX < otherW.speedX) ||
         ((V.X < otherV.X || W.X < otherW.X) && W.speedX > otherW.speedX)) {
          W.hitOtherX = V.grip > 100 ? otherV.P.netSpeedX : otherW.speedX;
         }
         if (
         ((V.Z > otherV.Z || W.Z > otherW.Z) && W.speedZ < otherW.speedZ) ||
         ((V.Z < otherV.Z || W.Z < otherW.Z) && W.speedZ > otherW.speedZ)) {
          W.hitOtherZ = V.grip > 100 ? otherV.P.netSpeedZ : otherW.speedZ;
         }
        }
        otherW.speedX += yourPushX;
        otherW.speedZ += yourPushZ;
        W.speedX -= theirPushX;
        W.speedZ -= theirPushZ;
        if (V.getsLifted > 0 && V.Y != otherV.Y) {
         W.speedY -= V.getsLifted * (V.Y < otherV.Y ? 1 : -1) * .0025 * Math.abs(U.netValue(netSpeedX, netSpeedZ) - U.netValue(otherV.P.netSpeedX, otherV.P.netSpeedZ));
        }
        crash(netDamage);//<-Crashing self now, not the collided
        if (W.speedY < -100) {
         V.VA.land();
        }
        if (otherV.getsLifted > 0 && otherV.Y != V.Y) {
         otherW.speedY -= V.liftsOthers * (otherV.Y < V.Y ? 1 : -1) * .0025 * Math.abs(U.netValue(netSpeedX, netSpeedZ) - U.netValue(otherV.P.netSpeedX, otherV.P.netSpeedZ));
        }
        aHit = true;
       }
      }
      if (aHit) {
       if (V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name())) {
        V.setDamage(V.damageCeiling());
        otherV.setDamage(otherV.damageCeiling());
        VE.Match.scoreDamage[greenTeam ? 0 : 1] += replay ? 0 : otherV.durability;
        V.setCameraShake(Camera.shakePresets.normalNuclear);
       }
       if (V.type != Vehicle.Type.aircraft && V.damageDealt[U.random(4)] >= 100 && (massiveHitTimer <= 0 || otherV.isIntegral())) {
        otherV.setDamage(otherV.damageCeiling());
        VE.Match.scoreDamage[greenTeam ? 0 : 1] += replay ? 0 : otherV.durability;
        V.VA.massiveHit.play(Double.NaN, V.VA.distanceVehicleToCamera);
        massiveHitTimer = U.random(5.);
        for (VehiclePart part : otherV.parts) {
         part.deform();
         part.throwChip(U.randomPlusMinus(Math.abs(netSpeed - otherV.P.netSpeed) * .5));
        }
        V.setCameraShake(Camera.shakePresets.massiveHit);
       }
      }
      if (V.spinner != null && U.distance(V, otherV) < V.renderRadius + otherV.collisionRadius()) {//<-'renderRadius' is best if spinner has largest diameter of the vehicle
       V.spinner.hit(otherV);
      }
     }
    }
   }
   for (Special special : V.specials) {
    for (Vehicle vehicle : VE.vehicles) {
     if (!U.sameTeam(V, vehicle) && (!vehicle.destroyed || wrathEngaged) && !vehicle.reviveImmortality && !vehicle.phantomEngaged) {
      double diameter = special.type == Special.Type.mine ? U.netValue(vehicle.P.netSpeedX, vehicle.P.netSpeedY, vehicle.P.netSpeedZ) : special.diameter;
      for (Shot shot : special.shots) {
       if (shot.stage > 0 && shot.hit < 1 && (shot.doneDamaging == null || !shot.doneDamaging[vehicle.index]) && (special.type != Special.Type.missile || vehicle.isIntegral()) && !(special.type == Special.Type.mine && (U.distance(shot, vehicle) > 2000 || !vehicle.isIntegral()))) {
        double amount = special.diameter + vehicle.collisionRadius(), shotAverageX = (shot.X + shot.behindX) * .5, shotAverageY = (shot.Y + shot.behindY) * .5, shotAverageZ = (shot.Z + shot.behindZ) * .5;
        if (
        ((U.distance(shotAverageY, vehicle.Y, shotAverageZ, vehicle.Z) < amount || U.distance(shot.behindY, vehicle.Y, shot.behindZ, vehicle.Z) < amount) && ((shot.X > vehicle.X && shot.behindX < vehicle.X) || (shot.X < vehicle.X && shot.behindX > vehicle.X))) ||//<-inBoundsX
        ((U.distance(shotAverageX, vehicle.X, shotAverageY, vehicle.Y) < amount || U.distance(shot.behindX, vehicle.X, shot.behindY, vehicle.Y) < amount) && ((shot.Z > vehicle.Z && shot.behindZ < vehicle.Z) || (shot.Z < vehicle.Z && shot.behindZ > vehicle.Z))) ||//<-inBoundsZ
        ((U.distance(shotAverageX, vehicle.X, shotAverageZ, vehicle.Z) < amount || U.distance(shot.behindX, vehicle.X, shot.behindZ, vehicle.Z) < amount) && ((shot.Y > vehicle.Y && shot.behindY < vehicle.Y) || (shot.Y < vehicle.Y && shot.behindY > vehicle.Y))) ||//<-inBoundsY
        U.distance(shot, vehicle) < diameter + vehicle.collisionRadius()) {
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
           for (Wheel wheel : vehicle.wheels) {
            wheel.speedX += U.randomPlusMinus(special.pushPower);
            wheel.speedZ += U.randomPlusMinus(special.pushPower);
           }
          }
          if (vehicle.getsLifted >= 0 && (special.type == Special.Type.forcefield || U.contains(special.type.name(), Special.Type.shell.name(), Special.Type.missile.name()))) {
           for (Wheel wheel : vehicle.wheels) {
            wheel.speedY += U.randomPlusMinus(special.pushPower);
           }
          }
         }
         for (VehiclePart part : vehicle.parts) {
          part.deform();
          part.throwChip(U.randomPlusMinus(shot.speed * .5));
         }
         double shotToCameraSoundDistance = Math.sqrt(U.distance(shot)) * .08;
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
        for (int n = 4; --n >= 0; ) {
         vehicle.wheels.get(n).speedX = V.wheels.get(n).speedX;
         vehicle.wheels.get(n).speedY = V.wheels.get(n).speedY;
         vehicle.wheels.get(n).speedZ = V.wheels.get(n).speedZ;
        }
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
       if (explosion.stage > 0 && U.distance(explosion, vehicle) < vehicle.collisionRadius() + explosionDiameter) {
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
         for (Wheel wheel : vehicle.wheels) {
          wheel.speedX += U.randomPlusMinus(explosionPush);
          wheel.speedZ += U.randomPlusMinus(explosionPush);
         }
        }
        if (vehicle.getsLifted >= 0) {
         for (Wheel wheel : vehicle.wheels) {
          wheel.speedY += U.randomPlusMinus(explosionPush);
         }
        }
        for (VehiclePart part : vehicle.parts) {
         part.deform();
         part.throwChip(U.randomPlusMinus(500.));
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
      if (!U.sameTeam(V, vehicle) && !vehicle.destroyed && !vehicle.reviveImmortality && U.distance(V.X, vehicle.X, V.Y + (V.turretBaseY * .5), vehicle.Y, V.Z, vehicle.Z) < V.collisionRadius() * .5 + vehicle.collisionRadius() && !vehicle.phantomEngaged) {
       hitCheck(vehicle);
       if (vehicle.fragility > 0) {
        vehicle.addDamage(V.turretBaseDamageDealt * vehicle.fragility);
        VE.Match.scoreDamage[greenTeam ? 0 : 1] += replay ? 0 : V.turretBaseDamageDealt * vehicle.fragility;
       }
       for (Wheel wheel : vehicle.wheels) {
        wheel.speedX += U.randomPlusMinus(500.);
        wheel.speedZ += U.randomPlusMinus(500.);
        wheel.speedY += U.randomPlusMinus(200.);
       }
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
      if (special.fire && special.type.name().contains("particle")) {
       boolean disintegrate = special.type == Special.Type.particledisintegrator;
       for (Vehicle vehicle : VE.vehicles) {
        if ((disintegrate ? !U.sameTeam(V, vehicle) : !U.sameVehicle(V, vehicle) && U.sameTeam(V, vehicle)) &&
        !vehicle.destroyed && !vehicle.reviveImmortality && !vehicle.phantomEngaged) {
         if (
         ((vehicle.Y <= V.Y && U.sin(V.YZ) >= 0) || (vehicle.Y >= V.Y && U.sin(V.YZ) <= 0) || Math.abs(vehicle.Y - V.Y) < vehicle.collisionRadius()) &&//<-inY
         ((vehicle.X <= V.X && U.sin(V.XZ) >= 0) || (vehicle.X >= V.X && U.sin(V.XZ) <= 0)) &&//<-inX
         ((vehicle.Z <= V.Z && U.cos(V.XZ) <= 0) || (vehicle.Z >= V.Z && U.cos(V.XZ) >= 0)))/*<-inZ*/ {
          if (disintegrate) {
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
      if (V.Y >= E.Storm.stormCloudY && distance < V.collisionRadius() * 6) {
       V.addDamage(V.durability * .5 + (distance < V.collisionRadius() * 2 ? V.durability : 0));
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
      if (distance < V.collisionRadius() + fire.absoluteRadius) {
       V.addDamage(10 * VE.tick);
       if (distance * 2 < V.collisionRadius() + fire.absoluteRadius) {
        V.addDamage(10 * VE.tick);
       }
       for (VehiclePart part : V.parts) {
        part.deform();
       }
      }
     }
     for (Boulder.Instance boulder : Boulder.instances) {
      if (U.distance(V.X, boulder.X, V.Z, boulder.Z) < V.collisionRadius() + boulder.S.getRadius() && V.Y > boulder.Y - V.collisionRadius() - boulder.S.getRadius()) {
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
  vehicleHit = V.isIntegral() ? -1 : vehicleHit;
  atPoolXZ = E.Pool.exists && U.distance(V.X, E.Pool.X, V.Z, E.Pool.Z) < E.Pool.C[0].getRadius();
  if (V.isFixed())/*TURRETS & INFRASTRUCTURE*/ {
   inPool = atPoolXZ && V.Y > 0;
   polarity = 1;
   double randomTurnKick = U.random(V.randomTurn);
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
    wheel.speedX = wheel.speedZ = wheel.speedY = 0;
    wheel.speedY += V.isIntegral() ? -wheel.speedY : E.gravity * VE.tick;
   }
   localVehicleGround = V.Y + V.turretBaseY;//<-Confirmed correct
   V.YZ = U.clamp(-90, V.YZ, 90);
   V.XY = speed = 0;
   flipped = false;
  } else /*VEHICLES & AIRCRAFT*/ {
   boolean replay = VE.status == VE.Status.replay;
   if (Volcano.exists) {
    double volcanoDistance = U.distance(V.X, Volcano.X, V.Z, Volcano.Z);
    onVolcano = volcanoDistance < Volcano.radiusBottom && volcanoDistance > Volcano.radiusTop && V.Y > -Volcano.radiusBottom + volcanoDistance;
   } else {
    onVolcano = false;
   }
   netSpeedX = (V.wheels.get(0).speedX + V.wheels.get(1).speedX + V.wheels.get(2).speedX + V.wheels.get(3).speedX) * .25;
   netSpeedY = (V.wheels.get(0).speedY + V.wheels.get(1).speedY + V.wheels.get(2).speedY + V.wheels.get(3).speedY) * .25;
   netSpeedZ = (V.wheels.get(0).speedZ + V.wheels.get(1).speedZ + V.wheels.get(2).speedZ + V.wheels.get(3).speedZ) * .25;
   netSpeed = U.netValue(netSpeedX, netSpeedY, netSpeedZ);
   polarity = Math.abs(V.YZ) > 90 ? -1 : 1;
   flipped = (Math.abs(V.XY) > 90 && Math.abs(V.YZ) <= 90) || (Math.abs(V.YZ) > 90 && Math.abs(V.XY) <= 90);
   double clearance = flipped && !V.landStuntsBothSides ? -V.absoluteRadius * .075 : V.clearanceY;
   inPool = atPoolXZ && V.Y + clearance > 0;
   if (mode.name().startsWith(Mode.drive.name())) {
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
   double turnAmount = V.maxTurn + U.random(V.randomTurn);
   if (!replay) {
    runSteering(turnAmount);
    if (V.VT != null) {
     V.VT.runSteering(turnAmount);
    }
   }
   double turnSpeed = V.steerInPlace ? 1 : Math.min(1, Math.max(netSpeed, Math.abs(speed)) * .025);
   turnSpeed *= speed < 0 ? -1 : 1;
   if (V.handbrake && mode == Mode.fly) {
    V.XZ += speedXZ * turnSpeed * fromAngleToVelocityConstant * polarity * VE.tick;
   } else if (mode.name().startsWith(Mode.drive.name()) || V.phantomEngaged) {
    if (!flipped) {
     airSpinXZ = (V.handbrake ? fromAngleToVelocityConstant : .0625) * speedXZ * turnSpeed;
     V.XZ += speedXZ * turnSpeed * fromAngleToVelocityConstant * VE.tick;
    }
   } else if (mode != Mode.fly && (!V.aerialControlEnhanced || mode != Mode.stunt)) {
    V.XZ += airSpinXZ * VE.tick;
    stuntXZ += airSpinXZ * VE.tick;
   }
   runFlight(turnAmount);
   for (Wheel wheel : V.wheels) {
    wheel.X = V.X + wheel.pointX;
    wheel.Y = V.Y + clearance;
    wheel.Z = V.Z + wheel.pointZ;
    double[] rotated = U.rotate(wheel.X, wheel.Y, V.X, V.Y, V.XY);
    wheel.X = rotated[0];
    wheel.Y = rotated[1];
    if (!angledSurface()) {
     rotated = U.rotate(wheel.Y, wheel.Z, V.Y, V.Z, V.YZ);
     wheel.Y = rotated[0];
     wheel.Z = rotated[1];
    }
    rotated = U.rotate(wheel.X, wheel.Z, V.X, V.Z, V.XZ);
    wheel.X = rotated[0];
    wheel.Z = rotated[1];
    if (!wheel.angledSurface && !V.phantomEngaged && !inTornado && !V.floats)
     wheel.speedY += (V.amphibious && atPoolXZ && wheel.Y - clearance > 0 ? -E.gravity : E.gravity) * VE.tick;
   }
   if (mode != Mode.fly || V.grip <= 100) {
    for (Wheel wheel : V.wheels) {
     wheel.speedX = wheel.speedX - netSpeedX > 200 ? netSpeedX + 200 : wheel.speedX - netSpeedX < -200 ? netSpeedX - 200 : wheel.speedX;
     wheel.speedZ = wheel.speedZ - netSpeedZ > 200 ? netSpeedZ + 200 : wheel.speedZ - netSpeedZ < -200 ? netSpeedZ - 200 : wheel.speedZ;
     wheel.X += (V.wheels.get(0).speedX + V.wheels.get(1).speedX + V.wheels.get(2).speedX + V.wheels.get(3).speedX) * .25 * VE.tick;
     wheel.Z += (V.wheels.get(0).speedZ + V.wheels.get(1).speedZ + V.wheels.get(2).speedZ + V.wheels.get(3).speedZ) * .25 * VE.tick;
     wheel.Y += wheel.speedY * VE.tick;
    }
   }
   if (mode.name().startsWith(Mode.drive.name()) || V.phantomEngaged) {
    speed -= V.turnDrag && (V.turnL || V.turnR) ? speed * .01 * VE.tick : 0;
    double setGrip = V.grip - (V.handbrake && !flipped && !U.equals(VE.Map.name, "the Maze", "XY Land") ? V.grip * .25 * Math.abs(lastXZ - V.XZ) : 0);
    setGrip *= (terrainProperties.contains(" ice ") ? .075 : terrainProperties.contains(" ground ") ? .75 : 1) * (flipped ? .2 : 1);
    setGrip = Math.max(setGrip * VE.tick, 0);
    if (V.destroyed) {
     speed *= .9;
     if (Math.abs(speed) < 2 * VE.tick) {
      speed = 0;
     } else {
      speed += (speed < 0 ? 2 : speed > 0 ? -2 : 0) * VE.tick;
     }
    }
    boolean modeDrive = mode == Mode.drive;
    double speed1 = -(speed * U.sin(V.XZ) * U.cos(V.YZ)), speed2 = speed * U.cos(V.XZ) * U.cos(V.YZ), speed3 = -(speed * U.sin(V.YZ));
    for (Wheel wheel : V.wheels) {
     if (flipped) {
      wheel.speedX -= wheel.speedX > setGrip ? setGrip : Math.max(wheel.speedX, -setGrip);
      wheel.speedZ -= wheel.speedZ > setGrip ? setGrip : Math.max(wheel.speedZ, -setGrip);
     } else {
      if (Math.abs(wheel.speedX - speed1) > setGrip) {
       wheel.speedX += wheel.speedX < speed1 ? setGrip : wheel.speedX > speed1 ? -setGrip : 0;
      } else {
       wheel.speedX = speed1;
      }
      if (Math.abs(wheel.speedZ - speed2) > setGrip) {
       wheel.speedZ += wheel.speedZ < speed2 ? setGrip : wheel.speedZ > speed2 ? -setGrip : 0;
      } else {
       wheel.speedZ = speed2;
      }
      if (modeDrive || V.phantomEngaged) {
       if (Math.abs(wheel.speedY - speed3) > setGrip) {
        wheel.speedY += wheel.speedY < speed3 ? setGrip : wheel.speedY > speed3 ? -setGrip : 0;
       } else {
        wheel.speedY = speed3;
       }
      }
     }
    }
    if (modeDrive) {
     runSkidsAndDust();
    }
    speed *= V.grip <= 100 && !flipped && Math.abs(speed) > netSpeed && Math.abs(Math.abs(speed) - netSpeed) > Math.abs(speed) * .5 && !terrainProperties.contains(" ice ") ? .5 : 1;
    if (terrainProperties.contains(" bounce ")) {
     for (Wheel wheel : V.wheels) {
      wheel.speedY -= U.random(.3) * Math.abs(speed) * V.bounce;
     }
     if (netSpeedY < -50 && V.bounce > .9) {
      V.VA.land.playIfNotPlaying(Double.NaN, V.VA.distanceVehicleToCamera);
     }
    } else if (terrainProperties.contains(" maxbounce ")) {
     for (Wheel wheel : V.wheels) {
      wheel.speedY -= U.random(.6) * Math.abs(speed) * V.bounce;
     }
     if (netSpeedY < -50 && V.bounce > .9) {
      V.VA.land.playIfNotPlaying(Double.NaN, V.VA.distanceVehicleToCamera);
     }
    }
    mode = Mode.neutral;
   }
   lastXZ = V.XZ;
   boolean crashLand = (Math.abs(V.YZ) > 30 || Math.abs(V.XY) > 30) && !(Math.abs(V.YZ) > 150 && Math.abs(V.XY) > 150);
   runPoolInteract();
   runSpeedBoost();
   double gravityCompensation = E.gravity * 2 * VE.tick;
   if (!V.phantomEngaged) {
    double hitsGroundY = -5;
    if (onAntiGravity) {
     hitsGroundY = Double.POSITIVE_INFINITY;//<-Why was this done? To eliminate '-5' traction-lock, maybe?
     onAntiGravity = false;
    }
    double bounceBackForce = flipped ? 1 : Math.abs(U.sin(V.XY)) + Math.abs(U.sin(V.YZ)),
    flatPlaneBounce = Math.min(Math.abs(U.sin(V.XY)) + Math.abs(U.sin(V.YZ)), 1);
    boolean possibleSpinnerHit = false;
    for (Wheel wheel : V.wheels) {
     if (wheel.Y > hitsGroundY + localVehicleGround) {
      mode = Mode.drive;
      if (V.Y + clearance > localVehicleGround + 100) {//Remove if bounce dust looks pointless
       V.deployDust(wheel, true);
      }
      wheel.Y = localVehicleGround;
      if (crashLand) {
       crash(wheel.speedY * bounceBackForce * .1);
      }
      if (wheel.speedY > 100) {
       V.VA.land();
      }
      if (wheel.speedY > 0) {
       wheel.speedY *= V.destroyed ? 0 : -V.bounce * flatPlaneBounce;
      }
      wheel.XY -= wheel.XY * .25;
      wheel.YZ -= wheel.YZ * .25;
      if (flipped && terrainProperties.contains(" hard ")) {
       wheel.sparks(true);
      }
      wheel.terrainRGB = E.Ground.RGB;
      possibleSpinnerHit = true;
     }
     if (inPool) {
      wheel.speedX -= wheel.speedX * .01 * VE.tick;
      wheel.speedY -= wheel.speedY * .1 * VE.tick;
      wheel.speedZ -= wheel.speedZ * .01 * VE.tick;
     }
     wheel.againstWall = wheel.angledSurface = false;
    }
    if (V.spinner != null && possibleSpinnerHit && !((Math.abs(V.YZ) < 10 && Math.abs(V.XY) < 10) || (Math.abs(V.YZ) > 170 && Math.abs(V.XY) > 170))) {
     V.spinner.hit(null);
    }
    mode = V.amphibious && inPool ? Mode.drivePool : mode;
    terrainProperties = E.Terrain.vehicleDefaultTerrain;
    setTerrainFromTrackPlanes(clearance, gravityCompensation);
    runTrackPlaneInteraction(crashLand, bounceBackForce, flatPlaneBounce, gravityCompensation);
    mode = onVolcano ? Mode.drive : mode;
    if (mode == Mode.drive && angledSurface()) {
     V.XY = (Math.abs(V.XY) > 90 ? 180 : 0) + (V.wheels.get(0).XY + V.wheels.get(1).XY + V.wheels.get(2).XY + V.wheels.get(3).XY) * .25;
     V.YZ = (Math.abs(V.YZ) > 90 ? 180 : 0) + (V.wheels.get(0).YZ + V.wheels.get(1).YZ + V.wheels.get(2).YZ + V.wheels.get(3).YZ) * .25;
    }
    if (Math.abs(V.XY) > 90) {
     V.XY += V.wheels.get(0).speedY * VE.tick / wheelGapLeftToRight;
     V.XY -= V.wheels.get(1).speedY * VE.tick / wheelGapLeftToRight;
     V.XY += V.wheels.get(2).speedY * VE.tick / wheelGapLeftToRight;
     V.XY -= V.wheels.get(3).speedY * VE.tick / wheelGapLeftToRight;
    } else {
     V.XY -= V.wheels.get(0).speedY * VE.tick / wheelGapLeftToRight;
     V.XY += V.wheels.get(1).speedY * VE.tick / wheelGapLeftToRight;
     V.XY -= V.wheels.get(2).speedY * VE.tick / wheelGapLeftToRight;
     V.XY += V.wheels.get(3).speedY * VE.tick / wheelGapLeftToRight;
    }
    if (Math.abs(V.YZ) > 90) {
     V.YZ += V.wheels.get(0).speedY * VE.tick / wheelGapFrontToBack;
     V.YZ += V.wheels.get(1).speedY * VE.tick / wheelGapFrontToBack;
     V.YZ -= V.wheels.get(2).speedY * VE.tick / wheelGapFrontToBack;
     V.YZ -= V.wheels.get(3).speedY * VE.tick / wheelGapFrontToBack;
    } else {
     V.YZ -= V.wheels.get(0).speedY * VE.tick / wheelGapFrontToBack;
     V.YZ -= V.wheels.get(1).speedY * VE.tick / wheelGapFrontToBack;
     V.YZ += V.wheels.get(2).speedY * VE.tick / wheelGapFrontToBack;
     V.YZ += V.wheels.get(3).speedY * VE.tick / wheelGapFrontToBack;
    }
    runCollisionSpin();
   }
   speed = V.topSpeeds[2] < Long.MAX_VALUE ? U.clamp(-V.topSpeeds[2], speed, V.topSpeeds[2]) : speed;
   inTornado = false;
   if (!V.phantomEngaged) {
    Tornado.vehicleInteract(V);
   }
   for (Wheel wheel : V.wheels) {
    if (!Double.isNaN(wheel.hitOtherX)) {
     wheel.speedX = wheel.hitOtherX;
     wheel.hitOtherX = Double.NaN;
    }
    if (!Double.isNaN(wheel.hitOtherZ)) {
     wheel.speedZ = wheel.hitOtherZ;
     wheel.hitOtherZ = Double.NaN;
    }
   }
   if (mode == Mode.drive) {
    V.Y = (V.wheels.get(0).Y + V.wheels.get(1).Y + V.wheels.get(2).Y + V.wheels.get(3).Y) * .25 - (clearance * U.cos(V.YZ) * U.cos(V.XY));
   } else {
    V.Y += (V.wheels.get(0).speedY + V.wheels.get(1).speedY + V.wheels.get(2).speedY + V.wheels.get(3).speedY) * .25 * VE.tick;
   }
   if (againstWall()) {
    V.X = (V.wheels.get(0).X - V.wheels.get(0).pointX * U.cos(V.XZ) + polarity * V.wheels.get(0).pointZ * U.sin(V.XZ) + V.wheels.get(1).X - V.wheels.get(1).pointX * U.cos(V.XZ) + polarity * V.wheels.get(1).pointZ * U.sin(V.XZ) + V.wheels.get(2).X - V.wheels.get(2).pointX * U.cos(V.XZ) + polarity * V.wheels.get(2).pointZ * U.sin(V.XZ) + V.wheels.get(3).X - V.wheels.get(3).pointX * U.cos(V.XZ) + polarity * V.wheels.get(3).pointZ * U.sin(V.XZ)) * .25 + clearance * U.sin(V.XY) * U.cos(V.XZ) - clearance * U.sin(V.YZ) * U.sin(V.XZ);
    V.Z = (V.wheels.get(0).Z - polarity * V.wheels.get(0).pointZ * U.cos(V.XZ) - V.wheels.get(0).pointX * U.sin(V.XZ) + V.wheels.get(1).Z - polarity * V.wheels.get(1).pointZ * U.cos(V.XZ) - V.wheels.get(1).pointX * U.sin(V.XZ) + V.wheels.get(2).Z - polarity * V.wheels.get(2).pointZ * U.cos(V.XZ) - V.wheels.get(2).pointX * U.sin(V.XZ) + V.wheels.get(3).Z - polarity * V.wheels.get(3).pointZ * U.cos(V.XZ) - V.wheels.get(3).pointX * U.sin(V.XZ)) * .25 + clearance * U.sin(V.XY) * U.sin(V.XZ) - clearance * U.sin(V.YZ) * U.cos(V.XZ);
   } else {
    V.X += (V.wheels.get(0).speedX + V.wheels.get(1).speedX + V.wheels.get(2).speedX + V.wheels.get(3).speedX) * .25 * V.energyMultiple * VE.tick;
    V.Z += (V.wheels.get(0).speedZ + V.wheels.get(1).speedZ + V.wheels.get(2).speedZ + V.wheels.get(3).speedZ) * .25 * V.energyMultiple * VE.tick;
   }
   if (mode == Mode.drive && !angledSurface()) {
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
    V.XY *= Math.abs(V.XY) <= 90 ? .25 : 1;
    V.YZ *= Math.abs(V.YZ) <= 90 ? .25 : 1;
   }
   localVehicleGround = E.Ground.level + (atPoolXZ ? E.Pool.depth : 0);
   V.runMoundInteract(clearance, gravityCompensation);//<-localVehicleGround is set here
   if (onVolcano) {
    double baseAngle = flipped ? 225 : 45, vehicleVolcanoXZ = V.XZ, VolcanoPlaneY = Math.max(Volcano.radiusTop * .5, (Volcano.radiusBottom * .5) - ((Volcano.radiusBottom * .5) * (Math.abs(V.Y) / Volcano.height)));
    vehicleVolcanoXZ += V.Z < Volcano.Z && Math.abs(V.X - Volcano.X) < VolcanoPlaneY ? 180 : V.X >= Volcano.X + VolcanoPlaneY ? 90 : V.X <= Volcano.X - VolcanoPlaneY ? -90 : 0;
    V.XY = baseAngle * U.sin(vehicleVolcanoXZ);
    V.YZ = -baseAngle * U.cos(vehicleVolcanoXZ);
   }
   for (Wheel wheel : V.wheels) {
    wheel.vibrate = 0;
   }
   if (mode.name().startsWith(Mode.drive.name())) {
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
    runGroundCruise();
   }
   if (V.engine == Vehicle.Engine.hotrod && !V.destroyed && U.random() < .5) {
    V.XY += U.random() < .5 ? 1 : -1;
   }
   TE.runVehicleInteraction(V, replay);
   V.runStuntScoring(replay);
   speed *= againstWall() && V.grip > 100 && E.gravity != 0 ? .95 : 1;
   if (V.drag != 0) {
    if (Math.abs(speed) < V.drag * VE.tick) {
     speed = 0;
    } else if ((mode != Mode.fly && !V.drive && !V.reverse) || mode == Mode.stunt || Math.abs(speed) > V.topSpeeds[1]) {
     speed -= (speed > 0 ? 1 : -1) * V.drag * VE.tick;
    }
   }
   speed -= Math.abs(speed) > V.topSpeeds[1] ? speed * Math.abs(speed) * .0000005 * VE.tick : 0;
   if (E.mapBounds.slowVehicles) {
    if (V.X > E.mapBounds.right || V.X < E.mapBounds.left) {
     V.wheels.get(U.random(4)).speedX = V.wheels.get(U.random(4)).speedZ = 0;
     speed *= .95;
    }
    if (Math.abs(V.Y) > Math.abs(E.mapBounds.Y)) {
     V.wheels.get(U.random(4)).speedY = 0;
     speed *= .95;
    }
    if (V.Z > E.mapBounds.forward || V.Z < E.mapBounds.backward) {
     V.wheels.get(U.random(4)).speedX = V.wheels.get(U.random(4)).speedZ = 0;
     speed *= .95;
    }
   }
   if (onVolcano && !V.phantomEngaged) {
    V.Y = Math.min(V.Y, -Volcano.radiusBottom + U.distance(V.X, Volcano.X, V.Z, Volcano.Z));
    for (Wheel wheel : V.wheels) {
     wheel.speedY = Math.min(wheel.speedY, 0);
    }
   }
   mode = (mode == Mode.stunt || mode == Mode.fly) && V.destroyed ? Mode.neutral : mode;
   while (Math.abs(V.XZ - cameraXZ) > 180) {
    cameraXZ += cameraXZ < V.XZ ? 360 : -360;
   }
   cameraXZ += (V.XZ - cameraXZ) * .3 * StrictMath.pow(VE.tick, .8);
   lastXZ = V.XZ;
  }
  if (V.explosionsWhenDestroyed > 0 && !V.isIntegral() && !V.destroyed) {
   for (n = (int) V.explosionsWhenDestroyed; --n >= 0; ) {
    V.explosions.get(V.currentExplosion).deploy(U.randomPlusMinus(V.absoluteRadius), U.randomPlusMinus(V.absoluteRadius), U.randomPlusMinus(V.absoluteRadius), V);
    V.currentExplosion = ++V.currentExplosion >= E.explosionQuantity ? 0 : V.currentExplosion;
   }
   V.setCameraShake(Camera.shakePresets.vehicleExplode);
   V.VA.explode.play(Double.NaN, V.VA.distanceVehicleToCamera);
   //VA.crashDestroy.play(Double.NaN, VA.distanceVehicleToCamera);For turrets...maybe?
  }
  V.X = U.clamp(E.mapBounds.left, V.X, E.mapBounds.right);
  V.Z = U.clamp(E.mapBounds.backward, V.Z, E.mapBounds.forward);
  V.Y = U.clamp(E.mapBounds.Y, V.Y, -E.mapBounds.Y);
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

 private void runGroundCruise() {
  if (V.isIntegral() && mode == Mode.drive && V.bounce > 0) {
   if (V.type == Vehicle.Type.vehicle && !flipped) {
    V.XY += speed * V.clearanceY * V.bounce * speedXZ * (speed < 0 ? -.0000133 : .0000133) * (Math.abs(V.XY) > 10 ? .5 : 1);
   }
   boolean rockTerrain = terrainProperties.contains(" rock ");
   if (rockTerrain || terrainProperties.contains(" ground ")) {
    if (Double.isNaN(V.shockAbsorb)) {
     double vibrate = terrainProperties.contains(" rock ") ? .0003 : .00015;
     V.YZ += U.clamp(-180 - U.random(180.), U.randomPlusMinus(vibrate) * netSpeed * V.clearanceY * V.bounce, 180 + U.random(180.));
     V.XY += U.clamp(-180 - U.random(180.), U.randomPlusMinus(vibrate) * netSpeed * V.clearanceY * V.bounce, 180 + U.random(180.));
    } else {
     double vibrate = rockTerrain ? 50 : 25;
     double[] storeVibrate = new double[4];
     for (int n = 4; --n >= 0; ) {
      storeVibrate[n] = U.randomPlusMinus(Math.min(vibrate * netSpeed * V.bounce * .002, vibrate));
      V.wheels.get(n).vibrate = storeVibrate[n] * V.shockAbsorb;
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
    }
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

 private void runCollisionSpin() {
  if (V.spin > 0) {
   if (againstWall()) {
    double v1;
    v1 = Math.abs(V.XZ + 45);
    while (v1 > 180) v1 -= 360;
    spinMultiplyPositive = Math.abs(v1) > 90 ? 1 : -1;
    v1 = Math.abs(V.XZ - 45);
    while (v1 > 180) v1 -= 360;
    spinMultiplyNegative = Math.abs(v1) > 90 ? 1 : -1;
   }
   V.XZ += ((((V.wheels.get(0).speedZ * spinMultiplyNegative - V.wheels.get(1).speedZ * spinMultiplyPositive) + V.wheels.get(2).speedZ * spinMultiplyPositive - V.wheels.get(3).speedZ * spinMultiplyNegative) + V.wheels.get(0).speedX * spinMultiplyPositive + V.wheels.get(1).speedX * spinMultiplyNegative) - V.wheels.get(2).speedX * spinMultiplyNegative - V.wheels.get(3).speedX * spinMultiplyPositive) * V.spin * VE.tick;
  }
 }

 private void runSkidsAndDust() {
  boolean markedSnow = false;
  if (terrainProperties.contains(" snow ")) {
   for (Wheel wheel : V.wheels) {
    wheel.skidmark(true);
   }
   markedSnow = true;
  }
  boolean kineticFriction = Math.abs(Math.abs(speed) - netSpeed) > 15, driveEngine = !U.contains(V.engine.name(), "prop", Vehicle.Engine.jet.name(), Vehicle.Engine.rocket.name());
  if (((driveEngine && kineticFriction) || StrictMath.pow(speedXZ, 2) > 300000 / netSpeed) && (kineticFriction || Math.abs(speed) > V.topSpeeds[1] * .9)) {
   if (terrainProperties.contains(" hard ") && V.contact == Contact.metal) {
    for (Wheel wheel : V.wheels) {
     wheel.sparks(true);
    }
   }
   if (V.contact == Contact.rubber || !terrainProperties.contains(" hard ")) {
    for (Wheel wheel : V.wheels) {
     V.deployDust(wheel, false);
    }
   }
   if (!terrainProperties.contains(" ice ")) {
    if (!markedSnow) {
     for (Wheel wheel : V.wheels) {
      wheel.skidmark(false);
     }
    }
    V.VA.skid();
   }
  } else if (terrainProperties.contains(" snow ")) {
   for (Wheel wheel : V.wheels) {
    if (U.random() < .4) {
     V.deployDust(wheel, false);
    }
   }
  } else if (terrainProperties.contains(" ground ")) {
   for (Wheel wheel : V.wheels) {
    if (U.random() < .2) {
     V.deployDust(wheel, false);
    }
   }
  }
 }

 private void runAirEngage() {
  if (V.type == Vehicle.Type.vehicle && V.handbrake && mode == Mode.neutral) {
   mode = Mode.stunt;
   V.VA.airEngage.playIfNotPlaying(V.VA.distanceVehicleToCamera);
  }
  if (V.type == Vehicle.Type.aircraft && V.drive2) {
   boolean engageFly = mode == Mode.neutral;
   if (mode.name().startsWith(Mode.drive.name()) && V.reverse) {
    V.Y -= 10;
    engageFly = true;
   }
   if (engageFly) {
    mode = Mode.fly;
    V.VA.airEngage.playIfNotPlaying(V.VA.distanceVehicleToCamera);
   }
  }
 }

 private void runAerialControl() {
  if (V.aerialControlEnhanced && !onAntiGravity) {
   for (Wheel wheel : V.wheels) {
    wheel.speedY = netSpeedY;
   }
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
  if (stuntSpeedYZ < 0) {
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
  V.X += V.airPush * U.cos(V.XZ) * polarity * stuntSpeedXY * VE.tick;
  V.Z += V.airPush * U.sin(V.XZ) * polarity * stuntSpeedXY * VE.tick;
 }

 private void runFlight(double turnAmount) {
  if (mode == Mode.fly) {
   if (V.handbrake) {
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
   for (Wheel wheel : V.wheels) {
    wheel.speedX = -speed * U.sin(V.XZ) * U.cos(V.YZ);
    wheel.speedZ = speed * U.cos(V.XZ) * U.cos(V.YZ);
    wheel.speedY = -speed * U.sin(V.YZ) + stallSpeed;
   }
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
   stallSpeed = netSpeedY;
  }
 }

 private void runSpeedBoost() {
  if (V.boost && V.speedBoost > 0 && !V.destroyed) {
   for (Wheel wheel : V.wheels) {
    if (Math.abs(wheel.speedX) < V.topSpeeds[2]) {
     wheel.speedX -= V.speedBoost * U.sin(V.XZ) * polarity * VE.tick;
    } else {
     wheel.speedX *= .999;
    }
    if (Math.abs(wheel.speedZ) < V.topSpeeds[2]) {
     wheel.speedZ += V.speedBoost * U.cos(V.XZ) * polarity * VE.tick;
    } else {
     wheel.speedZ *= .999;
    }
    if (Math.abs(wheel.speedY) < V.topSpeeds[2] || (E.gravity > 0 && wheel.speedY > 0)) {
     wheel.speedY -= V.speedBoost * U.sin(V.YZ) * VE.tick;
    } else {
     wheel.speedY *= .999;
    }
   }
   speed += V.grip <= 100 ? V.speedBoost * VE.tick : 0;
  }
 }

 private void runPoolInteract() {
  if (inPool && !V.phantomEngaged) {
   if (netSpeed > 0) {
    for (int n = 3; --n >= 0; ) {
     for (Wheel wheel : V.wheels) {
      V.splashes.get(V.currentSplash).deploy(wheel, V.absoluteRadius * .0125 + U.random(V.absoluteRadius * .0125),
      wheel.speedX + U.randomPlusMinus(Math.max(speed, netSpeed)),
      wheel.speedY + U.randomPlusMinus(Math.max(speed, netSpeed)),
      wheel.speedZ + U.randomPlusMinus(Math.max(speed, netSpeed)));
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
  }
 }

 void runHitTsunami() {
  //* Turrets automatically have getsPushed/Lifted set to -1
  for (Tsunami.Part tsunamiPart : Tsunami.parts) {
   if (U.distance(V, tsunamiPart) < V.collisionRadius() + tsunamiPart.C.getRadius()) {
    if (V.getsPushed >= 0) {//*
     for (Wheel wheel : V.wheels) {
      wheel.speedX += Tsunami.speedX * .5 * VE.tick;
      wheel.speedZ += Tsunami.speedZ * .5 * VE.tick;
     }
    }
    if (V.getsLifted >= 0) {//*
     for (Wheel wheel : V.wheels) {
      wheel.speedY += E.gravity * VE.tick * (V.Y < tsunamiPart.Y ? 4 : V.Y > tsunamiPart.Y ? -4 : 0);
     }
    }
    for (int n1 = 20; --n1 >= 0; ) {
     V.splashes.get(V.currentSplash).deploy(V.wheels.get(U.random(4)), U.random(V.absoluteRadius * .05),
     Tsunami.speedX + U.randomPlusMinus(Math.max(Tsunami.speed, netSpeed)),
     U.randomPlusMinus(Math.max(Tsunami.speed, netSpeed)),
     Tsunami.speedZ + U.randomPlusMinus(Math.max(Tsunami.speed, netSpeed)));
     V.currentSplash = ++V.currentSplash >= E.splashQuantity ? 0 : V.currentSplash;
    }
    V.VA.tsunamiSplash.playIfNotPlaying(V.VA.distanceVehicleToCamera);
   }
  }
 }

 private void runTrackPlaneInteraction(boolean crashLand, double bounceBackForce, double flatPlaneBounce, double gravityCompensation) {
  double wallPlaneBounce = Math.min(Math.abs(U.cos(V.XY)) + Math.abs(U.cos(V.YZ)), 1);
  boolean spinnerHit = false;
  for (TrackPart trackPart : TE.trackParts) {
   if (!trackPart.trackPlanes.isEmpty() && U.distance(V.X, trackPart.X, V.Z, trackPart.Z) < trackPart.renderRadius + V.renderRadius) {
    for (TrackPlane trackPlane : trackPart.trackPlanes) {
     double trackX = trackPlane.X + trackPart.X, trackY = trackPlane.Y + trackPart.Y, trackZ = trackPlane.Z + trackPart.Z,
     velocityXZ = Math.abs(U.sin(V.XZ)),
     radiusX = trackPlane.radiusX + (trackPlane.addSpeed && velocityXZ > U.sin45 ? netSpeed * VE.tick : 0),
     radiusY = trackPlane.radiusY + (trackPlane.addSpeed ? netSpeed * VE.tick : 0),
     radiusZ = trackPlane.radiusZ + (trackPlane.addSpeed && velocityXZ < U.sin45 ? netSpeed * VE.tick : 0);
     boolean isTree = trackPlane.type.contains(" tree "), gate = trackPlane.type.contains("gate"),
     isWall = trackPlane.wall != TrackPlane.Wall.none;
     String trackProperties = "";
     if (!isTree && !gate && (isWall || (Math.abs(V.X - trackX) < radiusX && Math.abs(V.Z - trackZ) < radiusZ && trackY + (radiusY * .5) >= V.Y))) {
      trackProperties = trackPlane.type + (U.contains(trackPlane.type, " paved ", " rock ", " grid ", " antigravity ", " metal ", " brightmetal") ? " hard " : " ground ");
     }
     for (Wheel wheel : V.wheels) {
      boolean inX = Math.abs(wheel.X - trackX) <= radiusX, inZ = Math.abs(wheel.Z - trackZ) <= radiusZ;
      if (Math.abs(wheel.Y - (trackY + gravityCompensation)) <= trackPlane.radiusY) {
       if (inX && inZ) {
        if (isTree) {
         wheel.speedX -= U.random(3.) * wheel.speedX * VE.tick;
         wheel.speedY -= U.random(3.) * wheel.speedY * VE.tick;
         wheel.speedZ -= U.random(3.) * wheel.speedZ * VE.tick;
         wheel.againstWall = true;
        } else if (gate) {
         if (trackPlane.type.contains(" slowgate ")) {
          if (Math.abs(trackPlane.YZ) == 90) {
           if (Math.abs(wheel.speedZ) > V.topSpeeds[0]) {
            wheel.speedZ *= .333;
            V.VA.gate.play(1, V.VA.distanceVehicleToCamera);
           }
          } else if (Math.abs(trackPlane.XY) == 90) {
           if (Math.abs(wheel.speedX) > V.topSpeeds[0]) {
            wheel.speedX *= .333;
            V.VA.gate.play(1, V.VA.distanceVehicleToCamera);
           }
          }
         } else {
          wheel.speedZ *= Math.abs(trackPlane.YZ) == 90 ? 3 : 1;
          wheel.speedX *= Math.abs(trackPlane.XY) == 90 ? 3 : 1;
          speed *= (speed > 0 && speed < V.topSpeeds[1]) || (speed < 0 && speed > -V.topSpeeds[0]) ? 1.25 : 1;
          if (wheel.speedX != 0 || wheel.speedZ != 0) {
           V.VA.gate.play(0, V.VA.distanceVehicleToCamera);
          }
         }
        } else if (trackProperties.contains(" antigravity ")) {
         wheel.speedY -= E.gravity * 2 * VE.tick;
         onAntiGravity = true;
        } else if (!isWall) {
         if (trackPlane.YZ == 0 && trackPlane.XY == 0 && wheel.Y > trackY - 5) {//'- 5' is for better traction control--not to be used for stationary objects. Do not transfer '-5' to any assignments
          mode = Mode.drive;
          wheel.Y = Math.min(localVehicleGround, trackY);
          if (flipped && trackProperties.contains(" hard ")) {
           wheel.sparks(true);
          }
          if (crashLand) {
           crash(wheel.speedY * bounceBackForce * .1);
          }
          if (wheel.speedY > 100) {
           V.deployDust(wheel, true);
           V.VA.land();
          }
          if (wheel.speedY > 0) {
           wheel.speedY *= V.destroyed ? 0 : -V.bounce * flatPlaneBounce;
          }
          wheel.XY -= wheel.XY * .25;
          wheel.YZ -= wheel.YZ * .25;
          wheel.minimumY = trackY;
         } else if (trackPlane.YZ != 0) {
          double setY = trackY + (wheel.Z - trackZ) * (trackPlane.radiusY / trackPlane.radiusZ) * (trackPlane.YZ > 0 ? 1 : trackPlane.YZ < 0 ? -1 : 0);
          if (wheel.Y >= setY - 100) {
           wheel.angledSurface = true;
           mode = Mode.drive;
           if (!trackProperties.contains(" hard ")) {
            V.deployDust(wheel, false);
           } else if (flipped) {
            wheel.sparks(true);
           }
           if (V.Y >= trackY || wheel.speedY >= 0 || Math.abs(speed) < E.gravity * 4 * VE.tick || Math.abs((U.cos(V.XZ) > 0 ? -V.YZ : V.YZ) - trackPlane.YZ) < 30) {
            wheel.YZ += (-trackPlane.YZ * U.cos(V.XZ) - wheel.YZ) * .25;
            wheel.Y = setY;
           }
           wheel.XY += (trackPlane.YZ * U.sin(V.XZ) - wheel.XY) * .25;
          }
         } else if (trackPlane.XY != 0) {
          double setY = trackY + (wheel.X - trackX) * (trackPlane.radiusY / trackPlane.radiusX) * (trackPlane.XY > 0 ? 1 : trackPlane.XY < 0 ? -1 : 0);
          if (wheel.Y >= setY - 100) {
           wheel.angledSurface = true;
           mode = Mode.drive;
           if (!trackProperties.contains(" hard ")) {
            V.deployDust(wheel, false);
           } else if (flipped) {
            wheel.sparks(true);
           }
           if (V.Y >= trackY || wheel.speedY >= 0 || Math.abs(speed) < E.gravity * 4 * VE.tick || (Math.abs((U.sin(V.XZ) < 0 ? -V.YZ : V.YZ) - trackPlane.XY) < 30)) {
            wheel.YZ += (trackPlane.XY * U.sin(V.XZ) - wheel.YZ) * .25;
            wheel.Y = setY;
           }
           wheel.XY += (trackPlane.XY * U.cos(V.XZ) - wheel.XY) * .25;
          }
         }
        }
       }
       if (isWall) {
        double vehicleRadius = V.collisionRadius() * .5, contactX = trackPlane.radiusX + vehicleRadius, contactZ = trackPlane.radiusZ + vehicleRadius;
        if (inX && Math.abs(wheel.Z - trackZ) <= contactZ) {
         if (trackPlane.wall == TrackPlane.Wall.front && wheel.Z < trackZ + contactZ && wheel.speedZ < 0) {
          for (Wheel otherWheel : V.wheels) {
           otherWheel.Z -= wheel != otherWheel && otherWheel.Z >= trackZ + contactZ ? wheel.Z - (trackZ + contactZ) : 0;
          }
          wheel.Z = trackZ + contactZ;
          if (trackProperties.contains(" hard ")) {
           wheel.sparks(false);
          }
          crash(wheel.speedZ * trackPlane.damage * .1);
          wheel.speedZ += Math.abs(wheel.speedZ) * V.bounce * wallPlaneBounce;
          spinnerHit = wheel.againstWall = true;
         }
         if (trackPlane.wall == TrackPlane.Wall.back && wheel.Z > trackZ - contactZ && wheel.speedZ > 0) {
          for (Wheel otherWheel : V.wheels) {
           otherWheel.Z -= wheel != otherWheel && otherWheel.Z <= trackZ - contactZ ? wheel.Z - (trackZ - contactZ) : 0;
          }
          wheel.Z = trackZ - contactZ;
          if (trackProperties.contains(" hard ")) {
           wheel.sparks(false);
          }
          crash(wheel.speedZ * trackPlane.damage * .1);
          wheel.speedZ -= Math.abs(wheel.speedZ) * V.bounce * wallPlaneBounce;
          spinnerHit = wheel.againstWall = true;
         }
        }
        if (inZ && Math.abs(wheel.X - trackX) <= contactX) {
         if (trackPlane.wall == TrackPlane.Wall.right && wheel.X < trackX + contactX && wheel.speedX < 0) {
          for (Wheel otherWheel : V.wheels) {
           otherWheel.X -= wheel != otherWheel && otherWheel.X >= trackX + contactX ? wheel.X - (trackX + contactX) : 0;
          }
          wheel.X = trackX + contactX;
          if (trackProperties.contains(" hard ")) {
           wheel.sparks(false);
          }
          crash(wheel.speedX * trackPlane.damage * .1);
          wheel.speedX += Math.abs(wheel.speedX) * V.bounce * wallPlaneBounce;
          spinnerHit = wheel.againstWall = true;
         }
         if (trackPlane.wall == TrackPlane.Wall.left && wheel.X > trackX - contactX && wheel.speedX > 0) {
          for (Wheel otherWheel : V.wheels) {
           otherWheel.X -= wheel != otherWheel && otherWheel.X <= trackX - contactX ? wheel.X - (trackX - contactX) : 0;
          }
          wheel.X = trackX - contactX;
          if (trackProperties.contains(" hard ")) {
           wheel.sparks(false);
          }
          crash(wheel.speedX * trackPlane.damage * .1);
          wheel.speedX -= Math.abs(wheel.speedX) * V.bounce * wallPlaneBounce;
          spinnerHit = wheel.againstWall = true;
         }
        }
       }
      }
     }
    }
   }
  }
  if (V.spinner != null && spinnerHit) {
   V.spinner.hit(null);
  }
 }

 private void setTerrainFromTrackPlanes(double clearance, double gravityCompensation) {
  for (TrackPart trackPart : TE.trackParts) {
   if (!trackPart.trackPlanes.isEmpty() && U.distance(V.X, trackPart.X, V.Z, trackPart.Z) < trackPart.renderRadius + V.renderRadius) {
    for (TrackPlane trackPlane : trackPart.trackPlanes) {
     if (trackPlane.wall == TrackPlane.Wall.none && !trackPlane.type.contains(" tree ") && !trackPlane.type.contains("gate")) {
      double trackX = trackPlane.X + trackPart.X, trackY = trackPlane.Y + trackPart.Y, trackZ = trackPlane.Z + trackPart.Z;
      String addHard = U.contains(trackPlane.type, " paved ", " rock ", " grid ", " antigravity ", " metal ", " brightmetal") ? " hard " : " ground ";
      if (Math.abs(V.X - trackX) <= trackPlane.radiusX && Math.abs(V.Z - trackZ) <= trackPlane.radiusZ &&
      Math.abs(V.Y + clearance - (trackY + gravityCompensation)) <= trackPlane.radiusY) {
       if (trackPlane.YZ == 0 && trackPlane.XY == 0) {
        terrainProperties = trackPlane.type + addHard;
        for (Wheel wheel : V.wheels) {
         wheel.terrainRGB = trackPlane.RGB;
        }
       } else if (trackPlane.YZ != 0) {
        double setY = trackY + (V.Z - trackZ) * (trackPlane.radiusY / trackPlane.radiusZ) * (trackPlane.YZ > 0 ? 1 : trackPlane.YZ < 0 ? -1 : 0);
        if (V.Y >= setY - 100) {
         terrainProperties = trackPlane.type + addHard;
         for (Wheel wheel : V.wheels) {
          wheel.terrainRGB = trackPlane.RGB;
         }
        }
       } else if (trackPlane.XY != 0) {
        double setY = trackY + (V.X - trackX) * (trackPlane.radiusY / trackPlane.radiusX) * (trackPlane.XY > 0 ? 1 : trackPlane.XY < 0 ? -1 : 0);
        if (V.Y >= setY - 100) {
         terrainProperties = trackPlane.type + addHard;
         for (Wheel wheel : V.wheels) {
          wheel.terrainRGB = trackPlane.RGB;
         }
        }
       }
      }
     }
    }
   }
  }
 }
}
