package ve.utilities;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import ve.Sound;
import ve.VE;
import ve.environment.E;
import ve.vehicles.Vehicle;

public class U {//<-The UTILITY Class

 public static double FPS;
 public static double averageFPS;
 public static double FPSTime;
 public static final double sin45 = .70710678118654752440084436210485;
 public static final long shinySpecular = 100;
 public static final long refreshRate = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getRefreshRate();
 public static final java.awt.Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
 private static final String imageFolder = "images";
 private static final String imageExtension = ".png";
 public static final String soundFolder = "sounds";
 public static final String soundExtension = ".au";
 public static final Canvas getRGBCanvas = new Canvas(1, 1);
 public static final GraphicsContext getRGB = getRGBCanvas.getGraphicsContext2D();
 public static final Quaternion inert = new Quaternion();

 public static boolean sameVehicle(Vehicle V1, Vehicle V2) {
  return V1.index == V2.index;
 }

 public static boolean sameTeam(Vehicle vehicle, Vehicle targetVehicle) {
  return sameTeam(vehicle.index, targetVehicle.index);
 }

 public static boolean sameTeam(long index1, long index2) {
  return (index1 < VE.vehiclesInMatch >> 1 && index2 < VE.vehiclesInMatch >> 1) || (index1 >= VE.vehiclesInMatch >> 1 && index2 >= VE.vehiclesInMatch >> 1);
 }

 public static void text(String s, double Y) {
  text(s, .5, Y);
 }

 public static void text(String s, double X, double Y) {
  VE.graphicsContext.setTextAlign(TextAlignment.CENTER);
  VE.graphicsContext.fillText(s, VE.width * X, VE.height * Y);
 }

 public static void textR(String s, double X, double Y) {
  VE.graphicsContext.setTextAlign(TextAlignment.RIGHT);
  VE.graphicsContext.fillText(s, VE.width * X, VE.height * Y);
 }

 public static void textL(String s, double X, double Y) {
  VE.graphicsContext.setTextAlign(TextAlignment.LEFT);
  VE.graphicsContext.fillText(s, VE.width * X, VE.height * Y);
 }

 public static void fillRGB(double R, double G, double B) {
  VE.graphicsContext.setFill(Color.color(U.clamp(R), U.clamp(G), U.clamp(B)));
 }

 public static void fillRGB(double R, double G, double B, double transparency) {
  VE.graphicsContext.setFill(Color.color(U.clamp(R), U.clamp(G), U.clamp(B), U.clamp(transparency)));
 }

 public static void fillRGB(Color C) {
  VE.graphicsContext.setFill(C);
 }

 public static void strokeRGB(double R, double G, double B) {
  VE.graphicsContext.setStroke(Color.color(U.clamp(R), U.clamp(G), U.clamp(B)));
 }

 public static void fillRectangle(double x, double y, double rectangleWidth, double rectangleHeight) {
  double width = rectangleWidth * VE.width, height = rectangleHeight * VE.height;
  VE.graphicsContext.fillRect((VE.width * x) - (width * .5), (VE.height * y) - (height * .5), width, height);
 }

 public static void drawRectangle(double x, double y, double rectangleWidth, double rectangleHeight) {
  double width = Math.abs(rectangleWidth * VE.width), height = Math.abs(rectangleHeight * VE.height);
  VE.graphicsContext.strokeRect((VE.width * x) - (width * .5), (VE.height * y) - (height * .5), width, height);
 }

 public static void font(double size) {
  VE.graphicsContext.setFont(new Font("Arial Black", size * VE.width));
 }

 public static String readInLAN(int n) {
  String s = "";
  try {
   s = VE.inLAN.get(n).ready() ? VE.inLAN.get(n).readLine() : s;
  } catch (IOException e) {
   e.printStackTrace();
  }
  return s;
 }

 public static FileInputStream getMapFile(int n) {
  FileInputStream FIS = null;
  try {
   try {
    try {
     FIS = new FileInputStream("maps" + File.separator + VE.maps.get(n));
    } catch (FileNotFoundException e) {
     FIS = new FileInputStream("maps" + File.separator + "User-Submitted" + File.separator + VE.maps.get(n));
    }
   } catch (Exception e) {//<-do NOT change
    VE.map = 0;
    FIS = new FileInputStream("maps" + File.separator + "basic");
   }
  } catch (FileNotFoundException ignored) {
  }
  return FIS;
 }

