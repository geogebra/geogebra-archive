package geogebra.gui.view.spreadsheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

/**
 * This class manages tracing of GeoElements to the spreadsheet. A trace is a
 * spreadsheet cell, or set of cells in the same row, that holds the numeric
 * value(s) of a GeoElement.
 * 
 * Specifically, the class:
 * 
 * (1) maintains TraceGeoCollection, a hash table that matches all GeoElements that
 *     trace to the spreadsheet with their trace settings
 * 
 * (2) creates and updates spreadsheet cell traces based on type of geo (e.g.
 *     angles, polar points, lists)
 * 
 * (3) determines the column/row location for a geo trace
 *  
 * 
 * @author G. Sturr 2010-4-22
 * 
 */


public class SpreadsheetTraceManager {
	
	// external components
	private Application app;
	private SpreadsheetView view;
	private Kernel kernel;
	private MyTable table; 
	
	
	
	// collection of all geos currently traced
	private HashMap<GeoElement, TraceSettings> traceGeoCollection;	
	
	
	// temporary collection of trace geos, held during construction updates 
	private HashSet<GeoElement> storedTraces;
	private boolean collectingTraces = false;
	
	
	// tracing options
	private boolean allowColumnReset = false;
	private boolean allowRowLimit = true;
	//private int maxTraceRow = 10;
	private boolean doShiftCellsUp = true; 
	
	
	// misc variables
	//private TraceSettings t;
	private double[] coords = new double[2];
	private ArrayList<Double> currentTrace = new ArrayList<Double>();
	
	
	
	public SpreadsheetTraceManager(SpreadsheetView view ){
		
		this.view = view;
		app = view.getApplication();
		kernel = app.getKernel();
		table = view.getTable();
		
		traceGeoCollection = new HashMap<GeoElement, TraceSettings >();	
		storedTraces = new HashSet<GeoElement>();
		
	}
	
	

	// =============================================	
	//            Add/Remove Geo Trace 
	// =============================================	
	 
	
	/**  Add a geo to the trace collection using default trace settings. */
	public void addSpreadsheetTraceGeo(GeoElement geo){
		addSpreadsheetTraceGeo(geo, new TraceSettings());
	}
	
	/**  Add a geo to the trace collection using custom trace settings. */
	public void addSpreadsheetTraceGeo(GeoElement geo, TraceSettings t){
			
		
		// prepare the trace settings
		setTraceColumns(geo,t);
		if(t.traceRow1 == -1){
			t.traceRow1 = 0;
		}
		if(t.doRowLimit){
			t.traceRow2 = t.traceRow1 + t.numRows -1;
			if(t.showName){
				++t.traceRow2; 
			}
		}else{
			t.traceRow2 = SpreadsheetView.MAX_ROWS;		
		}		
		t.tracingRow = t.traceRow1;
		
				
		// add the geo and its settings to the hash table
		traceGeoCollection.put(geo, t);	
		
		
		//set the tracing flag for this geo
		geo.setSpreadsheetTrace(true);
	
		
		//add the current trace to the spreadsheet
		t.lastTrace.clear();
		traceToSpreadsheet(geo);
		view.repaint();
		//Application.debug("add trace " + geo.getLabel());
	}
		
	
	public void updateTraceSettings(GeoElement geo, TraceSettings t){
		clearGeoTraceColumns(geo);
		addSpreadsheetTraceGeo(geo,t);	
	}

	
	/**
	 * Set trace columns. Requires that the first trace column is already set in
	 * the geo's tracesettings. If this is set to -1 then the next available
	 * trace column is used.
	 */
	public void setTraceColumns(GeoElement geo, TraceSettings t){
		 
		if(t.traceColumn1 == -1){
			t.traceColumn1 = getNextTraceColumn();
		}
		
		if(geo.isGeoNumeric()){
			t.traceColumn2 = t.traceColumn1;
		}
		else if(geo.isGeoPoint()){
			t.traceColumn2 = t.traceColumn1 + 1;
		}
		else if(geo.isGeoList()){
			t.traceColumn2 = t.traceColumn1 + ((GeoList)geo).size() - 1;
			for(int index = 0; index < ((GeoList)geo).size(); index++ ){
				if(((GeoList)geo).get(index).isGeoPoint())
					t.traceColumn2 ++;
			}
		}		
	}
	
		

	
	/** Remove a geo from the trace collection   */
	public void removeSpreadsheetTraceGeo(GeoElement geo){
	
		if(!traceGeoCollection.containsKey(geo)) return;
			
		traceGeoCollection.remove(geo);
		geo.setSpreadsheetTrace(false);
		geo.setTraceSettings(null);
		view.repaint();
			
		//Application.debug("remove trace " + geo.getLabel());
	}
	
	
	
