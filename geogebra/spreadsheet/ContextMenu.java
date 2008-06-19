
package geogebra.spreadsheet;

import geogebra.Application;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import geogebra.kernel.GeoElement;

public class ContextMenu
{
	
	final static Color bgColor = Color.white;

	protected static MyTable table = null;
	protected static int row1 = -1;
	protected static int row2 = -1;
	protected static int column1 = -1;
	protected static int column2 = -1;
	
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
   	 	if (row1 + 1 == row2 || column1 + 1 == column2) {
   	   	 	JMenuItem item5 = new JMenuItem(app.getMenu("ConvertToCoordinates"));
   	   	 	item5.setIcon(app.getImageIcon("mode_showhideobject_16.gif"));
   	   	 	item5.setBackground(bgColor);
   	   	 	item5.addActionListener(new ActionListener5());
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
	
	public static int nameCount = 0;
	
	public static String getNextPointName() {
		++ nameCount;
		return "P_" + nameCount;
	}
	
	public static class ActionListener5 implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 	   	 	if (row1 + 1 == row2) {
 	   	 		for (int i = column1; i <= column2; ++ i) {
 	   	 			GeoElement v1 = RelativeCopy.getValue(table, i, row1);
 	   	 			GeoElement v2 = RelativeCopy.getValue(table, i, row2);
 	   	 			if (v1 != null && v2 != null && v1.isGeoNumeric() && v2.isGeoNumeric()) {
 	   	 				String pointName = getNextPointName();
 	   	 				String text = pointName + "=(" + v1.getLabel() + "," + v2.getLabel() + ")";
 	   	 				table.kernel.getAlgebraProcessor().processAlgebraCommand(text, true);
 	   	 			}
 	   	 		}
 	   	 	}
 	   	 	if (column1 + 1 == column2) {
 	   	 		for (int i = row1; i <= row2; ++ i) {
 	   	 			GeoElement v1 = RelativeCopy.getValue(table, column1, i);
 	   	 			GeoElement v2 = RelativeCopy.getValue(table, column2, i);
 	   	 			if (v1 != null && v2 != null && v1.isGeoNumeric() && v2.isGeoNumeric()) {
 	   	 				String pointName = getNextPointName();
 	   	 				String text = pointName + "=(" + v1.getLabel() + "," + v2.getLabel() + ")";
 	   	 				table.kernel.getAlgebraProcessor().processAlgebraCommand(text, true);
 	   	 			}
 	   	 		}
 	   	 	}
 		}
	}
    	
	public static void showPopupMenu(MyTable table0, Component comp, int column01, int row01, int column02, int row02, int x, int y) {
		table = table0;
		column1 = column01;
		column2 = column02;
		row1 = row01;
		row2 = row02;
		Application app = table.kernel.getApplication();
		JPopupMenu menu = initMenu(app);		
		menu.show(comp, x, y);
	}

}
