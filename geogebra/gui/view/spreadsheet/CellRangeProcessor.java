package geogebra.gui.view.spreadsheet;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunctionNVar;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;

/**
 * 
 * Utility class with methods for processing cell ranges (e.g inserting rows,
 * creating lists of cells). Typical usage is via the instance of this class
 * created by the constructor of MyTable.
 * 
 * @author G. Sturr
 * 
 */
public class CellRangeProcessor {

	private MyTable table;
	private Application app;		
	
	public CellRangeProcessor(MyTable table) {
		
		this.table = table;
		app = table.kernel.getApplication();
		
	}
	
	
	
	//===============================================
	//            Validation
	//===============================================
	
	public boolean isCreatePointListPossible(ArrayList<CellRange> rangeList) {

		if (rangeList.size() == 1 && rangeList.get(0).is2D())
			return true;
		if (rangeList.size() == 2 && 
				rangeList.get(0).getWidth() == 1 && rangeList.get(1).getWidth() == 1 )
			return true;

		if (rangeList.size() == 2 && 
				rangeList.get(0).getHeight() == 1 && rangeList.get(1).getHeight() == 1 )
			return true;
		
		return false;
	}


	public boolean isCreateMatrixPossible(ArrayList<CellRange> rangeList){
		
		if(rangeList.size() == 1 && !rangeList.get(0).hasEmptyCells()) 
			return true;	
		else
			return false;
		
		/*
		// ctrl-selection block 
		if (rangeList.size() > 1){							 
				//rangeList.get(0).getWidth() == 1 && rangeList.get(1).getWidth() == 1 )
			return true;
		}

		if (rangeList.size() == 2 && 
				rangeList.get(0).getHeight() == 1 && rangeList.get(1).getHeight() == 1 )
			return true;
		
		return false;
		
		*/
		
	}
	
	
	/** Returns true if at least three cells in rangeList are nonempty  */
	public boolean isOneVarStatsPossible(ArrayList<CellRange> rangeList){

		if(rangeList == null || rangeList.size() == 0) return false; 
	
		int count = 0;
	    for(CellRange cr:rangeList)
			for (int col = cr.getMinColumn(); col <= cr.getMaxColumn(); ++col) {
				for (int row = cr.getMinRow(); row <= cr.getMaxRow(); ++row) {
					GeoElement geo = RelativeCopy.getValue(table, col, row);
					if (geo != null) ++count;
					if(count > 2) return true;
					}
				}
			
		return false;
			
	}
	
	
	
	
	
	
	
	
	//====================================================
	//           Create Lists from Cells
	//====================================================
	
	
	
