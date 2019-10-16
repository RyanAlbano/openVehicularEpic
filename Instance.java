package ve;

import java.io.*;

import ve.environment.E;
import ve.utilities.*;

public class Instance extends Core {

 public int vertexQuantity;
 public double distanceToCamera;
 public double renderRadius;
 public double boundsX, boundsY, boundsZ;
 public double collisionRadius;
 protected double modelSize = 1, instanceSize = 1;
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
 protected double rimRadius, rimDepth;
 public double netSpeedX, netSpeedY, netSpeedZ;
 protected final double[] wheelRGB = {.1875, .1875, .1875};
 protected final double[] rimRGB = new double[3];
 public final double[] theRandomColor = new double[3];
 public boolean flicker = U.random() < .5;
 public int modelNumber;
 public String modelProperties = "";
 protected String modelName = "";
 public Quaternion rotation;

 protected static FileInputStream getFile(String file) {
  FileInputStream FIS = null;
  try {
   try {
    try {
     FIS = new FileInputStream("models" + File.separator + file);
    } catch (FileNotFoundException e) {
     FIS = new FileInputStream("models" + File.separator + "User-Submitted" + File.separator + file);
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
   } catch (RuntimeException E) {
    RGB[0] = RGB[1] = RGB[2] = U.getValue(s, 0);
   }
  } else if (s.contains("theRandomColor")) {
   try {
    RGB[0] = theRandomColor[0] * U.getValue(s, 0);
    RGB[1] = theRandomColor[1] * U.getValue(s, 0);
    RGB[2] = theRandomColor[2] * U.getValue(s, 0);
   } catch (RuntimeException E) {
    RGB[0] = theRandomColor[0];
    RGB[1] = theRandomColor[1];
    RGB[2] = theRandomColor[2];
   }
  } else if (s.startsWith("pavedColor")) {
   RGB[0] = RGB[1] = RGB[2] = E.pavedRGB;
  }
 }

 protected void getSizeScaleTranslate(String s, double[] translate) {
  modelSize = s.startsWith("size(") ? U.getValue(s, 0) : modelSize;
  if (s.startsWith("scale(")) {
   try {
    modelScale[0] = U.getValue(s, 0);
    modelScale[1] = U.getValue(s, 1);
    modelScale[2] = U.getValue(s, 2);
   } catch (RuntimeException e) {
    modelScale[0] = modelScale[1] = modelScale[2] = U.getValue(s, 0);
   }
  } else if (s.startsWith("translate(")) {
   try {
    translate[0] = U.getValue(s, 0) * modelSize * instanceSize * modelScale[0] * instanceScale[0];
    translate[1] = U.getValue(s, 1) * modelSize * instanceSize * modelScale[1] * instanceScale[1];
    translate[2] = U.getValue(s, 2) * modelSize * instanceSize * modelScale[2] * instanceScale[2];
   } catch (RuntimeException e) {
    translate[0] = translate[1] = translate[2] = U.getValue(s, 0);
   }
  }
 }

 protected void addSizes(double xx, double yy, double zz) {
  renderRadius = Math.max(renderRadius, U.netValue(xx, yy, zz));
  boundsX = Math.max(boundsX, Math.abs(xx));
  boundsY = Math.max(boundsY, Math.abs(yy));
  boundsZ = Math.max(boundsZ, Math.abs(zz));
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
