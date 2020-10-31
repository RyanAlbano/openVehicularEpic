package ve.ui;

import javafx.scene.Cursor;
import ve.environment.E;
import ve.environment.Ground;
import ve.environment.Terrain;
import ve.instances.I;
import ve.trackElements.Bonus;
import ve.trackElements.TE;
import ve.trackElements.Waypoint;
import ve.ui.options.Options;
import ve.ui.options.Units;
import ve.utilities.*;
import ve.utilities.sound.Sounds;
import ve.vehicles.Physics;
import ve.vehicles.Vehicle;

public enum Match {
 ;
 public static boolean cursorDriving;
 public static boolean started;
 public static double timeLeft;
 private static double stuntTimer;
 private static String stuntPrint = "";
 public static double vehicleLightBrightnessChange;
 public static final long[] scoreWaypoint = new long[2];
 public static final long[] scoreLap = new long[2];
 public static final long[] scoreStunt = new long[2];//<-Is a long to keep the AI stunt behavior in check more easily, since there's no decimal
 private static final double[] finalScore = new double[2];
 static long MVP;

 public static void run(boolean gamePlay) {
  if (timeLeft > 0 && UI.status == UI.Status.play && started) {
   timeLeft -= U.tickSeconds;
  }
  Tournament.finished = Tournament.stage > 0 && timeLeft <= 0 &&
  ((Tournament.stage > 4 && Math.abs(Tournament.wins[0] - Tournament.wins[1]) > 0) || (Tournament.stage > 2 && Math.abs(Tournament.wins[0] - Tournament.wins[1]) > 1));
  if (started && (Keys.enter || Keys.escape) && gamePlay) {
   Keys.up = Keys.down = Keys.enter = Keys.escape = false;
   UI.selected = 0;
   UI.sound.play(1, 0);
   UI.status = UI.Status.paused;
  }
  Vehicle V = I.vehicles.get(I.vehiclePerspective);
  if (Options.headsUpDisplay) {
   U.font(.0125);
   if (stuntTimer > 0 && !V.P.flipped() && V.P.flipTimer <= 0) {
    U.fillRGB(0, U.yinYang ? 1 : 0, 0);
    U.text(stuntPrint, .13);
   }//Stunt display is less important, thus drawn earlier
   stuntTimer -= gamePlay ? U.tick : 0;
   runIndicators(V);
   if (!started && !Network.waiting) {//<-Drawing these texts AFTER indicators
    U.font(.02);
    U.fillRGB(U.yinYang ? 0 : 1);
    if (I.vehicles.get(I.vehiclePerspective).isFixed() && (I.vehiclesInMatch < 2 || I.vehiclePerspective < I.halfThePlayers())) {
     U.text("Use Arrow Keys and < and > to place your infrastructure, then", .2);
     if (Keys.up || Keys.down || Keys.left || Keys.right) {
      UI.movementSpeedMultiple = Math.max(10, UI.movementSpeedMultiple * 1.05);
      I.vehicles.get(I.vehiclePerspective).Z += Keys.up ? UI.movementSpeedMultiple * U.tick : 0;
      I.vehicles.get(I.vehiclePerspective).Z -= Keys.down ? UI.movementSpeedMultiple * U.tick : 0;
      I.vehicles.get(I.vehiclePerspective).X -= Keys.left ? UI.movementSpeedMultiple * U.tick : 0;
      I.vehicles.get(I.vehiclePerspective).X += Keys.right ? UI.movementSpeedMultiple * U.tick : 0;
     } else {
      UI.movementSpeedMultiple = 0;
     }
    }
    U.text("Press SPACE to Begin" + (Tournament.stage > 0 ? " Round " + Tournament.stage : ""), .25);
   }
   runMatchEndInfo();
   if (V.destroyed && V.explosionType != Vehicle.ExplosionType.maxnuclear) {
    U.font(.02);
    if (U.yinYang) {
     U.fillRGB(1);
     U.text(".. REVIVING.. ", .3);
    } else {
     U.fillRGB(0);
     U.text(" ..REVIVING ..", .3);
    }
   }
   U.font(.01);
   //U.textR(String.valueOf((V.scoreWaypoint + 1) * (V.scoreLap + 1) * (V.scoreStunt * stuntDecimate + 1) * (V.scoreDamage * damageDecimate + 1) * (V.scoreKill + 1)), .9, .525);
   //U.textR(String.valueOf(V.P.directionAgainstSpeed()), .9, .525);
   //U.textR(String.valueOf(V.wheels.get(2).Y), .9, .55);
   //U.textR(String.valueOf(V.wheels.get(3).Y), .9, .575);
   U.fillRGB(0, 0, 0, UI.colorOpacity.minimal);
   U.fillRectangle(.025, .8, .05, .425);
   U.fillRectangle(.975, .8, .05, .425);
   MatchLog.run();
   runHUDBlocks(V);
   if (Network.mode == Network.Mode.JOIN && Network.hostLeftMatch) {
    U.font(.02);
    U.fillRGB(U.yinYang ? 1 : .5);
    U.text("The Host has left match--hit Enter to start another match", .9);
   } else if (V.P.mode == Physics.Mode.fly && E.gravity != 0 && U.sin(V.YZ) > 0 && V.speedY + V.P.stallSpeed > 0) {
    U.fillRGB(U.yinYang ? 1 : 0);
    U.text("STALL", .95);
   }
  }
  scoreWaypoint[0] = 1;
  scoreWaypoint[1] = 1;
  scoreLap[0] = 1;
  scoreLap[1] = 1;
  scoreStunt[0] = 0;
  scoreStunt[1] = 0;
  double scoreDamage0 = 0, scoreDamage1 = 0;
  long scoreKill0 = 1, scoreKill1 = 1;
  if (I.vehiclesInMatch > 1) {
   for (int n = I.halfThePlayers(); --n >= 0; ) {
    scoreWaypoint[0] += I.vehicles.get(n).scoreWaypoint;
    scoreLap[0] += I.vehicles.get(n).scoreLap;
    scoreStunt[0] += I.vehicles.get(n).scoreStunt;
    scoreDamage0 += I.vehicles.get(n).scoreDamage;
    scoreKill0 += I.vehicles.get(n).scoreKill;
   }
  }
  for (int n = I.vehiclesInMatch; --n >= I.halfThePlayers(); ) {
   scoreWaypoint[1] += I.vehicles.get(n).scoreWaypoint;
   scoreLap[1] += I.vehicles.get(n).scoreLap;
   scoreStunt[1] += I.vehicles.get(n).scoreStunt;
   scoreDamage1 += I.vehicles.get(n).scoreDamage;
   scoreKill1 += I.vehicles.get(n).scoreKill;
  }
  scoreStunt[0] *= stuntDecimate;
  scoreStunt[1] *= stuntDecimate;
  scoreStunt[0] += 1;
  scoreStunt[1] += 1;
  scoreDamage0 *= damageDecimate;
  scoreDamage1 *= damageDecimate;
  scoreDamage0 += 1;
  scoreDamage1 += 1;
  double[] score = {
  scoreWaypoint[0] * scoreLap[0] * scoreStunt[0] * scoreDamage0 * scoreKill0,
  scoreWaypoint[1] * scoreLap[1] * scoreStunt[1] * scoreDamage1 * scoreKill1};
  if (Bonus.holder > -1) {
   score[Bonus.holder < I.halfThePlayers() ? 0 : 1] *= 2;
  }
  if (Options.headsUpDisplay) {
   U.font(.00875);
   U.fillRGB(0, 0, 0, UI.colorOpacity.minimal);
   U.fillRectangle(.9375, .26, .125, .2);
   String bonus = "BONUS (Player ", currentScore = "Current Score: ",
   waypoints = "Waypoints: ", laps = "Laps: ", stunts = "Stunts: ", damageDealt = "Damage Dealt: ", kills = "Kills: ";
   if (I.vehiclesInMatch > 1) {
    U.fillRectangle(.0625, .26, .125, .2);
    //GREEN
    U.fillRGB(0, 1, 0);
    U.textL(I.vehiclesInMatch > 2 ? UI.GREEN_TEAM : UI.playerNames[0], .0125, .175);
    if (Bonus.holder > -1 && Bonus.holder < I.halfThePlayers()) {
     //UI.drawBonus(.11, .3125, 20);
     U.textL("(Player " + Bonus.holder + ") BONUS", .0125, .325);
    }
    U.textL(U.DF.format(score[0]) + " :Current Score", .0125, .35);
    if (!TE.waypoints.isEmpty()) {
     U.fillRGB(0, 1, 0, U.yinYang || scoreWaypoint[0] >= scoreWaypoint[1] ? 1 : .5);
     U.textL(scoreWaypoint[0] + " :Waypoints", .0125, .2);
     U.fillRGB(0, 1, 0, U.yinYang || scoreLap[0] >= scoreLap[1] ? 1 : .5);
     U.textL(scoreLap[0] + " :Laps", .0125, .225);
    }
    U.fillRGB(0, 1, 0, U.yinYang || scoreStunt[0] >= scoreStunt[1] ? 1 : .5);
    U.textL(scoreStunt[0] + " :Stunts", .0125, .25);
    U.fillRGB(0, 1, 0, U.yinYang || scoreDamage0 >= scoreDamage1 ? 1 : .5);
    U.textL(U.DF.format(scoreDamage0) + " :Damage Dealt", .0125, .275);
    U.fillRGB(0, 1, 0, U.yinYang || scoreKill0 >= scoreKill1 ? 1 : .5);
    U.textL(scoreKill0 + " :Kills", .0125, .3);
    //RED
    U.fillRGB(1, 0, 0);
    U.textR(I.vehiclesInMatch > 2 ? UI.RED_TEAM : UI.playerNames[1], .9875, .175);
    if (Bonus.holder >= I.halfThePlayers()) {
     //UI.drawBonus(.89, .3125, 20);
     U.textR(bonus + Bonus.holder + ")", .9875, .325);
    }
    U.textR(currentScore + U.DF.format(score[1]), .9875, .35);
    if (!TE.waypoints.isEmpty()) {
     U.fillRGB(1, 0, 0, U.yinYang || scoreWaypoint[1] >= scoreWaypoint[0] ? 1 : .5);
     U.textR(waypoints + scoreWaypoint[1], .9875, .2);
     U.fillRGB(1, 0, 0, U.yinYang || scoreLap[1] >= scoreLap[0] ? 1 : .5);
     U.textR(laps + scoreLap[1], .9875, .225);
    }
    U.fillRGB(1, 0, 0, U.yinYang || scoreStunt[1] >= scoreStunt[0] ? 1 : .5);
    U.textR(stunts + scoreStunt[1], .9875, .25);
    U.fillRGB(1, 0, 0, U.yinYang || scoreDamage1 >= scoreDamage0 ? 1 : .5);
    U.textR(damageDealt + U.DF.format(scoreDamage1), .9875, .275);
    U.fillRGB(1, 0, 0, U.yinYang || scoreKill1 >= scoreKill0 ? 1 : .5);
    U.textR(kills + scoreKill1, .9875, .3);
   } else {
    U.fillRGB(1);
    U.textR("YOU", .9875, .175);
    if (!TE.waypoints.isEmpty()) {
     U.textR(waypoints + scoreWaypoint[1], .9875, .2);
     U.textR(laps + scoreLap[1], .9875, .225);
    }
    U.textR(stunts + scoreStunt[1], .9875, .25);
    U.textR(damageDealt + U.DF.format(scoreDamage1), .9875, .275);
    U.textR(kills + scoreKill1, .9875, .3);
    if (Bonus.holder >= I.halfThePlayers()) {
     U.textR(bonus + Bonus.holder + ")", .9875, .325);
    }
    U.textR(currentScore + U.DF.format(score[1]), .9875, .35);
   }
  }
  if (timeLeft < 0) {
   finalScore[0] = score[0];
   finalScore[1] = score[1];
   boolean tied = tied();
   if (I.vehiclesInMatch > 1 && Options.headsUpDisplay) {
    if (tied) {
     Sounds.finish.play(0, 0);
     Sounds.finish.play(1, 0);
    } else {
     long side =
     (score[0] > score[1] && I.vehiclePerspective < I.halfThePlayers()) ||
     (score[1] > score[0] && I.vehiclePerspective >= I.halfThePlayers()) ? 0 :
     1;
     Sounds.finish.play(side, 0);
    }
   }
   if (!tied) {
    Tournament.wins[score[0] > score[1] ? 0 : score[1] > score[0] ? 1 : -1]++;
   }
   getMVP();
   timeLeft = 0;
  }
 }

