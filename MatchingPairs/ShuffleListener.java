package matchingpairs;

/**
 * Interface for objects that want to listen to shuffle events
 */
public interface ShuffleListener {
    /**
     * Called when a shuffle event occurs
     * 
     * @param cardValues list of all the cards
     */
    void onShuffle(java.util.Map<Card, Integer> cardValues);
}
