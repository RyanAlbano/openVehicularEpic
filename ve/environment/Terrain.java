package ve.environment;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import ve.utilities.*;

public enum Terrain {
 ;
 public static String terrain = "", vehicleDefaultTerrain = "";
 public static final PhongMaterial universal = new PhongMaterial();
 public static final Image[] lowResolution = new Image[2];
 public static Color RGB = U.getColor(0);

 public static void load(String s) {
  if (s.startsWith("terrain(")) {
   terrain = " " + U.getString(s, 0) + " ";
   vehicleDefaultTerrain = terrain + (U.contains(terrain, D.thick(D.paved), D.thick(D.rock), D.thick(D.grid), D.thick(D.metal), D.thick(D.brightmetal)) ? D.thick(D.hard) : D.thick(D.ground));
   ((PhongMaterial) Ground.C.getMaterial()).setSpecularMap(Images.get(terrain.trim()));
   if (!U.getString(s, 0).isEmpty() && (RGB.getRed() > 0 || RGB.getGreen() > 0 || RGB.getBlue() > 0)) {
    for (long n = terrain.contains(D.thick(D.rock)) ? Long.MAX_VALUE : 4000; --n >= 0; ) {
     if (RGB.getRed() < 1 && RGB.getGreen() < 1 && RGB.getBlue() < 1) {
      RGB = U.getColor(RGB.getRed() * 1.0001, RGB.getGreen() * 1.0001, RGB.getBlue() * 1.0001);
     } else {
      break;
     }
    }
   }
   lowResolution[0] = Texture.getLowResolution(Images.get(terrain.trim()));
   lowResolution[1] = Texture.getLowResolution(Images.getNormalMap(terrain.trim()));
   universal.setSpecularPower(/*sloppy but works->*/vehicleDefaultTerrain.contains(D.thick(D.hard)) ? E.Specular.Powers.standard : E.Specular.Powers.dull);
   universal.setDiffuseMap(Images.get(terrain.trim()));
   universal.setSpecularMap(Images.get(terrain.trim()));
   universal.setBumpMap(Images.getNormalMap(terrain.trim()));
   Phong.setDiffuseRGB(universal, RGB);
   Phong.setSpecularRGB(universal, RGB);//<-'RGB' is tradition, but is 'Specular.Colors.standard' a better choice?
   GroundPlate.load(terrain.trim());
  }
 }

 static void reset() {
  terrain = D.thick(D.ground);
  RGB = U.getColor(0);
  universal.setDiffuseMap(null);
  universal.setSpecularMap(null);
  universal.setBumpMap(null);
  Phong.setDiffuseRGB(universal, 0);
  Phong.setSpecularRGB(universal, 0);
 }
}
