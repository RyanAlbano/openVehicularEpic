package ve.environment.storm;

import javafx.scene.PointLight;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import ve.effects.GroundBurst;
import ve.environment.E;
import ve.ui.UI;
import ve.utilities.Camera;
import ve.utilities.Images;
import ve.utilities.Nodes;
import ve.utilities.U;
import ve.utilities.sound.Sound;
import ve.utilities.sound.Sounds;
import ve.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.List;

public enum Lightning {
 ;
 public static boolean exists;
 public static double X, Z;
 public static double strikeStage;
 static final MeshView MV = new MeshView();
 private static final PointLight[] light = new PointLight[2];
 public static final List<GroundBurst> groundBursts = new ArrayList<>();
 private static int currentBurst;
 public static Sound thunder;

 static {
  TriangleMesh lightningTM = new TriangleMesh();
  lightningTM.getTexCoords().setAll(0, 0);
  lightningTM.getFaces().setAll(
  0, 0, 1, 0, 2, 0,
  1, 0, 2, 0, 3, 0,
  2, 0, 3, 0, 4, 0,
  3, 0, 4, 0, 5, 0);
  MV.setMesh(lightningTM);
  MV.setCullFace(CullFace.NONE);
  PhongMaterial lightningPM = new PhongMaterial();
  lightningPM.setSelfIlluminationMap(Images.white);
  U.setMaterialSecurely(MV, lightningPM);
  for (int n = light.length; --n >= 0; ) {
   light[n] = new PointLight();
   Nodes.setLightRGB(light[n], 1, 1, 1);
  }
 }

 static void runMesh() {
  double randomX = U.randomPlusMinus(3000.), randomZ = U.randomPlusMinus(3000.);
  ((TriangleMesh) MV.getMesh()).getPoints().setAll(
  (float) U.randomPlusMinus(1000.), (float) Storm.cloudY, (float) U.randomPlusMinus(1000.),
  (float) U.randomPlusMinus(1000.), (float) Storm.cloudY, (float) U.randomPlusMinus(1000.),
  (float) (U.randomPlusMinus(1000.) + randomX), (float) (Storm.cloudY * .5), (float) (U.randomPlusMinus(1000.) + randomZ),
  (float) (U.randomPlusMinus(1000.) + randomX), (float) (Storm.cloudY * .5), (float) (U.randomPlusMinus(1000.) + randomZ),
  (float) U.randomPlusMinus(1000.), 0, (float) U.randomPlusMinus(1000.),
  (float) U.randomPlusMinus(1000.), 0, (float) U.randomPlusMinus(1000.));
 }

 static void run(boolean update) {
  if (exists) {
   Nodes.removePointLight(light[0]);
   if (strikeStage < 8) {
    runMesh();
    U.setTranslate(MV, X, 0, Z);
    if (strikeStage < 4) {
     U.setTranslate(light[0], X, 0, Z);
     Nodes.addPointLight(light[0]);
     if (strikeStage <= 0) {//<-May call more than once if strikeStage progresses using 'tick'!
      MV.setVisible(true);
      for (int n = 25; --n >= 0; ) {
       groundBursts.get(currentBurst).deploy(X, Z);
       currentBurst = ++currentBurst >= groundBursts.size() ? 0 : currentBurst;
      }
      if (update) {
       thunder.play(Double.NaN, Math.sqrt(U.distance(Camera.C.X, X, Camera.C.Y, 0, Camera.C.Z, Z)) * Sounds.standardGain(Sounds.gainMultiples.thunder));
      }
     }
    }
    U.setTranslate(light[1], X, 0, Z);
    Nodes.addPointLight(light[1]);
    U.fillRGB(E.GC, 1, 1, 1, U.random(.5));
    U.fillRectangle(E.GC, .5, .5, 1, 1);
   } else {
    MV.setVisible(false);
    Nodes.removePointLight(light[1]);
   }
   if (UI.status != UI.Status.replay && (strikeStage += U.tick) > U.random(13000.)) {
    X = Camera.C.X + U.randomPlusMinus(200000.);
    Z = Camera.C.Z + U.randomPlusMinus(200000.);
    strikeStage = 0;
   }
   for (var burst : groundBursts) {
    burst.run();
   }
  }
 }

 public static void vehicleInteract(Vehicle V) {
  if (exists && strikeStage < 1) {
   double distance = U.distance(V.X, X, V.Z, Z);
   if (V.Y >= Storm.cloudY && distance < V.collisionRadius * 6) {
    V.addDamage(V.durability * .5 + (distance < V.collisionRadius * 2 ? V.durability : 0));
    V.deformParts();
    V.throwChips(500, true);
    V.VA.crashHard.play(Double.NaN, V.VA.distanceVehicleToCamera);
    V.VA.crashHard.play(Double.NaN, V.VA.distanceVehicleToCamera);
    V.VA.crashHard.play(Double.NaN, V.VA.distanceVehicleToCamera);
   }
  }
 }

 public static void closeSound() {
  if (thunder != null) thunder.close();
 }
}