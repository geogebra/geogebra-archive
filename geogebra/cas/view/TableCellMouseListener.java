package geogebra.cas.view;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.table.TableCellRenderer;

public class TableCellMouseListener extends MouseAdapter {

	private CASTable table;

	public TableCellMouseListener(CASTable table) {
		this.table = table;
	}		
	
	/**
	 * Handles clicks on the output panel of a table cell and inserts
	 * the clicked output string into the currently active editor.
	 */
	public void mousePressed(MouseEvent e) {		
		// clicked row in table
		Point p = e.getPoint();
		int clickedRow = table.rowAtPoint(p);
		
		// get renderer for this row
		TableCellRenderer  renderer =  table.getCellRenderer(clickedRow, CASTable.CONTENT_COLUMN);
		CASTableCell tableCell = (CASTableCell) table.prepareRenderer(renderer, clickedRow, CASTable.CONTENT_COLUMN);
		
		 // Convert the event to the renderer's coordinate system
		 Rectangle cellRect = table.getCellRect(clickedRow, CASTable.CONTENT_COLUMN, false);
		 p.translate(-cellRect.x, -cellRect.y);

		// check if we clicked on input panel within tableCell
		if (p.y <= tableCell.getInputPanelHeight() + 10) {
			return;
		}
		
		// CLICKED ON OUTPUT PANEL IN TABLE CELL				
		CASTableCellValue clickedCellValue = table.getCASTableCellValue(clickedRow);
		String outputStr = clickedCellValue.getOutput();
		if (outputStr == null || outputStr.length() == 0) return;
		
		// EDITOR ACTIVE: insert into editor
		if (table.isEditing()) {
			CASTableCellEditor editor = table.getEditor();
			if (editor.getEditingRow() != clickedRow) {						
				// if the editor is not empty and we insert a complex text
				// let's put parentheses around the inserted text
				if (editor.getInputText().trim().length() > 0 && outputStr.indexOf(' ') > 0) 
				{
					outputStr = " (" + outputStr + ") ";
				}				
				editor.insertText(outputStr);
			}
		}
		
		// NOT EDITING: put into next free cell
		else {
			CASTableCellValue newValue = null;
			if (table.getRowCount() > clickedRow + 1) {
				newValue = table.getCASTableCellValue(clickedRow + 1);
				if (newValue.isEmpty()) {
					newValue.setInput(outputStr);
					table.updateRow(clickedRow + 1);					
				}
			} 
			
			// create new row
			if (newValue == null) {
				newValue = new CASTableCellValue(table.getCASView());			
				newValue.setInput(outputStr);
				table.insertRowAfter(clickedRow, newValue);	
			}
			
			table.startEditingRow(clickedRow  + 1);
		}
		
		e.consume();
	}



}
