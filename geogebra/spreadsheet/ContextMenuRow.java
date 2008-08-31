
package geogebra.spreadsheet;

import geogebra.kernel.GeoElement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

public class ContextMenuRow extends ContextMenu
{
	
	private static final long serialVersionUID = -592258674730774706L;

	public ContextMenuRow(MyTable table0, int column01, int row01, int column02, int row02) {
		super(table0, column01, row01, column02, row02);
	}

	
	protected void initMenu() {
		// add main context menu
   	 	//super.initMenu();
		//addSeparator();
		
   	 	JMenuItem item5 = new JMenuItem(app.getMenu("InsertAbove"));
	   	item5.setIcon(app.getEmptyIcon());
   	 	item5.addActionListener(new ActionListener5());   	 	
   	 	add(item5);
   	 	JMenuItem item6 = new JMenuItem(app.getMenu("InsertBelow"));
	   	item6.setIcon(app.getEmptyIcon());
   	 	item6.addActionListener(new ActionListener6());   	 	
   	 	add(item6);
		addSeparator();
   	 	JMenuItem item7;
   	 	if (row1 == row2) item7 = new JMenuItem(app.getMenu("ClearRow"));
   	 	else item7 = new JMenuItem(app.getMenu("ClearRows"));
	   	item7.setIcon(app.getEmptyIcon());
   	 	item7.addActionListener(new ActionListener7());   	 	
   	 	add(item7); 
   	 	
   	 	
	}
	
	private class ActionListener5 implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			int columns = table.getModel().getColumnCount();
 			boolean succ = table.copyPasteCut.delete(0, table.getModel().getColumnCount() - 1, columns - 1, table.getModel().getColumnCount() - 1);
 			for (int y = table.getModel().getColumnCount() - 2; y >= row1; -- y) {
 				for (int x = 0; x < columns; ++ x) {
 					GeoElement geo = RelativeCopy.getValue(table, x, y);
 					if (geo == null) continue;
 					int column = GeoElement.getSpreadsheetColumn(geo.getLabel());
 					int row = GeoElement.getSpreadsheetRow(geo.getLabel());
 					row += 1;
 					String newLabel = "" + (char)('A' + column) + (row + 1);
 					geo.setLabel(newLabel);
 					succ = true;
 				}
 			}
 			
 			if (succ)
 				app.storeUndoInfo();
 		}
	}
	
	private class ActionListener6 implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			int columns = table.getModel().getColumnCount();
 			boolean succ = table.copyPasteCut.delete(0, table.getModel().getColumnCount() - 1, columns - 1, table.getModel().getColumnCount() - 1);
 			for (int y = table.getModel().getColumnCount() - 2; y >= row2 + 1; -- y) {
 				for (int x = 0; x < columns; ++ x) {
 					GeoElement geo = RelativeCopy.getValue(table, x, y);
 					if (geo == null) continue;
 					int column = GeoElement.getSpreadsheetColumn(geo.getLabel());
 					int row = GeoElement.getSpreadsheetRow(geo.getLabel());
 					row += 1;
 					String newLabel = "" + (char)('A' + column) + (row + 1);
 					geo.setLabel(newLabel);
 					succ = true;
 				}
 			}
 			
 			if (succ)
 				app.storeUndoInfo();
 		}
	}
	
	private class ActionListener7 implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			int columns = table.getModel().getColumnCount();
 			boolean succ = table.copyPasteCut.delete(column1, row1, column2, row2);
 			int dy = row2 - row1 + 1;
 			for (int y = row2 + 1; y < table.getModel().getColumnCount(); ++ y) {
 				for (int x = 0; x < columns; ++ x) {
 					GeoElement geo = RelativeCopy.getValue(table, x, y);
 					if (geo == null) continue;
 					int column = GeoElement.getSpreadsheetColumn(geo.getLabel());
 					int row = GeoElement.getSpreadsheetRow(geo.getLabel());
 					row -= dy;
 					String newLabel = "" + (char)('A' + column) + (row + 1);
 					geo.setLabel(newLabel);
 					succ = true;
 				}
 			}
 			
 			if (succ)
	 			app.storeUndoInfo();
 		}
	}
	    	
	
}
