/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

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
public class AlgoMacro extends AlgoElement 
implements EuclidianViewAlgo {

	private static final long serialVersionUID = 1L;	
	
	private Macro macro; 
	
	// macro construction, its input and output used by this algo	
	private GeoElement [] macroInput, macroOutput;
	
	// maps macro geos to algo geos
	private FastHashMapKeyless macroToAlgoMap;
	
	// all keys of macroToAlgoMap that are not part of macroInput
	private ArrayList macroOutputAndReferencedGeos;
	private ArrayList algoOutputAndReferencedGeos; // for efficiency, see getMacroConstructionState()	
        
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
    
    protected String getClassName() {
		return "AlgoMacro";
	}
	
	String getCommandName() {
		return macro.getCommandName();
	}
    
	protected void setInputOutput() {    	             
        setDependencies();
    }       
    
    

	final public boolean wantsEuclidianViewUpdate() {
		return macro.wantsEuclidianViewUpdate();
	}
	
	public void euclidianViewUpdate() {
    	// TODO: AlgoMacro.euclidianViewUpdate: make more efficient
		// I can't figure out at the moment why a simple update() doesn't 
		// do the trick to update locus lines in macros when the view
		// size has changed, Markus 2008-03-13
		
		input[0].updateCascade();		
		update();
    }   
           	
    final protected void compute() {
    	try {    		
    		// set macro geos to algo geos state
    		setMacroConstructionState();

			// update all algorithms of macro-construction
			macro.getMacroConstruction().updateAllAlgorithms();      	
       
    		// set algo geos to macro geos state   
    		getMacroConstructionState();
    		
    	} catch (Exception e) {
    		System.err.println("AlgoMacro compute():\n");
    		e.printStackTrace();
    		for (int i=0; i < output.length; i++) {
    			output[i].setUndefined();
    		}
    	}
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
					
			//System.out.println("SET INPUT object: " + input[i] + " => " + macroInput[i]);
    	}		
	}

	
	/** 
	 * Sets algo geos to the current state of macro geos.	 
	 */
	final void getMacroConstructionState() {	
		// for efficiency: instead of lookups in macroToAlgoMap 
		// we use an array list algoOutputAndReferencedGeos with corresponding macro and algo geos
		int size = macroOutputAndReferencedGeos.size();
		for (int i=0; i < size; i++) {	
			GeoElement macroGeo = (GeoElement) macroOutputAndReferencedGeos.get(i);
			GeoElement algoGeo = (GeoElement) algoOutputAndReferencedGeos.get(i);							
			algoGeo.set(macroGeo);	
		}
		
// old code:		
//		int size = macroOutputAndReferencedGeos.size();
//		for (int i=0; i < size; i++) {			
//			GeoElement macroGeo = (GeoElement) macroOutputAndReferencedGeos.get(i);
//			GeoElement algoGeo = (GeoElement) macroToAlgoMap.get(macroGeo);							
//			algoGeo.set(macroGeo);	
//			
//			System.out.println("RESULT from macro: " + macroGeo + " => " + algoGeo);
//	
//		}
	}
	
	
	/**
	 * Creates the output objects of this macro algorithm
	 */
	private void createOutputObjects() {		
		output = new GeoElement[macroOutput.length];								 						
		
		for (int i=0; i < macroOutput.length; i++) {  
			// copy output object of macro and make the copy part of this construction
			output[i] = macroOutput[i].copyInternal(cons);			
			output[i].setUseVisualDefaults(false);
			output[i].setVisualStyle(macroOutput[i]);	
			output[i].setAlgoMacroOutput(true);
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
		macroOutputAndReferencedGeos = new ArrayList();
		algoOutputAndReferencedGeos = new ArrayList();
			
		// INPUT initing	
		// map macro input to algo input
		for (int i=0; i < macroInput.length; i++) {
			map(macroInput[i], input[i]);
		}					
						
		// OUTPUT initing	
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
	}
	
	/**
	 * Adds a (macroGeo, algoGeo) pair to the map. 	 		
	 */		
	private void map(GeoElement macroGeo, GeoElement algoGeo) {					
		if (macroToAlgoMap.get(macroGeo) == null) {
			// map macroGeo to algoGeo
			macroToAlgoMap.put(macroGeo, algoGeo);	
			
			if (!isMacroInputObject(macroGeo)) {						
				macroOutputAndReferencedGeos.add(macroGeo);
				// for efficiency: to avoid lookups in macroToAlgoMap
				algoOutputAndReferencedGeos.add(algoGeo); 
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
				
			case GeoElement.GEO_CLASS_POLYGON:
				initPolygon((GeoPolygon) macroGeo, (GeoPolygon) algoGeo);
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
	 */
	private void initPolygon(GeoPolygon macroPoly, GeoPolygon poly) {								
		// points
		GeoPoint [] macroPolyPoints = macroPoly.getPoints();
		GeoPoint [] polyPoints = new GeoPoint[macroPolyPoints.length];										
		for (int i=0; i < macroPolyPoints.length; i++) {
			polyPoints[i] = (GeoPoint) getAlgoGeo( macroPolyPoints[i] );	
		}
		poly.setPoints(polyPoints);
		

//		// segments
//		GeoSegment [] macroPolySegments = macroPoly.getSegments();
//		GeoSegment [] polySegments = new GeoSegment[macroPolySegments.length];										
//		for (int i=0; i < macroPolySegments.length; i++) {
//			polySegments[i] = (GeoSegment) getAlgoGeo( macroPolySegments[i] );	
//			initLine(macroPolySegments[i], polySegments[i]);
//		}
//		poly.setSegments(polySegments);
										
	} 
	
	
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
