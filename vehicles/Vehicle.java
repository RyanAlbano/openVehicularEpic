package ve.vehicles;

import java.io.*;
import java.util.*;

import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import ve.*;
import ve.effects.*;
import ve.environment.*;
import ve.trackparts.*;
import ve.utilities.*;

public class Vehicle extends Instance {

 public AI AI;
 public final List<VehiclePiece> pieces = new ArrayList<>();
 private Sphere nukeBlastSphere;
 private double nukeBlastSphereSize;
 final PhongMaterial fixSpherePM = new PhongMaterial();
 public PointLight burnLight;
 public int index;
 public int checkpointsPassed;
 public int point;
 private int engineClipQuantity;
 public double height;
 public double steerAngleMultiply = 1;
 public final double[] accelerationStages = new double[2];
 public final double[] topSpeeds = {0, 0, Double.POSITIVE_INFINITY};
 public double turnRate;
 public double maxTurn;
 private double randomTurn;
 private double brake;
 public double grip;
 private double drag;
 private double bounce;
 public double airAcceleration;
 private double airTopSpeed;
 public double airPush;
 private double sidewaysLandingAngle;
 private double pushesOthers;
 private double getsPushed;
 private double liftsOthers;
 private double getsLifted;
 public final double[] damageDealt = new double[4];
 private double baseDamageDealt;
 public double durability;
 public double fragility;
 public double selfRepair;
 private double spin;
 public double speedBoost;
 public double othersAvoidAt;
 public double driverViewY;
 public double driverViewZ;
 public double extraViewHeight;
 public double lightBrightness;
 private double explosionDiameter;
 private double explosionDamage;
 private double explosionPush;
 public double speed;
 public double speedXZ;
 public double speedYZ;
 public double stallSpeed;
 public double netSpeed;
 public double cameraXZ;
 private double lastXZ;
 private double airSpinXZ;
 public double stuntXY;
 public double stuntYZ;
 public double stuntXZ;
 public double stuntTimer;
 private final double[] stuntSpeed = new double[2];
 public double flipTimer;
 public double stuntReward;
 public double damage;
 private double destructTimer;
 private double massiveHitTimer;
 public double spinnerXZ;
 public double spinnerSpeed = Double.NaN;
 private double chuffTimer, forceTimer, crashTimer, landTimer;
 private double splashing = Double.NaN;
 private double exhaust = Double.NaN;
 public double screenFlash;
 private double nukeBlastX, nukeBlastY, nukeBlastZ;
 private double wheelGapFrontToBack;
 private double wheelGapLeftToRight;
 public final double[] wheelSpin = new double[2];
 public double localVehicleGround;
 private double vehicleToCameraSoundDistance;
 private double engineTuneRatio = 2;
 public final boolean realVehicle;
 private boolean showVehicle = true;
 public boolean landStuntsBothSides;
 private boolean turnDrag;
 private boolean steerInPlace;
 public boolean amphibious;
 public boolean phantomEngaged;
 public boolean drive;
 public boolean reverse;
 public boolean turnL, turnR;
 public boolean handbrake;
 public final boolean[] useSpecial = new boolean[2];
 public boolean boost;
 private boolean steerByMouse;
 public boolean onFire;
 public boolean inDriverView;
 public boolean flipped;
 public boolean stuntEnd;
 public boolean offTheEdge;
 public boolean destroyed;
 public double turretDefaultY;
 private boolean onAntiGravity;
 private boolean inTornado;
 private boolean onVolcano;
 private boolean atPoolXZ;
 public boolean inPool;
 private boolean reviveImmortality;
 public boolean thrusting;
 private boolean skidding;
 private boolean scraping;
 private boolean wheelDiscord;
 public final boolean[] rollCheck = new boolean[2];
 public final boolean[] flipCheck = new boolean[2];
 private boolean[] gotNukeBlasted;
 public boolean wrathEngaged;
 private boolean[] wrathStuck;
 public boolean inWrath;
 private long spinMultiplyPositive = 1;
 private long spinMultiplyNegative = 1;
 public long vehicleHit = -1;
 private long explosionsWhenDestroyed;
 public long destructionType;
 public long polarity;
 public VE.AIBehavior AIBehavior;
 private long engineStage;
 private long lastEngineStage;
 private long randomCrashSound;
 private long randomSkidSound;
 private long randomScrapeSound;
 private long randomExhaustSound;
 public String vehicleName = "";
 private String landType = "";
 private String contact = "";
 public String explosionType = "";
 public String terrainProperties = "";
 public VE.mode mode;
 public VE.type vehicleType = VE.type.vehicle;
 final Map<String, Sound> sounds = new HashMap<>();
 public final List<Wheel> wheels = new ArrayList<>();
 private final List<Dust> dusts = new ArrayList<>();
 private int currentDust;
 private final List<Jet> jets = new ArrayList<>();
 private int currentJet;
 private final List<Splash> splashes = new ArrayList<>();
 private int currentSplash;
 private final List<FixSphere> fixSpheres = new ArrayList<>();
 public final List<Special> specials = new ArrayList<>();
 public final List<Explosion> explosions = new ArrayList<>();
 int currentExplosion;
 private final List<NukeBlast> nukeBlasts = new ArrayList<>();

 enum Engine {
  none, normal, tiny, agera, aventador, veyron, chiron, hotrod, huayra, laferrari, minicooper, p1, s7, turboracer,
  retro, electric,
  smalltruck, bigtruck, authentictruck, monstertruck, humvee, tank, smallcraft, turbo, power, massive, train,
  smallprop, bigprop,
  jet, brightjet, powerjet, torchjet, jetfighter, turbine, rocket
 }

 public Engine engine = Engine.none;

 public Vehicle(int model, List<String> L, int I, boolean vehicle) {
  realVehicle = vehicle;
  Set(model, L, I);
 }

 public Vehicle(int model, List<String> L, int I, boolean vehicle, boolean show) {
  realVehicle = vehicle;
  showVehicle = show;
  Set(model, L, I);
 }

