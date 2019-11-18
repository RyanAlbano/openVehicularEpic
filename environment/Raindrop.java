package ve.environment;

import javafx.scene.shape.Cylinder;

import ve.Camera;
import ve.Core;
import ve.VE;
import ve.utilities.U;

import java.util.ArrayList;
import java.util.Collection;

public class Raindrop extends Core {
 private static final double wrapDistance = 3000;
 public static final Collection<Instance> instances = new ArrayList<>();

 static void load(String s) {
  if (s.contains("rain")) {
   for (int n = 0; n < 1000; n++) {
    instances.add(new Instance());
   }
  }
 }

 public static void run() {
  for (Raindrop.Instance raindrop : instances) {
   raindrop.run();
  }
 }

 public static class Instance extends Core {
  public final Cylinder C;

  Instance() {
   C = new Cylinder(.5, 4, 3);
   C.setScaleY(10);
   U.rotate(C, 0, U.random(360.));//<-For visual variation
   U.Nodes.add(C);
  }

  private void run() {
   if (E.Wind.maxPotency > 0) {
    X += E.Wind.speedX * VE.tick;
    Z += E.Wind.speedZ * VE.tick;
   }
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
