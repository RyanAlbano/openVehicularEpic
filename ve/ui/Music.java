package ve.ui;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.File;
import java.io.FileInputStream;

public enum Music {
 ;
 public static double gain = -6.020599913;
 public static Player jLayer;
 private static String name = "";

 public static void load(String s) {
  if (!name.equals(s)) {
   name = s != null ? s : name;
   Thread thread = new Thread(() -> {
    if (jLayer != null) {
     jLayer.close();
    }
    try {
     jLayer = new Player(new FileInputStream(new File("music" + File.separator + name + ".mp3")));
    } catch (Exception E) {//<-do NOT change
     System.out.println("Problem loading Music: " + E);
    }
    if (jLayer != null) {
     try {
      jLayer.play();
     } catch (JavaLayerException ignored) {
     }
    }
   });
   thread.setDaemon(true);
   thread.start();
  }
 }
}
