/*
 * GeoGebra - Dynamic Geometry and Algebra Copyright Markus Hohenwarter,
 * http://www.geogebra.at
 * 
 * This file is part of GeoGebra.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 */

package geogebra.algebra;

import geogebra.Application;
import geogebra.MyError;
import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.Construction;
import geogebra.kernel.Dilateable;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoFunctionable;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoSegment;
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

import java.util.LinkedList;
import java.util.ListIterator;

public abstract class CommandProcessor  {
	
    AlgebraController algCtrl;
    Application app;
    Kernel kernel;
    Construction cons;
	
	public CommandProcessor(AlgebraController algCtrl) {
		 this.algCtrl = algCtrl;        
	     kernel = algCtrl.getKernel();     
	     cons = kernel.getConstruction();
	     app = kernel.getApplication();
	}
	
	/**
	 *  Every CommandProcessor has to implement this method
	 */
   	public abstract GeoElement [] process(Command c) throws MyError, CircularDefinitionException;   	
    
    final GeoElement[] resArgs(Command c) throws MyError {	  	
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
           GeoElement[] geos = algCtrl.processExpressionNode(arg);

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
    			throw argErr(app, c.getLabel(), c.getArgument(varPos));
    		}
    		
    		// create local variable
    	    GeoNumeric num = new GeoNumeric(cons);
    	    kernel.addLocalVariable(localVarName, num);            
    	    
    	    // resolve all command arguments including the local variable just created
    	    GeoElement [] arg = resArgs(c);                                   
    	    
    	    // clear local var table again
    	    kernel.clearLocalVariableTable();
    	    
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
        
        
    	
}



/* *****************************************
 *     Command classes used by CommandDispatcher
 *  *****************************************/


class CmdCenter extends CmdMidpoint {
	public CmdCenter(AlgebraController algCtrl) {
		super(algCtrl);
	}
}
       
/*
 * Midpoint[ <GeoConic> ] Midpoint[ <GeoPoint>, <GeoPoint> ]
 */
class CmdMidpoint extends CommandProcessor {
	
	public CmdMidpoint(AlgebraController algCtrl) {
		super(algCtrl);
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
 * Line[ <GeoPoint>, <GeoPoint> ] Line[ <GeoPoint>, <GeoVector> ] Line[
 * <GeoPoint>, <GeoLine> ]
 */
class CmdLine extends CommandProcessor {
	
	public CmdLine(AlgebraController algCtrl) {
		super(algCtrl);
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
                         kernel.Line(
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
                         kernel.Line(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoVector) arg[1])};
                return ret;
            }

            // line through point parallel to another line
            else if (
                (ok[0] = (arg[0] .isGeoPoint()))
                    && (ok[1] = (arg[1] .isGeoLine()))) {
                GeoElement[] ret =
                    {
                         kernel.Line(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoLine) arg[1])};
                return ret;
            }

            // syntax error
            else {
                if (!ok[0])
                    throw argErr(app, "Line", arg[0]);
                else
                    throw argErr(app, "Line", arg[1]);
            }

        default :
            throw argNumErr(app, "Line", n);
    }
}
}

/*
 * Ray[ <GeoPoint>, <GeoPoint> ] Ray[ <GeoPoint>, <GeoVector> ]
 */
class CmdRay extends CommandProcessor {
	
	public CmdRay(AlgebraController algCtrl) {
		super(algCtrl);
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
 * Segment[ <GeoPoint>, <GeoPoint> ] Segment[ <GeoPoint>, <Number> ]
 */
class CmdSegment extends CommandProcessor {
	
	public CmdSegment(AlgebraController algCtrl) {
		super(algCtrl);
	}
	
final public GeoElement[] process(Command c) throws MyError {	
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);

            // segment between two points
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.Segment(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoPoint) arg[1])};
                return ret;
            }
            
            // segment from point with given length
            else if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isNumberValue())))
				return
                         kernel.Segment(
                            c.getLabels(),
                            (GeoPoint) arg[0],
                            (NumberValue) arg[1]);
			else {
                if (!ok[0])
                    throw argErr(app, "Segment", arg[0]);
                else
                    throw argErr(app, "Segment", arg[1]);
            }

        default :
            throw argNumErr(app, "Segment", n);
    }
}
}

