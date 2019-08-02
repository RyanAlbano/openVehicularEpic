package ve.effects;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;

import java.util.*;

import ve.*;
import ve.utilities.U;
import ve.vehicles.*;

public class Explosion extends MeshView {

 public double X;
 public double Y;
 public double Z;
 public double stage;
 final double radius;
 public final boolean[] doneDamaging;
 Vehicle focusVehicle;
 private final List<ExplosionPart> explosionParts = new ArrayList<>();

 public Explosion(Vehicle vehicle) {
  TriangleMesh TM = new TriangleMesh();
  radius = vehicle.explosionType.contains("nuclear") ? 20000 : 1500;
  TM.getPoints().setAll(
  (float) U.randomPlusMinus(radius), (float) U.randomPlusMinus(radius), (float) U.randomPlusMinus(radius),
  (float) U.randomPlusMinus(radius), (float) U.randomPlusMinus(radius), (float) U.randomPlusMinus(radius),
  (float) U.randomPlusMinus(radius), (float) U.randomPlusMinus(radius), (float) U.randomPlusMinus(radius),
  (float) U.randomPlusMinus(radius), (float) U.randomPlusMinus(radius), (float) U.randomPlusMinus(radius),
  (float) U.randomPlusMinus(radius), (float) U.randomPlusMinus(radius), (float) U.randomPlusMinus(radius),
  (float) U.randomPlusMinus(radius), (float) U.randomPlusMinus(radius), (float) U.randomPlusMinus(radius));
  TM.getTexCoords().setAll(0, 0);
  TM.getFaces().setAll(
  0, 0, 1, 0, 2, 0,
  3, 0, 4, 0, 5, 0);
  setMesh(TM);
  setCullFace(CullFace.NONE);
  PhongMaterial PM = new PhongMaterial();
  U.setDiffuseRGB(PM, 0, 0, 0);
  U.setSpecularRGB(PM, 0, 0, 0);
  setMaterial(PM);
  U.add(this);
  setVisible(false);
  doneDamaging = new boolean[VE.vehiclesInMatch];
  for (int n = VE.explosionQuantity; --n >= 0; ) {
   explosionParts.add(new ExplosionPart(this));
  }
 }

 public void deploy(double x, double y, double z, Vehicle vehicle) {//<-'vehicle' not always the explosion's parent--can be null!
  X = x;
  Y = y;
  Z = z;
  focusVehicle = vehicle;
  stage = Double.MIN_VALUE;
  for (int n = VE.vehiclesInMatch; --n >= 0; ) {
   doneDamaging[n] = false;
  }
  for (ExplosionPart explosionPart : explosionParts) {
   explosionPart.deploy();
  }
 }

 public void run(boolean gamePlay) {
  if (stage > 0) {
   if ((stage += gamePlay ? VE.tick : 0) > 5) {
    stage = 0;
    setVisible(false);
   } else {
    double setX, setY, setZ;
    if (focusVehicle != null) {
     setX = X + focusVehicle.X;
     setY = Y + focusVehicle.Y;
     setZ = Z + focusVehicle.Z;
    } else {
     setX = X;
     setY = Y;
     setZ = Z;
    }
    if (U.getDepth(setX, setY, setZ) > -radius) {
     U.setTranslate(this, setX, setY, setZ);
     U.randomRotate(this);
     ((PhongMaterial) getMaterial()).setSelfIlluminationMap(U.getImage("firelight" + U.random(3)));
     setVisible(true);
    } else {
     setVisible(false);
    }
   }
  }
  for (ExplosionPart explosionPart : explosionParts) {
   explosionPart.run(this, gamePlay);
  }
 }
}
