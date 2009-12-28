package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CommandProcessor;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;



/*
 * Polyhedron[ <GeoList> ] 
 */
public class CmdPolyhedron extends CommandProcessor {
	
	Kernel3D kernel3D;

	public CmdPolyhedron(Kernel kernel) {
		super(kernel);
		
		
	}
	
	public CmdPolyhedron(Kernel3D kernel3D) {
		this( (Kernel) kernel3D);
		this.kernel3D = kernel3D;
		
		
	}	
	
	
	public GeoElement[] process(Command c) throws MyError {	


		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		
       // check arguments
		if (n!=1)
			throw argNumErr(app, c.getName(), n);
		else if (!(arg[0].isGeoList()))
			throw argErr(app, c.getName(), arg[0]);
		else 
			return kernel3D.Polyhedron(c.getLabel(), (GeoList) arg[0]);

		

	}

}
