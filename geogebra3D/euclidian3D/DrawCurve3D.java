package geogebra3D.euclidian3D;

import java.util.LinkedList;

import geogebra.Matrix.GgbVector;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoCurveCartesian3D;

/**
 * @author ggb3D
 * 
 * Drawable for GeoCurveCartesian3D
 *
 */
public class DrawCurve3D extends Drawable3DCurves {
	private CurveTree tree;
	
	/** gl index of the quadric */
	private int geometryIndex = -1;
	
	/** handle to the curve */
	private GeoCurveCartesian3D curve;
	
	private double savedRadius;
	
	private final double radiusMaxFactor = 1.1;
	private final double radiusMinFactor = 0.9;
	
	
	/**
	 * @param a_view3d the 3D view where the curve is drawn
	 * @param curve the 3D curve to draw
	 */
	public DrawCurve3D(EuclidianView3D a_view3d, GeoCurveCartesian3D curve) {
		super(a_view3d,curve);
		tree = new CurveTree(curve, a_view3d);
		this.curve=curve;
	}
	

	public void drawGeometry(Renderer renderer) {
		
		renderer.setThickness(getGeoElement().getLineThickness());

		renderer.getGeometryManager().draw(geometryIndex);
		
	}
	
	/** decides if the curve should be redrawn depending on how the view changes
	 * @return
	 */
	private boolean needRedraw(){
		double currRad = currentRadius();
		if(currRad>savedRadius*radiusMaxFactor || currRad< savedRadius*radiusMinFactor){
			savedRadius=currRad;
			return true;
		}
		return false;
	}
	
	/** gets the viewing radius based on the viewing frustum 
	 */
	private double currentRadius() {
		EuclidianView3D view = getView3D();
		Renderer temp = view.getRenderer();
		double x1 = temp.getLeft();
		double x2 = temp.getRight();
		double y1 = temp.getTop();
		double y2 = temp.getBottom();
		double z1 = temp.getFront();
		double z2 = temp.getBack();
		GgbVector [] v = new GgbVector[8];
		v[0] = new GgbVector(x1,y1,z1,1);
		v[1] = new GgbVector(x1,y2,z1,1);
		v[2] = new GgbVector(x1,y1,z2,1);
		v[3] = new GgbVector(x1,y2,z2,1);
		v[4] = new GgbVector(x2,y1,z1,1);
		v[5] = new GgbVector(x2,y2,z1,1);
		v[6] = new GgbVector(x2,y1,z2,1);
		v[7] = new GgbVector(x2,y2,z2,1);
		
		double radius=0;
		for(int i = 0; i < 8; i++){
			view.toSceneCoords3D(v[i]);
			if(v[i].norm()>radius)
				radius=v[i].norm();
		}
		return radius;
	}

	
	protected void updateForItSelf(){
		Renderer renderer = getView3D().getRenderer();
		if (!curve.isEuclidianVisible() || !curve.isDefined()){
			renderer.getGeometryManager().remove(geometryIndex);
			geometryIndex = -1;
		} else if(needRedraw()){
			renderer.getGeometryManager().remove(geometryIndex);
			
			PlotterBrush brush = renderer.getGeometryManager().getBrush();

			brush.setThickness(getGeoElement().getLineThickness(),(float) getView3D().getScale());

			brush.start(8);
			
			brush.draw(tree,savedRadius);

			geometryIndex = brush.end();
		}
		/*
		Application.debug(curve.evaluateCurve(curve.getMinParameter()).toString()+"\n"+curve.evaluateTangent(curve.getMinParameter()).toString());
		Application.debug(curve.evaluateCurve(0).toString()+"\n"+curve.evaluateTangent(0).toString());
		Application.debug(curve.evaluateCurve(curve.getMaxParameter()).toString()+"\n"+curve.evaluateTangent(curve.getMaxParameter()).toString());
		 */
	}
	
	
	protected void updateForView(){
		
		updateForItSelf();
		
	}
	
	
	
	public int getPickOrder() {
		return DRAW_PICK_ORDER_1D;
	}

}
