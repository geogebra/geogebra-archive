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

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Algorithm to invoke a specific macro. 
 * 
 * @author  Markus
 * @version 
 */
public class AlgoMacro extends AlgoElement {

	private static final long serialVersionUID = 1L;	
	
	private Macro macro;   	
	
	// macro construction, its input and output used by this algo	
	private GeoElement [] macroInput, macroOutput;
	
	// maps macro geos to algo geos
	private HashMap macroToAlgoMap = new HashMap();
	
	// all keys of macroToAlgoMap that are not part of macroInput
	private ArrayList outputAndReferencedGeos = new ArrayList();
        
    /**
     * Creates a new algorithm that applies a macro to the
     * given input objects.        
     */
    public AlgoMacro(Construction cons, String [] labels, Macro macro, GeoElement [] input) {
    	super(cons);
    	  
    	this.input = input;
        this.macro = macro;
        this.macroInput = macro.getMacroInput();
        this.macroOutput = macro.getMacroOutput();
                 	   
        // register algorithm with macro: the macro will        
        // call setMacroConsInputOutput
        macro.registerAlgorithm(this);
        
        // create copies for the output objects
    	initOuput();
    	
    	// initialize the mapping between macro geos and algo geos
    	initMap();    	  	
    	
    	setInputOutput();     	    	
    	compute();    	
        
        GeoElement.setLabels(labels, output);                       
    }         
    
    public void remove() {
    	macro.unregisterAlgorithm(this);
    	super.remove();    	
    }
    
    public void continuityUpdate() {
    	initMap();    
    	initForNearToRelationship();
    	super.continuityUpdate();
    }        
    
	String getClassName() {
		return "AlgoMacro";
	}
	
	String getCommandName() {
		return macro.getCommandName();
	}
    
    void setInputOutput() {    	             
        setDependencies();
    }             
        
    final void compute() {	
    	// set macro geos to algo geos state
    	setMacroConstructionState();
		
		// update all algorithms of macro-construction
        macro.updateAllAlgorithms();        
        
      	// set algo geos to macro geos state   
        getMacroConstructionState();            
    }   
    
    final public String toString() {    	
        return getCommandDescription();
    }	         
    
    /**
     * Returns true when macroGeo is part of macroInput.
     */
	private boolean isMacroInputObject(GeoElement macroGeo) {
		for (int i=0; i < macroInput.length; i++) {
			if (macroGeo == macroInput[i])
				return true;
		}		
		return false;
	}		

	
	/** 
	 * Sets macro geos to the current state of algo geos.	 
	 */
	final void setMacroConstructionState() {									
		// set input objects of macro construction		
		for (int i=0; i < macroInput.length; i++) {   
			macroInput[i].set(input[i]);							
			// TODO: remove
			//System.out.println("SET input object: " + macroInput[i]);
    	}		
	}

	
	/** 
	 * Sets algo geos to the current state of macro geos.	 
	 */
	final void getMacroConstructionState() {				
		int size = outputAndReferencedGeos.size();
		for (int i=0; i < size; i++) {			
			GeoElement macroGeo = (GeoElement) outputAndReferencedGeos.get(i);
			GeoElement algoGeo = (GeoElement) macroToAlgoMap.get(macroGeo);							
			algoGeo.set(macroGeo);
		
			// TODO: remove
			//System.out.println("RESULT from macro: " + macroGeo + " => " + algoGeo);
		}			
	}
	
	
	/**
	 * Inits the output objects 
	 */
	private void initOuput() {		
		output = new GeoElement[macroOutput.length];								 						
		
		for (int i=0; i < macroOutput.length; i++) {  
			// copy output object of macro and make the copy it part of this construction
			output[i] = macroOutput[i].copyInternal();
			output[i].setConstruction(cons);
			output[i].setVisualStyle(macroOutput[i]);				
    	}			
	}
	
	/**
	 * Inits the mapping of macro geos to algo geos construction.
	 * The map is used to set and get the state of the macro construction in compute()
	 * and to make sure that all output geos of the algorithm and all
	 * their references (e.g. the start point of a ray) are part of the algorithm's 
	 * construction.
	 */
	private void initMap() {	
		macroToAlgoMap.clear();		
		
		// map macro input to algo input
		for (int i=0; i < macroInput.length; i++) {
			map(macroInput[i], input[i]);
		}
		
		// map macro output to algo output
		for (int i=0; i < macroOutput.length; i++) {
			map(macroOutput[i], output[i]);	
			
			// SPECIAL REFERENCES of output
			// make sure all algo-output objects reference objects
			// in their own construction
			initSpecialReferences(macroOutput[i], output[i]);
    	}																									
	}
	
	/**
	 * Adds a (macroGeo, algoGeo) pair to the map. 	 		
	 */		
	private void map(GeoElement macroGeo, GeoElement algoGeo) {				
		if (macroToAlgoMap.get(macroGeo) == null) {
			if (algoGeo == null) {
				algoGeo = createAlgoCopy(macroGeo);
			}
			
			// map macroGeo to algoGeo
			macroToAlgoMap.put(macroGeo, algoGeo);	
			
			// remember non-input macroGeo
			if (!isMacroInputObject(macroGeo)) {
				outputAndReferencedGeos.add(macroGeo);
				
//				 TODO: remove
			//	System.out.println(this + ": map entry: " + macroGeo + " => " + algoGeo);
			}	
			
//			 TODO: remove
		//	else
		//	System.out.println(this + ": map entry (INPUT): " + macroGeo + " => " + algoGeo);								
		}
	}
	
