package geogebra.kernel.commands;


import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.Kernel;
import geogebra.kernel.Path;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
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

        	boolean oldMacroMode = cons.isSuppressLabelsActive();
        	
        	cons.setSuppressLabelCreation(true);	
        	GeoConic circle = kernel.Circle("c", points[0], new MyDouble(kernel, points[0].distance(points[1])));
			cons.setSuppressLabelCreation(oldMacroMode);
			
        	GeoPoint p = kernel.Point(null, (Path)circle, points[1].inhomX, points[1].inhomY, true);
		try {
			cons.replace(points[1], p);
			points[1] = p;
		} catch (Exception e) {
			throw argErr(app, c.getName(), arg[1]);
		}
		
		StringBuilder sb = new StringBuilder();
		
		double xA = points[0].inhomX;
		double yA = points[0].inhomY;
		double xB = points[1].inhomX;
		double yB = points[1].inhomY;
		
		GeoVec2D a = new GeoVec2D(kernel, xB - xA, yB - yA ); // vector AB
		GeoVec2D b = new GeoVec2D(kernel, yA - yB, xB - xA ); // perpendicular to AB
		
		a.makeUnitVector();
		b.makeUnitVector();

		for (int i = 2; i < n; i++) {

			double xC = points[i].inhomX;
			double yC = points[i].inhomY;
			
			GeoVec2D d = new GeoVec2D(kernel, xC - xA, yC - yA ); // vector AC
			
			kernel.setTemporaryPrintFigures(15);
			// make string like this
			// A+3.76UnitVector[Segment[A,B]]+-1.74UnitPerpendicularVector[Segment[A,B]]
			sb.setLength(0);
			sb.append(points[0].getLabel());
			sb.append('+');
			sb.append(kernel.format(a.inner(d)));
	
			sb.append("UnitVector[Segment[");
			sb.append(points[0].getLabel());
			sb.append(',');
			sb.append(points[1].getLabel());
			sb.append("]]+");
			sb.append(kernel.format(b.inner(d)));
			sb.append("UnitPerpendicularVector[Segment[");
			sb.append(points[0].getLabel());
			sb.append(',');
			sb.append(points[1].getLabel());
			sb.append("]]");
			
			kernel.restorePrintAccuracy();
						
				
			//Application.debug(sb.toString());
	
			GeoPoint pp = (GeoPoint)kernel.getAlgebraProcessor().evaluateToPoint(sb.toString());
			
			try {
				cons.replace(points[i], pp);
				points[i] = pp;
				points[i].setEuclidianVisible(false);
				points[i].update();
			} catch (Exception e) {
				throw argErr(app, c.getName(), arg[1]);
			}
        }
		//Application.debug(kernel.format(a.inner(d))+" UnitVector[Segment[A,B]] + "+kernel.format(b.inner(d))+" UnitPerpendicularVector[Segment[A,B]]");
		
		
	        // everything ok
	        return kernel.Polygon(c.getLabels(), points);
		}	
}
}
