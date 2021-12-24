package edu.upc.epsevg.prop.loa.players;

import edu.upc.epsevg.prop.loa.CellType;
import edu.upc.epsevg.prop.loa.GameStatus;
import edu.upc.epsevg.prop.loa.IAuto;
import edu.upc.epsevg.prop.loa.IPlayer;
import edu.upc.epsevg.prop.loa.Move;
import edu.upc.epsevg.prop.loa.SearchType;
import java.awt.Point;
import java.util.ArrayList;

/**
 * Jugador que utilitza l'algorisme de profunditat fixa.
 *
 * @author Javier Delgado
 * @author Roger Robles
 */

public class OLA_LIM implements IPlayer, IAuto {

    //Variables globales
    private int prof;
    private int nodesExp;
    private CellType color;
    private ArrayList <Point> centro, centroRival;
    
    // Segun seamos blancas o negras priorizamos la horizontal o la vertical
    private Point horizontal1 = new Point(3,2);
    private Point horizontal2 = new Point(3,3);
    private Point horizontal3 = new Point(3,4);
    private Point horizontal4 = new Point(3,5);
    
    private Point vertical1 = new Point(2,3);
    private Point vertical2 = new Point(3,3);
    private Point vertical3 = new Point(4,3);
    private Point vertical4 = new Point(5,3);
    
    
    /**
     * Constructor del jugador.
     * 
     * @param pprof profunditat màxima a la que arriba.
     */

    public OLA_LIM (int pprof) {
        nodesExp = 0;
        this.prof = pprof;
        centro = new ArrayList();
        centroRival = new ArrayList();
    }
    
    @Override
    public void timeout() {
    }
    
    @Override
    public String getName() {
        return "OLA_LIM";
    }

    /**
     * Funció que decideix quin és el millor moviment següent.
     *
     * @param s Tauler i estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    
    @Override
    public Move move(GameStatus s) {

        int valor = 0;
        int valorNou = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        Point from = new Point(0, 0);
        Point to = new Point(0, 0);

        color = s.getCurrentPlayer();
        int qn = s.getNumberOfPiecesPerColor(color);

        ArrayList<Point> moviments;
        
        
        // Según nuestro color priorizamos la linea central vertical u horizontal        
        if(centro.size() == 0){
            if(color == CellType.PLAYER1){
                centro.add(horizontal1);
                centro.add(horizontal2);
                centro.add(horizontal3);
                centro.add(horizontal4);
                centroRival.add(vertical1);
                centroRival.add(vertical2);
                centroRival.add(vertical3);
                centroRival.add(vertical4);
            }
            else{
                centro.add(vertical1);
                centro.add(vertical2);
                centro.add(vertical3);
                centro.add(vertical4);
                centroRival.add(horizontal1);
                centroRival.add(horizontal2);
                centroRival.add(horizontal3);
                centroRival.add(horizontal4);
            }
        }

        Move movimiento = new Move(from, to, 0, 0, SearchType.MINIMAX);
        
        valor = Integer.MIN_VALUE;
            
        // Esto es nuestro minimax :)

        for (int q = 0; q < qn; q++) {
            from = s.getPiece(color, q);

            // Per cada peça obtenim els seus moviments possibles
            moviments = (s.getMoves(from));

            if (moviments != null) {
                for (int qt = 0; qt < moviments.size(); qt++) {
                    to = moviments.get(qt);

                    //fusion de minimax y move
                    if (!s.isGameOver()) {

                        GameStatus saux = new GameStatus(s);

                        saux.movePiece(from, to);

                        if(saux.isGameOver() && saux.GetWinner() == color){
                            System.out.println("shortcut for real winners :P");
                            return new Move(from, to, nodesExp, prof, SearchType.MINIMAX);
                        }

                        valorNou = movMin(saux, prof - 1, alpha, beta, CellType.opposite(color));

                        if (valorNou >= valor) {
                            valor = valorNou;
                            movimiento = new Move(from, to, nodesExp, prof, SearchType.MINIMAX);
                        }
                    }
                }
            }
        }
        
        return movimiento;
    }
    
    /**
     *
     * Funció que retorna el moviment amb menys valor heurístic de tots els moviments
     * estudiats.
     *
     * @param s estat actual del tauler.
     * @param pprof profunditat a la que estem dins del minimax
     * @param alpha variable que fa refèrencia a l'alfa per fer la poda.
     * @param beta variable que fa refèrencia a la beta per fer la poda.
     * @param color variable que fa refèrencia al color del jugador que mou segon.
     * @return retorna el moviment que menys valor heurístic té.
     */
    
