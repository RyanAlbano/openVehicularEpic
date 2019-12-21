package ve.vehicles;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import ve.Core;
import ve.VE;
import ve.utilities.SL;
import ve.utilities.U;

class ThrustTrail extends Core {

 final Box B;
 private double stage;
 static final long defaultQuantity = 48;

 ThrustTrail(VehiclePart VP) {
  B = new Box(1, 1, 1);
  PhongMaterial PM = new PhongMaterial();
  if (VP.thrust == VehiclePart.Thrust.blue) {
   U.Phong.setDiffuseRGB(PM, 0);
   U.Phong.setSpecularRGB(PM, 0);
  } else {
   U.Phong.setSpecularRGB(PM, 1);
   PM.setSelfIlluminationMap(U.Images.get(VP.thrust == VehiclePart.Thrust.fire ? "firelight2" : SL.white));//<-Blue thrust selfIllumination set in real-time
  }
  U.setMaterialSecurely(B, PM);
  B.setVisible(false);
 }

 void deploy(Vehicle V, VehiclePart VP, boolean isThrusted) {
  X = VP.X + U.randomPlusMinus(absoluteRadius);
  Y = VP.Y + U.randomPlusMinus(absoluteRadius);
  Z = VP.Z + U.randomPlusMinus(absoluteRadius);
  absoluteRadius = VP.absoluteRadius * (isThrusted ? .16 : .04) * U.random();
  U.setScale(B, absoluteRadius);
  speedX = U.randomPlusMinus(absoluteRadius * .5);
  speedY = U.randomPlusMinus(absoluteRadius * .5);
  speedZ = U.randomPlusMinus(absoluteRadius * .5);
  if (isThrusted) {
   double amount = Math.min(V.topSpeeds[1], 300);
   speedX += amount * U.sin(V.XZ) * V.P.polarity;
   speedZ -= amount * U.cos(V.XZ) * V.P.polarity;
   speedY += amount * U.sin(V.YZ);
  }
  stage = Double.MIN_VALUE;
 }

 public void run(VehiclePart VP) {
  if (stage > 0) {
   if ((stage += VE.tick) > 10) {
    stage = 0;
    B.setVisible(false);
   } else {
    X += speedX * VE.tick;
    Y += speedY * VE.tick;
    Z += speedZ * VE.tick;
    if (U.renderWithLOD(this)) {
     U.setTranslate(B, this);
     U.randomRotate(B);
     if (VP.thrust == VehiclePart.Thrust.blue) {
      ((PhongMaterial) B.getMaterial()).setSelfIlluminationMap(U.Images.get(SL.blueJet + U.random(3)));
     } else {
      U.Phong.setDiffuseRGB((PhongMaterial) B.getMaterial(), 1 - (.05 * stage / Math.sqrt(VE.tick)), 1 - (.1 * stage / Math.sqrt(VE.tick)), 0);
     }
     B.setVisible(true);
    } else {
     B.setVisible(false);
    }
   }
  }
 }
}
