package geogebra.kernel.commands;

import geogebra.MyError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;

/*
 * Name[ <GeoElement> ]
 */
public class CmdtoLaTeX extends CommandProcessor {

	public CmdtoLaTeX(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			// Name[ <GeoElement> ]
			arg = resArgs(c);			
			GeoElement[] ret = { kernel.toLaTeX(c.getLabel(),
								arg[0]) };
			return ret;


		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
