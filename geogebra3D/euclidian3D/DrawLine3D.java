package geogebra3D.euclidian3D;

import java.awt.Color;

import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoLine3D;

public class DrawLine3D extends Drawable3D {

	GeoLine3D L;
	
	double dashLength;
	
	
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
		
		
		
		GgbMatrix mc = L.getSegmentMatrix(-20,21);  //TODO use frustrum
		view3D.toScreenCoords3D(mc);
		
		matrix = mc.copy();
		
		
		dashLength = 0.12f/((float) L.getUnit()); //TODO use object property
		

	}
	
	
	
	public void draw(EuclidianRenderer3D renderer) {
		if(!geo.isEuclidianVisible())
			return;
		
		renderer.setMaterial(geo.getObjectColor(),1.0f);//TODO geo.getAlphaValue());
		renderer.setMatrix(getMatrixGL());
		renderer.drawCylinder(LINE3D_THICKNESS); 
		renderer.resetMatrix();
		

	}
	
	
	
	public void drawHidden(EuclidianRenderer3D renderer){
		
		if(!geo.isEuclidianVisible())
			return;
		
		
		double l2;
		GgbMatrix m; 
		
    	for(float l=-20; l<21;l+=2*dashLength){ //TODO use frustrum
    		l2 = l+dashLength;
    		if (l2>21) l2=21;
    		m = L.getSegmentMatrix(l,l2); 
    		view3D.toScreenCoords3D(m);
    		renderer.setMaterial(geo.getObjectColor(),1.0f);//TODO geo.getAlphaValue());
    		renderer.setMatrix(m.getGL());
    		renderer.drawCylinder(LINE3D_THICKNESS); 
    		
    		renderer.resetMatrix();
    	}

	} 

	public void drawHiding(EuclidianRenderer3D renderer) {
		// TODO Raccord de méthode auto-généré

	}

	public void drawPicked(EuclidianRenderer3D renderer){
		if(!geo.isEuclidianVisible())
			return;
		if (!geo.doHighlighting())
			return;
		
		renderer.setMaterial(new Color(0f,0f,0f),0.75f);
		renderer.setMatrix(getMatrixGL());
		renderer.drawCylinder(LINE3D_THICKNESS*PICKED_DILATATION); 
		renderer.resetMatrix();		
	};	

	public void drawForPicking(EuclidianRenderer3D renderer) {
		draw(renderer);
	}

	public void drawTransp(EuclidianRenderer3D renderer) {
		// TODO Raccord de méthode auto-généré

	}

	public boolean isPicked(GgbVector pickLine, boolean repaint) {
		// TODO Raccord de méthode auto-généré
		return false;
	}



}
