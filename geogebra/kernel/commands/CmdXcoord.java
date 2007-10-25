package geogebra.kernel.commands;

import geogebra.MyError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.VectorValue;

/*
 * Xcoord[ <point> ]
 */
public class CmdXcoord extends CommandProcessor {

	public CmdXcoord(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:			
			arg = resArgs(c);
			   // point
            if ((ok[0] = arg[0].isVectorValue()))               	
            {
         	GeoElement[] ret = {  kernel.Xcoord(
                             c.getLabel(),
                             (VectorValue) arg[0]) };
                return ret; 
            } 


		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
