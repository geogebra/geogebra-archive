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
import javax.swing.JTextField;

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
       JTable table = new JTable(new CASTableModel(8,1));
       JScrollPane sp= new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
       //table.setFillsViewportHeight(true); <-- I think this is only for > 1.4.2
       //table.
     //  output = new JTextField();
       //add(sp, BorderLayout.NORTH);
      // add(input, BorderLayout.NORTH);
       sp.add(table);
       //table.setValueAt(new String("test"), 0, 0);
       add(sp, BorderLayout.CENTER);
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