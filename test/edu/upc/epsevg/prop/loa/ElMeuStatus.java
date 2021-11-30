package edu.upc.epsevg.prop.loa;

import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author roger
 */
public class ElMeuStatus extends GameStatus {
        
    public ElMeuStatus(int [][] tauler){
            super(tauler);
    }
    
    public ElMeuStatus(GameStatus gs){
        super(gs);
    }   
     
    public int getHeuristica(GameStatus gs){
        CellType color = gs.getCurrentPlayer();
        int quantes = gs.getNumberOfPiecesPerColor(color);
        return quantes;
    }
}
