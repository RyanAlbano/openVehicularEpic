package ve.vehicles;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.utilities.U;

class NukeBlast extends Sphere {

 double X, Y, Z, XZ, YZ;

 NukeBlast(PhongMaterial PM) {
  super(10000, 1);
  setMaterial(PM);
  U.randomRotate(this);
  U.add(this);
  setVisible(false);
 }
}
