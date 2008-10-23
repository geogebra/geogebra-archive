package geogebra.kernel.commands;

import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;

/*
 * Perimeter[ <GeoPolygon> ]
 * Perimeter[ <Conic> ]
 */
public class CmdPerimeter extends CommandProcessor {

	public CmdPerimeter(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			// Circumference[ <GeoPolygon> ]
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoPolygon())) {

				GeoElement[] ret = { kernel.Perimeter(c.getLabel(),
						(GeoPolygon) arg[0]) };
				return ret;

				// Circumference[ <Conic> ]
			} else if (ok[0] = (arg[0].isGeoConic())) {

				GeoElement[] ret = { kernel.Circumference(c.getLabel(),
						(GeoConic) arg[0]) };
				return ret;

			} else
				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

class CmdCircumference extends CmdPerimeter {
	public CmdCircumference(Kernel kernel) {
		super(kernel);
	}
}
