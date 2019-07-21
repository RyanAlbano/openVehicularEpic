package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

import static ve.VE.*;

import ve.utilities.U;
import ve.vehicles.Wheel;

public class Splash extends Cylinder {

 private double X;
 private double Y;
 private double Z;
 private double size;
 private double speedX;
 private double speedY;
 private double speedZ;

 public Splash() {
  super(1, 1);
  setMaterial(new PhongMaterial());
  U.add(this);
  setVisible(false);
 }

 public void deploy(Wheel W, double size, double speedX, double speedY, double speedZ) {
  U.setScale(this,size);
  this.size = size;
  this.speedX = speedX;
  this.speedY = speedY;
  this.speedZ = speedZ;
  X = W.X;
  Y = W.Y;
  Z = W.Z;
 }

 public void run() {
  if (speedX != 0 || speedY != 0 || speedZ != 0) {
   if (Y > size && speedY > 0) {
    speedX = speedY = speedZ = 0;
    setVisible(false);
   } else {
    boolean show = false;
    X += speedX * tick;
    Y += speedY * tick;
    Z += speedZ * tick;
    speedY += E.gravity * tick;
    if (U.getDepth(X, Y, Z) > 0) {
     U.randomRotate(this);
     double splashRGB = U.random(2.), r = .5 * splashRGB, g = splashRGB, b = 1;
     if (poolType.equals("lava")) {
      r = 1;
      g = 2 * splashRGB;
      b = 0;
     } else if (poolType.equals("acid")) {
      r = b = .5 * splashRGB;
      g = 1;
     }
     U.setDiffuseRGB((PhongMaterial) getMaterial(), r, g, b);
     U.setTranslate(this, X, Y, Z);
     show = true;
    }
    setVisible(show);
   }
  }
 }
}
