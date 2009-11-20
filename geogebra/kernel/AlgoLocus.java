/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


import geogebra.main.Application;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/** 
 * locus line for Q dependent on P
 */
public class AlgoLocus extends AlgoElement implements EuclidianViewAlgo {
	
	// TODO: update locus algorithm
	// * locus of Q=(x(B), a) with a= integral[f(x), 0, x(B)] and B is point on x-axis  freezes GeoGebra
	//   MAX_TIME handling does not solve this yet
	//
		
	
	// maximum time for the computation of one locus point in millis
	public static int MAX_TIME_FOR_ONE_STEP = 500;
	
	private static final long serialVersionUID = 1L;
	private static int MAX_X_PIXEL_DIST = 5;
	private static int MAX_Y_PIXEL_DIST = 5;

    private GeoPoint movingPoint, locusPoint; 	// input       
    private GeoLocus locus; 	// output   
    
    // for efficient dependency handling
    private GeoElement [] efficientInput, standardInput; 
    
    private Path path; 			// path of P
    private PathMover pathMover;
    private int pointCount;
    
    // copies of P and Q in a macro kernel
    private GeoPoint Pcopy, Qcopy, PstartPos, QstartPos;  
    private double lastX, lastY, maxXdist, 
		maxYdist, xmin, xmax, ymin, ymax,
		farXmin, farXmax, farYmin, farYmax;
    //private Line2D.Double tempLine = new Line2D.Double();
    private Rectangle2D.Double nearToScreenRect = new Rectangle2D.Double();   
    private boolean continuous;
    private boolean lastFarAway;
    private boolean foundDefined;
    private boolean maxTimeExceeded;
    private Construction macroCons;
    private MacroKernel macroKernel;
    //private AlgorithmSet macroConsAlgoSet;
	// list with all original elements used for the macro construction
    private TreeSet locusConsOrigElements, Qin; 
    
    private long countUpdates =0;
    
   // private Updater updater;

    AlgoLocus(Construction cons,  String label, GeoPoint Q, GeoPoint P) {
        super(cons);
        this.movingPoint = P;
        this.locusPoint = Q;    	
        
        path = P.getPath();
        pathMover = path.createPathMover();        
        
        QstartPos = new GeoPoint(cons);
        PstartPos = new GeoPoint(cons);
            
        init();        
        updateScreenBorders();
        locus = new GeoLocus(cons);                      
        setInputOutput(); // for AlgoElement       
        cons.registerEuclidianViewAlgo(this);
        compute();
        
     	// we may have created a starting point for the path now
        // make sure that the movingPoint in the main construction
        // uses the correct path parameter for it
        path.pointChanged(P);
   
        locus.setLabel(label);
    }
    
//    private void printMacroConsStatus() {
//		System.out.print("MOVER POINT: " + Pcopy);
//		System.out.print(", pp.t: " + Pcopy.getPathParameter().t);    		
//		System.out.println(", locus point: " + Qcopy);
//		
//		TreeSet geos = macroCons.getGeoSetLabelOrder();
//		Iterator it = geos.iterator();
//		while (it.hasNext()) {
//			GeoElement geo = (GeoElement) it.next();
//			System.out.println("  " + geo);
//		}
//    }
 
    protected String getClassName() {
        return "AlgoLocus";
    }
    
    public ArrayList getMoveableInputPoints() {
    	return null;
    }
    
    public GeoPoint getQ() {
    	return locusPoint;
    }
    
