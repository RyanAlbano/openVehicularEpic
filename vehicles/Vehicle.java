package ve.vehicles;

import java.io.*;
import java.util.*;

import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import ve.*;
import ve.Camera;
import ve.effects.*;
import ve.environment.*;
import ve.trackElements.Checkpoint;
import ve.trackElements.Point;
import ve.trackElements.TE;
import ve.trackElements.trackParts.TrackPart;
import ve.trackElements.trackParts.TrackPlane;
import ve.utilities.*;

public class Vehicle extends Instance {

 public AI AI;
 final VehicleAudio VA;
 public final List<VehiclePart> parts = new ArrayList<>();
 private Sphere nukeBlastSphere;
 private double nukeBlastSphereSize;
 private double cameraShake;
 public double cameraShakeXZ, cameraShakeYZ;
 final PhongMaterial fixSpherePM = new PhongMaterial();
 public PointLight burnLight;
 public final int index;
 public int checkpointsPassed;
 public int point;
 int engineClipQuantity;
 public final double height;
 double steerAngleMultiply = 1;
 public final double[] accelerationStages = new double[2];
 public final double[] topSpeeds = {0, 0, Double.POSITIVE_INFINITY};
 public double turnRate;
 double maxTurn;
 private double randomTurn;
 private double brake;
 double grip;
 private double drag;
 private double bounce;
 double shockAbsorb = Double.NaN;
 public double airAcceleration;
 private double airTopSpeed;
 double airPush;
 private boolean aerialControlEnhanced;
 private double sidewaysLandingAngle;
 private double pushesOthers, getsPushed;
 private double liftsOthers, getsLifted;
 public final double[] damageDealt = new double[4];
 private double turretBaseDamageDealt;
 public double durability, fragility;
 public double selfRepair;
 private double spin;
 public double speedBoost;
 double othersAvoidAt;
 Engine engine = Engine.none;
 public double driverViewY, driverViewZ;
 public double extraViewHeight;
 public double lightBrightness;
 private double explosionDiameter, explosionDamage, explosionPush;
 public double speed, speedXZ, speedYZ, stallSpeed;
 double minimumFlightSpeedWithoutStall;
 double netSpeed;
 public double cameraXZ;
 private double lastXZ;
 private double airSpinXZ;
 public double stuntXY, stuntYZ, stuntXZ;
 public double stuntTimer;
 private double stuntSpeedYZ, stuntSpeedXY, stuntSpeedXZ;
 public double flipTimer, stuntReward, stuntLandWaitTime = 8;
 public double damage;
 private double destructTimer;
 private double massiveHitTimer;
 public double spinnerXZ;
 public double spinnerSpeed = Double.NaN;
 private double chuffTimer, forceTimer, crashTimer;
 double splashing = Double.NaN;
 double exhaust = Double.NaN;
 public double screenFlash;
 private double nukeBlastX, nukeBlastY, nukeBlastZ;
 private double wheelGapFrontToBack, wheelGapLeftToRight;
 final double[] wheelSpin = new double[2];
 double localVehicleGround;
 final boolean realVehicle;
 boolean landStuntsBothSides;
 private boolean turnDrag;
 private boolean steerInPlace;
 public boolean amphibious;
 public boolean phantomEngaged;
 public boolean drive, reverse, turnL, turnR, handbrake;
 private boolean passBonus;
 public boolean hasTurret;
 public double vehicleTurretPivotZ;
 double vehicleTurretPivotY;
 private final double[] turretVerticalRanges = new double[2];
 public double vehicleTurretXZ, vehicleTurretYZ;
 private double vehicleTurretSpeedXZ, vehicleTurretSpeedYZ;
 public boolean turretAutoAim;
 public boolean driverInVehicleTurret;
 boolean drive2, reverse2;
 boolean vehicleTurretL, vehicleTurretR;
 public boolean boost;
 private boolean steerByMouse;
 boolean onFire;
 public boolean inDriverView;
 public boolean flipped;
 private boolean gotStunt;
 public boolean offTheEdge;
 public boolean destroyed;
 private boolean onAntiGravity;
 private boolean inTornado, onVolcano;
 private boolean atPoolXZ;
 public boolean inPool;
 private boolean reviveImmortality;
 public boolean thrusting;
 boolean skidding;
 boolean scraping;
 private boolean wheelDiscord;
 public final boolean[] rollCheck = new boolean[2], flipCheck = new boolean[2], spinCheck = new boolean[2];
 private boolean[] gotNukeBlasted;
 boolean wrathEngaged;
 boolean[] wrathStuck;
 boolean inWrath;
 private long spinMultiplyPositive = 1, spinMultiplyNegative = 1;
 public long vehicleHit = -1;
 long explosionsWhenDestroyed;
 public long destructionType;
 long polarity;
 ve.vehicles.AI.Behavior behavior;
 private long engineStage, lastEngineStage;
 double engineTuneRatio = 2, enginePitchBase = 1;
 public String vehicleName = "";
 Landing landType = Landing.tires;
 Contact contact = Contact.none;
 public ExplosionType explosionType = ExplosionType.none;
 public String terrainProperties = "";
 public Mode mode = Mode.drive;
 public Type vehicleType = Type.vehicle;
 boolean floats;
 public final List<Wheel> wheels = new ArrayList<>();
 private final List<Dust> dusts = new ArrayList<>();
 private int currentDust;
 private final List<Splash> splashes = new ArrayList<>();
 private int currentSplash;
 private final List<FixSphere> fixSpheres = new ArrayList<>();
 public final List<Special> specials = new ArrayList<>();
 private static final long maxSpecials = 3;
 private static final double fromAngleToVelocityConstant = .2;
 boolean hasShooting;
 public final List<Explosion> explosions = new ArrayList<>();
 int currentExplosion;
 private final Collection<NukeBlast> nukeBlasts = new ArrayList<>();

 public enum Mode {
  drive, neutral, stunt, fly, drivePool
 }

 public enum Type {vehicle, aircraft, turret}

 enum Contact {none, rubber, metal}

 enum Landing {tires, touch, crash}

 enum Engine {
  none, normal, tiny, agera, aventador, veyron, chiron, hotrod, huayra, laferrari, minicooper, p1, s7, turboracer,
  retro, electric,
  smalltruck, bigtruck, authentictruck, monstertruck, humvee, tank, smallcraft, turbo, power, massive, train,
  smallprop, bigprop,
  jet, brightjet, powerjet, torchjet, jetfighter, turbine, smallrocket, rocket, bigrocket, turborocket
 }

 EngineTuning engineTuning = EngineTuning.equalTemperament;

 //Engine tunings are here since they must load before VehicleAudio is an object
 enum EngineTuning {equalTemperament, harmonicSeries}

 public enum ExplosionType {none, normal, nuclear, maxnuclear}

 public Vehicle(int model, int listIndex, boolean isReal) {
  this(model, listIndex, isReal, true);
 }