	/** Remove all geos from the trace collection. */
	public void removeAllSpreadsheetTraceGeos(){
		
		for(GeoElement geo:traceGeoCollection.keySet()){
			//geo.setSpreadsheetTrace(false);
		}
		traceGeoCollection.clear();	
		view.repaint();
		//Application.printStacktrace("remove all traces ");
	}
	
	
	/** Load all tracing geos into the TraceGeoCollection */ 
	public void loadTraceGeoCollection(){
		//Application.debug("loading trace geos");
		traceGeoCollection.clear();	
		TreeSet<GeoElement> ts = app.getKernel().getConstruction().getGeoSetConstructionOrder();
			for(GeoElement geo: ts){
				if( geo.getSpreadsheetTrace()){
					traceGeoCollection.put(geo,geo.getTraceSettings());
					//System.out.println("load this geo: " + geo.toString());
				}
		}
		view.repaint();
	}

	public void loadTraceGeo(GeoElement geo){
		//Application.debug("loading trace geo" + geo.toString());
		traceGeoCollection.put(geo,geo.getTraceSettings());				
	}
	
	
	
	
	// =============================================	
	//              Utility Methods
 	// =============================================
	
	
	
	private int getNextTraceColumn(){
		return Math.max( view.getHighestUsedColumn() , getHighestTraceColumn())+ 1 ;
	}
	
	
	private int getHighestTraceColumn() {
		
		int max = -1;	
		for ( GeoElement geo : traceGeoCollection.keySet()) {
			if ( geo.getTraceSettings().traceColumn2 > max)
				max = geo.getTraceSettings().traceColumn2;
		}	
		return max;
	}
	
	
	public CellRange getNextTraceCell(){
		return new CellRange(table, getNextTraceColumn(), 0);
	}
	
	public boolean isTraceGeo(GeoElement geo){
		
		return traceGeoCollection.containsKey(geo);
		
	}
	
	
	public boolean isTraceColumn(int column){
		TraceSettings t = new TraceSettings();
		for ( GeoElement geo : traceGeoCollection.keySet()) {
			t = traceGeoCollection.get(geo);
			if ( column >= t.traceColumn1 && 
					column <= t.traceColumn2)
				return true;
		}
		
		return false;
	}
	
	
	public ArrayList<GeoElement> getTraceGeoList(){
	
		ArrayList<GeoElement> traceGeoList = new ArrayList<GeoElement>();
		for ( GeoElement geo : traceGeoCollection.keySet()) {
			traceGeoList.add(geo);
		}
		return traceGeoList;
		
	}
	
	public GeoElement getTraceGeo(int column){
		TraceSettings t = new TraceSettings();
		for ( GeoElement geo : traceGeoCollection.keySet()) {
			t = traceGeoCollection.get(geo);
			if ( column >= t.traceColumn1 && 
					column <= t.traceColumn2)
				return geo;
		}
		
		return null;
		
	}
	
	public TraceSettings getTraceSettings(GeoElement geo){
		
			return traceGeoCollection.get(geo);		
	}
	
	
	/**
 	* Delete the elements of all trace columns, excluding headers.
 	* This is called when a user resets the row limit.	  
 	*/
	private void clearAllTraceColumns(){
		
		for(GeoElement geo: traceGeoCollection.keySet()){			
			clearGeoTraceColumns(geo);
		}		
	}
	
