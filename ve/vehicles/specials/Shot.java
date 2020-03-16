package ve.vehicles.specials;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import ve.effects.Effects;
import ve.environment.E;
import ve.instances.Core;
import ve.instances.CoreAdvanced;
import ve.instances.I;
import ve.trackElements.TE;
import ve.ui.Match;
import ve.utilities.Images;
import ve.utilities.Nodes;
import ve.utilities.Phong;
import ve.utilities.U;
import ve.utilities.sound.Sounds;
import ve.vehicles.Vehicle;
import ve.vehicles.explosions.Explosion;

public class Shot extends CoreAdvanced {
 private final Vehicle V;
 private final Special S;
 public final MeshView MV;
 private MeshView thrust;
 private double behindX;
 private double behindY;
 private double behindZ;
 public double stage;
 private double speed;
 private double sinXZ;
 private double cosXZ;
 private double sinYZ;
 private double cosYZ;//<-Performance optimization
 private double gravityDistance;
 private double homeYZ;
 private long hit;
 private boolean[] doneDamaging;
 private final double homingSteerSpeed;
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
   Nodes.add(thrust);//<-MV added with transparent Nodes (for flamethrower)
  }
  MV.setVisible(false);
  if (S.type == Special.Type.forcefield) {
   doneDamaging = new boolean[I.vehiclesInMatch];
  }
  homingSteerSpeed = S.useSmallHits ? 10 : 5;
 }

 void deploy(Port port, double V_sinXZ, double V_cosXZ, double V_sinYZ, double V_cosYZ, double V_sinXY, double V_cosXY) {
  boolean complexAim = S.aimType != Special.AimType.normal;
  double[] shotX = {port.X}, shotY = {port.Y}, shotZ = {port.Z};
  if (complexAim) {
   U.rotateWithPivot(shotZ, shotY, V.VT.pivotY, V.VT.pivotZ, V.VT.YZ);
   U.rotateWithPivot(shotX, shotZ, 0, V.VT.pivotZ, V.VT.XZ);
  }
  double setXZ = V.XZ + (complexAim ? V.VT.XZ : 0),
  setYZ = V.YZ - (complexAim ? V.VT.YZ : 0);
  U.rotate(shotX, shotY, V_sinXY, V_cosXY);
  U.rotate(shotY, shotZ, V_sinYZ, V_cosYZ);
  U.rotate(shotX, shotZ, V_sinXZ, V_cosXZ);
  X = (V.X + shotX[0]) + U.randomPlusMinus(S.randomPosition);
  Y = (V.Y + shotY[0]) + U.randomPlusMinus(S.randomPosition);
  Z = (V.Z + shotZ[0]) + U.randomPlusMinus(S.randomPosition);
  XZ = setXZ + (port.XZ * V_cosXY) + (port.YZ * V_sinXY) * V.P.polarity + U.randomPlusMinus(S.randomAngle);
  YZ = setYZ + (port.YZ * V_cosXY) + (port.XZ * V_sinXY) + U.randomPlusMinus(S.randomAngle);
  setAngleTable();//<-Right after angles are set above
  speed = S.type == Special.Type.mine ? 0 : S.speed + (V.P.speed * U.cos(Math.abs(V.XZ - XZ)));
  //Order here is XZ/YZ, speed, behinds. Behinds are gotten at the end, otherwise the wrong value will be reached!
  behindX = X + (speed * (sinXZ * cosYZ) * U.tick);
  behindY = Y + (speed * sinYZ * U.tick);//Don't mix up pre-calculated shot sin's with the VEHICLE's!
  behindZ = Z - (speed * (cosXZ * cosYZ) * U.tick);
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
      for (var FM : TE.mounds) {
       if (FM.objectInside(this)) {//fixme--Major lag source if there are lots of active shots and lots of mounds!
        hit = 1;
        break;
       }
      }
     }
    }
    if (hit < 1) {
     behindX = X;
     behindY = Y;
     behindZ = Z;
     //^behinds getting the last positions of the shot
     runHoming();
     if (stage != Double.MIN_VALUE) {//<-Prevents shot from 'surging' ahead of vehicle while moving. It may even help fast shots hit close-range targets better!
      X -= speed * sinXZ * cosYZ * U.tick;
      Z += speed * cosXZ * cosYZ * U.tick;
      Y -= speed * sinYZ * U.tick;
     }
     if (S.type != Special.Type.flamethrower) {
      stage++;
      if (S.type == Special.Type.bomb) {
       gravityDistance += E.gravity * U.tick;
       Y += gravityDistance;
      } else if (S.type == Special.Type.forcefield && stage > 2) {
       stage = 0;
      }
     } else if ((stage += U.tick) > 50) {
      stage = 0;
     }
    } else if (hit == 1) {
     if (S.type == Special.Type.shell || S.type == Special.Type.missile || S.type == Special.Type.bomb) {
      V.explosions.get(V.currentExplosion).deploy(X, Y, Z, null);
      V.currentExplosion = ++V.currentExplosion >= Explosion.defaultQuantity ? 0 : V.currentExplosion;
      V.VA.hitExplosive.play(Double.NaN, Math.sqrt(U.distance(this)) * Sounds.standardGain(1));
     } else if (S.type == Special.Type.powershell || S.type == Special.Type.mine) {
      for (int i = 6; --i >= 0; ) {
       V.explosions.get(V.currentExplosion).deploy(((X + behindX) * .5) + U.randomPlusMinus(2000.), ((Y + behindY) * .5) + U.randomPlusMinus(2000.), ((Z + behindZ) * .5) + U.randomPlusMinus(2000.), null);
       V.currentExplosion = ++V.currentExplosion >= Explosion.defaultQuantity ? 0 : V.currentExplosion;
      }
      double shotToCameraSoundDistance = Math.sqrt(U.distance(this)) * Sounds.standardGain(1);
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
    U.setTranslate(thrust, X + size * sinXZ * cosYZ, Y + size * sinYZ, Z - size * cosXZ * cosYZ);
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

 private void setAngleTable() {
  sinXZ = U.sin(XZ);
  cosXZ = U.cos(XZ);
  sinYZ = U.sin(YZ);
  cosYZ = U.cos(YZ);
 }

 private void runHoming() {
  if (S.homing) {
   int shotTarget = I.userPlayerIndex;
   double compareDistance = Double.POSITIVE_INFINITY;
   for (var vehicle : I.vehicles) {
    if (!I.sameTeam(V, vehicle) && !vehicle.destroyed && U.distance(this, vehicle) < compareDistance) {
     shotTarget = vehicle.index;
     compareDistance = U.distance(this, vehicle);
    }
   }
   Vehicle targetV = I.vehicles.get(shotTarget);
   double homeXZ = (targetV.X < X ? 90 : targetV.X > X ? -90 : 0) + U.arcTan((targetV.Z - Z) / (targetV.X - X));
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
   setAngleTable();
  }
 }

 public void vehicleInteract(Vehicle vehicle, boolean throughWeapon, boolean replay, boolean greenTeam) {
  if (stage > 0 && hit < 1 && (doneDamaging == null || !doneDamaging[vehicle.index]) && (S.type != Special.Type.missile || vehicle.isIntegral()) && !(S.type == Special.Type.mine && (U.distance(this, vehicle) > 2000 || !vehicle.isIntegral()))) {
   if (advancedCollisionCheck(vehicle, (S.type == Special.Type.mine ? vehicle.P.netSpeed : S.diameter) + vehicle.collisionRadius)) {
    V.P.hitCheck(vehicle);
    double shotDamage = S.damageDealt;
    if (throughWeapon) {
     if (S.type == Special.Type.flamethrower) {
      shotDamage /= Math.max(1, stage);
     }
     shotDamage *= U.tick;
    } else if (S.type != Special.Type.forcefield) {
     hit = 1;
    }
    vehicle.addDamage(shotDamage);
    if (vehicle.isIntegral() && !replay) {
     Match.scoreDamage[greenTeam ? 0 : 1] += shotDamage;
     if (vehicle.index != I.userPlayerIndex && U.distance(vehicle, V) < U.distance(vehicle, I.vehicles.get(vehicle.AI.target))) {
      vehicle.AI.target = V.index;
     }
    }
    if (S.pushPower > 0) {
     if (vehicle.getsPushed >= 0) {
      vehicle.speedX += U.randomPlusMinus(S.pushPower);
      vehicle.speedZ += U.randomPlusMinus(S.pushPower);
     }
     if (vehicle.getsLifted >= 0 && (S.type == Special.Type.forcefield || S.type == Special.Type.missile || S.type == Special.Type.mine || U.contains(S.type.name(), Special.Type.shell.name()))) {
      vehicle.speedY += U.randomPlusMinus(S.pushPower);
     }
    }
    vehicle.deformParts();
    vehicle.throwChips(speed, true);
    double shotToCameraSoundDistance = Math.sqrt(U.distance(this)) * Sounds.standardGain(1);
    if (S.useSmallHits) {
     V.VA.hitShot.play(Double.NaN, shotToCameraSoundDistance);
    }
    if (S.type == Special.Type.heavymachinegun || S.type == Special.Type.blaster) {
     V.VA.hitShot.play(U.random(7), shotToCameraSoundDistance);//<-May not call effectively on Linux, but it's not so important since there's the default call above anyway
    } else if (S.type == Special.Type.heavyblaster || S.type == Special.Type.thewrath) {//<-These specials don't load 'hitExplosive' audio, so don't call!
     V.VA.crashDestroy.play(Double.NaN, shotToCameraSoundDistance);
    } else if (S.type.name().contains(Special.Type.shell.name()) || S.type == Special.Type.missile || S.type == Special.Type.bomb) {
     V.VA.crashDestroy.play(Double.NaN, shotToCameraSoundDistance);
     V.VA.hitExplosive.play(Double.NaN, shotToCameraSoundDistance);
    } else if (S.type == Special.Type.railgun) {
     for (int n = 4; --n >= 0; ) {
      V.VA.crashHard.play(Double.NaN, shotToCameraSoundDistance);
     }
    } else if (S.type == Special.Type.forcefield) {
     V.VA.crashHard.play(Double.NaN, V.VA.distanceVehicleToCamera);
     V.VA.crashHard.play(Double.NaN, V.VA.distanceVehicleToCamera);
     V.VA.crashHard.play(Double.NaN, V.VA.distanceVehicleToCamera);
    } else if (S.type == Special.Type.mine) {
     V.VA.mineExplosion.play(shotToCameraSoundDistance);
    }
    if (U.random() < .25 && S.ricochets) {
     V.VA.hitRicochet.play(Double.NaN, shotToCameraSoundDistance);
    }
    if (doneDamaging != null) {
     doneDamaging[vehicle.index] = true;
    }
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
