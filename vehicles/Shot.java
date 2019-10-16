package ve.vehicles;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import ve.Core;
import ve.VE;
import ve.environment.E;
import ve.utilities.U;

public class Shot extends Core {

 private final MeshView MV;
 private MeshView thrust;
 double behindX, behindY, behindZ;
 double stage;
 double speed;
 private double gravityDistance;
 double homeXZ, homeYZ;
 long hit;
 boolean[] doneDamaging;

 Shot(Special special) {
  TriangleMesh shotMesh = new TriangleMesh();
  shotMesh.getTexCoords().setAll(0, 0);
  if (special.type == Vehicle.specialType.flamethrower || special.type.name().contains(Vehicle.specialType.blaster.name()) || special.type == Vehicle.specialType.forcefield || special.type == Vehicle.specialType.thewrath) {
   shotMesh.getPoints().setAll(
   (float) U.randomPlusMinus(special.width), (float) U.randomPlusMinus(special.width), (float) U.randomPlusMinus(special.width),
   (float) U.randomPlusMinus(special.width), (float) U.randomPlusMinus(special.width), (float) U.randomPlusMinus(special.width),
   (float) U.randomPlusMinus(special.width), (float) U.randomPlusMinus(special.width), (float) U.randomPlusMinus(special.width),
   (float) U.randomPlusMinus(special.width), (float) U.randomPlusMinus(special.width), (float) U.randomPlusMinus(special.width),
   (float) U.randomPlusMinus(special.width), (float) U.randomPlusMinus(special.width), (float) U.randomPlusMinus(special.width),
   (float) U.randomPlusMinus(special.width), (float) U.randomPlusMinus(special.width), (float) U.randomPlusMinus(special.width));
   shotMesh.getFaces().setAll(
   0, 0, 1, 0, 2, 0,
   3, 0, 4, 0, 5, 0);
  } else if (special.type == Vehicle.specialType.mine) {
   shotMesh.getPoints().setAll(0, (float) -special.length, 0,
   0, 0, (float) -special.width,
   (float) -special.width, 0, 0,
   (float) special.width, 0, 0,
   0, 0, (float) special.width);
   shotMesh.getFaces().setAll(
   0, 0, 2, 0, 1, 0,
   0, 0, 1, 0, 3, 0,
   0, 0, 3, 0, 4, 0,
   0, 0, 4, 0, 2, 0,
   4, 0, 1, 0, 2, 0,
   4, 0, 3, 0, 1, 0);
  } else {
   shotMesh.getPoints().setAll(0, 0, (float) special.length,
   0, (float) -special.width, (float) -special.length,
   (float) -special.width, 0, (float) -special.length,
   (float) special.width, 0, (float) -special.length,
   0, (float) special.width, (float) -special.length);
   shotMesh.getFaces().setAll(
   0, 0, 2, 0, 1, 0,
   0, 0, 1, 0, 3, 0,
   0, 0, 3, 0, 4, 0,
   0, 0, 4, 0, 2, 0);
  }
  MV = new MeshView(shotMesh);
  MV.setCullFace(CullFace.NONE);
  PhongMaterial PM = new PhongMaterial();
  if (special.type == Vehicle.specialType.raygun || special.type.name().contains(Vehicle.specialType.blaster.name()) || special.type == Vehicle.specialType.forcefield || special.type == Vehicle.specialType.thewrath) {
   U.setSpecularRGB(PM, 0, 0, 0);
   PM.setSelfIlluminationMap(U.getImage("white"));
  } else {
   U.setDiffuseRGB(PM, .5, .5, .5);
   U.setSpecularRGB(PM, 1, 1, 1);
  }
  PM.setSpecularPower(special.type == Vehicle.specialType.flamethrower ? 0 : 10);
  MV.setMaterial(PM);
  if (special.hasThrust) {
   TriangleMesh thrustMesh = new TriangleMesh();
   thrustMesh.getTexCoords().setAll(0, 0);
   double size = special.length + special.width;
   thrustMesh.getPoints().setAll(
   (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size),
   (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size),
   (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size),
   (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size),
   (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size),
   (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size));
   thrustMesh.getFaces().setAll(
   0, 0, 1, 0, 2, 0,
   3, 0, 4, 0, 5, 0);
   thrust = new MeshView(thrustMesh);
   thrust.setCullFace(CullFace.NONE);
   thrust.setMaterial(new PhongMaterial());
   thrust.setVisible(false);
  }
  U.add(MV, thrust);
  MV.setVisible(false);
  if (special.type == Vehicle.specialType.forcefield) {
   doneDamaging = new boolean[VE.vehiclesInMatch];
  }
 }

