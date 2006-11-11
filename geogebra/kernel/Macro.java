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

import java.util.Iterator;
import java.util.TreeSet;

/**
 * A macro is a user defined commmand.
 * 
 * @author Markus Hohenwarter
 */
public class Macro {
	
	private Kernel kernel;
	private String name, description;
	private GeoElement [] macroInput, macroOutput;
		
	private TreeSet macroConsOrigElements;
	private Construction macroCons;
	
	/**
	 * Creates a new macro with a name and description by
	 * using the given input and output GeoElements.	 
	 */
	public Macro(Kernel kernel, String name, String description, 
					GeoElement [] input, GeoElement [] output) 
	throws Exception {
		this.kernel = kernel;
		this.name = name;
		this.description = description;
		
		initMacro(input, output);
	}
	
	/**
	 * Creates a new macro with a name and description by
	 * processing the given XML string and the given input and output labels.	 
	 */
	public Macro(Kernel kernel, String name, String description,
					String macroConsXML, String [] inputLabels, String [] outputLabels) 
	throws Exception {
		this.kernel = kernel;
		this.name = name;
		this.description = description;
		
		createMacroConstruction(macroConsXML, inputLabels, outputLabels);
	}
	
	private void initMacro(GeoElement [] input, GeoElement [] output)  throws Exception {
		// TODO: check dependence of input and output
		/*
		//check whether we found any macro elements:
    	// if not input and output elements don't depend on each other
    	if (macroConsOrigElements.size() == 0)
        	// TODO: localize error message
        	throw new Exception("InvalidMacroDefinition");
		*/
		
		// basic idea to create a macro
		// 1) outputParents = set of all predecessors of output objects
		// 2) inputChildren = set of all children of input objects
		// 3) macroElements = intersection of outputParents and inputChildren
		// 4) add input and output objects to macroElements
		// 5) create XML representation for macro-construction
		// 6) create a new macro-construction from this XML representation
		
		// 1) create the set of all parents of this macro's output objects
		TreeSet outputParents = new TreeSet();		
		for (int i=0; i < output.length; i++) {
			 output[i].addPredecessorsToSet(outputParents, false);
		}			
		
		// 2) and 3) get intersection of all inputChildren and all outputParents    	       	
    	macroConsOrigElements = new TreeSet(); 
    	Iterator it = outputParents.iterator();
    	while (it.hasNext()) {
    		GeoElement parent = (GeoElement) it.next();
    		if (parent.isLabelSet()) {
    			for (int i=0; i < input.length; i++) {
    				 if (parent.isChildOf(input[i])) {
    					 macroConsOrigElements.add(parent);
    					 i = input.length; // add parent only once: get out of loop
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
    	}    	    
    	for (int i=0; i < output.length; i++) {
    		isOutputLabeled[i] = output[i].isLabelSet();
    		if (!isOutputLabeled[i]) {
    			output[i].label = output[i].getDefaultLabel();
    			output[i].labelSet = true;    			
        	}
    		
    		// add labeled output elements to macroElements
    		macroConsOrigElements.add(output[i]);
    		outputLabels[i] = output[i].label;
    	}    	    	
    	    	
		// 5) create XML representation for macro-construction
    	String macroXML = buildMacroXML(input, macroConsOrigElements);
    	 	    
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
    	createMacroConstruction(macroXML, inputLabels, outputLabels);   
    }
	
	 private String buildMacroXML(GeoElement [] input, TreeSet macroConsElements) {        	
    	// get the XML for all macro construction elements
    	StringBuffer macroConsXML = new StringBuffer(500);
    	macroConsXML.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
    	macroConsXML.append("<geogebra format=\"" + Application.XML_FILE_FORMAT  + "\">\n");
    	macroConsXML.append("<construction author=\"\" title=\"\" date=\"\">\n");
    	
    	// get XML for input elements first 
    	for (int i=0; i < input.length; i++) {    		
    		macroConsXML.append(input[i].getXML());	    		
    	}   
    	
    	// get XML for the rest of the macro construction
    	Iterator it = macroConsElements.iterator();
    	while (it.hasNext()) {
    		GeoElement geo = (GeoElement) it.next();    		
    		if (geo.isIndependent())
    			macroConsXML.append(geo.getXML());
    		else 
    			macroConsXML.append(geo.getParentAlgorithm().getXML());
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
	 private void createMacroConstruction(String macroConsXML, String [] inputLabels, String [] outputLabels) throws Exception {		 
    	// build macro construction
    	MacroKernel mk = new MacroKernel(kernel);     	
    	    	
    	// tell the macro kernel that global variables are not allowed    	
    	mk.setGlobalVariableLookup(false);      	  	      	    
    	
    	try {    	
    		mk.loadXML(macroConsXML.toString());
    	
	    	// get the copies of input and output from the macro kernel
    		macroInput = new GeoElement[inputLabels.length];
    		macroOutput = new GeoElement[outputLabels.length];
    		for (int i=0; i < inputLabels.length; i++) {    		
    			macroInput[i] = mk.lookupLabel(inputLabels[i]);  
    			macroInput[i].setFixed(false);
    			
    			// TODO:remove
    			System.out.println("macroInput[" + i + "] = " + macroInput[i]);
        	}    	    
        	for (int i=0; i < outputLabels.length; i++) {    		
        		macroOutput[i] = mk.lookupLabel(outputLabels[i]);            		
        		
    			// TODO:remove
    			System.out.println("macroOutput[" + i + "] = " + macroOutput[i]);
        	}        			    		    	
	    	
        	macroCons = mk.getConstruction();	    	    	
    	} 
    	catch (MyError e) {  
    		String msg = e.getLocalizedMessage();
    		System.err.println(msg);
    		throw new Exception(msg);
    	}    	
    	catch (Exception e) {
    		e.printStackTrace();       		   
        	throw new Exception("");
    	}    	
    }
                 	
	/**
	 * Applies this macro to the given input objects and uses the result to set
	 * the given output objects.
	 */	
	public void applyMacro(GeoElement [] input, GeoElement [] output) {			
		// TODO: think about continuity and global vars
		/*
		// set all ellements in locusConsElements 
        // to the current values of the main construction    
      	Iterator it = locusConsOrigElements.iterator();
      	while (it.hasNext()) {
      		GeoElement geoOrig = (GeoElement) it.next();    		
      		GeoElement geoCopy = macroCons.lookupLabel(geoOrig.label);   
      		if (geoCopy != null) {
	  			try {
	  				geoCopy.set(geoOrig);    			
	  				geoCopy.update();      	      			 
	  			} catch (Exception e) {
	  				System.err.println("AlgoLocus: error in resetMacroConstruction(): " + e.getMessage());
	  			}
      		}
      	} */   	
		
		// use input objects to set macro construction   
		for (int i=0; i < macroInput.length; i++) {   
			macroInput[i].set(input[i]);						
    	}  
				
      	// update all algorithms of the macro construction	        
      	macroCons.updateConstruction();
      	
      	// set output objects to set macro construction   
		for (int i=0; i < macroOutput.length; i++) {    					 
			output[i].setInternal(macroOutput[i]);						
    	} 
	}
	
	/**
	 * Creates copies of all output elements of this macro. These copies can then be used
	 * to call applyMacro().
	 */	
	public GeoElement [] createOutputCopies() {
		GeoElement [] outputCopies = new GeoElement[macroOutput.length];
		
		// copy output objects  
		for (int i=0; i < macroOutput.length; i++) { 
			outputCopies[i] = macroOutput[i].copyInternal();
			outputCopies[i].setVisualStyle(macroOutput[i]);					
    	} 
		
		return outputCopies;
	}
	
	/**
	 * Returns an array of input objects for this macro.
	 * This can be used to check the
	 * @return
	 */
	public GeoElement [] getInputObjects() {
		return macroInput;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the syntax descriptiont of this macro.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
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
		
}
