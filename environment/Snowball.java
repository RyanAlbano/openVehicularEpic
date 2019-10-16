package ve.environment;

import javafx.scene.shape.Sphere;

import ve.Camera;
import ve.Core;
import ve.VE;
import ve.utilities.U;

public class Snowball extends Core {

 public Sphere S;
 private double stage;
 private double speedX, speedY, speedZ;
 private final double radius = 1 + U.random(9.);
 private final double[] rotation = new double[2];
 private boolean quality = true;

 public Snowball() {
  X = U.randomPlusMinus(1000000.);
  Y = U.randomPlusMinus(1000000.);
  Z = U.randomPlusMinus(1000000.);
  rotation[0] = U.randomPlusMinus(45.);
  rotation[1] = U.randomPlusMinus(45.);
  set(true);
 }

 private void set(boolean firstLoad) {
  if (U.averageFPS < 30 && (quality || firstLoad)) {
   U.remove(S);
   S = new Sphere(radius, 0);
   U.add(S);
   quality = false;
  } else if ((U.averageFPS >= 60 && !quality) || firstLoad) {
   U.remove(S);
   S = new Sphere(radius);
   U.add(S);
   quality = true;
  }
 }

 public void run() {
  speedY += U.randomPlusMinus(6.) + (6 * U.random(VE.tick));
  speedX += U.randomPlusMinus(6.);
  speedZ += U.randomPlusMinus(6.);
  speedX *= .9;
  speedY *= .9;
  speedZ *= .9;
  boolean windy = E.wind > 0;
  X += speedX * VE.tick + (windy ? E.windX * VE.tick : 0);
  Y += speedY * VE.tick;
  Z += speedZ * VE.tick + (windy ? E.windZ * VE.tick : 0);
  while (Math.abs(X - Camera.X) > E.snowWrapDistance) {
   X += (X > Camera.X ? -E.snowWrapDistance : E.snowWrapDistance) * 2;
   stage = 0;
  }
  while (Math.abs(Y - Camera.Y) > E.snowWrapDistance) {
   Y += (Y > Camera.Y ? -E.snowWrapDistance : E.snowWrapDistance) * 2;
   stage = 0;
  }
  while (Math.abs(Z - Camera.Z) > E.snowWrapDistance) {
   Z += (Z > Camera.Z ? -E.snowWrapDistance : E.snowWrapDistance) * 2;
   stage = 0;
  }
  set(false);
  stage += VE.tick;
  if (U.getDepth(this) > 0) {
   U.setTranslate(S, this);
   if (!quality) {
    U.rotate(S, rotation[0] * stage, rotation[1] * stage);
   }
   S.setVisible(true);
  } else {
   S.setVisible(false);
  }
 }
}
