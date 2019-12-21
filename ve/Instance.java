package ve;

import java.io.*;

import javafx.scene.paint.Color;
import ve.trackElements.TE;
import ve.trackElements.trackParts.TrackPart;
import ve.trackElements.trackParts.TrackPartPart;
import ve.utilities.*;
import ve.vehicles.Vehicle;
import ve.vehicles.VehiclePart;

public class Instance extends Core {

 public int vertexQuantity;
 public double renderRadius;
 public double boundsX, boundsY, boundsZ;
 public double modelSize = 1;
 public final double[] modelScale = {1, 1, 1};
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
 protected Color wheelRGB = U.getColor(.1875), rimRGB = U.getColor(0);
 public Color theRandomColor = U.getColor(0);
 public boolean flicker = U.random() < .5;
 public int modelNumber;
 public String modelName = "";
 public Quaternion rotation;

 protected static FileInputStream getFile(String file, boolean vehicle) {
  String directory = vehicle ? SL.vehicles : "trackParts";
  File F = new File(U.modelFolder + File.separator + directory + File.separator + file);
  if (!F.exists()) {
   F = new File(U.modelFolder + File.separator + directory + File.separator + U.userSubmittedFolder + File.separator + file);
  }
  if (!F.exists()) {
   F = new File(U.modelFolder + File.separator + directory + File.separator + SL.basic);
  }
  try {
   return new FileInputStream(F);
  } catch (FileNotFoundException E) {
   return null;
  }
 }

 protected static void append(StringBuilder SB, String s, boolean useContains, String... prefixes) {
  for (String s1 : prefixes) {
   if (useContains ? s.contains(s1) : s.startsWith(s1)) {
    SB.append(" ").append(s1).append(" ");
   }
  }
 }

