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
    
    private Proceso procesoEjecutando=null;
    static int numeroInstrucciones=0;
    private int numeroNucleo;
    private Boolean bandera= false;
    private Boolean ejecutar = false;
    private Boolean esperaInterrupcion = false; // Indica si el núcleo está a la espera que se complete una interrupción.
    private Timer timerOperacion;
    private int tiempoRestante=1; // Variable que indicara cuantos segendos debe esperar hasta recibir otra instrucción
    private boolean banderaInterrupcion=false;
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
    
    public void agregarProceso(Proceso proceso){
        procesos.add(proceso);
    }
    
    public List<Proceso> obtenerProcesos(){
        return procesos;
    }
    
    public List<Proceso> obtenerEjecucionProcesos(){
        return ejecucionProcesos;
    }
    
    /**
     * Función llamada por el timerOperación que controla la ejecución de las operaciones en el núcleo.
     * Si el tiempo es cero entonces verifica que hay una instrucción a ejecutar y la realiza, sino resta el tiempo...
     * ...requerido de la operación anterior.
     */
    private void controlEjecucionNucleo(){
        //try {
            if(tiempoRestante==0){
                if(ejecutar && !esperaInterrupcion){
                    ejecutar=false;
                    //Operaciones();
                }
            }else{
                tiempoRestante--;
            }
        /*} catch (InterruptedException ex) {
            // Modificar para mostrar mensaje correspondiente
            Logger.getLogger(Nucleo.class.getName()).log(Level.SEVERE, null, ex);
        }*/
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
    public void recibirProceso(Proceso proceso) throws InterruptedException{
        /*if(procesoEjecutando==null){
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
        }*/
    }
}
