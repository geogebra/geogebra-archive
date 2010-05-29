package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;
import geogebra.main.MyError;

/*
 * , (NumberValue) arg[1][ <Number>, <Number>,<Number> ]
 * 
 * adapted from CmdMax by Michael Borcherds 2008-01-20
 */
public class CmdNormal extends CommandProcessor {

	public CmdNormal(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;
		GeoElement[] arg;

		switch (n) {
		case 3:			
			arg = resArgs(c);
			if ((ok = arg[0].isNumberValue()) && (arg[1].isNumberValue())) {
				if (arg[2].isGeoFunction() && ((GeoFunction)arg[2]).toString().equals("x")) {
									
					String mean = arg[0].getLabel();
					String sd = arg[1].getLabel();
					
					GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand( "1/sqrt(2 * pi) / abs("+sd+")*exp(-((x-("+mean+"))/("+sd+"))^2/2)", true );
					
					return ret;
					
				} else if (arg[2].isNumberValue()) 
				{
					GeoElement[] ret = { 
							kernel.Normal(c.getLabel(),
							(NumberValue) arg[0], (NumberValue) arg[1], (NumberValue) arg[2]) };
					return ret;
					
				}  else
					throw argErr(app, c.getName(), arg[2]);
		} else throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
