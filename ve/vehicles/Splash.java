package ve.vehicles;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

import ve.environment.E;
import ve.environment.Pool;
import ve.instances.CoreAdvanced;
import ve.utilities.Nodes;
import ve.utilities.Phong;
import ve.utilities.U;

public class Splash extends CoreAdvanced {
 final Vehicle V;
 private final Cylinder C;
 public static final long defaultQuantity = 384;//<-Too many, maybe?

 Splash(Vehicle vehicle) {
  V = vehicle;
  C = new Cylinder(1, 1);
  U.setMaterialSecurely(C, new PhongMaterial());
  if (Pool.type == Pool.Type.lava) {
   ((PhongMaterial) C.getMaterial()).setSelfIlluminationMap(Phong.getSelfIllumination(E.lavaSelfIllumination[0], E.lavaSelfIllumination[1], E.lavaSelfIllumination[2]));
  }
  Nodes.add(C);
  C.setVisible(false);
 }

 public void deploy(Wheel W, double size, double inSpeedX, double inSpeedY, double inSpeedZ) {
  U.setScale(C, size);
  absoluteRadius = size;
  speedX = inSpeedX;
  speedY = inSpeedY;
  speedZ = inSpeedZ;
  if (W == null) {
   X = V.X;
   Y = V.Y;
   Z = V.Z;
  } else {
   X = W.X;
   Y = W.Y;
   Z = W.Z;
  }
 }

 public void run() {
  if (speedX != 0 || speedY != 0 || speedZ != 0) {
   if (Y > absoluteRadius && speedY > 0) {
    speedX = speedY = speedZ = 0;
    C.setVisible(false);
   } else {
    boolean show = false;
    X += speedX * U.tick;
    Y += speedY * U.tick;
    Z += speedZ * U.tick;
    speedY += E.gravity * U.tick;
    if (U.getDepth(this) > 0) {
     U.randomRotate(C);
     double splashRGB = U.random(2.), r = .5 * splashRGB, g = splashRGB, b = 1;
     if (Pool.type == Pool.Type.lava) {
      r = 1;
      g = 2 * splashRGB;
      b = 0;
     } else if (Pool.type == Pool.Type.acid) {
      r = b = .5 * splashRGB;
      g = 1;
     }
     Phong.setDiffuseRGB((PhongMaterial) C.getMaterial(), r, g, b);
     U.setTranslate(C, this);
     show = true;
    }
    C.setVisible(show);
   }
  }
 }
}
