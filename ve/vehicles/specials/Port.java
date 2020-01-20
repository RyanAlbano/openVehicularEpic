package ve.vehicles.specials;

import ve.instances.CoreAdvanced;

import java.util.ArrayList;
import java.util.List;

public class Port extends CoreAdvanced {

 public Spit spit;
 public List<PortSmoke> smokes;
 int currentSmoke;
 static final long defaultSmokeQuantity = 50;

 void addSmokes(Special special) {
  smokes = new ArrayList<>();
  for (long n = defaultSmokeQuantity; --n >= 0; ) {
   smokes.add(new PortSmoke(special, this));
  }
 }
}
