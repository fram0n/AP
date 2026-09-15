package matchingpairs;

import java.awt.Color;
import javax.swing.JButton;
import java.beans.*;
import java.awt.event.*;

public class Card extends JButton implements ShuffleListener, ActionListener, MatchedListener {

    /**
     * Creates new Card
     */
    public Card() {
        this.vetos = new VetoableChangeSupport(this);
        this.changes = new PropertyChangeSupport(this);
        this.value = 0;
        this.state = State.FACE_DOWN;
    }
    
    /**
     * 
     * @return the card value
     */
    public int getValue() {
        return value;
    }
    
    /**
     * Sets the value of the card to the specified number
     *
     * @param n the new value to assign to the card
     */
    public void setValue(int n) {
        this.value = n;
    }
    
    /**
     * 
     * @return the current state of the card (EXCLUDED, FACE_DOWN or FACE_UP)
     */
    public State getState() {
        return state;
    }
    
    /**
     * Changes the state of the card after validating the requested change
     * The new state is first checked by the registered vetoable change listeners
     * If the change is accepted, the card state and its visual appearance are updated, 
     * and a property change event is fired to notify the registered property change listeners
     *
     * @param newState the new state to assign to the card
     */
    public void setState(State newState) {
        State oldState = this.state;
        try {
            vetos.fireVetoableChange("state", oldState, newState);
            // if no veto then change the card state
            this.state = newState;
            switch (newState) {
                case State.EXCLUDED: 
                    this.setForeground(Color.red);
                    break;
                case State.FACE_DOWN:
                    this.setForeground(Color.green);
                    this.setText("?");
                    break;
                case State.FACE_UP:
                    this.setForeground(Color.black);
                    setText(String.valueOf(this.getValue()));
                    break;        
            }
            // fire property change after state is changed
            changes.firePropertyChange("state", oldState, newState);
        } catch (PropertyVetoException e) {
            // the state change is rejected because the requested move is invalid
            // the card keeps its current state
        }
    }
    
    /**
     * Handles the card click event by requesting to change the card state to FACE_UP
     * The state change is applied only if it is not rejected
     * by a registered vetoable change listener
     *
     * @param evt the action event generated when the card is clicked
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        setState(State.FACE_UP);    
    }
    
    /**
     * Handles a shuffle event by resetting the card to FACE_DOWN
     * and assigning it a new value from the shuffled list
     *
     * @param cardValues the list of shuffled values used to assign a new value to the card
     */
    @Override
    public void onShuffle(java.util.Map<Card,Integer> cardValues) {
        // Reset card to face down
        this.setState(State.FACE_DOWN);
        this.setValue(cardValues.get(this));
    }
    
    /**
     * Handles a match event by updating the card state according to the result
     * If the card is part of a matching pair, its state is changed to EXCLUDED
     * Otherwise, the card remains face up for 500 milliseconds before being automatically turned face down
     *
     * @param matched true if the selected cards match, false otherwise
     */
    @Override
    public void onMatch(boolean matched) {
        if (this.getState().equals(State.FACE_UP)) {
            if (matched) {
                this.setState(State.EXCLUDED);
            }
            else {
                // timer of half a second so player can see the cards
                javax.swing.Timer timer = new javax.swing.Timer(500, e -> {
                    this.setState(State.FACE_DOWN);
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }
    
    @Override
    public void addVetoableChangeListener(VetoableChangeListener l) { 
        vetos.addVetoableChangeListener(l);
    }

    @Override
    public void removeVetoableChangeListener(VetoableChangeListener l) {
        vetos.removeVetoableChangeListener(l);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        if (changes == null) {
            this.changes = new PropertyChangeSupport(this);
        }
        changes.addPropertyChangeListener(l);
    }
    
    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        changes.removePropertyChangeListener(l);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    public enum State {
        EXCLUDED, FACE_DOWN, FACE_UP
    }
    
    // value of the card
    private int value;
    // current state of the card (EXCLUDED, FACE_DOWN or FACE_UP)
    private State state;    // bound and constrained property
    
    // helper object for the constrained property (state)
    private final VetoableChangeSupport vetos;
    // helper object for the bound property (state)
    private PropertyChangeSupport changes;

}
