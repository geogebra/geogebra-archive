package geogebra.kernel.commands;

import geogebra.MyError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;

/*
 * Reverse[ <List> ]
 * Michael Borcherds 2008-02-16
 */
public class CmdReverse extends CmdOneListFunction {

	public CmdReverse(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Reverse(a, b);
	}


}
