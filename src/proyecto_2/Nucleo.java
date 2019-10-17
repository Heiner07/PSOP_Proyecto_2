/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import static java.util.Collections.reverse;
import java.util.List;
import java.util.Stack;
import javax.swing.Timer;

/**
 *
 * @author home
 */
public class Nucleo {
    
    private Proceso procesoEjecutando = null,procesoAnterior=null;
    private int numeroNucleo;
    private Boolean ejecutar = false;
    private Timer timerOperacion;
    private int tiempoRestante=1; // Variable que indicara cuantos segendos debe esperar hasta recibir otra instrucción
    private int tiempoEjecucion = 0; // El tiempo de ejecución en el que se encuentra el algoritmo
    private List<Proceso> procesos, ejecucionProcesos;
    
    //Datos para RR
    Stack<Proceso> pila;
    List<String> seq ;
    private int tiempoRR = 0;
    private int quantum=1;
    private boolean llenoPila = true;        
    
    public Nucleo(int numeroNucleo){
        this.numeroNucleo = numeroNucleo;
        this.procesos = new ArrayList<>();
        this.ejecucionProcesos = new ArrayList<>();
        this.pila = new Stack<>();
        this.seq = new ArrayList<>(); 
        
        
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
        switch (CPU.ALGORITMO_CPU) {
            case 0:
                algoritmoFCFS();
                break;
            case 1:
                algoritmoSJF();
                break;
            case 2:
                algoritmoRR();
                break;
            case 4:
                algoritmoHRRN();
                break;
            default:
                break;
        }
        
        // Ajusto las variables del sistema
        tiempoRestante = 1; // Establezco el tiempo de espera.
        tiempoEjecucion++; // Aumento el tiempo de ejecución en el que se encuentra
    }
    
    private void algoritmoRR(){
        
        if(llenoPila){
            llenoPila = false;
            //quantum = 1;//TEMPORAL
            Proceso primer_proceso = obtenerProcesoFCFS();
            pila.push(primer_proceso);
            //Meto a la pila el resto de los procesos con el mismo tiempo
            procesos.stream().filter((proceso) -> (proceso.obtenerTiempoLLegada() == primer_proceso.obtenerTiempoLLegada() && !(proceso.obtenerNombre().equals(primer_proceso.obtenerNombre())))).forEach((proceso) -> {
                pila.push(proceso);

            }); 
        }
        
        
        EjecucionAlgoritmoRR();
    }
    
