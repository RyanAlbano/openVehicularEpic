package ve.trackElements;

import ve.Core;

public class Point extends Core {

 public Type type;

 public enum Type {mustPassIfClosest, mustPassAbsolute, checkpoint}
}
