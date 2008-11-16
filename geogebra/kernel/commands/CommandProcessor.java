/*
 * GeoGebra - Dynamic Mathematics for Schools Copyright Markus Hohenwarter and GeoGebra Inc., 
 * http://www.geogebra.org
 * 
 * This file is part of GeoGebra.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 */

package geogebra.kernel.commands;

import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.Construction;
import geogebra.kernel.Dilateable;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoCurveCartesian;
import geogebra.kernel.GeoDeriveable;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoFunctionable;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoVec3D;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.Mirrorable;
import geogebra.kernel.Path;
import geogebra.kernel.PointRotateable;
import geogebra.kernel.Rotateable;
import geogebra.kernel.Translateable;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;
import geogebra.main.MyError;

import java.util.ArrayList;

public abstract class CommandProcessor  {
	    
    Application app;
    Kernel kernel;
    Construction cons;
	private AlgebraProcessor algProcessor;
    
	public CommandProcessor(Kernel kernel) {	      
	     this.kernel = kernel;     
	     cons = kernel.getConstruction();
	     app = kernel.getApplication();	  
	     algProcessor = kernel.getAlgebraProcessor();
	}
	
	/**
	 *  Every CommandProcessor has to implement this method
	 */
   	public abstract GeoElement [] process(Command c) throws MyError, CircularDefinitionException;   	
    
    protected final GeoElement[] resArgs(Command c) throws MyError {	  	
           boolean oldMacroMode = cons.isSuppressLabelsActive();
           cons.setSuppressLabelCreation(true);

           // resolve arguments to get GeoElements                     
           ExpressionNode[] arg = c.getArguments();
           GeoElement[] result = new GeoElement[arg.length];
           
           for (int i = 0; i < arg.length; ++i) {        	           	   
        	   // resolve variables in argument expression        	   
        	   arg[i].resolveVariables();
    
               // resolve i-th argument and get GeoElements
               // use only first resolved argument object for result
        	   result[i] = resArg(arg[i])[0];     
           }
           
           cons.setSuppressLabelCreation(oldMacroMode);
           return result;
       }    

       final GeoElement[] resArg(ExpressionNode arg) throws MyError {
           GeoElement[] geos = algProcessor.processExpressionNode(arg);

           if (geos != null)
			return geos;
	   else {
               String[] str = { "IllegalArgument", arg.toString()};
               throw new MyError(app, str);
           }
       }
        

    	/**
    	 * Resolve arguments of a command that has a local numeric variable
    	 * at the specified position.  
    	 */
    	final GeoElement [] resArgsLocalNumVar(Command c, int varPos) {
    		// check if there is a local variable in arguments    	
    		String localVarName = c.getVariableName(varPos);
    		if (localVarName == null) {        		    
    			throw argErr(app, c.getName(), c.getArgument(varPos));
    		}    		    	
    		
    		// add local variable name to construction 
    		Construction cmdCons = c.getKernel().getConstruction();    		
    	    GeoNumeric num = new GeoNumeric(cmdCons);
    	    cmdCons.addLocalVariable(localVarName, num); 
    	    // set local variable as our varPos argument
    	    c.setArgument(varPos, new ExpressionNode(c.getKernel(), num));
    	     
    	    // resolve all command arguments including the local variable just created
    	    GeoElement [] arg = resArgs(c);                                   
    	    
    	    // remove local variable name from kernel again
    	    cmdCons.removeLocalVariable(localVarName);     	    
    	    return arg;
    	}

        final MyError argErr(Application app, String cmd, Object arg) {
           String localName = app.getCommand(cmd);
           StringBuffer sb = new StringBuffer();
           sb.append(app.getCommand("Command") + " " + localName + ":\n");
           sb.append(app.getError("IllegalArgument") + ": ");
           if (arg instanceof GeoElement)
               sb.append(((GeoElement) arg).getNameDescription());
           else if (arg != null)
               sb.append(arg.toString());
           sb.append("\n\nSyntax:\n" + app.getCommand(cmd + "Syntax"));
           return new MyError(app, sb.toString());
       }

        final MyError argNumErr(
           Application app,
           String cmd,
           int argNumber) {
           StringBuffer sb = new StringBuffer();
           sb.append(
               app.getCommand("Command") + " " + app.getCommand(cmd) + ":\n");
           sb.append(app.getError("IllegalArgumentNumber") + ": " + argNumber);
           sb.append("\n\nSyntax:\n" + app.getCommand(cmd + "Syntax"));
           return new MyError(app, sb.toString());
       }

        final MyError chDepErr(Application app, GeoElement geo) {
           String[] strs = { "ChangeDependent", geo.getLongDescription()};
           return new MyError(app, strs);
       }
        
        
        /**
         * Creates a dependent list with all GeoElement objects from the given array.
         * @param args
         * @param type: -1 for any GeoElement object type; GeoElement.GEO_CLASS_ANGLE, etc. for specific types
         * @return null if GeoElement objects did not have the correct type
         * @author Markus Hohenwarter
         * @date Jan 26, 2008
         */
         public static GeoList wrapInList(Kernel kernel, GeoElement [] args, int type) {
		     Construction cons=kernel.getConstruction();
        	 boolean correctType = true;		        
		   	 ArrayList geoElementList = new ArrayList();
		   	 for (int i=0; i < args.length; i++) {
		   		 if (type < 0 || args[i].getGeoClassType() == type) 
		   			 geoElementList.add(args[i]);
		   		 else {
		   			correctType = false;
		   			break;
		   		 }
		   	 }
		   	 
		   	GeoList list = null;
		   	 if (correctType) {
		   		boolean oldMacroMode = cons.isSuppressLabelsActive();
				cons.setSuppressLabelCreation(true);		
				list = kernel.List(null, geoElementList, false);	
				cons.setSuppressLabelCreation(oldMacroMode);		   		
		   	 }
		   	 
		   	 return list;
        }            	
}



/* *****************************************
 *     Command classes used by CommandDispatcher
 *  *****************************************/


class CmdCenter extends CmdMidpoint {
	public CmdCenter(Kernel kernel) {
		super(kernel);
	}
}
       
/*
 * Midpoint[ <GeoConic> ] Midpoint[ <GeoPoint>, <GeoPoint> ]
 */
class CmdMidpoint extends CommandProcessor {
	
	public CmdMidpoint(Kernel kernel) {
		super(kernel);
	}
	
    public GeoElement[] process(Command c) throws MyError {
        int n = c.getArgumentNumber();
        boolean[] ok = new boolean[n];
        GeoElement[] arg;

        switch (n) {
            case 1 :
                arg = resArgs(c);
                if (ok[0] = (arg[0].isGeoConic())) {
                    GeoElement[] ret =
                        { kernel.Center(c.getLabel(), (GeoConic) arg[0])};
                    return ret;
                } else if (arg[0].isGeoSegment()) {
                    GeoElement[] ret =
                        { kernel.Midpoint(c.getLabel(), (GeoSegment) arg[0])};
                    return ret;
                } else
					throw argErr(app, c.getName(), arg[0]);

            case 2 :
                arg = resArgs(c);
                if ((ok[0] = (arg[0].isGeoPoint()))
                    && (ok[1] = (arg[1].isGeoPoint()))) {
                    GeoElement[] ret =
                        {
                             kernel.Midpoint(
                                c.getLabel(),
                                (GeoPoint) arg[0],
                                (GeoPoint) arg[1])};
                    return ret;
                } else {
                    if (!ok[0])
                        throw argErr(app, c.getName(), arg[0]);
                    else
                        throw argErr(app, c.getName(), arg[1]);
                }

            default :
                throw argNumErr(app, c.getName(), n);
        }
    }    
}

/*
 * Ray[ <GeoPoint>, <GeoPoint> ] Ray[ <GeoPoint>, <GeoVector> ]
 */
class CmdRay extends CommandProcessor {
	
	public CmdRay(Kernel kernel) {
		super(kernel);
	}
	    
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);

            // line through two points
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.Ray(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoPoint) arg[1])};
                return ret;
            }

            // line through point with direction vector
            else if (
                (ok[0] = (arg[0] .isGeoPoint()))
                    && (ok[1] = (arg[1] .isGeoVector()))) {
                GeoElement[] ret =
                    {
                         kernel.Ray(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoVector) arg[1])};
                return ret;
            }

            // syntax error
            else {
                if (!ok[0])
                    throw argErr(app, "Ray", arg[0]);
                else
                    throw argErr(app, "Ray", arg[1]);
            }

        default :
            throw argNumErr(app, "Ray", n);
    }
}
}


/*
 * Orthogonal[ <GeoPoint>, <GeoVector> ] Orthogonal[ <GeoPoint>, <GeoLine> ]
 */
class CmdOrthogonalLine extends CommandProcessor {
	
	public CmdOrthogonalLine(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);

            // line through point orthogonal to vector
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoVector()))) {
                GeoElement[] ret =
                    {
                         kernel.OrthogonalLine(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoVector) arg[1])};
                return ret;
            }

            // line through point orthogonal to another line
            else if (
                (ok[0] = (arg[0] .isGeoPoint()))
                    && (ok[1] = (arg[1] .isGeoLine()))) {
                GeoElement[] ret =
                    {
                         kernel.OrthogonalLine(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoLine) arg[1])};
                return ret;
            }

            // syntax error
            else {
                if (!ok[0])
                    throw argErr(app, "OrthogonalLine", arg[0]);
                else
                    throw argErr(app, "OrthogonalLine", arg[1]);
            }

        default :
            throw argNumErr(app, "OrthogonalLine", n);
    }
}
}


/*
 * LineBisector[ <GeoPoint>, <GeoPoint> ] LineBisector[ <GeoSegment> ]
 */
class CmdLineBisector extends CommandProcessor {
	
	public CmdLineBisector(Kernel kernel) {
		super(kernel);
	}

final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 1 : // segment
            arg = resArgs(c);
            // line through point orthogonal to segment
            if (ok[0] = (arg[0] .isGeoSegment())) {
                GeoElement[] ret =
                    {
                         kernel.LineBisector(
                            c.getLabel(),
                            (GeoSegment) arg[0])};
                return ret;
            }

            // syntax error
            else
                throw argErr(app, "LineBisector", arg[0]);

        case 2 : // two points
            arg = resArgs(c);

            // line through point orthogonal to vector
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.LineBisector(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoPoint) arg[1])};
                return ret;
            }

            // syntax error
            else {
                if (!ok[0])
                    throw argErr(app, "LineBisector", arg[0]);
                else
                    throw argErr(app, "LineBisector", arg[1]);
            }

        default :
            throw argNumErr(app, "LineBisector", n);
    }
}
}

/*
 * AngularBisector[ <GeoPoint>, <GeoPoint>, <GeoPoint> ] AngularBisector[
 * <GeoLine>, <GeoLine> ]
 */
class CmdAngularBisector extends CommandProcessor {
	
	public CmdAngularBisector(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);

            // angular bisector of 2 lines
            if ((ok[0] = (arg[0] .isGeoLine()))
                && (ok[1] = (arg[1] .isGeoLine())))
				return kernel.AngularBisector(
                    c.getLabels(),
                    (GeoLine) arg[0],
                    (GeoLine) arg[1]);
			else {
                if (!ok[0])
                    throw argErr(app, "AngularBisector", arg[0]);
                else
                    throw argErr(app, "AngularBisector", arg[1]);
            }

        case 3 :
            arg = resArgs(c);

            // angular bisector of three points
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoPoint()))
                && (ok[2] = (arg[2] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.AngularBisector(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoPoint) arg[1],
                            (GeoPoint) arg[2])};
                return ret;
            }

            // syntax error
            else {
                if (!ok[0])
                    throw argErr(app, "AngularBisector", arg[0]);
                else if (!ok[1])
                    throw argErr(app, "AngularBisector", arg[1]);
                else
                    throw argErr(app, "AngularBisector", arg[2]);
            }

        default :
            throw argNumErr(app, "AngularBisector", n);
    }
}
}

/*
 * Vector[ <GeoPoint>, <GeoPoint> ] Vector[ <GeoPoint> ]
 */
class CmdVector extends CommandProcessor {
	
	public CmdVector(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);
            if (ok[0] = (arg[0] .isGeoPoint())) {
                GeoElement[] ret =
                    { kernel.Vector(c.getLabel(), (GeoPoint) arg[0])};
                return ret;
            }

            /*
			 * wrap a vector as a vector. needed for vectors that are defined
			 * through points: e.g. v = B - A used in AlgoLinPointVector
			 * 
			 * @see AlgoLinePointVector.getCmdXML()
			 */
            else if (ok[0] = (arg[0] .isGeoVector())) {
              	// maybe we have to set a label here
            	if (!cons.isSuppressLabelsActive() && !arg[0].isLabelSet()) {            	           
            		arg[0].setLabel(c.getLabel());
            		
            		// make sure that arg[0] is in construction list
            		if (arg[0].isIndependent())
            			cons.addToConstructionList(arg[0], true);
            		else 
            			cons.addToConstructionList(arg[0].getParentAlgorithm(), true);
            	}       
                GeoElement[] ret = { arg[0] };
                return ret;
            } else
				throw argErr(app, "Vector", arg[0]);

        case 2 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.Vector(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoPoint) arg[1])};
                return ret;
            } else {
                if (!ok[0])
                    throw argErr(app, "Vector", arg[0]);
                else
                    throw argErr(app, "Vector", arg[1]);
            }

        default :
            throw argNumErr(app, "Vector", n);
    }
}
}

/*
 * Direction[ <GeoLine> ]
 */
class CmdDirection extends CommandProcessor {
	