	/**
	 * Delete the elements in the trace columns of a single geo.
	 */
	public void clearGeoTraceColumns(GeoElement geo){
		
		TraceSettings t = geo.getTraceSettings();
		if (t==null) return;
		 
		table.copyPasteCut.delete(t.traceColumn1, t.traceRow1, t.traceColumn2, SpreadsheetView.MAX_ROWS);
		t.tracingRow = t.traceRow1;	
	}
	
	
	/**
	 * If a user deletes the contents of a trace column then
	 *   1) re-insert the header text cell (if header is shown)  
	 *   2) reset the trace row to the top of the column
	 */
	public void handleColumnDelete(int column1, int column2){
		TraceSettings t;
		for ( GeoElement geo : traceGeoCollection.keySet()) {
			t = geo.getTraceSettings();
			if ( column2 >= t.traceColumn1 && column1 <= t.traceColumn2 ){
				//removeSpreadsheetTraceGeo(geo);
				Construction cons = app.getKernel().getConstruction();
				setHeader(geo,cons);
				t.tracingRow = 1;	
			}
		}
		
		view.repaint();
		
	}
	
	public TraceSettings getDefaultTraceSettings(){
		return new TraceSettings();
	}
	
	
	public void setNeedsColumnReset(GeoElement geo, boolean flag){
		if(!traceGeoCollection.containsKey(geo)) return;
		traceGeoCollection.get(geo).needsColumnReset = flag;
		
	}

	
	
	

	// =============================================	
	//                XML 
 	// =============================================
	
	
	
