package geogebra.cas.view;

import geogebra.cas.GeoGebraCAS;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.main.CasManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * A class which will give the view of the CAS
 */

/**
 * @author Quan Yuan
 * 
 */
public class CASView extends JComponent implements CasManager {
	
	private Kernel kernel;
	
	// TODO: add checkbox to set useGeoGebraVariableValues
	private boolean useGeoGebraVariableValues = true;

	
	private CASTable consoleTable;



	private Application app;

	private GeoGebraCAS cas;

	private final int numOfRows = 1;


	public CASView(Application app) {
		kernel = app.getKernel();
		this.app = app;
		
		// init cas
		cas = (geogebra.cas.GeoGebraCAS) kernel.getGeoGebraCAS();	
		Thread casInit = new Thread() {
			public void run() {
				cas.evaluateMathPiper("Simplify(1+1)");
			}
		};
		casInit.start();
		
		setLayout(new BorderLayout());
		
		// button panel
		JPanel btPanel = initButtons();		
		add(btPanel, BorderLayout.NORTH);
		
		// Ulven 01.03.09: excange line 90-97 with:
		// BtnPanel which sets up all the button.
		//add(geogebra.cas.view.components.BtnPanel.getInstance(this),BorderLayout.NORTH);
		
		// CAS input/output cells
		createCASTable();
		
		// row header
		final JList rowHeader = new RowHeader(consoleTable);		

		// init the scroll panel
		JScrollPane scrollPane = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setRowHeaderView(rowHeader);
		scrollPane.setViewportView(consoleTable);

		// put the scrollpanel in 
		add(scrollPane, BorderLayout.CENTER);
		this.setBackground(Color.WHITE);
		
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
		
		// start editing last row
		int lastRow = consoleTable.getRowCount() - 1;
		if (lastRow >= 0)
			consoleTable.startEditingRow(lastRow);
	}				
	
	private void createCASTable() {
		consoleTable = new CASTable(this, numOfRows);
		
		CASTableCellController inputListener = new CASTableCellController(this);
		consoleTable.getEditor().getInputArea().addKeyListener(inputListener);
		consoleTable.addKeyListener(inputListener);
			
		consoleTable.addKeyListener(new ConsoleTableKeyListener());
		
		TableCellMouseListener tableCellMouseListener = new TableCellMouseListener(consoleTable);
		consoleTable.addMouseListener(tableCellMouseListener);		
	}

	public static class CASListModel extends AbstractListModel {

		private static final long serialVersionUID = 1L;
		protected CASTableModel model;

		public CASListModel(CASTableModel model0) {
			model = model0;
		}

		public int getSize() {
			return model.getRowCount();
		}

		public Object getElementAt(int index) {
			return "" + (index + 1);
		}
	}


	// Key Listener for Console Table
	protected class ConsoleTableKeyListener implements KeyListener {

		public void keyTyped(KeyEvent e) {
			// System.out.println("Key typed on rowheader");
			e.consume();
		}

		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();

			boolean metaDown = Application.isControlDown(e);
			boolean altDown = e.isAltDown();

			// System.out.println("Key pressed on rowheader");
			// Application.debug(keyCode);
			switch (keyCode) {

			case KeyEvent.VK_DELETE: // delete
			case KeyEvent.VK_BACK_SPACE: // delete on MAC
				int[] delRows = consoleTable.getSelectedRows();
				int delRowsSize = delRows.length;
				int i = 0;
				while (i < delRowsSize) {
					int delRow = delRows[i];
					consoleTable.deleteRow(delRow - i);
					System.out.println("Key Delete row : " + delRow);
					i++;
				}
				
				System.out.println("Key Delete or BackSpace Action Performed ");
				break;
			default:
				e.consume();
			}
		}

