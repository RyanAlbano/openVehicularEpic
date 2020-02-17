package ve.utilities.sound;

public class Controlled extends Sound {//This is an optimized version of the Sound class, which has no support for fire-and-forget. No more than a single instance of this sound can be played at once

 public Controlled() {
 }

 public Controlled(String sound) {
  super(sound, Support.loop);
 }

 public Controlled(String sound, double maximumCheck) {
  super(sound, maximumCheck, Support.loop);
 }

 public Controlled(String sound, double maximumCheck, double ratio) {
  super(sound, maximumCheck, ratio, Support.loop);
 }

 public void addClip(String sound) {
  addClip(sound, Support.loop);
 }

 public void addClip(String sound, double ratio) {
  addClip(sound, ratio, Support.loop);
 }
}
