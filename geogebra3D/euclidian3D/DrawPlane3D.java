package geogebra3D.euclidian3D;




import java.awt.Color;

import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.Brush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.opengl.Surface;
import geogebra3D.euclidian3D.opengl.Textures;
import geogebra3D.kernel3D.GeoCoordSys;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoCoordSysAbstract;
import geogebra3D.kernel3D.GeoFunction2Var;
import geogebra3D.kernel3D.GeoPlane3D;




public class DrawPlane3D extends Drawable3DSurfaces {


	/** gl index of the plane */
	private int planeIndex = -1;
	/** gl index of the grid */
	private int gridIndex = -1;

	
	
	public DrawPlane3D(EuclidianView3D a_view3D, GeoPlane3D a_plane3D){
		
		super(a_view3D, a_plane3D);
	}
	
	
	

	public void drawGeometry(Renderer renderer) {

		if (!((GeoPlane3D)getGeoElement()).isPlateVisible())
			return;
		renderer.initMatrix();
		renderer.getGeometryManager().draw(planeIndex);
		renderer.resetMatrix();
		
	}
	
	
	public void drawGeometryHiding(Renderer renderer) {
		drawGeometry(renderer);
	}
	
	
	
	public void drawGeometryPicked(Renderer renderer){}
	

	public void drawHidden(Renderer renderer){
		renderer.setMatrix(getMatrix());
		drawGeometryHidden(renderer);
	}; 

	public void drawGeometryHidden(Renderer renderer){ 
		if (!((GeoPlane3D)getGeoElement()).isGridVisible())
			return;
		renderer.initMatrix();
		//dash
		renderer.getTextures().setTexture(Textures.DASH_SHORT);
		renderer.getGeometryManager().draw(gridIndex);
		renderer.resetMatrix();
		
	};
	
	
	
	

	
	protected void updateForItSelf(){
		
		
		super.updateForItSelf();

		Renderer renderer = getView3D().getRenderer();
		GeoPlane3D geo = (GeoPlane3D) getGeoElement();
		
		// plane
		renderer.getGeometryManager().remove(planeIndex);	
		
		
		Surface surface = renderer.getGeometryManager().getSurface();
		
		/*
		planeIndex = renderer.getGeometryManager().newPlane(
				geo.getObjectColor(),
				alpha,
				(float) (200/getView3D().getScale()));
		*/
		surface.start(geo);
		float dimension = (float) (200/getView3D().getScale()) * 1.5f;
		surface.setU(-dimension, dimension);surface.setDeltaU(2*dimension);
		surface.setV(-dimension, dimension);surface.setDeltaV(2*dimension);
		float fading = dimension * 0.75f;
		surface.setFading(fading, fading);
		surface.draw();
		planeIndex=surface.end();
		
		
		
		
		// grid
		renderer.getGeometryManager().remove(gridIndex);
		
		Brush brush = renderer.getGeometryManager().getBrush();
		
		brush.start(8);
		brush.setThickness(getGeoElement().getLineThickness(),(float) getView3D().getScale());

		brush.setColor(Color.GRAY);
		
		double l=10*200/getView3D().getScale();
		geo.setGridCorners(-l, -l, l, l);//TODO
		//double dx = Math.max(geo.getGridXd(), geo.getGridYd()); //TODO
		double dx = Math.min(geo.getGridXd(), geo.getGridYd());
		double dy = dx; //TODO
		//along x axis
		brush.setAffineTexture(
				(float) ((0-geo.getYmin())/(geo.getYmax()-geo.getYmin())),
				0.25f);
		for(int i=(int) (geo.getYmin()/dy);i<=geo.getYmax()/dy;i++)
			brush.segment(geo.getPoint(geo.getXmin(),i*dy), 
					geo.getPoint(geo.getXmax(),i*dy));	
		//along y axis
		brush.setAffineTexture(
				(float) ((0-geo.getXmin())/(geo.getXmax()-geo.getXmin())),
				0.25f);
		for(int i=(int) (geo.getXmin()/dx);i<=geo.getXmax()/dx;i++)
			brush.segment(geo.getPoint(i*dx, geo.getYmin()), 
					geo.getPoint(i*dx, geo.getYmax()));
	
		gridIndex = brush.end();

		/*
		if (geo.isGridVisible()){
			gridIndex = renderer.getGeometryManager().newGrid(
					Color.BLACK, 1f,
					//(float) (200/getView3D().getScale()),
					(float) geo.getXmin(), (float) geo.getXmax(),
					(float) geo.getYmin(), (float) geo.getYmax(),
					(float) geo.getGridXd(), (float) geo.getGridYd(), 
					(float) (0.4/getView3D().getScale()));
		}
		*/
		
	}

	protected void updateForView(){
		
		GeoCoordSysAbstract cs = ((GeoCoordSys) getGeoElement()).getCoordSys();
		
		GgbVector o = getView3D().getToScreenMatrix().mul(cs.getOrigin());
		GgbVector vx = getView3D().getToScreenMatrix().mul(cs.getVx());
		GgbVector vy = getView3D().getToScreenMatrix().mul(cs.getVy());
		
				
		double[] xMinMax = getView3D().getRenderer().getIntervalInFrustum(
				new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
				o, vx);
		double[] yMinMax = getView3D().getRenderer().getIntervalInFrustum(
				new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
				o, vy);
		
		//Application.debug("corners : "+xMinMax[0]+","+yMinMax[0]+" -- "+xMinMax[1]+","+yMinMax[1]);
		
		((GeoPlane3D) getGeoElement()).setGridCorners(xMinMax[0], yMinMax[0], xMinMax[1], yMinMax[1]);
		
		updateForItSelf();
	}

	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_2D;
	}		
	
	
	
	//TODO remove that
	public GgbMatrix4x4 getMatrix(){
		return GgbMatrix4x4.Identity();
	}
	
	

}
