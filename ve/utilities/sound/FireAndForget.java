package ve.utilities.sound;

public class FireAndForget extends Sound {//This is an optimized version of the Sound class, which has no support for frame-position, looping, etc.

 public FireAndForget() {
 }

 public FireAndForget(String sound) {
  super(sound, Support.fireAndForget);
 }

 public FireAndForget(String sound, double maximumCheck) {
  super(sound, maximumCheck, Support.fireAndForget);
 }

 public FireAndForget(String sound, double maximumCheck, double ratio) {
  super(sound, maximumCheck, ratio, Support.fireAndForget);
 }

 public void addClip(String sound) {
  addClip(sound, Support.fireAndForget);
 }

 public void addClip(String sound, double ratio) {
  addClip(sound, ratio, Support.fireAndForget);
 }
}
