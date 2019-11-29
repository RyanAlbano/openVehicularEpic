package ve.trackElements;

import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import ve.Camera;
import ve.Network;
import ve.Sound;
import ve.VE;
import ve.environment.E;
import ve.environment.Volcano;
import ve.trackElements.trackParts.TrackPart;
import ve.utilities.SL;
import ve.utilities.U;
import ve.vehicles.Physics;
import ve.vehicles.Vehicle;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public enum TE {//TrackElements
 ;
 public static double instanceSize = 1;
 public static double[] instanceScale = {1, 1, 1};
 public static double randomX, randomY, randomZ;
 public static long currentCheckpoint;
 public static boolean lapCheckpoint;
 private static boolean repairPointsExist;
 public static final List<TrackPart> trackParts = new ArrayList<>();
 public static final List<Point> points = new ArrayList<>();
 public static final List<Checkpoint> checkpoints = new ArrayList<>();

 public enum MS {//MapSelect
  ;
  public static int point;
  public static long X, Y, Z;
  public static double timer;
 }

 public enum Models {
  road, roadshort, roadturn, roadbendL, roadbendR, roadend, roadincline, offroad, offroadshort, offroadturn, offroadbump, offroadrocky, offroadend, offroadincline, mixroad,
  checkpoint, repair,
  ramp, rampcurved, ramptrapezoid, ramptriangle, rampwall, quarterpipe, pyramid, plateau,
  offramp, offplateau, mound, pavedmound,//<-'mound' is needed!
  floor, offfloor, wall, offwall, cube, offcube, spike, spikes, block, blocktower, border, beam, grid, tunnel, roadlift, speedgate, slowgate, antigravity,
  tree0, tree1, tree2, treepalm, cactus0, cactus1, cactus2, rainbow, crescent//<-Rainbow, crescent, etc. are not really 'track elements' but good enough
 }

 public enum Arrow {
  ;
  public static final MeshView MV = new MeshView();
  public static final Group group = new Group();
  public static Status status = Status.racetrack;
  private static Status lastStatus = Status.racetrack;
  public static SubScene scene;
  public static int target;

  public enum Status {racetrack, vehicles, locked}

  static {
   TriangleMesh TM = new TriangleMesh();
   TM.getTexCoords().setAll(0, 0);
   float arrowWidth = .25f;
   TM.getPoints().setAll(
   0, 0, arrowWidth * 3,
   0, -arrowWidth, -arrowWidth * 3,
   -arrowWidth, 0, -arrowWidth * 3,
   arrowWidth, 0, -arrowWidth * 3,
   0, arrowWidth, -arrowWidth * 3);
   TM.getFaces().setAll(
   0, 0, 2, 0, 1, 0,
   0, 0, 1, 0, 3, 0,
   0, 0, 3, 0, 4, 0,
   0, 0, 4, 0, 2, 0,
   4, 0, 1, 0, 2, 0,
   4, 0, 3, 0, 1, 0);
   MV.setMesh(TM);
   PhongMaterial PM = new PhongMaterial();
   U.Phong.setSpecularRGB(PM, 1);
   U.setMaterialSecurely(MV, PM);
   MV.setTranslateX(0);
   MV.setTranslateY(-5);
   MV.setTranslateZ(10);
  }

  public static void addToScene() {
   if (!group.getChildren().contains(MV)) {
    group.getChildren().add(MV);
   }
   MV.setVisible(false);
   PointLight backPL = new PointLight();
   backPL.setTranslateX(0);
   backPL.setTranslateY(MV.getTranslateY());
   backPL.setTranslateZ(-Long.MAX_VALUE);
   backPL.setColor(U.getColor(1));
   PointLight frontPL = new PointLight();
   frontPL.setTranslateX(0);
   frontPL.setTranslateY(MV.getTranslateY());
   frontPL.setTranslateZ(Long.MAX_VALUE);
   frontPL.setColor(U.getColor(1));
   group.getChildren().addAll(new AmbientLight(U.getColor(.5)), backPL, frontPL);
  }

  public static void run() {
   if (lastStatus != status) {
    VE.Match.print = status == Arrow.Status.locked ? "Arrow now Locked on " + VE.playerNames[target] : "Arrow now pointing at " + (status == Arrow.Status.vehicles ? "Vehicles" : SL.Map);
    VE.Match.messageWait = false;
    VE.Match.printTimer = 50;
    lastStatus = status;
   }
   Vehicle V = VE.vehicles.get(VE.vehiclePerspective);
   double d, dY, targetX = V.X, targetY = V.Y, targetZ = V.Z;
   if (status == Arrow.Status.racetrack) {
    boolean hasSize = !points.isEmpty();
    double nX = hasSize ? points.get(V.point).X : 0, nY = hasSize ? points.get(V.point).Y : 0, nZ = hasSize ? points.get(V.point).Z : 0;
    d = (nX - V.X >= 0 ? 270 : 90) + U.arcTan((nZ - V.Z) / (nX - V.X));
    dY = (nY - V.Y >= 0 ? 270 : 90) + U.arcTan(U.distance(nX, V.X, nZ, V.Z) / (nY - V.Y));
    if (hasSize) {
     targetX = points.get(V.point).X;
     targetY = points.get(V.point).Y;
     targetZ = points.get(V.point).Z;
    }
   } else {
    if (status != Arrow.Status.locked) {
     double compareDistance = Double.POSITIVE_INFINITY;
     for (Vehicle vehicle : VE.vehicles) {
      if (vehicle.index != VE.vehiclePerspective && vehicle.isIntegral() && U.distance(V, vehicle) < compareDistance) {
       target = vehicle.index;
       compareDistance = U.distance(V, vehicle);
      }
     }
     if (VE.vehiclePerspective == VE.userPlayerIndex && !U.sameTeam(VE.userPlayerIndex, target)) {
      VE.vehicles.get(VE.userPlayerIndex).AI.target = target;//Calling 'userPlayer' more accurate than 'vehiclePerspective' here
     }
    }
    target = VE.vehiclesInMatch < 2 ? 0 : target;
    Vehicle targetVehicle = VE.vehicles.get(target);
    targetX = targetVehicle.X;
    targetY = targetVehicle.Y;
    targetZ = targetVehicle.Z;
    double nameHeight = .15, B = targetVehicle.getDamage(true);
    U.fillRGB(1, 1 - B, 0);
    U.fillRectangle(.5, nameHeight, B * .1, .005);
    if (status == Arrow.Status.locked) {
     double C = VE.yinYang ? 1 : 0;
     U.strokeRGB(C, C, C);
     VE.graphicsContext.strokeLine((VE.width * .5) - 50, VE.height * nameHeight, (VE.width * .5) + 50, VE.height * nameHeight);
    }
    d = (targetVehicle.X - V.X >= 0 ? 270 : 90) + U.arcTan((targetVehicle.Z - V.Z) / (targetVehicle.X - V.X));
    dY = (targetVehicle.Y - V.Y >= 0 ? 270 : 90) + U.arcTan(U.distanceXZ(targetVehicle, V) / (targetVehicle.Y - V.Y));
    U.fillRGB(E.skyRGB.invert());
    U.text("[ " + VE.playerNames[target] + " ]", nameHeight);
   }
   U.fillRGB(VE.yinYang ? 1 : 0);
   U.text("(" + Math.round(VE.UI.getUnitDistance(U.distance(V.X, targetX, V.Y, targetY, V.Z, targetZ))) + ")", .175);
   d += Camera.XZ;
   while (d < -180) d += 360;
   while (d > 180) d -= 360;
   if (status != Arrow.Status.racetrack && (VE.vehiclesInMatch < 2 || target == VE.vehiclePerspective)) {
    d = dY = 0;
   }
   U.rotate(MV, -dY, d);
   if (status == Arrow.Status.racetrack || VE.vehiclesInMatch < 3) {
    U.Phong.setDiffuseRGB((PhongMaterial) MV.getMaterial(), E.skyRGB.invert());
   } else {
    long[] RG = {0, 0};
    if (VE.yinYang) {
     RG[target < VE.vehiclesInMatch >> 1 ? 1 : 0] = 1;
    }
    U.Phong.setDiffuseRGB((PhongMaterial) MV.getMaterial(), RG[0], RG[1], 0);
   }
  }
 }

 public enum Bonus {
  ;
  public static final Sphere big = new Sphere(500);
  public static double X, Y, Z;
  public static final List<Ball> balls = new ArrayList<>();
  public static Sound sound;

  static {
   U.setMaterialSecurely(big, new PhongMaterial());
   for (int n = 0; n < 64; n++) {
    balls.add(new TE.Bonus.Ball());
    U.setMaterialSecurely(balls.get(n), new PhongMaterial());
   }
  }

  public static class Ball extends Sphere {//<-Not worth extending Core here

   double X, Y, Z, speedX, speedY, speedZ;

   void run() {
    speedY += U.randomPlusMinus(6.);
    speedX += U.randomPlusMinus(6.);
    speedZ += U.randomPlusMinus(6.);
    speedX *= .99;
    speedY *= .99;
    speedZ *= .99;
    X += speedX * VE.tick;
    Vehicle vehicle = VE.vehicles.get(VE.bonusHolder);
    double driftTolerance = vehicle.absoluteRadius * .6;
    if (Math.abs(X) > driftTolerance) {
     speedX *= -1;
     X *= .999;
    }
    Y += speedY * VE.tick;
    if (Math.abs(Y) > driftTolerance) {
     speedY *= -1;
     Y *= .999;
    }
    Z += speedZ * VE.tick;
    if (Math.abs(Z) > driftTolerance) {
     speedZ *= -1;
     Z *= .999;
    }
    if (U.getDepth(vehicle.X + X, vehicle.Y + Y, vehicle.Z + Z) > 0) {
     U.setTranslate(this, vehicle.X + X, vehicle.Y + Y, vehicle.Z + Z);
     setVisible(true);
     U.Phong.setDiffuseRGB((PhongMaterial) getMaterial(), U.random(), U.random(), U.random());
    } else {
     setVisible(false);
    }
   }
  }

  public static void run() {
   if (VE.bonusHolder < 0) {
    if (U.getDepth(X, Y, Z) > -big.getRadius()) {
     U.setTranslate(big, X, Y, Z);
     U.Phong.setDiffuseRGB((PhongMaterial) big.getMaterial(), U.random(), U.random(), U.random());
     big.setVisible(true);
    } else {
     big.setVisible(false);
    }
    for (Bonus.Ball bonusBall : balls) {
     bonusBall.setVisible(false);
    }
   } else {
    big.setVisible(false);
    X = VE.vehicles.get(VE.bonusHolder).X;
    Y = VE.vehicles.get(VE.bonusHolder).Y;
    Z = VE.vehicles.get(VE.bonusHolder).Z;
    for (Bonus.Ball bonusBall : balls) {
     bonusBall.run();
    }
   }
   if (VE.Match.started) {
    if (Network.mode == Network.Mode.OFF) {
     for (Vehicle vehicle : VE.vehicles) {
      if (VE.bonusHolder < 0 && vehicle.isIntegral() && !vehicle.phantomEngaged && U.distance(vehicle.X, X, vehicle.Y, Y, vehicle.Z, Z) < vehicle.collisionRadius + big.getRadius()) {
       setHolder(vehicle);
      }
     }
     VE.bonusHolder = VE.bonusHolder > -1 && !VE.vehicles.get(VE.bonusHolder).isIntegral() ? -1 : VE.bonusHolder;
    } else {
     Vehicle V = VE.vehicles.get(VE.userPlayerIndex);
     if (Network.bonusHolder < 0 && V.isIntegral() && !V.phantomEngaged && U.distance(V.X, X, V.Y, Y, V.Z, Z) < V.collisionRadius + big.getRadius()) {
      Network.bonusHolder = VE.userPlayerIndex;
      if (Network.mode == Network.Mode.HOST) {
       for (PrintWriter PW : Network.out) {
        PW.println("BONUS0");
       }
      } else {
       Network.out.get(0).println(SL.BONUS);
      }
     }
     int setHolder = Network.bonusHolder < 0 ? Network.bonusHolder : VE.bonusHolder;
     if (setHolder > -1 && !VE.vehicles.get(setHolder).isIntegral()) {
      Network.bonusHolder = VE.bonusHolder = -1;
      if (Network.mode == Network.Mode.HOST) {
       for (PrintWriter PW : Network.out) {
        PW.println(SL.BonusOpen);
       }
      } else {
       Network.out.get(0).println(SL.BonusOpen);
      }
     }
     if (VE.bonusHolder != Network.bonusHolder) {
      VE.bonusHolder = Network.bonusHolder;
      if (VE.bonusHolder > -1) {
       setHolder(VE.vehicles.get(VE.bonusHolder));
      }
     }
    }
   }
  }

  public static void setHolder(Vehicle vehicle) {
   VE.bonusHolder = vehicle.index;
   for (Bonus.Ball bonusBall : balls) {
    bonusBall.setRadius(VE.vehicles.get(VE.bonusHolder).absoluteRadius * .02);
    bonusBall.X = bonusBall.Y = bonusBall.Z = bonusBall.speedX = bonusBall.speedY = bonusBall.speedZ = 0;
   }
   if (VE.Options.headsUpDisplay) {
    sound.playIfNotPlaying(0);
   }
  }
 }

 public enum Paved {
  ;
  public static final double globalShade = .55;
  public static final PhongMaterial universal = new PhongMaterial();
  public static final Image[] lowResolution = new Image[2];

  static {
   universal.setDiffuseMap(U.Images.get(SL.paved));
   universal.setSpecularMap(U.Images.get(SL.paved));
   universal.setBumpMap(U.Images.getNormalMap(SL.paved));
   U.Phong.setDiffuseRGB(universal, globalShade);
   U.Phong.setSpecularRGB(universal, E.Specular.Colors.standard);
   universal.setSpecularPower(E.Specular.Powers.standard);
   lowResolution[0] = U.Images.getLowResolution(U.Images.get(SL.paved));
   lowResolution[1] = U.Images.getLowResolution(U.Images.getNormalMap(SL.paved));
  }
 }

 public static void addTrackPart(String s, int listNumber, double summedPositionX, double summedPositionY, double summedPositionZ, double optionalRotation) {
  double[] X = {summedPositionX};
  double[] Y = {summedPositionY};
  double[] Z = {summedPositionZ};
  double rotation;
  try {
   rotation = Double.isNaN(optionalRotation) ? U.getValue(s, 4) : optionalRotation;
  } catch (RuntimeException ignored) {
   rotation = 0;
  }
  instanceScale[1] = VE.Map.name.equals(SL.MN.ghostCity) && listNumber == getTrackPartIndex(Models.cube.name()) && instanceSize == 10000 ? 1 + U.random(3.) : instanceScale[1];
  if (VE.Map.name.equals("Meteor Fields") && listNumber == getTrackPartIndex(Models.ramp.name())) {
   if (rotation == 0) {
    X[0] = U.randomPlusMinus(2500.);
    Z[0] = 5000 + U.random(20000.);
    Z[0] *= U.random() < .5 ? -1 : 1;
   } else {
    Z[0] = U.randomPlusMinus(2500.);
    X[0] = 5000 + U.random(20000.);
    X[0] *= U.random() < .5 ? -1 : 1;
   }
   rotation += U.random() < .5 ? 180 : 0;
  }
  if ((listNumber == getTrackPartIndex(Models.checkpoint.name()) || listNumber == getTrackPartIndex(Models.repair.name()))) {
   if (VE.Map.name.equals("Pyramid Paradise")) {
    forceTrackPartOutsideExistingParts(s, X, Y, Z, Models.pyramid.name());
   } else if (U.equals(VE.Map.name, "the Forest", "Volatile Sands")) {
    forceTrackPartOutsideExistingParts(s, X, Y, Z, (String[]) null);
   } else if (VE.Map.name.equals("Military Base")) {
    forceTrackPartOutsideExistingParts(s, X, Y, Z, Models.cube.name(), Models.ramptriangle.name());
   }
  }
  if (listNumber == getTrackPartIndex(Models.checkpoint.name())) {
   if (VE.Map.name.equals(SL.MN.highlands)) {
    if (U.random() < .5) {
     if (U.random() < .5) {
      X[0] += U.random() < .5 ? 100000 : -100000;
      Y[0] -= 25000;
     } else {
      Z[0] += U.random() < .5 ? 100000 : -100000;
      Y[0] -= 25000;
     }
    }
   } else if (VE.Map.name.equals(SL.MN.ghostCity)) {
    if (U.random() < .5) {
     rotation = 0;
     Z[0] = U.randomPlusMinus(150000.);
     long randomPosition = U.random(4);
     if (randomPosition == 0) {
      X[0] = -150000;
     } else if (randomPosition == 1 || randomPosition == 2) {
      X[0] = randomPosition == 1 ? -2000 : 2000;
      while (Math.abs(Z[0]) < 110000) {
       Z[0] = U.randomPlusMinus(150000.);
      }
     } else {
      X[0] = randomPosition == 3 ? 150000 : X[0];
     }
    } else {
     rotation = 90;
     X[0] = U.randomPlusMinus(150000.);
     long randomPosition = U.random(3);
     if (randomPosition == 0) {
      Z[0] = -150000;
     } else if (randomPosition == 1) {
      Z[0] = 0;
      while (Math.abs(X[0]) < 110000) {
       X[0] = U.randomPlusMinus(150000.);
      }
     }
     Z[0] = randomPosition == 2 ? 150000 : Z[0];
    }
   } else if (VE.Map.name.equals("the Machine is Out of Control")) {
    boolean inside = true;
    while (inside) {
     inside = Math.abs(X[0]) < 30000 && Math.abs(Z[0]) < 30000;
     for (TrackPart trackpart : trackParts) {
      inside = (trackpart.modelNumber == getTrackPartIndex(Models.pyramid.name()) || trackpart.modelNumber == getTrackPartIndex(Models.cube.name()) || trackpart.modelNumber == getTrackPartIndex(Models.ramptriangle.name())) &&
      Math.abs(X[0] - trackpart.X) <= trackpart.renderRadius && Math.abs(Z[0] - trackpart.Z) <= trackpart.renderRadius || inside;
     }
     if (inside) {
      X[0] = U.getValue(s, 1) + U.randomPlusMinus(randomX);
      Z[0] = U.getValue(s, 2) + U.randomPlusMinus(randomZ);
      Y[0] = U.getValue(s, 3) + U.randomPlusMinus(randomY);
     }
    }
   } else if (VE.Map.name.equals("DoomsDay")) {
    while (U.distance(X[0], 0, Z[0], 0) <= Volcano.radiusBottom) {
     X[0] = U.getValue(s, 1) + U.randomPlusMinus(randomX);
     Z[0] = U.getValue(s, 2) + U.randomPlusMinus(randomZ);
    }
   }
  }
  if (U.equals(U.getString(s, 0), Models.mound.name(), Models.pavedmound.name())) {
   try {
    trackParts.add(new TrackPart(X[0], Z[0], Y[0],
    U.getValue(s, 4) * instanceSize, U.getValue(s, 5) * instanceSize, U.getValue(s, 6) * instanceSize,
    false, U.getString(s, 0).contains(SL.paved), true));
   } catch (RuntimeException E) {
    trackParts.add(new TrackPart(X[0], Z[0], Y[0],
    U.getValue(s, 4) * U.random(instanceSize), U.getValue(s, 4) * U.random(instanceSize), U.getValue(s, 4) * U.random(instanceSize),
    false, U.getString(s, 0).contains(SL.paved), true));
   }
  } else {
   trackParts.add(new TrackPart(listNumber, X[0], Y[0], Z[0], rotation, instanceSize, instanceScale));
  }
  if (listNumber == getTrackPartIndex(Models.rainbow.name())) {
   trackParts.get(trackParts.size() - 1).rainbow = true;
  } else if (listNumber == getTrackPartIndex(Models.crescent.name())) {
   U.Nodes.remove(E.Sun.S);
  } else if (listNumber == getTrackPartIndex(Models.checkpoint.name())) {
   points.add(new Point());
   points.get(points.size() - 1).X = X[0];
   points.get(points.size() - 1).Y = Y[0];
   points.get(points.size() - 1).Z = Z[0];
   points.get(points.size() - 1).type = Point.Type.checkpoint;
   checkpoints.add(new Checkpoint());
   checkpoints.get(checkpoints.size() - 1).X = X[0];
   checkpoints.get(checkpoints.size() - 1).Y = Y[0];
   checkpoints.get(checkpoints.size() - 1).Z = Z[0];
   checkpoints.get(checkpoints.size() - 1).type = isSidewaysXZ(rotation) ? Checkpoint.Type.passX : Checkpoint.Type.passZ;
   checkpoints.get(checkpoints.size() - 1).location = points.size() - 1;
   trackParts.get(trackParts.size() - 1).checkpointNumber = checkpoints.size() - 1;
  } else if (s.contains(").")) {
   points.add(new Point());
   points.get(points.size() - 1).X = X[0];
   points.get(points.size() - 1).Y = Y[0];
   points.get(points.size() - 1).Z = Z[0];
   if (s.contains(")...")) {
    points.get(points.size() - 1).type = Point.Type.mustPassAbsolute;
   } else if (s.contains(")..")) {
    points.get(points.size() - 1).type = Point.Type.mustPassIfClosest;
   }
  }
  repairPointsExist = listNumber == getTrackPartIndex(Models.repair.name()) || repairPointsExist;
 }

 private static void forceTrackPartOutsideExistingParts(CharSequence source, double[] X, double[] Y, double[] Z, String... targets) {
  boolean inside = true;
  double tolerance = 1.05;//<-Not a sufficient distance away otherwise
  while (inside) {
   inside = false;
   for (TrackPart trackPart : trackParts) {
    inside = targets == null ? !trackPart.wraps && trackPart.mound != null && U.distance(X[0], trackPart.X, Z[0], trackPart.Z) <= trackPart.mound.getMajorRadius() * tolerance || inside : U.contains(trackPart.modelName, targets) &&
    Math.abs(Y[0] - trackPart.Y) <= trackPart.boundsY * 1.05 &&
    Math.abs(X[0] - trackPart.X) <= trackPart.boundsX * 1.05 &&
    Math.abs(Z[0] - trackPart.Z) <= trackPart.boundsZ * 1.05 ||//<-Aerial map parts check not foolproof
    inside;
   }
   if (inside) {
    X[0] = U.getValue(source, 1) + U.randomPlusMinus(randomX);
    Z[0] = U.getValue(source, 2) + U.randomPlusMinus(randomZ);
    Y[0] = U.getValue(source, 3) + U.randomPlusMinus(randomY);
   }
  }
 }

 public static int getTrackPartIndex(String s) {
  try {
   return Models.valueOf(s).ordinal();
  } catch (IllegalArgumentException E) {
   return -1;
  }
 }

 public static String getTrackPartName(int in) {
  return Models.values()[in].name();
 }

 public static void setVehicleMatchStartPlacement(Vehicle V) {
  V.Y = V.XY = V.YZ = 0;
  V.X = U.random(Math.min(50000., E.mapBounds.right)) + U.random(Math.max(-50000., E.mapBounds.left));
  V.Z = U.random(Math.min(50000., E.mapBounds.forward)) + U.random(Math.max(-50000., E.mapBounds.backward));
  V.XZ = !V.isFixed() && VE.Map.randomVehicleStartAngle ? U.randomPlusMinus(180.) : 0;//<-Fixed units ALWAYS face forward for less confusing placement
  if (VE.Map.name.equals("Vicious Versus V3") && VE.vehiclesInMatch > 1) {
   boolean green = V.index < VE.vehiclesInMatch >> 1;
   if (green) {
    V.Z = -10000;
    V.XZ = 0;
   } else {
    V.Z = 10000;
    V.XZ = 180;
   }
   if (VE.vehiclesInMatch < 3) {
    V.X = 0;
   } else {
    V.X = (V.index - (green ? 0 : (VE.vehiclesInMatch * .5))) * 2000;
    V.X -= 2000 * (VE.vehiclesInMatch * .5) * .5 - 1000;
   }
  } else if (VE.Map.name.equals("Moonlight")) {
   if (V.damageDealt < 100 && !V.isFixed()) {
    V.X *= V.X < 0 ? -1 : 1;
    V.Z *= V.Z < 0 ? -1 : 1;
   }
  } else if (VE.Map.name.equals(SL.MN.testOfDamage)) {
   if (V.damageDealt < 100 && !V.isFixed()) {
    V.X = U.random(E.mapBounds.right);
    V.Z = U.random(E.mapBounds.backward);
   }
  } else if (VE.Map.name.equals(SL.MN.vehicularFalls)) {
   V.Y -= 100000;
   if (!V.isFixed()) {
    V.Z = U.random(-10000.) + U.random(30000.);
    V.X = 0;
   }
  } else if (VE.Map.name.equals(SL.MN.highlands)) {
   V.X = U.randomPlusMinus(100000);
   V.Z = U.randomPlusMinus(100000);
   V.Y = -175000;
  } else if (VE.Map.name.equals(SL.MN.circleRaceXL)) {
   V.Z += 320000;
  } else if (VE.Map.name.equals(SL.MN.XYLand)) {
   V.X = V.isFixed() ? V.X : U.random(23000.) - U.random(25000.);
  } else if (VE.Map.name.equals(SL.MN.matrix2x3)) {
   if (!V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name()) && !V.isFixed()) {
    V.X = U.randomPlusMinus(14000.);
    V.Z = -U.random(31000.);
   }
  } else if (VE.Map.name.equals("Cold Fury")) {
   V.Y = -4000;
  } else if (VE.Map.name.equals(SL.MN.tunnelOfDoom)) {
   if (!V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name()) && !V.isFixed()) {
    V.X = U.randomPlusMinus(700.);
    V.Z = U.random(6000.) - U.random(10000.);
   }
  } else if (VE.Map.name.equals(SL.MN.everybodyEverything)) {
   V.X = U.random() < .5 ? -2000 : 2000;
   V.Z = U.randomPlusMinus(20000.);
  } else if (VE.Map.name.equals(SL.MN.theMaze)) {
   if (!V.isFixed()) {
    V.X = V.Z = 0;
   }
  } else if (VE.Map.name.equals("Volcanic Prophecy")) {
   V.X *= 2;
   V.Z *= 2;
  } else if (VE.Map.name.equals(SL.MN.speedway2000000)) {
   boolean random = U.random() < .5;
   V.XZ = random ? 180 : 0;
   V.Z += random ? 1000000 : -1000000;
  } else if (VE.Map.name.equals(SL.MN.ghostCity)) {
   V.X *= 4;
   V.Z *= 4;
   if (!V.isFixed()) {
    V.Y = -1000;
    V.X = U.random() < .5 ? 2000 : -2000;
   }
  } else if (VE.Map.name.equals("Open Ocean")) {
   V.X *= 4;
   V.Z *= 4;
   if (!V.isFixed()) {
    V.X = U.random() < .5 ? 2000 : -2000;
   }
  } else if (VE.Map.name.equals(SL.MN.summitOfEpic)) {
   V.X = !V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name()) && !V.isFixed() ? 0 : V.X;
   boolean random = U.random() < .5;
   V.XZ = random ? 180 : 0;
   V.Z = random ? 1050000 : -1050000;
   V.Z += U.randomPlusMinus(25000.);
  } else if (VE.Map.name.equals("Parallel Universe Portal")) {
   V.Z = 0;
  }
  if (E.gravity == 0) {
   if (VE.Map.name.equals("Outer Space V1")) {
    V.X = U.randomPlusMinus(500.);
    V.Z = U.random(2000.) - U.random(4000.);
    if (V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name())) {
     V.X = 0;
     V.Z = 100500;
    }
    if (V.isFixed()) {
     V.X = U.randomPlusMinus(50000.);
     V.Z = U.randomPlusMinus(50000.);
     V.Y = U.randomPlusMinus(50000.);
    }
   } else {
    V.Y = U.randomPlusMinus(50000.);
   }
   if (VE.Map.name.equals("Outer Space V3")) {
    V.Y = 0;
    double[] setX = {0}, setZ = {50000};
    U.rotate(setX, setZ, U.random(360.));
    V.X = setX[0];
    V.Z = setZ[0];
   }
  }
  if (VE.Map.name.equals("Black Hole")) {
   V.X = V.Y = V.Z = 0;
  }
  V.Y -= V.isFixed() ? 0 : V.clearanceY;
 }

 public static void runVehicleInteraction(Vehicle V, boolean replay) {
  if (!points.isEmpty()) {
   Point P = points.get(V.point);
   if (P.type == Point.Type.mustPassAbsolute && V.P.mode != Physics.Mode.fly) {
    V.point += U.distance(V, P) < 500 ? 1 : 0;
   } else if (P.type != Point.Type.checkpoint &&
   (U.distanceXZ(V, P) < 500 || (V.AI.skipStunts && P.type != Point.Type.mustPassIfClosest) || (!checkpoints.isEmpty() && !VE.Map.name.equals(SL.MN.devilsStairwell) && U.distance(V, checkpoints.get(V.checkpointsPassed)) <= U.distance(P, checkpoints.get(V.checkpointsPassed))))) {
    V.point++;
   }
  }
  if (!checkpoints.isEmpty() && !V.phantomEngaged) {
   double checkSize = VE.Map.name.equals(SL.MN.circleRaceXL) ? V.P.speed : 0;
   Checkpoint C = checkpoints.get(V.checkpointsPassed);
   if ((C.type == Checkpoint.Type.passZ || C.type == Checkpoint.Type.passAny) &&
   Math.abs(V.Z - C.Z) < (60 + checkSize) + Math.abs(V.P.speedZ) * VE.tick && Math.abs(V.X - C.X) < 700 && Math.abs((V.Y - C.Y) + 350) < 450) {
    V.checkpointsPassed++;
    V.point++;
    if (V.index == VE.vehiclePerspective) {
     if (!VE.Match.messageWait) {
      VE.Match.print = SL.Checkpoint;
      VE.Match.printTimer = 10;
     }
     if (VE.Options.headsUpDisplay) {
      VE.Sounds.checkpoint.play(0);
     }
    }
    VE.Match.scoreCheckpoint[V.index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : 1;
    if (V.checkpointsPassed >= checkpoints.size()) {
     VE.Match.scoreLap[V.index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : 1;
     V.checkpointsPassed = V.point = 0;
    }
   }
   if ((C.type == Checkpoint.Type.passX || C.type == Checkpoint.Type.passAny) &&
   Math.abs(V.X - C.X) < (60 + checkSize) + Math.abs(V.P.speedX) * VE.tick && Math.abs(V.Z - C.Z) < 700 && Math.abs((V.Y - C.Y) + 350) < 450) {
    V.checkpointsPassed++;
    V.point++;
    if (V.index == VE.vehiclePerspective) {
     if (!VE.Match.messageWait) {
      VE.Match.print = SL.Checkpoint;
      VE.Match.printTimer = 10;
     }
     if (VE.Options.headsUpDisplay) {
      VE.Sounds.checkpoint.play(0);
     }
    }
    VE.Match.scoreCheckpoint[V.index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : 1;
    if (V.checkpointsPassed >= checkpoints.size()) {
     VE.Match.scoreLap[V.index < VE.vehiclesInMatch >> 1 ? 0 : 1] += replay ? 0 : 1;
     V.checkpointsPassed = V.point = 0;
    }
   }
   V.point = V.checkpointsPassed > 0 ? (int) U.clamp(checkpoints.get(V.checkpointsPassed - 1).location + 1, V.point, checkpoints.get(V.checkpointsPassed).location) : V.point;
   if (V.index == VE.vehiclePerspective) {
    currentCheckpoint = V.checkpointsPassed;
    lapCheckpoint = V.checkpointsPassed >= checkpoints.size() - 1;
   }
  }
  V.point = V.point >= points.size() || V.point < 0 ? 0 : V.point;
 }

 public static void runVehicleRepairPointInteraction(Vehicle V, boolean gamePlay) {
  if (repairPointsExist && V.repairSpheres.get(U.random(V.repairSpheres.size())).stage <= 0 && !V.phantomEngaged) {
   for (TrackPart part : trackParts) {
    if (part.isRepairPoint) {
     boolean sideways = isSidewaysXZ(part.XZ);
     if (U.distance(sideways ? V.Z : V.X, sideways ? part.Z : part.X, V.Y, part.Y) <= 500 && Math.abs(sideways ? V.X - part.X : V.Z - part.Z) <= 200 + Math.abs(sideways ? V.P.speedX : V.P.speedZ) * VE.tick) {
      V.repair(gamePlay);
     }
    }
   }
  }
 }

 public static boolean isSidewaysXZ(double angleXZ) {
  return Math.abs(U.cos(angleXZ)) < U.sin45;
 }

 public static void reset() {
  trackParts.clear();
  points.clear();
  checkpoints.clear();
  repairPointsExist = false;
 }
}
