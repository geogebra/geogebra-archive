/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/*
 * AlgoJoinPointsSegment
 *
 * Created on 21. August 2003
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoJoinPointsSegment extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint P, Q; // input
    private GeoSegment s; // output: GeoSegment subclasses GeoLine 

    private GeoPolygon poly; // for polygons         

    /** Creates new AlgoJoinPoints */
    AlgoJoinPointsSegment(
        Construction cons,
        String label,
        GeoPoint P,
        GeoPoint Q) {
        this(cons, P, Q);
        s.setLabel(label);
    }

    AlgoJoinPointsSegment(
        Construction cons,
        GeoPolygon poly,
        GeoPoint P,
        GeoPoint Q) {
        this(cons, P, Q);
        this.poly = poly;
    }

    private AlgoJoinPointsSegment(Construction cons, GeoPoint P, GeoPoint Q) {
        super(cons);
        this.P = P;
        this.Q = Q;
          
        s = new GeoSegment(cons, P, Q);                
        setInputOutput(); // for AlgoElement
               
        // compute line through P, Q
        compute();        
    }

    String getClassName() {
        return "AlgoJoinPointsSegment";
    }

    // for AlgoElement
    void setInputOutput() {    	  
        input = new GeoElement[2];
        input[0] = P;
        input[1] = Q;

        output = new GeoElement[1];
        output[0] = s;
          
        setDependencies();
    }

    GeoSegment getSegment() {
        return s;
    }
    GeoPoint getP() {
        return P;
    }
    GeoPoint getQ() {
        return Q;
    }

    // calc the line g through P and Q    
    final void compute() {
        // g = P v Q  <=>  g_n : n = P x Q
        // g = cross(P, Q)
    	GeoVec3D.lineThroughPoints(P, Q, s);            	
    	s.calcLength();
    }

    public void remove() {
        super.remove();
        if (poly != null)
            poly.remove();
    }

    public int getConstructionIndex() {
        if (poly != null)
			return poly.getConstructionIndex();
		else
			return super.getConstructionIndex();
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();

        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getCommand("Segment"));
            sb.append('[');
            sb.append(P.getLabel());
            sb.append(", ");
            sb.append(Q.getLabel());
            sb.append(']');
            if (poly != null) {
                sb.append(' ');
                sb.append(app.getPlain("of"));
                sb.append(' ');
                sb.append(poly.getNameDescription());
            }
        } else {
            if (poly != null) {
                sb.append(poly.getNameDescription());
                sb.append(' ');
                sb.append(app.getPlain("of"));
                sb.append(' ');
            }
            sb.append(app.getCommand("Segment"));
            sb.append('[');
            sb.append(P.getLabel());
            sb.append(", ");
            sb.append(Q.getLabel());
            sb.append(']');
        }
        return sb.toString();
    }
}