	public CmdDirection(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);
            if (ok[0] = (arg[0] .isGeoLine())) {
                GeoElement[] ret =
                    { kernel.Direction(c.getLabel(), (GeoLine) arg[0])};
                return ret;
            } else {
                if (!ok[0])
                    throw argErr(app, "Direction", arg[0]);
            }

        default :
            throw argNumErr(app, "Direction", n);
    }
}
}

/*
 * Slope[ <GeoLine> ] Slope[ <GeoFunction> ]
 */
class CmdSlope extends CommandProcessor {
	
	public CmdSlope(Kernel kernel) {
		super(kernel);
	}
	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);
            if (arg[0] .isGeoLine()) {
                GeoElement[] ret =
                    { kernel.Slope(c.getLabel(), (GeoLine) arg[0])};
                return ret;
            } else if (arg[0].isGeoFunctionable()) {
                GeoElement[] ret =
                    { kernel.Slope(c.getLabel(), ((GeoFunctionable) arg[0]).getGeoFunction())};
                return ret;
            } else
				throw argErr(app, "Slope", arg[0]);

        default :
            throw argNumErr(app, "Slope", n);
    }
}
}

/*
 * OrthogonalVector[ <GeoLine> ] OrthogonalVector[ <GeoVector> ]
 */
class CmdOrthogonalVector extends CommandProcessor {
	
	public CmdOrthogonalVector(Kernel kernel) {
		super(kernel);
	}
	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);
            if (ok[0] = (arg[0] .isGeoLine())) {
                GeoElement[] ret =
                    {
                         kernel.OrthogonalVector(
                            c.getLabel(),
                            (GeoLine) arg[0])};
                return ret;
            } else if (ok[0] = (arg[0] .isGeoVector())) {
                GeoElement[] ret =
                    {
                         kernel.OrthogonalVector(
                            c.getLabel(),
                            (GeoVector) arg[0])};
                return ret;
            } else {
                if (!ok[0])
                    throw argErr(app, "OrthogonalVector", arg[0]);
            }

        default :
            throw argNumErr(app, "OrthogonalVector", n);
    }
}
}

/*
 * UnitVector[ <GeoLine> ] UnitVector[ <GeoVector> ]
 */
class CmdUnitVector extends CommandProcessor {
	
	public CmdUnitVector(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);
            if (ok[0] = (arg[0] .isGeoLine())) {
                GeoElement[] ret =
                    { kernel.UnitVector(c.getLabel(), (GeoLine) arg[0])};
                return ret;
            } else if (ok[0] = (arg[0] .isGeoVector())) {
                GeoElement[] ret =
                    { kernel.UnitVector(c.getLabel(), (GeoVector) arg[0])};
                return ret;
            } else {
                if (!ok[0])
                    throw argErr(app, "UnitVector", arg[0]);
            }

        default :
            throw argNumErr(app, "UnitVector", n);
    }
}
}

/*
 * UnitOrthogonalVector[ <GeoLine> ] UnitOrthogonalVector[ <GeoVector> ]
 */
class CmdUnitOrthogonalVector extends CommandProcessor {
	
	public CmdUnitOrthogonalVector(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);
            if (ok[0] = (arg[0] .isGeoLine())) {
                GeoElement[] ret =
                    {
                         kernel.UnitOrthogonalVector(
                            c.getLabel(),
                            (GeoLine) arg[0])};
                return ret;
            } else if (ok[0] = (arg[0] .isGeoVector())) {
                GeoElement[] ret =
                    {
                         kernel.UnitOrthogonalVector(
                            c.getLabel(),
                            (GeoVector) arg[0])};
                return ret;
            } else {
                if (!ok[0])
                    throw argErr(
                        app,
                        "UnitOrthogonalVector",
                        arg[0]);
            }

        default :
            throw argNumErr(app, "UnitOrthogonalVector", n);
    }
}
}

/*
 * Length[ <GeoVector> ] 
 * Length[ <GeoPoint> ]
 * Length[ <GeoList> ] 
 * Victor Franco 18-04-2007: add Length[ <Function>, <Number>, <Number> ]
 * 							 add Length[ <Function>, <Point>, <Point> ]
 * 							 add Length[ <Curve>, <Number>, <Number> ]
 *                           add Length[ <Curve>, <Point>, <Point> ]
 */
class CmdLength extends CommandProcessor {
	
	public CmdLength(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);
            if (arg[0].isGeoVector() || arg[0].isGeoPoint()) {
                GeoElement[] ret =
                    { kernel.Length(c.getLabel(), (GeoVec3D) arg[0])};
                return ret;
            } 
            else if (arg[0].isGeoList()) {
            	GeoElement[] ret =
                	{ kernel.Length(c.getLabel(), (GeoList) arg[0])};
            	return ret;
            }
            else {
                throw argErr(app, c.getName(), arg[0]);
            }
            
//          Victor Franco 18-04-2007
        case 3 :
            arg = resArgs(c);
            if ( (ok[0] = (arg[0].isGeoFunctionable()) )
            	&& (ok[1] = (arg[1].isGeoNumeric()) )
                && (ok[2] = (arg[2].isGeoNumeric()) ) ){
                GeoElement[] ret =
                    {
                         kernel.FunctionLength(
                            c.getLabel(),
                            (GeoFunction) arg[0],
                            (GeoNumeric) arg[1],
                            (GeoNumeric) arg[2])};
                return ret;
            }

            else if( (ok[0] = (arg[0].isGeoFunctionable()) )
                	&& (ok[1] = (arg[1].isGeoPoint()) )
                    && (ok[2] = (arg[2].isGeoPoint()) ) ){

                    GeoElement[] ret =
                        {
                             kernel.FunctionLength2Points(
                                c.getLabel(),
                                (GeoFunction) arg[0],
                                (GeoPoint) arg[1],
                                (GeoPoint) arg[2])};
                    return ret;
            }

            else if ( (ok[0] = (arg[0].isGeoCurveCartesian()) )
            	&& (ok[1] = (arg[1].isGeoNumeric()) )
                && (ok[2] = (arg[2].isGeoNumeric()) ) ){

                GeoElement[] ret =
                    {
                         kernel.CurveLength(
                            c.getLabel(),
                            (GeoCurveCartesian) arg[0],
                            (GeoNumeric) arg[1],
                            (GeoNumeric) arg[2])};
                return ret;

            }

            else if ( (ok[0] = (arg[0].isGeoCurveCartesian()) )
                	&& (ok[1] = (arg[1].isGeoPoint()) )
                    && (ok[2] = (arg[2].isGeoPoint()) ) ){

                    GeoElement[] ret =
                        {
                             kernel.CurveLength2Points(
                                c.getLabel(),
                                (GeoCurveCartesian) arg[0],
                                (GeoPoint) arg[1],
                                (GeoPoint) arg[2])};
                    return ret;
            }

            else {

                if (!ok[0])
                    throw argErr(app, c.getName(), arg[0]);
                if (!ok[1])
                    throw argErr(app, c.getName(), arg[1]);
                else
                    throw argErr(app, c.getName(), arg[2]);
            }

         //Victor Franco 18-04-2007 (end)
        default :
            throw argNumErr(app, "Length", n);
    }
}
}

/*
 * Distance[ <GeoPoint>, <GeoPoint> ] Distance[ <GeoPoint>, <GeoLine> ]
 * Distance[ <GeoLine>, <GeoPoint> ] Distance[ <GeoLine>, <GeoLine> ]
 */
class CmdDistance extends CommandProcessor {
	
	public CmdDistance(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);

            // distance between two points
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.Distance(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoPoint) arg[1])};
                return ret;
            }

            // distance between point and line
            else if (
                (ok[0] = (arg[0] .isGeoPoint()))
                    && (ok[1] = (arg[1] .isGeoLine()))) {
                GeoElement[] ret =
                    {
                         kernel.Distance(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoLine) arg[1])};
                return ret;
            }

            // distance between line and point
            else if (
                (ok[0] = (arg[0] .isGeoLine()))
                    && (ok[1] = (arg[1] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.Distance(
                            c.getLabel(),
                            (GeoPoint) arg[1],
                            (GeoLine) arg[0])};
                return ret;
            }

            // distance between line and line
            else if (
                (ok[0] = (arg[0] .isGeoLine()))
                    && (ok[1] = (arg[1] .isGeoLine()))) {
                GeoElement[] ret =
                    {
                         kernel.Distance(
                            c.getLabel(),
                            (GeoLine) arg[0],
                            (GeoLine) arg[1])};
                return ret;
            }

            // syntax error
            else {
                if (ok[0] && !ok[1])
                    throw argErr(app, "Distance", arg[1]);
                else
                    throw argErr(app, "Distance", arg[0]);
            }

        default :
            throw argNumErr(app, "Distance", n);
    }
}
}

/*
 * Angle[ number ] Angle[ <GeoPolygon> ] Angle[ <GeoConic> ] Angle[ <GeoVector> ]
 * Angle[ <GeoPoint> ] Angle[ <GeoVector>, <GeoVector> ] Angle[ <GeoLine>,
 * <GeoLine> ] Angle[ <GeoPoint>, <GeoPoint>, <GeoPoint> ] Angle[ <GeoPoint>,
 * <GeoPoint>, <Number> ]
 */
class CmdAngle extends CommandProcessor {
	
	public CmdAngle(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        /*
		 * // Anlge[ constant number ] // get number value ExpressionNode en =
		 * null; ExpressionValue eval; double value = 0.0; // check if we got
		 * number: // ExpressionNode && NumberValue || Assignment // build
		 * ExpressionNode from one of these cases ok[0] = false; Object ob =
		 * c.getArgument(0); if (ob instanceof ExpressionNode) { en =
		 * (ExpressionNode) ob; eval = en.evaluate(); if (eval .isNumberValue() &&
		 * !(eval .isGeoPolygon())) { value = ((NumberValue) eval).getDouble();
		 * ok[0] = true; } } else if (ob instanceof Assignment) { GeoElement geo =
		 * cons.lookupLabel(((Assignment) ob).getVariable()); if (geo
		 * .isGeoNumeric()) { // wrap GeoNumeric int ExpressionNode for //
		 * kernel.DependentNumer() en = new ExpressionNode(kernel, (NumberValue)
		 * geo, ExpressionNode.NO_OPERATION, null); ok[0] = true; } }
		 */
        case 1 :
            arg = resArgs(c);

            // wrap angle as angle (needed to avoid ambiguities between numbers
            // and angles in XML)
            if (arg[0] .isGeoAngle()) {
            	// maybe we have to set a label here
            	if (!cons.isSuppressLabelsActive() && !arg[0].isLabelSet()) {
            		arg[0].setLabel(c.getLabel());
            		
            		// make sure that arg[0] is in construction list
            		if (arg[0].isIndependent())
            			cons.addToConstructionList(arg[0], true);
            		else 
            			cons.addToConstructionList(arg[0].getParentAlgorithm(), true);              			          		
            	}                	
                GeoElement[] ret = { arg[0] };
                return ret;
            }
            //  angle from number
            else if (arg[0] .isGeoNumeric()) {
                GeoElement[] ret =
                    { kernel.Angle(c.getLabel(), (GeoNumeric) arg[0])};
                return ret;
            }
            // angle from number
            else if (arg[0] .isGeoPoint() || 
            		 arg[0] .isGeoVector()) {
                GeoElement[] ret =
                    { kernel.Angle(c.getLabel(), (GeoVec3D) arg[0])};
                return ret;
            }
            // angle of conic or polygon
            else {
                if (arg[0] .isGeoConic()) {
                    GeoElement[] ret =
                        { kernel.Angle(c.getLabel(), (GeoConic) arg[0])};
                    return ret;
                } else if (arg[0] .isGeoPolygon())
					return kernel.Angles(
                        c.getLabels(),
                        (GeoPolygon) arg[0]);
            }

            throw argErr(app, "Angle", arg[0]);

        case 2 :
            arg = resArgs(c);

            // angle between vectors
            if ((ok[0] = (arg[0] .isGeoVector()))
                && (ok[1] = (arg[1] .isGeoVector()))) {
                GeoElement[] ret =
                    {
                         kernel.Angle(
                            c.getLabel(),
                            (GeoVector) arg[0],
                            (GeoVector) arg[1])};
                return ret;
            }
            // angle between lines
            else if (
                (ok[0] = (arg[0] .isGeoLine()))
                    && (ok[1] = (arg[1] .isGeoLine()))) {
                GeoElement[] ret =
                    {
                         kernel.Angle(
                            c.getLabel(),
                            (GeoLine) arg[0],
                            (GeoLine) arg[1])};
                return ret;
            }
            // syntax error
            else {
                if (ok[0] && !ok[1])
                    throw argErr(app, "Angle", arg[1]);
                else
                    throw argErr(app, "Angle", arg[0]);
            }

        case 3 :
            arg = resArgs(c);

            // angle between three points
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoPoint()))
                && (ok[2] = (arg[2] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.Angle(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoPoint) arg[1],
                            (GeoPoint) arg[2])};
                return ret;
            } 
            // fixed angle
            else if ((ok[0] = (arg[0] .isGeoPoint()))
                    && (ok[1] = (arg[1] .isGeoPoint()))
                    && (ok[2] = (arg[2] .isNumberValue())))
				return
				         kernel.Angle(
				            c.getLabels(),
				            (GeoPoint) arg[0],
				            (GeoPoint) arg[1],
				            (NumberValue) arg[2]);
			else
            	throw argErr(app, "Angle", arg[0]);

        default :
            throw argNumErr(app, "Angle", n);
    }
}
}

