package ve.trackElements.trackParts;

import ve.Core;

public class TrackPlane extends Core {

 public double radiusX, radiusY, radiusZ, damage;
 public final double[] RGB = new double[3];
 public boolean addSpeed;
 public String type = "";
 public Wall wall = Wall.none;

 public enum Wall {none, front, back, left, right}
}
