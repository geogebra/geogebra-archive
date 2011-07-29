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

import geogebra.Matrix.Coords;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.arithmetic.Equation;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.Polynomial;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.kernel.commands.AlgebraProcessor;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.parser.ParseException;
import geogebra.kernel.parser.Parser;
import geogebra.main.Application;
import geogebra.main.MyError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.math.linear.DecompositionSolver;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealMatrixImpl;

/**
 * Represents implicit bivariat polynomial equations, with degree greater than 2.
 */
public class GeoImplicitPoly extends GeoUserInputElement implements Path,
Traceable, Mirrorable, ConicMirrorable, Translateable, PointRotateable,
Dilateable, Transformable, EuclidianViewCE {
	public static final int IMPLICIT_POLY_BY_EQUATION = 1;
	public static final int IMPLICIT_POLY_THROUGH_POINTS = 2;
	
	private int type = IMPLICIT_POLY_BY_EQUATION;
	
	private double[][] coeff;
	private double[][] coeffSquarefree;
	private int degX;
	private int degY;

	private boolean defined = true;
	private boolean isConstant;
	
	private boolean trace; //for traceable interface
	
	private Coords [] pointsOnCurve;
	private Coords lastClosestPoint;

	private Parser parser;
	private AlgebraProcessor algebraProcessor;
	
	public GeoLocus locus;
	
//	private Thread factorThread;

	protected GeoImplicitPoly(Construction c) {
		super(c);
		degX=-1;
		degY=-1;
		coeffSquarefree=null;
		pointsOnCurve = null;
		lastClosestPoint = null;
		parser = c.getKernel().getParser();
		algebraProcessor = c.getKernel().getAlgebraProcessor();
		locus=new GeoLocus(c);
		locus.setDefined(true);
		c.registerEuclidianViewCE(this);
	}
	
	protected GeoImplicitPoly(Construction c, String label,double[][] coeff){
		this(c);
		setLabel(label);
		setCoeff(coeff);
	}
	
	protected GeoImplicitPoly(Construction c, String label,Polynomial poly){
		this(c);
		setLabel(label);
		setCoeff(poly.getCoeff());
	}
	
	
	public GeoImplicitPoly(GeoImplicitPoly g){
		this(g.cons);
		set(g);
		if(type == IMPLICIT_POLY_THROUGH_POINTS)
		{
			pointsOnCurve = new Coords[g.pointsOnCurve.length];
	        for(int i=0; i<pointsOnCurve.length; i++)
	        	pointsOnCurve[i] = new Coords(g.pointsOnCurve[i].get());
		}
	}
	
	/* 
	 *               ( A[0]  A[3]    A[4] )
	 *      matrix = ( A[3]  A[1]    A[5] )
	 *               ( A[4]  A[5]    A[2] )
	 */
	
	/**
	 * Construct GeoImplicitPoly from GeoConic
	 * @param c
	 */
	public GeoImplicitPoly(GeoConic c){
		this(c.cons);
		coeff=new double[3][3];
		coeff[0][0]=c.matrix[2];
		coeff[1][1]=2*c.matrix[3];
		coeff[2][2]=0;
		coeff[1][0]=2*c.matrix[4];
		coeff[0][1]=2*c.matrix[5];
		coeff[2][0]=c.matrix[0];
		coeff[0][2]=c.matrix[1];
		coeff[2][1]=coeff[1][2]=0;
		degX=2;
		degY=2;
//		Application.debug("Conic -> "+this);
	}
	
	public Coords[] getpointsOnCurve() {
		return pointsOnCurve;
	}
	
	
	/**
	 * Create conic from this implicitPoly (if degX == degY == 2)
	 * @param g GeoConic for storing this implicitPoly
	 */
	public void toGeoConic(GeoConicND g)
	{
		if(degX != 2 || degY != 2)
		{
			g.setUndefined();
			return;
		}
		
		double [] matrix = new double[6];
		matrix[0] = coeff[2][0];
		matrix[1] = coeff[0][2];	
		matrix[2] = coeff[0][0];
		matrix[3] = .5*coeff[1][1];
		matrix[4] = .5*coeff[1][0];
		matrix[5] = .5*coeff[0][1];
		g.setMatrix(matrix);
	}
	
	/**
	 * Create this implicitPoly from geoConic
	 * @param g geoConic
	 */
	public void fromGeoConic(GeoConicND g)
	{
		double [][] coeffs = new double[3][3];
		coeffs[2][0] = g.matrix[0];
		coeffs[0][2] = g.matrix[1];	
		coeffs[0][0] = g.matrix[2];
		coeffs[1][1] = 2*g.matrix[3];
		coeffs[1][0] = 2*g.matrix[4];
		coeffs[0][1] = 2*g.matrix[5];
		this.setCoeff(coeffs);
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
		super.set(geo);
		setCoeff(((GeoImplicitPoly)geo).getCoeff());
		locus.set(((GeoImplicitPoly)geo).locus);
		this.defined=geo.isDefined();
	}

	@Override
	public void setUndefined() {
		defined=false;
	}
	
	public void setDefined(){
		defined=true;
	}

	@Override
	public boolean showInAlgebraView() {
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		return true;
	}

	private void addPow(StringBuilder sb, int i){
		if (i>1){
			if (kernel.getCASPrintForm()==ExpressionNode.STRING_TYPE_LATEX){
				sb.append('^');
				sb.append('{');
				sb.append(i);
				sb.append('}');
			}else if ((kernel.getCASPrintForm()==ExpressionNode.STRING_TYPE_JASYMCA)||
						(kernel.getCASPrintForm()==ExpressionNode.STRING_TYPE_GEOGEBRA_XML)||
						(kernel.getCASPrintForm()==ExpressionNode.STRING_TYPE_MATH_PIPER)||
						(kernel.getCASPrintForm()==ExpressionNode.STRING_TYPE_MAXIMA)||
						(kernel.getCASPrintForm()==ExpressionNode.STRING_TYPE_MPREDUCE)){
				sb.append('^');
				sb.append(i);
			}else{
				String p="";
				while(i>0){
					int c=i%10;
					switch(c){
					case 1: p='\u00b9'+p;break;
					case 2: p='\u00b2'+p;break;
					case 3: p='\u00b3'+p;break;
					default:
						p=(char)('\u2070'+c)+p;
					}
					i=i/10;
				}
				sb.append(p);
			}
		}
	}
	
	@Override
	public String toValueString() {
		if (coeff==null)
			return "";		
		StringBuilder sb=new StringBuilder();
		boolean first=true;
		for (int i=coeff.length-1;i>=0;i--){
			for (int j=coeff[i].length-1;j>=0;j--){
				if (i==0&&j==0){
					if (first)
						sb.append("0");
//					Application.debug("pf="+kernel.getCASPrintForm());
					if (kernel.getCASPrintForm() == ExpressionNode.STRING_TYPE_MATH_PIPER) 
						sb.append(" == ");
					else
						sb.append(" = ");
					sb.append(kernel.format(-coeff[0][0]));
				}else{
					if (Math.abs(coeff[i][j])>Kernel.EPSILON){
						String number=kernel.format(coeff[i][j]);
						if (!first)
							sb.append(number.charAt(0)=='-'?"":"+");
						if (Math.abs(coeff[i][j]-1)>Kernel.EPSILON){
							if (Math.abs(coeff[i][j]+1)<Kernel.EPSILON){
								sb.append("-");
							}else{
								sb.append(number);
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
						addPow(sb,i);
						if (j>0){
							if (!first)
								sb.append(' ');
							else
								first=false;
							sb.append('y');
						}
						addPow(sb,j);
					}
				}
			}
		}

		return sb.toString();
	}
	
	@Override
	public String toString() {
		return super.toString();
//		return label+": "+toValueString();
	}

	@Override
    public String getClassName() {
		return "GeoImplicitPoly";
	}

	public boolean isVector3DValue() {
		return false;
	}
	
	/**
	 * @param c assigns given coefficient-array to be the coefficients of this Polynomial.
	 */
	public void setCoeff(double[][] c){
		isConstant=true;
		degX=-1;
		degY=-1;
		coeffSquarefree=null;
		try {
			degX=c.length-1;
			coeff = new double[c.length][];
			for (int i = 0; i < c.length; i++) {
				coeff[i] = new double[c[i].length];
				if (c[i].length>degY+1)
					degY=c[i].length-1;
				for (int j = 0; j < c[i].length; j++){
					coeff[i][j]=c[i][j];
					isConstant=isConstant&&(c[i][j]==0||(i==0&&j==0));	
				}
			}
			updatePath();
		} catch (Exception e) {
			setUndefined();
			e.printStackTrace();
		}
	}
	
	/**
	 * @param ev assigns given coefficient-array to be the coefficients of this Polynomial.
	 */
	public void setCoeff(ExpressionValue[][] ev){
		try {
			isConstant=true;
			degX=-1;
			degY=-1;
			coeff = new double[ev.length][];
			degX=ev.length-1;
			coeffSquarefree=null;
			for (int i = 0; i < ev.length; i++) {
				coeff[i] = new double[ev[i].length];
				if (ev[i].length>degY+1)
					degY=ev[i].length-1;
				for (int j = 0; j < ev[i].length; j++){
					if (ev[i][j]==null)
						coeff[i][j]=0;
					else
						coeff[i][j] = ((NumberValue) ev[i][j].evaluate())
							.getDouble();
					isConstant=isConstant&&(Kernel.isZero(coeff[i][j])||(i==0&&j==0));
				}
			}
			getFactors();
			updatePath();
		} catch (Exception e) {
			setUndefined();
			e.printStackTrace();
		}
	}
	
	/**
	 * @param squarefree if squarefree is true returns a squarefree representation of this polynomial
	 * is such representation is available
	 * @return coefficient array of this implicit polynomial equation
	 */
	public double[][] getCoeff(boolean squarefree){
		if (squarefree&&coeffSquarefree!=null){
			return coeffSquarefree;
		}else
			return coeff;
	}
	
	/**
	 * @return coefficient array of this implicit polynomial equation
	 */
	public double[][] getCoeff(){
		return coeff;
	}
	
	public double evalPolyAt(double x,double y){
		return evalPolyAt(x,y,false);
	}
	
	public double evalPolyAt(double x,double y,boolean squarefree){
		return evalPolyCoeffAt(x,y,getCoeff(squarefree));
	}
	
	public double evalPolyCoeffAt(double x,double y,double[][] coeff){
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
		return evalDiffXPolyAt(x, y,false);
	}
	
	public double evalDiffXPolyAt(double x,double y,boolean squarefree){
		double sum=0;
		double zs=0;
		double[][] coeff=getCoeff(squarefree);
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
		return evalDiffYPolyAt(x, y,false);
	}
	
	public double evalDiffYPolyAt(double x,double y,boolean squarefree){
		double sum=0;
		double zs=0;
		double[][] coeff=getCoeff(squarefree);
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
	
	/**
	 * Plugs in two rational polynomials for x and y, x|->pX/qX and y|->pX/qX in the curve 
	 * (replacing the current coefficients with the new ones)
	 * [not yet tested for qX!=qY]
	 * @param pX
	 * @param pY
	 * @param qX
	 * @param qY
	 */
	public void plugInRatPoly(double[][] pX,double[][] pY,double[][] qX,double[][] qY){
		int degXpX=pX.length-1;
		int degYpX=0;
		for (int i=0;i<pX.length;i++){
			if (pX[i].length-1>degYpX)
				degYpX=pX[i].length-1;
		}
		int degXqX=-1;
		int degYqX=-1;
		if (qX!=null){
			degXqX=qX.length-1;
			for (int i=0;i<qX.length;i++){
				if (qX[i].length-1>degYqX)
					degYqX=qX[i].length-1;
			}
		}
		int degXpY=pY.length-1;
		int degYpY=0;
		for (int i=0;i<pY.length;i++){
			if (pY[i].length-1>degYpY)
				degYpY=pY[i].length-1;
		}
		int degXqY=-1;
		int degYqY=-1;
		if (qY!=null){
			degXqY=qY.length-1;
			for (int i=0;i<qY.length;i++){
				if (qY[i].length-1>degYqY)
					degYqY=qY[i].length-1;
			}
		}
		boolean sameDenom=false;
		if (qX!=null&&qY!=null){
			sameDenom=true;
			if (degXqX==degXqY&&degYqX==degYqY){
				for (int i=0;i<qX.length;i++)
					if (!Arrays.equals(qY[i], qX[i]))
					{
						sameDenom=false;
						break;
					}
			}
		}
		int commDeg=0;
		if (sameDenom){
			//find the "common" degree, e.g. x^4+y^4->4, but x^4 y^4->8
			commDeg=getDeg();
		}
//		Application.debug(String.format("sameDenom=%s,cd=%d",sameDenom+"",commDeg));
//		Application.debug(String.format("XX=%d,YX=%d,XY=%d,YY=%d", degXPolyX,degYPolyX,degXPolyY,degYPolyY));
	//	Application.debug(String.format("degXpX=%d,degXqX=%d,degXpY=%d,degXqY=%d",degXpX=%d,degXqX=%d,degXpY=%d,degXqY=%d));
		int newDegX=Math.max(degXpX, degXqX)*degX+Math.max(degXpY, degXqY)*degY;
		int newDegY=Math.max(degYpX, degYqX)*degX+Math.max(degYpY, degYqY)*degY;
//		Application.debug(String.format("newdegX=%d,newDegY=%d",newDegX,newDegY));
		double[][] newCoeff=new double[newDegX+1][newDegY+1];
		double[][] tmpCoeff=new double[newDegX+1][newDegY+1];
		double[][] ratXCoeff=new double[newDegX+1][newDegY+1];
		double[][] ratYCoeff=new double[newDegX+1][newDegY+1];
		int tmpCoeffDegX=0;
		int tmpCoeffDegY=0;
		int newCoeffDegX=0;
		int newCoeffDegY=0;
		int ratXCoeffDegX=0;
		int ratXCoeffDegY=0;
		int ratYCoeffDegX=0;
		int ratYCoeffDegY=0;
	//	double[][] xCoeff=new double[newDegX+1][newDegY+1];
		for (int i=0;i<newDegX;i++){
			for (int j=0;j<newDegY;j++){
				newCoeff[i][j]=0;
				tmpCoeff[i][j]=0;
				ratXCoeff[i][j]=0;
				ratYCoeff[i][j]=0;
			}
		}
		ratXCoeff[0][0]=1;
		for (int x=coeff.length-1;x>=0;x--){
			if (qY!=null){
				ratYCoeff[0][0]=1;
				ratYCoeffDegX=0;
				ratYCoeffDegY=0;
			}
			int startY=coeff[x].length-1;
			if (sameDenom)
				startY=commDeg-x;
			for (int y=startY;y>=0;y--){
				if (qY==null||y==startY){
					if (coeff[x].length>y)
						tmpCoeff[0][0]+=coeff[x][y];
				}else{
					polyMult(ratYCoeff,qY,ratYCoeffDegX,ratYCoeffDegY,degXqY,degYqY); //y^N-i
					ratYCoeffDegX+=degXqY;
					ratYCoeffDegY+=degYqY;
					if (coeff[x].length>y)
						for (int i=0;i<=ratYCoeffDegX;i++){
							for (int j=0;j<=ratYCoeffDegY;j++){
								tmpCoeff[i][j]+=coeff[x][y]*ratYCoeff[i][j];
								if (y==0){
									ratYCoeff[i][j]=0; //clear in last loop
								}
							}
						}
					tmpCoeffDegX=Math.max(tmpCoeffDegX, ratYCoeffDegX);
					tmpCoeffDegY=Math.max(tmpCoeffDegY, ratYCoeffDegY);
				}
				if (y>0){
//					Application.debug(String.format("x=%d,y=%d,tX=%d,tY=%d,dXY=%d,dYY=%d",x,y,tmpCoeffDegX,tmpCoeffDegY,degXPolyY,degYPolyY));
					polyMult(tmpCoeff,pY,tmpCoeffDegX,tmpCoeffDegY,degXpY,degYpY);
					tmpCoeffDegX+=degXpY;
					tmpCoeffDegY+=degYpY;
				}
			}
			if (qX!=null&&x!=coeff.length-1&&!sameDenom){
				polyMult(ratXCoeff,qX,ratXCoeffDegX,ratXCoeffDegY,degXqX,degYqX);
				ratXCoeffDegX+=degXqX;
				ratXCoeffDegY+=degYqX;
				polyMult(tmpCoeff,ratXCoeff,tmpCoeffDegX,tmpCoeffDegY,ratXCoeffDegX,ratXCoeffDegY);
				tmpCoeffDegX+=ratXCoeffDegX;
				tmpCoeffDegY+=ratXCoeffDegY;
			}
			for (int i=0;i<=tmpCoeffDegX;i++){
				for (int j=0;j<=tmpCoeffDegY;j++){
					newCoeff[i][j]+=tmpCoeff[i][j];
					tmpCoeff[i][j]=0;
				}
			}
			newCoeffDegX=Math.max(newCoeffDegX, tmpCoeffDegX);
			newCoeffDegY=Math.max(newCoeffDegY, tmpCoeffDegY);
			tmpCoeffDegX=0;
			tmpCoeffDegY=0;
			if (x>0){
//				Application.debug(String.format("x=%d,nX=%d,nY=%d,dXY=%d,dYY=%d",x,newCoeffDegX,newCoeffDegY,degXPolyY,degYPolyY));
				polyMult(newCoeff,pX,newCoeffDegX,newCoeffDegY,degXpX,degYpX);
				newCoeffDegX+=degXpX;
				newCoeffDegY+=degYpX;
			}
		}
		
		//maybe we made the degree larger than necessary, so we try to get it down.
		double[][] newCoeffMinDeg=null;
//	Application.debug("old degX="+newDegX+"; old degY="+newDegY);
		degX=0;
		degY=0;
		for (int i=newDegX;i>=0;i--){
			for (int j=newDegY;j>=0;j--){
				if (Math.abs(newCoeff[i][j])>Kernel.EPSILON){
					if (newCoeffMinDeg==null){
						newCoeffMinDeg=new double[i+1][];
						degX=i;
					}
					if (newCoeffMinDeg[i]==null){
						newCoeffMinDeg[i]=new double[j+1];
						if (j>degY)
							degY=j;
					}
					newCoeffMinDeg[i][j]=newCoeff[i][j];
				}
			}
			if (newCoeffMinDeg!=null&&newCoeffMinDeg[i]==null){
				newCoeffMinDeg[i]=new double[1];
				newCoeffMinDeg[i][0]=0;
			}
		}
		if (newCoeffMinDeg==null){
			newCoeffMinDeg=new double[1][1];
			newCoeffMinDeg[0][0]=0;
		}
//		Application.debug("new degX="+degX+"; new degY="+degY);
		coeff=newCoeffMinDeg;
		updatePath();
		if (algoUpdateSet!=null){
			double a=0,ax=0,ay=0,b=0,bx=0,by=0;
			if (qX==null&&qY==null&&degXpX<=1&&degYpX<=1&&degXpY<=1&&degYpY<=1){
				if ((degXpX!=1||degYpX!=1||pX[1].length==1||Kernel.isZero(pX[1][1]))&&(degXpY!=1||degYpY!=1||pY[1].length==1||Kernel.isZero(pY[1][1]))){
					if (pX.length>0){
						if (pX[0].length>0){
							a=pX[0][0];
						}
						if (pX[0].length>1){
							ay=pX[0][1];
						}
					}
					if (pX.length>1){
						ax=pX[1][0];
					}
					if (pY.length>0){
						if (pY[0].length>0){
							b=pY[0][0];
						}
						if (pY[0].length>1){
							by=pY[0][1];
						}
					}
					if (pY.length>1){
						bx=pY[1][0];
					}
					double det=ax*by-bx*ay;
					if (!Kernel.isZero(det)){
						double[][] iX=new double[][]{{(b*ay-a*by)/det,-ay/det},{by/det}};
						double[][] iY=new double[][]{{-(b*ax-a*bx)/det,ax/det},{-bx/det}};
						
						Iterator<AlgoElement> it=algoUpdateSet.getIterator();
						while(it!=null&&it.hasNext()){
							AlgoElement elem=it.next();
							if (elem instanceof AlgoPointOnPath){
								GeoPoint point=((AlgoPointOnPath)elem).getP();
								if (!Kernel.isZero(point.getZ())){
									double x=point.getX()/point.getZ();
									double y=point.getY()/point.getZ();
									double px=evalPolyCoeffAt(x,y,iX);
									double py=evalPolyCoeffAt(x,y,iY);
									point.setCoords(px,py,1);
									point.updateCoords();
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void plugInPoly(double[][] polyX,double[][] polyY){
		plugInRatPoly(polyX,polyY,null,null);
/*		if (true)
			return;
		int degXPolyX=polyX.length-1;
		int degYPolyX=0;
		for (int i=0;i<polyX.length;i++){
			if (polyX[i].length-1>degYPolyX)
				degYPolyX=polyX[i].length-1;
		}
		int degXPolyY=polyY.length-1;
		int degYPolyY=0;
		for (int i=0;i<polyY.length;i++){
			if (polyY[i].length-1>degYPolyY)
				degYPolyY=polyY[i].length-1;
		}
//		Application.debug(String.format("XX=%d,YX=%d,XY=%d,YY=%d", degXPolyX,degYPolyX,degXPolyY,degYPolyY));
		int newDegX=degXPolyX*degX+degXPolyY*degY;
		int newDegY=degYPolyX*degX+degYPolyY*degY;
		double[][] newCoeff=new double[newDegX+1][newDegY+1];
		double[][] tmpCoeff=new double[newDegX+1][newDegY+1];
		int tmpCoeffDegX=0;
		int tmpCoeffDegY=0;
		int newCoeffDegX=0;
		int newCoeffDegY=0;
	//	double[][] xCoeff=new double[newDegX+1][newDegY+1];
		for (int i=0;i<newDegX;i++){
			for (int j=0;j<newDegY;j++){
				newCoeff[i][j]=0;
				tmpCoeff[i][j]=0;
	//			xCoeff[i][j]=0;
			}
		}
		for (int x=coeff.length-1;x>=0;x--){
			for (int y=coeff[x].length-1;y>=0;y--){
				tmpCoeff[0][0]+=coeff[x][y];
				if (y>0){
//					Application.debug(String.format("x=%d,y=%d,tX=%d,tY=%d,dXY=%d,dYY=%d",x,y,tmpCoeffDegX,tmpCoeffDegY,degXPolyY,degYPolyY));
					polyMult(tmpCoeff,polyY,tmpCoeffDegX,tmpCoeffDegY,degXPolyY,degYPolyY);
					tmpCoeffDegX+=degXPolyY;
					tmpCoeffDegY+=degYPolyY;
				}
			}
			for (int i=0;i<=tmpCoeffDegX;i++){
				for (int j=0;j<=tmpCoeffDegY;j++){
					newCoeff[i][j]+=tmpCoeff[i][j];
					tmpCoeff[i][j]=0;
				}
			}
			newCoeffDegX=Math.max(newCoeffDegX, tmpCoeffDegX);
			newCoeffDegY=Math.max(newCoeffDegY, tmpCoeffDegY);
			tmpCoeffDegX=0;
			tmpCoeffDegY=0;
			if (x>0){
//				Application.debug(String.format("x=%d,nX=%d,nY=%d,dXY=%d,dYY=%d",x,newCoeffDegX,newCoeffDegY,degXPolyY,degYPolyY));
				polyMult(newCoeff,polyX,newCoeffDegX,newCoeffDegY,degXPolyX,degYPolyX);
				newCoeffDegX+=degXPolyX;
				newCoeffDegY+=degYPolyX;
			}
		}
		
		//maybe we made the degree larger than necessary, so we try to get it down.
		double[][] newCoeffMinDeg=null;
//	Application.debug("old degX="+newDegX+"; old degY="+newDegY);
		degX=0;
		degY=0;
		for (int i=newDegX;i>=0;i--){
			for (int j=newDegY;j>=0;j--){
				if (Math.abs(newCoeff[i][j])>Kernel.EPSILON){
					if (newCoeffMinDeg==null){
						newCoeffMinDeg=new double[i+1][];
						degX=i;
					}
					if (newCoeffMinDeg[i]==null){
						newCoeffMinDeg[i]=new double[j+1];
						if (j>degY)
							degY=j;
					}
					newCoeffMinDeg[i][j]=newCoeff[i][j];
				}
			}
			if (newCoeffMinDeg!=null&&newCoeffMinDeg[i]==null){
				newCoeffMinDeg[i]=new double[1];
				newCoeffMinDeg[i][0]=0;
			}
		}
		if (newCoeffMinDeg==null){
			newCoeffMinDeg=new double[1][1];
			newCoeffMinDeg[0][0]=0;
		}
//		Application.debug("new degX="+degX+"; new degY="+degY);
		coeff=newCoeffMinDeg;
//		xCoeff[0][0]=1;
//		for (int x=0;x<=degX;x++){
//			for (int i=0;i<=newDegX;i++){ //maybe a smaller loop suffices
//				for (int j=0;j<=newDegY;j++)
//					tmpCoeff[i][j]=xCoeff[i][j];
//			}
//			for (int y=0;y<=degY;y++){
//				for (int i=0;i<=x;i++){ //wrong bounds
//					for (int j=0;j<=y;j++){
//						newCoeff[i][j]+=coeff[x][y]*tmpCoeff[i][j];
//					}
//				}
//			}
//		}

 */
	}
	
	/**
	 * 
	 * @param polyDest
	 * @param polySrc
	 * polyDest=polyDest*polySrc;
	 */
	private static void polyMult(double[][] polyDest,double[][] polySrc, int degDestX,int degDestY,int degSrcX,int degSrcY){
		double[][] result=new double[degDestX+degSrcX+1][degDestY+degSrcY+1];
//		Application.debug(String.format("dX=%d,dY=%d,sX=%d,sY=%d", degDestX,degDestY,degSrcX,degSrcY));
		for (int n=0;n<=degDestX+degSrcX;n++){
			for (int m=0;m<=degDestY+degSrcY;m++){
				double sum=0;
				for (int k=Math.max(0, n-degSrcX);k<=Math.min(n, degDestX);k++)
					for (int j=Math.max(0, m-degSrcY);j<=Math.min(m, degDestY);j++)
						sum+=polyDest[k][j]*polySrc[n-k][m-j];
				result[n][m]=sum;
			}
		}
		for (int n=0;n<=degDestX+degSrcX;n++){
			for (int m=0;m<=degDestY+degSrcY;m++){
				polyDest[n][m]=result[n][m];
			}
		}
	}
	
	public boolean isConstant() {
		return isConstant;
	}
	
	public int getDegX() {
		return degX;
	}

	public int getDegY() {
		return degY;
	}
	
	private void getFactors(){
		/*
		Runnable r=new Runnable(){
			public void run() {
				toGenPolynomial();
				FactorAbstract<BigRational> fEngine=FactorFactory.getImplementation(BigRational.ONE);
//				SquarefreeAbstract<BigRational> sqEngine=SquarefreeFactory.getImplementation(BigRational.ONE);
				Iterator<GenPolynomial<BigRational>> it=fEngine.factors(genPoly).keySet().iterator();
				GenPolynomial<BigRational> res=it.next();
				while(it.hasNext()){
					res=res.multiply(it.next());
				}
				Application.debug("squarefree: "+res);
				coeffSquarefree=getCoeff(res);
			}
		};
		if (factorThread!=null&&factorThread.isAlive()){
			factorThread.interrupt();
		}
		factorThread=new Thread(r);
		factorThread.setPriority(Thread.MIN_PRIORITY);
		factorThread.start();
		*/
//		toGenPolynomial();
////		FactorAbstract<BigRational> fEngine=FactorFactory.getImplementation(BigRational.ONE);
//		SquarefreeAbstract<BigRational> sqEngine=SquarefreeFactory.getImplementation(BigRational.ONE);
//		Application.debug("start squarefree");
//		Application.debug("squarefree: "+sqEngine.baseSquarefreePart(genPoly));
	}
	
	
	public Coords getPointOnCurve()
	{
		double c=0;
		GeoLine line = new GeoLine(cons, "", 0, 1, c);
		line.remove();
		AlgoIntersectImplicitpolyParametric algo = new AlgoIntersectImplicitpolyParametric(cons, this, line);
		algo.remove();
		algo.compute();
		GeoPoint [] ip = (GeoPoint[]) algo.getIntersectionPoints();
		while(ip.length == 0 && c < 10000)
		{
			c += 0.5;
			line.setCoords(0, 1, c);
			line.update();
			algo.setOutputLength(0);
			algo.update();
			algo.compute();
			ip = (GeoPoint[]) algo.getIntersectionPoints();
			if(ip.length > 0)
				break;
			line.setCoords(0, 1, -c);
			line.update();
			algo.setOutputLength(0);
			algo.update();
			algo.compute();
			ip = (GeoPoint[]) algo.getIntersectionPoints();
			if(ip.length > 0)
				break;
			line.setCoords(1, 0, c);
			line.update();
			algo.setOutputLength(0);
			algo.update();
			algo.compute();
			ip = (GeoPoint[]) algo.getIntersectionPoints();
			if(ip.length > 0)
				break;
			line.setCoords(1, 0, -c);
			line.update();
			algo.setOutputLength(0);
			algo.update();
			algo.compute();
			ip = (GeoPoint[]) algo.getIntersectionPoints();
			if(ip.length > 0)
				break;
		}
		
		Coords coord = ip[0].getCoordsInD(2);
		return coord;
	}
	
	
	public void setNearestPointOnCurve(GeoPointND PI){
		
		if(this.isOnPath(PI))
			return;
		
		Coords coords = PI.getCoordsInD(2);
		double x = coords.getX();
		double y = coords.getY();
		
		Coords gp;
		if(lastClosestPoint == null)
			gp = getPointOnCurve();
		else
			gp = lastClosestPoint;
		
		double r = Math.sqrt(Math.pow(gp.getX() - x, 2) + Math.pow(gp.getY() - y, 2));
		double coeffs[] = {1, 0, 1,  -2*x, -2*y, -r*r+x*x+y*y};
		
		GeoConic circle = new GeoConic(cons, "", coeffs);
		GeoImplicitPoly poly = new GeoImplicitPoly(circle);
		circle.remove();
		poly.remove();

		AlgoIntersectImplicitpolys algo = new AlgoIntersectImplicitpolys(cons, this, poly);
		algo.compute();
		GeoPoint [] ip = (GeoPoint[]) algo.getIntersectionPoints();
		
		double down = 0, up = r;
		while(ip.length != 1 && !Kernel.isEqual(down, up))
		{
			if(down > up)
			{
				double t = up;
				up = down;
				down = t;
			}
			
			if (ip.length == 2)
				if(ip[0].distance(ip[1]) < 1E-2)
					break;
			
			if(ip.length >= 2)
			{
				r -= (up-down)/2;
				up = r;
			}	
			else
			{
				r += (up-down)/2;
				down = r;
			}
		
			coeffs[5] = -r*r+x*x+y*y;
			circle.setCoeffs(coeffs);
			circle.update();
			poly.fromGeoConic(circle);
			
			algo = new AlgoIntersectImplicitpolys(cons, this, poly);
			algo.compute();
			ip = (GeoPoint[]) algo.getIntersectionPoints();
		}
		
		PI.setCoords2D(ip[0].getX(), ip[0].getY(), 1);
		PI.updateCoords();
		
		if(lastClosestPoint == null)
			lastClosestPoint = new Coords(PI.getCoordsInD(2).get());
		else
			lastClosestPoint.set(PI.getCoordsInD(2));
	}
	
	
	final public double distance(GeoPoint p) {
		AlgoClosestPoint algo = new AlgoClosestPoint(cons, "", this, p);
		algo.compute();
		algo.remove();
		GeoPoint pointOnCurve = (GeoPoint) algo.getOutput(0);
		return p.distance(pointOnCurve);
	}
	
	/** 
	 * Makes make curve through given points 
	 * @param points array of points
	 */
	public void throughPoints(GeoPoint[] points)
	{
		ArrayList<GeoPoint> p = new ArrayList<GeoPoint>();
		for(int i=0; i<points.length; i++)
			p.add(points[i]);
		throughPoints(p);
	}
	
	/** 
	 * Makes make curve through given points 
	 * @param points array of points
	 */
	public void throughPoints(GeoList points)
	{
		ArrayList<GeoPoint> p = new ArrayList<GeoPoint>();
		for(int i=0; i<points.size(); i++)
			p.add((GeoPoint)points.get(i));
		throughPoints(p);
	}
	
	/**
	 * make curve through given points
	 * @param points ArrayList of points
	 */
	public void throughPoints(ArrayList<GeoPoint> points)
	{
		if((int)Math.sqrt(9+8*points.size()) != Math.sqrt(9+8*points.size()))
		{
			setUndefined();
			return;
		}
		
		if(pointsOnCurve == null)
			pointsOnCurve = new Coords[points.size()];
		
		for(int i=0; i<points.size(); i++)
		{
			if(pointsOnCurve[i] == null)
				pointsOnCurve[i] = new Coords(points.get(i).x, points.get(i).y, points.get(i).z);
			else
			{
				pointsOnCurve[i].setX(points.get(i).x);
				pointsOnCurve[i].setY(points.get(i).y);
				pointsOnCurve[i].setZ(points.get(i).z);
			}
		}
		
		int degree = (int)(0.5*Math.sqrt(8*(1+points.size()))) - 1;
		int realDegree = degree;
		
		RealMatrix extendMatrix = new RealMatrixImpl(points.size(), points.size()+1);
		RealMatrix matrix = new RealMatrixImpl(points.size(), points.size());
		double [][] coeffMatrix = new double[degree+1][degree+1];
		
		DecompositionSolver solver;
		
		double [] matrixRow = new double[points.size()+1];
		double [] results = new double[points.size()];
		
		for(int i=0; i<points.size(); i++)
		{
			double x = points.get(i).x / points.get(i).z;
			double y = points.get(i).y / points.get(i).z;
			
			for(int j=0, m=0; j<degree+1; j++)
				for(int k=0; j+k != degree+1; k++)
					matrixRow[m++] = Math.pow(x, j)*Math.pow(y, k);
			extendMatrix.setRow(i, matrixRow);
		}
		
		int solutionColumn = 0, noPoints = points.size();

		do 
		{
			if(solutionColumn > noPoints)
			{
				noPoints = noPoints-realDegree-1;
				
				if(noPoints < 2)
				{
					setUndefined();
					return;
				}
				
				extendMatrix = new RealMatrixImpl(noPoints, noPoints+1);
				realDegree-=1;
				matrixRow = new double[noPoints+1];
				
				for(int i=0; i<noPoints; i++)
				{
					double x = points.get(i).x;
					double y = points.get(i).y;
					
					for(int j=0, m=0; j<realDegree+1; j++)
						for(int k=0; j+k != realDegree+1; k++)
							matrixRow[m++] = Math.pow(x, j)*Math.pow(y, k);
					extendMatrix.setRow(i, matrixRow);
				}
					
				matrix = new RealMatrixImpl(noPoints, noPoints);
				solutionColumn = 0;
			}
						
			results = extendMatrix.getColumn(solutionColumn);
			
			for(int i=0, j=0; i<noPoints;i++)
				if(i==solutionColumn)
					continue;
				else
					matrix.setColumn(j++, extendMatrix.getColumn(i));
			solutionColumn++;
			
			solver = new LUDecompositionImpl(matrix).getSolver();
		} while (!solver.isNonSingular());
		
		for(int i=0; i<results.length; i++)
			results[i] *= -1;
        		
		double [] partialSolution = solver.solve(results);
			               
		for(int i=0; i<partialSolution.length; i++)
			if(Kernel.isZero(partialSolution[i]))
				partialSolution[i] = 0;
			               
		for(int i=0; i<partialSolution.length; i++)
			if(Kernel.isZero(partialSolution[i]))
				partialSolution[i] = 0;
		
		for(int i=0, k=0; i<realDegree+1; i++)
			for(int j=0; j+i != realDegree+1; j++)
				if(k==solutionColumn-1)
					coeffMatrix[i][j] = 1;
				else
					coeffMatrix[i][j] = partialSolution[k++];
		
		this.setCoeff(coeffMatrix);
		this.update();
		this.defined = true;
		for(int i=0; i<points.size(); i++)
			if(!this.isOnPath(points.get(i),1)) //precision must not be too small, error can be > 0.01
			{
				this.setUndefined();
				return;
			}
		
		this.type = IMPLICIT_POLY_THROUGH_POINTS;
	}
	

	public void pointChanged(GeoPointND PI) {
		if (locus.getPoints().size()>0)
			locus.pointChanged(PI);
//		setNearestPointOnCurve(PI);
//		PI.getPathParameter().setT(0);
	}

	public void pathChanged(GeoPointND PI) {
		if (locus.getPoints().size()>0)
			locus.pathChanged(PI);
//		setNearestPointOnCurve(PI);
//		PI.getPathParameter().setT(0);
	}

	public boolean isOnPath(GeoPointND PI) {
		return isOnPath(PI, Kernel.STANDARD_PRECISION);
	}
	
	public boolean isOnPath(GeoPointND PI, double eps) {
//		return locus.isOnPath(PI,eps);
		//Application.debug("on path? "+PI+"; eps="+eps);

		if(!PI.isDefined())
			return false;

		GeoPoint P = (GeoPoint) PI;

		double px = P.x;
		double py = P.y;
		double pz = P.z;
		
		if(P.isFinite())
		{
			px/=pz;
			py/=pz;
		}
		
		double value = this.evalPolyAt(px, py);
		
		return Math.abs(value) < Kernel.MIN_PRECISION; 
	}

	public double getMinParameter() {
		return locus.getMinParameter();
	}

	public double getMaxParameter() {
		return locus.getMaxParameter();
	}

	public boolean isClosedPath() {
		return locus.isClosedPath();
	}

	public PathMover createPathMover() {
		return locus.createPathMover();
	}
	
	//traceable
	
	public boolean isTraceable() {
		return true;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public boolean getTrace() {
		return trace;
	}
    
    /**
     * Return degree of implicit poly (x^n y^m = 1 has degree of m+n)
     * @return degree of implicit poly
     */
    public int getDeg()
    {
    	int deg=0;
    	for (int d=degX+degY;d>=0;d--){
			for (int x=0;x<=degX;x++){
				int y=d-x;
				if (y>=0&&y<coeff[x].length){
					if (Math.abs(coeff[x][y])>Kernel.EPSILON){
						deg=d;
						d=0;
						break;
					}
				}
			}
		}
    	return deg;
    }

	public void mirror(GeoConic c) 
	{
		
		if(c.getCircleRadius() < 10e-2)
		{
			setUndefined();
			return;
		}
		
		double cx=c.getMidpoint().getX();
		double cy=c.getMidpoint().getY();
		double cr=c.getCircleRadius();
		
		plugInRatPoly(new double[][]{{cx*cx*cx + cx*cy*cy - cx*cr*cr, -2 * cx * cy , cx}, {-2*cx*cx + cr*cr,0,0}, {cx,0,0}},
				new double[][]{{cx*cx*cy + cy*cy*cy - cy*cr*cr, -2*cy*cy + cr*cr, cy}, {-2*cx*cy,0,0}, {cy,0,0}},
				new double[][]{{cx*cx + cy*cy, -2*cy, 1}, {-2*cx,0,0}, {1,0,0}},
				new double[][]{{cx*cx + cy*cy, -2*cy, 1}, {-2*cx,0,0}, {1,0,0}});
		
/*		double r = c.getCircleRadius();
		GeoPoint S = new GeoPoint(cons, "", c.getMidpoint().getX(), c.getMidpoint().getY(), 1);
		S.remove();
		if(S.inhomX != 0 || S.inhomY != 0)
			translate(new Coords(-S.getX(), -S.getY()));
		
		translate(new Coords(-S.getX(), -S.getY()));
		
		String fun = new String("");
		
		int degx = coeff.length;
		int degy = coeff[0].length;
		
		int deg = (degx > degy) ? degx : degy;
		
		String [] u = new String[degx+degy];
		for(int i=0; i<degx+degy; i++)
			u[i] = new String("");
		
		for(int i=0; i<degx; i++)
			for(int j=0; j<degy; j++)
				if(!Kernel.isZero(coeff[i][j]))
				{
					if(coeff[i][j] > 0)
						u[i+j] += " + ";
					u[i+j] += coeff[i][j] + "*x^" + i + "*y^" + j;
				}
		
		for(int i=0; i<degx+degy; i++)
			if(u[i].length() > 0)
				u[i] = " * (" + u[i] + ")";
			else 
				u[i] = "* 0";
		
		for(int i=0; i<deg; i++)
			fun += " + " + Math.pow(r, 2*i) + "*(x^2+y^2)^" + (deg-i-1) + u[i];
		fun += " = 0";
		
		ValidExpression ve = null;
		try{
			ve = parser.parseGeoGebraExpression(fun);
		} catch(ParseException e)
		{
			throw new MyError(app, "Error");
		}
		
		GeoElement geo = (GeoElement) algebraProcessor.processEquation((Equation) ve)[0];
		GeoImplicitPoly gp = ((GeoImplicitPoly)geo);
		gp.normalize();
		setCoeff(gp.getCoeff());
		geo.remove();
		
		if(S.inhomX != 0 || S.inhomY != 0)
			translate(new Coords(S.getX(), S.getY()));
			*/
	}
	
	public void normalize()
	{
		double div = 0;
		
		outer_loop:
			for(int i=coeff.length-1; i >= 0; i--)
				for(int j=coeff[0].length-1; j >= 0; j--)
					if(coeff[i][j] != 0)
					{
						div = coeff[i][j];
						break outer_loop;
					}
		
		for(int i=0; i<coeff.length; i++)
			for(int j=0; j<coeff[0].length; j++)
				coeff[i][j] /= div;
	}

	public void mirror(GeoPoint Q) {
		plugInPoly(new double[][]{{2*Q.inhomX},{-1}},new double[][]{{2*Q.inhomY,-1}});
		//doTransformation(new String(2.0 * Q.inhomX + "- a"), new String(2.0 * Q.inhomY + "- b"));
	}

	public void mirror(GeoLine g) {
		if (!g.isDefined()){
			setUndefined();
			return;
		}
		double[] dir=new double[2];
		g.getDirection(dir);
		//g.setStandardStartPoint();
		double dx=dir[0];
		double dy=dir[1];
		double x=g.getStartPoint().inhomX;
		double y=g.getStartPoint().inhomY;
		double n=1/(dx*dx+dy*dy);
		plugInPoly(new double[][]{{2*n*dy*(x*dy-y*dx),2*n*dx*dy},{1-2*dy*dy*n,0}},new double[][]{{2*n*dx*(y*dx-x*dy),1-2*n*dx*dx},{2*n*dx*dy,0}});
		
		
		/*
		String newX, newY;
		if(g.getX() == 0)
		{
			this.translate(new Coords(-g.getZ()/g.getY(), 0));
			doTransformation("x", "-y");
			this.translate(new Coords(g.getZ()/g.getY(), 0));
			return;
		}
		Coords v = new Coords(g.getZ()/g.getX(), 0);
		this.translate(v);
		
		double R = Math.PI - Math.atan(-g.getX()/g.getY());
		double cosR = Math.cos(R), sinR=Math.sin(R);
		
		newX = cosR + "*a + " + sinR + "*b";
		newY = -sinR + "*c + " + cosR + "*d";
		doTransformation(newX, newY);
		
		doTransformation("-a", "b");
		
		cosR = Math.cos(Math.PI-R);
		sinR= Math.sin(Math.PI-R);
		
		newX = cosR + "*a + " + sinR + "*b";
		newY = -sinR + "*c + " + cosR + "*d";
		doTransformation(newX, newY);
		
		v.set(1, -v.getX());
		this.translate(v);*/
		
	}

	public void translate(Coords v) {
		translate(v.getX(),v.getY());
	}
	
	public void translate(double vx,double vy){
//		Application.debug("Translate, children:"+algoUpdateSet);
		//translate directly the coefficients
		double a=-vx; //-translate in X-dir
		double b=-vy; //-translate in Y-dir
//		Application.debug(String.format("Translate vx=%f,vy=%f",vx,vy));
	//	doTransformation(new String("a+(" + a+")"), new String("b+(" + b+")"));
		plugInPoly(new double[][]{{a},{1}},new double[][]{{b,1}});

/*		double[][] newCoeff=new double[coeff.length][];
		double[] transX=new double[degX+1];
		double[] transY=new double[degY+1];
		transX[0]=1;
		for (int k=1;k<degX+1;k++)
			transX[k]=0;
		for (int i=0;i<newCoeff.length;i++){
			newCoeff[i]=new double[coeff[i].length];
			for (int j=0;j<newCoeff[i].length;j++){
				newCoeff[i][j]=0;
				transY[j]=0;
			}
		//	String s="";
			for (int k=i;k>=1;k--){
				transX[k]+=transX[k-1]*a;
		//		s+=","+transX[k];
			}
		//	Application.debug(s);
			transY[0]=1;
			for (int j=0;j<newCoeff[i].length;j++){
				for (int k=j;k>=1;k--){
					transY[k]+=transY[k-1]*b;
				}
				for (int x=0;x<=i;x++)
					for (int y=0;y<=j;y++){
						newCoeff[x][y]+=coeff[i][j]*transX[i-x]*transY[j-y];
					}
			}
		}
		coeff=newCoeff;
//		// for curves given by equation
//		if(((GeoImplicitPoly)algoParent.input[0]).pointsOnCurve == null)
//			return;
//		
//		Coords [] parentCharacteristicPoints = ((GeoImplicitPoly)algoParent.input[0]).pointsOnCurve;
//			
//		GeoPoint [] points = new GeoPoint[parentCharacteristicPoints.length];
//		for(int i=0; i<parentCharacteristicPoints.length; i++)
//		{
//			points[i] = new GeoPoint(cons, null, parentCharacteristicPoints[i].getX(), parentCharacteristicPoints[i].getY(), parentCharacteristicPoints[i].getZ());
//			points[i].translate(v);
//			points[i].remove();
//		}
//		this.throughPoints(points);
	*/
	}

	public void rotate(NumberValue phiValue) {
		double phi=phiValue.getDouble();
		double cos=Math.cos(phi);
		double sin=Math.sin(-phi);
		plugInPoly(new double[][]{{0,-sin},{cos,0}},new double[][]{{0,cos},{sin,0}});
//		double cosR = Math.cos(r.getDouble()), sinR = Math.sin(r.getDouble());
//		String newX = cosR + "*a + " + sinR + "*b";
//		String newY = -sinR + "*c + " + cosR + "*d";
//		doTransformation(newX, newY);		
	}

	public void rotate(NumberValue phiValue, GeoPoint S) {
		double phi=phiValue.getDouble();
		double cos=Math.cos(phi);
		double sin=Math.sin(-phi);
		double x=S.getInhomX();
		double y=S.getInhomY();
		plugInPoly(new double[][]{{x*(1-cos)+y*sin,-sin},{cos,0}},new double[][]{{-x*sin+y*(1-cos),cos},{sin,0}});
//		this.translate(new Coords(-S.getX()/S.getZ(), -S.getY()/S.getZ()));
//		this.rotate(r);
//		this.translate(new Coords(S.getX()/S.getZ(), S.getY()/S.getZ()));
	}

	public void dilate(NumberValue rval, GeoPoint S) {
		double r=1/rval.getDouble();
		plugInPoly(new double[][]{{(1-r)*S.getInhomX()},{r}},new double[][]{{(1-r)*S.getInhomY(),r}});
//	double f = 1/r.getDouble();	
//		doTransformation(new String(f + "*a + " + (1-f)*S.inhomX), 
//				new String(f + "*b + " + (1-f)*S.inhomY));
	}

	public void doTransformation(String newX, String newY)
	{
		kernel.setTemporaryPrintFigures(20);
		String cmd = this.toString();
		kernel.restorePrintAccuracy();
		
		cmd = cmd.replace("x", "(" + newX + ")");
		cmd = cmd.replace("y", "(" + newY + ")");
		
		cmd = cmd.replace("a", "x");
		cmd = cmd.replace("b", "y");
		cmd = cmd.replace("c", "x");
		cmd = cmd.replace("d", "y");
		
		ValidExpression ve = null;
		try{
			ve = parser.parseGeoGebraExpression(cmd);
		} catch(ParseException e)
		{
			Application.debug(cmd);
			throw new MyError(app, "Error");
		}
		
		GeoElement geo = (GeoElement) algebraProcessor.processEquation((Equation) ve)[0];
		
		if(geo instanceof GeoConicND)
			this.fromGeoConic((GeoConicND)geo);
		else
			this.setCoeff(((GeoImplicitPoly)geo).getCoeff());
		
		geo.remove();
	}
	
	@Override
	public boolean isTranslateable() {
		return true;
	}
	
	 
	 @Override
	 protected char getLabelDelimiter(){
		 return ':';
	 }
	 
	 /*
	  * Calculation of the points on the curve.
	  * Mainly used for drawing, but also for Implementation of Path-Interface
	  * Was part of DrawImplicitPoly.
	  */
	 

		public void euclidianViewUpdate() {
			if (isDefined()){
				updatePath();
			}
//			Application.debug("EuclidianViewUpdate");
		}
	 
//		private List<GeneralPath> gps;
		/**
		 * X and Y of the point on the curve next to the Right Down Corner of the View, for the Label
		 */
//		private int pointNearRDCornerX=0, pointNearRDCornerY=0;
		private ArrayList<Double[]> singularitiesCollection;
		private ArrayList<Double[]> boundaryIntersectCollection;
		
		//Second Algorithm
		final public static double EPS=Kernel.EPSILON;
		
		/**
		 * @param x
		 * @return 0 if |x|<EPS, sgn(x) otherwise
		 */
		public int epsSignum(double x){
			if (x>EPS)
				return 1;
			if (x<-EPS)
				return -1;
			return 0;
		}
		
		private class GridRect{
			double x,y,width,height;
			int[] eval;

			public GridRect(double x, double y, double width, double height) {
				super();
				this.x = x;
				this.y = y;
				this.width = width;
				this.height = height;
				eval=new int[4];
			}
		}
		
		private boolean[][] remember;
		private GridRect[][] grid;
//		private final static double GRIDSIZE=10; //in Pixel
		
		private int gridWidth=30; //grid"Resolution" how many grids in one row
		private int gridHeight=30; //how many grids in one col
		
		private double scaleX;
		private double scaleY;
		
		private double minGap=0.001;
		
		/**
		 * updates the path inside the current view-rectangle
		 */
		public void updatePath(){
			EuclidianView view=kernel.getApplication().getEuclidianView();
			if (view!=null)
				updatePath(view.getXmin(), view.getYmin(), view.getXmax()-view.getXmin(), view.getYmax()-view.getYmin(),
        			(view.getXmax()-view.getXmin())*(view.getYmax()-view.getYmin())/view.getHeight()/view.getWidth());
		}
		
		/**
		 * Calculates the path of the curve inside the given rectangle.
		 * @param rectX X of lower left corner
		 * @param rectY Y of lower left corner
		 * @param rectW width of rect
		 * @param rectH height of rect
		 * @param resolution gives the area covered by one "pixel" of the screen
		 */
		public void updatePath(double rectX,double rectY,double rectW,double rectH,double resolution){
			try{
				locus.clearPoints();
				singularitiesCollection=new ArrayList<Double[]>();
//				if (fillSign!=0)
					boundaryIntersectCollection=new ArrayList<Double[]>();
//			int gridWidth=(int)Math.ceil(view.getWidth()/GRIDSIZE);
//			int gridHeight=(int)Math.ceil(view.getHeight()/GRIDSIZE);
			//ticket #249
/*			if (view.getWidth()==0||view.getHeight()==0){ 
				Application.debug("View not visible (width or height equal to zero)");
				return;
			} */
			grid=new GridRect[gridWidth][gridHeight];
			remember=new boolean[gridWidth][gridHeight];
//			scaleX=(view.xmax-view.xmin)/view.getWidth();
//			scaleY=(view.ymax-view.ymin)/view.getHeight();
			double prec=5;
			scaleX=prec*Math.sqrt(resolution);
			scaleY=prec*Math.sqrt(resolution);
//			Application.debug("scale="+scaleX);
			double grw=rectW/gridWidth;
			double grh=rectH/gridHeight;
			double x=rectX;
			int e;
			for (int w=0;w<gridWidth;w++){
				double y=rectY;
				for (int h=0;h<gridHeight;h++){
					e=epsSignum(evalPolyAt(x,y,true));
					grid[w][h]=new GridRect(x,y,grw,grh);
					grid[w][h].eval[0]=e;
					if (w>0){
						grid[w-1][h].eval[1]=e;
						if (h>0){
							grid[w][h-1].eval[2]=e;
							grid[w-1][h-1].eval[3]=e;
						}
					}
					else if (h>0)
						grid[w][h-1].eval[2]=e;
					y+=grh;
				}
				e=epsSignum(evalPolyAt(x,y,true));
				grid[w][gridHeight-1].eval[2]=e;
				if (w>0)
					grid[w-1][gridHeight-1].eval[3]=e;
				x+=grw;
			}
			double y=rectY;
			for (int h=0;h<gridHeight;h++){
				e=epsSignum(evalPolyAt(x,y,true));
				grid[gridWidth-1][h].eval[1]=e;
				if (h>0)
					grid[gridWidth-1][h-1].eval[3]=e;
				y+=grh;
			}
			grid[gridWidth-1][gridHeight-1].eval[3]=epsSignum(evalPolyAt(x,y,true));
			for (int w=0;w<gridWidth;w++){
				for (int h=0;h<gridHeight;h++){
					remember[w][h]=false;
					if (grid[w][h].eval[0]==0){
						remember[w][h]=true;
						continue;
					}
					for (int i=1;i<4;i++){
						if (grid[w][h].eval[0]!=grid[w][h].eval[i]){
							remember[w][h]=true;
							break;
						}
					}
				}
			}
//			gps=new ArrayList<GeneralPath>();
//			EuclidianView v=kernel.getApplication().getEuclidianView();
//			Graphics2D g2=(Graphics2D) v.getGraphics();
//			for (int w=0;w<gridWidth;w++)
//				for (int h=0;h<gridHeight;h++){
//					int sx=v.toScreenCoordX(grid[w][h].x);
//					int sy=v.toScreenCoordY(grid[w][h].y);
//					for (int i=0;i<4;i++){
//						Color c=Color.black;
//						if (grid[w][h].eval[i]<0){
//							c=Color.green;
//						}else if (grid[w][h].eval[i]>0){
//							c=Color.red;
//						}
//						g2.setColor(c);
//						g2.fillOval(sx+(i%2==0?-3:3), sy+(i<2?3:-3), 2, 2);
//					}
//				}
			for (int w=0;w<gridWidth;w++){
				for (int h=0;h<gridHeight;h++){
					if (remember[w][h]){
						if (grid[w][h].eval[0]==0){
							startPath(w,h,grid[w][h].x,grid[w][h].y,locus);
						}else{
							double xs,ys;
							if (grid[w][h].eval[0]!=grid[w][h].eval[3]){
								double a=bisec(grid[w][h].x,grid[w][h].y,grid[w][h].x+grid[w][h].width,grid[w][h].y+grid[w][h].height);
								xs=grid[w][h].x+a*grid[w][h].width;
								ys=grid[w][h].y+a*grid[w][h].height;
							}else if (grid[w][h].eval[1]!=grid[w][h].eval[2]){
								double a=bisec(grid[w][h].x+grid[w][h].width,grid[w][h].y,grid[w][h].x,grid[w][h].y+grid[w][h].height);
								xs=grid[w][h].x+(1-a)*grid[w][h].width;
								ys=grid[w][h].y+a*grid[w][h].height;
							}else{
								double a=bisec(grid[w][h].x,grid[w][h].y,grid[w][h].x+grid[w][h].width,grid[w][h].y);
								xs=grid[w][h].x+a*grid[w][h].width;
								ys=grid[w][h].y;
							}
							startPath(w,h,xs,ys,locus);
						}
					}
				}
			}
			if (algoUpdateSet!=null){
				Iterator<AlgoElement> it=algoUpdateSet.getIterator();
				while(it.hasNext()){
					AlgoElement elem=it.next();
					if (elem instanceof AlgoPointOnPath){
						for (int i=0;i<elem.getInput().length;i++){
							if (elem.getInput()[i]==this){
								AlgoPointOnPath ap=(AlgoPointOnPath) elem;
								if (ap.getPath()==this){
									ap.getP().setCoords(ap.getP().getCoords(),true);
								}
								break;
							}
						}
					}
				}
//				algoUpdateSet.updateAll(); //makes some problems with dependent implicit curves
			} 
			
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		
		private final static double MIN_GRAD=Kernel.STANDARD_PRECISION; 
		private final static double MIN_STEP_SIZE=0.1; //Pixel on Screen
		private final static double START_STEP_SIZE=0.5;
		private final static double MAX_STEP_SIZE=1;
		private final static double MIN_PATH_GAP=1;  
		private final static double SING_RADIUS=1; 
		private final static double NEAR_SING=1E-3;
		
		private double scaledNormSquared(double x,double y){
			return x*x/scaleX/scaleX+y*y/scaleY/scaleY;
		}
		
		private void startPath(int w, int h, double x, double y,GeoLocus locus) {
//			Application.debug("startPath at "+x+"/"+y+" in GridRect["+w+","+h+"]");
//			if (Double.isNaN(firstX)||Double.isNaN(firstY)){ //Save the first point on the curve for the label
//				firstX=view.toScreenCoordXd(x);
//				firstY=view.toScreenCoordYd(y);
//			}
			
//			GeneralPath gp=new GeneralPath();
			double sx=x;
			double sy=y;
			double lx=Double.NaN; //no previous point
			double ly=Double.NaN;
			boolean first=true;

			double stepSize=START_STEP_SIZE*Math.max(scaleX, scaleY);
			double startX=x;
			double startY=y;
//			float pathX=(float)view.toScreenCoordXd(x);
//			float pathY=(float)view.toScreenCoordYd(y);
//			gp.moveTo(pathX,pathY);
			locus.insertPoint(x, y, false);
			int s=0;
			int lastW=w;
			int lastH=h;
			int startW=w;
			int startH=h;
			int stepCount=0;
			boolean nearSing=false;
			double lastGradX=Double.POSITIVE_INFINITY;
			double lastGradY=Double.POSITIVE_INFINITY;
			while(true){
//				if (s>10000){
//					Application.debug("Too much steps");
//					return gp;
//				}
				s++;
				boolean reachedSingularity=false;
				boolean reachedEnd=false;
				if (!Double.isNaN(lx)&&!Double.isNaN(ly)){
					if ((scaledNormSquared(startX-sx, startY-sy)<MAX_STEP_SIZE*MAX_STEP_SIZE)
						&& (scaledNormSquared(startX-sx,startY-sy)<scaledNormSquared(startX-lx,startY-ly))){
						
//						pathX=(float)view.toScreenCoordXd(x);
//						pathY=(float)view.toScreenCoordYd(y);
//						gp.lineTo(pathX,pathY);
						locus.insertPoint(x, y, true);
//						singularitiesCollection.add(new Double[]{x,y});
//						Application.debug("reached start; s="+s+"; stepcount="+stepCount);
						return;
//						return gp;
					}
				}
				while (sx<grid[w][h].x){
					if (w>0)
						w--;
					else{
//						Application.debug("reached end of grid");
						reachedEnd=true;
						break;
					}
				}
				while (sx>grid[w][h].x+grid[w][h].width){
					if (w<grid.length-1)
						w++;
					else{
						reachedEnd=true;
//						Application.debug("reached end of grid");
						break;
					}
				}
				while (sy<grid[w][h].y){
					if (h>0)
						h--;
					else{
						reachedEnd=true;
//						Application.debug("reached end of grid");
						break;
					}
				}
				while (sy>grid[w][h].y+grid[w][h].height){
					if (h<grid[w].length-1)
						h++;
					else{
						reachedEnd=true;
//						Application.debug("reached end of grid");
						break;
					}
				}
//				if (reachedEnd&&fillSign!=0){ //we reached the boundary
//					
//				}
				if (lastW!=w||lastH!=h){
					int dw=(int)Math.signum(lastW-w);
					int dh=(int)Math.signum(lastH-h);
					for (int i=0;i<=Math.abs(lastW-w);i++){
						for (int j=0;j<=Math.abs(lastH-h);j++){
							remember[lastW-dw*i][lastH-dh*j]=false;
						}
					}
				}
				lastW=w;
				lastH=h;
				
				double gradX=0;
				double gradY=0;
				if (!reachedEnd){
					gradX=evalDiffXPolyAt(sx, sy,true);
					gradY=evalDiffYPolyAt(sx, sy,true);
					
					/*
					 * Dealing with singularities: tries to reach the singularity but stops there.
					 * Assuming that the singularity is on or at least near the curve. (Since first
					 * derivative is zero this can be assumed for 'nice' 2nd derivative)
					 */
					
					if (nearSing||(Math.abs(gradX)<NEAR_SING&&Math.abs(gradY)<NEAR_SING)){
						for (Double[] pair:singularitiesCollection){ //check if this singularity is already known
							if ((scaledNormSquared(pair[0]-sx,pair[1]-sy)<SING_RADIUS*SING_RADIUS)){
								sx=pair[0];
								sy=pair[1];
//								Application.debug("jump to Sing @["+sx+","+sy+"]");
								reachedSingularity=true;
								reachedEnd=true;
								break;
							}
						}
						if (!reachedEnd){
							if (gradX*gradX+gradY*gradY>lastGradX*lastGradX+lastGradY*lastGradY){ //going away from the singularity, stop here
//								Application.debug("Sing-c @["+sx+","+sy+"]");
								singularitiesCollection.add(new Double[]{sx,sy});
								reachedEnd=true;
								reachedSingularity=true;
							}else if (Math.abs(gradX)<MIN_GRAD&&Math.abs(gradY)<MIN_GRAD){ //singularity
//								Application.debug("Sing-a @["+sx+","+sy+"]");
								singularitiesCollection.add(new Double[]{sx,sy});
								reachedEnd=true;
								reachedSingularity=true;
							}
//							Application.debug("Near Sing dp = ["+gradX+","+gradY+"]; p = ["+sx+","+sy+"]");
							lastGradX=gradX;
							lastGradY=gradY;
							nearSing=true;
						}
					}
				}
				double a=0,nX=0,nY=0;
				if (!reachedEnd){
					a=1/(Math.abs(gradX)+Math.abs(gradY)); //trying to increase numerical stability
					gradX=a*gradX;
					gradY=a*gradY;
					a=Math.sqrt(gradX*gradX+gradY*gradY);
					gradX=gradX/a; //scale vector
					gradY=gradY/a;
					nX=-gradY;
					nY=gradX;
					if (!Double.isNaN(lx)&&!Double.isNaN(ly)){
						double c=(lx-sx)*nX+(ly-sy)*nY;
						if (c>0){
							nX=-nX;
							nY=-nY;
						}
					}else{
						if (!first){ //other dir now
							nX=-nX;
							nY-=nY;
//							Application.debug("Go in other dir now");
						}
					}
					lx=sx;
					ly=sy;
				}
				while(!reachedEnd){
					sx=lx+nX*stepSize; //go in "best" direction
					sy=ly+nY*stepSize;
					int e=epsSignum(evalPolyAt(sx,sy,true));
//					Application.debug("s: "+sx+"/"+sy+";l: "+lx+"/"+ly+"; e="+e);
					if (e==0){
						if (stepSize*2<=MAX_STEP_SIZE*Math.max(scaleX, scaleY))
							stepSize*=2;
						break;
					}else{
						gradX=evalDiffXPolyAt(sx, sy,true);
						gradY=evalDiffYPolyAt(sx, sy,true);
//						Application.debug("gradient in "+sx+"/"+sy+"="+gradX+"/"+gradY);
						if (Math.abs(gradX)<MIN_GRAD&&Math.abs(gradY)<MIN_GRAD){ //singularity
							stepSize/=2;
							if (stepSize>MIN_STEP_SIZE*Math.max(scaleX, scaleY))
								continue;
							else{
//								Application.debug("Sing-b @["+sx+","+sy+"]");
								singularitiesCollection.add(new Double[]{sx,sy});
								reachedEnd=true;
								break;
							}
						}
						a=Math.sqrt(gradX*gradX+gradY*gradY);
						gradX*=stepSize/a;
						gradY*=stepSize/a;
						if (e>0){
							gradX=-gradX;
							gradY=-gradY;
						}
						int e1=epsSignum(evalPolyAt(sx+gradX,sy+gradY,true));
						if (e1==0){
							sx=sx+gradX;
							sy=sy+gradY;
							break;
						}
						if (e1!=e){
							a=bisec(sx,sy,sx+gradX,sy+gradY);
							sx+=a*gradX;
							sy+=a*gradY;
							break;
						}else{
							stepSize/=2;
							if (stepSize>MIN_STEP_SIZE*Math.max(scaleX, scaleY))
								continue;
							else{
//								Application.debug("no matching point found");
								reachedEnd=true;
								break;
							}
						}
					}
				}
				if (!reachedEnd||reachedSingularity){
//					float newPathX=(float)view.toScreenCoordXd(sx);
//					float newPathY=(float)view.toScreenCoordYd(sy);
					if (reachedSingularity||((lx-sx)*(lx-sx)+(ly-sy)*(ly-sy)>minGap*minGap)){//(newPathX-pathX)*(newPathX-pathX)+(newPathY-pathY)*(newPathY-pathY)>MIN_PATH_GAP*MIN_PATH_GAP){
//						gp.lineTo(newPathX, newPathY);
						locus.insertPoint(sx, sy, true);
//						if (pointNearRDCornerY+pointNearRDCornerX<(int)pathY+(int)pathX){
//							pointNearRDCornerX=(int)pathX;
//							pointNearRDCornerY=(int)pathY;
//						}
						stepCount++;
//						pathX=newPathX;
//						pathY=newPathY;
					}
				}
				if (reachedEnd){
					if (!first){
//						Application.debug("reached end in both dir; s="+s+"; stepcount="+stepCount);
						return; //reached the end two times
					}
					lastGradX=Double.POSITIVE_INFINITY;
					lastGradY=Double.POSITIVE_INFINITY;
//					pathX=(float)view.toScreenCoordXd(startX);
//					pathY=(float)view.toScreenCoordYd(startY);
//					gp.moveTo(pathX,pathY);
					locus.insertPoint(startX, startY, false);
					sx=startX;
					sy=startY;
					lx=Double.NaN;
					ly=Double.NaN; 
					w=startW;
					h=startH;
					lastW=w;
					lastH=h;
					first=false;//start again with other direction
					reachedEnd=false;
					reachedSingularity=false;
					nearSing=false;
				}
			}
		}
		
		/**
		 * 
		 * @param x1
		 * @param y1
		 * @param x2
		 * @param y2
		 * @return a such that |f(x1+(x2-x1)*a,y1+(y2-y1)*a)| &lt eps
		 */
		public double bisec(double x1,double y1,double x2,double y2){
			int e1=epsSignum(evalPolyAt(x1,y1,true));
			int e2=epsSignum(evalPolyAt(x2,y2,true));
			if (e1==0)
				return 0.;
			if (e2==0)
				return 1.;
			double a1=0;
			double a2=1;
			int e;
			if (e1!=e2){
//				Application.debug("enter bisec");
				//solved #278 PRECISION to small (was Double.MIN_VALUE)
				while(a2-a1>Kernel.MAX_PRECISION){
					e=epsSignum(evalPolyAt(x1+(x2-x1)*(a2+a1)/2,y1+(y2-y1)*(a2+a1)/2,true));
					if (e==0){
//						Application.debug("leave bisec");
						return (a2+a1)/2;
					}
					if (e==e1){
						a1=(a2+a1)/2;
					}else{
						a2=(a1+a2)/2;
					}
				}
//				Application.debug("leave bisec");
				return a1;
			}
			return Double.NaN;
		}


}

