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
   
    private int tiempoRR = 0;
    private int quantum=1;
    private boolean llenoPila = true;  
    private int largo_secuencia = 0;
    private boolean esFeedBack = false;
    
    public Nucleo(int numeroNucleo){
        this.numeroNucleo = numeroNucleo;
        this.procesos = new ArrayList<>();
        this.ejecucionProcesos = new ArrayList<>();
        this.pila = new Stack<>();
        //System.out.println("Este es el quantum: "+CPU.QUANTUM);
        this.quantum = 1;
        
        
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
     * 0 = FCFSf
     */
    private void ejecutarAlgoritmoCPU(){
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
                esFeedBack = false;
                algoritmoRRFB();
                break;
            case 3:
                esFeedBack = true;
                algoritmoRRFB();
                break;
            case 4:
                algoritmoHRRN();
                break;
            case 5:
                algoritmoPrioridad();
                break;
            default:
                break;
        }
        
        // Ajusto las variables del sistema
        tiempoRestante = 1; // Establezco el tiempo de espera.
        tiempoEjecucion++; // Aumento el tiempo de ejecución en el que se encuentra
    }
    
    private void algoritmoRRFB(){
        
        if(llenoPila){
            llenoPila = false;
            Proceso primer_proceso = obtenerProcesoFCFS();
            if(primer_proceso!=null){
                pila.push(primer_proceso);
                //Meto a la pila el resto de los procesos con el mismo tiempo
                procesos.stream().filter((proceso) -> (proceso.obtenerTiempoLLegada() == primer_proceso.obtenerTiempoLLegada() && !(proceso.obtenerNombre().equals(primer_proceso.obtenerNombre())))).forEach((proceso) -> {
                    pila.push(proceso);

                });  
                EjecucionAlgoritmoRRFB();
            }else{
                ejecucionProcesos.add(new Proceso());
                llenoPila = true;
            }
        }         
        if(largo_secuencia > 0 && !llenoPila){
            procesoAInterfaz();
            
        }else{
            if(largo_secuencia==0 && !llenoPila){         
               manejarProcesoSalienteRRFB();            
               EjecucionAlgoritmoRRFB(); 
               if(!pila.isEmpty() || largo_secuencia==1){
                 procesoAInterfaz();
               }
            }
        
        }
    }
    private void procesoAInterfaz(){
        ejecucionProcesos.add(procesoEjecutando);
        procesoEjecutando.establecerEstado(Proceso.EN_EJECUCION);
        
        largo_secuencia--; 
    
    
    }
    private void manejarProcesoSalienteRRFB(){
        if(procesoEjecutando != null){
            
            if(procesoEjecutando.obtenerRafagaTemp() == 0){
                procesoEjecutando.establecerEstado(Proceso.TERMINADO);
            }else{
                if(pila.peek().obtenerNombre().equals(procesoEjecutando.obtenerNombre())){
                    procesoEjecutando.establecerEstado(Proceso.EN_EJECUCION);
                
                }else{procesoEjecutando.establecerEstado(Proceso.PREPARADO);}
                
            }
        }
    } 
    private void cambiarEstado_proceso(){
        for(int k=0;k<procesos.size();k++){
            if(procesos.get(k).obtenerEstadoUltimo()){           
               procesos.get(k).actualizarEstadoUltimo(false);
            }
        }
    }
    private Stack obtenerPrimerosPila(int tiempoInicio, int tiempoFinal){
        Stack <Proceso> primeros_procesos = new Stack<>();
        for(int i=tiempoInicio;i<=tiempoFinal;i++){                                    
            for (Proceso proceso : procesos) {
                if(proceso.obtenerTiempoLLegada()==i && !(proceso.obtenerEstadoUltimo()) && proceso.obtenerRafagaTemp()>0){
                    primeros_procesos.add(proceso);
                }
            }                    
        }//Sale del for
        return primeros_procesos;
    
    
    }
    
    private void restaRafaga(){
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
    }
    private Stack rellenarPilaTemporal(Stack pila_primeros){
        Stack <Proceso> pila_temporal = new Stack<>();
        for(int i=0;i<=tiempoRR;i++){                                    
            for (Proceso proceso : procesos) {
                if(proceso.obtenerTiempoLLegadaTemp()==i && proceso.obtenerRafagaTemp()>0 && !(proceso.obtenerNombre().equals(procesoEjecutando.obtenerNombre())) && !(proceso.obtenerEstadoUltimo())){
                    if(pila.isEmpty() || !pila.contains(proceso)){
                        //Si es FeedBack, verificamos que no se agregue el proceso que ya esta en la pila de primeros
                        if(esFeedBack){
                            if(!pila_primeros.contains(proceso)){
                                pila_temporal.push(proceso);                      
                            }                 
                        }else{
                            pila_temporal.push(proceso);
                        }
                        

                    }              
                }

            }
        }
        
        return pila_temporal;
    
    
    }
    private void EjecucionAlgoritmoRRFB(){
        if(!pila.isEmpty()){
            procesoEjecutando = pila.pop();     
            if(procesoEjecutando!=null){
                         
               //System.out.println("Le hice pop a "+procesoEjecutando.obtenerNombre()+" con rafaga de: "+ procesoEjecutando.obtenerRafagaTemp());
                if(procesoEjecutando.obtenerRafagaTemp()> 0){
                    
                    int tiempoSinProceso = tiempoRR;
                    //Se encarga de restar al proceso la rafaga que le corresponde.
                    restaRafaga();                           
                    //Obtengo los primeros procesos para el Feedback
                    Stack <Proceso> pila_primeros = obtenerPrimerosPila(tiempoSinProceso,tiempoRR);               
                    Stack <Proceso> pila_temporal = rellenarPilaTemporal(pila_primeros);                        
                    if(quantum>1){reverse(pila_temporal);}              
                    if(esFeedBack){
                        pila.addAll(pila_primeros);
                    }
                   
                   pila.addAll(0,pila_temporal);
                    //Si solo hay uno por hacer se agrega este
                    if(pila.isEmpty() && procesoEjecutando.obtenerRafagaTemp()>0){
                        
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
                procesoEjecutando = proceso_siguiente;
                //procesoEjecutando.establecerEstado(Proceso.EN_EJECUCION);
                pila.push(procesoEjecutando);
                tiempoRR = procesoEjecutando.obtenerTiempoLLegadaTemp();
                //ejecucionProcesos.add(new Proceso());
                EjecucionAlgoritmoRRFB();
                
            }else{
                ejecucionProcesos.add(new Proceso());
                procesoEjecutando.establecerEstado(Proceso.TERMINADO);
            }

        }
    }
    
    
    /**
     * Se encarga de retornar el Proceso siguiente a ejecutar según las condiciones del...
     * ... algoritmo Prioridad.
     * @return Proceso
     */
    private Proceso obtenerProcesoPrioridad(){
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
                    if(procesoTemp.obtenerPrioridad() < proceso.obtenerPrioridad()){
                        proceso = procesoTemp;
                    }
                }
            }
        }return proceso;
    }
    
    /**
     * Se encarga de ejecutar el algoritmo Prioridad de CPU
     */
    private void algoritmoPrioridad(){
        /* Maneja el proceso saliente, para establecer su estado correspondiente */
        manejarProcesoSalienteSJF();// Estoy usando el mismo del otro algoritmo, ya que funciona igual
        /* Obtengo el proceso siguiente del Prioridad...
        ...(Puede ser el mismo del paso anterior, ya que no ha terminado la ráfaga) */
        procesoEjecutando = obtenerProcesoPrioridad();
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
            //System.out.println("ESTOY ATENDIENDO: "+procesoEjecutando.obtenerNombre());
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
            if(procesos.get(i).obtenerRafagaTemp() > 0 || largo_secuencia>0){
                
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
    public void asignarQuantum(int quantumAsiganr){
        quantum = quantumAsiganr;
    
    }
    
    /**
     * Función llamada por el timerOperación que controla la ejecución de las operaciones en el núcleo.
     * Si el tiempo es cero entonces verifica si tiene que ejecutar los procesos, sino resta el tiempo...
     * ... para pasar al segundo siguiente.
     */
    private void controlEjecucionNucleo(){
        if(tiempoRestante == 0){
            if(ejecutar){
                ejecutarAlgoritmoCPU();
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
        pila.clear();
        llenoPila=true;
        tiempoRR = 0;
        
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
