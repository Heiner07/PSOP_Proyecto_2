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
public class EditorCeldasDisco extends DefaultTableCellRenderer {

    int indiceColor = -1;
    
    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object valor, boolean bln, boolean bln1, int fila, int columna) {
        super.getTableCellRendererComponent(jtable, valor, bln, bln1, fila, columna);
        this.setOpaque(false);
        this.setBackground(Color.WHITE);
        this.setForeground(Color.BLACK);
        indiceColor = CPU.obtenerColorBloque(fila+CPU.LARGOMEMORIA);
        if(indiceColor != -1){
            this.setOpaque(true);
            this.setBackground(Colores.COLORES[indiceColor]);
        }
        return this;
    }
    
}
