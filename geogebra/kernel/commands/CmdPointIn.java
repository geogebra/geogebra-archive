package geogebra.kernel.commands;


import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Region;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;


/*
 * PointIn[ <Region> ] 
 */
public class CmdPointIn extends CommandProcessor {

	public CmdPointIn (Kernel kernel) {
		super(kernel);
	}

	public  GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		if (n==1) {
			arg = resArgs(c);
			if (ok[0] = (arg[0].isRegion())) {
				GeoElement[] ret =
				{ kernel.PointIn(c.getLabel(), (Region) arg[0])};
				return ret;
			} else
				throw argErr(app, "Point", arg[0]);
		}else
			throw argNumErr(app, "Point", n);


	}
}