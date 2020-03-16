package ve.trackElements.trackParts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import ve.environment.Tornado;
import ve.instances.CoreAdvanced;
import ve.ui.Maps;
import ve.utilities.Images;
import ve.utilities.Nodes;
import ve.utilities.Phong;
import ve.utilities.U;

public enum RepairPoint {
 ;
 public static final Collection<Instance> instances = new ArrayList<>();
 private static final PhongMaterial PM = new PhongMaterial();
 private static final PhongMaterial shockPM = new PhongMaterial();

 static {
  Phong.setDiffuseRGB(PM, 1, 1, 1, .1);
  Phong.setSpecularRGB(PM, 0);
  shockPM.setSelfIlluminationMap(Images.white);
  Phong.setSpecularRGB(shockPM, 0);
 }

 public static class Instance extends CoreAdvanced {
  double pulseSize;
  private final List<Cylinder> shocks = new ArrayList<>();
  public final Sphere pulse = new Sphere();

  public Instance(double sourceX, double sourceY, double sourceZ, double inSize) {
   absoluteRadius = inSize;
   X = sourceX;
   Y = sourceY;
   Z = sourceZ;
   U.setMaterialSecurely(pulse, PM);
   //Nodes.add(pulse);//<-Added with transparent Nodes
   pulse.setVisible(false);
   for (int n = 0; n < 16; n++) {
    shocks.add(new Cylinder(U.random(absoluteRadius * .01), absoluteRadius * 2, 4));
    U.setMaterialSecurely(shocks.get(n), shockPM);
    Nodes.add(shocks.get(n));
    shocks.get(n).setVisible(false);
   }
  }

  public void run() {
   if (!Tornado.parts.isEmpty() && Tornado.movesRepairPoints) {
    speedX *= .995;
    speedY *= .995;
    speedZ *= .995;
    speedX += U.random(125.) * Double.compare(Tornado.parts.get(0).X, X);
    speedZ += U.random(125.) * Double.compare(Tornado.parts.get(0).Z, Z);
    speedY += Y < 0 ? U.random(125.) : 0;
    speedY -= Y > Tornado.parts.get(Tornado.parts.size() - 1).Y ? U.random(125.) : 0;
    X += speedX;
    Z += speedZ;
    Y = Math.min(0, Y + speedY);
   }
   double depth = U.getDepth(this);
   if (depth > -absoluteRadius) {
    pulseSize = (pulseSize += absoluteRadius * .1 * U.tick) > absoluteRadius ? 0 : pulseSize;
    U.setScale(pulse, pulseSize);
    U.setTranslate(pulse, this);
    pulse.setVisible(true);
   } else {
    pulse.setVisible(false);
   }
   runShocks(depth);
  }

  private void runShocks(double depth) {
   for (var shock : shocks) {
    if (depth > -absoluteRadius) {
     U.randomRotate(shock);
     U.setTranslate(shock, this);
     shock.setVisible(true);
    } else {
     shock.setVisible(false);
    }
   }
  }
 }

 public static void notifyDuplicates() {
  if (!Tornado.movesRepairPoints) {
   for (var sourceRepair : instances) {
    for (var targetRepair : instances) {
     if (sourceRepair != targetRepair && sourceRepair.X == targetRepair.X && sourceRepair.Y == targetRepair.Y && sourceRepair.Z == targetRepair.Z) {
      System.out.println("DUPLICATE repair singularities detected! (" + Maps.name + ")");
     }
    }
   }
  }
 }
}
