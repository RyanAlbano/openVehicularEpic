package ve.environment;

import javafx.scene.shape.*;

import static ve.VE.*;

import ve.utilities.U;

public class Cloud extends Sphere {

 private double X;
 private final double Y;
 private double Z;

 public Cloud() {
  super(1000 + U.random(4000));
  X = U.randomPlusMinus(E.cloudWrapDistance);
  Z = U.randomPlusMinus(E.cloudWrapDistance);
  Y = E.cloudProperties[3] + (E.cloudProperties[3] * U.randomPlusMinus(.5));
  setScaleX(8 + U.random(8.));
  setScaleY(1 + U.random());
  setScaleZ(8 + U.random(8.));
  setMaterial(E.cloudPM);
  U.add(this);
 }

 public void run() {
  if (E.wind > 0) {
   X += E.windX * tick + (X < -E.cloudWrapDistance ? E.cloudWrapDistance * 2 : X > E.cloudWrapDistance ? -E.cloudWrapDistance * 2 : 0);
   Z += E.windZ * tick + (Z < -E.cloudWrapDistance ? E.cloudWrapDistance * 2 : Z > E.cloudWrapDistance ? -E.cloudWrapDistance * 2 : 0);
  }
  double size = getRadius() * Math.max(getScaleX(), Math.max(getScaleY(), getScaleZ())), depth = U.getDepth(X, Y, Z);
  if (depth > -size) {
   setCullFace(depth > size ? CullFace.BACK : CullFace.NONE);
   U.setTranslate(this, X, Y, Z);
   setVisible(true);
  } else {
   setVisible(false);
  }
 }
}
