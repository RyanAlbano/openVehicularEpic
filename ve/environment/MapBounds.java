package ve.environment;

import ve.vehicles.Vehicle;

public enum MapBounds {//todo--Bots don't recognize that map bounds be treated as a wall!
 ;
 public static double left, right, forward, backward, Y;
 public static boolean slowVehicles;

 public static void slowVehicle(Vehicle V) {
  if (slowVehicles) {
   if (V.Z > forward || V.Z < backward || V.X > right || V.X < left) {
    V.speedX *= .5;
    V.speedZ *= .5;
    V.P.speed *= .95;
   }
   if (Math.abs(V.Y) > Math.abs(Y)) {
    V.speedY *= .5;
    V.P.speed *= .95;
   }
  }
 }

 static void reset() {
  left = backward = Y = Double.NEGATIVE_INFINITY;
  right = forward = Double.POSITIVE_INFINITY;
  slowVehicles = false;
 }
}