/*
 * Area[ <GeoPoint>, ..., <GeoPoint> ]
 * Area[ <GeoConic> ]
 * Area[ <Polygon> ] (returns Polygon directly)
 */
class CmdArea extends CommandProcessor {
	
	public CmdArea(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    GeoElement[] arg;

    if (n == 1) {
    	arg = resArgs(c);
    	
    	// area of conic
    	if (arg[0].isGeoConic()) {
			GeoElement[] ret = { kernel.Area(c.getLabel(), (GeoConic) arg[0]) };
			return ret;			
		} 
    	// area of polygon = polygon variable
    	else if (arg[0].isGeoPolygon()) {
			GeoElement[] ret = { arg[0] };
			return ret;			
		} 
    	else
			throw argErr(app, c.getName(), arg[0]);
    }
    
    // area of points
    else if (n > 2) {
        arg = resArgs(c);
        GeoPoint[] points = new GeoPoint[n];
        // check arguments
        for (int i = 0; i < n; i++) {
            if (!(arg[i] .isGeoPoint()))
				throw argErr(app, "Area", arg[i]);
			else {
                points[i] = (GeoPoint) arg[i];
            }
        }
        // everything ok
        GeoElement[] ret = { kernel.Area(c.getLabel(), points)};
        return ret;
    } else
		throw argNumErr(app, "Area", n);
}
}

/*
 * Focus[ <GeoConic> ]
 */
class CmdFocus extends CommandProcessor {
	
	public CmdFocus(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);
            if (ok[0] = (arg[0] .isGeoConic()))
				return kernel.Focus(c.getLabels(), (GeoConic) arg[0]);
			else
				throw argErr(app, "Focus", arg[0]);

        default :
            throw argNumErr(app, "Focus", n);
    }
}
}

/*
 * Vertex[ <GeoConic> ]
 */
class CmdVertex extends CommandProcessor {
	
	public CmdVertex(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
    	// Vertex[ <GeoConic> ]
    case 1 :
        arg = resArgs(c);
        if (ok[0] = (arg[0] .isGeoConic()))
			return kernel.Vertex(c.getLabels(), (GeoConic) arg[0]);
		else if (ok[0] = (arg[0] .isNumberValue()))
		{
            GeoElement[] ret =
            { kernel.CornerOfDrawingPad(c.getLabel(), (NumberValue) arg[0]) };
            return ret;
		}
		else
			throw argErr(app, c.getName(), arg[0]);

        // Corner[ <Image>, <number> ]
        case 2 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0].isGeoImage())) &&
            	(ok[1] = (arg[1].isNumberValue()))) {
                GeoElement[] ret =
                    { kernel.Corner(c.getLabel(), (GeoImage) arg[0], (NumberValue) arg[1])};
                return ret;
            }
            // Michael Borcherds 2007-11-26 BEGIN Corner[] for textboxes
            // Corner[ <Text>, <number> ]
            else if ((ok[0] = (arg[0].isGeoText())) &&
                    	(ok[1] = (arg[1].isNumberValue())))
                {
                        GeoElement[] ret =
                        { kernel.Corner(c.getLabel(), (GeoText) arg[0], (NumberValue) arg[1])};
                    return ret;
                    // Michael Borcherds 2007-11-26 END
            } else {
            	if (!ok[0])
            		throw argErr(app, c.getName(), arg[0]);
            	else
            		throw argErr(app, c.getName(), arg[1]);
            }
            
        default :
            throw argNumErr(app, c.getName(), n);
    }
}
}

/*
 * Corner[ <Image>, <number> ]
 */
class CmdCorner extends CmdVertex {
	
	public CmdCorner (Kernel kernel) {
		super(kernel);
	}

}

/*
 * Circle[ <GeoPoint>, <GeoNumeric> ] Circle[ <GeoPoint>, <GeoPoint> ] Circle[
 * <GeoPoint>, <GeoPoint>, <GeoPoint> ]
 */
class CmdCircle extends CommandProcessor {
	
	public CmdCircle(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.Circle(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (NumberValue) arg[1])};
                return ret;
            } else if (
                (ok[0] = (arg[0] .isGeoPoint()))
                    && (ok[1] = (arg[1] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.Circle(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoPoint) arg[1])};
                return ret;
            } else {
                if (!ok[0])
                    throw argErr(app, "Circle", arg[0]);
                else
                    throw argErr(app, "Circle", arg[1]);
            }

        case 3 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoPoint()))
                && (ok[2] = (arg[2] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.Circle(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoPoint) arg[1],
                            (GeoPoint) arg[2])};
                return ret;
            } else {
                if (!ok[0])
                    throw argErr(app, "Circle", arg[0]);
                else if (!ok[1])
                    throw argErr(app, "Circle", arg[1]);
                else
                    throw argErr(app, "Circle", arg[2]);
            }

        default :
            throw argNumErr(app, "Circle", n);
    }
}
}

/*
 * Semicircle[ <GeoPoint>, <GeoPoint> ]
 */
class CmdSemicircle extends CommandProcessor {
	
	public CmdSemicircle(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoPoint()))
                    && (ok[1] = (arg[1] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.Semicircle(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoPoint) arg[1])};
                return ret;
            } else {
                if (!ok[0])
                    throw argErr(app, "Semicircle", arg[0]);
                else
                    throw argErr(app, "Semicircle", arg[1]);
            }

        default :
            throw argNumErr(app, "Semicircle", n);
    }
}    
}

/*
 * Locus[ <GeoPoint Q>, <GeoPoint P> ]
 */
class CmdLocus extends CommandProcessor {
	
	public CmdLocus(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
        	// second argument has to be point on path
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoPoint()))
             && (ok[1] = (arg[1] .isGeoPoint()))) {
                GeoElement[] ret =
                    {kernel.Locus(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoPoint) arg[1])};
                return ret;
            } else {
                if (!ok[0])
                    throw argErr(app, "Locus", arg[0]);
                else
                    throw argErr(app, "Locus", arg[1]);
            }

        default :
            throw argNumErr(app, "Locus", n);
    }
}   
}

/*
 * Arc[ <GeoConic>, <Number>, <Number> ] Arc[ <GeoConic>, <GeoPoint>, <GeoPoint> ]
 */
class CmdArc extends CommandProcessor {
	
	public CmdArc(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 3 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoConic()))
                    && (ok[1] = (arg[1] .isNumberValue()))
                    && (ok[2] = (arg[2] .isNumberValue()))) {
                    GeoElement[] ret =
                        {
                             kernel.ConicArc(
                                c.getLabel(),
                                (GeoConic) arg[0],
                                (NumberValue) arg[1],
                                (NumberValue) arg[2])};
                    return ret;     
            }      
            else if ((ok[0] = (arg[0] .isGeoConic()))
                    && (ok[1] = (arg[1] .isGeoPoint()))
                    && (ok[2] = (arg[2] .isGeoPoint()))) {
                    GeoElement[] ret =
                        {
                             kernel.ConicArc(
                                c.getLabel(),
                                (GeoConic) arg[0],
                                (GeoPoint) arg[1],
                                (GeoPoint) arg[2])};
                    return ret;     
            }  
            else {
                if (!ok[0])
                    throw argErr(app, "Arc", arg[0]);
                else if (!ok[1])
                    throw argErr(app, "Arc", arg[1]);
                else
                    throw argErr(app, "Arc", arg[2]);
            }


        default :
            throw argNumErr(app, "Arc", n);
    }
}   
}

/*
 * Sector[ <GeoConic>, <Number>, <Number> ] Sector[ <GeoConic>, <GeoPoint>,
 * <GeoPoint> ]
 */
class CmdSector extends CommandProcessor {
	
	public CmdSector(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 3 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoConic()))
                    && (ok[1] = (arg[1] .isNumberValue()))
                    && (ok[2] = (arg[2] .isNumberValue()))) {
                    GeoElement[] ret =
                        {
                             kernel.ConicSector(
                                c.getLabel(),
                                (GeoConic) arg[0],
                                (NumberValue) arg[1],
                                (NumberValue) arg[2])};
                    return ret;     
            } 
            else if ((ok[0] = (arg[0] .isGeoConic()))
                    && (ok[1] = (arg[1] .isGeoPoint()))
                    && (ok[2] = (arg[2] .isGeoPoint()))) {
                    GeoElement[] ret =
                        {
                             kernel.ConicSector(
                                c.getLabel(),
                                (GeoConic) arg[0],
                                (GeoPoint) arg[1],
                                (GeoPoint) arg[2])};
                    return ret;     
            } 
            else {
                if (!ok[0])
                    throw argErr(app, "Sector", arg[0]);
                else if (!ok[1])
                    throw argErr(app, "Sector", arg[1]);
                else
                    throw argErr(app, "Sector", arg[2]);
            }


        default :
            throw argNumErr(app, "Sector", n);
    }
} 
}

/*
 * CircleArc[ <GeoPoint center>, <GeoPoint>, <GeoPoint> ]
 */
class CmdCircleArc extends CommandProcessor {
	
	public CmdCircleArc(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 3 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoPoint()))
                && (ok[2] = (arg[2] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.CircleArc(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoPoint) arg[1],
                            (GeoPoint) arg[2])};
                return ret;
            }                
            else {
                if (!ok[0])
                    throw argErr(app, c.getName(), arg[0]);
                else if (!ok[1])
                    throw argErr(app, c.getName(), arg[1]);
                else
                    throw argErr(app, c.getName(), arg[2]);
            }

        default :
            throw argNumErr(app, c.getName(), n);
    }
}   
}

/*
 * CircleSector[ <GeoPoint center>, <GeoPoint>, <GeoPoint> ]
 */
class CmdCircleSector extends CommandProcessor {
	
	public CmdCircleSector(Kernel kernel) {
		super(kernel);
	}
	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 3 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoPoint()))
                && (ok[2] = (arg[2] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.CircleSector(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoPoint) arg[1],
                            (GeoPoint) arg[2])};
                return ret;
            }                
            else {
                if (!ok[0])
                    throw argErr(app, c.getName(), arg[0]);
                else if (!ok[1])
                    throw argErr(app, c.getName(), arg[1]);
                else
                    throw argErr(app, c.getName(), arg[2]);
            }

        default :
            throw argNumErr(app, c.getName(), n);
    }
}  
}

/*
 * CircumcircleArc[ <GeoPoint center>, <GeoPoint>, <GeoPoint> ]
 */
class CmdCircumcircleArc extends CommandProcessor {
	
	public CmdCircumcircleArc(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 3 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoPoint()))
                && (ok[2] = (arg[2] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.CircumcircleArc(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoPoint) arg[1],
                            (GeoPoint) arg[2])};
                return ret;
            }                
            else {
                if (!ok[0])
                    throw argErr(app, c.getName(), arg[0]);
                else if (!ok[1])
                    throw argErr(app, c.getName(), arg[1]);
                else
                    throw argErr(app, c.getName(), arg[2]);
            }

        default :
            throw argNumErr(app, c.getName(), n);
    }
}   
}

/*
 * CircumcircleSector[ <GeoPoint center>, <GeoPoint>, <GeoPoint> ]
 */
class CmdCircumcircleSector extends CommandProcessor {
	
	public CmdCircumcircleSector(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 3 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoPoint()))
                && (ok[2] = (arg[2] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.CircumcircleSector(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoPoint) arg[1],
                            (GeoPoint) arg[2])};
                return ret;
            }                
            else {
                if (!ok[0])
                    throw argErr(app, c.getName(), arg[0]);
                else if (!ok[1])
                    throw argErr(app, c.getName(), arg[1]);
                else
                    throw argErr(app, c.getName(), arg[2]);
            }

        default :
            throw argNumErr(app, c.getName(), n);
    }
}
}

/*
 * Parabola[ <GeoPoint>, <GeoLine> ]
 */
class CmdParabola extends CommandProcessor {
	
	public CmdParabola(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoLine()))) {
                GeoElement[] ret =
                    {
                         kernel.Parabola(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoLine) arg[1])};
                return ret;
            } else {
                if (!ok[0])
                    throw argErr(app, "Parabola", arg[0]);
                else
                    throw argErr(app, "Parabola", arg[1]);
            }

        default :
            throw argNumErr(app, "Parabola", n);
    }
}
}

/*
 * Ellipse[ <GeoPoint>, <GeoPoint>, <NumberValue> ]
 */
class CmdEllipse extends CommandProcessor {
	
	public CmdEllipse(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 3 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoPoint()))
                    && (ok[1] = (arg[1] .isGeoPoint()))
                    && (ok[2] = (arg[2] .isNumberValue()))) {
                    GeoElement[] ret =
                        {
                             kernel.Ellipse(
                                c.getLabel(),
                                (GeoPoint) arg[0],
                                (GeoPoint) arg[1],
                                (NumberValue) arg[2])};
                    return ret;
                } else if
                    ((ok[0] = (arg[0] .isGeoPoint()))
                            && (ok[1] = (arg[1] .isGeoPoint()))
                            && (ok[2] = (arg[2] .isGeoPoint()))) {
                            GeoElement[] ret =
                                {
                                     kernel.Ellipse(
                                        c.getLabel(),
                                        (GeoPoint) arg[0],
                                        (GeoPoint) arg[1],
                                        (GeoPoint) arg[2])};
                            return ret;
                        } else {
                if (!ok[0])
                    throw argErr(app, "Ellipse", arg[0]);
                else
                    throw argErr(app, "Ellipse", arg[1]);
            }

        default :
            throw argNumErr(app, "Ellipse", n);
    }
}
}

/*
 * Hyperbola[ <GeoPoint>, <GeoPoint>, <NumberValue> ]
 */
