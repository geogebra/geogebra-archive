package geogebra3D.kernel3D;

import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.Functional2Var;
import geogebra.main.Application;

/** Class describing function of 2 variables
 * @author matthieu
 *
 */
public class GeoFunction2Var extends GeoElement3D
implements Functional2Var {
	
	protected FunctionNVar[] fun;
	protected FunctionNVar[] funD1;
	
	
	private double[] from, to;

	/** default constructor
	 * @param c
	 */
	public GeoFunction2Var(Construction c) {
		super(c);
	}
	
	public GeoFunction2Var(Construction c, FunctionNVar[] fun) {
		this(c);
		
		this.fun = fun;
		
		funD1 = new FunctionNVar[2];
		for (int i=0;i<2;i++){
			funD1[i] = fun[0].derivative(i, 1);
			/*
			Application.debug("funD1["+i+"]:"+funD1[i].toString()
					+"\nvar[0]="+funD1[i].getVarString(0)
					+"\nvar[1]="+funD1[i].getVarString(1)
					+"\nat(0,0):"+funD1[i].evaluate(new double[] {0,0})
					+"\nat(1,0):"+funD1[i].evaluate(new double[] {1,0})
					+"\nat(0,1):"+funD1[i].evaluate(new double[] {0,1})
					
			);
			*/
		}
		
		from = new double[2];
		to = new double[2];
		
	}
	
	
	
	/////////////////////////////////////////
	// SURFACE (u,v)->(x,y,z) INTERFACE
	/////////////////////////////////////////
	
	public GgbVector evaluatePoint(double u, double v){
		
		GgbVector p = new GgbVector(3);
		p.set(1, u);
		p.set(2, v);
		p.set(3, fun[0].evaluate(new double[] {u,v}));
		
		return p;

	}


	public GgbVector evaluateNormal(double u, double v){


		
		GgbVector vec = new GgbVector(
				-funD1[0].evaluate(new double[] {u,v}),
				-funD1[1].evaluate(new double[] {u,v}),
				1,
				0).normalized();
	
		//Application.debug("vec=\n"+vec.toString());
	
		return vec;
		
		//return new GgbVector(0,0,1,0);
	}


	
	
	
	
	
	
	public double getMinParameter(int index) {
		
		return from[index];
		
	}
	

	public double getMaxParameter(int index) {
		
		return to[index];
	}
	
	
	/** 
	 * Sets the start and end parameter value of this curve.
	 * @param startParam 
	 * @param endParam 
	 */
	public void setInterval(double startU, double endU, double startV, double endV) {
		
		from[0] = startU; to[0] = endU;
		from[1] = startV; to[1] = endV;
		
	}
	
	
	
	
	
	/////////////////////////////////////////
	// 
	/////////////////////////////////////////
	
	

	@Override
	public GeoElement copy() {
		// TODO Auto-generated method stub
		return null;
	}


	
	
	public boolean isDefined() {
		return true;
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
		return true;
	}

	@Override
	public String toValueString() {
		// TODO Auto-generated method stub
		return null;
	}


	
	
	protected String getClassName() {
		return "GeoFunction2Var";
	}        
	
    protected String getTypeString() {
		return "Function2Var";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_FUNCTION2VAR; 
    }
    
	/** to be able to fill it with an alpha value */
	public boolean isFillable() {
		return true;
	}

}
