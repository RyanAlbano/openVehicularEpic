package ve;

import ve.utilities.U;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;

public class Sound {

 static class ClipVE {

  Clip clip;
  FloatControl gain;
  double lastGain;

  ClipVE(String sound, double ratio) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
   try (AudioInputStream AIS = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(new File(sound))))) {
    AudioFormat AF = AIS.getFormat();
    AudioInputStream AIS2 = AudioSystem.getAudioInputStream(new AudioFormat(AF.getFrameRate(), Math.min(AF.getSampleSizeInBits(), VE.degradedSoundEffects ? 8 : 16), Math.min(AF.getChannels(), VE.degradedSoundEffects ? 1 : 2), true, AF.isBigEndian()), AIS);
    float setRate = (float) (AF.getFrameRate() * ratio);//<-'AF' gets changed--do NOT inline!
    AF = AIS2.getFormat();
    AudioInputStream convertedAIS = new AudioInputStream(AIS2, new AudioFormat(setRate, AF.getSampleSizeInBits(), AF.getChannels(), true, AF.isBigEndian()), AIS2.getFrameLength());
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

 public boolean running() {//Keep
  return running(0);
 }

 public boolean running(int index) {
  return index < clips.size() && clips.get(index).clip.isRunning();
 }

 public void play(double gain) {
  play(0, gain);
 }

 public void play(int index, double gain) {
  if (!VE.muteSound && index < clips.size()) {
   clips.get(index).clip.stop();
   if (gain < 80) {
    clips.get(index).gain.setValue((float) -gain);
    clips.get(index).clip.setFramePosition(0);
    clips.get(index).clip.loop(0);
   }
  }
 }

 public void playIfNotPlaying(double gain) {
  playIfNotPlaying(0, gain);
 }

 public void playIfNotPlaying(int index, double gain) {
  if (!VE.muteSound && index < clips.size() && !clips.get(index).clip.isRunning() && gain < 80) {
   clips.get(index).gain.setValue((float) -gain);
   clips.get(index).clip.setFramePosition(0);
   clips.get(index).clip.loop(0);
  }
 }

 public void loop(double gain) {
  loop(0, gain);
 }

 public void loop(int index, double gain) {
  if (!VE.muteSound && index < clips.size()) {
   if (Math.abs(clips.get(index).lastGain - clips.get(index).gain.getValue()) > 1) {
    clips.get(index).clip.flush();
    clips.get(index).lastGain = clips.get(index).gain.getValue();
   }
   clips.get(index).gain.setValue(Math.max((float) -gain, -80));
   if (gain < 80 && !clips.get(index).clip.isRunning()) {
    clips.get(index).clip.loop(-1);
   }
  }
 }

 public void resume(double gain) {//Keep
  resume(0, gain);
 }

 public void resume(int n, double gain) {
  if (!VE.muteSound && n < clips.size()) {
   if (Math.abs(clips.get(n).lastGain - clips.get(n).gain.getValue()) > 1) {
    clips.get(n).clip.flush();
    clips.get(n).lastGain = clips.get(n).gain.getValue();
   }
   clips.get(n).gain.setValue(Math.max((float) -gain, -80));
   if (gain < 80 && !clips.get(n).clip.isRunning()) {
    if (clips.get(n).clip.getFramePosition() >= clips.get(n).clip.getFrameLength()) {
     clips.get(n).clip.setFramePosition(0);
    }
    clips.get(n).clip.loop(0);
   }
  }
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
