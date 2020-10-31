package ve.utilities;

import javafx.scene.image.Image;
import ve.ui.UI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public enum Images {
 ;
 public static final String folder = "images";
 public static final String extension = ".png";
 private static final String exception = "Image-loading Exception: ";
 public static Image RA, white, red, green, amphibious;
 public static Texture paved, rock, metal, brightmetal, wood, grid, ground1, ground2, sand, grass, foliage, cactus, water,
 pavedN, rockN, metalN, brightmetalN, woodN, ground1N, ground2N, sandN, grassN, foliageN, cactusN;
 public static List<Image> blink, fireLight, blueJet;

 public static Image load(String name) {
  try {
   return new Image(new FileInputStream(folder + File.separator + name + extension));
  } catch (FileNotFoundException E) {
   System.out.println(exception + E);
   return null;
  }
 }

 public static List<Image> load(String name, double quantity) {
  List<Image> I = new ArrayList<>();
  try {
   for (int n = 0; n < quantity; n++) {
    I.add(new Image(new FileInputStream(folder + File.separator + name + n + extension)));
   }
  } catch (FileNotFoundException E) {
   if (quantity < Double.POSITIVE_INFINITY) {
    System.out.println(exception + E);
   }
  }
  return I;
 }

 public static Image get(String name) {
  if (name.isEmpty() || name.startsWith(D.snow) || name.startsWith(D.ice)) {
   return null;
  } else if (name.startsWith(D.paved)) {
   return paved.get();
  } else if (name.startsWith(D.rock)) {
   return rock.get();
  } else if (name.startsWith(D.metal)) {
   return metal.get();
  } else if (name.startsWith(D.brightmetal)) {
   return brightmetal.get();
  } else if (name.startsWith(D.wood)) {
   return wood.get();
  } else if (name.startsWith(D.grid)) {
   return grid.get();
  } else if (name.startsWith(D.ground + 1)) {
   return ground1.get();
  } else if (name.startsWith(D.ground + 2)) {
   return ground2.get();
  } else if (name.startsWith(D.sand)) {
   return sand.get();
  } else if (name.startsWith(D.grass)) {
   return grass.get();
  } else if (name.startsWith(D.foliage)) {
   return foliage.get();
  } else if (name.startsWith(D.cactus)) {
   return cactus.get();
  } else if (name.startsWith(D.water)) {
   return water.get();
  }
  UI.crashGame("Return of '" + name + "' is not supported");
  return null;//<-Dummy value--should never be reached
 }

 public static Image getNormalMap(String name) {
  if (Texture.normalMapping) {
   if (name.startsWith(D.paved)) {
    return pavedN.get();
   } else if (name.startsWith(D.rock)) {
    return rockN.get();
   } else if (name.startsWith(D.metal)) {
    return metalN.get();
   } else if (name.startsWith(D.brightmetal)) {
    return brightmetalN.get();
   } else if (name.startsWith(D.wood)) {
    return woodN.get();
   } else if (name.startsWith(D.ground + 1)) {
    return ground1N.get();
   } else if (name.startsWith(D.ground + 2)) {
    return ground2N.get();
   } else if (name.startsWith(D.sand)) {
    return sandN.get();
   } else if (name.startsWith(D.grass)) {
    return grassN.get();
   } else if (name.startsWith(D.foliage)) {
    return foliageN.get();
   } else if (name.startsWith(D.cactus)) {
    return cactusN.get();
   }
  }
  return null;
 }

 public static void draw(Image I, double X, double Y) {
  UI.GC.drawImage(I, UI.width * X - (I.getWidth() * .5), UI.height * Y - (I.getHeight() * .5));
 }

 public static void getLowResolutionTextures() {
  rock.getLowResolution();
  rockN.getLowResolution();
  metal.getLowResolution();
  metalN.getLowResolution();
  brightmetal.getLowResolution();
  brightmetalN.getLowResolution();
  paved.getLowResolution();
  pavedN.getLowResolution();
  wood.getLowResolution();
  woodN.getLowResolution();
  foliage.getLowResolution();
  foliageN.getLowResolution();
  cactus.getLowResolution();
  cactusN.getLowResolution();
  grass.getLowResolution();
  grassN.getLowResolution();
  sand.getLowResolution();
  sandN.getLowResolution();
  ground1.getLowResolution();
  ground1N.getLowResolution();
  ground2.getLowResolution();
  ground2N.getLowResolution();
  //non-normaled
  grid.getLowResolution();
  water.getLowResolution();
 }
}