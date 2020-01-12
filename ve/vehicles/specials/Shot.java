package ve.vehicles.specials;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import ve.effects.Effects;
import ve.environment.E;
import ve.environment.FrustumMound;
import ve.instances.Core;
import ve.instances.CoreAdvanced;
import ve.instances.I;
import ve.trackElements.TE;
import ve.utilities.*;
import ve.vehicles.Vehicle;
import ve.vehicles.explosions.Explosion;

public class Shot extends CoreAdvanced {
 final Vehicle V;
 final Special S;
 public final MeshView MV;
 private MeshView thrust;
 public double behindX, behindY, behindZ;
 public double stage, speed;
 private double gravityDistance;
 public double homeXZ, homeYZ;
 public long hit;
 public boolean[] doneDamaging;
 final double homingSteerSpeed;
 public static final long defaultQuantity = 96;

 Shot(Vehicle vehicle, Special special) {
  V = vehicle;
  S = special;
  TriangleMesh shotMesh = new TriangleMesh();
  shotMesh.getTexCoords().setAll(0, 0);
  if (S.type == Special.Type.flamethrower || S.type.name().contains(Special.Type.blaster.name()) || S.type == Special.Type.forcefield || S.type == Special.Type.thewrath) {
   shotMesh.getPoints().setAll(
   (float) U.randomPlusMinus(S.width), (float) U.randomPlusMinus(S.width), (float) U.randomPlusMinus(S.width),
   (float) U.randomPlusMinus(S.width), (float) U.randomPlusMinus(S.width), (float) U.randomPlusMinus(S.width),
   (float) U.randomPlusMinus(S.width), (float) U.randomPlusMinus(S.width), (float) U.randomPlusMinus(S.width),
   (float) U.randomPlusMinus(S.width), (float) U.randomPlusMinus(S.width), (float) U.randomPlusMinus(S.width),
   (float) U.randomPlusMinus(S.width), (float) U.randomPlusMinus(S.width), (float) U.randomPlusMinus(S.width),
   (float) U.randomPlusMinus(S.width), (float) U.randomPlusMinus(S.width), (float) U.randomPlusMinus(S.width));
   shotMesh.getFaces().setAll(
   0, 0, 1, 0, 2, 0,
   3, 0, 4, 0, 5, 0);
  } else if (S.type == Special.Type.mine) {
   shotMesh.getPoints().setAll(0, (float) -S.length, 0,
   0, 0, (float) -S.width,
   (float) -S.width, 0, 0,
   (float) S.width, 0, 0,
   0, 0, (float) S.width);
   shotMesh.getFaces().setAll(
   0, 0, 2, 0, 1, 0,
   0, 0, 1, 0, 3, 0,
   0, 0, 3, 0, 4, 0,
   0, 0, 4, 0, 2, 0,
   4, 0, 1, 0, 2, 0,
   4, 0, 3, 0, 1, 0);
  } else {
   shotMesh.getPoints().setAll(0, 0, (float) S.length,
   0, (float) -S.width, (float) -S.length,
   (float) -S.width, 0, (float) -S.length,
   (float) S.width, 0, (float) -S.length,
   0, (float) S.width, (float) -S.length);
   shotMesh.getFaces().setAll(
   0, 0, 2, 0, 1, 0,
   0, 0, 1, 0, 3, 0,
   0, 0, 3, 0, 4, 0,
   0, 0, 4, 0, 2, 0);
  }
  MV = new MeshView(shotMesh);
  MV.setCullFace(CullFace.NONE);
  PhongMaterial PM = new PhongMaterial();
  if (S.type == Special.Type.raygun || S.type.name().contains(Special.Type.blaster.name()) || S.type == Special.Type.forcefield || S.type == Special.Type.thewrath) {
   Phong.setSpecularRGB(PM, 0);
   PM.setSelfIlluminationMap(Images.white);
  } else {
   Phong.setDiffuseRGB(PM, .5);
   Phong.setSpecularRGB(PM, 1);//<-E.Specular.Shiny not called, since it's not really a 'shiny' object
  }
  PM.setSpecularPower(S.type == Special.Type.flamethrower ? 0 : E.Specular.Powers.standard);
  U.setMaterialSecurely(MV, PM);
  if (S.hasThrust) {
   TriangleMesh thrustMesh = new TriangleMesh();
   thrustMesh.getTexCoords().setAll(0, 0);
   double size = S.length + S.width;
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
   U.setMaterialSecurely(thrust, new PhongMaterial());
   thrust.setVisible(false);
  }
  Nodes.add(thrust);//<-MV added with transparent Nodes (for flamethrower)
  MV.setVisible(false);
  if (S.type == Special.Type.forcefield) {
   doneDamaging = new boolean[I.vehiclesInMatch];
  }
  homingSteerSpeed = S.useSmallHits ? 10 : 5;
 }

