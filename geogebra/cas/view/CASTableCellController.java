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

	private CASTableCell curCell;
	private CASView view;
	private Thread evalThread;
	
	

	public final String yacasErrorMsg = "CAS.GeneralErrorMessage";

	public CASTableCellController(CASTableCell cell, CASView view) {
		this.curCell = cell;
		this.view = view;
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		Object src = e.getSource();
		if (src == curCell.getInputArea())
			handleKeyPressedInputTextField(e);

		if (src == curCell.getLinePanel())
			handleKeyPressedLinePanel(e);
	}

	private void handleKeyPressedLinePanel(KeyEvent e) {

		boolean consumeEvent = false;

		CASTable table = view.getConsoleTable();
		CASTableModel tableModel = (CASTableModel) table.getModel();
		int selectedRow = table.getSelectedRow();
		int selectedCol = CASPara.contCol; // table.getSelectedColumn();

		CASTableCellValue curValue = (CASTableCellValue) tableModel.getValueAt(
				selectedRow, selectedCol);

		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			// Application.debug("Focus should be set at the line above");
			if (curCell.isLineVisiable()) {
				// Set the focus on the input text field
				// table.setRowHeight(selectedRow, curCell.setLineInvisiable());
				curCell.setLineInvisiable();
				curCell.setInputAreaFocused();
			}
			consumeEvent = true;
			break;

		case KeyEvent.VK_DOWN:
			if (curCell.isLineVisiable()) {// Set the focus on the input
				saveInput(curValue);

				// table.setRowHeight(selectedRow, curCell.setLineInvisiable());
				curCell.setLineInvisiable();
				// table.editCellAt(selectedRow+1, selectedCol);
				Application.debug("Key donw changed selection: "
						+ table.getSelectedRow() + " "
						+ table.getSelectedColumn());

				if (selectedRow < (table.getRowCount() - 1)) {
					table.setFocusAtRow(selectedRow + 1, selectedCol);

				} else {
					// Insert a new row
					// table
					// .setRowHeight(selectedRow, curCell
					// .setLineInvisiable());
					curCell.setLineInvisiable();
					if (curCell.getInput().length() != 0)
						table.insertRow(selectedRow, selectedCol, null);
					else
						curCell.setInputAreaFocused();
				}
			}

			consumeEvent = true;
			break;

		case KeyEvent.VK_ENTER:
			Application.debug("Press Enter at the Line Panel");
			if (curCell.isLineVisiable()) {
				saveInput(curValue);
				// Insert a new line here
				// table.setRowHeight(selectedRow, curCell.setLineInvisiable());
				curCell.setLineInvisiable();
				if (curCell.getInput().length() != 0)
					table.insertRow(selectedRow, selectedCol, null);
				else
					curCell.setInputAreaFocused();
			}
			consumeEvent = true;
			break;

		default: // Other Keys
			Application.debug("Press Enter at the Line Panel");
			if (curCell.isLineVisiable()) {
				saveInput(curValue);
				// Insert a new line here
				// table.setRowHeight(selectedRow, curCell.setLineInvisiable());
				curCell.setLineInvisiable();
				if (curCell.getInput().length() != 0)
					table.insertRow(selectedRow, selectedCol, e.getKeyChar());
				else
					curCell.setInputAreaFocused();
			}
			consumeEvent = true;
			break;
		}

		// consume keyboard event so the table
		// does not process it again
		if (consumeEvent)
			e.consume();
	}

	private void handleKeyPressedInputTextField(final KeyEvent e) {

		boolean consumeEvent = false;

		CASTable table = view.getConsoleTable();
		CASTableModel tableModel = (CASTableModel) table.getModel();
		int selectedRow = table.getSelectedRow();
		int selectedCol = CASPara.contCol; // table.getSelectedColumn();
		CASTableCellValue curValue = (CASTableCellValue) tableModel.getValueAt(
				selectedRow, selectedCol);

		switch (e.getKeyCode()) {				
		case KeyEvent.VK_ENTER:
			// evaluate input
			evalThread = new Thread() {
				public void run() {
					view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));				
					handleEnterKey(e);	
					view.setCursor(Cursor.getDefaultCursor());
					evalThread = null;
				}				
			};
			evalThread.start();	
						
			consumeEvent = true;
			break;

		case KeyEvent.VK_UP:
			// Application.debug("Focus should be set at the line above");
			if (!curCell.isLineVisiable()) {
				saveInput(curValue);

				if (selectedRow >= 1) {
					table.setFocusAtRowLinePanel(selectedRow - 1, selectedCol);
				} else { // If the focus is on the first row & that row is
					// empty, we create a new row
					if (curCell.getInput().length() != 0)
						table.insertRow(-1, CASPara.contCol, null);
					System.out.println("This is the first row: " + selectedRow);
				}
			}
			consumeEvent = true;
			break;
			
		case KeyEvent.VK_DOWN:
			if (!curCell.isLineVisiable()) {
				if (selectedRow != view.getConsoleTable().getRowCount() - 1) {
					// table.setRowHeight(selectedRow, curCell.addLinePanel());
					curCell.addLine();
					SwingUtilities.updateComponentTreeUI(curCell);
					curCell.setLineBorderFocus();
				}
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
		// Get the input from the user interface
		String inputText = curCell.getInput();
		if (inputText == null || inputText.length() == 0) return;
		
		// get current cell value
		CASTable table = view.getConsoleTable();
		CASTableModel tableModel = (CASTableModel) table.getModel();
		int selectedRow = table.getSelectedRow();
		int selectedCol = CASPara.contCol; // table.getSelectedColumn();
		CASTableCellValue curValue = (CASTableCellValue) tableModel.getValueAt(
				selectedRow, selectedCol);
		GeoGebraCAS ggbCAS = view.getCAS();

		// process input
		String evaluation = null;
		String error = null;
		try {
			// PARSE input
			ExpressionValue ev = ggbCAS.parseInput(inputText);
			
			// convert parsed input to Yacas string
			String yacasString = ggbCAS.toYacasString(ev, view.isUseGeoGebraVariableValues());
			
			// EVALUATE input in Yacas depending on key combination
			if (e.isShiftDown()) {
				evaluation = ggbCAS.evaluateYACAS("Simplify", yacasString);
			}
			else {
				evaluation = ggbCAS.evaluateYACAS("Hold", yacasString);
			}
			
			if (evaluation == null)
				error = ggbCAS.getYACASError();
			
		} catch (Throwable th) {
			error = view.getApp().getError(this.yacasErrorMsg);
		}
		

		// Set the value into the table
		saveInput(curValue);
		
		if (evaluation != null)	{
			curValue.setOutput(evaluation);
			curCell.setOutput(evaluation);
		} else {
			curValue.setOutput(error);
			curCell.setOutput(error);
			curCell.setForeground(Color.red);
		}

		// We enlarge the height of the selected row
		table.setRowHeight(selectedRow, CASPara.inputOutputHeight);
		curValue.setOutputAreaInclude(true);
		table.setValueAt(curValue, selectedRow, CASPara.contCol);

		CASTableCellValue newValue = (CASTableCellValue) tableModel
				.getValueAt(selectedRow, selectedCol);
		
		// TODO: remove
		System.out.println(selectedRow + " Value Updated: "
				+ newValue.getCommand() + newValue.getOutput());

		// update the cell appearance
		//SwingUtilities.updateComponentTreeUI(curCell);
		curCell.validate();

		if (selectedRow < (table.getRowCount() - 1)) {
			table.setFocusAtRow(selectedRow + 1, selectedCol);
		} else {
			// Insert a new row
			// table
			// .setRowHeight(selectedRow, curCell
			// .setLineInvisiable());
			curCell.setLineInvisiable();
			table.insertRow(selectedRow, selectedCol, null);
		}
	}
		

	/*
	 * Function: Save the input value from the view into the table
	 */
	public void saveInput(CASTableCellValue curValue) {
		String inputText = curCell.getInput();
		curValue.setCommand(inputText);
		curCell.setInput(inputText);
	}

	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
