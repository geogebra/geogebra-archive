/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.kernel;

import geogebra.io.MyXMLio;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.optimization.ExtremumFinder;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra.util.Util;
import geogebra3D.kernel3D.GeoPoint3D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Matcher;

/**
 * A Construction consists of a construction list with objects of type
 * ConstructionElement (i.e. GeoElement or AlgoElement) and a GeoElement table
 * with (String label, GeoElement geo) pairs. Every ConstructionElement is
 * responsible to add or remove itself from the construction list. Every
 * GeoElement is responsible to add or remove itself from the GeoElement table.
 * 
 * To remove a ConstructionElement ce form its construction call ce.remove();
 * 
 * @author Markus Hohenwarter
 */
public class Construction {

	// Added for Intergeo File Format (Yves Kreis) -->
	// writes the <elements> and the <constraints> part
	public static final int CONSTRUCTION = 0;
	// writes the <display> part with the <display> tag
	public static final int DISPLAY = 1;
	// <-- Added for Intergeo File Format (Yves Kreis)

	private String title, author, date;
	// text for dynamic worksheets: 0 .. above, 1 .. below
	private String[] worksheetText = new String[2];

	// ConstructionElement List (for objects of type ConstructionElement)
	private ArrayList ceList;

	// AlgoElement List (for objects of type AlgoElement)
	private ArrayList algoList; // used in updateConstruction()

	// GeoElementTable for (label, GeoElement) pairs
	protected HashMap geoTable, localVariableTable;

	// set with all labeled GeoElements in ceList order
	private TreeSet<GeoElement> geoSet; //generic Object replaced by GeoElement (Zbynek Konecny, 2010-06-14)

	// set with all labeled GeoElements in alphabetical order
	private TreeSet<GeoElement> geoSetLabelOrder;
	
	// list of random numbers
	private TreeSet randomNumbers;

	// a map for sets with all labeled GeoElements in alphabetical order of
	// specific types
	// (points, lines, etc.)
	private HashMap geoSetsTypeMap;	

	// list of Macro commands used in this construction
	private ArrayList usedMacros;
	
	// list of algorithms that need to be updated when EuclidianView changes
	private ArrayList euclidianViewAlgos;

	// UndoManager
	protected UndoManager undoManager;

	// current construction step (-1 ... ceList.size() - 1)
	// step == -1 shows empty construction
	private int step;

	// in macro mode no new labels or construction elements
	// can be added
	private boolean supressLabelCreation = false;
	
	// collect replace() requests to improve performance
	// when many cells in the spreadsheet are redefined at once
	private boolean collectRedefineCalls = false;
	private HashMap redefineMap;

	// showOnlyBreakpoints in construction protocol
	private boolean showOnlyBreakpoints;

	// construction belongs to kernel
	private Kernel kernel;

	// axis objects
	private GeoAxis xAxis, yAxis;
	private String xAxisLocalName, yAxisLocalName;

	// default elements
	protected ConstructionDefaults consDefaults;

	/**
	 * Creates a new Construction.
	 */
	public Construction(Kernel k) {
		this(k, null);
	}

