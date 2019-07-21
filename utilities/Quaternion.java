package ve.utilities;

public class Quaternion {

    double X, Y, Z, W;

    public Quaternion() {
        Z = 0;
        W = 1;
    }

    public Quaternion(double x, double y, double z, double w) {
        X = x;
        Y = y;
        Z = z;
        W = w;
    }

    public void set() {
        X = Y = Z = 0;
        W = 1;
    }

    public Quaternion set(double x, double y, double z, double w) {
        X = x;
        Y = y;
        Z = z;
        W = w;
        return this;
    }

    public void multiply(double x, double y, double z, double w) {
        set(
                W * x + X * w + Y * z - Z * y,
                W * y - X * z + Y * w + Z * x,
                W * z + X * y - Y * x + Z * w,
                W * w - X * x - Y * y - Z * z);
    }

    public Quaternion multiply(Quaternion Q) {
        return set(
                W * Q.X + X * Q.W + Y * Q.Z - Z * Q.Y,
                W * Q.Y - X * Q.Z + Y * Q.W + Z * Q.X,
                W * Q.Z + X * Q.Y - Y * Q.X + Z * Q.W,
                W * Q.W - X * Q.X - Y * Q.Y - Z * Q.Z);
    }
}
