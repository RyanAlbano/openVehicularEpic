package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import ve.instances.Core;
import ve.ui.Match;
import ve.utilities.*;
import ve.vehicles.Physics;
import ve.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.List;

public enum Tornado {
 ;
 public static boolean movesRepairPoints;
 private static double maxTravelDistance;
 public static final List<Part> parts = new ArrayList<>();
 public static Sound sound;

 public static void load(String s) {
  if (s.startsWith("tornado(")) {
   double size = 1;
   for (int n = 0; n < 40; n++) {
    parts.add(new Part(U.getValue(s, 0) * size, U.getValue(s, 0) * size));
    size *= U.getValue(s, 2);
    parts.get(n).Y = U.getValue(s, 1) * n / 40.;
    Nodes.add(parts.get(n).C);
    parts.get(n).groundDustC = new Cylinder(
    (n + 1) * U.getValue(s, 0) * .01,
    (n + 1) * U.getValue(s, 0) * .01);
    parts.get(n).groundDustPM.setSpecularPower(E.Specular.Powers.dull);
    Nodes.add(parts.get(n).groundDustC);
   }
   maxTravelDistance = U.getValue(s, 3);
   movesRepairPoints = s.contains("moveRepairPoints");
  }
 }

 static void run(boolean update) {
  if (!parts.isEmpty()) {
   if (Wind.maxPotency > 0 && update) {
    parts.get(0).X += Wind.speedX * U.tick;
    parts.get(0).Z += Wind.speedZ * U.tick;
   }
   if (U.distance(0, parts.get(0).X, 0, parts.get(0).Z) > maxTravelDistance) {
    parts.get(0).X *= .999;
    parts.get(0).Z *= .999;
    Wind.speedX *= -1;
    Wind.speedZ *= -1;
   }
   for (int n = 1; n < parts.size(); n++) {
    parts.get(n).X = (parts.get(n - 1).X + parts.get(n).X) * .5;
    parts.get(n).Z = (parts.get(n - 1).Z + parts.get(n).Z) * .5;
   }
   for (Tornado.Part tornadoPart : parts) {
    tornadoPart.run();
   }
   if (!Match.muteSound && update) {
    sound.loop(Math.sqrt(U.distance(parts.get(0))) * Sound.standardGain(1));
   } else {
    sound.stop();
   }
  }
 }

 public static void vehicleInteract(Vehicle V) {
  V.P.inTornado = false;
  if (!parts.isEmpty() && !V.phantomEngaged && V.Y > parts.get(parts.size() - 1).Y && U.distance(V.X, parts.get(0).X, V.Z, parts.get(0).Z) < parts.get(0).C.getRadius() * 7.5) {
   double throwEngage = (400000 / U.distance(V.X, parts.get(0).X, V.Z, parts.get(0).Z)) * U.tick * (V.P.mode == Physics.Mode.fly ? 20 : 1);
   long maxThrow = 750;
   if (V.getsPushed >= 0) {
    V.speedX += Math.abs(V.speedX) < maxThrow ? U.clamp(-maxThrow, U.randomPlusMinus(throwEngage), maxThrow) : 0;
    V.speedX += 2 *
    (V.X < parts.get(0).X && V.speedX < maxThrow ? Math.min(U.random(StrictMath.pow(throwEngage, .75)), maxThrow) :
    V.X > parts.get(0).X && V.speedX > -maxThrow ? -Math.min(U.random(StrictMath.pow(throwEngage, .75)), maxThrow) : 0);
    V.speedZ += Math.abs(V.speedZ) < maxThrow ? U.clamp(-maxThrow, U.randomPlusMinus(throwEngage), maxThrow) : 0;
    V.speedZ += 2 *
    (V.Z < parts.get(0).Z && V.speedZ < maxThrow ? Math.min(U.random(StrictMath.pow(throwEngage, .75)), maxThrow) :
    V.Z > parts.get(0).Z && V.speedZ > -maxThrow ? -Math.min(U.random(StrictMath.pow(throwEngage, .75)), maxThrow) : 0);
   }
   V.speedY += V.getsLifted >= 0 && Math.abs(V.speedY) < maxThrow ? U.clamp(-maxThrow, U.randomPlusMinus(throwEngage), maxThrow) : 0;
   V.P.inTornado = V.getsLifted >= 0;
  }
 }

 public static class Part extends Core {//<-Do NOT weaken access--will fail in 'TrackPart'!

  final Cylinder C;
  Cylinder groundDustC;
  final PhongMaterial PM = new PhongMaterial();
  final PhongMaterial groundDustPM = new PhongMaterial();
  final Core groundDust = new Core();

  Part(double radius, double height) {
   C = new Cylinder(radius, height);
   PM.setSpecularPower(E.Specular.Powers.dull);
   groundDustPM.setSpecularPower(E.Specular.Powers.dull);
   U.setMaterialSecurely(C, PM);
  }

  void run() {
   double depth = U.getDepth(this);
   if (depth > -C.getRadius()) {
    U.randomRotate(C);
    C.setCullFace(depth > C.getRadius() ? CullFace.BACK : CullFace.NONE);
    double color = 1 - U.random(.75);
    Phong.setDiffuseRGB(PM, color);
    U.setTranslate(C, this);
    C.setVisible(true);
   } else {
    C.setVisible(false);
   }
   runGroundDust();
  }

  void runGroundDust() {
   double radius0 = parts.get(0).C.getRadius();
   groundDust.Y = -U.random(radius0);
   double strewXZ = (radius0 + groundDust.Y) * 7.5;
   groundDust.X = parts.get(0).X + U.randomPlusMinus(strewXZ);
   groundDust.Z = parts.get(0).Z + U.randomPlusMinus(strewXZ);
   if (U.render(groundDust, false, false)) {
    U.randomRotate(groundDustC);
    double shade = 1 - U.random(.5);
    double[] mix = {(shade + Ground.RGB.getRed()) * .5, (shade + Ground.RGB.getGreen()) * .5, (shade + Ground.RGB.getBlue()) * .5};
    Phong.setDiffuseRGB(groundDustPM, mix[0], mix[1], mix[2]);
    U.setTranslate(groundDustC, groundDust);
    groundDustC.setVisible(true);
   } else {
    groundDustC.setVisible(false);
   }
  }
 }

 static void reset() {
  parts.clear();
  movesRepairPoints = false;
 }
}
