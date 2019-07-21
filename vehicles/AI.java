package ve.vehicles;

import ve.*;
import ve.VE.mode;
import ve.environment.E;
import ve.effects.*;
import ve.trackparts.TrackPart;
import ve.utilities.U;

public class AI {

 private final Vehicle V;
 public int target;
 private final int waitPoint;
 private int point;
 private double directionXZ;
 private double directionYZ, precisionYZ;
 private double aimAheadTarget;
 private boolean againstWall;
 private double nukeWait;
 boolean skipStunts;
 private boolean runTrackBackwards;
 private final boolean guardCheckpoint;
 private boolean boosting;
 private boolean squareAgainstWall;
 private boolean shooting;
 private boolean attacking;
 private boolean atGuardedCheckpoint;
 private long wallTurn;
 final long[] airRotationDirection = new long[2];

 AI(Vehicle vehicle) {
  V = vehicle;
  target = U.random(VE.vehiclesInMatch);
  airRotationDirection[0] = U.random() < .5 ? 1 : -1;
  airRotationDirection[1] = U.random() < .5 ? 1 : -1;
  guardCheckpoint = VE.guardCheckpoint && V.vehicleType != VE.type.turret && V.topSpeeds[1] < 200;
  nukeWait = V.explosionType.contains("nuclear") ? U.random(VE.matchLength) : nukeWait;
  waitPoint = U.random(VE.checkpoints.size());
 }

