package ve.vehicles;

import ve.environment.Boulder;
import ve.environment.E;
import ve.instances.I;
import ve.trackElements.Bonus;
import ve.trackElements.Checkpoint;
import ve.trackElements.TE;
import ve.trackElements.trackParts.TrackPart;
import ve.ui.Maps;
import ve.ui.Match;
import ve.ui.Options;
import ve.utilities.SL;
import ve.utilities.U;
import ve.vehicles.explosions.Explosion;
import ve.vehicles.specials.Shot;
import ve.vehicles.specials.Special;

public class AI {

 private final Vehicle V;
 public int target;
 private final int waitPoint;
 private int point;
 private double directionXZ, directionYZ, precisionXZ, precisionYZ;
 private double vehicleTurretDirectionXZ, vehicleTurretDirectionYZ;
 private double aimAheadTarget;
 private double nukeWait;
 public boolean skipStunts;
 private boolean driveTrackBackwards;
 private final boolean guardCheckpoint;
 private boolean squareAgainstWall;
 private boolean engagingOthers;//<-renamed from 'attacking', since some vehicles engage their teammates instead
 private boolean shooting;
 private boolean atGuardedCheckpoint;
 private final boolean supportInfrastructure;
 public static double speedLimit;
 long airRotationDirectionYZ = U.random() < .5 ? 1 : -1, airRotationDirectionXY = U.random() < .5 ? 1 : -1;
 private WallTurn wallTurn = WallTurn.none;

 public enum Behavior {adapt, race, engageOthers}

 enum WallTurn {none, left, right}

 enum BrakeStyle {ignore, handbrake, reverseEngine}

