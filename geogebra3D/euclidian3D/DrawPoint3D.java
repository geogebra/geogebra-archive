package geogebra3D.euclidian3D;



import java.awt.Color;

import geogebra.Application;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoPoint3D;





public class DrawPoint3D extends Drawable3D{
	
	
	
	private GeoPoint3D P;    
	private GgbVector coords = new GgbVector(4);
	
	
	float radius = 100f; //TODO use object property
		
	
	public DrawPoint3D(EuclidianView3D view3D, GeoPoint3D P) {     
		
    	this.view3D = view3D;          
        this.P = P;
        setGeoElement(P);
        
        update();
		
	}
	
	public void update() {
		isVisible = geo.isEuclidianVisible();       				 
		if (!isVisible) return;
		labelVisible = geo.isLabelVisible();  //TODO label  	

		coords.set(P.getCoords()); 
		view3D.toScreenCoords3D(coords);
		
		matrix.set(coords, 4);
		//TODO use point "thickness"
		for(int i=1;i<=3;i++){
			matrix.set(i,i,0.1);
		}

	}
	

	public void draw(EuclidianRenderer3D renderer){
		if(!geo.isEuclidianVisible())
			return;
		
		renderer.setMaterial(geo.getObjectColor(),1f);//TODO geo.getAlphaValue());
		renderer.setMatrix(getMatrixGL());
		renderer.drawSphere(radius);
		renderer.resetMatrix();

	}
	

	
	public void drawHidden(EuclidianRenderer3D renderer){
		draw(renderer);
		
	}	
	
	
	public void drawPicked(EuclidianRenderer3D renderer){
		if(!geo.isEuclidianVisible())
			return;
		
		if (geo.doHighlighting()){
			renderer.setMaterial(new Color(0f,0f,0f),0.75f);
			renderer.setMatrix(getMatrixGL());
			renderer.drawSphere(radius*1.3f);
			renderer.resetMatrix();
		}

	}
	
	
	public void drawTransp(EuclidianRenderer3D renderer){}
	public void drawHiding(EuclidianRenderer3D renderer){}
	
	
	
	public boolean isPicked(GgbVector pickPoint){
		//TODO use euclidianview3D scale factor
		if (coords.subVector(1,3).distLine(pickPoint.subVector(1,3),new GgbVector(new double[] {0,0,1}))<=radius/10f){
			//Application.debug("picked = "+P.getLabel());
			geo.setHighlighted(true);
			return true;
		}else{
			geo.setHighlighted(false);
			return false;
		}
	};

}
