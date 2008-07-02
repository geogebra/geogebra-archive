/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import java.awt.Point;
import java.util.ArrayList;

/**
 * Algorithm to create a GeoList with GeoElement objects of a given range 
 * in GeoGebra's spreadsheet. For example, CellRange[A1, B2] (or A1:B2)
 * returns the list {A1, B1, A2, B2}.
 *
 * @author  Markus Hohenwarter
 * @date 29.06.2008
 */
public class AlgoCellRange extends AlgoElement {

	private static final long serialVersionUID = 1L;		
    private GeoList geoList;     // output list of range    
    private String toStringOutput;
           
    /**
     * Creates an algorithm that produces a list of GeoElements for a range of cells in the spreadsheet.
     * @param startCell: e.g. A1
     * @param endCell: e.g. B2
     */
    public AlgoCellRange(Construction cons, String label, GeoElement startCell, GeoElement endCell) {    	    
    	super(cons);    	    
    	
    	// create output object
        geoList = new GeoList(cons);
    	    	
    	// get range: cell coordinates of range in spreadsheet
    	Point startCoords = startCell.getSpreadsheetCoords();
    	Point endCoords = endCell.getSpreadsheetCoords();
    	toStringOutput = startCell.getLabel() + ":" + endCell.getLabel();
    	
    	// build the geoList
    	initCellRangeList(startCoords, endCoords);       
        
        setInputOutput(); 
        geoList.setLabel(label);    	
    }   
    
	protected String getClassName() {
		return "AlgoCellRange";
	}
	
	public void remove() {      
       super.remove();                                     
       clearGeoList();    
    }   
	
	private void clearGeoList() {
		 // remove this algorithm as cell range user to allow renaming again        
        for (int i = 0; i < geoList.size(); i++) {
            geoList.get(i).removeCellRangeUser();
        }   
        
        geoList.clear();
	}
        
	// for AlgoElement
	protected void setInputOutput() {
    	// create input array from listItems array-list
    	// and fill the geoList with these objects
    	int size = geoList.size();
        input = new GeoElement[size];
    	for (int i=0; i < size; i++) {
    		input[i] = (GeoElement) geoList.get(i);    		
    	}          
        
        output = new GeoElement[1];        
        output[0] = geoList;        
        setDependencies(); // done by AlgoElement
    }    
    
    /**
     * Builds geoList with current objects in range of spreadsheet. Renaming 
     * of all cells added to the geoList is turned off, otherwise the user could
     * move an object out of the range by renaming it.
     * 
     * @param startCoords
     * @param endCoords
     */
    private void initCellRangeList(Point startCoords, Point endCoords) {   
   	
    	// check if we have valid spreadsheet coordinates
    	boolean validRange = startCoords != null && endCoords != null;
    	if (!validRange) {
    		geoList.setUndefined();
    		return;
    	}    		
    	
    	// min and max column and row of range
    	int minCol = Math.min(startCoords.x, endCoords.x);    	
    	int maxCol = Math.max(startCoords.x, endCoords.x);
    	int minRow = Math.min(startCoords.y, endCoords.y);
    	int maxRow = Math.max(startCoords.y, endCoords.y);
        	
    	// build the list
    	for (int colIndex = minCol; colIndex <= maxCol; colIndex++) {
    		for (int rowIndex = minRow; rowIndex <= maxRow; rowIndex++) {
    			// get cell object for col, row
    			String cellLabel = GeoElement.getSpreadsheetCellName(colIndex, rowIndex);    			
    			GeoElement geo = kernel.lookupLabel(cellLabel);
    			
    			// don't allow missing objects in range: 
    			// this algorithm cannot detect when a new object is missing in its range    			
    			if (geo == null) {
    				clearGeoList();
    				geoList.setUndefined();
    				return;
    			}
    			
    			// we got the cell object, add it to the list
    			geoList.add(geo);
    			
    			// make sure that this cell object cannot be renamed by the user
    			// renaming would move the object outside of our range
    			geo.addCellRangeUser();
    			
        			// TODO: remove
    				System.out.println("cell added: " + cellLabel + ", " + geo);
    		}    		
    	}    	
    }        
    
    public GeoList getList() { 
    	return geoList; 
    }       
    
        
    protected final void compute() {    	    	
    	// nothing to do in compute
    }   
    
    final public String toString() {
    	return toStringOutput;
    }        
    
}
