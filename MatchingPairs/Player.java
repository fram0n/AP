package matchingpairs;

public class Player {
    
    public Player(int id) {
        this.id = id;
        this.matchedPairs = 0;
    }
    
    /**
     * 
     * @return the player id
     */
    public int getId() {
        return id;
    }
    
    /**
     * 
     * @return the number of pairs matched by the player
     */
    public int getMatchedPairs() {
        return matchedPairs;
    }
    
    /**
     * Resets the number of pairs matched to 0
     */
    public void resetMatchedPairs() {
        matchedPairs = 0;
    }
    
    /**
     * Increments the number of pairs matched by 1
     */
    public void incrementMatchedPairs() {
        matchedPairs++;
    }
    
    // player id
    private final int id;
    // number of pairs matched by the player
    private int matchedPairs;
}
