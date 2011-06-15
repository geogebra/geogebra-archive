package geogebra3D.euclidian3D;




import geogebra.Matrix.Coords;
import geogebra.euclidian.Previewable;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.ConstructionDefaults3D;
import geogebra3D.kernel3D.GeoElement3DInterface;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoPolygon3D;
import geogebra3D.kernel3D.Kernel3D;

import java.util.ArrayList;
import java.util.Iterator;






/**
 * Class for drawing 3D polygons.
 * @author matthieu
 *
 */
public class DrawPolygon3D extends Drawable3DSurfaces implements Previewable {


	
	
	/**
	 * Common constructor
	 * @param a_view3D
	 * @param polygon
	 */
	public DrawPolygon3D(EuclidianView3D a_view3D, GeoPolygon polygon){
		
		super(a_view3D, polygon);
		
		
		
		

		
	}
	

	
	//drawing

	public void drawGeometry(Renderer renderer) {

		
		
		
		renderer.setLayer(getGeoElement().getLayer());

		/*
		renderer.startPolygonAndInitMatrix();
		GeoPolygon3D polygon = (GeoPolygon3D) getGeoElement();



		for(int i=0;i<polygon.getNumPoints();i++){
			renderer.addToPolygon(polygon.getPointX(i), polygon.getPointY(i));
		}

		renderer.endPolygonAndResetMatrix();
		*/
		
		renderer.drawPolygon(getGeometryIndex());
		
		renderer.setLayer(0);
			

	}
	public void drawGeometryPicked(Renderer renderer){
		drawGeometry(renderer);
	}
	public void drawGeometryHiding(Renderer renderer) {
		drawGeometry(renderer);
	}
	
	
	public void drawGeometryHidden(Renderer renderer){};
	
	
	
	
	
	public int getPickOrder(){
		/*
		Application.debug(alpha<1);
		if (alpha<1)
			return DRAW_PICK_ORDER_2D; //when transparent
		else*/
			return DRAW_PICK_ORDER_1D; //when not
	}	
	
	


	public void addToDrawable3DLists(Drawable3DLists lists){
		if (((GeoPolygon) getGeoElement()).isPartOfClosedSurface())
			addToDrawable3DLists(lists,DRAW_TYPE_CLOSED_SURFACES);
		else
			addToDrawable3DLists(lists,DRAW_TYPE_SURFACES);
	}
    
    public void removeFromDrawable3DLists(Drawable3DLists lists){
    	if (((GeoPolygon) getGeoElement()).isPartOfClosedSurface())
    		removeFromDrawable3DLists(lists,DRAW_TYPE_CLOSED_SURFACES); 
    	else
    		removeFromDrawable3DLists(lists,DRAW_TYPE_SURFACES);
    }
	
	
	protected boolean updateForItSelf(){
		
		super.updateForItSelf();
		
		
		
		//creates the polygon
		GeoPolygon polygon = (GeoPolygon) getGeoElement();
		int pointLength = polygon.getPointsLength();
		
		if (pointLength<3) //no polygon
			return true;
		
		if (Kernel.isZero(polygon.getArea())) //no polygon
			return true;
		
		
		Renderer renderer = getView3D().getRenderer();
		Coords v = polygon.getMainDirection();
		int index = renderer.startPolygon((float) v.get(1),(float) v.get(2),(float) v.get(3));
		
		
		
		// if index==0, no polygon have been created
		if (index==0)
			return true;
		
		setGeometryIndex(index);
				
		for(int i=0;i<pointLength;i++){
			//v = polygon.getPoint3D(i);
			v = polygon.getPoint3D(i);
			renderer.addToPolygon(v.get(1),v.get(2),v.get(3));
			/*
			Coords vInhom=v.getInhomCoordsInSameDimension();			
			renderer.addToPolygon(vInhom.get(1),vInhom.get(2),vInhom.get(3));
			Application.debug("v["+i+"]=\n"+v+"\ninhom=\n"+vInhom);
			*/
		}
		
		renderer.endPolygon();
				
		return true;
		
	}
	

	
	
	
	////////////////////////////////
	// Previewable interface 

	
	@SuppressWarnings("unchecked")
	private ArrayList selectedPoints;
	
