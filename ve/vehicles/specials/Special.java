package ve.vehicles.specials;

import ve.instances.I;
import ve.ui.UI;
import ve.utilities.D;
import ve.utilities.U;
import ve.utilities.sound.FireAndForget;
import ve.utilities.sound.Sound;
import ve.utilities.sound.Sounds;
import ve.vehicles.Vehicle;

import java.util.*;

public class Special {

 final Vehicle V;
 public EnergyBolt EB;
 public final Type type;
 private int currentShot;
 public double randomPosition, randomAngle;
 public double timer, timerMultiple = 1;
 public double speed;
 public double diameter;
 double damageDealt;
 double pushPower;
 double length;
 double width;
 public final boolean homing;
 boolean hasThrust;
 boolean ricochets, useSmallHits;
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
  particledisintegrator, particlereintegrator, spinner, thewrath, energy,
  bumpIgnore
 }

 public final AimType aimType;

 public enum AimType {normal, ofVehicleTurret, auto}

 public Special(Vehicle vehicle, String s) {
  V = vehicle;
  type = Special.Type.valueOf(U.getString(s, 0));
  if (V.realVehicle/*<-Memory leak possible if this is not checked!*/ && !type.name().contains(D.particle) && type != Special.Type.spinner && type != Type.bumpIgnore) {
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
  aimType = s.contains(D.autoAim) ? Special.AimType.auto : s.contains("ofVehicleTurret") ? Special.AimType.ofVehicleTurret : AimType.normal;
 }

 private boolean hasShots() {
  return type != Type.bumpIgnore && type != Type.spinner && type != Type.phantom && type != Type.teleport && type != Type.energy &&
  !type.name().contains(D.particle);
 }

 public void load() {
  boolean smokeyWeapon = false;
  if (type == Type.gun) {
   speed = 3000;
   diameter = 25;
   damageDealt = 100;
   pushPower = 2;
   width = 4;
   length = width * 3;
   ricochets = useSmallHits = V.hasShooting = smokeyWeapon = true;
  } else if (type == Type.machinegun) {
   speed = 3000;
   diameter = 25;
   damageDealt = 50;
   pushPower = 1;
   width = 4;
   length = width * 3;
   ricochets = useSmallHits = V.hasShooting = smokeyWeapon = true;
  } else if (type == Type.minigun) {
   speed = 3000;
   diameter = 10;
   damageDealt = 25;
   pushPower = 0;
   width = 2;
   length = width * 4;
   ricochets = useSmallHits = V.hasShooting = smokeyWeapon = true;
  } else if (type == Type.heavymachinegun) {
   speed = 3000;
   diameter = 50;
   damageDealt = 100;
   pushPower = 50;
   width = 5;
   length = width * 3;
   ricochets = useSmallHits = V.hasShooting = smokeyWeapon = true;
  } else if (type == Type.shotgun) {
   speed = 3000;
   diameter = 25;
   damageDealt = 25;
   pushPower = 1;
   width = 6;
   length = width * 2;
   ricochets = useSmallHits = smokeyWeapon = true;
  } else if (type == Type.raygun) {
   speed = 15000;
   diameter = 10;
   damageDealt = 100;//<-Multiplied by tick in-game
   pushPower = 0;
   width = 50;
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
   hasThrust = V.hasShooting = smokeyWeapon = true;
  } else if (type == Type.powershell) {
   speed = 3000;
   diameter = 200;
   damageDealt = 15000;
   pushPower = 1000;
   width = 24;
   length = width * 2;
   V.explosionType = Vehicle.ExplosionType.normal;
   AIAimPrecision = 10;
   hasThrust = V.hasShooting = smokeyWeapon = true;
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
   V.hasShooting = smokeyWeapon = true;
  } else if (type == Type.missile) {
   speed = 500;
   diameter = 100;
   damageDealt = 1000;
   pushPower = 1000;
   width = 6;
   length = width * 4;
   V.explosionType = Vehicle.ExplosionType.normal;
   AIAimPrecision = 10;
   hasThrust = V.hasShooting = smokeyWeapon = true;
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
  if (type == Type.bumpIgnore) {
   V.bumpIgnore = true;
  } else if (type == Type.energy) {
   EB = new EnergyBolt(V, this);
  } else if (hasShots()) {
   for (long n = Shot.defaultQuantity; --n >= 0; ) {
    shots.add(new Shot(V, this));
   }
   if (!type.name().contains(Type.blaster.name()) && type != Type.raygun && type != Type.forcefield && type != Type.mine && type != Type.thewrath) {
    for (Port port : ports) {
     port.spit = new Spit(this, port);
    }
   }
   if (smokeyWeapon) {
    for (Port port : ports) {
     port.addSmokes(this);
    }
   }
  }
  //*Checking if null because these are single objects OUTSIDE of this class--loading them multiple times is bad (for the sounds, especially)
  //**No need to check realVehicle here--void is already checked for such
  if (useSmallHits && V.VA.hitShot == null) {//* **
   V.VA.hitShot = Sounds.softwareBased ? Sounds.hitShot : new FireAndForget(D.hitShot, Double.POSITIVE_INFINITY);
  }
  if (ricochets && V.VA.hitRicochet == null) {//* **
   V.VA.hitRicochet = Sounds.softwareBased ? Sounds.hitRicochet : new FireAndForget(D.hitRicochet, Double.POSITIVE_INFINITY);
  }
  if (type == Type.thewrath && V.P.wrathStuck == null) {//*
   V.P.wrathStuck = new boolean[I.vehiclesInMatch];
  }
 }

 public void run(boolean gamePlay, double V_sinXZ, double V_cosXZ, double V_sinYZ, double V_cosYZ, double V_sinXY, double V_cosXY) {
  if (type != Type.energy && !type.name().contains(D.particle) && type != Type.spinner && type != Type.phantom && type != Type.teleport) {
   if (gamePlay) {
    if (timer <= 0) {
     if (fire && !V.destroyed) {
      time();
      fire(V_sinXZ, V_cosXZ, V_sinYZ, V_cosYZ, V_sinXY, V_cosXY);//<-Comes before engaging wrath so that wrath sound gets played ONCE
      V.P.wrathEngaged = type == Type.thewrath;
      if (V.P.wrathEngaged) {
       for (int n = I.vehiclesInMatch; --n >= 0; ) {
        V.P.wrathStuck[n] = false;
       }
      }
     }
    } else {
     timer -= U.tick * (type != Type.thewrath || !V.P.wrathEngaged ? V.energyMultiple : 1);//<-While running wrath, don't shorten duration if energized
    }
    if (V.P.wrathEngaged) {
     fire(V_sinXZ, V_cosXZ, V_sinYZ, V_cosYZ, V_sinXY, V_cosXY);
     V.thrusting = true;
     V.P.speed = Math.min(V.P.speed + ((U.random() < .5 ? V.accelerationStages[0] : V.accelerationStages[1]) * 4 * U.tick), V.topSpeeds[2]);
     V.setDamage(Math.min(V.getDamage(false), V.durability));
     V.screenFlash = (.5 + U.random(.5)) / Math.max(Math.sqrt(U.distance(V)) * .015, 1);
     V.P.wrathEngaged = !(timer < 850) && V.P.wrathEngaged;
    }
   }
   if (!V.destroyed && type == Type.flamethrower) {
    for (Port port : ports) {
     port.spit.deploy(V_sinXY, V_cosXY);
    }
   }
   for (Shot shot : shots) {
    shot.runLogic(gamePlay);
   }
   for (Port port : ports) {
    if (port.spit != null) {
     port.spit.runLogic(gamePlay, V_sinXZ, V_cosXZ, V_sinYZ, V_cosYZ, V_sinXY, V_cosXY);
    }
    if (port.smokes != null) {
     for (PortSmoke smoke : port.smokes) {
      smoke.runLogic();
     }
    }
   }
  }
 }

 private void fire(double V_sinXZ, double V_cosXZ, double V_sinYZ, double V_cosYZ, double V_sinXY, double V_cosXY) {
  for (Port port : ports) {
   shots.get(currentShot).deploy(port, V_sinXZ, V_cosXZ, V_sinYZ, V_cosYZ, V_sinXY, V_cosXY);
   currentShot = ++currentShot >= Shot.defaultQuantity ? 0 : currentShot;
   if (port.spit != null) {
    port.spit.deploy(V_sinXY, V_cosXY);
   }
   if (port.smokes != null) {
    double[] smokeX = {port.X}, smokeY = {port.Y}, smokeZ = {port.Z};
    if (aimType != Special.AimType.normal) {
     U.rotateWithPivot(smokeZ, smokeY, V.VT.pivotY, V.VT.pivotZ, V.VT.YZ);
     U.rotateWithPivot(smokeX, smokeZ, 0, V.VT.pivotZ, V.VT.XZ);
    }
    U.rotate(smokeX, smokeY, V_sinXY, V_cosXY);
    U.rotate(smokeY, smokeZ, V_sinYZ, V_cosYZ);
    U.rotate(smokeX, smokeZ, V_sinXZ, V_cosXZ);
    smokeX[0] += V.X;
    smokeY[0] += V.Y;
    smokeZ[0] += V.Z;
    for (long n = PortSmoke.emitQuantity(this); --n >= 0; ) {
     port.smokes.get(port.currentSmoke).deploy(smokeX[0], smokeY[0], smokeZ[0], V_sinXY, V_cosXY);
     port.currentSmoke = ++port.currentSmoke >= Port.defaultSmokeQuantity ? 0 : port.currentSmoke;
    }
   }
  }
  if (type == Type.bumpIgnore) {
   if (UI.status == UI.Status.play) {
    V.bumpIgnore = !V.bumpIgnore;
   }
  } else if (type == Type.phantom) {
   sound.loop(V.VA.distanceVehicleToCamera);
  } else if ((type != Type.flamethrower || U.yinYang) && !V.P.wrathEngaged) {
   sound.play(V.VA.distanceVehicleToCamera * (type == Type.thewrath ? .5 : 1));
  }
 }

 public void time() {
  timer =
  type == Type.gun || type == Type.bumpIgnore ? 8 :
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
  timer *= timerMultiple;
 }

 public boolean throughWeapon() {
  return type == Type.raygun || type == Type.flamethrower || type == Type.thewrath || type.name().contains(Type.blaster.name());
 }
}
