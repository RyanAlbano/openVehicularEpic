package ve.effects;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import ve.environment.*;
import ve.instances.CoreAdvanced;
import ve.ui.UI;
import ve.utilities.U;
import ve.vehicles.*;

public class Smoke extends CoreAdvanced {

 public final Cylinder C;
 private double stage;
 public static final long defaultQuantity = 50;

 public Smoke(VehiclePart VP) {
  C = new Cylinder(1, 1);
  PhongMaterial PM = new PhongMaterial();
  U.Phong.setDiffuseRGB(PM, 0, 0, 0, .25);
  U.Phong.setSpecularRGB(PM, 0);
  U.setMaterialSecurely(C, PM);
  absoluteRadius = .02 * VP.absoluteRadius;
  U.setScale(C, absoluteRadius);
  //U.add(this);<-Not added here because the add-order matters
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
   if ((stage += U.random(UI.tick)) > 10) {
    stage = 0;
   } else {
    X += speedX * UI.tick;
    Y += speedY * UI.tick;
    Z += speedZ * UI.tick;
    X += Wind.speedX * UI.tick;
    Z += Wind.speedZ * UI.tick;
    if (U.renderWithLOD(this)) {
     U.randomRotate(C);
     U.setTranslate(C, this);
     show = true;
    }
   }
   C.setVisible(show);
  }
 }
}
