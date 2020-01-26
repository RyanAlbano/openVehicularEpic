package ve.environment;

import java.util.*;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.effects.Effects;
import ve.instances.CoreAdvanced;
import ve.ui.Match;
import ve.utilities.*;
import ve.vehicles.Vehicle;

public enum Meteor {
 ;
 public static final Collection<Instance> instances = new ArrayList<>();
 private static double globalSpeed;

 public static void load(String s) {
  if (s.startsWith("meteors(")) {
   globalSpeed = U.getValue(s, 2);//<-Must come FIRST!
   for (int n = 0; n < U.getValue(s, 0); n++) {
    instances.add(new Instance(U.getValue(s, 1)));
   }
  }
 }

 static void run(boolean update) {
  for (Meteor.Instance instance : instances) {
   instance.run(update);
  }
 }

 public static void vehicleInteract(Vehicle V) {
  for (Instance meteor : instances) {
   Instance.Part MP = meteor.parts.get(0);
   double vehicleMeteorDistance = U.distance(V, MP);
   if (vehicleMeteorDistance < (V.collisionRadius + MP.S.getRadius()) * 4) {
    V.addDamage(V.durability * .5);
    V.speedX += U.randomPlusMinus(globalSpeed * .5);
    V.speedZ += U.randomPlusMinus(globalSpeed * .5);
    if (vehicleMeteorDistance < V.collisionRadius + MP.S.getRadius() * 2) {
     V.setDamage(V.damageCeiling());
     V.speedX += U.randomPlusMinus(globalSpeed * .5);
     V.speedZ += U.randomPlusMinus(globalSpeed * .5);
    }
    V.deformParts();
    V.throwChips(U.netValue(meteor.speedX, globalSpeed, meteor.speedZ), true);
    V.VA.crashDestroy.play(Double.NaN, V.VA.distanceVehicleToCamera);
   }
  }
 }

 public static class Instance {//Core extends in the parts, not the main instance

  double speedX, speedY = globalSpeed, speedZ;
  final List<Part> parts = new ArrayList<>();
  public final Sound sound;

  Instance(double inSize) {
   double size = 0;
   for (int n1 = 0; n1 < 10; n1++) {
    parts.add(new Part(inSize - size));
    size += inSize / 11.;
   }
   sound = new Sound(SL.meteor + U.random(3));//<-using lists--NO double
  }

  void deploy() {
   if (Ground.level == Double.POSITIVE_INFINITY && U.random() < .5) {
    speedY *= -1;
   }
   parts.get(0).X = Camera.C.X + U.randomPlusMinus(500000.);
   parts.get(0).Y = Camera.C.Y - ((125000 + U.random(250000.)) * (speedY > 0 ? 1 : -1));
   parts.get(0).Z = Camera.C.Z + U.randomPlusMinus(500000.);
   double speedsXZ = U.random(2.) * globalSpeed;
   speedX = U.random() < .5 ? speedsXZ : -speedsXZ;
   speedsXZ -= globalSpeed * 2;
   speedZ = U.random() < .5 ? speedsXZ : -speedsXZ;
   for (Part meteorPart : parts) {
    meteorPart.rotation[0] = U.randomPlusMinus(45.);
    meteorPart.rotation[1] = U.randomPlusMinus(45.);
   }
   parts.get(parts.size() - 1).Y = parts.get(0).Y;//<-Why was this done?
   speedX = parts.get(0).X > Camera.C.X ? -Math.abs(speedX) : parts.get(0).X < Camera.C.X ? Math.abs(speedX) : 0;
   speedZ = parts.get(0).Z > Camera.C.Z ? -Math.abs(speedZ) : parts.get(0).Z < Camera.C.Z ? Math.abs(speedZ) : 0;
  }

  private void run(boolean update) {
   parts.get(0).onFire = U.random() < .1;
   if (update) {
    parts.get(0).X += speedX * U.tick;
    parts.get(0).Y += speedY * U.tick;
    parts.get(0).Z += speedZ * U.tick;
    if (parts.get(parts.size() - 1).Y >= Ground.level ||
    Math.abs(parts.get(0).Y - Camera.C.Y) > 375000 || Math.abs(parts.get(0).X - Camera.C.X) > 500000 || Math.abs(parts.get(0).Z - Camera.C.Z) > 500000) {
     deploy();
    }
   }
   for (int n = 1; n < parts.size(); n++) {
    parts.get(n).X = parts.get(0).X - (speedX * n);
    parts.get(n).Y = parts.get(0).Y - (speedY * n);
    parts.get(n).Z = parts.get(0).Z - (speedZ * n);
   }
   for (int n = parts.size(); --n > 0; ) {
    parts.get(n).onFire = parts.get(n - 1).onFire;
   }
   for (Part meteorPart : parts) {
    meteorPart.run();
   }
   if (!Match.muteSound && update) {
    sound.loop(Math.sqrt(U.distance(parts.get(0))) * Sound.standardGain(1));
   } else {
    sound.stop();
   }
  }

  static class Part extends CoreAdvanced {

   final Sphere S;
   final double[] rotation = new double[3];
   boolean onFire;

   Part(double radius) {
    S = new Sphere(radius, 1);
    U.setMaterialSecurely(S, new PhongMaterial());
    Nodes.add(S);
   }

   void run() {
    XZ += rotation[0] * U.tick;
    YZ += rotation[1] * U.tick;
    if (U.render(this, -S.getRadius(), false, false)) {
     if (onFire) {
      Phong.setDiffuseRGB((PhongMaterial) S.getMaterial(), 0);
      Phong.setSpecularRGB((PhongMaterial) S.getMaterial(), 0);
      ((PhongMaterial) S.getMaterial()).setDiffuseMap(null);
      ((PhongMaterial) S.getMaterial()).setSelfIlluminationMap(Effects.fireLight());
     } else {
      Phong.setDiffuseRGB((PhongMaterial) S.getMaterial(), 1);
      Phong.setSpecularRGB((PhongMaterial) S.getMaterial(), 1);
      ((PhongMaterial) S.getMaterial()).setDiffuseMap(Images.get(SL.rock));
      ((PhongMaterial) S.getMaterial()).setSelfIlluminationMap(null);
     }
     U.rotate(S, YZ, XZ);
     U.setTranslate(S, this);
     S.setVisible(true);
    } else {
     S.setVisible(false);
    }
   }
  }
 }
}
