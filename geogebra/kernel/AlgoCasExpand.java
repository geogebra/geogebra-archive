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
 * Try to expand the given function's expression
 * (e.g. function expression or dependent number's expression). 
 * 
 * @author Markus Hohenwarter
 */
public class AlgoCasExpand extends AlgoCasBase {
       
    public AlgoCasExpand(Construction cons, String label, CasEvaluableFunction f) {
    	super(cons, label, f);
    }   
    
    public String getClassName() {
        return "AlgoCasExpand";
    }

	@Override
	protected void applyCasCommand() {
		// symbolic expand of f
		g.setUsingCasCommand("Expand(%)", f, true);		
	}
	
    final public String toString() {  
    	sb.setLength(0);
    	sb.append(getCommandDescription());
    	
        if (!f.toGeoElement().isIndependent()) { // show the symbolic representation too
            sb.append(": ");
            sb.append(g.toGeoElement().getLabelForAssignment());
            sb.append(" = ");
            sb.append(g.toSymbolicString());
        }
        return sb.toString();
    }

}
