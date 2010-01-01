package geogebra.cas.view;

import geogebra.cas.GeoGebraCAS;
import geogebra.gui.CasManager;
import geogebra.gui.view.algebra.MyComboBoxListener;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.View;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Computer algebra view.
 * 
 * @author Markus Hohenwarter, Quan Yuan
 */
public class CASView extends JComponent implements CasManager, FocusListener, View {
	
	private Kernel kernel;
	
	// TODO: add checkbox to set useGeoGebraVariableValues
	private boolean useGeoGebraVariableValues = true;

	private CASTable consoleTable;
	private CASInputHandler casInputHandler;

	private Application app;
	private GeoGebraCAS cas;
	private JPanel btPanel;
	

	public CASView(Application app) {
		kernel = app.getKernel();
		this.app = app;
		
		// init cas
		cas = (geogebra.cas.GeoGebraCAS) kernel.getGeoGebraCAS();	
//		Thread casInit = new Thread() {
//			public void run() {
//				cas.evaluateMathPiper("Simplify(1+1)");
//			}
//		};
//		casInit.start();		
	
		// CAS input/output cells
		createCASTable();	
		
		// row header
		final JList rowHeader = new RowHeader(consoleTable);		

		// init the scroll panel
		JScrollPane scrollPane = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setRowHeaderView(rowHeader);
		scrollPane.setViewportView(consoleTable);
		scrollPane.setBackground(Color.white);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
						
		// put the scrollpanel in 
		setLayout(new BorderLayout());	
		add(scrollPane, BorderLayout.CENTER);
		this.setBackground(Color.white);
						
		// tell rowheader about selection updates in table
		consoleTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						if (e.getValueIsAdjusting()) return;

						// table slection changed -> rowheader table selection			
						int [] selRows = consoleTable.getSelectedRows();
						if (selRows.length > 0)
							rowHeader.setSelectedIndices(selRows);
					}					
				});
		
		// listen to clicks below last row in consoleTable: create new row
		scrollPane.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					int clickedRow = consoleTable.rowAtPoint(e.getPoint());
					boolean undoNeeded = false;
					
					if (clickedRow < 0) {
						// clicked outside of console table
						int rows = consoleTable.getRowCount();
						if (rows == 0) {
							// insert first row
							consoleTable.insertRow(null, true);
							undoNeeded = true;
						} 
						else {
							CASTableCellValue cellValue = consoleTable.getCASTableCellValue(rows-1);
							if (cellValue.isEmpty()) {
								consoleTable.startEditingRow(rows-1);
							} else {
								consoleTable.insertRow(null, true);
								undoNeeded = true;
							}
						}
					}
					
					if (undoNeeded) {
						// store undo info
						getApp().storeUndoInfo();
					}
				}
		
			}
		);
		
		// input handler
		casInputHandler = new CASInputHandler(this);
		
		// Ulven 01.03.09: excange line 90-97 with:
		// BtnPanel which sets up all the button.
		//add(geogebra.cas.view.components.BtnPanel.getInstance(this),BorderLayout.NORTH);
		createButtonPanel();
	
		
		addFocusListener(this);		
	}		
	
	/** 
	 * Process currently selected cell using the given command and parameters, e.g.
	 *  "Integral", [ "x" ]
	 */	
	public void processInput(String ggbcmd, String[] params){
		casInputHandler.processInput(ggbcmd, params);
	}
	
	private void createButtonPanel() {
		if (btPanel != null)
			remove(btPanel);
		btPanel = initButtons();		
		add(btPanel, BorderLayout.NORTH);	
	}
	
	public void updateFonts() {
		if (app.getFontSize() == getFont().getSize()) return;
		
		setFont(app.getPlainFont());		
		createButtonPanel();		
		consoleTable.setFont(getFont());
		validate();
	}
	
	public Font getBoldFont() {
		return app.getBoldFont();
	}
	
	private void createCASTable() {
		consoleTable = new CASTable(this);
		
		CASTableCellController inputListener = new CASTableCellController(this);
		consoleTable.getEditor().getInputArea().addKeyListener(inputListener);
		//consoleTable.addKeyListener(inputListener);
			
		//consoleTable.addKeyListener(new ConsoleTableKeyListener());
		
		TableCellMouseListener tableCellMouseListener = new TableCellMouseListener(consoleTable);
		consoleTable.addMouseListener(tableCellMouseListener);		
	}

	public boolean getUseGeoGebraVariableValues() {
		return useGeoGebraVariableValues;
	}

	public GeoGebraCAS getCAS() {
		return cas;
	}

	public CASTable getConsoleTable() {
		return consoleTable;
	}

