package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;

/*
 * Product[ list ]
 * adapted from CmdSum by Michael Borcherds 2008-02-16
 */
class CmdProduct extends CmdOneListFunction {

	public CmdProduct(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Product(a, b);
	}

}
