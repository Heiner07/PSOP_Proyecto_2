/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

/**
 *
 * @author home
 */
public class Nucleo {
    
    private Proceso procesoEjecutando = null;
    private int numeroNucleo;
    private Boolean ejecutar = false;
    private Timer timerOperacion;
    private int tiempoRestante=1; // Variable que indicara cuantos segendos debe esperar hasta recibir otra instrucción
    private List<Proceso> procesos, ejecucionProcesos;
    
    public Nucleo(int numeroNucleo){
        this.numeroNucleo = numeroNucleo;
        this.procesos = new ArrayList<>();
        this.ejecucionProcesos = new ArrayList<>();
        
        timerOperacion = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                controlEjecucionNucleo();
            }
        });
        // Inicializo el timer.
        timerOperacion.start();
    }
    
    /**
     * Función llamada por el controlEjecucionNucleo.
     * Ejecuta el algoritmo correspondiente a lo seleccionado por el usuario.
     * 0 = FCFS
     */
    private void ejecutarAlgoritmo(){
        if(CPU.ALGORITMO_CPU == 0){
            algoritmoFCFS();
        }
    }
    
    /**
     * Se encarga de retornar el Proceso siguiente a ejecutar según las condiciones del...
     * ... algoritmo FCFS.
     * @return Proceso
     */
    private Proceso obtenerProcesoFCFS(){
        int cantidadProcesos = procesos.size();
        Proceso proceso = null, procesoTemp;
        for(int i = 0; i < cantidadProcesos; i++){
            procesoTemp = procesos.get(i);
            if(proceso == null){
                /* Si es nulo, estoy empezando o ya hay proceso valido por ejecutar */
                if(procesoTemp.obtenerRafaga() > 0 && procesoTemp.obtenerNumeroProceso() <= CPU.PROCESOSPORNUCLEO){
                    /* Si estoy empezando, asigno el proceso siempre y cuando...
                    ...sea menor al limite de procesos por núcleos y la ráfaga sea mayor a 0 */
                    proceso = procesos.get(i);
                }
            }else{
                /* Si no es nulo, entonces estoy con un proceso, lo evaluo con el siguiente y escojo el de menor...
                ... tiempo de llegada y que sea menor al limite de procesos por núcleos */
                if(procesoTemp.obtenerTiempoLLegada() < proceso.obtenerTiempoLLegada() &&
                        procesoTemp.obtenerRafaga() > 0 && procesoTemp.obtenerNumeroProceso() <= CPU.PROCESOSPORNUCLEO){
                    
                    proceso = procesos.get(i);
                }
            }
        }return proceso;
    }
    
    /**
     * Se encarga de ejecutar el algoritmo FCFS de CPU
     */
    private void algoritmoFCFS(){
        /* Maneja el proceso saliente, para establecer su estado correspondiente */
        manejarProcesoSalienteFCFS();
        /* Obtengo el proceso siguiente del FCFS...
        ...(Puede ser el mismo del paso anterior, ya que no ha terminado la ráfaga) */
        procesoEjecutando = obtenerProcesoFCFS();
        if(procesoEjecutando != null){
            /* Si no es nulo, lo agrego a la lista de control del algoritmo (leida por la interfaz) */
            ejecucionProcesos.add(procesoEjecutando);
            procesoEjecutando.establecerEstado(Proceso.EN_EJECUCION); // Lo establezco en ejecución
            procesoEjecutando.restarRafaga(); // Resto la ráfaga.
            tiempoRestante = 1; // Establezco el tiempo de espera.
        }else{
            /* Si es nulo, el algoritmo ya finalizó */
            ejecutar = false;
        }
    }
    
    /**
     * Establece el estado correspondiente para el proceso saliente del algoritmo FCFS
     */
    private void manejarProcesoSalienteFCFS(){
        if(procesoEjecutando != null){
            if(procesoEjecutando.obtenerRafaga() == 0){
                procesoEjecutando.establecerEstado(Proceso.TERMINADO);
            }
        }
    }
    
    public void agregarProceso(Proceso proceso){
        procesos.add(proceso);
    }
    
    public List<Proceso> obtenerProcesos(){
        return procesos;
    }
    
    public List<Proceso> obtenerEjecucionProcesos(){
        return ejecucionProcesos;
    }
    
    public int obtenerNumeroProcesoSiguiente(){
        return procesos.size() + 1;
    }
    
    public void empezarEjecucion(){
        ejecutar = true;
    }
    
    /**
     * Función llamada por el timerOperación que controla la ejecución de las operaciones en el núcleo.
     * Si el tiempo es cero entonces verifica si tiene que ejecutar los procesos, sino resta el tiempo...
     * ... para pasar al segundo siguiente.
     */
    private void controlEjecucionNucleo(){
        if(tiempoRestante == 0){
            if(ejecutar){
                ejecutarAlgoritmo();
            }
        }else{
            tiempoRestante--;
        }
    }
    
    public void limpiarProcesos(){
        procesos.clear();
        ejecucionProcesos.clear();
        procesoEjecutando = null;
        ejecutar = false;
    }
    
    public Boolean obtenerEstado(){
        return ejecutar;
    }
    
    /**
     * Recibe el proceso de la cola de trabajo. Y lo pone a ejecutar.
     * @param proceso
     * @throws InterruptedException 
     */
    public void recibirProceso(Proceso proceso) throws InterruptedException{
        
    }
}
