package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.effects.Dust;
import ve.instances.CoreAdvanced;
import ve.utilities.*;
import ve.utilities.sound.Controlled;
import ve.utilities.sound.Sounds;
import ve.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.List;

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
  for (var boulder : instances) {
   boulder.run(updateIfMatchBegan);
  }
 }

 public static void vehicleInteract(Vehicle V) {
  for (var boulder : instances) {
   if (U.distanceXZ(V, boulder) < V.collisionRadius + boulder.S.getRadius() && V.Y > boulder.Y - V.collisionRadius - boulder.S.getRadius()) {//<-Will call incorrectly in the unlikely event a vehicle is underground and the boulder rolls directly overhead
    V.setDamage(V.damageCeiling());
    V.deformParts();
    V.throwChips(boulder.speed, true);
    V.VA.crashDestroy.play(Double.NaN, V.VA.distanceVehicleToCamera);
   }
  }
 }

 public static class Instance extends CoreAdvanced {

  public final Sphere S;
  final double speed;
  private final List<Dust> dusts = new ArrayList<>();
  private int currentDust;
  final Controlled sound;

  Instance(double radius, double speed) {
   S = new Sphere(radius, 5);
   this.speed = speed;
   XZ = U.random(360.);
   PhongMaterial PM = new PhongMaterial();
   Y = -S.getRadius();
   Phong.setDiffuseRGB(PM, 1);
   Phong.setSpecularRGB(PM, E.Specular.Colors.standard);
   PM.setDiffuseMap(Images.get(D.rock));
   PM.setSpecularMap(Images.get(D.rock));
   PM.setBumpMap(Images.getNormalMap(D.rock));
   U.setMaterialSecurely(S, PM);
   Nodes.add(S);
   sound = new Controlled(D.boulder + U.random(2));
  }

  public void addTransparentNodes() {
   for (long n = Dust.defaultQuantity; --n >= 0; ) {
    dusts.add(new Dust());
   }
  }

  void run(boolean update) {
   if (update) {
    X += speed * U.sin(XZ) * U.tick;
    Z += speed * U.cos(XZ) * U.tick;
    dusts.get(currentDust).deploy(this);
    currentDust = ++currentDust >= Dust.defaultQuantity ? 0 : currentDust;
   }
   for (var otherBoulder : instances) {
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
   if (!Sounds.mute && update) {
    sound.loop(Math.sqrt(U.distance(this)) * Sounds.standardGain(1));
   } else {
    sound.stop();
   }
   for (var dust : dusts) {
    dust.run();
   }
  }

  public void closeSound() {
   if (sound != null) sound.close();
  }
 }
}
