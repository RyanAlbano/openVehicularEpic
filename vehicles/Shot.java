package ve.vehicles;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import ve.VE;
import ve.environment.E;
import ve.utilities.U;

class Shot extends MeshView {

 private MeshView thrust;
 double X, Y, Z;
 double behindX, behindY, behindZ;
 double XZ, YZ;
 double stage;
 double speed;
 private double gravityDistance;
 double homeXZ;
 double homeYZ;
 long hit;
 boolean[] doneDamaging;

 Shot(Special special) {
  TriangleMesh shotMesh = new TriangleMesh();
  shotMesh.getTexCoords().setAll(0, 0);
  if (U.startsWith(special.type, "forcefield", "flamethrower") || special.type.contains("blaster") || special.type.startsWith("thewrath")) {
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
  } else if (special.type.startsWith("mine")) {
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
  setMesh(shotMesh);
  setCullFace(CullFace.NONE);
  PhongMaterial PM = new PhongMaterial();
  if (special.type.startsWith("raygun") || special.type.contains("blaster") || special.type.startsWith("forcefield") || special.type.startsWith("thewrath")) {
   U.setSpecularRGB(PM, 0, 0, 0);
   PM.setSelfIlluminationMap(U.getImage("white"));
  } else {
   U.setDiffuseRGB(PM, .5, .5, .5);
   U.setSpecularRGB(PM, 1, 1, 1);
  }
  PM.setSpecularPower(special.type.startsWith("flamethrower") ? 0 : 10);
  setMaterial(PM);
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
  U.add(this, thrust);
  setVisible(false);
  if (special.type.startsWith("forcefield")) {
   doneDamaging = new boolean[VE.vehiclesInMatch];
  }
 }

 void deploy(Vehicle V, Special special, Port port) {
  double[] shotX = {port.X}, shotY = {port.Y}, shotZ = {port.Z};
  U.rotate(shotX, shotY, V.XY);
  U.rotate(shotY, shotZ, V.YZ);
  U.rotate(shotX, shotZ, V.XZ);
  X = (V.X + shotX[0]) + U.randomPlusMinus(special.randomPosition);
  Y = (V.Y + shotY[0]) + U.randomPlusMinus(special.randomPosition);
  Z = (V.Z + shotZ[0]) + U.randomPlusMinus(special.randomPosition);
  behindX = X;
  behindY = Y;
  behindZ = Z;
  behindX += speed * (U.sin(XZ) * U.cos(YZ)) * VE.tick;
  behindZ -= speed * (U.cos(XZ) * U.cos(YZ)) * VE.tick;
  behindY += speed * U.sin(YZ) * VE.tick;
  XZ = V.XZ + (port.XZ * U.cos(V.XY)) + (port.YZ * U.sin(V.XY)) * V.polarity + U.randomPlusMinus(special.randomAngle);
  YZ = V.YZ + (port.YZ * U.cos(V.XY)) + (port.XZ * U.sin(V.XY)) + U.randomPlusMinus(special.randomAngle);
  speed = special.type.startsWith("mine") ? 0 : special.speed + (V.speed * U.cos(Math.abs(V.XZ - XZ)));
  gravityDistance = hit = 0;
  U.rotate(this, -YZ, XZ);
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
    hit = hit < 1 && !special.type.startsWith("mine") && U.outOfBounds(X, Y, Z, 500) ? 1 : hit;
    if (hit < 1) {
     if (special.type.startsWith("flamethrower")) {
      if ((stage += VE.tick) > 50) {
       stage = 0;
      } else {
       U.setScale(this, 5 + stage * 2);
       double r = Math.max(0, 1 - (stage * .015625)),
       g = Math.max(0, 1 - (stage * .03125)),
       b = Math.max(0, 1 - (stage * .0625));
       U.setDiffuseRGB((PhongMaterial) getMaterial(), r, g, b);
       U.setSpecularRGB((PhongMaterial) getMaterial(), r, g, b);
      }
     }
     if (stage > 0) {
      behindX = X;
      behindY = Y;
      behindZ = Z;
     }
     X -= speed * (U.sin(XZ) * U.cos(YZ)) * VE.tick;
     Z += speed * (U.cos(XZ) * U.cos(YZ)) * VE.tick;
     Y -= speed * U.sin(YZ) * VE.tick;
     if (!special.type.startsWith("flamethrower")) {
      stage++;
      if (special.type.startsWith("bomb")) {
       gravityDistance += E.gravity * VE.tick;
       Y += gravityDistance;
      } else if (special.type.startsWith("forcefield") && stage > 2) {
       stage = 0;
      }
     }
    } else if (hit == 1) {
     if (U.startsWith(special.type, "shell", "bomb") || special.type.contains("missile")) {
      V.explosions.get(V.currentExplosion).deploy(X, Y, Z, false);
      V.currentExplosion = ++V.currentExplosion >= VE.explosionQuantity ? 0 : V.currentExplosion;
      U.soundPlay(V.sounds, "hugeHit" + U.random(7), Math.sqrt(U.distance(X, VE.cameraX, Y, VE.cameraY, Z, VE.cameraZ)) * .08);
     } else if (U.startsWith(special.type, "powershell", "mine")) {
      for (int i = 6; --i >= 0; ) {
       V.explosions.get(V.currentExplosion).deploy(((X + behindX) * .5) + U.randomPlusMinus(2000.), ((Y + behindY) * .5) + U.randomPlusMinus(2000.), ((Z + behindZ) * .5) + U.randomPlusMinus(2000.), false);
       V.currentExplosion = ++V.currentExplosion >= VE.explosionQuantity ? 0 : V.currentExplosion;
      }
      double shotToCameraSoundDistance = Math.sqrt(U.distance(X, VE.cameraX, Y, VE.cameraY, Z, VE.cameraZ)) * .08;
      U.soundPlay(V.sounds, "hugeHit" + U.random(7), shotToCameraSoundDistance);
      U.soundPlay(V.sounds, "hugeHit" + U.random(7), shotToCameraSoundDistance);
      U.soundPlay(V.sounds, "hugeHit" + U.random(7), shotToCameraSoundDistance);
     }
    }
   }
   if (hit > 0) {
    stage = ++hit > 2 ? 0 : stage;
   }
   boolean show = false;
   if (stage > 0 && U.getDepth(X, Y, Z) > 0) {
    U.setTranslate(this, X, Y, Z);
    if (U.startsWith(special.type, "flamethrower", "forcefield", "thewrath") || special.type.contains("blaster")) {
     U.randomRotate(this);
    } else if (special.homing) {
     U.rotate(this, -YZ, XZ);
    }
    show = true;
   }
   setVisible(show);
   if (special.hasThrust) {
    if (show) {
     double size = special.length + special.width;
     U.setTranslate(thrust, X + size * U.sin(XZ) * U.cos(YZ), Y + size * U.sin(YZ), Z - size * U.cos(XZ) * U.cos(YZ));
     U.randomRotate(thrust);
     U.setDiffuseRGB((PhongMaterial) thrust.getMaterial(), 0, 0, 0);
     U.setSpecularRGB((PhongMaterial) thrust.getMaterial(), 0, 0, 0);
     ((PhongMaterial) thrust.getMaterial()).setSelfIlluminationMap(U.getImage("firelight" + U.random(3)));
     thrust.setVisible(true);
    } else {
     thrust.setVisible(false);
    }
   }
  }
 }
}
