package ve.environment.storm;

import javafx.scene.shape.Cylinder;

import ve.environment.Wind;
import ve.instances.Core;
import ve.ui.Match;
import ve.utilities.*;
import ve.utilities.sound.Controlled;
import ve.utilities.sound.Sounds;

import java.util.ArrayList;
import java.util.Collection;

public enum Rain {
 ;
 private static final double wrapDistance = 3000;
 public static final Collection<Drop> raindrops = new ArrayList<>();
 private static Controlled sound;

 static void load(String s) {
  if (s.contains(D.rain)) {
   for (int n = 0; n < 1000; n++) {
    raindrops.add(new Drop());
   }
   sound = new Controlled(D.rain);
  }
 }

 public static void run(boolean update) {
  if (!raindrops.isEmpty()) {
   for (Drop raindrop : raindrops) {
    raindrop.run();
   }
   if (!Match.muteSound && update) {
    sound.loop(Math.sqrt(U.distance(0, 0, Camera.C.Y, 0, 0, 0)) * Sounds.standardGain(1));//Should never be called since rain doesn't exist
   } else {
    sound.stop();
   }
  }
 }

 public static void closeSound() {
  if (sound != null) sound.close();
 }

 static class Drop extends Core {
  final Cylinder C;

  Drop() {
   C = new Cylinder(.5, 4, 3);
   C.setScaleY(10);
   U.rotate(C, 0, U.random(360.));//<-For visual variation
   Nodes.add(C);
  }

  private void run() {
   X += Wind.speedX * U.tick;
   Z += Wind.speedZ * U.tick;
   Y += 200 * U.tick;
   if (Y > 0 || Math.abs(X - Camera.C.X) > wrapDistance || Math.abs(Y - Camera.C.Y) > wrapDistance || Math.abs(Z - Camera.C.Z) > wrapDistance) {
    X = Camera.C.X + U.randomPlusMinus(wrapDistance);
    Y = Camera.C.Y + U.randomPlusMinus(wrapDistance);
    Z = Camera.C.Z + U.randomPlusMinus(wrapDistance);
   }
   if (Y > Storm.cloudY && U.render(this, 200, false, false)) {
    U.setTranslate(C, this);
    C.setVisible(true);
   } else {
    C.setVisible(false);
   }
  }
 }
}
