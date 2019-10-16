package ve.environment;

import javafx.scene.shape.Cylinder;

import ve.Camera;
import ve.utilities.U;

class GroundPlate extends Cylinder {

 public double X, Z;

 GroundPlate(double radius) {
  super(radius, 0, 6);
 }

 public void run(double radius) {
  clampXZ();
  double y = Math.max(0, -Camera.Y * .005);
  if (y > Camera.Y && (!E.poolExists || U.distance(X, E.poolX, Z, E.poolZ) > radius) && U.render(X, y, Z, -getRadius())) {
   U.setTranslate(this, X, y, Z);
   setVisible(true);
  } else {
   setVisible(false);
  }
 }

 void clampXZ() {
  while (Math.abs(X - Camera.X) > 25980.762113533159402911695122588) {
   X += X > Camera.X ? -51961.524227066318805823390245176 : 51961.524227066318805823390245176;
  }
  while (Math.abs(Z - Camera.Z) > 30000) {
   Z += Z > Camera.Z ? -60000 : 60000;
  }
 }

 void checkDuplicate() {
  for (int n = 0; n < E.groundPlates.size(); n++) {
   if (E.groundPlates.get(n) != this && U.distance(X, E.groundPlates.get(n).X, Z, E.groundPlates.get(n).Z) < 2000) {
    U.remove(E.groundPlates.get(n));
    E.groundPlates.remove(n);
    checkDuplicate();
   }
  }
 }
}
