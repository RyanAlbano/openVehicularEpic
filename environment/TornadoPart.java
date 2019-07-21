package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import ve.utilities.U;

public class TornadoPart extends Cylinder {

 public double X;
 public double Y;
 public double Z;
 public Cylinder groundDust;

 public TornadoPart(double radius, double height) {
  super(radius, height);
 }

 public void run() {
  double depth = U.getDepth(X, Y, Z), radius0 = E.tornadoParts.get(0).getRadius();
  double groundDustY = -U.random(radius0);
  double dustLocation = (radius0 + groundDustY) * 7.5;
  double groundDustX = E.tornadoParts.get(0).X + U.randomPlusMinus(dustLocation);
  double groundDustZ = E.tornadoParts.get(0).Z + U.randomPlusMinus(dustLocation);
  if (depth > -getRadius()) {
   U.randomRotate(this);
   setCullFace(depth > getRadius() ? CullFace.BACK : CullFace.NONE);
   double C = 1 - U.random(.75);
   U.setDiffuseRGB((PhongMaterial) getMaterial(), C, C, C);
   U.setTranslate(this, X, Y, Z);
   setVisible(true);
  } else {
   setVisible(false);
  }
  if (U.getDepth(groundDustX, groundDustY, groundDustZ) > 0) {
   U.randomRotate(groundDust);
   PhongMaterial PM = new PhongMaterial();
   double c = 1 - U.random(.5);
   U.setDiffuseRGB(PM, c, c, c);
   groundDust.setMaterial(PM);
   U.setTranslate(groundDust, groundDustX, groundDustY, groundDustZ);
   groundDust.setVisible(true);
  } else {
   groundDust.setVisible(false);
  }
 }
}
