package geogebra.kernel;

import geogebra.kernel.arithmetic.Equation;
import geogebra.kernel.arithmetic.Inequality;
import geogebra.main.Application;
import geogebra.util.Unicode;

public class GeoLinearInequality extends GeoLine {
	
    public GeoLinearInequality(Construction c, Equation equ) {
		super(c);
		if (equ != null)
			this.op = ((Inequality)equ).op;
		update(); // make sure operation correct in AlgebraView
	}

    public GeoLinearInequality(GeoLinearInequality line) {
    	super((GeoLine)line);
    }
    
	public GeoLinearInequality(Construction cons, String label, double a,
			double b, double c, char op) {
		super(cons, label, a, b, c);
		this.op = op;
		update(); // make sure operation correct in AlgebraView
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
	
    /**
     * returns all class-specific xml tags for saveXML
     * GeoGebra File Format
     */
	protected void getXMLtags(StringBuilder sb) {
        super.getXMLtags(sb);

    	sb.append("\t<operation val=\"");
        sb.append(op);
        sb.append("\"/>\n");
        
	}
  

}
