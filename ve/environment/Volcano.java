package ve.environment;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import ve.effects.Effects;
import ve.instances.Core;
import ve.instances.CoreAdvanced;
import ve.utilities.*;
import ve.utilities.sound.FireAndForget;
import ve.utilities.sound.Sounds;
import ve.vehicles.Physics;
import ve.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.List;

public enum Volcano {
 ;
 public static final Core C = new Core();
 public static boolean exists;
 public static final double radiusBottom = 53000, radiusTop = 3000, height = 50000;
 private static double eruptionStage;
 public static double cameraShake;
 private static final MeshView MV;
 public static final List<Rock> rocks = new ArrayList<>();
 private static FireAndForget sound;

 static {
  TriangleMesh TM = new TriangleMesh();
  TM.getPoints().setAll(0, 0, (float) radiusBottom,
  9203.3534163473084891409812187737f, 0, 52194.810909647027146437380303245f,
  18127.06759626044285133727957816f, 0, 49803.708901653144354867791698211f,
  26500, 0, 45899.346400575248278477328049906f,
  34067.743313386584295100100725085f, 0, 40600.355485305835865726810479437f,
  40600.355485305835865726810479437f, 0, 34067.743313386584295100100725085f,
  45899.346400575248278477328049906f, 0, 26500,
  49803.708901653144354867791698211f, 0, 18127.06759626044285133727957816f,
  52194.810909647027146437380303245f, 0, 9203.3534163473084891409812187737f,
  (float) radiusBottom, 0, 0,
  52194.810909647027146437380303245f, 0, -9203.3534163473084891409812187737f,
  49803.708901653144354867791698211f, 0, -18127.06759626044285133727957816f,
  45899.346400575248278477328049906f, 0, -26500,
  40600.355485305835865726810479437f, 0, -34067.743313386584295100100725085f,
  34067.743313386584295100100725085f, 0, -40600.355485305835865726810479437f,
  26500, 0, -45899.346400575248278477328049906f,
  18127.06759626044285133727957816f, 0, -49803.708901653144354867791698211f,
  9203.3534163473084891409812187737f, 0, -52194.810909647027146437380303245f,
  0, 0, -(float) radiusBottom,
  -9203.3534163473084891409812187737f, 0, -52194.810909647027146437380303245f,
  -18127.06759626044285133727957816f, 0, -49803.708901653144354867791698211f,
  -26500, 0, -45899.346400575248278477328049906f,
  -34067.743313386584295100100725085f, 0, -40600.355485305835865726810479437f,
  -40600.355485305835865726810479437f, 0, -34067.743313386584295100100725085f,
  -45899.346400575248278477328049906f, 0, -26500,
  -49803.708901653144354867791698211f, 0, -18127.06759626044285133727957816f,
  -52194.810909647027146437380303245f, 0, -9203.3534163473084891409812187737f,
  -(float) radiusBottom, 0, 0,
  -52194.810909647027146437380303245f, 0, 9203.3534163473084891409812187737f,
  -49803.708901653144354867791698211f, 0, 18127.06759626044285133727957816f,
  -45899.346400575248278477328049906f, 0, 26500,
  -40600.355485305835865726810479437f, 0, 34067.743313386584295100100725085f,
  -34067.743313386584295100100725085f, 0, 40600.355485305835865726810479437f,
  -26500, 0, 45899.346400575248278477328049906f,
  -18127.06759626044285133727957816f, 0, 49803.708901653144354867791698211f,
  -9203.3534163473084891409812187737f, 0, 52194.810909647027146437380303245f,
  0, -(float) height, (float) radiusTop,
  520.94453300079104655514988030794f, -(float) height, 2954.4232590366241781002290737686f,
  1026.0604299770061991322988440468f, -(float) height, 2819.0778623577251521623278319742f,
  1500, -(float) height, 2598.0762113533159402911695122588f,
  1928.3628290596179789679302297218f, -(float) height, 2298.1333293569341056071779516663f,
  2298.1333293569341056071779516663f, -(float) height, 1928.3628290596179789679302297218f,
  2598.0762113533159402911695122588f, -(float) height, 1500,
  2819.0778623577251521623278319742f, -(float) height, 1026.0604299770061991322988440468f,
  2954.4232590366241781002290737686f, -(float) height, 520.94453300079104655514988030794f,
  (float) radiusTop, -(float) height, 0,
  2954.4232590366241781002290737686f, -(float) height, -520.94453300079104655514988030794f,
  2819.0778623577251521623278319742f, -(float) height, -1026.0604299770061991322988440468f,
  2598.0762113533159402911695122588f, -(float) height, -1500,
  2298.1333293569341056071779516663f, -(float) height, -1928.3628290596179789679302297218f,
  1928.3628290596179789679302297218f, -(float) height, -2298.1333293569341056071779516663f,
  1500, -(float) height, -2598.0762113533159402911695122588f,
  1026.0604299770061991322988440468f, -(float) height, -2819.0778623577251521623278319742f,
  520.94453300079104655514988030794f, -(float) height, -2954.4232590366241781002290737686f,
  0, -(float) height, -(float) radiusTop,
  -520.94453300079104655514988030794f, -(float) height, -2954.4232590366241781002290737686f,
  -1026.0604299770061991322988440468f, -(float) height, -2819.0778623577251521623278319742f,
  -1500, -(float) height, -2598.0762113533159402911695122588f,
  -1928.3628290596179789679302297218f, -(float) height, -2298.1333293569341056071779516663f,
  -2298.1333293569341056071779516663f, -(float) height, -1928.3628290596179789679302297218f,
  -2598.0762113533159402911695122588f, -(float) height, -1500,
  -2819.0778623577251521623278319742f, -(float) height, -1026.0604299770061991322988440468f,
  -2954.4232590366241781002290737686f, -(float) height, -520.94453300079104655514988030794f,
  -(float) radiusTop, -(float) height, 0,
  -2954.4232590366241781002290737686f, -(float) height, 520.94453300079104655514988030794f,
  -2819.0778623577251521623278319742f, -(float) height, 1026.0604299770061991322988440468f,
  -2598.0762113533159402911695122588f, -(float) height, 1500,
  -2298.1333293569341056071779516663f, -(float) height, 1928.3628290596179789679302297218f,
  -1928.3628290596179789679302297218f, -(float) height, 2298.1333293569341056071779516663f,
  -1500, -(float) height, 2598.0762113533159402911695122588f,
  -1026.0604299770061991322988440468f, -(float) height, 2819.0778623577251521623278319742f,
  -520.94453300079104655514988030794f, -(float) height, 2954.4232590366241781002290737686f);
  if (U.random() < .5) {
   TM.getTexCoords().setAll(0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0);
  } else {
   TM.getTexCoords().setAll(0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1);
  }
  TM.getFaces().addAll(
  0, 0, 1, 1, 37, 37, 1, 1, 2, 2, 38, 38, 2, 2, 3, 3, 39, 39, 3, 3, 4, 4, 40, 40, 4, 4, 5, 5, 41, 41, 5, 5, 6, 6, 42, 42, 6, 6, 7, 7, 43, 43, 7, 7, 8, 8, 44, 44, 8, 8, 9, 9, 45, 45, 9, 9, 10, 10, 46, 46, 10, 10, 11, 11, 47, 47, 11, 11, 12, 12, 48, 48, 12, 12, 13, 13, 49, 49, 13, 13, 14, 14, 50, 50, 14, 14, 15, 15, 51, 51, 15, 15, 16, 16, 52, 52, 16, 16, 17, 17, 53, 53, 17, 17, 18, 18, 54, 54, 18, 18, 19, 19, 55, 55, 19, 19, 20, 20, 56, 56, 20, 20, 21, 21, 57, 57, 21, 21, 22, 22, 58, 58, 22, 22, 23, 23, 59, 59, 23, 23, 24, 24, 60, 60, 24, 24, 25, 25, 61, 61, 25, 25, 26, 26, 62, 62, 26, 26, 27, 27, 63, 63, 27, 27, 28, 28, 64, 64, 28, 28, 29, 29, 65, 65, 29, 29, 30, 30, 66, 66, 30, 30, 31, 31, 67, 67, 31, 31, 32, 32, 68, 68, 32, 32, 33, 33, 69, 69, 33, 33, 34, 34, 70, 70, 34, 34, 35, 35, 71, 71, 35, 35, 0, 0, 36, 36,
  0, 0, 36, 36, 37, 37, 1, 1, 37, 37, 38, 38, 2, 2, 38, 38, 39, 39, 3, 3, 39, 39, 40, 40, 4, 4, 40, 40, 41, 41, 5, 5, 41, 41, 42, 42, 6, 6, 42, 42, 43, 43, 7, 7, 43, 43, 44, 44, 8, 8, 44, 44, 45, 45, 9, 9, 45, 45, 46, 46, 10, 10, 46, 46, 47, 47, 11, 11, 47, 47, 48, 48, 12, 12, 48, 48, 49, 49, 13, 13, 49, 49, 50, 50, 14, 14, 50, 50, 51, 51, 15, 15, 51, 51, 52, 52, 16, 16, 52, 52, 53, 53, 17, 17, 53, 53, 54, 54, 18, 18, 54, 54, 55, 55, 19, 19, 55, 55, 56, 56, 20, 20, 56, 56, 57, 57, 21, 21, 57, 57, 58, 58, 22, 22, 58, 58, 59, 59, 23, 23, 59, 59, 60, 60, 24, 24, 60, 60, 61, 61, 25, 25, 61, 61, 62, 62, 26, 26, 62, 62, 63, 63, 27, 27, 63, 63, 64, 64, 28, 28, 64, 64, 65, 65, 29, 29, 65, 65, 66, 66, 30, 30, 66, 66, 67, 67, 31, 31, 67, 67, 68, 68, 32, 32, 68, 68, 69, 69, 33, 33, 69, 69, 70, 70, 34, 34, 70, 70, 71, 71, 35, 35, 71, 71, 36, 36);
  MV = new MeshView(TM);
  MV.setCullFace(CullFace.NONE);
 }

