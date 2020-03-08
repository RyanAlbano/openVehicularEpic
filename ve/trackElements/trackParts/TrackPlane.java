package ve.trackElements.trackParts;

import javafx.scene.paint.Color;
import ve.instances.CoreAdvanced;
import ve.utilities.U;

public class TrackPlane extends CoreAdvanced {

 public double radiusX, radiusY, radiusZ, damage = 1;
 public Color RGB = U.getColor(0);
 public boolean addSpeed;
 public String type = "";
 public Wall wall = Wall.none;

 public enum Wall {none, front, back, left, right}
}
