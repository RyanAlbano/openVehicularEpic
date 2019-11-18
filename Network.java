package ve;

import ve.utilities.SL;
import ve.utilities.U;
import ve.vehicles.Vehicle;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public enum Network {
 ;

 static String userName = "";
 static final int maxPlayers = 10;
 static boolean waiting;
 public static int bonusHolder = -1;
 static boolean hostLeftMatch;
 static boolean runLoadThread;
 static boolean[] ready = new boolean[maxPlayers];
 static final boolean[] runGameThread = new boolean[maxPlayers];
 private static final Thread[] gameMatch = new Thread[maxPlayers];
 static ServerSocket server;
 static Socket client;
 static String targetHost = "";
 static int port = 7777;
 static String joinError = "";
 public static final List<PrintWriter> out = new ArrayList<>();
 public static final List<BufferedReader> in = new ArrayList<>();
 static final String[] vehicleData = {"", "", "", "", "", "", "", "", "", ""};
 static final String[] lastVehicleData = {"", "", "", "", "", "", "", "", "", ""};
 public static Mode mode = Mode.OFF;

 public enum Mode {
  OFF, HOST, JOIN
 }

 static void loadGameThread() {
  Thread gameSetup = new Thread(() -> {
   int n;
   runLoadThread = true;
   if (mode == Mode.HOST) {
    VE.userPlayerIndex = 0;
    VE.playerNames[0] = userName;
    try {
     server = new ServerSocket(port);
     while (runLoadThread) {
      if (out.size() + 1 < VE.vehiclesInMatch && !server.isClosed()) {
       try (Socket S = server.accept(); S) {
        out.add(new PrintWriter(S.getOutputStream(), true, U.standardChars));
        in.add(new BufferedReader(new InputStreamReader(S.getInputStream(), U.standardChars)));
       } catch (IOException E) {
        E.printStackTrace();
       }
      }
      while (true) {
       String s = readIn(out.size() - 1);
       if (s.startsWith(SL.Network.cancel)) {
        VE.escapeToLast(false);
       } else if (s.startsWith("Name(")) {
        VE.playerNames[out.size()] = U.getString(s, 0);
        for (PrintWriter PW : out) {
         for (int n1 = out.size() + 1; --n1 > 0; ) {
          PW.println("Name" + n1 + "(" + VE.playerNames[n1]);
         }
        }
        out.get(out.size() - 1).println("#Vehicles(" + VE.vehiclesInMatch);
        out.get(out.size() - 1).println("Name0(" + userName);
        out.get(out.size() - 1).println("Join#(" + out.size());
        out.get(out.size() - 1).println("MatchLength(" + VE.Options.matchLength);
       } else if (s.startsWith(SL.Network.joinerReady)) {
        System.out.println(VE.playerNames[out.size()] + " Joined Successfully");
        break;
       }
      }
      if (out.size() + 1 >= VE.vehiclesInMatch) {
       for (PrintWriter PW : out) {
        PW.println(SL.Network.hostReady);
       }
       VE.status = VE.Status.vehicleSelect;
       VE.UI.page = VE.VS.index = 0;
       waiting = runLoadThread = false;
      }
      VE.gameFPS = U.refreshRate;
     }
    } catch (IOException e) {
     e.printStackTrace();
    }
   } else {
    try {
     client = new Socket(targetHost, port);
     out.add(new PrintWriter(client.getOutputStream(), true, U.standardChars));
     in.add(new BufferedReader(new InputStreamReader(client.getInputStream(), U.standardChars)));
     out.get(0).println("Name(" + userName);
     while (runLoadThread) {
      String s = readIn(0);
      if (s.startsWith(SL.Network.cancel)) {
       VE.escapeToLast(false);
       break;
      }
      VE.vehiclesInMatch = s.startsWith("#Vehicles(") ? (int) Math.round(U.getValue(s, 0)) : VE.vehiclesInMatch;
      if (s.startsWith("Join#(")) {
       VE.userPlayerIndex = (int) Math.round(U.getValue(s, 0));
       VE.playerNames[VE.userPlayerIndex] = userName;
      } else if (s.startsWith("Name")) {
       for (n = maxPlayers; --n >= 0; ) {
        VE.playerNames[n] = s.startsWith("Name" + n) ? U.getString(s, 0) : VE.playerNames[n];
       }
      } else if (s.startsWith("MatchLength(")) {
       VE.Options.matchLength = Math.round(U.getValue(s, 0));
       out.get(0).println(SL.Network.joinerReady);
      } else if (s.startsWith(SL.Network.hostReady)) {
       VE.status = VE.Status.vehicleSelect;
       VE.VS.index = VE.userPlayerIndex;
       VE.UI.page = 0;
       waiting = runLoadThread = false;
      }
      VE.gameFPS = U.refreshRate;
     }
    } catch (IOException E) {
     E.printStackTrace();
     VE.status = VE.Status.loadLAN;
     joinError = "Could not connect to Host";
     VE.UI.page = 0;
     VE.errorTimer = 50;
    }
   }
  });
  gameSetup.setPriority(9);
  gameSetup.setDaemon(true);
  gameSetup.start();
 }

 static void preMatchCommunication(boolean gamePlay) {
  if (mode != Mode.OFF) {
   int n;
   if (mode == Mode.HOST) {
    if (VE.timerBase20 <= 0) {
     for (PrintWriter PW : out) {
      PW.println("Vehicle0(" + VE.vehicles.get(0).name);
     }
     if (gamePlay) {
      for (PrintWriter PW : out) {
       PW.println("Map(" + VE.Map.name);
      }
      if (waiting) {
       for (PrintWriter PW : out) {
        PW.println("Ready0");
       }
      }
     }
    }
    for (n = VE.vehiclesInMatch; --n > 0; ) {
     String s = readIn(n - 1);
     if (s.startsWith(SL.Network.cancel)) {
      VE.escapeToLast(false);
     } else if (s.startsWith("Vehicle(")) {
      VE.vehicleNumber[n] = VE.getVehicleIndex(U.getString(s, 0));
      if (VE.vehiclesInMatch > 2) {
       for (PrintWriter out : out) {
        out.println("Vehicle" + n + "(" + U.getString(s, 0));
       }
      }
     } else if (gamePlay && s.startsWith(SL.Network.ready)) {
      ready[n] = true;
      if (VE.vehiclesInMatch > 2) {
       for (PrintWriter out : out) {
        out.println(SL.Network.ready + n);
       }
      }
     }
    }
   } else {
    if (VE.timerBase20 <= 0) {
     out.get(0).println("Vehicle(" + VE.vehicles.get(0).name);
     if (gamePlay && waiting) {
      out.get(0).println(SL.Network.ready);
     }
    }
    String s = readIn(0);
    if (s.startsWith(SL.Network.cancel)) {
     VE.escapeToLast(false);
    } else if (VE.status == VE.Status.mapJump && s.startsWith("Map(")) {
     VE.map = VE.Map.getMapName(U.getString(s, 0));
     VE.status = VE.Status.mapLoadPass0;
    } else if (gamePlay) {
     for (n = VE.vehiclesInMatch; --n >= 0; ) {
      ready[n] = s.startsWith(SL.Network.ready + n) || ready[n];
     }
    }
   }
  }
 }

 static void gamePlay(int n) {
  gameMatch[n] = new Thread(() -> {
   String s;
   runGameThread[n] = true;
   while (runGameThread[n]) {
    if (mode == Mode.HOST) {
     s = readIn(n - 1);
     if (s.startsWith(SL.Network.bonusOpen)) {
      bonusHolder = -1;
      if (VE.vehiclesInMatch > 2) {
       for (int n1 = VE.vehiclesInMatch; --n1 > 0; ) {
        if (n1 != n) {
         out.get(n1 - 1).println(SL.Network.bonusOpen);
        }
       }
      }
     } else if (s.startsWith(SL.Network.bonus)) {
      bonusHolder = n;
      if (VE.vehiclesInMatch > 2) {
       for (int n1 = VE.vehiclesInMatch; --n1 > 0; ) {
        if (n1 != n) {
         out.get(n1 - 1).println(SL.Network.bonus + n);
        }
       }
      }
     } else if (s.startsWith("(")) {
      vehicleData[n] = s;
     }
     if (VE.vehiclesInMatch > 2 && !lastVehicleData[n].equals(vehicleData[n])) {
      for (int n1 = VE.vehiclesInMatch; --n1 > 0; ) {
       if (n1 != n) {
        out.get(n1 - 1).println(n + vehicleData[n]);
       }
      }
      lastVehicleData[n] = vehicleData[n];
     }
     if (vehicleData[n].startsWith("(")) {
      Vehicle V = VE.vehicles.get(n);
      V.X = U.getValue(vehicleData[n], 0);
      V.Y = U.getValue(vehicleData[n], 1);
      V.Z = U.getValue(vehicleData[n], 2);
      V.XZ = U.getValue(vehicleData[n], 3);
      V.YZ = U.getValue(vehicleData[n], 4);
      V.XY = U.getValue(vehicleData[n], 5);
      V.P.speed = U.getValue(vehicleData[n], 6);
      V.setDamage(U.getValue(vehicleData[n], 7));
      V.checkpointsPassed = (int) Math.round(U.getValue(vehicleData[n], 8));
      V.lightBrightness = U.getValue(vehicleData[n], 9);
      V.drive = vehicleData[n].contains(" ^ ");
      V.reverse = vehicleData[n].contains(" v ");
      V.turnL = vehicleData[n].contains(" < ");
      V.turnR = vehicleData[n].contains(" > ");
      V.handbrake = vehicleData[n].contains(" _ ");
      if (!V.specials.isEmpty()) {
       V.specials.get(0).fire = vehicleData[n].contains(" 0 ");
      }
      if (V.specials.size() > 1) {
       V.specials.get(1).fire = vehicleData[n].contains(" 1 ");
      }
      V.boost = vehicleData[n].contains(" b ");
     }
    } else {
     s = readIn(0);
     hostLeftMatch = s.startsWith("END") || hostLeftMatch;
     for (Vehicle vehicle : VE.vehicles) {
      if (vehicle.index != VE.userPlayerIndex) {
       bonusHolder = s.startsWith(SL.Network.bonusOpen) ? -1 : s.startsWith(SL.Network.bonus + vehicle.index) ? vehicle.index : bonusHolder;
       if (s.startsWith(vehicle.index + "(")) {
        vehicleData[vehicle.index] = s;
        vehicle.X = U.getValue(vehicleData[vehicle.index], 0);
        vehicle.Y = U.getValue(vehicleData[vehicle.index], 1);
        vehicle.Z = U.getValue(vehicleData[vehicle.index], 2);
        vehicle.XZ = U.getValue(vehicleData[vehicle.index], 3);
        vehicle.YZ = U.getValue(vehicleData[vehicle.index], 4);
        vehicle.XY = U.getValue(vehicleData[vehicle.index], 5);
        vehicle.P.speed = U.getValue(vehicleData[vehicle.index], 6);
        vehicle.setDamage(U.getValue(vehicleData[vehicle.index], 7));
        vehicle.checkpointsPassed = (int) Math.round(U.getValue(vehicleData[vehicle.index], 8));
        vehicle.lightBrightness = U.getValue(vehicleData[vehicle.index], 9);
        vehicle.drive = vehicleData[vehicle.index].contains(" ^ ");
        vehicle.reverse = vehicleData[vehicle.index].contains(" v ");
        vehicle.turnL = vehicleData[vehicle.index].contains(" < ");
        vehicle.turnR = vehicleData[vehicle.index].contains(" > ");
        vehicle.handbrake = vehicleData[vehicle.index].contains(" _ ");
        if (!vehicle.specials.isEmpty()) {
         vehicle.specials.get(0).fire = vehicleData[vehicle.index].contains(" 0 ");
        }
        if (vehicle.specials.size() > 1) {
         vehicle.specials.get(1).fire = vehicleData[vehicle.index].contains(" 1 ");
        }
        vehicle.boost = vehicleData[vehicle.index].contains(" b ");
       }
      }
     }
    }
    try {
     Thread.sleep(1);//<-Remove?
    } catch (InterruptedException ignored) {
    }
   }
  });
  gameMatch[n].setPriority(9);
  gameMatch[n].setDaemon(true);
  gameMatch[n].start();
 }

 static void matchDataOut() {
  Vehicle V = VE.vehicles.get(VE.userPlayerIndex);
  long specialsQuantity = V.specials.size();
  V.drive = VE.Keys.Up;
  V.reverse = VE.Keys.Down;
  V.turnL = VE.Keys.Left;
  V.turnR = VE.Keys.Right;
  V.handbrake = VE.Keys.Space;
  if (specialsQuantity > 0) {
   V.specials.get(0).fire = VE.Keys.Special[0];
  }
  if (specialsQuantity > 1) {
   V.specials.get(1).fire = VE.Keys.Special[1];
  }
  V.boost = VE.Keys.keyBoost;
  String s;
  if (VE.status == VE.Status.play) {
   s = "(" + V.X + "," + V.Y + "," + V.Z + "," + V.XZ + "," + V.YZ + "," + V.XY + "," + V.P.speed + "," + V.getDamage(false) + "," + V.checkpointsPassed + "," + V.lightBrightness + ")";
   s += V.drive ? " ^ " : "";
   s += V.reverse ? " v " : "";
   s += V.turnL ? " < " : "";
   s += V.turnR ? " > " : "";
   s += V.handbrake ? " _ " : "";
   if (specialsQuantity > 0) {
    s += V.specials.get(0).fire ? " 0 " : "";
   }
   if (specialsQuantity > 1) {
    s += V.specials.get(1).fire ? " 1 " : "";
   }
   s += V.boost ? " b " : "";
   if (mode == Mode.HOST) {
    for (PrintWriter out : out) {
     out.println("0" + s);
    }
   } else {
    out.get(0).println(s);
   }
  }
 }

 public static String readIn(int n) {
  String s = "";
  try {
   s = in.get(n).ready() ? in.get(n).readLine() : s;
  } catch (IOException e) {
   e.printStackTrace();
  }
  return s;
 }
}
