package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CmdSegment;
import geogebra.kernel.kernel3D.GeoElement3D;
import geogebra.kernel.kernel3D.GeoPoint3D;
import geogebra.kernel.kernel3D.Kernel3D;
import geogebra.main.Application;
import geogebra.main.MyError;



/*
 * Segment[ <GeoPoint3D>, <GeoPoint3D> ] or CmdSegment
 */
public class CmdSegment3D extends CmdSegment {
	

	public CmdSegment3D(Kernel kernel) {
		super(kernel);
		
	}
	
	
	
	public GeoElement[] process(Command c) throws MyError {	
		
		
	    int n = c.getArgumentNumber();
	    boolean[] ok = new boolean[n];
	    GeoElement[] arg;

	    
	   if (n==2) {
            arg = resArgs(c);
            if (arg[0].isGeoElement3D() && arg[1].isGeoElement3D()){
            	
            	GeoElement3D geo0 = (GeoElement3D) arg[0];
            	GeoElement3D geo1 = (GeoElement3D) arg[1];
            	
            	// segment between two 3D points
            	if ((ok[0] = (geo0.isGeoPoint()))
            			&& (ok[1] = (geo1.isGeoPoint()))) {
            		GeoElement[] ret =
            		{
            				kernel.Segment3D(
            						c.getLabel(),
            						(GeoPoint3D) geo0,
            						(GeoPoint3D) geo1)};
            		return ret;
            	}
            }
	    }
		
		return super.process(c);
	}

}