 static boolean tied() {
  return U.DF.format(finalScore[0]).equals(U.DF.format(finalScore[1]));
 }

 private static void runHUDBlocks(Vehicle V) {
  {//LEFT HUD BLOCK
   U.font(.0125);
   if (!V.isFixed()) {
    U.fillRGB(.75);
    U.fillRectangle(.025, .7, .01, Math.min(.2, .2 * (Math.abs(V.P.speed) / V.topSpeeds[1])));
    U.fillRGB(1);
    U.fillRectangle(.025, .6, .02, .001);
    U.fillRectangle(.025, .8, .02, .001);
    double speed = Units.getSpeed(V.P.speed);
    U.text(Math.abs(speed) >= 10000 ? U.DF.format(speed) : String.valueOf(Math.round(speed)), .025, .7);
    U.text(Units.getSpeedName(), .025, .825);
   }
   U.fillRGB(1);
   U.font(.0075);
   U.text("(" + Units.getDistanceName() + ")", .025, .90);
   double distanceConverted = Units.getDistance(1);
   U.textL("X: " + U.DF.format(V.X * distanceConverted), .00625, .92);
   U.textL("Y: " + U.DF.format(V.Y * distanceConverted), .00625, .94);
   U.textL("Z: " + U.DF.format(V.Z * distanceConverted), .00625, .96);
   U.font(.015);
  }
  {//RIGHT HUD BLOCK
   double damage = V.getDamage(true);
   U.fillRGB(1, 1 - damage, 0);
   U.fillRectangle(.975, .7, .01, Math.min(.2, .2 * damage));
   U.fillRGB(1);
   U.fillRectangle(.975, .6, .02, .001);
   U.fillRectangle(.975, .8, .02, .001);
   U.text(Math.round(100 * damage) + "%", .975, .7);
   U.font(.0075);
   U.text("DAMAGE", .975, .825);
   if (I.vehiclesInMatch > 1) {
    U.text("Vehicle #", .975, .865);
    if (I.vehiclesInMatch > 2) {
     if (I.vehiclePerspective < I.halfThePlayers()) {
      U.fillRGB(0, 1, 0);
     } else {
      U.fillRGB(1, 0, 0);
     }
    }
    U.font(.01);
    U.text(I.vehiclePerspective + (I.vehiclePerspective == I.userPlayerIndex ? " (You)" : ""), .975, .89);
   }
   U.fillRGB(1);
   U.font(.01);
   U.text("Time", .975, .925);
   U.text("Left:", .975, .94);
   U.font(.015);
   U.text(String.valueOf(Math.round(timeLeft)), .975, .965);
   if (V.amphibious == Vehicle.Amphibious.ON) {
    Images.draw(Images.amphibious, .025, .85);
   } else if (V.bumpIgnore) {
    drawBumpIgnore();
   }
  }
 }

