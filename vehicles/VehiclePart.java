package ve.vehicles;

import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import ve.*;
import ve.Camera;
import ve.effects.Smoke;
import ve.environment.E;
import ve.utilities.*;

import java.util.ArrayList;
import java.util.List;

public class VehiclePart extends InstancePart {

 private final Quaternion steerXY = new Quaternion(), steerYZ = new Quaternion(), steerXZ = new Quaternion();
 private final Vehicle V;
 Chip chip;
 Flame flame;
 public final PointLight pointLight;
 final Rotate damage;
 private final boolean exterior;
 private final boolean landingGear;
 private final boolean vehicleTurret;
 private final boolean vehicleTurretBarrel;
 private final boolean spinner;
 private final boolean noDeformation;
 private final boolean wheel;
 private final boolean shake;
 private final boolean fireLight;
 private final long side;
 private String steer = "";
 final Thrust thrust;
 private double brightness = Double.NaN;
 private final double steerAngleMultiply;
 double explodeStage, explodeTimer;
 private double explodeX, explodeY, explodeZ, explodeSpeedX, explodeSpeedY, explodeSpeedZ;
 double explodeGravitySpeed;
 private double explodeAngle, explodeAngleSpeed;
 private double[] thrustPoint;
 List<Smoke> smokes;
 int currentSmoke;
 List<ThrustTrail> thrustTrails;
 int currentThrustTrail;
 private final double pivotX, pivotY, pivotZ;
 private int wheelShockAbsorbAssign = -1;
 private final DrawMode defaultDrawMode;

 enum Thrust {fire, blue, white}

 public VehiclePart(Vehicle inV, double[] i_X, double[] i_Y, double[] i_Z, int vertexQuantity, double[] i_RGB, String type, String textureType) {
  this(inV, i_X, i_Y, i_Z, vertexQuantity, i_RGB, type, textureType, 0, 0, 0);
 }