 List<Double> arrayToList(double[] source) {
  List<Double> d = new ArrayList<>();
  for (double v : source) {
   d.add(v);
  }
  return d;
 }

 public static void add(Node N) {
  if (N != null && !VE.group.getChildren().contains(N)) {
   VE.group.getChildren().add(N);
  }
 }

 public static void add(Node... N) {
  for (Node n : N) {
   if (n != null && !VE.group.getChildren().contains(n)) {
    VE.group.getChildren().add(n);
   }
  }
 }

 public static void addLight(Node N) {
  if (N != null && !VE.group.getChildren().contains(N) && VE.lightsAdded < 3) {
   VE.group.getChildren().add(N);
   VE.lightsAdded++;
  }
 }

 public static void remove(Node N) {
  if (N != null) {
   VE.group.getChildren().remove(N);
  }
 }

 public static void remove(Node... N) {
  for (Node n : N) {
   if (n != null) {
    VE.group.getChildren().remove(n);
   }
  }
 }

 public static String getString(String s, int index) {
  try {
   return s.split("[(,)]")[index + 1];
  } catch (Exception e) {
   return "";
  }
 }

 public static double getValue(String s, int index) {
  return Double.parseDouble(s.split("[(,)]")[index + 1]);
 }

 public static double distance(double X1, double X2, double Z1, double Z2) {
  return netValue(X1 - X2, Z1 - Z2);
 }

 public static double distance(double X1, double X2, double Y1, double Y2, double Z1, double Z2) {
  return netValue(X1 - X2, Y1 - Y2, Z1 - Z2);
 }

 public static boolean outOfBounds(double x, double y, double z, double tolerance) {
  double depth = E.groundLevel + (E.poolExists && U.distance(x, E.poolX, z, E.poolZ) < E.pool[0].getRadius() ? E.poolDepth : 0);
  return y > depth || x > VE.limitR + tolerance || x < VE.limitL - tolerance || z > VE.limitFront + tolerance || z < VE.limitBack - tolerance || y < VE.limitY - tolerance;
 }

 public static void setDiffuseRGB(PhongMaterial PM, double R, double G, double B) {
  PM.setDiffuseColor(Color.color(U.clamp(R), U.clamp(G), U.clamp(B)));
 }

 public static void setDiffuseRGB(PhongMaterial PM, Color C) {
  PM.setDiffuseColor(C);
 }

 public static void setDiffuseRGB(PhongMaterial PM, double R, double G, double B, double transparency) {
  PM.setDiffuseColor(Color.color(U.clamp(R), U.clamp(G), U.clamp(B), U.clamp(transparency)));
 }

 public static void setSpecularRGB(PhongMaterial PM, double R, double G, double B) {
  PM.setSpecularColor(Color.color(U.clamp(R), U.clamp(G), U.clamp(B)));
 }

 public static void setSpecularRGB(PhongMaterial PM, double R, double G, double B, double transparency) {
  PM.setSpecularColor(Color.color(U.clamp(R), U.clamp(G), U.clamp(B), U.clamp(transparency)));
 }

 public static void setLightRGB(AmbientLight AL, double R, double G, double B) {
  AL.setColor(Color.color(U.clamp(R), U.clamp(G), U.clamp(B)));
 }

 public static void setLightRGB(PointLight PL, double R, double G, double B) {
  PL.setColor(Color.color(U.clamp(R), U.clamp(G), U.clamp(B)));
 }

 public static void loadImage(Map<String, Image> M, String s) {
  if (!M.containsKey(s)) {
   try {
    M.put(s, new Image(new FileInputStream(imageFolder + File.separator + s + imageExtension)));
   } catch (FileNotFoundException e) {
    System.out.println("Image loading exception: " + e);
   }
  }
 }

 public static void loadImage(Map<String, Image> M, String s, double quantity) {
  for (int n = 0; n < quantity; n++) {
   try {
    U.loadImage(M, s + n);
   } catch (Exception e) {
    if (quantity < Double.POSITIVE_INFINITY) {
     System.out.println("Image loading exception: " + e);
    }
    break;
   }
  }
 }

 public static Image getImage(String s) {
  return VE.images.getOrDefault(s, null);
 }

 public static Image getImageNormal(String s) {
  return VE.normalMapping ? getImage(s + "N") : null;
 }

 public static void loadSound(Map<String, Sound> M, String s) {
  if (!M.containsKey(s)) {
   try {
    M.put(s, new Sound(s));
   } catch (Exception E) {
    System.out.println("Sound loading exception: " + E);
   }
  }
 }

