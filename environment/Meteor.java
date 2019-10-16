package ve.environment;

import java.util.*;

import ve.Camera;
import ve.Sound;
import ve.VE;
import ve.utilities.U;

public class Meteor {

 public double speedX, speedZ;
 public final List<MeteorPart> meteorParts = new ArrayList<>();
 public final Sound sound;

 Meteor(double inSize) {
  double size = 0;
  for (int n1 = 0; n1 < 10; n1++) {
   meteorParts.add(new MeteorPart(inSize - size));
   size += inSize / 11.;
  }
  sound = new Sound("meteor" + U.random(2));//<-using lists--NO double
 }

 public void run(boolean update) {
  meteorParts.get(0).onFire = U.random() < .1;
  boolean deploy = false;
  if (update) {
   meteorParts.get(0).X += speedX * VE.tick;
   meteorParts.get(0).Y += E.meteorSpeed * VE.tick;
   meteorParts.get(0).Z += speedZ * VE.tick;
   deploy = Math.abs(meteorParts.get(0).Y - Camera.Y) > 375000 || Math.abs(meteorParts.get(0).X - Camera.X) > 500000 || Math.abs(meteorParts.get(0).Z - Camera.Z) > 500000;
  }
  if (meteorParts.get(meteorParts.size() - 1).Y >= 0 || deploy) {
   meteorParts.get(0).X = Camera.X + U.randomPlusMinus(500000.);
   meteorParts.get(0).Y = Camera.Y - 125000 - U.random(250000.);
   meteorParts.get(0).Z = Camera.Z + U.randomPlusMinus(500000.);
   double speedsXZ = U.random(2.) * E.meteorSpeed;
   speedX = U.random() < .5 ? speedsXZ : -speedsXZ;
   speedsXZ -= E.meteorSpeed * 2;
   speedZ = U.random() < .5 ? speedsXZ : -speedsXZ;
   for (MeteorPart meteorPart : meteorParts) {
    meteorPart.rotation[0] = U.randomPlusMinus(45.);
    meteorPart.rotation[1] = U.randomPlusMinus(45.);
   }
   meteorParts.get(meteorParts.size() - 1).Y = meteorParts.get(0).Y;
   speedX = meteorParts.get(0).X > Camera.X ? -Math.abs(speedX) : meteorParts.get(0).X < Camera.X ? Math.abs(speedX) : speedX;
   speedZ = meteorParts.get(0).Z > Camera.Z ? -Math.abs(speedZ) : meteorParts.get(0).Z < Camera.Z ? Math.abs(speedZ) : speedZ;
  }
  for (int n = 1; n < meteorParts.size(); n++) {
   meteorParts.get(n).X = meteorParts.get(0).X - (speedX * n);
   meteorParts.get(n).Y = meteorParts.get(0).Y - (E.meteorSpeed * n);
   meteorParts.get(n).Z = meteorParts.get(0).Z - (speedZ * n);
  }
  for (int n = meteorParts.size(); --n > 0; ) {
   meteorParts.get(n).onFire = meteorParts.get(n - 1).onFire;
  }
  for (MeteorPart meteorPart : meteorParts) {
   meteorPart.run();
  }
  if (!VE.muteSound && update) {
   sound.loop(Math.sqrt(U.distance(meteorParts.get(0))) * .08);
  } else {
   sound.stop();
  }
 }
}
