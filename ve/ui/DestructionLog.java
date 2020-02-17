package ve.ui;

import javafx.scene.paint.Color;
import ve.instances.I;
import ve.utilities.D;
import ve.utilities.U;

public enum DestructionLog {
 ;
 public static boolean inUse;
 public static final String[][] names = new String[5][2];
 public static final Color[][] nameColors = new Color[5][2];

 static void run() {
  if (inUse) {
   U.font(.00875);
   double x1 = .4725, x2 = .5275, y1 = .0375, y2 = .05, y3 = .0625, y4 = .075, y5 = .0875;
   U.fillRectangle(.5, .05, .4, .08);
   U.fillRGB(1);
   U.text(D.destroyed, y1);
   U.text(D.destroyed, y2);
   U.text(D.destroyed, y3);
   U.text(D.destroyed, y4);
   U.text(D.destroyed, y5);
   //LEFT
   U.fillRGB(nameColors[0][0]);
   U.textR(names[0][0], x1, y1);
   U.fillRGB(nameColors[1][0]);
   U.textR(names[1][0], x1, y2);
   U.fillRGB(nameColors[2][0]);
   U.textR(names[2][0], x1, y3);
   U.fillRGB(nameColors[3][0]);
   U.textR(names[3][0], x1, y4);
   U.fillRGB(nameColors[4][0]);
   U.textR(names[4][0], x1, y5);
   //RIGHT
   U.fillRGB(nameColors[0][1]);
   U.textL(names[0][1], x2, y1);
   U.fillRGB(nameColors[1][1]);
   U.textL(names[1][1], x2, y2);
   U.fillRGB(nameColors[2][1]);
   U.textL(names[2][1], x2, y3);
   U.fillRGB(nameColors[3][1]);
   U.textL(names[3][1], x2, y4);
   U.fillRGB(nameColors[4][1]);
   U.textL(names[4][1], x2, y5);
  }
 }

 public static void update() {
  for (int n = 1; n < 5; n++) {
   names[n - 1][0] = names[n][0];
   names[n - 1][1] = names[n][1];
   nameColors[n - 1][0] = nameColors[n][0];
   nameColors[n - 1][1] = nameColors[n][1];
  }
 }

 static void reset() {
  for (int n = names.length; --n >= 0; ) {
   names[n][0] = "";
   names[n][1] = "";
   nameColors[n][0] = new Color(0, 0, 0, 1);
   nameColors[n][1] = new Color(0, 0, 0, 1);
  }
  inUse = I.vehiclesInMatch > 1;
 }
}

