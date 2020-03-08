package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;

import ve.instances.Core;
import ve.ui.Maps;
import ve.utilities.Nodes;
import ve.utilities.Phong;
import ve.utilities.D;
import ve.utilities.U;

import java.util.ArrayList;
import java.util.Collection;

public enum Cloud {
 ;
 private static final PhongMaterial PM = new PhongMaterial();
 private static double wrapDistance;
 static final Collection<Instance> instances = new ArrayList<>();
 private static double globalHeight;

 static {
  PM.setSpecularPower(E.Specular.Powers.dull);
 }

 public static void load(String s) {
  if (s.startsWith("clouds(")) {
   globalHeight = U.getValue(s, 3);
   wrapDistance = U.equals(Maps.name, "the Test of Endurance", "an Immense Relevance", D.Maps.summitOfEpic) ? 10000000 : 1000000;
   Phong.setDiffuseRGB(PM, U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
   for (int n = 0; n < U.random(120); n++) {
    instances.add(new Instance());
   }
  }
 }

 public static void run() {
  for (Instance cloud : instances) {
   cloud.run();
  }
 }

 static class Instance extends Core {
  private final Sphere S;

  Instance() {
   S = new Sphere();
   X = U.randomPlusMinus(wrapDistance);
   Z = U.randomPlusMinus(wrapDistance);
   Y = globalHeight - U.randomPlusMinus(globalHeight * .5);
   double sizeRandom = 1000 + U.random(4000.);
   S.setScaleX(sizeRandom * (8 + U.random(8.)));
   S.setScaleY(sizeRandom * (1 + U.random()));
   S.setScaleZ(sizeRandom * (8 + U.random(8.)));
   absoluteRadius = Math.max(S.getScaleX(), Math.max(S.getScaleY(), S.getScaleZ()));
   U.setMaterialSecurely(S, PM);
   U.rotate(S, 0, U.random(360.));
   Nodes.add(S);
  }

  private void run() {
   if (Wind.maxPotency > 0) {
    X += Wind.speedX * U.tick + (wrapDistance * (X < -wrapDistance ? 2 : X > wrapDistance ? -2 : 0));
    Z += Wind.speedZ * U.tick + (wrapDistance * (Z < -wrapDistance ? 2 : Z > wrapDistance ? -2 : 0));
   }

   if (U.render(this, -absoluteRadius, false, false)) {
    S.setCullFace(U.getDepth(this) > absoluteRadius ? CullFace.BACK : CullFace.NONE);
    U.setTranslate(S, this);
    S.setVisible(true);
   } else {
    S.setVisible(false);
   }
  }
 }
}