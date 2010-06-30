package geogebra3D.euclidian3D;




import geogebra.Matrix.GgbVector;
import geogebra.euclidian.Previewable;
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


	/** gl index of the polygon */
	private int polygonIndex = -1;
	
	
	
	/**
	 * Common constructor
	 * @param a_view3D
	 * @param a_polygon3D
	 */
	public DrawPolygon3D(EuclidianView3D a_view3D, GeoPolygon3D a_polygon3D){
		
		super(a_view3D, a_polygon3D);
		
		
		
		

		
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
		
		renderer.drawPolygon(polygonIndex);
		
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
		return DRAW_PICK_ORDER_2D;
	}	
	
	

	public int getType(){
		if (((GeoPolygon3D) getGeoElement()).isPartOfClosedSurface())
			return DRAW_TYPE_CLOSED_SURFACES;
		else
			return DRAW_TYPE_SURFACES;
	}

	
	
	
	protected void updateForItSelf(){
		
		
		super.updateForItSelf();
		
		/*
		if (renderer == null)
			return;
			*/
		
		Renderer renderer = getView3D().getRenderer();
		
		renderer.removePolygon(polygonIndex);
		
		//creates the polygon
		GeoPolygon3D polygon = (GeoPolygon3D) getGeoElement();
		
		GgbVector v = polygon.getViewDirection();
		//Application.debug("normal\n"+v.toString());
		
		int index = renderer.startPolygon((float) v.get(1),(float) v.get(2),(float) v.get(3));
		
		
		
		// if index==0, no polygon have been created
		if (index==0)
			return;
		
		//Application.debug("udpate polygon index : "+polygonIndex+" >> "+index);
		
		polygonIndex = index;
		
		
		
				
		for(int i=0;i<polygon.getNumPoints();i++){
			v = polygon.getPoint3D(i);
			//Application.debug("point["+i+"]\n"+v.toString());
			//renderer.addToPolygon(polygon.getPointX(i), polygon.getPointY(i));
			renderer.addToPolygon(v.get(1),v.get(2),v.get(3));
		}
		
		renderer.endPolygon();
		
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
		((GeoElement3DInterface) getGeoElement()).setIsPickable(false);
		
		this.selectedPoints = selectedPoints;
		
		segments = new ArrayList<DrawSegment3D>();
		segmentsPoints = new ArrayList<ArrayList>();
		

		updatePreview();
		
	}	

	
	








	public void updateMousePos(double xRW, double yRW) {	
		// TODO Auto-generated method stub
		
	}



	@SuppressWarnings("unchecked")
	public void updatePreview() {
		
		

		int index =0;
		Iterator<ArrayList> spi = segmentsPoints.iterator();
		Iterator i = selectedPoints.iterator();
		GeoPoint3D point = null; // current point of the selected points
		ArrayList sp = null; // segment selected points
		
		// set points to existing segments points
		for (; i.hasNext() && spi.hasNext();){
			point = (GeoPoint3D) i.next();
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
			point = (GeoPoint3D) i.next();
			sp.add(point);
			DrawSegment3D s = new DrawSegment3D(getView3D(),sp);
			s.getGeoElement().setObjColor(ConstructionDefaults3D.colPolygon3D);
			segments.add(s);
			getView3D().getDrawList3D().add(s);
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
		
		GeoPoint3D[] points = new GeoPoint3D[selectedPoints.size()+1];
		
		index =0;
		//String s="points = ";
		for (Iterator p = selectedPoints.iterator(); p.hasNext();){
			points[index]= (GeoPoint3D) p.next();
			//s+=points[index].getLabel()+", ";
			index++;
		}
		
		points[index] = getView3D().getCursor3D();
		//Application.debug(s);
			
		((GeoPolygon3D) getGeoElement()).setPoints(points,null,false);
		
		//setWaitForUpdate();
		
		
	}
	
	public void disposePreview() {
		super.disposePreview();
		
		// dispose segments
		for (Iterator<DrawSegment3D> s = segments.iterator(); s.hasNext();)
			s.next().disposePreview();

		
	}


	

}
