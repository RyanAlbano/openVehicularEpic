package ve.vehicles;

import javafx.scene.shape.*;
import ve.environment.E;
import ve.instances.Core;
import ve.instances.CoreAdvanced;
import ve.instances.I;
import ve.utilities.Nodes;
import ve.utilities.SL;
import ve.utilities.U;

class Chip extends CoreAdvanced {

 private final MeshView MV;
 private final VehiclePart VP;
 private double stage;
 private double gravitySpeed;
 private double speedXZ, speedXY, speedYZ;
 final Core core = new Core();

 Chip(VehiclePart vp) {
  VP = vp;
  absoluteRadius = .1 * VP.absoluteRadius;
  TriangleMesh chipMesh = new TriangleMesh();
  setPoints(chipMesh);//<-Call it here as well so mesh loads properly
  chipMesh.getTexCoords().addAll(U.random() < .5 ? I.textureCoordinateBase0 : I.textureCoordinateBase1);
  chipMesh.getFaces().addAll(0, 0, 1, 1, 2, 2);
  chipMesh.getFaces().addAll(2, 2, 1, 1, 0, 0);
  MV = new MeshView(chipMesh);
  MV.setCullFace(CullFace.BACK);
  U.setMaterialSecurely(MV, VP.PM);
  Nodes.add(MV);
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
   if (Y + VP.Y > V.P.localGround || (stage += gamePlay ? U.random(U.tick) : 0) > 10) {
    stage = 0;
    MV.setVisible(false);
   } else {
    XZ += speedXZ * U.tick;
    XY += speedXY * U.tick;
    YZ += speedYZ * U.tick;
    if (gamePlay) {
     X += speedX * U.tick;
     Z += speedZ * U.tick;
     gravitySpeed += E.gravity * U.tick;
     Y += speedY * U.tick + (V.P.mode.name().startsWith(SL.drive) ? gravitySpeed * U.tick : 0);
    }
    core.X = X + VP.X;
    core.Y = X + VP.Y;
    core.Z = X + VP.Z;
    if (VP.MV.isVisible() && U.render(core, absoluteRadius, true, true)) {
     U.setTranslate(MV, core);
     U.rotate(MV, this);
     MV.setVisible(true);
    } else {
     MV.setVisible(false);
    }
   }
  }
 }
}
