package ve.environment;

import java.util.*;

import static ve.VE.*;

import ve.utilities.U;

public class Meteor {

 public double speedX, speedZ;
 public final List<MeteorPart> meteorParts = new ArrayList<>();

 public Meteor(double inSize) {
  double size = 0;
  for (int n1 = 0; n1 < 10; n1++) {
   meteorParts.add(new MeteorPart(inSize - size, 1));
   size += inSize / 11.;
  }
 }

 public void run(boolean update) {
  meteorParts.get(0).onFire = U.random() < .1;
  if (meteorParts.get(meteorParts.size() - 1).Y >= 0) {
   meteorParts.get(0).X = cameraX + U.randomPlusMinus(500000.);
   meteorParts.get(0).Y = -125000 - U.random(250000.);
   meteorParts.get(0).Z = cameraZ + U.randomPlusMinus(500000.);
   double speedsXZ = U.random(2.) * E.meteorSpeed;
   speedX = U.random() < .5 ? speedsXZ : -speedsXZ;
   speedsXZ -= E.meteorSpeed * 2;
   speedZ = U.random() < .5 ? speedsXZ : -speedsXZ;
   for (MeteorPart meteorPart : meteorParts) {
    meteorPart.rotation[0] = U.randomPlusMinus(45.);
    meteorPart.rotation[1] = U.randomPlusMinus(45.);
   }
   meteorParts.get(meteorParts.size() - 1).Y = meteorParts.get(0).Y;
   speedX = meteorParts.get(0).X > cameraX ? -Math.abs(speedX) : meteorParts.get(0).X < cameraX ? Math.abs(speedX) : speedX;
   speedZ = meteorParts.get(0).Z > cameraZ ? -Math.abs(speedZ) : meteorParts.get(0).Z < cameraZ ? Math.abs(speedZ) : speedZ;
  }
  if (update) {
   meteorParts.get(0).X += speedX * tick;
   meteorParts.get(0).Y += E.meteorSpeed * tick;
   meteorParts.get(0).Z += speedZ * tick;
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
  if (!muteSound && update) {
   U.soundLoop(sounds, "meteor" + E.meteors.indexOf(this), Math.sqrt(U.distance(cameraX, meteorParts.get(0).X, cameraY, meteorParts.get(0).Y, cameraZ, meteorParts.get(0).Z)) * .08);
  } else {
   U.soundStop(sounds, "meteor" + E.meteors.indexOf(this));
  }
 }
}
