package ve.vehicles.specials;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.Core;
import ve.Sound;
import ve.VE;
import ve.utilities.SL;
import ve.utilities.U;
import ve.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.Collection;

public class MaxNukeBlast extends Core {
 private final Vehicle V;
 private final Sphere main;
 private double sphereSize;
 private final boolean[] othersBlasted = new boolean[VE.vehiclesInMatch];
 private final Collection<NukeBlastPart> parts = new ArrayList<>();

 public MaxNukeBlast(Vehicle vehicle) {
  V = vehicle;
  main = new Sphere(1);
  PhongMaterial nukeBlastPM = new PhongMaterial();//<-More details later
  U.setMaterialSecurely(main, nukeBlastPM);
  U.Nodes.add(main);
  main.setVisible(false);
  PhongMaterial partPM = new PhongMaterial();
  U.Phong.setSpecularRGB(partPM, 0);
  partPM.setSelfIlluminationMap(U.Images.get(SL.white));
  for (int n = 1000; --n >= 0; ) {
   parts.add(new NukeBlastPart(partPM));
  }
 }

 public void setSingularity() {
  sphereSize = 0;
  X = V.X;
  Y = V.Y;
  Z = V.Z;
  for (NukeBlastPart nukeBlast : parts) {
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

 public void run(boolean gamePlay) {
  boolean vehicleExploded = !V.isIntegral();
  if (vehicleExploded && U.render(this, -sphereSize)) {
   U.setTranslate(main, this);
   main.setVisible(true);
  } else {
   main.setVisible(false);
  }
  double blastSpeed = 6000 * VE.tick;
  if (vehicleExploded && gamePlay) {
   sphereSize += blastSpeed;
   U.setScale(main, sphereSize);
   V.VA.nuke.loop(1, Math.sqrt(Math.abs(U.distance(this) - sphereSize)) * Sound.standardDistance(.5));
  } else {
   V.VA.nuke.stop(1);
  }
  ((PhongMaterial) main.getMaterial()).setSelfIlluminationMap(U.Images.get(SL.firelight + U.random(3)));
  for (NukeBlastPart nukeBlast : parts) {
   nukeBlast.run(gamePlay, blastSpeed, vehicleExploded);
  }
 }

 public void runHitOthers(boolean greenTeam) {
  boolean replay = VE.status == VE.Status.replay;
  for (Vehicle vehicle : VE.vehicles) {
   if (!U.sameTeam(V, vehicle) && !othersBlasted[vehicle.index] && !vehicle.reviveImmortality && U.distance(V.MNB, vehicle) < V.MNB.sphereSize + vehicle.collisionRadius && !vehicle.phantomEngaged) {
    V.P.hitCheck(vehicle);
    vehicle.setDamage(vehicle.damageCeiling());
    VE.Match.scoreDamage[greenTeam ? 0 : 1] += replay ? 0 : vehicle.durability;
    if (vehicle.getsPushed >= 0) {
     vehicle.P.speedX += 6000 * Double.compare(vehicle.X, X) * (1 + U.random(.5));
     vehicle.P.speedZ += 6000 * Double.compare(vehicle.Z, Z) * (1 + U.random(.5));
    }
    if (vehicle.getsLifted >= 0) {
     vehicle.P.speedY += 6000 * Double.compare(vehicle.Y, Y) * (1 + U.random(.5));
    }
    double soundDistance = Math.sqrt(U.distance(vehicle)) * Sound.standardDistance(1);
    vehicle.VA.crashDestroy.play(Double.NaN, soundDistance);
    vehicle.VA.crashDestroy.play(Double.NaN, soundDistance);
    vehicle.VA.crashDestroy.play(Double.NaN, soundDistance);
    othersBlasted[vehicle.index] = true;
   }
  }
 }

 static class NukeBlastPart extends Core {

  private final Sphere S;
  double sinXZ, sinYZ, cosXZ, cosYZ;//<-Performance optimization

  NukeBlastPart(PhongMaterial PM) {
   S = new Sphere(10000, 1);
   U.setMaterialSecurely(S, PM);
   U.randomRotate(S);
   U.Nodes.add(S);
   S.setVisible(false);
  }

  void run(boolean gamePlay, double blastSpeed, boolean vehicleExploded) {
   if (gamePlay) {
    X += blastSpeed * sinXZ * cosYZ;
    Y += blastSpeed * sinYZ;
    Z += blastSpeed * cosXZ * cosYZ;
   }
   if (vehicleExploded && !U.outOfBounds(this, S.getRadius()) && U.render(this)) {
    U.setTranslate(S, this);
    S.setVisible(true);
   } else {
    S.setVisible(false);
   }
  }
 }
}
