package ve.trackElements.trackParts;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import ve.effects.Effects;
import ve.environment.E;
import ve.environment.Terrain;
import ve.instances.I;
import ve.instances.InstancePart;
import ve.trackElements.TE;
import ve.ui.Maps;
import ve.ui.UI;
import ve.ui.options.Options;
import ve.utilities.Camera;
import ve.utilities.D;
import ve.utilities.Images;
import ve.utilities.Matrix4x3;
import ve.utilities.Phong;
import ve.utilities.U;

public class TrackPartPart extends InstancePart {

 private final TrackPart TP;
 Rotate rotateXZ;

 public TrackPartPart(TrackPart i, double[] i_X, double[] i_Y, double[] i_Z, int vertexQuantity, Color i_RGB, String type, String textureType) {
  TP = i;
  TP.vertexQuantity += vertexQuantity;
  int n;
  double[] storeX = new double[vertexQuantity];
  double[] storeY = new double[vertexQuantity];
  double[] storeZ = new double[vertexQuantity];
  light = type.contains(D.thick(D.light));
  blink = type.contains(D.thick(D.blink));
  selfIlluminate = type.contains(D.thick(D.selfIlluminate));
  base = type.contains(D.thick(D.base));
  controller = type.contains(D.thick(D.controller));
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
  float[] textureCoordinates = U.random() < .5 ? I.textureCoordinateBase0 : I.textureCoordinateBase1;
  for (n = 0; n < vertexQuantity / (double) 3; n++) {
   TM.getTexCoords().addAll(textureCoordinates);//<-'addAll' and NOT 'setAll'
  }
  if (type.contains(D.thick(FaceFunction.triangles.name()))) {
   setTriangles(TM, vertexQuantity);
  } else if (type.contains(D.thick(FaceFunction.conic.name()))) {
   setConic(TM, vertexQuantity, TP);
  } else if (type.contains(D.thick(FaceFunction.strip.name()))) {
   setStrip(TM, vertexQuantity, TP);
  } else if (type.contains(D.thick(FaceFunction.squares.name()))) {
   setSquares(TM, vertexQuantity, TP);
  } else if (type.contains(D.thick(FaceFunction.cylindric.name()))) {
   setCylindric(TM, vertexQuantity, TP);
  } else if (type.contains(D.thick(D.rimFaces))) {
   setConic(TM, 7, TP);
  } else if (type.contains(D.thick(D.sportRimFaces))) {
   setConic(TM, 16, TP);
  } else if (type.contains(D.thick(D.wheelRingFaces))) {
   setCylindric(TM, 48, TP);
   setWheelRingFaces(TM);
  } else {
   setFaces(TM, type.contains(D.thick(D.wheelFaces)) ? 24 : vertexQuantity, TP);
  }
  MV = new MeshView(TM);
  MV.setDrawMode(type.contains(D.thick(D.line)) ? DrawMode.LINE : DrawMode.FILL);
  MV.setCullFace(CullFace.BACK);
  setPhong(type, type.contains(D.thick(D.noTexture)) ? "" : textureType, i_RGB);
  if (UI.status != UI.Status.vehicleViewer) {
   fastCull = type.contains(D.thick(D.fastCullB)) ? 0 : fastCull;
   fastCull = type.contains(D.thick(D.fastCullF)) ? 2 : fastCull;
   fastCull = type.contains(D.thick(D.fastCullR)) ? -1 : fastCull;
   fastCull = type.contains(D.thick(D.fastCullL)) ? 1 : fastCull;
  }
  flickPolarity = type.contains(D.thick(D.flick1)) ? 1 : type.contains(D.thick(D.flick2)) ? 2 : flickPolarity;
  setRenderSizeRequirement(storeX, storeY, storeZ, vertexQuantity, light || blink);// || true);//<-Use 'true' when getting logo image
  MV.setVisible(false);
  if (matrix != null) {
   MV.getTransforms().setAll(matrix);
  }
 }

