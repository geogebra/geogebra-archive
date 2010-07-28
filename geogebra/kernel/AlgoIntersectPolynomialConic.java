/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoIntersectPolynomialConic.java
 *
 * Created on 14.07.2010, 14:28
 */

package geogebra.kernel;

import java.util.Arrays;

import org.apache.commons.math.analysis.polynomials.PolynomialFunction;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.PolyFunction;
import geogebra.main.Application;

public class AlgoIntersectPolynomialConic extends AlgoSimpleRootsPolynomial {

	private static final long serialVersionUID = 1L;
	private GeoFunction h;  // input
    private GeoConic c;
    private GeoPoint []  Q;     // points  
    
    private Function polyFunc;
    
   // private boolean isLimitedPathSituation;

	
	public AlgoIntersectPolynomialConic(Construction cons,GeoFunction h,GeoConic c) {
		super(cons,h,c);
		this.h=h;
		this.c=c;
	//	isLimitedPathSituation = f.isLimitedPath() || c.isLimitedPath(); 
		  
    //    initForNearToRelationship();
        compute();
	}
	
	public AlgoIntersectPolynomialConic(Construction cons,String[] labels,boolean setLabel,GeoFunction h,GeoConic c) {
		super(cons,labels,setLabel,h,c);
		this.h=h;
		this.c=c;
        compute();
	}

	@Override
	protected void compute() {
		
		double[] A=c.matrix;
		if (h.isPolynomialFunction(false)){
			PolyFunction pf=h.getFunction().getNumericPolynomialDerivative(0);
			PolynomialFunction y=new PolynomialFunction(pf.getCoeffs());
			PolynomialFunction r=new PolynomialFunction(new double[]{A[2],2*A[4],A[0]});
			r=r.add(y.multiply(new PolynomialFunction(new double[]{2*A[5],2*A[3]})));
			r=r.add(y.multiply(y.multiply(new PolynomialFunction(new double[]{A[1]}))));
			//Application.debug("r = "+r.toString());
			setRootsPolynomial(r);
		}

	}

	@Override
	public String getClassName() {
		return "AlgoIntersectPolynomialConic";
	}

	@Override
	protected double getYValue(double x) {
		return h.evaluate(x);
	}
	
}
