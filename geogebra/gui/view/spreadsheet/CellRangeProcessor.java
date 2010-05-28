package geogebra.gui.view.spreadsheet;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.commands.CommandDispatcher;
import geogebra.main.Application;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;


public class CellRangeProcessor {

	private MyTable table;
	private Application app;		
	
	public CellRangeProcessor(MyTable table) {
		
		this.table = table;
		app = table.kernel.getApplication();
		
	}
	
	
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

	
	
	public void CreatePointList(ArrayList<CellRange> rangeList, boolean byValue, boolean leftToRight) {
		
		int c1, c2, r1, r2;
		StringBuilder text = new StringBuilder();
		LinkedList<String> list = new LinkedList<String>();
		boolean doHorizontalPairs = true;
		boolean isError = false;
		
		// assume that range list has been tested 
		
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
					createListPoint(xCoord, yCoord, list, isError);
				}

			} else {
				for (int i = c1; i <= c2; ++i) {
					GeoElement xCoord = RelativeCopy.getValue(table, i, r1);
					GeoElement yCoord = RelativeCopy.getValue(table, i, r2);
					createListPoint(xCoord, yCoord, list, isError);
				}
			}

			// Convert the string list to a geoList
			if (list.size() > 0) {
				String listString = list.toString();
				listString = listString.replace("[", "{");
				listString = listString.replace("]", "}");

				table.kernel.getAlgebraProcessor()
						.processAlgebraCommandNoExceptionHandling(listString,
								false);
				
				app.storeUndoInfo();
			}

		}

		catch (Exception ex) {
			app.showError("NumberExpected");
		}

	}
	
	private void createListPoint(GeoElement xCoord, GeoElement yCoord,
			LinkedList<String> list, boolean isError) {
		
		String pointString = "";
		String pointName = "";

		try {
			if (xCoord != null && yCoord != null
					&& (!xCoord.isGeoNumeric() || !yCoord.isGeoNumeric()))
				isError = true;

			// if both cells are non-empty and numeric then make a point
			if (xCoord != null && yCoord != null && xCoord.isGeoNumeric()
					&& yCoord.isGeoNumeric()) {

				// create a text command to create the point, then
				// send the text to the algebra processor to create a
				// new geo
				pointString = "(" + xCoord.getLabel() + "," + yCoord.getLabel()
						+ ")";

				GeoElement[] geos = table.kernel.getAlgebraProcessor()
						.processAlgebraCommandNoExceptionHandling(pointString,
								false);

				pointName = geos[0].getIndexLabel("P");
				geos[0].setLabel(pointName);
				geos[0].setAuxiliaryObject(true);

				// add point name to the list
				list.addLast(pointName);
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
	
	
	
	
	public void CreateList(ArrayList<CellRange> rangeList,  boolean scanByColumn, boolean copyByValue) {

		String listString = "";
		ArrayList list = new ArrayList();
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
			
			for(CellRange cr:rangeList){
				cellList.addAll(cr.toCellList(scanByColumn));
			}
			for(Point cell: cellList){
				if(!usedCells.contains(cell)){
					GeoElement geo = RelativeCopy.getValue(table, cell.x, cell.y);
					if (geo != null)
						if (copyByValue)
							list.add(geo.getValueForInputBar());
						else
							list.add(geo.getLabel());
					usedCells.add(cell);
				}
			}
			
			
			listString = list.toString();
			listString = listString.replace("[", "{");
			listString = listString.replace("]", "}");

			// convert list string to geo
			GeoElement[] geos = table.kernel
					.getAlgebraProcessor()
					.processAlgebraCommandNoExceptionHandling(listString, false);

			// get geo name and set label
		//	String listName = geos[0].getIndexLabel("L");
		//	geos[0].setLabel(listName);

			
			
			
		} catch (Exception ex) {
			Application.debug("Creating list failed with exception " + ex);
		}

		app.storeUndoInfo();

	}
			

	
	
	
	
}
