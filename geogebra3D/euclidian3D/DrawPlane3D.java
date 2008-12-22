package geogebra3D.euclidian3D;




import java.awt.Color;

import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoPlane3D;




public class DrawPlane3D extends Drawable3DTransparent {



	
	
	public DrawPlane3D(EuclidianView3D a_view3D, GeoPlane3D a_plane3D){
		
		super(a_view3D, a_plane3D);
	}
	

	public void updateDrawingMatrix() {

		GeoPlane3D l_plane3D = (GeoPlane3D) getGeoElement();
		GgbMatrix l_matrix = l_plane3D.getDrawingMatrix(); 
		setMatrix(l_matrix);
		
       
	}
	
	

	public void drawGeometry(EuclidianRenderer3D renderer) {
		renderer.drawQuad();
	}
	
	public void drawGeometryPicked(EuclidianRenderer3D renderer){}
	

	
	
	public void drawPicked(EuclidianRenderer3D renderer){

	};	
	
	

	
	

	

	
	
	public void drawTransp(EuclidianRenderer3D renderer){
		if(!getGeoElement().isEuclidianVisible())
			return;
		
		renderer.setMaterial(getGeoElement().getObjectColor(),0.5f);//TODO geo.getAlphaValue());
		renderer.setMatrix(getMatrix());
		renderer.drawQuad();
		
		
		
		//grid
		GgbMatrix mc;
		GeoPlane3D l_plane3D = (GeoPlane3D) getGeoElement();
		
		for(double x=l_plane3D.getGridXmin();x<=l_plane3D.getGridXmax();x+=l_plane3D.getGridXd()){
			mc = l_plane3D.getDrawingXMatrix(x); 
			//getView3D().toScreenCoords3D(mc);
			renderer.setMatrix(mc);
			renderer.drawSegment(0.01f);			
		}
		
		for(double y=l_plane3D.getGridYmin();y<=l_plane3D.getGridYmax();y+=l_plane3D.getGridYd()){
			mc = l_plane3D.getDrawingYMatrix(y); 
			//getView3D().toScreenCoords3D(mc);
			renderer.setMatrix(mc);
			renderer.drawSegment(0.01f);		
		}
		
		
	}
	

	
	

	

	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_MAX; //for now : plane xOy should not be treated as a plane, but a part of the drawing pad
		//TODO return DRAW_PICK_ORDER_2D;
	}		
	
	
	
	

}
