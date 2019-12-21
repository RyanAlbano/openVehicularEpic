package ve.vehicles;

import javafx.scene.shape.Sphere;
import ve.Core;
import ve.VE;
import ve.environment.E;
import ve.utilities.U;

public class RepairSphere extends Core {

 private final Sphere S;
 public double stage;

 RepairSphere(Vehicle V) {
  S = new Sphere(U.random(V.absoluteRadius * .1));
  U.setMaterialSecurely(S, E.repairSpherePM);
  U.Nodes.add(S);
  S.setVisible(false);
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
    S.setVisible(false);
   } else {
    X = V.X + (speedX * stage);
    Y = V.Y + (speedY * stage);
    Z = V.Z + (speedZ * stage);
    if (U.render(this)) {
     U.setTranslate(S, this);
     S.setVisible(true);
    } else {
     S.setVisible(false);
    }
   }
  }
 }
}
