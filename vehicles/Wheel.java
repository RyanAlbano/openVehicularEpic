package ve.vehicles;

import java.util.*;

import ve.Core;
import ve.effects.*;
import ve.utilities.U;

public /*Must be public, or fails in Tsunami!*/class Wheel extends Core {
 private final Vehicle V;
 private int currentSkidmark;
 double pointX, pointZ;
 double vibrateY;
 double skidmarkSize;
 double minimumY;
 double sparkPoint;
 boolean angledSurface, againstWall;
 final Collection<Spark> sparks = new ArrayList<>();
 List<Skidmark> skidmarks;

 Wheel(Vehicle vehicle) {
  V = vehicle;
 }

 void sparks(boolean grounded) {
  double sparkX, sparkY, sparkZ;
  if (grounded) {
   sparkX = X;
   sparkY = Y;
   sparkZ = Z;
  } else {
   double[] rotateX = {pointX}, rotateY = {V.clearanceY - sparkPoint}, rotateZ = {pointZ};
   U.rotate(rotateX, rotateY, V.XY);
   U.rotate(rotateY, rotateZ, V.YZ);
   U.rotate(rotateX, rotateZ, V.XZ);
   sparkX = rotateX[0] + V.X;
   sparkY = rotateY[0] + V.Y;
   sparkZ = rotateZ[0] + V.Z;
  }
  double sparkSpeed = V.P.netSpeed;
  for (Spark spark : sparks) {
   if (U.random() < .25) {
    spark.deploy(sparkX, sparkY, sparkZ, sparkSpeed);
   }
  }
  V.VA.scraping = true;
 }

 void skidmark(boolean forSnow) {
  if (skidmarks != null && !V.P.flipped && !V.destroyed && !V.phantomEngaged && ((forSnow && V.P.netSpeed > 0) || Math.abs(Math.abs(V.P.speed) - V.P.netSpeed) > 10 + U.random(5.) || Math.abs(V.P.speed) > 50 + U.random(50.))) {
   skidmarks.get(currentSkidmark).deploy(V, this, forSnow);
   currentSkidmark = ++currentSkidmark >= skidmarks.size() ? 0 : currentSkidmark;
  }
  minimumY = V.P.localGround;
 }
}
