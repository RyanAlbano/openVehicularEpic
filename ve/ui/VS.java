package ve.ui;

import javafx.scene.paint.PhongMaterial;
import ve.environment.Ground;
import ve.instances.I;
import ve.ui.converter.MainFrame;
import ve.ui.options.Units;
import ve.utilities.*;
import ve.vehicles.Vehicle;
import ve.vehicles.specials.Special;

public enum VS {//VehicleSelect
 ;
 public static int index;
 public static final int[] chosen = new int[I.maxPlayers];
 static boolean allSame;
 public static boolean showModel;

 public static void run(boolean gamePlay) {
  int n;
  U.font(.03);
  if (Network.waiting) {
   U.fillRGB(1);
   if (I.vehiclesInMatch < 3) {
    U.text(UI.Please_Wait_For_ + UI.playerNames[Network.mode == Network.Mode.HOST ? 1 : 0] + " to Select Vehicle..", .5, .5);
   } else {
    U.text("..Please Wait for all other players to Select their Vehicle..", .5, .5);
   }
   if (Network.mode == Network.Mode.HOST) {
    if (U.timerBase20 <= 0) {
     for (var PW : Network.out) {
      PW.println(D.Vehicle + "0" + "(" + I.vehicles.get(0).name);
     }
    }
    for (n = I.vehiclesInMatch; --n > 0; ) {
     String s = Network.readIn(n - 1);
     if (s.startsWith(D.CANCEL)) {
      UI.escapeToLast(false);
     } else if (s.startsWith(D.Vehicle + "(")) {
      chosen[n] = I.getVehicleIndex(U.getString(s, 0));
      if (I.vehiclesInMatch > 2) {
       for (var out : Network.out) {
        out.println(D.Vehicle + n + "(" + U.getString(s, 0));
       }
      }
      Network.ready[n] = true;
      System.out.println(UI.playerNames[n] + " has selected Vehicle");
     }
    }
   } else {
    if (U.timerBase20 <= 0) {
     Network.out.get(0).println(D.Vehicle + "(" + I.vehicles.get(0).name);
    }
    String s = Network.readIn(0);
    if (s.startsWith(D.CANCEL)) {
     UI.escapeToLast(false);
    } else {
     for (n = I.vehiclesInMatch; --n >= 0; ) {
      if (n != I.userPlayerIndex) {
       if (s.startsWith(D.Vehicle + n + "(")) {
        chosen[n] = I.getVehicleIndex(U.getString(s, 0));
        Network.ready[n] = true;
       }
      }
     }
    }
   }
   long whoIsReady = 0;
   for (n = I.vehiclesInMatch; --n >= 0; ) {
    whoIsReady = Network.ready[n] ? ++whoIsReady : whoIsReady;
   }
   if (whoIsReady >= I.vehiclesInMatch) {
    UI.status = UI.Status.mapJump;
    Network.waiting = false;
   }
  } else {
   if (UI.page == 0) {
    Nodes.reset();
    I.vehicles.clear();
    UI.scene3D.setFill(U.getColor(0));
    Camera.C.X = Camera.C.Z = Camera.YZ = Camera.XY = 0;
    Camera.C.Y = -250;
    Camera.XZ = 180;
    Camera.rotateXY.setAngle(0);
    Camera.setAngleTable();
    U.setTranslate(Ground.C, 0, 0, 0);
    Phong.setDiffuseRGB((PhongMaterial) Ground.C.getMaterial(), .1);
    I.addVehicleModel(chosen[index], showModel);
    allSame = false;
    UI.page = 1;
   }
   allSame = index <= 0 && Network.mode == Network.Mode.OFF && allSame;
   Vehicle V = I.vehicles.get(0);
   U.fillRGB(1);
   U.text("SELECT " + (Viewer.inUse ? "VEHICLE TO VIEW/EDIT" : index > 0 ? "PLAYER #" + index : "VEHICLE"), .075);
   V.inDriverView = false;
   V.runRender(gamePlay);
   V.Z = -1000;
   V.XZ += (.5 - Mouse.X) * 20 * U.tick;
   if (V.spinner != null) {
    V.spinner.XZ = -V.XZ * 2;
   }
   if (V.isFixed()) {
    V.Y = -V.turretBaseY;
    V.YZ -= (.5 - Mouse.Y) * 20 * U.tick;
    V.YZ = U.clamp(-90, V.YZ, 90);
   } else {
    V.Y = -V.clearanceY;
   }
   U.font(.0125);
   if (I.vehiclesInMatch > 2) {
    if (index < I.vehiclesInMatch >> 1) {
     U.fillRGB(0, 1, 0);
     U.text(Network.mode == Network.Mode.OFF ? "(GREEN TEAM)" : "You're on the GREEN TEAM", .1);
    } else {
     U.fillRGB(1, 0, 0);
     U.text(Network.mode == Network.Mode.OFF ? "(RED TEAM)" : "You're on the RED TEAM", .1);
    }
   }
   U.fillRGB(1);
   U.font(.02);
   //U.textR("" + V., .9, .6);
   U.text(UI._LAST, .125, .5);
   U.text(UI.NEXT_, .875, .5);
   U.font(.01);
   if (showModel) {
    U.text(D.Meshes_ + V.parts.size(), .8);
    U.text(D.Vertices_ + V.vertexQuantity, .825);
   }
   U.text("Vehicles [" + (showModel ? "SHOW (can be slow--not recommended)" : UI.HIDE) + "]", .875);
   U.text(UI.CONTINUE + (allSame ? " (with all players as " + V.name + ")" : ""), .9);
   boolean singleSelection = !Viewer.inUse && (I.vehiclesInMatch < 2 || Network.mode != Network.Mode.OFF);
   if (singleSelection) {
    UI.selected = Math.min(1, UI.selected);
   } else {
    U.text(Viewer.inUse ? "START .OBJ-to-V.E. CONVERTER" : "SELECT NEXT VEHICLE", .925);
   }
   if (U.yinYang) {
    U.strokeRGB(1);
    U.drawRectangle(.5, UI.selected == 0 ? .875 : UI.selected == 1 ? .9 : .925, UI.width, UI.selectionHeight);
   }
   U.fillRGB(1);
   if (UI.selected == 1 && !singleSelection && !Viewer.inUse) {
    U.text(allSame ? "" : "(Remaining players are picked randomly)", .95);
   }
   runDrawProperties(V);
   U.font(.01);
   if (UI.selectionReady()) {
    if (Keys.up || Keys.down) {
     if (Keys.down) {
      UI.selected = ++UI.selected > (singleSelection ? 1 : 2) ? 0 : UI.selected;
     } else {
      UI.selected = --UI.selected < 0 ? (singleSelection ? 1 : 2) : UI.selected;
     }
     UI.sound.play(0, 0);
     Keys.inUse = true;
    }
    if (Keys.right) {
     I.removeVehicleModel();
     if (++chosen[index] >= I.vehicleModels.size()) {
      chosen[index] = 0;
     }
     if (index == I.userPlayerIndex) {
      I.userRandomRGB = U.getColor(U.random(), U.random(), U.random());
     }
     I.addVehicleModel(chosen[index], showModel);
     UI.sound.play(0, 0);
    }
    if (Keys.left) {
     I.removeVehicleModel();
     if (--chosen[index] < 0) {
      chosen[index] = I.vehicleModels.size() - 1;
     }
     if (index == I.userPlayerIndex) {
      I.userRandomRGB = U.getColor(U.random(), U.random(), U.random());
     }
     I.addVehicleModel(chosen[index], showModel);
     UI.sound.play(0, 0);
    }
    if (Keys.space || Keys.enter) {
     if (Viewer.inUse && UI.selected == 2) {
      new MainFrame().setVisible(true);
     } else {
      I.removeVehicleModel();
      if (UI.selected < 1) {
       showModel = !showModel;
       I.addVehicleModel(chosen[index], showModel);
      } else {
       if (Network.mode == Network.Mode.OFF) {
        index++;
        if (index < I.vehiclesInMatch) {
         I.addVehicleModel(chosen[index], showModel);
        }
       }
       if (index > (I.vehiclesInMatch * (Tournament.stage > 0 ? .5 : 1)) - 1 || UI.selected == 1) {
        if (Viewer.inUse) {
         UI.status = UI.Status.vehicleViewer;
         UI.page = 0;
        } else if (Network.mode != Network.Mode.OFF) {
         Network.ready[I.userPlayerIndex] = Network.waiting = true;
        } else {
         UI.status = UI.Status.mapJump;
         if (allSame) {
          for (n = chosen.length; --n > 0; ) {
           chosen[n] = chosen[0];
          }
         } else for (n = index; n < I.vehiclesInMatch; n++) {
          chosen[n] = U.random(I.vehicleModels.size());
         }
        }
       }
      }
     }
     UI.sound.play(1, 0);
     Keys.space = Keys.enter = false;
    }
   }
  }
  if (Keys.escape) {
   UI.escapeToLast(true);
  }
  if (!Keys.inUse) {
   UI.selected =
   Math.abs(.85 - Mouse.Y) < UI.clickRangeY ? 0 :
   Math.abs(.875 - Mouse.Y) < UI.clickRangeY ? 1 :
   Math.abs(.9 - Mouse.Y) < UI.clickRangeY ? 2 :
   UI.selected;
  }
  U.rotate(Camera.PC, Camera.YZ, -Camera.XZ);
  UI.gameFPS = Double.POSITIVE_INFINITY;
 }

