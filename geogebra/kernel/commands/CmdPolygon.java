package geogebra.kernel.commands;


import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.MyError;


/*
 * Polygon[ <GeoPoint>, ..., <GeoPoint> ]
 * Polygon[ <GeoPoint>, <GeoPoint>, <Number>] for regular polygon
 */
public class CmdPolygon extends CommandProcessor {
	
	public CmdPolygon(Kernel kernel) {
		super(kernel);
	}
	
public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    GeoElement[] arg;

    arg = resArgs(c);
    switch (n) {
    	case 3:        
        // regular polygon
        if (arg[0].isGeoPoint() && 
	        arg[1].isGeoPoint() &&
	        arg[2].isNumberValue())
				return kernel.RegularPolygon(c.getLabels(), (GeoPoint) arg[0], (GeoPoint) arg[1], (NumberValue) arg[2]);		
        
        // polygon operation
        if (arg[0].isGeoPolygon() && 
	        arg[1].isGeoPolygon() &&
	        arg[2].isNumberValue())
				return kernel.PolygonOperation(c.getLabels(), (GeoPolygon) arg[0], (GeoPolygon) arg[1],(NumberValue) arg[2]);		
        
        
        
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
	        return kernel.Polygon(c.getLabels(), points);
		}	
}
}
