/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * GeoImplicitPoly.java
 *
 * Created on 03. June 2010, 11:57
 */

package geogebra.kernel;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.jas.poly.GenPolynomial;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.Polynomial;
import geogebra.main.Application;

public class GeoImplicitPoly extends GeoElement implements Path{

	private double[][] coeff;
	
	private String userInput; //TODO Stores the Polynomial in the exact way the user entered it
	private boolean showUserInput;
	
	private boolean defined = true;
	
	protected GeoImplicitPoly(Construction c) {
		super(c);
		userInput="";
		showUserInput=false;
	}
	
	protected GeoImplicitPoly(Construction c, String label,double[][] coeff){
		this(c);
		setLabel(label);
		setCoeff(coeff);
//		this.coeff=new double[coeff.length][];
//		for (int i=0;i<coeff.length;i++){
//			this.coeff[i]=new double[coeff[i].length];
//			for (int j=0;j<coeff[i].length;j++)
//				this.coeff[i][j]=coeff[i][j];
//		}
	}
		
	
	protected GeoImplicitPoly(Construction c, String label,Polynomial poly){
		this(c);
		setLabel(label);
		setCoeff(poly.getCoeff());
	}
	
	public GeoImplicitPoly(GeoImplicitPoly g){
		this(g.cons,g.label,g.coeff);
	}
	
	public void setuserInput(String s){
		userInput=s;
		showUserInput=true;
	}

	@Override
	public GeoElement copy() {
		return new GeoImplicitPoly(this);
	}

	@Override
	public int getGeoClassType() {
		return GEO_CLASS_IMPLICIT_POLY;
	}

	@Override
	protected String getTypeString() {
		return "ImplicitPoly";
	}
	
	/**
	 * returns all class-specific xml tags for saveXML
	 */
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		getLineStyleXML(sb);
		
