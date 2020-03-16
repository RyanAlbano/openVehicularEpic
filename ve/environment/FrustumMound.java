package ve.environment;

import javafx.scene.shape.CullFace;
import javafx.scene.shape.TriangleMesh;
import org.fxyz3d.shapes.primitives.FrustumMesh;
import ve.instances.Core;
import ve.instances.I;
import ve.instances.InstancePart;
import ve.trackElements.TE;
import ve.utilities.Camera;
import ve.utilities.Nodes;
import ve.utilities.U;

public class FrustumMound extends Core {//NOTE: Small mounds follow the vehicles around the map--'re-perspectived' vehicles may appear to have sank through these mounds while another vehicle far away had perspective!
 public final boolean wraps;
 private final boolean renderAlways;
 private final FrustumMesh mound;
 public final double majorRadius;
 public final double minorRadius;
 public final double height;

 public FrustumMound(double x, double z, double y, double majorRadius, double minorRadius, double height, boolean wraps, boolean paved, boolean renderAlways) {//<-Changing method order to X,Y,Z will misplace mounds!
  while (majorRadius > 0 && minorRadius > majorRadius) {
   minorRadius *= .5;
  }
  //mound = new MeshView(getMesh(majorRadius, minorRadius, height, wraps ? 16 : 24));//<-Non-FXyz based frustum as a backup
  mound = new FrustumMesh(majorRadius, minorRadius, height, wraps ? 0 : 1);
  X = x;
  Z = z;
  Y = y;
  this.majorRadius = majorRadius;
  this.minorRadius = minorRadius;
  this.height = height;
  this.wraps = wraps;
  this.renderAlways = renderAlways;
  absoluteRadius = Math.max(majorRadius, Math.max(minorRadius, height));
  U.setMaterialSecurely(mound, paved ? TE.Paved.universal : Terrain.universal);
  Nodes.add(mound);
  U.rotate(mound, 0, U.random(360.));//<-For visual variation
 }

 public void setInitialSit() {
  if (wraps) {
   E.setTerrainSit(this, false);
  }
 }

 public void runGraphics() {
  if (wraps) {
   E.wrap(this);
  }
  if (U.getDepth(this) > -absoluteRadius && (renderAlways || absoluteRadius * E.renderLevel >= U.distance(this) * Camera.FOV)) {
   mound.setCullFace(objectInside(Camera.C) ? CullFace.NONE : CullFace.BACK);
   U.setTranslate(mound, X, Y - (height * .5), Z);
   mound.setVisible(true);
  } else {
   mound.setVisible(false);
  }
 }

 public boolean objectInside(Core C) {
  return objectInside(C.X, C.Y, C.Z);
 }

 private boolean objectInside(double inX, double inY, double inZ) {
  double moundHeight = height, halfHeight = moundHeight * .5;
  if (Math.abs(inY - (Y - halfHeight)) <= halfHeight) {
   double distance = U.distance(X, inX, Z, inZ),
   radiusTop = minorRadius;
   if (distance < radiusTop) {
    return true;
   } else {
    double radiusBottom = majorRadius;
    if (distance < radiusBottom) {
     return inY >= Y - (radiusBottom - distance) * (moundHeight / Math.abs(radiusBottom - radiusTop));
    }
   }
  }
  return false;
 }

 static TriangleMesh getMesh(double majorRadius, double minorRadius, double height, int divisions) {
  TriangleMesh TM = new TriangleMesh();
  //getting ring vertices
  float[] bottom = new float[divisions * 3];
  double placement = 360 / (double) divisions;
  for (int n = 0; ; n += 3) {
   try {
    bottom[n] = (float) (majorRadius * U.sin(placement * n / 3));
    bottom[n + 2] = (float) (majorRadius * U.cos(placement * n / 3));
   } catch (RuntimeException E) {
    break;
   }
  }
  TM.getPoints().addAll(bottom);
  float[] top = new float[divisions * 3];
  for (int n = 0; ; n += 3) {
   try {
    top[n] = (float) (minorRadius * U.sin(placement * n / 3));
    top[n + 1] = (float) -height;
    top[n + 2] = (float) (minorRadius * U.cos(placement * n / 3));
   } catch (RuntimeException E) {
    break;
   }
  }
  TM.getPoints().addAll(top);
  //adding center points for bottom/top
  TM.getPoints().addAll(0, 0, 0);
  TM.getPoints().addAll(0, (float) -height, 0);
  //adding faces
  int baseBottom = divisions << 1;//<-divisions * 2
  for (int n = 0; n < divisions; n++) {
   int up1 = n + 1;
   if (up1 >= divisions) {
    up1 -= divisions;
   }
   TM.getFaces().addAll(n, n, up1, up1, baseBottom, baseBottom);//<-Counterclockwise front
   //TM.getFaces().addAll(up1, up1, n, n, baseBottom, baseBottom);//<-Clockwise back
  }
  //TM.getFaces().addAll(divisions, divisions, 0, 0, baseBottom, baseBottom);
  int baseTop = baseBottom + 1;
  for (int n = divisions; n < baseBottom; n++) {
   int up1 = n + 1;
   if (up1 >= baseBottom) {
    up1 -= divisions;
   }
   TM.getFaces().addAll(up1, up1, n, n, baseTop, baseTop);//<-Counterclockwise front
   //TM.getFaces().addAll(n, n, up1, up1, baseTop, baseTop);//<-Clockwise back
  }
  //TM.getFaces().addAll(divisions * 2, divisions * 2, divisions * 2 - 1, divisions * 2 - 1, baseTop, baseTop);
  //textureCoordinates
  float[] textureCoordinates = U.random() < .5 ? I.textureCoordinateBase0 : I.textureCoordinateBase1;
  for (long n = 0; n < (baseBottom + 2) / (double) 3; n++) {
   TM.getTexCoords().addAll(textureCoordinates);//<-'addAll' and NOT 'setAll'
  }
  InstancePart.setCylindric(TM, baseBottom, null);
  return TM;
 }
}
