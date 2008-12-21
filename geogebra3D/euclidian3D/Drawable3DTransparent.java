package geogebra3D.euclidian3D;

import java.awt.Color;

import geogebra3D.kernel3D.GeoElement3D;

public abstract class Drawable3DTransparent extends Drawable3D {

	public Drawable3DTransparent(EuclidianView3D a_view3d, GeoElement3D a_geo) {
		super(a_view3d, a_geo);
	}



	public void drawHiding(EuclidianRenderer3D renderer){
		if(!getGeoElement().isEuclidianVisible())
			return;
		
		renderer.setMatrix(getMatrix());
		drawPrimitive(renderer);
		
	}
	
	
	

	public void drawPicked(EuclidianRenderer3D renderer){
		
		if(!getGeoElement().isEuclidianVisible())
			return;	
		if (!getGeoElement().doHighlighting())
			return;
		
		renderer.setMaterial(new Color(0f,0f,0f),0.25f);
		renderer.setMatrix(getMatrix());
		drawPrimitivePicked(renderer);
		
	};	
	


	public void drawTransp(EuclidianRenderer3D renderer){
		if(!getGeoElement().isEuclidianVisible())
			return;
		
		renderer.setMaterial(getGeoElement().getObjectColor(),0.5f);//TODO geo.getAlphaValue());
		renderer.setMatrix(getMatrix());
		drawPrimitive(renderer);
		
	}
	
	
	
	
	// method not used for transparent drawables
	public void draw(EuclidianRenderer3D renderer){}
	public void drawHidden(EuclidianRenderer3D renderer){} 	
	
	
	
	
	


	public boolean isTransparent() {
		return true;
	}


}
