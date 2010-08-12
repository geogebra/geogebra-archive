package geogebra3D.euclidian3D;

import geogebra.Matrix.GgbVector;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.SurfaceMesh;
import geogebra.kernel.GeoFunctionNVar;

/**
 * Class for drawing a 2-var function
 * @author matthieu
 *
 */
public class DrawFunction2Var extends Drawable3DSurfaces {
	
	private SurfaceMesh tree;
	
	private GeoFunctionNVar function;
	
	/** gl index of the geometry */
	private int geometryIndex = -1;
	
	private boolean unlimitedRange=true;
	
	private double lastBaseRadius;
	
	private static final double unlimitedScaleFactor = 2.0;
	
	private double savedRadius;


	/**
	 * common constructor
	 * @param a_view3d
	 * @param function
	 */
	public DrawFunction2Var(EuclidianView3D a_view3d, GeoFunctionNVar function) {
		super(a_view3d, function);
		this.function=function;
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
		v[0] = new GgbVector(x1,y1,z1);
		v[1] = new GgbVector(x1,y2,z1);
		v[2] = new GgbVector(x1,y1,z2);
		v[3] = new GgbVector(x1,y2,z2);
		v[4] = new GgbVector(x2,y1,z1);
		v[5] = new GgbVector(x2,y2,z1);
		v[6] = new GgbVector(x2,y1,z2);
		v[7] = new GgbVector(x2,y2,z2);

		savedRadius=0;
		double norm;
		for(int i = 0; i < 8; i++){
			view.toSceneCoords3D(v[i]);
			norm = v[i].norm();
			if(norm>savedRadius)
				savedRadius=norm;
		}
		savedRadius*=0.9;
	}
	
	protected void updateForItSelf(){
		updateRadius();
		if(unlimitedRange){
			lastBaseRadius=savedRadius*unlimitedScaleFactor;
			tree = new SurfaceMesh(function, lastBaseRadius, true);
		} else
			tree = new SurfaceMesh(function, savedRadius, false);

		super.updateForItSelf();
	}
	
	protected void updateForView(){
		updateRadius();
		if(unlimitedRange && savedRadius>lastBaseRadius){
			lastBaseRadius=savedRadius*unlimitedScaleFactor;
			function.setInterval(new double[] {-lastBaseRadius,lastBaseRadius}, 
								 new double [] {-lastBaseRadius,lastBaseRadius});
			tree = new SurfaceMesh(function, lastBaseRadius, true);
		} else if(unlimitedRange && savedRadius<lastBaseRadius/unlimitedScaleFactor*.5) {
			lastBaseRadius=savedRadius/unlimitedScaleFactor;
			function.setInterval(new double[] {-lastBaseRadius,lastBaseRadius}, 
								 new double [] {-lastBaseRadius,lastBaseRadius});
			tree = new SurfaceMesh(function, lastBaseRadius, true);
		}
	}

	public int getPickOrder() {
		return DRAW_PICK_ORDER_2D;
	}
	

	public int getType(){

			return DRAW_TYPE_CLOSED_SURFACES;

			//return DRAW_TYPE_SURFACES;
		
	}

}
