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
import geogebra.io.MyXMLio;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.optimization.ExtremumFinder;
import geogebra.util.FastHashMapKeyless;
import geogebra.util.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * A Construction consists of a construction list with objects of type 
 * ConstructionElement (i.e. GeoElement or AlgoElement) and a GeoElement 
 * table with (String label, GeoElement geo) pairs. Every ConstructionElement
 * is responsible to add or remove itself from the construction list. Every 
 * GeoElement is responsible to add or remove itself from the GeoElement
 * table. 
 * 
 * To remove a ConstructionElement ce form its construction call ce.remove(); 
 * 
 * @author Markus Hohenwarter
 */
public class Construction {		
    
    private String title, author, date;
    // text for dynamic worksheets: 0 .. above, 1 .. below
    private String [] worksheetText = new String[2];

    //  ConstructionElement List (for objects of type ConstructionElement)
    private ArrayList ceList;
    
    //  AlgoElement List (for objects of type AlgoElement)
    private ArrayList algoList; // used in updateConstruction()
    
    // GeoElementTable for (label, GeoElement) pairs    
    protected HashMap geoTable, localVariableTable;  

    // set with all labeled GeoElements in ceList order
    private TreeSet geoSet;
    
    // set with all labeled GeoElements in alphabetical order
    private TreeSet geoSetLabelOrder;
    
    // a map for sets with all labeled GeoElements in alphabetical order of specific types
    // (points, lines, etc.)
    private FastHashMapKeyless geoSetsTypeMap;

    // UndoManager
    protected UndoManager undoManager;

    // current construction step (-1 ... ceList.size() - 1)
    // step == -1 shows empty construction
    protected int step;
    
    // in macro mode no new labels or construction elements
    // can be added
    private boolean macroMode = false;
    
    // showOnlyBreakpoints in construction protocol
    private boolean showOnlyBreakpoints;   

    // member vars
    int angleUnit = Kernel.ANGLE_DEGREE;
    
    // construction belongs to kernel
    private Kernel kernel;
  
    // axis objects
    private GeoAxis xAxis, yAxis;
    private String xAxisLocalName, yAxisLocalName;

    // default elements
    private ConstructionDefaults consDefaults;   
    
    /**
     * Creates a new Construction.   
     */
    public Construction(Kernel k) {
    	this(k, null);   	
    }
    
    Construction(Kernel k, Construction parentConstruction) {
        kernel = k;
        
        ceList = new ArrayList(200);
        algoList = new ArrayList();
        step = -1;
                
        geoSet = new TreeSet();
        geoSetLabelOrder = new TreeSet(new LabelComparator());
        geoSetsTypeMap = new FastHashMapKeyless(50);
        
        if (parentConstruction != null)
        	consDefaults = parentConstruction.getConstructionDefaults();
        else
        	consDefaults = new ConstructionDefaults(this);
        
        xAxis = new GeoAxis(this, GeoAxis.X_AXIS);    
    	yAxis = new GeoAxis(this, GeoAxis.Y_AXIS);
    	
    	geoTable = new HashMap(200);
    	localVariableTable = new HashMap();
    	initGeoTable();   
    }
    
    /**
     * Returns the last GeoElement object in the construction list;
     */
    public GeoElement getLastGeoElement() {	
    	if (geoSet.size() > 0)
    		return (GeoElement) geoSet.last();
    	else 
    		return null;
    }           
    
    /**
     * Returns the construction default object of this 
     * construction.
     */
    final public ConstructionDefaults getConstructionDefaults() {    	
    	return consDefaults;
    }              
    
    private void initGeoTable() {
    	geoTable.clear();
    	geoTable.put("xAxis", xAxis);
    	geoTable.put("yAxis", yAxis);    	
    	updateLocalAxesNames();	
    }
    
    public void updateLocalAxesNames() {
    	geoTable.remove(xAxisLocalName);
    	geoTable.remove(yAxisLocalName);
    	
    	Application app = kernel.getApplication();
    	xAxisLocalName = app.getPlain("xAxis");
    	yAxisLocalName = app.getPlain("yAxis");
    	geoTable.put(xAxisLocalName, xAxis);
    	geoTable.put(yAxisLocalName, yAxis);
    }
        
    
    public Kernel getKernel() {
        return kernel;
    }
    
