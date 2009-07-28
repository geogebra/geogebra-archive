package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoSegmentInterface;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * 
 * class for hitting objects with the mouse
 * 
 * @author Markus Hohenwarter
 * @version
 */

//TODO change ArrayList to TreeSet 

public class Hits extends ArrayList {
	
	
	private int listCount;
	private int polyCount;
	private int imageCount;
	
	
	protected final int TEST_MOVEABLE = 1;
	protected final int TEST_ROTATEMOVEABLE = 2;

	
	
	

	/** init the hits */
	public void init(){
		clear();
		listCount = 0;
		polyCount = 0;
		imageCount = 0;
		
	}
	
	
	
	public Hits clone() {

		Hits ret = (Hits) super.clone();
		ret.listCount = this.listCount;
		ret.polyCount = this.polyCount;
		ret.imageCount = this.imageCount;

		return ret;
	} 
	
	/** adding specifics GeoElements */
	public void add(GeoElement geo){
		if (geo.isGeoList()) {
			listCount++;
		} else if (geo.isGeoImage()) {
			imageCount++;
		} else if (geo.isGeoPolygon()) {
			polyCount++;
		} 
		super.add(geo);		
	}
	

	public int getImageCount(){
		return imageCount;
	}
	
	public int getListCount(){
		return listCount;
	}	
	
	/**
	 * returns GeoElement whose label is at screen coords (x,y).
	 */
	/*
	final public GeoElement getLabelHit(Point p) {
		if (!app.isLabelDragsEnabled()) return null;
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			if (d.hitLabel(p.x, p.y)) {
				GeoElement geo = d.getGeoElement();
				if (geo.isEuclidianVisible())
					return geo;
			}
		}
		return null;
	}
	*/
	

