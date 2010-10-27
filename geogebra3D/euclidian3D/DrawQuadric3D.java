package geogebra3D.euclidian3D;

import java.util.ArrayList;

import geogebra.Matrix.GgbVector;
import geogebra.euclidian.Previewable;
import geogebra.kernel.CircularDefinitionException;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoQuadric3D;
import geogebra3D.kernel3D.GeoVector3D;

/**
 * Class for drawing quadrics.
 * @author matthieu
 *
 */
public class DrawQuadric3D extends Drawable3DSurfaces
implements Previewable {
	
	


	/**
	 * common constructor
	 * @param a_view3d
	 * @param a_quadric
	 */
	public DrawQuadric3D(EuclidianView3D a_view3d, GeoQuadric3D a_quadric) {
		
		super(a_view3d, a_quadric);
		
	}
	
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(getGeometryIndex());
	}

	void drawGeometryHiding(Renderer renderer) {
		drawGeometry(renderer);
	}

	public void drawGeometryHidden(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	public void drawGeometryPicked(Renderer renderer) {
		// TODO Auto-generated method stub

	}
	
	
	
	
	
	
	
	
	
	
	
	protected boolean updateForItSelf(){
		
		
		super.updateForItSelf();
		
		
		Renderer renderer = getView3D().getRenderer();
		
		

		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();
		GgbVector o;
		GgbVector v;
							
		double[] minmax;
		float min, max;
		float fade;// = (float) (50/getView3D().getScale());
		
			
		PlotterSurface surface;
		
		switch(quadric.getType()){
		case GeoQuadric3D.QUADRIC_SPHERE:
			surface = renderer.getGeometryManager().getSurface();
			surface.start(quadric);
			surface.setU((float) quadric.getMinParameter(0), (float) quadric.getMaxParameter(0));surface.setNbU(60); 
			surface.setV((float) quadric.getMinParameter(1), (float) quadric.getMaxParameter(1));surface.setNbV(30);
			surface.draw();
			setGeometryIndex(surface.end());
			
			break;
		case GeoQuadric3D.QUADRIC_CONE:
			
			
			o = getView3D().getToScreenMatrix().mul(quadric.getMidpoint());
			v = getView3D().getToScreenMatrix().mul(quadric.getEigenvec3D(2));
								
			minmax = getView3D().getRenderer().getIntervalInFrustum(
					new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
					o, v, true);
			
			min = (float) minmax[0]; 
			max = (float) minmax[1];		

			surface = renderer.getGeometryManager().getSurface();
			surface.start(quadric);
			surface.setU((float) quadric.getMinParameter(0), (float) quadric.getMaxParameter(0));surface.setNbU(60);
			
			//Application.debug("min, max ="+min+", "+max);
			fade = (max-min)/10f;
			if (min*max<0){
				surface.setV(min,0);surface.setNbV(2);surface.setVFading(fade, 0);surface.draw();
				surface.setV(0,max);surface.setNbV(2);surface.setVFading(0, fade);surface.draw();
			}else{
				surface.setV(min,max);surface.setNbV(3);surface.setVFading(fade, fade);surface.draw();
			}
			setGeometryIndex(surface.end());
			
			
			break;
			
			
		case GeoQuadric3D.QUADRIC_CYLINDER:
			
			o = getView3D().getToScreenMatrix().mul(quadric.getMidpoint());
			v = getView3D().getToScreenMatrix().mul(quadric.getEigenvec3D(2));
								
			minmax = getView3D().getRenderer().getIntervalInFrustum(
					new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
					o, v, true);
			
			
			
			min = (float) minmax[0]; 
			max = (float) minmax[1];		

			surface = renderer.getGeometryManager().getSurface();
			surface.start(quadric);
			surface.setU((float) quadric.getMinParameter(0), (float) quadric.getMaxParameter(0));surface.setNbU(60);
			
			//Application.debug("min, max ="+min+", "+max);
			
			fade = (max-min)/10f;
			surface.setV(min,max);surface.setNbV(3);surface.setVFading(fade, fade);surface.draw();
			
			setGeometryIndex(surface.end());
			
			break;

		}
		
		
		return true;
	}

	
	
	
	
	protected void updateForView(){
		updateForItSelf();
	}
	
	
	
	
	
	
	
	


	public int getPickOrder() {
		return DRAW_PICK_ORDER_2D;
	}
	

	public int getType(){
		switch(((GeoQuadric3D) getGeoElement()).getType()){
		case GeoQuadric3D.QUADRIC_SPHERE:
		case GeoQuadric3D.QUADRIC_CONE:
		case GeoQuadric3D.QUADRIC_CYLINDER:
			return DRAW_TYPE_CLOSED_SURFACES;
		default:
			return DRAW_TYPE_SURFACES;
		}
	}
	
	
	
	

	////////////////////////////////
	// Previewable interface 
	
	
	@SuppressWarnings("rawtypes")
	private ArrayList selectedPoints;

	/**
	 * constructor for previewable
	 * @param view3D
	 * @param selectedPoints
	 * @param cs1D
	 */
	@SuppressWarnings("unchecked")
	public DrawQuadric3D(EuclidianView3D view3D, ArrayList selectedPoints, int type){
		
		super(view3D);
		
		GeoQuadric3D q = new GeoQuadric3D(view3D.getKernel().getConstruction());
		setGeoElement(q);
		q.setIsPickable(false);
		q.setType(type);
		setGeoElement(q);
		
		this.selectedPoints = selectedPoints;
		
		updatePreview();
		
	}	

	




	public void updateMousePos(double xRW, double yRW) {	
		
	}


	public void updatePreview() {
		
		GeoPoint3D firstPoint = null;
		GeoPoint3D secondPoint = null;
		if (selectedPoints.size()>=1){
			firstPoint = (GeoPoint3D) selectedPoints.get(0);
			if (selectedPoints.size()==2)
				secondPoint = (GeoPoint3D) selectedPoints.get(1);
			else
				secondPoint = getView3D().getCursor3D();
		}
			
		
		if (selectedPoints.size()>=1){		
			((GeoQuadric3D) getGeoElement()).setSphereND(firstPoint, secondPoint);
			getGeoElement().setEuclidianVisible(true);
			setWaitForUpdate();
		}else{
			getGeoElement().setEuclidianVisible(false);
		}
		
			
	}

}
