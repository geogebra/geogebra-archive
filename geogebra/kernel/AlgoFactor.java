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
 * Find a limit
 * 
 * @author Michael Borcherds
 */
public class AlgoFactor extends AlgoSimplify {
   
    public AlgoFactor(Construction cons, String label, GeoFunction f) {
    	super(cons, label, f);
    }
    
    public String getClassName() {
        return "AlgoFactor";
    }
     
    protected String getCASString(String functionIn) {
    	



    	
	    sb.setLength(0);
        sb.append("Factor(");
        sb.append(functionIn);
        sb.append(')');

        
        return sb.toString();
        

    }

}