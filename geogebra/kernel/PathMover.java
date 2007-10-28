/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

public interface PathMover {
	
	public static final int MIN_STEPS = 32;
	public static final int DEFAULT_STEPS = 128;
	public static final double STEP_DECREASE_FACTOR = 0.5;
	public static final double STEP_INCREASE_FACTOR = 4;
	
	public static final double INIT_STEP_WIDTH = 1E-2;
	public static final double MIN_STEP_WIDTH = 1E-8;
	
	public static final double OPEN_BORDER_OFFSET = 1E-5;	
	public static final int MAX_POINTS = 5000;
	
	/**
	 * Inits the path mover using a point p on the path
	 * and sets the orientation to positive.
	 * Note: the path parameter of p may be changed here!
	 */
	public void init(GeoPoint p);	
	
	/**
	 * Sets point p to the next position on the path
	 * @param result
	 * @return true: draw line to point p; false: move to point p
	 */
	public boolean getNext(GeoPoint p);
	
	/**
	 * Returns false whenever the next call of getNext() 
     * would lead to passing the init path parameter
     * (note: there are two orientations) 
	 * @param result
	 */
	public boolean hasNext();	
	
	/**
	 * Resets this path mover to the inital start parameter.	 	 
	 */
	public void resetStartParameter();
	

	/**
	 * Changes the orientation of moving along the
	 * path.
	 */
	public void changeOrientation();	
	
	/**
	 * Returns whether the orientation of moving along the
	 * path is positive.
	 */
	public boolean hasPositiveOrientation();	
	
	/**
	 * Decreases the step width. Returns wheter this was possible. 
	 */
	public boolean smallerStep();
	
	/**
	 * Increases the step width. Returns whether this was possible.	 
	 */
	public boolean biggerStep();
	
	/**
	 * Goes back one step.
	 */
	public void stepBack();
}