 AI(Vehicle vehicle) {
  V = vehicle;
  target = U.random(I.vehiclesInMatch);
  guardCheckpoint = Maps.guardCheckpointAI && !V.isFixed() && V.topSpeeds[1] < 200;
  nukeWait = V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name()) ? U.random(Options.matchLength) : nukeWait;
  waitPoint = U.random(TE.checkpoints.size());
  boolean supportInfrastructure = V.type == Vehicle.Type.supportInfrastructure;
  for (Special special : V.specials) {
   if (special.type == Special.Type.particlereintegrator) {
    supportInfrastructure = true;
    break;
   }
  }
  this.supportInfrastructure = supportInfrastructure;
 }

 public void run() {
  if (V.index == I.userPlayerIndex) {//*Should ensure that this is only called for auto-aiming turrets, though it's a messy approach
   if (V.VT != null && V.VT.hasAutoAim) {
    for (Special special : V.specials) {
     if (special.aimType == Special.AimType.auto) {//*
      special.fire = false;
     }
    }
    runAutoAim();
    engagingOthers = true;
    for (Special special : V.specials) {
     if (special.aimType == Special.AimType.auto) {//*
      runAimAndShoot(special);
     }
    }
   }
  } else {
   V.drive = V.drive2 = V.reverse = V.reverse2 = V.turnL = V.turnR = V.handbrake = V.boost = false;
   for (Special special : V.specials) {
    special.fire = false;
   }
   boolean needRace =
   V.index < I.vehiclesInMatch >> 1 ?
   Match.scoreCheckpoint[0] <= Match.scoreCheckpoint[1] || Match.scoreLap[0] < Match.scoreLap[1] :
   Match.scoreCheckpoint[1] <= Match.scoreCheckpoint[0] || Match.scoreLap[1] < Match.scoreLap[0];
   long scoreStunt0 = Math.round(Match.scoreStunt[0] * .0005), scoreStunt1 = Math.round(Match.scoreStunt[1] * .0005);
   skipStunts = !V.landStuntsBothSides && !TE.checkpoints.isEmpty() && !Maps.name.equals(SL.Maps.summitOfEpic) &&
   (V.index < I.vehiclesInMatch >> 1 ? scoreStunt0 > scoreStunt1 : scoreStunt1 > scoreStunt0);
   if (driveTrackBackwards) {
    int setPoint = point - 1;
    while (setPoint < 0) setPoint += TE.points.size();
    if (U.distanceXZ(V, TE.points.get(point)) < Math.max(500, V.absoluteRadius) || U.distanceXZ(V, TE.points.get(setPoint)) < U.distanceXZ(V, TE.points.get(point))) {
     point = --point < 0 ? TE.points.size() - 1 : point;
    }
   } else {
    point = V.point;
   }
   if (V.type == Vehicle.Type.turret || shooting || U.equals(Maps.name, SL.Maps.vehicularFalls, SL.Maps.XYLand, SL.Maps.tunnelOfDoom, SL.Maps.summitOfEpic)) {
    precisionXZ = precisionYZ = U.tick;
   } else {
    precisionXZ = precisionYZ = 5;
    runRaceCheckpointSteeringOptimization();
   }
   if (!driveTrackBackwards && !V.isFixed() && !TE.points.isEmpty() && V.topSpeeds[1] < 200) {
    point = U.random(Math.max(1, TE.points.size() - 1));
    driveTrackBackwards = true;
   }
   boolean racing = (V.behavior == Behavior.race || (V.behavior == Behavior.adapt && needRace)) &&
   (!TE.checkpoints.isEmpty() || (V.behavior == Behavior.race && !V.hasShooting)) && !V.P.wrathEngaged;
   if (!engagingOthers && !racing && target != V.index && !I.vehicles.get(target).destroyed) {
    engagingOthers = true;
    aimAheadTarget = //<-Gets invalidated by shooting vehicles
    U.random() < .5 ? U.random(.75) :
    U.random(.00333 * I.vehicles.get(target).P.netSpeed);
   }
   runSwitchTarget();
   if (I.vehicles.get(target).destroyed || racing) {
    engagingOthers = false;
   }
   if (driveTrackBackwards && !V.hasShooting) {
    aimAheadTarget += U.random() < .05 ? U.randomPlusMinus(10.) : 0;
    aimAheadTarget = aimAheadTarget < 0 || aimAheadTarget > 10 ? U.random(10.) : aimAheadTarget;
   }
   shooting = false;
   for (Special special : V.specials) {//<-'shooting' gets engaged within block
    if (special.type == Special.Type.forcefield) {
     runForcefieldStrike(special);
    } else if (special.type == Special.Type.mine) {
     runMineDeploy(special);
    } else {
     runAimAndShoot(special);//<-aimAheadTarget is set here
    }
   }
   aimAheadTarget = aimAheadTarget < 0 || I.vehicles.get(target).isFixed() ? 0 : aimAheadTarget;
   if (guardCheckpoint && !atGuardedCheckpoint) {
    directionXZ = (TE.checkpoints.get(waitPoint).X - V.X >= 0 ? 270 : 90) + U.arcTan((TE.checkpoints.get(waitPoint).Z - V.Z) / (TE.checkpoints.get(waitPoint).X - V.X));
   } else if (engagingOthers) {
    Vehicle otherV = I.vehicles.get(target);
    boolean targetDriveActive = !U.startsWith(otherV.P.mode.name(), Physics.Mode.neutral.name(), Physics.Mode.stunt.name());
    double pX = otherV.X, pZ = otherV.Z;
    if (targetDriveActive) {
     double addAim = U.distanceXZ(V, otherV) * aimAheadTarget;
     if (otherV.P.speed > 0 && otherV.P.speed >= V.P.speed) {
      pX -= addAim * U.sin(otherV.XZ);
      pZ += addAim * U.cos(otherV.XZ);
     } else if (otherV.P.speed < 0 && otherV.P.speed <= V.P.speed) {
      pX += addAim * U.sin(otherV.XZ);
      pZ -= addAim * U.cos(otherV.XZ);
     }
    }
    directionXZ = (pX - V.X >= 0 ? 270 : 90) + U.arcTan((pZ - V.Z) / (pX - V.X));
    if (V.P.mode == Physics.Mode.fly || V.isFixed()) {
     double addAim = U.distance(V, otherV) * .02 * aimAheadTarget,
     aimY = otherV.Y;
     if (targetDriveActive) {
      aimY -= (otherV.P.speed > 0 && otherV.P.speed >= V.P.speed ? 1 : otherV.P.speed < 0 && otherV.P.speed <= V.P.speed ? -1 : 0) * addAim * U.sin(otherV.YZ);
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
     if (V.isFixed()/*<-Don't let aircraft do this!*/ && V.Y > -1 - V.turretBaseY + V.P.localGround && V.YZ < 0) {
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
   if (V.P.mode != Physics.Mode.stunt) {
    boolean flying = V.P.mode == Physics.Mode.fly;
    V.drive = (!flying && !V.isFixed() && V.P.speed < getTargetSpeed()) || V.drive;
    if (flying) {
     V.drive2 = V.P.speed < getTargetSpeed();//<-'V.speed' better than 'V.netSpeed' as it is stall-proof
     V.reverse2 = V.P.speed > getTargetSpeed();//<-Don't use 'V.netSpeed' or they'll start flying backwards!
    }
    runSteering();
    runWallHits();
   }
   runFlight();
   runVehicleStunts();
   if (guardCheckpoint && U.distanceXZ(V, TE.checkpoints.get(waitPoint)) < 500) {
    atGuardedCheckpoint = true;
    if (U.distance(V, I.vehicles.get(target)) > 500) {
     V.drive = V.reverse = false;
     V.handbrake = true;
    }
   } else {
    atGuardedCheckpoint = false;
   }
   if (nukeWait > 0) {
    V.drive = V.drive2 = V.reverse = V.reverse2 = false;
    nukeWait -= U.tick;
   }
   for (Special special : V.specials) {
    if (special.type == Special.Type.phantom) {
     for (Vehicle vehicle : I.vehicles) {
      if (!U.sameTeam(V, vehicle)) {
       for (Special otherSpecial : vehicle.specials) {
        for (Shot shot : otherSpecial.shots) {
         if (shot.stage > 0 && U.distance(shot, V) < 2000 + shot.absoluteRadius) {
          special.fire = true;
          break;
         }
        }
        special.fire = (otherSpecial.fire && otherSpecial.type == Special.Type.particledisintegrator) || special.fire;
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
   passBonus();
  }
 }

 private double getTargetSpeed() {
  if (engagingOthers) {
   return V.hasShooting ?
   Math.max(V.P.mode == Physics.Mode.fly ? V.P.minimumFlightSpeedWithoutStall : 0, U.distance(V, I.vehicles.get(target)) * .01) :
   Double.POSITIVE_INFINITY;
  } else {
   return speedLimit;
  }
 }

 private void runRaceCheckpointSteeringOptimization() {
  if (!engagingOthers && TE.points.size() > 1 && !Maps.name.contains("Circle")) {//<-Void is not helpful for AI's in circle races
   int n = V.point + 1;
   while (n >= TE.points.size()) n -= TE.points.size();
   double pointX = TE.points.get(n).X, pointZ = TE.points.get(n).Z,
   racePath = (pointX - V.X >= 0 ? 270 : 90) + U.arcTan((pointZ - V.Z) / (pointX - V.X));
   while (Math.abs(V.XZ - racePath) > 180) {
    racePath += racePath < V.XZ ? 360 : -360;
   }
   precisionXZ = (V.XZ > directionXZ && racePath > directionXZ) || (V.XZ < directionXZ && racePath < directionXZ) ? U.tick : precisionXZ;
  }
 }

 private void runSwitchTarget() {
  Vehicle targetV = I.vehicles.get(target);
  if (supportInfrastructure) {
   if (target == V.index || targetV.destroyed || !U.sameTeam(V.index, target)) {
    engagingOthers = true;//<-Always run it--why not?
    target = U.random(I.vehiclesInMatch);
    for (Vehicle vehicle : I.vehicles) {
     if (!U.sameVehicle(V, vehicle) && U.sameTeam(V, vehicle) &&
     vehicle.isIntegral() && vehicle.getDamage(true) > I.vehicles.get(target).getDamage(true)) {
      target = vehicle.index;
     }
    }
   }
  } else {
   if (target == V.index || targetV.destroyed || U.sameTeam(V.index, target) ||
   ((targetV.dealsMassiveDamage() || targetV.spinner != null) && !V.hasShooting && !V.dealsMassiveDamage() && !V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name()) && !V.P.wrathEngaged) ||//<-Not worth attacking
   (E.viewableMapDistance < Double.POSITIVE_INFINITY && !V.isFixed() && U.distance(V, targetV) > E.viewableMapDistance) ||//<-Out of visible range
   (driveTrackBackwards && !guardCheckpoint && !V.hasShooting && U.distanceXZ(V, targetV) > Math.min(E.viewableMapDistance, 10000)) ||//<-Too far away when running track backwards
   (targetV.explosionType == Vehicle.ExplosionType.maxnuclear && V.explosionType != Vehicle.ExplosionType.maxnuclear)) {//<-Don't attack max nukes unless bot's also a max nuke
    engagingOthers = false;
    target = U.random(I.vehiclesInMatch);
    if (U.random() < .5) {
     for (Vehicle vehicle : I.vehicles) {
      if (!U.sameTeam(V, vehicle) && Bonus.holder == vehicle.index) {
       target = vehicle.index;
      }
     }
    }
   }
  }
 }

 private void runSteering() {
  double differenceXZ = Math.abs(V.XZ - directionXZ);
  if (differenceXZ > precisionXZ) {
   V.turnL = V.XZ < directionXZ;
   V.turnR = V.XZ > directionXZ;
   if (!V.isFixed()) {
    boolean checkpoints = !TE.checkpoints.isEmpty();
    double checkpointDistance = checkpoints ? U.distanceXZ(V, TE.checkpoints.get(V.checkpointsPassed)) : 0;
    BrakeStyle brakeStyle =
    V.type == Vehicle.Type.aircraft && engagingOthers && I.vehicles.get(target).P.mode == Physics.Mode.fly ? BrakeStyle.ignore :
    V.highGrip() ? BrakeStyle.handbrake :
    BrakeStyle.reverseEngine;
    boolean flying = V.P.mode == Physics.Mode.fly,
    turningInTheRoad = V.maxTurn < 18 && !engagingOthers && !V.P.againstWall() && checkpoints && checkpointDistance < 2545 && differenceXZ > V.maxTurn;
    if (brakeStyle != BrakeStyle.ignore &&
    differenceXZ >= U.clamp(V.maxTurn, checkpoints && !engagingOthers ? checkpointDistance * .001 : 50, 90) &&
    V.P.speed > Math.max(50, V.accelerationStages[0] * U.tick) * (turningInTheRoad ? -1 : 1)) {
     if (flying) {
      if (V.P.speed > V.P.minimumFlightSpeedWithoutStall && !(engagingOthers && !V.hasShooting)) {
       V.drive2 = false;
       V.reverse2 = true;
      }
     } else {
      V.drive = false;
     }
     if (brakeStyle == BrakeStyle.reverseEngine) {
      V.reverse = !flying || V.reverse;
     } else if (!flying && V.P.mode != Physics.Mode.neutral) {
      V.handbrake = true;
     }
     if (turningInTheRoad) {
      V.reverse = !flying || V.reverse;
      if (V.P.speed <= 0) {
       V.handbrake = flying && V.handbrake;
       if (V.P.speed < 0) {
        V.turnL = !V.turnL;
        V.turnR = !V.turnR;
       }
      }
     }
    }
   }
  }
 }

 private void runVehicleStunts() {
  if (V.type == Vehicle.Type.vehicle && (V.P.mode == Physics.Mode.neutral || V.P.mode == Physics.Mode.stunt) && !U.equals(Maps.name, SL.Maps.tunnelOfDoom, SL.Maps.devilsStairwell)) {
   boolean vehicularFalls = Maps.name.equals(SL.Maps.vehicularFalls);
   double height = -300 - V.P.localGround;
   if (vehicularFalls) {
    height = -1010500 - ((V.X < -15000 && V.Z > 15000) || (V.X > 15000 && V.Z < -15000) ? 10000 : 0);
   }
   if (!skipStunts && ((V.Y + V.clearanceY < height && U.netValue(V.P.stuntXY, V.P.stuntXZ, V.P.stuntYZ) <= 4000 && (V.speedY <= 0 || V.landStuntsBothSides)) ||
   (!vehicularFalls && V.Y < -V.absoluteRadius * 256))) {
    V.handbrake = (!Maps.name.equals(SL.Maps.summitOfEpic) || V.Y < -1010500) || V.handbrake;
    if (V.P.mode == Physics.Mode.stunt) {
     V.handbrake = false;
     airRotationDirectionYZ = U.equals(Maps.name, SL.Maps.lapsOfGlory, SL.Maps.speedway2000000) ? 1 : airRotationDirectionYZ;
     V.drive = airRotationDirectionYZ > 0;
     V.reverse = airRotationDirectionYZ < 0;
     V.turnR = airRotationDirectionXY > 0;
     V.turnL = airRotationDirectionXY < 0;
     if (vehicularFalls) {
      V.reverse = true;
      V.drive = V.turnL = V.turnR = false;
     } else if (Maps.name.equals(SL.Maps.XYLand)) {
      V.drive = true;
      V.reverse = V.turnL = V.turnR = false;
     }
    }
   } else {
    if (V.P.mode == Physics.Mode.stunt) {
     V.turnL = V.XY > 20;
     V.turnR = V.XY < -20;
     V.reverse = V.YZ < -20;
     V.drive = V.YZ > 20;
    } else {
     V.handbrake = Math.abs(V.XY) > 30 || Math.abs(V.YZ) > 30;
    }
   }
   if (V.getDamage(false) > 0 && V.airPush > 0 && !vehicularFalls) {
    for (TrackPart trackPart : TE.trackParts) {
     if (trackPart.isRepairPoint && U.distance(V, trackPart) < 10000) {
      V.handbrake = V.Y + V.clearanceY < -300 - V.P.localGround && V.P.mode != Physics.Mode.stunt;
      if (Math.abs(V.speedZ) > Math.abs(V.speedX) && ((V.speedZ > 0 && trackPart.Z > V.Z) || (V.speedZ < 0 && trackPart.Z < V.Z))) {
       V.drive = false;
       V.reverse = trackPart.Y < V.Y - Math.min(Math.abs(V.P.speed), 500);
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
      if (Math.abs(V.speedX) > Math.abs(V.speedZ) && ((V.speedX > 0 && trackPart.X > V.X) || (V.speedX < 0 && trackPart.X < V.X))) {
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
   if (Bonus.holder != V.index && U.distance(V, TE.bonus) < 10000) {
    V.handbrake = V.Y + V.clearanceY < -300 - V.P.localGround && V.P.mode != Physics.Mode.stunt;
    if (Math.abs(V.speedZ) > Math.abs(V.speedX) && ((V.speedZ > 0 && TE.bonus.Z > V.Z) || (V.speedZ < 0 && TE.bonus.Z < V.Z))) {
     V.drive = false;
     V.reverse = TE.bonus.Y < V.Y - Math.min(Math.abs(V.P.speed), 500);
     if (Math.abs(V.YZ) < 90) {
      if (Math.abs(V.XZ) < 90) {
       V.turnR = TE.bonus.X > V.X;
       V.turnL = TE.bonus.X < V.X;
      }
      if (V.XZ > 90 || V.XZ < -90) {
       V.turnR = TE.bonus.X < V.X;
       V.turnL = TE.bonus.X > V.X;
      }
     } else {
      if (Math.abs(V.XZ) < 90) {
       V.turnR = TE.bonus.X < V.X;
       V.turnL = TE.bonus.X > V.X;
      }
      if (V.XZ > 90 || V.XZ < -90) {
       V.turnR = TE.bonus.X > V.X;
       V.turnL = TE.bonus.X < V.X;
      }
     }
    }
    if (Math.abs(V.speedX) > Math.abs(V.speedZ) && ((V.speedX > 0 && TE.bonus.X > V.X) || (V.speedX < 0 && TE.bonus.X < V.X))) {
     V.drive = false;
     V.reverse = TE.bonus.Y < V.Y;
     if (Math.abs(V.YZ) < 90) {
      if (V.XZ < 0) {
       V.turnR = TE.bonus.Z < V.Z;
       V.turnL = TE.bonus.Z > V.Z;
      } else if (V.XZ > 0) {
       V.turnR = TE.bonus.Z > V.Z;
       V.turnL = TE.bonus.Z < V.Z;
      }
     } else if (V.XZ < 0) {
      V.turnR = TE.bonus.Z > V.Z;
      V.turnL = TE.bonus.Z < V.Z;
     } else if (V.XZ > 0) {
      V.turnR = TE.bonus.Z < V.Z;
      V.turnL = TE.bonus.Z > V.Z;
     }
    }
   }
   if (vehicularFalls && V.P.againstWall()) {
    V.drive = false;
    V.reverse = true;
    V.handbrake = V.P.mode == Physics.Mode.neutral || V.handbrake;
   }
  }
 }

 private void runFlight() {
  if (V.type == Vehicle.Type.aircraft) {
   if (V.P.mode == Physics.Mode.fly) {
    long extraY = 350;
    V.handbrake = Math.abs(V.XZ - directionXZ) <= 90;
    if (!TE.checkpoints.isEmpty() && !engagingOthers) {
     Checkpoint C = TE.checkpoints.get(V.checkpointsPassed);
     directionYZ = C.Y - extraY == V.Y ? directionYZ : -((C.Y - extraY < V.Y ? -90 : 90) - U.arcTan(U.netValue(C.Z - V.Z, C.X - V.X) / (C.Y - extraY - V.Y)));
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
    if (V.engine.name().contains(SL.jetfighter)) {
     V.boost = ((!V.P.againstWall() && V.P.speed < V.P.minimumFlightSpeedWithoutStall) ||
     (engagingOthers && (I.vehicles.get(target).P.netSpeed >= V.P.netSpeed || U.distance(V, I.vehicles.get(target)) > 250000))) ||
     V.boost;
     if (engagingOthers) {
      double altitudeFloor = V.P.localGround -
      Math.max(I.vehicles.get(target).absoluteRadius, (Boulder.instances.isEmpty() ? 0 : (Boulder.instances.get(0).S.getRadius() * 2) + (V.collisionRadius * 2)));
      for (TrackPart trackPart : TE.trackParts) {
       altitudeFloor = trackPart.isRepairPoint && altitudeFloor > trackPart.Y ? trackPart.Y : altitudeFloor;
      }
      if (V.Y > altitudeFloor) {
       if (Math.abs(V.XY) < 90) {
        V.drive = false;
        V.reverse = true;
       }
       V.turnL = V.XY > 0;
       V.turnR = V.XY < 0;
      }
     }
    }//YZ check must come AFTER jetfighter block!
    V.drive = V.YZ > -80 && V.drive;
    V.reverse = V.YZ < 80 && V.reverse;
    V.reverse = V.YZ <= -90 || V.reverse;
    V.drive = V.YZ >= 90 || V.drive;
   } else if (U.startsWith(V.P.mode.name(), SL.drive, Physics.Mode.neutral.name()) && V.P.speed < getTargetSpeed()) {
    V.drive2 = V.boost = true;
    if (engagingOthers && I.vehicles.get(target).Y < V.Y - V.collisionRadius && I.vehicles.get(target).P.speed > 0 && V.P.speed >= Math.min(V.topSpeeds[1], getTargetSpeed())) {
     V.reverse = true;//<-For takeoff
    }
   }
   if (V.engine.name().contains(SL.jetfighter) && V.P.mode != Physics.Mode.fly) {//<-For AIR superiority
    V.drive2 = V.boost = V.reverse = true;
   }
  }
 }

 private void runWallHits() {
  if (!V.isFixed()) {
   boolean againstWall = V.P.againstWall();
   if (againstWall && (!shooting || guardCheckpoint) && !Maps.name.equals(SL.Maps.devilsStairwell) && !(U.random() < .5 && U.equals(Maps.name, SL.Maps.theBottleneck, SL.Maps.testOfDamage, SL.Maps.matrix2x3, SL.Maps.theMaze, SL.Maps.tunnelOfDoom))) {
    if (V.P.mode == Physics.Mode.fly) {
     V.drive = false;
     V.reverse = true;
    }
    if (wallTurn == WallTurn.none && ((V.wheels.get(0).againstWall && V.wheels.get(1).againstWall) || (V.wheels.get(2).againstWall && V.wheels.get(3).againstWall))) {
     wallTurn = U.random() < .5 ? WallTurn.left : WallTurn.right;
     squareAgainstWall = true;
    }
    if (!squareAgainstWall) {
     wallTurn = (V.wheels.get(0).againstWall && !V.wheels.get(1).againstWall) || (V.wheels.get(2).againstWall && !V.wheels.get(3).againstWall) ? WallTurn.right : wallTurn;
     wallTurn = (V.wheels.get(1).againstWall && !V.wheels.get(0).againstWall) || (V.wheels.get(3).againstWall && !V.wheels.get(2).againstWall) ? WallTurn.left : wallTurn;
    }
   } else {
    squareAgainstWall = false;
   }
   if (wallTurn == WallTurn.left) {
    V.turnR = false;
    V.turnL = true;
   } else if (wallTurn == WallTurn.right) {
    V.turnL = false;
    V.turnR = true;
   }
   wallTurn = !squareAgainstWall && !againstWall ? WallTurn.none : wallTurn;
  }
 }

 private void runJuke() {
  if (!V.isFixed() && !U.equals(Maps.name, SL.Maps.XYLand, SL.Maps.devilsStairwell, SL.Maps.tunnelOfDoom) && !V.dealsMassiveDamage()) {
   boolean racingSafely = !Maps.name.equals(SL.Maps.summitOfEpic) && !engagingOthers;
   for (Vehicle vehicle : I.vehicles) {
    double avoidDistance = V.collisionRadius + vehicle.collisionRadius + vehicle.othersAvoidAt;
    boolean theyHaveSpinner = vehicle.spinner != null,
    notWorthCrashing = V.damageDealt < 4 && (V.fragility > .5 || vehicle.durability > V.durability);
    avoidDistance += theyHaveSpinner ? vehicle.renderRadius * 2 * Math.abs(vehicle.spinner.speed) : 0;
    if (!U.sameTeam(V, vehicle) && !vehicle.destroyed && U.distance(V, vehicle) < avoidDistance &&
    (theyHaveSpinner || vehicle.dealsMassiveDamage() || (racingSafely && notWorthCrashing &&
    (vehicle.damageDealt > 7.5 || !U.equals(Maps.name, SL.Maps.lapsOfGlory, "the Checkpoint!", SL.Maps.vehicularFalls, "Zip n' Cross", SL.Maps.theBottleneck, "Railing Against", SL.Maps.testOfDamage, SL.Maps.matrix2x3))))) {//<-Racers ignoring risk on these maps
     if (V.P.mode != Physics.Mode.fly) {
      V.drive = true;
      V.reverse = V.handbrake = false;
     }
     boolean usePhantom = false;
     for (Special special : V.specials) {
      if (special.type == Special.Type.phantom) {
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
       if (V.P.mode != Physics.Mode.fly || Math.abs(V.XY) < 90) {
        if (Math.abs(V.speedZ) > Math.abs(V.speedX)) {
         if (V.speedZ > 0) {
          if (V.X < vehicle.X) {
           V.turnR = false;
           V.turnL = true;
          } else if (V.X > vehicle.X) {
           V.turnL = false;
           V.turnR = true;
          }
         } else if (V.speedZ < 0) {
          if (V.X < vehicle.X) {
           V.turnL = false;
           V.turnR = true;
          } else if (V.X > vehicle.X) {
           V.turnR = false;
           V.turnL = true;
          }
         }
        } else if (Math.abs(V.speedX) > Math.abs(V.speedZ)) {
         if (V.speedX > 0) {
          if (V.Z < vehicle.Z) {
           V.turnL = false;
           V.turnR = true;
          } else if (V.Z > vehicle.Z) {
           V.turnR = false;
           V.turnL = true;
          }
         } else if (V.speedX < 0) {
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
       if (V.P.mode == Physics.Mode.fly && Math.abs(V.YZ) < 90 && Math.abs(V.XY) < 90) {
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
  if (engagingOthers && special.type != Special.Type.phantom && special.type != Special.Type.teleport) {
   double
   accuracyRangeXZ = special.aimType == Special.AimType.auto ? Math.abs(V.VT.XZ - vehicleTurretDirectionXZ) : Math.abs(V.XZ - directionXZ),
   accuracyRangeYZ = special.aimType == Special.AimType.auto ? Math.abs(V.VT.YZ - vehicleTurretDirectionYZ) : 0;
   boolean shoot = false;
   if (accuracyRangeXZ < special.AIAimPrecision && accuracyRangeYZ < special.AIAimPrecision) {
    for (Vehicle vehicle : I.vehicles) {
     if (!vehicle.destroyed && (supportInfrastructure ? !U.sameVehicle(V, vehicle) && U.sameTeam(V, vehicle) : !U.sameTeam(V, vehicle))) {
      shoot = true;
      break;
     }
    }
   }
   if (shoot) {
    special.fire = shooting = true;
    if (special.type.name().contains(SL.particle)) {
     aimAheadTarget = 0;
    } else if (special.timer <= 0) {//<-Only recalibrate aimAhead when the gun actually fires
     aimAheadTarget =
     special.homing ? 0 ://<-None needed
     special.speed > 3000 ? Math.min(aimAheadTarget, U.random(.5)) ://<-Reduce for fast shots
     V.type == Vehicle.Type.turret ? I.vehicles.get(target).P.netSpeed * .00333 - U.random() ://<-All turrets excluding railgun special
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
  for (Vehicle vehicle : I.vehicles) {
   if (!U.sameTeam(V, vehicle) && !vehicle.destroyed && U.distance(V, vehicle) <= special.diameter + vehicle.collisionRadius) {//<-Don't skimp on bounds--Lightning Rod wouldn't fire at MarcoPolo bus, for example
    special.fire = true;
    break;
   }
  }
 }

 private void runAutoAim() {
  if (V.VT != null && V.VT.hasAutoAim) {
   V.VT.turnL = V.VT.turnR = false;
   double turretZ = V.Z;//<-Elaborate later if needed
   Vehicle otherV = I.vehicles.get(target);
   boolean targetDriveActive = !U.startsWith(otherV.P.mode.name(), Physics.Mode.neutral.name(), Physics.Mode.stunt.name());
   double pX = otherV.X, pZ = otherV.Z;
   if (targetDriveActive) {
    double addAim = U.distance(V.X, otherV.X, turretZ, otherV.Z) * aimAheadTarget;
    if (otherV.P.speed > 0 && otherV.P.speed >= V.P.speed) {
     pX -= addAim * U.sin(otherV.XZ);
     pZ += addAim * U.cos(otherV.XZ);
    } else if (otherV.P.speed < 0 && otherV.P.speed <= V.P.speed) {
     pX += addAim * U.sin(otherV.XZ);
     pZ -= addAim * U.cos(otherV.XZ);
    }
   }
   vehicleTurretDirectionXZ = (pX - V.X >= 0 ? 270 : 90) + U.arcTan((pZ - turretZ) / (pX - V.X)) - V.XZ;
   while (Math.abs(V.VT.XZ - vehicleTurretDirectionXZ) > 180) {
    vehicleTurretDirectionXZ += vehicleTurretDirectionXZ < V.VT.XZ ? 360 : -360;
   }
   if (Math.abs(V.VT.XZ - vehicleTurretDirectionXZ) > precisionXZ) {
    V.VT.turnL = V.VT.XZ < vehicleTurretDirectionXZ;
    V.VT.turnR = V.VT.XZ > vehicleTurretDirectionXZ;
   }
   double addAim = U.distance(V, otherV) * .02 * aimAheadTarget,
   aimY = otherV.Y;
   if (targetDriveActive) {
    aimY -= (otherV.P.speed > 0 && otherV.P.speed >= V.P.speed ? 1 : otherV.P.speed < 0 && otherV.P.speed <= V.P.speed ? -1 : 0) * addAim * U.sin(otherV.YZ);
   }
   vehicleTurretDirectionYZ = otherV.Y == V.Y ? 0 : (otherV.Y < V.Y ? -90 : 90) - U.arcTan(U.netValue(otherV.Z - turretZ, otherV.X - V.X) / (aimY - V.Y));
   if (Math.abs(vehicleTurretDirectionYZ - V.VT.YZ) > precisionYZ) {
    if (vehicleTurretDirectionYZ > V.VT.YZ) {
     V.drive2 = false;
     V.reverse2 = true;
    } else if (vehicleTurretDirectionYZ < V.VT.YZ) {
     V.reverse2 = false;
     V.drive2 = true;
    }
   }
  }
 }

 void passBonus() {
  if (I.vehiclesInMatch > 2) {//<-Otherwise there's no teammate to pass to, obviously
   V.passBonus = false;
   double yourDamage = V.getDamage(true);
   for (Vehicle otherV : I.vehicles) {
    if (!U.sameVehicle(otherV, V) && U.sameTeam(V, otherV) && !otherV.destroyed && U.distance(V, otherV) < V.collisionRadius + otherV.collisionRadius &&
    otherV.durability > V.durability && V.fragility > otherV.fragility && (yourDamage > otherV.getDamage(true) || V.selfRepair < otherV.selfRepair)) {//<-Trying to guess what's the best reasoning for an AI passing the bonus to another teammate
     V.passBonus = true;
     break;
    }
   }
  }
 }
}
