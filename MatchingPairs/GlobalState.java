package matchingpairs;

public class GlobalState {
    private static int nPairs;
    private static boolean multiplayer;
    
  
    public static int getNPairs() {
        return nPairs;
    }
    
    public static boolean isMultiplayer() {
        return multiplayer;
    }
    
    public static void setNPairs(int n) {
        nPairs = n;
    }
    
    public static void setMultiplayer(boolean b) {
        multiplayer = b;
    }
}