    private void init() {        
    	// copy the construction    	
    	Qin = locusPoint.getAllPredecessors(); // all parents of Q
    	
    	// get intersection of all children of P and all parents of Q    	       	
    	locusConsOrigElements = new TreeSet(); 
    	Iterator it = Qin.iterator();
    	while (it.hasNext()) {
    		GeoElement parent = (GeoElement) it.next();
    		    	        		
    		if (parent.isLabelSet() && parent.isChildOf(movingPoint)) {
    			// note: locusConsOrigElements will contain AlgoElement and GeoElement objects
    			Macro.addDependentElement(parent, locusConsOrigElements);   
    		}
    	}    
    	
    	// ensure that P and Q have labels set
    	// Note: we have to undo this at the end of this method !!!
    	boolean isLabeledP = movingPoint.isLabelSet();    	
    	if (!isLabeledP) {
    		movingPoint.label = movingPoint.getDefaultLabel();
    		movingPoint.labelSet = true;
    	}
    	boolean isLabeledQ = locusPoint.isLabelSet();    	
    	if (!isLabeledQ) {
    		locusPoint.label = locusPoint.getDefaultLabel();
    		locusPoint.labelSet = true;
    	}
    	    	
    	// add moving point on line
    	locusConsOrigElements.add(movingPoint);

    	// add locus creating point and its algorithm to locusConsOrigElements 
		Macro.addDependentElement(locusPoint, locusConsOrigElements); 	   	
    	    	      
    	// create macro construction
    	buildLocusMacroConstruction(locusConsOrigElements);
    	
    	// if we used temp labels remove them again
    	if (!isLabeledP) movingPoint.labelSet = false;    
    	if (!isLabeledQ) locusPoint.labelSet = false;
    }

    // for AlgoElement
    protected void setInputOutput() {
    	// it is inefficient to have Q and P as input
    	// let's take all independent parents of Q
    	// and the path as input
    	TreeSet inSet = new TreeSet();
    	inSet.add(path.toGeoElement());
    	
    	// we need all independent parents of Q PLUS
    	// all parents of Q that are points on a path    	
    	Iterator it = Qin.iterator();
    	while (it.hasNext()) {
    		GeoElement geo = (GeoElement) it.next();
    		if (geo.isIndependent() || geo.isPointOnPath()) {
    			inSet.add(geo);    			
    		}
    	}    	 
    	// remove P from input set!
    	inSet.remove(movingPoint);    
    	
    	efficientInput = new GeoElement[inSet.size()];
    	it = inSet.iterator();
    	int i=0;
    	while (it.hasNext()) {    		
    		efficientInput[i] = (GeoElement) it.next();    		
    		i++;
    	}    	
    	
    	// the standardInput array should be used for
    	// the dependency graph
    	standardInput = new GeoElement[2];
    	standardInput[0] = locusPoint;
    	standardInput[1] = movingPoint;    	
    	
        output = new GeoElement[1];
        output[0] = locus;
        
        // handle dependencies
        setEfficientDependencies(standardInput, efficientInput);           
    }     
    
    final public String toString() {    	
        return getCommandDescription();        
    }

