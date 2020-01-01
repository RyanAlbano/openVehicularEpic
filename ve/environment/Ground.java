package ve.environment;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import ve.ui.Map;
import ve.utilities.SL;
import ve.utilities.U;

public enum Ground {
 ;
 static double X;
 static double Z;
 public static double level;
 public static Color RGB = U.getColor(0);
 public static final Cylinder C = new Cylinder();

 static {
  C.setRadius(10000000);
  C.setHeight(0);
  U.setMaterialSecurely(C, new PhongMaterial());
  U.Phong.setSpecularRGB((PhongMaterial) C.getMaterial(), E.Specular.Colors.standard);
  ((PhongMaterial) C.getMaterial()).setSpecularPower(E.Specular.Powers.dull);
 }

 public static void load(String s) {
  if (s.startsWith("ground(")) {
   RGB = U.getColor(U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
   U.Phong.setDiffuseRGB((PhongMaterial) C.getMaterial(), RGB);
   Terrain.RGB = U.getColor(U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
   if (!Map.name.equals(SL.Maps.crystalCavern)) {
    U.Nodes.add(C);
   }
  }
 }
}
