package geogebra.cas.view;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.table.TableCellRenderer;

public class TableCellMouseListener extends MouseAdapter {

	private CASView casView;
	private CASTable table;

	public TableCellMouseListener(CASView casView) {
		this.casView = casView;
		this.table = casView.getConsoleTable();
	}		
	
	public void mouseReleased(MouseEvent e) {
		e.consume();
	}
	
	public void mouseClicked(MouseEvent e) {
		e.consume();
	}
	
	/**
	 * Handles clicks on the output panel of a table cell and inserts
	 * the clicked output string into the currently active editor.
	 */
	public void mousePressed(MouseEvent e) {	
		boolean substituteDialogActive = casView.getSubstituteDialog() != null;
		
		// clicked row in table
		Point p = e.getPoint();
		int clickedRow = table.rowAtPoint(p);
		if (clickedRow < 0) {
			// start editing last row
			if (!substituteDialogActive)
				table.startEditingRow(table.getRowCount()-1);
			return;
		}
		
		// get renderer for this row
		CASTableCellValue clickedCellValue = table.getCASTableCellValue(clickedRow);
		TableCellRenderer  renderer =  table.getCellRenderer(clickedRow, CASTable.COL_CAS_CELLS);
		CASTableCell tableCell = (CASTableCell) table.prepareRenderer(renderer, clickedRow, CASTable.COL_CAS_CELLS);
		
		 // Convert the event to the renderer's coordinate system
		 Rectangle cellRect = table.getCellRect(clickedRow, CASTable.COL_CAS_CELLS, false);
		 p.translate(-cellRect.x, -cellRect.y);
		 		
		// check if we clicked on input panel within tableCell
		if (p.y <= tableCell.getInputPanelHeight() + 10) {
			if (substituteDialogActive)
				casView.getSubstituteDialog().insertText(clickedCellValue.getTranslatedInput());
			else
				table.startEditingRow(clickedRow);
			return;
		}
			
		
		// CLICKED ON OUTPUT PANEL IN TABLE CELL
		String outputStr = clickedCellValue.getOutput();			
		if (outputStr == null || outputStr.length() == 0) {
			if (!substituteDialogActive)
				table.startEditingRow(clickedRow);
			return;
		}
		
		if (substituteDialogActive) {
			if (table.isEditing())
				table.stopEditing();
			
			casView.getSubstituteDialog().insertText(outputStr);
			return;
		}
		
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
				if (newValue.isInputEmpty()) {
					newValue.setInput(outputStr);
					table.updateRow(clickedRow + 1);					
				}
			} 
			
			// create new row
			if (newValue == null) {
				newValue = new CASTableCellValue(table.getCASView());			
				newValue.setInput(outputStr);
				table.insertRowAfter(clickedRow, newValue, true);	
			}
			
			table.startEditingRow(clickedRow  + 1);
		}
		
		e.consume();
	}



}
