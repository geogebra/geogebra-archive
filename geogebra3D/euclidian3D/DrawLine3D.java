package geogebra3D.euclidian3D;

import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoLine3D;

import java.awt.Color;

public class DrawLine3D extends Drawable3D {

	GeoLine3D L;
	
	double dashLength;
	
	
	public DrawLine3D(EuclidianView3D view, GeoLine3D l){
		this.L=l;
		setView3D(view);
		setGeoElement(l);
		
		update();
	}	
	
	public void update() {
		
        setVisible(getGeoElement().isEuclidianVisible());       				 
        if (!isVisible()) return;
        setLabelVisible(getGeoElement().isLabelVisible());    	
		
		
		
		GgbMatrix mc = L.getSegmentMatrix(-20,21);  //TODO use frustrum
		getView3D().toScreenCoords3D(mc);
		
		setMatrix(mc.copy());
		
		
		dashLength = 0.12f/((float) L.getUnit()); //TODO use object property
		

	}
	
	
	
	public void draw(EuclidianRenderer3D renderer) {
		if(!getGeoElement().isEuclidianVisible())
			return;
		
		renderer.setMaterial(getGeoElement().getObjectColor(),1.0f);//TODO geo.getAlphaValue());
		renderer.setMatrix(getMatrixGL());
		renderer.drawCylinder(LINE3D_THICKNESS); 
		renderer.resetMatrix();
		

	}
	
	
	
	public void drawHidden(EuclidianRenderer3D renderer){
		
		if(!getGeoElement().isEuclidianVisible())
			return;
		
		
		double l2;
		GgbMatrix m; 
		
    	for(float l=-20; l<21;l+=2*dashLength){ //TODO use frustrum
    		l2 = l+dashLength;
    		if (l2>21) l2=21;
    		m = L.getSegmentMatrix(l,l2); 
    		getView3D().toScreenCoords3D(m);
    		renderer.setMaterial(getGeoElement().getObjectColor(),1.0f);//TODO geo.getAlphaValue());
    		renderer.setMatrix(m.get());
    		renderer.drawCylinder(LINE3D_THICKNESS); 
    		
    		renderer.resetMatrix();
    	}

	} 

	public void drawHiding(EuclidianRenderer3D renderer) {
		// TODO Raccord de méthode auto-généré

	}

	
	
	public void drawPicked(EuclidianRenderer3D renderer){
		if(!getGeoElement().isEuclidianVisible())
			return;
		if (!getGeoElement().doHighlighting())
			return;
		
		renderer.setMaterial(new Color(0f,0f,0f),0.75f);
		renderer.setMatrix(getMatrixGL());
		renderer.drawCylinder(LINE3D_THICKNESS*PICKED_DILATATION); 
		renderer.resetMatrix();		
	};	

	public void drawForPicking(EuclidianRenderer3D renderer) {
		draw(renderer);
	}

	public int getPickOrder(){
		return DRAW_PICK_ORDER_1D;
	}
	
	public boolean isTransparent(){
		return false; //TODO : use object property
	}
	
	public void drawTransp(EuclidianRenderer3D renderer) {
		// TODO Raccord de méthode auto-généré

	}
	
	

	

}
