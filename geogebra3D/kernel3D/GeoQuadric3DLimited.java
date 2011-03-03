package geogebra3D.kernel3D;

import java.awt.Color;

import geogebra.Matrix.CoordMatrix;
import geogebra.Matrix.CoordSys;
import geogebra.Matrix.Coords;
import geogebra.kernel.AlgoJoinPointsSegment;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoQuadricND;
import geogebra.kernel.kernelND.GeoSegmentND;
import geogebra.main.Application;

/**
 * Class for limited quadrics (e.g. limited cones, cylinders, ...)
 * @author mathieu
 *
 */
public class GeoQuadric3DLimited extends GeoQuadricND {
	
	/** side of the quadric */
	private GeoQuadric3DPart side;
	/** bottom and top of the quadric */
	private GeoConic3D bottom, top;
	
	private GeoPointND bottomPoint, topPoint;
	
	private double min, max;
	

	/**
	 * constructor
	 * @param c
	 */
	public GeoQuadric3DLimited(Construction c) {
		this(c,null,null);
	}
	

	public GeoQuadric3DLimited(Construction c, GeoPointND bottomPoint, GeoPointND topPoint) {

		super(c,3);

		setPoints(bottomPoint, topPoint);
		
		
		//TODO merge with GeoQuadricND
		eigenvecND = new Coords[3];
		for (int i=0;i<3;i++){
			eigenvecND[i] = new Coords(4);
			eigenvecND[i].set(i+1,1);
		}
		
		//diagonal (diagonalized matrix)
		diagonal = new double[4];
		
		
		
		
	}
	
	public void setPoints(GeoPointND bottomPoint, GeoPointND topPoint){
		this.bottomPoint=bottomPoint;
		this.topPoint=topPoint;
	}
	
	
	public void setParts(){
				
		AlgoQuadricSide algo = new AlgoQuadricSide(cons, bottomPoint, topPoint, this);            
		cons.removeFromConstructionList(algo);
		side = (GeoQuadric3DPart) algo.getQuadric();
		
		AlgoQuadricEnds algo2 = new AlgoQuadricEnds(cons, this, bottomPoint, topPoint);
		cons.removeFromConstructionList(algo2);
		bottom = algo2.getSection1();
		top = algo2.getSection2();
	
	}
	
	
	public GeoQuadric3DLimited(GeoQuadric3DLimited quadric) {
		this(quadric.getConstruction());
		set(quadric);
	}
	
	
	
	public GeoConic3D getBottom(){
		return bottom;
	}
	
	public GeoConic3D getTop(){
		return top;
	}
	
	public GeoQuadric3DPart getSide(){
		return side;
	}
	
	public void updatePartsVisualStyle(){
		 setObjColor(getObjectColor()); 
		 setLineThickness(getLineThickness()); 
		 setAlphaValue(getAlphaValue());
		 setEuclidianVisible(isEuclidianVisible());
		 
	}
	
	

	/**
	 * inits the labels
	 * @param labels
	 */
	public void initLabels(String[] labels) {
		
		if (labels==null){
			setLabel(null);
			bottom.setLabel(null);
			top.setLabel(null);
			side.setLabel(null);
			return;
		}
		
		setLabel(labels[0]);
		
		if (labels.length<4){
			bottom.setLabel(null);
			top.setLabel(null);
			side.setLabel(null);
			return;
		}
		
		bottom.setLabel(labels[1]);
		top.setLabel(labels[2]);
		side.setLabel(labels[3]);
		
	}
	
	
	public double getMin(){
		return min;
	}
	
	public double getMax(){
		return max;
	}
	
	
	//TODO merge in GeoQuadricND
	/**
	 * @param origin 
	 * @param direction 
	 * @param r 
	 * @param min 
	 * @param max 
	 * 
	 */
	public void setCylinder(Coords origin, Coords direction, double r, double min, double max){

		// set center
		setMidpoint(origin.get());
		
		// set direction
		eigenvecND[2] = direction;
		

		// set others eigen vecs
		Coords[] ee = direction.completeOrthonormal();
		eigenvecND[0] = ee[0];
		eigenvecND[1] = ee[1];
		
		// set halfAxes = radius	
		for (int i=0;i<2;i++)
			halfAxes[i] = r;
		
		//limites
		this.min=min;
		this.max=max;
		
		// set the diagonal values
		diagonal[0] = 1;
		diagonal[1] = 1;
		diagonal[2] = 0;
		diagonal[3] = -r*r;
		
		// set matrix
		setMatrixFromEigen();
		
		//set type
		setType(QUADRIC_CYLINDER);
		
	}
	
	
	
	
	
