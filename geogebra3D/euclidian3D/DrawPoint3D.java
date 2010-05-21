package geogebra3D.euclidian3D;



import geogebra.Matrix.GgbVector;
import geogebra.euclidian.Previewable;
import geogebra.kernel.arithmetic.Functional2Var;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoPoint3D;

import java.awt.Graphics2D;



//TODO does not extend Drawable3DCurves

public class DrawPoint3D extends Drawable3DCurves 
implements Previewable, Functional2Var{
	
	
	
	/** gl index of the geometry */
	private int geometryIndex = -1;

	
		
	
	public DrawPoint3D(EuclidianView3D a_view3D, GeoPoint3D a_point3D) {     
		
		super(a_view3D, a_point3D);
		
	}
	
	
	
	
	

	public void drawGeometry(Renderer renderer) {


		renderer.getGeometryManager().draw(geometryIndex);
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
		
		renderer.getGeometryManager().remove(geometryIndex);


		PlotterSurface surface;

		surface = renderer.getGeometryManager().getSurface();
		surface.start(this);
		int nb = 4; //TODO change number of vertices regarding point size
		surface.setU((float) getMinParameter(0), (float) getMaxParameter(0));surface.setNbU(2*nb); 
		surface.setV((float) getMinParameter(1), (float) getMaxParameter(1));surface.setNbV(nb);
		surface.draw();
		geometryIndex=surface.end();
		
	}
	
	protected void updateForView(){
		
		updateForItSelf();
		
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
