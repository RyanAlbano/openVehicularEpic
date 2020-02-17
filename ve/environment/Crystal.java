package ve.environment;

import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.instances.Core;
import ve.trackElements.TE;
import ve.ui.Maps;
import ve.utilities.Nodes;
import ve.utilities.Phong;
import ve.utilities.U;

import java.util.ArrayList;
import java.util.List;

public enum Crystal {
 ;
 public static final List<Instance> instances = new ArrayList<>();

 public static void load(String s) {
  if (s.startsWith("crystals(")) {
   for (int n = 0; n < U.getValue(s, 0); n++) {
    instances.add(new Instance(U.getValue(s, 1), U.getValue(s, 2), U.getValue(s, 3)));
   }
  }
 }

 public static void run() {
  if (!instances.isEmpty()) {
   if (Maps.defaultVehicleLightBrightness > 0) {
    int closest = -1;
    double compareDistance = Double.POSITIVE_INFINITY;
    for (var crystal : instances) {
     Nodes.removePointLight(crystal.light);
     if (U.distance(crystal) < compareDistance) {
      closest = instances.indexOf(crystal);
      compareDistance = U.distance(crystal);
     }
    }
    instances.get(closest).addLight();//<-fixme--'closest' can be -1 (incorrect) for some reason
    for (var crystal : instances) {
     crystal.addLight();
    }
   }
   for (var crystal : instances) {
    crystal.run();
   }
  }
 }

 static class Instance extends Core {

  private final Sphere S;
  PointLight light;
  final PhongMaterial PM = new PhongMaterial();
  final Image selfIllumination;

  Instance(double inX, double inZ, double inY) {
   S = new Sphere(1, 0);
   double sizeVariation = 100;
   S.setScaleX(U.random(sizeVariation));
   S.setScaleY(U.random(sizeVariation));
   S.setScaleZ(U.random(sizeVariation));
   X = inX + U.randomPlusMinus(TE.randomX);
   Z = inZ + U.randomPlusMinus(TE.randomZ);
   Y = Math.min(inY + U.randomPlusMinus(TE.randomY), -sizeVariation);
   E.setTerrainSit(this, false);
   double[] RGB = {U.random(), U.random(), U.random()};
   while (RGB[0] < 1 && RGB[1] < 1 && RGB[2] < 1) {//<-Probably safe--the chance of 3 U.random()s stuck at zero is laughable
    RGB[0] *= 1.0001;
    RGB[1] *= 1.0001;
    RGB[2] *= 1.0001;
   }
   Phong.setDiffuseRGB(PM, RGB[0], RGB[1], RGB[2]);
   Phong.setSpecularRGB(PM, E.Specular.Colors.shiny);
   PM.setSpecularPower(E.Specular.Powers.shiny);
   selfIllumination = Phong.getSelfIllumination(RGB[0], RGB[1], RGB[2]);
   U.setMaterialSecurely(S, PM);
   Nodes.add(S);
   U.randomRotate(S);
   if (Maps.defaultVehicleLightBrightness > 0) {
    light = new PointLight(U.getColor(RGB[0], RGB[1], RGB[2]));
   }
  }

  private void run() {
   if (U.render(this, false, true)) {
    U.setTranslate(S, this);
    if (U.random() < .1) {
     PM.setSelfIlluminationMap(U.random() < .5 ? selfIllumination : null);
    }
    S.setVisible(true);
   } else {
    S.setVisible(false);
   }
  }

  void addLight() {
   if (U.distance(this) < E.viewableMapDistance) {
    U.setTranslate(light, this);
    Nodes.addPointLight(light);
   }
  }
 }
}
