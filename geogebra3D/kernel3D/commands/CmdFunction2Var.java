package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunctionable;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.commands.CmdFunction;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;



/**
 * Command for 2 var functions
 */
public class CmdFunction2Var extends CmdFunction {
	
	Kernel3D kernel3D;

	public CmdFunction2Var(Kernel kernel) {
		super(kernel);
	}

	public CmdFunction2Var(Kernel3D kernel3D) {
		this( (Kernel) kernel3D);
		this.kernel3D = kernel3D;


	}	
	

	public GeoElement[] process(Command c) throws MyError {	


		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg = resArgs(c);



		switch (n) {
		case 6 :            	                
			if (
					(ok[0] = (arg[0] .isNumberValue()))
					&& (ok[1] = (arg[1] .isNumberValue()))
					&& (ok[2] = (arg[2] .isNumberValue()))
					&& (ok[3] = (arg[3] .isNumberValue()))
					&& (ok[4] = (arg[4] .isNumberValue()))
					&& (ok[5] = (arg[5] .isNumberValue()))
					
			) {
				GeoElement[] ret =
				{
						kernel3D.Function2Var(
								c.getLabel(),
								(NumberValue) arg[0],
								(NumberValue) arg[1],
								(NumberValue) arg[2],
								(NumberValue) arg[3],
								(NumberValue) arg[4],
								(NumberValue) arg[5]							
						)
				};
				return ret;
			}                                
			else {
				int i=0;
				while (i<6 && ok[i])
					i++;
				throw argErr(app, "Function", arg[i]);
			}

		}

		return super.process(c);
	}

}