 private static void drawBumpIgnore() {
  double top = UI.height * .84, bottom = UI.height * .87,
  hardLeft = UI.width * .01, left = UI.width * .02, right = UI.width * .03, hardRight = UI.width * .04;
  double[]
  X = {hardLeft, left, right, hardRight},
  Z = {bottom, top, top, bottom};
  U.fillRGB(Terrain.RGB);
  UI.GC.fillPolygon(X, Z, 4);
  U.strokeRGB(1, 0, 0);
  UI.GC.strokeLine(hardLeft, top, hardRight, bottom);
  UI.GC.strokeLine(hardRight, top, hardLeft, bottom);
 }

 private static void runMatchEndInfo() {
  if (timeLeft == 0) {//<-todo--was '<='
   double titleHeight = .175;
   if (I.vehiclesInMatch > 1) {
    String[] formatFinal = {U.DF.format(finalScore[0]), U.DF.format(finalScore[1])};
    if (formatFinal[0].equals(formatFinal[1])) {
     U.font(.025);
     U.fillRGB(0);
     String tie = "IT'S A TIE!";
     U.text(tie, titleHeight - .001);
     U.text(tie, titleHeight + .001);
     U.fillRGB(1);
     U.text(tie, titleHeight);
    } else {
     String announce = Tournament.stage > 0 && !Tournament.finished ? "ROUND " + Tournament.stage + " OVER" : (finalScore[0] > finalScore[1] ? "GREEN" : "RED") + " TEAM WINS" + (Tournament.finished ? " THE TOURNAMENT!" : "!");
     U.font(.025);
     U.fillRGB(0);
     U.text(announce, titleHeight - .001);
     U.text(announce, titleHeight + .001);
     U.fillRGB(1);
     U.text(announce, titleHeight);
    }
    U.fillRGB(Ground.RGB.invert());
    U.font(.0175);
    U.fillRGB(U.yinYang ? .5 : 0);
    U.text((Tournament.stage < 1 || Tournament.finished ? "FINAL " : "") + "SCORES:", .225);
    if (U.yinYang) {
     U.fillRGB(0, 1, 0);
    }
    U.text(I.vehiclesInMatch > 2 ? UI.GREEN_TEAM : UI.playerNames[0], .3, .225);
    U.text(String.valueOf(Tournament.stage > 0 ? Long.valueOf(Tournament.wins[0]) : formatFinal[0]), .3, .25);
    if (U.yinYang) {
     U.fillRGB(1, 0, 0);
    }
    U.text(I.vehiclesInMatch > 2 ? UI.RED_TEAM : UI.playerNames[1], .7, .225);
    U.text(String.valueOf(Tournament.stage > 0 ? Long.valueOf(Tournament.wins[1]) : formatFinal[1]), .7, .25);
    if (I.vehiclesInMatch > 2 && MVP > -1) {
     U.fillRGB(U.yinYang ? .5 : 0);
     U.text("MVP: Player " + MVP + (MVP == 0 && I.vehiclePerspective == 0 ? " (YOU!)" : ""), .5, .25);
    }
   } else {
    U.font(.025);
    U.fillRGB(0);
    String timeUp = "TIME'S UP!";
    U.text(timeUp, titleHeight - .001);
    U.text(timeUp, titleHeight + .001);
    U.fillRGB(1);
    U.text(timeUp, titleHeight);
    U.fillRGB(Ground.RGB.invert());
    U.font(.0175);
    U.fillRGB(U.yinYang ? .5 : 0);
    U.text("FINAL SCORE: " + U.DF.format(finalScore[1]), .225);
   }
  }
 }