	// replaces final public ArrayList getHitsForNewPointMode(ArrayList hits) {	
	/**
	 * Returns hits that are suitable for new point mode.
	 * A polygon is only kept if one of its sides is also in
	 * hits.
	 */
	final public void keepOnlyHitsForNewPointMode() {	
	
		Iterator it = this.iterator();
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();
			if (geo.isGeoPolygon()) {
				boolean sidePresent = false;
				GeoSegmentInterface [] sides = ((GeoPolygon) geo).getSegments();
				for (int k=0; k < sides.length; k++) {
					if (this.contains(sides[k])) {
						sidePresent = true;
						break;
					}
				}
				
				if (!sidePresent)
					it.remove();					
			}				
		}				
				
	}

	// replaces final public ArrayList getPointVectorNumericHits(Point p) {
	final public Hits getPointVectorNumericHits(){

		Hits ret = new Hits();
		for (int i = 0; i < size(); ++i) {
			GeoElement geo = (GeoElement) get(i);
			if (
					//geo.isGeoNumeric() ||
					 geo.isGeoVector()
					|| geo.isGeoPoint())
				ret.add(geo);
		}
		
		return ret;
	}
	
	//replaces EuclidianView . final public ArrayList getHits(Point p, boolean includePolygons) {
	/**
	 * removes all polygons
	 */
	final public void removePolygons(){
		if (size() - polyCount > 0) {
			for (int i = 0; i < size(); ++i) {
				GeoElement geo = (GeoElement) get(i);
				if (geo.isGeoPolygon())
					remove(i);
			}
		}
	}
	
	
	
	
	
	
	


	/**
	 * returns array of independent GeoElements whose visual representation is
	 * at streen coords (x,y). order: points, vectors, lines, conics
	 */
	/*
	final public ArrayList getMoveableHits(Point p) {
		return getMoveableHits(getHits(p));
	}
	*/

	/**
	 * returns array of changeable GeoElements out of hits
	 */
	final public Hits getMoveableHits() {
		return getMoveables(TEST_MOVEABLE, null);
	}

	/**
	 * returns array of changeable GeoElements out of hits that implement
	 * PointRotateable
	 */
	final public Hits getPointRotateableHits(GeoPointInterface rotCenter) {
		return getMoveables(TEST_ROTATEMOVEABLE, rotCenter);
	}


	protected Hits getMoveables(int test, GeoPointInterface rotCenter) {


		GeoElement geo;
		Hits moveableList = new Hits();
		for (int i = 0; i < size(); ++i) {
			geo = (GeoElement) get(i);
			switch (test) {
			case TEST_MOVEABLE:
				// moveable object
				if (geo.isMoveable()) {
					moveableList.add(geo);
					//Application.debug("moveable GeoElement = "+geo);
				}
				// point with changeable parent coords
				else if (geo.isGeoPoint()) {
					GeoPointInterface point = (GeoPointInterface) geo;
					if (point.hasChangeableCoordParentNumbers())
						moveableList.add(point);
				}
				// not a point, but has moveable input points
				else if (geo.hasMoveableInputPoints()) {
					moveableList.add(geo);
				}
				break;

			case TEST_ROTATEMOVEABLE:
				// check for circular definition
				if (geo.isRotateMoveable()) {
					if (rotCenter == null || !geo.isParentOf((GeoElement) rotCenter))
						moveableList.add(geo);
				}

				break;
			}
		}
		
		/*
		if (moveableList.size() == 0)
			return null;
		else
			return moveableList;
			*/
		return moveableList;
	}


	/**
	 * returns array of GeoElements of type geoclass whose visual representation
	 * is at streen coords (x,y). order: points, vectors, lines, conics
	 */
	/*
	final public ArrayList getHits(Point p, Class geoclass, ArrayList result) {
		return getHits(getHits(p), geoclass, false, result);
	}
	*/

	/**
	 * returns array of GeoElements NOT of type geoclass out of hits
	 */
	final public Hits getOtherHits(Class geoclass,
			Hits result) {
		return getHits(geoclass, true, result);
	}

	final public Hits getHits(Class geoclass,
			Hits result) {
		return getHits(geoclass, false, result);
	}

	/**
	 * Returns array of polygons with n points out of hits.
	 * 
	 * @return
	 *
	final public ArrayList getPolygons(ArrayList hits, int n, ArrayList polygons) {
		// search for polygons in hits that exactly have the needed number of
		// points
		polygons.clear();
		getHits(hits, GeoPolygon.class, polygons);
		for (int k = polygons.size() - 1; k > 0; k--) {
			GeoPolygon poly = (GeoPolygon) polygons.get(k);
			// remove poly with wrong number of points
			if (n != poly.getPoints().length)
				polygons.remove(k);
		}
		return polygons;
	}*/

	/**
	 * Stores all GeoElements of type geoclass to result list.
	 * 
	 * @param other ==
	 *            true: returns array of GeoElements NOT of type geoclass out of
	 *            hits.
	 */
	final protected Hits getHits(Class geoclass,
			boolean other, Hits result) {


		result.clear();
		for (int i = 0; i < size(); ++i) {
			boolean success = geoclass.isInstance(get(i));
			if (other)
				success = !success;
			if (success)
				result.add(get(i));
		}
		//return result.size() == 0 ? null : result;
		
		return result;
	}

	/**
	 * Stores all GeoElements of type GeoPoint, GeoVector, GeoNumeric to result list.
	 * 
	 */
	/*
	final protected ArrayList getRecordableHits(ArrayList hits, ArrayList result) {
		if (hits == null)
			return null;

		result.clear();
		for (int i = 0; i < hits.size(); ++i) {
			GeoElement hit = (GeoElement)hits.get(i);
			boolean success = (hit.isGeoPoint() || hit.isGeoVector() || hit.isGeoNumeric());
			if (success)
				result.add(hits.get(i));
		}
		return result.size() == 0 ? null : result;
	}
	*/

	/**
	 * returns array of GeoElements whose visual representation is on top of
	 * screen coords of Point p. If there are points at location p only the
	 * points are returned. Otherwise all GeoElements are returned.
	 * 
	 * @see EuclidianController: mousePressed(), mouseMoved()
	 */
	/*
	final public ArrayList getTopHits(Point p) {
		return getTopHits(getHits(p));
	}
	*/

	/**
	 * if there are GeoPoints in hits, all these points are returned. Otherwise
	 * hits is returned.
	 * 
	 * @see EuclidianController: mousePressed(), mouseMoved()
	 */
	public Hits getTopHits() {
		
		if (isEmpty())
			return clone();
		
		// point in there?
		Hits topHitsList = new Hits();
		if (containsGeoPoint(topHitsList)) {
			//Hits topHitsList = new Hits();
			//getHits(GeoPoint.class, false, topHitsList);
			return topHitsList;
		} else
			return clone();
	}


	final public boolean containsGeoPoint() {

		for (int i = 0; i < size(); i++) {
			if (((GeoElement) get(i)).isGeoPoint())
				return true;
		}
		return false;
	}

	
	final public boolean containsGeoPoint(Hits ret) {

		GeoElement geo;
		for (int i = 0; i < size(); i++) {
			geo = (GeoElement) get(i);
			if (geo.isGeoPoint()){
				ret.add(geo);
				return true;
			}
		}
		return false;
	}
	
	
	
	public String toString(){
		String s = "hits: "+size();
		GeoElement geo;
		for (int i = 0; i < size(); i++) {
			geo = (GeoElement) get(i);
			s+="\n hits("+i+") = "+geo.getLabel();
		}
		return s;
	}
	

}
