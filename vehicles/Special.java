package ve.vehicles;

import ve.Sound;

import java.util.*;

public class Special {

 public Vehicle.specialType type = Vehicle.specialType.none;
 int currentShot;
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
 AimType aimType = AimType.normal;

 enum AimType {normal, ofVehicleTurret, auto}

 void time() {
  timer =
  type == Vehicle.specialType.gun ? 8 :
  type.name().contains(Vehicle.specialType.machinegun.name()) ? 1 :
  type == Vehicle.specialType.minigun ? .3 :
  type == Vehicle.specialType.shotgun ? 35 :
  type == Vehicle.specialType.shell ? 30 :
  type == Vehicle.specialType.railgun || type == Vehicle.specialType.missile || type == Vehicle.specialType.powershell || type == Vehicle.specialType.mine ? 200 :
  type == Vehicle.specialType.blaster ? 10 :
  type == Vehicle.specialType.heavyblaster || type == Vehicle.specialType.bomb ? 100 :
  type == Vehicle.specialType.forcefield ? 20 :
  type == Vehicle.specialType.thewrath ? 1000 :
  0;
 }
}
