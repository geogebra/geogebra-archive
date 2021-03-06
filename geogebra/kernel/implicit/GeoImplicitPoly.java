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

package geogebra.kernel.implicit;

import geogebra.euclidian.EuclidianViewInterface;
import geogebra.kernel.AlgoClosestPoint;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.AlgoPointOnPath;
import geogebra.kernel.ConicMirrorable;
import geogebra.kernel.Construction;
import geogebra.kernel.Dilateable;
import geogebra.kernel.EuclidianViewCE;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoLocus;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoUserInputElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Mirrorable;
import geogebra.kernel.MyPoint;
import geogebra.kernel.Path;
import geogebra.kernel.PathMover;
import geogebra.kernel.PointRotateable;
import geogebra.kernel.Traceable;
import geogebra.kernel.Transformable;
import geogebra.kernel.Translateable;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.Polynomial;
import geogebra.kernel.kernelND.GeoPointND;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
	
	private double[][] coeff;
	private double[][] coeffSquarefree;
	private int degX;
	private int degY;

	private boolean defined = true;
	private boolean isConstant;
	private boolean calcPath;
	
	private boolean trace; //for traceable interface
	
	public GeoLocus locus;
	public Polynomial poly;

	public GeoImplicitPoly(Construction c) {
		super(c);
		degX=-1;
		degY=-1;
		coeffSquarefree=new double[0][0];
		locus=new GeoLocus(c);
		locus.setDefined(true);
		calcPath=true;
		c.registerEuclidianViewCE(this);
	}
	
	private GeoImplicitPoly(Construction c, String label,double[][] coeff,boolean calcPath){
		this(c);
		setLabel(label);
		this.calcPath=calcPath;
		setCoeff(coeff,calcPath);
		if (!calcPath)
			c.unregisterEuclidianViewCE(this);
	}
	
	protected GeoImplicitPoly(Construction c, String label,double[][] coeff){
		this(c,label,coeff,true);
	}
	
	private GeoImplicitPoly(Construction c, String label,Polynomial poly,boolean calcPath){
		this(c);
		this.poly = poly;
		setLabel(label);
		this.calcPath=calcPath;
		setCoeff(poly.getCoeff(),calcPath);
		if (!calcPath)
			c.unregisterEuclidianViewCE(this);
	}
	
	public GeoImplicitPoly(Construction c, String label,Polynomial poly){
		this(c,label,poly,true);
	}
	
	/**
	 * 
	 * @param c
	 * @param coeff
	 * @return a GeoImplicitPoly witch doesn't calculate it's path.
	 */
	public static GeoImplicitPoly createImplicitPolyWithoutPath(Construction c,double[][] coeff){
		return new GeoImplicitPoly(c,null,coeff,false);
	}
	
	/**
	 * The curve will no longer update the Path and won't receive updates
	 * when the euclidian View changes.
	 * Useful if the curve is used as a container for helper algorithms.
	 * This method should be called directly after instantiation, via the one
	 * argument constructor, if the coefficients are present already
	 * {@link #createImplicitPolyWithoutPath}
	 */
	public void preventPathCreation(){
		calcPath=false;
		cons.unregisterEuclidianViewCE(this);
	}
	
	
	public GeoImplicitPoly(GeoImplicitPoly g){
		this(g.cons);
		set(g);
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
		this(c.getConstruction());
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
					if (!Kernel.isEqual(c, d))
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
		setCoeff(((GeoImplicitPoly)geo).getCoeff(),false);
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
	protected String toRawValueString() {
		if (coeff==null)
			return "";		
		StringBuilder sb=new StringBuilder();
		boolean first=true;
		for (int i=coeff.length-1;i>=0;i--){
			for (int j=coeff[i].length-1;j>=0;j--){
				if (i==0&&j==0){
					if (first)
						sb.append("0");
					if (kernel.getCASPrintForm() == ExpressionNode.STRING_TYPE_MATH_PIPER) 
						sb.append("== ");
					else
						sb.append("= ");
					sb.append(kernel.format(-coeff[0][0]));
				}else{
					String number=kernel.format(coeff[i][j]);
					boolean pos=true;
					if (number.charAt(0)=='-'){
						pos=false;
						number=number.substring(1);
					}
					if (!number.equals("0")){
						if (pos){
							if (!first){
								sb.append('+');
							}
						}else{
							sb.append('-');
						}
						if (!first){
							sb.append(' ');
						}
						first=false;
						if (!number.equals("1")){
							sb.append(number);
						}
						if (i>0){
							sb.append('x');
						}
						addPow(sb,i);
						if (j>0){
							if (i>0){ //insert blank after x^i
								sb.append(' ');
							}
							sb.append('y');
						}
						addPow(sb,j);
						sb.append(' ');
					}
				}
			}
		}

		return sb.toString();
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
		setCoeff(c,true);
	}
	
	/**
	 * @param c assigns given coefficient-array to be the coefficients of this Polynomial.
	 * @param calcPath : the path should be calculated "new".
	 */
	public void setCoeff(double[][] c,boolean calcPath){
		isConstant=true;
		degX=-1;
		degY=-1;
		coeffSquarefree=null;
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
		if (calcPath&&this.calcPath)
			updatePath();
	}
	
	/**
	 * @param ev assigns given coefficient-array to be the coefficients of this Polynomial.
	 */
	public void setCoeff(ExpressionValue[][] ev){
		setCoeff(ev,true);
	}
	
	/**
	 * @param ev assigns given coefficient-array to be the coefficients of this Polynomial.
	 * @param calcPath 
	 */
	public void setCoeff(ExpressionValue[][] ev,boolean calcPath){
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
		if (calcPath&&this.calcPath)
			updatePath();
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
	 * Plugs in two rational polynomials for x and y, x|->pX/qX and y|->pY/qY in the curve 
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
		int newDegX=Math.max(degXpX, degXqX)*degX+Math.max(degXpY, degXqY)*degY;
		int newDegY=Math.max(degYpX, degYqX)*degX+Math.max(degYpY, degYqY)*degY;

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
				polyMult(newCoeff,pX,newCoeffDegX,newCoeffDegY,degXpX,degYpX);
				newCoeffDegX+=degXpX;
				newCoeffDegY+=degYpX;
			}
		}
		//maybe we made the degree larger than necessary, so we try to get it down.
		coeff=PolynomialUtils.coeffMinDeg(newCoeff);
		//calculate new degree
		degX=coeff.length-1;
		degY=0;
		for (int i=0;i<coeff.length;i++){
			degY=Math.max(degY,coeff[i].length-1);
		}
		
		setValidInputForm(false); //we changed the polynomial => not the same as the userInput
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
							if (elem instanceof AlgoPointOnPath && isIndependent()){
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
	}
	
	/**
	 * 
	 * @param polyDest
	 * @param polySrc
	 * polyDest=polyDest*polySrc;
	 */
	private static void polyMult(double[][] polyDest,double[][] polySrc, int degDestX,int degDestY,int degSrcX,int degSrcY){
		double[][] result=new double[degDestX+degSrcX+1][degDestY+degSrcY+1];
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

	}
	
	final public double distance(GeoPoint p) {
		AlgoClosestPoint algo = new AlgoClosestPoint(cons, "", this, p);
		algo.remove();
		GeoPoint pointOnCurve = (GeoPoint) algo.getP();
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
		
		int degree = (int)(0.5*Math.sqrt(8*(1+points.size()))) - 1;
		int realDegree = degree;
		
		RealMatrix extendMatrix = new RealMatrixImpl(points.size(), points.size()+1);
		RealMatrix matrix = new RealMatrixImpl(points.size(), points.size());
		double [][] coeffMatrix = new double[degree+1][degree+1];
		
		DecompositionSolver solver;
		
		double [] matrixRow = new double[points.size()+1];
		double [] results;
		
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
			
			for(int i=0, j=0; i<noPoints+1;i++)
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
			 
		double [] solution = new double[partialSolution.length+1];
		
		for(int i=0, j=0; i<solution.length; i++)
			if(i == solutionColumn-1)
				solution[i] = 1;
			else
			{
				solution[i] = (Kernel.isZero(partialSolution[j])) ? 0 : partialSolution[j];
				j++;
			}
			
		for(int i=0, k=0; i < realDegree+1; i++)
			for(int j=0; i+j < realDegree+1; j++)
				coeffMatrix[i][j] = solution[k++];
		
		this.setCoeff(coeffMatrix,true);
		
		this.defined = true;
		for(int i=0; i<points.size(); i++)
			if(!this.isOnPath(points.get(i),1))
			{
				this.setUndefined();
				return;
			}
		
	}
	

	protected void polishPointOnPath(GeoPointND PI){
		PI.updateCoords2D();
		double x=PI.getX2D();
		double y=PI.getY2D();
		
		double dx,dy;
		dx=evalDiffXPolyAt(x,y);
		dy=evalDiffYPolyAt(x,y);
		double d=Math.abs(dx)+Math.abs(dy);
		if (Kernel.isZero(d))
			return;
		dx/=d;
		dy/=d;
		double[] pair=new double[]{x,y};
		double[] line=new double[]{y*dx-x*dy,dy,-dx};
		if (PolynomialUtils.rootPolishing(pair, this, line)){
			PI.setCoords2D(pair[0], pair[1], 1);
		}
	}
	
	public void pointChanged(GeoPointND PI) {
		if (locus.getPoints().size()>0){
			locus.pointChanged(PI);
			polishPointOnPath(PI);
		}
	}

	public void pathChanged(GeoPointND PI) {
		if (locus.getPoints().size()>0){
			locus.pathChanged(PI);
			polishPointOnPath(PI);
		}
	}

	public boolean isOnPath(GeoPointND PI) {
		return isOnPath(PI, Kernel.STANDARD_PRECISION);
	}
	
	public boolean isOnPath(GeoPointND PI, double eps) {

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
	}

	public void mirror(GeoPoint Q) {
		plugInPoly(new double[][]{{2*Q.inhomX},{-1}},new double[][]{{2*Q.inhomY,-1}});
	}

	public void mirror(GeoLine g) {
		if (!g.isDefined()){
			setUndefined();
			return;
		}
		double[] dir=new double[2];
		g.getDirection(dir);
		double dx=dir[0];
		double dy=dir[1];
		double x=g.getStartPoint().inhomX;
		double y=g.getStartPoint().inhomY;
		double n=1/(dx*dx+dy*dy);
		plugInPoly(new double[][]{{2*n*dy*(x*dy-y*dx),2*n*dx*dy},{1-2*dy*dy*n,0}},new double[][]{{2*n*dx*(y*dx-x*dy),1-2*n*dx*dx},{2*n*dx*dy,0}});
		
	}

	public void translate(Coords v) {
		translate(v.getX(),v.getY());
	}
	
	public void translate(double vx,double vy){
		double a=-vx; //-translate in X-dir
		double b=-vy; //-translate in Y-dir
		plugInPoly(new double[][]{{a},{1}},new double[][]{{b,1}});
	}

	public void rotate(NumberValue phiValue) {
		double phi=phiValue.getDouble();
		double cos=Math.cos(phi);
		double sin=Math.sin(-phi);
		plugInPoly(new double[][]{{0,-sin},{cos,0}},new double[][]{{0,cos},{sin,0}});	
	}

	public void rotate(NumberValue phiValue, GeoPoint S) {
		double phi=phiValue.getDouble();
		double cos=Math.cos(phi);
		double sin=Math.sin(-phi);
		double x=S.getInhomX();
		double y=S.getInhomY();
		plugInPoly(new double[][]{{x*(1-cos)+y*sin,-sin},{cos,0}},new double[][]{{-x*sin+y*(1-cos),cos},{sin,0}});
	}

	public void dilate(NumberValue rval, GeoPoint S) {
		double r=1/rval.getDouble();
		plugInPoly(new double[][]{{(1-r)*S.getInhomX()},{r}},new double[][]{{(1-r)*S.getInhomY(),r}});
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
	 

		public boolean euclidianViewUpdate() {
			if (isDefined()){
				updatePath();
				return true;
			}
			return false;
		}
	 
		/**
		 * X and Y of the point on the curve next to the Right Down Corner of the View, for the Label
		 */
		private List<double[]> singularitiesCollection;
		private List<double[]> boundaryIntersectCollection;
		
		//Second Algorithm
		final public static double EPS=Kernel.EPSILON;
		
		/**
		 * @param x
		 * @return 0 if |x| &lt EPS, sgn(x) otherwise
		 */
		public int epsSignum(double x){
			if (x>EPS)
				return 1;
			if (x<-EPS)
				return -1;
			return 0;
		}
		
		private static class GridRect{
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
		
		private int gridWidth=30; //grid"Resolution" how many grids in one row
		private int gridHeight=30; //how many grids in one col
		
		private double scaleX;
		private double scaleY;
		
		private double minGap=0.001;
		
		/**
		 * updates the path inside the current view-rectangle
		 */
		public void updatePath(){
			double[] viewBounds=kernel.getViewBoundsForGeo(this);
			if (viewBounds[0]==Double.POSITIVE_INFINITY){ //no active View
				viewBounds=new double[]{-10,10,-10,10,10,10}; //get some value...
			}
			updatePath(viewBounds[0], viewBounds[2], viewBounds[1]-viewBounds[0],
					viewBounds[3]-viewBounds[2], 1./viewBounds[4]/viewBounds[5]);
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
			if (!calcPath) //important for helper curves, which aren't visible
				return;
			locus.clearPoints();
			singularitiesCollection=new ArrayList<double[]>();
			boundaryIntersectCollection=new ArrayList<double[]>();

			grid=new GridRect[gridWidth][gridHeight];
			remember=new boolean[gridWidth][gridHeight];

			double prec=5;
			scaleX=prec*Math.sqrt(resolution);
			scaleY=prec*Math.sqrt(resolution);

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

			double sx=x;
			double sy=y;
			double lx=Double.NaN; //no previous point
			double ly=Double.NaN;
			boolean first=true;

			double stepSize=START_STEP_SIZE*Math.max(scaleX, scaleY);
			double startX=x;
			double startY=y;

			ArrayList<MyPoint> firstDirPoints=new ArrayList<MyPoint>();
			firstDirPoints.add(new MyPoint(x,y,true));

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
				s++;
				boolean reachedSingularity=false;
				boolean reachedEnd=false;
				if (!Double.isNaN(lx)&&!Double.isNaN(ly)){
					if ((scaledNormSquared(startX-sx, startY-sy)<MAX_STEP_SIZE*MAX_STEP_SIZE)
						&& (scaledNormSquared(startX-sx,startY-sy)<scaledNormSquared(startX-lx,startY-ly))){
						/* loop found */
						if (firstDirPoints!=null){
							MyPoint firstPoint=firstDirPoints.get(0);
							firstPoint.lineTo=false;
							locus.getPoints().addAll(firstDirPoints);
						}
						locus.insertPoint(x, y, true);
						return;
					}
				}
				while (sx<grid[w][h].x){
					if (w>0)
						w--;
					else{
						reachedEnd=true;
						break;
					}
				}
				while (sx>grid[w][h].x+grid[w][h].width){
					if (w<grid.length-1)
						w++;
					else{
						reachedEnd=true;
						break;
					}
				}
				while (sy<grid[w][h].y){
					if (h>0)
						h--;
					else{
						reachedEnd=true;
						break;
					}
				}
				while (sy>grid[w][h].y+grid[w][h].height){
					if (h<grid[w].length-1)
						h++;
					else{
						reachedEnd=true;
						break;
					}
				}
				if (reachedEnd){ //we reached the boundary
					boundaryIntersectCollection.add(new double[]{sx,sy});
				}
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
						for (double[] pair:singularitiesCollection){ //check if this singularity is already known
							if ((scaledNormSquared(pair[0]-sx,pair[1]-sy)<SING_RADIUS*SING_RADIUS)){
								sx=pair[0];
								sy=pair[1];
								reachedSingularity=true;
								reachedEnd=true;
								break;
							}
						}
						if (!reachedEnd){
							if (gradX*gradX+gradY*gradY>lastGradX*lastGradX+lastGradY*lastGradY){ //going away from the singularity, stop here
								singularitiesCollection.add(new double[]{sx,sy});
								reachedEnd=true;
								reachedSingularity=true;
							}else if (Math.abs(gradX)<MIN_GRAD&&Math.abs(gradY)<MIN_GRAD){ //singularity
								singularitiesCollection.add(new double[]{sx,sy});
								reachedEnd=true;
								reachedSingularity=true;
							}
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
						}
					}
					lx=sx;
					ly=sy;
				}
				while(!reachedEnd){
					sx=lx+nX*stepSize; //go in "best" direction
					sy=ly+nY*stepSize;
					int e=epsSignum(evalPolyAt(sx,sy,true));
					if (e==0){
						if (stepSize*2<=MAX_STEP_SIZE*Math.max(scaleX, scaleY))
							stepSize*=2;
						break;
					}else{
						gradX=evalDiffXPolyAt(sx, sy,true);
						gradY=evalDiffYPolyAt(sx, sy,true);
						if (Math.abs(gradX)<MIN_GRAD&&Math.abs(gradY)<MIN_GRAD){ //singularity
							stepSize/=2;
							if (stepSize>MIN_STEP_SIZE*Math.max(scaleX, scaleY))
								continue;
							else{
								singularitiesCollection.add(new double[]{sx,sy});
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
								reachedEnd=true;
								break;
							}
						}
					}
				}
				if (!reachedEnd||reachedSingularity){
					if (reachedSingularity||((lx-sx)*(lx-sx)+(ly-sy)*(ly-sy)>minGap*minGap)){
						if (firstDirPoints!=null){
							firstDirPoints.add(new MyPoint(sx,sy,true));
						}else{
							locus.insertPoint(sx, sy, true);
						}
						stepCount++;
					}
				}
				if (reachedEnd){
					if (!first){
						return; //reached the end two times
					}
					lastGradX=Double.POSITIVE_INFINITY;
					lastGradY=Double.POSITIVE_INFINITY;

					/* we reached end for the first time and now save the points into the locus */
					ArrayList<MyPoint> pointList=locus.getMyPointList();
					if (firstDirPoints.size()>0){
						MyPoint lastPoint=firstDirPoints.get(firstDirPoints.size()-1);
						lastPoint.lineTo=false;
						pointList.ensureCapacity(pointList.size()+firstDirPoints.size());
						for (int i=firstDirPoints.size()-1;i>=0;i--){
							pointList.add(firstDirPoints.get(i));
						}
					}
					firstDirPoints=null;
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
				//solved #278 PRECISION to small (was Double.MIN_VALUE)
				while(a2-a1>Kernel.MAX_PRECISION){
					e=epsSignum(evalPolyAt(x1+(x2-x1)*(a2+a1)/2,y1+(y2-y1)*(a2+a1)/2,true));
					if (e==0){
						return (a2+a1)/2;
					}
					if (e==e1){
						a1=(a2+a1)/2;
					}else{
						a2=(a1+a2)/2;
					}
				}
				return (a1+a2)/2;
			}
			return Double.NaN;
		}


}

