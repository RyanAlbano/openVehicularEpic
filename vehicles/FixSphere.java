package ve.vehicles;

import javafx.scene.shape.Sphere;
import ve.VE;
import ve.utilities.U;

class FixSphere extends Sphere {

 double stage;
 private double speedX;
 private double speedY;
 private double speedZ;

 FixSphere(Vehicle V) {
  setRadius(U.random(V.absoluteRadius * .1));
  setMaterial(V.fixSpherePM);
  U.add(this);
  setVisible(false);
 }

 void deploy() {
  speedX = U.randomPlusMinus(400);
  speedY = U.randomPlusMinus(400);
  speedZ = U.randomPlusMinus(400);
  stage = Double.MIN_VALUE;
 }

 void run(Vehicle V) {
  if (stage > 0) {
   if ((stage += U.random(VE.tick)) > 20) {
    stage = 0;
    setVisible(false);
   } else {
    U.render(this, V.X + (speedX * stage), V.Y + (speedY * stage), V.Z + (speedZ * stage));
   }
  }
 }
}
