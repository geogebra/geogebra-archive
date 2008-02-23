package geogebra.kernel.commands;

import geogebra.MyError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;

/*
 * SigmaXX[ list ]
 * adapted from CmdVariance by Michael Borcherds 2008-02-18
 */
public class CmdSigmaYY extends CommandProcessor {

	public CmdSigmaYY(Kernel kernel) {
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
						kernel.SigmaYY(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		
		case 2:			
			arg = resArgs(c);
			if ((arg[0].isGeoList()) &&
				(arg[1].isGeoList())) 
			{
				GeoElement[] ret = { 
						kernel.SigmaYY(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1]) };
				return ret;
			}
			default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
