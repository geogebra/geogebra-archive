/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

/*
 * Relation.java
 *
 * Created on 12. Dezember 2001, 12:37
 */

package geogebra.kernel;

import geogebra.Application;
import geogebra.kernel.arithmetic.NumberValue;

/**
 *
 * @author  markus
 * @version 
 */
public class Relation extends java.lang.Object {

    private Application app;
    private Kernel kernel;
    private Construction cons;

    public Relation(Kernel kernel) {
        this.kernel = kernel;
        app = kernel.getApplication();
        cons = kernel.getConstruction();
    }

    /**
     * description of the relation between two GeoElements a, b
     * (equal, incident, intersect, parallel, linear dependent, 
     * tangent of, ...)
     */
    final public String relation(GeoElement a, GeoElement b) {
        // chek defined state
        if (!a.isDefined()) {
            StringBuffer sb = new StringBuffer();
            sb.append(a.getNameDescription());
            sb.append(' ');
            sb.append(app.getPlain("isNotDefined"));
            return sb.toString();
        } else if (!b.isDefined()) {
            StringBuffer sb = new StringBuffer();
            sb.append(b.getNameDescription());
            sb.append(' ');
            sb.append(app.getPlain("isNotDefined"));
            return sb.toString();
        }

        // decide what relation method can be used                                

        // point, point
        if (a instanceof GeoPoint && b instanceof GeoPoint)
			return relation((GeoPoint) a, (GeoPoint) b);
		else if (a instanceof GeoVector && b instanceof GeoVector)
			return relation((GeoVector) a, (GeoVector) b);
		else if (a instanceof GeoSegment && b instanceof GeoSegment)
			return relation((GeoSegment) a, (GeoSegment) b);
		else if (a instanceof GeoLine && b instanceof GeoLine)
			return relation((GeoLine) a, (GeoLine) b);
		else if (a instanceof GeoConicPart && b instanceof GeoConicPart)
			return relation((GeoConicPart) a, (GeoConicPart) b);
		else if (a instanceof GeoConic && b instanceof GeoConic)
			return relation((GeoConic) a, (GeoConic) b);
		else if (a instanceof GeoPoint && b instanceof Path)
			return relation((GeoPoint) a, (Path) b);
		else if (a instanceof Path && b instanceof GeoPoint)
			return relation((GeoPoint) b, (Path) a);
		else if (a instanceof GeoConic && b instanceof GeoLine)
			return relation((GeoLine) b, (GeoConic) a);
		else if (a instanceof GeoLine && b instanceof GeoConic)
			return relation((GeoLine) a, (GeoConic) b);
		else if (a instanceof NumberValue && b instanceof NumberValue)
			return relation((NumberValue) a, (NumberValue) b);
		else {
            StringBuffer sb = new StringBuffer();
            sb.append(a.getNameDescription());
            sb.append(' ');
            sb.append(app.getPlain("and"));
            sb.append(' ');
            sb.append(b.getNameDescription());
            sb.append(' ');
            sb.append(app.getPlain("comparedCannot"));
            return sb.toString();
        }
    }

    /**
     * description of the relation between two numbers a, b
     * (equal, unequal)
     */
    final private String relation(NumberValue a, NumberValue b) {
        kernel.setMinPrecision();
        String str = equalityString(a.toGeoElement(), b.toGeoElement(), 
        							kernel.isEqual(a.getDouble(), b.getDouble()));
        kernel.resetPrecision();
        return str;
    }

    /**
     * description of the relation between segment a and segment b
     * (equal, unequal)
     */
    final private String relation(GeoSegment a, GeoSegment b) {
        kernel.setMinPrecision();
        StringBuffer sb = new StringBuffer();
        sb.append(equalityString(a, b, a.equals(b)));
        sb.append("\n");
        sb.append(app.getPlain("Length"));
        sb.append(": ");
        sb.append(relation((NumberValue) a, (NumberValue) b));
        kernel.resetPrecision();
        return sb.toString();
    }

   

    /**
     * description of the relation between two points A, B
     * (equal, unequal)
     */
    final private String relation(GeoPoint A, GeoPoint B) {
        kernel.setMinPrecision();
        String str = equalityString(A, B, A.equals(B));
        kernel.resetPrecision();
        return str;
    }

