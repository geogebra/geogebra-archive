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

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.util.FastHashMapKeyless;

import java.util.ArrayList;


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
	private FastHashMapKeyless macroToAlgoMap;
	
	// all keys of macroToAlgoMap that are not part of macroInput
	private ArrayList inputAndReferencedGeos, outputAndReferencedGeos;
	
	// for efficiency: instead of outputAndReferencedGeos and lookups in macroToAlgoMap 
	// we use two arrays with corresponding macro and algo geos in getMacroConstructionState()
	private GeoElement [] macroOutputAndRefGeos, algoOutputAndRefGeos;
	private GeoElement [] macroInputAndRefGeos, algoInputAndRefGeos;
	
	private static final int INIT_INPUT_REFERENCES = 1;
	private static final int INIT_OUTPUT_REFERENCES = 2;
	private int initing_state;
        
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
                 	   
        // register algorithm with macro
        macro.registerAlgorithm(this);
        
        // create copies for the output objects
    	createOutputObjects();
    	    	
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
			//macroInput[i].set(input[i]);
			macroInputAndRefGeos[i].set(algoInputAndRefGeos[i]);	
						
			// TODO: remove
			System.out.println("SET INPUT object: " + algoInputAndRefGeos[i] + " => " + macroInputAndRefGeos[i]);
    	}		
	}

	
	/** 
	 * Sets algo geos to the current state of macro geos.	 
	 */
	final void getMacroConstructionState() {	
		// for efficiency: instead of outputAndReferencedGeos and lookups in macroToAlgoMap 
		// we use two arrays with corresponding macro and algo geos in getMacroConstructionState()
		for (int i=0; i < macroOutputAndRefGeos.length; i++) {											
			algoOutputAndRefGeos[i].set(macroOutputAndRefGeos[i]);
			
			// TODO: remove
			System.out.println("RESULT from macro: " + macroOutputAndRefGeos[i] + " => " + algoOutputAndRefGeos[i]);
		}
		
		/* old code:
		int size = outputAndReferencedGeos.size();
		for (int i=0; i < size; i++) {			
			GeoElement macroGeo = (GeoElement) outputAndReferencedGeos.get(i);
			GeoElement algoGeo = (GeoElement) macroToAlgoMap.get(macroGeo);							
			algoGeo.set(macroGeo);					
		}*/	
	}
	
	
	/**
	 * Creates the output objects of this macro algorithm
	 */
	private void createOutputObjects() {		
		output = new GeoElement[macroOutput.length];								 						
		
		for (int i=0; i < macroOutput.length; i++) {  
			// copy output object of macro and make the copy it part of this construction
			output[i] = macroOutput[i].copyInternal(cons);			
			output[i].setUseVisualDefaults(false);
			output[i].setVisualStyle(macroOutput[i]);	
			output[i].isAlgoMacroOutput = true;						
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
		macroToAlgoMap = new FastHashMapKeyless();
		inputAndReferencedGeos = new ArrayList();
		outputAndReferencedGeos = new ArrayList();
		
		// INPUT initing
		initing_state = INIT_INPUT_REFERENCES;
		
		// map macro input to algo input
		for (int i=0; i < macroInput.length; i++) {
			map(macroInput[i], input[i]);
		}			
		// SPECIAL REFERENCES of input
		// make sure all algo-output objects reference objects in their own construction			
		for (int i=0; i < macroInput.length; i++) { 
			initSpecialReferences(macroInput[i], input[i]);					
    	}	
		
				
		// OUTPUT initing
		initing_state = INIT_OUTPUT_REFERENCES;
		
		// map macro output to algo output
		for (int i=0; i < macroOutput.length; i++) {
			map(macroOutput[i], output[i]);					
    	}				
		// SPECIAL REFERENCES of output
		// make sure all algo-output objects reference objects in their own construction		
		// note: we do this in an extra loop to make sure we don't create output objects twice	
		for (int i=0; i < macroOutput.length; i++) { 
			initSpecialReferences(macroOutput[i], output[i]);					
    	}		
		
		
		// for efficiency: instead of inputAndReferencedGeos and lookups in macroToAlgoMap 
		// we use two arrays with corresponding macro and algo geos in setMacroConstructionState()
		int size = inputAndReferencedGeos.size();
		macroInputAndRefGeos = new GeoElement[size];
		algoInputAndRefGeos = new GeoElement[size];
		for (int i=0; i < size; i++) {
			macroInputAndRefGeos[i] = (GeoElement) inputAndReferencedGeos.get(i);
		}				
		for (int i=0; i < size; i++) {
			algoInputAndRefGeos[i] = (GeoElement) macroToAlgoMap.get(macroInputAndRefGeos[i]);
		}
		
		// for efficiency: instead of outputAndReferencedGeos and lookups in macroToAlgoMap 
		// we use two arrays with corresponding macro and algo geos in getMacroConstructionState()
		size = outputAndReferencedGeos.size();
		macroOutputAndRefGeos = new GeoElement[size];
		algoOutputAndRefGeos = new GeoElement[size];
		for (int i=0; i < size; i++) {
			macroOutputAndRefGeos[i] = (GeoElement) outputAndReferencedGeos.get(i);
		}				
		for (int i=0; i < size; i++) {
			algoOutputAndRefGeos[i] = (GeoElement) macroToAlgoMap.get(macroOutputAndRefGeos[i]);
		}								
	}
	
	/**
	 * Adds a (macroGeo, algoGeo) pair to the map. 	 		
	 */		
	private void map(GeoElement macroGeo, GeoElement algoGeo) {					
		if (macroToAlgoMap.get(macroGeo) == null) {
			// map macroGeo to algoGeo
			macroToAlgoMap.put(macroGeo, algoGeo);	
			
			// remember mapped geos
			switch (initing_state) {
				case INIT_INPUT_REFERENCES:
					inputAndReferencedGeos.add(macroGeo);
					break;
					
				case INIT_OUTPUT_REFERENCES:
					if (!isMacroInputObject(macroGeo)) {						
						outputAndReferencedGeos.add(macroGeo);
					}
					break;										
			}			
		}
	}
	
	/**
	 * Returns a GeoElement in this algo's construction
	 * that corresponds to the given macroGeo from the macro construction.
	 * If a macro-geo is not yet
	 * mapped to an algo-geo, a new algo-geo is created and added to 
	 * the map automatically.
	 */
	private GeoElement getAlgoGeo(GeoElement macroGeo) {
		if (macroGeo == null) return null;
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
		GeoElement algoGeo = macroGeo.copyInternal(cons);		
		return algoGeo;
	}
	
		
	/**
	 * Some GeoElement types need special settings as they reference other
	 * GeoElement objects. We need to make sure that algoGeo
	 * only reference objects in its own construction.
	 */	
	private void initSpecialReferences(GeoElement macroGeo, GeoElement algoGeo) {
		
		switch (macroGeo.getGeoClassType()) {				
			case GeoElement.GEO_CLASS_FUNCTION:
				initFunction(((GeoFunction) algoGeo).getFunction());
				break;
				
			case GeoElement.GEO_CLASS_FUNCTIONCONDITIONAL:
				// done by set() in GeoFunctionConditional 
				// actually a GeoFunctionConditional consists of three GeoFunction objects,
				// so initFunction() is eventually used for them
				break;
											
			case GeoElement.GEO_CLASS_LIST:
				initList((GeoList) macroGeo, (GeoList) algoGeo);
				break;									
										
			case GeoElement.GEO_CLASS_LINE:						
				initLine((GeoLine) macroGeo, (GeoLine) algoGeo);
				break;	
				
			case GeoElement.GEO_CLASS_CONIC:
				initConic((GeoConic) macroGeo, (GeoConic) algoGeo);
				break;

			case GeoElement.GEO_CLASS_TEXT:
			case GeoElement.GEO_CLASS_VECTOR:
			case GeoElement.GEO_CLASS_IMAGE:
				initLocateable((Locateable) macroGeo, (Locateable) algoGeo);
				break;

			default:
			// no special treatment necessary at the moment
				// case GeoElement.GEO_CLASS_ANGLE:								
				// case GeoElement.GEO_CLASS_BOOLEAN:				
				// case GeoElement.GEO_CLASS_CONICPART:
				// case GeoElement.GEO_CLASS_LOCUS:
				// case GeoElement.GEO_CLASS_NUMERIC:
				// case GeoElement.GEO_CLASS_POINT:	
				// case GeoElement.GEO_CLASS_AXIS:
				// case GeoElement.GEO_CLASS_RAY:
				// case GeoElement.GEO_CLASS_SEGMENT:
				// case GeoElement.GEO_CLASS_POLYGON:
		}						
	}		
	
	/**
	 * Makes sure that the start and end point of a line are
	 * in its construction (if the line has this kind of information).
	 */			
	private void initLine(GeoLine macroLine, GeoLine line) {				
		GeoPoint startPoint = (GeoPoint) getAlgoGeo(macroLine.getStartPoint());
		GeoPoint endPoint   = (GeoPoint) getAlgoGeo(macroLine.getEndPoint());						
		line.setStartPoint(startPoint);
		line.setEndPoint(endPoint);					
	}
	
	/**
	 * Makes sure that all points on conic are
	 * in its construction.
	 */			
	private void initConic(GeoConic macroConic, GeoConic conic) {
		ArrayList macroPoints = macroConic.getPointsOnConic();
		if (macroPoints == null) return;
		
		int size = macroPoints.size();
		ArrayList points = new ArrayList(size);		
		for (int i=0; i < size; i++) {
			points.add(getAlgoGeo((GeoElement) macroPoints.get(i)));
		}
		conic.setPointsOnConic(points);					
	}

	/**
	 * Makes sure that the start points of locateable are
	 * in its construction.
	 */	
	private void initLocateable(Locateable macroLocateable, Locateable locateable) {
		GeoPoint [] macroStartPoints = macroLocateable.getStartPoints();
		if (macroStartPoints == null) return;
		
		try {					
			for (int i=0; i < macroStartPoints.length; i++) {
				GeoPoint point = (GeoPoint) getAlgoGeo(macroStartPoints[i]);
				locateable.initStartPoint(point, i);
				
				//System.out.println("set start point: " + locateable + " => " + point + "(" + point.cons +")");
				
			}	
		} catch (Exception e) {
			System.err.println("AlgoMacro.initLocateable:\n" + e.getStackTrace());
		}
	}		
	
	/**
	 * Makes sure that the points and segments of poly are
	 * in its construction.
	 *
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
			initLine(macroPolySegments[i], polySegments[i]);
		}
		poly.setSegments(polySegments);									
	} 
	*/
	
	/**
	 * Makes sure that all referenced GeoElements of geoList are
	 * in its construction.
	 */			
	final public void initList(GeoList macroList, GeoList geoList) {			
		// make sure all referenced GeoElements are from the algo-construction
		
		int size = macroList.size();
		geoList.clear();
		geoList.ensureCapacity(size);
		for (int i=0; i < size; i++) {	
			geoList.add( getAlgoGeo(macroList.get(i)) );				
		}			
	} 
	
	/**
	 * Makes sure that all referenced GeoElements of fun are
	 * in this algorithm's construction.
	 */			
	final public void initFunction(Function fun) {								
		// geoFun was created as a copy of macroFun, 
		// make sure all referenced GeoElements are from the algo-construction
		replaceReferencedMacroObjects(fun.getExpression());
	} 
		
	/**
	 * Replaces all references to macroGeos in expression exp by references to the corresponding
	 * algoGeos
	 */
	private void replaceReferencedMacroObjects(ExpressionNode exp) {
		ExpressionValue left = exp.getLeft();
		ExpressionValue right = exp.getRight();
		
		// left tree
		if (left.isGeoElement()) {								
			GeoElement referencedGeo = (GeoElement) left;			
			if (macro.isInMacroConstruction(referencedGeo)) {		
				exp.setLeft(getAlgoGeo(referencedGeo));				
			}	
		}
		else if (left.isExpressionNode()) {
			replaceReferencedMacroObjects((ExpressionNode) left);
		}		
		
		// right tree
		if (right == null) 
			return;
		else if (right.isGeoElement()) {
			GeoElement referencedGeo = (GeoElement) right;
			if (macro.isInMacroConstruction(referencedGeo)) {		
				exp.setRight(getAlgoGeo(referencedGeo));		
			}	
		}
		else if (right.isExpressionNode()) {
			replaceReferencedMacroObjects((ExpressionNode) right);
		}		
	}
	
	
    
}
