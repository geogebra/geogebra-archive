package geogebra.kernel.commands;

import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;

/*
 * Name[ <GeoElement> ]
 */
public class CmdLaTeX extends CommandProcessor {

	public CmdLaTeX(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			
			arg = resArgs(c);	

				GeoElement[] ret = { kernel.LaTeX(c.getLabel(),
									arg[0]) };
				return ret;

		case 2:
			
			arg = resArgs(c);	
			if (arg[1].isGeoBoolean()) {
				GeoElement[] ret2 = { kernel.LaTeX(c.getLabel(),
									arg[0], (GeoBoolean)arg[1]) };
				return ret2;
			}
			else
           	 	throw argErr(app, c.getName(), arg[1]);         



		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