    /**
     * description of the relation between two vectors a, b
     * (equal, linear dependent, linear independent)
     */
    final private String relation(GeoVector a, GeoVector b) {
        String str;
        kernel.setMinPrecision();
        if (a.equals(b)) {
            str = equalityString(a, b, true);
        } else {
            str = linDependencyString(a, b, a.linDep(b));
        }
        kernel.resetPrecision();
        return str;
    }

    /**
     * description of the relation between point A and a path
     * (incident, not incident)
     */
    final private String relation(GeoPoint A, Path path) {
    	return incidenceString(A, path.toGeoElement(), path.isOnPath(A, Kernel.MIN_PRECISION));   
    }

    /**
     * description of the relation between lines g and h
     * (equal, parallel or intersecting)
     */
    final private String relation(GeoLine g, GeoLine h) {
        String str;
        kernel.setMinPrecision();
        // check for equality
        if (g.equals(h)) {
            str = equalityString(g, h, true);
        } else {
            if (g.isParallel(h))
                str = parallelString(g, h);
            else {
            	// check if intersection point really lies on both objects (e.g. segments)
            	GeoPoint tempPoint = new GeoPoint(g.cons);
            	GeoVec3D.cross(g, h, tempPoint);
            	boolean isIntersection = g.isIntersectionPointIncident(tempPoint, Kernel.MIN_PRECISION)
					&& h.isIntersectionPointIncident(tempPoint, Kernel.MIN_PRECISION);
            	
                str = intersectString(g, h, isIntersection);
            }
        }
        kernel.resetPrecision();
        return str;
    }

    /**
     * description of the relation between line g and conic c
     * (intersection type: tangent, secant, ...)
     */
    final private String relation(GeoLine g, GeoConic c) {
        int type;
        String str;
        
        // limited paths have to handled differently (e.g. segments, arcs) 
        if (g.isLimitedPath() || c.isLimitedPath()) {
        	// intersect line and conic
            // precision setting is not needed here (done by algorithm)
            AlgoIntersectLineConic algo = new AlgoIntersectLineConic(cons, g, c);
            GeoPoint[] points = algo.getIntersectionPoints();
            cons.removeFromConstructionList(algo);

            // check for defined intersection points
            boolean intersect = false;
            for (int i = 0; i < points.length; i++) {
                if (points[i].isDefined()) {                	              
                    intersect = true;
                    break;
                }
            }
            // build relation string
            str = intersectString(g, c, intersect);
            
            // remove algorithm by removing one of its points
            points[0].remove();
            return str;
        }

        // is line defined as tangent or asymptote of c?
        if (g.isDefinedTangent(c)) {
            str =
                lineConicString(
                    g,
                    c,
                    AlgoIntersectLineConic.INTERSECTION_TANGENT_LINE);
        } else if (g.isDefinedAsymptote(c)) {
            str =
                lineConicString(
                    g,
                    c,
                    AlgoIntersectLineConic.INTERSECTION_ASYMPTOTIC_LINE);
        } else {
            // intersect line and conic 
            kernel.setMinPrecision();
            GeoPoint[] points = { new GeoPoint(cons), new GeoPoint(cons)};
            type = AlgoIntersectLineConic.intersectLineConic(g, c, points);           
            points = null;
            str = lineConicString(g, c, type);
            kernel.resetPrecision();
        }
        return str;
    }
    
    /**
     * description of the relation between conci parts a, b
     * (equal, intersecting or not intersecting)
     */
    final private String relation(GeoConicPart a, GeoConicPart b) {
    	kernel.setMinPrecision();
        StringBuffer sb = new StringBuffer();
        sb.append(equalityString(a, b, a.equals(b)));
                
        int type = a.getConicPartType();
        if (type == b.getConicPartType()) {
        	sb.append("\n");
	        if (type == GeoConicPart.CONIC_PART_ARC) {
	        	sb.append(app.getCommand("Length"));
	        } else {
	        	sb.append(app.getCommand("Area"));
	        }	        	
	        sb.append(": ");
	        sb.append(relation((NumberValue) a, (NumberValue) b));
        }
        
        kernel.resetPrecision();
        return sb.toString();
    }

    /**
     * description of the relation between conics a, b
     * (equal, intersecting or not intersecting)
     */
    final private String relation(GeoConic a, GeoConic b) {
        String str;

        if (a.equals(b)) {
            str = equalityString(a, b, true);
        } else {
            // intersect conics
            // precision setting is not needed here (done by algorithm)
            AlgoIntersectConics algo = new AlgoIntersectConics(cons, a, b);
            GeoPoint[] points = algo.getIntersectionPoints();
            cons.removeFromConstructionList(algo);

            // check for defined intersection points
            boolean intersect = false;
            for (int i = 0; i < points.length; i++) {
                if (points[i].isDefined()) {
                    intersect = true;
                    break;
                }
            }
            // build relation string
            str = intersectString(a, b, intersect);
            
            // remove algorithm by removing one of its points
            points[0].remove();
        }
        return str;
    }