    public int movMin(GameStatus s, int pprof, int alpha, int beta, CellType color) {
        nodesExp++;
       
        if(s.isGameOver()) {
            if (s.GetWinner() == CellType.opposite(color)) {
                return Integer.MAX_VALUE;
            } else if (s.GetWinner() == CellType.opposite(CellType.opposite(color))) {
                
                return Integer.MIN_VALUE;
            }
        } else if (pprof == 0) {
            
            int rival = heuristicas(s, color);
            int nosotros = heuristicas(s, CellType.opposite(color));
            
            return nosotros - rival;
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
                    to = moviments.get(qt);

                    if (!s.isGameOver()) {

                        GameStatus saux = new GameStatus(s);
                        saux.movePiece(from, to);
                        
                        if(saux.isGameOver() && saux.GetWinner() == color) return Integer.MIN_VALUE;

                        value = Math.min(value, movMax(saux, pprof - 1,
                                alpha, beta, CellType.opposite(color)));

                        beta = Math.min(value, beta);

                        if (alpha >= beta) {
                            return value;
                        }
                    }
                }
            }

        }
        return value;
    }

    /**
     * Funció que retorna el moviment amb més valor heurístic de tots els moviments
     * estudiats.
     *
     * @param s estat actual del tauler.
     * @param pprof profunditat a la que estem dins del minimax
     * @param alpha variable que fa refèrencia a l'alfa per fer la poda.
     * @param beta variable que fa refèrencia a la beta per fer la poda.
     * @param color variable que fa refèrencia al color del jugador que mou primer.
     * @return retorna el moviment que més valor heurístic té.
     */
    public int movMax(GameStatus s, int pprof, int alpha, int beta, CellType color) {

        nodesExp++;
        
        if(s.isGameOver()) {
            if (s.GetWinner() == CellType.opposite(color)) {
                //System.out.println("Caso base1b");
                return Integer.MIN_VALUE;
            } else if (s.GetWinner() == color) {
               // System.out.println("Caso base2b");
                return Integer.MAX_VALUE;
            }
        } else if(pprof == 0){
            
            int rival = heuristicas(s, CellType.opposite(color));
            int nosotros = heuristicas(s, color);
            
            return nosotros - rival;
            
        }

        int value = Integer.MIN_VALUE;
        int qn = s.getNumberOfPiecesPerColor(color);

        Point from = new Point(0, 0);
        Point to = new Point(0, 0);

        //ArrayList<Point> pendingPieces = new ArrayList<>();
        ArrayList<Point> moviments = new ArrayList<>();

        // Obtenim les peces i la seva ubicació
        for (int q = 0;q< qn ;q++) {
                from = s.getPiece(color, q);
            // Per cada peça obtenim els seus moviments possibles
            moviments = (s.getMoves(from));

            if (moviments != null) {
                for (int qt = 0; qt < moviments.size(); qt++) {
                    to = moviments.get(qt);

                    if (!s.isGameOver()) {

                        GameStatus saux = new GameStatus(s);
                        saux.movePiece(from, to);
                        
                        if(saux.isGameOver() && saux.GetWinner() == color) return Integer.MAX_VALUE;

                        value = Math.max(value, movMin(saux, pprof - 1,
                                alpha, beta, CellType.opposite(color)));

                        alpha = Math.max(value, alpha);

                        if (alpha >= beta) {
                            return value;
                        }
                    }
                }
            }
        }
     return value;
    }
    
    /**
     * Funció que comprova si un punt no està fora del rang del tauler.
     * 
     * @param x coordenada x d'un punt qualsevol
     * @param y coordenada y d'un punt qualsevol.
     * @return true si el punt és vàlid.
     */
    
    public boolean validaCoordenadas(int x, int y) {
        return (x >= 0 && x <= 7) && (y >= 0 && y <= 7);
    }
    
    /**
     * Funció que a partir d'un punt donat, retorna el grup que forma 
     * em les seves veïnes.
     * 
     * @param s tauler actual.
     * @param from punt central del qual es miren les veïnes.
     * @param color color del punt central.
     * @return retorna el conjunt que forma un punt qualsevol amb les del seu voltant.
     */
    
    public ArrayList creaSubConjunto(GameStatus s, Point from, CellType color) {
        
        ArrayList <Point> puntos = new ArrayList();
        
        //Derecha
        if(validaCoordenadas(from.x + 1, from.y) && s.getPos(from.x + 1, from.y) == color){
            puntos.add(new Point(from.x + 1, from.y));
        }
        
        //Izquierda
        if(validaCoordenadas(from.x - 1, from.y) && s.getPos(from.x - 1, from.y) == color){
            puntos.add(new Point(from.x - 1, from.y));
        }
        
        //Abajo
        if(validaCoordenadas(from.x, from.y + 1) && s.getPos(from.x, from.y + 1) == color){
            puntos.add(new Point(from.x, from.y + 1));
        }
        
        //Arriba
        if(validaCoordenadas(from.x, from.y - 1) && s.getPos(from.x, from.y - 1) == color){
            puntos.add(new Point(from.x, from.y - 1));
        }
        
        //Diagonal izq arriba
        if(validaCoordenadas(from.x - 1, from.y - 1) && s.getPos(from.x - 1, from.y - 1) == color){
            puntos.add(new Point(from.x - 1, from.y - 1));
        }
        
        //Diagonal izq abajo
        if(validaCoordenadas(from.x - 1, from.y + 1) && s.getPos(from.x - 1, from.y + 1) == color){
            puntos.add(new Point(from.x - 1, from.y + 1));
        }
        
        //Diagonal der arriba
        if(validaCoordenadas(from.x + 1, from.y - 1) && s.getPos(from.x + 1, from.y - 1) == color){
            puntos.add(new Point(from.x + 1, from.y - 1));
        }
        
        //Diagonal der abajo
        if( validaCoordenadas(from.x + 1, from.y + 1) && s.getPos(from.x + 1, from.y + 1) == color){
            puntos.add(new Point(from.x + 1, from.y + 1));
        }
        
        return puntos;
    }
    
    /**
     * Funció que fa la acció de retornar amb una array el major nombre
     * de fitxes connectades d'un determinat color de fitxes.
     *
     * @param s tauler actual.
     * @param color color del que volem crear el grup més gran.
     * @return retorna el grup més gran d'un determinat color.
     */
    
    public ArrayList grupoMayor(GameStatus s, CellType color){
        
        ArrayList <Point> listapuntos = new ArrayList(), grupoAux;
        ArrayList < ArrayList<Point> > grupos = new ArrayList();
        
        int quantes = s.getNumberOfPiecesPerColor(color);
        
        Point aux = new Point();
        
        for(int i = 0; i<quantes;i++){
           aux = s.getPiece(color, i);
           listapuntos.add(aux);
           grupos.add(creaSubConjunto(s, aux, color));
        }
        
        int sizeMayor = -1000;
        ArrayList grupoMayor = new ArrayList();
        
        for(int i = 0; i < listapuntos.size() - 1; i++) {
            
            grupoAux = grupos.get(i);
            
            for(int j = i + 1; j < grupos.size(); j++) {
                if(grupos.get(j).contains(listapuntos.get(i))){
                    grupoAux = concatena(grupoAux, grupos.get(j));
                    grupos.set(j, grupoAux);
                }
                
                if(grupos.get(j).size() > sizeMayor){
                    sizeMayor = grupos.get(j).size();
                    grupoMayor = grupos.get(j);
                }
            }
        }
        
        return grupoMayor;
    }
    
    /**
     * Funció que concatena dues arrays sense elements repetits.
     * 
     * @param a primera array per concatenar
     * @param b segona array per concatenar
     * @return retorna l'array resultant de la concatenació.
     */
    
    public ArrayList concatena(ArrayList a, ArrayList b){
        
        ArrayList conc = a;
        
        for(int i = 0; i < b.size(); i++){
            if(!a.contains(b.get(i))){
                conc.add(b.get(i));
            }
        }
        
        return conc;
    }
    
    /**
     * Funció que calcula la heurística a partir del grup més gran d'un determinat color,
     * tenint en compte quins punts es queden d'aquest grup.
     * 
     * @param gs tauler actual
     * @param color color del que volem calcular la heurística.
     * @return retorna el valor resultant calculat.
     */
   public int getHeuristicaDef(GameStatus gs, CellType color) {
        
        int qn = gs.getNumberOfPiecesPerColor(color);
        
        ArrayList <Point> puntosFuera = new ArrayList(); 
        ArrayList <Point> grupoMaximo = grupoMayor(gs, color);
        
        int valorMult = 1;
        if(color == this.color) {
            if(puntosFuera.size() <= grupoMaximo.size()) valorMult = 20;
            else if(puntosFuera.size() > grupoMaximo.size()) valorMult = -30;
        }
        else {
            if(puntosFuera.size() < grupoMaximo.size()) valorMult = 30;
            else if(puntosFuera.size() >= grupoMaximo.size()) valorMult = -40;
        }
        
        return grupoMaximo.size() * valorMult;
    }
   
    /**
     * Funció que segons la distància a la que es trobi un punt respecte un conjunt
     * de punts determinat, retorna un valor o un altre, quant més aprop més valor.
     * 
     * @param gs tauler actual
     * @param color color del que volem fer el càlcu heurístic.
     * @return retorna el valor calculat després de fer el càlcul
     */
   
    public int centre(GameStatus gs, CellType color){
        
        int res = 0;
        
        ArrayList <Point> centroAux = new ArrayList();
        
        if(color != this.color) {
            centroAux = this.centroRival;
        }else{
            centroAux = this.centro;
        }
        
        int qn = gs.getNumberOfPiecesPerColor(color);
        Point ficha;
        for (int i = 0; i < qn; i++) {
            ficha = gs.getPiece(color, i).getLocation();
            if(centroAux.contains(ficha)) res+=100;
            
            else{
                if(ficha.distance(centroAux.get(0)) == 1.0 || ficha.distance(centroAux.get(1)) == 1.0
                        || ficha.distance(centroAux.get(2)) == 1.0 || ficha.distance(centroAux.get(3)) == 1.0){ 
                    res+=100;
                }
                
                else{
                    // centro.get(2) porque es la mas centrica
                    double dist = ficha.distance(centroAux.get(2));
                    res -= 10*dist;
                }
            }        
        }

        return res;
        
    }
    
    /**
    * Funció que fa la suma ponderada de les dues heurístiques calculades,
    * la de centre y la del grup major.
    * @param s tauler actual
    * @param color color del que volem fer el càlcul heurístic.
    * @return retorna la suma de les dues heurísticas.
    */
    
    public int heuristicas(GameStatus s, CellType color){
        
        int h1 = 0;
        int h2 = 0;
        
        h1 = getHeuristicaDef(s,color);
        h2 = centre(s,color);
        
        return h1+h2;
    }
}