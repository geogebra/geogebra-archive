package geogebra.kernel;

import geogebra.util.MyPoint;

import java.util.LinkedList;



public class GeoLocus extends GeoElement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int MAX_PATH_RUNS = 10;
				
	private boolean defined;		
	
	// coords of points on locus
	private LinkedList myPointList;		
	
	public GeoLocus(Construction c) {
		super(c);				
		myPointList = new LinkedList();		
	}  
			
	public GeoElement copy() {
		GeoLocus ret =  new GeoLocus(cons);
		ret.setInternal(this);
		return ret; 
	}

	public void set(GeoElement geo) {
	//	GeoLocus gt = (GeoLocus) geo;		
	}
	
	public void setInternal(GeoElement geo) {
		GeoLocus locus = (GeoLocus) geo;	
		
		defined = locus.defined;		
		
		// coords of points on locus
		if (locus.cons == cons)
			myPointList = locus.myPointList;
		else {
			myPointList.clear();
			myPointList.addAll(locus.myPointList);
		}
	}
		
	/**
	 * Number of valid points in x and y arrays.
	 * @return
	 */
	final public int getPointLength() {
		return myPointList.size();
	}	
	
	public void clearPoints() {		
		myPointList.clear();				
	}
	
	/**
	 * Adds a new point (x,y) to the end of the point list of this locus.	 
	 * @param x
	 * @param y
	 * @param lineTo: true to draw a line to (x,y); false to only move to (x,y)
	 */
	public void insertPoint(double x, double y, boolean lineTo) { 
		myPointList.add(new MyPoint(x, y, lineTo));	
	}
	
	public LinkedList getMyPointList() {
		return myPointList;
	}
	
	public String toString() {		
		return getLabel();
	}	

	boolean showInAlgebraView() {
		return false;
	}

	String getClassName() {
		return "GeoLocus";
	}
	
    String getTypeString() {
		return "Locus";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_LOCUS;
    }
	
	public boolean showToolTipText() {
		return true;
	}
	
	/**
	* returns all class-specific xml tags for getXML
	*/
   	String getXMLtags() {   
   		//return super.getXMLtags();
	   	StringBuffer sb = new StringBuffer();
	   	sb.append(getXMLvisualTags());		
	   	sb.append(getLineStyleXML());
		return sb.toString();   
   	}

	public void setMode(int mode) {
	}

	public int getMode() {	
		return 0;
	}

	public boolean isDefined() {
		return defined;
	}
	
	public void setDefined(boolean flag) {
		defined = flag;
	}

	public void setUndefined() {
		defined = false;		
	}

	public String toValueString() {
		return toString();
	}

	boolean showInEuclidianView() {
		return isDefined();
	}	
	
	public boolean isGeoLocus() {
		return true;
	}
	
}
