package ve.utilities;

import java.awt.*;
import java.io.*;
import java.nio.charset.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape3D;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import ve.*;
import ve.Camera;
import ve.environment.E;
import ve.vehicles.Vehicle;

public enum U {//Utilities
 ;

 public static final String lineSeparator = System.lineSeparator();
 public static final Pattern regex = Pattern.compile("[(,)]");
 public static double FPS, averageFPS;
 public static double FPSTime;
 public static final double sin45 = .70710678118654752440084436210485;
 public static final double minimumAccurateLayeredOpacity = .05;
 public static final long refreshRate = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getRefreshRate();
 public static final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
 public static final String imageFolder = "images", soundFolder = "sounds", soundExtension = ".au",
 modelFolder = "models";
 private static final String mapFolder = "maps";
 public static final String userSubmittedFolder = "User-Submitted";
 private static final String imageExtension = ".png";
 public static final String JLayerStuff = "JLayerStuff";
 public static final Charset standardChars = StandardCharsets.UTF_8;
 private static final String imageLoadingException = "Image-loading Exception: ";
 public static final String soundLoadingException = "Sound-loading Exception: ";
 public static final String modelLoadingError = "Model-Loading Error: ";
 public static String debug = "Code executed to this location";
 private static final Canvas colorGetterCanvas = new Canvas(1, 1);
 private static final GraphicsContext colorGetter = colorGetterCanvas.getGraphicsContext2D();
 public static final DecimalFormat DF = new DecimalFormat("0.#E0");
 public static final Quaternion inert = new Quaternion();

 public static String getString(CharSequence s, int index) {
  try {
   return regex.split(s)[index + 1];
  } catch (RuntimeException e) {
   return "";//Catch is needed!
  }
 }

 public static double getValue(CharSequence s, int index) {
  return Double.parseDouble(regex.split(s)[index + 1]);
 }

 public static boolean sameVehicle(Vehicle V1, Vehicle V2) {
  return V1.index == V2.index;
 }

 public static boolean sameTeam(Vehicle vehicle, Vehicle targetVehicle) {
  return sameTeam(vehicle.index, targetVehicle.index);
 }

