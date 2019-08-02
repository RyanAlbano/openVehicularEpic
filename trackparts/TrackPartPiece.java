package ve.trackparts;

import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import ve.*;
import ve.environment.E;
import ve.utilities.*;

class TrackPartPiece extends Piece {

 private final TrackPart TP;
 Rotate XZ;
 private final boolean checkpoint;
 private final boolean checkpointWord;
 private final boolean lapWord;
 final boolean tree;

 TrackPartPiece(TrackPart i, double[] i_X, double[] i_Y, double[] i_Z, int vertexQuantity, double[] i_RGB, String type, String textureType) {
  TP = i;
  TP.vertexQuantity += vertexQuantity;
  int n;
  textureType = type.contains(" noTexture ") ? "" : TP.modelProperties.contains(" mapTerrain ") ? VE.terrain.trim() : textureType;
  double[] storeX = new double[vertexQuantity];
  double[] storeY = new double[vertexQuantity];
  double[] storeZ = new double[vertexQuantity];
  light = type.contains(" light ");
  selfIlluminate = type.contains(" selfIlluminate ");
  blink = type.contains(" blink ");
  checkpointWord = type.contains(" checkpointWord ");
  lapWord = type.contains(" lapWord ");
  checkpoint = checkpointWord || lapWord;
  base = type.contains(" base ");
  tree = TP.modelProperties.contains(" tree ");
  controller = type.contains(" controller ");
  for (n = vertexQuantity; --n >= 0; ) {
   storeX[n] = i_X[n];
   storeY[n] = i_Y[n];
   storeZ[n] = i_Z[n];
   renderRadius = Math.max(renderRadius, U.netValue(storeX[n], storeY[n], storeZ[n]));
  }
  if (TP.vehicleModel && !base) {
   setDisplacement(storeX, storeY, storeZ, vertexQuantity);
   matrix = new Matrix4x3();
  }
  float[] coordinates = new float[vertexQuantity * 3];
  for (n = vertexQuantity; --n >= 0; ) {
   coordinates[(n * 3)] = (float) storeX[n];
   coordinates[(n * 3) + 1] = (float) storeY[n];
   coordinates[(n * 3) + 2] = (float) storeZ[n];
  }
  TM.getPoints().setAll(coordinates);
  vertexQuantity -= type.contains("+1") ? 1 : 0;
  coordinates = U.random() < .5 ? new float[]{0, 1, 1, 1, 1, 0, 0, 0} : new float[]{0, 0, 1, 0, 1, 1, 0, 1};
  for (n = 0; n < vertexQuantity / 3.; n++) {//<- '3' MUST be double!
   TM.getTexCoords().addAll(coordinates);
  }
  if (type.contains(" triangles ") || type.contains(" triangles+1 ")) {
   setTriangles(vertexQuantity);
  } else if (type.contains(" conic ")) {
   setConic(vertexQuantity);
  } else if (type.contains(" strip ")) {
   setStrip(vertexQuantity);
  } else if (type.contains(" grid ")) {
   setGrid(vertexQuantity);
  } else if (type.contains(" cylindric ")) {
   setCylindric(vertexQuantity);
  } else if (type.contains(" rimFaces ")) {
   setConic(7);
  } else if (type.contains(" sportRimFaces ")) {
   setConic(16);
  } else if (type.contains(" wheelRingFaces ")) {
   setCylindric(48);
   setWheelRingFaces();
  } else {
   setFaces(type.contains(" wheelFaces ") ? 24 : vertexQuantity);
  }
  MV = new MeshView(TM);
  MV.setDrawMode(type.contains(" line ") ? DrawMode.LINE : DrawMode.FILL);
  MV.setCullFace(CullFace.BACK);
  if (type.contains(" theRandomColor ")) {
   RGB[0] = TP.theRandomColor[0];
   RGB[1] = TP.theRandomColor[1];
   RGB[2] = TP.theRandomColor[2];
  } else {
   RGB[0] = i_RGB[0];
   RGB[1] = i_RGB[1];
   RGB[2] = i_RGB[2];
  }
  if (tree && (RGB[0] > 0 || RGB[1] > 0 || RGB[2] > 0)) {
   while (RGB[0] < 1 && RGB[1] < 1 && RGB[2] < 1) {
    RGB[0] *= 1.01;
    RGB[1] *= 1.01;
    RGB[2] *= 1.01;
   }
  }
  double[] storeRGB = {RGB[0], RGB[1], RGB[2]};
  for (n = 3; --n >= 0; ) {
   RGB[n] = type.contains(" reflect ") ? E.skyRGB[n] : RGB[n];
   RGB[n] = light && (type.contains(" reflect ") || storeRGB[0] == storeRGB[1] && storeRGB[1] == storeRGB[2]) ? 1 : RGB[n];
   RGB[n] = TP.modelProperties.contains(" mapTerrain ") ? E.terrainRGB[n] : RGB[n];
   RGB[n] = blink ? 0 : RGB[n];
  }
  U.setDiffuseRGB(PM, RGB[0], RGB[1], RGB[2]);
  if (tree) {
   U.getRGB.setFill(Color.color(U.clamp(RGB[0] * .25), U.clamp(RGB[1] * .25), U.clamp(RGB[2] * .25)));
   U.getRGB.fillRect(0, 0, 1, 1);
   PM.setSelfIlluminationMap(U.getRGBCanvas.snapshot(null, null));
  }
  if (type.contains(" noSpecular ") || blink) {
   U.setSpecularRGB(PM, 0, 0, 0);
  } else if (type.contains(" shiny ")) {
   U.setSpecularRGB(PM, 1, 1, 1);
   PM.setSpecularPower(U.shinySpecular);
  } else {
   U.setSpecularRGB(PM, .5, .5, .5);
   PM.setSpecularPower(TP.modelProperties.contains(" mapTerrain ") ? E.groundSpecularPower : 10);
  }
  if (checkpoint || selfIlluminate) {
   U.getRGB.setFill(Color.color(U.clamp(RGB[0]), U.clamp(RGB[1]), U.clamp(RGB[2])));
   U.getRGB.fillRect(0, 0, 1, 1);
   PM.setSelfIlluminationMap(U.getRGBCanvas.snapshot(null, null));
  }
  MV.setMaterial(PM);
  PM.setDiffuseMap(U.getImage(textureType));
  PM.setSpecularMap(U.getImage(textureType));
  PM.setBumpMap(U.getImageNormal(textureType));
  if (VE.event != VE.event.vehicleViewer) {
   fastCull = type.contains(" fastCullB ") ? 0 : fastCull;
   fastCull = type.contains(" fastCullF ") ? 2 : fastCull;
   fastCull = type.contains(" fastCullR ") ? -1 : fastCull;
   fastCull = type.contains(" fastCullL ") ? 1 : fastCull;
  }
  flickPolarity = type.contains(" flick1 ") ? 1 : type.contains(" flick2 ") ? 2 : flickPolarity;
  setRenderSizeRequirement(storeX, storeY, storeZ, vertexQuantity, checkpoint || light || blink);// || true);//<-Use 'true' when getting logo image
  MV.setVisible(false);
  if (matrix != null) {
   MV.getTransforms().setAll(matrix);
  }
 }

