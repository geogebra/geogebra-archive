package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CmdRay;
import geogebra.kernel.kernel3D.GeoElement3D;
import geogebra.kernel.kernel3D.GeoPoint3D;
import geogebra.kernel.kernel3D.Kernel3D;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.MyError;



/*
 * Ray[ <GeoPoint3D>, <GeoPoint3D> ] or CmdRay
 */
public class CmdRay3D extends CmdRay {
	

	public CmdRay3D(Kernel kernel) {
		super(kernel);
		
	}

	

	public GeoElement[] process(Command c) throws MyError {	


		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;


		if (n==2) {
            arg = resArgs(c);
            if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()){
            	
            	GeoElement geo0 = (GeoElement) arg[0];
            	GeoElement geo1 = (GeoElement) arg[1];
            	
            	if ((ok[0] = (geo0.isGeoPoint()))
            			&& (ok[1] = (geo1.isGeoPoint()))) {
            		GeoElement[] ret =
            		{
            				kernel.getManager3D().Ray3D(
            						c.getLabel(),
            						(GeoPointND) geo0,
            						(GeoPointND) geo1)};
            		return ret;
            	}
            }
	    }

		return super.process(c);
	}

}
