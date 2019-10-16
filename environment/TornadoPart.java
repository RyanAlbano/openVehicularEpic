package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import ve.Core;
import ve.utilities.U;

public class TornadoPart extends Core {

 public final Cylinder C;
 Cylinder groundDust;

 TornadoPart(double radius, double height) {
  C = new Cylinder(radius, height);
 }

 public void run() {
  double depth = U.getDepth(this), radius0 = E.tornadoParts.get(0).C.getRadius(),
  groundDustY = -U.random(radius0),
  dustLocation = (radius0 + groundDustY) * 7.5,
  groundDustX = E.tornadoParts.get(0).X + U.randomPlusMinus(dustLocation),
  groundDustZ = E.tornadoParts.get(0).Z + U.randomPlusMinus(dustLocation);
  if (depth > -C.getRadius()) {
   U.randomRotate(C);
   C.setCullFace(depth > C.getRadius() ? CullFace.BACK : CullFace.NONE);
   double color = 1 - U.random(.75);
   U.setDiffuseRGB((PhongMaterial) C.getMaterial(), color, color, color);
   U.setTranslate(C, this);
   C.setVisible(true);
  } else {
   C.setVisible(false);
  }
  if (U.render(groundDustX, groundDustY, groundDustZ)) {
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
