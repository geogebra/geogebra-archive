package geogebra.kernel;

/**
 * Algorithms for transformations 
 */
public abstract class AlgoTransformation extends AlgoElement {

	/**
	 * Create new transformation algo
	 * @param c
	 */
	public AlgoTransformation(Construction c) {
        super(c);  	
	}		
	
	/**
     * Returns the resulting GeoElement
     * @return the resulting GeoElement
     */
	abstract GeoElement getResult();
	
	abstract protected void setTransformedObject(GeoElement g,GeoElement g2);
	protected void transformList(GeoList ageo2, GeoList bgeo2) {    	
		for(int i = bgeo2.size()-1; i>=ageo2.size();i--)
			bgeo2.remove(i);
		
		for(int i = 0; i<ageo2.size();i++){
			GeoElement trans = null;
			if(i<bgeo2.size()){
				trans = bgeo2.get(i);
				setTransformedObject(ageo2.get(i),trans);
				compute();
				bgeo2.get(i).set(trans);
			}
			else{
				trans = getResultTemplate(ageo2.get(i));
				
				setTransformedObject(ageo2.get(i),trans);
				compute();
				bgeo2.add(trans);
			}
		}		
		setTransformedObject(ageo2,bgeo2);
	}
	protected GeoElement getResultTemplate(GeoElement geo) {		
		if(geo instanceof GeoPolyLineInterface || geo.isLimitedPath())
			return geo.copyInternal(cons);
		if(geo.isGeoList())        	
        	return new GeoList(cons);
		return geo.copy();		
	}

	protected void transformLimitedPath(GeoElement a,GeoElement b){
		
		if(a instanceof GeoRay){
			setTransformedObject(
					((GeoRay)a).getStartPoint(),
					((GeoRay)b).getStartPoint());
			compute();
			setTransformedObject(a,b);
		}
		else if(a instanceof GeoSegment){
			setTransformedObject(
					((GeoSegment)a).getStartPoint(),
					((GeoSegment)b).getStartPoint());
			compute();
			setTransformedObject(
					((GeoSegment)a).getEndPoint(),
					((GeoSegment)b).getEndPoint());
			compute();
			setTransformedObject(a,b);
		}
		if(a instanceof GeoConicPart){
			double p = ((GeoConicPart)a).getParameterStart();
			double q = ((GeoConicPart)a).getParameterEnd();
			((GeoConicPart)b).setParameters(transformConicParam(p), transformConicParam(q), isInConstructionList());
			//TODO transform for conic part
		}
	}
	protected double transformConicParam(double d){
		return d;
	}
}