 static final double stuntDecimate = .0005, damageDecimate = .0000125;

 static void getMVP() {
  MVP = -1;
  if (I.vehiclesInMatch > 2) {//<-The MVP would be obvious if only 2 players are in the match!
   double[] scores = new double[I.vehiclesInMatch];
   for (Vehicle V : I.vehicles) {
    scores[V.index] =
    (V.scoreWaypoint + 1) *
    (V.scoreLap + 1) *
    (V.scoreStunt * stuntDecimate + 1) *
    (V.scoreDamage * damageDecimate + 1) *
    (V.scoreKill + 1);
    if (Bonus.holder == V.index) {
     scores[V.index] *= 2;
    }
   }
   double highestScore = 0;
   for (int n = I.vehiclesInMatch; --n >= 0; ) {
    highestScore = Math.max(highestScore, scores[n]);
   }
   int newMVP = -1;
   for (int n = I.vehiclesInMatch; --n >= 0; ) {
    if (scores[n] >= highestScore) {
     newMVP = n;
    }
   }
   for (int n = I.vehiclesInMatch; --n >= 0; ) {//No MVP if there's a tie somewhere
    if (newMVP != n && scores[n] >= highestScore) {
     newMVP = -1;
     break;
    }
   }
   MVP = newMVP;
  }
 }

