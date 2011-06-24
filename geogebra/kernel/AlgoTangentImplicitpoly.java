/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.kernel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.analysis.polynomials.PolynomialFunction;

import edu.jas.arith.BigRational;
import edu.jas.gb.GBFactory;
import edu.jas.gb.GroebnerBase;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import geogebra.euclidian.EuclidianConstants;
import geogebra.main.Application;

/**
 *	Algorithm to calculate all tangents to the implicit polynomial equation
 *	either going threw a given point or parallel to given line.
 */
public class AlgoTangentImplicitpoly extends AlgoElement {
	
	private GeoImplicitPoly p;
	private GeoPoint R;
	private GeoLine g;
	
	private OutputHandler<GeoLine> tangents;
	
	private String[] labels;
	
	private final static double EPS_ANGLE=1E-3;
	

	protected AlgoTangentImplicitpoly(Construction c) {
		super(c);
	}
	
	protected AlgoTangentImplicitpoly(Construction c, String[] labels,GeoImplicitPoly p) {
		this(c);
		this.labels=labels;
		this.p=p;
	}
	 
	
	public AlgoTangentImplicitpoly(Construction c,String[] labels,GeoImplicitPoly p,GeoPoint R) {
		this(c,labels,p);
		this.R=R;
		setInputOutput();
	}
	
	public AlgoTangentImplicitpoly(Construction c,String[] labels,GeoImplicitPoly p,GeoLine g) {
		this(c,labels,p);
		this.g=g;
		setInputOutput();
	}
	
	public AlgoTangentImplicitpoly(Construction c,GeoImplicitPoly p,GeoPoint R) {
		this(c,null,p,R);
	}
	
	public AlgoTangentImplicitpoly(Construction c,GeoImplicitPoly p,GeoLine g) {
		this(c,null,p,g);
	}

	@Override
	protected void setInputOutput() {
		input=new GeoElement[2];
		input[0]=p;
		if (g!=null)
			input[1]=g;
		else 
			input[1]=R;
		tangents=new OutputHandler<GeoLine>(new elementFactory<GeoLine>() {
			public GeoLine newElement() {
				GeoLine g=new GeoLine(cons);
				g.setParentAlgorithm(AlgoTangentImplicitpoly.this);
				return g;
			}
		});
		tangents.setLabels(labels);
		setDependencies();
	}
	
	
    
	@Override
	protected void compute() {
		// idea: find intersection points between given curve and
		// curve dF/dx * x_p + dF/dy * y_p + u_{n-1} + 2*u_{n-2} + ... + n*u_0
		// and construct lines through (x_p, y_p) and intersection points, 
		// where (x_p, y_p) is given point.
		
        if (!R.isDefined()) {
        	tangents.adjustOutputSize(0);
        	return;
        }   
		
        tangents.adjustOutputSize(0);
        
		double [][] matrix = this.p.getCoeff();
		
		double x = this.R.getX();
		double y = this.R.getY();
		
		int degX = this.p.getDegX();
		int degY = this.p.getDegY();
		
		double [][] matrixx = new double[degX+1][degY+1];
		double [][] matrixy = new double[degX+1][degY+1];
		
		for(int i=1; i<degX+1; i++)
			for(int j=0; j<degY+1; j++)
				matrixx[i-1][j] = x*i*matrix[i][j];
		
		for(int i=0; i<degX+1; i++)
			for(int j=1; j<degY+1; j++)
				matrixy[i][j-1] = y*j*matrix[i][j];
	
		double [][] newMatrix = new double[degX+1][degY+1];
		
		int maxDeg = (degX > degY) ? degX : degY;
		for(int i=0; i<degX+1; i++)
			for(int j=0; j<degY+1; j++)
				newMatrix[i][j] = (maxDeg - (i+j) + 1) * matrix[i][j] + matrixx[i][j] + matrixy[i][j];
		
		GeoImplicitPoly newPoly = new GeoImplicitPoly(cons, "", newMatrix);
		newPoly.remove();
		
		AlgoIntersectImplicitpolys algo = new AlgoIntersectImplicitpolys(cons, this.p, newPoly);
		algo.compute();
		algo.remove();
		GeoPoint[] ip = algo.getIntersectionPoints();
		
		int n=0;
		for(int i=0; i<ip.length; i++)
		{
			if(Kernel.isEqual(ip[i].inhomX, R.inhomX, 1E-2) 
					&& Kernel.isEqual(ip[i].inhomY, R.inhomY, 1E-2))
				continue;
			tangents.adjustOutputSize(tangents.size() + 1);
			tangents.getElement(n).setCoords(ip[i].getY() - this.R.getY(), this.R.getX() - ip[i].getX(), 
				ip[i].getX() * this.R.getY() - this.R.getX() * ip[i].getY());
			n++;
		}
		
		if(p.isOnPath(R))
        {
			tangents.adjustOutputSize(tangents.size() + 1);
	        double dfdx = this.p.evalDiffXPolyAt(R.inhomX, R.inhomY);
			double dfdy = this.p.evalDiffYPolyAt(R.inhomX, R.inhomY);
			tangents.getElement(n).setCoords(dfdx, dfdy, 
					-dfdx*R.inhomX - dfdy*R.inhomY);
		}
	}

	@Override
	public String getClassName() {
		return "AlgoTangentImplicitpoly";
	}

	@Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_TANGENTS;
    }

	
	public GeoLine[] getTangents() {
		return tangents.getOutput(new GeoLine[tangents.size()]);
	}
	
	public void setLabels(String[] labels) {
        tangents.setLabels(labels);

        update();
    }

}
