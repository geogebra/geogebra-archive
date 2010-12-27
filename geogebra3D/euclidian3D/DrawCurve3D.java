package geogebra3D.euclidian3D;

import geogebra.Matrix.GgbVector;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.plots.CurveMesh;
import geogebra3D.kernel3D.GeoCurveCartesian3D;

/**
 * @author ggb3D
 * 
 * Drawable for GeoCurveCartesian3D
 *
 */
public class DrawCurve3D extends Drawable3DCurves {
	private CurveMesh mesh;
	
	
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
		this.curve=curve;
		double rad = currentRadius();
		savedRadius=rad;
		mesh = new CurveMesh(curve, rad);
	}
	

	public void drawGeometry(Renderer renderer) {
		
		renderer.setThickness(getGeoElement().getLineThickness());

		renderer.getGeometryManager().draw(getGeometryIndex());
		
	}
	
	/**
	 * Decides if the curve should be redrawn or not depending on how the view changes
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
		double z1 = temp.getFront(true);
		double z2 = temp.getBack(true);
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

	
	@Override
	protected void realtimeUpdate(){
		
		Renderer renderer = getView3D().getRenderer();
		mesh.setRadius(savedRadius);
		mesh.optimize();
		
		PlotterBrush brush = renderer.getGeometryManager().getBrush();
		brush.start(8);
		brush.draw(mesh,savedRadius);

		setGeometryIndex(brush.end());
		
	}
	
	protected boolean updateForItSelf(){

		return true;
	}
	
	
	protected void updateForView(){
		
		updateForItSelf();
		
	}
	
	
	
	public int getPickOrder() {
		return DRAW_PICK_ORDER_1D;
	}

}
