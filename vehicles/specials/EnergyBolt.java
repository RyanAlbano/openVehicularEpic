package ve.vehicles.specials;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import ve.Camera;
import ve.VE;
import ve.utilities.SL;
import ve.utilities.U;
import ve.vehicles.Vehicle;
import ve.vehicles.VehicleAudio;

public class EnergyBolt extends MeshView {
 private final Vehicle V;
 private final Special S;
 public int target;

 EnergyBolt(Vehicle vehicle, Special special) {
  V = vehicle;
  S = special;
  PhongMaterial PM = new PhongMaterial();
  U.Phong.setDiffuseRGB(PM, 0);
  U.Phong.setSpecularRGB(PM, 0);
  PM.setSelfIlluminationMap(U.Images.get(SL.Images.white));
  U.setMaterialSecurely(this, PM);
  TriangleMesh TM = new TriangleMesh();
  TM.getPoints().setAll(
  0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0);
  TM.getTexCoords().setAll(0, 0);
  TM.getFaces().setAll(
  0, 0, 1, 0, 2, 0,
  2, 0, 3, 0, 0, 0);
  setMesh(TM);
  setCullFace(CullFace.NONE);
  U.Nodes.add(this);
 }

 void run(boolean gamePlay) {
  if (V.destroyed) {
   setVisible(false);
  } else {
   target = V.index;
   double compareDistance = Double.POSITIVE_INFINITY;
   for (Vehicle vehicle : VE.vehicles) {
    if (vehicle.index != V.index && U.sameTeam(V, vehicle) && !vehicle.destroyed && U.distance(V, vehicle) < compareDistance &&
    vehicle.type != Vehicle.Type.supportInfrastructure) {//<-May need to be changed if more infrastructure types get added later!
     target = vehicle.index;
     compareDistance = U.distance(V, vehicle);
    }
   }
   if (VE.Match.started) {
    VE.vehicles.get(target).energyMultiple *= 2;
   }
  }
  VehicleAudio.runEnergyBolt(S, gamePlay);
 }

 public void renderMesh() {
  if (!V.destroyed) {
   Vehicle targetV = VE.vehicles.get(target);
   double sourceRandom = V.absoluteRadius * .05,
   targetRandom = targetV.absoluteRadius * .05,
   X = Camera.X, Y = Camera.Y, Z = Camera.Z;//This implementation is ugly, but trying to set Node translate would probably be less accurate
   ((TriangleMesh) getMesh()).getPoints().setAll(
   //SOURCE
   (float) (V.X + U.randomPlusMinus(sourceRandom) - X), (float) (V.Y - V.renderRadius + U.randomPlusMinus(sourceRandom) - Y), (float) (V.Z + U.randomPlusMinus(sourceRandom) - Z),
   (float) (V.X + U.randomPlusMinus(sourceRandom) - X), (float) (V.Y - V.renderRadius + U.randomPlusMinus(sourceRandom) - Y), (float) (V.Z + U.randomPlusMinus(sourceRandom) - Z),
   //TARGET
   (float) (targetV.X + U.randomPlusMinus(targetRandom) - X), (float) (targetV.Y + U.randomPlusMinus(targetRandom) - Y), (float) (targetV.Z + U.randomPlusMinus(targetRandom) - Z),
   (float) (targetV.X + U.randomPlusMinus(targetRandom) - X), (float) (targetV.Y + U.randomPlusMinus(targetRandom) - Y), (float) (targetV.Z + U.randomPlusMinus(targetRandom) - Z));
   setVisible(true);
  }
 }
}
