package ve.trackElements;

import javafx.scene.SubScene;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import ve.Camera;
import ve.VE;
import ve.environment.E;
import ve.utilities.U;
import ve.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.List;

public enum TE {//<-TrackElements main Class
 ;
 public static final Sphere bonusBig = new Sphere(500);
 public static MeshView arrow = new MeshView();

 public enum Arrow {racetrack, vehicles, locked}

 public static Arrow arrowStatus = Arrow.racetrack;
 public static Arrow lastArrowStatus = Arrow.racetrack;
 public static SubScene arrowScene;
 public static final List<Point> points = new ArrayList<>();
 public static final List<Checkpoint> checkpoints = new ArrayList<>();

 public static class BonusBall extends Sphere {//<-Not worth extending Core here

  public double X, Y, Z, speedX, speedY, speedZ;
 }

 public static final List<BonusBall> bonusBalls = new ArrayList<>();

 public static void runBonusBalls() {
  for (BonusBall bonusBall : bonusBalls) {
   bonusBall.speedY += U.randomPlusMinus(6.);
   bonusBall.speedX += U.randomPlusMinus(6.);
   bonusBall.speedZ += U.randomPlusMinus(6.);
   bonusBall.speedX *= .99;
   bonusBall.speedY *= .99;
   bonusBall.speedZ *= .99;
   bonusBall.X += bonusBall.speedX * VE.tick;
   if (Math.abs(bonusBall.X) > VE.vehicles.get(VE.bonusHolder).collisionRadius * 2) {
    bonusBall.speedX *= -1;
    bonusBall.X *= .999;
   }
   bonusBall.Y += bonusBall.speedY * VE.tick;
   if (Math.abs(bonusBall.Y) > VE.vehicles.get(VE.bonusHolder).collisionRadius * 2) {
    bonusBall.speedY *= -1;
    bonusBall.Y *= .999;
   }
   bonusBall.Z += bonusBall.speedZ * VE.tick;
   if (Math.abs(bonusBall.Z) > VE.vehicles.get(VE.bonusHolder).collisionRadius * 2) {
    bonusBall.speedZ *= -1;
    bonusBall.Z *= .999;
   }
   if (U.getDepth(VE.vehicles.get(VE.bonusHolder).X + bonusBall.X, VE.vehicles.get(VE.bonusHolder).Y + bonusBall.Y, VE.vehicles.get(VE.bonusHolder).Z + bonusBall.Z) > 0) {
    U.setTranslate(bonusBall, VE.vehicles.get(VE.bonusHolder).X + bonusBall.X, VE.vehicles.get(VE.bonusHolder).Y + bonusBall.Y, VE.vehicles.get(VE.bonusHolder).Z + bonusBall.Z);
    bonusBall.setVisible(true);
    U.setDiffuseRGB((PhongMaterial) bonusBall.getMaterial(), U.random(), U.random(), U.random());
   } else {
    bonusBall.setVisible(false);
   }
  }
 }

 public static void runArrow() {
  Vehicle V = VE.vehicles.get(VE.vehiclePerspective);
  double d, dY, targetX = V.X, targetY = V.Y, targetZ = V.Z;
  if (arrowStatus == TE.Arrow.racetrack) {
   boolean hasSize = !points.isEmpty();
   double nX = hasSize ? points.get(V.point).X : 0, nY = hasSize ? points.get(V.point).Y : 0, nZ = hasSize ? points.get(V.point).Z : 0;
   d = (nX - V.X >= 0 ? 270 : 90) + U.arcTan((nZ - V.Z) / (nX - V.X));
   dY = (nY - V.Y >= 0 ? 270 : 90) + U.arcTan(U.distance(nX, V.X, nZ, V.Z) / (nY - V.Y));
   if (hasSize) {
    targetX = points.get(V.point).X;
    targetY = points.get(V.point).Y;
    targetZ = points.get(V.point).Z;
   }
  } else {
   targetX = VE.vehicles.get(VE.arrowTarget).X;
   targetY = VE.vehicles.get(VE.arrowTarget).Y;
   targetZ = VE.vehicles.get(VE.arrowTarget).Z;
   if (arrowStatus != TE.Arrow.locked) {
    double compareDistance = Double.POSITIVE_INFINITY;
    for (Vehicle vehicle : VE.vehicles) {
     if (vehicle.index != VE.vehiclePerspective && vehicle.destructionType < 1 && U.distance(V, vehicle) < compareDistance) {
      VE.arrowTarget = vehicle.index;
      compareDistance = U.distance(V, vehicle);
     }
    }
    VE.vehicles.get(VE.userPlayer).AI.target = VE.vehiclePerspective == VE.userPlayer ? VE.arrowTarget : VE.vehicles.get(VE.userPlayer).AI.target;
   }
   VE.arrowTarget = VE.vehiclesInMatch < 2 ? 0 : VE.arrowTarget;
   Vehicle targetVehicle = VE.vehicles.get(VE.arrowTarget);
   double nameHeight = .15, B = targetVehicle.damage / targetVehicle.durability;
   U.fillRGB(1, 1 - B, 0);
   U.fillRectangle(.5, nameHeight, B * .1, .005);
   if (arrowStatus == TE.Arrow.locked) {
    double C = VE.globalFlick ? 1 : 0;
    U.strokeRGB(C, C, C);
    VE.graphicsContext.strokeLine((VE.width * .5) - 50, VE.height * nameHeight, (VE.width * .5) + 50, VE.height * nameHeight);
   }
   d = (targetVehicle.X - V.X >= 0 ? 270 : 90) + U.arcTan((targetVehicle.Z - V.Z) / (targetVehicle.X - V.X));
   dY = (targetVehicle.Y - V.Y >= 0 ? 270 : 90) + U.arcTan(U.distance(targetVehicle.X, V.X, targetVehicle.Z, V.Z) / (targetVehicle.Y - V.Y));
   U.fillRGB(E.skyInverse);
   U.text("[ " + VE.playerNames[VE.arrowTarget] + " ]", nameHeight);
  }
  double convertedUnits = VE.units == .5364466667 ? .0175 : VE.units == 1 / 3. ? .0574147 : 1, color = VE.globalFlick ? 1 : 0;
  U.fillRGB(color, color, color);
  U.text("(" + Math.round(U.distance(V.X, targetX, V.Y, targetY, V.Z, targetZ) * convertedUnits) + ")", .175);
  d += Camera.XZ;
  while (d < -180) d += 360;
  while (d > 180) d -= 360;
  if (arrowStatus != TE.Arrow.racetrack && (VE.vehiclesInMatch < 2 || VE.arrowTarget == VE.vehiclePerspective)) {
   d = dY = 0;
  }
  U.rotate(arrow, -dY, d);
  if (arrowStatus == TE.Arrow.racetrack || VE.vehiclesInMatch < 3) {
   U.setDiffuseRGB((PhongMaterial) arrow.getMaterial(), E.skyInverse);
  } else {
   long[] RG = {0, 0};
   if (VE.globalFlick) {
    RG[VE.arrowTarget < VE.vehiclesInMatch >> 1 ? 1 : 0] = 1;
   }
   U.setDiffuseRGB((PhongMaterial) arrow.getMaterial(), RG[0], RG[1], 0);
  }
 }
}
