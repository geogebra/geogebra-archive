package geogebra3D.euclidian3D;

import java.awt.Color;

import geogebra.kernel.GeoElement;
import geogebra.main.Application;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoElement3DInterface;

public abstract class Drawable3DTransparent extends Drawable3D {

	public Drawable3DTransparent(EuclidianView3D a_view3d, GeoElement a_geo) {
		super(a_view3d, a_geo);
	}

	
	
	abstract void drawGeometryHiding(EuclidianRenderer3D renderer);


	public void drawHiding(EuclidianRenderer3D renderer){
		if(!getGeoElement().isEuclidianVisible())
			return;

		float alpha = getGeoElement().getAlphaValue();
		if (alpha<=0)
			return;
		
		renderer.setMatrix(getMatrix());
		drawGeometryHiding(renderer);
		
	}
	
	
	

	//TODO improve specific geometry for picking
	public void drawPicked(EuclidianRenderer3D renderer){
		
		if(!getGeoElement().isEuclidianVisible())
			return;	
		if (!getGeoElement().doHighlighting())
			return;
		
		renderer.setMaterial(new Color(0f,0f,0f),0.25f);
		renderer.setMatrix(getMatrix());
		drawGeometryPicked(renderer);
		
	};	
	


	public void drawTransp(EuclidianRenderer3D renderer){
		if(!getGeoElement().isEuclidianVisible())
			return;
		
		float alpha = getGeoElement().getAlphaValue();
		if (alpha<=0)
			return;
		
		//use 1-Math.sqrt(1-alpha) because transparent parts are drawn twice
		renderer.setMaterial(getGeoElement().getObjectColor(),1-Math.sqrt(1-alpha));
		renderer.setMatrix(getMatrix());
		drawGeometry(renderer);
		
		
	}
	
	
	
	
	// method not used for transparent drawables
	public void draw(EuclidianRenderer3D renderer){}
	public void drawHidden(EuclidianRenderer3D renderer){} 	
	
	
	
	
	


	public boolean isTransparent() {
		return true;
	}


}
