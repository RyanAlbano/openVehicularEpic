package ve.effects;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import ve.environment.*;
import ve.instances.CoreAdvanced;
import ve.utilities.Phong;
import ve.utilities.U;
import ve.vehicles.*;

public class Smoke extends CoreAdvanced {

 public final Cylinder C;
 protected double stage;
 public static final long defaultQuantity = 50;

 public Smoke(double size) {
  C = new Cylinder(1, 1);
  PhongMaterial PM = new PhongMaterial();
  Phong.setDiffuseRGB(PM, 0, 0, 0, .25);
  Phong.setSpecularRGB(PM, 0);
  U.setMaterialSecurely(C, PM);
  absoluteRadius = size;
  U.setScale(C, absoluteRadius);
  //(Added with transparent Nodes)
 }

 public void deploy(VehiclePart VP) {
  X = VP.X;
  Y = VP.Y;
  Z = VP.Z;
  speedX = U.randomPlusMinus(absoluteRadius * .25);
  speedY = U.randomPlusMinus(absoluteRadius * .25);
  speedZ = U.randomPlusMinus(absoluteRadius * .25);
  stage = Double.MIN_VALUE;
 }

 public void run() {
  if (stage > 0) {
   boolean show = false;
   if ((stage += U.random(U.tick)) > 10) {
    stage = 0;
   } else {
    X += speedX * U.tick;
    Y += speedY * U.tick;
    Z += speedZ * U.tick;
    X += Wind.speedX * U.tick;
    Z += Wind.speedZ * U.tick;
    if (U.render(this, true, true)) {
     U.randomRotate(C);
     U.setTranslate(C, this);
     show = true;
    }
   }
   C.setVisible(show);
  }
 }
}