class CmdHyperbola extends CommandProcessor {
	
	public CmdHyperbola(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 3 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoPoint()))
                    && (ok[1] = (arg[1] .isGeoPoint()))
                    && (ok[2] = (arg[2] .isNumberValue()))) {
                    GeoElement[] ret =
                        {
                             kernel.Hyperbola(
                                c.getLabel(),
                                (GeoPoint) arg[0],
                                (GeoPoint) arg[1],
                                (NumberValue) arg[2])};
                    return ret;
                } else if ((ok[0] = (arg[0] .isGeoPoint()))
                            && (ok[1] = (arg[1] .isGeoPoint()))
                            && (ok[2] = (arg[2] .isGeoPoint()))) {
                            GeoElement[] ret =
                                {
                                     kernel.Hyperbola(
                                        c.getLabel(),
                                        (GeoPoint) arg[0],
                                        (GeoPoint) arg[1],
                                        (GeoPoint) arg[2])};
                            return ret;
                        } else {
                if (!ok[0])
                    throw argErr(app, "Hyperbola", arg[0]);
                else
                    throw argErr(app, "Hyperbola", arg[1]);
            }

        default :
            throw argNumErr(app, "Hyperbola", n);
    }
}
}

/*
 * Conic[ five GeoPoints ]
 */
class CmdConic extends CommandProcessor {
	
	public CmdConic(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 5 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoPoint()))
                && (ok[2] = (arg[2] .isGeoPoint()))
                && (ok[3] = (arg[3] .isGeoPoint()))
                && (ok[4] = (arg[4] .isGeoPoint()))) {
                GeoPoint[] points =
                    {
                        (GeoPoint) arg[0],
                        (GeoPoint) arg[1],
                        (GeoPoint) arg[2],
                        (GeoPoint) arg[3],
                        (GeoPoint) arg[4] };
                GeoElement[] ret = { kernel.Conic(c.getLabel(), points)};
                return ret;
            } else {
                if (!ok[0])
                    throw argErr(app, "Conic", arg[0]);
                else
                    throw argErr(app, "Conic", arg[1]);
            }

        default :
            throw argNumErr(app, "Conic", n);
    }
}
}

/*
 * Intersect[ <GeoLine>, <GeoLine> ] Intersect[ <GeoLine>, <GeoConic> ]
 * Intersect[ <GeoConic>, <GeoLine> ] Intersect[ <GeoConic>, <GeoConic> ]
 * Intersect[ <GeoFunction>, <GeoFunction> ] Intersect[ <GeoFunction>, <GeoLine> ]
 */
class CmdIntersect extends CommandProcessor {
	
	public CmdIntersect(Kernel kernel) {
		super(kernel);
	}
	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);
            // Line - Line
            if ((ok[0] = (arg[0] .isGeoLine()))
                && (ok[1] = (arg[1] .isGeoLine()))) {
                GeoElement[] ret =
                    {
                         kernel.IntersectLines(
                            c.getLabel(),
                            (GeoLine) arg[0],
                            (GeoLine) arg[1])};
                return ret;
            }
            // Line - Conic
            else if (
                (ok[0] = (arg[0] .isGeoLine()))
                    && (ok[1] = (arg[1] .isGeoConic())))
				return kernel.IntersectLineConic(
                    c.getLabels(),
                    (GeoLine) arg[0],
                    (GeoConic) arg[1]);
			else if (
                (ok[0] = (arg[0] .isGeoConic()))
                    && (ok[1] = (arg[1] .isGeoLine())))
				return kernel.IntersectLineConic(
                    c.getLabels(),
                    (GeoLine) arg[1],
                    (GeoConic) arg[0]);
			else if (
                (ok[0] = (arg[0] .isGeoConic()))
                    && (ok[1] = (arg[1] .isGeoConic())))
				return kernel.IntersectConics(
                    c.getLabels(),
                    (GeoConic) arg[0],
                    (GeoConic) arg[1]);
			else if (
                (ok[0] = (arg[0].isGeoFunctionable()))
                    && (ok[1] = (arg[1].isGeoFunctionable())))
				return kernel.IntersectPolynomials(
                    c.getLabels(),
                    ((GeoFunctionable) arg[0]).getGeoFunction(),
                    ((GeoFunctionable) arg[1]).getGeoFunction());
			else if (
                (ok[0] = (arg[0] .isGeoFunctionable()))
                    && (ok[1] = (arg[1] .isGeoLine())))
				return kernel.IntersectPolynomialLine(
                    c.getLabels(),
                    ((GeoFunctionable) arg[0]).getGeoFunction(),
                    (GeoLine) arg[1]);
			else if (
                (ok[0] = (arg[0] .isGeoLine()))
                    && (ok[1] = (arg[1] .isGeoFunctionable())))
				return kernel.IntersectPolynomialLine(
                    c.getLabels(),
                    ((GeoFunctionable) arg[1]).getGeoFunction(),
                    (GeoLine) arg[0]);
			else {
                if (!ok[0])
                    throw argErr(app, "Intersect", arg[0]);
                else
                    throw argErr(app, "Intersect", arg[1]);
            }

        case 3 : // only one of the intersection points: the third argument
					 // states wich one
            arg = resArgs(c);
            // Line - Conic
            if ((ok[0] = (arg[0] .isGeoLine()))
                && (ok[1] = (arg[1] .isGeoConic()))
                && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.IntersectLineConicSingle(
                            c.getLabel(),
                            (GeoLine) arg[0],
                            (GeoConic) arg[1],
                            (NumberValue) arg[2])};
                return ret;
            }
            // Conic - Line
            else if (
                (ok[0] = (arg[0] .isGeoConic()))
                    && (ok[1] = (arg[1] .isGeoLine()))
                    && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.IntersectLineConicSingle(
                            c.getLabel(),
                            (GeoLine) arg[1],
                            (GeoConic) arg[0],
                            (NumberValue) arg[2])};
                return ret;
            }
            // Conic - Conic
            else if (
                (ok[0] = (arg[0] .isGeoConic()))
                    && (ok[1] = (arg[1] .isGeoConic()))
                    && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.IntersectConicsSingle(
                            c.getLabel(),
                            (GeoConic) arg[0],
                            (GeoConic) arg[1],
                            (NumberValue) arg[2])};
                return ret;
            }
            // Polynomial - Polynomial with index of point
            else if (
                (ok[0] = (arg[0] .isGeoFunctionable()))
                    && (ok[1] = (arg[1] .isGeoFunctionable()))
                    && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.IntersectPolynomialsSingle(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            ((GeoFunctionable) arg[1]).getGeoFunction(),
                            (NumberValue) arg[2])};
                return ret;
            }
            // Polynomial - Line with index of point
            else if (
                (ok[0] = (arg[0] .isGeoFunctionable()))
                    && (ok[1] = (arg[1] .isGeoLine()))
                    && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.IntersectPolynomialLineSingle(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            (GeoLine) arg[1],
                            (NumberValue) arg[2])};
                return ret;
            }
            // Line - Polynomial with index of point
            else if (
                (ok[0] = (arg[0] .isGeoLine()))
                    && (ok[1] = (arg[1] .isGeoFunctionable()))
                    && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.IntersectPolynomialLineSingle(
                            c.getLabel(),
                            ((GeoFunctionable) arg[1]).getGeoFunction(),
                            (GeoLine) arg[0],
                            (NumberValue) arg[2])};
                return ret;
            }
            // Function - Function with startPoint
            else if (
                (ok[0] = (arg[0] .isGeoFunctionable()))
                    && (ok[1] = (arg[1] .isGeoFunctionable()))
                    && (ok[2] = (arg[2] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.IntersectFunctions(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            ((GeoFunctionable) arg[1]).getGeoFunction(),
                            (GeoPoint) arg[2])};
                return ret;
            }
            // Function - Line with startPoint
            else if (
                (ok[0] = (arg[0] .isGeoFunctionable()))
                    && (ok[1] = (arg[1] .isGeoLine()))
                    && (ok[2] = (arg[2] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.IntersectFunctionLine(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            (GeoLine) arg[1],
                            (GeoPoint) arg[2])};
                return ret;
            }
            // Line - Function with startPoint
            else if (
                (ok[0] = (arg[0] .isGeoLine()))
                    && (ok[1] = (arg[1] .isGeoFunctionable()))
                    && (ok[2] = (arg[2] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.IntersectFunctionLine(
                            c.getLabel(),
                            ((GeoFunctionable) arg[1]).getGeoFunction(),
                            (GeoLine) arg[0],
                            (GeoPoint) arg[2])};
                return ret;
            }
            // Syntax Error
            else {
                if (!ok[0])
                    throw argErr(app, "Intersect", arg[0]);
                else if (!ok[1])
                    throw argErr(app, "Intersect", arg[1]);
                else
                    throw argErr(app, "Intersect", arg[2]);
            }

        default :
            throw argNumErr(app, "Intersect", n);
    }
}
}

/*
 * Polar[ <GeoPoint>, <GeoConic> ]
 */
class CmdPolar extends CommandProcessor {
	
	public CmdPolar(Kernel kernel) {
		super(kernel);
	}
	
final public   GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);

            // polar line to point relative to conic
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoConic()))) {
                GeoElement[] ret =
                    {
                         kernel.PolarLine(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoConic) arg[1])};
                return ret;
            }

            // syntax error
            else {
                if (!ok[0])
                    throw argErr(app, "Polar", arg[0]);
                else
                    throw argErr(app, "Polar", arg[1]);
            }

        default :
            throw argNumErr(app, "Polar", n);
    }
}
}

/*
 * Diameter[ <GeoVector>, <GeoConic> ] Diameter[ <GeoLine>, <GeoConic> ]
 */
class CmdDiameter extends CommandProcessor {
	
	public CmdDiameter(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);

            // diameter line conjugate to vector relative to conic
            if ((ok[0] = (arg[0] .isGeoVector()))
                && (ok[1] = (arg[1] .isGeoConic()))) {
                GeoElement[] ret =
                    {
                         kernel.DiameterLine(
                            c.getLabel(),
                            (GeoVector) arg[0],
                            (GeoConic) arg[1])};
                return ret;
            }

            // diameter line conjugate to line relative to conic
            if ((ok[0] = (arg[0] .isGeoLine()))
                && (ok[1] = (arg[1] .isGeoConic()))) {
                GeoElement[] ret =
                    {
                         kernel.DiameterLine(
                            c.getLabel(),
                            (GeoLine) arg[0],
                            (GeoConic) arg[1])};
                return ret;
            }

            // syntax error
            else {
                if (!ok[0])
                    throw argErr(app, "Diameter", arg[0]);
                else
                    throw argErr(app, "Diameter", arg[1]);
            }

        default :
            throw argNumErr(app, "Diameter", n);
    }
}
}

/*
 * Tangent[ <GeoPoint>, <GeoConic> ] Tangent[ <GeoLine>, <GeoConic> ] Tangent[
 * <NumberValue>, <GeoFunction> ] Tangent[ <GeoPoint>, <GeoFunction> ]
 *  Victor Franco 11-02-2007 (for curve's): Tangent[ <GeoPoint>, <GeoCurveCartesian> ]
 */
class CmdTangent extends CommandProcessor {
	
	public CmdTangent(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);

            // tangents through point
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoConic())))
				return kernel.Tangent(
                    c.getLabels(),
                    (GeoPoint) arg[0],
                    (GeoConic) arg[1]);
			else if (
                (ok[0] = (arg[0] .isGeoLine()))
                    && (ok[1] = (arg[1] .isGeoConic())))
				return kernel.Tangent(
                    c.getLabels(),
                    (GeoLine) arg[0],
                    (GeoConic) arg[1]);
			else if (
                (ok[0] = (arg[0] .isNumberValue()))
                    && (ok[1] = (arg[1] .isGeoFunctionable()))) {
                GeoElement[] ret =
                    {
                         kernel.Tangent(
                            c.getLabel(),
                            (NumberValue) arg[0],
                            ((GeoFunctionable) arg[1]).getGeoFunction())};
                return ret;
            }

            // tangents of function at x = x(Point P)
            else if (
                (ok[0] = (arg[0] .isGeoPoint()))
                    && (ok[1] = (arg[1] .isGeoFunctionable()))) {
                GeoElement[] ret =
                    {
                         kernel.Tangent(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            ((GeoFunctionable) arg[1]).getGeoFunction())};
                return ret;
            }
            //Victor Franco 11-02-2007: for curve's
            else if( (ok[0] = (arg[0] .isGeoPoint()) )
                    && (ok[1] = (arg[1] .isGeoCurveCartesian())) ){

            	GeoElement[] ret =
                {
                     kernel.Tangent(
                        c.getLabel(),
                        (GeoPoint) arg[0],
                        (GeoCurveCartesian) arg[1])};
                return ret;
            }
            //Victor Franco 11-02-2007: end for curve's

            // syntax error
            else {
                if (!ok[0])
                    throw argErr(app, "Tangent", arg[0]);
                else
                    throw argErr(app, "Tangent", arg[1]);
            }

        default :
            throw argNumErr(app, "Tangent", n);
    }
}
}

/*
 * Asymptote[ <GeoConic> ]
 */
class CmdAsymptote extends CommandProcessor {
	
	public CmdAsymptote(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);

            // asymptotes to conic
            if (arg[0] .isGeoConic())
				return kernel.Asymptote(c.getLabels(), (GeoConic) arg[0]);
			else
				throw argErr(app, "Asymptote", arg[0]);

        default :
            throw argNumErr(app, "Asymptote", n);
    }
}
}

