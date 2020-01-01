package ve.trackElements.trackParts;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import ve.effects.Effects;
import ve.environment.E;
import ve.environment.Terrain;
import ve.instances.InstancePart;
import ve.trackElements.TE;
import ve.ui.Options;
import ve.ui.UI;
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
  light = type.contains(SL.Thick(SL.light));
  blink = type.contains(SL.Thick(SL.blink));
  checkpointWord = type.contains(SL.Thick(SL.checkPointWord));
  lapWord = type.contains(SL.Thick(SL.lapWord));
  checkpoint = checkpointWord || lapWord;
  selfIlluminate = type.contains(SL.Thick(SL.selfIlluminate)) || checkpoint;
  base = type.contains(SL.Thick(SL.base));
  controller = type.contains(SL.Thick(SL.controller));
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
   TM.getTexCoords().addAll(textureCoordinates);//<-'addAll' and NOT 'setAll'
  }
  if (type.contains(SL.Thick(FaceFunction.triangles.name()))) {
   setTriangles(vertexQuantity);
  } else if (type.contains(SL.Thick(FaceFunction.conic.name()))) {
   setConic(TP, vertexQuantity);
  } else if (type.contains(SL.Thick(FaceFunction.strip.name()))) {
   setStrip(TP, vertexQuantity);
  } else if (type.contains(SL.Thick(FaceFunction.squares.name()))) {
   setSquares(TP, vertexQuantity);
  } else if (type.contains(SL.Thick(FaceFunction.cylindric.name()))) {
   setCylindric(TP, vertexQuantity);
  } else if (type.contains(SL.Thick(SL.rimFaces))) {
   setConic(TP, 7);
  } else if (type.contains(SL.Thick(SL.sportRimFaces))) {
   setConic(TP, 16);
  } else if (type.contains(SL.Thick(SL.wheelRingFaces))) {
   setCylindric(TP, 48);
   setWheelRingFaces();
  } else {
   setFaces(TP, type.contains(SL.Thick(SL.wheelFaces)) ? 24 : vertexQuantity);
  }
  MV = new MeshView(TM);
  MV.setDrawMode(type.contains(SL.Thick(SL.line)) ? DrawMode.LINE : DrawMode.FILL);
  MV.setCullFace(CullFace.BACK);
  setPhong(type, type.contains(SL.Thick(SL.noTexture)) ? "" : textureType, i_RGB);
  if (UI.status != UI.Status.vehicleViewer) {
   fastCull = type.contains(SL.Thick(SL.fastCullB)) ? 0 : fastCull;
   fastCull = type.contains(SL.Thick(SL.fastCullF)) ? 2 : fastCull;
   fastCull = type.contains(SL.Thick(SL.fastCullR)) ? -1 : fastCull;
   fastCull = type.contains(SL.Thick(SL.fastCullL)) ? 1 : fastCull;
  }
  flickPolarity = type.contains(SL.Thick(SL.flick1)) ? 1 : type.contains(SL.Thick(SL.flick2)) ? 2 : flickPolarity;
  setRenderSizeRequirement(storeX, storeY, storeZ, vertexQuantity, checkpoint || light || blink);// || true);//<-Use 'true' when getting logo image
  MV.setVisible(false);
  if (matrix != null) {
   MV.getTransforms().setAll(matrix);
  }
 }

 private void setPhong(String type, String textureType, Color i_RGB) {//Don't bother moving this void to super
  if (TP.universalPhongMaterialUsage == TrackPart.UniversalPhongMaterialUsage.terrain && !type.contains(SL.Thick(SL.noTexture))) {
   U.setMaterialSecurely(MV, Terrain.universal);
  } else if (TP.universalPhongMaterialUsage == TrackPart.UniversalPhongMaterialUsage.paved && !type.contains(SL.Thick(SL.noTexture))) {
   U.setMaterialSecurely(MV, TE.Paved.universal);
  } else {
   Color RGB = type.contains(SL.Thick(SL.theRandomColor)) ? TP.theRandomColor : i_RGB;
   if (TP.tree && (RGB.getRed() > 0 || RGB.getGreen() > 0 || RGB.getBlue() > 0)) {
    while (RGB.getRed() < 1 && RGB.getGreen() < 1 && RGB.getBlue() < 1) {
     RGB = U.getColor(RGB.getRed() * 1.01, RGB.getGreen() * 1.01, RGB.getBlue() * 1.01);
    }
   }
   Color storeRGB = RGB;
   if (type.contains(SL.Thick(SL.reflect))) {
    RGB = U.getColor(E.skyRGB);
   }
   if (light && (type.contains(SL.Thick(SL.reflect)) || (storeRGB.getRed() == storeRGB.getGreen() && storeRGB.getGreen() == storeRGB.getBlue()))) {
    RGB = U.getColor(1);
   }
   RGB =
   TP.universalPhongMaterialUsage == TrackPart.UniversalPhongMaterialUsage.terrain ? U.getColor(Terrain.RGB) :
   TP.universalPhongMaterialUsage == TrackPart.UniversalPhongMaterialUsage.paved ? U.getColor(TE.Paved.globalShade) :
   RGB;//<-Still needed!
   if (blink) {
    RGB = U.getColor(0);
   }
   PM = new PhongMaterial();
   U.Phong.setDiffuseRGB(PM, RGB);
   if (TP.tree) {
    PM.setSelfIlluminationMap(U.Phong.getSelfIllumination(RGB.getRed() * .25, RGB.getGreen() * .25, RGB.getBlue() * .25));
   }
   if (type.contains(SL.Thick(SL.noSpecular)) || blink) {
    U.Phong.setSpecularRGB(PM, 0);
   } else {
    boolean shiny = type.contains(SL.Thick(SL.shiny));
    U.Phong.setSpecularRGB(PM, shiny ? E.Specular.Colors.shiny : E.Specular.Colors.standard);
    PM.setSpecularPower(shiny ? E.Specular.Powers.shiny : E.Specular.Powers.standard);
   }
   if (selfIlluminate) {
    PM.setSelfIlluminationMap(U.Phong.getSelfIllumination(RGB));
   }
   PM.setDiffuseMap(Images.get(textureType));
   PM.setSpecularMap(Images.get(textureType));
   PM.setBumpMap(Images.getNormalMap(textureType));
   U.setMaterialSecurely(MV, PM);
  }
 }

 void runAsVehiclePart(double distanceTrackPartCamera, boolean renderALL) {
  if (renderALL || ((E.renderType == E.RenderType.fullDistance || size * E.renderLevel >= distanceTrackPartCamera * Camera.zoom) &&
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
    double[] placementX = {displaceX + (controller ? TP.driverViewX * Options.driverSeat : 0)};
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
      PM.setSelfIlluminationMap(Effects.blink());
     }
    }
   }
  }
 }

 void runAsTrackPart(double distanceTrackPartCamera, boolean renderALL) {
  if (renderALL || ((E.renderType == E.RenderType.fullDistance || size * E.renderLevel >= distanceTrackPartCamera * Camera.zoom) &&
  ((!checkpoint || TP.checkpointNumber == TE.currentCheckpoint) && !(checkpointWord && TE.lapCheckpoint) && !(lapWord && !TE.lapCheckpoint) &&
  !(flickPolarity == 1 && UI.yinYang) && !(flickPolarity == 2 && !UI.yinYang)))) {
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
     PM.setSelfIlluminationMap(Effects.blink());
    }
    if (TP.isRepairPoint) {
     U.rotate(MV, TP.XY, TP.YZ, 0);
    }
    if (checkpoint) {
     rotateXZ.setAngle(-TP.XZ + (TP.checkpointSignRotation ? 180 : 0));
     if (lapWord) {
      PM.setSelfIlluminationMap(UI.yinYang ? Images.white : null);
     }
    }
    U.setTranslate(MV, TP);
    visible = true;
   }
  }
 }
}
