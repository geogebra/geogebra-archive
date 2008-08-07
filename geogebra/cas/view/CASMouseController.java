package geogebra.cas.view;

import geogebra.Application;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;

public class CASMouseController implements MouseListener {

	private CASSession session;

	private CASView view;

	private JTable consoleTable;

	public CASMouseController(CASView view, CASSession session, JTable table) {
		this.session = session;
		this.view = view;
		this.consoleTable = table;
	}

	public void mouseClicked(MouseEvent e) {
		int rowI, colI;
		rowI = consoleTable.rowAtPoint(e.getPoint());// Get the row number
		colI = consoleTable.columnAtPoint(e.getPoint());
		if (rowI < 0)
			return;
		Application.debug("single click at" + rowI + "" + colI);
		if (colI == CASPara.contCol) { // Set the focus to the input textfiled
			consoleTable.changeSelection(rowI, colI, false, false);
			consoleTable.editCellAt(rowI, colI);
			Application.debug("Mouse down new location: " + rowI + " " + colI);
			// Get the deepest component at (X, Y)
			// Component clickedComponent =
			// consoleTable.findComponentAt(e.getPoint());
			// Component clickedCell =
			// consoleTable.getComponentAt(e.getPoint());

			CASTableCell clickedCell = (CASTableCell) consoleTable
					.getComponentAt(e.getPoint());
			Application.debug("clickedComponent: "
					+ (clickedCell.getComponents()).length);
			clickedCell.setLineInvisiable();
			clickedCell.setInputAreaFocused();
		} else {// To get the focus from the last edited cell
//			consoleTable.changeSelection(rowI, colI, false, false);
//			Component clickedCell = consoleTable.getComponentAt(e.getPoint());
//			Application.debug("clickedComponent: " + clickedCell);
//			clickedCell.requestFocus();
		}

	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
