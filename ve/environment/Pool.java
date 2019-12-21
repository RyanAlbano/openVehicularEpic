package ve.environment;

import javafx.scene.paint.*;
import javafx.scene.shape.*;
import ve.Camera;
import ve.Core;
import ve.VE;
import ve.utilities.SL;
import ve.utilities.U;

public class Pool extends Core {

 public static boolean exists;
 static final PhongMaterial PM = new PhongMaterial();
 public static double depth;
 public static final Cylinder[] C = new Cylinder[2];
 public static Type type;

 static {
  C[0] = new Cylinder();
  C[1] = new Cylinder();
  C[0].setHeight(0);
  U.setMaterialSecurely(C[0], PM);
  U.setMaterialSecurely(C[1], PM);
  C[1].setCullFace(CullFace.FRONT);
 }

 public enum Type {water, lava, acid}

 public void load(String s) {
  if (s.startsWith("pool(")) {
   X = U.getValue(s, 0);
   Z = U.getValue(s, 1);
   C[0].setRadius(U.getValue(s, 2));
   C[1].setRadius(U.getValue(s, 2));
   depth = U.getValue(s, 3);
   C[1].setHeight(depth);
   U.Nodes.add(C[0], C[1]);
   double R = 0, G = .25, B = .75;
   PM.setSelfIlluminationMap(null);
   if (s.contains("lava")) {
    type = Pool.Type.lava;
    U.Phong.setSelfIllumination(PM, E.lavaSelfIllumination[0], E.lavaSelfIllumination[1], E.lavaSelfIllumination[2]);
   } else if (s.contains("acid")) {
    type = Pool.Type.acid;
    R = B = .25;
    G = 1;
   }
   PM.setDiffuseMap(U.Images.get(SL.water));
   U.Phong.setDiffuseRGB(PM, R, G, B);
   U.Phong.setSpecularRGB(PM, E.Specular.Colors.shiny);
   exists = true;
  }
 }

 public void runVision() {
  if (exists && Camera.Y > 0 && Camera.Y <= depth && U.distanceXZ(this) < C[0].getRadius()) {
   if (type == Pool.Type.lava) {
    U.fillRGB(E.graphicsContext, 1, .5 + U.random(.25), 0, .75);
   } else if (type == Pool.Type.acid) {
    U.fillRGB(E.graphicsContext, .25, .5, .25, .5);
   } else {
    U.fillRGB(E.graphicsContext, 0, 0, VE.Map.defaultVehicleLightBrightness > 0 ? 0 : .5, .5);
   }
   U.fillRectangle(E.graphicsContext, .5, .5, 1, 1);
  }
 }
}