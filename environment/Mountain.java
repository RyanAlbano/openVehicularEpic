package ve.environment;

import javafx.scene.shape.*;
import ve.utilities.U;

public class Mountain extends MeshView {

 public double X, Z;

 public void run() {
  double depth = U.getDepth(X, 0, Z);
  if (depth > -50000) {
   setCullFace(depth > 50000 ? CullFace.BACK : CullFace.NONE);
   U.setTranslate(this, X, 0, Z);
   setVisible(true);
  } else {
   setVisible(false);
  }
 }
}
