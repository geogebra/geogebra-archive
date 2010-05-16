package geogebra3D.kernel3D;

import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoCurveCartesianND;
import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.Function;
import geogebra3D.euclidian3D.Drawable3D;

/**
 * Class for cartesian curves in 3D
 * 
 * @author matthieu
 *
 */
public class GeoCurveCartesian3D extends GeoCurveCartesianND
implements GeoCurveCartesian3DInterface, GeoElement3DInterface{

	
	

	/** link with drawable3D */
	private Drawable3D drawable3D = null;
	
	/** empty constructor (for ConstructionDefaults3D)
	 * @param c
	 */
	public GeoCurveCartesian3D(Construction c){
		super(c);
	}
	
	/** common constructor
	 * @param c
	 * @param fun
	 */
	public GeoCurveCartesian3D(Construction c, Function fun[]) {
		super(c, fun);
		
		/*
		Application.debug(evaluateCurve(getMinParameter()).toString()+"\n"+evaluateTangent(getMinParameter()).toString());
		Application.debug(evaluateCurve(0).toString()+"\n"+evaluateTangent(0).toString());
		Application.debug(evaluateCurve(getMaxParameter()).toString()+"\n"+evaluateTangent(getMaxParameter()).toString());
		*/
	}

	
	
	
	
	public GgbVector evaluateCurve(double t){
		
		//return new GgbVector(new double[] {t,0,t*t});
		GgbVector p = new GgbVector(3);
		for (int i=0;i<3;i++)
			p.set(i+1, fun[i].evaluate(t));
		
		return p;
	}
	
	public GgbVector evaluateTangent(double t){
		
		//return new GgbVector(new double[] {1,0,2*t}).normalized();
		GgbVector v = new GgbVector(3);
		for (int i=0;i<3;i++)
			v.set(i+1, funD1[i].evaluate(t));
		
		return v.normalized();
		
	}
	
	
	
	
	
	
	
	
	@Override
	public GeoElement copy() {
		// TODO Auto-generated method stub
		return null;
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
	public boolean showInAlgebraView() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return true;
	}


	
	
	
	protected String getClassName() {
		return "GeoCurveCartesian3D";
	}        
	
    protected String getTypeString() {
		return "CurveCartesian3D";
	}
    
    public int getGeoClassType() {
    	return GeoElement3D.GEO_CLASS_CURVECARTESIAN3D; 
    }




    
    
    
    
    
    
    
    
    

	public boolean isVector3DValue() {
		return false;
	}





	public Drawable3D getDrawable3D() {
		
		return drawable3D;
	}





	public GgbMatrix4x4 getDrawingMatrix() {
		return GgbMatrix4x4.Identity();
	}





	public GeoElement getGeoElement2D() {
		return null;
	}





	public GgbMatrix4x4 getLabelMatrix() {
		return GgbMatrix4x4.Identity();
	}





	public GgbVector getViewDirection() {
		return null;
	}





	public boolean hasGeoElement2D() {
		return false;
	}





	public boolean isPickable() {
		return true;
	}





	public void setDrawable3D(Drawable3D d) {
	
		drawable3D = d;
		
	}





	public void setDrawingMatrix(GgbMatrix4x4 aDrawingMatrix) {
		
	}





	public void setGeoElement2D(GeoElement geo) {
		
	}





	public void setIsPickable(boolean v) {
		
	}
	

  	public boolean isGeoElement3D() {
		return true;
	}
	

}
