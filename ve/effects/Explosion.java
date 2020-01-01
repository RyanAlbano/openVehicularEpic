package ve.effects;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;

import java.util.*;

import ve.instances.Core;
import ve.ui.UI;
import ve.utilities.U;
import ve.vehicles.*;

public class Explosion extends Core {

 private final MeshView MV;
 double inX, inY, inZ;
 public double stage;
 public final boolean[] doneDamaging;
 Vehicle focusVehicle;
 private final Collection<ExplosionPart> explosionParts = new ArrayList<>();
 public static final long defaultQuantity = 12;

 public Explosion(Vehicle vehicle) {
  TriangleMesh TM = new TriangleMesh();
  absoluteRadius = vehicle.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name()) ? 20000 : 1500;
  TM.getPoints().setAll(
  (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius),
  (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius),
  (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius),
  (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius),
  (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius),
  (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius));
  TM.getTexCoords().setAll(0, 0);
  TM.getFaces().setAll(
  0, 0, 1, 0, 2, 0,
  3, 0, 4, 0, 5, 0);
  MV = new MeshView(TM);
  MV.setCullFace(CullFace.NONE);
  PhongMaterial PM = new PhongMaterial();
  U.Phong.setDiffuseRGB(PM, 0);
  U.Phong.setSpecularRGB(PM, 0);
  U.setMaterialSecurely(MV, PM);
  U.Nodes.add(MV);
  MV.setVisible(false);
  doneDamaging = new boolean[UI.vehiclesInMatch];
  for (long n = defaultQuantity; --n >= 0; ) {
   explosionParts.add(new ExplosionPart(this));
  }
 }

 public void deploy(double x, double y, double z, Vehicle vehicle) {//<-'vehicle' not always the explosion's parent--can be null
  inX = x;
  inY = y;
  inZ = z;
  focusVehicle = vehicle;
  stage = Double.MIN_VALUE;
  for (int n = UI.vehiclesInMatch; --n >= 0; ) {
   doneDamaging[n] = false;
  }
  for (ExplosionPart explosionPart : explosionParts) {
   explosionPart.deploy();
  }
 }

 public void run(boolean gamePlay) {
  if (stage > 0) {
   if ((stage += gamePlay ? UI.tick : 0) > 5) {
    stage = 0;
    MV.setVisible(false);
   } else {
    if (focusVehicle != null) {
     X = inX + focusVehicle.X;
     Y = inY + focusVehicle.Y;
     Z = inZ + focusVehicle.Z;
    } else {
     X = inX;
     Y = inY;
     Z = inZ;
    }
    if (U.render(this, -absoluteRadius)) {
     U.setTranslate(MV, this);
     U.randomRotate(MV);
     ((PhongMaterial) MV.getMaterial()).setSelfIlluminationMap(Effects.fireLight());
     MV.setVisible(true);
    } else {
     MV.setVisible(false);
    }
   }
  }
  for (ExplosionPart explosionPart : explosionParts) {
   explosionPart.run(this, gamePlay);
  }
 }
}
