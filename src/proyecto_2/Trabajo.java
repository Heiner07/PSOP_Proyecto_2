/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_2;

/**
 *
 * @author Heiner
 */
public class Trabajo {
    int nucleoProcesador;
    int numeroBCP;
    String instruccion;
    
    public Trabajo(int nucleoProcesador, int numeroBCP){
        this.nucleoProcesador=nucleoProcesador;
        this.numeroBCP=numeroBCP;
       
    }
    public Trabajo(int nucleoProcesador, int numeroBCP,String instruccion){
        this.nucleoProcesador=nucleoProcesador;
        this.numeroBCP=numeroBCP;
        this.instruccion = instruccion;
       
    }
    
    public int obtenerNucleoProcesador(){
        return nucleoProcesador;
    }
    
    public int obtenerNumeroBCP(){
        return numeroBCP;
    }
    public String obtenerInstruccion(){
        return instruccion;
    
    }
}
