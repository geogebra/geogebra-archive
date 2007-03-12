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

import geogebra.Application;
import geogebra.MyError;
import geogebra.util.Util;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * A macro is a user defined commmand.
 * It has its own macro construction that is used by all using AlgoMacro 
 * instances if this macro is non-continous. However, if it is continous
 * every AlgoMacro instance gets its own copy of the macro construction.
 * 
 * @author Markus Hohenwarter
 */
public class Macro {
	
	private Kernel kernel;
	private String cmdName, toolName = "", toolHelp = "";
	private String iconFileName = ""; // image file		
	private boolean showInToolBar = true;
				
	private Construction macroCons; // macro construction
	private String macroConsXML;
	private GeoElement [] macroInput, macroOutput; // input and output objects 
	private String [] macroInputLabels, macroOutputLabels;
	private Class [] inputTypes;
	
	private int usingAlgos = 0;
		
	/**
	 * Creates a new macro 
	 * using the given input and output GeoElements.		  
	 */
	public Macro(Kernel kernel, String cmdName,  
					GeoElement [] input, GeoElement [] output) 
	throws Exception {
		this(kernel, cmdName);				
		initMacro(input, output);
	}
	
	/**
	 * Creates a new macro. Note: you need to call initMacro() when using
	 * this constructor.
	 */
	public Macro(Kernel kernel, String cmdName) { 	
		this.kernel = kernel;
		setCommandName(cmdName);						
	}		
	
	/**
	 * Returns all input geos from the macro construction.
	 */
	public GeoElement [] getMacroInput() {
		return macroInput;
	}
	
	/**
	 * Returns all output geos from the macro construction.
	 */
	public GeoElement [] getMacroOutput() {
		return macroOutput;
	}
	
	/**
	 * Returns whether geo is part of this macro's construction.
	 */
	final public boolean isInMacroConstruction(GeoElement geo) {
		return geo.cons == macroCons;
	}
	
	/**
	 * Returns the construction object of this macro.
	 */
	public Construction getMacroConstruction() {
		return macroCons;
	}
	
	/**
	 * Updates all algorithms of the macro construction.	
	 */	
	final public void updateAllAlgorithms() {
		macroCons.updateAllAlgorithms();		
	}
	
	public void initMacro(Construction macroCons, String [] inputLabels, String [] outputLabels) {				
		this.macroCons = macroCons;
		this.macroConsXML = macroCons.getConstructionXML();
		this.macroInputLabels = inputLabels;
		this.macroOutputLabels = outputLabels;	
		
		initInputOutput();
		
		// init inputTypes array		
		inputTypes = new Class[macroInput.length];		
		for (int i=0; i < macroInput.length; i++) {
			inputTypes[i] = macroInput[i].getClass();
		}						 	
	}	
	
	private void initInputOutput() {
		// get the input and output geos from the macro construction
		macroInput = new GeoElement[macroInputLabels.length];		
		macroOutput = new GeoElement[macroOutputLabels.length];
		
		for (int i=0; i < macroInputLabels.length; i++) {    		
			macroInput[i] = macroCons.lookupLabel(macroInputLabels[i]);  
			macroInput[i].setFixed(false);						
			//System.out.println("macroInput[" + i + "] = " + macroInput[i]);
    	}
		
    	for (int i=0; i < macroOutputLabels.length; i++) {    		
    		macroOutput[i] = macroCons.lookupLabel(macroOutputLabels[i]);            		    		    		    	
			//System.out.println("macroOutput[" + i + "] = " + macroOutput[i]);
    	}         		
	}
	
