package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.Core;
import ve.utilities.U;

public class VolcanoRock extends Core {

 public final Sphere S;
 public double speedX, speedY, speedZ;
 private final double[] rotation = new double[3];
 boolean groundHit, isLava;

 VolcanoRock(double radius, int divisions) {
  S = new Sphere(radius, divisions);
 }

 public void deploy() {
  X = E.volcanoX;
  Z = E.volcanoZ;
  Y = -50000;
  speedX = U.randomPlusMinus(1000.);
  speedZ = U.randomPlusMinus(1000.);
  speedY = -U.random(1000.);
  rotation[0] = U.randomPlusMinus(45.);
  rotation[1] = U.randomPlusMinus(45.);
  groundHit = false;
 }

 public void run() {
  if (U.render(this, -S.getRadius())) {
   U.rotate(S, rotation[0] * E.volcanoEruptionStage, rotation[1] * E.volcanoEruptionStage);
   if (isLava) {
    ((PhongMaterial) S.getMaterial()).setSelfIlluminationMap(U.getImage("firelight" + U.random(3)));
   }
   U.setTranslate(S, this);
   S.setVisible(true);
  } else {
   S.setVisible(false);
  }
 }
}
