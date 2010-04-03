package geogebra.cas.view;

import geogebra.main.Application;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CASTableCellController implements KeyListener {
	
	private CASView view;
	private CASTable table;
	private CASTableCellEditor tableCellEditor;
	private Thread evalThread;

	public CASTableCellController(CASView view) {		
		this.view = view;
		table = view.getConsoleTable();
		tableCellEditor = table.getEditor();		
	}

	public void keyPressed(KeyEvent e) {
		Object src = e.getSource();		
		if (src == tableCellEditor.getInputArea())
			handleKeyPressedInputTextField(e);
	}

	private void handleKeyPressedInputTextField(final KeyEvent e) {
		boolean consumeEvent = false;
		boolean needUndo = false;
		
		int selectedRow = table.getSelectedRow();
		int rowCount = table.getRowCount();

		switch (e.getKeyCode()) {				
		case KeyEvent.VK_ENTER:
			handleEnterKey(e);
			consumeEvent = true;
			needUndo = true;
			break;

		case KeyEvent.VK_UP:
			if (selectedRow >= 1) {
				table.startEditingRow(selectedRow - 1);
			} 
			else if (table.isRowEmpty(0)) {
				// insert empty row at beginning
				table.insertRowAfter(-1, null, true);
				needUndo = true;
			}			
			consumeEvent = true;
			break;
			
		case KeyEvent.VK_DOWN:
			if (selectedRow != rowCount - 1) {
				table.startEditingRow(selectedRow + 1);
			} 
			else {
				// insert empty row at end
				table.insertRow(null, true);
				needUndo = true;
			}	
			consumeEvent = true;
			break;
		}

		// consume keyboard event so the table
		// does not process it again
		if (consumeEvent) {
			e.consume();
		}
		
		if (needUndo) {
			// store undo info
			view.getApp().storeUndoInfo();
		}
	}
	
	/**
	 * Handles pressing of Enter key after user input.
	 * Enter checks the syntax of the input only. Shift+Enter evaluates the input 
	 */
	private synchronized void handleEnterKey(KeyEvent e) {
		if (Application.isControlDown(e)) {
			// don't evaluate, only parse by GeoGebra
			view.processInput("CheckInput", null);	
		} else {
			// evaluate
			view.processInput("Evaluate", null);
		}		
	}

	public void keyReleased(KeyEvent arg0) {
	
	}

	public void keyTyped(KeyEvent arg0) {
	
	}

}
