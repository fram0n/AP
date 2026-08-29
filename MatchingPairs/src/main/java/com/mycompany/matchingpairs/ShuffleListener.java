/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.matchingpairs;

/**
 * Interface for objects that want to listen to shuffle events
 * @author francescamontagnoli
 */
public interface ShuffleListener {
    /**
     * Called when a shuffle event occurs
     */
    void onShuffle(java.util.List<Integer> cardValues);
}
