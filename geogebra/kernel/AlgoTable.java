/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.main.MyError;



public class AlgoTable extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
    private GeoText text; //output	
    
    private GeoList[] geoLists;
    
    private StringBuffer sb = new StringBuffer();
    
    private int VERTICAL = 0;
    private int HORIZONTAL = 1;

    AlgoTable(Construction cons, String label, GeoList geoList) {
    	this(cons, geoList);
        text.setLabel(label);
    }

    AlgoTable(Construction cons, GeoList geoList) {
        super(cons);
        this.geoList = geoList;
               
        text = new GeoText(cons);

        setInputOutput();
        compute();
    }

    protected String getClassName() {
        return "AlgoTable";
    }

    protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = geoList;

        output = new GeoElement[1];
        output[0] = text;
        setDependencies(); // done by AlgoElement
    }

    GeoText getResult() {
        return text;
    }

    protected final void compute() {
    	int columns = geoList.size();
    	if (!geoList.isDefined() ||  columns == 0) {
    		throw new MyError(app, app.getError("InvalidInput"));   		
    	}
    	
    	int alignment = VERTICAL;
    	
    	String justification = "l"; // default (l, c or r)
    	
    	if (geoList.get(columns-1).isGeoText()) {
    		GeoText options = (GeoText)geoList.get(columns-1);
    		String optionsStr = options.getTextString();
    		if (optionsStr.endsWith("h")) alignment = HORIZONTAL; // horizontal table
    		if (optionsStr.startsWith("c")) justification = "c";
    		else if (optionsStr.startsWith("r")) justification = "r";
    		
    		columns --;
    	}
    	
    	if (columns == 0) {
    		throw new MyError(app, app.getError("InvalidInput"));   		
    	}
    	

    	if (geoLists == null || geoLists.length < columns)
    	    		geoLists = new GeoList[columns];
    	
    	int rows = 0;
    	
		for (int c = 0 ; c < columns ; c++) {
			GeoElement geo = geoList.get(c);
			if (!geo.isGeoList()) {
	    		throw new MyError(app, app.getPlain("SyntaxErrorAisNotAList",geo.toValueString()));
			}
			geoLists[c] = (GeoList)geoList.get(c);
			if (geoLists[c].size() > rows) rows = geoLists[c].size();
		}
		
    	if (rows == 0) {
    		throw new MyError(app, app.getError("InvalidInput"));   		
    	}

    	
    	
    	sb.setLength(0);
    	sb.append("$\\begin{tabular}{");
    	
    	
    	if (alignment == VERTICAL) {
    	
	    	for (int c = 0 ; c < columns ; c++)
	    		sb.append(justification); // "l", "r" or "c"
	    	sb.append("}");
	    	
	    	for (int r=0; r < rows; r++) {
	    		for (int c = 0 ; c < columns ; c++) {
	    			addCell(c, r);
	   		}
	    		sb.append(" \\\\ "); // newline in LaTeX ie \\
	    	}   
    	
    	}
    	else
    	{ // alignment == HORIZONTAL
    	
	    	for (int c = 0 ; c < rows ; c++)
	    		sb.append(justification); // "l", "r" or "c"
	    	sb.append("}");
	    	
	    	// Table[{11.1,322,3.11},{4,55,666,7777,88888},{6.11,7.99,8.01,9.81},{(1,2)},"c"]
	    	
			for (int c = 0 ; c < columns ; c++) {
	    	for (int r=0; r < rows; r++) {
	    			addCell(c, r);
	    		}
	    		sb.append(" \\\\ "); // newline in LaTeX ie \\
	    	}   
		
    	}
    	
    	sb.append("\\end{tabular}$");
    	
    	text.setTextString(sb.toString());
    	text.setLaTeX(true,false);
    }
    
    private void addCell(int c, int r) {
		if (geoLists[c].size() > r) { // check list has an element at this position
			GeoElement geo1 = geoLists[c].get(r);
			sb.append(geo1.toLaTeXString(false));
		}
		sb.append("&"); // separate columns    				

    }
  
}
