package geogebra3D.kernel3D;

import geogebra.Matrix.CoordMatrix4x4;
import geogebra.Matrix.Coords;
import geogebra.Matrix.Coords3D;
import geogebra.kernel.AlgoMacro;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoCurveCartesian;
import geogebra.kernel.GeoElement;
import geogebra.kernel.ParametricCurveDistanceFunction;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.kernelND.GeoCurveCartesianND;
import geogebra.main.Application;
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
	}
	
	/**
	 * 
	 * @param curve
	 */
	public GeoCurveCartesian3D(GeoCurveCartesian3D curve) {
		super(curve.cons);
		set(curve);
	}

	public Coords evaluateCurve(double t){
		Coords p = new Coords(3);
		for (int i=0;i<3;i++)
			p.set(i+1, fun[i].evaluate(t));
		
		return p;
	}
	
	public Coords evaluateTangent(double t){
		Coords v = new Coords(3);
		for (int i=0;i<3;i++)
			v.set(i+1, funD1[i].evaluate(t));
		
		return v.normalized();
		
	}
	
	public Coords3D evaluateCurve3D(double t){
		return new Coords3D(fun[0].evaluate(t),fun[1].evaluate(t),fun[2].evaluate(t),1);
	}
	
	public Coords3D evaluateTangent3D(double t){
		return new Coords3D( funD1[0].evaluate(t),funD1[1].evaluate(t),
								funD1[2].evaluate(t),1).normalize();
		
	}
	
	/**
	 * Returns the curvature at the specified point
	 * @param t
	 */
	public double evaluateCurvature(double t){
		Coords D1 = new Coords(3);
		Coords D2 = new Coords(3);

		for (int i=0;i<3;i++)
			D1.set(i+1, funD1[i].evaluate(t));
		
		for (int i=0;i<3;i++)
			D2.set(i+1, funD2[i].evaluate(t));
		
		//compute curvature using the formula k = |f'' x f'| / |f'|^3
		Coords cross = D1.crossProduct(D2);
		return cross.norm()/Math.pow(D1.norm(),3);
	}
	

	public GeoElement copy() {
		return new GeoCurveCartesian3D(this);
	}


	@Override
	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}


	public void set(GeoElement geo) {
		GeoCurveCartesian3D geoCurve = (GeoCurveCartesian3D) geo;				
		
		fun = new Function[3];
		for (int i=0; i<3; i++){
			fun[i] = new Function(geoCurve.fun[i], kernel);
			//Application.debug(fun[i].toString());
		}

		startParam = geoCurve.startParam;
		endParam = geoCurve.endParam;
		isDefined = geoCurve.isDefined;
		
		// macro OUTPUT
		if (geo.getConstruction() != cons && isAlgoMacroOutput()) {	
			if (!geo.isIndependent()) {				
				// this object is an output object of AlgoMacro
				// we need to check the references to all geos in its function's expression
				AlgoMacro algoMacro = (AlgoMacro) getParentAlgorithm();
				for (int i=0; i<3; i++)
					algoMacro.initFunction(fun[i]);
			}
		}
		
		//distFun = new ParametricCurveDistanceFunction(this);
		
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


	
	
	
	public String getClassName() {
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





	public CoordMatrix4x4 getDrawingMatrix() {
		return CoordMatrix4x4.Identity();
	}





	public GeoElement getGeoElement2D() {
		return null;
	}





	public Coords getLabelPosition(){
		return new Coords(4); //TODO
	}





	public Coords getMainDirection() {
		return null;
	}





	public boolean hasGeoElement2D() {
		return false;
	}







	public void setDrawable3D(Drawable3D d) {
	
		drawable3D = d;
		
	}





	public void setDrawingMatrix(CoordMatrix4x4 aDrawingMatrix) {
		
	}





	public void setGeoElement2D(GeoElement geo) {
		
	}





	

  	public boolean isGeoElement3D() {
		return true;
	}
	

}
