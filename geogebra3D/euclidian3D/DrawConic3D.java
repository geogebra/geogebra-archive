package geogebra3D.euclidian3D;

import geogebra.Matrix.GgbVector;
import geogebra.kernel.GeoConic;
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
	 * @param a_view3d the 3D view where the conic is drawn
	 * @param a_conic the 3D conic to draw
	 */
	public DrawConic3D(EuclidianView3D a_view3d, GeoConic3D a_conic) {
		super(a_view3d,a_conic);
	}

	
	
	
	

	public void drawGeometry(Renderer renderer) {
		
		GeoConic3D conic = (GeoConic3D) getGeoElement();
		
		switch(conic.getType()){
		case GeoConic.CONIC_CIRCLE:
			//renderer.drawCircle(xc,yc,r);
			renderer.getGeometryManager().draw(getGeometryIndex());
			break;
		default:
			break;
		
		}

	}




	
	
	protected boolean updateForItSelf(){
		
		

    	setColors();
    	
		Renderer renderer = getView3D().getRenderer();
		
		
		
		GeoConic3D conic = (GeoConic3D) getGeoElement();
		
		switch(conic.getType()){
		case GeoConic.CONIC_CIRCLE:
			
			PlotterBrush brush = renderer.getGeometryManager().getBrush();
			
			brush.setThickness(getGeoElement().getLineThickness(),(float) getView3D().getScale());
			
			brush.start(8);
			//brush.setAffineTexture(0.5f, 0.125f);
			
			GgbVector center = conic.getMidpoint();
			

			
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
