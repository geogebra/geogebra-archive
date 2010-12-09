package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.commands.CommandProcessor;
import geogebra.kernel.kernelND.GeoCoordSys;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoVector3D;
import geogebra3D.kernel3D.Kernel3D;

public class CmdCylinder extends CommandProcessor {
	
	
	
	public CmdCylinder(Kernel kernel) {
		super(kernel);
	}

	

	public GeoElement[] process(Command c) throws MyError {
	    int n = c.getArgumentNumber();
	    boolean[] ok = new boolean[n];
	    GeoElement[] arg;

	    switch (n) {	    	
	    case 3 :
	    	arg = resArgs(c);
	    	if (
	    			(ok[0] = (arg[0] .isGeoPoint() && arg[0].isGeoElement3D()) )
	    			&& (ok[1] = (arg[1] .isGeoVector() && arg[1].isGeoElement3D()))
	    			&& (ok[2] = (arg[2] .isNumberValue() )) 
	    	) {
	    		GeoElement[] ret =
	    		{
	    				kernel.getManager3D().Cylinder(
	    						c.getLabel(),
	    						(GeoPoint3D) arg[0],
	    						(GeoVector3D) arg[1],
	    						(NumberValue) arg[2])};
	    		return ret;
	    	}else{
	    		if (!ok[0])
	    			throw argErr(app, "Cylinder", arg[0]);
	    		else if (!ok[1])
	    			throw argErr(app, "Cylinder", arg[1]);
	    		else
	    			throw argErr(app, "Cylinder", arg[2]);
	    	}

	    default :
	    	throw argNumErr(app, "Cylinder", n);
	    }
	    

	}
	
}