    GeoLocus getLocus() {
        return locus;
    }    
    
   
    private void buildLocusMacroConstruction(TreeSet locusConsElements) {       	
    	// build macro construction
    	macroKernel = new MacroKernel(kernel); 
    	macroKernel.setGlobalVariableLookup(true);
    	
    	// tell the macro construction about reserved names:
    	// these names will not be looked up in the parent
    	// construction
    	Iterator it = locusConsElements.iterator();
    	while (it.hasNext()) {
    		ConstructionElement ce = (ConstructionElement) it.next();
    		if (ce.isGeoElement()) {
    			GeoElement geo = (GeoElement) ce;
    			macroKernel.addReservedLabel(geo.getLabel()); 
    		}    					
    	}    	    	
    	
    	try {    	
    		// get XML for macro construction of P -> Q        	
        	String locusConsXML = Macro.buildMacroXML(kernel, locusConsElements);  

    		macroKernel.loadXML(locusConsXML);
    	
	    	// get the copies of P and Q from the macro kernel
	    	Pcopy = (GeoPoint) macroKernel.lookupLabel(movingPoint.label);
	    	Pcopy.setFixed(false);
	    	Pcopy.setPath(movingPoint.getPath());
	    	
	    	Qcopy = (GeoPoint) macroKernel.lookupLabel(locusPoint.label);
	    	macroCons = macroKernel.getConstruction();

	    	/*
	    	// make sure that the references to e.g. start/end point of a segment are not
	    	// changed later on. This is achieved by setting isMacroOutput to true	    	
	    	it = macroCons.getGeoElementsIterator();          
          	while (it.hasNext()) {	          	
    	      	GeoElement geo = (GeoElement) it.next();
    	      	geo.isAlgoMacroOutput = true;
          	}
          	Pcopy.isAlgoMacroOutput = false;
          	*/         	
    	} catch (Exception e) {
    		e.printStackTrace();    
    		locus.setUndefined();
    		macroCons = null;
    	}    
    	   
//    	//Application.debug("P: " + P + ", kernel class: " + P.kernel.getClass());
//    	Application.debug("Pcopy: " + Pcopy  + ", kernel class: " + Pcopy.kernel.getClass());
//    	//Application.debug("P == Pcopy: " + (P == Pcopy));
//    	//Application.debug("Q: " + Q  + ", kernel class: " + Q.kernel.getClass());
//    	Application.debug("Qcopy: " + Qcopy  + ", kernel class: " + Qcopy.kernel.getClass());
//    	//Application.debug("Q == Qcopy: " + (Q == Qcopy));
    }
    
    /**
     *  Set all elements in locusConsElements 
     *  to the current values of the main construction    
     */
    private void resetMacroConstruction() {       
      	Iterator it = locusConsOrigElements.iterator();
      	while (it.hasNext()) {
      		ConstructionElement ce = (ConstructionElement) it.next();
      		if (ce.isGeoElement()) {
	      		GeoElement geoOrig = (GeoElement) ce;  
	      		// do not copy functions, their expressions already
	      		// include references to the correct other geos 
	      		if (!geoOrig.isGeoFunction()) {
		      		GeoElement geoCopy = macroCons.lookupLabel(geoOrig.label);   
		      		if (geoCopy != null) {
			  			try {	    				
			  				geoCopy.set(geoOrig);	  				
			  				geoCopy.update();      	      			 
			  			} catch (Exception e) {
			  				Application.debug("AlgoLocus: error in resetMacroConstruction(): " + e.getMessage());
			  			}
		      		}
	      		}
      		}
      	}               	      
      }             

