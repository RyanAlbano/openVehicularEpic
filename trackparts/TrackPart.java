package ve.trackparts;

import java.io.*;
import java.util.*;

import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import org.fxyz3d.shapes.primitives.FrustumMesh;
import org.fxyz3d.shapes.Torus;
import ve.*;
import ve.environment.E;
import ve.utilities.*;

public class TrackPart extends Instance {

 private final List<TrackPartPiece> pieces = new ArrayList<>();
 private Sphere foliageSphere;
 private Torus fixRing;
 public boolean vehicleModel;
 public boolean isFixRing;
 private boolean scenery;
 public boolean checkpointPerpendicularToX;
 public boolean checkpointSignRotation;
 public long checkpointNumber = -1;
 public final List<TrackPlane> trackPlanes = new ArrayList<>();
 private FrustumMesh pile;

 class Rock extends Sphere {

  double X, Y, Z;

  Rock() {
   super((double) 1, 5);
  }
 }

 private List<Rock> rocks;

 private final List<Cylinder> fixShocks = new ArrayList<>();

 public TrackPart(int a, List<String> L, double sourceX, double sourceY, double sourceZ, double angle, boolean isVehicleModel) {
  vehicleModel = isVehicleModel;
  Set(a, L, sourceX, sourceY, sourceZ, angle);
 }

 public TrackPart(int a, List<String> L, double sourceX, double sourceY, double sourceZ, double angle, boolean isVehicleModel, double inSize, double[] inScale) {
  vehicleModel = isVehicleModel;
  instanceSize = inSize;
  instanceScale[0] = inScale[0];
  instanceScale[1] = inScale[1];
  instanceScale[2] = inScale[2];
  Set(a, L, sourceX, sourceY, sourceZ, angle);
 }

