package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.MyError;

/*
 * Element[ <list>, <n> ]
 * Element[ <point>, <n> ]
 */
public class CmdElement extends CommandProcessor {
	
	public CmdElement(Kernel kernel) {
		super(kernel);
	}

	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;
    
    switch (n) {    	
	case 2 :
		arg = resArgs(c);
		// list
        if ((ok[0] = arg[0].isGeoList())
           	 && (ok[1] = arg[1].isNumberValue()))
           {
        	GeoElement[] ret = {  kernel.Element(
                            c.getLabel(),
                            (GeoList) arg[0],
                            (NumberValue) arg[1]) };
               return ret; 
           } 
                 
        // error
        else {          
        	for (int i=0; i < n; i++) {
        		if (!ok[i]) throw argErr(app, c.getName(), arg[i]);	
        	}            	
        }  
	case 3 :
		arg = resArgs(c);
		// list
        if ((ok[0] = arg[0].isGeoList())
              	 && (ok[1] = arg[1].isNumberValue())
          	 && (ok[2] = arg[2].isNumberValue()))
           {
        	GeoElement[] ret = {  kernel.Element(
                            c.getLabel(),
                            (GeoList) arg[0],
                            (NumberValue) arg[1],
                            (NumberValue) arg[2]) };
               return ret; 
           }   
                 
        // error
        else {          
        	for (int i=0; i < n; i++) {
        		if (!ok[i]) throw argErr(app, c.getName(), arg[i]);	
        	}            	
        }  

        default :
            throw argNumErr(app, c.getName(), n);
    }
}
}