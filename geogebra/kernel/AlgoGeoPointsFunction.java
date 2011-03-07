/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.PolyFunction;
import geogebra.kernel.roots.RealRootFunction;
import geogebra.main.Application;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Abstract class with all the label methods needed to update
 * labels of commands on functions, where the command returns
 * a varying number of GeoPoints.
 * This is to avoid a lot of duplicated label-updating code.
 * Most of the code is copied from AlgoRootsPolynomial.
 * (Where it might be eliminated later...)
 * 
 * @author Hans-Petter Ulven
 * @version 06.03.11
 * 
 */
public abstract class AlgoGeoPointsFunction extends AlgoElement{

	private static final long serialVersionUID = 1L;
	
    protected GeoFunction f;			 // For calculation of y-values 
    
    protected GeoPoint[] Points;		// output in subcclass  

    private String[] labels;
    private boolean initLabels;
	protected boolean setLabels;
	
    double[] 		curXValues = new double[30]; // current x-values
    int 			numberOfXValues;

    //Function yValFunction;

    private GeoPoint tempPoint;
    

    /**
     * Computes all roots of f
     */
    public AlgoGeoPointsFunction(
        Construction cons,
        String[] labels,
        boolean setLabels,
        GeoFunction f) {
    	super(cons);
    	this.labels=labels;
    	this.setLabels=setLabels;			//In subclass: !cons.isSuppressLabelsActive();
    	this.f=f;
    	
    	tempPoint = new GeoPoint(cons);
        
        //  make sure root points is not null
        int number = labels == null ? 1 : Math.max(1, labels.length);
        Points = new GeoPoint[0];
        initPoints(number);
        initLabels = true;  
        // setInputOutput, compute(), show at least one point: must be done in subclass.
    }//Constructor


    /**
     * The given labels will be used for the resulting points.   
     */
    public void setLabels(String[] labels) {
        this.labels = labels;
        setLabels = true;

        // make sure that there are at least as many
        // points as labels
        if (labels != null)
            initPoints(labels.length);

        update();
    }//setLabels(String[])

    public String getClassName() {
        return "AlgoGeoPointsFunction";
    }//getClassName()



    public GeoPoint[] getPoints() {
        return Points;
    }//getPoints()



    final private void removeX(int pos) {    
    	for (int i = pos+1; i < numberOfXValues; i++) {
    		curXValues[i-1] = curXValues[i];
    	}    
        numberOfXValues--;
    }//removeX(pos)

    // roots array and number of roots
    protected final void setPoints(double[] curXValues, int number) {
        initPoints(number);

        // now set the new values of the roots
        for (int i = 0; i < number; i++) {

                Points[i].setCoords(
                    curXValues[i],
                    f.evaluate(curXValues[i]),							//yValFunction.evaluate(curXValues[i]),
                    1.0);
                
              //  Application.debug("   " + rootPoints[i]); 
            
        }//for

        // all other roots are undefined
        for (int i = number; i < Points.length; i++) {
            Points[i].setUndefined();
        }//

        if (setLabels)
            updateLabels(number);
    }//setPoints(double[],n)

    // number is the number of current roots
    protected void updateLabels(int number) {  
    	if (initLabels) {
    		GeoElement.setLabels(labels, Points);
    		initLabels = false;
    	} else {	    
	        for (int i = 0; i < number; i++) {
	            //  check labeling      
	            if (!Points[i].isLabelSet()) {
	            	// use user specified label if we have one
	            	String newLabel = (labels != null && i < labels.length) ? labels[i] : null;	            	
	                Points[i].setLabel(newLabel);	                
	            }//if
	        }//for
    	}//if
        
        // all other roots are undefined
        for (int i = number; i < Points.length; i++) {
        	Points[i].setUndefined();						//Points[i].setAlgebraVisible(false);
        }//for
    }//updateLabels(n)
    
    
    protected void noUndefinedPointsInAlgebraView(GeoPoint[] gpts) {
   	 for (int i=1; i < gpts.length; i++) {
   		 gpts[i].showUndefinedInAlgebraView(false);
   	 }//for
    }//noUndefinedPointsInAlgebraView(GeoPoint[])
    
    /**
     * Removes only one single output element if possible. 
     * If this is not possible the whole algorithm is removed.
     */
    void remove(GeoElement output) {
    	// only single undefined points may be removed       
        for (int i = 0; i < Points.length; i++) {
        	if (Points[i] == output && !Points[i].isDefined()) {
        		removePoint(i);      		
        		return;
        	}//if            
        }//for
    	
        // if we get here removing output was not possible
        // so we remove the whole algorithm
        super.remove();
    }//remove(GeoElement)

    protected void initPoints(int number) {
        // make sure that there are enough points   
        if (Points.length < number) {
            GeoPoint[] temp = new GeoPoint[number];
            for (int i = 0; i < Points.length; i++) {
                temp[i] = Points[i];
                temp[i].setCoords(0, 0, 1); // init as defined
            }
            for (int i = Points.length; i < temp.length; i++) {
                temp[i] = new GeoPoint(cons);
                temp[i].setCoords(0, 0, 1); // init as defined
                temp[i].setParentAlgorithm(this);
            }
            Points = temp;
            output = Points;
        }//if
    }//initPoints(n)
    
    private void removePoint(int pos) {
    	Points[pos].doRemove();
    	
    	// build new rootPoints array without the removed point
    	GeoPoint[] temp = new GeoPoint[Points.length - 1];
    	int i;
    	for (i=0; i < pos; i++) 
    		temp[i] = Points[i];        		
    	for (i=pos+1; i < Points.length; i++) 
    		temp[i-1] = Points[i];
    	Points = temp;
    }//removePoint(int pos)

 // * //--- SNIP (after debugging and testing) -------------------------   
    /// --- Test interface --- ///

    // Needed for script testing of children
    public AlgoGeoPointsFunction(Construction cons){
    	super(cons);
    }//Test Constructor

    
// */ //--- SNIP end ---------------------------------------    

}//class AlgoGeoPontsFunction

