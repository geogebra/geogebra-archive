package geogebra3D.euclidian3D;

import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoLine3D;

import java.awt.Color;

public class DrawLine3D extends Drawable3DSolid {

	
	double dashLength;
	
	
	public DrawLine3D(EuclidianView3D a_view3D, GeoLine3D a_line3D){
		
		super(a_view3D, a_line3D);
	}	
	
	
	
	

	
	public void drawGeometry(EuclidianRenderer3D renderer) {
		
		renderer.drawLine(LINE3D_THICKNESS*getGeoElement().getLineThickness());
	}
	
	public void drawGeometryPicked(EuclidianRenderer3D renderer){
		
		renderer.drawLine(LINE3D_THICKNESS*PICKED_DILATATION*getGeoElement().getLineThickness()); 
	}
	
	
	
	public void drawHidden(EuclidianRenderer3D renderer){
		
		if(!getGeoElement().isEuclidianVisible())
			return;
		
		
		double l2;
		GgbMatrix m; 
		GeoLine3D l_line3D = (GeoLine3D) getGeoElement();
		dashLength = 0.12f/((float) l_line3D.getUnit()); //TODO use object property
		
    	for(float l=-20; l<21;l+=2*dashLength){ //TODO use frustrum
    		l2 = l+dashLength;
    		if (l2>21) l2=21;
    		m = l_line3D.getSegmentMatrix(l,l2); 
    		//getView3D().toScreenCoords3D(m);
    		renderer.setMaterial(getGeoElement().getObjectColor(),1.0f);//TODO geo.getAlphaValue());
    		renderer.setMatrix(m);
    		renderer.drawSegment(LINE3D_THICKNESS*getGeoElement().getLineThickness()); 
    		
    	}

	} 


	


	public int getPickOrder(){
		return DRAW_PICK_ORDER_1D;
	}
	
	

	
	

	

}
