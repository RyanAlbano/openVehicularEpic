package ve.vehicles.specials;

import javafx.scene.paint.*;
import javafx.scene.shape.*;
import ve.Core;
import ve.VE;
import ve.utilities.SL;
import ve.utilities.U;
import ve.vehicles.Vehicle;

public class Spit extends Core {

 private final MeshView MV;
 private double stage;

 Spit(Special special) {
  TriangleMesh TM = new TriangleMesh();
  long scale = 4;
  TM.getPoints().setAll(
  0, 0, (float) (scale * -special.length),
  (float) U.randomPlusMinus(scale * special.width), (float) U.randomPlusMinus(scale * special.width), 0,
  (float) U.randomPlusMinus(scale * special.width), (float) U.randomPlusMinus(scale * special.width), 0,
  0, 0, (float) (scale * special.length));
  TM.getTexCoords().setAll(0, 0);
  TM.getFaces().setAll(0, 0, 1, 0, 2, 0, 2, 0, 3, 0, 0, 0);
  MV = new MeshView(TM);
  MV.setCullFace(CullFace.NONE);
  PhongMaterial PM = new PhongMaterial();
  U.Phong.setDiffuseRGB(PM, 0);
  U.Phong.setSpecularRGB(PM, 0);
  U.setMaterialSecurely(MV, PM);
  U.Nodes.add(MV);
  MV.setVisible(false);
 }

 void deploy(Vehicle vehicle, Special special, Port port) {
  boolean complex = special.aimType != Special.AimType.normal;
  U.rotate(MV,
  -(vehicle.YZ - (complex ? vehicle.VT.YZ : 0) + (port.YZ * U.cos(vehicle.XY)) + (port.XZ * U.sin(vehicle.XY))),
  vehicle.XZ + (complex ? vehicle.VT.XZ : 0) + (port.XZ * U.cos(vehicle.XY)) + (port.YZ * U.sin(vehicle.XY)) * vehicle.P.polarity);
  stage = Double.MIN_VALUE;
 }

 public void run(Vehicle vehicle, Special special, Port port, boolean gamePlay) {
  if (stage > 0) {
   boolean longerSpitDuration = special.type.name().contains(Special.Type.shell.name()) || special.type == Special.Type.shotgun || special.type == Special.Type.missile;
   if ((stage += gamePlay ? VE.tick : 0) > (longerSpitDuration ? 3 : 2)) {
    stage = 0;
   } else {
    double[] spitX = {port.X}, spitY = {port.Y}, spitZ = {port.Z};
    if (special.aimType != Special.AimType.normal) {
     U.rotateWithPivot(spitZ, spitY, vehicle.VT.pivotY, vehicle.VT.pivotZ, vehicle.VT.YZ);
     U.rotateWithPivot(spitX, spitZ, 0, vehicle.VT.pivotZ, vehicle.VT.XZ);
    }
    U.rotate(spitX, spitY, vehicle.XY);
    U.rotate(spitY, spitZ, vehicle.YZ);
    U.rotate(spitX, spitZ, vehicle.XZ);
    X = vehicle.X + spitX[0];
    Y = vehicle.Y + spitY[0];
    Z = vehicle.Z + spitZ[0];
    long scale = 4;
    ((TriangleMesh) MV.getMesh()).getPoints().setAll(
    0, 0, (float) (scale * -special.length),
    (float) U.randomPlusMinus(scale * special.width), (float) U.randomPlusMinus(scale * special.width), 0,
    (float) U.randomPlusMinus(scale * special.width), (float) U.randomPlusMinus(scale * special.width), 0,
    0, 0, (float) (scale * special.length));
   }
  }
 }

 public void render() {
  if (stage > 0 && U.render(this)) {
   U.setTranslate(MV, this);
   ((PhongMaterial) MV.getMaterial()).setSelfIlluminationMap(U.Images.get(SL.Images.fireLight + U.random(3)));
   MV.setVisible(true);
  } else {
   MV.setVisible(false);
  }
 }
}
