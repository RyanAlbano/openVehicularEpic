package ve.ui.options;

import ve.instances.I;
import ve.ui.*;
import ve.utilities.Camera;
import ve.utilities.D;
import ve.utilities.U;

public enum Options {
 ;
 public static long driverSeat, matchLength;
 public static boolean headsUpDisplay = true;
 public static boolean showAppInfo;
 static final String notifyAdjustFunction = "Use Left and Right arrow keys to Adjust";
 static final String notifyEnterSpaceChangeFunction = "Click or hit Enter/Space to Change";

 public static void run() {
  boolean fromMenu = UI.status == UI.Status.optionsMenu;
  if (fromMenu) {
   U.fillRGB(0, 0, 0, UI.colorOpacity.maximal);
   U.fillRectangle(.5, .5, 1, 1);
  }
  U.fillRGB(1);
  U.font(.05);
  U.text(D.OPTIONS, .15);
  U.font(.015);
  U.text(UI.RETURN, .875 + UI.textOffset);
  U.fillRGB(1);
  U.text("DriverSeat [" + (driverSeat > 0 ? "RIGHT->" : driverSeat < 0 ? "<-LEFT" : "CENTER") + "]", .3 + UI.textOffset);
  U.text("Units [" + (Units.units == Units.Unit.metric ? "METRIC" : Units.units == Units.Unit.US ? "U.S." : Units.Unit.VEs.name()) + "]", .35 + UI.textOffset);
  U.text("Limit FPS to [" + (UI.userFPS > U.refreshRate ? "JavaFX Default" : Long.valueOf(UI.userFPS)) + "]", .4 + UI.textOffset);
  U.text("Camera-Shake Effects [" + (Camera.shake ? UI.ON : UI.OFF) + "]", .45 + UI.textOffset);
  if (fromMenu) {
   U.text("Match Length [" + matchLength + "]", .5 + UI.textOffset);
   U.text("Game Mode [" + (Tournament.stage > 0 ? "TOURNAMENT" : "NORMAL") + "]", .55 + UI.textOffset);
   U.text("# of Players [" + I.vehiclesInMatch + "]", .6 + UI.textOffset);
   U.text("Graphics Options", .65 + UI.textOffset);
   U.text("Sound Options", .7 + UI.textOffset);
  }
  if (UI.selectionReady()) {
   if (Keys.up) {
    if (--UI.selected < 0) {
     UI.selected = fromMenu ? 9 : 4;
    }
    Keys.inUse = true;
    UI.sound.play(0, 0);
   }
   if (Keys.down) {
    UI.selected = ++UI.selected > 9 || (!fromMenu && UI.selected > 4) ? 0 : UI.selected;
    Keys.inUse = true;
    UI.sound.play(0, 0);
   }
  }
  U.strokeRGB(U.yinYang ? 1 : 0);
  U.drawRectangle(.5, UI.selected == 0 ? .875 : .25 + (.05 * UI.selected), UI.width, UI.selectionHeight);
  U.fillRGB(1);
  boolean isAdjustFunction = false;
  double messageHeight = .775 + UI.textOffset;
  if (UI.selected == 1) {
   isAdjustFunction = true;
   U.text("Driver view location (for applicable vehicles)", messageHeight);
   if (UI.selectionReady()) {
    if (Keys.left && driverSeat > -1) {
     driverSeat--;
     UI.sound.play(0, 0);
    }
    if (Keys.right && driverSeat < 1) {
     driverSeat++;
     UI.sound.play(0, 0);
    }
   }
  } else if (UI.selected == 2) {
   U.text("Switch between Metric, U.S., or the game's raw units (VEs)", messageHeight);
   if ((Keys.enter || Keys.space) && UI.selectionReady()) {
    Units.cycle();
   }
  } else if (UI.selected == 3) {
   isAdjustFunction = true;
   if (UI.selectionReady()) {
    if (Keys.left && UI.userFPS > 1) {
     UI.userFPS = UI.userFPS > U.refreshRate ? U.refreshRate - 1 : --UI.userFPS;
     UI.sound.play(0, 0);
    }
    if (Keys.right && UI.userFPS < Long.MAX_VALUE) {
     UI.userFPS = ++UI.userFPS >= U.refreshRate ? Long.MAX_VALUE : UI.userFPS;
     UI.sound.play(0, 0);
    }
   }
   U.text("Lower the FPS ceiling if your PC can't process V.E. well (i.e. overheating). Leave maxed otherwise.", messageHeight);
  } else if (UI.selected == 4) {
   if ((Keys.enter || Keys.space) && UI.selectionReady()) {
    Camera.shake = !Camera.shake;
   }
   U.text("Shakes camera when vehicles explode, etc.", messageHeight);
  } else if (UI.selected == 5) {
   if (fromMenu) {
    isAdjustFunction = true;
    if (UI.selectionReady()) {
     if (Keys.left && matchLength > 0) {
      matchLength = Math.max(0, matchLength - 10);
      UI.sound.play(0, 0);
     }
     if (Keys.right) {
      matchLength += 10;
      UI.sound.play(0, 0);
     }
    }
    U.text("Set how long the match lasts", messageHeight);
   } else {
    UI.selected++;
   }
  } else if (UI.selected == 6) {
   if (fromMenu) {
    U.text("See the Documentation for more info on Game Modes", messageHeight);
    if ((Keys.enter || Keys.space) && UI.selectionReady()) {
     Tournament.stage = Tournament.stage > 0 ? 0 : 1;
    }
   } else {
    UI.selected++;
   }
  } else if (UI.selected == 7) {
   if (fromMenu) {
    isAdjustFunction = true;
    if (UI.selectionReady()) {
     int playerFloor = Tournament.stage > 0 ? 2 : 1;
     if (Keys.left) {
      I.vehiclesInMatch = --I.vehiclesInMatch < playerFloor ? I.maxPlayers : I.vehiclesInMatch;
      UI.sound.play(0, 0);
     }
     if (Keys.right) {
      I.vehiclesInMatch = ++I.vehiclesInMatch > I.maxPlayers ? playerFloor : I.vehiclesInMatch;
      UI.sound.play(0, 0);
     }
    }
    U.text("More players may slow down performance", messageHeight);
   } else {
    UI.selected++;
   }
  } else if (UI.selected == 8) {
   if (fromMenu) {
    U.text("Go to the Graphics Options", messageHeight);
    if ((Keys.enter || Keys.space) && UI.selectionReady()) {
     UI.status = UI.Status.optionsGraphics;
    }
   } else {
    UI.selected++;
   }
  } else if (UI.selected == 9) {
   if (fromMenu) {
    U.text("Go to the Sound Options", messageHeight);
    if ((Keys.enter || Keys.space) && UI.selectionReady()) {
     UI.status = UI.Status.optionsSound;
    }
   } else {
    UI.selected++;
   }
  }
  U.fillRGB(1);
  U.text(UI.selected > 0 ? isAdjustFunction ? notifyAdjustFunction : notifyEnterSpaceChangeFunction : "", .8 + UI.textOffset);
  if ((Keys.enter || Keys.space) && UI.selectionReady()) {
   if (UI.selected == 0) {
    UI.status = fromMenu ? UI.Status.mainMenu : UI.Status.paused;
    Keys.enter = Keys.space = false;
   } else if (UI.status == UI.Status.optionsGraphics || UI.status == UI.Status.optionsSound) {
    Keys.enter = Keys.space = false;
    UI.selected = 0;
   }
   UI.sound.play(1, 0);
  }
  if (Keys.escape) {
   UI.status = fromMenu ? UI.Status.mainMenu : UI.Status.paused;
   UI.sound.play(1, 0);
   Keys.escape = false;
  }
  if (Tournament.stage > 0) {
   I.vehiclesInMatch = Math.max(2, I.vehiclesInMatch);
  }
  if (!Keys.inUse) {
   UI.selected =
   Math.abs(.825 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 0 :
   Math.abs(.25 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 1 :
   Math.abs(.3 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 2 :
   Math.abs(.35 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 3 :
   Math.abs(.4 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 4 :
   UI.selected;
   if (UI.status == UI.Status.optionsMenu) {
    UI.selected =
    Math.abs(.45 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 5 :
    Math.abs(.5 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 6 :
    Math.abs(.55 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 7 :
    Math.abs(.6 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 8 :
    Math.abs(.65 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 9 :
    UI.selected;
   }
  }
  UI.gameFPS = Double.POSITIVE_INFINITY;
 }
}