/*
 * Axes[ <GeoConic> ]
 */
class CmdAxes extends CommandProcessor {
	
	public CmdAxes(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);

            // asymptotes to conic
            if (arg[0] .isGeoConic())
				return kernel.Axes(c.getLabels(), (GeoConic) arg[0]);
			else
				throw argErr(app, "Axes", arg[0]);

        default :
            throw argNumErr(app, "Axes", n);
    }
}
}

/*
 * FirstAxis[ <GeoConic> ]
 */
class CmdFirstAxis extends CommandProcessor {
	
	public CmdFirstAxis(Kernel kernel) {
		super(kernel);
	}
	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);

            // asymptotes to conic
            if (arg[0] .isGeoConic()) {
                GeoElement[] ret =
                    { kernel.FirstAxis(c.getLabel(), (GeoConic) arg[0])};
                return ret;
            } else
				throw argErr(app, "FirstAxis", arg[0]);

        default :
            throw argNumErr(app, "FirstAxis", n);
    }
}
}

/*
 * SecondAxis[ <GeoConic> ]
 */
class CmdSecondAxis extends CommandProcessor {
	
	public CmdSecondAxis(Kernel kernel) {
		super(kernel);
	}
	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);

            // asymptotes to conic
            if (arg[0] .isGeoConic()) {
                GeoElement[] ret =
                    { kernel.SecondAxis(c.getLabel(), (GeoConic) arg[0])};
                return ret;
            } else
				throw argErr(app, "SecondAxis", arg[0]);

        default :
            throw argNumErr(app, "SecondAxis", n);
    }
}
}

/*
 * FirstAxisLength[ <GeoConic> ]
 */
class CmdFirstAxisLength extends CommandProcessor {
	
	public CmdFirstAxisLength(Kernel kernel) {
		super(kernel);
	}
	
final public   GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);

            // asymptotes to conic
            if (arg[0] .isGeoConic()) {
                GeoElement[] ret =
                    {
                         kernel.FirstAxisLength(
                            c.getLabel(),
                            (GeoConic) arg[0])};
                return ret;
            } else
				throw argErr(app, "FirstAxisLength", arg[0]);

        default :
            throw argNumErr(app, "FirstAxisLength", n);
    }
}
}

/*
 * SecondAxisLength[ <GeoConic> ]
 */
class CmdSecondAxisLength extends CommandProcessor {
	
	public CmdSecondAxisLength(Kernel kernel) {
		super(kernel);
	}
	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);

            // asymptotes to conic
            if (arg[0] .isGeoConic()) {
                GeoElement[] ret =
                    {
                         kernel.SecondAxisLength(
                            c.getLabel(),
                            (GeoConic) arg[0])};
                return ret;
            } else
				throw argErr(app, "SecondAxisLength", arg[0]);

        default :
            throw argNumErr(app, "SecondAxisLength", n);
    }
}
}

/*
 * Excentricity[ <GeoConic> ]
 */
class CmdExcentricity extends CommandProcessor {
	
	public CmdExcentricity(Kernel kernel) {
		super(kernel);
	}
	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);

            // asymptotes to conic
            if (arg[0] .isGeoConic()) {
                GeoElement[] ret =
                    { kernel.Excentricity(c.getLabel(), (GeoConic) arg[0])};
                return ret;
            } else
				throw argErr(app, "Excentricity", arg[0]);

        default :
            throw argNumErr(app, "Excentricity", n);
    }
}
}

/*
 * Parameter[ <GeoConic> ]
 */
class CmdParameter extends CommandProcessor {
	
	public CmdParameter(Kernel kernel) {
		super(kernel);
	}
	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);

            // asymptotes to conic
            if (arg[0] .isGeoConic()) {
                GeoElement[] ret =
                    { kernel.Parameter(c.getLabel(), (GeoConic) arg[0])};
                return ret;
            } else
				throw argErr(app, "Parameter", arg[0]);

        default :
            throw argNumErr(app, "Parameter", n);
    }
}
}

/*
 * Radius[ <GeoConic> ]
 */
class CmdRadius extends CommandProcessor {
	
	public CmdRadius(Kernel kernel) {
		super(kernel);
	}
	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);

            // asymptotes to conic
            if (arg[0] .isGeoConic()) {
                GeoElement[] ret =
                    { kernel.Radius(c.getLabel(), (GeoConic) arg[0])};
                return ret;
            } else
				throw argErr(app, "Radius", arg[0]);

        default :
            throw argNumErr(app, "Radius", n);
    }
}
}


 
/*
 * Directrix[ <GeoConic> ]
 */
class CmdDirectrix extends CommandProcessor {
	
	public CmdDirectrix(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);

            // asymptotes to conic
            if (arg[0] .isGeoConic()) {
                GeoElement[] ret =
                    { kernel.Directrix(c.getLabel(), (GeoConic) arg[0])};
                return ret;
            } else
				throw argErr(app, "Directrix", arg[0]);

        default :
            throw argNumErr(app, "Directrix", n);
    }
}
}

/*
 * Translate[ <GeoPoint>, <GeoVector> ] Translate[ <GeoLine>, <GeoVector> ]
 * Translate[ <GeoConic>, <GeoVector> ] Translate[ <GeoFunction>, <GeoVector> ]
 * Translate[ <GeoVector>, <GeoPoint> ] // set start point 
 * Translate[ <GeoPolygon>, <GeoVector> ]
 *  
 */
class CmdTranslate extends CommandProcessor {
	
	public CmdTranslate(Kernel kernel) {
		super(kernel);
	}
	
final public  GeoElement[] process(Command c)  throws MyError, CircularDefinitionException {
    String label = c.getLabel();
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;
    GeoElement[] ret = new GeoElement[1];

    switch (n) {
        case 2 :
            arg = resArgs(c);

            // translate object
            if ((ok[0] = (arg[0] instanceof Translateable))
                && (ok[1] = (arg[1] .isGeoVector()))) {
            	Translateable p = (Translateable) arg[0];
                GeoVector v = (GeoVector) arg[1];
                //GeoElement geo = p.toGeoElement();
               
                /*
                // if we are not in a nested command (suppress labels)
                // and no label is given
                // we change the input object
                if (!cons.isSuppressLabelsActive() &&  
                	label == null && geo.isIndependent()) 
                {
                        p.translate(v);                     
                        geo.updateRepaint();
                        ret[0] = geo;
                } else {
                    ret = kernel.Translate(label, p, v);                 
                }
                */
                
                ret = kernel.Translate(label, p, v); 
                return ret;
            }
            
            // translate polygon
            else  if ((ok[0] = (arg[0] .isGeoPolygon()))
            			&& (ok[1] = (arg[1] .isGeoVector())))
				return kernel.Translate(label, (GeoPolygon) arg[0], (GeoVector) arg[1]);
			else if (
                (ok[0] = (arg[0] .isGeoVector()))
                    && (ok[1] = (arg[1] .isGeoPoint()))) {
                GeoVector v = (GeoVector) arg[0];
                GeoPoint P = (GeoPoint) arg[1];
                if (label == null) {                    
                    v.setStartPoint(P);
                    v.updateRepaint();
                    ret[0] = v;                 
                } else {
                    ret[0] = kernel.Translate(label, v, P);
                }
                return ret;
            }

            // syntax error
            else {
                if (!ok[0])
                    throw argErr(app, "Translate", arg[0]);
                else
                    throw argErr(app, "Translate", arg[1]);
            }

        default :
            throw argNumErr(app, "Translate", n);
    }
}
}

/*
 * Rotate[ <GeoPoint>, <NumberValue> ] 
 * Rotate[ <GeoVector>, <NumberValue> ]
 * Rotate[ <GeoLine>, <NumberValue> ] 
 * Rotate[ <GeoConic>, <NumberValue> ]
 * Rotate[ <GeoPolygon>, <NumberValue> ]
 * 
 * Rotate[ <GeoPoint>, <NumberValue>, <GeoPoint> ] 
 * Rotate[ <GeoLine>, <NumberValue>, <GeoPoint> ] 
 * Rotate[ <GeoConic>, <NumberValue>, <GeoPoint> ]
 * Rotate[ <GeoPolygon>, <NumberValue>, <GeoPoint> ]
 */
class CmdRotate extends CommandProcessor {
	
	public CmdRotate(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    String label = c.getLabel();
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;
    GeoElement[] ret = new GeoElement[1];

    switch (n) {
        case 2 :
            // ROTATE AROUND CENTER (0,0)
            arg = resArgs(c);

            // rotate point, line or conic
            if ((ok[0] = (arg[0] instanceof Rotateable))
                && (ok[1] = (arg[1] .isNumberValue()))) {
            	Rotateable p = (Rotateable) arg[0];
                NumberValue phi = (NumberValue) arg[1];
                //GeoElement geo = p.toGeoElement();
                
                /*
                // if we are not in a nested command (suppress labels)
                // and no label is given and the input object is independent
                // we change the input object
                if (!cons.isSuppressLabelsActive() &&                		
                	label == null && geo.isIndependent()) 
                {                	
                        p.rotate(phi);
                        geo.updateRepaint();
                        ret[0] = geo;
                } 
                // otherwise we create a new object
                else {                	
                    ret = kernel.Rotate(label, p, phi);           
                }
                */
                
                ret = kernel.Rotate(label, p, phi);  
                return ret;
            }             

            // rotate polygon
            else  if ((ok[0] = (arg[0] .isGeoPolygon()))
            			&& (ok[1] = (arg[1] .isNumberValue())))
				return kernel.Rotate(label, (GeoPolygon) arg[0], (NumberValue) arg[1]);
			else {
                if (!ok[0])
                    throw argErr(app, c.getName(), arg[0]);
                else
                    throw argErr(app, c.getName(), arg[1]);
            }

        case 3 :
            // ROTATION AROUND POINT
            arg = resArgs(c);

            // rotate point, line or conic
            if ((ok[0] = (arg[0] instanceof PointRotateable))
                && (ok[1] = (arg[1] .isNumberValue()))
                && (ok[2] = (arg[2] .isGeoPoint()))) {
            	PointRotateable p = (PointRotateable) arg[0];
                NumberValue phi = (NumberValue) arg[1];
                GeoPoint Q = (GeoPoint) arg[2];
                //GeoElement geo = p.toGeoElement();
                
                /*
                // if we are not in a nested command (suppress labels)
                // and no label is given and the input object is independent
                // we change the input object
                if (!cons.isSuppressLabelsActive() &&  
                	label == null && geo.isIndependent()) 
                {
                        p.rotate(phi, Q);
                        geo.updateRepaint();
                        ret[0] = geo;
                } else {
                    ret = kernel.Rotate(label, p, phi, Q);
                }
                */
                
                ret = kernel.Rotate(label, p, phi, Q);
                return ret;
            }
            
            // rotate polygon
            else  if ((ok[0] = (arg[0] .isGeoPolygon()))
            			&& (ok[1] = (arg[1] .isNumberValue()))
						&& (ok[2] = (arg[2] .isGeoPoint())))
				return kernel.Rotate(label, (GeoPolygon) arg[0], (NumberValue) arg[1], 
            								(GeoPoint) arg[2]);
			else {
                if (!ok[0])
                    throw argErr(app, c.getName(), arg[0]);
                else
                    throw argErr(app, c.getName(), arg[1]);
            }

        default :
            throw argNumErr(app, c.getName(), n);
    }
}
}

/*
 * Dilate[ <GeoPoint>, <NumberValue>, <GeoPoint> ] 
 * Dilate[ <GeoLine>, <NumberValue>, <GeoPoint> ] 
 * Dilate[ <GeoConic>, <NumberValue>, <GeoPoint> ]
 * Dilate[ <GeoPolygon>, <NumberValue>, <GeoPoint> ]
 */ 
class CmdDilate extends CommandProcessor {
	
	public CmdDilate(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    String label = c.getLabel();
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;
    GeoElement[] ret = new GeoElement[1];

    switch (n) {          
        case 3 :
            arg = resArgs(c);

            // dilate point, line or conic
            if ((ok[0] = (arg[0] instanceof Dilateable))
                && (ok[1] = (arg[1] .isNumberValue()))
                && (ok[2] = (arg[2] .isGeoPoint()))) {
            	Dilateable p = (Dilateable) arg[0];
                NumberValue phi = (NumberValue) arg[1];
                GeoPoint Q = (GeoPoint) arg[2];
                //GeoElement geo = p.toGeoElement();
                
                /*
                // if we are not in a nested command (suppress labels)
                // and no label is given and the input object is independent
                // we change the input object
                if (!cons.isSuppressLabelsActive() &&  
                	label == null && geo.isIndependent()) 
                {
                        p.dilate(phi, Q);
                        geo.updateRepaint();
                        ret[0] = geo;
                } else {
                    ret = kernel.Dilate(label, p, phi, Q);
                }
                */
                
                ret = kernel.Dilate(label, p, phi, Q);
                return ret;
            }
            
            // dilate polygon
            else  if ((ok[0] = (arg[0] .isGeoPolygon()))
            			&& (ok[1] = (arg[1] .isNumberValue()))
						&& (ok[2] = (arg[2] .isGeoPoint())))
				return kernel.Dilate(label, (GeoPolygon) arg[0], (NumberValue) arg[1], 
            								(GeoPoint) arg[2]);
			else {
                if (!ok[0])
                    throw argErr(app, c.getName(), arg[0]);
                else
                    throw argErr(app, c.getName(), arg[1]);
            }

        default :
            throw argNumErr(app, c.getName(), n);
    }
}
}