 void deploy(Port port) {
  boolean complexAim = S.aimType != Special.AimType.normal;
  double[] shotX = {port.X}, shotY = {port.Y}, shotZ = {port.Z};
  if (complexAim) {
   U.rotateWithPivot(shotZ, shotY, V.VT.pivotY, V.VT.pivotZ, V.VT.YZ);
   U.rotateWithPivot(shotX, shotZ, 0, V.VT.pivotZ, V.VT.XZ);
  }
  double setXZ = V.XZ + (complexAim ? V.VT.XZ : 0),
  setYZ = V.YZ - (complexAim ? V.VT.YZ : 0);
  U.rotate(shotX, shotY, V.XY);
  U.rotate(shotY, shotZ, V.YZ);
  U.rotate(shotX, shotZ, V.XZ);
  X = (V.X + shotX[0]) + U.randomPlusMinus(S.randomPosition);
  Y = (V.Y + shotY[0]) + U.randomPlusMinus(S.randomPosition);
  Z = (V.Z + shotZ[0]) + U.randomPlusMinus(S.randomPosition);
  behindX = X + (speed * (U.sin(XZ) * U.cos(YZ)) * U.tick);
  behindY = Y + (speed * U.sin(YZ) * U.tick);
  behindZ = Z - (speed * (U.cos(XZ) * U.cos(YZ)) * U.tick);
  XZ = setXZ + (port.XZ * U.cos(V.XY)) + (port.YZ * U.sin(V.XY)) * V.P.polarity + U.randomPlusMinus(S.randomAngle);
  YZ = setYZ + (port.YZ * U.cos(V.XY)) + (port.XZ * U.sin(V.XY)) + U.randomPlusMinus(S.randomAngle);
  speed = S.type == Special.Type.mine ? 0 : S.speed + (V.P.speed * U.cos(Math.abs(V.XZ - XZ)));
  gravityDistance = hit = 0;
  U.rotate(MV, -YZ, XZ);
  stage = Double.MIN_VALUE;
  if (doneDamaging != null) {
   for (int n = doneDamaging.length; --n >= 0; ) {
    doneDamaging[n] = false;
   }
  }
 }

 void runLogic(boolean gamePlay) {
  if (stage > 0) {
   if (gamePlay) {
    if (hit < 1 && S.type != Special.Type.mine) {
     if (U.outOfBounds(this, 500)) {
      hit = 1;
     } else {
      for (FrustumMound FM : TE.mounds) {
       if (FM.objectInside(this)) {
        hit = 1;
        break;
       }
      }
     }
    }
    if (hit < 1) {
     if (S.type == Special.Type.flamethrower && (stage += U.tick) > 50) {
      stage = 0;
     }
     behindX = X;
     behindY = Y;
     behindZ = Z;
     //^behinds getting the last positions of the shot
     runHoming();
     X -= speed * (U.sin(XZ) * U.cos(YZ)) * U.tick;
     Z += speed * (U.cos(XZ) * U.cos(YZ)) * U.tick;
     Y -= speed * U.sin(YZ) * U.tick;
     if (S.type != Special.Type.flamethrower) {
      stage++;
      if (S.type == Special.Type.bomb) {
       gravityDistance += E.gravity * U.tick;
       Y += gravityDistance;
      } else if (S.type == Special.Type.forcefield && stage > 2) {
       stage = 0;
      }
     }
    } else if (hit == 1) {
     if (S.type == Special.Type.shell || S.type == Special.Type.missile || S.type == Special.Type.bomb) {
      V.explosions.get(V.currentExplosion).deploy(X, Y, Z, null);
      V.currentExplosion = ++V.currentExplosion >= Explosion.defaultQuantity ? 0 : V.currentExplosion;
      V.VA.hitExplosive.play(Double.NaN, Math.sqrt(U.distance(this)) * Sound.standardDistance(1));
     } else if (S.type == Special.Type.powershell || S.type == Special.Type.mine) {
      for (int i = 6; --i >= 0; ) {
       V.explosions.get(V.currentExplosion).deploy(((X + behindX) * .5) + U.randomPlusMinus(2000.), ((Y + behindY) * .5) + U.randomPlusMinus(2000.), ((Z + behindZ) * .5) + U.randomPlusMinus(2000.), null);
       V.currentExplosion = ++V.currentExplosion >= Explosion.defaultQuantity ? 0 : V.currentExplosion;
      }
      double shotToCameraSoundDistance = Math.sqrt(U.distance(this)) * Sound.standardDistance(1);
      V.VA.hitExplosive.play(Double.NaN, shotToCameraSoundDistance);
      V.VA.hitExplosive.play(Double.NaN, shotToCameraSoundDistance);
      V.VA.hitExplosive.play(Double.NaN, shotToCameraSoundDistance);
     }
    }
   }
   if (hit > 0) {
    stage = ++hit > 2 ? 0 : stage;
   }
  }
 }

