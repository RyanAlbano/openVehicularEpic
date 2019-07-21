package ve.vehicles;

import java.util.*;

import javafx.scene.shape.MeshView;
import ve.utilities.U;

public class Special {

 public String type = "";
 int currentShot;
 double randomPosition, randomAngle, timer, spitStage, speed, diameter, damageDealt, pushPower, length, width;
 boolean homing, hasThrust, ricochets, useSmallHits;
 long AIAimPrecision = Long.MAX_VALUE;
 final List<Shot> shots = new ArrayList<>();
 final List<Port> ports = new ArrayList<>();
 final List<MeshView> spits = new ArrayList<>();

 public void time() {
  timer = type.startsWith("gun") ? 8 : type.contains("machinegun") ? 1 : type.startsWith("minigun") ? .3 : type.startsWith("shotgun") ? 35 : type.startsWith("shell") ? 30 : type.contains("missile") || U.startsWith(type, "railgun", "powershell", "mine") ? 200 : type.startsWith("blaster") ? 10 : U.startsWith(type, "heavyblaster", "bomb") ? 100 : type.startsWith("forcefield") ? 20 : type.startsWith("thewrath") ? 1000 : 0;
 }
}
