package edu.upc.epsevg.prop.loa.players;

import edu.upc.epsevg.prop.loa.CellType;
import edu.upc.epsevg.prop.loa.GameStatus;
import edu.upc.epsevg.prop.loa.IAuto;
import edu.upc.epsevg.prop.loa.IPlayer;
import edu.upc.epsevg.prop.loa.Move;
import edu.upc.epsevg.prop.loa.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Jugador aleatori
 *
 * @author bernat
 */
public class OLA implements IPlayer, IAuto {

    private String name;
    private GameStatus s;
    private int prof;


    public OLA(String name, int prof) {
        this.name = name;
        this.prof = prof;
    }

    @Override
    public void timeout() {
        // Nothing to do! I'm so fast, I never timeout 8-)
    }

    /**
     * Decideix el moviment del jugador donat un tauler i un color de peça que
     * ha de posar.
     *
     * @param s Tauler i estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    @Override
    public Move move(GameStatus s) {

        int valor = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int pprof = prof;

        Point fromAnt = new Point(0, 0);
        Point pos = new Point(0, 0);
        Point from = new Point(0, 0);
        Point to = new Point(0, 0);

        CellType color = s.getCurrentPlayer();
        this.s = s;
        int qn = s.getNumberOfPiecesPerColor(color);

        ArrayList<Point> moviments = new ArrayList<>();

        // Obtenim les peces i la seva ubicació
        for (int q = 0; q < qn; q++) {
            from = s.getPiece(color, q);
            // Per cada peça obtenim els seus moviments possibles
            moviments = (s.getMoves(from));

            if (moviments != null) {
                for (int qt = 0; qt < moviments.size(); qt++) {
                    to = moviments.remove(0);

                    //fusion de minimax y move
                    if (!s.isGameOver()) {

                        GameStatus saux = new GameStatus(s);
                        
                        saux.movePiece(from, to);

                        int valorNou = movMin(saux, pprof - 1, alpha, beta, CellType.opposite(color));
                        //int valorNou = 4;
                        if (valorNou > valor) {
                            System.out.println("valorNou: "+ valorNou);
                            valor = valorNou;
                            fromAnt = from;
                            pos = to;
                        }
                    }
                }
            }
        }
     
        return new Move(fromAnt, pos, 0, 0, SearchType.MINIMAX);
    }

    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public String getName() {
        return "Minimax(" + name + ")";
    }

    /**
     * Función que nos devuelve el movimiento con menor valor heurístico de
     * todos los movimientos estudiados.
     *
     * @param pt tablero resultante de poner una ficha en una determinada
     * columna.
     * @param lastcol columna en la que hemos puesto ficha para estudiar el
     * tablero.
     * @param pprof número de niveles restantes que le queda al algoritmo por
     * analizar.
     * @param alpha variable que determina el alfa para realizar la poda
     * alfa-beta.
     * @param beta variable que determina la beta para realizar la poda
     * alfa-beta.
     * @return retorna el valor heurístico máximo entre todas las posibilidades
     * comprobadas.
     */
    public int movMin(GameStatus s, int pprof, int alpha, int beta, CellType color) {

        if (s.GetWinner() == CellType.PLAYER1) {
            System.out.println("Guanya MovMin: " + CellType.PLAYER1.toString());
            return Integer.MAX_VALUE;
        } else if (s.GetWinner() == CellType.PLAYER2) {
            System.out.println("Guanya MovMin: " + CellType.PLAYER2.toString());
            return Integer.MIN_VALUE;
        } else if (pprof == 0) {
            return 0;
            // Funció heurística
        }

        int value = Integer.MAX_VALUE;
        int qn = s.getNumberOfPiecesPerColor(color);
        Point from = new Point(0, 0);
        Point to = new Point(0, 0);

        //ArrayList<Point> pendingPieces = new ArrayList<>();
        ArrayList<Point> moviments = new ArrayList<>();

        // Obtenim les peces i la seva ubicació
        for (int q = 0; q < qn; q++) {
            from = s.getPiece(color, q);
            // Per cada peça obtenim els seus moviments possibles
            moviments = (s.getMoves(from));

            if (moviments != null) {
                for (int qt = 0; qt < moviments.size(); qt++) {
                    to = moviments.remove(0);

                    if (!s.isGameOver()) {

                        GameStatus saux = new GameStatus(s);
                        saux.movePiece(from, to);

                        value = Math.min(value, movMax(saux, pprof - 1, alpha, beta, CellType.opposite(color)));
                        
                        beta = Math.min(value, beta);

                        if (alpha >= beta) {
                            //break;
                            
                            return value;
                        }

                    }
                }
            }

        }
        return value;
    }

    /**
     * Función que nos devuelve el movimiento con mayor valor heurístico de
     * todos los movimientos estudiados.
     *
     * @param pt tablero resultante de poner una ficha en una determinada
     * columna.
     * @param lastcol columna en la que hemos puesto ficha para estudiar el
     * tablero.
     * @param pprof número de niveles restantes que le queda al algoritmo por
     * analizar.
     * @param alpha variable que determina el alfa para realizar la poda
     * alfa-beta.
     * @param beta variable que determina la beta para realizar la poda
     * alfa-beta.
     * @return retorna el valor heurístico máximo entre todas las posibilidades
     * comprobadas.
     */
    public int movMax(GameStatus s, int pprof, int alpha, int beta, CellType color) {

        if (s.GetWinner() == CellType.PLAYER1) {
            System.out.println("Guanya MovMax: " + CellType.PLAYER1.toString());
            return Integer.MAX_VALUE;
        } else if (s.GetWinner() == CellType.PLAYER2) {
            System.out.println("Guanya MovMax: " + CellType.PLAYER2.toString());
            return Integer.MIN_VALUE;
        } else if (pprof == 0) {
            return 0;
            // Funció heurística
        }

        int value = Integer.MIN_VALUE;
        int qn = s.getNumberOfPiecesPerColor(color);
        Point from = new Point(0, 0);
        Point to = new Point(0, 0);

        //ArrayList<Point> pendingPieces = new ArrayList<>();
        ArrayList<Point> moviments = new ArrayList<>();

        // Obtenim les peces i la seva ubicació
        for (int q = 0; q < qn; q++) {
            from = s.getPiece(color, q);
            // Per cada peça obtenim els seus moviments possibles
            moviments = (s.getMoves(from));

            if (moviments != null) {
                for (int qt = 0; qt < moviments.size(); qt++) {
                    to = moviments.remove(0);

                    if (!s.isGameOver()) {

                        GameStatus saux = new GameStatus(s);
                        saux.movePiece(from, to);

                        value = Math.max(value, movMin(saux, pprof - 1, alpha, beta, CellType.opposite(color)));
                        
                        alpha = Math.max(value, alpha);

                        if (alpha >= beta) {
                            //break;
                            return value;
                        }

                    }
                }
            }

        }
        return value;
    }
}
