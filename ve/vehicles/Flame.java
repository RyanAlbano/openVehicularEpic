package ve.vehicles;

import javafx.scene.paint.*;
import javafx.scene.shape.*;
import ve.effects.Effects;
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
  Phong.setDiffuseRGB(PM, 0);
  Phong.setSpecularRGB(PM, 0);
  U.setMaterialSecurely(this, PM);
  setCullFace(CullFace.NONE);
  Nodes.add(this);
 }

 void run() {
  if (!VP.V.onFire) {
   setVisible(false);
  } else if (U.random() < .5 && VP.MV.isVisible()) {
   U.setTranslate(this, VP);
   ((PhongMaterial) getMaterial()).setSelfIlluminationMap(Effects.fireLight());
   U.randomRotate(this);
   setVisible(true);
  } else {
   setVisible(false);
  }
 }
}
