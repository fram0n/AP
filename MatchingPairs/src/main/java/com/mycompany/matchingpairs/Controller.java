/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.matchingpairs;

import java.beans.*;
import com.mycompany.matchingpairs.Card;
import java.awt.Color;
import java.util.*;

/**
 *
 * @author francescamontagnoli
 */
public class Controller implements VetoableChangeListener, PropertyChangeListener {
    
    private Card firstCard;     //first card flipped
    private Card secondCard;    // second card flipped
    private int cardsFlipped;   // cards currently turned face up (max 2)
    private int matchedPairs;   // number of already matched pairs
    private int counter;        // total number of times some card is turned face_up
    private java.util.List<MatchedListener> matchedListeners;
    
    public Controller() {
        this.firstCard = null;
        this.secondCard = null;
        this.cardsFlipped = 0;
        this.matchedPairs = 0;
        this.counter = 0;
        this.matchedListeners = new ArrayList<>();
    }
    
    public int getMatchedPairs() {
        return matchedPairs;
    }
    
    public void setMatchedPairs(int matchedPairs) {
        this.matchedPairs = matchedPairs;
    }
    
    public int getCounter() {
        return counter;
    }
    
    public void setCounter(int counter) {
        this.counter = counter;
    }
    
    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        String propertyName = evt.getPropertyName();

        if (!"state".equals(propertyName)){
            return;
        }
        
        Card.State oldState = (Card.State) evt.getOldValue();
        Card.State newState = (Card.State) evt.getNewValue();
        Card c = (Card) evt.getSource();
        
        // check if the move is valid
        if (!isValidMove(oldState, newState)) {
            throw new PropertyVetoException("Illegal move", evt);
        }
    }
    
    /**
     * 
     * @param evt 
     * action performed when the property state of a card is changed
     * if the new state is FACE_UP it handles the card flip
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if (!"state".equals(propertyName)){
            return;
        }

        Card.State oldState = (Card.State) evt.getOldValue();
        Card.State newState = (Card.State) evt.getNewValue();
        Card c = (Card) evt.getSource();

        if (newState == Card.State.FACE_UP) {
            handleCardFlip(c);
        }
    }
    
    /**
     * 
     * @param card 
     * check if one card was flipped or two cards were flipped
     * in case two cards were flipped, fire the matched event with parameter true if the two cards match, or false otherwise
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
                matchedPairs++;
                fireMatchedEvent(true);
                resetTurn();
            } else {
                // cards don't match -> flip them back after a delay
                fireMatchedEvent(false);
                resetTurn();
            }
        }
    }
    
     /**
     * Add a matched listener
     */
    public void addMatchedListener(MatchedListener listener) {
        matchedListeners.add(listener);
    }
    
    /**
     * Remove a matched listener
     */
    public void removeMatchedListener(MatchedListener listener) {
        matchedListeners.remove(listener);
    }
    
    private void fireMatchedEvent(Boolean matched) {
        for (MatchedListener listener : matchedListeners) {
            listener.onMatch(matched);
        }
    }
    
    public void resetTurn() {
        firstCard = null;
        secondCard = null;
        cardsFlipped = 0;
    }
    
    /**
     * 
     * @param oldState
     * @param newState
     * @return 
     * return false if we try to turn the card face up but it was already face up or excluded
     * return false if we try to turn the card face up but we already have two cards face up
     * return true otherwise
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
    
}
