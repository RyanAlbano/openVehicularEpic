package ve.effects;

import javafx.scene.image.Image;
import ve.utilities.Images;
import ve.utilities.U;

public enum Effects {
 ;

 public static Image blink() {
  return Images.blink.get(U.random(Images.blink.size()));
 }

 public static Image fireLight() {
  return Images.fireLight.get(U.random(Images.fireLight.size()));
 }

 public static Image blueJet() {
  return Images.blueJet.get(U.random(Images.blueJet.size()));
 }
}
