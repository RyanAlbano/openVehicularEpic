package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import static ve.VE.*;
import ve.utilities.U;

public class TsunamiPart extends Cylinder {

 public double X, Y, Z;

 public TsunamiPart(double radius, double height) {
  super(radius, height);
  setMaterial(new PhongMaterial());
 }

 public void run(boolean update) {
  if (update) {
   X += E.tsunamiSpeedX * tick;
   Z += E.tsunamiSpeedZ * tick;
  }
  if (U.getDepth(X, Y, Z) > -getRadius()) {
   U.setTranslate(this, X, Y, Z);
   U.randomRotate(this);
   double waveRG = U.random(2.);
   U.setDiffuseRGB((PhongMaterial) getMaterial(), waveRG * .5, waveRG, 1);
   setVisible(true);
  } else {
   setVisible(false);
  }
 }
}
