package ve.effects;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;

import static ve.VE.*;

import ve.utilities.U;

class ExplosionPart extends MeshView {

 private double X, Y, Z, XZ, YZ, stage, speed;
 private final double size;

 ExplosionPart(Explosion explosion) {
  TriangleMesh TM = new TriangleMesh();
  size = explosion.radius * .25;
  TM.getPoints().setAll(
  (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size),
  (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size),
  (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size),
  (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size),
  (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size),
  (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size));
  TM.getTexCoords().setAll(0, 0);
  TM.getFaces().setAll(
  0, 0, 1, 0, 2, 0,
  3, 0, 4, 0, 5, 0);
  setMesh(TM);
  setCullFace(CullFace.NONE);
  PhongMaterial PM = new PhongMaterial();
  U.setDiffuseRGB(PM, 0, 0, 0);
  U.setSpecularRGB(PM, 0, 0, 0);
  PM.setSelfIlluminationMap(U.getImage("white"));
  setMaterial(PM);
  U.add(this);
  setVisible(false);
 }

 void deploy() {
  X = Y = Z = 0;
  XZ = U.random(360.);
  YZ = U.random(360.);
  speed = (1 + U.random()) * size;
  stage = Double.MIN_VALUE;
 }

 public void run(Explosion explosion, boolean gamePlay) {
  if (stage > 0) {
   if ((stage += gamePlay ? U.random(tick) : 0) > 5) {
    stage = 0;
    setVisible(false);
   } else {
    if (gamePlay) {
     X -= speed * (U.sin(XZ) * U.cos(YZ)) * tick;
     Z += speed * (U.cos(XZ) * U.cos(YZ)) * tick;
     Y -= speed * U.sin(YZ) * tick;
     X = U.clamp(limitL, X, limitR);
     Z = U.clamp(limitBack, Z, limitFront);
     Y = U.clamp(limitY, Y, -limitY);
    }
    if (U.getDepth(X + explosion.X, Y + explosion.Y, Z + explosion.Z) > -size) {
     U.setTranslate(this, X + explosion.X, Y + explosion.Y, Z + explosion.Z);
     U.randomRotate(this);
     setVisible(true);
    } else {
     setVisible(false);
    }
   }
  }
 }
}
