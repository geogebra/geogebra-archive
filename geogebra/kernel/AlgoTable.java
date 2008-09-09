/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;


/**
 * Maximum value of a list.
 * @author Michael Borcherds
 * @version 2008-09-09
 */

public class AlgoTable extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
    private GeoText text; //output	
    
    private StringBuffer sb = new StringBuffer();

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
    	int size = geoList.size();
    	if (!geoList.isDefined() ||  size == 0) {
    		text.setTextString("");
    		return;
    	}
    	
    	sb.setLength(0);
    	sb.append("\\begin{tabular}");
    	
    	for (int i=0; i < size; i++) {
    		GeoElement geo = geoList.get(i);
    		sb.append(geo.toLaTeXString(true));
    		sb.append(" \\\\ "); // newline in LaTeX ie \\
    	}   
    	
    	sb.append("\\end{tabular}");
    	
    	text.setTextString(sb.toString());
    	text.setLaTeX(true,false,false);
    }
    
}
