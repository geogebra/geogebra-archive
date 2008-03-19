package geogebra.cas.view;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class CASTableCellController implements KeyListener {

	private CASTableCell curCell;

	private CASView view;

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

		JTable table = view.getConsoleTable();
		CASTableModel tableModel = (CASTableModel) table.getModel();
		int selectedRow = table.getSelectedRow();
		int selectedCol = table.getSelectedColumn();

		CASTableCellValue curValue = (CASTableCellValue) tableModel.getValueAt(
				selectedRow, selectedCol);

		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			// System.out.println("Focus should be set at the line above");
			if (curCell.isLineVisiable()) {
				// Set the focus on the input text field
				table.setRowHeight(selectedRow, curCell.setLineInvisiable());
				curCell.setInputAreaFocused();
			}
			consumeEvent = true;
			break;

		case KeyEvent.VK_DOWN:
			if (curCell.isLineVisiable()) {// Set the focus on the input
				// Set the value into the table before the focus go to the next
				// row
				String inputText = curCell.getInput();
				curValue.setCommand(inputText);
				curCell.setInput(inputText);

				table.setRowHeight(selectedRow, curCell.setLineInvisiable());
				// table.editCellAt(selectedRow+1, selectedCol);
				System.out.println("Key donw changed selection: "
						+ table.getSelectedRow() + " "
						+ table.getSelectedColumn());

				System.out.println("editCell: " + table.getEditingRow()
						+ table.getEditingColumn() + table.getRowCount());

				if (selectedRow < (table.getRowCount() - 1)) {
					CASTableCellValue value = (CASTableCellValue) tableModel
							.getValueAt(selectedRow + 1, selectedCol);

					table.changeSelection(selectedRow + 1, selectedCol, false,
							false);
					table.editCellAt(selectedRow + 1, selectedCol);
					((Component) ((CASTableCellEditor) table.getCellEditor(
							selectedRow + 1, selectedCol))
							.getTableCellEditorComponent(table, value, true,
									selectedRow + 1, selectedCol))
							.requestFocus();

				} else { // Insert a new row
					CASTableCellValue newValue = new CASTableCellValue();
					tableModel.insertRow(selectedRow + 1, new Object[] { "New",
							newValue });
					table
							.setRowHeight(selectedRow, curCell
									.setLineInvisiable());
					table.changeSelection(selectedRow + 1, selectedCol, false,
							false);
					table.editCellAt(selectedRow + 1, selectedCol);
					((Component) ((CASTableCellEditor) table.getCellEditor(
							selectedRow + 1, selectedCol))
							.getTableCellEditorComponent(table, newValue, true,
									selectedRow + 1, selectedCol))
							.requestFocus();
				}
			}

			consumeEvent = true;
			break;

		default:
			System.out.println("Press Enter at the Line Panel");
			if (curCell.isLineVisiable()) {
				// Insert a new line here
				CASTableCellValue newValue = new CASTableCellValue();
				// Here it has to be selectedRow+1. Otherwise there is a bug
				// because of celleditor: Everytime the stopediting is fired, a
				// value is stored into the Jtable. Otherwise, render has
				// nothing to show.
				tableModel.insertRow(selectedRow + 1, new Object[] { "New",
						newValue });
				// System.out.println("Set the line UNhighlighted");
				table.setRowHeight(selectedRow, curCell.setLineInvisiable());

				// set the focus on the new line
				table.changeSelection(selectedRow + 1, selectedCol, false,
						false);
				table.editCellAt(selectedRow + 1, selectedCol);
				((Component) ((CASTableCellEditor) table.getCellEditor(
						selectedRow + 1, selectedCol))
						.getTableCellEditorComponent(table, newValue, true,
								selectedRow + 1, selectedCol)).requestFocus();
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

		JTable table = view.getConsoleTable();
		CASTableModel tableModel = (CASTableModel) table.getModel();
		int selectedRow = table.getSelectedRow();
		int selectedCol = table.getSelectedColumn();
		CASTableCellValue curValue = (CASTableCellValue) tableModel.getValueAt(
				selectedRow, selectedCol);

		switch (e.getKeyCode()) {
		case KeyEvent.VK_ENTER:
			// Get the input from the user interface
			String inputText = curCell.getInput();
			// Evaluate the input with Yacas, which is too slow
			String evaluation = view.getCAS().evaluateYACAS(inputText);

			// Set the value into the table
			curValue.setCommand(inputText);
			curValue.setOutput(evaluation);
			curCell.setInput(inputText);
			curCell.setOutput(evaluation);

			// We enlarge the height of the selected row
			table.setRowHeight(selectedRow, CASPara.inputOutputHeight);
			// CASTableCellValue newValue = new CASTableCellValue(inputText,
			// evaluation);
			curValue.setOutputAreaInclude(true);
			// tableModel.setValueAt(curValue, selectedRow, CASPara.contCol);
			table.setValueAt(curValue, selectedRow, CASPara.contCol);

			CASTableCellValue newValue = (CASTableCellValue) tableModel
					.getValueAt(selectedRow, selectedCol);
			System.out.println(selectedRow + " Value Updated: "
					+ newValue.getCommand() + newValue.getOutput());

			// update the cell appearance
			SwingUtilities.updateComponentTreeUI(curCell);
			// table.repaint();
			curCell.setInputAreaFocused();

			consumeEvent = true;
			break;

		case KeyEvent.VK_UP:
			// System.out.println("Focus should be set at the line above");
			if (!curCell.isLineVisiable()) {
				curValue.setCommand(curCell.getInput());
				curCell.setInput(curCell.getInput());
				
				// Set Line of the previous row Highlighted;
				// Set the focus on the line;
				// curCell.setLineHighlighted();
				if (selectedRow >= 1) {
					CASTableCellValue value = (CASTableCellValue) tableModel
							.getValueAt(selectedRow - 1, selectedCol);

					table.changeSelection(selectedRow - 1, selectedCol, false,
							false);
					table.editCellAt(selectedRow - 1, selectedCol);
					CASTableCell temp = ((CASTableCell) ((CASTableCellEditor) table
							.getCellEditor(selectedRow - 1, selectedCol))
							.getTableCellEditorComponent(table, value, true,
									selectedRow - 1, selectedCol));
					table.setRowHeight(selectedRow - 1, temp.addLinePanel());
					SwingUtilities.updateComponentTreeUI(temp);
					temp.setLineBorderFocus();

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

	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
