package ve.vehicles;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.Core;
import ve.VE;
import ve.utilities.SL;
import ve.utilities.U;

import java.util.ArrayList;
import java.util.Collection;

class MaxNukeBlast extends Core {
 private final Vehicle V;
 private final Sphere main;
 private double sphereSize;
 private final boolean[] othersBlasted = new boolean[VE.vehiclesInMatch];
 private final Collection<NukeBlastPart> parts = new ArrayList<>();

 MaxNukeBlast(Vehicle vehicle) {
  V = vehicle;
  main = new Sphere(1);
  PhongMaterial nukeBlastPM = new PhongMaterial();//<-More details later
  U.setMaterialSecurely(main, nukeBlastPM);
  U.Nodes.add(main);
  main.setVisible(false);
  PhongMaterial partPM = new PhongMaterial();
  U.Phong.setSpecularRGB(partPM, 0);
  partPM.setSelfIlluminationMap(U.Images.get(SL.Images.white));
  for (int n = 1000; --n >= 0; ) {
   parts.add(new NukeBlastPart(partPM));
  }
 }

 void setSingularity() {
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
  }
 }

 void run(boolean gamePlay) {
  if (V.explosionType == Vehicle.ExplosionType.maxnuclear) {
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
    V.VA.nuke.loop(1, Math.sqrt(Math.abs(U.distance(this) - sphereSize)) * .04);
   } else {
    V.VA.nuke.stop(1);
   }
   ((PhongMaterial) main.getMaterial()).setSelfIlluminationMap(U.Images.get(SL.Images.fireLight + U.random(3)));
   for (NukeBlastPart nukeBlast : parts) {
    nukeBlast.run(gamePlay, blastSpeed, vehicleExploded);
   }
  }
 }

 void runHitOthers(boolean greenTeam) {
  boolean replay = VE.status == VE.Status.replay;
  for (Vehicle vehicle : VE.vehicles) {
   if (!U.sameTeam(V, vehicle) && !othersBlasted[vehicle.index] && !vehicle.reviveImmortality && U.distance(V.MNB, vehicle) < V.MNB.sphereSize + vehicle.collisionRadius() && !vehicle.phantomEngaged) {
    V.P.hitCheck(vehicle);
    vehicle.setDamage(vehicle.damageCeiling());
    VE.Match.scoreDamage[greenTeam ? 0 : 1] += replay ? 0 : vehicle.durability;
    if (!vehicle.isFixed()) {
     double blastSpeedX = vehicle.getsPushed >= 0 ? (vehicle.X > X ? 6000 : vehicle.X < X ? -6000 : 0) * (1 + U.random(.5)) : 0,
     blastSpeedZ = vehicle.getsPushed >= 0 ? (vehicle.Z > Z ? 6000 : vehicle.Z < Z ? -6000 : 0) * (1 + U.random(.5)) : 0,
     blastSpeedY = vehicle.getsLifted >= 0 ? (vehicle.Y > Y ? 6000 : vehicle.Y < Y ? -6000 : 0) * (1 + U.random(.5)) : 0;
     for (Wheel wheel : vehicle.wheels) {
      wheel.speedX += blastSpeedX;
      wheel.speedZ += blastSpeedZ;
      wheel.speedY += blastSpeedY;
     }
    }
    double soundDistance = Math.sqrt(U.distance(vehicle)) * .08;
    vehicle.VA.crashDestroy.play(Double.NaN, soundDistance);
    vehicle.VA.crashDestroy.play(Double.NaN, soundDistance);
    vehicle.VA.crashDestroy.play(Double.NaN, soundDistance);
    othersBlasted[vehicle.index] = true;
   }
  }
 }

 static class NukeBlastPart extends Core {

  private final Sphere S;

  NukeBlastPart(PhongMaterial PM) {
   S = new Sphere(10000, 1);
   U.setMaterialSecurely(S, PM);
   U.randomRotate(S);
   U.Nodes.add(S);
   S.setVisible(false);
  }

  void run(boolean gamePlay, double blastSpeed, boolean vehicleExploded) {
   if (gamePlay) {
    X += blastSpeed * U.sin(XZ) * U.cos(YZ);
    Y += blastSpeed * U.sin(YZ);
    Z += blastSpeed * U.cos(XZ) * U.cos(YZ);
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
