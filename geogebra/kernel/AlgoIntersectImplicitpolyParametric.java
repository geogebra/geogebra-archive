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
 * Created on 28.07.2010, 13:20
 */
package geogebra.kernel;

import org.apache.commons.math.analysis.polynomials.PolynomialFunction;

/**
 * Algorithm to intersect Implicit polynomials with either lines or polynomials
 */
public class AlgoIntersectImplicitpolyParametric extends
		AlgoSimpleRootsPolynomial {
	
	private PolynomialFunction tx;
	private PolynomialFunction ty;
	private GeoImplicitPoly p;
	private GeoLine l;
	private GeoFunction f;

	public AlgoIntersectImplicitpolyParametric(Construction c,GeoImplicitPoly p,GeoLine l) {
		this(c,null,false,p,l);
	}
	
	public AlgoIntersectImplicitpolyParametric(Construction c,GeoImplicitPoly p,GeoFunction f) {
		this(c,null,false,p,f);
	}

	public AlgoIntersectImplicitpolyParametric(Construction c, String[] labels,
			boolean setLabels, GeoImplicitPoly p, GeoLine l) {
		super(c,labels,setLabels,p,l);
		this.p=p;
		this.l=l;
	}

	public AlgoIntersectImplicitpolyParametric(Construction c, String[] labels,
			boolean setLabels, GeoImplicitPoly p, GeoFunction f) {
		super(c,labels,setLabels,p,f);
		this.p=p;
		this.f=f;
		
	}

	@Override
	protected double getYValue(double t) {
		return ty.value(t);
	}
	

	@Override
	protected double getXValue(double t) {
		return tx.value(t);
	}

	@Override
	protected void compute() {
		if (p==null||!p.isDefined()){
			return;
		}
		if (f!=null){
			if (!f.isPolynomialFunction(false)||!f.isDefined){
				return;
			}
			tx=new PolynomialFunction(new double[]{0,1}); //x=t
			ty=new PolynomialFunction(f.fun.getNumericPolynomialDerivative(0).getCoeffs()); //y=f(t)
		}else if (l!=null){
			if (!l.isDefined()){
				return;
			}
			//get parametrisation of line
			double startP[]=new double[2];
			l.getInhomPointOnLine(startP);
			tx=new PolynomialFunction(new double[]{startP[0],l.getY()}); //x=p1+t*r1
			ty=new PolynomialFunction(new double[]{startP[1],-l.getX()}); //x=p1+t*r1
		}else{
			return;
		}
		PolynomialFunction sum=null;
		PolynomialFunction zs=null;
		//Insert x and y (univariat)polynomials via the Horner-scheme
		double[][] coeff=p.getCoeff();
		if (coeff!=null)
			for (int i=coeff.length-1;i>=0;i--){
				zs=new PolynomialFunction(new double[]{coeff[i][coeff[i].length-1]});
				for (int j=coeff[i].length-2;j>=0;j--){
					zs=zs.multiply(ty).add(new PolynomialFunction(new double[]{coeff[i][j]}));//y*zs+coeff[i][j];
				}
				if (sum==null)
					sum=zs;
				else
					sum=sum.multiply(tx).add(zs);//sum*x+zs;
			}
		setRootsPolynomial(sum);
	}
	
	public String getClassName() {
        return "AlgoIntersectImplicitpolyParametric";
    }

}
