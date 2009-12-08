/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;



/**
 * Integral of a function (GeoFunction)
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntegral extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoFunction g; // output g = integral(f(x), x)      

    public AlgoIntegral(Construction cons, String label, GeoFunction f) {
        this(cons, f);
        g.setLabel(label);
    }
    
    AlgoIntegral(Construction cons, GeoFunction f) {
        super(cons);
        this.f = f;        
        g = (GeoFunction) f.copyInternal(cons);  // output
        setInputOutput(); // for AlgoElement        
        compute();
    }
    
    protected String getClassName() {
        return "AlgoIntegral";
    }   

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = f;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoFunction getIntegral() {
        return g;
    }
    
    protected final void compute() {  
        g.setIntegral(f);
    }

    final public String toString() {
        StringBuilder sb = new StringBuilder();
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("IntegralOfA",f.getLabel()));
        
        if (!f.isIndependent()) { // show the symbolic representation too
            sb.append(": ");
            sb.append(g.getLabel());
            sb.append("(x) = ");
            sb.append(g.toSymbolicString());
        }
        

        return sb.toString();
    }

}
