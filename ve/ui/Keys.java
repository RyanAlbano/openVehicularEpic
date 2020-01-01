package ve.ui;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ve.trackElements.TE;
import ve.utilities.Camera;

public enum Keys {
 ;
 public static boolean Up, Down, Left, Right, Space, W, S, A, D;
 public static final boolean[] Special = new boolean[2];
 public static boolean keyBoost, PassBonus, Amphibious = true;//<-Vehicle's 'amphibious' syncs to this, so defaulting true is desired
 public static boolean Enter;
 public static boolean Escape;
 public static boolean inUse;

 static {
  UI.scene.setOnKeyPressed((KeyEvent keyEvent) -> {
   KeyCode KC = keyEvent.getCode();
   if (KC == KeyCode.UP || KC == KeyCode.LEFT || KC == KeyCode.RIGHT || KC == KeyCode.DOWN || KC == KeyCode.SPACE) {
    if (Match.cursorDriving) {
     Up = Down = Left = Right = Space = Match.cursorDriving = false;
    }
    Up = KC == KeyCode.UP || Up;
    Down = KC == KeyCode.DOWN || Down;
    Left = KC == KeyCode.LEFT || Left;
    Right = KC == KeyCode.RIGHT || Right;
    Space = KC == KeyCode.SPACE || Space;
   } else if (KC == KeyCode.ENTER) {
    Enter = true;
   } else if (KC == KeyCode.ESCAPE) {
    Escape = true;
   } else if (KC == KeyCode.Z) {
    Camera.lookAround = 1;
    Camera.lookForward[1] = true;
    Camera.view = Camera.view == Camera.View.flow && !Match.started ? Camera.lastViewWithLookAround : Camera.view;
   } else if (KC == KeyCode.X) {
    Camera.lookAround = -1;
    Camera.lookForward[0] = true;
    Camera.view = Camera.view == Camera.View.flow && !Match.started ? Camera.lastViewWithLookAround : Camera.view;
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
    Special[0] = true;
   } else if (KC == KeyCode.F) {
    Special[1] = true;
   } else if (KC == KeyCode.B) {
    keyBoost = true;
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
    TE.Arrow.status =
    TE.Arrow.status == TE.Arrow.Status.racetrack ? TE.Arrow.Status.vehicles :
    TE.Arrow.status == TE.Arrow.Status.vehicles ? TE.Arrow.Status.locked :
    TE.Arrow.Status.racetrack;
   } else if (KC == KeyCode.E) {
    UI.vehiclePerspective = --UI.vehiclePerspective < 0 ? UI.vehiclesInMatch - 1 : UI.vehiclePerspective;
    Camera.toUserPerspective[1] = true;
   } else if (KC == KeyCode.R) {
    UI.vehiclePerspective = ++UI.vehiclePerspective >= UI.vehiclesInMatch ? 0 : UI.vehiclePerspective;
    Camera.toUserPerspective[0] = true;
   } else if (KC == KeyCode.H) {
    Options.headsUpDisplay = !Options.headsUpDisplay;
   } else if (KC == KeyCode.L) {
    DestructionLog.inUse = !DestructionLog.inUse;
   } else if (KC == KeyCode.P) {
    PassBonus = true;
   } else if (KC == KeyCode.SHIFT) {
    Camera.zoomChange = .98;
    Camera.restoreZoom[1] = true;
   } else if (KC == KeyCode.CONTROL) {
    Camera.zoomChange = 1.02;
    Camera.restoreZoom[0] = true;
   } else if (KC == KeyCode.M) {
    Match.muteSound = !Match.muteSound;
   } else if (KC == KeyCode.COMMA) {
    Music.gain = Math.max(Music.gain * 2 - 1, -100);
   } else if (KC == KeyCode.PERIOD) {
    Music.gain = Math.min(Music.gain * .5 + 1, 0);
   } else if (KC == KeyCode.T || KC == KeyCode.G || KC == KeyCode.U || KC == KeyCode.J) {
    Viewer.heightChange = KC == KeyCode.J ? 10 : KC == KeyCode.U ? -10 : Viewer.heightChange;
    Viewer.depthChange = KC == KeyCode.T ? 10 : KC == KeyCode.G ? -10 : Viewer.depthChange;
    UI.movementSpeedMultiple = Math.max(10, UI.movementSpeedMultiple * 1.05);
   } else if (KC == KeyCode.EQUALS) {
    Match.vehicleLightBrightnessChange = .01;
   } else if (KC == KeyCode.MINUS) {
    Match.vehicleLightBrightnessChange = -.01;
   } else if (KC == KeyCode.Q) {
    Amphibious = !Amphibious;
   } else if (KC == KeyCode.I) {
    Options.showInfo = !Options.showInfo;
   }
  });
  UI.scene.setOnKeyReleased((KeyEvent keyEvent) -> {
   KeyCode KC = keyEvent.getCode();
   Up = KC != KeyCode.UP && Up;
   Down = KC != KeyCode.DOWN && Down;
   Left = KC != KeyCode.LEFT && Left;
   Right = KC != KeyCode.RIGHT && Right;
   Space = KC != KeyCode.SPACE && Space;
   Enter = KC != KeyCode.ENTER && Enter;
   Escape = KC != KeyCode.ESCAPE && Escape;
   W = KC != KeyCode.W && W;
   S = KC != KeyCode.S && S;
   A = KC != KeyCode.A && A;
   D = KC != KeyCode.D && D;
   if (KC == KeyCode.Z || KC == KeyCode.X) {
    Camera.lookAround = 0;
    Camera.lookForward[0] = Camera.lookForward[1] = false;
   } else if (KC == KeyCode.V) {
    Special[0] = false;
   } else if (KC == KeyCode.F) {
    Special[1] = false;
   } else if (KC == KeyCode.B) {
    keyBoost = false;
   } else if (KC == KeyCode.P) {
    PassBonus = false;
   } else if (KC == KeyCode.E || KC == KeyCode.R) {
    Camera.toUserPerspective[0] = Camera.toUserPerspective[1] = false;
   } else if (KC == KeyCode.SHIFT || KC == KeyCode.CONTROL) {
    Camera.zoomChange = 1;
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
  Up = Down = Left = Right = Space =
  W = S = A = D =
  Enter = Special[0] = Special[1] = keyBoost = Escape = false;
 }
}