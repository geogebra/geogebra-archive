/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoIntersectImplictpolys.java
 *
 * Created on 04.08.2010, 23:12
 */

package geogebra.kernel;

import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.BigRational;
import edu.jas.gb.GBFactory;
import edu.jas.gb.GroebnerBase;
import edu.jas.poly.GenPolynomial;
import geogebra.main.Application;

/**
 *	Algorithm to intersect two implicit polynomial equations<br />
 *	output: GeoPoints if finitely many intersection points.
 */
public class AlgoIntersectImplicitpolys extends AlgoSimpleRootsPolynomial {
	
	private GeoImplicitPoly p1;
	private GeoImplicitPoly p2;
	
	private GeoConic c1;
	
	private static final int PolyX=0;
	private static final int PolyY=1;
	
	private int univarType;

	public AlgoIntersectImplicitpolys(Construction c) {
		super(c);
	}
	
	public AlgoIntersectImplicitpolys(Construction c, GeoImplicitPoly p1,GeoConic c1) {
		this(c,null,false,p1,c1);
	}
	public AlgoIntersectImplicitpolys(Construction c, String[] labels, boolean setLabels,
			GeoImplicitPoly p1, GeoConic c1) {
		super(c,labels,setLabels,p1,c1);
		this.p1=p1;
		this.c1=c1;
	}

	public AlgoIntersectImplicitpolys(Construction c, GeoImplicitPoly p1,GeoImplicitPoly p2) {
		this(c,null,false,p1,p2);
	}


	public AlgoIntersectImplicitpolys(Construction c, String[] labels,
			boolean setLabels,GeoImplicitPoly p1,GeoImplicitPoly p2) {
		super(c, labels, setLabels, p1,p2);
		this.p1=p1;
		this.p2=p2;
	}


	private List<Double> cRoots;
	
	@Override
	protected int getNrPoints(double t) {
		double[] newCoeff;
		GeoImplicitPoly tester;
		if (univarType==PolyX){
			double[][] coeff;
			if (p1.getDegY()<p2.getDegY()){
				coeff=p1.getCoeff();
				newCoeff=new double[p1.getDegY()+1];
				tester=p2;
			}else{
				coeff=p2.getCoeff();
				newCoeff=new double[p2.getDegY()+1];
				tester=p1;
			}
			for (int j=0;j<newCoeff.length;j++){
				newCoeff[j]=0;
			}
			for (int i=coeff.length-1;i>=0;i--){
				for (int j=0;j<coeff[i].length;j++){
					newCoeff[j]=newCoeff[j]*t+coeff[i][j];
				}
				for (int j=coeff[i].length;j<newCoeff.length;j++){
					newCoeff[j]=newCoeff[j]*t;
				}
			}
		}else{
			double[][] coeff;
			if (p1.getDegX()<p2.getDegX()){
				coeff=p1.getCoeff();
				newCoeff=new double[p1.getDegX()+1];
				tester=p2;
			}else{
				coeff=p2.getCoeff();
				newCoeff=new double[p2.getDegX()+1];
				tester=p1;
			}
			for (int i=0;i<coeff.length;i++){
				newCoeff[i]=0;
				for (int j=coeff[i].length-1;j>=0;j--){
					newCoeff[i]=newCoeff[i]*t+coeff[i][j];
				}
			}
		}
		int nr=getRoots(newCoeff,eqnSolver);
		cRoots=new ArrayList<Double>();
		for (int i=0;i<nr;i++){
			if (univarType==PolyX){
				if (Math.abs(tester.evalPolyAt(t, newCoeff[i]))<1E-3)
					cRoots.add(newCoeff[i]);
				else
					Application.debug("no root: "+tester.evalPolyAt(t, newCoeff[i]));
			}else{
				if (Math.abs(tester.evalPolyAt(newCoeff[i],t))<1E-3)
					cRoots.add(newCoeff[i]);
				else
					Application.debug("no root: "+tester.evalPolyAt(newCoeff[i],t));
			}
		}
		return cRoots.size();
	}

	@Override
	protected double getYValue(double t, int idx) {
		if (univarType==PolyX)
			return cRoots.get(idx);
		else
			return t;
	}

	@Override
	protected double getXValue(double t, int idx) {
		if (univarType==PolyY)
			return cRoots.get(idx);
		else
			return t;
	}

	@Override
	protected double getYValue(double t) {
		//will not be used
		return 0;
	}
	

	@Override
	protected void compute() {
		
		if (c1!=null){
			p2=new GeoImplicitPoly(c1);
		}
		
    	List<GenPolynomial<BigRational>> polynomials = new ArrayList<GenPolynomial<BigRational>>();
    	polynomials.add(p1.toGenPolynomial());
    	polynomials.add(p2.toGenPolynomial());
    	
//    	Application.debug("dp1: {"+p1.getDegX()+","+p1.getDegY()+"} dp2: {"+p2.getDegX()+","+p2.getDegY()+"}");
//    	Application.debug("size: "+polynomials.size());
//    	Application.debug("p: "+polynomials);
    	
    	
    	GroebnerBase<BigRational> gb = GBFactory.getImplementation(BigRational.ONE);
    	List<GenPolynomial<BigRational>> G=gb.GB(polynomials);
    	//G=gb.minimalGB(G);
    	Application.debug("Gröbner Basis: "+G);
    	boolean[] var=new boolean[2];
    	var[0]=var[1]=true;
    	setRootsPolynomial(GeoImplicitPoly.getUnivariatPoly(G,var));
    	if (var[0])
    		univarType=0;
    	else
    		univarType=1;
	}

	@Override
	public String getClassName() {
		return "AlgoIntersectImplicitpolys";
	}
	
	

}
