package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;

public class CmdImplicitPoly extends CmdOneListFunction {

	public CmdImplicitPoly(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement doCommand(String a, GeoList b) {
		int n = b.size();
		if(n == 0 || (int)Math.sqrt(9+8*n) != Math.sqrt(9+8*n))
			throw argNumErr(app, "ImplicitCurve", n);
		
		for(int i=0; i<n; i++)
			if(!b.get(i).isGeoPoint())
				throw argErr(app, "ImplicitCurve", b.get(i));
		
		GeoElement ret = kernel.ImplicitPoly(a, b);
		
		return ret;
	}
}