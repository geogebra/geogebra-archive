
package geogebra.spreadsheet;

import geogebra.kernel.GeoElement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

public class ContextMenuCol extends ContextMenu
{
		
	private static final long serialVersionUID = 9049753346127061012L;

	public ContextMenuCol(MyTable table0, int column01, int row01, int column02, int row02) {
		super(table0, column01, row01, column02, row02);		
	}

	protected void initMenu() {
		//super.initMenu();		
		//addSeparator();
		
   	 	JMenuItem item5 = new JMenuItem(app.getMenu("InsertLeft"));
	   	item5.setIcon(app.getEmptyIcon());
   	 	item5.addActionListener(new ActionListener5());   	 	
   	 	add(item5);
   	 	JMenuItem item6 = new JMenuItem(app.getMenu("InsertRight"));
	   	item6.setIcon(app.getEmptyIcon());
   	 	item6.addActionListener(new ActionListener6());   	 	
   	 	add(item6);
		addSeparator();
		JMenuItem item7;
   	 	if (column1 == column2) item7 = new JMenuItem(app.getMenu("ClearColumn"));
   	 	else item7 = new JMenuItem(app.getMenu("ClearColumns"));
	   	item7.setIcon(app.getEmptyIcon());
   	 	item7.addActionListener(new ActionListenerClearColumns());   	 	
   	 	add(item7);   	 	
	}
	
	private class ActionListener5 implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			int columns = table.getModel().getColumnCount();
 			table.copyPasteCut.delete(columns - 1, 0, columns - 1, table.getModel().getRowCount() - 1);
 			for (int x = columns - 2; x >= column1; -- x) {
 				for (int y = 0; y < table.getModel().getColumnCount(); ++ y) {
 					GeoElement geo = RelativeCopy.getValue(table, x, y);
 					if (geo == null) continue;
 					int column = GeoElement.getSpreadsheetColumn(geo.getLabel());
 					int row = GeoElement.getSpreadsheetRow(geo.getLabel());
 					column += 1;
 					String newLabel = "" + (char)('A' + column) + (row + 1);
 					geo.setLabel(newLabel);
 				}
 			}
 		}
	}
	
	private class ActionListener6 implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			int columns = table.getModel().getColumnCount();
 			table.copyPasteCut.delete(columns - 1, 0, columns - 1, table.getModel().getRowCount() - 1);
 			for (int x = columns - 2; x >= column2 + 1; -- x) {
 				for (int y = 0; y < table.getModel().getRowCount(); ++ y) {
 					GeoElement geo = RelativeCopy.getValue(table, x, y);
 					if (geo == null) continue;
 					int column = GeoElement.getSpreadsheetColumn(geo.getLabel());
 					int row = GeoElement.getSpreadsheetRow(geo.getLabel());
 					column += 1;
 					String newLabel = "" + (char)('A' + column) + (row + 1);
 					geo.setLabel(newLabel);
 				}
 			}
 		}
	}
	
	private class ActionListenerClearColumns implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			int columns = table.getModel().getColumnCount();
 			table.copyPasteCut.delete(column1, row1, column2, row2);
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
 					String newLabel = "" + (char)('A' + column) + (row + 1);
 					//System.out.print(" " + newLabel + " ");
 					geo.setLabel(newLabel);
 				}
 			}
 		}
	}
	    		

}