 private static void runIndicators(Vehicle V) {
  if (I.vehiclesInMatch > 1) {
   for (int n = I.vehiclesInMatch; --n >= 0; ) {
    runVehicleIndicator(I.vehicles.get(I.closest[n]));
   }
  }
  if (!TE.waypoints.isEmpty()) {
   Waypoint WP = TE.waypoints.get(V.waypointsPassed);
   double[]
   markX = {WP.X - Camera.C.X},
   markY = {WP.Y - 500 - Camera.C.Y},
   markZ = {WP.Z - Camera.C.Z};
   U.rotate(markX, markZ, Camera.XZ);
   if (Camera.YZ != 0) {
    U.rotate(markY, markZ, Camera.YZ);
   }
   if (Camera.XY != 0) {
    U.rotate(markX, markY, Camera.XY);
   }
   double X2 = U.to2DX(markX[0], markZ[0]), Y2 = U.to2DY(markY[0], markZ[0]);
   U.fillRGB(0, 0, 0, UI.colorOpacity.minimal);
   U.fillRectangle(X2, Y2 - .05, .1, .05);
   if (TE.lapWaypoint) {
    U.font(.02);
    U.fillRGB(U.yinYang ? 1 : 0);
    U.text("LAP", X2, Y2 - .05);
   } else {
    U.fillRGB(1);
    U.font(.015);
    U.text("WAYPOINT", X2, Y2 - .0525);
   }
   U.fillRGB(1);
   U.font(.01);
   U.text(String.valueOf(Math.round(Units.getDistance(U.distance(I.vehicles.get(I.vehiclePerspective), WP)))), X2, Y2 - .03);
  }
 }

