package ve.vehicles;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import ve.VE;
import ve.utilities.U;

class Jet extends Box {

 private double X, Y, Z, stage;

 Jet() {
  PhongMaterial PM = new PhongMaterial();
  U.setSpecularRGB(PM, 1, 1, 1);
  setMaterial(PM);
  U.add(this);
  setVisible(false);
 }

 void deploy(Vehicle vehicle) {
  double emitPoint = vehicle.absoluteRadius * .1125 + 400;
  X = vehicle.X + (emitPoint * U.sin(vehicle.XZ) * (U.cos(vehicle.YZ))) + U.randomPlusMinus(emitPoint * .25);
  Z = vehicle.Z - (emitPoint * U.cos(vehicle.XZ) * (U.cos(vehicle.YZ))) + U.randomPlusMinus(emitPoint * .25);
  Y = vehicle.Y + (emitPoint * U.sin(vehicle.YZ)) + U.randomPlusMinus(emitPoint * .25);
  U.setScale(this, U.random(vehicle.absoluteRadius * .04));
  stage = Double.MIN_VALUE;
 }

 public void run(boolean gamePlay) {
  if (stage > 0) {
   if ((stage += gamePlay ? VE.tick : 0) > 10) {
    stage = 0;
    setVisible(false);
   } else {
    if (U.getDepth(X, Y, Z) > 0) {
     U.setTranslate(this, X, Y, Z);
     U.randomRotate(this);
     U.setDiffuseRGB((PhongMaterial) getMaterial(), 1 - (.05 * stage / Math.sqrt(VE.tick)), 1 - (.1 * stage / Math.sqrt(VE.tick)), 0);
     setVisible(true);
    } else {
     setVisible(false);
    }
   }
  }
 }
}
