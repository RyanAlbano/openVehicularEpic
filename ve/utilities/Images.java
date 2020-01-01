package ve.utilities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ve.ui.Options;
import ve.ui.UI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static ve.utilities.U.*;

public enum Images {
 ;
 public static Image RA, white, amphibious,
 paved, rock, metal, brightmetal, wood, grid, ground1, ground2, sand, grass, foliage, cactus, water,
 pavedN, rockN, metalN, brightmetalN, woodN, ground1N, ground2N, sandN, grassN, foliageN, cactusN;
 public static List<Image> blink, fireLight, blueJet;

 public static Image load(String name) {
  try {
   return new Image(new FileInputStream(imageFolder + File.separator + name + imageExtension));
  } catch (FileNotFoundException E) {
   System.out.println(imageLoadingException + E);
   return null;
  }
 }

 public static List<Image> load(String name, double quantity) {
  List<Image> I = new ArrayList<>();
  try {
   for (int n = 0; n < quantity; n++) {
    I.add(new Image(new FileInputStream(imageFolder + File.separator + name + n + imageExtension)));
   }
  } catch (FileNotFoundException E) {
   if (quantity < Double.POSITIVE_INFINITY) {
    System.out.println(imageLoadingException + E);
   }
  }
  return I;
 }

 public static Image get(String name) {
  if (name.isEmpty() || name.startsWith(SL.snow) || name.startsWith(SL.ice)) {
   return null;
  } else if (name.startsWith(SL.paved)) {
   return paved;
  } else if (name.startsWith(SL.rock)) {
   return rock;
  } else if (name.startsWith(SL.metal)) {
   return metal;
  } else if (name.startsWith(SL.brightmetal)) {
   return brightmetal;
  } else if (name.startsWith(SL.wood)) {
   return wood;
  } else if (name.startsWith(SL.grid)) {
   return grid;
  } else if (name.startsWith(SL.ground + 1)) {
   return ground1;
  } else if (name.startsWith(SL.ground + 2)) {
   return ground2;
  } else if (name.startsWith(SL.sand)) {
   return sand;
  } else if (name.startsWith(SL.grass)) {
   return grass;
  } else if (name.startsWith(SL.foliage)) {
   return foliage;
  } else if (name.startsWith(SL.cactus)) {
   return cactus;
  } else if (name.startsWith(SL.water)) {
   return water;
  }
  throw new IllegalStateException("Return of '" + name + "' is not supported");
 }

 public static Image getNormalMap(String name) {
  if (Options.normalMapping) {
   if (name.startsWith(SL.paved)) {
    return pavedN;
   } else if (name.startsWith(SL.rock)) {
    return rockN;
   } else if (name.startsWith(SL.metal)) {
    return metalN;
   } else if (name.startsWith(SL.brightmetal)) {
    return brightmetalN;
   } else if (name.startsWith(SL.wood)) {
    return woodN;
   } else if (name.startsWith(SL.ground + 1)) {
    return ground1N;
   } else if (name.startsWith(SL.ground + 2)) {
    return ground2N;
   } else if (name.startsWith(SL.sand)) {
    return sandN;
   } else if (name.startsWith(SL.grass)) {
    return grassN;
   } else if (name.startsWith(SL.foliage)) {
    return foliageN;
   } else if (name.startsWith(SL.cactus)) {
    return cactusN;
   }
  }
  return null;
 }

 public static Image getLowResolution(Image I) {
  UI.denyExpensiveInGameCall();
  if (I != null) {
   ImageView IV = new ImageView(I);
   IV.setScaleX(Math.min(500 / I.getWidth(), 1));//*Don't exceed the original size!
   IV.setScaleY(Math.min(500 / I.getHeight(), 1));//*
   return IV.snapshot(null, null);
  }
  return null;
 }

 public static void draw(Image I, double X, double Y) {
  UI.graphicsContext.drawImage(I, UI.width * X - (I.getWidth() * .5), UI.height * Y - (I.getHeight() * .5));
 }
}