
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
	
	private static Application app;
	
	protected static JPopupMenu initMenu(Application app2) {
		app=app2;
		JPopupMenu menu = new JPopupMenu();
   	 	//JCheckBoxMenuItem item1 = new JCheckBoxMenuItem( app.getPlain("ShowObject"));
   	 	JMenuItem item1 = new JMenuItem(app.getMenu("Copy"));
   	 	item1.setIcon(app.getImageIcon("edit-copy.png"));
   	 	item1.addActionListener(new ActionListenerCopy());
   	 	menu.add(item1);
   	 	JMenuItem item2 = new JMenuItem(app.getMenu("Paste"));
   	 	item2.setIcon(app.getImageIcon("edit-paste.png"));
   	 	item2.addActionListener(new ActionListenerPaste());   	 	
   	 	menu.add(item2);
   	 	JMenuItem item3 = new JMenuItem(app.getMenu("Cut"));
   	 	item3.setIcon(app.getImageIcon("edit-cut.png"));
   	 	item3.addActionListener(new ActionListenerCut());   	 	
   	 	menu.add(item3);
   	 	JMenuItem item4 = new JMenuItem(app.getMenu("ClearSelection"));
   	 	item4.setIcon(app.getImageIcon("edit-clear.png"));
   	 	item4.addActionListener(new ActionListenerClear());
   	 	menu.add(item4);

	 	menu.addSeparator();

	 		if (column1 + 1 <= column2) {
   	 		
   	 		JMenuItem item5 = new JMenuItem(app.getMenu("CreateListOfPoints"));
   	   	 	item5.setIcon(app.getEmptyIcon());
   	   	 	item5.addActionListener(new ActionListenerCreatePoints());
   	   	 	menu.add(item5);
	 		}   	   	 	
   	   	 	
	 		if (column1 !=-1 && column2 !=-1 && row1 != -1 && row2 != -1) {
	   	   	 	JMenuItem item6 = new JMenuItem(app.getMenu("CreateMatrix"));
	   	   	 	item6.setIcon(app.getEmptyIcon());
	   	   	 	item6.addActionListener(new ActionListenerCreateMatrix());
	   	   	 	menu.add(item6);
	 		}
   	   	 	
	 		if (column1 == column2 || row1 == row2) {
	   	   	 	JMenuItem item7 = new JMenuItem(app.getMenu("CreateList"));
	   	   	 	item7.setIcon(app.getEmptyIcon());
	   	   	 	item7.addActionListener(new ActionListenerCreateList());
	   	   	 	menu.add(item7);
	 		}
	 		
	 		if (app.selectedGeosSize() > 0) {

			 	menu.addSeparator();
	
			 	JMenuItem item8 = new JMenuItem(app.getMenu("Properties")+"...");
		   	 	item8.setIcon(app.getImageIcon("document-properties.png"));
		   	 	item8.addActionListener(new ActionListenerProperties());
		   	 	menu.add(item8);
	 		}
		 	
   	 	return menu;
	}
	
	public static class ActionListenerCopy implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			table.copyPasteCut.copy(column1, row1, column2, row2, false);
 		}
	}
    	
	public static class ActionListenerPaste implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			table.copyPasteCut.paste(column1, row1, column2, row2);
 			table.getView().getRowHeader().revalidate(); 		
 		}
	}
    	
	public static class ActionListenerCut implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			table.copyPasteCut.cut(column1, row1, column2, row2);
 		}
	}
    	
	public static class ActionListenerClear implements ActionListener
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
	
	protected static int matrixNameCount = 0;
	
	public static String getNextMatrixName() {
		++ matrixNameCount;
		if (matrixNameCount < 10) {
			return "matrix_" + matrixNameCount;		
		}
		return "matrix_{" + matrixNameCount + "}";
	}
	
	protected static int listNameCount = 0;
	
	public static String getNextListName() {
		++ listNameCount;
		if (listNameCount < 10) {
			return "L_" + listNameCount;		
		}
		return "L_{" + listNameCount + "}";
	}
	
	public static class ActionListenerCreatePoints implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			Application.debug("CreatePoints " + column1 + " - " + column2+"   "+row1+" - "+row2);
 			if (selected == null) throw new RuntimeException("error state");
 			LinkedList list = new LinkedList();
 			try {
 				int xColumn = -1;
	 			for (int j = column1; j <= column2; ++ j) {
	 				if (selected.length > j && ! selected[j]) {
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
	
	public static class ActionListenerCreateMatrix implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			Application.debug("CreateMatrix " + column1 + " - " + column2+"   "+row1+" - "+row2);
 			if (selected == null) throw new RuntimeException("error state");
 			String text="";
 			try {
 		 			text=getNextMatrixName()+" = {";
 					for (int j = column1; j <= column2; ++ j) {
 						if (selected.length > j && ! selected[j])  continue; 	
 						String row = "{";
		  	   	 		for (int i = row1; i <= row2; ++ i) {
		 	   	 			GeoElement v2 = RelativeCopy.getValue(table, j, i);
		 	   	 			if (v2 != null) {
		 	   	 				row += v2.getLabel() + ",";
		 	   	 			}
		 	   	 			else {
		 	    				app.showErrorDialog(app.getPlain("CellAisNotDefined",GeoElement.getSpreadsheetCellName(j,i)));
		 	   		 	   	 	return;
		 	   	 			}
		 	   	 		}
		  	   	 		row = removeComma(row);
		  	   	 		text += row +"}" + ",";
 					}
 					
	  	   	 		text = removeComma(text)+ "}";
 					
 					Application.debug(text);
   	 				table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, false);

	
 			} catch (Exception ex) {
 				Application.debug("creating matrix failed "+text);
 				ex.printStackTrace();
 			} finally {
 				table.kernel.storeUndoInfo();
 			}

		}
	}
	
	public static class ActionListenerProperties implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			app.showPropertiesDialog();	
 		}
	}
	
	public static class ActionListenerCreateList implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			Application.debug("CreateList " + column1 + " - " + column2+"   "+row1+" - "+row2);
 			String text="";
 			try {
	 			if (row1 == row2)
	 			{
			 			text=getNextListName()+" = {";
	 					for (int j = column1; j <= column2; ++ j) {
			 	   	 			GeoElement v2 = RelativeCopy.getValue(table, j, row1);
			 	   	 			if (v2 != null) {
			 	   	 				text += v2.getLabel() + ",";
			 	   	 			}
			 	   	 			else {
			 	    				app.showErrorDialog(app.getPlain("CellAisNotDefined",GeoElement.getSpreadsheetCellName(j,row1)));
			 	   		 	   	 	return;
			 	   	 			}
				}
	 					
		  	   	 		text = removeComma(text)+ "}";
	 					
	 					Application.debug(text);
	   	 				table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, false);
	 				
	 			}
	 			else if (column1 == column2)
	 			{
		 			text=getNextListName()+" = {";
						for (int j = row1; j <= row2; ++ j) {
		 	   	 			GeoElement v2 = RelativeCopy.getValue(table, column1, j);
		 	   	 			if (v2 != null) {
		 	   	 				text += v2.getLabel() + ",";
		 	   	 			}
		 	   	 			else {
		 	    				app.showErrorDialog(app.getPlain("CellAisNotDefined",GeoElement.getSpreadsheetCellName(column1,j)));
		 	   		 	   	 	return;
		 	   	 			}
						}
						
	  	   	 		text = removeComma(text)+ "}";
						
						Application.debug(text);
		 				table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, false);
	 				
	 			}
	 			else
	 			{
	 				Application.debug("creating list failed "+text);				
	 			}
 			
 			} catch (Exception ex) {
 				Application.debug("creating list failed with exception "+text);
 			} finally {
 				table.kernel.storeUndoInfo();
 			}

		}
	}
	
	private static String removeComma(String s)
	{
		if (s.endsWith(",")) s = s.substring(0,s.length()-1); 	
		return s;
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
