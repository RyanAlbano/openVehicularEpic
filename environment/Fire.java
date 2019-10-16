package ve.environment;

import java.util.*;

import javafx.scene.PointLight;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;

import javafx.scene.shape.TriangleMesh;
import ve.Core;
import ve.Sound;
import ve.VE;
import ve.utilities.U;

public class Fire extends Core {

 private final MeshView pit;
 private PointLight light;
 private final Collection<Flame> flames = new ArrayList<>();
 private Sound sound;

 Fire(String s) {
  X = U.getValue(s, 0);
  Z = U.getValue(s, 1);
  Y = U.getValue(s, 2);
  absoluteRadius = U.getValue(s, 3);
  if (VE.defaultVehicleLightBrightness > 0 && (E.viewableMapDistance < Double.POSITIVE_INFINITY || VE.mapName.equals("Revelation 360 [HELL]"))) {
   light = new PointLight();
  }
  for (int n = 0; n < 50; n++) {
   flames.add(new Flame());
  }
  if (s.contains("hear")) {
   sound = new Sound("fire");
  }
  for (Flame flame : flames) {
   double fireSize = absoluteRadius * .5;
   TriangleMesh TM = new TriangleMesh();
   TM.getPoints().setAll((float) U.randomPlusMinus(fireSize), (float) U.randomPlusMinus(fireSize), (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), (float) U.randomPlusMinus(fireSize), (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), (float) U.randomPlusMinus(fireSize), (float) U.randomPlusMinus(fireSize));
   TM.getTexCoords().setAll(0, 0);
   TM.getFaces().setAll(0, 0, 1, 0, 2, 0);
   flame.MV = new MeshView(TM);
   flame.X = X;
   flame.Y = Double.POSITIVE_INFINITY;
   flame.Z = Z;
   flame.MV.setCullFace(CullFace.NONE);
   PhongMaterial PM = new PhongMaterial();
   U.setDiffuseRGB(PM, 0, 0, 0);
   U.setSpecularRGB(PM, 0, 0, 0);
   flame.MV.setMaterial(PM);
   U.add(flame.MV);
  }
  TriangleMesh TM = new TriangleMesh();
  double fireSize = absoluteRadius;
  TM.getPoints().setAll((float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
  (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize));
  TM.getTexCoords().setAll(0, 0);
  TM.getFaces().setAll(0, 0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8, 0, 9, 0, 10, 0, 11, 0, 12, 0, 13, 0, 14, 0, 15, 0, 16, 0, 17, 0, 18, 0, 19, 0, 20, 0, 21, 0, 22, 0, 23, 0);
  pit = new MeshView(TM);
  PhongMaterial PM = new PhongMaterial();
  U.setDiffuseRGB(PM, 0, 0, 0);
  U.setSpecularRGB(PM, 0, 0, 0);
  pit.setMaterial(PM);
  U.add(pit);
 }

 public void run(boolean update) {
  boolean windy = E.wind > 0;
  for (Flame flame : flames) {
   flame.X += flame.speedX * VE.tick + (windy ? E.windX * VE.tick : 0);
   flame.Z += flame.speedZ * VE.tick + (windy ? E.windZ * VE.tick : 0);
   flame.Y -= .1 * absoluteRadius * VE.tick;
   if (Math.abs(Y - flame.Y) > absoluteRadius + U.random(absoluteRadius) || Math.abs(X - flame.X) > absoluteRadius + U.random(absoluteRadius) || Math.abs(Z - flame.Z) > absoluteRadius + U.random(absoluteRadius)) {
    flame.X = X + U.randomPlusMinus(absoluteRadius);
    flame.Z = Z + U.randomPlusMinus(absoluteRadius);
    flame.Y = Y;
    flame.speedX = U.randomPlusMinus(.2 * absoluteRadius);
    flame.speedZ = U.randomPlusMinus(.2 * absoluteRadius);
   }
  }
  if (U.getDepth(this) > -absoluteRadius) {
   U.setTranslate(pit, this);
   pit.setVisible(true);
   for (Flame flame : flames) {
    U.randomRotate(flame.MV);
    ((PhongMaterial) flame.MV.getMaterial()).setSelfIlluminationMap(U.getImage("firelight" + U.random(3)));
    U.setTranslate(flame.MV, flame);
    flame.MV.setVisible(true);
   }
  } else {
   pit.setVisible(false);
   for (Flame flame : flames) {
    flame.MV.setVisible(false);
   }
  }
  double fireToCameraDistance = U.distance(this);
  if (light != null) {
   if (fireToCameraDistance < E.viewableMapDistance) {
    U.setLightRGB(light, 1, .5 + U.random(.4), U.random(.25));
    U.setTranslate(light, this);
    U.addLight(light);
   } else {
    U.removeLight(light);
   }
  }
  if (sound != null) {
   if (!VE.muteSound && update) {
    sound.loop(Math.sqrt(fireToCameraDistance) * .16);
   } else {
    sound.stop();
   }
  }
 }

 public void closeSound() {
  if (sound != null) sound.close();
 }
}