    public Application getApplication() {
        return kernel.getApplication();
    }
    
    public EquationSolver getEquationSolver() {
        return kernel.getEquationSolver();
    }
    
    public ExtremumFinder getExtremumFinder() {
        return kernel.getExtremumFinder();
    }
    
    final public GeoAxis getXAxis() {
        return xAxis;
    }
    
    final public GeoAxis getYAxis() {
        return yAxis;
    }  
    
    /**
     * If this is set to true new construction elements
     * won't get labels.
     */
    public void setSuppressLabelCreation(boolean flag) {
        macroMode = flag;
    }
    
    public boolean isSuppressLabelsActive() {
        return macroMode;
    }
    
    /**
     * Sets how steps in the construction protocol are
     * handled.
     */
	public void setShowOnlyBreakpoints(boolean flag) {
		 showOnlyBreakpoints = flag;
	}
	
	final public boolean showOnlyBreakpoints() {
		 return showOnlyBreakpoints;
	}

    /* *****************************
     *  Construction List Management
     *  *****************************/
     
    /**
     * Returns the ConstructionElement for the given construction index.
     */
    public ConstructionElement getConstructionElement(int index) {
        if (index < 0 || index >= ceList.size()) return null;
        return (ConstructionElement) ceList.get(index);
    }
        
    /**
     * Returns a set with all labeled GeoElement objects of this construction
     * in construction order.     
     */
    final public TreeSet getGeoSetConstructionOrder() {
    	return geoSet;
    }        
    
    /**
     * Returns a set with all labeled GeoElement objects of this construction
     * in alphabetical order of their labels.     
     */
    final public TreeSet getGeoSetLabelOrder() {
    	return geoSetLabelOrder;
    }
    
    /**
     * Returns a set with all labeled GeoElement objects of a specific type
     * in alphabetical order of their labels. 
     * @param geoClassType: use GeoElement.GEO_CLASS_* constants
     */
    final public TreeSet getGeoSetLabelOrder(int geoClassType) {
    	TreeSet typeSet = (TreeSet) geoSetsTypeMap.get(geoClassType);
    	if (typeSet == null) {
    		typeSet = createTypeSet(geoClassType);
    	} 
    	return typeSet;
    }
    
