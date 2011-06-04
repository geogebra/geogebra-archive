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
				trans = ageo2.get(i);
				if(trans instanceof GeoPolygon)
					trans = trans.copyInternal(cons);
				else 
					trans = trans.copy();
				setTransformedObject(ageo2.get(i),trans);
				compute();
				bgeo2.add(trans);
			}
		}		
		setTransformedObject(ageo2,bgeo2);
	}
	 
}
