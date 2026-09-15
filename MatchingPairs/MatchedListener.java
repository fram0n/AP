package matchingpairs;

/**
 *
 * Interface for objects that want to listen to matched events
 */
public interface MatchedListener {
    
    void onMatch(boolean matched);
    
}
