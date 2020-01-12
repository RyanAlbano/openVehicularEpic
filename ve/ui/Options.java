package ve.ui;

import ve.instances.I;
import ve.utilities.Camera;
import ve.utilities.SL;
import ve.utilities.U;

public enum Options {
 ;
 public static long driverSeat, matchLength;
 public static boolean normalMapping, headsUpDisplay = true;
 public static boolean degradedSoundEffects;
 public static boolean showAppInfo;

 public static void run() {
  boolean fromMenu = UI.status == UI.Status.optionsMenu;
  if (fromMenu) {
   U.fillRGB(0);
   U.fillRectangle(.5, .5, 1, 1);
  }
  U.fillRGB(1);
  U.font(.05);
  U.text(SL.OPTIONS, .15);
  U.font(.015);
  U.text(UI.RETURN, .875 + UI.textOffset);
  U.fillRGB(1);
  U.text("DriverSeat [" + (driverSeat > 0 ? "RIGHT->" : driverSeat < 0 ? "<-LEFT" : "CENTER") + "]", .3 + UI.textOffset);
  U.text("Units [" + (Units.units == Units.Unit.metric ? "METRIC" : Units.units == Units.Unit.US ? "U.S." : Units.Unit.VEs.name()) + "]", .35 + UI.textOffset);
  U.text("Limit FPS to [" + (UI.userFPS > U.refreshRate ? "JavaFX Default" : Long.valueOf(UI.userFPS)) + "]", .4 + UI.textOffset);
  U.text("Camera-Shake Effects [" + (Camera.shake ? UI.ON : UI.OFF) + "]", .45 + UI.textOffset);
  if (fromMenu) {
   U.text("Normal-Mapping [" + (normalMapping ? UI.ON : UI.OFF) + "]", .5 + UI.textOffset);
   U.text("Match Length [" + matchLength + "]", .55 + UI.textOffset);
   U.text("Game Mode [" + (Tournament.stage > 0 ? "TOURNAMENT" : "NORMAL") + "]", .6 + UI.textOffset);
   U.text("# of Players [" + I.vehiclesInMatch + "]", .65 + UI.textOffset);
  }
  if (UI.selectionReady()) {
   if (Keys.Up) {
    if (--UI.selected < 0) {
     UI.selected = fromMenu ? 8 : 4;
    }
    Keys.inUse = true;
    UI.sound.play(0, 0);
   }
   if (Keys.Down) {
    UI.selected = ++UI.selected > 8 || (!fromMenu && UI.selected > 4) ? 0 : UI.selected;
    Keys.inUse = true;
    UI.sound.play(0, 0);
   }
  }
  U.strokeRGB(U.yinYang ? 1 : 0);
  U.drawRectangle(.5, UI.selected == 0 ? .875 : .25 + (.05 * UI.selected), UI.width, UI.selectionHeight);
  U.fillRGB(1);
  boolean isAdjustFunction = false;
  if (UI.selected == 1) {
   isAdjustFunction = true;
   U.text("Driver view location (for applicable vehicles)", .75);
   if (UI.selectionReady()) {
    if (Keys.Left && driverSeat > -1) {
     driverSeat--;
     UI.sound.play(0, 0);
    }
    if (Keys.Right && driverSeat < 1) {
     driverSeat++;
     UI.sound.play(0, 0);
    }
   }
  } else if (UI.selected == 2) {
   U.text("Switch between Metric, U.S., or the game's raw units (VEs)", .75);
   if ((Keys.Enter || Keys.Space) && UI.selectionReady()) {
    Units.cycle();
   }
  } else if (UI.selected == 3) {
   isAdjustFunction = true;
   if (UI.selectionReady()) {
    if (Keys.Left && UI.userFPS > 1) {
     UI.userFPS = UI.userFPS > U.refreshRate ? U.refreshRate - 1 : --UI.userFPS;
     UI.sound.play(0, 0);
    }
    if (Keys.Right && UI.userFPS < Long.MAX_VALUE) {
     UI.userFPS = ++UI.userFPS >= U.refreshRate ? Long.MAX_VALUE : UI.userFPS;
     UI.sound.play(0, 0);
    }
   }
   U.text("Lower the FPS ceiling if your PC can't process V.E. well (i.e. overheating). Leave maxed otherwise.", .75);
  } else if (UI.selected == 4) {
   if ((Keys.Enter || Keys.Space) && UI.selectionReady()) {
    Camera.shake = !Camera.shake;
   }
   U.text("Shakes camera when vehicles explode, etc.", .75);
  } else if (UI.selected == 5) {
   if (fromMenu) {
    U.text("Use normal-mapping on textured surfaces", .75);
    if ((Keys.Enter || Keys.Space) && UI.selectionReady()) {
     normalMapping = !normalMapping;
    }
   } else {
    UI.selected++;
   }
  } else if (UI.selected == 6) {
   if (fromMenu) {
    isAdjustFunction = true;
    if (UI.selectionReady()) {
     if (Keys.Left && matchLength > 0) {
      matchLength = Math.max(0, matchLength - 10);
      UI.sound.play(0, 0);
     }
     if (Keys.Right) {
      matchLength += 10;
      UI.sound.play(0, 0);
     }
    }
    U.text("Set how long the match lasts", .75);
   } else {
    UI.selected++;
   }
  } else if (UI.selected == 7) {
   if (fromMenu) {
    U.text("See the Documentation for more info on Game Modes", .75);
    if ((Keys.Enter || Keys.Space) && UI.selectionReady()) {
     Tournament.stage = Tournament.stage > 0 ? 0 : 1;
    }
   } else {
    UI.selected++;
   }
  } else if (UI.selected == 8) {
   if (fromMenu) {
    isAdjustFunction = true;
    if (UI.selectionReady()) {
     int playerFloor = Tournament.stage > 0 ? 2 : 1;
     if (Keys.Left) {
      I.vehiclesInMatch = --I.vehiclesInMatch < playerFloor ? I.maxPlayers : I.vehiclesInMatch;
      UI.sound.play(0, 0);
     }
     if (Keys.Right) {
      I.vehiclesInMatch = ++I.vehiclesInMatch > I.maxPlayers ? playerFloor : I.vehiclesInMatch;
      UI.sound.play(0, 0);
     }
    }
    U.text("More players may slow down performance", .825);
   } else {
    UI.selected++;
   }
  }
  U.fillRGB(1);
  U.text(UI.selected > 0 ? isAdjustFunction ? "Use Left and Right arrow keys to Adjust" : "Click or hit Enter/Space to Change" : "", .8);
  if ((Keys.Enter || Keys.Space) && UI.selectionReady()) {
   UI.status = UI.selected == 0 ? fromMenu ? UI.Status.mainMenu : UI.Status.paused : UI.status;
   UI.sound.play(1, 0);
   Keys.Enter = Keys.Space = false;
  }
  if (Keys.Escape) {
   UI.status = fromMenu ? UI.Status.mainMenu : UI.Status.paused;
   UI.sound.play(1, 0);
   Keys.Escape = false;
  }
  if (Tournament.stage > 0) {
   I.vehiclesInMatch = Math.max(2, I.vehiclesInMatch);
  }
  if (!Keys.inUse) {
   double clickOffset = .025;
   UI.selected =
   Math.abs(.825 + clickOffset - Mouse.Y) < UI.clickRangeY ? 0 :
   Math.abs(.25 + clickOffset - Mouse.Y) < UI.clickRangeY ? 1 :
   Math.abs(.3 + clickOffset - Mouse.Y) < UI.clickRangeY ? 2 :
   Math.abs(.35 + clickOffset - Mouse.Y) < UI.clickRangeY ? 3 :
   Math.abs(.4 + clickOffset - Mouse.Y) < UI.clickRangeY ? 4 :
   UI.selected;
   if (UI.status == UI.Status.optionsMenu) {
    UI.selected =
    Math.abs(.45 + clickOffset - Mouse.Y) < UI.clickRangeY ? 5 :
    Math.abs(.5 + clickOffset - Mouse.Y) < UI.clickRangeY ? 6 :
    Math.abs(.55 + clickOffset - Mouse.Y) < UI.clickRangeY ? 7 :
    Math.abs(.6 + clickOffset - Mouse.Y) < UI.clickRangeY ? 8 :
    UI.selected;
   }
  }
  UI.gameFPS = Double.POSITIVE_INFINITY;
 }
}