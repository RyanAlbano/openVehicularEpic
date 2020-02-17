package ve.utilities.sound;

import kuusisto.tinysound.TinySound;
import ve.effects.echo.TimerEcho;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

class ClipVE {

 Clip sampled, sampledEcho;
 FloatControl gain, echoBalance;
 double lastGain;
 kuusisto.tinysound.Sound tinySingle;
 kuusisto.tinysound.Music tinyLoop;
 kuusisto.tinysound.Sound tinySingleEcho;
 kuusisto.tinysound.Music tinyLoopEcho;

 ClipVE(String sound, double ratio, Sound.Support support) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
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
    if (TimerEcho.presence > 0) {
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
  if (TimerEcho.presence > 0 && !Sounds.softwareBased) {
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
    gainEcho.setValue(-TimerEcho.presence * .05f);
   }
  }
 }
}