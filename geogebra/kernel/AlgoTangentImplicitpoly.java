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
		tangents.adjustOutputSize(ip.length);
		
		for(int i=0; i<ip.length; i++)
			tangents.getElement(i).setCoords(ip[i].getY() - this.R.getY(), this.R.getX() - ip[i].getX(), 
					ip[i].getX() * this.R.getY() - this.R.getX() * ip[i].getY());
		
		/*
		String[] vars={"y","x","l"};
		int varX=1;
		int varY=2;
		int varL=0;
		
		//Create Ring in 3 vars (x,y and the 'scalar' l)
		GenPolynomialRing<BigRational> ring=new GenPolynomialRing<BigRational>(new BigRational(0),vars.length,new TermOrder(TermOrder.INVLEX),vars);
		
		//The polynomial and both it derivatives as GenPolynomial
		GenPolynomial<BigRational> P=p.toGenPolynomial().extend(ring, 0, 0);
		GenPolynomial<BigRational> Px=GeoImplicitPoly.<BigRational>baseDeriviative(P,varX);
		GenPolynomial<BigRational> Py=GeoImplicitPoly.<BigRational>baseDeriviative(P,varY);
		GenPolynomial<BigRational> P1=null,P2=null;
		
//		Application.debug("extend: "+P);
//		Application.debug("Px: "+Px);
//		Application.debug("Py: "+Py);
		
		
		//create the Polynomials to solve
		/*
		 * the tangent in a point (x0,y0) on the curve can be calculated as the line in direction
		 * (-dP(x0,y0)/dy,dP(x0,y0)/dx) (normal to the steepest descent)
		 * 
		 * if a point (R) is given we search for points (x0,y0) such that there exists scalar l:
		 * (1) P(x0,y0)=0 (point on curve)
		 * (2) x0-l*(dP(x0,y0)/dy)=R.x
		 * (3) y0+l*(dP(x0,y0)/dx)=R.y 
		 * 
		 * if line (g) given:
		 * (1) P(x0,y0)=0 (point on curve)
		 * (2) l*(dP(x0,y0)/dy)=-g.y
		 * (3) l*(dP(x0,y0)/dx)=g.x
		 */
		
		/*if (R!=null){
			P1=Py.multiply(new BigRational(-1), ExpVector.create(3, varL, 1)).sum(BigRational.ONE,ExpVector.create(3,varX,1)).sum(GeoImplicitPoly.toRational(-R.inhomX));
			P2=Px.multiply(BigRational.ONE, ExpVector.create(3, varL, 1)).sum(BigRational.ONE,ExpVector.create(3,varY,1)).sum(GeoImplicitPoly.toRational(-R.inhomY));
		}else if (g!=null){
			P1=Py.multiply(BigRational.ONE,ExpVector.create(3, varL, 1)).sum(GeoImplicitPoly.toRational(-g.getY()));
			P2=Px.multiply(BigRational.ONE.fromInteger(-1),ExpVector.create(3, varL, 1)).sum(GeoImplicitPoly.toRational(g.getX()));
		}else
			return;
		
//		Application.debug("P1: "+P1);
//		Application.debug("P2: "+P2);
		
		List<GenPolynomial<BigRational>> polynomials = new ArrayList<GenPolynomial<BigRational>>();
		polynomials.add(P);
		polynomials.add(P1);
		polynomials.add(P2);
		
		//3 polynomials, use groebner basis to simplify the system
		GroebnerBase<BigRational> gb = GBFactory.getImplementation(BigRational.ONE);
    	List<GenPolynomial<BigRational>> G=gb.GB(polynomials);
    	Application.debug("Gr�bner Basis: "+G);

    	//we want to get univariat polynomials in x or y (we can solve them)
    	boolean[] varSet=new boolean[3];
    	varSet[varX]=true;
    	varSet[varY]=true;
    	
    	//find such a univariat polynomial in the GroebnerBasis and convert it
    	PolynomialFunction pf=GeoImplicitPoly.getUnivariatPoly(G, varSet);
//    	Application.debug("Solve "+pf);
    	double[] roots=pf.getCoefficients();
    	//solve it with the equationSolver
    	int nrRoots=AlgoSimpleRootsPolynomial.getRoots(roots, cons.getEquationSolver());
    	
//    	Application.debug("nr Solutions: "+nrRoots);
    	
    	List<Double[]> valPairs=new ArrayList<Double[]>();
    	int varEval=(varSet[varX]?varX:varY);
    	//iterate over our roots and plug the value into the remaining polynomials (in x and y) (we can throw away polynomials in which 'l' occurs)
    	for (int i=0;i<nrRoots;i++){
    		ArrayList<Double> actRoots=null;
    		for (GenPolynomial<BigRational> p:G){
//    			Application.debug("poly: "+p);
    			if (p.degreeVector().getVal(varL)==0){
    				//plug in the currently selected root
    				GenPolynomial<BigRational> e=GeoImplicitPoly.evalGenPolySimple(p,GeoImplicitPoly.toRational(roots[i]),varEval);
//    				Application.debug("evalPoly at "+roots[i]+": "+e);
    				if (actRoots==null){
    					if (GeoImplicitPoly.isZero(e,Kernel.MIN_PRECISION))
    						continue;
    					boolean[] b=new boolean[3];
    					b[varX+varY-varEval]=true;
    					//because we plugged in x or y, we get univariat polynomials in the other variable
    					PolynomialFunction f=GeoImplicitPoly.getUnivariatPoly(e,b);
    					double[] roots2=f.getCoefficients();
    					//solve and remember this roots
    			    	int nrRoots2=AlgoSimpleRootsPolynomial.getRoots(roots2, cons.getEquationSolver());
    			    	actRoots=new ArrayList<Double>(nrRoots2);
    			    	for (int j=0;j<nrRoots2;j++){
    			    		actRoots.add(roots2[j]);
    			    	}
    				}else{
    					//we already have a finite selection of roots
    					//but they also have to be roots of this polynomial
    					//throw away those who don't match this condition
    					for (int j=0;j<actRoots.size();j++){
    						GenPolynomial<BigRational> n=GeoImplicitPoly.evalGenPolySimple(e,GeoImplicitPoly.toRational(actRoots.get(j)),varX+varY-varEval);
//    						Application.debug("zeropoly? "+n);
    						if (!GeoImplicitPoly.isZero(n,Kernel.MIN_PRECISION)){
    							actRoots.remove(j--);
    						}
    					}
    				}
    			}
    		}
    		//we got some roots now we can create the pairs
    		if (actRoots!=null){ //==null => solution at least 1D (infinitely many points)
    			int vE=(varEval-varX)/(varY-varX); //ve=0 =>varEval==varX, ve=1 =>varEval=varY
    			for (int j=0;j<actRoots.size();j++){
    				Double[] pair=new Double[2];
    				pair[vE]=roots[i];
    				pair[1-vE]=actRoots.get(j);
    				//test the pair we found:
    				double lx;
    				double ly;
    				if (R!=null){
    					lx=R.x-pair[0];
    					ly=R.y-pair[1];
    				}else{
    					lx=g.y;
    					ly=-g.x;
    				}
    				double tx=p.evalDiffXPolyAt(pair[0], pair[1]);
    				double ty=p.evalDiffYPolyAt(pair[0], pair[1]);
//    				double ll=Math.sqrt(lx*lx+ly*ly);
//    				lx=lx/ll;
//    				ly=ly/ll;
//    				double tl=Math.sqrt(tx*tx+ty*ty);
//    				tx=tx/tl;
//    				ty=ty/tl;
    				double ip=tx*lx+ty*ly;
//    				if (Math.abs(ip)<EPS_ANGLE)
    				if (ip*ip<=(lx*lx+ly*ly)*(tx*tx+ty*ty)*EPS_ANGLE*EPS_ANGLE)
    					if (p.evalPolyAt(pair[0], pair[1])<Kernel.MIN_PRECISION)
    						valPairs.add(pair);
//    				else
//    					Application.debug("ip="+ip);
    			}
    		}
    	}
    	//we have a list of pairs (x0,y0) and have to make GeoLines out of them
    	tangents.adjustOutputSize(valPairs.size());
    	for (int i=0;i<valPairs.size();i++){
    		double a=Double.NaN;
    		double b=Double.NaN;
    		double c=Double.NaN;
//    		Application.debug("TP = ("+valPairs.get(i)[0]+","+valPairs.get(i)[1]+")");
    		if (R!=null){
    			a=valPairs.get(i)[1]-R.inhomY;
    			b=-(valPairs.get(i)[0]-R.inhomX);
    			c=-(R.x*a+R.y*b);
    		}else if(g!=null){
    			a=g.x;
    			b=g.y;
    			c=-(valPairs.get(i)[0]*a+valPairs.get(i)[1]*b);
    		}
    		
    		tangents.getElement(i).setCoords(a,b,c);
    	}*/
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
