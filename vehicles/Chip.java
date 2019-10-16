package ve.vehicles;

import javafx.scene.shape.*;
import ve.Core;
import ve.VE;
import ve.environment.E;
import ve.utilities.U;

class Chip extends Core {

 private final MeshView MV;
 private final VehiclePart VP;
 private double stage;
 private double speedX, speedY, speedZ;
 private double gravitySpeed;
 private double speedXZ, speedXY, speedYZ;

 Chip(VehiclePart vp) {
  VP = vp;
  absoluteRadius = .1 * VP.absoluteRadius;
  TriangleMesh chipMesh = new TriangleMesh();
  chipMesh.getPoints().setAll(//<-Do it here as well so it works correctly
  (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius),
  (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius),
  (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius));
  chipMesh.getTexCoords().setAll(0, 0);
  chipMesh.getFaces().setAll(0, 0, 1, 0, 2, 0);
  MV = new MeshView(chipMesh);
  MV.setCullFace(CullFace.NONE);
  MV.setMaterial(VP.PM);
  U.add(MV);
  MV.setVisible(false);
 }

 void deploy(Vehicle V, double throwPower) {
  if (stage <= 0 && U.random() < .5) {
   ((TriangleMesh) MV.getMesh()).getPoints().setAll(
   (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius),
   (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius),
   (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius));
   X = Y = Z = gravitySpeed = 0;
   XZ = V.XZ;
   XY = V.XY;
   YZ = V.YZ;
   speedX = throwPower * U.randomPlusMinus(6.);
   speedY = throwPower * U.randomPlusMinus(6.);
   speedZ = throwPower * U.randomPlusMinus(6.);
   speedXY = U.randomPlusMinus(80.);
   speedXZ = U.randomPlusMinus(80.);
   speedYZ = U.randomPlusMinus(80.);
   stage = Double.MIN_VALUE;
  }
 }

 public void run(Vehicle V, boolean gamePlay) {
  if (stage > 0) {
   if (Y + VP.Y > V.localVehicleGround || (stage += gamePlay ? U.random(VE.tick) : 0) > 10) {
    stage = 0;
    MV.setVisible(false);
   } else {
    XZ += speedXZ * VE.tick;
    XY += speedXY * VE.tick;
    YZ += speedYZ * VE.tick;
    if (gamePlay) {
     X += speedX * VE.tick;
     Z += speedZ * VE.tick;
     gravitySpeed += E.gravity * VE.tick;
     Y += speedY * VE.tick + (V.mode.name().startsWith(Vehicle.Mode.drive.name()) ? gravitySpeed * VE.tick : 0);
    }
    if (VP.MV.isVisible() && U.render(X + VP.X, Y + VP.Y, Z + VP.Z)) {
     U.setTranslate(MV, X + VP.X, Y + VP.Y, Z + VP.Z);
     U.rotate(MV,this);
     MV.setVisible(true);
    } else {
     MV.setVisible(false);
    }
   }
  }
 }
}
