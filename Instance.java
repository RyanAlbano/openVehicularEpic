package ve;

import java.io.*;
import java.util.*;

import ve.utilities.*;

public class Instance {

 //List<Piece> pieces = new ArrayList<>();
 public int vertexQuantity;
 public double X;
 public double Y;
 public double Z;
 public double XZ;
 public double XY;
 public double YZ;
 public double instanceToCameraDistance;
 public double renderRadius;
 public double absoluteRadius;
 public double collisionRadius;
 protected double modelSize = 1;
 protected double instanceSize = 1;
 protected final double[] modelScale = {1, 1, 1};
 protected final double[] instanceScale = {1, 1, 1};
 protected final double[] maxPlusX = new double[2];
 protected final double[] maxMinusX = new double[2];
 protected final double[] maxPlusY = new double[2];
 protected final double[] maxMinusY = new double[2];
 protected final double[] maxPlusZ = new double[2];
 protected final double[] maxMinusZ = new double[2];
 protected double wheelSmoothing;
 public double clearanceY;
 public double turretBaseY;
 public double driverViewX;
 protected double rimRadius;
 protected double rimDepth;
 public double netSpeedX;
 public double netSpeedY;
 public double netSpeedZ;
 protected final double[] wheelRGB = {.1875, .1875, .1875};
 protected final double[] rimRGB = new double[3];
 public final double[] theRandomColor = new double[3];
 public boolean flicker = U.random() < .5;
 public int modelNumber;
 public String modelProperties = "";
 public Quaternion rotation;

 protected FileInputStream getFile(List<String> l) {
  FileInputStream FIS = null;
  try {
   try {
    try {
     FIS = new FileInputStream("models" + File.separator + l.get(modelNumber));
    } catch (FileNotFoundException e) {
     FIS = new FileInputStream("models" + File.separator + "User-Submitted" + File.separator + l.get(modelNumber));
    }
   } catch (Exception e) {//<-MUST use general exception!
    FIS = new FileInputStream("models" + File.separator + "basic");
   }
  } catch (FileNotFoundException ignored) {
  }
  return FIS;
 }

 protected void getLoadColor(String s, double[] RGB) {
  if (U.startsWith(s, "RGB(", "><RGB(")) {
   try {
    RGB[0] = U.getValue(s, 0);
    RGB[1] = U.getValue(s, 1);
    RGB[2] = U.getValue(s, 2);
   } catch (Exception E) {
    RGB[0] = RGB[1] = RGB[2] = U.getValue(s, 0);
   }
  } else if (s.contains("theRandomColor")) {
   try {
    RGB[0] = theRandomColor[0] * U.getValue(s, 0);
    RGB[1] = theRandomColor[1] * U.getValue(s, 0);
    RGB[2] = theRandomColor[2] * U.getValue(s, 0);
   } catch (Exception E) {
    RGB[0] = theRandomColor[0];
    RGB[1] = theRandomColor[1];
    RGB[2] = theRandomColor[2];
   }
  } else if (s.startsWith("pavedColor")) {
   RGB[0] = RGB[1] = RGB[2] = VE.pavedRGB;
  }
 }

 protected void getSizeScaleTranslate(String s, double[] translate) {
  modelSize = s.startsWith("size") ? U.getValue(s, 0) : modelSize;
  if (s.startsWith("scale(")) {
   try {
    modelScale[0] = U.getValue(s, 0);
    modelScale[1] = U.getValue(s, 1);
    modelScale[2] = U.getValue(s, 2);
   } catch (Exception e) {
    modelScale[0] = modelScale[1] = modelScale[2] = U.getValue(s, 0);
   }
  } else if (s.startsWith("translate(")) {
   try {
    translate[0] = U.getValue(s, 0) * modelSize * instanceSize * modelScale[0] * instanceScale[0];
    translate[1] = U.getValue(s, 1) * modelSize * instanceSize * modelScale[1] * instanceScale[1];
    translate[2] = U.getValue(s, 2) * modelSize * instanceSize * modelScale[2] * instanceScale[2];
   } catch (Exception e) {
    translate[0] = translate[1] = translate[2] = U.getValue(s, 0);
   }
  }
 }

 protected void addSizes(double xx, double yy, double zz) {
  renderRadius = Math.max(renderRadius, U.netValue(xx, yy, zz));
  maxMinusX[0] = Math.min(maxMinusX[0], xx);
  maxPlusX[0] = Math.max(maxPlusX[0], xx);
  maxMinusY[0] = Math.min(maxMinusY[0], yy);
  maxPlusY[0] = Math.max(maxPlusY[0], yy);
  maxMinusZ[0] = Math.min(maxMinusZ[0], zz);
  maxPlusZ[0] = Math.max(maxPlusZ[0], zz);
  maxMinusX[1] += xx < 0 ? xx : 0;
  maxPlusX[1] += xx > 0 ? xx : 0;
  maxMinusY[1] += yy < 0 ? yy : 0;
  maxPlusY[1] += yy > 0 ? yy : 0;
  maxMinusZ[1] += zz < 0 ? zz : 0;
  maxPlusZ[1] += zz > 0 ? zz : 0;
 }
}