 public void runRender() {
  if (stage > 0 && U.render(this, false, true)) {
   if (S.type == Special.Type.flamethrower) {
    U.setScale(MV, 5 + stage * 2);
    double r = Math.max(0, 1 - (stage * .015625)),
    g = Math.max(0, 1 - (stage * .03125)),
    b = Math.max(0, 1 - (stage * .0625));
    Phong.setDiffuseRGB((PhongMaterial) MV.getMaterial(), r, g, b, .25);
    Phong.setSpecularRGB((PhongMaterial) MV.getMaterial(), r, g, b);
   }
   U.setTranslate(MV, this);
   if (S.type == Special.Type.flamethrower || S.type == Special.Type.forcefield || S.type == Special.Type.thewrath || S.type.name().contains(Special.Type.blaster.name())) {
    U.randomRotate(MV);
   } else if (S.homing) {
    U.rotate(MV, -YZ, XZ);
   }
   MV.setVisible(true);
   if (S.hasThrust) {
    double size = S.length + S.width;
    U.setTranslate(thrust, X + size * U.sin(XZ) * U.cos(YZ), Y + size * U.sin(YZ), Z - size * U.cos(XZ) * U.cos(YZ));
    U.randomRotate(thrust);
    Phong.setDiffuseRGB((PhongMaterial) thrust.getMaterial(), 0);
    Phong.setSpecularRGB((PhongMaterial) thrust.getMaterial(), 0);
    ((PhongMaterial) thrust.getMaterial()).setSelfIlluminationMap(Effects.fireLight());
   }
  } else {
   MV.setVisible(false);
  }
  if (S.hasThrust) {
   thrust.setVisible(MV.isVisible());
  }
 }

 void runHoming() {
  if (S.homing) {
   int shotTarget = I.userPlayerIndex;
   double compareDistance = Double.POSITIVE_INFINITY;
   for (Vehicle vehicle : I.vehicles) {
    if (!U.sameTeam(V, vehicle) && !vehicle.destroyed && U.distance(this, vehicle) < compareDistance) {
     shotTarget = vehicle.index;
     compareDistance = U.distance(this, vehicle);
    }
   }
   Vehicle targetV = I.vehicles.get(shotTarget);
   homeXZ = (targetV.X < X ? 90 : targetV.X > X ? -90 : 0) + U.arcTan((targetV.Z - Z) / (targetV.X - X));
   while (Math.abs(XZ - homeXZ) > 180) {
    homeXZ += homeXZ < XZ ? 360 : -360;
   }
   XZ += homingSteerSpeed * U.tick * Double.compare(homeXZ, XZ);
   double distance = U.netValue(targetV.Z - Z, targetV.X - X);
   homeYZ =
   targetV.Y < Y ? -(-90 - U.arcTan(distance / (targetV.Y - Y))) :
   targetV.Y > Y ? -(90 - U.arcTan(distance / (targetV.Y - Y))) :
   homeYZ;
   if (homeYZ < YZ) {
    YZ -= homingSteerSpeed * U.tick;
   }
   if (homeYZ > YZ || Y > -S.diameter * .5 - (targetV.isFixed() ? targetV.turretBaseY : targetV.clearanceY)) {
    YZ += homingSteerSpeed * U.tick;
   }
  }
 }

 public boolean advancedCollisionCheck(Core stationary, double collideAt) {//<-Stationary core may not always be stationary in actuality, but good enough
  if (U.distance(this, stationary) < collideAt) {
   return true;
  }
  double
  averageX = (X + behindX) * .5,
  averageY = (Y + behindY) * .5,
  averageZ = (Z + behindZ) * .5;
  return
  ((U.distance(averageY, stationary.Y, averageZ, stationary.Z) < collideAt || U.distance(behindY, stationary.Y, behindZ, stationary.Z) < collideAt) && ((X > stationary.X && behindX < stationary.X) || (X < stationary.X && behindX > stationary.X))) ||//<-inBoundsX
  ((U.distance(averageX, stationary.X, averageY, stationary.Y) < collideAt || U.distance(behindX, stationary.X, behindY, stationary.Y) < collideAt) && ((Z > stationary.Z && behindZ < stationary.Z) || (Z < stationary.Z && behindZ > stationary.Z))) ||//<-inBoundsZ
  ((U.distance(averageX, stationary.X, averageZ, stationary.Z) < collideAt || U.distance(behindX, stationary.X, behindZ, stationary.Z) < collideAt) && ((Y > stationary.Y && behindY < stationary.Y) || (Y < stationary.Y && behindY > stationary.Y)));  //<-inBoundsY
 }
}
