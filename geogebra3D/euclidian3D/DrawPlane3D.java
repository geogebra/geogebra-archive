package geogebra3D.euclidian3D;




import java.awt.Color;

import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoPlane3D;




public class DrawPlane3D extends Drawable3D {

	GeoPlane3D P;
	GgbMatrix m; //representative matrix in physical coordinates

	
	
	public DrawPlane3D(EuclidianView3D view, GeoPlane3D p){
		this.P=p;
		setView3D(view);
		setGeoElement(p);
        
		update();
	}
	

	public void update() {
       setVisible(getGeoElement().isEuclidianVisible());       				 
        if (!isVisible()) return;
        setLabelVisible(getGeoElement().isLabelVisible());    	

		
		//GgbMatrix mc = P.getMatrixCompleted(); 
		GgbMatrix mc = P.getDrawingMatrix(); 
		getView3D().toScreenCoords3D(mc);
		
		setMatrix(mc.copy());
		
       
	}
	
	

	
	public void draw(EuclidianRenderer3D renderer){}
	public void drawHidden(EuclidianRenderer3D renderer){} 
	
	
	public void drawPicked(EuclidianRenderer3D renderer){
		
		/*
		if(!geo.isEuclidianVisible())
			return;	
		if (!geo.doHighlighting())
			return;
		
		renderer.setMaterial(new Color(0f,0f,0f),0.25f);
		renderer.setMatrix(getMatrixGL());
		renderer.drawQuad();
		renderer.resetMatrix();		
		*/
	};	
	
	
	public void drawForPicking(EuclidianRenderer3D renderer) {

		if(!getGeoElement().isEuclidianVisible())
			return;
		renderer.setMatrix(getMatrixGL());
		renderer.drawQuad();
		renderer.resetMatrix();			
		
	};
	
	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_MAX; //for now : plane xOy should not be treated as a plane, but a part of the drawing pad
		//TODO return DRAW_PICK_ORDER_2D;
	}	
	
	public boolean isTransparent(){
		return true; //TODO : use object property
	}	
	
	public void drawTransp(EuclidianRenderer3D renderer){
		if(!getGeoElement().isEuclidianVisible())
			return;
		
		renderer.setMaterial(getGeoElement().getObjectColor(),0.5f);//TODO geo.getAlphaValue());
		renderer.setMatrix(getMatrixGL());
		renderer.drawQuad();
		renderer.resetMatrix();
		
		
		
		//grid
		GgbMatrix mc;
		
		for(double x=P.getGridXmin();x<=P.getGridXmax();x+=P.getGridXd()){
			mc = P.getDrawingXMatrix(x); 
			getView3D().toScreenCoords3D(mc);
			renderer.setMatrix(mc.get());
			renderer.drawCylinder(0.01f);
			renderer.resetMatrix();			
		}
		
		for(double y=P.getGridYmin();y<=P.getGridYmax();y+=P.getGridYd()){
			mc = P.getDrawingYMatrix(y); 
			getView3D().toScreenCoords3D(mc);
			renderer.setMatrix(mc.get());
			renderer.drawCylinder(0.01f);
			renderer.resetMatrix();			
		}
		
		
	}
	

	
	
	public void drawHiding(EuclidianRenderer3D renderer){
		if(!getGeoElement().isEuclidianVisible())
			return;
		
		renderer.setMatrix(getMatrixGL());
		renderer.drawQuad();
		renderer.resetMatrix();
		
	}
	

	
	
	
	
	
	

}
