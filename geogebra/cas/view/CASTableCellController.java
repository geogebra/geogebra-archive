package geogebra.cas.view;

import geogebra.Application;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.SwingUtilities;

public class CASTableCellController implements KeyListener {

	private CASTableCell curCell;

	private CASView view;

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
				table.setRowHeight(selectedRow, curCell.setLineInvisiable());
				curCell.setInputAreaFocused();
			}
			consumeEvent = true;
			break;

		case KeyEvent.VK_DOWN:
			if (curCell.isLineVisiable()) {// Set the focus on the input
				saveInput(curValue);

				table.setRowHeight(selectedRow, curCell.setLineInvisiable());
				// table.editCellAt(selectedRow+1, selectedCol);
				Application.debug("Key donw changed selection: "
						+ table.getSelectedRow() + " "
						+ table.getSelectedColumn());

				if (selectedRow < (table.getRowCount() - 1)) {
					table.setFocusAtRow(selectedRow + 1, selectedCol);

				} else {
					// Insert a new row
					table
							.setRowHeight(selectedRow, curCell
									.setLineInvisiable());
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
				table.setRowHeight(selectedRow, curCell.setLineInvisiable());
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
				table.setRowHeight(selectedRow, curCell.setLineInvisiable());
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

	private void handleKeyPressedInputTextField(KeyEvent e) {

		boolean consumeEvent = false;

		CASTable table = view.getConsoleTable();
		CASTableModel tableModel = (CASTableModel) table.getModel();
		int selectedRow = table.getSelectedRow();
		int selectedCol = CASPara.contCol; // table.getSelectedColumn();
		CASTableCellValue curValue = (CASTableCellValue) tableModel.getValueAt(
				selectedRow, selectedCol);

		switch (e.getKeyCode()) {
		case KeyEvent.VK_ENTER:
			// Get the input from the user interface
			String inputText = curCell.getInput();

			if (inputText.length() != 0) {
				// Evaluate the input with Yacas, which is too slow
				String evaluation = view.getCAS().evaluateYACAS(inputText);

				// Error message check
				if (inputText.compareTo("") != 0
						&& evaluation.compareTo("") == 0)
					evaluation = view.getApp().getError(this.yacasErrorMsg);

				// Set the value into the table
				saveInput(curValue);
				curValue.setOutput(evaluation);
				curCell.setOutput(evaluation);

				// We enlarge the height of the selected row
				table.setRowHeight(selectedRow, CASPara.inputOutputHeight);
				curValue.setOutputAreaInclude(true);
				// tableModel.setValueAt(curValue, selectedRow,
				// CASPara.contCol);
				table.setValueAt(curValue, selectedRow, CASPara.contCol);

				CASTableCellValue newValue = (CASTableCellValue) tableModel
						.getValueAt(selectedRow, selectedCol);
				System.out.println(selectedRow + " Value Updated: "
						+ newValue.getCommand() + newValue.getOutput());

				// update the cell appearance
				SwingUtilities.updateComponentTreeUI(curCell);

				if (selectedRow < (table.getRowCount() - 1)) {
					table.setFocusAtRow(selectedRow + 1, selectedCol);
				} else {
					// Insert a new row
					table
							.setRowHeight(selectedRow, curCell
									.setLineInvisiable());
					table.insertRow(selectedRow, selectedCol, null);
				}
			}

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
				table.setRowHeight(selectedRow, curCell.addLinePanel());
				SwingUtilities.updateComponentTreeUI(curCell);
				curCell.setLineBorderFocus();
			}
			consumeEvent = true;
			break;
		}

		// consume keyboard event so the table
		// does not process it again
		if (consumeEvent)
			e.consume();
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
