package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.main.MyError;

public class CmdIntersectInterior3D extends CmdIntersect3D{

	public CmdIntersectInterior3D(Kernel kernel) {
		super(kernel);
	}
	
	public  GeoElement[] process(Command c) throws MyError {
	    int n = c.getArgumentNumber();
	    GeoElement[] arg;

	    switch (n) {
	    case 2 :
	    	arg = resArgs(c);
	    	if ((arg[0] instanceof GeoLineND && arg[1] instanceof GeoCoordSys2D)
	    			|| (arg[0] instanceof GeoCoordSys2D && arg[1] instanceof GeoLineND)){

    			GeoElement[] ret =
    			{
    					kernel.getManager3D().Intersect(
    							c.getLabel(),
    							(GeoElement) arg[0],
    							(GeoElement) arg[1])};
    			return ret;
    		}
	    
	    }
	    
	    return super.process(c);
	}

}
