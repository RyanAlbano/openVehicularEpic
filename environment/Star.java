package ve.environment;

import javafx.scene.shape.Sphere;
import ve.utilities.U;

public class Star extends Sphere {

 private final double X;
 private final double Y;
 private final double Z;

 public Star() {
  super(200000000, 5);
  double distance = 40000000000.;
  double[] rotateX = {distance + U.random(distance * 4.)}, rotateY = {distance + U.random(distance * 4.)}, rotateZ = {distance + U.random(distance * 4.)};
  U.rotate(rotateX, rotateZ, U.random(360.));
  U.rotate(rotateX, rotateY, U.random(360.));
  U.rotate(rotateZ, rotateY, U.random(360.));
  X = rotateX[0];
  Z = rotateZ[0];
  Y = E.groundLevel <= 0 ? -Math.abs(rotateY[0]) : rotateY[0];
  setMaterial(E.starPM);
  U.add(this);
 }

 public void run() {
  U.render(this, X, Y, Z, -getRadius());
 }
}
