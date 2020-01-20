package ve.vehicles.specials;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import ve.environment.*;
import ve.instances.CoreAdvanced;
import ve.utilities.Phong;
import ve.utilities.U;

public class PortSmoke extends CoreAdvanced {

 public final Cylinder C;
 final Special S;
 final Port P;
 double stage, speed;
 double sinXZ, cosXZ, sinYZ, cosYZ;//<-Performance optimization

 public PortSmoke(Special special, Port port) {
  S = special;
  P = port;
  absoluteRadius = (S.length + S.width) * .5;
  C = new Cylinder(1, 1);
  PhongMaterial PM = new PhongMaterial();
  double shade = S.type == Special.Type.missile ? .25 : .125;
  Phong.setDiffuseRGB(PM, shade, shade, shade, .25);
  Phong.setSpecularRGB(PM, 0);
  U.setMaterialSecurely(C, PM);
  U.setScale(C, absoluteRadius);
  //(Added with transparent Nodes)
  C.setVisible(false);
 }

 public void deploy(double x, double y, double z, double V_sinXY, double V_cosXY) {
  X = x;
  Y = y;
  Z = z;
  boolean complexAim = S.aimType != Special.AimType.normal;
  double setXZ = S.V.XZ + (complexAim ? S.V.VT.XZ : 0),
  setYZ = S.V.YZ - (complexAim ? S.V.VT.YZ : 0);
  XZ = setXZ + (P.XZ * V_cosXY) + (P.YZ * V_sinXY) * S.V.P.polarity;
  YZ = setYZ + (P.YZ * V_cosXY) + (P.XZ * V_sinXY);
  sinXZ = U.sin(XZ);
  cosXZ = U.cos(XZ);
  sinYZ = U.sin(YZ);
  cosYZ = U.cos(YZ);
  speedX = U.randomPlusMinus(absoluteRadius * 1);
  speedY = U.randomPlusMinus(absoluteRadius * 1);
  speedZ = U.randomPlusMinus(absoluteRadius * 1);
  speed = Math.min(S.speed * .125, 300) * (S.type == Special.Type.missile ? -1 : 1) + (S.V.P.speed * U.cos(Math.abs(S.V.XZ - XZ)));
  stage = Double.MIN_VALUE;
 }

 public void runLogic() {
  if (stage > 0) {
   if (stage > 10) {
    stage = 0;
   } else {
    X += speedX * U.tick;
    Y += speedY * U.tick;
    Z += speedZ * U.tick;
    speed -= speed * .5 * U.tick;
    if (stage != Double.MIN_VALUE) {//<-Not good practice, but does prevent smoke from 'surging' ahead of vehicle while moving
     X -= speed * sinXZ * cosYZ * U.tick;
     Z += speed * cosXZ * cosYZ * U.tick;
     Y -= speed * sinYZ * U.tick;
    }
    X += Wind.speedX * U.tick;
    Z += Wind.speedZ * U.tick;
    stage += U.random(U.tick);
   }
  }
 }

 public void runRender() {
  if (stage > 0 && U.render(this, true, true)) {
   U.randomRotate(C);
   U.setTranslate(C, this);
   C.setVisible(true);
  } else {
   C.setVisible(false);
  }
 }

 static long emitQuantity(Special special) {
  return special.type == Special.Type.missile || special.type == Special.Type.powershell ? 50 : 10;
 }
}
