/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoIntersectLines.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.Matrix.Coords;
import geogebra.euclidian.EuclidianConstants;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;

import java.util.TreeMap;


/**
 * Algo for intersection of a line with a polygon
 * @author  matthieu
 * @version 
 */
public class AlgoIntersectLinePolygon extends AlgoElement{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private GeoLineND g; // input
	private GeoPolygon p; //input
	protected OutputHandler<GeoElement> outputPoints; // output   
    
    
    private TreeMap<Double, Coords> newCoords;
    
    
    /** 
     * common constructor
     * @param c 
     * @param labels
     * @param g
     * @param p
     */
    protected AlgoIntersectLinePolygon(Construction c, String[] labels, GeoLineND g, GeoPolygon p) {
        super(c);
        
		outputPoints=createOutputPoints();
        
        
        this.g = g;
        this.p = p;

        newCoords = new TreeMap<Double, Coords>(Kernel.DoubleComparator(Kernel.STANDARD_PRECISION));
        
        compute();
        
        setInputOutput(); // for AlgoElement

        
        //if only one label (e.g. "A") for more than one output, new labels will be A_1, A_2, ...
        if (labels!=null &&
        		labels.length==1 &&
        		outputPoints.size() > 1 &&
        		labels[0]!=null &&
        		!labels[0].equals(""))
        	outputPoints.setIndexLabels(labels[0]);
        else
        	outputPoints.setLabels(labels);
        				
        update();    
    }
    
    /**
     * 
     * @return handler for output points
     */
    protected OutputHandler<GeoElement> createOutputPoints(){
    	return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			public GeoPoint newElement() {
				GeoPoint p=new GeoPoint(cons);
				p.setCoords(0, 0, 1);
				p.setParentAlgorithm(AlgoIntersectLinePolygon.this);
				return p;
			}
		});
    }
    
    
    public String getClassName() {
        return "AlgoIntersectLinePolygon";
    }

    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECT;
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = (GeoElement) g;
        input[1] = p;
        
        setDependencies(); // done by AlgoElement
    }


    protected void intersectionsCoords(GeoLineND g, GeoPolygon p, TreeMap<Double, Coords> newCoords){
    	
    	double min = g.getMinParameter();
    	double max = g.getMaxParameter();
    	
    	for(int i=0; i<p.getSegments().length; i++){
    		GeoSegment seg = (GeoSegment) p.getSegments()[i];
    		Coords coords = GeoVec3D.cross((GeoLine) g, seg);
    		if (seg.respectLimitedPath(coords, Kernel.MIN_PRECISION)){
       			double t = ((GeoLine) g).getPossibleParameter(coords);
    			//Application.debug("parameter("+i+") : "+t);
       			if (t>=min && t<=max)//TODO optimize that
       				newCoords.put(t, coords);
    		}
        }
    }
    

    
    protected final void compute() {  
    	
    	//clear the points map
    	newCoords.clear();
    	
    	//fill a new points map
    	intersectionsCoords(g, p, newCoords);
    	
    	//update and/or create points
    	int index = 0;   	
    	//affect new computed points
    	outputPoints.adjustOutputSize(newCoords.size());
    	for (Coords coords : newCoords.values()){
    		GeoPointND point = (GeoPointND) outputPoints.getElement(index);
    		point.setCoords(coords,false);
    		point.updateCoords();
    		index++;
    	}
    	//other points are undefined
    	for(;index<outputPoints.size();index++)
    		outputPoints.getElement(index).setUndefined();
    	
 
    	
    }

    final public String toString() {
        return app.getPlain("IntersectionPointOfAB",((GeoElement) g).getLabel(),p.getLabel());
    }
    
      


    
    
    
}
