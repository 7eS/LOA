package edu.upc.epsevg.prop.loa;

import edu.upc.epsevg.prop.loa.Level;
import edu.upc.epsevg.prop.loa.IPlayer;
import edu.upc.epsevg.prop.loa.players.*;

import javax.swing.SwingUtilities;

/**
 * Lines Of Action: el joc de taula.
 * @author bernat
 */
public class Game {
     /**
     * @param args
     */
    public static void main(String[] args) {
        
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                // OLA és el jugador amb l'IDS i OLA_LIM el de profunditat fixa.
                 IPlayer player1 = new OLA();
                //IPlayer player1 = new OLA_LIM(4);
                
                
                IPlayer player2 = new MCCloudPlayer();
                //IPlayer player2 = new RandomPlayer("Crazy Chris");
                //IPlayer player2 = new RandomPlayer("Crazy Chris");
                //IPlayer player2 = new HumanPlayer("Octopus");
                
                new Board(player1 , player2, 10, Level.DIFFICULT);
             }
        });
    }
}
