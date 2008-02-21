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
		// System.out.println("Inside the cell");
		// System.out.println(src.getClass().getName());

		switch (e.getKeyCode()) {
		case KeyEvent.VK_ENTER:
			if (curCell.isLineHighlighted()) {
				// Insert a new line here, and set the focus on the new line
				int selectedRow = view.getConsoleTable().getSelectedRow();
				CASTableCellValue value = new CASTableCellValue();
				((CASTableModel) view.getConsoleTable().getModel()).insertRow(
						(selectedRow >= 0 ? selectedRow : 0), new Object[] {
								"New", value });
				// System.out.println("New Input = " + value.getCommand());
				curCell.setLineUnHighlighted();
				curCell.setInputFoucs();
			} else {
				JTextField ta = (JTextField) src;
				// Get the input of the user
				String inputText = (ta.getText().substring(2)).trim();
				// Evaluate the input with Yacas, which is too slow
				String evaluation = view.getCAS().evaluateYACAS(inputText);
				// String evaluation =
				// view.getCAS().evaluateJASYMCA(inputText);
				// show message box
				// StringBuffer sb = new StringBuffer();
				// sb.append("in: ");
				// sb.append(inputText);
				// sb.append("\nout: ");
				// sb.append(evaluation);
				// JOptionPane.showMessageDialog(view, sb.toString());
				curCell.setInput(inputText);
				curCell.setOutput(evaluation);
				// We enlarge the height of the selected row
				int selectedRow = curCell.getConsoleTable().getSelectedRow();
				// curCell.addOutputArea();
				CASTableCellValue newValue = new CASTableCellValue(inputText,
						evaluation);
				newValue.setOutputAreaInclude(true);
				curCell.getConsoleTable().setRowHeight(selectedRow,
						CASPara.reactiveHeight);
				((CASTableModel) curCell.getConsoleTable().getModel())
						.setValueAt(newValue, selectedRow);
				// update the cell appearance
				SwingUtilities.updateComponentTreeUI(curCell);
				
				// Set the cursor
				// curCell.getConsoleTable().changeSelection(selectedRow, 1,
				// true, true);

				// Object t=
				// ((CASTableModel)curCell.getConsoleTable().getModel()).getValueAt(selectedRow);
				// System.out.println("Out: " +
				// ((CASTableCellValue)t).getOutput());
			}
			consumeEvent = true;
			break;

		case KeyEvent.VK_UP:
			System.out.println("Focus should be set at the line above");
			if (!curCell.isLineHighlighted()) {
				// Set Line of the previous row Highlighted;
				// Set the focus on the line;
				// curCell.setLineHighlighted();

			} else {// Set the focus on the input text field

			}
			consumeEvent = true;
			break;

		case KeyEvent.VK_DOWN:
			if (!curCell.isLineHighlighted()) {
				// Show the line;
				// Set Line Highlighted;
				// Set the focus on the line;
				System.out.println("Set the line highlighted");
				curCell.setLineHighlighted();
				curCell.setLineFoucs();

			} else {// Set the focus on the input text field of the next row

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