 protected Color getLoadColor(String s, Color RGB) {
  if (U.startsWith(s, SL.RGB + "(", "><RGB(")) {
   try {
    RGB = U.getColor(U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
   } catch (RuntimeException E) {
    RGB = U.getColor(U.getValue(s, 0));
   }
  } else if (s.contains(SL.theRandomColor)) {
   try {
    RGB = U.getColor(
    theRandomColor.getRed() * U.getValue(s, 0),
    theRandomColor.getGreen() * U.getValue(s, 0),
    theRandomColor.getBlue() * U.getValue(s, 0));
   } catch (RuntimeException E) {
    RGB = theRandomColor;
   }
  } else if (s.startsWith(SL.pavedColor)) {
   RGB = U.getColor(TE.Paved.globalShade);
  }
  return RGB;//<-Value may pass through unchanged
 }

 protected void getSizeScaleTranslate(String s, double[] translate, double sizeIn, double[] scaleIn) {
  modelSize = s.startsWith(SL.size + "(") ? U.getValue(s, 0) : modelSize;
  if (s.startsWith(SL.scale + "(")) {
   try {
    modelScale[0] = U.getValue(s, 0);
    modelScale[1] = U.getValue(s, 1);
    modelScale[2] = U.getValue(s, 2);
   } catch (RuntimeException e) {
    modelScale[0] = modelScale[1] = modelScale[2] = U.getValue(s, 0);
   }
  } else if (s.startsWith("translate(")) {
   try {
    translate[0] = U.getValue(s, 0) * modelSize * modelScale[0] * sizeIn * scaleIn[0];
    translate[1] = U.getValue(s, 1) * modelSize * modelScale[1] * sizeIn * scaleIn[1];
    translate[2] = U.getValue(s, 2) * modelSize * modelScale[2] * sizeIn * scaleIn[2];
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

 /*public void setPhongToUniversalTerrain(PhongMaterial PM) {
  PM.setDiffuseColor(E.Terrain.universal.getDiffuseColor());
  PM.setDiffuseMap(E.Terrain.universal.getDiffuseMap());
  PM.setSpecularColor(E.Terrain.universal.getSpecularColor());
  PM.setSpecularPower(E.Terrain.universal.getSpecularPower());
  PM.setSpecularMap(E.Terrain.universal.getSpecularMap());
  PM.setBumpMap(E.Terrain.universal.getBumpMap());
  PM.setSelfIlluminationMap(E.Terrain.universal.getSelfIlluminationMap());
 }*/

 protected void loadWheel(Vehicle V, TrackPart TP, double sourceX, double sourceY, double sourceZ, double i_wheelThickness, double i_wheelRadius, String type, String rimType, String textureType, boolean i_steers, boolean hide) {
  boolean vehicle = V != null;
  sourceX *= modelSize * modelScale[0];
  sourceY *= modelSize * modelScale[1];
  sourceZ *= modelSize * modelScale[2];
  i_wheelThickness *= modelSize * modelScale[0];
  i_wheelRadius *= modelSize;
  wheelSmoothing *= -1;
  double[] x0 = new double[96], y0 = new double[96], z0 = new double[96];
  String steers = i_steers ? " steerXZ steerFromXZ " : "";
  clearanceY = Math.max(clearanceY, sourceY + i_wheelRadius);
  int n;
  if (i_wheelRadius != 0 && !hide) {
   double wheelThickness = i_wheelThickness + wheelSmoothing, wheelRadius = i_wheelRadius - Math.abs(wheelSmoothing);
   for (n = x0.length; --n >= 0; ) {
    x0[n] = sourceX - (n < 24 ? i_wheelThickness : -i_wheelThickness);
   }
   z0[0] = sourceZ + U.sin(0) * wheelRadius;
   z0[1] = sourceZ + U.sin(15) * wheelRadius;
   z0[2] = sourceZ + U.sin(30) * wheelRadius;
   z0[3] = sourceZ + U.sin(45) * wheelRadius;
   z0[4] = sourceZ + U.sin(60) * wheelRadius;
   z0[5] = sourceZ + U.sin(75) * wheelRadius;
   z0[6] = sourceZ + U.sin(90) * wheelRadius;
   z0[7] = sourceZ + U.sin(105) * wheelRadius;
   z0[8] = sourceZ + U.sin(120) * wheelRadius;
   z0[9] = sourceZ + U.sin(135) * wheelRadius;
   z0[10] = sourceZ + U.sin(150) * wheelRadius;
   z0[11] = sourceZ + U.sin(165) * wheelRadius;
   z0[12] = sourceZ + U.sin(180) * wheelRadius;
   z0[13] = sourceZ + U.sin(195) * wheelRadius;
   z0[14] = sourceZ + U.sin(210) * wheelRadius;
   z0[15] = sourceZ + U.sin(225) * wheelRadius;
   z0[16] = sourceZ + U.sin(240) * wheelRadius;
   z0[17] = sourceZ + U.sin(255) * wheelRadius;
   z0[18] = sourceZ + U.sin(270) * wheelRadius;
   z0[19] = sourceZ + U.sin(285) * wheelRadius;
   z0[20] = sourceZ + U.sin(300) * wheelRadius;
   z0[21] = sourceZ + U.sin(315) * wheelRadius;
   z0[22] = sourceZ + U.sin(330) * wheelRadius;
   z0[23] = sourceZ + U.sin(345) * wheelRadius;
   y0[0] = sourceY + U.cos(0) * wheelRadius;
   y0[1] = sourceY + U.cos(15) * wheelRadius;
   y0[2] = sourceY + U.cos(30) * wheelRadius;
   y0[3] = sourceY + U.cos(45) * wheelRadius;
   y0[4] = sourceY + U.cos(60) * wheelRadius;
   y0[5] = sourceY + U.cos(75) * wheelRadius;
   y0[6] = sourceY + U.cos(90) * wheelRadius;
   y0[7] = sourceY + U.cos(105) * wheelRadius;
   y0[8] = sourceY + U.cos(120) * wheelRadius;
   y0[9] = sourceY + U.cos(135) * wheelRadius;
   y0[10] = sourceY + U.cos(150) * wheelRadius;
   y0[11] = sourceY + U.cos(165) * wheelRadius;
   y0[12] = sourceY + U.cos(180) * wheelRadius;
   y0[13] = sourceY + U.cos(195) * wheelRadius;
   y0[14] = sourceY + U.cos(210) * wheelRadius;
   y0[15] = sourceY + U.cos(225) * wheelRadius;
   y0[16] = sourceY + U.cos(240) * wheelRadius;
   y0[17] = sourceY + U.cos(255) * wheelRadius;
   y0[18] = sourceY + U.cos(270) * wheelRadius;
   y0[19] = sourceY + U.cos(285) * wheelRadius;
   y0[20] = sourceY + U.cos(300) * wheelRadius;
   y0[21] = sourceY + U.cos(315) * wheelRadius;
   y0[22] = sourceY + U.cos(330) * wheelRadius;
   y0[23] = sourceY + U.cos(345) * wheelRadius;
   for (n = 24; --n >= 0; ) {
    z0[n + 24] = z0[n];
    y0[n + 24] = y0[n];
   }
   for (n = 48; --n >= 0; ) {
    maxMinusX[0] = Math.min(maxMinusX[0], x0[n]);
    maxPlusX[0] = Math.max(maxPlusX[0], x0[n]);
    maxMinusY[0] = Math.min(maxMinusY[0], y0[n]);
    maxPlusY[0] = Math.max(maxPlusY[0], y0[n]);
    maxMinusZ[0] = Math.min(maxMinusZ[0], z0[n]);
    maxPlusZ[0] = Math.max(maxPlusZ[0], z0[n]);
    maxMinusX[1] += x0[n] < 0 ? x0[n] : 0;
    maxPlusX[1] += x0[n] > 0 ? x0[n] : 0;
    maxMinusY[1] += x0[n] < 0 ? y0[n] : 0;
    maxPlusY[1] += x0[n] > 0 ? y0[n] : 0;
    maxMinusZ[1] += x0[n] < 0 ? z0[n] : 0;
    maxPlusZ[1] += x0[n] > 0 ? z0[n] : 0;
   }
   String outerPlateProperties = " wheel wheelFaces ";
   if (vehicle) {
    V.parts.add(new VehiclePart(V, x0, y0, z0, 48, wheelRGB, type + outerPlateProperties + steers, textureType));
   } else {//Outer Plate
    TP.parts.add(new TrackPartPart(TP, x0, y0, z0, 48, wheelRGB, type + outerPlateProperties + steers, textureType));
   }
   String wheelRingProperties = " wheel wheelRingFaces ";
   if (rimRadius > 0) {
    if (i_wheelThickness != 0) {
     x0[0] += i_wheelThickness < 0 ? rimDepth : -rimDepth;
    }
    if (rimType.contains(" sport ")) {
     double smallRimRadius = rimRadius * .125;
     for (n = x0.length; --n > 0; ) {
      x0[n] = sourceX - i_wheelThickness;
     }
     x0[16] = sourceX + i_wheelThickness;
     if (i_wheelThickness > 0) {
      x0[3] -= rimDepth;
      x0[6] -= rimDepth;
      x0[9] -= rimDepth;
      x0[12] -= rimDepth;
      x0[15] -= rimDepth;
     } else if (i_wheelThickness < 0) {
      x0[3] += rimDepth;
      x0[6] += rimDepth;
      x0[9] += rimDepth;
      x0[12] += rimDepth;
      x0[15] += rimDepth;
     }
     y0[0] = sourceY;
     z0[0] = z0[9] = z0[16] = sourceZ;
     y0[1] = y0[2] = sourceY - rimRadius * U.cos(5);
     z0[1] = sourceZ - rimRadius * U.sin(5);
     z0[2] = sourceZ + rimRadius * U.sin(5);
     y0[3] = y0[15] = sourceY - smallRimRadius * U.cos(36);
     z0[3] = sourceZ + smallRimRadius * U.sin(36);
     y0[4] = y0[14] = sourceY - rimRadius * U.cos(67);
     z0[4] = sourceZ + rimRadius * U.sin(67);
     y0[5] = y0[13] = sourceY - rimRadius * U.cos(77);
     z0[5] = sourceZ + rimRadius * U.sin(77);
     y0[6] = y0[12] = sourceY + smallRimRadius * -U.cos(108);
     z0[6] = sourceZ + smallRimRadius * U.sin(108);
     y0[7] = y0[11] = sourceY + rimRadius * -U.cos(139);
     z0[7] = sourceZ + rimRadius * U.sin(139);
     y0[8] = y0[10] = sourceY + rimRadius * -U.cos(149);
     z0[8] = sourceZ + rimRadius * U.sin(149);
     y0[9] = sourceY + smallRimRadius;
     z0[10] = sourceZ - rimRadius * U.sin(149);
     z0[11] = sourceZ - rimRadius * U.sin(139);
     z0[12] = sourceZ - smallRimRadius * U.sin(108);
     z0[13] = sourceZ - rimRadius * U.sin(77);
     z0[14] = sourceZ - rimRadius * U.sin(67);
     z0[15] = sourceZ - smallRimRadius * U.sin(36);
     y0[16] = sourceY + rimRadius * U.cos(5);
     String sportRimProperties = " wheel sportRimFaces ";
     if (vehicle) {
      V.parts.add(new VehiclePart(V, x0, y0, z0, 17, rimRGB, type + rimType + sportRimProperties + steers, textureType));
     } else {//Sport rim
      TP.parts.add(new TrackPartPart(TP, x0, y0, z0, 17, rimRGB, type + rimType + sportRimProperties + steers, textureType));
     }
     for (n = x0.length; --n >= 0; ) {
      x0[n] = sourceX - (n < 48 ? i_wheelThickness : -i_wheelThickness);
      x0[n] *= 1.001;
     }
     z0[0] = sourceZ + U.sin(0) * rimRadius;
     z0[1] = sourceZ + U.sin(15) * rimRadius;
     z0[2] = sourceZ + U.sin(30) * rimRadius;
     z0[3] = sourceZ + U.sin(45) * rimRadius;
     z0[4] = sourceZ + U.sin(60) * rimRadius;
     z0[5] = sourceZ + U.sin(75) * rimRadius;
     z0[6] = sourceZ + U.sin(90) * rimRadius;
     z0[7] = sourceZ + U.sin(105) * rimRadius;
     z0[8] = sourceZ + U.sin(120) * rimRadius;
     z0[9] = sourceZ + U.sin(135) * rimRadius;
     z0[10] = sourceZ + U.sin(150) * rimRadius;
     z0[11] = sourceZ + U.sin(165) * rimRadius;
     z0[12] = sourceZ + U.sin(180) * rimRadius;
     z0[13] = sourceZ + U.sin(195) * rimRadius;
     z0[14] = sourceZ + U.sin(210) * rimRadius;
     z0[15] = sourceZ + U.sin(225) * rimRadius;
     z0[16] = sourceZ + U.sin(240) * rimRadius;
     z0[17] = sourceZ + U.sin(255) * rimRadius;
     z0[18] = sourceZ + U.sin(270) * rimRadius;
     z0[19] = sourceZ + U.sin(285) * rimRadius;
     z0[20] = sourceZ + U.sin(300) * rimRadius;
     z0[21] = sourceZ + U.sin(315) * rimRadius;
     z0[22] = sourceZ + U.sin(330) * rimRadius;
     z0[23] = sourceZ + U.sin(345) * rimRadius;
     y0[0] = sourceY + U.cos(0) * rimRadius;
     y0[1] = sourceY + U.cos(15) * rimRadius;
     y0[2] = sourceY + U.cos(30) * rimRadius;
     y0[3] = sourceY + U.cos(45) * rimRadius;
     y0[4] = sourceY + U.cos(60) * rimRadius;
     y0[5] = sourceY + U.cos(75) * rimRadius;
     y0[6] = sourceY + U.cos(90) * rimRadius;
     y0[7] = sourceY + U.cos(105) * rimRadius;
     y0[8] = sourceY + U.cos(120) * rimRadius;
     y0[9] = sourceY + U.cos(135) * rimRadius;
     y0[10] = sourceY + U.cos(150) * rimRadius;
     y0[11] = sourceY + U.cos(165) * rimRadius;
     y0[12] = sourceY + U.cos(180) * rimRadius;
     y0[13] = sourceY + U.cos(195) * rimRadius;
     y0[14] = sourceY + U.cos(210) * rimRadius;
     y0[15] = sourceY + U.cos(225) * rimRadius;
     y0[16] = sourceY + U.cos(240) * rimRadius;
     y0[17] = sourceY + U.cos(255) * rimRadius;
     y0[18] = sourceY + U.cos(270) * rimRadius;
     y0[19] = sourceY + U.cos(285) * rimRadius;
     y0[20] = sourceY + U.cos(300) * rimRadius;
     y0[21] = sourceY + U.cos(315) * rimRadius;
     y0[22] = sourceY + U.cos(330) * rimRadius;
     y0[23] = sourceY + U.cos(345) * rimRadius;
     double ringSmallRadius = rimRadius * .875;
     z0[0 + 24] = sourceZ + U.sin(0) * ringSmallRadius;
     z0[1 + 24] = sourceZ + U.sin(15) * ringSmallRadius;
     z0[2 + 24] = sourceZ + U.sin(30) * ringSmallRadius;
     z0[3 + 24] = sourceZ + U.sin(45) * ringSmallRadius;
     z0[4 + 24] = sourceZ + U.sin(60) * ringSmallRadius;
     z0[5 + 24] = sourceZ + U.sin(75) * ringSmallRadius;
     z0[6 + 24] = sourceZ + U.sin(90) * ringSmallRadius;
     z0[7 + 24] = sourceZ + U.sin(105) * ringSmallRadius;
     z0[8 + 24] = sourceZ + U.sin(120) * ringSmallRadius;
     z0[9 + 24] = sourceZ + U.sin(135) * ringSmallRadius;
     z0[10 + 24] = sourceZ + U.sin(150) * ringSmallRadius;
     z0[11 + 24] = sourceZ + U.sin(165) * ringSmallRadius;
     z0[12 + 24] = sourceZ + U.sin(180) * ringSmallRadius;
     z0[13 + 24] = sourceZ + U.sin(195) * ringSmallRadius;
     z0[14 + 24] = sourceZ + U.sin(210) * ringSmallRadius;
     z0[15 + 24] = sourceZ + U.sin(225) * ringSmallRadius;
     z0[16 + 24] = sourceZ + U.sin(240) * ringSmallRadius;
     z0[17 + 24] = sourceZ + U.sin(255) * ringSmallRadius;
     z0[18 + 24] = sourceZ + U.sin(270) * ringSmallRadius;
     z0[19 + 24] = sourceZ + U.sin(285) * ringSmallRadius;
     z0[20 + 24] = sourceZ + U.sin(300) * ringSmallRadius;
     z0[21 + 24] = sourceZ + U.sin(315) * ringSmallRadius;
     z0[22 + 24] = sourceZ + U.sin(330) * ringSmallRadius;
     z0[23 + 24] = sourceZ + U.sin(345) * ringSmallRadius;
     y0[0 + 24] = sourceY + U.cos(0) * ringSmallRadius;
     y0[1 + 24] = sourceY + U.cos(15) * ringSmallRadius;
     y0[2 + 24] = sourceY + U.cos(30) * ringSmallRadius;
     y0[3 + 24] = sourceY + U.cos(45) * ringSmallRadius;
     y0[4 + 24] = sourceY + U.cos(60) * ringSmallRadius;
     y0[5 + 24] = sourceY + U.cos(75) * ringSmallRadius;
     y0[6 + 24] = sourceY + U.cos(90) * ringSmallRadius;
     y0[7 + 24] = sourceY + U.cos(105) * ringSmallRadius;
     y0[8 + 24] = sourceY + U.cos(120) * ringSmallRadius;
     y0[9 + 24] = sourceY + U.cos(135) * ringSmallRadius;
     y0[10 + 24] = sourceY + U.cos(150) * ringSmallRadius;
     y0[11 + 24] = sourceY + U.cos(165) * ringSmallRadius;
     y0[12 + 24] = sourceY + U.cos(180) * ringSmallRadius;
     y0[13 + 24] = sourceY + U.cos(195) * ringSmallRadius;
     y0[14 + 24] = sourceY + U.cos(210) * ringSmallRadius;
     y0[15 + 24] = sourceY + U.cos(225) * ringSmallRadius;
     y0[16 + 24] = sourceY + U.cos(240) * ringSmallRadius;
     y0[17 + 24] = sourceY + U.cos(255) * ringSmallRadius;
     y0[18 + 24] = sourceY + U.cos(270) * ringSmallRadius;
     y0[19 + 24] = sourceY + U.cos(285) * ringSmallRadius;
     y0[20 + 24] = sourceY + U.cos(300) * ringSmallRadius;
     y0[21 + 24] = sourceY + U.cos(315) * ringSmallRadius;
     y0[22 + 24] = sourceY + U.cos(330) * ringSmallRadius;
     y0[23 + 24] = sourceY + U.cos(345) * ringSmallRadius;
     for (n = 48; --n >= 0; ) {
      z0[n + 48] = z0[n];
      y0[n + 48] = y0[n];
     }
     if (vehicle) {
      V.parts.add(new VehiclePart(V, x0, y0, z0, 96, rimRGB, type + rimType + wheelRingProperties + steers, textureType));
     } else {//Sport rim rings
      TP.parts.add(new TrackPartPart(TP, x0, y0, z0, 96, rimRGB, type + rimType + wheelRingProperties + steers, textureType));
     }
    } else {
     double hexagonAngle1 = 0.86602540378443864676372317075294, hexagonAngle2 = .5;
     y0[0] = y0[1] = y0[4] = y0[7] = sourceY;
     z0[0] = z0[7] = sourceZ;
     z0[1] = sourceZ + rimRadius;
     z0[4] = sourceZ - rimRadius;
     y0[2] = y0[3] = sourceY + hexagonAngle1 * rimRadius;
     z0[2] = z0[6] = sourceZ + hexagonAngle2 * rimRadius;
     z0[3] = z0[5] = sourceZ - hexagonAngle2 * rimRadius;
     y0[5] = y0[6] = sourceY - hexagonAngle1 * rimRadius;
     if (i_wheelThickness != 0) {
      x0[7] = sourceX + i_wheelThickness;
      x0[7] -= i_wheelThickness < 0 ? rimDepth : -rimDepth;
     }
     String rimProperties = " wheel rimFaces ";
     if (vehicle) {
      V.parts.add(new VehiclePart(V, x0, y0, z0, 8, rimRGB, type + rimType + rimProperties + steers, textureType));
     } else {//Normal rim
      TP.parts.add(new TrackPartPart(TP, x0, y0, z0, 8, rimRGB, type + rimType + rimProperties + steers, textureType));
     }
    }
   }
   if (Math.abs(i_wheelThickness) > 0) {
    for (n = 24; --n >= 0; ) {
     x0[n] = sourceX - wheelThickness;
     x0[n + 24] = sourceX + wheelThickness;
    }
    z0[0] = sourceZ + U.sin(0) * i_wheelRadius;
    z0[1] = sourceZ + U.sin(15) * i_wheelRadius;
    z0[2] = sourceZ + U.sin(30) * i_wheelRadius;
    z0[3] = sourceZ + U.sin(45) * i_wheelRadius;
    z0[4] = sourceZ + U.sin(60) * i_wheelRadius;
    z0[5] = sourceZ + U.sin(75) * i_wheelRadius;
    z0[6] = sourceZ + U.sin(90) * i_wheelRadius;
    z0[7] = sourceZ + U.sin(105) * i_wheelRadius;
    z0[8] = sourceZ + U.sin(120) * i_wheelRadius;
    z0[9] = sourceZ + U.sin(135) * i_wheelRadius;
    z0[10] = sourceZ + U.sin(150) * i_wheelRadius;
    z0[11] = sourceZ + U.sin(165) * i_wheelRadius;
    z0[12] = sourceZ + U.sin(180) * i_wheelRadius;
    z0[13] = sourceZ + U.sin(195) * i_wheelRadius;
    z0[14] = sourceZ + U.sin(210) * i_wheelRadius;
    z0[15] = sourceZ + U.sin(225) * i_wheelRadius;
    z0[16] = sourceZ + U.sin(240) * i_wheelRadius;
    z0[17] = sourceZ + U.sin(255) * i_wheelRadius;
    z0[18] = sourceZ + U.sin(270) * i_wheelRadius;
    z0[19] = sourceZ + U.sin(285) * i_wheelRadius;
    z0[20] = sourceZ + U.sin(300) * i_wheelRadius;
    z0[21] = sourceZ + U.sin(315) * i_wheelRadius;
    z0[22] = sourceZ + U.sin(330) * i_wheelRadius;
    z0[23] = sourceZ + U.sin(345) * i_wheelRadius;
    y0[0] = sourceY + U.cos(0) * i_wheelRadius;
    y0[1] = sourceY + U.cos(15) * i_wheelRadius;
    y0[2] = sourceY + U.cos(30) * i_wheelRadius;
    y0[3] = sourceY + U.cos(45) * i_wheelRadius;
    y0[4] = sourceY + U.cos(60) * i_wheelRadius;
    y0[5] = sourceY + U.cos(75) * i_wheelRadius;
    y0[6] = sourceY + U.cos(90) * i_wheelRadius;
    y0[7] = sourceY + U.cos(105) * i_wheelRadius;
    y0[8] = sourceY + U.cos(120) * i_wheelRadius;
    y0[9] = sourceY + U.cos(135) * i_wheelRadius;
    y0[10] = sourceY + U.cos(150) * i_wheelRadius;
    y0[11] = sourceY + U.cos(165) * i_wheelRadius;
    y0[12] = sourceY + U.cos(180) * i_wheelRadius;
    y0[13] = sourceY + U.cos(195) * i_wheelRadius;
    y0[14] = sourceY + U.cos(210) * i_wheelRadius;
    y0[15] = sourceY + U.cos(225) * i_wheelRadius;
    y0[16] = sourceY + U.cos(240) * i_wheelRadius;
    y0[17] = sourceY + U.cos(255) * i_wheelRadius;
    y0[18] = sourceY + U.cos(270) * i_wheelRadius;
    y0[19] = sourceY + U.cos(285) * i_wheelRadius;
    y0[20] = sourceY + U.cos(300) * i_wheelRadius;
    y0[21] = sourceY + U.cos(315) * i_wheelRadius;
    y0[22] = sourceY + U.cos(330) * i_wheelRadius;
    y0[23] = sourceY + U.cos(345) * i_wheelRadius;
    for (n = 24; --n >= 0; ) {
     z0[n + 24] = z0[n];
     y0[n + 24] = y0[n];
    }
    String treadProperties = " wheel cylindric ";
    if (vehicle) {
     V.parts.add(new VehiclePart(V, x0, y0, z0, 48, wheelRGB, type + treadProperties + steers, textureType));
    } else {//Treads
     TP.parts.add(new TrackPartPart(TP, x0, y0, z0, 48, wheelRGB, type + treadProperties + steers, textureType));
    }
   }
   if (wheelSmoothing != 0) {
    for (n = 24; --n >= 0; ) {
     x0[n] = sourceX - wheelThickness;
     x0[n + 24] = sourceX - i_wheelThickness;
    }
    for (n = 72; --n >= 48; ) {
     x0[n] = sourceX + wheelThickness;
     x0[n + 24] = sourceX + i_wheelThickness;
    }
    z0[0] = sourceZ + U.sin(0) * i_wheelRadius;
    z0[1] = sourceZ + U.sin(15) * i_wheelRadius;
    z0[2] = sourceZ + U.sin(30) * i_wheelRadius;
    z0[3] = sourceZ + U.sin(45) * i_wheelRadius;
    z0[4] = sourceZ + U.sin(60) * i_wheelRadius;
    z0[5] = sourceZ + U.sin(75) * i_wheelRadius;
    z0[6] = sourceZ + U.sin(90) * i_wheelRadius;
    z0[7] = sourceZ + U.sin(105) * i_wheelRadius;
    z0[8] = sourceZ + U.sin(120) * i_wheelRadius;
    z0[9] = sourceZ + U.sin(135) * i_wheelRadius;
    z0[10] = sourceZ + U.sin(150) * i_wheelRadius;
    z0[11] = sourceZ + U.sin(165) * i_wheelRadius;
    z0[12] = sourceZ + U.sin(180) * i_wheelRadius;
    z0[13] = sourceZ + U.sin(195) * i_wheelRadius;
    z0[14] = sourceZ + U.sin(210) * i_wheelRadius;
    z0[15] = sourceZ + U.sin(225) * i_wheelRadius;
    z0[16] = sourceZ + U.sin(240) * i_wheelRadius;
    z0[17] = sourceZ + U.sin(255) * i_wheelRadius;
    z0[18] = sourceZ + U.sin(270) * i_wheelRadius;
    z0[19] = sourceZ + U.sin(285) * i_wheelRadius;
    z0[20] = sourceZ + U.sin(300) * i_wheelRadius;
    z0[21] = sourceZ + U.sin(315) * i_wheelRadius;
    z0[22] = sourceZ + U.sin(330) * i_wheelRadius;
    z0[23] = sourceZ + U.sin(345) * i_wheelRadius;
    z0[0 + 24] = sourceZ + U.sin(0) * wheelRadius;
    z0[1 + 24] = sourceZ + U.sin(15) * wheelRadius;
    z0[2 + 24] = sourceZ + U.sin(30) * wheelRadius;
    z0[3 + 24] = sourceZ + U.sin(45) * wheelRadius;
    z0[4 + 24] = sourceZ + U.sin(60) * wheelRadius;
    z0[5 + 24] = sourceZ + U.sin(75) * wheelRadius;
    z0[6 + 24] = sourceZ + U.sin(90) * wheelRadius;
    z0[7 + 24] = sourceZ + U.sin(105) * wheelRadius;
    z0[8 + 24] = sourceZ + U.sin(120) * wheelRadius;
    z0[9 + 24] = sourceZ + U.sin(135) * wheelRadius;
    z0[10 + 24] = sourceZ + U.sin(150) * wheelRadius;
    z0[11 + 24] = sourceZ + U.sin(165) * wheelRadius;
    z0[12 + 24] = sourceZ + U.sin(180) * wheelRadius;
    z0[13 + 24] = sourceZ + U.sin(195) * wheelRadius;
    z0[14 + 24] = sourceZ + U.sin(210) * wheelRadius;
    z0[15 + 24] = sourceZ + U.sin(225) * wheelRadius;
    z0[16 + 24] = sourceZ + U.sin(240) * wheelRadius;
    z0[17 + 24] = sourceZ + U.sin(255) * wheelRadius;
    z0[18 + 24] = sourceZ + U.sin(270) * wheelRadius;
    z0[19 + 24] = sourceZ + U.sin(285) * wheelRadius;
    z0[20 + 24] = sourceZ + U.sin(300) * wheelRadius;
    z0[21 + 24] = sourceZ + U.sin(315) * wheelRadius;
    z0[22 + 24] = sourceZ + U.sin(330) * wheelRadius;
    z0[23 + 24] = sourceZ + U.sin(345) * wheelRadius;
    y0[0] = sourceY + U.cos(0) * i_wheelRadius;
    y0[1] = sourceY + U.cos(15) * i_wheelRadius;
    y0[2] = sourceY + U.cos(30) * i_wheelRadius;
    y0[3] = sourceY + U.cos(45) * i_wheelRadius;
    y0[4] = sourceY + U.cos(60) * i_wheelRadius;
    y0[5] = sourceY + U.cos(75) * i_wheelRadius;
    y0[6] = sourceY + U.cos(90) * i_wheelRadius;
    y0[7] = sourceY + U.cos(105) * i_wheelRadius;
    y0[8] = sourceY + U.cos(120) * i_wheelRadius;
    y0[9] = sourceY + U.cos(135) * i_wheelRadius;
    y0[10] = sourceY + U.cos(150) * i_wheelRadius;
    y0[11] = sourceY + U.cos(165) * i_wheelRadius;
    y0[12] = sourceY + U.cos(180) * i_wheelRadius;
    y0[13] = sourceY + U.cos(195) * i_wheelRadius;
    y0[14] = sourceY + U.cos(210) * i_wheelRadius;
    y0[15] = sourceY + U.cos(225) * i_wheelRadius;
    y0[16] = sourceY + U.cos(240) * i_wheelRadius;
    y0[17] = sourceY + U.cos(255) * i_wheelRadius;
    y0[18] = sourceY + U.cos(270) * i_wheelRadius;
    y0[19] = sourceY + U.cos(285) * i_wheelRadius;
    y0[20] = sourceY + U.cos(300) * i_wheelRadius;
    y0[21] = sourceY + U.cos(315) * i_wheelRadius;
    y0[22] = sourceY + U.cos(330) * i_wheelRadius;
    y0[23] = sourceY + U.cos(345) * i_wheelRadius;
    y0[0 + 24] = sourceY + U.cos(0) * wheelRadius;
    y0[1 + 24] = sourceY + U.cos(15) * wheelRadius;
    y0[2 + 24] = sourceY + U.cos(30) * wheelRadius;
    y0[3 + 24] = sourceY + U.cos(45) * wheelRadius;
    y0[4 + 24] = sourceY + U.cos(60) * wheelRadius;
    y0[5 + 24] = sourceY + U.cos(75) * wheelRadius;
    y0[6 + 24] = sourceY + U.cos(90) * wheelRadius;
    y0[7 + 24] = sourceY + U.cos(105) * wheelRadius;
    y0[8 + 24] = sourceY + U.cos(120) * wheelRadius;
    y0[9 + 24] = sourceY + U.cos(135) * wheelRadius;
    y0[10 + 24] = sourceY + U.cos(150) * wheelRadius;
    y0[11 + 24] = sourceY + U.cos(165) * wheelRadius;
    y0[12 + 24] = sourceY + U.cos(180) * wheelRadius;
    y0[13 + 24] = sourceY + U.cos(195) * wheelRadius;
    y0[14 + 24] = sourceY + U.cos(210) * wheelRadius;
    y0[15 + 24] = sourceY + U.cos(225) * wheelRadius;
    y0[16 + 24] = sourceY + U.cos(240) * wheelRadius;
    y0[17 + 24] = sourceY + U.cos(255) * wheelRadius;
    y0[18 + 24] = sourceY + U.cos(270) * wheelRadius;
    y0[19 + 24] = sourceY + U.cos(285) * wheelRadius;
    y0[20 + 24] = sourceY + U.cos(300) * wheelRadius;
    y0[21 + 24] = sourceY + U.cos(315) * wheelRadius;
    y0[22 + 24] = sourceY + U.cos(330) * wheelRadius;
    y0[23 + 24] = sourceY + U.cos(345) * wheelRadius;
    for (n = 48; --n >= 0; ) {
     z0[n + 48] = z0[n];
     y0[n + 48] = y0[n];
    }
    if (vehicle) {
     V.parts.add(new VehiclePart(V, x0, y0, z0, 96, wheelRGB, type + wheelRingProperties + steers, textureType));
    } else {//Tread edges
     TP.parts.add(new TrackPartPart(TP, x0, y0, z0, 96, wheelRGB, type + wheelRingProperties + steers, textureType));
    }
   }
  }
 }
}