		sb.append("\t<coefficients rep=\"array\" data=\"");
		sb.append("[");
		for (int i=0;i<coeff.length;i++){
			if (i>0)
				sb.append(',');
			sb.append("[");
			for (int j=0;j<coeff[i].length;j++){
				if (j>0)
					sb.append(',');
				sb.append(coeff[i][j]);
			}
			sb.append("]");
		}
		sb.append("]");
		sb.append("\" />\n");
	}

	@Override
	public boolean isDefined() {
		return defined;
	}

	@Override
	public boolean isEqual(GeoElement Geo) {
		if (Geo instanceof GeoImplicitPoly){
			GeoImplicitPoly imp=(GeoImplicitPoly)Geo;
			for (int i=0;i<Math.max(imp.coeff.length,coeff.length);i++){
				int l=0;
				if (i<imp.coeff.length)
					l=imp.coeff[i].length;
				if (i<coeff.length){
					l=Math.max(l,coeff[i].length);
				}
				for (int j=0;j<l;j++){
					double c=0;
					if (i<imp.coeff.length){
						if (j<imp.coeff[i].length){
							c=imp.coeff[i][j];
						}
					}
					double d=0;
					if (i<coeff.length){
						if (j<coeff[i].length){
							d=coeff[i][j];
						}
					}
					if (c!=d)
						return false;
				}
			}
			return true;
		}
		return false;
	}
	
	

	@Override
	public boolean isGeoImplicitPoly() {
		return true;
	}

	@Override
	public boolean isPath() {
		return true;
	}

	@Override
	public void set(GeoElement geo) {
		if (!(geo instanceof GeoImplicitPoly))
			return;
		setCoeff(((GeoImplicitPoly)geo).getCoeff());
		this.defined=defined;
	}

	@Override
	public void setUndefined() {
		defined=false;
	}

	@Override
	public boolean showInAlgebraView() {
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		return true;
	}

	public String makePot(int p){
		return "^"+p;
	}
	
	public String toMathPipeString(){ 
		StringBuilder sb=new StringBuilder();
		if (coeff==null)
			return "";
		for (int i=0;i<coeff.length;i++){
			for (int j=0;j<coeff[i].length;j++){
				if (coeff[i][j]!=0){
					sb.append((coeff[i][j]>0?"+":""));
					sb.append(coeff[i][j]);
					if (i>0){
						sb.append("*x");
						if (i>1)
							sb.append(makePot(i));
					}
					if (j>0){
						sb.append("*y");
						if (j>1)
							sb.append(makePot(j));
					}
				}
			}
		}
		return sb.toString();
	}
	
	@Override
	public String toValueString() {
		if (showUserInput&&userInput.length()>0)
			return userInput;
		if (coeff==null)
			return "";		
		StringBuilder sb=new StringBuilder();
		boolean first=true;
		for (int i=coeff.length-1;i>=0;i--){
			for (int j=coeff[i].length-1;j>=0;j--){
				if (i==0&&j==0){
					if (first)
						sb.append("0");
//					if (kernel.casPrintForm == ExpressionNode.STRING_TYPE_MATH_PIPER) 
//						sb.append(" == ");
//					else
						sb.append(" = ");
					sb.append(-coeff[0][0]);
				}else{
					if (coeff[i][j]!=0){
						if (!first)
							sb.append((coeff[i][j]>0?"+":""));
						if (coeff[i][j]!=1){
							if (coeff[i][j]==-1){
								sb.append("-");
							}else{
								sb.append(coeff[i][j]);
							}
							first=false;
						}
						if (i>0){
							if (!first)
								sb.append(' ');
							else
								first=false;
							sb.append('x');
						}
						if (i>1){
							sb.append('^');
							sb.append(i);
						}
						if (j>0){
							if (!first)
								sb.append(' ');
							else
								first=false;
							sb.append('y');
						}
						if (j>1){
							sb.append('^');
							sb.append(j);
						}
					}
				}
			}
		}
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return label+": "+toValueString();
	}

	@Override
    public String getClassName() {
		return "GeoImplicitPoly";
	}

	public boolean isVector3DValue() {
		return false;
	}
	
	public void setCoeff(double[][] c){
		try {
			coeff = new double[c.length][];
			for (int i = 0; i < c.length; i++) {
				coeff[i] = new double[c[i].length];
				for (int j = 0; j < c[i].length; j++)
					coeff[i][j]=c[i][j];
			}
		} catch (Exception e) {
			setUndefined();
			e.printStackTrace();
		}
	}
	
	public void setCoeff(ExpressionValue[][] ev){
		try {
			coeff = new double[ev.length][];
			for (int i = 0; i < ev.length; i++) {
				coeff[i] = new double[ev[i].length];
				for (int j = 0; j < ev[i].length; j++)
					if (ev[i][j]==null)
						coeff[i][j]=0;
					else
						coeff[i][j] = ((NumberValue) ev[i][j].evaluate())
							.getDouble();
			}
			getFactors();
		} catch (Exception e) {
			setUndefined();
			e.printStackTrace();
		}
	}
	
	public double[][] getCoeff(){
		return coeff;
	}
	
	public double evalPolyAt(double x,double y){
		double sum=0;
		double zs=0;
		//Evaluating Poly via the Horner-scheme
		if (coeff!=null)
			for (int i=coeff.length-1;i>=0;i--){
				zs=0;
				for (int j=coeff[i].length-1;j>=0;j--){
					zs=y*zs+coeff[i][j];
				}
				sum=sum*x+zs;
			}
		return sum;
	}
	
	public double evalDiffXPolyAt(double x,double y){
		double sum=0;
		double zs=0;
		//Evaluating Poly via the Horner-scheme
		if (coeff!=null)
			for (int i=coeff.length-1;i>=1;i--){
				zs=0;
				for (int j=coeff[i].length-1;j>=0;j--){
					zs=y*zs+coeff[i][j];
				}
				sum=sum*x+i*zs;
			}
		return sum;
	}
	
	public double evalDiffYPolyAt(double x,double y){
		double sum=0;
		double zs=0;
		//Evaluating Poly via the Horner-scheme
		if (coeff!=null)
			for (int i=coeff.length-1;i>=0;i--){
				zs=0;
				for (int j=coeff[i].length-1;j>=1;j--){
					zs=y*zs+j*coeff[i][j];
				}
				sum=sum*x+zs;
			}
		return sum;
	}
	
	private void getFactors(){
//		String functionIn = toMathPipeString();
//		
//		Application.debug("...");
//		
//		JasPolynomial jp=new JasPolynomial("Integer", "x,y", functionIn);
////		Map<GenPolynomial,Long> fact=
//		List<GenPolynomial>	fact=jp.getFactorizationEngine().factorsRadical(jp.getPolynomial());
//		
//		Application.debug(fact.size()+"");
//		
//		//for (Entry<GenPolynomial,Long> e:fact.entrySet()){
//		for (GenPolynomial gp:fact){
//			Application.debug("Factor: "+gp);
//		}
// 	    StringBuilder sb=new StringBuilder();
//		sb.setLength(0);
//	    sb.append("Factors((");
//	    sb.append(functionIn);
//	    sb.append("))");
//		String functionOut = kernel.evaluateMathPiper(sb.toString());
//		Application.debug(functionOut);
	}

	public void pointChanged(GeoPointInterface PI) {
		// TODO Auto-generated method stub
		
	}

	public void pathChanged(GeoPointInterface PI) {
		// TODO Auto-generated method stub
		
	}

	public boolean isOnPath(GeoPointInterface PI, double eps) {
		
		return false;
	}

	public double getMinParameter() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getMaxParameter() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isClosedPath() {
		// TODO Auto-generated method stub
		return false;
	}

	public PathMover createPathMover() {
		// TODO Auto-generated method stub
		return null;
	}

}
