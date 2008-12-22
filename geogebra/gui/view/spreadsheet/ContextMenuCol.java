
package geogebra.gui.view.spreadsheet;

import geogebra.kernel.GeoElement;
import geogebra.main.Application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;

import javax.swing.JMenuItem;

public class ContextMenuCol extends ContextMenu
{
		
	private static final long serialVersionUID = 9049753346127061012L;

	public ContextMenuCol(MyTable table0, int column01, int row01, int column02, int row02, boolean[] selected0) {
		super(table0, column01, row01, column02, row02, selected0);		
	}

	protected void initMenu() {
		//super.initMenu();		
		//addSeparator();
		
   	 	JMenuItem item5 = new JMenuItem(app.getMenu("InsertLeft"));
	   	item5.setIcon(app.getEmptyIcon());
   	 	item5.addActionListener(new InsertLeft());   	 	
   	 	add(item5);
   	 	JMenuItem item6 = new JMenuItem(app.getMenu("InsertRight"));
	   	item6.setIcon(app.getEmptyIcon());
   	 	item6.addActionListener(new InsertRight());   	 	
   	 	add(item6);
		addSeparator();
		JMenuItem item7;
   	 	if (column1 == column2) item7 = new JMenuItem(app.getMenu("ClearColumn"));
   	 	else item7 = new JMenuItem(app.getMenu("ClearColumns"));
	   	item7.setIcon(app.getEmptyIcon());
   	 	item7.addActionListener(new ActionListenerClearColumns());   	 	
   	 	add(item7);   	 	
	}
	
	private class InsertLeft implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			int columns = table.getModel().getColumnCount();
 			if (columns == column1 + 1){
 				// last column: need to insert one more
				table.setMyColumnCount(table.getColumnCount() +1);		
				table.getView().getColumnHeader().revalidate();
				columns++;
 			}
 			int rows = table.getModel().getRowCount();
 			boolean succ = table.copyPasteCut.delete(columns - 1, 0, columns - 1, rows - 1);
 			for (int x = columns - 2; x >= column1; -- x) {
 				for (int y = 0; y < rows; ++ y) {
 					GeoElement geo = RelativeCopy.getValue(table, x, y);
 					if (geo == null) continue;
 					
 					Matcher matcher = GeoElement.spreadsheetPattern.matcher(geo.getLabel());
 					int column = GeoElement.getSpreadsheetColumn(matcher);
 					int row = GeoElement.getSpreadsheetRow(matcher);
 					column += 1;
 					String newLabel = GeoElement.getSpreadsheetCellName(column, row);
 					geo.setLabel(newLabel);
 					succ = true;
 				}
 			}
 			
 			if (succ)
 				app.storeUndoInfo();
 		}
	}
	
	private class InsertRight implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			int columns = table.getModel().getColumnCount();
			int rows = table.getModel().getRowCount();
			boolean succ = false;
 			if (columns == column1 + 1){
 				// last column: insert another on right
				table.setMyColumnCount(table.getColumnCount() +1);		
				table.getView().getColumnHeader().revalidate();
				// can't be undone
 			}
 			else
 			{
	 			succ = table.copyPasteCut.delete(columns - 1, 0, columns - 1, rows - 1);
	 			for (int x = columns - 2; x >= column2 + 1; -- x) {
	 				for (int y = 0; y < rows; ++ y) {
	 					GeoElement geo = RelativeCopy.getValue(table, x, y);
	 					if (geo == null) continue;
	 					
	 					Matcher matcher = GeoElement.spreadsheetPattern.matcher(geo.getLabel());
	 					int column = GeoElement.getSpreadsheetColumn(matcher);
	 					int row = GeoElement.getSpreadsheetRow(matcher);
	 					column += 1;
	 					String newLabel = GeoElement.getSpreadsheetCellName(column, row);
	 					geo.setLabel(newLabel);
	 					succ = true;
	 				}
	 			}
 			}
 			
 			if (succ)
 				app.storeUndoInfo();
 		}
	}
	
	private class ActionListenerClearColumns implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			int columns = table.getModel().getColumnCount();
 			int rows = table.getModel().getRowCount();

 			boolean succ = false;
 			
 			for (int col = 0 ; col < columns ; col++) {
 				if (selectedColumns[col]) {
 					if (table.copyPasteCut.delete(col, 0, col, rows)) succ = true;
 				}
 			}
 			
 			/* code to move all data left (which we don't want)
 			
 			int dx = column2 - column1 + 1;
			for (int x = column2 + 1; x < columns; ++ x) {
				for (int y = 0; y < table.getModel().getRowCount(); ++ y) {
 					GeoElement geo = RelativeCopy.getValue(table, x, y);
 					if (geo == null) continue;
 					int column = GeoElement.getSpreadsheetColumn(geo.getLabel());
 					int row = GeoElement.getSpreadsheetRow(geo.getLabel());
 					column = table.convertColumnIndexToView(column);
 					column -= dx;
 					column = table.convertColumnIndexToModel(column);
 					String newLabel = GeoElement.getSpreadsheetCellName(column, row);
 					//System.out.print(" " + newLabel + " ");
 					geo.setLabel(newLabel);
 					succ = true;
 				}
 			} */
			
			if (succ)
 				app.storeUndoInfo();
 		}
	}
	    		

}