	private void initMacro(GeoElement [] input, GeoElement [] output)  throws Exception {
		// check that every output object depends on an input object
		// and that all input objects are really needed
		boolean [] inputNeeded = new boolean[input.length];
		for (int i=0; i < output.length; i++) {
			boolean dependsOnInput = false;
			
			for (int k=0; k < input.length; k++) {
				boolean dependencyFound = output[i].isChildOf(input[k]);
				if (dependencyFound) {
					dependsOnInput = true; 
					inputNeeded[k] = true;
				}
			}
			
			if (!dependsOnInput) {
				throw new Exception(kernel.getApplication()
						.getError("Tool.OutputNotDependent") +
						": " + output[i].getNameDescription());
			}
		}	
		for (int k=0; k < input.length; k++) {			
			if (!inputNeeded[k]) {
				throw new Exception(kernel.getApplication()
						.getError("Tool.InputNotNeeded") +
						": " + input[k].getNameDescription());
			}
		}
		
		
		// steps to create a macro
		// 1) outputAndParents = set of all predecessors of output objects 
		// 2) inputChildren = set of all children of input objects
		// 3) macroElements = intersection of outputParents and inputChildren
		// 4) add input and output objects to macroElements
		// 5) create XML representation for macro-construction
		// 6) create a new macro-construction from this XML representation
		
		// 1) create the set of all parents of this macro's output objects				
		TreeSet outputParents = new TreeSet();		
		for (int i=0; i < output.length; i++) {
			 output[i].addPredecessorsToSet(outputParents, false);
			 
			 // note: Locateables (like Texts, Images, Vectors) may depend on points, 
			 //       these points must be part of the macro construction
			 if (output[i] instanceof Locateable) {
				 Locateable loc = (Locateable) output[i];
				 GeoPoint [] points = loc.getStartPoints();
				 if (points != null) {
					 for (int k=0; k < points.length; k++) {
						 outputParents.add(points[k]);
						 points[k].addPredecessorsToSet(outputParents, false);
					 }
				 }
			 }
		}			
		
		// 2) and 3) get intersection of inputChildren and outputParents				
		TreeSet macroConsOrigElements = new TreeSet(); 
    	Iterator it = outputParents.iterator();    	
    	while (it.hasNext()) {
    		GeoElement outputParent = (GeoElement) it.next();
    		if (outputParent.isLabelSet()) {
    			for (int i=0; i < input.length; i++) {
    				 if (outputParent.isChildOf(input[i])) {
    					 addDependentElement(outputParent, macroConsOrigElements);    					    			    	 
    			    	 // add parent only once: get out of loop
    					 i = input.length; 
    				 }
    			}    			
    		}
    	}        	       	    	
    	
    	// 4) add input and output objects to macroElements
    	// ensure that all input and all output objects have labels set
    	// Note: we have to undo this at the end of this method !!!
    	boolean [] isInputLabeled = new boolean[input.length];    	
    	boolean [] isOutputLabeled = new boolean[output.length];
    	String [] inputLabels = new String[input.length];
    	String [] outputLabels = new String[output.length];
    	
    	for (int i=0; i < input.length; i++) {
    		isInputLabeled[i] = input[i].isLabelSet();
    		if (!isInputLabeled[i]) {
    			input[i].label = input[i].getDefaultLabel();
    			input[i].labelSet = true;
        	}    		        		    			    		        	    	
    		inputLabels[i] = input[i].label;
    		    	
    		// add input element to macroConsOrigElements
    		// we handle some special cases for input types like segment, polygons, etc.
    		switch (input[i].getGeoClassType()) {
    			case GeoElement.GEO_CLASS_SEGMENT:    				
    			case GeoElement.GEO_CLASS_RAY:
    			case GeoElement.GEO_CLASS_POLYGON:
    				// add parent algo and input (points) to macroConsOrigElements
    				addSpecialInputElement(input[i], macroConsOrigElements);
    				break;    				    			
    				    		    		
    			default:
    				// add input element to macroConsOrigElements 	        	
    	    		macroConsOrigElements.add(input[i]);
    			
	        		// make sure we don't have any parent algorithms of input[i] in our construction
	        		AlgoElement algo = input[i].getParentAlgorithm();
	        		if (algo != null)
	        			macroConsOrigElements.remove(algo);
    		}
    		
    	}   
    	
    	for (int i=0; i < output.length; i++) {
    		isOutputLabeled[i] = output[i].isLabelSet();
    		if (!isOutputLabeled[i]) {
    			output[i].label = output[i].getDefaultLabel();
    			output[i].labelSet = true;    			
        	}        		   		    		
    		outputLabels[i] = output[i].label;
    		
    		// add output element and its algorithm to macroConsOrigElements 
    		addDependentElement(output[i], macroConsOrigElements); 	    		
    	}    	    	
    	    	
		// 5) create XML representation for macro-construction
    	String macroConsXML = buildMacroXML(macroConsOrigElements);
    	 	    
    	// if we used temp labels in step (4) remove them again
    	for (int i=0; i < input.length; i++) {    		
    		if (!isInputLabeled[i]) 
    			input[i].labelSet = false;        	    		
    	}    	    
    	for (int i=0; i < output.length; i++) {    		
    		if (!isOutputLabeled[i])		
    			output[i].labelSet = false;        
    	}    	    	    	    	    
    	
		// 6) create a new macro-construction from this XML representation
    	Construction macroCons = createMacroConstruction(macroConsXML); 
    	    	
    	// init macro 
    	initMacro(macroCons, inputLabels, outputLabels);
    }
	
