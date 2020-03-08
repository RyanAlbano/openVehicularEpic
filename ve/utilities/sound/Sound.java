package ve.utilities.sound;

import ve.effects.Echo;
import ve.environment.E;
import ve.ui.Match;
import ve.utilities.U;

import java.io.*;
import java.util.*;

public class Sound {//Both TinySound and javax.sound.sampled implementations exist within this class--prepare for a read!
 private int currentIndex;
 public final List<ClipHolder> clipHolders = new ArrayList<>();

 enum Support {full, fireAndForget, loop}

 Sound() {
 }

 public Sound(String sound) {
  this(sound, Support.full);
 }

 Sound(String sound, Support support) {
  try {
   clipHolders.add(new ClipHolder(Sounds.folder + File.separator + sound + Sounds.extension, 1, support));
  } catch (Exception E) {
   System.out.println(Sounds.exception + E);
  }
  removeFailedClipHolders(support);
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
    clipHolders.add(new ClipHolder(Sounds.folder + File.separator + sound + n + Sounds.extension, ratio, support));
   } catch (Exception E) {
    if (maximumCheck < Double.POSITIVE_INFINITY) {
     System.out.println(Sounds.exception + E);
    }
    break;
   }
  }
  removeFailedClipHolders(support);
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
   clipHolders.add(new ClipHolder(Sounds.folder + File.separator + sound + Sounds.extension, ratio, support));
  } catch (Exception E) {
   System.out.println(Sounds.exception + E);
  }
  removeFailedClipHolders(support);
 }

 private void removeFailedClipHolders(Support support) {//<-Absolutely necessary for TinySound. Failed clips don't halt ClipHolder creation, so they must be removed afterwards manually (so that unusable, null etc. clips are never checked for when playing audio).
  if (Sounds.softwareBased && !clipHolders.isEmpty()) {
   for (int n = clipHolders.size(); --n >= 0; ) {
    if ((clipHolders.get(n).tinyLoop == null && support != Support.fireAndForget) || (clipHolders.get(n).tinySingle == null && support != Support.loop)) {
     clipHolders.remove(n);
    }
   }
  }
 }

 public boolean running() {
  if (Sounds.softwareBased) {
   for (ClipHolder clip : clipHolders) {
    if (clip.tinyLoop.playing()) {
     return true;
    }
   }
  } else {
   for (ClipHolder clip : clipHolders) {
    if (clip.sampled.isRunning()) {
     return true;
    }
   }
  }
  return false;
 }

 public boolean running(int index) {
  return index < clipHolders.size() && (Sounds.softwareBased ? clipHolders.get(index).tinyLoop.playing() : clipHolders.get(index).sampled.isRunning());
 }

 public void play(double gain) {
  play(0, gain);
 }

 public void play(double index, double gain) {
  if (!Match.muteSound) {
   setCurrentIndex(index);
   if (currentIndex < clipHolders.size()) {
    if (Sounds.softwareBased) {
     gain = Sounds.decibelToLinear(-gain * E.soundMultiple);
     clipHolders.get(currentIndex).tinySingle.play(gain);
    } else {
     clipHolders.get(currentIndex).sampled.stop();//<-Could add overhead. But Linux tests show it also improves sound-restart reliability!
     gain *= E.soundMultiple;
     if (gain < Sounds.sampledGainLimit) {
      clipHolders.get(currentIndex).gain.setValue((float) -gain);
      clipHolders.get(currentIndex).sampled.setFramePosition(0);
      clipHolders.get(currentIndex).sampled.loop(0);
     }
     playEcho(currentIndex);
    }
   }
  }
 }

 private void playEcho(int index) {
  if (Echo.presence > 0) {
   if (clipHolders.get(index).tinySingleEcho != null) {
    new Timer().schedule(new TimerTask() {
     public void run() {
      clipHolders.get(index).tinySingleEcho.play(Sounds.echoVolume, U.randomPlusMinus(1));
      cancel();
     }
    }, Echo.presence);
   } else if (clipHolders.get(index).sampledEcho != null) {
    new Timer().schedule(new TimerTask() {
     public void run() {
      clipHolders.get(index).sampledEcho.stop();//<-Could add overhead. But Linux tests show it also improves sound-restart reliability!
      clipHolders.get(index).echoBalance.setValue((float) U.randomPlusMinus(1));
      clipHolders.get(index).sampledEcho.setFramePosition(0);
      clipHolders.get(index).sampledEcho.loop(0);
      cancel();
     }
    }, Echo.presence);
   }
  }
 }

 public void playIfNotPlaying(double gain) {
  playIfNotPlaying(0, gain);
 }

 public void playIfNotPlaying(double index, double gain) {
  if (!Match.muteSound) {
   setCurrentIndex(index);
   if (currentIndex < clipHolders.size()) {
    if (Sounds.softwareBased) {
     if (!clipHolders.get(currentIndex).tinyLoop.playing()) {
      gain = Sounds.decibelToLinear(-gain * E.soundMultiple);
      clipHolders.get(currentIndex).tinyLoop.setLoop(false);
      clipHolders.get(currentIndex).tinyLoop.rewind();
      clipHolders.get(currentIndex).tinyLoop.play(false, gain);
     }
    } else {
     gain *= E.soundMultiple;
     if (!clipHolders.get(currentIndex).sampled.isRunning() && gain < Sounds.sampledGainLimit) {
      clipHolders.get(currentIndex).gain.setValue((float) -gain);
      clipHolders.get(currentIndex).sampled.setFramePosition(0);
      clipHolders.get(currentIndex).sampled.loop(0);
     }
    }
    playIfNotPlayingEcho(currentIndex);
   }
  }
 }

 private void playIfNotPlayingEcho(int index) {
  if (Echo.presence > 0) {
   if (clipHolders.get(index).tinyLoopEcho != null) {
    new Timer().schedule(new TimerTask() {
     public void run() {
      if (!clipHolders.get(index).tinyLoopEcho.playing()) {
       clipHolders.get(index).tinyLoopEcho.setPan(U.randomPlusMinus(1));
       clipHolders.get(currentIndex).tinyLoop.rewind();
       clipHolders.get(index).tinyLoopEcho.play(false);
      }
      cancel();
     }
    }, Echo.presence);
   } else if (clipHolders.get(index).sampledEcho != null) {
    new Timer().schedule(new TimerTask() {
     public void run() {
      if (!clipHolders.get(index).sampledEcho.isRunning()) {
       clipHolders.get(index).echoBalance.setValue((float) U.randomPlusMinus(1));
       clipHolders.get(index).sampledEcho.setFramePosition(0);
       clipHolders.get(index).sampledEcho.loop(0);
      }
      cancel();
     }
    }, Echo.presence);
   }
  }
 }

 public void loop(double gain) {
  loop(0, gain);
 }

 public void loop(double index, double gain) {
  if (!Match.muteSound) {
   setCurrentIndex(index);
   if (currentIndex < clipHolders.size()) {
    if (Sounds.softwareBased) {
     gain = Sounds.decibelToLinear(-gain * E.soundMultiple);
     clipHolders.get(currentIndex).tinyLoop.setVolume(gain);
     if (!clipHolders.get(currentIndex).tinyLoop.playing()) {
      clipHolders.get(currentIndex).tinyLoop.play(true, gain);
      loopEcho(currentIndex, 0);//<-Inside the block here = less Timer tasks!
     }
    } else {
     shiftGainWithFlush();
     gain *= E.soundMultiple;
     clipHolders.get(currentIndex).gain.setValue(Math.max((float) -gain, -Sounds.sampledGainLimit));//<-Always set the gain, as a large distance jump (i.e. vehicle perspective change) could cause problems
     if (!clipHolders.get(currentIndex).sampled.isRunning()) {
      clipHolders.get(currentIndex).sampled.loop(-1);
      loopEcho(currentIndex, clipHolders.get(currentIndex).sampled.getFramePosition());//<-Inside the block here = less Timer tasks!
     }
    }
    setEchoBalance(currentIndex);
   }
  }
 }

 private void loopEcho(int index, int framePosition) {
  if (Echo.presence > 0) {
   if (clipHolders.get(index).sampledEcho != null) {
    new Timer().schedule(new TimerTask() {
     public void run() {
      clipHolders.get(index).sampledEcho.setFramePosition(framePosition);
      if (!clipHolders.get(index).sampledEcho.isRunning()) {
       clipHolders.get(index).sampledEcho.loop(-1);
      }
      cancel();
     }
    }, Echo.presence);
   } else if (clipHolders.get(index).tinyLoopEcho != null) {
    new Timer().schedule(new TimerTask() {
     public void run() {
      if (!clipHolders.get(index).tinyLoopEcho.playing()) {
       clipHolders.get(index).tinyLoopEcho.play(true);
      }
      cancel();
     }
    }, Echo.presence);
   }
  }
 }

 public void resume(double gain) {
  resume(0, gain);
 }//Keep

 public void resume(double index, double gain) {
  if (!Match.muteSound) {
   setCurrentIndex(index);
   if (currentIndex < clipHolders.size()) {
    if (Sounds.softwareBased) {
     if (!clipHolders.get(currentIndex).tinyLoop.playing()) {
      gain = Sounds.decibelToLinear(-gain * E.soundMultiple);
      clipHolders.get(currentIndex).tinyLoop.setVolume(gain);
      clipHolders.get(currentIndex).tinyLoop.setLoop(false);
      clipHolders.get(currentIndex).tinyLoop.rewind();
      clipHolders.get(currentIndex).tinyLoop.resume();
      resumeEcho(currentIndex, 0);//<-Inside the block here = less Timer tasks!
     }
    } else {
     shiftGainWithFlush();
     gain *= E.soundMultiple;
     clipHolders.get(currentIndex).gain.setValue(Math.max((float) -gain, -Sounds.sampledGainLimit));
     if (!clipHolders.get(currentIndex).sampled.isRunning()) {
      if (clipHolders.get(currentIndex).sampled.getFramePosition() >= clipHolders.get(currentIndex).sampled.getFrameLength()) {
       clipHolders.get(currentIndex).sampled.setFramePosition(0);
      }
      clipHolders.get(currentIndex).sampled.loop(0);
      resumeEcho(currentIndex, clipHolders.get(currentIndex).sampled.getFramePosition());//<-Inside the block here = less Timer tasks!
     }
    }
    setEchoBalance(currentIndex);
   }
  }
 }

 private void resumeEcho(int index, int framePosition) {
  if (Echo.presence > 0) {
   if (clipHolders.get(index).tinyLoopEcho != null) {
    new Timer().schedule(new TimerTask() {
     public void run() {
      if (!clipHolders.get(index).tinyLoopEcho.playing()) {
       clipHolders.get(index).tinyLoopEcho.resume();
      }
      cancel();
     }
    }, Echo.presence);
   } else if (clipHolders.get(index).sampledEcho != null) {
    new Timer().schedule(new TimerTask() {
     public void run() {
      clipHolders.get(index).sampledEcho.setFramePosition(framePosition);
      if (!clipHolders.get(index).sampledEcho.isRunning()) {
       if (clipHolders.get(index).sampledEcho.getFramePosition() >= clipHolders.get(index).sampledEcho.getFrameLength()) {
        clipHolders.get(index).sampledEcho.setFramePosition(0);
       }
       clipHolders.get(index).sampledEcho.loop(0);
      }
      cancel();
     }
    }, Echo.presence);
   }
  }
 }

 private void setCurrentIndex(double index) {
  currentIndex = Double.isNaN(index) ? U.randomize(currentIndex, clipHolders.size()) : (int) Math.round(index);
 }

 public void randomizeFramePosition(int index) {
  if (index < clipHolders.size() && !Sounds.softwareBased) {//<-No implementation exists if on TinySound
   clipHolders.get(index).sampled.setFramePosition(U.random(clipHolders.get(index).sampled.getFrameLength()));
  }
 }

 private void shiftGainWithFlush() {
  if (Math.abs(clipHolders.get(currentIndex).lastGain - clipHolders.get(currentIndex).gain.getValue()) > 1) {
   clipHolders.get(currentIndex).sampled.flush();
   clipHolders.get(currentIndex).lastGain = clipHolders.get(currentIndex).gain.getValue();
  }
 }

 private void setEchoBalance(int index) {
  if (Echo.presence > 0) {
   if (clipHolders.get(index).tinyLoopEcho != null) {
    clipHolders.get(index).tinyLoopEcho.setPan(U.randomPlusMinus(1));
   } else if (clipHolders.get(index).sampledEcho != null) {
    clipHolders.get(index).echoBalance.setValue((float) U.randomPlusMinus(1));
   }
  }
 }

 public void stop() {
  int size = clipHolders.size();
  for (int n = 0; n < size; n++) {//<-Forward loop probably safer here
   stop(n);
  }
 }

 public void stop(int index) {
  if (index < clipHolders.size()) {
   if (clipHolders.get(index).tinySingle != null) clipHolders.get(index).tinySingle.stop();
   if (clipHolders.get(index).tinyLoop != null) clipHolders.get(index).tinyLoop.stop();
   if (clipHolders.get(index).sampled != null) clipHolders.get(index).sampled.stop();
   stopTimerEcho(index);
  }
 }

 private void stopTimerEcho(int index) {
  if (Echo.presence > 0) {
   if (clipHolders.get(index).tinyLoopEcho != null && clipHolders.get(index).tinyLoopEcho.playing()) {
    new Timer().schedule(new TimerTask() {
     public void run() {
      clipHolders.get(index).tinyLoopEcho.stop();
      cancel();
     }
    }, Echo.presence);
   }
   if (clipHolders.get(index).sampledEcho != null && clipHolders.get(index).sampledEcho.isRunning()) {
    new Timer().schedule(new TimerTask() {
     public void run() {
      clipHolders.get(index).sampledEcho.stop();
      cancel();
     }
    }, Echo.presence);
   }
  }
 }

 public void close() {
  for (ClipHolder holder : clipHolders) {
   if (holder.sampled != null) holder.sampled.close();
   if (holder.sampledEcho != null) holder.sampledEcho.close();
   //TinySound audio all closes automatically in Sounds.close()
  }
 }
}
