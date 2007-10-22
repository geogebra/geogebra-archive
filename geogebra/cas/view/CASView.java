package geogebra.cas.view;

import geogebra.Application;
import geogebra.cas.GeoGebraCAS;
import geogebra.kernel.Kernel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * A class which will give the view of the CAS
 */

/**
 * @author James King
 *
 */
public class CASView extends JComponent
{
    
    private Kernel kernel;
    private JTextField input, output;
    private GeoGebraCAS cas;
    
    private boolean showMenuBar = true;
    
    public CASView(Application app)
    {
       kernel =app.getKernel();
       cas = new GeoGebraCAS();
       
       setLayout(new BorderLayout());
       
      // input = new JTextField();
       
       Object[][] data = {
    		   {"Welcome to Geogebra CAS!", new Integer(10)},
    		   {"Test", new Integer(5)}
       };
       
       String[] columnNames = {"Data", "Number"};
       //JTable table = new JTable(data, columnNames);
       JTable table = new JTable(new CASTableModel());
       TableColumn console = table.getColumnModel().getColumn(0);
       console.setMinWidth(384);
       //console.setCellRenderer(javax.swing.table.DefaultTableCellRenderer);
       //JTable table = new JTable()
    //table.setFillsViewportHeight(true); <-- I think this is only for > 1.4.2
     //JScrollPane sp= new JScrollPane(table);
       JScrollPane sp= new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
       
       //table.
     //  output = new JTextField();
       //add(sp, BorderLayout.NORTH);
      // add(input, BorderLayout.NORTH);
       add(sp, BorderLayout.CENTER);
   //    sp.add(table);
    //   table.setValueAt(new String("test"), 0, 0);
      
       //add(output, BorderLayout.SOUTH);
       
//       input.addActionListener(new ActionListener() {
//
//		public void actionPerformed(ActionEvent arg0) {
//			//String inputText = input.getText();
//			//String evalStr = cas.evaluateYACAS(inputText);
//			//String evalStr = 
//			//output.setText(evalStr);
//		}
//    	   
//       });
     }
    
  
   
}