	/**
	 * Adds the geo, its parent algorithm and all its siblings to the consElementSet
	 */	
	public static void addDependentElement(GeoElement geo, TreeSet consElementSet) {		 
		 AlgoElement algo = geo.getParentAlgorithm();
		 
	   	 if (algo.isInConstructionList()) {
	   		// STANDARD case
	   		// add algorithm
	   		consElementSet.add(algo);
	   		
	   		// add all output elements including geo
	   		GeoElement [] algoOutput = algo.getOutput();
	   		for (int i=0; i < algoOutput.length; i++) {
	   			consElementSet.add(algoOutput[i]);
	   		}	   		
	   	 } else {
			// HELPER algorithm, e.g. segment of polygon
	   		// we only add the geo because it is output 
	   		// of some other algorithm in construction list
	   		consElementSet.add(geo);
	   	 }
	}
	
	/**
	 * Adds the geo, its parent algorithm and all input of the parent algorithm to the consElementSet.
	 * This is used for e.g. a segment that is used as an input object of a macro. We also need to
	 * have the segment's start and endpoint.
	 */	
	public static void addSpecialInputElement(GeoElement geo, TreeSet consElementSet) {		 
		 // add geo
		 consElementSet.add(geo);
		 
		 // add parent algo and input objects
		 AlgoElement algo = geo.getParentAlgorithm();
	   	 if (algo.isInConstructionList()) {
	   		// STANDARD case
	   		// add algorithm
	   		consElementSet.add(algo);
	   		
	   		// add all output elements including geo
	   		GeoElement [] algoInput = algo.getInput();
	   		for (int i=0; i < algoInput.length; i++) {
	   			if (algoInput[i].isLabelSet())
	   				consElementSet.add(algoInput[i]);
	   		}	   		
	   	 }
	}
	
	/**
	 * Note: changes macroConsElements
	 * @param input
	 * @param macroConsElements
	 * @return
	 */
	 public static String buildMacroXML(TreeSet macroConsElements) {				 
    	// get the XML for all macro construction elements
    	StringBuffer macroConsXML = new StringBuffer(500);
    	macroConsXML.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
    	macroConsXML.append("<geogebra format=\"" + Application.XML_FILE_FORMAT  + "\">\n");
    	macroConsXML.append("<construction author=\"\" title=\"\" date=\"\">\n");
    	     	    	   
    	Iterator it = macroConsElements.iterator();
    	while (it.hasNext()) {    		
    		ConstructionElement ce = (ConstructionElement) it.next();
    		if (ce.isGeoElement()) {
    			macroConsXML.append(ce.getXML());
    		}
    		else if (ce.isAlgoElement()) {
    			AlgoElement algo = (AlgoElement) ce;
        		macroConsXML.append(algo.getXML(false));    			
    		}
    	}
    	
    	macroConsXML.append("</construction>\n");
    	macroConsXML.append("</geogebra>");
    	   
    	// TODO: remove
    	System.out.println("*** Macro XML BEGIN ***");
    	System.out.println(macroConsXML);
    	System.out.flush();
    	System.out.println("*** Macro XML END ***");
    	
    	
    	return macroConsXML.toString();
	 }
    	
	 /**
	  * Creates a macro construction from a given xml string. 
	  * The names of the input and output objects within this construction
	  * are given by inputLabels and outputLabels
	  * @param macroXML
	  */
	 private Construction createMacroConstruction(String macroConsXML) throws Exception {		 
    	// build macro construction
    	MacroKernel mk = new MacroKernel(kernel);   
    	mk.setContinuous(false);
    	mk.setGlobalVariableLookup(false);    	        	      	  	      	        	    	
    	
    	try {    	
    		mk.loadXML(macroConsXML);    		    			    		    	    	        	        	
        	return mk.getConstruction();
    	} 
    	catch (MyError e) {  
    		String msg = e.getLocalizedMessage();
    		System.err.println(msg);
    		e.printStackTrace(); 
    		throw new Exception(msg);
    	}    	
    	catch (Exception e) {
    		e.printStackTrace();       		   
        	throw new Exception(e.getMessage());
    	}    	
    }	 	                 
			
	
	public void registerAlgorithm(AlgoMacro algoMacro) {
		usingAlgos++;	
	}
	
