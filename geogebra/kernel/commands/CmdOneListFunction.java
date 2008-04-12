package geogebra.kernel.commands;

import geogebra.MyError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;

/*
 * abstract class for Commands with one list argument eg Mean[ <List> ]
 * 
 * if more than one argument, then they are put into a list
 * 
 * Michael Borcherds 2008-04-12
 */
public abstract class CmdOneListFunction extends CommandProcessor {

	public CmdOneListFunction(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { 
						doCommand(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		
		     // more than one argument
        default :
            // try to create list of numbers
       	 GeoList list = wrapInList(arg, GeoElement.GEO_CLASS_NUMERIC);
            if (list != null) {
           	 GeoElement[] ret = { doCommand(c.getLabel(), list)};
                return ret;             	     	 
            } 
			throw argNumErr(app, c.getName(), n);
		}
	}
	
    abstract protected GeoElement doCommand(String a, GeoList b);     
}
