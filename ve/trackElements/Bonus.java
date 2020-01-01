package ve.trackElements;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.instances.Core;
import ve.instances.I;
import ve.ui.Match;
import ve.ui.Options;
import ve.ui.UI;
import ve.utilities.Network;
import ve.utilities.SL;
import ve.utilities.Sound;
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
   X += speedX * UI.tick;
   Vehicle vehicle = I.vehicles.get(UI.bonusHolder);
   double driftTolerance = vehicle.absoluteRadius * .6;
   if (Math.abs(X) > driftTolerance) {
    speedX *= -1;
    X *= .999;
   }
   Y += speedY * UI.tick;
   if (Math.abs(Y) > driftTolerance) {
    speedY *= -1;
    Y *= .999;
   }
   Z += speedZ * UI.tick;
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
  if (UI.bonusHolder < 0) {
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
   X = I.vehicles.get(UI.bonusHolder).X;
   Y = I.vehicles.get(UI.bonusHolder).Y;
   Z = I.vehicles.get(UI.bonusHolder).Z;
   for (Bonus.Ball bonusBall : balls) {
    bonusBall.run();
   }
  }
  if (Match.started) {
   if (Network.mode == Network.Mode.OFF) {
    for (Vehicle vehicle : I.vehicles) {
     if (UI.bonusHolder < 0 && vehicle.isIntegral() && !vehicle.phantomEngaged && U.distance(vehicle.X, X, vehicle.Y, Y, vehicle.Z, Z) < vehicle.collisionRadius + big.getRadius()) {
      setHolder(vehicle);
     }
    }
    UI.bonusHolder = UI.bonusHolder > -1 && !I.vehicles.get(UI.bonusHolder).isIntegral() ? -1 : UI.bonusHolder;
   } else {
    Vehicle V = I.vehicles.get(UI.userPlayerIndex);
    if (Network.bonusHolder < 0 && V.isIntegral() && !V.phantomEngaged && U.distance(V.X, X, V.Y, Y, V.Z, Z) < V.collisionRadius + big.getRadius()) {
     Network.bonusHolder = UI.userPlayerIndex;
     if (Network.mode == Network.Mode.HOST) {
      for (PrintWriter PW : Network.out) {
       PW.println("BONUS0");
      }
     } else {
      Network.out.get(0).println(SL.BONUS);
     }
    }
    int setHolder = Network.bonusHolder < 0 ? Network.bonusHolder : UI.bonusHolder;
    if (setHolder > -1 && !I.vehicles.get(setHolder).isIntegral()) {
     Network.bonusHolder = UI.bonusHolder = -1;
     if (Network.mode == Network.Mode.HOST) {
      for (PrintWriter PW : Network.out) {
       PW.println(SL.BonusOpen);
      }
     } else {
      Network.out.get(0).println(SL.BonusOpen);
     }
    }
    if (UI.bonusHolder != Network.bonusHolder) {
     UI.bonusHolder = Network.bonusHolder;
     if (UI.bonusHolder > -1) {
      setHolder(I.vehicles.get(UI.bonusHolder));
     }
    }
   }
  }
 }

 public static void setHolder(Vehicle vehicle) {
  UI.bonusHolder = vehicle.index;
  for (Bonus.Ball bonusBall : balls) {
   bonusBall.setRadius(I.vehicles.get(UI.bonusHolder).absoluteRadius * .02);
   bonusBall.X = bonusBall.Y = bonusBall.Z = bonusBall.speedX = bonusBall.speedY = bonusBall.speedZ = 0;
  }
  if (Options.headsUpDisplay) {
   sound.playIfNotPlaying(0);
  }
 }
}