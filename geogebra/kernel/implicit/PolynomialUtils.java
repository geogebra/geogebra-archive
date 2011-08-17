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
 * Created on 17.08.2011, 13:05
 */

package geogebra.kernel.implicit;

import geogebra.kernel.Kernel;

import org.apache.commons.math.analysis.polynomials.PolynomialFunction;

public class PolynomialUtils {
	
	/**
	 * calculates the quotient of p/d (no calculation of the remainder is done)
	 * @param cp
	 * @param cd
	 * @return quotient of cp/cd
	 */
	public static double[] polynomialDivision(double[] cp,double[] cd){
		double[] cq;
		cp=cp.clone();
		int degD=cd.length-1;
//		Application.debug(String.format("Divide %s by %s",p,d));
		while(degD>=0&&Kernel.isZero(cd[degD])){
			degD--;
		}
		if (degD<0){ // => division by zero
			cp[0]=1/0;
		}
		if (cp.length-1<degD){ 
			return new double[]{0};
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
		return cq;
	}
	
	/**
	 * calculates the quotient of p/d (no calculation of the remainder is done)
	 * @param p
	 * @param d
	 * @return quotient of p/d
	 */
	public static PolynomialFunction polynomialDivision(PolynomialFunction p, PolynomialFunction d){
		return new PolynomialFunction(polynomialDivision(p.getCoefficients(),d.getCoefficients()));
	}
	
	public static int getDegree(PolynomialFunction p){
		return getDegree(p.getCoefficients());
	}
	
	public static int getDegree(double[] c){
		for (int i=c.length-1;i>=0;i--){
			if (!Kernel.isZero(c[i]))
				return i;
		}
		return -1;
	}
	
	public static double getLeadingCoeff(double[] c){
		int d=getDegree(c);
		if (d>=0)
			return c[d];
		else
			return 0;
	}
	
	public static double getLeadingCoeff(PolynomialFunction p){
		return getDegree(p.getCoefficients());
	}
	
	public static double eval(double[] c,double x){
		if (c.length==0)
			return 0;
		double s=c[c.length-1];
		for (int i=c.length-2;i>=0;i--){
			s*=x;
			s+=c[i];
		}
		return s;
	}

}
