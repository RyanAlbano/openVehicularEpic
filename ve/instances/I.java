package ve.instances;

import javafx.scene.paint.Color;
import ve.ui.Maps;
import ve.ui.Viewer;
import ve.utilities.D;
import ve.utilities.Network;
import ve.utilities.Nodes;
import ve.utilities.U;
import ve.vehicles.Vehicle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public enum I {//<-handles Instances
 ;
 public static final int maxPlayers = (int) Math.round(Math.max(Network.maxPlayers, U.maxMemory * .00000001125));
 public static List<String> vehicleModels;
 public static final List<Vehicle> vehicles = new ArrayList<>(maxPlayers);
 public static int vehiclePerspective;
 public static int userPlayerIndex;
 public static int vehiclesInMatch = 1;
 public static Color userRandomRGB = U.getColor(U.random(), U.random(), U.random());
 public static final float[] textureCoordinateBase0 = {0, 1, 1, 1, 1, 0, 0, 0}, textureCoordinateBase1 = {0, 0, 1, 0, 1, 1, 0, 1};
 public static boolean trainEngineInMatch, nukeInMatch, maxNukeInMatch;

 public static int getVehicleIndex(String s) {
  String s1, s3 = "";
  int n;
  for (n = 0; n < vehicleModels.size(); n++) {
   File F = new File(U.modelFolder + File.separator + D.vehicles + File.separator + vehicleModels.get(n));
   if (!F.exists()) {
    F = new File(U.modelFolder + File.separator + D.vehicles + File.separator + U.userSubmittedFolder + File.separator + vehicleModels.get(n));
   }
   if (!F.exists()) {
    F = new File(U.modelFolder + File.separator + D.vehicles + File.separator + D.basic);
   }
   try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream(F), U.standardChars))) {
    for (String s2; (s2 = BR.readLine()) != null; ) {
     s1 = s2.trim();
     if (s1.startsWith(D.name)) {
      s3 = U.getString(s1, 0);
      break;
     }
    }
   } catch (IOException e) {//<-Don't bother
    e.printStackTrace();
   }
   if (s.equals(s3)) {
    break;
   }
  }
  return n;
 }

 public static String getVehicleName(int in) {//<-Keep method in case we need it later
  String s, s3 = "";
  File F = new File(U.modelFolder + File.separator + vehicleModels.get(in));
  if (!F.exists()) {
   F = new File(U.modelFolder + File.separator + U.userSubmittedFolder + File.separator + vehicleModels.get(in));
  }
  if (!F.exists()) {
   F = new File(U.modelFolder + File.separator + D.basic);
  }
  try (BufferedReader BR = new BufferedReader(new InputStreamReader(new FileInputStream(F), U.standardChars))) {
   for (String s2; (s2 = BR.readLine()) != null; ) {
    s = s2.trim();
    if (s.startsWith(D.name)) {
     s3 = U.getString(s, 0);
     break;
    }
   }
  } catch (IOException e) {
   e.printStackTrace();
  }
  return s3;
 }

 public static void addVehicleModel(int v, boolean show) {
  vehicles.clear();
  vehicles.add(new Vehicle(v, 0, false, show));
  vehicles.get(0).lightBrightness = Maps.defaultVehicleLightBrightness;
  for (var part : vehicles.get(0).parts) {
   Nodes.add(part.MV);
   part.MV.setVisible(true);
   part.setDrawMode(Viewer.Vehicle.showWireframe);
  }
 }

 public static void removeVehicleModel() {
  if (!vehicles.isEmpty() && vehicles.get(0) != null) {
   for (var part : vehicles.get(0).parts) {
    Nodes.remove(part.MV);
    Nodes.removePointLight(part.pointLight);
   }
  }
 }

 public static boolean sameVehicle(Vehicle V1, Vehicle V2) {
  return V1.index == V2.index;
 }

 public static boolean sameTeam(Vehicle V1, Vehicle V2) {
  return sameTeam(V1.index, V2.index);
 }

 public static boolean sameTeam(long index1, long index2) {
  return index1 < halfThePlayers() == index2 < halfThePlayers();
 }

 public static double halfThePlayers() {
  return vehiclesInMatch >> 1;
 }

 public static void resetWhoIsIn() {
  trainEngineInMatch = nukeInMatch = maxNukeInMatch = false;
 }
}
