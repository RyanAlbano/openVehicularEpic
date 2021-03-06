package ve.vehicles;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import ve.environment.E;
import ve.environment.Ground;
import ve.instances.Core;
import ve.utilities.Camera;
import ve.utilities.Nodes;
import ve.utilities.Phong;
import ve.utilities.U;

class Skidmark extends Core {

 private final Cylinder C;
 private final Color defaultRGB;
 private boolean deployed;

 Skidmark(Wheel wheel, Color RGB) {
  defaultRGB = RGB;
  PhongMaterial PM = new PhongMaterial();
  Phong.setSpecularRGB(PM, 0);
  C = new Cylinder(wheel.skidmarkSize * 1.5, wheel.skidmarkSize * .001, 8);
  U.setMaterialSecurely(C, PM);
  Nodes.add(C);
  C.setVisible(false);
 }

 void deploy(Vehicle V, Wheel W, boolean forSnow) {
  X = W.X;
  Z = W.Z;
  Y = Math.min(W.Y, W.minimumSkidmarkY);
  C.setScaleZ(1 + V.P.netSpeed * .01);
  if (forSnow) {
   Phong.setDiffuseRGB((PhongMaterial) C.getMaterial(), Ground.RGB, .5);
  } else {
   Phong.setDiffuseRGB((PhongMaterial) C.getMaterial(), defaultRGB, .5);
  }
  U.rotate(C, W.XY, W.YZ, V.XZ);
  deployed = true;
 }

 public void run() {
  if (deployed && C.getRadius() * E.renderLevel >= U.distance(this) * Camera.FOV && U.getDepth(this) > C.getRadius() * C.getScaleZ()) {
   U.setTranslate(C, this);
   C.setVisible(true);
  } else {
   C.setVisible(false);
  }
 }
}
