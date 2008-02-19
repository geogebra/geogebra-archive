package geogebra.cas.view;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

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
		// System.out.println("Inside the cell");
		// System.out.println(src.getClass().getName());

		if (src instanceof JTextField) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (curCell.isLineHighlighted()) {
					//Insert a new line here, and set the focus on the new line
					int selectedRow = curCell.getConsoleTable()
							.getSelectedRow();
					CASTableCellValue value = new CASTableCellValue();
					((CASTableModel) view.getConsoleTable().getModel())
							.insertRow((selectedRow >= 0 ? selectedRow : 0),
									new Object[] { "New", value });
					
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
					int selectedRow = curCell.getConsoleTable()
							.getSelectedRow();
					curCell.getConsoleTable().setRowHeight(selectedRow, 45);
					((CASTableModel) curCell.getConsoleTable().getModel())
							.setValueAt(new CASTableCellValue(inputText,
									evaluation), selectedRow);
					// Set the cursor
					// curCell.getConsoleTable().changeSelection(selectedRow, 1,
					// true, true);

					// Object t=
					// ((CASTableModel)curCell.getConsoleTable().getModel()).getValueAt(selectedRow);
					// System.out.println("Out: " +
					// ((CASTableCellValue)t).getOutput());
				}
			}

			if (e.getKeyCode() == KeyEvent.VK_UP) {
				System.out.println("Focus should be set at the line above");
				if (!curCell.isLineHighlighted()) {
					// Set Line of the previous row Highlighted; 
					// Set the focus on the line;
					// curCell.setLineHighlighted();

				} else {// Set the focus on the input text field

				}
			}

			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				if (!curCell.isLineHighlighted()) {
					// Show the line;
					// Set Line Highlighted; 
					// Set the focus on the line;
					System.out.println("Set the line highlighted");
					curCell.setLineHighlighted();
					curCell.setLineFoucs();

				} else {// Set the focus on the input text field of the next row

				}

			}
		}
	}

	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