	/** segments of the polygon preview */
	private ArrayList<DrawSegment3D> segments;
	
	@SuppressWarnings("unchecked")
	private ArrayList<ArrayList> segmentsPoints;
	

	/**
	 * Constructor for previewable
	 * @param a_view3D
	 * @param selectedPoints
	 */
	@SuppressWarnings("unchecked")
	public DrawPolygon3D(EuclidianView3D a_view3D, ArrayList selectedPoints){
		
		super(a_view3D);
		
		Kernel3D kernel = (Kernel3D) getView3D().getKernel();

		setGeoElement(new GeoPolygon3D(kernel.getConstruction(),null));
		
		getGeoElement().setObjColor(ConstructionDefaults3D.colPolygon3D);
		getGeoElement().setAlphaValue(ConstructionDefaults3D.DEFAULT_POLYGON3D_ALPHA);
		getGeoElement().setIsPickable(false);
		
		this.selectedPoints = selectedPoints;
		
		segments = new ArrayList<DrawSegment3D>();
		segmentsPoints = new ArrayList<ArrayList>();
		

		updatePreview();
		
	}	

	
	








	public void updateMousePos(double xRW, double yRW) {	
		// TODO Auto-generated method stub
		
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updatePreview() {
		
		

		int index =0;
		Iterator<ArrayList> spi = segmentsPoints.iterator();
		Iterator i = selectedPoints.iterator();
		GeoPointND point = null; // current point of the selected points
		ArrayList sp = null; // segment selected points
		
		// set points to existing segments points
		for (; i.hasNext() && spi.hasNext();){
			point = (GeoPointND) i.next();
			if (sp!=null)
				sp.add(point);	// add second point to precedent segment
			
			sp = spi.next(); 
			sp.clear();	
			sp.add(point);	// add first point to current segment			
		}
		
		// clear segments points if there are some more
		for (; spi.hasNext();){
			sp = spi.next(); 
			sp.clear();
		}
		
		
		// set points to new segments points
		for (; i.hasNext() ;){
			if (sp!=null && point!=null)
				sp.add(point);	// add second point to precedent segment
			
			sp = new ArrayList();
			segmentsPoints.add(sp);
			point = (GeoPointND) i.next();
			sp.add(point);
			DrawSegment3D s = new DrawSegment3D(getView3D(),sp);
			s.getGeoElement().setObjColor(ConstructionDefaults3D.colPolygon3D);
			segments.add(s);
			getView3D().addToDrawable3DLists(s);
		}
		
		// update segments
		for (Iterator<DrawSegment3D> s = segments.iterator(); s.hasNext();)
			s.next().updatePreview();
		
		
		
		//Application.debug("DrawList3D:\n"+getView3D().getDrawList3D().toString());
		
		
		// polygon itself
		
		if (selectedPoints.size()<2){
			getGeoElement().setEuclidianVisible(false);
			return;
		}
		

		
		getGeoElement().setEuclidianVisible(true);
		
		GeoPointND[] points = new GeoPointND[selectedPoints.size()+1];
		
		index =0;
		for (Iterator p = selectedPoints.iterator(); p.hasNext();){
			points[index]= (GeoPointND) p.next();
			index++;
		}
		
		points[index] = getView3D().getCursor3D();
			
		//sets the points of the polygon
		((GeoPolygon3D) getGeoElement()).setPoints(points,null,false);
		//check if all points are on the same plane
		((GeoPolygon3D) getGeoElement()).updateCoordSys();
		if (getGeoElement().isDefined())
			setWaitForUpdate();
		
		
	}
	
	public void disposePreview() {
		super.disposePreview();
		
		// dispose segments
		for (Iterator<DrawSegment3D> s = segments.iterator(); s.hasNext();)
			s.next().disposePreview();

		
	}


	

}