 private static void runVehicleIndicator(Vehicle V) {
  if (!V.destroyed && I.vehiclePerspective != V.index) {
   double[]
   markX = {V.X - Camera.C.X},
   markY = {V.Y - V.collisionRadius - Camera.C.Y},
   markZ = {V.Z - Camera.C.Z};
   U.rotate(markX, markZ, Camera.XZ);
   if (Camera.YZ != 0) {
    U.rotate(markY, markZ, Camera.YZ);
   }
   if (Camera.XY != 0) {
    U.rotate(markX, markY, Camera.XY);
   }
   double X2 = U.to2DX(markX[0], markZ[0]), Y2 = U.to2DY(markY[0], markZ[0]);
   U.fillRGB(0, 0, 0, UI.colorOpacity.minimal);
   double width = .15;
   U.fillRectangle(X2, Y2 - .05, width, .05);
   if (V.index < I.halfThePlayers()) {
    U.fillRGB(0, 1, 0);
   } else {
    U.fillRGB(1, 0, 0);
   }
   U.font(.01);
   U.text(V.name, X2, Y2 - .06);
   if (Bonus.holder == V.index) {
    UI.drawBonus(X2 - .06, Y2 - .0425, 20);
    UI.drawBonus(X2 + .06, Y2 - .0425, 20);
   }
   double damage = V.getDamage(true);
   U.fillRGB(1, 1 - damage, 0);
   U.fillRectangle(X2, Y2 - .05, Math.min(width, width * damage), .01);
   U.fillRGB(1);
   U.text(Math.round(100 * damage) + "%", X2, Y2 - .045);
   U.text(String.valueOf(Math.round(Units.getDistance(U.distance(I.vehicles.get(I.vehiclePerspective), V)))), X2, Y2 - .03);
  }
 }