 void deploy(Vehicle V, Special special, Port port) {
  boolean complexAim = special.aimType != Special.AimType.normal;
  double[] shotX = {port.X}, shotY = {port.Y}, shotZ = {port.Z};
  if (complexAim) {
   U.rotateWithPivot(shotZ, shotY, V.vehicleTurretPivotY, V.vehicleTurretPivotZ, V.vehicleTurretYZ);
   U.rotateWithPivot(shotX, shotZ, 0, V.vehicleTurretPivotZ, V.vehicleTurretXZ);
  }
  double setXZ = V.XZ + (complexAim ? V.vehicleTurretXZ : 0),
  setYZ = V.YZ - (complexAim ? V.vehicleTurretYZ : 0);
  U.rotate(shotX, shotY, V.XY);
  U.rotate(shotY, shotZ, V.YZ);
  U.rotate(shotX, shotZ, V.XZ);
  X = (V.X + shotX[0]) + U.randomPlusMinus(special.randomPosition);
  Y = (V.Y + shotY[0]) + U.randomPlusMinus(special.randomPosition);
  Z = (V.Z + shotZ[0]) + U.randomPlusMinus(special.randomPosition);
  behindX = X + (speed * (U.sin(XZ) * U.cos(YZ)) * VE.tick);
  behindY = Y + (speed * U.sin(YZ) * VE.tick);
  behindZ = Z - (speed * (U.cos(XZ) * U.cos(YZ)) * VE.tick);
  XZ = setXZ + (port.XZ * U.cos(V.XY)) + (port.YZ * U.sin(V.XY)) * V.polarity + U.randomPlusMinus(special.randomAngle);
  YZ = setYZ + (port.YZ * U.cos(V.XY)) + (port.XZ * U.sin(V.XY)) + U.randomPlusMinus(special.randomAngle);
  speed = special.type == Vehicle.specialType.mine ? 0 : special.speed + (V.speed * U.cos(Math.abs(V.XZ - XZ)));
  gravityDistance = hit = 0;
  U.rotate(MV, -YZ, XZ);
  stage = Double.MIN_VALUE;
  if (doneDamaging != null) {
   for (int n = doneDamaging.length; --n >= 0; ) {
    doneDamaging[n] = false;
   }
  }
 }

 void run(Vehicle V, Special special, boolean gamePlay) {
  if (stage > 0) {
   if (gamePlay) {
    hit = hit < 1 && special.type != Vehicle.specialType.mine && U.outOfBounds(this, 500) ? 1 : hit;
    if (hit < 1) {
     if (special.type == Vehicle.specialType.flamethrower && (stage += VE.tick) > 50) {
      stage = 0;
     }
     if (stage > 0) {
      behindX = X;
      behindY = Y;
      behindZ = Z;
     }
     X -= speed * (U.sin(XZ) * U.cos(YZ)) * VE.tick;
     Z += speed * (U.cos(XZ) * U.cos(YZ)) * VE.tick;
     Y -= speed * U.sin(YZ) * VE.tick;
     if (special.type != Vehicle.specialType.flamethrower) {
      stage++;
      if (special.type == Vehicle.specialType.bomb) {
       gravityDistance += E.gravity * VE.tick;
       Y += gravityDistance;
      } else if (special.type == Vehicle.specialType.forcefield && stage > 2) {
       stage = 0;
      }
     }
    } else if (hit == 1) {
     if (special.type == Vehicle.specialType.shell || special.type == Vehicle.specialType.missile || special.type == Vehicle.specialType.bomb) {
      V.explosions.get(V.currentExplosion).deploy(X, Y, Z, null);
      V.currentExplosion = ++V.currentExplosion >= E.explosionQuantity ? 0 : V.currentExplosion;
      V.VA.hitExplosive.play(U.random(V.VA.hitExplosive.clips.size()), Math.sqrt(U.distance(this)) * .08);
     } else if (special.type == Vehicle.specialType.powershell || special.type == Vehicle.specialType.mine) {
      for (int i = 6; --i >= 0; ) {
       V.explosions.get(V.currentExplosion).deploy(((X + behindX) * .5) + U.randomPlusMinus(2000.), ((Y + behindY) * .5) + U.randomPlusMinus(2000.), ((Z + behindZ) * .5) + U.randomPlusMinus(2000.), null);
       V.currentExplosion = ++V.currentExplosion >= E.explosionQuantity ? 0 : V.currentExplosion;
      }
      double shotToCameraSoundDistance = Math.sqrt(U.distance(this)) * .08;
      V.VA.hitExplosive.play(U.random(V.VA.hitExplosive.clips.size()), shotToCameraSoundDistance);
      V.VA.hitExplosive.play(U.random(V.VA.hitExplosive.clips.size()), shotToCameraSoundDistance);
      V.VA.hitExplosive.play(U.random(V.VA.hitExplosive.clips.size()), shotToCameraSoundDistance);
     }
    }
   }
   if (hit > 0) {
    stage = ++hit > 2 ? 0 : stage;
   }
  }
 }

 public void render(Special special) {
  if (stage > 0 && U.render(this)) {
   if (special.type == Vehicle.specialType.flamethrower) {
    U.setScale(MV, 5 + stage * 2);
    double r = Math.max(0, 1 - (stage * .015625)),
    g = Math.max(0, 1 - (stage * .03125)),
    b = Math.max(0, 1 - (stage * .0625));
    U.setDiffuseRGB((PhongMaterial) MV.getMaterial(), r, g, b);
    U.setSpecularRGB((PhongMaterial) MV.getMaterial(), r, g, b);
   }
   U.setTranslate(MV, this);
   if (special.type == Vehicle.specialType.flamethrower || special.type == Vehicle.specialType.forcefield || special.type == Vehicle.specialType.thewrath || special.type.name().contains(Vehicle.specialType.blaster.name())) {
    U.randomRotate(MV);
   } else if (special.homing) {
    U.rotate(MV, -YZ, XZ);
   }
   MV.setVisible(true);
   if (special.hasThrust) {
    double size = special.length + special.width;
    U.setTranslate(thrust, X + size * U.sin(XZ) * U.cos(YZ), Y + size * U.sin(YZ), Z - size * U.cos(XZ) * U.cos(YZ));
    U.randomRotate(thrust);
    U.setDiffuseRGB((PhongMaterial) thrust.getMaterial(), 0, 0, 0);
    U.setSpecularRGB((PhongMaterial) thrust.getMaterial(), 0, 0, 0);
    ((PhongMaterial) thrust.getMaterial()).setSelfIlluminationMap(U.getImage("firelight" + U.random(3)));
   }
  } else {
   MV.setVisible(false);
  }
  if (special.hasThrust) {
   thrust.setVisible(MV.isVisible());
  }
 }
}
