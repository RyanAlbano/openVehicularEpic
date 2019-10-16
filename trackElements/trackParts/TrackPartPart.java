package ve.trackElements.trackParts;

import javafx.scene.shape.*;
import javafx.scene.transform.*;
import ve.*;
import ve.environment.E;
import ve.utilities.*;

class TrackPartPart extends InstancePart {

 private final TrackPart TP;
 Rotate rotateXZ;
 private final boolean checkpoint, checkpointWord, lapWord;
 final boolean tree;

 TrackPartPart(TrackPart i, double[] i_X, double[] i_Y, double[] i_Z, int vertexQuantity, double[] i_RGB, String type, String textureType) {
  TP = i;
  TP.vertexQuantity += vertexQuantity;
  int n;
  textureType = type.contains(" noTexture ") ? "" : TP.modelProperties.contains(" mapTerrain ") ? E.terrain.trim() : textureType;
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
  coordinates = U.random() < .5 ? new float[]{0, 1, 1, 1, 1, 0, 0, 0} : new float[]{0, 0, 1, 0, 1, 1, 0, 1};
  for (n = 0; n < vertexQuantity / 3.; n++) {//<- '3' MUST be double!
   TM.getTexCoords().addAll(coordinates);
  }
  if (type.contains(" triangles ")) {
   setTriangles(vertexQuantity);
  } else if (type.contains(" conic ")) {
   setConic(TP, vertexQuantity);
  } else if (type.contains(" strip ")) {
   setStrip(TP, vertexQuantity);
  } else if (type.contains(" squares ")) {
   setSquares(TP, vertexQuantity);
  } else if (type.contains(" cylindric ")) {
   setCylindric(TP, vertexQuantity);
  } else if (type.contains(" rimFaces ")) {
   setConic(TP, 7);
  } else if (type.contains(" sportRimFaces ")) {
   setConic(TP, 16);
  } else if (type.contains(" wheelRingFaces ")) {
   setCylindric(TP, 48);
   setWheelRingFaces();
  } else {
   setFaces(TP, type.contains(" wheelFaces ") ? 24 : vertexQuantity);
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
   RGB[n] = light && (type.contains(" reflect ") || (storeRGB[0] == storeRGB[1] && storeRGB[1] == storeRGB[2])) ? 1 : RGB[n];
   RGB[n] = TP.modelProperties.contains(" mapTerrain ") ? E.terrainRGB[n] : RGB[n];
   RGB[n] = blink ? 0 : RGB[n];
  }
  U.setDiffuseRGB(PM, RGB[0], RGB[1], RGB[2]);
  if (tree) {
   U.setSelfIllumination(PM, RGB[0] * .25, RGB[1] * .25, RGB[2] * .25);
  }
  if (type.contains(" noSpecular ") || blink) {
   U.setSpecularRGB(PM, 0, 0, 0);
  } else if (type.contains(" shiny ")) {
   U.setSpecularRGB(PM, 1, 1, 1);
   PM.setSpecularPower(E.SpecularPowers.shiny);
  } else {
   U.setSpecularRGB(PM, .5, .5, .5);
   PM.setSpecularPower(TP.modelProperties.contains(" mapTerrain ") ? E.SpecularPowers.dull : E.SpecularPowers.standard);
  }
  if (checkpoint || selfIlluminate) {
   U.setSelfIllumination(PM, RGB[0], RGB[1], RGB[2]);
  }
  MV.setMaterial(PM);
  PM.setDiffuseMap(U.getImage(textureType));
  PM.setSpecularMap(U.getImage(textureType));
  PM.setBumpMap(U.getImageNormal(textureType));
  if (VE.status != VE.Status.vehicleViewer) {
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
  if (E.renderAll || (size * E.renderLevel >= TP.distanceToCamera * Camera.zoom &&
  !(flickPolarity == 1 && TP.flicker) && !(flickPolarity == 2 && !TP.flicker))) {
   boolean render = true;
   if (!E.renderAll && !Double.isNaN(fastCull)) {
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
    render = shiftedAxis == 2 ? Camera.Z >= TP.Z : shiftedAxis < 0 ? Camera.X >= TP.X : shiftedAxis > 0 ? Camera.X <= TP.X : Camera.Z <= TP.Z;
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
    if (E.renderAll || U.getDepth(TP.X + placementX[0], TP.Y + placementY[0], TP.Z + placementZ[0]) > -renderRadius) {
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
  if (E.renderAll ||
  (size * E.renderLevel >= TP.distanceToCamera * Camera.zoom &&
  (!checkpoint || TP.checkpointNumber == VE.currentCheckpoint) && !(checkpointWord && VE.lapCheckpoint) &&
  !(lapWord && (!VE.lapCheckpoint || VE.globalFlick)) &&
  !(flickPolarity == 1 && VE.globalFlick) && !(flickPolarity == 2 && !VE.globalFlick))) {
   boolean render = true;
   if (!E.renderAll && !Double.isNaN(fastCull)) {
    if (fastCull == 0) {
     render = Camera.Z <= TP.Z;
    } else if (fastCull == 2) {
     render = Camera.Z >= TP.Z;
    } else if (fastCull == -1) {
     render = Camera.X >= TP.X;
    } else if (fastCull == 1) {
     render = Camera.X <= TP.X;
    }
   }
   if (E.renderAll || render && U.getDepth(TP) > -renderRadius) {
    if (blink) {
     PM.setSelfIlluminationMap(U.getImage("blink" + U.random(3)));
    }
    if (TP.isFixpoint) {
     U.rotate(MV, TP.XY, TP.YZ, 0);
    }
    if (checkpoint) {
     rotateXZ.setAngle(-TP.XZ + (TP.checkpointSignRotation ? 180 : 0));
    }
    U.setTranslate(MV, TP);
    visible = true;
   }
  }
 }
}