 public static boolean sameTeam(long index1, long index2) {
  long halfOfPlayers = VE.vehiclesInMatch >> 1;
  return index1 < halfOfPlayers == index2 < halfOfPlayers;
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

 public static void fillRGB(Color C) {
  VE.graphicsContext.setFill(C);
 }

 public static void fillRGB(double shade) {
  shade = clamp(shade);
  VE.graphicsContext.setFill(Color.color(shade, shade, shade));
 }

 public static void fillRGB(double R, double G, double B) {
  VE.graphicsContext.setFill(Color.color(clamp(R), clamp(G), clamp(B)));
 }

 public static void fillRGB(double R, double G, double B, double transparency) {
  fillRGB(VE.graphicsContext, R, G, B, transparency);
 }

 public static void fillRGB(GraphicsContext GC, double R, double G, double B, double transparency) {
  GC.setFill(Color.color(clamp(R), clamp(G), clamp(B), clamp(transparency)));
 }

 public static void strokeRGB(double shade) {
  shade = clamp(shade);
  VE.graphicsContext.setStroke(Color.color(shade, shade, shade));
 }

 public static void strokeRGB(double R, double G, double B) {
  VE.graphicsContext.setStroke(Color.color(clamp(R), clamp(G), clamp(B)));
 }

 public static void fillRectangle(double X, double Y, double rectangleWidth, double rectangleHeight) {
  fillRectangle(VE.graphicsContext, X, Y, rectangleWidth, rectangleHeight);
 }

 public static void fillRectangle(GraphicsContext GC, double X, double Y, double rectangleWidth, double rectangleHeight) {
  double width = rectangleWidth * VE.width, height = rectangleHeight * VE.height;
  GC.fillRect((VE.width * X) - (width * .5), (VE.height * Y) - (height * .5), width, height);
 }

 public static void drawRectangle(double X, double Y, double rectangleWidth, double rectangleHeight) {
  double width = Math.abs(rectangleWidth * VE.width), height = Math.abs(rectangleHeight * VE.height);
  VE.graphicsContext.strokeRect((VE.width * X) - (width * .5), (VE.height * Y) - (height * .5), width, height);
 }

 public static void font(double size) {
  VE.graphicsContext.setFont(new Font("Arial Black", size * VE.width));
 }

 public static FileInputStream getMapFile(int n) {
  File F = new File(mapFolder + File.separator + VE.maps.get(n));
  if (!F.exists()) {
   F = new File(mapFolder + File.separator + userSubmittedFolder + File.separator + VE.maps.get(n));
  }
  if (!F.exists()) {
   VE.map = 0;
   F = new File(mapFolder + File.separator + VE.maps.get(0));
  }
  try {
   return new FileInputStream(F);
  } catch (FileNotFoundException E) {
   return null;
  }
 }

 public enum Nodes {
  ;

  public static void add(Node... N) {
   VE.denyExpensiveInGameCall();
   for (Node n : N) {
    if (n != null && !VE.group.getChildren().contains(n)) {
     VE.group.getChildren().add(n);
    }
   }
  }

  public static void remove(Node... N) {
   VE.denyExpensiveInGameCall();
   for (Node n : N) {
    if (n != null) {
     VE.group.getChildren().remove(n);
    }
   }
  }

  public enum Light {
   ;

   public static void add(Node N) {//Find way to throw exception if node is not a light
    if (N != null && !E.lights.getChildren().contains(N) && E.lightsAdded < 3) {
     E.lights.getChildren().add(N);
     E.lightsAdded++;
    }
   }

   public static void remove(Node N) {
    if (N != null) {
     E.lights.getChildren().remove(N);
    }
   }

   public static void setRGB(AmbientLight AL, double R, double G, double B) {
    AL.setColor(Color.color(clamp(R), clamp(G), clamp(B)));
   }

   public static void setRGB(PointLight PL, double R, double G, double B) {
    PL.setColor(Color.color(clamp(R), clamp(G), clamp(B)));
   }
  }
 }

 public static Color getColor(Color C) {
  return Color.color(C.getRed(), C.getGreen(), C.getBlue());
 }

 public static Color getColor(double shade) {
  return getColor(shade, shade, shade);
 }

 public static Color getColor(double R, double G, double B) {
  return Color.color(clamp(R), clamp(G), clamp(B));
 }

 public enum Phong {
  ;

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
   PM.setDiffuseColor(Color.color(clamp(R), clamp(G), clamp(B)));
  }

  public static void setDiffuseRGB(PhongMaterial PM, double R, double G, double B, double transparency) {
   PM.setDiffuseColor(Color.color(clamp(R), clamp(G), clamp(B), clamp(transparency)));
  }

  public static void setSpecularRGB(PhongMaterial PM, Color C) {
   PM.setSpecularColor(C);
  }

  public static void setSpecularRGB(PhongMaterial PM, double shade) {
   setSpecularRGB(PM, shade, shade, shade);
  }

  public static void setSpecularRGB(PhongMaterial PM, double R, double G, double B) {
   PM.setSpecularColor(Color.color(clamp(R), clamp(G), clamp(B)));
  }

  public static void setSpecularRGB(PhongMaterial PM, double R, double G, double B, double transparency) {//<-Keep method in case we need it later
   PM.setSpecularColor(Color.color(clamp(R), clamp(G), clamp(B), clamp(transparency)));
  }

  public static void setSelfIllumination(PhongMaterial PM, Color C) {
   setSelfIllumination(PM, C.getRed(), C.getGreen(), C.getBlue());
  }

  public static void setSelfIllumination(PhongMaterial PM, double R, double G, double B) {
   colorGetter.setFill(Color.color(clamp(R), clamp(G), clamp(B)));
   colorGetter.fillRect(0, 0, 1, 1);
   PM.setSelfIlluminationMap(colorGetterCanvas.snapshot(null, null));
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

 public enum Images {
  ;

  public static void load(Map<? super String, ? super Image> M, String name) {
   if (!M.containsKey(name)) {
    try {
     M.put(name, new Image(new FileInputStream(imageFolder + File.separator + name + imageExtension)));
    } catch (FileNotFoundException E) {
     System.out.println(imageLoadingException + E);
    }
   }
  }

  public static void load(Map<? super String, ? super Image> M, String name, double quantity) {
   for (int n = 0; n < quantity; n++) {
    try {
     load(M, name + n);
    } catch (RuntimeException E) {
     if (quantity < Double.POSITIVE_INFINITY) {
      System.out.println(imageLoadingException + E);
     }
     break;
    }
   }
  }

  public static Image get(String name) {
   return VE.images.getOrDefault(name, null);
  }

  public static Image getNormalMap(String name) {
   return VE.Options.normalMapping ? get(name + "N") : null;
  }

  public static Image getLowResolution(Image I) {
   VE.denyExpensiveInGameCall();
   if (I != null) {
    ImageView IV = new ImageView(I);
    IV.setScaleX(Math.min(500 / I.getWidth(), 1));//*Don't exceed the original size!
    IV.setScaleY(Math.min(500 / I.getHeight(), 1));//*
    return IV.snapshot(null, null);
   }
   return null;
  }
 }

 public static void setMaterialSecurely(Shape3D shape3D, PhongMaterial PM) {
  if (PM == null) {
   throw new IllegalStateException("Blocked attempt at setting from a null PhongMaterial");
  } else {
   shape3D.setMaterial(PM);
  }
 }

 public static boolean equals(String in, String... prefixes) {
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

 public static boolean containsEnum(Enum in, Enum... prefixes) {//<-Does this work?!todo
  String name = in.name();
  for (Enum s : prefixes) {
   if (name.contains(s.name())) {
    return true;
   }
  }
  return false;
 }

 public static double[] listToArray(List<Double> source) {
  if (source.isEmpty()) {
   return new double[source.size()];
  } else {
   double[] d = new double[source.size()];
   for (int i = d.length; --i >= 0; ) {
    d[i] = source.get(i);
   }
   return d;
  }
 }

 static List<Double> arrayToList(double[] source) {//<-Keep in case it's needed later
  List<Double> d = new ArrayList<>();
  for (double v : source) {
   d.add(v);
  }
  return d;
 }

 public static int randomize(long value, int length) {
  if (length > 1) {
   long lastValue = value;
   while (value == lastValue) {
    value = random(length);
   }
  }
  return (int) value;
 }

 public static int random(int intIn) {
  return
  intIn > 0 ? ThreadLocalRandom.current().nextInt(intIn) :
  intIn < 0 ? -ThreadLocalRandom.current().nextInt(-intIn) :
  0;
 }

 public static long random(long longIn) {
  return
  longIn > 0 ? ThreadLocalRandom.current().nextLong(longIn) :
  longIn < 0 ? ThreadLocalRandom.current().nextLong(-longIn) :
  0;
 }

 public static double random(double doubleIn) {
  return doubleIn * random();
 }

 public static double random() {
  return ThreadLocalRandom.current().nextDouble();
 }

 public static double randomPlusMinus(double doubleIn) {
  return doubleIn * (random() - random());
 }

 public static double sin(double in) {
  while (in > 180) in -= 360;
  while (in < -180) in += 360;
  return StrictMath.sin(in * .01745329251994329576923690768489);
 }

 public static double cos(double in) {
  while (in > 180) in -= 360;
  while (in < -180) in += 360;
  return StrictMath.cos(in * .01745329251994329576923690768489);
 }

 private static double arcCos(double in) {
  return StrictMath.acos(in);
 }

 public static double arcTan(double in) {
  return StrictMath.atan(in) * 57.295779513082320876798154814092;
 }

 public static void rotate(Node N, Core core) {
  rotate(N, core.XY, core.YZ, core.XZ);
 }

 public static void rotate(Node n, double XY, double YZ, double XZ) {
  double cosXY = cos(XY), cosYZ = cos(YZ), cosXZ = cos(XZ);
  XY = sin(XY);
  YZ = sin(YZ);
  XZ = sin(XZ);
  double d = arcCos(((cosXY * cosXZ) + (cosXY * cosYZ - XY * YZ * XZ) + (cosYZ * cosXZ) - 1.) * .5);
  if (d == 0) {//<-do NOT remove!
   n.setRotationAxis(new Point3D(0, 0, 0));
   n.setRotate(0);
  } else {
   double den = 1 / (2 * StrictMath.sin(d));
   n.setRotationAxis(new Point3D(((-cosXZ * YZ) - (cosXY * YZ + cosYZ * XY * XZ)) * den, ((XY * YZ - cosXY * cosYZ * XZ) - XZ) * den, ((-cosXZ * XY) - (cosYZ * XY + cosXY * YZ * XZ)) * den));
   n.setRotate(Math.toDegrees(d));
  }
 }

 public static void rotate(Node N, double YZ, double XZ) {
  double cosXZ = cos(XZ), sinYZ = sin(YZ), sinXZ = sin(XZ), cosYZ = cos(YZ),
  d = arcCos((cosXZ + cosYZ + (cosYZ * cosXZ) - 1.) * .5),
  den = 1 / (2 * StrictMath.sin(d));
  N.setRotationAxis(new Point3D(((-cosXZ * sinYZ) - sinYZ) * den, (-(cosYZ * sinXZ) - sinXZ) * den, -(sinYZ * sinXZ) * den));
  N.setRotate(Math.toDegrees(d));
 }

 public static void randomRotate(Node N) {
  N.setRotationAxis(new Point3D(random(), random(), random()));
  N.setRotate(random(360.));
 }

 public static void rotate(double[] a, double[] b, double axis) {
  double a1 = a[0], b1 = b[0], sinAxis = sin(axis), cosAxis = cos(axis);
  a[0] = a1 * cosAxis - b1 * sinAxis;
  b[0] = a1 * sinAxis + b1 * cosAxis;
 }

 public static void rotateWithPivot(double[] a, double[] b, double pivot1, double pivot2, double axis) {
  double a1 = a[0], b1 = b[0], sinAxis = sin(axis), cosAxis = cos(axis);
  a[0] = pivot1 + ((a1 - pivot1) * cosAxis - (b1 - pivot2) * sinAxis);
  b[0] = pivot2 + ((a1 - pivot1) * sinAxis + (b1 - pivot2) * cosAxis);
 }

 /*public static double round(double value) {
  return round(value, 0);
 }
 public static double round(double value, int places) {
  return BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_EVEN).doubleValue();
 }
 public static long roundLong(double value) {
  return BigDecimal.valueOf(value).setScale(0, RoundingMode.HALF_EVEN).longValue();
 }*/

 public static double clamp(double d) {
  return clamp(0, d, 1);
 }

 public static double clamp(double minimum, double in, double maximum) {
  return Math.max(minimum, Math.min(in, maximum));
 }

 public static long clamp(long minimum, long in, long maximum) {
  return Math.max(minimum, Math.min(in, maximum));
 }

 public static double netValue(double v1, double v2) {
  return Math.sqrt(v1 * v1 + v2 * v2);
 }

 public static double netValue(double v1, double v2, double v3) {
  return Math.sqrt(v1 * v1 + v2 * v2 + v3 * v3);
 }

 public static double distance(double coordinate1, double otherCoordinate1, double coordinate2, double otherCoordinate2) {
  return netValue(coordinate1 - otherCoordinate1, coordinate2 - otherCoordinate2);
 }

 public static double distance(Core C1, Core C2) {
  return distance(C1.X, C2.X, C1.Y, C2.Y, C1.Z, C2.Z);
 }

 public static double distanceXZ(Core C1, Core C2) {
  return distance(C1.X, C2.X, C1.Z, C2.Z);
 }

 private static double distance(double x, double y, double z) {
  return distance(x, Camera.X, y, Camera.Y, z, Camera.Z);
 }

 public static double distance(Core core) {
  return distance(core.X, Camera.X, core.Y, Camera.Y, core.Z, Camera.Z);
 }

 public static double distance(double X1, double X2, double Y1, double Y2, double Z1, double Z2) {
  return netValue(X1 - X2, Y1 - Y2, Z1 - Z2);
 }

 public static boolean outOfBounds(Core core, double tolerance) {
  return outOfBounds(core.X, core.Y, core.Z, tolerance);
 }

 private static boolean outOfBounds(double x, double y, double z, double tolerance) {
  double depth = E.Ground.level + (E.Pool.exists && distance(x, E.Pool.X, z, E.Pool.Z) < E.Pool.C[0].getRadius() ? E.Pool.depth : 0);
  return y > depth || x > E.mapBounds.right + tolerance || x < E.mapBounds.left - tolerance || z > E.mapBounds.forward + tolerance || z < E.mapBounds.backward - tolerance || y < E.mapBounds.Y - tolerance;
 }

 public static void setTranslate(Node N, Core core) {
  setTranslate(N, core.X, core.Y, core.Z);
 }

 public static void setTranslate(Node N, double X, double Y, double Z) {
  N.setTranslateX(X - Camera.X);
  N.setTranslateY(Y - Camera.Y);
  N.setTranslateZ(Z - Camera.Z);
 }

 public static void setScale(Node N, double size) {
  N.setScaleX(size);
  N.setScaleY(size);
  N.setScaleZ(size);
 }

 public static double getDepth(Core core) {
  return getDepth(core.X, core.Y, core.Z);
 }

 public static double getDepth(double X, double Y, double Z) {
  return (Y - Camera.Y) * Camera.sinYZ + ((X - Camera.X) * Camera.sinXZ + (Z - Camera.Z) * Camera.cosXZ) * Camera.cosYZ;
 }

 public static boolean render(Core core) {
  return render(core, 0);
 }

 public static boolean render(Core core, double depthTolerance) {
  return render(core.X, core.Y, core.Z, depthTolerance);
 }

 public static boolean render(double X, double Y, double Z) {
  return render(X, Y, Z, 0);
 }

 public static boolean render(double X, double Y, double Z, double depthTolerance) {
  return E.renderType == E.RenderType.ALL || (getDepth(X, Y, Z) > depthTolerance);
 }

 public static boolean renderWithLOD(Core core) {
  return renderWithLOD(core, 0);
 }

 private static boolean renderWithLOD(Core core, double depthTolerance) {
  return renderWithLOD(core.X, core.Y, core.Z, core.absoluteRadius, depthTolerance);
 }

 private static boolean renderWithLOD(double X, double Y, double Z, double absoluteRadius, double depthTolerance) {
  return E.renderType == E.RenderType.ALL || (getDepth(X, Y, Z) > depthTolerance && absoluteRadius * E.renderLevel >= distance(X, Y, Z) * Camera.zoom);
 }

 public static void setFPS() {
  double time = System.currentTimeMillis();
  FPS = StrictMath.pow(Math.min(1000 / (time - FPSTime), refreshRate), 1.005);//<-Flat algorithm not as accurate
  averageFPS += (FPS - averageFPS) * .03125;
  FPSTime = time;
 }

 public static boolean maxedFPS(boolean useAverage) {
  return (useAverage ? averageFPS : FPS) > 59;//<-'59' is more reliable
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
