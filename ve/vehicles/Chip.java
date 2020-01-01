package ve.vehicles;

import javafx.scene.shape.*;
import ve.environment.E;
import ve.instances.CoreAdvanced;
import ve.ui.UI;
import ve.utilities.SL;
import ve.utilities.U;

class Chip extends CoreAdvanced {

 private final MeshView MV;
 private final VehiclePart VP;
 private double stage;
 private double gravitySpeed;
 private double speedXZ, speedXY, speedYZ;

 Chip(VehiclePart vp) {
  VP = vp;
  absoluteRadius = .1 * VP.absoluteRadius;
  TriangleMesh chipMesh = new TriangleMesh();
  setPoints(chipMesh);//<-Call it here as well so mesh loads properly
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
   speedX = throwPower * (U.random(3) - 1.);
   speedY = throwPower * (U.random(3) - 1.);
   speedZ = throwPower * (U.random(3) - 1.);
   speedXY = U.randomPlusMinus(80.);
   speedXZ = U.randomPlusMinus(80.);
   speedYZ = U.randomPlusMinus(80.);
   stage = Double.MIN_VALUE;
  }
 }

 public void run(Vehicle V, boolean gamePlay) {
  if (stage > 0) {
   if (Y + VP.Y > V.P.localGround || (stage += gamePlay ? U.random(UI.tick) : 0) > 10) {
    stage = 0;
    MV.setVisible(false);
   } else {
    XZ += speedXZ * UI.tick;
    XY += speedXY * UI.tick;
    YZ += speedYZ * UI.tick;
    if (gamePlay) {
     X += speedX * UI.tick;
     Z += speedZ * UI.tick;
     gravitySpeed += E.gravity * UI.tick;
     Y += speedY * UI.tick + (V.P.mode.name().startsWith(SL.drive) ? gravitySpeed * UI.tick : 0);
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
