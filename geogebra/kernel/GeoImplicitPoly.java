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

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.Polynomial;
import geogebra.main.Application;

public class GeoImplicitPoly extends GeoElement {

//	private Polynomial poly;
	private double[][] coeff;
	
	private boolean defined = true;
	
	protected GeoImplicitPoly(Construction c) {
		super(c);
	}
	
	protected GeoImplicitPoly(Construction c, String label,double[][] coeff){
		this(c);
		setLabel(label);
		this.coeff=new double[coeff.length][];
		for (int i=0;i<coeff.length;i++){
			this.coeff[i]=new double[coeff[i].length];
			for (int j=0;j<coeff[i].length;j++)
				this.coeff[i][j]=coeff[i][j];
		}
	}
		
	
	protected GeoImplicitPoly(Construction c, String label,Polynomial poly){
		this(c);
		setLabel(label);
		setCoeff(poly.getCoeff());
//		Application.debug("toStr->"+toString());
		//forceEuclidianVisible(true);
	}
	
	public GeoImplicitPoly(GeoImplicitPoly g){
		this(g.cons,g.label,g.coeff);
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
	
	@Override
	public String toValueString() {
		StringBuilder sb=new StringBuilder();
		if (coeff==null)
			return "";
		for (int i=0;i<coeff.length;i++){
			for (int j=0;j<coeff[i].length;j++){
				if (coeff[i][j]!=0){
					sb.append((coeff[i][j]>0?"+":""));
					if (coeff[i][j]!=1||(i==0&&j==0)){
						if (coeff[i][j]==-1&&(i!=0&&j!=0)){
							sb.append("-");
						}else{
							sb.append(coeff[i][j]);
						}
					}
					if (i>0){
						sb.append('x');
						if (i>1)
							sb.append(makePot(i));
						sb.append(' ');
					}
					if (j>0){
						sb.append('y');
						if (j>1)
							sb.append(makePot(j));
					}
				}
			}
		}
		sb.append("=0");
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return label+": "+toValueString();
	}

	@Override
	protected String getClassName() {
		return "GeoImplicitPoly";
	}

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
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
		} catch (Exception e) {
			setUndefined();
			e.printStackTrace();
		}
	}
	
	public double[][] getCoeff(){
		return coeff;
	}

}
