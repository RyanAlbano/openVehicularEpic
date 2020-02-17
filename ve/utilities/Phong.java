package ve.utilities;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

public enum Phong {
 ;
 private static final Canvas colorGetterCanvas = new Canvas(1, 1);
 private static final GraphicsContext colorGetter = colorGetterCanvas.getGraphicsContext2D();

 public static void setDiffuseRGB(PhongMaterial PM, Color C) {
  PM.setDiffuseColor(C);
 }

 public static void setDiffuseRGB(PhongMaterial PM, Color C, double transparency) {
  setDiffuseRGB(PM, C.getRed(), C.getGreen(), C.getBlue(), transparency);
 }

 public static void setDiffuseRGB(PhongMaterial PM, double shade) {
  setDiffuseRGB(PM, shade, shade, shade);
 }

 public static void setDiffuseRGB(PhongMaterial PM, double R, double G, double B) {
  PM.setDiffuseColor(Color.color(U.clamp(R), U.clamp(G), U.clamp(B)));
 }

 public static void setDiffuseRGB(PhongMaterial PM, double R, double G, double B, double transparency) {
  PM.setDiffuseColor(Color.color(U.clamp(R), U.clamp(G), U.clamp(B), U.clamp(transparency)));
 }

 public static void setSpecularRGB(PhongMaterial PM, Color C) {
  PM.setSpecularColor(C);
 }

 public static void setSpecularRGB(PhongMaterial PM, double shade) {
  setSpecularRGB(PM, shade, shade, shade);
 }

 public static void setSpecularRGB(PhongMaterial PM, double R, double G, double B) {
  PM.setSpecularColor(Color.color(U.clamp(R), U.clamp(G), U.clamp(B)));
 }

 public static void setSpecularRGB(PhongMaterial PM, double R, double G, double B, double transparency) {//<-Keep method in case we need it later
  PM.setSpecularColor(Color.color(U.clamp(R), U.clamp(G), U.clamp(B), U.clamp(transparency)));
 }

 public static Image getSelfIllumination(Color C) {
  return getSelfIllumination(C.getRed(), C.getGreen(), C.getBlue());
 }

 public static Image getSelfIllumination(double shade) {
  return getSelfIllumination(shade, shade, shade);
 }

 public static Image getSelfIllumination(double R, double G, double B) {
  colorGetter.setFill(Color.color(U.clamp(R), U.clamp(G), U.clamp(B)));
  colorGetter.fillRect(0, 0, 1, 1);
  return colorGetterCanvas.snapshot(null, null);
 }

  /*public static PhongMaterial copy(PhongMaterial in) {//In case it's needed again
  PhongMaterial PM = new PhongMaterial();
  PM.setDiffuseColor(in.getDiffuseColor());
  PM.setDiffuseMap(in.getDiffuseMap());
  PM.setSpecularColor(in.getSpecularColor());
  PM.setSpecularPower(in.getSpecularPower());
  PM.setSpecularMap(in.getSpecularMap());
  PM.setBumpMap(in.getBumpMap());
  return PM;
 }*/
}