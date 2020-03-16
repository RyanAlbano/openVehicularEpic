package ve.ui.options;

import kuusisto.tinysound.TinySound;
import ve.instances.I;
import ve.ui.Keys;
import ve.ui.Mouse;
import ve.ui.Tournament;
import ve.ui.UI;
import ve.utilities.U;
import ve.utilities.sound.Sounds;

public enum SoundOptions {
 ;

 public static void run() {
  U.fillRGB(0, 0, 0, UI.colorOpacity.maximal);
  U.fillRectangle(.5, .5, 1, 1);
  U.fillRGB(1);
  U.font(.05);
  U.text("SOUND OPTIONS", .15);
  U.font(.015);
  U.text(UI.RETURN, .875 + UI.textOffset);
  U.fillRGB(1);
  U.text("Software-based Processing [" + (Sounds.softwareBased ? UI.ON : UI.OFF) + "]", .3 + UI.textOffset);
  U.text("Channels [" + Sounds.channels + " (" + (Sounds.channels < 2 ? "MONO" : "STEREO") + ")]", .35 + UI.textOffset);
  U.text("Bit-Depth [" + Sounds.bitDepth + "]", .4 + UI.textOffset);
  if (Sounds.softwareBased) {
   U.text("SampleRate [" + Sounds.sampleRate + "]", .45 + UI.textOffset);
   U.text("BufferSize [" + Sounds.bufferSize + "]", .5 + UI.textOffset);
  }
  if (UI.selectionReady()) {
   long relevant = Sounds.softwareBased ? 5 : 3;
   if (Keys.up) {
    if (--UI.selected < 0) {
     UI.selected = relevant;
    }
    UI.sound.play(0, 0);
   }
   if (Keys.down) {
    UI.selected = ++UI.selected > relevant ? 0 : UI.selected;
    UI.sound.play(0, 0);
   }
  }
  U.strokeRGB(U.yinYang ? 1 : 0);
  U.drawRectangle(.5, UI.selected == 0 ? .875 : .25 + (.05 * UI.selected), UI.width, UI.selectionHeight);
  U.fillRGB(1);
  boolean isAdjustFunction = false;
  if (UI.selected == 1) {
   U.text("Use software-based audio (TinySound). Strongly recommended for Linux if the (vehicle) audio is not present", .75 + UI.textOffset);
   if ((Keys.enter || Keys.space) && UI.selectionReady()) {
    Sounds.softwareBased = !Sounds.softwareBased;
    if (Sounds.softwareBased) {
     TinySound.init();
    } else {
     TinySound.shutdown();
    }
    Sounds.reset();
   }
  } else if (UI.selected == 2) {
   U.text("Choose Mono or Stereo sound", .75 + UI.textOffset);
   if ((Keys.enter || Keys.space) && UI.selectionReady()) {
    Sounds.channels = Sounds.channels == 1 ? 2 : 1;
    Sounds.reset();
   }
  } else if (UI.selected == 3) {
   U.text("Choose between 8 or 16 bit", .75 + UI.textOffset);
   if ((Keys.enter || Keys.space) && UI.selectionReady()) {
    Sounds.bitDepth = Sounds.bitDepth == 8 ? 16 : 8;
    Sounds.reset();
   }
  } else if (UI.selected == 4) {
   if (Sounds.softwareBased) {
    isAdjustFunction = true;
    U.text("Set the global sample-rate of the software-based mixer", .75 + UI.textOffset);
    if (UI.selectionReady()) {
     if (Keys.left && Sounds.sampleRate > 0) {
      Sounds.sampleRate = Math.max(0, Sounds.sampleRate / 1.5);
      TinySound.restart();
      Sounds.reset();
      UI.sound.play(0, 0);
     }
     if (Keys.right) {
      Sounds.sampleRate *= 1.5;
      if (Sounds.sampleRate < 1) {
       Sounds.sampleRate++;
      }
      TinySound.restart();
      Sounds.reset();
      UI.sound.play(0, 0);
     }
    }
   } else {
    UI.selected++;
   }
  } else if (UI.selected == 5) {
   if (Sounds.softwareBased) {
    isAdjustFunction = true;
    U.text("The software-based buffer size in milliseconds. If the audio is 'choppy', this value may need increasing", .75 + UI.textOffset);
    if (UI.selectionReady()) {
     if (Keys.left && Sounds.bufferSize > 0) {
      Sounds.bufferSize = Math.max(0, Sounds.bufferSize / 1.5);
      UI.sound.play(0, 0);
     }
     if (Keys.right) {
      Sounds.bufferSize *= 1.5;
      if (Sounds.bufferSize < 1) {
       Sounds.bufferSize++;
      }
      UI.sound.play(0, 0);
     }
    }
   } else {
    UI.selected++;
   }
  }
  U.fillRGB(1);
  U.text(UI.selected > 0 ? isAdjustFunction ? Options.notifyAdjustFunction : Options.notifyEnterSpaceChangeFunction : "", .8 + UI.textOffset);
  if (!Keys.inUse) {
   UI.selected =
   Math.abs(.825 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 0 :
   Math.abs(.25 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 1 :
   Math.abs(.3 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 2 :
   Math.abs(.35 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 3 :
   UI.selected;
   if (Sounds.softwareBased) {
    UI.selected =
    Math.abs(.4 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 4 :
    Math.abs(.45 + UI.baseClickOffset - Mouse.Y) < UI.clickRangeY ? 5 :
    UI.selected;
   }
  }
  if ((Keys.enter || Keys.space) && UI.selectionReady()) {
   if (UI.selected == 0) {
    UI.status = UI.Status.optionsMenu;
    Keys.enter = Keys.space = false;
   }
   UI.sound.play(1, 0);
  }
  if (Keys.escape) {
   UI.status = UI.Status.optionsMenu;
   UI.sound.play(1, 0);
   Keys.escape = false;
  }
  if (Tournament.stage > 0) {
   I.vehiclesInMatch = Math.max(2, I.vehiclesInMatch);
  }
  UI.gameFPS = Double.POSITIVE_INFINITY;
 }
}