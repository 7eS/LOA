package edu.upc.epsevg.prop.loa;

import java.awt.Point;
import java.util.ArrayList;

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
    
    public int getHeuristicaDef(GameStatus gs, CellType color) {
        
        int qn = gs.getNumberOfPiecesPerColor(color);
        
        ArrayList <Point> puntosFuera = new ArrayList();
        
        ArrayList <Point> grupoMaximo = grupoMayor(gs, color);
        
        int sumaEucl = 0;
        
        // Hacemos la distancia de los puntos de fuera respecto al grupo mayor
        
        for(int i = 0; i< qn; i++) {
            
            Point puntoAux = gs.getPiece(color, i);
            
            if(!grupoMaximo.contains(puntoAux)){
                
                puntosFuera.add(puntoAux);
                
                double minimEucl = Integer.MAX_VALUE;
            
                for(int j = 0; j < grupoMaximo.size(); j++) {

                    double eucl = Euclidiana(puntoAux, grupoMaximo.get(j));

                    if(minimEucl > eucl) minimEucl = eucl;
                }

                sumaEucl += minimEucl;
            }
        }
        
        return -sumaEucl + grupoMaximo.size();
    }
    
    public int block(GameStatus gs, CellType colorRival){
        
        int res = 0;
        
        int qn = gs.getNumberOfPiecesPerColor(colorRival);
        
        ArrayList <Point> puntosFuera = new ArrayList();
        
        ArrayList <Point> grupoMaximo = grupoMayor(gs, colorRival);
        
        int sumaEucl = 0;
        
        // Hacemos la distancia de los puntos de fuera respecto al grupo mayor
        
        for(int i = 0; i< qn; i++) {
            
            Point puntoRival = gs.getPiece(colorRival, i);
            
            if(!grupoMaximo.contains(puntoRival)){
                
                puntosFuera.add(puntoRival);
                
                double minimEucl = Integer.MAX_VALUE;
            
                for(int j = 0; j < puntosFuera.size(); j++) {
                        
                    int vecinasRival = creaSubConjunto(gs, puntoRival, colorRival).size();
                    
                    if (vecinasRival == 0) res = 20;
                    else if (vecinasRival == 1) res = 10;
                    else res = 0;
                    
                    // Añadir posibles medidas euclidianas/Mnahattan para complementar la heurist.
                }
            }
        }
        return res;
    }
    
    public int centre(GameStatus gs, CellType color){
        int res = 0;
        
        ArrayList<Point> centro = new ArrayList();
        ArrayList<Integer> test2 = new ArrayList();
        
        
        
        Point centro1 = new Point(3,3);
        Point centro2 = new Point(3,4);
        Point centro3 = new Point(4,3);
        Point centro4 = new Point(4,4);
        
        centro.add(centro1);
        centro.add(centro2);
        centro.add(centro3);
        centro.add(centro4);
        
        int qn = gs.getNumberOfPiecesPerColor(color);
        Point ficha;
        
        for (int i = 0; i < qn; i++) {
            ficha = gs.getPiece(color, i).getLocation();
            if(centro.contains(ficha)) res+=100;
            
            else if(gs.isInBounds(i, gs.findPiece(centro3, color))){
                System.out.println("no estamos juntas: "+ficha+""+centro3.getLocation());
                res+=50;
            }
            else{
                System.out.println("dist: "+ficha.distance(centro4));
                double dist = ficha.distance(centro4);
                res -= 10*dist;
            }        
        }
        int test[] = gs.getConnectedComponents();
        System.out.println("qn: "+qn);
        System.out.println("test size: "+test.length);
        System.out.println("valors test:");
        for (int i = 0; i < test.length; i++) {
            System.out.println("test[i]: "+test[i]);
            System.out.println(""+gs.getPiece(color, test[i]).getLocation()+"");
            
        }

        return res;
    }
    
    public int zobrist(GameStatus gs, CellType color, Move from, Move to){
        int res = 0;
        return res;
    }
    
    
    
}