    private void cambiarEstado_proceso(){
        for(int k=0;k<procesos.size();k++){
            if(procesos.get(k).obtenerEstadoUltimo()){           
               procesos.get(k).actualizarEstadoUltimo(false);
            }
        }
    }
    private void buscar_proceso(Proceso proceso){
        procesos.stream().filter((proc) -> (proc.obtenerNombre().equals(proceso.obtenerNombre()))).forEach((proc) -> {
            proc.actualizar_proceso(proceso);
        });
    }
    private void estadosProcesosRR(){
        if(procesoAnterior!=null){
            if(!(procesoAnterior.obtenerNombre().equals(procesoEjecutando.obtenerNombre()))){          
                if(procesoAnterior.obtenerRafagaTemp()>0){
                    procesoAnterior.establecerEstado(Proceso.PREPARADO); 
                }else{
                    procesoAnterior.establecerEstado(Proceso.TERMINADO);
                } 
            }              
        }   
    }
    private void EjecucionAlgoritmoRR(){
        if(!pila.isEmpty()){
            procesoEjecutando = pila.pop();     
            if(procesoEjecutando!=null){
                estadosProcesosRR();
                procesoAnterior = procesoEjecutando;               
                System.out.println("Le hice pop a "+procesoEjecutando.obtenerNombre()+" con rafaga de: "+ procesoEjecutando.obtenerRafagaTemp());
                if(procesoEjecutando.obtenerRafagaTemp()> 0){
                    int largo_secuencia = 0;
                    if(procesoEjecutando.obtenerRafagaTemp() <= quantum){
                        //Le sumo la rafaga a tiempo                               
                        tiempoRR += procesoEjecutando.obtenerRafagaTemp(); 
                        largo_secuencia = procesoEjecutando.obtenerRafagaTemp();
                        procesoEjecutando.restarRafagaTemp(procesoEjecutando.obtenerRafagaTemp());                             
                        procesoEjecutando.sumarTiempoLlegadaTemp(tiempoRR); 
                        cambiarEstado_proceso();
                        
                          
                    }else{
                        //Le sumo quantum a tiempo                               
                        tiempoRR += quantum;
                        largo_secuencia = quantum;
                        procesoEjecutando.restarRafagaTemp(quantum);                             
                        procesoEjecutando.sumarTiempoLlegadaTemp(tiempoRR); 
                        cambiarEstado_proceso();
                        
                        
                        procesoEjecutando.actualizarEstadoUltimo(true);          
                    }                              
                    buscar_proceso(procesoEjecutando);
                    System.out.println("Tiempo: "+tiempoRR);
                    Stack <Proceso> pila_temporal = new Stack<>();
                    
                    //AQUI YO GUARDO LOS QUANTUMS O LA RAFAGA QUE TENIA DISPONIBLE EL PROCESO, POR EJEMPLO SI SE GUARDAN 4 TIEMPOS, SE ESCIBEN LOS 4 EN 1 SEGUNDO
                    for(int k=0;k<largo_secuencia;k++){
                        
                        ejecucionProcesos.add(procesoEjecutando);
                        procesoEjecutando.establecerEstado(Proceso.EN_EJECUCION); 
                        seq.add(procesoEjecutando.obtenerNombre());
                    }
                    
                    for(int i=0;i<=tiempoRR;i++){                                    
                        for(int k=0;k<procesos.size();k++){
                            if(procesos.get(k).obtenerTiempoLLegadaTemp()==i && procesos.get(k).obtenerRafagaTemp()>0 && !(procesos.get(k).obtenerNombre().equals(procesoEjecutando.obtenerNombre())) && !(procesos.get(k).obtenerEstadoUltimo())){
                                if(pila.isEmpty()){
                                    pila_temporal.push(procesos.get(k));
                                }else if(pila.contains(procesos.get(k))){
                                } else {pila_temporal.push(procesos.get(k));

                                }
                            }

                        }
                    }
                    if(quantum>1){reverse(pila_temporal);}
                    pila.addAll(0,pila_temporal);
                    //Si solo hay uno por hacer se agrega este
                    if(pila.isEmpty() && procesoEjecutando.obtenerRafagaTemp()>0){
                        
                        procesoEjecutando.establecerEstado(Proceso.PREPARADO);
                        buscar_proceso(procesoEjecutando);//lo actualizo
                        pila.push(procesoEjecutando);
                    }
                    
                }         
            }//Sale del IF PRINCIPAL
            else{ejecucionProcesos.add(new Proceso());} // Agrego un proceso de relleno para la interfaz.
        }
        //Entra al else si la pila no tiene un proceso en ese tiempo
        else{
            //Si la pila esta vacia es porque en ese tiempo no hay procesos
            Proceso proceso_siguiente = obtenerProcesoFCFS();
            //obtengo el proceso siguiente, si es null es porque ya todos los procesos fueron analizados
            if(proceso_siguiente!=null){
                pila.push(proceso_siguiente);

                tiempoRR = proceso_siguiente.obtenerTiempoLLegadaTemp();
            }

        }
    }
    
    
    
    
    
    
    
    /**
     * Se encarga de retornar el Proceso siguiente a ejecutar según las condiciones del...
     * ... algoritmo HRRN (TiempoActual - TiempoLlegada + Rafaga) / Rafaga.
     * @return Proceso
     */
    private Proceso obtenerProcesoHRRN(){
        int cantidadProcesos = procesos.size();
        Proceso proceso = null, procesoTemp;
        for(int i = 0; i < cantidadProcesos; i++){
            // Obtengo un proceso de la lista para evaluar
            procesoTemp = procesos.get(i);
            
            /* Verifico que sea menor al limite de procesos por núcleos y la ráfaga sea mayor a 0.
              Además, el tiempo de llegada debe ser igual o menor en el que se encuentre la ejecución */
            if(procesoTemp.obtenerTiempoLLegada() <= tiempoEjecucion &&
                    procesoTemp.obtenerNumeroProceso() <= CPU.PROCESOSPORNUCLEO &&
                    procesoTemp.obtenerRafagaTemp() > 0){
                if(proceso == null){
                    /* Si es nulo, estoy empezando, entonces asigno el proceso */
                    proceso = procesoTemp;
                }else{
                    /* Si no es nulo, entonces estoy con un proceso, lo evaluo con el siguiente y escojo el de mayor...
                    ... valor */
                    float valorProcesoTemp =
                            (tiempoEjecucion - procesoTemp.obtenerTiempoLLegada() + procesoTemp.obtenerRafagaTemp()) /
                            (float)procesoTemp.obtenerRafagaTemp();
                    float valorProceso =
                            (tiempoEjecucion - proceso.obtenerTiempoLLegada() + proceso.obtenerRafagaTemp()) /
                            (float)proceso.obtenerRafagaTemp();
                    if(valorProcesoTemp > valorProceso){
                        proceso = procesoTemp;
                    }
                }
            }
        }return proceso;
    }
    
