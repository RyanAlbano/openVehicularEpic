package ve.ui.options;

public enum Units {
 ;
 public static Unit units;

 public enum Unit {VEs, metric, US}

 public static String getDistanceName() {
  return units == Unit.VEs ? Unit.VEs.name() : units == Unit.metric ? "Meters" : "Feet";
 }

 public static String getSpeedName() {
  return units == Unit.VEs ? Unit.VEs.name() : units == Unit.metric ? "Kph" : "Mph";
 }

 public static double getDistance(double in) {
  return in * (units == Unit.VEs ? 1 : units == Unit.metric ? .0175 : .0574147);
 }

 public static double getSpeed(double in) {
  return in * (units == Unit.VEs ? 1 : units == Unit.metric ? .5364466667 : 1 / 3.);
 }

 static void cycle() {
  if (units == Unit.VEs) {
   units = Unit.metric;
  } else if (units == Unit.metric) {
   units = Unit.US;
  } else if (units == Unit.US) {
   units = Unit.VEs;
  }
 }
}

