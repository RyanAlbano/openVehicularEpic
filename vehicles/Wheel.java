package ve.vehicles;

import java.util.*;

import ve.effects.*;

public class Wheel {
 int currentSkidmark;
 public double X;
 public double Y;
 public double Z;
 public double pointX;
 public double pointZ;
 public double speedX;
 public double speedY;
 public double speedZ;
 public double YZ;
 public double XY;
 public double hitOtherX;
 public double hitOtherZ;
 public double skidmarkSize;
 public double minimumY;
 public double sparkPoint;
 public final double[] terrainRGB = new double[3];
 boolean angledSurface, againstWall;
 final List<Spark> sparks = new ArrayList<>();
 final List<Skidmark> skidmarks = new ArrayList<>();
}
