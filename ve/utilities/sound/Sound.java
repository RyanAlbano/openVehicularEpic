package ve.utilities.sound;

import ve.effects.echo.TimerEcho;
import ve.environment.E;
import ve.ui.Match;
import ve.utilities.U;

import java.io.*;
import java.util.*;

public class Sound {//Both TinySound and javax.sound.sampled implementations exist within this class--prepare for a read!
 private int currentIndex;
 public final List<ClipVE> clips = new ArrayList<>();

 enum Support {full, fireAndForget, loop}

 Sound() {
 }

 public Sound(String sound) {
  this(sound, Support.full);
 }

 Sound(String sound, Support support) {
  try {
   clips.add(new ClipVE(Sounds.folder + File.separator + sound + Sounds.extension, 1, support));
  } catch (Exception E) {
   System.out.println(Sounds.exception + E);
  }
 }

 public Sound(String sound, double maximumCheck) {
  this(sound, maximumCheck, 1, Support.full);
 }

 Sound(String sound, double maximumCheck, Support support) {
  this(sound, maximumCheck, 1, support);
 }

 Sound(String sound, double maximumCheck, double ratio, Support support) {
  for (int n = 0; n < maximumCheck; n++) {
   try {
    clips.add(new ClipVE(Sounds.folder + File.separator + sound + n + Sounds.extension, ratio, support));
   } catch (Exception E) {
    if (maximumCheck < Double.POSITIVE_INFINITY) {
     System.out.println(Sounds.exception + E);
    }
    break;
   }
  }
 }

 public void addClip(String sound) {
  addClip(sound, 1, Support.full);
 }

 public void addClip(String sound, double ratio) {
  addClip(sound, ratio, Support.full);
 }

 void addClip(String sound, Support support) {
  addClip(sound, 1, support);
 }

 void addClip(String sound, double ratio, Support support) {
  try {
   clips.add(new ClipVE(Sounds.folder + File.separator + sound + Sounds.extension, ratio, support));
  } catch (Exception E) {
   System.out.println(Sounds.exception + E);
  }
 }

 public boolean running() {
  if (Sounds.softwareBased) {
   for (var clip : clips) {
    if (clip.tinyLoop.playing()) {
     return true;
    }
   }
  } else {
   for (var clip : clips) {
    if (clip.sampled.isRunning()) {
     return true;
    }
   }
  }
  return false;
 }

 public boolean running(int index) {
  return index < clips.size() && (Sounds.softwareBased ? clips.get(index).tinyLoop.playing() : clips.get(index).sampled.isRunning());
 }

 public void play(double gain) {
  play(0, gain);
 }

 public void play(double index, double gain) {
  if (!Match.muteSound) {
   setCurrentIndex(index);
   if (currentIndex < clips.size()) {
    if (Sounds.softwareBased) {
     gain = Sounds.decibelToLinear(-gain * E.soundMultiple);
     clips.get(currentIndex).tinySingle.play(gain);
    } else {
     clips.get(currentIndex).sampled.stop();//<-Could add overhead. But Linux tests show it also improves sound-restart reliability!
     gain *= E.soundMultiple;
     if (gain < Sounds.sampledGainLimit) {
      clips.get(currentIndex).gain.setValue((float) -gain);
      clips.get(currentIndex).sampled.setFramePosition(0);
      clips.get(currentIndex).sampled.loop(0);
     }
     playEcho(currentIndex);
    }
   }
  }
 }

 private void playEcho(int index) {
  if (TimerEcho.presence > 0) {
   if (clips.get(index).tinySingleEcho != null) {
    new Timer().schedule(new TimerTask() {
     public void run() {
      clips.get(index).tinySingleEcho.play(Sounds.echoVolume, U.randomPlusMinus(1));
      cancel();
     }
    }, TimerEcho.presence);
   } else if (clips.get(index).sampledEcho != null) {
    new Timer().schedule(new TimerTask() {
     public void run() {
      clips.get(index).sampledEcho.stop();//<-Could add overhead. But Linux tests show it also improves sound-restart reliability!
      clips.get(index).echoBalance.setValue((float) U.randomPlusMinus(1));
      clips.get(index).sampledEcho.setFramePosition(0);
      clips.get(index).sampledEcho.loop(0);
      cancel();
     }
    }, TimerEcho.presence);
   }
  }
 }

 public void playIfNotPlaying(double gain) {
  playIfNotPlaying(0, gain);
 }

