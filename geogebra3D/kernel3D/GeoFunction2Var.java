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

	/** default constructor
	 * @param c
	 */
	public GeoFunction2Var(Construction c) {
		super(c);
		
	}
	
	
	
	/////////////////////////////////////////
	// SURFACE (u,v)->(x,y,z) INTERFACE
	/////////////////////////////////////////
	
	public GgbVector evaluatePoint(double u, double v){
		
		return new GgbVector(new double[] {-u,v,u*u+v*v}); // -u for surface orientation
		//return new GgbVector(new double[] {u,v,u*v});
	}
	

	public GgbVector evaluateNormal(double u, double v){
		
		return (new GgbVector(new double[] {-2*u,2*v,-1})).normalized();
		//return (new GgbVector(new double[] {v,u,-1})).normalized();
	}
	
	
	
	
	
	
	

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