    // compute locus line
    final protected void compute() {    	    
    	if (!movingPoint.isDefined() || macroCons == null) {    		
    		locus.setUndefined();
    		return;
    	}
 
    	locus.clearPoints();    
    	clearCache();
    	pointCount = 0;	 	     	
    	useCache = 0;
    	countUpdates = 0;
    	maxTimeExceeded = false;
    	foundDefined = false;
    	boolean prevQcopyDefined = false;
    	int max_runs;      	    
		
		// continuous kernel?
    	continuous = kernel.isContinuous();
    	macroKernel.setContinuous(continuous);  
    	
        
     	
    	// update macro construction with current values of global vars 
    	resetMacroConstruction();
    	macroCons.updateConstruction();
    	
    	// use current position of movingPoint to start Pcopy
    	pathMover.init(Pcopy);	
    	
    	if (continuous) {      		       	    		
    		// continous constructions may need several parameter run throughs
          	// to draw all parts of the locus
    		max_runs = GeoLocus.MAX_PATH_RUNS; 
    	} 
    	else {
    		max_runs = 1; 
    	}     	 
    	    	    
    	// update Pcopy to compute Qcopy      	
    	pcopyUpdateCascade();    	    	    
   		prevQcopyDefined = Qcopy.isDefined();   	
    	
    	// move Pcopy along the path
    	// do this until Qcopy comes back to its start position
    	// for continuous constructions 
   		//this may require several runs of Pcopy along the whole path
   		
    	int runs = 1;   
    	int MAX_LOOPS = 2*PathMover.MAX_POINTS;
    	int whileLoops = 0;    	 
    	    
    	do {    		    		
    		boolean finishedRun = false;    	
	        while ( !finishedRun && !maxTimeExceeded &&
	        		 pointCount <= PathMover.MAX_POINTS && 
	        		 whileLoops <= MAX_LOOPS) 
	        {		    	        		        	
	        	whileLoops++;	      
	        		        	
	        	// lineTo may be false due to a parameter jump
	        	// i.e. param in [0,1] gets bigger than 1 and thus jumps to 0   
	        	boolean parameterJump = !pathMover.getNext(Pcopy);		       			
	        	boolean stepChanged = false;
	        
	        	// update construction        
	        	pcopyUpdateCascade();
	        	
	       		// Qcopy DEFINED
	       		if (Qcopy.isDefined()) {		       				       			
	       			// STANDARD CASE: no parameter jump
	       			if (!parameterJump) {	       					   				       			      		
	   					// make steps smaller until distance ok to connect with last point
	       				while (Qcopy.isDefined() && !distanceOK(Qcopy) && !maxTimeExceeded)
	       				{			
	       					//go back and try smaller step	  
	       		        	boolean smallerStep = pathMover.smallerStep();	   
	       					if (!smallerStep) break;
	       					
	       					stepChanged = true;	       					
	       					pathMover.stepBack(); 
	       					pathMover.getNext(Pcopy);       					

	       					// update construction        
	       		        	pcopyUpdateCascade();	               		
	       				}	       					       					       			
	       				
	       				if (Qcopy.isDefined()) {
	       					// draw point
	       					insertPoint(Qcopy.inhomX, Qcopy.inhomY, distanceSmall(Qcopy));	
	       					prevQcopyDefined = true;
	       				}	       					       				
		       		}
	       			
	       			// PARAMETER jump: !lineTo
	       			else {	   
	       				// draw point
	       				insertPoint(Qcopy.inhomX, Qcopy.inhomY, distanceSmall(Qcopy));	
	       				prevQcopyDefined = true;
	       			}	       				       			
	       		}
	       		
	       		// Qcopy NOT DEFINED
       			else {    
       				// check if we moved from defined to undefined case:
       				// step back and try with smaller step
       				if (prevQcopyDefined && !parameterJump) {
       					pathMover.stepBack(); 
       					// set smallest step
       					if (!pathMover.smallerStep()) {     					
       						prevQcopyDefined = false;  
       					} else
       						stepChanged = true;
       				}       				       			       				
       				       				
       				// add better undefined case support for continuous curves
       				// maybe change orientation of path mover
       			}
	       		
	       		// if we didn't decrease the step width increase it
   				if (!stepChanged) {
   					pathMover.biggerStep();
   				}
	       		       			       			      
	       		// end of run: the next step would pass the start position	       			       		
	       		if (!pathMover.hasNext()) {	 
	       			if (distanceSmall(QstartPos)) {
	       				// draw line back to first point when it's close enough
	       				insertPoint(QstartPos.inhomX, QstartPos.inhomY, true);	
	       				finishedRun = true;
	       			} 
	       			else {		       			
		       			// decrease step until another step is possible
		       			while (!pathMover.hasNext() && pathMover.smallerStep());
		       			
		       			// no smaller step possible: run finished
		       			if (!pathMover.hasNext())		
		       				finishedRun = true;     
	       			}
	       		}
	        }		        
	        
	        // calculating the steps took too long, so we stopped somewhere
	        if (maxTimeExceeded) {
	        	System.err.println("AlgoLocus: max time exceeded");	        	
	        	return;	        
	        } 
	        else {	        
		        // make sure that Pcopy is back at startPos now
		        // look at Qcopy at startPos	    	         
				Pcopy.set((GeoElement) PstartPos);
				pcopyUpdateCascade();
				if (Qcopy.inhomX != lastX || Qcopy.inhomY != lastY)
					insertPoint(Qcopy.inhomX, Qcopy.inhomY, distanceSmall(Qcopy));				
		    				
//	    		 Application.debug("run: " + runs);
//	    		 Application.debug("pointCount: " + pointCount);
//		       	 Application.debug("  startPos: " + QstartPos); 
//		       	 Application.debug("  Qcopy: " + Qcopy);
	    		 	        
	    		// we are finished with all runs
	    		// if we got back to the start position of Qcopy
	    		// AND if the direction of moving along the path
	    		// is positive like in the beginning
		        if (pathMover.hasPositiveOrientation()) {
		        	kernel.setMinPrecision();
		        	boolean equal = QstartPos.isEqual(Qcopy);
		        	kernel.resetPrecision();
		        	if (equal)
		        		break;
		        }
		        	
	        }
	        
	        pathMover.resetStartParameter();	       
	        runs++;	        	 
        } while (runs < max_runs);
    	
    	// set defined/undefined
    	locus.setDefined(foundDefined); 

//    	System.out.println("  first point: " + locus.getMyPointList().get(0));
//    	ArrayList list = locus.getMyPointList();
//    	for (int i=list.size()-10; i < list.size()-1; i++) {
//    		System.out.println("      point: " + list.get(i));
//    	}
//    	System.out.println("  last  point: " + locus.getMyPointList().get(pointCount-1));  
    	
    	//Application.debug("LOCUS COMPUTE updateCascades: " + countUpdates + ", cache used: " + useCache);
    }
    
