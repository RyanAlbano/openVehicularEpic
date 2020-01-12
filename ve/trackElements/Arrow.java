package ve.trackElements;

import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.SubScene;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import ve.environment.E;
import ve.instances.I;
import ve.ui.Match;
import ve.ui.UI;
import ve.ui.Units;
import ve.utilities.Camera;
import ve.utilities.Phong;
import ve.utilities.SL;
import ve.utilities.U;
import ve.vehicles.Vehicle;

public enum Arrow {
 ;
 public static final MeshView MV = new MeshView();
 public static final Group group = new Group();
 public static Status status = Status.racetrack;
 private static Status lastStatus = Status.racetrack;
 public static SubScene scene;
 public static int target;

 public enum Status {racetrack, vehicles, locked}

 static {
  TriangleMesh TM = new TriangleMesh();
  TM.getTexCoords().setAll(0, 0);
  float arrowWidth = .25f;
  TM.getPoints().setAll(
  0, 0, arrowWidth * 3,
  0, -arrowWidth, -arrowWidth * 3,
  -arrowWidth, 0, -arrowWidth * 3,
  arrowWidth, 0, -arrowWidth * 3,
  0, arrowWidth, -arrowWidth * 3);
  TM.getFaces().setAll(
  0, 0, 2, 0, 1, 0,
  0, 0, 1, 0, 3, 0,
  0, 0, 3, 0, 4, 0,
  0, 0, 4, 0, 2, 0,
  4, 0, 1, 0, 2, 0,
  4, 0, 3, 0, 1, 0);
  MV.setMesh(TM);
  PhongMaterial PM = new PhongMaterial();
  Phong.setSpecularRGB(PM, 1);
  U.setMaterialSecurely(MV, PM);
  MV.setTranslateX(0);
  MV.setTranslateY(-5);
  MV.setTranslateZ(10);
 }

 public static void addToScene() {
  if (!group.getChildren().contains(MV)) {
   group.getChildren().add(MV);
  }
  MV.setVisible(false);
  PointLight backPL = new PointLight();
  backPL.setTranslateX(0);
  backPL.setTranslateY(MV.getTranslateY());
  backPL.setTranslateZ(-Long.MAX_VALUE);
  backPL.setColor(U.getColor(1));
  PointLight frontPL = new PointLight();
  frontPL.setTranslateX(0);
  frontPL.setTranslateY(MV.getTranslateY());
  frontPL.setTranslateZ(Long.MAX_VALUE);
  frontPL.setColor(U.getColor(1));
  group.getChildren().addAll(new AmbientLight(U.getColor(.5)), backPL, frontPL);
 }

 public static void run() {
  if (lastStatus != status) {
   Match.print = status == Arrow.Status.locked ? "Arrow now Locked on " + UI.playerNames[target] : "Arrow now pointing at " + (status == Arrow.Status.vehicles ? "Vehicles" : SL.Map);
   Match.messageWait = false;
   Match.printTimer = 50;
   lastStatus = status;
  }
  Vehicle V = I.vehicles.get(I.vehiclePerspective);
  double d, dY, targetX = V.X, targetY = V.Y, targetZ = V.Z;
  if (status == Arrow.Status.racetrack) {
   boolean hasSize = !TE.points.isEmpty();
   double nX = hasSize ? TE.points.get(V.point).X : 0, nY = hasSize ? TE.points.get(V.point).Y : 0, nZ = hasSize ? TE.points.get(V.point).Z : 0;
   d = (nX - V.X >= 0 ? 270 : 90) + U.arcTan((nZ - V.Z) / (nX - V.X));
   dY = (nY - V.Y >= 0 ? 270 : 90) + U.arcTan(U.distance(nX, V.X, nZ, V.Z) / (nY - V.Y));
   if (hasSize) {
    targetX = TE.points.get(V.point).X;
    targetY = TE.points.get(V.point).Y;
    targetZ = TE.points.get(V.point).Z;
   }
  } else {
   if (status != Arrow.Status.locked) {
    double compareDistance = Double.POSITIVE_INFINITY;
    for (Vehicle vehicle : I.vehicles) {
     if (vehicle.index != I.vehiclePerspective && vehicle.isIntegral() && U.distance(V, vehicle) < compareDistance) {
      target = vehicle.index;
      compareDistance = U.distance(V, vehicle);
     }
    }
    if (I.vehiclePerspective == I.userPlayerIndex && !U.sameTeam(I.userPlayerIndex, target)) {
     I.vehicles.get(I.userPlayerIndex).AI.target = target;//<-Calling userPlayer more accurate than 'vehiclePerspective' here
    }
   }
   target = I.vehiclesInMatch < 2 ? 0 : target;
   Vehicle targetVehicle = I.vehicles.get(target);
   targetX = targetVehicle.X;
   targetY = targetVehicle.Y;
   targetZ = targetVehicle.Z;
   double nameHeight = .15, B = targetVehicle.getDamage(true);
   U.fillRGB(1, 1 - B, 0);
   U.fillRectangle(.5, nameHeight, B * .1, .005);
   if (status == Arrow.Status.locked) {
    U.strokeRGB(U.yinYang ? 1 : 0);
    UI.GC.strokeLine((UI.width * .5) - 50, UI.height * nameHeight, (UI.width * .5) + 50, UI.height * nameHeight);
   }
   d = (targetVehicle.X - V.X >= 0 ? 270 : 90) + U.arcTan((targetVehicle.Z - V.Z) / (targetVehicle.X - V.X));
   dY = (targetVehicle.Y - V.Y >= 0 ? 270 : 90) + U.arcTan(U.distanceXZ(targetVehicle, V) / (targetVehicle.Y - V.Y));
   U.fillRGB(E.skyRGB.invert());
   U.text("[ " + UI.playerNames[target] + " ]", nameHeight);
  }
  U.fillRGB(U.yinYang ? 1 : 0);
  U.text("(" + Math.round(Units.getDistance(U.distance(V.X, targetX, V.Y, targetY, V.Z, targetZ))) + ")", .175);
  d += Camera.XZ;
  while (d < -180) d += 360;
  while (d > 180) d -= 360;
  if (status != Arrow.Status.racetrack && (I.vehiclesInMatch < 2 || target == I.vehiclePerspective)) {
   d = dY = 0;
  }
  U.rotate(MV, -dY, d);
  if (status == Arrow.Status.racetrack || I.vehiclesInMatch < 3) {
   Phong.setDiffuseRGB((PhongMaterial) MV.getMaterial(), E.skyRGB.invert());
  } else {
   long[] RG = {0, 0};
   if (U.yinYang) {
    RG[target < I.vehiclesInMatch >> 1 ? 1 : 0] = 1;
   }
   Phong.setDiffuseRGB((PhongMaterial) MV.getMaterial(), RG[0], RG[1], 0);
  }
 }
}