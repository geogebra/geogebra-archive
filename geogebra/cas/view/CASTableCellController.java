package geogebra.cas.view;

import geogebra.cas.GeoGebraCAS;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.SwingUtilities;

public class CASTableCellController implements KeyListener {
	
	private CASView view;
	private CASTable table;
	private CASTableCellEditor tableCellEditor;
	private Thread evalThread;
	
	public final String yacasErrorMsg = "CAS.GeneralErrorMessage";

	public CASTableCellController(CASView view) {		
		this.view = view;
		table = view.getConsoleTable();
		tableCellEditor = table.getEditor();		
	}

	public void keyPressed(KeyEvent e) {
		Object src = e.getSource();		
		if (src == tableCellEditor.getInputArea())
			handleKeyPressedInputTextField(e);

		if (src == table)
			handleKeyPressedTable(e);
	}

	private void handleKeyPressedTable(KeyEvent e) {
		e.consume();
		int selectedRow = table.getSelectedRow();

		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:			
			// Set the focus on the input text field
			table.setShowCellSeparator(false);		
			table.startEditingRow(selectedRow);						
			break;

		case KeyEvent.VK_DOWN:
			// go to next cell
			table.setShowCellSeparator(false);					
			table.startEditingRow(selectedRow + 1);								
			break;

		case KeyEvent.VK_ENTER:
			// Set the focus on the input text field
			table.setShowCellSeparator(false);
			table.insertRowAfter(selectedRow, null);
			break;

		default: // Other Keys
			// Set the focus on the input text field
			table.setShowCellSeparator(false);
		
			// put typed key into new cell 
			CASTableCellValue value = new CASTableCellValue(Character.toString(e.getKeyChar()));
			table.insertRowAfter(selectedRow, value);			
			break;
		}
	}

	private void handleKeyPressedInputTextField(final KeyEvent e) {
		boolean consumeEvent = false;
		
		int selectedRow = table.getSelectedRow();

		switch (e.getKeyCode()) {				
		case KeyEvent.VK_ENTER:
//			// evaluate input
//			evalThread = new Thread() {
//				public void run() {
//					view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));				
//					handleEnterKey(e);	
//					view.setCursor(Cursor.getDefaultCursor());
//					evalThread = null;
//				}				
//			};
//			SwingUtilities.invokeLater(evalThread);
			
			handleEnterKey(e);
			consumeEvent = true;
			break;

		case KeyEvent.VK_UP:
			if (selectedRow >= 1) {
				table.changeSelection(selectedRow - 1, CASTable.CONTENT_COLUMN, false, false);		
				table.scrollRectToVisible(table.getCellRect(selectedRow - 1, CASTable.CONTENT_COLUMN, true ) );
			} else if (table.isRowEmpty(0)) {
				// insert empty row at beginning
				table.insertRowAfter(-1, null);
			}			
			consumeEvent = true;
			break;
			
		case KeyEvent.VK_DOWN:
			if (selectedRow != view.getConsoleTable().getRowCount() - 1) {
				// table.setRowHeight(selectedRow, curCell.addLinePanel());
				table.setShowCellSeparator(true);
				table.updateRow(selectedRow);
			}
			consumeEvent = true;
			break;
		}

		// consume keyboard event so the table
		// does not process it again
		if (consumeEvent)
			e.consume();
	}
	
	/**
	 * Handles pressing of Enter key after user input.
	 * Enter checks the syntax of the input only. Shift+Enter evaluates the input 
	 */
	private synchronized void handleEnterKey(KeyEvent e) {
		table.stopEditing();
		String inputText = table.getCASTableCellValue(table.getSelectedRow()).getInput();
		if (inputText == null || inputText.length() == 0) return;				
				
		// Get the input from the user interface
		int selectedRow = table.getSelectedRow();					
		CASTableCellValue curValue = table.getCASTableCellValue(selectedRow);				
		GeoGebraCAS ggbCAS = view.getCAS();
		
		// process input
		String evaluation = null;
		String error = null;
		try {
			// Enter: evaluate
			// Shift + Enter: do not evaluate, only check syntax of input
			boolean evaluate = !e.isShiftDown();
			
			// provess input string
			evaluation = ggbCAS.processCASInput(inputText, evaluate, view.isUseGeoGebraVariableValues());
			
			if (evaluation == null)
				error = ggbCAS.getMathPiperError();
			
		} catch (Throwable th) {
			error = view.getApp().getError(yacasErrorMsg);
			th.printStackTrace();
		}
		

		// Set the value into the table		
		if (evaluation != null)	{
			curValue.setOutput(evaluation);
		} else {
			curValue.setOutput(error, true);			
		}
		
		table.updateRow(selectedRow);
		
		table.startEditingRow(selectedRow + 1);
	}
	

		


	public void keyReleased(KeyEvent arg0) {
	
	}

	public void keyTyped(KeyEvent arg0) {
	
	}

}