/*
 * (2nd argument is the mirror) 
 * Mirror[ <GeoPoint>, <GeoPoint> ] 
 * Mirror[<GeoLine>, <GeoPoint> ] 
 * Mirror[ <GeoConic>, <GeoPoint> ] 
 * Mirror[<GeoPolygon>, <GeoPoint> ]
 * 
 * Mirror[ <GeoPoint>, <GeoLine> ] 
 * Mirror[ <GeoLine>, <GeoLine> ] 
 * Mirror[ <GeoConic>, <GeoLine> ] 
 * Mirror[ <GeoPolygon>, <GeoLine> ]
 */ 
class CmdMirror extends CommandProcessor {
	
	public CmdMirror(Kernel kernel) {
		super(kernel);
	}
	
final public  GeoElement[] process(Command c) throws MyError {
    String label = c.getLabel();
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;
    GeoElement[] ret = new GeoElement[1];

    switch (n) {
        case 2 :
            arg = resArgs(c);

            if (arg[0].isGeoPoint() && arg[1].isGeoConic())
            {  // mirror point in circle Michael Borcherds 2008-02-10
                GeoPoint Q = (GeoPoint) arg[0];
                GeoConic conic = (GeoConic) arg[1];
                
                if (conic.getType()==GeoConic.CONIC_CIRCLE)
                {
                  ret = kernel.Mirror(label, Q, conic);
                  return ret;
                }

            }
            
            // mirror object
            if (ok[0] = (arg[0] instanceof Mirrorable)) {
            	Mirrorable p = (Mirrorable) arg[0];
            	//GeoElement geo = p.toGeoElement();
            	
            	 // mirror at point
            	if (ok[1] = (arg[1] .isGeoPoint())) {	                	
                    GeoPoint Q = (GeoPoint) arg[1];	  
                    
                    /*
                    // if we are not in a nested command (suppress labels)
                    // and no label is given and the input object is independent
                    // we change the input object
                    if (!cons.isSuppressLabelsActive() &&  
                    	label == null && geo.isIndependent()) 
                    {
                        p.mirror(Q);
                        geo.updateRepaint();
                        ret[0] = geo;
                    } else {
                        ret = kernel.Mirror(label, p, Q);
                    }
                    */
                    
                    ret = kernel.Mirror(label, p, Q);
                    return ret;
            	} 
            	 // mirror is line
            	else if (ok[1] = (arg[1] .isGeoLine())) {
                    GeoLine line = (GeoLine) arg[1];
                    
                    /*
                    // if we are not in a nested command (suppress labels)
                    // and no label is given and the input object is independent
                    // we change the input object
                    if (!cons.isSuppressLabelsActive() &&  
                    	label == null && geo.isIndependent()) 
                    {
	                    p.mirror(line);
	                    geo.updateRepaint();
	                    ret[0] = geo;
                    } else {
                        ret = kernel.Mirror(label, p, line);
                    }
                    */
                    
                    ret = kernel.Mirror(label, p, line);
                    return ret;
            	}
            }              
            
            // mirror polygon
            if (ok[0] = (arg[0] .isGeoPolygon())) {
            	GeoPolygon p = (GeoPolygon) arg[0];                	
            	
            	 // mirror at point
            	if (ok[1] = (arg[1] .isGeoPoint()))
					return kernel.Mirror(label, p, (GeoPoint) arg[1]);
				else if (ok[1] = (arg[1] .isGeoLine()))
					return kernel.Mirror(label, p, (GeoLine) arg[1]);
            }    
         
            // syntax error
            else {
                if (!ok[0])
                    throw argErr(app, c.getName(), arg[0]);
                else
                    throw argErr(app, c.getName(), arg[1]);
            }

        default :
            throw argNumErr(app, "Mirror", n);
    }
}
}

/*
 * Relation[ <GeoElement>, <GeoElement> ]
 */
class CmdRelation extends CommandProcessor {
	
	public CmdRelation(Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);

            // show relation string in a message dialog
            if ((ok[0] = (arg[0].isGeoElement()))
                && (ok[1] = (arg[1].isGeoElement()))) {
                app.showRelation((GeoElement) arg[0], (GeoElement) arg[1]);
                return null;
            }

            // syntax error
            else {
                if (!ok[0])
                    throw argErr(app, "Relation", arg[0]);
                else
                    throw argErr(app, "Relation", arg[1]);
            }

        default :
            throw argNumErr(app, "Relation", n);
    }
}
}

/*
 * Delete[ <GeoElement> ]
 */
class CmdDelete extends CommandProcessor {
	
	public CmdDelete(Kernel kernel) {
		super(kernel);
	}
	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);
            if (ok[0] = (arg[0].isGeoElement())) {
                GeoElement geo = (GeoElement) arg[0];
                GeoElement[] ret = { geo };
                // delete object
                geo.remove();
                return ret;
            } else
				throw argErr(app, "Delete", arg[0]);

        default :
            throw argNumErr(app, "Delete", n);
    }
}
}

/*
 * Derivative[ <GeoFunction> ]
 * Derivative[ <GeoCurveCartesian> ]
 */
class CmdDerivative extends CommandProcessor {
	
	public CmdDerivative (Kernel kernel) {
		super(kernel);
	}
	
final public   GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    String label = c.getLabel();
    GeoElement[] arg = resArgs(c);

    
    switch (n) {
        case 1 :           
            if (arg[0].isGeoDeriveable()) {
            	GeoDeriveable f = (GeoDeriveable) arg[0];
                if (label == null)
                    label = getDerivLabel(f.toGeoElement(), 1);
                GeoElement[] ret = { kernel.Derivative(label, f)};
                return ret;
            }                   
            else
				throw argErr(app, "Derivative", arg[0]);

        case 2 :                
            if (arg[0].isGeoDeriveable()
                && arg[1] .isNumberValue()) {
                double order = ((NumberValue) arg[1]).getDouble();
                if (order >= 0) {                    
                	GeoDeriveable f = (GeoDeriveable) arg[0];
                    if (label == null) {
                    	int iorder = (int) Math.round(order);
                    	label = getDerivLabel(f.toGeoElement(), iorder);
                    }                    	
                    GeoElement[] ret =
                        {
                             kernel.Derivative(
                                label,
                                f,
                                (NumberValue) arg[1])};
                    return ret;
                } else
					throw argErr(app, "Derivative", arg[1]);
            }            
            else {
                argErr(app, "Derivative", arg[0]);
            }

        default :
            throw argNumErr(app, "Derivative", n);
    }
        
}

	private String getDerivLabel(GeoElement geo, int order) {
		String label = null;
		
		if (geo.isLabelSet()) {
            label = geo.getLabel();
            for (int i = 0; i < order; i++)
                label = label + "'";
        }
        
		return label;
	}
}

/*
 * Integral[ <GeoFunction> ] Integral[ <GeoFunction>, <Number a>, <Number b> ]
 * Integral[ <GeoFunction f>, <GeoFunction g>, <Number a>, <Number b> ]
 */
class CmdIntegral extends CommandProcessor {
	
	public CmdIntegral (Kernel kernel) {
		super(kernel);
	}
	
final public    GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg = resArgs(c);     

    switch (n) {
        case 1 :                
            if (ok[0] = (arg[0].isGeoFunctionable())) {
                GeoElement[] ret =
                    { kernel.Integral(c.getLabel(), ((GeoFunctionable) arg[0]).getGeoFunction())};
                return ret;
            } else
				throw argErr(app, "Integral", arg[0]);

        case 3 :          
            if ((ok[0] = (arg[0].isGeoFunctionable()))
                && (ok[1] = (arg[1] .isNumberValue()))
                && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.Integral(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            (NumberValue) arg[1],
                            (NumberValue) arg[2])};
                return ret;
            } else {
                if (!ok[0])
                    throw argErr(app, "Integral", arg[0]);
                else if (!ok[1])
                    throw argErr(app, "Integral", arg[1]);
                else
                    throw argErr(app, "Integral", arg[2]);
            }

        case 4 :
            
            if ((ok[0] = (arg[0] .isGeoFunctionable()))
                && (ok[1] = (arg[1] .isGeoFunctionable()))
                && (ok[2] = (arg[2] .isNumberValue()))
                && (ok[3] = (arg[3] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.Integral(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            ((GeoFunctionable) arg[1]).getGeoFunction(),
                            (NumberValue) arg[2],
                            (NumberValue) arg[3])};
                return ret;
            } else {
                if (!ok[0])
                    throw argErr(app, "Integral", arg[0]);
                else if (!ok[1])
                    throw argErr(app, "Integral", arg[1]);
                else if (!ok[2])
                    throw argErr(app, "Integral", arg[2]);
                else
                    throw argErr(app, "Integral", arg[3]);
            }
        default :
            throw argNumErr(app, "Integral", n);
    }
}
}

/*
 * UpperSum[ <GeoFunction>, <Number>, <Number>, <Number> ]
 */
class CmdUpperSum extends CommandProcessor {
	
	public CmdUpperSum (Kernel kernel) {
		super(kernel);
	}
	
final public    GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 4 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoFunctionable()))
                && (ok[1] = (arg[1] .isNumberValue()))
                && (ok[2] = (arg[2] .isNumberValue()))
                && (ok[3] = (arg[3] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.UpperSum(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            (NumberValue) arg[1],
                            (NumberValue) arg[2],
                            (NumberValue) arg[3])};
                return ret;
            } else
				throw argErr(app, "UpperSum", null);

        default :
            throw argNumErr(app, "UpperSum", n);
    }
}
}

/*
 * BarChart[ <Number>, <Number>, <List> ]
 */
class CmdBarChart extends CommandProcessor {
	
	public CmdBarChart (Kernel kernel) {
		super(kernel);
	}
	
final public    GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
    case 2 :
        arg = resArgs(c);
        if ((ok[0] = (arg[0] .isGeoList()))
            && (ok[1] = (arg[1] .isGeoNumeric()))) {
            GeoElement[] ret =
                {
                     kernel.BarChart(
                        c.getLabel(),
                        (GeoList)arg[0],
                        (GeoNumeric)arg[1])};
            return ret;
        } else if ((ok[0] = (arg[0] .isGeoList()))
            && (ok[1] = (arg[1] .isGeoList()))) {
            GeoElement[] ret =
                {
                     kernel.BarChart(
                        c.getLabel(),
                        (GeoList)arg[0],
                        (GeoList)arg[1])};
            return ret;
        } else
			throw argErr(app, c.getName(), null);
    case 3 :
        arg = resArgs(c);
        if ((ok[0] = (arg[0] .isNumberValue()))
                && (ok[1] = (arg[1] .isNumberValue()))
                && (ok[2] = (arg[2] .isGeoList()))) {
                GeoElement[] ret =
                    {
                         kernel.BarChart(
                            c.getLabel(),
                            (NumberValue) arg[0],
                            (NumberValue) arg[1],
                            (GeoList)arg[2])};
                return ret;
            } else if ((ok[0] = (arg[0] .isGeoList()))
                    && (ok[1] = (arg[1] .isGeoList()))
                    && (ok[2] = (arg[2] .isNumberValue()))) {
                    GeoElement[] ret =
                        {
                             kernel.BarChart(
                                c.getLabel(),
                                (GeoList) arg[0],
                                (GeoList) arg[1],
                                (NumberValue)arg[2])};
                    return ret;
                } else
			throw argErr(app, c.getName(), null);
    case 6 :
        // create local variable at position 3 and resolve arguments
        arg = resArgsLocalNumVar(c, 3);      
        if ((ok[0] = (arg[0] .isNumberValue()))
            && (ok[1] = (arg[1] .isNumberValue()))
            && ((ok[2] = arg[2].isGeoElement()))
               	 && (ok[3] = arg[3].isGeoNumeric())
               	 && (ok[4] = arg[4].isNumberValue())
               	 && (ok[5] = arg[5].isNumberValue())) {
            GeoElement[] ret =
                {
                     kernel.BarChart(
                        c.getLabel(),
                        (NumberValue) arg[0],
                        (NumberValue) arg[1],
                        arg[2],
                        (GeoNumeric) arg[3],
                        (NumberValue) arg[4],
                        (NumberValue) arg[5],
                        null)};
            return ret;
        } else
			throw argErr(app, c.getName(), null);

    case 7 :
        // create local variable at position 3 and resolve arguments
        arg = resArgsLocalNumVar(c, 3);      
        if ((ok[0] = (arg[0] .isNumberValue()))
            && (ok[1] = (arg[1] .isNumberValue()))
            && ((ok[2] = arg[2].isGeoElement()))
               	 && (ok[3] = arg[3].isGeoNumeric())
               	 && (ok[4] = arg[4].isNumberValue())
               	 && (ok[5] = arg[5].isNumberValue())
               	 && (ok[6] = arg[6].isNumberValue())) {
            GeoElement[] ret =
                {
                     kernel.BarChart(
                        c.getLabel(),
                        (NumberValue) arg[0],
                        (NumberValue) arg[1],
                        arg[2],
                        (GeoNumeric) arg[3],
                        (NumberValue) arg[4],
                        (NumberValue) arg[5],
                        (NumberValue) arg[6])};
            return ret;
        } else
			throw argErr(app, c.getName(), null);

        default :
            throw argNumErr(app, c.getName(), n);
    }
}
}
/*
 * BarChart[ <Number>, <Number>, <List> ]
 */
class CmdBoxPlot extends CommandProcessor {
	
	public CmdBoxPlot (Kernel kernel) {
		super(kernel);
	}
	
final public    GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

