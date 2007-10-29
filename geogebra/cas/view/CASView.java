package geogebra.cas.view;

import geogebra.cas.view.CASTableRenderer;

import geogebra.Application;
import geogebra.cas.GeoGebraCAS;
import geogebra.kernel.Kernel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JTextArea;
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
    private CASTableRenderer renderer;
    private CASSession session;
    private boolean showMenuBar = true;
    
    public CASView (Application app)
    {
       kernel =app.getKernel();
       cas = new GeoGebraCAS();
       session = new CASSession();
       setLayout(new BorderLayout());
       
      // input = new JTextField();
       //JTable table = new JTable(data, columnNames);
       JTable table = new JTable(new CASTableModel(session));
       
       /*
       //table.getModel().addTableModelListener(this);
       TableColumn console = table.getColumnModel().getColumn(0);
       console.setMinWidth(384);
       console.setCellRenderer(renderer);
       //JTable table = new JTable()
    //table.setFillsViewportHeight(true); <-- I think this is only for > 1.4.2
     //JScrollPane sp= new JScrollPane(table);
       JScrollPane sp= new JScrollPane(table, 
    		   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
    		   JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
       
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
 * */
       
       // init scroll pane
       setLayout(new BorderLayout());
       JPanel panel = new JPanel();
       panel.setBorder(BorderFactory.createEmptyBorder(5, 5,5,5));
		JScrollPane scrollPane = new JScrollPane(panel);
		//setPreferredSize(new Dimension(450, 110));
		add(scrollPane, BorderLayout.CENTER);
       
	
		 // focus listenerr
        FocusListener fl = new FocusListener() {
			public void focusGained(FocusEvent e) {
				Object src = e.getSource();
				
				if (src instanceof JTextArea) {
					JTextArea ta = (JTextArea) src;					
					ta.setBorder(BorderFactory.createLineBorder(Color.red));
				}
			}

			public void focusLost(FocusEvent e) {
				Object src = e.getSource();
				
				if (src instanceof JTextArea) {
					JTextArea ta = (JTextArea) src;					
					ta.setBorder(null);
				}
			}        	
        };
        
       
		// CAScontroller
        CASController casCtrl = new CASController(this, session);
        
		// create array of text fields
		ArrayList jcomponents = new ArrayList();
		for (int i = 0; i < 4; i++) {
			JTextArea ta = new JTextArea(1,1);			
			ta.setLineWrap(true);
			
			 // register focus listener with ta
			ta.addFocusListener(fl);
			
			// register key listener
			ta.addKeyListener(casCtrl);
			
			jcomponents.add(ta);
		}		 		
        initSessionPanel(panel, jcomponents);
        
       
        
        
     }
    
    public GeoGebraCAS getCAS() {
    	return cas;
    }
    
    /**
     * Inits a panel to hold all components given in the list jcomponents.
     * @param panel
     * @param jcomponents
     */
    private void initSessionPanel(JPanel panel, ArrayList jcomponents) {
    	 panel.removeAll();		
    	 
		// create grid with one column
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.weightx = 1.0;
		c.weighty = 0;
		
		
		for (int i = 0; i < jcomponents.size(); i++) {
			JComponent p = (JComponent) jcomponents.get(i);
			c.gridx = 0;
			c.gridy = i;
								
			panel.add(p, c);			
		}			
		
		c.weighty = 1.0;
		panel.add(Box.createVerticalGlue(), c);
    }
  
   
}