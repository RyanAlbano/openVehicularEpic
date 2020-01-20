package ve.vehicles.specials;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import ve.instances.I;
import ve.ui.Match;
import ve.utilities.*;
import ve.vehicles.Vehicle;

public class EnergyBolt extends MeshView {
 private final Vehicle V;
 private final Special S;
 private int target;
 private long render;

 EnergyBolt(Vehicle vehicle, Special special) {
  V = vehicle;
  S = special;
  PhongMaterial PM = new PhongMaterial();
  Phong.setDiffuseRGB(PM, 0);
  Phong.setSpecularRGB(PM, 0);
  PM.setSelfIlluminationMap(Images.white);
  U.setMaterialSecurely(this, PM);
  TriangleMesh TM = new TriangleMesh();
  TM.getPoints().setAll(
  0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0,
  0, 0, 0, 0, 0, 0);
  TM.getTexCoords().setAll(0, 0);
  TM.getFaces().setAll(
  0, 0, 1, 0, 2, 0,
  1, 0, 2, 0, 3, 0,
  2, 0, 3, 0, 4, 0,
  3, 0, 4, 0, 5, 0);
  setMesh(TM);
  setCullFace(CullFace.NONE);
  Nodes.add(this);
 }

 public void run(boolean gamePlay) {
  if (V.destroyed) {
   render = 0;
   S.sound.stop();
  } else {
   target = V.index;
   double compareDistance1 = Double.POSITIVE_INFINITY;
   for (Vehicle vehicle : I.vehicles) {//<-Direct energy towards fellow support infrastructure
    if (vehicle.index != V.index && U.sameTeam(V, vehicle) && !vehicle.destroyed && U.distance(V, vehicle) < compareDistance1 &&
    vehicle.type == Vehicle.Type.supportInfrastructure) {//*
     target = vehicle.index;
     compareDistance1 = U.distance(V, vehicle);
    }
   }
   //*May need to be changed if more infrastructure types get added later!
   double compareDistance = Double.POSITIVE_INFINITY;
   for (Vehicle vehicle : I.vehicles) {//<-Redirect energy towards fellow non-support-infrastructure teammates if they're alive or exist
    if (vehicle.index != V.index && U.sameTeam(V, vehicle) && !vehicle.destroyed && U.distance(V, vehicle) < compareDistance &&
    vehicle.type != Vehicle.Type.supportInfrastructure) {//*
     target = vehicle.index;
     compareDistance = U.distance(V, vehicle);
    }
   }
   if (Match.started && gamePlay) {
    I.vehicles.get(target).energyMultiple *= 2;
   }
   if (S.sound.running()) {
    render++;
   } else {//This technique of the shock graphics depending on the audio timing (as opposed to vice-versa) is a bit unusual
    render = 0;
    if (gamePlay) {
     S.sound.play(Double.NaN, Math.min(V.VA.distanceVehicleToCamera, I.vehicles.get(target).VA.distanceVehicleToCamera));
    }
   }
  }
 }

 public void renderMesh() {
  if (!V.destroyed && render > 3) {//<-Not sure why 'render' check must be this high
   Vehicle targetV = I.vehicles.get(target);
   double sizeAtSource = V.absoluteRadius * .05,
   sizeAtTarget = targetV.absoluteRadius * .05,
   sizeAtMidpoint = (sizeAtSource + sizeAtTarget) * .5,
   sourceY = V.Y - V.renderRadius,
   mixX = (V.X + targetV.X) * .5, mixZ = (V.Z + targetV.Z) * .5, mixY = (sourceY + targetV.Y) * .5,
   mixPulse = targetV.absoluteRadius * .25,
   mixPulseX = U.randomPlusMinus(mixPulse), mixPulseY = U.randomPlusMinus(mixPulse), mixPulseZ = U.randomPlusMinus(mixPulse),
   X = Camera.X, Y = Camera.Y, Z = Camera.Z;//This implementation is ugly, but trying to set Node translate would probably be less accurate
   ((TriangleMesh) getMesh()).getPoints().setAll(
   //SOURCE
   (float) (V.X + U.randomPlusMinus(sizeAtSource) - X), (float) (sourceY + U.randomPlusMinus(sizeAtSource) - Y), (float) (V.Z + U.randomPlusMinus(sizeAtSource) - Z),
   (float) (V.X + U.randomPlusMinus(sizeAtSource) - X), (float) (sourceY + U.randomPlusMinus(sizeAtSource) - Y), (float) (V.Z + U.randomPlusMinus(sizeAtSource) - Z),
   //MIDPOINT
   (float) (mixX + U.randomPlusMinus(sizeAtMidpoint) + mixPulseX - X), (float) (mixY + U.randomPlusMinus(sizeAtMidpoint) + mixPulseY - Y), (float) (mixZ + U.randomPlusMinus(sizeAtMidpoint) + mixPulseZ - Z),
   (float) (mixX + U.randomPlusMinus(sizeAtMidpoint) + mixPulseX - X), (float) (mixY + U.randomPlusMinus(sizeAtMidpoint) + mixPulseY - Y), (float) (mixZ + U.randomPlusMinus(sizeAtMidpoint) + mixPulseZ - Z),
   //TARGET
   (float) (targetV.X + U.randomPlusMinus(sizeAtTarget) - X), (float) (targetV.Y + U.randomPlusMinus(sizeAtTarget) - Y), (float) (targetV.Z + U.randomPlusMinus(sizeAtTarget) - Z),
   (float) (targetV.X + U.randomPlusMinus(sizeAtTarget) - X), (float) (targetV.Y + U.randomPlusMinus(sizeAtTarget) - Y), (float) (targetV.Z + U.randomPlusMinus(sizeAtTarget) - Z));
   setVisible(true);
  } else {
   setVisible(false);
  }
 }
}
