package matchingpairs;

/**
 * Interface for objects that want to listen to the game reset event
 * 
 * This interface was created to implement the Observer design pattern,
 * allowing the Board to notify the Controller of a game reset without
 * directly invoking its reset methods.
 */
public interface GameResetListener {
    /**
     * Called when the game is reset
     */
    void onGameReset();
}
