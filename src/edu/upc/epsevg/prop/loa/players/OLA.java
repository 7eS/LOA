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

    public OLA() {
        nodesExp = 0;
        tout = false;
        prof = 0;
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

        int valor = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        Point from = new Point(0, 0);
        Point to = new Point(0, 0);

        CellType color = s.getCurrentPlayer();
        int qn = s.getNumberOfPiecesPerColor(color);

        ArrayList<Point> moviments;

        Move movimiento_def = new Move(from, to, 0, 0, SearchType.MINIMAX_IDS);

        Move movimiento = new Move(from, to, 0, 0, SearchType.MINIMAX_IDS);

        // Obtenim les peces i la seva ubicació
        while (!tout) {

            prof += 1;
            
            // Esto es nuestro minimax :)
            
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
                            
                            int valorNou = movMin(saux, prof - 1, alpha, beta, CellType.opposite(color));

                            // parem el minimax
                            if(tout) break;
                            
                            if (valorNou >= valor) {
                                valor = valorNou;
                                movimiento = new Move(from, to, nodesExp, prof, SearchType.MINIMAX);
                            }
                        }
                    }
                }
                if(tout){
                    break;
                }
            }
            
            // Si no tout siginfica que hemos acabado el minimax.
            if(!tout){
                movimiento_def = movimiento;  
            }
            
        }

        // Reiniciamos la profundidad
        prof = 0;
        
        return movimiento_def;
    }

    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public String getName() {
        return "Minimax(OLA)";
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

        // Caso base: timeout es true
        if(tout) return 0;
       
        if(s.isGameOver()) {
            if (s.GetWinner() == CellType.opposite(color)) {
                //System.out.println("Caso base1a");
                return Integer.MAX_VALUE;
            } else {
                //System.out.println("Caso base2a");
                return Integer.MIN_VALUE;
            }
        } else if (pprof == 0) {
            // Comparación entre heurística nuestra y la del rival para ver 
            //quien tiene ventaja
            return getHeuristicaDef(s, color) - getHeuristicaDef(s, CellType.opposite(color));
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

                        // Pregunta: Este if es necesario?
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

        // Caso base: timeout es true
        if(tout) return 0;
        
        
        if(s.isGameOver()) {
            if (s.GetWinner() == CellType.opposite(color)) {
                //System.out.println("Caso base1b");
                return Integer.MIN_VALUE;
            } else {
               // System.out.println("Caso base2b");
                return Integer.MAX_VALUE;
            }
        } else if(pprof == 0){
            // Comparación entre heurística nuestra y la del rival para ver 
            //quien tiene ventaja
            return getHeuristicaDef(s, CellType.opposite(color)) - getHeuristicaDef(s,color);
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

    public int getHeuristicaAlter(GameStatus gs, CellType color) {
        
        int qn = gs.getNumberOfPiecesPerColor(color);
        
        int value = Integer.MIN_VALUE;
        
        ArrayList <Point> subconjMayor = new ArrayList(), 
                subconjunto = new ArrayList();
        ArrayList <Point> puntosFuera = new ArrayList();
       
        // Calculamos cuál es el subconjunto mayor y obtenemos todas 
        // las fichas que lo forman
     
        for(int i = 0; i < qn; i++) {
            
                subconjunto = creaSubConjunto(gs, gs.getPiece(color, i), color);
                
                if(subconjunto.size() > value) {
                    value = subconjunto.size();
                    subconjMayor = subconjunto;
                }                
        }
        
        // Excluimos los puntos del subconjunto para mover los otros.
        for(int i = 0; i< qn; i++) {
            Point puntoAux = gs.getPiece(color, i);
            if(!subconjMayor.contains(puntoAux)) puntosFuera.add(puntoAux);
        }
        
        int sumaEucl = 0;
        
        // Hacemos una suma de todas las euclidianas respecto al conjunto mayor
        // para poder saber cuánto de dispersas están las fichas.
        for(int i = 0; i < puntosFuera.size(); i++) {
            double minimEucl = Integer.MAX_VALUE;
            for(int j = 0; j < subconjMayor.size(); j++) {
                double eucl = Euclidiana(puntosFuera.get(i), subconjMayor.get(j));
                
                if(minimEucl > eucl) minimEucl = eucl;
            }
            
            sumaEucl += minimEucl;
        }
        
        // Caso base= si la sumaEucl es igual a la size de puntosFuera.size()
        // significa que están todas las fichas juntas y por tanto hemos ganado.
        if(sumaEucl == puntosFuera.size()){
           // System.out.println("Caso base Heurística: " + color);
            return Integer.MAX_VALUE;
        }
        
        return sumaEucl;
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
        
        for(int i = 0; i < listapuntos.size(); i++) {
            
            grupoAux = grupos.get(i);
            
            for(int j = i + 1; j < grupos.size() - 1; j++) {
                if(grupos.get(j).contains(listapuntos.get(i))){
                    grupoAux = concatena(grupoAux, grupos.get(j));
                    grupos.set(j, grupoAux);
                }
            }
            
        }
        
        int sizeMayor = -1000;
        ArrayList grupoMayor = new ArrayList();
        
        for(int i = 0; i < grupos.size(); i++) {
            
            if(grupos.get(i).size() > sizeMayor){
                sizeMayor = grupos.get(i).size();
                grupoMayor = grupos.get(i);
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
    
    public int getHeuristicaDef(GameStatus gs, CellType color) {
        
        int qn = gs.getNumberOfPiecesPerColor(color);
        
        int value = Integer.MIN_VALUE;
        
        ArrayList <Point> puntosFuera = new ArrayList();
        
        ArrayList <Point> grupoMaxi = grupoMayor(gs, color);
       
       
        
        // Excluimos los puntos del subconjunto para mover los otros.
        for(int i = 0; i< qn; i++) {
            Point puntoAux = gs.getPiece(color, i);
            if(!grupoMaxi.contains(puntoAux)) puntosFuera.add(puntoAux);
        }
        
        int sumaEucl = 0;
        
        // Hacemos una suma de todas las euclidianas respecto al conjunto mayor
        // para poder saber cuánto de dispersas están las fichas.
        for(int i = 0; i < puntosFuera.size(); i++) {
            
            double minimEucl = Integer.MAX_VALUE;
            
            for(int j = 0; j < grupoMaxi.size(); j++) {
                
                double eucl = Euclidiana(puntosFuera.get(i), grupoMaxi.get(j));
                
                if(minimEucl > eucl) minimEucl = eucl;
            }
            
            sumaEucl += minimEucl;
        }
        
        // Caso base= si la sumaEucl es igual a la size de puntosFuera.size()
        // significa que están todas las fichas juntas y por tanto hemos ganado.
        
        if(sumaEucl == puntosFuera.size()){
           // System.out.println("Caso base Heurística: " + color);
            return 100;
        }
        
        return sumaEucl + grupoMaxi.size();
    }
    
    
    
    // Función que agrupa todas las heuristicas y devuelve el resultado final
    // Se calculan los arrays necesarios en esta y se pasa por parametro.
    public int heuristicas(){
        int total = 0;
        
       
        
        
        
        return total;
    }
    //Le pasamos el status, el color y la array de moviemientos. Si posicion to == ficha rival, contar vecinas
    public int comeFicha(GameStatus gs, CellType color) {
        
        
    
         
        return 0;
    }


}
