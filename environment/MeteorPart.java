package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

import ve.Core;
import ve.VE;
import ve.utilities.U;

public class MeteorPart extends Core {

 public final Sphere S;
 public final double[] rotation = new double[3];
 boolean onFire;

 MeteorPart(double radius) {
  S = new Sphere(radius, 1);
  S.setMaterial(new PhongMaterial());
  U.add(S);
 }

 public void run() {
  XZ += rotation[0] * VE.tick;
  YZ += rotation[1] * VE.tick;
  if (U.render(this, -S.getRadius())) {
   if (onFire) {
    U.setDiffuseRGB((PhongMaterial) S.getMaterial(), 0, 0, 0);
    U.setSpecularRGB((PhongMaterial) S.getMaterial(), 0, 0, 0);
    ((PhongMaterial) S.getMaterial()).setDiffuseMap(null);
    ((PhongMaterial) S.getMaterial()).setSelfIlluminationMap(U.getImage("firelight" + U.random(3)));
   } else {
    U.setDiffuseRGB((PhongMaterial) S.getMaterial(), 1, 1, 1);
    U.setSpecularRGB((PhongMaterial) S.getMaterial(), 1, 1, 1);
    ((PhongMaterial) S.getMaterial()).setDiffuseMap(U.getImage("rock"));
    ((PhongMaterial) S.getMaterial()).setSelfIlluminationMap(null);
   }
   U.rotate(S, YZ, XZ);
   U.setTranslate(S, this);
   S.setVisible(true);
  } else {
   S.setVisible(false);
  }
 }
}