	public GeoElement createPointList(ArrayList<CellRange> rangeList, boolean byValue, boolean leftToRight) {
		
		return  createPointList(rangeList, byValue, leftToRight, false, true);
		
	}
	
	
	public GeoElement createPointList(ArrayList<CellRange> rangeList, boolean byValue, boolean leftToRight,
			boolean isSorted, boolean doStoreUndo) {
		
		
		GeoElement[] geos = null;
		
		int c1, c2, r1, r2;
		StringBuilder list = new StringBuilder();
		//LinkedList<String> list = new LinkedList<String>();
		boolean doHorizontalPairs = true;
		boolean isError = false;
		
		// note: we assume that rangeList has passed the isCreatePointListPossible() test 
		
		list.append("{");
		try {			
			// Determine if pairs are joined vertically or horizontally and get the 
			// row column indices needed to traverse the cells.
			
			// CASE 1: selection is contiguous and 2D
			if (rangeList.size() == 1) {

				doHorizontalPairs = rangeList.get(0).getWidth() == 2;
				c1 = rangeList.get(0).getMinColumn();
				c2 = rangeList.get(0).getMaxColumn();
				r1 = rangeList.get(0).getMinRow();
				r2 = rangeList.get(0).getMaxRow();

			
			// CASE 2: non-contiguous with two ranges (either single row or single column)
			} else {

				if(rangeList.get(0).getWidth() == 1 && rangeList.get(1).getWidth() == 1){
					doHorizontalPairs = true;
					// we are traversing down columns. so get min and max column indices
					c1 = Math.min(rangeList.get(0).getMinColumn(), rangeList.get(1).getMinColumn());
					c2 = Math.max(rangeList.get(0).getMaxColumn(), rangeList.get(1).getMaxColumn());
					// but get the max-min and min-max row indices in case the columns don't line up
					r1 = Math.max(rangeList.get(0).getMinRow(), rangeList.get(1).getMinRow());
					r2 = Math.min(rangeList.get(0).getMaxRow(), rangeList.get(1).getMaxRow());
					
				}else{
					doHorizontalPairs = true;
					// we are traversing across rows. so get min and max row indices
					r1 = Math.min(rangeList.get(0).getMinRow(), rangeList.get(1).getMinRow());
					r2 = Math.max(rangeList.get(0).getMaxRow(), rangeList.get(1).getMaxRow());	
					// but get the max-min and min-max column indices in case the rows don't line up
					c1 = Math.max(rangeList.get(0).getMinColumn(), rangeList.get(1).getMinColumn());
					c2 = Math.min(rangeList.get(0).getMaxColumn(), rangeList.get(1).getMaxColumn());
								
				}
			}

			
			// Create points and store their names in list of strings
			if (doHorizontalPairs) {
				for (int i = r1; i <= r2; ++i) {
					GeoElement xCoord = RelativeCopy.getValue(table, c1, i);
					GeoElement yCoord = RelativeCopy.getValue(table, c2, i);
					createListPoint(xCoord, yCoord, list, isError, byValue);
				}

			} else {
				for (int i = c1; i <= c2; ++i) {
					GeoElement xCoord = RelativeCopy.getValue(table, i, r1);
					GeoElement yCoord = RelativeCopy.getValue(table, i, r2);
					createListPoint(xCoord, yCoord, list, isError, byValue);
				}
			}

			// finish processing the text
			list.deleteCharAt(list.length()-1);
			list.append("}");

			if(isSorted){
				list.insert(0, "Sort[" );
				list.append("]");
			}
			//System.out.println(list.toString());
			
			// convert the text to a GeoList
			geos = table.kernel.getAlgebraProcessor()
			.processAlgebraCommandNoExceptions(list.toString(), false);
			
			if(doStoreUndo)
				app.storeUndoInfo();
		}

		catch (Exception ex) {
			//app.showError("NumberExpected");
			Application.debug("Creating list of points failed with exception " + ex);
		}

		
		if(geos != null)
			return geos[0];
		else 
			return null;
		
	}
	
