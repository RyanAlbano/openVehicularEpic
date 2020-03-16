package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import ve.instances.Core;
import ve.ui.Maps;
import ve.utilities.Camera;
import ve.utilities.D;
import ve.utilities.Images;
import ve.utilities.Nodes;
import ve.utilities.Phong;
import ve.utilities.U;

public enum Pool {
 ;
 public static final Core core = new Core();
 public static boolean exists;
 static final PhongMaterial PM = new PhongMaterial();
 public static double depth;
 public static final Cylinder basin = new Cylinder(), surface = new Cylinder();
 public static Type type;

 static {
  surface.setHeight(0);
  U.setMaterialSecurely(surface, PM);
  U.setMaterialSecurely(basin, PM);
  basin.setCullFace(CullFace.FRONT);
 }

 public enum Type {water, lava, acid}

 public static void load(String s) {
  if (s.startsWith("pool(")) {
   core.X = U.getValue(s, 0);
   core.Z = U.getValue(s, 1);
   surface.setRadius(U.getValue(s, 2));
   basin.setRadius(U.getValue(s, 2));
   depth = U.getValue(s, 3);
   basin.setHeight(depth);
   Nodes.add(surface, basin);
   double R = 0, G = .25, B = .75;
   PM.setSelfIlluminationMap(null);
   if (s.contains("lava")) {
    type = Pool.Type.lava;
    PM.setSelfIlluminationMap(Phong.getSelfIllumination(E.lavaSelfIllumination));
   } else if (s.contains("acid")) {
    type = Pool.Type.acid;
    R = B = .25;
    G = 1;
   }
   PM.setDiffuseMap(Images.get(D.water));
   Phong.setDiffuseRGB(PM, R, G, B);
   Phong.setSpecularRGB(PM, E.Specular.Colors.shiny);
   exists = true;
  }
 }

 public static void runVision() {
  if (exists && Camera.C.Y > 0 && Camera.C.Y <= depth && U.distanceXZ(core) < surface.getRadius()) {
   if (type == Pool.Type.lava) {
    U.fillRGB(E.GC, 1, .5 + U.random(.25), 0, .75);
   } else if (type == Pool.Type.acid) {
    U.fillRGB(E.GC, .25, .5, .25, .5);
   } else {
    U.fillRGB(E.GC, 0, 0, Maps.defaultVehicleLightBrightness > 0 ? 0 : .5, .5);
   }
   U.fillRectangle(E.GC, .5, .5, 1, 1);
  }
 }

 static void reset() {
  depth = 0;
  exists = false;
  type = Type.water;
 }
}