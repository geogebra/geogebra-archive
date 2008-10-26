package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;

/*
 * Clean[ <List> ]
 * Michael Borcherds
 * 2008-03-06
 */
public class CmdClean extends CommandProcessor {

	public CmdClean(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
        boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 1:

			if (arg[0].isGeoList()) {
				GeoElement[] ret = { 
						kernel.Clean(c.getLabel(),
						(GeoList) arg[0] ) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		case 2:                 
			
			if (ok[0] = (arg[0] instanceof GeoFunction)) {
        	GeoFunction booleanFun = (GeoFunction) arg[0];
        	if ((ok[0] = booleanFun.isBooleanFunction()) &&
        		(ok[1] = arg[1].isGeoList())) 
        	{
		
        		GeoElement[] ret =
                	{
                     kernel.Clean(
                        c.getLabel(),
                        (GeoFunction) booleanFun,
                        ((GeoList) arg[1]) )
                	};
        		return ret;
        	}
        	
        	if (!ok[0])
                throw argErr(app, c.getName(), arg[0]);
            else 
                throw argErr(app, c.getName(), arg[1]);

        } 

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
