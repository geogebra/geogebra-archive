
package geogebra.spreadsheet;

import geogebra.Application;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class ContextMenu
{
	
	final static Color bgColor = Color.white;

	protected static MyTable table = null;
	protected static JPopupMenu menu = null;
	protected static int row1 = -1;
	protected static int row2 = -1;
	protected static int column1 = -1;
	protected static int column2 = -1;
	
	protected static JPopupMenu initMenu(Application app) {
		JPopupMenu menu = new JPopupMenu();
   	 	//JCheckBoxMenuItem item1 = new JCheckBoxMenuItem( app.getPlain("ShowObject"));
   	 	JMenuItem item1 = new JMenuItem("Copy");
   	 	item1.setIcon(app.getImageIcon("mode_showhideobject_16.gif"));
   	 	item1.setBackground(bgColor);
   	 	item1.addActionListener(new ActionListener1());
   	 	menu.add(item1);
   	 	JMenuItem item2 = new JMenuItem("Paste");
   	 	item2.setIcon(app.getImageIcon("mode_showhideobject_16.gif"));
   	 	item2.setBackground(bgColor);
   	 	item2.addActionListener(new ActionListener2());   	 	
   	 	menu.add(item2);
   	 	JMenuItem item3 = new JMenuItem("Cut");
   	 	item3.setIcon(app.getImageIcon("mode_showhideobject_16.gif"));
   	 	item3.setBackground(bgColor);
   	 	item3.addActionListener(new ActionListener3());   	 	
   	 	menu.add(item3);
   	 	JMenuItem item4 = new JMenuItem("Clear selection");
   	 	item4.setIcon(app.getImageIcon("mode_showhideobject_16.gif"));
   	 	item4.setBackground(bgColor);
   	 	item4.addActionListener(new ActionListener4());   	 	
   	 	menu.add(item4);
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
 			table.copyPasteCut.copy(column1, row1, column2, row2);
 			table.copyPasteCut.delete(column1, row1, column2, row2);
 		}
	}
    	
	public static class ActionListener4 implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			table.copyPasteCut.delete(column1, row1, column2, row2);
 		}
	}
	
	public static class ActionListener5 implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 		}
	}
    	
	public static void showPopupMenu(MyTable table0, Component comp, int column01, int row01, int column02, int row02, int x, int y) {
		table = table0;
		Application app = table.kernel.getApplication();
		if (menu == null) {
			menu = initMenu(app);		
		}
		column1 = column01;
		column2 = column02;
		row1 = row01;
		row2 = row02;
		menu.show(comp, x, y);
	}

}