 public void run() {
  V.drive = V.reverse = V.turnL = V.turnR = V.handbrake = V.useSpecial[0] = V.useSpecial[1] = V.boost = false;
  boolean needRace = (V.index < VE.vehiclesInMatch >> 1 && (VE.scoreCheckpoint[0] <= VE.scoreCheckpoint[1] || VE.scoreLap[0] < VE.scoreLap[1])) || (V.index >= VE.vehiclesInMatch >> 1 && (VE.scoreCheckpoint[1] <= VE.scoreCheckpoint[0] || VE.scoreLap[1] < VE.scoreLap[0]));
  againstWall = V.wheels.get(0).againstWall || V.wheels.get(1).againstWall || V.wheels.get(2).againstWall || V.wheels.get(3).againstWall;
  long scoreStunt0 = Math.round(VE.scoreStunt[0] * .0005), scoreStunt1 = Math.round(VE.scoreStunt[1] * .0005);
  skipStunts = !V.landStuntsBothSides && VE.checkpoints.size() > 0 && !VE.mapName.equals("SUMMIT of EPIC") && ((V.index < VE.vehiclesInMatch >> 1 && scoreStunt0 > scoreStunt1) || (V.index >= VE.vehiclesInMatch >> 1 && scoreStunt1 > scoreStunt0));
  if (runTrackBackwards) {
   int setPoint = point - 1;
   for (; setPoint < 0; setPoint += VE.points.size()) ;
   if (U.distance(V.X, VE.points.get(point).X, V.Z, VE.points.get(point).Z) < Math.max(500, V.collisionRadius) || U.distance(V.X, VE.points.get(setPoint).X, V.Z, VE.points.get(setPoint).Z) < U.distance(V.X, VE.points.get(point).X, V.Z, VE.points.get(point).Z)) {
    point = --point < 0 ? VE.points.size() - 1 : point;
   }
  } else {
   point = V.point;
  }
  double precisionXZ;
  if (V.vehicleType == VE.type.turret || shooting || U.listEquals(VE.mapName, "Vehicular Falls", "XY Land", "the Tunnel of Doom", "SUMMIT of EPIC")) {
   precisionXZ = precisionYZ = VE.tick;
  } else {
   precisionXZ = precisionYZ = 5;
   if (!attacking && VE.points.size() > 1 && !VE.mapName.contains("Circle")) {
    int n = V.point + 1;
    for (; n >= VE.points.size(); n -= VE.points.size()) ;
    double pointX = VE.points.get(n).X, pointZ = VE.points.get(n).Z, racePath = (pointX - V.X >= 0 ? 270 : 90) + U.arcTan((pointZ - V.Z) / (pointX - V.X));
    while (Math.abs(V.XZ - racePath) > 180) {
     racePath += racePath < V.XZ ? 360 : -360;
    }
    precisionXZ = (V.XZ > directionXZ && racePath > directionXZ) || (V.XZ < directionXZ && racePath < directionXZ) ? VE.tick : precisionXZ;
   }
  }
  if (!runTrackBackwards && V.vehicleType != VE.type.turret && VE.points.size() > 0 && V.topSpeeds[1] < 200) {
   point = U.random(Math.max(1, VE.points.size() - 1));
   runTrackBackwards = true;
  }
  boolean racing = (V.AIBehavior == VE.AIBehavior.race || (V.AIBehavior == VE.AIBehavior.adapt && needRace)) &&
  (VE.checkpoints.size() > 0 || (V.AIBehavior == VE.AIBehavior.race && V.specials.size() < 1)) && !V.wrathEngaged;
  if (!attacking && !racing && target != V.index && !VE.vehicles.get(target).destroyed) {
   attacking = true;
   aimAheadTarget = U.random() < .5 ? U.random(.75) : U.random(.00333) * VE.vehicles.get(target).netSpeed;
  }
  switchTarget();
  attacking = (!attacking || (!VE.vehicles.get(target).destroyed && !racing)) && attacking;
  if (runTrackBackwards && V.specials.size() < 1) {
   aimAheadTarget += U.random() < .05 ? U.randomPlusMinus(10.) : 0;
   aimAheadTarget = aimAheadTarget < 0 || aimAheadTarget > 10 ? U.random(10.) : aimAheadTarget;
  }
  for (Special special : V.specials) {
   if (Math.abs(V.XZ - directionXZ) < special.AIAimPrecision) {
    for (Vehicle vehicle : VE.vehicles) {
     if (!U.sameVehicle(V, vehicle) && !U.sameTeam(V, vehicle) && !vehicle.destroyed) {
      if (special.timer <= 0 && !U.startsWith(special.type, "phantom", "teleport") && ((attacking && !special.type.startsWith("forcefield")) || (special.type.startsWith("forcefield") && U.distance(V.X, vehicle.X, V.Y, vehicle.Y, V.Z, vehicle.Z) <= special.diameter))) {
       V.useSpecial[V.specials.indexOf(special)] = true;
       shooting = !special.type.startsWith("forcefield") || shooting;
      }
     }
    }
   }
   if (special.type.startsWith("mine")) {
    V.useSpecial[V.specials.indexOf(special)] = false;
    for (VE.Checkpoint checkpoint : VE.checkpoints) {
     if (U.distance(V.X, checkpoint.X, V.Y, checkpoint.Y, V.Z, checkpoint.Z) < 500) {
      V.useSpecial[V.specials.indexOf(special)] = true;
     }
    }
   }
   if (V.useSpecial[V.specials.indexOf(special)]) {
    aimAheadTarget = special.homing || special.type.startsWith("particledisintegrator") ? 0 :
    special.speed > 3000 ? Math.min(aimAheadTarget, U.random(.5)) :
    V.vehicleType == VE.type.turret ? VE.vehicles.get(target).netSpeed * .00333 - U.random() :
    special.speed == 3000 ? U.random() :
    !U.startsWith(special.type, "forcefield", "mine") ? U.random(2.) :
    aimAheadTarget;
   }
  }
  shooting = attacking && V.specials.size() >= 1 && shooting;
  aimAheadTarget = V.grip < 100 && V.specials.size() < 1 ? Math.max(aimAheadTarget, VE.vehicles.get(target).netSpeed * .0025) : aimAheadTarget;
  aimAheadTarget = aimAheadTarget < 0 || VE.vehicles.get(target).vehicleType == VE.type.turret ? 0 : aimAheadTarget;
  if (guardCheckpoint && !atGuardedCheckpoint) {
   directionXZ = (VE.checkpoints.get(waitPoint).X - V.X >= 0 ? 270 : 90) + U.arcTan((VE.checkpoints.get(waitPoint).Z - V.Z) / (VE.checkpoints.get(waitPoint).X - V.X));
  } else if (attacking) {
   double addAim = U.distance(V.X, VE.vehicles.get(target).X, V.Z, VE.vehicles.get(target).Z) * aimAheadTarget, pX = VE.vehicles.get(target).X, pZ = VE.vehicles.get(target).Z;
   if (!U.startsWith(VE.vehicles.get(target).mode.name(), "neutral", "stunt")) {
    if (VE.vehicles.get(target).speed > 0 && VE.vehicles.get(target).speed >= V.speed) {
     pX -= addAim * U.sin(VE.vehicles.get(target).XZ);
     pZ += addAim * U.cos(VE.vehicles.get(target).XZ);
    } else if (VE.vehicles.get(target).speed < 0 && VE.vehicles.get(target).speed <= V.speed) {
     pX += addAim * U.sin(VE.vehicles.get(target).XZ);
     pZ -= addAim * U.cos(VE.vehicles.get(target).XZ);
    }
   }
   directionXZ = (pX - V.X >= 0 ? 270 : 90) + U.arcTan((pZ - V.Z) / (pX - V.X));
   if (V.mode == mode.fly || V.vehicleType == VE.type.turret) {
    addAim = U.distance(V.X, VE.vehicles.get(target).X, V.Y, VE.vehicles.get(target).Y, V.Z, VE.vehicles.get(target).Z) * .02 * aimAheadTarget;
    double aimY = VE.vehicles.get(target).Y;
    if (!U.startsWith(VE.vehicles.get(target).mode.name(), "neutral", "stunt")) {
     aimY -= (VE.vehicles.get(target).speed > 0 && VE.vehicles.get(target).speed >= V.speed ? 1 : VE.vehicles.get(target).speed < 0 && VE.vehicles.get(target).speed <= V.speed ? -1 : 0) * addAim * U.sin(VE.vehicles.get(target).YZ);
    }
    directionYZ = VE.vehicles.get(target).Y != V.Y ? -((VE.vehicles.get(target).Y < V.Y ? -90 : 90) - U.arcTan(U.netValue(VE.vehicles.get(target).Z - V.Z, VE.vehicles.get(target).X - V.X) / (aimY - V.Y))) : 0;
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
  } else {
   boolean hasSize = VE.points.size() > 0;
   double nX = hasSize ? VE.points.get(point).X : 0, nZ = hasSize ? VE.points.get(point).Z : 0;
   directionXZ = (nX - V.X >= 0 ? 270 : 90) + U.arcTan((nZ - V.Z) / (nX - V.X));
  }
  if (V.mode != mode.stunt) {
   V.drive = V.mode != mode.fly && V.vehicleType != VE.type.turret && (V.speed < VE.speedLimitAI || attacking) || V.drive;
   while (Math.abs(V.XZ - directionXZ) > 180) {
    directionXZ += directionXZ < V.XZ ? 360 : -360;
   }
   if (Math.abs(V.XZ - directionXZ) > precisionXZ) {
    V.turnL = V.XZ < directionXZ || V.turnL;
    V.turnR = V.XZ > directionXZ || V.turnR;
    long steerType = attacking && V.vehicleType == VE.type.aircraft && VE.vehicles.get(target).mode == mode.fly ? 1 : V.grip > 100 ? -1 : 0;
    boolean turningInTheRoad = !attacking && VE.checkpoints.size() > 0 && V.maxTurn < 18 && V.maxTurn > -1 && !againstWall && U.distance(V.X, VE.checkpoints.get(V.checkpointsPassed).X, V.Z, VE.checkpoints.get(V.checkpointsPassed).Z) < 2545 && Math.abs(V.XZ - directionXZ) > V.maxTurn;
    if (Math.abs(V.XZ - directionXZ) >= Math.max(V.maxTurn, Math.min(VE.checkpoints.size() > 0 && !attacking ? U.distance(V.X, VE.checkpoints.get(V.checkpointsPassed).X, V.Z, VE.checkpoints.get(V.checkpointsPassed).Z) * .001 : 50, 90)) && V.speed > Math.max(50, V.accelerationStages[0] * VE.tick) * (turningInTheRoad ? -1 : 1) && steerType < 1 && V.mode != mode.fly && V.vehicleType != VE.type.turret && !(E.gravity == 0 && V.mode == mode.neutral)) {
     V.drive = false;
     if (steerType == 0) {
      V.reverse = true;
     } else if (V.mode != mode.neutral) {
      V.handbrake = true;
     }
     if (turningInTheRoad) {
      V.reverse = true;
      if (V.speed <= 0) {
       V.handbrake = false;
       if (V.speed < 0) {
        V.turnL = !V.turnL;
        V.turnR = !V.turnR;
       }
      }
     }
    }
   }
   if (V.vehicleType != VE.type.turret) {
    if (againstWall && (!shooting || guardCheckpoint) && !VE.mapName.equals("Devil's Stairwell") && !(U.random() < .5 && U.listEquals(VE.mapName, "the Bottleneck", "the Test of Damage", "2x2 Matrix", "the Maze", "the Tunnel of Doom"))) {
     if (V.mode == mode.fly) {
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
  if (guardCheckpoint && U.distance(V.X, VE.checkpoints.get(waitPoint).X, V.Z, VE.checkpoints.get(waitPoint).Z) < 500) {
   atGuardedCheckpoint = true;
   if (U.distance(V.X, VE.vehicles.get(target).X, V.Z, VE.vehicles.get(target).Z, V.Y, VE.vehicles.get(target).Y) > 500) {
    V.drive = V.reverse = false;
    V.handbrake = true;
   }
  } else {
   atGuardedCheckpoint = false;
  }
  if (V.vehicleType == VE.type.vehicle && U.startsWith(V.mode.name(), "neutral", "stunt") && !VE.mapName.equals("the Tunnel of Doom")) {
   stuntControl();
  }
  manageFlight();
  if (nukeWait > 0) {
   V.drive = V.reverse = false;
   nukeWait -= VE.tick;
  }
  for (Special special : V.specials) {
   if (special.type.startsWith("phantom")) {
    for (Vehicle vehicle : VE.vehicles) {
     if (!U.sameVehicle(V, vehicle) && !U.sameTeam(V, vehicle)) {
      for (Special otherSpecial : vehicle.specials) {
       for (Shot shot : otherSpecial.shots) {
        if (shot.stage > 0 && U.distance(shot.X, V.X, shot.Y, V.Y, shot.Z, V.Z) < 2000) {
         V.useSpecial[V.specials.indexOf(special)] = true;
         break;
        }
       }
      }
      for (Explosion explosion : vehicle.explosions) {
       if (explosion.stage > 0 && U.distance(explosion.X, V.X, explosion.Y, V.Y, explosion.Z, V.Z) < 2000) {
        V.useSpecial[V.specials.indexOf(special)] = true;
        break;
       }
      }
      V.useSpecial[V.specials.indexOf(special)] = ((vehicle.explosionType.contains("nuclear") && U.distance(V.X, vehicle.X, V.Y, vehicle.Y, V.Z, vehicle.Z) < 25000) || (vehicle.useSpecial[0] && vehicle.specials.size() > 0 && vehicle.specials.get(0).type.startsWith("particledisintegrator")) || (vehicle.useSpecial[1] && vehicle.specials.size() > 1 && vehicle.specials.get(1).type.startsWith("particledisintegrator"))) || V.useSpecial[V.specials.indexOf(special)];
     }
    }
   }
  }
  juke();
 }

 private void switchTarget() {
  if (target == V.index || VE.vehicles.get(target).destroyed || U.sameTeam(V.index, target) ||
  (VE.vehicles.get(target).damageDealt[U.random(4)] >= 100 && V.specials.size() < 1 && V.damageDealt[U.random(4)] < 100 && !V.explosionType.contains("nuclear")) ||//<-Not worth attacking
  (VE.viewableMapDistance > 0 && V.vehicleType != VE.type.turret && U.distance(V.X, VE.vehicles.get(target).X, V.Y, VE.vehicles.get(target).Y, V.Z, VE.vehicles.get(target).Z) > VE.viewableMapDistance) ||//<-Out of visible range
  (!guardCheckpoint && runTrackBackwards && V.specials.size() < 1 && U.distance(V.X, VE.vehicles.get(target).X, V.Z, VE.vehicles.get(target).Z) > Math.min(VE.viewableMapDistance > 0 ? VE.viewableMapDistance : Double.POSITIVE_INFINITY, 10000))) {//<-Too far away when running track backwards
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

 private void stuntControl() {
  double height = -300;
  if (VE.mapName.equals("Vehicular Falls")) {
   height = -1010500 - ((V.X < -15000 && V.Z > 15000) || (V.X > 15000 && V.Z < -15000) ? 10000 : 0);
  }
  if (!skipStunts && ((V.Y + V.clearanceY < height && U.netValue(V.stuntXY, V.stuntXZ, V.stuntYZ) <= 4000 && (V.netSpeedY <= 0 || V.landStuntsBothSides)) || (!VE.mapName.equals("Vehicular Falls") && V.Y < -V.absoluteRadius * 256))) {
   V.handbrake = (!VE.mapName.equals("SUMMIT of EPIC") || V.Y < -1010500) || V.handbrake;
   if (V.mode == mode.stunt) {
    airRotationDirection[0] = U.listEquals(VE.mapName, "Laps of Glory", "V.E. Speedway 2000000") ? 1 : airRotationDirection[0];
    V.drive = airRotationDirection[0] > 0 || V.drive;
    V.reverse = airRotationDirection[0] < 0 || V.reverse;
    V.turnR = airRotationDirection[1] > 0 || V.turnR;
    V.turnL = airRotationDirection[1] < 0 || V.turnL;
    if (VE.mapName.equals("Vehicular Falls")) {
     V.reverse = true;
     V.drive = V.turnL = V.turnR = false;
    } else if (VE.mapName.equals("XY Land")) {
     V.drive = true;
     V.reverse = V.turnL = V.turnR = false;
    }
   }
  } else {
   V.handbrake = (Math.abs(V.XY) > 30 || Math.abs(V.YZ) > 30) || V.handbrake;
   if (V.mode == mode.stunt) {
    V.turnL = V.XY > 20 || V.turnL;
    V.turnR = V.XY < -20 || V.turnR;
    V.reverse = V.YZ < -20 || V.reverse;
    V.drive = V.YZ > 20 || V.drive;
   }
  }
  if (V.damage > 0 && V.airPush > 0 && !VE.mapName.equals("Vehicular Falls")) {
   for (TrackPart trackPart : VE.trackParts) {
    if (trackPart.isFixRing && U.distance(V.X, trackPart.X, V.Z, trackPart.Z) < 10000) {
     V.handbrake = V.Y + V.clearanceY < -300 || V.handbrake;
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
  if (VE.bonusHolder != V.index && U.distance(V.X, VE.bonusX, V.Z, VE.bonusZ) < 10000) {
   V.handbrake = V.Y + V.clearanceY < -300 || V.handbrake;
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
  if (VE.mapName.equals("Vehicular Falls") && againstWall) {
   V.drive = false;
   V.reverse = true;
   V.handbrake = V.mode == mode.neutral || V.handbrake;
  }
 }

 private void manageFlight() {
  if (V.mode == mode.fly) {
   long extraY = 350;
   V.handbrake = Math.abs(V.XZ - directionXZ) <= 90;
   if (VE.checkpoints.size() > 0 && !attacking) {
    directionYZ = VE.checkpoints.get(V.checkpointsPassed).Y - extraY == V.Y ? directionYZ : -((VE.checkpoints.get(V.checkpointsPassed).Y - extraY < V.Y ? -90 : 90) - U.arcTan(U.netValue(VE.checkpoints.get(V.checkpointsPassed).Z - V.Z, VE.checkpoints.get(V.checkpointsPassed).X - V.X) / (VE.checkpoints.get(V.checkpointsPassed).Y - extraY - V.Y)));
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
   if (V.engine.equals("jetfighter")) {
    boosting = !againstWall && V.speed < 400 || (!(V.speed > 600) && boosting);
    V.boost = (boosting || (attacking && (VE.vehicles.get(target).netSpeed >= V.netSpeed || U.distance(V.X, VE.vehicles.get(target).X, V.Z, VE.vehicles.get(target).Z) > 250000))) || V.boost;
    double altitudeFloor = V.localVehicleGround - (E.boulders.size() > 0 ? (E.boulders.get(0).getRadius() * 2) + (V.collisionRadius * 2) : 1000);
    for (TrackPart trackPart : VE.trackParts) {
     altitudeFloor = trackPart.isFixRing && altitudeFloor > trackPart.Y ? trackPart.Y : altitudeFloor;
    }
    if (attacking && Math.abs(V.XY) < 90 && (V.Y > altitudeFloor || (V.YZ < -10 && E.gravity > 0))) {
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
  }
  if (V.vehicleType == VE.type.aircraft) {
   if (E.gravity == 0) {
    V.handbrake = V.mode == mode.neutral ? VE.mapName.equals("Outer Space V1") || (VE.mapName.equals("Outer Space V2") && V.speed >= Math.min(V.topSpeeds[1], U.random(4000.) + 2000)) : V.handbrake;
   } else if (U.startsWith(V.mode.name(), "drive", "neutral") && ((attacking && VE.vehicles.get(target).Y < V.Y - 1000 && V.speed > VE.vehicles.get(target).speed) || (!attacking && V.speed >= Math.min(V.topSpeeds[1], VE.speedLimitAI)) || V.speedBoost > 0)) {
    V.handbrake = true;
    V.reverse = V.mode.name().startsWith("drive") || V.reverse;
    V.boost = V.speedBoost > 0 || V.boost;
   }
  }
 }

 private void juke() {
  if (V.vehicleType != VE.type.turret && !U.listEquals(VE.mapName, "XY Land", "Devil's Stairwell", "the Tunnel of Doom") && V.damageDealt[U.random(4)] < 100) {
   for (Vehicle vehicle : VE.vehicles) {
    double avoidDistance = V.collisionRadius + vehicle.collisionRadius + vehicle.othersAvoidAt;
    boolean isSpinner = vehicle.spinnerSpeed == vehicle.spinnerSpeed;
    avoidDistance += isSpinner ? vehicle.renderRadius * 2 * Math.abs(vehicle.spinnerSpeed) : 0;
    if (!U.sameVehicle(V, vehicle) && !U.sameTeam(V, vehicle) && !vehicle.destroyed && U.distance(V.X, vehicle.X, V.Y, vehicle.Y, V.Z, vehicle.Z) < avoidDistance &&
    (isSpinner || ((vehicle.damageDealt[U.random(4)] >= 100 || (!VE.mapName.equals("SUMMIT of EPIC") && !attacking && ((V.fragility > .5 || vehicle.durability > V.durability) && V.damageDealt[U.random(4)] < 2))) &&
    (vehicle.damageDealt[U.random(4)] > 3.75 || !U.listEquals(VE.mapName, "Laps of Glory", "the Checkpoint!", "Vehicular Falls", "Zip n' Cross", "the Bottleneck", "Railing Against", "the Test of Damage", "2x2 Matrix"))))) {
     if (V.mode != mode.fly) {
      V.drive = true;
      V.reverse = false;
      V.handbrake = V.mode == mode.neutral && V.handbrake;
     }
     if (V.specials.size() == 1 && V.specials.get(0).type.startsWith("phantom")) {
      V.useSpecial[0] = true;
     } else if (V.specials.size() == 2 && V.specials.get(1).type.startsWith("phantom")) {
      V.useSpecial[1] = true;
     } else {
      double jukeAngle = (vehicle.X - V.X >= 0 ? 270 : 90) + U.arcTan((vehicle.Z - V.Z) / (vehicle.X - V.X));
      for (; jukeAngle < 0; jukeAngle += 360) ;
      for (; jukeAngle > 180; jukeAngle -= 360) ;
      jukeAngle = Math.abs(V.XZ - jukeAngle);
      if ((jukeAngle > 180 ? Math.abs(jukeAngle - 360) : jukeAngle) < 90) {
       if (V.mode != mode.fly || Math.abs(V.XY) < 90) {
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
       if (V.mode == mode.fly && Math.abs(V.YZ) < 90 && Math.abs(V.XY) < 90) {
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
}
