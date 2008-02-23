package geogebra.kernel.commands;

import geogebra.MyError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;

/*
 * Sum[ list ]
 * adapted from CmdLcm by Michael Borcherds 2008-02-16
 */
public class CmdSum extends CommandProcessor {

	public CmdSum(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { 
						kernel.Sum(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		case 2:
			if (arg[0].isGeoList() && arg[1].isGeoNumeric()) {
				GeoElement[] ret = { 
						kernel.Sum(c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1]) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		
		default:
            // try to create list of numbers
	       	 GeoList list = wrapInList(arg, GeoElement.GEO_CLASS_NUMERIC);
	            if (list != null) {
	           	 GeoElement[] ret = { kernel.Sum(c.getLabel(), list)};
	                return ret;             	     	 
	            } 
			throw argNumErr(app, c.getName(), n);
		}
	}

}
