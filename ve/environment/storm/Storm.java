package ve.environment.storm;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Sphere;
import ve.effects.GroundBurst;
import ve.utilities.*;

public enum Storm {
 ;
 public static boolean exists;
 private static final Sphere cloud = new Sphere(200000);
 public static double cloudY;

 static {
  cloud.setScaleY(.1);
  cloud.setCullFace(CullFace.NONE);
 }

 public static void load(String s) {
  if (s.startsWith("storm(")) {
   exists = true;
   PhongMaterial PM = new PhongMaterial();
   Phong.setDiffuseRGB(PM, U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
   U.setMaterialSecurely(cloud, PM);
   cloudY = U.getValue(s, 3);
   Nodes.add(cloud);
   Rain.load(s);
   if (s.contains("lightning")) {
    Lightning.exists = true;
    Lightning.runMesh();//<-Must run first or mesh wil not load
    Nodes.add(Lightning.MV);
    for (int n = 75; --n >= 0; ) {
     Lightning.groundBursts.add(new GroundBurst());
    }
    Lightning.thunder = new Sound("thunder", Double.POSITIVE_INFINITY);
   }
  }
 }

 public static void run(boolean update) {
  if (exists) {
   cloud.setTranslateY(cloudY - Camera.C.Y);//<-Cloud's X and Z location is always synced to the camera's
   Rain.run(update);
   Lightning.run(update);
  }
 }

 public static void reset() {
  exists = Lightning.exists = false;
  Rain.raindrops.clear();
  Lightning.groundBursts.clear();
 }
}