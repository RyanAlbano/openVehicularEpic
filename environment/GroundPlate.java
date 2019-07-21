package ve.environment;

import javafx.scene.shape.Cylinder;

import static ve.VE.*;

import ve.utilities.U;

public class GroundPlate extends Cylinder {

 public double X, Z;

 public GroundPlate(double radius, double height) {
  super(radius, height, 6);
 }

 public void run(double radius) {
  clampXZ();
  double y = Math.max(0, -cameraY * .005);
  if (y > cameraY && (!E.poolExists || U.distance(X, E.poolX, Z, E.poolZ) > radius)) {
   U.render(this, X, y, Z, -getRadius());
  } else {
   setVisible(false);
  }
 }

 public void clampXZ() {
  while (Math.abs(X - cameraX) > 25980.762113533159402911695122588) {
   X += X > cameraX ? -51961.524227066318805823390245176 : 51961.524227066318805823390245176;
  }
  while (Math.abs(Z - cameraZ) > 30000) {
   Z += Z > cameraZ ? -60000 : 60000;
  }
 }

 public void checkDuplicate() {
  for (int n = 0; n < E.groundPlates.size(); n++) {
   if (E.groundPlates.get(n) != this && U.distance(X, E.groundPlates.get(n).X, Z, E.groundPlates.get(n).Z) < 2000) {
    U.remove(E.groundPlates.get(n));
    E.groundPlates.remove(n);
    checkDuplicate();
   }
  }
 }
}
