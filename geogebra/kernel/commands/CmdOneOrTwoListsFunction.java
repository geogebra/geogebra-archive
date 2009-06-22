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
public abstract class CmdOneOrTwoListsFunction extends CommandProcessor {

	public CmdOneOrTwoListsFunction(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { 
						doCommand(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		
		case 2:			
			arg = resArgs(c);
			if ((arg[0].isGeoList()) &&
				(arg[1].isGeoList())) 
			{
				GeoElement[] ret = { 
						doCommand(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1]) };
				return ret;
				
			}  else
				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}

	}
	
    abstract protected GeoElement doCommand(String a, GeoList b);     
    abstract protected GeoElement doCommand(String a, GeoList b, GeoList c);     
}


class CmdSXX extends CmdOneOrTwoListsFunction {

	public CmdSXX(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.SXX(a, b);
	}

	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return kernel.SXX(a, b, c);
	}


}

class CmdSXY extends CmdOneOrTwoListsFunction {

	public CmdSXY(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.SXY(a, b);
	}

	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return kernel.SXY(a, b, c);
	}

}

class CmdSYY extends CmdOneListFunction {

	public CmdSYY(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.SYY(a, b);
	}
}

class CmdCovariance extends CmdOneOrTwoListsFunction {

	public CmdCovariance(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Covariance(a, b);
	}

	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return kernel.Covariance(a, b, c);
	}


}

class CmdMeanX extends CmdOneOrTwoListsFunction {

	public CmdMeanX(Kernel kernel) {
		super(kernel);
	}
	
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { 
						doCommand(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.MeanX(a, b);
	}

	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		throw argErr(app, a, b);
	}


}

class CmdMeanY extends CmdOneOrTwoListsFunction {

	public CmdMeanY(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { 
						doCommand(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

		final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.MeanY(a, b);
	}

	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return null;//kernel.MeanY(a, b, c);
	}


}

class CmdPMCC extends CmdOneOrTwoListsFunction {

	public CmdPMCC(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.PMCC(a, b);
	}

	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return kernel.PMCC(a, b, c);
	}


}

/*
 * SigmaXX[ list ]
 * adapted from CmdVariance by Michael Borcherds 2008-02-18
 */
class CmdSigmaXX extends CmdOneListFunction {

	public CmdSigmaXX(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.SigmaXX(a, b);
	}
}

class CmdSigmaXY extends CmdOneListFunction {

	public CmdSigmaXY(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.SigmaXY(a, b);
	}

	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return kernel.SigmaXY(a, b, c);
	}


}

/*
 * SigmaYY[ list ]
 * adapted from CmdVariance by Michael Borcherds 2008-02-18
 */
class CmdSigmaYY extends CmdOneListFunction {

	public CmdSigmaYY(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.SigmaYY(a, b);
	}

}



