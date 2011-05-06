package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.Path;
import geogebra.kernel.Region;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CmdMidpoint;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.MyError;



public class CmdMidpoint3D extends CmdMidpoint {
	
	public CmdMidpoint3D(Kernel kernel) {
		super(kernel);
		
		
	}
	
	
	public GeoElement[] process(Command c) throws MyError {	


		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;



		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { (GeoElement) kernel.getManager3D().Midpoint(c.getLabel(),
						(GeoPointND) arg[0], (GeoPointND) arg[1]) };
				return ret;
			} 


		default :

		}


		return super.process(c);
	}

}
