package ve;

import java.io.*;
import javax.sound.sampled.*;
import static ve.VE.*;
import ve.utilities.U;

public class Sound {

 public Clip clip;
 private FloatControl masterGain;
 private double lastGain;

 public Sound(String s) throws Exception {
  this(s, 1);
 }

 public Sound(String s, double rate) throws Exception {
  try (AudioInputStream AIS = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(new File(U.soundFolder + File.separator + s + U.soundExtension))))) {
   AudioFormat AF = AIS.getFormat();
   AudioInputStream AIS2 = AudioSystem.getAudioInputStream(new AudioFormat(AF.getFrameRate(), Math.min(AF.getSampleSizeInBits(), degradedSoundEffects ? 8 : 16), Math.min(AF.getChannels(), degradedSoundEffects ? 1 : 2), true, AF.isBigEndian()), AIS);
   float setRate = (float) (AF.getFrameRate() * rate);
   AF = AIS2.getFormat();
   AudioInputStream convertedAIS = new AudioInputStream(AIS2, new AudioFormat(setRate, AF.getSampleSizeInBits(), AF.getChannels(), true, AF.isBigEndian()), AIS2.getFrameLength());
   clip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, convertedAIS.getFormat()));
   clip.open(convertedAIS);
   masterGain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
  }
 }

 public boolean running() {
  return clip.isRunning();
 }

 public void play(double gain) {
  if (!VE.muteSound) {
   clip.stop();
   if (gain < 80) {
    masterGain.setValue((float) -gain);
    clip.setFramePosition(0);
    clip.loop(0);
   }
  }
 }

 public void playIfNotPlaying(double gain) {
  if (!VE.muteSound && !clip.isRunning() && gain < 80) {
   masterGain.setValue((float) -gain);
   clip.setFramePosition(0);
   clip.loop(0);
  }
 }

 public void loop(double gain) {
  if (!VE.muteSound) {
   if (Math.abs(lastGain - masterGain.getValue()) > 1) {
    clip.flush();
    lastGain = masterGain.getValue();
   }
   masterGain.setValue(Math.max((float) -gain, -80));
   if (gain < 80 && !clip.isRunning()) {
    clip.loop(-1);
   }
  }
 }

 public void resume(double gain) {
  if (!VE.muteSound) {
   if (Math.abs(lastGain - masterGain.getValue()) > 1) {
    clip.flush();
    lastGain = masterGain.getValue();
   }
   masterGain.setValue(Math.max((float) -gain, -80));
   if (gain < 80 && !clip.isRunning()) {
    if (clip.getFramePosition() >= clip.getFrameLength()) {
     clip.setFramePosition(0);
    }
    clip.loop(0);
   }
  }
 }

 public void stop() {
  if (clip.isRunning()) {
   clip.stop();
  }
 }

 public void close() {
  clip.close();
 }
}
