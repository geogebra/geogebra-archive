/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.algebra;

import geogebra.MyError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Macro;
import geogebra.kernel.arithmetic.Command;

/**
 * Tries to find a macro for the given command name. 
 * If a macro is found it will be used.
 * Syntax: Macro[ <list of input objects>, <list of output objects> ]  
 */
public class MacroProcessor extends CommandProcessor {
	
	private Macro macro;
	
	public MacroProcessor(AlgebraController algCtrl) {
		super(algCtrl);
	}
	
	void setMacro(Macro macro) {
		this.macro = macro;
	}
		
	public GeoElement[] process(Command c) throws MyError {        						 
		int n = c.getArgumentNumber();		
		GeoElement [] macroInput = macro.getInputObjects();
		
		// wrong number of arguments
		if (n != macroInput.length) {
			StringBuffer sb = new StringBuffer();
	        sb.append(app.getPlain("Macro") + " " + macro.getName() + ":\n");
	        sb.append(app.getError("IllegalArgumentNumber") + ": " + n);
	        sb.append("\n\nSyntax:\n" + macro.toString());
			throw new MyError(app, sb.toString());
		}
		
		// resolve command arguments
		GeoElement [] arg = resArgs(c);
		
		// check whether the types of the arguments are ok for our macro
		for (int i=0; i < macroInput.length; i++) {
			try {		
				macroInput[i].setInternal(arg[i]);							
			} catch(Exception e) {
				StringBuffer sb = new StringBuffer();
		        sb.append(app.getPlain("Macro") + " " + macro.getName() + ":\n");
		        sb.append(app.getError("IllegalArgument") + ": ");	            
	            sb.append(arg[i].getNameDescription());	            	            
		        sb.append("\n\nSyntax:\n" + macro.toString());
		        throw new MyError(app, sb.toString());
			}
		}
		
		// if we get here we have the right arguments for our macro
	    return kernel.useMacro(c.getLabels(), macro, arg);
    }    
}