 public static void load(String s) {
  if (s.startsWith("volcano(")) {
   PhongMaterial volcanoPM = new PhongMaterial();
   Phong.setDiffuseRGB(volcanoPM, Ground.RGB);
   U.setMaterialSecurely(MV, volcanoPM);
   C.X = U.getValue(s, 0);
   C.Z = U.getValue(s, 1);
   Nodes.add(MV);
   exists = true;
   if (s.contains("active")) {
    boolean isLava = true;
    for (int n = 0; n < 200; n++) {
     rocks.add(new Rock(1000 + (n * 10), 4 + U.random(2)));
     PhongMaterial PM = new PhongMaterial();
     if (isLava) {
      rocks.get(n).isLava = true;
      Phong.setDiffuseRGB(PM, 0);
      Phong.setSpecularRGB(PM, 0);
     } else {
      PM.setDiffuseMap(Images.get(D.rock));
      PM.setSpecularMap(Images.get(D.rock));
      PM.setBumpMap(Images.getNormalMap(D.rock));
     }
     U.setMaterialSecurely(rocks.get(n).S, PM);
     isLava = !isLava;
     Nodes.add(rocks.get(n).S);
    }
    sound = new FireAndForget("volcano");
   }
  }
 }

 static void run(boolean updateIfMatchBegan) {
  if (exists) {
   U.setTranslate(MV, C);//<-Core's Y should be '0'
   if (!rocks.isEmpty()) {//<-If active volcano
    if (eruptionStage > 0) {
     long rocksLanded = 0;
     for (Rock rock : rocks) {
      if (rock.groundHit) {
       rock.Y = -U.random(46000.);
       rocksLanded++;
      } else {
       if (updateIfMatchBegan) {
        rock.X += rock.speedX;
        rock.Y += rock.speedY;
        rock.Z += rock.speedZ;
        rock.speedY += E.gravity * U.tick;
       }
       if (rock.Y > rock.S.getRadius()) {
        rock.groundHit = true;
        rock.X = C.X;
        rock.Z = C.Z;
       }
      }
     }
     eruptionStage = rocksLanded >= rocks.size() ? 0 : eruptionStage + U.tick;
    } else if (updateIfMatchBegan) {
     for (Rock rock : rocks) {
      rock.deploy();
     }
     eruptionStage = 1;
     setCameraShake();
     sound.play(Math.sqrt(U.distance(Camera.C.X, C.X, Camera.C.Y, -height, Camera.C.Z, C.Z)) * Sounds.standardGain(Sounds.gainMultiples.volcano));
    }
    for (Rock rock : rocks) {
     rock.run();
    }
    cameraShake -= cameraShake > 0 ? U.tick : 0;
   }
  }
 }

