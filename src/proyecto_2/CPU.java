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
import javafx.util.Pair;
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
    static int LIMITE_TIEMPO_EJECUCION = 80;
    static Boolean EN_EJECUCION = false;
    static String[] memoriaVirtual = new String[LARGOMEMORIAVIRTUAL];
    static String[] memoria = new String[LARGOMEMORIA];
    static String[] disco = new String[LARGODISCO];
    static List<Bloque> Bloques; // Se utilizan para bloques fijos o dinámicos
    private Nucleo nucleo1, nucleo2;
    private List<Proceso> procesos;
    private Boolean controlColor = true; // Se utiliza para asignar los colores a los bloques.
    private int[] segmentos;
    
    /* Hilos de Control */
    private Timer timerControlMemoriaVirtual, timerControlEjecucion;
    
    public CPU(){
        this.nucleo1 = new Nucleo(0);
        this.nucleo2 = new Nucleo(1);
        this.procesos = new ArrayList<>();
        CPU.Bloques = new ArrayList<>();
        inicializaMemoria();
        inicializarDisco();
        inicializarMemoriaVirtual();
        configurarHilos();
    }
    
    /**
     * Ajusta los valores de memoria y disco (y virtual), con lo indicado por el usuario.
     * @param lMemoria
     * @param lDisco 
     */
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
        configurarHiloMemoriaVirtual();
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
     * ...los valores de la memoria virtual en la memoria o en el disco.
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
    
    public Nucleo obtenerNucleo1(){
        return nucleo1;
    }
    
    public Nucleo obtenerNucleo2(){
        return nucleo2;
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
        
        // Se inicializa la memoria según el algoritmo indicado
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
                }else if(cantidad_datos == 4){
                    if(Integer.valueOf(dato) < 1){
                        return false;
                    }
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
    
    /**
     * Hace el llamado al algoritmo correspondiente de memoria. No está el...
     * ...dinámica, ya que se crean las particiones en el momento de cargar los procesos
     */
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
    
    /**
     * Asigna la memoria para el proceso indicado, según el algoritmo elegido
     * @param proceso 
     */
    private void asignarMemoriaProceso(Proceso proceso){
        switch (ALGORITMO_MEMORIA) {
            case 0:// Memoria Fija
                proceso.establecerLimitesMemoria(obtenerLimitesMemoriaFija(proceso.obtenerTamanio()));
                break;
            case 1:// Memoria Dinámica
                proceso.establecerLimitesMemoria(crearBloqueDinamico(proceso.obtenerTamanio()));
                break;
            case 2:// Memoria Paginación
                proceso.establecerFrames(obtenerFramesParaProceso(proceso.obtenerTamanio()));
                break;
            case 3:// Memoria Segmentación
                proceso.establecerLimitesMemoria(obtenerLimitesMemoriaSegmento(proceso.obtenerTamanio()));
                break;
            default:
                break;
        }
    }
    
    /**
     * Crea los frames en la memoria, que serán usados por los procesos.
     * Además, ajusta los valores de memoria, disco y virual, producto de la...
     * ...generación de los frames.
     */
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
        CPU.LARGODISCO = CPU.LARGODISCO / 2 + CPU.LARGOMEMORIAVIRTUAL - CPU.LARGOMEMORIA;
    }
    
    /**
     * Crea los segmentos en la memoria indicados por el usuario.
     */
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
    
    /**
     * Crea los bloques fijos en la memoria del tamaño indicado.
     */
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
    
    /**
     * Crea un bloque del tamaño indicado
     * @param tamanio
     * @return 
     */
    private int[] crearBloqueDinamico(int tamanio){
        int cantidadBloques = Bloques.size();
        Bloque bloque;
        int finBloqueAnterior = 0;
        for(int i = 0; i < cantidadBloques; i++){
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
    
    /**
     * Obtiene los frames para un proceso de acuerdo al tamaño necesitado.
     * @param tamanio
     * @return 
     */
    private List<Pair<Integer, Integer>> obtenerFramesParaProceso(int tamanio){
        List<Pair<Integer, Integer>> frames = new ArrayList<>();
        int cantidadFrames = Bloques.size();
        Bloque bloque;
        for(int i = 0; i < cantidadFrames; i++){
            bloque = Bloques.get(i);
            if(!bloque.estaOcupado()){
                if(tamanio > 0){
                    if(tamanio >= CPU.TAMANIO_FRAME){
                        bloque.asignarEspacioUsado(CPU.TAMANIO_FRAME);
                        tamanio -= CPU.TAMANIO_FRAME;
                        frames.add(new Pair<>(i, CPU.TAMANIO_FRAME));
                    }else{
                        bloque.asignarEspacioUsado(tamanio);
                        frames.add(new Pair<>(i, tamanio));
                        tamanio = 0;
                        break;
                    }
                }else{
                    break;
                }
            }
        }
        if(tamanio > 0){
            /* Si no se pudo cargar todo, entonces libero los frames utilizados */
            int cantidadFramesLiberar = frames.size();
            Pair<Integer, Integer> frame;
            for(int i = 0; i < cantidadFramesLiberar; i++){
                frame = frames.get(i);
                Bloques.get(frame.getKey()).asignarEspacioUsado(0);
            }
        }
        return frames;
    }
    
    /**
     * Obtiene los limites de un segmento de acuerdo al tamaño indicado.
     * Trata de encontrar el segmento más optimo para el tamaño indicado, si no..
     * hay, obtiene el primero disponible.
     * @param tamanio
     * @return 
     */
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
        }
        return new int[]{ -1, -1};
    }
    
    /**
     * Obtiene los límites de un bloque fijo para el tamaño del proceso indicado
     * @param tamanio
     * @return 
     */
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
    
    /**
     * Se encarga de cargar el proceso en la memoria en las posiciones indicada...
     * por este.
     * @param proceso 
     */
    private void cargarProcesoMemoria(Proceso proceso){
        if(CPU.ALGORITMO_MEMORIA == 2){
            List<Pair<Integer, Integer>> frames = proceso.obtenerFrames();
            Pair<Integer, Integer> frame;
            int cantidadFrames = frames.size();
            for(int i = 0; i < cantidadFrames; i++){
                frame = frames.get(i);
                CPU.memoriaVirtual[frame.getKey()] = "(" + frame.getValue() + "/" +
                        CPU.TAMANIO_FRAME + ")" + proceso.obtenerNombre();
            }
        }else{
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
    }
    
    /**
     * Función llamada por EditorCeldasDisco y EditorCeldasMemoria, para pintar...
     * ...del color correspondiente la celda.
     * @param posicion
     * @return 
     */
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
