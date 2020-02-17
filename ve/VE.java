/*
OPEN VEHICULAR EPIC

HEAD DEVELOPER: Ryan Albano
(PAST) ASSISTANT DEVS: Vitor Macedo, Dany Fernández Diaz

PROJECT NAMING CONVENTIONS (variable names are usually typed as follows):

wordWord

For example, 'current checkpoint' would be typed as 'currentCheckpoint'.

Successive capital letters are usually abbreviations, such as 'PhongMaterial PM' or 'FileInputStream FIS';

Methods beginning with 'run' are usually called recursively per frame, i.e. 'runGraphics()'.

A note on Enums:
Enums are used throughout this project. They are often used instead of classes to ensure there's only ever one copy of the 'class' in existence.
The IDE may determine some enum fields as unused. Be sure to NOT remove any of them! (They become utilized when the game loads vehicles, etc.)
*/
package ve;

import ve.ui.UI;

enum VE {
 ;

 public static void main(String[] s) {
  UI.run(s);
 }
 //FIXME--Mini Cooper and the Love of God model files can fail to load on Linux?
 //todo--Use repair singularity instead of ring/diamond?
}
