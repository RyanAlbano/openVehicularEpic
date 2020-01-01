package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

import ve.instances.Core;
import ve.ui.Match;
import ve.ui.UI;
import ve.utilities.Sound;
import ve.utilities.U;
import ve.vehicles.Splash;
import ve.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.List;

public enum Tsunami {
 ;
 public static boolean exists;
 public static double X, Z;
 private static long speed;
 private static double speedX, speedZ;
 private static double globalSize;
 public static Direction direction;
 public static final List<Part> parts = new ArrayList<>();
 public static Sound sound;

 public enum Direction {
  forward, backward, right, left;

  static Direction random() {
   return Direction.values()[U.random(Direction.values().length)];
  }
 }

 public static void load(String s) {
  if (s.startsWith("tsunami(")) {
   exists = true;
   for (int n = 0; n < 200; n++) {
    parts.add(new Part(U.getValue(s, 0)));
   }
   speed = Math.round(U.getValue(s, 1));
   try {
    globalSize = Math.abs(U.getValue(s, 2));
   } catch (RuntimeException e) {
    globalSize = 200000;
   }
   for (Part tsunamiPart : parts) {
    tsunamiPart.Y = -tsunamiPart.C.getRadius() * .5;
    U.Nodes.add(tsunamiPart.C);
   }
   wrap();
  }
 }

 static void run(boolean update, boolean updateIfMatchBegan) {
  if (exists) {
   if (UI.status != UI.Status.replay) {//<-Still executes if in map viewer--using 'VE.Status.play' does not
    if (updateIfMatchBegan && Math.abs(direction == Tsunami.Direction.left || direction == Tsunami.Direction.right ? X : Z) > globalSize) {
     wrap();
    }
    if (updateIfMatchBegan) {
     X += speedX * UI.tick;
     Z += speedZ * UI.tick;
    }
   }
   double tsunamiPartShift = globalSize * .01;
   if (direction == Tsunami.Direction.forward || direction == Tsunami.Direction.backward) {//<-Was in wrap(), but now here to make recording effective
    speedX = 0;
    speedZ = speed * (direction == Direction.forward ? 1 : -1);
    for (int n = parts.size(); --n >= 0; ) {
     parts.get(n).X = n * tsunamiPartShift - globalSize;
     parts.get(n).Z = Z;
    }
   } else {
    speedZ = 0;
    speedX = speed * (direction == Direction.right ? 1 : -1);
    for (int n = parts.size(); --n >= 0; ) {
     parts.get(n).Z = n * tsunamiPartShift - globalSize;
     parts.get(n).X = X;
    }
   }
   for (Tsunami.Part tsunamiPart : parts) {
    tsunamiPart.run();
   }
   if (!Match.muteSound && update) {
    double soundDistance = Double.POSITIVE_INFINITY;
    for (Tsunami.Part tsunamiPart : parts) {
     soundDistance = Math.min(soundDistance, U.distance(tsunamiPart));
    }
    sound.loop(Math.sqrt(soundDistance) * Sound.standardDistance(.5));
   } else {
    sound.stop();
   }
  }
 }

 private static void wrap() {
  direction = Tsunami.Direction.random();
  if (direction == Tsunami.Direction.forward || direction == Tsunami.Direction.backward) {
   X = -globalSize;
   Z = direction == Direction.forward ? -globalSize : globalSize;
  } else {
   Z = -globalSize;
   X = direction == Tsunami.Direction.left ? globalSize : -globalSize;
  }
 }

 public static void vehicleInteract(Vehicle V) {
  for (Tsunami.Part tsunamiPart : parts) {
   if (U.distance(V, tsunamiPart) < V.collisionRadius + tsunamiPart.C.getRadius()) {
    if (V.getsPushed >= 0) {
     V.P.speedX += speedX * .5 * UI.tick;
     V.P.speedZ += speedZ * .5 * UI.tick;
    }
    if (V.getsLifted >= 0) {
     V.P.speedY += E.gravity * UI.tick * 4 * Double.compare(tsunamiPart.Y, V.Y);
    }
    for (int n1 = 20; --n1 >= 0; ) {
     V.splashes.get(V.currentSplash).deploy(V.wheels.isEmpty() ? null : V.wheels.get(U.random(4)), U.random(V.absoluteRadius * .05),
     speedX + U.randomPlusMinus(Math.max(speed, V.P.netSpeed)),
     U.randomPlusMinus(Math.max(speed, V.P.netSpeed)),
     speedZ + U.randomPlusMinus(Math.max(speed, V.P.netSpeed)));
     V.currentSplash = ++V.currentSplash >= Splash.defaultQuantity ? 0 : V.currentSplash;
    }
    V.VA.tsunamiSplash.playIfNotPlaying(V.VA.distanceVehicleToCamera);
   }
  }
 }

 public static class Part extends Core {
  public final Cylinder C;

  Part(double size) {
   C = new Cylinder(size, size);//<-Keep both parameters--not sure if Cylinder(double) yields same result
   U.setMaterialSecurely(C, new PhongMaterial());
  }

  void run() {
   if (U.render(this, -C.getRadius())) {
    U.setTranslate(C, this);
    U.randomRotate(C);
    double waveRG = U.random(2.);
    if (Pool.type == Pool.Type.lava) {
     U.Phong.setDiffuseRGB((PhongMaterial) C.getMaterial(), 1, waveRG, waveRG * .5);
    } else if (Pool.type == Pool.Type.acid) {
     U.Phong.setDiffuseRGB((PhongMaterial) C.getMaterial(), waveRG * .5, 1, waveRG);
    } else {
     U.Phong.setDiffuseRGB((PhongMaterial) C.getMaterial(), waveRG * .5, waveRG, 1);
    }
    C.setVisible(true);
   } else {
    C.setVisible(false);
   }
  }
 }
}
