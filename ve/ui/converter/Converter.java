package ve.ui.converter;

import javafx.scene.paint.Color;
import ve.utilities.D;
import ve.utilities.U;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Converter {

 static boolean axisSwap;
 static final String /*.*/obj = ".obj", triangulationError = ".OBJ must be fully Triangulated";

 static void saveFile(File file, CharSequence content) {
  try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(MainFrame.convertedFileFolder + File.separator + file.getName().replace(obj, "")), U.standardChars))) {
   bufferedWriter.write(content.toString());
  } catch (IOException e) {
   e.printStackTrace();
  }
 }

 private boolean objFaceEnd, gettingVertices;

 StringBuilder convert(File file, boolean invertX, boolean invertY, boolean invertZ) {
  List<Color> materialColor = new ArrayList<>(), modelColor = new ArrayList<>();
  List<String> materialName = new ArrayList<>();
  List<Vector3<Double>> vertices = new ArrayList<>();
  List<double[]> faces = new ArrayList<>();
  boolean hasMaterial = true;
  try (BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath().replace(obj, ".mtl"), U.standardChars))) {
   for (String line; (line = reader.readLine()) != null; ) {
    String[] split = line.split(" ");
    if (split[0].startsWith("newmtl")) {
     materialName.add(split[1]);
    } else if (split[0].startsWith("Kd")) {
     materialColor.add(U.getColor(Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), 1.));
    }
   }
  } catch (IOException e) {
   hasMaterial = false;
  }
  try (BufferedReader BR = new BufferedReader(new FileReader(file, U.standardChars))) {
   int materialIndex = 0;
   for (String S; (S = BR.readLine()) != null; ) {
    if (S.startsWith("v ")) {
     gettingVertices = true;
     vertices.add(new Vector3(valueOBJ(S, 0), valueOBJ(S, 1), valueOBJ(S, 2)));
    }
    materialIndex = S.startsWith("usemtl ") ? materialName.indexOf(S.split(" ")[1].trim()) : materialIndex;
    if (S.startsWith("f ")) {
     List<Double> listFaces = new ArrayList<>();
     gettingVertices = objFaceEnd = false;
     for (int i = 0; !objFaceEnd; i++) {
      listFaces.add(valueOBJ(S, i));
     }
     if (hasMaterial) {
      modelColor.add(materialColor.get(materialIndex));
     }
     faces.add(U.listToArray(listFaces));
    }
   }
  } catch (IOException E) {
   E.printStackTrace();
  }
  StringBuilder SB = new StringBuilder();
  for (int n = 0; n < faces.size(); ++n) {
   StringBuilder s2 = new StringBuilder(D.RGB + "(" + (hasMaterial ? Double.valueOf(modelColor.get(n).getRed()) : "1") + "," + (hasMaterial ? Double.valueOf(modelColor.get(n).getGreen()) : "1") + "," + (hasMaterial ? Double.valueOf(modelColor.get(n).getBlue()) : "1") + U.lineSeparator);
   int length = faces.get(n).length;
   for (int n1 = 0; n1 < length; ++n1) {
    int n2 = (int) faces.get(n)[n1];
    double y = vertices.get(n2).Y * (invertY ? -1 : 1), z = vertices.get(n2).Z * (invertZ ? -1 : 1);
    s2.append("(").append(vertices.get(n2).X * (invertX ? -1 : 1)).append(",").append(axisSwap ? z : y).append(",").append(axisSwap ? y : z).append(U.lineSeparator);
    if (n1 >= 3) {
     throw new IllegalStateException(triangulationError);//<-Not sure if sending this to UI will work properly
    }
   }
   SB.append(s2);
  }
  File newFile = new File(MainFrame.convertedFileFolder + File.separator + file.getName().replace(obj, ""));
  try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(newFile, U.standardChars))) {
   bufferedWriter.write(SB.toString());
  } catch (IOException e) {
   e.printStackTrace(System.err);
  }
  return optimize(newFile);
 }

 private static StringBuilder optimize(File file) {
  StringBuilder SB = new StringBuilder();
  String lastColor = "";
  try (BufferedReader BR = new BufferedReader(new FileReader(file, U.standardChars))) {
   for (String s; (s = BR.readLine()) != null; ) {
    if (s.startsWith("(")) {
     SB.append(s).append(U.lineSeparator);
    }
    if (s.startsWith(D.RGB + "(")) {
     if (!lastColor.equals(s)) {
      SB.append("><").append(s).append(U.lineSeparator).append("<>").append(U.lineSeparator).append("triangles").append(U.lineSeparator);
     }
     lastColor = s;
    }
   }
  } catch (IOException E) {
   E.printStackTrace();
  }
  return SB.append("><");
 }

 private double valueOBJ(CharSequence line, int index) {
  int n3 = 2, n4 = 0, n5 = 0, n6 = 0;
  StringBuilder s2 = new StringBuilder();
  while (n3 < line.length() && n5 != 2) {
   String s = String.valueOf(line.charAt(n3));
   if (s.equals(" ")) {
    if (n6 != 0) {
     ++n4;
     n6 = 0;
    }
    if (n5 == 1 || n4 > index) {
     n5 = 2;
    }
   } else {
    if (n4 == index) {
     s2.append(s);
     n5 = 1;
    }
    n6 = 1;
   }
   ++n3;
  }
  if (n3 >= line.length()) {
   objFaceEnd = true;
  }
  s2 = new StringBuilder((s2.length() == 0) ? "0" : s2.toString());
  double n2;
  if (gettingVertices) {
   n2 = Double.parseDouble(s2.toString());
  } else {
   int indexOf = s2.toString().indexOf('/');//<-Character is best
   if (indexOf != -1) {
    s2 = new StringBuilder(s2.substring(0, indexOf));
   }
   n2 = Double.parseDouble(s2.toString()) - 1;
  }
  return n2;
 }

 static class Vector3<E> {

  final E X, Y, Z;

  Vector3(E X, E Y, E Z) {
   this.X = X;
   this.Y = Y;
   this.Z = Z;
  }
 }
}
