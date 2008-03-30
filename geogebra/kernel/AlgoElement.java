/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoElement.java
 *
 * Created on 30. August 2001, 21:36
 */

package geogebra.kernel;

import geogebra.util.Util;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TreeSet;

/**
 *
 * @author  Markus
 * @version 
 */
public abstract class AlgoElement extends ConstructionElement 
implements EuclidianViewAlgo {
	 
    private static ResourceBundle rbalgo2command;
    
    GeoElement[] input, output;
    private GeoElement [] efficientInput;
    
    // numbers among input objects of algorithm that are used within random()
    private GeoNumeric [] randomInputNumbers;
    
    private boolean isPrintedInXML = true;
    private boolean stopUpdateCascade = false;
    
    public AlgoElement(Construction c) {
        super(c);                       
        c.addToConstructionList(this, false);                 
    }
    
    private String getCommandString(String classname) {
        // init rbalgo2command if needed
        // for translation of Algo-classname to command name
        if (rbalgo2command == null) {
        	rbalgo2command = app.initAlgo2CommandBundle();
        }
            	
    	// translate algorithm class name to internal command name
    	return rbalgo2command.getString(classname);
    }
    
    // in setInputOutput() the member vars input and output are set
    abstract void setInputOutput();

    // in compute() the output ist derived from the input
    abstract void compute();       

    /**
     * Inits this algorithm for the near-to-relationship. This
     * is important to init the intersection algorithms  when
     * loading a file, so that they have a look at the current
     * location of their output points. 
     */
    void initForNearToRelationship() {}
    
    public boolean isNearToAlgorithm() {
    	return false;
    }
    
//    public static double startTime, endTime;
//    public static double computeTime, updateTime;
//    public static double counter;
    
    void update() {
    	if (stopUpdateCascade) return;
    	
//    	counter++;
//    	startTime = System.currentTimeMillis(); 
    		
    	// update possible random values used by this algorithm
    	if (randomInputNumbers != null) {
    		updateRandomInputNumbers();
    	}
    	
        // compute output from input
        compute();
        
//        endTime = System.currentTimeMillis(); 
//        computeTime += (endTime - startTime);
    	//startTime = System.currentTimeMillis(); 
    	
        // update dependent objects 
        for (int i = 0; i < output.length; i++) {           
                output[i].update();
        }           
        
//        endTime = System.currentTimeMillis(); 
//        updateTime += (endTime - startTime );                
    }              
    
    /**
     * Updates all random numbers of this algorithm (if there
     * are any).
     */
    public boolean updateRandomAlgorithm() {
    	boolean doUpdate = randomInputNumbers != null;
    	if (doUpdate) 
    		update();
    	return doUpdate;
    }

    // public part    
    final public GeoElement[] getOutput() {
        return output;
    }
    final public GeoElement[] getInput() {
        return input;
    }

    /**
     *  DEPENDENCY handling 
     *  the dependencies are treated here
     *  by using input and output which must be set by
     *  every algorithm. Note: setDependencies() is
     *  called by every algorithm in topological order
     *  (i.e. possible helper algos call this method before
     *  the using algo does).
     * @see setInputOutput()
     */
    final void setDependencies() {       	      	      	
        // dependents on input
        for (int i = 0; i < input.length; i++) {
            input[i].addAlgorithm(this);            
        }    
        
        initRandomInputNumbers();
        setOutputDependencies();                
        cons.addToAlgorithmList(this); 
    }
    
   
    
    final void setEfficientDependencies(GeoElement [] standardInput, GeoElement [] efficientInput) {   	
    	// dependens on standardInput
        for (int i = 0; i < standardInput.length; i++) {
        	standardInput[i].addToAlgorithmListOnly(this);            
        }
    	
    	// we use efficientInput for updating
        for (int i = 0; i < efficientInput.length; i++) {
        	efficientInput[i].addToUpdateSetOnly(this);            
        }
        
        // input is standardInput
        input = standardInput;
        this.efficientInput = efficientInput;
        initRandomInputNumbers();
        setOutputDependencies();
        cons.addToAlgorithmList(this); 
    }
    
    /**
     * Creates a list of all GeoNumeric objects that
     * are used as values for random() 
     */
    private void initRandomInputNumbers() {
    	// look for random numbers
 		ArrayList randNumList = null;    	
        for (int i = 0; i < input.length; i++) {
            if (input[i].isGeoNumeric()) {
            	GeoNumeric num = (GeoNumeric) input[i];
            	if (num.isUsedForRandom()) {
            		if (randNumList == null)
            			randNumList = new ArrayList();
            		randNumList.add(num);            		
            	}
            }
        }    
        
        // init randomGeoNumerics array
        if (randNumList != null) {
        	randomInputNumbers = new GeoNumeric[randNumList.size()];
        	for (int i = 0; i < randomInputNumbers.length; i++) {
        		randomInputNumbers[i] = (GeoNumeric) randNumList.get(i);
        	}
        }        
    }
    
    /**
     * Sets the array of all GeoNumeric objects that
     * are need to be randomized. This is needed when there an
     * algorithm needs a random number that is not one of it's input objects.
     */
    void setRandomInputNumbers(GeoNumeric [] randNumbers) {
    	randomInputNumbers = randNumbers;    		
    }
    
    /**
     * Sets the array of random input numbers (GeoNumeric objects with
     * isUsedForRandom() returns true). Those numbers will be set using
     * Math.random() every time before the algorithm is updated. 
     * Usually this method does not need
     * to be called (see initRandomInputNumbers(). Use this method only
     * when you need to use random numbers as helper objects in your 
     * algorithm. 
     *
    void setRandomInputNumbers(GeoNumeric [] randNums) {
    	randomInputNumbers = randNums;
    }*/
    
    private void updateRandomInputNumbers() {
    	for (int i = 0; i < randomInputNumbers.length; i++) {
    		randomInputNumbers[i].setValue(Math.random());
    	}
    }
    
    private void setOutputDependencies() {
   	 // parent algorithm of output
       for (int i = 0; i < output.length; i++) {
           output[i].setParentAlgorithm(this);
           
           // every algorithm with an image as output
           // should be notified about view changes
           if (output[i].isGeoImage())
        	   kernel.registerEuclidianViewAlgo(this);
           
           //  make sure that every output has same construction as this algorithm
           // this is important for macro constructions that have input geos from
           // outside the macro: the output should be part of the macro construction!
           if (cons != output[i].cons)
           	output[i].setConstruction(cons);             
       }   
   }
   
    
    public void euclidianViewUpdate() {
    	compute();
    }
       
    public void remove() {      
        cons.removeFromConstructionList(this);
        
        if (this instanceof EuclidianViewAlgo)
        	kernel.unregisterEuclidianViewAlgo(this);
                        
        // delete dependent objects        
        for (int i = 0; i < output.length; i++) {
            output[i].doRemove();
        }
                
        // delete from algorithm lists of input                
        for (int i = 0; i < input.length; i++) {
            input[i].removeAlgorithm(this);
        }          
        
        if (efficientInput != null) {
        	// delete from algorithm lists of input                
            for (int i = 0; i < efficientInput.length; i++) {
            	efficientInput[i].removeAlgorithm(this);
            }  
        }
    }
    
    /**
     * Tells this algorithm to react on the deletion
     * of one of its outputs. 
     */
    void remove(GeoElement output) {
    	remove();
    }
    
    /**
     * Calls doRemove() for all output objects of this
     * algorithm except for keepGeo.
     */
    void removeOutputExcept(GeoElement keepGeo) {
    	for (int i=0; i < output.length; i++) {
            GeoElement geo = output[i];
            if (geo != keepGeo) 
            	geo.doRemove();
        }
    }

    /**
     * Tells all views to add all output GeoElements of this algorithm. 
     */
    final public void notifyAdd() {
        for (int i = 0; i < output.length; ++i) {
            output[i].notifyAdd();
        }
    }

    /**
     * Tells all views to remove all output GeoElements of this algorithm. 
     */
    final public void notifyRemove() {
        for (int i = 0; i < output.length; ++i) {
            output[i].notifyRemove();
        }
    }

    final public GeoElement[] getGeoElements() {
        return output;
    }
    
    /** 
     * Returns whether all output objects have
     * the same type.     
     */
    final public boolean hasSingleOutputType() {
    	int type = output[0].getGeoClassType();
    	
    	 for (int i = 1; i < output.length; ++i) {
            if (output[i].getGeoClassType() != type)
            	return false;
         }    	
    	 return true;
    }
    
    final public boolean isAlgoElement() {
        return true;
    }

    final public boolean isGeoElement() {
        return false;
    }      

	/**
	 * Returns true iff one of the output geos is shown
	 * in the construction protocol	 
	 */
	final public boolean isConsProtocolBreakpoint() {
		for (int i=0; i < output.length; i++) {
			if (output[i].isConsProtocolBreakpoint())
				return true;
		}
		return false;
	}
    
    /**
     * Returns construction index in current construction.
     * For an algorithm that is not in the construction list, the largest construction
     * index of its inputs is returned.
     */
    public int getConstructionIndex() {
        int index =  super.getConstructionIndex();
        // algorithm is in construction list
        if (index >= 0) return index;
        
        // algorithm is not in construction list
        for (int i=0; i < input.length; i++) {
            int temp = input[i].getConstructionIndex();
            if (temp > index) index = temp;
        }
        return index;
    }    
    
    /**
     * Returns the smallest possible construction index for this object in its construction.
     */
    public int getMinConstructionIndex() {    	
        // index must be greater than every input's index
    	int max = 0;
        for (int i=0; i < input.length; ++i) {
            int index = input[i].getConstructionIndex();
            if (index > max) max = index;
        }
        return max+1;
    }
    
    /**
     * Returns the largest possible construction index for this object in its construction.
     */ 
    public int getMaxConstructionIndex() {            	
         // index is less than minimum of all dependent algorithm's index of all output
         ArrayList algoList;
         int size, index;
         int min = cons.steps();                        
         for (int k=0; k < output.length; ++k) {
            algoList = output[k].getAlogrithmList();
            size = algoList.size();                                  
            for (int i=0; i < size; ++i) {                          
                 index = ((AlgoElement)algoList.get(i)).getConstructionIndex();
                 if (index < min) min = index;
            }
         }       
         return min-1;              
    }
    
    /**
     * Returns all independent predecessors (of type GeoElement) that this algo depends on.
     * The predecessors are sorted topologically.
     */
    final public TreeSet getAllIndependentPredecessors() {
        //  return predecessors of any output, i.e. the inputs of this algo
        TreeSet set = new TreeSet();
        addPredecessorsToSet(set, true);
        return set;
    }
    
    //  adds all predecessors of this object to the given list
    // the set is kept topologically sorted 
    // @param onlyIndependent: whether only indpendent geos should be added
    final void addPredecessorsToSet(TreeSet set, boolean onlyIndependent) {
        for (int i = 0; i < input.length; i++) {
            GeoElement parent = input[i];

            if (!onlyIndependent) {
            	// OLD: list implementation
	                //  move or insert parent at the beginning of the list
	                 // this ensures the topological sorting
	                 //list.remove(parent);
	                 //list.addFirst(parent);       
                 
                 set.add(parent);
            }
            parent.addPredecessorsToSet(set, onlyIndependent);
        }
    }
    
    /**
	 * Returns all moveable input points of this algorithm.	 
	 */   
    public ArrayList getFreeInputPoints() {
		if (freeInputPoints == null) {				
			freeInputPoints = new ArrayList(input.length);
			for (int i=0; i < input.length; i++) {				
				if (input[i].isGeoPoint() && input[i].isIndependent())
					freeInputPoints.add(input[i]);			
			}				
		}
	
		return freeInputPoints;
    }
    private ArrayList freeInputPoints;
    
    /**
	 * Returns all input points of this algorithm.	 
	 */
    public ArrayList getInputPoints() {	
    	if (inputPoints == null) {
			inputPoints = new ArrayList(input.length);
			for (int i=0; i < input.length; i++) {			
				if (input[i].isGeoPoint() )
					inputPoints.add(input[i]);			
			}	
    	}
		
		return inputPoints;
    }
    private ArrayList inputPoints;

    final public boolean isIndependent() {
        return false;
    }

    public String getNameDescription() {
        StringBuffer sb = new StringBuffer();
        if (output[0].isLabelSet()) sb.append(output[0].getNameDescription());
        for (int i = 1; i < output.length; ++i) {
            if (output[i].isLabelSet()) {
                sb.append("\n");
                sb.append(output[i].getNameDescription());              
            }                           
        }
        return sb.toString();
    }
    


    public String getAlgebraDescription() {
        StringBuffer sb = new StringBuffer();
        
        if (output[0].isLabelSet()) sb.append(output[0].getAlgebraDescription());       
        for (int i = 1; i < output.length; ++i) {
            if (output[i].isLabelSet()) {
                sb.append("\n");
                sb.append(output[i].getAlgebraDescription());               
            }           
        }
        return sb.toString();
    }

    public String getDefinitionDescription() {
        return toString();
    }    
        
    public String getCommandDescription() {
        String cmdname = getCommandName();          
        
        //      command name
        if (cmdname.equals("Expression"))
			return toString();
		else {
            StringBuffer sb = new StringBuffer();
            if (kernel.isTranslateCommandName()) {
                sb.append(app.getCommand(cmdname));        
            } else {
                sb.append(cmdname);
            } 

            sb.append("[");
            // input
            sb.append(input[0].getLabel());
            for (int i = 1; i < input.length; ++i) {
                sb.append(", ");
                sb.append(input[i].getLabel());
            }
            sb.append("]");
            return sb.toString();           
        }       
    }

    /**
     * translate class name to internal command name
     */
    String getCommandName() {
        String cmdname, classname;
        // get class name
        //classname = this.getClass().toString();
        //classname = classname.substring(classname.lastIndexOf('.') + 1);
        classname = getClassName();
        // dependent algorithm is an "Expression"
        if (classname.startsWith("AlgoDependent")) {
            cmdname = "Expression";
        } else {
            // translate algorithm class name to internal command name
            cmdname = getCommandString(classname);
        }
        return cmdname;
    }   

    /**
     * Returns this algorithm and it's output objects (GeoElement) in XML format.
     */
    final public String getXML() {
    	return getXML(true);
    }
    	
    final String getXML(boolean includeOutputGeos) {  
        // this is needed for helper commands like 
        // intersect for single intersection points
        if (!isPrintedInXML) return ""; 
        
        // USE INTERNAL COMMAND NAMES IN EXPRESSION        
        boolean oldValue = kernel.isTranslateCommandName();
        kernel.setTranslateCommandName(false);             
        int oldDigits = kernel.getMaximumFractionDigits();
        kernel.setMaximumFractionDigits(50);
        
        StringBuffer sb = new StringBuffer();
        
        try {
	        // command
	        String cmdname = getCommandName();
	        if (cmdname.equals("Expression"))
	            sb.append(getExpXML());
	        else
	            sb.append(getCmdXML(cmdname));
	        
	        if (includeOutputGeos) {	       
		        // output               
		        GeoElement geo;                   
		        for (int i = 0; i < output.length; i++) {
		            geo = output[i];
		            // save only GeoElements that have a valid label
		            if (geo.isLabelSet()) {
		                sb.append(geo.getXML());
		            }
		        }
	        }            
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        kernel.setMaximumFractionDigits(oldDigits);
        kernel.setTranslateCommandName(oldValue);
        
        return sb.toString();
    }

    // Expressions should be shown as out = expression
    // e.g. <expression label="u" exp="a + 7 b"/>
    private String getExpXML() {                
        StringBuffer sb = new StringBuffer();        
        sb.append("<expression");
        // add label
        if (output != null && output.length == 1) {
            if (output[0].isLabelSet()) {
                sb.append(" label=\"");
                sb.append(Util.encodeXML(output[0].getLabel()));
                sb.append("\"");
            }
        } 
        // add expression        
        sb.append(" exp=\"");       
        sb.append(Util.encodeXML(toString()));
        sb.append("\"");
        
        // make sure that a vector remains a vector and a point remains a point
        if (output[0].isGeoPoint()) {
            sb.append(" type=\"point\"");
        } else if (output[0].isGeoVector()) {
            sb.append(" type=\"vector\"");
        }
        
        // expression   
        sb.append(" />\n");
        
        return sb.toString();
    }

    // standard command has cmdname, output, input
    private String getCmdXML(String cmdname) {      
        StringBuffer sb = new StringBuffer();
        sb.append("<command name=\"");
            sb.append(cmdname);
            sb.append("\"");    
                sb.append(">\n");
        
        // add input information
        if (input != null) {
            sb.append("\t<input");
            for (int i = 0; i < input.length; i++) {
                sb.append(" a");
                sb.append(i);
                // attribute name is input No. 
                sb.append("=\"");
                
                // ensure a vector stays a vector!               
                if (input[i].isGeoVector() && !input[i].isLabelSet()) {
                    // add Vector[ ] command around argument
                    // to make sure that this really becomes a vector again
                    sb.append("Vector[");
                        sb.append(Util.encodeXML(input[i].getLabel()));
                    sb.append("]");
                } else {
                	// standard case
                    sb.append(Util.encodeXML(input[i].getLabel()));                 
                }                       
                        
                sb.append("\"");
            }
            sb.append("/>\n");
        } 
        
        // add output information    
        if (output != null) {
            sb.append("\t<output");
            for (int i = 0; i < output.length; i++) {
                sb.append(" a");
                sb.append(i);
                // attribute name is output No. 
                sb.append("=\"");
                if (output[i].isLabelSet())
                    sb.append(Util.encodeXML(output[i].getLabel()));
                sb.append("\"");
            }
            
            sb.append("/>\n");
        }
        
        sb.append("</command>\n");      
        return sb.toString();
    }
    
    /**
     * Sets whether the output of this command should
     * be labeled. This setting is used for getXML().
     */
    void setPrintedInXML(boolean flag) {
        isPrintedInXML = flag;
        if (flag)
            cons.addToConstructionList(this, true);
        else 
            cons.removeFromConstructionList(this);
    }
    
    boolean isPrintedInXML() {
        return isPrintedInXML;
    }
    
    public String toString() {
    	return getCommandDescription();
    }

	final boolean doStopUpdateCascade() {
		return stopUpdateCascade;
	}

	final void setStopUpdateCascade(boolean stopUpdateCascade) {
		this.stopUpdateCascade = stopUpdateCascade;
	}

}
