package ve.environment;

import ve.vehicles.Vehicle;

public enum MapBounds {
 ;
 public static double left, right, forward, backward, Y;
 public static boolean slowVehicles;

 public static void slowVehicle(Vehicle V) {
  if (slowVehicles) {
   if (V.Z > forward || V.Z < backward || V.X > right || V.X < left) {
    V.P.speedX *= .5;
    V.P.speedZ *= .5;
    V.P.speed *= .95;
   }
   if (Math.abs(V.Y) > Math.abs(Y)) {
    V.P.speedY *= .5;
    V.P.speed *= .95;
   }
  }
 }
}