	private void createListPoint(GeoElement xCoord, GeoElement yCoord,
			StringBuilder sb, boolean isError, boolean byValue) {
		
		String pointString = "";
		String pointName = "";
		boolean isPolar = false;
		try {
			if (xCoord != null && yCoord != null
					&& (!xCoord.isGeoNumeric() || !yCoord.isGeoNumeric()))
				isError = true;
			
			

			// if both cells are non-empty and numeric then make a point
			if (xCoord != null && yCoord != null && xCoord.isGeoNumeric()
					&& yCoord.isGeoNumeric()) {

				// test for polar point
				isPolar = yCoord.isAngle();
				String separator = isPolar? ";" : ",";
				
				pointString = "(" + xCoord.getLabel() + separator + yCoord.getLabel() + ")";

				// create a GeoPoint from text	
				if(!byValue){
					GeoElement[] geos = table.kernel.getAlgebraProcessor()
					.processAlgebraCommandNoExceptions(pointString,false);

					pointName = geos[0].getIndexLabel("P");
					geos[0].setLabel(pointName);
					geos[0].setAuxiliaryObject(true);
					
					//convert to polar mode
					if(isPolar) 
						((GeoPoint)geos[0]).setPolar();
				}
				
				// add point to the list string
				if(byValue){
					sb.append(pointString);
				}else{
					sb.append(pointName);
				}
				sb.append(",");
			}

		} catch (Exception ex) {
			app.showError("NumberExpected");
		}

	}
	
	
	/* --- old code, to be removed 
	 
	 
	public void CreatePointList(ArrayList<CellRange> rangeList, boolean byValue, boolean leftToRight) {
		
		int c1, c2, r1, r2;
		StringBuilder text = new StringBuilder();
		LinkedList list = new LinkedList();
		boolean isError = false;
		String pointString = "";
		String pointName = "";

		try {
			//selection is contiguous and 2D  
			if (rangeList.size() == 1 && rangeList.get(0).is2D()) {
				c1 = rangeList.get(0).getMinColumn();
				c2 = c1 + 1;
				r1 = rangeList.get(0).getMinRow();
				r2 = rangeList.get(0).getMaxRow();

			//non-contiguous, so use the first two ranges	
			} else {
				c1 = Math.min(rangeList.get(0).getMinColumn(),rangeList.get(1).getMinColumn());
				c2 = Math.max(rangeList.get(0).getMinColumn(),rangeList.get(1).getMinColumn());
				r1 = Math.max(rangeList.get(0).getMinRow(),rangeList.get(1).getMinRow());
				r2 = Math.min(rangeList.get(0).getMaxRow(),rangeList.get(1).getMaxRow());
			}

			//System.out.println("(" + c1 + "," + r1 + ")  (" + c2 + "," + r2 + ")"  );
			
			for (int i = r1; i <= r2; ++i) {
				GeoElement xCoord = RelativeCopy.getValue(table, c1, i);
				GeoElement yCoord = RelativeCopy.getValue(table, c2, i);
				if (xCoord != null
						&& yCoord != null
						&& (!xCoord.isGeoNumeric() || !yCoord
								.isGeoNumeric()))
					isError = true;
				if (xCoord != null && yCoord != null
						&& xCoord.isGeoNumeric() && yCoord.isGeoNumeric()) {

					pointString = "(" + xCoord.getLabel() + ","
							+ yCoord.getLabel() + ")";
					GeoElement[] geos = table.kernel.getAlgebraProcessor()
							.processAlgebraCommandNoExceptionHandling(
									pointString, false);

					// set label P_1, P_2, etc.
					pointName = geos[0].getIndexLabel("P");
					geos[0].setLabel(pointName);
					geos[0].setAuxiliaryObject(true);
					list.addLast(pointName);
				}
			}

			if (list.size() > 0) {

				String listString = list.toString();
				listString = listString.replace("[", "{");
				listString = listString.replace("]", "}");

				table.kernel.getAlgebraProcessor()
						.processAlgebraCommandNoExceptionHandling(
								listString, false);
				app.storeUndoInfo();
			}

		}

		catch (Exception ex) { // Just abort the process if (isError)
			app.showError("NumberExpected");
		}

	}
	
	*/
	
	
	
	/** Creates a GeoList from the cells in an array of cellranges. Empty cells are ignored.
	 * Uses these defaults: create undo point, do not sort, do not filter by geo type. */
	public GeoElement createList(ArrayList<CellRange> rangeList,  boolean scanByColumn, boolean copyByValue) {
		return  createList(rangeList,  scanByColumn, copyByValue, false, true, null) ;
	}
	
