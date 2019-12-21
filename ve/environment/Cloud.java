package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;

import ve.Core;
import ve.VE;
import ve.utilities.SL;
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
   wrapDistance = VE.Map.name.equals("Ethereal Mist") ? 100000 :
   U.equals(VE.Map.name, "the Test of Endurance", "an Immense Relevance", SL.Maps.summitOfEpic) ? 10000000 :
   1000000;
   U.Phong.setDiffuseRGB(PM, U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
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
   S = new Sphere(1000 + U.random(4000));
   X = U.randomPlusMinus(wrapDistance);
   Z = U.randomPlusMinus(wrapDistance);
   Y = globalHeight - U.randomPlusMinus(globalHeight * .5);
   S.setScaleX(8 + U.random(8.));
   S.setScaleY(1 + U.random());
   S.setScaleZ(8 + U.random(8.));
   U.setMaterialSecurely(S, PM);
   U.rotate(S, 0, U.random(360.));
   U.Nodes.add(S);
  }

  private void run() {
   if (E.Wind.maxPotency > 0) {
    X += E.Wind.speedX * VE.tick + (wrapDistance * (X < -wrapDistance ? 2 : X > wrapDistance ? -2 : 0));
    Z += E.Wind.speedZ * VE.tick + (wrapDistance * (Z < -wrapDistance ? 2 : Z > wrapDistance ? -2 : 0));
   }
   double size = S.getRadius() * Math.max(S.getScaleX(), Math.max(S.getScaleY(), S.getScaleZ()));
   if (U.render(this, -size)) {
    S.setCullFace(U.getDepth(this) > size ? CullFace.BACK : CullFace.NONE);
    U.setTranslate(S, this);
    S.setVisible(true);
   } else {
    S.setVisible(false);
   }
  }
 }
}