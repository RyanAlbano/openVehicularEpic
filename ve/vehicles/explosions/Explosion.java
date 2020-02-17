package ve.vehicles.explosions;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;

import java.util.*;

import ve.effects.Effects;
import ve.instances.Core;
import ve.instances.I;
import ve.ui.Match;
import ve.utilities.Nodes;
import ve.utilities.Phong;
import ve.utilities.U;
import ve.vehicles.*;

public class Explosion extends Core {

 private final Vehicle V;
 public Vehicle focusVehicle;
 private final boolean nuclear;
 private final MeshView MV;
 public double inX;
 public double inY;
 public double inZ;
 public double stage;
 private final boolean[] doneDamaging;
 private final Collection<ExplosionPart> explosionParts = new ArrayList<>();
 public static final long defaultQuantity = 12;

 public Explosion(Vehicle vehicle) {
  V = vehicle;
  nuclear = V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name());
  absoluteRadius = nuclear ? 20000 : 1500;
  TriangleMesh TM = new TriangleMesh();
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
  Phong.setDiffuseRGB(PM, 0);
  Phong.setSpecularRGB(PM, 0);
  U.setMaterialSecurely(MV, PM);
  Nodes.add(MV);
  MV.setVisible(false);
  doneDamaging = new boolean[I.vehiclesInMatch];
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
  for (int n = I.vehiclesInMatch; --n >= 0; ) {
   doneDamaging[n] = false;
  }
  for (var explosionPart : explosionParts) {
   explosionPart.deploy();
  }
 }

 public void run(boolean gamePlay) {
  if (stage > 0) {
   if ((stage += gamePlay ? U.tick : 0) > 5) {
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
    if (U.render(this, -absoluteRadius, false, false)) {
     U.setTranslate(MV, this);
     U.randomRotate(MV);
     ((PhongMaterial) MV.getMaterial()).setSelfIlluminationMap(Effects.fireLight());
     MV.setVisible(true);
    } else {
     MV.setVisible(false);
    }
   }
  }
  for (var explosionPart : explosionParts) {
   explosionPart.run(gamePlay);
  }
 }

 public void vehicleInteract(Vehicle vehicle, boolean replay, boolean greenTeam) {
  if (stage > 0 && U.distance(this, vehicle) < vehicle.collisionRadius + V.P.explosionDiameter) {
   if (!doneDamaging[vehicle.index]) {
    V.P.hitCheck(vehicle);
    vehicle.addDamage(V.P.explosionDamage);
    if (!nuclear) {
     if (vehicle.isIntegral() && !replay) {
      Match.scoreDamage[greenTeam ? 0 : 1] += V.P.explosionDamage;
     }
     doneDamaging[vehicle.index] = true;
    }
   }
   if (vehicle.getsPushed >= 0) {
    vehicle.speedX += U.randomPlusMinus(V.P.explosionPush);
    vehicle.speedZ += U.randomPlusMinus(V.P.explosionPush);
   }
   if (vehicle.getsLifted >= 0) {
    vehicle.speedY += U.randomPlusMinus(V.P.explosionPush);
   }
   vehicle.deformParts();
   vehicle.throwChips(300, false);
   if (nuclear) {
    V.VA.crashDestroy.play(Double.NaN, V.VA.distanceVehicleToCamera);
   }
  }
 }
}
