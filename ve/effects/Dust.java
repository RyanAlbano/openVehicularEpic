package ve.effects;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import ve.environment.*;
import ve.instances.CoreAdvanced;
import ve.ui.UI;
import ve.utilities.SL;
import ve.utilities.U;
import ve.vehicles.*;

public class Dust extends CoreAdvanced {

 private final Cylinder C;
 private double stage;
 private double duration;
 public static final long defaultQuantity = 96;

 public Dust() {
  C = new Cylinder(1, 1);
  PhongMaterial PM = new PhongMaterial();
  U.Phong.setSpecularRGB(PM, 0);
  U.setMaterialSecurely(C, PM);
  U.Phong.setDiffuseRGB(PM, Ground.RGB, .5);
  U.Nodes.add(C);//<-Leave here--creation times are determined in respective classes
 }

 public void deploy(Vehicle vehicle, double dustSpeed, double speedDifference) {
  X = vehicle.X + (dustSpeed > 0 ? U.randomPlusMinus(vehicle.absoluteRadius * .3) : 0);
  Z = vehicle.Z + (dustSpeed > 0 ? U.randomPlusMinus(vehicle.absoluteRadius * .3) : 0);
  Y = Math.min(vehicle.Y + vehicle.clearanceY, vehicle.P.localGround);//<-So that dust won't deploy underground during a hard ground hit
  speedX = speedZ = vehicle.P.flipped() ? 0 : speedDifference;
  double presence = vehicle.P.terrainProperties.contains(SL.Thick(SL.hard)) ? 1 : 2;
  absoluteRadius = .05 * vehicle.absoluteRadius * Math.sqrt(U.random()) * presence;
  duration = 2 * presence;
  U.setScale(C, absoluteRadius);
  U.Phong.setDiffuseRGB((PhongMaterial) C.getMaterial(), vehicle.terrainRGB, .5);
  stage = Double.MIN_VALUE;
 }

 public void deploy(Boulder.Instance boulder) {
  X = boulder.X + U.randomPlusMinus(boulder.S.getRadius() * 1.5);
  Z = boulder.Z + U.randomPlusMinus(boulder.S.getRadius() * 1.5);
  absoluteRadius = .5 * boulder.S.getRadius() * Math.sqrt(U.random());
  duration = 4 + U.random(4.);
  U.setScale(C, absoluteRadius);
  stage = Double.MIN_VALUE;
 }

 public void run() {
  if (stage > 0) {
   boolean show = false;
   if ((stage += U.random(UI.tick)) > duration) {
    stage = 0;
   } else {
    X += U.randomPlusMinus(speedX) + (Wind.speedX * UI.tick);
    Z += U.randomPlusMinus(speedZ) + (Wind.speedZ * UI.tick);
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
