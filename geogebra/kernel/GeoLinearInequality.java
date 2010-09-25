package geogebra.kernel;

import geogebra.kernel.arithmetic.Equation;
import geogebra.kernel.arithmetic.Inequality;
import geogebra.util.Unicode;

public class GeoLinearInequality extends GeoLine {
	
    public GeoLinearInequality(Construction c, Equation equ) {
		super(c);
		this.op = ((Inequality)equ).op;
		
	}

    public GeoLinearInequality(GeoLinearInequality line) {
    	super((GeoLine)line);
    }
	public GeoLinearInequality(Construction cons, String label, double a,
			double b, double c, char op) {
		super(cons, label, a, b, c);
		this.op = op;
	}

	public String getClassName() {
    	return "GeoLinearInequality";
    }
    
    public int getGeoClassType() {
    	return GEO_CLASS_LINEAR_INEQUALITY;
    }
      
    public GeoElement copy() {
        return new GeoLinearInequality(this);        
    }    
    
    

}