	Construction(Kernel k, Construction parentConstruction) {
		kernel = k;

		ceList = new ArrayList();
		algoList = new ArrayList();
		step = -1;

		geoSet = new TreeSet();
		geoSetLabelOrder = new TreeSet(new LabelComparator());
		geoSetsTypeMap = new HashMap();
		euclidianViewAlgos = new ArrayList();

		if (parentConstruction != null)
			consDefaults = parentConstruction.getConstructionDefaults();
		else
			newConstructionDefaults();
			//consDefaults = new ConstructionDefaults(this);

		initAxis();

		geoTable = new HashMap(200);
		localVariableTable = new HashMap();
		initGeoTable();
	}
	
	
	/**
	 * init the axis
	 */
	protected void initAxis(){
		xAxis = new GeoAxis(this, GeoAxis.X_AXIS);
		yAxis = new GeoAxis(this, GeoAxis.Y_AXIS);
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
	 * creates the ConstructionDefaults consDefaults
	 */
	protected void newConstructionDefaults(){
		consDefaults = new ConstructionDefaults(this);
	}

	/**
	 * Returns the construction default object of this construction.
	 */
	final public ConstructionDefaults getConstructionDefaults() {
		return consDefaults;
	}

	protected void initGeoTable() {
		geoTable.clear();		
		
		// add axes labels both in English and current language
		geoTable.put("xAxis", xAxis);
		geoTable.put("yAxis", yAxis);
		if (xAxisLocalName != null) {
			geoTable.put(xAxisLocalName, xAxis);
			geoTable.put(yAxisLocalName, yAxis);
		}		
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
	 * If this is set to true new construction elements won't get labels.
	 */
	public void setSuppressLabelCreation(boolean flag) {
		supressLabelCreation = flag;
	}

	public boolean isSuppressLabelsActive() {
		return supressLabelCreation;
	}

	/**
	 * Sets how steps in the construction protocol are handled.
	 */
	public void setShowOnlyBreakpoints(boolean flag) {
		showOnlyBreakpoints = flag;
	}

	final public boolean showOnlyBreakpoints() {
		return showOnlyBreakpoints;
	}

	/*
	 * Construction List Management
	 */

	/**
	 * Returns the ConstructionElement for the given construction index.
	 */
	public ConstructionElement getConstructionElement(int index) {
		if (index < 0 || index >= ceList.size())
			return null;
		return (ConstructionElement) ceList.get(index);
	}

	/**
	 * Returns a set with all labeled GeoElement objects of this construction in
	 * construction order.
	 */
	final public TreeSet getGeoSetConstructionOrder() {
		return geoSet;
	}

	/**
	 * Returns a set with all labeled GeoElement objects of this construction in
	 * alphabetical order of their labels.
	 */
	final public TreeSet<GeoElement> getGeoSetLabelOrder() {
		return geoSetLabelOrder;
	}

	/**
	 * Returns a set with all labeled GeoElement objects of a specific type in
	 * alphabetical order of their labels.
	 * 
	 * @param geoClassType
	 *            : use GeoElement.GEO_CLASS_* constants
	 */
	final public TreeSet getGeoSetLabelOrder(int geoClassType) {
		TreeSet typeSet = (TreeSet) geoSetsTypeMap.get(geoClassType);
		if (typeSet == null) {
			typeSet = createTypeSet(geoClassType);
		}
		return typeSet;
	}

	/**
	 * Returns a set with all labeled GeoElement objects sorted in alphabetical
	 * order of their type strings and labels (e.g. Line g, Line h, Point A,
	 * Point B, ...). Note: the returned TreeSet is a copy of the current
	 * situation and is not updated by the construction later on.
	 * @return Set of all labeld GeoElements orted by name and description
	 */
	final public TreeSet<GeoElement> getGeoSetNameDescriptionOrder() {
		// sorted set of geos
		TreeSet<GeoElement> sortedSet = new TreeSet<GeoElement>(new NameDescriptionComparator());

		// get all GeoElements from construction and sort them
		Iterator<GeoElement> it = geoSet.iterator();
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
	 * 
	 * @param checkContains
	 *            : true to first check if ce is already in list
	 */
	public void addToConstructionList(ConstructionElement ce,
			boolean checkContains) {
		if (supressLabelCreation)
			return;
		if (checkContains && ce.isInConstructionList())
			return;

		/*
		++step;
		updateAllConstructionProtocolAlgorithms(); // Michael Borcherds
													// 2008-05-15

		ceList.add(step, ce);
		updateConstructionIndex(step);
		*/
		addToConstructionList(ce, step+1);
	}
	
	
	/** Adds the given Construction Element to this Construction at position index
	 * @param ce
	 * @param index
	 */
	public void addToConstructionList(ConstructionElement ce, int index){
		
		++step;
		updateAllConstructionProtocolAlgorithms();
		ceList.add(index, ce);
		updateConstructionIndex(index);
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
			--step;
		} else { // pos > step
			ceList.remove(ce);
			ce.setConstructionIndex(-1);
		}

		updateConstructionIndex(pos);
		updateAllConstructionProtocolAlgorithms(); // Michael Borcherds
													// 2008-05-15
	}

	/**
	 * Adds the given algorithm to this construction's algorithm list
	 * 
	 * @see updateConstruction()
	 */
	public void addToAlgorithmList(AlgoElement algo) {
		algoList.add(algo);
	}

	public void removeFromAlgorithmList(AlgoElement algo) {
		algoList.remove(algo);
	}

	/**
	 * Moves geo to given position toIndex in this construction. Note: if ce (or
	 * its parent algorithm) is not in the construction list nothing is done.
	 * 
	 * @return whether construction list was changed or not.
	 */
	private boolean moveInConstructionList(GeoElement geo, int toIndex) {
		AlgoElement algoParent = geo.getParentAlgorithm();
		int fromIndex = (algoParent == null) ? ceList.indexOf(geo) : ceList
				.indexOf(algoParent);
		if (fromIndex >= 0)
			return moveInConstructionList(fromIndex, toIndex);
		else
			return false;
	}

	/**
	 * Moves object at position from to position to in this construction.
	 * 
	 * @return whether construction list was changed or not.
	 */
	public boolean moveInConstructionList(int fromIndex, int toIndex) {
		// check if move is possible
		ConstructionElement ce = (ConstructionElement) ceList.get(fromIndex);
		boolean change = fromIndex != toIndex
				&& ce.getMinConstructionIndex() <= toIndex
				&& toIndex <= ce.getMaxConstructionIndex();
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
			} else if (toIndex <= step && step < fromIndex) {
				++step;

				ce.notifyAdd();
			}
		}
		updateAllConstructionProtocolAlgorithms(); // Michael Borcherds
													// 2008-05-15
		return change;
	}

	// update all indices >= pos
	private void updateConstructionIndex(int pos) {
		if (pos < 0)
			return;
		int size = ceList.size();
		for (int i = pos; i < size; ++i) {
			((ConstructionElement) ceList.get(i)).setConstructionIndex(i);
		}
	}

	/**
	 * Returns true iff geo is independent and in the construction list or geo
	 * is dependent and its parent algorithm is in the construction list.
	 * 
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
		euclidianViewAlgos.clear();	
		initGeoTable();

		// reinit construction step
		step = -1;

		// delete title, author, date
		title = null;
		author = null;
		date = null;
		worksheetText[0] = null;
		worksheetText[1] = null;

		usedMacros = null;
	}

	/**
	 * Updates all objects in this construction.
	 */
	final public void updateConstruction() {
	//G.Sturr 2010-5-28: turned this off so that random numbers can be traced
	//	if (!kernel.isMacroKernel() && kernel.app.hasGuiManager())
	//		kernel.app.getGuiManager().startCollectingSpreadsheetTraces();

		// update all independent GeoElements
		int size = ceList.size();
		for (int i = 0; i < size; ++i) {
			ConstructionElement ce = (ConstructionElement) ceList.get(i);
			if (ce.isIndependent()) {
				ce.update();
			}
		}

		// update all random numbers()
		updateAllRandomGeos();
		
		// init and update all algorithms
		// make sure we call algo.initNearToRelationship() fist
		// for all algorithms because algo.update() could have 
		// the side-effect to call updateCascade() for points 
		// that have locateables (see GeoPoint.update())
		size = algoList.size();
		
		// init near to relationship for all algorithms:
		// this makes sure intersection points stay at their saved positions
		for (int i = 0; i < size; ++i) {
			AlgoElement algo = (AlgoElement) algoList.get(i);
			algo.initForNearToRelationship();						
		}
		
		// update all algorithms
		for (int i = 0; i < size; ++i) {
			AlgoElement algo = (AlgoElement) algoList.get(i);		
			
			// reinit near to relationship to make sure points stay at their saved position
			// keep this line, see http://code.google.com/p/geogebra/issues/detail?id=62
			algo.initForNearToRelationship();
			
			// update algorithm
			algo.update();
		}
	
	//G.Sturr 2010-5-28: 	
	//	if (!kernel.isMacroKernel() && kernel.app.hasGuiManager())
		//	kernel.app.getGuiManager().stopCollectingSpreadsheetTraces();
	}