    /**
     * Se encarga de ejecutar el algoritmo HRRN de CPU
     */
    private void algoritmoHRRN(){
        /* Maneja el proceso saliente, para establecer su estado correspondiente */
        manejarProcesoSalienteFCFS();// Estoy usando el mismo del otro algoritmo, ya que funciona igual
        /* Obtengo el proceso siguiente del HRRN...
        ...(Puede ser el mismo del paso anterior, ya que no ha terminado la ráfaga) */
        procesoEjecutando = obtenerProcesoHRRN();
        if(procesoEjecutando != null){
            /* Si no es nulo, lo agrego a la lista de control del algoritmo (leida por la interfaz) */
            ejecucionProcesos.add(procesoEjecutando);
            procesoEjecutando.establecerEstado(Proceso.EN_EJECUCION); // Lo establezco en ejecución
            procesoEjecutando.restarRafagaTemp(1); // Resto la ráfaga.
        }else{
            ejecucionProcesos.add(new Proceso()); // Agrego un proceso de relleno para la interfaz.
        }
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
                    procesoTemp.obtenerRafagaTemp() > 0){
                if(proceso == null){
                    /* Si es nulo, estoy empezando, entonces asigno el proceso */
                    proceso = procesoTemp;
                }else{
                    /* Si no es nulo, entonces estoy con un proceso, lo evaluo con el siguiente y escojo el de menor...
                    ... tiempo de llegada */
                    if(procesoTemp.obtenerRafagaTemp() < proceso.obtenerRafagaTemp()){
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
            procesoEjecutando.restarRafagaTemp(1); // Resto la ráfaga.
        }else{
            ejecucionProcesos.add(new Proceso()); // Agrego un proceso de relleno para la interfaz.
        }
    }
    
    /**
     * Establece el estado correspondiente para el proceso saliente del algoritmo SJF
     */
    private void manejarProcesoSalienteSJF(){
        if(procesoEjecutando != null){
            if(procesoEjecutando.obtenerRafagaTemp() == 0){
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
            if(procesoTemp.obtenerTiempoLLegadaTemp() <= tiempoEjecucion &&
                    procesoTemp.obtenerNumeroProceso() <= CPU.PROCESOSPORNUCLEO &&
                    procesoTemp.obtenerRafagaTemp() > 0){
                if(proceso == null){
                    /* Si es nulo, estoy empezando, entonces asigno el proceso */
                    proceso = procesoTemp;
                }else{
                    /* Si no es nulo, entonces estoy con un proceso, lo evaluo con el siguiente y escojo el de menor...
                    ... tiempo de llegada */
                    if(procesoTemp.obtenerTiempoLLegadaTemp() < proceso.obtenerTiempoLLegadaTemp()){
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
            procesoEjecutando.restarRafagaTemp(1); // Resto la ráfaga.
        }else{
            ejecucionProcesos.add(new Proceso()); // Agrego un proceso de relleno para la interfaz.
        }
    }
    
    /**
     * Establece el estado correspondiente para el proceso saliente del algoritmo FCFS
     */
    private void manejarProcesoSalienteFCFS(){
        if(procesoEjecutando != null){
            if(procesoEjecutando.obtenerRafagaTemp() == 0){
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
            if(procesos.get(i).obtenerRafagaTemp() > 0){
                return false;
            }
        }
        if(procesoAnterior!=null){procesoAnterior.establecerEstado(Proceso.TERMINADO);}
        
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