 public void playIfNotPlaying(double index, double gain) {
  if (!Match.muteSound) {
   setCurrentIndex(index);
   if (currentIndex < clips.size()) {
    if (Sounds.softwareBased) {
     if (!clips.get(currentIndex).tinyLoop.playing()) {
      gain = Sounds.decibelToLinear(-gain * E.soundMultiple);
      clips.get(currentIndex).tinyLoop.setLoop(false);
      clips.get(currentIndex).tinyLoop.rewind();
      clips.get(currentIndex).tinyLoop.play(false, gain);
     }
    } else {
     gain *= E.soundMultiple;
     if (!clips.get(currentIndex).sampled.isRunning() && gain < Sounds.sampledGainLimit) {
      clips.get(currentIndex).gain.setValue((float) -gain);
      clips.get(currentIndex).sampled.setFramePosition(0);
      clips.get(currentIndex).sampled.loop(0);
     }
    }
    playIfNotPlayingEcho(currentIndex);
   }
  }
 }

 private void playIfNotPlayingEcho(int index) {
  if (TimerEcho.presence > 0) {
   if (clips.get(index).tinyLoopEcho != null) {
    new Timer().schedule(new TimerTask() {
     public void run() {
      if (!clips.get(index).tinyLoopEcho.playing()) {
       clips.get(index).tinyLoopEcho.setPan(U.randomPlusMinus(1));
       clips.get(currentIndex).tinyLoop.rewind();
       clips.get(index).tinyLoopEcho.play(false);
      }
      cancel();
     }
    }, TimerEcho.presence);
   } else if (clips.get(index).sampledEcho != null) {
    new Timer().schedule(new TimerTask() {
     public void run() {
      if (!clips.get(index).sampledEcho.isRunning()) {
       clips.get(index).echoBalance.setValue((float) U.randomPlusMinus(1));
       clips.get(index).sampledEcho.setFramePosition(0);
       clips.get(index).sampledEcho.loop(0);
      }
      cancel();
     }
    }, TimerEcho.presence);
   }
  }
 }

 public void loop(double gain) {
  loop(0, gain);
 }

 public void loop(double index, double gain) {
  if (!Match.muteSound) {
   setCurrentIndex(index);
   if (currentIndex < clips.size()) {
    if (Sounds.softwareBased) {
     gain = Sounds.decibelToLinear(-gain * E.soundMultiple);
     clips.get(currentIndex).tinyLoop.setVolume(gain);
     if (!clips.get(currentIndex).tinyLoop.playing()) {
      clips.get(currentIndex).tinyLoop.play(true, gain);
      loopEcho(currentIndex, 0);//<-Inside the block here = less Timer tasks!
     }
    } else {
     shiftGainWithFlush();
     gain *= E.soundMultiple;
     clips.get(currentIndex).gain.setValue(Math.max((float) -gain, -Sounds.sampledGainLimit));//<-Always set the gain, as a large distance jump (i.e. vehicle perspective change) could cause problems
     if (!clips.get(currentIndex).sampled.isRunning()) {
      clips.get(currentIndex).sampled.loop(-1);
      loopEcho(currentIndex, clips.get(currentIndex).sampled.getFramePosition());//<-Inside the block here = less Timer tasks!
     }
    }
    setEchoBalance(currentIndex);
   }
  }
 }

 private void loopEcho(int index, int framePosition) {
  if (TimerEcho.presence > 0) {
   if (clips.get(index).sampledEcho != null) {
    new Timer().schedule(new TimerTask() {
     public void run() {
      clips.get(index).sampledEcho.setFramePosition(framePosition);
      if (!clips.get(index).sampledEcho.isRunning()) {
       clips.get(index).sampledEcho.loop(-1);
      }
      cancel();
     }
    }, TimerEcho.presence);
   } else if (clips.get(index).tinyLoopEcho != null) {
    new Timer().schedule(new TimerTask() {
     public void run() {
      if (!clips.get(index).tinyLoopEcho.playing()) {
       clips.get(index).tinyLoopEcho.play(true);
      }
      cancel();
     }
    }, TimerEcho.presence);
   }
  }
 }

 public void resume(double gain) {
  resume(0, gain);
 }//Keep

