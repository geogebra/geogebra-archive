package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CmdPolygon;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;



/*
 * Polygon[ <GeoPoint3D>, <GeoPoint3D>, <GeoPoint3D> ] or CmdPolygon
 * TODO change command for a n-polygon
 */
public class CmdPyramid extends CmdPolygon {
	
	Kernel3D kernel3D;

	public CmdPyramid(Kernel kernel) {
		super(kernel);
		
		
	}
	
	public CmdPyramid(Kernel3D kernel3D) {
		this( (Kernel) kernel3D);
		this.kernel3D = kernel3D;
		
		
	}	
	
	
	public GeoElement[] process(Command c) throws MyError {	


		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		
		// polygon for given points
        GeoPoint3D[] points = new GeoPoint3D[n];
        // check arguments
        for (int i = 0; i < n; i++) {
            if (!(arg[i].isGeoPoint()))
				throw argErr(app, c.getName(), arg[i]);
			else {
                points[i] = (GeoPoint3D) arg[i];
            }
        }
        // everything ok
        //TODO return kernel3D.Pyramid(c.getLabels(), points);
        return new GeoElement[] {kernel3D.Pyramid(c.getLabel(), points)};
		
		

	}

}
