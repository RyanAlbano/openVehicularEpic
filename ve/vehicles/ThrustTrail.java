package ve.vehicles;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import ve.effects.Effects;
import ve.instances.CoreAdvanced;
import ve.utilities.Images;
import ve.utilities.Phong;
import ve.utilities.U;

class ThrustTrail extends CoreAdvanced {
 private final VehiclePart VP;
 final Box B;
 private double stage;
 static final long defaultQuantity = 48;

 ThrustTrail(VehiclePart part) {
  VP = part;
  B = new Box(1, 1, 1);
  PhongMaterial PM = new PhongMaterial();
  if (VP.thrust == VehiclePart.Thrust.blue) {
   Phong.setDiffuseRGB(PM, 0);
   Phong.setSpecularRGB(PM, 0);
  } else {
   Phong.setSpecularRGB(PM, 1);
   PM.setSelfIlluminationMap(VP.thrust == VehiclePart.Thrust.fire ? Images.fireLight.get(2) : Images.white);//<-Blue thrust selfIllumination set in real-time
  }
  U.setMaterialSecurely(B, PM);
  B.setVisible(false);
 }

 void deploy(boolean isThrusted, double V_sinXZ, double V_cosXZ, double V_sinYZ) {
  X = VP.X + U.randomPlusMinus(absoluteRadius);
  Y = VP.Y + U.randomPlusMinus(absoluteRadius);
  Z = VP.Z + U.randomPlusMinus(absoluteRadius);
  absoluteRadius = VP.absoluteRadius * (isThrusted ? .16 : .04) * U.random();
  U.setScale(B, absoluteRadius);
  speedX = U.randomPlusMinus(absoluteRadius * .5);
  speedY = U.randomPlusMinus(absoluteRadius * .5);
  speedZ = U.randomPlusMinus(absoluteRadius * .5);
  if (isThrusted) {
   double amount = Math.min(VP.V.topSpeeds[1], 300);
   speedX += amount * V_sinXZ * VP.V.P.polarity;
   speedZ -= amount * V_cosXZ * VP.V.P.polarity;
   speedY += amount * V_sinYZ;
  }
  stage = Double.MIN_VALUE;
 }

 public void run() {
  if (stage > 0) {
   if ((stage += U.tick) > 10) {
    stage = 0;
    B.setVisible(false);
   } else {
    X += speedX * U.tick;
    Y += speedY * U.tick;
    Z += speedZ * U.tick;
    if (U.render(this, /*ThrustTrails should ALWAYS be clearly visible!->*/false, true)) {
     U.setTranslate(B, this);
     U.randomRotate(B);
     if (VP.thrust == VehiclePart.Thrust.blue) {
      ((PhongMaterial) B.getMaterial()).setSelfIlluminationMap(Effects.blueJet());
     } else {
      Phong.setDiffuseRGB((PhongMaterial) B.getMaterial(), 1 - (.05 * stage / Math.sqrt(U.tick)), 1 - (.1 * stage / Math.sqrt(U.tick)), 0);
     }
     B.setVisible(true);
    } else {
     B.setVisible(false);
    }
   }
  }
 }
}
