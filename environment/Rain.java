package ve.environment;

import javafx.scene.shape.Cylinder;

import ve.Camera;
import ve.Core;
import ve.Sound;
import ve.VE;
import ve.utilities.SL;
import ve.utilities.U;

import java.util.ArrayList;
import java.util.Collection;

public class Rain extends Core {
 private static final double wrapDistance = 3000;
 public static final Collection<Drop> raindrops = new ArrayList<>();
 public static Sound sound;

 static void load(String s) {
  if (s.contains(SL.rain)) {
   for (int n = 0; n < 1000; n++) {
    raindrops.add(new Drop());
   }
  }
 }

 public static void run(boolean update) {
  if (!raindrops.isEmpty()) {
   for (Drop raindrop : raindrops) {
    raindrop.run();
   }
   if (!VE.Match.muteSound && update) {
    sound.loop(Math.sqrt(U.distance(0, 0, Camera.Y, 0, 0, 0)) * Sound.standardDistance(1));
   } else {
    sound.stop();
   }
  }
 }

 public static class Drop extends Core {
  public final Cylinder C;

  Drop() {
   C = new Cylinder(.5, 4, 3);
   C.setScaleY(10);
   U.rotate(C, 0, U.random(360.));//<-For visual variation
   U.Nodes.add(C);
  }

  private void run() {
   X += E.Wind.speedX * VE.tick;
   Z += E.Wind.speedZ * VE.tick;
   Y += 200 * VE.tick;
   if (Y > 0 || Math.abs(X - Camera.X) > wrapDistance || Math.abs(Y - Camera.Y) > wrapDistance || Math.abs(Z - Camera.Z) > wrapDistance) {
    X = Camera.X + U.randomPlusMinus(wrapDistance);
    Y = Camera.Y + U.randomPlusMinus(wrapDistance);
    Z = Camera.Z + U.randomPlusMinus(wrapDistance);
   }
   if (Y > E.Storm.stormCloudY && U.render(this, 200)) {
    U.setTranslate(C, this);
    C.setVisible(true);
   } else {
    C.setVisible(false);
   }
  }
 }
}
