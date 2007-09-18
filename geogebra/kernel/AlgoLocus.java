/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


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
	public static int MAX_TIME_FOR_ONE_STEP = 200;
	
	private static final long serialVersionUID = 1L;
	private static int MAX_X_PIXEL_DIST = 8;
	private static int MAX_Y_PIXEL_DIST = 8;

    private GeoPoint P, Q; 	// input       
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
    private boolean lastFarAway;
    private Construction macroCons;
    private MacroKernel macroKernel;
    //private AlgorithmSet macroConsAlgoSet;
	// list with all original elements used for the macro construction
    private TreeSet locusConsOrigElements, Qin; 
    
   // private Updater updater;

    AlgoLocus(Construction cons,  String label, GeoPoint Q, GeoPoint P) {
        super(cons);
        this.P = P;
        this.Q = Q;
                
        path = P.getPath();
        pathMover = path.createPathMover();
        
        QstartPos = new GeoPoint(cons);
        PstartPos = new GeoPoint(cons);
            
        kernel.registerEuclidianViewAlgo(this);   
        init();  
        updateScreenBorders();
        locus = new GeoLocus(cons);                      
        setInputOutput(); // for AlgoElement       
        
        compute();
        locus.setLabel(label);
    }
 
    String getClassName() {
        return "AlgoLocus";
    }
    
    public ArrayList getMoveableInputPoints() {
    	return null;
    }
    
    public GeoPoint getQ() {
    	return Q;
    }
    
    private void init() {        
    	// copy the construction    	
    	Qin = Q.getAllPredecessors(); // all parents of Q
    	
    	// get intersection of all children of P and all parents of Q    	       	
    	locusConsOrigElements = new TreeSet(); 
    	Iterator it = Qin.iterator();
    	while (it.hasNext()) {
    		GeoElement parent = (GeoElement) it.next();
    		if (parent.isLabelSet() && parent.isChildOf(P))    
    			// note: locusConsOrigElements will contain AlgoElement and GeoElement objects
    			Macro.addDependentElement(parent, locusConsOrigElements);   
    	}    
    	
    	// ensure that P and Q have labels set
    	// Note: we have to undo this at the end of this method !!!
    	boolean isLabeledP = P.isLabelSet();    	
    	if (!isLabeledP) {
    		P.label = P.getDefaultLabel();
    		P.labelSet = true;
    	}
    	boolean isLabeledQ = Q.isLabelSet();    	
    	if (!isLabeledQ) {
    		Q.label = Q.getDefaultLabel();
    		Q.labelSet = true;
    	}
    	    	
    	// add moving point on line
    	locusConsOrigElements.add(P);

    	// add locus creating point and its algorithm to locusConsOrigElements 
		Macro.addDependentElement(Q, locusConsOrigElements); 	   	
    	    	      
    	// create macro construction
    	buildLocusMacroConstruction(locusConsOrigElements);    
    	
    	// if we used temp labels remove them again
    	if (!isLabeledP) P.labelSet = false;    
    	if (!isLabeledQ) Q.labelSet = false;
    }

    // for AlgoElement
    void setInputOutput() {
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
    	inSet.remove(P);    
    	
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
    	standardInput[0] = Q;
    	standardInput[1] = P;    	
    	
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
        	String locusConsXML = Macro.buildMacroXML(locusConsElements);  
        	
        	//System.out.println(locusConsXML);
        	
    		macroKernel.loadXML(locusConsXML);
    	
	    	// get the copies of P and Q from the macro kernel
	    	Pcopy = (GeoPoint) macroKernel.lookupLabel(P.label);
	    	Pcopy.setFixed(false);
	    	Qcopy = (GeoPoint) macroKernel.lookupLabel(Q.label);
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

//    	//System.out.println("P: " + P + ", kernel class: " + P.kernel.getClass());
//    	System.out.println("Pcopy: " + Pcopy  + ", kernel class: " + Pcopy.kernel.getClass());
//    	//System.out.println("P == Pcopy: " + (P == Pcopy));
//    	//System.out.println("Q: " + Q  + ", kernel class: " + Q.kernel.getClass());
//    	System.out.println("Qcopy: " + Qcopy  + ", kernel class: " + Qcopy.kernel.getClass());
//    	//System.out.println("Q == Qcopy: " + (Q == Qcopy));
    }
    
    private void resetMacroConstruction() {
        // set all ellements in locusConsElements 
        // to the current values of the main construction    
      	Iterator it = locusConsOrigElements.iterator();
      	while (it.hasNext()) {
      		ConstructionElement ce = (ConstructionElement) it.next();
      		if (ce.isGeoElement()) {
	      		GeoElement geoOrig = (GeoElement) ce;  		
	      		GeoElement geoCopy = macroCons.lookupLabel(geoOrig.label);   
	      		if (geoCopy != null) {
		  			try {	    				
		  				geoCopy.set(geoOrig);	  				
		  				geoCopy.update();      	      			 
		  			} catch (Exception e) {
		  				System.err.println("AlgoLocus: error in resetMacroConstruction(): " + e.getMessage());
		  			}
	      		}
      		}
      	}               	      
      }             

    // compute locus line
    final void compute() {
    	if (!P.isDefined() || macroCons == null) {    		
    		locus.setUndefined();
    		return;
    	}
    	
    	// continuous kernel?
    	boolean continuous = kernel.isContinuous();
    	macroKernel.setContinuous(continuous);    
    	    	    	
    	// set all elements in the macro construction
    	// to the current values in the main construction
    	int max_runs = GeoLocus.MAX_PATH_RUNS;
    	if (continuous) {
    		resetMacroConstruction();   
    	} else {
    		Pcopy.set(P);   
    		max_runs = 1; // we only go through the path once for non-continous constructions
    	}
    	// update all algorithms of the macro construction	        
      	macroCons.updateConstruction(); 

    	//  init path mover to the current position of Pcopy
    	pathMover.init(Pcopy); 
    	Pcopy.updateCascade();   	 
    	
//    	System.out.println("*** compute ***");
//    	System.out.println("init P: " + Pcopy);
//    	System.out.println("init Q: " + Qcopy); 
    		
    	// remember the start positions of Pcopy and Qcopy
    	PstartPos.set(Pcopy);
    	QstartPos.set(Qcopy);
    	
    	if (!Qcopy.isDefined()) {  
    		// try to find a position of P where Q is defined
    		boolean foundDefined = false;
    		while (pathMover.hasNext()) {
    			pathMover.getNext(Pcopy);
    			Pcopy.updateCascade();    			
    			if (Qcopy.isDefined()) {
    				foundDefined = true;
    				break;
    			}
    		}
    		
    		if (foundDefined) {
    			pathMover.init(Pcopy);
    			PstartPos.set(Pcopy);
    	    	QstartPos.set(Qcopy);
    		} else {
    			locus.setUndefined();
        		return;
    		}
    	}
    	
    	//  init locus with this first point    	
    	locus.setDefined(true);
    	locus.clearPoints();    
    	pointCount = 0;
   		insertPoint(Qcopy.inhomX, Qcopy.inhomY, false);   		
    	
    	// move Pcopy along the path
    	// do this until Qcopy comes back to its start position
    	// this may require several runs of Pcopy along the whole path
    	int runs = 1;   
    	int MAX_LOOPS = 2*PathMover.MAX_POINTS;
    	boolean maxTimeExceeded = false;
    	
    	// thread to perform update of P
    	/*
    	updater = new Updater();
    	updater.setGeoElement(Pcopy);
    	*/
    	
    	do {    		    		
    		boolean finishedRun = false;
    		int whileLoops = 0;
    		
        //	System.out.println("RUN " + runs);
    		
	        while ( !finishedRun && 
	        		 pointCount <= PathMover.MAX_POINTS && 
	        		 whileLoops <= MAX_LOOPS) 
	        {		    
	        		        	
	        	whileLoops++;	        	
	        	boolean lineTo = pathMover.getNext(Pcopy);
	        	
	     //   	System.out.println("   while " + whileLoops + ", Pcopy: " + Pcopy);
	        	
	        	
	        	// TODO: check
	        	//safeUpdateCascade();
	        	
	        	// measure time needed for update of construction
	        	long startTime = System.currentTimeMillis();	        
	       		Pcopy.updateCascade();
	       		long updateTime = System.currentTimeMillis() - startTime;	   
	       			       	
	       		      	        
	       	 // PRINT MACRO CONSTRUCTION STATE   
//	          	Iterator it = macroCons.getGeoElementsIterator();
//	          	System.out.println("*** locus macro construction state ***");
//	          	while (it.hasNext()) {	          	
//	    	      		System.out.println(it.next());			    	      		
//	          	}
	        
	          	
//	        	GeoSegment a = (GeoSegment) macroKernel.lookupLabel("a");
//	        	System.out.println("a: from " + a.getStartPoint() + "(" + a.getStartPoint().getConstruction() + ") to "
//	        			+ a.getEndPoint() + "(" + a.getEndPoint().getConstruction() + ")");
	       
	       		
	        	// TODO: check
	       		// if it takes too much time to calculate a single step, we stop
	       		if (updateTime > MAX_TIME_FOR_ONE_STEP) {
	       			System.err.println("AlgoLocus: max time exceeded " + updateTime);	       			
	       			maxTimeExceeded = true;	 
	       			return;
	       		}
	
	       		// add position of Qcopy to locus line
	       		if (Qcopy.isDefined()) {
	       			if (lineTo) { // no parameter jump	   
	       				boolean stepChanged = false;
	       		
	       				while (Qcopy.isDefined() && !distanceOK(Qcopy)) {	
	       	//	        	System.out.println("        lineto while ");
	       								
	       					//go back and try smaller step	       					
	       					if (!pathMover.smallerStep()) break;
	       					stepChanged = true;	       					
	       					pathMover.stepBack(); 
	       					pathMover.getNext(Pcopy);
	       					
	       				// TODO: check
	       					// safeUpdateCascade();
	       					// measure time needed for update of construction
	       		        	long st = System.currentTimeMillis();	        
	       		       		Pcopy.updateCascade();
	       		       		long ut = System.currentTimeMillis() - st;	
	       					
	       					if (ut > MAX_TIME_FOR_ONE_STEP) {
	       		       			System.err.println("AlgoLocus: max time exceeded  " + updateTime);	       		       				       		       			 
	       		       			maxTimeExceeded = true;
	       		       			return;
	       		       		}	       			
	       					//if (!lineTo) break;	       		
	       				}	       					       					       			
	       				
	       				if (Qcopy.isDefined()) {
	       					insertPoint(Qcopy.inhomX, Qcopy.inhomY, 
	       							distanceSmall(Qcopy));	       				
	       				}
	       				
	       				// if we didn't decrease the step width 
	       				// increase it
	       				if (!stepChanged) 
	       					pathMover.biggerStep();	       										
	       			} 
	       			else { // we had a parameter jump
	       				insertPoint(Qcopy.inhomX, Qcopy.inhomY, false);	       				
	       			}
	       		} 
	       		else {
		       		// TODO: add undefined case	       		
	       		}	       		
	       			       		       		
	       		if (!pathMover.hasNext()) {	       			
	       			// we got back to the start position:
	       			// let's make sure to slow down here!	       				       
	       			if (distanceSmall(Qcopy) || !pathMover.smallerStep()) {
	       				// we finished if no smaller step is possible
	       				finishedRun = true;	       			       				
	       			}
	       		}
	        }		        
	        
	        // calculating the steps took too long, so we stopped somewhere
	        // change orientation of pathMove to get other side of start position too
	        if (maxTimeExceeded) {
	        	return;	        
	        } 
	        else {	        
		        // make sure that Pcopy is back at startPos now
		        // look at Qcopy at startPos	    	         
				Pcopy.set(PstartPos);
				Pcopy.updateCascade();	               	    	       		       	   		   		       	       							
				insertPoint(Qcopy.inhomX, Qcopy.inhomY, distanceQuiteSmall(Qcopy));							
		    				
//	    		 System.out.println("run: " + runs);
//	    		 System.out.println("pointCount: " + pointCount);
//		       	 System.out.println("  startPos: " + QstartPos); 
//		       	 System.out.println("  Qcopy: " + Qcopy);
	    		 	        
	    		// we are finished with all runs
	    		// if we got back to the start position of Qcopy
	    		// AND if the direction of moving along the path
	    		// is positive like in the beginning
		        if (pathMover.hasPositiveOrientation() && 
		        	QstartPos.equals(Qcopy))
		        	break;
	        }
	        
	        pathMover.resetStartParameter();	       
	        runs++;	        	        	        
        } while (runs < max_runs);
            	
		//System.out.println("points in list: " + locus.getPointLength() +  ", runs: " + (runs-1));
    }
    
  
            
    private void insertPoint(double x, double y, boolean lineTo) {
    	pointCount++;
    		
    	// TODO: remove
    	//System.out.println("insertPoint: " + x + ", " + y + ", lineto: " + lineTo);
    	
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
    	if (lastFarAway && isFarAway(Q.inhomX, Q.inhomY))
			// if last point Q' was far away and Q is far away
    		// let's check whether the line Q'Q intersects the
    		// near to screen rectangle    	        		    	
    		return !nearToScreenRect.intersects(lastX, lastY, Q.inhomX, Q.inhomY);
		else
			return distanceSmall(Q);            
    }    
    
    private boolean distanceSmall(GeoPoint Q) {
    	boolean distSmall = Math.abs(Q.inhomX - lastX) < maxXdist &&
							Math.abs(Q.inhomY - lastY) < maxYdist;    	
    	return distSmall;
    }
    
    private boolean distanceQuiteSmall(GeoPoint Q) {
    	return Math.abs(Q.inhomX - lastX) < maxXdist * 3 &&
					Math.abs(Q.inhomY - lastY) < maxYdist * 3;
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
    	
    	// TODO: remove
    	//System.out.println("maxXdist: " + maxXdist);
    	//System.out.println("maxYdist: " + maxYdist);
    	
    	// we take a bit more than the screen
    	// itself so that we don't loose locus
    	// lines too often 
    	// that leave and reenter the screen
    	    	
    	farXmin = xmin - widthRW / 4;
    	farXmax = xmax + widthRW / 4;
    	farYmin = ymin - heightRW / 4;
    	farYmax = ymax + heightRW / 4;
    	
    	// TODO: remove
//    	System.out.println("farXmin: " + farXmin);
//    	System.out.println("farXmax: " + farXmax);
//    	System.out.println("farYmin: " + farYmin);
//    	System.out.println("farYmax: " + farYmax);
    	    			
    	// near to screen rectangle
    	nearToScreenRect.setFrame(farXmin, farYmin, 
    			  farXmax - farXmin, 
				  farYmax - farYmin);    	    	
    }
    
    public void euclidianViewUpdate() {
      	updateScreenBorders();
  		update();		
  	}
    
    // TODO: check updater, what does suspend(), resume(), interrupt() do exactly?
    /*
    private class Updater extends Thread {
    	GeoElement geo;
    	Thread sleeper;
    	
    	public Updater() {
    		sleeper = new Thread() {
        		public void run() {
        			// TODO: remove
            		System.out.println("sleeper: start");
        			try {
        				sleep(MAX_TIME_FOR_ONE_STEP);
        			} catch (Exception e) {    				
        			}
        			
        			if (Updater.this.isAlive()) {
        				// TODO: remove
        				System.out.println("updater: suspend (TOOK TOO LONG)");
        				Updater.this.interrupt();
        			}
        		}
        	};       
    	}
    	
    	public void setGeoElement(GeoElement geo) {
    		this.geo = geo;
    	}
    	
    	public void run() {    		
    		sleeper.start();
    		
    		// TODO: remove
    		System.out.println("updater: start");
    		geo.updateCascade();    		
    		
    		System.out.println("updater: finished");
    		System.out.println("sleeper: suspend");
    		sleeper.suspend();    		
    	}    
    	    	
    }
*/
    
    
    /*
     * Calls geo.updateCascade(). This method will terminate after
     * MAX_MILLIS.
     * @return
     *
    private void safeUpdateCascade() {
    	// measure time needed for update of construction
    
    	updater.start();    	
    }
    */ 
	
}
