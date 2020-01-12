package ve.effects;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.environment.*;
import ve.instances.CoreAdvanced;
import ve.utilities.Nodes;
import ve.utilities.Phong;
import ve.utilities.U;

public class GroundBurst extends CoreAdvanced {

 private final Sphere S;
 private double brightness;

 public GroundBurst() {
  S = new Sphere(1, 0);
  PhongMaterial PM = new PhongMaterial();
  brightness = 1;
  Phong.setDiffuseRGB(PM, Ground.RGB);
  Phong.setSpecularRGB(PM, 1);
  PM.setSpecularPower(0);
  U.setMaterialSecurely(S, PM);
  Nodes.add(S);
  S.setVisible(false);
 }

 public void deploy(double x, double z) {
  X = x;
  Y = 0;
  Z = z;
  speedX = U.randomPlusMinus(200.);
  speedY = -U.random(200.);
  speedZ = U.randomPlusMinus(200.);
  absoluteRadius = U.random(200.);
  U.setScale(S, absoluteRadius);
  brightness = 1;
  U.randomRotate(S);
 }

 public void run() {
  if (brightness >= 0) {
   if ((brightness -= U.random(U.tick * .1)) < 0) {
    S.setVisible(false);
   } else {
    boolean show = false;
    X += speedX * U.tick;
    Y += speedY * U.tick;
    Z += speedZ * U.tick;
    //X += E.Wind.speedX * VE.tick;*<-Applying wind here appears to detract from the overall effect
    //Z += E.Wind.speedZ * VE.tick;*
    if (U.render(this, false, false)) {//<-Bright and not common, so ignoring LOD is better
     Phong.setSpecularRGB((PhongMaterial) S.getMaterial(), brightness);
     U.setTranslate(S, this);
     show = true;
    }
    S.setVisible(show);
   }
  }
 }
}
