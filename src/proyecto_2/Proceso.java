/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 *
 * @author Heiner
 */
public class Proceso {
    
    // Estados del proceso
    static int NUEVO=0;
    static int EN_EJECUCION=1;
    static int EN_ESPERA=2;
    static int PREPARADO=3;
    static int TERMINADO=4;
    
    private int estadoProceso;
    private int numeroProceso;
    private int rafaga;
    private int tiempoLLegada;
    private int prioridad;
    private int tamanio;
    private int nucleo; // Se utiliza para un proceso en espera, así se sabe a que núcleo debe ir.
    private Timer timer;
    private int segundos;
    //Limites de memoria
    private int inicioMemoria, finMemoria;
    

    public Proceso(int estadoProceso, int numeroProceso, int rafaga, int tiempoLLegada, int prioridad, int tamanio,int inicioMemoria, int finMemoria, int nucleo){
        this.estadoProceso=estadoProceso;
        this.numeroProceso=numeroProceso;
        this.rafaga=rafaga;
        this.tiempoLLegada=tiempoLLegada;
        this.prioridad=prioridad;
        this.tamanio=tamanio;
        this.inicioMemoria=inicioMemoria;
        this.finMemoria=finMemoria;
        this.nucleo=nucleo;
        this.segundos=0;
        this.timer=new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                contadorTiempoEjecucion();
            }
        });
        this.timer.start();
    }
    
    /**
     * Se encarga de actualizar los datos del proceso, en caso de que...
     * no haya encontrado espacio en memoria. Cuando encuentra espacio en memoria...
     * se llama este método para actualizar los datos respectivos e iniciar su ejecución.
     * @param estadoProceso
     * @param inicioMemoria
     * @param finMemoria
     **/
    public void actualizarProceso(int estadoProceso,int inicioMemoria, int finMemoria){
        this.estadoProceso=estadoProceso;
        this.inicioMemoria = inicioMemoria;
        this.finMemoria = finMemoria;
    }
    
    /**
     * Función llamada por el timer que controla el tiempo de ejecución del proceso.
     * Solo aumenta el tiempo si el estado del proceso está en ejecución.
     */
    private void contadorTiempoEjecucion(){
        if(this.estadoProceso==Proceso.EN_EJECUCION){
            segundos++;
        }
    }

    public int obtenerNucleo(){
        return nucleo;
    }
    
    public int obtenerNumeroProceso(){
        return numeroProceso;
    }
   
    public int obtenerEstadoProceso(){
        return estadoProceso;
    }
    
    public int obtenerRafaga(){
        return rafaga;
    }
   
    public int obtenerTiempoLLegada(){
        return tiempoLLegada;
    }
    
    public int obtenerPrioridad(){
        return prioridad;
    }
   
    public int obtenerTamanio(){
        return tamanio;
    }
    
    public void establecerEstado(int estado){
        this.estadoProceso=estado;
    }
    
    public int obtenerInicioMemoria(){
        return inicioMemoria;
    }
    
    public int obtenerFinMemoria(){
        return finMemoria;
    }
    
    public int obtenerTiempoEjecucion(){
        return segundos;
    }
    
    /**
     * Recibe el estado del proceso como entero y devuelve la cadena(String) equivalente al estado recibido
     * @param estadoProceso
     * @return 
     */
    public static String estadoProcesoCadena(int estadoProceso){
        String estadoProcesoCadena;
        if(estadoProceso==Proceso.EN_EJECUCION){
            estadoProcesoCadena="En Ejecución";
        }else if(estadoProceso==Proceso.EN_ESPERA){
            estadoProcesoCadena="En Espera";
        }else if(estadoProceso==Proceso.NUEVO){
            estadoProcesoCadena="Nuevo";
        }else if(estadoProceso==Proceso.PREPARADO){
            estadoProcesoCadena="Preparado";
        }else{
            estadoProcesoCadena="Terminado";
        }return estadoProcesoCadena;
    }
}
