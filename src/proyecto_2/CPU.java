/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;


/**
 *
 * @author Heiner
 */
public class CPU {
    
    static int LARGOMEMORIA = 128;
    static int LARGODISCO = 1024;
    static int LARGOMEMORIAVIRTUAL = LARGOMEMORIA+LARGODISCO/2;
    static int LARGOPILA = 10;
    static String[] memoriaVirtual = new String[LARGOMEMORIAVIRTUAL];
    static String[] memoria = new String[LARGOMEMORIA];
    static String[] disco = new String[LARGODISCO];
    private Nucleo nucleo1, nucleo2;
    private List<Trabajo> colaTrabajoN1, colaTrabajoN2;
    private List<BCP> procesos;
    private int idProceso;
    private String[] parametros;
    private int procesoCola1,procesoCola2;
    
    static List<Trabajo> colaImprimir1,colaImprimir2;

    
    
    
    
    
    /* Hilos de Control */
    private Timer timerControlColasNucleos, timerControlMemoriaVirtual,timerControlProcesos;
    
    public CPU(){
        this.nucleo1 = new Nucleo(0);
        this.nucleo2 = new Nucleo(1);
        this.colaTrabajoN1 = new ArrayList<>();
        this.colaTrabajoN2 = new ArrayList<>();
        CPU.colaImprimir1 = new ArrayList<>();
        CPU.colaImprimir2 = new ArrayList<>();
        this.procesos = new ArrayList<>();
        this.idProceso = 0;
        inicializaMemoria();
        inicializarDisco();
        inicializarMemoriaVirtual();
        configurarHilos();
    }
    
    public void establecerValoresMemoriaDisco(int lMemoria, int lDisco){
        LARGOMEMORIA = lMemoria;
        LARGODISCO = lDisco;
        LARGOMEMORIAVIRTUAL = LARGOMEMORIA+LARGODISCO/2;
        memoriaVirtual = new String[LARGOMEMORIAVIRTUAL];
        memoria = new String[LARGOMEMORIA];
        disco = new String[LARGODISCO];
        inicializaMemoria();
        inicializarDisco();
        inicializarMemoriaVirtual();
    }
    
    private void inicializaMemoria(){
        for(int i=0;i<CPU.LARGOMEMORIA;i++){
            CPU.memoria[i] = "0000 0000 00000000";
        }
    }
    
    private void inicializarDisco(){
        for(int i=0;i<CPU.LARGODISCO;i++){
            CPU.disco[i] = "0000 0000 00000000";
        }
    }
    
    private void inicializarMemoriaVirtual(){
        for(int i=0;i<CPU.LARGOMEMORIAVIRTUAL;i++){
            CPU.memoriaVirtual[i] = "0000 0000 00000000";
        }
    }
    
    /**
     * Se encarga de llamar a las distintas funciones que controlan la ejecucion de la instrucciones.
     */
    private void configurarHilos(){
        configurarHiloColasNucleos();
        configurarHiloMemoriaVirtual();
        configurarHiloProcesos();
    }
    
