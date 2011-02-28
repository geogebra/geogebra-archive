package geogebra3D.euclidian3D;

import geogebra.Matrix.Coords;
import geogebra.kernel.GeoFunctionNVar;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.plots.SurfaceMesh;

/**
 * Class for drawing an implicit 3-var function
 * @author matthieu
 *
 */
public class DrawImplicitFunction3Var extends Drawable3DSurfaces {
	
	
	private GeoFunctionNVar function;
	
	


	/**
	 * common constructor
	 * @param a_view3d
	 * @param function
	 */
	public DrawImplicitFunction3Var(EuclidianView3D a_view3d, GeoFunctionNVar function) {
		super(a_view3d, function);
		this.function=function;
		
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
	
	@Override
	protected void realtimeUpdate(){
	}
	
	
	protected boolean updateForItSelf(){
		

		//begin test (to remove)
		Renderer renderer = getView3D().getRenderer();
		PlotterBrush brush = renderer.getGeometryManager().getBrush();		
		brush.start(8);
		brush.setThickness(5,(float) getView3D().getScale());
		brush.segment(new Coords(0,0,0,1),new Coords(1,1,0,1));
		setGeometryIndex(brush.end());
		//end test

		return super.updateForItSelf();
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
