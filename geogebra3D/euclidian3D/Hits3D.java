package geogebra3D.euclidian3D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import geogebra.euclidian.Hits;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;
import geogebra3D.kernel3D.GeoSegment3D;

public class Hits3D extends Hits {
	
	/** set of hits by picking order */
	private TreeSet[] hitSet = new TreeSet[Drawable3D.DRAW_PICK_ORDER_MAX];
	/** other hits */
	private TreeSet hitsOthers = new TreeSet(new Drawable3D.drawableComparator()); 
	/** label hits */
	private TreeSet hitsLabels = new TreeSet(new Drawable3D.drawableComparator()); 
	/** set of all the sets */
	private TreeSet hitSetSet = new TreeSet(new Drawable3D.setComparator()); 
	
	Hits topHits = new Hits();

	
	public Hits3D(){
		super();
		
		// initing hitSet
		for (int i=0;i<Drawable3D.DRAW_PICK_ORDER_MAX;i++)
			hitSet[i] = new TreeSet(new Drawable3D.drawableComparator());
	}
	
	
	
	public Hits3D clone() {

		Hits3D ret = (Hits3D) super.clone();
		ret.topHits = this.topHits.clone();
		
		// TreeSets are not cloned because they are only used when the hits are constructed

		return ret;
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

		
		for (Iterator iter = ((TreeSet) hitSetSet.first()).iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			topHits.add(d.getGeoElement());
		}
		
		
		// sets the hits to this
		ArrayList segmentList = new ArrayList();
		
		for (Iterator iterSet = hitSetSet.iterator(); iterSet.hasNext();) {
			TreeSet set = (TreeSet) iterSet.next();
			for (Iterator iter = set.iterator(); iter.hasNext();) {
				Drawable3D d = (Drawable3D) iter.next();
				GeoElement geo = d.getGeoElement();
				this.add(geo);
				
				// add the parent of this if it's a segment from a GeoPolygon3D or GeoPolyhedron
				if (geo.isGeoSegment())
					segmentList.add(geo);
			}
		}
		
		// add the parent of this if it's a segment from a GeoPolygon3D or GeoPolyhedron
		for (Iterator iter = segmentList.iterator(); iter.hasNext();) {
			GeoSegment3D seg = (GeoSegment3D) iter.next();
			GeoElement parent = seg.getGeoParent();
			if (parent!=null)
				if (!this.contains(parent))
					this.add(seg.getGeoParent());				
		}
		
		
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
	
	
	/** return the first label hit, if one
	 * @return the first label hit
	 */
	public GeoElement getLabelHit(){
		
		if (hitsLabels.isEmpty())
			return null;
		else{
			GeoElement labelGeo = ((Drawable3D) hitsLabels.first()).getGeoElement();
			
			return labelGeo;
			
		}
	}
	
	
	
	
	
}
