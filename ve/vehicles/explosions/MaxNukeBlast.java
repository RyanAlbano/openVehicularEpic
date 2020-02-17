package ve.vehicles.explosions;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.effects.Effects;
import ve.environment.E;
import ve.instances.Core;
import ve.instances.CoreAdvanced;
import ve.instances.I;
import ve.ui.Match;
import ve.ui.UI;
import ve.utilities.*;
import ve.utilities.sound.Controlled;
import ve.utilities.sound.Sounds;
import ve.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.Collection;

public class MaxNukeBlast extends Core {//fixme--there's still a slight delay in the sphere/blast placement, but this is probably only due to the logic being run in runMiscellaneous()
 private final Vehicle V;
 private final Sphere sphere;
 private double sphereSize;
 private boolean render;
 private final boolean[] othersBlasted = new boolean[I.vehiclesInMatch];
 private final Collection<BlastPart> parts = new ArrayList<>();
 private static final double blastSpeed = 6000;
 public Controlled travel;

 public MaxNukeBlast(Vehicle vehicle) {
  V = vehicle;
  sphere = new Sphere(1);
  PhongMaterial PM = new PhongMaterial();
  PM.setSpecularPower(E.Specular.Powers.dull);
  U.setMaterialSecurely(sphere, PM);
  Nodes.add(sphere);
  sphere.setVisible(false);
  PhongMaterial partPM = new PhongMaterial();
  Phong.setSpecularRGB(partPM, 0);
  partPM.setSelfIlluminationMap(Images.white);
  for (int n = 1000; --n >= 0; ) {
   parts.add(new BlastPart(partPM));
  }
  I.maxNukeInMatch = true;
 }

 public void setSingularity() {
  sphereSize = 0;
  X = V.X;
  Y = V.Y;
  Z = V.Z;
  for (var part : parts) {
   part.X = X;
   part.Y = Y;
   part.Z = Z;
   part.XZ = U.random(360.);
   part.YZ = U.random(360.);
   part.sinXZ = U.sin(part.XZ);
   part.cosXZ = U.cos(part.XZ);
   part.sinYZ = U.sin(part.YZ);
   part.cosYZ = U.cos(part.YZ);
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
   travel.loop(Math.sqrt(Math.abs(U.distance(this) - sphereSize)) * Sounds.standardGain(Sounds.gainMultiples.nuke));
  } else {
   travel.stop();
  }
  ((PhongMaterial) sphere.getMaterial()).setSelfIlluminationMap(Effects.fireLight());
  for (var blastPart : parts) {
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
  for (var part : parts) {
   part.runRender();
  }
 }

 public void runHitOthers(boolean greenTeam) {
  boolean replay = UI.status == UI.Status.replay;
  for (var vehicle : I.vehicles) {
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
    double soundDistance = Math.sqrt(U.distance(vehicle)) * Sounds.standardGain(1);
    vehicle.VA.crashDestroy.play(Double.NaN, soundDistance);
    vehicle.VA.crashDestroy.play(Double.NaN, soundDistance);
    vehicle.VA.crashDestroy.play(Double.NaN, soundDistance);
    othersBlasted[vehicle.index] = true;
   }
  }
 }

 class BlastPart extends CoreAdvanced {

  private final Sphere S;
  double sinXZ, cosXZ, sinYZ, cosYZ;//<-Performance optimization

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
