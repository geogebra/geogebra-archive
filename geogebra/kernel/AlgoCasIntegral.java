/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;



/**
 * Integral of a function
 * 
 * @author Markus Hohenwarter
 */
public class AlgoCasIntegral extends AlgoCasBase {
 
	private GeoNumeric var;
	
    public AlgoCasIntegral(
            Construction cons,
            String label,
            CasEvaluableFunction f,
            GeoNumeric var) 
        {
            this(cons, f, var);
            g.toGeoElement().setLabel(label);
        }
        
    AlgoCasIntegral(Construction cons,  CasEvaluableFunction f, GeoNumeric var) {
            super(cons, f);
            this.var = var;
 
            setInputOutput(); // for AlgoElement    
            compute();            
     }
    
    public String getClassName() {
        return "AlgoCasIntegral";
    }   
    
    protected void setInputOutput() {
        int length = 1;
        if (var != null) length++;
        
        input = new GeoElement[length];
        length = 0;
        input[0] = f.toGeoElement();
        if (var != null)
            input[++length] = var;

        setOutputLength(1);
        setOutput(0, g.toGeoElement());
        setDependencies(); // done by AlgoElement
    }  
    
    @Override
	protected void applyCasCommand() {
    	
		// var.getLabel() can return a number in wrong alphabet (need ASCII)
		boolean internationalizeDigits = kernel.internationalizeDigits;
		kernel.internationalizeDigits = false;

		 sb.setLength(0);
		 sb.append("Integral(%");
		 sb.append(",");
		 sb.append(var != null ? var.getLabel() : f.getVarString());
		 sb.append(")");
		 
		kernel.internationalizeDigits = internationalizeDigits;

		 
		 // find symbolic derivative of f
		 g.setUsingCasCommand(sb.toString(), f, true);	
	}

    final public String toString() {
        StringBuilder sb = new StringBuilder();
        
        if (var != null) {
        	// Integral[ a x^2, x ]
        	sb.append(super.toString());
        } else {
	        // Michael Borcherds 2008-03-30
	        // simplified to allow better Chinese translation
	        sb.append(app.getPlain("IntegralOfA",f.toGeoElement().getLabel()));
        }
        
        if (!f.toGeoElement().isIndependent()) { // show the symbolic representation too
            sb.append(": ");
            sb.append(g.toGeoElement().getLabelForAssignment());
            sb.append(" = ");
            sb.append(g.toSymbolicString());
        }

        return sb.toString();
    }

	

}
