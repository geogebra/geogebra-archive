package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;

/*
 * Rank[ <List> ]
 */
class CmdRank extends CmdOneListFunction {

	public CmdRank(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Rank(a, b);
	}


}
