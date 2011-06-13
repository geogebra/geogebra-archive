package geogebra.kernel.commands;

import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoFunctionable;
import geogebra.kernel.GeoImplicitPoly;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.MyError;


/*
 * Intersection[ <GeoList>, <GeoList> ]
 */
public class CmdIntersection extends CommandProcessor {
	
	public CmdIntersection(Kernel kernel) {
		super(kernel);
	}
	
public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);
            if (arg[0].isGeoList() && arg[1].isGeoList() ) {
				GeoElement[] ret = { 
						kernel.Intersection(c.getLabel(),
						(GeoList) arg[0], (GeoList)arg[1] ) };
				return ret;
			} 
            
			else {
                if (!ok[0])
                    throw argErr(app, "Intersection", arg[0]);
                else
                    throw argErr(app, "Intersection", arg[1]);
            }


        default :
            throw argNumErr(app, "Intersection", n);
    }
}
}