package ve.environment;

import javafx.scene.shape.*;
import org.fxyz3d.shapes.primitives.FrustumMesh;
import ve.instances.Core;
import ve.trackElements.TE;
import ve.utilities.*;


public class FrustumMound extends Core {//NOTE: Small mounds follow the vehicles around the map--'re-perspectived' vehicles may appear to have sank through these mounds while another vehicle far away had perspective!
 public final boolean wraps;
 private final boolean renderAlways;
 public final FrustumMesh mound;

 public FrustumMound(double x, double z, double y, double majorRadius, double minorRadius, double height, boolean wraps, boolean paved, boolean renderAlways) {//<-Changing method order to X,Y,Z will misplace mounds!
  while (majorRadius > 0 && minorRadius > majorRadius) {
   minorRadius *= .5;
  }
  mound = new FrustumMesh(majorRadius, minorRadius, height, wraps ? 0 : 1);
  X = x;
  Z = z;
  Y = y;
  this.wraps = wraps;
  this.renderAlways = renderAlways;
  absoluteRadius = Math.max(mound.getMajorRadius(), Math.max(mound.getMinorRadius(), mound.getHeight()));
  U.setMaterialSecurely(mound, paved ? TE.Paved.universal : Terrain.universal);
  Nodes.add(mound);
  U.rotate(mound, 0, U.random(360.));//<-For visual variation
 }

 public void runGraphics() {
  if (wraps) {
   E.wrap(this);
  }
  double moundY = Y - (mound.getHeight() * .5), depth = U.getDepth(X, moundY, Z);
  if (depth > -absoluteRadius && (renderAlways || absoluteRadius * E.renderLevel >= U.distance(this) * Camera.FOV)) {
   mound.setCullFace(objectInside(Camera.X, Camera.Y, Camera.Z) ? CullFace.NONE : CullFace.BACK);
   U.setTranslate(mound, X, moundY, Z);
   mound.setVisible(true);
  } else {
   mound.setVisible(false);
  }
 }

 public boolean objectInside(Core C) {
  return objectInside(C.X, C.Y, C.Z);
 }

 public boolean objectInside(double inX, double inY, double inZ) {
  double moundHeight = mound.getHeight(), halfHeight = moundHeight * .5;
  if (Math.abs(inY - (Y - halfHeight)) <= halfHeight) {
   double distance = U.distance(X, inX, Z, inZ),
   radiusTop = mound.getMinorRadius();
   if (distance < radiusTop) {
    return true;
   } else {
    double radiusBottom = mound.getMajorRadius();
    if (distance < radiusBottom) {
     return inY >= Y - (radiusBottom - distance) * (moundHeight / Math.abs(radiusBottom - radiusTop));
    }
   }
  }
  return false;
 }
}
