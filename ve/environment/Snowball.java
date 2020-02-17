package ve.environment;

import javafx.scene.shape.Sphere;

import ve.instances.CoreAdvanced;
import ve.utilities.Camera;
import ve.utilities.Nodes;
import ve.utilities.U;

import java.util.ArrayList;
import java.util.Collection;

public enum Snowball {
 ;
 public static final Collection<Instance> instances = new ArrayList<>();
 private static final long wrapDistance = 2000;

 public static void run() {
  for (var snowball : instances) {
   snowball.run();
  }
 }

 public static class Instance extends CoreAdvanced {
  final Sphere S;

  public Instance() {
   X = U.randomPlusMinus(1000000.);
   Y = U.randomPlusMinus(1000000.);
   Z = U.randomPlusMinus(1000000.);
   absoluteRadius = 1 + U.random(9.);
   S = new Sphere(absoluteRadius);
   Nodes.add(S);
   S.setVisible(false);
  }

  private void run() {
   speedY += U.randomPlusMinus(6.) + (6 * U.random(U.tick));
   speedX += U.randomPlusMinus(6.);
   speedZ += U.randomPlusMinus(6.);
   speedX *= .9;
   speedY *= .9;
   speedZ *= .9;
   X += speedX * U.tick + (Wind.speedX * U.tick);
   Y += speedY * U.tick;
   Z += speedZ * U.tick + (Wind.speedZ * U.tick);
   while (Math.abs(X - Camera.C.X) > wrapDistance) {
    X += (X > Camera.C.X ? -wrapDistance : wrapDistance) << 1;
   }
   while (Math.abs(Y - Camera.C.Y) > wrapDistance) {
    Y += (Y > Camera.C.Y ? -wrapDistance : wrapDistance) << 1;
   }
   while (Math.abs(Z - Camera.C.Z) > wrapDistance) {
    Z += (Z > Camera.C.Z ? -wrapDistance : wrapDistance) << 1;
   }
   if (U.render(this, true, true)) {
    U.setTranslate(S, this);
    S.setVisible(true);
   } else {
    S.setVisible(false);
   }
  }
 }
}
