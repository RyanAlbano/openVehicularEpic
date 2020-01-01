package ve.vehicles;

import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import ve.effects.Effects;
import ve.effects.Smoke;
import ve.environment.E;
import ve.instances.InstancePart;
import ve.ui.Map;
import ve.ui.Options;
import ve.ui.UI;
import ve.utilities.*;
import ve.utilities.Camera;

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
 ExplodeStage explodeStage = ExplodeStage.intact;
 private double explodeSpeedX, explodeSpeedY, explodeSpeedZ;
 private double explodeAngleSpeed;
 private double[] thrustPoint;
 List<Smoke> smokes;
 int currentSmoke;
 List<ThrustTrail> thrustTrails;
 int currentThrustTrail;
 private final double pivotX, pivotY, pivotZ;
 private int wheelShockAbsorbAssign = -1;
 private final DrawMode defaultDrawMode;
 private Color RGB;

 enum Thrust {fire, blue, white}

 enum ExplodeStage {intact, thrown}

 public VehiclePart(Vehicle inV, double[] i_X, double[] i_Y, double[] i_Z, int vertexQuantity, Color i_RGB, String type, String textureType) {
  this(inV, i_X, i_Y, i_Z, vertexQuantity, i_RGB, type, textureType, 0, 0, 0);
 }

 VehiclePart(Vehicle inV, double[] i_X, double[] i_Y, double[] i_Z, int vertexQuantity, Color i_RGB, String type, String textureType, double pivotX, double pivotY, double pivotZ) {
  V = inV;
  V.vertexQuantity += vertexQuantity;
  textureType = type.contains(SL.Thick(SL.noTexture)) ? "" : textureType;
  this.pivotX = pivotX;
  this.pivotY = pivotY;
  this.pivotZ = pivotZ;
  double[] storeX = new double[vertexQuantity], storeY = new double[vertexQuantity], storeZ = new double[vertexQuantity];
  light = type.contains(SL.Thick(SL.light));
  selfIlluminate = type.contains(SL.Thick(SL.selfIlluminate));
  fireLight = type.contains(SL.Thick(SL.fire)) && Map.defaultVehicleLightBrightness > 0;
  pointLight = light || fireLight ? new PointLight() : null;
  thrust = type.contains(SL.Thick(SL.thrustWhite)) ? Thrust.white : type.contains(SL.Thick(SL.thrustBlue)) ? Thrust.blue : type.contains(SL.Thick(SL.thrust)) ? Thrust.fire : null;
  blink = type.contains(SL.Thick(SL.blink));
  exterior = type.contains(" exterior ") && UI.status != UI.Status.vehicleViewer;
  landingGear = type.contains(SL.Thick(SL.landingGear));
  spinner = type.contains(" spinner ");
  wheel = type.contains(SL.Thick(SL.wheel));
  base = type.contains(SL.Thick(SL.base));
  noDeformation = type.contains(" noCrush ");
  shake = V.realVehicle && (type.contains(" shake ") || (V.engine == Vehicle.Engine.hotrod && !U.contains(type, " driver ", SL.Thick(SL.wheel))));
  vehicleTurretBarrel = type.contains(SL.Thick(SL.turretBarrel));
  vehicleTurret = type.contains(SL.Thick(SL.turret)) || vehicleTurretBarrel;
  controller = type.contains(SL.Thick(SL.controller));
  steer += type.contains(SL.Thick(SL.steerXY)) ? SL.Thick(SL.XY) : "";
  steer += type.contains(SL.Thick(SL.steerYZ)) ? SL.Thick(SL.YZ) : "";
  steer += type.contains(SL.Thick(SL.steerXZ)) ? SL.Thick(SL.XZ) : "";
  steer += type.contains(SL.Thick(SL.steerFromXZ)) ? SL.Thick(SL.fromXZ) : "";
  steer += type.contains(SL.Thick(SL.steerFromYZ)) ? SL.Thick(SL.fromYZ) : "";
  steerAngleMultiply = V.steerAngleMultiply;
  side = type.contains(" R ") ? 1 : type.contains(" L ") ? -1 : 0;
  double[]
  maxPlusX = new double[2], maxMinusX = new double[2],
  maxPlusY = new double[2], maxMinusY = new double[2],
  maxPlusZ = new double[2], maxMinusZ = new double[2];
  for (int n = vertexQuantity; --n >= 0; ) {
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
   for (long n = Smoke.defaultQuantity; --n >= 0; ) {
    smokes.add(new Smoke(this));
   }
  }
  if (type.contains(" thrustTrailPoint ")) {
   thrustTrails = new ArrayList<>();
   for (long n = ThrustTrail.defaultQuantity; --n >= 0; ) {
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
  for (int n = vertexQuantity; --n >= 0; ) {
   coordinates[(n * 3)] = (float) storeX[n];
   coordinates[(n * 3) + 1] = (float) storeY[n];
   coordinates[(n * 3) + 2] = (float) storeZ[n];
  }
  TM.getPoints().setAll(coordinates);
  float[] textureCoordinates = U.random() < .5 ? E.textureCoordinateBase0 : E.textureCoordinateBase1;
  for (long n = 0; n < vertexQuantity / (double) 3; n++) {
   TM.getTexCoords().addAll(textureCoordinates);//<-'addAll' and NOT 'setAll'
  }
  if (type.contains(SL.Thick(FaceFunction.triangles.name()))) {
   setTriangles(vertexQuantity);
  } else if (type.contains(SL.Thick(FaceFunction.conic.name()))) {
   setConic(V, vertexQuantity);
   thrustPoint = thrust != null && thrust != Thrust.white ? new double[]{TM.getPoints().get(0), TM.getPoints().get(1), TM.getPoints().get(2)} : thrustPoint;
  } else if (type.contains(SL.Thick(FaceFunction.strip.name()))) {
   setStrip(V, vertexQuantity);
  } else if (type.contains(SL.Thick(FaceFunction.squares.name()))) {
   setSquares(V, vertexQuantity);
  } else if (type.contains(SL.Thick(FaceFunction.cylindric.name()))) {
   setCylindric(V, vertexQuantity);
  } else if (type.contains(SL.Thick(SL.rimFaces))) {
   setConic(V, 7);
  } else if (type.contains(SL.Thick(SL.sportRimFaces))) {
   setConic(V, 16);
  } else if (type.contains(SL.Thick(SL.wheelRingFaces))) {
   setCylindric(V, 48);
   setWheelRingFaces();
  } else {
   setFaces(V, type.contains(SL.Thick(SL.wheelFaces)) ? 24 : vertexQuantity);
  }
  MV = new MeshView(TM);
  defaultDrawMode = type.contains(SL.Thick(SL.line)) ? DrawMode.LINE : DrawMode.FILL;
  MV.setDrawMode(defaultDrawMode);
  MV.setCullFace(CullFace.BACK);
  PM = new PhongMaterial();
  RGB = type.contains(SL.Thick(SL.theRandomColor)) ? V.theRandomColor : i_RGB;
  Color storeRGB = RGB;
  if (type.contains(SL.Thick(SL.reflect))) {
   RGB = U.getColor(E.skyRGB);
  }
  if (light && (type.contains(SL.Thick(SL.reflect)) || (storeRGB.getRed() == storeRGB.getGreen() && storeRGB.getGreen() == storeRGB.getBlue()))) {
   RGB = U.getColor(1);
  }
  if (blink || thrust != null) {
   RGB = U.getColor(0);
  }
  U.Phong.setDiffuseRGB(PM, RGB);
  if (light) {
   setBrightness();
  }
  if (type.contains(SL.Thick(SL.noSpecular)) || blink || thrust != null) {
   U.Phong.setSpecularRGB(PM, 0);
  } else {
   boolean shiny = type.contains(SL.Thick(SL.shiny));
   U.Phong.setSpecularRGB(PM, shiny ? E.Specular.Colors.shiny : E.Specular.Colors.standard);
   PM.setSpecularPower(shiny ? E.Specular.Powers.shiny : E.Specular.Powers.standard);
  }
  if (selfIlluminate) {
   PM.setSelfIlluminationMap(U.Phong.getSelfIllumination(RGB));
  }
  U.setMaterialSecurely(MV, PM);
  PM.setDiffuseMap(Images.get(textureType));
  PM.setSpecularMap(Images.get(textureType));
  PM.setBumpMap(Images.getNormalMap(textureType));
  if (V.realVehicle && thrust == null) {
   chip = new Chip(this);
   flame = new Flame(this);
  }
  if (UI.status != UI.Status.vehicleViewer) {
   fastCull = type.contains(SL.Thick(SL.fastCullB)) ? 0 : fastCull;
   fastCull = type.contains(SL.Thick(SL.fastCullF)) ? 2 : fastCull;
   fastCull = type.contains(SL.Thick(SL.fastCullR)) ? -1 : fastCull;
   fastCull = type.contains(SL.Thick(SL.fastCullL)) ? 1 : fastCull;
  }
  flickPolarity = type.contains(SL.Thick(SL.flick1)) ? 1 : type.contains(SL.Thick(SL.flick2)) ? 2 : flickPolarity;
  setRenderSizeRequirement(storeX, storeY, storeZ, vertexQuantity, light || blink || thrust != null || UI.vehiclesInMatch < 3);
  MV.setVisible(false);
  damage = new Rotate();
  if (matrix != null) {
   MV.getTransforms().setAll(matrix, damage);
  } else {
   MV.getTransforms().setAll(damage);
  }
 }

 private void setBrightness() {
  PM.setSelfIlluminationMap(U.Phong.getSelfIllumination(RGB.getRed() * 2 * brightness, RGB.getGreen() * 2 * brightness, RGB.getBlue() * 2 * brightness));
  if (light) {
   U.Nodes.Light.setRGB(pointLight, RGB.getRed() * brightness, RGB.getGreen() * brightness, RGB.getBlue() * brightness);
  }
 }

 void setPosition(boolean nullPhysics) {
  double[] placementX = {displaceX + (controller ? V.driverViewX * Options.driverSeat : 0)}, placementY = {displaceY}, placementZ = {displaceZ};
  if (!V.isIntegral()) {
   if (explodeStage == ExplodeStage.intact) {
    damage.setAxis(new Point3D(U.random(), U.random(), U.random()));
    explodeSpeedX = U.randomPlusMinus(150.);
    explodeSpeedY = U.randomPlusMinus(150.);
    explodeSpeedZ = U.randomPlusMinus(150.);
    explodeAngleSpeed = U.randomPlusMinus(40.);
    explodeStage = ExplodeStage.thrown;
   }
   damage.setAngle(explodeAngleSpeed * V.P.explodeStage);
   double explodeY = explodeSpeedY * V.P.explodeStage;
   if (explodeY + V.Y > V.P.localGround) {//<-Same value check as assigned below--just expressed more intuitively
    explodeY = V.P.localGround - V.Y;
   }
   placementX[0] += explodeSpeedX * V.P.explodeStage;
   placementY[0] += explodeY;
   placementZ[0] += explodeSpeedZ * V.P.explodeStage;
  } else if (shake && !V.destroyed) {
   placementX[0] += V.absoluteRadius * U.randomPlusMinus(.001);
   placementY[0] += V.absoluteRadius * U.randomPlusMinus(.001);
   placementZ[0] += V.absoluteRadius * U.randomPlusMinus(.001);
  }
  if (nullPhysics || !V.P.inWrath) {//<-'nullPhysics' is only called to prevent nullPointer from null Physics
   if (!nullPhysics && (pivotZ != 0 || pivotX != 0 || (!controller && V.VT != null))) {//<-May need to be further amended later so things don't rotate unintentionally
    if (steer.contains(SL.Thick(SL.YZ)) || vehicleTurretBarrel) {
     U.rotateWithPivot(placementZ, placementY,
     vehicleTurretBarrel ? V.VT.pivotY + pivotY : (pivotZ + displaceZ), vehicleTurretBarrel ? V.VT.pivotZ + pivotZ : (pivotY + displaceY),
     vehicleTurretBarrel ? V.VT.YZ : ((steer.contains(SL.Thick(SL.fromYZ)) ? -V.P.speedYZ : V.P.speedXZ) * steerAngleMultiply));
    }
    if (steer.contains(SL.Thick(SL.XZ)) || vehicleTurret) {
     U.rotateWithPivot(placementX, placementZ,
     pivotX + (vehicleTurret ? 0 : displaceX), vehicleTurret ? V.VT.pivotZ : (pivotZ + displaceZ),
     vehicleTurret ? V.VT.XZ : ((steer.contains(SL.Thick(SL.fromYZ)) && !steer.contains(SL.Thick(SL.fromXZ)) ? -V.P.speedYZ : V.P.speedXZ) * steerAngleMultiply));
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
   Y = V.Y + placementY[0] + (wheelShockAbsorbAssign >= 0 ? V.wheels.get(wheelShockAbsorbAssign).vibrateY : 0);
   Z = V.Z + placementZ[0];
  }
 }

 void render(boolean nullPhysics, double distanceCameraVehicle, boolean renderALL) {
  if (renderALL || ((E.renderType == E.RenderType.fullDistance || (size * E.renderLevel >= distanceCameraVehicle * Camera.zoom)) &&
  (!(exterior && V.inDriverView) && !(flickPolarity == 1 && V.flicker) && !(flickPolarity == 2 && !V.flicker) &&
  !(landingGear && !nullPhysics && V.P.mode == Physics.Mode.fly) &&
  (thrust == null || (V.thrusting && !V.destroyed))))) {
   boolean render = true;
   if (!Double.isNaN(fastCull) && !renderALL) {
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
     if (!nullPhysics) {
      if (steer.contains(SL.Thick(SL.XY))) {
       steerXY.setXY((steer.contains(SL.Thick(SL.fromYZ)) ? V.P.speedYZ : V.P.speedXZ) * steerAngleMultiply);
      }
      if (wheel) {
       steerYZ.setYZ(side > 0 ? V.P.wheelSpin[0] : side < 0 ? V.P.wheelSpin[1] : 0);
      } else if (steer.contains(SL.Thick(SL.YZ))) {
       steerYZ.setYZ((steer.contains(SL.Thick(SL.fromYZ)) ? -V.P.speedYZ : V.P.speedXZ) * steerAngleMultiply);//<-Master script
      }
      if (steer.contains(SL.Thick(SL.XZ))) {
       steerXZ.setXZ((steer.contains(SL.Thick(SL.fromYZ)) && !steer.contains(SL.Thick(SL.fromXZ)) ? -V.P.speedYZ : V.P.speedXZ) * steerAngleMultiply);//<-Master script
      } else if (vehicleTurret) {//<-May fail if the vehicle is never assigned a vehicle turret
       steerXZ.setXZ(V.VT.XZ);
       if (vehicleTurretBarrel) {
        steerYZ.setYZ(V.VT.YZ);
       }
      }
     }
     if (spinner) {
      steerXZ.setXZ(V.spinner.XZ);//<-May fail if the vehicle is never assigned a 'spinner' special
     }
     U.inert.set();
     matrix.set(U.inert.multiply(steerXY).multiply(steerYZ).multiply(steerXZ).multiply(V.rotation));
    }
    if (!V.destroyed) {
     if (fireLight) {
      U.setTranslate(pointLight, this);
      U.Nodes.Light.setRGB(pointLight, 1, .5 + U.random(.25), 0);
      U.Nodes.Light.add(pointLight);
     } else if (light) {
      if (brightness != V.lightBrightness) {
       brightness = V.lightBrightness;
       setBrightness();
      }
      if (brightness > 0) {
       U.setTranslate(pointLight, this);
       U.Nodes.Light.add(pointLight);
      }
     }
    }
    if (U.render(this, -renderRadius)) {
     if (thrust != null) {
      PM.setSelfIlluminationMap(thrust == Thrust.white ? Images.white :
      thrust == Thrust.blue ? Effects.blueJet() :
      Effects.fireLight());
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
      PM.setSelfIlluminationMap(V.destroyed ? null : Effects.blink());
     }
    }
   }
  }
 }

 public void throwChip(double power) {
  if (chip != null) {
   chip.deploy(V, power);
  }
 }

 void deform(double inDamage) {
  if (!noDeformation) {
   damage.setAxis(new Point3D(U.random(), U.random(), U.random()));
   damage.setAngle(U.randomPlusMinus(inDamage));
  }
 }

 public void setDrawMode(boolean forceWireframe) {
  MV.setDrawMode(forceWireframe ? DrawMode.LINE : defaultDrawMode);
 }
}
