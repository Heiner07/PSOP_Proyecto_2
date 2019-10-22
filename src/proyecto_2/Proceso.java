/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javafx.util.Pair;
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
    
    
    private String nombre;
    private int estadoProceso;
    private int numeroProceso;
    private int rafaga;
    private int rafagaTemp;
    private int tiempoLLegada;
    private int tiempoLlegadaTemp;
    private boolean estadoUltimo;
    private int prioridad;
    private int tamanio;
    private int nucleo; // Se utiliza para un proceso en espera, así se sabe a que núcleo debe ir.
    private Timer timer;
    private int segundos;
    //Limites de memoria
    private int inicioMemoria, finMemoria;
    private List<Pair<Integer, Integer>> frames;
    

    public Proceso(String nombre, int estadoProceso, int numeroProceso, int rafaga, int tiempoLLegada, int prioridad, int tamanio, int nucleo){
        this.nombre=nombre;
        this.estadoProceso=estadoProceso;
        this.numeroProceso=numeroProceso;
        this.rafaga=rafaga;
        this.rafagaTemp = rafaga;
        this.tiempoLLegada=tiempoLLegada;
        this.tiempoLlegadaTemp = tiempoLLegada;
        this.prioridad=prioridad;
        this.tamanio=tamanio;
        this.nucleo=nucleo;
        this.estadoUltimo=false;
        this.segundos=0;
        this.timer=new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                contadorTiempoEjecucion();
            }
        });
        this.timer.start();
    }
    
    /* Proceso basura, sirve de relleno para que la interfaz no pinte campos incorrectos */
    public Proceso(){
        this.estadoProceso = -1;
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
    
    public void establecerLimitesMemoria(int[] limites){
        if(limites[0] == -1){
            inicioMemoria = -1;
            finMemoria = -1;
            estadoProceso = Proceso.NUEVO;
        }else{
            inicioMemoria = limites[0];
            finMemoria = limites[1];
        }
    }
    
    public void establecerFrames(List<Pair<Integer, Integer>> frames){
        int cantidadFrames = frames.size();
        int tamanioFrames = 0;
        for(int i = 0; i < cantidadFrames; i++){
            tamanioFrames += frames.get(i).getValue();
        }
        /* Preguntar, qué pasa si no se cargan todos los frames de un proceso,
           lo ejecuto o libero los frames que sí pudo cargar */
        if(tamanioFrames < tamanio){
            estadoProceso = Proceso.NUEVO;
        }
        this.frames = frames;
    }
    
    public int espacioFaltante(){
        return inicioMemoria + tamanio-1 - finMemoria;// Menos 1, hay que considerar la posicion 0
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
    
    public void actualizar_proceso(Proceso proc){
        this.rafagaTemp=proc.obtenerRafagaTemp();
        this.tiempoLlegadaTemp=proc.obtenerTiempoLLegadaTemp();  
        this.estadoProceso = proc.obtenerEstadoProceso();
    }
    public boolean obtenerEstadoUltimo(){
        return estadoUltimo;
    
    }
    
    public double obtenerTrTs(){
        return (double)(tiempoLlegadaTemp-tiempoLLegada)/rafaga;
    
    }
    public int obtenerTurnaround(){
        return (tiempoLlegadaTemp-tiempoLLegada);
    
    }
    
    public void actualizarEstadoUltimo(boolean estado){
        this.estadoUltimo = estado;
    }
    public int obtenerNucleo(){
        return nucleo;
    }
    
    public String obtenerNombre(){
        return nombre;
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
   
    public int obtenerRafagaTemp(){
        return rafagaTemp;
    }
    public int obtenerTiempoLLegada(){
        return tiempoLLegada;
    }
    public int obtenerTiempoLLegadaTemp(){
        return tiempoLlegadaTemp;
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
    
    public List<Pair<Integer, Integer>> obtenerFrames(){
        return frames;
    }
    
    public void restarRafagaTemp(int n){
        this.rafagaTemp -= n;
    }
    
    public void sumarTiempoLlegadaTemp(int n){
        this.tiempoLlegadaTemp = n;
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
