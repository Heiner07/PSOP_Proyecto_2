/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_2;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Heiner
 */
public class EditorCeldas extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
        super.getTableCellRendererComponent(jtable, o, bln, bln1, i, i1);
        this.setOpaque(false);
        this.setBackground(Color.WHITE);
        this.setForeground(Color.BLACK);
        if(i1 == 5){
            String estado = (String)o;
            if(estado.equals(Proceso.estadoProcesoCadena(Proceso.NUEVO))){
                this.setOpaque(true);
                this.setBackground(Color.RED);
                this.setForeground(Color.WHITE);
            }
            else if(estado.equals(Proceso.estadoProcesoCadena(Proceso.PREPARADO))){
                this.setOpaque(true);
                this.setBackground(Color.BLUE);
                this.setForeground(Color.WHITE);
            }
            else if(estado.equals(Proceso.estadoProcesoCadena(Proceso.EN_EJECUCION))){
                this.setOpaque(true);
                this.setBackground(Color.GREEN);
            }
            else if(estado.equals(Proceso.estadoProcesoCadena(Proceso.EN_ESPERA))){
                this.setOpaque(true);
                this.setBackground(Color.YELLOW);
            }
        }
        return this;
    }
    
}
