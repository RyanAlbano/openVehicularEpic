package ve.environment;

import javafx.scene.PointLight;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.utilities.U;

import static ve.environment.E.skyRGB;

public enum Sun {
 ;
 static double X, Y, Z;
 public static final Sphere S = new Sphere(200000000);
 public static final PointLight light = new PointLight();
 static double lightX;
 static double lightY;
 static double lightZ;
 static double RGBVariance = .5;

 static {
  U.Nodes.Light.setRGB(light, 1, 1, 1);
  light.setTranslateX(0);
  light.setTranslateZ(0);
  light.setTranslateY(-Long.MAX_VALUE);
 }

 public static void load(String s) {
  if (s.startsWith("sun(")) {
   lightX = U.getValue(s, 0);
   lightY = U.getValue(s, 2);
   lightZ = U.getValue(s, 1);
   U.Nodes.Light.add(light);
   X = U.getValue(s, 0) * 2;
   Y = U.getValue(s, 2) * 2;
   Z = U.getValue(s, 1) * 2;
   PhongMaterial PM = new PhongMaterial();
   U.Phong.setDiffuseRGB(PM, 1);
   U.Phong.setSpecularRGB(PM, E.Specular.Colors.shiny);
   PM.setSpecularPower(0);
   PM.setSelfIlluminationMap(U.Phong.getSelfIllumination(skyRGB.getRed(), skyRGB.getGreen(), skyRGB.getBlue()));
   U.setMaterialSecurely(S, PM);
   U.Nodes.add(S);
  }
 }
}