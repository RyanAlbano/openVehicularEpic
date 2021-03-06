package ve.effects;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

import ve.instances.CoreAdvanced;
import ve.utilities.Nodes;
import ve.utilities.Phong;
import ve.utilities.U;

public class Spark extends CoreAdvanced {

 private final Cylinder C;
 private double stage;

 public Spark() {
  C = new Cylinder(.001, 1, 3);
  PhongMaterial PM = new PhongMaterial();
  Phong.setDiffuseRGB(PM, 0);
  Phong.setSpecularRGB(PM, 0);
  PM.setSelfIlluminationMap(Effects.fireLight());
  U.setMaterialSecurely(C, PM);
  Nodes.add(C);
  C.setVisible(false);
 }

 public void deploy(double sourceX, double sourceY, double sourceZ, double sparkSpeed) {
  X = sourceX;
  Y = sourceY;
  Z = sourceZ;
  U.setScale(C, sparkSpeed);
  speedX = .5 * U.randomPlusMinus(sparkSpeed);
  speedY = .5 * U.randomPlusMinus(sparkSpeed);
  speedZ = .5 * U.randomPlusMinus(sparkSpeed);
  stage = Double.MIN_VALUE;
 }

 public void run(boolean gamePlay) {
  if (stage > 0) {
   if ((stage += U.random(U.tick)) > 2) {
    stage = 0;
    C.setVisible(false);
   } else {
    boolean show = false;
    if (gamePlay) {
     X += speedX * U.tick;
     Y += speedY * U.tick;
     Z += speedZ * U.tick;
    }
    if (U.render(this, false, true)) {
     U.setTranslate(C, this);
     U.randomRotate(C);
     show = true;
    }
    C.setVisible(show);
   }
  }
 }
}
