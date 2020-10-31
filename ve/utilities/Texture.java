package ve.utilities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ve.environment.Terrain;
import ve.trackElements.TE;
import ve.ui.UI;

public class Texture {//todo--make specular maps for the existing textures
 private final Image original;
 private Image lowResolution;
 public static long userMaxResolution;
 public static boolean normalMapping;
 private static boolean adapted;

 public enum Resolution {original, limited, adapt}

 public static Resolution type;

 public Texture(String name) {
  original = Images.load(name);
 }

 Image get() {
  return type == Resolution.limited ? lowResolution : original;
 }

 void getLowResolution() {
  lowResolution = getLowResolution(original);
 }

 public static Image getLowResolution(Image I) {
  UI.denyExpensiveInGameCall();
  if (I != null) {
   ImageView IV = new ImageView(I);
   IV.setScaleX(Math.min(userMaxResolution / I.getWidth(), 1));//*Don't exceed the original size!
   IV.setScaleY(Math.min(userMaxResolution / I.getHeight(), 1));//*
   return IV.snapshot(null, null);//<-snapshot call prevents this void from being called outside the JavaFX application thread
  }
  return null;
 }

 public static void adapt() {
  if (type == Resolution.adapt) {
   if (!adapted && U.averageFPS < 30) {//<-Don't use direct FPS!
    Terrain.universal.setDiffuseMap(Terrain.lowResolution[0]);
    Terrain.universal.setSpecularMap(Terrain.lowResolution[0]);
    Terrain.universal.setBumpMap(Terrain.lowResolution[1]);
    TE.Paved.universal.setDiffuseMap(Images.paved.lowResolution);
    TE.Paved.universal.setSpecularMap(Images.paved.lowResolution);
    if (normalMapping) {
     TE.Paved.universal.setBumpMap(Images.pavedN.lowResolution);
    }
    adapted = true;
   } else if (adapted && U.goodFPS(true)) {//Don't create any 'new' images while setting the universals--or RAM will be killed!
    if (!Terrain.terrain.equals(D.thick(D.ground))) {//<-'ground' string will crash if checked in getter, thus skipped
     Terrain.universal.setDiffuseMap(Images.get(Terrain.terrain.trim()));
     Terrain.universal.setSpecularMap(Images.get(Terrain.terrain.trim()));
     Terrain.universal.setBumpMap(Images.getNormalMap(Terrain.terrain.trim()));
    }
    TE.Paved.universal.setDiffuseMap(Images.paved.original);
    TE.Paved.universal.setSpecularMap(Images.paved.original);
    if (normalMapping) {
     TE.Paved.universal.setBumpMap(Images.pavedN.original);
    }
    adapted = false;
   }
  }
 }
}