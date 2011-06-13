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

import java.awt.Color;
import java.util.TreeMap;


/**
 * Algo for intersection of a line with a polygon
 * @author  matthieu
 * @version 
 */
public class AlgoIntersectLinePolygon extends AlgoIntersectLinePolyLine{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	protected GeoLineND g; // input
	protected GeoPolygon p; //input
	protected OutputHandler<GeoElement> outputPoints; // output
	
    private TreeMap<Double, Coords> newCoords;
 
    
    public AlgoIntersectLinePolygon(Construction c, String[] labels,
			GeoLineND g, GeoPolygon p) {

    	super(c, labels, g, (GeoPolyLine) p.getBoundary());
        /*
		outputPoints=createOutputPoints();
        
        this.g = g;
        this.p = p;

        newCoords = new TreeMap<Double, Coords>(Kernel.DoubleComparator(Kernel.STANDARD_PRECISION));
        newSegmentCoords = new TreeMap<Double, Coords[]>(Kernel.DoubleComparator(Kernel.STANDARD_PRECISION));
        
        compute();
        
        setInputOutput(); // for AlgoElement
        
        setLabels(labels);
        update();    
*/
	}

	/**
     * 
     * @return handler for output points
     */
  /*  protected OutputHandler<GeoElement> createOutputPoints(){
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
    */
    public String getClassName() {
        return "AlgoIntersectLinePolygon";
    }

    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECT;
    }

    /*
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
    		if (Kernel.isZero(coords.getLast())){
    			Coords segStart = seg.getPointInD(2, 0);
    			Coords segEnd = seg.getPointInD(2, 1);
    			if (((GeoLine) g).isOnPath(segStart, Kernel.EPSILON) &&
    					((GeoLine) g).isOnPath(segEnd, Kernel.EPSILON)	) {
    				newCoords.put(((GeoLine) g).getPossibleParameter(segStart), segStart);
    				newCoords.put(((GeoLine) g).getPossibleParameter(segEnd), segEnd);
    			}
    		} else if (seg.respectLimitedPath(coords, Kernel.MIN_PRECISION)){
       			double t = ((GeoLine) g).getPossibleParameter(coords);
    			//Application.debug("parameter("+i+") : "+t);
       			if (t>=min && t<=max)//TODO optimize that
       				newCoords.put(t, coords);
    		}
        }
    }
    
*/
    
 /*   protected void compute() {
    	
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
    		
    		intersectionsSegments(g, p, newCoords, newSegmentCoords);
    		
    		
    		//update and/or create segments
    		int indexSegment = 0;   	
    		//affect new computed segments
    		outputSegments.adjustOutputSize(newSegmentCoords.size());
    		for (Coords[] segmentCoords : newSegmentCoords.values()){
    		
    			GeoSegmentND segment = (GeoSegmentND) outputSegments.getElement(indexSegment);
    			segment.setTwoPointsCoords(segmentCoords[0], segmentCoords[1]);
    			segment.setLineThickness(4); 
    			segment.setObjColor(new Color(153,0,255));
    			indexSegment++;
    		}
    		//other segments are undefined
    		for(;indexSegment<outputSegments.size();indexSegment++)
    		outputSegments.getElement(indexSegment).setUndefined();
    	}
    }
*/

    
/*
	protected void setLabels(String[] labels) {

		
		if (pAsBoundary) {
			if (labels!=null &&
	        		labels.length==1 &&
	        		outputPoints.size() > 1 &&
	        		labels[0]!=null &&
	        		!labels[0].equals("")) {
				outputPoints.setIndexLabels(labels[0]);
			} else {
				outputPoints.setLabels(labels);
        		outputPoints.setIndexLabels(outputSegments.getElement(0).getLabel());
			}
		} else {
        //if only one label (e.g. "A") for more than one output, new labels will be A_1, A_2, ...
			if (labels!=null &&
					labels.length==1 &&
					outputSegments.size() > 1 &&
					labels[0]!=null &&
					!labels[0].equals("")) {
        //	outputPoints.setIndexLabels(labels[0]);
          	outputSegments.setIndexLabels(labels[0]);
          	outputPoints.setLabels(new String[] {null});
        //if there are k>=2 labels, now for simplicity only the first two will be used.
          	//first for indexing points, second for indexing segments.
          	//  } else if (labels!=null &&
          	//		labels.length>=2 &&
          	//outputPoints.size() > 1 && outputSegments.size() > 1 &&
        	//	labels[0]!=null && labels[1]!=null &&
        		//!labels[0].equals("") && !labels[1].equals("")) {
        	//outputPoints.setIndexLabels(labels[0]);
        	//outputSegments.setIndexLabels(labels[1]);
        	} else {
        	
        	
        //	outputPoints.setIndexLabels(outputPoints.getElement(0).getLabel());
        	//if ( outputPoints.size()==0 || outputSegments.size()!=0) //when there is a point, an 'empty segment' is not needed.
        		outputSegments.setLabels(labels);
        		outputSegments.setIndexLabels(outputSegments.getElement(0).getLabel());
        		outputPoints.setLabels(new String[] {null});
        	}
		}
	}


    */
    
    
}
