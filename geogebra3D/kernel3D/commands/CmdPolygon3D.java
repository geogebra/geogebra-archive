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
public class CmdPolygon3D extends CmdPolygon {
	
	Kernel3D kernel3D;

	public CmdPolygon3D(Kernel kernel) {
		super(kernel);
		
		
	}
	
	public CmdPolygon3D(Kernel3D kernel3D) {
		this( (Kernel) kernel3D);
		this.kernel3D = kernel3D;
		
		
	}	
	
	
	public GeoElement[] process(Command c) throws MyError {	


		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;


		
		/*
		if (n==3) {
			arg = resArgs(c);
			if (arg[0].isGeoElement3D() && arg[1].isGeoElement3D() && arg[2].isGeoElement3D()){

				GeoElement3D geo0 = (GeoElement3D) arg[0];
				GeoElement3D geo1 = (GeoElement3D) arg[1];
				GeoElement3D geo2 = (GeoElement3D) arg[2];

				// segment between two 3D points
				if ((ok[0] = (geo0.isGeoPoint3D()))
						&& (ok[1] = (geo1.isGeoPoint3D()))
						&& (ok[2] = (geo2.isGeoPoint3D()))) {
					GeoElement[] ret =
					{
							kernel3D.Triangle3D(
									c.getLabel(),
									(GeoPoint3D) geo0,
									(GeoPoint3D) geo1,
									(GeoPoint3D) geo2)};
					return ret;
				}
			}
		}
		*/
		if (n==3) { //TODO process for more than 3 points
			arg = resArgs(c);
			// polygon for given points
			GeoPoint3D[] points = new GeoPoint3D[n];
			// check arguments
			for (int i = 0; i < n; i++) {
				if (!(arg[1].isGeoElement3D()))
					return super.process(c);	
				else if (!(arg[i].isGeoPoint()))
					return super.process(c);
				else {
					points[i] = (GeoPoint3D) arg[i];
				}
			}
			// everything ok
			GeoElement[] ret = {kernel3D.Triangle3D(c.getLabel(), points)};
			return ret;
		}

		return super.process(c);
	}

}
