package matchingpairs;

import java.beans.*;
import java.util.*;

public class Controller implements VetoableChangeListener, PropertyChangeListener, GameResetListener {
    
    /**
     * Creates new Controller
     */
    public Controller() {
        this.firstCard = null;
        this.secondCard = null;
        this.cardsFlipped = 0;
        this.counter = 0;
        this.matchedListeners = new ArrayList<>();
        this.multiplayer = GlobalState.isMultiplayer();
        this.player1 = new Player(1);
        if(multiplayer) {
            this.player2 = new Player(2);
        }
        else {
            this.player2 = null;
        }
        this.currentPlayer = 1;
    }
    
    /**
    * Changes the current player in multiplayer mode
    * The turn is switched between Player 1 and Player 2
    * In single-player mode, the method does nothing
    */
    private void changeTurn() {
        if(!multiplayer) {
            return;
        }
        if (currentPlayer == 1) {
            currentPlayer = 2;
        } 
        else {
            currentPlayer = 1;
        }
    }
    
    /**
     * 
     * @return the player whose turn is currently active
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }
    
    /**
     * 
     * @return the number of pairs matched by player1
     */
    public int getPlayer1Pairs() {
        return player1.getMatchedPairs();
    }
    
    /**
     * 
     * @return the number of pairs matched by player2
     */
    public int getPlayer2Pairs() {
        return player2.getMatchedPairs();
    }
    
    /**
     * 
     * @return the total number of matched pairs in the current game
     */
    public int getTotalMatchedPairs() {
        if(multiplayer) return player1.getMatchedPairs() + player2.getMatchedPairs();
        else return player1.getMatchedPairs();
    }
    
    /**
     * 
     * @return the total number of times some card is turned face up
     */
    public int getCounter() {
        return counter;
    }
    
    /**
    * Validates a card state change before it is applied
    * the change is rejected by throwing a PropertyVetoException
    *
    * @param evt the property change event containing the old and new card states
    * @throws PropertyVetoException if the requested state change is not valid
    */
    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        String propertyName = evt.getPropertyName();

        if (!"state".equals(propertyName)){
            return;
        }
        
        Card.State oldState = (Card.State) evt.getOldValue();
        Card.State newState = (Card.State) evt.getNewValue();
        
        // check if the move is valid
        if (!isValidMove(oldState, newState)) {
            throw new PropertyVetoException("Illegal move", evt);
        }
    }
    
    /**
    * Handles changes to the card's state property
    * When a card is turned face up, the method processes the card flip
    * and updates the game state accordingly
    *
    * @param evt the property change event containing the card and its new state
    */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if (!"state".equals(propertyName)){
            return;
        }

        //Card.State oldState = (Card.State) evt.getOldValue();
        Card.State newState = (Card.State) evt.getNewValue();
        Card c = (Card) evt.getSource();

        if (newState == Card.State.FACE_UP) {
            handleCardFlip(c);
        }
    }
    
    /**
     * Handles a card being flipped and keeps track of the cards currently face up 
     * When two cards have been flipped, checks whether they form a matching pair 
     * If they match, the pair is assigned to the current player and a match event is fired
     * Otherwise, the turn is changed and an event is fired to flip the cards back
     *
     * @param card the card that has just been flipped face up
     */
    private void handleCardFlip(Card card) {
        cardsFlipped++;
        counter++;
        
        if (cardsFlipped == 1) {
            firstCard = card;
        } 
        else if (cardsFlipped == 2) {
            secondCard = card;         
            // check if cards match
            if (firstCard.getValue() == secondCard.getValue()) {
                // cards match -> mark them as excluded
                if(currentPlayer == 1) {
                    player1.incrementMatchedPairs();
                }
                else {
                    player2.incrementMatchedPairs();
                }
                fireMatchedEvent(true);
                resetCardsFlipped();
            } else {
                // cards don't match -> flip them back after a delay
                resetCardsFlipped();
                changeTurn();
                fireMatchedEvent(false);
            }
        }
    }
    
    /**
    * Resets the current turn by clearing the selected cards and
    * resetting the number of flipped cards to zero
    */
    public void resetCardsFlipped() {
        firstCard = null;
        secondCard = null;
        cardsFlipped = 0;
    }
    
    /**
     * Checks whether a requested card state change is valid according to the game rules
     * A card cannot be turned face up if it is already face up,
     * has already been matched, or if two cards are already face up
     *
     * @param oldState the current state of the card
     * @param newState the requested new state of the card
     * @return true if the state change is valid, false otherwise
     */
    private Boolean isValidMove(Card.State oldState, Card.State newState) {
        // can't flip a card up that's already face up or excluded
        if(newState.equals(Card.State.FACE_UP)) {
            if (oldState.equals(Card.State.FACE_UP) || oldState.equals(Card.State.EXCLUDED)) {
                return false;
            }
            // can't flip more than 2 cards at once
            if (cardsFlipped >= 2) {
                return false;
            }
        }
        return true;
    }
    
    private void resetGameState() {
        resetCardsFlipped();
        currentPlayer = 1;
        player1.resetMatchedPairs();
        if (multiplayer) player2.resetMatchedPairs();
        counter = 0;
    }
    
    @Override
    public void onGameReset() {
        resetGameState();
    }
    
    /**
     * Notifies all registered listeners about the result of the card match
     * The event indicates whether the two selected cards form a matching pair
     *
     * @param matched true if the cards match, false otherwise
     */
    private void fireMatchedEvent(Boolean matched) {
        for (MatchedListener listener : matchedListeners) {
            listener.onMatch(matched);
        }
    }
    
    // add a matched event listener
    public void addMatchedListener(MatchedListener listener) {
        matchedListeners.add(listener);
    }
    
    // remove a matched event listener
    public void removeMatchedListener(MatchedListener listener) {
        matchedListeners.remove(listener);
    }
    
    // tracks the first card flipped in a turn
    private Card firstCard;
    // tracks the second card flipped in a turn
    private Card secondCard;
    // number of cards currently turned face up (max 2)
    private int cardsFlipped;
    // total number of times some card is turned face up
    private int counter;
    // list of listeners for the Matched event
    private final java.util.List<MatchedListener> matchedListeners;
    // player1
    private final Player player1;
    // player2, null when the game is in singleplayer mode
    private final Player player2;
    // true if the game is in multiplayer mode, false otherwise
    private final boolean multiplayer;
    // tracks the id of the player whose turn is currently active
    private int currentPlayer;
}
