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
		case 0:
			throw argNumErr(app, c.getName(), n);
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

/** 
 * FitGrowth[<List of points>]
 * 
 * @author Hans-Petter Ulven
 * @version 2010-02-25
 */
class CmdFitGrowth extends CmdOneListFunction{

    public CmdFitGrowth(Kernel kernel) {super(kernel);}
    
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.FitGrowth(a, b);
	}
}//class CmdFitGrowth

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

class CmdVoronoi extends CmdOneListFunction {

	public CmdVoronoi(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Voronoi(a, b);
	}

}

class CmdConvexHull extends CmdOneListFunction {

	public CmdConvexHull(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.ConvexHull(a, b);
	}

}

class CmdMinimumSpanningTree extends CmdOneListFunction {

	public CmdMinimumSpanningTree(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.MinimumSpanningTree(a, b);
	}

}

class CmdDelauneyTriangulation extends CmdOneListFunction {

	public CmdDelauneyTriangulation(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.DelauneyTriangulation(a, b);
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
 * ReducedRowEchelonForm[ <List> ]
 * Michael Borcherds 
 */
class CmdReducedRowEchelonForm extends CmdOneListFunction {

	public CmdReducedRowEchelonForm(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.ReducedRowEchelonForm(a, b);
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
 * Determinant[ <List> ]
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
 * SampleSD[ list ]
 * adapted from CmdVariance by Michael Borcherds 2008-02-18
 */
class CmdSampleSD extends CmdOneListFunction {

	public CmdSampleSD(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.SampleStandardDeviation(a, b);
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
 * Rank[ <List> ]
 */
class CmdRank extends CmdOneListFunction {

	public CmdRank(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Rank(a, b);
	}


}

/*
 * Shuffle[ <List> ]
 */
class CmdShuffle extends CmdOneListFunction {

	public CmdShuffle(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Shuffle(a, b);
	}


}

/*
 * Shuffle[ <List> ]
 */
class CmdRandomElement extends CmdOneListFunction {

	public CmdRandomElement(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.RandomElement(a, b);
	}


}

/*
 * PointList[ <List> ]
 */
class CmdPointList extends CmdOneListFunction {

	public CmdPointList(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.PointList(a, b);
	}


}

/*
 * PointList[ <List> ]
 */
class CmdRootList extends CmdOneListFunction {

	public CmdRootList(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.RootList(a, b);
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
	

}

/*
 * SampleVariance[ list ]
 * adapted from CmdSum by Michael Borcherds 2008-02-16
 */
class CmdSampleVariance extends CmdOneListFunction {

	public CmdSampleVariance(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.SampleVariance(a, b);
	}
	

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