 public Vehicle(int model, int listIndex, boolean isReal, boolean show) {
  modelNumber = model;
  index = listIndex;
  realVehicle = isReal;
  VA = realVehicle ? new VehicleAudio(this) : null;
  modelName = VE.vehicleModels.get(modelNumber);
  int n;
  theRandomColor[0] = index == VE.userPlayer ? VE.userRandomRGB[0] : U.random();
  theRandomColor[1] = index == VE.userPlayer ? VE.userRandomRGB[1] : U.random();
  theRandomColor[2] = index == VE.userPlayer ? VE.userRandomRGB[2] : U.random();
  behavior = ve.vehicles.AI.Behavior.adapt;
  int wheelCount = 0;
  for (n = 4; --n >= 0; ) {
   wheels.add(new Wheel(this));
  }
  long lightsAdded = 0;
  boolean onModelPart = false, addWheel = false;
  List<Double> xx = new ArrayList<>(), yy = new ArrayList<>(), zz = new ArrayList<>();
  double[] translate = new double[3];
  double[] RGB = {0, 0, 0};
  double pivotX = 0, pivotY = 0, pivotZ = 0;
  StringBuilder type = new StringBuilder(), wheelType = new StringBuilder(), rimType = new StringBuilder();
  String textureType = "", wheelTextureType = "", s = "";
  try (BufferedReader BR = new BufferedReader(new InputStreamReader(getFile(modelName), U.standardChars))) {
   for (String s1; (s1 = BR.readLine()) != null; ) {
    s = s1.trim();
    if (s.startsWith("<>")) {
     if (!show) {
      break;
     }
     onModelPart = true;
     addWheel = false;
     xx.clear();
     yy.clear();
     zz.clear();
     type.setLength(0);
     textureType = "";
     pivotX = pivotY = pivotZ = 0;
    } else if (s.startsWith("><") && onModelPart) {//<-Redundant?
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
      double wheelAverageZ = (minimumZ + maximumZ) * .5;
      int wheelNumber = averageX < 0 && wheelAverageZ > 0 ? 0 : averageX > 0 && wheelAverageZ > 0 ? 1 : averageX < 0 && wheelAverageZ < 0 ? 2 : averageX > 0 && wheelAverageZ < 0 ? 3 : Integer.MAX_VALUE;
      wheels.get(wheelNumber).pointX = averageX;
      wheels.get(wheelNumber).pointZ = wheelAverageZ;
      wheels.get(wheelNumber).sparkPoint = Math.abs(minimumZ - maximumZ);
      wheels.get(wheelNumber).skidmarkSize = Math.abs(minimumX - maximumX) * .5;
      wheelCount++;
     }
     if (!xx.isEmpty()) {
      parts.add(new VehiclePart(this, U.listToArray(xx), U.listToArray(yy), U.listToArray(zz), xx.size(), RGB, String.valueOf(type), textureType, pivotX, pivotY, pivotZ));
      xx.clear();
     }
     onModelPart = false;
    }
    getLoadColor(s, RGB);
    if (onModelPart) {
     if (s.startsWith("(")) {
      xx.add((U.getValue(s, 0) * modelSize * instanceSize * modelScale[0] * instanceScale[0]) + translate[0]);
      yy.add((U.getValue(s, 1) * modelSize * instanceSize * modelScale[1] * instanceScale[1]) + translate[1]);
      zz.add((U.getValue(s, 2) * modelSize * instanceSize * modelScale[2] * instanceScale[2]) + translate[2]);
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
       lightsAdded++;
      }
      type.append(s.startsWith("reflect") ? " reflect " : "");
      type.append(s.startsWith("thrustWhite") ? " thrustWhite " : s.startsWith("thrustBlue") ? " thrustBlue " : s.startsWith("thrust") ? " thrust " : "");
      type.append(s.startsWith("selfIlluminate") ? " selfIlluminate " : "");
      type.append(s.startsWith("blink") ? " blink " : "");
      type.append(s.startsWith("noSpecular") ? " noSpecular " : s.startsWith("shiny") ? " shiny " : "");
      type.append(s.startsWith("smokePoint") ? " smokePoint " : "");
      type.append(s.startsWith("thrustTrailPoint") ? " thrustTrailPoint " : "");
      type.append(s.startsWith("noTexture") ? " noTexture " : "");
      type.append(s.startsWith("flick1") ? " flick1 " : s.startsWith("flick2") ? " flick2 " : "");
      type.append(s.startsWith("landingGear") ? " landingGear " : "");
      type.append(s.startsWith("turretBarrel") ? " turretBarrel " : s.startsWith("turret") ? " turret " : "");
      type.append(s.startsWith("spinner") ? " spinner " : "");
      type.append(s.startsWith("noCrush") ? " noCrush " : "");
      type.append(s.startsWith("driver") ? " driver " : "");
      if (s.startsWith("controller")) {
       type.append(" controller ").append(s.contains("XZ") ? " steerXZ " : s.contains("XY") ? " steerXY " : "");
      } else if (s.startsWith("wheel")) {
       type.append(" wheel ");
       addWheel = s.startsWith("wheelPoint") || addWheel;
      } else if (s.startsWith("steer")) {
       type.append(s.startsWith("steerXY") ? " steerXY " : s.startsWith("steerYZ") ? " steerYZ " : U.startsWith(s, "steerXZ", "steers") ? " steerXZ " : "");
       type.append(s.startsWith("steerFromYZ") ? " steerFromYZ " : s.startsWith("steerFromXZ") ? " steerFromXZ " : "");
      } else if (s.startsWith("pivot(")) {
       pivotX = U.getValue(s, 0) * modelSize * instanceSize * modelScale[0] * instanceScale[0];
       pivotY = U.getValue(s, 1) * modelSize * instanceSize * modelScale[1] * instanceScale[1];
       pivotZ = U.getValue(s, 2) * modelSize * instanceSize * modelScale[2] * instanceScale[2];
      }
      type.append(s.startsWith("shake") ? " shake " : "");
      type.append(s.startsWith("line") ? " line " : "");
      type.append(s.startsWith("conic") ? " conic " : "");
      type.append(s.startsWith("cylindric") ? " cylindric " : "");
      type.append(s.startsWith("strip") ? " strip " : "");
      type.append(s.startsWith("squares") ? " squares " : "");
      type.append(s.startsWith("triangles") ? " triangles " : "");
      type.append(s.startsWith("base") ? " base " : "");
      type.append(s.startsWith("exterior") ? " exterior " : "");
     }
    }
    vehicleName = s.startsWith("name(") ? U.getString(s, 0) : vehicleName;
    VE.vehicleMaker = s.startsWith("maker(") ? U.getString(s, 0) : VE.vehicleMaker;
    if (s.startsWith("type(")) {
     vehicleType = s.contains("turret") ? Type.turret : s.contains("aircraft") ? Type.aircraft : vehicleType;
     floats = s.contains("floats") || floats;
    } else if (s.startsWith("acceleration(")) {
     accelerationStages[0] = U.getValue(s, 0);
     accelerationStages[1] = U.getValue(s, 1);
    } else if (s.startsWith("speeds(")) {
     topSpeeds[0] = U.getValue(s, 0);
     topSpeeds[1] = U.getValue(s, 1);
     topSpeeds[0] = topSpeeds[0] < 0 ? -Long.MAX_VALUE : topSpeeds[0];
     topSpeeds[1] = topSpeeds[1] < 0 ? Long.MAX_VALUE : topSpeeds[1];
     try {
      topSpeeds[2] = Math.abs(U.getValue(s, 2));
     } catch (RuntimeException ignored) {
     }
    }
    turnRate = s.startsWith("turnRate(") ? Math.max(0, U.getValue(s, 0)) : turnRate;
    maxTurn = s.startsWith("maxTurn(") ? U.getValue(s, 0) : maxTurn;
    randomTurn = s.startsWith("randomTurn(") ? U.getValue(s, 0) : randomTurn;
    brake = s.startsWith("brake(") ? U.getValue(s, 0) : brake;
    grip = s.startsWith("grip(") ? U.getValue(s, 0) < 0 ? Double.POSITIVE_INFINITY : U.getValue(s, 0) : grip;
    drag = s.startsWith("drag(") ? U.getValue(s, 0) : drag;
    if (s.startsWith("bounce(")) {
     bounce = U.getValue(s, 0);
     try {
      shockAbsorb = U.clamp(U.getValue(s, 1));
     } catch (RuntimeException ignored) {
     }
    } else if (s.startsWith("airRotate(")) {
     airAcceleration = U.getValue(s, 0) < 0 ? Double.POSITIVE_INFINITY : U.getValue(s, 0);
     airTopSpeed = U.getValue(s, 1);
    }
    airPush = s.startsWith("airPush(") ? U.getValue(s, 0) : airPush;
    aerialControlEnhanced = s.startsWith("aerialControlEnhanced(yes") || aerialControlEnhanced;
    sidewaysLandingAngle = s.startsWith("sidewaysLandingAngle(") ? U.getValue(s, 0) : sidewaysLandingAngle;
    landStuntsBothSides = s.startsWith("landStuntsBothSides(yes") || landStuntsBothSides;
    pushesOthers = s.startsWith("pushesOthers(") ? U.getValue(s, 0) : pushesOthers;
    getsPushed = s.startsWith("getsPushed(") ? U.getValue(s, 0) : getsPushed;
    liftsOthers = s.startsWith("liftsOthers(") ? U.getValue(s, 0) : liftsOthers;
    getsLifted = s.startsWith("getsLifted(") ? U.getValue(s, 0) : getsLifted;
    if (s.startsWith("damageDealt(")) {
     try {
      for (n = 4; --n >= 0; ) {
       damageDealt[n] = U.getValue(s, n);
      }
     } catch (RuntimeException E) {
      damageDealt[0] = damageDealt[1] = damageDealt[2] = damageDealt[3] = U.getValue(s, 0);
     }
    }
    turretBaseDamageDealt = s.startsWith("baseDamageDealt(") ? U.getValue(s, 0) : turretBaseDamageDealt;
    turretBaseY = s.startsWith("baseY(") ? U.getValue(s, 0) : turretBaseY;
    durability = s.startsWith("durability(") ? U.getValue(s, 0) : durability;
    fragility = s.startsWith("fragility(") ? U.getValue(s, 0) : fragility;
    selfRepair = s.startsWith("selfRepair(") ? U.getValue(s, 0) : selfRepair;
    spin = s.startsWith("spin(") ? U.getValue(s, 0) : spin;
    turnDrag = s.startsWith("turnDrag(yes") || turnDrag;
    steerInPlace = s.startsWith("steerInPlace(yes") || steerInPlace;
    speedBoost = s.startsWith("speedBoost(") ? U.getValue(s, 0) : speedBoost;
    if (s.startsWith("landType(") && !U.getString(s, 0).isEmpty()) {
     landType = Landing.valueOf(U.getString(s, 0));
    } else if (s.startsWith("contact(") && !U.getString(s, 0).isEmpty()) {
     contact = Contact.valueOf(U.getString(s, 0));
    }
    exhaust = s.startsWith("exhaustFire(yes") ? 0 : exhaust;
    othersAvoidAt = s.startsWith("othersAvoidAt(") ? U.getValue(s, 0) : othersAvoidAt;
    if (s.startsWith("engine(")) {
     engine = Engine.valueOf(U.getString(s, 0));
     try {
      engineClipQuantity = (int) Math.round(U.getValue(s, 1));
      engineTuneRatio = U.getValue(s, 2);
      enginePitchBase = U.getValue(s, 3);
     } catch (RuntimeException ignored) {
     }
     engineTuning = s.contains("harmonicSeries") ? EngineTuning.harmonicSeries : engineTuning;
    }
    amphibious = s.startsWith("amphibious(yes") || amphibious;
    driverViewY = s.startsWith("driverViewY(") ? U.getValue(s, 0) * modelSize * instanceSize + translate[1] : driverViewY;
    driverViewZ = s.startsWith("driverViewZ(") ? U.getValue(s, 0) * modelSize * instanceSize + translate[2] : driverViewZ;
    driverViewX = s.startsWith("driverViewX(") ? Math.abs(U.getValue(s, 0) * modelSize * instanceSize) + translate[0] : driverViewX;
    extraViewHeight = s.startsWith("extraViewHeight(") ? U.getValue(s, 0) : extraViewHeight;
    behavior = s.startsWith("behavior(race") ? ve.vehicles.AI.Behavior.race : s.startsWith("behavior(fight") ? ve.vehicles.AI.Behavior.fight : behavior;
    if (s.startsWith("special(") && !U.getString(s, 0).isEmpty()) {
     addSpecial(s);
    }
    addSpecialProperties(s);
    if (s.startsWith("explosion(") && !U.getString(s, 0).isEmpty()) {
     explosionType = ExplosionType.valueOf(U.getString(s, 0));
    }
    explosionsWhenDestroyed = s.startsWith("explodeWhenDestroyed(") ? Math.round(U.getValue(s, 0)) : explosionsWhenDestroyed;
    if (s.startsWith("vehicleTurret(")) {
     hasTurret = true;
     turretVerticalRanges[0] = U.getValue(s, 0);
     turretVerticalRanges[1] = U.getValue(s, 1);
     vehicleTurretPivotZ = U.getValue(s, 2) * modelSize * instanceSize * modelScale[2] * instanceScale[2];
     vehicleTurretPivotY = U.getValue(s, 3) * modelSize * instanceSize * modelScale[1] * instanceScale[1];
     turretAutoAim = s.contains("autoAim") || turretAutoAim;
     driverInVehicleTurret = s.contains("driverViewInside") || driverInVehicleTurret;
    }
    modelProperties += s.startsWith("mapTerrain") ? " mapTerrain " : "";
    getSizeScaleTranslate(s, translate);
    if (s.startsWith("wheelColor(")) {
     if (s.contains("reflect")) {
      wheelType.append(" reflect ");
     } else {
      try {
       wheelRGB[0] = U.getValue(s, 0);
       wheelRGB[1] = U.getValue(s, 1);
       wheelRGB[2] = U.getValue(s, 2);
      } catch (RuntimeException E) {
       if (s.contains("theRandomColor")) {
        wheelRGB[0] = theRandomColor[0];
        wheelRGB[1] = theRandomColor[1];
        wheelRGB[2] = theRandomColor[2];
        wheelType.append(" theRandomColor ");
       } else {
        wheelRGB[0] = wheelRGB[1] = wheelRGB[2] = U.getValue(s, 0);
       }
      }
     }
     wheelType.append(s.contains("noSpecular") ? " noSpecular " : s.contains("shiny") ? " shiny " : "");
    } else if (s.startsWith("rims(")) {
     if (!show) {
      break;
     }
     rimType.setLength(0);
     rimRadius = U.getValue(s, 0) * modelSize * instanceSize;
     rimDepth = Math.max(rimRadius * .0625, U.getValue(s, 1) * modelSize * instanceSize);
     try {
      rimRGB[0] = U.getValue(s, 2);
      rimRGB[1] = U.getValue(s, 3);
      rimRGB[2] = U.getValue(s, 4);
     } catch (RuntimeException E) {
      if (s.contains("theRandomColor")) {
       rimRGB[0] = theRandomColor[0];
       rimRGB[1] = theRandomColor[1];
       rimRGB[2] = theRandomColor[2];
       rimType.append(" theRandomColor ");
      } else {
       rimRGB[0] = rimRGB[1] = rimRGB[2] = U.getValue(s, 2);
      }
     }
     rimType.append(s.contains("reflect") ? " reflect " : "");
     rimType.append(s.contains("noSpecular") ? " noSpecular " : s.contains("shiny") ? " shiny " : "");
     rimType.append(s.contains("sport") ? " sport " : "");
    }
    wheelType.append(s.startsWith("landingGearWheels") ? " landingGear " : "");
    wheelTextureType = s.startsWith("wheelTexture(") ? U.getString(s, 0) : wheelTextureType;//<-Using 'append' would mess this up if found more than once in file
    wheelSmoothing = s.startsWith("smoothing(") ? U.getValue(s, 0) * modelSize : wheelSmoothing;
    if (s.startsWith("wheel(")) {
     if (!show) {
      break;
     }
     if (wheelCount < 4) {
      wheels.get(wheelCount).pointX = U.getValue(s, 0) * modelSize * instanceSize * modelScale[0] * instanceScale[0];
      wheels.get(wheelCount).pointZ = U.getValue(s, 2) * modelSize * instanceSize * modelScale[2] * instanceScale[2];
      wheels.get(wheelCount).skidmarkSize = Math.abs(U.getValue(s, 3)) * modelSize * instanceSize * modelScale[0] * instanceScale[0];
      wheels.get(wheelCount).sparkPoint = U.getValue(s, 4) * 2 * modelSize * instanceSize;
     }
     String side = U.getValue(s, 0) > 0 ? " R " : U.getValue(s, 0) < 0 ? " L " : U.random() < .5 ? " R " : " L ";
     loadWheel(this, null, U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2), U.getValue(s, 3), U.getValue(s, 4), wheelType + side, String.valueOf(rimType), wheelTextureType, s.contains("steers"), s.contains("hide"));
     wheelCount++;
    }
    steerAngleMultiply = s.startsWith("steerAngleMultiply(") ? U.getValue(s, 0) : steerAngleMultiply;
   }
  } catch (IOException e) {
   System.out.println(U.modelLoadingError + e);
   System.out.println("At File: " + model);
   System.out.println("At Line: " + s);
  }
  if (lightsAdded < 1) {
   parts.add(new VehiclePart(this, new double[1], new double[1], new double[1], 1, new double[]{1, 1, 1}, " light ", ""));
  }
  maxMinusX[1] /= vertexQuantity;
  maxPlusX[1] /= vertexQuantity;
  maxMinusY[1] /= vertexQuantity;
  maxPlusY[1] /= vertexQuantity;
  maxMinusZ[1] /= vertexQuantity;
  maxPlusZ[1] /= vertexQuantity;
  height = Math.abs(maxMinusY[0]) + Math.abs(maxPlusY[0]) + Math.abs(maxMinusY[1]) + Math.abs(maxPlusY[1]);
  absoluteRadius = Math.abs(maxMinusX[0]) + Math.abs(maxPlusX[0]) + Math.abs(maxMinusZ[0]) + Math.abs(maxPlusZ[0]) + Math.abs(maxMinusX[1]) + Math.abs(maxPlusX[1]) + Math.abs(maxMinusZ[1]) + Math.abs(maxPlusZ[1]) + height;
  collisionRadius = absoluteRadius * .3;
  if (vehicleType == Type.turret) {
   getsPushed = getsLifted = -1;
   behavior = ve.vehicles.AI.Behavior.fight;
  } else if (vehicleType == Type.aircraft) {
   stuntLandWaitTime = 1;
   minimumFlightSpeedWithoutStall = floats ? 0 : E.gravity * (engine == Engine.smallprop ? .25 : .5) * 100;
  }
  for (Special special : specials) {
   if (special.type == Special.Type.spinner) {
    spinnerSpeed = 0;
    break;
   }
  }
  explosionType = explosionsWhenDestroyed > 0 && !explosionType.name().contains(ExplosionType.nuclear.name()) ? ExplosionType.normal : explosionType;
  X = Y = Z = XZ = 0;
  for (VehiclePart part : parts) {
   U.add(part.MV);
  }
  if (realVehicle) {
   if (E.poolExists || !E.tsunamiParts.isEmpty()) {
    for (n = E.splashQuantity; --n >= 0; ) {
     splashes.add(new Splash());
    }
   }
   for (Wheel wheel : wheels) {
    for (n = 50; --n >= 0; ) {
     wheel.sparks.add(new Spark());
    }
   }
   for (VehiclePart part : parts) {
    if (part.thrustTrails != null) {
     for (ThrustTrail trail : part.thrustTrails) {
      U.add(trail.B);
     }
    }
   }
   if (contact == Contact.rubber || E.terrain.contains(" snow ")) {
    RGB[0] = wheelRGB[0] * .5;
    RGB[1] = wheelRGB[1] * .5;
    RGB[2] = wheelRGB[2] * .5;
    if (!wheelTextureType.isEmpty()) {
     RGB[0] *= .333;
     RGB[1] *= .333;
     RGB[2] *= .333;
    }
    for (Wheel wheel : wheels) {
     wheel.skidmarks = new ArrayList<>();
     for (n = 48; --n >= 0; ) {
      wheel.skidmarks.add(new Skidmark(wheel, RGB));
     }
    }
   }
   U.setDiffuseRGB(fixSpherePM, 1, 1, 1, .25);
   for (n = 32; --n >= 0; ) {
    fixSpheres.add(new FixSphere(this));
   }
   if (VE.defaultVehicleLightBrightness > 0) {
    burnLight = new PointLight();
   }
   if (explosionType == ExplosionType.maxnuclear) {
    nukeBlastSphere = new Sphere(1);
    PhongMaterial nukeBlastPM = new PhongMaterial();//<-More details later
    nukeBlastSphere.setMaterial(nukeBlastPM);
    PhongMaterial PM = new PhongMaterial();
    U.setSpecularRGB(PM, 0, 0, 0);
    PM.setSelfIlluminationMap(U.getImage("white"));
    for (n = 1000; --n >= 0; ) {
     nukeBlasts.add(new NukeBlast(PM));
    }
   }
   matchStartPlacement();
   loadVehicle();
   if (vehicleType != Type.turret) {
    for (n = E.dustQuantity; --n >= 0; ) {
     dusts.add(new Dust());
    }
   }
   for (VehiclePart part : parts) {
    if (part.smokes != null) {
     for (Smoke smoke : part.smokes) {
      U.add(smoke.C);
     }
    }
   }
  }
  Quaternion baseXZ = new Quaternion(0, U.sin(XZ * .5), 0, U.cos(XZ * .5)),
  baseYZ = new Quaternion(-U.sin(YZ * .5), 0, 0, U.cos(YZ * .5)),
  baseXY = new Quaternion(0, 0, -U.sin(XY * .5), U.cos(XY * .5));
  rotation = baseXY.multiply(baseYZ).multiply(baseXZ);
 }

 private void addSpecial(String s) {
  if (specials.size() < maxSpecials) {
   specials.add(new Special(this));
   Special S = specials.get(specials.size() - 1);
   S.type = Special.Type.valueOf(U.getString(s, 0));
   if (S.type != Special.Type.particledisintegrator && S.type != Special.Type.spinner) {
    String specialAudio = "";
    try {
     specialAudio = U.getString(s, 1);
    } catch (RuntimeException ignored) {
    }
    S.sound = new Sound(S.type.name() + specialAudio);
   }
   S.homing = s.contains("homing") || S.homing;
   S.aimType = s.contains("autoAim") || specials.size() > 2 ? Special.AimType.auto : s.contains("ofVehicleTurret") ? Special.AimType.ofVehicleTurret : S.aimType;
  }
 }

 private void addSpecialProperties(String s) {
  if (!specials.isEmpty() && specials.size() <= maxSpecials) {
   Special S = specials.get(specials.size() - 1);
   if (s.startsWith("gunY(")) {
    for (int n1 = 0; n1 < E.shotQuantity; n1++) {
     try {
      S.ports.add(new Port());
      S.ports.get(n1).Y = U.getValue(s, S.type == Special.Type.shotgun ? 0 : n1) * modelSize * instanceSize;
     } catch (RuntimeException e) {
      S.ports.remove(S.ports.size() - 1);
      break;
     }
    }
   } else if (s.startsWith("gunX(")) {
    try {
     for (Port port : S.ports) {
      port.X = U.getValue(s, S.ports.indexOf(port)) * modelSize * instanceSize;
     }
    } catch (RuntimeException E) {
     for (Port port : S.ports) {
      port.X = U.getValue(s, 0) * modelSize * instanceSize;
     }
    }
   } else if (s.startsWith("gunZ(")) {
    try {
     for (Port port : S.ports) {
      port.Z = U.getValue(s, S.ports.indexOf(port)) * modelSize * instanceSize;
     }
    } catch (RuntimeException E) {
     for (Port port : S.ports) {
      port.Z = U.getValue(s, 0) * modelSize * instanceSize;
     }
    }
   } else if (s.startsWith("gunXZ(")) {
    try {
     for (Port port : S.ports) {
      port.XZ = U.getValue(s, S.ports.indexOf(port));
     }
    } catch (RuntimeException E) {
     for (Port port : S.ports) {
      port.XZ = U.getValue(s, 0);
     }
    }
   } else if (s.startsWith("gunYZ(")) {
    try {
     for (Port port : S.ports) {
      port.YZ = U.getValue(s, S.ports.indexOf(port));
     }
    } catch (RuntimeException E) {
     for (Port port : S.ports) {
      port.YZ = U.getValue(s, 0);
     }
    }
   }
   if (s.startsWith("gunRandomPosition(")) {
    S.randomPosition = U.getValue(s, 0) * modelSize * instanceSize;
   } else if (s.startsWith("gunRandomAngle(")) {
    S.randomAngle = U.getValue(s, 0);
   }
  }
 }

 public void runGraphics(boolean gamePlay) {
  while (YZ < -180) YZ += 360;
  while (YZ > 180) YZ -= 360;
  while (XY < -180) XY += 360;
  while (XY > 180) XY -= 360;
  while (XZ < -180) XZ += 360;
  while (XZ > 180) XZ -= 360;
  distanceToCamera = U.distance(this);
  for (VehiclePart part : parts) {
   part.setPosition(gamePlay);
  }
  if (E.renderType.name().contains(E.RenderType.fullDistance.name()) || distanceToCamera < E.viewableMapDistance + collisionRadius) {
   onFire = VE.mapName.equals("the Sun") || onFire;
   rotation.set();
   rotation.multiply(0, 0, -U.sin(XY * .5), U.cos(XY * .5));//<-Mind the multiply order!
   rotation.multiply(-U.sin(YZ * .5), 0, 0, U.cos(YZ * .5));
   rotation.multiply(0, U.sin(XZ * .5), 0, U.cos(XZ * .5));
   for (VehiclePart part : parts) {
    part.render();
   }
  }
  double smokeEmitProbability = destroyed ? 0 : (mode.name().contains(Mode.drive.name()) || mode == Mode.neutral) && (drive || reverse) ? 1 : .25;
  for (VehiclePart part : parts) {
   if (part.smokes != null) {
    if (U.random() < smokeEmitProbability) {
     part.smokes.get(part.currentSmoke).deploy(part);
     part.currentSmoke = ++part.currentSmoke >= E.smokeQuantity ? 0 : part.currentSmoke;
    }
    for (Smoke smoke : part.smokes) {
     smoke.run();
    }
   }
   if (part.thrustTrails != null) {
    for (ThrustTrail trail : part.thrustTrails) {
     trail.run(this, part);
    }
   }
   part.MV.setVisible(part.visible);
   part.visible = false;
   if (part.chip != null) {
    part.chip.run(this, gamePlay);
   }
   if (part.flame != null) {
    part.flame.run(this);
   }
  }
  for (Wheel wheel : wheels) {
   for (Spark spark : wheel.sparks) {
    spark.run(gamePlay);
   }
  }
  for (Dust dust : dusts) {
   dust.run();
  }
  for (Splash splash : splashes) {
   splash.run();
  }
  long fixSpheresRemoved = 0;
  for (FixSphere fixSphere : fixSpheres) {
   fixSphere.run(this);
   fixSpheresRemoved += fixSphere.stage <= 0 ? 1 : 0;
  }
  reviveImmortality = fixSpheresRemoved < fixSpheres.size() && reviveImmortality;
  for (Wheel wheel : wheels) {
   if (wheel.skidmarks != null) {
    for (Skidmark skidmark : wheel.skidmarks) {
     skidmark.run();
    }
   }
  }
  if (damage > durability && VE.status == VE.Status.play && explosionType != ExplosionType.maxnuclear) {
   long reviveTimer = 0, multiple = explosionType.name().startsWith(ExplosionType.nuclear.name()) ? 2 : 1;
   for (VehiclePart part : parts) {
    if ((part.explodeTimer += U.random(VE.tick)) > 75 * multiple) {
     part.explodeStage = 4;
     reviveTimer++;
    }
   }
   if (reviveTimer >= parts.size()) {
    fix(gamePlay);
    reviveImmortality = true;
   }
  }
  if (VE.defaultVehicleLightBrightness > 0 && !parts.isEmpty() && parts.get(0).explodeStage > 0 && distanceToCamera < E.viewableMapDistance) {
   U.setLightRGB(burnLight, .5, .25 + U.random(.2), U.random(.125));
   U.setTranslate(burnLight, this);
   U.addLight(burnLight);
  }
 }

 private void deployDust(Wheel wheel) {
  if (!destroyed && !phantomEngaged && !terrainProperties.contains(" ice ")) {
   double dustSpeed = U.netValue(wheel.speedX, wheel.speedZ), speedDifference = flipped ? dustSpeed : Math.abs(Math.abs(speed) - dustSpeed);
   if (speedDifference * .5 > 10 + U.random(5.) || (!flipped && Math.abs(speed) > 50 + U.random(50.))) {
    dusts.get(currentDust).deploy(this, wheel, dustSpeed, speedDifference);
    currentDust = ++currentDust >= E.dustQuantity ? 0 : currentDust;
   }
  }
 }

 public void checkPlayer() {
  if (index == VE.userPlayer && Network.mode == Network.Mode.OFF && VE.status != VE.Status.replay) {
   drive = VE.keyUp;
   reverse = VE.keyDown;
   turnL = VE.keyLeft;
   turnR = VE.keyRight;
   handbrake = VE.keySpace;
   boost = VE.keyBoost;
   passBonus = VE.keyPassBonus;
   if (!turretAutoAim) {
    drive2 = VE.keyW;
    reverse2 = VE.keyS;
    vehicleTurretL = VE.keyA;
    vehicleTurretR = VE.keyD;
   }
   for (Special special : specials) {
    if (special.aimType != Special.AimType.auto) {
     boolean shootWithCanceledSteer = special.aimType != Special.AimType.normal && special.type != Special.Type.mine && vehicleTurretL && vehicleTurretR;
     special.fire = VE.keySpecial[specials.indexOf(special)] || shootWithCanceledSteer;
    }
   }
  }
 }

 private void runCrash(double power) {
  power = Math.abs(power * fragility);
  if (power > 10) {
   damage += Math.abs(power * 2 * VE.tick);
   for (VehiclePart part : parts) {
    part.deform();
    part.throwChip(power);
   }
   if (damage <= durability && crashTimer < 1) {
    if (power > 30) {
     VA.randomCrashSound = U.randomize(VA.randomCrashSound, VA.crashHard.clips.size());
     VA.crashHard.play(VA.randomCrashSound, VA.vehicleToCameraSoundDistance);
    } else {
     VA.randomCrashSound = U.randomize(VA.randomCrashSound, VA.crashSoft.clips.size());
     VA.crashSoft.play(VA.randomCrashSound, VA.vehicleToCameraSoundDistance);
    }
    if (bounce > .9) {
     VA.land.play(U.random(landType == Landing.tires ? 2 : 1), VA.vehicleToCameraSoundDistance);
    }
    crashTimer = 2;
   }
  }
 }

 public void runCollisions() {
  boolean replay = VE.status == VE.Status.replay, greenTeam = index < VE.vehiclesInMatch >> 1;
  if (!phantomEngaged) {
   double netDamage;
   if (!destroyed && !reviveImmortality) {
    for (Vehicle otherV : VE.vehicles) {
     if (!U.sameVehicle(this, otherV) && !U.sameTeam(this, otherV) && !otherV.destroyed && !otherV.reviveImmortality && !otherV.phantomEngaged) {
      boolean aHit = false;
      for (int n = 4; --n >= 0; ) {
       Wheel W = wheels.get(n), otherW = otherV.wheels.get(n);
       double collideAt = (collisionRadius + otherV.collisionRadius) * .5,
       behindX = W.X - (netSpeedX * VE.tick),
       behindY = W.Y - (netSpeedY * VE.tick),
       behindZ = W.Z - (netSpeedZ * VE.tick),
       averageX = behindX * .5,
       averageY = behindY * .5,
       averageZ = behindZ * .5;
       if (
       ((U.distance(averageY, otherW.Y, averageZ, otherW.Z) < collideAt || U.distance(behindY, otherW.Y, behindZ, otherW.Z) < collideAt) && ((W.X > otherW.X && behindX < otherW.X) || (W.X < otherW.X && behindX > otherW.X))) ||//<-inBoundsX
       ((U.distance(averageX, otherW.X, averageY, otherW.Y) < collideAt || U.distance(behindX, otherW.X, behindY, otherW.Y) < collideAt) && ((W.Z > otherW.Z && behindZ < otherW.Z) || (W.Z < otherW.Z && behindZ > otherW.Z))) ||//<-inBoundsZ
       ((U.distance(averageX, otherW.X, averageZ, otherW.Z) < collideAt || U.distance(behindX, otherW.X, behindZ, otherW.Z) < collideAt) && ((W.Y > otherW.Y && behindY < otherW.Y) || (W.Y < otherW.Y && behindY > otherW.Y))) ||//<-inBoundsY
       U.distance(W, otherW) < collideAt) {
        W.speedY -= getsLifted > 0 && Y < otherV.Y ? E.gravity * 1.5 * VE.tick : 0;
        netDamage = Math.abs(netSpeed - otherV.netSpeed) * otherV.damageDealt[n] * .3;//<-Damage now RECEIVING from other vehicles--not vice versa
        if (damage <= durability) {
         VE.scoreDamage[greenTeam ? 1 : 0] += replay ? 0 : netDamage;//<-Team assignment swapped because damage is RECEIVING
        }
        hitCheck(otherV);
        double pushThemX = 0, pushThemZ = 0, pushYouX = 0, pushYouZ = 0;
        if (otherV.damage <= otherV.durability && getsPushed >= otherV.getsPushed) {
         pushThemX = Math.max(0, getsPushed) * (otherW.speedX - W.speedX) * .25;
         pushThemZ = Math.max(0, getsPushed) * (otherW.speedZ - W.speedZ) * .25;
        }
        if (damage <= durability && getsPushed <= otherV.getsPushed) {
         pushYouX = Math.max(0, pushesOthers) * (W.speedX - otherW.speedX) * .25;
         pushYouZ = Math.max(0, pushesOthers) * (W.speedZ - otherW.speedZ) * .25;
        }
        if (getsPushed >= otherV.getsPushed) {
         if (
         ((X > otherV.X || W.X > otherW.X) && W.speedX < otherW.speedX) ||
         ((X < otherV.X || W.X < otherW.X) && W.speedX > otherW.speedX)) {
          W.hitOtherX = grip > 100 ? otherV.netSpeedX : otherW.speedX;
         }
         if (
         ((Z > otherV.Z || W.Z > otherW.Z) && W.speedZ < otherW.speedZ) ||
         ((Z < otherV.Z || W.Z < otherW.Z) && W.speedZ > otherW.speedZ)) {
          W.hitOtherZ = grip > 100 ? otherV.netSpeedZ : otherW.speedZ;
         }
        }
        otherW.speedX += pushYouX;
        otherW.speedZ += pushYouZ;
        W.speedX -= pushThemX;
        W.speedZ -= pushThemZ;
        if (getsLifted > 0 && Y != otherV.Y) {
         W.speedY -= (Y < otherV.Y ? getsLifted : -getsLifted) * .0025 * Math.abs(U.netValue(netSpeedX, netSpeedZ) - U.netValue(otherV.netSpeedX, otherV.netSpeedZ));
        }
        runCrash(netDamage);//<-Crashing self now, not the colidee
        if (W.speedY < -100) {
         VA.land();
        }
        if (otherV.getsLifted > 0 && otherV.Y != Y) {
         otherW.speedY -= (otherV.Y < Y ? liftsOthers : -liftsOthers) * .0025 * Math.abs(U.netValue(netSpeedX, netSpeedZ) - U.netValue(otherV.netSpeedX, otherV.netSpeedZ));
        }
        aHit = true;
       }
      }
      if (aHit) {
       if (explosionType.name().contains(ExplosionType.nuclear.name())) {
        damage += durability + Double.MIN_VALUE;
        otherV.damage += otherV.durability + Double.MIN_VALUE;
        VE.scoreDamage[greenTeam ? 0 : 1] += replay ? 0 : otherV.durability;
        setCameraShake(50);
       }
       if (vehicleType != Type.aircraft && damageDealt[U.random(4)] >= 100 && (massiveHitTimer <= 0 || otherV.damage <= otherV.durability)) {
        otherV.damage += otherV.durability + Double.MIN_VALUE;
        VE.scoreDamage[greenTeam ? 0 : 1] += replay ? 0 : otherV.durability;
        VA.massiveHit.play(U.random(VA.massiveHit.clips.size()), VA.vehicleToCameraSoundDistance);
        massiveHitTimer = U.random(5.);
        for (VehiclePart part : otherV.parts) {
         part.deform();
         part.throwChip(U.randomPlusMinus(Math.abs(netSpeed - otherV.netSpeed) * .5));
        }
        setCameraShake(30);
       }
      }
      if (!Double.isNaN(spinnerSpeed) && U.distance(this, otherV) < renderRadius + otherV.collisionRadius) {//<-'renderRadius' best if spinner has largest diameter of the vehicle
       spinnerHit(otherV);
      }
     }
    }
   }
   for (Special special : specials) {
    for (Vehicle vehicle : VE.vehicles) {
     if (!U.sameVehicle(this, vehicle) && !U.sameTeam(this, vehicle) && (!vehicle.destroyed || wrathEngaged) && !vehicle.reviveImmortality && !vehicle.phantomEngaged) {
      double diameter = special.type == Special.Type.mine ? U.netValue(vehicle.netSpeedX, vehicle.netSpeedY, vehicle.netSpeedZ) : special.diameter;
      for (Shot shot : special.shots) {
       if (shot.stage > 0 && shot.hit < 1 && (shot.doneDamaging == null || !shot.doneDamaging[vehicle.index]) && (special.type != Special.Type.missile || vehicle.damage <= vehicle.durability) && !(special.type == Special.Type.mine && (U.distance(shot, vehicle) > 2000 || vehicle.damage > vehicle.durability))) {
        double amount = special.diameter + vehicle.collisionRadius, shotAverageX = (shot.X + shot.behindX) * .5, shotAverageY = (shot.Y + shot.behindY) * .5, shotAverageZ = (shot.Z + shot.behindZ) * .5;
        if (
        ((U.distance(shotAverageY, vehicle.Y, shotAverageZ, vehicle.Z) < amount || U.distance(shot.behindY, vehicle.Y, shot.behindZ, vehicle.Z) < amount) && ((shot.X > vehicle.X && shot.behindX < vehicle.X) || (shot.X < vehicle.X && shot.behindX > vehicle.X))) ||//<-inBoundsX
        ((U.distance(shotAverageX, vehicle.X, shotAverageY, vehicle.Y) < amount || U.distance(shot.behindX, vehicle.X, shot.behindY, vehicle.Y) < amount) && ((shot.Z > vehicle.Z && shot.behindZ < vehicle.Z) || (shot.Z < vehicle.Z && shot.behindZ > vehicle.Z))) ||//<-inBoundsZ
        ((U.distance(shotAverageX, vehicle.X, shotAverageZ, vehicle.Z) < amount || U.distance(shot.behindX, vehicle.X, shot.behindZ, vehicle.Z) < amount) && ((shot.Y > vehicle.Y && shot.behindY < vehicle.Y) || (shot.Y < vehicle.Y && shot.behindY > vehicle.Y))) ||//<-inBoundsY
        U.distance(shot, vehicle) < diameter + vehicle.collisionRadius) {
         hitCheck(vehicle);
         double shotDamage = special.damageDealt;
         if (special.type == Special.Type.raygun || special.type == Special.Type.flamethrower || special.type == Special.Type.thewrath || special.type.name().contains(Special.Type.blaster.name())) {
          shotDamage /= special.type == Special.Type.flamethrower ? Math.max(1, shot.stage) : 1;
          shotDamage *= VE.tick;
         } else if (special.type != Special.Type.forcefield) {
          shot.hit = 1;
         }
         if ((vehicle.damage += shotDamage) <= vehicle.durability && !replay) {
          VE.scoreDamage[greenTeam ? 0 : 1] += shotDamage;
          if (U.distance(vehicle, this) < U.distance(vehicle, VE.vehicles.get(vehicle.AI.target))) {
           vehicle.AI.target = index;
          }
         }
         if (special.pushPower > 0) {
          if (vehicle.getsPushed >= 0) {
           for (Wheel wheel : vehicle.wheels) {
            wheel.speedX += U.randomPlusMinus(special.pushPower);
            wheel.speedZ += U.randomPlusMinus(special.pushPower);
           }
          }
          if (vehicle.getsLifted >= 0 && (special.type == Special.Type.forcefield || U.contains(special.type.name(), Special.Type.shell.name(), Special.Type.missile.name()))) {
           for (Wheel wheel : vehicle.wheels) {
            wheel.speedY += U.randomPlusMinus(special.pushPower);
           }
          }
         }
         for (VehiclePart part : vehicle.parts) {
          part.deform();
          if (special.type != Special.Type.particledisintegrator) {
           part.throwChip(U.randomPlusMinus(shot.speed * .5));
          }
         }
         double shotToCameraSoundDistance = Math.sqrt(U.distance(shot)) * .08;
         if (special.useSmallHits) {
          VA.hitShot.play(U.random(VA.hitShot.clips.size()), shotToCameraSoundDistance);
         }
         if (special.type == Special.Type.heavymachinegun || special.type == Special.Type.blaster) {
          VA.hitShot.play(U.random(7), shotToCameraSoundDistance);
         } else if (special.type == Special.Type.heavyblaster || special.type == Special.Type.thewrath) {//<-These specials don't load hitExplosive audio, so don't call!
          VA.crashDestroy.play(U.random(VA.crashDestroy.clips.size()), shotToCameraSoundDistance);
         } else if (special.type.name().contains(Special.Type.shell.name()) || special.type == Special.Type.missile || special.type == Special.Type.bomb) {
          VA.crashDestroy.play(U.random(VA.crashDestroy.clips.size()), shotToCameraSoundDistance);
          VA.hitExplosive.play(U.random(VA.hitExplosive.clips.size()), shotToCameraSoundDistance);
         } else if (special.type == Special.Type.railgun) {
          for (int n = 4; --n >= 0; ) {
           VA.crashHard.play(U.random(VA.crashHard.clips.size()), shotToCameraSoundDistance);
          }
         } else if (special.type == Special.Type.forcefield) {
          VA.crashHard.play(U.random(VA.crashHard.clips.size()), VA.vehicleToCameraSoundDistance);
          VA.crashHard.play(U.random(VA.crashHard.clips.size()), VA.vehicleToCameraSoundDistance);
          VA.crashHard.play(U.random(VA.crashHard.clips.size()), VA.vehicleToCameraSoundDistance);
         } else if (special.type == Special.Type.mine) {
          VA.mineExplosion.play(shotToCameraSoundDistance);
         }
         if (U.random() < .25 && special.ricochets) {
          VA.hitRicochet.play(U.random(VA.hitRicochet.clips.size()), shotToCameraSoundDistance);
         }
         if (shot.doneDamaging != null) {
          shot.doneDamaging[vehicle.index] = true;
         }
        }
       }
      }
      if (vehicleType == Type.turret && VE.bonusHolder < 0 && damage <= durability) {
       double bonusX = TE.bonusX, bonusY = TE.bonusY, bonusZ = TE.bonusZ, radius = special.diameter + TE.bonusBig.getRadius();
       for (Shot shot : special.shots) {
        if (shot.stage > 0) {
         double shotAverageX = (shot.X + shot.behindX) * .5, shotAverageY = (shot.Y + shot.behindY) * .5, shotAverageZ = (shot.Z + shot.behindZ) * .5;
         if (
         ((U.distance(shotAverageY, bonusY, shotAverageZ, bonusZ) < radius || U.distance(shot.behindY, bonusY, shot.behindZ, bonusZ) < radius) && ((shot.X > bonusX && shot.behindX < bonusX) || (shot.X < bonusX && shot.behindX > bonusX))) ||//<-inBoundsX
         ((U.distance(shotAverageX, bonusX, shotAverageY, bonusY) < radius || U.distance(shot.behindX, bonusX, shot.behindY, bonusY) < radius) && ((shot.Z > bonusZ && shot.behindZ < bonusZ) || (shot.Z < bonusZ && shot.behindZ > bonusZ))) ||//<-inBoundsZ
         ((U.distance(shotAverageX, bonusX, shotAverageZ, bonusZ) < radius || U.distance(shot.behindX, bonusX, shot.behindZ, bonusZ) < radius) && ((shot.Y > bonusY && shot.behindY < bonusY) || (shot.Y < bonusY && shot.behindY > bonusY))) ||//<-inBoundsY
         U.distance(shot.X, bonusX, shot.Y, bonusY, shot.Z, bonusZ) < radius) {
          TE.setBonusHolder(this);
         }
        }
       }
      }
      if (special.homing) {
       for (Shot shot : special.shots) {
        if (shot.stage > 0) {
         int shotTarget = VE.userPlayer;
         double compareDistance = Double.POSITIVE_INFINITY;
         for (Vehicle otherVehicle : VE.vehicles) {
          if (!U.sameVehicle(this, otherVehicle) && !U.sameTeam(this, otherVehicle) && !otherVehicle.destroyed && U.distance(shot, otherVehicle) < compareDistance) {
           shotTarget = otherVehicle.index;
           compareDistance = U.distance(shot, otherVehicle);
          }
         }
         shot.homeXZ = (VE.vehicles.get(shotTarget).X < shot.X ? 90 : VE.vehicles.get(shotTarget).X > shot.X ? -90 : 0) + U.arcTan((VE.vehicles.get(shotTarget).Z - shot.Z) / (VE.vehicles.get(shotTarget).X - shot.X));
         while (Math.abs(shot.XZ - shot.homeXZ) > 180) {
          shot.homeXZ += shot.homeXZ < shot.XZ ? 360 : -360;
         }
         shot.XZ -= shot.XZ > shot.homeXZ ? 10 * VE.tick : 0;
         shot.XZ += shot.XZ < shot.homeXZ ? 10 * VE.tick : 0;
         double distance = U.netValue(VE.vehicles.get(shotTarget).Z - shot.Z, VE.vehicles.get(shotTarget).X - shot.X);
         shot.homeYZ = VE.vehicles.get(shotTarget).Y < shot.Y ? -(-90 - U.arcTan(distance / (VE.vehicles.get(shotTarget).Y - shot.Y))) : VE.vehicles.get(shotTarget).Y > shot.Y ? -(90 - U.arcTan(distance / (VE.vehicles.get(shotTarget).Y - shot.Y))) : shot.homeYZ;
         shot.YZ -= shot.homeYZ < shot.YZ ? 10 * VE.tick : 0;
         shot.YZ += shot.homeYZ > shot.YZ || shot.Y > -special.diameter * .5 - (VE.vehicles.get(shotTarget).vehicleType == Type.turret ? VE.vehicles.get(shotTarget).turretBaseY : VE.vehicles.get(shotTarget).clearanceY) ? 10 * VE.tick : 0;
        }
       }
      }
      if (wrathEngaged && (U.distance(this, vehicle) < absoluteRadius + netSpeed || wrathStuck[vehicle.index])) {
       if (vehicle.getsPushed >= 0) {
        vehicle.X = X;
        vehicle.Y = Y;
        vehicle.Z = Z;
        for (int n = 4; --n >= 0; ) {
         vehicle.wheels.get(n).speedX = wheels.get(n).speedX;
         vehicle.wheels.get(n).speedY = wheels.get(n).speedY;
         vehicle.wheels.get(n).speedZ = wheels.get(n).speedZ;
        }
        for (VehiclePart part : vehicle.parts) {
         part.X = X + U.randomPlusMinus(absoluteRadius);
         part.Y = Y + U.randomPlusMinus(absoluteRadius);
         part.Z = Z + U.randomPlusMinus(absoluteRadius);
         part.explodeStage = Math.min(part.explodeStage, 2);
         part.explodeGravitySpeed = 0;
        }
        vehicle.inWrath = true;
        wrathStuck[vehicle.index] = true;
       }
       vehicle.damage += vehicle.durability + 1;
      }
     }
    }
   }
   if (explosionType != ExplosionType.none) {
    boolean nuclear = explosionType.name().contains(ExplosionType.nuclear.name());
    explosionDiameter = nuclear ? U.random(20000.) : explosionDiameter;
    explosionDamage = nuclear ? 2500 + U.random(5000.) : explosionDamage;
    for (Vehicle vehicle : VE.vehicles) {
     if (!U.sameVehicle(this, vehicle) && !U.sameTeam(this, vehicle) && !vehicle.destroyed && !vehicle.reviveImmortality && !vehicle.phantomEngaged) {
      for (Explosion explosion : explosions) {
       if (explosion.stage > 0 && U.distance(explosion, vehicle) < vehicle.collisionRadius + explosionDiameter) {
        if (!explosion.doneDamaging[vehicle.index]) {
         hitCheck(vehicle);
         vehicle.damage += explosionDamage;
         if (!nuclear) {
          if (vehicle.damage <= vehicle.durability && !replay) {
           VE.scoreDamage[greenTeam ? 0 : 1] += explosionDamage;
          }
          explosion.doneDamaging[vehicle.index] = true;
         }
        }
        if (vehicle.getsPushed >= 0) {
         for (Wheel wheel : vehicle.wheels) {
          wheel.speedX += U.randomPlusMinus(explosionPush);
          wheel.speedZ += U.randomPlusMinus(explosionPush);
         }
        }
        if (vehicle.getsLifted >= 0) {
         for (Wheel wheel : vehicle.wheels) {
          wheel.speedY += U.randomPlusMinus(explosionPush);
         }
        }
        for (VehiclePart part : vehicle.parts) {
         part.deform();
         part.throwChip(U.randomPlusMinus(500.));
        }
        if (nuclear) {//<-Is any of this needed?
         //VA.hitExplosive.play(U.random(VA.hitExplosive.clips.size()), vehicleToCameraSoundDistance);
         VA.crashDestroy.play(U.random(VA.crashDestroy.clips.size()), VA.vehicleToCameraSoundDistance);
        }
       }
      }
     }
    }
   }
   if (!destroyed) {
    if (vehicleType == Type.turret) {
     for (Vehicle vehicle : VE.vehicles) {
      if (!U.sameVehicle(this, vehicle) && !U.sameTeam(this, vehicle) && !vehicle.destroyed && !vehicle.reviveImmortality && U.distance(X, vehicle.X, Y + (turretBaseY * .5), vehicle.Y, Z, vehicle.Z) < collisionRadius * .5 + vehicle.collisionRadius && !vehicle.phantomEngaged) {
       hitCheck(vehicle);
       if (vehicle.fragility > 0) {
        vehicle.damage += turretBaseDamageDealt * vehicle.fragility;
        VE.scoreDamage[greenTeam ? 0 : 1] += replay ? 0 : turretBaseDamageDealt * vehicle.fragility;
       }
       for (Wheel wheel : vehicle.wheels) {
        wheel.speedX += U.randomPlusMinus(500.);
        wheel.speedZ += U.randomPlusMinus(500.);
        wheel.speedY += U.randomPlusMinus(200.);
       }
       for (VehiclePart part : vehicle.parts) {
        if (vehicle.fragility > 0) {
         part.deform();
        }
        part.throwChip(U.randomPlusMinus(vehicle.netSpeed));
       }
       VA.crashHard.play(U.random(4), VA.vehicleToCameraSoundDistance);
       VA.crashHard.play(U.random(4), VA.vehicleToCameraSoundDistance);
       VA.crashHard.play(U.random(4), VA.vehicleToCameraSoundDistance);
      }
     }
    }
    if (!reviveImmortality) {
     for (Special special : specials) {
      if (special.fire && special.type == Special.Type.particledisintegrator) {
       for (Vehicle vehicle : VE.vehicles) {
        if (!U.sameVehicle(this, vehicle) && !U.sameTeam(this, vehicle) && !vehicle.destroyed && !vehicle.reviveImmortality && !vehicle.phantomEngaged) {
         if (
         ((vehicle.Y <= Y && U.sin(YZ) >= 0) || (vehicle.Y >= Y && U.sin(YZ) <= 0) || Math.abs(vehicle.Y - Y) < vehicle.collisionRadius) &&//<-inY
         ((vehicle.X <= X && U.sin(XZ) >= 0) || (vehicle.X >= X && U.sin(XZ) <= 0)) &&//<-inX
         ((vehicle.Z <= Z && U.cos(XZ) <= 0) || (vehicle.Z >= Z && U.cos(XZ) >= 0)))//<-inZ
         {
          hitCheck(vehicle);
          vehicle.damage += 10 * VE.tick;
          VE.scoreDamage[greenTeam ? 0 : 1] += replay ? 0 : 10 * VE.tick;
         }
        }
       }
      }
     }
     if (E.lightningExists && E.lightningStrikeStage < 1) {
      double distance = U.distance(X, E.lightningX, Z, E.lightningZ);
      if (Y >= E.stormCloudY && distance < collisionRadius * 6) {
       damage += durability * .5 + (distance < collisionRadius * 2 ? durability : 0);
       for (VehiclePart part : parts) {
        part.deform();
        part.throwChip(U.randomPlusMinus(500.));
       }
       VA.crashHard.play(U.random(VA.crashHard.clips.size()), VA.vehicleToCameraSoundDistance);
       VA.crashHard.play(U.random(VA.crashHard.clips.size()), VA.vehicleToCameraSoundDistance);
       VA.crashHard.play(U.random(VA.crashHard.clips.size()), VA.vehicleToCameraSoundDistance);
      }
     }
     for (Fire fire : E.fires) {
      double distance = U.distance(this, fire);
      if (distance < collisionRadius + fire.absoluteRadius) {
       damage += 10 * VE.tick;
       if (distance * 2 < collisionRadius + fire.absoluteRadius) {
        damage += 10 * VE.tick;
       }
       for (VehiclePart part : parts) {
        part.deform();
       }
      }
     }
     for (Boulder boulder : E.boulders) {
      if (U.distance(X, boulder.X, Z, boulder.Z) < collisionRadius + boulder.S.getRadius() && Y > boulder.Y - collisionRadius - boulder.S.getRadius()) {
       damage += durability + 1;
       for (VehiclePart part : parts) {
        part.deform();
        part.throwChip(U.randomPlusMinus(boulder.speed));
       }
       VA.crashDestroy.play(U.random(VA.crashDestroy.clips.size()), VA.vehicleToCameraSoundDistance);
      }
     }
     for (VolcanoRock volcanoRock : E.volcanoRocks) {
      double vehicleVolcanoRockDistance = U.distance(this, volcanoRock);
      if (vehicleVolcanoRockDistance < (collisionRadius + volcanoRock.S.getRadius()) * 1.5) {
       damage += durability * .5 + (vehicleVolcanoRockDistance < collisionRadius + volcanoRock.S.getRadius() ? durability : 0);
       for (VehiclePart part : parts) {
        part.deform();
        part.throwChip(U.randomPlusMinus(U.netValue(volcanoRock.speedX, volcanoRock.speedY, volcanoRock.speedZ)));
       }
       VA.crashDestroy.play(U.random(VA.crashDestroy.clips.size()), VA.vehicleToCameraSoundDistance);
      }
     }
     for (Meteor meteor : E.meteors) {
      double vehicleMeteorDistance = U.distance(this, meteor.meteorParts.get(0));
      if (vehicleMeteorDistance < (collisionRadius + meteor.meteorParts.get(0).S.getRadius()) * 4) {
       damage += durability * .5;
       for (Wheel wheel : wheels) {
        wheel.speedX += U.randomPlusMinus(E.meteorSpeed * .5);
        wheel.speedZ += U.randomPlusMinus(E.meteorSpeed * .5);
       }
       if (vehicleMeteorDistance < collisionRadius + meteor.meteorParts.get(0).S.getRadius() * 2) {
        damage += durability;
        for (Wheel wheel : wheels) {
         wheel.speedX += U.randomPlusMinus(E.meteorSpeed * .5);
         wheel.speedZ += U.randomPlusMinus(E.meteorSpeed * .5);
        }
       }
       for (VehiclePart part : parts) {
        part.deform();
        part.throwChip(U.randomPlusMinus(U.netValue(meteor.speedX, E.meteorSpeed, meteor.speedZ)));
       }
       VA.crashDestroy.play(U.random(VA.crashDestroy.clips.size()), VA.vehicleToCameraSoundDistance);
      }
     }
    }
   }
  }
  if (damage > durability) {
   if (explosionType == ExplosionType.maxnuclear) {
    for (Vehicle vehicle : VE.vehicles) {
     if (!U.sameVehicle(this, vehicle) && !U.sameTeam(this, vehicle) && !gotNukeBlasted[vehicle.index] && !vehicle.reviveImmortality && U.distance(nukeBlastX, vehicle.X, nukeBlastY, vehicle.Y, nukeBlastZ, vehicle.Z) < nukeBlastSphereSize + vehicle.collisionRadius && !vehicle.phantomEngaged) {
      hitCheck(vehicle);
      vehicle.damage += vehicle.durability + 1;
      VE.scoreDamage[greenTeam ? 0 : 1] += replay ? 0 : vehicle.durability;
      if (vehicle.vehicleType != Type.turret) {
       double blastSpeedX = vehicle.getsPushed >= 0 ? (vehicle.X > nukeBlastX ? 6000 : vehicle.X < nukeBlastX ? -6000 : 0) * (1 + U.random(.5)) : 0,
       blastSpeedZ = vehicle.getsPushed >= 0 ? (vehicle.Z > nukeBlastZ ? 6000 : vehicle.Z < nukeBlastZ ? -6000 : 0) * (1 + U.random(.5)) : 0,
       blastSpeedY = vehicle.getsLifted >= 0 ? (vehicle.Y > nukeBlastY ? 6000 : vehicle.Y < nukeBlastY ? -6000 : 0) * (1 + U.random(.5)) : 0;
       for (Wheel wheel : vehicle.wheels) {
        wheel.speedX += blastSpeedX;
        wheel.speedZ += blastSpeedZ;
        wheel.speedY += blastSpeedY;
       }
      }
      double soundDistance = Math.sqrt(U.distance(vehicle)) * .08;
      VA.crashDestroy.play(U.random(VA.crashDestroy.clips.size()), soundDistance);
      VA.crashDestroy.play(U.random(VA.crashDestroy.clips.size()), soundDistance);
      VA.crashDestroy.play(U.random(VA.crashDestroy.clips.size()), soundDistance);
      gotNukeBlasted[vehicle.index] = true;
     }
    }
   }
   if (destructionType < 1) {
    String s = Network.mode == Network.Mode.OFF ? vehicleName : VE.playerNames[index];
    destructionType = 1;
    for (Vehicle vehicle : VE.vehicles) {
     if (!U.sameVehicle(this, vehicle) && !U.sameTeam(this, vehicle) && vehicleHit == vehicle.index && vehicle.vehicleHit == index) {
      destructionType = 2;
      VE.updateDestructionNames();
      String s1 = Network.mode == Network.Mode.OFF ? vehicle.vehicleName : VE.playerNames[vehicle.index];
      VE.destructionNames[4][0] = s1;
      VE.destructionNames[4][1] = s;
      VE.destructionNameColors[4][0] = vehicle.index < VE.vehiclesInMatch >> 1 ? Color.color(0, 1, 0) : Color.color(1, 0, 0);
      VE.destructionNameColors[4][1] = greenTeam ? Color.color(0, 1, 0) : Color.color(1, 0, 0);
     }
    }
    if (destructionType == 1) {
     VE.updateDestructionNames();
     VE.destructionNames[4][0] = s;
     VE.destructionNames[4][1] = "";
     VE.destructionNameColors[4][0] = greenTeam ? Color.color(0, 1, 0) : Color.color(1, 0, 0);
    }
   }
  } else {
   destructionType = 0;
  }
 }

 private void hitCheck(Vehicle vehicle) {
  vehicleHit = damage <= durability ? vehicle.index : vehicleHit;
  vehicle.vehicleHit = vehicle.damage <= vehicle.durability ? index : vehicle.vehicleHit;
 }

 private void loadVehicle() {
  cameraXZ = XZ;
  for (Wheel wheel : wheels) {
   wheel.X = X;
   wheel.Y = Y;
   wheel.Z = Z;
  }
  wheelGapFrontToBack = Math.max(Math.abs(wheels.get(0).pointZ - wheels.get(2).pointZ), Math.abs(wheels.get(1).pointZ - wheels.get(3).pointZ));
  wheelGapLeftToRight = Math.max(Math.abs(wheels.get(0).pointX - wheels.get(1).pointX), Math.abs(wheels.get(2).pointX - wheels.get(3).pointX));
  for (Special special : specials) {
   special.time();
  }
  AI = new AI(this);
  loadWeapons();
  VA.load(this);
  double volcanoDistance = U.distance(X, E.volcanoX, Z, E.volcanoZ);
  onVolcano = E.volcanoExists && volcanoDistance < E.volcanoBottomRadius && volcanoDistance > E.volcanoTopRadius && Y > -E.volcanoBottomRadius + volcanoDistance;
  Y = onVolcano ? Math.min(Y, -E.volcanoBottomRadius + volcanoDistance) - (vehicleType == Type.turret ? turretBaseY : 0) : Y;
  atPoolXZ = E.poolExists && U.distance(X, E.poolX, Z, E.poolZ) < E.pool[0].getRadius();
  inPool = atPoolXZ && Y + clearanceY > 0;
  localVehicleGround = E.groundLevel + (atPoolXZ ? E.poolDepth : 0);
  lightBrightness = VE.defaultVehicleLightBrightness;
 }

 private void loadWeapons() {
  int n;
  for (Special special : specials) {
   special.load();
  }
  if (!explosionType.name().contains(ExplosionType.nuclear.name())) {
   explosionDiameter = 500;
   explosionDamage = 250;
  }
  explosionPush = 500;
  if (explosionType != ExplosionType.none) {
   for (n = E.explosionQuantity; --n >= 0; ) {
    explosions.add(new Explosion(this));
   }
  }
  gotNukeBlasted = explosionType == ExplosionType.maxnuclear ? new boolean[VE.vehiclesInMatch] : null;
 }

 private void matchStartPlacement() {
  Y = XY = YZ = 0;
  X = U.random(Math.min(50000., E.mapBounds.right)) + U.random(Math.max(-50000., E.mapBounds.left));
  Z = U.random(Math.min(50000., E.mapBounds.forward)) + U.random(Math.max(-50000., E.mapBounds.backward));
  XZ = vehicleType != Type.turret && VE.randomStartAngle ? U.randomPlusMinus(180.) : 0;//<-Turrets ALWAYS face forward for less confusing placement
  if (VE.mapName.equals("Vicious Versus V3") && VE.vehiclesInMatch > 1) {
   boolean green = index < VE.vehiclesInMatch >> 1;
   if (green) {
    Z = -10000;
    XZ = 0;
   } else {
    Z = 10000;
    XZ = 180;
   }
   if (VE.vehiclesInMatch < 3) {
    X = 0;
   } else {
    X = (index - (green ? 0 : (VE.vehiclesInMatch * .5))) * 2000;
    X -= 2000 * (VE.vehiclesInMatch * .5) * .5 - 1000;
   }
  } else if (VE.mapName.equals("Moonlight")) {
   if (damageDealt[U.random(4)] < 100 && vehicleType != Type.turret) {
    X *= X < 0 ? -1 : 1;
    Z *= Z < 0 ? -1 : 1;
   }
  } else if (VE.mapName.equals("the Test of Damage")) {
   if (damageDealt[U.random(4)] < 100 && vehicleType != Type.turret) {
    X = U.random(E.mapBounds.right);
    Z = U.random(E.mapBounds.backward);
   }
  } else if (VE.mapName.equals("Vehicular Falls")) {
   Y -= 100000;
   if (vehicleType != Type.turret) {
    Z = U.random(-10000.) + U.random(30000.);
    X = 0;
   }
  } else if (VE.mapName.equals("Highlands")) {
   X = U.randomPlusMinus(100000);
   Z = U.randomPlusMinus(100000);
   Y = -175000;
  } else if (VE.mapName.equals("Circle Race XL")) {
   Z += 320000;
  } else if (VE.mapName.equals("XY Land")) {
   X = vehicleType == Type.turret ? X : U.random(23000.) - U.random(25000.);
  } else if (VE.mapName.equals("Matrix 2x3")) {
   if (!explosionType.name().contains(ExplosionType.nuclear.name()) && vehicleType != Type.turret) {
    X = U.randomPlusMinus(14000.);
    Z = -U.random(31000.);
   }
  } else if (VE.mapName.equals("Cold Fury")) {
   Y = -4000;
  } else if (VE.mapName.equals("the Tunnel of Doom")) {
   if (!explosionType.name().contains(ExplosionType.nuclear.name()) && vehicleType != Type.turret) {
    X = U.randomPlusMinus(700.);
    Z = U.random(6000.) - U.random(10000.);
   }
  } else if (VE.mapName.equals("Everybody Everything")) {
   X = U.random() < .5 ? -2000 : 2000;
   Z = U.randomPlusMinus(20000.);
  } else if (VE.mapName.equals("the Maze")) {
   if (vehicleType != Type.turret) {
    X = Z = 0;
   }
  } else if (VE.mapName.equals("Volcanic Prophecy")) {
   X *= 2;
   Z *= 2;
  } else if (VE.mapName.equals("V.E. Speedway 2000000")) {
   boolean random = U.random() < .5;
   XZ = random ? 180 : 0;
   Z += random ? 1000000 : -1000000;
  } else if (VE.mapName.equals("Ghost City")) {
   X *= 4;
   Z *= 4;
   if (vehicleType != Type.turret) {
    Y = -1000;
    X = U.random() < .5 ? 2000 : -2000;
   }
  } else if (VE.mapName.equals("Open Ocean")) {
   X *= 4;
   Z *= 4;
   if (vehicleType != Type.turret) {
    X = U.random() < .5 ? 2000 : -2000;
   }
  } else if (VE.mapName.equals("SUMMIT of EPIC")) {
   X = !explosionType.name().contains(ExplosionType.nuclear.name()) && vehicleType != Type.turret ? 0 : X;
   boolean random = U.random() < .5;
   XZ = random ? 180 : 0;
   Z = random ? 1050000 : -1050000;
   Z += U.randomPlusMinus(25000.);
  } else if (VE.mapName.equals("Parallel Universe Portal")) {
   Z = 0;
  }
  if (E.gravity == 0) {
   if (VE.mapName.equals("Outer Space V1")) {
    X = U.randomPlusMinus(500.);
    Z = U.random(2000.) - U.random(4000.);
    if (explosionType.name().contains(ExplosionType.nuclear.name())) {
     X = 0;
     Z = 100500;
    }
    if (vehicleType == Type.turret) {
     X = U.randomPlusMinus(50000.);
     Z = U.randomPlusMinus(50000.);
     Y = U.randomPlusMinus(50000.);
    }
   } else {
    Y = U.randomPlusMinus(50000.);
   }
   if (VE.mapName.equals("Outer Space V3")) {
    Y = 0;
    double[] setX = {0}, setZ = {50000};
    U.rotate(setX, setZ, U.random(360.));
    X = setX[0];
    Z = setZ[0];
   }
  }
  if (VE.mapName.equals("Black Hole")) {
   X = Y = Z = 0;
  }
  Y -= vehicleType == Type.turret ? 0 : clearanceY;
 }

 public void setTurretY() {
  if (vehicleType == Type.turret) {
   Y = 0;
   if (!U.listEquals(VE.mapName, "Everybody Everything", "Devil's Stairwell")) {
    E.setTerrainSit(this, true);
   }
   Y -= turretBaseY;
  }
 }

 public void physicsVehicle() {
  int n;
  boolean replay = VE.status == VE.Status.replay;
  if (E.volcanoExists) {
   double volcanoDistance = U.distance(X, E.volcanoX, Z, E.volcanoZ);
   onVolcano = volcanoDistance < E.volcanoBottomRadius && volcanoDistance > E.volcanoTopRadius && Y > -E.volcanoBottomRadius + volcanoDistance;
  } else {
   onVolcano = false;
  }
  atPoolXZ = E.poolExists && U.distance(X, E.poolX, Z, E.poolZ) < E.pool[0].getRadius();
  vehicleHit = damage <= durability ? -1 : vehicleHit;
  netSpeedX = (wheels.get(0).speedX + wheels.get(1).speedX + wheels.get(2).speedX + wheels.get(3).speedX) * .25;
  netSpeedY = (wheels.get(0).speedY + wheels.get(1).speedY + wheels.get(2).speedY + wheels.get(3).speedY) * .25;
  netSpeedZ = (wheels.get(0).speedZ + wheels.get(1).speedZ + wheels.get(2).speedZ + wheels.get(3).speedZ) * .25;
  netSpeed = U.netValue(netSpeedX, netSpeedY, netSpeedZ);
  polarity = Math.abs(YZ) > 90 ? -1 : 1;
  flipped = (Math.abs(XY) > 90 && Math.abs(YZ) <= 90) || (Math.abs(YZ) > 90 && Math.abs(XY) <= 90);
  double clearance = flipped && !landStuntsBothSides ? -absoluteRadius * .075 : clearanceY;
  inPool = atPoolXZ && Y + clearance > 0;
  if (mode.name().startsWith(Mode.drive.name())) {
   stuntSpeedYZ = stuntSpeedXY = stuntSpeedXZ = 0;
  }
  if (!destroyed) {
   runPhysicsAirEngage();
   if (mode == Mode.stunt) {
    runPhysicsAerialControl();
   } else {
    runPhysicsDrivePower();
   }
  }
  runPhysicsWheelSpin();
  double turnAmount = maxTurn + U.random(randomTurn);
  if (!replay) {
   runPhysicsSteering(turnAmount);
   runPhysicsVehicleTurretSteering(turnAmount);
  }
  double turnSpeed = steerInPlace ? 1 : Math.min(1, Math.max(netSpeed, Math.abs(speed)) * .025);
  turnSpeed *= speed < 0 ? -1 : 1;
  if (handbrake && mode == Mode.fly) {
   XZ += speedXZ * turnSpeed * fromAngleToVelocityConstant * polarity * VE.tick;
  } else if (mode.name().startsWith(Mode.drive.name()) || phantomEngaged) {
   if (!flipped) {
    airSpinXZ = (handbrake ? fromAngleToVelocityConstant : .0625) * speedXZ * turnSpeed;
    XZ += speedXZ * turnSpeed * fromAngleToVelocityConstant * VE.tick;
   }
  } else if (mode != Mode.fly && (!aerialControlEnhanced || mode != Mode.stunt)) {
   XZ += airSpinXZ * VE.tick;
   stuntXZ += airSpinXZ * VE.tick;
  }
  runPhysicsFlight(turnAmount);
  for (Wheel wheel : wheels) {
   wheel.X = X + wheel.pointX;
   wheel.Y = Y + clearance;
   wheel.Z = Z + wheel.pointZ;
   double[] rotated = U.rotate(wheel.X, wheel.Y, X, Y, XY);
   wheel.X = rotated[0];
   wheel.Y = rotated[1];
   if (!wheels.get(0).angledSurface && !wheels.get(1).angledSurface && !wheels.get(2).angledSurface && !wheels.get(3).angledSurface) {
    rotated = U.rotate(wheel.Y, wheel.Z, Y, Z, YZ);
    wheel.Y = rotated[0];
    wheel.Z = rotated[1];
   }
   rotated = U.rotate(wheel.X, wheel.Z, X, Z, XZ);
   wheel.X = rotated[0];
   wheel.Z = rotated[1];
   if (!wheel.angledSurface && !phantomEngaged && !inTornado && !floats)
    wheel.speedY += (amphibious && atPoolXZ && wheel.Y - clearance > 0 ? -E.gravity : E.gravity) * VE.tick;
  }
  if (mode != Mode.fly || grip <= 100) {
   for (Wheel wheel : wheels) {
    wheel.speedX = wheel.speedX - netSpeedX > 200 ? netSpeedX + 200 : wheel.speedX - netSpeedX < -200 ? netSpeedX - 200 : wheel.speedX;
    wheel.speedZ = wheel.speedZ - netSpeedZ > 200 ? netSpeedZ + 200 : wheel.speedZ - netSpeedZ < -200 ? netSpeedZ - 200 : wheel.speedZ;
    wheel.X += (wheels.get(0).speedX + wheels.get(1).speedX + wheels.get(2).speedX + wheels.get(3).speedX) * .25 * VE.tick;
    wheel.Z += (wheels.get(0).speedZ + wheels.get(1).speedZ + wheels.get(2).speedZ + wheels.get(3).speedZ) * .25 * VE.tick;
    wheel.Y += wheel.speedY * VE.tick;
   }
  }
  if (mode.name().startsWith(Mode.drive.name()) || phantomEngaged) {
   speed -= turnDrag && (turnL || turnR) ? speed * .01 * VE.tick : 0;
   double setGrip = grip - (handbrake && !flipped && !U.listEquals(VE.mapName, "the Maze", "XY Land") ? grip * .25 * Math.abs(lastXZ - XZ) : 0);
   setGrip *= (terrainProperties.contains(" ice ") ? .075 : terrainProperties.contains(" ground ") ? .75 : 1) * (flipped ? .2 : 1);
   setGrip = Math.max(setGrip * VE.tick, 0);
   if (destroyed) {
    speed *= .9;
    if (Math.abs(speed) < 2 * VE.tick) {
     speed = 0;
    } else {
     speed += (speed < 0 ? 2 : speed > 0 ? -2 : 0) * VE.tick;
    }
   }
   boolean modeDrive = mode == Mode.drive;
   double speed1 = -(speed * U.sin(XZ) * U.cos(YZ)), speed2 = speed * U.cos(XZ) * U.cos(YZ), speed3 = -(speed * U.sin(YZ));
   for (Wheel wheel : wheels) {
    if (flipped) {
     wheel.speedX -= wheel.speedX > setGrip ? setGrip : Math.max(wheel.speedX, -setGrip);
     wheel.speedZ -= wheel.speedZ > setGrip ? setGrip : Math.max(wheel.speedZ, -setGrip);
    } else {
     if (Math.abs(wheel.speedX - speed1) > setGrip) {
      wheel.speedX += wheel.speedX < speed1 ? setGrip : wheel.speedX > speed1 ? -setGrip : 0;
     } else {
      wheel.speedX = speed1;
     }
     if (Math.abs(wheel.speedZ - speed2) > setGrip) {
      wheel.speedZ += wheel.speedZ < speed2 ? setGrip : wheel.speedZ > speed2 ? -setGrip : 0;
     } else {
      wheel.speedZ = speed2;
     }
     if (modeDrive || phantomEngaged) {
      if (Math.abs(wheel.speedY - speed3) > setGrip) {
       wheel.speedY += wheel.speedY < speed3 ? setGrip : wheel.speedY > speed3 ? -setGrip : 0;
      } else {
       wheel.speedY = speed3;
      }
     }
    }
   }
   if (modeDrive) {
    runPhysicsSkidsAndDust();
   }
   speed *= grip <= 100 && !flipped && Math.abs(speed) > netSpeed && Math.abs(Math.abs(speed) - netSpeed) > Math.abs(speed) * .5 && !terrainProperties.contains(" ice ") ? .5 : 1;
   if (terrainProperties.contains(" bounce ")) {
    for (Wheel wheel : wheels) {
     wheel.speedY -= U.random(.3) * Math.abs(speed) * bounce;
    }
    if (netSpeedY < -50 && bounce > .9) {
     VA.land.playIfNotPlaying(U.random(VA.land.clips.size()), VA.vehicleToCameraSoundDistance);
    }
   } else if (terrainProperties.contains(" maxbounce ")) {
    for (Wheel wheel : wheels) {
     wheel.speedY -= U.random(.6) * Math.abs(speed) * bounce;
    }
    if (netSpeedY < -50 && bounce > .9) {
     VA.land.playIfNotPlaying(U.random(VA.land.clips.size()), VA.vehicleToCameraSoundDistance);
    }
   }
   mode = Mode.neutral;
  }
  lastXZ = XZ;
  boolean crashLand = (Math.abs(YZ) > 30 || Math.abs(XY) > 30) && !(Math.abs(YZ) > 150 && Math.abs(XY) > 150);
  runPhysicsPool();
  runPhysicsSpeedBoost();
  double gravityCompensation = E.gravity * 2 * VE.tick;
  if (!phantomEngaged) {
   double hitsGroundY = -5;
   if (onAntiGravity) {
    hitsGroundY = Double.POSITIVE_INFINITY;//<-Why was this done? To eliminate '-5' traction-lock, maybe?
    onAntiGravity = false;
   }
   double bounceBackForce = flipped ? 1 : Math.abs(U.sin(XY)) + Math.abs(U.sin(YZ)),
   flatPlaneBounce = Math.min(Math.abs(U.sin(XY)) + Math.abs(U.sin(YZ)), 1);
   boolean possibleSpinnerHit = false;
   for (Wheel wheel : wheels) {
    if (wheel.Y > hitsGroundY + localVehicleGround) {
     mode = Mode.drive;
     if (Y + clearance > localVehicleGround + 100) {//Check, and Remove if bounce dust looks pointless
      deployDust(wheel);
     }
     wheel.Y = localVehicleGround;
     if (crashLand) {
      runCrash(wheel.speedY * bounceBackForce * .1);
     }
     if (wheel.speedY > 100) {
      VA.land();
     }
     if (wheel.speedY > 0) {
      wheel.speedY *= destroyed ? 0 : -bounce * flatPlaneBounce;
     }
     wheel.XY -= wheel.XY * .25;
     wheel.YZ -= wheel.YZ * .25;
     if (flipped && terrainProperties.contains(" hard ")) {
      wheel.sparks(true);
     }
     wheel.terrainRGB[0] = E.groundRGB[0];
     wheel.terrainRGB[1] = E.groundRGB[1];
     wheel.terrainRGB[2] = E.groundRGB[2];
     possibleSpinnerHit = true;
    }
    if (inPool) {
     wheel.speedX -= wheel.speedX * .01 * VE.tick;
     wheel.speedY -= wheel.speedY * .1 * VE.tick;
     wheel.speedZ -= wheel.speedZ * .01 * VE.tick;
    }
    wheel.againstWall = wheel.angledSurface = false;
   }
   if (!Double.isNaN(spinnerSpeed) && possibleSpinnerHit && !((Math.abs(YZ) < 10 && Math.abs(XY) < 10) || (Math.abs(YZ) > 170 && Math.abs(XY) > 170))) {
    spinnerHit(null);
   }
   mode = amphibious && inPool ? Mode.drivePool : mode;
   terrainProperties = E.vehicleTerrain;
   setTerrainFromTrackPlanes(gravityCompensation);
   runPhysicsVehicleTrackPlaneInteraction(crashLand, bounceBackForce, flatPlaneBounce, gravityCompensation);
   mode = onVolcano ? Mode.drive : mode;
   if (mode == Mode.drive && (wheels.get(0).angledSurface || wheels.get(1).angledSurface || wheels.get(2).angledSurface || wheels.get(3).angledSurface)) {
    XY = (Math.abs(XY) > 90 ? 180 : 0) + (wheels.get(0).XY + wheels.get(1).XY + wheels.get(2).XY + wheels.get(3).XY) * .25;
    YZ = (Math.abs(YZ) > 90 ? 180 : 0) + (wheels.get(0).YZ + wheels.get(1).YZ + wheels.get(2).YZ + wheels.get(3).YZ) * .25;
   }
   if (Math.abs(XY) > 90) {
    XY += wheels.get(0).speedY * VE.tick / wheelGapLeftToRight;
    XY -= wheels.get(1).speedY * VE.tick / wheelGapLeftToRight;
    XY += wheels.get(2).speedY * VE.tick / wheelGapLeftToRight;
    XY -= wheels.get(3).speedY * VE.tick / wheelGapLeftToRight;
   } else {
    XY -= wheels.get(0).speedY * VE.tick / wheelGapLeftToRight;
    XY += wheels.get(1).speedY * VE.tick / wheelGapLeftToRight;
    XY -= wheels.get(2).speedY * VE.tick / wheelGapLeftToRight;
    XY += wheels.get(3).speedY * VE.tick / wheelGapLeftToRight;
   }
   if (Math.abs(YZ) > 90) {
    YZ += wheels.get(0).speedY * VE.tick / wheelGapFrontToBack;
    YZ += wheels.get(1).speedY * VE.tick / wheelGapFrontToBack;
    YZ -= wheels.get(2).speedY * VE.tick / wheelGapFrontToBack;
    YZ -= wheels.get(3).speedY * VE.tick / wheelGapFrontToBack;
   } else {
    YZ -= wheels.get(0).speedY * VE.tick / wheelGapFrontToBack;
    YZ -= wheels.get(1).speedY * VE.tick / wheelGapFrontToBack;
    YZ += wheels.get(2).speedY * VE.tick / wheelGapFrontToBack;
    YZ += wheels.get(3).speedY * VE.tick / wheelGapFrontToBack;
   }
   runPhysicsCollisionSpin();
  }
  speed = topSpeeds[2] < Long.MAX_VALUE ? U.clamp(-topSpeeds[2], speed, topSpeeds[2]) : speed;
  if (explosionsWhenDestroyed > 0 && damage > durability && !destroyed) {
   for (n = (int) explosionsWhenDestroyed; --n >= 0; ) {
    explosions.get(currentExplosion).deploy(U.randomPlusMinus(absoluteRadius), U.randomPlusMinus(absoluteRadius), U.randomPlusMinus(absoluteRadius), this);
    currentExplosion = ++currentExplosion >= E.explosionQuantity ? 0 : currentExplosion;
   }
   setCameraShake(30);
   VA.explode.play(U.random(VA.explode.clips.size()), VA.vehicleToCameraSoundDistance);
  }
  if (!phantomEngaged) {
   runPhysicsInTornado();
  }
  for (Wheel wheel : wheels) {
   if (!Double.isNaN(wheel.hitOtherX)) {
    wheel.speedX = wheel.hitOtherX;
    wheel.hitOtherX = Double.NaN;
   }
   if (!Double.isNaN(wheel.hitOtherZ)) {
    wheel.speedZ = wheel.hitOtherZ;
    wheel.hitOtherZ = Double.NaN;
   }
  }
  if (mode == Mode.drive) {
   Y = (wheels.get(0).Y + wheels.get(1).Y + wheels.get(2).Y + wheels.get(3).Y) * .25 - (clearance * U.cos(YZ) * U.cos(XY));
  } else {
   Y += (wheels.get(0).speedY + wheels.get(1).speedY + wheels.get(2).speedY + wheels.get(3).speedY) * .25 * VE.tick;
  }
  if (wheels.get(0).againstWall || wheels.get(1).againstWall || wheels.get(2).againstWall || wheels.get(3).againstWall) {
   X = (wheels.get(0).X - wheels.get(0).pointX * U.cos(XZ) + polarity * wheels.get(0).pointZ * U.sin(XZ) + wheels.get(1).X - wheels.get(1).pointX * U.cos(XZ) + polarity * wheels.get(1).pointZ * U.sin(XZ) + wheels.get(2).X - wheels.get(2).pointX * U.cos(XZ) + polarity * wheels.get(2).pointZ * U.sin(XZ) + wheels.get(3).X - wheels.get(3).pointX * U.cos(XZ) + polarity * wheels.get(3).pointZ * U.sin(XZ)) * .25 + clearance * U.sin(XY) * U.cos(XZ) - clearance * U.sin(YZ) * U.sin(XZ);
   Z = (wheels.get(0).Z - polarity * wheels.get(0).pointZ * U.cos(XZ) - wheels.get(0).pointX * U.sin(XZ) + wheels.get(1).Z - polarity * wheels.get(1).pointZ * U.cos(XZ) - wheels.get(1).pointX * U.sin(XZ) + wheels.get(2).Z - polarity * wheels.get(2).pointZ * U.cos(XZ) - wheels.get(2).pointX * U.sin(XZ) + wheels.get(3).Z - polarity * wheels.get(3).pointZ * U.cos(XZ) - wheels.get(3).pointX * U.sin(XZ)) * .25 + clearance * U.sin(XY) * U.sin(XZ) - clearance * U.sin(YZ) * U.cos(XZ);
  } else {
   X += (wheels.get(0).speedX + wheels.get(1).speedX + wheels.get(2).speedX + wheels.get(3).speedX) * .25 * VE.tick;
   Z += (wheels.get(0).speedZ + wheels.get(1).speedZ + wheels.get(2).speedZ + wheels.get(3).speedZ) * .25 * VE.tick;
  }
  if (mode == Mode.drive && !wheels.get(0).angledSurface && !wheels.get(1).angledSurface && !wheels.get(2).angledSurface && !wheels.get(3).angledSurface) {
   if (Math.abs(YZ) <= 90) {
    YZ -= YZ > 0 ? VE.tick : 0;
    YZ += YZ < 0 ? VE.tick : 0;
   } else {
    YZ += (YZ > 90 && YZ < 180 - VE.tick ? 1 : YZ < -90 && YZ > -180 + VE.tick ? -1 : 0) * VE.tick;
   }
   if (Math.abs(XY) <= 90) {
    XY -= XY > 0 ? VE.tick : 0;
    XY += XY < 0 ? VE.tick : 0;
   }
   if (sidewaysLandingAngle == 0) {
    XY += (XY > 90 && XY < 180 - VE.tick ? 1 : XY < -90 && XY > -180 + VE.tick ? -1 : 0) * VE.tick;
   } else {
    XY += U.random(3.) * VE.tick *
    ((XY > 90 && XY < sidewaysLandingAngle) || (XY < -sidewaysLandingAngle && XY > -(sidewaysLandingAngle + 30)) ? 1 :
    (XY < -90 && XY > -sidewaysLandingAngle) || (XY > sidewaysLandingAngle && XY < sidewaysLandingAngle + 30) ? -1 : 0);
    if (Math.abs(XY) >= sidewaysLandingAngle + 30) {
     XY -= XY > -180 + VE.tick ? VE.tick : 0;
     XY += XY < 180 - VE.tick ? VE.tick : 0;
    }
   }
   XY *= Math.abs(XY) <= 90 ? .25 : 1;
   YZ *= Math.abs(YZ) <= 90 ? .25 : 1;
  }
  localVehicleGround = E.groundLevel + (atPoolXZ ? E.poolDepth : 0);
  runPhysicsVehicleMound(clearance, gravityCompensation);//<-localVehicleGround is set here
  if (onVolcano) {
   double baseAngle = flipped ? 225 : 45, vehicleVolcanoXZ = XZ, VolcanoPlaneY = Math.max(E.volcanoTopRadius * .5, (E.volcanoBottomRadius * .5) - ((E.volcanoBottomRadius * .5) * (Math.abs(Y) / E.volcanoHeight)));
   vehicleVolcanoXZ += Z < E.volcanoZ && Math.abs(X - E.volcanoX) < VolcanoPlaneY ? 180 : X >= E.volcanoX + VolcanoPlaneY ? 90 : X <= E.volcanoX - VolcanoPlaneY ? -90 : 0;
   XY = baseAngle * U.sin(vehicleVolcanoXZ);
   YZ = -baseAngle * U.cos(vehicleVolcanoXZ);
  }
  for (Wheel wheel : wheels) {
   wheel.vibrate = 0;
  }
  if (mode.name().startsWith(Mode.drive.name())) {
   if (Math.abs(YZ) > 90 && Math.abs(XY) > 90) {
    long randomFlip = U.random() < .5 ? 180 : -180;
    XY += randomFlip;
    YZ += randomFlip;
    XZ += randomFlip;
   }
   if (flipped && landStuntsBothSides) {
    XZ += Math.abs(YZ) > 90 && Math.abs(XY) < 90 ? 180 : 0;
    XY = YZ = 0;
   }
   runPhysicsGroundCruise();
  }
  if (engine == Engine.hotrod && !destroyed && U.random() < .5) {
   XY += U.random() < .5 ? 1 : -1;
  }
  runVehicleCourseInteraction(replay);
  runPhysicsStunts(replay);
  speed *= (wheels.get(0).againstWall || wheels.get(1).againstWall || wheels.get(2).againstWall || wheels.get(3).againstWall) && grip > 100 && E.gravity != 0 ? .95 : 1;
  if (drag != 0) {
   if (Math.abs(speed) < drag * VE.tick) {
    speed = 0;
   } else if ((mode != Mode.fly && !drive && !reverse) || mode == Mode.stunt || Math.abs(speed) > topSpeeds[1]) {
    speed -= (speed > 0 ? 1 : -1) * drag * VE.tick;
   }
  }
  speed -= Math.abs(speed) > topSpeeds[1] ? speed * Math.abs(speed) * .0000005 * VE.tick : 0;
  if (E.slowVehiclesWhenAtLimit) {
   if (X > E.mapBounds.right || X < E.mapBounds.left) {
    wheels.get(U.random(4)).speedX = wheels.get(U.random(4)).speedZ = 0;
    speed *= .95;
   }
   if (Math.abs(Y) > Math.abs(E.mapBounds.Y)) {
    wheels.get(U.random(4)).speedY = 0;
    speed *= .95;
   }
   if (Z > E.mapBounds.forward || Z < E.mapBounds.backward) {
    wheels.get(U.random(4)).speedX = wheels.get(U.random(4)).speedZ = 0;
    speed *= .95;
   }
  }
  if (onVolcano && !phantomEngaged) {
   Y = Math.min(Y, -E.volcanoBottomRadius + U.distance(X, E.volcanoX, Z, E.volcanoZ));
   for (Wheel wheel : wheels) {
    wheel.speedY = Math.min(wheel.speedY, 0);
   }
  }
  X = U.clamp(E.mapBounds.left, X, E.mapBounds.right);
  Z = U.clamp(E.mapBounds.backward, Z, E.mapBounds.forward);
  Y = U.clamp(E.mapBounds.Y, Y, -E.mapBounds.Y);
  mode = (mode == Mode.stunt || mode == Mode.fly) && destroyed ? Mode.neutral : mode;
  while (Math.abs(XZ - cameraXZ) > 180) {
   cameraXZ += cameraXZ < XZ ? 360 : -360;
  }
  cameraXZ += (XZ - cameraXZ) * .3 * StrictMath.pow(VE.tick, .8);
  lastXZ = XZ;
 }

 private void runPhysicsVehicleMound(double clearance, double gravityCompensation) {
  for (TrackPart TP : TE.trackParts) {
   if (TP.mound != null) {
    double distance = U.distance(X, TP.X, Z, TP.Z),
    radiusTop = TP.mound.getMinorRadius(), moundHeight = TP.mound.getHeight();
    if (distance < radiusTop) {
     if (Y - clearance <= TP.Y + gravityCompensation) {
      localVehicleGround = Math.min(localVehicleGround, TP.Y - moundHeight);
     }
    } else {
     double radiusBottom = TP.mound.getMajorRadius();
     if (distance < radiusBottom && Math.abs(Y + clearance - ((TP.Y - (moundHeight * .5)) + gravityCompensation)) <= moundHeight * .5) {
      double slope = moundHeight / Math.abs(radiusBottom - radiusTop),
      finalHeight = TP.Y - (radiusBottom - distance) * slope - clearance;
      if (Y >= finalHeight) {
       double baseAngle = U.arcTan(slope) + (flipped ? 180 : 0),
       vehicleMoundXZ = XZ, moundPlaneY = Math.max(radiusTop * .5, (radiusBottom * .5) - ((radiusBottom * .5) * (Math.abs(Y) / moundHeight)));
       vehicleMoundXZ += Z < TP.Z && Math.abs(X - TP.X) < moundPlaneY ? 180 : X >= TP.X + moundPlaneY ? 90 : X <= TP.X - moundPlaneY ? -90 : 0;
       XY = baseAngle * U.sin(vehicleMoundXZ);
       YZ = -baseAngle * U.cos(vehicleMoundXZ);
       Y = finalHeight;
       mode = Mode.drive;
       terrainProperties = E.vehicleTerrain;
       for (Wheel wheel : wheels) {
        wheel.terrainRGB[0] = E.groundRGB[0];
        wheel.terrainRGB[1] = E.groundRGB[1];
        wheel.terrainRGB[2] = E.groundRGB[2];
       }
      }
     }
    }
   }
  }
 }

 private void runPhysicsDrivePower() {
  boolean aircraft = vehicleType == Type.aircraft, flying = mode == Mode.fly;
  boolean driveGet = !flying && aircraft ? drive || drive2 : flying ? drive2 : drive,
  reverseGet = !flying && aircraft ? reverse || reverse2 : flying ? reverse2 : reverse;
  if (reverseGet) {
   speed -= speed > 0 && engine != Engine.hotrod ? brake * .5 * VE.tick : speed > -topSpeeds[0] ? accelerationStages[0] * VE.tick : 0;
  }
  if (driveGet) {
   if (speed < 0 && engine != Engine.hotrod) {
    speed += brake * VE.tick;
   } else {
    int u = 0;
    for (int n = 2; --n >= 0; ) {
     u += speed >= topSpeeds[n] ? 1 : 0;
    }
    speed += u < 2 ? accelerationStages[u] * VE.tick : 0;
   }
  }
  if (!flying && handbrake && speed != 0) {
   if (speed < brake * VE.tick && speed > -brake * VE.tick) {
    speed = 0;
   } else {
    speed += (speed < 0 ? 1 : speed > 0 ? -1 : 0) * brake * VE.tick;
   }
  }
 }

 private void runPhysicsSteering(double turnAmount) {
  if (steerByMouse && turnRate >= Double.POSITIVE_INFINITY) {
   speedXZ = U.clamp(-turnAmount, VE.mouseSteerX, turnAmount);
  } else {
   if ((turnR && !turnL) || (steerByMouse && speedXZ > VE.mouseSteerX)) {
    speedXZ -= (speedXZ > 0 ? 2 : 1) * turnRate * VE.tick;
    speedXZ = Math.max(speedXZ, -turnAmount);
   }
   if ((turnL && !turnR) || (steerByMouse && speedXZ < VE.mouseSteerX)) {
    speedXZ += (speedXZ < 0 ? 2 : 1) * turnRate * VE.tick;
    speedXZ = Math.min(speedXZ, turnAmount);
   }
   if (speedXZ != 0 && !turnL && !turnR && !steerByMouse) {
    if (Math.abs(speedXZ) < turnRate * 2 * VE.tick) {
     speedXZ = 0;
    } else {
     speedXZ += (speedXZ < 0 ? turnRate : speedXZ > 0 ? -turnRate : 0) * 2 * VE.tick;
    }
   }
  }
  if (mode == Mode.fly) {
   if (drive || (steerByMouse && speedYZ > VE.mouseSteerY)) {
    speedYZ -= (speedYZ > 0 ? 2 : 1) * turnRate * VE.tick;
    speedYZ = Math.max(speedYZ, -maxTurn);
   }
   if (reverse || (steerByMouse && speedYZ < VE.mouseSteerY)) {
    speedYZ += (speedYZ < 0 ? 2 : 1) * turnRate * VE.tick;
    speedYZ = Math.min(speedYZ, maxTurn);
   }
  }
  if (speedYZ != 0 && (mode != Mode.fly || (!drive && !reverse && !steerByMouse))) {
   if (Math.abs(speedYZ) < turnRate * 2 * VE.tick) {
    speedYZ = 0;
   } else {
    speedYZ += (speedYZ < 0 ? turnRate : speedYZ > 0 ? -turnRate : 0) * 2 * VE.tick;
   }
  }
 }

 private void runPhysicsVehicleTurretSteering(double turnAmount) {
  boolean hear = false;
  if (hasTurret) {
   if (vehicleTurretR && !vehicleTurretL) {
    vehicleTurretSpeedXZ -= (vehicleTurretSpeedXZ > 0 ? 2 : 1) * turnRate * VE.tick;
    vehicleTurretSpeedXZ = Math.max(vehicleTurretSpeedXZ, -turnAmount);
    hear = true;
   }
   if (vehicleTurretL && !vehicleTurretR) {
    vehicleTurretSpeedXZ += (vehicleTurretSpeedXZ < 0 ? 2 : 1) * turnRate * VE.tick;
    vehicleTurretSpeedXZ = Math.min(vehicleTurretSpeedXZ, turnAmount);
    hear = true;
   }
   if (vehicleTurretSpeedXZ != 0 && !vehicleTurretL && !vehicleTurretR) {
    if (Math.abs(vehicleTurretSpeedXZ) < turnRate * 2 * VE.tick) {
     vehicleTurretSpeedXZ = 0;
    } else {
     vehicleTurretSpeedXZ += (vehicleTurretSpeedXZ < 0 ? turnRate : vehicleTurretSpeedXZ > 0 ? -turnRate : 0) * 2 * VE.tick;
    }
   }
   if (drive2) {
    vehicleTurretSpeedYZ -= (vehicleTurretSpeedYZ > 0 ? 2 : 1) * turnRate * VE.tick;
    vehicleTurretSpeedYZ = Math.max(vehicleTurretSpeedYZ, -maxTurn);
    hear = true;
   }
   if (reverse2) {
    vehicleTurretSpeedYZ += (vehicleTurretSpeedYZ < 0 ? 2 : 1) * turnRate * VE.tick;
    vehicleTurretSpeedYZ = Math.min(vehicleTurretSpeedYZ, maxTurn);
    hear = true;
   }
   if (vehicleTurretSpeedYZ != 0 && !drive2 && !reverse2) {
    if (Math.abs(vehicleTurretSpeedYZ) < turnRate * 2 * VE.tick) {
     vehicleTurretSpeedYZ = 0;
    } else {
     vehicleTurretSpeedYZ += (vehicleTurretSpeedYZ < 0 ? turnRate : vehicleTurretSpeedYZ > 0 ? -turnRate : 0) * 2 * VE.tick;
    }
   }
   vehicleTurretXZ += vehicleTurretSpeedXZ * fromAngleToVelocityConstant * VE.tick;
   while (vehicleTurretXZ < -180) vehicleTurretXZ += 360;
   while (vehicleTurretXZ > 180) vehicleTurretXZ -= 360;
   vehicleTurretYZ = U.clamp(turretVerticalRanges[0], vehicleTurretYZ + vehicleTurretSpeedYZ * fromAngleToVelocityConstant * VE.tick, turretVerticalRanges[1]);
   if (!destroyed && hear) {
    VA.turret.loop(VA.vehicleToCameraSoundDistance);
   } else {
    VA.turret.stop();
   }
  }
 }

 private void runPhysicsGroundCruise() {
  if (damage <= durability && mode == Mode.drive && bounce > 0) {
   if (vehicleType == Type.vehicle && !flipped) {
    XY += speed * clearanceY * bounce * speedXZ * (speed < 0 ? -.0000133 : .0000133) * (Math.abs(XY) > 10 ? .5 : 1);
   }
   boolean rockTerrain = terrainProperties.contains(" rock ");
   if (rockTerrain || terrainProperties.contains(" ground ")) {
    if (Double.isNaN(shockAbsorb)) {
     double vibrate = terrainProperties.contains(" rock ") ? .0003 : .00015;
     YZ += U.clamp(-180 - U.random(180.), U.randomPlusMinus(vibrate) * netSpeed * clearanceY * bounce, 180 + U.random(180.));
     XY += U.clamp(-180 - U.random(180.), U.randomPlusMinus(vibrate) * netSpeed * clearanceY * bounce, 180 + U.random(180.));
    } else {
     double vibrate = rockTerrain ? 50 : 25;
     double[] storeVibrate = new double[4];
     for (int n = 4; --n >= 0; ) {
      storeVibrate[n] = U.randomPlusMinus(Math.min(vibrate * netSpeed * bounce * .002, vibrate));
      wheels.get(n).vibrate = storeVibrate[n] * shockAbsorb;
     }
     if (shockAbsorb < 1) {
      double setXY = 0, setYZ = 0;
      setXY -= storeVibrate[0];
      setXY += storeVibrate[1];
      setXY -= storeVibrate[2];
      setXY += storeVibrate[3];
      setXY /= wheelGapLeftToRight;
      setXY -= setXY * shockAbsorb;
      setYZ -= storeVibrate[0];
      setYZ -= storeVibrate[1];
      setYZ += storeVibrate[2];
      setYZ += storeVibrate[3];
      setYZ /= wheelGapFrontToBack;
      setYZ -= setYZ * shockAbsorb;
      YZ -= setYZ * clearanceY * .5;
      XY -= setXY * clearanceY * .5;
     }
    }
   }
  }
 }

 private void runPhysicsWheelSpin() {
  double wheelSpun = U.clamp(-44 / VE.tick, 80 * Math.sqrt(Math.abs(StrictMath.pow(speed, 2) * 1.333)) / collisionRadius, 44 / VE.tick), amount = speed < 0 ? -1 : 1;
  if (Math.abs(amount * wheelSpun * VE.tick) > 25) {
   double randomAngle = U.randomPlusMinus(360.);
   wheelSpin[0] = randomAngle;
   wheelSpin[1] = randomAngle;
  } else {
   wheelSpin[0] += amount * wheelSpun * VE.tick;
   wheelSpin[1] += amount * wheelSpun * VE.tick;
   if (steerInPlace) {
    double steerSpin = 200 * speedXZ / collisionRadius;
    wheelSpin[0] += amount * steerSpin * VE.tick;
    wheelSpin[1] -= amount * steerSpin * VE.tick;
   }
   wheelSpin[0] = Math.abs(wheelSpin[0]) > 360 ? 0 : wheelSpin[0];
   wheelSpin[1] = Math.abs(wheelSpin[1]) > 360 ? 0 : wheelSpin[1];
  }
 }

 private void runPhysicsCollisionSpin() {
  if (spin > 0) {
   if (wheels.get(0).againstWall || wheels.get(1).againstWall || wheels.get(2).againstWall || wheels.get(3).againstWall) {
    double v1;
    v1 = Math.abs(XZ + 45);
    while (v1 > 180) v1 -= 360;
    spinMultiplyPositive = Math.abs(v1) > 90 ? 1 : -1;
    v1 = Math.abs(XZ - 45);
    while (v1 > 180) v1 -= 360;
    spinMultiplyNegative = Math.abs(v1) > 90 ? 1 : -1;
   }
   XZ += ((((wheels.get(0).speedZ * spinMultiplyNegative - wheels.get(1).speedZ * spinMultiplyPositive) + wheels.get(2).speedZ * spinMultiplyPositive - wheels.get(3).speedZ * spinMultiplyNegative) + wheels.get(0).speedX * spinMultiplyPositive + wheels.get(1).speedX * spinMultiplyNegative) - wheels.get(2).speedX * spinMultiplyNegative - wheels.get(3).speedX * spinMultiplyPositive) * spin * VE.tick;
  }
 }

 private void runPhysicsSkidsAndDust() {
  boolean markedSnow = false;
  if (terrainProperties.contains(" snow ")) {
   for (Wheel wheel : wheels) {
    wheel.skidmark(true);
   }
   markedSnow = true;
  }
  boolean kineticFriction = Math.abs(Math.abs(speed) - netSpeed) > 15, driveEngine = !U.contains(engine.name(), "prop", Engine.jet.name(), Engine.rocket.name());
  if (((driveEngine && kineticFriction) || StrictMath.pow(speedXZ, 2) > 300000 / netSpeed) && (kineticFriction || Math.abs(speed) > topSpeeds[1] * .9)) {
   if (terrainProperties.contains(" hard ") && contact == Contact.metal) {
    for (Wheel wheel : wheels) {
     wheel.sparks(true);
    }
   }
   if (contact == Contact.rubber || !terrainProperties.contains(" hard ")) {
    for (Wheel wheel : wheels) {
     deployDust(wheel);
    }
   }
   if (!terrainProperties.contains(" ice ")) {
    if (!markedSnow) {
     for (Wheel wheel : wheels) {
      wheel.skidmark(false);
     }
    }
    if (damage <= durability && !flipped && contact != Contact.none) {
     VA.skid();
    }
   }
  } else if (terrainProperties.contains(" snow ")) {
   for (Wheel wheel : wheels) {
    if (U.random() < .4) {
     deployDust(wheel);
    }
   }
  } else if (terrainProperties.contains(" ground ")) {
   for (Wheel wheel : wheels) {
    if (U.random() < .2) {
     deployDust(wheel);
    }
   }
  }
 }

 private void runPhysicsAirEngage() {
  if (vehicleType == Type.vehicle && handbrake && mode == Mode.neutral) {
   mode = Mode.stunt;
   VA.airEngage.playIfNotPlaying(VA.vehicleToCameraSoundDistance);
  }
  if (vehicleType == Type.aircraft && drive2) {
   boolean engageFly = mode == Mode.neutral;
   if (mode.name().startsWith(Mode.drive.name()) && reverse) {
    Y -= 10;
    engageFly = true;
   }
   if (engageFly) {
    mode = Mode.fly;
    VA.airEngage.playIfNotPlaying(VA.vehicleToCameraSoundDistance);
   }
  }
 }

 private void runPhysicsAerialControl() {
  if (aerialControlEnhanced && !onAntiGravity) {
   for (Wheel wheel : wheels) {
    wheel.speedY = netSpeedY;
   }
  }
  if (drive) {
   stuntSpeedYZ -= airAcceleration < Double.POSITIVE_INFINITY ? airAcceleration * VE.tick : 0;
   stuntSpeedYZ = stuntSpeedYZ < -airTopSpeed || airAcceleration == Double.POSITIVE_INFINITY ? -airTopSpeed : stuntSpeedYZ;
  }
  if (reverse) {
   stuntSpeedYZ += airAcceleration < Double.POSITIVE_INFINITY ? airAcceleration * VE.tick : 0;
   stuntSpeedYZ = stuntSpeedYZ > airTopSpeed || airAcceleration == Double.POSITIVE_INFINITY ? airTopSpeed : stuntSpeedYZ;
  }
  if (!drive && !reverse) {
   if (airAcceleration < Double.POSITIVE_INFINITY) {
    stuntSpeedYZ += (stuntSpeedYZ < 0 ? 1 : stuntSpeedYZ > 0 ? -1 : 0) * airAcceleration * VE.tick;
   }
   stuntSpeedYZ = Math.abs(stuntSpeedYZ) < airAcceleration || airAcceleration == Double.POSITIVE_INFINITY ? 0 : stuntSpeedYZ;
  }
  if (stuntSpeedYZ < 0) {
   double amount = Math.abs(XY) > 90 ? -1 : 1;
   X += amount * -airPush * U.sin(XZ) * -stuntSpeedYZ * VE.tick;
   Z += amount * airPush * U.cos(XZ) * -stuntSpeedYZ * VE.tick;
  }
  Y -= stuntSpeedYZ > 0 ? airPush * stuntSpeedYZ * VE.tick : 0;
  if ((turnL && !turnR) || (steerByMouse && (handbrake ? stuntSpeedXZ : stuntSpeedXY) * -40 < VE.mouseSteerX)) {
   if (handbrake) {
    stuntSpeedXZ -= airAcceleration < Double.POSITIVE_INFINITY ? airAcceleration * VE.tick : 0;
    stuntSpeedXZ = stuntSpeedXZ < -airTopSpeed || airAcceleration == Double.POSITIVE_INFINITY ? -airTopSpeed : stuntSpeedXZ;
   } else {
    stuntSpeedXY -= airAcceleration < Double.POSITIVE_INFINITY ? airAcceleration * VE.tick : 0;
    stuntSpeedXY = stuntSpeedXY < -airTopSpeed || airAcceleration == Double.POSITIVE_INFINITY ? -airTopSpeed : stuntSpeedXY;
   }
  }
  if ((turnR && !turnL) || (steerByMouse && (handbrake ? stuntSpeedXZ : stuntSpeedXY) * -40 > VE.mouseSteerX)) {
   if (handbrake) {
    stuntSpeedXZ += airAcceleration < Double.POSITIVE_INFINITY ? airAcceleration * VE.tick : 0;
    stuntSpeedXZ = stuntSpeedXZ > airTopSpeed || airAcceleration == Double.POSITIVE_INFINITY ? airTopSpeed : stuntSpeedXZ;
   } else {
    stuntSpeedXY += airAcceleration < Double.POSITIVE_INFINITY ? airAcceleration * VE.tick : 0;
    stuntSpeedXY = stuntSpeedXY > airTopSpeed || airAcceleration == Double.POSITIVE_INFINITY ? airTopSpeed : stuntSpeedXY;
   }
  }
  if ((!turnL && !turnR && !steerByMouse) || !handbrake) {
   if (airAcceleration < Double.POSITIVE_INFINITY) {
    stuntSpeedXZ += (stuntSpeedXZ < 0 ? 1 : stuntSpeedXZ > 0 ? -1 : 0) * airAcceleration * VE.tick;
   }
   stuntSpeedXZ = Math.abs(stuntSpeedXZ) < airAcceleration || airAcceleration == Double.POSITIVE_INFINITY ? 0 : stuntSpeedXZ;
  }
  if ((!turnL && !turnR && !steerByMouse) || handbrake) {
   if (airAcceleration < Double.POSITIVE_INFINITY) {
    stuntSpeedXY += (stuntSpeedXY < 0 ? 1 : stuntSpeedXY > 0 ? -1 : 0) * airAcceleration * VE.tick;
   }
   stuntSpeedXY = Math.abs(stuntSpeedXY) < airAcceleration || airAcceleration == Double.POSITIVE_INFINITY ? 0 : stuntSpeedXY;
  }
  YZ += 20 * stuntSpeedYZ * U.cos(XY) * VE.tick;
  XZ -= 20 * polarity * stuntSpeedYZ * U.sin(XY) * VE.tick;
  XZ -= stuntSpeedXZ * 20 * polarity * VE.tick;
  XY += 20 * stuntSpeedXY * VE.tick;
  X += airPush * U.cos(XZ) * polarity * stuntSpeedXY * VE.tick;
  Z += airPush * U.sin(XZ) * polarity * stuntSpeedXY * VE.tick;
 }

 private void runPhysicsFlight(double turnAmount) {
  if (mode == Mode.fly) {
   if (handbrake) {
    if (Math.abs(XY) < turnAmount * fromAngleToVelocityConstant * VE.tick) {
     XY = 0;
    } else {
     XY += (XY < 0 ? 1 : XY > 0 ? -1 : 0) * turnAmount * fromAngleToVelocityConstant * VE.tick;
    }
   } else {
    XY -= speedXZ * .27 * VE.tick;
    stuntXY -= speedXZ * .27 * VE.tick;
   }
   YZ += speedYZ * .135 * U.cos(XY) * VE.tick;
   stuntYZ -= speedYZ * .135 * U.cos(XY) * VE.tick;
   if (!handbrake && engine != Engine.powerjet) {
    XZ -= speedYZ * .135 * U.sin(XY) * polarity * VE.tick;
    stuntXZ -= speedYZ * .135 * U.sin(XY) * polarity * VE.tick;
   }
   double amount = (speed < 0 ? -5 : 5) * U.sin(XY) * polarity * VE.tick;
   XZ -= amount;
   stuntXZ -= amount;
   for (Wheel wheel : wheels) {
    wheel.speedX = -speed * U.sin(XZ) * U.cos(YZ);
    wheel.speedZ = speed * U.cos(XZ) * U.cos(YZ);
    wheel.speedY = -speed * U.sin(YZ) + stallSpeed;
   }
   if (E.gravity == 0 || onAntiGravity || floats) {
    stallSpeed = 0;
   } else {
    stallSpeed += E.gravity * VE.tick;
    if (Math.abs(speed) > 0 && stallSpeed > 0) {
     stallSpeed -= Math.abs(speed) * VE.tick * (engine == Engine.smallprop ? .04 : .02);
    }
    stallSpeed *= inPool ? Math.min(.95 * VE.tick, 1) : 1;
   }
  } else {
   stallSpeed = netSpeedY;
  }
 }

 private void runPhysicsSpeedBoost() {
  if (boost && speedBoost > 0 && !destroyed) {
   for (Wheel wheel : wheels) {
    if (Math.abs(wheel.speedX) < topSpeeds[2]) {
     wheel.speedX -= speedBoost * U.sin(XZ) * polarity * VE.tick;
    } else {
     wheel.speedX *= .999;
    }
    if (Math.abs(wheel.speedZ) < topSpeeds[2]) {
     wheel.speedZ += speedBoost * U.cos(XZ) * polarity * VE.tick;
    } else {
     wheel.speedZ *= .999;
    }
    if (Math.abs(wheel.speedY) < topSpeeds[2] || (E.gravity > 0 && wheel.speedY > 0)) {
     wheel.speedY -= speedBoost * U.sin(YZ) * VE.tick;
    } else {
     wheel.speedY *= .999;
    }
   }
   speed += grip <= 100 ? speedBoost * VE.tick : 0;
  }
 }

 private void runPhysicsPool() {
  if (inPool && !phantomEngaged) {
   if (netSpeed > 0) {
    for (int n = 3; --n >= 0; ) {
     for (Wheel wheel : wheels) {
      splashes.get(currentSplash).deploy(wheel, absoluteRadius * .0125 + U.random(absoluteRadius * .0125),
      wheel.speedX + U.randomPlusMinus(Math.max(speed, netSpeed)),
      wheel.speedY + U.randomPlusMinus(Math.max(speed, netSpeed)),
      wheel.speedZ + U.randomPlusMinus(Math.max(speed, netSpeed)));
      currentSplash = ++currentSplash >= E.splashQuantity ? 0 : currentSplash;
     }
    }
   }
   splashing = netSpeed;
   if (!reviveImmortality) {
    if (E.poolType == E.Pool.lava) {
     damage += 30 * VE.tick;
     for (VehiclePart part : parts) {
      part.deform();
     }
    } else {
     damage += E.poolType == E.Pool.acid ? .0025 * durability * VE.tick : 0;
    }
   }
  }
 }

 private void runPhysicsInTornado() {
  inTornado = false;
  if (!E.tornadoParts.isEmpty() && Y > E.tornadoParts.get(E.tornadoParts.size() - 1).Y && U.distance(X, E.tornadoParts.get(0).X, Z, E.tornadoParts.get(0).Z) < E.tornadoParts.get(0).C.getRadius() * 7.5) {
   double tornadoThrowEngage = (400000 / U.distance(X, E.tornadoParts.get(0).X, Z, E.tornadoParts.get(0).Z)) * VE.tick * (mode == Mode.fly ? 20 : 1);
   long maxThrow = 750;
   for (Wheel wheel : wheels) {
    if (getsPushed >= 0) {
     wheel.speedX += Math.abs(wheel.speedX) < maxThrow ? U.clamp(-maxThrow, U.randomPlusMinus(tornadoThrowEngage), maxThrow) : 0;
     wheel.speedX += 2 * (X < E.tornadoParts.get(0).X && wheel.speedX < maxThrow ? Math.min(U.random(StrictMath.pow(tornadoThrowEngage, .75)), maxThrow) : X > E.tornadoParts.get(0).X && wheel.speedX > -maxThrow ? -Math.min(U.random(StrictMath.pow(tornadoThrowEngage, .75)), maxThrow) : 0);
     wheel.speedZ += Math.abs(wheel.speedZ) < maxThrow ? U.clamp(-maxThrow, U.randomPlusMinus(tornadoThrowEngage), maxThrow) : 0;
     wheel.speedZ += 2 * (Z < E.tornadoParts.get(0).Z && wheel.speedZ < maxThrow ? Math.min(U.random(StrictMath.pow(tornadoThrowEngage, .75)), maxThrow) : Z > E.tornadoParts.get(0).Z && wheel.speedZ > -maxThrow ? -Math.min(U.random(StrictMath.pow(tornadoThrowEngage, .75)), maxThrow) : 0);
    }
    wheel.speedY += getsLifted >= 0 && Math.abs(wheel.speedY) < maxThrow ? U.clamp(-maxThrow, U.randomPlusMinus(tornadoThrowEngage), maxThrow) : 0;
   }
   inTornado = getsLifted >= 0;
  }
 }

 private void runPhysicsHitTsunami() {
  //* Turrets automatically have getsPushed/Lifted set to -1
  for (TsunamiPart tsunamiPart : E.tsunamiParts) {
   if (U.distance(this, tsunamiPart) < collisionRadius + tsunamiPart.C.getRadius()) {
    if (getsPushed >= 0) {//*
     for (Wheel wheel : wheels) {
      wheel.speedX += E.tsunamiSpeedX * .5 * VE.tick;
      wheel.speedZ += E.tsunamiSpeedZ * .5 * VE.tick;
     }
    }
    if (getsLifted >= 0) {//*
     for (Wheel wheel : wheels) {
      wheel.speedY += E.gravity * VE.tick * (Y < tsunamiPart.Y ? 4 : Y > tsunamiPart.Y ? -4 : 0);
     }
    }
    for (int n1 = 20; --n1 >= 0; ) {
     splashes.get(currentSplash).deploy(wheels.get(U.random(4)), U.random(absoluteRadius * .05),
     E.tsunamiSpeedX + U.randomPlusMinus(Math.max(E.tsunamiSpeed, netSpeed)),
     U.randomPlusMinus(Math.max(E.tsunamiSpeed, netSpeed)),
     E.tsunamiSpeedZ + U.randomPlusMinus(Math.max(E.tsunamiSpeed, netSpeed)));
     currentSplash = ++currentSplash >= E.splashQuantity ? 0 : currentSplash;
    }
    VA.tsunamiSplash.playIfNotPlaying(VA.vehicleToCameraSoundDistance);
   }
  }
 }

 public void physicsTurret() {
  int n;
  atPoolXZ = E.poolExists && U.distance(X, E.poolX, Z, E.poolZ) < E.pool[0].getRadius();
  inPool = atPoolXZ && Y > 0;
  polarity = 1;
  vehicleHit = damage <= durability ? -1 : vehicleHit;
  double randomTurnKick = U.random(randomTurn);
  if (steerByMouse && turnRate >= Double.POSITIVE_INFINITY) {
   speedXZ = U.clamp(-maxTurn - randomTurnKick, VE.mouseSteerX, maxTurn + randomTurnKick);
  } else {
   if ((turnR && !turnL) || (steerByMouse && speedXZ > VE.mouseSteerX)) {
    speedXZ -= (speedXZ > 0 ? 2 : 1) * turnRate * VE.tick;
    speedXZ = Math.max(speedXZ, -maxTurn);
    if (!destroyed) {
     VA.turret.loop(VA.vehicleToCameraSoundDistance);
    }
   }
   if ((turnL && !turnR) || (steerByMouse && speedXZ < VE.mouseSteerX)) {
    speedXZ += (speedXZ < 0 ? 2 : 1) * turnRate * VE.tick;
    speedXZ = Math.min(speedXZ, maxTurn);
    if (!destroyed) {
     VA.turret.loop(VA.vehicleToCameraSoundDistance);
    }
   }
   if (speedXZ != 0 && !turnL && !turnR && !steerByMouse) {
    if (Math.abs(speedXZ) < turnRate * 2 * VE.tick) {
     speedXZ = 0;
    } else {
     speedXZ += (speedXZ < 0 ? 1 : speedXZ > 0 ? -1 : 0) * turnRate * 2 * VE.tick;
    }
   }
  }
  if (drive || (steerByMouse && speedYZ > VE.mouseSteerY)) {
   speedYZ -= (speedYZ > 0 ? 2 : 1) * turnRate * VE.tick;
   speedYZ = Math.max(speedYZ, -maxTurn);
   if (!destroyed) {
    VA.turret.loop(VA.vehicleToCameraSoundDistance);
   }
  }
  if (reverse || (steerByMouse && speedYZ < VE.mouseSteerY)) {
   speedYZ += (speedYZ < 0 ? 2 : 1) * turnRate * VE.tick;
   speedYZ = Math.min(speedYZ, maxTurn);
   if (!destroyed) {
    VA.turret.loop(VA.vehicleToCameraSoundDistance);
   }
  }
  if (speedYZ != 0 && !drive && !reverse && !steerByMouse) {
   if (Math.abs(speedYZ) < turnRate * 2 * VE.tick) {
    speedYZ = 0;
   } else {
    speedYZ += (speedYZ < 0 ? 1 : speedYZ > 0 ? -1 : 0) * turnRate * 2 * VE.tick;
   }
  }
  double sharpShoot = fromAngleToVelocityConstant * (handbrake ? .1 : 1);
  XZ += speedXZ * sharpShoot * VE.tick;
  YZ += speedYZ * sharpShoot * VE.tick;
  for (Wheel wheel : wheels) {
   wheel.X = X;
   wheel.Y = Y + (turretBaseY * .5);
   wheel.Z = Z;
   wheel.againstWall = false;
   wheel.speedX = wheel.speedZ = wheel.speedY = 0;
   wheel.speedY += damage > durability ? E.gravity * VE.tick : -wheel.speedY;
  }
  localVehicleGround = Y + turretBaseY;//<-Confirmed correct
  YZ = U.clamp(-90, YZ, 90);
  XY = speed = 0;
  flipped = false;
  if (explosionsWhenDestroyed > 0 && damage > durability && !destroyed) {
   for (n = (int) explosionsWhenDestroyed; --n >= 0; ) {
    explosions.get(currentExplosion).deploy(U.randomPlusMinus(absoluteRadius), U.randomPlusMinus(absoluteRadius), U.randomPlusMinus(absoluteRadius), this);
    currentExplosion = ++currentExplosion >= E.explosionQuantity ? 0 : currentExplosion;
   }
   setCameraShake(30);
   VA.explode.play(U.random(VA.explode.clips.size()), VA.vehicleToCameraSoundDistance);
   VA.crashDestroy.play(U.random(VA.crashDestroy.clips.size()), VA.vehicleToCameraSoundDistance);
  }
  X = U.clamp(E.mapBounds.left, X, E.mapBounds.right);
  Z = U.clamp(E.mapBounds.backward, Z, E.mapBounds.forward);
  Y = U.clamp(E.mapBounds.Y, Y, -E.mapBounds.Y);
 }

 private void runVehicleCourseInteraction(boolean replay) {
  if (!TE.points.isEmpty()) {
   Point P = TE.points.get(point);
   if (P.type == Point.Type.mustPassAbsolute && mode != Mode.fly) {
    point += U.distance(this, P) < 500 ? 1 : 0;
   } else if (P.type != Point.Type.checkpoint &&
   (U.distance(X, P.X, Z, P.Z) < 500 || (AI.skipStunts && P.type != Point.Type.mustPassIfClosest) || (!TE.checkpoints.isEmpty() && !VE.mapName.equals("Devil's Stairwell") && U.distance(this, TE.checkpoints.get(checkpointsPassed)) <= U.distance(P, TE.checkpoints.get(checkpointsPassed))))) {
    point++;
   }
  }
  if (!TE.checkpoints.isEmpty() && !phantomEngaged) {
   double checkSize = VE.mapName.equals("Circle Race XL") ? speed : 0;
   Checkpoint C = TE.checkpoints.get(checkpointsPassed);
   if ((C.type == Checkpoint.Type.passZ || C.type == Checkpoint.Type.passAny) &&
   Math.abs(Z - C.Z) < (60 + checkSize) + Math.abs(netSpeedZ) * VE.tick && Math.abs(X - C.X) < 700 && Math.abs((Y - C.Y) + 350) < 450) {
    checkpointsPassed++;
    point++;
    if (index == VE.vehiclePerspective) {
     if (!VE.messageWait) {
      VE.print = "Checkpoint";
      VE.printTimer = 10;
     }
     if (VE.headsUpDisplay) {
      VE.checkpoint.play(0);
     }
    }
    VE.scoreCheckpoint[index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : 1;
    if (checkpointsPassed >= TE.checkpoints.size()) {
     VE.scoreLap[index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : 1;
     checkpointsPassed = point = 0;
    }
   }
   if ((C.type == Checkpoint.Type.passX || C.type == Checkpoint.Type.passAny) &&
   Math.abs(X - C.X) < (60 + checkSize) + Math.abs(netSpeedX) * VE.tick && Math.abs(Z - C.Z) < 700 && Math.abs((Y - C.Y) + 350) < 450) {
    checkpointsPassed++;
    point++;
    if (index == VE.vehiclePerspective) {
     if (!VE.messageWait) {
      VE.print = "Checkpoint";
      VE.printTimer = 10;
     }
     if (VE.headsUpDisplay) {
      VE.checkpoint.play(0);
     }
    }
    VE.scoreCheckpoint[index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : 1;
    if (checkpointsPassed >= TE.checkpoints.size()) {
     VE.scoreLap[index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : 1;
     checkpointsPassed = point = 0;
    }
   }
   point = checkpointsPassed > 0 ? (int) U.clamp(TE.checkpoints.get(checkpointsPassed - 1).location + 1, point, TE.checkpoints.get(checkpointsPassed).location) : point;
   if (index == VE.vehiclePerspective) {
    VE.currentCheckpoint = checkpointsPassed;
    VE.lapCheckpoint = checkpointsPassed >= TE.checkpoints.size() - 1;
   }
  }
  point = point >= TE.points.size() || point < 0 ? 0 : point;
 }

 private void runPhysicsVehicleTrackPlaneInteraction(boolean crashLand, double bounceBackForce, double flatPlaneBounce, double gravityCompensation) {
  double wallPlaneBounce = Math.min(Math.abs(U.cos(XY)) + Math.abs(U.cos(YZ)), 1);
  boolean spinnerHit = false;
  for (TrackPart trackPart : TE.trackParts) {
   if (!trackPart.trackPlanes.isEmpty() && U.distance(X, trackPart.X, Z, trackPart.Z) < trackPart.renderRadius + renderRadius) {
    for (TrackPlane trackPlane : trackPart.trackPlanes) {
     double trackX = trackPlane.X + trackPart.X, trackY = trackPlane.Y + trackPart.Y, trackZ = trackPlane.Z + trackPart.Z,
     velocityXZ = Math.abs(U.sin(XZ)),
     radiusX = trackPlane.radiusX + (trackPlane.addSpeed && velocityXZ > U.sin45 ? netSpeed * VE.tick : 0),
     radiusY = trackPlane.radiusY + (trackPlane.addSpeed ? netSpeed * VE.tick : 0),
     radiusZ = trackPlane.radiusZ + (trackPlane.addSpeed && velocityXZ < U.sin45 ? netSpeed * VE.tick : 0);
     boolean isTree = trackPlane.type.contains(" tree "), gate = trackPlane.type.contains("gate"),
     isWall = trackPlane.wall != TrackPlane.Wall.none;
     String trackProperties = "";
     if (!isTree && !gate && (isWall || (Math.abs(X - trackX) < radiusX && Math.abs(Z - trackZ) < radiusZ && trackY + (radiusY * .5) >= Y))) {
      trackProperties = trackPlane.type + (U.contains(trackPlane.type, " paved ", " rock ", " grid ", " antigravity ", " metal ", " brightmetal") ? " hard " : " ground ");
     }
     for (Wheel wheel : wheels) {
      boolean inX = Math.abs(wheel.X - trackX) <= radiusX, inZ = Math.abs(wheel.Z - trackZ) <= radiusZ;
      if (Math.abs(wheel.Y - (trackY + gravityCompensation)) <= trackPlane.radiusY) {
       if (inX && inZ) {
        if (isTree) {
         wheel.speedX -= U.random(3.) * wheel.speedX * VE.tick;
         wheel.speedY -= U.random(3.) * wheel.speedY * VE.tick;
         wheel.speedZ -= U.random(3.) * wheel.speedZ * VE.tick;
         wheel.againstWall = true;
        } else if (gate) {
         if (trackPlane.type.contains(" slowgate ")) {
          if (Math.abs(trackPlane.YZ) == 90) {
           if (Math.abs(wheel.speedZ) > topSpeeds[0]) {
            wheel.speedZ *= .333;
            VA.gate.play(1, VA.vehicleToCameraSoundDistance);
           }
          } else if (Math.abs(trackPlane.XY) == 90) {
           if (Math.abs(wheel.speedX) > topSpeeds[0]) {
            wheel.speedX *= .333;
            VA.gate.play(1, VA.vehicleToCameraSoundDistance);
           }
          }
         } else {
          wheel.speedZ *= Math.abs(trackPlane.YZ) == 90 ? 3 : 1;
          wheel.speedX *= Math.abs(trackPlane.XY) == 90 ? 3 : 1;
          speed *= (speed > 0 && speed < topSpeeds[1]) || (speed < 0 && speed > -topSpeeds[0]) ? 1.25 : 1;
          if (wheel.speedX != 0 || wheel.speedZ != 0) {
           VA.gate.play(0, VA.vehicleToCameraSoundDistance);
          }
         }
        } else if (trackProperties.contains(" antigravity ")) {
         wheel.speedY -= E.gravity * 2 * VE.tick;
         onAntiGravity = true;
        } else if (!isWall) {
         if (trackPlane.YZ == 0 && trackPlane.XY == 0 && wheel.Y > trackY - 5) {//'- 5' is for better traction control--not to be used for stationary objects. Do not transfer '-5' to the assignment
          mode = Mode.drive;
          wheel.Y = Math.min(localVehicleGround, trackY);
          if (flipped && trackProperties.contains(" hard ")) {
           wheel.sparks(true);
          }
          if (crashLand) {
           runCrash(wheel.speedY * bounceBackForce * .1);
          }
          if (wheel.speedY > 100) {
           deployDust(wheel);
           VA.land();
          }
          if (wheel.speedY > 0) {
           wheel.speedY *= destroyed ? 0 : -bounce * flatPlaneBounce;
          }
          wheel.XY -= wheel.XY * .25;
          wheel.YZ -= wheel.YZ * .25;
          wheel.minimumY = trackY;
         } else if (trackPlane.YZ != 0) {
          double setY = trackY + (wheel.Z - trackZ) * (trackPlane.radiusY / trackPlane.radiusZ) * (trackPlane.YZ > 0 ? 1 : trackPlane.YZ < 0 ? -1 : 0);
          if (wheel.Y >= setY - 100) {
           wheel.angledSurface = true;
           mode = Mode.drive;
           if (!trackProperties.contains(" hard ")) {
            deployDust(wheel);
           } else if (flipped) {
            wheel.sparks(true);
           }
           if (Y >= trackY || wheel.speedY >= 0 || Math.abs(speed) < E.gravity * 4 * VE.tick || Math.abs((U.cos(XZ) > 0 ? -YZ : YZ) - trackPlane.YZ) < 30) {
            wheel.YZ += (-trackPlane.YZ * U.cos(XZ) - wheel.YZ) * .25;
            wheel.Y = setY;
           }
           wheel.XY += (trackPlane.YZ * U.sin(XZ) - wheel.XY) * .25;
          }
         } else if (trackPlane.XY != 0) {
          double setY = trackY + (wheel.X - trackX) * (trackPlane.radiusY / trackPlane.radiusX) * (trackPlane.XY > 0 ? 1 : trackPlane.XY < 0 ? -1 : 0);
          if (wheel.Y >= setY - 100) {
           wheel.angledSurface = true;
           mode = Mode.drive;
           if (!trackProperties.contains(" hard ")) {
            deployDust(wheel);
           } else if (flipped) {
            wheel.sparks(true);
           }
           if (Y >= trackY || wheel.speedY >= 0 || Math.abs(speed) < E.gravity * 4 * VE.tick || (Math.abs((U.sin(XZ) < 0 ? -YZ : YZ) - trackPlane.XY) < 30)) {
            wheel.YZ += (trackPlane.XY * U.sin(XZ) - wheel.YZ) * .25;
            wheel.Y = setY;
           }
           wheel.XY += (trackPlane.XY * U.cos(XZ) - wheel.XY) * .25;
          }
         }
        }
       }
       if (isWall) {
        double vehicleRadius = collisionRadius * .5, contactX = trackPlane.radiusX + vehicleRadius, contactZ = trackPlane.radiusZ + vehicleRadius;
        if (inX && Math.abs(wheel.Z - trackZ) <= contactZ) {
         if (trackPlane.wall == TrackPlane.Wall.front && wheel.Z < trackZ + contactZ && wheel.speedZ < 0) {
          for (Wheel otherWheel : wheels) {
           otherWheel.Z -= wheel != otherWheel && otherWheel.Z >= trackZ + contactZ ? wheel.Z - (trackZ + contactZ) : 0;
          }
          wheel.Z = trackZ + contactZ;
          if (trackProperties.contains(" hard ")) {
           wheel.sparks(false);
          }
          runCrash(wheel.speedZ * trackPlane.damage * .1);
          wheel.speedZ += Math.abs(wheel.speedZ) * bounce * wallPlaneBounce;
          spinnerHit = wheel.againstWall = true;
         }
         if (trackPlane.wall == TrackPlane.Wall.back && wheel.Z > trackZ - contactZ && wheel.speedZ > 0) {
          for (Wheel otherWheel : wheels) {
           otherWheel.Z -= wheel != otherWheel && otherWheel.Z <= trackZ - contactZ ? wheel.Z - (trackZ - contactZ) : 0;
          }
          wheel.Z = trackZ - contactZ;
          if (trackProperties.contains(" hard ")) {
           wheel.sparks(false);
          }
          runCrash(wheel.speedZ * trackPlane.damage * .1);
          wheel.speedZ -= Math.abs(wheel.speedZ) * bounce * wallPlaneBounce;
          spinnerHit = wheel.againstWall = true;
         }
        }
        if (inZ && Math.abs(wheel.X - trackX) <= contactX) {
         if (trackPlane.wall == TrackPlane.Wall.right && wheel.X < trackX + contactX && wheel.speedX < 0) {
          for (Wheel otherWheel : wheels) {
           otherWheel.X -= wheel != otherWheel && otherWheel.X >= trackX + contactX ? wheel.X - (trackX + contactX) : 0;
          }
          wheel.X = trackX + contactX;
          if (trackProperties.contains(" hard ")) {
           wheel.sparks(false);
          }
          runCrash(wheel.speedX * trackPlane.damage * .1);
          wheel.speedX += Math.abs(wheel.speedX) * bounce * wallPlaneBounce;
          spinnerHit = wheel.againstWall = true;
         }
         if (trackPlane.wall == TrackPlane.Wall.left && wheel.X > trackX - contactX && wheel.speedX > 0) {
          for (Wheel otherWheel : wheels) {
           otherWheel.X -= wheel != otherWheel && otherWheel.X <= trackX - contactX ? wheel.X - (trackX - contactX) : 0;
          }
          wheel.X = trackX - contactX;
          if (trackProperties.contains(" hard ")) {
           wheel.sparks(false);
          }
          runCrash(wheel.speedX * trackPlane.damage * .1);
          wheel.speedX -= Math.abs(wheel.speedX) * bounce * wallPlaneBounce;
          spinnerHit = wheel.againstWall = true;
         }
        }
       }
      }
     }
    }
   }
  }
  if (spinnerHit) {
   spinnerHit(null);
  }
 }

 private void setTerrainFromTrackPlanes(double gravityCompensation) {
  for (TrackPart trackPart : TE.trackParts) {
   if (!trackPart.trackPlanes.isEmpty() && U.distance(X, trackPart.X, Z, trackPart.Z) < trackPart.renderRadius + renderRadius) {
    for (TrackPlane trackPlane : trackPart.trackPlanes) {
     if (trackPlane.wall == TrackPlane.Wall.none && !trackPlane.type.contains(" tree ") && !trackPlane.type.contains("gate")) {
      double trackX = trackPlane.X + trackPart.X, trackY = trackPlane.Y + trackPart.Y, trackZ = trackPlane.Z + trackPart.Z;
      String addHard = U.contains(trackPlane.type, " paved ", " rock ", " grid ", " antigravity ", " metal ", " brightmetal") ? " hard " : " ground ";
      if (Math.abs(X - trackX) <= trackPlane.radiusX && Math.abs(Z - trackZ) <= trackPlane.radiusZ && Math.abs(Y - (trackY + gravityCompensation)) <= trackPlane.radiusY) {
       if (trackPlane.YZ == 0 && trackPlane.XY == 0) {
        terrainProperties = trackPlane.type + addHard;
        for (Wheel wheel : wheels) {
         wheel.terrainRGB[0] = trackPlane.RGB[0];
         wheel.terrainRGB[1] = trackPlane.RGB[1];
         wheel.terrainRGB[2] = trackPlane.RGB[2];
        }
       } else if (trackPlane.YZ != 0) {
        double setY = trackY + (Z - trackZ) * (trackPlane.radiusY / trackPlane.radiusZ) * (trackPlane.YZ > 0 ? 1 : trackPlane.YZ < 0 ? -1 : 0);
        if (Y >= setY - 100) {
         terrainProperties = trackPlane.type + addHard;
         for (Wheel wheel : wheels) {
          wheel.terrainRGB[0] = trackPlane.RGB[0];
          wheel.terrainRGB[1] = trackPlane.RGB[1];
          wheel.terrainRGB[2] = trackPlane.RGB[2];
         }
        }
       } else if (trackPlane.XY != 0) {
        double setY = trackY + (X - trackX) * (trackPlane.radiusY / trackPlane.radiusX) * (trackPlane.XY > 0 ? 1 : trackPlane.XY < 0 ? -1 : 0);
        if (Y >= setY - 100) {
         terrainProperties = trackPlane.type + addHard;
         for (Wheel wheel : wheels) {
          wheel.terrainRGB[0] = trackPlane.RGB[0];
          wheel.terrainRGB[1] = trackPlane.RGB[1];
          wheel.terrainRGB[2] = trackPlane.RGB[2];
         }
        }
       }
      }
     }
    }
   }
  }
 }

 private void runPhysicsStunts(boolean replay) {
  if (mode.name().startsWith(Mode.drive.name())) {
   if ((stuntTimer += VE.tick) > stuntLandWaitTime && !gotStunt) {
    if (!flipped || landStuntsBothSides) {
     double rollReward = Math.abs(stuntXY) > 135 ? Math.abs(stuntXY) * .75 : rollCheck[0] || rollCheck[1] ? 270 : 0;
     rollReward *= rollCheck[0] && rollCheck[1] ? 2 : 1;
     double flipReward = Math.abs(stuntYZ) > 135 ? Math.abs(stuntYZ) : flipCheck[0] || flipCheck[1] ? 360 : 0;
     flipReward *= flipCheck[0] && flipCheck[1] ? 2 : 1;
     double spinReward = Math.abs(stuntXZ) >= 180 ? Math.abs(stuntXZ) * .75 : spinCheck[0] || spinCheck[1] ? 270 : 0;
     spinReward *= spinCheck[0] && spinCheck[1] ? 2 : 1;
     stuntReward = (rollReward + flipReward + spinReward) * (offTheEdge ? 2 : 1);
     VE.scoreStunt[index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : stuntReward;
     VE.processStuntForUI(this);
    }
    stuntXY = stuntYZ = stuntXZ = 0;
    flipCheck[0] = flipCheck[1] = rollCheck[0] = rollCheck[1] = spinCheck[0] = spinCheck[1] = offTheEdge = false;
    AI.airRotationDirection[0] = U.random() < .5 ? 1 : -1;
    AI.airRotationDirection[1] = U.random() < .5 ? 1 : -1;
    gotStunt = true;
   }
   if (!flipped || landStuntsBothSides) {
    flipTimer = 0;
   } else if ((flipTimer += VE.tick) > 39) {
    XZ += Math.abs(YZ) > 90 ? 180 : 0;
    speed = XY = YZ = flipTimer = 0;
   }
  } else {
   stuntTimer = 0;
   gotStunt = false;
   if (mode == Mode.stunt) {
    stuntXY += 20 * stuntSpeedXY * VE.tick;
    rollCheck[0] = stuntXY > 135 || rollCheck[0];
    rollCheck[1] = stuntXY < -135 || rollCheck[1];
    stuntYZ -= 20 * stuntSpeedYZ * VE.tick;
    flipCheck[0] = stuntYZ > 135 || flipCheck[0];
    flipCheck[1] = stuntYZ < -135 || flipCheck[1];
    stuntXZ += 20 * stuntSpeedXZ * VE.tick;
    spinCheck[0] = stuntXZ > 135 || spinCheck[0];
    spinCheck[1] = stuntXZ < -135 || spinCheck[1];
   }
   offTheEdge = (wheels.get(0).againstWall || wheels.get(1).againstWall || wheels.get(2).againstWall || wheels.get(3).againstWall) || offTheEdge;
  }
 }

 private void fix(boolean gamePlay) {
  if (!reviveImmortality && (explosionType != ExplosionType.maxnuclear || damage <= durability)) {
   damage = 0;
   for (FixSphere fixSphere : fixSpheres) {
    fixSphere.deploy();
   }
   if (gamePlay) {
    VA.fix.play(VA.vehicleToCameraSoundDistance);
   }
  }
 }

 public void miscellaneous() {
  steerByMouse = index == VE.userPlayer && VE.cursorDriving;
  int n;
  boolean gamePlay = VE.status == VE.Status.play || VE.status == VE.Status.replay;
  inDriverView = index == VE.vehiclePerspective && Camera.view == Camera.View.driver;
  if (!specials.isEmpty()) {
   phantomEngaged = false;
   for (Special special : specials) {
    if (special.fire && special.type == Special.Type.phantom) {
     phantomEngaged = true;
     break;
    }
   }
  }
  VA.vehicleToCameraSoundDistance = index == VE.vehiclePerspective && Camera.view == Camera.View.driver ? 0 : Math.sqrt(distanceToCamera) * .08;
  massiveHitTimer -= massiveHitTimer > 0 ? VE.tick : 0;
  if (screenFlash > 0) {
   screenFlash -= VE.tick * (explosionType == ExplosionType.maxnuclear ? .01 : .1);
  }
  if (cameraShake > 0) {
   cameraShakeXZ = U.randomPlusMinus(cameraShake * cameraShake);
   cameraShakeYZ = U.randomPlusMinus(cameraShake * cameraShake);
   cameraShake -= VE.tick;
  } else {
   cameraShakeXZ = cameraShakeYZ = 0;
  }
  /* double multipleBase = 30 * VE.tick;
   if (Math.abs(cameraShakeXZ) <= multipleBase) {
    cameraShakeXZ = 0;
   } else {
    cameraShakeXZ -= multipleBase * (cameraShakeXZ > 0 ? 1 : -1);
   }
   if (Math.abs(cameraShakeYZ) <= multipleBase) {
    cameraShakeYZ = 0;
   } else {
    cameraShakeYZ -= multipleBase * (cameraShakeYZ > 0 ? 1 : -1);
   }*/
  inWrath = false;
  if (damage > durability) {
   for (VehiclePart part : parts) {
    part.explodeStage += part.explodeStage < 1 ? 1 : 0;
   }
   onFire = true;
   if (destructTimer <= 0) {
    VA.explode.play(VA.vehicleToCameraSoundDistance);
    setCameraShake(20);
    if (explosionsWhenDestroyed > 0) {
     VA.explode.play(1, VA.vehicleToCameraSoundDistance * .5);
     nukeDetonate();
    }
   }
   destroyed = (destructTimer += VE.tick) >= 8 || destroyed;
   if (destroyed && gamePlay) {
    VA.burn.loop(VA.vehicleToCameraSoundDistance);
   }
   runMaxNukeBlast(gamePlay);
  } else {
   destroyed = false;
   destructTimer = 0;
   onFire = VE.mapName.equals("the Sun") && onFire;
   damage = Math.max(0, damage - ((gamePlay ? selfRepair : 0) * VE.tick));
   double deformation = (damage / durability) * 6;
   for (VehiclePart part : parts) {
    part.damage.setAngle(U.clamp(-deformation, part.damage.getAngle(), deformation));
    part.explodeStage = part.explodeTimer = 0;
   }
  }
  if (VE.bonusHolder == index && passBonus) {
   for (Vehicle vehicle : VE.vehicles) {
    if (!U.sameVehicle(this, vehicle) && U.sameTeam(this, vehicle) && U.distance(this, vehicle) < collisionRadius + vehicle.collisionRadius) {
     TE.setBonusHolder(vehicle);
    }
   }
  }
  runPhysicsSpinner(gamePlay);
  runPhysicsHitTsunami();
  boolean isJet = U.contains(engine.name(), Engine.jet.name(), Engine.turbine.name(), Engine.rocket.name()),
  thrustDrive = drive2 || (drive && mode != Mode.fly);
  thrusting = !destroyed && ((speedBoost > 0 && boost) || exhaust > 0 || (mode != Mode.stunt && isJet &&
  (thrustDrive || (mode == Mode.fly && engine != Engine.turbine && engineClipQuantity * (Math.abs(speed) / topSpeeds[1]) >= 1))));
  if (thrusting) {
   boolean forceOut = isJet || (speedBoost > 0 && boost);
   for (VehiclePart part : parts) {
    if (part.thrustTrails != null) {
     for (n = 4; --n >= 0; ) {
      part.thrustTrails.get(part.currentThrustTrail).deploy(part, forceOut);
      part.currentThrustTrail = ++part.currentThrustTrail >= E.thrustTrailQuantity ? 0 : part.currentThrustTrail;
     }
    }
   }
  }
  if (TE.fixPointsExist && fixSpheres.get(U.random(fixSpheres.size())).stage <= 0 && !phantomEngaged) {
   for (TrackPart part : TE.trackParts) {
    if (part.isFixpoint) {
     boolean sideways = TE.isSidewaysXZ(part.XZ);
     if (U.distance(sideways ? Z : X, sideways ? part.Z : part.X, Y, part.Y) <= 500 && Math.abs(sideways ? X - part.X : Z - part.Z) <= 200 + Math.abs(netSpeedZ) * VE.tick) {
      fix(gamePlay);
     }
    }
   }
  }
  XY = !VE.matchStarted && engine == Engine.hotrod ? U.randomPlusMinus(2.) : XY;
  for (Special special : specials) {
   if (special.type == Special.Type.phantom) {
    if (special.fire) {
     for (VehiclePart part : parts) {
      if (U.random() < .5) {
       part.MV.setMaterial(E.phantomPM);
      }
     }
     if (gamePlay) {
      special.sound.loop(VA.vehicleToCameraSoundDistance);
     }
    } else {
     special.sound.stop();
     for (VehiclePart part : parts) {
      part.MV.setMaterial(part.PM);
     }
    }
   } else if (special.type == Special.Type.teleport && special.fire) {
    X += U.randomPlusMinus(50000.);
    Y += U.randomPlusMinus(50000.);
    Z += U.randomPlusMinus(50000.);
    if (gamePlay) {
     special.sound.play(VA.vehicleToCameraSoundDistance);
    }
   }
  }
  if (engine == Engine.train && gamePlay && VE.matchStarted) {
   n = U.random(9);
   if (Math.abs(speed) * VE.tick > U.random(5000.)) {
    VA.train.playIfNotPlaying(n, VA.vehicleToCameraSoundDistance);
   }
   if (U.startsWith(mode.name(), Mode.drive.name(), Mode.neutral.name()) && !destroyed && (drive || reverse)) {
    if (Math.abs(speed) > topSpeeds[1] * .75 && !VA.train.running(9)) {
     VA.train.playIfNotPlaying(10, VA.vehicleToCameraSoundDistance);
    } else if (!VA.train.running(10)) {
     VA.train.playIfNotPlaying(9, VA.vehicleToCameraSoundDistance);
    }
   }
  }
  if (VA.backUp != null) {
   if (gamePlay && speed < 0 && !destroyed) {
    VA.backUp.loop(VA.vehicleToCameraSoundDistance);
   } else {
    VA.backUp.stop();
   }
  }
  wheelDiscord = false;
  boolean againstEngine = false;
  if (damage <= durability && gamePlay) {
   if (VA.chuff != null && chuffTimer <= 0 && Math.abs(speed) < 1 && (drive || reverse)) {
    VA.chuff.play(U.random(VA.chuff.clips.size()), VA.vehicleToCameraSoundDistance);
    VA.chuff.play(U.random(VA.chuff.clips.size()), VA.vehicleToCameraSoundDistance);
    chuffTimer = 22;
   }
   if (VA.fly != null && mode == Mode.fly && (drive || reverse || turnL || turnR || (steerByMouse && (Math.abs(VE.mouseSteerX) > U.random(2000.) || Math.abs(VE.mouseSteerY) > U.random(2000.))))) {
    VA.fly.playIfNotPlaying(U.random(VA.fly.clips.size()), VA.vehicleToCameraSoundDistance);
   }
   if (vehicleType != Type.turret) {
    n = 0;
    if (mode != Mode.stunt) {
     n = vehicleType == Type.vehicle && steerInPlace && (turnL || turnR) ? 1 : n;
     boolean aircraft = vehicleType == Type.aircraft, flying = mode == Mode.fly,
     driveGet = !flying && aircraft ? drive || drive2 : flying ? drive2 : drive,
     reverseGet = !flying && aircraft ? reverse || reverse2 : flying ? reverse2 : reverse;
     if (driveGet || reverseGet || mode == Mode.fly) {
      n =
      driveGet && !(flipped && mode.name().startsWith(Mode.drive.name())) &&
      U.contains(engine.name(), "prop", Engine.jet.name(), Engine.turbine.name(), Engine.rocket.name()) && mode != Mode.fly ?
      engineClipQuantity - 1 :
      Math.max((int) (engineClipQuantity * (Math.abs(speed) / topSpeeds[1])), floats ? 0 : 1);
      if (VA.grind != null) {
       againstEngine = (driveGet && speed < 0) || (reverseGet && speed > 0);
       wheelDiscord = mode.name().startsWith(Mode.drive.name()) &&
       (Math.abs(wheels.get(U.random(4)).speedZ - (wheels.get(0).speedZ + wheels.get(1).speedZ + wheels.get(2).speedZ + wheels.get(3).speedZ) * .25) > 1 ||
       Math.abs(wheels.get(U.random(4)).speedX - (wheels.get(0).speedX + wheels.get(1).speedX + wheels.get(2).speedX + wheels.get(3).speedX) * .25) > 1);
      }
     }
    }
    n = engine == Engine.turbine ? Math.max((int) (engineClipQuantity * (Math.abs(netSpeed) / topSpeeds[1])), n) : n;
    enginePowerSwitch(n);
    engineStage = Math.min(n, engineClipQuantity - 1);
   }
  } else if (vehicleType != Type.turret) {
   enginePowerSwitch(-1);
   engineStage = -1;
  }
  if (VA.grind != null) {
   if (againstEngine) {
    VA.grind.loop(VA.vehicleToCameraSoundDistance);
   } else {
    VA.grind.stop();
   }
  }
  if (speedBoost > 0) {
   if (boost && gamePlay && !destroyed) {
    VA.boost.loop(VA.vehicleToCameraSoundDistance);
   } else {
    VA.boost.stop();
   }
  }
  if (vehicleType == Type.turret && (destroyed || (!turnL && !turnR && !drive && !reverse && !steerByMouse))) {
   VA.turret.stop();
  }
  if (gamePlay) {
   forceTimer += U.random(netSpeed) * VE.tick;
   int speedCheck = netSpeed > 1000 ? 5 : netSpeed > 500 ? 4 : netSpeed > 250 ? 3 : 2;
   if (steerInPlace && vehicleType != Type.turret && mode == Mode.drive && (turnL || turnR) && !flipped && speedCheck < 3) {
    VA.force.play(U.random(2), VA.vehicleToCameraSoundDistance);
    forceTimer = 0;
   }
   if (forceTimer > 800) {
    VA.force.play(U.random(speedCheck), VA.vehicleToCameraSoundDistance);
    forceTimer = 0;
   }
  }
  chuffTimer -= chuffTimer > 0 ? VE.tick : 0;
  crashTimer -= crashTimer > 0 ? VE.tick : 0;
  VA.landTimer -= VA.landTimer > 0 ? VE.tick : 0;
  if (!Double.isNaN(splashing)) {
   if (splashing > 150 && Camera.Y <= 0) {
    VA.splashOverSurface.loop(VA.vehicleToCameraSoundDistance);
    VA.splash.stop();
   } else if (splashing > 0) {
    VA.splash.loop(VA.vehicleToCameraSoundDistance);
    VA.splashOverSurface.stop();
   } else {
    VA.splash.stop();
    VA.splashOverSurface.stop();
   }
   splashing = 0;
  }
  if (!Double.isNaN(exhaust)) {
   exhaust -= exhaust > 0 ? VE.tick : 0;
   if (lastEngineStage != engineStage) {
    if (U.random() < 1 / (double) engineClipQuantity) {
     exhaust = 5;
     VA.randomExhaustSound = U.randomize(VA.randomExhaustSound, VA.exhaust.clips.size());
     VA.exhaust.play(VA.randomExhaustSound, VA.vehicleToCameraSoundDistance);
    }
    lastEngineStage = engineStage;
   }
  }
  if (VA.skid != null) {
   if (!skidding) {
    VA.skid.stop();
   } else if (!terrainProperties.contains(" hard ")) {
    for (n = 5; --n >= 0; ) {
     VA.skid.stop(n);
    }
   } else {
    for (n = 10; --n >= 5; ) {
     VA.skid.stop(n);
    }
   }
   skidding = false;
  }
  if (VA.scrape != null) {
   if (scraping && U.netValue(netSpeedX, netSpeedY, netSpeedZ) > 100) {
    int n1;
    for (n1 = VA.scrape.clips.size(); --n1 >= 0; ) {
     if (VA.scrape.running(n1)) {
      n1 = -2;
      break;
     }
    }
    if (n1 > -2) {
     VA.randomScrapeSound = U.randomize(VA.randomScrapeSound, VA.scrape.clips.size());
     VA.scrape.resume(VA.randomScrapeSound, VA.vehicleToCameraSoundDistance);
    }
   } else {
    VA.scrape.stop();
   }
   scraping = false;
  }
  if (VE.muteSound || !destroyed || !gamePlay) {
   VA.burn.stop();
   if (explosionType == ExplosionType.maxnuclear) {//<-DO check this, as stopping may cut out blast sound on Tactical Nuke
    VA.nuke.stop(1);
   }
  }
  if (VE.muteSound || damage > durability || !gamePlay) {
   stopSounds();
  }
 }

 private void enginePowerSwitch(int n) {
  n = Math.min(n, engineClipQuantity - 1);
  for (int n1 = engineClipQuantity; --n1 >= 0; ) {
   if (n1 != n) {
    VA.engine.stop(n1);
   }
  }
  if (n > -1) {
   VA.engine.loop(n, VA.vehicleToCameraSoundDistance);
   if (wheelDiscord) {
    VA.engine.randomizeFramePosition(n);
   }
   if (engine == Engine.turbine) {
    double thrustGain = Math.max(0, StrictMath.pow(1 / (Math.abs(netSpeed) / topSpeeds[1]), 4));
    VA.turbineThrust.loop(VA.vehicleToCameraSoundDistance + (n >= engineClipQuantity - 1 ? 0 : thrustGain));
   }
  } else if (engine == Engine.turbine) {
   VA.turbineThrust.stop();
  }
 }

 private void runPhysicsSpinner(boolean gamePlay) {
  if (!Double.isNaN(spinnerSpeed)) {
   if (gamePlay) {
    if (destroyed) {
     if (Math.abs(spinnerSpeed) < .01 * VE.tick) {
      spinnerSpeed = 0;
     } else {
      spinnerSpeed += .01 * (spinnerSpeed > 0 ? -1 : 1) * VE.tick;
     }
    } else {
     boolean runSpinner = false;
     for (Special special : specials) {
      if (special.fire) {
       runSpinner = true;
       break;
      }
     }
     if (runSpinner) {
      double speedChange = VE.tick * .005;
      spinnerSpeed += spinnerSpeed > 0 ? speedChange : spinnerSpeed < 0 ? -speedChange : (U.random() < .5 ? -1 : 1) * Double.MIN_VALUE;
     }
    }
   }
   while (spinnerXZ > 180) spinnerXZ -= 360;
   while (spinnerXZ < -180) spinnerXZ += 360;
   spinnerXZ += spinnerSpeed * 120 * VE.tick;
   spinnerSpeed = U.clamp(-1, spinnerSpeed, 1);
   int spinSound = (int) (Math.round(Math.abs(spinnerSpeed) * 9) - 2);
   for (int n = VA.spinner.clips.size(); --n >= 0; ) {
    if (n != spinSound) {
     VA.spinner.stop(n);
    }
   }
   if (gamePlay) {
    spinnerSpeed *= .999;
    if (spinSound >= 0) {
     VA.spinner.loop(spinSound, VA.vehicleToCameraSoundDistance);
    }
   } else if (spinSound >= 0) {
    VA.spinner.stop(spinSound);
   }
  }
 }

 private void spinnerHit(Vehicle vehicle) {
  if (U.random() < .5) {
   double absSpeed = Math.abs(spinnerSpeed), speedReduction = absSpeed > .95 ? 1 : U.random();
   if (vehicle != null) {
    if (absSpeed > .125) {
     double damageAmount = vehicle.durability * absSpeed * speedReduction + (speedReduction >= 1 ? Double.MIN_VALUE : 0);
     vehicle.damage += damageAmount;
     VE.scoreDamage[vehicle.index < VE.vehiclesInMatch >> 1 ? 0 : 1] += VE.status == VE.Status.replay ? 0 : damageAmount;
     hitCheck(vehicle);
     for (VehiclePart part : vehicle.parts) {
      part.deform();
      part.throwChip(U.randomPlusMinus(absSpeed * .5));
     }
    }
    if (vehicle.getsPushed >= 0) {
     double speedX = (U.random() < .5 ? 1 : -1) * renderRadius * absSpeed * speedReduction * 4,
     speedZ = (U.random() < .5 ? 1 : -1) * renderRadius * absSpeed * speedReduction * 4;
     for (Wheel wheel : vehicle.wheels) {
      wheel.speedX += speedX;
      wheel.speedZ += speedZ;
     }
    }
    if (vehicle.getsLifted >= 0) {
     double speedY = (U.random() < .5 ? 1 : -1) * renderRadius * absSpeed * speedReduction;
     for (Wheel wheel : vehicle.wheels) {
      wheel.speedY += speedY;
     }
    }
   }
   spinnerSpeed -= spinnerSpeed * speedReduction;
   if (absSpeed > .125) {
    if (speedReduction >= 1) {
     setCameraShake(30);
     VA.massiveHit.play(U.random(VA.massiveHit.clips.size()), VA.vehicleToCameraSoundDistance);
    } else if (absSpeed > .25) {
     setCameraShake(15);
     VA.crashHard.play(U.random(5), VA.vehicleToCameraSoundDistance);
    } else {
     VA.crashSoft.play(U.random(3), VA.vehicleToCameraSoundDistance);
    }
   }
  }
 }

 private void nukeDetonate() {
  if (explosionType.name().contains(ExplosionType.nuclear.name())) {
   screenFlash = 1;
   if (explosionType == ExplosionType.maxnuclear) {
    setCameraShake(100);
    nukeBlastSphereSize = 0;
    nukeBlastX = X;
    nukeBlastY = Y;
    nukeBlastZ = Z;
    U.add(nukeBlastSphere);
    for (NukeBlast nukeBlast : nukeBlasts) {
     nukeBlast.X = nukeBlastX;
     nukeBlast.Y = nukeBlastY;
     nukeBlast.Z = nukeBlastZ;
     nukeBlast.XZ = U.random(360.);
     nukeBlast.YZ = U.random(360.);
    }
    VA.nuke.play(VA.vehicleToCameraSoundDistance * .25);
   } else {
    setCameraShake(50);
    VA.nuke.play(U.random(VA.nuke.clips.size()), VA.vehicleToCameraSoundDistance * .5);
   }
  }
 }

 private void runMaxNukeBlast(boolean gamePlay) {
  if (explosionType == ExplosionType.maxnuclear) {
   if (U.render(nukeBlastX, nukeBlastY, nukeBlastZ, -nukeBlastSphereSize)) {
    U.setTranslate(nukeBlastSphere, nukeBlastX, nukeBlastY, nukeBlastZ);
    nukeBlastSphere.setVisible(true);
   } else {
    nukeBlastSphere.setVisible(false);
   }
   double blastSpeed = 6000 * VE.tick;
   if (gamePlay) {
    nukeBlastSphereSize += blastSpeed;
    U.setScale(nukeBlastSphere, nukeBlastSphereSize);
    VA.nuke.loop(1, Math.sqrt(Math.abs(U.distance(Camera.X, nukeBlastX, Camera.Y, nukeBlastY, Camera.Z, nukeBlastZ) - nukeBlastSphereSize)) * .04);
   } else {
    VA.nuke.stop(1);
   }
   ((PhongMaterial) nukeBlastSphere.getMaterial()).setSelfIlluminationMap(U.getImage("firelight" + U.random(3)));
   for (NukeBlast nukeBlast : nukeBlasts) {
    nukeBlast.run(gamePlay, blastSpeed);
   }
  }
 }

 private void setCameraShake(double in) {
  cameraShake = Math.max(cameraShake, in);
 }

 private void stopSounds() {
  if (VA != null) VA.stop(this);
 }

 public void closeSounds() {
  if (VA != null) VA.close();
 }
}
