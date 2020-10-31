package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import ve.instances.InstancePart;
import ve.utilities.Camera;
import ve.utilities.Phong;
import ve.utilities.U;

import java.util.ArrayList;
import java.util.List;

public enum Atmosphere {
 ;
 private static boolean exists;
 private static final int maxRings = 64;//<-Color (and probable performance) problems start occurring at 128+ etc.
 public static final List<Ring> rings = new ArrayList<>();
 private static final PhongMaterial PM = new PhongMaterial();
 private static long currentQuantity;
 private static double calibrationTimer;

 static {
  Phong.setSpecularRGB(PM, 0, 0, 0, 0);
  PM.setSpecularPower(Double.POSITIVE_INFINITY);
 }

 public static void load(String s) {
  if (s.startsWith("atmosphere") && rings.isEmpty()) {
   exists = true;
   for (long n = maxRings; --n >= 0; ) {
    rings.add(new Ring());
   }
   for (var ring : rings) {
    ring.setMaterial(PM);
    ring.setCullFace(CullFace.FRONT);
   }
   calibrate(8, true);
  }
 }

 private static void calibrate(long quantity, boolean firstLoad) {
  if (calibrationTimer <= 0 || firstLoad) {
   for (var ring : rings) {
    ring.show = false;
   }
   double outerRadius = 1500000;
   double startRadius = 0, addRadius = outerRadius / quantity;
   double startHeight = 250000, subtractHeight = startHeight / quantity;
   for (int n = 0; n < quantity; n++) {
    rings.get(n).setMesh(getRing(startRadius, outerRadius, startHeight));
    rings.get(n).show = true;
    startRadius += addRadius;
    startHeight -= subtractHeight;
   }
   Phong.setDiffuseRGB(PM, 0, 0, 0, .5 / quantity);
   currentQuantity = quantity;
   calibrationTimer = 20;
  }
 }

 private static TriangleMesh getRing(double bottomRadius, double topRadius, double height) {
  TriangleMesh TM = new TriangleMesh();
  //getting ring vertices
  int divisions = 36;
  float[] bottom = new float[divisions * 3];
  double placement = 360 / (double) divisions;
  for (int n = 0; ; n += 3) {
   try {
    bottom[n] = (float) (bottomRadius * U.sin(placement * n / 3));
    bottom[n + 1] = 0;//(float) (height * .5);
    bottom[n + 2] = (float) (bottomRadius * U.cos(placement * n / 3));
   } catch (RuntimeException E) {
    break;
   }
  }
  TM.getPoints().addAll(bottom);
  float[] top = new float[divisions * 3];
  for (int n = 0; ; n += 3) {
   try {
    top[n] = (float) (topRadius * U.sin(placement * n / 3));
    top[n + 1] = (float) -height;
    top[n + 2] = (float) (topRadius * U.cos(placement * n / 3));
   } catch (RuntimeException E) {
    break;
   }
  }
  TM.getPoints().addAll(top);
  TM.getTexCoords().addAll/*<-'addAll' and NOT 'setAll'*/(
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
  InstancePart.setCylindric(TM, divisions << 1, null);
  return TM;
 }

 static void run() {
  if (exists) {
   if (U.averageFPS < 30 && currentQuantity > 2) {
    calibrate(currentQuantity >> 1, false);
   } else if (U.yinYang && U.goodFPS(true) && currentQuantity < maxRings) {
    calibrate(currentQuantity << 1, false);
   }
   calibrationTimer -= U.tick;
   for (var ring : rings) {
    if (ring.show) {
     U.setTranslate(ring, Camera.C.X, 0, Camera.C.Z);
     ring.setVisible(true);
    } else {
     ring.setVisible(false);
    }
   }
  }
 }

 static class Ring extends MeshView {
  boolean show;
 }

 static void reset() {
  rings.clear();
  exists = false;
 }
}
