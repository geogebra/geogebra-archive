package geogebra.cas.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;

public class CASTableCellMouseController implements MouseListener {

	private CASTableCell curCell;
	private CASView view;

	public CASTableCellMouseController(CASTableCell cell, CASView view) {
		this.curCell = cell;
		this.view = view;
	}

	public void mouseClicked(MouseEvent arg0) {
		System.out.println("Hello, I get the click in the table cell");
		
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
