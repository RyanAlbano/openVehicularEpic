package ve.environment;

import ve.ui.Match;
import ve.ui.UI;
import ve.utilities.U;
import ve.utilities.sound.Controlled;

public enum Wind {
 ;
 public static double maxPotency, speedX, speedZ;
 public static boolean stormExists;
 public static Controlled storm;

 static void runSetPower() {
  if (maxPotency > 0) {
   speedX += U.randomPlusMinus(maxPotency * .1 * U.tick);
   speedZ += U.randomPlusMinus(maxPotency * .1 * U.tick);
   speedX -= speedX * .0004 * U.tick;
   speedZ -= speedZ * .0004 * U.tick;
   speedX = U.clamp(-maxPotency, speedX, maxPotency);
   speedZ = U.clamp(-maxPotency, speedZ, maxPotency);
  }
 }

 static void runStorm(boolean update) {
  if (stormExists) {
   if (U.FPS < 15) {
    speedX *= .875;
    speedZ *= .875;
   }
   double stormPower = Math.sqrt(StrictMath.pow(speedX, 2) * StrictMath.pow(speedZ, 2));//<-Multiplied--not added!
   U.fillRGB(E.GC, Ground.RGB.getRed(), Ground.RGB.getGreen(), Ground.RGB.getBlue(), U.minimumAccurateLayeredOpacity);
   double dustWidth = UI.width * .25, dustHeight = UI.height * .25;
   for (double n = stormPower * .025; --n >= 0; ) {
    E.GC.fillOval(-dustWidth + U.random(UI.width + dustWidth), -dustHeight + U.random(UI.height + dustHeight), dustWidth, dustHeight);
   }
   if (!Match.muteSound && update) {
    storm.loop(40 - (Math.min(40, 3 * StrictMath.pow(stormPower, .25))));
   } else {
    storm.stop();
   }
  }
 }

 static void reset() {
  maxPotency = speedX = speedZ = 0;
  stormExists = false;
 }

 public static void closeSound() {
  if (storm != null) storm.close();
 }
}