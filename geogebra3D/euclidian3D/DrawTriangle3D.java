package geogebra3D.euclidian3D;




import geogebra.Application;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoTriangle3D;




public class DrawTriangle3D extends Drawable3D {

	GeoTriangle3D T;
	GgbMatrix m; //representative matrix in physical coordinates

	
	
	public DrawTriangle3D(EuclidianView3D view, GeoTriangle3D t){
		this.T=t;
		this.view3D=view;
		setGeoElement(t);
        
		update();
	}
	

	public void update() {
		
        isVisible = geo.isEuclidianVisible();       				 
        if (!isVisible){
        	
        	return;
        }
		labelVisible = geo.isLabelVisible();    	
		
		GgbMatrix mc = T.getMatrixCompleted(); 
		view3D.toScreenCoords3D(mc);
		
		matrix = mc.copy();

	}
	
	

	
	public void draw(EuclidianRenderer3D renderer){}

	
	public void drawHidden(EuclidianRenderer3D renderer){} 
	public void drawPicked(EuclidianRenderer3D renderer){};
	public void drawForPicking(EuclidianRenderer3D renderer) {};		
	
	public void drawTransp(EuclidianRenderer3D renderer){
		if(!geo.isEuclidianVisible())
			return;
		
		renderer.setMaterial(geo.getObjectColor(),0.5f);//TODO geo.getAlphaValue());
		renderer.setMatrix(getMatrixGL());
		renderer.drawTriangle();
		renderer.resetMatrix();
		
	}
	
	public void drawHiding(EuclidianRenderer3D renderer){
		if(!geo.isEuclidianVisible())
			return;
		
		renderer.setMatrix(getMatrixGL());
		renderer.drawTriangle();
		renderer.resetMatrix();
		
	}
	
	
	
	
	
	


	
	
	public boolean isPicked(GgbVector pickLine, boolean repaint){ return false; };
	

	

}
