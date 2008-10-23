package geogebra3D.euclidian3D;




import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoSegment3D;

import java.awt.Color;



public class DrawSegment3D extends Drawable3D {

	GeoSegment3D S;
	
	double dashLength;
	
	public DrawSegment3D(EuclidianView3D view, GeoSegment3D s){
		this.S=s;
		this.view3D=view;
		setGeoElement(s);
		
		update();
	}
	

	public void update() {
		
        isVisible = geo.isEuclidianVisible();       				 
        if (!isVisible) return;
		labelVisible = geo.isLabelVisible();    	
		
		
		
		GgbMatrix mc = S.getSegmentMatrix(0,1); 
		view3D.toScreenCoords3D(mc);
		
		matrix = mc.copy();
		
		
		
		dashLength = 0.12f/((float) S.getLength()); //TODO use object property

       
	}
	
		
	public void draw(EuclidianRenderer3D renderer){
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
		
    	for(float l=0; l<1;l+=2*dashLength){
    		l2 = l+dashLength;
    		if (l2>1) l2=1;
    		m = S.getSegmentMatrix(l,l2); 
    		view3D.toScreenCoords3D(m);
    		renderer.setMaterial(geo.getObjectColor(),1.0f);//TODO geo.getAlphaValue());
    		renderer.setMatrix(m.getGL());
    		renderer.drawCylinder(LINE3D_THICKNESS); 
    		
    		renderer.resetMatrix();
    	}

	} 
	
	
	public void drawTransp(EuclidianRenderer3D renderer){}
	public void drawHiding(EuclidianRenderer3D renderer){}
	
	
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
	};	
	
	
	public boolean isPicked(GgbVector pickLine, boolean repaint){ 
		
		
		return false; 
	};
	

}
