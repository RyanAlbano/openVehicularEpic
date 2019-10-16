package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

import ve.Core;
import ve.Sound;
import ve.VE;
import ve.utilities.U;
import ve.effects.Dust;

import java.util.*;

public class Boulder extends Core {

 public final Sphere S;
 public final double speed;
 private final List<Dust> dusts = new ArrayList<>();
 private int currentDust;
 public final Sound sound;

 Boulder(double radius, double speed) {
  S = new Sphere(radius, 5);
  this.speed = speed;
  XZ = U.random(360.);
  PhongMaterial PM = new PhongMaterial();
  Y = -S.getRadius();
  U.setDiffuseRGB(PM, 1, 1, 1);
  U.setSpecularRGB(PM, .5, .5, .5);
  PM.setDiffuseMap(U.getImage("rock"));
  PM.setSpecularMap(U.getImage("rock"));
  PM.setBumpMap(U.getImageNormal("rock"));
  S.setMaterial(PM);
  U.add(S);
  for (int n = E.dustQuantity; --n >= 0; ) {
   dusts.add(new Dust());
  }
  sound = new Sound("boulder", Double.POSITIVE_INFINITY);
 }

 public void run(boolean update) {
  if (update) {
   X += speed * U.sin(XZ) * VE.tick;
   Z += speed * U.cos(XZ) * VE.tick;
   dusts.get(currentDust).deploy(this);
   currentDust = ++currentDust >= E.dustQuantity ? 0 : currentDust;
  }
  for (Boulder otherBoulder : E.boulders) {
   if (U.random() < .5 && otherBoulder != this && U.distance(this, otherBoulder) < S.getRadius() + otherBoulder.S.getRadius()) {
    XZ = U.random(360.);
   }
  }
  while (Math.abs(X) > E.boulderMaxTravelDistance) {
   X += (X < 0 ? 2 : -2) * E.boulderMaxTravelDistance;
  }
  while (Math.abs(Z) > E.boulderMaxTravelDistance) {
   Z += (Z < 0 ? 2 : -2) * E.boulderMaxTravelDistance;
  }
  if (U.getDepth(this) > -S.getRadius()) {
   if (update) {
    U.randomRotate(S);
   }
   U.setTranslate(S, X, Y, Z);
   S.setVisible(true);
  } else {
   S.setVisible(false);
  }
  if (!VE.muteSound && update) {
   sound.loop(Math.sqrt(U.distance(this)) * .08);
  } else {
   sound.stop();
  }
  for (Dust dust : dusts) {
   dust.run();
  }
 }
}
