package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.Core;
import ve.utilities.SL;
import ve.utilities.U;

import java.util.ArrayList;
import java.util.Collection;

public enum Star {
 ;
 private static final PhongMaterial PM = new PhongMaterial(null, null, null, null, U.Images.get(SL.Images.white));
 public static final Collection<Instance> instances = new ArrayList<>();

 public static void load(String s) {
  if (s.startsWith("stars(")) {
   for (int n = 0; n < U.getValue(s, 0); n++) {
    instances.add(new Instance());
   }
  }
 }

 public static void run() {
  for (Star.Instance star : instances) {
   star.run();
  }
 }

 static class Instance extends Core {

  private final Sphere S;

  Instance() {
   S = new Sphere(200000000, 5);
   double distance = 40000000000.;
   double[] rotateX = {distance + U.random(distance * 4.)}, rotateY = {distance + U.random(distance * 4.)}, rotateZ = {distance + U.random(distance * 4.)};
   U.rotate(rotateX, rotateZ, U.random(360.));
   U.rotate(rotateX, rotateY, U.random(360.));
   U.rotate(rotateZ, rotateY, U.random(360.));
   X = rotateX[0];
   Z = rotateZ[0];
   Y = E.Ground.level <= 0 ? -Math.abs(rotateY[0]) : rotateY[0];
   U.setMaterialSecurely(S, PM);
   U.Nodes.add(S);
  }

  private void run() {
   if (U.render(this, -S.getRadius())) {
    U.setTranslate(S, this);
    S.setVisible(true);
   } else {
    S.setVisible(false);
   }
  }
 }
}
