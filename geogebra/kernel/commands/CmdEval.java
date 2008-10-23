package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;

/*
 * Name[ <GeoElement> ]
 */
public class CmdEval extends CommandProcessor {

	public CmdEval(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			// Name[ <GeoElement> ]
			arg = resArgs(c);	
			if (arg[0].isGeoText()) {
				GeoElement[] ret = { kernel.Eval(c.getLabel(),
								(GeoText)arg[0]) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}



