package geogebra3D.euclidian3D;

import geogebra.euclidian.Hits;
import geogebra.kernel.GeoElement;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra3D.euclidian3D.Drawable3D.drawableComparator;
import geogebra3D.kernel3D.GeoSegment3D;
import geogebra3D.kernel3D.GeoQuadric3D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * 3D hits (for picking, selection, ...)
 * @author matthieu
 *
 */
public class Hits3D extends Hits {
	

	private static final long serialVersionUID = 1L;
	
	/**
	 * class for tree set of drawable 3D
	 * @author matthieu
	 */
	private class TreeSetOfDrawable3D extends TreeSet<Drawable3D>{

		private static final long serialVersionUID = 1L;

		public TreeSetOfDrawable3D(drawableComparator drawableComparator) {
			super(drawableComparator);
		}
	}
	
	/** set of hits by picking order */
	private TreeSetOfDrawable3D[] hitSet = new TreeSetOfDrawable3D[Drawable3D.DRAW_PICK_ORDER_MAX];
	/** other hits */
	private TreeSetOfDrawable3D hitsOthers = new TreeSetOfDrawable3D(new Drawable3D.drawableComparator()); 
	/** label hits */
	private TreeSetOfDrawable3D hitsLabels = new TreeSetOfDrawable3D(new Drawable3D.drawableComparator()); 
	/** set of all the sets */
	private TreeSet<TreeSetOfDrawable3D> hitSetSet = new TreeSet<TreeSetOfDrawable3D>(new Drawable3D.setComparator()); 
	
	private Hits topHits = new Hits();

	/** number of coord sys 2D */
	private int cs2DCount;
	/** number of quadrics 2D */
	private int QuadCount;
	
	/**
	 * common constructor
	 */
	public Hits3D(){
		super();
		
		for (int i=0;i<Drawable3D.DRAW_PICK_ORDER_MAX;i++)
			hitSet[i] = new TreeSetOfDrawable3D(new Drawable3D.drawableComparator());
		
		// init counters
		cs2DCount = 0;
		QuadCount = 0;
	}
	
	
	
	public Hits3D clone() {

		Hits3D ret = (Hits3D) super.clone();
		ret.topHits = this.topHits.clone();
		ret.cs2DCount = cs2DCount;
		ret.QuadCount = QuadCount;
		
		// TreeSets are not cloned because they are only used when the hits are constructed

		return ret;
	} 
	
	
	
	public void add(GeoElement geo){
		
		if (geo instanceof GeoCoordSys2D) {
			cs2DCount++;
			//Application.debug("cs2DCount="+cs2DCount+"/"+(size()+1));
		}
		if (geo instanceof GeoQuadric3D) {
			QuadCount++;
		}
		
		super.add(geo);
	}
	
	
	public void init(){
		super.init();
		for (int i=0;i<Drawable3D.DRAW_PICK_ORDER_MAX;i++)
			hitSet[i].clear();
		hitsOthers.clear();
		hitsLabels.clear();
		
		topHits.init();
		
		
	}
	
	

	
	
	/** insert a drawable in the hitSet, called by EuclidianRenderer3D 
	 * @param d the drawable
	 * @param isLabel says if it's the label that is picked*/
	public void addDrawable3D(Drawable3D d, boolean isLabel){
		
		//Application.debug("isLabel = "+isLabel);
		
		if (isLabel)
			hitsLabels.add(d);
		//else{
		
		if(d.getPickOrder()<Drawable3D.DRAW_PICK_ORDER_MAX)
			hitSet[d.getPickOrder()].add(d);
		else
			hitsOthers.add(d);	
		
		
		
		
	}
	
