/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoSimpleRootsPolynomial.java
 *
 * Created on 27.07.2010, 17:41
 */

package geogebra.kernel;

import java.util.Arrays;

import geogebra.main.Application;

import org.apache.commons.math.analysis.polynomials.PolynomialFunction;

public abstract class AlgoSimpleRootsPolynomial extends AlgoIntersect {

	private String[] labels;
    private boolean initLabels=true;
	protected boolean setLabels;
    protected EquationSolver eqnSolver;
    protected GeoElement[] geos;
    protected GeoPoint[] points;
    private PolynomialFunction rootsPoly;
	
	public AlgoSimpleRootsPolynomial(Construction c) {
		super(c);
		points=new GeoPoint[0];
		eqnSolver=cons.getEquationSolver();
	}
	
	public AlgoSimpleRootsPolynomial(Construction c,String[] labels,boolean setLabels,GeoElement... geos) {
		this(c);
		this.geos=new GeoElement[geos.length];
		for (int i=0;i<geos.length;i++){
			this.geos[i]=geos[i];
		}
		setInputOutput();
	}
	
	public AlgoSimpleRootsPolynomial(Construction c,GeoElement... geos) {
		this(c,null,false,geos);
	}
	
	public void setRootsPolynomial(PolynomialFunction pf){
		this.rootsPoly=pf;
		this.doCalc();
	}

	@Override
	GeoPoint[] getIntersectionPoints() {
		return points;
	}

	@Override
	GeoPoint[] getLastDefinedIntersectionPoints() {
		return null;
	}

	@Override
	protected void setInputOutput() {
		input=geos;
		output=points;
		setDependencies();
	}

	protected void doCalc() {
		double roots[]=rootsPoly.getCoefficients();
		int nrRealRoots=eqnSolver.polynomialRoots(roots);
		StringBuilder sb=new StringBuilder();
		for (int i=0;i<nrRealRoots;i++){
			if (i>0)
				sb.append(',');
			sb.append(roots[i]);
		}
		Application.debug("roots->"+sb);
		if (nrRealRoots>1){
			int c=0;
			Arrays.sort(roots,0,nrRealRoots);
			double last=roots[0];
			for (int i=1;i<nrRealRoots;i++){
				Application.debug("diff = "+(roots[i]-last));
				if (roots[i]-last<=Kernel.MIN_PRECISION){
					c++;
				}else{
					last=roots[i];
					if (c>0)
						roots[i-c]=roots[i];
				}
			}
			nrRealRoots-=c;
		}
		makePoints(roots,nrRealRoots);
	}

	private void makePoints(double[] roots, int nrRealRoots) {
		adjustOutputSize(nrRealRoots);
		for (int i=0;i<nrRealRoots;i++){
			points[i].setCoords(getXValue(roots[i]), getYValue(roots[i]), 1);
		}
		
		 if (setLabels)
	            updateLabels(nrRealRoots);
	}
	
	 protected void updateLabels(int number) {  
	    	if (initLabels) {
	    		GeoElement.setLabels(labels, points);
	    		initLabels = false;
	    	} else {	    
		        for (int i = 0; i < number; i++) {
		            //  check labeling      
		            if (!points[i].isLabelSet()) {
		            	// use user specified label if we have one
		            	String newLabel = (labels != null && i < labels.length) ? labels[i] : null;	            	
		                points[i].setLabel(newLabel);	                
		            }
		        }
	    	}
	        
	    }
	 
	 public void setLabels(String[] labels) {
	        this.labels = labels;
	        setLabels = true;

	        // make sure that there are at least as many
	        // points as labels
	        if (labels != null)
	            adjustOutputSize(labels.length);

	        update();
	    }

	protected void adjustOutputSize(int size){
		Application.debug("size: "+size);
		if (points.length<size){
			GeoPoint[] temp=new GeoPoint[size];
			for (int i=0;i<points.length;i++)
				temp[i]=points[i];
			for (int i=points.length;i<size;i++){
				temp[i]=new GeoPoint(cons);
				temp[i].setCoords(0, 0, 1);
				temp[i].setParentAlgorithm(this);
			}
			points=temp;
			output=points;
		}else{
			for (int i=size;i<points.length;i++){
				points[i].setUndefined();
			}
		}
	}
	
	protected abstract double getYValue(double t);
	
	protected double getXValue(double t){
		return t;
	}

	@Override
	public String getClassName() {
		return "AlgoSimpleRootsPoly";
	}
	
}
