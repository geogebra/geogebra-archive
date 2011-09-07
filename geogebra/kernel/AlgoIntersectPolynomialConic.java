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

import geogebra.euclidian.EuclidianConstants;
import geogebra.kernel.arithmetic.PolyFunction;
import geogebra.kernel.implicit.GeoImplicitPoly;
import geogebra.kernel.kernelND.GeoConicND;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.analysis.polynomials.PolynomialFunction;

public class AlgoIntersectPolynomialConic extends AlgoSimpleRootsPolynomial {

	private static final long serialVersionUID = 1L;
	private GeoFunction h;  // input
    private GeoConic c;

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
		} else {
			Kernel ker = cons.getKernel();
			GeoFunction substituteFunctionX = null;
			
			ker.setSilentMode(true);
			/*try {
				substituteFunctionX = (GeoFunction) ker.getAlgebraProcessor().processValidExpression(
					ker.getParser().parseGeoGebraExpression("x"))[0];
			} catch (MyError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			GeoImplicitPoly iPoly = new GeoImplicitPoly(cons);
			c.toGeoImplicitPoly(iPoly);
			GeoFunction paramEquation = new GeoFunction(cons, iPoly, null, h);
			
			double nroots = 0;
			double res[] = new double[2];
			if (c.getType()==GeoConicND.CONIC_CIRCLE || c.getType()==GeoConicND.CONIC_ELLIPSE) {
				nroots = kernel.getEquationSolver().solveQuadratic(new double[]
				          {- A[5] * A[5] + A[1] * A[2],
						2 * (A[1] * A[4] - A[3] * A[5]),
						 A[0] * A[1] - A[3] * A[3]}
					,res);
			}
			
			AlgoRoots algo = null;
			if (nroots == 2) {
				//assume res[0]>=res[1]
				if (res[1]>res[0]) {
					double temp = res[0];
					res[0] = res[1];
					res[1] = temp;
				}
				algo = new AlgoRoots(cons, paramEquation, 
						new GeoNumeric(cons, Math.max(res[1]-Kernel.MIN_PRECISION,h.getMinParameter())),
						new GeoNumeric(cons, Math.min(res[0]+Kernel.MIN_PRECISION,h.getMaxParameter())));
			} else { 
				algo = new AlgoRoots(cons, paramEquation, 
						new GeoNumeric(cons, h.getMinParameter()),
						new GeoNumeric(cons, h.getMaxParameter()));
			}
			GeoPoint[] points = algo.getRootPoints();
			List<double[]> valPairs=new ArrayList<double[]>();
			for (int i=0;i<points.length;i++){
				double t = points[i].getX();
				valPairs.add(new double[]{t,h.evaluate(t)});
			}
			
			ker.setSilentMode(false);
			
			setPoints(valPairs);
			return;

		}

	}

	@Override
	public String getClassName() {
		return "AlgoIntersectPolynomialConic";
	}

	@Override
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECT;
    }
	
	@Override
	protected double getYValue(double x) {
		return h.evaluate(x);
	}
	
}
