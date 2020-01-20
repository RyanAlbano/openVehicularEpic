package ve.environment;

import javafx.scene.paint.*;
import javafx.scene.shape.*;
import ve.utilities.Phong;
import ve.utilities.U;

import java.util.ArrayList;
import java.util.List;

public enum Fog {
 ;
 static boolean exists;
 public static final List<Sphere> spheres = new ArrayList<>();
 static final PhongMaterial PM = new PhongMaterial();
 static long currentQuantity;
 static final double opacityBase = 4;
 static double recalibrationTimer;

 enum Recalibration {increment, decrement}

 public static void load(String s) {
  if (s.startsWith("fog(") && spheres.isEmpty()) {
   currentQuantity = s.contains("advanced") ? 16 : 1;
   exists = true;
   double radius = (E.viewableMapDistance / currentQuantity) * .5;
   for (int n = 0; n < currentQuantity; n++) {
    spheres.add(new Sphere());
    U.setScale(spheres.get(spheres.size() - 1), radius);
    radius += E.viewableMapDistance / currentQuantity;
   }
   Phong.setDiffuseRGB(PM, E.skyRGB, Math.min(opacityBase / currentQuantity, .5));
   Phong.setSpecularRGB(PM, 0);
   PM.setSpecularPower(Double.POSITIVE_INFINITY);
   for (Sphere fog : spheres) {
    fog.setMaterial(PM);
    fog.setCullFace(CullFace.FRONT);
   }
   currentQuantity = 1;
   recalibrate(null);
  }
 }

 static void run() {
  if (exists) {
   if (U.averageFPS < 30 && currentQuantity > 1) {
    recalibrate(Recalibration.decrement);
   } else if (U.yinYang && U.averageFPS > 45 && currentQuantity < spheres.size()) {
    recalibrate(Recalibration.increment);
   }
   recalibrationTimer -= U.tick;
  }
 }

 static void recalibrate(Enum recalibration) {
  if (recalibrationTimer <= 0) {//<-Using '<=' so that initial down-calibration gets called
   currentQuantity += recalibration == null ? 0 : recalibration == Recalibration.increment ? 1 : -1;
   double radius = (E.viewableMapDistance / currentQuantity) * .5;
   for (Sphere fog : spheres) {
    U.setScale(fog, radius);
    radius += E.viewableMapDistance / currentQuantity;
    fog.setVisible(spheres.indexOf(fog) < currentQuantity);
   }
   Phong.setDiffuseRGB(PM, E.skyRGB, Math.min(opacityBase / currentQuantity, .5));
   recalibrationTimer = 10;
  }
 }

 static void reset() {
  spheres.clear();
  exists = false;
 }
}