 private void Set(int model, List<String> L, int I) {
  modelNumber = model;
  index = I;
  int n, n1;
  theRandomColor[0] = index == VE.userPlayer ? VE.userRandomRGB[0] : U.random();
  theRandomColor[1] = index == VE.userPlayer ? VE.userRandomRGB[1] : U.random();
  theRandomColor[2] = index == VE.userPlayer ? VE.userRandomRGB[2] : U.random();
  AIBehavior = VE.AIBehavior.adapt;
  int wheelCount = 0;
  for (n = 4; --n >= 0; ) {
   wheels.add(new Wheel());
  }
  long lightsAdded = 0;
  boolean onModelPiece = false, addWheel = false;
  List<Double> xx = new ArrayList<>(), yy = new ArrayList<>(), zz = new ArrayList<>();
  double[] translate = new double[3];
  double[] RGB = {0, 0, 0};
  String type = "", wheelType = "", textureType = "", wheelTextureType = "", rimType = "", s = "";
  try (BufferedReader br = new BufferedReader(new InputStreamReader(getFile(L)))) {
   for (String s1; (s1 = br.readLine()) != null; ) {
    s = "" + s1.trim();
    if (s.startsWith("<>")) {
     if (!showVehicle) {
      break;
     }
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
      double wheelAverageZ = (minimumZ + maximumZ) * .5;
      int wheelNumber = averageX < 0 && wheelAverageZ > 0 ? 0 : averageX > 0 && wheelAverageZ > 0 ? 1 : averageX < 0 && wheelAverageZ < 0 ? 2 : averageX > 0 && wheelAverageZ < 0 ? 3 : Integer.MAX_VALUE;
      wheels.get(wheelNumber).pointX = averageX;
      wheels.get(wheelNumber).pointZ = wheelAverageZ;
      wheels.get(wheelNumber).sparkPoint = Math.abs(minimumZ - maximumZ);
      wheels.get(wheelNumber).skidmarkSize = Math.abs(minimumX - maximumX) * .5;
      wheelCount++;
     }
     if (xx.size() > 0) {
      pieces.add(new VehiclePiece(this, U.listToArray(xx), U.listToArray(yy), U.listToArray(zz), xx.size(), RGB, type, textureType));
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
       lightsAdded++;
      }
      type += s.startsWith("reflect") ? " reflect " : "";
      type += s.startsWith("thrustWhite") ? " thrustWhite " : s.startsWith("thrustBlue") ? " thrustBlue " : s.startsWith("thrust") ? " thrust " : "";
      type += s.startsWith("selfIlluminate") ? " selfIlluminate " : "";
      type += s.startsWith("blink") ? " blink " : "";
      type += s.startsWith("noSpecular") ? " noSpecular " : s.startsWith("shiny") ? " shiny " : "";
      type += s.startsWith("noTexture") ? " noTexture " : "";
      type += s.startsWith("flick1") ? " flick1 " : "";
      type += s.startsWith("flick2") ? " flick2 " : "";
      type += s.startsWith("landingGear") ? " landingGear " : "";
      type += s.startsWith("spinner") ? " spinner " : "";
      type += s.startsWith("noCrush") ? " noCrush " : "";
      type += s.startsWith("driver") ? " driver " : "";
      if (s.startsWith("controller")) {
       type += " controller ";
       type += s.contains("XZ") ? " steerXZ " : s.contains("XY") ? " steerXY " : "";
      } else if (s.startsWith("wheel")) {
       type += " wheel ";
       addWheel = s.startsWith("wheelPoint") || addWheel;
      } else if (s.startsWith("steer")) {
       type += s.startsWith("steerXY") ? " steerXY " : s.startsWith("steerYZ") ? " steerYZ " : U.startsWith(s, "steerXZ", "steers") ? " steerXZ " : "";
       type += s.startsWith("steerFromYZ") ? " steerFromYZ " : s.startsWith("steerFromXZ") ? " steerFromXZ " : "";
      }
      type += s.startsWith("shake") ? " shake " : "";
      type += s.startsWith("line") ? " line " : "";
      type += s.startsWith("conic") ? " conic " : "";
      type += s.startsWith("cylindric") ? " cylindric " : "";
      type += s.startsWith("strip") ? " strip " : "";
      type += s.startsWith("grid+1") ? " grid+1 " : s.startsWith("grid") ? " grid " : "";
      type += s.startsWith("triangles+1") ? " triangles+1 " : s.startsWith("triangles") ? " triangles " : "";
      type += s.startsWith("base") ? " base " : "";
      type += s.startsWith("exterior") ? " exterior " : "";
     }
    }
    vehicleName = s.startsWith("name(") ? U.getString(s, 0) : vehicleName;
    VE.vehicleMaker = s.startsWith("maker(") ? U.getString(s, 0) : VE.vehicleMaker;
    vehicleType = s.startsWith("type(aircraft") ? VE.type.aircraft : s.startsWith("type(turret") ? VE.type.turret : vehicleType;
    if (s.startsWith("acceleration(")) {
     accelerationStages[0] = U.getValue(s, 0);
     accelerationStages[1] = U.getValue(s, 1);
    } else if (s.startsWith("speeds(")) {
     topSpeeds[0] = U.getValue(s, 0);
     topSpeeds[1] = U.getValue(s, 1);
     topSpeeds[0] = topSpeeds[0] < 0 ? -Long.MAX_VALUE : topSpeeds[0];
     topSpeeds[1] = topSpeeds[1] < 0 ? Long.MAX_VALUE : topSpeeds[1];
     try {
      topSpeeds[2] = Math.abs(U.getValue(s, 2));
     } catch (Exception ignored) {
     }
    }
    turnRate = s.startsWith("turnRate(") ? U.getValue(s, 0) : turnRate;
    maxTurn = s.startsWith("maxTurn(") ? U.getValue(s, 0) : maxTurn;
    randomTurn = s.startsWith("randomTurn(") ? U.getValue(s, 0) : randomTurn;
    brake = s.startsWith("brake(") ? U.getValue(s, 0) : brake;
    grip = s.startsWith("grip(") ? U.getValue(s, 0) < 0 ? Double.POSITIVE_INFINITY : U.getValue(s, 0) : grip;
    drag = s.startsWith("drag(") ? U.getValue(s, 0) : drag;
    bounce = s.startsWith("bounce(") ? U.getValue(s, 0) : bounce;
    if (s.startsWith("airRotate(")) {
     airAcceleration = U.getValue(s, 0) < 0 ? Double.POSITIVE_INFINITY : U.getValue(s, 0);
     airTopSpeed = U.getValue(s, 1);
    }
    airPush = s.startsWith("airPush(") ? U.getValue(s, 0) : airPush;
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
     } catch (Exception e) {
      damageDealt[0] = damageDealt[1] = damageDealt[2] = damageDealt[3] = U.getValue(s, 0);
     }
    }
    baseDamageDealt = s.startsWith("baseDamageDealt(") ? U.getValue(s, 0) : baseDamageDealt;
    turretBaseY = s.startsWith("baseY(") ? U.getValue(s, 0) : turretBaseY;
    durability = s.startsWith("durability(") ? U.getValue(s, 0) : durability;
    fragility = s.startsWith("fragility(") ? U.getValue(s, 0) : fragility;
    selfRepair = s.startsWith("selfRepair(") ? U.getValue(s, 0) : selfRepair;
    spin = s.startsWith("spin(") ? U.getValue(s, 0) : spin;
    turnDrag = s.startsWith("turnDrag(yes") || turnDrag;
    steerInPlace = s.startsWith("steerInPlace(yes") || steerInPlace;
    speedBoost = s.startsWith("speedBoost(") ? U.getValue(s, 0) : speedBoost;
    landType = s.startsWith("landType(") ? U.getString(s, 0) : landType;
    contact = s.startsWith("contact(") ? U.getString(s, 0) : contact;
    exhaust = s.startsWith("exhaustFire(yes") ? 0 : exhaust;
    othersAvoidAt = s.startsWith("othersAvoidAt(") ? U.getValue(s, 0) : othersAvoidAt;
    if (s.startsWith("engine(")) {
     engine = Engine.valueOf(U.getString(s, 0));
     try {
      engineClipQuantity = (int) Math.round(U.getValue(s, 1));
      engineTuneRatio = U.getValue(s, 2);
     } catch (Exception ignored) {
     }
    }
    amphibious = s.startsWith("amphibious(yes") || amphibious;
    driverViewY = s.startsWith("driverViewY(") ? U.getValue(s, 0) * modelSize * instanceSize + translate[1] : driverViewY;
    driverViewZ = s.startsWith("driverViewZ(") ? U.getValue(s, 0) * modelSize * instanceSize + translate[2] : driverViewZ;
    driverViewX = s.startsWith("driverViewX(") ? Math.abs(U.getValue(s, 0) * modelSize * instanceSize) + translate[0] : driverViewX;
    extraViewHeight = s.startsWith("extraViewHeight(") ? U.getValue(s, 0) : extraViewHeight;
    AIBehavior = s.startsWith("behavior(race") ? VE.AIBehavior.race : s.startsWith("behavior(fight") ? VE.AIBehavior.fight : AIBehavior;
    if (s.startsWith("special1(") && !U.getString(s, 0).isEmpty()) {
     specials.add(new Special());
     specials.get(0).type = U.getString(s, 0);
     specials.get(0).homing = s.contains("homing") || specials.get(0).homing;
    } else if (s.startsWith("gun1Y(")) {
     for (n1 = 0; n1 < VE.shotQuantity; n1++) {
      try {
       specials.get(0).ports.add(new Port());
       specials.get(0).ports.get(n1).Y = U.getValue(s, specials.get(0).type.startsWith("shotgun") ? 0 : n1) * modelSize * instanceSize;
      } catch (Exception e) {
       specials.get(0).ports.remove(specials.get(0).ports.size() - 1);
       break;
      }
     }
    } else if (s.startsWith("gun1X(")) {
     try {
      for (Port port : specials.get(0).ports) {
       port.X = U.getValue(s, specials.get(0).ports.indexOf(port)) * modelSize * instanceSize;
      }
     } catch (Exception E) {
      for (Port port : specials.get(0).ports) {
       port.X = U.getValue(s, 0) * modelSize * instanceSize;
      }
     }
    } else if (s.startsWith("gun1Z(")) {
     try {
      for (Port port : specials.get(0).ports) {
       port.Z = U.getValue(s, specials.get(0).ports.indexOf(port)) * modelSize * instanceSize;
      }
     } catch (Exception E) {
      for (Port port : specials.get(0).ports) {
       port.Z = U.getValue(s, 0) * modelSize * instanceSize;
      }
     }
    } else if (s.startsWith("XZgun1(")) {
     try {
      for (Port port : specials.get(0).ports) {
       port.XZ = U.getValue(s, specials.get(0).ports.indexOf(port));
      }
     } catch (Exception E) {
      for (Port port : specials.get(0).ports) {
       port.XZ = U.getValue(s, 0);
      }
     }
    } else if (s.startsWith("YZgun1(")) {
     try {
      for (Port port : specials.get(0).ports) {
       port.YZ = U.getValue(s, specials.get(0).ports.indexOf(port));
      }
     } catch (Exception E) {
      for (Port port : specials.get(0).ports) {
       port.YZ = U.getValue(s, 0);
      }
     }
    }
    if (specials.size() == 1) {
     specials.get(0).randomPosition = s.startsWith("gun1RandomPosition(") ? U.getValue(s, 0) * modelSize * instanceSize : specials.get(0).randomPosition;
     specials.get(0).randomAngle = s.startsWith("gun1RandomAngle(") ? U.getValue(s, 0) : specials.get(0).randomAngle;
    }
    if (s.startsWith("special2") && !U.getString(s, 0).isEmpty()) {
     specials.add(new Special());
     specials.get(1).type = U.getString(s, 0);
     specials.get(1).homing = s.contains("homing") || specials.get(1).homing;
    } else if (s.startsWith("gun2Y(")) {
     for (n1 = 0; n1 < VE.shotQuantity; n1++) {
      try {
       specials.get(1).ports.add(new Port());
       specials.get(1).ports.get(n1).Y = U.getValue(s, specials.get(1).type.startsWith("shotgun") ? 0 : n1) * modelSize * instanceSize;
      } catch (Exception e) {
       specials.get(1).ports.remove(specials.get(1).ports.size() - 1);
       break;
      }
     }
    } else if (s.startsWith("gun2X(")) {
     try {
      for (Port port : specials.get(1).ports) {
       port.X = U.getValue(s, specials.get(1).ports.indexOf(port)) * modelSize * instanceSize;
      }
     } catch (Exception E) {
      for (Port port : specials.get(1).ports) {
       port.X = U.getValue(s, 0);
      }
     }
    } else if (s.startsWith("gun2Z(")) {
     try {
      for (Port port : specials.get(1).ports) {
       port.Z = U.getValue(s, specials.get(1).ports.indexOf(port)) * modelSize * instanceSize;
      }
     } catch (Exception E) {
      for (Port port : specials.get(1).ports) {
       port.Z = U.getValue(s, 0);
      }
     }
    } else if (s.startsWith("XZgun2(")) {
     try {
      for (Port port : specials.get(1).ports) {
       port.XZ = U.getValue(s, specials.get(1).ports.indexOf(port));
      }
     } catch (Exception E) {
      for (Port port : specials.get(1).ports) {
       port.XZ = U.getValue(s, 0);
      }
     }
    } else if (s.startsWith("YZgun2(")) {
     try {
      for (Port port : specials.get(1).ports) {
       port.YZ = U.getValue(s, specials.get(1).ports.indexOf(port));
      }
     } catch (Exception E) {
      for (Port port : specials.get(1).ports) {
       port.YZ = U.getValue(s, 0);
      }
     }
    }
    if (specials.size() == 2) {
     specials.get(1).randomPosition = s.startsWith("gun2RandomPosition(") ? U.getValue(s, 0) * modelSize * instanceSize : specials.get(1).randomPosition;
     specials.get(1).randomAngle = s.startsWith("gun2RandomAngle(") ? U.getValue(s, 0) : specials.get(1).randomAngle;
    }
    explosionType = s.startsWith("explosion") ? U.getString(s, 0) : explosionType;
    explosionsWhenDestroyed = s.startsWith("explodeWhenDestroyed(") ? Math.round(U.getValue(s, 0)) : explosionsWhenDestroyed;
    modelProperties += s.startsWith("mapTerrain") ? " mapTerrain " : "";
    getSizeScaleTranslate(s, translate);
    if (s.startsWith("wheelColor(")) {
     if (s.contains("reflect")) {
      wheelType += " reflect ";
     } else {
      try {
       wheelRGB[0] = U.getValue(s, 0);
       wheelRGB[1] = U.getValue(s, 1);
       wheelRGB[2] = U.getValue(s, 2);
      } catch (Exception e) {
       if (s.contains("theRandomColor")) {
        wheelRGB[0] = theRandomColor[0];
        wheelRGB[1] = theRandomColor[1];
        wheelRGB[2] = theRandomColor[2];
        wheelType += " theRandomColor ";
       } else {
        wheelRGB[0] = wheelRGB[1] = wheelRGB[2] = U.getValue(s, 0);
       }
      }
     }
     wheelType += s.contains("noSpecular") ? " noSpecular " : s.contains("shiny") ? " shiny " : "";
    } else if (s.startsWith("rims(")) {
     if (!showVehicle) {
      break;
     }
     rimType = "";
     rimRadius = U.getValue(s, 0) * modelSize * instanceSize;
     rimDepth = Math.max(rimRadius * .0625, U.getValue(s, 1) * modelSize * instanceSize);
     try {
      rimRGB[0] = U.getValue(s, 2);
      rimRGB[1] = U.getValue(s, 3);
      rimRGB[2] = U.getValue(s, 4);
     } catch (Exception e) {
      if (s.contains("theRandomColor")) {
       rimRGB[0] = theRandomColor[0];
       rimRGB[1] = theRandomColor[1];
       rimRGB[2] = theRandomColor[2];
       rimType += " theRandomColor ";
      } else {
       rimRGB[0] = rimRGB[1] = rimRGB[2] = U.getValue(s, 2);
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
     if (!showVehicle) {
      break;
     }
     if (wheelCount < 4) {
      wheels.get(wheelCount).pointX = U.getValue(s, 0) * modelSize * instanceSize * modelScale[0] * instanceScale[0];
      wheels.get(wheelCount).pointZ = U.getValue(s, 2) * modelSize * instanceSize * modelScale[2] * instanceScale[2];
      wheels.get(wheelCount).skidmarkSize = Math.abs(U.getValue(s, 3)) * modelSize * instanceSize;
      wheels.get(wheelCount).sparkPoint = U.getValue(s, 4) * 2 * modelSize * instanceSize;
     }
     String side = U.getValue(s, 0) > 0 ? " R " : U.getValue(s, 0) < 0 ? " L " : U.random() < .5 ? " R " : " L ";
     setWheel(U.getValue(s, 0), U.getValue(s, 1), U.getValue(s, 2), U.getValue(s, 3), U.getValue(s, 4), wheelType + side, rimType, wheelTextureType, s.contains("steers"), s.contains("hide"));
     wheelCount++;
    }
    steerAngleMultiply = s.startsWith("steerAngleMultiply(") ? U.getValue(s, 0) : steerAngleMultiply;
   }
  } catch (IOException e) {
   System.out.println("Model-Loading Error: " + e);
   System.out.println("At File: " + model);
   System.out.println("At Line: " + s);
  }
  if (lightsAdded < 1) {
   pieces.add(new VehiclePiece(this, new double[1], new double[1], new double[1], 1, new double[]{1, 1, 1}, " light ", ""));
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
  if (vehicleType == VE.type.turret) {
   getsPushed = getsLifted = -1;
   AIBehavior = VE.AIBehavior.fight;
  }
  for (Special special : specials) {
   if (special.type.equals("spinner")) {
    spinnerSpeed = 0;
    break;
   }
  }
  turnRate = turnRate <= 0 ? Double.POSITIVE_INFINITY : turnRate;
  explosionType = explosionsWhenDestroyed > 0 && !explosionType.contains("nuclear") ? "normal" : explosionType;
  X = Y = Z = XZ = 0;
  for (VehiclePiece piece : pieces) {
   U.add(piece.MV);
  }
  if (realVehicle) {
   if (E.poolExists || E.tsunamiParts.size() > 0) {
    for (n = E.splashQuantity; --n >= 0; ) {
     splashes.add(new Splash());
    }
   }
   for (Wheel wheel : wheels) {
    for (n = 50; --n >= 0; ) {
     wheel.sparks.add(new Spark());
    }
   }
   if (U.contains(engine.name(), "jet", "turbine", "rocket") || speedBoost > 0 || exhaust == exhaust) {
    for (n = VE.jetQuantity; --n >= 0; ) {
     jets.add(new Jet());
    }
   }
   if (contact.equals("rubber")) {
    RGB[0] = wheelRGB[0] * .5;
    RGB[1] = wheelRGB[1] * .5;
    RGB[2] = wheelRGB[2] * .5;
    if (!wheelTextureType.isEmpty()) {
     RGB[0] *= .333;
     RGB[1] *= .333;
     RGB[2] *= .333;
    }
    PhongMaterial PM = new PhongMaterial();
    U.setDiffuseRGB(PM, RGB[0], RGB[1], RGB[2], .5);
    U.setSpecularRGB(PM, 0, 0, 0);
    for (Wheel wheel : wheels) {
     for (int n0 = 0; n0 < 48; n0++) {
      wheel.skidmarks.add(new Skidmark(wheel, PM));
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
   if (explosionType.equals("maxnuclear")) {
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
   setVehicle();
   if (vehicleType != VE.type.turret) {
    for (n = VE.dustQuantity; --n >= 0; ) {
     dusts.add(new Dust());
    }
   }
  }
  Quaternion baseXZ = new Quaternion(0, U.sin(XZ * .5), 0, U.cos(XZ * .5)),
  baseYZ = new Quaternion(-U.sin(YZ * .5), 0, 0, U.cos(YZ * .5)),
  baseXY = new Quaternion(0, 0, -U.sin(XY * .5), U.cos(XY * .5));
  rotation = baseXY.multiply(baseYZ).multiply(baseXZ);
 }

 public void processGraphics(boolean gamePlay) {
  for (; YZ < -180; YZ += 360) ;
  for (; YZ > 180; YZ -= 360) ;
  for (; XY < -180; XY += 360) ;
  for (; XY > 180; XY -= 360) ;
  for (; XZ < -180; XZ += 360) ;
  for (; XZ > 180; XZ -= 360) ;
  instanceToCameraDistance = U.distance(X, VE.cameraX, Y, VE.cameraY, Z, VE.cameraZ);
  if (VE.viewableMapDistance < 1 || instanceToCameraDistance < VE.viewableMapDistance + collisionRadius) {
   onFire = VE.mapName.equals("the Sun") || onFire;
   rotation.set();
   rotation.multiply(0, 0, -U.sin(XY * .5), U.cos(XY * .5));//<-Mind the multiply order!
   rotation.multiply(-U.sin(YZ * .5), 0, 0, U.cos(YZ * .5));
   rotation.multiply(0, U.sin(XZ * .5), 0, U.cos(XZ * .5));
   for (VehiclePiece piece : pieces) {
    piece.process(gamePlay);
   }
  }
  for (VehiclePiece piece : pieces) {
   piece.MV.setVisible(piece.visible);
   piece.visible = false;
   if (piece.chip != null) {
    piece.chip.run(this, gamePlay);
   }
   if (piece.flame != null) {
    piece.flame.run(this);
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
  for (Jet jet : jets) {
   jet.run(gamePlay);
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
   for (Skidmark skidmark : wheel.skidmarks) {
    skidmark.run();
   }
  }
  if (damage > durability && VE.event == VE.event.play && !explosionType.equals("maxnuclear")) {
   long reviveTimer = 0, multiple = explosionType.startsWith("nuclear") ? 2 : 1;
   for (VehiclePiece piece : pieces) {
    if ((piece.explodeTimer += U.random(VE.tick)) > 75 * multiple) {
     piece.explodeStage = 4;
     reviveTimer++;
    }
   }
   if (reviveTimer >= pieces.size()) {
    fix(gamePlay);
    reviveImmortality = true;
   }
  }
  if (VE.defaultVehicleLightBrightness > 0 && pieces.size() > 0 && pieces.get(0).explodeStage > 0 && (VE.viewableMapDistance < 1 || U.distance(X, VE.cameraX, Y, VE.cameraY, Z, VE.cameraZ) < VE.viewableMapDistance)) {
   U.setLightRGB(burnLight, .5, .25 + U.random(.2), U.random(.125));
   U.setTranslate(burnLight, X, Y, Z);
   U.addLight(burnLight);
  }
 }

 private void deployDust(Wheel wheel) {
  if (!destroyed && !phantomEngaged && !terrainProperties.contains(" ice ")) {
   double dustSpeed = U.netValue(wheel.speedX, wheel.speedZ), speedDifference = flipped ? dustSpeed : Math.abs(Math.abs(speed) - dustSpeed);
   if (speedDifference * .5 > 10 + U.random(5.) || (!flipped && Math.abs(speed) > 50 + U.random(50.))) {
    dusts.get(currentDust).deploy(this, wheel, dustSpeed, speedDifference);
    currentDust = ++currentDust >= VE.dustQuantity ? 0 : currentDust;
   }
  }
 }

 private void skidmark(Wheel w) {
  if (contact.equals("rubber") && !flipped && !destroyed && !phantomEngaged && (Math.abs(Math.abs(speed) - U.netValue(w.speedX, w.speedZ)) > 10 + U.random(5.) || Math.abs(speed) > 50 + U.random(50.))) {
   Skidmark S = w.skidmarks.get(w.currentSkidmark);
   S.X = w.X;
   S.Z = w.Z;
   S.Y = Math.min(w.Y, w.minimumY);
   S.setScaleZ(1 + netSpeed * .01);
   U.rotate(S, w.XY, w.YZ, XZ);
   S.deployed = true;
   w.currentSkidmark = ++w.currentSkidmark >= w.skidmarks.size() ? 0 : w.currentSkidmark;
  }
 }

 private void sparks(Wheel wheel, boolean grounded) {
  double sparkX, sparkY, sparkZ;
  if (grounded) {
   sparkX = wheel.X;
   sparkY = wheel.Y;
   sparkZ = wheel.Z;
  } else {
   double[] rotateX = {wheel.pointX}, rotateY = {clearanceY - wheel.sparkPoint}, rotateZ = {wheel.pointZ};
   U.rotate(rotateX, rotateY, XY);
   U.rotate(rotateY, rotateZ, YZ);
   U.rotate(rotateX, rotateZ, XZ);
   sparkX = rotateX[0] + X;
   sparkY = rotateY[0] + Y;
   sparkZ = rotateZ[0] + Z;
  }
  double sparkSpeed = U.netValue(wheel.speedX, wheel.speedY, wheel.speedZ);
  for (Spark spark : wheel.sparks) {
   if (U.random() < .25) {
    spark.deploy(sparkX, sparkY, sparkZ, sparkSpeed);
   }
  }
  scraping = true;
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
   pieces.add(new VehiclePiece(this, x0, y0, z0, 48, wheelRGB, type + " wheel wheelFaces " + steers, textureType));//^Wheel Plates
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
     pieces.add(new VehiclePiece(this, x0, y0, z0, 17, rimRGB, type + m_rimType + " wheel sportRimFaces " + steers, textureType));//Sport rim
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
     pieces.add(new VehiclePiece(this, x0, y0, z0, 96, rimRGB, type + m_rimType + " wheel wheelRingFaces " + steers, textureType));//Sport rim ring
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
     pieces.add(new VehiclePiece(this, x0, y0, z0, 8, rimRGB, type + m_rimType + " wheel rimFaces " + steers, textureType));//Normal rim
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
    pieces.add(new VehiclePiece(this, x0, y0, z0, 48, wheelRGB, type + " wheel cylindric " + steers, textureType));//Treads
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
    pieces.add(new VehiclePiece(this, x0, y0, z0, 96, wheelRGB, type + " wheel wheelRingFaces " + steers, textureType));//Tread edges
   }
  }
 }

 private void checkPlayer() {
  if (index < 1 && VE.modeLAN == VE.LAN.OFF && VE.event != VE.event.replay) {
   drive = VE.keyUp;
   reverse = VE.keyDown;
   turnL = VE.keyL;
   turnR = VE.keyR;
   handbrake = VE.keySpace;
   useSpecial[0] = VE.keySpecial[0];
   useSpecial[1] = VE.keySpecial[1];
   boost = VE.keyBoost;
  }
 }

 private void manageCrash(double power) {
  power = Math.abs(power * fragility);
  if (power > 10) {
   damage += Math.abs(power * 2 * VE.tick);
   for (VehiclePiece piece : pieces) {
    piece.crush();
    addChip(piece, power);
   }
   if (damage <= durability && crashTimer < 1) {
    if (power > 30) {
     randomCrashSound = U.randomize(randomCrashSound, 7);
     U.soundPlay(sounds, "crashHard" + randomCrashSound, vehicleToCameraSoundDistance);
    } else {
     randomCrashSound = U.randomize(randomCrashSound, 6);
     U.soundPlay(sounds, "crashSoft" + randomCrashSound, vehicleToCameraSoundDistance);
    }
    if (bounce > .9) {
     U.soundPlay(sounds, landType + U.random(landType.equals("tires") ? 2 : 1), vehicleToCameraSoundDistance);
    }
    crashTimer = 2;
   }
  }
 }

 private void addChip(VehiclePiece vp, double power) {
  if (vp.chip != null) {
   vp.chip.deploy(this, power);
  }
 }

 public void manageCollisions() {
  checkPlayer();
  boolean replay = VE.event == VE.event.replay;
  if (!phantomEngaged) {
   double netDamage;
   if (!destroyed && !reviveImmortality) {
    for (Vehicle vehicle : VE.vehicles) {
     if (!U.sameVehicle(this, vehicle) && !U.sameTeam(this, vehicle) && !vehicle.destroyed && !vehicle.reviveImmortality && !vehicle.phantomEngaged) {
      boolean aHit = false;
      for (int n = 4; --n >= 0; ) {
       if (U.distance(wheels.get(n).X, vehicle.wheels.get(n).X, wheels.get(n).Y, vehicle.wheels.get(n).Y, wheels.get(n).Z, vehicle.wheels.get(n).Z) < (collisionRadius + vehicle.collisionRadius) * .5 + (netSpeed * .25 * VE.tick)) {
        wheels.get(n).speedY -= getsLifted > 0 && Y < vehicle.Y ? E.gravity * 1.5 * VE.tick : 0;
        netDamage = Math.abs(netSpeed - vehicle.netSpeed) * damageDealt[n] * .3;
        VE.scoreDamage[index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : netDamage;
        hitCheck(vehicle);
        double pushThemX = 0, pushThemZ = 0, pushYouX = 0, pushYouZ = 0;
        if (vehicle.damage <= vehicle.durability && getsPushed >= vehicle.getsPushed) {
         pushThemX = Math.max(0, getsPushed) * (vehicle.wheels.get(n).speedX - wheels.get(n).speedX) * .25;
         pushThemZ = Math.max(0, getsPushed) * (vehicle.wheels.get(n).speedZ - wheels.get(n).speedZ) * .25;
        }
        if (damage <= durability && getsPushed <= vehicle.getsPushed) {
         pushYouX = Math.max(0, pushesOthers) * (wheels.get(n).speedX - vehicle.wheels.get(n).speedX) * .25;
         pushYouZ = Math.max(0, pushesOthers) * (wheels.get(n).speedZ - vehicle.wheels.get(n).speedZ) * .25;
        }
        if (getsPushed >= vehicle.getsPushed) {
         if (((X > vehicle.X || wheels.get(n).X > vehicle.wheels.get(n).X) && wheels.get(n).speedX < vehicle.wheels.get(n).speedX) || ((X < vehicle.X || wheels.get(n).X < vehicle.wheels.get(n).X) && wheels.get(n).speedX > vehicle.wheels.get(n).speedX)) {
          wheels.get(n).hitOtherX = grip > 100 ? vehicle.netSpeedX : vehicle.wheels.get(n).speedX;
         }
         if (((Z > vehicle.Z || wheels.get(n).Z > vehicle.wheels.get(n).Z) && wheels.get(n).speedZ < vehicle.wheels.get(n).speedZ) || ((Z < vehicle.Z || wheels.get(n).Z < vehicle.wheels.get(n).Z) && wheels.get(n).speedZ > vehicle.wheels.get(n).speedZ)) {
          wheels.get(n).hitOtherZ = grip > 100 ? vehicle.netSpeedZ : vehicle.wheels.get(n).speedZ;
         }
        }
        vehicle.wheels.get(n).speedX += pushYouX;
        vehicle.wheels.get(n).speedZ += pushYouZ;
        wheels.get(n).speedX -= pushThemX;
        wheels.get(n).speedZ -= pushThemZ;
        if (getsLifted > 0) {
         wheels.get(n).speedY -= Y < vehicle.Y ? getsLifted * .0025 * Math.abs(U.netValue(netSpeedX, netSpeedZ) - U.netValue(vehicle.netSpeedX, vehicle.netSpeedZ)) : Y > vehicle.Y ? -getsLifted * .0025 * Math.abs(U.netValue(netSpeedX, netSpeedZ) - U.netValue(vehicle.netSpeedX, vehicle.netSpeedZ)) : 0;
        }
        vehicle.manageCrash(netDamage);
        if (wheels.get(n).speedY < -100) {
         land();
        }
        if (vehicle.getsLifted > 0) {
         vehicle.wheels.get(n).speedY -= vehicle.Y < Y ? liftsOthers * .0025 * Math.abs(U.netValue(netSpeedX, netSpeedZ) - U.netValue(vehicle.netSpeedX, vehicle.netSpeedZ)) : vehicle.Y > Y ? -liftsOthers * .0025 * Math.abs(U.netValue(netSpeedX, netSpeedZ) - U.netValue(vehicle.netSpeedX, vehicle.netSpeedZ)) : 0;
        }
        aHit = true;
       }
      }
      if (aHit) {
       if (explosionType.contains("nuclear")) {
        damage += durability + 1;
        vehicle.damage += vehicle.durability + 1;
        VE.scoreDamage[index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : vehicle.durability;
       }
       if (vehicleType != VE.type.aircraft && damageDealt[U.random(4)] >= 100 && (massiveHitTimer <= 0 || !vehicle.destroyed)) {
        vehicle.damage += vehicle.durability + 1;
        VE.scoreDamage[index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : vehicle.durability;
        U.soundPlay(sounds, "massiveHit" + U.random(2), vehicleToCameraSoundDistance);
        massiveHitTimer = U.random(5.);
        for (VehiclePiece piece : vehicle.pieces) {
         piece.crush();
         addChip(piece, U.randomPlusMinus(Math.abs(netSpeed - vehicle.netSpeed) * .5));
        }
       }
      }
      if (spinnerSpeed == spinnerSpeed && U.distance(X, vehicle.X, Y, vehicle.Y, Z, vehicle.Z) < renderRadius + vehicle.collisionRadius) {//<-'renderRadius' usage probably not ideal
       spinnerHit(vehicle);
      }
     }
    }
   }
   for (Special special : specials) {
    for (Vehicle vehicle : VE.vehicles) {
     if (!U.sameVehicle(this, vehicle) && !U.sameTeam(this, vehicle) && (!vehicle.destroyed || wrathEngaged) && !vehicle.reviveImmortality && !vehicle.phantomEngaged) {
      double diameter = special.type.startsWith("mine") ? U.netValue(vehicle.netSpeedX, vehicle.netSpeedY, vehicle.netSpeedZ) : special.diameter;
      for (Shot shot : special.shots) {
       if (shot.stage > 0 && shot.hit < 1 && (shot.doneDamaging == null || !shot.doneDamaging[vehicle.index]) && (!special.type.contains("missile") || vehicle.damage <= vehicle.durability) && !(special.type.startsWith("mine") && (U.distance(shot.X, vehicle.X, shot.Y, vehicle.Y, shot.Z, vehicle.Z) > 2000 || vehicle.damage > vehicle.durability))) {
        double amount = special.diameter + vehicle.collisionRadius, shotAverageX = (shot.X + shot.behindX) * .5, shotAverageY = (shot.Y + shot.behindY) * .5, shotAverageZ = (shot.Z + shot.behindZ) * .5;
        if (
        ((U.distance(shotAverageY, vehicle.Y, shotAverageZ, vehicle.Z) < amount || U.distance(shot.behindY, vehicle.Y, shot.behindZ, vehicle.Z) < amount) && ((shot.X > vehicle.X && shot.behindX < vehicle.X) || (shot.X < vehicle.X && shot.behindX > vehicle.X))) ||
        ((U.distance(shotAverageX, vehicle.X, shotAverageY, vehicle.Y) < amount || U.distance(shot.behindX, vehicle.X, shot.behindY, vehicle.Y) < amount) && ((shot.Z > vehicle.Z && shot.behindZ < vehicle.Z) || (shot.Z < vehicle.Z && shot.behindZ > vehicle.Z))) ||
        ((U.distance(shotAverageX, vehicle.X, shotAverageZ, vehicle.Z) < amount || U.distance(shot.behindX, vehicle.X, shot.behindZ, vehicle.Z) < amount) && ((shot.Y > vehicle.Y && shot.behindY < vehicle.Y) || (shot.Y < vehicle.Y && shot.behindY > vehicle.Y))) ||
        U.distance(shot.X, vehicle.X, shot.Y, vehicle.Y, shot.Z, vehicle.Z) < diameter + vehicle.collisionRadius) {
         hitCheck(vehicle);
         double shotDamage = special.damageDealt;
         if (special.type.startsWith("flamethrower")) {
          shotDamage /= Math.max(1, shot.stage);
          shotDamage *= VE.tick;
         } else if (U.startsWith(special.type, "raygun", "thewrath") || special.type.contains("blaster")) {
          shotDamage *= 2 * VE.tick;
         } else if (!special.type.startsWith("forcefield")) {
          shot.hit = 1;
         }
         if ((vehicle.damage += shotDamage) <= vehicle.durability && !replay) {
          VE.scoreDamage[index < VE.vehiclesInMatch >> 1 ? 0 : 1] += shotDamage;
         }
         if (special.pushPower > 0) {
          if (vehicle.getsPushed >= 0) {
           for (Wheel wheel : vehicle.wheels) {
            wheel.speedX += U.randomPlusMinus(special.pushPower);
            wheel.speedZ += U.randomPlusMinus(special.pushPower);
           }
          }
          if (vehicle.getsLifted >= 0 && (special.type.startsWith("forcefield") || U.contains(special.type, "shell", "missile"))) {
           for (Wheel wheel : vehicle.wheels) {
            wheel.speedY += U.randomPlusMinus(special.pushPower);
           }
          }
         }
         for (VehiclePiece piece : vehicle.pieces) {
          piece.crush();
          if (!special.type.startsWith("particledisintegrator")) {
           addChip(piece, U.randomPlusMinus(shot.speed * .5));
          }
         }
         double shotToCameraSoundDistance = Math.sqrt(U.distance(shot.X, VE.cameraX, shot.Y, VE.cameraY, shot.Z, VE.cameraZ)) * .08;
         if (special.useSmallHits) {
          U.soundPlay(sounds, "hit" + U.random(11), shotToCameraSoundDistance);
         }
         if (U.startsWith(special.type, "heavymachinegun", "blaster")) {
          U.soundPlay(sounds, "hit" + U.random(7), shotToCameraSoundDistance);
         } else if (U.contains(special.type, "shell", "missile") || U.startsWith(special.type, "bomb", "heavyblaster", "thewrath")) {
          U.soundPlay(sounds, "hugeHit" + U.random(12), shotToCameraSoundDistance);
         } else if (special.type.startsWith("railgun")) {
          for (int n = 4; --n >= 0; ) {
           U.soundPlay(sounds, "crashHard" + U.random(7), shotToCameraSoundDistance);
          }
         } else if (special.type.startsWith("forcefield")) {
          U.soundPlay(sounds, "crashHard" + U.random(7), vehicleToCameraSoundDistance);
          U.soundPlay(sounds, "crashHard" + U.random(7), vehicleToCameraSoundDistance);
          U.soundPlay(sounds, "crashHard" + U.random(7), vehicleToCameraSoundDistance);
         } else if (special.type.startsWith("mine")) {
          U.soundPlay(sounds, "mineExplode", shotToCameraSoundDistance);
         }
         if (U.random() < .25 && special.ricochets) {
          U.soundPlay(sounds, "hitRicochet" + U.random(11), shotToCameraSoundDistance);
         }
         if (shot.doneDamaging != null) {
          shot.doneDamaging[vehicle.index] = true;
         }
        }
       }
      }
      if (vehicleType == VE.type.turret && VE.bonusHolder < 0 && damage <= durability) {
       double bonusX = VE.bonusX, bonusY = VE.bonusY, bonusZ = VE.bonusZ, radius = special.diameter + VE.bonusBig.getRadius();
       for (Shot shot : special.shots) {
        if (shot.stage > 0) {
         double shotAverageX = (shot.X + shot.behindX) * .5, shotAverageY = (shot.Y + shot.behindY) * .5, shotAverageZ = (shot.Z + shot.behindZ) * .5;
         boolean inBoundsX = (U.distance(shotAverageY, bonusY, shotAverageZ, bonusZ) < radius || U.distance(shot.behindY, bonusY, shot.behindZ, bonusZ) < radius) && ((shot.X > bonusX && shot.behindX < bonusX) || (shot.X < bonusX && shot.behindX > bonusX)),
         inBoundsY = (U.distance(shotAverageX, bonusX, shotAverageZ, bonusZ) < radius || U.distance(shot.behindX, bonusX, shot.behindZ, bonusZ) < radius) && ((shot.Y > bonusY && shot.behindY < bonusY) || (shot.Y < bonusY && shot.behindY > bonusY)),
         inBoundsZ = (U.distance(shotAverageX, bonusX, shotAverageY, bonusY) < radius || U.distance(shot.behindX, bonusX, shot.behindY, bonusY) < radius) && ((shot.Z > bonusZ && shot.behindZ < bonusZ) || (shot.Z < bonusZ && shot.behindZ > bonusZ));
         if (inBoundsX || inBoundsZ || inBoundsY || U.distance(shot.X, bonusX, shot.Y, bonusY, shot.Z, bonusZ) < radius) {
          VE.setBonusHolder(this);
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
          if (!U.sameVehicle(this, otherVehicle) && !U.sameTeam(this, otherVehicle) && !otherVehicle.destroyed && U.distance(shot.X, otherVehicle.X, shot.Y, otherVehicle.Y, shot.Z, otherVehicle.Z) < compareDistance) {
           shotTarget = otherVehicle.index;
           compareDistance = U.distance(shot.X, otherVehicle.X, shot.Y, otherVehicle.Y, shot.Z, otherVehicle.Z);
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
         shot.YZ += shot.homeYZ > shot.YZ || shot.Y > -special.diameter * .5 - (VE.vehicles.get(shotTarget).vehicleType == VE.type.turret ? VE.vehicles.get(shotTarget).turretBaseY : VE.vehicles.get(shotTarget).clearanceY) ? 10 * VE.tick : 0;
        }
       }
      }
      if (wrathEngaged && (U.distance(X, vehicle.X, Y, vehicle.Y, Z, vehicle.Z) < absoluteRadius + netSpeed || wrathStuck[vehicle.index])) {
       if (vehicle.getsPushed >= 0) {
        vehicle.X = X;
        vehicle.Y = Y;
        vehicle.Z = Z;
        for (int n = 4; --n >= 0; ) {
         vehicle.wheels.get(n).speedX = wheels.get(n).speedX;
         vehicle.wheels.get(n).speedY = wheels.get(n).speedY;
         vehicle.wheels.get(n).speedZ = wheels.get(n).speedZ;
        }
        for (VehiclePiece piece : vehicle.pieces) {
         piece.X = X + U.randomPlusMinus(absoluteRadius);
         piece.Y = Y + U.randomPlusMinus(absoluteRadius);
         piece.Z = Z + U.randomPlusMinus(absoluteRadius);
         piece.explodeStage = Math.min(piece.explodeStage, 2);
         piece.explodeGravitySpeed = 0;
        }
        vehicle.inWrath = true;
        wrathStuck[vehicle.index] = true;
       }
       vehicle.damage += vehicle.durability + 1;
      }
     }
    }
   }
   if (!explosionType.isEmpty()) {
    boolean nuclear = explosionType.contains("nuclear");
    explosionDiameter = nuclear ? U.random(20000.) : explosionDiameter;
    explosionDamage = nuclear ? 2500 + U.random(5000.) : explosionDamage;
    for (Vehicle vehicle : VE.vehicles) {
     if (!U.sameVehicle(this, vehicle) && !U.sameTeam(this, vehicle) && !vehicle.destroyed && !vehicle.reviveImmortality && !vehicle.phantomEngaged) {
      for (Explosion explosion : explosions) {
       if (explosion.stage > 0 && U.distance(explosion.X, vehicle.X, explosion.Y, vehicle.Y, explosion.Z, vehicle.Z) < vehicle.collisionRadius + explosionDiameter) {
        if (!explosion.doneDamaging[vehicle.index]) {
         hitCheck(vehicle);
         vehicle.damage += explosionDamage;
         if (!explosionType.contains("nuclear")) {
          if (vehicle.damage <= vehicle.durability && !replay) {
           VE.scoreDamage[index < VE.vehiclesInMatch >> 1 ? 0 : 1] += explosionDamage;
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
        for (VehiclePiece piece : vehicle.pieces) {
         piece.crush();
         addChip(piece, U.randomPlusMinus(500.));
        }
        if (explosionType.contains("nuclear")) {
         U.soundPlay(sounds, "hugeHit" + U.random(12), vehicleToCameraSoundDistance);
        }
       }
      }
     }
    }
   }
   if (!destroyed) {
    if (vehicleType == VE.type.turret) {
     for (Vehicle vehicle : VE.vehicles) {
      if (!U.sameVehicle(this, vehicle) && !U.sameTeam(this, vehicle) && !vehicle.destroyed && !vehicle.reviveImmortality && U.distance(X, vehicle.X, Y + (turretBaseY * .5), vehicle.Y, Z, vehicle.Z) < 100 + vehicle.collisionRadius && !vehicle.phantomEngaged) {
       hitCheck(vehicle);
       if (vehicle.fragility > 0) {
        vehicle.damage += baseDamageDealt * vehicle.fragility;
        VE.scoreDamage[index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : baseDamageDealt * vehicle.fragility;
       }
       for (Wheel wheel : vehicle.wheels) {
        wheel.speedX += U.randomPlusMinus(500.);
        wheel.speedZ += U.randomPlusMinus(500.);
        wheel.speedY += U.randomPlusMinus(200.);
       }
       for (VehiclePiece piece : vehicle.pieces) {
        if (vehicle.fragility > 0) {
         piece.crush();
        }
        addChip(piece, U.randomPlusMinus(vehicle.netSpeed));
       }
       U.soundPlay(sounds, "crashHard" + U.random(4), vehicleToCameraSoundDistance);
       U.soundPlay(sounds, "crashHard" + U.random(4), vehicleToCameraSoundDistance);
       U.soundPlay(sounds, "crashHard" + U.random(4), vehicleToCameraSoundDistance);
      }
     }
    }
    if (!reviveImmortality) {
     for (Special special : specials) {
      if (useSpecial[specials.indexOf(special)] && special.type.startsWith("particledisintegrator")) {
       double disintegratorFieldX = Double.MAX_VALUE * U.sin(-XZ) * U.cos(-YZ);
       double disintegratorFieldY = Double.MAX_VALUE * U.sin(-YZ);
       double disintegratorFieldZ = Double.MAX_VALUE * U.cos(XZ) * U.cos(-YZ);
       for (Vehicle vehicle : VE.vehicles) {
        if (!U.sameVehicle(this, vehicle) && !U.sameTeam(this, vehicle) && !vehicle.destroyed && !vehicle.reviveImmortality && !vehicle.phantomEngaged) {
         if (((vehicle.Y <= Y && vehicle.Y >= disintegratorFieldY) || (vehicle.Y >= Y && vehicle.Y <= disintegratorFieldY) || Math.abs(vehicle.Y - Y) < vehicle.collisionRadius) && ((vehicle.X <= X && vehicle.X >= disintegratorFieldX) || (vehicle.X >= X && vehicle.X <= disintegratorFieldX)) && ((vehicle.Z <= Z && vehicle.Z >= disintegratorFieldZ) || (vehicle.Z >= Z && vehicle.Z <= disintegratorFieldZ))) {
          hitCheck(vehicle);
          vehicle.damage += 10 * VE.tick;
          VE.scoreDamage[index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : 10 * VE.tick;
         }
        }
       }
      }
     }
     if (E.lightningExists && E.lightningStrikeStage < 1) {
      if (Y >= E.stormCloudY && U.distance(X, E.lightningX, Z, E.lightningZ) < collisionRadius * 6) {
       damage += durability * .5 + (U.distance(X, E.lightningX, Z, E.lightningZ) < collisionRadius * 2 ? durability : 0);
       for (VehiclePiece piece : pieces) {
        piece.crush();
        addChip(piece, U.randomPlusMinus(500.));
       }
       U.soundPlay(sounds, "crashHard" + U.random(7), vehicleToCameraSoundDistance);
       U.soundPlay(sounds, "crashHard" + U.random(7), vehicleToCameraSoundDistance);
       U.soundPlay(sounds, "crashHard" + U.random(7), vehicleToCameraSoundDistance);
      }
     }
     for (Fire fire : E.fires) {
      double distance = U.distance(X, fire.X, Y, fire.Y, Z, fire.Z);
      if (distance < collisionRadius + fire.size) {
       damage += 10 * VE.tick;
       if (distance * 2 < collisionRadius + fire.size) {
        damage += 10 * VE.tick;
       }
       for (VehiclePiece piece : pieces) {
        piece.crush();
       }
      }
     }
     for (Boulder boulder : E.boulders) {
      if (U.distance(X, boulder.X, Z, boulder.Z) < collisionRadius + boulder.getRadius() && Y > boulder.Y - collisionRadius - boulder.getRadius()) {
       damage += durability + 1;
       for (VehiclePiece piece : pieces) {
        piece.crush();
        addChip(piece, U.randomPlusMinus(boulder.speed));
       }
       U.soundPlay(sounds, "hugeHit" + (7 + U.random(5)), vehicleToCameraSoundDistance);
      }
     }
     for (VolcanoRock volcanoRock : E.volcanoRocks) {
      double vehicleVolcanoRockDistance = U.distance(X, volcanoRock.X, Y, volcanoRock.Y, Z, volcanoRock.Z);
      if (vehicleVolcanoRockDistance < (collisionRadius + volcanoRock.getRadius()) * 1.5) {
       damage += durability * .5 + (vehicleVolcanoRockDistance < collisionRadius + volcanoRock.getRadius() ? durability : 0);
       for (VehiclePiece piece : pieces) {
        piece.crush();
        addChip(piece, U.randomPlusMinus(U.netValue(volcanoRock.speedX, volcanoRock.speedY, volcanoRock.speedZ)));
       }
       U.soundPlay(sounds, "hugeHit" + (7 + U.random(5)), vehicleToCameraSoundDistance);
      }
     }
     for (Meteor meteor : E.meteors) {
      double vehicleMeteorDistance = U.distance(X, meteor.meteorParts.get(0).X, Y, meteor.meteorParts.get(0).Y, Z, meteor.meteorParts.get(0).Z);
      if (vehicleMeteorDistance < (collisionRadius + meteor.meteorParts.get(0).getRadius()) * 4) {
       damage += durability * .5;
       for (Wheel wheel : wheels) {
        wheel.speedX += U.randomPlusMinus(E.meteorSpeed * .5);
        wheel.speedZ += U.randomPlusMinus(E.meteorSpeed * .5);
       }
       if (vehicleMeteorDistance < collisionRadius + meteor.meteorParts.get(0).getRadius() * 2) {
        damage += durability;
        for (Wheel wheel : wheels) {
         wheel.speedX += U.randomPlusMinus(E.meteorSpeed * .5);
         wheel.speedZ += U.randomPlusMinus(E.meteorSpeed * .5);
        }
       }
       for (VehiclePiece piece : pieces) {
        piece.crush();
        addChip(piece, U.randomPlusMinus(U.netValue(meteor.speedX, E.meteorSpeed, meteor.speedZ)));
       }
       U.soundPlay(sounds, "hugeHit" + (7 + U.random(5)), vehicleToCameraSoundDistance);
      }
     }
    }
   }
  }
  if (damage > durability) {
   if (explosionType.equals("maxnuclear")) {
    for (Vehicle vehicle : VE.vehicles) {
     if (!U.sameVehicle(this, vehicle) && !U.sameTeam(this, vehicle) && !gotNukeBlasted[vehicle.index] && !vehicle.reviveImmortality && U.distance(nukeBlastX, vehicle.X, nukeBlastY, vehicle.Y, nukeBlastZ, vehicle.Z) < nukeBlastSphereSize + vehicle.collisionRadius && !vehicle.phantomEngaged) {
      hitCheck(vehicle);
      vehicle.damage += vehicle.durability + 1;
      VE.scoreDamage[index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : vehicle.durability;
      if (vehicle.vehicleType != VE.type.turret) {
       double blastSpeedX = vehicle.getsPushed >= 0 ? (vehicle.X > nukeBlastX ? 6000 : vehicle.X < nukeBlastX ? -6000 : 0) * (1 + U.random(.5)) : 0,
       blastSpeedZ = vehicle.getsPushed >= 0 ? (vehicle.Z > nukeBlastZ ? 6000 : vehicle.Z < nukeBlastZ ? -6000 : 0) * (1 + U.random(.5)) : 0,
       blastSpeedY = vehicle.getsLifted >= 0 ? (vehicle.Y > nukeBlastY ? 6000 : vehicle.Y < nukeBlastY ? -6000 : 0) * (1 + U.random(.5)) : 0;
       for (Wheel wheel : vehicle.wheels) {
        wheel.speedX += blastSpeedX;
        wheel.speedZ += blastSpeedZ;
        wheel.speedY += blastSpeedY;
       }
      }
      double soundDistance = Math.sqrt(U.distance(vehicle.X, VE.cameraX, vehicle.Y, VE.cameraY, vehicle.Z, VE.cameraZ)) * .08;
      U.soundPlay(sounds, "hugeHit" + U.random(12), soundDistance);
      U.soundPlay(sounds, "hugeHit" + U.random(12), soundDistance);
      U.soundPlay(sounds, "hugeHit" + U.random(12), soundDistance);
      gotNukeBlasted[vehicle.index] = true;
     }
    }
   }
   /*if (destructionType < 1) {
    String s = VE.modeLAN != VE.LAN.OFF ? VE.playerNames[index] : vehicleName;
    destructionType = 1;
    for (Vehicle vehicle : VE.vehicles) {
     if (!U.sameVehicle(this, vehicle) && !U.sameTeam(this, vehicle) && vehicleHit == vehicle.index && vehicle.vehicleHit == index) {
      destructionType = 2;
      VE.messageWait = true;
      String s1 = VE.modeLAN != VE.LAN.OFF ? VE.playerNames[vehicle.index] : vehicle.vehicleName;
      VE.print = vehicle.index == VE.vehiclePerspective ? "You destroyed " + s + "!" : damage > durability && vehicle.damage > vehicle.durability ? s + " and " + s1 + " have destroyed each other!" : s1 + " has destroyed " + s + "!";
      VE.printTimer = 40;
     }
    }
    if (destructionType == 1 && index != VE.vehiclePerspective) {
     VE.messageWait = true;
     VE.print = s + " has been destroyed!";
     VE.printTimer = 40;
    }
   }*/
   if (destructionType < 1) {
    String s = VE.modeLAN != VE.LAN.OFF ? VE.playerNames[index] : vehicleName;
    destructionType = 1;
    for (Vehicle vehicle : VE.vehicles) {
     if (!U.sameVehicle(this, vehicle) && !U.sameTeam(this, vehicle) && vehicleHit == vehicle.index && vehicle.vehicleHit == index) {
      destructionType = 2;
      VE.updateDestructionNames();
      String s1 = VE.modeLAN != VE.LAN.OFF ? VE.playerNames[vehicle.index] : vehicle.vehicleName;
      VE.destructionNames[4][0] = s1;
      VE.destructionNames[4][1] = s;
      VE.destructionNameColors[4][0] = vehicle.index < VE.vehiclesInMatch >> 1 ? Color.color(0, 1, 0) : Color.color(1, 0, 0);
      VE.destructionNameColors[4][1] = index < VE.vehiclesInMatch >> 1 ? Color.color(0, 1, 0) : Color.color(1, 0, 0);
     }
    }
    if (destructionType == 1) {
     VE.updateDestructionNames();
     VE.destructionNames[4][0] = s;
     VE.destructionNames[4][1] = "";
     VE.destructionNameColors[4][0] = index < VE.vehiclesInMatch >> 1 ? Color.color(0, 1, 0) : Color.color(1, 0, 0);
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

 private void setVehicle() {
  mode = VE.mode.drive;
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
  setWeapons();
  setSounds();
  onVolcano = E.volcanoExists && U.distance(X, E.volcanoX, Z, E.volcanoZ) < E.volcanoBottomRadius && U.distance(X, E.volcanoX, Z, E.volcanoZ) > E.volcanoTopRadius && Y > -E.volcanoBottomRadius + U.distance(X, E.volcanoX, Z, E.volcanoZ);
  Y = onVolcano ? Math.min(Y, -E.volcanoBottomRadius + U.distance(X, E.volcanoX, Z, E.volcanoZ)) - (vehicleType == VE.type.turret ? turretBaseY : 0) : Y;
  atPoolXZ = E.poolExists && U.distance(X, E.poolX, Z, E.poolZ) < E.pool[0].getRadius();
  inPool = atPoolXZ && Y + clearanceY > 0;
  localVehicleGround = atPoolXZ ? E.groundLevel + E.poolDepth : E.groundLevel;
  AI = new AI(this);
  lightBrightness = VE.defaultVehicleLightBrightness;
 }

 private void setWeapons() {
  for (Special special : specials) {
   if (special.type.startsWith("gun")) {
    special.speed = 3000;
    special.diameter = 25;
    special.damageDealt = 100;
    special.pushPower = 2;
    special.width = 4;
    special.length = special.width * 3;
    special.ricochets = special.useSmallHits = true;
   }
   if (special.type.startsWith("machinegun")) {
    special.speed = 3000;
    special.diameter = 25;
    special.damageDealt = 50;
    special.pushPower = 1;
    special.width = 4;
    special.length = special.width * 3;
    special.ricochets = special.useSmallHits = true;
   }
   if (special.type.startsWith("minigun")) {
    special.speed = 3000;
    special.diameter = 10;
    special.damageDealt = 25;
    special.pushPower = 0;
    special.width = 2;
    special.length = special.width * 4;
    special.ricochets = special.useSmallHits = true;
   }
   if (special.type.startsWith("heavymachinegun")) {
    special.speed = 3000;
    special.diameter = 50;
    special.damageDealt = 100;
    special.pushPower = 50;
    special.width = 5;
    special.length = special.width * 3;
    special.ricochets = special.useSmallHits = true;
   }
   if (special.type.startsWith("shotgun")) {
    special.speed = 3000;
    special.diameter = 25;
    special.damageDealt = 25;
    special.pushPower = 1;
    special.width = 6;
    special.length = special.width * 2;
    special.ricochets = special.useSmallHits = true;
   }
   if (special.type.startsWith("raygun")) {
    special.speed = 15000;
    special.diameter = 10;
    special.damageDealt = 50;
    special.pushPower = 0;
    special.width = 5;
    special.length = special.width * 10;
    special.useSmallHits = true;
   }
   if (special.type.startsWith("shell")) {
    special.speed = 500;
    special.diameter = 100;
    special.damageDealt = 1500;
    special.pushPower = 1000;
    special.width = 12;
    special.length = special.width * 2;
    explosionType = "normal";
    special.AIAimPrecision = 10;
    special.hasThrust = true;
   }
   if (special.type.startsWith("powershell")) {
    special.speed = 3000;
    special.diameter = 200;
    special.damageDealt = 15000;
    special.pushPower = 1000;
    special.width = 24;
    special.length = special.width * 2;
    explosionType = "normal";
    special.AIAimPrecision = 10;
    special.hasThrust = true;
   }
   if (special.type.startsWith("bomb")) {
    special.speed = 0;
    special.diameter = 100;
    special.damageDealt = 1500;
    special.pushPower = 1000;
    special.width = 12;
    special.length = special.width * 2;
    explosionType = "normal";
   }
   if (special.type.startsWith("railgun")) {
    special.speed = 20000;
    special.diameter = 500;
    special.damageDealt = 7500;
    special.pushPower = 1000;
    special.width = 10;
    special.length = special.width * 3;
    special.AIAimPrecision = 5;
   }
   if (special.type.startsWith("missile")) {
    special.speed = 500;
    special.diameter = 100;
    special.damageDealt = 1000;
    special.pushPower = 1000;
    special.width = 6;
    special.length = special.width * 4;
    explosionType = "normal";
    special.AIAimPrecision = 10;
    special.hasThrust = true;
   }
   if (special.type.startsWith("blaster")) {
    special.speed = 1500;
    special.diameter = 20;
    special.damageDealt = 250;
    special.pushPower = 100;
    special.width = 15;
    special.useSmallHits = true;
   }
   if (special.type.startsWith("heavyblaster")) {
    special.speed = 750;
    special.diameter = 250;
    special.damageDealt = 1250;
    special.pushPower = 2000;
    special.width = 200;
    special.AIAimPrecision = 10;
   }
   if (special.type.startsWith("thewrath")) {
    special.speed = 3000;
    special.diameter = absoluteRadius;
    special.damageDealt = 1000;
    special.width = absoluteRadius * 2;
    special.AIAimPrecision = 10;
   }
   if (special.type.startsWith("flamethrower")) {
    special.speed = 250;
    special.diameter = 500;
    special.damageDealt = 250;
    special.pushPower = 0;
    special.width = 10;
    special.length = special.width;
   }
   if (special.type.startsWith("forcefield")) {
    special.diameter = collisionRadius * 2;
    special.damageDealt = 2500;
    special.pushPower = 2000;
    special.width = collisionRadius * 2;
   }
   if (special.type.startsWith("mine")) {
    special.diameter = 500;
    special.damageDealt = 15000;
    special.pushPower = 500;
    special.width = 100;
    special.length = special.width * .2;
    explosionType = "normal";
   }
   special.AIAimPrecision = special.homing ? Long.MAX_VALUE : special.AIAimPrecision;
  }
  if (explosionType.contains("nuclear")) {
   explosionDiameter = 0;
   explosionDamage = 0;
   explosionPush = 500;
  } else {
   explosionDiameter = 500;
   explosionDamage = 250;
   explosionPush = 500;
  }
  int n;
  if (!explosionType.isEmpty()) {
   for (n = VE.explosionQuantity; --n >= 0; ) {
    explosions.add(new Explosion(this));
   }
  }
  for (Special special : specials) {
   if (!U.startsWith(special.type, "particledisintegrator", "spinner")) {
    for (n = VE.shotQuantity; --n >= 0; ) {
     special.shots.add(new Shot(special));
    }
   }
   if (!special.type.contains("blaster") && !U.startsWith(special.type, "raygun", "forcefield", "mine", "thewrath")) {
    for (n = 0; n < special.ports.size(); n++) {
     special.spits.add(new MeshView());
     TriangleMesh TM = new TriangleMesh();
     TM.getTexCoords().setAll(0, 0);
     TM.getFaces().setAll(0, 0, 1, 0, 2, 0, 2, 0, 3, 0, 0, 0);
     special.spits.get(n).setMesh(TM);
     special.spits.get(n).setCullFace(CullFace.NONE);
     PhongMaterial PM = new PhongMaterial();
     U.setDiffuseRGB(PM, 0, 0, 0);
     U.setSpecularRGB(PM, 0, 0, 0);
     special.spits.get(n).setMaterial(PM);
    }
   }
   U.loadSound(sounds, "hit", special.useSmallHits ? 1 / 0. : 0);
   U.loadSound(sounds, "hitRicochet", special.ricochets ? 1 / 0. : 0);
   wrathStuck = special.type.equals("thewrath") ? new boolean[VE.vehiclesInMatch] : wrathStuck;
  }
  gotNukeBlasted = explosionType.equals("maxnuclear") ? new boolean[VE.vehiclesInMatch] : null;
 }

 private void matchStartPlacement() {
  Y = XY = YZ = 0;
  X = U.random(Math.min(50000., VE.limitR)) + U.random(Math.max(-50000., VE.limitL));
  Z = U.random(Math.min(50000., VE.limitFront)) + U.random(Math.max(-50000., VE.limitBack));
  XZ = vehicleType != VE.type.turret && VE.randomPark ? U.randomPlusMinus(180.) : 0;//<-Turrets face forward for less confusing placement
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
   if (damageDealt[U.random(4)] < 100 && vehicleType != VE.type.turret) {
    X *= X < 0 ? -1 : 1;
    Z *= Z < 0 ? -1 : 1;
   }
  } else if (VE.mapName.equals("the Test of Damage")) {
   if (damageDealt[U.random(4)] < 100 && vehicleType != VE.type.turret) {
    X = U.random(VE.limitR);
    Z = U.random(VE.limitBack);
   }
  } else if (VE.mapName.equals("Vehicular Falls")) {
   Y -= 100000;
   if (vehicleType != VE.type.turret) {
    Z = U.random(-10000.) + U.random(30000.);
    X = 0;
   }
  } else if (VE.mapName.equals("Circle Race XL")) {
   Z += 320000;
  } else if (VE.mapName.equals("XY Land")) {
   X = vehicleType != VE.type.turret ? U.random(23000.) - U.random(25000.) : X;
  } else if (VE.mapName.equals("2x2 Matrix")) {
   if (!explosionType.contains("nuclear") && vehicleType != VE.type.turret) {
    X = U.randomPlusMinus(14000.);
    Z = -U.random(31000.);
   }
  } else if (VE.mapName.equals("the Tunnel of Doom")) {
   if (!explosionType.contains("nuclear") && vehicleType != VE.type.turret) {
    X = U.randomPlusMinus(700.);
    Z = U.random(6000.) - U.random(10000.);
   }
  } else if (VE.mapName.equals("Everybody Everything")) {
   X = 0;
   Z = U.randomPlusMinus(20000.);
  } else if (VE.mapName.equals("the Maze")) {
   if (vehicleType != VE.type.turret) {
    X = Z = 0;
   }
  } else if (VE.mapName.equals("Parallel Universe Portal")) {
   Z = 0;
   X *= .1;
  } else if (E.volcanoExists) {
   X *= 2;
   Z *= 2;
  } else if (VE.mapName.equals("V.E. Speedway 2000000")) {
   boolean random = U.random() < .5;
   XZ = random ? 180 : 0;
   Z += random ? 1000000 : -1000000;
  } else if (VE.mapName.equals("Ocean Jump")) {
   X *= .5;
   Z *= 4;
   X = vehicleType != VE.type.turret ? (U.random() < .5 ? 2000 : -2000) : X;
  } else if (VE.mapName.equals("Ghost City")) {
   X *= 4;
   Z *= 4;
   if (vehicleType != VE.type.turret) {
    Y = -1000;
    X = U.random() < .5 ? 2000 : -2000;
   }
  } else if (VE.mapName.equals("Open Ocean")) {
   X *= 4;
   Z *= 4;
   if (vehicleType != VE.type.turret) {
    X = U.random() < .5 ? 2000 : -2000;
   }
  } else if (VE.mapName.equals("SUMMIT of EPIC")) {
   X = !explosionType.contains("nuclear") && vehicleType != VE.type.turret ? 0 : X;
   boolean random = U.random() < .5;
   XZ = random ? 180 : 0;
   Z = random ? 1050000 : -1050000;
   Z += U.randomPlusMinus(25000.);
  }
  if (E.gravity == 0) {
   if (VE.mapName.equals("Outer Space V1")) {
    X = U.randomPlusMinus(500.);
    Z = U.random(2000.) - U.random(4000.);
    if (explosionType.contains("nuclear")) {
     X = 0;
     Z = 100500;
    }
    if (vehicleType == VE.type.turret) {
     X = U.randomPlusMinus(50000.);
     Z = U.randomPlusMinus(50000.);
     Y = U.randomPlusMinus(50000.);
    }
   } else {
    Y = U.randomPlusMinus(50000.);
   }
  }
  if (VE.mapName.equals("Black Hole")) {
   X = Y = Z = 0;
  }
  Y -= vehicleType == VE.type.turret ? 0 : clearanceY;
  turretDefaultY = Y;
 }

 public void setTurretY() {
  if (vehicleType == VE.type.turret) {
   Y = turretDefaultY;
   double volcanoDistance = U.distance(X, E.volcanoX, Z, E.volcanoZ);
   Y = E.volcanoExists && volcanoDistance < 53000 && volcanoDistance > 3000 && Y > -53000 + volcanoDistance ? Math.min(Y, -53000 + volcanoDistance) : Y;
   if (!U.listEquals(VE.mapName, "Everybody Everything", "Devil's Stairwell")) {
    for (TrackPart trackPart : VE.trackParts) {
     if (trackPart.trackPlanes.size() > 0 && U.distance(X, trackPart.X, Z, trackPart.Z) < trackPart.renderRadius + renderRadius) {
      for (TrackPlane trackPlane : trackPart.trackPlanes) {
       double trackX = trackPlane.X + trackPart.X, trackY = trackPlane.Y + trackPart.Y, trackZ = trackPlane.Z + trackPart.Z;
       boolean inX = Math.abs(X - trackX) <= trackPlane.radiusX, inZ = Math.abs(Z - trackZ) <= trackPlane.radiusZ;
       if (Y > trackY) {
        if (inX && inZ) {
         if (trackPlane.type.contains(" tree ")) {
          Y = -trackPlane.radiusY * 2;
         } else if (trackPlane.wall.isEmpty()) {
          if (trackPlane.YZ == 0 && trackPlane.XY == 0 && Y > trackY - 5) {
           Y = Math.min(Math.max(0, E.groundLevel), trackY);
          } else if (trackPlane.YZ != 0) {
           double yz90 = 90 + trackPlane.YZ, rotatedY = trackY + ((Y - trackY) * U.cos(yz90) - (Z - trackZ) * U.sin(yz90)),
           rotatedZ = trackZ + ((Y - trackY) * U.sin(yz90) + (Z - trackZ) * U.cos(yz90)),
           rotatedZ2 = rotatedZ;
           if (rotatedZ > trackZ && rotatedZ < trackZ + 200) {
            rotatedZ = trackZ;
           }
           Y = trackY + ((rotatedY - trackY) * U.cos(-yz90) - (rotatedZ - trackZ) * U.sin(-yz90));
           Y -= rotatedZ2 > trackZ ? rotatedZ2 - trackZ : 0;
          } else if (trackPlane.XY != 0) {
           double xy90 = 90 + trackPlane.XY, rotatedY = trackY + ((Y - trackY) * U.cos(xy90) - (X - trackX) * U.sin(xy90)),
           rotatedX = trackX + ((Y - trackY) * U.sin(xy90) + (X - trackX) * U.cos(xy90)),
           rotatedX2 = rotatedX;
           if (rotatedX > trackX && rotatedX < trackX + 200) {
            rotatedX = trackX;
           }
           Y = trackY + ((rotatedY - trackY) * U.cos(-xy90) - (rotatedX - trackX) * U.sin(-xy90));
           Y -= rotatedX2 > trackX ? rotatedX2 - trackX : 0;
          }
         }
        }
       }
      }
     }
    }
   }
   Y -= turretBaseY;
  }
 }

 public void physicsVehicle() {
  checkPlayer();
  int n;
  boolean replay = VE.event == VE.event.replay;
  onVolcano = E.volcanoExists && U.distance(X, E.volcanoX, Z, E.volcanoZ) < 53000 && U.distance(X, E.volcanoX, Z, E.volcanoZ) > 3000 && Y > -53000 + U.distance(X, E.volcanoX, Z, E.volcanoZ);
  atPoolXZ = E.poolExists && U.distance(X, E.poolX, Z, E.poolZ) < E.pool[0].getRadius();
  inPool = atPoolXZ && Y + clearanceY > 0;
  localVehicleGround = atPoolXZ ? localVehicleGround + E.poolDepth : E.groundLevel;
  vehicleHit = damage <= durability ? -1 : vehicleHit;
  netSpeedX = (wheels.get(0).speedX + wheels.get(1).speedX + wheels.get(2).speedX + wheels.get(3).speedX) * .25;
  netSpeedY = (wheels.get(0).speedY + wheels.get(1).speedY + wheels.get(2).speedY + wheels.get(3).speedY) * .25;
  netSpeedZ = (wheels.get(0).speedZ + wheels.get(1).speedZ + wheels.get(2).speedZ + wheels.get(3).speedZ) * .25;
  netSpeed = U.netValue(netSpeedX, netSpeedY, netSpeedZ);
  polarity = Math.abs(YZ) > 90 ? -1 : 1;
  flipped = (Math.abs(XY) > 90 && Math.abs(YZ) <= 90) || (Math.abs(YZ) > 90 && Math.abs(XY) <= 90);
  double clearance = flipped && !landStuntsBothSides ? -absoluteRadius * .075 : clearanceY;
  if (mode.name().startsWith("drive")) {
   stuntSpeed[0] = stuntSpeed[1] = 0;
  }
  if (!destroyed) {
   if (handbrake) {
    if (vehicleType == VE.type.vehicle) {
     if (mode == VE.mode.neutral) {
      mode = VE.mode.stunt;
      U.soundPlayIfNotPlaying(sounds, "aA", vehicleToCameraSoundDistance);
     }
    } else if (mode == VE.mode.neutral) {
     mode = VE.mode.fly;
     U.soundPlayIfNotPlaying(sounds, "aA", vehicleToCameraSoundDistance);
    } else if (mode.name().startsWith("drive") && reverse) {
     Y -= 10;
     mode = VE.mode.fly;
     U.soundPlayIfNotPlaying(sounds, "aA", vehicleToCameraSoundDistance);
    }
   }
   if (mode == VE.mode.stunt) {
    if (!onAntiGravity) {
     for (Wheel wheel : wheels) {
      wheel.speedY = netSpeedY;
     }
    }
    if (drive) {
     stuntSpeed[0] -= airAcceleration < Double.POSITIVE_INFINITY ? airAcceleration * VE.tick : 0;
     stuntSpeed[0] = stuntSpeed[0] < -airTopSpeed || airAcceleration == Double.POSITIVE_INFINITY ? -airTopSpeed : stuntSpeed[0];
    }
    if (reverse) {
     stuntSpeed[0] += airAcceleration < Double.POSITIVE_INFINITY ? airAcceleration * VE.tick : 0;
     stuntSpeed[0] = stuntSpeed[0] > airTopSpeed || airAcceleration == Double.POSITIVE_INFINITY ? airTopSpeed : stuntSpeed[0];
    }
    if (!drive && !reverse) {
     if (airAcceleration < Double.POSITIVE_INFINITY) {
      stuntSpeed[0] += (stuntSpeed[0] < 0 ? 1 : stuntSpeed[0] > 0 ? -1 : 0) * airAcceleration * VE.tick;
     }
     stuntSpeed[0] = Math.abs(stuntSpeed[0]) < airAcceleration || airAcceleration == Double.POSITIVE_INFINITY ? 0 : stuntSpeed[0];
    }
    if (stuntSpeed[0] < 0) {
     double amount = Math.abs(XY) > 90 ? -1 : 1;
     X += amount * -airPush * U.sin(XZ) * -stuntSpeed[0] * VE.tick;
     Z += amount * airPush * U.cos(XZ) * -stuntSpeed[0] * VE.tick;
    }
    Y -= stuntSpeed[0] > 0 ? airPush * stuntSpeed[0] * VE.tick : 0;
    if ((turnL && !turnR) || (steerByMouse && stuntSpeed[1] * -40 < VE.mouseSteerX)) {
     stuntSpeed[1] -= airAcceleration < Double.POSITIVE_INFINITY ? airAcceleration * VE.tick : 0;
     stuntSpeed[1] = stuntSpeed[1] < -airTopSpeed || airAcceleration == Double.POSITIVE_INFINITY ? -airTopSpeed : stuntSpeed[1];
    }
    if ((turnR && !turnL) || (steerByMouse && stuntSpeed[1] * -40 > VE.mouseSteerX)) {
     stuntSpeed[1] += airAcceleration < Double.POSITIVE_INFINITY ? airAcceleration * VE.tick : 0;
     stuntSpeed[1] = stuntSpeed[1] > airTopSpeed || airAcceleration == Double.POSITIVE_INFINITY ? airTopSpeed : stuntSpeed[1];
    }
    if (!turnL && !turnR && !steerByMouse) {
     if (airAcceleration < Double.POSITIVE_INFINITY) {
      stuntSpeed[1] += (stuntSpeed[1] < 0 ? 1 : stuntSpeed[1] > 0 ? -1 : 0) * airAcceleration * VE.tick;
     }
     stuntSpeed[1] = Math.abs(stuntSpeed[1]) < airAcceleration || airAcceleration == Double.POSITIVE_INFINITY ? 0 : stuntSpeed[1];
    }
    YZ += 20 * stuntSpeed[0] * U.cos(XY) * VE.tick;
    XZ += (Math.abs(YZ) > 90 ? 20 : -20) * stuntSpeed[0] * U.sin(XY) * VE.tick;
    XY += 20 * stuntSpeed[1] * VE.tick;
    X += airPush * U.cos(XZ) * polarity * stuntSpeed[1] * VE.tick;
    Z += airPush * U.sin(XZ) * polarity * stuntSpeed[1] * VE.tick;
   } else if (mode != VE.mode.fly) {
    if (reverse) {
     speed -= speed > 0 && engine != Engine.hotrod ? brake * .5 * VE.tick : speed > -topSpeeds[0] ? accelerationStages[0] * VE.tick : 0;
    }
    if (drive) {
     if (speed < 0 && engine != Engine.hotrod) {
      speed += brake * VE.tick;
     } else {
      int u = 0;
      for (n = 2; --n >= 0; ) {
       u += speed >= topSpeeds[n] ? 1 : 0;
      }
      speed += u < 2 ? accelerationStages[u] * VE.tick : 0;
     }
    }
    if (handbrake && speed != 0) {
     if (speed < brake * VE.tick && speed > -brake * VE.tick) {
      speed = 0;
     } else {
      speed += (speed < 0 ? 1 : speed > 0 ? -1 : 0) * brake * VE.tick;
     }
    }
   }
  }
  double wheelSpun = U.clamp(-44 / VE.tick, 80 * Math.sqrt(Math.abs(Math.pow(speed, 2) * 1.333)) / collisionRadius, 44 / VE.tick), amount = speed < 0 ? -1 : 1;
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
  double turnAmount = maxTurn + U.random(randomTurn);
  if (!replay) {
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
   if (mode == VE.mode.fly) {
    if (drive || (steerByMouse && speedYZ > VE.mouseSteerY)) {
     speedYZ -= (speedYZ > 0 ? 2 : 1) * turnRate * VE.tick;
     speedYZ = Math.max(speedYZ, -maxTurn);
    }
    if (reverse || (steerByMouse && speedYZ < VE.mouseSteerY)) {
     speedYZ += (speedYZ < 0 ? 2 : 1) * turnRate * VE.tick;
     speedYZ = Math.min(speedYZ, maxTurn);
    }
   }
   if (speedYZ != 0 && (mode != VE.mode.fly || (!drive && !reverse && !steerByMouse))) {
    if (Math.abs(speedYZ) < turnRate * 2 * VE.tick) {
     speedYZ = 0;
    } else {
     speedYZ += (speedYZ < 0 ? turnRate : speedYZ > 0 ? -turnRate : 0) * 2 * VE.tick;
    }
   }
  }
  double turnSpeed = steerInPlace ? 1 : Math.min(1, Math.max(netSpeed, Math.abs(speed)) * .025);
  turnSpeed *= speed < 0 ? -1 : 1;
  if (handbrake && mode == VE.mode.fly) {
   XZ += speedXZ * turnSpeed * .2 * polarity * VE.tick;
  } else if (mode.name().startsWith("drive") || phantomEngaged) {
   if (!flipped) {
    airSpinXZ = (handbrake ? .2 : .0625) * speedXZ * turnSpeed;
    XZ += speedXZ * turnSpeed * .2 * VE.tick;
   }
  } else if (mode != VE.mode.fly) {
   XZ += airSpinXZ * VE.tick;
   stuntXZ += airSpinXZ * VE.tick;
  }
  if (mode == VE.mode.fly) {
   if (handbrake) {
    if (Math.abs(XY) < turnAmount * .2 * VE.tick) {
     XY = 0;
    } else {
     XY += (XY < 0 ? .2 : XY > 0 ? -.2 : 0) * turnAmount * VE.tick;
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
   amount = (speed < 0 ? -5 : 5) * U.sin(XY) * polarity * VE.tick;
   XZ -= amount;
   stuntXZ -= amount;
   for (Wheel wheel : wheels) {
    wheel.speedX = -speed * U.sin(XZ) * U.cos(YZ);
    wheel.speedZ = speed * U.cos(XZ) * U.cos(YZ);
    wheel.speedY = -speed * U.sin(YZ) + stallSpeed;
   }
   if (E.gravity == 0 || onAntiGravity) {
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
  if (E.gravity != 0 && !onAntiGravity) {
   speed -= speed > 0 && ((!drive && !reverse) || mode == VE.mode.stunt || mode == VE.mode.fly || speed > accelerationStages[1]) ? drag * VE.tick : speed < 0 && ((!drive && !reverse) || mode == VE.mode.stunt || mode == VE.mode.fly || speed < accelerationStages[1]) ? -drag * VE.tick : 0;
   speed = Math.abs(speed) < drag * VE.tick ? 0 : speed;
  }
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
   wheel.speedY += !phantomEngaged && !wheel.angledSurface && !inTornado ? (amphibious && atPoolXZ && wheel.Y - clearance > 0 ? -E.gravity : E.gravity) * VE.tick : 0;
  }
  if (grip <= 100 || mode != VE.mode.fly) {
   for (Wheel wheel : wheels) {
    wheel.speedX = wheel.speedX - netSpeedX > 200 ? netSpeedX + 200 : wheel.speedX - netSpeedX < -200 ? netSpeedX - 200 : wheel.speedX;
    wheel.speedZ = wheel.speedZ - netSpeedZ > 200 ? netSpeedZ + 200 : wheel.speedZ - netSpeedZ < -200 ? netSpeedZ - 200 : wheel.speedZ;
    wheel.X += (wheels.get(0).speedX + wheels.get(1).speedX + wheels.get(2).speedX + wheels.get(3).speedX) * .25 * VE.tick;
    wheel.Z += (wheels.get(0).speedZ + wheels.get(1).speedZ + wheels.get(2).speedZ + wheels.get(3).speedZ) * .25 * VE.tick;
    wheel.Y += wheel.speedY * VE.tick;
   }
  }
  if (mode.name().startsWith("drive") || phantomEngaged) {
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
   boolean isDrive = mode == VE.mode.drive;
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
     if (isDrive || phantomEngaged) {
      if (Math.abs(wheel.speedY - speed3) > setGrip) {
       wheel.speedY += wheel.speedY < speed3 ? setGrip : wheel.speedY > speed3 ? -setGrip : 0;
      } else {
       wheel.speedY = speed3;
      }
     }
    }
   }
   double netSpeedXZ = U.netValue(netSpeedX, netSpeedZ);
   if (isDrive) {
    boolean kineticFriction = Math.abs(Math.abs(speed) - netSpeedXZ) > 15, driveEngine = !U.contains(engine.name(), "prop", "jet", "rocket");
    if (((driveEngine && kineticFriction) || Math.pow(speedXZ, 2) > 300000 / netSpeedXZ) && (kineticFriction || Math.abs(speed) > topSpeeds[1] * .9)) {
     if (terrainProperties.contains(" hard ") && contact.equals("metal")) {
      for (Wheel wheel : wheels) {
       sparks(wheel, true);
      }
     }
     if (contact.equals("rubber") || !terrainProperties.contains(" hard ")) {
      for (Wheel wheel : wheels) {
       deployDust(wheel);
      }
     }
     if (!terrainProperties.contains(" ice ")) {
      for (Wheel wheel : wheels) {
       skidmark(wheel);
       wheel.minimumY = localVehicleGround;
      }
      if (damage <= durability && !flipped && !contact.isEmpty()) {
       skid();
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
   speed *= grip <= 100 && !flipped && Math.abs(speed) > netSpeedXZ && Math.abs(Math.abs(speed) - netSpeedXZ) > Math.abs(speed) * .5 && !terrainProperties.contains(" ice ") ? .5 : 1;
   if (terrainProperties.contains(" bounce ")) {
    for (Wheel wheel : wheels) {
     wheel.speedY -= U.random(.3) * Math.abs(speed) * bounce;
    }
    if (netSpeedY < -50 && bounce > .9) {
     U.soundPlayIfNotPlaying(sounds, landType + U.random(landType.equals("tires") ? 2 : 1), vehicleToCameraSoundDistance);
    }
   } else if (terrainProperties.contains(" maxbounce ")) {
    for (Wheel wheel : wheels) {
     wheel.speedY -= U.random(.6) * Math.abs(speed) * bounce;
    }
    if (netSpeedY < -50 && bounce > .9) {
     U.soundPlayIfNotPlaying(sounds, landType + U.random(landType.equals("tires") ? 2 : 1), vehicleToCameraSoundDistance);
    }
   }
   mode = VE.mode.neutral;
  }
  lastXZ = XZ;
  boolean crashLand = (Math.abs(YZ) > 30 || Math.abs(XY) > 30) && !(Math.abs(YZ) > 150 && Math.abs(XY) > 150);
  if (inPool && !phantomEngaged) {
   if (netSpeed > 0) {
    for (n = 3; --n >= 0; ) {
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
   damage += VE.poolType.equals("lava") ? 30 * VE.tick : VE.poolType.equals("acid") ? .0025 * durability * VE.tick : 0;
  }
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
  if (!phantomEngaged) {
   double hitsGroundY = -5, by = Math.min(Math.abs(U.sin(XY)) + Math.abs(U.sin(YZ)), 1), bounceXZ = Math.min(Math.abs(U.cos(XY)) + Math.abs(U.cos(YZ)), 1);
   if (onAntiGravity) {
    hitsGroundY = Double.POSITIVE_INFINITY;
    onAntiGravity = false;
   }
   hitsGroundY += atPoolXZ ? E.poolDepth : 0;
   double bounceBackForce = (Math.abs(XY) > 90 && Math.abs(YZ) <= 90) || (Math.abs(YZ) > 90 && Math.abs(XY) <= 90) ? 1 : Math.abs(U.sin(XY)) + Math.abs(U.sin(YZ));
   boolean possibleSpinnerHit = false;
   for (Wheel wheel : wheels) {
    if (wheel.Y > hitsGroundY + E.groundLevel) {
     mode = VE.mode.drive;
     if (Y + clearanceY > 100) {
      deployDust(wheel);
     }
     wheel.Y = atPoolXZ ? E.poolDepth : 0;
     if (crashLand) {
      manageCrash(wheel.speedY * bounceBackForce * .1);
     }
     if (wheel.speedY > 100) {
      land();
     }
     wheel.speedY *= wheel.speedY > 0 ? destroyed ? 0 : -bounce * by : 1;
     wheel.XY -= wheel.XY * .25;
     wheel.YZ -= wheel.YZ * .25;
     if (flipped && terrainProperties.contains(" hard ")) {
      sparks(wheel, true);
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
   if (spinnerSpeed == spinnerSpeed && possibleSpinnerHit && !((Math.abs(YZ) < 10 && Math.abs(XY) < 10) || (Math.abs(YZ) > 170 && Math.abs(XY) > 170))) {
    spinnerHit(null);
   }
   mode = amphibious && inPool ? VE.mode.drivePool : mode;
   long touched = 0;
   terrainProperties = VE.terrain + (U.contains(VE.terrain, " paved ", " rock ", " grid ", " metal ", " brightmetal") ? " hard " : " ground ");
   for (TrackPart trackPart : VE.trackParts) {
    if (trackPart.trackPlanes.size() > 0 && U.distance(X, trackPart.X, Z, trackPart.Z) < trackPart.renderRadius + renderRadius) {
     for (TrackPlane trackPlane : trackPart.trackPlanes) {
      double trackX = trackPlane.X + trackPart.X, trackY = trackPlane.Y + trackPart.Y, trackZ = trackPlane.Z + trackPart.Z,
      radiusX = trackPlane.radiusX + (trackPlane.addSpeed && Math.abs(U.sin(XZ)) > U.sin45 ? netSpeed * VE.tick : 0),
      radiusY = trackPlane.radiusY + (trackPlane.addSpeed ? netSpeed * VE.tick : 0),
      radiusZ = trackPlane.radiusZ + (trackPlane.addSpeed && Math.abs(U.sin(XZ)) < U.sin45 ? netSpeed * VE.tick : 0);
      boolean isTree = trackPlane.type.contains(" tree "), gate = trackPlane.type.contains("gate"), needsTerrainRGB = false;
      String trackProperties = "";
      if (!isTree && !gate && (!trackPlane.wall.isEmpty() || (Math.abs(X - trackX) < radiusX && Math.abs(Z - trackZ) < radiusZ && trackY + (radiusY * .5) >= Y))) {
       trackProperties = trackPlane.type + (U.contains(trackPlane.type, " paved ", " rock ", " grid ", " antigravity ", " metal ", " brightmetal") ? " hard " : " ground ");
       terrainProperties = trackPlane.wall.isEmpty() ? trackProperties : terrainProperties;
       needsTerrainRGB = trackPlane.wall.isEmpty() || needsTerrainRGB;
      }
      for (Wheel wheel : wheels) {
       if (needsTerrainRGB) {
        wheel.terrainRGB[0] = trackPlane.RGB[0];
        wheel.terrainRGB[1] = trackPlane.RGB[1];
        wheel.terrainRGB[2] = trackPlane.RGB[2];
       }
       boolean inX = Math.abs(wheel.X - trackX) <= radiusX, inZ = Math.abs(wheel.Z - trackZ) <= radiusZ;
       if (Math.abs(wheel.Y - (trackY + (E.gravity * 2 * VE.tick))) <= trackPlane.radiusY) {
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
             U.soundPlay(sounds, "gateSlow", vehicleToCameraSoundDistance);
            }
           } else if (Math.abs(trackPlane.XY) == 90) {
            if (Math.abs(wheel.speedX) > topSpeeds[0]) {
             wheel.speedX *= .333;
             U.soundPlay(sounds, "gateSlow", vehicleToCameraSoundDistance);
            }
           }
          } else {
           wheel.speedZ *= Math.abs(trackPlane.YZ) == 90 ? 3 : 1;
           wheel.speedX *= Math.abs(trackPlane.XY) == 90 ? 3 : 1;
           speed *= (speed > 0 && speed < topSpeeds[1]) || (speed < 0 && speed > -topSpeeds[0]) ? 1.25 : 1;
           if (wheel.speedX != 0 || wheel.speedZ != 0) {
            U.soundPlay(sounds, "gateSpeed", vehicleToCameraSoundDistance);
           }
          }
         } else if (trackProperties.contains(" antigravity ")) {
          wheel.speedY -= E.gravity * 2 * VE.tick;
          onAntiGravity = true;
         } else if (trackPlane.wall.isEmpty()) {
          if (wheel.speedY >= -bounce && trackPlane.YZ == 0 && trackPlane.XY == 0 && wheel.Y > trackY - 5) {
           mode = VE.mode.drive;
           wheel.Y = Math.min(Math.max(hitsGroundY, E.groundLevel), trackY);
           if (flipped && trackProperties.contains(" hard ")) {
            sparks(wheel, true);
           }
           if (crashLand) {
            manageCrash(wheel.speedY * bounceBackForce * .1);
           }
           if (wheel.speedY > 100) {
            deployDust(wheel);
            land();
           }
           wheel.speedY *= wheel.speedY > 0 ? destroyed ? 0 : -bounce * by : 1;
           wheel.XY -= wheel.XY * .25;
           wheel.YZ -= wheel.YZ * .25;
           wheel.minimumY = trackY;
          } else if (trackPlane.YZ != 0) {
           double yz90 = trackPlane.YZ + 90,
           rotatedY = trackY + ((wheel.Y - trackY) * U.cos(yz90) - (wheel.Z - trackZ) * U.sin(yz90)),
           rotatedZ = trackZ + ((wheel.Y - trackY) * U.sin(yz90) + (wheel.Z - trackZ) * U.cos(yz90));
           if (rotatedZ > trackZ - collisionRadius) {
            wheel.angledSurface = true;
            touched++;
            mode = VE.mode.drive;
            if (!trackProperties.contains(" hard ")) {
             deployDust(wheel);
            } else if (flipped) {
             sparks(wheel, true);
            }
            if (Y >= trackY || wheel.speedY >= 0 || Math.abs(speed) < E.gravity * 4 * VE.tick || Math.abs((U.cos(XZ) > 0 ? -YZ : YZ) - trackPlane.YZ) < 30) {
             wheel.Y = trackY + ((rotatedY - trackY) * U.cos(-yz90));
             wheel.YZ += (-trackPlane.YZ * U.cos(XZ) - wheel.YZ) * .25;
            }
            wheel.Z = trackZ + ((rotatedY - trackY) * U.sin(-yz90));
            wheel.XY += (trackPlane.YZ * U.sin(XZ) - wheel.XY) * .25;
            wheel.Y -= rotatedZ > trackZ ? rotatedZ - trackZ : 0;
           }
          } else if (trackPlane.XY != 0) {
           double xy90 = trackPlane.XY + 90,
           rotatedY = trackY + ((wheel.Y - trackY) * U.cos(xy90) - (wheel.X - trackX) * U.sin(xy90)),
           rotatedX = trackX + ((wheel.Y - trackY) * U.sin(xy90) + (wheel.X - trackX) * U.cos(xy90));
           if (rotatedX > trackX - collisionRadius) {
            wheel.angledSurface = true;
            touched++;
            mode = VE.mode.drive;
            if (!trackProperties.contains(" hard ")) {
             deployDust(wheel);
            } else if (flipped) {
             sparks(wheel, true);
            }
            if (Y >= trackY || wheel.speedY >= 0 || Math.abs(speed) < E.gravity * 4 * VE.tick || (Math.abs((U.sin(XZ) < 0 ? -YZ : YZ) - trackPlane.XY) < 30)) {
             wheel.Y = trackY + ((rotatedY - trackY) * U.cos(-xy90));
             wheel.YZ += (trackPlane.XY * U.sin(XZ) - wheel.YZ) * .25;
            }
            wheel.X = trackX + ((rotatedY - trackY) * U.sin(-xy90));
            wheel.XY += (trackPlane.XY * U.cos(XZ) - wheel.XY) * .25;
            wheel.Y -= rotatedX > trackX ? rotatedX - trackX : 0;
           }
          }
         }
        }
        if (!trackPlane.wall.isEmpty()) {
         double vehicleRadius = collisionRadius * .5, contactX = trackPlane.radiusX + vehicleRadius, contactZ = trackPlane.radiusZ + vehicleRadius;
         if (inX && Math.abs(wheel.Z - trackZ) <= contactZ) {
          if (trackPlane.wall.equals("F") && wheel.Z < trackZ + contactZ && wheel.speedZ < 0) {
           for (Wheel otherWheel : wheels) {
            otherWheel.Z -= wheel != otherWheel && otherWheel.Z >= trackZ + contactZ ? wheel.Z - (trackZ + contactZ) : 0;
           }
           wheel.Z = trackZ + contactZ;
           if (trackProperties.contains(" hard ")) {
            sparks(wheel, false);
           }
           manageCrash(wheel.speedZ * trackPlane.damage * .1);
           wheel.speedZ += Math.abs(wheel.speedZ) * bounce * bounceXZ;
           wheel.againstWall = true;
          }
          if (trackPlane.wall.equals("B") && wheel.Z > trackZ - contactZ && wheel.speedZ > 0) {
           for (Wheel otherWheel : wheels) {
            otherWheel.Z -= wheel != otherWheel && otherWheel.Z <= trackZ - contactZ ? wheel.Z - (trackZ - contactZ) : 0;
           }
           wheel.Z = trackZ - contactZ;
           if (trackProperties.contains(" hard ")) {
            sparks(wheel, false);
           }
           manageCrash(wheel.speedZ * trackPlane.damage * .1);
           wheel.speedZ -= Math.abs(wheel.speedZ) * bounce * bounceXZ;
           wheel.againstWall = true;
          }
         }
         if (inZ && Math.abs(wheel.X - trackX) <= contactX) {
          if (trackPlane.wall.equals("R") && wheel.X < trackX + contactX && wheel.speedX < 0) {
           for (Wheel otherWheel : wheels) {
            otherWheel.X -= wheel != otherWheel && otherWheel.X >= trackX + contactX ? wheel.X - (trackX + contactX) : 0;
           }
           wheel.X = trackX + contactX;
           if (trackProperties.contains(" hard ")) {
            sparks(wheel, false);
           }
           manageCrash(wheel.speedX * trackPlane.damage * .1);
           wheel.speedX += Math.abs(wheel.speedX) * bounce * bounceXZ;
           wheel.againstWall = true;
          }
          if (trackPlane.wall.equals("L") && wheel.X > trackX - contactX && wheel.speedX > 0) {
           for (Wheel otherWheel : wheels) {
            otherWheel.X -= wheel != otherWheel && otherWheel.X <= trackX - contactX ? wheel.X - (trackX - contactX) : 0;
           }
           wheel.X = trackX - contactX;
           if (trackProperties.contains(" hard ")) {
            sparks(wheel, false);
           }
           manageCrash(wheel.speedX * trackPlane.damage * .1);
           wheel.speedX -= Math.abs(wheel.speedX) * bounce * bounceXZ;
           wheel.againstWall = true;
          }
         }
        }
       }
      }
     }
    }
   }
   mode = touched > 3 || onVolcano ? VE.mode.drive : mode;
   if (mode == VE.mode.drive && (wheels.get(0).angledSurface || wheels.get(1).angledSurface || wheels.get(2).angledSurface || wheels.get(3).angledSurface)) {
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
   if (spin > 0) {
    if (wheels.get(0).againstWall || wheels.get(1).againstWall || wheels.get(2).againstWall || wheels.get(3).againstWall) {
     double v1;
     for (v1 = Math.abs(XZ + 45); v1 > 180; v1 -= 360) ;
     spinMultiplyPositive = Math.abs(v1) > 90 ? 1 : -1;
     for (v1 = Math.abs(XZ - 45); v1 > 180; v1 -= 360) ;
     spinMultiplyNegative = Math.abs(v1) > 90 ? 1 : -1;
    }
    XZ += ((((wheels.get(0).speedZ * spinMultiplyNegative - wheels.get(1).speedZ * spinMultiplyPositive) + wheels.get(2).speedZ * spinMultiplyPositive - wheels.get(3).speedZ * spinMultiplyNegative) + wheels.get(0).speedX * spinMultiplyPositive + wheels.get(1).speedX * spinMultiplyNegative) - wheels.get(2).speedX * spinMultiplyNegative - wheels.get(3).speedX * spinMultiplyPositive) * spin * VE.tick;
   }
  }
  speed = topSpeeds[2] < Long.MAX_VALUE ? U.clamp(-topSpeeds[2], speed, topSpeeds[2]) : speed;
  if (damage > durability && !destroyed) {
   for (n = (int) explosionsWhenDestroyed; --n >= 0; ) {
    explosions.get(currentExplosion).deploy(U.randomPlusMinus(absoluteRadius), U.randomPlusMinus(absoluteRadius), U.randomPlusMinus(absoluteRadius), this);
    currentExplosion = ++currentExplosion >= VE.explosionQuantity ? 0 : currentExplosion;
    U.soundPlay(sounds, "explode" + U.random(2), vehicleToCameraSoundDistance);
   }
  }
  if (!phantomEngaged) {
   inTornado = false;
   if (E.tornadoParts.size() > 0 && Y > E.tornadoParts.get(E.tornadoParts.size() - 1).Y && U.distance(X, E.tornadoParts.get(0).X, Z, E.tornadoParts.get(0).Z) < E.tornadoParts.get(0).getRadius() * 7.5) {
    amount = (400000 / U.distance(X, E.tornadoParts.get(0).X, Z, E.tornadoParts.get(0).Z)) * VE.tick * (mode == VE.mode.fly ? 20 : 1);
    long maxThrow = 750;
    for (Wheel wheel : wheels) {
     if (getsPushed >= 0) {
      wheel.speedX += Math.abs(wheel.speedX) < maxThrow ? U.clamp(-maxThrow, U.randomPlusMinus(amount), maxThrow) : 0;
      wheel.speedX += 2 * (X < E.tornadoParts.get(0).X && wheel.speedX < maxThrow ? Math.min(U.random(Math.pow(amount, .75)), maxThrow) : X > E.tornadoParts.get(0).X && wheel.speedX > -maxThrow ? -Math.min(U.random(Math.pow(amount, .75)), maxThrow) : 0);
      wheel.speedZ += Math.abs(wheel.speedZ) < maxThrow ? U.clamp(-maxThrow, U.randomPlusMinus(amount), maxThrow) : 0;
      wheel.speedZ += 2 * (Z < E.tornadoParts.get(0).Z && wheel.speedZ < maxThrow ? Math.min(U.random(Math.pow(amount, .75)), maxThrow) : Z > E.tornadoParts.get(0).Z && wheel.speedZ > -maxThrow ? -Math.min(U.random(Math.pow(amount, .75)), maxThrow) : 0);
     }
     wheel.speedY += getsLifted >= 0 && Math.abs(wheel.speedY) < maxThrow ? U.clamp(-maxThrow, U.randomPlusMinus(amount), maxThrow) : 0;
    }
    inTornado = getsLifted >= 0;
   }
   for (TsunamiPart tsunamiPart : E.tsunamiParts) {
    if (U.distance(X, tsunamiPart.X, Y, tsunamiPart.Y, Z, tsunamiPart.Z) < collisionRadius + tsunamiPart.getRadius()) {
     if (getsPushed >= 0) {
      for (Wheel wheel : wheels) {
       wheel.speedX += E.tsunamiSpeedX * .5 * VE.tick;
       wheel.speedZ += E.tsunamiSpeedZ * .5 * VE.tick;
      }
     }
     if (getsLifted >= 0) {
      for (Wheel wheel : wheels) {
       wheel.speedY += Y < tsunamiPart.Y ? E.gravity * 4 * VE.tick : Y > tsunamiPart.Y ? E.gravity * -4 * VE.tick : 0;
      }
     }
     for (int n1 = 20; --n1 >= 0; ) {
      splashes.get(currentSplash).deploy(wheels.get(U.random(4)), U.random(absoluteRadius * .05),
      E.tsunamiSpeedX + U.randomPlusMinus(Math.max(E.tsunamiSpeed, netSpeed)),
      U.randomPlusMinus(Math.max(E.tsunamiSpeed, netSpeed)),
      E.tsunamiSpeedZ + U.randomPlusMinus(Math.max(E.tsunamiSpeed, netSpeed)));
      currentSplash = ++currentSplash >= E.splashQuantity ? 0 : currentSplash;
     }
     U.soundPlayIfNotPlaying(sounds, "tsunamiSplash", vehicleToCameraSoundDistance);
    }
   }
  }
  for (Wheel wheel : wheels) {
   if (wheel.hitOtherX == wheel.hitOtherX) {
    wheel.speedX = wheel.hitOtherX;
    wheel.hitOtherX = Double.NaN;
   }
   if (wheel.hitOtherZ == wheel.hitOtherZ) {
    wheel.speedZ = wheel.hitOtherZ;
    wheel.hitOtherZ = Double.NaN;
   }
  }
  if (mode == VE.mode.drive) {
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
  if (mode == VE.mode.drive && !(wheels.get(0).angledSurface || wheels.get(1).angledSurface || wheels.get(2).angledSurface || wheels.get(3).angledSurface)) {
   if (Math.abs(YZ) <= 90) {
    YZ -= YZ > 0 ? VE.tick : 0;
    YZ += YZ < 0 ? VE.tick : 0;
   } else {
    YZ += YZ > 90 && YZ < 180 - VE.tick ? VE.tick : YZ < -90 && YZ > -180 + VE.tick ? -VE.tick : 0;
   }
   if (Math.abs(XY) <= 90) {
    XY -= XY > 0 ? VE.tick : 0;
    XY += XY < 0 ? VE.tick : 0;
   }
   if (sidewaysLandingAngle == 0) {
    XY += XY > 90 && XY < 180 - VE.tick ? VE.tick : XY < -90 && XY > -180 + VE.tick ? -VE.tick : 0;
   } else {
    XY += (XY > 90 && XY < sidewaysLandingAngle) || (XY < -sidewaysLandingAngle && XY > -(sidewaysLandingAngle + 30)) ? U.random(3.) * VE.tick : (XY < -90 && XY > -sidewaysLandingAngle) || (XY > sidewaysLandingAngle && XY < sidewaysLandingAngle + 30) ? -U.random(3.) * VE.tick : 0;
    if (Math.abs(XY) >= sidewaysLandingAngle + 30) {
     XY -= XY > -180 + VE.tick ? VE.tick : 0;
     XY += XY < 180 - VE.tick ? VE.tick : 0;
    }
   }
   XY *= Math.abs(XY) <= 90 ? .25 : 1;
   YZ *= Math.abs(YZ) <= 90 ? .25 : 1;
  }
  if (onVolcano) {
   double baseAngle = flipped ? 225 : 45, vehicleVolcanoXZ = XZ, VolcanoPlaneY = Math.max(E.volcanoTopRadius * .5, (E.volcanoBottomRadius * .5) - ((E.volcanoBottomRadius * .5) * (Math.abs(Y) / E.volcanoHeight)));
   vehicleVolcanoXZ += Z < E.volcanoZ && Math.abs(X - E.volcanoX) < VolcanoPlaneY ? 180 : X >= E.volcanoX + VolcanoPlaneY ? 90 : X <= E.volcanoX - VolcanoPlaneY ? -90 : 0;
   XY = baseAngle * U.sin(vehicleVolcanoXZ);
   YZ = -baseAngle * U.cos(vehicleVolcanoXZ);
  }
  if (mode.name().startsWith("drive")) {
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
   if (damage <= durability && mode == VE.mode.drive && bounce > 0) {
    XY += vehicleType == VE.type.vehicle && !flipped ? speed * clearanceY * bounce * speedXZ * (speed < 0 ? -.0000133 : .0000133) * (Math.abs(XY) > 10 ? .5 : 1) : 0;
    double vibrate = 0;
    if (U.contains(terrainProperties, " rock ")) {
     vibrate += netSpeed > U.random(50.) ? .0003 : 0;
    } else if (terrainProperties.contains(" ground ")) {
     vibrate += netSpeed > U.random(100.) ? .00015 : 0;
    }
    if (vibrate > 0) {
     YZ += U.clamp(-180 - U.random(180.), U.randomPlusMinus(vibrate) * netSpeed * clearanceY * bounce, 180 + U.random(180.));
     XY += U.clamp(-180 - U.random(180.), U.randomPlusMinus(vibrate) * netSpeed * clearanceY * bounce, 180 + U.random(180.));
    }
   }
  }
  XY += engine == Engine.hotrod && !destroyed && U.random() < .5 ? U.random() < .5 ? 1 : -1 : 0;
  if (VE.points.size() > 0) {
   if (VE.points.get(point).type.contains("...") && mode != VE.mode.fly) {
    point += U.distance(X, VE.points.get(point).X, Y, VE.points.get(point).Y, Z, VE.points.get(point).Z) < 500 ? 1 : 0;
   } else if (!VE.points.get(point).type.contains("cp") && (U.distance(X, VE.points.get(point).X, Z, VE.points.get(point).Z) < 500 || (AI.skipStunts && !VE.points.get(point).type.contains("..")) || (VE.checkpoints.size() > 0 && !VE.mapName.equals("Devil's Stairwell") && U.distance(X, VE.checkpoints.get(checkpointsPassed).X, Z, VE.checkpoints.get(checkpointsPassed).Z) <= U.distance(VE.points.get(point).X, VE.checkpoints.get(checkpointsPassed).X, VE.points.get(point).Z, VE.checkpoints.get(checkpointsPassed).Z)))) {
    point++;
   }
  }
  if (VE.checkpoints.size() > 0 && !phantomEngaged) {
   double checkSize = VE.mapName.equals("Circle Race XL") ? speed : 0;
   if ((VE.checkpoints.get(checkpointsPassed).type.contains("Z") || VE.checkpoints.get(checkpointsPassed).type.equals("cp")) && Math.abs(Z - VE.checkpoints.get(checkpointsPassed).Z) < (60 + checkSize) + Math.abs(wheels.get(0).speedZ + wheels.get(1).speedZ + wheels.get(2).speedZ + wheels.get(3).speedZ) * .25 * VE.tick && Math.abs(X - VE.checkpoints.get(checkpointsPassed).X) < 700 && Math.abs((Y - VE.checkpoints.get(checkpointsPassed).Y) + 350) < 450) {
    checkpointsPassed++;
    point++;
    if (index == VE.vehiclePerspective) {
     if (!VE.messageWait) {
      VE.print = "Checkpoint";
      VE.printTimer = 10;
     }
     if (VE.headsUpDisplay) {
      U.soundPlay(VE.sounds, "checkpoint", 0);
     }
    }
    VE.scoreCheckpoint[index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : 1;
    if (checkpointsPassed >= VE.checkpoints.size()) {
     VE.scoreLap[index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : 1;
     checkpointsPassed = point = 0;
    }
   }
   if ((VE.checkpoints.get(checkpointsPassed).type.contains("X") || VE.checkpoints.get(checkpointsPassed).type.equals("cp")) && Math.abs(X - VE.checkpoints.get(checkpointsPassed).X) < (60 + checkSize) + Math.abs(wheels.get(0).speedX + wheels.get(1).speedX + wheels.get(2).speedX + wheels.get(3).speedX) * .25 * VE.tick && Math.abs(Z - VE.checkpoints.get(checkpointsPassed).Z) < 700 && Math.abs((Y - VE.checkpoints.get(checkpointsPassed).Y) + 350) < 450) {
    checkpointsPassed++;
    point++;
    if (index == VE.vehiclePerspective) {
     if (!VE.messageWait) {
      VE.print = "Checkpoint";
      VE.printTimer = 10;
     }
     if (VE.headsUpDisplay) {
      U.soundPlay(VE.sounds, "checkpoint", 0);
     }
    }
    VE.scoreCheckpoint[index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : 1;
    if (checkpointsPassed >= VE.checkpoints.size()) {
     VE.scoreLap[index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : 1;
     checkpointsPassed = point = 0;
    }
   }
   point = checkpointsPassed > 0 ? (int) U.clamp(VE.checkpoints.get(checkpointsPassed - 1).location + 1, point, VE.checkpoints.get(checkpointsPassed).location) : point;
   if (index == VE.vehiclePerspective) {
    VE.currentCheckpoint = checkpointsPassed;
    VE.lapCheckpoint = checkpointsPassed >= VE.checkpoints.size() - 1;
   }
  }
  point = point >= VE.points.size() || point < 0 ? 0 : point;
  if (mode.name().startsWith("drive")) {
   if (stuntTimer > 8) {
    if (!stuntEnd) {
     stuntXY = stuntYZ = stuntXZ = 0;
     flipCheck[0] = flipCheck[1] = rollCheck[0] = rollCheck[1] = offTheEdge = false;
     AI.airRotationDirection[0] = U.random() < .5 ? 1 : -1;
     AI.airRotationDirection[1] = U.random() < .5 ? 1 : -1;
     stuntEnd = true;
    }
   } else {
    stuntTimer += VE.tick;
   }
  }
  if (!mode.name().startsWith("drive")) {
   stuntTimer = 0;
   stuntEnd = false;
   if (mode == VE.mode.stunt) {
    stuntXY += 20 * stuntSpeed[1] * VE.tick;
    rollCheck[0] = stuntXY > 135 || rollCheck[0];
    rollCheck[1] = stuntXY < -135 || rollCheck[1];
    stuntYZ -= 20 * stuntSpeed[0] * VE.tick;
    flipCheck[0] = stuntYZ > 135 || flipCheck[0];
    flipCheck[1] = stuntYZ < -135 || flipCheck[1];
   }
   offTheEdge = (wheels.get(0).againstWall || wheels.get(1).againstWall || wheels.get(2).againstWall || wheels.get(3).againstWall) || offTheEdge;
  } else if (!flipped || landStuntsBothSides) {
   flipTimer = 0;
   if (stuntTimer > 8 && !stuntEnd) {
    double rollReward = Math.abs(stuntXY) > 135 ? Math.abs(stuntXY) * .75 : rollCheck[0] || rollCheck[1] ? 270 : 0;
    rollReward *= rollCheck[0] && rollCheck[1] ? 2 : 1;
    double flipReward = Math.abs(stuntYZ) > 135 ? Math.abs(stuntYZ) : flipCheck[0] || flipCheck[1] ? 360 : 0;
    flipReward *= flipCheck[0] && flipCheck[1] ? 2 : 1;
    double spinReward = Math.abs(stuntXZ) >= 180 ? Math.abs(stuntXZ) : 0;
    stuntReward = (rollReward + flipReward + spinReward) + (offTheEdge ? 360 : 0);
    VE.scoreStunt[index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : stuntReward;
   }
  } else if ((flipTimer += VE.tick) > 39) {
   XZ += Math.abs(YZ) > 90 ? 180 : 0;
   speed = XY = YZ = flipTimer = 0;
  }
  speed *= (wheels.get(0).againstWall || wheels.get(1).againstWall || wheels.get(2).againstWall || wheels.get(3).againstWall) && grip > 100 && E.gravity != 0 ? .95 : 1;
  speed -= drag > 0 && engine != Engine.rocket && E.gravity != 0 ? speed * Math.abs(speed) * .000001 * drag * VE.tick : 0;
  if (U.listEquals(VE.mapName, "Vicious Versus V3", "Vehicular Fusion", "the Linear Accelerator", "Everybody Everything", "Parallel Universe Portal")) {
   if (X > VE.limitR || X < VE.limitL) {
    wheels.get(U.random(4)).speedX = wheels.get(U.random(4)).speedZ = 0;
    speed *= .95;
   }
   if (Math.abs(Y) > Math.abs(VE.limitY)) {
    wheels.get(U.random(4)).speedY = 0;
    speed *= .95;
   }
   if (Z > VE.limitFront || Z < VE.limitBack) {
    wheels.get(U.random(4)).speedX = wheels.get(U.random(4)).speedZ = 0;
    speed *= .95;
   }
  }
  if (onVolcano && !phantomEngaged) {
   Y = Math.min(Y, -53000 + U.distance(X, E.volcanoX, Z, E.volcanoZ));
   for (Wheel wheel : wheels) {
    wheel.speedY = Math.min(wheel.speedY, 0);
   }
  }
  X = U.clamp(VE.limitL, X, VE.limitR);
  Z = U.clamp(VE.limitBack, Z, VE.limitFront);
  Y = U.clamp(VE.limitY, Y, -VE.limitY);
  mode = (mode == VE.mode.stunt || mode == VE.mode.fly) && destroyed ? VE.mode.neutral : mode;
  if (mode.name().startsWith("drive") || phantomEngaged) {
   while (Math.abs(XZ - cameraXZ) > 180) {
    cameraXZ += cameraXZ < XZ ? 360 : -360;
   }
   cameraXZ += (XZ - cameraXZ) * .3 * Math.pow(VE.tick, .8);
  }
  lastXZ = XZ;
 }

 public void physicsTurret() {
  checkPlayer();
  int n;
  atPoolXZ = E.poolExists && U.distance(X, E.poolX, Z, E.poolZ) < E.pool[0].getRadius();
  inPool = atPoolXZ && Y > 0;
  polarity = 1;
  vehicleHit = damage <= durability ? -1 : vehicleHit;
  double randomTurnKick = U.random(randomTurn);
  if (steerByMouse && turnRate < 0) {
   speedXZ = U.clamp(-maxTurn - randomTurnKick, VE.mouseSteerX, maxTurn + randomTurnKick);
  } else {
   if ((turnR && !turnL) || (steerByMouse && speedXZ > VE.mouseSteerX)) {
    speedXZ -= (speedXZ > 0 ? 2 : 1) * turnRate * VE.tick;
    speedXZ = speedXZ < -maxTurn || turnRate < 0 ? -maxTurn : speedXZ;
    if (!destroyed) {
     U.soundLoop(sounds, "turret", vehicleToCameraSoundDistance);
    }
   }
   if ((turnL && !turnR) || (steerByMouse && speedXZ < VE.mouseSteerX)) {
    speedXZ += (speedXZ < 0 ? 2 : 1) * turnRate * VE.tick;
    speedXZ = speedXZ > maxTurn || turnRate < 0 ? maxTurn : speedXZ;
    if (!destroyed) {
     U.soundLoop(sounds, "turret", vehicleToCameraSoundDistance);
    }
   }
   if (speedXZ != 0 && !turnL && !turnR && !steerByMouse) {
    if (Math.abs(speedXZ) < turnRate * 2 * VE.tick || turnRate < 0) {
     speedXZ = 0;
    } else {
     speedXZ += (speedXZ < 0 ? 1 : speedXZ > 0 ? -1 : 0) * turnRate * 2 * VE.tick;
    }
   }
  }
  if (drive || (steerByMouse && speedYZ > VE.mouseSteerY)) {
   speedYZ -= (speedYZ > 0 ? 2 : 1) * turnRate * VE.tick;
   speedYZ = speedYZ < -maxTurn || turnRate < 0 ? -maxTurn : speedYZ;
   if (!destroyed) {
    U.soundLoop(sounds, "turret", vehicleToCameraSoundDistance);
   }
  }
  if (reverse || (steerByMouse && speedYZ < VE.mouseSteerY)) {
   speedYZ += (speedYZ < 0 ? 2 : 1) * turnRate * VE.tick;
   speedYZ = speedYZ > maxTurn || turnRate < 0 ? maxTurn : speedYZ;
   if (!destroyed) {
    U.soundLoop(sounds, "turret", vehicleToCameraSoundDistance);
   }
  }
  if (speedYZ != 0 && !drive && !reverse && !steerByMouse) {
   if (Math.abs(speedYZ) < turnRate * 2 * VE.tick || turnRate < 0) {
    speedYZ = 0;
   } else {
    speedYZ += (speedYZ < 0 ? 1 : speedYZ > 0 ? -1 : 0) * turnRate * 2 * VE.tick;
   }
  }
  double sharpShoot = handbrake ? .02 : .2;
  XZ += speedXZ * sharpShoot * VE.tick;
  YZ += speedYZ * sharpShoot * VE.tick;
  for (Wheel wheel : wheels) {
   wheel.X = X;
   wheel.Y = Y + (turretBaseY * .5);
   wheel.Z = Z;
   wheel.againstWall = false;
   wheel.speedX = wheel.speedZ = 0;
   wheel.speedY += damage > durability ? E.gravity * VE.tick : -wheel.speedY;
  }
  Y = turretDefaultY;
  Y -= damage <= durability ? turretBaseY : 0;
  YZ = U.clamp(-90, YZ, 90);
  XY = speed = 0;
  flipped = false;
  double turretGround = damage > durability ? 0 : -turretBaseY;
  if (Y > turretGround) {
   Y = turretGround;
   for (Wheel wheel : wheels) {
    wheel.speedY = Math.min(wheel.speedY, 0);
   }
  }
  if (explosionsWhenDestroyed > 0 && damage > durability && !destroyed) {
   for (n = (int) explosionsWhenDestroyed; --n >= 0; ) {
    explosions.get(currentExplosion).deploy(U.randomPlusMinus(absoluteRadius), U.randomPlusMinus(absoluteRadius), U.randomPlusMinus(absoluteRadius), this);
    currentExplosion = ++currentExplosion >= VE.explosionQuantity ? 0 : currentExplosion;
   }
   U.soundPlay(sounds, "hugeHit" + U.random(12), vehicleToCameraSoundDistance);
   U.soundPlay(sounds, "explode" + U.random(2), vehicleToCameraSoundDistance);
  }
  X = U.clamp(VE.limitL, X, VE.limitR);
  Z = U.clamp(VE.limitBack, Z, VE.limitFront);
  Y = U.clamp(VE.limitY, Y, -VE.limitY);
 }

 private void fix(boolean gamePlay) {
  if (!reviveImmortality && (!explosionType.equals("maxnuclear") || damage <= durability)) {
   damage = 0;
   for (FixSphere fixSphere : fixSpheres) {
    fixSphere.deploy();
   }
   if (gamePlay) {
    U.soundPlay(sounds, "fix", vehicleToCameraSoundDistance);
   }
  }
 }

 public void miscellaneous() {
  checkPlayer();
  steerByMouse = index == VE.userPlayer && VE.cursorDriving;
  int n;
  boolean gamePlay = VE.event == VE.event.play || VE.event == VE.event.replay;
  inDriverView = index == VE.vehiclePerspective && VE.cameraView.equals("driver");
  phantomEngaged = (useSpecial[0] && specials.size() > 0 && specials.get(0).type.startsWith("phantom")) || (useSpecial[1] && specials.size() > 1 && specials.get(1).type.startsWith("phantom"));
  vehicleToCameraSoundDistance = index == VE.vehiclePerspective && VE.cameraView.equals("driver") ? 0 : Math.sqrt(U.distance(X, VE.cameraX, Y, VE.cameraY, Z, VE.cameraZ)) * .08;
  massiveHitTimer -= massiveHitTimer > 0 ? VE.tick : 0;
  screenFlash -= screenFlash > 0 ? explosionType.equals("maxnuclear") ? VE.tick * .01 : VE.tick * .1 : 0;
  inWrath = false;
  if (damage > durability) {
   for (VehiclePiece piece : pieces) {
    piece.explodeStage += piece.explodeStage < 1 ? 1 : 0;
   }
   onFire = true;
   if (destructTimer <= 0) {
    U.soundPlay(sounds, "explode0", vehicleToCameraSoundDistance);
    if (explosionsWhenDestroyed > 0) {
     U.soundPlay(sounds, "explode1", vehicleToCameraSoundDistance * .5);
     if (explosionType.contains("nuclear")) {
      screenFlash = 1;
      if (explosionType.equals("maxnuclear")) {
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
       U.soundPlay(sounds, "nuke0", vehicleToCameraSoundDistance * .25);
      } else {
       U.soundPlay(sounds, "nuke" + U.random(2), vehicleToCameraSoundDistance * .5);
      }
     }
    }
   }
   destroyed = (destructTimer += VE.tick) >= 8 || destroyed;
   if (destroyed && gamePlay) {
    U.soundLoop(sounds, "burn", vehicleToCameraSoundDistance);
   }
   if (explosionType.equals("maxnuclear")) {
    U.render(nukeBlastSphere, nukeBlastX, nukeBlastY, nukeBlastZ, -nukeBlastSphereSize);
    double blastSpeed = 6000 * VE.tick;
    if (gamePlay) {
     nukeBlastSphereSize += blastSpeed;
     U.setScale(nukeBlastSphere, nukeBlastSphereSize);
     U.soundLoop(sounds, "nuke1", Math.sqrt(Math.abs(U.distance(VE.cameraX, nukeBlastX, VE.cameraY, nukeBlastY, VE.cameraZ, nukeBlastZ) - nukeBlastSphereSize)) * .04);
    } else {
     U.soundStop(sounds, "nuke1");
    }
    ((PhongMaterial) nukeBlastSphere.getMaterial()).setSelfIlluminationMap(U.getImage("firelight" + U.random(3)));
    for (NukeBlast nukeBlast : nukeBlasts) {
     if (gamePlay) {
      nukeBlast.X += blastSpeed * U.sin(nukeBlast.XZ) * U.cos(nukeBlast.YZ);
      nukeBlast.Y += blastSpeed * U.sin(nukeBlast.YZ);
      nukeBlast.Z += blastSpeed * U.cos(nukeBlast.XZ) * U.cos(nukeBlast.YZ);
     }
     if (U.outOfBounds(nukeBlast.X, nukeBlast.Y, nukeBlast.Z, nukeBlast.getRadius())) {
      nukeBlast.setVisible(false);
     } else {
      U.render(nukeBlast, nukeBlast.X, nukeBlast.Y, nukeBlast.Z);
     }
    }
   }
  } else {
   destroyed = false;
   destructTimer = 0;
   onFire = VE.mapName.equals("the Sun") && onFire;
   damage = Math.max(0, damage - ((gamePlay ? selfRepair : 0) * VE.tick));
   double deformation = (damage / durability) * 6;
   for (VehiclePiece piece : pieces) {
    piece.damage.setAngle(U.clamp(-deformation, piece.damage.getAngle(), deformation));
    piece.explodeStage = piece.explodeTimer = 0;
   }
  }
  if (spinnerSpeed == spinnerSpeed) {
   if (gamePlay) {
    if (destroyed) {
     if (Math.abs(spinnerSpeed) < .01 * VE.tick) {
      spinnerSpeed = 0;
     } else {
      spinnerSpeed += .01 * (spinnerSpeed > 0 ? -1 : 1) * VE.tick;
     }
    } else if (useSpecial[0] || useSpecial[1]) {
     double speedChange = VE.tick * .005;
     spinnerSpeed += spinnerSpeed > 0 ? speedChange : spinnerSpeed < 0 ? -speedChange : (U.random() < .5 ? -1 : 1) * Double.MIN_VALUE;
    }
   }
   for (; spinnerXZ > 180; spinnerXZ -= 360) ;
   for (; spinnerXZ < -180; spinnerXZ += 360) ;
   spinnerXZ += spinnerSpeed * 60 * VE.tick;
   spinnerSpeed = U.clamp(-1, spinnerSpeed, 1);
   long spinSound = Math.round(Math.abs(spinnerSpeed) * 9) - 1;
   for (n = 9; --n >= 0; ) {
    if (n != spinSound) {
     U.soundStop(sounds, "spinner" + n);
    }
   }
   if (gamePlay) {
    if (wheels.get(0).againstWall || wheels.get(1).againstWall || wheels.get(2).againstWall || wheels.get(3).againstWall) {
     spinnerHit(null);
    }
    spinnerSpeed *= .999;
    U.soundLoop(sounds, "spinner" + spinSound, vehicleToCameraSoundDistance);
   } else {
    U.soundStop(sounds, "spinner" + spinSound);
   }
  }
  thrusting = !destroyed && ((boost && speedBoost > 0) || exhaust > 0 || (mode != VE.mode.stunt && U.contains(engine.name(), "jet", "turbine", "rocket") && ((drive && !(mode == VE.mode.fly && engine == Engine.turbine)) || (mode == VE.mode.fly && engine != Engine.turbine))));
  if (thrusting) {
   jets.get(currentJet).deploy(this);
   currentJet = ++currentJet >= VE.jetQuantity ? 0 : currentJet;
  }
  if (VE.fixRingsExist && fixSpheres.get(U.random(fixSpheres.size())).stage <= 0 && !phantomEngaged) {
   for (TrackPart trackPart : VE.trackParts) {
    if (trackPart.isFixRing) {
     boolean sideways = Math.abs(trackPart.XZ) > 45;
     if (U.distance(sideways ? Z : X, sideways ? trackPart.Z : trackPart.X, Y, trackPart.Y) <= 500 && Math.abs(sideways ? X - trackPart.X : Z - trackPart.Z) <= 200 + Math.abs(netSpeedZ) * VE.tick) {
      fix(gamePlay);
     }
    }
   }
  }
  XY = !VE.matchStarted && engine == Engine.hotrod ? U.randomPlusMinus(2.) : XY;
  for (Special special : specials) {
   if (special.type.startsWith("phantom")) {
    if (useSpecial[specials.indexOf(special)]) {
     for (VehiclePiece piece : pieces) {
      if (U.random() < .5) {
       piece.MV.setMaterial(VE.phantomPM);
      }
     }
     if (gamePlay) {
      U.soundLoop(sounds, special.type + specials.indexOf(special), vehicleToCameraSoundDistance);
     }
    } else {
     U.soundStop(sounds, special.type + specials.indexOf(special));
     for (VehiclePiece piece : pieces) {
      piece.MV.setMaterial(piece.PM);
     }
    }
   } else if (useSpecial[specials.indexOf(special)] && special.type.startsWith("teleport")) {
    X += U.randomPlusMinus(50000.);
    Y += U.randomPlusMinus(50000.);
    Z += U.randomPlusMinus(50000.);
    if (gamePlay) {
     U.soundPlay(sounds, special.type + specials.indexOf(special), vehicleToCameraSoundDistance);
    }
   }
  }
  if (engine == Engine.train && gamePlay && VE.matchStarted) {
   n = U.random(9);
   if (Math.abs(speed) * VE.tick > U.random(5000.)) {
    U.soundPlayIfNotPlaying(sounds, "train" + n, vehicleToCameraSoundDistance);
   }
   if (U.startsWith(mode.name(), "drive", "neutral") && !destroyed && (drive || reverse)) {
    if (Math.abs(speed) > topSpeeds[1] * .75 && !U.soundRunning(sounds, "train9")) {
     U.soundPlayIfNotPlaying(sounds, "train10", vehicleToCameraSoundDistance);
    } else if (!U.soundRunning(sounds, "train10")) {
     U.soundPlayIfNotPlaying(sounds, "train9", vehicleToCameraSoundDistance);
    }
   }
  }
  if (gamePlay && speed < 0 && !destroyed) {
   U.soundLoop(sounds, "backUp", vehicleToCameraSoundDistance);
  } else {
   U.soundStop(sounds, "backUp");
  }
  wheelDiscord = false;
  boolean againstEngine = false;
  if (damage <= durability && gamePlay) {
   if (chuffTimer <= 0 && sounds.containsKey("chuff0") && Math.abs(speed) < 1 && (drive || reverse)) {
    U.soundPlay(sounds, "chuff" + U.random(5), vehicleToCameraSoundDistance);
    U.soundPlay(sounds, "chuff" + U.random(5), vehicleToCameraSoundDistance);
    chuffTimer = 22;
   }
   if (mode == VE.mode.fly && !explosionType.contains("nuclear") && (drive || reverse || turnL || turnR || (steerByMouse && (Math.abs(VE.mouseSteerX) > U.random(2000.) || Math.abs(VE.mouseSteerY) > U.random(2000.))))) {
    U.soundPlayIfNotPlaying(sounds, "fly" + U.random(2), vehicleToCameraSoundDistance);
   }
   if (vehicleType != VE.type.turret) {
    n = 0;
    if (mode != VE.mode.stunt) {
     n = vehicleType == VE.type.vehicle && steerInPlace && (turnL || turnR) ? 1 : n;
     if (drive || reverse || mode == VE.mode.fly) {
      n = drive && !(flipped && mode.name().startsWith("drive")) && U.contains(engine.name(), "prop", "jet", "turbine", "rocket") && mode != VE.mode.fly ? engineClipQuantity - 1 : Math.max((int) (engineClipQuantity * (Math.abs(speed) / topSpeeds[1])), 1);
      if (sounds.containsKey("grind")) {
       againstEngine = (drive && speed < 0) || (reverse && speed > 0);
       wheelDiscord = mode.name().startsWith("drive") && (Math.abs(wheels.get(U.random(4)).speedZ - (wheels.get(0).speedZ + wheels.get(1).speedZ + wheels.get(2).speedZ + wheels.get(3).speedZ) * .25) > 1 || Math.abs(wheels.get(U.random(4)).speedX - (wheels.get(0).speedX + wheels.get(1).speedX + wheels.get(2).speedX + wheels.get(3).speedX) * .25) > 1);
      }
     }
    }
    n = engine == Engine.turbine ? Math.max((int) (engineClipQuantity * (Math.abs(netSpeed) / topSpeeds[1])), n) : n;
    enginePowerSwitch(n);
    engineStage = Math.min(n, engineClipQuantity - 1);
   }
  } else if (vehicleType != VE.type.turret) {
   enginePowerSwitch(-1);
   engineStage = -1;
  }
  if (sounds.containsKey("grind")) {
   if (againstEngine) {
    U.soundLoop(sounds, "grind", vehicleToCameraSoundDistance);
   } else {
    U.soundStop(sounds, "grind");
   }
  }
  if (speedBoost > 0) {
   if (boost && gamePlay && !destroyed) {
    U.soundLoop(sounds, "boost", vehicleToCameraSoundDistance);
   } else {
    U.soundStop(sounds, "boost");
   }
  }
  forceTimer += U.random(.0625) * netSpeed * VE.tick;
  if (vehicleType == VE.type.turret && (destroyed || (!turnL && !turnR && !drive && !reverse && !steerByMouse))) {
   U.soundStop(sounds, "turret");
  }
  if (gamePlay) {
   int speedCheck = netSpeed > 1000 ? 5 : netSpeed > 500 ? 4 : netSpeed > 250 ? 3 : 2;
   if (vehicleType != VE.type.turret && mode == VE.mode.drive && (turnL || turnR) && !flipped && speedCheck < 3 && steerInPlace) {
    U.soundPlay(sounds, "force" + U.random(2), vehicleToCameraSoundDistance);
    forceTimer = 0;
   }
   if (Math.abs(forceTimer) > 50) {
    U.soundPlay(sounds, "force" + U.random(speedCheck), vehicleToCameraSoundDistance);
    forceTimer = 0;
   }
  }
  chuffTimer -= chuffTimer > 0 ? VE.tick : 0;
  crashTimer -= crashTimer > 0 ? VE.tick : 0;
  landTimer -= landTimer > 0 ? VE.tick : 0;
  if (splashing == splashing) {
   if (splashing > 400 && VE.cameraY <= 0) {
    U.soundLoop(sounds, "splashOver", vehicleToCameraSoundDistance);
    U.soundStop(sounds, "splash");
   } else if (splashing > 0) {
    U.soundLoop(sounds, "splash", vehicleToCameraSoundDistance);
    U.soundStop(sounds, "splashOver");
   } else {
    U.soundStop(sounds, "splash");
    U.soundStop(sounds, "splashOver");
   }
   splashing = 0;
  }
  if (exhaust == exhaust) {
   exhaust -= exhaust > 0 ? VE.tick : 0;
   if (lastEngineStage != engineStage) {
    if (U.random() < 1. / engineClipQuantity) {//<-'1' must be double
     exhaust = 5;
     randomExhaustSound = U.randomize(randomExhaustSound, 6);
     U.soundPlay(sounds, "exhaust" + randomExhaustSound, vehicleToCameraSoundDistance);
    }
    lastEngineStage = engineStage;
   }
  }
  if (sounds.containsKey("skid0")) {
   if (!skidding) {
    for (n = 10; --n >= 0; ) {
     U.soundStop(sounds, "skid" + n);
    }
   } else if (!terrainProperties.contains(" hard ")) {
    for (n = 5; --n >= 0; ) {
     U.soundStop(sounds, "skid" + n);
    }
   } else {
    for (n = 10; --n >= 5; ) {
     U.soundStop(sounds, "skid" + n);
    }
   }
   skidding = false;
  }
  if (sounds.containsKey("scrape0")) {
   if (scraping && U.netValue(netSpeedX, netSpeedY, netSpeedZ) > 100) {
    int n1;
    for (n1 = 8; --n1 >= 0; ) {
     if (U.soundRunning(sounds, "scrape" + n1)) {
      n1 = -2;
      break;
     }
    }
    if (n1 > -2) {
     randomScrapeSound = U.randomize(randomScrapeSound, 8);
     U.soundResume(sounds, "scrape" + randomScrapeSound, vehicleToCameraSoundDistance);
    }
   } else {
    for (n = 8; --n >= 0; ) {
     U.soundStop(sounds, "scrape" + n);
    }
   }
   scraping = false;
  }
  if (VE.muteSound || !destroyed || !gamePlay) {
   U.soundStop(sounds, "burn");
   if (explosionType.equals("maxnuclear")) {
    U.soundStop(sounds, "nuke1");
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
    U.soundStop(sounds, engine + "-" + n1);
   }
  }
  if (n > -1 && sounds.containsKey(engine + "-" + n)) {
   sounds.get(engine + "-" + n).loop(vehicleToCameraSoundDistance);
   if (wheelDiscord) {
    sounds.get(engine + "-" + n).clip.setFramePosition(U.random(sounds.get(engine + "-" + n).clip.getFrameLength()));
   }
   if (engine == Engine.turbine) {
    double thrustGain = Math.max(0, Math.pow(1 / (Math.abs(netSpeed) / topSpeeds[1]), 4));
    U.soundLoop(sounds, "turbineThrust", vehicleToCameraSoundDistance + (n >= engineClipQuantity - 1 ? 0 : thrustGain));
   }
  } else if (engine == Engine.turbine) {
   U.soundStop(sounds, "turbineThrust");
  }
 }

 private void spinnerHit(Vehicle vehicle) {
  if (U.random() < .5) {
   double absSpeed = Math.abs(spinnerSpeed), speedReduction = absSpeed > .95 ? 1 : U.random();
   if (vehicle != null) {
    if (absSpeed > .125) {
     double damageAmount = vehicle.durability * absSpeed * speedReduction + (speedReduction >= 1 ? 1 : 0);
     vehicle.damage += damageAmount;
     VE.scoreDamage[vehicle.index < VE.vehiclesInMatch >> 1 ? 0 : 1] += VE.event == VE.event.replay ? 0 : damageAmount;
     hitCheck(vehicle);
     for (VehiclePiece piece : vehicle.pieces) {
      piece.crush();
      addChip(piece, U.randomPlusMinus(absSpeed * .5));
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
    U.soundPlay(sounds, speedReduction >= 1 ? "massiveHit" + U.random(2) : absSpeed > .25 ? "crashHard" + U.random(5) : "crashSoft" + U.random(3), vehicleToCameraSoundDistance);
   }
  }
 }

 private void land() {
  if (damage <= durability && landTimer <= 0 && vehicleType != VE.type.turret) {
   if (landType.equals("crash")) {
    U.soundPlay(sounds, "crashHard" + U.random(7), vehicleToCameraSoundDistance);
   } else if ((Math.abs(YZ) < 30 && Math.abs(XY) < 30) || (Math.abs(YZ) > 150 && Math.abs(XY) > 150)) {
    U.soundPlay(sounds, landType + U.random(landType.equals("tires") ? 2 : 1), vehicleToCameraSoundDistance);
   } else {
    U.soundPlay(sounds, "crashSoft" + U.random(6), vehicleToCameraSoundDistance);
   }
   landTimer = 5;
  }
 }

 private void skid() {
  if (!contact.equals("metal")) {
   int n;
   if (terrainProperties.contains(" hard ")) {
    for (n = 5; --n >= 0; ) {
     if (U.soundRunning(sounds, "skid" + n)) {
      n = -2;
      break;
     }
    }
    if (n > -2) {
     randomSkidSound = U.randomize(randomSkidSound, 5);
     U.soundResume(sounds, "skid" + randomSkidSound, vehicleToCameraSoundDistance);
    }
   } else {
    for (n = 10; --n >= 5; ) {
     if (U.soundRunning(sounds, "skid" + n)) {
      n = -2;
      break;
     }
    }
    if (n > -2) {
     randomSkidSound = U.randomize(randomSkidSound, 5);
     U.soundResume(sounds, "skid" + (randomSkidSound + 5), vehicleToCameraSoundDistance);
    }
   }
   skidding = true;
  }
 }

 private void setSounds() {
  U.loadSound(sounds, "fix");
  U.loadSound(sounds, "burn");
  if (speedBoost > 0 && engine != Engine.turbine) {
   U.loadSound(sounds, "boost");
  }
  boolean loadHugeHits = E.boulders.size() > 0 || E.volcanoRocks.size() > 0 || E.meteors.size() > 0;
  for (Special special : specials) {
   if (!special.type.startsWith("spinner")) {
    U.loadSound(sounds, special.type + specials.indexOf(special), special.type);
   }
   loadHugeHits = U.startsWith(special.type, "shell", "powershell", "bomb", "missile", "heavyblaster", "thewrath", "mine") || loadHugeHits;
  }
  if (loadHugeHits) {
   U.loadSound(sounds, "hugeHit", 1 / 0.);
  }
  if ((specials.size() > 0 && specials.get(0).type.startsWith("mine")) || (specials.size() > 1 && specials.get(1).type.startsWith("mine"))) {
   U.loadSound(sounds, "mineExplode");
  }
  if (E.poolExists) {
   U.loadSound(sounds, "splash");
   U.loadSound(sounds, "splashOver");
   splashing = 0;
  }
  if (E.tsunamiParts.size() > 0) {
   U.loadSound(sounds, "tsunamiSplash");
  }
  U.loadSound(sounds, "explode0");
  if (explosionsWhenDestroyed > 0 && !explosionType.contains("nuclear")) {
   U.loadSound(sounds, "explode1");
  }
  if (vehicleType == VE.type.turret) {
   U.loadSound(sounds, "turret");
  } else {
   if (engineClipQuantity < 1) {
    File[] engines = new File(U.soundFolder).listFiles((D, name) -> name.startsWith(engine.name() + '-') && name.endsWith(U.soundExtension));
    engineClipQuantity = Objects.requireNonNull(engines).length;
    for (File file : engines) {
     String engineName = file.getName().replace(U.soundExtension, "");
     U.loadSound(sounds, engineName);
    }
   } else {
    double equalTemperament = 1;
    for (int n = 0; n < engineClipQuantity; n++) {
     try {
      sounds.put(engine + "-" + n, new Sound(engine.name(), equalTemperament));
     } catch (Exception E) {
      System.out.println("Sound loading exception: " + E);
     }
     if (engineTuneRatio < 0) {
      equalTemperament++;
     } else {
      equalTemperament *= Math.pow(engineTuneRatio, 1 / (double) (engineClipQuantity - 1));
     }
    }
   }
   U.loadSound(sounds, "gateSpeed");
   U.loadSound(sounds, "gateSlow");
   U.loadSound(sounds, "force", 5);
   if (!landType.equals("crash")) {
    U.loadSound(sounds, landType, 1 / 0.);
   }
   U.loadSound(sounds, "scrape", 1 / 0.);
   if (engine.name().contains("truck") || engine == Engine.tank || engine == Engine.massive) {
    U.loadSound(sounds, "grind");
   }
   U.loadSound(sounds, "aA");
   if (contact.equals("rubber")) {
    U.loadSound(sounds, "skid", 10);
   }
   if (vehicleType == VE.type.aircraft && !explosionType.contains("nuclear")) {
    U.loadSound(sounds, "fly", 2);
   }
  }
  boolean hasSpinner = spinnerSpeed == spinnerSpeed;
  if (hasSpinner) {
   double equalTemperament = 1;
   for (int n = 9; --n > 0; ) {
    try {
     sounds.put("spinner" + n, new Sound("spinner", equalTemperament));
    } catch (Exception E) {
     System.out.println("Sound loading exception: " + E);
    }
    equalTemperament /= Math.pow(2, 1 / 3.);
   }
  }
  if (vehicleType != VE.type.aircraft && (hasSpinner || (damageDealt[0] >= 100 || damageDealt[1] >= 100 || damageDealt[2] >= 100 || damageDealt[3] >= 100))) {
   U.loadSound(sounds, "massiveHit", 2);
  }
  if (explosionType.startsWith("nuclear")) {
   U.loadSound(sounds, "nuke", 2);
  } else if (explosionType.equals("maxnuclear")) {
   U.loadSound(sounds, "nuke0", "nukeMax0");//<-Using maxNukes but labeled as nukes
   U.loadSound(sounds, "nuke1", "nukeMax1");
  }
  U.loadSound(sounds, "crashSoft", 1 / 0.);
  U.loadSound(sounds, "crashHard", 1 / 0.);
  if (exhaust == exhaust) {
   U.loadSound(sounds, "exhaust", 1 / 0.);
  }
  if (engine == Engine.authentictruck || engine == Engine.train) {
   if (engine == Engine.train) {
    U.loadSound(sounds, "chuff", 4);
    U.loadSound(sounds, "train", 11);
   } else {
    U.loadSound(sounds, "chuff", 5);
    U.loadSound(sounds, "backUp");
   }
  } else if (engine == Engine.turbine) {
   U.loadSound(sounds, "turbineThrust");
  }
 }

 private void stopSounds() {
  for (Special special : specials) {
   if (special.type.startsWith("phantom")) {
    U.soundStop(sounds, special.type + specials.indexOf(special));
   }
  }
  U.soundStop(sounds, "boost");
  U.soundStop(sounds, "grind");
  U.soundStop(sounds, "train9");
  U.soundStop(sounds, "train10");
  if (vehicleType == VE.type.turret) {
   U.soundStop(sounds, "turret");
  } else {
   for (int n = engineClipQuantity; --n >= 0; ) {
    U.soundStop(sounds, engine.name() + '-' + n);
   }
  }
 }

 public void closeSounds() {
  for (Sound sound : sounds.values()) {
   sound.close();
  }
 }

 public void manageSpecial(Special special, boolean gamePlay) {
  if (gamePlay) {
   if (special.timer <= 0) {
    if (useSpecial[specials.indexOf(special)] && !destroyed) {
     special.time();
     shoot(special);
     wrathEngaged = special.type.startsWith("thewrath");
     if (wrathEngaged) {
      for (int n = VE.vehiclesInMatch; --n >= 0; ) {
       wrathStuck[n] = false;
      }
     }
    }
   } else {
    special.timer -= VE.tick;
   }
   if (wrathEngaged) {
    shoot(special);
    thrusting = true;
    speed = Math.min(speed + ((U.random() < .5 ? accelerationStages[0] : accelerationStages[1]) * 4 * VE.tick), topSpeeds[2]);
    damage = Math.min(damage, durability);
    screenFlash = (.5 + U.random(.5)) / Math.max(Math.sqrt(U.distance(X, VE.cameraX, Y, VE.cameraY, Z, VE.cameraZ)) * .015, 1);
    wrathEngaged = !(special.timer < 850) && wrathEngaged;
   }
  }
  special.spitStage = !destroyed && special.type.startsWith("flamethrower") ? 1 : special.spitStage;
  for (Shot shot : special.shots) {
   shot.run(this, special, gamePlay);
  }
 }

 private void shoot(Special special) {
  if (!special.type.startsWith("particledisintegrator")) {
   for (Port port : special.ports) {
    double[] shotX = {port.X}, shotY = {port.Y}, shotZ = {port.Z};
    U.rotate(shotX, shotY, XY);
    U.rotate(shotY, shotZ, YZ);
    U.rotate(shotX, shotZ, XZ);
    special.shots.get(special.currentShot).deploy(this, special, port);
    special.currentShot = ++special.currentShot >= VE.shotQuantity ? 0 : special.currentShot;
   }
   special.spitStage = 1;
   if (special.type.startsWith("phantom")) {
    U.soundLoop(sounds, special.type + specials.indexOf(special), vehicleToCameraSoundDistance);
   } else if ((!special.type.startsWith("flamethrower") || VE.globalFlick) && !wrathEngaged) {
    U.soundPlay(sounds, special.type + specials.indexOf(special), vehicleToCameraSoundDistance * (special.type.startsWith("thewrath") ? .5 : 1));
   }
  }
 }

 public void spit(Special special, boolean gamePlay) {
  int n;
  if (special.spitStage > 0 && !special.type.contains("blaster") && !U.startsWith(special.type, "raygun", "forcefield", "mine", "thewrath")) {
   long scale = 4;
   for (n = special.ports.size(); --n >= 0; ) {
    double[] spitX = {special.ports.get(n).X}, spitY = {special.ports.get(n).Y}, spitZ = {special.ports.get(n).Z};
    U.rotate(spitX, spitY, XY);
    U.rotate(spitY, spitZ, YZ);
    U.rotate(spitX, spitZ, XZ);
    U.setTranslate(special.spits.get(n), X + spitX[0], Y + spitY[0], Z + spitZ[0]);
    ((TriangleMesh) special.spits.get(n).getMesh()).getPoints().setAll(
    0, 0, (float) (scale * -special.length),
    (float) U.randomPlusMinus(scale * special.width), (float) U.randomPlusMinus(scale * special.width), 0,
    (float) U.randomPlusMinus(scale * special.width), (float) U.randomPlusMinus(scale * special.width), 0,
    0, 0, (float) (scale * special.length));
    U.rotate(special.spits.get(n), -(YZ + (special.ports.get(n).YZ * U.cos(XY)) + (special.ports.get(n).XZ * U.sin(XY))), XZ + (special.ports.get(n).XZ * U.cos(XY)) + (special.ports.get(n).YZ * U.sin(XY)) * polarity);
    ((PhongMaterial) special.spits.get(n).getMaterial()).setSelfIlluminationMap(U.getImage("firelight" + U.random(3)));
   }
   if (special.spitStage == 1) {
    for (MeshView spit : special.spits) {
     U.add(spit);
     spit.setVisible(true);
    }
   }
   special.spitStage += gamePlay ? VE.tick : 0;
   if (special.spitStage > (U.contains(special.type, "shell", "missile", "shotgun") ? 3 : 2)) {
    for (MeshView spit : special.spits) {
     spit.setVisible(false);
    }
    special.spitStage = 0;
   }
  }
 }
}
