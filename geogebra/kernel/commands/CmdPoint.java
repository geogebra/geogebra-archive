package geogebra.kernel.commands;


import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.Path;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;


/*
 * Point[ <Path> ] Point[ <Point>, <Vector> ]
 */
public class CmdPoint extends CommandProcessor {
	
	public CmdPoint (Kernel kernel) {
		super(kernel);
	}
	
public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);
            if (ok[0] = (arg[0].isPath())) {
                GeoElement[] ret =
                    { kernel.Point(c.getLabel(), (Path) arg[0])};
                return ret;
            } else if (ok[0] = (arg[0].isGeoList())) {
                return kernel.PointsFromList(c.getLabels(), (GeoList) arg[0]);
        } else
				throw argErr(app, "Point", arg[0]);

        case 2 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoVector()))) {
                GeoElement[] ret =
                    {
                         kernel.Point(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoVector) arg[1])};
                return ret;
            } else {                
                if (!ok[0])
                    throw argErr(app, "Point", arg[0]);     
                else
                    throw argErr(app, "Point", arg[1]);
            }

        default :
            throw argNumErr(app, "Point", n);
    }
}
}