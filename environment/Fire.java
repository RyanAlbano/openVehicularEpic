package ve.environment;

import java.util.*;

import javafx.scene.PointLight;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;

import static ve.VE.*;

import ve.utilities.U;

public class Fire extends MeshView {

 public final double X;
 public final double Y;
 public final double Z;
 public final double size;
 private PointLight light;
 public final boolean hasSound;
 public final List<Flame> flames = new ArrayList<>();

 public Fire(String s) {
  X = U.getValue(s, 0);
  Z = U.getValue(s, 1);
  Y = U.getValue(s, 2);
  size = U.getValue(s, 3);
  if (defaultVehicleLightBrightness > 0 && (viewableMapDistance > 0 || mapName.equals("Revelation 360 [HELL]"))) {
   light = new PointLight();
  }
  for (int n = 0; n < 50; n++) {
   flames.add(new Flame());
  }
  hasSound = s.contains("hear");
 }

 public void run(boolean update) {
  boolean windy = E.wind > 0;
  for (Flame flame : flames) {
   flame.X += flame.speedX * tick + (windy ? E.windX * tick : 0);
   flame.Z += flame.speedZ * tick + (windy ? E.windZ * tick : 0);
   flame.Y -= .1 * size * tick;
   if (Math.abs(Y - flame.Y) > size + U.random(size) || Math.abs(X - flame.X) > size + U.random(size) || Math.abs(Z - flame.Z) > size + U.random(size)) {
    flame.X = X + U.randomPlusMinus(size);
    flame.Z = Z + U.randomPlusMinus(size);
    flame.Y = Y;
    flame.speedX = U.randomPlusMinus(.2 * size);
    flame.speedZ = U.randomPlusMinus(.2 * size);
   }
  }
  if (U.getDepth(X, Y, Z) > -size) {
   U.setTranslate(this, X, Y, Z);
   setVisible(true);
   for (Flame flame : flames) {
    U.randomRotate(flame);
    ((PhongMaterial) flame.getMaterial()).setSelfIlluminationMap(U.getImage("firelight" + U.random(3)));
    U.setTranslate(flame, flame.X, flame.Y, flame.Z);
    flame.setVisible(true);
   }
  } else {
   setVisible(false);
   for (Flame flame : flames) {
    flame.setVisible(false);
   }
  }
  double fireToCameraDistance = U.distance(cameraX, X, cameraY, Y, cameraZ, Z);
  if (light != null) {
   if (viewableMapDistance < 1 || fireToCameraDistance < viewableMapDistance) {
    U.setLightRGB(light, 1, .5 + U.random(.4), U.random(.25));
    U.setTranslate(light, X, Y, Z);
    U.addLight(light);
   } else {
    U.remove(light);
   }
  }
  if (!muteSound && update) {
   U.soundLoop(sounds, "fire" + E.fires.indexOf(this), Math.sqrt(fireToCameraDistance) * .16);
  } else {
   U.soundStop(sounds, "fire" + E.fires.indexOf(this));
  }
 }
}
