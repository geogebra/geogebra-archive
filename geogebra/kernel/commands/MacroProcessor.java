/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;

/**
 * Processes the use of macros from the command line.
 */
public class MacroProcessor extends CommandProcessor {
	
	public MacroProcessor(Kernel kernel) {
		super(kernel);
	}	
		
	public GeoElement[] process(Command c) throws MyError {        						 							
		// resolve command arguments
		GeoElement [] arg = resArgs(c);
		Macro macro = c.getMacro();
				
		Class [] macroInputTypes = macro.getInputTypes();		
		
		// wrong number of arguments
		if (arg.length != macroInputTypes.length) {
			boolean lengthOk = false;
			
			// check if we have a polygon in the arguments
			// if yes, let's use its points
			if (arg[0].isGeoPolygon()) {
				arg = ((GeoPolygon) arg[0]).getPoints();
				lengthOk = arg.length == macroInputTypes.length;
			}
			
			if (!lengthOk) {
				StringBuffer sb = new StringBuffer();
		        sb.append(app.getMenu("Macro") + " " + macro.getCommandName() + ":\n");
		        sb.append(app.getError("IllegalArgumentNumber") + ": " + arg.length);
		        sb.append("\n\nSyntax:\n" + macro.toString());
				throw new MyError(app, sb.toString());
			}
		}				
		
		// check whether the types of the arguments are ok for our macro
		for (int i=0; i < macroInputTypes.length; i++) {
			if (!macroInputTypes[i].isInstance(arg[i]))	{				
				StringBuffer sb = new StringBuffer();
		        sb.append(app.getPlain("Macro") + " " + macro.getCommandName() + ":\n");
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