package geogebra3D.euclidian3D;

import geogebra.kernel.GeoElement;
import geogebra3D.euclidian3D.opengl.Renderer;

/**
 * Class for drawing surfaces
 * @author matthieu
 *
 */
public abstract class Drawable3DSurfaces extends Drawable3D {
	
	
	/** alpha value for rendering transparency */
	protected float alpha;

	/**
	 * common constructor
	 * @param a_view3d
	 * @param a_geo
	 */
	public Drawable3DSurfaces(EuclidianView3D a_view3d, GeoElement a_geo) {
		super(a_view3d, a_geo);
	}

	/**
	 * common constructor for previewable
	 * @param a_view3d
	 */
	public Drawable3DSurfaces(EuclidianView3D a_view3d) {
		super(a_view3d);
	}
	
	
	
	
	/**
	 * draws the geometry that hides other drawables (for dashed curves)
	 * @param renderer
	 */
	abstract void drawGeometryHiding(Renderer renderer);


	public void drawHiding(Renderer renderer){
		if(!isVisible())
			return;

		if (alpha<=0 || alpha == 1)
			return;
		
		//renderer.setMatrix(getMatrix());
		drawGeometryHiding(renderer);
		
	}
	
	
	

	


	public void drawTransp(Renderer renderer){
		if(!isVisible()){
			return;
		}
		
		
		if (alpha<=0 || alpha == 1)
			return;
		
		setLight(renderer);

		setHighlightingColor(alpha);
			
		
		//renderer.setMatrix(getMatrix());
		drawGeometry(renderer);
		
		
	}
	
	
	
	
	// method not used for transparent drawables
	public void draw(Renderer renderer){
		
		if(!isVisible()){
			//Application.debug("not visible "+getGeoElement().isEuclidianVisible()+",");
			return;
		}
		
		
		if (alpha<1)
			return;
		
		setLight(renderer);

		setHighlightingColor(alpha);
			
		
		//renderer.setMatrix(getMatrix());
		drawGeometry(renderer);
		
	}
	
	public void drawHidden(Renderer renderer){} 	
	
	
	
	
	protected boolean updateForItSelf(){
		
		//update alpha value
		//use 1-Math.sqrt(1-alpha) because transparent parts are drawn twice
		alpha = (float) (1-Math.pow(1-getGeoElement().getAlphaValue(),1./3.));
		
		setColors(alpha);
		
		return true;
	}
	
	protected void updateForView(){
		
	}
	
	


	public boolean isTransparent() {
		return true;
	}
	
	

	

	public void addToDrawable3DLists(Drawable3DLists lists){
		addToDrawable3DLists(lists,DRAW_TYPE_SURFACES);
	}
    
    public void removeFromDrawable3DLists(Drawable3DLists lists){
    	removeFromDrawable3DLists(lists,DRAW_TYPE_SURFACES);
    }
	

	protected double getColorShift(){
		return 0.2;
	}


}
