package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;

public class CmdImplicitPoly extends CommandProcessor {

	public CmdImplicitPoly(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError
	{
		int n = c.getArgumentNumber();
		if(n == 0 || (int)Math.sqrt(9+8*n) != Math.sqrt(9+8*n))
			throw argNumErr(app, "ImplicitCurve", n);
		
		GeoElement[] arg = resArgs(c);
		for(int i=0; i<n; i++)
			if(!arg[i].isGeoPoint())
				throw argErr(app, "ImplicitCurve", arg[i]);
		
		GeoPoint [] points = new GeoPoint[n];
		for(int i=0; i<n; i++)
			points[i] = (GeoPoint) arg[i];
		
		GeoElement [] ret = { kernel.ImplicitPoly(c.getLabel(), points) };
		
		return ret;
	}

}