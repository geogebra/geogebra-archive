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

import geogebra.main.Application;

import java.util.ArrayList;
import java.util.TreeMap;


/**
 * Algo for intersection of a line with a polygon
 * @author  matthieu
 * @version 
 */
public class AlgoIntersectLinePolygon extends AlgoElement
implements AlgoElementWithResizeableOutput{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private GeoLine g; // input
	private GeoPolygon p; //input
    private ArrayList<GeoPoint> pointsList; // output   
    //private ArrayList<GeoSegment> segmentsList; // output   
    
    private int nbPoints = 0;
    
    private TreeMap<Double, GeoPoint> pointsTree;
    
    private int labelsUsed = 0;
    
    private String singleLabel = null;
    
    /** 
     * common constructor
     * @param cons
     * @param labels
     * @param g
     * @param p
     */
    AlgoIntersectLinePolygon(Construction cons, String[] labels, GeoLine g, GeoPolygon p) {
        super(cons);
        
        
        this.g = g;
        this.p = p;

        pointsTree = new TreeMap<Double, GeoPoint>();
        
        setInputOutput(); // for AlgoElement

        
        //if only one label (e.g. "A"), new labels will be A_1, A_2, ...
        if (labels.length==1)
        	if (labels[0]!=null)
        		if (!labels[0].equals(""))
        			singleLabel = labels[0];

        //set labels dependencies: will be used with Construction.resolveLabelDependency()
        if (singleLabel==null)
        	for (int i=0; i<labels.length; i++){
        		cons.setLabelDependsOn(labels[i], this);
        	}

        compute();
        
        
        

    }

    public String getClassName() {
        return "AlgoIntersectLinePolygon";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = g;
        input[1] = p;

        

        nbPoints = 0;
        pointsList = new ArrayList<GeoPoint>();
        //segmentsList = new ArrayList<GeoSegment>();
        
        
        setDependencies(); // done by AlgoElement
    }

    private void setLabel(GeoElement geo){
    	
    	if (singleLabel==null) //choose any free label
    		geo.setLabel(null);
    	else //choose a "A_i" label
    		geo.setLabel(geo.getIndexLabel(singleLabel));
    	
    	labelsUsed++;
    }
    

    
    protected final void compute() {  
    	
    	//clear the points map
    	pointsTree.clear();
    	
    	//fill a new points map
    	for(int i=0; i<p.getSegments().length; i++){
    		GeoSegment seg = (GeoSegment) p.getSegments()[i];
    		GeoPoint point = new GeoPoint(getConstruction());
    		GeoVec3D.cross(g, seg,point);
    		if (seg.isIntersectionPointIncident(point, Kernel.MIN_PRECISION)){
       			double t = g.getPossibleParameter(point);
       			//TODO line without start point
       			//double t = g.inner(point);
    			//Application.debug("parameter("+i+") : "+t);
       			if (t>=g.getMinParameter() && t<=g.getMaxParameter())//TODO optimize that
       				pointsTree.put(t, point);
    		}
        }
    	
    	//update and/or create points
    	int index = 0;
    	for (GeoPointInterface point : pointsTree.values()){
    		if (index<nbPoints)//re-affect old point
    			pointsList.get(index).set(point);
    		else{//add new point to the list and to the output
    			//setOutputDependencies((GeoElement) point);
    			addToOutput((GeoElement) point);
    			setLabel((GeoElement) point);
    			//pointsList.add((GeoPoint) point);
    		}
    		
    		//segment
    		/*
    		if (startPoint!=null){
    			GeoSegment segment;
    			if (indexSegment < nbSegments){
    				segment = segmentsList.get(indexSegment);
    				segment.setPoints((GeoPoint) startPoint, (GeoPoint) point);
    				segment.calcLength();
    			}else{
    				segment = new GeoSegment(getConstruction(),(GeoPoint) startPoint, (GeoPoint) point);
    				segment.calcLength();
    				addToOutput(segment);
    				setLabel(segment);
    				//segmentsList.add(segment);
    			}  			
    			indexSegment++;
    		}
    		*/
    		
    		index++;
    	}
    	
    	

    	// set other points to undefined
    	for (int i=index; i<nbPoints; i++)
    		pointsList.get(i).setUndefined();
    	
    	// set other segments to undefined
    	/*
    	for (int i=indexSegment; i<nbSegments; i++)
    		segmentsList.get(i).setUndefined();
    	*/
   	
    	
    	// update number of points
    	nbPoints = pointsList.size();
    	
    	// update number of segments
    	//nbSegments = segmentsList.size();
    	
    	//Application.debug("nbDefinedPoints = "+nbDefinedPoints+", nbDefinedSegments = "+nbDefinedSegments);
    }

    final public String toString() {
        return app.getPlain("IntersectionPointOfAB",g.getLabel(),p.getLabel());
    }
    
    
    
    ///////////////////////////////////////////////////
    // OUTPUT
    ///////////////////////////////////////////////////
    
    private int nbLabelSetPoints = 0;
    //private int nbLabelSetSegment = 0;
    
    private void addToOutput(GeoElement geo){
    	addToOutput(geo, true);
    }
    
    public GeoElement addToOutput(GeoElement geo, boolean computedGeo){
    	

    	switch (geo.getGeoClassType()){
    	case GeoElement.GEO_CLASS_POINT:
    		if (nbLabelSetPoints<pointsList.size() && !computedGeo){
    			GeoElement geo2 = pointsList.get(nbLabelSetPoints);
    			//geo2.setLabel(geo.getLabel());
    			geo=geo2;
    			nbLabelSetPoints++;
    		}else{
    			pointsList.add((GeoPoint) geo);
    			setOutputDependencies(geo);
    			if (!computedGeo)
    				nbLabelSetPoints++;
    		}
    		break;
    		/*
    	case GeoElement.GEO_CLASS_SEGMENT:
    		if (nbLabelSetSegment<segmentsList.size() && !computedGeo){
    			GeoElement geo2 = segmentsList.get(nbLabelSetSegment);
    			//geo2.setLabel(geo.getLabel());
    			geo=geo2;
    			nbLabelSetSegment++;
    		}else{
    			segmentsList.add((GeoSegment) geo);
    			setOutputDependencies(geo);
    			if (!computedGeo)
    				nbLabelSetSegment++;
    		}
    		break;
    		*/
    	default:
    		Application.printStacktrace("wrong geo class type");
    		break;
    	}
    	
    	return geo;
    }
       
    protected GeoElement getOutput(int i){
    	
    	return pointsList.get(i);
    	
    	/*
    	if (i<pointsList.size())
    		return pointsList.get(i);
    	else
    		return segmentsList.get(i-pointsList.size());
    		*/
    }
    
    protected int getNbOutput(){
    	return pointsList.size();//+segmentsList.size();
    }
    
    public GeoElement[] getOutput(){
    	GeoElement[] output = new GeoElement[getNbOutput()];
    	
    	
    	int i = 0;
    	for (GeoElement geo : pointsList){
    		output[i] = geo;
    		i++;
    	}
    	
    	/*
    	for (GeoElement geo : segmentsList){
    		output[i] = geo;
    		i++;
    	}
    	*/
    	
    	return output;
    	
    }
    
}
