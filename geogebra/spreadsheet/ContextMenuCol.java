
package geogebra.spreadsheet;

import geogebra.Application;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import geogebra.kernel.GeoElement;

public class ContextMenuCol extends ContextMenu
{
	
	protected static JPopupMenu menu2 = null;
	
	protected static JPopupMenu initMenu2(Application app) {
		JPopupMenu menu = ContextMenu.initMenu(app);
		menu.addSeparator();
   	 	JMenuItem item5 = new JMenuItem(app.getMenu("InsertLeft"));
   	 	item5.setBackground(bgColor);
   	 	item5.addActionListener(new ActionListener5());   	 	
   	 	menu.add(item5);
   	 	JMenuItem item6 = new JMenuItem(app.getMenu("InsertRight"));
   	 	item6.setBackground(bgColor);
   	 	item6.addActionListener(new ActionListener6());   	 	
   	 	menu.add(item6);
		menu.addSeparator();
   	 	JMenuItem item7 = new JMenuItem(app.getMenu("DeleteColumn"));
   	 	item7.setBackground(bgColor);
   	 	item7.addActionListener(new ActionListener7());   	 	
   	 	menu.add(item7);
   	 	return menu;
	}
	
	public static class ActionListener5 implements ActionListener
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
	
	public static class ActionListener6 implements ActionListener
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
	
	public static class ActionListener7 implements ActionListener
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
	    	
	public static void showPopupMenu2(MyTable table0, Component comp, int column01, int row01, int column02, int row02, int x, int y) {
		table = table0;
		Application app = table.kernel.getApplication();
		if (menu2 == null) {
			menu2 = initMenu2(app);		
		}
		column1 = column01;
		column2 = column02;
		row1 = row01;
		row2 = row02;
		menu2.show(comp, x, y);
	}

}
