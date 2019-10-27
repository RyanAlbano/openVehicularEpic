package ve.vehicles;

import ve.Sound;
import ve.VE;
import ve.environment.E;
import ve.utilities.U;

import java.util.*;

public class Special {

 private final Vehicle V;
 public Type type = Type.none;
 private int currentShot;
 double randomPosition, randomAngle, timer, speed, diameter, damageDealt, pushPower, length, width;
 boolean homing;
 boolean hasThrust;
 boolean ricochets;
 boolean useSmallHits;
 public boolean fire;
 long AIAimPrecision = Long.MAX_VALUE;
 public final List<Shot> shots = new ArrayList<>();
 public final List<Port> ports = new ArrayList<>();
 Sound sound;

 public enum Type {
  none,
  gun, machinegun, minigun, heavymachinegun, shotgun, raygun, railgun,
  shell, powershell, missile, bomb, flamethrower, mine,
  blaster, heavyblaster, forcefield,
  phantom, teleport,
  particledisintegrator, spinner, thewrath
 }

 AimType aimType = AimType.normal;

 enum AimType {normal, ofVehicleTurret, auto}

 Special(Vehicle vehicle) {
  V = vehicle;
 }

 void load() {
  if (type == Type.gun) {
   speed = 3000;
   diameter = 25;
   damageDealt = 100;
   pushPower = 2;
   width = 4;
   length = width * 3;
   ricochets = useSmallHits = V.hasShooting = true;
  } else if (type == Type.machinegun) {
   speed = 3000;
   diameter = 25;
   damageDealt = 50;
   pushPower = 1;
   width = 4;
   length = width * 3;
   ricochets = useSmallHits = V.hasShooting = true;
  } else if (type == Type.minigun) {
   speed = 3000;
   diameter = 10;
   damageDealt = 25;
   pushPower = 0;
   width = 2;
   length = width * 4;
   ricochets = useSmallHits = V.hasShooting = true;
  } else if (type == Type.heavymachinegun) {
   speed = 3000;
   diameter = 50;
   damageDealt = 100;
   pushPower = 50;
   width = 5;
   length = width * 3;
   ricochets = useSmallHits = V.hasShooting = true;
  } else if (type == Type.shotgun) {
   speed = 3000;
   diameter = 25;
   damageDealt = 25;
   pushPower = 1;
   width = 6;
   length = width * 2;
   ricochets = useSmallHits = true;
  } else if (type == Type.raygun) {
   speed = 15000;
   diameter = 10;
   damageDealt = 100;//<-Multiplied by tick in-game
   pushPower = 0;
   width = 5;
   length = width * 10;
   useSmallHits = V.hasShooting = true;
  } else if (type == Type.shell) {
   speed = 500;
   diameter = 100;
   damageDealt = 1500;
   pushPower = 1000;
   width = 12;
   length = width * 2;
   V.explosionType = Vehicle.ExplosionType.normal;
   AIAimPrecision = 10;
   hasThrust = V.hasShooting = true;
  } else if (type == Type.powershell) {
   speed = 3000;
   diameter = 200;
   damageDealt = 15000;
   pushPower = 1000;
   width = 24;
   length = width * 2;
   V.explosionType = Vehicle.ExplosionType.normal;
   AIAimPrecision = 10;
   hasThrust = V.hasShooting = true;
  } else if (type == Type.bomb) {
   speed = 0;
   diameter = 100;
   damageDealt = 1500;
   pushPower = 1000;
   width = 12;
   length = width * 2;
   V.explosionType = Vehicle.ExplosionType.normal;
  } else if (type == Type.railgun) {
   speed = 20000;
   diameter = 500;
   damageDealt = 7500;
   pushPower = 1000;
   width = 10;
   length = width * 3;
   AIAimPrecision = 5;
   V.hasShooting = true;
  } else if (type == Type.missile) {
   speed = 500;
   diameter = 100;
   damageDealt = 1000;
   pushPower = 1000;
   width = 6;
   length = width * 4;
   V.explosionType = Vehicle.ExplosionType.normal;
   AIAimPrecision = 10;
   hasThrust = V.hasShooting = true;
  } else if (type == Type.blaster) {
   speed = 1500;
   diameter = 20;
   damageDealt = 500;//<-Multiplied by tick in-game
   pushPower = 100;
   width = 15;
   useSmallHits = V.hasShooting = true;
  } else if (type == Type.heavyblaster) {
   speed = 750;
   diameter = 250;
   damageDealt = 2500;//<-Multiplied by tick in-game
   pushPower = 2000;
   width = 200;
   AIAimPrecision = 10;
   V.hasShooting = true;
  } else if (type == Type.flamethrower) {
   speed = 250;
   diameter = 500;
   damageDealt = 250;//<-Multiplied by tick in-game
   pushPower = 0;
   width = 10;
   length = width;
   V.hasShooting = true;
  } else if (type == Type.forcefield) {
   diameter = V.collisionRadius * 2;
   damageDealt = 2500;
   pushPower = 2000;
   width = V.collisionRadius * 2;
  } else if (type == Type.mine) {
   diameter = 500;
   damageDealt = 15000;
   pushPower = 500;
   width = 100;
   length = width * .2;
   V.explosionType = Vehicle.ExplosionType.normal;
  } else if (type == Type.thewrath) {
   speed = 3000;
   diameter = V.absoluteRadius;
   damageDealt = 2000;//<-Multiplied by tick in-game
   width = V.absoluteRadius * 2;
   AIAimPrecision = 10;
  }
  AIAimPrecision = homing ? Long.MAX_VALUE : AIAimPrecision;
  if (type != Type.particledisintegrator && type != Type.spinner) {
   for (int n = E.shotQuantity; --n >= 0; ) {
    shots.add(new Shot(this));
   }
  }
  if (!type.name().contains(Type.blaster.name()) && type != Type.raygun && type != Type.forcefield && type != Type.mine && type != Type.thewrath) {
   for (Port port : ports) {
    port.spit = new Spit(this);
   }
  }
  if (useSmallHits) {
   V.VA.hitShot = new Sound("hitShot", Double.POSITIVE_INFINITY);
  }
  if (ricochets) {
   V.VA.hitRicochet = new Sound("hitRicochet", Double.POSITIVE_INFINITY);
  }
  V.wrathStuck = type == Type.thewrath ? new boolean[VE.vehiclesInMatch] : V.wrathStuck;
 }

