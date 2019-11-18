package ve.trackElements.trackParts;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import ve.*;
import ve.environment.E;
import ve.trackElements.TE;
import ve.utilities.*;

public class TrackPartPart extends InstancePart {

 private final TrackPart TP;
 Rotate rotateXZ;
 private final boolean checkpoint, checkpointWord, lapWord;

 public TrackPartPart(TrackPart i, double[] i_X, double[] i_Y, double[] i_Z, int vertexQuantity, Color i_RGB, String type, String textureType) {
  TP = i;
  TP.vertexQuantity += vertexQuantity;
  int n;
  double[] storeX = new double[vertexQuantity];
  double[] storeY = new double[vertexQuantity];
  double[] storeZ = new double[vertexQuantity];
  light = type.contains(" light ");
  blink = type.contains(" blink ");
  checkpointWord = type.contains(" checkpointWord ");
  lapWord = type.contains(" lapWord ");
  checkpoint = checkpointWord || lapWord;
  selfIlluminate = type.contains(" selfIlluminate ") || checkpoint;
  base = type.contains(" base ");
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
  float[] textureCoordinates = U.random() < .5 ? E.textureCoordinateBase0 : E.textureCoordinateBase1;
  for (n = 0; n < vertexQuantity / (double) 3; n++) {
   TM.getTexCoords().addAll(textureCoordinates);//<-'addAll'--NOT 'setAll'
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
  setPhong(type, type.contains(" noTexture ") ? "" : textureType, i_RGB);
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

 private void setPhong(String type, String textureType, Color i_RGB) {//Don't bother moving to super
  if (TP.universalPhongMaterialUsage == TrackPart.UniversalPhongMaterialUsage.terrain && !type.contains(" noTexture ")) {
   U.setMaterialSecurely(MV, E.Terrain.universal);
  } else if (TP.universalPhongMaterialUsage == TrackPart.UniversalPhongMaterialUsage.paved && !type.contains(" noTexture ")) {
   U.setMaterialSecurely(MV, TE.Paved.universal);
  } else {
   Color RGB = type.contains(" theRandomColor ") ? TP.theRandomColor : i_RGB;
   if (TP.tree && (RGB.getRed() > 0 || RGB.getGreen() > 0 || RGB.getBlue() > 0)) {
    while (RGB.getRed() < 1 && RGB.getGreen() < 1 && RGB.getBlue() < 1) {
     RGB = U.getColor(RGB.getRed() * 1.01, RGB.getGreen() * 1.01, RGB.getBlue() * 1.01);
    }
   }
   Color storeRGB = RGB;
   if (type.contains(" reflect ")) {
    RGB = U.getColor(E.skyRGB);
   }
   if (light && (type.contains(" reflect ") || (storeRGB.getRed() == storeRGB.getGreen() && storeRGB.getGreen() == storeRGB.getBlue()))) {
    RGB = U.getColor(1, 1, 1);
   }
   RGB =
   TP.universalPhongMaterialUsage == TrackPart.UniversalPhongMaterialUsage.terrain ? U.getColor(E.Terrain.RGB) :
   TP.universalPhongMaterialUsage == TrackPart.UniversalPhongMaterialUsage.paved ? U.getColor(TE.Paved.globalShade, TE.Paved.globalShade, TE.Paved.globalShade) :
   RGB;//<-Still needed!
   if (blink) {
    RGB = U.getColor(0, 0, 0);
   }
   PM = new PhongMaterial();
   U.Phong.setDiffuseRGB(PM, RGB);
   if (TP.tree) {
    U.Phong.setSelfIllumination(PM, RGB.getRed() * .25, RGB.getGreen() * .25, RGB.getBlue() * .25);
   }
   if (type.contains(" noSpecular ") || blink) {
    U.Phong.setSpecularRGB(PM, 0);
   } else {
    boolean shiny = type.contains(" shiny ");
    U.Phong.setSpecularRGB(PM, shiny ? E.Specular.Colors.shiny : E.Specular.Colors.standard);
    PM.setSpecularPower(shiny ? E.Specular.Powers.shiny : E.Specular.Powers.standard);
   }
   if (selfIlluminate) {
    U.Phong.setSelfIllumination(PM, RGB);
   }
   PM.setDiffuseMap(U.Images.get(textureType));
   PM.setSpecularMap(U.Images.get(textureType));
   PM.setBumpMap(U.Images.getNormalMap(textureType));
   U.setMaterialSecurely(MV, PM);
  }
 }

 void runAsVehiclePart(boolean renderALL) {
  if (renderALL || ((E.renderType == E.RenderType.fullDistance || size * E.renderLevel >= TP.distanceToCamera * Camera.zoom) &&
  !(flickPolarity == 1 && TP.flicker) && !(flickPolarity == 2 && !TP.flicker))) {
   boolean render = true;
   if (!Double.isNaN(fastCull) && !renderALL) {
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
    double[] placementX = {displaceX + (controller ? TP.driverViewX * VE.Options.driverSeat : 0)};
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
    if (renderALL || U.getDepth(TP.X + placementX[0], TP.Y + placementY[0], TP.Z + placementZ[0]) > -renderRadius) {
     U.setTranslate(MV, TP.X + placementX[0], TP.Y + placementY[0], TP.Z + placementZ[0]);
     visible = true;
     if (blink) {
      PM.setSelfIlluminationMap(U.Images.get(SL.Instance.blink + U.random(3)));
     }
    }
   }
  }
 }

 void runAsTrackPart(boolean renderALL) {
  if (renderALL || ((E.renderType == E.RenderType.fullDistance || size * E.renderLevel >= TP.distanceToCamera * Camera.zoom) &&
  ((!checkpoint || TP.checkpointNumber == TE.currentCheckpoint) && !(checkpointWord && TE.lapCheckpoint) && !(lapWord && !TE.lapCheckpoint) &&
  !(flickPolarity == 1 && VE.yinYang) && !(flickPolarity == 2 && !VE.yinYang)))) {
   boolean render = true;
   if (!Double.isNaN(fastCull) && !renderALL) {
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
   if (renderALL || (render && U.getDepth(TP) > -renderRadius)) {
    if (blink) {
     PM.setSelfIlluminationMap(U.Images.get(SL.Instance.blink + U.random(3)));
    }
    if (TP.isRepairPoint) {
     U.rotate(MV, TP.XY, TP.YZ, 0);
    }
    if (checkpoint) {
     rotateXZ.setAngle(-TP.XZ + (TP.checkpointSignRotation ? 180 : 0));
     if (lapWord) {
      PM.setSelfIlluminationMap(VE.yinYang ? U.Images.get(SL.Images.white) : null);
     }
    }
    U.setTranslate(MV, TP);
    visible = true;
   }
  }
 }
}
