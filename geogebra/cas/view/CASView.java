package geogebra.cas.view;

import geogebra.Application;
import geogebra.cas.GeoGebraCAS;
import geogebra.kernel.Kernel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

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

	private GeoGebraCAS cas;

	private CASSession session;

	private final int numOfRows = 25;

	public CASView(Application app) {
		kernel = app.getKernel();
		this.app = app;
		cas = new GeoGebraCAS();
		session = new CASSession();
		setLayout(new BorderLayout());

		consoleTable = createTable(numOfRows);

		//Set the property of the value column;
		consoleTable.getColumn(consoleTable.getColumnName(CASPara.contCol)).setCellRenderer(
				new CASTableCellRender(this, consoleTable));
		consoleTable.getColumn(consoleTable.getColumnName(CASPara.contCol)).setCellEditor(
				new CASTableCellEditor(new JTextField(), new CASTableCell(this,
						consoleTable)));

		// CAScontroller
		CASKeyController casKeyCtrl = new CASKeyController(this, session, consoleTable);
		CASMouseController casMouseCtrl = new CASMouseController(this, session, consoleTable);
		consoleTable.addKeyListener(casKeyCtrl);
		consoleTable.addMouseListener(casMouseCtrl);
		
		// init the scroll panel
		JScrollPane scrollPane = new JScrollPane(consoleTable,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
	}

	public GeoGebraCAS getCAS() {
		return cas;
	}

	public JTable getConsoleTable() {
		return consoleTable;
	}
	
	/**
	 * Can be used by subclasses to customize the underlying JTable
	 * 
	 * @return The JTable to be used by the CAS
	 */
	protected JTable createTable(int rows) {
		JTable t = new JTable();

		t.setShowGrid(false);
		
		// Dynamically change the height of the table
		t.setRowHeight(CASPara.originalHeight);
		t.setBackground(Color.white);
		// t.setDefaultRenderer(Object.class, new MyRenderer());

		tableModel = new CASTableModel(consoleTable, rows, session, app);
		t.setModel(tableModel);
		
		//Set the width of the index column;
		t.getColumn(t.getColumnName(CASPara.indexCol)).setMinWidth(30);
		t.getColumn(t.getColumnName(CASPara.indexCol)).setMaxWidth(30);
		t.sizeColumnsToFit(0);
		t.setSurrendersFocusOnKeystroke(true);
		//System.out.println("SurrendersFocusOn" + t.getSurrendersFocusOnKeystroke());
	
		return t;
	}



	/**
	 * Inits a panel to hold all components given in the list jcomponents.
	 * 
	 * @param panel
	 * @param jcomponents
	 */
	private void initSessionPanel(JPanel panel, ArrayList jcomponents) {
		// panel.removeAll();

		// // create grid with one column
		// panel.setLayout(new GridBagLayout());
		// GridBagConstraints c = new GridBagConstraints();
		// c.fill = GridBagConstraints.HORIZONTAL;
		// c.anchor = GridBagConstraints.NORTHWEST;
		// c.weightx = 1.0;
		// c.weighty = 0;
		//		
		//		
		// for (int i = 0; i < jcomponents.size(); i++) {
		// JComponent p = (JComponent) jcomponents.get(i);
		// c.gridx = 0;
		// c.gridy = i;
		//								
		// panel.add(p, c);
		// }
		//		
		// c.weighty = 1.0;
		// panel.add(Box.createVerticalGlue(), c);
	}



}