package ve.vehicles;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

import ve.Core;
import ve.VE;
import ve.environment.E;
import ve.utilities.U;

class Splash extends Core {

 private final Cylinder C;
 private double speedX, speedY, speedZ;

 Splash() {
  C = new Cylinder(1, 1);
  C.setMaterial(new PhongMaterial());
  if (E.poolType == E.Pool.lava) {
   U.setSelfIllumination((PhongMaterial) C.getMaterial(), E.lavaSelfIllumination[0], E.lavaSelfIllumination[1], E.lavaSelfIllumination[2]);
  }
  U.add(C);
  C.setVisible(false);
 }

 public void deploy(Wheel W, double size, double speedX, double speedY, double speedZ) {
  U.setScale(C, size);
  absoluteRadius = size;
  this.speedX = speedX;
  this.speedY = speedY;
  this.speedZ = speedZ;
  X = W.X;
  Y = W.Y;
  Z = W.Z;
 }

 public void run() {
  if (speedX != 0 || speedY != 0 || speedZ != 0) {
   if (Y > absoluteRadius && speedY > 0) {
    speedX = speedY = speedZ = 0;
    C.setVisible(false);
   } else {
    boolean show = false;
    X += speedX * VE.tick;
    Y += speedY * VE.tick;
    Z += speedZ * VE.tick;
    speedY += E.gravity * VE.tick;
    if (U.getDepth(this) > 0) {
     U.randomRotate(C);
     double splashRGB = U.random(2.), r = .5 * splashRGB, g = splashRGB, b = 1;
     if (E.poolType == E.Pool.lava) {
      r = 1;
      g = 2 * splashRGB;
      b = 0;
     } else if (E.poolType == E.Pool.acid) {
      r = b = .5 * splashRGB;
      g = 1;
     }
     U.setDiffuseRGB((PhongMaterial) C.getMaterial(), r, g, b);
     U.setTranslate(C, this);
     show = true;
    }
    C.setVisible(show);
   }
  }
 }
}
