package geogebra.algebra;

import geogebra.MyError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;

/*
 * Sequence[ <expression>, <number-var>, <from>, <to> ]
 * Sequence[ <expression>, <number-var>, <from>, <to>, <step> ]  
 */
public class CmdSequence extends CommandProcessor {
	
	public CmdSequence(AlgebraController algCtrl) {
		super(algCtrl);
	}

	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
 
    // create local variable at position 1 and resolve arguments
    GeoElement[] arg = resArgsLocalNumVar(c, 1);      
    
    switch (n) {
    	case 4 :
            if ((ok[0] = arg[0].isGeoElement())
               	 && (ok[1] = arg[1].isGeoNumeric())
               	 && (ok[2] = arg[2].isNumberValue())
               	 && (ok[3] = arg[3].isNumberValue()))
               {
                   return  kernel.Sequence(
                                c.getLabel(),
                                arg[0],
                                (GeoNumeric) arg[1],
                                (NumberValue) arg[2],
                                (NumberValue) arg[3],
                                null);
               } else {          
               	for (int i=0; i < n; i++) {
               		if (!ok[i]) throw argErr(app, c.getName(), arg[i]);	
               	}            	
               }               
    		    		
        case 5 :        	        	                           
            if ((ok[0] = arg[0].isGeoElement())
            	 && (ok[1] = arg[1].isGeoNumeric())
            	 && (ok[2] = arg[2].isNumberValue())
            	 && (ok[3] = arg[3].isNumberValue())
            	 && (ok[4] = arg[4].isNumberValue()) )
            {
                return kernel.Sequence(
                             c.getLabel(),
                             arg[0],
                             (GeoNumeric) arg[1],
                             (NumberValue) arg[2],
                             (NumberValue) arg[3],
                             (NumberValue) arg[4]);
            } else {          
            	for (int i=0; i < n; i++) {
            		if (!ok[i]) throw argErr(app, c.getName(), arg[i]);	
            	}            	
            }           

        default :
            throw argNumErr(app, c.getLabel(), n);
    }
}
}