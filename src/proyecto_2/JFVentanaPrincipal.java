/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_2;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;


public class JFVentanaPrincipal extends javax.swing.JFrame {
    
    PanelQuantum panelConfigCPU;
    PanelPaginacion panelConfigPaginacion;
    String rutaArchivo;
    Boolean archivoCargado;
    Boolean configurado;
    DefaultTableModel modeloTablaArchivos, modeloTablaMemoria, modeloTablaDisco, modeloTablaNucleo1, modeloTablaNucleo2;
    List<String> archivos;
    List<List<JLabel>> nucleo1, nucleo2;
    List<JLabel> procesosN1, procesosN2;
    CPU cpu;
    int algoritmoCPUSeleccionado;
    int algoritmoMemoriaSeleccionado;
    
    /* Hilos de control */
    Timer timerControlProcesos, timerControlNucleos, timerControlMemoria, timerControlDisco;
    
    /**
     * Creates new form JFVentanaPrincipal
     */
    public JFVentanaPrincipal() {
        initComponents();
        this.modeloTablaArchivos = (DefaultTableModel) jtArchivos.getModel();
        jtArchivos.setDefaultRenderer (Object.class, new EditorCeldas());
        this.archivos=new ArrayList<>();
        this.nucleo1=new ArrayList<>();
        this.nucleo2=new ArrayList<>();
        this.procesosN1=new ArrayList<>();
        this.procesosN2=new ArrayList<>();
        this.cpu=new CPU();
        this.archivoCargado=false;
        this.configurado=false;
        this.algoritmoCPUSeleccionado = 0;
        this.algoritmoMemoriaSeleccionado = 0;
        configurarTablaMemoria();
        configurarTablaDisco();
        configurarTablaNucleo1();
        configurarTablaNucleo2();
        configuararHilos();
        this.setLocationRelativeTo(null);
    }
    
    private void configurarTablaMemoria(){
        this.modeloTablaMemoria = (DefaultTableModel) jtMemoria.getModel();
        this.modeloTablaMemoria.setRowCount(0);
        for(int i=0;i<CPU.LARGOMEMORIA;i++){
            modeloTablaMemoria.addRow(new Object[]{i,"0000"});
        }
    }
    
    private void configurarTablaDisco(){
        this.modeloTablaDisco = (DefaultTableModel) jtDisco.getModel();
        this.modeloTablaDisco.setRowCount(0);
        for(int i=0;i<CPU.LARGODISCO;i++){
            modeloTablaDisco.addRow(new Object[]{i,"0000"});
        }
    }
    
