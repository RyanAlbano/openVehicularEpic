package ve.trackElements.trackParts;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import ve.environment.E;
import ve.environment.Ground;
import ve.environment.Terrain;
import ve.instances.Instance;
import ve.instances.InstancePart;
import ve.trackElements.TE;
import ve.ui.UI;
import ve.utilities.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TrackPart extends Instance {
 public final Collection<TrackPartPart> parts = new ArrayList<>();
 private Sphere foliageSphere;
 final boolean vehicleModel;
 public boolean wraps;
 private boolean sidewaysXZ;
 boolean tree;
 public boolean rainbow;
 boolean checkpointSignRotation;
 public long checkpointNumber = -1;
 public final List<TrackPlane> trackPlanes = new ArrayList<>();
 UniversalPhongMaterialUsage universalPhongMaterialUsage;

 enum UniversalPhongMaterialUsage {none, terrain, paved}

 private List<RoadRock> roadRocks;

 public TrackPart(String model, double sourceX, double sourceY, double sourceZ, double angle) {
  this(model, sourceX, sourceY, sourceZ, angle, false, 1, new double[]{1, 1, 1});
 }

 public TrackPart(String model, double sourceX, double sourceY, double sourceZ, double angle, boolean isVehicleModel) {
  this(model, sourceX, sourceY, sourceZ, angle, isVehicleModel, 1, new double[]{1, 1, 1});
 }

 public TrackPart(String model, double sourceX, double sourceY, double sourceZ, double angle, double inSize, double[] inScale) {
  this(model, sourceX, sourceY, sourceZ, angle, false, inSize, inScale);
 }

 private TrackPart(String model, double sourceX, double sourceY, double sourceZ, double angle, boolean isVehicleModel, double inSize, double[] inScale) {
  //modelNumber = model;
  vehicleModel = isVehicleModel;
  if (model != null || vehicleModel) {
   modelName = model;
   if (modelName.equals(TE.Models.checkpoint.name())) {//<-Checkpoint sign may glitch otherwise
    while (angle >= 90) angle -= 180;
    while (angle <= -90) angle += 180;//Will always end up at '90' rather then '-90'
   }
   double[] instanceScale = {1, 1, 1};
   instanceScale[0] = inScale[0];
   instanceScale[1] = inScale[1];
   instanceScale[2] = inScale[2];
   double treeRandomXZ = U.randomPlusMinus(180.);
   theRandomColor = U.getColor(U.random(), U.random(), U.random());
   int wheelCount = 0;
   boolean onModelPart = false, onTrackPlane = false, trackPlaneOnly = false, addWheel = false, rocky = false;
   List<Double> xx = new ArrayList<>(), yy = new ArrayList<>(), zz = new ArrayList<>();
   double[] translate = new double[3];
   Color RGB = U.getColor(0);
   StringBuilder type = new StringBuilder(), wheelType = new StringBuilder(), rimType = new StringBuilder();
   String textureType = "", wheelTextureType = "", s = "";
   try (BufferedReader BR = new BufferedReader(new InputStreamReader(getFile(modelName, vehicleModel), U.standardChars))) {
    for (String s1; (s1 = BR.readLine()) != null; ) {
     s = s1.trim();
     if (s.startsWith("<>") && (!s.contains(D.aerialOnly) || sourceY != 0)) {
      onModelPart = true;
      addWheel = trackPlaneOnly = false;
      xx.clear();
      yy.clear();
      zz.clear();
      type.setLength(0);
      textureType = "";
     } else if (s.startsWith("><")) {
      double minimumX = Double.NEGATIVE_INFINITY, maximumX = Double.POSITIVE_INFINITY;
      for (double listX : xx) {
       minimumX = Math.max(minimumX, listX);
       maximumX = Math.min(maximumX, listX);
      }
      double averageX = (minimumX + maximumX) * .5;
      type.append(averageX > 0 ? " R " : averageX < 0 ? " L " : U.random() < .5 ? " R " : " L ");
      if (addWheel && wheelCount < 4) {
       double minimumZ = Double.NEGATIVE_INFINITY, maximumZ = Double.POSITIVE_INFINITY;
       for (double listZ : zz) {
        minimumZ = Math.max(minimumZ, listZ);
        maximumZ = Math.min(maximumZ, listZ);
       }
       for (double listY : yy) {
        clearanceY = Math.max(clearanceY, listY);
       }
       wheelCount++;
      }
      if (String.valueOf(type).contains(" foliage ")) {
       foliageSphere = new Sphere(125, 9);
      } else if (!xx.isEmpty()) {
       if (!trackPlaneOnly && !String.valueOf(type).contains(D.thick(D.thrust))) {
        parts.add(new TrackPartPart(this, U.listToArray(xx), U.listToArray(yy), U.listToArray(zz), xx.size(), RGB, String.valueOf(type), textureType));
       }
      }
      xx.clear();
      onModelPart = false;
     }
     RGB = getLoadColor(s, RGB);
     if (onModelPart) {
      if (s.startsWith("(")) {
       xx.add((U.getValue(s, 0) * modelSize * inSize * modelScale[0] * instanceScale[0]) + translate[0]);
       yy.add((U.getValue(s, 1) * modelSize * inSize * modelScale[1] * instanceScale[1]) + translate[1]);
       zz.add((U.getValue(s, 2) * modelSize * inSize * modelScale[2] * instanceScale[2]) + translate[2]);
       if (!String.valueOf(type).contains(D.thick(D.thrust))) {
        int size = xx.size() - 1;
        addSizes(xx.get(size), yy.get(size), zz.get(size));
       }
      }
      if (xx.size() < 1) {
       trackPlaneOnly = s.startsWith("trackPlaneOnly") || trackPlaneOnly;
       textureType = s.startsWith(D.texture + "(") ? U.getString(s, 0) : textureType;
       type.append(s.startsWith(D.fastCull) ? " " + D.fastCull + (s.endsWith("B") ? "B" : s.endsWith("F") ? "F" : s.endsWith("R") ? "R" : s.endsWith("L") ? "L" : "") + " " : "");
       if (s.startsWith(D.lit)) {
        type.append(D.thick(D.light)).append(s.endsWith(D.fire) ? D.thick(D.fire) : "");
       }
       append(type, s, false, D.thrust,/*<-thrust is here only to forward the property so that the part is NOT loaded*/
       D.reflect, D.blink, D.line, D.selfIlluminate, D.noSpecular, D.shiny, D.noTexture, D.flick1, D.flick2, "onlyAerial", D.foliage,
       InstancePart.FaceFunction.conic.name(),
       InstancePart.FaceFunction.cylindric.name(),
       InstancePart.FaceFunction.strip.name(),
       InstancePart.FaceFunction.squares.name(),
       InstancePart.FaceFunction.triangles.name(),
       D.base);
       type.append(s.startsWith("checkpointWord") ? D.thick(D.checkPointWord) : "");
       type.append(s.startsWith(D.lapWord) ? D.thick(D.lapWord) : "");
       if (s.startsWith(D.controller)) {
        type.append(D.thick(D.controller)).append(s.contains(D.XY) ? " controllerXY " : s.contains(D.XZ) ? " controllerXZ " : "");
       } else if (s.startsWith(D.wheel)) {
        type.append(D.thick(D.wheel));
        addWheel = s.startsWith(D.wheelPoint) || addWheel;
       }
      }
      computeTrackPlane(s, xx, yy, zz, RGB);
     }
     driverViewX = s.startsWith("driverViewX" + "(") ? Math.abs(U.getValue(s, 0) * modelSize * inSize) + translate[0] : driverViewX;
     turretBaseY = s.startsWith("turretBaseY" + "(") ? U.getValue(s, 0) : turretBaseY;
     wraps = s.startsWith("scenery") || wraps;
     tree = s.startsWith(D.tree) || tree;
     rocky = s.startsWith("rocky") || rocky;
     universalPhongMaterialUsage =
     s.startsWith("mapTerrain") ? UniversalPhongMaterialUsage.terrain :
     s.startsWith("universalPaved") ? UniversalPhongMaterialUsage.paved :
     universalPhongMaterialUsage;
     getSizeScaleTranslate(s, translate, inSize, instanceScale);
     if (s.startsWith("wheelColor" + "(")) {
      if (s.contains(D.reflect)) {
       wheelType.append(D.thick(D.reflect));
      } else {
       try {
        wheelRGB = U.getColor(U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
       } catch (RuntimeException e) {
        if (s.contains(D.theRandomColor)) {
         wheelRGB = theRandomColor;
         wheelType.append(D.thick(D.theRandomColor));
        } else {
         wheelRGB = U.getColor(U.getValue(s, 0));
        }
       }
      }
      append(wheelType, s, true, D.noSpecular, D.shiny);
     } else if (s.startsWith("rims" + "(")) {
      rimType.setLength(0);
      rimRadius = U.getValue(s, 0) * modelSize * inSize;
      rimDepth = Math.max(rimRadius * .0625, U.getValue(s, 1) * modelSize * inSize);
      try {
       rimRGB = U.getColor(U.getValue(s, 2), U.getValue(s, 3), U.getValue(s, 4));
      } catch (RuntimeException e) {
       if (s.contains(D.theRandomColor)) {
        rimRGB = theRandomColor;
        rimType.append(D.thick(D.theRandomColor));
       } else {
        rimRGB = U.getColor(U.getValue(s, 2));
       }
      }
      append(rimType, s, true, D.reflect, D.noSpecular, D.shiny, D.sport);
     }
     wheelTextureType = s.startsWith("wheelTexture" + "(") ? U.getString(s, 0) : wheelTextureType;//<-Using 'append' would mess this up if found more than once in file
     wheelSmoothing = s.startsWith("smoothing" + "(") ? U.getValue(s, 0) * modelSize : wheelSmoothing;
     if (s.startsWith(D.wheel + "(")) {
      String side = U.getValue(s, 0) > 0 ? " R " : U.getValue(s, 0) < 0 ? " L " : U.random() < .5 ? " R " : " L ";
      loadWheel(null, this, U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2), U.getValue(s, 3), U.getValue(s, 4), wheelType + side, String.valueOf(rimType), wheelTextureType, s.contains(D.steers), s.contains(D.hide));
      wheelCount++;
     } else if (s.startsWith("<t>") && (!s.contains(D.aerialOnly) || sourceY != 0)) {
      trackPlanes.add(new TrackPlane());
      onTrackPlane = true;
      if (universalPhongMaterialUsage == UniversalPhongMaterialUsage.terrain) {
       trackPlanes.get(trackPlanes.size() - 1).RGB = Ground.RGB;
       trackPlanes.get(trackPlanes.size() - 1).type += Terrain.terrain;
      } else if (universalPhongMaterialUsage == UniversalPhongMaterialUsage.paved) {
       trackPlanes.get(trackPlanes.size() - 1).RGB = U.getColor(TE.Paved.globalShade);
       trackPlanes.get(trackPlanes.size() - 1).type += D.thick(D.paved);
      }
     } else if (s.startsWith(">t<")) {
      onTrackPlane = false;
     }
     if (onTrackPlane) {
      int index = trackPlanes.size() - 1;
      if (s.startsWith(D.RGB)) {
       try {
        trackPlanes.get(index).RGB = U.getColor(U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
       } catch (RuntimeException E) {
        trackPlanes.get(index).RGB = U.getColor(U.getValue(s, 0));
       }
      } else if (s.startsWith(D.pavedColor)) {
       trackPlanes.get(index).RGB = U.getColor(TE.Paved.globalShade);
      } else if (s.startsWith("wall")) {
       trackPlanes.get(index).wall =
       s.contains("F") ? TrackPlane.Wall.front :
       s.contains("B") ? TrackPlane.Wall.back :
       s.contains("R") ? TrackPlane.Wall.right :
       s.contains("L") ? TrackPlane.Wall.left :
       TrackPlane.Wall.none;
      }
      trackPlanes.get(index).XY = s.startsWith("XY(") ? U.getValue(s, 0) : trackPlanes.get(index).XY;
      trackPlanes.get(index).YZ = s.startsWith("YZ(") ? U.getValue(s, 0) : trackPlanes.get(index).YZ;
      trackPlanes.get(index).radiusX = s.startsWith("spanX(") ? U.getValue(s, 0) * modelSize * inSize * instanceScale[0] : trackPlanes.get(index).radiusX;
      trackPlanes.get(index).radiusZ = s.startsWith("spanZ(") ? U.getValue(s, 0) * modelSize * inSize * instanceScale[2] : trackPlanes.get(index).radiusZ;
      trackPlanes.get(index).radiusY = s.startsWith("spanY(") ? U.getValue(s, 0) * modelSize * inSize * instanceScale[1] : trackPlanes.get(index).radiusY;
      trackPlanes.get(index).radiusY = trackPlanes.get(index).radiusY < 0 ? Double.POSITIVE_INFINITY : trackPlanes.get(index).radiusY;
      trackPlanes.get(index).X = s.startsWith("X(") ? U.getValue(s, 0) * modelSize * inSize * instanceScale[0] : trackPlanes.get(index).X;
      trackPlanes.get(index).Y = s.startsWith("Y(") ? U.getValue(s, 0) * modelSize * inSize * instanceScale[1] : trackPlanes.get(index).Y;
      trackPlanes.get(index).Z = s.startsWith("Z(") ? U.getValue(s, 0) * modelSize * inSize * instanceScale[2] : trackPlanes.get(index).Z;
      trackPlanes.get(index).type += s.startsWith(D.type + "(") ? " " + U.getString(s, 0) + " " : "";
      trackPlanes.get(index).damage = s.startsWith("damage(") ? U.getValue(s, 0) : trackPlanes.get(index).damage;
     }
    }
   } catch (IOException e) {
    System.out.println(U.modelLoadingError + e);
    System.out.println(UI.At_File_ + model);
    System.out.println(UI.At_Line_ + s);
   }
   maxMinusX[1] /= vertexQuantity;
   maxPlusX[1] /= vertexQuantity;
   maxMinusY[1] /= vertexQuantity;
   maxPlusY[1] /= vertexQuantity;
   maxMinusZ[1] /= vertexQuantity;
   maxPlusZ[1] /= vertexQuantity;
   absoluteRadius = Math.abs(maxMinusX[0]) + Math.abs(maxPlusX[0]) + Math.abs(maxMinusY[0]) + Math.abs(maxPlusY[0]) + Math.abs(maxMinusZ[0]) + Math.abs(maxPlusZ[0]) + Math.abs(maxMinusX[1]) + Math.abs(maxPlusX[1]) + Math.abs(maxMinusY[1]) + Math.abs(maxPlusY[1]) + Math.abs(maxMinusZ[1]) + Math.abs(maxPlusZ[1]);
   X = sourceX;
   Y = sourceY;
   Z = sourceZ;
   XZ = angle;
   while (XZ > 180) XZ -= 360;
   while (XZ < -180) XZ += 360;
   if (Math.abs(XZ) > 45 && Math.abs(XZ) < 135) {
    double storeBoundsX = boundsX;
    boundsX = boundsZ;
    boundsZ = storeBoundsX;
   }
   for (var part : parts) {
    if (!Double.isNaN(part.fastCull)) {
     if (XZ > 45 && XZ < 135) {
      part.fastCull = --part.fastCull < -1 ? 2 : part.fastCull;
     } else if (XZ < -45 && XZ > -135) {
      part.fastCull = ++part.fastCull > 2 ? -1 : part.fastCull;
     } else if (Math.abs(XZ) > 135) {
      part.fastCull = ++part.fastCull > 2 ? -1 : part.fastCull;
      part.fastCull = ++part.fastCull > 2 ? -1 : part.fastCull;
     }
    }
    if (!vehicleModel) {
     part.rotateXZ = new Rotate();
     part.MV.getTransforms().add(part.rotateXZ);
     part.rotateXZ.setAxis(Rotate.Y_AXIS);
     part.rotateXZ.setAngle(tree ? treeRandomXZ : -XZ);
    }
    Nodes.add(part.MV);
   }
   if (foliageSphere != null) {
    PhongMaterial PM = new PhongMaterial();
    PM.setDiffuseMap(Images.get(D.foliage));
    PM.setBumpMap(Images.getNormalMap(D.foliage));
    PM.setSelfIlluminationMap(Phong.getSelfIllumination(.125));
    Phong.setDiffuseRGB(PM, 1);
    Phong.setSpecularRGB(PM, 0);
    U.setMaterialSecurely(foliageSphere, PM);
    Nodes.add(foliageSphere);
   }
   for (var trackPlane : trackPlanes) {
    if (Math.abs(XZ) > 135) {
     trackPlane.YZ *= -1;
     trackPlane.XY *= -1;
    } else if (XZ >= 45 || XZ <= -45) {
     double storeYZ = trackPlane.YZ;
     trackPlane.YZ = trackPlane.XY * (XZ >= 45 ? 1 : -1);
     trackPlane.XY = storeYZ * (XZ >= 45 ? -1 : 1);
    }
    double storeX = trackPlane.X, storeZ = trackPlane.Z;
    trackPlane.X = storeX * U.cos(XZ) - storeZ * U.sin(XZ);
    trackPlane.Z = storeZ * U.cos(XZ) + storeX * U.sin(XZ);
    double storeRadiusX = trackPlane.radiusX, storeRadiusZ = trackPlane.radiusZ;
    trackPlane.radiusX = storeRadiusX * Math.abs(U.cos(XZ)) + storeRadiusZ * Math.abs(U.sin(XZ));
    trackPlane.radiusZ = storeRadiusX * Math.abs(U.sin(XZ)) + storeRadiusZ * Math.abs(U.cos(XZ));
    if (trackPlane.wall != TrackPlane.Wall.none) {
     if (Math.abs(XZ) > 135) {
      trackPlane.wall =
      trackPlane.wall == TrackPlane.Wall.front ? TrackPlane.Wall.back :
      trackPlane.wall == TrackPlane.Wall.left ? TrackPlane.Wall.right :
      trackPlane.wall == TrackPlane.Wall.back ? TrackPlane.Wall.front :
      trackPlane.wall == TrackPlane.Wall.right ? TrackPlane.Wall.left :
      TrackPlane.Wall.none;
     } else if (XZ >= 45) {
      trackPlane.wall =
      trackPlane.wall == TrackPlane.Wall.front ? TrackPlane.Wall.left :
      trackPlane.wall == TrackPlane.Wall.left ? TrackPlane.Wall.back :
      trackPlane.wall == TrackPlane.Wall.back ? TrackPlane.Wall.right :
      trackPlane.wall == TrackPlane.Wall.right ? TrackPlane.Wall.front :
      TrackPlane.Wall.none;
     } else if (XZ <= -45) {
      trackPlane.wall =
      trackPlane.wall == TrackPlane.Wall.front ? TrackPlane.Wall.right :
      trackPlane.wall == TrackPlane.Wall.right ? TrackPlane.Wall.back :
      trackPlane.wall == TrackPlane.Wall.back ? TrackPlane.Wall.left :
      trackPlane.wall == TrackPlane.Wall.left ? TrackPlane.Wall.front :
      TrackPlane.Wall.none;
     }
    }
   }
   if (rocky) {
    roadRocks = new ArrayList<>();
    for (int n = 50; --n >= 0; ) {
     roadRocks.add(new RoadRock(X, Y, Z, XZ));
    }
   }
   sidewaysXZ = Math.abs(U.cos(XZ)) < U.sin45;
   Quaternion baseXZ = new Quaternion(0, U.sin(XZ * .5), 0, U.cos(XZ * .5)),
   baseYZ = new Quaternion(-U.sin(YZ * .5), 0, 0, U.cos(YZ * .5)),
   baseXY = new Quaternion(0, 0, -U.sin(XY * .5), U.cos(XY * .5));
   rotation = baseXY.multiply(baseYZ).multiply(baseXZ);
   if (vehicleModel) {
    Y += -clearanceY - turretBaseY;
   }
  }
 }

 private void computeTrackPlane(String s, List<Double> xx, List<Double> yy, List<Double> zz, Color RGB) {//Keep as a void?
  if (s.startsWith("track(")) {
   trackPlanes.add((new TrackPlane()));
   TrackPlane TP = trackPlanes.get(trackPlanes.size() - 1);
   if (universalPhongMaterialUsage == UniversalPhongMaterialUsage.paved) {
    TP.RGB = U.getColor(TE.Paved.globalShade);
   } else if (universalPhongMaterialUsage == UniversalPhongMaterialUsage.terrain) {
    TP.RGB = Ground.RGB;
   } else {
    TP.RGB = RGB;
   }
   double averageX = xx.get(0), averageY = yy.get(0), averageZ = zz.get(0),
   rangeNegativeX = xx.get(0), rangePositiveX = xx.get(0),
   rangeNegativeY = yy.get(0), rangePositiveY = yy.get(0),
   rangeNegativeZ = zz.get(0), rangePositiveZ = zz.get(0);
   for (int n1 = xx.size(); --n1 > 0; ) {
    averageX += xx.get(n1);
    averageY += yy.get(n1);
    averageZ += zz.get(n1);
    rangeNegativeX = Math.min(rangeNegativeX, xx.get(n1));
    rangePositiveX = Math.max(rangePositiveX, xx.get(n1));
    rangeNegativeY = Math.min(rangeNegativeY, yy.get(n1));
    rangePositiveY = Math.max(rangePositiveY, yy.get(n1));
    rangeNegativeZ = Math.min(rangeNegativeZ, zz.get(n1));
    rangePositiveZ = Math.max(rangePositiveZ, zz.get(n1));
   }
   TP.X = averageX / xx.size();
   TP.Y = averageY / yy.size();
   TP.Z = averageZ / zz.size();
   TP.radiusX = Math.abs(rangeNegativeX - rangePositiveX) * .5;
   TP.radiusY = Math.abs(rangeNegativeY - rangePositiveY) * .5;
   TP.radiusZ = Math.abs(rangeNegativeZ - rangePositiveZ) * .5;
   TP.YZ = s.contains(D.getYZ) ? U.arcTan(TP.radiusY / TP.radiusZ) * (s.contains("-getYZ") ? 1 : -1) : TP.YZ;
   TP.XY = s.contains(D.getXY) ? U.arcTan(TP.radiusY / TP.radiusX) * (s.contains("-getXY") ? 1 : -1) : TP.XY;
   TP.wall =
   s.contains("wallF") ? TrackPlane.Wall.front :
   s.contains("wallB") ? TrackPlane.Wall.back :
   s.contains("wallR") ? TrackPlane.Wall.right :
   s.contains("wallL") ? TrackPlane.Wall.left :
   TP.wall;
   if (s.contains("useLargerRadius")) {
    if (s.contains(D.getYZ)) {
     double largerRadius = Math.max(TP.radiusY, TP.radiusZ);
     TP.radiusY = TP.radiusZ = largerRadius;
    }
    if (s.contains(D.getXY)) {
     double largerRadius = Math.max(TP.radiusX, TP.radiusY);
     TP.radiusX = TP.radiusY = largerRadius;
    }
   }
   TP.addSpeed = true;
   try {
    TP.type = D.thick(U.getString(s, 0));
    TP.damage = U.getValue(s, 1);
   } catch (RuntimeException ignored) {
   }
  }
 }

 public void setInitialSit() {
  if (wraps) {
   E.setTerrainSit(this, false);
  }
 }

 public void runGraphics(boolean renderALL) {
  if (rainbow) {
   for (var part : parts) {
    U.setTranslate(part.MV, Camera.C.X + X, Camera.C.Y + Y, Camera.C.Z + Z);
    part.MV.setVisible(true);
   }
  } else {
   if (wraps) {
    E.wrap(this);
   }
   boolean showFoliageSphere = false;
   if (U.getDepth(this) > -renderRadius) {
    if (checkpointNumber >= 0 && checkpointNumber == TE.currentCheckpoint) {
     checkpointSignRotation = sidewaysXZ ? (XZ > 0 ? Camera.C.X < X : Camera.C.X > X) : Camera.C.Z > Z;//If checkpoint, XZ is never > Math.abs(90)
    }
    double distanceToCameraTimesFOV = U.distance(this) * Camera.FOV;
    if (vehicleModel) {
     for (var part : parts) {
      part.runAsVehiclePart(distanceToCameraTimesFOV, renderALL);
     }
    } else {
     for (var part : parts) {
      part.runAsTrackPart(distanceToCameraTimesFOV, renderALL);
     }
    }
    if (foliageSphere != null) {
     showFoliageSphere = true;
     foliageSphere.setCullFace(U.getDepth(X, Y - 425, Z) > foliageSphere.getRadius() ? CullFace.BACK : CullFace.NONE);
     U.setTranslate(foliageSphere, X, Y - 425, Z);
    }
   }
   if (foliageSphere != null) {
    foliageSphere.setVisible(showFoliageSphere);
   }
   for (var part : parts) {
    part.MV.setVisible(part.visible);
    part.visible = false;
   }
   if (roadRocks != null) {
    for (var rock : roadRocks) {
     rock.run();
    }
   }
  }
 }
}