		public void keyReleased(KeyEvent e) {
			// System.out.println("Key Released on rowheader");
			e.consume();
		}

	}

	

	public GeoGebraCAS getCAS() {
		return cas;
	}

	public CASTable getConsoleTable() {
		return consoleTable;
	}

	/**
	 * returns settings in XML format
	 */
	public String getGUIXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<casView>\n");

		int width = getWidth(); // getPreferredSize().width;
		int height = getHeight(); // getPreferredSize().height;

		// if (width > MIN_WIDTH && height > MIN_HEIGHT)
		{
			sb.append("\t<size ");
			sb.append(" width=\"");
			sb.append(width);
			sb.append("\"");
			sb.append(" height=\"");
			sb.append(height);
			sb.append("\"");
			sb.append("/>\n");
		}

		sb.append("</casView>\n");
		return sb.toString();
	}

	public String getSessionXML() {
	
		StringBuffer sb = new StringBuffer();
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
	 * If the n-th cell has no output string, the input string of this cell is returned.
	 */
	public String getRowValue(int n) {
		CASTableCellValue temp = consoleTable.getCASTableCellValue(n);
		
		String result = temp.getOutput();
		if (result == null || result.length() == 0)
			result = temp.getInput();
		
		return result;
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

	/**
	 * Loads
	 * 
	 * @param cellPairList
	 */
	public void initCellPairs(LinkedList cellPairList) {
		// Delete the current rows
		consoleTable.deleteAllRow();

		if (cellPairList == null) {
			CASTableCellValue cellPair = new CASTableCellValue(this);
			consoleTable.insertRowAfter(-1, cellPair);
		} else {
			Iterator it = cellPairList.iterator();
			boolean firstElementFlag = true;
			while (it.hasNext()) {
				CASTableCellValue cellPair = (CASTableCellValue) it.next();
				if (firstElementFlag) {
					consoleTable.insertRowAfter(-1, cellPair);
					firstElementFlag = false;
				} else
					consoleTable.insertRow(cellPair);
			}
		}

		// Set the focus at the right cell
		// table.setFocusAtRow(table.getRowCount() - 1,
		// geogebra.cas.view.CASPara.contCol);
	}

	public Object setInputExpression(Object cellValue, String input) {
		if (cellValue instanceof CASTableCellValue) {
			((CASTableCellValue) cellValue).setInput(input);
		}
		return cellValue;
	}

	public Object setOutputExpression(Object cellValue, String output) {
		if (cellValue instanceof CASTableCellValue) {
			((CASTableCellValue) cellValue).setOutput(output);
		}
		return cellValue;
	}

	public Object createCellValue() {
		CASTableCellValue cellValue = new CASTableCellValue(this);
		return cellValue;
	}

	//Ulven 01.03.09: Drop this, do it in components.BtnPanel
	private JPanel initButtons() {	
		ActionListener ac = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {					
				apply(ae.getActionCommand(), null);
			}	
		};
		
		JPanel btPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));	
		
		// evaluate
		JButton btEval = new JButton("=");
		btEval.setActionCommand("Eval");
		btEval.addActionListener(ac);
		btPanel.add(btEval);
		
		// hold
		JButton btHold = new JButton("\u2713");
		btHold.setActionCommand("Hold");
		btHold.addActionListener(ac);
		btPanel.add(btHold);
		
		// numeric
		JButton btNumeric = new JButton("\u2248");
		btNumeric.setActionCommand("Numeric");
		btNumeric.addActionListener(ac);
		btPanel.add(btNumeric);		
		
		// group terms
		JButton btGroup = new JButton(app.getPlain("GroupTerms"));
		btGroup.setActionCommand("Simplify");
		btGroup.addActionListener(ac);
		btPanel.add(btGroup);

		// expand
		JButton btExp = new JButton(app.getPlain("Expand"));
		btExp.setActionCommand("Expand");
		btExp.addActionListener(ac);
		btPanel.add(btExp);

		// factor
		JButton btFactor = new JButton(app.getPlain("Factor"));
		btFactor.setActionCommand("Factor");
		btFactor.addActionListener(ac);
		btPanel.add(btFactor);	
		
		// substitute
		JButton btSub = new JButton(app.getPlain("Substitute"));
		btSub.setActionCommand("Substitute");
		btSub.addActionListener(ac);
		btPanel.add(btSub);
	
						
		return btPanel;	
	}

	// Ulven 01.03.09:
	//Drop the whole ButtonListener, let buttons listen to themselves
	//Only needs an apply("Integrate",{"x","a","b"}) method
	//The Substitute command has to be handled another way, though...todo...
	
	/** Called from buttons and menues with for example:
	 *  "Integral", [par1, par2, ...]
	 *  Copied from apply(int mod)
	 */	
	public void apply(String ggbcmd,String[] params){
		// TODO: remove
		System.out.println(ggbcmd);
		
		// get editor and possibly selected text
		CASTableCellEditor cellEditor = consoleTable.getEditor();
		String selectedText = cellEditor == null ? null : cellEditor.getInputSelectedText();
		int selStart = cellEditor.getInputSelectionStart();
		int selEnd = cellEditor.getInputSelectionEnd();
		
		// TODO: remove
		System.out.println("selectedText: " + selectedText + ", selStart: " + selStart + ", selEnd: " + selEnd);					
	
		
		// save the edited value into the table model
		consoleTable.stopEditing();
		
		// get current row and input text		
		int selRow = consoleTable.getSelectedRow();			
		CASTableCellValue cellValue = consoleTable.getCASTableCellValue(selRow);
		String selRowInput = cellValue.getInput();		
		
		// break text into prefix, evalText, postfix
		String prefix, evalText, postfix;			
		boolean hasSelectedText = selectedText == null || selectedText.trim().length() == 0;
		if (hasSelectedText) {
			// no selected text: evaluate input using current cell
			prefix = "";
			evalText = selRowInput;
			postfix = "";		
		}
		else {
			// selected text: break it up into prefix, evalText, and postfix
			prefix = selRowInput.substring(0, selStart);
			evalText = selectedText;
			postfix = selRowInput.substring(selEnd);
		}
					
		// TODO: remove
		System.out.println("SELECTED ROW: " + selRow + ", prefix: " + prefix + ", evalText: " + evalText + ", postfix: " + postfix);					

		// handle action command string
		if (ggbcmd.equals("Substitute")) {
			// Create a CASSubDialog with the cell value
			CASSubDialog d = new CASSubDialog(CASView.this, prefix, evalText, postfix, selRow);
			d.setVisible(true);
			return;
		}
		else {
			// use action command as command for mathpiper
			evalText=ggbcmd+"["+evalText+"]";
		}
									
		// process evalText
		String evaluation = null;
		try {
			// evaluate
			evaluation = cas.processCASInput(evalText, useGeoGebraVariableValues);			
		} catch (Throwable th) {
			th.printStackTrace();
		}
		
		// Set the value into the table		
		if (evaluation != null)	{
			cellValue.setOutput(prefix + evaluation + postfix);
		} else {
			// 	error = app.getError("CAS.GeneralErrorMessage");
			cellValue.setOutput(cas.getMathPiperError(), true);			
		}
		
		consoleTable.updateRow(selRow);
		
		if (evaluation != null)	
			// start editing next row (may create a new row)
			consoleTable.startEditingRow(selRow + 1);
		else
			consoleTable.startEditingRow(selRow);
		
	}//apply(String,String[]
		

	public Application getApp() {
		return app;
	}
	
	public final boolean isUseGeoGebraVariableValues() {
		return useGeoGebraVariableValues;
	}

	public final void setUseGeoGebraVariableValues(boolean useGeoGebraVariableValues) {
		this.useGeoGebraVariableValues = useGeoGebraVariableValues;
	}


}