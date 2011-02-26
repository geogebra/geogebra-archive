package geogebra3D.kernel3D;

import java.awt.Color;

import geogebra.Matrix.CoordSys;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.kernelND.GeoSegmentND;
import geogebra.main.Application;

/**
 * Class for limited quadrics (e.g. limited cones, cylinders, ...)
 * @author mathieu
 *
 */
public class GeoQuadric3DLimited extends GeoElement {
	
	/** side of the quadric */
	private GeoQuadric3DPart side;
	/** bottom and top of the quadric */
	private GeoConic3D bottom, top;
	

	/**
	 * constructor
	 * @param c
	 */
	public GeoQuadric3DLimited(Construction c) {
		super(c);
		
		side=new GeoQuadric3DPart(c);
		bottom=new GeoConic3D(c);
		top=new GeoConic3D(c);
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
		return side.isDefined();
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

	public String toValueString() {
		return "todo-GeoQuadric3DLimited";
	}

	public String getClassName() {
		return "GeoQuadricLimited";
	}


}
