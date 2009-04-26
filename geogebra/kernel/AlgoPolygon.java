/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.main.Application;



/**
 * Creates a Polygon from a given list of points or point array.
 * 
 * @author  Markus Hohenwarter
 * @version 
 */
public class AlgoPolygon extends AlgoElement {

	private static final long serialVersionUID = 1L;
	protected GeoPointInterface [] points;  // input
	private GeoList geoList;  // alternative input
    protected GeoPolygon poly;     // output
    
    /** /2D coord sys used for 3D */
    protected GeoElement cs2D; 
    
    /** polyhedron (when segment is part of), used for 3D */
    protected GeoElement polyhedron; 
    
    
    protected AlgoPolygon(Construction cons, String [] labels, GeoList geoList) {
    	this(cons, labels, null, geoList);
    }
    
    protected AlgoPolygon(Construction cons, String [] labels, GeoPointInterface [] points) {
    	this(cons, labels, points, null);
    }
 
    protected AlgoPolygon(Construction cons, String [] labels, GeoPointInterface [] points, GeoList geoList) {
    	this(cons,labels,points,geoList,null,true,null);
    }
    
    /**
     * @param cons the construction
     * @param labels names of the polygon and the segments
     * @param points vertices of the polygon
     * @param geoList list of vertices of the polygon (alternative to points)
     * @param cs2D for 3D stuff : GeoCoordSys2D
     * @param createSegments  says if the polygon has to creates its edges (3D only) 
     * @param polyhedron polyhedron (when segment is part of), used for 3D
     */
    protected AlgoPolygon(Construction cons, String [] labels, 
    		GeoPointInterface [] points, GeoList geoList, GeoElement cs2D, 
    		boolean createSegments, GeoElement polyhedron) {
        super(cons);
        this.points = points;           
        this.geoList = geoList;
        this.cs2D = cs2D;
        this.polyhedron = polyhedron;
          
        //poly = new GeoPolygon(cons, points);
        createPolygon(createSegments);  
        
        // compute polygon points
        compute();  
        
        setInputOutput(); // for AlgoElement                          
        poly.initLabels(labels);
    }   
    
    
    /**
     * create the polygon
     * @param createSegments says if the polygon has to creates its edges (3D only)
     */
    protected void createPolygon(boolean createSegments){
    	poly = new GeoPolygon(this.cons, this.points);
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
    protected void setInputOutput() {
    	if (geoList != null) {
    		// list as input
    		if (polyhedron==null){
    			input = new GeoElement[1];
    			input[0] = geoList;
    		}else{
    			input = new GeoElement[2];
    			input[0] = geoList;
    			input[1] = polyhedron;
    		}
    	} else {    	
    		// points as input
    		if (polyhedron==null)
    			input = (GeoElement[]) points;
    		else{
    			input = new GeoElement[points.length+1];
    			for(int i = 0; i < points.length; i++)
    				input[i]=(GeoElement) points[i];
    			input[points.length] = polyhedron;
    		}
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
    	GeoSegmentInterface [] segments = poly.getSegments();
    	int size = 1;
    	if (segments!=null)
    		size+=segments.length;
        output = new GeoElement[size];                       
        output[0] = poly;        
        for (int i=0; i < size-1; i++) {
            output[i+1] = (GeoElement) segments[i];
        }
    }
    
    void update() {
        // compute output from input
        compute();
        output[0].update();
    }
    
    
    public GeoPolygon getPoly() { return poly; }    
    public GeoPoint [] getPoints() {
    	return (GeoPoint[]) points;
    }
    
    
    
    public void remove() {
        super.remove();
        //if polygon is part of a polyhedron, remove it
        if (polyhedron != null)
            polyhedron.remove();
    }  
    
        
    protected final void compute() { 
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
