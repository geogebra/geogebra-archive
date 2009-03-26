package geogebra3D.euclidian3D;

import java.awt.Color;

import geogebra.kernel.GeoElement;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoElement3DInterface;
import geogebra3D.kernel3D.GeoSegment3D;


/**
 * 
 * @author ggb3D
 *	
 *	for "solid" drawables, like lines, segments, etc.
 *	these are drawable that are not to become transparent
 *
 */


public abstract class Drawable3DSolid extends Drawable3D {

	public Drawable3DSolid(EuclidianView3D a_view3d) {
		super(a_view3d);
	}
	
	
	public Drawable3DSolid(EuclidianView3D a_view3d, GeoElement a_geo) {
		super(a_view3d, a_geo);
	}

	public void draw(EuclidianRenderer3D renderer) {
		if(!getGeoElement().isEuclidianVisible() || !getGeoElement().isDefined())
			return;	
		
		renderer.setMaterial(getGeoElement().getObjectColor(),1.0f);//TODO geo.getAlphaValue());
		renderer.setDash(EuclidianRenderer3D.DASH_NONE); //TODO use object property
		renderer.setMatrix(getMatrix());
		
		drawGeometry(renderer);
		
		

	}

	
	public void drawPicked(EuclidianRenderer3D renderer) {
		if(!getGeoElement().isEuclidianVisible() || !getGeoElement().isDefined())
			return;
		if (!getGeoElement().doHighlighting())
			return;
		
		renderer.setMaterial(new Color(0f,0f,0f),0.75f);
		renderer.setDash(EuclidianRenderer3D.DASH_NONE);
		renderer.setMatrix(getMatrix());
		drawGeometryPicked(renderer);		
		

	}
	
	
	public void drawHidden(EuclidianRenderer3D renderer){
		
		if(!getGeoElement().isEuclidianVisible() || !getGeoElement().isDefined())
			return;
		
				
		renderer.setMaterial(getGeoElement().getObjectColor(),1.0f);//TODO geo.getAlphaValue());
		renderer.setDash(EuclidianRenderer3D.DASH_SIMPLE); //TODO use object property
		renderer.setMatrix(getMatrix());		
		drawGeometryHidden(renderer);		
		


	} 


	
	// methods not used for solid drawables
	public void drawHiding(EuclidianRenderer3D renderer) {}
	public void drawTransp(EuclidianRenderer3D renderer) {}

	public boolean isTransparent() {
		return false;
	}

}
