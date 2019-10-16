package ve.environment;

import javafx.scene.shape.Cylinder;

import ve.Camera;
import ve.Core;
import ve.VE;
import ve.utilities.U;

public class Raindrop extends Core {

 public final Cylinder C;

 Raindrop() {
  C = new Cylinder(.5, 4, 3);
  C.setScaleY(10);
  U.add(C);
 }

 public void run() {
  if (E.wind > 0) {
   X += E.windX * VE.tick;
   Z += E.windZ * VE.tick;
  }
  Y += 200 * VE.tick;
  if (Y > 0 || Math.abs(X - Camera.X) > E.rainWrapDistance || Math.abs(Y - Camera.Y) > E.rainWrapDistance || Math.abs(Z - Camera.Z) > E.rainWrapDistance) {
   X = Camera.X + U.randomPlusMinus(E.rainWrapDistance);
   Y = Camera.Y + U.randomPlusMinus(E.rainWrapDistance);
   Z = Camera.Z + U.randomPlusMinus(E.rainWrapDistance);
  }
  if (Y > E.stormCloudY && U.render(this, 200)) {
   U.setTranslate(C, this);
   C.setVisible(true);
  } else {
   C.setVisible(false);
  }
 }
}
