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
 * Jugador aleatori
 *
 * @author bernat
 */
public class OLA implements IPlayer, IAuto {

    //Variables globales
    private int prof;
    private int nodesExp;
    private boolean tout;
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
    
    
    

    public OLA () {
        nodesExp = 0;
        tout = false;
        this.prof = 0;
        centro = new ArrayList();
        centroRival = new ArrayList();
    }

    @Override
    public void timeout() {
        tout = true;
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

        tout = false;

        int valor = 0;
        int valorNou = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int valorAux = 0;

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
        
        Move movimiento_def = new Move(from, to, 0, 0, SearchType.MINIMAX_IDS);

        Move movimiento = new Move(from, to, 0, 0, SearchType.MINIMAX_IDS);

        
        // Obtenim les peces i la seva ubicació
        while (!tout) {

            prof += 1;
            valor = Integer.MIN_VALUE;
            
            // Esto es nuestro minimax :)
            
            for (int q = 0; q < qn && !tout; q++) {
                from = s.getPiece(color, q);
                
                // Per cada peça obtenim els seus moviments possibles
                moviments = (s.getMoves(from));

                if (moviments != null) {
                    for (int qt = 0; qt < moviments.size() && !tout; qt++) {
                        to = moviments.remove(0);

                        //fusion de minimax y move
                        if (!s.isGameOver()) {

                            GameStatus saux = new GameStatus(s);

                            saux.movePiece(from, to);
                            
                            if(!tout && saux.isGameOver() && saux.GetWinner() == color){
                                System.out.println("shortcut for real winners :P");
                                return new Move(from, to, nodesExp, prof, SearchType.MINIMAX_IDS);
                            }
                            
                                valorNou = movMin(saux, prof - 1, alpha, beta, CellType.opposite(color));

                            // parem el minimax
                            if(tout) break;
                            
                            if (valorNou >= valor) {
                                valor = valorNou;
                                movimiento = new Move(from, to, nodesExp, prof, SearchType.MINIMAX_IDS);
                            }
                        }
                    }
                }
            }
            
            // Si no tout siginfica que hemos acabado el minimax.
            if(!tout){
                valorAux = valor;
                movimiento_def = movimiento;  
            }
            
        }


        // Reiniciamos la profundidad
        prof = 0;
        System.out.println("heur: "+ valorAux);
        System.out.println("movim: "+movimiento_def.getFrom()+""+movimiento_def.getTo());
        return movimiento_def;
    }

    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public String getName() {
        return "OLA";
    }

    /**
     * texto importante sobre la funcion
     * @param s
     * @param pprof
     * @param alpha
     * @param beta
     * @param color
     * @return 
     */
    public int movMin(GameStatus s, int pprof, int alpha, int beta, CellType color) {
        nodesExp++;

        // Caso base: timeout es true
        if(tout) return 0;
       
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
                    to = moviments.remove(0);

                    if (!s.isGameOver()) {

                        GameStatus saux = new GameStatus(s);
                        saux.movePiece(from, to);
                        
                        if(!tout && saux.isGameOver() && saux.GetWinner() == color) return Integer.MIN_VALUE;
                        
                        if (tout) {
                            return 0;
                        }

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
     * texto importante sobre la funcion
     * @param s
     * @param pprof
     * @param alpha
     * @param beta
     * @param color
     * @return 
     */
    public int movMax(GameStatus s, int pprof, int alpha, int beta, CellType color) {

        nodesExp++;

        // Caso base: timeout es true
        if(tout) return 0;
        
        
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
                    to = moviments.remove(0);

                    if (!s.isGameOver()) {

                        GameStatus saux = new GameStatus(s);
                        saux.movePiece(from, to);
                        
                        if(!tout && saux.isGameOver() && saux.GetWinner() == color) return Integer.MAX_VALUE;

                        if (tout) {
                            return 0;
                        }

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

    public double Euclidiana(Point from, Point center) {
        
        double calx = Math.pow(from.x - center.x, 2);
        double caly = Math.pow(from.y - center.y, 2);
        
        return Math.sqrt(calx + caly);
    }
    
    public boolean validaCoordenadas(int x, int y) {
        return (x >= 0 && x <= 7) && (y >= 0 && y <= 7);
    }
    
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
     * texto importante sobre la funcion
     * @param gs
     * @param color
     * @return 
     */
   public int getHeuristicaDef(GameStatus gs, CellType color) {
        // Quitar euclidiana y tener en cuenta solo el grupo Mayor.
        
        int qn = gs.getNumberOfPiecesPerColor(color);
        
        ArrayList <Point> puntosFuera = new ArrayList(); 
        ArrayList <Point> grupoMaximo = grupoMayor(gs, color);
        
        // Hacemos la distancia de los puntos de fuera respecto al grupo mayor
        
//        for(int i = 0; i< qn; i++) {
//            
//            Point puntoAux = gs.getPiece(color, i);
//            
//            if(!grupoMaximo.contains(puntoAux)){
//                
//                puntosFuera.add(puntoAux);
//                
//                double minimEucl = Integer.MAX_VALUE;
//            
//                for(int j = 0; j < grupoMaximo.size(); j++) {
//
//                    double eucl = Euclidiana(puntoAux, grupoMaximo.get(j));
//
//                    if(minimEucl > eucl) minimEucl = eucl;
//                }
//
//                sumaEucl += minimEucl;
//            }
//        }
        
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
    
    public int heuristicas(GameStatus s, CellType color){
        
        int h1 = 0;
        int h2 = 0;
        
        h1 = getHeuristicaDef(s,color);
        h2 = centre(s,color);
        
        return h1+h2;
    }
    
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
}