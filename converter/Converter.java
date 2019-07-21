package ve.converter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

class Converter {

 static boolean axisSwap;

 static void saveFile(File file, StringBuilder content) {
  try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File("ConvertedFiles/" + file.getName().replace(".obj", ""))))) {
   bufferedWriter.write(content.toString());
  } catch (IOException e) {
   e.printStackTrace(System.err);
  }
 }

 private boolean objFaceEnd;
 private boolean gettingVertices;

 StringBuilder convert(File file, boolean invertX, boolean invertY, boolean invertZ) {
  List<Color> materialColor = new ArrayList<>(), modelColor = new ArrayList<>();
  List<String> materialName = new ArrayList<>();
  List<Vector3<Double>> vertices = new ArrayList<>();
  List<double[]> faces = new ArrayList<>();
  boolean hasMaterial = true;
  try (BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath().replace(".obj", ".mtl")))) {
   for (String line; (line = reader.readLine()) != null; ) {
    String[] sp = line.split(" ");
    if (sp[0].startsWith("newmtl")) {
     materialName.add(sp[1]);
    } else if (sp[0].startsWith("Kd")) {
     materialColor.add(new Color(Double.valueOf(sp[1]), Double.valueOf(sp[2]), Double.valueOf(sp[3]), 1.));
    }
   }
  } catch (IOException e) {
   hasMaterial = false;
  }
  try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
   int materialIndex = 0;
   for (String l; (l = bufferedReader.readLine()) != null; ) {
    if (l.startsWith("v ")) {
     gettingVertices = true;
     vertices.add(new Vector3(valueOBJ(l, 0), valueOBJ(l, 1), valueOBJ(l, 2)));
    }
    materialIndex = l.startsWith("usemtl ") ? materialName.indexOf(l.split(" ")[1].trim()) : materialIndex;
    if (l.startsWith("f ")) {
     List<Double> al = new ArrayList<>();
     gettingVertices = objFaceEnd = false;
     for (int i = 0; !objFaceEnd; i++) {
      al.add(valueOBJ(l, i));
     }
     if (hasMaterial) {
      modelColor.add(materialColor.get(materialIndex));
     }
     faces.add(listToArray(al));
    }
   }
  } catch (IOException ex) {
   ex.printStackTrace(System.err);
  }
  StringBuilder SB = new StringBuilder();
  for (int n = 0; n < faces.size(); ++n) {
   StringBuilder s2 = new StringBuilder("RGB(" + (hasMaterial ? modelColor.get(n).getRed() : "1") + "," + (hasMaterial ? modelColor.get(n).getGreen() : "1") + "," + (hasMaterial ? modelColor.get(n).getBlue() : "1") + "\n");
   for (int n1 = 0; n1 < faces.get(n).length; ++n1) {
    int n2 = (int) faces.get(n)[n1];
    double y = vertices.get(n2).y * (invertY ? -1 : 1), z = vertices.get(n2).z * (invertZ ? -1 : 1);
    s2.append("(").append(vertices.get(n2).x * (invertX ? -1 : 1)).append(",").append(axisSwap ? z : y).append(",").append(axisSwap ? y : z).append("\n");
    if (n1 >= 3) {
     throw new IllegalStateException();
    }
   }
   SB.append(s2);
  }
  File newFile = new File("ConvertedFiles/" + file.getName().replace(".obj", ""));
  try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(newFile))) {
   bufferedWriter.write(SB.toString());
  } catch (IOException e) {
   e.printStackTrace(System.err);
  }
  return optimize(newFile);
 }

 private StringBuilder optimize(File file) {
  StringBuilder SB = new StringBuilder();
  String lastColor = "";
  try (BufferedReader BR = new BufferedReader(new FileReader(file))) {
   for (String s; (s = BR.readLine()) != null; ) {
    if (s.startsWith("(")) {
     SB.append(s).append("\n");
    }
    if (s.startsWith("RGB(")) {
     if (!lastColor.equals(s)) {
      SB.append("><").append(s).append("\n<>\ntriangles\n");
     }
     lastColor = s;
    }
   }
  } catch (IOException E) {
   E.printStackTrace(System.err);
  }
  return SB.append("><");
 }

 private double[] listToArray(List<Double> in) {
  double[] d = new double[in.size()];
  for (int n = 0; n < d.length; n++) {
   d[n] = in.get(n);
  }
  return d;
 }

 private double valueOBJ(String line, int index) {
  int n3 = 2, n4 = 0, n5 = 0, n6 = 0;
  StringBuilder s2 = new StringBuilder();
  while (n3 < line.length() && n5 != 2) {
   String string = "" + line.charAt(n3);
   if (string.equals(" ")) {
    if (n6 != 0) {
     ++n4;
     n6 = 0;
    }
    if (n5 == 1 || n4 > index) {
     n5 = 2;
    }
   } else {
    if (n4 == index) {
     s2.append(string);
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
   int indexOf = s2.toString().indexOf('/');
   if (indexOf != -1) {
    s2 = new StringBuilder(s2.substring(0, indexOf));
   }
   n2 = Double.parseDouble(s2.toString()) - 1;
  }
  return n2;
 }

 class Vector3<E> {

  final E x;
  final E y;
  final E z;

  Vector3(E x, E y, E z) {
   this.x = x;
   this.y = y;
   this.z = z;
  }
 }
}
