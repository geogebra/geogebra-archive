package geogebra3D.euclidian3D;



import geogebra.Matrix.Coords;
import geogebra.euclidian.Previewable;
import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.Functional2Var;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoPoint3D;

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
	 * @param point
	 */
	public DrawPoint3D(EuclidianView3D view3D, GeoPointND point) {     
		
		super(view3D, (GeoElement) point);
		
	}
	
	
	
	
	

	public void drawGeometry(Renderer renderer) {


		renderer.getGeometryManager().draw(getGeometryIndex());


	}


	

	
	public void drawGeometryHidden(Renderer renderer){

		drawGeometry(renderer);
	}	
	
	
	
	

	protected boolean updateForItSelf(){
		
		setColors();
		
		Renderer renderer = getView3D().getRenderer();
		
	

		PlotterSurface surface;

		surface = renderer.getGeometryManager().getSurface();
		
		
		/*
		surface.start(this);
		
		
		//number of vertices depends on point size
		int nb = 2+((GeoPointND) getGeoElement()).getPointSize();
				
		surface.setU((float) getMinParameter(0), (float) getMaxParameter(0));surface.setNbU(2*nb); 
		surface.setV((float) getMinParameter(1), (float) getMaxParameter(1));surface.setNbV(nb);
		surface.draw();
		*/

		
		surface.start();
		GeoPointND point = (GeoPointND) getGeoElement(); 
		surface.drawSphere(point.getPointSize(),point.getInhomCoordsInD(3), point.getPointSize()/getView3D().getScale()*1.5);
		
		
		setGeometryIndex(surface.end());
		
		return true;
	}
	
	protected void updateForView(){
		
		if (getView3D().viewChangedByZoom())
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
	
	

	public void addToDrawable3DLists(Drawable3DLists lists){
		addToDrawable3DLists(lists,DRAW_TYPE_POINTS);
	}
    
    public void removeFromDrawable3DLists(Drawable3DLists lists){
    	removeFromDrawable3DLists(lists,DRAW_TYPE_POINTS);
    }



	///////////////////////////////////
	// FUNCTION2VAR INTERFACE
	///////////////////////////////////
	
	








	public Coords evaluatePoint(double u, double v) {
		
		GeoPointND point = (GeoPointND) getGeoElement(); 
		
		double r = point.getPointSize()/getView3D().getScale()*1.5;
		Coords n = new Coords(new double[] {
				Math.cos(u)*Math.cos(v)*r,
				Math.sin(u)*Math.cos(v)*r,
				Math.sin(v)*r});
		
		return (Coords) n.add(point.getInhomCoordsInD(3));
	}


	

	public Coords evaluateNormal(double u, double v) {
		return new Coords(new double[] {
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





	

	protected float getLabelOffsetX(){
		//consistent with DrawPoint
		return super.getLabelOffsetX()+4;
	}

	protected float getLabelOffsetY(){
		//consistent with DrawPoint
		return super.getLabelOffsetY()-2*((GeoPointND) getGeoElement()).getPointSize();
	}
	
	
	

	
	protected double getColorShift(){
		return 0.86;//mostly sqrt(3)/2
	}
}
