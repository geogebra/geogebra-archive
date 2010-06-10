package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunctionable;
import geogebra.kernel.GeoNumeric;
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



		switch (n) {
		case 7 :   
			// create local variable at position 3 and resolve arguments
	    	GeoElement[] arg = resArgsLocalNumVar(c, new int[] {1,4}, new int[] {2,5});    
	    	
			if (
					(ok[0] = arg[0] .isNumberValue()) //function
	    			&& (ok[1] = arg[1].isGeoNumeric()) //first var
	    			&& (ok[2] = arg[2].isNumberValue()) //from
	    			&& (ok[3] = arg[3].isNumberValue()) //to
	    			&& (ok[4] = arg[4].isGeoNumeric()) //second var
	    			&& (ok[5] = arg[5].isNumberValue()) //from
	    			&& (ok[6] = arg[6].isNumberValue()) //to
					
			) {
				GeoElement[] ret =
				{
						kernel3D.Function2Var(
								c.getLabel(),
								(NumberValue) arg[0],
								(GeoNumeric) arg[1],
								(NumberValue) arg[2],
								(NumberValue) arg[3],
								(GeoNumeric) arg[4],
								(NumberValue) arg[5],							
								(NumberValue) arg[6]							
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
