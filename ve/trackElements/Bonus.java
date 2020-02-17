package ve.trackElements;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.environment.E;
import ve.instances.Core;
import ve.instances.CoreAdvanced;
import ve.instances.I;
import ve.ui.Match;
import ve.ui.options.Options;
import ve.utilities.*;
import ve.utilities.sound.Controlled;
import ve.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.Collection;

public class Bonus extends Core {//<-fixme--balls drifted away from vehicle (om Linux)
 ;
 public static int holder = -1;
 public static final Sphere big = new Sphere(500);
 public static final Collection<Ball> balls = new ArrayList<>();
 public static double startX, startY, startZ;
 public static Controlled sound;

 static {
  U.setMaterialSecurely(big, new PhongMaterial());
  for (long n = 0; n < 64; n++) {
   balls.add(new Ball());
  }
 }

 public static void load() {//<-Must be called AFTER all map parts are loaded, since a moundSit is performed
  TE.bonus.X = startX;
  TE.bonus.Y = startY;
  TE.bonus.Z = startZ;
  E.setMoundSit(TE.bonus, false);
  Nodes.add(big);
  for (var ball : balls) {
   Nodes.add(ball.S);
  }
 }

 public void run() {
  if (holder < 0) {
   if (U.getDepth(this) > -big.getRadius()) {
    U.setTranslate(big, this);
    Phong.setDiffuseRGB((PhongMaterial) big.getMaterial(), U.random(), U.random(), U.random());
    big.setVisible(true);
   } else {
    big.setVisible(false);
   }
   for (var ball : balls) {
    ball.S.setVisible(false);
   }
  } else {
   big.setVisible(false);
   X = I.vehicles.get(holder).X;
   Y = I.vehicles.get(holder).Y;
   Z = I.vehicles.get(holder).Z;
   for (var ball : balls) {
    ball.run();
   }
  }
  if (Match.started) {
   if (Network.mode == Network.Mode.OFF) {
    for (var vehicle : I.vehicles) {
     if (holder < 0 && vehicle.isIntegral() && !vehicle.phantomEngaged && U.distance(this, vehicle) < vehicle.collisionRadius + big.getRadius()) {
      setHolder(vehicle);
     }
    }
    if (holder > -1 && !I.vehicles.get(holder).isIntegral()) {
     holder = -1;
    }
   } else {
    Vehicle V = I.vehicles.get(I.userPlayerIndex);
    if (Network.bonusHolder < 0 && V.isIntegral() && !V.phantomEngaged && U.distance(this, V) < V.collisionRadius + big.getRadius()) {
     Network.bonusHolder = I.userPlayerIndex;
     if (Network.mode == Network.Mode.HOST) {
      for (var PW : Network.out) {
       PW.println("BONUS0");
      }
     } else {
      Network.out.get(0).println(D.BONUS);
     }
    }
    int setHolder = Network.bonusHolder < 0 ? Network.bonusHolder : holder;
    if (setHolder > -1 && !I.vehicles.get(setHolder).isIntegral()) {
     Network.bonusHolder = holder = -1;
     if (Network.mode == Network.Mode.HOST) {
      for (var PW : Network.out) {
       PW.println(D.BonusOpen);
      }
     } else {
      Network.out.get(0).println(D.BonusOpen);
     }
    }
    if (holder != Network.bonusHolder) {
     holder = Network.bonusHolder;
     if (holder > -1) {
      setHolder(I.vehicles.get(holder));
     }
    }
   }
  }
 }

 public static void setHolder(Vehicle vehicle) {
  holder = vehicle.index;
  for (var ball : balls) {
   ball.S.setRadius(I.vehicles.get(holder).absoluteRadius * .02);
   ball.X = ball.Y = ball.Z = ball.speedX = ball.speedY = ball.speedZ = 0;
  }
  if (Options.headsUpDisplay) {
   sound.playIfNotPlaying(0);
  }
 }

 public static class Ball extends CoreAdvanced {

  public final Sphere S = new Sphere();

  {
   U.setMaterialSecurely(S, new PhongMaterial());
  }

  void run() {
   speedY += U.randomPlusMinus(6.);
   speedX += U.randomPlusMinus(6.);
   speedZ += U.randomPlusMinus(6.);
   speedX *= .99;
   speedY *= .99;
   speedZ *= .99;
   X += speedX * U.tick;
   Vehicle vehicle = I.vehicles.get(holder);
   double driftTolerance = vehicle.absoluteRadius * .6;
   if (Math.abs(X) > driftTolerance) {
    speedX *= -1;
    X *= .999;
   }
   Y += speedY * U.tick;
   if (Math.abs(Y) > driftTolerance) {
    speedY *= -1;
    Y *= .999;
   }
   Z += speedZ * U.tick;
   if (Math.abs(Z) > driftTolerance) {
    speedZ *= -1;
    Z *= .999;
   }
   if (U.getDepth(vehicle.X + X, vehicle.Y + Y, vehicle.Z + Z) > 0) {
    U.setTranslate(S, vehicle.X + X, vehicle.Y + Y, vehicle.Z + Z);
    S.setVisible(true);
    Phong.setDiffuseRGB((PhongMaterial) S.getMaterial(), U.random(), U.random(), U.random());
   } else {
    S.setVisible(false);
   }
  }
 }

 static void reset() {
  startX = startY = startZ = 0;
 }

 public static void closeSound() {
  if (sound != null) sound.close();
 }
}