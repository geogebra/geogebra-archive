package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;

/*
 * Row[ <Number>, <Number> ]
 * Row[ <Number> ]
 */
public class CmdRow extends CmdTwoNumFunction {

	public CmdRow(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernel.Row(a, b, c);
	}


}
