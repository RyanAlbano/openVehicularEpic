package ve.vehicles;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import ve.Camera;
import ve.Core;
import ve.environment.E;
import ve.utilities.U;

class Skidmark extends Core {

 private final Cylinder C;
 private final double[] defaultRGB = new double[3];
 private boolean deployed;

 Skidmark(Wheel wheel, double[] RGB) {
  defaultRGB[0] = RGB[0];
  defaultRGB[1] = RGB[1];
  defaultRGB[2] = RGB[2];
  PhongMaterial PM = new PhongMaterial();
  U.setSpecularRGB(PM, 0, 0, 0);
  C = new Cylinder(wheel.skidmarkSize * 1.5, wheel.skidmarkSize * .001, 8);
  C.setMaterial(PM);
  U.add(C);
  C.setVisible(false);
 }

 void deploy(Vehicle V, Wheel W, boolean forSnow) {
  X = W.X;
  Z = W.Z;
  Y = Math.min(W.Y, W.minimumY);
  C.setScaleZ(1 + V.netSpeed * .01);
  if (forSnow) {
   U.setDiffuseRGB((PhongMaterial) C.getMaterial(), E.groundRGB[0], E.groundRGB[1], E.groundRGB[2], .5);
  } else {
   U.setDiffuseRGB((PhongMaterial) C.getMaterial(), defaultRGB[0], defaultRGB[1], defaultRGB[2], .5);
  }
  U.rotate(C, W.XY, W.YZ, V.XZ);
  deployed = true;
 }

 public void run() {
  if (deployed && C.getRadius() * E.renderLevel >= U.distance(this) * Camera.zoom && U.getDepth(this) > C.getRadius() * C.getScaleZ()) {
   U.setTranslate(C, this);
   C.setVisible(true);
  } else {
   C.setVisible(false);
  }
 }
}
