package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CommandProcessor;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoCoordSys;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;

/*
 * Orthogonal[ <GeoPoint3D>, <GeoCoordSys> ]
 */
public class CmdOrthogonalPlane extends CommandProcessor {
	
	
	Kernel3D kernel3D;
	
	
	public CmdOrthogonalPlane(Kernel kernel) {
		super(kernel);
	}

	public CmdOrthogonalPlane(Kernel3D kernel3D) {
		this( (Kernel) kernel3D);
		this.kernel3D = kernel3D;
		
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
	    				kernel3D.OrthogonalPlane3D(
	    						c.getLabel(),
	    						(GeoPoint3D) arg[0],
	    						(GeoCoordSys) arg[1])};
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
