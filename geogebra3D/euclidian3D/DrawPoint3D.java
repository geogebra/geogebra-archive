package geogebra3D.euclidian3D;



import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoPoint3D;

import java.awt.Color;





public class DrawPoint3D extends Drawable3D{
	
	
	
	private GeoPoint3D P;    
	private GgbVector coords = new GgbVector(4);
	
	
		
	
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
		if (P.hasPath1D())
			renderer.drawSphere(POINT3D_RADIUS*POINT_ON_PATH_DILATATION); //points on path are more visible 
		else
			renderer.drawSphere(POINT3D_RADIUS);//TODO use object property
		renderer.resetMatrix();

	}
	

	
	public void drawHidden(EuclidianRenderer3D renderer){
		draw(renderer);
		
	}	
	
	
	public void drawPicked(EuclidianRenderer3D renderer){
		if(!geo.isEuclidianVisible())
			return;		
		if (!geo.doHighlighting())
			return;

		//renderer.setMaterial(new Color(0f,0f,0f),0.75f);
		renderer.setMatrix(getMatrixGL());
		renderer.drawSphere(POINT3D_RADIUS*PICKED_DILATATION);//TODO use object property
		renderer.resetMatrix();
		

	}
	
	public void drawForPicking(EuclidianRenderer3D renderer) {
		draw(renderer);
	};	
	
	public void drawTransp(EuclidianRenderer3D renderer){}
	public void drawHiding(EuclidianRenderer3D renderer){}
	
	
	/*
	public boolean isPicked(GgbVector pickPoint, boolean repaint){
		//TODO use euclidianview3D scale factor
		if (coords.subVector(1,3).distLine(pickPoint.subVector(1,3),new GgbVector(new double[] {0,0,1}))<=POINT3D_RADIUS/10f){
			//Application.debug("picked = "+P.getLabel());
			//P.setHighlighted(true,repaint);
			return true;
		}else{
			//P.setHighlighted(false,repaint);
			return false;
		}
	};
	*/

}
