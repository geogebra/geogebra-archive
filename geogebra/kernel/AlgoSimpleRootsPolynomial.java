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

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import geogebra.main.Application;

import org.apache.commons.math.analysis.polynomials.PolynomialFunction;

public abstract class AlgoSimpleRootsPolynomial extends AlgoIntersect {

//	private String[] labels;
//    private boolean initLabels=true;
	protected boolean setLabels;
    protected EquationSolver eqnSolver;
    protected GeoElement[] geos;
   // protected GeoPoint[] points;
    private PolynomialFunction rootsPoly;
    private OutputHandler<GeoPoint> points;
	
	public AlgoSimpleRootsPolynomial(Construction c) {
		super(c);
		//points=new GeoPoint[0];
		eqnSolver=cons.getEquationSolver();
		points=new OutputHandler<GeoPoint>(new elementFactory<GeoPoint>() {
					public GeoPoint newElement() {
						GeoPoint p=new GeoPoint(cons);
						p.setCoords(0, 0, 1);
						p.setParentAlgorithm(AlgoSimpleRootsPolynomial.this);
						return p;
					}
		});
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
	
	/**
	 * @param pf assigns a PolynomialFunction to this Algorithm which roots lead to one or more output Points
	 */
	public void setRootsPolynomial(PolynomialFunction pf){
		this.rootsPoly=pf;
		this.doCalc();
	}

	@Override
	protected
	GeoPoint[] getIntersectionPoints() {
		return points.getOutput(new GeoPoint[0]);
	}

	@Override
	protected
	GeoPoint[] getLastDefinedIntersectionPoints() {
		return null;
	}

	@Override
	protected void setInputOutput() {
		input=geos;
		//output=points;
		setDependencies();
	}
	
	/**
	 * @param roots array with the coefficients of the polynomial<br/>
	 * the roots of the polynomial are assigned to the first n elements of <b>roots</b>
	 * @return number of distinct roots
	 */
	public static int getRoots(double[] roots,EquationSolver eqnSolver){
		int nrRealRoots=eqnSolver.polynomialRoots(roots);
//		StringBuilder sb=new StringBuilder();
//		for (int i=0;i<nrRealRoots;i++){
//			if (i>0)
//				sb.append(',');
//			sb.append(roots[i]);
//		}
//		Application.debug("roots->"+sb);
		if (nrRealRoots>1){
			int c=0;
			Arrays.sort(roots,0,nrRealRoots);
			double last=roots[0];
			for (int i=1;i<nrRealRoots;i++){
//				Application.debug("diff = "+(roots[i]-last));
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
		return nrRealRoots;
	}

	protected void doCalc() {
		double roots[]=rootsPoly.getCoefficients();
		int nrRealRoots=getRoots(roots,eqnSolver);
		makePoints(roots,nrRealRoots);
	}
	

	private void makePoints(double[] roots, int nrRealRoots) {
		List<Double[]> valPairs=new ArrayList<Double[]>();
		for (int i=0;i<nrRealRoots;i++){
			for (int j=0;j<getNrPoints(roots[i]);j++)
				valPairs.add(new Double[]{getXValue(roots[i],j),getYValue(roots[i],j)});
			//points[i].setCoords(getXValue(roots[i]), getYValue(roots[i]), 1);
		}
		points.adjustOutputSize(valPairs.size());
		for (int i=0;i<valPairs.size();i++){
			points.getElement(i).setCoords(valPairs.get(i)[0], valPairs.get(i)[1], 1);
		}
		
		 if (setLabels)
	            points.updateLabels();
	}
	
	 public void setLabels(String[] labels){
		 points.setLabels(labels);
		 update();
	 }
	 
	/**
	 * @param t root of PolynomialFunction
	 * @return number of corresponding outputPoints 
	 */
	protected int getNrPoints(double t){
		return 1;
	}
	
	/**
	 * @param t root of PolynomialFunction
	 * @param idx
	 * @return Y-value corresponding to t and idx.
	 */
	protected double getYValue(double t,int idx){
		return getYValue(t);
	}
	
	/**
	 * @param t root of PolynomialFunction
	 * @return the corresponding Y-value
	 */
	protected abstract double getYValue(double t);
	
	/**
	 * @param t root of PolynomialFunction
	 * @return the corresponding X-value
	 */
	protected double getXValue(double t){
		return t;
	}
	
	/**
	 * @param t root of PolynomialFunction
	 * @param idx
	 * @return X-value corresponding to t and idx.
	 */
	protected double getXValue(double t,int idx){
		return getXValue(t);
	}

	@Override
	public String getClassName() {
		return "AlgoSimpleRootsPoly";
	}
	
}
