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
  setPoints(chipMesh);//<-Do it here as well so mesh loads properly
  chipMesh.getTexCoords().addAll(U.random() < .5 ? E.textureCoordinateBase0 : E.textureCoordinateBase1);
  chipMesh.getFaces().addAll(0, 0, 1, 1, 2, 2);
  chipMesh.getFaces().addAll(2, 2, 1, 1, 0, 0);
  MV = new MeshView(chipMesh);
  MV.setCullFace(CullFace.BACK);
  U.setMaterialSecurely(MV, VP.PM);
  U.Nodes.add(MV);
  MV.setVisible(false);
 }

 private void setPoints(TriangleMesh TM) {
  TM.getPoints().setAll(
  (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius),
  (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius),
  (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius), (float) U.randomPlusMinus(absoluteRadius));
 }

 void deploy(Vehicle V, double throwPower) {
  if (stage <= 0 && U.random() < .5) {
   setPoints((TriangleMesh) MV.getMesh());
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
   if (Y + VP.Y > V.P.localVehicleGround || (stage += gamePlay ? U.random(VE.tick) : 0) > 10) {
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
     Y += speedY * VE.tick + (V.P.mode.name().startsWith(Physics.Mode.drive.name()) ? gravitySpeed * VE.tick : 0);
    }
    if (VP.MV.isVisible() && U.render(X + VP.X, Y + VP.Y, Z + VP.Z)) {
     U.setTranslate(MV, X + VP.X, Y + VP.Y, Z + VP.Z);
     U.rotate(MV, this);
     MV.setVisible(true);
    } else {
     MV.setVisible(false);
    }
   }
  }
 }
}