	public String getTraceXML(GeoElement geo){
		TraceSettings t = geo.getTraceSettings();
		StringBuilder sb = new StringBuilder();
		
		
		sb.append("\t<spreadsheetTrace val=\"true\"");	
		
		sb.append(" traceColumn1=\"");
		sb.append(t.traceColumn1);
		sb.append("\"");
		
		sb.append(" traceColumn2=\"");
		sb.append(t.traceColumn2);
		sb.append("\"");
		
		sb.append(" traceRow1=\"");
		sb.append(t.traceRow1);
		sb.append("\"");
		
		sb.append(" traceRow2=\"");
		sb.append(t.traceRow2);
		sb.append("\"");
		
		sb.append(" tracingRow=\"");
		sb.append(t.tracingRow);
		sb.append("\"");
		
		sb.append(" numRows=\"");
		sb.append(t.numRows);
		sb.append("\"");
		
		sb.append(" doColumnReset=\"");
		sb.append(t.doColumnReset  ? "true" : "false" );
		sb.append("\"");
		
		sb.append(" doRowLimit=\"");
		sb.append(t.doRowLimit  ? "true" : "false" );
		sb.append("\"");
		
		sb.append(" showName=\"");
		sb.append(t.showName  ? "true" : "false" );
		sb.append("\"");
		
		sb.append("/>\n");
		
		/* this param is not included:
		 * 
				public ArrayList<Double> lastTrace = new ArrayList<Double>();
				
		   do we need it?
		*/
					
		return sb.toString();	
	}
	
	
	// =============================================	
	//                Tracing 
 	// =============================================
	
	
	/**
	 * Turns on the collectingTraces flag and clears the storedTraces collection
	 * so that trace geos will be recorded but not traced while a construction
	 * updates.
	 */
	public void startCollectingSpreadsheetTraces() {
		
		collectingTraces = true;
		storedTraces.clear();
	}
	
	
	/**
	 * Turns off the collectingTraces flag after a construction has updated and
	 * then calls traceToSpreadsheet to trace all geos that have been put on
	 * hold during the update.
	 */
	public void stopCollectingSpreadsheetTraces() {
		
		collectingTraces = false;
		
		for(GeoElement geo:storedTraces) {
			traceToSpreadsheet(geo);
		}		
		storedTraces.clear();
	}

	
	
	
	/**
	 * Trace the current value(s) of a geo into the spreadsheet.
	 * 
	 * This will either create new cells to display trace values for the geo or
	 * update old trace cells with new trace values. Placement in the
	 * spreadsheet is determined by geo.traceSettings.
	 * 
	 * Traces are displayed according to geo type. An optional header cell of
	 * type GeoText can be placed in the first tracing row. This displays the
	 * geo name, or the name of each element in a GeoList.
	 * 
	 */
	public void traceToSpreadsheet(GeoElement geo) {
				
		if(!traceGeoCollection.containsKey(geo)) return;
				
		TraceSettings t = traceGeoCollection.get(geo);
				
		//TODO 
		  // 1) test if equals is working here
		  // 2) add code to override this test if grouping of trace elements is allowed
		  // 3) improve selection rectangle handling
		
		// get the current trace for this geo and compare with last trace
		// exit if no change in trace
		getCurrentTrace(geo);	
		if(currentTrace.equals(t.lastTrace)) return;	
		t.lastTrace = (ArrayList<Double>) currentTrace.clone();
		
		
		// If we are only collecting traces, then record this geo and exit.
		// The geo will be traced later.
		if (collectingTraces) {
			storedTraces.add(geo);
			return;
		}
		
		
		// handle column reset
		if(t.needsColumnReset && t.doColumnReset){
			t.traceColumn1 = getNextTraceColumn();
			t.tracingRow = t.traceRow1;
			t.needsColumnReset = false;
		}
		
		
		// prepare for trace 
		view.setScrollToShow(true);
		Construction cons = app.getKernel().getConstruction();
		
		// row = the row that will get the new trace 
		int row = t.tracingRow;
    	
		// create header
    	if(row == t.traceRow1 && t.showName){
    		setHeader(geo,cons);
    		row = (row == t.traceRow2)? -1 : row + 1;
    	}
    	
    	// when rows are limited then tracingRow = -1 serves as a flag to
    	// keep our row counter from going past traceRow2
		if(t.tracingRow == -1)
			row = t.traceRow2;
		
    		
    	
    	// add geo traces if we are NOT in the last row
    	if(t.tracingRow != -1){
    		setGeoTrace(geo,cons, t.lastTrace, row);
    	} 
    	
    	// if in the last row, shift cells up and put new trace in last row  
    	else if(doShiftCellsUp){

    		GeoElement sourceCell;
    		GeoElement targetCell; 

    		int minTraceRow = t.showName ? t.traceRow1 + 2 : t.traceRow1 + 1;
    		if(t.numRows == 1) --minTraceRow;
    		
    		for(int c = t.traceColumn1; c <= t.traceColumn2; c++){
    			for (int r = minTraceRow; r <= t.traceRow2; r++){

    				// get the source cell (or create a null source cell if needed)
    				sourceCell = RelativeCopy.getValue(table, c, r);
    				if(sourceCell == null || !sourceCell.isGeoNumeric())
    					setGeoTrace(geo,cons,null,r);
    				sourceCell = RelativeCopy.getValue(table, c, r);

    				// copy the value from the source cell into the target cell below
    				// (don't do this if there is only one row)
    				if(t.numRows > 1){
    					targetCell = RelativeCopy.getValue(table, c, r-1);
    					// get the target cell (or create a null source cell if needed)
    					if(targetCell == null || !targetCell.isGeoNumeric()){
    						setGeoTrace(geo,cons,null,r-1);
    					}
    					targetCell = RelativeCopy.getValue(table, c, r-1);

    					((GeoNumeric)targetCell).setValue(((GeoNumeric)sourceCell).getValue());
    					targetCell.update();
    				}	

    				// if the source cell is in the last row, update it with a new trace 
    				if(r == t.traceRow2){
    					((GeoNumeric)sourceCell).setValue(t.lastTrace.get(c - t.traceColumn1)); 
    					sourceCell.update();
    				}			
    			}
    		}	   		
    	}

    	// draw the selection rectangle around the last trace row
    	//table.setSelectionRectangle(new CellRange(table,t.traceColumn1, row, t.traceColumn2, row));
    	
    	
    	// update geo.traceRow counter
    	t.tracingRow = (row < t.traceRow2) ? t.tracingRow + 1 : -1;
    	
		view.setScrollToShow(false);
      		
	}
	
	
	
	
	
	
	/** Create a row of trace cell(s) for the trace column(s) of a geo.  
	 * note: trace geos are actually created by calls to setElementTrace */
	private void setGeoTrace(GeoElement geo, Construction cons, ArrayList<Double> traceArray,  int row) {
		TraceSettings t = traceGeoCollection.get(geo);
		int columnIndex = t.traceColumn1;
    	int traceIndex = 0;
    	if(geo.isGeoList()){
    		for (int i = 0; i < ((GeoList)geo).size(); i++) {
    			
    			// create the trace geos for this element of the geoList
    			setElementTrace( ((GeoList)geo).get(i),  cons, traceArray, traceIndex, columnIndex, row);
    				
    			if(((GeoList)geo).get(i).isGeoPoint())
    					columnIndex = columnIndex + 2;
    			else
    				columnIndex ++;
    			traceIndex = columnIndex - t.traceColumn1;
    			}
    					
    	}else{
    		// create trace geos for non-list geo
    		setElementTrace( geo,  cons, traceArray, traceIndex, columnIndex,  row);
    	}
	}
	
	
	/**
	 * Create a row of trace cell(s) for the trace column(s) of a single element.
	 * The element could be either a stand-alone geo or an element of a GeoList.
	 */
	private void setElementTrace(GeoElement geo, Construction cons, ArrayList<Double> traceArray, 
			int traceIndex, int columnIndex, int row) {

		String cellName = GeoElement.getSpreadsheetCellName(columnIndex,row);
		GeoElement traceCell;
		
		
		
		if(traceArray == null){
			traceArray = new ArrayList<Double>();
			traceArray.add(Double.NaN);
			traceArray.add(Double.NaN);
		}
		
		switch (geo.getGeoClassType()) {

		case GeoElement.GEO_CLASS_POINT: 
			// delete old cell geos
			table.copyPasteCut.delete(columnIndex, row, columnIndex+1, row);
			
			// create new cell geos
			traceCell = new GeoNumeric(cons, cellName, traceArray.get(traceIndex));
			traceCell.setAuxiliaryObject(true);
		
			cellName = GeoElement.getSpreadsheetCellName(columnIndex + 1,row);
			
			if (((GeoPoint) geo).getMode() == Kernel.COORD_POLAR)
				traceCell = new GeoAngle(cons, cellName, traceArray.get(traceIndex + 1));
			else
				traceCell = new GeoNumeric(cons, cellName,traceArray.get(traceIndex + 1));

			traceCell.setAuxiliaryObject(true);

			break;

		case GeoElement.GEO_CLASS_VECTOR: 
			// delete old cell geos
			table.copyPasteCut.delete(columnIndex, row, columnIndex+1, row);
			
			// create new cell geos
			traceCell = new GeoNumeric(cons, cellName, traceArray.get(traceIndex));
			traceCell.setAuxiliaryObject(true);

			cellName = GeoElement.getSpreadsheetCellName(columnIndex + 1,row);
			traceCell = new GeoNumeric(cons, cellName,traceArray.get(traceIndex + 1));
			traceCell.setAuxiliaryObject(true);

			break;

		case GeoElement.GEO_CLASS_NUMERIC:
			// delete old cell geo
			table.copyPasteCut.delete(columnIndex, row, columnIndex, row);
			
			// create new cell geos
			traceCell = new GeoNumeric(cons, cellName, traceArray.get(traceIndex));
			traceCell.setAuxiliaryObject(true);

			break;
		

		case GeoElement.GEO_CLASS_ANGLE: 
			// delete old cell geo
			table.copyPasteCut.delete(columnIndex, row, columnIndex, row);
			
			// create new cell geos
			traceCell = new GeoAngle(cons, cellName, traceArray.get(traceIndex));
			traceCell.setAuxiliaryObject(true);

			break;

		}

	}
	
	
	/**  Create header cell(s) for each trace column of a geo.   */
	private void setHeader(GeoElement geo, Construction cons) {
		TraceSettings t = traceGeoCollection.get(geo);
		int columnIndex = t.traceColumn1;

		if (geo.isGeoList()) {
			for (int i = 0; i < ((GeoList) geo).size(); i++) {
				setElementHeader(((GeoList) geo).get(i), cons, columnIndex, t.traceRow1);
				if (((GeoList) geo).get(i).isGeoPoint())
					columnIndex = columnIndex + 2;
				else
					columnIndex++;
			}

		} else {
			setElementHeader(geo, cons, columnIndex,t.traceRow1);
		}
		 
	}
	
