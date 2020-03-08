package ve.ui;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import ve.environment.E;
import ve.environment.FrustumMound;
import ve.environment.Ground;
import ve.environment.Sun;
import ve.instances.I;
import ve.trackElements.Bonus;
import ve.trackElements.TE;
import ve.trackElements.trackParts.RepairPoint;
import ve.trackElements.trackParts.TrackPart;
import ve.utilities.*;
import ve.utilities.sound.Sounds;
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
   //collisionBounds.setCullFace(CullFace.NONE);<-CullFace.NONE on transparent Spheres does NOT look right!
   Phong.setDiffuseRGB(boundSpherePM, 1, 1, 1, .5);
   Phong.setSpecularRGB(boundSpherePM, 0);
  }

  public static void run(boolean gamePlay) {
   U.font(.03);
   U.fillRGB(1);
   U.text("Vehicle Viewer", .075);
   boolean loadModel = false;
   if (UI.page < 1) {
    Nodes.reset();
    Nodes.remove(Sun.S, Ground.C);
    Nodes.removePointLight(Sun.light);
    UI.scene3D.setFill(U.getColor(0));
    Nodes.setLightRGB(Sun.light, 1, 1, 1);
    Camera.C.X = Camera.C.Y = Camera.C.Z = Camera.XZ = Camera.YZ = Camera.XY = Y = YZ = 0;
    E.viewableMapDistance = Double.POSITIVE_INFINITY;
    U.rotate(Camera.PC, Camera.YZ, -Camera.XZ);
    Camera.rotateXY.setAngle(0);
    Camera.setAngleTable();
    Z = 1000;
    XZ = 180;
    showCollisionBounds = false;//<-Covers vehicle otherwise
    loadModel = true;
    Nodes.setLightRGB(E.ambientLight, 1, 1, 1);
    U.setTranslate(Sun.light, 0, -Long.MAX_VALUE, 0);
    if (lighting3D) {
     Nodes.setLightRGB(E.ambientLight, .5, .5, .5);
     Nodes.addPointLight(Sun.light);
    }
    UI.page = 1;
   }
   XZ += Keys.left ? 5 : 0;
   XZ -= Keys.right ? 5 : 0;
   YZ -= Keys.up ? 5 : 0;
   YZ += Keys.down ? 5 : 0;
   Y += heightChange * U.tick;
   Z += depthChange * U.tick;
   if (I.vehicles.get(0) != null) {
    I.vehicles.get(0).Y = Y;
    I.vehicles.get(0).Z = Z;
    I.vehicles.get(0).XZ = XZ;
    I.vehicles.get(0).YZ = YZ;
    I.vehicles.get(0).thrusting = (U.timerBase20 <= 0) != I.vehicles.get(0).thrusting;
    I.vehicles.get(0).runRender(gamePlay);
    U.font(.02);
    U.text(I.vehicles.get(0).name, .1125);
    U.font(.0125);
    U.fillRGB(1);
    U.text(D.Meshes_ + I.vehicles.get(0).parts.size(), .25, .8);
    U.text(D.Vertices_ + I.vehicles.get(0).vertexQuantity, .75, .8);
    if (showCollisionBounds) {
     U.setTranslate(collisionBounds, I.vehicles.get(0));
     Nodes.add(collisionBounds);
    } else {
     Nodes.remove(collisionBounds);
    }
   }
   U.fillRGB(1);
   U.text("Move Vehicle with the T, G, U, and J Keys. Rotate with the Arrow Keys", .95 + UI.textOffset);
   if (U.yinYang) {
    U.strokeRGB(1);
    U.drawRectangle(.5, UI.selected == 0 ? .825 : UI.selected == 1 ? .85 : UI.selected == 2 ? .875 : UI.selected == 3 ? .9 : .925, UI.width, UI.selectionHeight);
   }
   U.text("RE-LOAD VEHICLE FILE", .825 + UI.textOffset);
   U.text("3D Lighting [" + (lighting3D ? UI.ON : UI.OFF) + "]", .85 + UI.textOffset);
   U.text("Draw Mode [" + (showWireframe ? "LINE" : "FILL") + "]", .875 + UI.textOffset);
   U.text("Collision Bounds [" + (showCollisionBounds ? "SHOW" : UI.HIDE) + "]", .9 + UI.textOffset);
   U.text(UI.BACK_TO_MAIN_MENU, .925 + UI.textOffset);
   if (UI.selectionReady()) {
    if (Keys.up) {
     UI.selected = --UI.selected < 0 ? 4 : UI.selected;
     UI.sound.play(0, 0);
    }
    if (Keys.down) {
     UI.selected = ++UI.selected > 4 ? 0 : UI.selected;
     UI.sound.play(0, 0);
    }
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
   if (Keys.space || Keys.enter) {
    if (UI.selected == 0) {
     loadModel = true;
    } else if (UI.selected == 1) {
     lighting3D = !lighting3D;
     if (lighting3D) {
      Nodes.setLightRGB(E.ambientLight, .5, .5, .5);
      Nodes.addPointLight(Sun.light);
     } else {
      Nodes.setLightRGB(E.ambientLight, 1, 1, 1);
      Nodes.removePointLight(Sun.light);
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
    UI.sound.play(1, 0);
    Keys.space = Keys.enter = false;
   }
   if (Keys.escape) {
    UI.status = UI.Status.mainMenu;
    I.removeVehicleModel();
    UI.sound.play(1, 0);
    Keys.escape = false;
   }
   if (loadModel) {
    I.removeVehicleModel();
    I.userRandomRGB = U.getColor(U.random(), U.random(), U.random());
    I.addVehicleModel(VS.chosen[0], true);
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
   Camera.C.X = Camera.C.Z = Camera.XZ = Camera.YZ = Camera.XY = 0;
   Y = -5000;
   UI.page = 1;
  }
  Camera.XZ -= Keys.left ? 5 : 0;
  Camera.XZ += Keys.right ? 5 : 0;
  Camera.YZ += Keys.up ? 5 : 0;
  Camera.YZ -= Keys.down ? 5 : 0;
  Y += heightChange * UI.movementSpeedMultiple * U.tick;
  Camera.C.Y = Y;
  Camera.C.Z += depthChange * U.cos(Camera.XZ) * UI.movementSpeedMultiple * U.tick;
  Camera.C.X += depthChange * U.sin(Camera.XZ) * UI.movementSpeedMultiple * U.tick;
  U.rotate(Camera.PC, Camera.YZ, -Camera.XZ);
  Camera.rotateXY.setAngle(-Camera.XY);
  Camera.setAngleTable();
  if (!E.lights.getChildren().contains(Sun.light)) {
   Nodes.addPointLight(E.mapViewerLight);
   U.setTranslate(E.mapViewerLight, Camera.C.X, Camera.C.Y, Camera.C.Z);
  }
  E.run(gamePlay);
  for (TrackPart trackPart : TE.trackParts) {
   trackPart.runGraphics(false);
  }
  for (RepairPoint.Instance repairPoint : RepairPoint.instances) {
   repairPoint.run();
  }
  for (FrustumMound mound : TE.mounds) {
   mound.runGraphics();
  }
  U.fillRGB(0, 0, 0, UI.colorOpacity.minimal);
  U.fillRectangle(.5, .9, 1, .2);
  U.font(.015);
  if (U.yinYang) {
   U.strokeRGB(1);
   U.drawRectangle(.5, UI.selected == 0 ? .85 : .875, UI.width, UI.selectionHeight);
  }
  U.fillRGB(1);
  U.text("RE-LOAD MAP FILE", .85 + UI.textOffset);
  U.text(UI.BACK_TO_MAIN_MENU, .875 + UI.textOffset);
  U.text("Move Camera with the T, G, U, and J Keys. Rotate with the Arrow Keys", .9 + UI.textOffset);
  if (UI.selectionReady() && (Keys.up || Keys.down)) {
   UI.selected = UI.selected < 1 ? 1 : 0;
   UI.sound.play(0, 0);
  }
  if (!Keys.inUse) {
   UI.selected =
   Math.abs(.825 - Mouse.Y) < UI.clickRangeY ? 0 :
   Math.abs(.85 - Mouse.Y) < UI.clickRangeY ? 1 :
   UI.selected;
  }
  if (Keys.space || Keys.enter) {
   UI.status = UI.selected == 0 ? UI.Status.mapLoadPass0 : UI.Status.mainMenu;
   Keys.space = Keys.enter = false;
   Sounds.reset();
   UI.sound.play(1, 0);
  }
  if (Keys.escape) {
   UI.status = UI.Status.mainMenu;
   Keys.escape = false;
   Sounds.reset();
   UI.sound.play(1, 0);
  }
  Bonus.run();
  UI.gameFPS = Double.POSITIVE_INFINITY;
  E.renderType = E.RenderType.fullDistance;
 }
}