 private void Set(int a, List<String> L, double sourceX, double sourceY, double sourceZ, double angle) {
  modelNumber = a;
  if (a > 0 || vehicleModel) {
   double treeRandomXZ = U.randomPlusMinus(180.);
   theRandomColor[0] = U.random();
   theRandomColor[1] = U.random();
   theRandomColor[2] = U.random();
   int wheelCount = 0;
   boolean onModelPiece = false, onTrackPlane = false, addWheel = false;
   List<Double> xx = new ArrayList<>(), yy = new ArrayList<>(), zz = new ArrayList<>();
   double[] translate = new double[3], RGB = {0, 0, 0};
   String type = "", wheelType = "", textureType = "", wheelTextureType = "", rimType = "", s = "";
   try (BufferedReader BR = new BufferedReader(new InputStreamReader(getFile(L)))) {
    for (String s1; (s1 = BR.readLine()) != null; ) {
     s = "" + s1.trim();
     if (s.startsWith("<>") && (!s.contains("aerialOnly") || sourceY != 0)) {
      onModelPiece = true;
      addWheel = false;
      xx.clear();
      yy.clear();
      zz.clear();
      type = textureType = "";
     } else if (s.startsWith("><")) {
      double minimumX = Double.NEGATIVE_INFINITY, maximumX = Double.POSITIVE_INFINITY;
      for (double listX : xx) {
       minimumX = Math.max(minimumX, listX);
       maximumX = Math.min(maximumX, listX);
      }
      double averageX = (minimumX + maximumX) * .5;
      type += averageX > 0 ? " R " : averageX < 0 ? " L " : U.random() < .5 ? " R " : " L ";
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
      if (type.contains(" foliage ")) {
       foliageSphere = new Sphere(125, 9);
      } else if (xx.size() > 0 && !type.contains(" thrust")) {
       pieces.add(new TrackPartPiece(this, U.listToArray(xx), U.listToArray(yy), U.listToArray(zz), xx.size(), RGB, type, textureType));
       xx.clear();
      }
      onModelPiece = false;
     }
     getLoadColor(s, RGB);
     if (onModelPiece) {
      if (s.startsWith("(")) {
       xx.add((U.getValue(s, 0) * modelSize * instanceSize * modelScale[0] * instanceScale[0]) + translate[0]);
       yy.add((U.getValue(s, 1) * modelSize * instanceSize * modelScale[1] * instanceScale[1]) + translate[1]);
       zz.add((U.getValue(s, 2) * modelSize * instanceSize * modelScale[2] * instanceScale[2]) + translate[2]);
       if (!type.contains(" thrust ")) {
        int size = xx.size() - 1;
        addSizes(xx.get(size), yy.get(size), zz.get(size));
       }
      }
      if (xx.size() < 1) {
       textureType = s.startsWith("t(") ? U.getString(s, 0) : textureType;
       type += s.startsWith("cs") ? " fastCull" + (s.endsWith("B") ? "B" : s.endsWith("F") ? "F" : s.endsWith("R") ? "R" : s.endsWith("L") ? "L" : "") + " " : "";
       if (s.startsWith("lit")) {
        type += " light ";
        type += s.endsWith("fire") ? " fire " : "";
       }
       type += s.startsWith("reflect") ? " reflect " : "";
       type += s.startsWith("thrustWhite") ? " thrustWhite " : s.startsWith("thrustBlue") ? " thrustBlue " : s.startsWith("thrust") ? " thrust " : "";
       type += s.startsWith("selfIlluminate") ? " selfIlluminate " : "";
       type += s.startsWith("blink") ? " blink " : "";
       type += s.startsWith("noSpecular") ? " noSpecular " : s.startsWith("shiny") ? " shiny " : "";
       type += s.startsWith("noTexture") ? " noTexture " : "";
       type += s.startsWith("flick1") ? " flick1 " : "";
       type += s.startsWith("flick2") ? " flick2 " : "";
       type += s.startsWith("checkpointWord") ? " checkpointWord " : "";
       type += s.startsWith("lapWord") ? " lapWord " : "";
       if (s.startsWith("controller")) {
        type += " controller ";
        type += s.contains("XZ") ? " controllerXZ " : s.contains("XY") ? " controllerXY " : "";
       } else if (s.startsWith("wheel")) {
        type += " wheel ";
        addWheel = s.startsWith("wheelPoint") || addWheel;
       }
       type += s.startsWith("foliage") ? " foliage " : "";
       type += s.startsWith("line") ? " line " : "";
       type += s.startsWith("onlyAerial") ? " onlyAerial " : "";
       type += s.startsWith("conic") ? " conic " : "";
       type += s.startsWith("cylindric") ? " cylindric " : "";
       type += s.startsWith("strip") ? " strip " : "";
       type += s.startsWith("grid") ? " grid " : "";
       type += s.startsWith("triangles+1") ? " triangles+1 " : s.startsWith("triangles") ? " triangles " : "";
       type += s.startsWith("base") ? " base " : "";
      }
      computeTrackPlane(s, xx, yy, zz, RGB);
     }
     driverViewX = s.startsWith("driverViewX(") ? Math.abs(U.getValue(s, 0) * modelSize * instanceSize) + translate[0] : driverViewX;
     turretBaseY = s.startsWith("baseY(") ? U.getValue(s, 0) : turretBaseY;
     scenery = s.startsWith("scenery") || scenery;
     modelProperties += s.startsWith("tree") ? " tree " : "";
     if (s.startsWith("fixRing")) {
      isFixRing = true;
      fixRing = new Torus();
      setFixRing(true);
      PhongMaterial PM = new PhongMaterial();
      U.setDiffuseRGB(PM, E.terrainRGB[0], E.terrainRGB[1], E.terrainRGB[2]);
      PM.setDiffuseMap(U.getImage(VE.terrain.trim()));
      PM.setSpecularMap(U.getImage(VE.terrain.trim()));
      PM.setBumpMap(U.getImageNormal(VE.terrain.trim()));
      fixRing.setMaterial(PM);
      U.add(fixRing);
      fixRing.setRotationAxis(Rotate.Y_AXIS);
      fixRing.setRotate(angle);
     }
     modelProperties += s.startsWith("mapTerrain") ? " mapTerrain " : "";
     modelProperties += s.startsWith("rocky") ? " rocky " : "";
     getSizeScaleTranslate(s, translate);
     if (s.startsWith("wheelColor(")) {
      try {
       wheelRGB[0] = U.getValue(s, 0);
       wheelRGB[1] = U.getValue(s, 1);
       wheelRGB[2] = U.getValue(s, 2);
      } catch (Exception e) {
       wheelRGB[0] = wheelRGB[1] = wheelRGB[2] = .1875;
       if (s.contains("theRandomColor")) {
        wheelRGB[0] = theRandomColor[0];
        wheelRGB[1] = theRandomColor[1];
        wheelRGB[2] = theRandomColor[2];
        wheelType += " theRandomColor ";
       }
      }
      wheelType += s.contains("reflect") ? " reflect " : "";
      wheelType += s.contains("noSpecular") ? " noSpecular " : s.contains("shiny") ? " shiny " : "";
     } else if (s.startsWith("rims(")) {
      rimType = "";
      rimRadius = U.getValue(s, 0) * modelSize * instanceSize;
      rimDepth = Math.max(rimRadius * .0625, U.getValue(s, 1) * modelSize * instanceSize);
      try {
       rimRGB[0] = U.getValue(s, 2);
       rimRGB[1] = U.getValue(s, 3);
       rimRGB[2] = U.getValue(s, 4);
      } catch (Exception e) {
       rimRGB[0] = rimRGB[1] = rimRGB[2] = .5;
       if (s.contains("theRandomColor")) {
        rimRGB[0] = theRandomColor[0];
        rimRGB[1] = theRandomColor[1];
        rimRGB[2] = theRandomColor[2];
        rimType += " theRandomColor ";
       }
      }
      rimType += s.contains("reflect") ? " reflect " : "";
      rimType += s.contains("noSpecular") ? " noSpecular " : s.contains("shiny") ? " shiny " : "";
      rimType += s.contains("sport") ? " sport " : "";
     }
     wheelType = s.startsWith("landingGearWheels") ? " landingGear " : wheelType;
     wheelTextureType = s.startsWith("wheelTexture(") ? U.getString(s, 0) : wheelTextureType;
     wheelSmoothing = s.startsWith("smoothing(") ? U.getValue(s, 0) * modelSize : wheelSmoothing;
     if (s.startsWith("wheel(")) {
      String side = U.getValue(s, 0) > 0 ? " R " : U.getValue(s, 0) < 0 ? " L " : U.random() < .5 ? " R " : " L ";
      setWheel(U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2), U.getValue(s, 3), U.getValue(s, 4), wheelType + side, rimType, wheelTextureType, s.contains("steers"), s.contains("hide"));
      wheelCount++;
     } else if (s.startsWith("<t>") && (!s.contains("aerialOnly") || sourceY != 0)) {
      trackPlanes.add((new TrackPlane()));
      onTrackPlane = true;
      if (modelProperties.contains(" mapTerrain ")) {
       trackPlanes.get(trackPlanes.size() - 1).RGB[0] = E.groundRGB[0];
       trackPlanes.get(trackPlanes.size() - 1).RGB[1] = E.groundRGB[1];
       trackPlanes.get(trackPlanes.size() - 1).RGB[2] = E.groundRGB[2];
       trackPlanes.get(trackPlanes.size() - 1).type += VE.terrain;
      }
      trackPlanes.get(trackPlanes.size() - 1).damage = 1;
     } else if (s.startsWith(">t<")) {
      onTrackPlane = false;
     }
     if (onTrackPlane) {
      int index = trackPlanes.size() - 1;
      if (s.startsWith("RGB")) {
       trackPlanes.get(index).RGB[0] = U.getValue(s, 0);
       trackPlanes.get(index).RGB[1] = U.getValue(s, 1);
       trackPlanes.get(index).RGB[2] = U.getValue(s, 2);
      } else if (s.startsWith("pavedColor")) {
       trackPlanes.get(index).RGB[0] = trackPlanes.get(index).RGB[1] = trackPlanes.get(index).RGB[2] = VE.pavedRGB;
      } else if (s.startsWith("wall")) {
       trackPlanes.get(index).wall = s.contains("F") ? "F" : s.contains("B") ? "B" : s.contains("R") ? "R" : s.contains("L") ? "L" : "";
      }
      trackPlanes.get(index).XY = s.startsWith("xy") ? U.getValue(s, 0) : trackPlanes.get(index).XY;
      trackPlanes.get(index).YZ = s.startsWith("yz") ? U.getValue(s, 0) : trackPlanes.get(index).YZ;
      trackPlanes.get(index).radiusX = s.startsWith("spanX") ? U.getValue(s, 0) * modelSize * instanceSize * instanceScale[0] : trackPlanes.get(index).radiusX;
      trackPlanes.get(index).radiusZ = s.startsWith("spanZ") ? U.getValue(s, 0) * modelSize * instanceSize * instanceScale[2] : trackPlanes.get(index).radiusZ;
      trackPlanes.get(index).radiusY = s.startsWith("spanY") ? U.getValue(s, 0) * modelSize * instanceSize * instanceScale[1] : trackPlanes.get(index).radiusY;
      trackPlanes.get(index).radiusY = trackPlanes.get(index).radiusY < 0 ? Double.POSITIVE_INFINITY : trackPlanes.get(index).radiusY;
      trackPlanes.get(index).X = s.startsWith("tx(") ? U.getValue(s, 0) * modelSize * instanceSize * instanceScale[0] : trackPlanes.get(index).X;
      trackPlanes.get(index).Y = s.startsWith("ty(") ? U.getValue(s, 0) * modelSize * instanceSize * instanceScale[1] : trackPlanes.get(index).Y;
      trackPlanes.get(index).Z = s.startsWith("tz(") ? U.getValue(s, 0) * modelSize * instanceSize * instanceScale[2] : trackPlanes.get(index).Z;
      trackPlanes.get(index).type += s.startsWith("type") ? " " + U.getString(s, 0) + " " : "";
      trackPlanes.get(index).damage = s.startsWith("dmg") ? U.getValue(s, 0) : trackPlanes.get(index).damage;
     }
    }
   } catch (IOException e) {
    System.out.println("Model-Loading Error: " + e);
    System.out.println("at File: " + a);
    System.out.println("at Line: " + s);
   }
   maxMinusX[1] /= vertexQuantity;
   maxPlusX[1] /= vertexQuantity;
   maxMinusY[1] /= vertexQuantity;
   maxPlusY[1] /= vertexQuantity;
   maxMinusZ[1] /= vertexQuantity;
   maxPlusZ[1] /= vertexQuantity;
   absoluteRadius = Math.abs(maxMinusX[0]) + Math.abs(maxPlusX[0]) + Math.abs(maxMinusY[0]) + Math.abs(maxPlusY[0]) + Math.abs(maxMinusZ[0]) + Math.abs(maxPlusZ[0]) + Math.abs(maxMinusX[1]) + Math.abs(maxPlusX[1]) + Math.abs(maxMinusY[1]) + Math.abs(maxPlusY[1]) + Math.abs(maxMinusZ[1]) + Math.abs(maxPlusZ[1]);
   collisionRadius = absoluteRadius * .3;
   X = sourceX;
   Y = sourceY;
   Z = sourceZ;
   for (XZ = angle; XZ > 180; XZ -= 360) ;
   for (; XZ < -180; XZ += 360) ;
   for (TrackPartPiece piece : pieces) {
    if (piece.fastCull == piece.fastCull) {
     if (XZ > 45 && XZ < 135) {
      piece.fastCull = --piece.fastCull < -1 ? 2 : piece.fastCull;
     } else if (XZ < -45 && XZ > -135) {
      piece.fastCull = ++piece.fastCull > 2 ? -1 : piece.fastCull;
     } else if (Math.abs(XZ) > 135) {
      piece.fastCull = ++piece.fastCull > 2 ? -1 : piece.fastCull;
      piece.fastCull = ++piece.fastCull > 2 ? -1 : piece.fastCull;
     }
    }
    if (!vehicleModel) {
     piece.XZ = new Rotate();
     piece.MV.getTransforms().add(piece.XZ);
     piece.XZ.setAxis(Rotate.Y_AXIS);
     piece.XZ.setAngle(piece.tree ? treeRandomXZ : -XZ);
    }
    U.add(piece.MV);
   }
   if (foliageSphere != null) {
    PhongMaterial PM = new PhongMaterial();
    PM.setDiffuseMap(U.getImage("foliage"));
    PM.setBumpMap(U.getImageNormal("foliage"));
    U.getRGB.setFill(Color.color(.125, .125, .125));
    U.getRGB.fillRect(0, 0, 1, 1);
    PM.setSelfIlluminationMap(U.getRGBCanvas.snapshot(null, null));
    U.setDiffuseRGB(PM, 1, 1, 1);
    U.setSpecularRGB(PM, 0, 0, 0);
    foliageSphere.setMaterial(PM);
    U.add(foliageSphere);
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
    if (!trackPlane.wall.isEmpty()) {
     if (Math.abs(XZ) > 135) {
      trackPlane.wall = trackPlane.wall.equals("F") ? "B" : trackPlane.wall.equals("L") ? "R" : trackPlane.wall.equals("B") ? "F" : trackPlane.wall.equals("R") ? "L" : "";
     } else if (XZ >= 45) {
      trackPlane.wall = trackPlane.wall.equals("F") ? "L" : trackPlane.wall.equals("L") ? "B" : trackPlane.wall.equals("B") ? "R" : trackPlane.wall.equals("R") ? "F" : "";
     } else if (XZ <= -45) {
      trackPlane.wall = trackPlane.wall.equals("F") ? "R" : trackPlane.wall.equals("R") ? "B" : trackPlane.wall.equals("B") ? "L" : trackPlane.wall.equals("L") ? "F" : "";
     }
    }
   }
   if (modelProperties.contains(" rocky ")) {
    rocks = new ArrayList<>();
    for (int n = 50; --n >= 0; ) {
     Rock rock = new Rock();
     rock.setScaleX(100 + U.random(200.));
     rock.setScaleY(U.random(25.));
     rock.setScaleZ(100 + U.random(200.));
     U.rotate(rock, 0, U.random(360.));
     rock.X = X + U.randomPlusMinus(Math.max(850., 3000 * U.sin(XZ)));
     rock.Y = Y;
     rock.Z = Z + U.randomPlusMinus(Math.max(850., 3000 * U.cos(XZ)));
     PhongMaterial PM = new PhongMaterial();
     rock.setMaterial(PM);
     PM.setDiffuseMap(U.getImage("rock"));
     PM.setSpecularMap(U.getImage("rock"));
     PM.setBumpMap(U.getImageNormal("rock"));
     U.add(rock);
     rocks.add(rock);
    }
   }
   if (isFixRing) {
    PhongMaterial PM = new PhongMaterial();
    U.setSpecularRGB(PM, 0, 0, 0);
    PM.setSelfIlluminationMap(U.getImage("white"));
    for (int n = 0; n < 8; n++) {
     fixShocks.add(new Cylinder(Math.round(U.random(5.)) + 1, 1100, 4));
     fixShocks.get(n).setMaterial(PM);
     U.add(fixShocks.get(n));
     fixShocks.get(n).setVisible(false);
    }
   }
   Quaternion baseXZ = new Quaternion(0, U.sin(XZ * .5), 0, U.cos(XZ * .5)),
   baseYZ = new Quaternion(-U.sin(YZ * .5), 0, 0, U.cos(YZ * .5)),
   baseXY = new Quaternion(0, 0, -U.sin(XY * .5), U.cos(XY * .5));
   rotation = baseXY.multiply(baseYZ).multiply(baseXZ);
  }
 }

 public TrackPart() {
  pile = new FrustumMesh(100 + U.random(400.), U.random(100.), U.random(200.), 0);
  X = U.randomPlusMinus(800000.);
  Z = U.randomPlusMinus(800000.);
  scenery = true;
  renderRadius = Math.max(pile.getMajorRadius(), Math.max(pile.getMinorRadius(), pile.getHeight()));
  PhongMaterial PM = new PhongMaterial();
  U.setDiffuseRGB(PM, E.terrainRGB[0], E.terrainRGB[1], E.terrainRGB[2]);
  U.setSpecularRGB(PM, E.terrainRGB[0], E.terrainRGB[1], E.terrainRGB[2]);
  PM.setDiffuseMap(U.getImage(VE.terrain.trim()));
  PM.setSpecularMap(U.getImage(VE.terrain.trim()));
  PM.setBumpMap(U.getImageNormal(VE.terrain.trim()));
  pile.setMaterial(PM);
  U.add(pile);
  double averageRadius = (pile.getMinorRadius() + pile.getMajorRadius()) * .5, radiusDifference = Math.abs(pile.getMinorRadius() - pile.getMajorRadius());
  for (int n = 0; n < 4; n++) {
   trackPlanes.add(new TrackPlane());
   trackPlanes.get(n).Y = -pile.getHeight() * .5;
   trackPlanes.get(n).radiusY = pile.getHeight();
   if (n == 0) {
    trackPlanes.get(n).Z = averageRadius;
    trackPlanes.get(n).radiusX = averageRadius;
    trackPlanes.get(n).radiusZ = radiusDifference * .5;
    trackPlanes.get(n).YZ = Math.min(Math.round(Math.atan(trackPlanes.get(n).radiusY / radiusDifference) * 57.295779513082320876798154814092), 90);
   } else if (n == 1) {
    trackPlanes.get(n).X = averageRadius;
    trackPlanes.get(n).radiusZ = averageRadius;
    trackPlanes.get(n).radiusX = radiusDifference * .5;
    trackPlanes.get(n).XY = Math.min(Math.round(Math.atan(trackPlanes.get(n).radiusY / radiusDifference) * 57.295779513082320876798154814092), 90);
   } else if (n == 2) {
    trackPlanes.get(n).Z = -averageRadius;
    trackPlanes.get(n).radiusX = averageRadius;
    trackPlanes.get(n).radiusZ = radiusDifference * .5;
    trackPlanes.get(n).YZ = -Math.min(Math.round(Math.atan(trackPlanes.get(n).radiusY / radiusDifference) * 57.295779513082320876798154814092), 90);
   } else {
    trackPlanes.get(n).X = -averageRadius;
    trackPlanes.get(n).radiusZ = averageRadius;
    trackPlanes.get(n).radiusX = radiusDifference * .5;
    trackPlanes.get(n).XY = -Math.min(Math.round(Math.atan(trackPlanes.get(n).radiusY / radiusDifference) * 57.295779513082320876798154814092), 90);
   }
   trackPlanes.get(n).RGB[0] = E.groundRGB[0];
   trackPlanes.get(n).RGB[1] = E.groundRGB[1];
   trackPlanes.get(n).RGB[2] = E.groundRGB[2];
   trackPlanes.get(n).type = VE.terrain;
   trackPlanes.get(n).damage = 1;
  }
  trackPlanes.add(new TrackPlane());
  trackPlanes.get(4).Y = -pile.getHeight();
  trackPlanes.get(4).radiusY = pile.getHeight();
  trackPlanes.get(4).radiusX = trackPlanes.get(4).radiusZ = pile.getMinorRadius();
  trackPlanes.get(4).RGB[0] = E.groundRGB[0];
  trackPlanes.get(4).RGB[1] = E.groundRGB[1];
  trackPlanes.get(4).RGB[2] = E.groundRGB[2];
  trackPlanes.get(4).type = VE.terrain + " maxbounce ";
  trackPlanes.get(4).damage = 1;
 }

 private void computeTrackPlane(String s, List<Double> xx, List<Double> yy, List<Double> zz, double[] RGB) {//Keep as a void?
  if (s.startsWith("track(")) {
   trackPlanes.add((new TrackPlane()));
   int index = trackPlanes.size() - 1;
   if (modelProperties.contains(" mapTerrain ")) {
    trackPlanes.get(index).RGB[0] = E.groundRGB[0];
    trackPlanes.get(index).RGB[1] = E.groundRGB[1];
    trackPlanes.get(index).RGB[2] = E.groundRGB[2];
   } else {
    trackPlanes.get(index).RGB[0] = RGB[0];
    trackPlanes.get(index).RGB[1] = RGB[1];
    trackPlanes.get(index).RGB[2] = RGB[2];
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
   trackPlanes.get(index).X = averageX / xx.size();
   trackPlanes.get(index).Y = averageY / yy.size();
   trackPlanes.get(index).Z = averageZ / zz.size();
   trackPlanes.get(index).radiusX = Math.abs(rangeNegativeX - rangePositiveX) * .5;
   trackPlanes.get(index).radiusY = Math.abs(rangeNegativeY - rangePositiveY) * .5;
   trackPlanes.get(index).radiusZ = Math.abs(rangeNegativeZ - rangePositiveZ) * .5;
   trackPlanes.get(index).YZ = s.contains("getYZ") ? Math.atan(trackPlanes.get(index).radiusY / trackPlanes.get(index).radiusZ) * 57.295779513082320876798154814092 * (s.contains("-getYZ") ? 1 : -1) : trackPlanes.get(index).YZ;
   trackPlanes.get(index).XY = s.contains("getXY") ? Math.atan(trackPlanes.get(index).radiusY / trackPlanes.get(index).radiusX) * 57.295779513082320876798154814092 * (s.contains("-getXY") ? 1 : -1) : trackPlanes.get(index).XY;
   trackPlanes.get(index).wall = s.contains("wallF") ? "F" : s.contains("wallB") ? "B" : s.contains("wallR") ? "R" : s.contains("wallL") ? "L" : trackPlanes.get(index).wall;
   trackPlanes.get(index).damage = 1;
   if (s.contains("useLargerRadius")) {
    if (s.contains("getYZ")) {
     double largerRadius = Math.max(trackPlanes.get(index).radiusY, trackPlanes.get(index).radiusZ);
     trackPlanes.get(index).radiusY = trackPlanes.get(index).radiusZ = largerRadius;
    }
    if (s.contains("getXY")) {
     double largerRadius = Math.max(trackPlanes.get(index).radiusX, trackPlanes.get(index).radiusY);
     trackPlanes.get(index).radiusX = trackPlanes.get(index).radiusY = largerRadius;
    }
   }
   trackPlanes.get(index).addSpeed = true;
   try {
    trackPlanes.get(trackPlanes.size() - 1).type = " " + U.getString(s, 0) + " ";
    trackPlanes.get(trackPlanes.size() - 1).damage = U.getValue(s, 1);
   } catch (Exception ignored) {
   }
  }
 }

 public void processGraphics() {
  instanceToCameraDistance = U.distance(X, VE.cameraX, Y, VE.cameraY, Z, VE.cameraZ);
  boolean showFoliageSphere = false, nullSphere = foliageSphere == null;
  if (modelProperties.contains("rainbow")) {
   for (TrackPartPiece piece : pieces) {
    U.setTranslate(piece.MV, VE.cameraX + X, VE.cameraY + Y, VE.cameraZ + Z);
    piece.MV.setVisible(true);
   }
  } else {
   if (scenery) {
    if (Math.abs(X - VE.cameraX) > 40000) {
     for (; X > VE.cameraX + 40000; X -= 80000) ;
     for (; X < VE.cameraX - 40000; X += 80000) ;
     Y = U.distance(X, E.poolX, Z, E.poolZ) < E.pool[0].getRadius() ? E.poolDepth : 0;
    }
    if (Math.abs(Z - VE.cameraZ) > 40000) {
     for (; Z > VE.cameraZ + 40000; Z -= 80000) ;
     for (; Z < VE.cameraZ - 40000; Z += 80000) ;
     Y = U.distance(X, E.poolX, Z, E.poolZ) < E.pool[0].getRadius() ? E.poolDepth : 0;
    }
    if (pile != null) {
     U.render(pile, X, -pile.getHeight() * .5 + (E.poolExists && U.distance(X, E.poolX, Z, E.poolZ) < E.pool[0].getRadius() ? E.poolDepth : 0), Z, -renderRadius);
    }
   }
   if (isFixRing) {
    if (E.tornadoParts.size() > 0 && E.tornadoMovesFixpoints) {
     netSpeedX *= .995;
     netSpeedY *= .995;
     netSpeedZ *= .995;
     netSpeedX += X < E.tornadoParts.get(0).X ? U.random(125.) : X > E.tornadoParts.get(0).X ? -U.random(125.) : 0;
     netSpeedZ += Z < E.tornadoParts.get(0).Z ? U.random(125.) : Z > E.tornadoParts.get(0).Z ? -U.random(125.) : 0;
     netSpeedY += Y < 0 ? U.random(125.) : 0;
     netSpeedY -= Y > E.tornadoParts.get(E.tornadoParts.size() - 1).Y ? U.random(125.) : 0;
     X += netSpeedX;
     Z += netSpeedZ;
     Y = Math.min(0, Y + netSpeedY);
    }
    double depth = U.getDepth(X, Y, Z);
    if (depth > -fixRing.getRadius()) {
     setFixRing(false);
     U.setTranslate(fixRing, X, Y, Z);
     fixRing.setVisible(true);
    } else {
     fixRing.setVisible(false);
    }
    manageFixShocks(depth);
   }
   if (U.getDepth(X, Y, Z) > -renderRadius) {
    if (checkpointNumber >= 0 && checkpointNumber == VE.currentCheckpoint) {
     checkpointSignRotation = ((!checkpointPerpendicularToX && VE.cameraZ > Z) || (checkpointPerpendicularToX && VE.cameraX < X)) || checkpointSignRotation;
     checkpointSignRotation = (checkpointPerpendicularToX || !(VE.cameraZ < Z)) && (!checkpointPerpendicularToX || !(VE.cameraX > X)) && checkpointSignRotation;
    }
    if (vehicleModel) {
     for (TrackPartPiece piece : pieces) {
      piece.processAsVehiclePart();
     }
    } else {
     for (TrackPartPiece piece : pieces) {
      piece.processAsTrackPart();
     }
    }
    showFoliageSphere = true;
    if (!nullSphere) {
     foliageSphere.setCullFace(U.getDepth(X, -425, Z) > foliageSphere.getRadius() ? CullFace.BACK : CullFace.NONE);
     U.setTranslate(foliageSphere, X, Y - 425, Z);
    }
   }
   if (!nullSphere) {
    foliageSphere.setVisible(showFoliageSphere);
   }
   for (TrackPartPiece piece : pieces) {
    piece.MV.setVisible(piece.visible);
    piece.visible = false;
   }
   if (rocks != null) {
    for (Rock rock : rocks) {
     U.render(rock, rock.X, rock.Y, rock.Z);
    }
   }
  }
 }

 private void manageFixShocks(double depth) {
  double radius = fixRing.getRadius();
  for (Cylinder fixShock : fixShocks) {
   if (depth > -radius) {
    U.rotate(fixShock, U.random(360.), Math.abs(XZ) > 45 ? 0 : 90);
    U.setTranslate(fixShock, X, Y, Z);
    fixShock.setVisible(true);
   } else {
    fixShock.setVisible(false);
   }
  }
 }

 private void setWheel(double sourceX, double sourceY, double sourceZ, double i_wheelThickness, double i_wheelRadius, String type, String m_rimType, String textureType, boolean i_steers, boolean hide) {
  sourceX *= modelSize * modelScale[0];
  sourceY *= modelSize * modelScale[1];
  sourceZ *= modelSize * modelScale[2];
  i_wheelThickness *= modelSize * modelScale[0];
  i_wheelRadius *= modelSize;
  wheelSmoothing *= -1;
  double[] x0 = new double[96], y0 = new double[96], z0 = new double[96];
  String steers = i_steers ? " steerXZ steerFromXZ " : "";
  clearanceY = Math.max(clearanceY, sourceY + i_wheelRadius);
  int n;
  if (i_wheelRadius != 0 && !hide) {
   double wheelThickness = i_wheelThickness + wheelSmoothing, wheelRadius = i_wheelRadius - Math.abs(wheelSmoothing);
   for (n = x0.length; --n >= 0; ) {
    x0[n] = sourceX - (n < 24 ? i_wheelThickness : -i_wheelThickness);
   }
   z0[0] = sourceZ + U.sin(0) * wheelRadius;
   z0[1] = sourceZ + U.sin(15) * wheelRadius;
   z0[2] = sourceZ + U.sin(30) * wheelRadius;
   z0[3] = sourceZ + U.sin(45) * wheelRadius;
   z0[4] = sourceZ + U.sin(60) * wheelRadius;
   z0[5] = sourceZ + U.sin(75) * wheelRadius;
   z0[6] = sourceZ + U.sin(90) * wheelRadius;
   z0[7] = sourceZ + U.sin(105) * wheelRadius;
   z0[8] = sourceZ + U.sin(120) * wheelRadius;
   z0[9] = sourceZ + U.sin(135) * wheelRadius;
   z0[10] = sourceZ + U.sin(150) * wheelRadius;
   z0[11] = sourceZ + U.sin(165) * wheelRadius;
   z0[12] = sourceZ + U.sin(180) * wheelRadius;
   z0[13] = sourceZ + U.sin(195) * wheelRadius;
   z0[14] = sourceZ + U.sin(210) * wheelRadius;
   z0[15] = sourceZ + U.sin(225) * wheelRadius;
   z0[16] = sourceZ + U.sin(240) * wheelRadius;
   z0[17] = sourceZ + U.sin(255) * wheelRadius;
   z0[18] = sourceZ + U.sin(270) * wheelRadius;
   z0[19] = sourceZ + U.sin(285) * wheelRadius;
   z0[20] = sourceZ + U.sin(300) * wheelRadius;
   z0[21] = sourceZ + U.sin(315) * wheelRadius;
   z0[22] = sourceZ + U.sin(330) * wheelRadius;
   z0[23] = sourceZ + U.sin(345) * wheelRadius;
   y0[0] = sourceY + U.cos(0) * wheelRadius;
   y0[1] = sourceY + U.cos(15) * wheelRadius;
   y0[2] = sourceY + U.cos(30) * wheelRadius;
   y0[3] = sourceY + U.cos(45) * wheelRadius;
   y0[4] = sourceY + U.cos(60) * wheelRadius;
   y0[5] = sourceY + U.cos(75) * wheelRadius;
   y0[6] = sourceY + U.cos(90) * wheelRadius;
   y0[7] = sourceY + U.cos(105) * wheelRadius;
   y0[8] = sourceY + U.cos(120) * wheelRadius;
   y0[9] = sourceY + U.cos(135) * wheelRadius;
   y0[10] = sourceY + U.cos(150) * wheelRadius;
   y0[11] = sourceY + U.cos(165) * wheelRadius;
   y0[12] = sourceY + U.cos(180) * wheelRadius;
   y0[13] = sourceY + U.cos(195) * wheelRadius;
   y0[14] = sourceY + U.cos(210) * wheelRadius;
   y0[15] = sourceY + U.cos(225) * wheelRadius;
   y0[16] = sourceY + U.cos(240) * wheelRadius;
   y0[17] = sourceY + U.cos(255) * wheelRadius;
   y0[18] = sourceY + U.cos(270) * wheelRadius;
   y0[19] = sourceY + U.cos(285) * wheelRadius;
   y0[20] = sourceY + U.cos(300) * wheelRadius;
   y0[21] = sourceY + U.cos(315) * wheelRadius;
   y0[22] = sourceY + U.cos(330) * wheelRadius;
   y0[23] = sourceY + U.cos(345) * wheelRadius;
   for (n = 24; --n >= 0; ) {
    z0[n + 24] = z0[n];
    y0[n + 24] = y0[n];
   }
   for (n = 48; --n >= 0; ) {
    maxMinusX[0] = Math.min(maxMinusX[0], x0[n]);
    maxPlusX[0] = Math.max(maxPlusX[0], x0[n]);
    maxMinusY[0] = Math.min(maxMinusY[0], y0[n]);
    maxPlusY[0] = Math.max(maxPlusY[0], y0[n]);
    maxMinusZ[0] = Math.min(maxMinusZ[0], z0[n]);
    maxPlusZ[0] = Math.max(maxPlusZ[0], z0[n]);
    maxMinusX[1] += x0[n] < 0 ? x0[n] : 0;
    maxPlusX[1] += x0[n] > 0 ? x0[n] : 0;
    maxMinusY[1] += x0[n] < 0 ? y0[n] : 0;
    maxPlusY[1] += x0[n] > 0 ? y0[n] : 0;
    maxMinusZ[1] += x0[n] < 0 ? z0[n] : 0;
    maxPlusZ[1] += x0[n] > 0 ? z0[n] : 0;
   }
   pieces.add(new TrackPartPiece(this, x0, y0, z0, 48, wheelRGB, type + " wheel wheelFaces " + steers, textureType));//^Wheel Plates
   if (rimRadius > 0) {
    if (i_wheelThickness != 0) {
     x0[0] += i_wheelThickness < 0 ? rimDepth : -rimDepth;
    }
    if (m_rimType.contains(" sport ")) {
     double smallRimRadius = rimRadius * .125;
     for (n = x0.length; --n > 0; ) {
      x0[n] = sourceX - i_wheelThickness;
     }
     x0[16] = sourceX + i_wheelThickness;
     if (i_wheelThickness > 0) {
      x0[3] -= rimDepth;
      x0[6] -= rimDepth;
      x0[9] -= rimDepth;
      x0[12] -= rimDepth;
      x0[15] -= rimDepth;
     } else if (i_wheelThickness < 0) {
      x0[3] += rimDepth;
      x0[6] += rimDepth;
      x0[9] += rimDepth;
      x0[12] += rimDepth;
      x0[15] += rimDepth;
     }
     y0[0] = sourceY;
     z0[0] = z0[9] = z0[16] = sourceZ;
     y0[1] = y0[2] = sourceY - rimRadius * U.cos(5);
     z0[1] = sourceZ - rimRadius * U.sin(5);
     z0[2] = sourceZ + rimRadius * U.sin(5);
     y0[3] = y0[15] = sourceY - smallRimRadius * U.cos(36);
     z0[3] = sourceZ + smallRimRadius * U.sin(36);
     y0[4] = y0[14] = sourceY - rimRadius * U.cos(67);
     z0[4] = sourceZ + rimRadius * U.sin(67);
     y0[5] = y0[13] = sourceY - rimRadius * U.cos(77);
     z0[5] = sourceZ + rimRadius * U.sin(77);
     y0[6] = y0[12] = sourceY + smallRimRadius * -U.cos(108);
     z0[6] = sourceZ + smallRimRadius * U.sin(108);
     y0[7] = y0[11] = sourceY + rimRadius * -U.cos(139);
     z0[7] = sourceZ + rimRadius * U.sin(139);
     y0[8] = y0[10] = sourceY + rimRadius * -U.cos(149);
     z0[8] = sourceZ + rimRadius * U.sin(149);
     y0[9] = sourceY + smallRimRadius;
     z0[10] = sourceZ - rimRadius * U.sin(149);
     z0[11] = sourceZ - rimRadius * U.sin(139);
     z0[12] = sourceZ - smallRimRadius * U.sin(108);
     z0[13] = sourceZ - rimRadius * U.sin(77);
     z0[14] = sourceZ - rimRadius * U.sin(67);
     z0[15] = sourceZ - smallRimRadius * U.sin(36);
     y0[16] = sourceY + rimRadius * U.cos(5);
     pieces.add(new TrackPartPiece(this, x0, y0, z0, 17, rimRGB, type + m_rimType + " wheel sportRimFaces " + steers, textureType));//Sport rim
     for (n = x0.length; --n >= 0; ) {
      x0[n] = sourceX - (n < 48 ? i_wheelThickness : -i_wheelThickness);
      x0[n] *= 1.001;
     }
     z0[0] = sourceZ + U.sin(0) * rimRadius;
     z0[1] = sourceZ + U.sin(15) * rimRadius;
     z0[2] = sourceZ + U.sin(30) * rimRadius;
     z0[3] = sourceZ + U.sin(45) * rimRadius;
     z0[4] = sourceZ + U.sin(60) * rimRadius;
     z0[5] = sourceZ + U.sin(75) * rimRadius;
     z0[6] = sourceZ + U.sin(90) * rimRadius;
     z0[7] = sourceZ + U.sin(105) * rimRadius;
     z0[8] = sourceZ + U.sin(120) * rimRadius;
     z0[9] = sourceZ + U.sin(135) * rimRadius;
     z0[10] = sourceZ + U.sin(150) * rimRadius;
     z0[11] = sourceZ + U.sin(165) * rimRadius;
     z0[12] = sourceZ + U.sin(180) * rimRadius;
     z0[13] = sourceZ + U.sin(195) * rimRadius;
     z0[14] = sourceZ + U.sin(210) * rimRadius;
     z0[15] = sourceZ + U.sin(225) * rimRadius;
     z0[16] = sourceZ + U.sin(240) * rimRadius;
     z0[17] = sourceZ + U.sin(255) * rimRadius;
     z0[18] = sourceZ + U.sin(270) * rimRadius;
     z0[19] = sourceZ + U.sin(285) * rimRadius;
     z0[20] = sourceZ + U.sin(300) * rimRadius;
     z0[21] = sourceZ + U.sin(315) * rimRadius;
     z0[22] = sourceZ + U.sin(330) * rimRadius;
     z0[23] = sourceZ + U.sin(345) * rimRadius;
     y0[0] = sourceY + U.cos(0) * rimRadius;
     y0[1] = sourceY + U.cos(15) * rimRadius;
     y0[2] = sourceY + U.cos(30) * rimRadius;
     y0[3] = sourceY + U.cos(45) * rimRadius;
     y0[4] = sourceY + U.cos(60) * rimRadius;
     y0[5] = sourceY + U.cos(75) * rimRadius;
     y0[6] = sourceY + U.cos(90) * rimRadius;
     y0[7] = sourceY + U.cos(105) * rimRadius;
     y0[8] = sourceY + U.cos(120) * rimRadius;
     y0[9] = sourceY + U.cos(135) * rimRadius;
     y0[10] = sourceY + U.cos(150) * rimRadius;
     y0[11] = sourceY + U.cos(165) * rimRadius;
     y0[12] = sourceY + U.cos(180) * rimRadius;
     y0[13] = sourceY + U.cos(195) * rimRadius;
     y0[14] = sourceY + U.cos(210) * rimRadius;
     y0[15] = sourceY + U.cos(225) * rimRadius;
     y0[16] = sourceY + U.cos(240) * rimRadius;
     y0[17] = sourceY + U.cos(255) * rimRadius;
     y0[18] = sourceY + U.cos(270) * rimRadius;
     y0[19] = sourceY + U.cos(285) * rimRadius;
     y0[20] = sourceY + U.cos(300) * rimRadius;
     y0[21] = sourceY + U.cos(315) * rimRadius;
     y0[22] = sourceY + U.cos(330) * rimRadius;
     y0[23] = sourceY + U.cos(345) * rimRadius;
     smallRimRadius = rimRadius * .875;
     z0[0 + 24] = sourceZ + U.sin(0) * smallRimRadius;
     z0[1 + 24] = sourceZ + U.sin(15) * smallRimRadius;
     z0[2 + 24] = sourceZ + U.sin(30) * smallRimRadius;
     z0[3 + 24] = sourceZ + U.sin(45) * smallRimRadius;
     z0[4 + 24] = sourceZ + U.sin(60) * smallRimRadius;
     z0[5 + 24] = sourceZ + U.sin(75) * smallRimRadius;
     z0[6 + 24] = sourceZ + U.sin(90) * smallRimRadius;
     z0[7 + 24] = sourceZ + U.sin(105) * smallRimRadius;
     z0[8 + 24] = sourceZ + U.sin(120) * smallRimRadius;
     z0[9 + 24] = sourceZ + U.sin(135) * smallRimRadius;
     z0[10 + 24] = sourceZ + U.sin(150) * smallRimRadius;
     z0[11 + 24] = sourceZ + U.sin(165) * smallRimRadius;
     z0[12 + 24] = sourceZ + U.sin(180) * smallRimRadius;
     z0[13 + 24] = sourceZ + U.sin(195) * smallRimRadius;
     z0[14 + 24] = sourceZ + U.sin(210) * smallRimRadius;
     z0[15 + 24] = sourceZ + U.sin(225) * smallRimRadius;
     z0[16 + 24] = sourceZ + U.sin(240) * smallRimRadius;
     z0[17 + 24] = sourceZ + U.sin(255) * smallRimRadius;
     z0[18 + 24] = sourceZ + U.sin(270) * smallRimRadius;
     z0[19 + 24] = sourceZ + U.sin(285) * smallRimRadius;
     z0[20 + 24] = sourceZ + U.sin(300) * smallRimRadius;
     z0[21 + 24] = sourceZ + U.sin(315) * smallRimRadius;
     z0[22 + 24] = sourceZ + U.sin(330) * smallRimRadius;
     z0[23 + 24] = sourceZ + U.sin(345) * smallRimRadius;
     y0[0 + 24] = sourceY + U.cos(0) * smallRimRadius;
     y0[1 + 24] = sourceY + U.cos(15) * smallRimRadius;
     y0[2 + 24] = sourceY + U.cos(30) * smallRimRadius;
     y0[3 + 24] = sourceY + U.cos(45) * smallRimRadius;
     y0[4 + 24] = sourceY + U.cos(60) * smallRimRadius;
     y0[5 + 24] = sourceY + U.cos(75) * smallRimRadius;
     y0[6 + 24] = sourceY + U.cos(90) * smallRimRadius;
     y0[7 + 24] = sourceY + U.cos(105) * smallRimRadius;
     y0[8 + 24] = sourceY + U.cos(120) * smallRimRadius;
     y0[9 + 24] = sourceY + U.cos(135) * smallRimRadius;
     y0[10 + 24] = sourceY + U.cos(150) * smallRimRadius;
     y0[11 + 24] = sourceY + U.cos(165) * smallRimRadius;
     y0[12 + 24] = sourceY + U.cos(180) * smallRimRadius;
     y0[13 + 24] = sourceY + U.cos(195) * smallRimRadius;
     y0[14 + 24] = sourceY + U.cos(210) * smallRimRadius;
     y0[15 + 24] = sourceY + U.cos(225) * smallRimRadius;
     y0[16 + 24] = sourceY + U.cos(240) * smallRimRadius;
     y0[17 + 24] = sourceY + U.cos(255) * smallRimRadius;
     y0[18 + 24] = sourceY + U.cos(270) * smallRimRadius;
     y0[19 + 24] = sourceY + U.cos(285) * smallRimRadius;
     y0[20 + 24] = sourceY + U.cos(300) * smallRimRadius;
     y0[21 + 24] = sourceY + U.cos(315) * smallRimRadius;
     y0[22 + 24] = sourceY + U.cos(330) * smallRimRadius;
     y0[23 + 24] = sourceY + U.cos(345) * smallRimRadius;
     for (n = 48; --n >= 0; ) {
      z0[n + 48] = z0[n];
      y0[n + 48] = y0[n];
     }
     pieces.add(new TrackPartPiece(this, x0, y0, z0, 96, rimRGB, type + m_rimType + " wheel wheelRingFaces " + steers, textureType));//Sport rim ring
    } else {
     double hexagonAngle1 = 0.86602540378443864676372317075294, hexagonAngle2 = .5;
     y0[0] = y0[1] = y0[4] = y0[7] = sourceY;
     z0[0] = z0[7] = sourceZ;
     z0[1] = sourceZ + rimRadius;
     z0[4] = sourceZ - rimRadius;
     y0[2] = y0[3] = sourceY + hexagonAngle1 * rimRadius;
     z0[2] = z0[6] = sourceZ + hexagonAngle2 * rimRadius;
     z0[3] = z0[5] = sourceZ - hexagonAngle2 * rimRadius;
     y0[5] = y0[6] = sourceY - hexagonAngle1 * rimRadius;
     if (i_wheelThickness != 0) {
      x0[7] = sourceX + i_wheelThickness;
      x0[7] -= i_wheelThickness < 0 ? rimDepth : -rimDepth;
     }
     pieces.add(new TrackPartPiece(this, x0, y0, z0, 8, rimRGB, type + m_rimType + " wheel rimFaces " + steers, textureType));//Normal rim
    }
   }
   if (Math.abs(i_wheelThickness) > 0) {
    for (n = 24; --n >= 0; ) {
     x0[n] = sourceX - wheelThickness;
     x0[n + 24] = sourceX + wheelThickness;
    }
    z0[0] = sourceZ + U.sin(0) * i_wheelRadius;
    z0[1] = sourceZ + U.sin(15) * i_wheelRadius;
    z0[2] = sourceZ + U.sin(30) * i_wheelRadius;
    z0[3] = sourceZ + U.sin(45) * i_wheelRadius;
    z0[4] = sourceZ + U.sin(60) * i_wheelRadius;
    z0[5] = sourceZ + U.sin(75) * i_wheelRadius;
    z0[6] = sourceZ + U.sin(90) * i_wheelRadius;
    z0[7] = sourceZ + U.sin(105) * i_wheelRadius;
    z0[8] = sourceZ + U.sin(120) * i_wheelRadius;
    z0[9] = sourceZ + U.sin(135) * i_wheelRadius;
    z0[10] = sourceZ + U.sin(150) * i_wheelRadius;
    z0[11] = sourceZ + U.sin(165) * i_wheelRadius;
    z0[12] = sourceZ + U.sin(180) * i_wheelRadius;
    z0[13] = sourceZ + U.sin(195) * i_wheelRadius;
    z0[14] = sourceZ + U.sin(210) * i_wheelRadius;
    z0[15] = sourceZ + U.sin(225) * i_wheelRadius;
    z0[16] = sourceZ + U.sin(240) * i_wheelRadius;
    z0[17] = sourceZ + U.sin(255) * i_wheelRadius;
    z0[18] = sourceZ + U.sin(270) * i_wheelRadius;
    z0[19] = sourceZ + U.sin(285) * i_wheelRadius;
    z0[20] = sourceZ + U.sin(300) * i_wheelRadius;
    z0[21] = sourceZ + U.sin(315) * i_wheelRadius;
    z0[22] = sourceZ + U.sin(330) * i_wheelRadius;
    z0[23] = sourceZ + U.sin(345) * i_wheelRadius;
    y0[0] = sourceY + U.cos(0) * i_wheelRadius;
    y0[1] = sourceY + U.cos(15) * i_wheelRadius;
    y0[2] = sourceY + U.cos(30) * i_wheelRadius;
    y0[3] = sourceY + U.cos(45) * i_wheelRadius;
    y0[4] = sourceY + U.cos(60) * i_wheelRadius;
    y0[5] = sourceY + U.cos(75) * i_wheelRadius;
    y0[6] = sourceY + U.cos(90) * i_wheelRadius;
    y0[7] = sourceY + U.cos(105) * i_wheelRadius;
    y0[8] = sourceY + U.cos(120) * i_wheelRadius;
    y0[9] = sourceY + U.cos(135) * i_wheelRadius;
    y0[10] = sourceY + U.cos(150) * i_wheelRadius;
    y0[11] = sourceY + U.cos(165) * i_wheelRadius;
    y0[12] = sourceY + U.cos(180) * i_wheelRadius;
    y0[13] = sourceY + U.cos(195) * i_wheelRadius;
    y0[14] = sourceY + U.cos(210) * i_wheelRadius;
    y0[15] = sourceY + U.cos(225) * i_wheelRadius;
    y0[16] = sourceY + U.cos(240) * i_wheelRadius;
    y0[17] = sourceY + U.cos(255) * i_wheelRadius;
    y0[18] = sourceY + U.cos(270) * i_wheelRadius;
    y0[19] = sourceY + U.cos(285) * i_wheelRadius;
    y0[20] = sourceY + U.cos(300) * i_wheelRadius;
    y0[21] = sourceY + U.cos(315) * i_wheelRadius;
    y0[22] = sourceY + U.cos(330) * i_wheelRadius;
    y0[23] = sourceY + U.cos(345) * i_wheelRadius;
    for (n = 24; --n >= 0; ) {
     z0[n + 24] = z0[n];
     y0[n + 24] = y0[n];
    }
    pieces.add(new TrackPartPiece(this, x0, y0, z0, 48, wheelRGB, type + " wheel cylindric " + steers, textureType));//Treads
   }
   if (wheelSmoothing != 0) {
    for (n = 24; --n >= 0; ) {
     x0[n] = sourceX - wheelThickness;
     x0[n + 24] = sourceX - i_wheelThickness;
    }
    for (n = 72; --n >= 48; ) {
     x0[n] = sourceX + wheelThickness;
     x0[n + 24] = sourceX + i_wheelThickness;
    }
    z0[0] = sourceZ + U.sin(0) * i_wheelRadius;
    z0[1] = sourceZ + U.sin(15) * i_wheelRadius;
    z0[2] = sourceZ + U.sin(30) * i_wheelRadius;
    z0[3] = sourceZ + U.sin(45) * i_wheelRadius;
    z0[4] = sourceZ + U.sin(60) * i_wheelRadius;
    z0[5] = sourceZ + U.sin(75) * i_wheelRadius;
    z0[6] = sourceZ + U.sin(90) * i_wheelRadius;
    z0[7] = sourceZ + U.sin(105) * i_wheelRadius;
    z0[8] = sourceZ + U.sin(120) * i_wheelRadius;
    z0[9] = sourceZ + U.sin(135) * i_wheelRadius;
    z0[10] = sourceZ + U.sin(150) * i_wheelRadius;
    z0[11] = sourceZ + U.sin(165) * i_wheelRadius;
    z0[12] = sourceZ + U.sin(180) * i_wheelRadius;
    z0[13] = sourceZ + U.sin(195) * i_wheelRadius;
    z0[14] = sourceZ + U.sin(210) * i_wheelRadius;
    z0[15] = sourceZ + U.sin(225) * i_wheelRadius;
    z0[16] = sourceZ + U.sin(240) * i_wheelRadius;
    z0[17] = sourceZ + U.sin(255) * i_wheelRadius;
    z0[18] = sourceZ + U.sin(270) * i_wheelRadius;
    z0[19] = sourceZ + U.sin(285) * i_wheelRadius;
    z0[20] = sourceZ + U.sin(300) * i_wheelRadius;
    z0[21] = sourceZ + U.sin(315) * i_wheelRadius;
    z0[22] = sourceZ + U.sin(330) * i_wheelRadius;
    z0[23] = sourceZ + U.sin(345) * i_wheelRadius;
    z0[0 + 24] = sourceZ + U.sin(0) * wheelRadius;
    z0[1 + 24] = sourceZ + U.sin(15) * wheelRadius;
    z0[2 + 24] = sourceZ + U.sin(30) * wheelRadius;
    z0[3 + 24] = sourceZ + U.sin(45) * wheelRadius;
    z0[4 + 24] = sourceZ + U.sin(60) * wheelRadius;
    z0[5 + 24] = sourceZ + U.sin(75) * wheelRadius;
    z0[6 + 24] = sourceZ + U.sin(90) * wheelRadius;
    z0[7 + 24] = sourceZ + U.sin(105) * wheelRadius;
    z0[8 + 24] = sourceZ + U.sin(120) * wheelRadius;
    z0[9 + 24] = sourceZ + U.sin(135) * wheelRadius;
    z0[10 + 24] = sourceZ + U.sin(150) * wheelRadius;
    z0[11 + 24] = sourceZ + U.sin(165) * wheelRadius;
    z0[12 + 24] = sourceZ + U.sin(180) * wheelRadius;
    z0[13 + 24] = sourceZ + U.sin(195) * wheelRadius;
    z0[14 + 24] = sourceZ + U.sin(210) * wheelRadius;
    z0[15 + 24] = sourceZ + U.sin(225) * wheelRadius;
    z0[16 + 24] = sourceZ + U.sin(240) * wheelRadius;
    z0[17 + 24] = sourceZ + U.sin(255) * wheelRadius;
    z0[18 + 24] = sourceZ + U.sin(270) * wheelRadius;
    z0[19 + 24] = sourceZ + U.sin(285) * wheelRadius;
    z0[20 + 24] = sourceZ + U.sin(300) * wheelRadius;
    z0[21 + 24] = sourceZ + U.sin(315) * wheelRadius;
    z0[22 + 24] = sourceZ + U.sin(330) * wheelRadius;
    z0[23 + 24] = sourceZ + U.sin(345) * wheelRadius;
    y0[0] = sourceY + U.cos(0) * i_wheelRadius;
    y0[1] = sourceY + U.cos(15) * i_wheelRadius;
    y0[2] = sourceY + U.cos(30) * i_wheelRadius;
    y0[3] = sourceY + U.cos(45) * i_wheelRadius;
    y0[4] = sourceY + U.cos(60) * i_wheelRadius;
    y0[5] = sourceY + U.cos(75) * i_wheelRadius;
    y0[6] = sourceY + U.cos(90) * i_wheelRadius;
    y0[7] = sourceY + U.cos(105) * i_wheelRadius;
    y0[8] = sourceY + U.cos(120) * i_wheelRadius;
    y0[9] = sourceY + U.cos(135) * i_wheelRadius;
    y0[10] = sourceY + U.cos(150) * i_wheelRadius;
    y0[11] = sourceY + U.cos(165) * i_wheelRadius;
    y0[12] = sourceY + U.cos(180) * i_wheelRadius;
    y0[13] = sourceY + U.cos(195) * i_wheelRadius;
    y0[14] = sourceY + U.cos(210) * i_wheelRadius;
    y0[15] = sourceY + U.cos(225) * i_wheelRadius;
    y0[16] = sourceY + U.cos(240) * i_wheelRadius;
    y0[17] = sourceY + U.cos(255) * i_wheelRadius;
    y0[18] = sourceY + U.cos(270) * i_wheelRadius;
    y0[19] = sourceY + U.cos(285) * i_wheelRadius;
    y0[20] = sourceY + U.cos(300) * i_wheelRadius;
    y0[21] = sourceY + U.cos(315) * i_wheelRadius;
    y0[22] = sourceY + U.cos(330) * i_wheelRadius;
    y0[23] = sourceY + U.cos(345) * i_wheelRadius;
    y0[0 + 24] = sourceY + U.cos(0) * wheelRadius;
    y0[1 + 24] = sourceY + U.cos(15) * wheelRadius;
    y0[2 + 24] = sourceY + U.cos(30) * wheelRadius;
    y0[3 + 24] = sourceY + U.cos(45) * wheelRadius;
    y0[4 + 24] = sourceY + U.cos(60) * wheelRadius;
    y0[5 + 24] = sourceY + U.cos(75) * wheelRadius;
    y0[6 + 24] = sourceY + U.cos(90) * wheelRadius;
    y0[7 + 24] = sourceY + U.cos(105) * wheelRadius;
    y0[8 + 24] = sourceY + U.cos(120) * wheelRadius;
    y0[9 + 24] = sourceY + U.cos(135) * wheelRadius;
    y0[10 + 24] = sourceY + U.cos(150) * wheelRadius;
    y0[11 + 24] = sourceY + U.cos(165) * wheelRadius;
    y0[12 + 24] = sourceY + U.cos(180) * wheelRadius;
    y0[13 + 24] = sourceY + U.cos(195) * wheelRadius;
    y0[14 + 24] = sourceY + U.cos(210) * wheelRadius;
    y0[15 + 24] = sourceY + U.cos(225) * wheelRadius;
    y0[16 + 24] = sourceY + U.cos(240) * wheelRadius;
    y0[17 + 24] = sourceY + U.cos(255) * wheelRadius;
    y0[18 + 24] = sourceY + U.cos(270) * wheelRadius;
    y0[19 + 24] = sourceY + U.cos(285) * wheelRadius;
    y0[20 + 24] = sourceY + U.cos(300) * wheelRadius;
    y0[21 + 24] = sourceY + U.cos(315) * wheelRadius;
    y0[22 + 24] = sourceY + U.cos(330) * wheelRadius;
    y0[23 + 24] = sourceY + U.cos(345) * wheelRadius;
    for (n = 48; --n >= 0; ) {
     z0[n + 48] = z0[n];
     y0[n + 48] = y0[n];
    }
    pieces.add(new TrackPartPiece(this, x0, y0, z0, 96, wheelRGB, type + " wheel wheelRingFaces " + steers, textureType));//Tread edges
   }
  }
 }

 private void setFixRing(boolean boot) {
  if (U.averageFPS < 30) {
   fixRing.setRadius(700);
   fixRing.setTubeRadius(125);
   fixRing.setRadiusDivisions(4);
   fixRing.setTubeDivisions(4);
  } else if (U.averageFPS >= 60 || boot) {
   fixRing.setRadius(650);
   fixRing.setTubeRadius(100);
   fixRing.setRadiusDivisions(64);
   fixRing.setTubeDivisions(64);
  }
 }
}
