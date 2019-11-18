package ve.trackElements.trackParts;

import java.io.*;
import java.util.*;

import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import org.fxyz3d.shapes.primitives.FrustumMesh;
import org.fxyz3d.shapes.Torus;
import ve.*;
import ve.environment.E;
import ve.environment.Tornado;
import ve.trackElements.TE;
import ve.utilities.*;

public class TrackPart extends Instance {
 public final Collection<TrackPartPart> parts = new ArrayList<>();
 private Sphere foliageSphere;
 private Torus repairTorus;
 boolean vehicleModel;
 public boolean isRepairPoint;
 public boolean wraps;
 boolean tree;
 public boolean rainbow;
 boolean checkpointSignRotation;
 public long checkpointNumber = -1;
 private double netSpeedX, netSpeedY, netSpeedZ;
 private boolean renderAlways;
 public final List<TrackPlane> trackPlanes = new ArrayList<>();
 public FrustumMesh mound;
 UniversalPhongMaterialUsage universalPhongMaterialUsage;

 enum UniversalPhongMaterialUsage {none, terrain, paved}

 static class RoadRock extends Core {
  final Sphere S;

  RoadRock() {
   S = new Sphere(1, 5);
  }
 }

 private List<RoadRock> roadRocks;

 private final List<Cylinder> repairShocks = new ArrayList<>();

 public TrackPart(int model, double sourceX, double sourceY, double sourceZ, double angle) {
  this(model, sourceX, sourceY, sourceZ, angle, false, 1, new double[]{1, 1, 1});
 }

 public TrackPart(int model, double sourceX, double sourceY, double sourceZ, double angle, boolean isVehicleModel) {
  this(model, sourceX, sourceY, sourceZ, angle, isVehicleModel, 1, new double[]{1, 1, 1});
 }

 public TrackPart(int model, double sourceX, double sourceY, double sourceZ, double angle, double inSize, double[] inScale) {
  this(model, sourceX, sourceY, sourceZ, angle, false, inSize, inScale);
 }

