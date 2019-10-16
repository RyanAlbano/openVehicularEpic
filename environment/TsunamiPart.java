package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

import ve.Core;
import ve.VE;
import ve.utilities.U;

public class TsunamiPart extends Core {
 public final Cylinder C;

 TsunamiPart(double size) {
  C = new Cylinder(size, size);//<-Keep both parameters--not sure if Cylinder(double) yields same result
  C.setMaterial(new PhongMaterial());
 }

 public void run(boolean update) {
  if (update) {
   X += E.tsunamiSpeedX * VE.tick;
   Z += E.tsunamiSpeedZ * VE.tick;
  }
  if (U.render(this, -C.getRadius())) {
   U.setTranslate(C, this);
   U.randomRotate(C);
   double waveRG = U.random(2.);
   if (E.poolType == E.Pool.lava) {
    U.setDiffuseRGB((PhongMaterial) C.getMaterial(), 1, waveRG, waveRG * .5);
   } else if (E.poolType == E.Pool.acid) {
    U.setDiffuseRGB((PhongMaterial) C.getMaterial(), waveRG * .5, 1, waveRG);
   } else {
    U.setDiffuseRGB((PhongMaterial) C.getMaterial(), waveRG * .5, waveRG, 1);
   }
   C.setVisible(true);
  } else {
   C.setVisible(false);
  }
 }
}
