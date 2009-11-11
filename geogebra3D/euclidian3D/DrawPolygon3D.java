package geogebra3D.euclidian3D;




import geogebra.euclidian.Previewable;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoPolygon3D;
import geogebra3D.kernel3D.Kernel3D;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;




public class DrawPolygon3D extends Drawable3DSurfaces implements Previewable {


	
	
	public DrawPolygon3D(EuclidianView3D a_view3D, GeoPolygon3D a_polygon3D){
		
		super(a_view3D, a_polygon3D);
	}
	

	
	//drawing

	public void drawGeometry(Renderer renderer) {

		
		
		renderer.setLayer(getGeoElement().getLayer());

		renderer.startPolygonAndInitMatrix();
		GeoPolygon3D polygon = (GeoPolygon3D) getGeoElement();



		for(int i=0;i<polygon.getNumPoints();i++){
			renderer.addToPolygon(polygon.getPointX(i), polygon.getPointY(i));
		}

		renderer.endPolygonAndResetMatrix();
		
		renderer.setLayer(0);
			

	}
	public void drawGeometryPicked(Renderer renderer){
		drawGeometry(renderer);
	}
	public void drawGeometryHiding(Renderer renderer) {
		drawGeometry(renderer);
	}
	
	
	public void drawGeometryHidden(Renderer renderer){};
	
	
	
	
	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_2D;
	}	
	
	

	public int getType(){
		if (((GeoPolygon3D) getGeoElement()).isPartOfClosedSurface())
			return DRAW_TYPE_CLOSED_SURFACES;
		else
			return DRAW_TYPE_SURFACES;
	}

	
	
	
	
	
	////////////////////////////////
	// Previewable interface 

	
	private ArrayList selectedPoints;

	public DrawPolygon3D(EuclidianView3D a_view3D, ArrayList selectedPoints){
		
		super(a_view3D);
		
		Kernel3D kernel = (Kernel3D) getView3D().getKernel();

		setGeoElement(new GeoPolygon3D(kernel.getConstruction(),null));
		
		this.selectedPoints = selectedPoints;
		

		updatePreview();
		
	}	

	
	


	public void disposePreview() {
		// TODO Auto-generated method stub
		
	}



	public void drawPreview(Graphics2D g2) {
		// TODO Auto-generated method stub
		
	}



	public void updateMousePos(int x, int y) {
		// TODO Auto-generated method stub
		
	}



	public void updatePreview() {
		
		if (selectedPoints.size()<3)
			getGeoElement().setEuclidianVisible(false);
		else
			getGeoElement().setEuclidianVisible(true);
		
		GeoPoint3D[] points = new GeoPoint3D[selectedPoints.size()];
		
		int index =0;
		//String s="points = ";
		for (Iterator p = selectedPoints.iterator(); p.hasNext();){
			points[index]= (GeoPoint3D) p.next();
			//s+=points[index].getLabel()+", ";
			index++;
		}
		//Application.debug(s);
			
		((GeoPolygon3D) getGeoElement()).setPoints(points);
		
	}
	
	

	

}