/*
 * Orthogonal[ <GeoPoint>, <GeoVector> ] Orthogonal[ <GeoPoint>, <GeoLine> ]
 */
class CmdOrthogonalLine extends CommandProcessor {
	
	public CmdOrthogonalLine(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdLineBisector(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdAngularBisector(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdVector(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdDirection(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdSlope(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdOrthogonalVector(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdUnitVector(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdUnitOrthogonalVector(AlgebraController algCtrl) {
		super(algCtrl);
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
 * Length[ <GeoVector> ] Length[ <GeoPoint> ]
 */
class CmdLength extends CommandProcessor {
	
	public CmdLength(AlgebraController algCtrl) {
		super(algCtrl);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);
            if (ok[0] = (arg[0] .isGeoVector() ||
            		     arg[0] .isGeoPoint())) {
                GeoElement[] ret =
                    { kernel.Length(c.getLabel(), (GeoVec3D) arg[0])};
                return ret;
            } else {
                if (!ok[0])
                    throw argErr(app, "Length", arg[0]);
            }

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
	
	public CmdDistance(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdAngle(AlgebraController algCtrl) {
		super(algCtrl);
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
 */
class CmdArea extends CommandProcessor {
	
	public CmdArea(AlgebraController algCtrl) {
		super(algCtrl);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    GeoElement[] arg;

    if (n > 2) {
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
 * Polygon[ <GeoPoint>, ..., <GeoPoint> ]
 */
class CmdPolygon extends CommandProcessor {
	
	public CmdPolygon(AlgebraController algCtrl) {
		super(algCtrl);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    GeoElement[] arg;

    if (n > 2) {
        arg = resArgs(c);
        GeoPoint[] points = new GeoPoint[n];
        // check arguments
        for (int i = 0; i < n; i++) {
            if (!(arg[i].isGeoPoint()))
				throw argErr(app, "Polygon", arg[i]);
			else {
                points[i] = (GeoPoint) arg[i];
            }
        }
        // everything ok
        return kernel.Polygon(c.getLabels(), points);
    } else
		throw argNumErr(app, "Polygon", n);
}
}

/*
 * Focus[ <GeoConic> ]
 */
class CmdFocus extends CommandProcessor {
	
	public CmdFocus(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdVertex(AlgebraController algCtrl) {
		super(algCtrl);
	}
	
final public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);
            if (ok[0] = (arg[0] .isGeoConic()))
				return kernel.Vertex(c.getLabels(), (GeoConic) arg[0]);
			else
				throw argErr(app, "Vertex", arg[0]);

        default :
            throw argNumErr(app, "Vertex", n);
    }
}
}

/*
 * Circle[ <GeoPoint>, <GeoNumeric> ] Circle[ <GeoPoint>, <GeoPoint> ] Circle[
 * <GeoPoint>, <GeoPoint>, <GeoPoint> ]
 */
class CmdCircle extends CommandProcessor {
	
	public CmdCircle(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdSemicircle(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdLocus(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdArc(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdSector(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdCircleArc(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdCircleSector(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdCircumcircleArc(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdCircumcircleSector(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdParabola(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdEllipse(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdHyperbola(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdConic(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdIntersect(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdPolar(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdDiameter(AlgebraController algCtrl) {
		super(algCtrl);
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
 */
class CmdTangent extends CommandProcessor {
	
	public CmdTangent(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdAsymptote(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdAxes(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdFirstAxis(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdSecondAxis(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdFirstAxisLength(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdSecondAxisLength(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdExcentricity(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdParameter(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdRadius(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdDirectrix(AlgebraController algCtrl) {
		super(algCtrl);
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
 * Translate[ <GeoVector>, <GeoPoint> ] // set start point Translate[
 * <GeoPolygon>, <GeoVector> ]
 *  
 */
class CmdTranslate extends CommandProcessor {
	
	public CmdTranslate(AlgebraController algCtrl) {
		super(algCtrl);
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
                GeoElement geo = p.toGeoElement();
                if (label == null && geo.isIndependent()) {
                        p.translate(v);                     
                        geo.updateRepaint();
                        ret[0] = geo;
                } else {
                    ret = kernel.Translate(label, p, v);                 
                }
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
	
	public CmdRotate(AlgebraController algCtrl) {
		super(algCtrl);
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
                GeoElement geo = p.toGeoElement();
                if (label == null && geo.isIndependent()) {
                        p.rotate(phi);
                        geo.updateRepaint();
                        ret[0] = geo;
                } else {
                    ret = kernel.Rotate(label, p, phi);
                }
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
                GeoElement geo = p.toGeoElement();
                if (label == null && geo.isIndependent()) {
                        p.rotate(phi, Q);
                        geo.updateRepaint();
                        ret[0] = geo;
                } else {
                    ret = kernel.Rotate(label, p, phi, Q);
                }
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
	
	public CmdDilate(AlgebraController algCtrl) {
		super(algCtrl);
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
                GeoElement geo = p.toGeoElement();
                if (label == null && geo.isIndependent()) {
                        p.dilate(phi, Q);
                        geo.updateRepaint();
                        ret[0] = geo;
                } else {
                    ret = kernel.Dilate(label, p, phi, Q);
                }
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
	
	public CmdMirror(AlgebraController algCtrl) {
		super(algCtrl);
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

            // mirror object
            if (ok[0] = (arg[0] instanceof Mirrorable)) {
            	Mirrorable p = (Mirrorable) arg[0];
            	GeoElement geo = p.toGeoElement();
            	
            	 // mirror at point
            	if (ok[1] = (arg[1] .isGeoPoint())) {	                	
                    GeoPoint Q = (GeoPoint) arg[1];	                    
                    if (label == null && geo.isIndependent()) {
                        p.mirror(Q);
                        geo.updateRepaint();
                        ret[0] = geo;
                    } else {
                        ret = kernel.Mirror(label, p, Q);
                    }
                    return ret;
            	} 
            	 // mirror is line
            	else if (ok[1] = (arg[1] .isGeoLine())) {
                    GeoLine line = (GeoLine) arg[1];
                    if (label == null && geo.isIndependent()) {
	                    p.mirror(line);
	                    geo.updateRepaint();
	                    ret[0] = geo;
                    } else {
                        ret = kernel.Mirror(label, p, line);
                    }
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
	
	public CmdRelation(AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdDelete(AlgebraController algCtrl) {
		super(algCtrl);
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
 */
class CmdDerivative extends CommandProcessor {
	
	public CmdDerivative (AlgebraController algCtrl) {
		super(algCtrl);
	}
	
final public   GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    String label = c.getLabel();
    GeoElement[] arg = resArgs(c);

    
    switch (n) {
        case 1 :
           
            if (arg[0].isGeoFunctionable()) {
                GeoFunction f = ((GeoFunctionable) arg[0]).getGeoFunction();
                if (label == null && f.isLabelSet())
                    label = f.getLabel() + "'";
                GeoElement[] ret = { kernel.Derivative(label, f)};
                return ret;
            } else
				throw argErr(app, "Derivative", arg[0]);

        case 2 :                
            if (arg[0].isGeoFunctionable()
                && arg[1] .isNumberValue()) {
                double order = ((NumberValue) arg[1]).getDouble();
                if (order >= 0) {
                    int iorder = (int) Math.round(order);
                    GeoFunction f = ((GeoFunctionable) arg[0]).getGeoFunction();
                    if (label == null && f.isLabelSet()) {
                        label = f.getLabel();
                        for (int i = 0; i < iorder; i++)
                            label = label + "'";
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
            } else {
                argErr(app, "Derivative", arg[0]);
            }

        default :
            throw argNumErr(app, "Derivative", n);
    }
}
}

/*
 * Integral[ <GeoFunction> ] Integral[ <GeoFunction>, <Number a>, <Number b> ]
 * Integral[ <GeoFunction f>, <GeoFunction g>, <Number a>, <Number b> ]
 */
class CmdIntegral extends CommandProcessor {
	
	public CmdIntegral (AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdUpperSum (AlgebraController algCtrl) {
		super(algCtrl);
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
 * LowerSum[ <GeoFunction>, <Number>, <Number>, <Number> ]
 */
class CmdLowerSum extends CommandProcessor {
	
	public CmdLowerSum (AlgebraController algCtrl) {
		super(algCtrl);
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
 * Polynomial[ <GeoFunction> ]
 */
class CmdPolynomial extends CommandProcessor {
	
	public CmdPolynomial (AlgebraController algCtrl) {
		super(algCtrl);
	}
	
final public GeoElement[] process(Command c) throws MyError {
     int n = c.getArgumentNumber();
     boolean[] ok = new boolean[n];
     GeoElement[] arg;

     switch (n) {
         case 1 :
             arg = resArgs(c);
             if (ok[0] = (arg[0] .isGeoFunctionable())) {
                 GeoElement[] ret =
                     {
                          kernel.PolynomialFunction(
                             c.getLabel(),
                             ((GeoFunctionable) arg[0]).getGeoFunction())};
                 return ret;
             } else
				throw argErr(app, c.getName(), arg[0]);

         default :
             throw argNumErr(app, c.getName(), n);
     }
 }    
}

/*
 * TaylorSeries[ <GeoFunction>, <Number>, <Number> ]
 */
class CmdTaylorSeries extends CommandProcessor {
	
	public CmdTaylorSeries (AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdRoot (AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdExtremum (AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdTurningPoint (AlgebraController algCtrl) {
		super(algCtrl);
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
	
	public CmdFunction (AlgebraController algCtrl) {
		super(algCtrl);
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
 * Point[ <Path> ] Point[ <Point>, <Vector> ]
 */
class CmdPoint extends CommandProcessor {
	
	public CmdPoint (AlgebraController algCtrl) {
		super(algCtrl);
	}
	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 1 :
            arg = resArgs(c);
            if (ok[0] = (arg[0] .isPath())) {
                GeoElement[] ret =
                    { kernel.Point(c.getLabel(), (Path) arg[0], 0, 0)};
                return ret;
            } else
				throw argErr(app, "Point", arg[0]);

        case 2 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoVector()))) {
                GeoElement[] ret =
                    {
                         kernel.Point(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoVector) arg[1])};
                return ret;
            } else {                
                if (!ok[0])
                    throw argErr(app, "Point", arg[0]);     
                else
                    throw argErr(app, "Point", arg[2]);
            }

        default :
            throw argNumErr(app, "Point", n);
    }
}
}

/*
 * Centroid[ <Polygon> ]
 */
class CmdCentroid extends CommandProcessor {
	
	public CmdCentroid (AlgebraController algCtrl) {
		super(algCtrl);
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
 * Corner[ <Image> ]
 */
class CmdCorner extends CommandProcessor {
	
	public CmdCorner (AlgebraController algCtrl) {
		super(algCtrl);
	}
	
final public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoImage())) &&
            	(ok[1] = (arg[1] .isNumberValue()))) {
                GeoElement[] ret =
                    { kernel.Corner(c.getLabel(), (GeoImage) arg[0], (NumberValue) arg[1])};
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
 * If[ <GeoBoolean>, <GeoElement> ]
 * If[ <GeoBoolean>, <GeoElement>, <GeoElement> ]
 */
class CmdIf extends CommandProcessor {
	
	public CmdIf(AlgebraController algCtrl) {
		super(algCtrl);
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
                else if (ok[0] = (arg[0].isGeoFunction())) {
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