 private TrackPart(int model, double sourceX, double sourceY, double sourceZ, double angle, boolean isVehicleModel, double inSize, double[] inScale) {
  modelNumber = model;
  vehicleModel = isVehicleModel;
  if (model >= 0 || vehicleModel) {
   modelName = vehicleModel ? VE.vehicleModels.get(modelNumber) : TE.getTrackPartName(modelNumber);
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
   boolean onModelPart = false, onTrackPlane = false, addWheel = false, rocky = false;
   List<Double> xx = new ArrayList<>(), yy = new ArrayList<>(), zz = new ArrayList<>();
   double[] translate = new double[3];
   Color RGB = U.getColor(0);
   StringBuilder type = new StringBuilder(), wheelType = new StringBuilder(), rimType = new StringBuilder();
   String textureType = "", wheelTextureType = "", s = "";
   try (BufferedReader BR = new BufferedReader(new InputStreamReader(getFile(modelName), U.standardChars))) {
    for (String s1; (s1 = BR.readLine()) != null; ) {
     s = s1.trim();
     if (s.startsWith("<>") && (!s.contains(SL.Instance.aerialOnly) || sourceY != 0)) {
      onModelPart = true;
      addWheel = false;
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
      } else if (!xx.isEmpty() && !String.valueOf(type).contains(" thrust ")) {
       parts.add(new TrackPartPart(this, U.listToArray(xx), U.listToArray(yy), U.listToArray(zz), xx.size(), RGB, String.valueOf(type), textureType));
       xx.clear();
      }
      onModelPart = false;
     }
     RGB = getLoadColor(s, RGB);
     if (onModelPart) {
      if (s.startsWith("(")) {
       xx.add((U.getValue(s, 0) * modelSize * inSize * modelScale[0] * instanceScale[0]) + translate[0]);
       yy.add((U.getValue(s, 1) * modelSize * inSize * modelScale[1] * instanceScale[1]) + translate[1]);
       zz.add((U.getValue(s, 2) * modelSize * inSize * modelScale[2] * instanceScale[2]) + translate[2]);
       if (!String.valueOf(type).contains(" thrust ")) {
        int size = xx.size() - 1;
        addSizes(xx.get(size), yy.get(size), zz.get(size));
       }
      }
      if (xx.size() < 1) {
       textureType = s.startsWith("texture(") ? U.getString(s, 0) : textureType;
       type.append(s.startsWith("cs") ? " fastCull" + (s.endsWith("B") ? "B" : s.endsWith("F") ? "F" : s.endsWith("R") ? "R" : s.endsWith("L") ? "L" : "") + " " : "");
       if (s.startsWith("lit")) {
        type.append(" light ").append(s.endsWith("fire") ? " fire " : "");
       }
       append(type, s, false, "thrust",/*<-thrust is here only to forward the property so that the part is NOT loaded*/
       SL.Instance.reflect, SL.Instance.blink, "line", SL.Instance.selfIlluminate, SL.Instance.noSpecular, SL.Instance.shiny, SL.Instance.noTexture, SL.Instance.flick1, SL.Instance.flick2, "onlyAerial", "foliage",
       InstancePart.FaceFunction.conic.name(),
       InstancePart.FaceFunction.cylindric.name(),
       InstancePart.FaceFunction.strip.name(),
       InstancePart.FaceFunction.squares.name(),
       InstancePart.FaceFunction.triangles.name(),
       "base");
       type.append(s.startsWith("checkpointWord") ? " checkpointWord " : "");
       type.append(s.startsWith("lapWord") ? " lapWord " : "");
       if (s.startsWith(SL.Instance.controller)) {
        type.append(" controller ").append(s.contains("XY") ? " controllerXY " : s.contains("XZ") ? " controllerXZ " : "");
       } else if (s.startsWith("wheel")) {
        type.append(" wheel ");
        addWheel = s.startsWith("wheelPoint") || addWheel;
       }
      }
      computeTrackPlane(s, xx, yy, zz, RGB);
     }
     driverViewX = s.startsWith("driverViewX(") ? Math.abs(U.getValue(s, 0) * modelSize * inSize) + translate[0] : driverViewX;
     turretBaseY = s.startsWith("baseY(") ? U.getValue(s, 0) : turretBaseY;
     wraps = s.startsWith("scenery") || wraps;
     tree = s.startsWith("tree") || tree;
     if (s.startsWith(TE.Models.repair.name())) {
      isRepairPoint = true;
      repairTorus = new Torus();
      setRepairTorusDetail(true);
      repairTorus.setMaterial(E.Terrain.universal);//<-Can't set this securely!
      U.Nodes.add(repairTorus);
      repairTorus.setRotationAxis(Rotate.Y_AXIS);
      repairTorus.setRotate(angle);
     }
     rocky = s.startsWith("rocky") || rocky;
     universalPhongMaterialUsage =
     s.startsWith("mapTerrain") ? UniversalPhongMaterialUsage.terrain :
     s.startsWith("universalPaved") ? UniversalPhongMaterialUsage.paved :
     universalPhongMaterialUsage;
     getSizeScaleTranslate(s, translate, inSize, instanceScale);
     if (s.startsWith("wheelColor(")) {
      if (s.contains(SL.Instance.reflect)) {
       wheelType.append(" reflect ");
      } else {
       try {
        wheelRGB = U.getColor(U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
       } catch (RuntimeException e) {
        if (s.contains(SL.Instance.theRandomColor)) {
         wheelRGB = theRandomColor;
         wheelType.append(" theRandomColor ");
        } else {
         wheelRGB = U.getColor(U.getValue(s, 0));
        }
       }
      }
      append(wheelType, s, true, SL.Instance.noSpecular, SL.Instance.shiny);
     } else if (s.startsWith("rims(")) {
      rimType.setLength(0);
      rimRadius = U.getValue(s, 0) * modelSize * inSize;
      rimDepth = Math.max(rimRadius * .0625, U.getValue(s, 1) * modelSize * inSize);
      try {
       rimRGB = U.getColor(U.getValue(s, 2), U.getValue(s, 3), U.getValue(s, 4));
      } catch (RuntimeException e) {
       if (s.contains(SL.Instance.theRandomColor)) {
        rimRGB = theRandomColor;
        rimType.append(" theRandomColor ");
       } else {
        rimRGB = U.getColor(U.getValue(s, 2));
       }
      }
      append(rimType, s, true, SL.Instance.reflect, SL.Instance.noSpecular, SL.Instance.shiny, SL.Instance.sport);
     }
     wheelType.append(s.startsWith("landingGearWheels") ? " landingGear " : "");
     wheelTextureType = s.startsWith("wheelTexture(") ? U.getString(s, 0) : wheelTextureType;//<-Using 'append' would mess this up if found more than once in file
     wheelSmoothing = s.startsWith("smoothing(") ? U.getValue(s, 0) * modelSize : wheelSmoothing;
     if (s.startsWith("wheel(")) {
      String side = U.getValue(s, 0) > 0 ? " R " : U.getValue(s, 0) < 0 ? " L " : U.random() < .5 ? " R " : " L ";
      loadWheel(null, this, U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2), U.getValue(s, 3), U.getValue(s, 4), wheelType + side, String.valueOf(rimType), wheelTextureType, s.contains("steers"), s.contains("hide"));
      wheelCount++;
     } else if (s.startsWith("<t>") && (!s.contains(SL.Instance.aerialOnly) || sourceY != 0)) {
      trackPlanes.add((new TrackPlane()));
      onTrackPlane = true;
      if (universalPhongMaterialUsage == UniversalPhongMaterialUsage.terrain) {
       trackPlanes.get(trackPlanes.size() - 1).RGB = E.Ground.RGB;
       trackPlanes.get(trackPlanes.size() - 1).type += E.Terrain.terrain;
      } else if (universalPhongMaterialUsage == UniversalPhongMaterialUsage.paved) {
       trackPlanes.get(trackPlanes.size() - 1).RGB = U.getColor(TE.Paved.globalShade);
       trackPlanes.get(trackPlanes.size() - 1).type += " paved ";
      }
      trackPlanes.get(trackPlanes.size() - 1).damage = 1;
     } else if (s.startsWith(">t<")) {
      onTrackPlane = false;
     }
     if (onTrackPlane) {
      int index = trackPlanes.size() - 1;
      if (s.startsWith("RGB")) {
       try {
        trackPlanes.get(index).RGB = U.getColor(U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
       } catch (RuntimeException E) {
        trackPlanes.get(index).RGB = U.getColor(U.getValue(s, 0));
       }
      } else if (s.startsWith("pavedColor")) {
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
      trackPlanes.get(index).type += s.startsWith("type(") ? " " + U.getString(s, 0) + " " : "";
      trackPlanes.get(index).damage = s.startsWith("damage(") ? U.getValue(s, 0) : trackPlanes.get(index).damage;
     }
    }
   } catch (IOException e) {
    System.out.println(U.modelLoadingError + e);
    System.out.println("at File: " + model);
    System.out.println("at Line: " + s);
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
   for (TrackPartPart part : parts) {
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
    U.Nodes.add(part.MV);
   }
   if (foliageSphere != null) {
    PhongMaterial PM = new PhongMaterial();
    PM.setDiffuseMap(U.Images.get("foliage"));
    PM.setBumpMap(U.Images.getNormalMap("foliage"));
    U.Phong.setSelfIllumination(PM, .125, .125, .125);
    U.Phong.setDiffuseRGB(PM, 1);
    U.Phong.setSpecularRGB(PM, 0);
    U.setMaterialSecurely(foliageSphere, PM);
    U.Nodes.add(foliageSphere);
   }
   for (TrackPlane trackPlane : trackPlanes) {
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
    storeX = trackPlane.radiusX;
    storeZ = trackPlane.radiusZ;
    trackPlane.radiusX = storeX * Math.abs(U.cos(XZ)) + storeZ * Math.abs(U.sin(XZ));
    trackPlane.radiusZ = storeX * Math.abs(U.sin(XZ)) + storeZ * Math.abs(U.cos(XZ));
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
     RoadRock rock = new RoadRock();
     rock.S.setScaleX(100 + U.random(200.));
     rock.S.setScaleY(U.random(25.));
     rock.S.setScaleZ(100 + U.random(200.));
     U.rotate(rock.S, 0, U.random(360.));
     rock.X = X + U.randomPlusMinus(Math.max(850, 3000 * Math.abs(U.sin(XZ))));
     rock.Y = Y;
     rock.Z = Z + U.randomPlusMinus(Math.max(850, 3000 * Math.abs(U.cos(XZ))));
     PhongMaterial PM = new PhongMaterial();
     U.setMaterialSecurely(rock.S, PM);
     PM.setDiffuseMap(U.Images.get("rock"));
     PM.setSpecularMap(U.Images.get("rock"));
     PM.setBumpMap(U.Images.getNormalMap("rock"));
     U.Nodes.add(rock.S);
     roadRocks.add(rock);
    }
   }
   if (isRepairPoint) {
    PhongMaterial PM = new PhongMaterial();
    U.Phong.setSpecularRGB(PM, 0);
    PM.setSelfIlluminationMap(U.Images.get(SL.Images.white));
    for (int n = 0; n < 8; n++) {
     repairShocks.add(new Cylinder(Math.round(U.random(5.)) + 1, 1100, 4));
     U.setMaterialSecurely(repairShocks.get(n), PM);
     U.Nodes.add(repairShocks.get(n));
     repairShocks.get(n).setVisible(false);
    }
   }
   Quaternion baseXZ = new Quaternion(0, U.sin(XZ * .5), 0, U.cos(XZ * .5)),
   baseYZ = new Quaternion(-U.sin(YZ * .5), 0, 0, U.cos(YZ * .5)),
   baseXY = new Quaternion(0, 0, -U.sin(XY * .5), U.cos(XY * .5));
   rotation = baseXY.multiply(baseYZ).multiply(baseXZ);
   if (vehicleModel) {
    Y += -clearanceY - turretBaseY;
   }
  }
 }

 public TrackPart(double x, double z, double y, double majorRadius, double minorRadius, double height, boolean wraps, boolean paved, boolean renderAlways) {//<-Changing method order to X,Y,Z will misplace mounds!
  while (majorRadius > 0 && minorRadius > majorRadius) {
   minorRadius *= .5;
  }
  mound = new FrustumMesh(majorRadius, minorRadius, height, wraps ? 0 : 1);
  X = x;
  Z = z;
  Y = y;
  this.wraps = wraps;
  this.renderAlways = renderAlways;
  renderRadius = Math.max(mound.getMajorRadius(), Math.max(mound.getMinorRadius(), mound.getHeight()));
  U.setMaterialSecurely(mound, paved ? TE.Paved.universal : E.Terrain.universal);
  U.Nodes.add(mound);
  U.rotate(mound, 0, U.random(360.));//<-For visual variation
 }

 private void computeTrackPlane(String s, List<Double> xx, List<Double> yy, List<Double> zz, Color RGB) {//Keep as a void?
  if (s.startsWith("track(")) {
   trackPlanes.add((new TrackPlane()));
   TrackPlane TP = trackPlanes.get(trackPlanes.size() - 1);
   if (universalPhongMaterialUsage == UniversalPhongMaterialUsage.paved) {
    TP.RGB = U.getColor(TE.Paved.globalShade);
   } else if (universalPhongMaterialUsage == UniversalPhongMaterialUsage.terrain) {
    TP.RGB = E.Ground.RGB;
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
   TP.YZ = s.contains("getYZ") ? U.arcTan(TP.radiusY / TP.radiusZ) * (s.contains("-getYZ") ? 1 : -1) : TP.YZ;
   TP.XY = s.contains("getXY") ? U.arcTan(TP.radiusY / TP.radiusX) * (s.contains("-getXY") ? 1 : -1) : TP.XY;
   TP.wall =
   s.contains("wallF") ? TrackPlane.Wall.front :
   s.contains("wallB") ? TrackPlane.Wall.back :
   s.contains("wallR") ? TrackPlane.Wall.right :
   s.contains("wallL") ? TrackPlane.Wall.left :
   TP.wall;
   TP.damage = 1;
   if (s.contains("useLargerRadius")) {
    if (s.contains("getYZ")) {
     double largerRadius = Math.max(TP.radiusY, TP.radiusZ);
     TP.radiusY = TP.radiusZ = largerRadius;
    }
    if (s.contains("getXY")) {
     double largerRadius = Math.max(TP.radiusX, TP.radiusY);
     TP.radiusX = TP.radiusY = largerRadius;
    }
   }
   TP.addSpeed = true;
   try {
    TP.type = " " + U.getString(s, 0) + " ";
    TP.damage = U.getValue(s, 1);
   } catch (RuntimeException ignored) {
   }
  }
 }

 public void runGraphics(boolean renderALL) {
  distanceToCamera = U.distance(this);
  if (rainbow) {
   for (TrackPartPart part : parts) {
    U.setTranslate(part.MV, Camera.X + X, Camera.Y + Y, Camera.Z + Z);
    part.MV.setVisible(true);
   }
  } else {
   if (wraps) {
    boolean setSlope = false;
    if (VE.Map.name.equals(SL.MN.speedway2000000) && Math.abs(X) < 2000) {//<-No trees/dirt on the speedway!
     X += U.random() < .5 ? 2000 : -2000;
     setSlope = true;
    }
    if (Math.abs(X - Camera.X) > 40000) {
     while (X > Camera.X + 40000) X -= 80000;
     while (X < Camera.X - 40000) X += 80000;
     setSlope = true;
    }
    if (Math.abs(Z - Camera.Z) > 40000) {
     while (Z > Camera.Z + 40000) Z -= 80000;
     while (Z < Camera.Z - 40000) Z += 80000;
     setSlope = true;
    }
    if (setSlope) {
     E.setTerrainSit(this, false);
    }
   }
   if (mound != null) {
    double moundY = Y + -mound.getHeight() * .5, depth = U.getDepth(X, moundY, Z);
    if (depth > -renderRadius && (renderAlways || renderRadius * E.renderLevel >= distanceToCamera * Camera.zoom)) {
     mound.setCullFace(depth > renderRadius ? CullFace.BACK : CullFace.NONE);
     U.setTranslate(mound, X, moundY, Z);
     mound.setVisible(true);
    } else {
     mound.setVisible(false);
    }
   }
   if (isRepairPoint) {
    if (!Tornado.parts.isEmpty() && Tornado.movesRepairPoints) {
     netSpeedX *= .995;
     netSpeedY *= .995;
     netSpeedZ *= .995;
     netSpeedX += X < Tornado.parts.get(0).X ? U.random(125.) : X > Tornado.parts.get(0).X ? -U.random(125.) : 0;
     netSpeedZ += Z < Tornado.parts.get(0).Z ? U.random(125.) : Z > Tornado.parts.get(0).Z ? -U.random(125.) : 0;
     netSpeedY += Y < 0 ? U.random(125.) : 0;
     netSpeedY -= Y > Tornado.parts.get(Tornado.parts.size() - 1).Y ? U.random(125.) : 0;
     X += netSpeedX;
     Z += netSpeedZ;
     Y = Math.min(0, Y + netSpeedY);
    }
    double depth = U.getDepth(this);
    if (depth > -repairTorus.getRadius()) {
     setRepairTorusDetail(false);
     U.setTranslate(repairTorus, this);
     repairTorus.setVisible(true);
    } else {
     repairTorus.setVisible(false);
    }
    runRepairShocks(depth);
   }
   boolean showFoliageSphere = false, liveSphere = foliageSphere != null;
   if (U.getDepth(this) > -renderRadius) {
    if (checkpointNumber >= 0 && checkpointNumber == TE.currentCheckpoint) {
     boolean sideways = TE.isSidewaysXZ(XZ);
     checkpointSignRotation = sideways ? (XZ > 0 ? Camera.X < X : Camera.X > X) : Camera.Z > Z;//If checkpoint, XZ is never > Math.abs(90)
    }
    if (vehicleModel) {
     for (TrackPartPart part : parts) {
      part.runAsVehiclePart(renderALL);
     }
    } else {
     for (TrackPartPart part : parts) {
      part.runAsTrackPart(renderALL);
     }
    }
    showFoliageSphere = true;
    if (liveSphere) {
     foliageSphere.setCullFace(U.getDepth(X, Y - 425, Z) > foliageSphere.getRadius() ? CullFace.BACK : CullFace.NONE);
     U.setTranslate(foliageSphere, X, Y - 425, Z);
    }
   }
   if (liveSphere) {
    foliageSphere.setVisible(showFoliageSphere);
   }
   for (TrackPartPart part : parts) {
    part.MV.setVisible(part.visible);
    part.visible = false;
   }
   if (roadRocks != null) {
    for (RoadRock rock : roadRocks) {
     if ((U.render(rock))) {
      U.setTranslate(rock.S, rock);
      rock.S.setVisible(true);
     } else {
      rock.S.setVisible(false);
     }
    }
   }
  }
 }

 private void runRepairShocks(double depth) {
  double radius = repairTorus.getRadius();
  for (Cylinder shock : repairShocks) {
   if (depth > -radius) {
    U.rotate(shock, U.random(360.), TE.isSidewaysXZ(XZ) ? 0 : 90);//<-Reversed for some reason
    U.setTranslate(shock, this);
    shock.setVisible(true);
   } else {
    shock.setVisible(false);
   }
  }
 }

 private void setRepairTorusDetail(boolean firstLoad) {
  if (U.averageFPS < 30) {
   repairTorus.setRadius(700);
   repairTorus.setTubeRadius(125);
   repairTorus.setRadiusDivisions(4);
   repairTorus.setTubeDivisions(4);
  } else if (U.maxedFPS(true) || firstLoad) {
   repairTorus.setRadius(650);
   repairTorus.setTubeRadius(100);
   repairTorus.setRadiusDivisions(64);
   repairTorus.setTubeDivisions(64);
  }
 }
}
