package ve.ui;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ve.instances.I;
import ve.trackElements.Arrow;
import ve.ui.options.Options;
import ve.utilities.Camera;

public enum Keys {
 ;
 public static boolean up, down, left, right, space, W, S, A, D;
 public static final boolean[] special = new boolean[2];
 public static boolean boost, passBonus, amphibious = true;//<-Vehicle's 'amphibious' syncs to this, so defaulting true is desired
 public static boolean enter;
 public static boolean escape;
 public static boolean inUse;//<-todo--VERIFY--now only controlled in Keys/Mouse.java--check working

 static {
  UI.scene.setOnKeyPressed((KeyEvent keyEvent) -> {
   inUse = true;
   KeyCode KC = keyEvent.getCode();
   if (KC == KeyCode.UP || KC == KeyCode.LEFT || KC == KeyCode.RIGHT || KC == KeyCode.DOWN || KC == KeyCode.SPACE) {
    if (Match.cursorDriving) {
     up = down = left = right = space = Match.cursorDriving = false;
    }
    up = KC == KeyCode.UP || up;
    down = KC == KeyCode.DOWN || down;
    left = KC == KeyCode.LEFT || left;
    right = KC == KeyCode.RIGHT || right;
    space = KC == KeyCode.SPACE || space;
   } else if (KC == KeyCode.ENTER) {
    enter = true;
   } else if (KC == KeyCode.ESCAPE) {
    escape = true;
   } else if (KC == KeyCode.Z) {
    Camera.lookAround = 1;
    Camera.lookForward[1] = true;
    Camera.disablePreMatchFlow();
   } else if (KC == KeyCode.X) {
    Camera.lookAround = -1;
    Camera.lookForward[0] = true;
    Camera.disablePreMatchFlow();
   } else if (KC == KeyCode.DIGIT1) {
    Camera.view = Camera.View.docked;
    Camera.lastView = Camera.view;
    Camera.lastViewNear = false;
    Camera.lastViewWithLookAround = Camera.view;
   } else if (KC == KeyCode.DIGIT2) {
    Camera.view = Camera.View.near;
    Camera.lastView = Camera.view;
    Camera.lastViewNear = true;
    Camera.lastViewWithLookAround = Camera.view;
   } else if (KC == KeyCode.DIGIT3) {
    Camera.view = Camera.View.driver;
    Camera.lastView = Camera.view;
   } else if (KC == KeyCode.DIGIT4) {
    Camera.view = Camera.View.distant;
    Camera.lastView = Camera.view;
   } else if (KC == KeyCode.DIGIT5) {
    Camera.view = Camera.View.flow;
    Camera.lastView = Camera.view;
   } else if (KC == KeyCode.DIGIT6) {
    Camera.view = Camera.View.watchMove;
    Camera.lastView = Camera.view;
   } else if (KC == KeyCode.DIGIT7 || KC == KeyCode.DIGIT8 || KC == KeyCode.DIGIT9 || KC == KeyCode.DIGIT0) {
    Camera.view = Camera.View.watch;
    Camera.lastView = Camera.view;
   } else if (KC == KeyCode.V) {
    special[0] = true;
   } else if (KC == KeyCode.F) {
    special[1] = true;
   } else if (KC == KeyCode.B) {
    boost = true;
   } else if (KC == KeyCode.W) {
    W = true;
   } else if (KC == KeyCode.S) {
    S = true;
   } else if (KC == KeyCode.A) {
    A = true;
    VS.allSame = !VS.allSame;
   } else if (KC == KeyCode.D) {
    D = true;
   } else if (KC == KeyCode.C) {
    Arrow.status =
    Arrow.status == Arrow.Status.racetrack ? Arrow.Status.vehicles :
    Arrow.status == Arrow.Status.vehicles ? Arrow.Status.locked :
    Arrow.Status.racetrack;
   } else if (KC == KeyCode.COMMA) {
    I.vehiclePerspective = --I.vehiclePerspective < 0 ? I.vehiclesInMatch - 1 : I.vehiclePerspective;
    Camera.toUserPerspective[1] = true;
    Camera.disablePreMatchFlow();
   } else if (KC == KeyCode.PERIOD) {
    I.vehiclePerspective = ++I.vehiclePerspective >= I.vehiclesInMatch ? 0 : I.vehiclePerspective;
    Camera.toUserPerspective[0] = true;
    Camera.disablePreMatchFlow();
   } else if (KC == KeyCode.H) {
    Options.headsUpDisplay = !Options.headsUpDisplay;
   } else if (KC == KeyCode.L) {
    DestructionLog.inUse = !DestructionLog.inUse;
   } else if (KC == KeyCode.P) {
    passBonus = true;
   } else if (KC == KeyCode.SHIFT) {
    Camera.adjustFOV = .98;
    Camera.restoreZoom[1] = true;
   } else if (KC == KeyCode.CONTROL) {
    Camera.adjustFOV = 1.02;
    Camera.restoreZoom[0] = true;
   } else if (KC == KeyCode.M) {
    Match.muteSound = !Match.muteSound;
   } else if (KC == KeyCode.T || KC == KeyCode.G || KC == KeyCode.U || KC == KeyCode.J) {
    Viewer.heightChange = KC == KeyCode.J ? 10 : KC == KeyCode.U ? -10 : Viewer.heightChange;
    Viewer.depthChange = KC == KeyCode.T ? 10 : KC == KeyCode.G ? -10 : Viewer.depthChange;
    UI.movementSpeedMultiple = Math.max(10, UI.movementSpeedMultiple * 1.05);
   } else if (KC == KeyCode.EQUALS) {
    Match.vehicleLightBrightnessChange = .01;
   } else if (KC == KeyCode.MINUS) {
    Match.vehicleLightBrightnessChange = -.01;
   } else if (KC == KeyCode.Q) {
    amphibious = !amphibious;
   } else if (KC == KeyCode.I) {
    Options.showAppInfo = !Options.showAppInfo;
   }
  });
  UI.scene.setOnKeyReleased((KeyEvent keyEvent) -> {
   KeyCode KC = keyEvent.getCode();
   up = KC != KeyCode.UP && up;
   down = KC != KeyCode.DOWN && down;
   left = KC != KeyCode.LEFT && left;
   right = KC != KeyCode.RIGHT && right;
   space = KC != KeyCode.SPACE && space;
   enter = KC != KeyCode.ENTER && enter;
   escape = KC != KeyCode.ESCAPE && escape;
   W = KC != KeyCode.W && W;
   S = KC != KeyCode.S && S;
   A = KC != KeyCode.A && A;
   D = KC != KeyCode.D && D;
   if (KC == KeyCode.Z || KC == KeyCode.X) {
    Camera.lookAround = 0;
    Camera.lookForward[0] = Camera.lookForward[1] = false;
   } else if (KC == KeyCode.V) {
    special[0] = false;
   } else if (KC == KeyCode.F) {
    special[1] = false;
   } else if (KC == KeyCode.B) {
    boost = false;
   } else if (KC == KeyCode.P) {
    passBonus = false;
   } else if (KC == KeyCode.COMMA || KC == KeyCode.PERIOD) {
    Camera.toUserPerspective[0] = Camera.toUserPerspective[1] = false;
   } else if (KC == KeyCode.SHIFT || KC == KeyCode.CONTROL) {
    Camera.adjustFOV = 1;
    Camera.restoreZoom[0] = Camera.restoreZoom[1] = false;
   } else if (KC == KeyCode.T || KC == KeyCode.G || KC == KeyCode.U || KC == KeyCode.J) {
    if (KC == KeyCode.U || KC == KeyCode.J) {
     Viewer.heightChange = 0;
    } else {
     Viewer.depthChange = 0;
    }
    UI.movementSpeedMultiple = 0;
   } else if (KC == KeyCode.EQUALS || KC == KeyCode.MINUS) {
    Match.vehicleLightBrightnessChange = 0;
   }
  });
 }

 public static void falsify() {
  up = down = left = right = space =
  W = S = A = D =
  enter = special[0] = special[1] = boost = escape = false;
 }
}