package geogebra.kernel.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;

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
        	if (arg[0].isNumberValue()) {
	            // try to create list of numbers
	       	 GeoList list = wrapInList(kernel, arg, arg.length, GeoElement.GEO_CLASS_NUMERIC);
	            if (list != null) {
	           	 GeoElement[] ret = { doCommand(c.getLabel(), list)};
	                return ret;             	     	 
	            } 
        	} else if (arg[0].isVectorValue()) {
                // try to create list of points (eg FitExp[])
              	 GeoList list = wrapInList(kernel, arg, arg.length, GeoElement.GEO_CLASS_POINT);
                   if (list != null) {
                  	 GeoElement[] ret = { doCommand(c.getLabel(), list)};
                       return ret;             	     	 
                   } 
        		
        	}
			throw argNumErr(app, c.getName(), n);
		}
	}
	
    abstract protected GeoElement doCommand(String a, GeoList b);     
}

/** 
 * FitExp[<List of points>]
 * 
 * @author Hans-Petter Ulven
 * @version 12.04.08
 */
class CmdFitExp extends CmdOneListFunction{

    public CmdFitExp(Kernel kernel) {super(kernel);}
    
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.FitExp(a, b);
	}

}// class CmdFitExp

/*
 * 
 * FitLineX[list of points]
 * adapted from CmdLcm by Michael Borcherds 2008-01-14
 */
class CmdFitLineX extends CmdOneListFunction {

	public CmdFitLineX(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.FitLineX(a, b);
	}


}

/*
 * 
 * FitLineY[list of points]
 * adapted from CmdLcm by Michael Borcherds 2008-01-14
 */
class CmdFitLineY extends CmdOneListFunction {

	public CmdFitLineY(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.FitLineY(a, b);
	}

}

/** 
 * FitLog[<List of points>]
 * 
 * @author Hans-Petter Ulven
 * @version 12.04.08
 */

class CmdFitLog extends CmdOneListFunction{

    public CmdFitLog(Kernel kernel) {super(kernel);}
    
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.FitLog(a, b);
	}

}// class CmdFitLog

/** 
 * FitPow[<List of points>]
 * 
 * @author Hans-Petter Ulven
 * @version 07.04.08
 */
class CmdFitPow extends CmdOneListFunction{

    public CmdFitPow(Kernel kernel) {super(kernel);}
    
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.FitPow(a, b);
	}

}// class CmdFitPow

/** 
 * FitSin[<List of points>]
 * 
 * @author Hans-Petter Ulven
 * @version 15.11.08
 */
class CmdFitSin extends CmdOneListFunction{

    public CmdFitSin(Kernel kernel) {super(kernel);}
    
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.FitSin(a, b);
	}

}// class CmdFitSin

/** 
 * FitLogistic[<List of points>]
 * 
 * @author Hans-Petter Ulven
 * @version 15.11.08
 */
class CmdFitLogistic extends CmdOneListFunction{

    public CmdFitLogistic(Kernel kernel) {super(kernel);}
    
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.FitLogistic(a, b);
	}

}// class CmdFitLogistic
/*
 * Mean[ list ]
 *  Michael Borcherds 2008-04-12
 */
class CmdMean extends CmdOneListFunction {

	public CmdMean(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Mean(a, b);
	}

}

/*
 * Sum[ list ]
 * adapted from CmdLcm by Michael Borcherds 2008-02-16
 */
class CmdMedian extends CmdOneListFunction {

	public CmdMedian(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Median(a, b);
	}


}

/*
 * Mode[ <List> ]
 */
class CmdMode extends CmdOneListFunction {

	public CmdMode(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Mode(a, b);
	}

}

/*
 * Sum[ list ]
 * adapted from CmdLcm by Michael Borcherds 2008-02-16
 */
class CmdQ1 extends CmdOneListFunction {

	public CmdQ1(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Q1(a, b);
	}


}

/*
 * Sum[ list ]
 * adapted from CmdLcm by Michael Borcherds 2008-02-16
 */
class CmdQ3 extends CmdOneListFunction {

	public CmdQ3(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Q3(a, b);
	}


}

/*
 * Reverse[ <List> ]
 * Michael Borcherds 2008-02-16
 */
class CmdReverse extends CmdOneListFunction {

	public CmdReverse(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Reverse(a, b);
	}


}

/*
 * Invert[ <List> ]
 * Michael Borcherds 
 */
class CmdInvert extends CmdOneListFunction {

	public CmdInvert(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Invert(a, b);
	}


}

/*
 * Transpose[ <List> ]
 * Michael Borcherds 
 */
class CmdTranspose extends CmdOneListFunction {

	public CmdTranspose(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Transpose(a, b);
	}

}

/*
 * Transpose[ <List> ]
 * Michael Borcherds 
 */
class CmdDeterminant extends CmdOneListFunction {

	public CmdDeterminant(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Determinant(a, b);
	}

}

/*
 * SD[ list ]
 * adapted from CmdVariance by Michael Borcherds 2008-02-18
 */
class CmdSD extends CmdOneListFunction {

	public CmdSD(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.StandardDeviation(a, b);
	}


}

/*
 * Sort[ <List> ]
 */
class CmdSort extends CmdOneListFunction {

	public CmdSort(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Sort(a, b);
	}


}

/*
 * Variance[ list ]
 * adapted from CmdSum by Michael Borcherds 2008-02-16
 */
class CmdVariance extends CmdOneListFunction {

	public CmdVariance(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Variance(a, b);
	}
	
	/*
	 * Product[ list ]
	 * adapted from CmdSum by Michael Borcherds 2008-02-16
	 */
	class CmdProduct extends CmdOneListFunction {

		public CmdProduct(Kernel kernel) {
			super(kernel);
		}

		final protected GeoElement doCommand(String a, GeoList b)
		{
			return kernel.Product(a, b);
		}

	}

}




