package geogebra.kernel.commands;

import geogebra.MyError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;

/*
 * Defined[ Object ]
 * Michael Borcherds
 * 2008-03-06
 */
public class CmdStep extends CommandProcessor {

	public CmdStep(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 0:

			GeoElement[] ret = { 
					kernel.Step(c.getLabel() ) };
			return ret;
	
		case 1:
			if (arg[0].isGeoNumeric())
			{
				GeoNumeric num=(GeoNumeric)arg[0];
				if (num.getDouble()==-1)
				{
					GeoElement[] ret2 = { kernel.Step(c.getLabel() ) };
					return ret2;
				}
			}
			GeoElement[] ret3 = { 
					kernel.Step(c.getLabel(), arg[0] ) };
			//kernel.Step(c.getLabel() ) };
			return ret3;
	
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
