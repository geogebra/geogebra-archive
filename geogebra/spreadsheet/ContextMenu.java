
package geogebra.spreadsheet;

import geogebra.Application;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import geogebra.kernel.GeoElement;
import java.util.LinkedList;

public class ContextMenu
{
	
	final static Color bgColor = Color.white;

	protected static MyTable table = null;
	protected static int row1 = -1;
	protected static int row2 = -1;
	protected static int column1 = -1;
	protected static int column2 = -1;
	protected static boolean[] selected = null;
	
	protected static JPopupMenu initMenu(Application app) {
		JPopupMenu menu = new JPopupMenu();
   	 	//JCheckBoxMenuItem item1 = new JCheckBoxMenuItem( app.getPlain("ShowObject"));
   	 	JMenuItem item1 = new JMenuItem(app.getMenu("Copy"));
   	 	item1.setIcon(app.getImageIcon("mode_showhideobject_16.gif"));
   	 	item1.setBackground(bgColor);
   	 	item1.addActionListener(new ActionListener1());
   	 	menu.add(item1);
   	 	JMenuItem item2 = new JMenuItem(app.getMenu("Paste"));
   	 	item2.setIcon(app.getImageIcon("mode_showhideobject_16.gif"));
   	 	item2.setBackground(bgColor);
   	 	item2.addActionListener(new ActionListener2());   	 	
   	 	menu.add(item2);
   	 	JMenuItem item3 = new JMenuItem(app.getMenu("Cut"));
   	 	item3.setIcon(app.getImageIcon("mode_showhideobject_16.gif"));
   	 	item3.setBackground(bgColor);
   	 	item3.addActionListener(new ActionListener3());   	 	
   	 	menu.add(item3);
   	 	JMenuItem item4 = new JMenuItem(app.getMenu("ClearSelection"));
   	 	item4.setIcon(app.getImageIcon("mode_showhideobject_16.gif"));
   	 	item4.setBackground(bgColor);
   	 	item4.addActionListener(new ActionListener4());
   	 	menu.add(item4);
   	 	if (column1 + 1 <= column2) {
   	   	 	JMenuItem item5 = new JMenuItem(app.getMenu("CreateListOfPoints"));
   	   	 	item5.setIcon(app.getImageIcon("mode_showhideobject_16.gif"));
   	   	 	item5.setBackground(bgColor);
   	   	 	item5.addActionListener(new ActionListener51());
   	   	 	menu.add(item5);
   	 	}
   	 	return menu;
	}
	
	public static class ActionListener1 implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			table.copyPasteCut.copy(column1, row1, column2, row2);
 		}
	}
    	
	public static class ActionListener2 implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			table.copyPasteCut.paste(column1, row1, column2, row2);
 			table.getView().getRowHeader().revalidate(); 		
 		}
	}
    	
	public static class ActionListener3 implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			table.copyPasteCut.cut(column1, row1, column2, row2);
 		}
	}
    	
	public static class ActionListener4 implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			table.copyPasteCut.delete(column1, row1, column2, row2);
 		}
	}
	
	protected static int pointNameCount = 0;
	
	public static String getNextPointName() {
		++ pointNameCount;
		if (pointNameCount < 10) {
			return "P_" + pointNameCount;		
		}
		return "P_{" + pointNameCount + "}";
	}
	
	protected static int listNameCount = 0;
	
	public static String getNextListName() {
		++ listNameCount;
		if (listNameCount < 10) {
			return "L_" + listNameCount;		
		}
		return "L_{" + listNameCount + "}";
	}
	
	public static class ActionListener51 implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			Application.debug("ActionListener5 " + column1 + " - " + column2);
 			if (selected == null) throw new RuntimeException("error state");
 			LinkedList list = new LinkedList();
 			try {
 				int xColumn = -1;
	 			for (int j = column1; j <= column2; ++ j) {
	 				if (! selected[j]) {
	 					continue;
	 				}
	 				if (xColumn == -1) {
	 					xColumn = j;
	 					continue;
	 				}
	  	   	 		for (int i = row1; i <= row2; ++ i) {
	 	   	 			GeoElement v1 = RelativeCopy.getValue(table, xColumn, i);
	 	   	 			GeoElement v2 = RelativeCopy.getValue(table, j, i);
	 	   	 			if (v1 != null && v2 != null && v1.isGeoNumeric() && v2.isGeoNumeric()) {
	 	   	 				String pointName = getNextPointName();
	 	   	 				String text = pointName + "=(" + v1.getLabel() + "," + v2.getLabel() + ")";
	 	   	 				table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, false);
	 		   	 			list.addLast(pointName);
	 	   	 			}
	 	   	 		}
	 			}
	
	 			if (list.size() > 0) {
	 				String[] points = (String[])list.toArray(new String[0]);
	 				String listName = ContextMenu.getNextListName();
	 				String text = listName + "={";
	 				for (int i = 0; i < points.length; ++ i) {
	 					text += points[i];
	 					if (i != points.length - 1) text += ",";
	 				}
	 				text += "}";
	 				GeoElement[] values = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, false);
	 				for (int i = 0; i < values.length; ++ i) {
	 					values[i].setAuxiliaryObject(true);
	 				}
	 			}
 			} catch (Exception ex) {
 				// Just abort the process
 			} finally {
 				table.kernel.storeUndoInfo();
 			}

		}
	}
    	
	public static void showPopupMenu(MyTable table0, Component comp, int column01, int row01, int column02, int row02, int x, int y, boolean[] selected0) {
		//Application.debug("showPopupMenu <<<<<<<<<<<<<<<<<<<");
		table = table0;
		column1 = column01;
		column2 = column02;
		row1 = row01;
		row2 = row02;
		if (selected0 == null) {
			throw new RuntimeException("error state");
		}
		else {
			//Application.debug("Correct !!!!!!!!!!!!!!!!!!!!");
		}
		selected = selected0;
		Application app = table.kernel.getApplication();
		JPopupMenu menu = initMenu(app);		
		menu.show(comp, x, y);
	}

}
