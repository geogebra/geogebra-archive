package geogebra.kernel.commands;

import geogebra.MyError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;

/*
 * 
 * FitLineX[list of points]
 * adapted from CmdLcm by Michael Borcherds 2008-01-14
 */
public class CmdFitLineX extends CommandProcessor {

	public CmdFitLineX(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { 
						kernel.FitLineX(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		/*
		case 2:			
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoList()) &&
				(ok[1] = arg[1].isGeoList())) 
			{
				GeoElement[] ret = { 
						kernel.FitLineY(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1]) };
				return ret;
				
			}  else
				throw argErr(app, c.getName(), arg[0]);
*/
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
