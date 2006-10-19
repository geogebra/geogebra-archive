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


public class PathMoverGeneric implements PathMover {		
	
	private static final int BOUNDS_FIXED = 1;
	private static final int BOUNDS_INFINITE = 2;
	private static final int BOUNDS_FIXED_INFINITE = 3;
	private static final int BOUNDS_INFINITE_FIXED = 4;
	
	private Path path;
	protected double start_param, start_paramUP;
	protected double curr_param, last_param, param_extent, min_param, max_param,
					max_step_width, init_step_width, step_width, offset;
	protected int  mode;	
	protected boolean posOrientation;
	private boolean maxBorderSet, minBorderSet;
	
	public PathMoverGeneric(Path path) {		
		this.path = path;
	}
	
	public void init(GeoPoint p) {
		start_param = p.pathParameter.t; 							
				
		min_param = path.getMinParameter();
		max_param = path.getMaxParameter();		
		
		// make sure start_param is between min and max
		if (start_param < min_param || start_param > max_param) {
			start_param = (start_param - min_param) % (max_param - min_param);
			if (start_param < min_param)
				start_param += (max_param - min_param);		
		}
		
		/*
		System.out.println("min_param : " + min_param);
		System.out.println("max_param : " + max_param);
		System.out.println("start_param : " + start_param);
		*/
		
		if (min_param == Double.NEGATIVE_INFINITY) { 
			if (max_param == Double.POSITIVE_INFINITY){
				// (-infinite, +infinite)
				mode = BOUNDS_INFINITE;		
				
				// infFunction(-1, 1)
				min_param  = -1 + OPEN_BORDER_OFFSET;
				max_param  =  1 - OPEN_BORDER_OFFSET;								
			}
			else { 
				// (-infinite, max_param]
				mode = BOUNDS_INFINITE_FIXED;				
				start_param = 0;
			
				// max_param +  infFunction(-1 ... 0)				
				offset = max_param;
				min_param = -1 + OPEN_BORDER_OFFSET;
				max_param = 0;				
			}
		} 
		else {
			if (max_param == Double.POSITIVE_INFINITY){
				// [min_param, +infinite)
				mode = BOUNDS_FIXED_INFINITE;		
				start_param = 0;
				
				// min_param + infFunction(0 ... 1)	
				offset = min_param;
				min_param = 0;
				max_param = 1 - OPEN_BORDER_OFFSET;				
			}
			else { 
				// [min_param, max_param]
				mode = BOUNDS_FIXED;											
			}
		}
				
		param_extent = max_param - min_param;
		start_paramUP = start_param + param_extent;
		init_step_width = INIT_STEP_WIDTH;
		max_step_width = param_extent / MIN_STEPS;		
		posOrientation = true; 						
		resetStartParameter();
	}
	
	public void resetStartParameter() {		
		curr_param = start_param;	
		last_param = start_param;
		step_width = init_step_width;		
	}
	
	
	public boolean getNext(GeoPoint p) {													
		//  check if we are in our interval
		boolean lineTo = true;				
		last_param = curr_param;
		
		// in the last step we got outside a border and stopped there
		// now continue at the other border
		if (maxBorderSet) {			
			curr_param = min_param;	
			lineTo = path.isClosedPath();
			maxBorderSet = false;
		//	step_width = init_step_width;
			
		} 
		else if (minBorderSet) {
			curr_param = max_param;	
			lineTo = path.isClosedPath();
			minBorderSet = false;		
		//	step_width = init_step_width;
			
		} 
		// STANDARD CASE
		else {
			curr_param += step_width;
			
			if (curr_param >= max_param) {
				curr_param = max_param;	
				maxBorderSet = true;				
				//System.out.println("set MAX Border: " + curr_param);
			} 	
			else if (curr_param <= min_param) {
				curr_param = min_param;	
				minBorderSet = true;				
				//System.out.println("set MIN Border: " + curr_param);
			}
		}					
			
		//System.out.println("curr_param: " + curr_param);
		calcPoint(p);
					
		return lineTo;
	}
	
	protected void calcPoint(GeoPoint p) {
		double param;
		switch (mode) {
			case BOUNDS_FIXED:
				param = curr_param;
				break;
			
			case BOUNDS_INFINITE:
				param = infFunction(curr_param);
				break;
			
			case BOUNDS_FIXED_INFINITE:
			case BOUNDS_INFINITE_FIXED:
				param = offset + infFunction(curr_param);				
				break;
			
			default:
				param = Double.NaN;
		}
		
		p.pathParameter.t = param;
		path.pathChanged(p);
		p.updateCoords();
	}
	
	public boolean hasNext() {		
		// check if we pass the start parameter
		// from last_param to the next parameter curr_param							
		double next_param = curr_param + step_width;
		if (posOrientation)
			return !(curr_param < start_param && next_param >= start_param
				|| curr_param < start_paramUP && next_param >= start_paramUP);
		else
			return !(curr_param > start_param && next_param <= start_param
				|| curr_param > start_paramUP && next_param <= start_paramUP);								
	}
	
	public void changeOrientation() {
		posOrientation = !posOrientation;
		step_width = -step_width;
	}


	public boolean hasPositiveOrientation() {		
		return posOrientation;
	}

	final public boolean smallerStep() {		
		return changeStep(step_width * STEP_DECREASE_FACTOR);		
	}

	final public boolean biggerStep() {
		return changeStep(step_width * STEP_INCREASE_FACTOR);
	}
	
	private boolean changeStep(double new_step) {
		double abs_new_step = Math.abs(new_step);		
		
		if (abs_new_step > max_step_width) {
			step_width = new_step >= 0 ? max_step_width : -max_step_width;
			return false;
		} 
		else if (abs_new_step < MIN_STEP_WIDTH) {
			step_width = new_step >= 0 ? MIN_STEP_WIDTH : -MIN_STEP_WIDTH;
			return false;
		} 
		else {
			step_width = new_step;
			return true;
		}	
	}

	final public void stepBack() {
		curr_param = last_param;			
	}
	
	/**
	 * Function t: [-1, 1] -> (-inf, +inf)
	 * @param t
	 * @return
	 */
	public static double infFunction(double t) {		
		return  t /  (1 - Math.abs(t));
	}



}
