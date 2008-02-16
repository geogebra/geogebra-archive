package geogebra.kernel.commands;

import geogebra.MyError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;

/*
 * Sum[ list ]
 * adapted from CmdLcm by Michael Borcherds 2008-02-16
 */
public class CmdMean extends CommandProcessor {

	public CmdMean(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { 
						kernel.Mean(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
