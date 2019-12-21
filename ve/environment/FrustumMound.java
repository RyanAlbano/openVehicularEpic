package ve.environment;

import javafx.scene.shape.*;
import org.fxyz3d.shapes.primitives.FrustumMesh;
import ve.*;
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
  U.setMaterialSecurely(mound, paved ? TE.Paved.universal : E.Terrain.universal);
  U.Nodes.add(mound);
  U.rotate(mound, 0, U.random(360.));//<-For visual variation
 }

 public void runGraphics() {
  if (wraps) {
   boolean setSlope = false;
   if (Math.abs(X) < E.centerShiftOffAt) {
    X += U.random() < .5 ? E.centerShiftOffAt : -E.centerShiftOffAt;
    setSlope = true;
   }
   if (Math.abs(X - Camera.X) > 40000) {
    while (X > Camera.X + 40000) X -= 80000;
    while (X < Camera.X - 40000) X += 80000;
    setSlope = true;
   }
   if (Math.abs(Z - Camera.Z) > 40000) {
    while (Z > Camera.Z + 40000) Z -= 80000;
    while (Z < Camera.Z - 40000) Z += 80000;
    setSlope = true;
   }
   if (setSlope) {
    E.setTerrainSit(this, false);
   }
  }
  double moundY = Y - (mound.getHeight() * .5), depth = U.getDepth(X, moundY, Z);
  if (depth > -absoluteRadius && (renderAlways || absoluteRadius * E.renderLevel >= U.distance(this) * Camera.zoom)) {
   mound.setCullFace(cameraInside() ? CullFace.NONE : CullFace.BACK);
   U.setTranslate(mound, X, moundY, Z);
   mound.setVisible(true);
  } else {
   mound.setVisible(false);
  }
 }

 public boolean cameraInside() {
  double moundHeight = mound.getHeight(), halfHeight = moundHeight * .5;
  if (Math.abs(Camera.Y - (Y - halfHeight)) <= halfHeight) {
   double distance = U.distanceXZ(this),
   radiusTop = mound.getMinorRadius();
   if (distance < radiusTop) {
    return true;
   } else {
    double radiusBottom = mound.getMajorRadius();
    if (distance < radiusBottom) {
     return Y >= Y - (radiusBottom - distance) * (moundHeight / Math.abs(radiusBottom - radiusTop));
    }
   }
  }
  return false;
 }
}