	public void unregisterAlgorithm(AlgoMacro algoMacro) {
		usingAlgos--;		
	}		
	
	/**
	 * Returns whether this macro is being used by algorithms
	 * in the current construction.
	 */
	final public boolean isUsed() {	
		return usingAlgos >  0;
	}
	
	
	final public void setUnused() {	
		usingAlgos = 0;
	}
						
	/**
	 * Returns the types of input objects of the default macro construction.
	 * This can be used to check whether a given GeoElement array can be used
	 * as input for this macro.
	 */
	final public Class [] getInputTypes() {	
		return inputTypes;
	}			
	
	public String getToolHelp() {		
		return toolHelp;				
	}
	
	
	/**
	 * Returns a String showing all needed types of this macro.
	 */
	public String getNeededTypesString() {
		StringBuffer sb = new StringBuffer();			
        sb.append(macroInput[0].translatedTypeString());	       
        for (int i = 1; i < macroInput.length; ++i) {
            sb.append(", ");	            
            sb.append(macroInput[i].translatedTypeString());	            
        }	        			
		return sb.toString();				
	}

	public void setToolHelp(String toolHelp) {
		if (toolHelp == null)
			this.toolHelp = "";
		else
			this.toolHelp = toolHelp;
	}

	public String getCommandName() {
		return cmdName;
	}

	void setCommandName(String name) {
		if (name == null)
			this.cmdName = "";
		else
			this.cmdName = name;
	}

	public String getToolName() {		
		return toolName;
	}
	
	public String getToolOrCommandName() {
		if (toolName != "") 
			return toolName;
		else
			return cmdName;			
	}

	public void setToolName(String name) {
		if (name == null)
			this.toolName = "";
		else
			this.toolName = name;
	}
	
	public void setIconFileName(String name) {
		if (name == null)
			this.iconFileName = "";
		else
			this.iconFileName = name;
	}
	
	public String getIconFileName() {
		return iconFileName;
	}
	
	/**
	 * Returns the syntax descriptiont of this macro.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(cmdName);
        sb.append("[ ");
               
        // input types
        sb.append('<');
        sb.append(macroInput[0].translatedTypeString());
        sb.append('>');
        for (int i = 1; i < macroInput.length; ++i) {
            sb.append(", ");
            sb.append('<');
            sb.append(macroInput[i].translatedTypeString());
            sb.append('>');
        }
        sb.append(" ]");
		
		return sb.toString();
	}
	
	
	/**
	 * Returns XML representation of this macro for
	 * saving in a ggb file.
	 */
    public String getXML() {      
        StringBuffer sb = new StringBuffer();               
        sb.append("<macro cmdName=\"");
        sb.append(Util.encodeXML(cmdName));         
        sb.append("\" toolName=\"");
        sb.append(Util.encodeXML(toolName));
        sb.append("\" toolHelp=\"");
        sb.append(Util.encodeXML(toolHelp));  
        sb.append("\" iconFile=\""); 
        sb.append(Util.encodeXML(iconFileName));  
        sb.append("\" showInToolBar=\"");
        sb.append(showInToolBar);  
		sb.append("\">\n");
    			        
        // add input labels
        sb.append("<macroInput");
        for (int i = 0; i < macroInputLabels.length; i++) {
	    	// attribute name is input no. 
	        sb.append(" a");
	        sb.append(i);                
	        sb.append("=\"");
	        sb.append(Util.encodeXML(macroInputLabels[i]));                                                           
	        sb.append("\"");
        }
        sb.append("/>\n");
        
        // add output labels           
        sb.append("<macroOutput");
        for (int i = 0; i < macroOutputLabels.length; i++) {
	    	// attribute name is output no.
	        sb.append(" a");
	        sb.append(i);                
	        sb.append("=\"");
	        sb.append(Util.encodeXML(macroOutputLabels[i]));                                                           
	        sb.append("\"");
        }        
        sb.append("/>\n");            
        
        // macro construction XML
        sb.append(macroConsXML);    
        
        sb.append("</macro>\n");           
        return sb.toString();
    }

	public final boolean isShowInToolBar() {
		return showInToolBar;
	}

	public final void setShowInToolBar(boolean showInToolBar) {
		this.showInToolBar = showInToolBar;
	}
		  
}
