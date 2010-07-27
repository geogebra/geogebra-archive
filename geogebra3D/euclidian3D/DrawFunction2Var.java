package geogebra3D.euclidian3D;

import geogebra.Matrix.GgbVector;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.opengl.SurfaceTree2;
import geogebra3D.kernel3D.GeoFunction2Var;

/**
 * Class for drawing a 2-var function
 * @author matthieu
 *
 */
public class DrawFunction2Var extends Drawable3DSurfaces {
	
	
	private SurfaceTree2 tree;
	
	/** gl index of the geometry */
	private int geometryIndex = -1;


	/**
	 * common constructor
	 * @param a_view3d
	 * @param function
	 */
	public DrawFunction2Var(EuclidianView3D a_view3d, GeoFunction2Var function) {
		super(a_view3d, function);
		tree = new SurfaceTree2(function, a_view3d);
	}
	
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(geometryIndex);
	}

	void drawGeometryHiding(Renderer renderer) {
		drawGeometry(renderer);
	}

	public void drawGeometryHidden(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	public void drawGeometryPicked(Renderer renderer) {
		// TODO Auto-generated method stub

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
		v[0] = new GgbVector(x1,y1,z1,0);
		v[1] = new GgbVector(x1,y2,z1,0);
		v[2] = new GgbVector(x1,y1,z2,0);
		v[3] = new GgbVector(x1,y2,z2,0);
		v[4] = new GgbVector(x2,y1,z1,0);
		v[5] = new GgbVector(x2,y2,z1,0);
		v[6] = new GgbVector(x2,y1,z2,0);
		v[7] = new GgbVector(x2,y2,z2,0);
		
		double radius=0;
		double norm;
		for(int i = 0; i < 8; i++){
			view.toSceneCoords3D(v[i]);
			norm = v[i].norm();
			if(norm>radius)
				radius=norm;
		}
		return radius;
	}
	
	private double savedRadius;
	
	public final double radiusMaxFactor = 1.1;
	private final double radiusMinFactor = 0.9;
	
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
	
	protected void updateForItSelf(){
		super.updateForItSelf();
		
		Renderer renderer = getView3D().getRenderer();
		
		//if(needRedraw()){
			renderer.getGeometryManager().remove(geometryIndex);
			tree.setRadius(savedRadius);
			tree.setRadius(0.05);
			
			PlotterSurface surface = renderer.getGeometryManager().getSurface();
			GeoFunction2Var geo = (GeoFunction2Var) getGeoElement();
			surface.start(geo);
			surface.setU((float) geo.getMinParameter(0), (float) geo.getMaxParameter(0));
			surface.setNbU((int) ((geo.getMaxParameter(0)-geo.getMinParameter(0))*10));
			surface.setV((float) geo.getMinParameter(1), (float) geo.getMaxParameter(1));
			surface.setNbV((int) ((geo.getMaxParameter(1)-geo.getMinParameter(1))*10));
			
			tree.optimize();
			
			surface.draw(tree);
			geometryIndex=surface.end();
		//}
		
		
	}

	
	
	
	
	protected void updateForView(){
		updateForItSelf();
	}
	
	
	
	
	
	
	
	


	public int getPickOrder() {
		return DRAW_PICK_ORDER_2D;
	}
	

	public int getType(){

			return DRAW_TYPE_CLOSED_SURFACES;

			//return DRAW_TYPE_SURFACES;
		
	}

}
