package geogebra.euclidian3D;

import java.awt.Graphics2D;

import geogebra.euclidian.*;
import geogebra.kernel.GeoElement;
import geogebra.kernel3D.*;


// TODO delete final keyword from EuclidianView class declaration
// TODO change EuclidianView to EuclidianView3D in Application
public class EuclidianView3D 
	extends EuclidianView{
	
	
	//matrix for representation
	private double[] vx = {1,0,0};
	private double[] vy = {0,1,0};
	private double[] vz = {0,0,1};
	
	
	
	double a,b; //angles
	

	
	private static final long serialVersionUID = 1L;
	//TODO understanding what is serialVersionUID
	
	protected DrawableList drawPoint3DList = new DrawableList();
	

	public EuclidianView3D(EuclidianController3D ec, boolean[] showAxes,
			boolean showGrid) {
		super(ec, showAxes, showGrid);
		
		ec.setView(this);
		
		
		setRotXY(Math.PI/3,Math.PI/3,true);
	}
	
	
	
	/**
	 * Converts real world coordinates to screen coordinates.
	 * 
	 * @param inOut:
	 *            input and output array with x, y, z coords (z output for "z buffer" and so on)
	 */
	final public void toScreenCoords3D(double[] inOut) {
		
		//TODO matrix 3D -> 2D
		double x,y,z;
		x = inOut[0]*vx[0]+inOut[1]*vy[0]+inOut[2]*vz[0];
		y = inOut[0]*vx[1]+inOut[1]*vy[1]+inOut[2]*vz[1];
		z = inOut[0]*vx[2]+inOut[1]*vy[2]+inOut[2]*vz[2];
		
		
		//for the screen		
		inOut[0] = getXZero() + x * getXscale();
		inOut[1] = getYZero() - y * getYscale();

		// java drawing crashes for huge coord values
		if (Math.abs(inOut[0]) > MAX_SCREEN_COORD_VAL
				|| Math.abs(inOut[0]) > MAX_SCREEN_COORD_VAL) {
			inOut[0] = Double.NaN;
			inOut[1] = Double.NaN;
		}
	}
	
	/**
	 * adds a GeoElement3D to this view
	 */	
	public void add(GeoElement geo) {
		
		if (!geo.isGeoElement3D()){
			// call the EuclidianView method
			super.add(geo);
		}else{
			// process 3D
			Drawable d = null;
			d = createDrawable(geo);
			if (d != null) {
				addToDrawableLists(d);
				repaint();			
			}
		}
	}
	
	
	
	protected Drawable createDrawable(GeoElement geo) {
		Drawable d=null;

		if (!geo.isGeoElement3D()){
			// call the EuclidianView method
			d = super.createDrawable(geo);
		}else{		
			// process 3D 
			if (d == null){
	
				switch (geo.getGeoClassType()) {
				
				case GeoElement3D.GEO_CLASS_POINT3D:
					System.out.println("GEO_CLASS_POINT3D");
					d = new DrawPoint3D(this, (GeoPoint3D) geo);
					break;									
				}
				
				if (d != null) {			
					DrawableMap.put(geo, d);
				}
			
			}
		}
		
		
		return d;
	}
	
	
	protected void addToDrawableLists(Drawable d) {
		
		if (d == null) return;
		GeoElement geo = d.getGeoElement();
				
		if (!geo.isGeoElement3D()){
			// call the EuclidianView method
			super.addToDrawableLists(d);
		}else{		
			// process 3D 
			switch (geo.getGeoClassType()) {
			case GeoElement3D.GEO_CLASS_POINT3D:			
				drawPoint3DList.add(d);
				break;
			}

			if (d != null) {
				allDrawableList.add(d);			
			}
		}
	}
	
	
	
	/**
	 * Draws all GeoElement3Ds 
	 */
	protected void drawGeometricObjects(Graphics2D g2) {	
		
		/*
		if (previewDrawable != null) {
			previewDrawable.drawPreview(g2);
		}		
		*/
		
		super.drawGeometricObjects(g2);

		// draw points
		drawPoint3DList.drawAll(g2); 

	}
	
	
	
	
	


	
	/**
	 * set Matrix for view3D
	 */	
	public void setRotXY(double a, double b, boolean repaint){
		
		this.a = a;
		this.b = b;
		
		double ca = Math.cos(a);
		double sa = Math.sin(a);
		double cb = Math.cos(b);
		double sb = Math.sin(b);
		
		vx[0]=ca;
		vx[1]=cb*sa;
		vx[2]=sb*sa;
		
		vy[0]=sa;
		vy[1]=-cb*ca;
		vy[2]=-sb*ca;
		
		vz[0]=0;
		vz[1]=sb;
		vz[2]=-cb;
		
		
		if (repaint) {
			updateBackgroundImage();
			updateAllDrawables(repaint);
			//app.updateStatusLabelAxesRatio();
		}
		
		
		
	}
	
	
	

}
