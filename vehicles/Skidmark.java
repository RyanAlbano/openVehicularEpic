package ve.vehicles;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import ve.VE;
import ve.utilities.U;

class Skidmark extends Cylinder {//<-is stored

 double X, Y, Z;
 boolean deployed;

 Skidmark(Wheel wheel, PhongMaterial PM) {
  super(wheel.skidmarkSize * 1.5, wheel.skidmarkSize * .001, 8);
  setMaterial(PM);
  U.add(this);
  setVisible(false);
 }

 public void run() {
  if (deployed && getRadius() * VE.renderLevel >= U.distance(X, VE.cameraX, Y, VE.cameraY, Z, VE.cameraZ) * VE.zoom && U.getDepth(X, Y, Z) > getRadius() * getScaleZ()) {
   U.setTranslate(this, X, Y, Z);
   setVisible(true);
  } else {
   setVisible(false);
  }
 }
}
