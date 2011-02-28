package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.commands.CommandProcessor;
import geogebra.kernel.kernelND.GeoCoordSys;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoVectorND;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoVector3D;
import geogebra3D.kernel3D.Kernel3D;

public class CmdCylinderOpen extends CommandProcessor {
	
	
	
	public CmdCylinderOpen(Kernel kernel) {
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
	    			(ok[0] = (arg[0] .isGeoPoint() ) )
	    			&& (ok[1] = (arg[1] .isGeoPoint()))
	    			&& (ok[2] = (arg[2] .isNumberValue() )) 
	    	) {
	    		return cylinderPointPointRadius(
    					c,
    					(GeoPointND) arg[0],
    					(GeoPointND) arg[1],
    					(NumberValue) arg[2]);
	    	}else{
	    		if (!ok[0])
	    			throw argErr(arg[0]);
	    		else if (!ok[1])
	    			throw argErr(arg[1]);
	    		else
	    			throw argErr(arg[2]);
	    	}

	    default :
	    	throw argNumErr(n);
	    }
	    

	}
	
	
	protected GeoElement[] cylinderPointPointRadius(Command c, GeoPointND p1, GeoPointND p2, NumberValue r){
		return new GeoElement[] { kernel.getManager3D().CylinderOpen(
				c.getLabel(),p1,p2,r)};
	}
	
	protected MyError argErr(GeoElement geo){
		return argErr(app,"CylinderOpen",geo);
	}
	
	protected MyError argNumErr(int n){
		return argNumErr(app,"CylinderOpen",n);
	}
	
}
