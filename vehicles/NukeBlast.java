package ve.vehicles;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.Core;
import ve.utilities.U;

class NukeBlast extends Core {

 private final Sphere S;

 NukeBlast(PhongMaterial PM) {
  S = new Sphere(10000, 1);
  S.setMaterial(PM);
  U.randomRotate(S);
  U.add(S);
  S.setVisible(false);
 }

 void run(boolean gamePlay, double blastSpeed) {
  if (gamePlay) {
   X += blastSpeed * U.sin(XZ) * U.cos(YZ);
   Y += blastSpeed * U.sin(YZ);
   Z += blastSpeed * U.cos(XZ) * U.cos(YZ);
  }
  if (!U.outOfBounds(this, S.getRadius()) && U.render(this)) {
   U.setTranslate(S, this);
   S.setVisible(true);
  } else {
   S.setVisible(false);
  }
 }
}
