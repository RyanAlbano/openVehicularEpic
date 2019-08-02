package ve.vehicles;

import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import ve.*;
import ve.environment.E;
import ve.utilities.*;

public class VehiclePiece extends Piece {

 private final Quaternion steerXY = new Quaternion(), steerYZ = new Quaternion(), steerXZ = new Quaternion();
 private final Vehicle V;
 Chip chip;
 Flame flame;
 public final PointLight pointLight;
 final Rotate damage;
 private final boolean exterior;
 private final boolean landingGear;
 private final boolean spinner;
 private final boolean noCrush;
 private final boolean wheel;
 private final boolean shake;
 private final boolean fireLight;
 private final long side;
 private String steer = "";
 private final String thrust;
 double X;
 double Y;
 double Z;
 private double brightness = Double.NaN;
 private final double steerAngleMultiply;
 final double absoluteRadius;
 double explodeStage;
 double explodeTimer;
 private double explodeX;
 private double explodeY;
 private double explodeZ;
 private double explodeSpeedX;
 private double explodeSpeedY;
 private double explodeSpeedZ;
 double explodeGravitySpeed;
 private double explodeAngle;
 private double explodeAngleSpeed;
 private double[] thrustPoint;

 VehiclePiece(Vehicle v, double[] i_X, double[] i_Y, double[] i_Z, int vertexQuantity, double[] i_RGB, String type, String textureType) {
  V = v;
  V.vertexQuantity += vertexQuantity;
  int n;
  textureType = type.contains(" noTexture ") ? "" : V.modelProperties.contains(" mapTerrain ") ? VE.terrain : textureType;
  double[] storeX = new double[vertexQuantity], storeY = new double[vertexQuantity], storeZ = new double[vertexQuantity];
  light = type.contains(" light ");
  selfIlluminate = type.contains(" selfIlluminate ");
  fireLight = type.contains(" fire ") && VE.defaultVehicleLightBrightness > 0;
  pointLight = light || fireLight ? new PointLight() : null;
  thrust = type.contains(" thrustWhite ") ? "white" : type.contains(" thrustBlue ") ? "blue" : type.contains(" thrust ") ? "" : null;
  blink = type.contains(" blink ");
  exterior = type.contains(" exterior ") && VE.event != VE.event.vehicleViewer;
  landingGear = type.contains(" landingGear ");
  spinner = type.contains(" spinner ");
  wheel = type.contains(" wheel ");
  base = type.contains(" base ");
  noCrush = type.contains(" noCrush ");
  shake = V.realVehicle && (type.contains(" shake ") || (V.engine == Vehicle.Engine.hotrod && !U.contains(type, " driver ", " wheel ")));
  controller = type.contains(" controller ");
  steer += type.contains(" steerXY ") ? " XY " : "";
  steer += type.contains(" steerYZ ") ? " YZ " : "";
  steer += type.contains(" steerXZ ") ? " XZ " : "";
  steer += type.contains(" steerFromXZ ") ? " fromXZ " : "";
  steer += type.contains(" steerFromYZ ") ? " fromYZ " : "";
  steerAngleMultiply = V.steerAngleMultiply;
  side = type.contains(" R ") ? 1 : type.contains(" L ") ? -1 : 0;
  double[] maxPlusX = new double[2];
  double[] maxMinusX = new double[2];
  double[] maxPlusY = new double[2];
  double[] maxMinusY = new double[2];
  double[] maxPlusZ = new double[2];
  double[] maxMinusZ = new double[2];
  for (n = vertexQuantity; --n >= 0; ) {
   storeX[n] = i_X[n];
   storeY[n] = i_Y[n];
   storeZ[n] = i_Z[n];
   renderRadius = Math.max(renderRadius, U.netValue(storeX[n], storeY[n], storeZ[n]));
   maxMinusX[0] = Math.min(maxMinusX[0], storeX[n]);
   maxPlusX[0] = Math.max(maxPlusX[0], storeX[n]);
   maxMinusY[0] = Math.min(maxMinusY[0], storeY[n]);
   maxPlusY[0] = Math.max(maxPlusY[0], storeY[n]);
   maxMinusZ[0] = Math.min(maxMinusZ[0], storeZ[n]);
   maxPlusZ[0] = Math.max(maxPlusZ[0], storeZ[n]);
   maxMinusX[1] += storeX[n] < 0 ? storeX[n] : 0;
   maxPlusX[1] += storeX[n] > 0 ? storeX[n] : 0;
   maxMinusY[1] += storeY[n] < 0 ? storeY[n] : 0;
   maxPlusY[1] += storeY[n] > 0 ? storeY[n] : 0;
   maxMinusZ[1] += storeZ[n] < 0 ? storeZ[n] : 0;
   maxPlusZ[1] += storeZ[n] > 0 ? storeZ[n] : 0;
  }
  maxMinusX[1] /= vertexQuantity;
  maxPlusX[1] /= vertexQuantity;
  maxMinusY[1] /= vertexQuantity;
  maxPlusY[1] /= vertexQuantity;
  maxMinusZ[1] /= vertexQuantity;
  maxPlusZ[1] /= vertexQuantity;
  absoluteRadius = Math.abs(maxMinusX[0]) + Math.abs(maxPlusX[0]) + Math.abs(maxMinusY[0]) + Math.abs(maxPlusY[0]) + Math.abs(maxMinusZ[0]) + Math.abs(maxPlusZ[0]) + Math.abs(maxMinusX[1]) + Math.abs(maxPlusX[1]) + Math.abs(maxMinusY[1]) + Math.abs(maxPlusY[1]) + Math.abs(maxMinusZ[1]) + Math.abs(maxPlusZ[1]);
  if (!base) {
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
  if (U.contains(type, " triangles ", " triangles+1 ")) {
   setTriangles(vertexQuantity);
  } else if (type.contains(" conic ")) {
   setConic(vertexQuantity);
   thrustPoint = thrust != null ? new double[]{TM.getPoints().get(0), TM.getPoints().get(1), TM.getPoints().get(2)} : null;
  } else if (type.contains(" strip ")) {
   setStrip(vertexQuantity);
  } else if (U.contains(type, " grid ", " grid+1 ")) {
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
  if (V.realVehicle && thrust == null) {
   chip = new Chip(this);
   flame = new Flame(this);
  }
  if (type.contains(" theRandomColor ")) {
   RGB[0] = V.theRandomColor[0];
   RGB[1] = V.theRandomColor[1];
   RGB[2] = V.theRandomColor[2];
  } else {
   RGB[0] = i_RGB[0];
   RGB[1] = i_RGB[1];
   RGB[2] = i_RGB[2];
  }
  double[] storeRGB = {RGB[0], RGB[1], RGB[2]};
  for (n = 3; --n >= 0; ) {
   RGB[n] = type.contains(" reflect ") ? E.skyRGB[n] : RGB[n];
   RGB[n] = light && (type.contains(" reflect ") || storeRGB[0] == storeRGB[1] && storeRGB[1] == storeRGB[2]) ? 1 : RGB[n];
   RGB[n] = V.modelProperties.contains(" mapTerrain ") ? E.terrainRGB[n] : RGB[n];
   RGB[n] = blink || thrust != null ? 0 : RGB[n];
  }
  U.setDiffuseRGB(PM, RGB[0], RGB[1], RGB[2]);
  if (light) {
   setBrightness();
  }
  if (type.contains(" noSpecular ") || blink || thrust != null) {
   U.setSpecularRGB(PM, 0, 0, 0);
  } else if (type.contains(" shiny ")) {
   U.setSpecularRGB(PM, 1, 1, 1);
   PM.setSpecularPower(U.shinySpecular);
  } else {
   U.setSpecularRGB(PM, .5, .5, .5);
   PM.setSpecularPower(10);
  }
  if (selfIlluminate) {
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
  setRenderSizeRequirement(storeX, storeY, storeZ, vertexQuantity, light || blink || thrust != null || VE.vehiclesInMatch < 3);
  MV.setVisible(false);
  damage = new Rotate();
  if (matrix != null) {
   MV.getTransforms().setAll(matrix, damage);
  } else {
   MV.getTransforms().setAll(damage);
  }
 }

 private void setBrightness() {
  U.getRGB.setFill(Color.color(U.clamp(RGB[0] * 2 * brightness), U.clamp(RGB[1] * 2 * brightness), U.clamp(RGB[2] * 2 * brightness)));
  U.getRGB.fillRect(0, 0, 1, 1);
  PM.setSelfIlluminationMap(U.getRGBCanvas.snapshot(null, null));
  if (light) {
   U.setLightRGB(pointLight, RGB[0] * brightness, RGB[1] * brightness, RGB[2] * brightness);
  }
 }

 void process(boolean gamePlay) {
  if (size * VE.renderLevel >= V.instanceToCameraDistance * VE.zoom && explodeStage <= 3 && !(exterior && V.inDriverView) && !(flickPolarity == 1 && V.flicker) && !(flickPolarity == 2 && !V.flicker) && !(landingGear && V.mode == VE.mode.fly) && (thrust == null || (V.thrusting && !V.destroyed))) {
   boolean render = true;
   if (fastCull == fastCull) {
    long shiftedAxis = Math.round(fastCull);
    if (V.XZ > 45 && V.XZ < 135) {
     shiftedAxis = --shiftedAxis < -1 ? 2 : shiftedAxis;
    } else if (V.XZ < -45 && V.XZ > -135) {
     shiftedAxis = ++shiftedAxis > 2 ? -1 : shiftedAxis;
    } else if (Math.abs(V.XZ) > 135) {
     shiftedAxis = ++shiftedAxis > 2 ? -1 : shiftedAxis;
     shiftedAxis = ++shiftedAxis > 2 ? -1 : shiftedAxis;
    }
    if (Math.abs(V.YZ) > 90) {
     shiftedAxis = ++shiftedAxis > 2 ? -1 : shiftedAxis;
     shiftedAxis = ++shiftedAxis > 2 ? -1 : shiftedAxis;
    }
    render = shiftedAxis == 2 ? VE.cameraZ >= V.Z : shiftedAxis < 0 ? VE.cameraX >= V.X : shiftedAxis > 0 ? VE.cameraX <= V.X : VE.cameraZ <= V.Z;
   }
   if (render) {
    double[] placementX = {displaceX + (controller ? V.driverViewX * VE.driverSeat : 0)}, placementY = {displaceY}, placementZ = {displaceZ};
    if (!base) {
     double rotation = (steer.contains(" fromYZ ") ? V.speedYZ : V.speedXZ) * steerAngleMultiply * .5;
     if (steer.contains(" XY ")) {
      steerXY.set(0, 0, U.sin(rotation), U.cos(rotation));
     }
     if (wheel) {
      double wheelSpin = (side > 0 ? V.wheelSpin[0] : side < 0 ? V.wheelSpin[1] : 0) * .5;
      steerYZ.set(U.sin(wheelSpin), 0, 0, U.cos(wheelSpin));
     } else if (steer.contains(" YZ ")) {
      rotation = (steer.contains(" fromYZ ") ? -V.speedYZ : V.speedXZ) * steerAngleMultiply * .5;
      steerYZ.set(U.sin(rotation), 0, 0, U.cos(rotation));
     }
     rotation = (steer.contains(" fromYZ ") && !steer.contains(" fromXZ ") ? -V.speedYZ : V.speedXZ) * steerAngleMultiply * .5;
     if (steer.contains(" XZ ")) {
      steerXZ.set(0, U.sin(rotation), 0, U.cos(rotation));
     }
     if (spinner) {
      steerXZ.set(0, U.sin(V.spinnerXZ), 0, U.cos(V.spinnerXZ));
     }
     U.inert.set();
     matrix.set(U.inert.multiply(steerXY).multiply(steerYZ).multiply(steerXZ).multiply(V.rotation));
    }
    if (explodeStage > 0) {
     if (explodeStage == 1) {
      long random = U.random(3);
      explodeAngle = random == 2 ? V.XY : random == 1 ? V.YZ : V.XZ;
      explodeAngleSpeed = U.randomPlusMinus(40.);
      explodeX = explodeY = explodeZ = explodeGravitySpeed = 0;
      explodeSpeedX = U.randomPlusMinus(200.);
      explodeSpeedY = U.randomPlusMinus(200.);
      explodeSpeedZ = U.randomPlusMinus(200.);
      explodeStage = 2;
     }
     explodeAngle += explodeStage < 3 ? explodeAngleSpeed * VE.tick : 0;
     damage.setAngle(explodeAngle);
     if (gamePlay) {
      if (explodeStage < 3) {
       explodeX += explodeSpeedX * VE.tick;
       explodeZ += explodeSpeedZ * VE.tick;
      }
      explodeY += explodeSpeedY * VE.tick;
      explodeGravitySpeed += E.gravity * VE.tick;
      explodeY += V.mode.name().startsWith("drive") ? explodeGravitySpeed * VE.tick : 0;
     }
     if (explodeY > V.localVehicleGround - V.Y) {
      explodeY = V.localVehicleGround - V.Y;
      explodeStage = 3;
     }
     placementX[0] += explodeX;
     placementY[0] += explodeY;
     placementZ[0] += explodeZ;
    } else if (shake && !V.destroyed) {
     placementX[0] += V.absoluteRadius * U.randomPlusMinus(.001);
     placementY[0] += V.absoluteRadius * U.randomPlusMinus(.001);
     placementZ[0] += V.absoluteRadius * U.randomPlusMinus(.001);
    }
    if (!V.inWrath) {
     if (V.XY != 0) {
      U.rotate(placementX, placementY, V.XY);
     }
     if (V.YZ != 0) {
      U.rotate(placementY, placementZ, V.YZ);
     }
     U.rotate(placementX, placementZ, V.XZ);
     X = V.X + placementX[0];
     Y = V.Y + placementY[0];
     Z = V.Z + placementZ[0];
    }
    if (!V.destroyed) {
     if (fireLight) {
      U.setTranslate(pointLight, X, Y, Z);
      U.setLightRGB(pointLight, 1, .5 + U.random(.25), 0);
      U.addLight(pointLight);
     } else if (light) {
      if (brightness != V.lightBrightness) {
       brightness = V.lightBrightness;
       setBrightness();
      }
      if (brightness > 0) {
       U.setTranslate(pointLight, X, Y, Z);
       U.addLight(pointLight);
      }
     }
    }
    if (U.getDepth(X, Y, Z) > -renderRadius) {
     if (thrust != null) {
      PM.setSelfIlluminationMap(U.getImage(thrust.contains("white") ? "white" : ((thrust.contains("blue") ? "blueJet" : "firelight") + U.random(3))));
      if (thrustPoint != null) {
       double thrustShift = V.absoluteRadius * .02;
       for (int n = 3; --n >= 0; ) {
        TM.getPoints().set(n, (float) (thrustPoint[n] + U.randomPlusMinus(thrustShift)));
       }
      }
     }
     U.setTranslate(MV, X, Y, Z);
     visible = true;
     if (blink) {
      PM.setSelfIlluminationMap(V.destroyed ? null : U.getImage("blink" + U.random(3)));
     }
    }
   }
  }
 }

 void crush() {
  if (!noCrush) {
   damage.setAxis(new Point3D(U.random(), U.random(), U.random()));
   damage.setAngle(U.randomPlusMinus((V.damage / V.durability) * 6.));
  }
 }
}