    /**
     * Calls Pcopy.updateCascade() to compute Qcopy. For non-continous constructions
     * caching of previous paramater positions is used. 
     */
    private void pcopyUpdateCascade() {
    	countUpdates++;  
    	    	
    	if (continuous) {
    		// CONTINOUS construction   
    		// don't use caching for continuous constructions:
        	// the same position of Pcopy can have different results for Qcopy 
    		Pcopy.updateCascade();    		
    	} 
    	else {    	    
	    	// NON-CONTINOUS construction    	
	    	// check if the path parameter's resulting Qcopy is already in cache
	    	double param = Pcopy.getPathParameter().t;
	    	Point2D.Double cachedPoint = getCachedPoint(param);        
	    	    	    	    	
	    	if (cachedPoint == null) {    		
	    		// measure time needed for update of construction
	        	long startTime = System.currentTimeMillis();	        
	        	
	        	// result not in cache: update Pcopy to compute Qcopy
	    		Pcopy.updateCascade();
	 
	       		long updateTime = System.currentTimeMillis() - startTime;	   
	       			       	
	       		// if it takes too much time to calculate a single step, we stop
	       		if (updateTime > MAX_TIME_FOR_ONE_STEP) {
	       			Application.debug("AlgoLocus: max time exceeded " + updateTime);	       			
	       			maxTimeExceeded = true;
	       		}
	    		
	    		// cache value of Qcopy    		
	    		putCachedPoint(param, Qcopy);   		
	    	} else {    		
	    		// use cached result to set Qcopy
	    		Qcopy.setCoords(cachedPoint.x, cachedPoint.y, 1.0);
	    		useCache++;
	    	}    	   
    	}	    	    

//    	if (Qcopy.isDefined()) {
//    		if (!foundDefined)
//    			System.out.print(locus.label + " FIRST DEFINED param: " + Pcopy.getPathParameter().t);
//    		else
//    			System.out.print(locus.label + " param: " + Pcopy.getPathParameter().t);
//        	System.out.println(", Qcopy: " + Qcopy);  
//    	} else {
//    		System.out.print(locus.label + " param: " + Pcopy.getPathParameter().t);
//        	System.out.println(", Qcopy: NOT DEFINED");  
//    	}
      	
    	// check found defined
    	if (!foundDefined && Qcopy.isDefined()) {
    		pathMover.init(Pcopy);
    		PstartPos.set((GeoElement) Pcopy);
        	QstartPos.set((GeoElement) Qcopy);
        	foundDefined = true;
        	
        	// insert first point
        	insertPoint(Qcopy.inhomX, Qcopy.inhomY, false);    
    	}
    }
    
