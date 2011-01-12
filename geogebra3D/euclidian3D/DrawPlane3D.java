package geogebra3D.euclidian3D;




import geogebra.Matrix.CoordSys;
import geogebra.Matrix.CoordMatrix4x4;
import geogebra.Matrix.Coords;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.opengl.Textures;
import geogebra3D.kernel3D.GeoPlane3D;

import java.awt.Color;




/**
 * Class for drawing 3D planes.
 * @author matthieu
 *
 */
public class DrawPlane3D extends Drawable3DSurfaces {



	/** gl index of the grid */
	private int gridIndex = -1;

	
	
	/**
	 * Common constructor
	 * @param a_view3D
	 * @param a_plane3D
	 */
	public DrawPlane3D(EuclidianView3D a_view3D, GeoPlane3D a_plane3D){
		
		super(a_view3D, a_plane3D);
	}
	
	
	

	public void drawGeometry(Renderer renderer) {

		if (!((GeoPlane3D)getGeoElement()).isPlateVisible())
			return;
		//renderer.initMatrix();
		renderer.getGeometryManager().draw(getGeometryIndex());
		//renderer.resetMatrix();
		
	}
	
	
	public void drawGeometryHiding(Renderer renderer) {
		drawGeometry(renderer);
	}
	
	
	
	public void drawGeometryPicked(Renderer renderer){}
	

	public void drawHidden(Renderer renderer){
		//renderer.setMatrix(getMatrix());
		drawGeometryHidden(renderer);
	}; 

	public void drawGeometryHidden(Renderer renderer){ 
		if (!((GeoPlane3D)getGeoElement()).isGridVisible())
			return;
		//renderer.initMatrix();
		//dash
		renderer.getTextures().loadTextureNearest(Textures.DASH_SHORT);
		renderer.getGeometryManager().draw(gridIndex);
		//renderer.resetMatrix();
		
	};
	
	
	
	

	
	protected boolean updateForItSelf(){
		
		
		super.updateForItSelf();

		Renderer renderer = getView3D().getRenderer();
		GeoPlane3D geo = (GeoPlane3D) getGeoElement();
		CoordSys coordsys = geo.getCoordSys();
		
		// plane	
		PlotterSurface surface = renderer.getGeometryManager().getSurface();
		
		surface.start(geo);
		/*
		float dimension = (float) (200/getView3D().getScale()) * 2f;
		surface.setU(-dimension, dimension);surface.setNbU(2);
		surface.setV(-dimension, dimension);surface.setNbV(2);
		*/
		
		surface.setU((float) geo.getXmin(), (float) geo.getXmax());surface.setNbU(2);
		surface.setV((float) geo.getYmin(), (float) geo.getYmax());surface.setNbV(2);
		
		float fading;
		fading = (float) ((geo.getXmax()-geo.getXmin()) * 0.25);
		surface.setUFading(fading, fading);
		fading = (float) ((geo.getYmax()-geo.getYmin()) * 0.25);
		surface.setVFading(fading, fading);
		surface.draw();
		setGeometryIndex(surface.end());
		
		
		
		
		// grid
		removeGeometryIndex(gridIndex);
		
		PlotterBrush brush = renderer.getGeometryManager().getBrush();
		
		brush.start(8);
		brush.setThickness(getGeoElement().getLineThickness(),(float) getView3D().getScale());

		brush.setColor(Color.GRAY);
		
		//double dx = Math.max(geo.getGridXd(), geo.getGridYd()); //TODO
		double dx = Math.min(geo.getGridXd(), geo.getGridYd());
		double dy = dx; //TODO
		//along x axis
		brush.setAffineTexture(
				(float) ((0-geo.getYmin())/(geo.getYmax()-geo.getYmin())),
				0.25f);
		for(int i=(int) (geo.getYmin()/dy);i<=geo.getYmax()/dy;i++)
			brush.segment(coordsys.getPoint(geo.getXmin(),i*dy), 
					coordsys.getPoint(geo.getXmax(),i*dy));	
		//along y axis
		brush.setAffineTexture(
				(float) ((0-geo.getXmin())/(geo.getXmax()-geo.getXmin())),
				0.25f);
		for(int i=(int) (geo.getXmin()/dx);i<=geo.getXmax()/dx;i++)
			brush.segment(coordsys.getPoint(i*dx, geo.getYmin()), 
					coordsys.getPoint(i*dx, geo.getYmax()));
	
		gridIndex = brush.end();

		return true;
	}

	protected void updateForView(){
		
		/* TODO
		GgbCoordSys cs = ((GeoPlane3D) getGeoElement()).getCoordSys();
		
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
		
		double factor = 1.5;
		((GeoPlane3D) getGeoElement()).setGridCorners(
				xMinMax[0]*factor, 
				yMinMax[0]*factor, 
				xMinMax[1]*factor, 
				yMinMax[1]*factor);
		*/
		

		double l= (200/getView3D().getScale()) * 2f;
		((GeoPlane3D) getGeoElement()).setGridCorners(-l, -l, l, l);//TODO
		
		updateForItSelf();
	}

	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_2D;
	}		
	
	
	
	//TODO remove that
	public CoordMatrix4x4 getMatrix(){
		return CoordMatrix4x4.Identity();
	}
	
	

}
