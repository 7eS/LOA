package edu.upc.epsevg.prop.loa;

import java.awt.Point;
import java.util.ArrayList;
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
    
    private ArrayList<Point> piezasCentro = new ArrayList();
    private ArrayList<Point> piezasFuera = new ArrayList();
    public ArrayList < ArrayList<Point> > grupos  = new ArrayList();
    
    public ElMeuStatus(int [][] tauler){
            super(tauler);
    }
    
    public ElMeuStatus(GameStatus gs){
        super(gs);
    }   

    
    public int grupoMayor(CellType color){
        
        ArrayList <Point> listapuntos = new ArrayList();
        
        int quantes = getNumberOfPiecesPerColor(color);
        
        Point aux = new Point();
        
        for(int i = 0; i<quantes;i++){
           aux = getPiece(color, i);
           listapuntos.add(aux);
           grupos.add(creaSubConjunto(this, aux));
        }
        
        for(int i = 0; i < listapuntos.size(); i++) {
            
            ArrayList<Point> grupoAux = grupos.get(i);
            
            for(int j = i + 1; j < grupos.size() - 1; j++) {
                if(grupos.get(j).contains(listapuntos.get(i))){
                    grupoAux = concatena(grupoAux, grupos.get(j));
                    grupos.set(j, grupoAux);
                }
            }
            
        }
        
        
        int sizeMayor = -100;
        ArrayList grupoMayor = new ArrayList();
        
        for(int i = 0; i < grupos.size(); i++) {
            if(grupos.get(i).size() > sizeMayor){
                sizeMayor = grupos.get(i).size();
                grupoMayor = grupos.get(i);
            }
        }
        
        grupos = grupoMayor;
        
        return sizeMayor;
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
    
    public int getHeuristicaAlter(GameStatus gs, CellType color) {
        
        int qn = gs.getNumberOfPiecesPerColor(color);
        
        int value = Integer.MIN_VALUE;
        
        ArrayList <Point> subconjMayor = new ArrayList(), 
                subconjunto = new ArrayList();
        ArrayList <Point> puntosFuera = new ArrayList();
       
        // Calculamos cuál es el subconjunto mayor y obtenemos todas 
        // las fichas que lo forman
     
        for(int i = 0; i < qn; i++) {
            
                subconjunto = creaSubConjunto(gs, gs.getPiece(color, i));
                
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
        if(sumaEucl == puntosFuera.size()) return Integer.MAX_VALUE;
        
        return sumaEucl + subconjMayor.size();
    }

    public double Euclidiana(Point from, Point center) {
        
        double calx = Math.pow(from.x - center.x, 2);
        double caly = Math.pow(from.y - center.y, 2);
        
        return Math.sqrt(calx + caly);
    }
    
    public boolean validaCoordenadas(int x, int y) {
        return (x >= 0 && x <= 7) && (y >= 0 && y <= 7);
    }
    
    public ArrayList creaSubConjunto(GameStatus s, Point from) {
        
        CellType colorNuestro = s.getCurrentPlayer();
        ArrayList <Point> puntos = new ArrayList();
        
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
}