 private void setPhong(String type, String textureType, Color i_RGB) {//Don't bother moving this void to super
  if (TP.universalPhongMaterialUsage == TrackPart.UniversalPhongMaterialUsage.terrain && !type.contains(D.thick(D.noTexture))) {
   U.setMaterialSecurely(MV, Terrain.universal);
  } else if (TP.universalPhongMaterialUsage == TrackPart.UniversalPhongMaterialUsage.paved && !type.contains(D.thick(D.noTexture))) {
   U.setMaterialSecurely(MV, TE.Paved.universal);
  } else {
   Color RGB = type.contains(D.thick(D.theRandomColor)) ? TP.theRandomColor : i_RGB;
   if (TP.tree && (RGB.getRed() > 0 || RGB.getGreen() > 0 || RGB.getBlue() > 0)) {
    while (RGB.getRed() < 1 && RGB.getGreen() < 1 && RGB.getBlue() < 1) {
     RGB = U.getColor(RGB.getRed() * 1.01, RGB.getGreen() * 1.01, RGB.getBlue() * 1.01);
    }
   }
   if (light) {
    if (Maps.defaultVehicleLightBrightness <= 0 && type.contains(D.thick(D.reflect))) {
     RGB = U.getColor(E.skyRGB);
    } else if (type.contains(D.thick(D.reflect)) || (RGB.getRed() == RGB.getGreen() && RGB.getGreen() == RGB.getBlue())) {
     RGB = U.getColor(1);
    }
   } else if (type.contains(D.thick(D.reflect))) {
    RGB = U.getColor(E.skyRGB);
   }
   RGB =
   TP.universalPhongMaterialUsage == TrackPart.UniversalPhongMaterialUsage.terrain ? U.getColor(Terrain.RGB) :
   TP.universalPhongMaterialUsage == TrackPart.UniversalPhongMaterialUsage.paved ? U.getColor(TE.Paved.globalShade) :
   RGB;//<-Still needed!
   if (blink) {
    RGB = U.getColor(0);
   }
   PM = new PhongMaterial();
   Phong.setDiffuseRGB(PM, RGB);
   if (TP.tree) {
    PM.setSelfIlluminationMap(Phong.getSelfIllumination(RGB.getRed() * .25, RGB.getGreen() * .25, RGB.getBlue() * .25));
   }
   if (type.contains(D.thick(D.noSpecular)) || blink) {
    Phong.setSpecularRGB(PM, 0);
   } else {
    boolean shiny = type.contains(D.thick(D.shiny));
    Phong.setSpecularRGB(PM, shiny ? E.Specular.Colors.shiny : E.Specular.Colors.standard);
    PM.setSpecularPower(shiny ? E.Specular.Powers.shiny : E.Specular.Powers.standard);
   }
   if (selfIlluminate) {
    PM.setSelfIlluminationMap(Phong.getSelfIllumination(RGB));
   }
   PM.setDiffuseMap(Images.get(textureType));
   PM.setSpecularMap(Images.get(textureType));
   PM.setBumpMap(Images.getNormalMap(textureType));
   U.setMaterialSecurely(MV, PM);
  }
 }

 void runAsVehiclePart(double distanceTrackPartCameraTimesFOV, boolean renderALL) {
  if (renderALL || ((E.renderType == E.RenderType.fullDistance || size * E.renderLevel >= distanceTrackPartCameraTimesFOV) &&
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
    render = shiftedAxis == 2 ? Camera.C.Z >= TP.Z : shiftedAxis < 0 ? Camera.C.X >= TP.X : shiftedAxis > 0 ? Camera.C.X <= TP.X : Camera.C.Z <= TP.Z;
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

 void runAsTrackPart(double distanceTrackPartCameraTimesFOV, boolean renderALL) {
  if (renderALL || ((E.renderType == E.RenderType.fullDistance || size * E.renderLevel >= distanceTrackPartCameraTimesFOV) &&
  !(flickPolarity == 1 && U.yinYang) && !(flickPolarity == 2 && !U.yinYang))) {
   boolean render = true;
   if (!Double.isNaN(fastCull) && !renderALL) {
    if (fastCull == 0) {
     render = Camera.C.Z <= TP.Z;
    } else if (fastCull == 2) {
     render = Camera.C.Z >= TP.Z;
    } else if (fastCull == -1) {
     render = Camera.C.X >= TP.X;
    } else if (fastCull == 1) {
     render = Camera.C.X <= TP.X;
    }
   }
   if (renderALL || (render && U.getDepth(TP) > -renderRadius)) {
    if (blink) {
     PM.setSelfIlluminationMap(Effects.blink());
    }
    U.setTranslate(MV, TP);
    visible = true;
   }
  }
 }
}
