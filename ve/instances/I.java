package ve.instances;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import ve.trackElements.TE;
import ve.trackElements.trackParts.TrackPart;
import ve.trackElements.trackParts.TrackPartPart;
import ve.ui.Map;
import ve.ui.Viewer;
import ve.utilities.*;
import ve.vehicles.Vehicle;
import ve.vehicles.VehiclePart;

import static ve.ui.UI.maxPlayers;

public abstract class I {//<-Abstract class for handling Instances

 public static final List<Vehicle> vehicles = new ArrayList<>(maxPlayers);

 public static void addVehicleModel(int v, boolean show) {
  vehicles.clear();
  vehicles.add(new Vehicle(v, 0, false, show));
  vehicles.get(0).lightBrightness = Map.defaultVehicleLightBrightness;
  for (VehiclePart part : vehicles.get(0).parts) {
   U.Nodes.add(part.MV);
   part.MV.setVisible(true);
   part.setDrawMode(Viewer.Vehicle.showWireframe);
  }
 }

 public static void removeVehicleModel() {
  if (!vehicles.isEmpty() && vehicles.get(0) != null) {
   for (VehiclePart part : vehicles.get(0).parts) {
    U.Nodes.remove(part.MV);
    U.Nodes.Light.remove(part.pointLight);
   }
  }
 }
}
