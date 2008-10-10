package geogebra3D.euclidian3D;




import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoPlane3D;




public class DrawPlane3D extends Drawable3D {

	GeoPlane3D P;
	GgbMatrix m; //representative matrix in physical coordinates

	
	
	public DrawPlane3D(EuclidianView3D view, GeoPlane3D p){
		this.P=p;
		this.view3D=view;
		setGeoElement(p);
        
		update();
	}
	

	public void update() {
		
        isVisible = geo.isEuclidianVisible();       				 
        if (!isVisible) return;
		labelVisible = geo.isLabelVisible();    	
		
		

		
		//GgbMatrix mc = P.getMatrixCompleted(); 
		GgbMatrix mc = P.getDrawingMatrix(); 
		view3D.toScreenCoords3D(mc);
		
		matrix = mc.copy();
		
       
	}
	
	

	
	public void draw(EuclidianRenderer3D renderer){}
	public void drawHidden(EuclidianRenderer3D renderer){} 
	public void drawPicked(EuclidianRenderer3D renderer){};
	
	public void drawTransp(EuclidianRenderer3D renderer){
		if(!geo.isEuclidianVisible())
			return;
		
		renderer.setMaterial(geo.getObjectColor(),0.5f);//TODO geo.getAlphaValue());
		renderer.setMatrix(getMatrixGL());
		renderer.drawQuad();
		renderer.resetMatrix();
		
		
		
		//grid
		GgbMatrix mc;
		
		for(double x=P.getGridXmin();x<=P.getGridXmax();x+=P.getGridXd()){
			mc = P.getDrawingXMatrix(x); 
			view3D.toScreenCoords3D(mc);
			renderer.setMatrix(mc.getGL());
			renderer.drawCylinder(0.01f);
			renderer.resetMatrix();			
		}
		
		for(double y=P.getGridYmin();y<=P.getGridYmax();y+=P.getGridYd()){
			mc = P.getDrawingYMatrix(y); 
			view3D.toScreenCoords3D(mc);
			renderer.setMatrix(mc.getGL());
			renderer.drawCylinder(0.01f);
			renderer.resetMatrix();			
		}
		
		
	}
	
	/*
	public void drawTransp(GraphicsContext3D gc){
		if(isVisible){
			gc.setAppearance(appTransp);
			
			gc.setModelTransform(t3d);			
			gc.draw(geomTransp);
			
			//grid
			GgbMatrix mc;
			Transform3D t;			
			gc.setAppearance(gridApp);
			
			for(double x=P.getGridXmin();x<=P.getGridXmax();x+=P.getGridXd()){
				mc = P.getDrawingXMatrix(x); 
				view3D.toScreenCoords3D(mc);
				t = new Transform3D();
				mc.getTransform3D(t);
				gc.setModelTransform(t);			
				gc.draw(gridGeom);
			}
			
			for(double y=P.getGridYmin();y<=P.getGridYmax();y+=P.getGridYd()){
				mc = P.getDrawingYMatrix(y); 
				view3D.toScreenCoords3D(mc);
				t = new Transform3D();
				mc.getTransform3D(t);
				gc.setModelTransform(t);			
				gc.draw(gridGeom);
			}
		}
		
	}
	*/
	
	
	public void drawHiding(EuclidianRenderer3D renderer){
		if(!geo.isEuclidianVisible())
			return;
		
		renderer.setMatrix(getMatrixGL());
		renderer.drawQuad();
		renderer.resetMatrix();
		
	}
	
	/*
	public void drawHiding(GraphicsContext3D gc){
		if(isVisible){
			gc.setModelTransform(t3d);    	
			gc.draw(geomTransp);
		}
		
	}	
	*/
	
	
	
	
	
	
	
	
	public boolean isPicked(GgbVector pickLine){ return false; };
	

	

}
