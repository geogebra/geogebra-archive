package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.MyError;

/*
 * abstract class for Commands with two numberical arguments eg Binomial[ <Number>, <Number> ]
 * Michael Borcherds 2008-04-12
 */
public abstract class CmdTwoNumFunction extends CommandProcessor {

	public CmdTwoNumFunction(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		
		case 2:			
			arg = resArgs(c);
			if ((arg[0].isNumberValue()) &&
				(arg[1].isNumberValue())) 
			{
				GeoElement[] ret = { 
						doCommand(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]) };
				return ret;
				
			}  else
				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
	
    abstract protected GeoElement doCommand(String a, NumberValue b, NumberValue c);     
}

/*
 * Binomial[ <Number>, <Number> ]
 * Michael Borcherds 2008-04-12
 */
class CmdBinomial extends CmdTwoNumFunction {

	public CmdBinomial(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernel.Binomial(a, b, c);
	}
}

/*
 * Div[ <Number>, <Number> ]
 */
class CmdDiv extends CmdTwoNumFunction {

	public CmdDiv(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernel.Div(a, b, c);
	}

}

/*
 * Mod[ <Number>, <Number> ]
 */
class CmdMod extends CmdTwoNumFunction {

	public CmdMod(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernel.Mod(a, b, c);
	}
}

/*
 * RandomNormal[ <Number>, <Number> ]
 */
class CmdRandomNormal extends CmdTwoNumFunction {

	public CmdRandomNormal(Kernel kernel) {
		super(kernel);
	}

	protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernel.RandomNormal(a, b, c);
	}

}

/*
 * Random[ <Number>, <Number> ]
 */
class CmdRandom extends CmdTwoNumFunction {

	public CmdRandom(Kernel kernel) {
		super(kernel);
	}

	protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernel.Random(a, b, c);
	}

}

/*
 * RandomBinomial[ <Number>, <Number> ]
 */
class CmdRandomBinomial extends CmdTwoNumFunction {

	public CmdRandomBinomial(Kernel kernel) {
		super(kernel);
	}

	protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernel.RandomBinomial(a, b, c);
	}

}






