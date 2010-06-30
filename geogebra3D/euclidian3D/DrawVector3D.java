package geogebra3D.euclidian3D;


import geogebra.Matrix.GgbVector;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoVector3D;

/**
 * Class for drawing vectors
 * @author matthieu
 *
 */
public class DrawVector3D extends Drawable3DCurves {

	/** gl index of the geometry */
	private int geometryIndex = -1;
	
	/**
	 * Common constructor
	 * @param a_view3D
	 * @param a_vector3D
	 */
	public DrawVector3D(EuclidianView3D a_view3D, GeoVector3D a_vector3D)
	{
		
		super(a_view3D, a_vector3D);
	}
	
	/////////////////////////////////////////
	// DRAWING GEOMETRIES
	
	
	public void drawGeometry(Renderer renderer) {
		double t = getGeoElement().getLineThickness();
		renderer.setThickness(t);
		renderer.setArrowType(Renderer.ARROW_TYPE_SIMPLE);
		renderer.setArrowLength(10*t);
		renderer.setArrowWidth(5*t);
		

		//renderer.drawSegment();
		renderer.getGeometryManager().draw(geometryIndex);
		
		renderer.setArrowType(Renderer.ARROW_TYPE_NONE);		
	}


	
	protected void updateForItSelf(){

		
		GeoVector3D geo = ((GeoVector3D) getGeoElement());
		
		geo.updateStartPointPosition();

		Renderer renderer = getView3D().getRenderer();

		renderer.getGeometryManager().remove(geometryIndex);
		
		GgbVector p1;
		if (geo.getStartPoint()==null){
			p1 = new GgbVector(4);
			p1.setW(1);
		}else
			p1 = geo.getStartPoint().getInhomCoords();
		GgbVector p2 = (GgbVector) p1.add(geo.getCoords());
		
		PlotterBrush brush = renderer.getGeometryManager().getBrush();

		brush.setArrowType(PlotterBrush.ARROW_TYPE_SIMPLE);
		brush.setThickness(getGeoElement().getLineThickness(),(float) getView3D().getScale());
		
		brush.start(8);
		brush.setAffineTexture(0.5f, 0.125f);
		brush.segment(p1,p2);
		brush.setArrowType(PlotterBrush.ARROW_TYPE_NONE);
		geometryIndex = brush.end();
		
		
	}
	
	protected void updateForView(){
		updateForItSelf();
	}
	
	
	
	
	
	public int getPickOrder() {		
		return DRAW_PICK_ORDER_1D;
	}

	
		
		
		

}
