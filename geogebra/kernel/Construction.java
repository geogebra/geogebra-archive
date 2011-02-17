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
import geogebra.util.Unicode;
import geogebra.util.Util;
import geogebra3D.kernel3D.GeoPoint3D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
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

	/** Added for Intergeo File Format (Yves Kreis) -->
	 writes the <elements> and the <constraints> part */
	public static final int CONSTRUCTION = 0;
	/** Added for Intergeo File Format (Yves Kreis) 
	 * writes the <display> part with the <display> tag */
	public static final int DISPLAY = 1;
	
	private String title, author, date;
	// text for dynamic worksheets: 0 .. above, 1 .. below
	private String[] worksheetText = new String[2];

	// ConstructionElement List (for objects of type ConstructionElement)
	private ArrayList<ConstructionElement> ceList;

	// AlgoElement List (for objects of type AlgoElement)
	private ArrayList<AlgoElement> algoList; // used in updateConstruction()

	/** Table for (label, GeoElement) pairs, contains global variables*/
	protected HashMap<String,GeoElement> geoTable;
	/** Table for (label, GeoElement) pairs, contains local variables */
	protected HashMap<String,GeoElement> localVariableTable;

	// set with all labeled GeoElements in ceList order
	private TreeSet<GeoElement> geoSet; //generic Object replaced by GeoElement (Zbynek Konecny, 2010-06-14)
	private GeoPoint origin;
	// set with all labeled GeoElements in alphabetical order
	private TreeSet<GeoElement> geoSetLabelOrder;
	
	// list of random numbers or lists
	private TreeSet<GeoElement> randomElements;

	// a map for sets with all labeled GeoElements in alphabetical order of
	// specific types
	// (points, lines, etc.)
	private HashMap<Integer,TreeSet<GeoElement>> geoSetsTypeMap;	

	// list of Macro commands used in this construction
	private ArrayList<Macro> usedMacros;
	
	// list of algorithms that need to be updated when EuclidianView changes
	private ArrayList<EuclidianViewAlgo> euclidianViewAlgos;

	/** UndoManager */
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
	private HashMap<GeoElement,GeoElement> redefineMap;

	// showOnlyBreakpoints in construction protocol
	private boolean showOnlyBreakpoints;

	// construction belongs to kernel
	private Kernel kernel;

	// axis objects
	private GeoAxis xAxis, yAxis;
	private String xAxisLocalName, yAxisLocalName;

	/** default elements */
	protected ConstructionDefaults consDefaults;

	/**
	 * Creates a new Construction.
	 * @param k Kernel
	 */
	public Construction(Kernel k) {
		this(k, null);
	}

	/**
	 * Returns the point (0,0)
	 * @return point (0,0)
	 */
	public final GeoPoint getOrigin(){
		if(origin==null){
			origin=new GeoPoint(this);
			origin.setCoords(0.0, 0.0, 1.0);
		}
		return origin;
	}
	/**
	 * Creates a new Construction.
	 * @param k Kernel
	 * @param parentConstruction parent construction (used for macro constructions)
	 */
	Construction(Kernel k, Construction parentConstruction) {
		kernel = k;

		ceList = new ArrayList<ConstructionElement>();
		algoList = new ArrayList<AlgoElement>();
		step = -1;

		geoSet = new TreeSet<GeoElement>();
		geoSetLabelOrder = new TreeSet<GeoElement>(new LabelComparator());
		geoSetsTypeMap = new HashMap<Integer,TreeSet<GeoElement>>();
		euclidianViewAlgos = new ArrayList<EuclidianViewAlgo>();

		if (parentConstruction != null)
			consDefaults = parentConstruction.getConstructionDefaults();
		else
			newConstructionDefaults();
			//consDefaults = new ConstructionDefaults(this);

		initAxis();

		geoTable = new HashMap<String,GeoElement>(200);
		localVariableTable = new HashMap<String,GeoElement>();
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
	 * Returns the last GeoElement object in the construction list.
	 * @return the last GeoElement object in the construction list.
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
	 * @return construction default object of this construction.
	 */
	final public ConstructionDefaults getConstructionDefaults() {
		return consDefaults;
	}

	/**
	 * Make geoTable contain only xAxis and yAxis
	 */
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

	/** 
	 * Renames xAxis and yAxis in the geoTable
	 * and sets *AxisLocalName-s acordingly 
	 */
	public void updateLocalAxesNames() {		
		geoTable.remove(xAxisLocalName);
		geoTable.remove(yAxisLocalName);

		Application app = kernel.getApplication();
		xAxisLocalName = app.getPlain("xAxis");
		yAxisLocalName = app.getPlain("yAxis");
		geoTable.put(xAxisLocalName, xAxis);
		geoTable.put(yAxisLocalName, yAxis);	
	}

	/**
	 * Returns current kernel
	 * @return current kernel
	 */
	public Kernel getKernel() {
		return kernel;
	}

	/**
	 * Returns current application
	 * @return current application
	 */
	public Application getApplication() {
		return kernel.getApplication();
	}

	/** 
	 * Returns equation solver
	 * @return equation solver
	 */
	public EquationSolver getEquationSolver() {
		return kernel.getEquationSolver();
	}

	/**
	 * Returns extremum finder
	 * @return extremum finder
	 */
	public ExtremumFinder getExtremumFinder() {
		return kernel.getExtremumFinder();
	}

	/**
	 * Returns x-axis
	 * @return x-axis
	 */
	final public GeoAxis getXAxis() {
		return xAxis;
	}

	/**
	 * Returns y-axis
	 * @return y-axis
	 */
	final public GeoAxis getYAxis() {
		return yAxis;
	}

	/**
	 * If this is set to true new construction elements won't get labels.
	 * @param flag true iff labelcreation should be supressed
	 */
	public void setSuppressLabelCreation(boolean flag) {
		supressLabelCreation = flag;
	}

	/**
	 * Returns true iff new construction elements won't get labels.
	 * @return true iff new construction elements won't get labels.
	 */
	public boolean isSuppressLabelsActive() {
		return supressLabelCreation;
	}

	/**
	 * Sets how steps in the construction protocol are handled.
	 * @param flag true iff construction protocol should show only breakpoints
	 */
	public void setShowOnlyBreakpoints(boolean flag) {
		showOnlyBreakpoints = flag;
	}

	/**
	 * True iff construction protocol should show only breakpoints
	 * @return true iff construction protocol should show only breakpoints
	 */
	final public boolean showOnlyBreakpoints() {
		return showOnlyBreakpoints;
	}

	/*
	 * Construction List Management
	 */

	/**
	 * Returns the ConstructionElement for the given construction index.
	 * @return the ConstructionElement for the given construction index.
	 * @param index Construction index of element to look for
	 */
	public ConstructionElement getConstructionElement(int index) {
		if (index < 0 || index >= ceList.size())
			return null;
		return (ConstructionElement) ceList.get(index);
	}

	/**
	 * Returns a set with all labeled GeoElement objects of this construction in
	 * construction order.
	 * @return set with all labeled geos in construction order.
	 */
	final public TreeSet<GeoElement> getGeoSetConstructionOrder() {
		return geoSet;
	}

	/**
	 * Returns a set with all labeled GeoElement objects of this construction in
	 * alphabetical order of their labels.
	 * @return set with all labeled geos in alphabetical order.
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
	 * @return Set of elements of given type.
	 */
	final public TreeSet<GeoElement> getGeoSetLabelOrder(int geoClassType) {
		TreeSet<GeoElement> typeSet = (TreeSet<GeoElement>) geoSetsTypeMap.get(geoClassType);
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
	 * @param ce Construction element to be added
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
	 * @param ce ConstuctionElement to be removed
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
	 * @param algo to be added
	 * @see #updateConstruction()
	 */
	public void addToAlgorithmList(AlgoElement algo) {
		algoList.add(algo);
	}

	/**
	 * Removes the given algorithm from this construction's algorithm list
	 * @param algo algo to be removed
	 */
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
	 * @param fromIndex index of element to be moved
	 * @param toIndex target index of this element
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
	 * @param geo GeoElement to be looked for
	 * @return true iff geo or its parent algo are in construction list
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
		
		// copy array to avoid problems with the list changing during the loop
		// eg Polygon[A,B,RandomBetween[4,5]]
		// http://www.geogebra.org/forum/viewtopic.php?p=56618
        ArrayList<AlgoElement> tempList = new ArrayList<AlgoElement>(algoList);

        // update all algorithms
        for (int i = 0; i < size; ++i) {
                AlgoElement algo = tempList.get(i);
			
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

	/**
	 * Updates all algorithms in this construction
	 */
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
	 * @param algo Algo to be registred
	 */
	final void registerEuclidianViewAlgo(EuclidianViewAlgo algo) {
		if (!euclidianViewAlgos.contains(algo))
			euclidianViewAlgos.add(algo);
	}
	
	/**
	 * Unregisters an algorithm that wants to be notified when setEuclidianViewBounds() is called.	 
	 * @param algo Algo to be unregistred
	 */
	void unregisterEuclidianViewAlgo(EuclidianViewAlgo algo) {		
		euclidianViewAlgos.remove(algo);
	}
	
	/**
	 * Calls update on all euclidian view algos
	 * @return true iff there were any algos to update
	 */
	public boolean notifyEuclidianViewAlgos() {
		boolean didUpdate = false;		
		int size = euclidianViewAlgos.size();	
		for (int i=0; i < size; i++) {
			didUpdate = true;
			((EuclidianViewAlgo) euclidianViewAlgos.get(i)).euclidianViewUpdate();			
		}		
		return didUpdate;
	}	
	
	/**
	 * Returns true iff there are any euclidian view algos in this construction
	 * @return true iff there are any euclidian view algos in this construction
	 */
	public boolean hasEuclidianViewAlgos() {
		return euclidianViewAlgos.size() > 0;
	}
	

	/**
	 * Updates all algos 
	 * @author Michael Borcherds 
	 * @version 2008-05-15
	 * @return true iff there were any algos that wanted update
	 */
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
			if (app.hasFullGui())
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
	 * @return Total number of construction steps.
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
	 * @return current construction step position.
	 */
	public int getStep() {
		return step;
	}

	/*
	 * GeoElementTable Management
	 */
	/**
	 * Adds given GeoElement to a table where (label, object) pairs are stored.
	 * @param geo GeoElement to be added, must be labeled
	 * @see #removeLabel(GeoElement)
	 * @see #lookupLabel(String)
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
	 * @param geo GeoElement to be removed
	 * @see #putLabel(GeoElement)
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
		TreeSet<GeoElement> typeSet = geoSetsTypeMap.get(type);
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

	private TreeSet<GeoElement> createTypeSet(int type) {
		TreeSet<GeoElement> typeSet = new TreeSet<GeoElement>(new LabelComparator());
		geoSetsTypeMap.put(type, typeSet);
		return typeSet;
	}

	private void removeFromGeoSets(GeoElement geo) {
		geoSet.remove(geo);
		geoSetLabelOrder.remove(geo);

		// set ordered type set
		int type = geo.getGeoClassType();
		TreeSet<GeoElement> typeSet = geoSetsTypeMap.get(type);
		if (typeSet != null)
			typeSet.remove(geo);

		/*
		 * Application.debug("*** geoSet order (remove " + geo + ") ***");
		 * Iterator it = geoSet.iterator(); int i = 0; while (it.hasNext()) {
		 * GeoElement g = (GeoElement) it.next();
		 * Application.debug(g.getConstructionIndex() + ": " + g); }
		 */
	}

	/**
	 * Adds a geo to list of local variables
	 * @param varname Variable name
	 * @param geo Variable geo
	 */
	final public void addLocalVariable(String varname, GeoElement geo) {
		localVariableTable.put(varname, geo);
		geo.setLocalVariableLabel(varname);
	}

	/**
	 * Removes local variable of given name
	 * @param varname name of variable to be removed
	 */
	final public void removeLocalVariable(String varname) {
		localVariableTable.remove(varname);
	}

	/**
	 * Returns a GeoElement for the given label. Note: only geos with
	 * construction index 0 to step are available.
	 * @param label label to be looked for
	 * @return may return null
	 */
	GeoElement lookupLabel(String label) {
		return lookupLabel(label, false);
	}

	/**
	 * Returns a GeoElement for the given label. Note: only geos with
	 * construction index 0 to step are available.
	 * @param label label to be looked for
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
		GeoElement geo = geoTableVarLookup(label);

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
				geo = geoTableVarLookup(upperCaseLabel);
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
	 * @see #willAutoCreateGeoElement()
	 */
	private GeoElement autoCreateGeoElement(String label) {		
		GeoElement createdGeo = null;
		boolean fix = true;
		boolean auxilliary = true;
		
		// if referring to variable "i" (complex) that is undefined, create it
		if (label.equals("i") || label.equals(Unicode.IMAGINARY)) {
			
			GeoElement geo = kernel.lookupLabel(Unicode.IMAGINARY);
			
			if (geo != null && geo.isGeoPoint() && ((GeoPoint)geo).isI()) {
				createdGeo = (GeoPoint)geo;
			} else {			
			
				GeoPoint point = new GeoPoint(this);
				point.setCoords(0.0d, 1.0d, 1.0d);
				point.setEuclidianVisible(false);
				point.setComplex();
				point.setIsI();
				createdGeo = point;
				
				if (geo == null) label = Unicode.IMAGINARY; // else just leave as "i" if label not free
				
			}
		}

		// if referring to variable "e" (Euler no) that is undefined, create it
		// this is then changed into exp(x) in ExpressionNode.resolveVariables()
		else if (label.equals("e")) {
			GeoNumeric number = new GeoNumeric(this);
			number.setValue(Math.E);
			number.setNeedsReplacingInExpressionNode();
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
				GeoElement neighbourCell = geoTableVarLookup(col + (row - 1));
				if (neighbourCell == null) // look below
					neighbourCell = geoTableVarLookup(col + (row + 1));
	
				label = col + row;			
				createdGeo = createSpreadsheetGeoElement(neighbourCell, label);
			}
		}	
			
		return createdGeo;
	}
	
	/**
	 * Returns whether the specified label will automatically create a GeoElement
	 * when autoCreateGeoElement() is called with it.
	 * @param label Label
	 * @return true iff the label will create new geo when autoCreateGeoElement() is called with it.
	 * 
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
	 * @return new GeoElement of desired type
	 * @param neighbourCell another geo of the desired type
	 * @param label Label for the new geo
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

	/**
	 * Looks for geo with given label, doesn't work for e.g. A$1
	 * @param label Label to be looked up
	 * @return Geo with given label
	 */
	GeoElement geoTableVarLookup(String label) {
		GeoElement ret = (GeoElement) geoTable.get(label);	
		return ret;
	}

	/**
	 * Returns true if label is not occupied by any GeoElement.
	 * @param label label to be checked
	 * @return true iff label is not occupied by any GeoElement.
	 */
	public boolean isFreeLabel(String label) {
		if (label == null)
			return false;
		else
			return !geoTable.containsKey(label) && !isDependentLabel(label);
	}
	
	
	
	///////////////////////////////////////////////
	// LABELS DEPENDING ON ALGOS
	///////////////////////////////////////////////
	

	/** tree for dependecies label--algo */
    private TreeMap<String, AlgoElementWithResizeableOutput> labelDependsOn = new TreeMap<String, AlgoElementWithResizeableOutput>();
    
    /**
     * set that the label depends on the algo.
     * Used when loading file, algos implementing AlgoElementWithResizeableOutput
     * tell that output labels depend on it (used with {@link #resolveLabelDependency(String, int)} when element
     * is handled) 
     * @param label
     * @param algo
     */
    public void setLabelDependsOn(String label, AlgoElementWithResizeableOutput algo){
    	//Application.debug(label);
    	if (label!=null)
    		labelDependsOn.put(label, algo);
    }
    
    
    
    
    /**
     * when a new element is handled (on file loading), if the label
     * depend on an AlgoElementWithResizeableOutput, the label will be set 
     * on its output or the GeoElement will be created by the algo's output handlers
     * @param label Label that depends on an AlgoElementWithResizeableOutput
     * @param type 
     * @return geo, possibly already computed by the algo
     */
    public GeoElement resolveLabelDependency(String label, int type){
    	AlgoElementWithResizeableOutput algo = labelDependsOn.get(label);
    	
    	//Application.debug(label);
    	
    	GeoElement ret;
    	if (algo!=null){
    		//Application.debug(label+", type:"+type);

    		labelDependsOn.remove(label);
    		ret=algo.addLabelToOutput(label,type);
    		//Application.debug("ret:"+ret.toString());
    	}else
    		ret=null;
    	
    	return ret;
    }
    
    
    /**
     * says if the label depends on any algo
     * @param label
     * @return if the label depends on any algo
     */
    public boolean isDependentLabel(String label){
    	
    	//Application.debug("isDependentLabel("+label+")="+labelDependsOn.containsKey(label));
    	
    	return labelDependsOn.containsKey(label);
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

	/**
	 * Stores current state of construction.
	 * @see UndoManager#storeUndoInfo 
	 */
	public void storeUndoInfo() {
		// undo unavailable in applets
		//if (getApplication().isApplet()) return;

		undoManager.storeUndoInfo();		
	}

	/**
	 * Restores undo info
	 * @see UndoManager#restoreCurrentUndoInfo()
	 */
	public void restoreCurrentUndoInfo() {
		// undo unavailable in applets
		//if (getApplication().isApplet()) return;
		collectRedefineCalls = false;
		
		if (undoManager != null)
			undoManager.restoreCurrentUndoInfo();
	}

	/**
	 * Redoes last undone step
	 */
	public void redo() {
		// undo unavailable in applets
		//if (getApplication().isApplet()) return;

		undoManager.redo();
	}

	/**
	 * Undoes last operation
	 */
	public void undo() {
		// undo unavailable in applets
		//if (getApplication().isApplet()) return;

		undoManager.undo();
	}

	/**
	 * Returns true iff undo is possible
	 * @return true iff undo is possible
	 */
	public boolean undoPossible() {
		// undo unavailable in applets
		//if (getApplication().isApplet()) return false;

		return undoManager != null && undoManager.undoPossible();
	}

	/**
	 * Returns true iff redo is possible
	 * @return true iff redo is possible
	 */
	public boolean redoPossible() {
		// undo unavailable in applets
		//if (getApplication().isApplet()) return false;

		return undoManager != null && undoManager.redoPossible();
	}


	/**
	 * Replaces oldGeo by newGeo in the current construction.
	 * This may change the logic of the
	 * construction and is a very powerful operation
	 * @param oldGeo Geo to be replaced.
	 * @param newGeo Geo to be used instead.
	 * @throws Exception 
	 */
	public void replace(GeoElement oldGeo, GeoElement newGeo) throws Exception {
		if (oldGeo == null || newGeo == null || oldGeo == newGeo)
			return;

		
		// put back, breaks the Rigid polygon Tool, see #379
		
		///* removed, see ticket #379
		// * http://www.geogebra.org/trac/ticket/379
		
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
		}//*/
		
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
			for (int i=0; i < newGeoAlgo.getOutputLength(); i++) {
				GeoElement geo = newGeoAlgo.getOutput(i);
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
		TreeSet<GeoElement> predList = newGeo.getAllPredecessors();

		// check if moving is needed
		// find max construction index of newGeo's predecessors and newGeo
		// itself
		Iterator<GeoElement> it = predList.iterator();
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
	 * @see #processCollectedRedefineCalls()
	 */
	public void startCollectingRedefineCalls() {
		collectRedefineCalls = true;
		if (redefineMap == null)
			redefineMap = new HashMap<GeoElement,GeoElement>();
		redefineMap.clear();
	}
	
	/**
	 * Stops collecting redefine calls.
	 * @see #processCollectedRedefineCalls()
	 */
	public void stopCollectingRedefineCalls() {
		collectRedefineCalls = false;
		if (redefineMap != null)
			redefineMap.clear();
	}
	
	/**
	 * Processes all collected redefine calls as a batch to improve performance.
	 * @see #startCollectingRedefineCalls()
	 * @throws Exception
	 */
	public void processCollectedRedefineCalls() throws Exception {
		collectRedefineCalls = false;
		
		if (redefineMap == null || redefineMap.size() == 0)
			return;
		
		// get current construction XML
		StringBuilder consXML = getCurrentUndoXML();
		
		// replace all oldGeo -> newGeo pairs in XML
		Iterator<GeoElement> it = redefineMap.keySet().iterator();			
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
	 * @param sb StringBuilder to which the XML is appended
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
	 * Returns Execute command that can recreate this construction
	 * @param sb
	 */
	public void toExecuteCommand(StringBuilder sb){
		int size = ceList.size();
		sb.setLength(0);
		sb.append("Execute[{");
		ConstructionElement ce;
		for (int i = 0; i < size; ++i) {
			sb.append(i==0?'"':",\"");
			ce = (ConstructionElement) ceList.get(i);
			if(ce.isIndependent()){
				sb.append(ce.toString().replace("\"", "\"+UnicodeToLetter[34]+\""));
			}
			else sb.append(ce.getCommandDescription().replace("\"", "\"+UnicodeToLetter[34]+\""));
			sb.append('"');
		}
		sb.append("}]");
	}

	/**
	 * Returns this construction in I2G format. Intergeo File Format.
	 * (Yves Kreis)
	 * @param sb String builder to which the XML is appended
	 * @param mode output mode, either CONSTRUCTION (0) or DISPLAY (1)
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
	 * @return StringBuilder with xml  of this construction.
	 */
	public StringBuilder getCurrentUndoXML() {
		return MyXMLio.getUndoXML(this);
	}

	/**
	 * Returns construction's author
	 * @return construction's author
	 */
	public String getAuthor() {
		return (author != null) ? author : "";
	}

	/**
	 * Returns construction's date
	 * @return construction's date
	 */
	public String getDate() {
		return (date != null) ? date : "";
	}

	/**
	 * Returns construction's title
	 * @return construction's title
	 */
	public String getTitle() {
		return (title != null) ? title : "";
	}

	/**
	 * Sets construction's author
	 * @param string new author
	 */
	public void setAuthor(String string) {
		author = string;
	}

	/**
	 * Sets construction's date
	 * @param string new date
	 */
	public void setDate(String string) {
		date = string;
	}

	/**
	 * Sets construction's title
	 * @param string new title
	 */
	public void setTitle(String string) {
		title = string;
	}

	/**
	 * Returns part of worksheet text
	 * @param i 0 for first part, 1 for second part  
	 * @return given part of worksheet text
	 */
	public String getWorksheetText(int i) {
		return (worksheetText[i] != null) ? worksheetText[i] : "";
	}

	/**
	 * Sets part of worksheet text
	 * @param i 0 for first part, 1 for second part  
	 * @param text new text for that part
	 */
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

	private class LabelComparator implements Comparator<GeoElement> {
		public int compare(GeoElement ob1, GeoElement ob2) {
			GeoElement geo1 = ob1;
			GeoElement geo2 = ob2;
			
			return GeoElement.compareLabels(geo1.label, geo2.label);
		}
	}

	/**
	 * Add a macro to list of used macros
	 * @param macro Macro to be added
	 */
	public final void addUsedMacro(Macro macro) {
		if (usedMacros == null)
			usedMacros = new ArrayList<Macro>();
		usedMacros.add(macro);
	}

	/**
	 * Returns list of macros used in this construction
	 * @return list of macros used in this construction
	 */
	public ArrayList<Macro> getUsedMacros() {
		return usedMacros;
	}
	
	/**
	 * Adds a number to the set of random numbers of this construction.
	 * @param num Element to be added
	 */
	public void addRandomGeo(GeoElement num) {
		if (randomElements == null) 
			randomElements = new TreeSet<GeoElement>();
		randomElements.add(num);
		num.setRandomGeo(true);
	}
	
	/**
	 * Removes a number from the set of random numbers of this construction.
	 * @param num Element to be removed
	 */
	public void removeRandomGeo(GeoElement num) {
		if (randomElements != null) 
			randomElements.remove(num);
		num.setRandomGeo(false);
	}
	
	/**
     * Updates all random numbers of this construction.
     */
    final public void updateAllRandomGeos() {    	
    	if (randomElements == null) return;
    	
    	Iterator<GeoElement> it = randomElements.iterator();
    	while (it.hasNext()) {
    		GeoElement num = (GeoElement) it.next();
    		num.updateRandomGeo();
    	}    	     
    }    
    
   
    
}