 public static void processStunt(Vehicle V) {
  if (V.index == I.vehiclePerspective && V.P.stuntTimer > V.stuntLandWaitTime() && V.P.stuntReward > 0) {
   String stuntSpins = "", stuntRolls = "", stuntFlips = "";
   long computeStuntYZ = 0, computeStuntXY = 0, computeStuntXZ = 0;
   //FLIPS
   while (computeStuntYZ < Math.abs(V.P.stuntYZ) - 45) computeStuntYZ += 360;
   stuntFlips = computeStuntYZ > 0 ? (V.flipCheck[0] && V.flipCheck[1] ? D.BiDirectional + " " : "") + computeStuntYZ + "-Flip" :
   V.flipCheck[0] || V.flipCheck[1] ? "Half-Flip" : stuntFlips;
   //ROLLS
   while (computeStuntXY < Math.abs(V.P.stuntXY) - 45) computeStuntXY += 360;
   stuntRolls = computeStuntXY > 0 ? (V.rollCheck[0] && V.rollCheck[1] ? D.BiDirectional + " " : "") + computeStuntXY + "-Roll" :
   V.rollCheck[0] || V.rollCheck[1] ? "Half-Roll" : stuntRolls;
   //SPINS
   while (computeStuntXZ < Math.abs(V.P.stuntXZ) - 45) computeStuntXZ += 180;
   stuntSpins = computeStuntXZ > 0 ? (V.spinCheck[0] && V.spinCheck[1] ? D.BiDirectional + " " : "") + computeStuntXZ + "-Spin" :
   V.spinCheck[0] || V.spinCheck[1] ? "Half-Spin" : stuntSpins;
   //
   stuntTimer = (stuntFlips.isEmpty() ? 0 : 25) + (stuntRolls.isEmpty() ? 0 : 25) + (stuntSpins.isEmpty() ? 0 : 25) + (V.offTheEdge ? 25 : 0);
   if (UI.status == UI.Status.play || UI.status == UI.Status.replay) {
    if (Options.headsUpDisplay) {
     Sounds.stunt.play(0);
    }
    String by = " by ",
    by1 = !stuntFlips.isEmpty() && !stuntRolls.isEmpty() ? by : "",
    by2 = !stuntSpins.isEmpty() && (!stuntFlips.isEmpty() || !stuntRolls.isEmpty()) ? by : "";
    stuntPrint = "Landed " + (V.offTheEdge ? "an off-the-edge " : "a ") + stuntFlips + by1 + stuntRolls + by2 + stuntSpins + "!";
   }
   V.P.stuntReward = 0;
  }
 }

 public static void reset() {
  started = Camera.flowFlip = false;
  I.vehiclePerspective = I.userPlayerIndex;
  timeLeft = Options.matchLength;
  scoreWaypoint[0] = scoreWaypoint[1] = scoreLap[0] = scoreLap[1] = scoreStunt[0] = scoreStunt[1] = 1;
  Camera.aroundVehicleXZ = Camera.lookAround = 0;
  Bonus.big.setVisible(true);
  for (var ball : Bonus.balls) {
   ball.S.setVisible(false);
  }
  Bonus.holder = Network.bonusHolder = -1;
  stuntTimer = TE.MS.timer = Recorder.recorded = TE.MS.point = 0;
  int n;
  MatchLog.reset();
  if (!Viewer.inUse && Network.mode == Network.Mode.OFF) {
   for (n = I.vehiclesInMatch; --n >= 0; ) {
    UI.playerNames[n] = I.vehicles.get(n).name;
   }
  }
  Camera.mapSelectRandomRotationDirection = U.random() < .5 ? 1 : -1;
  E.renderLevel = Double.POSITIVE_INFINITY;//<-Render everything once first to prevent frame spikes at match start
  Network.ready = new boolean[Network.maxPlayers];
  UI.scene.setCursor(Cursor.CROSSHAIR);
 }
}

