/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;



/**
 * Creates a Polygon from a given list of points or point array.
 * 
 * @author  Markus Hohenwarter
 * @version 
 */
public class AlgoPolygon extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoPoint [] points;  // input
	private GeoList geoList;  // alternative input
    private GeoPolygon poly;     // output
    
    AlgoPolygon(Construction cons, String [] labels, GeoList geoList) {
    	this(cons, labels, null, geoList);
    }
    
    AlgoPolygon(Construction cons, String [] labels, GeoPoint [] points) {
    	this(cons, labels, points, null);
    }
    	
    private AlgoPolygon(Construction cons, String [] labels, GeoPoint [] points, GeoList geoList) {
        super(cons);
        this.points = points;           
        this.geoList = geoList;
          
        poly = new GeoPolygon(cons, points);
             
        // compute polygon points
        compute();  
        
        setInputOutput(); // for AlgoElement                          
        poly.initLabels(labels);
    }   
        
    protected String getClassName() {
        return "AlgoPolygon";
    }
    
    /**
     * Update point array of polygon using the given array list
     * @param pointList
     */
    private void updatePointArray(GeoList pointList) {
    	// check if we have a point list
    	if (pointList.getElementType() != GeoElement.GEO_CLASS_POINT) {
    		poly.setUndefined();
    		return;
    	}
    	
    	// remember old number of points
    	int oldPointsLength = points == null ? 0 : points.length;
    	
    	// create new points array
    	int size = pointList.size();
    	points = new GeoPoint[size];
    	for (int i=0; i < size; i++) {    		
    		points[i] = (GeoPoint) pointList.get(i);
    	}
    	poly.setPoints(points);
    	
    	if (oldPointsLength != points.length)
    		setOutput();    	
    }
    
    
    // for AlgoElement
    void setInputOutput() {
    	if (geoList != null) {
    		// list as input
    		input = new GeoElement[1];
    		input[0] = geoList;
    	} else {    	
    		// points as input
    		input = points;
    	}    	
    	// set dependencies
        for (int i = 0; i < input.length; i++) {
            input[i].addAlgorithm(this);
        }
        
    	setOutput();

        // parent of output
        poly.setParentAlgorithm(this);       
        cons.addToAlgorithmList(this); 
    }    
    
    private void setOutput() {
    	GeoSegment [] segments = poly.getSegments();    	    	
        int size = 1 + segments.length;
        output = new GeoElement[size];                       
        output[0] = poly;        
        for (int i=0; i < segments.length; i++) {
            output[i+1] = segments[i];
        }
    }
    
    void update() {
        // compute output from input
        compute();
        output[0].update();
    }
    
    
    GeoPolygon getPoly() { return poly; }    
    public GeoPoint [] getPoints() {
    	return points;
    }
        
    final void compute() { 
    	if (geoList != null) {
    		updatePointArray(geoList);
    	}
    	
        // compute area
        poly.calcArea();                                 
    }   
    
    final public String toString() {
        StringBuffer sb = new StringBuffer();
  
        sb.append(app.getPlain("Polygon"));
        sb.append(' ');     
        int last = points.length - 1;       
        for (int i = 0; i < last; i++) {
            sb.append(points[i].getLabel());
            sb.append(", ");
        }
        sb.append(points[last].getLabel());        
        
        return  sb.toString();
    }
}
