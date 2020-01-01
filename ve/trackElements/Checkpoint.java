package ve.trackElements;

import ve.instances.Core;

public class Checkpoint extends Core {

 public Type type;
 public int location;

 public enum Type {passZ, passX, passAny}
}