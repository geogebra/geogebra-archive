package geogebra.cas.view;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
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
		//System.out.println("Inside the cell");
		//System.out.println(src.getClass().getName());

		if (src instanceof JTextField) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				JTextField ta = (JTextField) src;
				//Get the input of the user
				String inputText = (ta.getText().substring(2)).trim();
				//Evaluate the input with Yacas, which is too slow
				String evaluation = view.getCAS().evaluateYACAS(inputText);
				//String evaluation = view.getCAS().evaluateJASYMCA(inputText);
				// show message box
				//StringBuffer sb = new StringBuffer();
				//sb.append("in: ");
				//sb.append(inputText);
				//sb.append("\nout: ");				
				//sb.append(evaluation);
				//JOptionPane.showMessageDialog(view, sb.toString());
				curCell.setInput(inputText);
				curCell.setOutput(evaluation);
				//We enlarge the height of the selected row
				int selectedRow = curCell.getConsoleTable().getSelectedRow();
				curCell.getConsoleTable().setRowHeight(selectedRow, 45);
				((CASTableModel)curCell.getConsoleTable().getModel()).setValueAt(new CASTableCellValue(inputText, evaluation), selectedRow);
				//Object t= ((CASTableModel)curCell.getConsoleTable().getModel()).getValueAt(selectedRow); 
				//System.out.println("Out: " + ((CASTableCellValue)t).getOutput());
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
