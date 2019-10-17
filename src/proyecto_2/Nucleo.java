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
    private int tiempoEjecucion = 0; // El tiempo de ejecución en el que se encuentra el algoritmo
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
        /* Primeramente compruebo si ya finalizó */
        if(ejecucionTerminada()){
            ejecutar = false;
        }
        
        /* Ejecuto el algoritmo correspondiente */
        if(CPU.ALGORITMO_CPU == 0){
            algoritmoFCFS();
        }else if(CPU.ALGORITMO_CPU == 1){
            algoritmoSJF();
        }
        
        // Ajusto las variables del sistema
        tiempoRestante = 1; // Establezco el tiempo de espera.
        tiempoEjecucion++; // Aumento el tiempo de ejecución en el que se encuentra
    }
    
    /**
     * Se encarga de retornar el Proceso siguiente a ejecutar según las condiciones del...
     * ... algoritmo SJF.
     * @return Proceso
     */
    private Proceso obtenerProcesoSJF(){
        int cantidadProcesos = procesos.size();
        Proceso proceso = null, procesoTemp;
        for(int i = 0; i < cantidadProcesos; i++){
            // Obtengo un proceso de la lista para evaluar
            procesoTemp = procesos.get(i);
            
            /* Verifico que sea menor al limite de procesos por núcleos y la ráfaga sea mayor a 0.
              Además, el tiempo de llegada debe ser igual o menor en el que se encuentre la ejecución */
            if(procesoTemp.obtenerTiempoLLegada() <= tiempoEjecucion &&
                    procesoTemp.obtenerNumeroProceso() <= CPU.PROCESOSPORNUCLEO &&
                    procesoTemp.obtenerRafaga() > 0){
                if(proceso == null){
                    /* Si es nulo, estoy empezando, entonces asigno el proceso */
                    proceso = procesoTemp;
                }else{
                    /* Si no es nulo, entonces estoy con un proceso, lo evaluo con el siguiente y escojo el de menor...
                    ... tiempo de llegada */
                    if(procesoTemp.obtenerRafaga() < proceso.obtenerRafaga()){
                        proceso = procesoTemp;
                    }
                }
            }
        }return proceso;
    }
    
    /**
     * Se encarga de ejecutar el algoritmo SJF de CPU
     */
    private void algoritmoSJF(){
        /* Maneja el proceso saliente, para establecer su estado correspondiente */
        manejarProcesoSalienteSJF();
        /* Obtengo el proceso siguiente del SJF...
        ...(Puede ser el mismo del paso anterior, ya que no hay un proceso con ráfaga menor) */
        procesoEjecutando = obtenerProcesoSJF();
        if(procesoEjecutando != null){
            /* Si no es nulo, lo agrego a la lista de control del algoritmo (leida por la interfaz) */
            ejecucionProcesos.add(procesoEjecutando);
            procesoEjecutando.establecerEstado(Proceso.EN_EJECUCION); // Lo establezco en ejecución
            procesoEjecutando.restarRafaga(); // Resto la ráfaga.
        }else{
            ejecucionProcesos.add(new Proceso()); // Agrego un proceso de relleno para la interfaz.
        }
    }
    
    /**
     * Establece el estado correspondiente para el proceso saliente del algoritmo SJF
     */
    private void manejarProcesoSalienteSJF(){
        if(procesoEjecutando != null){
            if(procesoEjecutando.obtenerRafaga() == 0){
                procesoEjecutando.establecerEstado(Proceso.TERMINADO);
            }else{
                procesoEjecutando.establecerEstado(Proceso.PREPARADO);
            }
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
            // Obtengo un proceso de la lista para evaluar
            procesoTemp = procesos.get(i);
            
            /* Verifico que sea menor al limite de procesos por núcleos y la ráfaga sea mayor a 0.
              Además, el tiempo de llegada debe ser igual o menor en el que se encuentre la ejecución */
            if(procesoTemp.obtenerTiempoLLegada() <= tiempoEjecucion &&
                    procesoTemp.obtenerNumeroProceso() <= CPU.PROCESOSPORNUCLEO &&
                    procesoTemp.obtenerRafaga() > 0){
                if(proceso == null){
                    /* Si es nulo, estoy empezando, entonces asigno el proceso */
                    proceso = procesoTemp;
                }else{
                    /* Si no es nulo, entonces estoy con un proceso, lo evaluo con el siguiente y escojo el de menor...
                    ... tiempo de llegada */
                    if(procesoTemp.obtenerTiempoLLegada() < proceso.obtenerTiempoLLegada()){
                        proceso = procesoTemp;
                    }
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
        }else{
            ejecucionProcesos.add(new Proceso()); // Agrego un proceso de relleno para la interfaz.
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
    
    /**
     * Se encarga de verificar si ya todos los procesos terminaron de ejecutarse.
     * Se llama al final de ejecutarAlgoritmo
     * @return Boolean
     */
    private Boolean ejecucionTerminada(){
        int cantidadProcesos = procesos.size();
        for(int i = 0; i < cantidadProcesos; i++){
            /* Si algún proceso tiene ráfaga por ejecutar, entonces no se ha finalizado */
            if(procesos.get(i).obtenerRafaga() > 0){
                return false;
            }
        }
        return true;
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
        tiempoEjecucion = 0;
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