	/**
	 * Create header cell(s) for the trace column(s) of a single element. 
	 * The element could be either a stand-alone geo or an element of a GeoList.
	 */
	private void setElementHeader(GeoElement geo, Construction cons, int columnIndex, int rowIndex) {

		String headerText = "";
		GeoText titleCell;
		
		/*
		if (geo.isGeoPoint()) {
			headerText = GeoElement.getSpreadsheetCellName(columnIndex,rowIndex);
			headerText += " = \"x(\" + Name[" + geo.getLabel() + "] + \")\" ";
			
			try {
				GeoElement[] geos = table.kernel.getAlgebraProcessor()
						.processAlgebraCommandNoExceptionHandling(headerText,
								false);
				geos[0].setEuclidianVisible(false);
				geos[0].update();
			} catch (Exception e) {
				e.printStackTrace();
			}		
					
			headerText = GeoElement.getSpreadsheetCellName(columnIndex+1,rowIndex);
			headerText += " = \"y(\" + Name[" + geo.getLabel() + "] + \")\" ";
			
			try {
				GeoElement[] geos = table.kernel.getAlgebraProcessor()
						.processAlgebraCommandNoExceptionHandling(headerText,
								false);
				geos[0].setEuclidianVisible(false);
				geos[0].update();
			} catch (Exception e) {
				e.printStackTrace();
			}		
			
		} else {

			headerText = GeoElement.getSpreadsheetCellName(columnIndex,rowIndex)
					+ " = Name[" + geo.getLabel() + "]";
			try {
				GeoElement[] geos = table.kernel.getAlgebraProcessor()
						.processAlgebraCommandNoExceptionHandling(headerText,
								false);
				geos[0].setEuclidianVisible(false);
				geos[0].update();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		*/
		
		
		if (geo.isGeoPoint()) {
			headerText = "x( " + geo.getLabel() + " )";
			titleCell = new GeoText(cons, GeoElement
					.getSpreadsheetCellName(columnIndex,rowIndex), headerText);
			titleCell.setEuclidianVisible(false);
			titleCell.update();

			headerText = "y( " + geo.getLabel() + " )";
			titleCell = new GeoText(cons, GeoElement
					.getSpreadsheetCellName(columnIndex + 1,rowIndex), headerText);
			titleCell.setEuclidianVisible(false);
			titleCell.update();

		} else {
			headerText = geo.getLabel();
			titleCell = new GeoText(cons, GeoElement
					.getSpreadsheetCellName(columnIndex,rowIndex), headerText);
			titleCell.setEuclidianVisible(false);
			titleCell.update();
		}
		 

	}

	
	

