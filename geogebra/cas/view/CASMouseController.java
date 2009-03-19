package geogebra.cas.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CASMouseController extends MouseAdapter {

	private CASTable table;

	public CASMouseController(CASTable table) {
		this.table = table;
	}
	
	public void mousePressed(MouseEvent e) {
		int row = table.rowAtPoint(e.getPoint());
		CASTableCellValue clickedCellValue = table.getCASTableCellValue(row);
		
		// EDITOR ACTIVE: insert into editor
		if (table.isEditing()) {
			CASTableCellEditor editor = table.getEditor();
			String str = clickedCellValue.getOutput();
			
			// if the editor is not empty and we insert a complex text
			// let's put parentheses around the inserted text
			if (editor.getInputText().trim().length() > 0 && str.indexOf(' ') > 0) 
			{
				str = " (" + str + ") ";
			}
			
			editor.insertText(str);
			e.consume();
			
		}
		// NOT EDITING: put into next free cell
		else {
			CASTableCellValue newValue = null;
			if (table.getRowCount() > row + 1) {
				newValue = table.getCASTableCellValue(row + 1);
				if (newValue.isEmpty()) {
					newValue.setInput(clickedCellValue.getOutput());
					table.updateRow(row + 1);
					return;
				}
			} 
			
			// create new row
			newValue = new CASTableCellValue();			
			newValue.setInput(clickedCellValue.getOutput());
			table.insertRowAfter(row, newValue);	
		}

	}



}
