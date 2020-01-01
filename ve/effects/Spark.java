package ve.effects;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

import ve.instances.CoreAdvanced;
import ve.ui.UI;
import ve.utilities.U;

public class Spark extends CoreAdvanced {

 private final Cylinder C;
 private double stage;

 public Spark() {
  C = new Cylinder(.001, 1, 3);
  PhongMaterial PM = new PhongMaterial();
  U.Phong.setDiffuseRGB(PM, 0);
  U.Phong.setSpecularRGB(PM, 0);
  PM.setSelfIlluminationMap(Effects.fireLight());
  U.setMaterialSecurely(C, PM);
  U.Nodes.add(C);
  C.setVisible(false);
 }

 public void deploy(double sourceX, double sourceY, double sourceZ, double sparkSpeed) {
  X = sourceX;
  Y = sourceY;
  Z = sourceZ;
  U.setScale(C, sparkSpeed);
  speedX = .1 * Math.max(-sparkSpeed, Math.min(U.randomPlusMinus(sparkSpeed * 2.), sparkSpeed));
  speedY = .1 * Math.max(-sparkSpeed, Math.min(U.randomPlusMinus(sparkSpeed * 2.), sparkSpeed));
  speedZ = .1 * Math.max(-sparkSpeed, Math.min(U.randomPlusMinus(sparkSpeed * 2.), sparkSpeed));
  stage = Double.MIN_VALUE;
 }

 public void run(boolean gamePlay) {
  if (stage > 0) {
   if ((stage += U.random(UI.tick)) > 2) {
    stage = 0;
    C.setVisible(false);
   } else {
    boolean show = false;
    if (gamePlay) {
     X += speedX;
     Y += speedY;
     Z += speedZ;
    }
    if (U.render(this)) {
     U.setTranslate(C, this);
     U.randomRotate(C);
     show = true;
    }
    C.setVisible(show);
   }
  }
 }
}
