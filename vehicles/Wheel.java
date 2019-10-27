package ve.vehicles;

import java.util.*;

import ve.Core;
import ve.effects.*;
import ve.utilities.U;

public class Wheel extends Core {
 private final Vehicle V;
 private int currentSkidmark;
 double pointX, pointZ;
 public double speedX, speedY, speedZ;
 double vibrate;
 double hitOtherX, hitOtherZ;
 double skidmarkSize;
 double minimumY;
 double sparkPoint;
 public final double[] terrainRGB = new double[3];
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
  double sparkSpeed = U.netValue(speedX, speedY, speedZ);
  for (Spark spark : sparks) {
   if (U.random() < .25) {
    spark.deploy(sparkX, sparkY, sparkZ, sparkSpeed);
   }
  }
  V.scraping = true;
 }

 void skidmark(boolean forSnow) {
  if (skidmarks != null && !V.flipped && !V.destroyed && !V.phantomEngaged && ((forSnow && V.netSpeed > 0) || Math.abs(Math.abs(V.speed) - U.netValue(speedX, speedZ)) > 10 + U.random(5.) || Math.abs(V.speed) > 50 + U.random(50.))) {
   skidmarks.get(currentSkidmark).deploy(V, this, forSnow);
   currentSkidmark = ++currentSkidmark >= skidmarks.size() ? 0 : currentSkidmark;
  }
  minimumY = V.localVehicleGround;
 }
}
