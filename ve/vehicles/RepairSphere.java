package ve.vehicles;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.instances.CoreAdvanced;
import ve.utilities.Nodes;
import ve.utilities.Phong;
import ve.utilities.U;

public class RepairSphere extends CoreAdvanced {

 private static final PhongMaterial repairSpherePM = new PhongMaterial();
 private final Sphere S;
 public double stage;

 static {
  Phong.setDiffuseRGB(repairSpherePM, 1, 1, 1, .25);
 }

 RepairSphere(Vehicle V) {
  absoluteRadius = U.random(V.absoluteRadius * .1);
  S = new Sphere(absoluteRadius);
  U.setMaterialSecurely(S, repairSpherePM);
  Nodes.add(S);
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
   if ((stage += U.random(U.tick)) > 20) {
    stage = 0;
    S.setVisible(false);
   } else {
    X = V.X + (speedX * stage);
    Y = V.Y + (speedY * stage);
    Z = V.Z + (speedZ * stage);
    if (U.render(this, true, false)) {
     U.setTranslate(S, this);
     S.setVisible(true);
    } else {
     S.setVisible(false);
    }
   }
  }
 }
}
