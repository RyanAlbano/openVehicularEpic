package ve.vehicles.explosions;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.effects.Effects;
import ve.instances.Core;
import ve.instances.CoreAdvanced;
import ve.instances.I;
import ve.ui.Match;
import ve.ui.UI;
import ve.utilities.*;
import ve.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.Collection;

public class MaxNukeBlast extends Core {//there's still a slight delay in the sphere/blast placement, but this is probably only due to the logic being run in runMiscellaneous()
 private final Vehicle V;
 private final Sphere sphere;
 private double sphereSize;
 boolean render;
 private final boolean[] othersBlasted = new boolean[I.vehiclesInMatch];
 private final Collection<BlastPart> parts = new ArrayList<>();
 static final double blastSpeed = 6000;

 public MaxNukeBlast(Vehicle vehicle) {
  V = vehicle;
  sphere = new Sphere(1);
  PhongMaterial nukeBlastPM = new PhongMaterial();//<-More details later
  U.setMaterialSecurely(sphere, nukeBlastPM);
  Nodes.add(sphere);
  sphere.setVisible(false);
  PhongMaterial partPM = new PhongMaterial();
  Phong.setSpecularRGB(partPM, 0);
  partPM.setSelfIlluminationMap(Images.white);
  for (int n = 1000; --n >= 0; ) {
   parts.add(new BlastPart(partPM));
  }
 }

 public void setSingularity() {
  sphereSize = 0;
  X = V.X;
  Y = V.Y;
  Z = V.Z;
  for (BlastPart nukeBlast : parts) {
   nukeBlast.X = X;
   nukeBlast.Y = Y;
   nukeBlast.Z = Z;
   nukeBlast.XZ = U.random(360.);
   nukeBlast.YZ = U.random(360.);
   nukeBlast.sinXZ = U.sin(nukeBlast.XZ);
   nukeBlast.cosXZ = U.cos(nukeBlast.XZ);
   nukeBlast.sinYZ = U.sin(nukeBlast.YZ);
   nukeBlast.cosYZ = U.cos(nukeBlast.YZ);
  }
 }

 public void runLogic(boolean gamePlay) {
  double speed = blastSpeed * U.tick;
  if (V.isIntegral()) {
   render = false;
  } else {
   render = true;
   if (gamePlay) {
    sphereSize += speed;
    U.setScale(sphere, sphereSize);
   }
  }
  if (!V.isIntegral() && gamePlay) {
   V.VA.nuke.loop(1, Math.sqrt(Math.abs(U.distance(this) - sphereSize)) * Sound.standardDistance(.5));
  } else {
   V.VA.nuke.stop(1);
  }
  ((PhongMaterial) sphere.getMaterial()).setSelfIlluminationMap(Effects.fireLight());
  for (BlastPart blastPart : parts) {
   blastPart.runLogic(gamePlay, speed);
  }
 }

 public void runRender() {
  if (render && U.render(this, -sphereSize, false, true)) {
   U.setTranslate(sphere, this);
   sphere.setVisible(true);
  } else {
   sphere.setVisible(false);
  }
  ((PhongMaterial) sphere.getMaterial()).setSelfIlluminationMap(Effects.fireLight());
  for (BlastPart nukeBlast : parts) {
   nukeBlast.runRender();
  }
 }

 public void runHitOthers(boolean greenTeam) {
  boolean replay = UI.status == UI.Status.replay;
  for (Vehicle vehicle : I.vehicles) {
   if (!U.sameTeam(V, vehicle) && !othersBlasted[vehicle.index] && !vehicle.reviveImmortality && U.distance(V.MNB, vehicle) < V.MNB.sphereSize + vehicle.collisionRadius && !vehicle.phantomEngaged) {
    V.P.hitCheck(vehicle);
    vehicle.setDamage(vehicle.damageCeiling());
    Match.scoreDamage[greenTeam ? 0 : 1] += replay ? 0 : vehicle.durability;
    if (vehicle.getsPushed >= 0) {
     vehicle.speedX += blastSpeed * Double.compare(vehicle.X, X) * (1 + U.random(.5));
     vehicle.speedZ += blastSpeed * Double.compare(vehicle.Z, Z) * (1 + U.random(.5));
    }
    if (vehicle.getsLifted >= 0) {
     vehicle.speedY += blastSpeed * Double.compare(vehicle.Y, Y) * (1 + U.random(.5));
    }
    double soundDistance = Math.sqrt(U.distance(vehicle)) * Sound.standardDistance(1);
    vehicle.VA.crashDestroy.play(Double.NaN, soundDistance);
    vehicle.VA.crashDestroy.play(Double.NaN, soundDistance);
    vehicle.VA.crashDestroy.play(Double.NaN, soundDistance);
    othersBlasted[vehicle.index] = true;
   }
  }
 }

 class BlastPart extends CoreAdvanced {

  private final Sphere S;
  double sinXZ, sinYZ, cosXZ, cosYZ;//<-Performance optimization

  BlastPart(PhongMaterial PM) {
   S = new Sphere(10000, 1);
   U.setMaterialSecurely(S, PM);
   U.randomRotate(S);
   Nodes.add(S);
   S.setVisible(false);
  }

  void runLogic(boolean gamePlay, double speed) {
   if (gamePlay) {
    X += speed * sinXZ * cosYZ;
    Y += speed * sinYZ;
    Z += speed * cosXZ * cosYZ;
   }
  }

  void runRender() {
   if (render && !U.outOfBounds(this, S.getRadius()) && U.render(this, false, true)) {
    U.setTranslate(S, this);
    S.setVisible(true);
   } else {
    S.setVisible(false);
   }
  }
 }
}
