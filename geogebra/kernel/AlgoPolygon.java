/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
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
    private GeoSegment [] segments;   
    
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
          
        // TODO: implement polygon for lists
        if (geoList != null) {
        //	updatePointArray();
        }
                
        poly = new GeoPolygon(cons, points);                       
        createSegments();        
        poly.setSegments(segments);
        
        setInputOutput(); // for AlgoElement
      
        // compute angle
        compute();          
        
        setLabels(labels);
    }   
        
    String getClassName() {
        return "AlgoPolygon";
    }
    
    
    // for AlgoElement
    void setInputOutput() {
        input = points;
        
        int size = 1 + points.length;
        output = new GeoElement[size];                       
        output[0] = poly;
        for (int i=0; i < segments.length; i++) {
            output[i+1] = segments[i];
        }
        
        // set dependencies
        for (int i = 0; i < input.length; i++) {
            input[i].addAlgorithm(this);
        }
        
        // parent of output
        poly.setParentAlgorithm(this);       
        cons.addToAlgorithmList(this); 
    }    
    
    void update() {
        // compute output from input
        compute();
        output[0].update();
    }
    
    private void createSegments() {             
        segments = new GeoSegment[points.length];   
        AlgoJoinPointsSegment []  segmentAlgos = 
                        new AlgoJoinPointsSegment[points.length];
        for (int i=0; i < points.length; i++) {
            segmentAlgos[i] = new AlgoJoinPointsSegment(cons, poly, points[i], 
                                                                points[(i+1) % points.length]);            
            cons.removeFromConstructionList(segmentAlgos[i]);                       
            segments[i] = segmentAlgos[i].getSegment();
        }
    }
    
    private void setLabels(String [] labels) {
        // additional labels for the polygon's segments
        if (labels != null && labels.length == segments.length + 1) {
            for (int i=0; i < segments.length; i++) {
                segments[i].setLabel(labels[i+1]);
            }
        } else { // no labels for segments specified
            //  set labels of segments according to point names
             if (points.length == 3) {          
                setLabel(segments[0], points[2]);
                setLabel(segments[1], points[0]);
                setLabel(segments[2], points[1]); 
             } else {
                for (int i=0; i < points.length; i++) {
                    setLabel(segments[i], points[i]);
                }
             }
        }
        
        // label polygon
        if (labels == null || labels.length == 0) {
           poly.setLabel(null);
        } else {
            // first label for polygon itself
            poly.setLabel(labels[0]);
        }
    }
    
    private void setLabel(GeoSegment s, GeoPoint p) {
        if (!p.isLabelSet() || p.getLabel() == null) s.setLabel(null);
        else s.setLabel(p.getLabel().toLowerCase());
    }
    
    GeoPolygon getPoly() { return poly; }    
        
    final void compute() {      
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
