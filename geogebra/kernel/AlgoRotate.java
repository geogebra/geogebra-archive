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
public class AlgoRotate extends AlgoTransformation {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Rotateable B;    
    private NumberValue angle; 
    private GeoElement Ageo, Bgeo, angleGeo;
    /**
     * Creates new generic rotation algo
     * @param cons
     * @param label
     * @param A
     * @param angle
     */
    AlgoRotate(Construction cons, String label,
            GeoElement A, NumberValue angle) {
    	this(cons, A, angle);
    	Bgeo.setLabel(label);
    }
    
    /**
     * Creates new unlabeled rotation algo
     * @param cons
     * @param A
     * @param angle
     */
    AlgoRotate(Construction cons, GeoElement A, NumberValue angle) {
        super(cons);        
        this.angle = angle;

        angleGeo = angle.toGeoElement();
        Ageo = A;
        
        
        // create output object
        if(A instanceof GeoPolygon || A instanceof GeoPolyLine){
	        Bgeo = ((GeoPolygon)Ageo).copyInternal(cons);
	        B = (Rotateable) Bgeo;
        }
        else if(A instanceof Rotateable){
	        Bgeo = Ageo.copy();
	        B = (Rotateable) Bgeo;
        }
        else if(A instanceof GeoFunction){
        	Bgeo = new GeoCurveCartesian(cons);
            B = (Rotateable) Bgeo;	
        }
        else if(A.isGeoList()){
        	Bgeo = new GeoList(cons);
        }
        setInputOutput();
        
        cons.registerEuclidianViewAlgo(this);

        compute();        
    }

    public String getClassName() {
        return "AlgoRotate";
    }

    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ROTATE_BY_ANGLE;
    }
    
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = Ageo;
        input[1] = angle.toGeoElement();

        setOutputLength(1);
        setOutput(0,Bgeo);
        setDependencies(); // done by AlgoElement
    }

    /**
     * Returns the rotated object
     * @return rotated object
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
        B.rotate(angle);
    }
    
       
    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("ARotatedByAngleB",Ageo.getLabel(),angleGeo.getLabel());

    }    
}
