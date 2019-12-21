package ve.vehicles;

import javafx.scene.paint.*;
import javafx.scene.shape.*;
import ve.utilities.*;

class Flame extends MeshView {

 private final VehiclePart VP;

 Flame(VehiclePart vp) {
  VP = vp;
  TriangleMesh flameMesh = new TriangleMesh();
  double size = VP.absoluteRadius * .125;
  flameMesh.getPoints().setAll(
  (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size),
  (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size),
  (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size));
  flameMesh.getTexCoords().setAll(0, 0);
  flameMesh.getFaces().setAll(0, 0, 1, 0, 2, 0);
  setMesh(flameMesh);
  PhongMaterial PM = new PhongMaterial();
  U.Phong.setDiffuseRGB(PM, 0);
  U.Phong.setSpecularRGB(PM, 0);
  U.setMaterialSecurely(this, PM);
  setCullFace(CullFace.NONE);
  U.Nodes.add(this);
 }

 void run(Vehicle vehicle) {
  if (!vehicle.onFire) {
   setVisible(false);
  } else if (U.random() < .5 && VP.MV.isVisible()) {
   U.setTranslate(this, VP);
   ((PhongMaterial) getMaterial()).setSelfIlluminationMap(U.Images.get(SL.firelight + U.random(3)));
   U.randomRotate(this);
   setVisible(true);
  } else {
   setVisible(false);
  }
 }
}
