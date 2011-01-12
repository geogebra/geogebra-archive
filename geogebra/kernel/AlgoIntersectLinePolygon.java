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
import geogebra.kernel.kernelND.GeoPointND;
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
    private OutputList pointsList, segmentsList; // output   
    //private ArrayList<GeoSegment> segmentsList; // output   
    
    
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
        if (labels!=null)
        	if (labels.length==1)
        		if (labels[0]!=null)
        			if (!labels[0].equals(""))
        				singleLabel = labels[0];

        //set labels dependencies: will be used with Construction.resolveLabelDependency()
        if (singleLabel==null && labels!=null)
        	for (int i=0; i<labels.length; i++){
        		cons.setLabelDependsOn(labels[i], this);
        	}

        compute();
        
        
        

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
        input[0] = g;
        input[1] = p;

        

        pointsList = new OutputList();
        segmentsList = new OutputList();
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
    	for (GeoPointND point : pointsTree.values()){
    		if (index<pointsList.getNumber())//re-affect old point
    			pointsList.get(index).set((GeoElement) point);
    		else{//add new point to the list and to the output
    			addComputedElementToOutput((GeoElement) point);
    			setLabel((GeoElement) point);
    		}
    		index++;
    	}
 
    	// set other points to undefined
    	pointsList.setLastUndefined(index);
    	
    	
    	//update and/or create segments
    	index = 0;
    	GeoPointND startPoint = null;
    	for (GeoPointND point : pointsTree.values()){
    		if (startPoint!=null){
    			
    			Coords middle = (Coords) startPoint.getInhomCoords().add(point.getInhomCoords()).mul(0.5);
    			if (p.isInRegion(middle.getX(),middle.getY())){

    				GeoSegment segment;
    				if (index < segmentsList.getNumber()){
    					segment = (GeoSegment) segmentsList.get(index);
    					segment.setPoints((GeoPoint) startPoint, (GeoPoint) point);
    					segment.calcLength();
    				}else{    				
    					segment = new GeoSegment(getConstruction(),(GeoPoint) startPoint, (GeoPoint) point);
    					segment.calcLength();
    					addComputedElementToOutput(segment);
    					setLabel(segment);
    				}  			
    				index++;

    			}
    		}
    		startPoint = point;    		
    	}
 
    	// set other points to undefined
    	segmentsList.setLastUndefined(index);
    	
    }

    final public String toString() {
        return app.getPlain("IntersectionPointOfAB",g.getLabel(),p.getLabel());
    }
    
    
    
    ///////////////////////////////////////////////////
    // OUTPUT
    ///////////////////////////////////////////////////
    

    private OutputList getOutputList(int geoClassType){
    	switch (geoClassType){
    	case GeoElement.GEO_CLASS_POINT:
    		return pointsList;
    	case GeoElement.GEO_CLASS_SEGMENT:
    		return segmentsList;
    	default:
    		return null;
    	}
    }
    
    private void addComputedElementToOutput(GeoElement geo){

    	/*
    	switch (geo.getGeoClassType()){
    	case GeoElement.GEO_CLASS_POINT:
    		pointsList.add((GeoPoint) geo);
    		break;
    	}
    	*/
    	
    	getOutputList(geo.getGeoClassType()).add(geo);
    	
		setOutputDependencies(geo);
    	
    }
    
    public GeoElement addLabelToOutput(String label, int type){
    	
    	GeoElement ret;
    	
    	GeoElement geo;
    	
    	switch (type){
    	case GeoElement.GEO_CLASS_POINT:
    		geo=new GeoPoint(getConstruction());
    	case GeoElement.GEO_CLASS_SEGMENT:
    		geo=new GeoSegment(getConstruction());
    	default:
    		geo=null;
    	}
    	
    	ret = getOutputList(type).addCreatedElement(geo);
    	
    	
    	
    	return ret;
    }
    
    
    protected GeoElement getOutput(int i){
    	
    	//return pointsList.get(i);
    	
    	
    	if (i<pointsList.size())
    		return pointsList.get(i);
    	else
    		return segmentsList.get(i-pointsList.size());
    		
    }
    
    protected int getOutputLength(){
    	return pointsList.size()+segmentsList.size();
    }
    
    public GeoElement[] getOutput(){
    	GeoElement[] output = new GeoElement[getOutputLength()];
    	
    	
    	int i = 0;
    	for (GeoElement geo : pointsList){
    		output[i] = geo;
    		i++;
    	}
    	
    	
    	for (GeoElement geo : segmentsList){
    		output[i] = geo;
    		i++;
    	}
    	
    	
    	return output;
    	
    }
    
    
    ///////////////////////////////////////////////////
    // OUTPUT LIST
    ///////////////////////////////////////////////////
    
    /**
     * Class managing output lists
     */
    private class OutputList extends ArrayList<GeoElement>{
    	
    	private int number = 0;
    	
    	private int nbLabelSet = 0;
    	
    	public int getNumber(){
    		return number;
    	}
    	
    	/**
    	 * set geos from index to number to undefined
    	 * @param index
    	 */
    	public void setLastUndefined(int index){
        	for (int i=index; i<number; i++)
        		get(i).setUndefined();
        	
        	// update number of points
        	number = size();
    	}
    	
    	
    	public GeoElement addCreatedElement(GeoElement geo){
    		GeoElement ret;
    		if (nbLabelSet<size()){ //set geo equal to element of the list
    			ret=get(nbLabelSet);
    			nbLabelSet++;
    		}else{ // add this geo at the end of the list
    			add(geo);
    			ret=geo;
    			setOutputDependencies(geo);
    			nbLabelSet++;
    		}
    		return ret;
    	}
    }

    
}
