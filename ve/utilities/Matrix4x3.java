package ve.utilities;

public class Matrix4x3 extends javafx.scene.transform.Affine {

 public void set(Quaternion q) {
  double w2 = q.W * q.W,
  x2 = q.X * q.X,
  y2 = q.Y * q.Y,
  z2 = q.Z * q.Z,
  zw = q.Z * q.W,
  dzw = zw + zw,
  xy = q.X * q.Y,
  dxy = xy + xy,
  xz = q.X * q.Z,
  dxz = xz + xz,
  yw = q.Y * q.W,
  dyw = yw + yw,
  yz = q.Y * q.Z,
  dyz = yz + yz,
  xw = q.X * q.W,
  dxw = xw + xw;
  setMxx(w2 + x2 - z2 - y2);
  setMxy(dxy + dzw);
  setMxz(dxz - dyw);
  setMyx(dxy - dzw);
  setMyy(y2 - z2 + w2 - x2);
  setMyz(dyz + dxw);
  setMzx(dyw + dxz);
  setMzy(dyz - dxw);
  setMzz(z2 - y2 - x2 + w2);
  setTx(0);
  setTy(0);
  setTz(0);
 }
}
