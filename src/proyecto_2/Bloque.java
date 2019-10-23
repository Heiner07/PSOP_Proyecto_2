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
public class Bloque {
    int inicio;
    int fin;
    int espacioUsado;
    Boolean ocupado;
    int indiceColor; // Variable para pintar las filas del color correspondiente al bloque

    public Bloque(int inicio, int fin, int espacioUsado, Boolean ocupado, int indiceColor) {
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
    
    /* Retorna los límites del bloque */
    public int[] obtenerInicioFin(){
        return new int[]{ inicio, fin };
    }
    
    /* Retorna los límites usados dentro del bloque */
    public int[] obtenerLimitesUsados(){
        if(inicio + espacioUsado > fin){
            return new int[]{ inicio, fin };
        }else{
            return new int[]{ inicio, inicio + espacioUsado };
        }
    }
    
    /* Retorna si el bloque está siendo usada por algún proceso */
    public Boolean estaOcupado(){
        return ocupado;
    }
    
    /* Retorna el color con el que se pintará en la interfaz (para hacer distinción entre bloques) */
    public int obtenerIndiceColor(){
        return indiceColor;
    }
    
    /* Retorna el espacio (tamaño del bloque) */
    public int obtenerEspacioBloque(){
        return fin - inicio + 1;
    }
}
