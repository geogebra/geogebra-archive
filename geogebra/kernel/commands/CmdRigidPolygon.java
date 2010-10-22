package geogebra.kernel.commands;


import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.Kernel;
import geogebra.kernel.Path;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;
import geogebra.main.MyError;


/*
 * Polygon[ <GeoPoint>, ..., <GeoPoint> ]
 * Polygon[ <GeoPoint>, <GeoPoint>, <Number>] for regular polygon
 */
public class CmdRigidPolygon extends CommandProcessor {
	
	public CmdRigidPolygon(Kernel kernel) {
		super(kernel);
	}
	
public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    GeoElement[] arg;

    arg = resArgs(c);
    switch (n) {
    case 0 :
    case 1 :
    case 2 :
	    	throw argNumErr(app, c.getName(), n);
    
        
        default:

			// polygon for given points
	        GeoPoint[] points = new GeoPoint[n];
	        // check arguments
	        for (int i = 0; i < n; i++) {
	            if (!(arg[i].isGeoPoint()))
					throw argErr(app, c.getName(), arg[i]);
				else {
	                points[i] = (GeoPoint) arg[i];
	            }
	        }		
		
	        // everything ok
	        return kernel.RigidPolygon(c.getLabels(), points);
		}	
}
}
