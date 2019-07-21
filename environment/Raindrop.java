package ve.environment;

import javafx.scene.shape.Cylinder;
import static ve.VE.*;
import ve.utilities.U;

public class Raindrop extends Cylinder {

 private double X;
 private double Y;
 private double Z;

 public Raindrop() {
  super(.5, 4, 3);
  setScaleY(10);
  U.add(this);
 }

 public void run() {
  if (E.wind > 0) {
   X += E.windX * tick;
   Z += E.windZ * tick;
  }
  Y += 200 * tick;
  if (Y > 0 || Math.abs(X - cameraX) > E.rainWrapDistance || Math.abs(Y - cameraY) > E.rainWrapDistance || Math.abs(Z - cameraZ) > E.rainWrapDistance) {
   X = cameraX + U.randomPlusMinus(E.rainWrapDistance);
   Y = cameraY + U.randomPlusMinus(E.rainWrapDistance);
   Z = cameraZ + U.randomPlusMinus(E.rainWrapDistance);
  }
  if (Y > E.stormCloudY) {
   U.render(this, X, Y, Z, 200);
  } else {
   setVisible(false);
  }
 }
}
