package geogebra.kernel;

import java.util.Comparator;

public class NameDescriptionComparator implements Comparator {				    	
	public int compare(Object ob1, Object ob2) {
		GeoElement geo1 = (GeoElement) ob1;
		GeoElement geo2 = (GeoElement) ob2;
		return geo1.getNameDescription().compareTo(geo2.getNameDescription());			
	}				
}