 VehiclePart(Vehicle inV, double[] i_X, double[] i_Y, double[] i_Z, int vertexQuantity, double[] i_RGB, String type, String textureType, double pivotX, double pivotY, double pivotZ) {
  V = inV;
  V.vertexQuantity += vertexQuantity;
  int n;
  textureType = type.contains(" noTexture ") ? "" : V.modelProperties.contains(" mapTerrain ") ? E.terrain : textureType;
  this.pivotX = pivotX;
  this.pivotY = pivotY;
  this.pivotZ = pivotZ;
  double[] storeX = new double[vertexQuantity], storeY = new double[vertexQuantity], storeZ = new double[vertexQuantity];
  light = type.contains(" light ");
  selfIlluminate = type.contains(" selfIlluminate ");
  fireLight = type.contains(" fire ") && VE.defaultVehicleLightBrightness > 0;
  pointLight = light || fireLight ? new PointLight() : null;
  thrust = type.contains(" thrustWhite ") ? Thrust.white : type.contains(" thrustBlue ") ? Thrust.blue : type.contains(" thrust ") ? Thrust.fire : null;
  blink = type.contains(" blink ");
  exterior = type.contains(" exterior ") && VE.status != VE.Status.vehicleViewer;
  landingGear = type.contains(" landingGear ");
  spinner = type.contains(" spinner ");
  wheel = type.contains(" wheel ");
  base = type.contains(" base ");
  noDeformation = type.contains(" noCrush ");
  shake = V.realVehicle && (type.contains(" shake ") || (V.engine == Vehicle.Engine.hotrod && !U.contains(type, " driver ", " wheel ")));
  vehicleTurretBarrel = type.contains(" turretBarrel ");
  vehicleTurret = type.contains(" turret ") || vehicleTurretBarrel;
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
  if (type.contains(" smokePoint ")) {
   smokes = new ArrayList<>();
   for (n = E.smokeQuantity; --n >= 0; ) {
    smokes.add(new Smoke(this));
   }
  }
  if (type.contains(" thrustTrailPoint ")) {
   thrustTrails = new ArrayList<>();
   for (n = E.thrustTrailQuantity; --n >= 0; ) {
    thrustTrails.add(new ThrustTrail(this));
   }
  }
  if (!base) {
   setDisplacement(storeX, storeY, storeZ, vertexQuantity);
   if (V.shockAbsorb > 0 && wheel) {
    if (displaceZ > 0) {
     wheelShockAbsorbAssign = displaceX > 0 ? 1 : 0;
    } else if (displaceZ < 0) {
     wheelShockAbsorbAssign = displaceX > 0 ? 3 : 2;
    }
   }
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
  for (n = 0; n < vertexQuantity / (double) 3; n++) {
   TM.getTexCoords().addAll(coordinates);
  }
  if (type.contains(" triangles ")) {
   setTriangles(vertexQuantity);
  } else if (type.contains(" conic ")) {
   setConic(V, vertexQuantity);
   thrustPoint = thrust != null && thrust != Thrust.white ? new double[]{TM.getPoints().get(0), TM.getPoints().get(1), TM.getPoints().get(2)} : null;
  } else if (type.contains(" strip ")) {
   setStrip(V, vertexQuantity);
  } else if (type.contains(" squares ")) {
   setSquares(V, vertexQuantity);
  } else if (type.contains(" cylindric ")) {
   setCylindric(V, vertexQuantity);
  } else if (type.contains(" rimFaces ")) {
   setConic(V, 7);
  } else if (type.contains(" sportRimFaces ")) {
   setConic(V, 16);
  } else if (type.contains(" wheelRingFaces ")) {
   setCylindric(V, 48);
   setWheelRingFaces();
  } else {
   setFaces(V, type.contains(" wheelFaces ") ? 24 : vertexQuantity);
  }
  MV = new MeshView(TM);
  defaultDrawMode = type.contains(" line ") ? DrawMode.LINE : DrawMode.FILL;
  MV.setDrawMode(defaultDrawMode);
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
   PM.setSpecularPower(E.SpecularPowers.shiny);
  } else {
   U.setSpecularRGB(PM, .5, .5, .5);
   PM.setSpecularPower(E.SpecularPowers.standard);
  }
  if (selfIlluminate) {
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
  U.setSelfIllumination(PM, RGB[0] * 2 * brightness, RGB[1] * 2 * brightness, RGB[2] * 2 * brightness);
  if (light) {
   U.setLightRGB(pointLight, RGB[0] * brightness, RGB[1] * brightness, RGB[2] * brightness);
  }
 }

 void setPosition(boolean gamePlay) {
  double[] placementX = {displaceX + (controller ? V.driverViewX * VE.driverSeat : 0)}, placementY = {displaceY}, placementZ = {displaceZ};
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
    explodeY += V.mode.name().startsWith(Vehicle.Mode.drive.name()) ? explodeGravitySpeed * VE.tick : 0;
   }
   if (explodeY + V.Y > V.localVehicleGround) {//<-Same value check as assigned below--just expressed more intuitively
    explodeY = V.localVehicleGround - V.Y;
    explodeStage = Math.max(3, explodeStage);
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
   if (pivotZ != 0 || pivotX != 0 || (!controller && V.hasTurret)) {//<-May need to be further amended later so things don't rotate unintentionally
    if (steer.contains(" YZ ") || vehicleTurretBarrel) {
     U.rotateWithPivot(placementZ, placementY,
     vehicleTurretBarrel ? V.vehicleTurretPivotY + pivotY : (pivotZ + displaceZ), vehicleTurretBarrel ? V.vehicleTurretPivotZ + pivotZ : (pivotY + displaceY),
     vehicleTurretBarrel ? V.vehicleTurretYZ : ((steer.contains(" fromYZ ") ? -V.speedYZ : V.speedXZ) * steerAngleMultiply));
    }
    if (steer.contains(" XZ ") || vehicleTurret) {
     U.rotateWithPivot(placementX, placementZ,
     pivotX + (vehicleTurret ? 0 : displaceX), vehicleTurret ? V.vehicleTurretPivotZ : (pivotZ + displaceZ),
     vehicleTurret ? V.vehicleTurretXZ : ((steer.contains(" fromYZ ") && !steer.contains(" fromXZ ") ? -V.speedYZ : V.speedXZ) * steerAngleMultiply));
    }
   }
   if (V.XY != 0) {
    U.rotate(placementX, placementY, V.XY);
   }
   if (V.YZ != 0) {
    U.rotate(placementY, placementZ, V.YZ);
   }
   U.rotate(placementX, placementZ, V.XZ);
   X = V.X + placementX[0];
   Y = V.Y + placementY[0] + (wheelShockAbsorbAssign >= 0 ? V.wheels.get(wheelShockAbsorbAssign).vibrate : 0);
   Z = V.Z + placementZ[0];
  }
 }

