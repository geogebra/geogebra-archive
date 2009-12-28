package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CmdPolygon;
import geogebra.kernel.commands.CommandProcessor;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;



/*
 * Prism[ <GeoPoint3D>, <GeoPoint3D>, <GeoPoint3D>, ... ] 
 */
public class CmdPrism extends CommandProcessor {
	
	Kernel3D kernel3D;

	public CmdPrism(Kernel kernel) {
		super(kernel);
		
		
	}
	
	public CmdPrism(Kernel3D kernel3D) {
		this( (Kernel) kernel3D);
		this.kernel3D = kernel3D;
		
		
	}	
	
	
	public GeoElement[] process(Command c) throws MyError {	


		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		
		// polygon for given points
        GeoPoint3D[] points = new GeoPoint3D[n];
        // check arguments
        for (int i = 0; i < n; i++) {
            if (!(arg[i].isGeoPoint()))
				throw argErr(app, c.getName(), arg[i]);
			else {
                points[i] = (GeoPoint3D) arg[i];
            }
        }
        // everything ok
        return kernel3D.Prism(c.getLabel(), points);
		
		

	}

}
