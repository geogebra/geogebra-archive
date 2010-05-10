package geogebra3D.kernel3D;

import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;

/** Class describing function of 2 variables
 * @author matthieu
 *
 */
public class GeoFunction2Var extends GeoElement3D
implements GeoFunction2VarInterface {
	
	
	// to remove
	private int type;
	private double coeff;
	
	
	private double[] start, end;

	/** default constructor
	 * @param c
	 */
	public GeoFunction2Var(Construction c) {
		super(c);
		
		start = new double[2];
		end = new double[2];
		
	}
	
	
	
	/////////////////////////////////////////
	// SURFACE (u,v)->(x,y,z) INTERFACE
	/////////////////////////////////////////
	
	public GgbVector evaluatePoint(double u, double v){
		
		switch (type){
		case 1:
		default:
			return new GgbVector(new double[] {-u,v,(u*u+v*v)*coeff}); // -u for surface orientation
		case 2:
			return new GgbVector(new double[] {u,v,coeff*u*v});
		}

	}
	

	public GgbVector evaluateNormal(double u, double v){
		
		
		switch (type){
		case 1:
		default:
			return (new GgbVector(new double[] {-2*u*coeff,2*v*coeff,-1})).normalized();
		case 2:
			return (new GgbVector(new double[] {-coeff*v,-coeff*u,1})).normalized();
		}
		
		
	}
	
	
	
	
	
	/**
	 * Returns the start parameter value 
	 * @return
	 */
	public double getMinParameter(int index) {
		
		/*
		switch (type){
		case 1:
		default:
			return -5;
		case 2:
			return -2;
		}
		*/
		
		return start[index];
		
	}
	
	/**
	 * Returns the largest possible parameter value 
	 * @return
	 */
	public double getMaxParameter(int index) {
		
		/*
		switch (type){
		case 1:
		default:
			return 5;
		case 2:
			return 2;
		}
		*/
		
		return end[index];
	}
	
	
	/** 
	 * Sets the start and end parameter value of this curve.
	 * @param startParam 
	 * @param endParam 
	 */
	public void setInterval(double startU, double endU, double startV, double endV) {
		
		start[0] = startU; end[0] = endU;
		start[1] = startV; end[1] = endV;
		
	}
	
	
	/////////////////////////////////////////
	// TO REMOVE (TEST)
	/////////////////////////////////////////

	@Deprecated
	public void set(int type, double coeff){
		this.type = type;
		this.coeff = coeff;
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
