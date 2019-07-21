package ve.vehicles;

import javafx.scene.shape.*;
import ve.VE;
import ve.environment.E;
import ve.utilities.U;

class Chip extends MeshView {

 private final VehiclePiece VP;
 private double X;
 private double Y;
 private double Z;
 private double stage;
 private double speedX;
 private double speedY;
 private double speedZ;
 private double gravitySpeed;
 private double XZ;
 private double XY;
 private double YZ;
 private double speedXZ;
 private double speedXY;
 private double speedYZ;
 private final double size;

 Chip(VehiclePiece vp) {
  VP = vp;
  size = .1 * VP.absoluteRadius;
  TriangleMesh chipMesh = new TriangleMesh();
  chipMesh.getPoints().setAll(//<-Do it here as well so it works correctly
   (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size),
   (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size),
   (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size));
  chipMesh.getTexCoords().setAll(0, 0);
  chipMesh.getFaces().setAll(0, 0, 1, 0, 2, 0);
  setMesh(chipMesh);
  setCullFace(CullFace.NONE);
  setMaterial(VP.PM);
  U.add(this);
  setVisible(false);
 }

 void deploy(Vehicle V, double throwPower) {
  if (stage <= 0 && U.random() < .5) {
   ((TriangleMesh) getMesh()).getPoints().setAll(
    (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size),
    (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size),
    (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size), (float) U.randomPlusMinus(size));
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
    setVisible(false);
   } else {
    XZ += speedXZ * VE.tick;
    XY += speedXY * VE.tick;
    YZ += speedYZ * VE.tick;
    if (gamePlay) {
     X += speedX * VE.tick;
     Z += speedZ * VE.tick;
     gravitySpeed += E.gravity * VE.tick;
     Y += speedY * VE.tick + (V.mode.name().startsWith("drive") ? gravitySpeed * VE.tick : 0);
    }
    if (VP.MV.isVisible() && U.getDepth(X + VP.X, Y + VP.Y, Z + VP.Z) > 0) {
     U.setTranslate(this, X + VP.X, Y + VP.Y, Z + VP.Z);
     U.rotate(this, XY, YZ, XZ);
     setVisible(true);
    } else {
     setVisible(false);
    }
   }
  }
 }
}
