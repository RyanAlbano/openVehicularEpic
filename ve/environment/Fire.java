package ve.environment;

import javafx.scene.PointLight;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import ve.effects.Effects;
import ve.instances.Core;
import ve.instances.CoreAdvanced;
import ve.utilities.D;
import ve.utilities.Nodes;
import ve.utilities.Phong;
import ve.utilities.U;
import ve.utilities.sound.Controlled;
import ve.utilities.sound.Sounds;
import ve.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.Collection;

public enum Fire {
 ;
 public static final Collection<Instance> instances = new ArrayList<>();

 public static void load(String s) {
  if (s.startsWith("fire(")) {
   instances.add(new Instance(s));
  }
 }

 static void run(boolean update) {
  for (var fire : instances) {
   fire.run(update);
  }
 }

 public static void vehicleInteract(Vehicle V) {
  for (var fire : instances) {
   double distance = U.distance(V, fire);
   if (distance < V.collisionRadius + fire.absoluteRadius) {
    V.addDamage(10 * U.tick);
    if (distance * 2 < V.collisionRadius + fire.absoluteRadius) {
     V.addDamage(10 * U.tick);
    }
    V.deformParts();
   }
  }
 }

 public static class Instance extends Core {

  private final MeshView pit;
  private final PointLight PL;
  private final Collection<Flame> flames = new ArrayList<>();
  private final Controlled sound;

  Instance(String s) {
   X = U.getValue(s, 0);
   Z = U.getValue(s, 1);
   Y = U.getValue(s, 2);
   absoluteRadius = U.getValue(s, 3);
   PL = s.contains(D.light) ? new PointLight() : null;
   double flameSize = absoluteRadius * .25;
   for (int n = 0; n < 50; n++) {
    flames.add(new Flame(this, flameSize));
   }
   sound = s.contains("hear") ? new Controlled(D.fire) : null;
   TriangleMesh TM = new TriangleMesh();
   double fireSize = absoluteRadius;
   TM.getPoints().setAll(
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize),
   (float) U.randomPlusMinus(fireSize), 0, (float) U.randomPlusMinus(fireSize));
   TM.getTexCoords().setAll(0, 0);
   TM.getFaces().setAll(0, 0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8, 0, 9, 0, 10, 0, 11, 0, 12, 0, 13, 0, 14, 0, 15, 0, 16, 0, 17, 0, 18, 0, 19, 0, 20, 0, 21, 0, 22, 0, 23, 0);
   pit = new MeshView(TM);
   PhongMaterial PM = new PhongMaterial();
   Phong.setDiffuseRGB(PM, 0);
   Phong.setSpecularRGB(PM, 0);
   U.setMaterialSecurely(pit, PM);
   Nodes.add(pit);
  }

  void run(boolean update) {
   for (var flame : flames) {
    flame.X += flame.speedX * U.tick + (Wind.speedX * U.tick);
    flame.Z += flame.speedZ * U.tick + (Wind.speedZ * U.tick);
    flame.Y -= .1 * absoluteRadius * U.tick;
    if (Math.abs(Y - flame.Y) > absoluteRadius + U.random(absoluteRadius) || Math.abs(X - flame.X) > absoluteRadius + U.random(absoluteRadius) || Math.abs(Z - flame.Z) > absoluteRadius + U.random(absoluteRadius)) {
     flame.X = X + U.randomPlusMinus(absoluteRadius);
     flame.Z = Z + U.randomPlusMinus(absoluteRadius);
     flame.Y = Y;
     flame.speedX = U.randomPlusMinus(.2 * absoluteRadius);
     flame.speedZ = U.randomPlusMinus(.2 * absoluteRadius);
    }
   }
   if (U.getDepth(this) > -absoluteRadius) {
    U.setTranslate(pit, this);
    pit.setVisible(true);
    for (var flame : flames) {
     U.randomRotate(flame.MV);
     ((PhongMaterial) flame.MV.getMaterial()).setSelfIlluminationMap(Effects.fireLight());
     U.setTranslate(flame.MV, flame);
     flame.MV.setVisible(true);
    }
   } else {
    pit.setVisible(false);
    for (var flame : flames) {
     flame.MV.setVisible(false);
    }
   }
   double fireToCameraDistance = U.distance(this);
   if (PL != null) {
    if (fireToCameraDistance < E.viewableMapDistance) {
     Nodes.setLightRGB(PL, 1, .5 + U.random(.4), U.random(.25));
     U.setTranslate(PL, this);
     Nodes.addPointLight(PL);
    } else {
     Nodes.removePointLight(PL);
    }
   }
   if (sound != null) {
    if (!Sounds.mute && update) {
     sound.loop(Math.sqrt(fireToCameraDistance) * .16);
    } else {
     sound.stop();
    }
   }
  }

  public void closeSound() {
   if (sound != null) sound.close();
  }

  static class Flame extends CoreAdvanced {

   final MeshView MV;

   Flame(Fire.Instance FI, double fireSize) {
    TriangleMesh TM = new TriangleMesh();
    TM.getPoints().setAll(
    (float) U.randomPlusMinus(fireSize), (float) U.randomPlusMinus(fireSize), (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), (float) U.randomPlusMinus(fireSize), (float) U.randomPlusMinus(fireSize),
    (float) U.randomPlusMinus(fireSize), (float) U.randomPlusMinus(fireSize), (float) U.randomPlusMinus(fireSize));
    TM.getTexCoords().setAll(0, 0);
    TM.getFaces().setAll(0, 0, 1, 0, 2, 0);
    MV = new MeshView(TM);
    X = FI.X;
    Y = Double.POSITIVE_INFINITY;
    Z = FI.Z;
    MV.setCullFace(CullFace.NONE);
    PhongMaterial PM = new PhongMaterial();
    Phong.setDiffuseRGB(PM, 0);
    Phong.setSpecularRGB(PM, 0);
    U.setMaterialSecurely(MV, PM);
    Nodes.add(MV);
   }
  }
 }
}
