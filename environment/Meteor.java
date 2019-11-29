package ve.environment;

import java.util.*;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.Camera;
import ve.Core;
import ve.Sound;
import ve.VE;
import ve.utilities.SL;
import ve.utilities.U;
import ve.vehicles.Vehicle;
import ve.vehicles.VehiclePart;

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
  for (Meteor.Instance meteor : instances) {
   meteor.run(update);
  }
 }

 public static void vehicleInteract(Vehicle V) {
  for (Instance meteor : instances) {
   Instance.Part MP = meteor.parts.get(0);
   double vehicleMeteorDistance = U.distance(V, MP);
   if (vehicleMeteorDistance < (V.collisionRadius + MP.S.getRadius()) * 4) {
    V.addDamage(V.durability * .5);
    V.P.speedX += U.randomPlusMinus(globalSpeed * .5);
    V.P.speedZ += U.randomPlusMinus(globalSpeed * .5);
    if (vehicleMeteorDistance < V.collisionRadius + MP.S.getRadius() * 2) {
     V.setDamage(V.damageCeiling());
     V.P.speedX += U.randomPlusMinus(globalSpeed * .5);
     V.P.speedZ += U.randomPlusMinus(globalSpeed * .5);
    }
    for (VehiclePart part : V.parts) {
     part.deform();
     part.throwChip(U.randomPlusMinus(U.netValue(meteor.speedX, globalSpeed, meteor.speedZ)));
    }
    V.VA.crashDestroy.play(Double.NaN, V.VA.distanceVehicleToCamera);
   }
  }
 }

 public static class Instance {

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

  void run(boolean update) {
   parts.get(0).onFire = U.random() < .1;
   boolean deploy = false;
   if (update) {
    parts.get(0).X += speedX * VE.tick;
    parts.get(0).Y += speedY * VE.tick;
    parts.get(0).Z += speedZ * VE.tick;
    deploy = Math.abs(parts.get(0).Y - Camera.Y) > 375000 || Math.abs(parts.get(0).X - Camera.X) > 500000 || Math.abs(parts.get(0).Z - Camera.Z) > 500000;
   }
   if (parts.get(parts.size() - 1).Y >= E.Ground.level || deploy) {
    if (E.Ground.level == Double.POSITIVE_INFINITY && U.random() < .5) {
     speedY *= -1;
    }
    parts.get(0).X = Camera.X + U.randomPlusMinus(500000.);
    parts.get(0).Y = Camera.Y - ((125000 - U.random(250000.)) * speedY > 0 ? 1 : -1);
    parts.get(0).Z = Camera.Z + U.randomPlusMinus(500000.);
    double speedsXZ = U.random(2.) * globalSpeed;
    speedX = U.random() < .5 ? speedsXZ : -speedsXZ;
    speedsXZ -= globalSpeed * 2;
    speedZ = U.random() < .5 ? speedsXZ : -speedsXZ;
    for (Part meteorPart : parts) {
     meteorPart.rotation[0] = U.randomPlusMinus(45.);
     meteorPart.rotation[1] = U.randomPlusMinus(45.);
    }
    parts.get(parts.size() - 1).Y = parts.get(0).Y;
    speedX = parts.get(0).X > Camera.X ? -Math.abs(speedX) : parts.get(0).X < Camera.X ? Math.abs(speedX) : speedX;
    speedZ = parts.get(0).Z > Camera.Z ? -Math.abs(speedZ) : parts.get(0).Z < Camera.Z ? Math.abs(speedZ) : speedZ;
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
   if (!VE.Match.muteSound && update) {
    sound.loop(Math.sqrt(U.distance(parts.get(0))) * Sound.standardDistance(1));
   } else {
    sound.stop();
   }
  }

  static class Part extends Core {

   final Sphere S;
   final double[] rotation = new double[3];
   boolean onFire;

   Part(double radius) {
    S = new Sphere(radius, 1);
    U.setMaterialSecurely(S, new PhongMaterial());
    U.Nodes.add(S);
   }

   void run() {
    XZ += rotation[0] * VE.tick;
    YZ += rotation[1] * VE.tick;
    if (U.render(this, -S.getRadius())) {
     if (onFire) {
      U.Phong.setDiffuseRGB((PhongMaterial) S.getMaterial(), 0);
      U.Phong.setSpecularRGB((PhongMaterial) S.getMaterial(), 0);
      ((PhongMaterial) S.getMaterial()).setDiffuseMap(null);
      ((PhongMaterial) S.getMaterial()).setSelfIlluminationMap(U.Images.get(SL.firelight + U.random(3)));
     } else {
      U.Phong.setDiffuseRGB((PhongMaterial) S.getMaterial(), 1);
      U.Phong.setSpecularRGB((PhongMaterial) S.getMaterial(), 1);
      ((PhongMaterial) S.getMaterial()).setDiffuseMap(U.Images.get(SL.rock));
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
