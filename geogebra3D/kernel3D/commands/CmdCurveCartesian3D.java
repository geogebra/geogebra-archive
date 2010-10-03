package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.commands.CmdCurveCartesian;
import geogebra.kernel.commands.CmdLine;
import geogebra.kernel.kernel3D.GeoElement3D;
import geogebra.kernel.kernel3D.GeoPoint3D;
import geogebra.kernel.kernel3D.Kernel3D;
import geogebra.main.MyError;



/*
 * Line[ <GeoPoint3D>, <GeoPoint3D> ] or CmdLine
 */
public class CmdCurveCartesian3D extends CmdCurveCartesian {
	
	public CmdCurveCartesian3D(Kernel kernel) {
		super(kernel);
		
	}
	

	public GeoElement[] process(Command c) throws MyError {	


		int n = c.getArgumentNumber();
	    boolean[] ok = new boolean[n];
	 
	    if (n==6){
	    	// Curve[ <x-coord expression>,  <y-coord expression>,  <z-coord expression>, <number-var>, <from>, <to> ] 
	    	// Note: x and y and z coords are numbers dependent on number-var

	    	// create local variable at position 3 and resolve arguments
	    	GeoElement[] arg = resArgsLocalNumVar(c, 3, 4);      

	    	if ((ok[0] = arg[0].isNumberValue())
	    			&& (ok[1] = arg[1].isNumberValue())
	    			&& (ok[2] = arg[2].isNumberValue())
	    			&& (ok[3] = arg[3].isGeoNumeric())
	    			&& (ok[4] = arg[4].isNumberValue())
	    			&& (ok[5] = arg[5].isNumberValue()))
	    	{
	    		GeoElement [] ret = new GeoElement[1];
	    		ret[0] = kernel.CurveCartesian3D(
	    				c.getLabel(),
	    				(NumberValue) arg[0],
	    				(NumberValue) arg[1],
	    				(NumberValue) arg[2],
	    				(GeoNumeric) arg[3],
	    				(NumberValue) arg[4],
	    				(NumberValue) arg[5]);
	    		return ret;
	    	} else {          
	    		for (int i=0; i < n; i++) {
	    			if (!ok[i]) throw argErr(app, "CurveCartesian", arg[i]);	
	    		}            	
	    	}                   	  

	    }

		return super.process(c);
	}

}
