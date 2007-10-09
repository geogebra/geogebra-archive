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
       
       input = new JTextField();
       JScrollPane sp= new JScrollPane();
       JTable table = new JTable(1,20);
       output = new JTextField();
       
       add(input, BorderLayout.NORTH);
       sp.add(table);
       add(sp, BorderLayout.CENTER);
       add(output, BorderLayout.SOUTH);
       
       input.addActionListener(new ActionListener() {

		public void actionPerformed(ActionEvent arg0) {
			String inputText = input.getText();
			String evalStr = cas.evaluateYACAS(inputText);
			//String evalStr = 
			output.setText(evalStr);
		}
    	   
       });
     }
    
  
   
}