	/** sort all hits in different sets */
	public void sort(){
				

		hitSetSet.clear();
		
		for (int i=0;i<Drawable3D.DRAW_PICK_ORDER_MAX;i++)
			hitSetSet.add(hitSet[i]);		

		
		for (Iterator<Drawable3D> iter = hitSetSet.first().iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			topHits.add(d.getGeoElement());
		}
		
		
		// sets the hits to this
		ArrayList<GeoElement> segmentList = new ArrayList<GeoElement>();
		
		for (Iterator<TreeSetOfDrawable3D> iterSet = hitSetSet.iterator(); iterSet.hasNext();) {
			TreeSetOfDrawable3D set = iterSet.next();
			for (Iterator<Drawable3D> iter = set.iterator(); iter.hasNext();) {
				Drawable3D d = (Drawable3D) iter.next();
				GeoElement geo = d.getGeoElement();
				this.add(geo);
				
				// add the parent of this if it's a segment from a GeoPolygon3D or GeoPolyhedron
				if (geo.isGeoSegment())
					segmentList.add(geo);
			}
		}
		
		// add the parent of this if it's a segment from a GeoPolygon3D or GeoPolyhedron
		/* TODO ?
		for (Iterator<GeoElement> iter = segmentList.iterator(); iter.hasNext();) {
			GeoSegment3D seg = (GeoSegment3D) iter.next();
			GeoElement parent = seg.getGeoParent();
			if (parent!=null)
				if (!this.contains(parent))
					this.add(seg.getGeoParent());				
		}
		*/
		
		//debug
		/*
		if (getLabelHit()==null)
			Application.debug(toString());
		else
			Application.debug(toString()+"\n first label : "+getLabelHit().getLabel());
		*/
		
	}
	
	
	
	
	
	
	
	
	
	public Hits getTopHits() {

		if (topHits.isEmpty())
			return clone();
		else
			return topHits;
		
	}
	
	public Hits getTopHits(int depth, int geoN) {
		Hits ret = new Hits();
		int depthCount = 0;
		int geoNCount = 0;
		for (Iterator<TreeSetOfDrawable3D> iterSet = hitSetSet.iterator(); 
		iterSet.hasNext() && depthCount < depth;) {
			TreeSetOfDrawable3D set = iterSet.next();
			if (set.size()>0)
				depthCount++;
			
			for (Iterator<Drawable3D> iter = set.iterator();
			iter.hasNext() && geoNCount < geoN;) {
				Drawable3D d = (Drawable3D) iter.next();
				GeoElement geo = d.getGeoElement();
				ret.add(geo);
				geoNCount++;
			}
		}
		return ret;	
	}
	
	/** return the first label hit, if one
	 * @return the first label hit
	 */
	public GeoElement getLabelHit(){
		
		if (hitsLabels.isEmpty())
			return null;
		else{
			GeoElement labelGeo = hitsLabels.first().getGeoElement();
			
			return labelGeo;
			
		}
	}
	
	
	
	
	/**
	 * remove all polygons, if hits are not all instance of GeoCoordSys2D
	 */
	public void removePolygonsIfNotOnlyCS2D(){
		
		//String s = "cs2DCount="+cs2DCount+"/"+(size());
		
		if (size() - cs2DCount > 0) {
			removePolygons();
			//s+="\n"+toString();
			/*
			for (int i = 0; i < size(); ) {
				GeoElement geo = (GeoElement) get(i);
				
				if (geo instanceof GeoCoordSys2D)
					remove(i);
				else
					i++;
			}
			*/
			//Application.debug(s+"\n"+toString());
		}
	}
	
	
	/**
	 * remove all polygons but one
	 */
	public void removeAllPolygonsButOne(){
		super.removeAllPolygonsButOne();
		topHits.clear(); //getTopHits() return this
	}
	
	public void removeAllPolygonsAndQuadricsButOne(){
		int toRemove = polyCount + QuadCount -1;
		for (int i = size() - 1 ; i >= 0 && toRemove>0; i-- ) {
			GeoElement geo = (GeoElement) get(i);
			if (geo.isGeoPolygon() || geo instanceof GeoQuadric3D){
				remove(i);
				toRemove--;
			}
		}
	}
	
	
	
}
