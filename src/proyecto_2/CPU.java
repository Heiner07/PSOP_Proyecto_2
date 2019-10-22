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
    static int ALGORITMO_CPU = 0;
    static int ALGORITMO_MEMORIA = 0;
    static int PROCESOSPORNUCLEO = 6;
    static int QUANTUM = 1;
    static int PARTICIONFIJA = 64;
    static int TAMANIO_FRAME = 64;
    static Boolean EN_EJECUCION = false;
    static String[] memoriaVirtual = new String[LARGOMEMORIAVIRTUAL];
    static String[] memoria = new String[LARGOMEMORIA];
    static String[] disco = new String[LARGODISCO];
    static List<Bloque> Bloques; // Se utilizan para bloques fijos o dinámicos
    private Nucleo nucleo1, nucleo2;
    private List<Trabajo> colaTrabajoN1, colaTrabajoN2;
    private List<Proceso> procesos;
    private Boolean controlColor = true; // Se utiliza para asignar los colores a los bloques.
    private int procesoCola1,procesoCola2;
    private int[] segmentos;
    
    static List<Trabajo> colaImprimir1,colaImprimir2;
    
    /* Hilos de Control */
    private Timer timerControlColasNucleos, timerControlMemoriaVirtual, timerControlProcesos, timerControlEjecucion;
    
    public CPU(){
        this.nucleo1 = new Nucleo(0);
        this.nucleo2 = new Nucleo(1);
        this.colaTrabajoN1 = new ArrayList<>();
        this.colaTrabajoN2 = new ArrayList<>();
        CPU.colaImprimir1 = new ArrayList<>();
        CPU.colaImprimir2 = new ArrayList<>();
        this.procesos = new ArrayList<>();
        CPU.Bloques = new ArrayList<>();
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
            CPU.memoria[i] = "Libre";
        }
    }
    
    private void inicializarDisco(){
        for(int i=0;i<CPU.LARGODISCO;i++){
            CPU.disco[i] = "Libre";
        }
    }
    
    private void inicializarMemoriaVirtual(){
        for(int i=0;i<CPU.LARGOMEMORIAVIRTUAL;i++){
            CPU.memoriaVirtual[i] = "Libre";
        }
    }
    
    /**
     * Se encarga de llamar a las distintas funciones que controlan la ejecucion de la instrucciones.
     */
    private void configurarHilos(){
        configurarHiloColasNucleos();
        configurarHiloMemoriaVirtual();
        configurarHiloProcesos();
        configurarHiloEjecucion();
    }
    
    /**
     * Establece la funcion para el timer timerControlEjecucion que se encargará de establecer...
     * ...si se termino o no la ejecución
     */
    private void configurarHiloEjecucion(){
        timerControlEjecucion = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                // Función que repetirá según el intervalo asignado (1 segundo).
                controlEjecucion();
            }
        });
        // Inicializo el timer.
        timerControlEjecucion.start();
    }
    
    /**
     * Establece la variable EN_EJECUCION en false cuando ambos núcleos terminen.
     * Función llamada por el timerControlEjecucion.
     */
    private void controlEjecucion(){
        if(!nucleo1.obtenerEstado() && !nucleo2.obtenerEstado()){
            CPU.EN_EJECUCION = false;
        }
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
               Proceso procesoAEjecutar = retornarProceso(1);
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
               Proceso procesoAEjecutar = retornarProceso(2);
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
        
    }
   
    /**
     * Se encarga de retornar el BCP del proceso que se va a ejecutar en el núcleo
     * Con respecto a numeroCola que ingresa ya sea 1 o 2
     * @param numeroCola
     * @return  un BCP*/
    private Proceso retornarProceso(int numeroCola){
        if(numeroCola == 1){
            int largoCola = colaTrabajoN1.size()-1;
            
            if(procesoCola1 > largoCola){
               procesoCola1 = 0;          
            }
            int proc;
            for(;procesoCola1<=largoCola;procesoCola1++){
                proc = colaTrabajoN1.get(procesoCola1).numeroBCP;
                Proceso procesoAEjecutar = obtenerBCP(proc);
                /*if(procesoAEjecutar.obtenerPC() <= procesoAEjecutar.obtenerFinMemoria() && procesoAEjecutar.obtenerEstadoProceso()!=BCP.TERMINADO){
                    return procesoAEjecutar;                   
                }*/                            
            }           
        }else{
            int largoCola = colaTrabajoN2.size()-1;           
            if(procesoCola2 > largoCola){
               procesoCola2 = 0;          
            }                   
            int proc;
            for(;procesoCola2<=largoCola;procesoCola2++){
                proc = colaTrabajoN2.get(procesoCola2).numeroBCP;
                Proceso procesoAEjecutar = obtenerBCP(proc);
                /*if(procesoAEjecutar.obtenerPC() <= procesoAEjecutar.obtenerFinMemoria() && procesoAEjecutar.obtenerEstadoProceso()!=BCP.TERMINADO){
                    return procesoAEjecutar;                  
                }*/                           
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
                Proceso procesoAEjecutar = obtenerBCP(proc);
                /*if(procesoAEjecutar.obtenerPC() <= procesoAEjecutar.obtenerFinMemoria() && procesoAEjecutar.obtenerEstadoProceso()!=BCP.TERMINADO){
                    trabajo=new Trabajo(0, proc,memoriaVirtual[procesoAEjecutar.obtenerPC()]);
                    colaImprimir1.add(trabajo);                   
                } */                            
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
                Proceso procesoAEjecutar = obtenerBCP(proc);          
                /*if(procesoAEjecutar.obtenerPC() <= procesoAEjecutar.obtenerFinMemoria() && procesoAEjecutar.obtenerEstadoProceso()!=BCP.TERMINADO){
                    trabajo=new Trabajo(1, proc,memoriaVirtual[procesoAEjecutar.obtenerPC()]);
                    colaImprimir2.add(trabajo);                   
                } */                             
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
    
    public List<Proceso> obtenerProcesos(){
        return procesos;
    }
    
    public void asignarQuantum(int quantum){
        QUANTUM = quantum;
    }
    
    public void asignarParticionFija(int particion){
        PARTICIONFIJA = particion;
    }
    
    public void asignarSegmentos(int[] segmentos){
        this.segmentos = segmentos;
    }
    
    public void asignarFrame(int frame){
        CPU.TAMANIO_FRAME = frame;
    }
    
    private Proceso obtenerBCP(int numeroBCP){
        int numeroProcesos = procesos.size();
        Proceso proceso;
        for(int i=0;i<numeroProcesos;i++){
            proceso=procesos.get(i);
            if(proceso.obtenerNumeroProceso()==numeroBCP){
                return proceso;
            }
        }return null;
    }
    
    /**
     * Se encarga de cargar los procesos del archivo.
     * Valida que los datos sean correctos y si lo son, llama a crear el proceso...
     * ... y se agrega al sistema.
     * @param archivo
     * @return 
     */
    public List<String> cargarPrograma(String archivo){
        List<String> erroresLectura = new ArrayList<>(); // Almacena los errores que ocurren (para la interfaz);
        List<String> procesos_totales;
        int linea = 1;
        
        // Se inicializa la memoria
        ejecutarAlgoritmoMemoria();
       
        try {
            /* Se obtienen los procesos del archivo */
            procesos_totales=obtenerProcesosArchivo(archivo);
            for(String proceso: procesos_totales){
                String proceso_validar = proceso.replace(" ",""); // Se limpia el string
                String[] datos = proceso_validar.split(";"); // Se dividen los datos
                if(validar_proceso(datos)){
                    // Una vez se comprueba que los datos son correctos se crea el proceso y se agrega al núcleo respectivo
                    crearProceso(datos);
                }else{
                    // Si no es coreecto se registra el error.
                    erroresLectura.add("Error en la línea "+linea);
                }linea++;
            }
        }catch (IOException ex) {
            erroresLectura.add("Error al leer el archivo.");
        }return erroresLectura;
    }
    
    /**
     * Se encarga de crear el proceso extraído del arcgivo y lo agrega a un núcleo
     * @param datos 
     */
    private void crearProceso(String[] datos){
        Proceso procesoNuevo;
        /* Se establecen los datos del proceso */
        int nucleo = (int) (Math.random() * 2);// Se determina el núcleo donde se ejecutará el proceso.
        String nombre = datos[0];
        int estado = Proceso.PREPARADO;
        int numeroProceso;
        int rafaga = Integer.valueOf(datos[1]);
        int tiempoLlegada = Integer.valueOf(datos[2]);
        int prioridad = Integer.valueOf(datos[3]);
        int tamanio = Integer.valueOf(datos[4]);
        /* Se agrega al núcleo correspondiante */
        if(nucleo == 0){
            numeroProceso = nucleo1.obtenerNumeroProcesoSiguiente();
            procesoNuevo = new Proceso(nombre, estado, numeroProceso, rafaga, tiempoLlegada, prioridad, tamanio, 0);
            asignarMemoriaProceso(procesoNuevo);
            
            /* Si supera el límite de procesos por núcleo, se pone en espera */
            if(numeroProceso > CPU.PROCESOSPORNUCLEO){
                procesoNuevo.establecerEstado(Proceso.NUEVO);
            }else if(procesoNuevo.obtenerEstadoProceso() != Proceso.NUEVO){
                cargarProcesoMemoria(procesoNuevo);
                nucleo1.agregarProceso(procesoNuevo);
            }
        }else{
            numeroProceso = nucleo2.obtenerNumeroProcesoSiguiente();
            procesoNuevo = new Proceso(nombre, estado, numeroProceso, rafaga, tiempoLlegada, prioridad, tamanio, 1);
            asignarMemoriaProceso(procesoNuevo);
            
            /* Si supera el límite de procesos por núcleo, se pone en espera */
            if(numeroProceso > CPU.PROCESOSPORNUCLEO){
                procesoNuevo.establecerEstado(Proceso.NUEVO);
            }else if(procesoNuevo.obtenerEstadoProceso() != Proceso.NUEVO){
                cargarProcesoMemoria(procesoNuevo);
                nucleo2.agregarProceso(procesoNuevo);
            }
        }
        procesos.add(procesoNuevo);
    }
    
    private Boolean esNumero(String numeroTemp){
        try{
            int numero = Integer.valueOf(numeroTemp);
            return numero >= 0;
        }catch(NumberFormatException e){
            return false;
        }
    }
    
    private Boolean nombreProcesoRepetido(String nombreProceso){
        int numeroProcesos = procesos.size();
        for(int i=0; i < numeroProcesos; i++){
            if(procesos.get(i).obtenerNombre().equals(nombreProceso)){
                return true;
            }
        }return false;
    }
    
    /**
     * Verifica que los datos sean completos y tengan el formato correcto (que sean números)
     * @param datos
     * @return 
     */
    private Boolean validar_proceso(String[] datos){
        int cantidad_datos = 0;
        for (String dato : datos) {
            if(cantidad_datos == 0){
                if(dato.equals("")){
                    return false;
                }else{
                    if(nombreProcesoRepetido(dato)){
                        return false;
                    }
                }
            }else{
                if(!esNumero(dato)){
                    return false;
                }
            }
            cantidad_datos++;
        }
        return cantidad_datos == 5;
    }
    
    /**
     * Obtiene los procesos (líneas) del archivo indicado. Las restorna en una lista de string.
     * @param archivo
     * @return List<String>
     * @throws FileNotFoundException
     * @throws IOException 
     */
    private List<String> obtenerProcesosArchivo(String archivo) throws FileNotFoundException, IOException{
        String cadena;
        List<String> procesosArchivo = new ArrayList<>();
        
        try (FileReader f = new FileReader(archivo)) {
            BufferedReader b = new BufferedReader(f);
            while((cadena = b.readLine())!=null) {
                procesosArchivo.add(cadena);
            }
        }
        
        return procesosArchivo;
    }
    
    /**
     * Empieza la ejecución de los algoritmos en los núcleos
     */
    public void empezarEjecucion(){
        nucleo1.asignarQuantum(QUANTUM);
        nucleo2.asignarQuantum(QUANTUM);
        nucleo1.empezarEjecucion();
        
        nucleo2.empezarEjecucion();
        
        CPU.EN_EJECUCION = true;
    }
    
    /**
     * Limpia los posibles valores de una ejecución anterior en los núcleos
     */
    public void limpiarProcesos(){
        procesos.clear();
        CPU.Bloques.clear();
        nucleo1.limpiarProcesos();
        nucleo2.limpiarProcesos();
        controlColor = true;
    }
    
    private void ejecutarAlgoritmoMemoria(){
        switch (ALGORITMO_MEMORIA) {
            case 0:
                crearBloquesFijos();
                break;
            case 2:
                crearFrames();
                break;
            case 3:
                crearSegmentos();
                break;
            default:
                break;
        }
    }
    
    private void asignarMemoriaProceso(Proceso proceso){
        switch (ALGORITMO_MEMORIA) {
            case 0:
                proceso.establecerLimitesMemoria(obtenerLimitesMemoriaFija(proceso.obtenerTamanio()));
                break;
            case 1:
                proceso.establecerLimitesMemoria(crearBloqueDinamico(proceso.obtenerTamanio()));
                break;
            case 2:
                proceso.establecerLimitesMemoria(crearBloqueDinamico(proceso.obtenerTamanio()));
                //proceso.establecerFrames()
                break;
            case 3:
                proceso.establecerLimitesMemoria(obtenerLimitesMemoriaSegmento(proceso.obtenerTamanio()));
                break;
            default:
                break;
        }
    }
    
    private void crearFrames(){
        int tamanio = 0;
        int numeroFrame = 0;
        int nuevoTamanioMemoria = -1;
        Bloque bloque;
        for(int i = 0; i < CPU.LARGOMEMORIAVIRTUAL; i++){
            if(nuevoTamanioMemoria == -1 && i > CPU.LARGOMEMORIA){
                nuevoTamanioMemoria = numeroFrame;
            }
            if(tamanio == CPU.TAMANIO_FRAME){
                bloque = new Bloque(numeroFrame, numeroFrame, 0, Boolean.FALSE, controlColor? 3 : 4);
                Bloques.add(bloque);
                tamanio = 0;
                numeroFrame++;
                controlColor = !controlColor;
            }
            tamanio++;
        }
        if(tamanio > 1){
            bloque = new Bloque(numeroFrame, numeroFrame, 0, Boolean.FALSE, controlColor? 3 : 4);
            Bloques.add(bloque);
        }
        CPU.LARGOMEMORIA = nuevoTamanioMemoria;
        CPU.LARGOMEMORIAVIRTUAL = nuevoTamanioMemoria + Bloques.size() - nuevoTamanioMemoria;
        CPU.LARGODISCO -= CPU.LARGOMEMORIAVIRTUAL - CPU.LARGOMEMORIA;
        System.out.println("Tamaño M: "+nuevoTamanioMemoria);
        System.out.println("Tamaño MV: "+CPU.LARGOMEMORIAVIRTUAL);
        System.out.println("Tamaño F: "+CPU.TAMANIO_FRAME);
    }
    
    private void crearSegmentos(){
        int inicio = 0;
        int cantidadSegmentos = segmentos.length;
        Bloque bloque;
        for(int i = 0; i < cantidadSegmentos; i++){
            bloque = new Bloque(inicio, inicio + segmentos[i]-1, 0, Boolean.FALSE, controlColor? 3 : 4);
            Bloques.add(bloque);
            inicio += segmentos[i];
            controlColor = !controlColor;
        }
    }
    
    private void crearBloquesFijos(){
        int tamanio = 0;
        int inicio = 0;
        Bloque bloque;
        for(int i = 0; i < CPU.LARGOMEMORIAVIRTUAL; i++){
            if(tamanio == PARTICIONFIJA){
                bloque = new Bloque(inicio, inicio + PARTICIONFIJA-1, 0, Boolean.FALSE, controlColor? 3 : 4);
                Bloques.add(bloque);
                tamanio = 0;
                inicio += PARTICIONFIJA;
                controlColor = !controlColor;
            }
            tamanio++;
        }
        if(tamanio > 1){
            bloque = new Bloque(inicio, inicio + tamanio-1, 0, Boolean.FALSE, controlColor? 3 : 4);
            Bloques.add(bloque);
        }
    }
    
    private int[] crearBloqueDinamico(int tamanio){
        int cantidadBloquesFijos = Bloques.size();
        Bloque bloque;
        int finBloqueAnterior = 0;
        for(int i = 0; i < cantidadBloquesFijos; i++){
            bloque = Bloques.get(i);
            finBloqueAnterior = bloque.obtenerInicioFin()[1]+1;// Se suma uno para que no toque memoria de este bloque
        }
        if(LARGOMEMORIAVIRTUAL - finBloqueAnterior >= tamanio){
            bloque = new Bloque(finBloqueAnterior, finBloqueAnterior + tamanio-1, tamanio, Boolean.TRUE, controlColor? 3 : 4);
            Bloques.add(bloque);
            controlColor = !controlColor;
            return new int[]{ finBloqueAnterior, finBloqueAnterior + tamanio -1 };
        }else{
            return new int[]{ -1, -1 };
        }
    }
    
    private int[] obtenerLimitesMemoriaSegmento(int tamanio){
        int cantidadBloques = Bloques.size();
        Bloque bloque, bloqueLibre = null, bloqueOptimo = null;
        for(int i = 0; i < cantidadBloques; i++){
            bloque = Bloques.get(i);
            if(!bloque.estaOcupado()){
                if(bloqueLibre == null){
                    bloqueLibre = bloque;
                }
                if(bloque.obtenerEspacioBloque() == tamanio){
                    bloque.asignarEspacioUsado(tamanio);
                    return bloque.obtenerLimitesUsados();
                }
                else if(bloque.obtenerEspacioBloque() > tamanio){
                    if(bloqueOptimo == null){
                        bloqueOptimo = bloque;
                    }else{
                        if(bloque.obtenerEspacioBloque() - tamanio < bloqueOptimo.obtenerEspacioBloque() - tamanio){
                            bloqueOptimo = bloque;
                        }
                    }
                }
            }
        }
        if(bloqueOptimo != null){
            bloqueOptimo.asignarEspacioUsado(tamanio);
            return bloqueOptimo.obtenerLimitesUsados();
        }else if(bloqueLibre != null){
            bloqueLibre.asignarEspacioUsado(tamanio);
            return bloqueLibre.obtenerLimitesUsados();
        }
        return new int[]{ -1, -1};
    }
    
    private int[] obtenerLimitesMemoriaFija(int tamanio){
        int cantidadBloquesFijos = Bloques.size();
        Bloque bloque;
        for(int i = 0; i < cantidadBloquesFijos; i++){
            bloque = Bloques.get(i);
            if(!bloque.estaOcupado()){
                bloque.asignarEspacioUsado(tamanio);
                return bloque.obtenerLimitesUsados();
            }
        }return new int[]{ -1, -1};
    }
    
    private void cargarProcesoMemoria(Proceso proceso){
        int inicio = proceso.obtenerInicioMemoria();
        int fin = proceso.obtenerFinMemoria();
        int faltante = proceso.espacioFaltante();
        for(int i = inicio; i <= fin; i++){
            CPU.memoriaVirtual[i] = proceso.obtenerNombre();
        }
        if(faltante > 0){
            CPU.memoriaVirtual[fin] += "+" + faltante;
        }
    }
    
    public static int obtenerColorBloque(int posicion){
        Bloque bloque;
        int cantidadBloques = Bloques.size();
        int[] limitesBloque;
        for(int i = 0; i < cantidadBloques; i++){
            bloque = Bloques.get(i);
            limitesBloque = bloque.obtenerInicioFin();
            if(limitesBloque[0] <= posicion && posicion <= limitesBloque[1]){
                return bloque.obtenerIndiceColor();
            }
        }return -1;
    }
}
