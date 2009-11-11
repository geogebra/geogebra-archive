package geogebra3D.euclidian3D;

import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoVec2D;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoConic3D;

/**
 * @author ggb3D
 * 
 * Drawable for GeoConic3D
 *
 */
public class DrawConic3D extends Drawable3DCurves {
	
	
	
	// CIRCLE
	
	/** x coord of the circle center*/
	double xc;
	/** y coord of the circle center*/
	double yc;
	/** radius the circle*/
	double r;
	
	
	
	
	
	
	/**
	 * @param a_view3d the 3D view where the conic is drawn
	 * @param a_conic the 3D conic to draw
	 */
	public DrawConic3D(EuclidianView3D a_view3d, GeoConic3D a_conic) {
		super(a_view3d,a_conic);
	}

	
	
	private void drawGeometryForAll(Renderer renderer) {
		
		GeoConic3D conic = (GeoConic3D) getGeoElement();
		
		switch(conic.getType()){
		case GeoConic.CONIC_CIRCLE:
			renderer.drawCircle(xc,yc,r);
			break;
		default:
			break;
		
		}
	}
	
	

	public void drawGeometry(Renderer renderer) {
		
		renderer.setThickness(getGeoElement().getLineThickness());		
		drawGeometryForAll(renderer);

	}


	public void drawGeometryHidden(Renderer renderer) {
		renderer.setThickness(getGeoElement().getLineThickness());		
		drawGeometryForAll(renderer);

	}


	public void drawGeometryPicked(Renderer renderer) {
		
		renderer.setThickness(getGeoElement().getLineThickness());
		drawGeometryForAll(renderer);

	}


	
	
	protected void updateForItSelf(){
		
		
		GeoConic3D conic = (GeoConic3D) getGeoElement();
		
		switch(conic.getType()){
		case GeoConic.CONIC_CIRCLE:
			GeoVec2D center = conic.getTranslationVector();
			xc = center.x;
			yc = center.y;
			r = conic.getCircleRadius();
			break;
		default:
			break;
		
		}

	}
	
	
	protected void updateForView(){
		
	}
	
	
	
	public int getPickOrder() {
		return DRAW_PICK_ORDER_1D;
	}

}
