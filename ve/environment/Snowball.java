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
 private static final double wrapDistance = 2000;

 public static void run() {
  for (Snowball.Instance snowball : instances) {
   snowball.run();
  }
 }

 public static class Instance extends CoreAdvanced {
  public final Sphere round, lowResolution;
  private double rotateAngle;
  private final double[] rotation = new double[2];

  public Instance() {
   X = U.randomPlusMinus(1000000.);
   Y = U.randomPlusMinus(1000000.);
   Z = U.randomPlusMinus(1000000.);
   rotation[0] = U.randomPlusMinus(45.);
   rotation[1] = U.randomPlusMinus(45.);
   double radius = 1 + U.random(9.);
   round = new Sphere(radius);
   lowResolution = new Sphere(radius, 0);
   Nodes.add(round, lowResolution);
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
   while (Math.abs(X - Camera.X) > wrapDistance) {
    X += (X > Camera.X ? -wrapDistance : wrapDistance) * 2;
    rotateAngle = 0;
   }
   while (Math.abs(Y - Camera.Y) > wrapDistance) {
    Y += (Y > Camera.Y ? -wrapDistance : wrapDistance) * 2;
    rotateAngle = 0;
   }
   while (Math.abs(Z - Camera.Z) > wrapDistance) {
    Z += (Z > Camera.Z ? -wrapDistance : wrapDistance) * 2;
    rotateAngle = 0;
   }
   rotateAngle += U.tick;
   round.setVisible(false);
   lowResolution.setVisible(false);
   if (U.getDepth(this) > 0) {
    Sphere S = U.averageFPS < 30 ? lowResolution : round;
    U.setTranslate(S, this);
    if (S == lowResolution) {
     U.rotate(S, rotation[0] * rotateAngle, rotation[1] * rotateAngle);
    }
    S.setVisible(true);
   }
  }
 }
}
