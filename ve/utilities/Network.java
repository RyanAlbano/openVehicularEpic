package ve.utilities;

import ve.instances.I;
import ve.ui.*;
import ve.vehicles.Vehicle;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public enum Network {
 ;

 public static String userName = "";
 public static final int maxPlayers = 10;
 public static boolean waiting;
 public static int bonusHolder = -1;
 public static boolean hostLeftMatch;
 public static boolean runLoadThread;
 public static boolean[] ready = new boolean[maxPlayers];
 public static final boolean[] runGameThread = new boolean[maxPlayers];
 private static final Thread[] gameMatch = new Thread[maxPlayers];
 public static ServerSocket server;
 public static Socket client;
 public static String targetHost = "";
 public static int port = 7777;
 public static String joinError = "";
 public static final List<PrintWriter> out = new ArrayList<>();
 public static final List<BufferedReader> in = new ArrayList<>();
 public static final String[] vehicleData = {"", "", "", "", "", "", "", "", "", ""};
 public static final String[] lastVehicleData = {"", "", "", "", "", "", "", "", "", ""};
 public static Mode mode = Mode.OFF;

 public enum Mode {
  OFF, HOST, JOIN
 }

 public static void loadGameThread() {
  Thread gameSetup = new Thread(() -> {
   int n;
   runLoadThread = true;
   if (mode == Mode.HOST) {
    UI.userPlayerIndex = 0;
    UI.playerNames[0] = userName;
    try {
     server = new ServerSocket(port);
     while (runLoadThread) {
      if (out.size() + 1 < UI.vehiclesInMatch && !server.isClosed()) {
       try (Socket S = server.accept(); S) {
        out.add(new PrintWriter(S.getOutputStream(), true, U.standardChars));
        in.add(new BufferedReader(new InputStreamReader(S.getInputStream(), U.standardChars)));
       } catch (IOException E) {
        E.printStackTrace();
       }
      }
      while (true) {
       String s = readIn(out.size() - 1);
       if (s.startsWith(SL.CANCEL)) {
        UI.escapeToLast(false);
       } else if (s.startsWith("Name(")) {
        UI.playerNames[out.size()] = U.getString(s, 0);
        for (PrintWriter PW : out) {
         for (int n1 = out.size() + 1; --n1 > 0; ) {
          PW.println(SL.Name + n1 + "(" + UI.playerNames[n1]);
         }
        }
        out.get(out.size() - 1).println("#Vehicles" + "(" + UI.vehiclesInMatch);
        out.get(out.size() - 1).println("Name0(" + userName);
        out.get(out.size() - 1).println("Join#(" + out.size());
        out.get(out.size() - 1).println(SL.MatchLength + "(" + Options.matchLength);
       } else if (s.startsWith(SL.joinerReady)) {
        System.out.println(UI.playerNames[out.size()] + " Joined Successfully");
        break;
       }
      }
      if (out.size() + 1 >= UI.vehiclesInMatch) {
       for (PrintWriter PW : out) {
        PW.println(SL.HostReady);
       }
       UI.status = UI.Status.vehicleSelect;
       UI.page = VS.index = 0;
       waiting = runLoadThread = false;
      }
      UI.gameFPS = U.refreshRate;
     }
    } catch (IOException e) {
     e.printStackTrace();
    }
   } else {
    try {
     client = new Socket(targetHost, port);
     out.add(new PrintWriter(client.getOutputStream(), true, U.standardChars));
     in.add(new BufferedReader(new InputStreamReader(client.getInputStream(), U.standardChars)));
     out.get(0).println(SL.Name + "(" + userName);
     while (runLoadThread) {
      String s = readIn(0);
      if (s.startsWith(SL.CANCEL)) {
       UI.escapeToLast(false);
       break;
      }
      UI.vehiclesInMatch = s.startsWith("#Vehicles(") ? (int) Math.round(U.getValue(s, 0)) : UI.vehiclesInMatch;
      if (s.startsWith("Join#" + "(")) {
       UI.userPlayerIndex = (int) Math.round(U.getValue(s, 0));
       UI.playerNames[UI.userPlayerIndex] = userName;
      } else if (s.startsWith(SL.Name)) {
       for (n = maxPlayers; --n >= 0; ) {
        UI.playerNames[n] = s.startsWith(SL.Name + n) ? U.getString(s, 0) : UI.playerNames[n];
       }
      } else if (s.startsWith("MatchLength(")) {
       Options.matchLength = Math.round(U.getValue(s, 0));
       out.get(0).println(SL.joinerReady);
      } else if (s.startsWith(SL.HostReady)) {
       UI.status = UI.Status.vehicleSelect;
       VS.index = UI.userPlayerIndex;
       UI.page = 0;
       waiting = runLoadThread = false;
      }
      UI.gameFPS = U.refreshRate;
     }
    } catch (IOException E) {
     E.printStackTrace();
     UI.status = UI.Status.loadLAN;
     joinError = "Could not connect to Host";
     UI.page = 0;
     UI.errorTimer = 50;
    }
   }
  });
  gameSetup.setPriority(9);
  gameSetup.setDaemon(true);
  gameSetup.start();
 }

 public static void preMatchCommunication(boolean gamePlay) {
  if (mode != Mode.OFF) {
   int n;
   if (mode == Mode.HOST) {
    if (UI.timerBase20 <= 0) {
     for (PrintWriter PW : out) {
      PW.println(SL.Vehicle + "0" + "(" + I.vehicles.get(0).name);
     }
     if (gamePlay) {
      for (PrintWriter PW : out) {
       PW.println(SL.Map + "(" + Map.name);
      }
      if (waiting) {
       for (PrintWriter PW : out) {
        PW.println(SL.Ready + "0");
       }
      }
     }
    }
    for (n = UI.vehiclesInMatch; --n > 0; ) {
     String s = readIn(n - 1);
     if (s.startsWith(SL.CANCEL)) {
      UI.escapeToLast(false);
     } else if (s.startsWith(SL.Vehicle + "(")) {
      VS.chosen[n] = UI.getVehicleIndex(U.getString(s, 0));
      if (UI.vehiclesInMatch > 2) {
       for (PrintWriter out : out) {
        out.println(SL.Vehicle + n + "(" + U.getString(s, 0));
       }
      }
     } else if (gamePlay && s.startsWith(SL.Ready)) {
      ready[n] = true;
      if (UI.vehiclesInMatch > 2) {
       for (PrintWriter out : out) {
        out.println(SL.Ready + n);
       }
      }
     }
    }
   } else {
    if (UI.timerBase20 <= 0) {
     out.get(0).println(SL.Vehicle + "(" + I.vehicles.get(0).name);
     if (gamePlay && waiting) {
      out.get(0).println(SL.Ready);
     }
    }
    String s = readIn(0);
    if (s.startsWith(SL.CANCEL)) {
     UI.escapeToLast(false);
    } else if (UI.status == UI.Status.mapJump && s.startsWith("Map(")) {
     UI.map = Map.getName(U.getString(s, 0));
     UI.status = UI.Status.mapLoadPass0;
    } else if (gamePlay) {
     for (n = UI.vehiclesInMatch; --n >= 0; ) {
      ready[n] = s.startsWith(SL.Ready + n) || ready[n];
     }
    }
   }
  }
 }

 public static void gamePlay(int n) {
  gameMatch[n] = new Thread(() -> {
   String s;
   runGameThread[n] = true;
   while (runGameThread[n]) {
    if (mode == Mode.HOST) {
     s = readIn(n - 1);
     if (s.startsWith(SL.BonusOpen)) {
      bonusHolder = -1;
      if (UI.vehiclesInMatch > 2) {
       for (int n1 = UI.vehiclesInMatch; --n1 > 0; ) {
        if (n1 != n) {
         out.get(n1 - 1).println(SL.BonusOpen);
        }
       }
      }
     } else if (s.startsWith(SL.BONUS)) {
      bonusHolder = n;
      if (UI.vehiclesInMatch > 2) {
       for (int n1 = UI.vehiclesInMatch; --n1 > 0; ) {
        if (n1 != n) {
         out.get(n1 - 1).println(SL.BONUS + n);
        }
       }
      }
     } else if (s.startsWith("(")) {
      vehicleData[n] = s;
     }
     if (UI.vehiclesInMatch > 2 && !lastVehicleData[n].equals(vehicleData[n])) {
      for (int n1 = UI.vehiclesInMatch; --n1 > 0; ) {
       if (n1 != n) {
        out.get(n1 - 1).println(n + vehicleData[n]);
       }
      }
      lastVehicleData[n] = vehicleData[n];
     }
     if (vehicleData[n].startsWith("(")) {
      Vehicle V = I.vehicles.get(n);
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
     hostLeftMatch = s.startsWith(SL.END) || hostLeftMatch;
     for (Vehicle vehicle : I.vehicles) {
      if (vehicle.index != UI.userPlayerIndex) {
       bonusHolder = s.startsWith(SL.BonusOpen) ? -1 : s.startsWith(SL.BONUS + vehicle.index) ? vehicle.index : bonusHolder;
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
     Thread.sleep(1);//todo<-Remove?
    } catch (InterruptedException ignored) {
    }
   }
  });
  gameMatch[n].setPriority(9);
  gameMatch[n].setDaemon(true);
  gameMatch[n].start();
 }

 public static void matchDataOut() {
  Vehicle V = I.vehicles.get(UI.userPlayerIndex);
  long specialsQuantity = V.specials.size();
  V.drive = Keys.Up;
  V.reverse = Keys.Down;
  V.turnL = Keys.Left;
  V.turnR = Keys.Right;
  V.handbrake = Keys.Space;
  if (specialsQuantity > 0) {
   V.specials.get(0).fire = Keys.Special[0];
  }
  if (specialsQuantity > 1) {
   V.specials.get(1).fire = Keys.Special[1];
  }
  V.boost = Keys.keyBoost;
  String s;
  if (UI.status == UI.Status.play) {
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