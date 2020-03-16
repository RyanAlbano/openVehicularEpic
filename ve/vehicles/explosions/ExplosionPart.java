package ve.vehicles.explosions;

import javafx.scene.paint.PhongMaterial;

import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import ve.instances.Core;
import ve.instances.CoreAdvanced;
import ve.utilities.Images;
import ve.utilities.Nodes;
import ve.utilities.Phong;
import ve.utilities.U;

class ExplosionPart extends CoreAdvanced {

 private final Explosion E;
 private final MeshView MV;
 private double stage, speed;
 private final Core positionSet = new Core();

 ExplosionPart(Explosion explosion) {
  E = explosion;
  TriangleMesh TM = new TriangleMesh();
  absoluteRadius = E.absoluteRadius * .25;
  TM.getPoints().setAll(
  (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius),
  (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius),
  (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius),
  (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius),
  (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius),
  (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius));
  TM.getTexCoords().setAll(0, 0);
  TM.getFaces().setAll(
  0, 0, 1, 0, 2, 0,
  3, 0, 4, 0, 5, 0);
  MV = new MeshView(TM);
  MV.setCullFace(CullFace.NONE);
  PhongMaterial PM = new PhongMaterial();
  Phong.setDiffuseRGB(PM, 0);
  Phong.setSpecularRGB(PM, 0);
  PM.setSelfIlluminationMap(Images.white);
  U.setMaterialSecurely(MV, PM);
  Nodes.add(MV);
  MV.setVisible(false);
 }

 public void deploy() {
  X = Y = Z = 0;
  XZ = U.random(360.);
  YZ = U.random(360.);
  speed = (1 + U.random()) * absoluteRadius;
  stage = Double.MIN_VALUE;
 }

 public void run(boolean gamePlay) {
  if (stage > 0) {
   if ((stage += gamePlay ? U.random(U.tick) : 0) > 5) {
    stage = 0;
    MV.setVisible(false);
   } else {
    if (gamePlay) {
     double cosYZ = U.cos(YZ);
     X -= speed * (U.sin(XZ) * cosYZ) * U.tick;
     Z += speed * (U.cos(XZ) * cosYZ) * U.tick;
     Y -= speed * U.sin(YZ) * U.tick;
    }
    if (E.focusVehicle != null) {
     positionSet.X = X + E.focusVehicle.X;
     positionSet.Y = Y + E.focusVehicle.Y;
     positionSet.Z = Z + E.focusVehicle.Z;
    } else {
     positionSet.X = X + E.inX;
     positionSet.Y = Y + E.inY;
     positionSet.Z = Z + E.inZ;
    }
    if (U.render(positionSet, -absoluteRadius, false, false)) {
     U.setTranslate(MV, positionSet);
     U.randomRotate(MV);
     MV.setVisible(true);
    } else {
     MV.setVisible(false);
    }
   }
  }
 }
}
