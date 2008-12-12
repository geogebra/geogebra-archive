package geogebra3D.euclidian3D;




import java.awt.Color;

import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoTriangle3D;




public class DrawPolygon3D extends Drawable3D {

	GeoTriangle3D T;
	GgbMatrix m; //representative matrix in physical coordinates

	
	
	public DrawPolygon3D(EuclidianView3D view, GeoTriangle3D t){
		this.T=t;
		setView3D(view);
		setGeoElement(t);
        
		update();
	}
	

	public void update() {
		
		setVisible(getGeoElement().isEuclidianVisible());       				 
        if (!isVisible()){
        	return;
        }
        setLabelVisible(getGeoElement().isLabelVisible());    	
		
		GgbMatrix mc = T.getMatrixCompleted(); 
		getView3D().toScreenCoords3D(mc);
		
		setMatrix(mc.copy());

	}
	
	
	
	
	//drawing

	
	public void draw(EuclidianRenderer3D renderer){}

	
	public void drawHidden(EuclidianRenderer3D renderer){} 
	
	public void drawPicked(EuclidianRenderer3D renderer){
		if(!getGeoElement().isEuclidianVisible())
			return;	
		if (!getGeoElement().doHighlighting())
			return;
		
		renderer.setMaterial(new Color(0f,0f,0f),0.25f);
		renderer.setMatrix(getMatrixGL());
		renderer.drawTriangle();
		renderer.resetMatrix();		
	};
	
	
	public void drawForPicking(EuclidianRenderer3D renderer) {
		if(!getGeoElement().isEuclidianVisible())
			return;
		renderer.setMatrix(getMatrixGL());
		renderer.drawTriangle();
		renderer.resetMatrix();	
		
	};	
	
	
	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_2D;
	}	
	
	
	public boolean isTransparent(){
		return true; //TODO : use object property
	}	
	
	
	
	
	
	
	
	public void drawTransp(EuclidianRenderer3D renderer){
		if(!getGeoElement().isEuclidianVisible())
			return;
		
		renderer.setMaterial(getGeoElement().getObjectColor(),0.5f);//TODO geo.getAlphaValue());
		renderer.setMatrix(getMatrixGL());
		renderer.drawTriangle();
		renderer.resetMatrix();
		
	}
	
	public void drawHiding(EuclidianRenderer3D renderer){
		if(!getGeoElement().isEuclidianVisible())
			return;
		
		renderer.setMatrix(getMatrixGL());
		renderer.drawTriangle();
		renderer.resetMatrix();
		
	}
	
	
	
	
	
	


	
	

	

}
