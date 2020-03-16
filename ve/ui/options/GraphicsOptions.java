package ve.ui.options;

import ve.instances.I;
import ve.trackElements.TE;
import ve.ui.Keys;
import ve.ui.Mouse;
import ve.ui.Tournament;
import ve.ui.UI;
import ve.utilities.Images;
import ve.utilities.Texture;
import ve.utilities.U;

public enum GraphicsOptions {
 ;

 public static void run() {
  U.fillRGB(0, 0, 0, UI.colorOpacity.maximal);
  U.fillRectangle(.5, .5, 1, 1);
  U.fillRGB(1);
  U.font(.05);
  U.text("GRAPHICS OPTIONS", .15);
  U.font(.015);
  U.text(UI.RETURN, .875 + UI.textOffset);
  U.fillRGB(1);
  U.text("Texture Resolution [" + (Texture.type == Texture.Resolution.adapt ? "ADAPT TO PERFORMANCE" : Texture.type == Texture.Resolution.limited ? "USE LIMITED" : "USE ORIGINAL SIZES") + "]", .3 + UI.textOffset);
  U.text("Limited Resolution [" + Texture.userMaxResolution + "x" + Texture.userMaxResolution + "]", .35 + UI.textOffset);
  U.text("Normal-Mapping [" + (Texture.normalMapping ? UI.ON : UI.OFF) + "]", .4 + UI.textOffset);
  if (UI.selectionReady()) {
   if (Keys.up) {
    if (--UI.selected < 0) {
     UI.selected = 3;
    }
    UI.sound.play(0, 0);
   }
   if (Keys.down) {
    UI.selected = ++UI.selected > 3 ? 0 : UI.selected;
    UI.sound.play(0, 0);
   }
  }
  U.strokeRGB(U.yinYang ? 1 : 0);
  U.drawRectangle(.5, UI.selected == 0 ? .875 : .25 + (.05 * UI.selected), UI.width, UI.selectionHeight);
  U.fillRGB(1);
  boolean isAdjustFunction = false;
  if (UI.selected == 1) {
   U.text("Choose how the system should handle resolution on textures", .75 + UI.textOffset);
   if ((Keys.enter || Keys.space) && UI.selectionReady()) {
    cycleTextureResolution();
   }
  } else if (UI.selected == 2) {
   isAdjustFunction = true;
   U.text("When limiting texture resolution, limit to this size", .75 + UI.textOffset);
   if (UI.selectionReady()) {
    if (Keys.left && Texture.userMaxResolution > 0) {
     Texture.userMaxResolution = Math.max(0, Texture.userMaxResolution / 2);
     Images.getLowResolutionTextures();
     TE.Paved.setTexture();
     UI.sound.play(0, 0);
    }
    if (Keys.right) {
     Texture.userMaxResolution <<= 1;
     if (Texture.userMaxResolution < 1) {
      Texture.userMaxResolution++;
     }
     Images.getLowResolutionTextures();
     TE.Paved.setTexture();
     UI.sound.play(0, 0);
    }
   }
  } else if (UI.selected == 3) {
   U.text("Use normal-mapping on textured surfaces", .75 + UI.textOffset);
   if ((Keys.enter || Keys.space) && UI.selectionReady()) {
    Texture.normalMapping = !Texture.normalMapping;
    TE.Paved.setTexture();
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

 private static void cycleTextureResolution() {
  if (Texture.type == Texture.Resolution.original) {
   Texture.type = Texture.Resolution.limited;
   TE.Paved.setTexture();
  } else if (Texture.type == Texture.Resolution.limited) {
   Texture.type = Texture.Resolution.adapt;
  } else if (Texture.type == Texture.Resolution.adapt) {
   Texture.type = Texture.Resolution.original;
   TE.Paved.setTexture();
  }
 }
}