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
			if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
				JTextField ta = (JTextField) src;
				//Get the input of the user
				String inputText = (ta.getText().substring(2)).trim();
				//Evaluate the input with Yacas, which is too slow
				String evaluation = view.getCAS().evaluateYACAS(inputText);
				//String evaluation = view.getCAS().evaluateJASYMCA(inputText);
				// show message box
				StringBuffer sb = new StringBuffer();
				sb.append("in: ");
				sb.append(inputText);
				//sb.append("\nout: ");				
				//sb.append(evaluation);
				curCell.setOutput("<<" + evaluation);
				//JOptionPane.showMessageDialog(view, sb.toString());
				curCell.getConsoleTable().setRowHeight(0,50);
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
