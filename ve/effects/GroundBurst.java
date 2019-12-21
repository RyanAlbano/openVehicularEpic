package ve.effects;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.Core;
import ve.VE;
import ve.environment.*;
import ve.utilities.U;

public class GroundBurst extends Core {

 private final Sphere S;
 private double brightness;

 public GroundBurst() {
  S = new Sphere(1, 0);
  PhongMaterial PM = new PhongMaterial();
  brightness = 1;
  U.Phong.setDiffuseRGB(PM, E.Ground.RGB);
  U.Phong.setSpecularRGB(PM, 1);
  PM.setSpecularPower(0);
  U.setMaterialSecurely(S, PM);
  U.Nodes.add(S);
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
   if ((brightness -= U.random(VE.tick * .1)) < 0) {
    S.setVisible(false);
   } else {
    boolean show = false;
    X += speedX * VE.tick;
    Y += speedY * VE.tick;
    Z += speedZ * VE.tick;
    //X += E.Wind.speedX * VE.tick;*<-Applying wind here appears to detract from the overall effect
    //Z += E.Wind.speedZ * VE.tick;*
    if (U.render(this)) {//<-Bright and not common, so ignoring LOD is better
     U.Phong.setSpecularRGB((PhongMaterial) S.getMaterial(), brightness);
     U.setTranslate(S, this);
     show = true;
    }
    S.setVisible(show);
   }
  }
 }
}
