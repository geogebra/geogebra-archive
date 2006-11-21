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

import geogebra.util.FastHashMapKeyless;


/**
 * Algorithm to invoke a specific macro. 
 * 
 * @author  Markus
 * @version 
 */
public class AlgoMacro extends AlgoElement {

	private static final long serialVersionUID = 1L;	
	
	private Macro macro;   
	
	// maps macro construction geos to construction geos
	private FastHashMapKeyless macroToAlgoGeos = new FastHashMapKeyless(); 
	private ArrayList updateMacroGeos = new ArrayList();
        
    /**
     * Creates a new algorithm that applies a macro to the
     * given input objects.        
     */
    public AlgoMacro(Construction cons, String [] labels, Macro macro, GeoElement [] input) {
    	super(cons);
    	    	    	    	     
        this.macro = macro;
    	this.input = input;
    	
    	initAlgorithm(macro);
    	macro.registerAlgorithm(this);    	
    	
    	setInputOutput();                 
        compute();
        
        GeoElement.setLabels(labels, output);                       
    }   
    
    public void remove() {
    	macro.unregisterAlgorithm(this);
    	super.remove();    	
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
    	// apply macro to update output
    	macro.applyMacro(this);
    }   
    
    final public String toString() {    	
        return getCommandDescription();
    }	  
    
    /**
	 * Returns a GeoElement in this algo's construction
	 * that corresponds to the given macroGeo from the macro construction.
	 * Note: this method never returns null; if a macro-geo is not yet
	 * mapped to an algo-geo, a new algo-geo is created and added to 
	 * the map automatically.
	 * @return
	 */
	private GeoElement getAlgoGeo(GeoElement macroGeo) {
		GeoElement algoGeo = (GeoElement) macroToAlgoGeos.get(macroGeo);
		
		// if we don't have a corresponding GeoElement in our map yet, 
		// create a new geo and update the map
		if (algoGeo == null) {					
			algoGeo = macroGeo.copyInternal();
			algoGeo.setConstruction(cons);
			map(macroGeo, algoGeo);		
			
			// TODO: remove
			System.out.println(this + ": SPECIAL map entry: " + macroGeo + " => " + algoGeo);	
			
			
		}					
		
		return algoGeo;		
	}
	
	/**
	 * Adds a (macroGeo, algoGeo) pair to the map. 	 
	 * @param needsUpdating: states whether an update to macroGeo should
	 * also update algoGeo, then
	 */
	private void map(GeoElement macroGeo, GeoElement algoGeo, boolean needsUpdating) {
		macroToAlgoGeos.put(macroGeo, algoGeo);
	
		if (needsUpdating)			
			updateMacroGeos.add(macroGeo);		
	}
	
	private void map(GeoElement macroGeo, GeoElement geo) {
		map(macroGeo, geo, true);
	}
	
	/** 
	 * Updates all output geos and geos referenced by them.
	 * Note: this method is called by Macro.applyMacro()
	 */
	final void updateMappedGeoElements() {
		// Output geos might reference other objects that need to be updated too.
		// For example, a segment might have a start and endpoint that are not
		// part of this algo's output.
		// All elements that need to be updated are stored in updateMacroGeos
		int size = updateMacroGeos.size();
		for (int i=0; i < size; i++) {
			GeoElement macroGeo = (GeoElement) updateMacroGeos.get(i);
			GeoElement algoGeo = (GeoElement) macroToAlgoGeos.get(macroGeo);
			specialSetInternal(macroGeo, algoGeo);					
		}		
		
		// we don't need to call update for the output objects as this
		// is done by AlgoElement.update()
	}
	
	
	/**
	 * Inits the output objects and a map where geos from the macro-construction
	 * are mapped to corresponding objects in this algorithm's construction.
	 * The map is used to make sure that all output geos of the algorithm and all
	 * their references (e.g. the start point of a ray) are part of the algorithm's construction.
	 */
	private void initAlgorithm(Macro macro) {		
		GeoElement [] macroInput = macro.getInputObjects();
		GeoElement [] macroOutput = macro.getOutputObjects();										
		
		// init output and the (macroGeo, algoGeo) map
		output = new GeoElement[macroOutput.length];		
			
		// map macro input to algo input
		for (int i=0; i < macroInput.length; i++) {
			map(macroInput[i], input[i], false);
		}		 								
		
		for (int i=0; i < macroOutput.length; i++) {  
			// copy output object of macro and make the copy it part of this construction
			output[i] = macroOutput[i].copyInternal();
			output[i].setConstruction(cons);
			output[i].setVisualStyle(macroOutput[i]);	
			
			// map macro geo to algo geo
			map(macroOutput[i], output[i]);			
			
			// this may call getAlgoGeo() and thus add mappings too
			specialSetInternal(macroOutput[i], output[i]);	
    	}			
	}
	
	/**
	 * Sets algoGeo to the current value of macroGeo.
	 * Some GeoElement types need special settings as they reference other
	 * GeoElement objects. We need to make sure that algoGeo
	 * only reference objects in its own construction.
	 */	
	private void specialSetInternal(GeoElement macroGeo, GeoElement algoGeo) {
		
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
				setPolygon((GeoPolygon) macroGeo, (GeoPolygon) algoGeo);
				break;

			case GeoElement.GEO_CLASS_RAY:
				setRay((GeoRay) macroGeo, (GeoRay) algoGeo);
				break;

			case GeoElement.GEO_CLASS_SEGMENT:
				setSegment((GeoSegment) macroGeo, (GeoSegment) algoGeo);
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
				algoGeo.setInternal(macroGeo);	
				
		}						
	}
	
	/**
	 * Makes sure that the start point of ray is
	 * in its construction.
	 */			
	private void setRay(GeoRay macroRay, GeoRay ray) {
		ray.setInternal(macroRay);	
		
		GeoPoint startPoint = (GeoPoint) getAlgoGeo(macroRay.getStartPoint());							
		ray.setStartPoint(startPoint);		
	}
	
	/**
	 * Makes sure that the start and end point of segment are
	 * in its construction.
	 */			
	private void setSegment(GeoSegment macroSegment, GeoSegment segment) {		
		segment.setInternal(macroSegment);	
		
		GeoPoint startPoint = (GeoPoint) getAlgoGeo(macroSegment.getStartPoint());
		GeoPoint endPoint   = (GeoPoint) getAlgoGeo(macroSegment.getEndPoint());						
		segment.setStartPoint(startPoint);
		segment.setEndPoint(endPoint);					
	}
	
	/**
	 * Makes sure that the points and segments of poly are
	 * in its construction.
	 */			
	private void setPolygon(GeoPolygon macroPoly, GeoPolygon poly) {
		// TODO: implement polygon setting in macros
		/*
		GeoPoint [] macroPolyPoints = macroPoly.getPoints();
		GeoSegment [] macroPolySegments = macroPoly.getSegments();
				
		GeoPoint [] polyPoints = poly.getPoints(); 
		if (polyPoints == null) {
			polyPoints = new GeoPoint[macroPo]
		}
		
		
		
		
		for (int i=0; i < macroPolyPoints.length; i++) {
			GeoPoint startPoint = (GeoPoint) getOrigConsGeo(poly.cons, macroToOrigConsMap, macroPoly.getStartPoint());	
		}
		
								
		poly.setStartPoint(startPoint);
		*/		
	} 
    
}
