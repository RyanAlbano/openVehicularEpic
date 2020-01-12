package ve.effects;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;

import ve.instances.Core;
import ve.instances.CoreAdvanced;
import ve.utilities.Images;
import ve.utilities.Nodes;
import ve.utilities.Phong;
import ve.utilities.U;
import ve.vehicles.explosions.Explosion;

public class ExplosionPart extends CoreAdvanced {

 private final MeshView MV;
 private double stage, speed;
 final Core positionSet = new Core();

 public ExplosionPart(Explosion explosion) {
  TriangleMesh TM = new TriangleMesh();
  absoluteRadius = explosion.absoluteRadius * .25;
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

 public void run(Explosion explosion, boolean gamePlay) {
  if (stage > 0) {
   if ((stage += gamePlay ? U.random(U.tick) : 0) > 5) {
    stage = 0;
    MV.setVisible(false);
   } else {
    if (gamePlay) {
     X -= speed * (U.sin(XZ) * U.cos(YZ)) * U.tick;
     Z += speed * (U.cos(XZ) * U.cos(YZ)) * U.tick;
     Y -= speed * U.sin(YZ) * U.tick;
    }
    if (explosion.focusVehicle != null) {
     positionSet.X = X + explosion.focusVehicle.X;
     positionSet.Y = Y + explosion.focusVehicle.Y;
     positionSet.Z = Z + explosion.focusVehicle.Z;
    } else {
     positionSet.X = X + explosion.inX;
     positionSet.Y = Y + explosion.inY;
     positionSet.Z = Z + explosion.inZ;
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