    /**
	 * Returns a set with all labeled GeoElement objects sorted
     * in alphabetical order of their type strings and labels
     * (e.g. Line g, Line h, Point A, Point B, ...).  
     * Note: the returned TreeSet is a copy of the current
     * situation and is not updated by the construction later on.
	 */
	final public TreeSet getGeoSetNameDescriptionOrder() {
		// sorted set of geos
		TreeSet sortedSet = new TreeSet(new NameDescriptionComparator());
		
		// get all GeoElements from construction and sort them		
		Iterator it = geoSet.iterator();	
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();
			// sorted inserting using name description of geo
			sortedSet.add(geo);			
		}	
		return sortedSet;
	}
    
    /**
     * Adds the given Construction Element to this Construction at position
     * getStep() + 1.    
     * @param checkContains: true to first check if ce is already in list 
     */
    public void addToConstructionList(ConstructionElement ce, boolean checkContains) {      	
    	if (macroMode) return;        
        if (checkContains && ce.isInConstructionList()) return;
        
        ++step;
        ceList.add(step, ce);            
        updateConstructionIndex(step);                     
    }     
    
    /**
      * Removes the given Construction Element from this Construction and updates
      * step if necessary (i.e. if ce.getConstructionIndex() <= getStep()).
      */
    public void removeFromConstructionList(ConstructionElement ce) {
        int pos = ceList.indexOf(ce);             
        if (pos == -1)
            return;
        else if (pos <= step) {
            ceList.remove(ce);
            ce.setConstructionIndex(-1);
            //setStep(step-1);
            --step;
        } else { // pos > step
            ceList.remove(ce);
            ce.setConstructionIndex(-1);
        }        
                
        updateConstructionIndex(pos);                
    }
       
    /**
     * Adds the given algorithm to this construction's algorithm list
     * @see updateConstruction() 
     */
    void addToAlgorithmList(AlgoElement algo) {
    	algoList.add(algo);          	
    }        
    
    /**
     * Moves geo to given position toIndex in this construction.
     * Note: if ce (or its parent algorithm) is not in the construction list nothing is done.
     * @return whether construction list was changed or not.
     */
    private boolean moveInConstructionList(GeoElement geo, int toIndex) {
        AlgoElement algoParent = geo.getParentAlgorithm(); 
        int fromIndex = (algoParent == null) ? ceList.indexOf(geo) : ceList.indexOf(algoParent);
        if (fromIndex >= 0)
            return moveInConstructionList(fromIndex, toIndex);
        else
            return false;
    }
    
    /**
     * Moves object at position from to position to in this construction.
     * @return whether construction list was changed or not.
     */
    public boolean moveInConstructionList(int fromIndex, int toIndex) {
        // check if move is possible
        ConstructionElement ce = (ConstructionElement) ceList.get(fromIndex);
        boolean change = fromIndex != toIndex && 
                        ce.getMinConstructionIndex() <= toIndex &&
                        toIndex <= ce.getMaxConstructionIndex();                        
        if (change) {
                // move the construction element
                ceList.remove(fromIndex);
                ceList.add(toIndex, ce);
                
                // update construction indices
                updateConstructionIndex(Math.min(toIndex, fromIndex));
                
                // update construction step
                if (fromIndex <= step && step < toIndex) {
                    --step;
                    ce.notifyRemove();
                }
                else if (toIndex <= step && step < fromIndex) {
                    ++step;
                    ce.notifyAdd();
                }                                                       
        }       
        return change;
    }
    
    // update all indices >= pos
    private void updateConstructionIndex(int pos) {
        if (pos < 0) return;
        int size = ceList.size();
        for (int i = pos; i < size; ++i) {
            ((ConstructionElement)ceList.get(i)).setConstructionIndex(i);
        }
    }
    
    /**
     * Returns true iff geo is independent and in the construction
     * list or geo is dependent and its parent algorithm is in
     * the construction list.
     * @param ce
     * @return
     */    
    public boolean isInConstructionList(GeoElement geo) {
    	if (geo.isIndependent())
    		return geo.isInConstructionList();
    	else 
    		return geo.getParentAlgorithm().isInConstructionList();
    }

    /**
     * Calls remove() for every ConstructionElement in the construction list.
     * After this the construction list will be empty.   
     */
    public void clearConstruction() {
        ceList.clear();
        algoList.clear();
        geoSet.clear();
        geoSetLabelOrder.clear();
        geoSetsTypeMap.clear();
        localVariableTable.clear();
        initGeoTable();
        
        // reinit construction step
        step = -1;
        
        // delete title, author, date
        title = null;
        author = null;
        date = null;
        worksheetText[0] = null;
        worksheetText[1] = null;
    }
    
    /**
     * Updates all objects in this construction.
     */
    final public void updateConstruction() { 
    	// update all independet GeoElements    	    	
    	 int size = ceList.size();   
         for (int i = 0; i < size; ++i) {
             ConstructionElement ce = (ConstructionElement) ceList.get(i);
             if (ce.isIndependent()) {	             
             	ce.update();                    
             }
         }    
         
         // init and update all algorithms
         size = algoList.size();
         for (int i = 0; i < size; ++i) {        	        	
        	 AlgoElement algo = (AlgoElement)algoList.get(i);           	 	
        	 algo.initForNearToRelationship();
             algo.update();                         	
         }    
    }   
        
    final void updateAllAlgorithms() {
    	// update all algorithms
        int size = algoList.size();
        for (int i = 0; i < size; ++i) {        	        	
       	AlgoElement algo = (AlgoElement)algoList.get(i);   
            algo.update();                         	
        }  
    }
    
    
    /**
     * Build a set with all algorithms of this construction (in topological order). 
     * The method updateAll() of this set can be used to update
     * the whole construction.
     *
    public AlgorithmSet buildOveralAlgorithmSet() {
        // 1) get all independent GeoElements in construction and update them
        // 2) build one overall updateSet from all updateSets of (1)       
                
        // 1) get all independent geos in construction
        LinkedHashSet indGeos = new LinkedHashSet();
        int size = ceList.size();   
        for (int i = 0; i < size; ++i) {
            ConstructionElement ce = (ConstructionElement) ceList.get(i);
            if (ce.isIndependent())
                indGeos.add(ce);
            else {
                indGeos.addAll(ce.getAllIndependentPredecessors());
            }
        }       
        
        // 2) build one overall updateSet
        AlgorithmSet algoSet = new AlgorithmSet();
        Iterator it = indGeos.iterator();
        while (it.hasNext()) {
            GeoElement geo = (GeoElement) it.next();
            
            // update this geo only
            geo.update();
            
            // get its update set and add it to the overall updateSet
            algoSet.addAll(geo.getAlgoUpdateSet());
        }               
        
        return algoSet;
    }       */

    /**
     * Tests if this construction has no elements. 
     * @return true if this construction has no GeoElements; false otherwise.
     */
    public boolean isEmpty() {
        return ceList.isEmpty();
    }

    /**
     * Returns the total number of construction steps.   
     */
    public int steps() {
        return ceList.size();
    }

    /**
     * Sets construction step position. Objects 0 to step in the construction
     * list will be visible in the views, objects step+1 to the end will be hidden. 
     * @param s: step number from range -1 ... steps()-1 where
     * -1 shows an empty construction.
     */
    public void setStep(int s) {
        if (s == step || s < -1 || s >= ceList.size())
			return;

        if (s < step) {
            for (int i = s + 1; i <= step; ++i)
                 ((ConstructionElement) ceList.get(i)).notifyRemove();
        } else {
            for (int i = step + 1; i <= s; ++i)
                 ((ConstructionElement) ceList.get(i)).notifyAdd();
        }
        step = s;
    }

    /**
     * Returns current construction step position.   
     */
    public int getStep() {
        return step;
    }

    /* *****************************
     *  GeoElementTable Management
     *  *****************************/
    /**
     * Adds given GeoElement to a table
     * where (label, object) pairs are stored.
     * @see removeLabel(), lookupLabel()
     */
    public void putLabel(GeoElement geo) {              
        if (macroMode || geo.label == null) return;
        
        geoTable.put(geo.label, geo);
        addToGeoSets(geo);        
    }       

    /**
      * Removes given GeoElement from a table
      * where (label, object) pairs are stored.
      * @see putLabel()
      */
    public void removeLabel(GeoElement geo) {
        geoTable.remove(geo.label);
        removeFromGeoSets(geo);               
    }
    
    private void addToGeoSets(GeoElement geo) {
    	geoSet.add(geo);
    	geoSetLabelOrder.add(geo);
    	
    	// get ordered type set
    	int type = geo.getGeoClassType();    	
    	TreeSet typeSet = (TreeSet) geoSetsTypeMap.get(type);
    	if (typeSet == null) {
    		typeSet = createTypeSet(type);
    	} 
    	typeSet.add(geo);	
    	
    	
    	 /*      
        System.out.println("*** geoSet order (add " + geo + ") ***");
        Iterator it = geoSet.iterator();
      
        while (it.hasNext()) {
        	GeoElement g = (GeoElement) it.next();
        	System.out.println(g.getConstructionIndex() + ": " + g);
        }
        */
    }
    
    private TreeSet createTypeSet(int type) {
    	TreeSet typeSet = new TreeSet(new LabelComparator());
		geoSetsTypeMap.put(type, typeSet);
		return typeSet;
    }
    
    private void removeFromGeoSets(GeoElement geo) {
    	geoSet.remove(geo);
    	geoSetLabelOrder.remove(geo);    	    	
    	
    	// set ordered type set
    	int type = geo.getGeoClassType();    	
    	TreeSet typeSet = (TreeSet) geoSetsTypeMap.get(type);
    	if (typeSet != null)
    		typeSet.remove(geo);    		    	

   	 /*
       System.out.println("*** geoSet order (remove " + geo + ") ***");
       Iterator it = geoSet.iterator();
       int i = 0;
       while (it.hasNext()) {
       	GeoElement g = (GeoElement) it.next();
       	System.out.println(g.getConstructionIndex() + ": " + g);
       }
       */
    }
    	
    final public void addLocalVariable(String varname, GeoElement geo) {    	
    	 localVariableTable.put(varname, geo);
    	 geo.setLocalVariableLabel(varname); 
    }
    
    final public void removeLocalVariable(String varname) {
   	 	localVariableTable.remove(varname);   	 	   	  	  
   	}        			

    /**
     * Returns a GeoElement for the given label. Note: only geos with
     * construction index 0 to step are available.
     * @return may return null
     */
     public GeoElement lookupLabel(String label) {    	          	
        if (label == null)
            return null;
        
        // local var handling
        if (!localVariableTable.isEmpty()) {        	
        	GeoElement localGeo = (GeoElement) localVariableTable.get(label);        
            if (localGeo != null) return localGeo;
        }
                
        // global var handling        
        GeoElement geo =  (GeoElement) geoTable.get(label); 
        if (geo == null) 
        	return null;       
        
        //  check if geo is available for current step
        if (geo.isAvailableAtConstructionStep(step))
			return geo;
		else
			return null;           
    }
    

    /**
       * Returns true if label is not occupied by any 
       * GeoElement.
       */
    public boolean isFreeLabel(String label) {
        if (label == null)
			return false;
		else
            return !geoTable.containsKey(label);
    }

    /* ******************************
     * redo / undo 
     * ******************************/

    /**
     * Clears the undo info list of this construction and adds the current construction
     * state to the undo info list.
     */
    public void initUndoInfo() {
        if (undoManager == null) undoManager = new UndoManager(this);
        undoManager.initUndoInfo();
    }

    public void storeUndoInfo() {
        undoManager.storeUndoInfo();
    }

    public void restoreCurrentUndoInfo() {
    	if (undoManager != null)
    		undoManager.restoreCurrentUndoInfo();
    }

    public void redo() {
        undoManager.redo();
    }

    public void undo() {
        undoManager.undo();
    }

    public boolean undoPossible() {
        return undoManager.undoPossible();
    }

    public boolean redoPossible() {
        return undoManager.redoPossible();
    }
    
    /* *************************
     *  replacing one GeoElement by another:
     * this may change the logic of the construction
     * and is a very powerful operation
     *  *************************/

    /**
     * Replaces oldGeo by newGeo in the current construction
     */ 
    public void replace(GeoElement oldGeo, GeoElement newGeo) throws Exception {        
    	if (oldGeo == null || 
             newGeo == null || 
             oldGeo == newGeo) return;  
               
        // check for circular definition
        if (newGeo.isChildOf(oldGeo)) {
            restoreCurrentUndoInfo();
            throw new CircularDefinitionException();
        }
        
        AlgoElement oldGeoAlgo = oldGeo.getParentAlgorithm();
        AlgoElement newGeoAlgo = newGeo.getParentAlgorithm();      
        
        //  1) remove all brothers and sisters of oldGeo
        if (oldGeoAlgo != null) {
            oldGeoAlgo.removeOutputExcept(oldGeo);
        }
                
        // if newGeo is not in construction index, we must set its index now
        // in order to let (2) and (3) work
        if (newGeo.getConstructionIndex() == -1) {
            int ind = ceList.size();
            if (newGeoAlgo == null)
                newGeo.setConstructionIndex(ind);
            else 
                newGeoAlgo.setConstructionIndex(ind);
        }       
        
        // 2) move all predecessors of newGeo to the left of oldGeo in construction list
        updateConstructionOrder(oldGeo, newGeo);                                                                                                
        
       
        String a, b; // a = old string, b = new string
        
        // set maximum digits for XML
        int oldDecimals = kernel.getPrintDecimals();		
        kernel.setPrintDecimals(50);        
        
        // set label to get replaceable XML              
        if (newGeo.isLabelSet()) { // newGeo already exists in construction
            // oldGeo is replaced by newGeo, so oldGeo get's newGeo's label
            oldGeo.label = newGeo.label;
                            
            a = (oldGeoAlgo == null) ? oldGeo.getXML() : oldGeoAlgo.getXML();
            b = ""; // remove oldGeo from construction
        } else {
            // newGeo doesn't exist in construction, so we take oldGeo's label
            newGeo.label = oldGeo.label;
            newGeo.labelSet = true; // to get right XML output
            newGeo.setAllVisualProperties(oldGeo);                         
            
            //  NEAR-TO-RELATION for dependent new geo:
            //  copy oldGeo's values to newGeo so that the
            // near-to-relationship can do its job if possible
            if (newGeoAlgo != null && newGeoAlgo.isNearToAlgorithm()) { 
                try {                   
                    newGeo.set(oldGeo);                    
                } catch (Exception e) {}                
            }                        
            
            a = (oldGeoAlgo == null) ? oldGeo.getXML() : oldGeoAlgo.getXML();
            b = (newGeoAlgo == null) ? newGeo.getXML() : newGeoAlgo.getXML();              

//            System.out.println("oldGeo: " + oldGeo + ", algo: " + oldGeoAlgo);
//            System.out.println("newGeo: " + newGeo + ", algo: " + newGeoAlgo);
        }
        
        // set back decimals
        kernel.setPrintDecimals(oldDecimals);

        // 3) replace oldGeo by newGeo
        doReplace(getCurrentUndoXML(), a, b);       
    }
     
     /**
      * Moves all predecessors of newGeo (i.e. all objects that newGeo depends upon) 
      * to the left of oldGeo in the construction list
      */
     private void updateConstructionOrder(GeoElement oldGeo, GeoElement newGeo) {
        TreeSet predList = newGeo.getAllPredecessors();      
        
        // check if moving is needed
        // find max construction index of newGeo's predecessors and newGeo itself       
        Iterator it = predList.iterator();
        int maxPredIndex = newGeo.getConstructionIndex();
        while (it.hasNext()) {
            GeoElement pred = (GeoElement) it.next();
            int predIndex = pred.getConstructionIndex();    
            if (predIndex > maxPredIndex) maxPredIndex = predIndex;
        }
        
        // no reordering is needed
        if (oldGeo.getConstructionIndex() > maxPredIndex)
            return;             
        
        // reordering is needed                 
        // move all predecessors of newGeo (i.e. all objects that geo depends upon) as
        // far as possible to the left in the construction list
        it = predList.iterator();
        while (it.hasNext()) {          
            GeoElement pred = (GeoElement) it.next();           
            moveInConstructionList(pred, pred.getMinConstructionIndex());       
        }
        
        // move newGeo to the left as well (important if newGeo already existed in construction)
        moveInConstructionList(newGeo, newGeo.getMinConstructionIndex());    
        
        // move oldGeo to its maximum construction index
        moveInConstructionList(oldGeo, oldGeo.getMaxConstructionIndex());                       
     }
    
    /**
     * Replaces oldGeoXML by newGeoXML in consXML and tries to build the new 
     * construction
     * @param consXML
     * @param oldGeoXML
     * @param newGeoXML
     * @return
     */
    private void doReplace(String consXML, String oldXML, String newXML) throws Exception {             
        // try to process the new construction
        try {           	
//            System.out.println("***");
//            System.out.println("old XML:\n" + oldXML);
//            System.out.println("new XML:\n" + newXML);  
            
            //  replace Strings: oldXML by newXML in consXML            
            int pos = consXML.indexOf(oldXML);
            if (pos < 0) {
                System.err.println("replace failed: oldXML string not found:\n"+ oldXML);
                throw new MyError(getApplication(), "ReplaceFailed");
            }           
            StringBuffer newConsXML = new StringBuffer();
            newConsXML.append(consXML.substring(0, pos));
            newConsXML.append(newXML);
            newConsXML.append(consXML.substring(pos + oldXML.length()));                
   
            System.out.println("***");
            System.out.println("cons XML:\n" + consXML);
            System.out.println("***");      
            System.out.println("*** REPLACE ***\n" + oldXML + "*** BY ***\n" + newXML);
            System.out.println("***");
            System.out.println("new XML:\n" + newConsXML);
            System.out.println("***");
           
            undoManager.processXML(newConsXML.toString());
            kernel.notifyReset();
            kernel.updateConstruction();              
        } 
        catch (MyError e) {
            System.err.println("replace failed");
            restoreCurrentUndoInfo();
            throw e;
        }
        catch (Exception e) {
            System.err.println("replace failed"); //+ e.getMessage());
            restoreCurrentUndoInfo();
            throw e;
        }       
    }

    /* *************************
     *  XML output
     *  *************************/

    /**
     * Returns this construction in XML format.
     */
    public String getXML() {    	       	
        StringBuffer sb = new StringBuffer();

        // kernel settings
        sb.append("<kernel>\n");
        
        // continuity: true or false, since V3.0
        sb.append("\t<continuous val=\"");
        sb.append(kernel.isContinuous());
        sb.append("\"/>\n");
        
        // decimal places
        sb.append("\t<decimals val=\"");
        sb.append(kernel.getPrintDecimals());
        sb.append("\"/>\n");
        
        // angle unit
        sb.append("\t<angleUnit val=\"");
        sb.append(angleUnit == Kernel.ANGLE_RADIANT ? "radiant" : "degree");           
        sb.append("\"/>\n");
        
        // coord style
        sb.append("\t<coordStyle val=\"");
        sb.append(kernel.getCoordStyle());           
        sb.append("\"/>\n");
        
        sb.append("</kernel>\n");

        // construction XML
        sb.append(getConstructionXML());        
                         
        return sb.toString();
    }
    
    public String getConstructionXML() {
    	StringBuffer sb = new StringBuffer(500);
    	
    	// change kernel settings temporarily
    	int oldCoordStlye = kernel.getCoordStyle();
    	int oldDecimals = kernel.getPrintDecimals();
    	int oldPrintForm = kernel.getCASPrintForm();        
        kernel.setCoordStyle(Kernel.COORD_STYLE_DEFAULT);                 		
        kernel.setPrintDecimals(50);
        kernel.setCASPrintForm(ExpressionNode.STRING_TYPE_GEOGEBRA_XML);
        
        try {
	    	// save construction elements
	        sb.append("<construction title=\""); 
	        sb.append(Util.encodeXML(getTitle())); 
	        sb.append("\" author=\"");
	        sb.append(Util.encodeXML(getAuthor()));
	        sb.append("\" date=\"");
	        sb.append(Util.encodeXML(getDate()));
	        sb.append("\">\n");
	        
	        // worksheet text
	        if (worksheetTextDefined()) {
		        sb.append("\t<worksheetText above=\""); 
		        sb.append(Util.encodeXML(getWorksheetText(0))); 
		        sb.append("\" below=\"");
		        sb.append(Util.encodeXML(getWorksheetText(1)));        
		        sb.append("\"/>\n");     
	        }                
	        
	        ConstructionElement ce;
	        int size = ceList.size();
	        for (int i = 0; i < size; ++i) {
	            ce = (ConstructionElement) ceList.get(i);
	            sb.append(ce.getXML());
	        }       
	        
	        sb.append("</construction>\n");	        
        } catch (Exception e) {
	        e.printStackTrace();
	    }  
        
        // restore old kernel settings
        kernel.setPrintDecimals(oldDecimals);
        kernel.setCoordStyle(oldCoordStlye);   
        kernel.setCASPrintForm(oldPrintForm);
        
        return sb.toString();
    }
    
    /**
     * Returns undo xml string of this  construction.
     */
    public String getCurrentUndoXML() {
        return MyXMLio.getUndoXML(this);
    }

    public String getAuthor() {
        return (author != null) ? author : "";
    }

    public String getDate() {
        return (date != null) ? date : "";
    }

    public String getTitle() {
        return (title != null) ? title : "";
    }     

    public void setAuthor(String string) {
        author = string;       
    }

    public void setDate(String string) {
        date = string;
    }

    public void setTitle(String string) {
        title = string;
    }       
    
    public String getWorksheetText(int i) {
        return (worksheetText[i] != null) ? worksheetText[i] : "";
    }
    
    public void setWorksheetText(String text, int i) {
        worksheetText[i] = text;
    }

    private boolean worksheetTextDefined() {
    	for (int i=0; i < worksheetText.length; i++) {
    		if (worksheetText[i] != null && 
    			worksheetText[i].length() > 0) return true;
    	}
    	return false;
    }
    
    private class LabelComparator implements Comparator {				    	
    	public int compare(Object ob1, Object ob2) {
    		GeoElement geo1 = (GeoElement) ob1;
    		GeoElement geo2 = (GeoElement) ob2;
    		return geo1.label.compareTo(geo2.label);			
    	}				 
    }
    
   
}




