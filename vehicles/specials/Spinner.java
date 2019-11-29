package ve.vehicles.specials;

import ve.Camera;
import ve.VE;
import ve.utilities.U;
import ve.vehicles.Vehicle;
import ve.vehicles.VehiclePart;

public class Spinner {
 private final Vehicle V;
 public double XZ;
 public double speed;

 public Spinner(Vehicle vehicle) {
  V = vehicle;
 }

 public void run(boolean gamePlay) {
  if (gamePlay) {
   if (V.destroyed) {
    if (Math.abs(speed) < .01 * VE.tick) {
     speed = 0;
    } else {
     speed += .01 * (speed > 0 ? -1 : 1) * VE.tick;
    }
   } else {
    boolean runSpinner = false;
    for (Special special : V.specials) {
     if (special.fire) {
      runSpinner = true;
      break;
     }
    }
    if (runSpinner) {
     double speedChange = VE.tick * .005 * V.energyMultiple;
     speed += speed > 0 ? speedChange : speed < 0 ? -speedChange : (U.random() < .5 ? -1 : 1) * Double.MIN_VALUE;
    }
   }
  }
  XZ += speed * 120 * VE.tick;
  while (XZ > 180) XZ -= 360;
  while (XZ < -180) XZ += 360;
  speed = U.clamp(-1, speed, 1);
  int spinSound = (int) (Math.round(Math.abs(speed) * 9) - 2);
  for (int n = V.VA.spinner.clips.size(); --n >= 0; ) {
   if (n != spinSound) {
    V.VA.spinner.stop(n);
   }
  }
  if (gamePlay) {
   speed *= .999;
   if (spinSound >= 0) {
    V.VA.spinner.loop(spinSound, V.VA.distanceVehicleToCamera);
   }
  } else if (spinSound >= 0) {
   V.VA.spinner.stop(spinSound);
  }
 }

 public void hit(Vehicle vehicle) {
  if (U.random() < .5) {
   double absSpeed = Math.abs(speed), speedReduction = absSpeed > .95 ? 1 : U.random();
   if (vehicle != null) {
    if (absSpeed > .125) {
     double damageAmount = vehicle.durability * absSpeed * speedReduction + (speedReduction >= 1 ? Double.MIN_VALUE : 0);
     vehicle.addDamage(damageAmount);
     VE.Match.scoreDamage[vehicle.index < VE.vehiclesInMatch >> 1 ? 0 : 1] += VE.status == VE.Status.replay ? 0 : damageAmount;
     V.P.hitCheck(vehicle);
     for (VehiclePart part : vehicle.parts) {
      part.deform();
      part.throwChip(U.randomPlusMinus(V.renderRadius * absSpeed));
     }
    }
    if (vehicle.getsPushed >= 0) {
     double speedX = (U.random() < .5 ? 1 : -1) * V.renderRadius * absSpeed * speedReduction * 4,
     speedZ = (U.random() < .5 ? 1 : -1) * V.renderRadius * absSpeed * speedReduction * 4;
     vehicle.P.speedX += speedX;
     vehicle.P.speedZ += speedZ;
    }
    if (vehicle.getsLifted >= 0) {
     double speedY = (U.random() < .5 ? 1 : -1) * V.renderRadius * absSpeed * speedReduction;
     vehicle.P.speedY += speedY;
    }
   }
   speed -= speed * speedReduction;
   if (absSpeed > .125) {
    if (speedReduction >= 1) {
     V.setCameraShake(Camera.shakePresets.maxSpinnerHit);
     V.VA.massiveHit.play(Double.NaN, V.VA.distanceVehicleToCamera);
    } else if (absSpeed > .25) {
     V.setCameraShake(15);
     V.VA.crashHard.play(U.random(5), V.VA.distanceVehicleToCamera);
    } else {
     V.VA.crashSoft.play(U.random(3), V.VA.distanceVehicleToCamera);
    }
   }
  }
 }
}
