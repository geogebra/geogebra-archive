package geogebra3D.euclidian3D;

import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoLine3D;

public class DrawLine3D extends Drawable3D {

	GeoLine3D L;
	
	public DrawLine3D(EuclidianView3D view, GeoLine3D l){
		this.L=l;
		this.view3D=view;
		setGeoElement(l);
		
		update();
	}	
	
	public void update() {
		
        isVisible = geo.isEuclidianVisible();       				 
        if (!isVisible) return;
		labelVisible = geo.isLabelVisible();    	
		
		
		
		GgbMatrix mc = L.getSegmentMatrix(-2,3); 
		view3D.toScreenCoords3D(mc);
		
		matrix = mc.copy();

	}
	
	
	
	public void draw(EuclidianRenderer3D renderer) {
		if(!geo.isEuclidianVisible())
			return;
		
		renderer.setMaterial(geo.getObjectColor(),1.0f);//TODO geo.getAlphaValue());
		renderer.setMatrix(getMatrixGL());
		renderer.drawCylinder(0.02f); 
		renderer.resetMatrix();
		

	}

	public void drawHidden(EuclidianRenderer3D renderer) {
		// TODO Raccord de méthode auto-généré

	}

	public void drawHiding(EuclidianRenderer3D renderer) {
		// TODO Raccord de méthode auto-généré

	}

	public void drawPicked(EuclidianRenderer3D renderer) {
		// TODO Raccord de méthode auto-généré

	}

	public void drawTransp(EuclidianRenderer3D renderer) {
		// TODO Raccord de méthode auto-généré

	}

	public boolean isPicked(GgbVector pickLine) {
		// TODO Raccord de méthode auto-généré
		return false;
	}



}
