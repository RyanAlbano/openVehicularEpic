package ve.utilities;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import ve.environment.E;
import ve.environment.Ground;
import ve.environment.MapBounds;
import ve.environment.Pool;
import ve.instances.Core;
import ve.instances.CoreAdvanced;
import ve.ui.UI;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public enum U {//Low-level utilities, such as math functions
 ;

 public static final String lineSeparator = System.lineSeparator();
 public static final Pattern regex = Pattern.compile("[(,)]");
 public static double tick, tickSeconds;
 public static long lastTime;
 public static boolean yinYang;
 public static double timerBase20;
 public static double FPS, averageFPS;
 public static double FPSTime;
 public static final double sin45 = .70710678118654752440084436210485;
 public static final double minimumAccurateLayeredOpacity = .05;
 public static final int refreshRate = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getRefreshRate();
 public static final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
 public static final String modelFolder = "models";
 public static final String userSubmittedFolder = "User-Submitted";
 public static final Charset standardChars = StandardCharsets.UTF_8;
 public static final String modelLoadingError = "Model-Loading Error: ";
 public static final DecimalFormat DF = new DecimalFormat("0.#E0");
 public static final Quaternion inert = new Quaternion();
 //public static final boolean onLinux = System.getProperty("os.name").toLowerCase().contains("linux");
 public static final long maxMemory = Runtime.getRuntime().maxMemory();

 public static String getString(CharSequence s, int index) {
  try {
   return regex.split(s)[index + 1];
  } catch (RuntimeException E) {
   return "";//Catch is needed!
  }
 }

 public static double getValue(CharSequence s, int index) {
  return Double.parseDouble(regex.split(s)[index + 1]);
 }

 public static void text(String s, double Y) {
  text(s, .5, Y);
 }

 public static void text(String s, double X, double Y) {
  UI.GC.setTextAlign(TextAlignment.CENTER);
  UI.GC.fillText(s, UI.width * X, UI.height * Y);
 }

 public static void textR(String s, double X, double Y) {
  UI.GC.setTextAlign(TextAlignment.RIGHT);
  UI.GC.fillText(s, UI.width * X, UI.height * Y);
 }

 public static void textL(String s, double X, double Y) {
  UI.GC.setTextAlign(TextAlignment.LEFT);
  UI.GC.fillText(s, UI.width * X, UI.height * Y);
 }

 public static void fillRGB(Color C) {
  UI.GC.setFill(C);
 }

 public static void fillRGB(double shade) {
  shade = clamp(shade);
  UI.GC.setFill(Color.color(shade, shade, shade));
 }

 public static void fillRGB(double R, double G, double B) {
  UI.GC.setFill(Color.color(clamp(R), clamp(G), clamp(B)));
 }

 public static void fillRGB(double R, double G, double B, double transparency) {
  fillRGB(UI.GC, R, G, B, transparency);
 }

 public static void fillRGB(GraphicsContext GC, double R, double G, double B, double transparency) {
  GC.setFill(Color.color(clamp(R), clamp(G), clamp(B), clamp(transparency)));
 }

 public static void strokeRGB(double shade) {
  shade = clamp(shade);
  UI.GC.setStroke(Color.color(shade, shade, shade));
 }

 public static void strokeRGB(double R, double G, double B) {
  UI.GC.setStroke(Color.color(clamp(R), clamp(G), clamp(B)));
 }

 public static void fillRectangle(double X, double Y, double rectangleWidth, double rectangleHeight) {
  fillRectangle(UI.GC, X, Y, rectangleWidth, rectangleHeight);
 }

 public static void fillRectangle(GraphicsContext GC, double X, double Y, double rectangleWidth, double rectangleHeight) {
  double width = rectangleWidth * UI.width, height = rectangleHeight * UI.height;
  GC.fillRect((UI.width * X) - (width * .5), (UI.height * Y) - (height * .5), width, height);
 }

 public static void drawRectangle(double X, double Y, double rectangleWidth, double rectangleHeight) {
  double width = Math.abs(rectangleWidth * UI.width), height = Math.abs(rectangleHeight * UI.height);
  UI.GC.strokeRect((UI.width * X) - (width * .5), (UI.height * Y) - (height * .5), width, height);
 }

 public static void font(double size) {
  UI.GC.setFont(new Font("Arial Black", size * UI.width));
 }

 public static Color getColor(Color C) {
  return Color.color(C.getRed(), C.getGreen(), C.getBlue());
 }

 public static Color getColor(double shade) {
  shade = clamp(shade);
  return Color.color(shade, shade, shade);
 }

 public static Color getColor(double R, double G, double B) {
  return Color.color(clamp(R), clamp(G), clamp(B));
 }

 public static Color getColor(double R, double G, double B, double A) {
  return Color.color(clamp(R), clamp(G), clamp(B), clamp(A));
 }

 public static void setMaterialSecurely(Shape3D shape3D, PhongMaterial PM) {
  UI.denyExpensiveInGameCall();//<-It's not really that expensive, but there's no reason this void should ever be called in-game, regardless
  if (PM == null) {
   UI.crashGame("Blocked attempt at setting from a null PhongMaterial");
  } else {
   shape3D.setMaterial(PM);
  }
 }

 public static boolean equals(String in, String... prefixes) {
  for (var s : prefixes) {
   if (in.equals(s)) {
    return true;
   }
  }
  return false;
 }

 public static boolean startsWith(String in, String... prefixes) {
  for (var s : prefixes) {
   if (in.startsWith(s)) {
    return true;
   }
  }
  return false;
 }

 public static boolean contains(String in, String... prefixes) {
  for (var s : prefixes) {
   if (in.contains(s)) {
    return true;
   }
  }
  return false;
 }

 public static boolean contains(Enum in, Enum... prefixes) {
  String name = in.name();
  for (var e : prefixes) {
   if (name.contains(e.name())) {
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
  intIn < 0 ? -ThreadLocalRandom.current().nextInt(-intIn/*<-Negated in value will always be positive*/) :
  0;
 }

 public static long random(long longIn) {
  return
  longIn > 0 ? ThreadLocalRandom.current().nextLong(longIn) :
  longIn < 0 ? -ThreadLocalRandom.current().nextLong(-longIn/*<-Negated in value will always be positive*/) :
  0;
 }

 public static double random(double doubleIn) {
  return doubleIn * random();
 }

 public static double random() {
  return ThreadLocalRandom.current().nextDouble();
 }

 public static double randomPlusMinus(double doubleIn) {
  //return doubleIn * (random() - random());//<-todo-has been changed to the system below--some areas trying to counter inaccuracies in the older system are no longer needed and should be removed
  return random(doubleIn) * (random() < .5 ? 1 : -1);
 }

 public static double sin(double in) {//*Value returned is for DEGREES and NOT radians
  while (in > 180) in -= 360;
  while (in < -180) in += 360;
  return StrictMath.sin(in * .01745329251994329576923690768489);
 }

 public static double cos(double in) {//*
  while (in > 180) in -= 360;
  while (in < -180) in += 360;
  return StrictMath.cos(in * .01745329251994329576923690768489);
 }

 private static double arcCos(double in) {
  return StrictMath.acos(in);
 }

 public static double arcTan(double in) {//*
  return StrictMath.atan(in) * 57.295779513082320876798154814092;
 }

 public static void rotate(Node N, CoreAdvanced core) {
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

 public static void rotate(Node N, double sinYZ, double cosYZ, double sinXZ, double cosXZ) {
  double d = arcCos((cosXZ + cosYZ + (cosYZ * cosXZ) - 1.) * .5),
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

 public static void rotate(double[] a, double[] b, double sinAxis, double cosAxis) {
  double a1 = a[0], b1 = b[0];
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

 public static double distance(Core core) {
  return distance(core, Camera.C);
 }

 public static double distance(Core C1, Core C2) {
  return distance(C1.X, C2.X, C1.Y, C2.Y, C1.Z, C2.Z);
 }

 public static double distance(double X1, double X2, double Y1, double Y2, double Z1, double Z2) {
  return netValue(X1 - X2, Y1 - Y2, Z1 - Z2);
 }

 public static double distanceXZ(Core core) {
  return distanceXZ(core, Camera.C);
 }

 public static double distanceXZ(Core C1, Core C2) {
  return distance(C1.X, C2.X, C1.Z, C2.Z);
 }

 /*public static double distanceXY(Core core) {
  return distance(core.X, Camera.X, core.Y, Camera.Y);
 }

 public static double distanceXY(Core C1, Core C2) {
  return distance(C1.X, C2.X, C1.Y, C2.Y);
 }

 public static double distanceYZ(Core core) {
  return distance(core.Y, Camera.Y, core.Z, Camera.Z);
 }

 public static double distanceYZ(Core C1, Core C2) {
  return distance(C1.Y, C2.Y, C1.Z, C2.Z);
 }*/

 public static boolean outOfBounds(Core core, double tolerance) {
  return outOfBounds(core.X, core.Y, core.Z, tolerance);
 }

 private static boolean outOfBounds(double x, double y, double z, double tolerance) {
  double depth = Ground.level + (Pool.exists && distance(x, Pool.core.X, z, Pool.core.Z) < Pool.surface.getRadius() ? Pool.depth : 0);
  return y > depth || x > MapBounds.right + tolerance || x < MapBounds.left - tolerance || z > MapBounds.forward + tolerance || z < MapBounds.backward - tolerance || y < MapBounds.Y - tolerance;
 }

 public static void setTranslate(Node N, Core core) {
  setTranslate(N, core.X, core.Y, core.Z);
 }

 public static void setTranslate(Node N, double X, double Y, double Z) {
  N.setTranslateX(X - Camera.C.X);
  N.setTranslateY(Y - Camera.C.Y);
  N.setTranslateZ(Z - Camera.C.Z);
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
  return (Y - Camera.C.Y) * Camera.sinYZ + ((X - Camera.C.X) * Camera.sinXZ + (Z - Camera.C.Z) * Camera.cosXZ) * Camera.cosYZ;
 }

 public static boolean render(Core core, boolean useLOD, boolean useViewableMapDistance) {
  return render(core, 0, useLOD, useViewableMapDistance);
 }

 public static boolean render(Core core, double depthTolerance, boolean useLOD, boolean useViewableMapDistance) {
  if (E.renderType == E.RenderType.ALL) {
   return true;
  } else if (getDepth(core) < depthTolerance) {
   return false;
  } else if (useLOD && useViewableMapDistance) {
   double distance = distance(core);
   return distance + depthTolerance < E.viewableMapDistance && core.absoluteRadius * E.renderLevel >= distance * Camera.FOV;
  } else if (useLOD) {
   return core.absoluteRadius * E.renderLevel >= distance(core) * Camera.FOV;
  } else if (useViewableMapDistance) {
   return distance(core) + depthTolerance < E.viewableMapDistance;
   //'depthTolerance' is also being used to adjust the distance objects are render-cut from viewableMapDistance. Not to be confused with 'absoluteRadius' usage.
  }
  return true;
 }

 public static void setFPS() {
  double time = System.currentTimeMillis();
  FPS = 1000 / Math.max(time - FPSTime, 1);//<-Math.max call is important--division by zero kills averageFPS calibration
  averageFPS += (FPS - averageFPS) * .03125;
  FPSTime = time;
 }

 public static boolean goodFPS(boolean useAverage) {
  return (useAverage ? averageFPS : FPS) > 59;//<-'59' allows reaching 'true' on standard 60-Hz devices
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

 public static void printSuccess() {
  System.out.println("Code executed to this location");
 }
}
