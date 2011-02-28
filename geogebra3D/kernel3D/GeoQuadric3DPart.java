package geogebra3D.kernel3D;

import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;

/**
 * Class for part of a quadric (e.g. side of a limited cone, cylinder, ...)
 * @author mathieu
 *
 */
public class GeoQuadric3DPart extends GeoQuadric3D {
	
	/** min value for limites */
	private double min;
	/** max value for limites */
	private double max;

	/**
	 * constructor
	 * @param c
	 */
	public GeoQuadric3DPart(Construction c) {
		super(c);
	}

	public GeoQuadric3DPart(GeoQuadric3DPart quadric){
		super(quadric);		
	}

	public void set(GeoElement geo) {
		super.set(geo);
		GeoQuadric3DPart quadric = (GeoQuadric3DPart) geo;
		setLimits(quadric.min, quadric.max);
	}

	
	/**
	 * sets the min and max values for limits
	 * @param min
	 * @param max
	 */
	public void setLimits(double min, double max){
		this.min = min;
		this.max = max;
	}
	
	
	public double getMinParameter(int index) {

		if (index==1)
			return min;
		else
			return super.getMinParameter(index);
	}
	
	

	public double getMaxParameter(int index) {
		if (index==1)
			return max;
		else
			return super.getMaxParameter(index);
	}
	
	
	public void set(Coords origin, Coords direction, double r){
		switch(type){
		case QUADRIC_CYLINDER:
			setCylinder(origin, direction, r);
		}
	}
	
	
    public int getGeoClassType() {

        return GeoElement3D.GEO_CLASS_QUADRIC_PART;

    }

    protected StringBuilder buildValueString(){
    	return new StringBuilder("todo-GeoQuadric3DPart");
    }
    
    public GeoElement copy() {

        return new GeoQuadric3DPart(this);

    }
    
}