 void processAsVehiclePart() {
  if (size * VE.renderLevel >= TP.instanceToCameraDistance * VE.zoom && !(flickPolarity == 1 && TP.flicker) && !(flickPolarity == 2 && !TP.flicker)) {
   boolean render = true;
   if (fastCull == fastCull) {
    long shiftedAxis = Math.round(fastCull);
    if (TP.XZ > 45 && TP.XZ < 135) {
     shiftedAxis = --shiftedAxis < -1 ? 2 : shiftedAxis;
    } else if (TP.XZ < -45 && TP.XZ > -135) {
     shiftedAxis = ++shiftedAxis > 2 ? -1 : shiftedAxis;
    } else if (Math.abs(TP.XZ) > 135) {
     shiftedAxis = ++shiftedAxis > 2 ? -1 : shiftedAxis;
     shiftedAxis = ++shiftedAxis > 2 ? -1 : shiftedAxis;
    }
    if (Math.abs(TP.YZ) > 90) {
     shiftedAxis = ++shiftedAxis > 2 ? -1 : shiftedAxis;
     shiftedAxis = ++shiftedAxis > 2 ? -1 : shiftedAxis;
    }
    render = shiftedAxis == 2 ? VE.cameraZ >= TP.Z : shiftedAxis < 0 ? VE.cameraX >= TP.X : shiftedAxis > 0 ? VE.cameraX <= TP.X : VE.cameraZ <= TP.Z;
   }
   if (render) {
    double[] placementX = {displaceX + (controller ? TP.driverViewX * VE.driverSeat : 0)};
    double[] placementY = {displaceY};
    double[] placementZ = {displaceZ};
    if (TP.vehicleModel && !base) {
     matrix.set(TP.rotation);
    }
    if (TP.XY != 0) {
     U.rotate(placementX, placementY, TP.XY);
    }
    if (TP.YZ != 0) {
     U.rotate(placementY, placementZ, TP.YZ);
    }
    U.rotate(placementX, placementZ, TP.XZ);
    if (U.getDepth(TP.X + placementX[0], TP.Y + placementY[0], TP.Z + placementZ[0]) > -renderRadius) {
     U.setTranslate(MV, TP.X + placementX[0], TP.Y + placementY[0], TP.Z + placementZ[0]);
     visible = true;
     if (blink) {
      PM.setSelfIlluminationMap(U.getImage("blink" + U.random(3)));
     }
    }
   }
  }
 }

 void processAsTrackPart() {
  if (size * VE.renderLevel >= TP.instanceToCameraDistance * VE.zoom && !(checkpoint && TP.checkpointNumber != VE.currentCheckpoint) && !(checkpointWord && VE.lapCheckpoint) && !(lapWord && (!VE.lapCheckpoint || VE.globalFlick)) && !(flickPolarity == 1 && VE.globalFlick) && !(flickPolarity == 2 && !VE.globalFlick)) {
   boolean render = true;
   if (fastCull == fastCull) {
    if (fastCull == 0) {
     render = VE.cameraZ <= TP.Z;
    } else if (fastCull == 2) {
     render = VE.cameraZ >= TP.Z;
    } else if (fastCull == -1) {
     render = VE.cameraX >= TP.X;
    } else if (fastCull == 1) {
     render = VE.cameraX <= TP.X;
    }
   }
   if (render && U.getDepth(TP.X, TP.Y, TP.Z) > -renderRadius) {
    if (blink) {
     PM.setSelfIlluminationMap(U.getImage("blink" + U.random(3)));
    }
    if (TP.isFixRing) {
     U.rotate(MV, TP.XY, TP.YZ, 0);
    }
    if (checkpoint) {
     XZ.setAngle(-TP.XZ + (TP.checkpointSignRotation ? 180 : 0));
    }
    U.setTranslate(MV, TP.X, TP.Y, TP.Z);
    visible = true;
   }
  }
 }
}