   switch (n) {
        case 3 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isNumberValue()))
                    && (ok[1] = (arg[1] .isNumberValue()))
                && (ok[2] = (arg[2] .isGeoList()))) {
                GeoElement[] ret =
                    {
                         kernel.BoxPlot(
                            c.getLabel(),
                            (NumberValue) arg[0],
                            (NumberValue) arg[1],
                            (GeoList)arg[2])};
                return ret;
            } else
				throw argErr(app, c.getName(), null);

        case 7:
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isNumberValue())
                    && (ok[1] = (arg[1] .isNumberValue()))
                    && (ok[2] = (arg[2] .isNumberValue()))
                    && (ok[3] = (arg[3] .isNumberValue()))
                    && (ok[4] = (arg[4] .isNumberValue()))
                    && (ok[5] = (arg[5] .isNumberValue()))
                    && (ok[6] = (arg[6] .isNumberValue()))))  {
	            
	           	 	GeoElement[] ret = { kernel.BoxPlot(c.getLabel(), (NumberValue) arg[0],
	           	 		(NumberValue) arg[1], (NumberValue) arg[2], (NumberValue) arg[3], (NumberValue) arg[4], (NumberValue) arg[5], (NumberValue) arg[6])};
	                return ret;  
	            
            }
	            // else continue:
	                
        default :
            throw argNumErr(app, c.getName(), n);
    }
}
}
/*
 * Histogram[ <List>, <List> ]
 */
class CmdHistogram extends CommandProcessor {
	
	public CmdHistogram (Kernel kernel) {
		super(kernel);
	}
	
final public    GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoList()))
                && (ok[1] = (arg[1] .isGeoList()))) {
                GeoElement[] ret =
                    {
                         kernel.Histogram(
                            c.getLabel(),                      
                            (GeoList)arg[0],
                			(GeoList)arg[1])};
                return ret;
            } else
				throw argErr(app, c.getName(), null);

        default :
            throw argNumErr(app, c.getName(), n);
    }
}
}

/*
 * LowerSum[ <GeoFunction>, <Number>, <Number>, <Number> ]
 */
class CmdLowerSum extends CommandProcessor {
	
	public CmdLowerSum (Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 4 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoFunctionable()))
                && (ok[1] = (arg[1] .isNumberValue()))
                && (ok[2] = (arg[2] .isNumberValue()))
                && (ok[3] = (arg[3] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.LowerSum(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            (NumberValue) arg[1],
                            (NumberValue) arg[2],
                            (NumberValue) arg[3])};
                return ret;
            } else
				throw argErr(app, "LowerSum", null);

        default :
            throw argNumErr(app, "LowerSum", n);
    }
}
}

/*
 * LowerSum[ <GeoFunction>, <Number>, <Number>, <Number> ]
 */
class CmdTrapezoidalSum extends CommandProcessor {
	
	public CmdTrapezoidalSum (Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 4 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoFunctionable()))
                && (ok[1] = (arg[1] .isNumberValue()))
                && (ok[2] = (arg[2] .isNumberValue()))
                && (ok[3] = (arg[3] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.TrapezoidalSum(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            (NumberValue) arg[1],
                            (NumberValue) arg[2],
                            (NumberValue) arg[3])};
                return ret;
            } else
				throw argErr(app, "TrapezoidalSum", null);

        default :
            throw argNumErr(app, "TrapezoidalSum", n);
    }
}
}

/*
 * Polynomial[ <GeoFunction> ]
 */
class CmdPolynomial extends CommandProcessor {
	
	public CmdPolynomial (Kernel kernel) {
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
                          kernel.PolynomialFunction(
                             c.getLabel(),
                             ((GeoFunctionable) arg[0]).getGeoFunction())};
                 return ret;                
             }                        
             // Michael Borcherds 2008-01-22 BEGIN
             // PolynomialFromCoordinates
             else if (ok[0] = (arg[0].isGeoList())) {
                 GeoElement[] ret =
                 {
                      kernel.PolynomialFunction(
                         c.getLabel(),
                         ((GeoList) arg[0]))};
                 return ret;                 
             } 
             // Michael Borcherds 2008-01-22 END                                   
             else
            	 throw argErr(app, c.getName(), arg[0]);         
			 
	     // more than one argument
         default :
        	 // Markus Hohenwarter 2008-01-26 BEGIN
             // try to create list of points
        	 GeoList list = wrapInList(kernel, arg, GeoElement.GEO_CLASS_POINT);
             if (list != null) {
            	 GeoElement[] ret = { kernel.PolynomialFunction(c.getLabel(), list)};
                 return ret;             	     	 
             } 
             // Markus Hohenwarter 2008-01-26 END
             else        	 
            	 throw argNumErr(app, c.getName(), n);
     }
 }    
}

/*
 * TaylorSeries[ <GeoFunction>, <Number>, <Number> ]
 */
class CmdTaylorSeries extends CommandProcessor {
	
	public CmdTaylorSeries (Kernel kernel) {
		super(kernel);
	}
	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 3 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoFunctionable()))
                && (ok[1] = (arg[1] .isNumberValue()))
                && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.TaylorSeries(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            (NumberValue) arg[1],
                            (NumberValue) arg[2])};
                return ret;
            } else
				throw argErr(app, "TaylorSeries", null);

        default :
            throw argNumErr(app, "TaylorSeries", n);
    }
}
}

/*
 * Root[ <GeoFunction> ] Root[ <GeoFunction> , <Number> ] Root[ <GeoFunction> ,
 * <Number> , <Number> ]
 */
class CmdRoot extends CommandProcessor {
	
	public CmdRoot (Kernel kernel) {
		super(kernel);
	}
	
final public   GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        //  roots of polynomial
        case 1 :
            arg = resArgs(c);
            if (ok[0] = (arg[0] .isGeoFunctionable()))
				return kernel.Root(c.getLabels(), 
                		((GeoFunctionable) arg[0]).getGeoFunction());
			else
				throw argErr(app, "Root", arg[0]);

            //  root with start value
        case 2 :
            arg = resArgs(c);
            if (ok[0] =
                (arg[0] .isGeoFunctionable())
                    && (ok[1] = (arg[1] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.Root(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            (NumberValue) arg[1])};
                return ret;
            } else {
                if (!ok[0])
                    throw argErr(app, "Root", arg[0]);
                else
                    throw argErr(app, "Root", arg[1]);
            }

            // root in interval
        case 3 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoFunctionable()))
                && (ok[1] = (arg[1] .isNumberValue()))
                && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.Root(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            (NumberValue) arg[1],
                            (NumberValue) arg[2])};
                return ret;
            } else {
                if (!ok[0])
                    throw argErr(app, "Root", arg[0]);
                else if (!ok[1])
                    throw argErr(app, "Root", arg[1]);
                else
                    throw argErr(app, "Root", arg[2]);
            }

        default :
            throw argNumErr(app, "Root", n);
    }
}
}

/*
 * Extremum[ <GeoFunction> ]
 */
class CmdExtremum extends CommandProcessor {
	
	public CmdExtremum (Kernel kernel) {
		super(kernel);
	}
	
final public   GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);
            if (ok[0] = (arg[0] .isGeoFunctionable()))
				return kernel.Extremum(c.getLabels(), 
                		((GeoFunctionable) arg[0]).getGeoFunction());
			else
				throw argErr(app, "Extremum", arg[0]);

        default :
            throw argNumErr(app, "Extremum", n);
    }
}
}

/*
 * TurningPoint[ <GeoFunction> ]
 */
class CmdTurningPoint extends CommandProcessor {
	
	public CmdTurningPoint (Kernel kernel) {
		super(kernel);
	}
	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);
            if (ok[0] = (arg[0] .isGeoFunctionable()))
				return kernel.TurningPoint(
                    c.getLabels(),
                    ((GeoFunctionable) arg[0]).getGeoFunction());
			else
				throw argErr(app, "TurningPoint", arg[0]);

        default :
            throw argNumErr(app, "TurningPoint", n);
    }
}
}

/*
 * Function[ <GeoFunction>, <NumberValue>, <NumberValue> ]
 */
class CmdFunction extends CommandProcessor {
	
	public CmdFunction (Kernel kernel) {
		super(kernel);
	}
	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg = resArgs(c);

    switch (n) {
        case 3 :            	                
            if ((ok[0] = (arg[0] .isGeoFunctionable()))
                && (ok[1] = (arg[1] .isNumberValue()))
                && (ok[2] = (arg[2] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernel.Function(
                            c.getLabel(),
                            ((GeoFunctionable) arg[0]).getGeoFunction(),
                            (NumberValue) arg[1],
                            (NumberValue) arg[2])
					};
                return ret;
            }                                
            else {
                if (!ok[0])
                    throw argErr(app, "Function", arg[0]);
                else if (!ok[1])
                    throw argErr(app, "Function", arg[1]);
                else
                    throw argErr(app, "Function", arg[2]);
            }

        default :
            throw argNumErr(app, "Function", n);
    }
}
}


/*
 * Centroid[ <Polygon> ]
 */
class CmdCentroid extends CommandProcessor {
	
	public CmdCentroid (Kernel kernel) {
		super(kernel);
	}
	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);
            if (ok[0] = (arg[0] .isGeoPolygon())) {
                GeoElement[] ret =
                    { kernel.Centroid(c.getLabel(), (GeoPolygon) arg[0])};
                return ret;
            } else
				throw argErr(app, "Centroid", arg[0]);

        default :
            throw argNumErr(app, "Centroid", n);
    }
}
}




/*
 * If[ <GeoBoolean>, <GeoElement> ]
 * If[ <GeoBoolean>, <GeoElement>, <GeoElement> ]
 */
class CmdIf extends CommandProcessor {
	
	public CmdIf(Kernel kernel) {
		super(kernel);
	}
	
    public GeoElement[] process(Command c) throws MyError {
        int n = c.getArgumentNumber();
        boolean[] ok = new boolean[n];
        GeoElement[] arg;

        switch (n) {            	
            case 2: // if - then
            case 3: //	if - then - else
                arg = resArgs(c);                            
                GeoElement geoElse = n == 3 ? arg[2] : null;
                
                // standard case: simple boolean condition
                if (ok[0] = arg[0].isGeoBoolean()) {
                    GeoElement[] ret =
                        {
                             kernel.If(
                                c.getLabel(),
                                (GeoBoolean) arg[0],
								 arg[1],
                                 geoElse)};
                    return ret;
                } 

                // SPECIAL CASE for functions: 
                // boolean function in x as condition 
                //   example: If[ x < 2, x^2, x + 2 ]
                // DO NOT change instanceof here (see GeoFunction.isGeoFunctionable())
                else if (ok[0] = (arg[0] instanceof GeoFunction)) {
                	GeoFunction booleanFun = (GeoFunction) arg[0];
                	if ((ok[0] = booleanFun.isBooleanFunction()) &&
                		(ok[1] = arg[1].isGeoFunctionable()) &&
						(geoElse == null || geoElse.isGeoFunctionable())) 
                	{
                		GeoFunction elseFun = geoElse == null ? null :
                			((GeoFunctionable) geoElse).getGeoFunction();
				
                		GeoElement[] ret =
                        	{
                             kernel.If(
                                c.getLabel(),
                                (GeoFunction) booleanFun,
                                ((GeoFunctionable) arg[1]).getGeoFunction(),
                                elseFun )
                        	};
                		return ret;
                	}
                } 
                                  
            	if (!ok[0])
                    throw argErr(app, c.getName(), arg[0]);
                else if (n == 2 || !ok[1])
                    throw argErr(app, c.getName(), arg[1]);
                else
                    throw argErr(app, c.getName(), arg[2]);                                 
                                             
            default :
                throw argNumErr(app, c.getName(), n);
        }
    }
    
}

/*
 * CountIf[ <GeoBoolean>, <GeoList> ]
 */
class CmdCountIf extends CommandProcessor {
	
	public CmdCountIf(Kernel kernel) {
		super(kernel);
	}
	
    public GeoElement[] process(Command c) throws MyError {
        int n = c.getArgumentNumber();
        boolean[] ok = new boolean[n];
        GeoElement[] arg;

        switch (n) {            	
            case 2: 

                arg = resArgs(c);   
                
                // DO NOT change instanceof here (see GeoFunction.isGeoFunctionable())
                if (ok[0] = (arg[0] instanceof GeoFunction)) {
                	GeoFunction booleanFun = (GeoFunction) arg[0];
                	if ((ok[0] = booleanFun.isBooleanFunction()) &&
                		(ok[1] = arg[1].isGeoList())) 
                	{
				
                		GeoElement[] ret =
                        	{
                             kernel.CountIf(
                                c.getLabel(),
                                (GeoFunction) booleanFun,
                                ((GeoList) arg[1]) )
                        	};
                		return ret;
                	}
                } 
                                  
            	if (!ok[0])
                    throw argErr(app, c.getName(), arg[0]);
                else 
                    throw argErr(app, c.getName(), arg[1]);
                                             
            default :
                throw argNumErr(app, c.getName(), n);
        }
    }
    
}

/**
* Victor Franco Espino 11-02-2007: Command's processors for new commands
*/

class CmdAffineRatio extends CommandProcessor {
	public CmdAffineRatio(Kernel kernel) {
		super(kernel);
	}
	
