package geogebra3D.euclidian3D;

import geogebra.euclidian.Previewable;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.euclidian3D.opengl.EuclidianRenderer3D;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoLine3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class DrawLine3D extends DrawCoordSys1D implements Previewable {

	
	
	public DrawLine3D(EuclidianView3D a_view3D, GeoLine3D a_line3D){
		
		super(a_view3D, a_line3D);
	}	
	
	
	
	
	public boolean update(){
		if (!super.update())
			return false;
		
		Ggb3DVector o = getView3D().getToScreenMatrix().mul(((GeoCoordSys1D) getGeoElement()).getOrigin());
		Ggb3DVector v = getView3D().getToScreenMatrix().mul(((GeoCoordSys1D) getGeoElement()).getVx());
		
				
		double[] minmax = getView3D().getRenderer().getIntervalInFrustum(
				new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
				o, v);
		
		drawMin = minmax[0];
		drawMax = minmax[1];
		
		return true;
	}
	
	
	
	

	/*
	public void drawGeometry(EuclidianRenderer3D renderer) {
		
		//renderer.setThickness(LINE3D_THICKNESS*getGeoElement().getLineThickness());
		super.drawGeometry(renderer);
		renderer.drawLine();
	}
	
	public void drawGeometryPicked(EuclidianRenderer3D renderer){
		
		//renderer.setThickness(LINE3D_THICKNESS*PICKED_DILATATION*getGeoElement().getLineThickness());
		super.drawGeometryPicked(renderer);
		renderer.drawLine(); 
	}
	
*/

	


	/*
	public int getPickOrder(){
		return DRAW_PICK_ORDER_1D;
	}

	*/
	
	
	
	
	////////////////////////////////
	// Previewable interface 
	
	
	//private ArrayList selectedPoints;

	public DrawLine3D(EuclidianView3D a_view3D, ArrayList selectedPoints){
		
		super(a_view3D,selectedPoints, new GeoLine3D(a_view3D.getKernel().getConstruction()));
		
		/*
		Kernel3D kernel = (Kernel3D) getView3D().getKernel();
		
		//kernel.setSilentMode(true);
		GeoLine3D line = new GeoLine3D(kernel.getConstruction());
		line.setIsPickable(false);
		setGeoElement(line);
		
		//kernel.setSilentMode(false);
		
		
		this.selectedPoints = selectedPoints;
		

		updatePreview();
		*/
		
	}	

	/*
	public void disposePreview() {
		// TODO Auto-generated method stub
		
	}


	public void drawPreview(Graphics2D g2) {
		// TODO Auto-generated method stub
		
	}


	public void updateMousePos(int x, int y) {
		
	}


	public void updatePreview() {
		
		//Application.debug("selectedPoints : "+selectedPoints);
		
		if (selectedPoints.size()==2){
			GeoPoint3D firstPoint = (GeoPoint3D) selectedPoints.get(0);
			GeoPoint3D secondPoint = (GeoPoint3D) selectedPoints.get(1);
			((GeoCoordSys1D) getGeoElement()).setCoordFromPoints(firstPoint.getCoords(), secondPoint.getCoords());
			getGeoElement().setEuclidianVisible(true);
		}else if (selectedPoints.size()==1){
			GeoPoint3D firstPoint = (GeoPoint3D) selectedPoints.get(0);
			GeoPoint3D secondPoint = getView3D().getPreviewPoint();
			((GeoCoordSys1D) getGeoElement()).setCoordFromPoints(firstPoint.getCoords(), secondPoint.getCoords());
			getGeoElement().setEuclidianVisible(true);
		}else{
			getGeoElement().setEuclidianVisible(false);
		}
			
	}
	
	*/

	
	

	

}
