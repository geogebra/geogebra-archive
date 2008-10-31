package geogebra3D.euclidian3D;


import geogebra3D.kernel3D.GeoElement3D;


import java.util.Comparator;
import java.util.TreeSet;

public class HitSetSet extends TreeSet {
	
	/**
	 * class for set of sets of hit objects
	 */
	private static final long serialVersionUID = 1L;




	
	

	public HitSetSet(){
		//super();
		super(new GeoComparator());
		
	}

	
	
	//Comparator for GeoElements
	static final public class GeoComparator implements Comparator{
		public int compare(Object arg1, Object arg2) {
			
			HitSet set1 = (HitSet) arg1;
			HitSet set2 = (HitSet) arg2;
			
			//check if one set is empty
			if (set1.isEmpty())
				return 1;
			if (set2.isEmpty())
				return -1;
			
			GeoElement3D geo1 = (GeoElement3D) set1.first();
			GeoElement3D geo2 = (GeoElement3D) set2.first();
			
						
			return geo1.zPickCompareTo(geo2,true);


		}
	}

}