	/** Creates a GeoList from the cells in an array of cellranges. Empty cells are ignored */
	public GeoElement createList(ArrayList<CellRange> rangeList,  boolean scanByColumn, boolean copyByValue, 
			boolean isSorted, boolean doStoreUndo, Integer geoTypeFilter) {
		
		GeoElement[] geos = null;
		StringBuilder listString = new StringBuilder();
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<Point> cellList = new ArrayList<Point>();
		
		// temporary fix for catching duplicate cells
		// will not be needed when sorting of cells by row/column is done
		HashSet<Point> usedCells = new HashSet<Point>();
		
		try {
			// get list string
			/*
			CellRange cr = new CellRange(table);
			for (int i = 0; i < rangeList.size(); i++) {
				cr = (CellRange) rangeList.get(i);
				list.addAll(0, cr.toGeoLabelList(scanByColumn, copyByValue));
			}
			*/
			
			listString.append("{");
			for(CellRange cr:rangeList){
				cellList.addAll(cr.toCellList(scanByColumn));
			}
			for(Point cell: cellList){
				if(!usedCells.contains(cell)){
					GeoElement geo = RelativeCopy.getValue(table, cell.x, cell.y);
					if (geo != null && (geoTypeFilter == null || geo.getGeoClassType() == geoTypeFilter)){
						if(copyByValue)
							listString.append(geo.toDefinedValueString());
						else
							listString.append(geo.getLabel());
						
						//listString.append(geo.getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, copyByValue));
						listString.append(",");
					}
						//list.add(geo.getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, copyByValue));
						/*
						if (copyByValue)
							//list.add(geo.getValueForInputBar());
							list.add(arg0)
						else
							list.add(geo.getLabel());
						*/
						
					usedCells.add(cell);
				}
			}
			
			listString.deleteCharAt(listString.length()-1);
			listString.append("}");
					
			if(isSorted){
				listString.insert(0, "Sort[" );
				listString.append("]");
			}
			
			
			//System.out.println(listString);
			// convert list string to geo
			geos = table.kernel.getAlgebraProcessor()
					.processAlgebraCommandNoExceptions(listString.toString(), false);

			// get geo name and set label
		//	String listName = geos[0].getIndexLabel("L");
		//	geos[0].setLabel(listName);
					
			
		} catch (Exception ex) {
			Application.debug("Creating list failed with exception " + ex);
		}

		if(doStoreUndo)
			app.storeUndoInfo();
		
		if(geos != null)
			return geos[0];
		else 
			return null;

	}
		
	/** Create a list from the cells in a spreadsheet column */
	public GeoElement createListFromColumn(int column,  boolean scanByColumn, boolean copyByValue, boolean isSorted,
			boolean storeUndoInfo, Integer geoTypeFilter) {
		
		ArrayList<CellRange> rangeList = new ArrayList<CellRange>();
		CellRange cr = new CellRange(table,column,-1);
		cr.setActualRange();
		rangeList.add(cr);

		return  createList(rangeList,  scanByColumn, copyByValue, isSorted, storeUndoInfo, geoTypeFilter) ;
	}
	
	

