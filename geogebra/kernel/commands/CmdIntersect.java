package geogebra.kernel.commands;

import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoFunctionable;
import geogebra.kernel.GeoImplicitPoly;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.MyError;


/*
 * Intersect[ <GeoLine>, <GeoLine> ] Intersect[ <GeoLine>, <GeoPolygon> ] 
 * Intersect[ <GeoLine>, <GeoConic> ]
 * Intersect[ <GeoConic>, <GeoLine> ] Intersect[ <GeoConic>, <GeoConic> ]
 * Intersect[ <GeoFunction>, <GeoFunction> ] Intersect[ <GeoFunction>, <GeoLine> ]
 * Intersect[ <GeoImplicitPoly>, <GeoImplicitPoly> ] Intersect[ <GeoImplicitPoly>, <GeoLine> ]
 * Intersect[ <GeoImplicitPoly>, <GeoFunction(Polynomial)> ]
 */
public class CmdIntersect extends CommandProcessor {
	
	public CmdIntersect(Kernel kernel) {
		super(kernel);
	}
	
public  GeoElement[] process(Command c) throws MyError {
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
            // Line - Polygon
            else if ((ok[0] = (arg[0] .isGeoLine()))
                && (ok[1] = (arg[1] .isGeoPolygon()))) {
                GeoElement[] ret =
                         kernel.IntersectLinePolygon(
                            c.getLabels(),
                            (GeoLine) arg[0],
                            (GeoPolygon) arg[1]);
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
         // Polynomial - Conic
            else if (
                (ok[0] = (arg[0] .isGeoFunction()))
                    && (ok[1] = (arg[1] .isGeoConic())))
				return kernel.IntersectPolynomialConic(
                    c.getLabels(),
                    (GeoFunction) arg[0],
                    (GeoConic) arg[1]);
			else if (
                (ok[0] = (arg[0] .isGeoConic()))
                    && (ok[1] = (arg[1] .isGeoFunction())))
				return kernel.IntersectPolynomialConic(
                    c.getLabels(),
                    (GeoFunction) arg[1],
                    (GeoConic) arg[0]);
            // Line - Cubic
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
            //implicit Poly - Polynomial
			else if (
	                (ok[0] = (arg[0] .isGeoImplicitPoly()))
	                    && (ok[1] = (arg[1] .isGeoFunctionable())
	                    && (ok[1]=((GeoFunctionable) arg[1]).getGeoFunction().isPolynomialFunction(false))))
					return kernel.IntersectImplicitpolyPolynomial(
	                    c.getLabels(), (GeoImplicitPoly) arg[0],
	                    ((GeoFunctionable) arg[1]).getGeoFunction()
	                   );
			else if (ok[0] = (arg[0] .isGeoFunctionable())
	                    && (ok[0]=((GeoFunctionable) arg[0]).getGeoFunction().isPolynomialFunction(false))
	                    && (ok[1] = (arg[1] .isGeoImplicitPoly())))
					return kernel.IntersectImplicitpolyPolynomial(
	                    c.getLabels(), (GeoImplicitPoly) arg[1],
	                    ((GeoFunctionable) arg[0]).getGeoFunction()
	                   );
            //implicitPoly - Line
			else if (
	                (ok[0] = (arg[0] .isGeoImplicitPoly()))
	                    && (ok[1] = (arg[1].isGeoLine())))
					return kernel.IntersectImplicitpolyLine(
	                    c.getLabels(), (GeoImplicitPoly) arg[0],
	                    (GeoLine) arg[1] );
			else if (
	                (ok[1] = (arg[1] .isGeoImplicitPoly()))
	                    && (ok[0] = (arg[0].isGeoLine())))
					return kernel.IntersectImplicitpolyLine(
	                    c.getLabels(), (GeoImplicitPoly) arg[1],
	                    (GeoLine) arg[0] );
            //implicitPoly - implicitPoly
			else if (
	                (ok[0] = (arg[0] .isGeoImplicitPoly()))
	                    && (ok[1] = (arg[1].isGeoImplicitPoly())))
					return kernel.IntersectImplicitpolys(
	                    c.getLabels(), (GeoImplicitPoly) arg[0],
	                    (GeoImplicitPoly) arg[1] );
            //implicitPoly-conic
			else if (
	                (ok[0] = (arg[0] .isGeoImplicitPoly()))
                    && (ok[1] = (arg[1].isGeoConic())))
				return kernel.IntersectImplicitpolyConic(
                    c.getLabels(), (GeoImplicitPoly) arg[0],
                    (GeoConic) arg[1] );
			else if (
	                (ok[1] = (arg[1] .isGeoImplicitPoly()))
                    && (ok[0] = (arg[0].isGeoConic())))
				return kernel.IntersectImplicitpolyConic(
                    c.getLabels(), (GeoImplicitPoly) arg[1],
                    (GeoConic) arg[0] );
			// intersection of two lists
			else if (arg[0].isGeoList() && arg[1].isGeoList() ) {
				GeoElement[] ret = { 
						kernel.Intersection(c.getLabel(),
						(GeoList) arg[0], (GeoList)arg[1] ) };
				return ret;
			} 
            
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
            //Polynomial - Conic with index of point
            else if (
                    (ok[0] = (arg[0] .isGeoFunction()))
                        && (ok[1] = (arg[1] .isGeoConic()))
                        && (ok[2] = (arg[2] .isNumberValue())) )
    				return new GeoElement[]{kernel.IntersectPolynomialConicSingle(
                        c.getLabel(),
                        (GeoFunction) arg[0],
                        (GeoConic) arg[1],(NumberValue)arg[2])};
    		else if (
                    (ok[0] = (arg[0] .isGeoConic()))
                        && (ok[1] = (arg[1] .isGeoFunction()))
                        && (ok[2] = (arg[2] .isNumberValue())))
    				return new GeoElement[]{kernel.IntersectPolynomialConicSingle(
                        c.getLabel(),
                        (GeoFunction) arg[1],
                        (GeoConic) arg[0],(NumberValue)arg[2])};
            //ImplicitPoly - Functionable
    		else if (
	                (ok[0] = (arg[0] .isGeoImplicitPoly()))
	                    && (ok[1] = (arg[1] .isGeoFunctionable())
	                    && (ok[1]=((GeoFunctionable) arg[1]).getGeoFunction().isPolynomialFunction(false)))
	                    && (ok[2] = (arg[2] .isNumberValue())))
					return new GeoElement[]{kernel.IntersectImplicitpolyPolynomialSingle(
	                    c.getLabel(), (GeoImplicitPoly) arg[0],
	                    ((GeoFunctionable) arg[1]).getGeoFunction(),(NumberValue)arg[2]
	                   )};
			else if (ok[0] = (arg[0] .isGeoFunctionable())
	                    && (ok[0]=((GeoFunctionable) arg[0]).getGeoFunction().isPolynomialFunction(false))
	                    && (ok[1] = (arg[1] .isGeoImplicitPoly()))
	                    && (ok[2] = (arg[2] .isNumberValue())))
						return new GeoElement[]{kernel.IntersectImplicitpolyPolynomialSingle(
			                    c.getLabel(), (GeoImplicitPoly) arg[0],
			                    ((GeoFunctionable) arg[1]).getGeoFunction(),(NumberValue)arg[2]
			                   )};
          //implicitPoly - Line
			else if (
	                (ok[0] = (arg[0] .isGeoImplicitPoly()))
	                    && (ok[1] = (arg[1].isGeoLine()))
	                    && (ok[2] = (arg[2] .isNumberValue())) )
					return new GeoElement[]{kernel.IntersectImplicitpolyLineSingle(
	                    c.getLabel(), (GeoImplicitPoly) arg[0],
	                    (GeoLine) arg[1] ,(NumberValue)arg[2])};
			else if (
	                (ok[1] = (arg[1] .isGeoImplicitPoly()))
	                    && (ok[0] = (arg[0].isGeoLine()))
	                    && (ok[2] = (arg[2] .isNumberValue())))
				return new GeoElement[]{kernel.IntersectImplicitpolyLineSingle(
	                    c.getLabel(), (GeoImplicitPoly) arg[1],
	                    (GeoLine) arg[0] ,(NumberValue)arg[2])};
          //implicitPoly - implicitPoly
			else if (
	                (ok[0] = (arg[0] .isGeoImplicitPoly()))
	                    && (ok[1] = (arg[1].isGeoImplicitPoly()))
	                    && (ok[2]=arg[2].isNumberValue()))
					return new GeoElement[]{kernel.IntersectImplicitpolysSingle(
	                    c.getLabel(), (GeoImplicitPoly) arg[0],
	                    (GeoImplicitPoly) arg[1], (NumberValue) arg[2] )};
            //implicitPoly-conic
			else if (
	                (ok[0] = (arg[0] .isGeoImplicitPoly()))
                    && (ok[1] = (arg[1].isGeoConic()))
                    && (ok[2]=arg[2].isNumberValue()))
				return new GeoElement[]{kernel.IntersectImplicitpolyConicSingle(
                    c.getLabel(), (GeoImplicitPoly) arg[0],
                    (GeoConic) arg[1], (NumberValue) arg[2] )};
			else if (
	                (ok[1] = (arg[1] .isGeoImplicitPoly()))
                    && (ok[0] = (arg[0].isGeoConic()))
                    && (ok[2]=arg[2].isNumberValue()))
				return new GeoElement[]{kernel.IntersectImplicitpolyConicSingle(
                    c.getLabel(), (GeoImplicitPoly) arg[1],
                    (GeoConic) arg[0], (NumberValue) arg[2] )};
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