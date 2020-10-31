package ve.ui;

import javafx.scene.paint.Color;
import ve.instances.I;
import ve.utilities.D;
import ve.utilities.U;

public enum MatchLog {
 ;
 public static boolean inUse;
 public static final int listQuantity = 6;
 public static final String[][] names = new String[listQuantity][2];
 public static final String[] middleText = new String[listQuantity];
 public static final Color[][] nameColors = new Color[listQuantity][2];
 public static final String hasBonus = "has the BONUS!";
 private static final double nonSymmetricalMiddleTextStart = .4775;

 static void run() {
  if (inUse) {
   U.fillRGB(0, 0, 0, UI.colorOpacity.minimal);
   U.fillRectangle(.5, .0625, .4, .08);
   U.fillRGB(1);
   U.font(.00875);
   double x1 = .4725, x2 = .5275;
   double[] y = {.0375, .05, .0625, .075, .0875, .1};
   for (int n = listQuantity; --n >= 0; ) {
    if (middleText[n].equals(D.destroyed)) {
     U.text(middleText[n], y[n]);
    } else {
     U.textL(middleText[n], nonSymmetricalMiddleTextStart, y[n]);
    }
   }
   //LEFT
   boolean forceWhite = I.vehiclesInMatch < 2;
   for (int n = listQuantity; --n >= 0; ) {
    U.fillRGB(forceWhite ? U.getColor(1) : nameColors[n][0]);
    U.textR(names[n][0], x1, y[n]);
   }
   //RIGHT
   for (int n = listQuantity; --n >= 0; ) {
    if (!middleText[n].equals(hasBonus)) {
     U.fillRGB(forceWhite ? U.getColor(1) : nameColors[n][1]);
     U.textL(names[n][1], x2, y[n]);
    }
   }
  }
 }

 public static void update() {
  for (int n = 1; n < listQuantity; n++) {
   names[n - 1][0] = names[n][0];
   names[n - 1][1] = names[n][1];
   nameColors[n - 1][0] = nameColors[n][0];
   nameColors[n - 1][1] = nameColors[n][1];
   middleText[n - 1] = middleText[n];
  }
 }

 static void reset() {
  for (int n = names.length; --n >= 0; ) {
   names[n][0] = "";
   names[n][1] = "";
   middleText[n] = "";
   nameColors[n][0] = U.getColor(0, 0, 0, 1);
   nameColors[n][1] = U.getColor(0, 0, 0, 1);
  }
  middleText[listQuantity - 1] = "Match Log";
  inUse = I.vehiclesInMatch > 1;//<-Pretty good
 }
}

