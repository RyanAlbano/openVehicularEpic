package ve.utilities;

import ve.environment.*;
import ve.environment.storm.Lightning;
import ve.environment.storm.Rain;
import ve.instances.I;
import ve.vehicles.Vehicle;

public enum Sounds {
 ;
 public static Sound checkpoint;
 public static Sound stunt;
 public static Sound finish;

 public static void clear() {//Not all sounds close
  for (Vehicle vehicle : I.vehicles) {
   if (vehicle != null) {//<-This is likely needed--there's a brief period on map-load where null 'placeholder' Vehicles are added to the list. A crash there would cause a nullPointer here as well
    vehicle.closeSounds();
   }
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
  if (Lightning.thunder != null) Lightning.thunder.close();
  if (Wind.storm != null) Wind.storm.close();
 }
}