package ve.environment;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import ve.ui.Maps;
import ve.utilities.Nodes;
import ve.utilities.Phong;
import ve.utilities.SL;
import ve.utilities.U;

public enum Ground {
 ;
 static boolean exists;
 static double X;
 static double Z;
 public static double level;
 public static Color RGB = U.getColor(0);
 public static final Cylinder C = new Cylinder();

 static {
  C.setRadius(10000000);
  C.setHeight(0);
  U.setMaterialSecurely(C, new PhongMaterial());
  Phong.setSpecularRGB((PhongMaterial) C.getMaterial(), E.Specular.Colors.standard);
  ((PhongMaterial) C.getMaterial()).setSpecularPower(E.Specular.Powers.dull);
 }

 public static void load(String s) {
  if (s.startsWith("ground(")) {
   RGB = U.getColor(U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
   Phong.setDiffuseRGB((PhongMaterial) C.getMaterial(), RGB);
   Terrain.RGB = U.getColor(U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
   if (!Maps.name.equals(SL.Maps.crystalCavern)) {
    Nodes.add(C);
    exists = true;
   }
  }
 }

 static void reset() {
  Nodes.remove(C);
  exists = false;
  RGB = U.getColor(0);
  level = 0;
  Phong.setDiffuseRGB((PhongMaterial) C.getMaterial(), 0);
  ((PhongMaterial) C.getMaterial()).setSpecularMap(null);
 }
}
