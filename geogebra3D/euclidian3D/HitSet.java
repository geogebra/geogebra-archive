package geogebra3D.euclidian3D;

import geogebra.main.Application;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoPoint3D;

import java.util.Comparator;
import java.util.TreeSet;

public class HitSet extends TreeSet {
	
	/**
	 * 
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
			
			
			if (geo1.zPick<geo2.zPick)
				return -1;
			else if (geo1.zPick>geo2.zPick)
				return 1;
			else{
				//if geos are GeoPoint3D (necessary both), looks if one is on a Path
				if (geo1 instanceof GeoPoint3D){
					GeoPoint3D p1 = (GeoPoint3D) arg1;
					GeoPoint3D p2 = (GeoPoint3D) arg2;
					if ((p1.hasPath1D())&&(!p2.hasPath1D()))
						return 1;
					else if ((!p1.hasPath1D())&&(p2.hasPath1D()))
						return -1;
					//Application.debug("points");
				}
				return 0;
			}
			 
			
			
			
			//return geo1.zPick-geo2.zPick;
		}
	}

}