	/**
	 * Returns a GeoElement in this algo's construction
	 * that corresponds to the given macroGeo from the macro construction.
	 * Note: this method never returns null; if a macro-geo is not yet
	 * mapped to an algo-geo, a new algo-geo is created and added to 
	 * the map automatically.
	 */
	private GeoElement getAlgoGeo(GeoElement macroGeo) {
		GeoElement algoGeo = (GeoElement) macroToAlgoMap.get(macroGeo);
		
		// if we don't have a corresponding GeoElement in our map yet, 
		// create a new geo and update the map
		if (algoGeo == null) {					
			algoGeo = createAlgoCopy(macroGeo);
			map(macroGeo, algoGeo);										
		}					
		
		return algoGeo;		
	}
	
	/**
	 * Creates a new algo-geo in this construction that is copy of macroGeo from
	 * the macro construction.
	 */
	private GeoElement createAlgoCopy(GeoElement macroGeo) {
		GeoElement algoGeo = macroGeo.copyInternal();
		algoGeo.setConstruction(cons);
		return algoGeo;
	}
	
		
	/**
	 * Some GeoElement types need special settings as they reference other
	 * GeoElement objects. We need to make sure that algoGeo
	 * only reference objects in its own construction.
	 */	
	private void initSpecialReferences(GeoElement macroGeo, GeoElement algoGeo) {
		
		switch (algoGeo.getGeoClassType()) {				
			case GeoElement.GEO_CLASS_FUNCTION:
				// TODO: implement function support
				break;
				
			case GeoElement.GEO_CLASS_FUNCTIONCONDITIONAL:
				// TODO: implement function conditional support
				break;
				
			case GeoElement.GEO_CLASS_IMAGE:
				// TODO: implement image support
				break;
				
			case GeoElement.GEO_CLASS_LIST:
				// TODO: implement list support
				break;
			
			case GeoElement.GEO_CLASS_POLYGON:
				initPolygon((GeoPolygon) macroGeo, (GeoPolygon) algoGeo);
				break;

			case GeoElement.GEO_CLASS_RAY:
				initRay((GeoRay) macroGeo, (GeoRay) algoGeo);
				break;

			case GeoElement.GEO_CLASS_SEGMENT:
				initSegment((GeoSegment) macroGeo, (GeoSegment) algoGeo);
				break;

			case GeoElement.GEO_CLASS_TEXT:
				// TODO: implement list support
				break;

			case GeoElement.GEO_CLASS_VECTOR:
				// TODO: implement list support
				break;										
				
			default:
			// no special treatment necessary
				// case GeoElement.GEO_CLASS_ANGLE:					
				// case GeoElement.GEO_CLASS_AXIS:
				// case GeoElement.GEO_CLASS_BOOLEAN:
				// case GeoElement.GEO_CLASS_CONIC:
				// case GeoElement.GEO_CLASS_CONICPART:
				// case GeoElement.GEO_CLASS_LINE:
				// case GeoElement.GEO_CLASS_LOCUS:
				// case GeoElement.GEO_CLASS_NUMERIC:
				// case GeoElement.GEO_CLASS_POINT:										
		}						
	}
	
	/**
	 * Makes sure that the start point of ray is
	 * in its construction.
	 */			
	private void initRay(GeoRay macroRay, GeoRay ray) {		
		GeoPoint startPoint = (GeoPoint) getAlgoGeo(macroRay.getStartPoint());							
		ray.setStartPoint(startPoint);		
	}
	
	/**
	 * Makes sure that the start and end point of segment are
	 * in its construction.
	 */			
	private void initSegment(GeoSegment macroSegment, GeoSegment segment) {				
		GeoPoint startPoint = (GeoPoint) getAlgoGeo(macroSegment.getStartPoint());
		GeoPoint endPoint   = (GeoPoint) getAlgoGeo(macroSegment.getEndPoint());						
		segment.setStartPoint(startPoint);
		segment.setEndPoint(endPoint);					
	}
	
	/**
	 * Makes sure that the points and segments of poly are
	 * in its construction.
	 */			
	private void initPolygon(GeoPolygon macroPoly, GeoPolygon poly) {								
		// points
		GeoPoint [] macroPolyPoints = macroPoly.getPoints();
		GeoPoint [] polyPoints = new GeoPoint[macroPolyPoints.length];										
		for (int i=0; i < macroPolyPoints.length; i++) {
			polyPoints[i] = (GeoPoint) getAlgoGeo( macroPolyPoints[i] );	
		}
		poly.setPoints(polyPoints);
		
		// segments
		GeoSegment [] macroPolySegments = macroPoly.getSegments();
		GeoSegment [] polySegments = new GeoSegment[macroPolySegments.length];										
		for (int i=0; i < macroPolySegments.length; i++) {
			polySegments[i] = (GeoSegment) getAlgoGeo( macroPolySegments[i] );	
			initSegment(macroPolySegments[i], polySegments[i]);
		}
		poly.setSegments(polySegments);									
	} 
    
}
