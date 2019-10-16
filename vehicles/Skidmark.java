package ve.vehicles;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import ve.Camera;
import ve.Core;
import ve.environment.E;
import ve.utilities.U;

class Skidmark extends Core {

 final Cylinder C;
 boolean deployed;

 Skidmark(Wheel wheel, PhongMaterial PM) {
  C = new Cylinder(wheel.skidmarkSize * 1.5, wheel.skidmarkSize * .001, 8);
  C.setMaterial(PM);
  U.add(C);
  C.setVisible(false);
 }

 public void run() {
  if (deployed && C.getRadius() * E.renderLevel >= U.distance(this) * Camera.zoom && U.getDepth(this) > C.getRadius() * C.getScaleZ()) {
   U.setTranslate(C, this);
   C.setVisible(true);
  } else {
   C.setVisible(false);
  }
 }
}
