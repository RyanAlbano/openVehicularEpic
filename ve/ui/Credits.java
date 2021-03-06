package ve.ui;

import ve.utilities.Images;
import ve.utilities.D;
import ve.utilities.U;
import ve.utilities.sound.Sounds;

enum Credits {
 ;
 private static boolean direction;
 private static double quantity;

 public static void run() {
  if (UI.page < 1) {
   Sounds.finish.play(U.random() < .5 ? 0 : 1, 0);
   UI.page = 1;
  }
  U.fillRGB(0);
  U.fillRectangle(.5, .5, 1, 1);
  if (UI.page == 1) {
   UI.GC.drawImage(Images.RA, UI.width * .2 - (Images.RA.getWidth() * .5), UI.height * .5 - (Images.RA.getHeight() * .5));
   UI.GC.drawImage(Images.RA, UI.width * .8 - (Images.RA.getWidth() * .5), UI.height * .5 - (Images.RA.getHeight() * .5));
   quantity = Math.round(quantity);
   quantity += direction ? -1 : 1;
   direction = !(quantity < 2) && (quantity > 11 || direction);
   U.fillRGB(.5);
   if (quantity == 1) {
    U.fillRGB(1);
   }
   U.font(.075);
   U.text(D.OPEN_VEHICULAR_EPIC, .15);
   U.font(.015);
   U.fillRGB(.5);
   if (quantity == 2) {
    U.fillRGB(1);
   }
   U.text("an open-source project maintained by", .2);
   U.fillRGB(.5);
   if (quantity == 3) {
    U.fillRGB(1);
   }
   U.font(.0175);
   U.text("Ryan Albano (RyanAlbano1@gmail.com)", .275);
   U.font(.015);
   U.fillRGB(.5);
   if (quantity == 4) {
    U.fillRGB(1);
   }
   U.text("Other Credits:", .35);
   U.fillRGB(.5);
   if (quantity == 5) {
    U.fillRGB(1);
   }
   U.text("Vitor Macedo (VitorMac) and Dany Fernández Diaz--for programming assistance", .4);
   U.fillRGB(.5);
   if (quantity == 6) {
    U.fillRGB(1);
   }
   U.text("Omar Waly--his Java work (Need for Madness and Radical Aces) have served as a design 'template' for V.E.", .45);
   U.fillRGB(.5);
   if (quantity == 7) {
    U.fillRGB(1);
   }
   U.text("Rory McHenry--for teaching IDE/Java basics", .5);
   U.fillRGB(.5);
   if (quantity == 8) {
    U.fillRGB(1);
   }
   U.text("The OpenJavaFX team/community--for JavaFX, of course", .55);
   U.fillRGB(.5);
   if (quantity == 9) {
    U.fillRGB(1);
   }
   U.text("The FXyz library--for additional shape/geometry support", .6);
   U.fillRGB(.5);
   if (quantity == 10) {
    U.fillRGB(1);
   }
   U.text("JetBrains--for building an awesome IDE", .65);
   U.fillRGB(.5);
   if (quantity == 11) {
    U.fillRGB(1);
   }
   U.text("Everyone who suggested or submitted content!", .7);
   U.fillRGB(.5);
   if (quantity == 12) {
    U.fillRGB(1);
   }
   U.font(.04);
   U.text("And thank YOU for playing!", .9);
  } else if (UI.page == 2) {
   quantity *= direction ? .99 : 1.01;
   if (quantity < 2) {
    direction = false;
    quantity = 2;
   } else if (quantity > 2000) {
    direction = true;
   }
   U.font(.2);
   U.fillRGB(.5);
   U.text("OP     EN", .6);//<-Just the right number of spaces
   double[] clusterX = new double[(int) quantity],
   clusterY = new double[(int) quantity];
   for (int n = (int) quantity; --n >= 0; ) {
    clusterX[n] = (UI.width * .5) + StrictMath.pow(U.random(90000000000.), .25) - StrictMath.pow(U.random(90000000000.), .25);
    clusterY[n] = (UI.height * .5) + StrictMath.pow(U.random(60000000000.), .25) - StrictMath.pow(U.random(60000000000.), .25);
   }
   U.fillRGB(1);
   UI.GC.fillPolygon(clusterX, clusterY, (int) quantity);
   U.font(.05);
   U.fillRGB(0);
   U.text("VEHICULAR", .45);
   U.text("EPIC", .55);
  }
  if (UI.selectionReady()) {
   if (Keys.left) {
    if (--UI.page < 1) {
     UI.page = 0;
     UI.status = UI.Status.mainMenu;
    }
    UI.sound.play(0, 0);
   }
   if (Keys.right) {
    if (++UI.page > 2) {
     UI.page = 0;
     UI.status = UI.Status.mainMenu;
    }
    UI.sound.play(0, 0);
   }
   if (Keys.enter || Keys.space) {
    UI.page = 0;
    UI.status = UI.Status.mainMenu;
    Keys.enter = Keys.space = false;
    UI.sound.play(1, 0);
   }
  }
  if (Keys.escape) {
   UI.page = 0;
   UI.status = UI.Status.mainMenu;
   Keys.escape = false;
   UI.sound.play(1, 0);
  }
  U.fillRGB(1);
  U.font(.03);
  U.text(UI._LAST, .1, .75);
  U.text(UI.NEXT_, .9, .75);
  UI.gameFPS = U.refreshRate;
 }
}