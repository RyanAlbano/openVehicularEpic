package ve.vehicles;

import ve.*;
import ve.environment.E;
import ve.effects.*;
import ve.trackElements.Checkpoint;
import ve.trackElements.TE;
import ve.trackElements.trackParts.TrackPart;
import ve.utilities.U;

public class AI {

 private final Vehicle V;
 public int target;
 private double targetSpeed;
 private final int waitPoint;
 private int point;
 private double directionXZ, directionYZ, precisionXZ, precisionYZ;
 private double vehicleTurretDirectionXZ, vehicleTurretDirectionYZ;
 private double aimAheadTarget;
 private boolean againstWall;
 private double nukeWait;
 boolean skipStunts;
 private boolean runTrackBackwards;
 private final boolean guardCheckpoint;
 private boolean squareAgainstWall;
 private boolean attacking, shooting;
 private boolean atGuardedCheckpoint;
 private long wallTurn;
 final long[] airRotationDirection = new long[2];

 public enum Behavior {adapt, race, fight}

 AI(Vehicle vehicle) {
  V = vehicle;
  target = U.random(VE.vehiclesInMatch);
  airRotationDirection[0] = U.random() < .5 ? 1 : -1;
  airRotationDirection[1] = U.random() < .5 ? 1 : -1;
  guardCheckpoint = VE.guardCheckpoint && V.vehicleType != Vehicle.Type.turret && V.topSpeeds[1] < 200;
  nukeWait = V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name()) ? U.random(VE.matchLength) : nukeWait;
  waitPoint = U.random(TE.checkpoints.size());
 }

 public void run() {
  if (V.index == VE.userPlayer) {
   if (V.turretAutoAim) {
    for (Special special : V.specials) {
     if (special.aimType == Special.AimType.auto) {
      special.fire = false;
     }
    }
    runAutoAim();
    attacking = true;
    for (Special special : V.specials) {
     runAimAndShoot(special);
    }
   }
  } else {
   V.drive = V.reverse = V.turnL = V.turnR = V.handbrake = V.boost = false;
   for (Special special : V.specials) {
    special.fire = false;
   }
   boolean needRace =
   V.index < VE.vehiclesInMatch >> 1 ? VE.scoreCheckpoint[0] <= VE.scoreCheckpoint[1] || VE.scoreLap[0] < VE.scoreLap[1] : VE.scoreCheckpoint[1] <= VE.scoreCheckpoint[0] || VE.scoreLap[1] < VE.scoreLap[0];
   againstWall = V.wheels.get(0).againstWall || V.wheels.get(1).againstWall || V.wheels.get(2).againstWall || V.wheels.get(3).againstWall;
   long scoreStunt0 = Math.round(VE.scoreStunt[0] * .0005), scoreStunt1 = Math.round(VE.scoreStunt[1] * .0005);
   skipStunts = !V.landStuntsBothSides && !TE.checkpoints.isEmpty() && !VE.mapName.equals("SUMMIT of EPIC") &&
   (V.index < VE.vehiclesInMatch >> 1 ? scoreStunt0 > scoreStunt1 : scoreStunt1 > scoreStunt0);
   if (runTrackBackwards) {
    int setPoint = point - 1;
    while (setPoint < 0) setPoint += TE.points.size();
    if (U.distance(V.X, TE.points.get(point).X, V.Z, TE.points.get(point).Z) < Math.max(500, V.collisionRadius) || U.distance(V.X, TE.points.get(setPoint).X, V.Z, TE.points.get(setPoint).Z) < U.distance(V.X, TE.points.get(point).X, V.Z, TE.points.get(point).Z)) {
     point = --point < 0 ? TE.points.size() - 1 : point;
    }
   } else {
    point = V.point;
   }
   if (V.vehicleType == Vehicle.Type.turret || shooting || U.listEquals(VE.mapName, "Vehicular Falls", "XY Land", "the Tunnel of Doom", "SUMMIT of EPIC")) {
    precisionXZ = precisionYZ = VE.tick;
   } else {
    precisionXZ = precisionYZ = 5;
    if (!attacking && TE.points.size() > 1 && !VE.mapName.contains("Circle")) {
     int n = V.point + 1;
     while (n >= TE.points.size()) n -= TE.points.size();
     double pointX = TE.points.get(n).X, pointZ = TE.points.get(n).Z, racePath = (pointX - V.X >= 0 ? 270 : 90) + U.arcTan((pointZ - V.Z) / (pointX - V.X));
     while (Math.abs(V.XZ - racePath) > 180) {
      racePath += racePath < V.XZ ? 360 : -360;
     }
     precisionXZ = (V.XZ > directionXZ && racePath > directionXZ) || (V.XZ < directionXZ && racePath < directionXZ) ? VE.tick : precisionXZ;
    }
   }
   if (!runTrackBackwards && V.vehicleType != Vehicle.Type.turret && !TE.points.isEmpty() && V.topSpeeds[1] < 200) {
    point = U.random(Math.max(1, TE.points.size() - 1));
    runTrackBackwards = true;
   }
   boolean racing = (V.behavior == Behavior.race || (V.behavior == Behavior.adapt && needRace)) &&
   (!TE.checkpoints.isEmpty() || (V.behavior == Behavior.race && !V.hasShooting)) && !V.wrathEngaged;
   if (!attacking && !racing && target != V.index && !VE.vehicles.get(target).destroyed) {
    attacking = true;
    aimAheadTarget = U.random() < .5 ? U.random(.75) : U.random(.00333) * VE.vehicles.get(target).netSpeed;
   }
   runSwitchTarget();
   attacking = (!attacking || (!VE.vehicles.get(target).destroyed && !racing)) && attacking;
   if (attacking) {
    targetSpeed = V.hasShooting ? U.distance(V, VE.vehicles.get(target)) * .01 : Double.POSITIVE_INFINITY;
   } else {
    targetSpeed = VE.speedLimitAI;
   }
   if (runTrackBackwards && !V.hasShooting) {
    aimAheadTarget += U.random() < .05 ? U.randomPlusMinus(10.) : 0;
    aimAheadTarget = aimAheadTarget < 0 || aimAheadTarget > 10 ? U.random(10.) : aimAheadTarget;
   }
   shooting = false;
   for (Special special : V.specials) {//<-'shooting' gets engaged within block
    if (special.type == Vehicle.specialType.forcefield) {
     runForcefieldStrike(special);
    } else if (special.type == Vehicle.specialType.mine) {
     runMineDeploy(special);
    } else {
     runAimAndShoot(special);
    }
   }
   aimAheadTarget = V.grip < 100 && !V.hasShooting ? Math.max(aimAheadTarget, VE.vehicles.get(target).netSpeed * .0025) : aimAheadTarget;
   aimAheadTarget = aimAheadTarget < 0 || VE.vehicles.get(target).vehicleType == Vehicle.Type.turret ? 0 : aimAheadTarget;
   if (guardCheckpoint && !atGuardedCheckpoint) {
    directionXZ = (TE.checkpoints.get(waitPoint).X - V.X >= 0 ? 270 : 90) + U.arcTan((TE.checkpoints.get(waitPoint).Z - V.Z) / (TE.checkpoints.get(waitPoint).X - V.X));
   } else if (attacking) {
    Vehicle otherV = VE.vehicles.get(target);
    boolean targetDriveActive = !U.startsWith(otherV.mode.name(), Vehicle.Mode.neutral.name(), Vehicle.Mode.stunt.name());
    double addAim = U.distance(V.X, otherV.X, V.Z, otherV.Z) * aimAheadTarget,
    pX = otherV.X, pZ = otherV.Z;
    if (targetDriveActive) {
     if (otherV.speed > 0 && otherV.speed >= V.speed) {
      pX -= addAim * U.sin(otherV.XZ);
      pZ += addAim * U.cos(otherV.XZ);
     } else if (otherV.speed < 0 && otherV.speed <= V.speed) {
      pX += addAim * U.sin(otherV.XZ);
      pZ -= addAim * U.cos(otherV.XZ);
     }
    }
    directionXZ = (pX - V.X >= 0 ? 270 : 90) + U.arcTan((pZ - V.Z) / (pX - V.X));
    if (V.mode == Vehicle.Mode.fly || V.vehicleType == Vehicle.Type.turret) {
     addAim = U.distance(V, otherV) * .02 * aimAheadTarget;
     double aimY = otherV.Y;
     if (targetDriveActive) {
      aimY -= (otherV.speed > 0 && otherV.speed >= V.speed ? 1 : otherV.speed < 0 && otherV.speed <= V.speed ? -1 : 0) * addAim * U.sin(otherV.YZ);
     }
     directionYZ = otherV.Y == V.Y ? 0 : -((otherV.Y < V.Y ? -90 : 90) - U.arcTan(U.netValue(otherV.Z - V.Z, otherV.X - V.X) / (aimY - V.Y)));
     if (Math.abs(directionYZ - V.YZ) > precisionYZ) {
      if (directionYZ > V.YZ) {
       V.drive = false;
       V.reverse = true;
      } else if (directionYZ < V.YZ) {
       V.reverse = false;
       V.drive = true;
      }
     }
     if (V.Y > -1 - V.turretBaseY + V.localVehicleGround && V.YZ < 0) {
      V.drive = false;
      V.reverse = true;
     }
    }
    runAutoAim();
   } else {
    boolean hasSize = !TE.points.isEmpty();
    double nX = hasSize ? TE.points.get(point).X : 0, nZ = hasSize ? TE.points.get(point).Z : 0;
    directionXZ = (nX - V.X >= 0 ? 270 : 90) + U.arcTan((nZ - V.Z) / (nX - V.X));
   }
   while (Math.abs(V.XZ - directionXZ) > 180) {
    directionXZ += directionXZ < V.XZ ? 360 : -360;
   }
   if (V.mode != Vehicle.Mode.stunt) {
    V.drive = (V.mode != Vehicle.Mode.fly && V.vehicleType != Vehicle.Type.turret && V.speed < targetSpeed) || V.drive;
    V.drive2 = V.mode == Vehicle.Mode.fly && V.speed < targetSpeed || V.drive2;//<-'V.speed' over 'V.netSpeed' as it is stall-proof
    V.reverse2 = V.vehicleType != Vehicle.Type.aircraft && V.reverse2;
    runSteering();
    runWallHits();
   }
   runFlight();
   runVehicleStunts();
   if (guardCheckpoint && U.distance(V.X, TE.checkpoints.get(waitPoint).X, V.Z, TE.checkpoints.get(waitPoint).Z) < 500) {
    atGuardedCheckpoint = true;
    if (U.distance(V, VE.vehicles.get(target)) > 500) {
     V.drive = V.reverse = false;
     V.handbrake = true;
    }
   } else {
    atGuardedCheckpoint = false;
   }
   if (nukeWait > 0) {
    V.drive = V.reverse = false;
    nukeWait -= VE.tick;
   }
   for (Special special : V.specials) {
    if (special.type == Vehicle.specialType.phantom) {
     for (Vehicle vehicle : VE.vehicles) {
      if (!U.sameVehicle(V, vehicle) && !U.sameTeam(V, vehicle)) {
       for (Special otherSpecial : vehicle.specials) {
        for (Shot shot : otherSpecial.shots) {
         if (shot.stage > 0 && U.distance(shot, V) < 2000 + shot.absoluteRadius) {
          special.fire = true;
          break;
         }
        }
        special.fire = (otherSpecial.fire && otherSpecial.type == Vehicle.specialType.particledisintegrator) || special.fire;
       }
       for (Explosion explosion : vehicle.explosions) {
        if (explosion.stage > 0 && U.distance(explosion, V) < 2000 + explosion.absoluteRadius) {
         special.fire = true;
         break;
        }
       }
       special.fire = (vehicle.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name()) && U.distance(V, vehicle) < 25000) ||
       special.fire;
      }
     }
    }
   }
   runJuke();
  }
 }

 private void runSwitchTarget() {
  Vehicle targetVehicle = VE.vehicles.get(target);
  if (target == V.index || targetVehicle.destroyed || U.sameTeam(V.index, target) ||
  ((targetVehicle.damageDealt[U.random(4)] >= 100 || !Double.isNaN(targetVehicle.spinnerSpeed)) && !V.hasShooting && V.damageDealt[U.random(4)] < 100 && !V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name()) && !V.wrathEngaged) ||//<-Not worth attacking
  (E.viewableMapDistance < Double.POSITIVE_INFINITY && V.vehicleType != Vehicle.Type.turret && U.distance(V, targetVehicle) > E.viewableMapDistance) ||//<-Out of visible range
  (runTrackBackwards && !guardCheckpoint && !V.hasShooting && U.distance(V.X, targetVehicle.X, V.Z, targetVehicle.Z) > Math.min(E.viewableMapDistance, 10000)) ||//<-Too far away when running track backwards
  (targetVehicle.explosionType == Vehicle.ExplosionType.maxnuclear && V.explosionType != Vehicle.ExplosionType.maxnuclear)) {//<-Don't attack max nukes unless max nuke
   attacking = false;
   target = U.random(VE.vehiclesInMatch);
   if (U.random() < .5) {
    for (Vehicle vehicle : VE.vehicles) {
     if (!U.sameVehicle(V, vehicle) && !U.sameTeam(V, vehicle) && VE.bonusHolder == vehicle.index) {
      target = vehicle.index;
     }
    }
   }
  }
 }

 private void runSteering() {
  if (Math.abs(V.XZ - directionXZ) > precisionXZ) {
   V.turnL = V.XZ < directionXZ;
   V.turnR = V.XZ > directionXZ;
   double checkpointDistance = TE.checkpoints.isEmpty() ? 0 : U.distance(V.X, TE.checkpoints.get(V.checkpointsPassed).X, V.Z, TE.checkpoints.get(V.checkpointsPassed).Z);
   long steerType = V.vehicleType == Vehicle.Type.aircraft && attacking && VE.vehicles.get(target).mode == Vehicle.Mode.fly ? 1 : V.grip > 100 ? -1 : 0;
   boolean flying = V.mode == Vehicle.Mode.fly,
   turningInTheRoad = !attacking && V.maxTurn < 18 && !againstWall && !TE.checkpoints.isEmpty() && checkpointDistance < 2545 && Math.abs(V.XZ - directionXZ) > V.maxTurn;
   if (steerType < 1 && V.vehicleType != Vehicle.Type.turret &&
   Math.abs(V.XZ - directionXZ) >= U.clamp(V.maxTurn, !TE.checkpoints.isEmpty() && !attacking ? checkpointDistance * .001 : 50, 90) &&
   V.speed > Math.max(50, V.accelerationStages[0] * VE.tick) * (turningInTheRoad ? -1 : 1)) {
    if (flying) {
     V.drive2 = false;
     V.reverse2 = true;
    } else {
     V.drive = false;
    }
    if (steerType == 0) {
     if (!flying) {
      V.reverse = true;
     }
    } else if (!flying && V.mode != Vehicle.Mode.neutral) {
     V.handbrake = true;
    }
    if (turningInTheRoad) {
     if (!flying) {
      V.reverse = true;
     }
     if (V.speed <= 0) {
      if (!flying) {
       V.handbrake = false;
      }
      if (V.speed < 0) {
       V.turnL = !V.turnL;
       V.turnR = !V.turnR;
      }
     }
    }
   }
  }
 }

 private void runVehicleStunts() {
  if (V.vehicleType == Vehicle.Type.vehicle && (V.mode == Vehicle.Mode.neutral || V.mode == Vehicle.Mode.stunt) && !U.listEquals(VE.mapName, "the Tunnel of Doom", "Devil's Stairwell")) {
   boolean vehicularFalls = VE.mapName.equals("Vehicular Falls");
   double height = -300 - V.localVehicleGround;
   if (vehicularFalls) {
    height = -1010500 - ((V.X < -15000 && V.Z > 15000) || (V.X > 15000 && V.Z < -15000) ? 10000 : 0);
   }
   if (!skipStunts && ((V.Y + V.clearanceY < height && U.netValue(V.stuntXY, V.stuntXZ, V.stuntYZ) <= 4000 && (V.netSpeedY <= 0 || V.landStuntsBothSides)) ||
   (!vehicularFalls && V.Y < -V.absoluteRadius * 256))) {
    V.handbrake = (!VE.mapName.equals("SUMMIT of EPIC") || V.Y < -1010500) || V.handbrake;
    if (V.mode == Vehicle.Mode.stunt) {
     V.handbrake = false;
     airRotationDirection[0] = U.listEquals(VE.mapName, "Laps of Glory", "V.E. Speedway 2000000") ? 1 : airRotationDirection[0];
     V.drive = airRotationDirection[0] > 0;
     V.reverse = airRotationDirection[0] < 0;
     V.turnR = airRotationDirection[1] > 0;
     V.turnL = airRotationDirection[1] < 0;
     if (vehicularFalls) {
      V.reverse = true;
      V.drive = V.turnL = V.turnR = false;
     } else if (VE.mapName.equals("XY Land")) {
      V.drive = true;
      V.reverse = V.turnL = V.turnR = false;
     }
    }
   } else {
    if (V.mode == Vehicle.Mode.stunt) {
     V.turnL = V.XY > 20;
     V.turnR = V.XY < -20;
     V.reverse = V.YZ < -20;
     V.drive = V.YZ > 20;
    } else {
     V.handbrake = Math.abs(V.XY) > 30 || Math.abs(V.YZ) > 30;
    }
   }
   if (V.damage > 0 && V.airPush > 0 && !vehicularFalls) {
    for (TrackPart trackPart : VE.trackParts) {
     if (trackPart.isFixpoint && U.distance(V, trackPart) < 10000) {
      V.handbrake = V.Y + V.clearanceY < -300 - V.localVehicleGround && V.mode != Vehicle.Mode.stunt;
      if (Math.abs(V.netSpeedZ) > Math.abs(V.netSpeedX) && ((V.netSpeedZ > 0 && trackPart.Z > V.Z) || (V.netSpeedZ < 0 && trackPart.Z < V.Z))) {
       V.drive = false;
       V.reverse = trackPart.Y < V.Y - Math.min(Math.abs(V.speed), 500);
       if (Math.abs(V.YZ) < 90) {
        if (Math.abs(V.XZ) < 90) {
         V.turnR = trackPart.X > V.X;
         V.turnL = trackPart.X < V.X;
        }
        if (V.XZ > 90 || V.XZ < -90) {
         V.turnR = trackPart.X < V.X;
         V.turnL = trackPart.X > V.X;
        }
       } else {
        if (Math.abs(V.XZ) < 90) {
         V.turnR = trackPart.X < V.X;
         V.turnL = trackPart.X > V.X;
        }
        if (V.XZ > 90 || V.XZ < -90) {
         V.turnR = trackPart.X > V.X;
         V.turnL = trackPart.X < V.X;
        }
       }
      }
      if (Math.abs(V.netSpeedX) > Math.abs(V.netSpeedZ) && ((V.netSpeedX > 0 && trackPart.X > V.X) || (V.netSpeedX < 0 && trackPart.X < V.X))) {
       V.drive = false;
       V.reverse = trackPart.Y < V.Y;
       if (Math.abs(V.YZ) < 90) {
        if (V.XZ < 0) {
         V.turnR = trackPart.Z < V.Z;
         V.turnL = trackPart.Z > V.Z;
        } else if (V.XZ > 0) {
         V.turnR = trackPart.Z > V.Z;
         V.turnL = trackPart.Z < V.Z;
        }
       } else if (V.XZ < 0) {
        V.turnR = trackPart.Z > V.Z;
        V.turnL = trackPart.Z < V.Z;
       } else if (V.XZ > 0) {
        V.turnR = trackPart.Z < V.Z;
        V.turnL = trackPart.Z > V.Z;
       }
      }
     }
    }
   }
   if (VE.bonusHolder != V.index && U.distance(V.X, VE.bonusX, V.Y, VE.bonusY, V.Z, VE.bonusZ) < 10000) {
    V.handbrake = V.Y + V.clearanceY < -300 - V.localVehicleGround && V.mode != Vehicle.Mode.stunt;
    if (Math.abs(V.netSpeedZ) > Math.abs(V.netSpeedX) && ((V.netSpeedZ > 0 && VE.bonusZ > V.Z) || (V.netSpeedZ < 0 && VE.bonusZ < V.Z))) {
     V.drive = false;
     V.reverse = VE.bonusY < V.Y - Math.min(Math.abs(V.speed), 500);
     if (Math.abs(V.YZ) < 90) {
      if (Math.abs(V.XZ) < 90) {
       V.turnR = VE.bonusX > V.X;
       V.turnL = VE.bonusX < V.X;
      }
      if (V.XZ > 90 || V.XZ < -90) {
       V.turnR = VE.bonusX < V.X;
       V.turnL = VE.bonusX > V.X;
      }
     } else {
      if (Math.abs(V.XZ) < 90) {
       V.turnR = VE.bonusX < V.X;
       V.turnL = VE.bonusX > V.X;
      }
      if (V.XZ > 90 || V.XZ < -90) {
       V.turnR = VE.bonusX > V.X;
       V.turnL = VE.bonusX < V.X;
      }
     }
    }
    if (Math.abs(V.netSpeedX) > Math.abs(V.netSpeedZ) && ((V.netSpeedX > 0 && VE.bonusX > V.X) || (V.netSpeedX < 0 && VE.bonusX < V.X))) {
     V.drive = false;
     V.reverse = VE.bonusY < V.Y;
     if (Math.abs(V.YZ) < 90) {
      if (V.XZ < 0) {
       V.turnR = VE.bonusZ < V.Z;
       V.turnL = VE.bonusZ > V.Z;
      } else if (V.XZ > 0) {
       V.turnR = VE.bonusZ > V.Z;
       V.turnL = VE.bonusZ < V.Z;
      }
     } else if (V.XZ < 0) {
      V.turnR = VE.bonusZ > V.Z;
      V.turnL = VE.bonusZ < V.Z;
     } else if (V.XZ > 0) {
      V.turnR = VE.bonusZ < V.Z;
      V.turnL = VE.bonusZ > V.Z;
     }
    }
   }
   if (vehicularFalls && againstWall) {
    V.drive = false;
    V.reverse = true;
    V.handbrake = V.mode == Vehicle.Mode.neutral || V.handbrake;
   }
  }
 }

 private void runFlight() {
  if (V.vehicleType == Vehicle.Type.aircraft) {
   //V.drive2 = V.mode == Vehicle.Mode.neutral || V.drive2;
   if (V.mode == Vehicle.Mode.fly) {
    long extraY = 350;
    V.handbrake = Math.abs(V.XZ - directionXZ) <= 90;
    if (!TE.checkpoints.isEmpty() && !attacking) {
     directionYZ = TE.checkpoints.get(V.checkpointsPassed).Y - extraY == V.Y ? directionYZ : -((TE.checkpoints.get(V.checkpointsPassed).Y - extraY < V.Y ? -90 : 90) - U.arcTan(U.netValue(TE.checkpoints.get(V.checkpointsPassed).Z - V.Z, TE.checkpoints.get(V.checkpointsPassed).X - V.X) / (TE.checkpoints.get(V.checkpointsPassed).Y - extraY - V.Y)));
     if (Math.abs(directionYZ - V.YZ) > precisionYZ) {
      if (directionYZ > V.YZ) {
       V.drive = false;
       V.reverse = true;
      } else if (directionYZ < V.YZ) {
       V.reverse = false;
       V.drive = true;
      }
     }
    }
    if (Math.abs(V.XY) > 80 && Math.abs(V.XY) < 100) {
     if ((V.XZ > directionXZ && V.XY < 0) || (V.XZ < directionXZ && V.XY > 0)) {
      V.reverse = false;
      V.drive = true;
     } else if ((V.XZ < directionXZ && V.XY < 0) || (V.XZ > directionXZ && V.XY > 0)) {
      V.drive = false;
      V.reverse = true;
     }
    }
    V.turnR = !(V.XY >= 80) && V.turnR;
    V.turnL = !(V.XY <= -80) && V.turnL;
    V.turnL = V.XY >= 90 || V.turnL;
    V.turnR = V.XY <= -90 || V.turnR;
    V.drive = !(V.YZ <= -80) && V.drive;
    V.reverse = !(V.YZ >= 80) && V.reverse;
    V.reverse = V.YZ <= -90 || V.reverse;
    V.drive = V.YZ >= 90 || V.drive;
    if (attacking) {//<-While non-attacking is managed in the steering void
     V.drive2 = V.netSpeed < targetSpeed;
     V.reverse2 = V.speed > targetSpeed;//<-Don't use 'V.netSpeed' or they'll start flying backwards!
    }
    if (V.engine == Vehicle.Engine.jetfighter) {
     V.boost = ((!againstWall && V.speed < 400) ||
     (attacking && (VE.vehicles.get(target).netSpeed >= V.netSpeed || U.distance(V.X, VE.vehicles.get(target).X, V.Z, VE.vehicles.get(target).Z) > 250000))) ||
     V.boost;
     double altitudeFloor = V.localVehicleGround - (E.boulders.isEmpty() ? 1000 : (E.boulders.get(0).S.getRadius() * 2) + (V.collisionRadius * 2));
     for (TrackPart trackPart : VE.trackParts) {
      altitudeFloor = trackPart.isFixpoint && altitudeFloor > trackPart.Y ? trackPart.Y : altitudeFloor;
     }
     if (attacking && V.Y > altitudeFloor) {
      if (Math.abs(V.XY) < 90) {
       V.drive = false;
       V.reverse = true;
      }
      V.turnL = V.XY > 0;
      V.turnR = V.XY < 0;
     }
    }
   }
   double engageTakeoffSpeed = Math.min(V.topSpeeds[1], targetSpeed);
   if (U.startsWith(V.mode.name(), Vehicle.Mode.drive.name(), Vehicle.Mode.neutral.name()) &&
   ((attacking && VE.vehicles.get(target).Y < V.Y - V.absoluteRadius && VE.vehicles.get(target).speed > 0 && V.speed >= engageTakeoffSpeed) ||
   !attacking || V.speedBoost > 0 || V.floats)) {
    V.drive2 = true;
    V.boost = V.speedBoost > 0 || V.boost;
   }
  }
 }

 private void runWallHits() {
  if (V.vehicleType != Vehicle.Type.turret) {
   if (againstWall && (!shooting || guardCheckpoint) && !VE.mapName.equals("Devil's Stairwell") && !(U.random() < .5 && U.listEquals(VE.mapName, "the Bottleneck", "the Test of Damage", "Matrix 2x3", "the Maze", "the Tunnel of Doom"))) {
    if (V.mode == Vehicle.Mode.fly) {
     V.drive = false;
     V.reverse = true;
    }
    if (wallTurn == 0 && ((V.wheels.get(0).againstWall && V.wheels.get(1).againstWall) || (V.wheels.get(2).againstWall && V.wheels.get(3).againstWall))) {
     wallTurn = U.random() < .5 ? 1 : -1;
     squareAgainstWall = true;
    }
    if (!squareAgainstWall) {
     wallTurn = (V.wheels.get(0).againstWall && !V.wheels.get(1).againstWall) || (V.wheels.get(2).againstWall && !V.wheels.get(3).againstWall) ? -1 : wallTurn;
     wallTurn = (V.wheels.get(1).againstWall && !V.wheels.get(0).againstWall) || (V.wheels.get(3).againstWall && !V.wheels.get(2).againstWall) ? 1 : wallTurn;
    }
   } else {
    squareAgainstWall = false;
   }
   if (wallTurn > 0) {
    V.turnR = false;
    V.turnL = true;
   } else if (wallTurn < 0) {
    V.turnL = false;
    V.turnR = true;
   }
   wallTurn = !squareAgainstWall && !againstWall ? 0 : wallTurn;
  }
 }

 private void runJuke() {
  if (V.vehicleType != Vehicle.Type.turret && !U.listEquals(VE.mapName, "XY Land", "Devil's Stairwell", "the Tunnel of Doom") && V.damageDealt[U.random(4)] < 100) {
   for (Vehicle vehicle : VE.vehicles) {
    double avoidDistance = V.collisionRadius + vehicle.collisionRadius + vehicle.othersAvoidAt;
    boolean hasSpinner = !Double.isNaN(vehicle.spinnerSpeed);
    avoidDistance += hasSpinner ? vehicle.renderRadius * 2 * Math.abs(vehicle.spinnerSpeed) : 0;
    if (!U.sameVehicle(V, vehicle) && !U.sameTeam(V, vehicle) && !vehicle.destroyed && U.distance(V, vehicle) < avoidDistance &&
    (hasSpinner || ((vehicle.damageDealt[U.random(4)] >= 100 || (!VE.mapName.equals("SUMMIT of EPIC") && !attacking && ((V.fragility > .5 || vehicle.durability > V.durability) && V.damageDealt[U.random(4)] < 2))) &&
    (vehicle.damageDealt[U.random(4)] > 3.75 || !U.listEquals(VE.mapName, "Laps of Glory", "the Checkpoint!", "Vehicular Falls", "Zip n' Cross", "the Bottleneck", "Railing Against", "the Test of Damage", "Matrix 2x3"))))) {
     if (V.mode != Vehicle.Mode.fly) {
      V.drive = true;
      V.reverse = V.handbrake = false;
     }
     boolean usePhantom = false;
     for (Special special : V.specials) {
      if (special.type == Vehicle.specialType.phantom) {
       special.fire = usePhantom = true;
       break;
      }
     }
     if (!usePhantom) {
      double jukeAngle = (vehicle.X - V.X >= 0 ? 270 : 90) + U.arcTan((vehicle.Z - V.Z) / (vehicle.X - V.X));
      while (jukeAngle < 0) jukeAngle += 360;
      while (jukeAngle > 180) jukeAngle -= 360;
      jukeAngle = Math.abs(V.XZ - jukeAngle);
      if ((jukeAngle > 180 ? Math.abs(jukeAngle - 360) : jukeAngle) < 90) {
       if (V.mode != Vehicle.Mode.fly || Math.abs(V.XY) < 90) {
        if (Math.abs(V.netSpeedZ) > Math.abs(V.netSpeedX)) {
         if (V.netSpeedZ > 0) {
          if (V.X < vehicle.X) {
           V.turnR = false;
           V.turnL = true;
          } else if (V.X > vehicle.X) {
           V.turnL = false;
           V.turnR = true;
          }
         } else if (V.netSpeedZ < 0) {
          if (V.X < vehicle.X) {
           V.turnL = false;
           V.turnR = true;
          } else if (V.X > vehicle.X) {
           V.turnR = false;
           V.turnL = true;
          }
         }
        } else if (Math.abs(V.netSpeedX) > Math.abs(V.netSpeedZ)) {
         if (V.netSpeedX > 0) {
          if (V.Z < vehicle.Z) {
           V.turnL = false;
           V.turnR = true;
          } else if (V.Z > vehicle.Z) {
           V.turnR = false;
           V.turnL = true;
          }
         } else if (V.netSpeedX < 0) {
          if (V.Z < vehicle.Z) {
           V.turnR = false;
           V.turnL = true;
          } else if (V.Z > vehicle.Z) {
           V.turnL = false;
           V.turnR = true;
          }
         }
        }
       }
       if (V.mode == Vehicle.Mode.fly && Math.abs(V.YZ) < 90 && Math.abs(V.XY) < 90) {
        if (V.Y > vehicle.Y) {
         V.reverse = false;
         V.drive = true;
        } else if (V.Y < vehicle.Y) {
         V.drive = false;
         V.reverse = true;
        }
       }
      }
     }
    }
   }
  }
 }

 private void runAimAndShoot(Special special) {
  if (attacking && special.type != Vehicle.specialType.phantom && special.type != Vehicle.specialType.teleport) {
   double accuracyRangeXZ = special.aimType == Special.AimType.auto ? Math.abs(V.vehicleTurretXZ - vehicleTurretDirectionXZ) : Math.abs(V.XZ - directionXZ),
   accuracyRangeYZ = special.aimType == Special.AimType.auto ? Math.abs(V.vehicleTurretYZ - vehicleTurretDirectionYZ) : 0;
   boolean shoot = false;
   if (accuracyRangeXZ < special.AIAimPrecision && accuracyRangeYZ < special.AIAimPrecision) {
    for (Vehicle vehicle : VE.vehicles) {
     if (!U.sameVehicle(V, vehicle) && !U.sameTeam(V, vehicle) && !vehicle.destroyed) {
      shoot = true;
      break;
     }
    }
   }
   if (shoot) {
    special.fire = shooting = true;
    if (special.timer <= 0) {//<-Only recalibrate aimAhead when the gun actually fires
     aimAheadTarget =
     special.homing || special.type == Vehicle.specialType.particledisintegrator ? 0 ://<-None needed
     special.speed > 3000 ? Math.min(aimAheadTarget, U.random(.5)) ://<-Reduce for fast shots
     V.vehicleType == Vehicle.Type.turret ? VE.vehicles.get(target).netSpeed * .00333 - U.random() ://<-All turrets excluding railgun special
     special.speed == 3000 ? U.random() ://<-Normal gun speeds
     U.random(2.);//<-Everything else
    }
   }
  }
 }

 private void runMineDeploy(Special special) {
  special.fire = false;
  for (Checkpoint checkpoint : TE.checkpoints) {
   if (U.distance(V, checkpoint) < 500) {
    special.fire = true;
    break;
   }
  }
 }

 private void runForcefieldStrike(Special special) {
  for (Vehicle vehicle : VE.vehicles) {
   if (!U.sameVehicle(V, vehicle) && !U.sameTeam(V, vehicle) && !vehicle.destroyed && U.distance(V, vehicle) <= special.diameter) {
    special.fire = true;
    break;
   }
  }
 }

 private void runAutoAim() {
  if (V.turretAutoAim) {
   V.vehicleTurretL = V.vehicleTurretR = false;
   double turretZ = V.Z;//<-Elaborate later if needed
   Vehicle otherV = VE.vehicles.get(target);
   boolean targetDriveActive = !U.startsWith(otherV.mode.name(), Vehicle.Mode.neutral.name(), Vehicle.Mode.stunt.name());
   double addAim = U.distance(V.X, otherV.X, turretZ, otherV.Z) * aimAheadTarget,
   pX = otherV.X, pZ = otherV.Z;
   if (targetDriveActive) {
    if (otherV.speed > 0 && otherV.speed >= V.speed) {
     pX -= addAim * U.sin(otherV.XZ);
     pZ += addAim * U.cos(otherV.XZ);
    } else if (otherV.speed < 0 && otherV.speed <= V.speed) {
     pX += addAim * U.sin(otherV.XZ);
     pZ -= addAim * U.cos(otherV.XZ);
    }
   }
   vehicleTurretDirectionXZ = (pX - V.X >= 0 ? 270 : 90) + U.arcTan((pZ - turretZ) / (pX - V.X)) - V.XZ;
   while (Math.abs(V.vehicleTurretXZ - vehicleTurretDirectionXZ) > 180) {
    vehicleTurretDirectionXZ += vehicleTurretDirectionXZ < V.vehicleTurretXZ ? 360 : -360;
   }
   if (Math.abs(V.vehicleTurretXZ - vehicleTurretDirectionXZ) > precisionXZ) {
    V.vehicleTurretL = V.vehicleTurretXZ < vehicleTurretDirectionXZ;
    V.vehicleTurretR = V.vehicleTurretXZ > vehicleTurretDirectionXZ;
   }
   addAim = U.distance(V, otherV) * .02 * aimAheadTarget;
   double aimY = otherV.Y;
   if (targetDriveActive) {
    aimY -= (otherV.speed > 0 && otherV.speed >= V.speed ? 1 : otherV.speed < 0 && otherV.speed <= V.speed ? -1 : 0) * addAim * U.sin(otherV.YZ);
   }
   vehicleTurretDirectionYZ = otherV.Y == V.Y ? 0 : (otherV.Y < V.Y ? -90 : 90) - U.arcTan(U.netValue(otherV.Z - turretZ, otherV.X - V.X) / (aimY - V.Y));
   if (Math.abs(vehicleTurretDirectionYZ - V.vehicleTurretYZ) > precisionYZ) {
    if (vehicleTurretDirectionYZ > V.vehicleTurretYZ) {
     V.drive2 = false;
     V.reverse2 = true;
    } else if (vehicleTurretDirectionYZ < V.vehicleTurretYZ) {
     V.reverse2 = false;
     V.drive2 = true;
    }
   }
  }
 }
}
