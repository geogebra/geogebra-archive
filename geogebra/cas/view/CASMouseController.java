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

	private boolean flag = false;// Judge whether there is a double-click

	private int clickNum = 0;// Record the number of clicks

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
		System.out.println("single click at"
					+ rowI + "" + colI);
		if (colI == CASPara.contCol){ //Set the focus to the input textfiled
			//CASTableCellValue src = (CASTableCellValue)((CASTableModel) consoleTable.getModel()).getValueAt(rowI);
			consoleTable.editCellAt(rowI, colI);
			Component clickedComponent = consoleTable.findComponentAt(e.getX(), e.getY());
			clickedComponent.requestFocus();
			System.out.println("clickedComponent: " + clickedComponent);
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

	/*
	 * Function for the double-click
	 */
//	public void mouseDoubleClicked(MouseEvent e) {
//		// System.out.println("Doublc Clicked!");
//		int rowI, colI;
//		rowI = consoleTable.rowAtPoint(e.getPoint());// Get the row number
//		colI = consoleTable.columnAtPoint(e.getPoint());
//		if (rowI > -1)
//			System.out.println("double click at"
//					+ rowI + "" + colI);
//
//		if (colI == CASPara.indexCol){
//			//Insert a new row
//			CASTableCellValue value = new CASTableCellValue();
//			((CASTableModel) consoleTable.getModel()).insertRow(
//					(rowI >= 0 ? rowI : 0), new Object[]{ "New", value});
//		}
//	}
}
