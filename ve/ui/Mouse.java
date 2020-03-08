package ve.ui;

import javafx.scene.input.MouseEvent;

public enum Mouse {
 ;
 public static boolean mouse;//<-What's 'mouse' for?
 public static boolean click;
 public static double X, Y;
 public static double steerX, steerY;

 static {
  UI.scene.setOnMouseMoved((MouseEvent mouseEvent) -> {
   Keys.inUse = false;
   X = mouseEvent.getX() / UI.width;
   Y = mouseEvent.getY() / UI.height;
   Match.cursorDriving = Match.started || Match.cursorDriving;
  });
  UI.scene.setOnMousePressed((MouseEvent mouseEvent) -> {
   Keys.inUse = false;
   X = mouseEvent.getX() / UI.width;
   Y = mouseEvent.getY() / UI.height;
   mouse = !click || mouse;
  });
  UI.scene.setOnMouseReleased((MouseEvent mouseEvent) -> {
   Keys.inUse = false;
   X = mouseEvent.getX() / UI.width;
   Y = mouseEvent.getY() / UI.height;
   click = mouse = false;
   Keys.falsify();
  });
 }
}