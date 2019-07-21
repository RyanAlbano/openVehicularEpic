package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import static ve.VE.*;
import ve.utilities.U;

public class MeteorPart extends Sphere {

 public double X;
 public double Y;
 public double Z;
 private double XZ;
 private double YZ;
 public final double[] rotation = new double[3];
 public boolean onFire;

 public MeteorPart(double radius, int divisions) {
  super(radius, divisions);
  setMaterial(new PhongMaterial());
  U.add(this);
 }

 public void run() {
  XZ += rotation[0] * tick;
  YZ += rotation[1] * tick;
  if (U.getDepth(X, Y, Z) > -getRadius()) {
   if (onFire) {
    U.setDiffuseRGB((PhongMaterial) getMaterial(), 0, 0, 0);
    U.setSpecularRGB((PhongMaterial) getMaterial(), 0, 0, 0);
    ((PhongMaterial) getMaterial()).setDiffuseMap(null);
    ((PhongMaterial) getMaterial()).setSelfIlluminationMap(U.getImage("firelight" + U.random(3)));
   } else {
    U.setDiffuseRGB((PhongMaterial) getMaterial(), 1, 1, 1);
    U.setSpecularRGB((PhongMaterial) getMaterial(), 1, 1, 1);
    ((PhongMaterial) getMaterial()).setDiffuseMap(U.getImage("rock"));
    ((PhongMaterial) getMaterial()).setSelfIlluminationMap(null);
   }
   U.rotate(this, YZ, XZ);
   U.setTranslate(this, X, Y, Z);
   setVisible(true);
  } else {
   setVisible(false);
  }
 }
}