 void render() {
  if ((E.renderType.name().contains(E.RenderType.fullDistance.name()) || (size * E.renderLevel >= V.distanceToCamera * Camera.zoom)) &&
  (E.renderType == E.RenderType.fullDistanceALL || (explodeStage <= 3 &&
  !(exterior && V.inDriverView) &&
  !(flickPolarity == 1 && V.flicker) && !(flickPolarity == 2 && !V.flicker) &&
  !(landingGear && V.mode == Vehicle.Mode.fly) &&
  (thrust == null || (V.thrusting && !V.destroyed))))) {
   boolean render = true;
   if (!Double.isNaN(fastCull) && E.renderType != E.RenderType.fullDistanceALL) {
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
    render = shiftedAxis == 2 ? Camera.Z >= V.Z : shiftedAxis < 0 ? Camera.X >= V.X : shiftedAxis > 0 ? Camera.X <= V.X : Camera.Z <= V.Z;
   }
   if (render) {
    if (!base) {
     if (steer.contains(" XY ")) {
      steerXY.setXY((steer.contains(" fromYZ ") ? V.speedYZ : V.speedXZ) * steerAngleMultiply);
     }
     if (wheel) {
      steerYZ.setYZ(side > 0 ? V.wheelSpin[0] : side < 0 ? V.wheelSpin[1] : 0);
     } else if (steer.contains(" YZ ")) {
      steerYZ.setYZ((steer.contains(" fromYZ ") ? -V.speedYZ : V.speedXZ) * steerAngleMultiply);//<-Master script
     }
     if (steer.contains(" XZ ")) {
      steerXZ.setXZ((steer.contains(" fromYZ ") && !steer.contains(" fromXZ ") ? -V.speedYZ : V.speedXZ) * steerAngleMultiply);//<-Master script
     } else if (vehicleTurret) {
      steerXZ.setXZ(V.vehicleTurretXZ);
      if (vehicleTurretBarrel) {
       steerYZ.setYZ(V.vehicleTurretYZ);
      }
     } else if (spinner) {
      steerXZ.setXZ(V.spinnerXZ);
     }
     U.inert.set();
     matrix.set(U.inert.multiply(steerXY).multiply(steerYZ).multiply(steerXZ).multiply(V.rotation));
    }
    if (!V.destroyed) {
     if (fireLight) {
      U.setTranslate(pointLight, this);
      U.setLightRGB(pointLight, 1, .5 + U.random(.25), 0);
      U.addLight(pointLight);
     } else if (light) {
      if (brightness != V.lightBrightness) {
       brightness = V.lightBrightness;
       setBrightness();
      }
      if (brightness > 0) {
       U.setTranslate(pointLight, this);
       U.addLight(pointLight);
      }
     }
    }
    if (E.renderType.name().contains(E.RenderType.fullDistance.name()) || U.render(this, -renderRadius)) {
     if (thrust != null) {
      PM.setSelfIlluminationMap(U.getImage(thrust == Thrust.white ? "white" : ((thrust == Thrust.blue ? "blueJet" : "firelight") + U.random(3))));
      if (thrustPoint != null) {
       double thrustShift = V.absoluteRadius * .02;
       for (int n = 3; --n >= 0; ) {
        TM.getPoints().set(n, (float) (thrustPoint[n] + U.randomPlusMinus(thrustShift)));
       }
      }
     }
     U.setTranslate(MV, this);
     visible = true;
     if (blink) {
      PM.setSelfIlluminationMap(V.destroyed ? null : U.getImage("blink" + U.random(3)));
     }
    }
   }
  }
 }

 void throwChip(double power) {
  if (chip != null) {
   chip.deploy(V, power);
  }
 }

 void deform() {
  if (!noDeformation) {
   damage.setAxis(new Point3D(U.random(), U.random(), U.random()));
   damage.setAngle(U.randomPlusMinus((V.damage / V.durability) * 6.));
  }
 }

 public void setDrawMode(boolean forceWireframe) {
  MV.setDrawMode(forceWireframe ? DrawMode.LINE : defaultDrawMode);
 }
}
