package geogebra.cas.view;

import geogebra.cas.GeoGebraCAS;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.main.CasManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

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

	private JButton btSub, btEval, btExp, btFactor;

	private final int numOfRows = 1;
	
	private static final int SUB_Flag = 0;
	private static final int EVAL_Flag = 1;
	private static final int EXP_Flag = 2;
	private static final int FAC_Flag = 3;

	public CASView(Application app) {
		kernel = app.getKernel();
		this.app = app;
		
		// init cas
		cas = (geogebra.cas.GeoGebraCAS) kernel.getGeoGebraCAS();			
		
		setLayout(new BorderLayout());
		
		// button panel
		initButtons();
		JPanel btPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		btPanel.add(btEval);
		btPanel.add(btExp);
		btPanel.add(btFactor);
		btPanel.add(btSub);	
		//add(btPanel, BorderLayout.NORTH);
		
		// Ulven 01.03.09: excange line 90-97 with:
		// BtnPanel which sets up all the button.
		add(geogebra.cas.view.components.BtnPanel.getInstance(this),BorderLayout.NORTH);
		
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
	}				
	
	private void createCASTable() {
		consoleTable = new CASTable(this, numOfRows);
		
		CASTableCellController inputListener = new CASTableCellController(this);
		consoleTable.getEditor().getInputArea().addKeyListener(inputListener);
		consoleTable.addKeyListener(inputListener);
			
		consoleTable.addKeyListener(new ConsoleTableKeyListener());
		CASMouseController casMouseCtrl = new CASMouseController(consoleTable);
		consoleTable.addMouseListener(casMouseCtrl);
		

		
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
			CASTableCellValue cellPair = new CASTableCellValue();
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
		CASTableCellValue cellValue = new CASTableCellValue();
		return cellValue;
	}

	//Ulven 01.03.09: Drop this, do it in components.BtnPanel
	private void initButtons() {
		JButton ret = null;
		
		ButtonListener btListener = new ButtonListener();
		
		// evaluate
		btEval = new JButton("=");
		btEval.setActionCommand("Simplify");
		btEval.addActionListener(btListener);

		// expand
		btExp = new JButton(app.getPlain("Expand"));
		btExp.setActionCommand("Expand");
		btExp.addActionListener(btListener);

		// factor
		btFactor = new JButton("Factor");
		btFactor.setActionCommand("Factor");
		btFactor.addActionListener(btListener);
		
		// substitute
		btSub = new JButton(app.getPlain("Substitute"));
		btSub.setActionCommand("Subsim");
		btSub.addActionListener(btListener);

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
		
		// always use same cell for output
		CASTableCellValue outputCellValue = cellValue;
		
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

		/* ToDo:
		switch (mod) {
			case SUB_Flag:
				// Create a CASSubDialog with the cell value
				CASSubDialog d = new CASSubDialog(CASView.this, cellValue, selectedText, selRow);
				d.setVisible(true);
				return;
		 */	
		evalText=ggbcmd+"["+evalText+"]";
		
		// process evalText
		String result;
		boolean error;
		try {
			evalText = cas.processCASInput(evalText, true, useGeoGebraVariableValues);
			result = prefix + evalText + postfix;	
			error = false;
		} catch (Throwable e) {					
			e.printStackTrace();	
			result = cas.getMathPiperError();
			error = true;
		}
								
			
//		if (hasSelectedText || error) {
//			outputCellValue.setInput(selRowInput);						
//			outputCellValue.setOutput(result, error);
//			consoleTable.updateRow(selRow);
//			consoleTable.startEditingRow(selRow);
//		} else {				
//			outputCellValue.setInput(result);										
//			consoleTable.insertRowAfter(selRow, outputCellValue);				
//		}		
		
		// update output cell	
		outputCellValue.setInput(selRowInput);						
		outputCellValue.setOutput(result, error);
		consoleTable.updateRow(selRow);
		//consoleTable.startEditingRow(selRow);
		
		// create new empty row
		consoleTable.insertRowAfter(selRow, null);
		
	}//apply(String,String[]
	
	protected class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent ae) {
			
			
			Object src = ae.getSource();
			if (src == btSub) {
				apply(SUB_Flag);
			} else if (src == btEval) {
				apply(EVAL_Flag);
			} else if (src == btExp) {
				apply(EXP_Flag);
			} else if (src == btFactor) {
				apply(FAC_Flag);
			}

		}
		
		private void apply(int mod) {				
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
			
			// always use same cell for output
			CASTableCellValue outputCellValue = cellValue;
			
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
	
			switch (mod) {
				case SUB_Flag:
					// Create a CASSubDialog with the cell value
					CASSubDialog d = new CASSubDialog(CASView.this, cellValue, selectedText, selRow);
					d.setVisible(true);
					return;
					
				case EVAL_Flag:	
					evalText = "Simplify[" + evalText + "]";				
					break;
					
				case EXP_Flag:
					evalText = "Expand[" + evalText + "]";	
					break;
					
				case FAC_Flag:
					evalText = "Factor[" + evalText + "]";	
					break;
			}
			
			// process evalText
			String result;
			boolean error;
			try {
				evalText = cas.processCASInput(evalText, true, useGeoGebraVariableValues);
				result = prefix + evalText + postfix;	
				error = false;
			} catch (Throwable e) {					
				e.printStackTrace();	
				result = cas.getMathPiperError();
				error = true;
			}
									
				
//			if (hasSelectedText || error) {
//				outputCellValue.setInput(selRowInput);						
//				outputCellValue.setOutput(result, error);
//				consoleTable.updateRow(selRow);
//				consoleTable.startEditingRow(selRow);
//			} else {				
//				outputCellValue.setInput(result);										
//				consoleTable.insertRowAfter(selRow, outputCellValue);				
//			}		
			
			// update output cell	
			outputCellValue.setInput(selRowInput);						
			outputCellValue.setOutput(result, error);
			consoleTable.updateRow(selRow);
			//consoleTable.startEditingRow(selRow);
			
			// create new empty row
			consoleTable.insertRowAfter(selRow, null);
		}

	}

	public JButton getBtSub() {
		return btSub;
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


}