	public GeoElement CreateMatrix(int column1, int column2, int row1, int row2){
		
		GeoElement[] geos = null;
		
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
						return null;
					}
				}
				row = removeComma(row);
				text += row +"}" + ",";
			}

			text = removeComma(text)+ "}";

			//Application.debug(text);
			geos = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptions(text, false);
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

		if(geos != null)
			return geos[0];
		else 
			return null;
	}

	public GeoElement CreateTableText(int column1, int column2, int row1, int row2){

		GeoElement[] geos = null;

		StringBuilder text= new StringBuilder();
		try {
			text.append("TableText[{");
			for (int j = row1; j <= row2; ++ j) {
				//if (selected.length > j && ! selected[j])  continue; 	
				text.append('{');
				for (int i = column1; i <= column2; ++ i) {
					GeoElement v2 = RelativeCopy.getValue(table, i, j);
					if (v2 != null) {
						text.append(v2.getLabel());
						if (i < column2) text.append(',');
					}
					else {
						app.showErrorDialog(app.getPlain("CellAisNotDefined",GeoElement.getSpreadsheetCellName(i,j)));
						return null;
					}
				}
				text.append('}');
				if (j < row2) text.append(',');
			}

			//text = removeComma(text)+ "}]";

			text.append("},\"|_\"]");

			//Application.debug(text);
			geos = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptions(text.toString(), false);

			app.storeUndoInfo();
		} 
		catch (Exception ex) {
			Application.debug("creating TableText failed " + text);
			ex.printStackTrace();
		} 

		if(geos != null)
			return geos[0];
		else 
			return null;
	}


	private static String removeComma(String s)
	{
		if (s.endsWith(",")) s = s.substring(0,s.length()-1); 	
		return s;
	}





	
	
	
	

	//===================================================
	//              Insert Rows/Columns
	//===================================================
	
	
	//TODO: these methods are taken from the old code and need work
	// They behave badly when cells have references to cells on the other
	// side of a newly inserted row or column
	
	

	public void InsertLeft(int column1, int column2){ 

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
	

	public void InsertRight(int column1, int column2){

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

	
	
	public void InsertAbove(int row1, int row2){
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
	
	
	public void InsertBelow(int row1, int row2){	
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

	
	
	
	/**
	 * Creates an operation table. 
	 */
	public void createOperationTable(CellRange cr, GeoFunctionNVar fcn ){
		
		int r1 = cr.getMinRow();
		int c1 = cr.getMinColumn();
		String text = "";
		GeoElement[] geos;
		fcn = (GeoFunctionNVar) RelativeCopy.getValue(table, r1,c1);
		
		for(int r = r1+1; r <= cr.getMaxRow(); ++r){
			for(int c = c1+1; c <= cr.getMaxColumn(); ++c){
				//System.out.println(GeoElement.getSpreadsheetCellName(c, r) + ": " + text);
				
				text = GeoElement.getSpreadsheetCellName(c, r) + "=" + fcn.getLabel() + "(";
				text += GeoElement.getSpreadsheetCellName(c1, r);
				text += ",";
				text += GeoElement.getSpreadsheetCellName(c, r1);
				text += ")";
				
				geos = table.kernel.getAlgebraProcessor()
				.processAlgebraCommandNoExceptions(text,false);

				//geos[0].setLabel(GeoElement.getSpreadsheetCellName(c, r));
				geos[0].setAuxiliaryObject(true);
				
				
			}
		}
		
	}
	
	
	
	//Experimental ---- merging ctrl-selected cells 
	
	private void consolidateRangeList(ArrayList<CellRange> rangeList){
		
		ArrayList<Point> columnList = new ArrayList<Point>();
		ArrayList<ArrayList<Point>> matrix = new ArrayList<ArrayList<Point>>();
		int minRow = rangeList.get(0).getMinRow();
		int maxRow = rangeList.get(0).getMaxRow();
		int minColumn = rangeList.get(0).getMinColumn();
		int maxColumn = rangeList.get(0).getMaxColumn();
		
		
		for(CellRange cr:rangeList){
			
			minColumn = Math.min(cr.getMinColumn(), minColumn);
			maxColumn = Math.max(cr.getMaxColumn(), maxColumn);
			minRow = Math.min(cr.getMinRow(), minRow);
			maxRow = Math.max(cr.getMaxRow(), maxRow);
			
			// create matrix of cells from all ranges in the list
			for(int col = cr.getMinColumn(); col <= cr.getMaxColumn(); col++){
				
				// add columns from this cell range to the matrix
				if(matrix.get(col) == null){
					matrix.add(col, new ArrayList<Point>());
					matrix.get(col).add(new Point(cr.getMinColumn(),cr.getMaxColumn()));
				} else {
					Point p = matrix.get(col).get(1);
				//	if(cr.getMinColumn()>)
				//	insertPoint(matrix, new Point(new Point(cr.getMinColumn(),cr.getMaxColumn())));
				}

			}
		
		
			// convert our matrix to a CellRange list
			for(int col = minColumn; col <= maxColumn; col++){
				if(matrix.contains(col)){
					
				}
			}
		
		}
		
	}
	
	
	
}
