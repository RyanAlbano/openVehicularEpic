package ve.vehicles;

import java.util.*;

import ve.Core;
import ve.effects.*;

public class Wheel extends Core {
 int currentSkidmark;
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
 final List<Skidmark> skidmarks = new ArrayList<>();
}
