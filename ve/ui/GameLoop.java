package ve.ui;

import javafx.animation.AnimationTimer;
import javafx.stage.Stage;
import ve.environment.E;
import ve.environment.Pool;
import ve.instances.I;
import ve.trackElements.Arrow;
import ve.trackElements.TE;
import ve.ui.options.GraphicsOptions;
import ve.ui.options.Options;
import ve.ui.options.SoundOptions;
import ve.utilities.*;
import ve.utilities.sound.Sounds;
import ve.vehicles.Physics;
import ve.vehicles.specials.Special;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

class GameLoop extends AnimationTimer {
 private final Stage stage;

 GameLoop(Stage primaryStage) {
  stage = primaryStage;
 }

 public void handle(long now) {
  try {
   int n;
   UI.GC.clearRect(0, 0, UI.width, UI.height);
   E.GC.clearRect(0, 0, UI.width, UI.height);
   E.renderLevel = U.clamp(10000, E.renderLevel * (U.FPS < 30 ? .75 : 1.05), 40000);
   E.renderLevel = U.maxedFPS(true) ? Double.POSITIVE_INFINITY : E.renderLevel;
   Camera.FOV = Math.min(Camera.FOV * Camera.adjustFOV, 170);
   Camera.FOV = Camera.restoreZoom[0] && Camera.restoreZoom[1] ? Camera.defaultFOV : Camera.FOV;
   Camera.PC.setFieldOfView(Camera.FOV);
   if (I.userPlayerIndex < I.vehicles.size() && I.vehicles.get(I.userPlayerIndex) != null) {
    I.vehicles.get(I.userPlayerIndex).lightBrightness = U.clamp(I.vehicles.get(I.userPlayerIndex).lightBrightness + Match.vehicleLightBrightnessChange);
   }
   E.lightsAdded = 0;//<-Must come before any lights get added, obviously
   if (Mouse.click) {
    Mouse.mouse = Keys.left = Keys.right = Keys.enter = false;
   }
   boolean gamePlay = UI.status == UI.Status.play || UI.status == UI.Status.replay,//<-All 'gamePlay' calls in the entire project are determined by this!
   renderALL = E.renderType == E.RenderType.ALL;
   if (Mouse.mouse && (!gamePlay || !Match.started)) {
    if (Mouse.X < .375) {
     Keys.left = true;
    } else if (Mouse.X > .625) {
     Keys.right = true;
    } else {
     Keys.enter = Mouse.click = true;
    }
    Mouse.click = UI.status != UI.Status.vehicleSelect && UI.status != UI.Status.mapJump && !UI.status.name().contains("options") || Mouse.click;
   }
   UI.selectionTimer += U.tick;
   if (UI.width != stage.getWidth() || UI.height != stage.getHeight()) {
    UI.width = stage.getWidth();
    UI.height = stage.getHeight();
    UI.scene3D.setWidth(UI.width);
    UI.scene3D.setHeight(UI.height);
    Arrow.scene.setWidth(UI.width);
    Arrow.scene.setHeight(UI.height);
    UI.canvas.setWidth(UI.width);
    UI.canvas.setHeight(UI.height);
    E.canvas.setWidth(UI.width);
    E.canvas.setHeight(UI.height);
   }
   if (gamePlay || UI.status == UI.Status.paused || UI.status == UI.Status.optionsMatch) {
    for (var vehicle : I.vehicles) {//*These are SPLIT so that energy towers can empower specials before the affected vehicles fire, and to make shots render correctly
     for (var special : vehicle.specials) {
      if (special.type == Special.Type.energy) {
       special.EB.run(gamePlay);//*
      }
     }
    }
    //Energization before runMiscellaneous() is called
    for (var vehicle : I.vehicles) {
     vehicle.runMiscellaneous(gamePlay);
    }
    if (Match.started) {
     if (gamePlay && Match.cursorDriving) {
      Mouse.steerX = 100 * (.5 - Mouse.X);
      Mouse.steerY = 100 * (Mouse.Y - .5);
      if (I.vehicles.get(I.userPlayerIndex).P.mode != Physics.Mode.fly && !I.vehicles.get(I.userPlayerIndex).isFixed()) {
       if (Mouse.Y < .5) {
        Keys.down = false;
        Keys.up = true;
       } else if (Mouse.Y > .75) {
        Keys.up = false;
        Keys.down = true;
       } else {
        Keys.up = Keys.down = false;
       }
      }
      Keys.space = Mouse.mouse;
     }
     if (Network.mode != Network.Mode.OFF) {
      Network.matchDataOut();
     }
     if (gamePlay) {
      for (var vehicle : I.vehicles) {
       vehicle.getPlayerInput();
       vehicle.P.run();
      }
      for (n = I.vehiclesInMatch; --n >= 0; ) {
       Recorder.vehicles.get(n).recordVehicle(I.vehicles.get(n));
      }
     }
     for (var vehicle : I.vehicles) {
      for (var explosion : vehicle.explosions) {
       explosion.run(gamePlay);
      }
      double
      sinXZ = U.sin(vehicle.XZ), cosXZ = U.cos(vehicle.XZ),
      sinYZ = U.sin(vehicle.YZ), cosYZ = U.cos(vehicle.YZ),
      sinXY = U.sin(vehicle.XY), cosXY = U.cos(vehicle.XY);
      for (var special : vehicle.specials) {
       special.run(gamePlay, sinXZ, cosXZ, sinYZ, cosYZ, sinXY, cosXY);//*
      }
     }
     if (gamePlay) {
      for (var vehicle : I.vehicles) {
       vehicle.P.runCollisions();
      }
      for (var vehicle : I.vehicles) {
       if (vehicle.destroyed && vehicle.P.vehicleHit > -1) {
        Match.scoreKill[vehicle.index < I.vehiclesInMatch >> 1 ? 1 : 0] += UI.status == UI.Status.replay ? 0 : 1;
        if (vehicle.index != I.userPlayerIndex) {
         vehicle.AI.target = U.random(I.vehiclesInMatch);//<-Needed!
        }
        vehicle.P.vehicleHit = -1;
       }
       vehicle.energyMultiple = 1;//<-Reset vehicle energy levels for next frame
      }
      if (UI.status == UI.Status.play) {
       Recorder.recordGeneral();
       if (Network.mode == Network.Mode.OFF) {
        for (var vehicle : I.vehicles) {
         vehicle.AI.run();
        }
       }
      }
     }
     Recorder.updateFrame();
    } else {
     for (var vehicle : I.vehicles) {
      vehicle.setTurretY();
     }
     Network.preMatchCommunication(gamePlay);
     Match.cursorDriving = false;
     if (Network.waiting) {
      U.font(.02);
      U.fillRGB(U.yinYang ? 0 : 1);
      if (I.vehiclesInMatch < 3) {
       U.text("..Waiting on " + UI.playerNames[Network.mode == Network.Mode.HOST ? 1 : 0] + "..", .5, .25);
      } else {
       U.text("..Waiting for all Players to Start..", .5, .25);
      }
      long whoIsReady = 0;
      for (n = I.vehiclesInMatch; --n >= 0; ) {
       whoIsReady += Network.ready[n] ? 1 : 0;
      }
      if (whoIsReady >= I.vehiclesInMatch) {
       if (Network.mode == Network.Mode.HOST) {
        for (n = I.vehiclesInMatch; --n > 0; ) {
         Network.gamePlay(n);
        }
       } else {
        Network.gamePlay(0);
       }
       Match.started = true;
       Network.waiting = false;
      }
     } else if (Keys.space) {
      UI.sound.play(1, 0);
      Camera.view = Camera.lastView;
      if (Network.mode == Network.Mode.OFF) {
       Match.started = true;
      } else {
       Network.ready[I.userPlayerIndex] = Network.waiting = true;
       if (Network.mode == Network.Mode.HOST) {
        for (var PW : Network.out) {
         PW.println(D.Ready + "0");
         PW.println(D.Ready + "0");
        }
       } else {
        Network.out.get(0).println(D.Ready);
        Network.out.get(0).println(D.Ready);
       }
      }
      Keys.space = false;
     }
     if (!Network.waiting) {
      U.font(.02);
      U.fillRGB(U.yinYang ? 0 : 1);
      if (I.vehicles.get(I.vehiclePerspective).isFixed() && (I.vehiclesInMatch < 2 || I.vehiclePerspective < I.vehiclesInMatch >> 1)) {
       U.text("Use Arrow Keys and < and > to place your infrastructure, then", .2);
       if (Keys.up || Keys.down || Keys.left || Keys.right) {
        UI.movementSpeedMultiple = Math.max(10, UI.movementSpeedMultiple * 1.05);
        I.vehicles.get(I.vehiclePerspective).Z += Keys.up ? UI.movementSpeedMultiple * U.tick : 0;
        I.vehicles.get(I.vehiclePerspective).Z -= Keys.down ? UI.movementSpeedMultiple * U.tick : 0;
        I.vehicles.get(I.vehiclePerspective).X -= Keys.left ? UI.movementSpeedMultiple * U.tick : 0;
        I.vehicles.get(I.vehiclePerspective).X += Keys.right ? UI.movementSpeedMultiple * U.tick : 0;
       } else {
        UI.movementSpeedMultiple = 0;
       }
      }
      U.text("Press SPACE to Begin" + (Tournament.stage > 0 ? " Round " + Tournament.stage : ""), .25);
     }
     if (Keys.escape) {
      UI.escapeToLast(true);
     }
    }
    Recorder.playBack();
    //RENDERING begins here
    Camera.run(I.vehicles.get(I.vehiclePerspective), gamePlay);
    E.run(gamePlay);
    for (var vehicle : I.vehicles) {
     for (var special : vehicle.specials) {
      for (var shot : special.shots) {
       shot.runRender();
      }
      for (var port : special.ports) {
       if (port.spit != null) {
        port.spit.runRender();
       }
       if (port.smokes != null) {
        for (var smoke : port.smokes) {
         smoke.runRender();
        }
       }
      }
      if (special.EB != null) {
       special.EB.renderMesh();
      }
     }
     for (var part : vehicle.parts) {
      Nodes.removePointLight(part.pointLight);
     }
    }
    if (Maps.defaultVehicleLightBrightness > 0) {
     for (var vehicle : I.vehicles) {
      Nodes.removePointLight(vehicle.burnLight);
     }
    }
    if (I.vehiclesInMatch < 2) {
     I.vehicles.get(I.vehiclePerspective).runRender(gamePlay);
    } else {
     int closest = I.vehiclePerspective;
     double compareDistance = Double.POSITIVE_INFINITY;
     for (var vehicle : I.vehicles) {
      if (vehicle.index != I.vehiclePerspective && U.distance(I.vehicles.get(I.vehiclePerspective), vehicle) < compareDistance) {
       closest = vehicle.index;
       compareDistance = U.distance(I.vehicles.get(I.vehiclePerspective), vehicle);
      }
     }
     if (I.vehicles.get(I.vehiclePerspective).lightBrightness >= I.vehicles.get(closest).lightBrightness) {
      I.vehicles.get(I.vehiclePerspective).runRender(gamePlay);
      I.vehicles.get(closest).runRender(gamePlay);
     } else {
      I.vehicles.get(closest).runRender(gamePlay);
      I.vehicles.get(I.vehiclePerspective).runRender(gamePlay);
     }
     for (var vehicle : I.vehicles) {
      if (vehicle.index != I.vehiclePerspective && vehicle.index != closest) {
       vehicle.runRender(gamePlay);
      }
     }
    }
    for (var trackPart : TE.trackParts) {
     trackPart.runGraphics(renderALL);
    }
    for (var mound : TE.mounds) {
     mound.runGraphics();
    }
    TE.bonus.run();
    Match.run(gamePlay);
    if (Camera.toUserPerspective[0] && Camera.toUserPerspective[1]) {
     I.vehiclePerspective = I.userPlayerIndex;
    }
    UI.gameFPS = Double.POSITIVE_INFINITY;
    E.renderType = E.RenderType.standard;
   } else {
    Pool.runVision();//<-Not called in-match HERE because it would draw over screenFlash
   }
   if (UI.status == UI.Status.paused) {
    UI.runPaused();
   } else if (UI.status == UI.Status.optionsMatch || UI.status == UI.Status.optionsMenu) {
    Options.run();
   } else if (UI.status == UI.Status.optionsGraphics) {
    GraphicsOptions.run();
   } else if (UI.status == UI.Status.optionsSound) {
    SoundOptions.run();
   } else if (UI.status == UI.Status.vehicleViewer) {
    Viewer.Vehicle.run(gamePlay);
   } else if (UI.status == UI.Status.mapViewer) {
    Viewer.runMapViewer(gamePlay);
   } else if (UI.status == UI.Status.credits) {
    Credits.run();
   } else if (UI.status == UI.Status.mainMenu) {
    UI.runMainMenu();
   } else if (UI.status == UI.Status.howToPlay) {
    UI.runHowToPlay();
   } else if (UI.status == UI.Status.vehicleSelect) {
    VS.run(gamePlay);
   } else if (UI.status == UI.Status.loadLAN) {
    UI.runLANMenu();
   } else if (UI.status == UI.Status.mapError) {
    Maps.runErrored();
   } else if (UI.status == UI.Status.mapJump) {
    Maps.runQuickSelect(gamePlay);
   } else if (UI.status == UI.Status.mapView) {
    Maps.runView(gamePlay);
   } else if (UI.status == UI.Status.mapLoadPass0 || UI.status == UI.Status.mapLoadPass1 || UI.status == UI.Status.mapLoadPass2 || UI.status == UI.Status.mapLoadPass3 || UI.status == UI.Status.mapLoadPass4) {
    Maps.load();
    Keys.falsify();
   }
   U.yinYang = !U.yinYang;
   U.timerBase20 = (U.timerBase20 += U.tick) > 20 ? 0 : U.timerBase20;
   if (UI.status != UI.Status.vehicleSelect) {
    for (var vehicle : I.vehicles) {
     if (vehicle != null && !vehicle.destroyed) {
      vehicle.flicker = !vehicle.flicker;
     }
    }
   }
   UI.selectionTimer = (UI.selectionTimer > UI.selectionWait ? 0 : UI.selectionTimer) + 5 * U.tick;
   if (Keys.left || Keys.right || Keys.up || Keys.down || Keys.space || Keys.enter) {
    if (UI.selectionWait == -1) {
     UI.selectionWait = 30;
     UI.selectionTimer = 0;
    }
    if (UI.selectionWait > 0) {
     UI.selectionWait -= U.tick;
    }
   } else {
    UI.selectionWait = -1;
    UI.selectionTimer = 0;
   }
   double targetFPS = Math.min(UI.gameFPS, UI.userFPS), dividedFPS = 1000 / targetFPS;
   if (targetFPS < U.refreshRate) {
    double difference = System.currentTimeMillis() - U.FPSTime;
    if (difference < dividedFPS) {
     U.zZz(dividedFPS - difference);
    }
   }
   U.setFPS();
   if (Options.showAppInfo) {
    U.fillRGB(0, 0, 0, UI.colorOpacity.minimal);
    U.fillRectangle(.25, .9625, .15, .05);
    U.fillRectangle(.75, .9625, .15, .05);
    U.fillRGB(1);
    U.font(.015);
    U.text("Nodes: " + (UI.group.getChildren().size() + Arrow.group.getChildren().size() + E.lights.getChildren().size()), .25, .965);
    U.font(.02);
    U.text(Math.round(U.averageFPS) + " FPS", .75, .965);
   }
   long time = System.nanoTime();
   U.tick = Math.min((time - U.lastTime + 500000) * .00000002, 1);
   U.lastTime = time;
  } catch (Exception E) {//<-It's for the entire loop--a general exception is probably most surefire
   try (PrintWriter PW = new PrintWriter(new File("V.E. EXCEPTION"), U.standardChars)) {
    E.printStackTrace(PW);
   } catch (IOException ignored) {
   }
   E.printStackTrace();
   handleException();
  }
 }


 private static void handleException() {
  UI.error = "An Exception Occurred!" + U.lineSeparator + "A File with the exception has been saved to the game folder";
  UI.status = UI.Status.mainMenu;
  Tournament.stage = UI.selected = 0;
  Nodes.reset();
  Sounds.reset();
  Keys.falsify();
  for (int n = VS.chosen.length; --n >= 0; ) {//<-Prevents recursive shutout from Vehicle Select if a bugged vehicle is causing such
   VS.chosen[n] = U.random(I.vehicleModels.size());
  }
  if (Network.mode == Network.Mode.HOST) {
   for (var PW : Network.out) {
    PW.println(D.CANCEL);
    PW.println(D.CANCEL);
   }
  } else if (Network.mode == Network.Mode.JOIN) {
   Network.out.get(0).println(D.CANCEL);
   Network.out.get(0).println(D.CANCEL);
  }
 }
}