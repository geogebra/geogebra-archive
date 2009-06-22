package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Path;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CmdPoint;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.Kernel3D;



/*
 * Point[ <Path1D> ] or CmdPoint
 */
public class CmdPoint3D extends CmdPoint {
	
	Kernel3D kernel3D;

	public CmdPoint3D(Kernel kernel) {
		super(kernel);
		
		
	}
	
	public CmdPoint3D(Kernel3D kernel3D) {
		this( (Kernel) kernel3D);
		this.kernel3D = kernel3D;
		
		
	}	
	
	
	public GeoElement[] process(Command c) throws MyError {	


		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;



		switch (n) {
		case 1 :
			arg = resArgs(c);
			if (arg[0].isGeoElement3D() ){

				GeoElement3D geo0 = (GeoElement3D) arg[0];
				if (ok[0] = (geo0.isPath())) {
					GeoElement[] ret =
					{ kernel3D.Point3D(c.getLabel(), (Path) geo0)};
					return ret;
				}
			}


		default :

		}


		return super.process(c);
	}

}
