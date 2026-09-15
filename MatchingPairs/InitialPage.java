package matchingpairs;

import javax.swing.*;
import java.awt.*;

public class InitialPage extends JFrame {
    
    public InitialPage() {
        this.nPairsLabel = new JLabel("Select the number of pairs");
        this.nPairs = new JSpinner(new SpinnerNumberModel(4, 4, 10, 1));    // min 4 pairs, max 10 pairs
        
        this.nPlayersLabel = new JLabel("Select the number of players");
        this.nPlayers = new JSpinner(new SpinnerNumberModel(1, 1, 2, 1));   // max 2 players
        
        this.playButton = new JButton("PLAY");
        setupInitialPage();
    }
    
    private void setupInitialPage() {
        // set up the initial frame
        setTitle("Matching Pairs Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // create the panel for selecting the number of pairs wanted
        JPanel pairsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        add(pairsPanel, BorderLayout.NORTH);
        pairsPanel.add(nPairsLabel);
        pairsPanel.add(nPairs);
        
        // create the panel for selecting the number of players wanted
        JPanel playersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        add(playersPanel, BorderLayout.CENTER);
        playersPanel.add(nPlayersLabel);
        playersPanel.add(nPlayers);
        
        // add to the frame the button that starts the game
        add(playButton, BorderLayout.SOUTH);
        
        playButton.addActionListener(e -> {
            boolean multiplayer = false;
            int n = (int) nPairs.getValue();
            int p = (int) nPlayers.getValue();
            
            if(p > 1) {
                multiplayer = true;
            }
            
            // close and destroy the initial frame
            setVisible(false);
            dispose();
            
            // set the global state
            GlobalState.setNPairs(n);
            GlobalState.setMultiplayer(multiplayer);
            
            // create and open the main page of the game
            new Board().setVisible(true);
        });
        
        pack();
        setLocationRelativeTo(null); // Center the window
    }
    
    private final JLabel nPairsLabel;
    private final JSpinner nPairs;
    
    private final JLabel nPlayersLabel;
    private final JSpinner nPlayers;
    
    private final JButton playButton;
}