	final void updateAllAlgorithms() {
		// update all algorithms

		// *** algoList.size() can change during the loop
		for (int i = 0; i < algoList.size() ; ++i) {
			AlgoElement algo = (AlgoElement) algoList.get(i);
			algo.update();
		}
	}
	
	
	/**
	 * Registers an algorithm that wants to be notified when setEuclidianViewBounds() is called.	 
	 */
	final void registerEuclidianViewAlgo(EuclidianViewAlgo algo) {
		if (!euclidianViewAlgos.contains(algo))
			euclidianViewAlgos.add(algo);
	}
	
	void unregisterEuclidianViewAlgo(EuclidianViewAlgo algo) {		
		euclidianViewAlgos.remove(algo);
	}
	
	public boolean notifyEuclidianViewAlgos() {
		boolean didUpdate = false;		
		int size = euclidianViewAlgos.size();	
		for (int i=0; i < size; i++) {
			didUpdate = true;
			((EuclidianViewAlgo) euclidianViewAlgos.get(i)).euclidianViewUpdate();			
		}		
		return didUpdate;
	}	
	
	public boolean hasEuclidianViewAlgos() {
		return euclidianViewAlgos.size() > 0;
	}
	

	// Michael Borcherds 2008-05-15
	final boolean updateAllConstructionProtocolAlgorithms() {
		boolean didUpdate = false;
		// Application.debug("updateAllConstructionProtocolAlgorithms");
		// update all algorithms
		int size = algoList.size();
		for (int i = 0; i < size; ++i) {
			AlgoElement algo = (AlgoElement) algoList.get(i);
			if (algo.wantsConstructionProtocolUpdate()) {
				algo.compute();
				// algo.euclidianViewUpdate();
				algo.getGeoElements()[0].updateCascade();
				didUpdate = true;
				// Application.debug("  update algo: " + algo + " , kernel " +
				// algo.getKernel() + ", ymin: " + algo.getKernel().getYmin());
			}

		}
		
		if (didUpdate) {
			Application app = kernel.getApplication();
			if (app.hasGuiManager())
				app.getGuiManager().updateConstructionProtocol();
		}

		return didUpdate;
	}


	/**
	 * Build a set with all algorithms of this construction (in topological
	 * order). The method updateAll() of this set can be used to update the
	 * whole construction.
	 * 
	 * public AlgorithmSet buildOveralAlgorithmSet() { // 1) get all independent
	 * GeoElements in construction and update them // 2) build one overall
	 * updateSet from all updateSets of (1)
	 * 
	 * // 1) get all independent geos in construction LinkedHashSet indGeos =
	 * new LinkedHashSet(); int size = ceList.size(); for (int i = 0; i < size;
	 * ++i) { ConstructionElement ce = (ConstructionElement) ceList.get(i); if
	 * (ce.isIndependent()) indGeos.add(ce); else {
	 * indGeos.addAll(ce.getAllIndependentPredecessors()); } }
	 * 
	 * // 2) build one overall updateSet AlgorithmSet algoSet = new
	 * AlgorithmSet(); Iterator it = indGeos.iterator(); while (it.hasNext()) {
	 * GeoElement geo = (GeoElement) it.next();
	 * 
	 * // update this geo only geo.update();
	 * 
	 * // get its update set and add it to the overall updateSet
	 * algoSet.addAll(geo.getAlgoUpdateSet()); }
	 * 
	 * return algoSet; }
	 */

	/**
	 * Tests if this construction has no elements.
	 * 
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
	 * list will be visible in the views, objects step+1 to the end will be
	 * hidden.
	 * 
	 * @param s
	 *            : step number from range -1 ... steps()-1 where -1 shows an
	 *            empty construction.
	 */
	public void setStep(int s) {
		//Application.debug("setStep"+step+" "+s);

		if (s == step || s < -1 || s >= ceList.size())
			return;

			kernel.setAllowVisibilitySideEffects(false);

		if (s < step) {
			for (int i = s + 1; i <= step; ++i) {
				((ConstructionElement) ceList.get(i)).notifyRemove();
			}
		} else {
			for (int i = step + 1; i <= s; ++i) {
				//Application.debug(i+"");
				((ConstructionElement) ceList.get(i)).notifyAdd();
			}
		}
	
		step = s;

		kernel.setAllowVisibilitySideEffects(true);

		// Michael Borcherds 2008-05-15
		updateAllConstructionProtocolAlgorithms();
	}

	/**
	 * Returns current construction step position.
	 */
	public int getStep() {
		return step;
	}

