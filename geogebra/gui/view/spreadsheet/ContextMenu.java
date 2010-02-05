
package geogebra.gui.view.spreadsheet;

import geogebra.kernel.GeoElement;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.TableModel;

public class ContextMenu extends JPopupMenu
{
	
	private static final long serialVersionUID = -7749575525048631798L;

	final static Color bgColor = Color.white;

	protected MyTable table = null;
	protected int row1 = -1;
	protected int row2 = -1;
	protected int column1 = -1;
	protected int column2 = -1;
	protected boolean[] selectedColumns = null;
	
	//G.Sturr 2009-9-30

	private ArrayList<CellRange> selectedCellRanges;
	private int selectionType;
	// end G.Sturr
	
	
	protected Application app;
	
	
	
	// G.Sturr 2009-10-3: Added selection type parameter.
	// Allows a single context menu to serve rows, columns and cells
	public ContextMenu(MyTable table0, int column01, int row01, int column02, int row02, 
			boolean[] selected0,int selectionType0) {
			
		//Application.debug("showPopupMenu <<<<<<<<<<<<<<<<<<<");
		table = table0;
		column1 = column01;
		column2 = column02;
		row1 = row01;
		row2 = row02;		
		selectedColumns = selected0;
		app = table.kernel.getApplication();
				
		selectionType = table.getSelectionType();  //G.Sturr 2009-10-3
		selectedCellRanges = table.selectedCellRanges;
		
		
		initMenu();			
	}

	
	protected void initMenu() {
   	 	//JCheckBoxMenuItem item1 = new JCheckBoxMenuItem( app.getPlain("ShowObject"));
		
		//G.Sturr 2009-10-3: Added menu title  
		ArrayList geos = app.getSelectedGeos();
   	 		
   	 	//title = cell range if empty or multiple cell selection
		String title = GeoElement.getSpreadsheetCellName(column1, row1);
		if(column1 != column2 || row1 != row2){
			title += ":" + GeoElement.getSpreadsheetCellName(column2, row2);
		} 
		// title = geo description if single geo in cell  
		else if (geos.size() == 1){	 
			GeoElement geo0 = (GeoElement) geos.get(0);
			title = geo0.getLongDescriptionHTML(false, true);
	        if (title.length() > 80)
	        	title = geo0.getNameDescriptionHTML(false, true);          
		}
		setTitle(title);  
		// end G.Sturr
		
		
		// Cut Copy Paste Delete 
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
   	 	
   	 	
   	 	// don't show "Delete" if all selected objects fixed
   	 	// ArrayList geos = app.getSelectedGeos();   (G.Sturr: geos now declared above)   
   	 	
   	 	boolean allFixed = true;
   	 	
   	 	if (geos != null && geos.size() >0) {
   	 		for (int i = 0 ; (i < geos.size() && allFixed) ; i++) {
   	 			GeoElement geo = (GeoElement)geos.get(i);
   	 			if (!geo.isFixed()) allFixed = false;
   	 		}
   	 	}
   	 	
   	 	if (!allFixed) {	 	
	   	 	JMenuItem item4 = new JMenuItem(app.getMenu("Delete"));
	   	 	item4.setIcon(app.getImageIcon("delete_small.gif"));
	   	 	item4.addActionListener(new ActionListenerClear());
	   	 	add(item4);
   	 	}
	 	addSeparator();
	 	
	 	
	 	// G.Sturr 20009-10-2  
	 	// Insert (insert new row or new column)
	 	
	 	if(selectionType == table.COLUMN_SELECT){
		 	JMenuItem itemInsertLeft = new JMenuItem(app.getMenu("InsertLeft"));
		 	itemInsertLeft.setIcon(app.getEmptyIcon());
		 	itemInsertLeft.addActionListener(new InsertLeft());   	 	
	   	 	add(itemInsertLeft);
	   	 	JMenuItem itemInsertRight = new JMenuItem(app.getMenu("InsertRight"));
	   	 	itemInsertRight.setIcon(app.getEmptyIcon());
	   	 	itemInsertRight.addActionListener(new InsertRight());   	 	
	   	 	add(itemInsertRight);
			addSeparator();
	 	}
	 	
	 	if(selectionType == table.ROW_SELECT){
		 	JMenuItem itemInsertAbove = new JMenuItem(app.getMenu("InsertAbove"));
		 	itemInsertAbove.setIcon(app.getEmptyIcon());
		 	itemInsertAbove.addActionListener(new InsertAbove());   	 	
	   	 	add(itemInsertAbove);
	   	 	JMenuItem itemInsertBelow = new JMenuItem(app.getMenu("InsertBelow"));
	   	 	itemInsertBelow.setIcon(app.getEmptyIcon());
	   	 	itemInsertBelow.addActionListener(new InsertBelow());   	 	
	   	 	add(itemInsertBelow);
			addSeparator();
	 	}
	 	
	 	
	 	// Create (Lists, Matrix, etc.) 	
	 	if (table.getCellRangeProcessor().isCreatePointListPossible(selectedCellRanges))  {
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
   	 	
 		//if ((column1 == column2 && column1 !=-1) || (row1 == row2 && row1 != -1)) {
 		if (!isEmptySelection()) {
   	   	 	JMenuItem item7 = new JMenuItem(app.getMenu("CreateList"));
   	   	 	item7.setIcon(app.getEmptyIcon());
   	   	 	item7.addActionListener(new ActionListenerCreateList());
   	   	 	add(item7);
 		}
	 	
	 		
	 	// Object Properties 	
 		if (app.selectedGeosSize() > 0) {
		 	addSeparator();
		 	JMenuItem item8 = new JMenuItem(app.getMenu(app.getPlain("Properties"))+"...");
	   	 	item8.setIcon(app.getImageIcon("document-properties.png"));
	   	 	item8.addActionListener(new ActionListenerProperties());
	   	 	add(item8);
 		}
 		
 		//G.STURR 2010-1-11
 	    // Import Data	
 		if (app.selectedGeosSize() >= 0) {
		 	addSeparator();
		 	JMenuItem item9 = new JMenuItem(app.getMenu(app.getPlain("Import Data"))+"...");
	   	 	item9.setIcon(app.getEmptyIcon());
	   	 	item9.addActionListener(new ActionListenerImportData());
	   	 	add(item9);
	   	 //END GSTURR
 		}
 		
	}
	
	//G.Sturr 2009-10-3: added setTitle (copied from gui.ContextMenuGeoElement)
	void setTitle(String str) {
    	JLabel title = new JLabel(str);
        title.setFont(app.getBoldFont());                      
        title.setBackground(bgColor);        
                
        title.setBorder(BorderFactory.createEmptyBorder(5, 10, 2, 5));      
        add(title);
        addSeparator();   
        
        title.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
        		setVisible(false);
        	}
        });
        
    }
	// end G.Sturr
	
	
	
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
	
	
	//G.Sturr 2009-10-9: Insert rows and columns. (Copied from old ContextMenuCol and ContextMenuRow)
	
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
	
 	//
	// end G.Sturr
	
	
	// G.STURR 2010-1029: Create list now done by CellRangeProcessor
	private class ActionListenerCreatePoints implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			if (table.getCellRangeProcessor().isCreatePointListPossible(selectedCellRanges)) {

				table.getCellRangeProcessor().CreatePointList(selectedCellRanges, true, true);
			}
		}
	}
	
	// OLD CREATE POINTS --- to be removed
	private class ActionListenerCreatePointsOLD implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			
 			
 			//Application.debug("CreatePoints " + column1 + " - " + column2+"   "+row1+" - "+row2);
 			//if (selected == null) throw new RuntimeException("error state");
 			StringBuilder text = new StringBuilder();
 			LinkedList list = new LinkedList();
 			TableModel model = table.getModel();
 			
			//String s="columnsSelected ";
			//	for (int i=0 ; i<selectedColumns.length ; i++)
			//		if (selectedColumns[i]) s=s+"1"; else s=s+"0";
			//	Application.debug(s);
 			
 			boolean error = false;
				
 			try {
 				
 				if (row1 == -1 && row2 == -1)
 				{
 					Application.debug("multiple columns selected");
 					int r1 = 0;
 					int r2 = model.getRowCount();
 					
 					int c1=-1, c2=-1;
 					for (int i=0 ; i<selectedColumns.length ; i++)
 					{
 						if (selectedColumns[i])
 						{
 							if (c1 == -1) 
 								c1=i;
 							else if (c2 == -1)
 							{
 								c2=i;
 								break;
 							}
 						}
 					}
 		 			Application.debug(c1 + " - " + c2+"   "+r1+" - "+r2);
 					
	  	   	 		for (int i = r1; i <= r2; ++ i) {
	 	   	 			GeoElement v1 = RelativeCopy.getValue(table, c1, i);
	 	   	 			GeoElement v2 = RelativeCopy.getValue(table, c2, i);
	 	   	 			if (v1 != null && v2 != null && (!v1.isGeoNumeric() || !v2.isGeoNumeric())) 
	 	   	 				error = true;
		 	   	 		if (v1 != null && v2 != null && v1.isGeoNumeric() && v2.isGeoNumeric()) {	 
	 	   	 				
	 	   	 				text.setLength(0);
	 	   	 				text.append("(");
	 	   	 				text.append(v1.getLabel());
	 	   	 				text.append(",");
	 	   	 				text.append(v2.getLabel());
	 	   	 				text.append(")");
	 	   	 				
	 	   	 				GeoElement [] geos = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text.toString(), false);
	 	   	 				// set label P_1, P_2, etc.
	 		   	 		    String pointName = geos[0].getIndexLabel("P");
	 		   	 		    geos[0].setLabel(pointName);
	 		   	 		    geos[0].setAuxiliaryObject(true);
	 		   	 		    list.addLast(pointName);
	 	   	 			}
	 	   	 		}

 					
 				}
 				
 				else if (column2 - column1 == 1)
 				{
 					Application.debug("adjacent columns selected");
 					int r1 = row1;
 					int r2 = row2;
 					int c1 = column1;
 					int c2 = column2;
 					
	  	   	 		for (int i = r1; i <= r2; ++ i) {
	 	   	 			GeoElement v1 = RelativeCopy.getValue(table, c1, i);
	 	   	 			GeoElement v2 = RelativeCopy.getValue(table, c2, i);
	 	   	 			if (v1 != null && v2 != null && (!v1.isGeoNumeric() || !v2.isGeoNumeric())) 
	 	   	 				error = true;
	 	   	 			if (v1 != null && v2 != null && v1.isGeoNumeric() && v2.isGeoNumeric()) {	 
	 	   	 				
	 	   	 				text.setLength(0);
	 	   	 				text.append("(");
	 	   	 				text.append(v1.getLabel());
	 	   	 				text.append(",");
	 	   	 				text.append(v2.getLabel());
	 	   	 				text.append(")");
	 	   	 				
	 	   	 				GeoElement [] geos = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text.toString(), false);
	 	   	 				// set label P_1, P_2, etc.
	 		   	 		    String pointName = geos[0].getIndexLabel("P");
	 		   	 		    geos[0].setLabel(pointName);
	 		   	 		    geos[0].setAuxiliaryObject(true);
	 		   	 		    list.addLast(pointName);
	 	   	 			}
	 	   	 		}
 					
 				}
 				else if (row2 - row1 == 1)
 				{
 					Application.debug("adjacent rows selected");
 					int r1 = row1;
 					int r2 = row2;
 					int c1 = column1;
 					int c2 = column2;
 					
	  	   	 		for (int i = c1; i <= c2; ++ i) {
	 	   	 			GeoElement v1 = RelativeCopy.getValue(table, i, r1);
	 	   	 			GeoElement v2 = RelativeCopy.getValue(table, i, r2);
	 	   	 			if (v1 != null && v2 != null && (!v1.isGeoNumeric() || !v2.isGeoNumeric())) 
	 	   	 				error = true;
	 	   	 			if (v1 != null && v2 != null && v1.isGeoNumeric() && v2.isGeoNumeric()) {	 
	 	   	 				
	 	   	 				text.setLength(0);
	 	   	 				text.append("(");
	 	   	 				text.append(v1.getLabel());
	 	   	 				text.append(",");
	 	   	 				text.append(v2.getLabel());
	 	   	 				text.append(")");
	 	   	 				
	 	   	 				GeoElement [] geos = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text.toString(), false);
	 	   	 				// set label P_1, P_2, etc.
	 		   	 		    String pointName = geos[0].getIndexLabel("P");
	 		   	 		    geos[0].setLabel(pointName);
	 		   	 		    geos[0].setAuxiliaryObject(true);
	 		   	 			list.addLast(pointName);
	 	   	 			}
	 	   	 		}
 					
 				}
 				
 				
 				/*
 				int xColumn = -1;
	 			for (int j = column1; j <= column2; ++ j) {
	 				//if (selected.length > j && ! selected[j]) {
	 				//	continue;
	 				//}
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
	 			}*/
	
	 			if (list.size() > 0) {
	 				String[] points = (String[])list.toArray(new String[0]);
	 				text.setLength(0);
	 				text.append("{");
	 				for (int i = 0; i < points.length; ++ i) {
	 					text.append(points[i]);
	 					if (i != points.length - 1) text.append(",");
	 				}
	 				text.append("}");
	 				Application.debug(text.toString());
	 				//GeoElement[] values = 
	 				table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text.toString(), false);
	 				// set list label 
		   	 		//String listName = values[0].getIndexLabel(app.getPlain("Name.list"));
		   	 		//values[0].setLabel(listName);
	 				
		   	 		// DON'T want the list to be auxiliary
	 				//for (int i = 0; i < values.length; ++ i) {
	 				//	values[i].setAuxiliaryObject(true);
	 				//}
	 			}
	 			
	 			if (error)
	 				app.showError("NumberExpected");
	 			
	 			app.storeUndoInfo();
 			} catch (Exception ex) {
 				// Just abort the process
	 			if (error)
	 				app.showError("NumberExpected");
 			} 
		}
	}
	
	private class ActionListenerCreateMatrix implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			//Application.debug("CreateMatrix " + column1 + " - " + column2+"   "+row1+" - "+row2);
 			//if (selected == null) throw new RuntimeException("error state");
 			String text="";
 			try {
 		 			text="{";
 					for (int j = row1; j <= row2; ++ j) {
 						//if (selected.length > j && ! selected[j])  continue; 	
 						String row = "{";
		  	   	 		for (int i = column1; i <= column2; ++ i) {
		 	   	 			GeoElement v2 = RelativeCopy.getValue(table, i, j);
		 	   	 			if (v2 != null) {
		 	   	 				row += v2.getLabel() + ",";
		 	   	 			}
		 	   	 			else {
		 	    				app.showErrorDialog(app.getPlain("CellAisNotDefined",GeoElement.getSpreadsheetCellName(i,j)));
		 	   		 	   	 	return;
		 	   	 			}
		 	   	 		}
		  	   	 		row = removeComma(row);
		  	   	 		text += row +"}" + ",";
 					}
 					
	  	   	 		text = removeComma(text)+ "}";
 					
 					//Application.debug(text);
   	 				GeoElement [] geos = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, false);
   	 				// set matrix label
	   	 		    // no longer needed
   	 				//String matrixName = geos[0].getIndexLabel("matrix");
	   	 		    //geos[0].setLabel(matrixName);
	
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
 			app.getGuiManager().showPropertiesDialog();	
 		}
	}
	
	// G.STURR 2010-1029: Create list now done by CellRangeProcessor
	private class ActionListenerCreateList implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			table.getCellRangeProcessor().CreateList(selectedCellRanges, true, true);
 		}
	}
	
	
	// OLD CREATE LIST -- to be removed
	private class ActionListenerCreateList_OLD implements ActionListener
	{
 		public void actionPerformed(ActionEvent e) {
 			Application.debug("CreateList " + column1 + " - " + column2+"   "+row1+" - "+row2);
 			
 			TableModel model = table.getModel();
 			int r1 = (row1 == -1 ? 0 : row1);
 			int r2 = (row2 == -1 ? model.getRowCount() : row2);
 			
 			int c1 = (column1 == -1 ? 0 : column1);
 			int c2 = (column2 == -1 ? model.getColumnCount() : column2);
 			
 			String text="";
 			try {
	 			if (r1 == r2)
	 			{
			 			text="{";
	 					for (int j = c1; j <= c2; ++ j) {
			 	   	 			GeoElement v2 = RelativeCopy.getValue(table, j, r1);
			 	   	 			if (v2 != null) {
			 	   	 				text += v2.getLabel() + ",";
			 	   	 			}
			 	   	 			else {
			 	    				//app.showErrorDialog(app.getPlain("CellAisNotDefined",GeoElement.getSpreadsheetCellName(j,row1)));
			 	   		 	   	 	//return;
			 	   	 			}
				}
	 					
		  	   	 		text = removeComma(text)+ "}";
	 					
	 					Application.debug(text);	   	 				
		   	 			GeoElement [] geos = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, false);
		   		   		
		   		   		// set list name
		   	 		    String listName = geos[0].getIndexLabel("L");
		   	 		    geos[0].setLabel(listName);	 				
	 			}
	 			else if (c1 == c2)
	 			{
		 			text="{";
						for (int j = r1; j <= r2; ++ j) {
		 	   	 			GeoElement v2 = RelativeCopy.getValue(table, c1, j);
		 	   	 			if (v2 != null) {
		 	   	 				text += v2.getLabel() + ",";
		 	   	 			}
		 	   	 			else {
		 	    				//app.showErrorDialog(app.getPlain("CellAisNotDefined",GeoElement.getSpreadsheetCellName(column1,j)));
		 	   		 	   	 	//return;
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
	
	private class ActionListenerImportData implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			
			//File dataFile = new File("C:testTab.txt");
			File dataFile = app.getGuiManager().getDataFile();
			

			// paste file into spreadsheet
			boolean succ = table.copyPasteCut.pasteFromFile(dataFile);
			if (succ){
				app.storeUndoInfo();
			}
		}
	}

	//G.STURR 2010-1-29
	private boolean isEmptySelection(){
		return (app.getSelectedGeos().isEmpty()) ;
	}
	// END G.STURR
	
	private static String removeComma(String s)
	{
		if (s.endsWith(",")) s = s.substring(0,s.length()-1); 	
		return s;
	}
    	
	
}
