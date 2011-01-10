/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoRotatePoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;
import geogebra.kernel.arithmetic.NumberValue;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoRotatePoint extends AlgoTransformation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint Q;
    private PointRotateable B;    
    private NumberValue angle; 
    private GeoElement Ageo, Bgeo, angleGeo;
    
    /**
     * Creates new point rotation algo
     * @param cons
     * @param label
     * @param A
     * @param angle
     * @param Q
     */
    AlgoRotatePoint(Construction cons, String label,
            GeoElement A, NumberValue angle, GeoPoint Q) {
    	this(cons, A, angle, Q);
    	Bgeo.setLabel(label);
    }
    
    /**
     * Creates new unlabeled point rotation algo
     * @param cons
     * @param A
     * @param angle
     * @param Q
     */
    AlgoRotatePoint(Construction cons, 
    		GeoElement A, NumberValue angle, GeoPoint Q) {
        super(cons);               
        this.angle = angle;
        this.Q = Q;

        angleGeo = angle.toGeoElement();
        Ageo = A;
        
        if(A instanceof GeoPolygon){
	        Bgeo = ((GeoPolygon)Ageo).copyInternal(cons);
	        B = (PointRotateable) Bgeo;
        }
        else if(A instanceof Rotateable){	    
	        Bgeo = Ageo.copy();
	        B = (PointRotateable) Bgeo;
        }
        else if(A instanceof GeoFunction){        	
            Bgeo = new GeoCurveCartesian(cons);
            B = (PointRotateable) Bgeo;	
        }
        else if(A.isGeoList()){        	
        	Bgeo = new GeoList(cons);
        }        
        
        setInputOutput();

        cons.registerEuclidianViewAlgo(this);
        
        compute();
      
    }

    public String getClassName() {
        return "AlgoRotatePoint";
    }


    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ROTATE_BY_ANGLE;
    }

    
    /**
     * Returns true iff euclidian view updte is needed (for images)
     * @return true iff euclidian view updte is needed 
     */
    final public boolean wantsEuclidianViewUpdate() {
        return Ageo.isGeoImage();
    }
    
    // for AlgoElement
    protected void setInputOutput() {    	
        input = new GeoElement[3];
        input[0] = Ageo;
        input[1] = angleGeo;
        input[2] = Q;

        setOutputLength(1);
        setOutput(0,Bgeo);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Returns the rotated point
     * @return rotated point
     */
    GeoElement getResult() {
        return Bgeo;
    }

    // calc rotated point
    protected final void compute() {
    	if(Ageo.isGeoList()){
    		return;
    	}
    	if(Ageo instanceof GeoFunction){
    		((GeoFunction)Ageo).toGeoCurveCartesian((GeoCurveCartesian)Bgeo);
    	}
    	else Bgeo.set(Ageo);
        B.rotate(angle, Q);
    }
       
    final public String toString() {
        // Michael Borcherds 2008-03-25
        // simplified to allow better Chinese translation
        return app.getPlain("ARotatedByAngleB",Ageo.getLabel(),angleGeo.getLabel());

    }
}