	/*
	 * GeoElementTable Management
	 */
	/**
	 * Adds given GeoElement to a table where (label, object) pairs are stored.
	 * 
	 * @see removeLabel(), lookupLabel()
	 */
	public void putLabel(GeoElement geo) {
		if (supressLabelCreation || geo.label == null)
			return;

		geoTable.put(geo.label, geo);
		addToGeoSets(geo);		
	}

	/**
	 * Removes given GeoElement from a table where (label, object) pairs are
	 * stored.
	 * 
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
		 * Application.debug("*** geoSet order (add " + geo + ") ***"); Iterator
		 * it = geoSet.iterator();
		 * 
		 * while (it.hasNext()) { GeoElement g = (GeoElement) it.next();
		 * Application.debug(g.getConstructionIndex() + ": " + g); }
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
		 * Application.debug("*** geoSet order (remove " + geo + ") ***");
		 * Iterator it = geoSet.iterator(); int i = 0; while (it.hasNext()) {
		 * GeoElement g = (GeoElement) it.next();
		 * Application.debug(g.getConstructionIndex() + ": " + g); }
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
	 * 
	 * @return may return null
	 */
	GeoElement lookupLabel(String label) {
		return lookupLabel(label, false);
	}

	/**
	 * Returns a GeoElement for the given label. Note: only geos with
	 * construction index 0 to step are available.
	 * 
	 * @param allowAutoCreate
	 *            : true = allow automatic creation of missing labels (e.g. for
	 *            spreadsheet)
	 * @return may return null
	 */
	GeoElement lookupLabel(String label, boolean allowAutoCreate) {
		if (label == null)
			return null;
		
		// local var handling
		if (!localVariableTable.isEmpty()) {
			GeoElement localGeo = (GeoElement) localVariableTable.get(label);
			if (localGeo != null)
				return localGeo;
		}

		// global var handling
		GeoElement geo = geoTabelVarLookup(label);

		// STANDARD CASE: variable name found
		if (geo != null) {
			return checkConstructionStep(geo);
		}
		
		// DESPARATE CASE: variable name not found			
						
		/*
		 * SPREADSHEET $ HANDLING
		 * In the spreadsheet we may have variable names like
		 * "A$1" for the "A1" to deal with absolute references.
		 * Let's remove all "$" signs from label and try again.
		 */ 	
        if (label.indexOf('$') > -1) {
			StringBuilder labelWithout$ = new StringBuilder(label.length());
			for (int i=0; i < label.length(); i++) {
				char ch = label.charAt(i);
				if (ch != '$')
					labelWithout$.append(ch);
			}

			// allow automatic creation of elements
	        geo = lookupLabel(labelWithout$.toString(), allowAutoCreate);				
			if (geo != null) {
				// geo found for name that includes $ signs
				return checkConstructionStep(geo);
			}
        }	
        
        // try upper case version for spreadsheet label like a1
        if (allowAutoCreate) {	    	
			if (Character.isLetter(label.charAt(0)) // starts with letter
				&& Character.isDigit(label.charAt(label.length()-1)))  // ends with digit
			{
				String upperCaseLabel = label.toUpperCase();
				geo = geoTabelVarLookup(upperCaseLabel);
				if (geo != null) {
					return checkConstructionStep(geo);
				}
			}
        }
        
        // if we get here, nothing worked: 
        // possibly auto-create new GeoElement with that name			
		if (allowAutoCreate)
			return autoCreateGeoElement(label);
		else
			return null;			
	}	
	
	/**
	 * Returns geo if it is available at the current
	 * construction step, otherwise returns null.
	 */
	private GeoElement checkConstructionStep(GeoElement geo) {
		// check if geo is available for current step
		if (geo.isAvailableAtConstructionStep(step))
			return geo;
		else
			return null;
	}

