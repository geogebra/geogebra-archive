package geogebra.cas.view;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
		if (!(src instanceof JTextField))
			return;

		boolean consumeEvent = false;

		int selectedRow = view.getConsoleTable().getSelectedRow();
		int selectedCol = view.getConsoleTable().getSelectedColumn();
		CASTableModel tableModel = (CASTableModel) view.getConsoleTable()
				.getModel();
		CASTableCellEditor ccurCell = (CASTableCellEditor) view
				.getConsoleTable().getCellEditor(selectedRow, selectedCol);

		switch (e.getKeyCode()) {
		case KeyEvent.VK_ENTER:
			if (curCell.isLineHighlighted()) {
				// Insert a new line here, and set the focus on the new line
				CASTableCellValue newValue = new CASTableCellValue();
				// Here it has to be selectedRow+1. Otherwise there is a bug
				// because of celleditor: Everytime the stopediting is fired, a
				// value is stored into the Jtable. Otherwise, render has
				// nothing to show.
				tableModel.insertRow(selectedRow + 1, new Object[] { "New",
						newValue });
				// tableModel.fireTableCellUpdated(newRow, CASPara.contCol);
				//System.out.println("newValue = " + newValue.getOutputAreaInclude());

				curCell.getConsoleTable().setRowHeight(selectedRow,
						curCell.setLineUnHighlighted());
				// System.out.println("Set the line UNhighlighted");
				curCell.setInputAreaFocus();
			} else {
				JTextField ta = (JTextField) src;
				// Get the input of the user
				String inputText = (ta.getText().substring(2)).trim();
				// Evaluate the input with Yacas, which is too slow
				String evaluation = view.getCAS().evaluateYACAS(inputText);
				curCell.setInput(inputText);
				curCell.setOutput(evaluation);
				// We enlarge the height of the selected row

				curCell.getConsoleTable().setRowHeight(selectedRow,
						CASPara.inputOutputHeight);
				curCell.addOutputArea();
				CASTableCellValue newValue = new CASTableCellValue(inputText,
						evaluation);
				newValue.setOutputAreaInclude(true);
				tableModel.setValueAt(newValue, selectedRow);

				// update the cell appearance
				SwingUtilities.updateComponentTreeUI(curCell);
				curCell.setInputAreaFocus();
			}
			consumeEvent = true;
			break;

		case KeyEvent.VK_UP:
			//System.out.println("Focus should be set at the line above");
			if (!curCell.isLineHighlighted()) {
				// Set Line of the previous row Highlighted;
				// Set the focus on the line;
				// curCell.setLineHighlighted();

			} else {// Set the focus on the input text field
				curCell.getConsoleTable().setRowHeight(selectedRow,
						curCell.setLineUnHighlighted());
				curCell.setInputAreaFocus();
			}
			consumeEvent = true;
			break;

		case KeyEvent.VK_DOWN:
			if (!curCell.isLineHighlighted()) {
				curCell.setLineHighlighted();
				curCell.getConsoleTable().setRowHeight(selectedRow,
						curCell.addBBorder());
				SwingUtilities.updateComponentTreeUI(curCell);
				// curCell.setBBorderFocus();
			} else {// Set the focus on the input text field of the next row

			}
			consumeEvent = true;
			break;

		default:
			if (curCell.isLineHighlighted())
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
