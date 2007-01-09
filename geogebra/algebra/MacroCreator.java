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
import geogebra.kernel.GeoList;
import geogebra.kernel.arithmetic.Command;

/**
 * The Macro[] command creates a new macro within the kernel
 * that can be used as a new command later on.
 * Syntax: Macro[ <list of input objects>, <list of output objects> ]  
 */
public class MacroCreator extends CommandProcessor {
	
	public MacroCreator(AlgebraController algCtrl) {
		super(algCtrl);
	}
		
	 public GeoElement[] process(Command c) throws MyError {
        int n = c.getArgumentNumber();
        boolean[] ok = new boolean[n];
        GeoElement[] arg;

        switch (n) {            
        	// Macro[ list, list ]
            case 2 :
                arg = resArgs(c);
                if ((ok[0] = (arg[0].isGeoList())) && (ok[1] = (arg[1].isGeoList()))) 
                {
                	GeoElement [] input = ((GeoList) arg[0]).toArray();
                	GeoElement [] output = ((GeoList) arg[1]).toArray();	                		                		               
                   	kernel.addMacro(c.getLabel(), input, output);	
                   	
                   	// no GeoElement created here
                    return null; 
                } else {
                    if (!ok[0])
                        throw argErr(app, c.getName(), arg[0]);
                    else
                        throw argErr(app, c.getName(), arg[1]);
                }

            default :
                throw argNumErr(app, c.getName(), n);
        }
    }    
}