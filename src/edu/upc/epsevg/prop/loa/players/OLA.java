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
    
    public class Tuple {
        private Point from;
        private double valor;
        
        public Tuple() {
            
        }
        
        public Tuple(Point p, double v){
            from = p;
            valor = v;
        }

        @Override
        public String toString() {
            return '\n' + "Tuple{" + "from=" + from +  "valor=" + valor + '}';
        }
    }
    
    //Arrays necesarias para los cálculos eurísticos.
    private ArrayList<Point> piezasCentro, piezasFuera;
    private ArrayList<Tuple> euclidianas = new ArrayList();
    
    //Variables globales
    private String name;
    private GameStatus s;
    private int prof;
    private int nodesExp = 0;


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

        Point fromAnt = new Point(0,0);
        Point pos = new Point(0, 0);
        Point from = new Point(0, 0);
        Point to = new Point(0, 0);

        CellType color = s.getCurrentPlayer();
        this.s = s;
        int qn = s.getNumberOfPiecesPerColor(color);
        int index = 0;

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

                        if (valorNou >= valor) {
                            valor = valorNou;
                            fromAnt = from;
                            pos = to;
                        }
                    }
                }
            }
        }
        
        return new Move(fromAnt, pos, nodesExp, prof, SearchType.MINIMAX);
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
        nodesExp++;

        if(s.GetWinner() == color){
            return Integer.MAX_VALUE;
        }
        else if(pprof == 0){
            return getHeuristica(s, CellType.opposite(color));
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

                        value = Math.min(value, movMax(saux, pprof - 1, 
                                alpha, beta, CellType.opposite(color)));
                        
                        if (alpha >= beta) {
                            return value;
                        }
                        
                        beta = Math.min(value, beta);
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
        nodesExp++;

        if(s.GetWinner() == color){
            return Integer.MIN_VALUE;
        }
        else if(pprof == 0){
            return getHeuristica(s, CellType.opposite(color));
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

                        value = Math.max(value, movMin(saux, pprof - 1, 
                                alpha, beta, CellType.opposite(color)));
                        
                        if (alpha >= beta) {
                            return value;
                        }
                        
                        alpha = Math.max(value, alpha);
                    }
                }
            }

        }
        return value;
    }
    
    public int getHeuristica(GameStatus gs, CellType color) {
        
         int quantes = gs.getNumberOfPiecesPerColor(color);
         Point from = new Point(0,0);
         int maximHeur = -100000, maximHeur2 = -100000;
         
         piezasCentro = new ArrayList();
         piezasFuera = new ArrayList();
         
         for(int i = 0; i< quantes; i++) {
             from = gs.getPiece(color, i);
             int valor = mayorSubconjunto(gs, color, from);
             
             if(maximHeur < valor) {
                 maximHeur = valor;
             }
         }
         
         for(int i = 0; i< gs.getNumberOfPiecesPerColor(CellType.opposite(color)); i++) {
             from = gs.getPiece(CellType.opposite(color), i);
             int valor = mayorSubconjunto(gs, CellType.opposite(color), from);
             
             if(maximHeur2 < valor) {
                 maximHeur2 = valor;
             }
         }
         
         return maximHeur - maximHeur2;
    }
    
    public double Euclidiana(Point from, Point center) {
        
        double calx = Math.pow(from.x - center.x, 2);
        double caly = Math.pow(from.y - center.y, 2);
        
        return Math.sqrt(calx + caly);
    }
    
    public boolean comprovaX(Point from) {
        return from.x >= 2 && from.x <= 5;
    }
    
    public boolean comprovaY(Point from) {
        return from.y >= 2 && from.y <= 5;
    }
    
    public boolean validaCoordenadas(int x, int y) {
        return (x >= 0 && x <= 7) && (y >= 0 && y <= 7);
    }
    
    public ArrayList creaSubConjunto(GameStatus s, Point from) {
        
        CellType colorNuestro = s.getCurrentPlayer();
        ArrayList <Point> puntos = new ArrayList();
        
        puntos.add(from);
        
        //Derecha
        if(validaCoordenadas(from.x + 1, from.y) && s.getPos(from.x + 1, from.y) == colorNuestro){
            puntos.add(new Point(from.x + 1, from.y));
        }
        
        //Izquierda
        if(validaCoordenadas(from.x - 1, from.y) && s.getPos(from.x - 1, from.y) == colorNuestro){
            puntos.add(new Point(from.x - 1, from.y));
        }
        
        //Abajo
        if(validaCoordenadas(from.x, from.y + 1) && s.getPos(from.x, from.y + 1) == colorNuestro){
            puntos.add(new Point(from.x, from.y + 1));
        }
        
        //Arriba
        if(validaCoordenadas(from.x, from.y - 1) && s.getPos(from.x, from.y - 1) == colorNuestro){
            puntos.add(new Point(from.x, from.y - 1));
        }
        
        //Diagonal izq arriba
        if(validaCoordenadas(from.x - 1, from.y - 1) && s.getPos(from.x - 1, from.y - 1) == colorNuestro){
            puntos.add(new Point(from.x - 1, from.y - 1));
        }
        
        //Diagonal izq abajo
        if(validaCoordenadas(from.x - 1, from.y + 1) && s.getPos(from.x - 1, from.y + 1) == colorNuestro){
            puntos.add(new Point(from.x - 1, from.y + 1));
        }
        
        //Diagonal der arriba
        if(validaCoordenadas(from.x + 1, from.y - 1) && s.getPos(from.x + 1, from.y - 1) == colorNuestro){
            puntos.add(new Point(from.x + 1, from.y - 1));
        }
        
        //Diagonal der abajo
        if( validaCoordenadas(from.x + 1, from.y + 1) && s.getPos(from.x + 1, from.y + 1) == colorNuestro){
            puntos.add(new Point(from.x + 1, from.y + 1));
        }
        
        return puntos;
    }
    
    public int mayorSubconjunto(GameStatus s, CellType color, Point from) {
        
        Point to;
        ArrayList<Point> moviments, subconjunto;
        
        int qn = s.getNumberOfPiecesPerColor(color);
        int valor = 0;
        
        subconjunto = creaSubConjunto(s, from);
        
        if((double)subconjunto.size()/qn < 0.25) {
            valor = 5;
        }
        
        else if((double)subconjunto.size()/qn < 0.5) {
            valor = 10;
        }
        
        else if((double)subconjunto.size()/qn < 0.75) {
            valor = 15;
        }
        
        else if((double)subconjunto.size()/qn < 1.0) {
            valor = 20;
        }
        
        else if((double)subconjunto.size()/qn == 1.0) {
            valor = 10000;
        }
        
        return valor;
    }
}