    private void clearCache() {
    	for (int i=0; i < paramCache.length; i++) {
    		paramCache[i] = Double.NaN;
    		if (qcopyCache[i] == null)
    			qcopyCache[i] = new Point2D.Double();
    	}
    }
    
    private Point2D.Double getCachedPoint(double param) {
    	// search for cached parameter
    	for (int i=0; i < paramCache.length; i++) {
    		if (param == paramCache[i])
    			return qcopyCache[i];    		
    	}
    	
    	return null;
    }
    
    private void putCachedPoint(double param, GeoPoint Qcopy) {
    	cacheIndex++;
    	if (cacheIndex >= paramCache.length) cacheIndex = 0;
    	
    	paramCache[cacheIndex] = param;
    	qcopyCache[cacheIndex].x = Qcopy.inhomX;
    	qcopyCache[cacheIndex].y = Qcopy.inhomY;
    }
    
    // small cache of 3 last parameters and Qcopy positions 
    private double [] paramCache = new double[3];
    private Point2D.Double [] qcopyCache = new Point2D.Double[paramCache.length];
    private int cacheIndex = 0;   
    private long useCache = 0;
            
    private void insertPoint(double x, double y, boolean lineTo) {  
    	pointCount++;
    	    	
    	//	Application.debug("insertPoint: " + x + ", " + y + ", lineto: " + lineTo);    	
    	locus.insertPoint(x, y, lineTo);
    	lastX = x;
    	lastY = y;
    	lastFarAway = isFarAway(lastX, lastY);  
    }
    
    private boolean isFarAway(double x, double y) {
    	boolean farAway =  (x > farXmax || x < farXmin ||
    						y > farYmax || y < farYmin);    
    	return farAway;
	}
    
    private boolean distanceOK(GeoPoint Q) {   
    	boolean distanceOK;
    	
    	if (lastFarAway && isFarAway(Q.inhomX, Q.inhomY)) {
			// if last point Q' was far away and Q is far away
    		// let's check whether the line Q'Q intersects the
    		// near to screen rectangle    	        		    	
    		distanceOK = !nearToScreenRect.intersects(lastX, lastY, Q.inhomX, Q.inhomY);
    	} else {
    		distanceOK = distanceSmall(Q);             		    		
    	}

    	return distanceOK;
    }        
    
    private boolean distanceSmall(GeoPoint Q) {
    	boolean distSmall = Math.abs(Q.inhomX - lastX) < maxXdist &&
							Math.abs(Q.inhomY - lastY) < maxYdist;   
    	return distSmall;
    }    
    
    private void updateScreenBorders() {
    	xmax = kernel.getXmax();
    	xmin = kernel.getXmin();
    	ymax = kernel.getYmax();
    	ymin = kernel.getYmin();
    	
    	double widthRW = xmax - xmin;
    	double heightRW = ymax - ymin;    	
    	maxXdist = MAX_X_PIXEL_DIST / kernel.getXscale(); // widthRW / 100;
    	maxYdist = MAX_Y_PIXEL_DIST / kernel.getYscale(); // heightRW / 100;   

    	// we take a bit more than the screen
    	// itself so that we don't loose locus
    	// lines too often 
    	// that leave and reenter the screen
    	    	
    	farXmin = xmin - widthRW / 2;
    	farXmax = xmax + widthRW / 2;
    	farYmin = ymin - heightRW / 2;
    	farYmax = ymax + heightRW / 2;
 			
    	// near to screen rectangle
    	nearToScreenRect.setFrame(farXmin, farYmin, 
    			  farXmax - farXmin, 
				  farYmax - farYmin);      	
    }
    
    public void euclidianViewUpdate() {
      	updateScreenBorders();
  		update();    	
   	}
 	
}
