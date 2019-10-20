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
public class BloqueFijo {
    int inicio;
    int fin;
    int espacioUsado;
    Boolean ocupado;
    int indiceColor; // Variable para pintar las filas del color correspondiente al bloque

    public BloqueFijo(int inicio, int fin, int espacioUsado, Boolean ocupado, int indiceColor) {
        this.inicio = inicio;
        this.fin = fin;
        this.espacioUsado = espacioUsado;
        this.ocupado = ocupado;
        this.indiceColor = indiceColor;
    }
    
    public void asignarEspacioUsado(int espacio){
        this.espacioUsado = espacio;
        if(espacio > 0){
            ocupado = true;
        }
    }
    
    public int[] obtenerInicioFin(){
        return new int[]{ inicio, fin };
    }
    
    public int[] obtenerLimitesUsados(){
        if(inicio + espacioUsado > fin){
            return new int[]{ inicio, fin };
        }else{
            return new int[]{ inicio, inicio + espacioUsado };
        }
    }
    
    public Boolean estaOcupado(){
        return ocupado;
    }
    
    public int obtenerIndiceColor(){
        return indiceColor;
    }
}
