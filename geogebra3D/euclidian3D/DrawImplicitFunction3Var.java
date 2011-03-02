package geogebra3D.euclidian3D;

import geogebra.Matrix.Coords;
import geogebra.kernel.GeoFunctionNVar;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.plots.MarchingCubes;
import geogebra3D.euclidian3D.plots.SurfaceMesh;

/**
 * Class for drawing an implicit 3-var function
 * @author matthieu
 *
 */
public class DrawImplicitFunction3Var extends Drawable3DSurfaces {

	private MarchingCubes mc;
	
	private double savedRadius;
	
	/**
	 * common constructor
	 * @param a_view3d
	 * @param function
	 */
	public DrawImplicitFunction3Var(EuclidianView3D a_view3d, GeoFunctionNVar function) {
		super(a_view3d, function);
		updateRadius();
		mc = new MarchingCubes(function, savedRadius);
	}
	
	private void updateRadius() {
		EuclidianView3D view = getView3D();
		Renderer temp = view.getRenderer();
		double x1 = temp.getLeft();
		double x2 = temp.getRight();
		double y1 = temp.getTop();
		double y2 = temp.getBottom();
		double z1 = temp.getFront(true);
		double z2 = temp.getBack(true);
		Coords [] v = new Coords[8];
		v[0] = new Coords(x1,y1,z1);
		v[1] = new Coords(x1,y2,z1);
		v[2] = new Coords(x1,y1,z2);
		v[3] = new Coords(x1,y2,z2);
		v[4] = new Coords(x2,y1,z1);
		v[5] = new Coords(x2,y2,z1);
		v[6] = new Coords(x2,y1,z2);
		v[7] = new Coords(x2,y2,z2);

		savedRadius=0;
		double norm;
		for(int i = 0; i < 8; i++){
			view.toSceneCoords3D(v[i]);
			norm = v[i].norm();
			if(norm>savedRadius)
				savedRadius=norm;
		}
	}

	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(getGeometryIndex());
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
	
	protected boolean updateForItSelf(){
		Renderer renderer = getView3D().getRenderer();
		PlotterSurface surface = renderer.getGeometryManager().getSurface();
		GeoFunctionNVar geo = (GeoFunctionNVar) getGeoElement();
		surface.start(geo);
		
		float uMin, uMax, vMin, vMax;
		uMin = -1; uMax = 1; vMin = -1; vMax = 1;
		surface.setU(uMin,uMax);
		surface.setNbU((int) (uMax-uMin)*10);
		surface.setV(vMin, vMax);
		surface.setNbV((int) (vMax-vMin)*10);

		surface.draw(mc);
		setGeometryIndex(surface.end());

		return false;
	}
	
	protected void updateForView(){
		
	}

	public int getPickOrder() {
		return DRAW_PICK_ORDER_2D;
	}
	


	public void addToDrawable3DLists(Drawable3DLists lists){
		addToDrawable3DLists(lists,DRAW_TYPE_CLOSED_SURFACES);
	}
    
    public void removeFromDrawable3DLists(Drawable3DLists lists){
    	removeFromDrawable3DLists(lists,DRAW_TYPE_CLOSED_SURFACES);
    }
    

}
