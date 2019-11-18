package ve.trackElements.trackParts;

import javafx.scene.paint.Color;
import ve.Core;
import ve.utilities.U;

public class TrackPlane extends Core {

 public double radiusX, radiusY, radiusZ, damage;
 public Color RGB = U.getColor(0);
 public boolean addSpeed;
 public String type = "";
 public Wall wall = Wall.none;

 public enum Wall {none, front, back, left, right}
}