	/**
	 * Automatically creates a GeoElement object for a certain label that is not
	 * yet used in the geoTable of this construction. This is done for e.g.
	 * point i = (0,1), number e = Math.E, empty spreadsheet cells
	 * 
	 * @param label
	 * @see willAutoCreateGeoElement()
	 */
	private GeoElement autoCreateGeoElement(String label) {		
		GeoElement createdGeo = null;
		boolean fix = true;
		boolean auxilliary = true;
		
		// if referring to variable "i" (complex) that is undefined, create it
		if (label.equals("i")) {
			GeoPoint point = new GeoPoint(this);
			point.setCoords(0.0d, 1.0d, 1.0d);
			point.setEuclidianVisible(false);
			point.setComplex();
			createdGeo = point;
		}

		// if referring to variable "e" (Euler no) that is undefined, create it
		else if (label.equals("e")) {
			GeoNumeric number = new GeoNumeric(this);
			number.setValue(Math.E);
			createdGeo = number;			
		}
		
		// expression like AB, autocreate AB=Distance[A,B] or AB = A * B according to whether A,B are points or numbers
		else if (label.length() == 2) {
			GeoElement geo1 = kernel.lookupLabel(label.charAt(0)+"");
			if (geo1 != null && geo1.isGeoPoint()) {
				GeoElement geo2 = kernel.lookupLabel(label.charAt(1)+"");
				if (geo2 != null && geo2.isGeoPoint()) {
					AlgoDistancePoints dist = new AlgoDistancePoints(this, null, (GeoPoint)geo1, (GeoPoint)geo2);
					createdGeo = dist.getDistance();
					fix = false;
				}
			} else if (geo1 != null && geo1.isNumberValue()) {
				GeoElement geo2 = kernel.lookupLabel(label.charAt(1)+"");
				if (geo2 != null && geo2.isNumberValue()) {
					ExpressionNode node = new ExpressionNode(kernel, ((NumberValue)geo1).evaluate(), ExpressionNodeConstants.MULTIPLY, ((NumberValue)geo2).evaluate());
					AlgoDependentNumber algo = new AlgoDependentNumber(this, null, node, false);
					createdGeo = algo.getNumber();
					fix = false;					
				}
			}

		} else if (label.length() == 3) {
			if (label.equals("lnx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("ln(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} 
		} else if (label.length() == 4) {
			if (label.equals("sinx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("sin(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("cosx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("cos(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("tanx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("tan(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("secx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("sec(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("cscx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("csc(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("cotx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("cot(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("logx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("log(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			}
		} else if (label.length() == 5) {
			if (label.equals("sinhx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("sinh(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("coshx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("cosh(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("tanhx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("tanh(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("sechx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("sech(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("cothx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("coth(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("acosx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("acos(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("asinx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("asin(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("atanx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("atan(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			}
		} else if (label.length() == 6) {
			if (label.equals("cosecx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("cosec(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("arcosx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("acos(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("asinhx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("asinh(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("acoshx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("acosh(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("atanhx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("atanh(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			}
		} else if (label.length() == 7) {
			if (label.equals("arccosx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("acos(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("arcsinx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("asin(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("arctanx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("atan(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} 
		} else if (label.length() == 8) {
			if (label.equals("arccoshx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("acosh(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("arcsinhx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("asinh(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} else if (label.equals("arctanhx")) {
				createdGeo = kernel.getAlgebraProcessor().evaluateToFunction("atanh(x)",true); 
				label=createdGeo.getDefaultLabel();
				auxilliary = false;
				fix = false;
			} 
		}
		

		
		// handle i or e case
		if (createdGeo != null) {
			// make sure that label creation is turned on
			boolean oldSuppressLabelsActive = isSuppressLabelsActive();
			setSuppressLabelCreation(false);
			
			createdGeo.setAuxiliaryObject(auxilliary);
			createdGeo.setLabel(label);
			createdGeo.setFixed(fix);
			
			// revert to previous label creation state
			setSuppressLabelCreation(oldSuppressLabelsActive);	
			return createdGeo;
		}
						
		// check spreadsheet cells
		else {
			// for missing spreadsheet cells, create object 
			// of same type as above
			Matcher cellNameMatcher = GeoElement.spreadsheetPattern.matcher(label);
			if (cellNameMatcher.matches()) {
				String col = cellNameMatcher.group(1);
				int row = Integer.parseInt(cellNameMatcher.group(2));
	
				// try to get neighbouring cell for object type look above
				GeoElement neighbourCell = geoTabelVarLookup(col + (row - 1));
				if (neighbourCell == null) // look below
					neighbourCell = geoTabelVarLookup(col + (row + 1));
	
				label = col + row;			
				createdGeo = createSpreadsheetGeoElement(neighbourCell, label);
			}
		}	
			
		return createdGeo;
	}
	
	/**
	 * Returns whether the specified label will automatically create a GeoElement
	 * when autoCreateGeoElement() is called with it.
	 */
	final public boolean willAutoCreateGeoElement(String label) {
		if ("i".equals(label) || "e".equals(label))
			return true;
		
		Matcher cellNameMatcher = GeoElement.spreadsheetPattern. matcher(label);
		if (cellNameMatcher.matches())
			return true;
		
		return false;		
	}
	
	/**
	 * Creates a new GeoElement for the spreadsheet of same type as neighbourCell.
	 */
	final public GeoElement createSpreadsheetGeoElement(GeoElement neighbourCell, String label) {	
		GeoElement result; 
		
		// found neighbouring cell: create geo of same type
		if (neighbourCell != null) {
			result = neighbourCell.copy();
		}
		// no neighbouring cell: create number with value 0
		else {
			result = new GeoNumeric(this);
		}				
		
		// make sure that label creation is turned on
		boolean oldSuppressLabelsActive = isSuppressLabelsActive();
		setSuppressLabelCreation(false);
		
		// set 0 and label
		result.setZero();
		result.setAuxiliaryObject(true);
		result.setLabel(label);
		
		// revert to previous label creation state
		setSuppressLabelCreation(oldSuppressLabelsActive);	
		
		return result;
	}

	GeoElement geoTabelVarLookup(String label) {
		GeoElement ret = (GeoElement) geoTable.get(label);	
		return ret;
	}

	/**
	 * Returns true if label is not occupied by any GeoElement.
	 */
	public boolean isFreeLabel(String label) {
		if (label == null)
			return false;
		else
			return !geoTable.containsKey(label);
	}

	/*
	 * redo / undo
	 */

	/**
	 * Clears the undo info list of this construction and adds the current
	 * construction state to the undo info list.
	 */
	public void initUndoInfo() {
		if (undoManager == null)
			undoManager = new UndoManager(this);
		undoManager.initUndoInfo();
	}

	public void storeUndoInfo() {
		// undo unavailable in applets
		//if (getApplication().isApplet()) return;

		undoManager.storeUndoInfo();		
	}

	public void restoreCurrentUndoInfo() {
		// undo unavailable in applets
		//if (getApplication().isApplet()) return;
		collectRedefineCalls = false;
		
		if (undoManager != null)
			undoManager.restoreCurrentUndoInfo();
	}

	public void redo() {
		// undo unavailable in applets
		//if (getApplication().isApplet()) return;

		undoManager.redo();
	}

	public void undo() {
		// undo unavailable in applets
		//if (getApplication().isApplet()) return;

		undoManager.undo();
	}

	public boolean undoPossible() {
		// undo unavailable in applets
		//if (getApplication().isApplet()) return false;

		return undoManager != null && undoManager.undoPossible();
	}

	public boolean redoPossible() {
		// undo unavailable in applets
		//if (getApplication().isApplet()) return false;

		return undoManager != null && undoManager.redoPossible();
	}


	/**
	 * Replaces oldGeo by newGeo in the current construction.
	 * This may change the logic of the
	 * construction and is a very powerful operation
	 */
	public void replace(GeoElement oldGeo, GeoElement newGeo) throws Exception {
		if (oldGeo == null || newGeo == null || oldGeo == newGeo)
			return;

		// if oldGeo does not have any children, we can simply
		// delete oldGeo and give newGeo the name of oldGeo
		if (!oldGeo.hasChildren()) {
			String oldGeoLabel = oldGeo.label;			
			oldGeo.remove();
			
			if (newGeo.isIndependent())
				addToConstructionList(newGeo, true);
			else 
				addToConstructionList(newGeo.getParentAlgorithm(), true);
			newGeo.setAllVisualProperties(oldGeo, false);			
			newGeo.setLabel(oldGeoLabel);			
			return;
		}
		
	    // check for circular definition
	    if (newGeo.isChildOf(oldGeo)) {

	        // check for eg a = a + 1, A = A + (1,1)
	    	if (oldGeo.isIndependent() && oldGeo instanceof GeoNumeric) {

	            ((GeoNumeric)oldGeo).setValue(((GeoNumeric)newGeo).getDouble());
	            oldGeo.updateRepaint();
	            return;

	        } else if (oldGeo.isIndependent() && oldGeo instanceof GeoPoint) {

	            ((GeoPoint)oldGeo).set(newGeo);
	            oldGeo.updateRepaint();
	            return;

	        } else if (oldGeo.isIndependent() && oldGeo instanceof GeoVector) {

	            ((GeoVector)oldGeo).set(newGeo);
	            oldGeo.updateRepaint();
	            return;

	        } else if (oldGeo.isIndependent() && oldGeo instanceof GeoPoint3D) {

	            ((GeoPoint3D)oldGeo).set(newGeo);
	            oldGeo.updateRepaint();
	            return;

	        } else {

	            restoreCurrentUndoInfo();
	            throw new CircularDefinitionException();

	        }

	    }				
		// 1) remove all brothers and sisters of oldGeo
		// 2) move all predecessors of newGeo to the left of oldGeo in construction list
		prepareReplace(oldGeo, newGeo);
				
		if (collectRedefineCalls) {
			// collecting redefine calls in redefineMap
			redefineMap.put(oldGeo, newGeo);
			return;
		}			
		
		// get current construction XML
		StringBuilder consXML = getCurrentUndoXML();
							
		// 3) replace oldGeo by newGeo in XML
		doReplaceInXML(consXML, oldGeo, newGeo);
		
		// 4) build new construction
		buildConstruction(consXML);
	}
	
	// 1) remove all brothers and sisters of oldGeo
	// 2) move all predecessors of newGeo to the left of oldGeo in construction list
	private void prepareReplace(GeoElement oldGeo, GeoElement newGeo)  {
		AlgoElement oldGeoAlgo = oldGeo.getParentAlgorithm();
		AlgoElement newGeoAlgo = newGeo.getParentAlgorithm();
		
		// 1) remove all brothers and sisters of oldGeo
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
		
		// make sure all output objects of newGeoAlgo are labeled, otherwise
		// we may end up with several objects that have the same label
		if (newGeoAlgo != null) {
			for (int i=0; i < newGeoAlgo.output.length; i++) {
				GeoElement geo = newGeoAlgo.output[i];
				if (geo != newGeo && geo.isDefined() && !geo.isLabelSet()) {
					geo.setLabel(null); // get free label
				}				
			}
		}

		// 2) move all predecessors of newGeo to the left of oldGeo in
		// construction list
		updateConstructionOrder(oldGeo, newGeo);
	}

	/**
	 * Moves all predecessors of newGeo (i.e. all objects that newGeo depends
	 * upon) to the left of oldGeo in the construction list
	 */
	private void updateConstructionOrder(GeoElement oldGeo, GeoElement newGeo) {
		TreeSet predList = newGeo.getAllPredecessors();

		// check if moving is needed
		// find max construction index of newGeo's predecessors and newGeo
		// itself
		Iterator it = predList.iterator();
		int maxPredIndex = newGeo.getConstructionIndex();
		while (it.hasNext()) {
			GeoElement pred = (GeoElement) it.next();
			int predIndex = pred.getConstructionIndex();
			if (predIndex > maxPredIndex)
				maxPredIndex = predIndex;
		}

		// no reordering is needed
		if (oldGeo.getConstructionIndex() > maxPredIndex)
			return;

		// reordering is needed
		// move all predecessors of newGeo (i.e. all objects that geo depends
		// upon) as
		// far as possible to the left in the construction list
		it = predList.iterator();
		while (it.hasNext()) {
			GeoElement pred = (GeoElement) it.next();
			moveInConstructionList(pred, pred.getMinConstructionIndex());
		}

		// move newGeo to the left as well (important if newGeo already existed
		// in construction)
		moveInConstructionList(newGeo, newGeo.getMinConstructionIndex());

		// move oldGeo to its maximum construction index
		moveInConstructionList(oldGeo, oldGeo.getMaxConstructionIndex());
	}
	
	/**
	 * Starts to collect all redefinition calls for the current construction.
	 * This is used to improve performance of many redefines in the spreadsheet
	 * caused by e.g. relative copy.
	 * @see processCollectedRedefineCalls()
	 */
	public void startCollectingRedefineCalls() {
		collectRedefineCalls = true;
		if (redefineMap == null)
			redefineMap = new HashMap();
		redefineMap.clear();
	}
	
	public void stopCollectingRedefineCalls() {
		collectRedefineCalls = false;
		if (redefineMap != null)
			redefineMap.clear();
	}
	
	/**
	 * Processes all collected redefine calls as a batch to improve performance.
	 * @see startCollectingRedefineCalls()
	 */
	public void processCollectedRedefineCalls() throws Exception {
		collectRedefineCalls = false;
		
		if (redefineMap == null || redefineMap.size() == 0)
			return;
		
		// get current construction XML
		StringBuilder consXML = getCurrentUndoXML();
		
		// replace all oldGeo -> newGeo pairs in XML
		Iterator it = redefineMap.keySet().iterator();			
		while (it.hasNext()) {
			GeoElement oldGeo = (GeoElement) it.next();
			GeoElement newGeo = (GeoElement) redefineMap.get(oldGeo);

			// 3) replace oldGeo by newGeo in XML
			doReplaceInXML(consXML, oldGeo, newGeo);
		}
		
		try {
			// 4) build new construction for all changes at once
			buildConstruction(consXML);
		} 
		catch (Exception e) {						
			throw e;
		}
		finally {
			stopCollectingRedefineCalls();
			consXML.setLength(0);
			consXML = null;
			System.gc();
		}
	}

	/**
	 * Replaces oldGeo by newGeo in consXML.
	 */
	private void doReplaceInXML(StringBuilder consXML, GeoElement oldGeo, GeoElement newGeo) {		
		String oldXML, newXML; // a = old string, b = new string
		
		AlgoElement oldGeoAlgo = oldGeo.getParentAlgorithm();
		AlgoElement newGeoAlgo = newGeo.getParentAlgorithm();

		// change kernel settings temporarily
		
		// change kernel settings temporarily
		int oldCoordStlye = kernel.getCoordStyle();
		int oldPrintForm = kernel.getCASPrintForm();
		kernel.setCoordStyle(Kernel.COORD_STYLE_DEFAULT);	
		kernel.setCASPrintForm(ExpressionNode.STRING_TYPE_GEOGEBRA_XML);

		// set label to get replaceable XML
		if (newGeo.isLabelSet()) { // newGeo already exists in construction
			// oldGeo is replaced by newGeo, so oldGeo get's newGeo's label
			oldGeo.label = newGeo.label;

			oldXML = (oldGeoAlgo == null) ? oldGeo.getXML() : oldGeoAlgo.getXML();
			newXML = ""; // remove oldGeo from construction
		} else {
			// newGeo doesn't exist in construction, so we take oldGeo's label
			newGeo.label = oldGeo.label;
			newGeo.labelSet = true; // to get right XML output
			newGeo.setAllVisualProperties(oldGeo, false);

			// NEAR-TO-RELATION for dependent new geo:
			// copy oldGeo's values to newGeo so that the
			// near-to-relationship can do its job if possible
			if (newGeoAlgo != null && newGeoAlgo.isNearToAlgorithm()) {
				try {
					newGeo.set(oldGeo);
				} catch (Exception e) {
				}
			}

			oldXML = (oldGeoAlgo == null) ? oldGeo.getXML() : oldGeoAlgo.getXML();
			newXML = (newGeoAlgo == null) ? newGeo.getXML() : newGeoAlgo.getXML();

//			 Application.debug("oldGeo: " + oldGeo + ", visible: " + oldGeo.isEuclidianVisible() + ", algo: " + oldGeoAlgo);
//			 Application.debug("newGeo: " + newGeo + ", visible: " + newGeo.isEuclidianVisible() + ", algo: " + newGeoAlgo);
		}

		// restore old kernel settings
		kernel.setCoordStyle(oldCoordStlye);
		kernel.setCASPrintForm(oldPrintForm);	
		
		// replace Strings: oldXML by newXML in consXML
		int pos = consXML.indexOf(oldXML);
		if (pos < 0) {
			restoreCurrentUndoInfo();
			Application.debug("replace failed: oldXML string not found:\n" + oldXML);
			//Application.debug("consXML=\n" + consXML);
			throw new MyError(getApplication(), "ReplaceFailed");
		}
		
//		System.out.println("REDEFINE: oldGeo: " + oldGeo + ", newGeo: " + newGeo);
//		System.out.println(" old XML:\n" + consXML.substring(pos, pos + oldXML.length()));
//		System.out.println(" new XML:\n" + newXML);
//		System.out.println("END redefine.");
		
		// replace oldXML by newXML in consXML
		consXML.replace(pos, pos + oldXML.length(), newXML);
		
	}
	
	/**
	 * Tries to build the new construction from the given XML string.
	 */
	private void buildConstruction(StringBuilder consXML) throws Exception {
		// try to process the new construction
		try {
			if (undoManager == null)
				undoManager = new UndoManager(this);
			undoManager.processXML(consXML.toString());
			kernel.notifyReset();
			kernel.updateConstruction();
		} catch (Exception e) {
			restoreCurrentUndoInfo();
			throw e;
		} catch (MyError err) {
			restoreCurrentUndoInfo();
			throw err;
		}
	}	

	/*
	 * XML output
	 */

//	/**
//	 * Returns this construction in XML format. GeoGebra File Format.
//	 */
//	public String getXML(boolean includeConstruction) {
//		StringBuilder sb = new StringBuilder();
//
//		// kernel settings
//		sb.append("<kernel>\n");
//
//		// continuity: true or false, since V3.0
//		sb.append("\t<continuous val=\"");
//		sb.append(kernel.isContinuous());
//		sb.append("\"/>\n");
//		
//		if (kernel.useSignificantFigures) {
//			// significant figures
//			sb.append("\t<significantfigures val=\"");
//			sb.append(kernel.getPrintFigures());
//			sb.append("\"/>\n");			
//		}
//		else
//		{
//			// decimal places
//			sb.append("\t<decimals val=\"");
//			sb.append(kernel.getPrintDecimals());
//			sb.append("\"/>\n");
//		}
//		
//		// angle unit
//		sb.append("\t<angleUnit val=\"");
//		sb.append(angleUnit == Kernel.ANGLE_RADIANT ? "radiant" : "degree");
//		sb.append("\"/>\n");
//
//		// coord style
//		sb.append("\t<coordStyle val=\"");
//		sb.append(kernel.getCoordStyle());
//		sb.append("\"/>\n");
//		
//		// animation
//		if (kernel.isAnimationRunning()) {
//			sb.append("\t<startAnimation val=\"");
//			sb.append(kernel.isAnimationRunning());
//			sb.append("\"/>\n");
//		}
//
//		sb.append("</kernel>\n");
//
//		// construction XML
//		if (includeConstruction)
//			sb.append(getConstructionXML());
//
//		return sb.toString();
//	}

//	/**
//	 * Returns this construction in I2G format. Intergeo File Format.
//	 * (Yves Kreis)
//	 */
//	public String getI2G(int mode) {
//		StringBuilder sb = new StringBuilder();
//
//		// construction I2G
//		sb.append(getConstructionI2G(mode));
//
//		return sb.toString();
//	}

	/**
	 * Returns this construction in XML format. GeoGebra File Format.
	 */
	public void getConstructionXML(StringBuilder sb) {

		// change kernel settings temporarily
		int oldCoordStlye = kernel.getCoordStyle();
		int oldPrintForm = kernel.getCASPrintForm();
        boolean oldValue = kernel.isTranslateCommandName();
		kernel.setCoordStyle(Kernel.COORD_STYLE_DEFAULT);	
		kernel.setCASPrintForm(ExpressionNode.STRING_TYPE_GEOGEBRA_XML);
        kernel.setTranslateCommandName(false); 
		
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
				ce.getXML(sb);
			}

			sb.append("</construction>\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		kernel.setCoordStyle(oldCoordStlye);
		kernel.setCASPrintForm(oldPrintForm);
		kernel.setTranslateCommandName(oldValue);                          
		
	}

	/**
	 * Returns this construction in I2G format. Intergeo File Format.
	 * (Yves Kreis)
	 */
	public void getConstructionI2G(StringBuilder sb, int mode) {
		
		// change kernel settings temporarily
		int oldCoordStlye = kernel.getCoordStyle();
		int oldPrintForm = kernel.getCASPrintForm();
        boolean oldValue = kernel.isTranslateCommandName();
		kernel.setCoordStyle(Kernel.COORD_STYLE_DEFAULT);
		kernel.setCASPrintForm(ExpressionNode.STRING_TYPE_GEOGEBRA_XML);
        kernel.setTranslateCommandName(false); 

		try {
			ConstructionElement ce;
			int size = ceList.size();

			if (mode == CONSTRUCTION) {
				sb.append("\t<elements>\n");
				for (int i = 0; i < size; ++i) {
					ce = (ConstructionElement) ceList.get(i);
					ce.getI2G(sb, ConstructionElement.ELEMENTS);
				}
				sb.append("\t</elements>\n");

				sb.append("\t<constraints>\n");
				for (int i = 0; i < size; ++i) {
					ce = (ConstructionElement) ceList.get(i);
					ce.getI2G(sb, ConstructionElement.CONSTRAINTS);
				}
				sb.append("\t</constraints>\n");
			} else if (mode == DISPLAY) {
				for (int i = 0; i < size; ++i) {
					ce = (ConstructionElement) ceList.get(i);
					ce.getI2G(sb, ConstructionElement.DISPLAY);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		kernel.setCoordStyle(oldCoordStlye);
		kernel.setCASPrintForm(oldPrintForm);
		kernel.setTranslateCommandName(oldValue);                          
		
	}

	/**
	 * Returns undo xml string of this construction.
	 */
	public StringBuilder getCurrentUndoXML() {
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
		for (int i = 0; i < worksheetText.length; i++) {
			if (worksheetText[i] != null && worksheetText[i].length() > 0)
				return true;
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

	public final void addUsedMacro(Macro macro) {
		if (usedMacros == null)
			usedMacros = new ArrayList();
		usedMacros.add(macro);
	}

	public ArrayList getUsedMacros() {
		return usedMacros;
	}
	
	/**
	 * Adds a number to the set of random numbers of this construction.
	 */
	public void addRandomGeo(GeoElement num) {
		if (randomNumbers == null) 
			randomNumbers = new TreeSet();
		randomNumbers.add(num);
		num.setRandomGeo(true);
	}
	
	/**
	 * Removes a number from the set of random numbers of this construction.
	 */
	public void removeRandomGeo(GeoNumeric num) {
		if (randomNumbers != null) 
			randomNumbers.remove(num);
		num.setRandomGeo(false);
	}
	
	/**
     * Updates all random numbers of this construction.
     */
    final public void updateAllRandomGeos() {    	
    	if (randomNumbers == null) return;
    	
    	Iterator it = randomNumbers.iterator();
    	while (it.hasNext()) {
    		GeoElement num = (GeoElement) it.next();
    		num.updateRandomGeo();
    	}    	     
    }    
    
   
    
}
