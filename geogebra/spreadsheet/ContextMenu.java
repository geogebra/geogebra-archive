
package geogebra.spreadsheet;

import geogebra.Application;
import geogebra.kernel.GeoElement;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class ContextMenu extends JPopupMenu
{
	
	private static final long serialVersionUID = -7749575525048631798L;

	final static Color bgColor = Color.white;

	protected MyTable table = null;
	protected int row1 = -1;
	protected int row2 = -1;
	protected int column1 = -1;
	protected int column2 = -1;
	protected boolean[] selected = null;
	
	protected Application app;
	
	public ContextMenu(MyTable table0, int column01, int row01, int column02, int row02) {
		//Application.debug("showPopupMenu <<<<<<<<<<<<<<<<<<<");
		table = table0;
		column1 = column01;
		column2 = column02;
		row1 = row01;
		row2 = row02;		
		app = table.kernel.getApplication();
		
		initMenu();			
	}

	
	protected void initMenu() {
   	 	//JCheckBoxMenuItem item1 = new JCheckBoxMenuItem( app.getPlain("ShowObject"));
   	 	JMenuItem item1 = new JMenuItem(app.getMenu("Copy"));
   	 	item1.setIcon(app.getImageIcon("edit-copy.png"));
   	 	item1.addActionListener(new ActionListenerCopy());
   	 	add(item1);
   	 	JMenuItem item2 = new JMenuItem(app.getMenu("Paste"));
   	 	item2.setIcon(app.getImageIcon("edit-paste.png"));
   	 	item2.addActionListener(new ActionListenerPaste());   	 	
   	 	add(item2);
   	 	JMenuItem item3 = new JMenuItem(app.getMenu("Cut"));
   	 	item3.setIcon(app.getImageIcon("edit-cut.png"));
   	 	item3.addActionListener(new ActionListenerCut());   	 	
   	 	add(item3);
   	 	JMenuItem item4 = new JMenuItem(app.getMenu("Delete"));
   	 	item4.setIcon(app.getImageIcon("delete_small.gif"));
   	 	item4.addActionListener(new ActionListenerClear());
   	 	add(item4);

	 	addSeparator();

	 		if (column1 + 1 <= column2) {
   	 		
   	 		JMenuItem item5 = new JMenuItem(app.getMenu("CreateListOfPoints"));
   	   	 	item5.setIcon(app.getEmptyIcon());
   	   	 	item5.addActionListener(new ActionListenerCreatePoints());
   	   	 	add(item5);
	 		}   	   	 	
   	   	 	
	 		if (column1 !=-1 && column2 !=-1 && row1 != -1 && row2 != -1) {
	   	   	 	JMenuItem item6 = new JMenuItem(app.getMenu("CreateMatrix"));
	   	   	 	item6.setIcon(app.getEmptyIcon());
	   	   	 	item6.addActionListener(new ActionListenerCreateMatrix());
	   	   	 	add(item6);
	 		}
   	   	 	
	 		if ((column1 == column2 && column1 !=-1) || (row1 == row2 && row1 != -1)) {
	   	   	 	JMenuItem item7 = new JMenuItem(app.getMenu("CreateList"));
	   	   	 	item7.setIcon(app.getEmptyIcon());
	   	   	 	item7.addActionListener(new ActionListenerCreateList());
	   	   	 	add(item7);
	 		}
	 		
	 		if (app.selectedGeosSize() > 0) {

			 	addSeparator();
	
			 	JMenuItem item8 = new JMenuItem(app.getMenu("Properties")+"...");
		   	 	item8.setIcon(app.getImageIcon("document-properties.png"));
		   	 	item8.addActionListener(new ActionListenerProperties());
		   	 	add(item8);
	 		}
	}
	
	public class ActionListenerCopy implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			table.copyPasteCut.copy(column1, row1, column2, row2, false);
 		}
	}
    	
	public class ActionListenerPaste implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			boolean succ = table.copyPasteCut.paste(column1, row1, column2, row2);
 			if (succ) app.storeUndoInfo();
 			table.getView().getRowHeader().revalidate(); 		
 		}
	}
    	
	public class ActionListenerCut implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			boolean succ = table.copyPasteCut.cut(column1, row1, column2, row2);
 			if (succ) app.storeUndoInfo();
 		}
	}
    	
	public class ActionListenerClear implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			boolean succ = table.copyPasteCut.delete(column1, row1, column2, row2);
 			if (succ) app.storeUndoInfo();
 		}
	}
	
	
	
	private class ActionListenerCreatePoints implements ActionListener
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
	 	   	 				String text = "(" + v1.getLabel() + "," + v2.getLabel() + ")";
	 	   	 				GeoElement [] geos = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, false);
	 	   	 				// set label P_1, P_2, etc.
	 		   	 		    String pointName = geos[0].getIndexLabel("P");
	 		   	 		    geos[0].setLabel(pointName);
	 		   	 			list.addLast(pointName);
	 	   	 			}
	 	   	 		}
	 			}
	
	 			if (list.size() > 0) {
	 				String[] points = (String[])list.toArray(new String[0]);	 				
	 				String text = "{";
	 				for (int i = 0; i < points.length; ++ i) {
	 					text += points[i];
	 					if (i != points.length - 1) text += ",";
	 				}
	 				text += "}";
	 				GeoElement[] values = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, false);
	 				// set list label 
		   	 		String listName = values[0].getIndexLabel("L");
		   	 		values[0].setLabel(listName);
	 				
	 				for (int i = 0; i < values.length; ++ i) {
	 					values[i].setAuxiliaryObject(true);
	 				}
	 			}
	 			
	 			app.storeUndoInfo();
 			} catch (Exception ex) {
 				// Just abort the process
 			} 
		}
	}
	
	private class ActionListenerCreateMatrix implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			Application.debug("CreateMatrix " + column1 + " - " + column2+"   "+row1+" - "+row2);
 			//if (selected == null) throw new RuntimeException("error state");
 			String text="";
 			try {
 		 			text="{";
 					for (int j = column1; j <= column2; ++ j) {
 						//if (selected.length > j && ! selected[j])  continue; 	
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
   	 				GeoElement [] geos = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, false);
   	 				// set matrix label
	   	 		    String matrixName = geos[0].getIndexLabel("matrix");
	   	 		    geos[0].setLabel(matrixName);
	
	   	 		app.storeUndoInfo();
 			} 
 			catch (Exception ex) {
 				Application.debug("creating matrix failed "+text);
 				ex.printStackTrace();
 			} 

		}
	}
	
	private class ActionListenerProperties implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			app.showPropertiesDialog();	
 		}
	}
	
	private class ActionListenerCreateList implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			Application.debug("CreateList " + column1 + " - " + column2+"   "+row1+" - "+row2);
 			String text="";
 			try {
	 			if (row1 == row2)
	 			{
			 			text="{";
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
		   	 			GeoElement [] geos = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, false);
		   		   		
		   		   		// set list name
		   	 		    String listName = geos[0].getIndexLabel("L");
		   	 		    geos[0].setLabel(listName);	 				
	 			}
	 			else if (column1 == column2)
	 			{
		 			text="{";
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
		 				GeoElement [] geos = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, false);
		 		   		
		 		   		// set list name
		 	 		    String listName = geos[0].getIndexLabel("L");
		 	 		    geos[0].setLabel(listName);
	 			}
	 			else
	 			{
	 				Application.debug("creating list failed "+text);	
	 				return;
	 			}
 			
	 			app.storeUndoInfo();
 			} 
 			catch (Exception ex) {
 				Application.debug("creating list failed with exception "+text);
 			} 
		}
	}
	
	private static String removeComma(String s)
	{
		if (s.endsWith(",")) s = s.substring(0,s.length()-1); 	
		return s;
	}
    	
	
}
