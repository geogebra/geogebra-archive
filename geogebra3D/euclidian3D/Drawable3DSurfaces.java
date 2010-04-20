package geogebra3D.euclidian3D;

import geogebra.kernel.GeoElement;
import geogebra3D.euclidian3D.opengl.Renderer;

import java.awt.Color;

public abstract class Drawable3DSurfaces extends Drawable3D {
	
	/** alpha value for rendering transparency */
	protected float alpha;

	public Drawable3DSurfaces(EuclidianView3D a_view3d, GeoElement a_geo) {
		super(a_view3d, a_geo);
	}

	public Drawable3DSurfaces(EuclidianView3D a_view3d) {
		super(a_view3d);
	}
	
	
	
	
	abstract void drawGeometryHiding(Renderer renderer);


	public void drawHiding(Renderer renderer){
		if(!getGeoElement().isEuclidianVisible() || !getGeoElement().isDefined())
			return;

		if (alpha<=0)
			return;
		
		renderer.setMatrix(getMatrix());
		drawGeometryHiding(renderer);
		
	}
	
	
	

	


	public void drawTransp(Renderer renderer){
		if(!getGeoElement().isEuclidianVisible() || !getGeoElement().isDefined()){
			//Application.debug("not visible "+getGeoElement().isEuclidianVisible()+",");
			return;
		}
		
		
		if (alpha<=0)
			return;
		
		
		if(getGeoElement().doHighlighting())
			renderer.setColor(renderer.getGeometryManager().getHigthlighting(getGeoElement().getObjectColor()),alpha);
		else
			renderer.setColor(getGeoElement().getObjectColor(),alpha);
			
		
		renderer.setMatrix(getMatrix());
		drawGeometry(renderer);
		
		
	}
	
	
	
	
	// method not used for transparent drawables
	public void draw(Renderer renderer){}
	public void drawHidden(Renderer renderer){} 	
	
	
	
	
	protected void updateForItSelf(){
		
		//update alpha value
		//use 1-Math.sqrt(1-alpha) because transparent parts are drawn twice
		alpha = (float) (1-Math.pow(1-getGeoElement().getAlphaValue(),1./3.));
		
	}
	
	protected void updateForView(){
		
	}
	
	


	public boolean isTransparent() {
		return true;
	}
	
	

	public int getType(){
		return DRAW_TYPE_SURFACES;
	}


}
