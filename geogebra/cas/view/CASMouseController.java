package geogebra.cas.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CASMouseController implements MouseListener {

	private CASTable consoleTable;

	public CASMouseController(CASTable table) {
		this.consoleTable = table;
	}

	public void mouseClicked(MouseEvent e) {
		int row = consoleTable.getSelectedRow();
				
		if (e.getClickCount() == 1) { 
			// start editing
			consoleTable.startEditingRow(row);			
		}
		if (e.getClickCount() == 2) {
			CASTableCellValue selCellValue = consoleTable.getCASTableCellValue(row);
			
			CASTableCellValue newValue = null;
			if (consoleTable.getRowCount() > row + 1) {
				newValue = consoleTable.getCASTableCellValue(row + 1);
				if (newValue.isEmpty()) {
					newValue.setInput(selCellValue.getOutput());
					consoleTable.updateRow(row + 1);
					return;
				}
			} 
			
			// create new row
			newValue = new CASTableCellValue();			
			newValue.setInput(selCellValue.getOutput());
			consoleTable.insertRowAfter(row, newValue);
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
