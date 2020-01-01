package ve.ui;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.environment.E;
import ve.environment.FrustumMound;
import ve.environment.Ground;
import ve.environment.Sun;
import ve.instances.I;
import ve.trackElements.TE;
import ve.trackElements.trackParts.TrackPart;
import ve.utilities.Camera;
import ve.utilities.SL;
import ve.utilities.Sounds;
import ve.utilities.U;
import ve.vehicles.VehiclePart;

public enum Viewer {
 ;
 public static boolean inUse;
 private static double Y;
 private static double Z;
 private static double XZ;
 private static double YZ;
 static double heightChange;
 static double depthChange;

 public enum Vehicle {
  ;
  private static boolean lighting3D;
  private static boolean showCollisionBounds;
  public static boolean showWireframe;
  private static final Sphere collisionBounds = new Sphere();

  static {
   PhongMaterial boundSpherePM = new PhongMaterial();
   U.setMaterialSecurely(collisionBounds, boundSpherePM);
   U.Phong.setDiffuseRGB(boundSpherePM, 1, 1, 1, .5);
   U.Phong.setSpecularRGB(boundSpherePM, 0);
  }

  public static void run(boolean gamePlay) {
   U.font(.03);
   U.fillRGB(1);
   U.text("Vehicle Viewer", .075);
   boolean loadModel = false;
   if (UI.page < 1) {
    UI.resetGraphics();
    U.Nodes.remove(Sun.S, Ground.C);
    U.Nodes.Light.remove(Sun.light);
    UI.scene3D.setFill(U.getColor(0));
    U.Nodes.Light.setRGB(Sun.light, 1, 1, 1);
    Camera.X = Camera.Y = Camera.Z = Camera.XZ = Camera.YZ = Camera.XY = Y = YZ = 0;
    E.viewableMapDistance = Double.POSITIVE_INFINITY;
    U.rotate(Camera.camera, Camera.YZ, -Camera.XZ);
    Camera.rotateXY.setAngle(0);
    Camera.setAngleTable();
    Z = 1000;
    XZ = 180;
    showCollisionBounds = false;//<-Covers vehicle otherwise
    loadModel = true;
    U.Nodes.Light.setRGB(E.ambientLight, 1, 1, 1);
    U.setTranslate(Sun.light, 0, -Long.MAX_VALUE, 0);
    if (lighting3D) {
     U.Nodes.Light.setRGB(E.ambientLight, .5, .5, .5);
     U.Nodes.Light.add(Sun.light);
    }
    UI.page = 1;
   }
   XZ += Keys.Left ? 5 : 0;
   XZ -= Keys.Right ? 5 : 0;
   YZ -= Keys.Up ? 5 : 0;
   YZ += Keys.Down ? 5 : 0;
   Y += heightChange * UI.tick;
   Z += depthChange * UI.tick;
   if (I.vehicles.get(0) != null) {
    I.vehicles.get(0).Y = Y;
    I.vehicles.get(0).Z = Z;
    I.vehicles.get(0).XZ = XZ;
    I.vehicles.get(0).YZ = YZ;
    I.vehicles.get(0).thrusting = (UI.timerBase20 <= 0) != I.vehicles.get(0).thrusting;
    I.vehicles.get(0).runRender(gamePlay);
    U.font(.02);
    U.text(I.vehicles.get(0).name, .1125);
    U.font(.0125);
    U.fillRGB(1);
    U.text(SL.Meshes_ + I.vehicles.get(0).parts.size(), .25, .8);
    U.text(SL.Vertices_ + I.vehicles.get(0).vertexQuantity, .75, .8);
    if (showCollisionBounds) {
     U.setTranslate(collisionBounds, I.vehicles.get(0));
     U.Nodes.add(collisionBounds);
    } else {
     U.Nodes.remove(collisionBounds);
    }
   }
   U.fillRGB(1);
   U.text("Move Vehicle with the T, G, U, and J Keys. Rotate with the Arrow Keys", .95 + UI.textOffset);
   if (UI.yinYang) {
    U.strokeRGB(1);
    U.drawRectangle(.5, UI.selected == 0 ? .825 : UI.selected == 1 ? .85 : UI.selected == 2 ? .875 : UI.selected == 3 ? .9 : .925, UI.width, UI.selectionHeight);
   }
   U.text("RE-LOAD VEHICLE FILE", .825 + UI.textOffset);
   U.text("3D Lighting [" + (lighting3D ? UI.ON : UI.OFF) + "]", .85 + UI.textOffset);
   U.text("Draw Mode [" + (showWireframe ? "LINE" : "FILL") + "]", .875 + UI.textOffset);
   U.text("Collision Bounds [" + (showCollisionBounds ? "SHOW" : UI.HIDE) + "]", .9 + UI.textOffset);
   U.text(UI.BACK_TO_MAIN_MENU, .925 + UI.textOffset);
   if (UI.selectionReady()) {
    if (Keys.Up) {
     UI.selected = --UI.selected < 0 ? 4 : UI.selected;
     Keys.inUse = true;
     Sounds.UI.play(0, 0);
    }
    if (Keys.Down) {
     UI.selected = ++UI.selected > 4 ? 0 : UI.selected;
     Keys.inUse = true;
     Sounds.UI.play(0, 0);
    }
   }
   if (Keys.Space || Keys.Enter) {
    if (UI.selected == 0) {
     loadModel = true;
    } else if (UI.selected == 1) {
     lighting3D = !lighting3D;
     if (lighting3D) {
      U.Nodes.Light.setRGB(E.ambientLight, .5, .5, .5);
      U.Nodes.Light.add(Sun.light);
     } else {
      U.Nodes.Light.setRGB(E.ambientLight, 1, 1, 1);
      U.Nodes.Light.remove(Sun.light);
     }
    } else if (UI.selected == 2) {
     showWireframe = !showWireframe;
     for (VehiclePart part : I.vehicles.get(0).parts) {
      part.setDrawMode(showWireframe);
     }
    } else if (UI.selected == 3) {
     showCollisionBounds = !showCollisionBounds;
     collisionBounds.setRadius(I.vehicles.get(0).collisionRadius);
    } else {
     UI.status = UI.Status.mainMenu;
     I.removeVehicleModel();
    }
    Sounds.UI.play(1, 0);
    Keys.Space = Keys.Enter = false;
   }
   if (Keys.Escape) {
    UI.status = UI.Status.mainMenu;
    I.removeVehicleModel();
    Sounds.UI.play(1, 0);
    Keys.Escape = false;
   }
   if (loadModel) {
    I.removeVehicleModel();
    UI.userRandomRGB = U.getColor(U.random(), U.random(), U.random());
    I.addVehicleModel(VS.chosen[0], true);
   }
   if (!Keys.inUse) {
    UI.selected =
    Math.abs(.8 - Mouse.Y) < UI.clickRangeY ? 0 :
    Math.abs(.825 - Mouse.Y) < UI.clickRangeY ? 1 :
    Math.abs(.85 - Mouse.Y) < UI.clickRangeY ? 2 :
    Math.abs(.875 - Mouse.Y) < UI.clickRangeY ? 3 :
    Math.abs(.9 - Mouse.Y) < UI.clickRangeY ? 4 :
    UI.selected;
   }
   UI.gameFPS = Double.POSITIVE_INFINITY;
   E.renderType = E.RenderType.fullDistance;
  }
 }

