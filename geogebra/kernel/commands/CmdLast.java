package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;

/*
 * Last[ <List>,n ]
 * Michael Borcherds
 * 2008-03-04
 */
public class CmdLast extends CommandProcessor {

	public CmdLast(Kernel kernel) {
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
						kernel.Last(c.getLabel(),
						(GeoList) arg[0], null ) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		
		case 2:
			boolean ok0 = arg[0].isGeoList();
			if ( ok0 && arg[1].isGeoNumeric() ) {
				GeoElement[] ret = { 
						kernel.Last(c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1] ) };
				return ret;
			} else
				throw argErr(app, c.getName(), ok0 ? arg[1] : arg[0]);
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
