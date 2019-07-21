package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

import static ve.VE.*;

import ve.VE;
import ve.utilities.U;
import ve.effects.Dust;

import java.util.*;

public class Boulder extends Sphere {

 public double X;
 public final double Y;
 public double Z;
 private double XZ;
 public final double speed;
 private final List<Dust> dusts = new ArrayList<>();
 private int currentDust;

 public Boulder(double radius, int divisions, double speed) {
  super(radius, divisions);
  this.speed = speed;
  XZ = U.random(360.);
  PhongMaterial PM = new PhongMaterial();
  Y = -getRadius();
  U.setDiffuseRGB(PM, 1, 1, 1);
  U.setSpecularRGB(PM, .5, .5, .5);
  PM.setDiffuseMap(U.getImage("rock"));
  PM.setSpecularMap(U.getImage("rock"));
  PM.setBumpMap(U.getImageNormal("rock"));
  setMaterial(PM);
  U.add(this);
  for (int n = VE.dustQuantity; --n >= 0; ) {
   dusts.add(new Dust());
  }
 }

 public void run(boolean update) {
  if (update) {
   X += speed * U.sin(XZ) * tick;
   Z += speed * U.cos(XZ) * tick;
   dusts.get(currentDust).deploy(this);
   currentDust = ++currentDust >= VE.dustQuantity ? 0 : currentDust;
  }
  for (Boulder otherBoulder : E.boulders) {
   if (U.random() < .5 && otherBoulder != this && U.distance(X, otherBoulder.X, Z, otherBoulder.Z) < getRadius() + otherBoulder.getRadius()) {
    XZ = U.random(360.);
   }
  }
  while (Math.abs(X) > E.boulderMaxTravelDistance) {
   X += (X < 0 ? 2 : -2) * E.boulderMaxTravelDistance;
  }
  while (Math.abs(Z) > E.boulderMaxTravelDistance) {
   Z += (Z < 0 ? 2 : -2) * E.boulderMaxTravelDistance;
  }
  if (U.getDepth(X, Y, Z) > -getRadius()) {
   if (update) {
    U.randomRotate(this);
   }
   U.setTranslate(this, X, Y, Z);
   setVisible(true);
  } else {
   setVisible(false);
  }
  if (!muteSound && update) {
   U.soundLoop(sounds, "boulder" + E.boulders.indexOf(this), Math.sqrt(U.distance(cameraX, X, cameraY, Y, cameraZ, Z)) * .08);
  } else {
   U.soundStop(sounds, "boulder" + E.boulders.indexOf(this));
  }
  for (Dust dust : dusts) {
   dust.run();
  }
 }
}
