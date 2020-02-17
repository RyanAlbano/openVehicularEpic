package ve.trackElements.trackParts;


import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.instances.Core;
import ve.utilities.Images;
import ve.utilities.Nodes;
import ve.utilities.D;
import ve.utilities.U;

class RoadRock extends Core {
 private final Sphere S;

 RoadRock(double inX, double inY, double inZ, double inXZ) {
  S = new Sphere(1, 5);
  S.setScaleX(100 + U.random(200.));
  S.setScaleY(U.random(25.));
  S.setScaleZ(100 + U.random(200.));
  absoluteRadius = Math.max(S.getScaleX(), Math.max(S.getScaleY(), S.getScaleZ()));
  U.rotate(S, 0, U.random(360.));
  X = inX + U.randomPlusMinus(Math.max(840, 2500 * Math.abs(U.sin(inXZ))));
  Y = inY;
  Z = inZ + U.randomPlusMinus(Math.max(840, 2500 * Math.abs(U.cos(inXZ))));
  PhongMaterial PM = new PhongMaterial();
  U.setMaterialSecurely(S, PM);
  PM.setDiffuseMap(Images.get(D.rock));
  PM.setSpecularMap(Images.get(D.rock));
  PM.setBumpMap(Images.getNormalMap(D.rock));
  Nodes.add(S);
 }

 void run() {
  if ((U.render(this, true, true))) {
   U.setTranslate(S, this);
   S.setVisible(true);
  } else {
   S.setVisible(false);
  }
 }
}