 public static void runMapViewer(boolean gamePlay) {
  U.font(.03);
  U.fillRGB(1);
  U.text("Map Viewer", .075);
  if (UI.page < 1) {
   Camera.X = Camera.Z = Camera.XZ = Camera.YZ = Camera.XY = 0;
   Y = -5000;
   UI.page = 1;
  }
  Camera.XZ -= Keys.Left ? 5 : 0;
  Camera.XZ += Keys.Right ? 5 : 0;
  Camera.YZ += Keys.Up ? 5 : 0;
  Camera.YZ -= Keys.Down ? 5 : 0;
  Y += heightChange * UI.movementSpeedMultiple * UI.tick;
  Camera.Y = Y;
  Camera.Z += depthChange * U.cos(Camera.XZ) * UI.movementSpeedMultiple * UI.tick;
  Camera.X += depthChange * U.sin(Camera.XZ) * UI.movementSpeedMultiple * UI.tick;
  U.rotate(Camera.camera, Camera.YZ, -Camera.XZ);
  Camera.rotateXY.setAngle(-Camera.XY);
  Camera.setAngleTable();
  if (!E.lights.getChildren().contains(Sun.light)) {
   U.Nodes.Light.add(E.mapViewerLight);
   U.setTranslate(E.mapViewerLight, Camera.X, Camera.Y, Camera.Z);
  }
  E.run(gamePlay);
  for (TrackPart trackPart : TE.trackParts) {
   trackPart.runGraphics(false);
  }
  for (FrustumMound mound : TE.mounds) {
   mound.runGraphics();
  }
  U.fillRGB(0, 0, 0, UI.colorOpacity.minimal);
  U.fillRectangle(.5, .9, 1, .2);
  U.font(.015);
  if (UI.yinYang) {
   U.strokeRGB(1);
   U.drawRectangle(.5, UI.selected == 0 ? .85 : .875, UI.width, UI.selectionHeight);
  }
  U.fillRGB(1);
  U.text("RE-LOAD MAP FILE", .85 + UI.textOffset);
  U.text(UI.BACK_TO_MAIN_MENU, .875 + UI.textOffset);
  U.text("Move Camera with the T, G, U, and J Keys. Rotate with the Arrow Keys", .9 + UI.textOffset);
  if (UI.selectionReady() && (Keys.Up || Keys.Down)) {
   UI.selected = UI.selected < 1 ? 1 : 0;
   Keys.inUse = true;
   Sounds.UI.play(0, 0);
  }
  if (Keys.Space || Keys.Enter) {
   UI.status = UI.selected == 0 ? UI.Status.mapLoadPass0 : UI.Status.mainMenu;
   Sounds.UI.play(1, 0);
   Keys.Space = Keys.Enter = false;
   Sounds.clear();
  }
  if (Keys.Escape) {
   UI.status = UI.Status.mainMenu;
   Sounds.UI.play(1, 0);
   Keys.Escape = false;
   Sounds.clear();
  }
  TE.bonus.run();
  if (!Keys.inUse) {
   UI.selected = Math.abs(.825 - Mouse.Y) < UI.clickRangeY ? 0 : Math.abs(.85 - Mouse.Y) < UI.clickRangeY ? 1 : UI.selected;
  }
  UI.gameFPS = Double.POSITIVE_INFINITY;
  E.renderType = E.RenderType.fullDistance;
 }
}