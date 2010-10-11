package geogebra3D.euclidian3D;



import geogebra.Matrix.GgbVector;
import geogebra.euclidian.Previewable;
import geogebra.kernel.arithmetic.Functional2Var;
import geogebra.kernel.kernel3D.GeoPoint3D;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;

import java.awt.Graphics2D;



//TODO does not extend Drawable3DCurves


/**
 * Class for drawing 3D points.
 * 
 * @author matthieu
 * 
 *
 */
public class DrawPoint3D extends Drawable3DCurves 
implements Previewable, Functional2Var{
	
	
	

	
		
	
	/**
	 * common constructor
	 * @param view3D
	 * @param point3D
	 */
	public DrawPoint3D(EuclidianView3D view3D, GeoPoint3D point3D) {     
		
		super(view3D, point3D);
		
	}
	
	
	
	
	

	public void drawGeometry(Renderer renderer) {


		renderer.getGeometryManager().draw(getGeometryIndex());
		/*
		GeoPoint3D point = (GeoPoint3D) getGeoElement(); 
		
		renderer.drawPoint(point.getPointSize());
		*/


	}


	

	
	public void drawGeometryHidden(Renderer renderer){

		drawGeometry(renderer);
	}	
	
	
	
	

	protected void updateForItSelf(){
		
		Renderer renderer = getView3D().getRenderer();
		
	

		PlotterSurface surface;

		surface = renderer.getGeometryManager().getSurface();
		surface.start(this);
		//number of vertices depends on point size
		int nb = 2+((GeoPoint3D) getGeoElement()).getPointSize();
		surface.setU((float) getMinParameter(0), (float) getMaxParameter(0));surface.setNbU(2*nb); 
		surface.setV((float) getMinParameter(1), (float) getMaxParameter(1));surface.setNbV(nb);
		surface.draw();
		setGeometryIndex(surface.end());
		
	}
	
	protected void updateForView(){
		
		updateForItSelf();
		//TODO only if zoom
		
	}
	
	
	
	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_0D;
	}	
	
	
	
	////////////////////////////////
	// Previewable interface 
	

	/**
	 * @param a_view3D
	 */
	public DrawPoint3D(EuclidianView3D a_view3D){
		
		super(a_view3D);
		
		setGeoElement(a_view3D.getCursor3D());
		
	}	

	public void disposePreview() {
		// TODO Auto-generated method stub
		
	}


	public void drawPreview(Graphics2D g2) {
		// TODO Auto-generated method stub
		
	}


	public void updateMousePos(double xRW, double yRW) {	
			
		
	}


	public void updatePreview() {

		
	}
	
	

	public int getType(){
		return DRAW_TYPE_POINTS;
	}




	///////////////////////////////////
	// FUNCTION2VAR INTERFACE
	///////////////////////////////////
	
	








	public GgbVector evaluatePoint(double u, double v) {
		
		GeoPoint3D point = (GeoPoint3D) getGeoElement(); 
		
		double r = point.getPointSize()/getView3D().getScale()*1.5;
		GgbVector n = new GgbVector(new double[] {
				Math.cos(u)*Math.cos(v)*r,
				Math.sin(u)*Math.cos(v)*r,
				Math.sin(v)*r});
		
		return (GgbVector) n.add(point.getInhomCoords());
	}


	

	public GgbVector evaluateNormal(double u, double v) {
		return new GgbVector(new double[] {
				Math.cos(u)*Math.cos(v),
				Math.sin(u)*Math.cos(v),
				Math.sin(v)});
	}




	public double getMinParameter(int index) {
		switch(index){
		case 0: //u
		default:
			return 0;
		case 1: //v
			return -Math.PI/2;
		}
	}


	public double getMaxParameter(int index) {
		switch(index){
		case 0: //u
		default:
			return 2*Math.PI; 
		case 1: //v
			return Math.PI/2;
		}
		
	}





	
	
	

}