 public static void runVehicleInteract(Vehicle V) {
  if (exists) {
   V.P.onVolcano = false;
   double volcanoDistance = U.distanceXZ(V, C);
   if (volcanoDistance < radiusBottom && volcanoDistance > radiusTop && V.Y > -radiusBottom + volcanoDistance - V.P.clearance) {
    V.P.onVolcano = true;
    V.terrainRGB = Ground.RGB;
    V.P.mode = Physics.Mode.driveSolid;
    V.P.terrainProperties = Terrain.vehicleDefaultTerrain;
    double halfRadiusBottom = radiusBottom * .5,
    baseAngle = V.P.flipped() ? 225 : 45, vehicleVolcanoXZ = V.XZ,
    volcanoPlaneY = Math.max(radiusTop * .5, halfRadiusBottom - (halfRadiusBottom * (Math.abs(V.Y) / height)));
    vehicleVolcanoXZ += V.Z < C.Z && Math.abs(V.X - C.X) < volcanoPlaneY ? 180 : V.X >= C.X + volcanoPlaneY ? 90 : V.X <= C.X - volcanoPlaneY ? -90 : 0;
    V.XY += (baseAngle * U.sin(vehicleVolcanoXZ) - V.XY) * Physics.valueAdjustSmoothing * U.tick;
    V.YZ += (-baseAngle * U.cos(vehicleVolcanoXZ) - V.YZ) * Physics.valueAdjustSmoothing * U.tick;
    V.Y = -radiusBottom + volcanoDistance - V.P.clearance;//<-'onVolcano' is already confirmed, so there should be no reason not to hard-set this
    V.speedY = Math.min(V.speedY, 0);
   }
  }
 }

