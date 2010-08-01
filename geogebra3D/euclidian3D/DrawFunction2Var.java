package geogebra3D.euclidian3D;

import geogebra.Matrix.GgbVector;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.opengl.SurfaceTree2;
import geogebra.kernel.GeoFunctionNVar;

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
	public DrawFunction2Var(EuclidianView3D a_view3d, GeoFunctionNVar function) {
		super(a_view3d, function);
		updateRadius();
		tree = new SurfaceTree2(function, a_view3d, savedRadius);
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
	
	@Override
	protected void realtimeUpdate(){
		
		Renderer renderer = getView3D().getRenderer();
		tree.optimize();
		renderer.getGeometryManager().remove(geometryIndex);
		updateRadius();
		tree.setRadius(savedRadius);
		
		PlotterSurface surface = renderer.getGeometryManager().getSurface();
		GeoFunctionNVar geo = (GeoFunctionNVar) getGeoElement();
		surface.start(geo);
		surface.setU((float) geo.getMinParameter(0), (float) geo.getMaxParameter(0));
		surface.setNbU((int) ((geo.getMaxParameter(0)-geo.getMinParameter(0))*10));
		surface.setV((float) geo.getMinParameter(1), (float) geo.getMaxParameter(1));
		surface.setNbV((int) ((geo.getMaxParameter(1)-geo.getMinParameter(1))*10));
		surface.draw(tree);
		geometryIndex=surface.end();
	}
	
	/** gets the viewing radius based on the viewing frustum 
	 */
	private void updateRadius() {
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

		savedRadius=0;
		double norm;
		for(int i = 0; i < 8; i++){
			view.toSceneCoords3D(v[i]);
			norm = v[i].norm();
			if(norm>savedRadius)
				savedRadius=norm;
		}
		savedRadius*=0.65;
	}
	
	private double savedRadius;
	
	protected void updateForItSelf(){
		super.updateForItSelf();
		
		/*Renderer renderer = getView3D().getRenderer();

		if(needRedraw()){
			renderer.getGeometryManager().remove(geometryIndex);
			tree.setRadius(savedRadius);
			
			PlotterSurface surface = renderer.getGeometryManager().getSurface();
			GeoFunctionNVar geo = (GeoFunctionNVar) getGeoElement();
			surface.start(geo);
			surface.setU((float) geo.getMinParameter(0), (float) geo.getMaxParameter(0));
			surface.setNbU((int) ((geo.getMaxParameter(0)-geo.getMinParameter(0))*10));
			surface.setV((float) geo.getMinParameter(1), (float) geo.getMaxParameter(1));
			surface.setNbV((int) ((geo.getMaxParameter(1)-geo.getMinParameter(1))*10));
			surface.draw(tree);
			geometryIndex=surface.end();
		}*/
		
	}
	
	protected void updateForView(){
	}

	public int getPickOrder() {
		return DRAW_PICK_ORDER_2D;
	}
	

	public int getType(){

			return DRAW_TYPE_CLOSED_SURFACES;

			//return DRAW_TYPE_SURFACES;
		
	}

}
