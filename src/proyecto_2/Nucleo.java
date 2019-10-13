/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 *
 * @author home
 */
public class Nucleo {
    
    //[0] = AC
    //[1] = AX
    //[2] = BX
    //[3] = CX
    //[4] = DX
    private int[] registros = {0,0,0,0,0};
    private int PC=0, IR=0;
    private String instruccionIR="";
    private BCP procesoEjecutando=null;
    static int numeroInstrucciones=0;
    private int numeroNucleo;
    private Boolean bandera= false;
    private Boolean ejecutar = false;
    private Boolean esperaInterrupcion = false; // Indica si el núcleo está a la espera que se complete una interrupción.
    private Timer timerOperacion;
    private int tiempoRestante=1; // Variable que indicara cuantos segendos debe esperar hasta recibir otra instrucción
    private int inicioMemoria;
    private String instrucciones;
    private boolean banderaInterrupcion=false;
    public Nucleo(int numeroNucleo){
        this.numeroNucleo = numeroNucleo;
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
     * Función llamada por el timerOperación que controla la ejecución de las operaciones en el núcleo.
     * Si el tiempo es cero entonces verifica que hay una instrucción a ejecutar y la realiza, sino resta el tiempo...
     * ...requerido de la operación anterior.
     */
    private void controlEjecucionNucleo(){
        try {
            if(tiempoRestante==0){
                if(ejecutar && !esperaInterrupcion){
                    ejecutar=false;
                    Operaciones();
                }
            }else{
                tiempoRestante--;
            }
        } catch (InterruptedException ex) {
            // Modificar para mostrar mensaje correspondiente
            Logger.getLogger(Nucleo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Se encarga de realizar las operaciones correspondientes a la instrucción que entre
     *
     */
    private void Operaciones() throws InterruptedException{
        // Asigno el IR
        IR=PC;
        instruccionIR=CPU.memoriaVirtual[IR];
        procesoEjecutando.establecerCadenaInstruccionIR(instruccionIR);
        // Traigo la instrucción de memoria y aumento el PC
        instrucciones = CPU.memoriaVirtual[PC++];      
        String[] parts;
        parts = instrucciones.split(" ");
        String operacion = parts[0];
        String registro = parts[1];
        String numeroORegistro = parts[2];    
        switch(operacion) {
            case "0001"://LOAD
                tiempoRestante=TiempoInstrucciones.LOAD;
                registros[0] = registros[registroPosicion(registro)];
                
                break;    
                
            case "0010"://STORE
                tiempoRestante=TiempoInstrucciones.STORE;
                registros[registroPosicion(registro)] = registros[0];
                
                break;
                
            case "0011"://MOV
               tiempoRestante=TiempoInstrucciones.MOV;
               movimiento(registro,numeroORegistro);
               
               break; 
                
            case "0100"://SUB
                tiempoRestante=TiempoInstrucciones.SUB;
                restar(registro,numeroORegistro);
                
                break;
                
            case "0101"://ADD
                tiempoRestante=TiempoInstrucciones.ADD;
                sumar(registro, numeroORegistro);
                
                break;
            case "0110"://INC  
                tiempoRestante=TiempoInstrucciones.INC;
                incrementar(registro);
                
                break;
            case "0111"://DEC
                tiempoRestante=TiempoInstrucciones.DEC;
                decrementar(registro);
                
                break;
            case "1000"://INT
                
                break;
            case "1001"://JUMP [+/-Desplazamiento]
                tiempoRestante=TiempoInstrucciones.JUMP;
                int decimal = Integer.parseInt(numeroORegistro.substring(1, 8),2);
                if("1".equals(numeroORegistro.substring(0,1))){
                    decimal *= -1;
                    PC -=2;
                }           
                
                PC += decimal;
                
                break;
            case "1010"://CMP Val1,Val2
                tiempoRestante=TiempoInstrucciones.CMP;
                compararValores(registro,numeroORegistro);
                
                break;
            case "1011"://JE [ +/-Desplazamiento]
                tiempoRestante=TiempoInstrucciones.JEJNE;
                if(bandera){
                    int decimal2 = Integer.parseInt(numeroORegistro.substring(1, 8),2);
                    if("1".equals(numeroORegistro.substring(0,1))){
                        decimal2 *= -1;    
                        PC-=2;
                    }       
                    
                    PC += decimal2;                   
                }
                
                break;
            case "1100"://JNE [ +/-Desplazamiento]
                tiempoRestante=TiempoInstrucciones.JEJNE;
                if(!bandera){
                    int decimal3 = Integer.parseInt(numeroORegistro.substring(1, 8),2);
                    if("1".equals(numeroORegistro.substring(0,1))){
                        decimal3 *= -1;    
                        PC-=2;

                    }    
                    
                    PC += decimal3;                   
                }
                
                break;
            case "1101"://POP AX
                tiempoRestante=TiempoInstrucciones.POP;
                popRegistro(registro);
                
                break;
            case "1111"://PARAM
                tiempoRestante=TiempoInstrucciones.PARAM;
                parametrosAPila();
                break;
            default:             
                break;
        }
        if(!esperaInterrupcion){
            // Si el núcleo espera una instrucción, entonces no es necesario guardar el contexto porque no hay cambios.
            guardarContexto(); // Guarda el estado en el proceso. (Para que sea reflejado en la interfaz).
        }
    }
    
    private static int registroPosicion(String registro){       
        switch(registro) {
            case "0001"://AX                    
                 return 1;   
            case "0010"://BX
                return 2;                                  
            case "0011"://CX
                return 3;
            case "0100"://DX
               return 4;
            default:
                return 0;  
        }
    }
    
    
    
    private void movimiento(String registro, String numero){
        int numeroDecimal;
        if(numero.length() == 8){
            numeroDecimal = Integer.parseInt(numero.substring(1, 8),2);
        }else{
            //Es un registro entonces accedo a la dirección del registro para obtener el número
            numeroDecimal = registros[registroPosicion(numero.substring(5, 9))];       
        }
        if("1".equals(numero.substring(0,1))){
            numeroDecimal *= -1;           
        }
        registros[registroPosicion(registro)] = numeroDecimal;
        
    }
    
    private void sumar(String registro, String numero){     
        int numeroDecimal;
        if(numero.length() == 8){
            numeroDecimal = Integer.parseInt(numero.substring(1, 8),2);
        }else{
            //Es un registro entonces accedo a la dirección del registro para obtener el número
            numeroDecimal = registros[registroPosicion(numero.substring(5, 9))];       
        }
        if(numero.equals("00000000")){
            registros[0] += registros[registroPosicion(registro)];
        }else{            
            if("1".equals(numero.substring(0,1))){
                numeroDecimal *= -1;           
            }
            registros[registroPosicion(registro)] += numeroDecimal;
        }       
    }
    
    
    private void restar(String registro, String numero){
        int numeroDecimal;
        if(numero.length() == 8){
            numeroDecimal = Integer.parseInt(numero.substring(1, 8),2);
        }else{
            //Es un registro entonces accedo a la dirección del registro para obtener el número
            numeroDecimal = registros[registroPosicion(numero.substring(5, 9))];       
        }
        if(numero.equals("00000000")){
            registros[0] -= registros[registroPosicion(registro)];
        }else{            
            if("1".equals(numero.substring(0,1))){
                numeroDecimal *= -1;           
            }
            registros[registroPosicion(registro)] -= numeroDecimal;
        }
    }
    
    private void incrementar(String registro){
        if(registro.equals("0000")){
            registros[0] += 1;
        }else{
            registros[registroPosicion(registro)] += 1;
            
        }       
    }
    
    private void decrementar(String registro){
        if(registro.equals("0000")){
            registros[0] -= 1;
        }else{
            registros[registroPosicion(registro)] -= 1;
            
        }       
    }
    
    private void compararValores(String val1,String val2){
        int numeroDecimal1 = registros[(registroPosicion(val1))];      
        int numeroDecimal2 = registros[registroPosicion(val2.substring(5, 9))];
        bandera = numeroDecimal1 == numeroDecimal2;
    }
    
    private void popRegistro(String registro){
        if(procesoEjecutando.obtenerDireccionpila()>=inicioMemoria){
            registros[(registroPosicion(registro))] = binarioADecimal(CPU.memoriaVirtual[procesoEjecutando.popPila()].split(" ")[2]);
        }else{
            
        }
    }
    
    private void parametrosAPila(){
        // Obtengo los parámetros del proceso
        List<String> parametrosTemp = procesoEjecutando.obtenerParametros();
        int numeroParametros = parametrosTemp.size();
        if(numeroParametros<11){// Si es menor a 11, entonces caben en la pila.
            for(int i=0; i<numeroParametros;i++){// Agrego los parámetros a la sección de la memoria usada como pila
                CPU.memoriaVirtual[procesoEjecutando.pushPila()]="1111 0000 "+
                        decimalABinaro(Integer.valueOf(parametrosTemp.get(i)));
            }
        }else{// Si no indico el error
            
        }
    }
    
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
    
    private int binarioADecimal(String binario){
        int numeroDecimal=0;
        int largoBinario = binario.length();
        int potencia = largoBinario-1;
        for(int i=0;i<largoBinario;i++){
            if(binario.charAt(i)=='1'){
                numeroDecimal += 1 * Math.pow(2, potencia);
            }potencia--;
        }
        return numeroDecimal;
    }
    
    public int obtenerPC(){
        return PC;
    }
    
    public int obtenerIR(){
        return IR;
    }
    
    public String obtenerCadenaInstruccionIR(){
        return instruccionIR;
    }
    
    public int[] obtenerRegistros(){
        return registros;
    }
    
    public Boolean obtenerEstado(){
        // Si es igual a cero(tiempo de espera) y no espera interrupción, entonces está listo.
        return (tiempoRestante==0) && !esperaInterrupcion;
    }
    
    /**
     * Recibe el proceso de la cola de trabajo. Y lo pone a ejecutar.
     * @param proceso
     * @throws InterruptedException 
     */
    public void recibirProceso(BCP proceso) throws InterruptedException{
        if(procesoEjecutando==null){
            // Si no hay proceso asignado, entonces solo se establece el entrante.
            establecerContexto(proceso);
            ejecutar=true;
        }else if(procesoEjecutando.obtenerNumeroProceso()==proceso.obtenerNumeroProceso()){
            procesoEjecutando.establecerEstado(BCP.EN_EJECUCION);
            // Si es el mismo proceso, solo indico que ejecute la siguiente instrucción
            ejecutar=true;
        }else{
            // Si es otro proceso diferente, entonces ejecuto un cambio de contexto.
            cambioContexto(proceso);
            ejecutar=true;
        }
    }
    
    /**
     * Guarda el contexto que tiene el núcleo en el procesos que está ejecutando.
     * Esta función se llama para el cambio de contexto y cada vez que termina Operaciones (Para reflejar los cambios en la interfaz)
     */
    private void guardarContexto(){
        if((PC>procesoEjecutando.obtenerFinMemoria() && procesoEjecutando.obtenerEstadoProceso()!=BCP.TERMINADO) || banderaInterrupcion){
            // Si el pc supera al fin de memoria, entonces se llegó a la última instrucción
            banderaInterrupcion = false;
            procesoEjecutando.establecerEstado(BCP.TERMINADO);
            // Si el proceso ha terminado, entonces se limpia la memoria.
            for(int i=procesoEjecutando.obtenerInicioMemoria();i<=procesoEjecutando.obtenerFinMemoria();i++){
               
                CPU.memoriaVirtual[i] = "0000 0000 00000000";
            }
        }
        
        procesoEjecutando.establecerRegistros(registros[1], registros[2], registros[3], registros[4], IR, registros[0],PC,instrucciones);
    }
    
    /**
     * Establece el contexto, del proceso entrante, en el núcleo.
     * Esta función se llama para el cambio de contexto.
     * @param procesoEntrante 
     */
    private void establecerContexto(BCP procesoEntrante){
        int[] registrosProceso=procesoEntrante.obtenerRegistros();
        registros[0]=registrosProceso[5];
        registros[1]=registrosProceso[0];
        registros[2]=registrosProceso[1];
        registros[3]=registrosProceso[2];
        registros[4]=registrosProceso[3];
        IR=registrosProceso[4];
        PC=registrosProceso[6];
        inicioMemoria = procesoEntrante.obtenerInicioMemoria();
        //finMemoria = procesoEntrante.obtenerFinMemoria();
        procesoEjecutando=procesoEntrante;
        procesoEjecutando.establecerEstado(BCP.EN_EJECUCION);
    }
    
    /**
     * Guarda el contexto (información de la ejecución) del procesos actual(ejecutandose) y carga...
     * ...el contexto del proceso entrante para realizar las operaciones.
     * @param procesoEntrante 
     */
    private void cambioContexto(BCP procesoEntrante){
        guardarContexto();
        if(procesoEjecutando.obtenerEstadoProceso()!=BCP.TERMINADO){
            procesoEjecutando.establecerEstado(BCP.PREPARADO);
        }      
        establecerContexto(procesoEntrante);
    }
}
