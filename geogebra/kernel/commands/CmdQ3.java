package geogebra.kernel.commands;

import geogebra.MyError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;

/*
 * Sum[ list ]
 * adapted from CmdLcm by Michael Borcherds 2008-02-16
 */
public class CmdQ3 extends CmdOneListFunction {

	public CmdQ3(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Q3(a, b);
	}


}
