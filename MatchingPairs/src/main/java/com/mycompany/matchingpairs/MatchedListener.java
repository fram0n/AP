/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.matchingpairs;

/**
 *
 * Interface for objects that want to listen to matched events
 * @author francescamontagnoli
 */
public interface MatchedListener {
    
    void onMatch(Boolean matched);
    
}
