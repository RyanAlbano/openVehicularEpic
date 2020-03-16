package ve.utilities.sound;

import kuusisto.tinysound.TinySound;
import ve.effects.Echo;
import ve.environment.*;
import ve.environment.storm.Lightning;
import ve.environment.storm.Rain;
import ve.instances.I;
import ve.trackElements.Bonus;
import ve.ui.UI;
import ve.utilities.D;

public enum Sounds {
 ;
 public static final String folder = "sounds";
 public static final String extension = ".wav";
 public static final String exception = "Sound-loading Exception: ";
 public static int channels;//<-fixme--or remove if stereo's the only good way for TinySound
 public static int bitDepth;
 public static double sampleRate;
 public static boolean softwareBased;
 public static double bufferSize;
 static final long sampledGainLimit = 80;
 public static double decibelHalf = 6.020599913;
 public static double echoVolume;
 public static FireAndForget checkpoint;
 public static FireAndForget stunt;
 public static FireAndForget finish;
 //Software-based globals--this optimization only works for TinySound one-shot MemSounds. 'playIfNotPlaying' usage not supported either.
 public static FireAndForget repair;
 public static FireAndForget force;
 public static FireAndForget crashSoft, crashHard, crashDestroy;
 public static FireAndForget death, deathExplode;
 public static FireAndForget exhaust;
 public static FireAndForget hitShot;
 public static FireAndForget hitRicochet;
 public static FireAndForget hitExplosive;
 public static FireAndForget mineExplosion;
 public static FireAndForget massiveHit;
 public static FireAndForget trainNoise;//<-FireAndForget is preferred because it prevents having to store trainNoise copies per train when softwareBased
 public static FireAndForget nuke;
 public static FireAndForget nukeMax;
 //
 public static final int maxSoftwareBasedLayeredEngines = 9;//<-9 still allows 'standard' 9-tone harmonic engines to sound correctly

 public static void loadSoftwareBasedGlobals() {
  if (softwareBased) {
   repair = new FireAndForget(D.repair);
   force = new FireAndForget(D.force, Double.POSITIVE_INFINITY);
   crashSoft = new FireAndForget(D.crashSoft, Double.POSITIVE_INFINITY);
   crashHard = new FireAndForget(D.crashHard, Double.POSITIVE_INFINITY);
   crashDestroy = new FireAndForget(D.crashDestroy, Double.POSITIVE_INFINITY);
   death = new FireAndForget(D.death);
   deathExplode = new FireAndForget(D.deathExplode, Double.POSITIVE_INFINITY);
   exhaust = new FireAndForget(D.exhaust, Double.POSITIVE_INFINITY);
   hitShot = new FireAndForget(D.hitShot, Double.POSITIVE_INFINITY);
   hitRicochet = new FireAndForget(D.hitRicochet, Double.POSITIVE_INFINITY);
   hitExplosive = new FireAndForget(D.hitExplosive, Double.POSITIVE_INFINITY);
   mineExplosion = new FireAndForget(D.mineExplode);
   massiveHit = new FireAndForget(D.massiveHit, Double.POSITIVE_INFINITY);
   trainNoise = new FireAndForget(D.train, Double.POSITIVE_INFINITY);
   nuke = new FireAndForget(D.nuke, Double.POSITIVE_INFINITY);
   nukeMax = new FireAndForget(D.nukeMax);
  }
 }

 public static void removeExtraneousGlobals() {//todo--anything else here worth closing?
  if (!I.trainEngineInMatch && trainNoise != null) trainNoise.close();
  if (!I.nukeInMatch && nuke != null) nuke.close();
  if (!I.maxNukeInMatch && nukeMax != null) nukeMax.close();
 }

 public static void reset() {
  Echo.presence = 0;//<-Always reset first, so that echo is not picked up undesirably on UI sounds, etc.
  if (TinySound.mixer != null) {
   TinySound.mixer.clearMusic();
   TinySound.mixer.clearSounds();
  }
  for (var vehicle : I.vehicles) {
   if (vehicle != null) {//<-This is likely needed--there's a brief period on map-load where null 'placeholder' Vehicles are added to the list. A crash there would cause a nullPointer here as well
    vehicle.VA.close();
   }
  }
  Rain.closeSound();
  Tornado.closeSound();
  Tsunami.closeSound();
  for (var fire : Fire.instances) {
   fire.closeSound();
  }
  for (var boulder : Boulder.instances) {
   boulder.closeSound();
  }
  for (var meteor : Meteor.instances) {
   meteor.closeSound();
  }
  Lightning.closeSound();
  Wind.closeSound();
  Volcano.closeSound();
  if (UI.sound != null) UI.sound.close();
  if (checkpoint != null) checkpoint.close();
  if (stunt != null) stunt.close();
  Bonus.closeSound();
  if (finish != null) finish.close();
  UI.sound = new FireAndForget("UI", 2);
  checkpoint = new FireAndForget(D.checkpoint);
  stunt = new FireAndForget("stunt");
  Bonus.sound = new Controlled("bonus");
  finish = new FireAndForget("finish", 2);
 }

 public static double decibelToLinear(double dB) {
  return StrictMath.pow(10., dB / 20.);
 }

 public static double linearToDecibel(double linear) {//Keep
  return linear == 0 ? -144. : 20. * StrictMath.log10(linear);
 }

 public static double standardGain(double in) {
  return in * .08;
 }

 public enum gainMultiples {
  ;//These are not calculated for real-time use, and still need to be enclosed within standardGain() below
  public static final double deathExplode = .75;
  public static final double thunder = .5;
  public static final double tsunami = .5;
  public static final double nuke = .5;
  public static final double nukeMax = .25;
  public static final double volcano = .25;
 }
}