 private static void runDrawProperties(Vehicle V) {
  if (showModel) {
   U.font(.02);
   U.text(V.name, .15);
  } else {
   U.font(.03);
   U.text(V.name, .5);
  }
  U.font(.015);
  U.text(UI.Made_by_ + Vehicle.vehicleMaker, .2);
  double lineLL = .1125, lineLR = .125, lineRL = 1 - lineLR, lineRR = 1 - lineLL,
  Y0 = .725, Y1 = .75, Y2 = .775, Y3 = .8, Y4 = .825, Y5 = .85;
  U.font(.00875);
  U.textR("Type: ", lineLL, Y0);
  boolean supportInfrastructure = V.type == Vehicle.Type.supportInfrastructure;
  String type =
  V.type == Vehicle.Type.aircraft ? "Aircraft (Flying)" :
  V.type == Vehicle.Type.turret ? "Turret (Fixed)" :
  supportInfrastructure ? "Support Infrastructure (Fixed)"
  : "Vehicle (Grounded)";
  U.textL(type, lineLR, Y0);
  if (!supportInfrastructure) {
   if (V.type != Vehicle.Type.turret) {
    U.textR("Top Speed:", lineLL, Y1);
    boolean hasWrath = false;
    for (var special : V.specials) {
     if (special.type == Special.Type.thewrath) {
      hasWrath = true;
      break;
     }
    }
    String topSpeed = V.topSpeeds[1] >= Long.MAX_VALUE ? D.None :
    V.speedBoost > 0 && V.topSpeeds[2] >= Long.MAX_VALUE ? "None (with Speed Boost)" :
    hasWrath ? Math.round(Units.getSpeed(V.topSpeeds[2])) + " " + Units.getSpeedName() + " (with Wrath engaged)" :
    V.speedBoost > 0 ? Math.round(Units.getSpeed(V.topSpeeds[2])) + " " + Units.getSpeedName() + " (with Speed Boost)" :
    Math.round(Units.getSpeed(V.topSpeeds[1])) + " " + Units.getSpeedName();
    U.textL(topSpeed, lineLR, Y1);
    U.textR("Acceleration Phases:", lineLL, Y2);
    U.textL("+" + V.accelerationStages[0] + ",  +" + V.accelerationStages[1], lineLR, Y2);
   }
   U.textR("Handling Response:", lineLL, Y3);
   U.textL(V.turnRate == Double.POSITIVE_INFINITY ? D.Instant : String.valueOf(V.turnRate), lineLR, Y3);
   if (V.type == Vehicle.Type.vehicle) {
    U.textR("Stunt Response:", lineLL, Y4);
    U.textL(String.valueOf(V.airAcceleration == Double.POSITIVE_INFINITY ? D.Instant : (float) V.airAcceleration), lineLR, Y4);
   }
  }
  boolean hasForceField = false;
  if (!V.specials.isEmpty()) {
   U.textR("Special(s):", lineLL, Y5);
   StringBuilder specials = new StringBuilder();
   for (var special : V.specials) {
    specials.append(special.type.name()).append(", ");
    hasForceField = special.type == Special.Type.forcefield || hasForceField;
   }
   U.textL(String.valueOf(specials), lineLR, Y5);
  }
  U.textR("Collision Damage Rating:", lineRL, Y0);
  String damageDealt =
  V.dealsMassiveDamage() || V.explosionType.name().contains(Vehicle.ExplosionType.nuclear.name()) ? "Instant-Kill" :
  hasForceField || V.spinner != null ? "'Inconsistent'" :
  String.valueOf((float) V.damageDealt);
  U.textL(damageDealt, lineRR, Y0);
  U.textR("Fragility:", lineRL, Y1);
  U.textL(String.valueOf(V.fragility), lineRR, Y1);
  U.textR("Self-Repair:", lineRL, Y2);
  U.textL(String.valueOf(V.selfRepair), lineRR, Y2);
  U.textR("Total Durability:", lineRL, Y3);
  U.textL(String.valueOf(V.durability), lineRR, Y3);
  if (!V.isFixed()) {
   U.textR("Speed Boost:", lineRL, Y4);
   U.textL(V.speedBoost > 0 ? UI.Yes : UI.No, lineRR, Y4);
   U.textR("Amphibious-Capable:", lineRL, Y5);
   U.textL(V.amphibious != null ? UI.Yes : UI.No, lineRR, Y5);
  }
 }
}