	public GeoElement[] process(Command c) throws MyError {
       int n = c.getArgumentNumber();
       boolean[] ok = new boolean[n];
       GeoElement[] arg;
       
       switch (n) {
         case 3 :
           arg = resArgs(c);
           if ( (ok[0] = (arg[0].isGeoPoint()) )
               && (ok[1] = (arg[1].isGeoPoint()) ) 
               && (ok[2] = (arg[2].isGeoPoint()) ) ){
               GeoElement[] ret =
                   {
                        kernel.AffineRatio(
                           c.getLabel(),
                           (GeoPoint) arg[0],
                           (GeoPoint) arg[1],
                           (GeoPoint) arg[2])};
               return ret;
           } else {
               if (!ok[0])
                   throw argErr(app, c.getName(), arg[0]);
               if (!ok[1])
                   throw argErr(app, c.getName(), arg[1]);
               else
                   throw argErr(app, c.getName(), arg[2]);
           }

       default :
           throw argNumErr(app, c.getName(), n);
       }
	}
}

class CmdCrossRatio extends CommandProcessor {
	public CmdCrossRatio(Kernel kernel) {
		super(kernel);
	}
	
	public GeoElement[] process(Command c) throws MyError {
       int n = c.getArgumentNumber();
       boolean[] ok = new boolean[n];
       GeoElement[] arg;
       
       switch (n) {
         case 4 :
           arg = resArgs(c);
           if ( (ok[0] = (arg[0].isGeoPoint()) )
               && (ok[1] = (arg[1].isGeoPoint()) ) 
               && (ok[2] = (arg[2].isGeoPoint()) )
               && (ok[3] = (arg[3].isGeoPoint()) ) ){
               GeoElement[] ret =
                   {
                        kernel.CrossRatio(
                           c.getLabel(),
                           (GeoPoint) arg[0],
                           (GeoPoint) arg[1],
                           (GeoPoint) arg[2],
                           (GeoPoint) arg[3])};
               return ret;
           } else {
               if (!ok[0])
                   throw argErr(app, c.getName(), arg[0]);
               if (!ok[1])
                   throw argErr(app, c.getName(), arg[1]);
               if (!ok[2])
                   throw argErr(app, c.getName(), arg[2]);
               else
                   throw argErr(app, c.getName(), arg[3]);
           }

       default :
           throw argNumErr(app, c.getName(), n);
       }
	}
}

class CmdCurvatureVector extends CommandProcessor {
	public CmdCurvatureVector(Kernel kernel) {
		super(kernel);
	}
	
	public GeoElement[] process(Command c) throws MyError {
       int n = c.getArgumentNumber();
       boolean[] ok = new boolean[n];
       GeoElement[] arg;
       
       switch (n) {
         case 2 :
           arg = resArgs(c);
           if ( (ok[0] = (arg[0].isGeoPoint()) )
               && (ok[1] = (arg[1].isGeoFunctionable()) ) ){
               GeoElement[] ret =
                   {
                        kernel.CurvatureVector(
                           c.getLabel(),
                           (GeoPoint) arg[0],
                           (GeoFunction) arg[1])};
               return ret;
           }else if( (ok[0] = (arg[0].isGeoPoint()) )
                   && (ok[1] = (arg[1].isGeoCurveCartesian()) ) ){
           	GeoElement[] ret =
                   {
                    kernel.CurvatureVectorCurve(
                       c.getLabel(),
                       (GeoPoint) arg[0],
                       (GeoCurveCartesian) arg[1])};
               return ret;
           } else {
               if (!ok[0])
                   throw argErr(app, c.getName(), arg[0]);
               else
                   throw argErr(app, c.getName(), arg[1]);
           }

       default :
           throw argNumErr(app, c.getName(), n);
       }
	}
}

class CmdCurvature extends CommandProcessor {
	public CmdCurvature(Kernel kernel) {
		super(kernel);
	}
	
	public GeoElement[] process(Command c) throws MyError {
       int n = c.getArgumentNumber();
       boolean[] ok = new boolean[n];
       GeoElement[] arg;
       
       switch (n) {
         case 2 :
           arg = resArgs(c);
           if ( (ok[0] = (arg[0].isGeoPoint()) )
               && (ok[1] = (arg[1].isGeoFunctionable()) ) ){
               GeoElement[] ret =
                   {
                        kernel.Curvature(
                           c.getLabel(),
                           (GeoPoint) arg[0],
                           (GeoFunction) arg[1])};
               return ret;
           }else if( (ok[0] = (arg[0].isGeoPoint()) )
                   && (ok[1] = (arg[1].isGeoCurveCartesian()) ) ){
           	GeoElement[] ret =
                   {
                    kernel.CurvatureCurve(
                       c.getLabel(),
                       (GeoPoint) arg[0],
                       (GeoCurveCartesian) arg[1])};
                return ret;
           } else {
               if (!ok[0])
                   throw argErr(app, c.getName(), arg[0]);
               else
                   throw argErr(app, c.getName(), arg[1]);
           }

       default :
           throw argNumErr(app, c.getName(), n);
       }
	}
}



class CmdOsculatingCircle extends CommandProcessor {
	public CmdOsculatingCircle(Kernel kernel) {
		super(kernel);
	}
	
	public GeoElement[] process(Command c) throws MyError {
       int n = c.getArgumentNumber();
       boolean[] ok = new boolean[n];
       GeoElement[] arg;
       
       switch (n) {
         case 2 :
           arg = resArgs(c);
           if ( (ok[0] = (arg[0].isGeoPoint()) )
               && (ok[1] = (arg[1].isGeoFunctionable()) ) ){
               GeoElement[] ret =
                   {
                        kernel.OsculatingCircle(
                           c.getLabel(),
                           (GeoPoint) arg[0],
                           (GeoFunction) arg[1])};
               return ret;
           }else if( (ok[0] = (arg[0].isGeoPoint()) )
                   && (ok[1] = (arg[1].isGeoCurveCartesian()) ) ){
           	GeoElement[] ret =
                   {
                    kernel.OsculatingCircleCurve(
                       c.getLabel(),
                       (GeoPoint) arg[0],
                       (GeoCurveCartesian) arg[1])};
               return ret;
           } else {
               if (!ok[0])
                   throw argErr(app, c.getName(), arg[0]);
               else
                   throw argErr(app, c.getName(), arg[1]);
           }

       default :
           throw argNumErr(app, c.getName(), n);
       }
	}
}



/*
* Victor Franco Espino 11-02-2007: End command's processors for new commands
*/

class CmdTableText extends CommandProcessor {
	public CmdTableText(Kernel kernel) {
		super(kernel);
	}
	
	public GeoElement[] process(Command c) throws MyError {
       int n = c.getArgumentNumber();
       boolean[] ok = new boolean[n];
       GeoElement[] arg;
       arg = resArgs(c);
       
       switch (n) {
       
         case 1 :
           if ( (ok[0] = (arg[0].isGeoList()) ) ){
        	   GeoList list = (GeoList)arg[0];
        	   
        	   if (list.size() == 0)
                   throw argErr(app, c.getName(), arg[0]);
        	   
        	   if (list.get(0).isGeoList()) { // list of lists: no need to wrap   		   
               GeoElement[] ret =
                   {
                        kernel.TableText(
                           c.getLabel(),
                           (GeoList) arg[0])};
               return ret;
        	   } else {
        		   list = wrapInList(kernel, arg, -1);
                   if (list != null) {
                  	 GeoElement[] ret = { kernel.TableText(c.getLabel(), list)};
                       return ret;             	     	 
                   } 
       			throw argErr(app, c.getName(), arg[0]);        		   
        	   }
           } else {
                   throw argErr(app, c.getName(), arg[0]);
           }
           
         case 0:
        	 throw argNumErr(app, c.getName(), n);

         default :
             // try to create list of numbers
        	 GeoList list = wrapInList(kernel, arg, -1);
             if (list != null) {
            	 GeoElement[] ret = { kernel.TableText(c.getLabel(), list)};
                 return ret;             	     	 
             } 
 			throw argErr(app, c.getName(), arg[0]);
       }
	}
}

class CmdObject extends CommandProcessor {
	public CmdObject(Kernel kernel) {
		super(kernel);
	}
	
	public GeoElement[] process(Command c) throws MyError {
       int n = c.getArgumentNumber();
       boolean[] ok = new boolean[n];
       GeoElement[] arg;
       arg = resArgs(c);
       
       switch (n) {
         case 1 :
           if ( (ok[0] = (arg[0].isGeoText()) ) ){
               GeoElement[] ret =
                   {
                        kernel.Object(
                           c.getLabel(),
                           (GeoText) arg[0])};
               return ret;
           } else {
                   throw argErr(app, c.getName(), arg[0]);
           }

         default :
			throw argNumErr(app, c.getName(), n);
       }
	}
}


class CmdColumnName extends CommandProcessor {
	public CmdColumnName(Kernel kernel) {
		super(kernel);
	}
	
	public GeoElement[] process(Command c) throws MyError {
       int n = c.getArgumentNumber();
       boolean[] ok = new boolean[n];
       GeoElement[] arg;
       arg = resArgs(c);
       
       switch (n) {
         case 1 :
               GeoElement[] ret =
                   {
                        kernel.ColumnName(
                           c.getLabel(),
                            arg[0])};
               return ret;

         default :
			throw argNumErr(app, c.getName(), n);
       }
	}
}

class CmdAppend extends CommandProcessor {

	public CmdAppend(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 2:

			if (arg[0].isGeoList() ) {
				GeoElement[] ret = { 
						kernel.Append(c.getLabel(),
						(GeoList) arg[0], arg[1] ) };
				return ret;
			} else if (arg[1].isGeoList() ) {
				GeoElement[] ret = { 
						kernel.Append(c.getLabel(),
						 arg[0], (GeoList)arg[1] ) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[0]);
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

class CmdJoin extends CommandProcessor {

	public CmdJoin(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
	    boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 1:
			
			ok[0] = arg[0].isGeoList();

			if (ok[0] ) {
				GeoElement[] ret = { 
						kernel.Join(c.getLabel(),
						(GeoList) arg[0] ) };
				return ret;
			} else

	               if (!ok[0])
	                   throw argErr(app, c.getName(), arg[0]);
	               else
	                   throw argErr(app, c.getName(), arg[1]);
		
		default:
            // try to create list of numbers
	       	 GeoList list = wrapInList(kernel, arg, GeoElement.GEO_CLASS_LIST);
	            if (list != null) {
	           	 GeoElement[] ret = { kernel.Join(c.getLabel(), list)};
	                return ret;             	     	 
	            } 
			throw argNumErr(app, c.getName(), n);
		}
	}

}

class CmdUnion extends CommandProcessor {

	public CmdUnion(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 2:

			if (arg[0].isGeoList() && arg[1].isGeoList() ) {
				GeoElement[] ret = { 
						kernel.Union(c.getLabel(),
						(GeoList) arg[0], (GeoList)arg[1] ) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[0]);
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

class CmdIntersection extends CommandProcessor {

	public CmdIntersection(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 2:

			if (arg[0].isGeoList() && arg[1].isGeoList() ) {
				GeoElement[] ret = { 
						kernel.Intersection(c.getLabel(),
						(GeoList) arg[0], (GeoList)arg[1] ) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[0]);
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

class CmdInsert extends CommandProcessor {

	public CmdInsert(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 3:

			if (arg[1].isGeoList() && arg[2].isGeoNumeric()) {
				GeoElement[] ret = { 
						kernel.Insert(c.getLabel(),
						arg[0], (GeoList)arg[1], (GeoNumeric)arg[2] ) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[0]);
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

class CmdIsInteger extends CommandProcessor {

	public CmdIsInteger(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 1:

			if (arg[0].isGeoNumeric()) {
				GeoElement[] ret = { 
						kernel.IsInteger(c.getLabel(),
						(GeoNumeric)arg[0] ) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[0]);
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

class CmdRandomPoisson extends CommandProcessor {

	public CmdRandomPoisson(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 1:

			if (arg[0].isGeoNumeric()) {
				GeoElement[] ret = { 
						kernel.RandomPoisson(c.getLabel(),
						(GeoNumeric)arg[0] ) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[0]);
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

class CmdFractionText extends CommandProcessor {

	public CmdFractionText(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 1:

			if (arg[0].isGeoNumeric()) {
				GeoElement[] ret = { 
						kernel.FractionText(c.getLabel(),
						(GeoNumeric)arg[0] ) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[0]);
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

class CmdKeepIf extends CommandProcessor {

	public CmdKeepIf(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
        boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 2:                 
			
			if (ok[0] = (arg[0] instanceof GeoFunction)) {
        	GeoFunction booleanFun = (GeoFunction) arg[0];
        	if ((ok[0] = booleanFun.isBooleanFunction()) &&
        		(ok[1] = arg[1].isGeoList())) 
        	{
		
        		GeoElement[] ret =
                	{
                     kernel.KeepIf(
                        c.getLabel(),
                        (GeoFunction) booleanFun,
                        ((GeoList) arg[1]) )
                	};
        		return ret;
        	}
        	
        	if (!ok[0])
                throw argErr(app, c.getName(), arg[0]);
            else 
                throw argErr(app, c.getName(), arg[1]);

        } 

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

class CmdAxisStepX extends CommandProcessor {

	public CmdAxisStepX(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
        boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 0:                 
			
        		GeoElement[] ret =
                	{
                     kernel.AxisStepX(c.getLabel())};
        		return ret;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

class CmdAxisStepY extends CommandProcessor {

	public CmdAxisStepY(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
        boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 0:                 
			
        		GeoElement[] ret =
                	{
                     kernel.AxisStepY(c.getLabel())};
        		return ret;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

class CmdSimplify extends CommandProcessor {
	
	public CmdSimplify (Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
     int n = c.getArgumentNumber();
     GeoElement[] arg;
     arg = resArgs(c);
     
     switch (n) {
         case 1 :             
             if ((arg[0].isGeoFunction())) {
                 GeoElement[] ret =
                     { kernel.Simplify(c.getLabel(), (GeoFunction) arg[0] )};
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


