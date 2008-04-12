package geogebra.kernel.commands;

import geogebra.MyError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;

/*
 * Mod[ <Number>, <Number> ]
 */
public class CmdMod extends CmdTwoNumFunction {

	public CmdMod(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernel.Mod(a, b, c);
	}
}
