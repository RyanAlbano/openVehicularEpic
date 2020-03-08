package ve.ui;

import ve.utilities.U;

enum HowToPlay {
 ;
 private static double singularitySize;

 static void run() {
  UI.page = Math.max(UI.page, 1);
  U.fillRGB(0, 0, 0, UI.colorOpacity.maximal);
  U.fillRectangle(.5, .5, 1, 1);
  U.fillRGB(1);
  U.font(.03);
  U.text(UI._LAST, .1, .75);
  U.text(UI.NEXT_, .9, .75);
  U.text(UI.RETURN, .5, .95);
  double targetFPS = U.refreshRate * .25;
  if (UI.page == 1) {
   U.font(.03);
   U.text("All Vehicle Controls", .1);
   double keySizeX = .04, keySizeY = .06;
   U.fillRectangle(.85, .375, keySizeX, keySizeY);//UP
   U.fillRectangle(.85, .45, keySizeX, keySizeY);//DOWN
   U.fillRectangle(.8, .45, keySizeX, keySizeY);//LEFT
   U.fillRectangle(.9, .45, keySizeX, keySizeY);//RIGHT
   U.fillRectangle(.35, .45, keySizeX * 8, keySizeY);//SPACE
   U.fillRectangle(.125, .225, keySizeX, keySizeY);//Q
   U.fillRectangle(.175, .225, keySizeX, keySizeY);//W
   U.fillRectangle(.125, .3, keySizeX, keySizeY);//A
   U.fillRectangle(.175, .3, keySizeX, keySizeY);//S
   U.fillRectangle(.225, .3, keySizeX, keySizeY);//D
   U.fillRectangle(.275, .3, keySizeX, keySizeY);//F
   U.fillRectangle(.3, .375, keySizeX, keySizeY);//V
   U.fillRectangle(.35, .375, keySizeX, keySizeY);//B
   U.fillRectangle(.575, .3, keySizeX, keySizeY);//P
   U.fillRectangle(.6, .225, keySizeX, keySizeY);//MINUS
   U.fillRectangle(.65, .225, keySizeX, keySizeY);//PLUS
   U.fillRGB(0);
   U.font(.02);
   U.text("UP", .85, .375);
   U.font(.0125);
   U.text("DOWN", .85, .45);
   U.text("LEFT", .8, .45);
   U.text("RIGHT", .9, .45);
   U.text("SPACE", .35, .45);
   U.text("Q", .125, .225);
   U.text("W", .175, .225);
   U.text("A", .125, .3);
   U.text("S", .175, .3);
   U.text("D", .225, .3);
   U.text("F", .275, .3);
   U.text("V", .3, .375);
   U.text("B", .35, .375);
   U.text("P", .575, .3);
   U.text("-", .6, .225);
   U.text("+", .65, .225);
   U.fillRGB(1);
   U.text("While Driving on the GROUND, Spacebar is the Handbrake", .5);
   U.text("For standard CARS and TRUCKS, press Spacebar to perform Stunts for points", .525);
   U.text("For AIRCRAFT vehicles, press (W or S) + Down Arrow to engage flight at any time", .55);
   U.text("While FLYING, hold Spacebar to yaw-steer instead of steer by banking, and use W and S to control throttle", .575);
   U.text("For FIXED TURRETS, Spacebar enables finer Precision while Aiming", .6);
   U.text("B = Boost Speed/Change Aerial Velocity (if available)", .625);
   U.text("V and/or F = Use weapons/specials if your vehicle has them", .65);
   U.text("For TANKS, control the turret with the W/A/S/D keys", .675);
   U.text("(It is recommended to fire the tank cannon by pressing A and D simultaneously)", .7);
   U.text("+ and - = Adjust Vehicle Light Brightness", .725);
   U.text("Q = Amphibious mode ON/OFF (for amphibious-capable vehicles)", .75);
   U.text("P = Pass bonus to a teammate (if crossing paths)", .775);
   U.fillRGB(.5, 1, .5);
   U.text("----------Cursor Controls----------", .8);
   U.fillRGB(1);
   U.text("Raise the cursor to go forward, lower it to Reverse", .825);
   U.text("Move the Cursor Left and Right to Turn", .85);
   U.text("Click to engage Handbrake/perform Stunts", .875);
  } else if (UI.page == 2) {
   U.font(.03);
   U.text("Game Objectives", .125);
   U.font(.0125);
   U.text("The primary objective in this game is to Maximize your (team's) score.", .2);
   U.text("There are several ways to do this:", .225);
   U.text("Checkpoints--how many checkpoints you/your team passed through", .275);
   U.text("Laps--completing more laps than the opposition can be the key to winning", .3);
   U.text("Stunts--landing stunts can be supported by most vehicles (except turrets)", .325);
   U.text("Damage Dealt--How much damage is dealt to the opposition", .35);
   U.text("Kills--How many opposing vehicles were destroyed by you/your team", .375);
   U.text("And last but not least--the Bonus", .4);
   UI.drawBonus(.5, .5, 100);
   U.font(.015);
   String spacing = "                ";
   U.textR("Bonus->" + spacing, .5, .5);
   U.textL(spacing + "<-Bonus", .5, .5);
   U.font(.0125);
   U.text("Grab the bonus by driving into it--turrets can also get the bonus by shooting it.", .6);
   U.text("Being in possession of the Bonus when time's up will DOUBLE your (team's) score!", .625);
   U.text("All these factors get multiplied together. When time's up, the player/team with the higher score wins!", .65);
   U.text("(Some values are handled in scientific notation for brevity)", .675);
   U.text("The user is always on the Green team--except in Multiplayer Games.", .7);
  } else if (UI.page == 3) {
   U.font(.025);
   U.text("Other Important Information", .125);
   U.font(.0125);
   U.text("Based on the given circumstances, pick the best strategy (race, fight, etc.)", .175);
   U.text("Press 'C' to toggle the guidance arrow between pointing to the Vehicles or Racetrack", .2);
   U.text("Your vehicle will revive shortly after being destroyed.", .25);
   U.text("However, you can Repair it before then by passing through a Repair Singularity (if one exists on the map)", .275);
   drawSingularity();
   targetFPS = U.refreshRate * .5;
   U.font(.015);
   String spacing = "                ";
   U.textR("Repair->" + spacing, .5, .375);
   U.textL(spacing + "<-Singularity", .5, .375);
   U.font(.0125);
   U.text("It's important to note that vehicles on the same team don't 'interact',", .475);
   U.text("so there's no need to worry about crashing into your own team members, friendly fire, etc.", .5);
   U.fillRGB(1, 1, .5);
   U.text("Not all maps have checkpoints and a designated route.", .55);
   U.text("You'll need to score points using methods besides checkpoints and laps", .575);
   U.text("such as good stunts, fighting opponents, or keeping the bonus.", .6);
   U.fillRGB(0, 1, 1);
   U.text("Some Maps have special environments or may be less straightforward.", .65);
   U.text("There may be an extra learning curve to such maps.", .675);
   U.fillRGB(1);
   U.text("Some vehicles have Guided weaponry.", .725);
   U.text("When fired, these weapons will intercept the nearest opponent automatically.", .75);
   U.text("Some turrets (e.g. YottaVolt Particle Reintegrator) are not designed for attacking.", .8);
   U.text("Rather, they should be aimed at fellow teammates to help heal them, etc.", .825);
  } else if (UI.page == 4) {
   U.font(.0125);
   U.text("Other Key Controls:", .15);
   U.text("Digits 1-7 = Camera Views", .25);
   U.text("Z or X = To look around/behind you while driving (for Views 1-4)", .275);
   U.fillRGB(.5, 1, .5);
   U.text("(Press Z and X simultaneously to look forward again)", .3);
   U.fillRGB(1);
   U.text("Enter or Escape = Pause/exit out of Match", .325);
   U.text("M = Mute Sound", .35);
   U.text("Control and Shift = Adjust Field-Of-View", .375);
   U.fillRGB(.5, 1, .5);
   U.text("(Press Control and Shift simultaneously to restore F.O.V.)", .4);
   U.fillRGB(1);
   U.text("< or > = Change Player Perspective (and set turrets/infrastructure before starting a match)", .425);
   U.fillRGB(.5, 1, .5);
   U.text("(Press < and > simultaneously to view yourself again)", .45);
   U.fillRGB(1);
   U.text("H = Heads-up Display ON/OFF", .475);
   U.text("L = Destruction Log ON/OFF", .5);
   U.text("I = Show/Hide Application Info", .525);
   U.text("There are many other aspects not covered here in these instructions,", .7);
   U.text("but you will learn with experience.", .725);
   U.text("GOOD LUCK", .75);
  }
  if (UI.selectionReady()) {
   if (Keys.right) {
    if (++UI.page > 4) {
     UI.page = 0;
     UI.status = UI.lastStatus;
    }
    UI.sound.play(0, 0);
   }
   if (Keys.left) {
    if (--UI.page < 1) {
     UI.page = 0;
     UI.status = UI.lastStatus;
    }
    UI.sound.play(0, 0);
   }
   if (Keys.enter) {
    UI.page = 0;
    UI.status = UI.lastStatus;
    UI.sound.play(1, 0);
    Keys.enter = false;
   }
  }
  if (Keys.escape) {
   UI.page = 0;
   UI.status = UI.lastStatus;
   UI.sound.play(1, 0);
   Keys.escape = false;
  }
  UI.gameFPS = targetFPS;
 }

 private static void drawSingularity() {
  U.fillRGB(1);
  singularitySize = (singularitySize += 100 * .1 * U.tick) > 100 ? 0 : singularitySize;
  double half = singularitySize * .5;
  UI.GC.fillOval((UI.width * .5) - half, (UI.height * .375) - half, singularitySize, singularitySize);
 }
}
