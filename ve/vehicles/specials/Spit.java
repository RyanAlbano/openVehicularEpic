package ve.vehicles.specials;

import javafx.scene.paint.*;
import javafx.scene.shape.*;
import ve.effects.Effects;
import ve.instances.Core;
import ve.utilities.Nodes;
import ve.utilities.Phong;
import ve.utilities.U;

public class Spit extends Core {
 final Special S;
 final Port P;
 private final MeshView MV;
 private double stage;
 static final long scale = 4;

 Spit(Special special, Port port) {
  S = special;
  P = port;
  TriangleMesh TM = new TriangleMesh();
  setMesh(TM);
  TM.getTexCoords().setAll(0, 0);
  TM.getFaces().setAll(0, 0, 1, 0, 2, 0, 2, 0, 3, 0, 0, 0);
  MV = new MeshView(TM);
  MV.setCullFace(CullFace.NONE);
  PhongMaterial PM = new PhongMaterial();
  Phong.setDiffuseRGB(PM, 0);
  Phong.setSpecularRGB(PM, 0);
  U.setMaterialSecurely(MV, PM);
  Nodes.add(MV);
  MV.setVisible(false);
 }

 void deploy(double V_sinXY, double V_cosXY) {
  boolean complex = S.aimType != Special.AimType.normal;
  U.rotate(MV,
  -(S.V.YZ - (complex ? S.V.VT.YZ : 0) + (P.YZ * V_cosXY) + (P.XZ * V_sinXY)),
  S.V.XZ + (complex ? S.V.VT.XZ : 0) + (P.XZ * V_cosXY) + (P.YZ * V_sinXY) * S.V.P.polarity);
  stage = Double.MIN_VALUE;
 }

 long duration() {
  return S.type.name().contains(Special.Type.shell.name()) || S.type == Special.Type.shotgun || S.type == Special.Type.missile ? 3 : 2;
 }

 public void runLogic(boolean gamePlay, double V_sinXZ, double V_cosXZ, double V_sinYZ, double V_cosYZ, double V_sinXY, double V_cosXY) {
  if (stage > 0) {
   if ((stage += gamePlay ? U.tick : 0) > duration()) {
    stage = 0;
   } else {
    double[] spitX = {P.X}, spitY = {P.Y}, spitZ = {P.Z};
    if (S.aimType != Special.AimType.normal) {
     U.rotateWithPivot(spitZ, spitY, S.V.VT.pivotY, S.V.VT.pivotZ, S.V.VT.YZ);
     U.rotateWithPivot(spitX, spitZ, 0, S.V.VT.pivotZ, S.V.VT.XZ);
    }
    U.rotate(spitX, spitY, V_sinXY, V_cosXY);
    U.rotate(spitY, spitZ, V_sinYZ, V_cosYZ);
    U.rotate(spitX, spitZ, V_sinXZ, V_cosXZ);
    //^fixme--these rotations only work correctly on gun ports that shoot directly forward
    X = S.V.X + spitX[0];
    Y = S.V.Y + spitY[0];
    Z = S.V.Z + spitZ[0];
    setMesh((TriangleMesh) MV.getMesh());
   }
  }
 }

 void setMesh(TriangleMesh TM) {
  TM.getPoints().setAll(
  0, 0, (float) (scale * -S.length),
  (float) U.randomPlusMinus(scale * S.width), (float) U.randomPlusMinus(scale * S.width), 0,
  (float) U.randomPlusMinus(scale * S.width), (float) U.randomPlusMinus(scale * S.width), 0,
  0, 0, (float) (scale * S.length));
 }

 public void runRender() {
  if (stage > 0 && U.render(this, false, true)) {
   U.setTranslate(MV, this);
   ((PhongMaterial) MV.getMaterial()).setSelfIlluminationMap(Effects.fireLight());
   MV.setVisible(true);
  } else {
   MV.setVisible(false);
  }
 }
}
