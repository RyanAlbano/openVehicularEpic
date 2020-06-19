package ve.vehicles;

import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import ve.effects.Dust;
import ve.effects.Smoke;
import ve.effects.Spark;
import ve.environment.*;
import ve.instances.I;
import ve.instances.Instance;
import ve.instances.InstancePart;
import ve.trackElements.Bonus;
import ve.trackElements.TE;
import ve.ui.Keys;
import ve.ui.Maps;
import ve.ui.Match;
import ve.ui.UI;
import ve.utilities.*;
import ve.utilities.sound.Controlled;
import ve.utilities.sound.Sounds;
import ve.vehicles.explosions.Explosion;
import ve.vehicles.explosions.MaxNukeBlast;
import ve.vehicles.specials.Port;
import ve.vehicles.specials.Shot;
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
 public MaxNukeBlast MNB;
 public final double collisionRadius;
 public final List<VehiclePart> parts = new ArrayList<>();
 public Color terrainRGB = U.getColor(0);
 public static String vehicleMaker = "";
 //CORE PROPERTIES--keep the order!
 public String name = "";
 public Type type = Vehicle.Type.vehicle;
 boolean floats;
 public final double[] accelerationStages = new double[2];
 public final double[] topSpeeds = {0, 0, Double.POSITIVE_INFINITY};
 public double turnRate;
 double maxTurn;
 double randomTurnKick;
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
 public double damageDealt;
 double structureBaseDamageDealt;
 public double durability, fragility, selfRepair;
 private double spin;
 boolean turnDrag;
 boolean steerInPlace;
 public double speedBoost;
 Physics.Landing landType = Physics.Landing.tires;
 Physics.Contact contact = Physics.Contact.none;
 double exhausting = Double.NaN;
 double othersAvoidAt;
 Engine engine = Engine.none;
 public Amphibious amphibious;
 public double driverViewY, driverViewZ /*driverViewX*/, extraViewHeight;
 ve.vehicles.AI.Behavior behavior = ve.vehicles.AI.Behavior.adapt;//<-The whole AI class would have to be needlessly instantiated every time if this were in AI
 public final List<Special> specials = new ArrayList<>();
 public ExplosionType explosionType = ExplosionType.none;
 long explosionsWhenDestroyed;
 //
 public boolean bumpIgnore;
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
 public final boolean realVehicle;
 public boolean phantomEngaged;
 public boolean drive, reverse, turnL, turnR, handbrake;
 boolean passBonus;
 boolean drive2, reverse2;
 public boolean boost;
 boolean steerByMouse;
 boolean onFire;
 public boolean inDriverView;
 private boolean gotStunt;
 public boolean offTheEdge;
 public boolean reviveImmortality;
 public boolean thrusting;
 public final boolean[] rollCheck = new boolean[2], flipCheck = new boolean[2], spinCheck = new boolean[2];
 Death death = Death.none;
 public final List<Wheel> wheels = new ArrayList<>();
 private final List<Dust> dusts = new ArrayList<>();
 private int currentDust;
 public final List<Splash> splashes = new ArrayList<>();
 public int currentSplash;
 public final List<RepairSphere> repairSpheres = new ArrayList<>();
 private static final long maxSpecials = 3;
 public boolean hasShooting;
 public final List<Explosion> explosions = new ArrayList<>();
 public int currentExplosion;

 public enum Type {vehicle, aircraft, turret, supportInfrastructure}

 enum Engine {//<-The 'Engine' holds more info than just audio, so keep in this class
  none, normal, tiny, agera, aventador, veyron, chiron, hotrod, huayra, laferrari, minicooper, p1, s7, turboracer,
  retro, electric,
  smalltruck, bigtruck, authentictruck, monstertruck, humvee, tank, smallcraft, turbo, power, massive, train,
  propsmall, prop, propbig,//'Prop' is not an engine YET
  jet, brightjet, powerjet, torchjet, jetfighter1, jetfighter2, turbine, smallrocket, rocket, bigrocket, turborocket
 }

 public enum ExplosionType {none, normal, nuclear, maxnuclear}

 enum Death {none, diedAlone, killedByAnother}

 public enum Amphibious {ON, OFF}

 public VehicleTurret VT;

 public Vehicle(int model, int inIndex, boolean isReal) {
  this(model, inIndex, isReal, true);
 }

 public Vehicle(int model, int inIndex, boolean isReal, boolean show) {
  index = inIndex;
  realVehicle = isReal;
  modelName = I.vehicleModels.get(model);
  long n;
  theRandomColor = index == I.userPlayerIndex ? I.userRandomRGB : U.getColor(U.random(), U.random(), U.random());
  for (n = 4; --n >= 0; ) {
   wheels.add(new Wheel(this));//Compatibility for vehicles with no wheels
  }
  int wheelCount = 0;
  long lightsAdded = 0;
  boolean onModelPart = false, addWheel = false;
  List<Double> xx = new ArrayList<>(), yy = new ArrayList<>(), zz = new ArrayList<>();
  double[] translate = new double[3];
  Color RGB = U.getColor(0);
  double pivotX = 0, pivotY = 0, pivotZ = 0;
  StringBuilder properties = new StringBuilder(), wheelProperties = new StringBuilder(), rimProperties = new StringBuilder();
  String texture = "", s = "";
  try (BufferedReader BR = new BufferedReader(new InputStreamReader(getFile(modelName, true), U.standardChars))) {//todo--set up system that allows vehicles to be read from separate files in a FOLDER
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
    } else if (s.startsWith("><") && onModelPart) {//<-Is onModelPart Redundant?
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
      if (wheels.isEmpty()) {
       for (n = 4; --n >= 0; ) {//Add wheels now because wheels derived from model have no set order
        wheels.add(new Wheel(this));
       }
      }
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
      if (!String.valueOf(properties).contains(D.thick(D.thrust))) {
       int size = xx.size() - 1;
       addSizes(xx.get(size), yy.get(size), zz.get(size));
      }
     }
     if (xx.size() < 1) {
      texture = s.startsWith(D.texture + "(") ? U.getString(s, 0) : texture;
      properties.append(s.startsWith(D.fastCull) ? " fastCull" + (s.endsWith("B") ? "B" : s.endsWith("F") ? "F" : s.endsWith("R") ? "R" : s.endsWith("L") ? "L" : "") + " " : "");
      if (s.startsWith(D.lit)) {
       properties.append(D.thick(D.light)).append(s.endsWith(D.fire) ? D.thick(D.fire) : "");
       lightsAdded++;
      }
      append(properties, s, false, D.noTexture, "noCrush",
      D.reflect, D.blink, D.line, D.selfIlluminate, D.noSpecular, D.shiny,
      "smokePoint", "thrustTrailPoint", D.flick1, D.flick2, D.landingGear, "shake", D.spinner, "driver",
      InstancePart.FaceFunction.conic.name(),
      InstancePart.FaceFunction.cylindric.name(),
      InstancePart.FaceFunction.strip.name(),
      InstancePart.FaceFunction.squares.name(),
      InstancePart.FaceFunction.triangles.name(),
      D.base, "exterior");
      properties.append(s.startsWith(D.thrustWhite) ? D.thick(D.thrustWhite) : s.startsWith(D.thrustBlue) ? D.thick(D.thrustBlue) : s.startsWith(D.thrust) ? D.thick(D.thrust) : "");
      properties.append(s.startsWith(D.turretBarrel) ? D.thick(D.turretBarrel) : s.startsWith(D.turret) ? D.thick(D.turret) : "");
      if (s.startsWith(D.controller)) {
       properties.append(D.thick(D.controller)).append(s.contains(D.XZ) ? D.thick(D.steerXZ) : s.contains(D.XY) ? D.thick(D.steerXY) : "");
      } else if (s.startsWith(D.wheel)) {
       properties.append(D.thick(D.wheel));
       addWheel = s.startsWith(D.wheelPoint) || addWheel;
      } else if (s.startsWith("steer")) {
       properties.append(s.startsWith(D.steerXY) ? D.thick(D.steerXY) : s.startsWith(D.steerYZ) ? D.thick(D.steerYZ) : U.startsWith(s, D.steerXZ, D.steers) ? D.thick(D.steerXZ) : "");
       properties.append(s.startsWith(D.steerFromYZ) ? D.thick(D.steerFromYZ) : s.startsWith(D.steerFromXZ) ? D.thick(D.steerFromXZ) : "");
      } else if (s.startsWith("pivot(")) {
       pivotX = U.getValue(s, 0) * modelSize * modelScale[0];
       pivotY = U.getValue(s, 1) * modelSize * modelScale[1];
       pivotZ = U.getValue(s, 2) * modelSize * modelScale[2];
      }
     }
    }
    name = s.startsWith("name(") ? U.getString(s, 0) : name;
    vehicleMaker = s.startsWith("maker(") ? U.getString(s, 0) : vehicleMaker;
    if (s.startsWith("type(")) {
     type = Type.valueOf(U.getString(s, 0));
     floats = s.contains("floats") || floats;
     if (type == Type.turret && realVehicle) {//<-Do NOT load for non-real vehicles!
      long turretAudioChoice = 0;
      try {
       turretAudioChoice = Math.round(U.getValue(s, 1));
      } catch (RuntimeException ignored) {
      }
      VA.turret = new Controlled(D.turret + (turretAudioChoice > 0 ? turretAudioChoice : ""));
     }
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
    randomTurnKick = s.startsWith("randomTurn(") ? U.getValue(s, 0) : randomTurnKick;
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
    damageDealt = s.startsWith("damageDealt(") ? U.getValue(s, 0) : damageDealt;
    structureBaseDamageDealt = s.startsWith("structureBaseDamageDealt(") ? U.getValue(s, 0) : structureBaseDamageDealt;
    turretBaseY = s.startsWith("turretBaseY(") ? U.getValue(s, 0) : turretBaseY;
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
      if (Sounds.softwareBased) {
       VA.engineClipQuantity = Math.min(VA.engineClipQuantity, Sounds.maxSoftwareBasedLayeredEngines);
      }
      VA.engineTuneRatio = U.getValue(s, 2);
      VA.enginePitchBase = U.getValue(s, 3);
     } catch (RuntimeException ignored) {
     }
     VA.engineTuning = s.contains("harmonicSeries") ? VehicleAudio.EngineTuning.harmonicSeries : VA.engineTuning;
    }
    amphibious = s.startsWith("amphibious(yes") ? Amphibious.ON : amphibious;
    driverViewY = s.startsWith("driverViewY(") ? U.getValue(s, 0) * modelSize + translate[1] : driverViewY;
    driverViewZ = s.startsWith("driverViewZ(") ? U.getValue(s, 0) * modelSize + translate[2] : driverViewZ;
    driverViewX = s.startsWith("driverViewX(") ? Math.abs(U.getValue(s, 0) * modelSize) + translate[0] : driverViewX;
    extraViewHeight = s.startsWith("extraViewHeight(") ? U.getValue(s, 0) : extraViewHeight;
    if (s.startsWith("behavior(") && !U.getString(s, 0).isEmpty()) {
     behavior = ve.vehicles.AI.Behavior.valueOf(U.getString(s, 0));
    }
    if (s.startsWith("special(") && !U.getString(s, 0).isEmpty() && specials.size() < maxSpecials) {
     specials.add(new Special(this, s));
    }
    addSpecialProperties(s);
    if (s.startsWith("explosion(") && !U.getString(s, 0).isEmpty()) {
     explosionType = ExplosionType.valueOf(U.getString(s, 0));
     I.nukeInMatch = explosionType == ExplosionType.nuclear || I.nukeInMatch;
    }
    explosionsWhenDestroyed = s.startsWith("explodeWhenDestroyed(") ? Math.round(U.getValue(s, 0)) : explosionsWhenDestroyed;
    if (s.startsWith("vehicleTurret(")) {
     VT = new VehicleTurret(this, s);
    }
    getSizeScaleTranslate(s, translate, 1, new double[]{1, 1, 1});
    if (s.startsWith("wheelColor(")) {
     if (s.contains(D.reflect)) {
      wheelProperties.append(D.thick(D.reflect));
     } else {
      try {
       wheelRGB = U.getColor(U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2));
      } catch (RuntimeException E) {
       if (s.contains(D.theRandomColor)) {
        wheelRGB = theRandomColor;
        wheelProperties.append(D.thick(D.theRandomColor));
       } else {
        wheelRGB = U.getColor(U.getValue(s, 0));
       }
      }
     }
     append(wheelProperties, s, true, D.noSpecular, D.shiny);
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
      if (s.contains(D.theRandomColor)) {
       rimRGB = theRandomColor;
       rimProperties.append(D.thick(D.theRandomColor));
      } else {
       rimRGB = U.getColor(U.getValue(s, 2));
      }
     }
     append(rimProperties, s, true, D.reflect, D.noSpecular, D.shiny, D.sport);
    }
    wheelProperties.append(s.startsWith("landingGearWheels") ? D.thick(D.landingGear) : "");
    wheelTextureType = s.startsWith("wheelTexture(") ? U.getString(s, 0) : wheelTextureType;//<-Using 'append' would mess this up if found more than once in file
    wheelSmoothing = s.startsWith("smoothing(") ? U.getValue(s, 0) * modelSize : wheelSmoothing;
    if (s.startsWith("wheel(")) {
     if (!show) {
      break;
     }
     if (wheelCount < 4) {
      wheels.add(new Wheel(this));
      wheels.get(wheelCount).pointX = U.getValue(s, 0) * modelSize * modelScale[0];
      wheels.get(wheelCount).pointZ = U.getValue(s, 2) * modelSize * modelScale[2];
      wheels.get(wheelCount).skidmarkSize = Math.abs(U.getValue(s, 3)) * modelSize * modelScale[0];
      wheels.get(wheelCount).sparkPoint = U.getValue(s, 4) * 2 * modelSize;
     }
     String side = U.getValue(s, 0) > 0 ? " R " : U.getValue(s, 0) < 0 ? " L " : U.random() < .5 ? " R " : " L ";
     loadWheel(this, null, U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2), U.getValue(s, 3), U.getValue(s, 4), wheelProperties + side, String.valueOf(rimProperties), wheelTextureType, s.contains(D.steers), s.contains(D.hide));
     wheelCount++;
    }
    steerAngleMultiply = s.startsWith("steerAngleMultiply(") ? U.getValue(s, 0) : steerAngleMultiply;
   }
  } catch (IOException e) {
   System.out.println(U.modelLoadingError + e);
   System.out.println(UI.At_File_ + model);
   System.out.println(UI.At_Line_ + s);
  }
  if (lightsAdded < 1) {
   parts.add(new VehiclePart(this, new double[1], new double[1], new double[1], 1, U.getColor(1), D.thick(D.light), ""));
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
  for (var special : specials) {
   if (special.type == Special.Type.spinner) {
    spinner = new Spinner(this);
    break;
   }
  }
  I.trainEngineInMatch = engine == Engine.train || I.trainEngineInMatch;
  collisionRadius = spinner == null ? absoluteRadius * .2 : Math.min(renderRadius * .75, absoluteRadius * .2);//<-Optimized for CALAMITUS MAXIMUS
  explosionType = explosionsWhenDestroyed > 0 && !explosionType.name().contains(ExplosionType.nuclear.name()) ? ExplosionType.normal : explosionType;
  X = Y = Z = XZ = 0;
  for (var part : parts) {
   Nodes.add(part.MV);
  }
  loadRealVehicleContent();
  Quaternion
  baseXZ = new Quaternion(0, U.sin(XZ * .5), 0, U.cos(XZ * .5)),
  baseYZ = new Quaternion(-U.sin(YZ * .5), 0, 0, U.cos(YZ * .5)),
  baseXY = new Quaternion(0, 0, -U.sin(XY * .5), U.cos(XY * .5));
  rotation = baseXY.multiply(baseYZ).multiply(baseXZ);
 }

 private void addSpecialProperties(String s) {
  if (!specials.isEmpty() && specials.size() <= maxSpecials) {
   Special special = specials.get(specials.size() - 1);
   if (s.startsWith("gunY(")) {
    for (int n1 = 0; n1 < Shot.defaultQuantity; n1++) {
     try {
      special.ports.add(new Port());
      special.ports.get(n1).Y = U.getValue(s, special.type == Special.Type.shotgun ? 0 : n1) * modelSize;
     } catch (RuntimeException e) {
      special.ports.remove(special.ports.size() - 1);
      break;
     }
    }
   } else if (s.startsWith("gunX(")) {
    try {
     for (var port : special.ports) {
      port.X = U.getValue(s, special.ports.indexOf(port)) * modelSize;
     }
    } catch (RuntimeException E) {
     for (var port : special.ports) {
      port.X = U.getValue(s, 0) * modelSize;
     }
    }
   } else if (s.startsWith("gunZ(")) {
    try {
     for (var port : special.ports) {
      port.Z = U.getValue(s, special.ports.indexOf(port)) * modelSize;
     }
    } catch (RuntimeException E) {
     for (var port : special.ports) {
      port.Z = U.getValue(s, 0) * modelSize;
     }
    }
   } else if (s.startsWith("gunXZ(")) {
    try {
     for (var port : special.ports) {
      port.XZ = U.getValue(s, special.ports.indexOf(port));
     }
    } catch (RuntimeException E) {
     for (var port : special.ports) {
      port.XZ = U.getValue(s, 0);
     }
    }
   } else if (s.startsWith("gunYZ(")) {
    try {
     for (var port : special.ports) {
      port.YZ = U.getValue(s, special.ports.indexOf(port));
     }
    } catch (RuntimeException E) {
     for (var port : special.ports) {
      port.YZ = U.getValue(s, 0);
     }
    }
   } else if (s.startsWith("gunRandomPosition(")) {
    special.randomPosition = U.getValue(s, 0) * modelSize;
   } else if (s.startsWith("gunRandomAngle(")) {
    special.randomAngle = U.getValue(s, 0);
   } else if (s.startsWith("reloadTimeMultiply(")) {
    special.timerMultiple = U.getValue(s, 0);
   }
  }
 }

 public void addTransparentNodes() {
  if (realVehicle) {
   if (contact == Physics.Contact.rubber || Terrain.terrain.contains(D.thick(D.snow))) {
    Color C = U.getColor(wheelRGB.getRed() * .5, wheelRGB.getGreen() * .5, wheelRGB.getBlue() * .5);
    if (!wheelTextureType.isEmpty()) {
     C = U.getColor(C.getRed() * .333, C.getGreen() * .333, C.getBlue() * .333);
    }
    for (var wheel : wheels) {
     wheel.skidmarks = new ArrayList<>();
     for (int n = 48; --n >= 0; ) {
      wheel.skidmarks.add(new Skidmark(wheel, C));
     }
    }
   }
   for (int n = 32; --n >= 0; ) {
    repairSpheres.add(new RepairSphere(this));
   }
   if (!isFixed()) {
    for (long n = Dust.defaultQuantity; --n >= 0; ) {
     dusts.add(new Dust());
    }
   }
   for (var special : specials) {
    for (var shot : special.shots) {
     Nodes.add(shot.MV);
    }
    for (var port : special.ports) {
     if (port.smokes != null) {
      for (var smoke : port.smokes) {
       Nodes.add(smoke.C);
      }
     }
    }
   }
   for (var part : parts) {
    if (part.smokes != null) {
     for (var smoke : part.smokes) {
      Nodes.add(smoke.C);//<-Smokes are the most transparent, thus should be the last Nodes added
     }
    }
   }
  }
 }

 private void loadRealVehicleContent() {
  if (realVehicle) {
   long n;
   P = new Physics(this);
   if (Pool.exists || Tsunami.exists) {
    for (n = Splash.defaultQuantity; --n >= 0; ) {
     splashes.add(new Splash(this));
    }
   }
   for (var wheel : wheels) {
    for (n = 50; --n >= 0; ) {
     wheel.sparks.add(new Spark());
    }
   }
   for (var part : parts) {
    if (part.thrustTrails != null) {
     for (var trail : part.thrustTrails) {
      Nodes.add(trail.B);
     }
    }
   }
   if (Maps.defaultVehicleLightBrightness > 0) {
    burnLight = new PointLight();
   }
   if (explosionType == ExplosionType.maxnuclear) {
    MNB = new MaxNukeBlast(this);
   }
   TE.setVehicleMatchStartPlacement(this);
   P.cameraXZ = XZ;
   for (var wheel : wheels) {
    wheel.X = X;
    wheel.Y = Y;
    wheel.Z = Z;
   }
   if (!wheels.isEmpty()) {
    P.wheelGapFrontToBack = Math.max(Math.abs(wheels.get(0).pointZ - wheels.get(2).pointZ), Math.abs(wheels.get(1).pointZ - wheels.get(3).pointZ));
    P.wheelGapLeftToRight = Math.max(Math.abs(wheels.get(0).pointX - wheels.get(1).pointX), Math.abs(wheels.get(2).pointX - wheels.get(3).pointX));
   }
   for (var special : specials) {
    special.time();
    special.load();
   }
   if (!explosionType.name().contains(ExplosionType.nuclear.name())) {
    P.explosionDiameter = 500;
    P.explosionDamage = 250;
   }
   P.explosionPush = 500;
   if (explosionType != ExplosionType.none) {
    for (n = Explosion.defaultQuantity; --n >= 0; ) {
     explosions.add(new Explosion(this));
    }
   }
   AI = new AI(this);
   VA.load();
   double volcanoDistance = U.distanceXZ(this, Volcano.C);
   P.onVolcano = Volcano.exists && volcanoDistance < Volcano.radiusBottom && volcanoDistance > Volcano.radiusTop && Y > -Volcano.radiusBottom + volcanoDistance;
   Y = P.onVolcano ? Math.min(Y, -Volcano.radiusBottom + volcanoDistance) - (isFixed() ? turretBaseY : 0) : Y;
   P.atPoolXZ = Pool.exists && U.distanceXZ(this, Pool.core) < Pool.surface.getRadius();
   P.inPool = P.atPoolXZ && Y + clearanceY > 0;
   P.resetLocalGround();
   lightBrightness = Maps.defaultVehicleLightBrightness;
  }
 }

 public boolean isFixed() {
  return type == Type.turret || type == Type.supportInfrastructure;
 }

 public void addDamage(double in) {
  damage = U.clamp(0, damage + in, damageCeiling());
 }

 public void setDamage(double in) {
  damage = U.clamp(0, in, damageCeiling());
 }

 public double getDamage(boolean percent) {//<-'Percent' here is out of 1, not 100
  return percent ? damage / durability : damage;
 }

 public void deformParts() {
  double deformAngle = getDamage(true) * 6.;
  for (var part : parts) {
   part.deform(deformAngle);
  }
 }

 public void throwChips(double power, boolean randomizePower) {//<-Parameter passed in should never have random() or randomPlusMinus() applied
  for (var part : parts) {
   if (part.chip != null) {
    part.chip.deploy(randomizePower ? U.randomPlusMinus(power) : power);
   }
  }
 }

 public double damageCeiling() {
  return durability * 1.004;//<-Always rounds to '100%' visually in UI
 }

 public boolean isIntegral() {
  return damage <= durability;
 }

 boolean highGrip() {
  return grip > 100;
 }

 public boolean dealsMassiveDamage() {//'Massive damage' is defined as a non-aircraft vehicle, with or without any specials, that has a damageDealt >= 100
  return type != Type.aircraft && damageDealt >= 100;
 }

 public void runRender(boolean gamePlay) {
  while (YZ < -180) YZ += 360;
  while (YZ > 180) YZ -= 360;
  while (XY < -180) XY += 360;
  while (XY > 180) XY -= 360;
  while (XZ < -180) XZ += 360;
  while (XZ > 180) XZ -= 360;
  if (UI.status != UI.Status.vehicleSelect && !destroyed) {
   flicker = !flicker;
  }
  double distanceToCamera = U.distance(this);
  boolean nullPhysics = P == null;
  double sinXZ = U.sin(XZ), cosXZ = U.cos(XZ), sinYZ = U.sin(YZ), cosYZ = U.cos(YZ), sinXY = U.sin(XY), cosXY = U.cos(XY);
  for (var part : parts) {
   part.runSetPosition(nullPhysics, sinXZ, cosXZ, sinYZ, cosYZ, sinXY, cosXY);
  }
  boolean renderALL = E.renderType == E.RenderType.ALL;
  if (renderALL || E.renderType == E.RenderType.fullDistance || distanceToCamera < E.viewableMapDistance + collisionRadius) {
   onFire = Maps.name.equals(D.Maps.theSun) || onFire;
   rotation.set();
   rotation.multiply(0, 0, -U.sin(XY * .5), U.cos(XY * .5));//<-Mind the multiply order!
   rotation.multiply(-U.sin(YZ * .5), 0, 0, U.cos(YZ * .5));
   rotation.multiply(0, U.sin(XZ * .5), 0, U.cos(XZ * .5));
   double deformation = getDamage(true) * 6;
   for (var part : parts) {
    part.runRender(nullPhysics, distanceToCamera, renderALL);
    part.damage.setAngle(U.clamp(-deformation, part.damage.getAngle(), deformation));//<-Ensures the visual deformation is never above the damage--fixMe--this doesn't seem to be working in replays?
   }
  }
  double smokeEmitProbability = nullPhysics || destroyed ? 0 : (P.mode.name().contains(D.drive) || P.mode == Physics.Mode.neutral) && (drive || reverse) ? 1 : .25;
  for (var part : parts) {
   if (part.smokes != null) {
    if (U.random() < smokeEmitProbability) {
     part.smokes.get(part.currentSmoke).deploy(part);
     part.currentSmoke = ++part.currentSmoke >= Smoke.defaultQuantity ? 0 : part.currentSmoke;
    }
    for (var smoke : part.smokes) {
     smoke.run();
    }
   }
   if (part.thrustTrails != null) {
    for (var trail : part.thrustTrails) {
     trail.run();
    }
   }
   part.MV.setVisible(part.visible);
   part.visible = false;
   if (part.chip != null) {
    part.chip.run(gamePlay);
   }
   if (part.flame != null) {
    part.flame.run();
   }
  }
  for (var wheel : wheels) {
   for (var spark : wheel.sparks) {
    spark.run(gamePlay);
   }
   if (wheel.skidmarks != null) {
    for (var skidmark : wheel.skidmarks) {
     skidmark.run();
    }
   }
  }
  for (var dust : dusts) {
   dust.run();
  }
  for (var splash : splashes) {
   splash.run();
  }
  if (MNB != null) {
   MNB.runRender();
  }
  long repairSpheresRemoved = 0;
  for (var repairSphere : repairSpheres) {
   repairSphere.run(this);
   if (repairSphere.stage <= 0) {
    repairSpheresRemoved++;
   }
  }
  if (repairSpheresRemoved >= repairSpheres.size()) {
   reviveImmortality = false;
  }
  if (!isIntegral() && Maps.defaultVehicleLightBrightness > 0 && !parts.isEmpty() && distanceToCamera < E.viewableMapDistance) {
   Nodes.setLightRGB(burnLight, .5, .25 + U.random(.2), U.random(.125));
   U.setTranslate(burnLight, this);
   Nodes.addPointLight(burnLight);
  }
 }

 void deployDust(boolean groundHit) {
  if (!destroyed && !phantomEngaged && !P.terrainProperties.contains(D.thick(D.ice))) {
   boolean flipped = P.flipped();
   double dustSpeed = U.netValue(speedX, speedZ), speedDifference = flipped ? dustSpeed : Math.abs(Math.abs(P.speed) - dustSpeed);
   if (groundHit || speedDifference * .5 > 10 + U.random(5.) || (!flipped && Math.abs(P.speed) > 50 + U.random(50.))) {
    dusts.get(currentDust).deploy(this, dustSpeed, speedDifference);
    currentDust = ++currentDust >= Dust.defaultQuantity ? 0 : currentDust;
   }
  }
 }

 public void getPlayerInput() {
  if (index == I.userPlayerIndex && Network.mode == Network.Mode.OFF && UI.status != UI.Status.replay) {
   drive = Keys.up;
   reverse = Keys.down;
   turnL = Keys.left;
   turnR = Keys.right;
   handbrake = Keys.space;
   boost = Keys.boost;
   passBonus = Keys.passBonus;
   if (amphibious != null) {
    amphibious = Keys.amphibious ? Amphibious.ON : Amphibious.OFF;
   }
   boolean turretExists = VT != null, get2ndDrive = false;
   if (turretExists && !VT.hasAutoAim) {
    get2ndDrive = true;
    VT.turnL = Keys.A;
    VT.turnR = Keys.D;
   }
   if (type == Type.aircraft || get2ndDrive) {
    drive2 = Keys.W;
    reverse2 = Keys.S;
   }
   for (var special : specials) {
    if (special.aimType != Special.AimType.auto) {
     boolean shootWithCanceledSteer = special.aimType != Special.AimType.normal && special.type != Special.Type.mine && turretExists && VT.turnL && VT.turnR;
     special.fire = Keys.special[specials.indexOf(special)] || shootWithCanceledSteer;
    }
   }
  }
 }

 public void setTurretY() {
  if (isFixed()) {
   Y = 0;
   if (!U.equals(Maps.name, D.Maps.everybodyEverything)) {
    E.setTerrainSit(this, true);
   }
   Y -= turretBaseY;
  }
 }

 public void repair(boolean hear) {
  if (!reviveImmortality && (explosionType != ExplosionType.maxnuclear || isIntegral())) {
   setDamage(0);
   for (var sphere : repairSpheres) {
    sphere.deploy();
   }
   if (hear) {
    VA.repair.play(VA.distanceVehicleToCamera);
   }
  }
 }

 public void runMiscellaneous(boolean gamePlay) {//<-todo--turn this into a runLogic() and give it a reasonable call location?
  steerByMouse = index == I.userPlayerIndex && Match.cursorDriving;
  int n;
  inDriverView = index == I.vehiclePerspective && Camera.view == Camera.View.driver;
  if (!specials.isEmpty()) {
   phantomEngaged = false;//This block is probably best at the top, so that phantom is already engaged by the time collisions, destruction events, etc. are handled
   for (var special : specials) {
    if (special.fire && special.type == Special.Type.phantom) {
     phantomEngaged = true;
     break;
    }
   }
  }
  VA.setDistance();
  if (P.massiveHitTimer > 0) {
   P.massiveHitTimer -= U.tick;
  }
  if (screenFlash > 0) {
   screenFlash -= U.tick * (explosionType == ExplosionType.maxnuclear ? .01 : .1);
  }
  cameraShake -= cameraShake > 0 ? U.tick : 0;
  P.inWrath = false;
  if (isIntegral()) {
   destroyed = P.subtractExplodeStage = false;
   P.destructTimer = P.explodeStage = 0;
   onFire = Maps.name.equals(D.Maps.theSun) && onFire;
   addDamage(gamePlay ? -selfRepair * U.tick : 0);
   for (var part : parts) {
    part.explodeStage = VehiclePart.ExplodeStage.intact;
   }
  } else {
   onFire = true;
   if (P.destructTimer <= 0) {
    VA.death.play(VA.distanceVehicleToCamera);
    setCameraShake(Camera.shakeIntensity.vehicleDeath);
    if (explosionsWhenDestroyed > 0) {
     if (VA.deathExplode != null) {//<-Nukes don't have this
      VA.deathExplode.play(Double.NaN, VA.distanceVehicleToCamera * Sounds.gainMultiples.deathExplode);
     }
     nukeDetonate();
    }
    P.destructTimer = Double.MIN_VALUE;//<-Prevents recursive calls of this block
   }
   if (gamePlay) {
    destroyed = (P.destructTimer += U.tick) >= 8 || destroyed;
    if (VA.burn != null && destroyed) {
     VA.burn.loop(VA.distanceVehicleToCamera);
    }
    double reviveWait = explosionType == ExplosionType.maxnuclear ? Double.POSITIVE_INFINITY : explosionType == ExplosionType.nuclear ? 2 : 1;
    P.subtractExplodeStage = P.explodeStage > 100 * reviveWait || P.subtractExplodeStage;
    P.explodeStage += U.tick * (P.subtractExplodeStage ? -1 : 1);
    if (P.explodeStage < 0) {
     repair(true);
     reviveImmortality = true;
    }
   }
  }
  if (MNB != null) {
   MNB.runLogic(gamePlay);
  }
  if (passBonus && Bonus.holder == index) {
   for (var vehicle : I.vehicles) {
    if (!I.sameVehicle(this, vehicle) && I.sameTeam(this, vehicle) && U.distance(this, vehicle) < collisionRadius + vehicle.collisionRadius) {
     Bonus.setHolder(vehicle);//^Checking same team, so sameVehicle check IS needed
    }
   }
  }
  if (spinner != null) {
   spinner.run(gamePlay);
  }
  Tsunami.vehicleInteract(this);
  boolean isJet = U.contains(engine, Engine.jet, Engine.turbine, Engine.rocket),
  thrustDrive = drive2 || (drive && P.mode != Physics.Mode.fly);
  thrusting = !destroyed && ((speedBoost > 0 && boost) || exhausting > 0 || (P.mode != Physics.Mode.stunt && isJet &&
  (thrustDrive || (P.mode == Physics.Mode.fly && engine != Engine.turbine && VA.engineClipQuantity * (Math.abs(P.speed) / topSpeeds[1]) >= 1))));
  if (thrusting) {
   boolean forceOut = isJet || (speedBoost > 0 && boost);
   double sinXZ = U.sin(XZ), cosXZ = U.cos(XZ), sinYZ = U.sin(YZ);
   for (var part : parts) {
    if (part.thrustTrails != null) {
     for (n = 4; --n >= 0; ) {
      part.thrustTrails.get(part.currentThrustTrail).deploy(forceOut, sinXZ, cosXZ, sinYZ);
      part.currentThrustTrail = ++part.currentThrustTrail >= ThrustTrail.defaultQuantity ? 0 : part.currentThrustTrail;
     }
    }
   }
  }
  TE.runVehicleRepairPointInteraction(this, gamePlay);
  if (!Match.started && engine == Engine.hotrod) {
   XY = U.randomPlusMinus(2.);
  }
  for (var special : specials) {
   if (special.type == Special.Type.phantom) {
    if (special.fire) {
     for (var part : parts) {
      if (U.random() < .5) {
       part.MV.setMaterial(E.phantomPM);
      }
     }
     if (gamePlay) {
      special.sound.loop(VA.distanceVehicleToCamera);
     }
    } else {
     special.sound.stop();
     for (var part : parts) {
      part.MV.setMaterial(part.PM);
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
  VA.run(gamePlay);
 }

 private void nukeDetonate() {
  if (explosionType.name().contains(ExplosionType.nuclear.name())) {
   screenFlash = 1;
   if (explosionType == ExplosionType.maxnuclear) {
    MNB.setSingularity();
    setCameraShake(Camera.shakeIntensity.maxNuclear);
    MaxNukeBlast.whoIsLit = index;
    VA.nukeMax.play(0, VA.distanceVehicleToCamera * Sounds.gainMultiples.nukeMax);
   } else {
    setCameraShake(Camera.shakeIntensity.normalNuclear);
    VA.nuke.play(Double.NaN, VA.distanceVehicleToCamera * Sounds.gainMultiples.nuke);
   }
  }
 }

 void runStuntScoring(boolean replay) {
  if (P.mode.name().startsWith(D.drive)) {
   if ((P.stuntTimer += U.tick) > stuntLandWaitTime() && !gotStunt) {
    if (!P.flipped() || landStuntsBothSides) {
     double rollReward = Math.abs(P.stuntXY) > 135 ? Math.abs(P.stuntXY) * .75 : rollCheck[0] || rollCheck[1] ? 270 : 0;
     rollReward *= rollCheck[0] && rollCheck[1] ? 2 : 1;
     double flipReward = Math.abs(P.stuntYZ) > 135 ? Math.abs(P.stuntYZ) : flipCheck[0] || flipCheck[1] ? 360 : 0;
     flipReward *= flipCheck[0] && flipCheck[1] ? 2 : 1;
     double spinReward = Math.abs(P.stuntXZ) >= 180 ? Math.abs(P.stuntXZ) * .75 : spinCheck[0] || spinCheck[1] ? 270 : 0;
     spinReward *= spinCheck[0] && spinCheck[1] ? 2 : 1;
     P.stuntReward = (rollReward + flipReward + spinReward) * (offTheEdge ? 2 : 1);
     Match.scoreStunt[index < I.halfThePlayers() ? 0 : 1] += replay ? 0 : P.stuntReward;
     Match.processStunt(this);
    }
    P.stuntXY = P.stuntYZ = P.stuntXZ = 0;
    flipCheck[0] = flipCheck[1] = rollCheck[0] = rollCheck[1] = spinCheck[0] = spinCheck[1] = offTheEdge = false;
    AI.airRotationDirectionYZ = U.random() < .5 ? 1 : -1;
    AI.airRotationDirectionXY = U.random() < .5 ? 1 : -1;
    gotStunt = true;
   }
   if (!P.flipped() || landStuntsBothSides) {
    P.flipTimer = 0;
   } else if ((P.flipTimer += U.tick) > 39) {
    XZ += Math.abs(YZ) > 90 ? 180 : 0;
    P.speed = XY = YZ = P.flipTimer = 0;
   }
  } else {
   P.stuntTimer = 0;
   gotStunt = false;
   if (P.mode == Physics.Mode.stunt) {
    P.stuntXY += 20 * P.stuntSpeedXY * U.tick;
    rollCheck[0] = P.stuntXY > 135 || rollCheck[0];
    rollCheck[1] = P.stuntXY < -135 || rollCheck[1];
    P.stuntYZ -= 20 * P.stuntSpeedYZ * U.tick;
    flipCheck[0] = P.stuntYZ > 135 || flipCheck[0];
    flipCheck[1] = P.stuntYZ < -135 || flipCheck[1];
    P.stuntXZ += 20 * P.stuntSpeedXZ * U.tick;
    spinCheck[0] = P.stuntXZ > 135 || spinCheck[0];
    spinCheck[1] = P.stuntXZ < -135 || spinCheck[1];
   }
   offTheEdge = P.againstWall() || offTheEdge;
  }
 }

 public double stuntLandWaitTime() {
  return type == Type.aircraft ? 1 : 8;
 }

 public void setCameraShake(double in) {
  cameraShake = Math.max(cameraShake, in);
 }
}
