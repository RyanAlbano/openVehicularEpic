package ve.environment;

import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import ve.effects.GroundBurst;
import ve.ui.UI;
import ve.utilities.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public enum Storm {
 ;
 private static final Sphere stormCloud = new Sphere(200000);
 public static double stormCloudY;
 public static Sound thunder;

 static {
  stormCloud.setScaleY(.1);
  stormCloud.setCullFace(CullFace.NONE);
 }

 public static void load(String s) {
  if (s.startsWith("storm(")) {
   PhongMaterial PM = new PhongMaterial();
   Phong.setDiffuseRGB(PM, U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
   U.setMaterialSecurely(stormCloud, PM);
   stormCloudY = U.getValue(s, 3);
   Nodes.add(stormCloud);
   Rain.load(s);
   if (s.contains("lightning")) {
    Lightning.exists = true;
    Lightning.runMesh();//<-Must run first or mesh wil not load
    Nodes.add(Lightning.MV);
    for (int n = 75; --n >= 0; ) {
     Lightning.groundBursts.add(new GroundBurst());
    }
    thunder = new Sound("thunder", Double.POSITIVE_INFINITY);
   }
  }
 }

 static void run(Collection<Node> theChildren, boolean update) {
  if (theChildren.contains(stormCloud)) {
   stormCloud.setTranslateY(stormCloudY - Camera.Y);
   Rain.run(update);
   Lightning.run(update);
  }
 }

 public enum Lightning {
  ;
  public static boolean exists;
  public static double X, Z;
  public static long strikeStage;
  private static final MeshView MV = new MeshView();
  private static final PointLight[] light = new PointLight[2];
  static final List<GroundBurst> groundBursts = new ArrayList<>();
  static int currentBurst;

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
    Nodes.setRGB(light[n], 1, 1, 1);
   }
  }

  private static void runMesh() {
   double randomX = U.randomPlusMinus(3000.), randomZ = U.randomPlusMinus(3000.);
   ((TriangleMesh) MV.getMesh()).getPoints().setAll(
   (float) U.randomPlusMinus(1000.), (float) stormCloudY, (float) U.randomPlusMinus(1000.),
   (float) U.randomPlusMinus(1000.), (float) stormCloudY, (float) U.randomPlusMinus(1000.),
   (float) (U.randomPlusMinus(1000.) + randomX), (float) (stormCloudY * .5), (float) (U.randomPlusMinus(1000.) + randomZ),
   (float) (U.randomPlusMinus(1000.) + randomX), (float) (stormCloudY * .5), (float) (U.randomPlusMinus(1000.) + randomZ),
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
      if (strikeStage < 1) {//<-May call more than once if strikeStage progresses using 'tick'!
       MV.setVisible(true);
       for (int n = 25; --n >= 0; ) {
        groundBursts.get(currentBurst).deploy(X, Z);
        currentBurst = ++currentBurst >= groundBursts.size() ? 0 : currentBurst;
       }
       if (update) {
        thunder.play(Double.NaN, Math.sqrt(U.distance(Camera.X, X, Camera.Y, 0, Camera.Z, Z)) * Sound.standardDistance(.5));
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
    if (UI.status != UI.Status.replay && ++strikeStage > U.random(13000.)) {//<-Progress strikeStage using tick?
     X = Camera.X + U.randomPlusMinus(200000.);
     Z = Camera.Z + U.randomPlusMinus(200000.);
     strikeStage = 0;
    }
    for (GroundBurst burst : groundBursts) {
     burst.run();
    }
   }
  }
 }
}