package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;

/*
 * Take[ <List>,m,n ]
 * Michael Borcherds
 * 2008-03-04
 */
public class CmdTake extends CommandProcessor {

	public CmdTake(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 3:

			if (arg[0].isGeoList() && arg[1].isGeoNumeric() && arg[2].isGeoNumeric() ) {
				GeoElement[] ret = { 
						kernel.Take(c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1], (GeoNumeric) arg[2] ) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
