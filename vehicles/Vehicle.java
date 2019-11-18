package ve.vehicles;

import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import ve.*;
import ve.effects.Dust;
import ve.effects.Explosion;
import ve.effects.Smoke;
import ve.effects.Spark;
import ve.environment.*;
import ve.trackElements.TE;
import ve.trackElements.trackParts.TrackPart;
import ve.utilities.Quaternion;
import ve.utilities.SL;
import ve.utilities.U;
import ve.vehicles.specials.Port;
import ve.vehicles.specials.Special;
import ve.vehicles.specials.Spinner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Vehicle extends Instance {

 public Physics P;
 public AI AI;
 public final VehicleAudio VA = new VehicleAudio(this);
 public Spinner spinner;
 MaxNukeBlast MNB;
 public final List<VehiclePart> parts = new ArrayList<>();
 //CORE PROPERTIES--keep the order!
 public String name = "";
 public Type type = Vehicle.Type.vehicle;
 boolean floats;
 public final double[] accelerationStages = new double[2];
 public final double[] topSpeeds = {0, 0, Double.POSITIVE_INFINITY};
 public double turnRate;
 double maxTurn;
 double randomTurn;
 double brake;
 double grip;
 double drag;
 double bounce;
 double shockAbsorb = Double.NaN;
 public double airAcceleration;
 double airTopSpeed;
 double airPush;
 boolean aerialControlEnhanced;
 double sidewaysLandingAngle;
 boolean landStuntsBothSides;
 double pushesOthers;
 public double getsPushed;
 double liftsOthers;
 public double getsLifted;
 public final double[] damageDealt = new double[4];
 double turretBaseDamageDealt;
 public double durability, fragility, selfRepair;
 double spin;
 boolean turnDrag;
 boolean steerInPlace;
 public double speedBoost;
 Physics.Landing landType = Physics.Landing.tires;
 Physics.Contact contact = Physics.Contact.none;
 double exhausting = Double.NaN;
 double othersAvoidAt;
 Engine engine = Engine.none;
 public boolean amphibious;
 public double driverViewY, driverViewZ /*driverViewX*/, extraViewHeight;
 ve.vehicles.AI.Behavior behavior = ve.vehicles.AI.Behavior.adapt;//<-The whole AI class would have to be needlessly instantiated every time if this were in AI
 public final List<Special> specials = new ArrayList<>();
 public ExplosionType explosionType = ExplosionType.none;
 long explosionsWhenDestroyed;
 //
 private String wheelTextureType = "";
 private double damage;
 public boolean destroyed;
 public double energyMultiple = 1;
 public double cameraShake;
 public PointLight burnLight;
 public final int index;
 public int checkpointsPassed;
 public int point;
 public final double height;
 double steerAngleMultiply = 1;
 public double lightBrightness;
 public double screenFlash;
 final boolean realVehicle;
 public boolean phantomEngaged;
 public boolean drive, reverse, turnL, turnR, handbrake;
 private boolean passBonus;
 boolean drive2, reverse2;
 public boolean boost;
 boolean steerByMouse;
 boolean onFire;
 public boolean inDriverView;
 private boolean gotStunt;
 public boolean offTheEdge;
 boolean reviveImmortality;
 public boolean thrusting;
 public final boolean[] rollCheck = new boolean[2], flipCheck = new boolean[2], spinCheck = new boolean[2];
 Death death = Death.none;
 public final List<Wheel> wheels = new ArrayList<>();
 private final List<Dust> dusts = new ArrayList<>();
 private int currentDust;
 final List<Splash> splashes = new ArrayList<>();
 int currentSplash;
 public final List<repairSphere> repairSpheres = new ArrayList<>();
 private static final long maxSpecials = 3;
 public boolean hasShooting;
 public final List<Explosion> explosions = new ArrayList<>();
 public int currentExplosion;

 public enum Type {vehicle, aircraft, turret, supportInfrastructure}

 enum Engine {//<-The engine holds more info than just audio, so keep in this class
  none, normal, tiny, agera, aventador, veyron, chiron, hotrod, huayra, laferrari, minicooper, p1, s7, turboracer,
  retro, electric,
  smalltruck, bigtruck, authentictruck, monstertruck, humvee, tank, smallcraft, turbo, power, massive, train,
  smallprop, bigprop,
  jet, brightjet, powerjet, torchjet, jetfighter, turbine, smallrocket, rocket, bigrocket, turborocket
 }

 public enum ExplosionType {none, normal, nuclear, maxnuclear}

 enum Death {none, diedAlone, killedByAnother}

 public VehicleTurret VT;

 public Vehicle(int model, int listIndex, boolean isReal) {
  this(model, listIndex, isReal, true);
 }

 public Vehicle(int model, int listIndex, boolean isReal, boolean show) {
  modelNumber = model;
  index = listIndex;
  realVehicle = isReal;
  modelName = VE.vehicleModels.get(modelNumber);
  int n;
  theRandomColor = index == VE.userPlayerIndex ? VE.userRandomRGB/*VERIFY*/ : U.getColor(U.random(), U.random(), U.random());
  int wheelCount = 0;
  for (n = 4; --n >= 0; ) {
   wheels.add(new Wheel(this));
  }
  long lightsAdded = 0;
  boolean onModelPart = false, addWheel = false;
  List<Double> xx = new ArrayList<>(), yy = new ArrayList<>(), zz = new ArrayList<>();
  double[] translate = new double[3];
  Color RGB = U.getColor(0);
  double pivotX = 0, pivotY = 0, pivotZ = 0;
  StringBuilder properties = new StringBuilder(), wheelProperties = new StringBuilder(), rimProperties = new StringBuilder();
  String texture = "", s = "";
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
     properties.setLength(0);
     texture = "";
     pivotX = pivotY = pivotZ = 0;
    } else if (s.startsWith("><") && onModelPart) {//<-Redundant?
     double minimumX = Double.NEGATIVE_INFINITY, maximumX = Double.POSITIVE_INFINITY;
     for (double listX : xx) {
      minimumX = Math.max(minimumX, listX);
      maximumX = Math.min(maximumX, listX);
     }
     double averageX = (minimumX + maximumX) * .5;
     properties.append(averageX > 0 ? " R " : averageX < 0 ? " L " : U.random() < .5 ? " R " : " L ");
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
      parts.add(new VehiclePart(this, U.listToArray(xx), U.listToArray(yy), U.listToArray(zz), xx.size(), RGB, String.valueOf(properties), texture, pivotX, pivotY, pivotZ));
      xx.clear();
     }
     onModelPart = false;
    }
    RGB = getLoadColor(s, RGB);
    if (onModelPart) {
     if (s.startsWith("(")) {
      xx.add((U.getValue(s, 0) * modelSize * modelScale[0]) + translate[0]);
      yy.add((U.getValue(s, 1) * modelSize * modelScale[1]) + translate[1]);
      zz.add((U.getValue(s, 2) * modelSize * modelScale[2]) + translate[2]);
      if (!String.valueOf(properties).contains(" thrust ")) {
       int size = xx.size() - 1;
       addSizes(xx.get(size), yy.get(size), zz.get(size));
      }
     }
     if (xx.size() < 1) {
      texture = s.startsWith("texture(") ? U.getString(s, 0) : texture;
      properties.append(s.startsWith("cs") ? " fastCull" + (s.endsWith("B") ? "B" : s.endsWith("F") ? "F" : s.endsWith("R") ? "R" : s.endsWith("L") ? "L" : "") + " " : "");
      if (s.startsWith("lit")) {
       properties.append(" light ").append(s.endsWith("fire") ? " fire " : "");
       lightsAdded++;
      }
      append(properties, s, false, SL.Instance.noTexture, "noCrush",
      SL.Instance.reflect, SL.Instance.blink, "line", SL.Instance.selfIlluminate, SL.Instance.noSpecular, SL.Instance.shiny,
      "smokePoint", "thrustTrailPoint", SL.Instance.flick1, SL.Instance.flick2, "landingGear", "shake", "spinner", "driver",
      InstancePart.FaceFunction.conic.name(),
      InstancePart.FaceFunction.cylindric.name(),
      InstancePart.FaceFunction.strip.name(),
      InstancePart.FaceFunction.squares.name(),
      InstancePart.FaceFunction.triangles.name(),
      "base", "exterior");
      properties.append(s.startsWith("thrustWhite") ? " thrustWhite " : s.startsWith("thrustBlue") ? " thrustBlue " : s.startsWith("thrust") ? " thrust " : "");
      properties.append(s.startsWith("turretBarrel") ? " turretBarrel " : s.startsWith("turret") ? " turret " : "");
      if (s.startsWith(SL.Instance.controller)) {
       properties.append(" controller ").append(s.contains("XZ") ? " steerXZ " : s.contains("XY") ? " steerXY " : "");
      } else if (s.startsWith("wheel")) {
       properties.append(" wheel ");
       addWheel = s.startsWith("wheelPoint") || addWheel;
      } else if (s.startsWith("steer")) {
       properties.append(s.startsWith("steerXY") ? " steerXY " : s.startsWith("steerYZ") ? " steerYZ " : U.startsWith(s, "steerXZ", "steers") ? " steerXZ " : "");
       properties.append(s.startsWith("steerFromYZ") ? " steerFromYZ " : s.startsWith("steerFromXZ") ? " steerFromXZ " : "");
      } else if (s.startsWith("pivot(")) {
       pivotX = U.getValue(s, 0) * modelSize * modelScale[0];
       pivotY = U.getValue(s, 1) * modelSize * modelScale[1];
       pivotZ = U.getValue(s, 2) * modelSize * modelScale[2];
      }
     }
    }
    name = s.startsWith("name(") ? U.getString(s, 0) : name;
    VE.vehicleMaker = s.startsWith("maker(") ? U.getString(s, 0) : VE.vehicleMaker;
    if (s.startsWith("type(")) {
     type = Type.valueOf(U.getString(s, 0));
     floats = s.contains("floats") || floats;
    } else if (s.startsWith("acceleration(")) {
     accelerationStages[0] = U.getValue(s, 0);
     accelerationStages[1] = U.getValue(s, 1);
    } else if (s.startsWith("speeds(")) {
     topSpeeds[0] = U.getValue(s, 0);
     topSpeeds[1] = U.getValue(s, 1);
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
     landType = Physics.Landing.valueOf(U.getString(s, 0));
    } else if (s.startsWith("contact(") && !U.getString(s, 0).isEmpty()) {
     contact = Physics.Contact.valueOf(U.getString(s, 0));
    }
    exhausting = s.startsWith("exhaustFire(yes") ? 0 : exhausting;
    othersAvoidAt = s.startsWith("othersAvoidAt(") ? U.getValue(s, 0) : othersAvoidAt;
    if (s.startsWith("engine(")) {
     engine = Engine.valueOf(U.getString(s, 0));
     try {
      VA.engineClipQuantity = (int) Math.round(U.getValue(s, 1));
      VA.engineTuneRatio = U.getValue(s, 2);
      VA.enginePitchBase = U.getValue(s, 3);
     } catch (RuntimeException ignored) {
     }
     VA.engineTuning = s.contains("harmonicSeries") ? VehicleAudio.EngineTuning.harmonicSeries : VA.engineTuning;
    }
    amphibious = s.startsWith("amphibious(yes") || amphibious;
    driverViewY = s.startsWith("driverViewY(") ? U.getValue(s, 0) * modelSize + translate[1] : driverViewY;
    driverViewZ = s.startsWith("driverViewZ(") ? U.getValue(s, 0) * modelSize + translate[2] : driverViewZ;
    driverViewX = s.startsWith("driverViewX(") ? Math.abs(U.getValue(s, 0) * modelSize) + translate[0] : driverViewX;
    extraViewHeight = s.startsWith("extraViewHeight(") ? U.getValue(s, 0) : extraViewHeight;
    if (s.startsWith("behavior(") && !U.getString(s, 0).isEmpty()) {
     behavior = ve.vehicles.AI.Behavior.valueOf(U.getString(s, 0));
    }
    if (s.startsWith("special(") && !U.getString(s, 0).isEmpty()) {
     addSpecial(s);
    }
    addSpecialProperties(s);
    if (s.startsWith("explosion(") && !U.getString(s, 0).isEmpty()) {
     explosionType = ExplosionType.valueOf(U.getString(s, 0));
    }
    explosionsWhenDestroyed = s.startsWith("explodeWhenDestroyed(") ? Math.round(U.getValue(s, 0)) : explosionsWhenDestroyed;
    if (s.startsWith("vehicleTurret(")) {
     VT = new VehicleTurret(this, s);
    }
    getSizeScaleTranslate(s, translate, 1, new double[]{1, 1, 1});
    if (s.startsWith("wheelColor(")) {
     if (s.contains(SL.Instance.reflect)) {
      wheelProperties.append(" reflect ");
     } else {
      try {
       wheelRGB = U.getColor(U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
      } catch (RuntimeException E) {
       if (s.contains(SL.Instance.theRandomColor)) {
        wheelRGB = theRandomColor;
        wheelProperties.append(" theRandomColor ");
       } else {
        wheelRGB = U.getColor(U.getValue(s, 0));
       }
      }
     }
     append(wheelProperties, s, true, SL.Instance.noSpecular, SL.Instance.shiny);
    } else if (s.startsWith("rims(")) {
     if (!show) {
      break;
     }
     rimProperties.setLength(0);
     rimRadius = U.getValue(s, 0) * modelSize;
     rimDepth = Math.max(rimRadius * .0625, U.getValue(s, 1) * modelSize);
     try {
      rimRGB = U.getColor(U.getValue(s, 2), U.getValue(s, 3), U.getValue(s, 4));
     } catch (RuntimeException E) {
      if (s.contains(SL.Instance.theRandomColor)) {
       rimRGB = theRandomColor;
       rimProperties.append(" theRandomColor ");
      } else {
       rimRGB = U.getColor(U.getValue(s, 2));
      }
     }
     append(rimProperties, s, true, SL.Instance.reflect, SL.Instance.noSpecular, SL.Instance.shiny, SL.Instance.sport);
    }
    wheelProperties.append(s.startsWith("landingGearWheels") ? " landingGear " : "");
    wheelTextureType = s.startsWith("wheelTexture(") ? U.getString(s, 0) : wheelTextureType;//<-Using 'append' would mess this up if found more than once in file
    wheelSmoothing = s.startsWith("smoothing(") ? U.getValue(s, 0) * modelSize : wheelSmoothing;
    if (s.startsWith("wheel(")) {
     if (!show) {
      break;
     }
     if (wheelCount < 4) {
      wheels.get(wheelCount).pointX = U.getValue(s, 0) * modelSize * modelScale[0];
      wheels.get(wheelCount).pointZ = U.getValue(s, 2) * modelSize * modelScale[2];
      wheels.get(wheelCount).skidmarkSize = Math.abs(U.getValue(s, 3)) * modelSize * modelScale[0];
      wheels.get(wheelCount).sparkPoint = U.getValue(s, 4) * 2 * modelSize;
     }
     String side = U.getValue(s, 0) > 0 ? " R " : U.getValue(s, 0) < 0 ? " L " : U.random() < .5 ? " R " : " L ";
     loadWheel(this, null, U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2), U.getValue(s, 3), U.getValue(s, 4), wheelProperties + side, String.valueOf(rimProperties), wheelTextureType, s.contains("steers"), s.contains("hide"));
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
   parts.add(new VehiclePart(this, new double[1], new double[1], new double[1], 1, U.getColor(1), " light ", ""));
  }
  maxMinusX[1] /= vertexQuantity;
  maxPlusX[1] /= vertexQuantity;
  maxMinusY[1] /= vertexQuantity;
  maxPlusY[1] /= vertexQuantity;
  maxMinusZ[1] /= vertexQuantity;
  maxPlusZ[1] /= vertexQuantity;
  height = Math.abs(maxMinusY[0]) + Math.abs(maxPlusY[0]) + Math.abs(maxMinusY[1]) + Math.abs(maxPlusY[1]);
  absoluteRadius = Math.abs(maxMinusX[0]) + Math.abs(maxPlusX[0]) + Math.abs(maxMinusZ[0]) + Math.abs(maxPlusZ[0]) + Math.abs(maxMinusX[1]) + Math.abs(maxPlusX[1]) + Math.abs(maxMinusZ[1]) + Math.abs(maxPlusZ[1]) + height;
  if (isFixed()) {
   getsPushed = getsLifted = -1;
   behavior = ve.vehicles.AI.Behavior.engageOthers;
  }
  for (Special special : specials) {
   if (special.type == Special.Type.spinner) {
    spinner = new Spinner(this);
    break;
   }
  }
  explosionType = explosionsWhenDestroyed > 0 && !explosionType.name().contains(ExplosionType.nuclear.name()) ? ExplosionType.normal : explosionType;
  X = Y = Z = XZ = 0;
  for (VehiclePart part : parts) {
   U.Nodes.add(part.MV);
  }
  if (realVehicle) {
   P = new Physics(this);
   if (E.Pool.exists || !Tsunami.parts.isEmpty()) {
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
      U.Nodes.add(trail.B);
     }
    }
   }
   if (VE.Map.defaultVehicleLightBrightness > 0) {
    burnLight = new PointLight();
   }
   if (explosionType == ExplosionType.maxnuclear) {
    MNB = new MaxNukeBlast(this);
   }
   TE.setVehicleMatchStartPlacement(this);
   loadVehicle();
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
   if (!S.type.name().contains("particle") && S.type != Special.Type.spinner) {
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
      S.ports.get(n1).Y = U.getValue(s, S.type == Special.Type.shotgun ? 0 : n1) * modelSize;
     } catch (RuntimeException e) {
      S.ports.remove(S.ports.size() - 1);
      break;
     }
    }
   } else if (s.startsWith("gunX(")) {
    try {
     for (Port port : S.ports) {
      port.X = U.getValue(s, S.ports.indexOf(port)) * modelSize;
     }
    } catch (RuntimeException E) {
     for (Port port : S.ports) {
      port.X = U.getValue(s, 0) * modelSize;
     }
    }
   } else if (s.startsWith("gunZ(")) {
    try {
     for (Port port : S.ports) {
      port.Z = U.getValue(s, S.ports.indexOf(port)) * modelSize;
     }
    } catch (RuntimeException E) {
     for (Port port : S.ports) {
      port.Z = U.getValue(s, 0) * modelSize;
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
    S.randomPosition = U.getValue(s, 0) * modelSize;
   } else if (s.startsWith("gunRandomAngle(")) {
    S.randomAngle = U.getValue(s, 0);
   }
  }
 }

 public void addTransparents() {
  if (realVehicle) {
   int n;
   if (contact == Physics.Contact.rubber || E.Terrain.terrain.contains(" snow ")) {
    Color C = U.getColor(wheelRGB.getRed() * .5, wheelRGB.getGreen() * .5, wheelRGB.getBlue() * .5);
    if (!wheelTextureType.isEmpty()) {
     C = U.getColor(C.getRed() * .333, C.getGreen() * .333, C.getBlue() * .333);
    }
    for (Wheel wheel : wheels) {
     wheel.skidmarks = new ArrayList<>();
     for (n = 48; --n >= 0; ) {
      wheel.skidmarks.add(new Skidmark(wheel, C));
     }
    }
   }
   for (n = 32; --n >= 0; ) {
    repairSpheres.add(new repairSphere(this));
   }
   if (!isFixed()) {
    for (n = E.dustQuantity; --n >= 0; ) {
     dusts.add(new Dust());
    }
   }
   for (VehiclePart part : parts) {
    if (part.smokes != null) {
     for (Smoke smoke : part.smokes) {
      U.Nodes.add(smoke.C);
     }
    }
   }
  }
 }

 private void loadVehicle() {
  P.cameraXZ = XZ;
  for (Wheel wheel : wheels) {
   wheel.X = X;
   wheel.Y = Y;
   wheel.Z = Z;
  }
  P.wheelGapFrontToBack = Math.max(Math.abs(wheels.get(0).pointZ - wheels.get(2).pointZ), Math.abs(wheels.get(1).pointZ - wheels.get(3).pointZ));
  P.wheelGapLeftToRight = Math.max(Math.abs(wheels.get(0).pointX - wheels.get(1).pointX), Math.abs(wheels.get(2).pointX - wheels.get(3).pointX));
  for (Special special : specials) {
   special.time();
  }
  int n;
  for (Special special : specials) {
   special.load();
  }
  if (!explosionType.name().contains(ExplosionType.nuclear.name())) {
   P.explosionDiameter = 500;
   P.explosionDamage = 250;
  }
  P.explosionPush = 500;
  if (explosionType != ExplosionType.none) {
   for (n = E.explosionQuantity; --n >= 0; ) {
    explosions.add(new Explosion(this));
   }
  }
  AI = new AI(this);
  VA.load();
  double volcanoDistance = U.distance(X, Volcano.X, Z, Volcano.Z);
  P.onVolcano = Volcano.exists && volcanoDistance < Volcano.radiusBottom && volcanoDistance > Volcano.radiusTop && Y > -Volcano.radiusBottom + volcanoDistance;
  Y = P.onVolcano ? Math.min(Y, -Volcano.radiusBottom + volcanoDistance) - (isFixed() ? turretBaseY : 0) : Y;
  P.atPoolXZ = E.Pool.exists && U.distance(X, E.Pool.X, Z, E.Pool.Z) < E.Pool.C[0].getRadius();
  P.inPool = P.atPoolXZ && Y + clearanceY > 0;
  P.localVehicleGround = E.Ground.level + (P.atPoolXZ ? E.Pool.depth : 0);
  lightBrightness = VE.Map.defaultVehicleLightBrightness;
 }

 public boolean isFixed() {
  return type == Type.turret || type == Type.supportInfrastructure;
 }

 public double collisionRadius() {
  return absoluteRadius * .3;
 }

 public void addDamage(double in) {
  damage = U.clamp(0, damage + in, damageCeiling());
 }

 public void setDamage(double in) {
  damage = U.clamp(0, in, damageCeiling());
 }

 public double getDamage(boolean percent) {
  return percent ? damage / durability : damage;
 }

 public double damageCeiling() {
  return durability * 1.004;//<-Always rounds to '100%' in UI
 }

 public boolean isIntegral() {
  return damage <= durability;
 }

 public void runGraphics(boolean gamePlay) {
  while (YZ < -180) YZ += 360;
  while (YZ > 180) YZ -= 360;
  while (XY < -180) XY += 360;
  while (XY > 180) XY -= 360;
  while (XZ < -180) XZ += 360;
  while (XZ > 180) XZ -= 360;
  distanceToCamera = U.distance(this);
  boolean nullPhysics = P == null;
  for (VehiclePart part : parts) {
   part.setPosition(nullPhysics);
  }
  boolean renderALL = E.renderType == E.RenderType.ALL;
  if (renderALL || E.renderType == E.RenderType.fullDistance || distanceToCamera < E.viewableMapDistance + collisionRadius()) {
   onFire = VE.Map.name.equals("the Sun") || onFire;
   rotation.set();
   rotation.multiply(0, 0, -U.sin(XY * .5), U.cos(XY * .5));//<-Mind the multiply order!
   rotation.multiply(-U.sin(YZ * .5), 0, 0, U.cos(YZ * .5));
   rotation.multiply(0, U.sin(XZ * .5), 0, U.cos(XZ * .5));
   for (VehiclePart part : parts) {
    part.render(nullPhysics, renderALL);
   }
  }
  double smokeEmitProbability = nullPhysics || destroyed ? 0 : (P.mode.name().contains(Physics.Mode.drive.name()) || P.mode == Physics.Mode.neutral) && (drive || reverse) ? 1 : .25;
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
   if (wheel.skidmarks != null) {
    for (Skidmark skidmark : wheel.skidmarks) {
     skidmark.run();
    }
   }
  }
  for (Dust dust : dusts) {
   dust.run();
  }
  for (Splash splash : splashes) {
   splash.run();
  }
  long repairSpheresRemoved = 0;
  for (ve.vehicles.repairSphere repairSphere : repairSpheres) {
   repairSphere.run(this);
   repairSpheresRemoved += repairSphere.stage <= 0 ? 1 : 0;
  }
  reviveImmortality = repairSpheresRemoved < repairSpheres.size() && reviveImmortality;
  if (!isIntegral() && VE.Map.defaultVehicleLightBrightness > 0 && !parts.isEmpty() && distanceToCamera < E.viewableMapDistance) {
   U.Nodes.Light.setRGB(burnLight, .5, .25 + U.random(.2), U.random(.125));
   U.setTranslate(burnLight, this);
   U.Nodes.Light.add(burnLight);
  }
 }

 void deployDust(Wheel wheel, boolean groundHit) {
  if (!destroyed && !phantomEngaged && !P.terrainProperties.contains(" ice ")) {
   double dustSpeed = U.netValue(wheel.speedX, wheel.speedZ), speedDifference = P.flipped ? dustSpeed : Math.abs(Math.abs(P.speed) - dustSpeed);
   if (groundHit || speedDifference * .5 > 10 + U.random(5.) || (!P.flipped && Math.abs(P.speed) > 50 + U.random(50.))) {
    dusts.get(currentDust).deploy(this, wheel, dustSpeed, speedDifference);
    currentDust = ++currentDust >= E.dustQuantity ? 0 : currentDust;
   }
  }
 }

 public void getPlayerInput() {
  if (index == VE.userPlayerIndex && Network.mode == Network.Mode.OFF && VE.status != VE.Status.replay) {
   drive = VE.Keys.Up;
   reverse = VE.Keys.Down;
   turnL = VE.Keys.Left;
   turnR = VE.Keys.Right;
   handbrake = VE.Keys.Space;
   boost = VE.Keys.keyBoost;
   passBonus = VE.Keys.PassBonus;
   boolean turretExists = VT != null, get2ndDrive = false;
   if (turretExists && !VT.hasAutoAim) {
    get2ndDrive = true;
    VT.turnL = VE.Keys.A;
    VT.turnR = VE.Keys.D;
   }
   if (type == Type.aircraft || get2ndDrive) {
    drive2 = VE.Keys.W;
    reverse2 = VE.Keys.S;
   }
   for (Special special : specials) {
    if (special.aimType != Special.AimType.auto) {
     boolean shootWithCanceledSteer = special.aimType != Special.AimType.normal && special.type != Special.Type.mine && turretExists && VT.turnL && VT.turnR;
     special.fire = VE.Keys.Special[specials.indexOf(special)] || shootWithCanceledSteer;
    }
   }
  }
 }

 public void setTurretY() {
  if (isFixed()) {
   Y = 0;
   if (!U.equals(VE.Map.name, SL.MN.everybodyEverything, SL.MN.devilsStairwell)) {
    E.setTerrainSit(this, true);
   }
   Y -= turretBaseY;
  }
 }

 void runMoundInteract(double clearance, double gravityCompensation) {
  if (!phantomEngaged) {
   for (TrackPart TP : TE.trackParts) {
    if (TP.mound != null) {
     double distance = U.distance(X, TP.X, Z, TP.Z),
     radiusTop = TP.mound.getMinorRadius(), moundHeight = TP.mound.getHeight();
     if (distance < radiusTop) {
      if (Y - clearance <= TP.Y + gravityCompensation) {
       P.localVehicleGround = Math.min(P.localVehicleGround, TP.Y - moundHeight);
      }
     } else {
      double radiusBottom = TP.mound.getMajorRadius();
      if (distance < radiusBottom && Math.abs(Y + clearance - ((TP.Y - (moundHeight * .5)) + gravityCompensation)) <= moundHeight * .5) {
       double slope = moundHeight / Math.abs(radiusBottom - radiusTop),
       finalHeight = TP.Y - (radiusBottom - distance) * slope - clearance;
       if (Y >= finalHeight) {
        double baseAngle = U.arcTan(slope) + (P.flipped ? 180 : 0),
        vehicleMoundXZ = XZ, moundPlaneY = Math.max(radiusTop * .5, (radiusBottom * .5) - ((radiusBottom * .5) * (Math.abs(Y) / moundHeight)));
        vehicleMoundXZ += Z < TP.Z && Math.abs(X - TP.X) < moundPlaneY ? 180 : X >= TP.X + moundPlaneY ? 90 : X <= TP.X - moundPlaneY ? -90 : 0;
        XY += (baseAngle * U.sin(vehicleMoundXZ) - XY) * .5;
        YZ += (-baseAngle * U.cos(vehicleMoundXZ) - YZ) * .5;
        Y = finalHeight;
        P.mode = Physics.Mode.drive;
        P.terrainProperties = E.Terrain.vehicleDefaultTerrain;
        for (Wheel wheel : wheels) {
         wheel.terrainRGB = E.Ground.RGB;
        }
       }
      }
     }
    }
   }
  }
 }

 public void repair(boolean gamePlay) {
  if (!reviveImmortality && (explosionType != ExplosionType.maxnuclear || isIntegral())) {
   setDamage(0);
   for (repairSphere sphere : repairSpheres) {
    sphere.deploy();
   }
   if (gamePlay) {
    VA.repair.play(VA.distanceVehicleToCamera);
   }
  }
 }

 public void miscellaneous(boolean gamePlay) {
  steerByMouse = index == VE.userPlayerIndex && VE.Match.cursorDriving;
  int n;
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
  VA.distanceVehicleToCamera = index == VE.vehiclePerspective && Camera.view == Camera.View.driver ? 0 : Math.sqrt(distanceToCamera) * .08;
  P.massiveHitTimer -= P.massiveHitTimer > 0 ? VE.tick : 0;
  if (screenFlash > 0) {
   screenFlash -= VE.tick * (explosionType == ExplosionType.maxnuclear ? .01 : .1);
  }
  cameraShake -= cameraShake > 0 ? VE.tick : 0;
  P.inWrath = false;
  if (isIntegral()) {
   destroyed = false;
   P.destructTimer = 0;
   onFire = VE.Map.name.equals("the Sun") && onFire;
   setDamage(getDamage(false) - (gamePlay ? selfRepair * VE.tick : 0));
   for (VehiclePart part : parts) {
    part.explodeStage = VehiclePart.ExplodeStage.intact;
   }
   P.subtractExplodeStage = false;
   P.explodeStage = 0;
  } else {
   onFire = true;
   if (P.destructTimer <= 0) {
    VA.explode.play(VA.distanceVehicleToCamera);
    setCameraShake(Camera.shakePresets.vehicleDeath);
    if (explosionsWhenDestroyed > 0) {
     VA.explode.play(1, VA.distanceVehicleToCamera * .5);
     nukeDetonate();
    }
   }
   destroyed = (P.destructTimer += VE.tick) >= 8 || destroyed;
   if (destroyed && gamePlay) {
    VA.burn.loop(VA.distanceVehicleToCamera);
   }
   if (gamePlay) {
    double multiple = explosionType == ExplosionType.maxnuclear ? Double.POSITIVE_INFINITY : explosionType == ExplosionType.nuclear ? 2 : 1;
    P.subtractExplodeStage = P.explodeStage > 100 * multiple || P.subtractExplodeStage;
    P.explodeStage += VE.tick * (P.subtractExplodeStage ? -1 : 1);
    if (P.explodeStage < 0) {
     repair(gamePlay);
     reviveImmortality = true;
    }
   }
  }
  if (MNB != null) {
   MNB.run(gamePlay);
  }
  double deformation = getDamage(true) * 6;
  for (VehiclePart part : parts) {//Ensures the visual deformation is never above the damage
   part.damage.setAngle(U.clamp(-deformation, part.damage.getAngle(), deformation));
  }
  if (VE.bonusHolder == index && passBonus) {
   for (Vehicle vehicle : VE.vehicles) {
    if (!U.sameVehicle(this, vehicle) && U.sameTeam(this, vehicle) && U.distance(this, vehicle) < collisionRadius() + vehicle.collisionRadius()) {
     TE.Bonus.setHolder(vehicle);//^Checking same team, so sameVehicle check IS needed
    }
   }
  }
  if (spinner != null) {
   spinner.run(gamePlay);
  }
  P.runHitTsunami();
  boolean isJet = U.contains(engine.name(), Engine.jet.name(), Engine.turbine.name(), Engine.rocket.name()),
  thrustDrive = drive2 || (drive && P.mode != Physics.Mode.fly);
  thrusting = !destroyed && ((speedBoost > 0 && boost) || exhausting > 0 || (P.mode != Physics.Mode.stunt && isJet &&
  (thrustDrive || (P.mode == Physics.Mode.fly && engine != Engine.turbine && VA.engineClipQuantity * (Math.abs(P.speed) / topSpeeds[1]) >= 1))));
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
  TE.runVehicleRepairPointInteraction(this, gamePlay);
  XY = !VE.Match.started && engine == Engine.hotrod ? U.randomPlusMinus(2.) : XY;
  for (Special special : specials) {
   if (special.type == Special.Type.phantom) {
    if (special.fire) {
     for (VehiclePart part : parts) {
      if (U.random() < .5) {
       U.setMaterialSecurely(part.MV, E.phantomPM);
      }
     }
     if (gamePlay) {
      special.sound.loop(VA.distanceVehicleToCamera);
     }
    } else {
     special.sound.stop();
     for (VehiclePart part : parts) {
      U.setMaterialSecurely(part.MV, part.PM);
     }
    }
   } else if (special.type == Special.Type.teleport && special.fire) {
    X += U.randomPlusMinus(50000.);
    Y += U.randomPlusMinus(50000.);
    Z += U.randomPlusMinus(50000.);
    if (gamePlay) {
     special.sound.play(VA.distanceVehicleToCamera);
    }
   }
  }
  P.wheelDiscord = VA.grind != null && P.mode.name().startsWith(Physics.Mode.drive.name()) &&
  (Math.abs(wheels.get(U.random(4)).speedZ - (wheels.get(0).speedZ + wheels.get(1).speedZ + wheels.get(2).speedZ + wheels.get(3).speedZ) * .25) > 1 ||
  Math.abs(wheels.get(U.random(4)).speedX - (wheels.get(0).speedX + wheels.get(1).speedX + wheels.get(2).speedX + wheels.get(3).speedX) * .25) > 1);
  VA.run(gamePlay);
 }

 private void nukeDetonate() {
  if (explosionType.name().contains(ExplosionType.nuclear.name())) {
   screenFlash = 1;
   if (explosionType == ExplosionType.maxnuclear) {
    MNB.setSingularity();
    setCameraShake(Camera.shakePresets.maxNuclear);
    VA.nuke.play(VA.distanceVehicleToCamera * .25);
   } else {
    setCameraShake(Camera.shakePresets.normalNuclear);
    VA.nuke.play(Double.NaN, VA.distanceVehicleToCamera * .5);
   }
  }
 }

 void runStuntScoring(boolean replay) {
  if (P.mode.name().startsWith(Physics.Mode.drive.name())) {
   if ((P.stuntTimer += VE.tick) > P.stuntLandWaitTime && !gotStunt) {
    if (!P.flipped || landStuntsBothSides) {
     double rollReward = Math.abs(P.stuntXY) > 135 ? Math.abs(P.stuntXY) * .75 : rollCheck[0] || rollCheck[1] ? 270 : 0;
     rollReward *= rollCheck[0] && rollCheck[1] ? 2 : 1;
     double flipReward = Math.abs(P.stuntYZ) > 135 ? Math.abs(P.stuntYZ) : flipCheck[0] || flipCheck[1] ? 360 : 0;
     flipReward *= flipCheck[0] && flipCheck[1] ? 2 : 1;
     double spinReward = Math.abs(P.stuntXZ) >= 180 ? Math.abs(P.stuntXZ) * .75 : spinCheck[0] || spinCheck[1] ? 270 : 0;
     spinReward *= spinCheck[0] && spinCheck[1] ? 2 : 1;
     P.stuntReward = (rollReward + flipReward + spinReward) * (offTheEdge ? 2 : 1);
     VE.Match.scoreStunt[index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : P.stuntReward;
     VE.Match.processStunt(this);
    }
    P.stuntXY = P.stuntYZ = P.stuntXZ = 0;
    flipCheck[0] = flipCheck[1] = rollCheck[0] = rollCheck[1] = spinCheck[0] = spinCheck[1] = offTheEdge = false;
    AI.airRotationDirection[0] = U.random() < .5 ? 1 : -1;
    AI.airRotationDirection[1] = U.random() < .5 ? 1 : -1;
    gotStunt = true;
   }
   if (!P.flipped || landStuntsBothSides) {
    P.flipTimer = 0;
   } else if ((P.flipTimer += VE.tick) > 39) {
    XZ += Math.abs(YZ) > 90 ? 180 : 0;
    P.speed = XY = YZ = P.flipTimer = 0;
   }
  } else {
   P.stuntTimer = 0;
   gotStunt = false;
   if (P.mode == Physics.Mode.stunt) {
    P.stuntXY += 20 * P.stuntSpeedXY * VE.tick;
    rollCheck[0] = P.stuntXY > 135 || rollCheck[0];
    rollCheck[1] = P.stuntXY < -135 || rollCheck[1];
    P.stuntYZ -= 20 * P.stuntSpeedYZ * VE.tick;
    flipCheck[0] = P.stuntYZ > 135 || flipCheck[0];
    flipCheck[1] = P.stuntYZ < -135 || flipCheck[1];
    P.stuntXZ += 20 * P.stuntSpeedXZ * VE.tick;
    spinCheck[0] = P.stuntXZ > 135 || spinCheck[0];
    spinCheck[1] = P.stuntXZ < -135 || spinCheck[1];
   }
   offTheEdge = P.againstWall() || offTheEdge;
  }
 }

 public void setCameraShake(double in) {
  cameraShake = Math.max(cameraShake, in);
 }

 public void closeSounds() {
  VA.close();
 }
}
