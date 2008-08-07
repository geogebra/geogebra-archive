package geogebra.cas.view;

import geogebra.Application;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CASKeyController implements KeyListener {

	private CASSession session;

	private CASView view;

	private CASTable consoleTable;

	public CASKeyController(CASView view, CASSession session, CASTable table) {
		this.session = session;
		this.view = view;
		this.consoleTable = table;
	}

	/*
	 * KeyListener
	 */

	public void keyPressed(KeyEvent e) {
		Object src = e.getSource();
		Application.debug("Key Pressed " + src.getClass().getName());
		int[] selectedRowArray = consoleTable.getSelectedRows();
		int selectedCol = consoleTable.getSelectedColumn();
		
		// Here we delete the chosen row when press DELETE at coloumn 1;
		if (selectedCol == CASPara.indexCol) {
			if (e.getKeyCode() == KeyEvent.VK_DELETE) {
				// e.consume();
				Application.debug("selectedRowArray: "
						+ selectedRowArray.length);
				consoleTable.deleteRow(selectedRowArray[0]);
			} else
				e.consume();
		} else {
			e.consume();
		}
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
