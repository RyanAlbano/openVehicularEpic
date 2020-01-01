package ve.environment;

import ve.ui.Match;
import ve.ui.UI;
import ve.utilities.Sound;
import ve.utilities.U;

import static ve.environment.E.graphicsContext;

public enum Wind {
 ;
 public static double maxPotency, speedX, speedZ;
 public static boolean stormExists;
 public static Sound storm;

 static void runPowerSet() {
  if (Wind.maxPotency > 0) {
   Wind.speedX += U.randomPlusMinus(Wind.maxPotency * .1 * UI.tick);
   Wind.speedZ += U.randomPlusMinus(Wind.maxPotency * .1 * UI.tick);
   Wind.speedX -= Wind.speedX * .0004 * UI.tick;
   Wind.speedZ -= Wind.speedZ * .0004 * UI.tick;
   Wind.speedX = U.clamp(-Wind.maxPotency, Wind.speedX, Wind.maxPotency);
   Wind.speedZ = U.clamp(-Wind.maxPotency, Wind.speedZ, Wind.maxPotency);
  }
 }

 static void runStorm(boolean update) {
  if (Wind.stormExists) {
   if (U.FPS < 15) {
    Wind.speedX *= .875;
    Wind.speedZ *= .875;
   }
   double stormPower = Math.sqrt(StrictMath.pow(Wind.speedX, 2) * StrictMath.pow(Wind.speedZ, 2));//<-Multiplied--not added!
   U.fillRGB(graphicsContext, Ground.RGB.getRed(), Ground.RGB.getGreen(), Ground.RGB.getBlue(), U.minimumAccurateLayeredOpacity);
   double dustWidth = UI.width * .25, dustHeight = UI.height * .25;
   for (double n = stormPower * .025; --n >= 0; ) {
    graphicsContext.fillOval(-dustWidth + U.random(UI.width + dustWidth), -dustHeight + U.random(UI.height + dustHeight), dustWidth, dustHeight);
   }
   if (!Match.muteSound && update) {
    Wind.storm.loop(40 - (Math.min(40, 3 * StrictMath.pow(stormPower, .25))));
   } else {
    Wind.storm.stop();
   }
  }
 }
}