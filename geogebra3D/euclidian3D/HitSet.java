package geogebra3D.euclidian3D;

import geogebra.main.Application;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoPoint3D;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.TreeSet;

public class HitSet extends TreeSet {
	
	/**
	 * class for sets of hit objects
	 */
	
	
	private static final long serialVersionUID = -5697683292043175829L;
	

	public HitSet(){
		//super();
		super(new GeoComparator());
		
	}

	
	
	//Comparator for GeoElements
	static final public class GeoComparator implements Comparator{
		public int compare(Object arg1, Object arg2) {
			GeoElement3D geo1 = (GeoElement3D) arg1;
			GeoElement3D geo2 = (GeoElement3D) arg2;
			
						
			return geo1.zPickCompareTo(geo2,false);


		}
	}

}
