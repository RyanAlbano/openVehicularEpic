package ve.vehicles.specials;

import ve.Sound;
import ve.VE;
import ve.utilities.SL;
import ve.utilities.U;
import ve.vehicles.Vehicle;

import java.util.*;

public class Special {

 private final Vehicle V;
 public EnergyBolt EB;
 public final Type type;
 private int currentShot;
 public double randomPosition, randomAngle;
 public double timer;
 public double speed;
 public double diameter, damageDealt;
 public double pushPower;
 double length;
 double width;
 public final boolean homing;
 boolean hasThrust;
 public boolean ricochets, useSmallHits;
 public boolean fire;
 public long AIAimPrecision = Long.MAX_VALUE;
 public final List<Shot> shots = new ArrayList<>();
 public final List<Port> ports = new ArrayList<>();
 public Sound sound;

 public enum Type {
  gun, machinegun, minigun, heavymachinegun, shotgun, raygun, railgun,
  shell, powershell, missile, bomb, flamethrower, mine,
  blaster, heavyblaster, forcefield,
  phantom, teleport,
  particledisintegrator, particlereintegrator, spinner, thewrath, energy
 }

 public final AimType aimType;

 public enum AimType {normal, ofVehicleTurret, auto}

 public Special(Vehicle vehicle, String s) {
  V = vehicle;
  type = Special.Type.valueOf(U.getString(s, 0));
  if (!type.name().contains(SL.particle) && type != Special.Type.spinner) {
   if (type == Type.energy) {
    sound = new Sound(type.name(), Double.POSITIVE_INFINITY);
   } else {
    String specialAudio = "";
    try {
     specialAudio = U.getString(s, 1);
    } catch (RuntimeException ignored) {
    }
    sound = new Sound(type.name() + specialAudio);
   }
  }
  homing = s.contains("homing");
  aimType = s.contains(SL.autoAim) ? Special.AimType.auto : s.contains("ofVehicleTurret") ? Special.AimType.ofVehicleTurret : AimType.normal;
 }

 public void load() {
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
  if (type == Type.energy) {
   EB = new EnergyBolt(V, this);
  } else if (!type.name().contains(SL.particle) && type != Type.spinner) {
   for (long n = Shot.defaultQuantity; --n >= 0; ) {
    shots.add(new Shot(V, this));
   }
   if (!type.name().contains(Type.blaster.name()) && type != Type.raygun && type != Type.forcefield && type != Type.mine && type != Type.thewrath) {
    for (Port port : ports) {
     port.spit = new Spit(this);
    }
   }
  }
  if (useSmallHits) {
   V.VA.hitShot = new Sound("hitShot", Double.POSITIVE_INFINITY);
  }
  if (ricochets) {
   V.VA.hitRicochet = new Sound("hitRicochet", Double.POSITIVE_INFINITY);
  }
  if (type == Type.thewrath) {
   V.P.wrathStuck = new boolean[VE.vehiclesInMatch];
  }
 }

 public void run(boolean gamePlay) {
  if (type == Type.energy) {
   EB.run(gamePlay);
  } else if (!type.name().contains(SL.particle) && type != Type.spinner && type != Type.phantom && type != Type.teleport) {
   if (gamePlay) {
    if (timer <= 0) {
     if (fire && !V.destroyed) {
      time();
      shoot();//<-Shoot comes before engaging wrath so that wrath sound gets played ONCE
      V.P.wrathEngaged = type == Type.thewrath;
      if (V.P.wrathEngaged) {
       for (int n = VE.vehiclesInMatch; --n >= 0; ) {
        V.P.wrathStuck[n] = false;
       }
      }
     }
    } else {
     timer -= VE.tick * (type != Type.thewrath || !V.P.wrathEngaged ? V.energyMultiple : 1);//<-Don't shorten wrath duration if energized
    }
    if (V.P.wrathEngaged) {
     shoot();
     V.thrusting = true;
     V.P.speed = Math.min(V.P.speed + ((U.random() < .5 ? V.accelerationStages[0] : V.accelerationStages[1]) * 4 * VE.tick), V.topSpeeds[2]);
     V.setDamage(Math.min(V.getDamage(false), V.durability));
     V.screenFlash = (.5 + U.random(.5)) / Math.max(Math.sqrt(U.distance(V)) * .015, 1);
     V.P.wrathEngaged = !(timer < 850) && V.P.wrathEngaged;
    }
   }
   if (!V.destroyed && type == Type.flamethrower) {
    for (Port port : ports) {
     port.spit.deploy(V, this, port);
    }
   }
   for (Shot shot : shots) {
    shot.runLogic(gamePlay);
   }
   for (Port port : ports) {
    if (port.spit != null) {
     port.spit.run(V, this, port, gamePlay);
    }
   }
  }
 }

 private void shoot() {
  for (Port port : ports) {
   double[] shotX = {port.X}, shotY = {port.Y}, shotZ = {port.Z};
   U.rotate(shotX, shotY, V.XY);
   U.rotate(shotY, shotZ, V.YZ);
   U.rotate(shotX, shotZ, V.XZ);
   shots.get(currentShot).deploy(port);
   currentShot = ++currentShot >= Shot.defaultQuantity ? 0 : currentShot;
  }
  for (Port port : ports) {
   if (port.spit != null) {
    port.spit.deploy(V, this, port);
   }
  }
  if (type == Type.phantom) {
   sound.loop(V.VA.distanceVehicleToCamera);
  } else if ((type != Type.flamethrower || VE.yinYang) && !V.P.wrathEngaged) {
   sound.play(V.VA.distanceVehicleToCamera * (type == Type.thewrath ? .5 : 1));
  }
 }

 public void time() {
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
