package ve.vehicles.specials;

import ve.instances.I;
import ve.ui.Match;
import ve.ui.UI;
import ve.utilities.Camera;
import ve.utilities.U;
import ve.vehicles.Vehicle;

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
    if (Math.abs(speed) < .01 * U.tick) {
     speed = 0;
    } else {
     speed += .01 * (speed > 0 ? -1 : 1) * U.tick;
    }
   } else {
    boolean runSpinner = false;
    for (var special : V.specials) {
     if (special.fire) {
      runSpinner = true;
      break;
     }
    }
    if (runSpinner) {
     double speedChange = U.tick * .005 * V.energyMultiple;
     speed += speed > 0 ? speedChange : speed < 0 ? -speedChange : (U.random() < .5 ? -1 : 1) * Double.MIN_VALUE;
    }
   }
  }
  XZ += speed * 120 * U.tick;
  while (XZ > 180) XZ -= 360;
  while (XZ < -180) XZ += 360;
  speed = U.clamp(-1, speed, 1);
  int spinSound = (int) (Math.round(Math.abs(speed) * 9) - 2);
  for (int n = V.VA.spinner.clipHolders.size(); --n >= 0; ) {
   if (n != spinSound) {
    V.VA.spinner.stop(n);
   }
  }
  if (gamePlay) {
   speed -= speed * .002 * U.tick;
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
     V.P.hitCheck(vehicle);//<-Must come BEFORE any damage is added, or the target vehicle might not be integral when checked!
     double damageAmount = vehicle.durability * absSpeed * speedReduction + (speedReduction >= 1 ? vehicle.damageCeiling() - vehicle.durability : 0);
     vehicle.addDamage(damageAmount);
     Match.scoreDamage[V.index < I.halfThePlayers() ? 0 : 1] += UI.status == UI.Status.replay ? 0 : damageAmount;
     if (absSpeed > .5) {
      for (var wheel : vehicle.wheels) {
       wheel.sparks(false);
      }
     }
     vehicle.deformParts();
     vehicle.throwChips(V.renderRadius * absSpeed, true);
    }
    if (vehicle.getsPushed >= 0) {
     vehicle.speedX += (U.random() < .5 ? 1 : -1) * V.renderRadius * absSpeed * speedReduction * 4;
     vehicle.speedZ += (U.random() < .5 ? 1 : -1) * V.renderRadius * absSpeed * speedReduction * 4;
    }
    if (vehicle.getsLifted >= 0) {
     vehicle.speedY += (U.random() < .5 ? 1 : -1) * V.renderRadius * absSpeed * speedReduction;
    }
   }
   speed -= speed * speedReduction;
   if (absSpeed > .125) {
    if (speedReduction >= 1) {
     V.setCameraShake(Camera.shakeIntensity.maxSpinnerHit);
     V.VA.massiveHit.play(Double.NaN, V.VA.distanceVehicleToCamera);
    } else if (absSpeed > .25) {
     V.setCameraShake(15);
     V.VA.crashHard.play(Double.NaN, V.VA.distanceVehicleToCamera);
    } else {
     V.VA.crashSoft.play(Double.NaN, V.VA.distanceVehicleToCamera);
    }
   }
  }
 }
}
