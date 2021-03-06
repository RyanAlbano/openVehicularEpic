package ve.utilities.sound;

import kuusisto.tinysound.TinySound;
import ve.effects.Echo;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

class ClipHolder {

 Clip sampled, sampledEcho;
 FloatControl gain, echoBalance;
 double lastGain;
 kuusisto.tinysound.Sound tinySingle;
 kuusisto.tinysound.Music tinyLoop;
 kuusisto.tinysound.Sound tinySingleEcho;
 kuusisto.tinysound.Music tinyLoopEcho;

 ClipHolder(String sound, double ratio, Sound.Support support) throws Exception {
  try (AudioInputStream AIS = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(new File(sound))))) {
   AudioFormat AF = AIS.getFormat();
   AudioInputStream AIS2 = AudioSystem.getAudioInputStream(
   new AudioFormat(AF.getFrameRate(), Sounds.bitDepth, 2, true, AF.isBigEndian()), AIS);//<-TinySound seems to degenerate if 2 channels is not hard-forced
   AudioFormat convertedAF = AIS2.getFormat();
   AudioInputStream convertedAIS = new AudioInputStream(AIS2,
   new AudioFormat((float) (AF.getFrameRate() * ratio), convertedAF.getSampleSizeInBits(), 2, true, convertedAF.isBigEndian()),
   AIS2.getFrameLength());
   if (Sounds.softwareBased) {
    String temp = "temp";
    File F = new File(temp + File.separator + temp + Sounds.extension);
    AudioSystem.write(convertedAIS, AudioFileFormat.Type.WAVE, F);
    tinySingle = support == Sound.Support.loop ? null : TinySound.loadSound(F);
    tinyLoop = support == Sound.Support.fireAndForget ? null : TinySound.loadMusic(F);
    if (Echo.presence > 0 && Sounds.useEcho) {
     tinySingleEcho = support == Sound.Support.loop ? null : TinySound.loadSound(F);
     if (support != Sound.Support.fireAndForget) {
      tinyLoopEcho = TinySound.loadMusic(F);
      tinyLoopEcho.setVolume(Sounds.echoVolume);
     }
    }
   } else {
    sampled = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
    sampled.open(convertedAIS);
    gain = (FloatControl) sampled.getControl(FloatControl.Type.MASTER_GAIN);
   }
  }
  if (Echo.presence > 0 && Sounds.useEcho && !Sounds.softwareBased) {
   try (AudioInputStream AIS = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(new File(sound))))) {
    AudioFormat AF = AIS.getFormat();
    AudioInputStream AIS2 = AudioSystem.getAudioInputStream(
    new AudioFormat(AF.getFrameRate(), Sounds.bitDepth, 2, true, AF.isBigEndian()), AIS);
    AudioFormat convertedAF = AIS2.getFormat();
    AudioInputStream convertedAIS = new AudioInputStream(AIS2,
    new AudioFormat((float) (AF.getFrameRate() * ratio), convertedAF.getSampleSizeInBits(), 2, true, convertedAF.isBigEndian()),
    AIS2.getFrameLength());
    sampledEcho = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
    sampledEcho.open(convertedAIS);
    FloatControl gainEcho = (FloatControl) sampledEcho.getControl(FloatControl.Type.MASTER_GAIN);
    echoBalance = (FloatControl) sampledEcho.getControl(FloatControl.Type.BALANCE);
    gainEcho.setValue(-Echo.presence * .05f);
   }
  }
 }
}