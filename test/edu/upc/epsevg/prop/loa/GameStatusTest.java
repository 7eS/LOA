/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.loa;

import edu.upc.epsevg.prop.loa.GameStatus;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Usuari
 */
public class GameStatusTest {
    
    public GameStatusTest() {
    }

    
    @Test
    public void testGetHeuristic() {
        
//        int matrix[][] = new int[][] {
//            {+0,-1,-1,-1,-1,-1,-1,+0},
//            {+1,+0,+0,+0,+0,+0,+0,+1},
//            {+1,+0,+0,+0,+0,+0,+0,+1},
//            {+1,+0,+0,+0,+0,+0,+0,+1},
//            {+1,+0,+0,+0,+0,+0,+0,+1},
//            {+1,+0,+0,+0,+0,+0,+0,+1},
//            {+1,+0,+0,+0,+0,+0,+0,+1},
//            {+0,-1,-1,-1,-1,-1,-1,+0}
//        };
//        ElMeuStatus gs = new ElMeuStatus(matrix);
//        System.out.println(gs.toString());
//        
//        System.out.println("heuristica: "+gs.getHeuristica());
//        System.out.println("=========================================================");
//               
//       
//        int matrix2[][] = 
//        new int[][] {
//            {+0,-1,-1,-1,-1,-1,-1,+0},
//            {+0,+0,+0,+0,+0,+0,+0,+0},
//            {+0,+0,+0,+1,+0,+0,+0,+1},
//            {+0,+0,+0,+1,+1,+1,+1,+0},
//            {+0,+0,+0,+1,+1,+0,+0,+1},
//            {+0,+0,+0,+1,+0,+0,+0,+1},
//            {+0,+0,+0,+0,+0,+0,+0,+0},
//            {+0,-1,-1,-1,-1,-1,-1,+0}
//        };            
//        GameStatus gs2 = new GameStatus(matrix2);
//        System.out.println(gs2.toString());
//        System.out.println(gs2.getPos(7, 2));
//        System.out.println("heuristica: "+gs.getHeuristica());
//        System.out.println("=========================================================");
//        
//        
//        int matrix3[][] = 
//        new int[][] {
//            {+0,-1,-1,-1,-1,-1,-1,+0},
//            {+0,+0,+0,+1,+0,+0,+0,+0},
//            {+0,+0,+0,+1,+0,+0,+0,+0},
//            {+0,+0,+0,+1,+1,+1,+0,+0},
//            {+0,+0,+0,+1,+1,+0,+0,+0},
//            {+0,+0,+0,+1,+0,+0,+0,+0},
//            {+0,+0,+0,+0,+0,+0,+0,+0},
//            {+0,-1,-1,-1,-1,-1,-1,+0}
//        };     

//         int matrix3[][] = 
//        new int[][] {
//            {+0,-1,-1,+0,+0,+0,+0,+0},
//            {+0,+0,+1,+0,+0,+0,+0,+1},
//            {+0,+0,+1,+0,+0,+1,+0,+0},
//            {+0,+0,+0,+0,+0,+0,+0,+0},
//            {+0,-1,+0,+0,+0,+0,+0,+0},
//            {+0,+0,+0,+0,+0,+0,+0,+0},
//            {+0,+0,+0,+0,+0,+0,+0,+0},
//            {+0,-1,+0,+0,+0,+0,+0,+0}
//        }; 

        int matrix3[][] = 
                new int[][] {
                    {+0,+0,+0,+0,+0,+0,+0,+0},
                    {+0,+0,+0,+1,+0,+0,+0,+0},
                    {+0,+0,+0,+1,+0,+0,+0,+0},
                    {+0,+0,+0,+1,+0,+0,+0,+1},
                    {+0,+0,+1,+1,+0,+0,+0,+0},
                    {+0,+0,+0,+1,+0,+0,+0,+0},
                    {+0,+0,+0,+0,+0,+0,+0,+0},
                    {+0,+0,+0,+0,+0,+0,+0,+0}
                }; 
        
        int matrix4[][] = 
                new int[][] {
                    {+0,+0,+0,+0,+0,+0,+0,+0},
                    {+0,+0,+0,+1,+0,+0,+0,+0},
                    {+0,+0,+0,+1,+0,+1,+0,+0},
                    {+0,+0,+0,+1,+0,+0,+0,+0},
                    {+0,+0,+0,+1,+0,+0,+0,+0},
                    {+0,+0,+0,+0,+0,+0,+0,+0},
                    {+0,+1,+0,+0,+1,+0,+0,+0},
                    {+0,+0,+0,+0,+0,+0,+0,+0}
                }; 
        
        ElMeuStatus gs3 = new ElMeuStatus(matrix3);
        ElMeuStatus gs4 = new ElMeuStatus(matrix4);
        System.out.println("Heur1: " + gs3.getHeuristicaDef(gs3, CellType.PLAYER1));
        System.out.println("Heur2: " + gs4.getHeuristicaDef(gs4, CellType.PLAYER1));
//        System.out.println("Pieza: " + gs3.getPiece(CellType.PLAYER1, 0).toString());
//        System.out.println(gs3.toString());
//        ArrayList hola = gs3.grupoMayor(gs3, CellType.PLAYER1);
//        System.out.println("Grupo Mayor:" + '\n' +hola + '\n' + hola.size());


        int matrix5[][] = 
                new int[][] {
                    {+0,-1,-1,-1,+0,+0,+0,+0},
                    {+0,+0,+0,+0,+0,+1,+0,+0},
                    {+1,+0,+0,+1,+0,-1,-1,+1},
                    {+0,+0,+0,+1,-1,+1,+0,+0},
                    {+1,+0,+0,+1,+0,+0,+0,+1},
                    {+1,+0,-1,+0,+0,+1,+0,-1},
                    {+0,+0,+0,+0,+1,+0,+0,+1},
                    {+0,-1,+0,+0,-1,+0,-1,+0}
                };
        
        int matrix6[][] = 
                new int[][] {
                    {+0,-1,-1,-1,+0,+0,+0,+0},
                    {+0,+0,+0,+0,+0,+1,+0,+0},
                    {+1,+1,+0,+1,+0,-1,-1,+0},
                    {+1,+1,+0,+1,-1,+1,+0,+0},
                    {+1,+0,+0,+1,+0,+0,+0,+0},
                    {+1,+0,-1,+0,+0,+1,+0,-1},
                    {+0,+0,+0,+0,+0,+0,+0,+1},
                    {+0,-1,+0,+0,-1,+0,-1,+0}
                };
        
        int matrix7[][] = 
                new int[][] {
                    {+0,+0,+0,+0,+0,+0,+0,+0},
                    {+0,+0,+0,+0,+0,+0,+0,+0},
                    {+0,+0,+0,+0,+0,+0,+0,+0},
                    {+0,+0,+0,+1,+1,+0,+0,+0},
                    {+0,+0,+0,+1,+0,+0,+0,+0},
                    {+0,+0,+0,+0,+0,+0,+0,+0},
                    {+0,+0,+0,+0,+0,+0,+0,+0},
                    {+0,+0,+0,+0,+0,+0,+0,+1}
                };
        
        ElMeuStatus gs7 = new ElMeuStatus(matrix7);
        //System.out.println("Heur1: " + gs3.block(gs3, CellType.PLAYER2));
        System.out.println("Heur3: " + gs7.centre(gs7, CellType.PLAYER1));



    }
   
}
