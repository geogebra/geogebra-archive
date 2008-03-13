package geogebra.cas.view;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import javax.swing.JTable;

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
		int selectedRow = consoleTable.getSelectedRow();
		int selectedCol = consoleTable.getSelectedColumn();

		// Here we delete the chosen row when press DELETE at coloumn 1;

		if (selectedCol == CASPara.indexCol) {
			if (e.getKeyCode() == KeyEvent.VK_DELETE) {
				{
					((CASTableModel) consoleTable.getModel())
							.removeRow(selectedRow);
				}
			}
		} else {

		}
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