//	/**
//	 * returns settings in XML format
//	 */
//	public String getGUIXML() {
//		StringBuilder sb = new StringBuilder();
//		sb.append("<casView>\n");
//
//		int width = getWidth(); // getPreferredSize().width;
//		int height = getHeight(); // getPreferredSize().height;
//
//		// if (width > MIN_WIDTH && height > MIN_HEIGHT)
//		{
//			sb.append("\t<size ");
//			sb.append(" width=\"");
//			sb.append(width);
//			sb.append("\"");
//			sb.append(" height=\"");
//			sb.append(height);
//			sb.append("\"");
//			sb.append("/>\n");
//		}
//
//		sb.append("</casView>\n");
//		return sb.toString();
//	}

	public String getSessionXML() {
	
		StringBuilder sb = new StringBuilder();
		sb.append("<casSession>\n");

		// get the number of pairs in the view
		int numOfRows = consoleTable.getRowCount();

		// get the content of each pair in the table with a loop
		// append the content to the string sb
		for (int i = 0; i < numOfRows; ++i) {
			CASTableCellValue temp = consoleTable.getCASTableCellValue(i);
			sb.append(temp.getXML());
		}

		sb.append("</casSession>\n");
		return sb.toString();
	}
	
	/**
	 * Returns the output string in the n-th row of this CAS view. 
	 */
	public String getRowOutputValue(int n) {
		return consoleTable.getCASTableCellValue(n).getOutput();
	}
	
	/**
	 * Returns the input string in the n-th row of this CAS view. 
	 * If the n-th cell has no output string, the input string of this cell is returned.
	 */
	public String getRowInputValue(int n) {
		return consoleTable.getCASTableCellValue(n).getInput();
	}
	
	/**
	 * Returns the number of rows of this CAS view.
	 */
	public int getRowCount() {
		return consoleTable.getRowCount();
	}

	public JComponent getCASViewComponent() {
		return this;
	}
	

	//Ulven 01.03.09: Drop this, do it in components.BtnPanel
	private JPanel initButtons() {	

		final String [][][] menuStrings = {
			{  
				// command for apply, visible text, tooltip text
				{"Eval", 		"=", 		app.getPlain("Evaluate") }, 
				{"Numeric", 	"\u2248", 	app.getPlain("Approximate")}, 
				{"Hold", 		"\u2713", 	app.getPlain("CheckInput")}
			},		
			{  
				{"Expand", 		app.getCommand("Expand")}, 
				{"Factor", 		app.getCommand("Factor")},
				{"Simplify", 	app.getCommand("Simplify")},
				{"Substitute", 	app.getPlain("Substitute")},
				{"Solve", 		app.getPlain("Solve")}, 
				{"Derivative", 	"d/dx", 	 app.getCommand("Derivative")}, 
				{"Integral", 	"\u222b dx", app.getCommand("Integral")}	
			}
		};
		
		JPanel btPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JComboBox [] menus = new JComboBox[menuStrings.length];
				
		MyComboBoxListener ml = new MyComboBoxListener() {
			public void doActionPerformed(Object source) {	
				for (int i=0; i < menus.length; i++) {
					if (source == menus[i]) {
						int pos = menus[i].getSelectedIndex();
						processInput(menuStrings[i][pos][0], null);
						// update tooltip
						if (menuStrings[i][pos].length >= 3)
							menus[i].setToolTipText(menuStrings[i][pos][2]);
					}
				}
			}
		};
		
		for (int i=0; i < menus.length; i++) {
			menus[i] = new JComboBox();			
			for (int k=0; k < menuStrings[i].length; k++ ) {
				// visible text
				menus[i].addItem("  " + menuStrings[i][k][1]);
			}
			// tooltip
			if (menuStrings[i][0].length >= 3)
				menus[i].setToolTipText(menuStrings[i][0][2]);
			menus[i].setFocusable(false);
			menus[i].addMouseListener(ml);
			menus[i].addActionListener(ml);
			btPanel.add(menus[i]);
		}
							
		return btPanel;	
	}		

	public Application getApp() {
		return app;
	}
	
	public final boolean isUseGeoGebraVariableValues() {
		return useGeoGebraVariableValues;
	}

	public final void setUseGeoGebraVariableValues(boolean useGeoGebraVariableValues) {
		this.useGeoGebraVariableValues = useGeoGebraVariableValues;
	}

	public void focusGained(FocusEvent arg0) {
		// start editing last row
		int lastRow = consoleTable.getRowCount() - 1;
		if (lastRow >= 0)
			consoleTable.startEditingRow(lastRow);
	}

	public void focusLost(FocusEvent arg0) {
		
	}

	public void add(GeoElement geo) {
	}

	public void clearView() {
		// delete all rows
		consoleTable.deleteAllRows();
		
		// insert one empty row
		consoleTable.insertRow(new CASTableCellValue(this), false);
		repaintView();
	}
	
	/**
	 * Returns an empty row at the bottom of the cas view.
	 */
	public CASTableCellValue createRow() {
		return consoleTable.createRow();
	}

	public void remove(GeoElement geo) {
	}

	public void rename(GeoElement geo) {
	}

	public void repaintView() {
		consoleTable.updateAllRows();
		validate();
	}

	public void reset() {
		repaintView();
	}

	public void update(GeoElement geo) {
	}

	public void updateAuxiliaryObject(GeoElement geo) {
	}

	public void attachView() {
		kernel.attach(this);
	}

	public void detachView() {
		kernel.detach(this);
	}


}