 public static void rockVehicleInteract(Vehicle V) {
  for (Rock rock : rocks) {
   double vehicleVolcanoRockDistance = U.distance(V, rock);
   if (vehicleVolcanoRockDistance < (V.collisionRadius + rock.S.getRadius()) * 1.5) {
    V.addDamage(V.durability * .5 + (vehicleVolcanoRockDistance < V.collisionRadius + rock.S.getRadius() ? V.durability : 0));
    V.deformParts();
    V.throwChips(U.netValue(rock.speedX, rock.speedY, rock.speedZ), true);
    V.VA.crashDestroy.play(Double.NaN, V.VA.distanceVehicleToCamera);
   }
  }
 }

 private static void setCameraShake() {
  cameraShake = Math.max(cameraShake, Camera.shakeIntensity.volcano);
 }

 static class Rock extends CoreAdvanced {

  final Sphere S;
  private final double[] rotation = new double[3];
  boolean groundHit, isLava;

  Rock(double radius, int divisions) {
   S = new Sphere(radius, divisions);
  }

  void deploy() {
   X = C.X;
   Z = C.Z;
   Y = -50000;
   speedX = U.randomPlusMinus(1000.);
   speedZ = U.randomPlusMinus(1000.);
   speedY = -U.random(1000.);
   rotation[0] = U.randomPlusMinus(45.);
   rotation[1] = U.randomPlusMinus(45.);
   groundHit = false;
  }

  void run() {
   if (U.render(this, -S.getRadius(), false, false)) {
    U.rotate(S, rotation[0] * eruptionStage, rotation[1] * eruptionStage);
    if (isLava) {
     ((PhongMaterial) S.getMaterial()).setSelfIlluminationMap(Effects.fireLight());
    }
    U.setTranslate(S, this);
    S.setVisible(true);
   } else {
    S.setVisible(false);
   }
  }
 }

 static void reset() {
  rocks.clear();
  cameraShake = 0;
  exists = false;
 }

 public static void closeSound() {
  if (sound != null) sound.close();
 }
}
