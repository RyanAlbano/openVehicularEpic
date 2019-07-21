package ve.effects;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

import static ve.VE.*;

import ve.utilities.U;

public class Spark extends Cylinder {

 private double X;
 private double Y;
 private double Z;
 private double stage;
 private double speedX;
 private double speedY;
 private double speedZ;

 public Spark() {
  super(.001, 1, 3);
  PhongMaterial PM = new PhongMaterial();
  U.setDiffuseRGB(PM, 0, 0, 0);
  U.setSpecularRGB(PM, 0, 0, 0);
  PM.setSelfIlluminationMap(U.getImage("firelight" + U.random(3)));//<-Integer random for lists
  setMaterial(PM);
  U.add(this);
  setVisible(false);
 }

 public void deploy(double sourceX, double sourceY, double sourceZ, double sparkSpeed) {
  X = sourceX;
  Y = sourceY;
  Z = sourceZ;
  U.setScale(this, sparkSpeed);
  speedX = .1 * Math.max(-sparkSpeed, Math.min(U.randomPlusMinus(sparkSpeed * 2.), sparkSpeed));
  speedY = .1 * Math.max(-sparkSpeed, Math.min(U.randomPlusMinus(sparkSpeed * 2.), sparkSpeed));
  speedZ = .1 * Math.max(-sparkSpeed, Math.min(U.randomPlusMinus(sparkSpeed * 2.), sparkSpeed));
  stage = Double.MIN_VALUE;
 }

 public void run(boolean gamePlay) {
  if (stage > 0) {
   if ((stage += U.random(tick)) > 2) {
    stage = 0;
    setVisible(false);
   } else {
    boolean show = false;
    if (gamePlay) {
     X += speedX;
     Y += speedY;
     Z += speedZ;
    }
    if (U.getDepth(X, Y, Z) > 0) {
     U.setTranslate(this, X, Y, Z);
     U.randomRotate(this);
     show = true;
    }
    setVisible(show);
   }
  }
 }
}
