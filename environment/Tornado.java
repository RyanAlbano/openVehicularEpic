package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import ve.Camera;
import ve.Core;
import ve.Sound;
import ve.VE;
import ve.utilities.U;
import ve.vehicles.Physics;
import ve.vehicles.Vehicle;
import ve.vehicles.Wheel;

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
    U.setMaterialSecurely(parts.get(n).C, new PhongMaterial());
    size *= U.getValue(s, 2);
    parts.get(n).Y = U.getValue(s, 1) * n / 40.;
    U.Nodes.add(parts.get(n).C);
    parts.get(n).groundDust = new Cylinder(
    (n + 1) * U.getValue(s, 0) * .01,
    (n + 1) * U.getValue(s, 0) * .01);
    U.Nodes.add(parts.get(n).groundDust);
   }
   maxTravelDistance = U.getValue(s, 3);
   movesRepairPoints = s.contains("moveRepairPoints");
  }
 }

 static void run(boolean update) {
  if (!parts.isEmpty()) {
   if (E.Wind.maxPotency > 0 && update) {
    parts.get(0).X += E.Wind.speedX * VE.tick;
    parts.get(0).Z += E.Wind.speedZ * VE.tick;
   }
   if (U.distance(0, parts.get(0).X, 0, parts.get(0).Z) > maxTravelDistance) {
    parts.get(0).X *= .999;
    parts.get(0).Z *= .999;
    E.Wind.speedX *= -1;
    E.Wind.speedZ *= -1;
   }
   for (int n = 1; n < parts.size(); n++) {
    parts.get(n).X = (parts.get(n - 1).X + parts.get(n).X) * .5;
    parts.get(n).Z = (parts.get(n - 1).Z + parts.get(n).Z) * .5;
   }
   for (Tornado.Part tornadoPart : parts) {
    tornadoPart.run();
   }
   if (!VE.Match.muteSound && update) {
    sound.loop(Math.sqrt(U.distance(Camera.X, parts.get(0).X, Camera.Y, 0, Camera.Z, parts.get(0).Z)) * .08);
   } else {
    sound.stop();
   }
  }
 }

 public static void vehicleInteract(Vehicle V) {
  if (!parts.isEmpty() && V.Y > parts.get(parts.size() - 1).Y && U.distance(V.X, parts.get(0).X, V.Z, parts.get(0).Z) < parts.get(0).C.getRadius() * 7.5) {
   double tornadoThrowEngage = (400000 / U.distance(V.X, parts.get(0).X, V.Z, parts.get(0).Z)) * VE.tick * (V.P.mode == Physics.Mode.fly ? 20 : 1);
   long maxThrow = 750;
   for (Wheel wheel : V.wheels) {
    if (V.getsPushed >= 0) {
     wheel.speedX += Math.abs(wheel.speedX) < maxThrow ? U.clamp(-maxThrow, U.randomPlusMinus(tornadoThrowEngage), maxThrow) : 0;
     wheel.speedX += 2 * (V.X < parts.get(0).X && wheel.speedX < maxThrow ? Math.min(U.random(StrictMath.pow(tornadoThrowEngage, .75)), maxThrow) : V.X > parts.get(0).X && wheel.speedX > -maxThrow ? -Math.min(U.random(StrictMath.pow(tornadoThrowEngage, .75)), maxThrow) : 0);
     wheel.speedZ += Math.abs(wheel.speedZ) < maxThrow ? U.clamp(-maxThrow, U.randomPlusMinus(tornadoThrowEngage), maxThrow) : 0;
     wheel.speedZ += 2 * (V.Z < parts.get(0).Z && wheel.speedZ < maxThrow ? Math.min(U.random(StrictMath.pow(tornadoThrowEngage, .75)), maxThrow) : V.Z > parts.get(0).Z && wheel.speedZ > -maxThrow ? -Math.min(U.random(StrictMath.pow(tornadoThrowEngage, .75)), maxThrow) : 0);
    }
    wheel.speedY += V.getsLifted >= 0 && Math.abs(wheel.speedY) < maxThrow ? U.clamp(-maxThrow, U.randomPlusMinus(tornadoThrowEngage), maxThrow) : 0;
   }
   V.P.inTornado = V.getsLifted >= 0;
  }
 }

 public static class Part extends Core {//<-Do NOT weaken access--will fail in 'TrackPart'!

  final Cylinder C;
  Cylinder groundDust;

  Part(double radius, double height) {
   C = new Cylinder(radius, height);
  }

  void run() {
   double depth = U.getDepth(this), radius0 = parts.get(0).C.getRadius(),
   groundDustY = -U.random(radius0),
   dustLocation = (radius0 + groundDustY) * 7.5,
   groundDustX = parts.get(0).X + U.randomPlusMinus(dustLocation),
   groundDustZ = parts.get(0).Z + U.randomPlusMinus(dustLocation);
   if (depth > -C.getRadius()) {
    U.randomRotate(C);
    C.setCullFace(depth > C.getRadius() ? CullFace.BACK : CullFace.NONE);
    double color = 1 - U.random(.75);
    U.Phong.setDiffuseRGB((PhongMaterial) C.getMaterial(), color);
    U.setTranslate(C, this);
    C.setVisible(true);
   } else {
    C.setVisible(false);
   }
   if (U.render(groundDustX, groundDustY, groundDustZ)) {
    U.randomRotate(groundDust);
    PhongMaterial PM = new PhongMaterial();
    double shade = 1 - U.random(.5);
    double[] mix = {(shade + E.Ground.RGB.getRed()) * .5, (shade + E.Ground.RGB.getGreen()) * .5, (shade + E.Ground.RGB.getBlue()) * .5};
    U.Phong.setDiffuseRGB(PM, mix[0], mix[1], mix[2]);
    U.setMaterialSecurely(groundDust, PM);
    U.setTranslate(groundDust, groundDustX, groundDustY, groundDustZ);
    groundDust.setVisible(true);
   } else {
    groundDust.setVisible(false);
   }
  }
 }
}
