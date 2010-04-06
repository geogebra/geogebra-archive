/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * GeoConic.java
 *
 * Created on 10. September 2001, 08:52
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;
import geogebra.util.MyMath;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class GeoCubic extends GeoQuadricND
implements Path, Traceable, LineProperties
{
	
	private static final long serialVersionUID = 1L;
	// avoid very large and small coefficients for numerical stability	
	private static final double MAX_COEFFICIENT_SIZE = 100000;
	private static final double MIN_COEFFICIENT_SIZE = 1;

	private static String[] vars = {
		"x\u00b3 y\u00b3", "x\u00b2 y\u00b3", "x y\u00b2", "y\u00b3",
		"x\u00b3 y\u00b2", "x\u00b2 y\u00b2", "x y\u00b2", "y\u00b2",
		"x\u00b3 y", "x\u00b2 y", "x y", "y",
		"x\u00b3", "x\u00b2", "x", ""
	};
	
	// enable negative sign of first coefficient in implicit equations
	private static boolean KEEP_LEADING_SIGN = false;

	private double coeffs[] = new double[16];

	
	private EquationSolver eqnSolver;
	
	
	public boolean trace;

	
	
	protected GeoCubic(Construction c) {
		super(c,3);
		eqnSolver = c.getEquationSolver();
	}		

	
	/** Creates new GeoConic with Coordinate System for 3D */
	protected GeoCubic(Construction c, String label, double[] coeffs) {

		this(c);
		setCoeffs(coeffs);
		setLabel(label);		
	}	
	
	

	public GeoCubic(GeoCubic conic) {
		this(conic.cons);
		set(conic);
	}
	
	protected String getClassName() {
		return "GeoCubic";
	}
	
    public int getGeoClassType() {
    	return GEO_CLASS_CUBIC;
    }
	

	public GeoElement copy() {
		return new GeoCubic(this);
	}
	
	final public void setCoeffs(double[] coeffs) {
		for (int i = 0 ; i < 16 ; i ++) {
			this.coeffs[i] = coeffs[i];
		}
	}

	final public void setCoeffs(double a, double b, double c, double d, double e, double f, double g, double h, double i, double j, double k, double l, double m, double n, double o, double p) {
		this.coeffs[0] = a;
		this.coeffs[1] = b;
		this.coeffs[2] = c;
		this.coeffs[3] = d;
		this.coeffs[4] = e;
		this.coeffs[5] = f;
		this.coeffs[6] = g;
		this.coeffs[7] = h;
		this.coeffs[8] = i;
		this.coeffs[9] = j;
		this.coeffs[10] = k;
		this.coeffs[11] = l;
		this.coeffs[12] = m;
		this.coeffs[13] = n;
		this.coeffs[14] = o;
		this.coeffs[15] = p;
	}

	final public double[] getCoeffs() {
		return coeffs;
	}


	public boolean isFillable() {
		return true;
	}
	
	public boolean isTraceable() {
		return true;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public boolean getTrace() {
		return trace;
	}
	
	public boolean isPath() {
		return true;
	}
	

	public boolean isNumberValue() {
		return false;
	}

	public boolean isVectorValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}   
	
	public boolean isTextValue() {
		return false;
	}
	
	final public boolean isGeoConic() {
		return true;
	}
	
	public void setZero() {
		for (int i = 0 ; i < 16 ; i++) {
			coeffs[i] = 0;
		}
		// set to x^3 + y^3 = 0;
		coeffs[3] = 1; // y^3
		coeffs[12] = 1; // x^3
	}

	public boolean isVector3DValue() {
		return false;
	}


	@Override
	protected StringBuilder buildValueString() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setSphereND(GeoPointInterface M, GeoSegmentInterface segment) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setSphereND(GeoPointInterface M, GeoPointInterface P) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected String getTypeString() {
		return "cubic";
	}


	@Override
	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void set(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setUndefined() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean showInAlgebraView() {
		return true;
	}


	@Override
	protected boolean showInEuclidianView() {
		return defined;
	}


	public PathMover createPathMover() {
		// TODO Auto-generated method stub
		return null;
	}


	public double getMaxParameter() {
		// TODO Auto-generated method stub
		return 0;
	}


	public double getMinParameter() {
		// TODO Auto-generated method stub
		return 0;
	}


	public boolean isClosedPath() {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean isOnPath(GeoPointInterface PI, double eps) {
		// TODO Auto-generated method stub
		return false;
	}


	public void pathChanged(GeoPointInterface PI) {
		// TODO Auto-generated method stub
		
	}


	public void pointChanged(GeoPointInterface PI) {
		// TODO Auto-generated method stub
		
	}
	


}
