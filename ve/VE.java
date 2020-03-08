/*
OPEN VEHICULAR EPIC

HEAD DEVELOPER: Ryan Albano
(PAST) ASSISTANT DEVS: Vitor Macedo, Dany Fernández Diaz

PROJECT NAMING CONVENTIONS (variable names are usually typed as follows):

wordWord

For example, 'current checkpoint' would be typed as 'currentCheckpoint'.

Successive capital letters are usually abbreviations, such as 'PhongMaterial PM' or 'FileInputStream FIS';

Methods beginning with 'run' are usually called recursively per frame, i.e. 'runLogic()'.

A note on Enums:
Enums are used throughout this project. They are used instead of classes (when possible) to ensure there's only ever one copy of the 'class' in existence.
The IDE may determine some enum fields as unused. Be sure to NOT remove any of them! (They become utilized when the game loads vehicles, etc.)
*/
package ve;

import ve.ui.UI;

enum VE {
 ;

 public static void main(String[] s) {
  System.setProperty("sun.java2d.opengl", "true");//<-Is this even necessary?
  System.setProperty("javafx.animation.fullspeed", "true");//<-Capping the FPS manually seems to actually do a better job than the AnimationTimer at 75Hz--this also lets the user max out the FPS, which is great
  //Properties must be set BEFORE this point!
  UI.run(s);
 }
 //FIXME--Mini Cooper and the Love of God model files can fail to load on Linux?
 //TODO--Bring 'var' back and update everything to Java 13--PointLights are verified to work--we'll probably never need Java 8 again
 //DON'T FORGET TO UPDATE THE .JAR!
}
