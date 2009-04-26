package geogebra3D.euclidian3D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import geogebra.euclidian.Hits;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;
import geogebra3D.kernel3D.GeoSegment3D;

public class Hits3D extends Hits {

	TreeSet hitsHighlighted = new TreeSet(new Drawable3D.drawableComparator()); 
	TreeSet[] hitSet = new TreeSet[Drawable3D.DRAW_PICK_ORDER_MAX];
	TreeSet hitsOthers = new TreeSet(new Drawable3D.drawableComparator()); //others
	TreeSet hitSetSet = new TreeSet(new Drawable3D.setComparator()); //set of sets
	
	Hits topHits = new Hits();

	
	public Hits3D(){
		super();
		
		// initing hitSet
		for (int i=0;i<Drawable3D.DRAW_PICK_ORDER_MAX;i++)
			hitSet[i] = new TreeSet(new Drawable3D.drawableComparator());
	}
	
	
	public void init(){
		super.init();
		for (int i=0;i<Drawable3D.DRAW_PICK_ORDER_MAX;i++)
			hitSet[i].clear();
		hitsOthers.clear();
		hitSetSet.clear();
		
		topHits.init();
		
		
	}
	
	
	/** insert a drawable in the hitSet, called by EuclidianRenderer3D */
	public void addDrawable3D(Drawable3D d){
		
		if(d.getPickOrder()<Drawable3D.DRAW_PICK_ORDER_MAX)
			hitSet[d.getPickOrder()].add(d);
		else
			hitsOthers.add(d);	
		
	}
	
	/** sort all hits in different sets */
	public void sort(){
				
		
		for (int i=0;i<Drawable3D.DRAW_PICK_ORDER_MAX;i++)
			hitSetSet.add(hitSet[i]);		

		hitsHighlighted = (TreeSet) hitSetSet.first();
		
		for (Iterator iter = hitsHighlighted.iterator(); iter.hasNext();) {
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
		String s = "hits:";
		for (Iterator iter = this.iterator(); iter.hasNext();) {
			GeoElement geo = (GeoElement) iter.next();
			s+="\n"+geo.getLabel();
		}
		Application.debug(s);
		*/
		
	}
	
	
	
	/** update highlights */
	public TreeSet getHitsHighlighted(){
		
		return hitsHighlighted;

	}
	
	
	
	
	
	
	
	
	public Hits getTopHits() {

		if (topHits.isEmpty())
			return this;
		else
			return topHits;
		
	}
	
	
	
	
	
}
