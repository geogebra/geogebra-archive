package geogebra.cas.view;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

public class CASKeyController implements KeyListener {

	private CASSession session;

	private CASView view;

	private JTable consoleTable;

	public CASKeyController(CASView view, CASSession session, JTable table) {
		this.session = session;
		this.view = view;
		this.consoleTable = table;
	}

	/*
	 * KeyListener
	 */

	public void keyPressed(KeyEvent e) {
		Object src = e.getSource();
		System.out.println("Key Pressed " + src.getClass().getName());

		//Here we delete the chosen row when press DELETE at coloumn 1;
		if (e.getKeyCode() == KeyEvent.VK_DELETE) {
			int selectedCol = consoleTable.getSelectedColumn();
			if(selectedCol == CASPara.indexCol){
				int selectedRow = consoleTable.getSelectedRow();
				((CASTableModel)consoleTable.getModel()).removeRow(selectedRow);
			}
		}

	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseClicked(MouseEvent e) {
		Object src = e.getSource();
		System.out
				.println("Mouse Clicked--------- " + src.getClass().getName());

	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		Object src = e.getSource();
		System.out
				.println("Mouse Pressed--------- " + src.getClass().getName());
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		Object src = e.getSource();
		System.out.println("Mouse Released--------- "
				+ src.getClass().getName());
	}

}
