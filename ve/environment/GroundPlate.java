package ve.environment;

import javafx.scene.shape.Cylinder;

import javafx.scene.transform.Rotate;
import ve.Camera;
import ve.VE;
import ve.utilities.SL;
import ve.utilities.U;

import java.util.ArrayList;
import java.util.List;

enum GroundPlate {
 ;
 public static final List<Instance> instances = new ArrayList<>();

 static void load(String terrain) {
  if (E.Ground.level <= 0 && !terrain.contains(SL.Thick(SL.snow))) {
   for (int n = 0; n < 419; n++) {//<-'419' is the minimum needed to have groundPlates cover the entire ground surface with NO gaps, before duplicates get removed
    instances.add(new Instance(VE.Map.name.equals("Epic Trip") ? 1500 : 1732.0508075688772935274463415059));
   }
   double baseX = -30000, baseZ = -30000;
   boolean shift = false;
   for (Instance groundPlate : instances) {
    groundPlate.X = baseX;
    groundPlate.Z = baseZ;
    baseZ += 3000;
    if (baseZ > 30000) {
     baseZ = -30000;
     shift = !shift;
     baseZ -= shift ? 1500 : 0;
     baseX += 2598.0762113533159402911695122588;
    }
    //double varyRGB = 1 + U.randomPlusMinus(.05);<-In case we decide using it again
    U.setMaterialSecurely(groundPlate, E.Terrain.universal);
    groundPlate.clampXZ();
    groundPlate.setRotationAxis(Rotate.Y_AXIS);
    groundPlate.setRotate(-30 + (60 * U.random(6)));//<-INT random for hex-rotation!
    U.Nodes.add(groundPlate);
   }
   for (int n = 0; n < instances.size(); n++) {//<-NO enhanced loop--ConcurrentModificationException will occur!
    instances.get(n).checkDuplicate();
   }
  }
 }

 static void run() {
  if (!instances.isEmpty()) {
   double radius = Pool.C[0].getRadius() - instances.get(0).getRadius();
   for (GroundPlate.Instance groundPlate : instances) {
    groundPlate.run(radius);
   }
  }
 }

 static class Instance extends Cylinder {

  double X, Z;

  Instance(double radius) {
   super(radius, 0, 6);
  }

  void run(double radius) {
   clampXZ();
   double y = Math.max(0, -Camera.Y * .005);
   if (y > Camera.Y && (!Pool.exists || U.distance(X, E.pool.X, Z, E.pool.Z) > radius) && U.render(X, y, Z, -getRadius())) {
    U.setTranslate(this, X, y, Z);
    setVisible(true);
   } else {
    setVisible(false);
   }
  }

  void clampXZ() {
   while (Math.abs(X - Camera.X) > 25980.762113533159402911695122588) {
    X += X > Camera.X ? -51961.524227066318805823390245176 : 51961.524227066318805823390245176;
   }
   while (Math.abs(Z - Camera.Z) > 30000) {
    Z += Z > Camera.Z ? -60000 : 60000;
   }
  }

  void checkDuplicate() {
   for (int n = 0; n < instances.size(); n++) {
    if (instances.get(n) != this && U.distance(X, instances.get(n).X, Z, instances.get(n).Z) < 2000) {
     U.Nodes.remove(instances.get(n));
     instances.remove(n);
     checkDuplicate();
    }
   }
  }
 }
}
