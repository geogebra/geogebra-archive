package geogebra.cas.view;

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
				table.changeSelection(selectedRow - 1, CASTable.COL_CAS_CELLS, false, false);		
				table.scrollRectToVisible(table.getCellRect(selectedRow - 1, CASTable.COL_CAS_CELLS, true ) );
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
		if (e.isShiftDown()) {
			// Hold
			view.apply("Hold", null);	
		} else {
			// Eval
			view.apply("Eval", null);
		}		
	}
	

		


	public void keyReleased(KeyEvent arg0) {
	
	}

	public void keyTyped(KeyEvent arg0) {
	
	}

}
