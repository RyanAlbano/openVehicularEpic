package ve.vehicles;

import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import ve.environment.E;
import ve.instances.Core;
import ve.instances.CoreAdvanced;
import ve.instances.I;
import ve.utilities.Nodes;
import ve.utilities.D;
import ve.utilities.U;

class Chip extends CoreAdvanced {

 private final MeshView MV;
 private final VehiclePart VP;
 private double stage;
 private double gravitySpeed;
 private double speedXZ, speedXY, speedYZ;
 private final Core core = new Core();

 Chip(VehiclePart part) {
  VP = part;
  absoluteRadius = .1 * VP.absoluteRadius;
  core.absoluteRadius = absoluteRadius;//<-Must transfer this--parts were not showing up otherwise!
  TriangleMesh TM = new TriangleMesh();
  setPoints(TM);//<-Call it here as well so mesh loads properly
  TM.getTexCoords().addAll(U.random() < .5 ? I.textureCoordinateBase0 : I.textureCoordinateBase1);
  TM.getFaces().addAll(0, 0, 1, 1, 2, 2);
  TM.getFaces().addAll(2, 2, 1, 1, 0, 0);
  MV = new MeshView(TM);
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

 void deploy(double throwPower) {
  if (stage <= 0 && U.random() < .5) {
   setPoints((TriangleMesh) MV.getMesh());
   X = Y = Z = gravitySpeed = 0;
   XZ = VP.V.XZ;
   XY = VP.V.XY;
   YZ = VP.V.YZ;
   speedX = throwPower * (U.random(3) - 1.);
   speedY = throwPower * (U.random(3) - 1.);
   speedZ = throwPower * (U.random(3) - 1.);
   speedXY = U.randomPlusMinus(80.);
   speedXZ = U.randomPlusMinus(80.);
   speedYZ = U.randomPlusMinus(80.);
   stage = Double.MIN_VALUE;
  }
 }

 public void run(boolean gamePlay) {
  if (stage > 0) {
   if (Y + VP.Y > VP.V.P.localGround || (stage += gamePlay ? U.random(U.tick) : 0) > 10) {
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
     Y += speedY * U.tick + (VP.V.P.mode.name().startsWith(D.drive) ? gravitySpeed * U.tick : 0);
    }
    core.X = X + VP.X;
    core.Y = Y + VP.Y;
    core.Z = Z + VP.Z;
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