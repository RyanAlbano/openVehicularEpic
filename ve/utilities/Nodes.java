package ve.utilities;

import javafx.scene.LightBase;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import ve.environment.E;
import ve.environment.Ground;
import ve.environment.Sun;
import ve.trackElements.Arrow;
import ve.ui.UI;

public enum Nodes {
 ;

 public static void add(Node... N) {
  UI.crashOnExpensiveInGameCall();
  for (Node n : N) {
   if (n != null) {
    denyPointLight(n);
    if (!UI.group.getChildren().contains(n)) {
     UI.group.getChildren().add(n);
    }
   }
  }
 }

 public static void remove(Node... N) {
  UI.crashOnExpensiveInGameCall();
  for (Node n : N) {
   if (n != null) {
    denyPointLight(n);
    UI.group.getChildren().remove(n);
   }
  }
 }

 public static void addPointLight(Node N) {
  if (N != null) {
   enforcePointLight(N);
   if (!E.lights.getChildren().contains(N) && E.lightsAdded < 3) {
    E.lights.getChildren().add(N);
    E.lightsAdded++;
   }
  }
 }

 public static void removePointLight(Node N) {
  if (N != null) {
   enforcePointLight(N);
   E.lights.getChildren().remove(N);
  }
 }

 static void enforcePointLight(Node N) {
  if (!N.getClass().getName().equals(PointLight.class.getName())) {
   throw new IllegalStateException("PointLight is the only object allowed in or out of this area");
  }
 }

 static void denyPointLight(Node N) {
  if (N.getClass().getName().equals(PointLight.class.getName())) {
   throw new IllegalStateException("PointLight is not allowed in or out of this area");
  }
 }

 public static void setRGB(LightBase LB, double R, double G, double B) {
  LB.setColor(Color.color(U.clamp(R), U.clamp(G), U.clamp(B)));
 }

 public static void reset() {
  boolean addSunlightBack = E.lights.getChildren().contains(Sun.light),//<-Check LIGHT group, not main group!
  addSunBack = UI.group.getChildren().contains(Sun.S),
  addGroundBack = UI.group.getChildren().contains(Ground.C);
  UI.group.getChildren().clear();
  E.lights.getChildren().clear();
  add(E.ambientLight, addSunBack ? Sun.S : null, addGroundBack ? Ground.C : null);
  addPointLight(addSunlightBack ? Sun.light : null);
  add(E.lights);
  Arrow.group.getChildren().clear();
 }
}