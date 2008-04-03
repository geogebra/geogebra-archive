package geogebra.kernel.commands;

import geogebra.MyError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunctionable;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;

class CmdExpand extends CommandProcessor {
	
	public CmdExpand (Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
     int n = c.getArgumentNumber();
     boolean[] ok = new boolean[n];
     GeoElement[] arg;
     arg = resArgs(c);
     
     switch (n) {
         case 1 :             
             if (ok[0] = (arg[0] .isGeoFunctionable())) {
                 GeoElement[] ret =
                     {
                          kernel.Expand(
                             c.getLabel(), (GeoFunction)arg[0])};
                 return ret;                
             }                        
              else
            	 throw argErr(app, c.getName(), arg[0]);         
			 
	     // more than one argument
         default :
            	 throw argNumErr(app, c.getName(), n);
     }
 }    
}
