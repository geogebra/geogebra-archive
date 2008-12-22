
package geogebra.gui.view.spreadsheet;

import geogebra.kernel.GeoElement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;

import javax.swing.JMenuItem;

public class ContextMenuRow extends ContextMenu
{
	
	private static final long serialVersionUID = -592258674730774706L;

	public ContextMenuRow(MyTable table0, int column01, int row01, int column02, int row02, boolean[] selected0) {
		super(table0, column01, row01, column02, row02, selected0);
	}

	
	protected void initMenu() {
		// add main context menu
   	 	//super.initMenu();
		//addSeparator();
		
   	 	JMenuItem item5 = new JMenuItem(app.getMenu("InsertAbove"));
	   	item5.setIcon(app.getEmptyIcon());
   	 	item5.addActionListener(new InsertAbove());   	 	
   	 	add(item5);
   	 	JMenuItem item6 = new JMenuItem(app.getMenu("InsertBelow"));
	   	item6.setIcon(app.getEmptyIcon());
   	 	item6.addActionListener(new InsertBelow());   	 	
   	 	add(item6);
		addSeparator();
   	 	JMenuItem item7;
   	 	if (row1 == row2) item7 = new JMenuItem(app.getMenu("ClearRow"));
   	 	else item7 = new JMenuItem(app.getMenu("ClearRows"));
	   	item7.setIcon(app.getEmptyIcon());
   	 	item7.addActionListener(new ActionListenerClearRows());   	 	
   	 	add(item7); 
   	 	
   	 	
	}
	
	private class InsertAbove implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			int columns = table.getModel().getColumnCount();
 			int rows = table.getModel().getRowCount();
 			if (rows == row2 + 1){
 				// last row: need to insert one more
				table.tableModel.setRowCount(table.getRowCount() +1);		
				table.getView().getRowHeader().revalidate();
				rows++;
 			}
 			boolean succ = table.copyPasteCut.delete(0, rows - 1, columns - 1, rows - 1);
 			for (int y = rows - 2; y >= row1; -- y) {
 				for (int x = 0; x < columns; ++ x) {
 					GeoElement geo = RelativeCopy.getValue(table, x, y);
 					if (geo == null) continue;
 					
 					Matcher matcher = GeoElement.spreadsheetPattern.matcher(geo.getLabel());
 					int column = GeoElement.getSpreadsheetColumn(matcher);
 					int row = GeoElement.getSpreadsheetRow(matcher);
 					row += 1;
 					String newLabel = GeoElement.getSpreadsheetCellName(column, row);
 					geo.setLabel(newLabel);
 					succ = true;
 				}
 			}
 			
 			if (succ)
 				app.storeUndoInfo();
 		}
	}
	
	private class InsertBelow implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			int columns = table.getModel().getColumnCount();
 			int rows = table.getModel().getRowCount();
 			boolean succ = false;
 			if (rows == row2 + 1){
 				// last row: need to insert one more
				table.tableModel.setRowCount(table.getRowCount() +1);		
				table.getView().getRowHeader().revalidate();
				// can't be undone
 			}
 			else
 			{
	 			succ = table.copyPasteCut.delete(0, rows - 1, columns - 1, rows - 1);
	 			for (int y = rows - 2; y >= row2 + 1; -- y) {
	 				for (int x = 0; x < columns; ++ x) {
	 					GeoElement geo = RelativeCopy.getValue(table, x, y);
	 					if (geo == null) continue;
	 					Matcher matcher = GeoElement.spreadsheetPattern.matcher(geo.getLabel());
	 					int column = GeoElement.getSpreadsheetColumn(matcher);
	 					int row = GeoElement.getSpreadsheetRow(matcher);
	 					row += 1;
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
	
	private class ActionListenerClearRows implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			//int rows = table.getModel().getRowCount();
 			boolean succ = table.copyPasteCut.delete(column1, row1, column2, row2);

 			/* code to move data into cleared space ie turn into delete row
 			 * (which we don't want)
 			int dy = row2 - row1 + 1;
 			for (int y = row2 + 1; y < table.getModel().getColumnCount(); ++ y) {
 				for (int x = 0; x < rows; ++ x) {
 					GeoElement geo = RelativeCopy.getValue(table, x, y);
 					if (geo == null) continue;
 					int column = GeoElement.getSpreadsheetColumn(geo.getLabel());
 					int row = GeoElement.getSpreadsheetRow(geo.getLabel());
 					row -= dy;
 					String newLabel = "" + (char)('A' + column) + (row + 1);
 					geo.setLabel(newLabel);
 					succ = true;
 				}
 			}*/
 			
 			if (succ)
	 			app.storeUndoInfo();
 		}
	}
	    	
	
}
