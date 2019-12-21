package ve.trackElements;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.*;
import ve.utilities.SL;
import ve.utilities.U;
import ve.vehicles.Vehicle;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Bonus extends Core {
 ;
 public static final Sphere big = new Sphere(500);
 public static final List<Ball> balls = new ArrayList<>();
 public static Sound sound;

 static {
  U.setMaterialSecurely(big, new PhongMaterial());
  for (int n = 0; n < 64; n++) {
   balls.add(new Ball());
   U.setMaterialSecurely(balls.get(n), new PhongMaterial());
  }
 }

 public static class Ball extends Sphere {//<-Not worth extending Core here

  double X, Y, Z, speedX, speedY, speedZ;

  void run() {
   speedY += U.randomPlusMinus(6.);
   speedX += U.randomPlusMinus(6.);
   speedZ += U.randomPlusMinus(6.);
   speedX *= .99;
   speedY *= .99;
   speedZ *= .99;
   X += speedX * VE.tick;
   Vehicle vehicle = VE.vehicles.get(VE.bonusHolder);
   double driftTolerance = vehicle.absoluteRadius * .6;
   if (Math.abs(X) > driftTolerance) {
    speedX *= -1;
    X *= .999;
   }
   Y += speedY * VE.tick;
   if (Math.abs(Y) > driftTolerance) {
    speedY *= -1;
    Y *= .999;
   }
   Z += speedZ * VE.tick;
   if (Math.abs(Z) > driftTolerance) {
    speedZ *= -1;
    Z *= .999;
   }
   if (U.getDepth(vehicle.X + X, vehicle.Y + Y, vehicle.Z + Z) > 0) {
    U.setTranslate(this, vehicle.X + X, vehicle.Y + Y, vehicle.Z + Z);
    setVisible(true);
    U.Phong.setDiffuseRGB((PhongMaterial) getMaterial(), U.random(), U.random(), U.random());
   } else {
    setVisible(false);
   }
  }
 }

 public void run() {
  if (VE.bonusHolder < 0) {
   if (U.getDepth(this) > -big.getRadius()) {
    U.setTranslate(big, this);
    U.Phong.setDiffuseRGB((PhongMaterial) big.getMaterial(), U.random(), U.random(), U.random());
    big.setVisible(true);
   } else {
    big.setVisible(false);
   }
   for (Bonus.Ball bonusBall : balls) {
    bonusBall.setVisible(false);
   }
  } else {
   big.setVisible(false);
   X = VE.vehicles.get(VE.bonusHolder).X;
   Y = VE.vehicles.get(VE.bonusHolder).Y;
   Z = VE.vehicles.get(VE.bonusHolder).Z;
   for (Bonus.Ball bonusBall : balls) {
    bonusBall.run();
   }
  }
  if (VE.Match.started) {
   if (Network.mode == Network.Mode.OFF) {
    for (Vehicle vehicle : VE.vehicles) {
     if (VE.bonusHolder < 0 && vehicle.isIntegral() && !vehicle.phantomEngaged && U.distance(vehicle.X, X, vehicle.Y, Y, vehicle.Z, Z) < vehicle.collisionRadius + big.getRadius()) {
      setHolder(vehicle);
     }
    }
    VE.bonusHolder = VE.bonusHolder > -1 && !VE.vehicles.get(VE.bonusHolder).isIntegral() ? -1 : VE.bonusHolder;
   } else {
    Vehicle V = VE.vehicles.get(VE.userPlayerIndex);
    if (Network.bonusHolder < 0 && V.isIntegral() && !V.phantomEngaged && U.distance(V.X, X, V.Y, Y, V.Z, Z) < V.collisionRadius + big.getRadius()) {
     Network.bonusHolder = VE.userPlayerIndex;
     if (Network.mode == Network.Mode.HOST) {
      for (PrintWriter PW : Network.out) {
       PW.println("BONUS0");
      }
     } else {
      Network.out.get(0).println(SL.BONUS);
     }
    }
    int setHolder = Network.bonusHolder < 0 ? Network.bonusHolder : VE.bonusHolder;
    if (setHolder > -1 && !VE.vehicles.get(setHolder).isIntegral()) {
     Network.bonusHolder = VE.bonusHolder = -1;
     if (Network.mode == Network.Mode.HOST) {
      for (PrintWriter PW : Network.out) {
       PW.println(SL.BonusOpen);
      }
     } else {
      Network.out.get(0).println(SL.BonusOpen);
     }
    }
    if (VE.bonusHolder != Network.bonusHolder) {
     VE.bonusHolder = Network.bonusHolder;
     if (VE.bonusHolder > -1) {
      setHolder(VE.vehicles.get(VE.bonusHolder));
     }
    }
   }
  }
 }

 public static void setHolder(Vehicle vehicle) {
  VE.bonusHolder = vehicle.index;
  for (Bonus.Ball bonusBall : balls) {
   bonusBall.setRadius(VE.vehicles.get(VE.bonusHolder).absoluteRadius * .02);
   bonusBall.X = bonusBall.Y = bonusBall.Z = bonusBall.speedX = bonusBall.speedY = bonusBall.speedZ = 0;
  }
  if (VE.Options.headsUpDisplay) {
   sound.playIfNotPlaying(0);
  }
 }
}