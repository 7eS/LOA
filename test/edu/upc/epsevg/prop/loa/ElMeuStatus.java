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
    
    public class Tuple {
        private Point from, cercano;
        private double valor;
        
        public Tuple() {
            
        }
        
        public Tuple(Point p, Point a, double v){
            from = p;
            cercano = a;
            valor = v;
        }

        @Override
        public String toString() {
            return '\n' + "Tuple{" + "from=" + from + ", cercano=" + cercano + ", valor=" + valor + '}';
        }
    }   
    
    private ArrayList<Point> piezasCentro = new ArrayList();
    private ArrayList<Point> piezasFuera = new ArrayList();
    private ArrayList<Tuple> euclidianas = new ArrayList();
    
    public ElMeuStatus(int [][] tauler){
            super(tauler);
    }
    
    public ElMeuStatus(GameStatus gs){
        super(gs);
    }   
     
    public int getHeuristica(){
        CellType color = this.getCurrentPlayer();
        
        int center = calculCenterHeur(this, color);
        
        return center;
    }
    
    public ArrayList grupoMayor(){
        
        
        
        return new ArrayList();
    }
    
    public int calculCenterHeur(GameStatus gs, CellType color) {
        
         int quantes = gs.getNumberOfPiecesPerColor(color);
         Point from = new Point(0,0);
         
         int cont  = 0;
         
         for(int i = 0; i< quantes; i++) {
             from = gs.getPiece(color, i);
             if(comprovaX(from) && comprovaY(from)) {
                 piezasCentro.add(from);
                 cont++;
             }
             else piezasFuera.add(from);
         }
         
         for(int i = 0; i < piezasFuera.size(); i++){
            from = piezasFuera.get(i);
            Tuple min = new Tuple();
            double minim = 10000;

            for(int j = 0; j < piezasCentro.size(); j++) {

                Point piezaCentro = piezasCentro.get(j);

                double eucl = Euclidiana(from, piezaCentro );

                if(minim > eucl) {
                    Tuple p = new Tuple (from, piezaCentro, eucl);
                    min = p;
                    minim = eucl;
                }
            }
             
             if(min.from != null) euclidianas.add(min);
         }
        
        return cont;
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
    
    public ArrayList retornaCentro(){
        return piezasCentro;
    }
    
    public String retornaEuclidiana(){
        String s = "";
        for(int i = 0; i < euclidianas.size(); ++i){
           s += euclidianas.get(i).toString();
        }
        return s;
    }
    
    public int pintaSize() {
        return euclidianas.size();
    }
}
