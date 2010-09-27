package geogebra.kernel;

import geogebra.kernel.arithmetic.Equation;
import geogebra.kernel.arithmetic.Inequality;
import geogebra.main.Application;
import geogebra.util.Unicode;

public class GeoLinearInequality extends GeoLine {
	
    public GeoLinearInequality(Construction c, Equation equ) {
		super(c);
		if (equ != null)
			setOperation(((Inequality)equ).op);
		
		// make sure dashed / solid according to op
		// already set (wrongly) in super() call
		setConstructionDefaults();
		update();
	}

    public GeoLinearInequality(GeoLinearInequality line) {
    	super((GeoLine)line);
    }
    
	public GeoLinearInequality(Construction cons, String label, double a,
			double b, double c, char op) {
		super(cons, label, a, b, c);
		setOperation(op);
		
		// make sure dashed / solid according to op
		// already set (wrongly) in super() call
		setConstructionDefaults();
		update();
	}

	public String getClassName() {
    	return "GeoLinearInequality";
    }
    
    protected String getTypeString() {
		return "LinearInequality";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_LINEAR_INEQUALITY;
    }
      
    public GeoElement copy() {
        return new GeoLinearInequality(this);        
    }    
    
	public boolean isFillable() {
		return true;
	}
	
	public void setOperation(char op) {
		this.op = op;
	}
	
	public char getOperation() {
		return op;
	}
	
	public boolean isStrict() {
		return op == '<' || op == '>';
	}
	
}
