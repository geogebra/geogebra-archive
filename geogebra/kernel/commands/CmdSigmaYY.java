package geogebra.kernel.commands;

import geogebra.MyError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;

/*
 * SigmaXX[ list ]
 * adapted from CmdVariance by Michael Borcherds 2008-02-18
 */
public class CmdSigmaYY extends CmdOneOrTwoListsFunction {

	public CmdSigmaYY(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.SigmaYY(a, b);
	}

	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return kernel.SigmaYY(a, b, c);
	}


}
