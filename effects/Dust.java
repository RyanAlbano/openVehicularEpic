package ve.effects;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import ve.VE;
import ve.environment.*;
import ve.utilities.U;
import ve.vehicles.*;

public class Dust extends Cylinder {

 private double X;
 private double Y;
 private double Z;
 private double size;
 private double stage;
 private double speedX;
 private double speedZ;
 private double duration;

 public Dust() {
  super(1, 1);
  PhongMaterial PM = new PhongMaterial();
  U.setSpecularRGB(PM, 0, 0, 0);
  setMaterial(PM);
  U.add(this);
 }

 public void deploy(Vehicle vehicle, Wheel wheel, double dustSpeed, double speedDifference) {
  X = vehicle.X + (dustSpeed > 0 ? U.randomPlusMinus(vehicle.collisionRadius) : 0);
  Z = vehicle.Z + (dustSpeed > 0 ? U.randomPlusMinus(vehicle.collisionRadius) : 0);
  Y = vehicle.Y + vehicle.clearanceY;
  speedX = speedZ = vehicle.flipped ? 0 : speedDifference;
  double presence = vehicle.terrainProperties.contains(" hard ") ? 1 : 2;
  size = .05 * vehicle.absoluteRadius * Math.sqrt(U.random()) * presence;
  duration = 2 * presence;
  U.setScale(this, size);
  U.setDiffuseRGB((PhongMaterial) getMaterial(), wheel.terrainRGB[0], wheel.terrainRGB[1], wheel.terrainRGB[2], .5);
  stage = Double.MIN_VALUE;
 }

 public void deploy(Boulder boulder) {
  X = boulder.X + U.randomPlusMinus(boulder.getRadius() * 1.5);
  Z = boulder.Z + U.randomPlusMinus(boulder.getRadius() * 1.5);
  size = .5 * boulder.getRadius() * Math.sqrt(U.random());
  duration = 4 + U.random(4.);
  U.setScale(this, size);
  U.setDiffuseRGB((PhongMaterial) getMaterial(), E.groundRGB[0], E.groundRGB[1], E.groundRGB[2], .5);
  stage = Double.MIN_VALUE;
 }

 public void run() {
  if (stage > 0) {
   boolean show = false;
   if ((stage += U.random(VE.tick)) > duration) {
    stage = 0;
   } else {
    boolean wind = E.wind > 0;
    X += U.randomPlusMinus(speedX) + (wind ? E.windX * VE.tick : 0);
    Z += U.randomPlusMinus(speedZ) + (wind ? E.windZ * VE.tick : 0);
    if (U.getDepth(X, Y, Z) > 0 && size * VE.renderLevel >= U.distance(X, VE.cameraX, Y, VE.cameraY, Z, VE.cameraZ) * VE.zoom) {
     U.randomRotate(this);
     U.setTranslate(this, X, Y, Z);
     show = true;
    }
   }
   setVisible(show);
  }
 }
}
