package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;

/*
 * Sum[ list ]
 * adapted from CmdLcm by Michael Borcherds 2008-02-16
 */
public class CmdSum extends CommandProcessor {

	public CmdSum(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		if (!arg[0].isGeoList()) throw argErr(app, c.getName(), arg[0]);
		GeoList list = (GeoList)arg[0];
		int size = list.size();
		if (size == 0) throw argErr(app, c.getName(), arg[0]);
		
		boolean allNumbers = true;
		boolean allFunctions = true;
		boolean allNumbersVectorsPoints = true;
		
		for (int i =0 ; i< size ; i++) {
			GeoElement geo = list.get(i);
			if (!geo.isGeoFunction()) {
				allFunctions = false;
			}
			if (!geo.isGeoNumeric()) {
				allNumbers = false;
			}
			if (!geo.isGeoNumeric() && !geo.isGeoVector() && !geo.isGeoPoint()) {
				allNumbersVectorsPoints = false;
			}
			
		}

		switch (n) {
		case 1:
				if (allNumbers) 
				{
					GeoElement[] ret = { 
							kernel.Sum(c.getLabel(),
							list) };
					return ret;
				}
				else if (allFunctions) {
					GeoElement[] ret = { 
							kernel.SumFunctions(c.getLabel(),
							list) };
					return ret;
				}
				else if (allNumbersVectorsPoints) {
					GeoElement[] ret = { 
							kernel.SumPoints(c.getLabel(),
							list) };
					return ret;
				}
				else {
					throw argErr(app, c.getName(), arg[0]);
				}

		case 2:
			if (arg[1].isGeoNumeric()) {

				if (allNumbers) {
	
					GeoElement[] ret = { 
							kernel.Sum(c.getLabel(),
							list, (GeoNumeric) arg[1]) };
					return ret;
				}
				else if (allFunctions) {
					GeoElement[] ret = { 
							kernel.SumFunctions(c.getLabel(),
							list, (GeoNumeric) arg[1]) };
					return ret;
			}
				else if (allNumbersVectorsPoints) {
					GeoElement[] ret = { 
							kernel.SumPoints(c.getLabel(),
							list, (GeoNumeric) arg[1]) };
					return ret;
			}
				else {
					throw argErr(app, c.getName(), arg[0]);
				}
			} else
				throw argErr(app, c.getName(), arg[0]);
		
		default:
            // try to create list of numbers
	       	 GeoList wrapList = wrapInList(kernel, arg, GeoElement.GEO_CLASS_NUMERIC);
	            if (wrapList != null) {
	           	 GeoElement[] ret = { kernel.Sum(c.getLabel(), wrapList)};
	                return ret;             	     	 
	            } 
			throw argNumErr(app, c.getName(), n);
		}
	}

}
