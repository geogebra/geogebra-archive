package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;

/*
 * Div[ <Number>, <Number> ]
 */
public class CmdDiv extends CmdTwoNumFunction {

	public CmdDiv(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernel.Div(a, b, c);
	}

}
