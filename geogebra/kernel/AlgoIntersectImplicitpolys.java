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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.math.analysis.polynomials.PolynomialFunction;

import geogebra.euclidian.EuclidianConstants;
import geogebra.main.Application;

/**
 *	Algorithm to intersect two implicit polynomial equations<br />
 *	output: GeoPoints if finitely many intersection points.
 */
public class AlgoIntersectImplicitpolys extends AlgoSimpleRootsPolynomial {
	
	private GeoImplicitPoly p1;
	private GeoImplicitPoly p2;
	
	private GeoConic c1;
	private List<double[]> valPairs;
	
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
        initForNearToRelationship();
        compute();               
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
	
	protected boolean rootPolishing(double[] pair){
		double x=pair[0],y=pair[1];
		double p,q;
		p=p1.evalPolyAt(x, y);
		q=p2.evalPolyAt(x, y);
		double lastErr=Double.MAX_VALUE;
		double err=Math.abs(p)+Math.abs(q);
		while(err<lastErr&&err>Kernel.STANDARD_PRECISION){
			double px,py;
			double qx,qy;
			px=p1.evalDiffXPolyAt(x, y);
			py=p1.evalDiffYPolyAt(x, y);
			qx=p2.evalDiffXPolyAt(x, y);
			qy=p2.evalDiffYPolyAt(x, y);
			double det=px*qy-py*qx;
			if (Kernel.isZero(det)){
				break;
			}
			x-=(p*qy-q*py)/det;
			y-=(q*px-p*qx)/det;
			lastErr=err;
			p=p1.evalPolyAt(x, y);
			q=p2.evalPolyAt(x, y);
			err=Math.abs(p)+Math.abs(q);
		}
		pair[0]=x;
		pair[1]=y;
		return err<Kernel.STANDARD_PRECISION;
	}

	@Override
	protected double getYValue(double t) {
		//will not be used
		return 0;
	}


	/**
	 * calculates the quotient of p/d (no calculation of the remainder is done)
	 * @param p
	 * @param d
	 * @return quotient of p/d
	 */
	private PolynomialFunction polynomialDivision(PolynomialFunction p, PolynomialFunction d){
		double[] cp=p.getCoefficients();
		double[] cd=d.getCoefficients();
		double[] cq;
		int degD=cd.length-1;
//		Application.debug(String.format("Divide %s by %s",p,d));
		while(degD>=0&&Kernel.isZero(cd[degD])){
			degD--;
		}
		if (degD<0){ // => division by zero
			cp[0]=1/0;
		}
		if (cp.length-1<degD){ 
			return new PolynomialFunction(new double[]{0});
		}else{
			cq=new double[cp.length-degD];
		}
		double lcd=cd[degD];
		int k=cp.length-1;
		for (int i=cq.length-1;i>=0;i--){
			cq[i]=cp[k]/lcd;
			for (int j=0;j<=degD-1;j++){
				cp[j+i]=cp[j+i]-cq[i]*cd[j];
			}
			k--;
		}
		PolynomialFunction ret=new PolynomialFunction(cq);
//		Application.debug(ret);
		return ret;
	}
	
	private double getLeadingCoeff(PolynomialFunction p){
		double[] c=p.getCoefficients();
		for (int i=c.length-1;i>=0;i--){
			if (!Kernel.isZero(c[i]))
				return c[i];
		}
		return 0;
	}

