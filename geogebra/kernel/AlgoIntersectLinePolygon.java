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
import geogebra.kernel.kernelND.GeoSegmentND;

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
	
	
	protected GeoLineND g; // input
	protected GeoPolygon p; //input
	protected OutputHandler<GeoElement> outputPoints; // output
	protected OutputHandler<GeoElement> outputSegments; // output 
    
    
    private TreeMap<Double, Coords> newCoords;
    private TreeMap<Double, Coords[]> newSegmentCoords;


	protected boolean pAsBoundary;
    
    /** 
     * common constructor
     * @param c 
     * @param labels
     * @param g
     * @param p
     */
    protected AlgoIntersectLinePolygon(Construction c, String[] labels, GeoLineND g, GeoPolygon p) {
        this(c, labels, g, p, true);    }
    
    public AlgoIntersectLinePolygon(Construction c, String[] labels,
			GeoLineND g, GeoPolygon p, boolean asBoundary) {

    	super(c);
        
		outputPoints=createOutputPoints();
		outputSegments = createOutputSegments();
        pAsBoundary = asBoundary;
        
        this.g = g;
        this.p = p;

        newCoords = new TreeMap<Double, Coords>(Kernel.DoubleComparator(Kernel.STANDARD_PRECISION));
        newSegmentCoords = new TreeMap<Double, Coords[]>(Kernel.DoubleComparator(Kernel.STANDARD_PRECISION));
        
        compute();
        
        setInputOutput(); // for AlgoElement

        
        //if only one label (e.g. "A") for more than one output, new labels will be A_1, A_2, ...
        if (labels!=null &&
        		labels.length==1 &&
        		outputPoints.size() > 1 &&
        		labels[0]!=null &&
        		!labels[0].equals("")) {
        	outputPoints.setIndexLabels(labels[0]);
        	outputSegments.setIndexLabels(labels[0]);
        } else {
        	outputPoints.setLabels(labels);
        	if ( outputPoints.size()==0 || outputSegments.size()!=0) //when there is a point, an 'empty segment' is not needed.
        		outputSegments.setLabels(labels);
        }
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
    
    protected OutputHandler<GeoElement> createOutputSegments(){
    	return new OutputHandler<GeoElement>(new elementFactory<GeoElement>() {
			public GeoSegment newElement() {
				GeoSegment a=new GeoSegment(cons);
				GeoPoint aS = new GeoPoint(cons);
				aS.setCoords(0, 0, 1);
				GeoPoint aE = new GeoPoint(cons);
				aE.setCoords(0, 0, 1);
				a.setPoints(aS, aE);
				a.setParentAlgorithm(AlgoIntersectLinePolygon.this);
				return a;
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
    

    protected void intersectionsSegments(GeoLineND g, GeoPolygon p,
			TreeMap<Double, Coords> newCoords,
			TreeMap<Double, Coords[]> newSegmentCoords) {
		
    	
    	if (newCoords==null || newCoords.size()==0) {
    		newSegmentCoords.clear();
    		return;
    	}
    	/*
      	//coords of some point outside of p, e.g. (x=1+Max X coord of P, 0) 
    	double pMaxX = 0;
    	for (int i = 0; i< p.getPointsLength(); i++) {
    		pMaxX = Math.max(pMaxX, p.getPointX(i));
    		//pMaxY = Math.max(pMaxY, p.getPointY(i));
    	}
    	double pOutsideX = pMaxX + 1;
    	double pOutsideY = 0;
    	*/
    	
    	//naive traversing algorithm which assumes no special case
    	double s1 = g.getMinParameter();
    	double s2 = g.getMaxParameter(); 
       	double minKey = newCoords.firstKey();
    	double maxKey = newCoords.lastKey();
    	
    	double tLast;
   
   	
    	if (s1>=maxKey)
    		tLast = s1;
    	else
    		tLast = s2;
      	
    	int nextChangeOfRegion = 1; //assume that the first acquaintance of a newCoords is "-1:entry".
            	
    	PathParameter tempParam = new PathParameter(tLast);
      	Coords tempCoords = new Coords(0, 0, 1);
    	((GeoLine)g).doPointChanged(tempCoords, tempParam);

    	if (p.isInRegion(tempCoords.get(1),tempCoords.get(2)) && !Kernel.isEqual(tLast, maxKey)) {
    		newSegmentCoords.put(tLast, new Coords[] {tempCoords, newCoords.get(maxKey)});
    		nextChangeOfRegion = -1;
    	} 
    	
    	tLast = maxKey;
    	
    	while (tLast > minKey) {
    		double t = newCoords.subMap(minKey, tLast).lastKey();
    		if (nextChangeOfRegion == 1) {
    			newSegmentCoords.put(tLast,  new Coords[] {
    					newCoords.get(tLast), 
    					newCoords.get(t)
    					});
    			nextChangeOfRegion = -1;
    		} else if (nextChangeOfRegion == -1) {
    			nextChangeOfRegion = 1;
    		}
    		tLast = t;
    	}
 
    	
    	//traversing algorithm for dealing with degenerated case
   /* 	int regionChange = 1;  // 1: enter once; -1: quit once; 0: neither
    	int regionWaitingToChange = 0; //0: no need to wait;
    	//1: wait for other right hand segment
    	//-1: wait for another left hand segment 
    	
    	double minKey = newCoords.firstKey();
    	double maxKey = newCoords.lastKey();
    	
    	for (double t = maxKey;
    			t > minKey;
    			t = newCoords.subMap(minKey, t).lastKey()) {
    		//if guaranteed no singularity, just do    		
    		//check which segments contains P(t) 
    	}	 
    */		
		
	}

    
    protected void compute() {
    	
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

    	
    	if (!pAsBoundary) {
    		newSegmentCoords.clear();
    		
    		//Calculate segments. Not for degenerating case 
    		//(i.e. P has some 0 segments or some coinciding points)
    		//TODO degenerating cases
    		intersectionsSegments(g, p, newCoords, newSegmentCoords);
    		
    		
    		//update and/or create segments
    		int indexSegment = 0;   	
    		//affect new computed segments
    		outputSegments.adjustOutputSize(newSegmentCoords.size());
    		for (Coords[] segmentCoords : newSegmentCoords.values()){
    		
    			GeoSegmentND segment = (GeoSegmentND) outputSegments.getElement(indexSegment);
    			segment.setTwoPointsCoords(segmentCoords[0], segmentCoords[1]);
    			indexSegment++;
    		}
    		//other segments are undefined
    		for(;indexSegment<outputSegments.size();indexSegment++)
    		outputSegments.getElement(indexSegment).setUndefined();
    	}
    }


	final public String toString() {
        return app.getPlain("IntersectionPointOfAB",((GeoElement) g).getLabel(),p.getLabel());
    }
    
      


    
    
    
}
