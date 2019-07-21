package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.utilities.U;

public class VolcanoRock extends Sphere {

 public double X;
    public double Y;
    public double Z;
    public double speedX;
    public double speedY;
    public double speedZ;
    private final double[] rotation = new double[3];
 public boolean groundHit, isLava;

 public VolcanoRock(double radius, int divisions) {
  super(radius, divisions);
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
  if (U.getDepth(X, Y, Z) > -getRadius()) {
   U.rotate(this, rotation[0] * E.volcanoEruptionStage, rotation[1] * E.volcanoEruptionStage);
   if (isLava) {
    ((PhongMaterial) getMaterial()).setSelfIlluminationMap(U.getImage("firelight" + U.random(3)));
   }
   U.setTranslate(this, X, Y, Z);
   setVisible(true);
  } else {
   setVisible(false);
  }
 }
}