package geogebra3D.euclidian3D;




import java.awt.Color;

import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoPlane3D;




public class DrawPlane3D extends Drawable3DSurfaces {



	
	
	public DrawPlane3D(EuclidianView3D a_view3D, GeoPlane3D a_plane3D){
		
		super(a_view3D, a_plane3D);
	}
	
	
	

	public void drawGeometry(Renderer renderer) {
		GeoPlane3D p = (GeoPlane3D) getGeoElement();
		
		//renderer.setMaterial(getGeoElement().getObjectColor(),1);
		renderer.drawQuad(p.getXmin(),p.getYmin(),p.getXmax(),p.getYmax());
		
		/*
		renderer.setDash(EuclidianRenderer3D.DASH_SIMPLE); //TODO use object property
		renderer.setThickness(GRID3D_THICKNESS*getGeoElement().getLineThickness());
		if (p.hasGrid())
			renderer.drawGrid(p.getXmin(),p.getYmin(),
					p.getXmax(),p.getYmax(),
					p.getGridXd(),p.getGridYd());
					*/
					
	}
	
	
	public void drawGeometryHiding(Renderer renderer) {
		GeoPlane3D p = (GeoPlane3D) getGeoElement();
		renderer.drawQuad(p.getXmin(),p.getYmin(),p.getXmax(),p.getYmax());
	}
	
	
	
	public void drawGeometryPicked(Renderer renderer){}
	
	public void drawGeometryHidden(Renderer renderer){};
	
	
	public void drawHighlighting(Renderer renderer){

	};	
	
	

	
	

	

	
	/*
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
	
	*/

	
	

	

	
	public int getPickOrder(){
		//return DRAW_PICK_ORDER_MAX; //for now : plane xOy should not be treated as a plane, but a part of the drawing pad
		//TODO return DRAW_PICK_ORDER_2D;
		return DRAW_PICK_ORDER_2D;
	}		
	
	
	
	

}
