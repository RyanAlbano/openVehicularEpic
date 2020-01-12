package ve.utilities;

import ve.environment.*;
import ve.instances.I;
import ve.vehicles.Vehicle;

public enum Sounds {
 ;
 public static Sound checkpoint;
 public static Sound stunt;
 public static Sound finish;

 public static void clear() {//Not all sounds close
  for (Vehicle vehicle : I.vehicles) {
   vehicle.closeSounds();
  }
  Rain.sound.stop();
  Tornado.sound.stop();
  Tsunami.sound.stop();
  for (Fire.Instance fire : Fire.instances) {
   fire.closeSound();
  }
  for (Boulder.Instance boulder : Boulder.instances) {
   boulder.sound.close();
  }
  for (Meteor.Instance meteor : Meteor.instances) {
   meteor.sound.close();
  }
  if (Storm.thunder != null) Storm.thunder.close();
  if (Wind.storm != null) Wind.storm.close();
 }
}