    /**
     * Establece la funcion para el timer timerControlMemoriaVirtual que se encargará de establecer...
     * ...los valores de la memoria virtual en la memoria o en el disco (como memoria virtual).
     */
    private void configurarHiloMemoriaVirtual(){
        timerControlMemoriaVirtual = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // Función que repetirá según el intervalo asignado (1 segundo).
                controlMemoriaVirtual();
            }
        });
        // Inicializo el timer.
        timerControlMemoriaVirtual.start();
    }
    
    /**
     * Establece los valores de la memoria virtual en la memoria y en el disco según corresponda.
     * Función llamada por el timerControlMemoriaVirtual.
     */
    private void controlMemoriaVirtual(){
        for(int i=0;i<CPU.LARGOMEMORIAVIRTUAL;i++){
            if(i<CPU.LARGOMEMORIA){
                CPU.memoria[i]=CPU.memoriaVirtual[i];
            }else{
                CPU.disco[i-CPU.LARGOMEMORIA]=CPU.memoriaVirtual[i];
            }
        }
    }
    
    /**
     * Establece la funcion para el timer timerControlColasNucleos que se encargará de enviar...
     * ...las instrucciones al procesador correspondiente.
     */
    private void configurarHiloColasNucleos(){
        timerControlColasNucleos = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    // Función que repetirá según el intervalo asignado (1 segundo).
                    verificarColas();
                    
                    
                } catch (InterruptedException ex) {
                    // Modificar para mostrar mensaje correspondiente.
                    Logger.getLogger(CPU.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        // Inicializo el timer.
        timerControlColasNucleos.start();
    }
    /**
     * Establece la funcion para el timer timerControlProcesos que se encargará de enviar...
     * ...los procesos constantemente
     */
    private void configurarHiloProcesos(){
        timerControlProcesos = new Timer(1000, (ActionEvent ae) -> {
            // Modificar para mostrar mensaje correspondiente.           
            verificarProcesos();
        });
        // Inicializo el timer.
        timerControlProcesos.start();
    }
    /**
     * Verifica cuando un núcleo queda disponible y le envía el proceso siguiente.
     * Función llamada por el timerControlColasNucleos.
     * @throws InterruptedException 
     */
    private void verificarColas() throws InterruptedException{

        if(!colaTrabajoN1.isEmpty()){              
            if(nucleo1.obtenerEstado()){              
               colaImprimir1.clear();
               BCP procesoAEjecutar = retornarProceso(1);
               listadoColas(1);
               if(colaImprimir1.isEmpty()){
                   colaImprimir1 = new ArrayList<>();              
               }             
               procesoCola1++;
               if(procesoAEjecutar!=null){                  
                   nucleo1.recibirProceso(procesoAEjecutar);                  
               }               
            }
        }
        if(!colaTrabajoN2.isEmpty()){
            if(nucleo2.obtenerEstado()){
               colaImprimir2.clear();
               BCP procesoAEjecutar = retornarProceso(2);
               listadoColas(2);
               if(colaImprimir2.isEmpty()){
                   colaImprimir2 = new ArrayList<>();              
               }
               procesoCola2++;
               if(procesoAEjecutar!=null){                  
                   nucleo2.recibirProceso(procesoAEjecutar);                 
               }
            }
        }
    }
    
    /*
    Verifica constantemente cuando los procesos estan en espera de ser colocados a la memoria.
    Lo llama el hilo correspondiente
    Si tiene espacio en memoria, edita las posiciones de memoria.
    */
    private void verificarProcesos(){
        for(int i=0;i<procesos.size();i++){        
            BCP proc = procesos.get(i);
            if(proc.obtenerInicioMemoria() == -1 && (proc.obtenerEstadoProceso()==BCP.NUEVO)){
                int[] finInicioMemoria = determinarPosicionesMemoria(proc.obtenerInstruccionesMemoria().size());
                if(finInicioMemoria[0] != -1){
                    // Si hay espacio, entonces sí se cargar las instrucciones en memoria.
                    cargarInstrucciones(finInicioMemoria[0],finInicioMemoria[1],proc.obtenerInstruccionesMemoria());
                    agregarProcesoCola(proc.obtenerNucleo(),proc.obtenerNumeroProceso());
                    proc.actualizarProceso(BCP.PREPARADO,finInicioMemoria[0],finInicioMemoria[1]);
                    
                
                }//SALE IF2
            
            }//SALE IF1
        }//SALE FOR
    
    }
   
    /**
     * Se encarga de retornar el BCP del proceso que se va a ejecutar en el núcleo
     * Con respecto a numeroCola que ingresa ya sea 1 o 2
     * @param numeroCola
     * @return  un BCP*/
    private BCP retornarProceso(int numeroCola){
        if(numeroCola == 1){
            int largoCola = colaTrabajoN1.size()-1;
            
            if(procesoCola1 > largoCola){
               procesoCola1 = 0;          
            }
            int proc;
            for(;procesoCola1<=largoCola;procesoCola1++){
                proc = colaTrabajoN1.get(procesoCola1).numeroBCP;
                BCP procesoAEjecutar = obtenerBCP(proc);
                if(procesoAEjecutar.obtenerPC() <= procesoAEjecutar.obtenerFinMemoria() && procesoAEjecutar.obtenerEstadoProceso()!=BCP.TERMINADO){
                    return procesoAEjecutar;                   
                }                             
            }           
        }else{
            int largoCola = colaTrabajoN2.size()-1;           
            if(procesoCola2 > largoCola){
               procesoCola2 = 0;          
            }                   
            int proc;
            for(;procesoCola2<=largoCola;procesoCola2++){
                proc = colaTrabajoN2.get(procesoCola2).numeroBCP;
                BCP procesoAEjecutar = obtenerBCP(proc);
                if(procesoAEjecutar.obtenerPC() <= procesoAEjecutar.obtenerFinMemoria() && procesoAEjecutar.obtenerEstadoProceso()!=BCP.TERMINADO){
                    return procesoAEjecutar;                  
                }                            
            }  
        }//SALE ELSE
        return null;
    }
    
    /**
     *Entra como parametro el número de cola, se encarga
     * de llenar las colas de trabajo con sus respectivos procesos
     * e instrucciones que se van a ejecutar
     * @param numeroCola
     */
    private void listadoColas(int numeroCola){
        Trabajo trabajo;
        int numProceso;
        if(numeroCola == 1){           
            numProceso = procesoCola1;
            int largoCola = colaTrabajoN1.size()-1;          
            if(numProceso > largoCola){
               numProceso = 0;          
            }
            int proc;
            for(;numProceso<=largoCola;numProceso++){
                proc = colaTrabajoN1.get(numProceso).numeroBCP;
                BCP procesoAEjecutar = obtenerBCP(proc);
                if(procesoAEjecutar.obtenerPC() <= procesoAEjecutar.obtenerFinMemoria() && procesoAEjecutar.obtenerEstadoProceso()!=BCP.TERMINADO){
                    trabajo=new Trabajo(0, proc,memoriaVirtual[procesoAEjecutar.obtenerPC()]);
                    colaImprimir1.add(trabajo);                   
                }                             
            }            
        }else{          
            numProceso = procesoCola2;
            int largoCola = colaTrabajoN2.size()-1;          
            if(numProceso > largoCola){
               numProceso = 0;          
            }
            int proc;
            for(;numProceso<=largoCola;numProceso++){
                proc = colaTrabajoN2.get(numProceso).numeroBCP;
                BCP procesoAEjecutar = obtenerBCP(proc);          
                if(procesoAEjecutar.obtenerPC() <= procesoAEjecutar.obtenerFinMemoria() && procesoAEjecutar.obtenerEstadoProceso()!=BCP.TERMINADO){
                    trabajo=new Trabajo(1, proc,memoriaVirtual[procesoAEjecutar.obtenerPC()]);
                    colaImprimir2.add(trabajo);                   
                }                              
            }              
        }//SALE ELSE       
    }
    
    public Nucleo obtenerNucleo1(){
        return nucleo1;
    }
    
    public Nucleo obtenerNucleo2(){
        return nucleo2;
    }
    
    public List<Trabajo> obtenerColaTrabajoN1(){
        return colaTrabajoN1;
    }
    
    public List<Trabajo> obtenerColaTrabajoN2(){
        return colaTrabajoN2;
    }
    
    public List<BCP> obtenerProcesos(){
        return procesos;
    }
    
    private BCP obtenerBCP(int numeroBCP){
        int numeroProcesos = procesos.size();
        BCP proceso;
        for(int i=0;i<numeroProcesos;i++){
            proceso=procesos.get(i);
            if(proceso.obtenerNumeroProceso()==numeroBCP){
                return proceso;
            }
        }return null;
    }
    
    /*public List<String> cargarProgramas(List<String> archivos){
        int cantidadArchivos=archivos.size();
        List<String> erroresLectura = new ArrayList<>(); // Almacena los archivos donde ocurrió un error;
        List<String> instrucciones;
        
        for(int i=0;i<cantidadArchivos;i++){
            try {
                instrucciones=obtenerIntruccionesArchivo(archivos.get(i));
                              
                crearProceso(instrucciones);
            } catch (IOException ex) {
                erroresLectura.add(archivos.get(i));
            }
        }return erroresLectura;
    }
    */
    public void cargarPrograma(String archivo){
       // int cantidadArchivos=archivo.length();
        List<String> erroresLectura = new ArrayList<>(); // Almacena los archivos donde ocurrió un error;
        List<String> procesos_totales;
       
        try {
            
            procesos_totales=obtenerInstruccionesArchivo(archivo);
            procesos_totales.stream().forEach((proceso) -> {
            String proceso_validar = proceso.replace(" ","");
            if(validar_proceso(proceso_validar)){
                //Se agrega
                System.out.println(proceso_validar);   
            }else{
                System.out.println("No se agrega el proceso");
            //no se agrega
            }

            });
            
        }catch (IOException ex) {
         }
        
    }
    
    public boolean validar_proceso(String proceso){
        String[] obtener_datos;
        obtener_datos = proceso.split(";");
        int cantidad_datos = 0;
        for (String obtener_dato : obtener_datos) {
            cantidad_datos++;
            System.out.println(obtener_dato);
        }
        if(cantidad_datos == 5){
            //Validar que el nombre no se repita
            return true;
        }
        return false;
    }
    /**
     * Crea un proceso para la lista de instrucciones indicadas.
     * Si hay espacio para crear un bloque se agregan a memoria, sino se pone a la espera. 
     * @param instruccionesTemp
     */
    private void crearProceso(List<String> instruccionesTemp){
        String[] obtenerParametros;
        List<String> pila = new ArrayList<>();
        List<String> instrucciones = new ArrayList<>();
        obtenerParametros = instruccionesTemp.get(0).split(" ");
        if(obtenerParametros[0].equals("PARAM")){   
            // Si tiene parámetros, entonces "inicializo" la sección de memoria a usar como pila.
            parametros = obtenerParametros[1].split(",");//Obtengo los parámetros
            pila.addAll(Arrays.asList(parametros));//Los agrego a la pila temporal que se pasará al proceso.
             
            for(int i=0;i<CPU.LARGOPILA;i++){
                    instrucciones.add("PARAM 0");//Se "inicializa" la posición (Las 10 primeras del proceso).
            }
            
        }
        
        instrucciones.addAll(instruccionesTemp);// Se agregan las instrucciones después de la sección de Pila (si existe).
        int numeroInstrucciones=instrucciones.size();
        
        int[] finInicioMemoria=determinarPosicionesMemoria(numeroInstrucciones);
        int idProcesoNuevo=this.idProceso++;
        int estadoProceso;
        int nucleo = (int) (Math.random() * 2); // Se determina el núcleo donde se ejecutará el proceso.
        
        Boolean agregarACola=false; // Me indica si agrego el proceso a una cola.
        if(finInicioMemoria[0]==-1){
            // Si no hay espacio, entonces no se cargan las instrucciones en memoria.
            estadoProceso=BCP.NUEVO;
        }else{
            // Si hay espacio, entonces sí se cargar las instrucciones en memoria.
            cargarInstrucciones(finInicioMemoria[0],finInicioMemoria[1],instrucciones);
            estadoProceso=BCP.PREPARADO;
            agregarACola=true; // Indica que se agregue el proceso a la cola.
        }
        
        BCP proceso=new BCP(estadoProceso, idProcesoNuevo, 0, finInicioMemoria[0], finInicioMemoria[1], nucleo,pila,instrucciones);
        procesos.add(proceso);
        if(agregarACola){
            // Lo agrego aquí para que sea agregado a la cola después de agregar el proceso a la lista de procesos...
            // ...para que no genere conflicto (muy poco probable) con los hilos.
            agregarProcesoCola(nucleo,idProcesoNuevo);
        }
    }
    
    private void cargarInstrucciones(int inicioMemoria, int finMemoria, List<String> instrucciones){
        String[] parteOperacion,parteResto;
        String instruccionEnbits;
        // Utilizo inicio de memoria como mi indice para moverme en la memoria, por eso lo aumento
        for(int i=0; inicioMemoria<=finMemoria; inicioMemoria++,i++){
            parteOperacion = instrucciones.get(i).split(" ");
            instruccionEnbits = parteOperacion[0];          
            if("INC".equals(instruccionEnbits) || "DEC".equals(instruccionEnbits)){                
                 if(parteOperacion.length == 2){
                    CPU.memoriaVirtual[inicioMemoria] = toBinario(instruccionEnbits)+" "+toBinario(parteOperacion[1])+" 00000000";
                 }else{
                    CPU.memoriaVirtual[inicioMemoria] = toBinario(instruccionEnbits)+" "+"0000"+" 00000000";
                 }
            }else{
                parteResto = parteOperacion[1].split(",");
                if(parteResto.length >=2){
                    try{
                        CPU.memoriaVirtual[inicioMemoria] = toBinario(instruccionEnbits)+" "+toBinario(parteResto[0])+" "+decimalABinaro(Integer.parseInt(parteResto[1]));                   
                    }catch(NumberFormatException e){
                        CPU.memoriaVirtual[inicioMemoria] = toBinario(instruccionEnbits)+" "+toBinario(parteResto[0])+" 00000"+toBinario(parteResto[1]);    
                    }

                }else{
                    //Verifico si es jum, je, jne
                    if("JUMP".equals(instruccionEnbits) || "JE".equals(instruccionEnbits) || "JNE".equals(instruccionEnbits)){
                        CPU.memoriaVirtual[inicioMemoria] = toBinario(instruccionEnbits)+ " 0000 " +decimalABinaro(Integer.parseInt(parteResto[0]));
                    }else{
                        CPU.memoriaVirtual[inicioMemoria] = toBinario(instruccionEnbits)+" "+toBinario(parteResto[0])+" 00000000";                  
                    }
                    
                }//SALE ELSE DENTRO              
            }
        }
    }
    
    /**
     * Se encarga de pasar los registros en String a binario para rellenar la memoria
     * retorna el string en binario del string correspondiente.
    **/
    private String toBinario(String registro){      
        switch(registro) {
            case "AX"://AX                    
                 return "0001";
            case "BX"://BX
                return "0010";                                  
            case "CX"://CX
                return "0011";
            case "DX"://DX
               return "0100";                  
            case "LOAD"://LOAD
               return "0001";
            case "STORE"://STORE
               return "0010";
            case "MOV"://MOV
               return "0011";
            case "SUB"://SUB
               return "0100";                
            case "ADD"://ADD
               return "0101"; 
            case "INC"://INC    
                return "0110";
            case "DEC"://DEC
                return "0111";
            case "INT"://INT
                return "1000";
            case "JUMP"://JUM [+/-Desplazamiento]
                return "1001";
            case "CMP"://CMP Val1,Val2
                return "1010";
            case "JE"://JE [ +/-Desplazamiento]
                return "1011";
            case "JNE"://JNE [ +/-Desplazamiento]
                return "1100";
            case "POP"://POP AX
                return "1101";                
            case "20H":
                return "0101";
            case "16H":
                return "0110";
            case "05H":
                return "0111";
            case "PARAM":
                return "1111";
            default:
                return "0000";
        }    
    }
    
    /**
     * Se encarga de pasar los números de decimal a binario.
     * retorna el string del número en binario
     */
    private String decimalABinaro(int a) {
        boolean negativo = false;
        if(a<0){
            a *=-1;
            negativo = true;
        }       
        String temp = Integer.toBinaryString(a);
        
        while(temp.length() !=7){
            temp = "0"+temp;
        }
        if(negativo){temp="1"+temp;}else{temp="0"+temp;}
        return temp;
    }
    
    private void agregarProcesoCola(int nucleo, int idProcesoNuevo){
        Trabajo trabajo;
        if(nucleo==0){
            
                trabajo=new Trabajo(0, idProcesoNuevo);
                colaTrabajoN1.add(trabajo);
            
        }else{           
                trabajo=new Trabajo(1, idProcesoNuevo);
                colaTrabajoN2.add(trabajo);
        }
       
    }
    
    /**
     * Determina si hay espacio suficiente según la memoria indicada (memoriaRequerida). Se utiliza...
     * ...para determinar las posiciones de memoria que tendra el bloque (proceso).
     * Se retorna un arreglo de enteros de dos elementos. La posicion 0 es el inicio de memoria...
     * ... y la posicion 1 es el fin de memoria.
     * Si no hay espacio para el bloque, se retorna -1 en ambas posiciones [-1, -1].
     * @param memoriaRequerida
     * @return int[]
     */
    private int[] determinarPosicionesMemoria(int memoriaRequerida){
        int inicioMemoria = -1, finMemoria = -1;
        Boolean hayEspacio = false;      
        /* 
        * Se recorre toda la memoria en busca de una posición de memoria...
        * en la que no se encuentre ningúnas instrucción, a partir de ahí sigue
        * recorriendo hasta encontrar la memoria requerida por el proceso.
        */
        
        for(int i=0;i<CPU.LARGOMEMORIAVIRTUAL;i++){        
           if(!hayEspacio){
                if(CPU.memoriaVirtual[i].equals("0000 0000 00000000")){
                    int memoriaAcumulada=0;
                     for(int k=i;k<CPU.LARGOMEMORIAVIRTUAL;k++){
                         
                         if(CPU.memoriaVirtual[k].equals("0000 0000 00000000")){
                             memoriaAcumulada++;
                             if(memoriaAcumulada == memoriaRequerida){
                                 inicioMemoria = i;
                                 finMemoria = k;
                                 hayEspacio = true;
                                 break;
                             }

                         }else{
                             break;
                         }
                     }//SALE FOR
                }//SALE IF
           }//SALE IF
           else break;
           
        }      
        if(hayEspacio){
            // Si hay espacio, retorno las posiciones encontradas.
            return new int[]{inicioMemoria, finMemoria};
        }else{            
                return new int[]{ -1,-1};           
            }
    }
    
    /**
     * Obtiene las instrucciones (líneas) del archivo indicado. Las restorna en una lista de string.
     * @param archivo
     * @return List<String>
     * @throws FileNotFoundException
     * @throws IOException 
     */
    private List<String> obtenerInstruccionesArchivo(String archivo) throws FileNotFoundException, IOException{
        String cadena;
        List<String> instrucciones = new ArrayList<>();
        
        try (FileReader f = new FileReader(archivo)) {
            BufferedReader b = new BufferedReader(f);
            while((cadena = b.readLine())!=null) {
                instrucciones.add(cadena);
            }
        }
        
        return instrucciones;
    }
}
