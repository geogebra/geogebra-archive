package geogebra3D.old.euclidian3D;

import java.awt.Graphics2D;

import geogebra.euclidian.Drawable;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.GeoElement;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoSegment3D;
import geogebra3D.kernel3D.Linalg.GgbMatrix;
import geogebra3D.kernel3D.Linalg.GgbVector;


public class EuclidianView3D 
	extends EuclidianView{
	
	
	
	static final boolean DEBUG = false; //conditionnal compilation
	
	
	//matrix for representation
	private GgbMatrix m = GgbMatrix.Identity(4); 
	
	
	double a = 0.0;
	double b = 0.0; //angles
	

	
	private static final long serialVersionUID = 1L;
	
	protected DrawableList3D drawList3D = new DrawableList3D();
	

	public EuclidianView3D(EuclidianController3D ec, boolean[] showAxes,
			boolean showGrid) {
		super(ec, showAxes, showGrid);
		
		ec.setView(this);
		
		
		//setRotXY(Math.PI/3,Math.PI/3,true);
		//setRotXY(-Math.PI/6,Math.PI/6,true);
		setRotXY(0.0,0.0,true);
	}
	
	
	
	/**
	 * Converts real world coordinates to screen coordinates.
	 * 
	 * @param inOut:
	 *            input and output array with x, y, z coords (z output for "z buffer" and so on)
	 */
	final public void toScreenCoords3D(GgbVector vInOut) {
		
		GgbVector v1 = vInOut.getCoordsLast1();
		vInOut.set(m.mul(v1));

		// TODO java drawing crashes for huge coord values	
		/*
		if (Math.abs(vInOut.get(1)) > MAX_SCREEN_COORD_VAL
				|| Math.abs(vInOut.get(1)) > MAX_SCREEN_COORD_VAL) {
			vInOut.set(1,Double.NaN);
			vInOut.set(2,Double.NaN);
		}
		*/
		
	}

	
	final public void toScreenCoords3D(GgbMatrix mInOut) {
		
		GgbMatrix m1 = mInOut.copy();
		mInOut.set(m.mul(m1));
		
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
					if(DEBUG){System.out.println("GEO_CLASS_POINT3D");}
					d = new DrawPoint3D(this, (GeoPoint3D) geo);
					if(DEBUG){System.out.println("new DrawPoint3D");}
					break;									
								
				case GeoElement3D.GEO_CLASS_SEGMENT3D:
					if(DEBUG){System.out.println("GEO_CLASS_SEGMENT3D");}
					d = new DrawSegment3D(this, (GeoSegment3D) geo);
					//System.out.println("new DrawPoint3D");
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
		if(DEBUG){System.out.println(d);}
		if (!geo.isGeoElement3D()){
			// call the EuclidianView method
			super.addToDrawableLists(d);
		}else{		
			// process 3D 
			switch (geo.getGeoClassType()) {
			case GeoElement3D.GEO_CLASS_POINT3D:
				if(DEBUG){System.out.println("drawList3D.add(d)");}
				drawList3D.add(d);
				break;
			case GeoElement3D.GEO_CLASS_SEGMENT3D:
				if(DEBUG){System.out.println("drawList3D.add(d)");}
				drawList3D.add(d);
				break;
			}
			
			/*
			if (d != null) {
				allDrawableList.add(d);			
			}
			*/
		}
	}
	
	
	
	/**
	 * Draws all GeoElement3Ds 
	 */
	
	protected void drawLayers(Graphics2D g2, boolean isSVGExtensions) {

		// draw points 3D
		if(DEBUG){System.out.println("drawPoint3DList");}
		//drawList3D.drawAll(g2, -1); // draw all (without hidden parts)
		drawList3D.drawHiddenParts(g2); // draw with hidden parts
		
		//finally paint 2D elements - turn it on if wanted
		//super.drawLayers(g2,isSVGExtensions);
	}

	
	
	
	
	


	
	/**
	 * set Matrix for view3D
	 */	
	public void updateMatrix(){
		//rotations
		GgbMatrix m1 = GgbMatrix.Rotation3DMatrix(GgbMatrix.AXE_X, this.b + Math.PI/2.0);
		GgbMatrix m2 = GgbMatrix.Rotation3DMatrix(GgbMatrix.AXE_Z, this.a);
		GgbMatrix m3 = m1.mul(m2);
		
		//scaling TODO getZscale()
		GgbMatrix m4 = GgbMatrix.ScaleMatrix(new double[] {getXscale(),getYscale(),getXscale()});		
		
		//translation TODO getZZero()
		GgbMatrix m5 = GgbMatrix.TranslationMatrix(new double[] {getXZero(),getYZero(),0.0});
		
		m = m5.mul(m3.mul(m4));	
	}
	
	
	public void updateAllDrawables(boolean repaint){
		
		updateMatrix();
		super.updateAllDrawables(repaint);
	}
	
	
	
	public void setRotXY(double a, double b, boolean repaint){
		
		this.a = a;
		this.b = b;
		
		if (repaint) {
			updateBackgroundImage();
			drawList3D.updateAll();
			updateAllDrawables(repaint);
			
			//app.updateStatusLabelAxesRatio();
		}
		
		
	}
	
	
	
	
	//TODO drawAxes for 3D
	protected void drawAxes(Graphics2D g2) {}
	
	
	
	

}
