package geogebra.cas.view;

import geogebra.cas.view.CASTableRenderer;

import geogebra.Application;
import geogebra.cas.GeoGebraCAS;
import geogebra.kernel.GeoElement;
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
import javax.swing.JCheckBox;
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
 * @author Quan Yuan
 *
 */
public class CASView extends JComponent {

	private Kernel kernel;

	private JTable consoleTable;

	private CASTableModel tableModel;

	private Application app;

	private JTextField input, output;

	private GeoGebraCAS cas;

	private CASTableRenderer renderer;

	private CASSession session;

	private boolean showMenuBar = true;

	private final int numOfRows = 25;

	public CASView(Application app) {
		kernel = app.getKernel();
		this.app = app;
		cas = new GeoGebraCAS();
		session = new CASSession();
		setLayout(new BorderLayout());

		// input = new JTextField();

		consoleTable = createTable();

		newTableModel(numOfRows);
		//new JTable(new CASTableModel(session));

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
		// init the table
		//CASTableCellController cellContrl =  new CASTableCellController(this, session);
		//Creat the cell one by one;
		//for(int i=0; i< this.numOfRows; i++){	
			consoleTable.getColumn("A").setCellRenderer(new CASTableCellRender(this,consoleTable));
			consoleTable.getColumn("A").setCellEditor(
					new CASTableCellEditor(new JTextField(), new CASTableCell(this,consoleTable)));
//			for(int i=0; i< this.numOfRows; i++){
//				tableModel.setValueAt(new CASTableCellEditor(new JTextField(), new CASTableCell(this,consoleTable)), 
//				i, 0);				
//			}
//			tableModel.setValueAt(new String("This is test"), 
//					0, 0);
//			tableModel.setValueAt(new String("This is test2"), 
//					1, 0);
//			tableModel.setValueAt(new CASTableCellEditor(new JTextField(), new CASTableCell(this,consoleTable)), 
//					2, 0);
//			tableModel.setValueAt(new CASTableCellEditor(new JTextField(), new CASTableCell(this,consoleTable)), 
//					3, 0);
		//}
		//Object[] rowData = new Object[1];
		//rowData[0] = new CASTableCell(this,consoleTable);	
		//tableModel.addRow(rowData);
		// focus listenerr
		//       FocusListener fl = new FocusListener() {
		//			public void focusGained(FocusEvent e) {
		//				Object src = e.getSource();
		//				
		//				if (src instanceof JTextArea) {
		//					JTextArea ta = (JTextArea) src;					
		//					ta.setBorder(BorderFactory.createLineBorder(Color.red));
		//				}
		//			}
		//
		//			public void focusLost(FocusEvent e) {
		//				Object src = e.getSource();
		//				
		//				if (src instanceof JTextArea) {
		//					JTextArea ta = (JTextArea) src;					
		//					ta.setBorder(null);
		//				}
		//			}        	
		//        };

		// CAScontroller
		CASController casCtrl = new CASController(this, session);
		consoleTable.addKeyListener(casCtrl);
		// init the scroll panel
		JScrollPane scrollPane = new JScrollPane(consoleTable,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		// create the cells of the table
		//ArrayList jcomponents = new ArrayList();
		//for (int i = 0; i < this.numOfRows; i++) {
		//CASTableCell ta = new CASTableCell();			
		//	JTextArea input = new JTextArea(1,1);
		//	input.append(">>");
		//	ta.setLineWrap(true);

		// register focus listener with ta
		//	ta.addFocusListener(fl);

		// register key listener
		//	ta.addKeyListener(casCtrl);

		//jcomponents.add(ta);
		//	tableModel.setValueAt(ta, i);
		//	tableModel.setValueAt(input, i);
		//}	

		//initSessionPanel(panel, jcomponents);  
	}

	public GeoGebraCAS getCAS() {
		return cas;
	}

	/** Can be used by subclasses to customize the underlying JTable
	 * @return The JTable to be used by the CAS
	 */
	protected JTable createTable() {
		JTable t = new JTable();
		
		t.setShowGrid(false);
		//Dynamically change the height of the table
		t.setRowHeight(22);
		t.setBackground(Color.white);
		//t.setDefaultRenderer(Object.class, new MyRenderer());
		return t;
	}

	/**
	 * Creates new blank SharpTableModel object with specified number of
	 * rows and columns.  table is set to this table model to update screen.
	 *
	 * @param rows number of rows in new table model
	 */
	private void newTableModel(int rows) {
		tableModel = new CASTableModel(consoleTable, rows, session, app);
		consoleTable.setModel(tableModel);
	}

	/**
	 * Inits a panel to hold all components given in the list jcomponents.
	 * @param panel
	 * @param jcomponents
	 */
	private void initSessionPanel(JPanel panel, ArrayList jcomponents) {
		//   	 panel.removeAll();		

		//		// create grid with one column
		//		panel.setLayout(new GridBagLayout());
		//		GridBagConstraints c = new GridBagConstraints();
		//		c.fill = GridBagConstraints.HORIZONTAL;
		//		c.anchor = GridBagConstraints.NORTHWEST;
		//		c.weightx = 1.0;
		//		c.weighty = 0;
		//		
		//		
		//		for (int i = 0; i < jcomponents.size(); i++) {
		//			JComponent p = (JComponent) jcomponents.get(i);
		//			c.gridx = 0;
		//			c.gridy = i;
		//								
		//			panel.add(p, c);			
		//		}			
		//		
		//		c.weighty = 1.0;
		//		panel.add(Box.createVerticalGlue(), c);
	}
	
}