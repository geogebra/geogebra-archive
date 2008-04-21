package tutor.gui.applet;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
 
public class TableButton3 extends JFrame
{
    public TableButton3()
    {
        String[] columnNames = {"Date", "String", "Integer", "Decimal", ""};
        Object[][] data =
        {
            {new Date(), "A", new Integer(1), new Double(5.1), "Delete1"},
            {new Date(), "B", new Integer(2), new Double(6.2), "Delete2"},
            {new Date(), "C", new Integer(3), new Double(7.3), "Delete3"},
            {new Date(), "D", new Integer(4), new Double(8.4), "Delete4"}
        };
 
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable( model ) {
               //####### This method if i keep outside as a separate method i can edit all the columns
              //#######but i want column 1 to be non-editable
                public boolean isCellEditable(int row, int column) {
                        int modelColumn = convertColumnIndexToModel( column );
                        return (modelColumn == 1) ? false : true;
                }
        };
 
        JScrollPane scrollPane = new JScrollPane( table );
        getContentPane().add( scrollPane );
 
        //  Create button column
        ButtonColumn buttonColumn = new ButtonColumn(table, 4);
                /*String strPlaf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
                
                try {
                        //set the look and feel as of windows
                        UIManager.setLookAndFeel(strPlaf);
                        //setting new grid border for table header
                        UIManager.put("TableHeader.cellBorder",BorderFactory.createEmptyBorder(1, 1, 1, 1)); 
                        SwingUtilities.updateComponentTreeUI(this);
                } catch(Exception e) {
                        e.printStackTrace();
                }*/
    }
 
    public static void main(String[] args)
    {
        TableButton3 frame = new TableButton3();
        frame.setDefaultCloseOperation( EXIT_ON_CLOSE );
        frame.pack();
        frame.setVisible(true);
    }
 
    class ButtonColumn extends AbstractCellEditor
        implements TableCellRenderer, TableCellEditor, ActionListener
    {
        JTable table;
        JButton renderButton;
        JButton editButton;
        String text;
 
        public ButtonColumn(JTable table, int column)
        {
            super();
            this.table = table;
            renderButton = new JButton();
 
            editButton = new JButton();
            editButton.setFocusPainted( false );
            editButton.addActionListener( this );
 
            TableColumnModel columnModel = table.getColumnModel();
            columnModel.getColumn(column).setCellRenderer( this );
            columnModel.getColumn(column).setCellEditor( this );
        }
        public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            if (isSelected)
            {
                renderButton.setForeground(table.getSelectionForeground());
                 renderButton.setBackground(table.getSelectionBackground());
            }
            else
            {
                renderButton.setForeground(table.getForeground());
                renderButton.setBackground(UIManager.getColor("Button.background"));
            }
 
            renderButton.setText( (value == null) ? "" : value.toString() );
            return renderButton;
        }
 
        public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column)
        {
            text = (value == null) ? "" : value.toString();
            editButton.setText( text );
            return editButton;
        }
 
        public Object getCellEditorValue()
        {
            return text;
        }
 
        public void actionPerformed(ActionEvent e)
        {
            fireEditingStopped();
            System.out.println( "Action: " + e.getActionCommand() );
        }
    }
}