package ve.environment;

import javafx.scene.PointLight;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.instances.Core;
import ve.utilities.Nodes;
import ve.utilities.Phong;
import ve.utilities.U;

public enum Sun {
 ;
 public static final Core C = new Core();
 public static final Sphere S = new Sphere(200000000);
 public static final PointLight light = new PointLight();
 static double lightX;
 static double lightY;
 static double lightZ;
 static double RGBVariance = .5;

 enum Type {none, sun, crescent}

 static Type type = Type.none;

 static {
  Nodes.setLightRGB(light, 1, 1, 1);
  light.setTranslateX(0);
  light.setTranslateZ(0);
  light.setTranslateY(-Long.MAX_VALUE);
 }

 public static void load(String s) {
  if (s.startsWith("sun(")) {
   type = Type.sun;
   lightX = U.getValue(s, 0);
   lightY = U.getValue(s, 2);
   lightZ = U.getValue(s, 1);
   Nodes.addPointLight(light);
   C.X = U.getValue(s, 0) * 2;
   C.Y = U.getValue(s, 2) * 2;
   C.Z = U.getValue(s, 1) * 2;
   PhongMaterial PM = new PhongMaterial();
   Phong.setDiffuseRGB(PM, 1);
   Phong.setSpecularRGB(PM, E.Specular.Colors.shiny);
   PM.setSpecularPower(0);
   PM.setSelfIlluminationMap(Phong.getSelfIllumination(E.skyRGB.getRed(), E.skyRGB.getGreen(), E.skyRGB.getBlue()));
   U.setMaterialSecurely(S, PM);
   Nodes.add(S);
  }
 }

 public static void enforceCrescent() {
  Nodes.remove(S);
  type = Type.crescent;
 }

 static void reset() {
  C.X = C.Y = C.Z = 0;
  Nodes.remove(S);
  type = Type.none;
  Nodes.removePointLight(light);
  Nodes.setLightRGB(light, 1, 1, 1);
 }
}