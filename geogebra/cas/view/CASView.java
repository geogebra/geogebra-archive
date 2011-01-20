package geogebra.cas.view;

import geogebra.cas.GeoGebraCAS;
import geogebra.euclidian.EuclidianConstants;
import geogebra.gui.CasManager;
import geogebra.gui.view.algebra.MyComboBoxListener;
import geogebra.gui.view.spreadsheet.MyTable;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.Kernel;
import geogebra.kernel.View;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.HashSet;

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
public class CASView extends JComponent implements CasManager, FocusListener,
		View {

	private Kernel kernel;

	private boolean useGeoGebraVariableValues = true;

	private CASTable consoleTable;
	private CASInputHandler casInputHandler;
	private CASSubDialog subDialog;

	private GeoGebraCAS cas;
	private Application app;
	private JPanel btPanel;
	private HashMap<String, CASTableCellValue> assignmentCellMap;
	private HashSet<String> ignoreUpdateVars;
	private final RowHeader rowHeader;

	public CASView(Application app) {
		kernel = app.getKernel();
		this.app = app;

		// init CAS
		getCAS();

		// CAS input/output cells
		createCASTable();

		// map for assignments to cell
		assignmentCellMap = new HashMap<String, CASTableCellValue>();
		ignoreUpdateVars = new HashSet<String>();

		// row header
		//final JList rowHeader = new RowHeader(consoleTable);
		rowHeader = new RowHeader(consoleTable, true);
		
		// init the scroll panel
		JScrollPane scrollPane = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setRowHeaderView(rowHeader);
		scrollPane.setViewportView(consoleTable);
		scrollPane.setBackground(Color.white);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		//set the lower left corner so that the horizontal scroller looks good
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, MyTable.TABLE_GRID_COLOR));
		p.setBackground(Color.white);
		scrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER, p);
		

		// put the scrollpanel in
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		this.setBackground(Color.white);

		// tell rowheader about selection updates in table
		consoleTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						if (e.getValueIsAdjusting())
							return;

						// table slection changed -> rowheader table selection
						int[] selRows = consoleTable.getSelectedRows();
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
					} else {
						CASTableCellValue cellValue = consoleTable
								.getCASTableCellValue(rows - 1);
						if (cellValue.isEmpty()) {
							consoleTable.startEditingRow(rows - 1);
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

		});

		// input handler
		casInputHandler = new CASInputHandler(this);

		addFocusListener(this);

		// TODO: remove
		attachView();
	}
	
	
	public void showSubstituteDialog(String prefix, String evalText, String postfix, int selRow) {
		if (subDialog != null && subDialog.isShowing()) return;
		
		CASSubDialog d = new CASSubDialog(this, prefix, evalText, postfix, selRow);
		d.setAlwaysOnTop(true);
		d.setVisible(true);
		setSubstituteDialog(d);
	}
	
	public void setSubstituteDialog(CASSubDialog d) {
		subDialog = d;
	}
	
	public CASSubDialog getSubstituteDialog() {
		return subDialog;
	}

	/**
	 * Process currently selected cell using the given command and parameters,
	 * e.g. "Integral", [ "x" ]
	 */
	public void processInput(String ggbcmd, String[] params) {
		casInputHandler.processCurrentRow(ggbcmd, params);
		getApp().storeUndoInfo();
	}
	
	public void processRow(int row) {
		casInputHandler.processRow(row);
	}
	
	public String resolveCASrowReferences(String inputExp, int row) {
		String result = casInputHandler.resolveCASrowReferences(inputExp, row, CASInputHandler.ROW_REFERENCE_STATIC);
		return casInputHandler.resolveCASrowReferences(result, row, CASInputHandler.ROW_REFERENCE_DYNAMIC);
	}

	public void updateFonts() {
		if (app.getFontSize() == getFont().getSize())
			return;

		setFont(app.getPlainFont());
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
		// consoleTable.addKeyListener(inputListener);

		// consoleTable.addKeyListener(new ConsoleTableKeyListener());

		TableCellMouseListener tableCellMouseListener = new TableCellMouseListener(this);
		consoleTable.addMouseListener(tableCellMouseListener);
		
	}

	public boolean getUseGeoGebraVariableValues() {
		return useGeoGebraVariableValues;
	}

	final public GeoGebraCAS getCAS() {
		if (cas == null) {
			cas = (geogebra.cas.GeoGebraCAS) kernel.getGeoGebraCAS();
		}

		return cas;
	}

	public CASTable getConsoleTable() {
		return consoleTable;
	}

	// /**
	// * returns settings in XML format
	// */
	// public String getGUIXML() {
	// StringBuilder sb = new StringBuilder();
	// sb.append("<casView>\n");
	//
	// int width = getWidth(); // getPreferredSize().width;
	// int height = getHeight(); // getPreferredSize().height;
	//
	// // if (width > MIN_WIDTH && height > MIN_HEIGHT)
	// {
	// sb.append("\t<size ");
	// sb.append(" width=\"");
	// sb.append(width);
	// sb.append("\"");
	// sb.append(" height=\"");
	// sb.append(height);
	// sb.append("\"");
	// sb.append("/>\n");
	// }
	//
	// sb.append("</casView>\n");
	// return sb.toString();
	// }

	public void getSessionXML(StringBuilder sb) {
		// change kernel settings temporarily
		int oldCoordStlye = kernel.getCoordStyle();
		int oldPrintForm = kernel.getCASPrintForm();
        boolean oldValue = kernel.isTranslateCommandName();
		kernel.setCoordStyle(Kernel.COORD_STYLE_DEFAULT);	
		kernel.setCASPrintForm(ExpressionNode.STRING_TYPE_GEOGEBRA_XML);
        kernel.setTranslateCommandName(false); 

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
		
		// set back kernel
		kernel.setCoordStyle(oldCoordStlye);
		kernel.setCASPrintForm(oldPrintForm);
		kernel.setTranslateCommandName(oldValue);      
	}

	/**
	 * Returns the output string in the n-th row of this CAS view.
	 */
	public String getRowOutputValue(int n) {
		return consoleTable.getCASTableCellValue(n).getOutput();
	}

	/**
	 * Returns the input string in the n-th row of this CAS view. If the n-th
	 * cell has no output string, the input string of this cell is returned.
	 */
	public String getRowInputValue(int n) {
		return consoleTable.getCASTableCellValue(n).getTranslatedInput();
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
	public RowHeader getRowHeader(){
		return rowHeader;
	}

	

	public Application getApp() {
		return app;
	}

	public final boolean isUseGeoGebraVariableValues() {
		return useGeoGebraVariableValues;
	}

	public final void setUseGeoGebraVariableValues(
			boolean useGeoGebraVariableValues) {
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

	/**
	 * Defines new functions in the CAS
	 */
	public void add(GeoElement geo) {
		try {
			if (geo.isCasEvaluableObject()) {
				String funStr = geo.toGeoGebraCASString();
				getCAS().evaluateGeoGebraCAS(funStr);
			}
		} catch (Throwable e) {
			System.err.println("CASView.add: " + geo + ", " + e.getMessage());
		}
	}

	/**
	 * Removes function definitions from the CAS
	 */
	public void remove(GeoElement geo) {
		getCAS().unbindVariable(geo.getLabel());
	}

	/**
	 * Removes function definitions in the CAS
	 */
	public void update(GeoElement geo) {
		// check if update should be ignored
		if (ignoreUpdateVars.contains(geo.getLabel())) {
			// TODO: remove
			System.out.println("IGNORE update: " + geo.getLabel());
			return;
		}

		boolean updateHandled = false;
		int updateStartRow = 0;

		// TODO: remove
		//System.out.println("update: " + geo);

		// TODO: avoid updating loops, e.g.
		// c := Limit[ (3k+1)/k, k, Infinity ]

		// check if we have a cell with an assignment for geo
		CASTableCellValue cellValue = assignmentCellMap.get(geo.getLabel());
		if (cellValue != null && cellValue.isIndependent()) {
			int row = cellValue.getRow();
			updateStartRow = row + 1;

			// process row if geo is independent
			// set input of assignment row, e.g. a := 2;
			String assignmentStr = geo.toGeoGebraCASString();
			cellValue.setInput(assignmentStr);
			casInputHandler.processRow(row);
			consoleTable.repaint();
			
			updateHandled = true;
		}

		// update all dependent rows
		if (geo.isLabelSet())
			casInputHandler.processDependentRows(geo.getLabel(), updateStartRow);

		if (!updateHandled) {
			add(geo);
		}
	}

	void addToIgnoreUpdates(String var) {
		ignoreUpdateVars.add(var);
	}

	void removeFromIgnoreUpdates(String var) {
		ignoreUpdateVars.remove(var);
	}
	
	/**
	 * Handles toolbar mode changes
	 */
	public void setMode(int mode) {
		String command = kernel.getModeText(mode); // e.g. "Derivative"
		
		switch (mode) {		
			case EuclidianConstants.MODE_CAS_EVALUATE:
			case EuclidianConstants.MODE_CAS_NUMERIC:
			case EuclidianConstants.MODE_CAS_KEEP_INPUT:
			case EuclidianConstants.MODE_CAS_EXPAND:
			case EuclidianConstants.MODE_CAS_FACTOR:
			case EuclidianConstants.MODE_CAS_SUBSTITUTE:			
				// no parameters
				processInput(command, null);
				break;
			
			case EuclidianConstants.MODE_CAS_SOLVE:
			case EuclidianConstants.MODE_CAS_DERIVATIVE:
			case EuclidianConstants.MODE_CAS_INTEGRAL:
				// use first variable in expression as parameter
				processInput(command, new String[] {"%0"});
				break;
			default:
				// ignore other modes
		}				
	}

	/**
	 * Renames function definitions in the CAS
	 */
	public void rename(GeoElement geo) {
		// remove old function name from MathPiper
		getCAS().unbindVariable(geo.getOldLabel());

		// add new function name to MathPiper
		add(geo);
	}

	public void clearView() {
		cas.reset();

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

	public void repaintView() {
		consoleTable.updateAllRows();
		validate();
	}

	public void reset() {
		repaintView();
	}

	public void updateAuxiliaryObject(GeoElement geo) {
	}

	public void attachView() {
		//clearView();
		kernel.notifyAddAll(this);
		kernel.attach(this);
	}

	public void detachView() {
		kernel.detach(this);
		clearView();
	}

	public void setAssignment(String varLabel, CASTableCellValue cellValue) {
		assignmentCellMap.put(varLabel, cellValue);
	}
	
	public CASInputHandler getInputHandler()
	{
		return casInputHandler;
	}
}