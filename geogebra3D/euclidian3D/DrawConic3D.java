package geogebra3D.euclidian3D;

import geogebra.Matrix.Coords;
import geogebra.kernel.GeoConic;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoConic3D;

/**
 * @author ggb3D
 * 
 * Drawable for GeoConic3D
 *
 */
public class DrawConic3D extends Drawable3DCurves {
	
	
	
	
	
	
	
	
	
	/**
	 * @param view3d the 3D view where the conic is drawn
	 * @param conic the conic to draw
	 */
	public DrawConic3D(EuclidianView3D view3d, GeoConicND conic) {
		super(view3d,conic);
	}

	
	
	
	

	public void drawGeometry(Renderer renderer) {
		
		GeoConicND conic = (GeoConicND) getGeoElement();
		
		switch(conic.getType()){
		case GeoConic.CONIC_CIRCLE:
			renderer.getGeometryManager().draw(getGeometryIndex());
			break;
		default:
			break;
		
		}

	}




	
	
	protected boolean updateForItSelf(){
		
		

    	setColors();
    	
		Renderer renderer = getView3D().getRenderer();
		
		
		
		GeoConicND conic = (GeoConicND) getGeoElement();
		
		switch(conic.getType()){
		case GeoConic.CONIC_CIRCLE:
			
			PlotterBrush brush = renderer.getGeometryManager().getBrush();
			
			brush.setThickness(getGeoElement().getLineThickness(),(float) getView3D().getScale());
			
			brush.start(8);
			//brush.setAffineTexture(0.5f, 0.125f);
			
			Coords center = conic.getMidpoint3D();
			

			
			brush.circle(center, conic.getEigenvec3D(0), conic.getEigenvec3D(1), conic.getHalfAxis(0));
			
			setGeometryIndex(brush.end());
			
			
			break;
		default:
			break;
		
		}

		return true;
	}
	
	
	protected void updateForView(){
		
		updateForItSelf();
		
	}
	
	
	
	public int getPickOrder() {
		return DRAW_PICK_ORDER_1D;
	}

}
