/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;




public class AlgoTableText extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
    private GeoText text; //output	
    private GeoText args; //input	
    
    private GeoList[] geoLists;
    
    private StringBuilder sb = new StringBuilder();
    
    private int VERTICAL = 0;
    private int HORIZONTAL = 1;

    AlgoTableText(Construction cons, String label, GeoList geoList, GeoText args) {
    	this(cons, geoList, args);
        text.setLabel(label);
    }

    AlgoTableText(Construction cons, GeoList geoList, GeoText args) {
        super(cons);
        this.geoList = geoList;
        this.args = args;
               
        text = new GeoText(cons);
		text.setIsCommand(true); // stop editing as text
		
        setInputOutput();
        compute();
    }

    protected String getClassName() {
        return "AlgoTableText";
    }

    protected void setInputOutput(){
    	if (args == null) {
	        input = new GeoElement[1];
	        input[0] = geoList;
    	} else {
            input = new GeoElement[2];
            input[0] = geoList;
            input[1] = args;
    	}

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
    		text.setTextString(sb.toString());
    		return;
    		//throw new MyError(app, app.getError("InvalidInput"));   		
    	}
    	
    	int alignment = HORIZONTAL;
    	
    	String justification = "l"; // default (l, c or r)
    	
    	if (args != null) {
    		String optionsStr = args.getTextString();
    		if (optionsStr.indexOf("v") > -1) alignment = VERTICAL; // vertical table
    		if (optionsStr.indexOf("c") > -1) justification = "c";
    		else if (optionsStr.indexOf("r") > -1) justification = "r";	
    		
    	} else if (geoList.get(columns-1).isGeoText()) {
    		
    		// support for older files before the fix
    		
     		GeoText options = (GeoText)geoList.get(columns-1);
     		String optionsStr = options.getTextString();
     		if (optionsStr.indexOf("h") > -1) alignment = HORIZONTAL; // horizontal table
     		if (optionsStr.indexOf("c") > -1) justification = "c";
     		else if (optionsStr.indexOf("r") > -1) justification = "r";
 
     		columns --;
    	}

    	
    	if (columns == 0) {
    		text.setTextString(sb.toString());
    		return;
    		//throw new MyError(app, app.getError("InvalidInput"));   		
    	}
    	

    	if (geoLists == null || geoLists.length < columns)
    	    		geoLists = new GeoList[columns];
    	
    	int rows = 0;
    	
		for (int c = 0 ; c < columns ; c++) {
			GeoElement geo = geoList.get(c);
			if (!geo.isGeoList()) {
				text.setTextString(sb.toString());
				return;
	    		//throw new MyError(app, app.getPlain("SyntaxErrorAisNotAList",geo.toValueString()));
			}
			geoLists[c] = (GeoList)geoList.get(c);
			if (geoLists[c].size() > rows) rows = geoLists[c].size();
		}
		
    	if (rows == 0) {
    		text.setTextString(sb.toString());
    		return;
    		//throw new MyError(app, app.getError("InvalidInput"));   		
    	}

    	
    	text.setTemporaryPrintAccuracy();
    	
    	sb.setLength(0);
    	sb.append("\\begin{tabular}{");
    	
    	
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

    	text.restorePrintAccuracy();
   	
    	sb.append("\\end{tabular}");
    	//Application.debug(sb.toString());
    	text.setTextString(sb.toString());
    	text.setLaTeX(true,false);
    }
    
    private void addCell(int c, int r) {
		if (geoLists[c].size() > r) { // check list has an element at this position
			GeoElement geo1 = geoLists[c].get(r);
						
			// replace " " and "" with a hard space (allow blank columns/rows)
			String text = geo1.toLaTeXString(false);
			if (" ".equals(text) || "".equals(text))
				text = "\u00a0";	
			
			sb.append(text);
		}
		sb.append("&"); // separate columns    				

    }
  
}