	/////////////////////////
	// GEOELEMENT
	/////////////////////////
	

	public void setObjColor(Color color) {
		super.setObjColor(color);
		if (bottom==null)
			return;
		bottom.setObjColor(color);
		top.setObjColor(color);
		side.setObjColor(color);

	}



	/** to be able to fill it with an alpha value */
	public boolean isFillable() {
		return true;
	}
	


	public void setEuclidianVisible(boolean visible) {

		super.setEuclidianVisible(visible);
		bottom.setEuclidianVisible(visible);
		top.setEuclidianVisible(visible);
		side.setEuclidianVisible(visible);


	}  


	public void setLineType(int type) {
		super.setLineType(type);


		if (bottom==null)
			return;

		bottom.setLineType(type);
		top.setLineType(type);

	}


	public void setLineTypeHidden(int type) {
		super.setLineTypeHidden(type);


		if (bottom==null)
			return;

		bottom.setLineTypeHidden(type);
		top.setLineTypeHidden(type);
	}


	public void setLineThickness(int th) {
		super.setLineThickness(th);
		if (bottom==null)
			return;
		bottom.setLineThickness(th);
		top.setLineThickness(th);	
	}


	public void setAlphaValue(float alpha) {

		super.setAlphaValue(alpha);


		if (bottom==null)
			return;

		bottom.setAlphaValue(alpha);
		bottom.update();
		top.setAlphaValue(alpha);
		top.update();
		side.setAlphaValue(alpha);
		side.update();



	}


	public GeoElement copy() {
		return new GeoQuadric3DLimited(this);
	}


	public int getGeoClassType() {
		return GEO_CLASS_QUADRIC_LIMITED;
	}

	protected String getTypeString() {
		return side.getTypeString();
	}

	public boolean isDefined() {
		return true;//side.isDefined();
	}

	@Override
	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}

	public void set(GeoElement geo) {
		
		
		if (geo instanceof GeoQuadric3DLimited){
			GeoQuadric3DLimited quadric = (GeoQuadric3DLimited) geo;
			//setParts((GeoConic3D) quadric.bottom.copy(), (GeoConic3D) quadric.top.copy(), (GeoQuadric3DPart) quadric.side.copy()); 

			bottom.set(quadric.bottom);
			top.set(quadric.top);
			side.set(quadric.side);
		
		}
		
	}

	public void setUndefined() {
		
	}

	public boolean showInAlgebraView() {
		return true;
	}

	protected boolean showInEuclidianView() {
		return true;
	}


	public String getClassName() {
		return "GeoQuadricLimited";
	}


	/////////////////////////////////////
	// GEOQUADRICND
	/////////////////////////////////////
	
	
	private double volume;
	
	public void calcVolume(){
		
		//Application.debug("ici");
		
		switch(type){
		case QUADRIC_CYLINDER:
			volume=bottom.getHalfAxis(0)*bottom.getHalfAxis(0)*Math.PI*(max-min);
		//default:
		//	volume=Double.NaN;
		}
	}
	
	public double getVolume(){
		if (defined)
			return volume;				        
		else 
			return Double.NaN;			        	
	}	

	public String toValueString() {
		switch(type){
		case QUADRIC_CYLINDER:
			return kernel.format(volume);
		
		}
		
		return "todo-GeoQuadric3DLimited";
		
	}
	
	protected StringBuilder buildValueString() {
		return new StringBuilder(toValueString());
	}





	@Override
	public void setSphereND(GeoPointND M, GeoSegmentND segment) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setSphereND(GeoPointND M, GeoPointND P) {
		// TODO Auto-generated method stub
		
	}




}