 public static void loadSound(Map<String, Sound> M, String keyName, String fileName) {
  if (!M.containsKey(keyName)) {
   try {
    M.put(keyName, new Sound(fileName));
   } catch (Exception E) {
    System.out.println("Sound loading exception: " + E);
   }
  }
 }

 public static void loadSound(Map<String, Sound> M, String s, double quantity) {
  for (int n = 0; n < quantity; n++) {
   try {
    if (!M.containsKey(s + n)) {
     M.put(s + n, new Sound(s + n));
    }
   } catch (Exception E) {
    if (quantity < Double.POSITIVE_INFINITY) {
     System.out.println("Sound loading exception: " + E);
    }
    break;
   }
  }
 }

 public static void soundPlay(Map<String, Sound> M, String s, double gain) {
  if (M.containsKey(s)) {
   M.get(s).play(gain);
  }
 }

 public static void soundPlayIfNotPlaying(Map<String, Sound> M, String s, double gain) {
  if (M.containsKey(s)) {
   M.get(s).playIfNotPlaying(gain);
  }
 }

 public static void soundLoop(Map<String, Sound> M, String s, double gain) {
  if (M.containsKey(s)) {
   M.get(s).loop(gain);
  }
 }

 public static void soundResume(Map<String, Sound> M, String s, double gain) {
  if (M.containsKey(s)) {
   M.get(s).resume(gain);
  }
 }

 public static void soundStop(Map<String, Sound> M, String s) {
  if (M.containsKey(s)) {
   M.get(s).stop();
  }
 }

 public static boolean soundRunning(Map<String, Sound> M, String s) {
  return M.containsKey(s) && M.get(s).running();
 }

 public static void soundClose(Map<String, Sound> M, String s) {
  if (M.containsKey(s)) {
   M.get(s).close();
   M.remove(s);
  }
 }

 public static void soundClose(Map<String, Sound> M, String s, int quantity) {
  for (int n = quantity; --n >= 0; ) {
   soundClose(M, s + n);
  }
 }

 public static boolean listEquals(String in, String... prefixes) {
  for (String s : prefixes) {
   if (in.equals(s)) {
    return true;
   }
  }
  return false;
 }

 public static boolean startsWith(String in, String... prefixes) {
  for (String s : prefixes) {
   if (in.startsWith(s)) {
    return true;
   }
  }
  return false;
 }

 public static boolean contains(String in, String... prefixes) {
  for (String s : prefixes) {
   if (in.contains(s)) {
    return true;
   }
  }
  return false;
 }

 public static double[] listToArray(List<Double> source) {
  if (source.size() > 0) {
   double[] d = new double[source.size()];
   for (int i = d.length; --i >= 0; ) {
    d[i] = source.get(i);
   }
   return d;
  } else {
   return new double[source.size()];
  }
 }

 public static long randomize(long value, int length) {
  if (length > 1) {
   long lastValue = value;
   while (value == lastValue) {
    value = random(length);
   }
  }
  return value;
 }

 public static int random(int randomInt) {
  return randomInt > 0 ? ThreadLocalRandom.current().nextInt(randomInt) : randomInt;
 }

 public static long random(long randomLong) {
  return randomLong > 0 ? ThreadLocalRandom.current().nextLong(randomLong) : randomLong;
 }

 public static double random(double randomDouble) {
  return randomDouble * random();
 }

 public static double random() {
  return ThreadLocalRandom.current().nextDouble();
 }

 public static double randomPlusMinus(double randomDouble) {
  return randomDouble * (random() - random());
 }

 public static double sin(double in) {
  for (; in > 180; in -= 360) ;
  for (; in < -180; in += 360) ;
  return Math.sin(in * .01745329251994329576923690768489);
 }

 public static double cos(double in) {
  for (; in > 180; in -= 360) ;
  for (; in < -180; in += 360) ;
  return Math.cos(in * .01745329251994329576923690768489);
 }

 private static double arcCos(double in) {
  return Math.acos(in);
 }

 public static double arcTan(double in) {
  return Math.atan(in) * 57.295779513082320876798154814092;
 }

