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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.DecompositionSolver;
import org.apache.commons.math.linear.InvalidMatrixException;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.MatrixIndexException;
import org.apache.commons.math.linear.MatrixVisitorException;
import org.apache.commons.math.linear.NonSquareMatrixException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealMatrixChangingVisitor;
import org.apache.commons.math.linear.RealMatrixImpl;
import org.apache.commons.math.linear.RealMatrixPreservingVisitor;
import org.apache.commons.math.linear.RealVector;

import edu.jas.arith.BigRational;
import edu.jas.poly.ExpVector;
import edu.jas.poly.ExpVectorLong;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.util.ArrayUtil;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.Polynomial;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;
import geogebra.main.MyError;

/**
 * Represents implicit bivariat polynomial equations, with degree greater than 2.
 */
public class GeoImplicitPoly extends GeoUserInputElement 
implements Path, Traceable, Mirrorable, ConicMirrorable
{

	private double[][] coeff;
	private double[][] coeffSquarefree;
	private int degX;
	private int degY;
	
	private static GenPolynomialRing<BigRational> CoeffRing;

	
	private boolean defined = true;
	private boolean isConstant;
	
	private boolean trace; //for traceable interface
	
//	private Thread factorThread;
	
	private GenPolynomial<BigRational> genPoly;

	protected GeoImplicitPoly(Construction c) {
		super(c);
		degX=-1;
		degY=-1;
		coeffSquarefree=null;
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
		Application.debug("Conic -> "+this);
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
		this.defined=geo.isDefined();
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

	private void addPow(StringBuilder sb, int i){
		if (i>1){
			sb.append('^');
			if (kernel.getCASPrintForm()==ExpressionNode.STRING_TYPE_LATEX){
				sb.append('{');
				sb.append(i);
				sb.append('}');
			}else
				sb.append(i);
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
					if (coeff[i][j]!=0){
						if (!first)
							sb.append((coeff[i][j]>0?"+":""));
						if (coeff[i][j]!=1){
							if (coeff[i][j]==-1){
								sb.append("-");
							}else{
								sb.append(kernel.format(coeff[i][j]));
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
		genPoly=null;
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
			genPoly=null;
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
					isConstant=isConstant&&(coeff[i][j]==0||(i==0&&j==0));
				}
			}
			getFactors();
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
		double sum=0;
		double zs=0;
		//Evaluating Poly via the Horner-scheme
		double[][] coeff=getCoeff(squarefree);
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
	
	public boolean isConstant() {
		return isConstant;
	}
	
	public int getDegX() {
		return degX;
	}

	public int getDegY() {
		return degY;
	}
	
	
	
		
	/**
	 * @return the coefficient ring used by toGenPolynomial()
	 */
	public static GenPolynomialRing<BigRational> getCoeffRing() {
		if (CoeffRing==null){
	    	String[] vars={"y","x"};
			CoeffRing=new GenPolynomialRing<BigRational>(new BigRational(0),vars.length,new TermOrder(TermOrder.REVILEX),vars);
		}
		return CoeffRing;
	}

	/**
	 * @return representation of this polynomial as GenPolynomial<BigRational>
	 */
	public synchronized GenPolynomial<BigRational> toGenPolynomial(){
		if (genPoly==null){
			GenPolynomial<BigRational> gp=new GenPolynomial<BigRational>(getCoeffRing());
			for (int i=0;i<coeff.length;i++){
				for (int j=0;j<coeff[i].length;j++){
					if (coeff[i][j]!=0){
						BigRational b=toRational(coeff[i][j]);
						gp=gp.sum(b, new ExpVectorLong(new long[]{i,j}));
						//Application.debug(coeff[i][j]+" = "+toRational(coeff[i][j]));
					}
				}
			}
			genPoly=gp;
			return genPoly;
		}
		else
			return genPoly;
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
	
	public void setNearestPointOnCurve(GeoPointND PI){
//		GeoLine g=new GeoLine(cons);
//		PI.updateCoords2D();
//		double x=PI.getX2D();
//		double y=PI.getY2D();
//		double dy=-evalDiffXPolyAt(x, y);
//		double dx=evalDiffYPolyAt(x, y);
//		double c=x*dx+y*dy;
//		g.setCoords(dx, dy, -c);
////		Application.debug("a = "+dx+"; b = "+dy+"; c="+-c);
//		Application.debug("g = "+g);
//		AlgoIntersect algo=new AlgoIntersectImplicitpolyParametric(cons, this,g);
//		cons.removeFromConstructionList(algo);
//		algo.compute();
//		GeoPoint[] points=algo.getIntersectionPoints();
//		Application.debug("points = "+Arrays.deepToString(points));
//		double dist=Double.POSITIVE_INFINITY;
//		int k=-1;
//		for (int i=0;i<points.length;i++){
//			if (dist>(points[i].x-x)*(points[i].x-x)+(points[i].y-y)*(points[i].y-y)){
//				dist=(points[i].x-x)*(points[i].x-x)+(points[i].y-y)*(points[i].y-y);
//				k=i;
//			}
//		}
//		if (k>=0){
//			GeoPoint p=((GeoPoint) PI);
//			p.x=points[k].x;
//			p.y=points[k].y;
//			p.z=points[k].z;
//		}
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
	 * make curve through given points
	 * @param points ArrayList of points
	 */
	public void throughPoints(ArrayList<GeoPoint> points)
	{
		if((int)Math.sqrt(9+8*points.size()) != Math.sqrt(9+8*points.size()))
		{
			// throw "Inadequate number of points"
		}
		
		int degree = (int)(0.5*Math.sqrt(8*(1+points.size()))) - 1;
		
		RealMatrix extendMatrix = new RealMatrixImpl(points.size(), points.size()+1);
		RealMatrix matrix = new RealMatrixImpl(points.size(), points.size());
		
		DecompositionSolver solver;
		
		double [] matrixRow = new double[points.size()+1];
		double [] results = new double[points.size()];
		
		for(int i=0; i<points.size(); i++)
		{
			double x = points.get(i).x;
			double y = points.get(i).y;
			
			for(int j=0, m=0; j<degree+1; j++)
				for(int k=0; j+k != degree+1; k++)
					matrixRow[m++] = Math.pow(x, j)*Math.pow(y, k);
			extendMatrix.setRow(i, matrixRow);
		}
		
		int solutionColumn = 0;
		do 
		{
			results = extendMatrix.getColumn(solutionColumn);
			
			for(int i=0, j=0; i<points.size();i++)
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
		
		double [][] coeffMatrix = new double[degree+1][degree+1];
		
		
		for(int i=0; i<partialSolution.length; i++)
			if(Kernel.isZero(partialSolution[i]))
				partialSolution[i] = 0;
		
		for(int i=0, k=0; i<degree+1; i++)
			for(int j=0; j+i != degree+1; j++)
				if(k==solutionColumn-1)
					coeffMatrix[i][j] = 1;
				else
					coeffMatrix[i][j] = partialSolution[k++];
		
		this.setCoeff(coeffMatrix);
	}
	
	

	public void pointChanged(GeoPointND PI) {
		Application.debug("point changed="+PI);
		setNearestPointOnCurve(PI);
	}

	public void pathChanged(GeoPointND PI) {
		Application.debug("path changed="+PI);
		setNearestPointOnCurve(PI);
	}

	public boolean isOnPath(GeoPointND PI, double eps) {
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
		
		return Kernel.isZero(value);
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
	
	
	
	//static - adapter methods for use with JAS
	
	public static BigRational toRational(double d){
		int s=(int)Math.signum(d);
		d=Math.abs(d);
		long p=(int)Math.floor(d);
		long p1=1;
		long q=1;
		long q1=0;
		double res=1/(d-p);
		while(Math.abs(p/(double)q-d)>Kernel.STANDARD_PRECISION&&(q1==0||Math.abs(p1/(double)q1-d)>Math.abs(p/(double)q-d))){
			long b=(int)Math.floor(res);
			long t=p;
			p=b*p+p1;
			p1=t;
			t=q;
			q=b*q+q1;
			q1=t;
			res=1/(res-b);
		}
		return new BigRational(p*s,q);
	}
	
	public static double toDouble(BigRational b) {
		return b.numerator().doubleValue()/b.denominator().doubleValue();
	}
	
	/**
	 * 
	 * @param G List of GenPolynomial
	 * @param varN connected to p.ring.nvar; if varN[i] set, p univariat in x_i is fine with p in G
	 * @return p univariat in x_i, with varN[i]==true converted to PolynomialFunction, else null
	 * 			varN[i] remains true, all other set to false
	 */
	public static PolynomialFunction getUnivariatPoly(List<GenPolynomial<BigRational>> G,boolean[] varN){	
		Iterator<GenPolynomial<BigRational>> it=G.iterator();
    	while(it.hasNext()){
    		PolynomialFunction pf=getUnivariatPoly(it.next(),varN);
    		if (pf!=null)
    			return pf;
    	}
    	for (int i=0;i<varN.length;i++){
			varN[i]=false;
		}
    	return null;
	}
    
	/**
	 * 
	 * @param p GenPolynomial
	 * @param varN connected to p.ring.nvar; if varN[i] set, p univariat in x_i is fine
	 * @return p univariat in x_i, with varN[i]==true converted to PolynomialFunction, else null<br>
	 * 			varN[i] remains true, all other set to false
	 */
    public static PolynomialFunction getUnivariatPoly(GenPolynomial<BigRational> p,boolean[] varN){	
		ExpVector e=p.degreeVector();
		double[] coeff;
		if (varN.length!=p.ring.nvar){
			Application.debug("Length of varN doesn't match ring.nvar");
			return null;
		}
		int k=-1;
		for (int i=0;i<varN.length;i++){
			if (e.getVal(i)!=0){
				if (k>=0){
//					Application.debug("not univariat: "+p);
					return null;
				}else{
					k=i;
				}
			}
		}
		for (int i=0;i<varN.length&&k<0;i++) //if k<0 => constant => 'univariat' in every var, take first one allowed
			if (varN[i])
				k=i;
		if (k<0){
			return null;
		}
//		Application.debug("univar-poly = "+p);
		if (!varN[k]){
//			Application.debug("univariat in wrong var: "+p);
			return null;
		}
		for (int i=0;i<varN.length;i++){
			varN[i]=i==k;
		}
		ExpVector dir=ExpVector.create(varN.length, k, 1);
		coeff=new double[(int) e.getVal(k)+1];
		for (int i=coeff.length-1;i>=0;i--){
			BigRational b=p.coefficient(e);
			coeff[i]=b.numerator().doubleValue()/b.denominator().doubleValue();
			e=e.subtract(dir);
		}
		return new PolynomialFunction(coeff);
	}
    
    /**
     * GenPolynomial polynomial derivative k variable.<br/>
     * extended from PolyUtil
     * @param <C> coefficient type.
     * @param P GenPolynomial.
     * @return deriviative(P).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<C> 
           baseDeriviative( GenPolynomial<C> P, int k ) {
        if ( P == null || P.isZERO() ) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if ( pfac.nvar <= k ) { 
           // baseContent not possible by return type
           throw new RuntimeException(P.getClass().getName()
                     + " k to big ");
        }
        RingFactory<C> rf = pfac.coFac;
//        GenPolynomial<C> d = pfac.getZERO().clone();
       // Map<ExpVector,C> dm = d.getMap();
     //   SortedMap<ExpVector,C> dm = new SortedMap<ExpVector,C>();
        GenPolynomial<C> d=new GenPolynomial<C>(pfac);
        for ( Map.Entry<ExpVector,C> m : P.getMap().entrySet() ) {
            ExpVector f = m.getKey();  
            long fl = f.getVal(k);
            if ( fl > 0 ) {
               C cf = rf.fromInteger( fl );
               C a = m.getValue(); 
               C x = a.multiply(cf);
               if ( x != null && !x.isZERO() ) {
                  ExpVector e =f.subtract(ExpVector.create( f.length(), k, 1L) );  
                  d=d.sum(x, e);
               }
            }
        }
        return d; 
    }
    
    public static boolean isZero(GenPolynomial<BigRational> p,double eps){
    	Iterator<BigRational> it=p.coefficientIterator();
		while(it.hasNext()){
			if (Math.abs(GeoImplicitPoly.toDouble(it.next()))>=eps)
				return false;
		}
		return true;
    }

    public static GenPolynomial<BigRational> evalGenPolySimple(GenPolynomial<BigRational> p,BigRational a, int k){
    	GenPolynomial<BigRational> ep=new GenPolynomial<BigRational>(p.ring);
    	BigRational[] powers=new BigRational[(int)p.degreeVector().getVal(k)+1];
    	powers[0]=BigRational.ONE;
    	for (int i=1;i<powers.length;i++){
    		powers[i]=powers[i-1].multiply(a);
    	}
    	for ( Map.Entry<ExpVector,BigRational> m : p.getMap().entrySet() ) {
            ExpVector f = m.getKey();  
            long fl = f.getVal(k);
            ep=ep.sum(powers[(int)fl].multiply(m.getValue()), f.subtract(ExpVector.create(p.ring.nvar, k, fl)));
        }
    	return ep;
    }
    
    public double[][] getCoeff(GenPolynomial<BigRational> p){
    	ExpVector deg=p.degreeVector();
    	int xVal=0;
    	int yVal=1;
    	double[][] c=new double[(int) deg.getVal(xVal)+1][(int) deg.getVal(yVal)+1];
    	for (int i=0;i<c.length;i++){
    		for (int j=0;j<c[i].length;j++){
    			c[i][j]=0;
    		}
    	}
    	Iterator<ExpVector> it=p.exponentIterator();
    	while(it.hasNext()){
    		ExpVector e=it.next();
    		c[(int) e.getVal(xVal)][(int) e.getVal(yVal)]=toDouble(p.coefficient(e));
    	}
    	return c;
    }

	public void mirror(GeoConic c) 
	{
		
		if(c.getType() != GeoConic.CONIC_CIRCLE)
		{
			setUndefined();
			return;
		}
		
		if(degX != 2 || degY != 2)
		{
			setUndefined();
			return;
		}
		
		
		GeoConic gc = new GeoConic(cons);
		this.toGeoConic(gc);	
		
		if(gc.type == GeoConic.CONIC_CIRCLE)
		{ 
			gc.mirror(c);
			this.fromGeoConic(gc);
			return;
		}
		
		GeoPoint centerOfInversion = new GeoPoint(cons, "", c.getMidpoint().getX(), c.getMidpoint().getY(), 1);
		double distance = gc.distance(centerOfInversion);
		centerOfInversion.remove();

		ArrayList<GeoPoint> pointsOnConic;    	
    	
		// center of circle inversion is on conic
		if(Math.abs(distance) < Kernel.STANDARD_PRECISION)
		{
			pointsOnConic = gc.getPointsOnConic(10);
			for(int i=0; i<pointsOnConic.size(); i++)
				pointsOnConic.get(i).remove();
			
			for(int i=0; i<pointsOnConic.size(); i++)
			{
				pointsOnConic.get(i).mirror(c);
				if(pointsOnConic.get(i).isInfinite())
					pointsOnConic.remove(i);
			}
			
			if(pointsOnConic.size() > 9)
				pointsOnConic.remove(9);
		
			this.throughPoints(pointsOnConic);
		}
		else
		{
			pointsOnConic = gc.getPointsOnConic(15);
			for(int i=0; i<pointsOnConic.size(); i++)
				pointsOnConic.get(i).remove();
			for(int i=0; i<pointsOnConic.size(); i++)
			{
				pointsOnConic.get(i).mirror(c);
				if(pointsOnConic.get(i).isInfinite())
					pointsOnConic.remove(i);
			}
			
			if(pointsOnConic.size() > 14)
				pointsOnConic.remove(14);
		
			this.throughPoints(pointsOnConic);
		}
			
	}

	public void mirror(GeoPoint Q) {
		// TODO Auto-generated method stub
		
	}

	public void mirror(GeoLine g) {
		// TODO Auto-generated method stub
		
	}

	
}
