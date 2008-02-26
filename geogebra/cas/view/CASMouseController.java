package geogebra.cas.view;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
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
		if (rowI <0 )
			return;
		//System.out.println("single click at" + rowI + "" + colI);
		if (colI == CASPara.contCol){ //Set the focus to the input textfiled
			consoleTable.editCellAt(rowI, colI);
			//Get the deepest component at (X, Y)
			Component clickedComponent = consoleTable.findComponentAt(e.getPoint());
			clickedComponent.requestFocus();
			//System.out.println("clickedComponent: " + clickedComponent);
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
