/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.NumberValue;


/**
 *Function limited to interval [a, b]
 */
public class AlgoFunctionInterval extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input    
    private NumberValue a, b; // input
    private GeoElement ageo, bgeo;
    private GeoFunction g; // output g     
    
    private Function gfun; // current function of g 
    private ExpressionNode curExp; // current expression of f (needed to notice change of f)
        
    /** Creates new AlgoDependentFunction */
    public AlgoFunctionInterval(Construction cons, String label, GeoFunction f, 
                                                NumberValue a, NumberValue b) {
        super(cons);
        this.f = f;
        this.a = a;
        this.b = b;         
        ageo = a.toGeoElement();
        bgeo = b.toGeoElement();
            
        g = new GeoFunction(cons); // output
        setInputOutput(); // for AlgoElement    
        compute();
        g.setLabel(label);
    }
    
    String getClassName() {
        return "AlgoFunctionInterval";
    }   

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[3];
        input[0] = f;
        input[1] = ageo;
        input[2] = bgeo;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoFunction getFunction() {
        return g;
    }
    
    final void compute() {  
        if (!(f.isDefined() && ageo.isDefined() && bgeo.isDefined())) 
            g.setUndefined();
        
        // check if f has changed
        Function fun = f.getFunction();
        ExpressionNode exp = fun.getExpression();
        if (exp != curExp) { 
            // f has changed
            curExp = exp;
            gfun = new Function(fun, kernel);
            g.setFunction(gfun);
        }
                        
        double ad = a.getDouble();
        double bd = b.getDouble(); 
        boolean defined = gfun.setInterval(ad, bd);
        g.setDefined(defined);        
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if(!app.isReverseLanguage()){//FKH 20040906
        sb.append(app.getPlain("Function"));        
        sb.append(' ');
        sb.append(f.getLabel());
        sb.append(' ');
        }
        sb.append(app.getPlain("onInterval"));
        sb.append(" [");
        sb.append(ageo.getLabel());
        sb.append(", ");
        sb.append(bgeo.getLabel());
        sb.append("]");
         if(app.isReverseLanguage()){//FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("Function"));
            sb.append(' ');
            sb.append(f.getLabel());
            sb.append(' ');
        }
        return sb.toString();
    }

}
