package ve.effects;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import ve.Core;
import ve.VE;
import ve.environment.*;
import ve.utilities.U;
import ve.vehicles.*;

public class Dust extends Core {

 private final Cylinder C;
 private double stage;
 private double speedX, speedZ;
 private double duration;

 public Dust() {
  C = new Cylinder(1, 1);
  PhongMaterial PM = new PhongMaterial();
  U.Phong.setSpecularRGB(PM, 0);
  U.setMaterialSecurely(C, PM);
  U.Nodes.add(C);
 }

 public void deploy(Vehicle vehicle, Wheel wheel, double dustSpeed, double speedDifference) {
  X = vehicle.X + (dustSpeed > 0 ? U.randomPlusMinus(vehicle.collisionRadius()) : 0);
  Z = vehicle.Z + (dustSpeed > 0 ? U.randomPlusMinus(vehicle.collisionRadius()) : 0);
  Y = vehicle.Y + vehicle.clearanceY;
  speedX = speedZ = vehicle.P.flipped ? 0 : speedDifference;
  double presence = vehicle.P.terrainProperties.contains(" hard ") ? 1 : 2;
  absoluteRadius = .05 * vehicle.absoluteRadius * Math.sqrt(U.random()) * presence;
  duration = 2 * presence;
  U.setScale(C, absoluteRadius);
  U.Phong.setDiffuseRGB((PhongMaterial) C.getMaterial(), wheel.terrainRGB, .5);
  stage = Double.MIN_VALUE;
 }

 public void deploy(Boulder.Instance boulder) {
  X = boulder.X + U.randomPlusMinus(boulder.S.getRadius() * 1.5);
  Z = boulder.Z + U.randomPlusMinus(boulder.S.getRadius() * 1.5);
  absoluteRadius = .5 * boulder.S.getRadius() * Math.sqrt(U.random());
  duration = 4 + U.random(4.);
  U.setScale(C, absoluteRadius);
  U.Phong.setDiffuseRGB((PhongMaterial) C.getMaterial(), E.Ground.RGB, .5);
  stage = Double.MIN_VALUE;
 }

 public void run() {
  if (stage > 0) {
   boolean show = false;
   if ((stage += U.random(VE.tick)) > duration) {
    stage = 0;
   } else {
    X += U.randomPlusMinus(speedX) + (E.Wind.speedX * VE.tick);
    Z += U.randomPlusMinus(speedZ) + (E.Wind.speedZ * VE.tick);
    if (U.renderWithLOD(this)) {
     U.randomRotate(C);
     U.setTranslate(C, this);
     show = true;
    }
   }
   C.setVisible(show);
  }
 }
}
