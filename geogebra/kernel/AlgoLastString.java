/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

/**
 * Take first n objects from a list
 * @author Michael Borcherds
 * @version 2008-03-04
 */

public class AlgoLastString extends AlgoFirstString {


    AlgoLastString(Construction cons, String label, GeoText inputText, GeoNumeric n) {
        super(cons, label, inputText, n);

    }

    public String getClassName() {
        return "AlgoLastString";
    }


    protected final void compute() {
    	String str = inputText.getTextString();
    	size = str.length();
    	int outsize = n == null ? 1 : (int)n.getDouble();
    	
    	if (!inputText.isDefined() ||  size == 0 || outsize < 0 || outsize > size) {
    		outputText.setUndefined();
    		return;
    	} 
       
    	    	
    	if (outsize == 0) outputText.setTextString(""); // return empty string
    	
    	else outputText.setTextString(str.substring(size - outsize));
   }
  
}
