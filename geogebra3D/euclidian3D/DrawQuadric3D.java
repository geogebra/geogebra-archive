package geogebra3D.euclidian3D;

import geogebra.Matrix.GgbVector;
import geogebra.kernel.arithmetic.Functional2Var;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoFunction2Var;
import geogebra3D.kernel3D.GeoQuadric3D;

public class DrawQuadric3D extends Drawable3DSurfaces {
	
	
	/** gl index of the quadric */
	private int quadricIndex = -1;


	public DrawQuadric3D(EuclidianView3D a_view3d, GeoQuadric3D a_quadric) {
		
		super(a_view3d, a_quadric);
		
	}
	
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(quadricIndex);
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
	
	
	
	
	
	
	
	
	
	
	
	protected void updateForItSelf(){
		
		
		super.updateForItSelf();
		
		
		Renderer renderer = getView3D().getRenderer();
		
		renderer.getGeometryManager().remove(quadricIndex);
		

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
			quadricIndex=surface.end();
			
			break;
		case GeoQuadric3D.QUADRIC_CONE:
			
			
			o = getView3D().getToScreenMatrix().mul(quadric.getMidpoint());
			v = getView3D().getToScreenMatrix().mul(quadric.getEigenvec3D(2));
								
			minmax = getView3D().getRenderer().getIntervalInFrustum(
					new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
					o, v);
			
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
			quadricIndex=surface.end();
			
			
			break;
			
			
		case GeoQuadric3D.QUADRIC_CYLINDER:
			
			o = getView3D().getToScreenMatrix().mul(quadric.getMidpoint());
			v = getView3D().getToScreenMatrix().mul(quadric.getEigenvec3D(2));
								
			minmax = getView3D().getRenderer().getIntervalInFrustum(
					new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
					o, v);
			
			/*
			brush = renderer.getGeometryManager().getBrush();
			

			brush.start(120);
			//brush.setColor(getGeoElement().getObjectColor());
			brush.setThickness((float) quadric.getHalfAxis(1));
			
			min = (float) (minmax[0]+(minmax[1]-minmax[0])*-3); //TODO change that
			max = (float) (minmax[1]+(minmax[1]-minmax[0])*3);
			
			brush.cylinder(quadric.getMidpoint(),quadric.getEigenvec3D(2), min, max);
			quadricIndex = brush.end();
			
			*/
			
			
			min = (float) minmax[0]; 
			max = (float) minmax[1];		

			surface = renderer.getGeometryManager().getSurface();
			surface.start(quadric);
			surface.setU((float) quadric.getMinParameter(0), (float) quadric.getMaxParameter(0));surface.setNbU(60);
			
			//Application.debug("min, max ="+min+", "+max);
			
			fade = (max-min)/10f;
			surface.setV(min,max);surface.setNbV(3);surface.setVFading(fade, fade);surface.draw();
			
			quadricIndex=surface.end();
			
			break;

		}
		
		
		
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

}