 public static void rotate(Node n, double XY, double YZ, double XZ) {
  double cosXY = U.cos(XY), cosYZ = U.cos(YZ), cosXZ = U.cos(XZ);
  XY = U.sin(XY);
  YZ = U.sin(YZ);
  XZ = U.sin(XZ);
  double d = U.arcCos(((cosXY * cosXZ) + (cosXY * cosYZ - XY * YZ * XZ) + (cosYZ * cosXZ) - 1.) * .5);
  if (d != 0) {//<-do NOT Remove!
   double den = 1 / (2 * Math.sin(d));
   n.setRotationAxis(new Point3D(((-cosXZ * YZ) - (cosXY * YZ + cosYZ * XY * XZ)) * den, ((XY * YZ - cosXY * cosYZ * XZ) - XZ) * den, ((-cosXZ * XY) - (cosYZ * XY + cosXY * YZ * XZ)) * den));
   n.setRotate(Math.toDegrees(d));
  } else {
   n.setRotationAxis(new Point3D(0, 0, 0));
   n.setRotate(0);
  }
 }

 public static void rotate(Node n, double YZ, double XZ) {
  double cosXZ = U.cos(XZ), sinYZ = U.sin(YZ), sinXZ = U.sin(XZ), cosYZ = U.cos(YZ),
  d = U.arcCos((cosXZ + cosYZ + (cosYZ * cosXZ) - 1.) * .5),
  den = 1 / (2 * Math.sin(d));
  n.setRotationAxis(new Point3D(((-cosXZ * sinYZ) - sinYZ) * den, (-(cosYZ * sinXZ) - sinXZ) * den, -(sinYZ * sinXZ) * den));
  n.setRotate(Math.toDegrees(d));
 }

 public static void randomRotate(Node N) {
  N.setRotationAxis(new Point3D(U.random(), U.random(), U.random()));
  N.setRotate(U.random(360.));
 }

 public static double[] rotate(double a, double b, double o1, double o2, double axs) {
  return new double[]{o1 + ((a - o1) * U.cos(axs) - (b - o2) * U.sin(axs)), o2 + ((a - o1) * U.sin(axs) + (b - o2) * U.cos(axs))};
 }

 public static void rotate(double[] a, double[] b, double axis) {
  double a1 = a[0];
  double b1 = b[0];
  a[0] = a1 * U.cos(axis) - b1 * U.sin(axis);
  b[0] = a1 * U.sin(axis) + b1 * U.cos(axis);
 }

 public static double clamp(double d) {
  return clamp(0, d, 1);
 }

 public static double clamp(double minimum, double d, double maximum) {
  return Math.max(minimum, Math.min(d, maximum));
 }

 public static long clamp(long minimum, long d, long maximum) {
  return Math.max(minimum, Math.min(d, maximum));
 }

 public static double netValue(double v1, double v2) {
  return Math.sqrt(v1 * v1 + v2 * v2);
 }

 public static double netValue(double v1, double v2, double v3) {
  return Math.sqrt(v1 * v1 + v2 * v2 + v3 * v3);
 }

 public static void setTranslate(Node N, double X, double Y, double Z) {
  N.setTranslateX(X - VE.cameraX);
  N.setTranslateY(Y - VE.cameraY);
  N.setTranslateZ(Z - VE.cameraZ);
 }

 public static void setScale(Node N, double size) {
  N.setScaleX(size);
  N.setScaleY(size);
  N.setScaleZ(size);
 }

 public static double getDepth(double X, double Y, double Z) {
  return (Y - VE.cameraY) * U.sin(VE.cameraYZ) + ((X - VE.cameraX) * U.sin(VE.cameraXZ) + (Z - VE.cameraZ) * U.cos(VE.cameraXZ)) * U.cos(VE.cameraYZ);
 }

 public static void render(Node N, double X, double Y, double Z) {
  render(N, X, Y, Z, 0);
 }

 public static void render(Node N, double X, double Y, double Z, double depthTolerance) {
  if (getDepth(X, Y, Z) > depthTolerance) {
   setTranslate(N, X, Y, Z);
   N.setVisible(true);
  } else {
   N.setVisible(false);
  }
 }

 public static void getFPS() {
  double time = System.currentTimeMillis();
  FPS = Math.min(960.25 / (time - FPSTime) + 1.25, refreshRate);//<-Flat algorithm not as accurate
  averageFPS += (FPS - averageFPS) * .03125;
  FPSTime = time;
 }

 /*public static void cache(Node N) {
  N.setCache(true);
  N.setCacheHint(CacheHint.QUALITY);
 }*/

 public static void zZz(double sleep) {
  try {
   Thread.sleep(Math.round(sleep));
  } catch (InterruptedException e) {
   e.printStackTrace();
  }
 }
}
