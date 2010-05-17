package geogebra3D.euclidian3D;

import geogebra.Matrix.GgbVector;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoVec2D;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.Brush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoConic3D;
import geogebra3D.kernel3D.GeoCurveCartesian3D;

/**
 * @author ggb3D
 * 
 * Drawable for GeoCurveCartesian3D
 *
 */
public class DrawCurve3D extends Drawable3DCurves {
	
	
	/** gl index of the quadric */
	private int geometryIndex = -1;
	
	
	
	
	
	
	
	/**
	 * @param a_view3d the 3D view where the curve is drawn
	 * @param curve the 3D curve to draw
	 */
	public DrawCurve3D(EuclidianView3D a_view3d, GeoCurveCartesian3D curve) {
		super(a_view3d,curve);
	}

	
	
	
	

	public void drawGeometry(Renderer renderer) {
		
		renderer.setThickness(getGeoElement().getLineThickness());			
				

		renderer.getGeometryManager().draw(geometryIndex);
		
	}




	
	
	protected void updateForItSelf(){
		
		
		Renderer renderer = getView3D().getRenderer();
		
		renderer.getGeometryManager().remove(geometryIndex);
		
		if (!getGeoElement().isEuclidianVisible() || !getGeoElement().isDefined()){
			geometryIndex = -1;
			return;
		}


		GeoCurveCartesian3D curve = (GeoCurveCartesian3D) getGeoElement();
		

		Brush brush = renderer.getGeometryManager().getBrush();

		brush.setThickness(getGeoElement().getLineThickness(),(float) getView3D().getScale());

		brush.start(8);
		
		/*
		Application.debug(curve.evaluateCurve(curve.getMinParameter()).toString()+"\n"+curve.evaluateTangent(curve.getMinParameter()).toString());
		Application.debug(curve.evaluateCurve(0).toString()+"\n"+curve.evaluateTangent(0).toString());
		Application.debug(curve.evaluateCurve(curve.getMaxParameter()).toString()+"\n"+curve.evaluateTangent(curve.getMaxParameter()).toString());
		 */


		brush.setT((float) curve.getMinParameter(), (float) curve.getMaxParameter());
		brush.setDelta(0.1f);
		brush.draw(curve);



		geometryIndex = brush.end();

			
		
		

	}
	
	
	protected void updateForView(){
		
		updateForItSelf();
		
	}
	
	
	
	public int getPickOrder() {
		return DRAW_PICK_ORDER_1D;
	}

}
