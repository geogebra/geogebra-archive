package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CommandProcessor;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoCoordSys;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;

public class CmdPlane extends CommandProcessor {
	
	
	Kernel3D kernel3D;
	
	
	public CmdPlane(Kernel kernel) {
		super(kernel);
	}

	public CmdPlane(Kernel3D kernel3D) {
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
	    			&& (ok[1] = (arg[1] instanceof GeoCoordSys ))
	    	) {
	    		GeoElement[] ret =
	    		{
	    				kernel3D.Plane3D(
	    						c.getLabel(),
	    						(GeoPoint3D) arg[0],
	    						(GeoCoordSys) arg[1])};
	    		return ret;
	    	}else{
	    		if (!ok[0])
	    			throw argErr(app, "Plane", arg[0]);
	    		else 
	    			throw argErr(app, "Plane", arg[1]);
	    	}
	    	
	    case 3 :
	    	arg = resArgs(c);
	    	if ((ok[0] = (arg[0] .isGeoPoint() && arg[0].isGeoElement3D()) )
	    			&& (ok[1] = (arg[1] .isGeoPoint() && arg[1].isGeoElement3D() ))
	    			&& (ok[2] = (arg[2] .isGeoPoint() && arg[2].isGeoElement3D() ))) {
	    		GeoElement[] ret =
	    		{
	    				kernel3D.Plane3D(
	    						c.getLabel(),
	    						(GeoPoint3D) arg[0],
	    						(GeoPoint3D) arg[1],
	    						(GeoPoint3D) arg[2])};
	    		return ret;
	    	}else{
	    		if (!ok[0])
	    			throw argErr(app, "Plane", arg[0]);
	    		else if (!ok[1])
	    			throw argErr(app, "Plane", arg[1]);
	    		else
	    			throw argErr(app, "Plane", arg[2]);
	    	}

	    default :
	    	throw argNumErr(app, "Plane", n);
	    }
	    

	}
	
}