	@Override
	protected void compute() {
		
		if (c1!=null){
			p2=new GeoImplicitPoly(c1);
		}
		
		if (valPairs==null){
			valPairs=new LinkedList<double[]>();
		}else{
			valPairs.clear();
		}
		
		/*
		 * New approach: calculating determinant of Sylvester-matrix to get resolvent
		 * 
		 */
		
//		Application.debug("p1="+p1);
//		Application.debug("p2="+p2);
		
		GeoImplicitPoly a=p1,b=p2;
		
		if (p1.getDegX()<p2.getDegX()){
			a=p2;
			b=p1;
		}
		
		int m=a.getDegX();
		int n=b.getDegX();
		
		//setting up reduced sylvester-matrix
		PolynomialFunction[][] mat=new PolynomialFunction[n][n];
		for (int i=0;i<n;i++){
			for (int j=0;j<n;j++){
				mat[i][j]=new PolynomialFunction(new double[]{0});
				for (int k=Math.max(0, i-j);k<=Math.min(i, m+i-j);k++){
					PolynomialFunction p=new PolynomialFunction(b.getCoeff()[k]);
					mat[i][j]=mat[i][j].add(p.multiply(new PolynomialFunction(a.getCoeff()[m+i-k-j])));
				}
				for (int k=Math.max(0, i+m-j-n);k<=Math.min(i, m+i-j);k++){
					PolynomialFunction p=new PolynomialFunction(a.getCoeff()[k]);
					mat[i][j]=mat[i][j].subtract(p.multiply(new PolynomialFunction(b.getCoeff()[m+i-k-j])));
				}
			}
		}
//		Application.debug(Arrays.deepToString(mat));
		
		//Gauß-Bareiss for calculating the determinant
		
		PolynomialFunction c=new PolynomialFunction(new double[]{1});
		PolynomialFunction det=null;
		for (int k=0;k<n-1;k++){
			int r=0;
			double glc=0; //greatest leading coefficient
			for (int i=k;i<n;i++){
				double lc=getLeadingCoeff(mat[i][k]);
				if (!Kernel.isZero(lc)){
					if (Math.abs(lc)>Math.abs(glc)){
						glc=lc;
						r=i;
					}
				}
			}
			if (Kernel.isZero(glc)){
				det=new PolynomialFunction(new double[]{0});
				break;
			}else if (r>k){
				for (int j=k;j<n;j++){
					//exchange functions
					PolynomialFunction temp=mat[r][j];
					mat[r][j]=mat[k][j];
					mat[k][j]=temp;
				}
			}
			for (int i=k+1;i<n;i++){
				for (int j=k+1;j<n;j++){
					PolynomialFunction t1=mat[i][j].multiply(mat[k][k]);
					PolynomialFunction t2=mat[i][k].multiply(mat[k][j]);
					PolynomialFunction t=t1.subtract(t2);
					mat[i][j]=polynomialDivision(t, c);
				}
			}
			c=mat[k][k];
		}
		if (det==null)
			det=mat[n-1][n-1];
//		Application.debug("resultante = "+det);
		univarType=PolyY;
		double roots[]=det.getCoefficients();
		int nrRealRoots=0;
		if (roots.length>1)
			nrRealRoots=getRoots(roots,eqnSolver);
		
		double[][] coeff;
		double[] newCoeff;
		if (univarType==PolyX){
			if (p1.getDegY()<p2.getDegY()){
				coeff=p1.getCoeff();
				newCoeff=new double[p1.getDegY()+1];
			}else{
				coeff=p2.getCoeff();
				newCoeff=new double[p2.getDegY()+1];
			}
			
		}else{
			if (p1.getDegX()<p2.getDegX()){
				coeff=p1.getCoeff();
				newCoeff=new double[p1.getDegX()+1];
			}else{
				coeff=p2.getCoeff();
				newCoeff=new double[p2.getDegX()+1];
			}
		}
		
		for (int k=0;k<nrRealRoots;k++){
			double t=roots[k];
			if (univarType==PolyX){
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
				for (int i=0;i<coeff.length;i++){
					newCoeff[i]=0;
					for (int j=coeff[i].length-1;j>=0;j--){
						newCoeff[i]=newCoeff[i]*t+coeff[i][j];
					}
				}
			}
			int nr=getRoots(newCoeff,eqnSolver);
			for (int i=0;i<nr;i++){
				double[] pair=new double[2];
				if (univarType==PolyX){
					pair[0]=t;
					pair[1]=newCoeff[i];
				}else{
					pair[0]=newCoeff[i];
					pair[1]=t;
				}
				if (rootPolishing(pair))
					insert(pair);
			}
		}
		
		setPoints(valPairs);
		/* [end new]
		
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
    		 */
	}

	private void insert(double[] pair) {
		ListIterator<double[]> it=valPairs.listIterator();
		double eps=1E-3; //find good value...
		while(it.hasNext()){
			double[] p=it.next();
			if (Kernel.isGreater(p[0],pair[0],eps)){
				it.previous();
				break;
			}
			if (Kernel.isEqual(p[0],pair[0],eps)){
				if (Kernel.isGreater(p[1],pair[1],eps)){
					it.previous();
					break;
				}
				if (Kernel.isEqual(p[1], pair[1],eps))
					return; //do not add
			}
		}
		it.add(pair);
	}

	@Override
	public String getClassName() {
		return "AlgoIntersectImplicitpolys";
	}
	
	@Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECT;
    }
    

}
