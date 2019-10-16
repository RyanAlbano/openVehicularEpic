package ve.environment;

import javafx.scene.shape.*;

import ve.Core;
import ve.VE;
import ve.utilities.U;

class Cloud extends Core {
 private final Sphere S;

 Cloud() {
  S = new Sphere(1000 + U.random(4000));
  X = U.randomPlusMinus(E.cloudWrapDistance);
  Z = U.randomPlusMinus(E.cloudWrapDistance);
  Y = E.cloudHeight - U.randomPlusMinus(E.cloudHeight * .5);
  S.setScaleX(8 + U.random(8.));
  S.setScaleY(1 + U.random());
  S.setScaleZ(8 + U.random(8.));
  S.setMaterial(E.cloudPM);
  U.rotate(S, 0, U.random(360.));
  U.add(S);
 }

 public void run() {
  if (E.wind > 0) {
   X += E.windX * VE.tick + (X < -E.cloudWrapDistance ? E.cloudWrapDistance * 2 : X > E.cloudWrapDistance ? -E.cloudWrapDistance * 2 : 0);
   Z += E.windZ * VE.tick + (Z < -E.cloudWrapDistance ? E.cloudWrapDistance * 2 : Z > E.cloudWrapDistance ? -E.cloudWrapDistance * 2 : 0);
  }
  double size = S.getRadius() * Math.max(S.getScaleX(), Math.max(S.getScaleY(), S.getScaleZ()));
  if (U.render(this, -size)) {
   S.setCullFace(U.getDepth(this) > size ? CullFace.BACK : CullFace.NONE);
   U.setTranslate(S, this);
   S.setVisible(true);
  } else {
   S.setVisible(false);
  }
 }
}
