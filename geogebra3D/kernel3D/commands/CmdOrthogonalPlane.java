package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CommandProcessor;
import geogebra.kernel.kernel3D.GeoCoordSys;
import geogebra.kernel.kernel3D.GeoCoordSys1D;
import geogebra.kernel.kernel3D.GeoPlane3D;
import geogebra.kernel.kernel3D.GeoPoint3D;
import geogebra.kernel.kernel3D.Kernel3D;
import geogebra.main.MyError;

/*
 * Orthogonal[ <GeoPoint3D>, <GeoCoordSys> ]
 */
public class CmdOrthogonalPlane extends CommandProcessor {
	
	
	
	public CmdOrthogonalPlane(Kernel kernel) {
		super(kernel);
	}
	
	

	public GeoElement[] process(Command c) throws MyError {
	    int n = c.getArgumentNumber();
	    boolean[] ok = new boolean[n];
	    GeoElement[] arg;

	    switch (n) {
	    case 2 :
	    	arg = resArgs(c);
	    	if (
	    			(ok[0] = (arg[0] .isGeoPoint() && arg[0].isGeoElement3D()) )
	    			&& (ok[1] = (arg[1] instanceof GeoCoordSys1D ))
	    	) {
	    		GeoElement[] ret =
	    		{
	    				kernel.getManager3D().OrthogonalPlane3D(
	    						c.getLabel(),
	    						(GeoPoint3D) arg[0],
	    						(GeoElement) arg[1])};
	    		return ret;
	    	}else{
	    		if (!ok[0])
	    			throw argErr(app, "OrthogonalPlane", arg[0]);
	    		else 
	    			throw argErr(app, "OrthogonalPlane", arg[1]);
	    	}
	    	
	    default :
	    	throw argNumErr(app, "OrthogonalPlane", n);
	    }
	    

	}
	
}