 public void resume(double index, double gain) {
  if (!Match.muteSound) {
   setCurrentIndex(index);
   if (currentIndex < clips.size()) {
    if (Sounds.softwareBased) {
     if (!clips.get(currentIndex).tinyLoop.playing()) {
      gain = Sounds.decibelToLinear(-gain * E.soundMultiple);
      clips.get(currentIndex).tinyLoop.setVolume(gain);
      clips.get(currentIndex).tinyLoop.setLoop(false);
      clips.get(currentIndex).tinyLoop.rewind();
      clips.get(currentIndex).tinyLoop.resume();
      resumeEcho(currentIndex, 0);//<-Inside the block here = less Timer tasks!
     }
    } else {
     shiftGainWithFlush();
     gain *= E.soundMultiple;
     clips.get(currentIndex).gain.setValue(Math.max((float) -gain, -Sounds.sampledGainLimit));
     if (!clips.get(currentIndex).sampled.isRunning()) {
      if (clips.get(currentIndex).sampled.getFramePosition() >= clips.get(currentIndex).sampled.getFrameLength()) {
       clips.get(currentIndex).sampled.setFramePosition(0);
      }
      clips.get(currentIndex).sampled.loop(0);
      resumeEcho(currentIndex, clips.get(currentIndex).sampled.getFramePosition());//<-Inside the block here = less Timer tasks!
     }
    }
    setEchoBalance(currentIndex);
   }
  }
 }

 private void resumeEcho(int index, int framePosition) {
  if (TimerEcho.presence > 0) {
   if (clips.get(index).tinyLoopEcho != null) {
    new Timer().schedule(new TimerTask() {
     public void run() {
      if (!clips.get(index).tinyLoopEcho.playing()) {
       clips.get(index).tinyLoopEcho.resume();
      }
      cancel();
     }
    }, TimerEcho.presence);
   } else if (clips.get(index).sampledEcho != null) {
    new Timer().schedule(new TimerTask() {
     public void run() {
      clips.get(index).sampledEcho.setFramePosition(framePosition);
      if (!clips.get(index).sampledEcho.isRunning()) {
       if (clips.get(index).sampledEcho.getFramePosition() >= clips.get(index).sampledEcho.getFrameLength()) {
        clips.get(index).sampledEcho.setFramePosition(0);
       }
       clips.get(index).sampledEcho.loop(0);
      }
      cancel();
     }
    }, TimerEcho.presence);
   }
  }
 }

 private void setCurrentIndex(double index) {
  currentIndex = Double.isNaN(index) ? U.randomize(currentIndex, clips.size()) : (int) Math.round(index);
 }

 public void randomizeFramePosition(int index) {
  if (index < clips.size() && !Sounds.softwareBased) {//<-No implementation exists if on TinySound
   clips.get(index).sampled.setFramePosition(U.random(clips.get(index).sampled.getFrameLength()));
  }
 }

 private void shiftGainWithFlush() {
  if (Math.abs(clips.get(currentIndex).lastGain - clips.get(currentIndex).gain.getValue()) > 1) {
   clips.get(currentIndex).sampled.flush();
   clips.get(currentIndex).lastGain = clips.get(currentIndex).gain.getValue();
  }
 }

 private void setEchoBalance(int index) {
  if (TimerEcho.presence > 0) {
   if (clips.get(index).tinyLoopEcho != null) {
    clips.get(index).tinyLoopEcho.setPan(U.randomPlusMinus(1));
   } else if (clips.get(index).sampledEcho != null) {
    clips.get(index).echoBalance.setValue((float) U.randomPlusMinus(1));
   }
  }
 }

 public void stop() {
  int size = clips.size();
  for (int n = 0; n < size; n++) {//<-Forward loop probably safer here
   stop(n);
  }
 }

 public void stop(int index) {
  if (index < clips.size()) {
   if (clips.get(index).tinySingle != null) clips.get(index).tinySingle.stop();
   if (clips.get(index).tinyLoop != null) clips.get(index).tinyLoop.stop();
   if (clips.get(index).sampled != null) clips.get(index).sampled.stop();
   stopTimerEcho(index);
  }
 }

 private void stopTimerEcho(int index) {
  if (TimerEcho.presence > 0) {
   if (clips.get(index).tinyLoopEcho != null && clips.get(index).tinyLoopEcho.playing()) {
    new Timer().schedule(new TimerTask() {
     public void run() {
      clips.get(index).tinyLoopEcho.stop();
      cancel();
     }
    }, TimerEcho.presence);
   }
   if (clips.get(index).sampledEcho != null && clips.get(index).sampledEcho.isRunning()) {
    new Timer().schedule(new TimerTask() {
     public void run() {
      clips.get(index).sampledEcho.stop();
      cancel();
     }
    }, TimerEcho.presence);
   }
  }
 }

 public void close() {
  for (var clip : clips) {
   if (clip.sampled != null) clip.sampled.close();
   if (clip.sampledEcho != null) clip.sampledEcho.close();
   //TinySound audio all closes automatically in Sounds.close()
  }
 }
}
