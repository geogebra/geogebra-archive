/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.main.Application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;




public class AlgoStemPlot extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
	private GeoNumeric multiplier; //input
    private GeoText text; //output	
    
    private GeoList[] geoLists;
    
    private StringBuffer sb = new StringBuffer();
    
    AlgoStemPlot(Construction cons, String label, GeoList geoList, GeoNumeric multiplier) {
    	this(cons, geoList, multiplier);
        text.setLabel(label);
    }

    AlgoStemPlot(Construction cons, GeoList geoList, GeoNumeric multiplier) {
        super(cons);
        this.geoList = geoList;
        this.multiplier = multiplier;
               
        text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text
		
        setInputOutput();
        compute();
        
        // make sure for new LaTeX texts we get nice "x"s
        text.setSerifFont(true);
    }

    public String getClassName() {
        return "AlgoStemPlot";
    }

    protected void setInputOutput(){

        input = new GeoElement[multiplier == null ? 1 : 2];
        input[0] = geoList;
        
        if (multiplier != null)
        	input[1] = multiplier;


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
    		//throw new MyError(app, app.getError("InvalidInput"));   		
    	}
    	
    	double[] nos = new double[size];
    	
    	// get numbers out of list
    	for (int i = 0 ; i < size ; i++) {
    		GeoElement geo = geoList.get(i);
    		if (!geo.isGeoNumeric()) {
        		text.setTextString("");
        		return;
    		}
    		
    		nos[i] = ((GeoNumeric)geo).getDouble();
    	}
    	
    	// sort numbers
    	Arrays.sort(nos);
    	
    	double max = nos[size - 1];
    	double min = nos[0];
    	
    	// next power of 10 above max eg 76 -> 100, 100->1000
    	double multiplie = Math.pow(10.0, Math.round(Math.log10(max*1.00000001)));
    	int noOfBins = 10;
    	
    	if (multiplier != null && multiplier.isDefined()) {
    		multiplie = multiplier.getDouble();
    	}
    	
    	double multTen = multiplie / 10;
    	double multUnit = multiplie / 100;
    	
    	int minBin = (int) ( min / multTen);
    	int maxBin = (int) ( max / multTen);
    	
    	
    	noOfBins = maxBin - minBin + 1;
    	
    	Application.debug("minBin = "+minBin);
    	Application.debug("maxBin = "+maxBin);
    	Application.debug("noOfBins = "+noOfBins);
    	// StemAndLeaf[{22, 33, 44, 55, 22, 33, 44, 22, 33}]
    	//double [][] bins = new double[10][];
    	
    	ArrayList<Integer> bins[] = new ArrayList[noOfBins];
    	
    	for (int i = 0 ; i < noOfBins ; i++) {
    		bins[i] = new ArrayList<Integer>();
    	}
    	
    	for (int i = 0 ; i < size ; i++) {

    		// get most significant digit eg 89.1 -> 8
    		int msd = (int)Math.floor(nos[i] / multTen);
    	
    		// get second significant digit eg 89 -> 9
    		// Kernel.EPSILON so that eg 9.7 -> 7 not 6
    		int ssd = (int)Math.floor(Kernel.EPSILON + (nos[i] - msd * multTen) / multUnit);
    		
    		bins[msd - minBin].add(new Integer(ssd));
    	
    	}
    	
    	int maxSize = 0;
    	
    	for (int i = 0 ; i < noOfBins ; i++) {
    		Collections.sort(bins[i]);
    		maxSize = Math.max(maxSize, bins[i].size());
    	}

    	
   	
    	sb.setLength(0);
    	
    	
    	// surround in { } to make eg this work:
    	// FormulaText["\bgcolor{ff0000}"+TableText[matrix1]]
    	sb.append('{');
    	
    	sb.append("\\begin{array}{");
   	
    	{ // alignment == HORIZONTAL
    	
    		sb.append("l|"); // "l", "r" or "c"
        	for (int i = 0 ; i < maxSize ; i++) {
	    		
	    		sb.append('l'); // "l", "r" or "c"
	    	}
	    	sb.append("}");
	    	
	    	
	    	// TableText[{11.1,322,3.11},{4,55,666,7777,88888},{6.11,7.99,8.01,9.81},{(1,2)},"c()"]
	    	
			for (int r = 0 ; r < noOfBins ; r++) {
	    	for (int c=-1 ; c < maxSize; c++) {
    			if (c == -1) sb.append((r+minBin)+""); // stem
    			else sb.append(bins[r].size() > c ? bins[r].get(c)+"" : " " );
    			
    			if (c < maxSize - 1) sb.append("&"); // column separator
    			
	    		}
	    		sb.append(" \\\\ "); // newline in LaTeX ie \\
	    	}   
		
    	}
    	
    	// avoid eg 31.0
    	String key = (multUnit >= 1) ? ""+31*(int)multUnit : ""+31.0*multUnit;

    	sb.append("\\end{array} \\fbox{\\text{");
    	sb.append(app.getPlain("StemPlot.KeyAMeansB", "3|1", key));
    	sb.append("}}");
    	
    	// surround in { } to make eg this work:
    	// FormulaText["\bgcolor{ff0000}"+TableText[matrix1]]
    	sb.append('}');
    	
    	//Application.debug(sb.toString());
    	text.setTextString(sb.toString());
    	text.setLaTeX(true,false);
    
    }
  
}