    /**
     * Crea la tabla del núcleo 1 donde se mostrará la ejecución de los algortimos de CPU
     */
    private void configurarTablaNucleo1(){
        JLabel label;
        configurarTablaProcesosN1();
        
        /* Se limpia la interfaz y las estructuras de control */
        panelNucleo1.removeAll();
        nucleo1.clear();
        
        panelNucleo1.setLayout(new GridLayout(CPU.PROCESOSPORNUCLEO+1, 80));
        for(int i = 0; i < CPU.PROCESOSPORNUCLEO+1; i++){
            List<JLabel> fila = new ArrayList<>();
            for(int j = 0; j < 80; j++){
                if(i == 0){
                    label = new JLabel(" "+String.valueOf(j+1)+" ");
                }else{
                    label = new JLabel("");
                }
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("Arial", 0, 11));
                label.setBackground(Color.WHITE);
                label.setOpaque(true);
                panelNucleo1.add(label);
                fila.add(label);
            }
            nucleo1.add(fila);
        }
        panelNucleo1.updateUI();
    }
    
    /**
     * Crea los labels donde se colocaran los nombres de los procesos para este núcleo
     */
    private void configurarTablaProcesosN1(){
        JLabel label;
        
        /* Se limpia la interfaz y las estructuras de control */
        panelProcesosNucleo1.removeAll();
        procesosN1.clear();
        
        panelProcesosNucleo1.setLayout(new GridLayout(CPU.PROCESOSPORNUCLEO+1, 1));
        label = new JLabel("Procesos");
        label.setFont(new Font("Arial", 0, 11));
        panelProcesosNucleo1.add(label);
        for(int i = 0; i < CPU.PROCESOSPORNUCLEO; i++){
            label = new JLabel(" ");
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            label.setFont(new Font("Arial", 0, 11));
            label.setBackground(Color.WHITE);
            label.setOpaque(true);
            panelProcesosNucleo1.add(label);
            procesosN1.add(label);
        }
    }
    
    /**
     * Crea la tabla del núcleo 2 donde se mostrará la ejecución de los algortimos de CPU
     */
    private void configurarTablaNucleo2(){
        JLabel label;
        configurarTablaProcesosN2();
        
        /* Se limpia la interfaz y las estructuras de control */
        panelNucleo2.removeAll();
        nucleo2.clear();
        
        panelNucleo2.setLayout(new GridLayout(CPU.PROCESOSPORNUCLEO+1, 80));
        for(int i = 0; i < CPU.PROCESOSPORNUCLEO+1; i++){
            List<JLabel> fila = new ArrayList<>();
            for(int j = 0; j < 80; j++){
                if(i == 0){
                    label = new JLabel(" "+String.valueOf(j+1)+" ");
                }else{
                    label = new JLabel("");
                }
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("Arial", 0, 11));
                label.setBackground(Color.WHITE);
                label.setOpaque(true);
                panelNucleo2.add(label);
                fila.add(label);
            }
            nucleo2.add(fila);
        }panelNucleo2.updateUI();
    }
    
    /**
     * Crea los labels donde se colocaran los nombres de los procesos para este núcleo
     */
    private void configurarTablaProcesosN2(){
        JLabel label;
        
        /* Se limpia la interfaz y las estructuras de control */
        panelProcesosNucleo2.removeAll();
        procesosN2.clear();
        
        panelProcesosNucleo2.setLayout(new GridLayout(CPU.PROCESOSPORNUCLEO+1, 1));
        label = new JLabel("Procesos");
        label.setFont(new Font("Arial", 0, 11));
        panelProcesosNucleo2.add(label);
        for(int i = 0; i < CPU.PROCESOSPORNUCLEO; i++){
            label = new JLabel(" ");
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            label.setFont(new Font("Arial", 0, 11));
            label.setBackground(Color.WHITE);
            label.setOpaque(true);
            panelProcesosNucleo2.add(label);
            procesosN2.add(label);
        }
    }
    
    /**
     * Establece los procesos, con sus datos, cargados en el programa en la interfaz.
     */
    private void configurarTablaProcesos(){
        this.modeloTablaArchivos = (DefaultTableModel) jtArchivos.getModel();
        this.modeloTablaArchivos.setRowCount(0);
        List<Proceso> procesos = cpu.obtenerProcesos();
        Proceso proceso;
        int cantidadProcesos = procesos.size();
        String nombre, estado;
        int rafaga, tiempoLlegada, prioridad, tamanio;
        for(int i = 0; i < cantidadProcesos; i++){
            proceso = procesos.get(i);
            nombre = proceso.obtenerNombre();
            estado = Proceso.estadoProcesoCadena(proceso.obtenerEstadoProceso());
            rafaga = proceso.obtenerRafaga();
            tiempoLlegada = proceso.obtenerTiempoLLegada();
            prioridad = proceso.obtenerPrioridad();
            tamanio = proceso.obtenerTamanio();
            modeloTablaArchivos.addRow(new Object[]{nombre, rafaga, tiempoLlegada, prioridad, tamanio, estado });
        }
    }
    
    /**
     * Establece el nombre de los procesos asignados a cada núcleo en la interfaz
     */
    private void configurarProcesosNucleos(){
        // Obtengo los núcleos del CPU
        Nucleo n1 = cpu.obtenerNucleo1();
        Nucleo n2 = cpu.obtenerNucleo2();
        // Obtengo los procesos de cada núcleo
        List<Proceso> procesosObtenidosN1 = n1.obtenerProcesos();
        List<Proceso> procesosObtenidosN2 = n2.obtenerProcesos();
        int numeroProcesosN1 = procesosObtenidosN1.size();
        int numeroProcesosN2 = procesosObtenidosN2.size();
        JLabel label;
        
        //Establezco los procesos en la interfaz
        for(int i = 0; i < numeroProcesosN1 && i < CPU.PROCESOSPORNUCLEO; i++){
            label = procesosN1.get(i);
            label.setText(procesosObtenidosN1.get(i).obtenerNombre());
            label.setBackground(Colores.COLORES[i]);
            if(i == 2 || i == 5){
                label.setForeground(Color.WHITE);
            }
        }
        
        for(int i = 0; i < numeroProcesosN2 && i < CPU.PROCESOSPORNUCLEO; i++){
            label = procesosN2.get(i);
            label.setText(procesosObtenidosN2.get(i).obtenerNombre());
            label.setBackground(Colores.COLORES[i]);
            if(i == 2 || i == 5){
                label.setForeground(Color.WHITE);
            }
        }
    }
    
    /**
     * Se encarga de llamar a las distintas funciones que inicializan los hilos que actualizan...
     * ...los valores en la interfaz gráfica.
     */
    private void configuararHilos(){
        configurarHiloProcesos();
        configurarHiloNucleos();
        configurarHiloMemoria();
        configurarHiloDisco();
    }
    
    /**
     * Establece la función para el timer timerControlProcesos que se encargará de actualizar...
     * ...los valores de los procesos en la interfaz gráfica.
     */
    private void configurarHiloProcesos(){
        timerControlProcesos = new Timer(1000, (ActionEvent ae) -> {
            // Función que repetirá segun el intervalo asignado (1 segundo).
            controlGraficoProcesos();
        });
        // Inicializo el timer.
        timerControlProcesos.start();
    }
    
    /**
     * Controla los valores de los procesos que se muestran en la interfaz gráfica.
     * Este método es invocado por el timer timerControlProcesos.
     */
    private void controlGraficoProcesos(){
        List<Proceso> procesos = cpu.obtenerProcesos();
        Proceso proceso;
        int cantidadProcesos = procesos.size();
        for(int i = 0; i < cantidadProcesos; i++){
            proceso = procesos.get(i);
            modeloTablaArchivos.setValueAt(Proceso.estadoProcesoCadena(proceso.obtenerEstadoProceso()), i, 5);
        }
    }
    
    /**
     * Establece la función para el timer timerControlDisco que se encargará de actualizar...
     * ...los valores del disco en la interfaz gráfica.
     */
    private void configurarHiloDisco(){
        timerControlDisco = new Timer(1000, (ActionEvent ae) -> {
            // Función que repetirá segun el intervalo asignado (1 segundo).
            controlGraficoDisco();
        });
        // Inicializo el timer.
        timerControlDisco.start();
    }
    
    /**
     * Controla los valores del disco que se muestran en la interfaz gráfica.
     * Carga los valores del disco y actualiza los valores en la interfaz gráfica.
     * Este método es invocado por el timer timerControlDisco.
     */
    private void controlGraficoDisco(){
        String[] instruccion;
        for(int i=0;i<CPU.LARGODISCO;i++){
            instruccion = CPU.disco[i].split(" ");
            modeloTablaDisco.setValueAt(instruccion[0], i, 1);
        }
    }
    
    /**
     * Establece la función para el timer timerControlMemoria que se encargará de actualizar...
     * ...los valores de la memoria en la interfaz gráfica.
     */
    private void configurarHiloMemoria(){
        timerControlMemoria = new Timer(1000, (ActionEvent ae) -> {
            // Función que repetirá segun el intervalo asignado (1 segundo).
            controlGraficoMemoria();
        });
        // Inicializo el timer.
        timerControlMemoria.start();
    }
    
    /**
     * Controla los valores de la memoria que se muestran en la interfaz gráfica.
     * Carga los valores de memoria y actualiza los valores en la interfaz gráfica.
     * Este método es invocado por el timer timerControlMemoria.
     */
    private void controlGraficoMemoria(){
        String[] instruccion;
        for(int i=0;i<CPU.LARGOMEMORIA;i++){
            instruccion = CPU.memoriaVirtual[i].split(" ");
            modeloTablaMemoria.setValueAt(instruccion[0], i, 1);
        }
    }
    
    /**
     * Establece la función para el timer timerControlNucleos que se encargará de actualizar...
     * ...los valores de los Núcleos en la interfaz gráfica.
     */
    private void configurarHiloNucleos(){
        timerControlNucleos = new Timer(1000, (ActionEvent ae) -> {
            // Función que repetirá según el intervalo asignado (1 segundo).
            controlGraficoNucleos();
        });
        // Inicializo el timer.
        timerControlNucleos.start();
    }
    
    /**
     * Controla los Núcleos que se muestran en la interfaz gráfica.
     * Carga los Núcleos del cpu, actualiza los valores de los núcleos en la interfaz gráfica.
     * Este método es invocado por el timer timerControlNucleos.
     */
    private void controlGraficoNucleos(){
        // Obtengo los núcleos del CPU
        Nucleo n1 = cpu.obtenerNucleo1();
        Nucleo n2 = cpu.obtenerNucleo2();
        if(n1.obtenerEstado() || n2.obtenerEstado()){
            // Obtengo los procesos de cada núcleo
            List<Proceso> procesosEjecutandoN1 = n1.obtenerEjecucionProcesos();
            List<Proceso> procesosEjecutandoN2 = n2.obtenerEjecucionProcesos();
            int numeroProcesosN1 = procesosEjecutandoN1.size();
            int numeroProcesosN2 = procesosEjecutandoN2.size();
            JLabel label;
            Proceso proceso;

            //Establezco los datos generados hasta el momento según el algoritmo ejecutado
            for(int i = 0; i < numeroProcesosN1; i++){
                proceso = procesosEjecutandoN1.get(i);
                if(proceso.obtenerEstadoProceso() != -1){
                    label = nucleo1.get(proceso.obtenerNumeroProceso()).get(i);
                    label.setBackground(Colores.COLORES[proceso.obtenerNumeroProceso()-1]);
                }
            }

            for(int i = 0; i < numeroProcesosN2; i++){
                proceso = procesosEjecutandoN2.get(i);
                if(proceso.obtenerEstadoProceso() != -1){
                    label = nucleo2.get(proceso.obtenerNumeroProceso()).get(i);
                    label.setBackground(Colores.COLORES[proceso.obtenerNumeroProceso()-1]);
                }
            }
        }else{
            btCargarArchivo.setEnabled(true);
            btEjecutar.setEnabled(true);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btCargarArchivo = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtArchivos = new javax.swing.JTable();
        btEjecutar = new javax.swing.JButton();
        lbConfigMensaje = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtMemoria = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jtDisco = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jspNucleo1 = new javax.swing.JScrollPane();
        panelNucleo1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        panelProcesosNucleo1 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        panelProcesosNucleo2 = new javax.swing.JPanel();
        jspNucleo2 = new javax.swing.JScrollPane();
        panelNucleo2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cbAlgoritmoCPU = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        cbAlgoritmoMemoria = new javax.swing.JComboBox<>();
        btUtilizarConfiguracion = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jspTamanioMemoria = new javax.swing.JSpinner();
        jLabel10 = new javax.swing.JLabel();
        jspTamanioDisco = new javax.swing.JSpinner();
        panelConfigAlgoritmos = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Proyecto_2");

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btCargarArchivo.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btCargarArchivo.setText("Cargar Archivo");
        btCargarArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCargarArchivoActionPerformed(evt);
            }
        });

        jtArchivos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Proceso", "Ráfaga", "Tiempo llegada", "Prioridad", "Tamaño (kb)", "Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtArchivos.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jtArchivos);
        if (jtArchivos.getColumnModel().getColumnCount() > 0) {
            jtArchivos.getColumnModel().getColumn(0).setPreferredWidth(90);
        }

        btEjecutar.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btEjecutar.setText("Ejecutar");
        btEjecutar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEjecutarActionPerformed(evt);
            }
        });

        lbConfigMensaje.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lbConfigMensaje.setText("Configuración: Pendiente");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btCargarArchivo)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btEjecutar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbConfigMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btCargarArchivo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbConfigMensaje)
                    .addComponent(btEjecutar))
                .addContainerGap())
        );

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setText("Memoria");

        jtMemoria.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Posición", "Proceso"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtMemoria.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(jtMemoria);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3)
                .addContainerGap())
        );

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText("Disco");

        jtDisco.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Posición", "Proceso"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtDisco.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(jtDisco);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 155, Short.MAX_VALUE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4)
                .addContainerGap())
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("CPU");

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Núcleo 1");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Núcleo 2");

        panelNucleo1.setLayout(new java.awt.GridLayout(1, 0));
        jspNucleo1.setViewportView(panelNucleo1);

        panelProcesosNucleo1.setMaximumSize(new java.awt.Dimension(65, 205));
        panelProcesosNucleo1.setMinimumSize(new java.awt.Dimension(65, 205));

        javax.swing.GroupLayout panelProcesosNucleo1Layout = new javax.swing.GroupLayout(panelProcesosNucleo1);
        panelProcesosNucleo1.setLayout(panelProcesosNucleo1Layout);
        panelProcesosNucleo1Layout.setHorizontalGroup(
            panelProcesosNucleo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 65, Short.MAX_VALUE)
        );
        panelProcesosNucleo1Layout.setVerticalGroup(
            panelProcesosNucleo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 205, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(panelProcesosNucleo1);

        panelProcesosNucleo2.setMaximumSize(new java.awt.Dimension(65, 205));
        panelProcesosNucleo2.setMinimumSize(new java.awt.Dimension(65, 205));

        javax.swing.GroupLayout panelProcesosNucleo2Layout = new javax.swing.GroupLayout(panelProcesosNucleo2);
        panelProcesosNucleo2.setLayout(panelProcesosNucleo2Layout);
        panelProcesosNucleo2Layout.setHorizontalGroup(
            panelProcesosNucleo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 65, Short.MAX_VALUE)
        );
        panelProcesosNucleo2Layout.setVerticalGroup(
            panelProcesosNucleo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 205, Short.MAX_VALUE)
        );

        jScrollPane6.setViewportView(panelProcesosNucleo2);

        panelNucleo2.setLayout(new java.awt.GridLayout(1, 0));
        jspNucleo2.setViewportView(panelNucleo2);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jspNucleo1, javax.swing.GroupLayout.DEFAULT_SIZE, 1024, Short.MAX_VALUE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jspNucleo2)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jspNucleo1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jspNucleo2, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText("Selección de algoritmos");

        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setText("Algoritmo CPU");

        cbAlgoritmoCPU.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FCFS", "SJF (apropiativo)", "RR", "Feedback", "HRRN", "Prioridad (apropiativo)" }));

        jLabel8.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel8.setText("Algoritmo Memoria");

        cbAlgoritmoMemoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Fija", "Dinámica", "Paginación", "Segmentación" }));

        btUtilizarConfiguracion.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btUtilizarConfiguracion.setText("Utilizar esta configuración");
        btUtilizarConfiguracion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btUtilizarConfiguracionActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel9.setText("Tamaño Memoria");

        jspTamanioMemoria.setModel(new javax.swing.SpinnerNumberModel(128, 1, null, 1));

        jLabel10.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel10.setText("Tamaño Disco");

        jspTamanioDisco.setModel(new javax.swing.SpinnerNumberModel(1024, 1, null, 1));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jspTamanioDisco, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btUtilizarConfiguracion)
                    .addComponent(jLabel9)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jspTamanioMemoria, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(cbAlgoritmoCPU, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(cbAlgoritmoMemoria, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel10))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbAlgoritmoCPU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbAlgoritmoMemoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jspTamanioMemoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jspTamanioDisco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btUtilizarConfiguracion)
                .addContainerGap())
        );

        panelConfigAlgoritmos.setLayout(new java.awt.GridLayout(1, 2));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelConfigAlgoritmos, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 311, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelConfigAlgoritmos, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btEjecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEjecutarActionPerformed
        if(configurado){
            if(cpu.obtenerProcesos().size() > 0){
                cpu.limpiarProcesos();
                cpu.cargarPrograma(rutaArchivo);
                configurarTablaNucleo1();
                configurarTablaNucleo2();
                configurarProcesosNucleos();
                cpu.empezarEjecucion();
                btEjecutar.setEnabled(false);
                btCargarArchivo.setEnabled(false);
            }else{
                JOptionPane.showMessageDialog(this, "No hay procesos cargados",
                            "Cargue procesos",JOptionPane.WARNING_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(this, "Establezca la configuración por utilizar",
                            "Configuración de algoritmos",JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btEjecutarActionPerformed

    private void btCargarArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCargarArchivoActionPerformed
        JFileChooser cargador=new JFileChooser();
        cargador.setFileFilter(new FileNameExtensionFilter("CPU", "cpu"));
        cargador.showOpenDialog(this);
        File archivo = cargador.getSelectedFile();
        if(archivo!=null){
            rutaArchivo=archivo.getPath();
            archivoCargado=true;
            // Reinicia los valores
            cpu.limpiarProcesos();
            //Leer los procesos del archivo cargado
            List<String> errores = cpu.cargarPrograma(rutaArchivo);
            configurarTablaProcesos();
            if(errores.size() > 0){
                JOptionPane.showMessageDialog(this, "Se presentaron los siguientes errores:\n"+errores.toString(),
                            "Error cargando el archivo",JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btCargarArchivoActionPerformed

    private void btUtilizarConfiguracionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btUtilizarConfiguracionActionPerformed
        /* Obtengo los valores de la interfaz */
        algoritmoCPUSeleccionado = cbAlgoritmoCPU.getSelectedIndex();
        algoritmoMemoriaSeleccionado = cbAlgoritmoMemoria.getSelectedIndex();
        int largoMemoria = (int)jspTamanioMemoria.getValue();
        int largoDisco = (int)jspTamanioDisco.getValue();
        
        /* Establezco los algoritmos seleccionados en el CPU */
        CPU.ALGORITMO_CPU = algoritmoCPUSeleccionado;
        CPU.ALGORITMO_MEMORIA = algoritmoMemoriaSeleccionado;
        
        /* Establezco el mensaje de la configuración seleccionada en la interfaz */
        lbConfigMensaje.setText("Configuración: "+ cbAlgoritmoCPU.getSelectedItem() +" y "+
                cbAlgoritmoMemoria.getSelectedItem());
        
        /* Actualizo los valores de la memoria y disco en el CPU */
        cpu.establecerValoresMemoriaDisco(largoMemoria, largoDisco);
        
        /* Vuelvo a formar las tablas con los valores nuevos */
        configurarTablaDisco();
        configurarTablaMemoria();
        
        /* Establezco los paneles de configuración correspondientes con los algoritmos seleccionados */
        panelConfigAlgoritmos.removeAll();
        if(algoritmoCPUSeleccionado == 2 || algoritmoCPUSeleccionado == 3){
            panelConfigCPU = new PanelQuantum();
            panelConfigCPU.setBounds(0, 0, 231, 250);
            panelConfigAlgoritmos.add(panelConfigCPU);
        }else{
            panelConfigCPU = null;
        }
        
        if(algoritmoMemoriaSeleccionado == 2){
            panelConfigPaginacion = new PanelPaginacion();
            panelConfigPaginacion.setBounds(0, 232, 257, 250);
            panelConfigAlgoritmos.add(panelConfigPaginacion);
        }else{
            panelConfigPaginacion = null;
        }panelConfigAlgoritmos.updateUI();
        configurado = true;
    }//GEN-LAST:event_btUtilizarConfiguracionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCargarArchivo;
    private javax.swing.JButton btEjecutar;
    private javax.swing.JButton btUtilizarConfiguracion;
    private javax.swing.JComboBox<String> cbAlgoritmoCPU;
    private javax.swing.JComboBox<String> cbAlgoritmoMemoria;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jspNucleo1;
    private javax.swing.JScrollPane jspNucleo2;
    private javax.swing.JSpinner jspTamanioDisco;
    private javax.swing.JSpinner jspTamanioMemoria;
    private javax.swing.JTable jtArchivos;
    private javax.swing.JTable jtDisco;
    private javax.swing.JTable jtMemoria;
    private javax.swing.JLabel lbConfigMensaje;
    private javax.swing.JPanel panelConfigAlgoritmos;
    private javax.swing.JPanel panelNucleo1;
    private javax.swing.JPanel panelNucleo2;
    private javax.swing.JPanel panelProcesosNucleo1;
    private javax.swing.JPanel panelProcesosNucleo2;
    // End of variables declaration//GEN-END:variables
}
