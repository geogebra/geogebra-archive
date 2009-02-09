package geogebra3D.euclidian3D;

import java.util.Iterator;
import java.util.TreeSet;

import geogebra.euclidian.Hits;

public class Hits3D extends Hits {

	TreeSet hitsHighlighted = new TreeSet(new Drawable3D.drawableComparator()); 
	TreeSet[] hitSet = new TreeSet[Drawable3D.DRAW_PICK_ORDER_MAX];
	TreeSet hitsOthers = new TreeSet(new Drawable3D.drawableComparator()); //others
	TreeSet hitSetSet = new TreeSet(new Drawable3D.setComparator()); //set of sets

	
	public Hits3D(){
		super();
		
		// initing hitSet
		for (int i=0;i<Drawable3D.DRAW_PICK_ORDER_MAX;i++)
			hitSet[i] = new TreeSet(new Drawable3D.drawableComparator());
	}
	
	
	/** dispatch all hits in different sets */
	public void dispatch(){
		
		for (int i=0;i<Drawable3D.DRAW_PICK_ORDER_MAX;i++)
			hitSet[i].clear();
		hitsOthers.clear();
		hitSetSet.clear();
		
		
		for (Iterator iter = this.iterator(); iter.hasNext();) {			
			Drawable3D d = (Drawable3D) iter.next();	
			if(d.getPickOrder()<Drawable3D.DRAW_PICK_ORDER_MAX)
				hitSet[d.getPickOrder()].add(d);
			else
				hitsOthers.add(d);			
		}
		
		
		for (int i=0;i<Drawable3D.DRAW_PICK_ORDER_MAX;i++)
			hitSetSet.add(hitSet[i]);
		//hitSets.add(hitsOthers);
		hitsHighlighted = (TreeSet) hitSetSet.first();
	}
	
	
	
	/** update highlights */
	public TreeSet getHitsHighlighted(){
		
		return hitsHighlighted;

	}
	
	
	
	
	
	
	
	
	public Hits getTopHits() {

		Hits ret = new Hits();
		ret.add(((Drawable3D) hitsHighlighted.first()).getGeoElement3D());
		return ret;
		
	}
	
	
	
	
	
}