 public void run(Vehicle V, boolean gamePlay) {
  if (type != Type.particledisintegrator && type != Type.spinner && type != Type.phantom && type != Type.teleport) {
   if (gamePlay) {
    if (timer <= 0) {
     if (fire && !V.destroyed) {
      time();
      shoot(V);
      V.wrathEngaged = type == Type.thewrath;
      if (V.wrathEngaged) {
       for (int n = VE.vehiclesInMatch; --n >= 0; ) {
        V.wrathStuck[n] = false;
       }
      }
     }
    } else {
     timer -= VE.tick;
    }
    if (V.wrathEngaged) {
     shoot(V);
     V.thrusting = true;
     speed = Math.min(speed + ((U.random() < .5 ? V.accelerationStages[0] : V.accelerationStages[1]) * 4 * VE.tick), V.topSpeeds[2]);
     V.damage = Math.min(V.damage, V.durability);
     V.screenFlash = (.5 + U.random(.5)) / Math.max(Math.sqrt(V.distanceToCamera) * .015, 1);
     V.wrathEngaged = !(timer < 850) && V.wrathEngaged;
    }
   }
   if (!V.destroyed && type == Type.flamethrower) {
    for (Port port : ports) {
     port.spit.deploy(V, this, port);
    }
   }
   for (Shot shot : shots) {
    shot.run(V, this, gamePlay);
   }
   for (Port port : ports) {
    if (port.spit != null) {
     port.spit.run(V, this, port, gamePlay);
    }
   }
  }
 }

 private void shoot(Vehicle V) {
  for (Port port : ports) {
   double[] shotX = {port.X}, shotY = {port.Y}, shotZ = {port.Z};
   U.rotate(shotX, shotY, V.XY);
   U.rotate(shotY, shotZ, V.YZ);
   U.rotate(shotX, shotZ, V.XZ);
   shots.get(currentShot).deploy(V, this, port);
   currentShot = ++currentShot >= E.shotQuantity ? 0 : currentShot;
  }
  for (Port port : ports) {
   if (port.spit != null) {
    port.spit.deploy(V, this, port);
   }
  }
  if (type == Type.phantom) {
   sound.loop(V.VA.vehicleToCameraSoundDistance);
  } else if ((type != Type.flamethrower || VE.globalFlick) && !V.wrathEngaged) {
   sound.play(V.VA.vehicleToCameraSoundDistance * (type == Type.thewrath ? .5 : 1));
  }
 }

 void time() {
  timer =
  type == Type.gun ? 8 :
  type.name().contains(Type.machinegun.name()) ? 1 :
  type == Type.minigun ? .3 :
  type == Type.shotgun ? 35 :
  type == Type.shell ? 30 :
  type == Type.railgun || type == Type.missile || type == Type.powershell || type == Type.mine ? 200 :
  type == Type.blaster ? 10 :
  type == Type.heavyblaster || type == Type.bomb ? 100 :
  type == Type.forcefield ? 20 :
  type == Type.thewrath ? 1000 :
  0;
 }
}
