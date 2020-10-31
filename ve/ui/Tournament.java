package ve.ui;

public enum Tournament {
 ;
 public static long stage;
 public static final long[] wins = new long[2];
 public static boolean finished;

 static void reset(long setStage) {
  stage = setStage;
  wins[0] = wins[1] = 0;
  finished = false;
 }
}

