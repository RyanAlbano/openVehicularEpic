package ve.effects;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;

import ve.Core;
import ve.VE;
import ve.utilities.U;

class ExplosionPart extends Core {

 private final MeshView MV;
 private double stage, speed;

 ExplosionPart(Explosion explosion) {
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
  U.setDiffuseRGB(PM, 0, 0, 0);
  U.setSpecularRGB(PM, 0, 0, 0);
  PM.setSelfIlluminationMap(U.getImage("white"));
  MV.setMaterial(PM);
  U.add(MV);
  MV.setVisible(false);
 }

 void deploy() {
  X = Y = Z = 0;
  XZ = U.random(360.);
  YZ = U.random(360.);
  speed = (1 + U.random()) * absoluteRadius;
  stage = Double.MIN_VALUE;
 }

 public void run(Explosion explosion, boolean gamePlay) {
  if (stage > 0) {
   if ((stage += gamePlay ? U.random(VE.tick) : 0) > 5) {
    stage = 0;
    MV.setVisible(false);
   } else {
    if (gamePlay) {
     X -= speed * (U.sin(XZ) * U.cos(YZ)) * VE.tick;
     Z += speed * (U.cos(XZ) * U.cos(YZ)) * VE.tick;
     Y -= speed * U.sin(YZ) * VE.tick;
    }
    double setX, setY, setZ;
    if (explosion.focusVehicle != null) {
     setX = X + explosion.focusVehicle.X;
     setY = Y + explosion.focusVehicle.Y;
     setZ = Z + explosion.focusVehicle.Z;
    } else {
     setX = X + explosion.inX;
     setY = Y + explosion.inY;
     setZ = Z + explosion.inZ;
    }
    if (U.render(setX, setY, setZ, -absoluteRadius)) {
     U.setTranslate(MV, setX, setY, setZ);
     U.randomRotate(MV);
     MV.setVisible(true);
    } else {
     MV.setVisible(false);
    }
   }
  }
 }
}
