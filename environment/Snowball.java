package ve.environment;

import javafx.scene.shape.Sphere;
import static ve.VE.*;
import ve.utilities.U;

public class Snowball {

 public Sphere S;
 private double stage;
 private double X;
 private double Y;
 private double Z;
 private double speedX;
 private double speedY;
 private double speedZ;
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
  speedY += U.randomPlusMinus(6.) + (6 * U.random(tick));
  speedX += U.randomPlusMinus(6.);
  speedZ += U.randomPlusMinus(6.);
  speedX *= .9;
  speedY *= .9;
  speedZ *= .9;
  boolean windy = E.wind > 0;
  X += speedX * tick + (windy ? E.windX * tick : 0);
  Y += speedY * tick;
  Z += speedZ * tick + (windy ? E.windZ * tick : 0);
  while (Math.abs(X - cameraX) > E.snowWrapDistance) {
   X += (X > cameraX ? -E.snowWrapDistance : E.snowWrapDistance) * 2;
   stage = 0;
  }
  while (Math.abs(Y - cameraY) > E.snowWrapDistance) {
   Y += (Y > cameraY ? -E.snowWrapDistance : E.snowWrapDistance) * 2;
   stage = 0;
  }
  while (Math.abs(Z - cameraZ) > E.snowWrapDistance) {
   Z += (Z > cameraZ ? -E.snowWrapDistance : E.snowWrapDistance) * 2;
   stage = 0;
  }
  set(false);
  stage += tick;
  if (U.getDepth(X, Y, Z) > 0) {
   U.setTranslate(S, X, Y, Z);
   if (!quality) {
    U.rotate(S, rotation[0] * stage, rotation[1] * stage);
   }
   S.setVisible(true);
  } else {
   S.setVisible(false);
  }
 }
}
