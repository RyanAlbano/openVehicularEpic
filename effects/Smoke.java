package ve.effects;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import ve.Core;
import ve.VE;
import ve.environment.*;
import ve.utilities.U;
import ve.vehicles.*;

public class Smoke extends Core {

 public final Cylinder C;
 private double speedX, speedY, speedZ,stage;

 public Smoke(VehiclePart VP) {
  C = new Cylinder(1, 1);
  PhongMaterial PM = new PhongMaterial();
  U.setDiffuseRGB(PM, 0, 0, 0, .25);
  U.setSpecularRGB(PM, 0, 0, 0);
  C.setMaterial(PM);
  absoluteRadius = .02 * VP.absoluteRadius;
  U.setScale(C, absoluteRadius);
  //U.add(this);<-Smokes added in Vehicle because the add-order matters
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
   if ((stage += U.random(VE.tick)) > 10) {
    stage = 0;
   } else {
    X += speedX * VE.tick;
    Y += speedY * VE.tick;
    Z += speedZ * VE.tick;
    boolean wind = E.wind > 0;
    X += wind ? E.windX * VE.tick : 0;
    Z += wind ? E.windZ * VE.tick : 0;
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
