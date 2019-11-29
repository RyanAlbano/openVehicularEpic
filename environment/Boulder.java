package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

import ve.Core;
import ve.Sound;
import ve.VE;
import ve.utilities.SL;
import ve.utilities.U;
import ve.effects.Dust;

import java.util.*;

public enum Boulder {
 ;
 private static double maxTravelDistance;
 public static final List<Instance> instances = new ArrayList<>();

 public static void load(String s) {
  if (s.startsWith("boulders(")) {
   for (int n = 0; n < U.getValue(s, 0); n++) {
    instances.add(new Instance(U.getValue(s, 1), U.getValue(s, 2)));
   }
   maxTravelDistance = U.getValue(s, 3);
  }
 }

 static void run(boolean updateIfMatchBegan) {
  for (Boulder.Instance boulder : instances) {
   boulder.run(updateIfMatchBegan);
  }
 }

 public static class Instance extends Core {

  public final Sphere S;
  public final double speed;
  private final List<Dust> dusts = new ArrayList<>();
  private int currentDust;
  public final Sound sound;

  Instance(double radius, double speed) {
   S = new Sphere(radius, 5);
   this.speed = speed;
   XZ = U.random(360.);
   PhongMaterial PM = new PhongMaterial();
   Y = -S.getRadius();
   U.Phong.setDiffuseRGB(PM, 1);
   U.Phong.setSpecularRGB(PM, E.Specular.Colors.standard);
   PM.setDiffuseMap(U.Images.get(SL.rock));
   PM.setSpecularMap(U.Images.get(SL.rock));
   PM.setBumpMap(U.Images.getNormalMap(SL.rock));
   U.setMaterialSecurely(S, PM);
   U.Nodes.add(S);
   sound = new Sound(SL.boulder, Double.POSITIVE_INFINITY);
  }

  public void addTransparentNodes() {
   for (int n = E.dustQuantity; --n >= 0; ) {
    dusts.add(new Dust());
   }
  }

  void run(boolean update) {
   if (update) {
    X += speed * U.sin(XZ) * VE.tick;
    Z += speed * U.cos(XZ) * VE.tick;
    dusts.get(currentDust).deploy(this);
    currentDust = ++currentDust >= E.dustQuantity ? 0 : currentDust;
   }
   for (Instance otherBoulder : instances) {
    if (U.random() < .5 && otherBoulder != this && U.distance(this, otherBoulder) < S.getRadius() + otherBoulder.S.getRadius()) {
     XZ = U.random(360.);
    }
   }
   while (Math.abs(X) > maxTravelDistance) {
    X += (X < 0 ? 2 : -2) * maxTravelDistance;
   }
   while (Math.abs(Z) > maxTravelDistance) {
    Z += (Z < 0 ? 2 : -2) * maxTravelDistance;
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
   if (!VE.Match.muteSound && update) {
    sound.loop(Math.sqrt(U.distance(this)) * Sound.standardDistance(1));
   } else {
    sound.stop();
   }
   for (Dust dust : dusts) {
    dust.run();
   }
  }
 }
}
