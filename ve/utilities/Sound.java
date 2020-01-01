package ve.utilities;

import ve.environment.E;
import ve.ui.Match;
import ve.ui.Options;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;

public class Sound {
 private int currentIndex;

 public static double standardDistance(double in) {
  return in * .08;
 }

 static class ClipVE {

  Clip clip;
  FloatControl gain;
  double lastGain;

  ClipVE(String sound, double ratio) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
   try (AudioInputStream AIS = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(new File(sound))))) {
    AudioFormat AF = AIS.getFormat();
    AudioInputStream AIS2 = AudioSystem.getAudioInputStream(new AudioFormat
    (AF.getFrameRate(),
    Math.min(AF.getSampleSizeInBits(), Options.degradedSoundEffects ? 8 : 16),
    Math.min(AF.getChannels(), Options.degradedSoundEffects ? 1 : 2),
    true,
    AF.isBigEndian()),
    AIS);
    AudioFormat convertedAF = AIS2.getFormat();
    AudioInputStream convertedAIS = new AudioInputStream(AIS2, new AudioFormat(
    (float) (AF.getFrameRate() * ratio),
    convertedAF.getSampleSizeInBits(),
    convertedAF.getChannels(), true,
    convertedAF.isBigEndian()),
    AIS2.getFrameLength());
    clip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, convertedAIS.getFormat()));
    clip.open(convertedAIS);
    gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
   }
  }
 }

 public final List<ClipVE> clips = new ArrayList<>();

 public Sound() {
 }

 public Sound(String sound) {
  try {
   clips.add(new ClipVE(U.soundFolder + File.separator + sound + U.soundExtension, 1));
  } catch (Exception E) {
   System.out.println(U.soundLoadingException + E);
  }
 }

 public Sound(String sound, double maximumCheck) {
  this(sound, maximumCheck, 1);
 }

 public Sound(String sound, double maximumCheck, double ratio) {
  for (int n = 0; n < maximumCheck; n++) {
   try {
    clips.add(new ClipVE(U.soundFolder + File.separator + sound + n + U.soundExtension, ratio));
   } catch (Exception E) {
    if (maximumCheck < Double.POSITIVE_INFINITY) {
     System.out.println(U.soundLoadingException + E);
    }
    break;
   }
  }
 }

 public void addClip(String sound, double ratio) {
  try {
   clips.add(new ClipVE(U.soundFolder + File.separator + sound + U.soundExtension, ratio));
  } catch (Exception E) {
   System.out.println(U.soundLoadingException + E);
  }
 }

 public boolean running() {
  for (ClipVE clip : clips) {
   if (clip.clip.isRunning()) {
    return true;
   }
  }
  return false;
 }

 public boolean running(int index) {
  return index < clips.size() && clips.get(index).clip.isRunning();
 }

 public void play(double gain) {
  play(0, gain);
 }

 public void play(double index, double gain) {
  if (!Match.muteSound) {
   setCurrentIndex(index);
   if (currentIndex < clips.size()) {
    clips.get(currentIndex).clip.stop();
    if (gain < 80) {
     clips.get(currentIndex).gain.setValue((float) -gain);
     clips.get(currentIndex).clip.setFramePosition(0);
     clips.get(currentIndex).clip.loop(0);
    }
   }
  }
 }

 public void playIfNotPlaying(double gain) {
  playIfNotPlaying(0, gain);
 }

 public void playIfNotPlaying(double index, double gain) {
  if (!Match.muteSound) {
   setCurrentIndex(index);
   gain *= E.soundMultiple;
   if (currentIndex < clips.size() && !clips.get(currentIndex).clip.isRunning() && gain < 80) {
    clips.get(currentIndex).gain.setValue((float) -gain);
    clips.get(currentIndex).clip.setFramePosition(0);
    clips.get(currentIndex).clip.loop(0);
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
    if (Math.abs(clips.get(currentIndex).lastGain - clips.get(currentIndex).gain.getValue()) > 1) {
     clips.get(currentIndex).clip.flush();
     clips.get(currentIndex).lastGain = clips.get(currentIndex).gain.getValue();
    }
    gain *= E.soundMultiple;
    clips.get(currentIndex).gain.setValue(Math.max((float) -gain, -80));
    if (gain < 80 && !clips.get(currentIndex).clip.isRunning()) {
     clips.get(currentIndex).clip.loop(-1);
    }
   }
  }
 }

 public void resume(double gain) {//Keep
  resume(0, gain);
 }

 public void resume(double index, double gain) {
  if (!Match.muteSound) {
   setCurrentIndex(index);
   if (currentIndex < clips.size()) {
    if (Math.abs(clips.get(currentIndex).lastGain - clips.get(currentIndex).gain.getValue()) > 1) {
     clips.get(currentIndex).clip.flush();
     clips.get(currentIndex).lastGain = clips.get(currentIndex).gain.getValue();
    }
    gain *= E.soundMultiple;
    clips.get(currentIndex).gain.setValue(Math.max((float) -gain, -80));
    if (gain < 80 && !clips.get(currentIndex).clip.isRunning()) {
     if (clips.get(currentIndex).clip.getFramePosition() >= clips.get(currentIndex).clip.getFrameLength()) {
      clips.get(currentIndex).clip.setFramePosition(0);
     }
     clips.get(currentIndex).clip.loop(0);
    }
   }
  }
 }

 private void setCurrentIndex(double index) {
  currentIndex = Double.isNaN(index) ? U.randomize(currentIndex, clips.size()) : (int) Math.round(index);
 }

 public void randomizeFramePosition(int index) {
  if (index < clips.size()) {
   clips.get(index).clip.setFramePosition(U.random(clips.get(index).clip.getFrameLength()));
  }
 }

 public void stop() {
  for (ClipVE clip : clips) {
   clip.clip.stop();
  }
 }

 public void stop(int index) {
  if (index < clips.size() && clips.get(index).clip.isRunning()) {
   clips.get(index).clip.stop();
  }
 }

 public void close() {
  for (ClipVE clip : clips) {
   clip.clip.close();
  }
 }
}