	private void getCurrentTrace(GeoElement geo) {

		currentTrace.clear();
		Construction cons = app.getKernel().getConstruction();

		if (geo.isGeoList()) {
			for (int elem = 0; elem < ((GeoList) geo).size(); elem++) {
				addElementTrace(((GeoList) geo).get(elem), cons,
						currentTrace);
			}
		} else {
			addElementTrace(geo, cons, currentTrace);
		}

	}
	
	
	private void addElementTrace(GeoElement geo, Construction cons, ArrayList<Double> currentTrace){

		switch (geo.getGeoClassType()) {

		case GeoElement.GEO_CLASS_POINT:

			GeoPoint P = (GeoPoint) geo;
			boolean polar = P.getMode() == Kernel.COORD_POLAR;

			if (polar)
				P.getPolarCoords(coords);
			else
				P.getInhomCoords(coords);

			currentTrace.add(coords[0]);
			currentTrace.add(coords[1]);		
			break;

			
		case GeoElement.GEO_CLASS_VECTOR:
			GeoVector vector = (GeoVector) geo;
			vector.getInhomCoords(coords);

			currentTrace.add(coords[0]);
			currentTrace.add(coords[1]);

			break;

		case GeoElement.GEO_CLASS_NUMERIC:
			GeoNumeric num = (GeoNumeric) geo;			
			currentTrace.add(num.getValue());
			break;
			
		case GeoElement.GEO_CLASS_ANGLE:
			GeoAngle angle = (GeoAngle) geo;			
			currentTrace.add(angle.getValue());
			break;
		}
	}
	
	
	
	
}