    /***************************
     * private methods
     ***************************/

    // "Relation of a and b: equal"
    // "Relation of a and b: unequal"
    final private String equalityString(
        GeoElement a,
        GeoElement b,
        boolean equal) {
        StringBuffer sb = new StringBuffer();
        sb.append(a.getNameDescription());
        sb.append(' ');
        sb.append(app.getPlain("and"));
        sb.append(' ');
        sb.append(b.getNameDescription());
        sb.append(": ");
        if (equal)
            sb.append(app.getPlain("equal"));
        else
            sb.append(app.getPlain("unequal"));
        return sb.toString();
    }

    // "Relation of a and b: linear dependent"
    // "Relation of a and b: linear independent"
    final private String linDependencyString(
        GeoElement a,
        GeoElement b,
        boolean dependent) {
        StringBuffer sb = new StringBuffer();
        sb.append(a.getNameDescription());
        sb.append(' ');
        sb.append(app.getPlain("and"));
        sb.append(' ');
        sb.append(b.getNameDescription());
        sb.append(": ");
        if (dependent)
            sb.append(app.getPlain("linearDependent"));
        else
            sb.append(app.getPlain("linearIndependent"));
        return sb.toString();
    }

    // "a lies on b"
    // "a does not lie on b"
    final private String incidenceString(
        GeoPoint a,
        GeoElement b,
        boolean incident) {
        StringBuffer sb = new StringBuffer();
        sb.append(a.getNameDescription());
        sb.append(' ');
        if (incident)
            sb.append(app.getPlain("liesOn"));
        else
            sb.append(app.getPlain("liesOnNot"));
        sb.append(' ');
        sb.append(b.getNameDescription());
        return sb.toString();
    }

    // "Relation of a and b: parallel"    
    final private String parallelString(GeoLine a, GeoLine b) {
        StringBuffer sb = new StringBuffer();
        sb.append(a.getNameDescription());
        sb.append(' ');
        sb.append(app.getPlain("and"));
        sb.append(' ');
        sb.append(b.getNameDescription());
        sb.append(": ");
        sb.append(app.getPlain("parallel"));
        return sb.toString();
    }

    // "a intersects with b"
    final private String intersectString(
        GeoElement a,
        GeoElement b,
        boolean intersects) {
        StringBuffer sb = new StringBuffer();
        sb.append(a.getNameDescription());
        sb.append(' ');
        if (intersects)
            sb.append(app.getPlain("intersectsWith"));
        else
            sb.append(app.getPlain("intersectsWithNot"));
        sb.append(' ');
        sb.append(b.getNameDescription());
        return sb.toString();
    }

    // e.g "a is tangent of b"
    // types are defined in AlgoIntersectLineConic
    final private String lineConicString(GeoLine a, GeoConic b, int type) {
        StringBuffer sb = new StringBuffer();
        sb.append(a.getNameDescription());
        sb.append(' ');
        sb.append(app.getPlain("and"));
        sb.append(' ');
        sb.append(b.getNameDescription());
        sb.append(": ");
        
        String strType;
        switch (type) {
            case AlgoIntersectLineConic.INTERSECTION_PRODUCING_LINE :
                strType = app.getPlain("producingLine");
                break;

            case AlgoIntersectLineConic.INTERSECTION_ASYMPTOTIC_LINE :
                strType = app.getPlain("asymptoticLine");
                break;

            case AlgoIntersectLineConic.INTERSECTION_MEETING_LINE :
                strType = app.getPlain("meetingLine");
                break;

            case AlgoIntersectLineConic.INTERSECTION_TANGENT_LINE :
                strType = app.getPlain("tangentLine");
                break;

            case AlgoIntersectLineConic.INTERSECTION_SECANT_LINE :
                strType = app.getPlain("secantLine");
                break;

            default :
                //case AlgoIntersectLineConic.INTERSECTION_PASSING_LINE:
                strType = app.getPlain("passingLine");
                break;
        }

        sb.append(strType);
        return sb.toString();
    }

}
